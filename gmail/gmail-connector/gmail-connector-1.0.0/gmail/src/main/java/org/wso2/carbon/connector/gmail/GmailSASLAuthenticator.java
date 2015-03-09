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

/**
 * Provide the SASL authentication for both IMAP and SMTP.
 */
public final class GmailSASLAuthenticator {

	/**
	 * Making the default constructor private since Utility classes should not
	 * have a public constructors
	 */
	private GmailSASLAuthenticator() {
	}

	/**
	 * Connects to IMAPStore
	 * 
	 * @param username
	 *            user name
	 * @param password
	 *            password of the user
	 * @return the authenticated IMAPSore instance
	 * @throws MessagingException
	 *             as a result of authentication failure
	 */
	public static IMAPStore connectToIMAP(String username, String password)
	                                                                       throws MessagingException {
		Properties properties = System.getProperties();
		properties.setProperty("mail.store.protocol", "imaps");
		Session session = Session.getDefaultInstance(properties, null);
		IMAPSSLStore imapStore = new IMAPSSLStore(session, new URLName("http://imap.gmail.com"));
		imapStore.connect(username, password);
		return imapStore;
	}

	/**
	 * Connects to SMTP transport and mail session.
	 * 
	 * @param username
	 *            user name
	 * @param password
	 *            password of the user
	 * @return {@link GmailSMTPConnectionObject} instance
	 * @throws MessagingException
	 *             as a result of authentication failure
	 */
	public static GmailSMTPConnectionObject connectToSMTPSession(final String username,
	                                                             final String password)
	                                                                                   throws MessagingException {
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", true);
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put(GmailConstants.GMAIL_SMTP_HOST, GmailConstants.GMAIL_SMTP_PORT);
		Session session = Session.getInstance(properties);
		SMTPTransport transport = new SMTPTransport(session, null);
		transport.connect(GmailConstants.GMAIL_SMTP_HOST, GmailConstants.GMAIL_SMTP_PORT, username,
		                  password);
		return new GmailSMTPConnectionObject(session, transport);
	}
}