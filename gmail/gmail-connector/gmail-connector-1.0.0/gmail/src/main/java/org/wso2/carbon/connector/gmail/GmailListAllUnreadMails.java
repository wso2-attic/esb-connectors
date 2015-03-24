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
import com.google.code.javax.mail.MessagingException;
import com.google.code.javax.mail.Flags.Flag;
import com.google.code.javax.mail.search.FlagTerm;
import com.google.code.javax.mail.search.SearchTerm;

/**
 * This class performs the "list all unread mails" operation which, lists all
 * the unread mails in the mail box with their details.
 */
public class GmailListAllUnreadMails extends AbstractConnector {

	/*
	 * Lists all unread mail messages in the mail box with their details. User
	 * can specify the batch number optionally.
	 */
	@Override
	public void connect(MessageContext messageContext) {
		try {
			// Reading the optional parameter, batch number, to read
			// from the message context
			String batchString =
			                     GmailUtils.lookupFunctionParam(messageContext,
			                                                    GmailConstants.GMAIL_PARAM_BATCH_NUMBER);
			int batchNumber = GmailUtils.getBatchNumber(batchString);
			GmailIMAPClientLoader imapClientLoader = new GmailIMAPClientLoader();
			log.info("Loading the IMAPStore");
			IMAPStore store = imapClientLoader.loadIMAPStore(messageContext);
			SearchTerm flagTerm = new FlagTerm(new Flags(Flag.SEEN), false);
			GmailUtils.listMails(messageContext, store, flagTerm, batchNumber,
			                     GmailConstants.GMAIL_LIST_ALL_UNREAD_MAILS_RESPONSE);
			log.info("Successfully completed the \"list all unread mails\" operation");
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
