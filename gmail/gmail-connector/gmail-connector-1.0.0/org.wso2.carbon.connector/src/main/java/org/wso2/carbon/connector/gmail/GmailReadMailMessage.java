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
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

import com.google.code.com.sun.mail.imap.IMAPStore;
import com.google.code.javax.mail.MessagingException;
import com.google.code.javax.mail.search.GmailMessageIDTerm;
import com.google.code.javax.mail.search.SearchTerm;

/**
 * This class performs the "read mail message" operation.
 */
public class GmailReadMailMessage extends AbstractConnector {

	/*
	 * Reads the message ID from the message context and reads the belonging
	 * e-mail message.
	 */
	@Override
	public void connect(MessageContext messageContext) {
		try {
			// Reading message ID from the message context
			String messageID =
			                   GmailUtils.lookupFunctionParam(messageContext,
			                                                  GmailConstants.GMAIL_PARAM_MESSAGEID);

			// Validating the message ID
			if (messageID == null || "".equals(messageID.trim())) {
				String errorLog = "Invalid messageID";
				log.error(errorLog);
				ConnectException connectException = new ConnectException(errorLog);
				GmailUtils.storeErrorResponseStatus(messageContext,
				                                    connectException,
				                                    GmailErrorCodes.GMAIL_ERROR_CODE_CONNECT_EXCEPTION);
				handleException(connectException.getMessage(), connectException, messageContext);
			}

			GmailIMAPClientLoader imapClientLoader = new GmailIMAPClientLoader();
			log.info("Loading the IMAPStore");
			IMAPStore store = imapClientLoader.loadIMAPStore(messageContext);
			SearchTerm term = new GmailMessageIDTerm(messageID);
			GmailUtils.readMails(messageContext, store, term,
			                     GmailConstants.GMAIL_READ_MAIL_MESSAGE_RESPONSE);
			log.info("Successfully completed the \"read mail messsages\" operation");
		} catch (MessagingException e) {
			GmailUtils.storeErrorResponseStatus(messageContext,
			                                    e,
			                                    GmailErrorCodes.GMAIL_ERROR_CODE_MESSAGING_EXCEPTION);
			handleException(e.getMessage(), e, messageContext);
		} catch (ConnectException e) {
			GmailUtils.storeErrorResponseStatus(messageContext, e,
			                                    GmailErrorCodes.GMAIL_ERROR_CODE_CONNECT_EXCEPTION);
			handleException(e.getMessage(), e, messageContext);
		} catch (Exception e) {
			GmailUtils.storeErrorResponseStatus(messageContext, e,
			                                    GmailErrorCodes.GMAIL_COMMON_EXCEPTION);
			handleException(e.getMessage(), e, messageContext);
		}
	}
}
