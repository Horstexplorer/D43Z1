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

package de.netbeacon.d43z.three.objects.base;

import de.netbeacon.d43z.three.objects.bp.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Node<Base extends IContent<?>> implements INode<Base>{

	private final IMRTree<Base> manager;
	private final UUID uuid;
	private final Base content;
	private final List<UUID> paths = new ArrayList<>();


	public Node(IMRTree<Base> manager, Base content){
		this.manager = manager;
		this.content = content;
		this.uuid = UUID.randomUUID();
	}

	public Node(IMRTree<Base> manager, JSONObject jsonObject){
		this.manager = manager;
		this.uuid = UUID.fromString(jsonObject.getString("uuid"));
		this.content = (Base) IContent.getOf(jsonObject.getJSONObject("content"));
		JSONArray pathUUIDs = jsonObject.getJSONArray("pathUUIDs");
		for(int i = 0; i < pathUUIDs.length(); i++){
			paths.add(UUID.fromString(pathUUIDs.getString(i)));
		}
	}

	@Override
	public UUID getUUID(){
		return uuid;
	}

	@Override
	public JSONObject asJSON(){
		JSONArray pathUUIDs = new JSONArray();
		for(var path : paths){
			pathUUIDs.put(path.toString());
		}
		return new JSONObject()
			.put("uuid", uuid.toString())
			.put("content", content.asJSON())
			.put("pathUUIDs", pathUUIDs)
			;
	}

	@Override
	public IMRTree<Base> getManager(){
		return manager;
	}

	@Override
	public Base represent(){
		return content;
	}

	@Override
	public List<UUID> getPaths(){
		return paths;
	}

	@Override
	public void addPaths(IPath<Base>... paths){
		this.paths.addAll(Arrays.stream(paths).map(IIdentifiable::getUUID).collect(Collectors.toList()));
	}

	@Override
	public void removePaths(IPath<Base>... paths){
		this.paths.removeAll(Arrays.stream(paths).map(IIdentifiable::getUUID).collect(Collectors.toList()));
	}

	private final void preloadCache(){

	}

	@Override
	public Path<Base> getPathBy(PathMode pathMode){
		return null;
	}

}
