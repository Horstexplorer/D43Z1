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

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class Task<T, E, R>{

	private final int pos;
	private final String desc;
	private final List<Trigger<T, ?>> triggers;
	private final BiFunction<T, E, R> task;

	public Task(int pos, String desc, List<Trigger<T, ?>> triggers, BiFunction<T, E, R> task){
		this.pos = pos;
		this.desc = desc;
		this.triggers = triggers;
		this.task = task;
	}

	public int getPos(){
		return pos;
	}

	public String getDesc(){
		return desc;
	}

	public float testTrigger(T input){
		float max = Float.MIN_VALUE;
		for(Trigger<T, ?> trigger : triggers){
			float res = trigger.testF(input);
			if(max < res){
				max = res;
			}
		}
		return max;
	}

	public R execute(T input, Supplier<E> externalSupplier){
		return task.apply(input, externalSupplier.get());
	}

	@Override
	public String toString(){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(pos).append(" : ").append(desc);
		for(Trigger<T, ?> trigger : triggers){
			stringBuilder.append('\n').append(trigger.toString());
		}
		return stringBuilder.toString();
	}

}
