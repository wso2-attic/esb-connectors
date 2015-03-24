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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.context.OperationContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.util.ConnectorUtils;

import com.google.code.com.sun.mail.imap.IMAPFolder;
import com.google.code.com.sun.mail.imap.IMAPMessage;
import com.google.code.com.sun.mail.imap.IMAPStore;
import com.google.code.com.sun.mail.smtp.SMTPTransport;
import com.google.code.javax.activation.DataHandler;
import com.google.code.javax.mail.BodyPart;
import com.google.code.javax.mail.FetchProfile;
import com.google.code.javax.mail.Folder;
import com.google.code.javax.mail.Message;
import com.google.code.javax.mail.MessagingException;
import com.google.code.javax.mail.Session;
import com.google.code.javax.mail.Flags.Flag;
import com.google.code.javax.mail.Multipart;
import com.google.code.javax.mail.Part;
import com.google.code.javax.mail.internet.InternetAddress;
import com.google.code.javax.mail.internet.MimeBodyPart;
import com.google.code.javax.mail.internet.MimeMessage;
import com.google.code.javax.mail.internet.MimeMultipart;
import com.google.code.javax.mail.search.SearchTerm;
import com.google.code.javax.mail.util.ByteArrayDataSource;

/**
 * Utility class for the ESB connector for Gmail
 */
public final class GmailUtils {

	/**
	 * Making the default constructor private since Utility classes should not
	 * have a public constructors
	 */
	private GmailUtils() {
	}

	/**
	 * Log instance.
	 */
	private static Log log = LogFactory.getLog(GmailUtils.class);

	/**
	 * Extracts a given parameter from message context
	 * 
	 * @param messageContext
	 *            Input message context
	 * @param paramName
	 *            Name of the parameter to extract from the message context
	 * @return extracted parameter as a {@link String}
	 */
	public static String lookupFunctionParam(MessageContext messageContext, String paramName) {
		return (String) ConnectorUtils.lookupTemplateParamater(messageContext, paramName);
	}

	/**
	 * Stores error response in the message context
	 * 
	 * @param messageContext
	 *            Message Context where the error response should be stored
	 * @param e
	 *            Exception
	 */
	public static void storeErrorResponseStatus(MessageContext messageContext, final Throwable e,
	                                            int errorCode) {
		messageContext.setProperty(SynapseConstants.ERROR_EXCEPTION, e);
		messageContext.setProperty(SynapseConstants.ERROR_MESSAGE, e.getMessage());

		if (messageContext.getEnvelope().getBody().getFirstElement() != null) {
			messageContext.getEnvelope().getBody().getFirstElement().detach();
		}

		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMNamespace ns = factory.createOMNamespace("http://org.wso2.esbconnectors.gmail", "ns");
		OMElement result = factory.createOMElement("ErrorResponse", ns);
		OMElement errorMessageElement = factory.createOMElement("ErrorMessage", ns);
		result.addChild(errorMessageElement);
		errorMessageElement.setText(e.getMessage());
		messageContext.getEnvelope().getBody().addChild(result);

		messageContext.setProperty(SynapseConstants.ERROR_CODE, errorCode);
		messageContext.setFaultResponse(true);
		log.info("Stored the error response");
	}

	/**
	 * Read and list the e-mail messages searched from the IMAP store according
	 * to the given search term
	 * 
	 * @param messageContext
	 *            Message context where the response should be stored
	 * @param store
	 *            IMAPStore
	 * @param term
	 *            Search term
	 * @param batchNumber
	 *            The batch number to return
	 * @param responseElementName
	 *            Name of the response element name
	 * @throws MessagingException
	 * @throws ConnectException
	 *             if the folder does not exist
	 */
	public static void listMails(MessageContext messageContext, IMAPStore store, SearchTerm term,
	                             int batchNumber, String responseElementName)
	                                                                         throws MessagingException,
	                                                                         ConnectException {
		FetchProfile fetchprofile = getFetchProfile();
		Message[] messages = null;
		try {
			log.info("Started reading messages");
			IMAPFolder folder = getFolder(GmailConstants.GMAIL_ALL_MAIL, store);
			folder.open(Folder.READ_ONLY);
			if (term != null) {
				messages = GmailUtils.getBatch(folder.search(term), batchNumber);
			} else {
				messages = GmailUtils.getBatch(folder.getMessages(), batchNumber);
			}
			folder.fetch(messages, fetchprofile);
			log.info("Number of fetched messages:" + messages.length);
			storeMailListInResponse(messages, messageContext, responseElementName, false);
			folder.close(true);
		} catch (MessagingException e) {
			log.error("Failure while fetching messages");
			throw (e);
		}
	}

