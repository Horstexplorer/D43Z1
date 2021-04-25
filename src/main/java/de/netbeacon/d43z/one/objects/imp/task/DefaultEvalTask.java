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

package de.netbeacon.d43z.one.objects.imp.task;

import de.netbeacon.d43z.one.eval.Eval;
import de.netbeacon.d43z.one.eval.io.EvalRequest;
import de.netbeacon.d43z.one.objects.base.Content;
import de.netbeacon.d43z.one.objects.base.Task;
import de.netbeacon.d43z.one.objects.imp.trigger.DefaultEvalTrigger;
import de.netbeacon.utils.tuples.Pair;

import java.util.List;

public class DefaultEvalTask extends Task<Content, Pair<Eval, EvalRequest>, Object>{

	public DefaultEvalTask(float threshold){
		super(Integer.MIN_VALUE, "Default eval task with a threshold of " + threshold, List.of(new DefaultEvalTrigger(threshold)), (content, quartet) -> {
			quartet.getValue1().enqueue(quartet.getValue2());
			return null;
		});
	}

}
