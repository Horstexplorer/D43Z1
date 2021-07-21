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

import de.netbeacon.d43z.three.objects.base.cache.Cache;
import de.netbeacon.d43z.three.objects.bp.IContent;
import de.netbeacon.d43z.three.objects.bp.IMRTree;
import de.netbeacon.d43z.three.objects.bp.INode;
import de.netbeacon.d43z.three.objects.bp.IPath;

import java.util.UUID;

public class MRTree<Type extends IContent<?>> implements IMRTree<Type>{

	private final Cache<Node<Type>> nodeCache = new Cache<>();
	private final Cache<Path<Type>> pathCache = new Cache<>();

	public void addNodes(Node<Type>... nodes){
		nodeCache.addIdentifiable(nodes);
	}

	public void addPaths(Path<Type>... paths){
		pathCache.addIdentifiable(paths);
	}

	public void removeNodes(Node<Type>... nodes){
		nodeCache.removeIdentifiable(nodes);
	}

	public void removePaths(Path<Type>... paths){
		pathCache.removeIdentifiable(paths);
	}

	@Override
	public INode<Type> getNodeById(UUID uuid){
		return nodeCache.getByUUID(uuid);
	}

	@Override
	public IPath<Type> getPathById(UUID uuid){
		return pathCache.getByUUID(uuid);
	}

}
