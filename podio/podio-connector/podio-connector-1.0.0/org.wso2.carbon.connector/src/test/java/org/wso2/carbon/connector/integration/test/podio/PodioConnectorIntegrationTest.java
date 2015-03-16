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

package org.wso2.carbon.connector.integration.test.podio;

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

public class PodioConnectorIntegrationTest extends ConnectorIntegrationTestBase {
   
   private Map<String, String> esbRequestHeadersMap;
   
   private Map<String, String> apiRequestHeadersMap;
   
   private String apiUrl;
   
   private long timeOut;
   
   /**
    * Set up the environment.
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {
   
      init("podio-connector-1.0.0");

      esbRequestHeadersMap = new HashMap<String, String>();
      apiRequestHeadersMap = new HashMap<String, String>();
      
      apiUrl = connectorProperties.getProperty("apiUrl");
      timeOut = Long.parseLong(connectorProperties.getProperty("timeOut"));
      
      esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
      
      apiRequestHeadersMap.putAll(esbRequestHeadersMap);
      apiRequestHeadersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("accessToken"));
      apiRequestHeadersMap.put("Content-Type", "application/json");
      
   }
   
   /**
    * Positive test case for uploadFile method.
    */
   @Test(priority = 1, description = "podio {uploadFile} integration test positive case.")
   public void testUploadFile() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:uploadFile");
      esbRequestHeadersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("accessToken"));
      
      String multipartProxyUrl = getProxyServiceURL("podio_uploadFile");
      String requestString = multipartProxyUrl + "?apiUrl=" + apiUrl;
      MultipartFormdataProcessor multipartProcessor =
            new MultipartFormdataProcessor(requestString, esbRequestHeadersMap);
      
      File file = new File(pathToResourcesDirectory + connectorProperties.getProperty("uploadSourcePath"));
      multipartProcessor.addFileToRequest("source", file);
      multipartProcessor.addFormDataToRequest("filename", connectorProperties.getProperty("uploadSourcePath"));
      RestResponse<JSONObject> esbRestResponse = multipartProcessor.processForJsonResponse();
      
      String fileId = esbRestResponse.getBody().getString("file_id");
      connectorProperties.put("fileId", fileId);
      
      String apiEndPoint = apiUrl + "/file/" + fileId;
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("created_by").getString("name"), apiRestResponse
            .getBody().getJSONObject("created_by").getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getString("created_on"),
            apiRestResponse.getBody().getString("created_on"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("created_via").getString("name"), apiRestResponse
            .getBody().getJSONObject("created_via").getString("name"));
      esbRequestHeadersMap.remove("Authorization");
      esbRequestHeadersMap.put("Content-Type", "application/json");
   }
   
   /**
    * Positive test case for getFile method with mandatory parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testUploadFile" }, description = "podio {getFile} integration test with mandatory parameters.")
   public void testGetFileWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Content-Type", "application/json");
      esbRequestHeadersMap.put("Action", "urn:getFile");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFile_mandatory.json");
      
      String apiEndPoint = apiUrl + "/file/" + connectorProperties.getProperty("fileId");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("created_by").getString("name"), apiRestResponse
            .getBody().getJSONObject("created_by").getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getString("link"), apiRestResponse.getBody().getString("link"));
      Assert.assertEquals(esbRestResponse.getBody().getString("file_id"), connectorProperties.getProperty("fileId"));
   }
   
   /**
    * Negative test case for getFile method.
    */
   @Test(priority = 1, dependsOnMethods = { "testGetFileWithMandatoryParameters" }, description = "podio {getFile} integration test with negative case.")
   public void testGetFileWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getFile");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFile_negative.json");
      
      String apiEndPoint = apiUrl + "/file/Invalid";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
      
   }
   
   /**
    * Positive test case for createTask method with mandatory parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testGetFileWithNegativeCase" }, description = "podio {createTask} integration test with mandatory parameters.")
   public void testCreateTaskWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createTask");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_mandatory.json");
      String taskId = esbRestResponse.getBody().getString("task_id");
      connectorProperties.put("taskId", taskId);
      
      String apiEndPoint = apiUrl + "/task/" + taskId;
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("created_by").getString("name"), apiRestResponse
            .getBody().getJSONObject("created_by").getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getString("text"), apiRestResponse.getBody().getString("text"));
   }
   
   /**
    * Positive test case for createTask method with optional parameters.
    */
   @Test(dependsOnMethods = { "testCreateTaskWithMandatoryParameters" }, description = "podio {createTask} integration test with optonal parameters.")
   public void testCreateTaskWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createTask");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_optional.json");
      String taskId = esbRestResponse.getBody().getString("task_id");
      
      String apiEndPoint = apiUrl + "/task/" + taskId;
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("created_by").getString("name"), apiRestResponse
            .getBody().getJSONObject("created_by").getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getString("text"), apiRestResponse.getBody().getString("text"));
      Assert.assertEquals(esbRestResponse.getBody().getString("external_id"),
            apiRestResponse.getBody().getString("external_id"));
   }
   
   /**
    * Negative test case for createTask method.
    */
   @Test(dependsOnMethods = { "testCreateTaskWithOptionalParameters" }, description = "podio {createTask} integration test with negative case.")
   public void testCreateTaskWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createTask");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_negative.json");
      String apiEndPoint = apiUrl + "/task";
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createTask_negative.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
      
   }
   
   /**
    * Positive test case for getTask method with mandatory parameters.
    */
   @Test(dependsOnMethods = { "testCreateTaskWithNegativeCase" }, description = "podio {getTask} integration test with mandatory parameters.")
   public void testGetTaskWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getTask");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTask_mandatory.json");
      
      String taskId = connectorProperties.getProperty("taskId");
      String apiEndPoint = apiUrl + "/task/" + taskId;
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("created_by").getString("name"), apiRestResponse
            .getBody().getJSONObject("created_by").getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getString("text"), apiRestResponse.getBody().getString("text"));
   }
   
   /**
    * Negative test case for getTask method.
    */
   @Test(dependsOnMethods = { "testGetTaskWithMandatoryParameters" }, description = "podio {getTask} integration test with negative case.")
   public void testGetTaskWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getTask");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTask_negative.json");
      
      String apiEndPoint = apiUrl + "/task/Invalid";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
      
   }
   
   /**
    * Positive test case for attachFile method with mandatory parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testGetTaskWithNegativeCase" }, description = "podio {attachFile} integration test with mandatory parameters.")
   public void testAttachFileWithMandatoryParameters() throws IOException, JSONException {
   
      apiRequestHeadersMap.remove("Content-Type");
      esbRequestHeadersMap.put("Action", "urn:attachFile");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_attachFile_mandatory.json");
      
      String apiEndPoint = apiUrl + "/file/" + connectorProperties.getProperty("fileId") + "/attach";
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_attachFile_mandatory.json");
      
      // Since file is attached in esb call it gives not found error in api call
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
   }
   
   /**
    * Negative test case for attachFile method.
    */
   @Test(priority = 1, dependsOnMethods = { "testAttachFileWithMandatoryParameters" }, description = "podio {attachFile} integration test with negative case.")
   public void testAttachFileWithNegativeCase() throws IOException, JSONException, InterruptedException {
   
      esbRequestHeadersMap.put("Action", "urn:attachFile");
      
      Thread.sleep(timeOut);
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_attachFile_negative.json");
      
      String apiEndPoint = apiUrl + "/file/Invalid/attach";
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_attachFile_negative.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
      
   }
   
   /**
    * Positive test case for incompleteTask method with mandatory parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testAttachFileWithNegativeCase" }, description = "podio {incompleteTask} integration test with mandatory parameters.")
   public void testIncompleteTaskWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:incompleteTask");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_incompleteTask_mandatory.json");
      
      String apiEndPoint = apiUrl + "/task/" + connectorProperties.getProperty("taskId");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
      Assert.assertEquals(apiRestResponse.getBody().getString("status"), "active");
   }
   
   /**
    * Negative test case for incompleteTask method.
    */
   @Test(priority = 1, dependsOnMethods = { "testAttachFileWithMandatoryParameters" }, description = "podio {incompleteTask} integration test with negative case.")
   public void testIncompleteTaskWithNegativeCase() throws IOException, JSONException, InterruptedException {
   
      esbRequestHeadersMap.put("Action", "urn:incompleteTask");
      
      Thread.sleep(timeOut);
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_incompleteTask_negative.json");
      
      String apiEndPoint = apiUrl + "/task/Invalid/incomplete";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
      
   }
   
   /**
    * Positive test case for listTasks method with optional parameters.
    */
   @Test(dependsOnMethods = { "testIncompleteTaskWithNegativeCase" }, description = "podio {listTasks} integration test with optional parameters.")
   public void testListTaskskWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listTasks");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_optional.json");
      
      String esbResponseArrayString = esbRestResponse.getBody().getString("output");
      JSONArray esbResponseArray = new JSONArray(esbResponseArrayString);
      JSONObject esbObject = esbResponseArray.getJSONObject(0).getJSONObject("responsible");
      
      String apiEndPoint =
            apiUrl + "/task?grouping=created_by&created_by=user:"
                  + connectorProperties.getProperty("userId");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      String apiResponseArrayString = apiRestResponse.getBody().getString("output");
      JSONArray apiResponseArray = new JSONArray(apiResponseArrayString);
      JSONObject apiObject = apiResponseArray.getJSONObject(0).getJSONObject("responsible");
      
      Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
      Assert.assertEquals(esbObject.getString("user_id"), apiObject.getString("user_id"));
      Assert.assertEquals(esbObject.getString("profile_id"), apiObject.getString("profile_id"));
   }
   
   /**
    * Negative test case for listTasks method.
    */
   @Test(dependsOnMethods = { "testAttachFileWithMandatoryParameters" }, description = "podio {listTasks} integration test with negative case.")
   public void testListTasksWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listTasks");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_negative.json");
      
      String apiEndPoint = apiUrl + "/task";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
      
   }
   
   /**
    * Positive test case for createReminder method with mandatory parameters.
    */
   @Test(dependsOnMethods = { "testListTasksWithNegativeCase" }, description = "podio {createReminder} integration test with mandatory parameters.")
   public void testCreateReminderWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createReminder");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createReminder_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
      String taskId = connectorProperties.getProperty("taskId");
      String apiEndPoint = apiUrl + "/task/" + taskId;
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(connectorProperties.getProperty("remindDelta"),
            apiRestResponse.getBody().getJSONObject("reminder").getString("remind_delta"));
      Assert.assertEquals(connectorProperties.getProperty("taskId"), apiRestResponse.getBody().getString("task_id"));
   }
   
   /**
    * Negative test case for createReminder method.
    */
   @Test(dependsOnMethods = { "testCreateReminderWithMandatoryParameters" }, description = "podio {createReminder} integration test with negative case.")
   public void testCreateReminderWithNegativeCase() throws IOException, JSONException, InterruptedException {
   
      Thread.sleep(timeOut);
      esbRequestHeadersMap.put("Action", "urn:createReminder");
      
      String apiEndPoint = apiUrl + "/reminder/task/Invalid";
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_createReminder_negative.json");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createReminder_negative.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
   }
   
   /**
    * Positive test case for getReminder method with mandatory parameters.
    */
   @Test(dependsOnMethods = { "testCreateReminderWithNegativeCase" }, description = "podio {getReminder} integration test with mandatory parameters.")
   public void testGetReminderWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getReminder");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getReminder_mandatory.json");
      
      String apiEndPoint = apiUrl + "/reminder/task/" + connectorProperties.getProperty("taskId");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getString("remind_delta"),
            apiRestResponse.getBody().getString("remind_delta"));
   }
   
   /**
    * Negative test case for getReminder method.
    */
   @Test(dependsOnMethods = { "testGetReminderWithMandatoryParameters" }, description = "podio {getReminder} integration test with negative case.")
   public void testGetReminderWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getReminder");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getReminder_negative.json");
      
      String apiEndPoint = apiUrl + "/reminder/task/Invalid";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
      
   }
   
   /**
    * Positive test case for assignTask method with mandatory parameters.
    */
   @Test(dependsOnMethods = { "testGetReminderWithNegativeCase" }, description = "podio {assignTask} integration test with mandatory parameters.")
   public void testAssignTaskWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:assignTask");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_assignTask_mandatory.json");
      
      String apiEndPoint = apiUrl + "/task/" + connectorProperties.getProperty("taskId");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("responsible").getString("user_id"),
            connectorProperties.getProperty("userId"));
   }
   
   /**
    * Negative test case for assignTask method.
    */
   @Test(dependsOnMethods = { "testAssignTaskWithMandatoryParameters" }, description = "podio {assignTask} integration test with negative case.")
   public void testAssignTaskWithNegativeCase() throws IOException, JSONException, InterruptedException {
   
      Thread.sleep(timeOut);
      esbRequestHeadersMap.put("Action", "urn:assignTask");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_assignTask_negative.json");
      
      String apiEndPoint = apiUrl + "/task/" + connectorProperties.getProperty("taskId") + "/assign";
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_assignTask_negative.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_propagate"),
            apiRestResponse.getBody().getString("error_propagate"));
      
   }
   
   /**
    * Positive test case for updateTask method with optional parameters.
    */
   @Test(dependsOnMethods = { "testAssignTaskWithNegativeCase" }, description = "podio {updateTask} integration test with optonal parameters.")
   public void testUpdateTaskWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateTask");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTask_optional.json");
      String taskId = esbRestResponse.getBody().getString("task_id");
      
      String apiEndPoint = apiUrl + "/task/" + taskId;
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("created_by").getString("name"), apiRestResponse
            .getBody().getJSONObject("created_by").getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getString("text"), apiRestResponse.getBody().getString("text"));
      Assert.assertEquals(esbRestResponse.getBody().getString("external_id"),
            apiRestResponse.getBody().getString("external_id"));
   }
   
   /**
    * Negative test case for updateTask method.
    */
   @Test(dependsOnMethods = { "testUpdateTaskWithOptionalParameters" }, description = "podio {updateTask} integration test with negative case.")
   public void testUpdateTaskWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateTask");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTask_negative.json");
      String taskId = connectorProperties.getProperty("taskId");
      String apiEndPoint = apiUrl + "/task/" + taskId;
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateTask_negative.json");
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
      
   }
   
   /**
    * Positive test case for completeTask method with mandatory parameters.
    */
   @Test(dependsOnMethods = { "testUpdateTaskWithNegativeCase" }, description = "podio {completeTask} integration test with mandatory parameters.")
   public void testCompleteTaskWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:completeTask");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_completeTask_mandatory.json");
      
      String apiEndPoint = apiUrl + "/task/" + connectorProperties.getProperty("taskId");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getBody().getString("status"), "completed");
   }
   
   /**
    * Negative test case for completeTask method.
    */
   @Test(dependsOnMethods = { "testCompleteTaskWithMandatoryParameters" }, description = "podio {completeTask} integration test with negative case.")
   public void testCompleteTaskWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:completeTask");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_completeTask_negative.json");
      
      String apiEndPoint = apiUrl + "/task/Invalid/complete";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_description"), apiRestResponse.getBody()
            .getString("error_description"));
      
   }
   
}
