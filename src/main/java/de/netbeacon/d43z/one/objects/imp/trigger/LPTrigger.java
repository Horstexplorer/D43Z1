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

package de.netbeacon.d43z.one.objects.imp.trigger;

import de.netbeacon.d43z.one.algo.LiamusPattern;
import de.netbeacon.d43z.one.objects.base.Trigger;
import de.netbeacon.utils.tuples.Pair;

import java.util.function.Function;

public class LPTrigger extends Trigger<Pair<Boolean, Float>>{

	public LPTrigger(int pos, String desc, LiamusPattern liamusPattern, Function<Pair<Boolean, Float>, Boolean> toBool, Function<Pair<Boolean, Float>, Float> toFloat){
		super(pos, desc, Type.LIAMUSPATTERN, liamusPattern::match, toBool, toFloat);
	}

	public static Function<Pair<Boolean, Float>, Boolean> defaultToBool(){
		return Pair::getValue1;
	}

	public static Function<Pair<Boolean, Float>, Float> defaultToFloat(){
		return (pair) -> pair.getValue1() ? pair.getValue2() : -pair.getValue2();
	}

}
