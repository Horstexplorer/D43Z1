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

import de.netbeacon.d43z.one.objects.base.Trigger;
import de.netbeacon.d43z.one.objects.bp.IContentprovider;

import java.util.regex.Pattern;

public class RegexTrigger extends Trigger<IContentprovider, Boolean>{

	public RegexTrigger(String desc, Pattern pattern){
		super(desc, (input) -> pattern.matcher(input.getContent()).matches(), (bool) -> bool, (aBoolean) -> aBoolean ? 1F : 0F);
	}

}
