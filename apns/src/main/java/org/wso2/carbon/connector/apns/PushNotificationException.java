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

/**
 * Exception which is being thrown when something goes wrong with push
 * Notification sending.
 */
public class PushNotificationException extends Exception {

    private static final long serialVersionUID = 943509928066781151L;

    /**
     * Error code to represent the type of the error.
     */
    private String errorCode;

    /**
     * Constructor with error message, error code and cause
     * 
     * @param message
     *            Error message.
     * @param errorCode
     *            Error code.
     * @param cause
     *            Cause.
     */
    public PushNotificationException(String message, String errorCode,
	    Throwable cause) {
	super(String.format("%s - Error Code : <%s>", message, errorCode),
		cause);
	this.errorCode = errorCode;
    }

    /**
     * Constructor with error message and error code.
     * 
     * @param message
     *            Error message.
     * @param errorCode
     *            Error code.
     */
    public PushNotificationException(String message, String errorCode) {
	super(String.format("%s - Error Code : <%s>", message, errorCode));
	this.errorCode = errorCode;
    }

    /**
     * Returns the error code.
     * 
     * @return Error code.
     */
    public String getErrorCode() {
	return errorCode;
    }
}
