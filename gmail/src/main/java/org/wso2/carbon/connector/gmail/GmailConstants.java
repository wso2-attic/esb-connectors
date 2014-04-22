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

/**
 * Contains all constants used in Gmail connector implementation
 */
public final class GmailConstants {

	/**
	 * Making the default constructor private since Utility classes should not
	 * have a public constructors
	 */
	private GmailConstants() {
	}

	/**
	 * Gmail login mode. This can be either "OAUTH" or "SASL".
	 */
	public static final String GMAIL_LOGIN_MODE = "login.mode";

	/**
	 * Gmail "OAUTH" login mode.
	 */
	public static final String GMAIL_OAUTH_LOGIN_MODE = "OAUTH";

	/**
	 * Gmail "SASL" login mode.
	 */
	public static final String GMAIL_SASL_LOGIN_MODE = "SASL";

	/**
	 * Name of the "username" parameter in synapse configuration.
	 */
	public static final String GMAIL_PARAM_USERNAME = "username";

	/**
	 * Name of the "password" parameter in synapse configuration.
	 */
	public static final String GMAIL_PARAM_PASSWORD = "password";

	/**
	 * Name of the "OAuth access token" parameter in synapse configuration.
	 */
	public static final String GMAIL_PARAM_OAUTH_ACCESS_TOKEN = "oauthAccessToken";

	/**
	 * Property name to store the user name for SASL authentication.
	 */
	public static final String GMAIL_USER_USERNAME = "gmail.user.username";

	/**
	 * Property name to store the password of the user for SASL authentication.
	 */
	public static final String GMAIL_USER_PASSWORD = "gmail.user.password";

	/**
	 * Property name to store the user name for OAuth authentication.
	 */
	public static final String GMAIL_OAUTH_USERNAME = "gmail.oauth.username";

	/**
	 * Property name to store the OAuth2 access token of the user for SASL
	 * authentication.
	 */
	public static final String GMAIL_OAUTH_ACCESS_TOKEN = "gmail.oauth.accessToken";

	/**
	 * Property name to store whether the OAuth2 provider is initialized or not.
	 */
	public static final String GMAIL_OAUTH2_PROVIDER = "gmail.oauth2.provider";

	/**
	 * Property name to store the IMAPStore instance
	 */
	public static final String GMAIL_IMAP_STORE_INSTANCE = "gmail.imap.store.instance";

	/**
	 * Property name to store the SMTP connection information
	 */
	public static final String GMAIL_SMTP_CONNECTION_INSTANCE = "gmail.smtp.session.instance";

	/**
	 * Name of the "label" parameter in synapse configuration.
	 */
	public static final String GMAIL_PARAM_LABEL = "label";

	/**
	 * Name of the "labels" parameter in synapse configuration.
	 */
	public static final String GMAIL_PARAM_LABELS = "labels";

	/**
	 * Name of the "threadID" parameter in synapse configuration.
	 */
	public static final String GMAIL_PARAM_THREADID = "threadID";

	/**
	 * Name of the "messageID" parameter in synapse configuration.
	 */
	public static final String GMAIL_PARAM_MESSAGEID = "messageID";

	/**
	 * Name of the "subject" parameter in synapse configuration.
	 */
	public static final String GMAIL_PARAM_SUBJECT = "subject";

	/**
	 * Name of the "toRecipients" parameter in synapse configuration.
	 */
	public static final String GMAIL_PARAM_TO_RECIPIENTS = "toRecipients";

	/**
	 * Name of the "ccRecipients" parameter in synapse configuration.
	 */
	public static final String GMAIL_PARAM_CC_RECIPIENTS = "ccRecipients";

	/**
	 * Name of the "bccRecipients" parameter in synapse configuration.
	 */
	public static final String GMAIL_PARAM_BCC_RECIPIENTS = "bccRecipients";

	/**
	 * Name of the "textContent" parameter in synapse configuration.
	 */
	public static final String GMAIL_PARAM_TEXT_CONTENT = "textContent";

	/**
	 * Name of the "attachmentIDs" parameter in synapse configuration.
	 */
	public static final String GMAIL_PARAM_ATTACHMENTIDS = "attachmentIDs";

	/**
	 * Name of the "searchTerm" parameter in synapse configuration.
	 */
	public static final String GMAIL_PARAM_SEARCH_TERM = "gmailSearchTerm";

	/**
	 * Name of the "batchNumber" parameter in synapse configuration.
	 */
	public static final String GMAIL_PARAM_BATCH_NUMBER = "batchNumber";

	/**
	 * Default batch size.
	 */
	public static final int GMAIL_BATCH_SIZE = 50;

	/**
	 * Gmail folder name for all mails.
	 */
	public static final String GMAIL_ALL_MAIL = "[Gmail]/All Mail";

	/**
	 * Gmail folder name for trash.
	 */
	public static final String GMAIL_TRASH = "[Gmail]/Trash";

	/**
	 * Response node name of the "delete mail message" operation.
	 */
	public static final String GMAIL_DELETE_MAIL_MESSAGE_RESPONSE =
	                                                                "gmailDeleteMailMessageResponse";

	/**
	 * Response node name of the "delete mail thread" operation.
	 */
	public static final String GMAIL_DELETE_MAIL_THREAD_RESPONSE = "gmailDeleteMailThreadResponse";

	/**
	 * Response node name of the "list all mails" operation.
	 */
	public static final String GMAIL_LIST_ALL_MAILS_RESPONSE = "gmailListAllMailsResponse";

	/**
	 * Response node name of the "list all unread mails" operation.
	 */
	public static final String GMAIL_LIST_ALL_UNREAD_MAILS_RESPONSE =
	                                                                  "gmailListAllUnreadMailsResponse";

	/**
	 * Response node name of the "list all unread mails under label" operation.
	 */
	public static final String GMAIL_LIST_ALL_UNREAD_MAILS_UNDER_LABEL_RESPONSE =
	                                                                              "gmailListAllUnreadMailsUnderLabelResponse";

	/**
	 * Response node name of the "list read mail message" operation.
	 */
	public static final String GMAIL_READ_MAIL_MESSAGE_RESPONSE = "gmailReadMailMessageResponse";

	/**
	 * Response node name of the "search mails" operation.
	 */
	public static final String GMAIL_SEARCH_MAILS_RESPONSE = "gmailSearchMailsResponse";

	/**
	 * Response node name of the "send mail" operation.
	 */
	public static final String GMAIL_SEND_MAIL_RESPONSE = "sendMailResponse";

	/**
	 * Response node name of the "set labels" operation.
	 */
	public static final String GMAIL_SET_LABELS_RESPONSE = "gmailSetLabelsResponse";

	/**
	 * Stores the value, "true".
	 */
	public static final boolean GMAIL_TRUE_VALUE = true;

	/**
	 * Gmail authentication mechanism, "XOAUTH2".
	 */
	public static final String GMAIL_AUTHENTICATION_MECHANISM = "XOAUTH2";

	/**
	 * IMAP host name
	 */
	public static final String GMAIL_IMAP_HOST = "imap.gmail.com";

	/**
	 * IMAP port
	 */
	public static final int GMAIL_IMAP_PORT = 993;

	/**
	 * SMTP host name
	 */
	public static final String GMAIL_SMTP_HOST = "smtp.gmail.com";

	/**
	 * SMTP port
	 */
	public static final int GMAIL_SMTP_PORT = 587;
}
