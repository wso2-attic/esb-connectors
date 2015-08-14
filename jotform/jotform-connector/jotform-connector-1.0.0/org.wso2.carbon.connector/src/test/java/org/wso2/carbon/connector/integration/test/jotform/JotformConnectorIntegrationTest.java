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

package org.wso2.carbon.connector.integration.test.jotform;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.util.URLEncoder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class JotformConnectorIntegrationTest extends ConnectorIntegrationTestBase {
   
   private Map<String, String> esbRequestHeadersMap;
   
   private Map<String, String> apiRequestHeadersMap;
   
   private String apiEndpointUrl;
   
   /**
    * Set up the environment.
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {
   
      init("jotform-connector-1.0.0");
      
      esbRequestHeadersMap = new HashMap<String, String>();
      apiRequestHeadersMap = new HashMap<String, String>();
      
      esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
      esbRequestHeadersMap.put("Content-Type", "application/json");
      
      apiRequestHeadersMap.putAll(esbRequestHeadersMap);
      apiRequestHeadersMap.put("APIKEY", connectorProperties.getProperty("apiKey"));
      apiEndpointUrl = connectorProperties.getProperty("apiUrl");
      
   }
   
   /**
    * Positive test case for cloneForm method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jotform {cloneForm} integration test with mandatory parameters.")
   public void testCloneFormWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:cloneForm");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_cloneForm_mandatory.json");
      String clonedFormId = esbRestResponse.getBody().getJSONObject("content").getString("id");
      final String apiEndpoint = apiEndpointUrl + "/form/" + connectorProperties.getProperty("formId");
      final String clonedApiEndpoint = apiEndpointUrl + "/form/" + clonedFormId;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      RestResponse<JSONObject> clonedApiRestResponse =
            sendJsonRestRequest(clonedApiEndpoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");
      Assert.assertEquals(clonedApiRestResponse.getBody().getJSONObject("content").getString("title"), "Clone of "
            + apiRestResponse.getBody().getJSONObject("content").getString("title"));
      
   }
   
   /**
    * Method Name: cloneForm
    * Skipped Case: optional case
    * Reason: No optional parameter(s) to assert. 
    */
     
   /**
    * Negative test case for cloneForm method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jotform {cloneForm} integration test with negative case.")
   public void testCloneFormWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:cloneForm");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_cloneForm_negative.json");
      final String apiEndpoint = apiEndpointUrl + "/form/INVALID/clone";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap);
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("responseCode"),
            apiRestResponse.getBody().getString("responseCode"));
      
   }
   
   /**
    * Positive test case for getForm method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jotform {getForm} integration test with mandatory parameters.")
   public void testGetFormWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getForm");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getForm_mandatory.json");
      
      final String apiEndpoint =
            apiEndpointUrl + "/form/" + connectorProperties.getProperty("formId") + "?apikey="
                  + connectorProperties.getProperty("apiKey");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), "success");
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("content").getString("title"), apiRestResponse
            .getBody().getJSONObject("content").getString("title"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("content").getString("created_at"), apiRestResponse
            .getBody().getJSONObject("content").getString("created_at"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("content").getString("last_submission"),
            apiRestResponse.getBody().getJSONObject("content").getString("last_submission"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("content").getString("username"), apiRestResponse
            .getBody().getJSONObject("content").getString("username"));
      
   }
   
   /**
    * Method Name: getForm
    * Skipped Case: optional case
    * Reason: No optional parameter(s) to assert. 
    */
   
   /**
    * Negative test case for getForm method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jotform {getForm} integration test with negative case.")
   public void testGetFormWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getForm");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getForm_negative.json");
      
      final String apiEndpoint = apiEndpointUrl + "/form/INVALID?apikey=" + connectorProperties.getProperty("apiKey");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("responseCode"),
            apiRestResponse.getBody().getString("responseCode"));
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      
   }
   
   /**
    * Positive test case for getSubmission method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jotform {getSubmission} integration test with mandatory parameters.")
   public void testGetSubmissionWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getSubmission");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSubmission_mandatory.json");
      
      final String apiEndpoint =
            apiEndpointUrl + "/submission/" + connectorProperties.getProperty("submissionId") + "?apikey="
                  + connectorProperties.getProperty("apiKey");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("content").getString("id"), apiRestResponse.getBody()
            .getJSONObject("content").getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("content").getString("form_id"), apiRestResponse
            .getBody().getJSONObject("content").getString("form_id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("content").getString("ip"), apiRestResponse.getBody()
            .getJSONObject("content").getString("ip"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("content").getString("created_at"), apiRestResponse
            .getBody().getJSONObject("content").getString("created_at"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("content").getString("status"), apiRestResponse
            .getBody().getJSONObject("content").getString("status"));
   }
   
   /**
    * Method Name: getSubmission
    * Skipped Case: optional case
    * Reason: No optional parameter(s) to assert. 
    */
   
   /**
    * Negative test case for getSubmission method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jotform {getSubmission} integration test with negative case.")
   public void testGetSubmissionWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getSubmission");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSubmission_negative.json");
      
      final String apiEndpoint =
            apiEndpointUrl + "/submission/INVALID?apikey=" + connectorProperties.getProperty("apiKey");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("responseCode"),
            apiRestResponse.getBody().getString("responseCode"));
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      
   }
   
   /**
    * Positive test case for getUserSubmissions method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jotform {getUserSubmissions} integration test with mandatory parameters.")
   public void testGetUserSubmissionsWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getUserSubmissions");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserSubmissions_mandatory.json");
      
      final String apiEndpoint =
            apiEndpointUrl + "/user/submissions?apikey=" + connectorProperties.getProperty("apiKey");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("id"),
            apiRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("form_id"),
            apiRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("form_id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("ip"),
            apiRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("ip"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("created_at"),
            apiRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("created_at"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("status"),
            apiRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("status"));
      
   }
   
   /**
    * Positive test case for getUserSubmissions method with optional parameters.
    * 
    * @throws IOException
    * @throws JSONException
    */
   @Test(groups = { "wso2.esb" }, description = "jotform {getUserSubmissions} integration test with optional parameters.")
   public void testGetUserSubmissionsWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getUserSubmissions");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserSubmissions_optional.json");
      String filter = "{\"created_at:gt\":\"2015-06-20 00:00:00\"}";
      final String apiEndpoint =
            apiEndpointUrl + "/user/submissions?apikey=" + connectorProperties.getProperty("apiKey")
                  + "&offset=0&limit=10&filter=" + filter + "&orderby=id";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("id"),
            apiRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("form_id"),
            apiRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("form_id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("ip"),
            apiRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("ip"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("created_at"),
            apiRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("created_at"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("status"),
            apiRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("status"));
   }
   
   /**
    * Method Name: getUserSubmissions
    * Skipped Case: negative case
    * Reason: No parameter(s) to test negative case. 
    */
   
   /**
    * Positive test case for getFormSubmissions method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jotform {getFormSubmissions} integration test with mandatory parameters.")
   public void testGetFormSubmissionsWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getFormSubmissions");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFormSubmissions_mandatory.json");
      
      final String apiEndpoint =
            apiEndpointUrl + "/form/" + connectorProperties.getProperty("formId") + "/submissions?apikey="
                  + connectorProperties.getProperty("apiKey");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("id"),
            apiRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("updated_at"),
            apiRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("updated_at"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("ip"),
            apiRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("ip"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("created_at"),
            apiRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("created_at"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("status"),
            apiRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("status"));
      
   }
   
   /**
    * Positive test case for getFormSubmissions method with optional parameters.
    * 
    * @throws IOException
    * @throws JSONException
    */
   @Test(groups = { "wso2.esb" }, description = "jotform {getFormSubmissions} integration test with optional parameters.")
   public void testGetFormSubmissionsWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getFormSubmissions");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFormSubmissions_optional.json");
      String filter = "{\"created_at:lt\":\"2015-06-20 00:00:00\"}";
      final String apiEndpoint =
            apiEndpointUrl + "/form/" + connectorProperties.getProperty("formId") + "/submissions?apikey="
                  + connectorProperties.getProperty("apiKey") + "&offset=0&limit=10&filter=" + filter + "&orderby=id";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("id"),
            apiRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("updated_at"),
            apiRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("updated_at"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("ip"),
            apiRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("ip"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("created_at"),
            apiRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("created_at"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("status"),
            apiRestResponse.getBody().getJSONArray("content").getJSONObject(0).getString("status"));
   }
   
   /**
    * Negative test case for getFormSubmissions method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "jotform {getFormSubmissions} integration test with negative case.")
   public void testGetFormSubmissionsWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getFormSubmissions");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFormSubmissions_negative.json");
      
      final String apiEndpoint =
            apiEndpointUrl + "/form/INVALID/submissions?apikey=" + connectorProperties.getProperty("apiKey");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("responseCode"),
            apiRestResponse.getBody().getString("responseCode"));
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      
   }
   
}
