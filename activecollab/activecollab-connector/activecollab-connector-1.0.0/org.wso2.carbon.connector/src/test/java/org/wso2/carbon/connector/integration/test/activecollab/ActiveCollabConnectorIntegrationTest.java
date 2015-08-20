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

package org.wso2.carbon.connector.integration.test.activecollab;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

public class ActiveCollabConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    SimpleDateFormat sdf = new SimpleDateFormat("MMM d. yyyy");
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("activecollab-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");
        
        //setting an assignRoleId property.
        setAssignRoleId();
        
    }
    
    /**
     * This method will execute before test execution to set assignRoleId property.
     */
    private void setAssignRoleId() throws IOException, JSONException {
    
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=info/roles/project";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        String apiResponseArrayString = apiRestResponse.getBody().getString("output");
        
        JSONArray apiResponseArray = new JSONArray(apiResponseArrayString);
        
        if (apiResponseArray.length() > 0) {
            String assignRoleId = apiResponseArray.getJSONObject(0).getString("id");
            connectorProperties.put("assignRoleId", assignRoleId);
        } else {
            Assert.fail("Test execution skipped.Please create at least one project role.");
        }
        
    }
    
    /**
     * Positive test case for createProject method with mandatory parameters.
     */
    @Test(priority = 1, description = "activecollab {createProject} integration test with mandatory parameters.")
    public void testCreateProjectWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createProject");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_mandatory.json");
        
        String projectId = esbRestResponse.getBody().getString("id");
        
        connectorProperties.put("projectId", projectId);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/" + projectId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("projectNameMandatory"), apiRestResponse.getBody()
                .getString("name"));
        
        Assert.assertEquals(connectorProperties.getProperty("companyId"),
                apiRestResponse.getBody().getJSONObject("company").getString("id"));
    }
    
    /**
     * Positive test case for createProject method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "activecollab {createProject} integration test with optional parameters.")
    public void testCreateProjectWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createProject");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_optional.json");
        
        String projectId = esbRestResponse.getBody().getString("id");
        
        connectorProperties.put("projectIdOptional", projectId);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/" + projectId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("projectNameOptional"), apiRestResponse.getBody()
                .getString("name"));
        
        Assert.assertEquals(connectorProperties.getProperty("companyId"),
                apiRestResponse.getBody().getJSONObject("company").getString("id"));
        
        Assert.assertEquals(esbRestResponse.getBody().getString("overview"),
                apiRestResponse.getBody().getString("overview"));
    }
    
    /**
     * Negative test case for createProject method.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectWithOptionalParameters" }, description = "activecollab {createProject} integration test with negative case.")
    public void testCreateProjectWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createProject");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/add";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createProject_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        Assert.assertEquals(esbRestResponse.getBody().getString("type"), apiRestResponse.getBody().getString("type"));
        Assert.assertEquals(esbRestResponse.getBody().getString("object_class"),
                apiRestResponse.getBody().getString("object_class"));
        
    }
    
    /**
     * Positive test case for createDiscussion method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "activecollab {createDiscussion} integration test with mandatory parameters.")
    public void testCreateDiscussionWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createDiscussion");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDiscussion_mandatory.json");
        
        String discussionId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("discussionId", discussionId);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/"
                        + connectorProperties.getProperty("projectId") + "/discussions/" + discussionId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("discussionName"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("discussionBody"),
                apiRestResponse.getBody().getString("body"));
        Assert.assertEquals(connectorProperties.getProperty("projectId"),
                apiRestResponse.getBody().getJSONObject("project").getString("id"));
    }
    
    /**
     * Positive test case for createDiscussion method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateDiscussionWithMandatoryParameters" }, description = "activecollab {createDiscussion} integration test with optional parameters.")
    public void testCreateDiscussionWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createDiscussion");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDiscussion_optional.json");
        
        String discussionId = esbRestResponse.getBody().getString("id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/"
                        + connectorProperties.getProperty("projectId") + "/discussions/" + discussionId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("discussionName"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("discussionBody"),
                apiRestResponse.getBody().getString("body"));
        Assert.assertEquals(connectorProperties.getProperty("projectId"),
                apiRestResponse.getBody().getJSONObject("project").getString("id"));
        Assert.assertEquals(connectorProperties.getProperty("discussionVisibility"), apiRestResponse.getBody()
                .getString("visibility"));
        
    }
    
    /**
     * Negative test case for createDiscussion method.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateDiscussionWithOptionalParameters" }, description = "activecollab {CreateDiscussion} integration test with negative case.")
    public void testCreateDiscussionWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createDiscussion");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDiscussion_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/"
                        + connectorProperties.getProperty("projectId") + "/discussions/add";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createDiscussion_negative.json");
        
        //Asserting error message which given from esb and api 
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        Assert.assertEquals(esbRestResponse.getBody().getString("type"), apiRestResponse.getBody().getString("type"));
        Assert.assertEquals(esbRestResponse.getBody().getString("object_class"),
                apiRestResponse.getBody().getString("object_class"));
    }
    
    /**
     * Positive test case for assignMembers method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "activecollab {assignMembers} integration test with mandatory parameters.")
    public void testAssignMembersWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:assignMembers");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/"
                        + connectorProperties.getProperty("projectId") + "/people";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String apiResponseArrayString = apiRestResponse.getBody().getString("output");
        JSONArray apiResponseArray = new JSONArray(apiResponseArrayString);
        
        // checking whether the user Id is already assigned to the project or not. If already assigned, then
        // fail the assertion
        for (int i = 0; i < apiResponseArray.length(); i++) {
            if (connectorProperties.getProperty("assignUserIdMandatory").equals(
                    apiResponseArray.getJSONObject(i).getString("user_id"))) {
                Assert.fail("Asseertion failed: The user is already Assigned to the project.");
                break;
            }
        }
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_assignMembers_mandatory.json");
        
        String esbResponseArrayString = esbRestResponse.getBody().getString("output");
        JSONArray esbResponseArray = new JSONArray(esbResponseArrayString);
        
        String esbUserId = esbResponseArray.getJSONObject(0).getString("id");
        String esbCompanyId = esbResponseArray.getJSONObject(0).getString("company_id");
        
        apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        apiResponseArrayString = apiRestResponse.getBody().getString("output");
        apiResponseArray = new JSONArray(apiResponseArrayString);
        
        // Checking whether the user is assigned properly.If the assigned user Id is found, assert the user
        // object
        for (int i = 0; i < apiResponseArray.length(); i++) {
            if (esbUserId.equals(apiResponseArray.getJSONObject(i).getString("user_id"))) {
                Assert.assertEquals(connectorProperties.getProperty("assignUserIdMandatory"), apiResponseArray
                        .getJSONObject(i).getString("user_id"));
                Assert.assertEquals(esbCompanyId,
                        apiResponseArray.getJSONObject(i).getJSONObject("user").getString("company_id"));
                break;
            }
        }
    }
    
    /**
     * Positive test case for assignMembers method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectWithOptionalParameters" }, description = "activecollab {assignMembers} integration test with optional parameters.")
    public void testAssignMembersWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:assignMembers");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/"
                        + connectorProperties.getProperty("projectIdOptional") + "/people";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String apiResponseArrayString = apiRestResponse.getBody().getString("output");
        JSONArray apiResponseArray = new JSONArray(apiResponseArrayString);
        
        // checking whether the user Id is already assigned to the project or not. If already assigned, then
        // fail the assertion
        for (int i = 0; i < apiResponseArray.length(); i++) {
            if (connectorProperties.getProperty("assignUserIdOptional").equals(
                    apiResponseArray.getJSONObject(i).getString("user_id"))) {
                Assert.fail("Asseertion failed: The user is already Assigned to the project.");
                break;
            }
        }
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_assignMembers_optional.json");
        
        String esbResponseArrayString = esbRestResponse.getBody().getString("output");
        JSONArray esbResponseArray = new JSONArray(esbResponseArrayString);
        
        String esbUserId = esbResponseArray.getJSONObject(0).getString("id");
        String esbCompanyId = esbResponseArray.getJSONObject(0).getString("company_id");
        
        apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        apiResponseArrayString = apiRestResponse.getBody().getString("output");
        apiResponseArray = new JSONArray(apiResponseArrayString);
        
        // Checking whether the user is assigned properly.If the assigned user Id is found, assert the user
        // object
        for (int i = 0; i < apiResponseArray.length(); i++) {
            
            if (esbUserId.equals(apiResponseArray.getJSONObject(i).getString("user_id"))) {
                Assert.assertEquals(connectorProperties.getProperty("assignUserIdOptional"), apiResponseArray
                        .getJSONObject(i).getString("user_id"));
                
                Assert.assertEquals(connectorProperties.getProperty("assignRoleId"), apiResponseArray.getJSONObject(i)
                        .getString("role_id"));
                
                Assert.assertEquals(esbCompanyId,
                        apiResponseArray.getJSONObject(i).getJSONObject("user").getString("company_id"));
                break;
            }
        }
    }
    
    /**
     * Negative test case for assignMembers method.
     */
    @Test(priority = 1, dependsOnMethods = { "testAssignMembersWithOptionalParameters" }, description = "activecollab {assignMembers} integration test with negative case.")
    public void testAssignMembersWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:assignMembers");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_assignMembers_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/" + "%20"
                        + "/people/add";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_assignMembers_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("output"), apiRestResponse.getBody()
                .getString("output"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for getDiscussion method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "activecollab {getDiscussion} integration test with mandatory parameters.")
    public void testGetDiscussionWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getDiscussion");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDiscussion_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/"
                        + connectorProperties.getProperty("projectId") + "/discussions/"
                        + connectorProperties.getProperty("discussionId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("body"), apiRestResponse.getBody().getString("body"));
        Assert.assertEquals(esbRestResponse.getBody().getString("state"), apiRestResponse.getBody().getString("state"));
        Assert.assertEquals(esbRestResponse.getBody().getString("visibility"),
                apiRestResponse.getBody().getString("visibility"));
    }
    
    /**
     * Negative test case for getDiscussion method.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "activecollab {getDiscussion} integration test with negative case.")
    public void testGetDiscussionWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getDiscussion");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDiscussion_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/"
                        + connectorProperties.getProperty("projectId") + "/discussions/invalid";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("output"), apiRestResponse.getBody()
                .getString("output"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     *
     * Returns 403
     *
     * Positive test case for createClient method with mandatory parameters.
     */
    @Test(priority = 1, enabled = false, description = "activecollab {createClient} integration test with mandatory parameters.")
    public void testCreateClientWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createClient");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createClient_mandatory.json");
        
        String clientId = esbRestResponse.getBody().getString("id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=people/"
                        + connectorProperties.getProperty("companyId") + "/users/" + clientId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("emailMandatory"),
                apiRestResponse.getBody().getString("email"));
        Assert.assertEquals(connectorProperties.getProperty("companyId"),
                apiRestResponse.getBody().getString("company_id"));
        
    }
    
    /**
     *
     * Returns 403
     *
     * Positive test case for createClient method with optional parameters.
     */
    @Test(priority = 1, enabled = false, description = "activecollab {createClient} integration test with optional parameters.")
    public void testCreateClientWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createClient");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createClient_optional.json");
        
        String clientId = esbRestResponse.getBody().getString("id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=people/"
                        + connectorProperties.getProperty("companyId") + "/users/" + clientId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("emailOptional"),
                apiRestResponse.getBody().getString("email"));
        Assert.assertEquals(connectorProperties.getProperty("companyId"),
                apiRestResponse.getBody().getString("company_id"));
        Assert.assertEquals(connectorProperties.getProperty("firstName"),
                apiRestResponse.getBody().getString("first_name"));
        Assert.assertEquals(connectorProperties.getProperty("lastName"),
                apiRestResponse.getBody().getString("last_name"));
        Assert.assertEquals(connectorProperties.getProperty("userType"), apiRestResponse.getBody().getString("class"));
        
    }
    
    /**
     *
     * Returns 403
     *
     * Negative test case for createClient method.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateClientWithMandatoryParameters" }, description = "activecollab {createClient} integration test with negative Case.")
    public void testCreateClientWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createClient");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createClient_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=people/"
                        + connectorProperties.getProperty("companyId") + "/add-user";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createClient_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        Assert.assertEquals(esbRestResponse.getBody().getString("type"), apiRestResponse.getBody().getString("type"));
        Assert.assertEquals(esbRestResponse.getBody().getString("object_class"),
                apiRestResponse.getBody().getString("object_class"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("field_errors").getString("email"), apiRestResponse
                .getBody().getJSONObject("field_errors").getString("email"));
        
    }
    
    /**
     * Positive test case for createTask method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "activecollab {createTask} integration test with mandatory parameters.")
    public void testCreateTaskWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTask");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_mandatory.json");
        
        String taskId = esbRestResponse.getBody().getString("task_id");
        connectorProperties.put("taskId", taskId);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/" + taskId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("taskName"), apiRestResponse.getBody().getString("name"));
        
    }
    
    /**
     * Positive test case for createTask method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "activecollab {createTask} integration test with optional parameters.")
    public void testCreateTaskWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTask");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_optional.json");
        
        String taskId = esbRestResponse.getBody().getString("task_id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/" + taskId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(connectorProperties.getProperty("taskName"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("taskDesc"), apiRestResponse.getBody().getString("body"));
        Assert.assertEquals(connectorProperties.getProperty("taskVisibility"),
                apiRestResponse.getBody().getString("visibility"));
        Assert.assertEquals(connectorProperties.getProperty("taskPriority"),
                apiRestResponse.getBody().getString("priority"));
        
    }
    
    /**
     * Negative test case for createTask method.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "activecollab {createTask} integration test with negative Case.")
    public void testCreateTaskWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTask");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/add";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createTask_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        Assert.assertEquals(esbRestResponse.getBody().getString("type"), apiRestResponse.getBody().getString("type"));
        Assert.assertEquals(esbRestResponse.getBody().getString("object_class"),
                apiRestResponse.getBody().getString("object_class"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("field_errors").getString("name"), apiRestResponse
                .getBody().getJSONObject("field_errors").getString("name"));
        
    }
    
    /**
     * Positive test case for updateTask method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateTaskWithMandatoryParameters" }, description = "activecollab {updateTask} integration test with optional parameters.")
    public void testUpdateTaskWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateTask");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/"
                        + connectorProperties.getProperty("taskId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String originalTaskName = apiRestResponse.getBody().getString("name");
        String originalTaskDesc = apiRestResponse.getBody().getString("body");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTask_mandatory.json");
        
        apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(originalTaskName, apiRestResponse.getBody().getString("name"));
        Assert.assertNotEquals(originalTaskDesc, apiRestResponse.getBody().getString("body"));
        
    }
    
    /**
     * Negative test case for updateTask method.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateTaskWithMandatoryParameters" }, description = "activecollab {updateTask} integration test with negative Case.")
    public void testUpdateTaskWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateTask");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTask_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/"
                        + connectorProperties.getProperty("taskId") + "/edit/";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateTask_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        Assert.assertEquals(esbRestResponse.getBody().getString("type"), apiRestResponse.getBody().getString("type"));
        Assert.assertEquals(esbRestResponse.getBody().getString("object_class"),
                apiRestResponse.getBody().getString("object_class"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("field_errors").getString("name"), apiRestResponse
                .getBody().getJSONObject("field_errors").getString("name"));
        
    }
    
    /**
     * Positive test case for getTask method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateTaskWithMandatoryParameters" }, description = "activecollab {getTask} integration test with mandatory parameters.")
    public void testGetTaskWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getTask");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTask_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/"
                        + connectorProperties.getProperty("taskId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("name").toString(), apiRestResponse.getBody().get("name")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().get("project_id").toString(),
                apiRestResponse.getBody().get("project_id").toString());
    }
    
    /**
     * Negative test case for getTask.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateTaskWithMandatoryParameters" }, description = "activecollab {getTask} integration test with negative case.")
    public void testGetTaskWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getTask");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTask_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/invalid";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("output"), apiRestResponse.getBody()
                .getString("output"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for createMilestone method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "activecollab {createMilestone} integration test with mandatory parameters.")
    public void testCreateMilestoneWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createMilestone");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createMilestone_mandatory.json");
        String milestoneId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("milestoneId", milestoneId);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/"
                        + connectorProperties.getProperty("projectId") + "/milestones/"
                        + connectorProperties.getProperty("milestoneId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("name"),
                connectorProperties.getProperty("milestoneName"));
        Assert.assertEquals(sdf.format(new Date()),
                apiRestResponse.getBody().getJSONObject("created_on").get("formatted_date"));
    }
    
    /**
     * Positive test case for createMilestone method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "activecollab {createMilestone} integration test with optional parameters.")
    public void testCreateMilestoneWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createMilestone");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createMilestone_optional.json");
        String milestoneIdOptional = esbRestResponse.getBody().getString("id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/"
                        + connectorProperties.getProperty("projectId") + "/milestones/" + milestoneIdOptional;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        
        Assert.assertEquals(apiRestResponse.getBody().getString("name"),
                connectorProperties.getProperty("milestoneNameOptional"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("start_on").get("mysql"),
                connectorProperties.getProperty("startOn"));
        Assert.assertEquals(sdf.format(new Date()),
                apiRestResponse.getBody().getJSONObject("created_on").get("formatted_date"));
        
    }
    
    /**
     * Negative test case for createMilestone.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "activecollab {createMilestone} integration test with negative case.")
    public void testCreateMilestoneWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createMilestone");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createMilestone_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/"
                        + connectorProperties.getProperty("projectId") + "/milestones/add";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createMilestone_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        Assert.assertEquals(esbRestResponse.getBody().getString("type"), apiRestResponse.getBody().getString("type"));
        Assert.assertEquals(esbRestResponse.getBody().getString("object_class"),
                apiRestResponse.getBody().getString("object_class"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("field_errors").getString("name"), apiRestResponse
                .getBody().getJSONObject("field_errors").getString("name"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for listCompanies method with mandatory parameters.
     */
    @Test(priority = 1,  description = "activecollab {listCompanies} integration test with mandatory parameters.")
    public void testListCompaniesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCompanies");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCompanies_mandatory.json");
        
       
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=people";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String esbResponseArrayString = apiRestResponse.getBody().getString("output");
        JSONArray esbResponseArray = new JSONArray(esbResponseArrayString);
        
        String apiResponseArrayString = apiRestResponse.getBody().getString("output");
        JSONArray apiResponseArray = new JSONArray(apiResponseArrayString);

        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        
        if(apiResponseArray.length() > 0 && esbResponseArray.length() > 0){
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0).getString("id"));
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("class"), apiResponseArray.getJSONObject(0).getString("class"));
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("name"), apiResponseArray.getJSONObject(0).getString("name"));
            Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        } 
        
    }
    /**
     * Positive test case for listCompanies method with optional parameters.
     */
    @Test(priority = 1,  description = "activecollab {listCompanies} integration test with optional parameters.")
    public void testListCompaniesWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCompanies");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCompanies_optional.json");
        
       
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=people/archive";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        if(apiRestResponse.getBody().getString("output").equals("null") && esbRestResponse.getBody().getString("output").equals("null")){
           
           Assert.assertEquals(apiRestResponse.getBody().getString("output"), esbRestResponse.getBody().getString("output"));
           
           
        }else {
            
            String esbResponseArrayString = esbRestResponse.getBody().getString("output");
            JSONArray esbResponseArray = new JSONArray(esbResponseArrayString);
            
            String apiResponseArrayString = apiRestResponse.getBody().getString("output");
            JSONArray apiResponseArray = new JSONArray(apiResponseArrayString);

            Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
            
            if(apiResponseArray.length() > 0 && esbResponseArray.length() > 0){
                Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0).getString("id"));
                Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("class"), apiResponseArray.getJSONObject(0).getString("class"));
                Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("name"), apiResponseArray.getJSONObject(0).getString("name"));
                Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
            } 
            
        }
        
    }
    
    /**
     * Positive test case for listProjects method with mandatory parameters.
     */
    @Test(priority = 1,  description = "activecollab {listProjects} integration test with mandatory parameters.")
    public void testListProjectsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listProjects");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjects_mandatory.json");
        
       
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String esbResponseArrayString = esbRestResponse.getBody().getString("output");
        JSONArray esbResponseArray = new JSONArray(esbResponseArrayString);
        
        String apiResponseArrayString = apiRestResponse.getBody().getString("output");
        JSONArray apiResponseArray = new JSONArray(apiResponseArrayString);

        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        
        if(apiResponseArray.length() > 0 && esbResponseArray.length() > 0){
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0).getString("id"));
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("class"), apiResponseArray.getJSONObject(0).getString("class"));
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("name"), apiResponseArray.getJSONObject(0).getString("name"));
            Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        } 
        
    }
    /**
     * Positive test case for listProjects method with optional parameters.
     */
    @Test(priority = 1,  description = "activecollab {listProjects} integration test with optional parameters.")
    public void testListProjectsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listProjects");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjects_optional.json");
        
       
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/archive";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        
        
        if(apiRestResponse.getBody().getString("output").equals("null") && esbRestResponse.getBody().getString("output").equals("null")){
           
           Assert.assertEquals(apiRestResponse.getBody().getString("output"), esbRestResponse.getBody().getString("output"));
           
           
        }else {
            
            String esbResponseArrayString = esbRestResponse.getBody().getString("output");
            JSONArray esbResponseArray = new JSONArray(esbResponseArrayString);
            
            String apiResponseArrayString = apiRestResponse.getBody().getString("output");
            JSONArray apiResponseArray = new JSONArray(apiResponseArrayString);

            Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
            
            if(apiResponseArray.length() > 0 && esbResponseArray.length() > 0){
                Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0).getString("id"));
                Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("class"), apiResponseArray.getJSONObject(0).getString("class"));
                Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("name"), apiResponseArray.getJSONObject(0).getString("name"));
                Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
            } 
            
        }

    }
    
    /**
     * Positive test case for listTasks method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "activecollab {listTasks} integration test with mandatory parameters.")
    public void testListTasksWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTasks");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_mandatory.json");
        
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/"
                        + connectorProperties.getProperty("projectId")+ "/tasks";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        if(apiRestResponse.getBody().getString("output").equals("null") && esbRestResponse.getBody().getString("output").equals("null")){
            
            Assert.assertEquals(apiRestResponse.getBody().getString("output"), esbRestResponse.getBody().getString("output"));
            
           
         }else{
             
             String esbResponseArrayString = esbRestResponse.getBody().getString("output");
             JSONArray esbResponseArray = new JSONArray(esbResponseArrayString);
             
             String apiResponseArrayString = apiRestResponse.getBody().getString("output");
             JSONArray apiResponseArray = new JSONArray(apiResponseArrayString);
             
             Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
             
             if(apiResponseArray.length() > 0 && esbResponseArray.length() > 0){
                 Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("name"), esbResponseArray.getJSONObject(0).getString("name"));
                 Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("class"), esbResponseArray.getJSONObject(0).getString("class"));
                 Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
             } 
             
         }
         
    }
    
    /**
     * Positive test case for listTasks method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "activecollab {listTasks} integration test with optional parameters.")
    public void testListTasksWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTasks");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_optional.json");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/"
                        + connectorProperties.getProperty("projectId")+ "/tasks/archive";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        if(apiRestResponse.getBody().getString("output").equals("null") && esbRestResponse.getBody().getString("output").equals("null")){
            
            Assert.assertEquals(apiRestResponse.getBody().getString("output"), esbRestResponse.getBody().getString("output"));
            
            
         }else {
             
             String esbResponseArrayString = esbRestResponse.getBody().getString("output");
             JSONArray esbResponseArray = new JSONArray(esbResponseArrayString);
             
             String apiResponseArrayString = apiRestResponse.getBody().getString("output");
             JSONArray apiResponseArray = new JSONArray(apiResponseArrayString);
             
             Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
             
             if(apiResponseArray.length() > 0 && esbResponseArray.length() > 0){
                 Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("name"), esbResponseArray.getJSONObject(0).getString("name"));
                 Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("class"), esbResponseArray.getJSONObject(0).getString("class"));
                 Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
             } 
            
        }
        
    }
    
    /**
     * Negative test case for listTasks.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "activecollab {listTasks} integration test with negative case.")
    public void testListTasksWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTasks");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects//tasks";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("output"), apiRestResponse.getBody()
                .getString("output"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for completeContext method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "activecollab {completeContext} integration test with mandatory parameters.")
    public void testCompleteContextWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:completeContext");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_completeContext_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects/"
                        + connectorProperties.getProperty("projectId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(apiRestResponse.getBody().getInt("is_completed"),
                1);
        Assert.assertEquals(sdf.format(new Date()),
                apiRestResponse.getBody().getJSONObject("completed_on").get("formatted_date"));
    }
    
    /**
     * Negative test case for completeContext.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "activecollab {completeContext} integration test with negative case.")
    public void testCompleteContextWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:completeContext");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_completeContext_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api.php?auth_api_token="
                        + connectorProperties.getProperty("apiToken") + "&format=json&path_info=projects//complete";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_completeContext_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("output"), apiRestResponse.getBody()
                .getString("output"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
}
