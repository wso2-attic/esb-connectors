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

import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

public class TumblrCreatePost extends AbstractConnector {

    private static Log log = LogFactory.getLog(TumblrCreatePost.class);

    @Override
    public void connect(MessageContext msgCtxt) throws ConnectException {

        //retrieve oauth 1.0a credentials from the message context
        String consumerKey = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_CONSUMER_KEY);
        String consumerSecret = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_CONSUMER_SECRET);
        String accessToken = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_ACCESS_TOKEN);
        String tokenSecret = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_ACCESS_SECRET);

        String destUrl = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_URL_CREATEPOST);

        //Retrieving parameter values from the message context
        //----Common post parameters
        String typeParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_TYPE);
        String stateParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_STATE);
        String tagsParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_TAGS);
        String tweetParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_TWEET);
        String formatParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_FORMAT);
        String slugParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_SLUG);

        //new OAuth request message
        OAuthRequest requestMsg = new OAuthRequest(Verb.POST, destUrl);

        //update content type
        requestMsg.addHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.MEDIA_TYPE_X_WWW_FORM);

        //setting query parameters in the http message 
        requestMsg.addBodyParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_TYPE, typeParam);

        if (stateParam != null && stateParam.isEmpty() == false) {
            requestMsg.addBodyParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_STATE, stateParam);
        }


        if (tagsParam != null && tagsParam.isEmpty() == false) {
            requestMsg.addBodyParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_TAGS, tagsParam);
        }

        if (tweetParam != null && tweetParam.isEmpty() == false) {
            requestMsg.addBodyParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_TWEET, tweetParam);
        }

        if (formatParam != null && formatParam.isEmpty() == false) {
            requestMsg.addBodyParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_FORMAT, formatParam);
        }


        if (slugParam != null && slugParam.isEmpty() == false) {
            requestMsg.addBodyParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_SLUG, slugParam);
        }

        //----Specialized post parameters
        if (typeParam != null) {
            if (typeParam.equals("text")) {

                if (log.isDebugEnabled()) {
                    log.debug("CREATING TEXT POST");
                }
                String titleParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_TITLE);
                if (titleParam != null) {
                    requestMsg.addBodyParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_TITLE, titleParam);
                }

                String bodyParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_BODY);
                if (bodyParam != null) {
                    requestMsg.addBodyParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_BODY, bodyParam);
                }

            } else if (typeParam.equals("quote")) {

                if (log.isDebugEnabled()) {
                    log.debug("CREATING QUOTE POST");
                }
                String quoteParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_QUOTE);
                if (quoteParam != null) {
                    requestMsg.addBodyParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_QUOTE, quoteParam);
                }
                String soureParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_SOURCE);
                if (soureParam != null) {
                    requestMsg.addBodyParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_SOURCE, soureParam);
                }

            } else if (typeParam.equals("link")) {

                if (log.isDebugEnabled()) {
                    log.debug("CREATING LINK POST");
                }
                String titleParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_TITLE);
                if (titleParam != null) {
                    requestMsg.addBodyParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_TITLE, titleParam);
                }

                String urlParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_SOURCE);
                if (urlParam != null) {
                    requestMsg.addBodyParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_URL, urlParam);
                }

                String descriptionParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_DESCRIPTION);
                if (descriptionParam != null) {
                    requestMsg.addBodyParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_DESCRIPTION, descriptionParam);
                }

            } else if (typeParam.equals("chat")) {

                if (log.isDebugEnabled()) {
                    log.debug("CREATING CHAT POST");
                }
                String titleParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_TITLE);
                if (titleParam != null) {
                    requestMsg.addBodyParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_TITLE, titleParam);
                }

                String conversationParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_CONVERSATION);
                if (conversationParam != null) {
                    requestMsg.addBodyParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_CONVERSATION, conversationParam);
                }

            } else if (typeParam.equals("audio")) {

                if (log.isDebugEnabled()) {
                    log.debug("CREATING AUDIO POST");
                }
                String captionParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_CAPTION);
                if (captionParam != null) {
                    requestMsg.addBodyParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_CAPTION, captionParam);
                }

                String external_urlParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_SOURCE);
                if (external_urlParam != null) {
                    requestMsg.addBodyParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_EXTURL, external_urlParam);
                }

            } else if (typeParam.equals("video")) {

                if (log.isDebugEnabled()) {
                    log.debug("CREATING VIDEO POST");
                }
                String captionParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_CAPTION);
                if (captionParam != null) {
                    requestMsg.addBodyParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_CAPTION, captionParam);
                }

                String embedParam = (String) msgCtxt.getProperty(TumblrConstants.TUMBLR_PARAMETER_EMBED);
                if (embedParam != null) {
                    requestMsg.addBodyParameter(TumblrConstants.TUMBLR_REQUEST_PARAM_EMBED, embedParam);
                }


            }
        }
        //sign the http request message for OAuth 1.0a
        requestMsg = TumblrUtils.signOAuthRequestGeneric(requestMsg, consumerKey, consumerSecret,
                                                         accessToken, tokenSecret);

        //send request to tumblr
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
