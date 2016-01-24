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
import com.google.code.javax.mail.Flags;
import com.google.code.javax.mail.Flags.Flag;
import com.google.code.javax.mail.MessagingException;
import com.google.code.javax.mail.search.AndTerm;
import com.google.code.javax.mail.search.FlagTerm;
import com.google.code.javax.mail.search.GmailLabelTerm;
import com.google.code.javax.mail.search.SearchTerm;

/**
 * This class performs the "list all unread mails under label" operation which,
 * lists all the unread mails under a specified label.
 */
public class GmailListAllUnreadMailsUnderLabel extends AbstractConnector {

	/*
	 * Lists all unread mail messages under a specified label. User
	 * can specify the batch number optionally.
	 */
	@Override
	public void connect(MessageContext messageContext) throws ConnectException {
		try {
			// Reading input parameters, batch number and label name, from the
			// message context
			String label =
			               GmailUtils.lookupFunctionParam(messageContext,
			                                              GmailConstants.GMAIL_PARAM_LABEL);
			String batchString =
			                     GmailUtils.lookupFunctionParam(messageContext,
			                                                    GmailConstants.GMAIL_PARAM_BATCH_NUMBER);

			// Validating the mandatory parameter, label.
			if (label == null || "".equals(label.trim())) {
				String errorLog = "A valid label name is not provided";
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
			SearchTerm term = this.getSearchTerm(label);
			GmailUtils.listMails(messageContext, store, term, batchNumber,
			                     GmailConstants.GMAIL_LIST_ALL_UNREAD_MAILS_UNDER_LABEL_RESPONSE);
			log.info("Successfully completed the \"list all unread mails under label\" operation");
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

	private SearchTerm getSearchTerm(String label) throws MessagingException {
		SearchTerm labelTerm = new GmailLabelTerm(label);
		SearchTerm flagTerm = new FlagTerm(new Flags(Flag.SEEN), false);
		return new AndTerm(flagTerm, labelTerm);
	}
}
