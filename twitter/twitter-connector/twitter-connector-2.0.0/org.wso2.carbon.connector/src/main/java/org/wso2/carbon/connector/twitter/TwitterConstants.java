/*
 * Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.twitter;

/**
 * Class TwitterConstants defines all constants used for Twitter Connector.
 */
public final class TwitterConstants {

    //property key for the twitter credential
    public static final String TWITTER_CONSUMER_KEY="twitter.consumerKey";
    public static final String TWITTER_CONSUMER_SECRET="twitter.consumerSecret";
    public static final String TWITTER_ACCESS_TOKEN="twitter.accessToken";
    public static final String TWITTER_ACCESS_TOKEN_SECRET="twitter.accessTokenSecret";

    //property key for the endpoint
    public static final String TWITTER_ENDPOINT="uri.var.apiUrl.final";
    //property key for the http method
    public static final String HTTP_METHOD="uri.var.httpMethod";
    //signature method
    public static final String SIGNATURE_METHOD= "HMAC-SHA1";
    //encoding type
    public static final String ENC= "UTF-8";
    //Constant for empty string.
    public static final String EMPTY_STR = "";

}
