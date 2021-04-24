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

package de.netbeacon.d43z.one.algo;

import de.netbeacon.d43z.one.objects.bp.IContentprovider;
import de.netbeacon.utils.tuples.Pair;
import de.netbeacon.utils.tuples.Triplet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Heavily simplified version of a pattern matcher utilizing LiamusJaccard for string matches
 */
public class LiamusPattern{

	private static final int JACCARD_ARRAY_64WORDS = 4;
	private static final java.util.regex.Pattern SPLIT = java.util.regex.Pattern.compile("\\s+");
	private final LiamusJaccard.BitArray64[] pattern;

	private LiamusPattern(LiamusJaccard.BitArray64[] pattern){
		this.pattern = pattern;
	}

	public static LiamusPattern compile(String pattern){
		String[] parts = SPLIT.split(pattern);
		LiamusJaccard.BitArray64[] array64 = new LiamusJaccard.BitArray64[parts.length];
		for(int i = 0; i < parts.length; i++){
			if(parts[i].codePointAt(0) == '\0'){
				array64[i] = null;
			}
			else{
				array64[i] = LiamusJaccard.hashString(parts[i], 1, JACCARD_ARRAY_64WORDS);
			}
		}
		return new LiamusPattern(array64);
	}

	public Pair<Boolean, Float> match(IContentprovider iContentprovider){
		return match(iContentprovider.getContent());
	}

	public Pair<Boolean, Float> match(String input){
		// s0
		List<LiamusJaccard.BitArray64> bitArray64s = Arrays.stream(SPLIT.split(input)).map(part -> LiamusJaccard.hashString(part, 1, JACCARD_ARRAY_64WORDS)).collect(Collectors.toList());
		// s1
		List<Float[]> stage1Results = new ArrayList<>();
		for(LiamusJaccard.BitArray64 bitArray64 : bitArray64s){
			Float[] results = new Float[pattern.length];
			for(int i = 0; i < pattern.length; i++){
				if(pattern[i] == null){
					results[i] = -1F;
				}
				else{
					results[i] = LiamusJaccard.similarityCoefficient(bitArray64, pattern[i]);
				}
			}
			stage1Results.add(results);
		}
		// s2
		List<Triplet<Integer, Integer, Float>> stage2Results = new ArrayList<>();
		for(int i = 0; i < stage1Results.size(); i++){
			Float[] floats = stage1Results.get(i);
			Triplet<Integer, Integer, Float> best = new Triplet<>(0, 0, -1F);
			for(int ii = 0; ii < floats.length; ii++){
				if(best.getValue3() >= floats[ii]){
					continue;
				}
				best = new Triplet<>(i, ii, floats[ii]);
			}
			stage2Results.add(best);
		}
		// s3
		boolean resultState = true;
		int last = 0;
		for(var triplet : stage2Results){
			if(triplet.getValue2() < last && triplet.getValue3() > 0.3F){
				resultState = false;
				break;
			}
			else if(triplet.getValue3() >= 0.3F){
				last = triplet.getValue2();
			}
		}
		// s4
		float value = stage2Results.stream().filter(t -> t.getValue3() > 0).map(Triplet::getValue3).reduce(0F, Float::sum) / stage2Results.stream().filter(t -> t.getValue3() > 0).count();
		// return
		return new Pair<>(resultState, value);
	}

}
