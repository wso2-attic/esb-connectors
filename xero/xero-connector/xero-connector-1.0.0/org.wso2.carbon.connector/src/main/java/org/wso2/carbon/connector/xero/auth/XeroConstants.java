/*
 * Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved. WSO2 Inc. licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.connector.xero.auth;

/**
 * Class XeroConstants defines all constants used for Xero Connector.
 */
public class XeroConstants {
    
    /**
     * Variable constant to extract request method value from message context.
     */
    public static final String REQUEST_METHOD = "uri.var.method";
    
    /**
     * Variable constant to extract api URL value from message context.
     */
    public static final String API_URL = "uri.var.apiUrl";
    
    /**
     * Variable constant to extract parameter values from message context.
     */
    public static final String PARAMS = "uri.var.params";
    
    /**
     * Variable constant to extract URI remainder value from message context.
     */
    public static final String URI_REMAINDER = "uri.var.uriRemainder";
    
    /**
     * Variable constant to extract URI appender value from message context.
     */
    public static final String URI_APPENDER = "uri.var.uriAppender";
    
    /**
     * Variable constant to extract consumer key value from message context.
     */
    public static final String CONSUMER_KEY = "uri.var.consumerKey";
    
    /**
     * Variable constant to extract consumer secret value from message context.
     */
    public static final String CONSUMER_SECRET = "uri.var.consumerSecret";
    
    /**
     * Variable constant to extract access token value from message context.
     */
    public static final String ACCESS_TOKEN = "uri.var.accessToken";
    
    /**
     * Variable constant to extract access token secret value from message context.
     */
    public static final String ACCESS_TOKEN_SECRET = "uri.var.accessTokenSecret";
    
    /**
     * Constant errorCode for OAuthMessageSignerException.
     */
    public static final int OAUTH_MESSAGE_SIGNER_EXCEPTION = 800007;
    
    /**
     * Constant errorCode for OAuthExpectationFailedException.
     */
    public static final int OAUTH_EXPECTATION_FAILED_EXCEPTION = 800008;
    
    /**
     * Constant errorCode for OAuthCommunicationException.
     */
    public static final int OAUTH_COMMUNICATION_EXCEPTION = 800009;
    
}
