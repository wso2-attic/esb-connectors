/*
 *  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.integration.test.yammer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class YammerConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private String apiRequestUrl;
    
    private long timeOut;
    
    private static final int SUCCESS_STATUS_CODE = 200;
    
    private static final int ERROR_STATUS_CODE = 404;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("yammer-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("apiToken"));
        
        apiRequestUrl = connectorProperties.getProperty("apiUrl") + "/api/v1";
        
        timeOut = Long.parseLong(connectorProperties.getProperty("timeOut"));
    }
    
    /**
     * Positive test case for postNewMessage method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "yammer {postNewMessage} integration test with mandatory parameters.")
    public void testPostNewMessageWithMandatoryParameters() throws IOException, JSONException {
    
        final String proxyUrlAttachment = getProxyServiceURL("yammer_postMessage");
        
        final String endPointUrl =
                proxyUrlAttachment + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&apiToken="
                        + connectorProperties.getProperty("apiToken") + "&responseType=json";
        
        final MultipartFormdataProcessor multipartRequestProcessor = new MultipartFormdataProcessor(endPointUrl);
        
        File file = new File(pathToResourcesDirectory + connectorProperties.getProperty("attachmentFileName"));
        
        multipartRequestProcessor.addFileToRequest("attachment1", file);
        multipartRequestProcessor.addFormDataToRequest("body", connectorProperties.getProperty("postNewMessageBody"),
                org.apache.commons.lang3.CharEncoding.UTF_8);
        final RestResponse<JSONObject> esbRestResponse = multipartRequestProcessor.processForJsonResponse();
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        final String messageId = esbRestResponse.getBody().getJSONArray("messages").getJSONObject(0).getString("id");
        connectorProperties.setProperty("messageId", messageId);
        
        final String apiEndPoint = apiRequestUrl + "/messages/" + messageId + ".json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        Assert.assertEquals(connectorProperties.getProperty("postNewMessageBody"), apiRestResponse.getBody()
                .getJSONObject("body").getString("plain"));
    }
    
    /**
     * Positive test case for getAlgorithmicFeed method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testPostNewMessageWithMandatoryParameters" }, description = "yammer {getAlgorithmicFeed} integration test with mandatory parameters.")
    public void testGetAlgorithmicFeedWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getAlgorithmicFeed");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAlgorithmicFeed_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/messages/algo.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("references");
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("references");
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
    }
    
    /**
     * Positive test case for getAlgorithmicFeed method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetAlgorithmicFeedWithMandatoryParameters" }, description = "yammer {getAlgorithmicFeed} integration test with optional parameters.")
    public void testGetAlgorithmicFeedWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getAlgorithmicFeed");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAlgorithmicFeed_optional.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/messages/algo.json?limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("references");
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("references");
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
    }
    
    /**
     * Positive test case for getFollowing method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetAlgorithmicFeedWithOptionalParameters" }, description = "yammer {getFollowing} integration test with mandatory parameters.")
    public void testGetFollowingWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getFollowing");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFollowing_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/messages/following.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("references");
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("references");
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
    }
    
    /**
     * Positive test case for getFollowing method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetFollowingWithMandatoryParameters" }, description = "yammer {getFollowing} integration test with optional parameters.")
    public void testGetFollowingWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getFollowing");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFollowing_optional.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/messages/following.json?limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("references");
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("references");
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
    }
    
    /**
     * Positive test case for getMessages method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetFollowingWithOptionalParameters" }, description = "yammer {getMessages} integration test with mandatory parameters.")
    public void testGetMessagesWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getMessages");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMessages_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/messages.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("references");
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("references");
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
    }
    
    /**
     * Positive test case for getMessages method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetMessagesWithMandatoryParameters" }, description = "yammer {getMessages} integration test with optional parameters.")
    public void testGetMessagesWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getMessages");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMessages_optional.json");
        
        final String apiEndPoint = apiRequestUrl + "/messages.json?limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("references");
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("references");
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
    }
    
    /**
     * Positive test case for getMyFeed method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetMessagesWithOptionalParameters" }, description = "yammer {getMyFeed} integration test with mandatory parameters.")
    public void testGetMyFeedWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getMyFeed");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMyFeed_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/messages/my_feed.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("references");
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("references");
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
    }
    
    /**
     * Positive test case for getMyFeed method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetMyFeedWithMandatoryParameters" }, description = "yammer {getMyFeed} integration test with optional parameters.")
    public void testGetMyFeedWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:getMyFeed");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMyFeed_optional.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/messages/my_feed.json?limit=" + connectorProperties.getProperty("limit");
        Thread.sleep(timeOut);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("references");
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("references");
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
    }
    
    /**
     * Positive test case for getSentMessages method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetMyFeedWithOptionalParameters" }, description = "yammer {getSentMessages} integration test with mandatory parameters.")
    public void testGetSentMessagesWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getSentMessages");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSentMessages_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/messages/sent.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("references");
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("references");
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
    }
    
    /**
     * Positive test case for getSentMessages method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetSentMessagesWithMandatoryParameters" }, description = "Yammer {getSentMessages} integration test with optional parameters.")
    public void testGetSentMessagesWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getSentMessages");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSentMessages_optional.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/messages/sent.json?limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("references");
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("references");
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
    }
    
    /**
     * Positive test case for getPrivateMessages method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetSentMessagesWithOptionalParameters" }, description = "yammer {getPrivateMessages} integration test with mandatory parameters.")
    public void testGetPrivateMessagesWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getPrivateMessages");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPrivateMessages_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/messages/private.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("references");
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("references");
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
    }
    
    /**
     * Positive test case for getPrivateMessages method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetPrivateMessagesWithMandatoryParameters" }, description = "Yammer {getPrivateMessages} integration test with optional parameters.")
    public void testGetPrivateMessagesWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getPrivateMessages");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPrivateMessages_optional.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/messages/private.json?limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("references");
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("references");
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
    }
    
    /**
     * Positive test case for getReceivedMessages method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetPrivateMessagesWithOptionalParameters" }, description = "yammer {getReceivedMessages} integration test with mandatory parameters.")
    public void testGetReceivedMessagesWithMandatoryParameters() throws IOException, JSONException,
            InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getReceivedMessages");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getReceivedMessages_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/messages/received.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("references");
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("references");
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
    }
    
    /**
     * Positive test case for getReceivedMessages method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetReceivedMessagesWithMandatoryParameters" }, description = "Yammer {getReceivedMessages} integration test with optional parameters.")
    public void testGetReceivedMessagesWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getReceivedMessages");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getReceivedMessages_optional.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/messages/received.json?limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("references");
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("references");
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
    }
    
    /**
     * Positive test case for postLike method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetReceivedMessagesWithOptionalParameters" }, description = "yammer {postLike} integration test with mandatory parameters.")
    public void testPostLikeWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:postLike");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_postLike_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        /*
         * No Direct API call since GET method does not exist.
         */
    }
    
    /**
     * Negative test case for postLike method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testPostLikeWithMandatoryParameters" }, description = "yammer {postLike} integration test with negative case.")
    public void testPostLikeWithNegativeCase() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:postLike");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_postLike_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/messages/liked_by/current.json?message_id=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 403);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 403);
        
    }
    
    /**
     * Positive test case for removeLike method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testPostLikeWithNegativeCase" }, description = "yammer {removeLike} integration test with mandatory parameters.")
    public void testRemoveLikeWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:removeLike");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_removeLike_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
        /*
         * No Direct API call since GET method does not exist.
         */
    }
    
    /**
     * Negative test case for removeLike method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testRemoveLikeWithMandatoryParameters" }, description = "yammer {removeLike} integration test with negative case.")
    public void testRemoveLikeWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:removeLike");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_removeLike_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/messages/liked_by/current.json?message_id=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
    }
    
    /**
     * Positive test case for sendMessageToEmail method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testRemoveLikeWithNegativeCase" }, description = "yammer {sendMessageToEmail} integration test with mandatory parameters.")
    public void testSendMessageToEmailWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:sendMessageToEmail");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendMessageToEmail_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
        /*
         * No Direct API call since GET method does not exist.
         */
    }
    
    /**
     * Negative test case for sendMessageToEmail method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSendMessageToEmailWithMandatoryParameters" }, description = "yammer {sendMessageToEmail} integration test with negative case.")
    public void testSendMessageToEmailWithNegativeCase() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut * 2);
        esbRequestHeadersMap.put("Action", "urn:sendMessageToEmail");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendMessageToEmail_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/messages/email.json?message_id=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
    }
    
    /**
     * Positive test case for getUsers method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSendMessageToEmailWithNegativeCase" }, description = "yammer {getUsers} integration test with mandatory parameters.")
    public void testGetUsersWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getUsers");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUsers_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/users.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("type"), apiResponseArray.getJSONObject(0)
                .getString("type"));
    }
    
    /**
     * Positive test case for getUsers method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetUsersWithMandatoryParameters" }, description = "Yammer {getUsers} integration test with optional parameters.")
    public void testGetUsersWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getUsers");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUsers_optional.json");
        
        final String apiEndPoint = apiRequestUrl + "/users.json?reverse=true";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("type"), apiResponseArray.getJSONObject(0)
                .getString("type"));
    }
    
    /**
     * Positive test case for getCurrentUser method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetUsersWithOptionalParameters" }, description = "yammer {getCurrentUser} integration test with mandatory parameters.")
    public void testGetCurrentUserWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getCurrentUser");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCurrentUser_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/users/current.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("network_domains"), apiRestResponse.getBody()
                .getString("network_domains"));
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("first_name"),
                apiRestResponse.getBody().getString("first_name"));
        
    }
    
    /**
     * Positive test case for getAboutAUser method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetCurrentUserWithMandatoryParameters" }, description = "yammer {getAboutAUser} integration test with mandatory parameters.")
    public void testGetAboutAUserWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAboutAUser");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAboutAUser_mandatory.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/users/" + connectorProperties.getProperty("subscribedUserId") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("network_domains"), apiRestResponse.getBody()
                .getString("network_domains"));
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("first_name"),
                apiRestResponse.getBody().getString("first_name"));
        
    }
    
    /**
     * Negative test case for getAboutAUser method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetAboutAUserWithMandatoryParameters" }, description = "yammer {getAboutAUser} integration test with negative case.")
    public void testGetAboutAUserWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAboutAUser");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAboutAUser_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/users/invalid.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
    }
    
    /**
     * Positive test case for getByEmail method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetAboutAUserWithNegativeCase" }, description = "yammer {getByEmail} integration test with mandatory parameters.")
    public void testGetByEmailWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getByEmail");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getByEmail_mandatory.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/users/by_email.json?email=" + connectorProperties.getProperty("userEmail");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("state"), apiResponseArray.getJSONObject(0)
                .getString("state"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("first_name"), apiResponseArray
                .getJSONObject(0).getString("first_name"));
    }
    
    /**
     * Negative test case for getByEmail method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetByEmailWithMandatoryParameters" }, description = "yammer {getByEmail} integration test with negative case.")
    public void testGetByEmailWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getByEmail");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getByEmail_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/users/by_email.json?email=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
    }
    
    /**
     * Positive test case for getByGroup method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetByEmailWithNegativeCase" }, description = "yammer {getByGroup} integration test with mandatory parameters.")
    public void testGetByGroupWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getByGroup");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getByGroup_mandatory.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/users/in_group/" + connectorProperties.getProperty("groupId") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("users"));
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("users"));
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("job_title"), apiResponseArray.getJSONObject(0)
                .getString("job_title"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("full_name"), apiResponseArray.getJSONObject(0)
                .getString("full_name"));
    }
    
    /**
     * Positive test case for getByGroup method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetByGroupWithMandatoryParameters" }, description = "Yammer {getByGroup} integration test with optional parameters.")
    public void testGetByGroupWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getByGroup");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getByGroup_optional.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/users/in_group/" + connectorProperties.getProperty("groupId") + ".json?page=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("users"));
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("users"));
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("job_title"), apiResponseArray.getJSONObject(0)
                .getString("job_title"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("full_name"), apiResponseArray.getJSONObject(0)
                .getString("full_name"));
    }
    
    /**
     * Negative test case for getByGroup method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetByGroupWithOptionalParameters" }, description = "yammer {getByGroup} integration test with negative case.")
    public void testGetByGroupWithNegativeCase() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getByGroup");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getByGroup_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/users/in_group/invalid.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
    }
    
    /**
     * Positive test case for getTopicById method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetByGroupWithNegativeCase" }, description = "yammer {getTopicById} integration test with mandatory parameters.")
    public void testGetTopicByIdWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getTopicById");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTopicById_mandatory.json");
        
        String apiEndPoint = apiRequestUrl + "/topics/" + connectorProperties.getProperty("topicId") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("web_url"),
                apiRestResponse.getBody().getString("web_url"));
    }
    
    /**
     * Negative test case for getTopicById method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetTopicByIdWithMandatoryParameters" }, description = "yammer {getTopicById} integration test with negative case.")
    public void testGetTopicByIdWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getTopicById");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTopicById_negative.json");
        
        String apiEndPoint = apiRequestUrl + "/topics/INVALID.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
    }
    
    /**
     * Positive test case for getMessageById method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetTopicByIdWithNegativeCase" }, description = "yammer {getMessageById} integration test with mandatory parameters.")
    public void testGetMessageByIdWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getMessageById");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMessageById_mandatory.json");
        
        String apiEndPoint =
                apiRequestUrl + "/messages/about_topic/" + connectorProperties.getProperty("topicId") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("messages");
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("messages");
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
    }
    
    /**
     * Negative test case for getMessageById method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetMessageByIdWithMandatoryParameters" }, description = "yammer {getMessageById} integration test with negative case.")
    public void testGetMessageByIdWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getMessageById");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMessageById_negative.json");
        
        String apiEndPoint = apiRequestUrl + "/messages/about_topic/INVALID.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
    }
    
    /**
     * Positive test case for getThread method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetMessageByIdWithNegativeCase" }, description = "yammer {getThread} integration test with mandatory parameters.")
    public void testGetThreadWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getThread");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getThread_mandatory.json");
        
        String apiEndPoint = apiRequestUrl + "/threads/" + connectorProperties.getProperty("threadId") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("type"), apiRestResponse.getBody().getString("type"));
        Assert.assertEquals(esbRestResponse.getBody().getString("web_url"),
                apiRestResponse.getBody().getString("web_url"));
    }
    
    /**
     * Negative test case for getThread method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetThreadWithMandatoryParameters" }, description = "yammer {getThread} integration test with negative case.")
    public void testGetThreadWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getThread");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getThread_negative.json");
        
        String apiEndPoint = apiRequestUrl + "/threads/INVALID.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
    }
    
    /**
     * Positive test case for getSuggestion method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetThreadWithNegativeCase" }, description = "yammer {getSuggestion} integration test with mandatory parameters.")
    public void testGetSuggestionWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getSuggestion");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSuggestion_mandatory.json");
        
        String apiEndPoint = apiRequestUrl + "/suggestions.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
        JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
    }
    
    /**
     * Positive test case for getSuggestion method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetSuggestionWithMandatoryParameters" }, description = "yammer {getSuggestion} integration test with optional parameters.")
    public void testGetSuggestionWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getSuggestion");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSuggestion_optional.json");
        
        String apiEndPoint = apiRequestUrl + "/suggestions.json?limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
        JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
    }
    
    /**
     * Positive test case for addRelationship method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetSuggestionWithOptionalParameters" }, description = "yammer {addRelationship} integration test with mandatory parameters.")
    public void testAddRelationshipWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:addRelationship");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addRelationship_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        final String subordinateId =
                esbRestResponse.getBody().getJSONObject("relationship").getString("subordinate_user_id");
        connectorProperties.setProperty("subordinateId", subordinateId);
        
        String apiEndPoint = apiRequestUrl + "/relationships.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("subordinates");
        
        boolean isSubordinateFound = false;
        String networkId = "";
        for (int i = 0; i < apiResponseArray.length(); i++) {
            JSONObject currentSubordinate = apiResponseArray.getJSONObject(i);
            if (subordinateId.equals(currentSubordinate.getString("id"))) {
                isSubordinateFound = true;
                networkId = currentSubordinate.getString("network_id");
                break;
            }
        }
        
        Assert.assertTrue(isSubordinateFound);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("relationship").getString("network_id"), networkId);
    }
    
    /**
     * Positive test case for addRelationship method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddRelationshipWithMandatoryParameters" }, description = "yammer {addRelationship} integration test with optional parameters.")
    public void testAddRelationshipWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:addRelationship");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addRelationship_optional.json");
        
        final String subordinateId =
                esbRestResponse.getBody().getJSONObject("relationship").getString("subordinate_user_id");
        
        String apiEndPoint = apiRequestUrl + "/relationships.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("subordinates");
        
        boolean isSubordinateFound = false;
        String networkId = "";
        for (int i = 0; i < apiResponseArray.length(); i++) {
            JSONObject currentSubordinate = apiResponseArray.getJSONObject(i);
            if (subordinateId.equals(currentSubordinate.getString("id"))) {
                isSubordinateFound = true;
                networkId = currentSubordinate.getString("network_id");
                break;
            }
        }
        
        Assert.assertTrue(isSubordinateFound);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("relationship").getString("network_id"), networkId);
    }
    
    /**
     * Negative test case for addRelationship method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddRelationshipWithOptionalParameters" }, description = "yammer {addRelationship} integration test with negative case.")
    public void testAddRelationshipWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:addRelationship");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addRelationship_negative.json");
        
        String apiEndPoint =
                apiRequestUrl + "/relationships.json?subordinate="
                        + connectorProperties.getProperty("relationshipEmail");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
    }
    
    /**
     * Positive test case for getRelationship method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddRelationshipWithNegativeCase" }, description = "yammer {getRelationship} integration test with mandatory parameters.")
    public void testGetRelationshipWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getRelationship");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRelationship_mandatory.json");
        
        String apiEndPoint = apiRequestUrl + "/relationships.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("subordinates");
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("subordinates");
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("full_name"), apiResponseArray.getJSONObject(0)
                .getString("full_name"));
    }
    
    /**
     * Positive test case for getRelationship method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetRelationshipWithMandatoryParameters" }, description = "yammer {getRelationship} integration test with optional parameters.")
    public void testGetRelationshipWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getRelationship");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRelationship_optional.json");
        
        String apiEndPoint =
                apiRequestUrl + "/relationships.json?user_id=" + connectorProperties.getProperty("relationshipUserId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("subordinates");
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("subordinates");
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("full_name"), apiResponseArray.getJSONObject(0)
                .getString("full_name"));
    }
    
    /**
     * Negative test case for getRelationship method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetRelationshipWithOptionalParameters" }, description = "yammer {getRelationship} integration test with negative case.")
    public void testGetRelationshipWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getRelationship");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRelationship_negative.json");
        
        String apiEndPoint = apiRequestUrl + "/relationships.json?user_id=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 500);
    }
    
    /**
     * Positive test case for deleteRelationship method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetRelationshipWithNegativeCase" }, description = "yammer {deleteRelationship} integration test with mandatory parameters.")
    public void testDeleteRelationshipWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:deleteRelationship");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteRelationship_mandatory.json");
        
        String apiEndPoint = apiRequestUrl + "/relationships.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("subordinates");
        
        boolean isSubordinateFound = false;
        for (int i = 0; i < apiResponseArray.length(); i++) {
            JSONObject currentSubordinate = apiResponseArray.getJSONObject(i);
            if (connectorProperties.getProperty("subordinateId").equals(currentSubordinate.getString("id"))) {
                isSubordinateFound = true;
                break;
            }
        }
        
        Assert.assertFalse(isSubordinateFound);
    }
    
    /**
     * Negative test case for deleteRelationship method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteRelationshipWithMandatoryParameters" }, description = "yammer {deleteRelationship} integration test with negative case.")
    public void testDeleteRelationshipWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteRelationship");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteRelationship_negative.json");
        
        String apiEndPoint =
                apiRequestUrl + "/relationships/" + connectorProperties.getProperty("subordinateId")
                        + ".json?type=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
    }
    
    /**
     * Positive test case for joinGroup method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteRelationshipWithNegativeCase" }, description = "yammer {joinGroup} integration test with mandatory parameters.")
    public void testJoinGroupWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:joinGroup");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_joinGroup_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        /*
         * No Direct API call since GET method does not exist.
         */
    }
    
    /**
     * Negative test case for joinGroup method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testJoinGroupWithMandatoryParameters" }, description = "yammer {joinGroup} integration test with negative case.")
    public void testJoinGroupWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:joinGroup");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_joinGroup_negative.json");
        
        String apiEndPoint = apiRequestUrl + "/group_memberships.json?group_id=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
    }
    
    /**
     * Positive test case for leaveGroup method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testJoinGroupWithNegativeCase" }, description = "yammer {leaveGroup} integration test with mandatory parameters.")
    public void testLeaveGroupWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:leaveGroup");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_leaveGroup_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
        /*
         * No Direct API call since GET method does not exist.
         */
    }
    
    /**
     * Negative test case for leaveGroup method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testLeaveGroupWithMandatoryParameters" }, description = "yammer {leaveGroup} integration test with negative case.")
    public void testLeaveGroupWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:leaveGroup");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_leaveGroup_negative.json");
        
        String apiEndPoint = apiRequestUrl + "/group_memberships.json?group_id=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
    }
    
    /**
     * Positive test case for addSubscription method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testLeaveGroupWithNegativeCase" }, description = "yammer {addSubscription} integration test with mandatory parameters.")
    public void testAddSubscriptionWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:addSubscription");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addSubscription_mandatory.json");
        
        String apiEndPoint =
                apiRequestUrl + "/subscriptions/to_topic/" + connectorProperties.getProperty("topicId") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        // Checking the created subscription is created
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
        Assert.assertEquals(connectorProperties.getProperty("topicId"), apiRestResponse.getBody()
                .getString("target_id"));
    }
    
    /**
     * Negative test case for addSubscription method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddSubscriptionWithMandatoryParameters" }, description = "yammer {addSubscription} integration test with negative case.")
    public void testAddSubscriptionWithNegativeCase() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:addSubscription");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addSubscription_negative.json");
        
        String apiEndPoint = apiRequestUrl + "/subscriptions?target_type=topic&target_id=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
    }
    
    /**
     * Positive test case for getSubTopic method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddSubscriptionWithNegativeCase" }, description = "yammer {getSubTopic} integration test with mandatory parameters.")
    public void testGetSubTopicWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getSubTopic");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSubTopic_mandatory.json");
        
        String apiEndPoint =
                apiRequestUrl + "/subscriptions/to_topic/" + connectorProperties.getProperty("topicId") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("target_web_url"),
                apiRestResponse.getBody().getString("target_web_url"));
    }
    
    /**
     * Negative test case for getSubTopic method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetSubTopicWithMandatoryParameters" }, description = "yammer {getSubTopic} integration test with negative case.")
    public void testGetSubTopicWithNegativeCase() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getSubTopic");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSubTopic_negative.json");
        
        String apiEndPoint = apiRequestUrl + "/subscriptions/to_topic/INVALID.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
    }
    
    /**
     * Positive test case for sendInvitation method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetSubTopicWithNegativeCase" }, description = "yammer {sendInvitation} integration test with mandatory parameters.")
    public void testSendInvitationWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:sendInvitation");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendInvitation_mandatory.json");
        
        String email = connectorProperties.getProperty("email");
        String apiEndPoint = apiRequestUrl + "/invitations.json?email=" + email;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
    }
    
    /**
     * Negative test case for sendInvitation method
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSendInvitationWithMandatoryParameters" }, description = "yammer {sendInvitation} integration test with negative case.")
    public void testSendInvitationWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:sendInvitation");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendInvitation_negative.json");
        String apiEndPoint = apiRequestUrl + "/invitations.json?email=";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
    }
    
    /**
     * Positive test case for getNotification method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSendInvitationWithNegativeCase" }, description = "yammer {getNotification} integration test with mandatory parameters.")
    public void testGetNotificationWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getNotification");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getNotification_mandatory.json");
        
        String apiEndPoint = apiRequestUrl + "/streams/notifications.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getString(0), apiRestResponse.getBody()
                .getJSONArray("items").getString(0));
    }
    
    /**
     * Positive test case for getSearch method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetNotificationWithMandatoryParameters" }, description = "yammer {getSearch} integration test with mandatory parameters.")
    public void testGetSearchWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getSearch");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSearch_mandatory.json");
        
        String apiEndPoint = apiRequestUrl + "/search.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("count").get("messages"), apiRestResponse.getBody()
                .getJSONObject("count").get("messages"));
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("count").get("topics"), apiRestResponse.getBody()
                .getJSONObject("count").get("topics"));
    }
    
    /**
     * Positive test case for getSearch method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetSearchWithMandatoryParameters" }, description = "yammer {getSearch} integration test with optional parameters.")
    public void testGetSearchWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getSearch");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSearch_optional.json");
        String searchQuery = connectorProperties.getProperty("search");
        String page = connectorProperties.getProperty("page");
        String numPerPage = connectorProperties.getProperty("limit");
        
        String apiEndPoint =
                apiRequestUrl + "/search.json?search=" + searchQuery + "&page=" + page + "&num_per_page=" + numPerPage;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("count").get("messages"), apiRestResponse.getBody()
                .getJSONObject("count").get("messages"));
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("count").get("topics"), apiRestResponse.getBody()
                .getJSONObject("count").get("topics"));
    }
    
    /**
     * Positive test case for getAutocomplete method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetSearchWithOptionalParameters" }, description = "yammer {getAutocomplete} integration test with mandatory parameters.")
    public void testGetAutocompleteWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAutocomplete");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAutocomplete_mandatory.json");
        
        String prefix = connectorProperties.getProperty("prefix");
        
        String apiEndPoint = apiRequestUrl + "/autocomplete/ranked?prefix=" + prefix + "&models=user:2";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("user").getJSONObject(0).get("id"), apiRestResponse
                .getBody().getJSONArray("user").getJSONObject(0).get("id"));
    }
    
    /**
     * Positive test case for getNetwork method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetAutocompleteWithMandatoryParameters" }, description = "yammer {getNetwork} integration test with mandatory parameters.")
    public void testGetNetworkWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getNetwork");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getNetwork_mandatory.json");
        String apiEndPoint = apiRequestUrl + "/networks/current.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        
        JSONArray esbJsonArrayResponse = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiJsonArrayResponse = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbJsonArrayResponse.getJSONObject(0).getString("id"), apiJsonArrayResponse
                .getJSONObject(0).getString("id"));
    }
    
    /**
     * Positive test case for getNetwork method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetNetworkWithMandatoryParameters" }, description = "yammer {getNetwork} integration test with optional parameters.")
    public void testGetNetworkWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getNetwork");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getNetwork_mandatory.json");
        
        String apiEndPoint =
                apiRequestUrl + "/networks/current.json?include_suspended=TRUE"
                        + "&exclude_own_messages_from_unseen=TRUE";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
        JSONArray esbJsonArrayResponse = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiJsonArrayResponse = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbJsonArrayResponse.length(), apiJsonArrayResponse.length());
        Assert.assertEquals(esbJsonArrayResponse.getJSONObject(0).getString("id"), apiJsonArrayResponse
                .getJSONObject(0).getString("id"));
    }
    
    /**
     * Positive test case for getSubUser method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetNetworkWithOptionalParameters" }, description = "yammer {getSubUser} integration test with mandatory parameters.")
    public void testGetSubUserWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getSubUser");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSubUser_mandatory.json");
        String userId = connectorProperties.getProperty("subscribedUserId");
        String apiEndPoint = apiRequestUrl + "/subscriptions/to_user/" + userId + ".json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("target_id"),
                apiRestResponse.getBody().getString("target_id"));
    }
    
    /**
     * Negative test case for getSubUser method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetSubUserWithMandatoryParameters" }, description = "yammer {getSubUser} integration test with negative case.")
    public void testGetSubUserWithNegativeCase() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getSubUser");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSubUser_negative.json");
        
        String apiEndPoint = apiRequestUrl + "/subscriptions/to_user/r.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
    }
    
    /**
     * Positive test case for getSubThread method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetSubUserWithNegativeCase" }, description = "yammer {getSubThread} integration test with mandatory parameters.")
    public void testGetSubThreadWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getSubThread");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSubThread_mandatory.json");
        String threadId = connectorProperties.getProperty("threadId");
        String apiEndPoint = apiRequestUrl + "/subscriptions/to_thread/" + threadId + ".json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("target_id"),
                apiRestResponse.getBody().getString("target_id"));
    }
    
    /**
     * Negative test case for getSubThread method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetSubThreadWithMandatoryParameters" }, description = "yammer {getSubThread} integration test with negative case.")
    public void testGetSubThreadWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getSubThread");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSubThread_negative.json");
        
        String apiEndPoint = apiRequestUrl + "/subscriptions/to_thread/r.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
    }
    
    /**
     * Positive test case for deleteSubscription method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetSubThreadWithNegativeCase" }, description = "yammer {deleteSubscription} integration test with mandatory parameters.")
    public void testDeleteSubscriptionWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteSubscription");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteSubscription_mandatory.json");
        
        String subscribedUserId = connectorProperties.getProperty("subscribedUserId");
        
        String apiEndPoint = apiRequestUrl + "subscriptions/to_user/" + subscribedUserId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
    }
    
    /**
     * Negative test case for deleteSubscription method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteSubscriptionWithMandatoryParameters" }, description = "yammer {deleteSubscription} integration test with negative case.")
    public void testDeleteSubscriptionWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteSubscription");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteSubscription_negative.json");
        
        String apiEndPoint = apiRequestUrl + "/subscriptions.json?target_type=user&target_id=a";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
    }
    
    /**
     * Positive test case for createPendingAttachment method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteSubscriptionWithNegativeCase" }, description = "yammer {createPendingAttachment} integration test with mandatory parameters.")
    public void testCreatePendingAttachmentWithMandatoryParameters() throws IOException, JSONException,
            InterruptedException {
    
        Thread.sleep(timeOut);
        final String proxyUrlAttachment = getProxyServiceURL("yammer_attachment");
        
        String endPointUrl =
                proxyUrlAttachment + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&apiToken="
                        + connectorProperties.getProperty("apiToken") + "&responseType=json";
        
        final MultipartFormdataProcessor fileRequestProcessor = new MultipartFormdataProcessor(endPointUrl);
        
        File file = new File(pathToResourcesDirectory + connectorProperties.getProperty("attachmentFileName"));
        
        fileRequestProcessor.addFileToRequest("attachment", file);
        final RestResponse<JSONObject> esbRestResponse = fileRequestProcessor.processForJsonResponse();
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        final String attachmentId = esbRestResponse.getBody().getString("id");
        
        String apiEndPoint = apiRequestUrl + "/subscriptions.json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("subscriptions");
        
        boolean isAttachmentFound = false;
        for (int i = 0; i < apiResponseArray.length(); i++) {
            if (attachmentId.equals(apiResponseArray.getJSONObject(i).getString("target_id"))) {
                isAttachmentFound = true;
                break;
            }
        }
        
        Assert.assertTrue(isAttachmentFound);
    }
    
    /**
     * Positive test case for deleteMessage method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePendingAttachmentWithMandatoryParameters" }, description = "yammer {deleteMessage} integration test with mandatory parameters.")
    public void testDeleteMessageWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteMessage");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteMessage_mandatory.json");
        
        String messageId = connectorProperties.getProperty("messageId");
        
        String apiEndPoint = apiRequestUrl + "/messages/" + messageId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), SUCCESS_STATUS_CODE);
        // Retrieving deleted message.
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
    }
    
    /**
     * Negative test case for deleteMessage method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteMessageWithMandatoryParameters" }, description = "yammer {deleteMessage} integration test with negative case.")
    public void testDeleteMessageWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteMessage");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteMessage_negative.json");
        
        String apiEndPoint = apiRequestUrl + "/messages/1.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), ERROR_STATUS_CODE);
    }
}
