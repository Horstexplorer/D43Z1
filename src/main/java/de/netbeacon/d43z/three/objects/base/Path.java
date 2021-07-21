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

import de.netbeacon.d43z.three.objects.base.utils.Counter;
import de.netbeacon.d43z.three.objects.bp.IContent;
import de.netbeacon.d43z.three.objects.bp.IMRTree;
import de.netbeacon.d43z.three.objects.bp.INode;
import de.netbeacon.d43z.three.objects.bp.IPath;
import org.json.JSONObject;

import java.util.UUID;

public class Path<Base extends IContent<?>> implements IPath<Base>{

	private final IMRTree<Base> manager;
	private final UUID uuid;
	private final UUID endpoint;
	private final Counter counter;

	public Path(IMRTree<Base> manager, INode<Base> iNode){
		this.manager = manager;
		this.uuid = UUID.randomUUID();
		this.endpoint = iNode.getUUID();
		this.counter = new Counter();
	}

	public Path(IMRTree<Base> manager, JSONObject jsonObject){
		this.manager = manager;
		this.uuid = UUID.fromString(jsonObject.getString("uuid"));
		this.endpoint = UUID.fromString(jsonObject.getString("endpoint"));
		this.counter = new Counter(jsonObject.getJSONObject("counter"));
	}

	@Override
	public UUID getUUID(){
		return uuid;
	}

	@Override
	public JSONObject asJSON(){
		return new JSONObject()
			.put("uuid", uuid.toString())
			.put("endpoint", endpoint.toString())
			.put("counter", counter.asJSON());
	}

	@Override
	public IMRTree<Base> getManager(){
		return manager;
	}

	@Override
	public INode<Base> walk(){
		return null;
	}

	@Override
	public INode<Base> walkSilently(){
		return null;
	}

}
