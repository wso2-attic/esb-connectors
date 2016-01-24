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

import org.apache.synapse.MessageContext;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

public class TumblrGetUserDashboard extends AbstractConnector {

    @Override
    public void connect(MessageContext msgCtxt) throws ConnectException {

        String consumerKey = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_CONSUMER_KEY);
        String consumerSecret = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_CONSUMER_SECRET);
        String accessToken = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_ACCESS_TOKEN);
        String tokenSecret = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_ACCESS_SECRET);

        String destUrl = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_URL_DASHBOARD);

        String limitParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_LIMIT);
        String offsetParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_OFFSET);
        String typeParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_TYPE);
        String sinceIdParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_SINCEID);
        String needReblogInfoParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_NEEDREBLOGINFO);
        String needNoteInfoParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_NEEDNOTEINFO);


        //new OAuth request message
        OAuthRequest requestMsg = new OAuthRequest(Verb.GET, destUrl);

        //setting query parameters in the http message body
        if (limitParam != null && limitParam.isEmpty() == false) {
            requestMsg.addQuerystringParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_LIMIT, limitParam);
        }
        if (offsetParam != null && offsetParam.isEmpty() == false) {
            requestMsg.addQuerystringParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_OFFSET, offsetParam);
        }

        if (typeParam != null && typeParam.isEmpty() == false) {
            requestMsg.addQuerystringParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_TYPE, typeParam);
        }

        if (sinceIdParam != null && sinceIdParam.isEmpty() == false) {
            requestMsg.addQuerystringParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_SINCE_ID, sinceIdParam);
        }

        if (needReblogInfoParam != null && needNoteInfoParam.isEmpty() == false) {
            requestMsg.addQuerystringParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_REBLOG_INFO, needReblogInfoParam);
        }

        if (needNoteInfoParam != null && needNoteInfoParam.isEmpty() == false) {
            requestMsg.addQuerystringParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_NOTES_INFO, needNoteInfoParam);
        }


        //sign the http request message for OAuth 1.0a
        requestMsg = TumblrUtils.signOAuthRequestGeneric(requestMsg, consumerKey, consumerSecret,
                                                         accessToken, tokenSecret);

        Response response = requestMsg.send();

        if (log.isDebugEnabled()) {
            log.debug("REQUEST TO TUMBLR : Header - " + requestMsg.getHeaders());
            log.debug("REQUEST TO TUMBLR : Body - " + requestMsg.getBodyContents());
            log.debug("SENDING REQUEST TO TUMBLR : " + destUrl);
            log.debug("RECEIVED RESPONSE FROM TUMBLR : Header - " + response.getHeaders());
            log.debug("RECEIVED RESPONSE FROM TUMBLR : Body - " + response.getBody());
        }
        //update message payload in message context
        msgCtxt.setProperty("tumblr.response", response.getBody());

    }

}
