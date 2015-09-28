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

package org.wso2.carbon.inbound.twitter.poll;

public class TwitterConstant {

    //property key for the twitter inbound endpoint
    public static final String CONSUMER_KEY = "connection.twitter.consumerKey";
    public static final String CONSUMER_SECRET = "connection.twitter.consumerSecret";
    public static final String ACCESS_TOKEN = "connection.twitter.accessToken";
    public static final String ACCESS_SECRET = "connection.twitter.accessSecret";
    public static final String TWITTER_OPERATION = "twitter.operation";
    public static final String TWITTER_COUNT = "twitter.count";
    public static final String TWITTER_FOLLOW = "twitter.follow";
    public static final String TWITTER_TRACK = "twitter.track";
    public static final String TWITTER_LOCATIONS = "twitter.locations";
    public static final String TWITTER_LANGUAGE = "twitter.language";
    public static final String TWITTER_FILTER_LEVEL = "twitter.filterLevel";
    public static final String TWITTER_WITH_FOLLOWINGS = "twitter.withFollowings";

    //operation for the twitter inbound endpoint
    public static final String FILTER_STREAM_OPERATION = "filter";
    public static final String FIREHOSE_STREAM_OPERATION = "firehose";
    public static final String LINK_STREAM_OPERATION = "link";
    public static final String SAMPLE_STREAM_OPERATION = "sample";
    public static final String SITE_STREAM_OPERATION = "site";
    public static final String USER_STREAM_OPERATION = "user";
    public static final String RETWEET_STREAM_OPERATION = "retweet";

    //content type of the message
    public static final String CONTENT_TYPE = "application/json";
}