	/**
	 * Deletes the e-mail messages searched from the IMAP store according
	 * to the given search term
	 * 
	 * @param messageContext
	 *            Message context where the response should be stored
	 * @param store
	 *            IMAPStore
	 * @param term
	 *            Search term
	 * @param responseElementName
	 *            Name of the response element name
	 * @return an array of messages fetched from the IMAPStore
	 * @throws MessagingException
	 *             if any failure occur while deleting messages
	 * @throws ConnectException
	 *             if no messages are fetched to delete
	 */
	public static void deleteMails(IMAPStore store, SearchTerm term, MessageContext messageContext,
	                               String responseElementName) throws MessagingException,
	                                                          ConnectException {
		FetchProfile fetchprofile = getFetchProfile();
		Message[] messages = null;
		try {
			log.info("Reading messages");
			IMAPFolder folder = getFolder(GmailConstants.GMAIL_ALL_MAIL, store);
			IMAPFolder trash = getFolder(GmailConstants.GMAIL_TRASH, store);
			folder.open(Folder.READ_WRITE);
			messages = folder.search(term);
			if (messages.length == 0) {
				String errorLog =
				                  "No messages are found to delete. Please make sure the threda ID/ message ID is correct.";
				log.error(errorLog);
				ConnectException connectException = new ConnectException(errorLog);
				throw (connectException);
			}

			log.info("Fetching messages");
			folder.fetch(messages, fetchprofile);
			log.info("Number of fetched messages:" + messages.length);
			storeMailListInResponse(messages, messageContext, responseElementName, true);
			folder.copyMessages(messages, trash);
			folder.close(true);
		} catch (MessagingException e) {
			log.error("Error while deleting messages");
			throw (e);
		}
	}

	/**
	 * Read and list the e-mail messages searched from the IMAP store according
	 * to the given search term
	 * 
	 * @param messageContext
	 *            Message context where the response should be stored
	 * @param store
	 *            IMAPStore
	 * @param term
	 *            Search term
	 * @param responseElementName
	 *            Name of the response element name
	 * @throws MessagingException
	 *             if any failure occur while reading messages
	 * @throws ConnectException
	 *             if no messages are fetched to read
	 */
	public static void readMails(MessageContext messageContext, IMAPStore store, SearchTerm term,
	                             String responseElementName) throws MessagingException,
	                                                        ConnectException {
		FetchProfile fetchprofile = getFetchProfile();
		Message[] messages = null;
		try {
			log.info("Started reading messages");
			IMAPFolder folder = getFolder(GmailConstants.GMAIL_ALL_MAIL, store);
			folder.open(Folder.READ_WRITE);
			messages = folder.search(term);
			if (messages.length == 0) {
				String errorLog =
				                  "No messages are found to read. Please make sure the threda ID/ message ID is correct.";
				log.error(errorLog);
				ConnectException connectException = new ConnectException(errorLog);
				throw (connectException);
			}
			log.info("Fetching messages");
			folder.fetch(messages, fetchprofile);
			log.info("Number of fetched messages:" + messages.length);
			storeMailListInResponse(messages, messageContext, responseElementName, true);
			folder.close(true);
		} catch (MessagingException e) {
			log.error("Failure while fetching messages");
			throw (e);
		}
	}

