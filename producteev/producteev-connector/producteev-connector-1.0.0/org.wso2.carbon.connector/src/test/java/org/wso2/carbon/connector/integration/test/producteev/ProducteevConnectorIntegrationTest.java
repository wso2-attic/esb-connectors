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

package org.wso2.carbon.connector.integration.test.producteev;

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

public class ProducteevConnectorIntegrationTest extends ConnectorIntegrationTestBase {
   
   private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
   
   private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
   
   private String apiUrl;
   
   /**
    * Set up the environment.
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {
   
      init("producteev-connector-1.0.0");
      
      apiUrl = connectorProperties.getProperty("apiUrl") + "/api";
      
      esbRequestHeadersMap.put("Content-Type", "application/json");
      apiRequestHeadersMap.putAll(esbRequestHeadersMap);
      
   }
   
   /**
    * Positive test case for getAccessTokenFromRefreshToken method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {getAccessTokenFromRefreshToken} integration test with mandatory parameters.")
   public void testGetAccessTokenFromRefreshToken() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getAccessTokenFromRefreshToken");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                  "esb_getAccessTokenFromRefreshToken_mandatory.json");
      
      final String accessToken = esbRestResponse.getBody().getString("access_token");
      connectorProperties.put("accessToken", accessToken);
      apiRequestHeadersMap.put("Authorization", "Bearer " + accessToken);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertNotNull(esbRestResponse.getBody().getString("token_type"));
      Assert.assertNotNull(esbRestResponse.getBody().getString("access_token"));
      
      final String taskIdWithFileNote = connectorProperties.getProperty("taskIdWithFileNote");
      final String fileId = getFileId(taskIdWithFileNote);
      if ("".equals(fileId)) {
         Assert.fail("Prerequisite failed. Please set at least one file attached note for the task.");
      }
      connectorProperties.setProperty("fileId", fileId);
   }
   
   /**
    * Method name: getAccessTokenFromRefreshToken 
    * Test scenario: Optional 
    * Reason to skip: There are no optional parameters to be tested.
    */
   
   /**
    * Method name: getAccessTokenFromRefreshToken 
    * Test scenario: Negative 
    * Reason to skip: There are no any negative parameters in this method.
    */
   
