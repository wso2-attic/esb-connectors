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
package org.wso2.carbon.connector.integration.test.agilezen;

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


public class AgileZenConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap;
    
    private Map<String, String> apiRequestHeadersMap;
    
    private String apiRequestUrl;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        
        esbRequestHeadersMap = new HashMap<String, String>();
        
        apiRequestHeadersMap = new HashMap<String, String>();
        
        init("agilezen-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept", "application/json");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        apiRequestHeadersMap.put("X-Zen-ApiKey", connectorProperties.getProperty("apiKey"));
        apiRequestUrl = connectorProperties.getProperty("apiUrl") + "/api/v1";
        
    }
    
    /**
     * Positive test case for listProjects method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {listProjects} integration test with mandatory parameters.")
    public void testListProjectsWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listProjects");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjects_mandatory.json");
        
        connectorProperties.setProperty("projectId", esbRestResponse.getBody().getJSONArray("items").getJSONObject(0)
                        .getString("id"));
        
        final String apiEndPoint = apiRequestUrl + "/projects";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("page"), apiRestResponse.getBody().getString("page"));
        Assert.assertEquals(esbRestResponse.getBody().getString("pageSize"), apiRestResponse.getBody().getString(
                        "pageSize"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("totalItems"), apiRestResponse.getBody().getString(
                        "totalItems"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").length(), apiRestResponse.getBody()
                        .getJSONArray("items").length());
        
    }
    
    /**
     * Positive test case for listProjects method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {listProjects} integration test with optional parameters.")
    public void testListProjectsWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listProjects");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjects_optional.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects?enrichments=" + connectorProperties.getProperty("enrichments");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("page"), apiRestResponse.getBody().getString("page"));
        Assert.assertEquals(esbRestResponse.getBody().getString("pageSize"), apiRestResponse.getBody().getString(
                        "pageSize"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("totalItems"), apiRestResponse.getBody().getString(
                        "totalItems"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").length(), apiRestResponse.getBody()
                        .getJSONArray("items").length());
        
    }
    
    /**
     * Positive test case for getProject method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {getProject} integration test with mandatory parameters.", dependsOnMethods = { "testListProjectsWithMandatoryParameters" })
    public void testGetProjectWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getProject");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProject_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("description"), apiRestResponse.getBody().getString(
                        "description"));
        Assert.assertEquals(esbRestResponse.getBody().getString("createTime"), apiRestResponse.getBody().getString(
                        "createTime"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("owner").getString("id"), apiRestResponse.getBody()
                        .getJSONObject("owner").getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("owner").getString("name"), apiRestResponse
                        .getBody().getJSONObject("owner").getString("name"));
    }
    
    /**
     * Positive test case for getProject method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {getProject} integration test with optional parameters.", dependsOnMethods = { "testListProjectsWithMandatoryParameters" })
    public void testGetProjectWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getProject");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProject_optional.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "?with="
                                        + connectorProperties.getProperty("enrichments");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("description"), apiRestResponse.getBody().getString(
                        "description"));
        Assert.assertEquals(esbRestResponse.getBody().getString("createTime"), apiRestResponse.getBody().getString(
                        "createTime"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("members").length(), apiRestResponse.getBody()
                        .getJSONArray("members").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("owner").getString("id"), apiRestResponse.getBody()
                        .getJSONObject("owner").getString("id"));
        
    }
    
    /**
     * Positive test case for getProject method with negative case.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {getProject} integration test with negative case.", dependsOnMethods = { "testListProjectsWithMandatoryParameters" })
    public void testGetProjectWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getProject");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProject_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/projects/123456";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for updateProject method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {updateProject} integration test with optional parameters.", dependsOnMethods = { "testListProjectsWithMandatoryParameters" })
    public void testUpdateProjectWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateProject");
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "?with="
                                        + connectorProperties.getProperty("enrichments");
        
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateProject_optional.json");
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("name"), apiRestResponseAfter.getBody()
                        .getString("name"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("description"), apiRestResponseAfter.getBody()
                        .getString("description"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("details"), apiRestResponseAfter.getBody()
                        .getString("details"));
        
    }
    
    /**
     * Positive test case for updateProject method with negative case.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {updateProject} integration test with negative case.", dependsOnMethods = { "testListProjectsWithMandatoryParameters" })
    public void testUpdateProjectWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateProject");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateProject_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/projects/123456";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for createStory method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {createStory} integration test with mandatory parameters.", dependsOnMethods = { "testListProjectsWithMandatoryParameters" })
    public void testCreateStoryWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createStory");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createStory_mandatory.json");
        
        final String storyid = esbRestResponse.getBody().getString("id");
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                                        + storyid;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("text"), apiRestResponse.getBody().getString("text"));
        Assert.assertEquals(esbRestResponse.getBody().getString("color"), apiRestResponse.getBody().getString("color"));
        Assert.assertEquals(esbRestResponse.getBody().getString("size"), apiRestResponse.getBody().getString("size"));
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                        .getString("status"));
        
    }
    
    /**
     * Positive test case for createStory method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {createStory} integration test with optional parameters.", dependsOnMethods = { "testListProjectsWithMandatoryParameters" })
    public void testCreateStoryWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createStory");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createStory_optional.json");
        
        String storyId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("storyId", storyId);
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                                        + storyId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("text"), apiRestResponse.getBody().getString("text"));
        Assert.assertEquals(esbRestResponse.getBody().getString("color"), apiRestResponse.getBody().getString("color"));
        Assert.assertEquals(esbRestResponse.getBody().getString("size"), apiRestResponse.getBody().getString("size"));
        Assert.assertEquals(esbRestResponse.getBody().getString("priority"), apiRestResponse.getBody().getString(
                        "priority"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("creator").getString("id"), apiRestResponse
                        .getBody().getJSONObject("creator").getString("id"));
        
    }
    
    /**
     * Positive test case for createStory method with negative case.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {createStory} integration test with negative case.", dependsOnMethods = {
                    "testCreateStoryWithOptionalParameters", "testListProjectsWithMandatoryParameters" })
    public void testCreateStoryWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createStory");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createStory_negative.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/123456";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for getStory method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {getStory} integration test with mandatory parameters.", dependsOnMethods = { "testCreateStoryWithOptionalParameters" })
    public void testGetStoryWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getStory");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getStory_mandatory.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                                        + connectorProperties.getProperty("storyId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("text"), apiRestResponse.getBody().getString("text"));
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                        .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("size"), apiRestResponse.getBody().getString("size"));
        Assert.assertEquals(esbRestResponse.getBody().getString("color"), apiRestResponse.getBody().getString("color"));
        Assert.assertEquals(esbRestResponse.getBody().getString("priority"), apiRestResponse.getBody().getString(
                        "priority"));
        
    }
    
    /**
     * Positive test case for getStory method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {getStory} integration test with optional parameters.", dependsOnMethods = { "testCreateStoryWithOptionalParameters" })
    public void testGetStoryWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getStory");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getStory_optional.json");
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                                        + connectorProperties.getProperty("storyId") + "?with="
                                        + connectorProperties.getProperty("enrichments");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("text"), apiRestResponse.getBody().getString("text"));
        Assert.assertEquals(esbRestResponse.getBody().getString("details"), apiRestResponse.getBody().getString(
                        "details"));
        Assert.assertEquals(esbRestResponse.getBody().getString("size"), apiRestResponse.getBody().getString("size"));
        Assert.assertEquals(esbRestResponse.getBody().getString("color"), apiRestResponse.getBody().getString("color"));
        Assert.assertEquals(esbRestResponse.getBody().getString("priority"), apiRestResponse.getBody().getString(
                        "priority"));
        
    }
    
    /**
     * Positive test case for getStory method with negative case.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {getStory} integration test with negative case.", dependsOnMethods = { "testListProjectsWithMandatoryParameters" })
    public void testGetStoryWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getStory");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getStory_negative.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/111111";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for listStories method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {listStories} integration test with mandatory parameters.", dependsOnMethods = { "testCreateStoryWithOptionalParameters" })
    public void testListStoriesWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listStories");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listStories_mandatory.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("page"), apiRestResponse.getBody().getString("page"));
        Assert.assertEquals(esbRestResponse.getBody().getString("pageSize"), apiRestResponse.getBody().getString(
                        "pageSize"));
        Assert.assertEquals(esbRestResponse.getBody().getString("totalItems"), apiRestResponse.getBody().getString(
                        "totalItems"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).getString("size"),
                        apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).getString("size"));
        
    }
    
    /**
     * Positive test case for listStories method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {listStories} integration test with optional parameters.", dependsOnMethods = { "testCreateStoryWithOptionalParameters" })
    public void testListStoriesWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listStories");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listStories_optional.json");
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories"
                                        + "?page=1&pageSize=1&where=status:blocked&with="
                                        + connectorProperties.getProperty("enrichments");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("page"), apiRestResponse.getBody().getString("page"));
        Assert.assertEquals(esbRestResponse.getBody().getString("pageSize"), apiRestResponse.getBody().getString(
                        "pageSize"));
        Assert.assertEquals(esbRestResponse.getBody().getString("totalPages"), apiRestResponse.getBody().getString(
                        "totalPages"));
        Assert.assertEquals(esbRestResponse.getBody().getString("totalItems"), apiRestResponse.getBody().getString(
                        "totalItems"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").length(), 1);
        
    }
    
    /**
     * Positive test case for listStories method with negative case.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {listStories} integration test with negative case.", dependsOnMethods = { "testListProjectsWithMandatoryParameters" })
    public void testListStoriesWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listStories");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listStories_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/projects/123456/stories";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for updateStory method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {updateStory} integration test with optional parameters.", dependsOnMethods = { "testCreateStoryWithOptionalParameters" })
    public void testUpdateStoryWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateStory");
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                                        + connectorProperties.getProperty("storyId") + "?with="
                                        + connectorProperties.getProperty("enrichments");
        
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateStory_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("text"), apiRestResponseAfter.getBody()
                        .getString("text"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("details"), apiRestResponseAfter.getBody()
                        .getString("details"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("size"), apiRestResponseAfter.getBody()
                        .getString("size"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("priority"), apiRestResponseAfter.getBody()
                        .getString("priority"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("color"), apiRestResponseAfter.getBody()
                        .getString("color"));
        
    }
    
    /**
     * Positive test case for updateStory method with negative case.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {updateStory} integration test with negative case.", dependsOnMethods = { "testListProjectsWithMandatoryParameters" })
    public void testUpdateStoryWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateStory");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateStory_negative.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/123456";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for createTask method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {createTask} integration test with mandatory parameters.", dependsOnMethods = { "testCreateStoryWithOptionalParameters" })
    public void testCreateTaskWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createTask");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_mandatory.json");
        final String taskIdMandatory = esbRestResponse.getBody().getString("id");
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                                        + connectorProperties.getProperty("storyId") + "/tasks/" + taskIdMandatory;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("textTaskMandatory"), apiRestResponse.getBody().getString(
                        "text"));
        Assert.assertEquals(esbRestResponse.getBody().getString("createTime").split("\\.")[0], apiRestResponse
                        .getBody().getString("createTime"));
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                        .getString("status"));
    }
    
    /**
     * Positive test case for createTask method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {createTask} integration test with optional parameters.", dependsOnMethods = { "testCreateStoryWithOptionalParameters" })
    public void testCreateTaskWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createTask");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_optional.json");
        final String taskIdOptional = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("taskIdOptional", taskIdOptional);
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                                        + connectorProperties.getProperty("storyId") + "/tasks/" + taskIdOptional;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("textTaskOptional"), apiRestResponse.getBody().getString(
                        "text"));
        Assert.assertEquals(connectorProperties.getProperty("status"), apiRestResponse.getBody().getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("createTime").split("\\.")[0], apiRestResponse
                        .getBody().getString("createTime"));
    }
    
    /**
     * Negative test case for createTask method.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {createTask} integration test with negative case.", dependsOnMethods = { "testCreateStoryWithOptionalParameters" })
    public void testCreateTaskWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createTask");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_negative.json");
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                                        + connectorProperties.getProperty("storyId") + "/tasks";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createTask_negative.json");
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("property"), apiResponseArray.getJSONObject(0)
                        .getString("property"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("message"), apiResponseArray.getJSONObject(0)
                        .getString("message"));
    }
    
    /**
     * Positive test case for updateTask method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {updateTask} integration test with optional parameters.", dependsOnMethods = { "testCreateTaskWithOptionalParameters" })
    public void testUpdateTaskWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateTask");
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                                        + connectorProperties.getProperty("storyId") + "/tasks/"
                                        + connectorProperties.getProperty("taskIdOptional");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTask_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("text"), apiRestResponseAfter.getBody()
                        .getString("text"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("status"), apiRestResponseAfter.getBody()
                        .getString("status"));
        
        Assert.assertEquals(connectorProperties.getProperty("textTaskUpdated"), apiRestResponseAfter.getBody()
                        .getString("text"));
        Assert.assertEquals(connectorProperties.getProperty("statusUpdated"), apiRestResponseAfter.getBody().getString(
                        "status"));
    }
    
    /**
     * Negative test case for updateTask method.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {updateTask} integration test with negative case.", dependsOnMethods = { "testCreateStoryWithOptionalParameters" })
    public void testUpdateTaskWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateTask");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTask_negative.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                                        + connectorProperties.getProperty("storyId") + "/tasks/999999";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateTask_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Positive test case for listTasks method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {listTasks} integration test with mandatory parameters.", dependsOnMethods = {
                    "testCreateTaskWithOptionalParameters", "testCreateTaskWithMandatoryParameters" })
    public void testListTasksWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listTasks");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_mandatory.json");
        final JSONObject esbItemObject = esbRestResponse.getBody().getJSONArray("items").getJSONObject(0);
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                                        + connectorProperties.getProperty("storyId") + "/tasks";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONObject apiItemObject = apiRestResponse.getBody().getJSONArray("items").getJSONObject(0);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("totalItems"), apiRestResponse.getBody().getInt(
                        "totalItems"));
        Assert.assertEquals(esbItemObject.getString("id"), apiItemObject.getString("id"));
        Assert.assertEquals(esbItemObject.getString("text"), apiItemObject.getString("text"));
        Assert.assertEquals(esbItemObject.getString("createTime"), apiItemObject.getString("createTime"));
        Assert.assertEquals(esbItemObject.getString("status"), apiItemObject.getString("status"));
    }
    
    /**
     * Positive test case for listTasks method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {listTasks} integration test with optional parameters.", dependsOnMethods = {
                    "testCreateTaskWithOptionalParameters", "testCreateTaskWithMandatoryParameters" })
    public void testListTasksWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listTasks");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_optional.json");
        final JSONObject esbItemObject = esbRestResponse.getBody().getJSONArray("items").getJSONObject(0);
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                                        + connectorProperties.getProperty("storyId") + "/tasks?page=1&pageSize=1";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONObject apiItemObject = apiRestResponse.getBody().getJSONArray("items").getJSONObject(0);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("page"), 1);
        Assert.assertEquals(apiRestResponse.getBody().getInt("page"), 1);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("pageSize"), 1);
        Assert.assertEquals(apiRestResponse.getBody().getInt("pageSize"), 1);
        
        Assert.assertEquals(esbItemObject.getString("id"), apiItemObject.getString("id"));
        Assert.assertEquals(esbItemObject.getString("text"), apiItemObject.getString("text"));
        Assert.assertEquals(esbItemObject.getString("createTime"), apiItemObject.getString("createTime"));
        Assert.assertEquals(esbItemObject.getString("status"), apiItemObject.getString("status"));
    }
    
    /**
     * Negative test case for listTasks method.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {listTasks} integration test with negative case.")
    public void testListTasksWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listTasks");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_negative.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId")
                                        + "/stories/111111/tasks";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Positive test case for getTask method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {getTask} integration test with mandatory parameters.", dependsOnMethods = { "testCreateTaskWithOptionalParameters" })
    public void testGetTaskWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getTask");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTask_mandatory.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                                        + connectorProperties.getProperty("storyId") + "/tasks/"
                                        + connectorProperties.getProperty("taskIdOptional");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("text"), apiRestResponse.getBody().getString("text"));
        Assert.assertEquals(esbRestResponse.getBody().getString("createTime"), apiRestResponse.getBody().getString(
                        "createTime"));
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                        .getString("status"));
    }
    
    /**
     * Negative test case for getTask method.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {getTask} integration test with negative case.", dependsOnMethods = { "testCreateStoryWithOptionalParameters" })
    public void testGetTaskWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getTask");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTask_negative.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                                        + connectorProperties.getProperty("storyId") + "/tasks/123456";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Positive test case for createComment method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {createComment} integration test with mandatory parameters.", dependsOnMethods = { "testCreateStoryWithOptionalParameters" })
    public void testCreateCommentWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createComment");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_mandatory.json");
        final String commentIdMandatory = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("commentIdMandatory", commentIdMandatory);
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                                        + connectorProperties.getProperty("storyId") + "/comments/"
                                        + commentIdMandatory;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("textCommentMandatory"), apiRestResponse.getBody()
                        .getString("text"));
        Assert.assertEquals(esbRestResponse.getBody().getString("createTime").split("\\.")[0], apiRestResponse
                        .getBody().getString("createTime"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("author").getString("id"), apiRestResponse
                        .getBody().getJSONObject("author").getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("author").getString("userName"), apiRestResponse
                        .getBody().getJSONObject("author").getString("userName"));
    }
    
    /**
     * Negative test case for createComment method.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {createComment} integration test with negative case.", dependsOnMethods = { "testCreateStoryWithOptionalParameters" })
    public void testCreateCommentWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createComment");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_negative.json");
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                                        + connectorProperties.getProperty("storyId") + "/comments";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                                        "api_createComment_negative.json");
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("property"), apiResponseArray.getJSONObject(0)
                        .getString("property"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("message"), apiResponseArray.getJSONObject(0)
                        .getString("message"));
    }
    
    /**
     * Positive test case for updateComment method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {updateComment} integration test with optional parameters.", dependsOnMethods = { "testCreateCommentWithMandatoryParameters" })
    public void testUpdateCommentWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateComment");
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                                        + connectorProperties.getProperty("storyId") + "/comments/"
                                        + connectorProperties.getProperty("commentIdMandatory");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateComment_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("text"), apiRestResponseAfter.getBody()
                        .getString("text"));
        Assert.assertEquals(connectorProperties.getProperty("textCommentUpdated"), apiRestResponseAfter.getBody()
                        .getString("text"));
    }
    
    /**
     * Negative test case for updateComment method.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {updateComment} integration test with negative case.", dependsOnMethods = { "testCreateStoryWithOptionalParameters" })
    public void testUpdateCommentWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateComment");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateComment_negative.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                                        + connectorProperties.getProperty("storyId") + "/comments/123456";
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                                        "api_updateComment_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Positive test case for listComments method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {listComments} integration test with mandatory parameters.", dependsOnMethods = { "testCreateCommentWithMandatoryParameters" })
    public void testListCommentsWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listComments");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listComments_mandatory.json");
        final JSONObject esbItemObject = esbRestResponse.getBody().getJSONArray("items").getJSONObject(0);
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                                        + connectorProperties.getProperty("storyId") + "/comments";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONObject apiItemObject = apiRestResponse.getBody().getJSONArray("items").getJSONObject(0);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("totalItems"), apiRestResponse.getBody().getInt(
                        "totalItems"));
        Assert.assertEquals(esbItemObject.getString("id"), apiItemObject.getString("id"));
        Assert.assertEquals(esbItemObject.getString("text"), apiItemObject.getString("text"));
        Assert.assertEquals(esbItemObject.getString("createTime"), apiItemObject.getString("createTime"));
        Assert.assertEquals(esbItemObject.getJSONObject("author").getString("id"), apiItemObject
                        .getJSONObject("author").getString("id"));
        Assert.assertEquals(esbItemObject.getJSONObject("author").getString("userName"), apiItemObject.getJSONObject(
                        "author").getString("userName"));
    }
    
    /**
     * Positive test case for listComments method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {listComments} integration test with optional parameters.", dependsOnMethods = { "testCreateCommentWithMandatoryParameters" })
    public void testListCommentsWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listComments");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listComments_optional.json");
        final JSONObject esbItemObject = esbRestResponse.getBody().getJSONArray("items").getJSONObject(0);
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                                        + connectorProperties.getProperty("storyId") + "/comments?page=1&pageSize=1";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONObject apiItemObject = apiRestResponse.getBody().getJSONArray("items").getJSONObject(0);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("page"), 1);
        Assert.assertEquals(apiRestResponse.getBody().getInt("page"), 1);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("pageSize"), 1);
        Assert.assertEquals(apiRestResponse.getBody().getInt("pageSize"), 1);
        
        Assert.assertEquals(esbItemObject.getString("id"), apiItemObject.getString("id"));
        Assert.assertEquals(esbItemObject.getString("text"), apiItemObject.getString("text"));
        Assert.assertEquals(esbItemObject.getString("createTime"), apiItemObject.getString("createTime"));
        Assert.assertEquals(esbItemObject.getJSONObject("author").getString("id"), apiItemObject
                        .getJSONObject("author").getString("id"));
        Assert.assertEquals(esbItemObject.getJSONObject("author").getString("userName"), apiItemObject.getJSONObject(
                        "author").getString("userName"));
    }
    
    /**
     * Negative test case for listComments method.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {listComments} integration test with negative case.")
    public void testListCommentsWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listComments");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listComments_negative.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId")
                                        + "/stories/111111/comments";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Positive test case for getComment method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {getComment} integration test with mandatory parameters.", dependsOnMethods = { "testCreateCommentWithMandatoryParameters" })
    public void testGetCommentWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getComment");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getComment_mandatory.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                                        + connectorProperties.getProperty("storyId") + "/comments/"
                                        + connectorProperties.getProperty("commentIdMandatory");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("text"), apiRestResponse.getBody().getString("text"));
        Assert.assertEquals(esbRestResponse.getBody().getString("createTime"), apiRestResponse.getBody().getString(
                        "createTime"));
    }
    
    /**
     * Negative test case for getComment method.
     */
    @Test(groups = { "wso2.esb" }, description = "agilezen {getComment} integration test with negative case.", dependsOnMethods = { "testCreateStoryWithOptionalParameters" })
    public void testGetCommentWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getComment");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getComment_negative.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                                        + connectorProperties.getProperty("storyId") + "/comments/123456";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
}
