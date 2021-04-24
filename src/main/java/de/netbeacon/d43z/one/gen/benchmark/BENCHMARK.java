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

package de.netbeacon.d43z.one.gen.benchmark;

import de.netbeacon.d43z.one.eval.Eval;
import de.netbeacon.d43z.one.eval.io.EvalRequest;
import de.netbeacon.d43z.one.objects.base.Content;
import de.netbeacon.d43z.one.objects.base.ContentContext;
import de.netbeacon.d43z.one.objects.base.ContextPool;
import de.netbeacon.d43z.one.objects.bp.ISimilarity;
import de.netbeacon.d43z.one.objects.eval.ContentMatchBuffer;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static de.netbeacon.d43z.one.settings.StaticSettings.*;

public class BENCHMARK {

    static int rounds = 1500;
    static int linesTotal = 1_500_000; // lines total
    static int linesPerContext = 128;  // lines within each context
    static int lineLengthMin = 16;
    static int lineLengthMax = 2000;

    public static void main(String...args){

        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.println("Performance Benchmark");
        System.out.println();
        System.out.println("Settings:");

        System.out.println("Processing Threads Total: "+EVAL_MAX_PROCESSING_THREADS.get());
        System.out.println("Processing Threads Per Request: "+EVAL_MAX_THREADS_PER_REQUEST.get());
        System.out.println("Algorithm: "+EVAL_ALGORITHM.get());
        System.out.println("Eval Processing Time: "+EVAL_MAX_PROCESSING_TIME.get()+" downTo "+EVAL_MIN_PROCESSING_TIME.get());
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.println("Creating Test Data");
        long a = System.currentTimeMillis();
        List<Content> warmupData = new LinkedList<>();
        for(int i = 0; i < rounds/10; i++){
            String randomLine = RandomStringUtils.randomAlphanumeric(lineLengthMin, lineLengthMax);
            warmupData.add(new Content(randomLine));
        }
        List<Content> testData = new LinkedList<>();
        for(int i = 0; i < rounds; i++){
            String randomLine = RandomStringUtils.randomAlphanumeric(lineLengthMin, lineLengthMax);
            testData.add(new Content(randomLine));
        }
        List<ContentContext> contextBuffer = new LinkedList<>();
        List<Content> contentBuffer = new LinkedList<>();
        List<Content> contentList = new LinkedList<>();
        for(int i = 0; i < linesTotal; i++){
            String randomLine = RandomStringUtils.randomAlphanumeric(lineLengthMin, lineLengthMax);
            contentBuffer.add(new Content(randomLine));
            contentList.add(new Content(randomLine));
            if(contentBuffer.size()+1 >= linesPerContext || i == linesTotal-1){
                contextBuffer.add(new ContentContext("", new HashSet<>(), new LinkedList<>(contentBuffer)));
                contentBuffer.clear();
            }
        }
        ContextPool contextPool = new ContextPool("", contextBuffer);
        long b = System.currentTimeMillis();
        System.out.println("<>> Time: "+(b-a)+"ms total");
        // Tests
        System.out.println("----------------------------------------------------------------------------------------------------");
        EvalBenchmark(warmupData, testData, contextPool);
        System.out.println("----------------------------------------------------------------------------------------------------");
        AlgoEval(ISimilarity.Algorithm.LIAMUS_JACCARD, warmupData, testData, contentList);
        System.out.println("----------------------------------------------------------------------------------------------------");
        //AlgoEval(ISimilarity.Algorithm.SET_BASED_JACCARD, warmupData, testData, contentList);
        System.out.println("----------------------------------------------------------------------------------------------------");
    }

    public static void AlgoEval(ISimilarity.Algorithm algorithm, List<Content> warmupData, List<Content> testData, List<Content> poolData){
        System.out.println("> Running Algo Benchmark For "+algorithm);
        System.out.println("Running Warmup ("+(poolData.size()/1000)+" Rounds)");
        List<Float> ff = new LinkedList<>();
        Content w = warmupData.get(0);
        for(int i = 0; i < poolData.size()/1000; i++){
            w.eval(algorithm, poolData.get(i));
        }
        System.out.println("Running Benchmark ("+poolData.size()+" Rounds)");
        long a = System.nanoTime();
        Content t = testData.get(0);
        for (Content poolDatum : poolData) {
            ff.add(t.eval(algorithm, poolDatum));
        }
        long b = System.nanoTime();
        System.out.println("> Algo Benchmark Finished");
        System.out.println("<>> Time: "+(b-a)/1000000+"ms total | "+(b-a)/poolData.size()+"ns avg");
        System.out.println("");
    }

    public static void EvalBenchmark(List<Content> warmupData, List<Content> testData, ContextPool contextPool){
        try{
            System.out.println("> Running Eval Benchmark");
            Object syncO = new Object();

            Eval eval = new Eval();
            ContentMatchBuffer contentMatchBuffer = new ContentMatchBuffer();
            ExecutorService executor = Executors.newScheduledThreadPool(10);
            System.out.println("Running Warmup ("+warmupData.size()+" Rounds)");
            for(int i = 0; i < warmupData.size()-1; i++){
                EvalRequest evalRequest = new EvalRequest(contextPool, contentMatchBuffer, warmupData.get(i), (unused)->{}, executor);
                eval.enqueue(evalRequest);
            }
            EvalRequest evalRequest = new EvalRequest(contextPool, contentMatchBuffer, warmupData.get(warmupData.size()-1), (unused)->{
                System.out.println("Running Benchmark ("+testData.size()+" Rounds)");
                for(int i = 0; i < testData.size()-1; i++){
                    EvalRequest evalRequest2 = new EvalRequest(contextPool, contentMatchBuffer, testData.get(i), (unused2)->{}, executor);
                    eval.enqueue(evalRequest2);
                }
                EvalRequest evalRequest2 = new EvalRequest(contextPool, contentMatchBuffer, testData.get(testData.size()-1), (unused2)->{
                    synchronized (syncO){ syncO.notify(); }
                }, executor);
                eval.enqueue(evalRequest2);
            }, executor);
            eval.enqueue(evalRequest);
            long a = System.currentTimeMillis();
            synchronized (syncO){ syncO.wait(); }
            long b = System.currentTimeMillis();
            System.out.println("> Eval Benchmark Finished");
            System.out.println("<>> Time: "+(b-a)+"ms total | "+(b-a)/testData.size()+"ms avg");
            System.out.println("");
        }catch (Exception e){
            System.out.println("> Eval Benchmark Error");
        }
    }
}