	/**
	 * Set labels to the e-mail messages which are searched from the IMAP store
	 * according to the given search term
	 * 
	 * @param messageContext
	 *            Message context where the response should be stored
	 * @param store
	 *            IMAPStore
	 * @param term
	 *            Search term
	 * @param responseElementName
	 *            Name of the response element
	 * @param labels
	 *            comma separated list of label names
	 * @throws MessagingException
	 *             if any failure occur while setting labels
	 * @throws ConnectException
	 *             if no messages are fetched to set labels
	 */
	public static void setLabels(IMAPStore store, SearchTerm term, MessageContext messageContext,
	                             String[] labels, String responseElementName)
	                                                                         throws MessagingException,
	                                                                         ConnectException {
		FetchProfile fetchprofile = getFetchProfile();
		IMAPMessage[] messages = null;
		try {
			IMAPFolder folder = getFolder(GmailConstants.GMAIL_ALL_MAIL, store);
			log.info("Reading messages");
			folder.open(Folder.READ_WRITE);
			messages = (IMAPMessage[]) folder.search(term);
			if (messages.length == 0) {
				String errorLog =
				                  "No messages are found to set labels. Please make sure the threda ID/ message ID is correct.";
				log.error(errorLog);
				ConnectException connectException = new ConnectException(errorLog);
				throw (connectException);
			}
			log.info("Fetching messages");
			folder.fetch(messages, fetchprofile);
			log.info("Number of fetched messages:" + messages.length);
			folder.setGoogleMessageLabels(messages, labels, true);
			GmailUtils.storeMailListInResponse(messages, messageContext, responseElementName, false);
			folder.close(true);
		} catch (MessagingException e) {
			log.error("Error while deleting messages");
			throw (e);
		}
	}

	/**
	 * {@link FetchProfile} is created to fetch Gmail thread ID, Gmail message
	 * ID, Gmail labels, flags and envelop of e-mail messages
	 * 
	 * @return the {@link FetchProfile}
	 */
	public static FetchProfile getFetchProfile() {
		FetchProfile fetchprofile = new FetchProfile();
		fetchprofile.add(IMAPFolder.FetchProfileItem.X_GM_THRID);
		fetchprofile.add(IMAPFolder.FetchProfileItem.X_GM_MSGID);
		fetchprofile.add(IMAPFolder.FetchProfileItem.X_GM_LABELS);
		fetchprofile.add(IMAPFolder.FetchProfileItem.ENVELOPE);
		fetchprofile.add(IMAPFolder.FetchProfileItem.FLAGS);
		return fetchprofile;
	}

