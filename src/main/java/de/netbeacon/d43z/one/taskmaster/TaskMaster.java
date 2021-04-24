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

package de.netbeacon.d43z.one.taskmaster;

import de.netbeacon.d43z.one.objects.base.Task;
import de.netbeacon.utils.tuples.Pair;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskMaster<I>{

	private final String desc;
	private List<Task<I, ?, ?>> taskList = new LinkedList<>();

	public TaskMaster(String desc){
		this.desc = desc;
	}

	public String getDesc(){
		return desc;
	}

	public synchronized void addTasks(Task<I, ?, ?>... tasks){
		taskList.addAll(Arrays.asList(tasks));
		taskList = taskList.stream().sorted(Comparator.comparingInt(Task::getPos)).collect(Collectors.toList());
	}

	public List<Task<I, ?, ?>> getTaskList(){
		return new LinkedList<>(taskList);
	}

	public Pair<Task<I, ?, ?>, Float> getTask(I input){
		Pair<Task<I, ?, ?>, Float> best = new Pair<>(null, Float.MIN_VALUE);
		for(Task<I, ?, ?> task : new LinkedList<>(taskList)){
			float res = task.testTrigger(input);
			if(res > best.getValue2()){
				best = new Pair<>(task, res);
			}
		}
		return best;
	}

	@Override
	public String toString(){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("TaskMaster").append(" : ").append(desc);
		for(Task<I, ?, ?> task : new LinkedList<>(taskList)){
			stringBuilder.append("\n\t").append(task.toString());
		}
		return stringBuilder.toString();
	}

}
