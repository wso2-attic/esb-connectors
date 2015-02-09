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

import java.io.ByteArrayInputStream;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axis2.AxisFault;
import org.apache.synapse.MessageContext;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TumblrApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import org.apache.synapse.commons.json.JsonUtil;

/**
 * Class containing utility methods
 */
public class TumblrUtils {

    /**
     * Method to sign generic OAuthRequest using provided credentialsfor OAuth 1.0a using Scrib library
     *
     * @param request        OAuthRequest to sign
     * @param consumerKey    Consumer key of the application
     * @param consumerSecret Consumer secret of the consumer key
     * @param accessToken    Access token for protected resource
     * @param tokenSecret    Token secret of the access token
     * @return Signed OAuthRequest
     */
    public static OAuthRequest signOAuthRequestGeneric(OAuthRequest request, String consumerKey,
                                                       String consumerSecret, String accessToken,
                                                       String tokenSecret) {

        OAuthService service = new ServiceBuilder().provider(TumblrApi.class).apiKey(consumerKey).
                                                                    apiSecret(consumerSecret).build();
        Token userAccessToken = new Token(accessToken, tokenSecret);
        service.signRequest(userAccessToken, request);

        return request;
    }

}