   /**
    * Positive test case for createTask method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {createTask} integration test with mandatory parameters.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
   public void testCreateTaskWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createTask");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_mandatory.json");
      String taskIdMandatory = esbRestResponse.getBody().getJSONObject("task").getString("id");
      connectorProperties.setProperty("taskIdMandatory", taskIdMandatory);
      
      String apiEndpoint = apiUrl + "/tasks/" + taskIdMandatory;
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(connectorProperties.getProperty("taskTitleMandatory"), apiRestResponse.getBody()
            .getJSONObject("task").getString("title"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").getString("status"), apiRestResponse
            .getBody().getJSONObject("task").getString("status"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").getString("priority"), apiRestResponse
            .getBody().getJSONObject("task").getString("priority"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").getJSONObject("creator").getString("id"),
            apiRestResponse.getBody().getJSONObject("task").getJSONObject("creator").getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").getJSONObject("project").getString("id"),
            apiRestResponse.getBody().getJSONObject("task").getJSONObject("project").getString("id"));
   }
   
   /**
    * Positive test case for createTask method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {createTask} integration test with optional parameters.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
   public void testCreateTaskWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createTask");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_optional.json");
      String taskIdOptional = esbRestResponse.getBody().getJSONObject("task").getString("id");
      
      connectorProperties.setProperty("taskIdOptional", taskIdOptional);
      
      String apiEndpoint = apiUrl + "/tasks/" + taskIdOptional;
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(connectorProperties.getProperty("taskTitleOptional"), apiRestResponse.getBody()
            .getJSONObject("task").getString("title"));
      Assert.assertEquals(connectorProperties.getProperty("endDate").substring(0, 10), apiRestResponse.getBody()
            .getJSONObject("task").getString("deadline").substring(0, 10));
      Assert.assertEquals(connectorProperties.getProperty("endDateTimeZone"),
            apiRestResponse.getBody().getJSONObject("task").getString("deadline_timezone"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").getString("status"), apiRestResponse
            .getBody().getJSONObject("task").getString("status"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").getJSONObject("creator").getString("id"),
            apiRestResponse.getBody().getJSONObject("task").getJSONObject("creator").getString("id"));
   }
   
   /**
    * Negative test case for createTask method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {createTask} integration test with negative case.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
   public void testCreateTaskWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createTask");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_negative.json");
      
      String apiEndpoint = apiUrl + "/tasks";
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createTask_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
   }
   
   /**
    * Positive test case for createNote method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {createNote} integration test with mandatory parameters.", dependsOnMethods = {
         "testCreateTaskWithOptionalParameters", "testGetAccessTokenFromRefreshToken" })
   public void testCreateNoteWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createNote");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNote_mandatory.json");
      final String noteId = esbRestResponse.getBody().getJSONObject("note").getString("id");
      connectorProperties.setProperty("noteIdMandatory", noteId);
      
      final String apiEndpoint = apiUrl + "/notes/" + noteId;
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(connectorProperties.getProperty("noteMessageMand"),
            apiRestResponse.getBody().getJSONObject("note").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("note").getString("created_at"), apiRestResponse
            .getBody().getJSONObject("note").getString("created_at"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("note").getString("updated_at"), apiRestResponse
            .getBody().getJSONObject("note").getString("updated_at"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("note").getJSONObject("creator").getString("id"),
            apiRestResponse.getBody().getJSONObject("note").getJSONObject("creator").getString("id"));
   }
   
   /**
    * Positive test case for createNote method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {createNote} integration test with optional parameters.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
   public void testCreateNoteWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createNote");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNote_optional.json");
      final String noteId = esbRestResponse.getBody().getJSONObject("note").getString("id");
      
      final String apiEndpoint = apiUrl + "/notes/" + noteId;
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      final String apiFileId =
            apiRestResponse.getBody().getJSONObject("note").getJSONArray("files").getJSONObject(0).getString("id");
      
      Assert.assertEquals(connectorProperties.getProperty("fileId"), apiFileId);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("note").getString("message"), apiRestResponse
            .getBody().getJSONObject("note").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("note").getString("created_at"), apiRestResponse
            .getBody().getJSONObject("note").getString("created_at"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("note").getString("updated_at"), apiRestResponse
            .getBody().getJSONObject("note").getString("updated_at"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("note").getJSONObject("creator").getString("id"),
            apiRestResponse.getBody().getJSONObject("note").getJSONObject("creator").getString("id"));
   }
   
   /**
    * Negative test case for createNote method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {createNote} integration test with negative case.", dependsOnMethods = {
         "testCreateTaskWithOptionalParameters", "testGetAccessTokenFromRefreshToken" })
   public void testCreateNoteWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createNote");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNote_negative.json");
      
      String apiEndpoint = apiUrl + "/notes";
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createNote_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
   }
   
   /**
    * Positive test case for getNote method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {getNote} integration test with mandatory parameters.", dependsOnMethods = {
         "testCreateNoteWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
   public void testGetNoteWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getNote");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getNote_mandatory.json");
      
      final String apiEndpoint = apiUrl + "/notes/" + connectorProperties.getProperty("noteIdMandatory");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("note").getString("created_at"), apiRestResponse
            .getBody().getJSONObject("note").getString("created_at"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("note").getString("message"), apiRestResponse
            .getBody().getJSONObject("note").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("note").getString("updated_at"), apiRestResponse
            .getBody().getJSONObject("note").getString("updated_at"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("note").getJSONObject("creator").getString("id"),
            apiRestResponse.getBody().getJSONObject("note").getJSONObject("creator").getString("id"));
   }
   
   /**
    * Test case: testGetNoteWithOptionalParameters. Status: Skipped. Reason : There are no any optional parameters for
    * the function invocation.
    */
   
