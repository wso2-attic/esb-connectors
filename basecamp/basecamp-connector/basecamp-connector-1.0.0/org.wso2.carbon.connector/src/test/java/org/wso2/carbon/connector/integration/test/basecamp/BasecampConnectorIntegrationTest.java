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

package org.wso2.carbon.connector.integration.test.basecamp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class BasecampConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> esbParametersMap = new HashMap<String, String>();
    
    private Map<String, String> apiParametersMap = new HashMap<String, String>();
    
    private String apiUrl, binaryUploadProxyUrl;
    
    private int SLEEP_TIME;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("basecamp-connector-1.0.0");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("accessToken"));
        apiUrl =
                connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("accountId")
                        + "/api/v1";
        binaryUploadProxyUrl = getProxyServiceURL("basecamp_createAttachment");
        SLEEP_TIME = Integer.parseInt(connectorProperties.getProperty("sleepTime"));
    }
    
    /**
     * Create a TodoList which is a Dependency but not in the List of methods that were implemented
     * 
     * @throws Exception
     */
    private void createTodoList() throws Exception {
    
        RestResponse<JSONObject> apiReponse =
                sendJsonRestRequest(apiUrl + "/projects/" + connectorProperties.getProperty("projectId")
                        + "/todolists.json", "POST", apiRequestHeadersMap, "api_createTodoList.json");
        connectorProperties.put("todoListId", apiReponse.getBody().get("id").toString());
    }
    
    /**
     * Positive test case for createProject method with mandatory parameters.
     * 
     * @throws Exception
     */
    @Test(priority = 1, description = "Test createProject{BaseCamp} with Mandatory Parameters")
    public void testCreateProjectWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createProject");
        RestResponse<JSONObject> esbRestReponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_mandatory.json");
        
        connectorProperties.put("projectId", esbRestReponse.getBody().get("id").toString());
        createTodoList();
        
        String apiEndpoint = apiUrl + "/projects/" + esbRestReponse.getBody().getString("id") + ".json";
        
        RestResponse<JSONObject> apiRestReponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestReponse.getHttpStatusCode(), 201);
        Assert.assertEquals(esbRestReponse.getBody().getString("id"), apiRestReponse.getBody().getString("id"));
        Assert.assertEquals(esbRestReponse.getBody().getString("name"), apiRestReponse.getBody().getString("name"));
    }
    
    /**
     * Positive test case for createProject method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "Test createProject{BaseCamp} with optional parameters")
    public void testCreateProjectWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createProject");
        RestResponse<JSONObject> esbReponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_optional.json");
        
        String apiEndpoint = apiUrl + "/projects/" + esbReponse.getBody().getString("id") + ".json";
        
        RestResponse<JSONObject> apiReponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbReponse.getBody().getString("id"), apiReponse.getBody().getString("id"));
        Assert.assertEquals(esbReponse.getBody().getString("name"), apiReponse.getBody().getString("name"));
        Assert.assertEquals(esbReponse.getBody().getString("description"), apiReponse.getBody()
                .getString("description"));
    }
    
    /**
     * Negative test case for createProject method with invalid parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectWithOptionalParameters" }, description = "Test createProject{BaseCamp} with negative case")
    public void testCreateProjectWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createProject");
        RestResponse<JSONObject> esbReponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_negative.json");
        
        String apiEndpoint = apiUrl + "/projects" + ".json";
        RestResponse<JSONObject> apiReponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createProject_negative.json");
        
        Assert.assertEquals(esbReponse.getHttpStatusCode(), apiReponse.getHttpStatusCode());
        Assert.assertEquals(esbReponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiReponse.getHttpStatusCode(), 422);
    }
    
    /**
     * Positive test case for getProject method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = "testCreateProjectWithNegativeCase", description = "Test getProject{BaseCamp} with Mandatory Parameters")
    public void testGetProjectWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getProject");
        RestResponse<JSONObject> esbReponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProject_mandatory.json");
        RestResponse<JSONObject> apiReponse =
                sendJsonRestRequest(apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + ".json",
                        "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbReponse.getBody().getString("id"), apiReponse.getBody().getString("id"));
        Assert.assertEquals(esbReponse.getBody().getString("name"), apiReponse.getBody().getString("name"));
    }
    
    /**
     * Negative test case for getProject method with invalid parameters.
     */
    @Test(priority = 1, dependsOnMethods = "testGetProjectWithMandatoryParameters", description = "Test getProject{BaseCamp} with negative case")
    public void testGetProjectWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getProject");
        RestResponse<JSONObject> esbReponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProject_negative.json");
        RestResponse<JSONObject> apiReponse =
                sendJsonRestRequest(apiUrl + "/projects/INVALID.json", "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbReponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbReponse.getHttpStatusCode(), apiReponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for listProjects method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = "testGetProjectWithNegativeCase", description = "Test listProjects{BaseCamp} with Mandatory Parameters")
    public void testListProjectsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listProjects");
        String apiEndpoint = apiUrl + "/projects.json";
        RestResponse<JSONObject> esbReponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjects_mandatory.json");
        RestResponse<JSONObject> apiReponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        JSONArray esbJsonArray = new JSONArray(esbReponse.getBody().getString("output"));
        JSONArray apiJsonArray = new JSONArray(apiReponse.getBody().getString("output"));
        Assert.assertEquals(esbJsonArray.length(), apiJsonArray.length());
        Assert.assertEquals(esbJsonArray.getJSONObject(0).get("id").toString(), apiJsonArray.getJSONObject(0).get("id")
                .toString());
        Assert.assertEquals(esbJsonArray.getJSONObject(0).get("name").toString(), apiJsonArray.getJSONObject(0).get("name")
                .toString());
    }
    
    /**
     * Positive test case for listProjects method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = "testListProjectsWithMandatoryParameters", description = "Test listProjects{BaseCamp} with Mandatory Parameters")
    public void testListProjectsWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listProjects");
        String apiEndpoint = apiUrl + "/projects.json";
        RestResponse<JSONObject> esbReponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjects_optional.json");
        RestResponse<JSONObject> apiReponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        JSONArray esbJsonArray = new JSONArray(esbReponse.getBody().getString("output"));
        JSONArray apiJsonArray = new JSONArray(apiReponse.getBody().getString("output"));
        Assert.assertEquals(esbJsonArray.length(), apiJsonArray.length());
        Assert.assertEquals(esbJsonArray.getJSONObject(0).get("id").toString(), apiJsonArray.getJSONObject(0).get("id")
                .toString());
        Assert.assertEquals(esbJsonArray.getJSONObject(0).get("name").toString(), apiJsonArray.getJSONObject(0).get("name")
                .toString());
    }
    
    /**
     * Positive test case for createCalendarEvent method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testListProjectsWithOptionalParameters" }, description = "BaseCamp {createCalendarEvent} integration test with mandatory parameters.")
    public void testCreateCalendarEventWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createCalendarEvent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCalendarEvent_mandatory.json");
        connectorProperties.put("calendarEventId", esbRestResponse.getBody().get("id").toString());
        String apiEndPoint =
                apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/calendar_events/"
                        + connectorProperties.getProperty("calendarEventId") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(esbRestResponse.getBody().getString("private"),
                apiRestResponse.getBody().getString("private"));
        Assert.assertEquals(esbRestResponse.getBody().getString("description"),
                apiRestResponse.getBody().getString("description"));
        Assert.assertEquals(esbRestResponse.getBody().getString("all_day"),
                apiRestResponse.getBody().getString("all_day"));
        
    }
    
    /**
     * Positive test case for createCalendarEvent method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateCalendarEventWithMandatoryParameters" }, description = "BaseCamp {createCalendarEvent} integration test with optional parameters.")
    public void testCreateCalendarEventWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createCalendarEvent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCalendarEvent_optional.json");
        connectorProperties.put("calendarEventIdOptional", esbRestResponse.getBody().getString("id"));
        String apiEndPoint =
                apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/calendar_events/"
                        + connectorProperties.getProperty("calendarEventIdOptional") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(esbRestResponse.getBody().getString("summary"),
                apiRestResponse.getBody().getString("summary"));
        Assert.assertEquals(esbRestResponse.getBody().getString("description"),
                apiRestResponse.getBody().getString("description"));
        Assert.assertEquals(esbRestResponse.getBody().getString("all_day"),
                apiRestResponse.getBody().getString("all_day"));
        Assert.assertEquals(esbRestResponse.getBody().getString("private"),
                apiRestResponse.getBody().getString("private"));
    }
    
    /**
     * Negative test case for createCalendarEvent method.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateCalendarEventWithOptionalParameters" }, description = "BaseCamp {createCalendarEvent} integration test case for negative case.")
    public void testCreateCalendarEventWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createCalendarEvent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCalendarEvent_negative.json");
        String apiEndPoint =
                apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/calendar_events.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createCalendarEvent_negative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }
    
    /**
     * Positive test case for listCalendarEvents method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testCreateCalendarEventWithNegativeCase" }, description = "BaseCamp {listCalendarEvents} integration test with mandatory parameters.")
    public void testListCalendarEventsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCalendarEvents");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCalendarEvents_mandatory.json");
        
        String apiEndPoint =
                apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/calendar_events.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String apiResponse = apiRestResponse.getBody().getString("output").toString();
        String esbResponse = esbRestResponse.getBody().getString("output").toString();
        
        org.json.JSONArray esbJsonAry = new org.json.JSONArray(esbResponse);
        org.json.JSONArray apiJsonAry = new org.json.JSONArray(apiResponse);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

    }
    
    /**
     * Positive test case for listCalendarEvents method with optional parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testListCalendarEventsWithMandatoryParameters" }, description = "BaseCamp {listCalendarEvents} integration test with optional parameters.")
    public void testListCalendarEventsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCalendarEvents");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCalendarEvents_optional.json");
        
        String apiEndPoint =
                apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/calendar_events/past.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String apiResponse = apiRestResponse.getBody().getString("output").toString();
        String esbResponse = esbRestResponse.getBody().getString("output").toString();
        
        org.json.JSONArray esbJsonAry = new org.json.JSONArray(esbResponse);
        org.json.JSONArray apiJsonAry = new org.json.JSONArray(apiResponse);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
       
    }
    
    /**
     * Negative test case for listCalendarEvents method.
     */
    @Test(priority = 2, dependsOnMethods = { "testListCalendarEventsWithOptionalParameters" }, description = "BaseCamp {listCalendarEvents} integration test case for negative case.")
    public void testListCalendarEventsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCalendarEvents");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCalendarEvents_negative.json");
        
        String apiEndPoint = apiUrl + "/projects/Invalid/calendar_events.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for getCalendarEvent method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testListCalendarEventsWithNegativeCase" }, description = "Test getCalendarEvent{BaseCamp} with Mandatory Parameters")
    public void testGetCalendarEventWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getCalendarEvent");
        RestResponse<JSONObject> esbReponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCalendarEvent_mandatory.json");
        String apiEndpoint =
                apiUrl + "/projects/" + connectorProperties.getProperty("projectId").toString() + "/calendar_events/"
                        + connectorProperties.getProperty("calendarEventId").toString() + ".json";
        RestResponse<JSONObject> apiReponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbReponse.getBody().getString("id"), apiReponse.getBody().getString("id"));
        Assert.assertEquals(esbReponse.getBody().getString("starts_at"), apiReponse.getBody().getString("starts_at"));
        Assert.assertEquals(esbReponse.getBody().getString("summary"), apiReponse.getBody().getString("summary"));
    }
    
    /**
     * Negative test case for getCalendarEvent method with invalid parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetCalendarEventWithMandatoryParameters" }, description = "Test getCalendarEvent{BaseCamp} with negative case")
    public void testGetCalendarEventWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getCalendarEvent");
        RestResponse<JSONObject> esbReponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCalendarEvent_optional.json");
        RestResponse<JSONObject> apiReponse =
                sendJsonRestRequest(apiUrl + "/projects/" + connectorProperties.getProperty("projectId").toString()
                        + "/calendar_events/INVALID.json", "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbReponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbReponse.getHttpStatusCode(), apiReponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for listPeople method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testGetCalendarEventWithNegativeCase" }, description = "Basecamp {listPeople} integration test with mandatory parameters.")
    public void testListPeopleWithMandatoryParameters() throws JSONException, IOException {
    
        esbRequestHeadersMap.put("Action", "urn:listPeople");
        String apiEndpoint = apiUrl + "/people.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPeople_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        String esbResponse = esbRestResponse.getBody().get("output").toString();
        String apiResponse = apiRestResponse.getBody().get("output").toString();
        
        JSONArray esbJsonAry = new org.json.JSONArray(esbResponse);
        JSONArray apiJsonAry = new org.json.JSONArray(apiResponse);
        connectorProperties.put("personId", esbJsonAry.getJSONObject(0).get("id").toString());
        Assert.assertEquals(esbJsonAry.getJSONObject(0).get("id").toString(), apiJsonAry.getJSONObject(0).get("id")
                .toString());
        Assert.assertEquals(esbJsonAry.getJSONObject(0).get("name").toString(), apiJsonAry.getJSONObject(0).get("name")
                .toString());
        Assert.assertEquals(esbJsonAry.length(), apiJsonAry.length());
    }

    /**
     * Negative test case for listPeople method.
     */
    @Test(priority = 2, dependsOnMethods = { "testListPeopleWithMandatoryParameters" }, description = "Basecamp {listPeople} integration test with negative case.")
    public void testListPeopleWithNegativeCase() throws JSONException, IOException {
    
        esbRequestHeadersMap.put("Action", "urn:listPeople");
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/-/api/v1/people.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPeople_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Positive test case for listTodoLists method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = "testListPeopleWithMandatoryParameters", description = "Test listTodoLists{BaseCamp} with Mandatory Parameters")
    public void testListTodoListsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listTodoLists");
        String apiEndpoint = apiUrl + "/todolists.json";
        RestResponse<JSONObject> esbReponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTodoLists_mandatory.json");
        RestResponse<JSONObject> apiReponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        JSONArray esbJsonArray = new JSONArray(esbReponse.getBody().getString("output"));
        JSONArray apiJsonArray = new JSONArray(apiReponse.getBody().getString("output"));
        Assert.assertEquals(esbReponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbJsonArray.length(), apiJsonArray.length());
        Assert.assertEquals(esbJsonArray.getJSONObject(0).get("id").toString(), apiJsonArray.getJSONObject(0).get("id")
                .toString());
        Assert.assertEquals(esbJsonArray.getJSONObject(0).get("name").toString(), apiJsonArray.getJSONObject(0).get("name")
                .toString());
        
    }
    
    /**
     * Positive test case for listTodoLists method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testListTodoListsWithMandatoryParameters" }, description = "Test listTodoLists{BaseCamp} with optional parameters")
    public void testListTodoListsWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listTodoLists");
        RestResponse<JSONObject> esbReponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTodoLists_optional.json");
        RestResponse<JSONObject> apiReponse =
                sendJsonRestRequest(apiUrl + "/projects/" + connectorProperties.getProperty("projectId")
                        + "/todolists.json", "GET", apiRequestHeadersMap);
        JSONArray esbJsonArray = new JSONArray(esbReponse.getBody().getString("output"));
        JSONArray apiJsonArray = new JSONArray(apiReponse.getBody().getString("output"));
        Assert.assertEquals(esbReponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbJsonArray.length(), apiJsonArray.length());
        Assert.assertEquals(esbJsonArray.getJSONObject(0).get("id").toString(), apiJsonArray.getJSONObject(0).get("id")
                .toString());
        Assert.assertEquals(esbJsonArray.getJSONObject(0).get("name").toString(), apiJsonArray.getJSONObject(0).get("name")
                .toString());
    }
    
    /**
     * Negative test case for listTodoLists method with invalid parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testListTodoListsWithOptionalParameters" }, description = "Test listTodoLists{BaseCamp} with negative case")
    public void testListTodoListsWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listTodoLists");
        RestResponse<JSONObject> esbReponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTodoLists_negative.json");
        RestResponse<JSONObject> apiReponse =
                sendJsonRestRequest(apiUrl + "/projects/INVALID/todolists.json", "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbReponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbReponse.getHttpStatusCode(), apiReponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for createMessage method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testListTodoListsWithNegativeCase" }, description = "Basecamp {createMessage} integration test with mandatory parameters.")
    public void testCreateMessageMandatoryParameters() throws JSONException, IOException {
    
        esbRequestHeadersMap.put("Action", "urn:createMessage");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createMessage_mandatory.json");
        connectorProperties.put("messageId", esbRestResponse.getBody().get("id").toString());
        String apiEndpoint =
                apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/messages/"
                        + connectorProperties.getProperty("messageId") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getBody().get("id").toString(), connectorProperties
                .getProperty("messageId").toString());
    }
    
    /**
     * Positive test case for createMessage method with optional parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testCreateMessageMandatoryParameters" }, description = "Basecamp {createMessage} integration test with optional parameters.")
    public void testCreateMessageOptionalParameters() throws JSONException, IOException {
    
        esbRequestHeadersMap.put("Action", "urn:createMessage");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createMessage_optional.json");
        connectorProperties.put("messageId", esbRestResponse.getBody().get("id").toString());
        String apiEndpoint =
                apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/messages/"
                        + connectorProperties.getProperty("messageId") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getBody().get("id").toString(), connectorProperties
                .getProperty("messageId").toString());
    }
    
    /**
     * Negative test case for createMessage method.
     */
    @Test(priority = 2, dependsOnMethods = { "testCreateMessageOptionalParameters" }, description = "Basecamp {createMessage} integration test negative case.")
    public void testCreateMessageNegativeCase() throws JSONException, IOException {
    
        esbRequestHeadersMap.put("Action", "urn:createMessage");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createMessage_negative.json");
        String apiEndpoint = apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/messages.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createMessage_negative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for getMessage method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testCreateMessageNegativeCase" }, description = "Basecamp {getMessage} integration test with mandatory parameters.")
    public void testGetMessageWithMandatoryParameters() throws JSONException, IOException {
    
        esbRequestHeadersMap.put("Action", "urn:getMessage");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMessage_mandatory.json");
        connectorProperties.put("messageId", esbRestResponse.getBody().get("id").toString());
        String apiEndpoint =
                apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/messages/"
                        + connectorProperties.getProperty("messageId") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("id").toString(), esbRestResponse.getBody().get("id")
                .toString());
        Assert.assertEquals(apiRestResponse.getBody().get("created_at").toString(),
                esbRestResponse.getBody().get("created_at").toString());
        
    }
    
    /**
     * Negative test case for getMessage method
     */
    @Test(priority = 2, dependsOnMethods = { "testGetMessageWithMandatoryParameters" }, description = "Basecamp {getMessage} integration test case for negative case.")
    public void testGetMessageWithNegativeCase() throws JSONException, IOException {
    
        esbRequestHeadersMap.put("Action", "urn:getMessage");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMessage_negative.json");
        
        String apiEndpoint = apiUrl + "/projects/-/messages/" + connectorProperties.getProperty("messageId") + ".json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for createTodo method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testGetMessageWithNegativeCase" }, description = "Basecamp {createTodo} integration test with mandatory parameters.")
    public void testCreateTodoWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTodo");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTodo_mandatory.json");
        
        connectorProperties.setProperty("todoId", esbRestResponse.getBody().getString("id"));
        
        String apiEndPoint =
                apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/todos/"
                        + connectorProperties.getProperty("todoId") + ".json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(esbRestResponse.getBody().getString("content").toString(), apiRestResponse.getBody()
                .getString("content").toString());
        Assert.assertEquals(esbRestResponse.getBody().getString("position").toString(), apiRestResponse.getBody()
                .getString("position").toString());
    }
    
    /**
     * Positive test case for createTodo method with optional parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testCreateTodoWithMandatoryParameters" }, description = "Basecamp {createTodo} integration test with optional parameters.")
    public void testCreateTodoWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTodo");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTodo_optional.json");
        
        connectorProperties.setProperty("todoOptionalId", esbRestResponse.getBody().getString("id"));
        
        String apiEndPoint =
                apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/todos/"
                        + connectorProperties.getProperty("todoOptionalId") + ".json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(esbRestResponse.getBody().getString("content").toString(), apiRestResponse.getBody()
                .getString("content").toString());
        Assert.assertEquals(esbRestResponse.getBody().getString("due_at").toString(), apiRestResponse.getBody()
                .getString("due_at").toString());
    }
    
    /**
     * Negative test case for createTodo method.
     */
    @Test(priority = 2, dependsOnMethods = { "testCreateTodoWithOptionalParameters" }, description = "Basecamp {createTodo} integration test case for negative case.")
    public void testCreateTodoWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTodo");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTodo_negative.json");
        
        String apiEndPoint =
                apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/todolists/"
                        + connectorProperties.getProperty("todoListId") + "/todos.json";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createTodo_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("content").get(0), apiRestResponse.getBody()
                .getJSONArray("content").get(0));
    }
    
    /**
     * Positive test case for getTodo method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testCreateTodoWithNegativeCase" }, description = "Basecamp {getTodo} integration test with mandatory parameters.")
    public void testGetTodoWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getTodo");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTodo_mandatory.json");
        
        String apiEndPoint =
                apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/todos/"
                        + connectorProperties.getProperty("todoId") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("content").toString(),
                apiRestResponse.getBody().get("content").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("created_at").toString(),
                apiRestResponse.getBody().get("created_at").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("position").toString(),
                apiRestResponse.getBody().get("position").toString());
    }
    
    /**
     * Negative test case for getTodo method.
     */
    @Test(priority = 2, dependsOnMethods = { "testGetTodoWithMandatoryParameters" }, description = "Basecamp {getTodo} integration test for Negative case.")
    public void testGetTodoWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getTodo");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTodo_negative.json");
        
        String apiEndPoint = apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/todos/-.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Positive test case for createAttachment method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testGetTodoWithNegativeCase" }, description = "Basecamp {createAttachment} integration test with mandatory parameters.")
    public void testCreateAttachmentWithMandatoryParameters() throws IOException, JSONException {
    
        binaryUploadProxyUrl +=
                "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&accountId="
                        + connectorProperties.getProperty("accountId");
        
        String apiEndPoint = apiUrl + "/attachments.json";
        
        esbRequestHeadersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("accessToken"));
        esbRequestHeadersMap.put("Content-Type", connectorProperties.getProperty("contentType"));
        
        apiRequestHeadersMap.put("Content-Type", connectorProperties.getProperty("contentType"));
        
        final MultipartFormdataProcessor esbFileRequestProcessor =
                new MultipartFormdataProcessor(binaryUploadProxyUrl, esbRequestHeadersMap);
        final File file = new File(pathToResourcesDirectory + connectorProperties.getProperty("txtFileName"));
        esbFileRequestProcessor.addFiletoRequestBody(file);
        final RestResponse<JSONObject> esbRestResponse = esbFileRequestProcessor.processAttachmentForJsonResponse();
        
        final MultipartFormdataProcessor apiFileRequestProcessor =
                new MultipartFormdataProcessor(apiEndPoint, apiRequestHeadersMap);
        final File apiFile = new File(pathToResourcesDirectory + connectorProperties.getProperty("txtFileName"));
        apiFileRequestProcessor.addFiletoRequestBody(apiFile);
        final RestResponse<JSONObject> apiRestResponse = apiFileRequestProcessor.processAttachmentForJsonResponse();
        
        // Stores the created token in property file to be used in other test cases.
        connectorProperties.setProperty("token", esbRestResponse.getBody().get("token").toString());
        connectorProperties.setProperty("apiToken", apiRestResponse.getBody().get("token").toString());
        final MultipartFormdataProcessor apiFileRequestProcessorToken =
                new MultipartFormdataProcessor(apiEndPoint, apiRequestHeadersMap);
        final File apiFileToken = new File(pathToResourcesDirectory + connectorProperties.getProperty("txtFileName"));
        apiFileRequestProcessorToken.addFiletoRequestBody(apiFileToken);
        final RestResponse<JSONObject> apiRestResponseToken =
                apiFileRequestProcessorToken.processAttachmentForJsonResponse();
        connectorProperties.setProperty("commentToken", apiRestResponseToken.getBody().get("token").toString());
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponseToken.getHttpStatusCode(), 200);
        Assert.assertFalse("".equals(esbRestResponse.getBody().get("token").toString()));
        Assert.assertFalse("".equals(apiRestResponseToken.getBody().get("token").toString()));
    }
    
    /**
     * Negative test case for createAttachment method.
     */
    @Test(priority = 2, dependsOnMethods = { "testCreateAttachmentWithMandatoryParameters" }, description = "Basecamp {createAttachment} integration test for Negative case.")
    public void testCreateAttachmentWithNegativeCase() throws IOException, JSONException {
    
        binaryUploadProxyUrl += "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&accountId=0";
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/0/api/v1/attachments.json";
        
        final MultipartFormdataProcessor esbFileRequestProcessor =
                new MultipartFormdataProcessor(binaryUploadProxyUrl, esbRequestHeadersMap);
        final File file = new File(pathToResourcesDirectory + connectorProperties.getProperty("txtFileName"));
        esbFileRequestProcessor.addFiletoRequestBody(file);
        final RestResponse<JSONObject> esbRestResponse = esbFileRequestProcessor.processAttachmentForJsonResponse();
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        
        final MultipartFormdataProcessor apiFileRequestProcessor =
                new MultipartFormdataProcessor(apiEndPoint, apiRequestHeadersMap);
        final File apiFile = new File(pathToResourcesDirectory + connectorProperties.getProperty("txtFileName"));
        apiFileRequestProcessor.addFiletoRequestBody(apiFile);
        final RestResponse<JSONObject> apiRestResponse = apiFileRequestProcessor.processAttachmentForJsonResponse();
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
        // Resets headers added for createAttachment binary upload.
        esbRequestHeadersMap.remove("Authorization");
        apiRequestHeadersMap.remove("Content-Type");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
    }
    
    /**
     * Positive test case for listAttachments method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testCreateAttachmentWithNegativeCase" }, description = "Basecamp {listAttachments} integration test with mandatory parameters.")
    public void testListAttachmentsWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(SLEEP_TIME);
        esbRequestHeadersMap.put("Action", "urn:listAttachments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAttachments_mandatory.json");
        
        String apiEndPoint = apiUrl + "/attachments.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String esbResponse = esbRestResponse.getBody().get("output").toString();
        String apiResponse = apiRestResponse.getBody().get("output").toString();
        
        JSONArray esbJsonAry = new JSONArray(esbResponse);
        JSONArray apiJsonAry = new JSONArray(apiResponse);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbJsonAry.length(), apiJsonAry.length());
        Assert.assertEquals(esbJsonAry.getJSONObject(0).get("id").toString(), apiJsonAry.getJSONObject(0).get("id")
                .toString());
        Assert.assertEquals(esbJsonAry.getJSONObject(0).get("name").toString(), apiJsonAry.getJSONObject(0).get("name")
                .toString());
        
    }
    
    /**
     * Positive test case for listAttachments method with optional parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testListAttachmentsWithMandatoryParameters" }, description = "Basecamp {listAttachments} integration test with optional parameters.")
    public void testListAttachmentsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listAttachments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAttachments_optional.json");
        
        String apiEndPoint = apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/attachments.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String esbResponse = esbRestResponse.getBody().get("output").toString();
        String apiResponse = apiRestResponse.getBody().get("output").toString();
        
        JSONArray esbJsonAry = new JSONArray(esbResponse);
        JSONArray apiJsonAry = new JSONArray(apiResponse);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbJsonAry.length(), apiJsonAry.length());
        
    }
    
    /**
     * Negative test case for listAttachments method.
     */
    @Test(priority = 2, dependsOnMethods = { "testListAttachmentsWithOptionalParameters" }, description = "Basecamp {listAttachments} integration test with negative case.")
    public void testListAttachmentsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listAttachments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAttachments_negative.json");
        
        String apiEndPoint = apiUrl + "/projects/-/attachments.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for grantAccess method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testListAttachmentsWithNegativeCase" }, description = "Basecamp {grantAccess} integration test with mandatory parameters.")
    public void testGrantAccessWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:grantAccess");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_grantAccess_mandatory.json");
        
        String apiEndPoint = apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/accesses.json";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_grantAccess_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 204);
        Assert.assertNull(esbRestResponse.getBody());
        
    }
    
    /**
     * Positive test case for grantAccess method with optional parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testGrantAccessWithMandatoryParameters" }, description = "Basecamp {grantAccess} integration test with optional parameters.")
    public void testGrantAccessWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(SLEEP_TIME);
        esbRequestHeadersMap.put("Action", "urn:grantAccess");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_grantAccess_optional.json");
        
        String apiEndPoint = apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/accesses.json";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_grantAccess_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 204);
        Assert.assertNull(esbRestResponse.getBody());
        
    }
    
    /**
     * Negative test case for grantAccess method.
     */
    @Test(priority = 2, dependsOnMethods = { "testGrantAccessWithOptionalParameters" }, description = "Basecamp {grantAccess} integration test with negative case.")
    public void testGrantAccessWithNegativeCase() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(SLEEP_TIME);
        esbRequestHeadersMap.put("Action", "urn:grantAccess");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_grantAccess_negative.json");
        
        String apiEndPoint = apiUrl + "/projects/-/accesses.json";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_grantAccess_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for createUploads method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testGrantAccessWithNegativeCase" }, description = "Basecamp {createUploads} integration test with mandatory parameters.")
    public void testCreateUploadsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createUploads");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createUploads_mandatory.json");
        
        connectorProperties.setProperty("uploadId", esbRestResponse.getBody().get("id").toString());
        String apiEndPoint =
                apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/uploads/"
                        + connectorProperties.getProperty("uploadId") + ".json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("content").toString(),
                apiRestResponse.getBody().get("content").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("private").toString(),
                apiRestResponse.getBody().get("private").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("trashed").toString(),
                apiRestResponse.getBody().get("trashed").toString());
        
    }
    
    /**
     * Positive test case for createUploads method with optional parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testCreateUploadsWithMandatoryParameters" }, description = "Basecamp {createUploads} integration test with optional parameters.")
    public void testCreateUploadsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createUploads");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createUploads_optional.json");
        
        connectorProperties.setProperty("optionalUploadId", esbRestResponse.getBody().get("id").toString());
        String apiEndPoint =
                apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/uploads/"
                        + connectorProperties.getProperty("optionalUploadId") + ".json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("content").toString(),
                connectorProperties.getProperty("uploadContent"));
        Assert.assertEquals(esbRestResponse.getBody().get("content").toString(),
                apiRestResponse.getBody().get("content").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("trashed").toString(),
                apiRestResponse.getBody().get("trashed").toString());
    }
    
    /**
     * Negative test case for createUploads method.
     */
    @Test(priority = 2, dependsOnMethods = { "testCreateUploadsWithOptionalParameters" }, description = "Basecamp {createUploads} integration test with negative case.")
    public void testCreateUploadsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createUploads");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createUploads_negative.json");
        
        String apiEndPoint = apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/uploads.json";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createUploads_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for getUpload method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testCreateUploadsWithNegativeCase" }, description = "Basecamp {getUpload} integration test with mandatory parameters.")
    public void testGetUploadWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getUpload");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUpload_mandatory.json");
        
        String apiEndPoint =
                apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/uploads/"
                        + connectorProperties.getProperty("uploadId") + ".json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("content").toString(),
                apiRestResponse.getBody().get("content").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("private").toString(),
                apiRestResponse.getBody().get("private").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("trashed").toString(),
                apiRestResponse.getBody().get("trashed").toString());
        
    }
    
    /**
     * Negative test case for getUpload method.
     */
    @Test(priority = 2, dependsOnMethods = { "testGetUploadWithMandatoryParameters" }, description = "Basecamp {getUpload} integration test case for negative case.")
    public void testGetUploadWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getUpload");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUpload_negative.json");
        
        String apiEndPoint = apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/uploads/-.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for listTopics method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testGetUploadWithNegativeCase" }, description = "Basecamp {listTopics} integration test with mandatory parameters.")
    public void testListTopicsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTopics");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTopics_mandatory.json");
        
        String apiEndPoint = apiUrl + "/topics.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String esbResponse = esbRestResponse.getBody().get("output").toString();
        String apiResponse = apiRestResponse.getBody().get("output").toString();
        
        JSONArray esbJsonAry = new JSONArray(esbResponse);
        JSONArray apiJsonAry = new JSONArray(apiResponse);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbJsonAry.length(), apiJsonAry.length());
        Assert.assertEquals(esbJsonAry.getJSONObject(0).get("id").toString(), apiJsonAry.getJSONObject(0).get("id")
                .toString());
        Assert.assertEquals(esbJsonAry.getJSONObject(0).get("created_at").toString(), apiJsonAry.getJSONObject(0).get("created_at")
                .toString());
    }
    
    /**
     * Positive test case for listTopics method with optional parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testListTopicsWithMandatoryParameters" }, description = "Basecamp {listTopics} integration test with optional parameters.")
    public void testListTopicsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTopics");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTopics_optional.json");
        
        String apiEndPoint = apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/topics.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String esbResponse = esbRestResponse.getBody().get("output").toString();
        String apiResponse = apiRestResponse.getBody().get("output").toString();
        
        JSONArray esbJsonAry = new JSONArray(esbResponse);
        JSONArray apiJsonAry = new JSONArray(apiResponse);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbJsonAry.length(), apiJsonAry.length());
        Assert.assertEquals(esbJsonAry.getJSONObject(0).get("id").toString(), apiJsonAry.getJSONObject(0).get("id")
                .toString());
        Assert.assertEquals(esbJsonAry.getJSONObject(0).get("created_at").toString(), apiJsonAry.getJSONObject(0).get("created_at")
                .toString());
    }
    
    /**
     * Negative test case for listTopics method with optional parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testListTopicsWithOptionalParameters" }, description = "Basecamp {listTopics} integration test case for negative case .")
    public void testListTopicsNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTopics");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTopics_negative.json");
        
        String apiEndPoint = apiUrl + "/projects/-/topics.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for listTodoListsAssignedTodos method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testListTopicsNegativeCase" }, description = "Basecamp {listTodoListsAssignedTodos} integration test with mandatory parameters.")
    public void testListTodoListsAssignedTodosWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTodoListsAssignedTodos");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_listTodoListsAssignedTodos_mandatory.json");
        
        String apiEndPoint = apiUrl + "/people/" + connectorProperties.getProperty("personId") + "/assigned_todos.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String esbResponse = esbRestResponse.getBody().get("output").toString();
        String apiResponse = apiRestResponse.getBody().get("output").toString();
        
        JSONArray esbJsonAry = new JSONArray(esbResponse);
        JSONArray apiJsonAry = new JSONArray(apiResponse);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbJsonAry.length(), apiJsonAry.length());
    }
    
    /**
     * Negative test case for listTodoListsAssignedTodos method with negative case.
     */
    @Test(priority = 2, dependsOnMethods = { "testListTodoListsAssignedTodosWithMandatoryParameters" }, description = "Basecamp {listTodoListsAssignedTodos} integration test case with negative case.")
    public void testListTodoListsAssignedTodosWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTodoListsAssignedTodos");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_listTodoListsAssignedTodos_negative.json");
        
        String apiEndPoint = apiUrl + "/people/-/assigned_todos.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for starProject method.
     */
    @Test(priority = 2, dependsOnMethods = { "testListTodoListsAssignedTodosWithNegativeCase" }, description = "Basecamp {starProject} integration test with mandatory parameters.")
    public void testStarProjectMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:starProject");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_starProject_mandatory.json");
        
        String apiEndPoint = apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/star.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_starProject_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 201);
    }
    
    /**
     * Negative test case for starProject method.
     */
    @Test(priority = 2, dependsOnMethods = { "testStarProjectMandatoryParameters" }, description = "Basecamp {starProject} integration test case with negative case.")
    public void testStarProjectWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:starProject");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_starProject_negative.json");
        
        String apiEndPoint = apiUrl + "/projects/invalid/star.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_starProject_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Positive test case for listStars method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testStarProjectWithNegativeCase" }, description = "Basecamp {listStars} integration test with mandatory parameters.")
    public void testListStarsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listStars");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listStars_mandatory.json");
        
        String apiEndPoint = apiUrl + "/stars.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String esbResponse = esbRestResponse.getBody().get("output").toString();
        String apiResponse = apiRestResponse.getBody().get("output").toString();
        
        JSONArray esbJsonAry = new JSONArray(esbResponse);
        JSONArray apiJsonAry = new JSONArray(apiResponse);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbJsonAry.length(), apiJsonAry.length());
        Assert.assertEquals(esbJsonAry.getJSONObject(0).get("project_id").toString(), apiJsonAry.getJSONObject(0).get("project_id")
                .toString());
        Assert.assertEquals(esbJsonAry.getJSONObject(0).get("created_at").toString(), apiJsonAry.getJSONObject(0).get("created_at")
                .toString());
    }
    
    /**
     * Negative test case for listStars method.
     */
    @Test(priority = 2, dependsOnMethods = { "testListStarsWithMandatoryParameters" }, description = "Basecamp {listStars} integration test with Negative case.")
    public void testListStarsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listStars");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listStars_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/-/api/v1/stars.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Positive test case for listGlobalEvents method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testListStarsWithNegativeCase" }, description = "Test listGlobalEvents{BaseCamp} with Mandatory Parameters")
    public void testListGlobalEventsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listGlobalEvents");
        RestResponse<JSONObject> esbReponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listGlobalEvents_mandatory.json");
        RestResponse<JSONObject> apiReponse = sendJsonRestRequest(apiUrl + "/events.json", "GET", apiRequestHeadersMap);
        JSONArray esbJsonArray = new JSONArray(esbReponse.getBody().getString("output"));
        JSONArray apiJsonArray = new JSONArray(apiReponse.getBody().getString("output"));
        Assert.assertEquals(esbReponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbJsonArray.length(), apiJsonArray.length());
        Assert.assertEquals(esbJsonArray.getJSONObject(0).get("id").toString(), apiJsonArray.getJSONObject(0).get("id")
                .toString());
        Assert.assertEquals(esbJsonArray.getJSONObject(0).get("created_at").toString(), apiJsonArray.getJSONObject(0).get("created_at")
                .toString());
        
    }
    
    /**
     * Positive test case for listGlobalEvents method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testListGlobalEventsWithMandatoryParameters" }, description = "Test listGlobalEvents{BaseCamp} with Mandatory Parameters")
    public void testListGlobalEventsWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listGlobalEvents");
        RestResponse<JSONObject> esbReponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listGlobalEvents_optional.json");
        RestResponse<JSONObject> apiReponse = sendJsonRestRequest(apiUrl + "/events.json", "GET", apiRequestHeadersMap);
        JSONArray esbJsonArray = new JSONArray(esbReponse.getBody().getString("output"));
        JSONArray apiJsonArray = new JSONArray(apiReponse.getBody().getString("output"));
        Assert.assertEquals(esbReponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbJsonArray.length(), apiJsonArray.length());
        Assert.assertEquals(esbJsonArray.getJSONObject(0).get("id").toString(), apiJsonArray.getJSONObject(0).get("id")
                .toString());
        Assert.assertEquals(esbJsonArray.getJSONObject(0).get("created_at").toString(), apiJsonArray.getJSONObject(0).get("created_at")
                .toString());
    }
    
    /**
     * Negative test case for listGlobalEvents method.
     */
    @Test(priority = 1, dependsOnMethods = { "testListGlobalEventsWithOptionalParameters" }, description = "Test listGlobalEvents{BaseCamp} with Negative case")
    public void testListGlobalEventsWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listGlobalEvents");
        RestResponse<JSONObject> esbReponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listGlobalEvents_negative.json");
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/-/api/v1/events.json";
        
        RestResponse<JSONObject> apiReponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbReponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiReponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Positive test case for createComment method with mandatory parameters.
     * 
     * @throws Exception
     */
    @Test(priority = 1, dependsOnMethods = { "testListGlobalEventsWithNegativeCase" }, description = "Test createComment{BaseCamp} with Mandatory Parameters")
    public void testCreateCommentWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createComment");
        RestResponse<JSONObject> esbReponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_mandatory.json");
        RestResponse<JSONObject> apiReponse =
                sendJsonRestRequest(apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/messages/"
                        + connectorProperties.getProperty("messageId") + ".json", "GET", apiRequestHeadersMap);
        
        int noOfComments = apiReponse.getBody().getJSONArray("comments").length();
        
        Assert.assertEquals(esbReponse.getHttpStatusCode(), 201);
        Assert.assertEquals(
                apiReponse.getBody().getJSONArray("comments").getJSONObject(noOfComments - 1).getString("content"),
                esbReponse.getBody().getString("content"));
        Assert.assertEquals(
                apiReponse.getBody().getJSONArray("comments").getJSONObject(noOfComments - 1).getString("id"),
                esbReponse.getBody().getString("id"));
        Assert.assertEquals(apiReponse.getBody().getJSONArray("comments").getJSONObject(noOfComments - 1)
                .getJSONObject("creator").getString("id"), esbReponse.getBody().getJSONObject("creator")
                .getString("id"));
        Assert.assertEquals(apiReponse.getBody().getJSONArray("comments").getJSONObject(noOfComments - 1)
                .getJSONObject("creator").getString("avatar_url"), esbReponse.getBody().getJSONObject("creator")
                .getString("avatar_url"));
        
    }
    
    /**
     * Positive test case for createComment method with optional parameters.
     * 
     * @throws Exception
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateCommentWithMandatoryParameters" }, description = "Test createComment{BaseCamp} with optional parameters")
    public void testCreateCommentWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createComment");
        RestResponse<JSONObject> esbReponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_optional.json");
        String commentId = esbReponse.getBody().getString("id");
        String apiEndpoint =
                apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/messages/"
                        + connectorProperties.getProperty("messageId") + ".json";
        RestResponse<JSONObject> apiReponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbReponse.getHttpStatusCode(), 201);
        Assert.assertTrue(apiReponse.getBody().getJSONArray("comments").toString().contains(commentId));
    }
    
    /**
     * Negative test case for createComment method with invalid parameters.
     * 
     * @throws Exception
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateCommentWithOptionalParameters" }, description = "Test createComment{BaseCamp} with negative case")
    public void testCreateCommentWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createComment");
        RestResponse<JSONObject> esbReponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_negative.json");
        
        RestResponse<JSONObject> apiReponse =
                sendJsonRestRequest(apiUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/messages/"
                        + connectorProperties.getProperty("messageId") + "/comments.json", "POST",
                        apiRequestHeadersMap, "api_createComment_negative.json");
        
        Assert.assertEquals(esbReponse.getHttpStatusCode(), apiReponse.getHttpStatusCode());
        Assert.assertEquals(esbReponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiReponse.getHttpStatusCode(), 400);
    }
    
}