	/**
	 * Store resulted e-mail messages in the response.
	 * 
	 * @param messagesArray
	 *            Array of {@link Message}
	 * @param messageContext
	 *            Message Context where the messages should be stored
	 * @param resultElementName
	 *            Name of the result element
	 * @param storeContent
	 *            Message context is stored in the response only if this flag is
	 *            true
	 */
	public static void storeMailListInResponse(Message[] messagesArray,
	                                           MessageContext messageContext,
	                                           String resultElementName, boolean storeContent) {
		log.info("Storing the response in the message context");
		if (messageContext.getEnvelope().getBody().getFirstElement() != null) {
			messageContext.getEnvelope().getBody().getFirstElement().detach();
		}

		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMNamespace ns = factory.createOMNamespace("http://org.wso2.esbconnectors.gmail", "ns");
		OMElement result = factory.createOMElement(resultElementName, ns);

		OMElement messages = factory.createOMElement("messages", ns);
		result.addChild(messages);

		for (int i = messagesArray.length - 1; i >= 0; i--) {
			Message message = messagesArray[i];
			if (message != null) {
				OMElement messageElement = factory.createOMElement("message", ns);
				messages.addChild(messageElement);

				OMElement subject = factory.createOMElement("subject", ns);
				messageElement.addChild(subject);
				try {
					subject.setText(message.getSubject());
				} catch (MessagingException me) {
					log.info("\"Subject\" cannot be resolved");
				}

				OMElement from = factory.createOMElement("from", ns);
				messageElement.addChild(from);
				try {
					from.setText(InternetAddress.toString(message.getFrom()));
				} catch (MessagingException e) {
					log.info("\"From\" cannot be resolved");
				}

				OMElement to = factory.createOMElement("to", ns);
				messageElement.addChild(to);
				try {
					to.setText(InternetAddress.toString(message.getAllRecipients()));
				} catch (MessagingException e) {
					log.info("\"To\" cannot be resolved");
				}

				OMElement date = factory.createOMElement("sentDate", ns);
				messageElement.addChild(date);
				try {
					date.setText(message.getSentDate().toString());
				} catch (MessagingException e) {
					log.info("\"Sent date\" cannot be resolved");
				}

				OMElement labelsElement = factory.createOMElement("labels", ns);
				messageElement.addChild(labelsElement);
				String[] labels = ((IMAPMessage) message).getGoogleMessageLabels();
				for (String label : labels) {
					OMElement labelElement = factory.createOMElement("label", ns);
					labelsElement.addChild(labelElement);
					labelElement.setText(label);
				}

				OMElement msgId = factory.createOMElement("messageID", ns);
				messageElement.addChild(msgId);
				String messageID = Long.toString(((IMAPMessage) message).getGoogleMessageId());
				msgId.setText(messageID);

				OMElement threadId = factory.createOMElement("threadID", ns);
				messageElement.addChild(threadId);
				threadId.setText(Long.toString(((IMAPMessage) message).getGoogleMessageThreadId()));

				OMElement status = factory.createOMElement("Status", ns);
				messageElement.addChild(status);
				try {
					if (message.isSet(Flag.SEEN)) {
						status.setText("READ");
					} else {
						status.setText("UNREAD");
					}
				} catch (MessagingException e) {
					log.info("\"Message Status\" cannot be resolved");
				}

				if (storeContent) {
					OMElement content = factory.createOMElement("content", ns);
					messageElement.addChild(content);
					StringBuilder attachmentContentIDs = new StringBuilder();
					try {

						log.info("Processing message content");
						content.setText("\n" +
						                GmailUtils.processMessageBody(message, messageContext,
						                                              attachmentContentIDs,
						                                              messageID));
					} catch (Exception e) {
						log.info("Cannot retrive \"Message Content\".");
					}

					if (attachmentContentIDs.length() > 0) {
						OMElement attachments = factory.createOMElement("attachemnts", ns);
						messageElement.addChild(attachments);
						attachmentContentIDs.setLength(attachmentContentIDs.length() - 1);
						attachments.setText(attachmentContentIDs.toString());
					}
				}
			}
		}
		messageContext.getEnvelope().getBody().addChild(result);
	}

	/**
	 * Process message body.
	 * 
	 * @param message
	 *            Message to be processed.
	 * @param messageContext
	 *            Message Context
	 * @param attachmentContentIDs
	 *            file names as the content IDs of the attachments
	 * @param messageID
	 *            ID of the message.
	 * @return
	 * @throws IOException
	 * @throws MessagingException
	 */
	private static String processMessageBody(Message message, MessageContext messageContext,
	                                         StringBuilder attachmentContentIDs, String messageID)
	                                                                                              throws IOException,
	                                                                                              MessagingException {
		Object content = message.getContent();
		if (content instanceof Multipart) {
			Multipart multiPart = (Multipart) content;
			StringBuilder builder = new StringBuilder();
			return procesMultiPart(builder, multiPart, messageContext, attachmentContentIDs,
			                       messageID);
		} else if (content instanceof String) {
			return content.toString();
		} else if (content instanceof InputStream) {
			InputStream inStream = (InputStream) content;
			BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
			String line;
			StringBuilder builder = new StringBuilder();
			while ((line = br.readLine()) != null) {
				builder.append(line);
			}
			br.close();
			inStream.close();
			return builder.toString();
		}
		log.error("invalid message content");
		return null;
	}

	/**
	 * Reads the batch number from the input string.
	 * 
	 * @param batchString
	 *            input string
	 * @return batch number
	 * @throws NumberFormatException
	 *             if the batch number is not an integer
	 * @throws ConnectException
	 *             if the batch number is not a positive integer
	 */
	public static int getBatchNumber(String batchString) throws NumberFormatException,
	                                                    ConnectException {
		int batchNumber;
		if (batchString != null && !"".equals(batchString.trim())) {
			batchNumber = Integer.parseInt(batchString);
			if (batchNumber <= 0) {
				String errorLog = "Batch number should be a positive integer";
				log.error(errorLog);
				ConnectException connectException = new ConnectException(errorLog);
				throw (connectException);
			}
		} else {
			// Use first batch as the default batch.
			batchNumber = 1;
		}

		return batchNumber;
	}

