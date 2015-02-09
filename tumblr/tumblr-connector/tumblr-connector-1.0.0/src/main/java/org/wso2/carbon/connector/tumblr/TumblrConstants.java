/*
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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


package org.wso2.carbon.connector.tumblr;

public class TumblrConstants {

    public static final String TUMBLR_CONSUMER_KEY = "tumblr.oauth.consumerKey";
    public static final String TUMBLR_CONSUMER_SECRET = "tumblr.oauth.consumerSecret";
    public static final String TUMBLR_ACCESS_TOKEN = "tumblr.oauth.accessToken";
    public static final String TUMBLR_ACCESS_SECRET = "tumblr.oauth.accessSecret";

    public static final String TUMBLR_URL_QUEUEDPOSTS = "tumblr.url.queuedPosts";
    public static final String TUMBLR_URL_FOLLOWERS = "tumblr.url.followers";
    public static final String TUMBLR_URL_DRAFTS = "tumblr.url.drafts";
    public static final String TUMBLR_URL_CREATEPOST = "tumblr.url.createPost";
    public static final String TUMBLR_URL_EDITPOST = "tumblr.url.editPost";
    public static final String TUMBLR_URL_DELETEPOST = "tumblr.url.deletePost";
    public static final String TUMBLR_URL_USERINFO = "tumblr.url.userInfo";
    public static final String TUMBLR_URL_DASHBOARD = "tumblr.url.dashboard";
    public static final String TUMBLR_URL_LIKES = "tumblr.url.likes";
    public static final String TUMBLR_URL_FOLLOWING = "tumblr.url.following";
    public static final String TUMBLR_URL_FOLLOW = "tumblr.url.follow";
    public static final String TUMBLR_URL_UNFOLLOW = "tumblr.url.unFollow";
    public static final String TUMBLR_URL_LIKE = "tumblr.url.like";
    public static final String TUMBLR_URL_UNLIKE = "tumblr.url.unlike";
    public static final String TUMBLR_URL_SUBMISSIONS = "tumblr.url.submissions";

    public static final String TUMBLR_PARAMETER_OFFSET = "tumblr.param.offset"; // used for  get queued posts, dashboard, likes
    public static final String TUMBLR_PARAMETER_LIMIT = "tumblr.param.limit"; // used for  get queued posts, dashboard, likes
    public static final String TUMBLR_PARAMETER_FILTER = "tumblr.param.filter";
    public static final String TUMBLR_PARAMETER_BEFOREID = "tumblr.param.beforeId";

    public static final String TUMBLR_PARAMETER_TYPE = "tumblr.param.type";// used for creating post, get dashboard
    public static final String TUMBLR_PARAMETER_STATE = "tumblr.param.state";
    public static final String TUMBLR_PARAMETER_TAGS = "tumblr.param.tags";
    public static final String TUMBLR_PARAMETER_TWEET = "tumblr.param.tweet";
    public static final String TUMBLR_PARAMETER_FORMAT = "tumblr.param.format";
    public static final String TUMBLR_PARAMETER_SLUG = "tumblr.param.slug";

    public static final String TUMBLR_PARAMETER_TITLE = "tumblr.param.title";// used for text/link/chat posts
    public static final String TUMBLR_PARAMETER_BODY = "tumblr.param.body";

    public static final String TUMBLR_PARAMETER_CAPTION = "tumblr.param.caption";//used for photo/audio/video posts
    public static final String TUMBLR_PARAMETER_CLICKLINK = "tumblr.param.clickLink";
    public static final String TUMBLR_PARAMETER_SOURCE = "tumblr.param.source";//used for photo/quote/link/audio posts

    public static final String TUMBLR_PARAMETER_QUOTE = "tumblr.param.quote";

    public static final String TUMBLR_PARAMETER_DESCRIPTION = "tumblr.param.description";

    public static final String TUMBLR_PARAMETER_CONVERSATION = "tumblr.param.conversation";

    public static final String TUMBLR_PARAMETER_EMBED = "tumblr.param.embed";

    public static final String TUMBLR_PARAMETER_ID = "tumblr.param.id";//used for edit/reblog/delete posts, like, unlike

    public static final String TUMBLR_PARAMETER_REBLOGID = "tumblr.param.reblogId";//used in reblog, like, unlike
    public static final String TUMBLR_PARAMETER_COMMENT = "tumblr.param.comment";

    public static final String TUMBLR_PARAMETER_SINCEID = "tumblr.param.sinceId";
    public static final String TUMBLR_PARAMETER_NEEDREBLOGINFO = "tumblr.param.needReblogInfo";
    public static final String TUMBLR_PARAMETER_NEEDNOTEINFO = "tumblr.param.needNoteInfo";

    public static final String TUMBLR_PARAMETER_URL = "tumblr.param.url";

    //Constants related to tumblr API requests
    public static final String TUMBLR_REQUEST_PARAM_TYPE = "type";
    public static final String TUMBLR_REQUEST_PARAM_STATE = "state";
    public static final String TUMBLR_REQUEST_PARAM_TAGS = "tags";
    public static final String TUMBLR_REQUEST_PARAM_TWEET = "tweet";
    public static final String TUMBLR_REQUEST_PARAM_FORMAT = "format";
    public static final String TUMBLR_REQUEST_PARAM_SLUG = "slug";
    public static final String TUMBLR_REQUEST_PARAM_TITLE = "title";
    public static final String TUMBLR_REQUEST_PARAM_BODY = "body";
    public static final String TUMBLR_REQUEST_PARAM_QUOTE = "quote";
    public static final String TUMBLR_REQUEST_PARAM_SOURCE = "source";
    public static final String TUMBLR_REQUEST_PARAM_URL = "url";
    public static final String TUMBLR_REQUEST_PARAM_DESCRIPTION = "description";
    public static final String TUMBLR_REQUEST_PARAM_CONVERSATION = "conversation";
    public static final String TUMBLR_REQUEST_PARAM_CAPTION = "caption";
    public static final String TUMBLR_REQUEST_PARAM_EXTURL = "external_url";
    public static final String TUMBLR_REQUEST_PARAM_EMBED = "embed";
    public static final String TUMBLR_REQUEST_PARAM_ID = "id";
    public static final String TUMBLR_REQUEST_PARAM_BEFOREID = "before_id";
    public static final String TUMBLR_REQUEST_PARAM_FILTER = "filter";
    public static final String TUMBLR_REQUEST_PARAM_LIMIT = "limit";
    public static final String TUMBLR_REQUEST_PARAM_OFFSET = "offset";
    public static final String TUMBLR_REQUEST_PARAM_SINCE_ID = "since_id";
    public static final String TUMBLR_REQUEST_PARAM_REBLOG_INFO = "reblog_info";
    public static final String TUMBLR_REQUEST_PARAM_NOTES_INFO = "notes_info";
    public static final String TUMBLR_REQUEST_PARAM_REBLOG_KEY = "reblog_key";
    public static final String TUMBLR_REQUEST_PARAM_COMMENT = "comment";
    public static final String TUMBLR_REQUEST_PARAM_ = "";

}