   /**
    * Negative test case for getNote method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {getNote} integration test with negative case.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
   public void testGetNoteWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getNote");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getNote_negative.json");
      
      String apiEndpoint = apiUrl + "/notes/INVALID";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
   }
   
   /**
    * Positive test case for listTaskNotes method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {listTaskNotes} integration test with mandatory parameters.", dependsOnMethods = {
         "testCreateNoteWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
   public void testListTaskNotesWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listTaskNotes");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTaskNotes_mandatory.json");
      JSONArray esbNotesArray = esbRestResponse.getBody().getJSONArray("notes");
      
      final String apiEndpoint = apiUrl + "/tasks/" + connectorProperties.getProperty("taskIdOptional") + "/notes";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      JSONArray apiNotesArray = apiRestResponse.getBody().getJSONArray("notes");
      
      Assert.assertEquals(esbNotesArray.getJSONObject(0).getString("id"), apiNotesArray.getJSONObject(0)
            .getString("id"));
      Assert.assertEquals(esbNotesArray.getJSONObject(0).getString("created_at"), apiNotesArray.getJSONObject(0)
            .getString("created_at"));
      Assert.assertEquals(esbNotesArray.getJSONObject(0).getString("message"), apiNotesArray.getJSONObject(0)
            .getString("message"));
      Assert.assertEquals(esbNotesArray.getJSONObject(0).getString("updated_at"), apiNotesArray.getJSONObject(0)
            .getString("updated_at"));
      Assert.assertEquals(esbNotesArray.getJSONObject(0).getJSONObject("creator").getString("id"), apiNotesArray
            .getJSONObject(0).getJSONObject("creator").getString("id"));
   }
   
   /**
    * Test case: testListTaskNotesWithOptionalParameters. Status: Skipped. Reason : There are no any optional parameters
    * for the function invocation.
    */
   
   /**
    * Negative test case for listTaskNotes method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {listTaskNotes} integration test with negative case.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
   public void testListTaskNotesWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listTaskNotes");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTaskNotes_negative.json");
      
      String apiEndpoint = apiUrl + "/tasks/INVALID/notes";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
   }
   
   /**
    * Positive test case for assignTask method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {assignTask} integration test with mandatory parameters.", dependsOnMethods = {
         "testCreateTaskWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
   public void testAssignTaskWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:assignTask");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_assignTask_mandatory.json");
      
      String apiEndpoint = apiUrl + "/tasks/" + connectorProperties.getProperty("taskIdMandatory");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("users").length(), apiRestResponse.getBody()
            .getJSONObject("task").getJSONArray("responsibles").length());
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONArray("users").getJSONObject(1).getString("id"),
            apiRestResponse.getBody().getJSONObject("task").getJSONArray("responsibles").getJSONObject(1)
                  .getString("id"));
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONArray("users").getJSONObject(1).getString("firstname"),
            apiRestResponse.getBody().getJSONObject("task").getJSONArray("responsibles").getJSONObject(1)
                  .getString("firstname"));
   }
   
   /**
    * Test case: testAssignTaskWithOptionalParameters. Status: Skipped. Reason : There are no any optional parameters
    * for the function invocation.
    */
   
   /**
    * Negative test case for assignTask method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {assignTask} integration test with negative case.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
   public void testAssignTaskWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:assignTask");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_assignTask_negative.json");
      
      String apiEndpoint = apiUrl + "/tasks/INVALID/responsibles/" + connectorProperties.getProperty("userId");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
   }
   
   /**
    * Positive test case for removeAssignee method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {removeAssignee} integration test with mandatory parameters.", dependsOnMethods = {
         "testAssignTaskWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
   public void testRemoveAssigneeWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:removeAssignee");
      
      // Checking how many responsible users are there assigned to the task.
      String apiEndpoint = apiUrl + "/tasks/" + connectorProperties.getProperty("taskIdMandatory");
      RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      int assignedUsersCountBefore =
            apiRestResponseBefore.getBody().getJSONObject("task").getJSONArray("responsibles").length();
      
      // Removing one of the assignees from the task.
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_removeAssignee_mandatory.json");
      
      // Checking the number of responsible users for the task.
      RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      int assignedUsersCountAfter =
            apiRestResponseAfter.getBody().getJSONObject("task").getJSONArray("responsibles").length();
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
      Assert.assertNotEquals(assignedUsersCountBefore, assignedUsersCountAfter);
   }
   
   /**
    * Test case: testRemoveAssigneeWithOptionalParameters. Status: Skipped. Reason : There are no any optional
    * parameters for the function invocation.
    */
   
   /**
    * Negative test case for removeAssignee method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {removeAssignee} integration test with negative case.", dependsOnMethods = {
         "testCreateTaskWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
   public void testRemoveAssigneeWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:removeAssignee");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_removeAssignee_negative.json");
      
      String apiEndpoint =
            apiUrl + "/tasks/" + connectorProperties.getProperty("taskIdMandatory") + "/responsibles/INVALID";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "DELETE", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
   }
   
   /**
    * Positive test case for getUser method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {getUser} integration test with mandatory parameters.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
   public void testGetUserWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getUser");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUser_mandatory.json");
      
      String apiEndpoint = apiUrl + "/users/" + connectorProperties.getProperty("userId");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").getString("id"), apiRestResponse.getBody()
            .getJSONObject("user").getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").getString("firstname"), apiRestResponse
            .getBody().getJSONObject("user").getString("firstname"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").getString("email"), apiRestResponse.getBody()
            .getJSONObject("user").getString("email"));
   }
   
   /**
    * Test case: testGetUserWithOptionalParameters. Status: Skipped. Reason : There are no any optional parameters for
    * the function invocation.
    */
   
