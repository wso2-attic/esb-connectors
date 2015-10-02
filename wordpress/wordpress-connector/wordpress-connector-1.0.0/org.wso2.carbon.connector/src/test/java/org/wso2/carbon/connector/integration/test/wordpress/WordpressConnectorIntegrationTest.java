/**
 *  Copyright (c) 2009-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.integration.test.wordpress;

import java.lang.String;
import java.lang.System;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

/**
 * Integration test class for Wordpress connector.
 */
public class WordpressConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> parametersMap = new HashMap<String, String>();

    private Map<String, String> headersMap = new HashMap<String, String>();

    private String multipartProxyUrl;

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        init("wordpress-connector-1.0.0");

        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");

        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("accessToken"));

        String multipartPoxyName = connectorProperties.getProperty("multipartProxyName");
        multipartProxyUrl = getProxyServiceURL(multipartPoxyName);
        connectorProperties.setProperty("tagNameOptional", System.currentTimeMillis() + connectorProperties
                .getProperty("tagNameOptional"));
        connectorProperties.setProperty("tagNameMandatory", System.currentTimeMillis() + connectorProperties
                .getProperty("tagNameMandatory"));
        connectorProperties.setProperty("editedTagNameMandatory", System.currentTimeMillis() + connectorProperties
                .getProperty("editedTagNameMandatory"));
        connectorProperties.setProperty("editedTagNameOptional", System.currentTimeMillis() + connectorProperties
                .getProperty("editedTagNameOptional"));
    }

    /**
     * Positive test case for getBlogUsers method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getBlogUsers} integration test with mandatory parameters.")
    public void testGetBlogUsersWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getBlogUsers");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/users";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBlogUsers_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("found"), apiRestResponse.getBody().get("found"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("users").length(), apiRestResponse.getBody().getJSONArray("users").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("users").getJSONObject(0).get("ID"), apiRestResponse.getBody().getJSONArray("users").getJSONObject(0).get("ID"));
    }

    /**
     * Positive test case for getBlogUsers method with optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getBlogUsers} integration test with optional parameters.")
    public void testGetBlogUsersWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getBlogUsers");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/users?fields=ID,login,email&order=ASC&order_by=ID&http_envelope=true";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBlogUsers_optional.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("code"), apiRestResponse.getBody().get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("body").get("found"), apiRestResponse.getBody().getJSONObject("body").get("found"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("body").getJSONArray("users").length(), apiRestResponse.getBody().getJSONObject("body").getJSONArray("users").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("body").getJSONArray("users").getJSONObject(0).length(), apiRestResponse.getBody().getJSONObject("body").getJSONArray("users").getJSONObject(0).length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("body").getJSONArray("users").getJSONObject(0).get("ID"), apiRestResponse.getBody().getJSONObject("body").getJSONArray("users").getJSONObject(0).get("ID"));

    }

    /**
     * Negative test case for getBlogUsers method.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getBlogUsers} integration test with negative parameters.")
    public void testGetBlogUsersWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getBlogUsers");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "imirshadhassan.com/users?http_envelope=true";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBlogUsers_negative.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("code"), apiRestResponse.getBody().get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("body").get("error"), apiRestResponse.getBody().getJSONObject("body").get("error"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("body").get("message"), apiRestResponse.getBody().getJSONObject("body").get("message"));

    }

    /**
     * Positive test case for createPost method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {createPost} integration test with mandatory parameters.")
    public void testCreatePostWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createPost");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPost_mandatory.txt");
        parametersMap.put("postId", esbRestResponse.getBody().getString("ID"));

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/" + parametersMap.get("postId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("ID"), apiRestResponse.getBody().get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("title"), apiRestResponse.getBody().get("title"));

    }

    /**
     * Positive test case for createPost method with optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {createPost} integration test with mandatory parameters.")
    public void testCreatePostWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createPost");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPost_optional.txt");
        parametersMap.put("postIdOptional", esbRestResponse.getBody().getString("ID"));

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/" + parametersMap.get("postIdOptional");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("ID"), apiRestResponse.getBody().get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("title"), apiRestResponse.getBody().get("title"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("author").get("ID"), apiRestResponse.getBody().getJSONObject("author").get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("content"), apiRestResponse.getBody().get("content"));
        Assert.assertEquals(esbRestResponse.getBody().get("slug"), apiRestResponse.getBody().get("slug"));
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().get("comments_open"), apiRestResponse.getBody().get("comments_open"));

    }

    /**
     * Negative test case for createPost method.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {createPost} integration test with negative parameters.")
    public void testCreatePostWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createPost");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPost_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/new";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createPost_negative.txt");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));

    }

    /**
     * Positive test case for editPost method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testCreatePostWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "wordpress {editPost} integration test with mandatory parameters.")
    public void testEditPostWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:editPost");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editPost_mandatory.txt", parametersMap);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/" + parametersMap.get("postId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("ID"), apiRestResponse.getBody().get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("title"), apiRestResponse.getBody().get("title"));

    }

    /**
     * Positive test case for editPost method with optional parameters.
     */
    @Test(dependsOnMethods = {"testEditPostWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "wordpress {editPost} integration test with optional parameters.")
    public void testEditPostWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:editPost");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editPost_optional.txt", parametersMap);
        parametersMap.put("postSlug", esbRestResponse.getBody().getString("slug"));

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/" + parametersMap.get("postId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("ID"), apiRestResponse.getBody().get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("title"), apiRestResponse.getBody().get("title"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("author").get("ID"), apiRestResponse.getBody().getJSONObject("author").get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("content"), apiRestResponse.getBody().get("content"));
        Assert.assertEquals(esbRestResponse.getBody().get("slug"), apiRestResponse.getBody().get("slug"));
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().get("comments_open"), apiRestResponse.getBody().get("comments_open"));

    }

    /**
     * Negative test case for editPost method.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {editPost} integration test with negative parameters.")
    public void testEditPostWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:editPost");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editPost_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/700";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_editPost_negative.txt");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));

    }

    /**
     * Positive test case for createComment with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testCreatePostWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "wordpress {createComment} integration test with mandatory parameters.")
    public void testCreateCommentWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createComment");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_mandatory.txt", parametersMap);
        parametersMap.put("commentId", esbRestResponse.getBody().getString("ID"));

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/comments/" + parametersMap.get("commentId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("ID"), apiRestResponse.getBody().get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("content"), apiRestResponse.getBody().get("content"));
        Assert.assertEquals(esbRestResponse.getBody().get("URL"), apiRestResponse.getBody().get("URL"));
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
    }

    /**
     * Positive test case for createComment with optional parameters.
     */
    @Test(dependsOnMethods = {"testCreatePostWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "wordpress {createComment} integration test with optional parameters.")
    public void testCreateCommentWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createComment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_optional.txt", parametersMap);
        parametersMap.put("commentId2", esbRestResponse.getBody().getString("ID"));


        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/comments/" + parametersMap.get("commentId2") + "?meta=self,post";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("ID"), apiRestResponse.getBody().get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("content"), apiRestResponse.getBody().get("content"));
        Assert.assertEquals(esbRestResponse.getBody().get("URL"), apiRestResponse.getBody().get("URL"));
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
    }

    /**
     * Negative test case for createComment method
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {createComment} integration test with negative case")
    public void testCreateCommentWithNegativeCase() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:createComment");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_negative.txt", parametersMap);


        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "non-exist.wordpress.com" + "/comments/" + parametersMap.get("commentId") + "?meta=self,post";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
    }

    /**
     * Positive test case for editComment with mandatory parameters
     */
    @Test(dependsOnMethods = {"testCreateCommentWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "wordpress {editComment} integration test with mandatory parameters")
    public void testEditCommentWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:editComment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editComment_mandatory.txt", parametersMap);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/comments/" + parametersMap.get("commentId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("ID"), apiRestResponse.getBody().get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("content"), apiRestResponse.getBody().get("content"));
        Assert.assertEquals(esbRestResponse.getBody().get("URL"), apiRestResponse.getBody().get("URL"));
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));

    }

    /**
     * Positive test case for editComment with optional parameters
     */
    @Test(dependsOnMethods = {"testCreateCommentWithOptionalParameters"}, groups = {"wso2.esb"}, description = "wordpress {editComment} integration test with optional parameters")
    public void testEditCommentWithOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:editComment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editComment_optional.txt", parametersMap);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/comments/" + parametersMap.get("commentId2");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("ID"), apiRestResponse.getBody().get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("content"), apiRestResponse.getBody().get("content"));
        Assert.assertEquals(esbRestResponse.getBody().get("URL"), apiRestResponse.getBody().get("URL"));
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
    }

    /**
     * Negative test case for editComment method
     */
    @Test(dependsOnMethods = {"testCreateCommentWithOptionalParameters"}, groups = {"wso2.esb"}, description = "wordpress {editComment} integration test with negative case")
    public void testEditCommentWithNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:editComment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editComment_negative.txt", parametersMap);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "non-exist.wordpress.com" + "/comments/" + parametersMap.get("commentId2");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
    }

    /**
     * Positive test case for getComments with mandatory parameters
     */
    @Test(dependsOnMethods = {"testEditCommentWithOptionalParameters"}, groups = {"wso2.esb"}, description = "wordpress {getComments} integration test with mandatory parameters")
    public void testGetCommentsWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getComments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getComments_mandatory.txt", parametersMap);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/" + parametersMap.get("postId") + "/replies";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("found"), apiRestResponse.getBody().get("found"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("comments").getJSONObject(0).get("content"),
                apiRestResponse.getBody().getJSONArray("comments").getJSONObject(0).get("content"));

    }


    /**
     * Positive test case for getComments with optional parameters
     */
    @Test(dependsOnMethods = {"testEditCommentWithOptionalParameters"}, groups = {"wso2.esb"}, description = "wordpress {getComments} integration test with optional parameters")
    public void testGetCommentsWithOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getComments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getComments_optional.txt", parametersMap);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/" + parametersMap.get("postId") + "/replies"
                + "?meta=self,site,post&fields=ID,post,author,meta&order=DESC";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("found"), apiRestResponse.getBody().get("found"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("comments").getJSONObject(0).get("ID"),
                apiRestResponse.getBody().getJSONArray("comments").getJSONObject(0).get("ID"));

    }

    /**
     * Negative test case for getComments method
     */
    @Test(dependsOnMethods = {"testEditCommentWithOptionalParameters"}, groups = {"wso2.esb"}, description = "wordpress {getComments} integration test with negative case")
    public void testGetCommentsWithNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getComments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getComments_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "non-exist.wordpress.com/posts/24/replies"
                + "?meta=self,site,post&fields=ID,post,author,meta&number=3&offset=1&order=DESC";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));

    }

    /**
     * Positive test case for getRecentComments with mandatory parameters
     */
    @Test(dependsOnMethods = {"testEditCommentWithOptionalParameters"}, groups = {"wso2.esb"}, description = "wordpress {getRecentComments} integration test with mandatory parameters")
    public void testGetRecentCommentsWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getRecentComments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecentComments_mandatory.txt");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/comments";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("found"), apiRestResponse.getBody().get("found"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("comments").getJSONObject(0).get("content"),
                apiRestResponse.getBody().getJSONArray("comments").getJSONObject(0).get("content"));
    }

    /**
     * Positive test case for getRecentComments with optional parameters
     */
    @Test(dependsOnMethods = {"testEditCommentWithOptionalParameters"}, groups = {"wso2.esb"}, description = "wordpress {getRecentComments} integration test with optional parameters")
    public void testGetRecentCommentsWithOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getRecentComments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecentComments_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/comments?meta=self,site,post&fields=ID,post,author,meta&order=DESC";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("found"), apiRestResponse.getBody().get("found"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("comments").getJSONObject(0).get("ID"),
                apiRestResponse.getBody().getJSONArray("comments").getJSONObject(0).get("ID"));
    }

    /**
     * Negative test case for getRecentComments method
     */
    @Test(dependsOnMethods = {"testEditCommentWithOptionalParameters"}, groups = {"wso2.esb"}, description = "wordpress {getRecentComments} integration test with negative case")
    public void testGetRecentCommentsWithNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getRecentComments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecentComments_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "non-exist.wordpress.com/comments?meta=self,site,post&fields=ID,post,author,meta&order=DESC";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
    }

    /**
     * Positive test case for deleteComment with mandatory parameters.
     */
    @Test(priority = 3, groups = {"wso2.esb"}, description = "wordpress {deleteComment} integration test with mandatory parameters.")
    public void testDeleteCommentWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:deleteComment");

        RestResponse<JSONObject> esbRestResponse1 = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteComment_mandatory.txt", parametersMap);
        RestResponse<JSONObject> esbRestResponse2 = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteComment_mandatory.txt", parametersMap);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/comments/" + parametersMap.get("commentId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse1.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);

    }

    /**
     * Positive test case for deleteComment with optional parameters.
     */
    @Test(priority = 4, groups = {"wso2.esb"}, description = "wordpress {deleteComment} integration test with optional parameters.")
    public void testDeleteCommentWithOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:deleteComment");

        RestResponse<JSONObject> esbRestResponse1 = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteComment_optional.txt", parametersMap);
        RestResponse<JSONObject> esbRestResponse2 = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteComment_optional.txt", parametersMap);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/comments/" + parametersMap.get("commentId2") + "?meta=self,post&fields=ID,URL,content,meta";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse1.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);

    }

    /**
     * Negative test case for deleteComment method.
     */
    @Test(priority = 5, groups = {"wso2.esb"}, description = "wordpress {deleteComment} integration test with negative case")
    public void testDeleteCommentWithNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:deleteComment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteComment_negative.txt", parametersMap);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/comments/" + parametersMap.get("commentId2") + "/delete?meta=self,post&fields=ID,URL,content,meta";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));

    }

    /**
     * Positive test case for likePost with mandatory parameters
     */
    @Test(dependsOnMethods = {"testCreatePostWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "wordpress {likePost} integration test with mandatory parameters")
    public void testLikePostWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:likePost");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_likePost_mandatory.txt", parametersMap);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/" + parametersMap.get("postId") + "/likes/";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("success"), new Boolean("true"));
        Assert.assertEquals(esbRestResponse.getBody().get("i_like"), apiRestResponse.getBody().get("i_like"));
        Assert.assertEquals(esbRestResponse.getBody().get("like_count"), apiRestResponse.getBody().get("found"));

    }

    /**
     * Positive test case for likePost with optional parameters
     */
    @Test(dependsOnMethods = {"testCreatePostWithOptionalParameters"}, groups = {"wso2.esb"}, description = "wordpress {likePost} integration test with optional parameters")
    public void testLikePostWithOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:likePost");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_likePost_optional.txt", parametersMap);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/" + parametersMap.get("postIdOptional") + "/likes/?fields=found";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("like_count"), apiRestResponse.getBody().get("found"));

        int expected_no_of_fields = 1;
        Assert.assertEquals(esbRestResponse.getBody().length(), expected_no_of_fields);
    }

    /**
     * Negative test case for likePost method
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {likePost} integration test with negative case")
    public void testLikePostWithNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:likePost");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_likePost_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/10000/likes/new";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
    }

    /**
     * Positive test case for getLikesForPost method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testLikePostWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "wordpress {getLikesForPost} integration test with Mandatory Parameters.")
    public void testGetLikesForPostWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getLikesForPost");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/" + parametersMap.get("postId") + "/likes/";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getLikesForPost_mandatory.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("found"), apiRestResponse.getBody().get("found"));
        Assert.assertEquals(esbRestResponse.getBody().get("i_like"), apiRestResponse.getBody().get("i_like"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("likes").length(), apiRestResponse.getBody().getJSONArray("likes").length());

    }

    /**
     * Positive test case for getLikesForPost method with optional parameters.
     */
    @Test(dependsOnMethods = {"testLikePostWithOptionalParameters"}, groups = {"wso2.esb"}, description = "wordpress {getPostById} integration test with Mandatory Parameters.")
    public void testGetLikesForPostWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getLikesForPost");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/" + parametersMap.get("postIdOptional") + "/likes/?fields=found,i_like";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getLikesForPost_optional.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("found"), apiRestResponse.getBody().get("found"));
        Assert.assertEquals(esbRestResponse.getBody().get("i_like"), apiRestResponse.getBody().get("i_like"));

    }

    /**
     * Negative test case for getLikesForPost method.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getLikesForPost} integration test with Negative Parameters.")
    public void testGetLikesForPostWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getLikesForPost");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/10000/likes/";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getLikesForPost_negative.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));

    }

    /**
     * Positive test case for getPostById method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testCreatePostWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "wordpress {getPostById} integration test with Mandatory Parameters.")
    public void testGetPostByIdWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getPostById");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/" + parametersMap.get("postId");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPostById_mandatory.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("ID"), apiRestResponse.getBody().get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("site_ID"), apiRestResponse.getBody().get("site_ID"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("author").length(), apiRestResponse.getBody().getJSONObject("author").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("author").get("ID"), apiRestResponse.getBody().getJSONObject("author").get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("date"), apiRestResponse.getBody().get("date"));
        Assert.assertEquals(esbRestResponse.getBody().get("title"), apiRestResponse.getBody().get("title"));
        Assert.assertEquals(esbRestResponse.getBody().get("URL"), apiRestResponse.getBody().get("URL"));
        Assert.assertEquals(esbRestResponse.getBody().get("content"), apiRestResponse.getBody().get("content"));
        Assert.assertEquals(esbRestResponse.getBody().get("slug"), apiRestResponse.getBody().get("slug"));
        Assert.assertEquals(esbRestResponse.getBody().get("global_ID"), apiRestResponse.getBody().get("global_ID"));

    }

    /**
     * Positive test case for getPostById method with optional parameters.
     */
    @Test(dependsOnMethods = {"testCreatePostWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "wordpress {getPostById} integration test with optional Parameters.")
    public void testGetPostByIdWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getPostById");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/" + parametersMap.get("postId") + "?fields=ID,author,date,title,URL,content";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPostById_optional.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().length(), apiRestResponse.getBody().length());
        Assert.assertEquals(esbRestResponse.getBody().get("ID"), apiRestResponse.getBody().get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("author").length(), apiRestResponse.getBody().getJSONObject("author").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("author").get("ID"), apiRestResponse.getBody().getJSONObject("author").get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("date"), apiRestResponse.getBody().get("date"));
        Assert.assertEquals(esbRestResponse.getBody().get("title"), apiRestResponse.getBody().get("title"));
        Assert.assertEquals(esbRestResponse.getBody().get("URL"), apiRestResponse.getBody().get("URL"));
        Assert.assertEquals(esbRestResponse.getBody().get("content"), apiRestResponse.getBody().get("content"));

    }

    /**
     * Negative test case for getPostById method.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getPostById} integration test with Negative Parameters.")
    public void testGetPostByIdWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getPostById");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/10000";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPostById_negative.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));

    }

    /**
     * Positive test case for getPostBySlug method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testEditPostWithOptionalParameters"}, groups = {"wso2.esb"}, description = "wordpress {getPostBySlug} integration test with Mandatory Parameters.")
    public void testGetPostBySlugWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getPostBySlug");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/slug:" + parametersMap.get("postSlug");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPostBySlug_mandatory.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("ID"), apiRestResponse.getBody().get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("site_ID"), apiRestResponse.getBody().get("site_ID"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("author").length(), apiRestResponse.getBody().getJSONObject("author").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("author").get("ID"), apiRestResponse.getBody().getJSONObject("author").get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("date"), apiRestResponse.getBody().get("date"));
        Assert.assertEquals(esbRestResponse.getBody().get("title"), apiRestResponse.getBody().get("title"));
        Assert.assertEquals(esbRestResponse.getBody().get("URL"), apiRestResponse.getBody().get("URL"));
        Assert.assertEquals(esbRestResponse.getBody().get("content"), apiRestResponse.getBody().get("content"));
        Assert.assertEquals(esbRestResponse.getBody().get("slug"), apiRestResponse.getBody().get("slug"));
        Assert.assertEquals(esbRestResponse.getBody().get("global_ID"), apiRestResponse.getBody().get("global_ID"));

    }

    /**
     * Positive test case for getPostBySlug method with optional parameters.
     */
    @Test(dependsOnMethods = {"testEditPostWithOptionalParameters"}, groups = {"wso2.esb"}, description = "wordpress {getPostBySlug} integration test with Mandatory Parameters.")
    public void testGetPostBySlugWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getPostBySlug");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/slug:" + parametersMap.get("postSlug") + "?fields=slug,author,date,title,URL,content";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPostBySlug_optional.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().length(), apiRestResponse.getBody().length());
        Assert.assertEquals(esbRestResponse.getBody().get("slug"), apiRestResponse.getBody().get("slug"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("author").length(), apiRestResponse.getBody().getJSONObject("author").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("author").get("ID"), apiRestResponse.getBody().getJSONObject("author").get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("date"), apiRestResponse.getBody().get("date"));
        Assert.assertEquals(esbRestResponse.getBody().get("title"), apiRestResponse.getBody().get("title"));
        Assert.assertEquals(esbRestResponse.getBody().get("URL"), apiRestResponse.getBody().get("URL"));
        Assert.assertEquals(esbRestResponse.getBody().get("content"), apiRestResponse.getBody().get("content"));

    }

    /**
     * Negative test case for getPostBySlug method.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getPostBySlug} integration test with Negative Parameters.")
    public void testGetPostBySlugWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getPostBySlug");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/slug:FalseSlug";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPostBySlug_negative.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));

    }

    /**
     * Positive test case for getPosts method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testEditPostWithOptionalParameters"}, groups = {"wso2.esb"}, description = "wordpress {getPosts} integration test with Mandatory Parametors.")
    public void testGetPostsWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getPosts");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPosts_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("found"), apiRestResponse.getBody().get("found"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("posts").length(), apiRestResponse.getBody().getJSONArray("posts").length());

    }

    /**
     * Positive test case for getPosts method with optional parameters.
     */
    @Test(dependsOnMethods = {"testEditPostWithOptionalParameters"}, groups = {"wso2.esb"}, description = "wordpress {getPosts} integration test with Mandatory Parametors.")
    public void testGetPostsWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getPosts");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts?fields=ID,date,URL";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPosts_optional.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("found"), apiRestResponse.getBody().get("found"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("posts").length(), apiRestResponse.getBody().getJSONArray("posts").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("posts").getJSONObject(0).length(), apiRestResponse.getBody().getJSONArray("posts").getJSONObject(0).length());
    }

    /**
     * Negative test case for getPosts method.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getPosts} integration test with Negative Parameters.")
    public void testGetPostsWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getPosts");
        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/sites/google.lk/posts";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPosts_negative.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));

    }

    /**
     * Positive test case for insertCategory method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {insertCategory} integration test with mandatory parameters.")
    public void testInsertCategoryWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:insertCategory");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_insertCategory_mandatory.txt");
        parametersMap.put("categoryMandatory", esbRestResponse.getBody().getString("slug"));

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/categories/slug:" + parametersMap.get("categoryMandatory");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("ID"), apiRestResponse.getBody().get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("slug"), apiRestResponse.getBody().get("slug"));
    }

    /**
     * Positive test case for insertCategory method with optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {insertCategory} integration test with optional parameters.")
    public void testInsertCategoryWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:insertCategory");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_insertCategory_optional.txt");
        parametersMap.put("categoryOptional", esbRestResponse.getBody().getString("slug"));

        /*
        optional parameter fields not working because of a API error
         */

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/categories/slug:" + parametersMap.get("categoryOptional");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().get("ID"), apiRestResponse.getBody().get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("slug"), apiRestResponse.getBody().get("slug"));
        Assert.assertEquals(esbRestResponse.getBody().get("description"), apiRestResponse.getBody().get("description"));
    }

    /**
     * Negative test case for insertCategory method.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {insertCategory} integration test Negative case.")
    public void testInsertCategoryWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:insertCategory");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_insertCategory_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/categories/new";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_insertCategory_negative.txt");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));

    }

    /**
     * Positive test case for editCategory method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testInsertCategoryWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "wordpress {editCategory} integration test with mandatory parameters.")
    public void testEditCategoryWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:editCategory");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editCategory_mandatory.txt", parametersMap);
        parametersMap.put("categoryMandatory", esbRestResponse.getBody().getString("slug"));

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/categories/slug:" + parametersMap.get("categoryMandatory");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("ID"), apiRestResponse.getBody().get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("slug"), apiRestResponse.getBody().get("slug"));
    }

    /**
     * Positive test case for editCategory method with optional parameters.
     */
    @Test(dependsOnMethods = {"testInsertCategoryWithOptionalParameters"}, groups = {"wso2.esb"}, description = "wordpress {editCategory} integration test with mandatory parameters.")
    public void testEditCategoryWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:editCategory");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editCategory_optional.txt", parametersMap);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/categories/slug:" + esbRestResponse.getBody().getString("slug");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("ID"), apiRestResponse.getBody().get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("slug"), apiRestResponse.getBody().get("slug"));
        Assert.assertEquals(esbRestResponse.getBody().get("description"), apiRestResponse.getBody().get("description"));
    }


    /**
     * Negative test case for editCategory method.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {editCategory} integration test with mandatory parameters.")
    public void testEditCategoryWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:editCategory");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editCategory_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/categories/slug:thefalsename";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_editCategory_negative.txt");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
    }


    /**
     * Positive test case for getCategories method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testEditCategoryWithOptionalParameters"}, groups = {"wso2.esb"}, description = "wordpress {getCategories} integration test with mandatory parameters.")
    public void testGetCategoriesWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getCategories");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/categories";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCategories_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("found"), apiRestResponse.getBody().get("found"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("categories").length(), apiRestResponse.getBody().getJSONArray("categories").length());

    }

    /**
     * Positive test case for getCategories method with optional parameters.
     */
    @Test(dependsOnMethods = {"testEditCategoryWithOptionalParameters"}, groups = {"wso2.esb"}, description = "wordpress {getCategories} integration test with mandatory parameters.")
    public void testGetCategoriesWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getCategories");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/categories";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCategories_optional.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "api_getCategories_optional.txt");

        Assert.assertEquals(esbRestResponse.getBody().get("found"), apiRestResponse.getBody().get("found"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("categories").length(), apiRestResponse.getBody().getJSONArray("categories").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("categories").getJSONObject(0).get("ID"), apiRestResponse.getBody().getJSONArray("categories").getJSONObject(0).get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("categories").getJSONObject(0).get("name"), apiRestResponse.getBody().getJSONArray("categories").getJSONObject(0).get("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("categories").getJSONObject(0).get("slug"), apiRestResponse.getBody().getJSONArray("categories").getJSONObject(0).get("slug"));

    }

    /**
     * Negative test case for getCategories method.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getCategories} integration test with negative parameters.")
    public void testGetCategoriesWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getCategories");
        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/sites/imirshadhassan.com/categories";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCategories_negative.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));

    }

    /**
     * Positive test case for deleteCategory method with Mandatory parameters.
     */
    @Test(dependsOnMethods = {"testGetCategoriesWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "wordpress {deleteCategory} integration test.")
    public void testDeleteCategoryWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:deleteCategory");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/categories/slug:" + parametersMap.get("categoryMandatory");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteCategory_mandatory.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("success"), "true");
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for deleteCategory method with Optional parameters.
     */
    @Test(dependsOnMethods = {"testGetCategoriesWithOptionalParameters"}, groups = {"wso2.esb"}, description = "wordpress {deleteCategory} integration test.")
    public void testDeleteCategoryWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:deleteCategory");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/categories/slug:" + parametersMap.get("categoryOptional");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteCategory_optional.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("code"), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("body").get("success"), "true");
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Negative test case for deleteCategory method.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {deleteCategory} integration test.")
    public void testDeleteCategoryWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:deleteCategory");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/categories/slug:*/delete";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteCategory_negative.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));

    }

    /**
     * Positive test case for insertTag method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {insertTag} integration test with mandatory parameters.")
    public void testInsertTagWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:insertTag");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_insertTag_mandatory.txt");
        parametersMap.put("tagSlugMandatory", esbRestResponse.getBody().getString("slug"));

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/tags/slug:" + parametersMap.get("tagSlugMandatory");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("ID"), apiRestResponse.getBody().get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("slug"), apiRestResponse.getBody().get("slug"));

    }

    /**
     * Positive test case for insertTag method with optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {insertTag} integration test with optional parameters.")
    public void testInsertTagWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:insertTag");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_insertTag_optional.txt");
        parametersMap.put("tagSlugOptional", esbRestResponse.getBody().getString("slug"));

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/tags/slug:" + parametersMap.get("tagSlugOptional");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("ID"), apiRestResponse.getBody().get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("slug"), apiRestResponse.getBody().get("slug"));
        Assert.assertEquals(esbRestResponse.getBody().get("description"), apiRestResponse.getBody().get("description"));
    }

    /**
     * Negative test case for insertTag method.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {insertTag} integration test negative.")
    public void testInsertTagWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:insertTag");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_insertTag_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/tags/new";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_insertTag_negative.txt");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
    }

    /**
     * Positive test case for editTag method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testInsertTagWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "wordpress {editTag} integration test with mandatory parameters.")
    public void testEditTagWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:editTag");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editTag_mandatory.txt", parametersMap);

        parametersMap.put("tagEditedSlugMandatory", esbRestResponse.getBody().getString("slug"));

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/tags/slug:" + parametersMap.get("tagEditedSlugMandatory");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("ID"), apiRestResponse.getBody().get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("slug"), apiRestResponse.getBody().get("slug"));
    }

    /**
     * Positive test case for editTag method with optional parameters.
     */
    @Test(dependsOnMethods = {"testInsertTagWithOptionalParameters"}, groups = {"wso2.esb"}, description = "wordpress {editTag} integration test with optional parameters.")
    public void testEditTagWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:editTag");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editTag_optional.txt", parametersMap);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/tags/slug:" + esbRestResponse.getBody().getString("slug");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("ID"), apiRestResponse.getBody().get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("slug"), apiRestResponse.getBody().get("slug"));
        Assert.assertEquals(esbRestResponse.getBody().get("description"), apiRestResponse.getBody().get("description"));
    }

