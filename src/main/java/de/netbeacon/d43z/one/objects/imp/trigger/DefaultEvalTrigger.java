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

import de.netbeacon.d43z.one.objects.base.Content;
import de.netbeacon.d43z.one.objects.base.Trigger;

public class DefaultEvalTrigger extends Trigger<Content, Content>{

	private final float threshold;

	public DefaultEvalTrigger(float threshold){
		super("Default trigger with a threshold of " + threshold, null, null, null);
		this.threshold = threshold;
	}

	@Override
	public boolean testB(Content input){
		return true;
	}

	@Override
	public float testF(Content input){
		return threshold;
	}

}
