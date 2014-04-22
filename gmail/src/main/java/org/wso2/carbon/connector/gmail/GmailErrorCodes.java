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
 * Contains all error codes used in the Gmail connector.
 */
public final class GmailErrorCodes {

	/**
	 * Making the default constructor private since Utility classes should not
	 * have a public constructors
	 */
	private GmailErrorCodes() {
	}

	/**
	 * Error code for connect exception.
	 */
	public static final int GMAIL_ERROR_CODE_CONNECT_EXCEPTION = 700001;

	/**
	 * Error code for messaging exception.
	 */
	public static final int GMAIL_ERROR_CODE_MESSAGING_EXCEPTION = 700002;

	/**
	 * Error code for number format exception.
	 */
	public static final int GMAIL_ERROR_CODE_NUMBER_FORMAT_EXCEPTION = 700003;

	/**
	 * Error code for IO exception.
	 */
	public static final int GMAIL_ERROR_CODE_IO_EXCEPTION = 700004;

	/**
	 * Error code for common exceptions.
	 */
	public static final int GMAIL_COMMON_EXCEPTION = 700005;
}
