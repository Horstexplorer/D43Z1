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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DAILYDIALOG{

	public static void main(String... args) throws IOException{
		File act = new File("F:\\D31\\EMNLP_dataset\\dialogues_act.txt");
		String[] acts = {"", "inform", "question", "directive", "commissive"};
		File emotion = new File("F:\\D31\\EMNLP_dataset\\dialogues_emotion.txt");
		String[] emotions = {"no emotion", "anger", "disgust", "fear", "happiness", "sadness", "surprise"};
		File text = new File("F:\\D31\\EMNLP_dataset\\dialogues_text.txt");
		File topic = new File("F:\\D31\\EMNLP_dataset\\dialogues_topic.txt");
		String[] topics = {"", "Ordinary Life", "School Life", "Culture & Education", "Attitude & Emotion", "Relationship", "Tourism", "Health", "Work", "Politics", "Finance"};

		BufferedReader actReader = new BufferedReader(new InputStreamReader(
			new FileInputStream(act), "UTF-8"));
		BufferedReader emotionReader = new BufferedReader(new InputStreamReader(
			new FileInputStream(emotion), "UTF-8"));
		BufferedReader textReader = new BufferedReader(new InputStreamReader(
			new FileInputStream(text), "UTF-8"));
		BufferedReader topicReader = new BufferedReader(new InputStreamReader(
			new FileInputStream(topic), "UTF-8"));

		String actLine = "";
		String emotionLine = "";
		String textLine = "";
		String topicLine = "";
		int i = 0;
		JSONArray dialogs = new JSONArray();
		List<ContentContext> contentContextList = new ArrayList<>();
		while((actLine = actReader.readLine()) != null){ // this is now our baseline file
			emotionLine = emotionReader.readLine();
			textLine = textReader.readLine();
			topicLine = topicReader.readLine();
			// parse into segments
			String[] actLineSplit = actLine.split("\\s");
			String[] emotionLineSplit = emotionLine.split("\\s");
			String[] textLineSplit = textLine.split("\\_\\_eou\\_\\_");
			// prepare JSON & Context
			List<Content> contents = new ArrayList<>();
			JSONArray chat = new JSONArray();
			JSONObject lineObject = new JSONObject()
				.put("dialogId", i++)
				.put("topic", topics[Integer.parseInt(topicLine)])
				.put("chat", chat);
			// create chat objects & add em
			for(int ii = 0; ii < actLineSplit.length; ii++){
				String textClean = textLineSplit[ii].trim();
				textClean = textClean.replace(" .", ".");
				textClean = textClean.replace(" \u2019 ", "\u0027");
				textClean = textClean.replace(" ,", ",");
				textClean = textClean.replace(" !", "!");
				textClean = textClean.replace(" ?", "?");
				chat.put(new JSONObject()
					.put("act", acts[Integer.parseInt(actLineSplit[ii])])
					.put("emotion", emotions[Integer.parseInt(emotionLineSplit[ii])])
					.put("text", textClean)
				);
				contents.add(new Content(textClean));
			}
			HashSet<String> c = new HashSet<>();
			c.add(topics[Integer.parseInt(topicLine)]);
			contentContextList.add(new ContentContext("DailyDialog_" + i, c, contents));
			dialogs.put(lineObject);
		}
		File stage1Out = new File("F:\\D31\\EMNLP_dataset\\CONVERTED.json");
		Files.writeString(stage1Out.toPath(), dialogs.toString(1));
		ContextPool contextPool = new ContextPool("DailyDialog", contentContextList);
		File stage2Out = new File("F:\\D31\\OUT\\" + contextPool.getUUID().toString() + ".cp.json");
		Files.writeString(stage2Out.toPath(), contextPool.asJSON().toString(1));
	}

}
