/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.wso2.carbon.connector;

import com.google.code.com.sun.mail.smtp.SMTPTransport;
import com.google.code.javax.mail.Session;

/**
 * This class provides the object model to store {@link com.google.code.javax.mail.Session} and
 * {@link com.google.code.com.sun.mail.smtp.SMTPTransport} instances to send mails through SMTP.
 */
public class GmailSMTPConnectionObject {

    /**
     * class variable to store {@link com.google.code.javax.mail.Session} instance.
     */
    private Session session;

    /**
     * Class variable to store {@link com.google.code.com.sun.mail.smtp.SMTPTransport} instance.
     */
    private SMTPTransport transport;

    /**
     * Constructor of the {@link GmailSMTPConnectionObject} class
     *
     * @param session
     *            Authenticated {@link com.google.code.javax.mail.Session} instance
     * @param transport
     *            Authenticated and connected {@link com.google.code.com.sun.mail.smtp.SMTPTransport} instance
     */
    public GmailSMTPConnectionObject(Session session, SMTPTransport transport) {
        this.session = session;
        this.transport = transport;
    }

    /**
     * @return Stored {@link com.google.code.javax.mail.Session}
     */
    public Session getSession() {
        return this.session;
    }

    /**
     * @return Stored {@link SMTPTransportM}
     */
    public SMTPTransport getTransport() {
        return this.transport;
    }

}
