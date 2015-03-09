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

import java.util.Properties;

import com.google.code.com.sun.mail.imap.IMAPSSLStore;
import com.google.code.com.sun.mail.imap.IMAPStore;
import com.google.code.com.sun.mail.smtp.SMTPTransport;
import com.google.code.javax.mail.MessagingException;
import com.google.code.javax.mail.Session;
import com.google.code.javax.mail.URLName;
import com.google.code.samples.oauth2.OAuth2Authenticator;
import com.google.code.samples.oauth2.OAuth2SaslClientFactory;

/**
 * Provide the OAuth authentication for both IMAP and SMTP.
 */
public final class GmailOAuth2SASLAuthenticator {

	/**
	 * Making the default constructor private since Utility classes should not
	 * have a public constructors
	 */
	private GmailOAuth2SASLAuthenticator() {
	}

	/**
	 * Installing the OAuth2 SASL provider.
	 */
	public static void initializeOAuth2Provider() {
		OAuth2Authenticator.initialize();
	}

	/**
	 * Connects to IMAPStore
	 * 
	 * @param username
	 *            user name
	 * @param oauthToken
	 *            user's OAuth access token
	 * @return authenticated IMAPSore instance
	 * @throws MessagingException
	 *             as a result of authentication failure
	 */
	public static IMAPStore connectToIMAP(String username, String oauthToken)
	                                                                         throws MessagingException {
		Properties props = new Properties();
		props.put("mail.imaps.sasl.enable", GmailConstants.GMAIL_TRUE_VALUE);
		props.put("mail.imaps.sasl.mechanisms", GmailConstants.GMAIL_AUTHENTICATION_MECHANISM);
		props.put(OAuth2SaslClientFactory.OAUTH_TOKEN_PROP, oauthToken);

		Session session = Session.getInstance(props);
		final URLName unusedUrlName = null;
		final String emptyPassword = "";
		IMAPSSLStore store = new IMAPSSLStore(session, unusedUrlName);
		store.connect(GmailConstants.GMAIL_IMAP_HOST, GmailConstants.GMAIL_IMAP_PORT, username,
		              emptyPassword);
		return store;
	}

	/**
	 * Connects to SMTP transport and mail session.
	 * 
	 * @param username
	 *            user name
	 * @param accessToken
	 *            OAuth access token of the user
	 * @return {@link GmailSMTPConnectionObject} instance
	 * @throws MessagingException
	 *             as a result of authentication failure
	 */
	public static GmailSMTPConnectionObject connectToSMTP(String username, String accessToken)
	                                                                                          throws MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.starttls.enable", GmailConstants.GMAIL_TRUE_VALUE);
		props.put("mail.smtp.starttls.required", GmailConstants.GMAIL_TRUE_VALUE);
		props.put("mail.smtp.sasl.enable", GmailConstants.GMAIL_TRUE_VALUE);
		props.put("mail.smtp.sasl.mechanisms", GmailConstants.GMAIL_AUTHENTICATION_MECHANISM);
		props.put(OAuth2SaslClientFactory.OAUTH_TOKEN_PROP, accessToken);

		Session session = Session.getInstance(props);
		SMTPTransport transport = new SMTPTransport(session, null);
		transport.connect(GmailConstants.GMAIL_SMTP_HOST, GmailConstants.GMAIL_SMTP_PORT, username,
		                  "");
		return new GmailSMTPConnectionObject(session, transport);
	}
}