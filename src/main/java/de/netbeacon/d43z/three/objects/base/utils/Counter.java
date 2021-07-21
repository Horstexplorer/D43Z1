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

package de.netbeacon.d43z.three.objects.base.utils;

import de.netbeacon.d43z.three.objects.bp.IJSONAble;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicLong;

public class Counter implements IJSONAble{

	private final AtomicLong counter = new AtomicLong(0);

	public Counter(){}

	public Counter(JSONObject jsonObject){
		counter.set(jsonObject.getLong("value"));
	}

	public void increment(){
		counter.incrementAndGet();
	}

	public long getCount(){
		return counter.get();
	}

	@Override
	public JSONObject asJSON(){
		return new JSONObject()
			.put("value", counter.get());
	}

}
