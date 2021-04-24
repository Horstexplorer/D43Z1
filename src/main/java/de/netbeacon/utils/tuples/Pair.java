/*
 *     Copyright 2020 Horstexplorer @ https://www.netbeacon.de
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

package de.netbeacon.utils.tuples;

public class Pair<V1, V2>{

	private final V1 value1;
	private final V2 value2;

	public Pair(V1 v1, V2 v2){
		this.value1 = v1;
		this.value2 = v2;
	}

	public V1 getValue1(){
		return value1;
	}

	public V2 getValue2(){
		return value2;
	}

}
