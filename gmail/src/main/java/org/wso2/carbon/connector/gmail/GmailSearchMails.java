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
import com.google.code.javax.mail.search.GmailRawSearchTerm;
import com.google.code.javax.mail.search.SearchTerm;

/**
 * This class performs the "search mails" operation.
 * Resulted e-mail messages are grouped as batches of 50 messages and user is
 * given the option to get the any batch according to his needs
 */
public class GmailSearchMails extends AbstractConnector {

	/*
	 * Search and lists the resulted mail messages with their details. User can
	 * specify the search term and optionally the batch number. If the batch
	 * number is not specified, the batch of first 50 mails will be returned.
	 */
	@Override
	public void connect(MessageContext messageContext) {
		try {
			// Reading the search string and the batch number from the message context
			String searchString =
			                      GmailUtils.lookupFunctionParam(messageContext,
			                                                     GmailConstants.GMAIL_PARAM_SEARCH_TERM);
			String batchString =
			                     GmailUtils.lookupFunctionParam(messageContext,
			                                                    GmailConstants.GMAIL_PARAM_BATCH_NUMBER);

			// Validate the mandatory parameter, search string.
			if (searchString == null || "".equals(searchString.trim())) {
				String errorLog = "Invalid search term";
				log.error(errorLog);
				ConnectException connectException = new ConnectException(errorLog);
				GmailUtils.storeErrorResponseStatus(messageContext,
				                                    connectException,
				                                    GmailErrorCodes.GMAIL_ERROR_CODE_CONNECT_EXCEPTION);
				handleException(connectException.getMessage(), connectException, messageContext);
			}

			int batchNumber = GmailUtils.getBatchNumber(batchString);
			GmailIMAPClientLoader imapClientLoader = new GmailIMAPClientLoader();
			log.info("Loading the IMAPStore");
			IMAPStore store = imapClientLoader.loadIMAPStore(messageContext);
			SearchTerm searchTerm = new GmailRawSearchTerm(searchString);
			GmailUtils.listMails(messageContext, store, searchTerm, batchNumber,
			                     GmailConstants.GMAIL_SEARCH_MAILS_RESPONSE);
			log.info("Successfully completed the \"search mails\" operation");
		} catch (NumberFormatException e) {
			GmailUtils.storeErrorResponseStatus(messageContext,
			                                    e,
			                                    GmailErrorCodes.GMAIL_ERROR_CODE_NUMBER_FORMAT_EXCEPTION);
			handleException(e.getMessage(), e, messageContext);
		} catch (ConnectException e) {
			GmailUtils.storeErrorResponseStatus(messageContext, e,
			                                    GmailErrorCodes.GMAIL_ERROR_CODE_CONNECT_EXCEPTION);
			handleException(e.getMessage(), e, messageContext);
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
}
