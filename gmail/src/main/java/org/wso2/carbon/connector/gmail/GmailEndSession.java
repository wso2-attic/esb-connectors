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
 * Class which terminates the authenticated IMAP and SMTP connections
 * with Gmail
 */
public class GmailEndSession extends AbstractConnector {

	/*
	 * Terminates the IMAP and SMTP sessions.
	 */
	@Override
	public void connect(MessageContext messageContext) throws ConnectException {
		try {
			org.apache.axis2.context.MessageContext axis2MessageContext =
			                                                              ((Axis2MessageContext) messageContext).getAxis2MessageContext();
			GmailUtils.closeConnection(axis2MessageContext);
			log.info("Successfully terminated the session");

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
