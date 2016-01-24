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

package org.wso2.carbon.connector.integration.test.jira;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class JiraConnectorIntegrationTest extends ConnectorIntegrationTestBase {
   
   private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
   
   private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
   
   private String apiUrl;
   
   /**
    * Set up the environment.
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {
   
      init("jira-connector-1.0.0");
      
      esbRequestHeadersMap.put("Content-Type", "application/json");
      
      // Create base64-encoded auth string using username and password
      final String authString =
            connectorProperties.getProperty("username") + ":" + connectorProperties.getProperty("password");
      final String base64AuthString = Base64.encode(authString.getBytes());
      
      apiRequestHeadersMap.put("Authorization", "Basic " + base64AuthString);
      
      apiRequestHeadersMap.putAll(esbRequestHeadersMap);
      
      connectorProperties.setProperty("responseType", "json");
      
      apiUrl = connectorProperties.getProperty("apiUrl") + "/rest/api/2";
      
   }
   
   /**
    * Positive test case for createComponent method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {createComponent} integration test with mandatory parameters.")
   public void testCreateComponentWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createComponent");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComponent_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      String componentIdMandatory = esbRestResponse.getBody().getString("id");
      connectorProperties.put("componentIdMandatory", componentIdMandatory);
      
      String apiEndPoint = apiUrl + "/component/" + connectorProperties.getProperty("componentIdMandatory");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(connectorProperties.getProperty("componentNameMandatory"), apiRestResponse.getBody()
            .getString("name"));
      Assert.assertEquals(connectorProperties.getProperty("project"), apiRestResponse.getBody().getString("project"));
   }
   
   /**
    * Positive test case for createComponent method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {createComponent} integration test with optional parameters.")
   public void testCreateComponentWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createComponent");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComponent_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      String componentIdOptional = esbRestResponse.getBody().getString("id");
      connectorProperties.put("componentIdOptional", componentIdOptional);
      
      String apiEndPoint = apiUrl + "/component/" + connectorProperties.getProperty("componentIdOptional");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(connectorProperties.getProperty("componentNameOptional"), apiRestResponse.getBody()
            .getString("name"));
      Assert.assertEquals(connectorProperties.getProperty("project"), apiRestResponse.getBody().getString("project"));
      Assert.assertEquals(connectorProperties.getProperty("componentDescription"),
            apiRestResponse.getBody().getString("description"));
      Assert.assertEquals(connectorProperties.getProperty("leadUserName"),
            apiRestResponse.getBody().getJSONObject("lead").getString("name"));
      Assert.assertEquals(connectorProperties.getProperty("assigneeType"),
            apiRestResponse.getBody().getString("assigneeType"));
      
   }
   
   /**
    * Negative test case for createComponent method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateComponentWithMandatoryParameters" }, description = "jira {createComponent} integration test with negative case.")
   public void testCreateComponentWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createComponent");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComponent_negative.json");
      String apiEndPoint = apiUrl + "/component";
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createComponent_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("errors").getString("name"), esbRestResponse
            .getBody().getJSONObject("errors").getString("name"));
      Assert.assertEquals(apiRestResponse.getBody().getJSONArray("errorMessages").length(), esbRestResponse.getBody()
            .getJSONArray("errorMessages").length());
      
   }
   
   /**
    * Positive test case for getComponent method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateComponentWithOptionalParameters" }, description = "jira {getComponent} integration test with mandatory parameters.")
   public void testGetComponentWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getComponent");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getComponent_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint = apiUrl + "/component/" + connectorProperties.getProperty("componentIdOptional");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getString("project"), apiRestResponse.getBody()
            .getString("project"));
      Assert.assertEquals(esbRestResponse.getBody().getString("description"),
            apiRestResponse.getBody().getString("description"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("lead").getString("name"), apiRestResponse.getBody()
            .getJSONObject("lead").getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getString("assigneeType"),
            apiRestResponse.getBody().getString("assigneeType"));
   }
   
   /**
    * Test case: testGetComponentWithOptionalParameters. 
    * Status: Skipped. 
    * Reason : There are no optional parameters to assert.
    */
   
   /**
    * Negative test case for getComponent method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {getComponent} integration test with negative case.")
   public void testGetComponentWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getComponent");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getComponent_negative.json");
      
      String apiEndPoint = apiUrl + "/component/INVALID";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(apiRestResponse.getBody().getJSONArray("errorMessages").length(), esbRestResponse.getBody()
            .getJSONArray("errorMessages").length());
      Assert.assertEquals(apiRestResponse.getBody().getJSONArray("errorMessages").getString(0), esbRestResponse
            .getBody().getJSONArray("errorMessages").getString(0));
   }
   
   /**
    * Test case: testUpdateComponentWithMandatoryParameters. 
    * Status: Skipped. 
    * Reason : There are no mandatory parameters to assert.
    */
   
   /**
    * Positive test case for updateComponent method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateComponentWithOptionalParameters" }, description = "jira {updateComponent} integration test with optional parameters.")
   public void testUpdateComponentWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateComponent");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateComponent_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint = apiUrl + "/component/" + connectorProperties.getProperty("componentIdOptional");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(connectorProperties.getProperty("updatedComponentName"),
            apiRestResponse.getBody().getString("name"));
      Assert.assertEquals(connectorProperties.getProperty("updatedComponentDescription"), apiRestResponse.getBody()
            .getString("description"));
      Assert.assertEquals(connectorProperties.getProperty("updatedLeadUserName"), apiRestResponse.getBody()
            .getJSONObject("lead").getString("name"));
      Assert.assertEquals(connectorProperties.getProperty("updatedAssigneeType"),
            apiRestResponse.getBody().getString("assigneeType"));
      
   }
   
   /**
    * Negative test case for updateComponent method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {updateComponent} integration test with negative case.")
   public void testUpdateComponentWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateComponent");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateComponent_negative.json");
      
      String apiEndPoint = apiUrl + "/component/INVALID";
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "api_updateComponent_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(apiRestResponse.getBody().getJSONArray("errorMessages").length(), esbRestResponse.getBody()
            .getJSONArray("errorMessages").length());
      Assert.assertEquals(apiRestResponse.getBody().getJSONArray("errorMessages").getString(0), esbRestResponse
            .getBody().getJSONArray("errorMessages").getString(0));
   }
   
   /**
    * Positive test case for countComponentRelatedIssues method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {countComponentRelatedIssues} integration test with mandatory parameters.")
   public void testCountComponentRelatedIssuesWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:countComponentRelatedIssues");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                  "esb_countComponentRelatedIssues_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint =
            apiUrl + "/component/" + connectorProperties.getProperty("componentIdMandatory") + "/relatedIssueCounts";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getString("self"), apiRestResponse.getBody().getString("self"));
      Assert.assertEquals(esbRestResponse.getBody().getString("issueCount"),
            apiRestResponse.getBody().getString("issueCount"));
   }
   
   /**
    * Method Name: countComponentRelatedIssues Skipped Case: optional case Reason: No optional parameter(s) to
    * assert
    */
   
   /**
    * Negative test case for countComponentRelatedIssues method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateComponentWithMandatoryParameters" }, description = "jira {countComponentRelatedIssues} integration test with negative case.")
   public void testCountComponentRelatedIssuesWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:countComponentRelatedIssues");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_countComponentRelatedIssues_negative.json");
      
      String apiEndPoint = apiUrl + "/component/INVALID/relatedIssueCounts";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(apiRestResponse.getBody().getJSONArray("errorMessages").length(), esbRestResponse.getBody()
            .getJSONArray("errorMessages").length());
      Assert.assertEquals(apiRestResponse.getBody().getJSONArray("errorMessages").getString(0), esbRestResponse
            .getBody().getJSONArray("errorMessages").getString(0));
   }
   
   /**
    * Positive test case for createBulkIssue method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {createBulkIssue} integration test with mandatory parameters.")
   public void testCreateBulkIssueWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createBulkIssue");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createBulkIssue_mandatory.json");
      
      final JSONObject esbResponseObject = esbRestResponse.getBody().getJSONArray("issues").getJSONObject(0);
      
      final String bulkIssueId = esbResponseObject.getString("id");
      connectorProperties.setProperty("bulkIssueId", bulkIssueId);
      
      final String apiEndPoint = apiUrl + "/issue/" + bulkIssueId;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONObject apiRestResponseObject = apiRestResponse.getBody().getJSONObject("fields");
      
      Assert.assertEquals(apiRestResponseObject.getJSONObject("issuetype").getString("id"),
            connectorProperties.getProperty("issueTypeId"));
      Assert.assertEquals(apiRestResponseObject.getJSONObject("project").getString("key"),
            connectorProperties.getProperty("project"));
      Assert.assertEquals(apiRestResponseObject.getString("summary"), connectorProperties.getProperty("summary"));
   }
   
   /**
    * Positive test case for createBulkIssue method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {createBulkIssue} integration test with optional parameters.")
   public void testCreateBulkIssueWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createBulkIssue");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createBulkIssue_optional.json");
      
      final JSONObject esbResponseObject = esbRestResponse.getBody().getJSONArray("issues").getJSONObject(0);
      
      final String bulkIssueIdOptional = esbResponseObject.getString("id");
      connectorProperties.setProperty("bulkIssueIdOptional", bulkIssueIdOptional);
      
      final String apiEndPoint = apiUrl + "/issue/" + bulkIssueIdOptional;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONObject apiRestResponseObject = apiRestResponse.getBody().getJSONObject("fields");
      
      Assert.assertEquals(apiRestResponseObject.getJSONObject("assignee").getString("name"),
            connectorProperties.getProperty("assigneeName"));
      Assert.assertEquals(apiRestResponseObject.getJSONObject("reporter").getString("name"),
            connectorProperties.getProperty("reporterName"));
      Assert.assertEquals(apiRestResponseObject.getString("description"),
            connectorProperties.getProperty("description"));
      Assert.assertEquals(apiRestResponseObject.getJSONArray("labels").get(0), connectorProperties.getProperty("label"));
   }
   
   /**
    * Negative test case for createBulkIssue method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {createBulkIssue} integration test with negative case.")
   public void testCreateBulkIssueWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createBulkIssue");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createBulkIssue_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      final JSONObject esbResponseObject = esbRestResponse.getBody();
      
      final String apiEndPoint = apiUrl + "/issue/bulk";
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createBulkIssue_negative.json");
      final JSONObject apiResponseObject = apiRestResponse.getBody();
      
      Assert.assertEquals(esbResponseObject.getJSONArray("errors").toString(), apiResponseObject.getJSONArray("errors")
            .toString());
   }
   
   /**
    * Positive test case for getCommentById method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {getCommentById} integration test with mandatory parameters.")
   public void testGetCommentByIdWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getCommentById");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCommentById_mandatory.json");
      final JSONObject esbRestResponseObject = esbRestResponse.getBody();
      
      final String apiEndPoint =
            apiUrl + "/issue/" + connectorProperties.getProperty("issueIdOrKey") + "/comment/"
                  + connectorProperties.getProperty("commentId");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONObject apiRestResponseObject = apiRestResponse.getBody();
      
      Assert.assertEquals(esbRestResponseObject.getJSONObject("author").getString("name"), apiRestResponseObject
            .getJSONObject("author").getString("name"));
      Assert.assertEquals(esbRestResponseObject.getString("body"), apiRestResponseObject.getString("body"));
      Assert.assertEquals(esbRestResponseObject.getString("created"), apiRestResponseObject.getString("created"));
   }
   
   /**
    * Positive test case for getCommentById method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {getCommentById} integration test with optional parameters.")
   public void testGetCommentByIdWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getCommentById");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCommentById_optional.json");
      final JSONObject esbRestResponseObject = esbRestResponse.getBody();
      
      final String apiEndPoint =
            apiUrl + "/issue/" + connectorProperties.getProperty("issueIdOrKey") + "/comment/"
                  + connectorProperties.getProperty("commentId") + "?expand="
                  + connectorProperties.getProperty("expand");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONObject apiRestResponseObject = apiRestResponse.getBody();
      
      Assert.assertEquals(esbRestResponseObject.getString("renderedBody"),
            apiRestResponseObject.getString("renderedBody"));
   }
   
   /**
    * Negative test case for getCommentById method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {getCommentById} integration test negative case.")
   public void testGetCommentByIdWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getCommentById");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCommentById_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      final JSONObject esbResponseObject = esbRestResponse.getBody();
      
      final String apiEndPoint = apiUrl + "/issue/INVALID/comment/" + connectorProperties.getProperty("commentId");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONObject apiResponseObject = apiRestResponse.getBody();
      
      Assert.assertEquals(esbResponseObject.getJSONArray("errorMessages").getString(0),
            apiResponseObject.getJSONArray("errorMessages").getString(0));
   }
   
   /**
    * Positive test case for assignIssueToUser method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {assignIssueToUser} integration test with mandatory parameters.")
   public void testAssignIssueToUserWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:assignIssueToUser");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_assignIssueToUser_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
      
      final String apiEndPoint = apiUrl + "/issue/" + connectorProperties.getProperty("issueIdOrKey");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONObject apiRestResponseObject = apiRestResponse.getBody().getJSONObject("fields");
      
      Assert.assertEquals(apiRestResponseObject.getJSONObject("assignee").getString("name"),
            connectorProperties.getProperty("assigneeName"));
      
   }
   
   /**
    * Test case: testAssignIssueToUserWithOptionalParameters. 
    * Status: Skipped. 
    * Reason : There are no optional parameters to assert.
    */
   
   /**
    * Negative test case for assignIssueToUser method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {assignIssueToUser} integration test with negative case.")
   public void testAssignIssueToUserWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:assignIssueToUser");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_assignIssueToUser_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      
      final String apiEndPoint = apiUrl + "/issue/" + connectorProperties.getProperty("issueIdOrKey") + "/assignee";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_assignIssueToUser_negative.json");
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").getString("assignee"), apiRestResponse
            .getBody().getJSONObject("errors").getString("assignee"));
   }
   
   /**
    * Positive test case for sendNotification method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {sendNotification} integration test with mandatory parameters.")
   public void testSendNotificationWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:sendNotification");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendNotification_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
   }
   
   /**
    * Test case: testSendNotificationWithOptionalParameters. 
    * Status: Skipped. 
    * Reason : The method returns only "204 no content" as the response for successful method execution.Therefore cannot assert the optional parameters. 
    */
   
   /**
    * Negative test case for sendNotification method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {sendNotification} integration test with negative case.")
   public void testSendNotificationWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:sendNotification");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendNotification_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      
      final String apiEndPoint = apiUrl + "/issue/" + connectorProperties.getProperty("issueIdOrKey") + "/notify";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_sendNotification_negative.json");
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errorMessages").getString(0), apiRestResponse
            .getBody().getJSONArray("errorMessages").getString(0));
   }
   
   /**
    * Positive test case for getWatchersForIssue method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {getWatchersForIssue} integration test with mandatory parameters.")
   public void testGetWatchersForIssueWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getWatchersForIssue");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getWatchersForIssue_mandatory.json");
      final JSONObject esbResponseObject = esbRestResponse.getBody();
      
      final String apiEndPoint =
            apiUrl + "/issue/" + connectorProperties.getProperty("issueIdWithWatchList") + "/watchers";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONObject apiResponseObject = apiRestResponse.getBody();
      
      Assert.assertEquals(esbResponseObject.getString("self"), apiResponseObject.getString("self"));
      Assert.assertEquals(esbResponseObject.getString("isWatching"), apiResponseObject.getString("isWatching"));
      Assert.assertEquals(esbResponseObject.getString("watchCount"), apiResponseObject.getString("watchCount"));
      Assert.assertEquals(esbResponseObject.getJSONArray("watchers").getJSONObject(0).getString("name"),
            apiResponseObject.getJSONArray("watchers").getJSONObject(0).getString("name"));
      Assert.assertEquals(esbResponseObject.getJSONArray("watchers").getJSONObject(0).getString("active"),
            apiResponseObject.getJSONArray("watchers").getJSONObject(0).getString("active"));
   }
   
   /**
    * Test case: testGetWatchersForIssueWithOptionalParameters. 
    * Status: Skipped. 
    * Reason : There are no optional parameters to assert.
    */
   
   /**
    * Negative test case for getWatchersForIssue method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {getWatchersForIssue} integration test negative case.")
   public void testGetWatchersForIssueWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getWatchersForIssue");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getWatchersForIssue_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      final JSONObject esbResponseObject = esbRestResponse.getBody();
      
      final String apiEndPoint = apiUrl + "/issue/INVALID/watchers";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONObject apiResponseObject = apiRestResponse.getBody();
      
      Assert.assertEquals(esbResponseObject.getJSONArray("errorMessages").getString(0),
            apiResponseObject.getJSONArray("errorMessages").getString(0));
   }
   
   /**
    * Positive test case for removeUserFromWatcherList method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetWatchersForIssueWithMandatoryParameters" }, description = "jira {removeUserFromWatcherList} integration test with mandatory parameters.")
   public void testRemoveUserFromWatcherListWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:removeUserFromWatcherList");
      
      final String apiEndPoint =
            apiUrl + "/issue/" + connectorProperties.getProperty("issueIdWithWatchList") + "/watchers";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final int watchesCountBeforeRemove = apiRestResponse.getBody().getInt("watchCount");
      
      sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_removeUserFromWatcherList_mandatory.json");
      
      apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final int watchesCountAfterRemove = apiRestResponse.getBody().getInt("watchCount");
      
      Assert.assertNotSame(watchesCountAfterRemove, watchesCountBeforeRemove);
   }
   
   /**
    * Test case: testRemoveUserFromWatcherListWithOptionalParameters. 
    * Status: Skipped. 
    * Reason : There are no optional parameters to assert.
    */
   
   /**
    * Test case: testRemoveUserFromWatcherListWithNegativeCase. 
    * Status: Skipped. 
    * Reason : No error message is returned.
    */
   
   /**
    * Positive test case for createIssueLink method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {createIssueLink} integration test with mandatory parameters.")
   public void testcreateIssueLinkWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createIssueLink");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createIssueLink_mandatory.json");
      
      Assert.assertEquals(201, esbRestResponse.getHttpStatusCode());
      
      String issueLinkLocation = esbRestResponse.getHeadersMap().get("Location").toString();
      issueLinkLocation = issueLinkLocation.substring(1, issueLinkLocation.length() - 1);
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(issueLinkLocation, "GET", apiRequestHeadersMap);
      final JSONObject apiResponseObject = apiRestResponse.getBody();
      final String issueLinkIdMand = apiResponseObject.getString("id");
      connectorProperties.setProperty("issueLinkIdMand", issueLinkIdMand);
      
      Assert.assertEquals(apiResponseObject.getJSONObject("type").getString("name"),
            connectorProperties.getProperty("issueLinkType"));
      Assert.assertEquals(apiResponseObject.getJSONObject("inwardIssue").getString("key"),
            connectorProperties.getProperty("inwardIssueKey"));
      Assert.assertEquals(apiResponseObject.getJSONObject("outwardIssue").getString("key"),
            connectorProperties.getProperty("outwardIssueKey"));
      
   }
   
   /**
    * Test case: testcreateIssueLinkWithOptionalParameters. 
    * Status: Skipped. 
    * Reason : There are no optional parameters to assert.
    */
   
   /**
    * Negative test case for createIssueLink method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {createIssueLink} integration test negative case.")
   public void testCreateIssueLinkWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createIssueLink");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createIssueLink_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 500);
      final JSONObject esbResponseObject = esbRestResponse.getBody();
      
      final String apiEndPoint = apiUrl + "/issueLink";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createIssueLink_negative.json");
      final JSONObject apiResponseObject = apiRestResponse.getBody();
      
      Assert.assertEquals(esbResponseObject.getJSONArray("errorMessages").getString(0),
            apiResponseObject.getJSONArray("errorMessages").getString(0));
   }
   
   /**
    * Positive test case for getIssueLinkById method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testcreateIssueLinkWithMandatoryParameters" }, description = "jira {getIssueLinkById} integration test with mandatory parameters.")
   public void testGetIssueLinkByIdWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getIssueLinkById");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getIssueLinkById_mandatory.json");
      final JSONObject esbResponseObject = esbRestResponse.getBody();
      
      final String apiEndPoint = apiUrl + "/issueLink/" + connectorProperties.getProperty("issueLinkIdMand");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONObject apiResponseObject = apiRestResponse.getBody();
      Assert.assertEquals(esbResponseObject.getString("self"), apiResponseObject.getString("self"));
      Assert.assertEquals(esbResponseObject.getJSONObject("type").getString("name"),
            apiResponseObject.getJSONObject("type").getString("name"));
      Assert.assertEquals(esbResponseObject.getJSONObject("inwardIssue").getString("id"), apiResponseObject
            .getJSONObject("inwardIssue").getString("id"));
   }
   
   /**
    * Test case: testGetIssueLinkByIdWithOptionalParameters. 
    * Status: Skipped. 
    * Reason : There are no optional parameters to assert.
    */
   
   /**
    * Test case: testGetIssueLinkByIdWithNegativeCase. 
    * Status: Skipped. 
    * Reason : No error message is returned in negative case.
    */
   
   /**
    * Positive test case for listGroupUserPicker method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {listGroupUserPicker} integration test with mandatory parameters.")
   public void testListGroupUserPickerWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listGroupUserPicker");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listGroupUserPicker_mandatory.json");
      final JSONObject esbResponseObject = esbRestResponse.getBody();
      
      final String apiEndPoint = apiUrl + "/groupuserpicker?query=admin";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONObject apiResponseObject = apiRestResponse.getBody();
      
      Assert.assertEquals(esbResponseObject.getJSONObject("users").getString("total"),
            apiResponseObject.getJSONObject("users").getString("total"));
      Assert.assertEquals(
            esbResponseObject.getJSONObject("users").getJSONArray("users").getJSONObject(0).getString("name"),
            apiResponseObject.getJSONObject("users").getJSONArray("users").getJSONObject(0).getString("name"));
      Assert.assertEquals(
            esbResponseObject.getJSONObject("users").getJSONArray("users").getJSONObject(0).getString("key"),
            apiResponseObject.getJSONObject("users").getJSONArray("users").getJSONObject(0).getString("key"));
      Assert.assertEquals(
            esbResponseObject.getJSONObject("users").getJSONArray("users").getJSONObject(0).getString("displayName"),
            apiResponseObject.getJSONObject("users").getJSONArray("users").getJSONObject(0).getString("displayName"));
      Assert.assertEquals(esbResponseObject.getJSONObject("users").getString("header"), apiResponseObject
            .getJSONObject("users").getString("header"));
      
   }
   
   /**
    * Positive test case for listGroupUserPicker method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {listGroupUserPicker} integration test with mandatory parameters.")
   public void testListGroupUserPickerWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listGroupUserPicker");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listGroupUserPicker_optional.json");
      final JSONObject esbResponseObject = esbRestResponse.getBody();
      final String apiEndPoint = apiUrl + "/groupuserpicker?query=admin&maxResults=1&showAvatar=true";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONObject apiResponseObject = apiRestResponse.getBody();
      Assert.assertEquals(esbResponseObject.getJSONObject("users").getString("total"),
            apiResponseObject.getJSONObject("users").getString("total"));
      Assert.assertEquals(
            esbResponseObject.getJSONObject("users").getJSONArray("users").getJSONObject(0).getString("name"),
            apiResponseObject.getJSONObject("users").getJSONArray("users").getJSONObject(0).getString("name"));
      Assert.assertEquals(
            esbResponseObject.getJSONObject("users").getJSONArray("users").getJSONObject(0).getString("avatarUrl"),
            apiResponseObject.getJSONObject("users").getJSONArray("users").getJSONObject(0).getString("avatarUrl"));
      Assert.assertEquals(
            esbResponseObject.getJSONObject("users").getJSONArray("users").getJSONObject(0).getString("displayName"),
            apiResponseObject.getJSONObject("users").getJSONArray("users").getJSONObject(0).getString("displayName"));
      Assert.assertEquals(esbResponseObject.getJSONObject("users").getString("header"), apiResponseObject
            .getJSONObject("users").getString("header"));
      
   }
   
   /**
    * Negative test case for listGroupUserPicker method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {listGroupUserPicker} integration test negative case.")
   public void testListGroupUserPickerWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listGroupUserPicker");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listGroupUserPicker_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      final JSONObject esbResponseObject = esbRestResponse.getBody();
      
      final String apiEndPoint = apiUrl + "/groupuserpicker";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONObject apiResponseObject = apiRestResponse.getBody();
      
      Assert.assertEquals(esbResponseObject.getJSONArray("errorMessages").getString(0),
            apiResponseObject.getJSONArray("errorMessages").getString(0));
   }
   
   /**
    * Positive test case for listGroupPicker method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {listGroupPicker} integration test with mandatory parameters.")
   public void testListGroupPickerWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listGroupPicker");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listGroupPicker_mandatory.json");
      final JSONObject esbResponseObject = esbRestResponse.getBody();
      final String apiEndPoint = apiUrl + "/groups/picker";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONObject apiResponseObject = apiRestResponse.getBody();
      Assert.assertEquals(esbResponseObject.getString("total"), apiResponseObject.getString("total"));
      Assert.assertEquals(esbResponseObject.getJSONArray("groups").getJSONObject(0).getString("name"),
            apiResponseObject.getJSONArray("groups").getJSONObject(0).getString("name"));
      Assert.assertEquals(esbResponseObject.getJSONArray("groups").getJSONObject(0).getString("html"),
            apiResponseObject.getJSONArray("groups").getJSONObject(0).getString("html"));
      Assert.assertEquals(esbResponseObject.getJSONArray("groups").getJSONObject(0).getJSONArray("labels")
            .getJSONObject(0).getString("text"), apiResponseObject.getJSONArray("groups").getJSONObject(0)
            .getJSONArray("labels").getJSONObject(0).getString("text"));
      Assert.assertEquals(esbResponseObject.getJSONArray("groups").getJSONObject(0).getJSONArray("labels")
            .getJSONObject(0).getString("title"), apiResponseObject.getJSONArray("groups").getJSONObject(0)
            .getJSONArray("labels").getJSONObject(0).getString("title"));
      
   }
   
   /**
    * Positive test case for listGroupPicker method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {listGroupPicker} integration test with optional parameters.")
   public void testListGroupPickerWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listGroupPicker");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listGroupPicker_optional.json");
      final JSONObject esbResponseObject = esbRestResponse.getBody();
      
      final String apiEndPoint = apiUrl + "/groups/picker?query=admin&maxResults=1&exclude=system-administrators";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONObject apiResponseObject = apiRestResponse.getBody();
      
      Assert.assertEquals(esbResponseObject.getString("header"), apiResponseObject.getString("header"));
      Assert.assertEquals(esbResponseObject.getJSONArray("groups").getJSONObject(0).getString("name"),
            apiResponseObject.getJSONArray("groups").getJSONObject(0).getString("name"));
      Assert.assertEquals(esbResponseObject.getJSONArray("groups").getJSONObject(0).getJSONArray("labels")
            .getJSONObject(0).getString("text"), apiResponseObject.getJSONArray("groups").getJSONObject(0)
            .getJSONArray("labels").getJSONObject(0).getString("text"));
      Assert.assertEquals(esbResponseObject.getJSONArray("groups").getJSONObject(0).getJSONArray("labels")
            .getJSONObject(0).getString("title"), apiResponseObject.getJSONArray("groups").getJSONObject(0)
            .getJSONArray("labels").getJSONObject(0).getString("title"));
      Assert.assertEquals(esbResponseObject.getJSONArray("groups").getJSONObject(0).getString("html"),
            apiResponseObject.getJSONArray("groups").getJSONObject(0).getString("html"));
      
   }
   
   /**
    * Negative test case for listGroupPicker method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {listGroupPicker} integration test negative case.")
   public void testListGroupPickerWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listGroupPicker");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listGroupPicker_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      
      final String apiEndPoint = apiUrl + "/groups/picker?maxResults=INVALID";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
   }
   
   /**
    * Positive test case for getAttachmentById method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {getAttachmentById} integration test with mandatory parameters.")
   public void testGetAttachmentByIdWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getAttachmentById");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAttachmentById_mandatory.json");
      final JSONObject esbResponseObject = esbRestResponse.getBody();
      
      final String apiEndPoint = apiUrl + "/attachment/" + connectorProperties.getProperty("attachmentId");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONObject apiResponseObject = apiRestResponse.getBody();
      
      Assert.assertEquals(esbResponseObject.getString("self"), apiResponseObject.getString("self"));
      Assert.assertEquals(esbResponseObject.getString("filename"), apiResponseObject.getString("filename"));
      Assert.assertEquals(esbResponseObject.getString("mimeType"), apiResponseObject.getString("mimeType"));
      Assert.assertEquals(esbResponseObject.getString("content"), apiResponseObject.getString("content"));
      
   }
   
   /**
    * Test case: testGetAttachmentByIdWithOptionalParameters. 
    * Status: Skipped. 
    * Reason : There are no optional parameters to assert.
    */
   
   /**
    * Negative test case for getAttachmentById method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {getAttachmentById} integration test negative case.")
   public void testGetAttachmentByIdWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getAttachmentById");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAttachmentById_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      final JSONObject esbResponseObject = esbRestResponse.getBody();
      
      final String apiEndPoint = apiUrl + "/attachment/INVALID";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONObject apiResponseObject = apiRestResponse.getBody();
      
      Assert.assertEquals(esbResponseObject.getJSONArray("errorMessages").getString(0),
            apiResponseObject.getJSONArray("errorMessages").getString(0));
   }
   
   /**
    * Positive test case for addVotesForIssue method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {addVotesForIssue} integration test with mandatory parameters.")
   public void testAddVotesForIssueWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:addVotesForIssue");
      
      final String apiEndPoint = apiUrl + "/issue/" + connectorProperties.getProperty("issueId");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      final JSONObject apiResponseObjectBefore = apiRestResponse.getBody();
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addVotesForIssue_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
      
      apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONObject apiResponseObjectAfter = apiRestResponse.getBody();
      
      Assert.assertEquals(apiResponseObjectBefore.getJSONObject("fields").getJSONObject("votes").getInt("votes") + 1,
            apiResponseObjectAfter.getJSONObject("fields").getJSONObject("votes").getInt("votes"));
   }
   
   /**
    * Test case: testAddVotesForIssueWithOptionalParameters. 
    * Status: Skipped. 
    * Reason : There are no optional parameters to assert.
    */
   
   /**
    * Negative test case for addVotesForIssue method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jira {getAttachmentById} integration test negative case.")
   public void testAddVotesForIssueWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:addVotesForIssue");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addVotesForIssue_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      
      final JSONObject esbResponseObject = esbRestResponse.getBody();
      
      final String apiEndPoint = apiUrl + "/issue/" + connectorProperties.getProperty("issueId") + "/votes";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
      final JSONObject apiResponseObject = apiRestResponse.getBody();
      
      Assert.assertEquals(esbResponseObject.getJSONArray("errorMessages").getString(0),
            apiResponseObject.getJSONArray("errorMessages").getString(0));
   }
   
}
