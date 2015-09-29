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

package org.wso2.carbon.connector.integration.test.contactform;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class ContactformConnectorIntegrationTest extends ConnectorIntegrationTestBase {

   private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

   private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

   private Map<String, String> parametersMap = new HashMap<String, String>();

   private String apiUrl;

   private String apiKey;

   /**
    * Set up the environment.
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {

      init("contactform-connector-1.0.0");
      esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
      apiRequestHeadersMap.put("Accept-Charset", "UTF-8");
      esbRequestHeadersMap.put("Content-Type", "application/json");
      apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");
      apiUrl = connectorProperties.getProperty("apiUrl") + "/api/";
      apiKey = "?apiKey=" + connectorProperties.getProperty("apiKey");

   }

   /**
    * Positive test case for listForms method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "contactform {listForms} integration test with mandatory parameters.")
   public void testListFormsWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:listForms");

      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_listForms_mandatory.json");

      final String apiEndPoint = apiUrl + "forms.json" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("forms").getJSONObject(0).getString("formId"),
            apiRestResponse.getBody().getJSONArray("forms").getJSONObject(0).getString("formId"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("forms").getJSONObject(0).getString("formName"),
            apiRestResponse.getBody().getJSONArray("forms").getJSONObject(0).getString("formName"));

   }

   /**
    * Method Name: listForms 
    * Skipped Case: optional case 
    * Reason: No optional parameter(s) to assert
    */

   /**
    * Method Name: listForms 
    * Skipped Case: negative case 
    * Reason: No parameters cause a negative scenario
    */

   /**
    * Positive test case for getForm method with mandatory parameters.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "contactform {getForm} integration test with mandatory parameters.")
   public void testGetFormWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getForm");

      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getForm_mandatory.json");

      final String apiEndPoint = apiUrl + "forms/" + connectorProperties.getProperty("formId") + ".json" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("forms").getJSONObject(0).getString("formId"),
            apiRestResponse.getBody().getJSONArray("forms").getJSONObject(0).getString("formId"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("forms").getJSONObject(0).getString("formName"),
            apiRestResponse.getBody().getJSONArray("forms").getJSONObject(0).getString("formName"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("forms").getJSONObject(0).getString("formCreated"),
            apiRestResponse.getBody().getJSONArray("forms").getJSONObject(0).getString("formCreated"));

   }

   /**
    * Method Name: getForm 
    * Skipped Case: optional case 
    * Reason: No optional parameter(s) to assert
    */

   /**
    * Negative test case for getForm method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "contactform {getForm} integration test with negative case.")
   public void testGetFormWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getForm");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getForm_negative.json");

      final String apiEndpoint = apiUrl + "forms/INVALID.json?apikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getString("errorMessage"),
            apiRestResponse.getBody().getString("errorMessage"));

   }

   /**
    * Positive test case for getFields method with mandatory parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "contactform {getFields} integration test with mandatory parameters.")
   public void testGetFileldsWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getFields");

      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getFields_mandatory.json");

      final String apiEndPoint = apiUrl + "forms/" + connectorProperties.getProperty("formId") + "/fields.json" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("fields").getJSONObject(0).getString("fieldId"),
            apiRestResponse.getBody().getJSONArray("fields").getJSONObject(0).getString("fieldId"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("fields").getJSONObject(0).getString("fieldTitle"),
            apiRestResponse.getBody().getJSONArray("fields").getJSONObject(0).getString("fieldTitle"));

   }

   /**
    * Method Name: getFields 
    * Skipped Case: optional case 
    * Reason: No optional parameter(s) to assert.
    */

   /**
    * Negative test case for getFields method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "contactform {getFields} integration test with negative case.")
   public void testGetFieldsWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getFields");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getFields_negative.json");

      final String apiEndpoint = apiUrl + "forms/INVALID/fields.json?apikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getString("errorMessage"),
            apiRestResponse.getBody().getString("errorMessage"));

   }

   /**
    * Positive test case for getFormSubmissionCount method with mandatory
    * parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "contactform {getFormSubmissionCount} integration test with mandatory parameters.")
   public void testGetFormSubmissionCountWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getFormSubmissionCount");

      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getFormSubmissionCount_mandatory.json");

      final String apiEndPoint = apiUrl + "forms/" + connectorProperties.getProperty("formId") + "/submissions/count.json"
            + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("submissionsCount"),
            apiRestResponse.getBody().getString("submissionsCount"));

   }

   /**
    * Method Name: getFormSubmissionCount 
    * Skipped Case: optional case 
    * Reason: No optional parameter(s) to assert.
    */

   /**
    * Negative test case for getFormSubmissionCount method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "contactform {getFormSubmissionCount} integration test with negative case.")
   public void testGetFormSubmissionCountWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getFormSubmissionCount");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getFormSubmissionCount_negative.json");

      final String apiEndpoint = apiUrl + "forms/INVALID/submissions/count.json?apikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

      Assert.assertEquals(esbRestResponse.getBody().getString("errorMessage"),
            apiRestResponse.getBody().getString("errorMessage"));

   }

   /**
    * Positive test case for getFormSubmissions method with mandatory
    * parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "contactform {getFormSubmissions} integration test with mandatory parameters.")
   public void testGetFormSubmissionsWithMandatoryParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getFormSubmissions");

      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getFormSubmissions_mandatory.json");

      final String apiEndPoint = apiUrl + "forms/" + connectorProperties.getProperty("formId") + "/submissions.json" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("submissions").getJSONObject(0).getJSONArray("fields")
            .getJSONObject(0).getString("fieldvalue"), apiRestResponse.getBody().getJSONArray("submissions")
            .getJSONObject(0).getJSONArray("fields").getJSONObject(0).getString("fieldvalue"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("submissions").getJSONObject(0).getJSONArray("fields")
            .getJSONObject(0).getString("fieldid"), apiRestResponse.getBody().getJSONArray("submissions")
            .getJSONObject(0).getJSONArray("fields").getJSONObject(0).getString("fieldid"));

   }

   /**
    * Positive test case for getFormSubmissions method with optional parameters.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "contactform {getFormSubmissions} integration test with optional parameters.")
   public void testGetFormSubmissionsWithOptionalParameters() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getFormSubmissions");

      final String pageNumber = "1";
      final String pageSize = "26";
      final String sortValue = "ASC";
      parametersMap.put("pageSize", pageSize);
      parametersMap.put("pageNumber", pageNumber);
      parametersMap.put("sortValue", sortValue);
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getFormSubmissions_optional.json", parametersMap);

      final String apiEndPoint = apiUrl + "forms/" + connectorProperties.getProperty("formId") + "/submissions.json" + apiKey;

      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
            "api_getFormSubmissions_optional.json", parametersMap);
      
      final int resultLength = esbRestResponse.getBody().getJSONArray("submissions").length();
      //Check whether the API call is working according to the pageSize optional parameter. 
      if (resultLength > Integer.parseInt(pageSize)) {
          Assert.fail("Length is less than " + (Integer.parseInt(pageSize) + 1));
      }
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("submissions").getJSONObject(0).getJSONArray("fields")
            .getJSONObject(0).getString("fieldvalue"), apiRestResponse.getBody().getJSONArray("submissions")
            .getJSONObject(0).getJSONArray("fields").getJSONObject(0).getString("fieldvalue"));

   }

   /**
    * Negative test case for getFormSubmissions method.
    * 
    * @throws JSONException
    * @throws IOException
    */
   @Test(groups = { "wso2.esb" }, description = "contactform {getFormSubmissions} integration test with negative case.")
   public void testGetFormSubmissionsWithNegativeCase() throws IOException, JSONException {

      esbRequestHeadersMap.put("Action", "urn:getFormSubmissions");
      RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
            "esb_getFormSubmissions_negative.json");

      final String apiEndpoint = apiUrl + "forms/INVALID/submissions.json?apikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("errorMessage"),
            apiRestResponse.getBody().getString("errorMessage"));

   }
}
