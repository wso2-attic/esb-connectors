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

package org.wso2.carbon.connector.integration.test.pagerduty;

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

public class PagerdutyConnectorIntegrationTest extends ConnectorIntegrationTestBase {
   
   private Map<String, String> esbRequestHeadersMap;
   
   private Map<String, String> apiRequestHeadersMap;
   
   private String apiUrl;
   
   /**
    * Set up the environment.
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {
   
      init("pagerduty-connector-1.0.0");
      
      esbRequestHeadersMap = new HashMap<String, String>();
      apiRequestHeadersMap = new HashMap<String, String>();
      
      esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
      esbRequestHeadersMap.put("Content-Type", "application/json");
      
      apiRequestHeadersMap.putAll(esbRequestHeadersMap);
      
      apiRequestHeadersMap.put("Authorization", "Token token=" + connectorProperties.getProperty("apiToken"));
      
      apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v1";
   }
   
   /**
    * Positive test case for createUser method with mandatory parameters.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "pagerduty {createUser} integration test with mandatory parameters.")
   public void testCreateUserWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createUser");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createUser_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      final String userId = esbRestResponse.getBody().getJSONObject("user").getString("id");
      connectorProperties.setProperty("userIdMandatory", userId);
      
      final String apiEndPoint = apiUrl + "/users/" + userId;
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("user").getString("name"),
            connectorProperties.getProperty("userName"));
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("user").getString("email"),
            connectorProperties.getProperty("userEmail"));
   }
   
   /**
    * Positive test case for createUser method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateUserWithMandatoryParameters" }, description = "pagerduty {createUser} integration test with optional parameters.")
   public void testCreateUserWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createUser");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createUser_optional.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      final String userId = esbRestResponse.getBody().getJSONObject("user").getString("id");
      connectorProperties.setProperty("userIdOptional", userId);
      
      final String apiEndPoint = apiUrl + "/users/" + userId;
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("user").getString("name"),
            connectorProperties.getProperty("userNameOpt"));
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("user").getString("email"),
            connectorProperties.getProperty("userEmailOpt"));
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("user").getString("role"),
            connectorProperties.getProperty("userRole"));
   }
   
   /**
    * Negative test case for createUser method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateUserWithOptionalParameters" }, description = "pagerduty {createUser} integration test with negative case.")
   public void testCreateUserWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createUser");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createUser_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      
      final String apiEndPoint = apiUrl + "/users";
      final RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createUser_negative.json");
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getString(0),
            apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getString(0));
   }
   
   /**
    * Positive test case for getUserById method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateUserWithNegativeCase" }, description = "pagerduty {getUserById} integration test with mandatory parameters.")
   public void testGetUserByIdWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getUserById");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserById_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      final String apiEndPoint = apiUrl + "/users/" + connectorProperties.getProperty("userIdMandatory");
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").getString("name"), apiRestResponse.getBody()
            .getJSONObject("user").getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").getString("email"), apiRestResponse.getBody()
            .getJSONObject("user").getString("email"));
   }
   
   /**
    * Positive test case for getUserById method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetUserByIdWithMandatoryParameters" }, description = "pagerduty {getUserById} integration test with optional parameters.")
   public void testGetUserByIdWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getUserById");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserById_optional.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      final String apiEndPoint =
            apiUrl + "/users/" + connectorProperties.getProperty("userIdMandatory") + "?include[]=contact_methods";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      final JSONObject esbUser = esbRestResponse.getBody().getJSONObject("user");
      final JSONObject apiUser = apiRestResponse.getBody().getJSONObject("user");
      
      Assert.assertEquals(esbUser.getString("name"), apiUser.getString("name"));
      Assert.assertEquals(esbUser.getJSONArray("contact_methods").getJSONObject(0).getString("address"), apiUser
            .getJSONArray("contact_methods").getJSONObject(0).getString("address"));
      Assert.assertEquals(esbUser.getJSONArray("contact_methods").getJSONObject(0).getString("type"), apiUser
            .getJSONArray("contact_methods").getJSONObject(0).getString("type"));
   }
   
   /**
    * Negative test case for getUserById method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetUserByIdWithOptionalParameters" }, description = "pagerduty {getUserById} integration test with negative case.")
   public void testGetUserByIdWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getUserById");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserById_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      
      final String apiEndPoint = apiUrl + "/users/Invalid";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("code"), apiRestResponse.getBody()
            .getJSONObject("error").getString("code"));
   }
   
   /**
    * Positive test case for listUsers method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetUserByIdWithNegativeCase" }, description = "pagerduty {listUsers} integration test with mandatory parameters.")
   public void testListUsersWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listUsers");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      final String apiEndPoint = apiUrl + "/users";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getInt("total"), apiRestResponse.getBody().getInt("total"));
      
      final JSONObject esbUser = esbRestResponse.getBody().getJSONArray("users").getJSONObject(0);
      final JSONObject apiUser = apiRestResponse.getBody().getJSONArray("users").getJSONObject(0);
      
      Assert.assertEquals(esbUser.getString("name"), apiUser.getString("name"));
      Assert.assertEquals(esbUser.getString("email"), apiUser.getString("email"));
   }
   
   /**
    * Positive test case for listUsers method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListUsersWithMandatoryParameters" }, description = "pagerduty {listUsers} integration test with optional parameters.")
   public void testListUsersWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listUsers");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_optional.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      final String apiEndPoint = apiUrl + "/users?include[]=contact_methods&include[]=notification_rules";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getInt("total"), apiRestResponse.getBody().getInt("total"));
      
      final JSONObject esbUser = esbRestResponse.getBody().getJSONArray("users").getJSONObject(0);
      final JSONObject apiUser = apiRestResponse.getBody().getJSONArray("users").getJSONObject(0);
      
      Assert.assertEquals(esbUser.getString("name"), apiUser.getString("name"));
      Assert.assertEquals(esbUser.getString("email"), apiUser.getString("email"));
   }
   
   /**
    * Negative test case for listUsers method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListUsersWithOptionalParameters" }, description = "pagerduty {listUsers} integration test with negative case.")
   public void testListUsersWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listUsers");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      
      final String apiEndPoint = apiUrl + "/users?limit=Invalid";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONObject("errors")
            .getJSONArray("limit").getString(0),
            apiRestResponse.getBody().getJSONObject("error").getJSONObject("errors").getJSONArray("limit").getString(0));
   }
   
   /**
    * Positive test case for createContactMethod method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListUsersWithNegativeCase" }, description = "pagerduty {createContactMethod} integration test with mandatory parameters.")
   public void testCreateContactMethodWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createContactMethod");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContactMethod_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      
      final String apiEndPoint =
            apiUrl + "/users/" + connectorProperties.getProperty("userIdMandatory") + "/contact_methods";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      
      final JSONArray contactMethods = apiRestResponse.getBody().getJSONArray("contact_methods");
      
      JSONObject currentObject = null;
      for (int i = 0; i < contactMethods.length(); i++) {
         currentObject = contactMethods.getJSONObject(i);
         if ("phone".equals(currentObject.getString("type"))) {
            break;
         }
      }
      
      Assert.assertNotNull(currentObject);
      Assert.assertEquals(currentObject.getString("address"), connectorProperties.getProperty("contactPhoneNumber"));
   }
   
   /**
    * Positive test case for createContactMethod method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactMethodWithMandatoryParameters" }, description = "pagerduty {createContactMethod} integration test with optional parameters.")
   public void testCreateContactMethodWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createContactMethod");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContactMethod_optional.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      
      final String apiEndPoint =
            apiUrl + "/users/" + connectorProperties.getProperty("userIdOptional") + "/contact_methods";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      
      final JSONArray contactMethods = apiRestResponse.getBody().getJSONArray("contact_methods");
      
      JSONObject currentObject = null;
      for (int i = 0; i < contactMethods.length(); i++) {
         currentObject = contactMethods.getJSONObject(i);
         if (connectorProperties.getProperty("contactMethodLabel").equals(currentObject.getString("label"))) {
            break;
         }
      }
      
      Assert.assertNotNull(currentObject);
      Assert.assertEquals(currentObject.getString("address"), connectorProperties.getProperty("userEmail"));
   }
   
   /**
    * Negative test case for createContactMethod method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactMethodWithOptionalParameters" }, description = "pagerduty {createContactMethod} integration test with negative case.")
   public void testCreateContactMethodWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createContactMethod");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContactMethod_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      
      final String apiEndPoint =
            apiUrl + "/users/" + connectorProperties.getProperty("userIdMandatory") + "/contact_methods";
      final RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createContactMethod_negative.json");
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getString(0),
            apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getString(0));
   }
   
   /**
    * Positive test case for listContactMethods method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactMethodWithNegativeCase" }, description = "pagerduty {listContactMethods} integration test with mandatory parameters.")
   public void testListContactMethodsWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listContactMethods");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContactMethods_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      final String apiEndPoint =
            apiUrl + "/users/" + connectorProperties.getProperty("userIdMandatory") + "/contact_methods";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getInt("total"), apiRestResponse.getBody().getInt("total"));
      
      final JSONObject esbContactMethod = esbRestResponse.getBody().getJSONArray("contact_methods").getJSONObject(0);
      final JSONObject apiContactMethod = apiRestResponse.getBody().getJSONArray("contact_methods").getJSONObject(0);
      
      Assert.assertEquals(esbContactMethod.getString("label"), apiContactMethod.getString("label"));
      Assert.assertEquals(esbContactMethod.getString("address"), apiContactMethod.getString("address"));
   }
   
   /**
    * Negative test case for listContactMethods method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListContactMethodsWithMandatoryParameters" }, description = "pagerduty {listContactMethods} integration test with negative case.")
   public void testListContactMethodsWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listContactMethods");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContactMethods_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      
      final String apiEndPoint = apiUrl + "/users/invalid/contact_methods";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getString(0),
            apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getString(0));
   }
   
   /**
    * Positive test case for createIncident method with mandatory parameters.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "pagerduty {createIncident} integration test with mandatory parameters.")
   public void testCreateIncidentWithMandatoryParameters() throws IOException, JSONException, NumberFormatException,
         InterruptedException {
   
      esbRequestHeadersMap.put("Action", "urn:createIncident");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createIncident_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
      
      final String apiEndPoint = apiUrl + "/incidents?incident_key=" + connectorProperties.getProperty("incidentKey");
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getInt("total"), 1);
      
      connectorProperties.setProperty("incidentIdMandatory", apiRestResponse.getBody().getJSONArray("incidents")
            .getJSONObject(0).getString("id"));
      
      Assert.assertEquals(
            apiRestResponse.getBody().getJSONArray("incidents").getJSONObject(0).getJSONObject("trigger_summary_data")
                  .getString("description"), connectorProperties.getProperty("description"));
   }
   
   /**
    * Positive test case for createIncident method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateIncidentWithMandatoryParameters" }, description = "pagerduty {createIncident} integration test with optional parameters.")
   public void testCreateIncidentWithOptionalParameters() throws IOException, JSONException, NumberFormatException,
         InterruptedException {
   
      esbRequestHeadersMap.put("Action", "urn:createIncident");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createIncident_optional.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
      
      final String apiEndPoint =
            apiUrl + "/incidents?incident_key=" + connectorProperties.getProperty("incidentKeyOpt");
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getInt("total"), 1);
      
      connectorProperties.setProperty("incidentIdOptional", apiRestResponse.getBody().getJSONArray("incidents")
            .getJSONObject(0).getString("id"));
      
      Assert.assertEquals(
            apiRestResponse.getBody().getJSONArray("incidents").getJSONObject(0).getJSONObject("trigger_summary_data")
                  .getString("description"), connectorProperties.getProperty("description"));
      Assert.assertEquals(
            apiRestResponse.getBody().getJSONArray("incidents").getJSONObject(0).getJSONObject("trigger_summary_data")
                  .getString("client"), connectorProperties.getProperty("userName"));
   }
   
   /**
    * Negative test case for createIncident method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateIncidentWithOptionalParameters" }, description = "pagerduty {createIncident} integration test with negative case.")
   public void testCreateIncidentWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createIncident");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createIncident_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      
      // API end-point of create incident method is a fixed value which differs from others.
      final String apiEndPoint = "https://events.pagerduty.com/generic/2010-04-15/create_event.json";
      final RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createIncident_negative.json");
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").getString(0), apiRestResponse.getBody()
            .getJSONArray("errors").getString(0));
   }
   
   /**
    * Positive test case for getIncidentById method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateIncidentWithNegativeCase" }, description = "pagerduty {getIncidentById} integration test with mandatory parameters.")
   public void testGetIncidentByIdWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getIncidentById");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getIncidentById_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      final String apiEndPoint = apiUrl + "/incidents/" + connectorProperties.getProperty("incidentIdMandatory");
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("trigger_details_html_url"), apiRestResponse.getBody()
            .getString("trigger_details_html_url"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("trigger_summary_data").getString("description"),
            apiRestResponse.getBody().getJSONObject("trigger_summary_data").getString("description"));
   }
   
   /**
    * Negative test case for getIncidentById method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetIncidentByIdWithMandatoryParameters" }, description = "pagerduty {getIncidentById} integration test with negative case.")
   public void testGetIncidentByIdWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getIncidentById");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getIncidentById_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      
      final String apiEndPoint = apiUrl + "/incidents/Invalid";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("code"), apiRestResponse.getBody()
            .getJSONObject("error").getString("code"));
   }
   
   /**
    * Positive test case for listIncidents method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetIncidentByIdWithNegativeCase" }, description = "pagerduty {listIncidents} integration test with mandatory parameters.")
   public void testListIncidentsWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listIncidents");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listIncidents_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      final String apiEndPoint = apiUrl + "/incidents";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getInt("total"), apiRestResponse.getBody().getInt("total"));
      
      final JSONObject esbUser = esbRestResponse.getBody().getJSONArray("incidents").getJSONObject(0);
      final JSONObject apiUser = apiRestResponse.getBody().getJSONArray("incidents").getJSONObject(0);
      
      Assert.assertEquals(esbUser.getString("incident_number"), apiUser.getString("incident_number"));
      Assert.assertEquals(esbUser.getString("trigger_details_html_url"), apiUser.getString("trigger_details_html_url"));
   }
   
   /**
    * Positive test case for listIncidents method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListIncidentsWithMandatoryParameters" }, description = "pagerduty {listIncidents} integration test with optional parameters.")
   public void testListIncidentsWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listIncidents");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listIncidents_optional.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      final String apiEndPoint = apiUrl + "/incidents?date_range=all&sort_by=created_on:desc&limit=5&offset=0";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getInt("total"), apiRestResponse.getBody().getInt("total"));
      
      final JSONObject esbUser = esbRestResponse.getBody().getJSONArray("incidents").getJSONObject(0);
      final JSONObject apiUser = apiRestResponse.getBody().getJSONArray("incidents").getJSONObject(0);
      
      Assert.assertEquals(esbUser.getString("incident_number"), apiUser.getString("incident_number"));
      Assert.assertEquals(esbUser.getString("trigger_details_html_url"), apiUser.getString("trigger_details_html_url"));
   }
   
   /**
    * Negative test case for listIncidents method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListIncidentsWithOptionalParameters" }, description = "pagerduty {listIncidents} integration test with negative case.")
   public void testListIncidentsWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listIncidents");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listIncidents_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      
      final String apiEndPoint = apiUrl + "/incidents?limit=invalid";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONObject("errors")
            .getJSONArray("limit").getString(0),
            apiRestResponse.getBody().getJSONObject("error").getJSONObject("errors").getJSONArray("limit").getString(0));
   }
   
   /**
    * Positive test case for createNote method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListIncidentsWithNegativeCase" }, description = "pagerduty {createNote} integration test with mandatory parameters.")
   public void testCreateNoteWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createNote");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNote_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      
      final String apiEndPoint =
            apiUrl + "/incidents/" + connectorProperties.getProperty("incidentIdMandatory") + "/notes";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      
      final JSONObject apiNote = apiRestResponse.getBody().getJSONArray("notes").getJSONObject(0);
      
      Assert.assertEquals(apiNote.getString("content"), connectorProperties.getProperty("incidentNote"));
      Assert.assertEquals(apiNote.getJSONObject("user").getString("id"), connectorProperties.getProperty("requesterId"));
   }
   
   /**
    * Negative test case for createNote method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateNoteWithMandatoryParameters" }, description = "pagerduty {createNote} integration test with negative case.")
   public void testCreateNoteWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createNote");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNote_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      
      final String apiEndPoint =
            apiUrl + "/incidents/" + connectorProperties.getProperty("incidentIdMandatory") + "/notes";
      final RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createNote_negative.json");
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getString(0),
            apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getString(0));
   }
   
   /**
    * Positive test case for listNotes method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateNoteWithNegativeCase" }, description = "pagerduty {listNotes} integration test with mandatory parameters.")
   public void testListNotesWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listNotes");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listNotes_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      final String apiEndPoint =
            apiUrl + "/incidents/" + connectorProperties.getProperty("incidentIdMandatory") + "/notes";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      
      final JSONArray esbNotes = esbRestResponse.getBody().getJSONArray("notes");
      final JSONArray apiNotes = apiRestResponse.getBody().getJSONArray("notes");
      
      Assert.assertEquals(esbNotes.length(), apiNotes.length());
      Assert.assertEquals(esbNotes.getJSONObject(0).getString("id"), apiNotes.getJSONObject(0).getString("id"));
      Assert.assertEquals(esbNotes.getJSONObject(0).getString("content"), apiNotes.getJSONObject(0)
            .getString("content"));
   }
   
   /**
    * Negative test case for listNotes method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListNotesWithMandatoryParameters" }, description = "pagerduty {listNotes} integration test with negative case.")
   public void testListNotesWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listNotes");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listNotes_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      
      final String apiEndPoint = apiUrl + "/incidents/Invalid/notes";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("code"), apiRestResponse.getBody()
            .getJSONObject("error").getString("code"));
   }
   
   /**
    * Positive test case for reassignIncident method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateIncidentWithMandatoryParameters",
         "testCreateUserWithMandatoryParameters" }, description = "pagerduty {reassignIncident} integration test with mandatory parameters.")
   public void testReassignIncidentWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:reassignIncident");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_reassignIncident_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      final String apiEndPoint = apiUrl + "/incidents/" + connectorProperties.getProperty("incidentIdMandatory");
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      // Only the assigned user is changed in the response.
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("assigned_to_user").getString("id"),
            connectorProperties.getProperty("userIdMandatory"));
   }
   
   /**
    * Positive test case for reassignIncident method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testReassignIncidentWithMandatoryParameters" }, description = "pagerduty {reassignIncident} integration test with optional parameters.")
   public void testReassignIncidentWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:reassignIncident");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_reassignIncident_optional.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      final String apiEndPoint = apiUrl + "/incidents/" + connectorProperties.getProperty("incidentIdOptional");
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("escalation_policy").getString("id"),
            connectorProperties.getProperty("escalationPolicyId"));
   }
   
   /**
    * Negative test case for reassignIncident method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testReassignIncidentWithOptionalParameters" }, description = "pagerduty {reassignIncident} integration test with negative case.")
   public void testReassignIncidentWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:reassignIncident");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_reassignIncident_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      
      final String apiEndPoint =
            apiUrl + "/incidents/" + connectorProperties.getProperty("incidentIdMandatory")
                  + "/reassign?requester_id=Invalid" + "&assigned_to_user="
                  + connectorProperties.getProperty("userIdMandatory");
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("code"), apiRestResponse.getBody()
            .getJSONObject("error").getString("code"));
   }
   
   /**
    * Positive test case for resolveIncident method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testReassignIncidentWithNegativeCase" }, description = "pagerduty {resolveIncident} integration test with mandatory parameters.")
   public void testResolveIncidentWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:resolveIncident");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_resolveIncident_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      final String apiEndPoint = apiUrl + "/incidents/" + connectorProperties.getProperty("incidentIdOptional");
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      
      // Can only assert for the change of the incident status to 'resolved'.
      Assert.assertEquals(apiRestResponse.getBody().getString("status"), "resolved");
   }
   
   /**
    * Negative test case for resolveIncident method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testResolveIncidentWithMandatoryParameters" }, description = "pagerduty {resolveIncident} integration test with negative case.")
   public void testResolveIncidentWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:resolveIncident");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_resolveIncident_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      
      final String apiEndPoint =
            apiUrl + "/incidents/" + connectorProperties.getProperty("incidentIdOptional") + "/resolve?requester_id="
                  + connectorProperties.getProperty("requesterId");
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
            .getBody().getJSONObject("error").getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("code"), apiRestResponse.getBody()
            .getJSONObject("error").getString("code"));
   }
   
}
