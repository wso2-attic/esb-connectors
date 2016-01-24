/*
 *  Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.integration.test.pivotaltracker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.util.Base64;
import org.apache.catalina.util.URLEncoder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class PivotaltrackerConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap;
    
    private Map<String, String> apiRequestHeadersMap;
    
    private String apiEndpointUrl;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("pivotaltracker-connector-1.0.0");
        
        esbRequestHeadersMap = new HashMap<String, String>();
        
        apiRequestHeadersMap = new HashMap<String, String>();
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        apiEndpointUrl = connectorProperties.getProperty("apiUrl") + "/services/v5";
        setTrackerTokenHeader();
    }
    
    /**
     * Set X-TrackerToken header to API request.
     * 
     * @throws JSONException
     * @throws IOException
     */
    private void setTrackerTokenHeader() throws IOException, JSONException {
    
        // Create base64-encoded auth string using apiToken
        final String userName = connectorProperties.getProperty("userName");
        final String password = connectorProperties.getProperty("password");
        final String authString = userName + ":" + password;
        final String base64AuthString = Base64.encode(authString.getBytes());
        
        apiRequestHeadersMap.put("Authorization", "Basic " + base64AuthString);
        
        final String apiEndpoint = apiEndpointUrl + "/me";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final String apiToken = apiRestResponse.getBody().getString("api_token");
        
        apiRequestHeadersMap.remove("Authorization");
        apiRequestHeadersMap.put("X-TrackerToken", apiToken);
    }
    
    /**
     * Positive test case for createProject method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "pivotaltracker {createProject} integration test with mandatory parameters.")
    public void testCreateProjectWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createProject");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_mandatory.json");
        System.out.println("esb: "+esbRestResponse.getBody());
        final String projectId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("projectId", projectId);
        
        final String apiEndpoint = apiEndpointUrl + "/projects/" + projectId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("projectName"), apiRestResponse.getBody().getString("name"));
        
    }
    
    /**
     * Positive test case for createProject method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "pivotaltracker {createProject} integration test with optional parameters.")
    public void testCreateProjectWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createProject");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_optional.json");
        final String projectId = esbRestResponse.getBody().getString("id");
        final String accountId = esbRestResponse.getBody().getString("account_id");
        connectorProperties.put("accountId", accountId);
        
        final String apiEndpoint = apiEndpointUrl + "/projects/" + projectId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("projectNameOpt"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("weekStartDay"),
                apiRestResponse.getBody().getString("week_start_day"));
        Assert.assertEquals(connectorProperties.getProperty("projectDescription"),
                apiRestResponse.getBody().getString("description"));
        Assert.assertEquals(connectorProperties.getProperty("projectType"),
                apiRestResponse.getBody().getString("project_type"));
        Assert.assertEquals(connectorProperties.getProperty("projectProfile"),
                apiRestResponse.getBody().getString("profile_content"));
        Assert.assertEquals(connectorProperties.getProperty("projectTimeZone"), apiRestResponse.getBody()
                .getJSONObject("time_zone").getString("olson_name"));
        
    }
    
    /**
     * Negative test case for createProject method .
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "pivotaltracker {createProject} integration test with negative case.")
    public void testCreateProjectWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createProject");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/projects/";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createProject_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("kind"), apiRestResponse.getBody().getString("kind"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        Assert.assertEquals(esbRestResponse.getBody().getString("general_problem"), apiRestResponse.getBody()
                .getString("general_problem"));
        
    }
    
    /**
     * Positive test case for getProject method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "pivotaltracker {getProject} integration test with mandatory parameters.")
    public void testGetProjectWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getProject");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProject_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/projects/" + connectorProperties.getProperty("projectId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("account_id"),
                apiRestResponse.getBody().getString("account_id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("kind"), apiRestResponse.getBody().getString("kind"));
        Assert.assertEquals(esbRestResponse.getBody().getString("week_start_day"),
                apiRestResponse.getBody().getString("week_start_day"));
        
    }
    
    /**
     * Method Name: getProject
     * Skipped Case: optional case
     * Reason: No optional parameter(s) to assert. 
     */
    
    /**
     * Negative test case for getProject method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "pivotaltracker {getProject} integration test with negative case.")
    public void testGetProjectWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getProject");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProject_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/projects/INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("kind"), apiRestResponse.getBody().getString("kind"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for listProjects method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectWithMandatoryParameters",
            "testCreateProjectWithOptionalParameters" }, description = "pivotaltracker {listProjects} integration test with mandatory parameters.")
    public void testListProjectsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listProjects");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjects_mandatory.json");
        JSONArray esbOutputArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndpoint = apiEndpointUrl + "/projects";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        JSONArray apiOutputArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbOutputArray.length(), apiOutputArray.length());
        Assert.assertEquals(esbOutputArray.getJSONObject(0).getString("id"),
                apiOutputArray.getJSONObject(0).getString("id"));
        Assert.assertEquals(esbOutputArray.getJSONObject(0).getString("name"), apiOutputArray.getJSONObject(0)
                .getString("name"));
        Assert.assertEquals(esbOutputArray.getJSONObject(0).getString("account_id"), apiOutputArray.getJSONObject(0)
                .getString("account_id"));
        Assert.assertEquals(esbOutputArray.getJSONObject(0).getString("kind"), apiOutputArray.getJSONObject(0)
                .getString("kind"));
        Assert.assertEquals(esbOutputArray.getJSONObject(0).getString("week_start_day"), apiOutputArray
                .getJSONObject(0).getString("week_start_day"));
        
    }
    
    /**
     * Positive test case for listProjects method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectWithMandatoryParameters",
            "testCreateProjectWithOptionalParameters" }, description = "pivotaltracker {listProjects} integration test with mandatory parameters.")
    public void testListProjectsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listProjects");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjects_optional.json");
        JSONArray esbOutputArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndpoint = apiEndpointUrl + "/projects";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        JSONArray apiOutputArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertNotEquals(esbOutputArray.length(), apiOutputArray.length());
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        
    }
    
    /**
     * Method Name: listProjects
     * Skipped Case: negative case
     * Reason: No parameter(s) to test negative case. 
     */
    
    /**
     * Positive test case for createLabelForProject method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "pivotaltracker {createLabelForProject} integration test with mandatory parameters.")
    public void testCreateLabelForProjectWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createLabelForProject");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createLabelForProject_mandatory.json");
        final String labelId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("labelId", labelId);
        
        final String apiEndpoint =
                apiEndpointUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/labels/" + labelId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("labelName"), apiRestResponse.getBody().getString("name"));
        
    }
    
    /**
     * Method Name: createLabelForProject
     * Skipped Case: optional case
     * Reason: No optional parameter(s) to assert. 
     */
    
    /**
     * Negative test case for createLabelForProject method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "pivotaltracker {createLabelForProject} integration test with negative case.")
    public void testCreateLabelForProjectWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createLabelForProject");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createLabelForProject_negative.json");
        final String apiEndpoint =
                apiEndpointUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/labels";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                        "api_createLabelForProject_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        Assert.assertEquals(esbRestResponse.getBody().getString("general_problem"), apiRestResponse.getBody()
                .getString("general_problem"));
        
    }
    
    /**
     * Positive test case for updateProjectLabel method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateLabelForProjectWithMandatoryParameters" }, description = "pivotaltracker {updateProjectLabel} integration test with mandatory parameters.")
    public void testUpdateProjectLabelWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateProjectLabel");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateProjectLabel_mandatory.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/labels/"
                        + connectorProperties.getProperty("labelId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("name"),
                connectorProperties.getProperty("updatedLabelName"));
        Assert.assertEquals(apiRestResponse.getBody().getString("created_at"),
                esbRestResponse.getBody().getString("created_at"));
        Assert.assertEquals(apiRestResponse.getBody().getString("updated_at"),
                esbRestResponse.getBody().getString("updated_at"));
    }
    
    /**
     * Method Name: updateProjectLabel
     * Skipped Case: optional case
     * Reason: No optional parameter(s) to assert. 
     */
    
    /**
     * Negative test case for updateProjectLabel method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateLabelForProjectWithMandatoryParameters" }, description = "pivotaltracker {updateProjectLabel} integration test with negative case.")
    public void testUpdateProjectLabelWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateProjectLabel");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateProjectLabel_negative.json");
        final String apiEndpoint =
                apiEndpointUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/labels/"
                        + connectorProperties.getProperty("labelId");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_updateProjectLabel_negative.json");
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        Assert.assertEquals(esbRestResponse.getBody().getString("general_problem"), apiRestResponse.getBody()
                .getString("general_problem"));
        
    }
    
    /**
     * Positive test case for getProjectLabels method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateLabelForProjectWithMandatoryParameters" }, description = "pivotaltracker {getProjectLabels} integration test with mandatory parameters.")
    public void testGetProjectLabelsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getProjectLabels");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProjectLabels_mandatory.json");
        JSONArray esbOutputArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndpoint =
                apiEndpointUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/labels";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        JSONArray apiOutputArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbOutputArray.length(), apiOutputArray.length());
        Assert.assertEquals(esbOutputArray.getJSONObject(0).getString("id"),
                apiOutputArray.getJSONObject(0).getString("id"));
        Assert.assertEquals(esbOutputArray.getJSONObject(0).getString("name"), apiOutputArray.getJSONObject(0)
                .getString("name"));
        Assert.assertEquals(esbOutputArray.getJSONObject(0).getString("project_id"), apiOutputArray.getJSONObject(0)
                .getString("project_id"));
        Assert.assertEquals(esbOutputArray.getJSONObject(0).getString("kind"), apiOutputArray.getJSONObject(0)
                .getString("kind"));
        Assert.assertEquals(esbOutputArray.getJSONObject(0).getString("created_at"), apiOutputArray.getJSONObject(0)
                .getString("created_at"));
        
    }
    
    /**
     * Method Name: getProjectLabels
     * Skipped Case: optional case
     * Reason: No optional parameter(s) to assert. 
     */
    
    /**
     * Negative test case for getProjectLabels method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "pivotaltracker {getProjectLabels} integration test with negative case.")
    public void testGetProjectLabelWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getProjectLabels");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProjectLabels_negative.json");
        final String apiEndpoint = apiEndpointUrl + "/projects/INVALID/labels";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        Assert.assertEquals(esbRestResponse.getBody().getString("kind"), apiRestResponse.getBody().getString("kind"));
        
    }
    
    /**
     * Positive test case for createLabelForStory method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectWithMandatoryParameters","testCreateStoryWithMandatoryParameters" }, description = "pivotaltracker {createLabelForStory} integration test with mandatory parameters.")
    public void testCreateLabelForStoryWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createLabelForStory");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createLabelForStory_mandatory.json");
        final String labelId = esbRestResponse.getBody().getString("id");
        
        final String apiEndpoint =
                apiEndpointUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                        + connectorProperties.getProperty("storyId") + "/labels";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        JSONArray apiOutputArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        JSONObject lableObj = null;
        for (int i = 0; i < apiOutputArray.length(); i++) {
            lableObj = apiOutputArray.getJSONObject(i);
            if (labelId.equals(lableObj.getString("id"))) {
                break;
            }
        }
        Assert.assertEquals(connectorProperties.getProperty("labelName"), lableObj.getString("name"));
        
    }
    
    /**
     * Method Name: createLabelForStory
     * Skipped Case: optional case
     * Reason: No optional parameter(s) to assert. 
     */
    
    /**
     * Negative test case for createLabelForStory method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectWithMandatoryParameters","testCreateStoryWithMandatoryParameters","testCreateLabelForProjectWithMandatoryParameters" }, description = "pivotaltracker {createLabelForStory} integration test with negative case.")
    public void testCreateLabelForStoryWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createLabelForStory");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createLabelForStory_negative.json");
        final String apiEndpoint =
                apiEndpointUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                        + connectorProperties.getProperty("storyId") + "/labels";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createLabelForStory_negative.json");
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("kind"), apiRestResponse.getBody().getString("kind"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        Assert.assertEquals(esbRestResponse.getBody().getString("requirement"),
                apiRestResponse.getBody().getString("requirement"));
        
    }
    
    /**
     * Positive test case for createStory method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" },dependsOnMethods = { "testCreateProjectWithMandatoryParameters"}, description = "pivotaltracker {createStory} integration test with mandatory parameters.")
    public void testCreateStoryWithMandatoryParameters() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:createStory");
       
       RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createStory_mandatory.json");
       final String storyId = esbRestResponse.getBody().getString("id");
       final String storyName = esbRestResponse.getBody().getString("name");
       connectorProperties.put("storyId", storyId);
       
       final String apiEndpoint =
             apiEndpointUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/" + storyId;
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
       
       Assert.assertEquals(storyName, apiRestResponse.getBody().getString("name"));
       
    }
    
    /**
     * Positive test case for createStory method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" },dependsOnMethods = { "testCreateProjectWithMandatoryParameters"}, description = "pivotaltracker {createStory} integration test with optional parameters.")
    public void testCreateStoryWithOptionalParameters() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:createStory");
       RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createStory_optional.json");
       final String storyId = esbRestResponse.getBody().getString("id");
       
       final String apiEndpoint =
             apiEndpointUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/" + storyId;
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
       
       Assert.assertEquals(connectorProperties.getProperty("storyNameOpt"), apiRestResponse.getBody().getString("name"));
       Assert.assertEquals(connectorProperties.getProperty("storyDescription"), apiRestResponse.getBody().getString("description"));
       Assert.assertEquals(connectorProperties.getProperty("storyState"), apiRestResponse.getBody().getString("current_state"));
       Assert.assertEquals(connectorProperties.getProperty("storyType"), apiRestResponse.getBody().getString("story_type"));
       Assert.assertEquals(connectorProperties.getProperty("deadline"), apiRestResponse.getBody().getString("deadline"));
       Assert.assertEquals(connectorProperties.getProperty("acceptedAt"), apiRestResponse.getBody().getString("accepted_at"));
       
    }
    
    /**
     * Negative test case for createStory method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" },dependsOnMethods = { "testCreateProjectWithMandatoryParameters","testCreateStoryWithMandatoryParameters"}, description = "pivotaltracker {createStory} integration test with negative case.")
    public void testCreateStoryWithNegativeCase() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:createStory");
       RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createStory_negative.json");
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
       
       final String apiEndpoint =
             apiEndpointUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories";
       RestResponse<JSONObject> apiRestResponse =
             sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createStory_negative.json");
       Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
       
       Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
       Assert.assertEquals(esbRestResponse.getBody().getString("kind"), apiRestResponse.getBody().getString("kind"));
       Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for getStory method with mandatory parameters.
     * 
     * @throws IOException
     * @throws JSONException
    */
    @Test(groups = { "wso2.esb" },dependsOnMethods = { "testCreateProjectWithMandatoryParameters","testCreateStoryWithMandatoryParameters"}, description = "pivotaltracker {getStory} integration test with mandatory parameters.")
    public void testGetStoryWithMandatoryParameters() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:getStory");
       
       RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getStory_mandatory.json");
       
       String apiEndPoint =
             apiEndpointUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories/"
                   + connectorProperties.getProperty("storyId");
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
       Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString("created_at"));
       Assert.assertEquals(esbRestResponse.getBody().getString("updated_at"), apiRestResponse.getBody().getString("updated_at"));
       Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
    }
    
    /**
     * Method Name: getStory
     * Skipped Case: optional case
     * Reason: No optional parameter(s) to assert. 
     */
    
    /**
     * Negative test case for getStory method.
     */
    @Test(groups = { "wso2.esb" },dependsOnMethods = { "testCreateStoryWithMandatoryParameters"}, description = "pivotaltracker {getStory} integration test with negative case.")
    public void testGetStoryWithNegativeCase() throws Exception {
    
       esbRequestHeadersMap.put("Action", "urn:getStory");
       RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getStory_negative.json");
       String apiEndPoint = apiEndpointUrl + "/projects/INVALID/stories/"+connectorProperties.getProperty("storyId");
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
       Assert.assertEquals(esbRestResponse.getBody().getString("kind"), apiRestResponse.getBody().getString("kind"));
       Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listStories method with mandatory parameters.
     * 
     * @throws IOException
     * @throws JSONException
     */
    @Test(groups = { "wso2.esb" },dependsOnMethods = { "testCreateProjectWithMandatoryParameters","testCreateStoryWithOptionalParameters"}, description = "pivotaltracker {listStories} integration test with mandatory parameters.")
    public void testListStoriesWithMandatoryParameters() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:listStories");
       
       RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listStories_mandatory.json");
       JSONArray esbUserStoryArray = new JSONArray(esbRestResponse.getBody().getString("output"));
       
       String apiEndPoint = apiEndpointUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/stories";
       
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       JSONArray apiUserStoryArray = new JSONArray(apiRestResponse.getBody().getString("output"));
       
       Assert.assertEquals(esbUserStoryArray.length(), apiUserStoryArray.length());
       Assert.assertEquals(esbUserStoryArray.getJSONObject(0).getString("id"), apiUserStoryArray.getJSONObject(0)
             .getString("id"));
       Assert.assertEquals(esbUserStoryArray.getJSONObject(0).getString("name"), apiUserStoryArray.getJSONObject(0)
             .getString("name"));
       Assert.assertEquals(esbUserStoryArray.getJSONObject(0).getString("created_at"), apiUserStoryArray
             .getJSONObject(0).getString("created_at"));
       Assert.assertEquals(esbUserStoryArray.getJSONObject(0).getString("updated_at"), apiUserStoryArray
             .getJSONObject(0).getString("updated_at"));
    }
    
    /**
     * Positive test case for listStories method with optional parameters.
     * 
     * @throws IOException
     * @throws JSONException
     */
    @Test(groups = { "wso2.esb" },dependsOnMethods = { "testCreateProjectWithMandatoryParameters","testCreateStoryWithOptionalParameters"}, description = "pivotaltracker {listStories} integration test with optional parameters.")
    public void testListStoriesWithOptionalParameters() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:listStories");
       
       RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listStories_optional.json");
       JSONArray esbUserStoryArray = new JSONArray(esbRestResponse.getBody().getString("output"));
       
       URLEncoder encoder = new URLEncoder();
       final String state = encoder.encode("accepted");
       
       String apiEndPoint =
             apiEndpointUrl
                   + "/projects/"
                   + connectorProperties.getProperty("projectId")
                   + "/stories?offset=0&limit=10&created_before="+connectorProperties.getProperty("createdBefore")+"&created_after="+connectorProperties.getProperty("createdAfter")+"&with_state="
                   + state;
       
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       JSONArray apiUserStoryArray = new JSONArray(apiRestResponse.getBody().getString("output"));
       
       Assert.assertEquals(esbUserStoryArray.length(), apiUserStoryArray.length());
       Assert.assertEquals(esbUserStoryArray.getJSONObject(0).getString("id"), apiUserStoryArray.getJSONObject(0)
             .getString("id"));
       Assert.assertEquals(esbUserStoryArray.getJSONObject(0).getString("name"), apiUserStoryArray.getJSONObject(0)
             .getString("name"));
       Assert.assertEquals(esbUserStoryArray.getJSONObject(0).getString("created_at"), apiUserStoryArray
             .getJSONObject(0).getString("created_at"));
       Assert.assertEquals(esbUserStoryArray.getJSONObject(0).getString("updated_at"), apiUserStoryArray
             .getJSONObject(0).getString("updated_at"));
       
    }
    
    /**
     * Negative test case for listStories method.
     * 
     * @throws IOException
     * @throws JSONException
     */
    @Test(groups = { "wso2.esb" }, description = "pivotaltracker {listStories} integration test with negative case.")
    public void testListStoriesWithNegativeCase() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:listStories");
       
       RestResponse<JSONObject>  esbRestResponse=
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listStories_negative.json");
       
       String apiEndPoint = apiEndpointUrl + "/projects/INVALID/stories";
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
       Assert.assertEquals(esbRestResponse.getBody().getString("kind"), apiRestResponse.getBody().getString("kind"));
       Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
       
    }
    
}