//    /**
//     * Negative test case for editTag method.
//     */
//    @Test(groups = {"wso2.esb"}, description = "wordpress {editTag} integration test with negative parameters.")
//    public void testEditTagWithNegativeParameters() throws Exception {
//
//        esbRequestHeadersMap.put("Action", "urn:editTag");
//        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editTag_negative.txt");
//
//        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/tags/slug:*";
//        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
//
//        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
//        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
//        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
//    }
//
    /**
     * Positive test case for subscribeTag with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testEditTagWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "wordpress {subscribeTag} integration test with mandatory parameters.")
    public void testSubscribeTagsWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:subscribeTag");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_subscribeTag_mandatory.txt", parametersMap);

        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/read/tags/" + parametersMap.get("tagSlugMandatory") + "/mine";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("subscribed"), apiRestResponse.getBody().get("subscribed"));
    }
//
    /**
     * Positive test case for subscribeTag with optional parameters.
     */
    @Test(dependsOnMethods = {"testUnsubscribeTagsWithNegativeCase"}, groups = {"wso2.esb"}, description = "wordpress {subscribeTag} integration test with optional parameters.")
    public void testSubscribeTagsWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:subscribeTag");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_subscribeTag_optional.txt", parametersMap);

        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/read/tags/" + parametersMap.get("tagSlugMandatory") + "/mine?fields=subscribed";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("subscribed"), true);
    }

    /**
     * Negative test case for subscribeTag method.
     */
    @Test(dependsOnMethods = {"testSubscribeTagsWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "wordpress {subscribeTag} integration test with negative case.")
    public void testSubscribeTagsWithNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:subscribeTag");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_subscribeTag_negative.txt", parametersMap);

        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/read/tags/" + parametersMap.get("tagSlugMandatory") + "/mine/new";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
    }


    /**
     * Positive test case for unsubscribeTag with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testSubscribeTagsWithNegativeCase"}, groups = {"wso2.esb"}, description = "wordpress {unsubscribeTag} integration test with mandatory parameters.")
    public void testUnsubscribeTagsWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:unsubscribeTag");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_unsubscribeTag_mandatory.txt", parametersMap);

        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/read/tags/" + parametersMap.get("tagSlugMandatory") + "/mine";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("subscribed"), apiRestResponse.getBody().get("subscribed"));
    }

    /**
     * Negative test case for unsubscribeTag method.
     */
    @Test(dependsOnMethods = {"testUnsubscribeTagsWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "wordpress {unsubscribeTag} integration test with negative case.")
    public void testUnsubscribeTagsWithNegativeCase() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:unsubscribeTag");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_unsubscribeTag_negative.txt", parametersMap);

        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/read/tags/" + parametersMap.get("tagSlugMandatory") + "/mine/delete";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
    }

    /**
     * Positive test case for unsubscribeTag with optional parameters.
     */
    @Test(dependsOnMethods = {"testSubscribeTagsWithOptionalParameters"}, groups = {"wso2.esb"}, description = "wordpress {unsubscribeTag} integration test with optional parameters.")
    public void testUnsubscribeTagsWithOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:unsubscribeTag");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_unsubscribeTag_optional.txt", parametersMap);

        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/read/tags/" + parametersMap.get("tagSlugMandatory") + "/mine?fields=subscribed";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("subscribed"), apiRestResponse.getBody().get("subscribed"));
    }

    /**
     * Positive test case for getTags method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testUnsubscribeTagsWithOptionalParameters"}, groups = {"wso2.esb"}, description = "wordpress {getTags} integration test with mandatory parameters.")
    public void testGetTagsWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getTags");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/tags";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTags_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        String tagSlugMandatoryToDelete = esbRestResponse.getBody().getJSONArray("tags").getJSONObject(0).get("slug").toString();
        parametersMap.put("tagSlugMandatoryToDelete", tagSlugMandatoryToDelete);
        Assert.assertEquals(esbRestResponse.getBody().get("found"), apiRestResponse.getBody().get("found"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tags").length(), apiRestResponse.getBody().getJSONArray("tags").length());

    }

    /**
     * Positive test case for getTags method with optional parameters.
     */
    @Test(dependsOnMethods = {"testUnsubscribeTagsWithOptionalParameters", "testDeleteTagWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "wordpress {getTags} integration test with mandatory parameters.")
    public void testGetTagsWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getTags");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/tags";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTags_optional.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "api_getTags_optional.txt");
        String tagSlugOptionalToDelete = esbRestResponse.getBody().getJSONArray("tags").getJSONObject(0).get("slug").toString();
        parametersMap.put("tagSlugOptionalToDelete", tagSlugOptionalToDelete);
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%\n"+tagSlugOptionalToDelete);

        Assert.assertEquals(esbRestResponse.getBody().get("found"), apiRestResponse.getBody().get("found"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tags").length(), apiRestResponse.getBody().getJSONArray("tags").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tags").getJSONObject(0).get("ID"), apiRestResponse.getBody().getJSONArray("tags").getJSONObject(0).get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tags").getJSONObject(0).get("name"), apiRestResponse.getBody().getJSONArray("tags").getJSONObject(0).get("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tags").getJSONObject(0).get("slug"), apiRestResponse.getBody().getJSONArray("tags").getJSONObject(0).get("slug"));

    }
//
//    /**
//     * Negative test case for getTags method.
//     */
//    @Test(groups = {"wso2.esb"}, description = "wordpress {getTags} integration test with negative parameters.")
//    public void testGetTagsWithNegativeParameters() throws Exception {
//
//        esbRequestHeadersMap.put("Action", "urn:getTags");
//        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "imirshadhassan.com/tags";
//
//        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTags_negative.txt", parametersMap);
//        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
//
//        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
//        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
//        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
//
//    }

    /**
     * Positive test case for deleteTag method with Mandatory parameters.
     */
    @Test(dependsOnMethods = {"testGetTagsWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "wordpress {deleteTag} integration test.")
    public void testDeleteTagWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:deleteTag");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/tags/slug:" + parametersMap.get("tagSlugMandatoryToDelete");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteTag_mandatory.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for deleteTag method with Optional parameters.
     */
    @Test(dependsOnMethods = {"testGetTagsWithOptionalParameters"}, priority = 2, groups = {"wso2.esb"}, description = "wordpress {deleteTag} integration test.")
    public void testDeleteTagWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:deleteTag");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/tags/slug:" + parametersMap.get("tagSlugOptionalToDelete");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteTag_optional.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Negative test case for deleteTag method.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {deleteTag} integration test.")
    public void testDeleteTagWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:deleteTag");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/tags/slug:*/delete";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteTag_negative.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));

    }

    /**
     * Positive test case for getFollowingBlogPosts with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getFollowingBlogPosts} integration test with mandatory parameters.")
    public void testGetFollowingBlogPostsWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getFollowingBlogPosts");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFollowingBlogPosts_mandatory.txt");

        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/read/following/";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("number"), apiRestResponse.getBody().get("number"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("posts").getJSONObject(0).get("ID"), apiRestResponse.getBody().getJSONArray("posts").getJSONObject(0).get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("posts").getJSONObject(0).getJSONObject("author").get("ID"),
                apiRestResponse.getBody().getJSONArray("posts").getJSONObject(0).getJSONObject("author").get("ID"));

    }

    /**
     * Positive test case for getFollowingBlogPosts with optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getFollowingBlogPosts} integration test with optional parameters.")
    public void testGetFollowingBlogPostsWithOptionalPrameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getFollowingBlogPosts");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFollowingBlogPosts_optional.txt");

        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/read/following/"
                + "?fields=number,posts,ID,author,date&number=2&page=1&order=ASC";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("number"), apiRestResponse.getBody().get("number"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("posts").getJSONObject(0).get("ID"),
                apiRestResponse.getBody().getJSONArray("posts").getJSONObject(0).get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("posts").getJSONObject(0).getJSONObject("author").get("ID"),
                apiRestResponse.getBody().getJSONArray("posts").getJSONObject(0).getJSONObject("author").get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("posts").getJSONObject(0).get("date"),
                apiRestResponse.getBody().getJSONArray("posts").getJSONObject(0).get("date"));

    }

    /**
     * Negative test case for getFollowingBlogPosts method.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getFollowingBlogPosts} integration test with negative case.")
    public void testGetFollowingBlogPostsWithNegativeCase() throws Exception {

        /**
         * Passing a negative access token with negativeRequestHeadersMap to the apiRestResponse
         */
        Map<String, String> negativeRequestHeadersMap = new HashMap<String, String>();
        negativeRequestHeadersMap.put("Authorization", "Bearer " + "nskso23!");

        esbRequestHeadersMap.put("Action", "urn:getFollowingBlogPosts");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFollowingBlogPosts_negative.txt");

        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/read/following/"
                + "?fields=number,posts,ID,author,date&number=2&page=1&order=ASC";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", negativeRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));

    }

    /**
     * Positive test case for followBlog method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {followBlog} integration test with Mandatory Parametors.")
    public void testFollowBlogWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:followBlog");

        parametersMap.put("blog", "jakegoldman.me");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + parametersMap.get("blog") + "/follows/mine";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_followBlog_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("is_following"), apiRestResponse.getBody().get("is_following"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").getJSONObject("links").get("site"), apiRestResponse.getBody().getJSONObject("meta").getJSONObject("links").get("site"));

    }

    /**
     * Positive test case for followBlog method with optional parameters.
     */
    @Test(dependsOnMethods = {"testFollowBlogWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "wordpress {followBlog} integration test with Optional Parametors.")
    public void testFollowBlogWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:followBlog");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + parametersMap.get("blog") + "/follows/mine?fields=is_following";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_followBlog_optional.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("is_following"), apiRestResponse.getBody().get("is_following"));
        Assert.assertEquals(esbRestResponse.getBody().length(), apiRestResponse.getBody().length());

    }

    /**
     * Negative test case for followBlog method.
     */
    @Test(dependsOnMethods = {"testFollowBlogWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "wordpress {followBlog} integration test with Optional Parametors.")
    public void testFollowBlogWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:followBlog");
        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/sites/google.lk/follows/new";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_followBlog_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
    }

    /**
     * Positive test case for unFollowBlog method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testFollowBlogWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "wordpress {unFollowBlog} integration test with Mandatory Parametors.")
    public void testUnFollowBlogWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:unFollowBlog");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + parametersMap.get("blog") + "/follows/mine";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_unFollowBlog_mandatory.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("is_following"), apiRestResponse.getBody().get("is_following"));

    }

    /**
     * Positive test case for unFollowBlog method with optional parameters.
     */
    @Test(dependsOnMethods = {"testFollowBlogWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "wordpress {followBlog} integration test with Optional Parametors.")
    public void testUnFollowBlogWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:unFollowBlog");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + parametersMap.get("blog") + "/follows/mine?fields=is_following";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_unFollowBlog_optional.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("is_following"), apiRestResponse.getBody().get("is_following"));
        Assert.assertEquals(esbRestResponse.getBody().length(), apiRestResponse.getBody().length());

    }

    /**
     * Negative test case for unFollowBlog method.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {unFollowBlog} integration test with Optional Parametors.")
    public void testUnFollowBlogWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:unFollowBlog");
        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/sites/google.lk/follows/mine/delete";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_unFollowBlog_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
    }

    /**
     * Positive test case for getFollowers method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getFollowers} integration test with Mandatory Parametors.")
    public void testGetFollowersWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getFollowers");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/follows/";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFollowers_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("found"), apiRestResponse.getBody().get("found"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("users").length(), apiRestResponse.getBody().getJSONArray("users").length());

    }

    /**
     * Positive test case for getFollowers method with optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getFollowers} integration test with Optional Parametors.")
    public void testGetFollowersWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getFollowers");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/follows/";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFollowers_optional.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "api_getFollowers_optional.txt");

        Assert.assertEquals(esbRestResponse.getBody().get("found"), apiRestResponse.getBody().get("found"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("users").length(), apiRestResponse.getBody().getJSONArray("users").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("users").getJSONObject(0).get("ID"), apiRestResponse.getBody().getJSONArray("users").getJSONObject(0).get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("users").getJSONObject(0).get("login"), apiRestResponse.getBody().getJSONArray("users").getJSONObject(0).get("login"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("users").getJSONObject(0).get("email"), apiRestResponse.getBody().getJSONArray("users").getJSONObject(0).get("email"));
    }

    /**
     * Negative test case for getFollowers method.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getFollowers} integration test with Negative Parametors.")
    public void testGetFollowersWithNegativeParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getFollowers");
        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/sites/imirshadhassan.com/follows/";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFollowers_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));

    }

    /**
     * Positive test case for getSiteInfo with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getSiteInfo} integration test with mandatory parameters.")
    public void testGetSiteInfoWithMandatoryParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getSiteInfo");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSiteInfo_mandatory.txt");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("post_count"), apiRestResponse.getBody().get("post_count"));
        Assert.assertEquals(esbRestResponse.getBody().get("description"), apiRestResponse.getBody().get("description"));
        Assert.assertEquals(esbRestResponse.getBody().get("URL"), apiRestResponse.getBody().get("URL"));
    }

    /**
     * Positive test case for getSiteInfo method with optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getSiteInfo} integration test with optional parameters.")
    public void testGetSiteInfoWithOptionalParameters() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getSiteInfo");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain")
                + "?meta=self,posts,comments&fields=ID,name,URL,meta";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSiteInfo_optional.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("ID"), apiRestResponse.getBody().get("ID"));
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("URL"), apiRestResponse.getBody().get("URL"));

    }

    /**
     * Negative test case for getSiteInfo method.
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getSiteInfo} integration test with negative case.")
    public void testGetSiteInfoWithNegativeCase() throws Exception {

        esbRequestHeadersMap.put("Action", "urn:getSiteInfo");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "non-exist.wordpress.com" + "?meta=self,TEST&fields=ID,TEST";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSiteInfo_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));

    }

    /**
     * Positive test case for getStats with mandatory parameters
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getStats} integration test with mandatory parameters")
    public void testGetStatsWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getStats");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getStats_mandatory.txt");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/stats";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("day"), apiRestResponse.getBody().get("day"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("stats").get("visitors_today"), apiRestResponse.getBody().getJSONObject("stats").get("visitors_today"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("stats").get("views_best_day"), apiRestResponse.getBody().getJSONObject("stats").get("views_best_day"));

    }

    /**
     * Positive test case for getStats with optional parameters
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getStats} integration test with optional parameters")
    public void testGetStatsWithOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getStats");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getStats_optional.txt");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/stats"
                + "?fields=stats,day";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("day"), apiRestResponse.getBody().get("day"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("stats").get("visitors_today"), apiRestResponse.getBody().getJSONObject("stats").get("visitors_today"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("stats").get("views_best_day"), apiRestResponse.getBody().getJSONObject("stats").get("views_best_day"));

    }

    /**
     * Negative test case for getStats method
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getStats} integration test with negative case")
    public void testGetStatsWithNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getStats");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getStats_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "non-exist.wordpress.com" + "/stats?fields=stats,day";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
    }

    /**
     * Positive test case for getVisitorsCount with mandatory parameters
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getVisitorsCount} integration test with mandatory parameters")
    public void testGetVisitorsCountWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getVisitorsCount");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getVisitorsCount_mandatory.txt");

        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/sites/" + connectorProperties.getProperty("domain") + "/stats/visits";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("unit"), apiRestResponse.getBody().get("unit"));
        //Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data"), apiRestResponse.getBody().getJSONArray("data"));
    }


    /**
     * Positive test case for getVisitorsCount with optional parameters
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getVisitorsCount} integration test with optional parameters")
    public void testGetVisitorsCountWithOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getVisitorsCount");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getVisitorsCount_optional.txt");

        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/sites/" + connectorProperties.getProperty("domain") + "/stats/visits"
                + "?unit=day&quantity=1&fields=unit,data";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("unit"), apiRestResponse.getBody().get("unit"));
    }


    /**
     * Negative test case for getVisitorsCount method
     */
    @Test(description = "wordpress {getVisitorsCount} integration test with negative case")
    public void testGetVisitorsCountWithNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getVisitorsCount");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getVisitorsCount_negative.txt");

        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/sites/" + "non-exist.wordpress.com" + "/stats/visits"
                + "?unit=month&quantity=1&fields=unit,data";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
    }

    /**
     * Positive test case for getSearchTerms with mandatory parameters
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getSearchTerms} integration test with mandatory parameters")
    public void testgetSearchTermsWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getSearchTerms");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSearchTerms_mandatory.txt");

        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/sites/" + connectorProperties.getProperty("domain") + "/stats/search-terms";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("days"), apiRestResponse.getBody().get("days"));
        Assert.assertEquals(esbRestResponse.getBody().get("date"), apiRestResponse.getBody().get("date"));

    }

    /**
     * Positive test case for getSearchTerms with optional parameters
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getSearchTerms} integration test with mandatory parameters")
    public void testgetSearchTermsWithOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getSearchTerms");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSearchTerms_optional.txt");

        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/sites/" + connectorProperties.getProperty("domain") + "/stats/search-terms"
                + "?date=2014-07-08&days=1&fields=days,date";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("days"), apiRestResponse.getBody().get("days"));
        Assert.assertEquals(esbRestResponse.getBody().get("date"), apiRestResponse.getBody().get("date"));
    }

    /**
     * Negative test case for getSearchTerms method
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getSearchTerms} integration test with negative case")
    public void testgetSearchTermsWithNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getSearchTerms");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSearchTerms_negative.txt");

        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/sites/" + "non-exist.wordpress.com" + "/stats/search-terms"
                + "?date=2014-07-08";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
    }

    /**
     * Positive test case for getViewsByCountry with mandatory parameters
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getViewsByCountry} integration test with mandatory parameters")
    public void testGetViewsByCountryWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getViewsByCountry");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getViewsByCountry_mandatory.txt");

        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/sites/" + connectorProperties.getProperty("domain") + "/stats/country-views";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("date"), apiRestResponse.getBody().get("date"));
    }

    /**
     * Positive test case for getViewsByCountry with optional parameters
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getViewsByCountry} intgration test with optional parameters")
    public void testGetViewsByCountryWithOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getViewsByCountry");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getViewsByCountry_optional.txt");

        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/sites/" + connectorProperties.getProperty("domain") + "/stats/country-views"
                + "?fields=date,country-views&date=2014-07-08";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("date"), apiRestResponse.getBody().get("date"));

    }

    /**
     * Negative test case for getViewsByCountry method
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {getViewsByCountry} intgration test with negative case")
    public void testGetViewsByCountryWithNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:getViewsByCountry");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getViewsByCountry_negative.txt");

        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/sites/" + "non-exist.wordpress.com" + "/stats/country-views"
                + "?fields=date,country-views&date=2014-07-08";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
    }

    /**
     * Positive test case for deletePost with mandatory parameters.
     */
    @Test(priority = 10, groups = {"wso2.esb"}, description = "wordpress {deletePost} integration test with mandatory parameters.")
    public void testDeletePostWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:deletePost");

        //sending the post to trash
        RestResponse<JSONObject> esbRestResponse1 = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deletePost_mandatory.txt", parametersMap);

        //Removing the post from the trash
        RestResponse<JSONObject> esbRestResponse2 = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deletePost_mandatory.txt", parametersMap);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/" + parametersMap.get("postId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse1.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);

    }

    /**
     * Positive test case for deletePost with optional parameters.
     */
    @Test(priority = 11, groups = {"wso2.esb"}, description = "wordpress {deletePost} integration test with mandatory parameters.")
    public void testDeletePostWithOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:deletePost");

        //sending the post to trash
        RestResponse<JSONObject> esbRestResponse1 = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deletePost_optional.txt", parametersMap);

        //Removing the post from the trash
        RestResponse<JSONObject> esbRestResponse2 = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deletePost_optional.txt", parametersMap);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/" + parametersMap.get("postIdOptional");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse1.getBody().get("code"), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);

    }

    /**
     * Negative test case for deletePost.
     */
    @Test(priority = 12, groups = {"wso2.esb"}, description = "wordpress {deletePost} integration test with mandatory parameters.")
    public void testDeletePostWithNegativeParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:deletePost");

        //Removing the post from the trash
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deletePost_negative.txt");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/10000/delete";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());

    }

    /**
     * Positive test case for batchRequest with mandatory parameters
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {batchRequest} integration test with mandatory parameters")
    public void testBatchRequestWithMandatoryParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:batchRequest");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_batchRequest_mandatory.txt");

        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/batch"
                + "?urls[]=/sites/" + connectorProperties.getProperty("domain") + "&urls[]=/read/following";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        String siteEndPoint = "/sites/" + connectorProperties.getProperty("domain");
        Assert.assertEquals
                (esbRestResponse.getBody().getJSONObject("/read/following").getJSONArray("posts").getJSONObject(0).get("title"),
                        apiRestResponse.getBody().getJSONObject("/read/following").getJSONArray("posts").getJSONObject(0).get("title"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject(siteEndPoint).get("URL"),
                apiRestResponse.getBody().getJSONObject(siteEndPoint).get("URL"));

    }

    /**
     * Positive test case for batchRequest with optional parameters
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {batchRequest} integration test with optional parameters")
    public void testBatchRequestWithOptionalParameters() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:batchRequest");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_batchRequest_optional.txt");

        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/batch"
                + "?urls[]=/sites/" + connectorProperties.getProperty("domain") + "&urls[]=/read/following"
                + "&meta=self,posts";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        String siteEndPoint = "/sites/" + connectorProperties.getProperty("domain");

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject(siteEndPoint).getJSONObject("meta").getJSONObject("data").getJSONObject("self").get("ID"),
                apiRestResponse.getBody().getJSONObject(siteEndPoint).getJSONObject("meta").getJSONObject("data").getJSONObject("self").get("ID"));

    }


    /**
     * Negative test case for batchRequest method
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {batchRequest} integration test with negative case")
    public void testBatchRequestWithNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:batchRequest");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_batchRequest_negative.txt");

        String apiEndPoint = "https://public-api.wordpress.com/rest/v1/batch?"
                + "&urls[]=/sites/non-exist.wordpress.com"
                + "&meta=self,posts";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("/sites/non-exist.wordpress.com").get("status_code"),
                apiRestResponse.getBody().getJSONObject("/sites/non-exist.wordpress.com").get("status_code"));


        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("/sites/non-exist.wordpress.com").get("status_code"), 404);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("/sites/non-exist.wordpress.com").get("status_code"), 404);

    }

    /**
     * Positive test case for createImagePost .
     */
    @Test(groups = {"wso2.esb"}, description = "wordpress {createImagePost} integration test with mandatory parameters.")
    public void testCreateImagePostWithMandatoryParameters() throws Exception {

        headersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("accessToken"));

        headersMap.put("accessToken", connectorProperties.getProperty("accessToken"));
        headersMap.put("domain", connectorProperties.getProperty("domain"));

        headersMap.put("Action", "urn:createImagePost");

        MultipartFormdataProcessor multipartProcessor =
                new MultipartFormdataProcessor(multipartProxyUrl, headersMap);


        multipartProcessor.addFormDataToRequest("title", "Post with Images");
        multipartProcessor.addFileToRequest("media[]", connectorProperties.getProperty("uploadFileName"), null, connectorProperties.getProperty("targetFileName"));

        RestResponse<JSONObject> esbRestResponse = multipartProcessor.processForJsonResponse();
        parametersMap.put("imagePostId", esbRestResponse.getBody().getString("ID"));

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/" + parametersMap.get("imagePostId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertTrue(apiRestResponse.getBody().has("ID"));

    }

    /**
     * Positive test case for editImagePost .
     */
    @Test(dependsOnMethods = {"testCreateImagePostWithMandatoryParameters"}, groups = {"wso2.esb"}, description = "wordpress {editImagePost} integration test with mandatory parameters.")
    public void testEditImagePostWithMandatoryParameters() throws Exception {

        headersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("accessToken"));

        headersMap.put("accessToken", connectorProperties.getProperty("accessToken"));
        headersMap.put("domain", connectorProperties.getProperty("domain"));
        headersMap.put("post_id", parametersMap.get("imagePostId"));

        headersMap.put("Action", "urn:editImagePost");

        MultipartFormdataProcessor multipartProcessor =
                new MultipartFormdataProcessor(multipartProxyUrl, headersMap);


        multipartProcessor.addFormDataToRequest("title", "Edit Post with Images");
        multipartProcessor.addFileToRequest("media[]", connectorProperties.getProperty("editUploadFileName"), null, connectorProperties.getProperty("editTargetFileName"));

        RestResponse<JSONObject> esbRestResponse = multipartProcessor.processForJsonResponse();

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/" + parametersMap.get("imagePostId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("ID"), apiRestResponse.getBody().get("ID"));

    }

    /*
     *delete the Image Post
     */
    @AfterClass(alwaysRun = true)
    public void cleanUp() throws Exception {

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("domain") + "/posts/" + parametersMap.get("imagePostId") + "/delete";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
    }

}
