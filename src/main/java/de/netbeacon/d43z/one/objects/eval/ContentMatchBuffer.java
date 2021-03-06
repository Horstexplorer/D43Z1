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

package de.netbeacon.d43z.one.objects.eval;

import de.netbeacon.d43z.one.objects.base.ContentContext;
import de.netbeacon.d43z.one.objects.bp.IIdentifiable;

import java.util.*;

import static de.netbeacon.d43z.one.settings.StaticSettings.*;

public class ContentMatchBuffer implements IIdentifiable{

	private final UUID uuid = UUID.randomUUID();
	private final List<ContentMatch> lastMatches = new LinkedList<>();
	private final Statistics statistics = new Statistics(this);

	public ContentMatchBuffer(){}

	@Override
	public UUID getUUID(){
		return uuid;
	}

	public synchronized void push(ContentMatch contentMatch){
		lastMatches.add(contentMatch);
		if(lastMatches.size() > BUFFER_MAX_SIZE.get()){
			lastMatches.remove(0);
		}
	}

	public synchronized List<ContentMatch> getLastMatches(){
		return lastMatches;
	}

	public synchronized ContentMatch getLastMatch(){
		if(lastMatches.isEmpty()){
			return null;
		}
		return lastMatches.get(lastMatches.size() - 1);
	}

	public synchronized Set<String> expectedMetaTags(){
		Set<String> metaTags = new HashSet<>();
		lastMatches.stream().map(lastMatches -> lastMatches.origin.getParent().getMetaTags()).forEachOrdered(metaTags::addAll);
		return metaTags;
	}

	public synchronized Map<ContentContext, Float> getLastMatchContextEval(){
		Map<ContentContext, Float> map = new HashMap<>();
		for(int i = lastMatches.size() - 1; i >= 0; i--){
			ContentMatch contentMatch = lastMatches.get(i);
			ContentContext parentContent = contentMatch.getOrigin().getParent();
			if(!map.containsKey(parentContent)){
				map.put(parentContent, getPositionBonusFor(i));
			}
			else{
				map.put(parentContent, map.get(parentContent) + getPositionBonusFor(i));
			}
		}
		return map;
	}

	private float getPositionBonusFor(int pos){
		return BUFFER_BONUS.get() - BUFFER_BONUS_SUBTRACTION.get() * ((lastMatches.size() - 1) - pos);
	}

	protected int size(){
		return lastMatches.size();
	}

	public ContentMatchBuffer.Statistics getStatistics(){
		return statistics;
	}


	public static class Statistics{

		private final ContentMatchBuffer contentMatchBuffer;

		public Statistics(ContentMatchBuffer contentMatchBuffer){
			this.contentMatchBuffer = contentMatchBuffer;
		}

		public enum FillState{
			EMPTY,
			PARTIAL,
			FULL;
		}

		public float getRawFillState(){
			return contentMatchBuffer.size() / (float) BUFFER_MAX_SIZE.get();
		}

		public FillState getFillState(){
			if(contentMatchBuffer.size() == 0){
				return FillState.EMPTY;
			}
			else if(contentMatchBuffer.size() != BUFFER_MAX_SIZE.get()){
				return FillState.PARTIAL;
			}
			else{
				return FillState.FULL;
			}
		}

		public synchronized float getAvgOutputMatchCoefficient(){
			return new LinkedList<>(contentMatchBuffer.getLastMatches()).stream().map(ContentMatch::getAdjustedCoefficient).reduce(0F, Float::sum) / (float) contentMatchBuffer.size();
		}

		public enum MatchTendency{
			GOOD,
			NEUTRAL,
			POOR;
		}

		public float getRawMatchTendency(){
			if(contentMatchBuffer.size() == 0){
				return 0;
			}
			return contentMatchBuffer.getLastMatch().getAdjustedCoefficient() / getAvgOutputMatchCoefficient();
		}

		public MatchTendency getMatchTendency(){
			float f = getRawMatchTendency();
			if(f > 0.95F){
				return MatchTendency.GOOD;
			}
			else if(f > 0.9F){
				return MatchTendency.NEUTRAL;
			}
			else{
				return MatchTendency.POOR;
			}
		}

	}

}
