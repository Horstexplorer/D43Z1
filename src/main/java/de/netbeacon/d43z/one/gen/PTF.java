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

package de.netbeacon.d43z.one.gen;

import de.netbeacon.d43z.one.objects.base.Content;
import de.netbeacon.d43z.one.objects.base.ContentContext;
import de.netbeacon.d43z.one.objects.base.ContextPool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PTF{

	public static void main(String... args){
		File f = new File("F:\\D31\\xenia_personality_booster_d43z1_ptf.txt");
		try(BufferedReader bufferedReader = new BufferedReader(new FileReader(f))){
			List<ContentContext> contentContexts = new ArrayList<>();
			String line;
			int i = 0;
			while((line = bufferedReader.readLine()) != null){
				String[] triggers = line.split(";\\s");
				List<String> options = new ArrayList<>();
				while((line = bufferedReader.readLine()) != null && !line.contains("-")){
					options.add(line);
				}
				for(String trigger : triggers){
					for(String option : options){
						Content t = new Content(trigger);
						t.setWeight(1.1F);
						Content o = new Content(option);
						contentContexts.add(new ContentContext("Xenia Personality Booster_" + i++, new HashSet<>(), List.of(t, o)));
					}
				}
			}
			ContextPool contextPool = new ContextPool("Xenia Personality Booster", contentContexts);
			File of = new File("F:\\D31\\OUT\\xeniapersonalitybooster\\" + contextPool.getUUID().toString() + ".cp.json");
			Files.write(of.toPath(), contextPool.asJSON().toString(1).getBytes());
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}
