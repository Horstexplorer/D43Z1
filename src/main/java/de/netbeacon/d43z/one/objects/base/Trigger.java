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

package de.netbeacon.d43z.one.objects.base;

import java.util.function.Function;

public abstract class Trigger<I, T>{

	private final String desc;
	private final Function<I, T> trigger;
	private final Function<T, Boolean> toBool;
	private final Function<T, Float> toFloat;

	public Trigger(String desc, Function<I, T> trigger, Function<T, Boolean> toBool, Function<T, Float> toFloat){
		this.desc = desc;
		this.trigger = trigger;
		this.toBool = toBool;
		this.toFloat = toFloat;
	}

	public String getDescription(){
		return desc;
	}

	public boolean testB(I input){
		return toBool.apply(trigger.apply(input));
	}

	public float testF(I input){
		return toFloat.apply(trigger.apply(input));
	}

	public String toString(){
		return "Trigger : " + desc;
	}

}
