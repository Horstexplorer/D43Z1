/*
 *     Copyright 2021 Horstexplorer @ https://www.netbeacon.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.netbeacon.d43z.one.eval;

import de.netbeacon.d43z.one.eval.io.EvalRequest;
import de.netbeacon.d43z.one.eval.io.EvalResult;
import de.netbeacon.d43z.one.objects.base.Content;
import de.netbeacon.d43z.one.objects.base.ContentContext;
import de.netbeacon.d43z.one.objects.base.ContentShard;
import de.netbeacon.d43z.one.objects.bp.IContextPool;
import de.netbeacon.d43z.one.objects.bp.ISimilarity;
import de.netbeacon.d43z.one.objects.eval.ContentMatch;
import de.netbeacon.d43z.one.objects.eval.ContentMatchBuffer;
import de.netbeacon.utils.concurrent.SuspendableBlockingQueue;
import de.netbeacon.utils.shutdownhook.IShutdown;
import de.netbeacon.utils.shutdownhook.ShutdownException;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static de.netbeacon.d43z.one.settings.StaticSettings.*;

public class Eval implements IShutdown{

	private final ExecutorService REQUESTER_EXECUTOR = Executors.newSingleThreadExecutor();
	private final ThreadPoolExecutor REQUESTER_EXECUTOR_2 = (ThreadPoolExecutor) Executors.newFixedThreadPool(EVAL_MAX_CONCURRENT_TASKS.get());
	private final ExecutorService PROCESSING_EXECUTOR = Executors.newFixedThreadPool(EVAL_MAX_PROCESSING_THREADS.get());
	private final SuspendableBlockingQueue<EvalRequest> requestQueue = new SuspendableBlockingQueue<>();
	private final Object REQUESTER_NOTIFICATOR = new Object();

	private final AtomicLong queueTimeAVG = new AtomicLong(0);
	private final AtomicLong evalTimeAVG = new AtomicLong(0);

	public Eval(){
		REQUESTER_EXECUTOR.execute(this::REQUESTER_LOOP);
	}

	public void enqueue(EvalRequest evalRequest){
		requestQueue.put(evalRequest);
	}

	public void suspend(boolean state){
		requestQueue.suspend(state);
	}

	public int getQueueLength(){
		return requestQueue.size();
	}

	public long getEvalTimeAVGNs(){
		return evalTimeAVG.get();
	}

	public long getQueueTimeAVGMs(){
		return queueTimeAVG.get();
	}

	private void updateQueueTimeMs(long ms){
		synchronized(queueTimeAVG){
			queueTimeAVG.set(((queueTimeAVG.get() * EVAL_AVG_BASE.get()) + ms) / EVAL_AVG_BASE.get());
		}
	}

	private void updateEvalTimeNs(long ns){
		synchronized(evalTimeAVG){
			evalTimeAVG.set(((evalTimeAVG.get() * EVAL_AVG_BASE.get()) + ns) / EVAL_AVG_BASE.get());
		}
	}

	private void REQUESTER_LOOP(){
		try{
			while(true){
				try{
					if(REQUESTER_EXECUTOR_2.getActiveCount() == REQUESTER_EXECUTOR_2.getMaximumPoolSize()){
						synchronized(REQUESTER_NOTIFICATOR){
							REQUESTER_NOTIFICATOR.wait();
						}
					}
					EvalRequest evalRequest = requestQueue.get();
					long queueTime = System.currentTimeMillis() - evalRequest.getRequestTimestamp();
					updateQueueTimeMs(queueTime);
					final long maxEvalTime = Math.max(EVAL_MAX_PROCESSING_TIME.get() - queueTime, EVAL_MIN_PROCESSING_TIME.get());
					REQUESTER_EXECUTOR_2.execute(() -> {
						try{
							long startNanos = System.nanoTime();
							ContentMatch contentMatch = RUN_EVAL(EVAL_ALGORITHM.get(), evalRequest.getContextPool(), evalRequest.getContentMatchBuffer(), evalRequest.getContent(), maxEvalTime);
							updateEvalTimeNs(System.nanoTime() - startNanos);
							evalRequest.getCallbackExecutor().execute(() -> evalRequest.getCallback().accept(new EvalResult(contentMatch)));
						}
						catch(Exception e){
							evalRequest.getCallbackExecutor().execute(() -> evalRequest.getCallback().accept(new EvalResult(e)));
						}
						synchronized(REQUESTER_NOTIFICATOR){
							REQUESTER_NOTIFICATOR.notify();
						}
					});
				}
				catch(InterruptedException e){
					throw e;
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}

	private ContentMatch RUN_EVAL(ISimilarity.Algorithm algorithm, IContextPool contextPool, ContentMatchBuffer contentMatchBuffer, Content content, long maxDurationMs){
		long runEvalPreloadStart = System.currentTimeMillis();
		List<ContentContext> contentContexts = contextPool.getContentContexts();
		LinkedList<ContentShard> contentShards = new LinkedList<>();
		contentContexts.stream().map(ContentContext::getContentShards).forEach(contentShards::addAll);
		Collections.shuffle(contentShards);

		ConcurrentLinkedQueue<ContentShard> contentQueue = new ConcurrentLinkedQueue<>(contentShards);

		List<Future<ContentMatch>> processingFutures = new ArrayList<>();

		long runEvalPreloadEnd = System.currentTimeMillis();

		for(int i = 0; i < EVAL_MAX_THREADS_PER_REQUEST.get(); i++){
			processingFutures.add(PROCESSING_EXECUTOR.submit(() -> RUN_ANALYZE(contentQueue, contentMatchBuffer, algorithm, content, maxDurationMs - (runEvalPreloadStart - runEvalPreloadEnd))));
		}
		ContentMatch bestMatch = new ContentMatch(null, null, null, null, -1);
		for(Future<ContentMatch> processingFuture : processingFutures){
			ContentMatch contentMatch;
			try{
				contentMatch = processingFuture.get();
			}
			catch(ExecutionException | InterruptedException e){
				continue;
			}
			if(bestMatch.getAdjustedCoefficient() < contentMatch.getAdjustedCoefficient()){
				bestMatch = contentMatch;
			}
		}
		contentMatchBuffer.push(bestMatch);
		return bestMatch;
	}

	private static ContentMatch RUN_ANALYZE(ConcurrentLinkedQueue<ContentShard> contentShards, ContentMatchBuffer contentMatchBuffer, ISimilarity.Algorithm algorithm, Content content, long maxDurationMs){
		try{
			long startNanos = System.nanoTime();

			ContentMatch lastMatch = contentMatchBuffer.getLastMatch();
			Map<ContentContext, Float> matchEval = contentMatchBuffer.getLastMatchContextEval();
			Set<String> expectedMetaTags = contentMatchBuffer.expectedMetaTags();
			ContentMatch bestMatch = new ContentMatch(null, null, null, null, -1);
			ContentShard contentShard;

			ContentMatchBuffer.Statistics statistics = contentMatchBuffer.getStatistics();
			ContentMatchBuffer.Statistics.FillState fillState = statistics.getFillState();
			float avgOPM = statistics.getAvgOutputMatchCoefficient();

			while((System.nanoTime() - startNanos) < (maxDurationMs * 1000000) && (contentShard = contentShards.poll()) != null){
				int tagMatches = 0;
				if(EVAL_ENABLE_TAG_POLICY.get() && lastMatch != null && fillState.equals(ContentMatchBuffer.Statistics.FillState.FULL) && avgOPM > EVAL_TAG_POLICY_OVERRIDE_THRESHOLD.get()){
					Set<String> subC = new HashSet<>(contentShard.getParent().getMetaTags());
					subC.removeAll(expectedMetaTags);
					tagMatches = contentShard.getParent().getMetaTags().size() - subC.size();
					if(tagMatches == 0){
						continue;
					}
				}
				ContentMatch contentMatch = contentShard.getMatchFor(algorithm, content);
				// calculate adjustment
				float adjustment = 0;
				// buffer adjustment
				if(EVAL_ENABLE_BUFFER_BONUS_POLICY.get() && matchEval.containsKey(contentMatch.getOrigin().getParent())){
					adjustment += matchEval.get(contentMatch.getOrigin().getParent());
				}
				// tag adjustment
				if(EVAL_ENABLE_TAG_POLICY.get() && lastMatch != null && lastMatch.getAdjustedCoefficient() > EVAL_TAG_POLICY_OVERRIDE_THRESHOLD.get()){
					adjustment += tagMatches * EVAL_TAG_BONUS_PER_MATCH.get();
				}
				// set adjustment
				contentMatch.setCoefficientAdjustment(adjustment);
				// update
				if(bestMatch.getAdjustedCoefficient() < contentMatch.getAdjustedCoefficient()){
					bestMatch = contentMatch;
				}
			}
			return bestMatch;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void onShutdown() throws ShutdownException{
		REQUESTER_EXECUTOR.shutdown();
		REQUESTER_EXECUTOR_2.shutdown();
		PROCESSING_EXECUTOR.shutdown();
	}

}
