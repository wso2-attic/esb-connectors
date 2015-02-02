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
package org.wso2.carbon.connector.apns;

import java.io.InputStream;

/**
 * This class represents certificate information which is needed to access push
 * notification service.
 */
public class Certificate {

    /**
     * Label of the certificate.
     */
    private String name;

    /**
     * Content of the certificate.
     */
    private InputStream content;

    /**
     * Password of the certificate (PKCS12 file).
     */
    private String password;

    /**
     * Constructor of {@link Certificate}.
     * 
     * @param name
     *            Label of the certificate.
     * @param content
     *            Content of the certificate.
     * @param password
     *            Password of the certificate (PKCS12 file).
     */
    public Certificate(String name, InputStream content, String password) {
	this.name = name;
	this.content = content;
	this.password = password;
    }

    /**
     * Returns the name of the certificate.
     * 
     * @return Name of the certificate.
     */
    public String getName() {
	return name;
    }

    /**
     * Set the name of the certificate.
     * 
     * @param name
     *            Name of the certificate.
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * Returns the content of the certificate.
     * 
     * @return Content of the certificate.
     */
    public InputStream getContent() {
	return content;
    }

    /**
     * Set the content of the certificate.
     * 
     * @param content
     *            Content of the certificate.
     */
    public void setContent(InputStream content) {
	this.content = content;
    }

    /**
     * Returns the password of the certificate.
     * 
     * @return Password of the certificate.
     */
    public String getPassword() {
	return password;
    }

    /**
     * Sets the password of the certificate.
     * 
     * @param password
     *            Password of the certificate.
     */
    public void setPassword(String password) {
	this.password = password;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Certificate{" + "name='" + name + '\'' + ", data=" + content
		+ ", password='" + password + '\'' + '}';
    }
}
