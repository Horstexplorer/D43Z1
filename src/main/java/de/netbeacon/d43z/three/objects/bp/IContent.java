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

import org.json.JSONObject;

import java.util.Base64;

public interface IContent<Base> extends IJSONAble{

	Base getRaw();

	byte[] getRawAsByte();

	@Override
	default JSONObject asJSON(){
		return new JSONObject()
			.put("class", getClass().getName())
			.put("bytes", Base64.getEncoder().encodeToString(getRawAsByte()));
	}

	static IContent<?> getOf(JSONObject jsonObject){
		return getOf(jsonObject.getString("class"), Base64.getDecoder().decode(jsonObject.getString("bytes")));
	}

	static IContent<?> getOf(String className, byte[] content){
		try{
			Class<?> aClass = Class.forName(className);
			return (IContent<?>) aClass.getConstructor(byte[].class).newInstance(content);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

}
