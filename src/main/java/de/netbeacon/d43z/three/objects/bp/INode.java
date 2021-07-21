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

import de.netbeacon.d43z.three.objects.base.Path;

import java.util.List;
import java.util.UUID;

public interface INode<Base extends IContent<?>> extends IIdentifiable, IJSONAble, IMRTree.Managed<Base>{


	Base represent();

	List<UUID> getPaths();

	void addPaths(IPath<Base>... paths);

	void removePaths(IPath<Base>... paths);

	enum PathMode {

	}

	Path<Base> getPathBy(PathMode pathMode);



}
