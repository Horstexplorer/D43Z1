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
import de.netbeacon.d43z.one.objects.bp.IContentprovider;
import de.netbeacon.d43z.one.objects.bp.ISimilarity;

public class MatchTrigger extends Trigger<IContentprovider, Float>{

	public MatchTrigger(String desc, ISimilarity.Algorithm algorithm, String match, float boolThreshold){
		super(desc, (input) -> new Content(input.getContent()).eval(algorithm, new Content(match)), (aFloat) -> aFloat > boolThreshold, (aFloat) -> aFloat);
	}

}
