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
import org.apache.commons.collections4.ListUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ANIM {

    public static void main(String...args) throws IOException {
        File inDir = new File("F:\\D31\\animeinput.txt");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(inDir));
        String line;
        List<ContentContext> contentContexts = new ArrayList<>();
        // initial
        String name = bufferedReader.readLine();
        List<Content> contentList = new ArrayList<>();
        while((line = bufferedReader.readLine()) != null){
            if(!line.startsWith(">")){
                contentContexts.add(new ContentContext(name, new HashSet<>(), new ArrayList<>(contentList)));
                name = line;
                contentList.clear();
            }else{
                line = line.substring(1).replaceAll("\\s+", " ");
                contentList.add(new Content(line));
            }
        }
        for(var part : ListUtils.partition(contentContexts, contentContexts.size()/2)){
            ContextPool contextPool = new ContextPool("Anime Subtitles", part);
            File out = new File("F:\\D31\\OUT\\"+contextPool.getUUID().toString()+".json");
            Files.write(out.toPath(), contextPool.asJSON().toString(1).getBytes());
        }
    }

}
