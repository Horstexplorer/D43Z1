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

package de.netbeacon.d43z.three.objects.base.cache;

import de.netbeacon.d43z.three.objects.bp.ICache;
import de.netbeacon.d43z.three.objects.bp.IIdentifiable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Cache<Type extends IIdentifiable> implements ICache<Type>{

	private final ConcurrentHashMap<UUID, Type> uuidTypeConcurrentHashMap = new ConcurrentHashMap<>();

	@Override
	public synchronized void addIdentifiable(Type... objects){
		for(Type object : objects){
			uuidTypeConcurrentHashMap.put(object.getUUID(), object);
		}
	}

	@Override
	public synchronized void removeIdentifiable(UUID... objects){
		for(UUID object : objects){
			uuidTypeConcurrentHashMap.remove(object);
		}
	}

	@Override
	public synchronized void removeIdentifiable(Type... objects){
		for(Type object : objects){
			uuidTypeConcurrentHashMap.remove(object.getUUID());
		}
	}

	@Override
	public Type getByString(String uuid){
		return getByUUID(UUID.fromString(uuid));
	}

	@Override
	public Type getByUUID(UUID uuid){
		return uuidTypeConcurrentHashMap.get(uuid);
	}

}
