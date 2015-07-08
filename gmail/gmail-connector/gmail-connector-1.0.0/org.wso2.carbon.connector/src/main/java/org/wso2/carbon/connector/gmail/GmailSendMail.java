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

import java.io.IOException;

import org.apache.axiom.attachments.Attachments;
import org.apache.commons.lang.StringUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

import com.google.code.com.sun.mail.smtp.SMTPTransport;
import com.google.code.javax.mail.Message;
import com.google.code.javax.mail.MessagingException;
import com.google.code.javax.mail.Session;
import com.google.code.javax.mail.internet.InternetAddress;

/**
 * This class performs the "send mail" operation.
 */
public class GmailSendMail extends AbstractConnector {

	/*
	 * Sends an e-mail message to specified recipients.
	 */
	@Override
	public void connect(MessageContext messageContext) {
		try {
			// Reading input parameters from the message context
			String toRecipients =
			                      this.setRecipients(messageContext,
			                                         GmailConstants.GMAIL_PARAM_TO_RECIPIENTS);
			String ccRecipients =
			                      this.setRecipients(messageContext,
			                                         GmailConstants.GMAIL_PARAM_CC_RECIPIENTS);
			String bccRecipients =
			                       this.setRecipients(messageContext,
			                                          GmailConstants.GMAIL_PARAM_BCC_RECIPIENTS);

			// Validating recipients. At least one recipient should have been
			// given to send the mail
			if (toRecipients == null && bccRecipients == null && ccRecipients == null) {
				String errorLog = "No recipients are found";
				log.error(errorLog);
				ConnectException connectException = new ConnectException(errorLog);
				GmailUtils.storeErrorResponseStatus(messageContext,
				                                    connectException,
				                                    GmailErrorCodes.GMAIL_ERROR_CODE_CONNECT_EXCEPTION);
				handleException(connectException.getMessage(), connectException, messageContext);
			}

			String[] attachmentList = this.setAttachmentList(messageContext);
			String subject = this.setSubject(messageContext);
			String textContent = this.setTextContent(messageContext);

			GmailSMTPClientLoader smtpClientLoader = new GmailSMTPClientLoader();
			log.info("Loading the SMTP connection");
			GmailSMTPConnectionObject smtpConnectionObject =
			                                                 (GmailSMTPConnectionObject) smtpClientLoader.loadSMTPSession(messageContext);
			Session session = smtpConnectionObject.getSession();
			SMTPTransport transport = smtpConnectionObject.getTransport();
			org.apache.axis2.context.MessageContext axis2MsgCtx =
			                                                      ((Axis2MessageContext) messageContext).getAxis2MessageContext();
			Message message =
			                  GmailUtils.createNewMessage(session, subject, textContent,
			                                              toRecipients, ccRecipients,
			                                              bccRecipients, attachmentList,
			                                              axis2MsgCtx);
			GmailUtils.sendMessage(message, transport);
			GmailUtils.storeSentMailResponse(GmailConstants.GMAIL_SEND_MAIL_RESPONSE, subject,
			                                 textContent,
			                                 InternetAddress.toString(message.getAllRecipients())
			                                                .toString(),
			                                 StringUtils.join(attachmentList, ','), messageContext);
			log.info("Successfully completed the \"send mail\" operation");
		} catch (ConnectException e) {
			GmailUtils.storeErrorResponseStatus(messageContext, e,
			                                    GmailErrorCodes.GMAIL_ERROR_CODE_CONNECT_EXCEPTION);
			handleException(e.getMessage(), e, messageContext);
		} catch (MessagingException e) {
			GmailUtils.storeErrorResponseStatus(messageContext,
			                                    e,
			                                    GmailErrorCodes.GMAIL_ERROR_CODE_MESSAGING_EXCEPTION);
			handleException(e.getMessage(), e, messageContext);
		} catch (IOException e) {
			GmailUtils.storeErrorResponseStatus(messageContext, e,
			                                    GmailErrorCodes.GMAIL_ERROR_CODE_IO_EXCEPTION);
			handleException(e.getMessage(), e, messageContext);
		} catch (Exception e) {
			GmailUtils.storeErrorResponseStatus(messageContext, e,
			                                    GmailErrorCodes.GMAIL_COMMON_EXCEPTION);
			handleException(e.getMessage(), e, messageContext);
		}
	}

	/**
	 * Reads mail's subject parameter from the message context.
	 * 
	 * @param messageContext
	 *            from where the mail subject should be read
	 * @return the mail subject
	 */
	private String setSubject(MessageContext messageContext) {
		String subject =
		                 GmailUtils.lookupFunctionParam(messageContext,
		                                                GmailConstants.GMAIL_PARAM_SUBJECT);
		if (subject == null || "".equals(subject.trim())) {
			log.warn("Mail subject is not provided. Mail will be sent without a subject");
			subject = "(no suject)";
		}
		return subject;
	}

	/**
	 * Reads attachments' names from the message context.
	 * 
	 * @param messageContext
	 *            from where the attachment list should be read
	 * @return returns an array of file names
	 */
	private String[] setAttachmentList(MessageContext messageContext) {
		String attachmentIDs =
		                       GmailUtils.lookupFunctionParam(messageContext,
		                                                      GmailConstants.GMAIL_PARAM_ATTACHMENTIDS);
		if (attachmentIDs == null || "".equals(attachmentIDs.trim())) {
			org.apache.axis2.context.MessageContext axis2MsgCtx =
			                                                      ((Axis2MessageContext) messageContext).getAxis2MessageContext();
			Attachments attachments = axis2MsgCtx.getAttachmentMap();
			return attachments.getAllContentIDs();
		} else {
			return attachmentIDs.split(",");
		}
	}

	/**
	 * Reads mail's text content from the message context.
	 * 
	 * @param messageContext
	 *            from where the text content should be read
	 * @return mail's text content
	 */
	private String setTextContent(MessageContext messageContext) {
		String textContent =
		                     GmailUtils.lookupFunctionParam(messageContext,
		                                                    GmailConstants.GMAIL_PARAM_TEXT_CONTENT);
		if (textContent == null || "".equals(textContent.trim())) {
			log.warn("Mail text content is not provided. Mail will be sent without a text content");
			textContent = "";
		}
		return textContent;
	}

	/**
	 * Reads recipients parameter from message context
	 * 
	 * @param messageContext
	 *            from where the recipients should be read
	 * @param paramName
	 *            Name of the input parameter
	 * @return comma separated list of recipients' addresses
	 */
	private String setRecipients(MessageContext messageContext, String paramName) {
		String recipients = GmailUtils.lookupFunctionParam(messageContext, paramName);

		if (recipients == null || "".equals(recipients.trim())) {
			return null;
		}
		return recipients;
	}
}
