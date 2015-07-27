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

package org.wso2.carbon.connector.integration.test.tumblr;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;


/**
 * Tumblr Connector Integration test
 */
public class TumblrConnectoreIntegrationTest extends ConnectorIntegrationTestBase {

    protected static final String CONNECTOR_NAME = "tumblr-connector-1.0.0";

    private static Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

    private static String createdTextPostID;
    private static String createdOptionalTextPostID;
    private static String connecterCreatedTextPostID;
    private static String connecterCreatedOptionalTextPostID;

    private static Log log = LogFactory.getLog(TumblrConnectoreIntegrationTest.class);

    /**
     * Initialization
     *
     * @throws Exception
     */
    @BeforeTest(alwaysRun = true)
    protected void init() throws Exception {
        super.init(CONNECTOR_NAME);

        esbRequestHeadersMap.put("Content-Type", "application/json");

    }


    /**
     * Function to clean up created resources
     */
    @AfterClass(alwaysRun = true)
    protected void cleanup() {
        axis2Client.destroy();
    }


/*************************************************************************************************
 *
 * 										POSTIVE TEST CASES
 *
 * ************************************************************************************************/

    /**
     * Positive test case for getBlogInfo
     * @throws org.json.JSONException Thrown if exception occurred during JSONObject creation or sending json request
     * to ESB failed by sendJsonRestRequest
     * @throws java.io.IOException Thrown sending json request to ESB failed by sendJsonRestRequest
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "tumblr {getBlogInfo} integration positive test")
    public void testTumblrGetBlogInfo() throws JSONException, IOException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/info?api_key=" + consumerKey;
        log.info("requestUrl : " + requestUrl);

        String directResponse = ConnectorIntegrationUtil.DirectHttpGET(requestUrl);
        log.info("DirectResponse : " + directResponse);

        JSONObject directResponseJObj = new JSONObject(directResponse);

        //Get response using the connector
        log.info("Proxy service url :" + getProxyServiceURL("tumblr"));


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "getBlogInfo.json");

        //String connectorResponse = ConnectorIntegrationUtil.ConnectorHttpPOST("", getProxyServiceURL("tumblr"));
        log.info("Connector response : " + esbRestResponse.getBody().toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(), directResponseJObj.get("meta").toString());
       // Assert.assertEquals(esbRestResponse.getBody().get("response").toString(), directResponseJObj.get("response").toString());
    }


    /**
     * Positive integration test case for getBlogLikes
     * Including default values for optional parameters
     *
     * @throws org.json.JSONException Thrown if exception occurred during JSONObject creation and json
     *                                  request to ESB failed by sendJsonRestRequest
     * @throws java.io.IOException Thrown sending json request to ESB failed by sendJsonRestRequest
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "tumblr {getBlogLikes} integration positive test")
    public void testTumblrGetBlogLikes() throws JSONException, IOException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/likes?api_key=" + consumerKey;
        log.info("requestUrl : " + requestUrl);

        String directResponse = ConnectorIntegrationUtil.DirectHttpGET(requestUrl);


        JSONObject directResponseJObj = new JSONObject(directResponse);
        log.info("DirectResponse : " + directResponseJObj.get("meta").toString());

        //Get response using the connector
        log.info("Proxy service url :" + getProxyServiceURL("tumblr"));


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "getBlogLikes.json");

        log.info("Connector response : " + esbRestResponse.getBody().get("meta").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(), directResponseJObj.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(), directResponseJObj.get("response").toString());
    }


    /**
     * Positive integration test case for getAvatar
     *
     * @throws JSONException
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "tumblr {getAvatar} integration positive test")
    public void testTumblrGetAvatar() throws JSONException {

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/avatar";
        log.info("requestUrl : " + requestUrl);

        String directResponse = ConnectorIntegrationUtil.DirectHttpGET(requestUrl);
        log.info("DirectResponse : " + directResponse);


        //Get response using the connector
        log.info("Proxy service url :" + getProxyServiceURL("tumblr"));

        String esbResponse = ConnectorIntegrationUtil.ConnectorHttpPOST("{\"json_request\":{\"operation\":\"getAvatar\",\"baseHostUrl\":\""
                                                                        + targetBlogUrl
                                                                        + "\"}}", getProxyServiceURL("tumblr"));

        log.info("Connector response:" + esbResponse);

        Assert.assertEquals(esbResponse, directResponse);
    }


    /**
     * Positive integration test case for getFollowers
     * Including default values for optional parameters
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "tumblr {getFollowers} integration positive test")
    public void testTumblrGetFollowers() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/followers";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthGET(requestUrl, consumerKey,
                                                                               consumerSecret, accessToken,
                                                                               tokenSecret, params);

        log.info("DirectResponse : " + directResponse.get("meta").toString());
        log.debug("DirectResponse : " + directResponse.get("response").toString());

        //Get response using the connector
        log.info("Proxy service url :" + getProxyServiceURL("tumblr"));


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "getFollowers.json");

        log.info("ESBResponse:" + esbRestResponse.getBody().get("meta").toString());
        log.debug("ESBResponse:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());

    }


    /**
     * Positive integration test case for getPosts
     * Use only mandatory parameters
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "tumblr {getPosts} integration positive test")
    public void testTumblrGetPosts() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/posts?api_key=" + consumerKey;
        log.info("requestUrl : " + requestUrl);

        String directResponse = ConnectorIntegrationUtil.DirectHttpGET(requestUrl);

        //Get response using the connector
        log.info("Proxy service url :" + getProxyServiceURL("tumblr"));

        JSONObject directResponseJObj = new JSONObject(directResponse);

        log.info("ESB:" + directResponseJObj.get("meta").toString());
        log.debug("ESB:" + directResponseJObj.get("response").toString());

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "getPosts.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.debug("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponseJObj.get("meta").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponseJObj.get("response").toString());

    }


    /**
     * Positive integration test case for getQueuedPosts
     * Use only mandatory parameters
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "tumblr {getPosts} integration positive test")
    public void testTumblrGetQueuedPosts() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/posts/queue";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthGET(requestUrl, consumerKey,
                                                                               consumerSecret, accessToken,
                                                                               tokenSecret, params);

        log.info("Direct Response:" + directResponse.get("meta").toString());
        log.debug("Direct Response:" + directResponse.get("response").toString());

        //Get response using the connector
        log.info("Proxy service url :" + getProxyServiceURL("tumblr"));

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "getQueuedPosts.json");

        log.info("Connector Response:" + esbRestResponse.getBody().get("meta").toString());
        log.debug("Connector Response:" + esbRestResponse.getBody().get("response").toString());

        //check response status
        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());
        //response posts are not compared due to tumblr may returned number of posts may vary
    }


    /**
     * Positive integration test case for getDraftedPosts
     * Use only mandatory parameters
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "tumblr {getDraftedPosts} integration positive test")
    public void testTumblrGetDraftedPosts() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/posts/draft";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthGET(requestUrl, consumerKey,
                                                                               consumerSecret, accessToken,
                                                                               tokenSecret, params);

        log.info("DirectResponse : " + directResponse.get("meta").toString());
        log.debug("DirectResponse:" + directResponse.get("response").toString());

        //Get response using the connector
        log.info("Proxy service url :" + getProxyServiceURL("tumblr"));

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap,
                                                                       "getDraftedPosts.json");

        log.info("ESB Response:" + esbRestResponse.getBody().get("meta").toString());
        log.debug("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());

    }

    /**
     * Positive integration test case for getSubmissions
     * Use only mandatory parameters
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "tumblr {getSubmissions} integration positive test")
    public void testTumblrGetSubmissions() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/posts/submission";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();
        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthGET(requestUrl, consumerKey,
                                                                               consumerSecret, accessToken,
                                                                               tokenSecret, params);

        log.info("DirectResponse : " + directResponse.get("meta").toString());
        log.debug("DirectResponse:" + directResponse.get("response").toString());

        //Get response using the connector
        log.info("Proxy service url :" + getProxyServiceURL("tumblr"));

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap,
                                                                       "getSubmissions.json");

        log.info("ESB Response:" + esbRestResponse.getBody().get("meta").toString());
        log.debug("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());

    }


    /**
     * Positive integration test case for createPost (text post)
     * mandatory parameters: type="text", body="This is INTEGRATION TEST text post" + [method of posting]
     * Add default values (according to tumblr api) for some optional parameters: state=publishd, format=html
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "tumblr {createPost} text post integration positive test")
    public void testTumblrCreateTextPost() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/post";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("type", "text");
        params.put("body", "This is INTEGRATION TEST text post : DIRECT TEST CASE");

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthPOST(requestUrl, consumerKey,
                                                                                consumerSecret, accessToken,
                                                                                tokenSecret, params);
        createdTextPostID = directResponse.getJSONObject("response").get("id").toString();

        log.info("direct post ID: " + createdTextPostID);

        log.info("DirectResponse : " + directResponse.get("meta").toString());

        //Get response using the connector
        log.info("Proxy service url :" + getProxyServiceURL("tumblr"));


        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "createTextPost.json");

        //store id for later use in deletePost test
        connecterCreatedTextPostID = esbRestResponse.getBody().getJSONObject("response").get("id").toString();

        log.info("connector post ID: " + connecterCreatedTextPostID);

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.debug("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());

    }


    /**
     * Positive integration test case for editPost (text post)
     * mandatory parameters: id, type="text", body="This is INTEGRATION TEST text post" + [method of posting]
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "tumblr {editPost} text post integration positive test")
    public void testTumblrEditTextPost() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Edit Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/post/edit";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("type", "text");
        params.put("id", createdTextPostID);
        params.put("body", "This is INTEGRATION TEST text post : EDIT DIRECTLY");

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthPOST(requestUrl, consumerKey,
                                                                                consumerSecret, accessToken,
                                                                                tokenSecret, params);
        log.info("DirectResponse : " + directResponse.toString());

        //Get response using the connector
        log.info("Proxy service url :" + getProxyServiceURL("tumblr"));

        String jsonRequestBody = "{\"json_request\":{\"operation\":\"editPost\","
                                 + "\"consumerKey\":\"" + consumerKey + "\","
                                 + "\"consumerSecret\":\"" + consumerSecret + "\","
                                 + "\"accessToken\":\"" + accessToken + "\","
                                 + "\"tokenSecret\":\"" + tokenSecret + "\","
                                 + "\"baseHostUrl\":\"" + targetBlogUrl + "\","
                                 + "\"postId\":\"" + connecterCreatedTextPostID + "\","
                                 + "\"postType\":\"text\","
                                 + "\"postBody\":\"This is INTEGRATION TEST text post : EDITED THROUGH ESB\"}}";

        JSONObject esbRestResponse = ConnectorIntegrationUtil.ConnectorHttpPOSTJsonObj(jsonRequestBody,
                                                                                       getProxyServiceURL("tumblr"));
        log.info("ESB:" + esbRestResponse);

        Assert.assertEquals(directResponse.get("meta").toString(), "{\"status\":200,\"msg\":\"OK\"}");
        Assert.assertEquals(esbRestResponse.get("meta").toString(), "{\"status\":200,\"msg\":\"OK\"}");
    }


    /**
     * Positive integration test case for reblogPost (text post)
     * mandatory parameters: id, type="text", body="This is INTEGRATION TEST text post" + [method of posting]
     * Add default values (according to tumblr api) for some optional parameters: state=publishd, format=html
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 2, groups = {"wso2.esb"}, description = "tumblr {reblogPost} text post integration positive test")
    public void testTumblrReblogTextPost() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        String reblogKey = connectorProperties.getProperty("reblogKey");
        String reblogPostId = connectorProperties.getProperty("reblogPostId");

        //Edit Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/post/reblog";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("type", "text");
        params.put("id", reblogPostId);
        params.put("reblog_key", reblogKey);
        params.put("comment", "This is INTEGRATION TEST text post : REBLOG DIRECTLY");

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthPOST(requestUrl, consumerKey,
                                                                                consumerSecret, accessToken,
                                                                                tokenSecret, params);
        log.info("DirectResponse : " + directResponse.toString());

        //Get response using the connector
        log.info("Proxy service url :" + getProxyServiceURL("tumblr"));

        String jsonRequestBody = "{\"json_request\":{\"operation\":\"reblogPost\","
                                 + "\"consumerKey\":\"" + consumerKey + "\","
                                 + "\"consumerSecret\":\"" + consumerSecret + "\","
                                 + "\"accessToken\":\"" + accessToken + "\","
                                 + "\"tokenSecret\":\"" + tokenSecret + "\","
                                 + "\"baseHostUrl\":\"" + targetBlogUrl + "\","
                                 + "\"postId\":\"" + reblogPostId + "\","
                                 + "\"reblogKey\":\"" + reblogKey + "\","
                                 + "\"postType\":\"text\","
                                 + "\"postComment\":\"This is INTEGRATION TEST text post : REBLOG THROUGH ESB\"}}";

        JSONObject esbRestResponse = ConnectorIntegrationUtil.ConnectorHttpPOSTJsonObj(jsonRequestBody,
                                                                                       getProxyServiceURL("tumblr"));

        log.info("ESB:" + esbRestResponse);

        Assert.assertEquals(directResponse.get("meta").toString(), "{\"status\":201,\"msg\":\"Created\"}");
        Assert.assertEquals(esbRestResponse.get("meta").toString(), "{\"status\":201,\"msg\":\"Created\"}");
    }


    /**
     * Positive integration test case for deletePost (text post)
     * mandatory parameters: post id
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 3, groups = {"wso2.esb"}, description = "tumblr {deletePost} text post integration positive test")
    public void testTumblrDeletePost() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/post/delete";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("id", createdTextPostID);

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthPOST(requestUrl, consumerKey,
                                                                                consumerSecret, accessToken,
                                                                                tokenSecret, params);
        log.info("DirectResponse : " + directResponse.toString());

        //Get response using the connector
        log.info("Proxy service url :" + getProxyServiceURL("tumblr"));

        String esbResponse = ConnectorIntegrationUtil.ConnectorHttpPOST("{\"json_request\":{\"operation\":\"deletePost\",\"consumerKey\":\"" + consumerKey + "\","
                                                                        + "\"consumerSecret\":\"" + consumerSecret + "\","
                                                                        + "\"accessToken\":\"" + accessToken + "\","
                                                                        + "\"tokenSecret\":\"" + tokenSecret + "\","
                                                                        + "\"baseHostUrl\":\"" + targetBlogUrl + "\","
                                                                        + "\"postId\":\"" + connecterCreatedTextPostID + "\"}}",
                                                                        getProxyServiceURL("tumblr"));

        log.info("ESB:" + esbResponse);
        Assert.assertEquals(directResponse.get("meta").toString(), "{\"status\":200,\"msg\":\"OK\"}");
        Assert.assertEquals(esbResponse, "{\"meta\":{\"status\":200,\"msg\":\"OK\"},\"response\":{\"id\":" + connecterCreatedTextPostID + "}}");
    }


    /**
     * Positive integration test case for getUserInfo
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 3, groups = {"wso2.esb"}, description = "tumblr {getUserInfo} integration positive test")
    public void testTumblrGetUserInfo() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = "http://api.tumblr.com/v2/user/info";
        log.info("requestUrl : " + requestUrl);

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthGET(requestUrl, consumerKey,
                                                                               consumerSecret, accessToken,
                                                                               tokenSecret, null);
        log.info("DirectResponse : " + directResponse.toString());

        //Get response using the connector
        log.info("Proxy service url :" + getProxyServiceURL("tumblr"));

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "getUserInfo.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.info("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());
    }

    /**
     * Positive integration test case for getUserDashboard
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 3, groups = {"wso2.esb"}, description = "tumblr {getUserDashboard} integration positive test")
    public void testTumblrGetUserDashboard() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = "http://api.tumblr.com/v2/user/dashboard";
        log.info("requestUrl : " + requestUrl);

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthGET(requestUrl, consumerKey,
                                                                               consumerSecret, accessToken,
                                                                               tokenSecret, null);

        log.info("DirectResponse : " + directResponse.get("meta").toString());

        //Get response using the connector
        log.info("Proxy service url :" + getProxyServiceURL("tumblr"));

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "getUserDashboard.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());

        //check response status
        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());
    }


    /**
     * Positive integration test case for getLikes
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, groups = {"wso2.esb"}, description = "tumblr {getLikes} integration positive test")
    public void testTumblrGetLikes() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = "http://api.tumblr.com/v2/user/likes";
        log.info("requestUrl : " + requestUrl);

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthGET(requestUrl, consumerKey,
                                                                               consumerSecret, accessToken,
                                                                               tokenSecret, null);
        log.info("DirectResponse : " + directResponse.get("meta").toString());
        log.debug("DirectResponse : " + directResponse.get("response").toString());

        //Get response using the connector
        log.info("Proxy service url :" + getProxyServiceURL("tumblr"));

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "getLikes.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.debug("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());
    }


    /**
     * Positive integration test case for getFollowing
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 2, groups = {"wso2.esb"}, description = "tumblr {getFollowing} integration positive test")
    public void testTumblrGetFollowing() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = "http://api.tumblr.com/v2/user/following";
        log.info("requestUrl : " + requestUrl);

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthGET(requestUrl, consumerKey,
                                                                               consumerSecret, accessToken,
                                                                               tokenSecret, null);
        log.info("DirectResponse : " + directResponse.get("meta").toString());
        log.debug("DirectResponse : " + directResponse.get("response").toString());

        //Get response using the connector
        log.info("Proxy service url :" + getProxyServiceURL("tumblr"));

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "getFollowing.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.debug("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());
    }


    /**
     * Positive integration test case for follow
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 3, groups = {"wso2.esb"}, description = "tumblr {follow} integration positive test")
    public void testTumblrFollow() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        String followBlogUrl = connectorProperties.getProperty("followBlogUrl");

        //Get Direct response from tumblr        
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("url", followBlogUrl);

        String requestUrl = "http://api.tumblr.com/v2/user/follow";
        log.info("requestUrl : " + requestUrl);

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthPOST(requestUrl, consumerKey,
                                                                                consumerSecret, accessToken,
                                                                                tokenSecret, params);
        log.info("DirectResponse : " + directResponse.toString());

        //Get response using the connector
        log.info("Proxy service url :" + getProxyServiceURL("tumblr"));

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "follow.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.info("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());
    }


    /**
     * Positive integration test case for unfollow
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 3, groups = {"wso2.esb"}, description = "tumblr {unfollow} integration positive test")
    public void testTumblrUnFollow() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        String followBlogUrl = connectorProperties.getProperty("followBlogUrl");

        //Get Direct response from tumblr        
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("url", followBlogUrl);

        String requestUrl = "http://api.tumblr.com/v2/user/unfollow";
        log.info("requestUrl : " + requestUrl);

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthPOST(requestUrl, consumerKey,
                                                                                consumerSecret, accessToken,
                                                                                tokenSecret, params);
        log.info("DirectResponse : " + directResponse.toString());

        //Get response using the connector
        log.info("Proxy service url :" + getProxyServiceURL("tumblr"));

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "unFollow.json");
        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.info("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());
    }


    /**
     * Positive integration test case for like
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 3, groups = {"wso2.esb"}, description = "tumblr {like} integration positive test")
    public void testTumblrLike() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        String directReblogKey = connectorProperties.getProperty("directLikeReblogKey");
        String directPostId = connectorProperties.getProperty("directLikePostId");

        //Get Direct response from tumblr        
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("id", directPostId);
        params.put("reblog_key", directReblogKey);

        String requestUrl = "http://api.tumblr.com/v2/user/like";
        log.info("requestUrl : " + requestUrl);

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthPOST(requestUrl, consumerKey,
                                                                                consumerSecret, accessToken,
                                                                                tokenSecret, params);

        log.info("DirectResponse : " + directResponse.toString());

        //Get response using the connector
        log.info("Proxy service url :" + getProxyServiceURL("tumblr"));

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "like.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.info("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());
    }


    /**
     * Positive integration test case for unLike
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 3, groups = {"wso2.esb"}, description = "tumblr {like} integration positive test")
    public void testTumblrUnLike() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        String reblogKey = connectorProperties.getProperty("directLikeReblogKey");
        String postId = connectorProperties.getProperty("directLikePostId");

        //Get Direct response from tumblr        
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("id", postId);
        params.put("reblog_key", reblogKey);

        String requestUrl = "http://api.tumblr.com/v2/user/unlike";
        log.info("requestUrl : " + requestUrl);

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthPOST(requestUrl, consumerKey,
                                                                                consumerSecret, accessToken,
                                                                                tokenSecret, params);
        log.info("DirectResponse : " + directResponse.toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "unLike.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.info("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());
    }


    /**
     * Positive integration test case for getTaggedPosts
     * Use only mandatory parameters
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 3, groups = {"wso2.esb"}, description = "tumblr {getTaggedPosts} integration positive test")
    public void testTumblrGetTaggedPosts() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);

        String tag = connectorProperties.getProperty("postTag");

        //Get Direct response from tumblr
        String requestUrl = "http://api.tumblr.com/v2/tagged?api_key=" + consumerKey + "&tag=" + tag;
        log.info("requestUrl : " + requestUrl);

        String directResponse = ConnectorIntegrationUtil.DirectHttpGET(requestUrl);

        JSONObject directResponseJObj = new JSONObject(directResponse);
        log.info("ESB:" + directResponseJObj.get("meta").toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "getTaggedPosts.json");
        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        //response not compared since tumblr may return different tagged post
        log.debug("ESB:" + esbRestResponse.getBody().get("response").toString());
        //check response status
        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponseJObj.get("meta").toString());

    }


    /*************************************************************************************************
     *
     * 										NEGATIVE TEST CASES
     *
     * ************************************************************************************************/


