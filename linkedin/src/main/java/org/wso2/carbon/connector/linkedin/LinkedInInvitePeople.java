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
package org.wso2.carbon.connector.linkedin;

import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.linkedin.jsonadapters.MessageSerializer;
import org.wso2.carbon.connector.linkedin.model.MessageModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Class mediator which maps to <strong>/communication</strong> endpoint's
 * <strong>inivitation</strong> method
 * 
 * @see http://developer.linkedin.com/documents/invitation-api
 * 
 */
public class LinkedInInvitePeople extends AbstractConnector implements
		Connector {

	@Override
	public void connect(MessageContext messageContext) throws ConnectException {
		String recipients = (String)getParameter(messageContext, LinkedinUtils.StringConstants.RECIPIENTS);
		
		String[] recipientsArray = recipients.split(",");
		
		MessageModel messageModel = new MessageModel();
		messageModel.setValues(recipientsArray);
		
		Gson gson = new GsonBuilder()
						.setPrettyPrinting()
						.disableHtmlEscaping()
						.registerTypeAdapter(MessageModel.class, new MessageSerializer())
						.create();
		
		String messageJson = gson.toJson(messageModel);
		System.out.println(messageJson);
		
		messageContext.setProperty(LinkedinUtils.StringConstants.JSON_VALUES, messageJson);

	}

}
