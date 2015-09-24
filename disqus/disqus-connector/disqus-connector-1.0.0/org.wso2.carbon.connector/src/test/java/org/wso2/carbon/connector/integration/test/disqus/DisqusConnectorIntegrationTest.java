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

package org.wso2.carbon.connector.integration.test.disqus;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class DisqusConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("disqus-connector-1.0.0");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
    }
    
    /**
     * Test createThread method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "disqus {createThread} integration test with mandatory parameters")
    public void testCreateThreadWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createThread");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createThread_mandatory.json");
        
        String threadId = esbRestResponse.getBody().getJSONObject("response").getString("id");
        connectorProperties.put("threadId", threadId);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/threads/details.json?thread=" + threadId
                        + "&api_key=" + connectorProperties.getProperty("apiKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("title"),
                apiRestResponse.getBody().getJSONObject("response").getString("title"));
        Assert.assertEquals(sdf.format(new Date()),
                apiRestResponse.getBody().getJSONObject("response").getString("createdAt").split("T")[0]);
        
    }
    
    /**
     * Test createThread method with Optional Parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "disqus {createThread} integration test with optional parameters")
    public void testCreateThreadWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createThread");
        
        connectorProperties.put("slug", getSlugName());
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createThread_optional.json");
        
        String threadIdOptional = esbRestResponse.getBody().getJSONObject("response").getString("id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/threads/details.json?thread=" + threadIdOptional
                        + "&api_key=" + connectorProperties.getProperty("apiKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("title"),
                apiRestResponse.getBody().getJSONObject("response").getString("title"));
        Assert.assertEquals(connectorProperties.getProperty("message"),
                apiRestResponse.getBody().getJSONObject("response").getString("raw_message"));
        Assert.assertEquals(connectorProperties.getProperty("slug"), apiRestResponse.getBody()
                .getJSONObject("response").getString("slug"));
        Assert.assertEquals(sdf.format(new Date()),
                apiRestResponse.getBody().getJSONObject("response").getString("createdAt").split("T")[0]);
        
    }
    
    /**
     * Test createThread method with Negative case.
     */
    @Test(groups = { "wso2.esb" }, description = "disqus {createThread} integration test with negative case.")
    public void testCreateThreadWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createThread");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createThread_negative.json");
        
        apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/threads/create.json" + "?api_key="
                        + connectorProperties.getProperty("apiKey") + "&access_token="
                        + connectorProperties.getProperty("accessToken") + "&title=SampleTitle&forum=INVALID";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createThread_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("response"),
                apiRestResponse.getBody().getString("response"));
        
    }
    
    /**
     * Test getThreadDetails method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateThreadWithMandatoryParameters" }, description = "disqus {getThreadDetails} integration test with mandatory parameters")
    public void testGetThreadDetailsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getThreadDetails");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getThreadDetails_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/threads/details.json?thread="
                        + connectorProperties.getProperty("threadId") + "&api_key="
                        + connectorProperties.getProperty("apiKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("response").getString("title"), esbRestResponse
                .getBody().getJSONObject("response").getString("title"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("response").getString("category"), esbRestResponse
                .getBody().getJSONObject("response").getString("category"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("response").getString("author"), esbRestResponse
                .getBody().getJSONObject("response").getString("author"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("response").getString("posts"), esbRestResponse
                .getBody().getJSONObject("response").getString("posts"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("response").getString("forum"), esbRestResponse
                .getBody().getJSONObject("response").getString("forum"));
    }
    
    /**
     * Test getThreadDetails method with Optional Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateThreadWithMandatoryParameters" }, description = "disqus {getThreadDetails} integration test with optional parameters")
    public void testGetThreadDetailsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getThreadDetails");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getThreadDetails_optional.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/threads/details.json?thread="
                        + connectorProperties.getProperty("threadId") + "&api_key="
                        + connectorProperties.getProperty("apiKey") + "&related=forum";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("response").getJSONObject("forum")
                .getString("name"), esbRestResponse.getBody().getJSONObject("response").getJSONObject("forum")
                .getString("name"));
        
        Assert.assertEquals(
                apiRestResponse.getBody().getJSONObject("response").getJSONObject("forum").getString("language"),
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("forum").getString("language"));
        
        Assert.assertEquals(
                apiRestResponse.getBody().getJSONObject("response").getJSONObject("forum").getString("founder"),
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("forum").getString("founder"));
        
        Assert.assertEquals(
                apiRestResponse.getBody().getJSONObject("response").getJSONObject("forum").getString("founder"),
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("forum").getString("founder"));
        
    }
    
    /**
     * Test getThreadDetails method with Negative Case.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateThreadWithMandatoryParameters" }, description = "disqus {getThreadDetails} integration test with negative case.")
    public void testGetThreadDetailsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getThreadDetails");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getThreadDetails_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/threads/details.json?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getString("code"), esbRestResponse.getBody().getString("code"));
        Assert.assertEquals(apiRestResponse.getBody().getString("response"),
                esbRestResponse.getBody().getString("response"));
        
    }
    
    /**
     * Test listThreads method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateThreadWithMandatoryParameters" }, description = "disqus {listThreads} integration test with mandatory parameters")
    public void testListThreadsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listThreads");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listThreads_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/trends/listThreads.json?api_key="
                        + connectorProperties.getProperty("apiKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("response");
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("response");
        
        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        
        if (esbResponseArray.length() > 0) {
            
            Assert.assertEquals(apiResponseArray.getJSONObject(0).getJSONObject("thread").getString("category"),
                    esbResponseArray.getJSONObject(0).getJSONObject("thread").getString("category"));
            Assert.assertEquals(apiResponseArray.getJSONObject(0).getJSONObject("thread").getString("author"),
                    esbResponseArray.getJSONObject(0).getJSONObject("thread").getString("author"));
            Assert.assertEquals(apiResponseArray.getJSONObject(0).getJSONObject("thread").getString("id"),
                    esbResponseArray.getJSONObject(0).getJSONObject("thread").getString("id"));
            
        } else {
            Assert.fail("There are no any Threads to list.");
        }
        
    }
    
    /**
     * Test listThreads method with optional Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateThreadWithMandatoryParameters" }, description = "disqus {listThreads} integration test with optional parameters")
    public void testListThreadsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listThreads");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listThreads_optional.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl")
                        + "/api/3.0/trends/listThreads.json?related=forum&related=author&api_key="
                        + connectorProperties.getProperty("apiKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("response");
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("response");
        
        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        
        if (esbResponseArray.length() > 0) {
            
            Assert.assertEquals(
                    apiResponseArray.getJSONObject(0).getJSONObject("thread").getJSONObject("forum")
                            .getString("category"),
                    esbResponseArray.getJSONObject(0).getJSONObject("thread").getJSONObject("forum")
                            .getString("category"));
            Assert.assertEquals(apiResponseArray.getJSONObject(0).getJSONObject("thread").getJSONObject("forum")
                    .getString("name"), esbResponseArray.getJSONObject(0).getJSONObject("thread")
                    .getJSONObject("forum").getString("name"));
            Assert.assertEquals(apiResponseArray.getJSONObject(0).getJSONObject("thread").getJSONObject("author")
                    .getString("id"), esbResponseArray.getJSONObject(0).getJSONObject("thread").getJSONObject("author")
                    .getString("id"));
            
        } else {
            Assert.fail("There are no any Threads to list according to given parameters.");
        }
        
    }
    
    /**
     * Test listThreads method with Negative Case.
     */
    @Test(groups = { "wso2.esb" }, description = "disqus {listThreads} integration test with negative case.")
    public void testListThreadsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listThreads");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listThreads_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/trends/listThreads.json?related=invalid&api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getString("code"), esbRestResponse.getBody().getString("code"));
        Assert.assertEquals(apiRestResponse.getBody().getString("response"),
                esbRestResponse.getBody().getString("response"));
        
    }
    
    /**
     * Test subscribe method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateThreadWithMandatoryParameters" }, description = "disqus {subscribe} integration test with mandatory parameters")
    public void testSubscribeWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:subscribe");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_subscribe_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/threads/subscribe.json?thread="
                        + connectorProperties.getProperty("threadId") + "&email="
                        + connectorProperties.getProperty("apiSubscribeEmail") + "&api_key="
                        + connectorProperties.getProperty("apiKey");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_subscribe_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        
    }
    
    /**
     * Test subscribe method with Negative Case.
     */
    @Test(groups = { "wso2.esb" }, description = "disqus {subscribe} integration test with negative case.")
    public void testSubscribeWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:subscribe");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_subscribe_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/threads/subscribe.json?thread="
                        + connectorProperties.getProperty("threadId") + "&api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_subscribe_mandatory.json");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getString("code"), esbRestResponse.getBody().getString("code"));
        Assert.assertEquals(apiRestResponse.getBody().getString("response"),
                esbRestResponse.getBody().getString("response"));
        
    }
    
    /**
     * Test listPosts method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "disqus {listPosts} integration test with mandatory parameters")
    public void testListPostsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listPosts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPosts_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/posts/listPopular.json?api_key="
                        + connectorProperties.getProperty("apiKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("response");
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("response");
        
        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        
        if (esbResponseArray.length() > 0) {
            
            connectorProperties.put("postId", apiResponseArray.getJSONObject(0).getString("id"));
            
            Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("id"), esbResponseArray.getJSONObject(0)
                    .getString("id"));
            Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("forum"), esbResponseArray.getJSONObject(0)
                    .getString("forum"));
            Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("thread"), esbResponseArray
                    .getJSONObject(0).getString("thread"));
            Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("createdAt"), esbResponseArray
                    .getJSONObject(0).getString("createdAt"));
            
        } else {
            Assert.fail("There are no any Posts to list.");
        }
    }
    
    /**
     * Test listPosts method with Optional Parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "disqus {listPosts} integration test with optional parameters")
    public void testListPostsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listPosts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPosts_optional.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl")
                        + "/api/3.0/posts/listPopular.json?include=unapproved&order=best&api_key="
                        + connectorProperties.getProperty("apiKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("response");
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("response");
        
        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        
        if (esbResponseArray.length() > 0) {
            
            Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("forum"), esbResponseArray.getJSONObject(0)
                    .getString("forum"));
            Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("thread"), esbResponseArray
                    .getJSONObject(0).getString("thread"));
            Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("isApproved"), esbResponseArray
                    .getJSONObject(0).getString("isApproved"));
            Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("createdAt"), esbResponseArray
                    .getJSONObject(0).getString("createdAt"));
            Assert.assertEquals(false, esbResponseArray.getJSONObject(0).getBoolean("isApproved"));
            
        } else {
            Assert.fail("There are no any Posts to list according to given parameters.");
        }
        
    }
    
    /**
     * Test listPosts method with Negative Case.
     */
    @Test(groups = { "wso2.esb" }, description = "disqus {listPosts} integration test with negative case.")
    public void testListPostsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listPosts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPosts_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/posts/listPopular.json?&include=INVALID&api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getString("code"), esbRestResponse.getBody().getString("code"));
        Assert.assertEquals(apiRestResponse.getBody().getString("response"),
                esbRestResponse.getBody().getString("response"));
        
    }
    
    /**
     * Test for listCategories method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "disqus {listCategories} integration test with mandatory parameters")
    public void testListCategoriesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCategories");
        RestResponse<JSONObject> esbResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCategories_mandatory.json");
        
        JSONArray esbResponseArray = esbResponse.getBody().getJSONArray("response");
        String forum = esbResponseArray.getJSONObject(0).getString("forum");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/categories/list.json?forum="
                        + connectorProperties.getProperty("forumId") + "&api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("response");
        
        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        
        if (esbResponseArray.length() > 0) {
            Assert.assertEquals(forum, apiResponseArray.getJSONObject(0).getString("forum"));
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("title"), apiResponseArray.getJSONObject(0)
                    .getString("title"));
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                    .getString("id"));
            connectorProperties.put("categoryId", esbResponseArray.getJSONObject(0).getString("id"));
            
        } else {
            Assert.fail("There are no any Categories to list.");
        }
        
    }
    
    /**
     * Test for listCategories method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "disqus {listCategories} integration test with optional parameters")
    public void testListCategoriesWithOptionalParameters() throws IOException, JSONException, ParseException {
    
        esbRequestHeadersMap.put("Action", "urn:listCategories");
        
        connectorProperties.put("since", getUnixTimeStamp("sinceDate"));
        RestResponse<JSONObject> esbResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCategories_optional.json");
        
        JSONArray esbResponseArray = esbResponse.getBody().getJSONArray("response");
        String forum = esbResponseArray.getJSONObject(0).getString("forum");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/categories/list.json?forum="
                        + connectorProperties.getProperty("forumId") + "&order="
                        + connectorProperties.getProperty("order") + "&since_id="
                        + connectorProperties.getProperty("since") + "&api_key="
                        + connectorProperties.getProperty("apiKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("response");
        
        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        
        if (esbResponseArray.length() > 0) {
            Assert.assertEquals(forum, apiResponseArray.getJSONObject(0).getString("forum"));
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("title"), apiResponseArray.getJSONObject(0)
                    .getString("title"));
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("order"), apiResponseArray.getJSONObject(0)
                    .getString("order"));
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                    .getString("id"));
        } else {
            Assert.fail("There are no any Categories to list according to given parameters.");
        }
        
    }
    
    /**
     * Test for listCategories method with negative case.
     */
    @Test(groups = { "wso2.esb" }, description = "disqus {listCategories} integration test with negative case")
    public void testListCategoriesWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCategories");
        RestResponse<JSONObject> esbResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCategories_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/categories/list.json?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getString("code"), esbResponse.getBody().getString("code"));
        Assert.assertEquals(apiRestResponse.getBody().getString("response"), esbResponse.getBody()
                .getString("response"));
        
    }
    
    /**
     * Test getForumDetails method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "disqus {getForumDetails} integration test with mandatory parameters")
    public void testGetForumDetailsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getForumDetails");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getForumDetails_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/forums/details.json?forum="
                        + connectorProperties.getProperty("forumId") + "&api_key="
                        + connectorProperties.getProperty("apiKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("response").getString("language"), esbRestResponse
                .getBody().getJSONObject("response").getString("language"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("response").getString("name"), esbRestResponse
                .getBody().getJSONObject("response").getString("name"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("response").getString("founder"), esbRestResponse
                .getBody().getJSONObject("response").getString("founder"));
    }
    
    /**
     * Test getForumDetails method with Optional Parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "disqus {getForumDetails} integration test with optional parameters")
    public void testGetForumDetailsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getForumDetails");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getForumDetails_optional.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/forums/details.json?forum="
                        + connectorProperties.getProperty("forumId") + "&related=author&api_key="
                        + connectorProperties.getProperty("apiKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("response").getString("name"), esbRestResponse
                .getBody().getJSONObject("response").getString("name"));
        Assert.assertEquals(
                apiRestResponse.getBody().getJSONObject("response").getJSONObject("author").getString("name"),
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("author").getString("name"));
        Assert.assertEquals(
                apiRestResponse.getBody().getJSONObject("response").getJSONObject("author").getString("id"),
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("author").getString("id"));
    }
    
    /**
     * Test getForumDetails method with Negative Case.
     */
    @Test(groups = { "wso2.esb" }, description = "disqus {getForumDetails} integration test with negative case.")
    public void testGetForumDetailsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getForumDetails");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getForumDetails_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/forums/details.json?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getString("code"), esbRestResponse.getBody().getString("code"));
        Assert.assertEquals(apiRestResponse.getBody().getString("response"),
                esbRestResponse.getBody().getString("response"));
        
    }
    
    /**
     * Test for getCategoryDetails method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListCategoriesWithMandatoryParameters" }, description = "disqus {getCategoryDetails} integration test with mandatory parameters")
    public void testGetCategoryDetailsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getCategoryDetails");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCategoryDetails_mandatroy.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/categories/details.json?category="
                        + connectorProperties.getProperty("categoryId") + "&api_key="
                        + connectorProperties.getProperty("apiKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("response").getString("forum"), esbRestResponse
                .getBody().getJSONObject("response").getString("forum"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("response").getString("order"), esbRestResponse
                .getBody().getJSONObject("response").getString("order"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("response").getString("title"), esbRestResponse
                .getBody().getJSONObject("response").getString("title"));
        
    }
    
    /**
     * Test for getCategoryDetails method details with negative case.
     */
    @Test(groups = { "wso2.esb" }, description = "disqus {getCategoryDetails} integration test with the negative case")
    public void testGetCategoryDetailsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getCategoryDetails");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCategoryDetails_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/categories/details.json?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getString("code"), esbRestResponse.getBody().getString("code"));
        Assert.assertEquals(apiRestResponse.getBody().getString("response"),
                esbRestResponse.getBody().getString("response"));
        
    }
    
    /**
     * Test for getPost method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListPostsWithMandatoryParameters" }, description = "disqus {getPost} integration test with mandatory parameters")
    public void testGetPostWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getPost");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPost_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/posts/details.json?post="
                        + connectorProperties.getProperty("postId") + "&api_key="
                        + connectorProperties.getProperty("apiKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("response").getString("thread"), esbRestResponse
                .getBody().getJSONObject("response").getString("thread"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("response").getString("forum"), esbRestResponse
                .getBody().getJSONObject("response").getString("forum"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("response").getString("createdAt"), esbRestResponse
                .getBody().getJSONObject("response").getString("createdAt"));
        
    }
    
    /**
     * Test for getPost method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListPostsWithMandatoryParameters" }, description = "disqus {listCategories} integration test with optional parameters")
    public void testGetPostWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getPost");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPost_optional.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/posts/details.json?post="
                        + connectorProperties.getProperty("postId") + "&api_key="
                        + connectorProperties.getProperty("apiKey") + "&related=forum";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(
                apiRestResponse.getBody().getJSONObject("response").getJSONObject("forum").getString("category"),
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("forum").getString("category"));
        Assert.assertEquals(
                apiRestResponse.getBody().getJSONObject("response").getJSONObject("forum").getString("founder"),
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("forum").getString("founder"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("response").getJSONObject("forum").getString("id"),
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("forum").getString("id"));
    }
    
    /**
     * Test for getPost method with negative case.
     */
    @Test(groups = { "wso2.esb" }, description = "disqus {listCategories} integration test with negative case.")
    public void testGetPostWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getPost");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPost_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/3.0/posts/details.json?api_key="
                        + connectorProperties.getProperty("apiKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getString("code"), esbRestResponse.getBody().getString("code"));
        Assert.assertEquals(apiRestResponse.getBody().getString("response"),
                esbRestResponse.getBody().getString("response"));
    }
    
    /**
     * Builds a 5-character Slug name String .
     * 
     * @return String the randomly-generated SlugName
     */
    private String getSlugName() {
    
        char[] chars = "abcdefghijklmnop0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }
    
    /**
     * Generate Unix time stamp based on given property value.
     * 
     * @param String property key which needs to be convert.
     * @return String the generated time stamp.
     */
    private String getUnixTimeStamp(String propertyKey) throws ParseException {
    
        Date date = sdf.parse(connectorProperties.getProperty(propertyKey));
        
        return Long.toString(date.getTime() / 1000);
        
    }
    
}