   /**
    * Negative test case for getUser method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {getUser} integration test with negative case.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
   public void testGetUserWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getUser");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUser_negative.json");
      
      String apiEndpoint = apiUrl + "/users/INVALID";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
   }
   
   /**
    * Positive test case for inviteUserToNetwork method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {inviteUserToNetwork} integration test with mandatory parameters.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
   public void testInviteUserToNetworkWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:inviteUserToNetwork");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_inviteUserToNetwork_mandatory.json");
      
      String apiEndpoint = apiUrl + "/network_invitations";
      
      // Attempting to invite the same user to the network which would cause a conflict to check the user added to
      // network or not.
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_inviteUserToNetwork_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("network_invitation").getString("email"),
            connectorProperties.getProperty("invitationEmail"));
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 409);
      Assert.assertEquals(apiRestResponse.getBody().getString("error"), "Conflict");
      Assert.assertEquals(apiRestResponse.getBody().getString("error_code"), "409");
   }
   
   /**
    * Test case: testInviteUserToNetworkWithOptionalParamaters Status: Skipped. Reason : There are no any optional
    * parameters for the function invocation.
    */
   
   /**
    * Negative test case for inviteUserToNetwork method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {inviteUserToNetwork} integration test with negative case.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
   public void testInviteUserToNetworkWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:inviteUserToNetwork");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_inviteUserToNetwork_negative.json");
      
      String apiEndpoint = apiUrl + "/network_invitations";
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_inviteUserToNetwork_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
   }
   
   /**
    * Positive test case for getTask method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTaskWithOptionalParameters",
         "testGetAccessTokenFromRefreshToken" }, description = "Producteev {getTask} integration test with mandatory parameters.")
   public void testGetTaskWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getTask");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTask_mandatory.json");
      
      final String apiEndpoint = apiUrl + "/tasks/" + connectorProperties.getProperty("taskIdOptional");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").getString("title"), apiRestResponse.getBody()
            .getJSONObject("task").getString("title"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").getString("status"), apiRestResponse
            .getBody().getJSONObject("task").getString("status"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").getString("priority"), apiRestResponse
            .getBody().getJSONObject("task").getString("priority"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").getJSONObject("creator").getString("id"),
            apiRestResponse.getBody().getJSONObject("task").getJSONObject("creator").getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").getJSONObject("project").getString("id"),
            apiRestResponse.getBody().getJSONObject("task").getJSONObject("project").getString("id"));
   }
   
   /**
    *  Test case: testGetTaskWithOptionalParameters.        Status: Skipped.        Reason : There are no any optional
    * parameters for the function invocation.      
    */
   