	/**
	 * Close and remove the already stored IMAP and SMTP connections
	 * 
	 * @param operationContext
	 *            where the connections are stored
	 * @throws MessagingException
	 */
	public static void closeConnection(org.apache.axis2.context.MessageContext axis2MessageContext)
	                                                                                               throws MessagingException {
		if (axis2MessageContext.getProperty(GmailConstants.GMAIL_LOGIN_MODE) == null) {
			return;
		}

		OperationContext operationContext = axis2MessageContext.getOperationContext();
		if (operationContext.getProperty(GmailConstants.GMAIL_IMAP_STORE_INSTANCE) != null) {
			log.info("Closing the previously opened IMAP Store");
			((IMAPStore) operationContext.getProperty(GmailConstants.GMAIL_IMAP_STORE_INSTANCE)).close();
			operationContext.removeProperty(GmailConstants.GMAIL_IMAP_STORE_INSTANCE);
		}

		if (operationContext.getProperty(GmailConstants.GMAIL_SMTP_CONNECTION_INSTANCE) != null) {
			log.info("Closing the previously opened SMTP transport");
			((GmailSMTPConnectionObject) operationContext.getProperty(GmailConstants.GMAIL_SMTP_CONNECTION_INSTANCE)).getTransport()
			                                                                                                         .close();
			operationContext.removeProperty(GmailConstants.GMAIL_SMTP_CONNECTION_INSTANCE);
		}

		axis2MessageContext.removeProperty((String) axis2MessageContext.getProperty(GmailConstants.GMAIL_LOGIN_MODE));
	}

	/**
	 * Store the response for send mail operations.
	 * 
	 * @param responseElementName
	 *            Response element's name
	 * @param subject
	 *            Subject of the mail
	 * @param textContent
	 *            Text content of the mail
	 * @param recipients
	 *            A comma separated list of recipients
	 * @param attachmentIDs
	 *            A comma separated list of attachmentIDs
	 * @param messageContext
	 *            Message context where the response should be stored
	 */
	public static void storeSentMailResponse(String responseElementName, String subject,
	                                         String textContent, String recipients,
	                                         String attachmentIDs, MessageContext messageContext) {
		log.info("Storing the response in the message context");
		if (messageContext.getEnvelope().getBody().getFirstElement() != null) {
			messageContext.getEnvelope().getBody().getFirstElement().detach();
		}
		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMNamespace ns = factory.createOMNamespace("http://org.wso2.esbconnectors.gmail", "ns");
		OMElement result = factory.createOMElement(responseElementName, ns);

		OMElement messageElement = factory.createOMElement("message", ns);
		result.addChild(messageElement);
		OMElement subjectElement = factory.createOMElement("subject", ns);
		subjectElement.setText(subject);
		messageElement.addChild(subjectElement);
		OMElement contentElement = factory.createOMElement("content", ns);
		contentElement.setText(textContent);
		messageElement.addChild(contentElement);
		OMElement attachmentElement = factory.createOMElement("attachments", ns);
		attachmentElement.setText(attachmentIDs);
		messageElement.addChild(attachmentElement);
		OMElement recipientsElement = factory.createOMElement("recipients", ns);
		recipientsElement.setText(recipients);
		messageElement.addChild(recipientsElement);
		messageContext.getEnvelope().getBody().addChild(result);
	}

