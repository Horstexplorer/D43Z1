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

package de.netbeacon.d43z.three.objects.bp;

import java.util.UUID;

public interface IMRTree<Base extends IContent<?>>{

	INode<Base> getNodeById(UUID uuid);

	default INode<Base> getNodeById(String uuid){
		return getNodeById(UUID.fromString(uuid));
	}

	IPath<Base> getPathById(UUID uuid);

	default IPath<Base> getPathById(String uuid){
		return getPathById(UUID.fromString(uuid));
	}

	interface Managed<Base extends IContent<?>> {

		IMRTree<Base> getManager();

	}
}