   /**
    * Negative test case for getTask method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {getTask} integration test with negative case.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
   public void testGetTaskWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getTask");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTask_negative.json");
      
      final String apiEndpoint = apiUrl + "/tasks/INVALID.json";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
   }
   
   /**
    * Positive test case for listTasks method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTaskWithOptionalParameters",
         "testCreateTaskWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" }, description = "Producteev {listTasks} integration test with mandatory parameters.")
   public void testListTasksWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listTasks");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_optional.json");
      
      String apiEndpoint = apiUrl + "/tasks/search?order=asc&sort=created_at";
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_listTasks_mandatory.json");
      
      JSONObject esbFirstTaskObject = esbRestResponse.getBody().getJSONArray("tasks").getJSONObject(0);
      JSONObject esbLastTaskObject =
            esbRestResponse.getBody().getJSONArray("tasks")
                  .getJSONObject(esbRestResponse.getBody().getJSONArray("tasks").length() - 1);
      
      JSONObject apiFirstTaskObject = apiRestResponse.getBody().getJSONArray("tasks").getJSONObject(0);
      JSONObject apiLastTaskObject =
            apiRestResponse.getBody().getJSONArray("tasks")
                  .getJSONObject(apiRestResponse.getBody().getJSONArray("tasks").length() - 1);
      
      Assert.assertEquals(esbFirstTaskObject.getString("id"), apiFirstTaskObject.getString("id"));
      Assert.assertEquals(esbLastTaskObject.getString("id"), apiLastTaskObject.getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tasks").length(), apiRestResponse.getBody()
            .getJSONArray("tasks").length());
      Assert.assertEquals(esbRestResponse.getBody().getString("total_hits"),
            apiRestResponse.getBody().getString("total_hits"));
      
   }
   
   /**
    * Positive test case for listTasks method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(dependsOnMethods = { "testCreateTaskWithOptionalParameters", "testCreateTaskWithMandatoryParameters",
         "testGetAccessTokenFromRefreshToken" }, description = "Producteev {listTasks} integration test with optional parameters.")
   public void testListTasksWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listTasks");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_optional.json");
      
      String apiEndpoint = apiUrl + "/tasks/search?order=asc&sort=created_at&alias=active&page=1";
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_listTasks_optional.json");
      
      JSONObject esbFirstTaskObject = esbRestResponse.getBody().getJSONArray("tasks").getJSONObject(0);
      JSONObject esbLastTaskObject =
            esbRestResponse.getBody().getJSONArray("tasks")
                  .getJSONObject(esbRestResponse.getBody().getJSONArray("tasks").length() - 1);
      
      JSONObject apiFirstTaskObject = apiRestResponse.getBody().getJSONArray("tasks").getJSONObject(0);
      JSONObject apiLastTaskObject =
            apiRestResponse.getBody().getJSONArray("tasks")
                  .getJSONObject(apiRestResponse.getBody().getJSONArray("tasks").length() - 1);
      
      Assert.assertEquals(esbFirstTaskObject.getString("id"), apiFirstTaskObject.getString("id"));
      Assert.assertEquals(esbLastTaskObject.getString("id"), apiLastTaskObject.getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tasks").length(), apiRestResponse.getBody()
            .getJSONArray("tasks").length());
      Assert.assertEquals(esbRestResponse.getBody().getString("total_hits"),
            apiRestResponse.getBody().getString("total_hits"));
   }
   
   /**
    * Negative test case for listTasks method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(description = "Producteev {listTasks} integration test with negative case.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
   public void testListTasksWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listTasks");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_negative.json");
      
      String apiEndpoint = apiUrl + "/tasks/search?order=asc&sort=INVALID";
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_listTasks_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
   }
   
   /**
    * Test case: testUpdateTaskWithMandatoryParameters.        Status: Skipped.        Reason : There are no any
    * mandatory parameter(s) for the function invocation.      
    */
   
