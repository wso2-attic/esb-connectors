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

package org.wso2.carbon.inbound.salesforce.poll;

public class SalesforceConstant {

    //property values for the salesforce inbound endpoint
    public static final String USER_NAME = "connection.salesforce.userName";
    public static final String PASSWORD = "connection.salesforce.password";
    public static final String LOGIN_ENDPOINT = "connection.salesforce.loginEndpoint";
    public static final String PACKAGE_NAME="connection.salesforce.packageName";
    public static final String PACKAGE_VERSION="connection.salesforce.packageVersion";

    //object for the salesforce inbound endpoint
    public static final String SOBJECT = "connection.salesforce.salesforceObject";

    //defaultt parameters for the salesforce inbound endpoint
    public static final String CONNECTION_TIMEOUT = "connection.salesforce.connectionTimeout";
    public static final int CONNECTION_TIMEOUT_DEFAULT = 10*1000;

    public static final String READ_TIMEOUT = "connection.salesforce.readTimeout";
    public static final int READ_TIMEOUT_DEFAULT = 10*1000;

    public static final String WAIT_TIME="connection.salesforce.waitTime";
    public static final int WAIT_TIME_DEFAULT= 24 * 60 * 60 * 1000;


    //content type of the message
    public static final String CONTENT_TYPE = "application/json";
}
