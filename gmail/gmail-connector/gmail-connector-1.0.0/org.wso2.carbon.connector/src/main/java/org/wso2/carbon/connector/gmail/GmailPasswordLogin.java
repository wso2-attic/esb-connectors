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
package org.wso2.carbon.connector.gmail;

import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

import com.google.code.javax.mail.MessagingException;

/**
 * Class which reads user name and password from the
 * message context to perform SASL authentication for Gmail.
 * 
 */
public class GmailPasswordLogin extends AbstractConnector {

	/*
	 * Extracts the values for user name and password and stores them in the
	 * message context.
	 */
	@Override
	public void connect(MessageContext messageContext) throws ConnectException {
		try {
			String username =
			                  GmailUtils.lookupFunctionParam(messageContext,
			                                                 GmailConstants.GMAIL_PARAM_USERNAME);
			String password =
			                  GmailUtils.lookupFunctionParam(messageContext,
			                                                 GmailConstants.GMAIL_PARAM_PASSWORD);
			if (username == null || "".equals(username.trim()) || password == null ||
			    "".equals(password.trim())) {

				String errorLog = "Invalid username or password";
				log.error(errorLog);
				ConnectException connectException = new ConnectException(errorLog);
				GmailUtils.storeErrorResponseStatus(messageContext,
				                                    connectException,
				                                    GmailErrorCodes.GMAIL_ERROR_CODE_CONNECT_EXCEPTION);
				handleException(connectException.getMessage(), connectException, messageContext);
			}

			// Storing user login details in the message context
			this.storeSASLUserLogin(username, password, messageContext);
		} catch (MessagingException e) {
			GmailUtils.storeErrorResponseStatus(messageContext,
			                                    e,
			                                    GmailErrorCodes.GMAIL_ERROR_CODE_MESSAGING_EXCEPTION);
			handleException(e.getMessage(), e, messageContext);
		} catch (Exception e) {
			GmailUtils.storeErrorResponseStatus(messageContext, e,
			                                    GmailErrorCodes.GMAIL_COMMON_EXCEPTION);
			handleException(e.getMessage(), e, messageContext);
		}
	}

	/**
	 * Stores user name and password information for SASL authentication
	 * 
	 * @param username
	 *            user name
	 * @param password
	 *            password
	 * @param messageContext
	 *            message context where the user login information should be
	 *            stored
	 * @throws MessagingException
	 *             if failures occur while authentication.
	 */
	private void storeSASLUserLogin(String username, String password, MessageContext messageContext)
	                                                                                                throws MessagingException {
		org.apache.axis2.context.MessageContext axis2MessageContext =
		                                                              ((Axis2MessageContext) messageContext).getAxis2MessageContext();
		Object loginMode = axis2MessageContext.getProperty(GmailConstants.GMAIL_LOGIN_MODE);
		if (loginMode != null &&
		    (loginMode.toString() == GmailConstants.GMAIL_SASL_LOGIN_MODE) &&
		    messageContext.getProperty(GmailConstants.GMAIL_USER_USERNAME).toString()
		                  .equals(username) &&
		    messageContext.getProperty(GmailConstants.GMAIL_USER_PASSWORD).toString()
		                  .equals(password)) {
			log.info("The same authentication is already available. Hence no changes are needed.");
			return;
		}

		// Closing already stored connections
		GmailUtils.closeConnection(axis2MessageContext);

		log.info("Setting the loggin mode to \"SASL\"");
		axis2MessageContext.setProperty(GmailConstants.GMAIL_LOGIN_MODE,
		                                GmailConstants.GMAIL_SASL_LOGIN_MODE);
		log.info("Storing new username and password");
		messageContext.setProperty(GmailConstants.GMAIL_USER_USERNAME, username);
		messageContext.setProperty(GmailConstants.GMAIL_USER_PASSWORD, password);
	}
}