   /**
    * Positive test case for updateTask method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(dependsOnMethods = { "testCreateTaskWithOptionalParameters", "testGetAccessTokenFromRefreshToken" }, description = "Producteev {updateTask} integration test with optional parameters.")
   public void testUpdateTaskWithOptionalParameters() throws IOException, JSONException {
   
      connectorProperties.setProperty("taskStatus", "0");
      connectorProperties.setProperty("taskPriority", "1");
      
      String apiEndpoint = apiUrl + "/tasks/" + connectorProperties.getProperty("taskIdOptional");
      
      RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      esbRequestHeadersMap.put("Action", "urn:updateTask");
      
      sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTask_optional.json");
      
      RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(connectorProperties.getProperty("updateTaskTitle"), apiRestResponseAfter.getBody()
            .getJSONObject("task").getString("title"));
      Assert.assertEquals(connectorProperties.getProperty("updateTaskEndDate").substring(0, 10), apiRestResponseAfter
            .getBody().getJSONObject("task").getString("deadline").substring(0, 10));
      Assert.assertEquals(connectorProperties.getProperty("updateTaskEndDateTimeZone"), apiRestResponseAfter.getBody()
            .getJSONObject("task").getString("deadline_timezone"));
      Assert.assertEquals(connectorProperties.getProperty("taskStatus"),
            apiRestResponseAfter.getBody().getJSONObject("task").getString("status"));
      Assert.assertEquals(connectorProperties.getProperty("taskPriority"), apiRestResponseAfter.getBody()
            .getJSONObject("task").getString("priority"));
      
      Assert.assertNotEquals(apiRestResponseBefore.getBody().getJSONObject("task").getString("title"),
            apiRestResponseAfter.getBody().getJSONObject("task").getString("title"));
      Assert.assertNotEquals(
            apiRestResponseBefore.getBody().getJSONObject("task").getString("deadline").substring(0, 10),
            apiRestResponseAfter.getBody().getJSONObject("task").getString("deadline").substring(0, 10));
      Assert.assertNotEquals(apiRestResponseBefore.getBody().getJSONObject("task").getString("deadline_timezone"),
            apiRestResponseAfter.getBody().getJSONObject("task").getString("deadline_timezone"));
      Assert.assertNotEquals(apiRestResponseBefore.getBody().getJSONObject("task").getString("status"),
            apiRestResponseAfter.getBody().getJSONObject("task").getString("status"));
      Assert.assertNotEquals(apiRestResponseBefore.getBody().getJSONObject("task").getString("priority"),
            apiRestResponseAfter.getBody().getJSONObject("task").getString("priority"));
      
   }
   
   /**
    * Negative test case for updateTask method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(description = "Producteev {updateTask} integration test with negative case.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
   public void testUpdateTaskWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateTask");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTask_negative.json");
      
      String apiEndpoint = apiUrl + "/tasks/INVALID";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_updateTask_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
   }
   
   /**
    * Positive test case for addLabelToTask method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {addLabelToTask} integration test with mandatory parameters.", dependsOnMethods = {
         "testCreateTaskWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
   public void testAddLabelToTaskWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:addLabelToTask");
      // Adding label to the task.
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "esb_addLabelToTask_mandatory.json");
      
      // Retrieving and storing the details of the label from the response.
      JSONObject esbLabelObject = esbRestResponse.getBody().getJSONArray("labels").getJSONObject(0);
      
      // Retrieving and storing the label details of the task via a direct call to API.
      String apiEndpoint = apiUrl + "/tasks/" + connectorProperties.getProperty("taskIdMandatory");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      JSONArray apiLabelObjects = apiRestResponse.getBody().getJSONObject("task").getJSONArray("labels");
      
      for (int i = 0; i < apiLabelObjects.length(); i++) {
         JSONObject apiLabelObject = apiLabelObjects.getJSONObject(i);
         // Comparing the id of the label added, with the label Ids of the task.
         if ((esbLabelObject.getString("id").equals(apiLabelObject.getString("id")))) {
            Assert.assertEquals(connectorProperties.getProperty("labelId"), apiLabelObject.get("id"));
            Assert.assertEquals(esbLabelObject.getString("title"), apiLabelObject.getString("title"));
            Assert.assertEquals(esbLabelObject.getString("created_at"), apiLabelObject.getString("created_at"));
            break;
            
         } else {
            Assert.fail("Label is not added to the task");
         }
      }
      
   }
   
   /**
    * Test case: testAddLabelToTaskWithOptionalParameters. Status: Skipped. Reason : There are no any optional
    * parameters for the function invocation.
    */
   
   /**
    * Negative test case for addLabelToTask method. Provides an invalid taskId.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {addLabelToTask} integration test with negative case.", dependsOnMethods = {
         "testCreateTaskWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
   public void testAddLabelToTaskWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:addLabelToTask");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "esb_addLabelToTask_negative.json");
      
      String apiEndpoint = apiUrl + "/tasks/INVALID/labels/" + connectorProperties.getProperty("labelId");
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_addLabelToTask_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
   }
   
   /**
    * Positive test case for addFollowerToTask method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {addFollowerToTask} integration test with mandatory parameters.", dependsOnMethods = {
         "testCreateTaskWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
   public void testAddFollowerToTaskWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:addFollowerToTask");
      // Adding follower to the task.
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "esb_addFollowerToTask_mandatory.json");
      
      // Retrieving and storing the follower details from the response.
      JSONArray esbLabelObjects = esbRestResponse.getBody().getJSONArray("users");
      String esbLabelObjectId = null;
      JSONObject esbLabelObject = null;
      
      // Retrieving and storing the follower details of the task via a direct call to API.
      String apiEndpoint = apiUrl + "/tasks/" + connectorProperties.getProperty("taskIdMandatory");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      JSONArray apiLabelObjects = apiRestResponse.getBody().getJSONObject("task").getJSONArray("followers");
      
      for (int i = 0; i < esbLabelObjects.length(); i++) {
         esbLabelObject = esbLabelObjects.getJSONObject(i);
         // Comparing the userId of the follower added, with the follower Ids of the task.
         if ((esbLabelObject.getString("id").equals(connectorProperties.getProperty("userId")))) {
            esbLabelObjectId = esbLabelObject.getString("id");
            for (int j = 0; j < apiLabelObjects.length(); j++) {
               JSONObject apiLabelObject = apiLabelObjects.getJSONObject(j);
               if ((esbLabelObjectId.equals(apiLabelObject.getString("id")))) {
                  Assert.assertEquals(esbLabelObject.getString("email"), apiLabelObject.getString("email"));
                  Assert.assertEquals(esbLabelObject.getString("firstname"), apiLabelObject.getString("firstname"));
                  Assert.assertEquals(esbLabelObject.getString("lastname"), apiLabelObject.getString("lastname"));
                  break;
               }
            }
            
         }
      }
      
   }
   
   /**
    * Test case: testAddFollowerToTaskWithOptionalParameters. Status: Skipped. Reason : There are no any optional
    * parameters for the function invocation.
    */
   