	/**
	 * Creates a new {@link Message}.
	 * 
	 * @param session
	 *            Mail {@link Session}.
	 * @param subject
	 *            Subject of the mail.
	 * @param textContent
	 *            Text content of the mail message.
	 * @param toRecipients
	 *            'To' recipients of the mail message.
	 * @param ccRecipients
	 *            'CC' recipients of the mail message.
	 * @param bccRecipients
	 *            'BCC' recipients of the mail message.
	 * @param attachmentList
	 *            Array of attachment file names.
	 * @param axis2MsgCtx
	 *            Axis2 message context where the attachment files are stored.
	 * @return returns the created {@link MessageConstraintException#}
	 * @throws ConnectException
	 *             if invalid attachment IDs are provided.
	 * @throws MessagingException
	 * @throws IOException
	 */
	public static Message createNewMessage(Session session, String subject, String textContent,
	                                       String toRecipients, String ccRecipients,
	                                       String bccRecipients, String[] attachmentList,
	                                       org.apache.axis2.context.MessageContext axis2MsgCtx)
	                                                                                           throws ConnectException,
	                                                                                           MessagingException,
	                                                                                           IOException {
		log.info("Creating the mail message");
		MimeMessage message = new MimeMessage(session);
		if (toRecipients != null) {
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toRecipients));
		}
		if (ccRecipients != null) {
			message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccRecipients));
		}
		if (bccRecipients != null) {
			message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bccRecipients));
		}
		message.setSubject(subject);
		MimeMultipart content = new MimeMultipart();
		MimeBodyPart mainPart = new MimeBodyPart();
		mainPart.setText(textContent);
		content.addBodyPart(mainPart);

		for (String attachment : attachmentList) {
			javax.activation.DataHandler handler = axis2MsgCtx.getAttachment(attachment);
			if (handler != null) {
				InputStream inStream = handler.getInputStream();
				byte[] bytes = IOUtils.toByteArray(inStream);
				ByteArrayDataSource source =
				                             new ByteArrayDataSource(bytes,
				                                                     handler.getContentType());
				MimeBodyPart bodyPart = new MimeBodyPart();
				bodyPart.setDataHandler(new DataHandler(source));
				bodyPart.setFileName(attachment);
				content.addBodyPart(bodyPart);
			} else {
				String errorLog = "Invalid attachemnt ID, " + attachment;
				log.error(errorLog);
				ConnectException connectException = new ConnectException(errorLog);
				throw (connectException);
			}
		}
		message.setContent(content);
		return message;
	}

	/**
	 * Sends the given {@link Message} through the given {@link SMTPTransport}
	 * 
	 * @param message
	 *            The message to be sent
	 * @param transport
	 *            The {@link SMTPTransport} through which the message should be
	 *            sent
	 * @throws MessagingException
	 *             as a result of failures in message transportation
	 */
	public static void sendMessage(Message message, SMTPTransport transport)
	                                                                        throws MessagingException {
		log.info("Sending the mail...");
		transport.sendMessage(message, message.getAllRecipients());
		log.info("The mail is succesfully sent");
	}

	/**
	 * Gets {@link IMAPFolder} when the folder name and the {@link IMAPStore} is
	 * given.
	 * 
	 * @param folderName
	 *            name of the {@link IMAPFolder}
	 * @param store
	 *            {@link IMAPStore} instance where the folder is located
	 * @return the folder
	 * @throws MessagingException
	 *             as a result of the failures occur while getting the folder
	 * @throws ConnectException
	 *             if the folder is null
	 */
	private static IMAPFolder getFolder(String folderName, IMAPStore store)
	                                                                       throws MessagingException,
	                                                                       ConnectException {
		IMAPFolder folder = (IMAPFolder) store.getFolder(folderName);
		if (folder == null) {
			String errorLog = "Invalid label/ folder name";
			log.error(errorLog);
			ConnectException connectException = new ConnectException(errorLog);
			throw (connectException);
		}
		return folder;
	}

	/**
	 * Returns a batch of 50 e-mail messages according to the given batch number
	 * 
	 * @param messages
	 *            Array of {@link Message}
	 * @param batchNumber
	 *            The batch number
	 * @return a batch of 50 e-mail messages
	 */
	private static Message[] getBatch(Message[] messages, int batchNumber) {
		Message[] newMessages;
		if (messages.length > GmailConstants.GMAIL_BATCH_SIZE * batchNumber) {
			newMessages =
			              Arrays.copyOfRange(messages, messages.length -
			                                           GmailConstants.GMAIL_BATCH_SIZE *
			                                           batchNumber,
			                                 messages.length - GmailConstants.GMAIL_BATCH_SIZE *
			                                         (batchNumber - 1));
		} else if (messages.length > GmailConstants.GMAIL_BATCH_SIZE * (batchNumber - 1)) {
			newMessages =
			              Arrays.copyOfRange(messages, 0, messages.length -
			                                              GmailConstants.GMAIL_BATCH_SIZE *
			                                              (batchNumber - 1));
		} else {
			newMessages = new Message[0];
		}
		return newMessages;
	}

	/**
	 * Process {@link Multipart} content.
	 * 
	 * @param contentBuilder
	 *            String builder to store the content
	 * @param multipart
	 *            input {@link Multipart} to process
	 * @param messageContext
	 *            Message context from where the attachments should be taken
	 * @param attachmentContentIDs
	 *            String builder to store content IDs of the attachments
	 * @param messageID
	 *            ID of the message
	 * @return the {@link Multipart} content as a {@link String}
	 * @throws MessagingException
	 * @throws IOException
	 */
	private static String procesMultiPart(StringBuilder contentBuilder, Multipart multipart,
	                                      MessageContext messageContext,
	                                      StringBuilder attachmentContentIDs, String messageID)
	                                                                                           throws MessagingException,
	                                                                                           IOException {
		int multiPartCount = multipart.getCount();
		for (int i = 0; i < multiPartCount; i++) {
			BodyPart bodyPart = multipart.getBodyPart(i);

			if (bodyPart.isMimeType("text/plain")) {
				contentBuilder.append("Text:\n" + bodyPart.getContent() + "\n");
			} else if (bodyPart.getContent() instanceof Multipart) {
				procesMultiPart(contentBuilder, (Multipart) bodyPart.getContent(), messageContext,
				                attachmentContentIDs, messageID);
			} else if (null != bodyPart.getDisposition() &&
			           bodyPart.getDisposition().equalsIgnoreCase(Part.ATTACHMENT)) {
				String fileName = bodyPart.getFileName();
				String attachmentID = messageID + fileName;
				contentBuilder.append("Attachment:" + fileName + "\n");
				attachmentContentIDs.append(attachmentID);
				attachmentContentIDs.append(',');
				addAttachmentToMessageContext(attachmentID, bodyPart.getInputStream(),
				                              bodyPart.getContentType(), messageContext);
			} else if (null != bodyPart.getDisposition() &&
			           bodyPart.getDisposition().equalsIgnoreCase(Part.INLINE)) {
				String fileName = bodyPart.getFileName();
				contentBuilder.append("INLINE:" + fileName + "\n");
			}

		}
		return contentBuilder.toString();
	}

	/**
	 * Add attachments to the message context.
	 * 
	 * @param attachmentContentID
	 *            Content ID (file name) of the attachment
	 * @param inputStream
	 *            Input stream to attach
	 * @param type
	 *            Content type of the attachment
	 * @param messageContext
	 *            Message context to where the attachments should be added
	 * @throws IOException
	 *             as a result of the failures occur while getting the byte
	 *             array from the input stream
	 */
	private static void addAttachmentToMessageContext(String attachmentContentID,
	                                                  InputStream inputStream, String type,
	                                                  MessageContext messageContext)
	                                                                                throws IOException {
		org.apache.axis2.context.MessageContext axis2mc =
		                                                  ((Axis2MessageContext) messageContext).getAxis2MessageContext();
		byte[] bytes = IOUtils.toByteArray(inputStream);
		javax.mail.util.ByteArrayDataSource source =
		                                             new javax.mail.util.ByteArrayDataSource(bytes,
		                                                                                     type);
		javax.activation.DataHandler handler =
		                                       new javax.activation.DataHandler(
		                                                                        (javax.activation.DataSource) source);
		axis2mc.addAttachment(attachmentContentID, handler);
		log.info("Added an attachemnt named \"" + attachmentContentID + "\" to message context");
	}
}
