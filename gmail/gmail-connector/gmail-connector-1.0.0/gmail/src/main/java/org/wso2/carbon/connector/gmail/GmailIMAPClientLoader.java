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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.wso2.carbon.connector.core.ConnectException;

import com.google.code.com.sun.mail.imap.IMAPStore;
import com.google.code.javax.mail.MessagingException;

/**
 * Class which loads the IMAP store according to the authentication mode.
 */
public class GmailIMAPClientLoader {

	/**
	 * Log instance.
	 */
	private static Log log = LogFactory.getLog(GmailIMAPClientLoader.class);

	/**
	 * Method which loads the IMAPStore instance according to the authentication
	 * mode.
	 * 
	 * @param messageContext
	 *            Message context where the instantiated IMAPStore instance is
	 *            stored.
	 * @return
	 *         the loaded IMAPStrore instance.
	 * @throws MessagingException
	 *             as a result of authentication failures
	 * @throws ConnectException
	 *             as a result of invalid configuration
	 */
	public IMAPStore loadIMAPStore(MessageContext messageContext) throws MessagingException,
	                                                             ConnectException {

		org.apache.axis2.context.MessageContext axis2MsgCtx =
		                                                      ((Axis2MessageContext) messageContext).getAxis2MessageContext();
		Object prestoredInstance =
		                           axis2MsgCtx.getOperationContext()
		                                      .getProperty(GmailConstants.GMAIL_IMAP_STORE_INSTANCE);

		// Use if there exists an already stored IMAPStore instance.
		if (prestoredInstance != null) {
			log.info("Retriving the prestored IMAPstore instance");
			return (IMAPStore) prestoredInstance;
		}

		// Login mode should have been defined during either "init" or
		// "passwordLogin" operations.
		Object loginMode = axis2MsgCtx.getProperty(GmailConstants.GMAIL_LOGIN_MODE);
		if (loginMode == null) {
			String errorLog = "Gmail configuration details were not initialized";
			log.error(errorLog);
			ConnectException connectException = new ConnectException(errorLog);
			throw (connectException);
		}

		IMAPStore store = null;

		// Perform SASL authentication if configured using the "Password Login"
		// operation.
		if (loginMode.toString().equals(GmailConstants.GMAIL_SASL_LOGIN_MODE)) {
			log.info("SASL authentication starts");
			try {
				store =
				        GmailSASLAuthenticator.connectToIMAP(messageContext.getProperty(GmailConstants.GMAIL_USER_USERNAME)
				                                                           .toString(),
				                                             messageContext.getProperty(GmailConstants.GMAIL_USER_PASSWORD)
				                                                           .toString());
			} catch (MessagingException e) {
				log.error("Failure in SASL authentication");
				throw (e);
			}
		}

		// Perform OAuth authentication if configured using the "init"
		// operation.
		else if (loginMode.toString().equals(GmailConstants.GMAIL_OAUTH_LOGIN_MODE)) {
			if (axis2MsgCtx.getProperty(GmailConstants.GMAIL_OAUTH2_PROVIDER) == null) {
				log.info("Initializing OAuth2 provider");
				GmailOAuth2SASLAuthenticator.initializeOAuth2Provider();
				axis2MsgCtx.getOperationContext().setProperty(GmailConstants.GMAIL_OAUTH2_PROVIDER,
				                                              "Initialized");
			}

			log.info("OAuth2 authentication starts");
			try {
				store =
				        GmailOAuth2SASLAuthenticator.connectToIMAP(messageContext.getProperty(GmailConstants.GMAIL_OAUTH_USERNAME)
				                                                                 .toString(),
				                                                   messageContext.getProperty(GmailConstants.GMAIL_OAUTH_ACCESS_TOKEN)
				                                                                 .toString());
			} catch (MessagingException e) {
				log.error("Failure in OAuth2 authentication.");
				throw (e);
			}

		} else {
			String errorLog = "Gmail configuration details were not initialized";
			log.error(errorLog);
			ConnectException connectException = new ConnectException(errorLog);
			throw (connectException);
		}

		// Stores the newly instantiated IMAPStore in the operation context.
		axis2MsgCtx.getOperationContext().setProperty(GmailConstants.GMAIL_IMAP_STORE_INSTANCE,
		                                              store);
		return store;
	}
}
