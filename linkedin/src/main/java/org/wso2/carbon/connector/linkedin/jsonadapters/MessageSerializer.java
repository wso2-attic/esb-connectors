/*
 * Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.connector.linkedin.jsonadapters;

import java.lang.reflect.Type;

import org.wso2.carbon.connector.linkedin.model.MessageModel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
/**
 * MessageSerializer for JSON
 * 
 *
 */
public class MessageSerializer implements JsonSerializer<MessageModel> {

	public JsonElement serialize(MessageModel messageModel, Type type,
			JsonSerializationContext context) {
		
		final JsonArray recipientsArray = new JsonArray();
		
		for ( int i = 0; i < messageModel.getValues().size(); i++ ) {
			JsonObject personObject = new JsonObject();
			JsonObject pathObject = new JsonObject();
			pathObject.addProperty("_path", messageModel.getValues().get(i).getPerson());
			personObject.add("person", pathObject);
			recipientsArray.add(personObject);
		}
		JsonObject recipientsObject = new JsonObject();
		recipientsObject.add("values", recipientsArray);
		return recipientsObject;
	}

}