   /**
    * Negative test case for addFollowerToTask method. Provides an invalid taskId.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {addFollowerToTask} integration test with negative case.", dependsOnMethods = {
         "testCreateTaskWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
   public void testAddFollowerToTaskWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:addFollowerToTask");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "esb_addFollowerToTask_negative.json");
      
      String apiEndpoint = apiUrl + "/tasks/INVALID/followers/" + connectorProperties.getProperty("userId");
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_addFollowerToTask_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
   }
   
   /**
    * Test case: testUpdateLabelWithMandatoryParameters. Status: Skipped. Reason : There are no any mandatory parameters
    * for the function invocation.
    */
   
   /**
    * Positive test case for updateLabel method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {updateLabel} method with optional parameters.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
   public void testUpdateLabelWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateLabel");
      
      // Retrieving the content of label before updating.
      String apiEndpoint = apiUrl + "/labels/" + connectorProperties.getProperty("labelId");
      RestResponse<JSONObject> apiResponseBefore = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiResponseBefore.getHttpStatusCode(), 200);
      
      JSONObject labelBefore = apiResponseBefore.getBody().getJSONObject("label");
      
      // Updating the label.
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "esb_updateLabel_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      // Retrieving the content of label after updating.
      RestResponse<JSONObject> apiResponseAfter = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiResponseAfter.getHttpStatusCode(), 200);
      JSONObject labelAfter = apiResponseAfter.getBody().getJSONObject("label");
      
      // Replacing the spaces of label title to be compatible with the updated title value in API.
      String labelString = connectorProperties.getProperty("labelTitle").replace(" ", "-");
      
      // Comparing the content of the label before and after updating.
      Assert.assertEquals(labelString, labelAfter.getString("title"));
      Assert.assertEquals(connectorProperties.getProperty("foregroundColor"), labelAfter.getString("foreground_color"));
      Assert.assertEquals(connectorProperties.getProperty("backgroundColor"), labelAfter.getString("background_color"));
      
      Assert.assertNotEquals(labelBefore.getString("title"), labelAfter.getString("title"));
      Assert.assertNotEquals(labelBefore.getString("foreground_color"), labelAfter.getString("foreground_color"));
      Assert.assertNotEquals(labelBefore.getString("background_color"), labelAfter.getString("background_color"));
   }
   
   /**
    * Negative test case for updateLabel method. Provides an invalid labelId.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "Producteev {updateLabel} integration test with negative case.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
   public void testUpdateLabelWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateLabel");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "esb_updateLabel_negative.json");
      
      String apiEndpoint = apiUrl + "/labels/INVALID";
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_updateLabel_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
   }
   
   /**
    * Retrieving one attached file ID from notes.
    * 
    * @throws JSONException
    * @throws IOException
    */
   private final String getFileId(final String taskId) throws IOException, JSONException {
   
      final String apiEndpoint = apiUrl + "/tasks/" + taskId + "/notes";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      JSONArray notesArray = apiRestResponse.getBody().getJSONArray("notes");
      String fileId = "";
      for (int i = 0; i < notesArray.length(); i++) {
         JSONObject noteObject = notesArray.getJSONObject(i);
         JSONArray filesArray = noteObject.getJSONArray("files");
         if (filesArray.length() != 0) {
            fileId = filesArray.getJSONObject(0).getString("id");
            break;
         }
      }
      return fileId;
   }
}