    /**
     * negative test case for getBlogInfo
     * With empty target url
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {getBlogInfo} integration negative test")
    public void NegativeTestTumblrGetBlogInfo() throws JSONException, IOException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);

        requestUrl = requestUrl + "//info?api_key=" + consumerKey;
        log.info("requestUrl : " + requestUrl);

        String directResponse = ConnectorIntegrationUtil.DirectHttpGET(requestUrl);

        JSONObject directResponseJObj = new JSONObject(directResponse);

        log.info("Direct Response : " + directResponseJObj.get("meta").toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "negative/getBlogInfo.json");

        log.info("Connector response : " + esbRestResponse.getBody().get("meta").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponseJObj.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponseJObj.get("response").toString());
    }


    /**
     * Negative integration test case for getBlogLikes
     * wrong consumer key
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {getBlogLikes} integration negative test")
    public void NegativeTestTumblrGetBlogLikes() throws JSONException, IOException {

        String consumerKey = "wrong" + connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/likes?api_key=" + consumerKey;
        log.info("requestUrl : " + requestUrl);

        String directResponse = ConnectorIntegrationUtil.DirectHttpGET(requestUrl);

        JSONObject directResponseJObj = new JSONObject(directResponse);
        log.info("DirectResponse : " + directResponseJObj.get("meta").toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "negative/getBlogLikes.json");

        //String connectorResponse = ConnectorIntegrationUtil.ConnectorHttpPOST("", getProxyServiceURL("tumblr"));
        log.info("Connector response : " + esbRestResponse.getBody().get("meta").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(), directResponseJObj.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(), directResponseJObj.get("response").toString());
    }


    /**
     * Negative integration test case for getAvatar
     * Empty blogHostUrl
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {getAvatar} integration negative test")
    public void NegativeTestTumblrGetAvatar() throws JSONException, IOException {

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = "";

        requestUrl = requestUrl + "/" + targetBlogUrl + "/avatar";
        log.info("requestUrl : " + requestUrl);

        String directResponse = ConnectorIntegrationUtil.DirectHttpGET(requestUrl);
        log.info("DirectResponse : " + directResponse);

        //Get response using the connector
        String esbResponse = ConnectorIntegrationUtil.ConnectorHttpPOST("{\"json_request\":{\"operation\":\"getAvatar\",\"baseHostUrl\":\""
                                                                        + targetBlogUrl
                                                                        + "\"}}", getProxyServiceURL("tumblr"));
        log.info("Connector response:" + esbResponse);

        Assert.assertEquals(esbResponse, directResponse);
    }


    /**
     * Negative integration test case for getFollowers
     * With altered access token
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {getFollowers} integration Negative test")
    public void NegativeTestTumblrGetFollowers() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = "wrong" + connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/followers";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthGET(requestUrl, consumerKey,
                                                                               consumerSecret, accessToken,
                                                                               tokenSecret, params);

        log.info("DirectResponse : " + directResponse.get("meta").toString());
        log.debug("DirectResponse : " + directResponse.get("response").toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap,
                                                                       "negative/getFollowers.json");

        log.info("ESBResponse:" + esbRestResponse.getBody().get("meta").toString());
        log.debug("ESBResponse:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());

    }


    /**
     * Negative integration test case for getPosts
     * requesting post with post id does not exists
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {getPosts} integration negative test")
    public void NegativeTestTumblrGetPosts() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);


        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);
        String wrongPostId = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_WRONG_POSTID);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/posts?api_key=" + consumerKey
                     + "&id=" + wrongPostId;
        log.info("requestUrl : " + requestUrl);

        String directResponse = ConnectorIntegrationUtil.DirectHttpGET(requestUrl);

        //Get response using the connector
        JSONObject directResponseJObj = new JSONObject(directResponse);

        log.info("ESB:" + directResponseJObj.get("meta").toString());
        log.debug("ESB:" + directResponseJObj.get("response").toString());

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "negative/getPosts.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.debug("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponseJObj.get("meta").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponseJObj.get("response").toString());

    }


    /**
     * Negative integration test case for getQueuedPosts
     * Use only mandatory parameters
     * access by wrong access token
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {getPosts} integration negative test")
    public void NegativeTestTumblrGetQueuedPosts() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = "wrong" + connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/posts/queue";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthGET(requestUrl, consumerKey,
                                                                               consumerSecret, accessToken,
                                                                               tokenSecret, params);

        log.info("Direct Response:" + directResponse.get("meta").toString());
        log.debug("Direct Response:" + directResponse.get("response").toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap,
                                                                       "negative/getQueuedPosts.json");

        log.info("Connector Response:" + esbRestResponse.getBody().get("meta").toString());
        log.debug("Connector Response:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());

    }


    /**
     * Negative integration test case for getDraftedPosts
     * Use only mandatory parameters
     * Using unauthorized access token
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {getDraftedPosts} integration negative test")
    public void NegativeTestTumblrGetDraftedPosts() throws IOException, JSONException {


        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = "wrong" + connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/posts/draft";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthGET(requestUrl, consumerKey,
                                                                               consumerSecret, accessToken,
                                                                               tokenSecret, params);

        log.info("DirectResponse : " + directResponse.get("meta").toString());
        log.debug("DirectResponse:" + directResponse.get("response").toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap,
                                                                       "negative/getDraftedPosts.json");

        log.info("ESB Response:" + esbRestResponse.getBody().get("meta").toString());
        log.debug("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());

    }


    /**
     * Negative integration test case for createPost (text post)
     * mandatory parameters: type="", body=""
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {createPost} text post integration negative test")
    public void NegativeTestTumblrCreateTextPost() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/post";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("type", "");
        params.put("body", "");

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthPOST(requestUrl, consumerKey,
                                                                                consumerSecret, accessToken,
                                                                                tokenSecret, params);

        log.info("DirectResponse:" + directResponse.get("meta").toString());
        log.info("DirectResponse:" + directResponse.get("response").toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap,
                                                                       "negative/createTextPost.json");

        log.info("ConnectorResponse:" + esbRestResponse.getBody().get("meta").toString());
        log.info("ConnectorResponse:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());
    }


    /**
     * Negative integration test case for editPost (text post)
     * mandatory parameters: id, type="text", body="This is INTEGRATION TEST text post" + [method of posting]
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {editPost} text post integration positive test")
    public void NegativeTestTumblrEditTextPost() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        String wrongPostId = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_WRONG_POSTID);

        //Edit Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/post/edit";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("type", "text");
        params.put("id", wrongPostId);
        params.put("body", "This is INTEGRATION TEST text post : EDIT DIRECTLY");

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthPOST(requestUrl, consumerKey,
                                                                                consumerSecret, accessToken,
                                                                                tokenSecret, params);

        log.info("DirectResponse : " + directResponse.toString());

        //Get response using the connector
        String jsonRequestBody = "{\"json_request\":{\"operation\":\"editPost\","
                                 + "\"consumerKey\":\"" + consumerKey + "\","
                                 + "\"consumerSecret\":\"" + consumerSecret + "\","
                                 + "\"accessToken\":\"" + accessToken + "\","
                                 + "\"tokenSecret\":\"" + tokenSecret + "\","
                                 + "\"baseHostUrl\":\"" + targetBlogUrl + "\","
                                 + "\"postId\":\"" + wrongPostId + "\","
                                 + "\"postType\":\"text\","
                                 + "\"postBody\":\"This is INTEGRATION TEST text post : EDITED THROUGH ESB\"}}";

        JSONObject esbRestResponse = ConnectorIntegrationUtil.ConnectorHttpPOSTJsonObj(jsonRequestBody,
                                                                                       getProxyServiceURL("tumblr"));

        log.info("ESB:" + esbRestResponse);

        Assert.assertEquals(esbRestResponse.get("meta").toString(), directResponse.get("meta").toString());
        Assert.assertEquals(esbRestResponse.get("response").toString(), directResponse.get("response").toString());
    }


    /**
     * Negative integration test case for reblogPost (text post)
     * reblog with wrong reblog key
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {reblogPost} text post integration negative test")
    public void NegativeTestTumblrReblogTextPost() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        String reblogKey = "wrong" + connectorProperties.getProperty("reblogKey");
        String reblogPostId = connectorProperties.getProperty("reblogPostId");

        //Edit Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/post/reblog";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("type", "text");
        params.put("id", reblogPostId);
        params.put("reblog_key", reblogKey);
        params.put("comment", "This is INTEGRATION TEST text post : REBLOG DIRECTLY");


        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthPOST(requestUrl, consumerKey,
                                                                                consumerSecret, accessToken,
                                                                                tokenSecret, params);
        log.info("DirectResponse : " + directResponse.toString());

        //Get response using the connector
        String jsonRequestBody = "{\"json_request\":{\"operation\":\"reblogPost\","
                                 + "\"consumerKey\":\"" + consumerKey + "\","
                                 + "\"consumerSecret\":\"" + consumerSecret + "\","
                                 + "\"accessToken\":\"" + accessToken + "\","
                                 + "\"tokenSecret\":\"" + tokenSecret + "\","
                                 + "\"baseHostUrl\":\"" + targetBlogUrl + "\","
                                 + "\"postId\":\"" + reblogPostId + "\","
                                 + "\"reblogKey\":\"" + reblogKey + "\","
                                 + "\"postType\":\"text\","
                                 + "\"postComment\":\"This is INTEGRATION TEST text post : REBLOG THROUGH ESB\"}}";

        JSONObject esbRestResponse = ConnectorIntegrationUtil.ConnectorHttpPOSTJsonObj(jsonRequestBody,
                                                                                       getProxyServiceURL("tumblr"));

        log.info("ESB:" + esbRestResponse);

        Assert.assertEquals(esbRestResponse.get("meta").toString(), directResponse.get("meta").toString());
        Assert.assertEquals(esbRestResponse.get("response").toString(), directResponse.get("response").toString());
    }


    /**
     * Positive integration test case for deletePost (text post)
     * mandatory parameters: post id
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {deletePost} text post integration positive test")
    public void NegativeTestTumblrDeletePost() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/post/delete";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("id", createdTextPostID);

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthPOST(requestUrl, consumerKey,
                                                                                consumerSecret, accessToken,
                                                                                tokenSecret, params);
        log.info("DirectResponse : " + directResponse.toString());

        //Get response using the connector
         String esbResponse = ConnectorIntegrationUtil.ConnectorHttpPOST("{\"json_request\":{\"operation\":\"deletePost\",\"consumerKey\":\"" + consumerKey + "\","
                                                                        + "\"consumerSecret\":\"" + consumerSecret + "\","
                                                                        + "\"accessToken\":\"" + accessToken + "\","
                                                                        + "\"tokenSecret\":\"" + tokenSecret + "\","
                                                                        + "\"baseHostUrl\":\"" + targetBlogUrl + "\","
                                                                        + "\"postId\":\"" + connecterCreatedTextPostID + "\"}}",
                                                                        getProxyServiceURL("tumblr"));

        log.info("ESB:" + esbResponse);

        JSONObject esbResponseObj = new JSONObject(esbResponse);

        Assert.assertEquals(esbResponseObj.get("meta").toString(), directResponse.get("meta").toString());
        Assert.assertEquals(esbResponseObj.get("response").toString(), directResponse.get("response").toString());
    }


    /**
     * Negative integration test case for getUserInfo
     * send request with wrong access token
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {getUserInfo} integration negative test")
    public void NegativeTestTumblrGetUserInfo() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = "wrong" + connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = "http://api.tumblr.com/v2/user/info";
        log.info("requestUrl : " + requestUrl);

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthGET(requestUrl, consumerKey,
                                                                               consumerSecret, accessToken,
                                                                               tokenSecret, null);

        log.info("DirectResponse : " + directResponse.toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "negative/getUserInfo.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.info("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());
    }


    /**
     * Negative integration test case for getUserDashboard
     * send request with wrong access token
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {getUserDashboard} integration negative test")
    public void NegativeTestTumblrGetUserDashboard() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = "wrong" + connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = "http://api.tumblr.com/v2/user/dashboard";
        log.info("requestUrl : " + requestUrl);

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthGET(requestUrl, consumerKey,
                                                                               consumerSecret, accessToken,
                                                                               tokenSecret, null);

        log.info("DirectResponse : " + directResponse.get("meta").toString());
        log.info("DirectResponse : " + directResponse.get("response").toString());
        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "negative/getUserDashboard.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.info("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());
    }


    /**
     * Negative integration test case for getLikes
     * using wrong access token
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {getLikes} integration negative test")
    public void negativeTestTumblrGetLikes() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = "wrong" + connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = "http://api.tumblr.com/v2/user/likes";
        log.info("requestUrl : " + requestUrl);

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthGET(requestUrl, consumerKey,
                                                                               consumerSecret, accessToken,
                                                                               tokenSecret, null);

        log.info("DirectResponse : " + directResponse.get("meta").toString());
        log.info("DirectResponse : " + directResponse.get("response").toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "negative/getLikes.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.info("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());
    }


    /**
     * Negative integration test case for getFollowing
     * using wrong access token
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {getFollowing} integration negative test")
    public void NegativeTestTumblrGetFollowing() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = "wrong" + connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = "http://api.tumblr.com/v2/user/following";
        log.info("requestUrl : " + requestUrl);

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthGET(requestUrl, consumerKey,
                                                                               consumerSecret, accessToken,
                                                                               tokenSecret, null);

        log.info("DirectResponse : " + directResponse.get("meta").toString());
        log.info("DirectResponse : " + directResponse.get("response").toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "negative/getFollowing.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.info("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());
    }


    /**
     * Negative integration test case for follow
     * send request to follow blog which does not exist
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {follow} integration negative test")
    public void NegativeTestTumblrFollow() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        String followBlogUrl = "nonexistingblog";

        //Get Direct response from tumblr        
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("url", followBlogUrl);

        String requestUrl = "http://api.tumblr.com/v2/user/follow";
        log.info("requestUrl : " + requestUrl);

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthPOST(requestUrl, consumerKey,
                                                                                consumerSecret, accessToken,
                                                                                tokenSecret, params);

        log.info("DirectResponse : " + directResponse.toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "negative/follow.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.info("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());
    }


    /**
     * Negative integration test case for unfollow
     * send request to follow blog which does not exist
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {unfollow} integration negative test")
    public void NegativeTestTumblrUnFollow() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        String followBlogUrl = "nonexistingblog";

        //Get Direct response from tumblr        
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("url", followBlogUrl);

        String requestUrl = "http://api.tumblr.com/v2/user/unfollow";
        log.info("requestUrl : " + requestUrl);

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthPOST(requestUrl, consumerKey,
                                                                                consumerSecret, accessToken,
                                                                                tokenSecret, params);

        log.info("DirectResponse : " + directResponse.toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "negative/unFollow.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.info("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());
    }


    /**
     * Neagtive integration test case for like
     * send request with wrong post id
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {like} integration negative test")
    public void NegativeTestTumblrLike() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        String directReblogKey = connectorProperties.getProperty("directLikeReblogKey");
        String directPostId = connectorProperties.getProperty("wrongPostId");

        //Get Direct response from tumblr        
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("id", directPostId);
        params.put("reblog_key", directReblogKey);

        String requestUrl = "http://api.tumblr.com/v2/user/like";
        log.info("requestUrl : " + requestUrl);

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthPOST(requestUrl, consumerKey,
                                                                                consumerSecret, accessToken,
                                                                                tokenSecret, params);

        log.info("DirectResponse : " + directResponse.toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "negative/like.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.info("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());
    }


    /**
     * Negative integration test case for unLike
     * Try to unlike already unliked post
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {like} integration negative test")
    public void NegativeTestTumblrUnLike() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        String reblogKey = connectorProperties.getProperty("directLikeReblogKey");
        String postId = connectorProperties.getProperty("directLikePostId");

        //Get Direct response from tumblr        
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("id", postId);
        params.put("reblog_key", reblogKey);

        String requestUrl = "http://api.tumblr.com/v2/user/unlike";
        log.info("requestUrl : " + requestUrl);

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthPOST(requestUrl, consumerKey,
                                                                                consumerSecret, accessToken,
                                                                                tokenSecret, params);

        log.info("DirectResponse : " + directResponse.toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "negative/unLike.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.info("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());
        /*Assert.assertEquals(esbRestResponse.getBody().get("response").toString(), 
                                                                directResponse.get("response").toString());*/
    }


    /**
     * Positive integration test case for getTaggedPosts
     * Use only mandatory parameters
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {getTaggedPosts} integration positive test")
    public void NegativeTestTumblrGetTaggedPosts() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);

        //Get Direct response from tumblr
        String requestUrl = "http://api.tumblr.com/v2/tagged?api_key=" + consumerKey;
        log.info("requestUrl : " + requestUrl);

        String directResponse = ConnectorIntegrationUtil.DirectHttpGET(requestUrl);

        JSONObject directResponseJObj = new JSONObject(directResponse);
        log.info("ESB:" + directResponseJObj.get("meta").toString());


        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "negative/getTaggedPosts.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.debug("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponseJObj.get("meta").toString());
        //response not compared since tumblr may return different tagged post

    }


    /*************************************************************************************************
     *
     * 								POSITIVE TEST CASES WITH OPTIONAL
     *
     * ************************************************************************************************/


    /**
     * Positive integration test case for getAvatar WITH optional parameters
     * Requesting image with size 512
     *
     * @throws JSONException
     */
    @Test(priority = 4, enabled = false,groups = {"wso2.esb"}, description = "tumblr {getAvatar} integration optional positive test")
    public void optionalTestTumblrGetAvatar() throws JSONException {

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/avatar/512";
        log.info("requestUrl : " + requestUrl);

        String directResponse = ConnectorIntegrationUtil.DirectHttpGET(requestUrl);
        log.info("DirectResponse : " + directResponse);


        //Get response using the connector
        String esbResponse = ConnectorIntegrationUtil.ConnectorHttpPOST("{\"json_request\":{\"operation\":\"getAvatar\","
                                                                        + "\"baseHostUrl\":\"" + targetBlogUrl + "\","
                                                                        + "\"size\":\"512\"" + "}}", getProxyServiceURL("tumblr"));
        log.info("Connector response:" + esbResponse);

        Assert.assertEquals(esbResponse, directResponse);
    }


    /**
     * Positive integration test case for getBlogLikes WITH optional parameters
     * with optional parameters limit=2 offset=3
     * Including default values for optional parameters
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 4, groups = {"wso2.esb"}, description = "tumblr {getBlogLikes} integration positive test")
    public void optionalTestTumblrGetBlogLikes() throws JSONException, IOException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/likes?api_key=" + consumerKey + "&limit=1&offset=3";
        log.info("requestUrl : " + requestUrl);

        String directResponse = ConnectorIntegrationUtil.DirectHttpGET(requestUrl);


        JSONObject directResponseJObj = new JSONObject(directResponse);
        log.info("DirectResponse : " + directResponseJObj.get("meta").toString());
        log.debug("DirectResponse : " + directResponseJObj.get("response").toString());
        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "optional/getBlogLikes.json");

        //String connectorResponse = ConnectorIntegrationUtil.ConnectorHttpPOST("", getProxyServiceURL("tumblr"));
        log.info("Connector response : " + esbRestResponse.getBody().get("meta").toString());
        log.debug("Connector response : " + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(), directResponseJObj.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(), directResponseJObj.get("response").toString());
    }


    /**
     * Positive integration test case for getFollowers WITH optional parameters
     * Including default values for optional parameters
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 4, groups = {"wso2.esb"}, description = "tumblr {getFollowers} integration optional positive test")
    public void optionalTestTumblrGetFollowers() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/followers";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("offset", "1");
        params.put("limit", "2");

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthGET(requestUrl, consumerKey,
                                                                               consumerSecret, accessToken,
                                                                               tokenSecret, params);

        log.info("DirectResponse : " + directResponse.get("meta").toString());
        log.debug("DirectResponse : " + directResponse.get("response").toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "optional/getFollowers.json");
        log.info("ESBResponse:" + esbRestResponse.getBody().get("meta").toString());
        log.debug("ESBResponse:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());

    }


    /**
     * Positive integration test case for getPosts WITH optional parameters
     * limit=2, offset=2, reblog_info=true, notes_info=true, filter=text
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 4, groups = {"wso2.esb"}, description = "tumblr {getPosts} integration optional positive test")
    public void optionalTestTumblrGetPosts() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);


        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);
        String tag = connectorProperties.getProperty("postTag");

        requestUrl = requestUrl + "/" + targetBlogUrl + "/posts?api_key=" + consumerKey
                     + "&tag=" + tag
                     + "&limit=2&offset=2"
                     + "&reblog_info=true&notes_info=true&filter=text";
        log.info("requestUrl : " + requestUrl);


        String directResponse = ConnectorIntegrationUtil.DirectHttpGET(requestUrl);

        //log.info("DirectResponse : " +directResponse);

        //Get response using the connector
        JSONObject directResponseJObj = new JSONObject(directResponse);

        log.info("ESB:" + directResponseJObj.get("meta").toString());
        log.debug("ESB:" + directResponseJObj.get("response").toString());

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "optional/getPosts.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.debug("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponseJObj.get("meta").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponseJObj.get("response").toString());

    }


    /**
     * Positive integration test case for getQueuedPosts WITH optional parameters
     * optional values offset=2, limit=2, filter=text
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 4, groups = {"wso2.esb"}, description = "tumblr {getPosts} integration positive test")
    public void optionalTestTumblrGetQueuedPosts() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/posts/queue";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("offset", "2");
        params.put("limit", "2");
        params.put("filter", "text");

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthGET(requestUrl, consumerKey,
                                                                               consumerSecret, accessToken,
                                                                               tokenSecret, params);

        log.info("Direct Response:" + directResponse.get("meta").toString());
        log.debug("Direct Response:" + directResponse.get("response").toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "optional/getQueuedPosts.json");

        log.info("Connector Response:" + esbRestResponse.getBody().get("meta").toString());
        log.debug("Connector Response:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());
        //response posts are not compared due to tumblr may returned number of posts may vary

    }


    /**
     * Positive integration test case for getDraftedPosts WITH optional parameters
     * before_id=0, filter=text
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {getDraftedPosts} integration positive test")
    public void optionalTestTumblrGetDraftedPosts() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/posts/draft";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("before_id", "0");
        params.put("filter", "text");

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthGET(requestUrl, consumerKey,
                                                                               consumerSecret, accessToken,
                                                                               tokenSecret, params);

        log.info("DirectResponse : " + directResponse.get("meta").toString());
        log.debug("DirectResponse:" + directResponse.get("response").toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap,
                                                                       "optional/getDraftedPosts.json");

        log.info("ESB Response:" + esbRestResponse.getBody().get("meta").toString());
        log.debug("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());

    }


    /**
     * Positive integration test case for getSubmissions WITH optional parameters
     * Use only mandatory parameters
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {getSubmissions} integration positive test")
    public void optionalTestTumblrGetSubmissions() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/posts/submission";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("offset", "1");
        params.put("filter", "text");

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthGET(requestUrl, consumerKey,
                                                                               consumerSecret, accessToken,
                                                                               tokenSecret, params);

        log.info("DirectResponse : " + directResponse.get("meta").toString());
        log.info("DirectResponse:" + directResponse.get("response").toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap,
                                                                       "optional/getSubmissions.json");

        log.info("ESB Response:" + esbRestResponse.getBody().get("meta").toString());
        log.info("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());

    }


    /**
     * Positive integration test case for createPost (text post) WITH optional parameters
     * mandatory parameters: type="text", body="This is INTEGRATION TEST text post" + [method of posting]
     * Add default values (according to tumblr api) for some optional parameters: state=publishd, format=html
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 4, groups = {"wso2.esb"}, description = "tumblr {createPost} text post integration optional positive test")
    public void optionalTestTumblrCreateTextPost() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/post";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("state", "queue");
        params.put("tags", "optionalTest");
        params.put("tweet", "optional tweet");
        params.put("format", "html");
        params.put("slug", "optional direct slug");

        params.put("type", "text");
        params.put("body", "This is INTEGRATION TEST text post : DIRECT OPTIONAL TEST CASE");


        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthPOST(requestUrl, consumerKey,
                                                                                consumerSecret, accessToken,
                                                                                tokenSecret, params);
        createdOptionalTextPostID = directResponse.getJSONObject("response").get("id").toString();


        log.info("direct post ID: " + createdTextPostID);

        log.info("DirectResponse : " + directResponse.get("meta").toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "optional/createTextPost.json");

        //store id for later use in deletePost test
        connecterCreatedOptionalTextPostID = esbRestResponse.getBody().getJSONObject("response").get("id").toString();

        log.info("connector post ID: " + connecterCreatedTextPostID);

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.debug("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());

    }


    /**
     * Positive integration test case for editPost (text post) WITH optional parameters
     * mandatory parameters: id, type="text", body="This is INTEGRATION TEST text post" + [method of posting]
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "tumblr {editPost} text post integration positive test")
    public void optionalTestTumblrEditTextPost() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Edit Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/post/edit";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("type", "text");
        params.put("id", createdOptionalTextPostID);
        params.put("state", "queue");
        params.put("tags", "optionalTestEdit");
        params.put("tweet", "optional tweet edit");
        params.put("format", "html");
        params.put("slug", "optional direct slug edit");

        params.put("body", "This is INTEGRATION TEST text post : OPTIONAL EDIT DIRECTLY");


        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthPOST(requestUrl, consumerKey,
                                                                                consumerSecret, accessToken,
                                                                                tokenSecret, params);

        log.info("DirectResponse : " + directResponse.toString());

        //Get response using the connector
        String jsonRequestBody = "{\"json_request\":{\"operation\":\"editPost\","
                                 + "\"consumerKey\":\"" + consumerKey + "\","
                                 + "\"consumerSecret\":\"" + consumerSecret + "\","
                                 + "\"accessToken\":\"" + accessToken + "\","
                                 + "\"tokenSecret\":\"" + tokenSecret + "\","
                                 + "\"baseHostUrl\":\"" + targetBlogUrl + "\","
                                 + "\"postId\":\"" + connecterCreatedOptionalTextPostID + "\","
                                 + "\"postType\":\"text\","
                                 + "\"state\":\"queue\","
                                 + "\"postTag\":\"optionalTestESBEdit\","
                                 + "\"tweet\":\"optional tweet\","
                                 + "\"format\":\"html\","
                                 + "\"slug\":\"optional direct slug\","
                                 + "\"postBody\":\"This is INTEGRATION TEST text post : OPTIONAL EDITED THROUGH ESB\"}}";

        JSONObject esbRestResponse = ConnectorIntegrationUtil.ConnectorHttpPOSTJsonObj(jsonRequestBody,
                                                                                       getProxyServiceURL("tumblr"));

        log.info("ESB:" + esbRestResponse);

        Assert.assertEquals(directResponse.get("meta").toString(), "{\"status\":200,\"msg\":\"OK\"}");
        Assert.assertEquals(esbRestResponse.get("meta").toString(), "{\"status\":200,\"msg\":\"OK\"}");
    }


    /**
     * Positive integration test case for reblogPost (text post) WITH optional parameters
     * mandatory parameters: id, type="text", body="This is INTEGRATION TEST text post" + [method of posting]
     * Add default values (according to tumblr api) for some optional parameters: state=publishd, format=html
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 4, groups = {"wso2.esb"}, description = "tumblr {reblogPost} text post integration positive test")
    public void optionalTestTumblrReblogTextPost() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        String reblogKey = connectorProperties.getProperty("reblogKey");
        String reblogPostId = connectorProperties.getProperty("reblogPostId");

        //Edit Direct response from tumblr
        String requestUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BASEAPIURL);
        String targetBlogUrl = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_BLOGURL);

        requestUrl = requestUrl + "/" + targetBlogUrl + "/post/reblog";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("type", "text");
        params.put("id", reblogPostId);
        params.put("reblog_key", reblogKey);
        params.put("state", "queue");
        params.put("tags", "optionalTestReblog");
        params.put("tweet", "optional tweet edit");
        params.put("format", "html");
        params.put("slug", "optional direct slug reblog");
        params.put("comment", "This is INTEGRATION TEST text post : OPTIONAL REBLOG DIRECTLY");


        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthPOST(requestUrl, consumerKey,
                                                                                consumerSecret, accessToken,
                                                                                tokenSecret, params);

        log.info("DirectResponse : " + directResponse.toString());

        //Get response using the connector
        String jsonRequestBody = "{\"json_request\":{\"operation\":\"reblogPost\","
                                 + "\"consumerKey\":\"" + consumerKey + "\","
                                 + "\"consumerSecret\":\"" + consumerSecret + "\","
                                 + "\"accessToken\":\"" + accessToken + "\","
                                 + "\"tokenSecret\":\"" + tokenSecret + "\","
                                 + "\"baseHostUrl\":\"" + targetBlogUrl + "\","
                                 + "\"postId\":\"" + reblogPostId + "\","
                                 + "\"reblogKey\":\"" + reblogKey + "\","
                                 + "\"postType\":\"text\","
                                 + "\"state\":\"queue\","
                                 + "\"postTag\":\"optionalTestESBReblog\","
                                 + "\"tweet\":\"optional reblog tweet\","
                                 + "\"format\":\"html\","
                                 + "\"slug\":\"optional direct reblog slug\","
                                 + "\"postComment\":\"This is INTEGRATION TEST text post : OPTIONAL REBLOG THROUGH ESB\"}}";

        JSONObject esbRestResponse = ConnectorIntegrationUtil.ConnectorHttpPOSTJsonObj(jsonRequestBody,
                                                                                       getProxyServiceURL("tumblr"));

        log.info("ESB:" + esbRestResponse);

        Assert.assertEquals(directResponse.get("meta").toString(), "{\"status\":201,\"msg\":\"Created\"}");
        Assert.assertEquals(esbRestResponse.get("meta").toString(), "{\"status\":201,\"msg\":\"Created\"}");
    }


    /**
     * Positive integration test case for getUserDashboard WITH optional parameters
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 4, groups = {"wso2.esb"}, description = "tumblr {getUserDashboard} integration positive test")
    public void optionalTestTumblrGetUserDashboard() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = "http://api.tumblr.com/v2/user/dashboard";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("limit", "2");
        params.put("offset", "2");
        params.put("type", "photo");
        params.put("since_id", "0");
        params.put("reblog_info", "true");
        params.put("notes_info", "true");


        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthGET(requestUrl, consumerKey,
                                                                               consumerSecret, accessToken,
                                                                               tokenSecret, params);

        log.info("DirectResponse : " + directResponse.get("meta").toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "optional/getUserDashboard.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());
    }


    /**
     * Positive integration test case for getLikes WITH optional parameters
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 4, groups = {"wso2.esb"}, description = "tumblr {getLikes} integration positive test")
    public void optionalTestTumblrGetLikes() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = "http://api.tumblr.com/v2/user/likes";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("offset", "2");
        params.put("limit", "2");

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthGET(requestUrl, consumerKey,
                                                                               consumerSecret, accessToken,
                                                                               tokenSecret, params);

        log.info("DirectResponse : " + directResponse.get("meta").toString());
        log.debug("DirectResponse : " + directResponse.get("response").toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "optional/getLikes.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.debug("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());
    }


    /**
     * Positive integration test case for getFollowing WITH optional parameters
     * OAuth 1.0a authentcation required
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 4, groups = {"wso2.esb"}, description = "tumblr {getFollowing} integration positive test")
    public void optionalTestTumblrGetFollowing() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);
        String consumerSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_SECRET);
        log.info("consumerSecret : " + consumerSecret);

        String accessToken = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_ACCESS_TOKEN);
        log.info("accessToken : " + accessToken);
        String tokenSecret = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_TOKEN_SECRET);
        log.info("tokenSecret : " + tokenSecret);

        //Get Direct response from tumblr
        String requestUrl = "http://api.tumblr.com/v2/user/following";
        log.info("requestUrl : " + requestUrl);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("offset", "2");
        params.put("limit", "2");

        JSONObject directResponse = ConnectorIntegrationUtil.DirectHttpAuthGET(requestUrl, consumerKey,
                                                                               consumerSecret, accessToken,
                                                                               tokenSecret, params);

        log.info("DirectResponse : " + directResponse.get("meta").toString());
        log.debug("DirectResponse : " + directResponse.get("response").toString());

        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "optional/getFollowing.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.debug("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponse.get("meta").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString(),
                            directResponse.get("response").toString());
    }


    /**
     * Positive integration test case for getTaggedPosts
     * Use only mandatory parameters
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 4, groups = {"wso2.esb"}, description = "tumblr {getTaggedPosts} integration positive test")
    public void optionalTestTumblrGetTaggedPosts() throws IOException, JSONException {

        String consumerKey = connectorProperties.getProperty(TumblrTestConstants.PROPERTY_CONSUMER_KEY);
        log.info("consumerKey : " + consumerKey);

        String tag = connectorProperties.getProperty("postTag");

        //Get Direct response from tumblr
        String requestUrl = "http://api.tumblr.com/v2/tagged?api_key=" + consumerKey + "&tag=" + tag
                            + "&limit=2&offset=2";
        log.info("requestUrl : " + requestUrl);

        String directResponse = ConnectorIntegrationUtil.DirectHttpGET(requestUrl);

        JSONObject directResponseJObj = new JSONObject(directResponse);
        log.info("ESB:" + directResponseJObj.get("meta").toString());


        //Get response using the connector
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL("tumblr"),
                                                                       "POST", esbRequestHeadersMap, "optional/getTaggedPosts.json");

        log.info("ESB:" + esbRestResponse.getBody().get("meta").toString());
        log.debug("ESB:" + esbRestResponse.getBody().get("response").toString());

        Assert.assertEquals(esbRestResponse.getBody().get("meta").toString(),
                            directResponseJObj.get("meta").toString());
        //response not compared since tumblr may return different tagged post
    }


}