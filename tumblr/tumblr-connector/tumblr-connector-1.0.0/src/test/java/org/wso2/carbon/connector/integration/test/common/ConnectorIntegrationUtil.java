/**
 *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.connector.integration.test.common;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TumblrApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Request;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;


public class ConnectorIntegrationUtil {

    /**
     * Function to make simple GET request directly from Tumblr api
     * @param targetUrl target request uri
     * @return returns response payload as a String
     */
    public static String DirectHttpGET(String targetUrl) {

        Request httpRequest = new Request(Verb.GET, targetUrl);
        httpRequest.setFollowRedirects(false);
        Response response = httpRequest.send();

        return response.getBody();
    }


    /**
     * Method to make OAuth 1.0a authenticated GET request directly from Tumblr api
     *
     * @param targetUrl      target request uri
     * @param consumerKey    consumer key (api key) for oauth 1.0a authentication
     * @param consumerSecret Consumer secret of the consumer key
     * @param accessToken    Access token received from oauth dance
     * @param tokenSecret    Access token secret
     * @param queryParams    Hashmap of key value pairs for query string parameters
     * @return JSONObject representation of json response payload
     * @throws JSONException throws when error occurred while creating JSONObject
     */
    public static JSONObject DirectHttpAuthGET(String targetUrl, String consumerKey,
                                               String consumerSecret,
                                               String accessToken, String tokenSecret,
                                               HashMap<String, String> queryParams)
                                                                        throws JSONException {

        OAuthService service = new ServiceBuilder().provider(TumblrApi.class).
                                                apiKey(consumerKey).apiSecret(consumerSecret).build();

        Token userAccessToken = new Token(accessToken, tokenSecret);

        //new OAuth request message
        OAuthRequest requestMsg = new OAuthRequest(Verb.GET, targetUrl);

        if (queryParams != null) {
            Set set = queryParams.entrySet();
            Iterator iter = set.iterator();

            while (iter.hasNext()) {

                Map.Entry mapEntry = (Map.Entry) iter.next();
                requestMsg.addQuerystringParameter((String) mapEntry.getKey(), (String) mapEntry.getValue());

            }
        }

        //sign the oauth request
        service.signRequest(userAccessToken, requestMsg);

        Response response = requestMsg.send();

        JSONObject jObj = new JSONObject(response.getBody());

        return jObj;
    }


    /**
     * Method to make OAuth 1.0a authenticated POST request directly from Tumblr api
     *
     * @param targetUrl      target request uri
     * @param consumerKey    consumer key (api key) for oauth 1.0a authentication
     * @param consumerSecret Consumer secret of the consumer key
     * @param accessToken    Access token received from oauth dance
     * @param tokenSecret    Access token secret
     * @return JSONObject representation of json response payload
     * @throws JSONException throws when error occurred while creating JSONObject
     */
    public static JSONObject DirectHttpAuthPOST(String targetUrl, String consumerKey,
                                                String consumerSecret,
                                                String accessToken, String tokenSecret,
                                                HashMap<String, String> bodyQueryParams)
            throws JSONException {

        OAuthService service = new ServiceBuilder().provider(TumblrApi.class)
                .apiKey(consumerKey)
                .apiSecret(consumerSecret)
                .build();

        Token userAccessToken = new Token(accessToken, tokenSecret);

        //new OAuth request message
        OAuthRequest requestMsg = new OAuthRequest(Verb.POST, targetUrl);

        //update content type
        requestMsg.addHeader("Content-Type", "application/x-www-form-urlencoded");

        Set set = bodyQueryParams.entrySet();
        Iterator iter = set.iterator();

        while (iter.hasNext()) {

            Map.Entry mapEntry = (Map.Entry) iter.next();
            requestMsg.addBodyParameter((String) mapEntry.getKey(), (String) mapEntry.getValue());

        }

        //sign the oauth request
        service.signRequest(userAccessToken, requestMsg);

        Response response = requestMsg.send();

        JSONObject jObj = new JSONObject(response.getBody());

        return jObj;
    }

    /**
     * Function to send POST request
     * @param payLoad payload to send in POST request
     * @param targetUrl target endpoint of the POST request
     * @return returns response payload as a String
     */
    public static String ConnectorHttpPOST(String payLoad, String targetUrl) {

        Request httpRequest = new Request(Verb.POST, targetUrl);
        httpRequest.setFollowRedirects(false);
        httpRequest.addHeader("Content-Type", "application/json");
        httpRequest.addPayload(payLoad);

        Response response = httpRequest.send();

        return response.getBody();
    }

    /**
     * Function to send POST request
     * @param payLoad payload to send in POST request
     * @param targetUrl target endpoint of the POST request
     * @return returns response payload as a JSONObject
     * @throws org.json.JSONException throws when error occurred while creating JSONObject
     */
    public static JSONObject ConnectorHttpPOSTJsonObj(String payLoad, String targetUrl)
            throws JSONException {

        Request httpRequest = new Request(Verb.POST, targetUrl);
        httpRequest.setFollowRedirects(false);
        httpRequest.addHeader("Content-Type", "application/json");
        httpRequest.addPayload(payLoad);

        Response response = httpRequest.send();

        JSONObject obj = new JSONObject(response.getBody());

        return obj;
    }

}



