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

package org.wso2.carbon.connector.integration.test.planyo;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

public class PlanyoConnectorIntegrationTest extends ConnectorIntegrationTestBase {
   
   private Map<String, String> esbRequestHeadersMap;
   
   private Map<String, String> apiRequestHeadersMap;
   
   private String apiUrl;
   
   private String currentTimeString;
   
   private DateFormat dateFormat;
   
   private Calendar calendar;
   
   /**
    * Set up the environment.
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {
   
      init("planyo-connector-1.0.0");
      
      esbRequestHeadersMap = new HashMap<String, String>();
      apiRequestHeadersMap = new HashMap<String, String>();
      esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
      esbRequestHeadersMap.put("Content-Type", "application/json");
      apiRequestHeadersMap.putAll(esbRequestHeadersMap);
      apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");
      
      // Saves the future days to be used as reservation dates
      dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      calendar = Calendar.getInstance();
      calendar.setTime(new Date());
      calendar.add(Calendar.DATE, 1);
      connectorProperties.setProperty("reservationStartTimeMandatory", dateFormat.format(calendar.getTime()));
      calendar.add(Calendar.DATE, 1);
      connectorProperties.setProperty("reservationEndTimeMandatory", dateFormat.format(calendar.getTime()));
      calendar.add(Calendar.DATE, 1);
      connectorProperties.setProperty("reservationStartTimeOptional", dateFormat.format(calendar.getTime()));
      calendar.add(Calendar.DATE, 1);
      connectorProperties.setProperty("reservationEndTimeOptional", dateFormat.format(calendar.getTime()));
      
      currentTimeString = String.valueOf(System.currentTimeMillis());
      apiUrl = connectorProperties.getProperty("apiUrl") + "/rest/";
   }
   
   /**
    * Positive test case for createResource method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "planyo {createResource} integration test with mandatory parameters.")
   public void testCreateResourceWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createResource");
      connectorProperties.setProperty("esbResourceNameMandatory", "esbResName_" + currentTimeString);
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createResource_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getInt("response_code"), 0);
      int resourceIdMandatory = esbRestResponse.getBody().getJSONObject("data").getInt("new_resource_id");
      connectorProperties.setProperty("resourceId", String.valueOf(resourceIdMandatory));
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey") + "&method=get_resource_info&resource_id="
                  + resourceIdMandatory;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      String apiResourceName = apiRestResponse.getBody().getJSONObject("data").getString("name");
      Assert.assertEquals(connectorProperties.getProperty("esbResourceNameMandatory"), apiResourceName);
   }
   
   /**
    * Positive test case for createResource method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateResourceWithMandatoryParameters" }, description = "planyo {createResource} integration test with optional parameters.")
   public void testCreateResourceWithptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createResource");
      connectorProperties.setProperty("esbResourceNameOptional", "esbResNameOpt_" + currentTimeString);
      connectorProperties.setProperty("esbResourceQuantity", "158");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createResource_optional.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getInt("response_code"), 0);
      int resourceIdOptional = esbRestResponse.getBody().getJSONObject("data").getInt("new_resource_id");
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey") + "&method=get_resource_info&resource_id="
                  + resourceIdOptional;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      String apiResourceName = apiRestResponse.getBody().getJSONObject("data").getString("name");
      String apiResourceQuantity = apiRestResponse.getBody().getJSONObject("data").getString("quantity");
      Assert.assertEquals(connectorProperties.getProperty("esbResourceNameOptional"), apiResourceName);
      Assert.assertEquals(connectorProperties.getProperty("esbResourceQuantity"), apiResourceQuantity);
   }
   
   /**
    * Negative test case for createResource method, Tries to create a resource with an existing resource name.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateResourceWithptionalParameters" }, description = "planyo {createResource} integration test negative case.")
   public void testCreateResourceNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createResource");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createResource_negative.json");
      
      int esbResponseCode = esbRestResponse.getBody().getInt("response_code");
      Assert.assertEquals(esbResponseCode, 3);
      String esbResponseMessage = esbRestResponse.getBody().getString("response_message");
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiUrl, "POST", apiRequestHeadersMap, "api_createResource_negative.json");
      
      int apiResponseCode = apiRestResponse.getBody().getInt("response_code");
      Assert.assertEquals(esbResponseCode, apiResponseCode);
      String apiResponseMessage = apiRestResponse.getBody().getString("response_message");
      Assert.assertEquals(esbResponseMessage, apiResponseMessage);
   }
   
   /**
    * Positive test case for createReservation method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateResourceNegativeCase" }, description = "planyo {createReservation} integration test with mandatory parameters.")
   public void testCreateReservationWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createReservation");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createReservation_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getInt("response_code"), 0);
      int reservationIdMandatory = esbRestResponse.getBody().getJSONObject("data").getInt("reservation_id");
      connectorProperties.setProperty("reservationId", String.valueOf(reservationIdMandatory));
      String esbReservationStatus = esbRestResponse.getBody().getJSONObject("data").getString("status");
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey")
                  + "&method=get_reservation_data&reservation_id=" + reservationIdMandatory;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      String apiResourceId = apiRestResponse.getBody().getJSONObject("data").getString("resource_id");
      String apiReservationStatus = apiRestResponse.getBody().getJSONObject("data").getString("status");
      
      Assert.assertEquals(esbReservationStatus, apiReservationStatus);
      Assert.assertEquals(connectorProperties.getProperty("resourceId"), apiResourceId);
   }
   
   /**
    * Positive test case for createReservation method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateReservationWithMandatoryParameters" }, description = "planyo {createReservation} integration test with optional parameters.")
   public void testCreateReservationWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createReservation");
      connectorProperties.setProperty("userNotes", "reservationUserNote");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createReservation_optional.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getInt("response_code"), 0);
      int reservationIdOptional = esbRestResponse.getBody().getJSONObject("data").getInt("reservation_id");
      String esbReservationStatus = esbRestResponse.getBody().getJSONObject("data").getString("status");
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey")
                  + "&method=get_reservation_data&reservation_id=" + reservationIdOptional;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      String apiResourceId = apiRestResponse.getBody().getJSONObject("data").getString("resource_id");
      String apiReservationStatus = apiRestResponse.getBody().getJSONObject("data").getString("status");
      String apiReservationUserNote = apiRestResponse.getBody().getJSONObject("data").getString("user_notes");
      
      Assert.assertEquals(esbReservationStatus, apiReservationStatus);
      Assert.assertEquals(connectorProperties.getProperty("resourceId"), apiResourceId);
      Assert.assertEquals(connectorProperties.getProperty("userNotes"), apiReservationUserNote);
   }
   
   /**
    * Negative test case for createReservation method, Provides same start and end date.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateReservationWithOptionalParameters" }, description = "planyo {createReservation} integration test negative case.")
   public void testCreateReservationNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createReservation");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createReservation_negative.json");
      
      int esbResponseCode = esbRestResponse.getBody().getInt("response_code");
      Assert.assertEquals(esbResponseCode, 4);
      String esbResponseMessage = esbRestResponse.getBody().getString("response_message");
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiUrl, "POST", apiRequestHeadersMap, "api_createReservation_negative.json");
      
      int apiResponseCode = apiRestResponse.getBody().getInt("response_code");
      Assert.assertEquals(esbResponseCode, apiResponseCode);
      String apiResponseMessage = apiRestResponse.getBody().getString("response_message");
      Assert.assertEquals(esbResponseMessage, apiResponseMessage);
   }
   
   /**
    * Positive test case for getInvoiceItemsById method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateReservationNegativeCase" }, description = "planyo {getInvoiceItemsById} integration test with mandatory parameters.")
   public void testGetInvoiceItemsByIdWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getInvoiceItemsById");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getInvoiceItemsById_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getInt("response_code"), 0);
      JSONArray esbItemsArray = esbRestResponse.getBody().getJSONObject("data").getJSONArray("results");
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey")
                  + "&method=get_invoice_items&reservation_id=" + connectorProperties.getProperty("reservationId");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getInt("response_code"), 0);
      JSONArray apiItemsArray = apiRestResponse.getBody().getJSONObject("data").getJSONArray("results");
      
      if (esbItemsArray == null || apiItemsArray == null) {
         Assert.assertFalse(true);
      }
      Assert.assertEquals(esbItemsArray.length(), apiItemsArray.length());
      Assert.assertEquals(esbItemsArray.getJSONObject(0).getString("name"),
            apiItemsArray.getJSONObject(0).getString("name"));
      Assert.assertEquals(esbItemsArray.getJSONObject(0).getString("reservation_id"), apiItemsArray.getJSONObject(0)
            .getString("reservation_id"));
   }
   
   /**
    * Negative test case for getInvoiceItemsById method, provides an invalid reservation id.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetInvoiceItemsByIdWithMandatoryParameters" }, description = "planyo {getInvoiceItemsById} integration test with negative case.")
   public void testGetInvoiceItemsByIdNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getInvoiceItemsById");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getInvoiceItemsById_negative.json");
      
      int esbResponseCode = esbRestResponse.getBody().getInt("response_code");
      Assert.assertEquals(esbResponseCode, 3);
      String esbResponseMessage = esbRestResponse.getBody().getString("response_message");
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey")
                  + "&method=get_invoice_items&reservation_id=--INVALID--";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      int apiResponseCode = apiRestResponse.getBody().getInt("response_code");
      Assert.assertEquals(apiResponseCode, 3);
      String apiResponseMessage = apiRestResponse.getBody().getString("response_message");
      
      Assert.assertEquals(esbResponseMessage, apiResponseMessage);
   }
   
   /**
    * Positive test case for listResources method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetInvoiceItemsByIdNegativeCase" }, description = "planyo {listResources} integration test with mandatory parameters.")
   public void testListResourcesWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listResources");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listResources_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getInt("response_code"), 0);
      JSONObject esbResourcesObject = esbRestResponse.getBody().getJSONObject("data").getJSONObject("resources");
      
      String resourceId = esbResourcesObject.keys().next().toString();
      
      String apiEndPoint = apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey") + "&method=list_resources";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getInt("response_code"), 0);
      JSONObject apiResourcesObject = apiRestResponse.getBody().getJSONObject("data").getJSONObject("resources");
      
      if (esbResourcesObject == null || apiResourcesObject == null) {
         Assert.assertFalse(true);
      }
      
      Assert.assertEquals(resourceId, apiResourcesObject.keys().next().toString());
      Assert.assertEquals(esbResourcesObject.length(), apiResourcesObject.length());
      Assert.assertEquals(esbResourcesObject.getJSONObject(resourceId).getString("name"), apiResourcesObject
            .getJSONObject(resourceId).getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getString("response_message"),
            apiRestResponse.getBody().getString("response_message"));
   }
   
   /**
    * Positive test case for listResources method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListResourcesWithMandatoryParameters" }, description = "planyo {listResources} integration test with optional parameters.")
   public void testListResourcesWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listResources");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listResources_optional.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getInt("response_code"), 0);
      JSONObject esbResourcesObject = esbRestResponse.getBody().getJSONObject("data").getJSONObject("resources");
      
      String resourceId = esbResourcesObject.keys().next().toString();
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey")
                  + "&method=list_resources&detail_level=4&res_filter_name="
                  + connectorProperties.getProperty("resourcePropertyName") + "&res_filter_value="
                  + connectorProperties.getProperty("resourcePropertyValue");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getInt("response_code"), 0);
      JSONObject apiResourcesObject = apiRestResponse.getBody().getJSONObject("data").getJSONObject("resources");
      
      if (esbResourcesObject == null || apiResourcesObject == null) {
         Assert.assertFalse(true);
      }
      
      String esbPropertyValue =
            esbResourcesObject.getJSONObject(resourceId).getJSONObject("properties")
                  .getString(connectorProperties.getProperty("resourcePropertyName"));
      String apiPropertyValue =
            apiResourcesObject.getJSONObject(resourceId).getJSONObject("properties")
                  .getString(connectorProperties.getProperty("resourcePropertyName"));
      
      Assert.assertEquals(resourceId, apiResourcesObject.keys().next().toString());
      Assert.assertEquals(esbResourcesObject.length(), apiResourcesObject.length());
      Assert.assertEquals(esbPropertyValue, apiPropertyValue);
      Assert.assertEquals(esbRestResponse.getBody().getString("response_message"),
            apiRestResponse.getBody().getString("response_message"));
   }
   
   /**
    * Positive test case for listUsers method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListResourcesWithOptionalParameters" }, description = "planyo {listUsers} integration test with mandatory parameters.")
   public void testListUsersWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listUsers");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_mandatory.json");
      
      String userId =
            esbRestResponse.getBody().getJSONObject("data").getJSONArray("users").getJSONObject(0).getString("id");
      
      connectorProperties.put("userId", userId);
      
      String apiEndPoint = apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey") + "&method=list_users";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      int esbUserCount = esbRestResponse.getBody().getJSONObject("data").getJSONArray("users").length();
      int apiUserCount = apiRestResponse.getBody().getJSONObject("data").getJSONArray("users").length();
      
      Assert.assertEquals(esbUserCount, apiUserCount);
      
      String esbEmail =
            esbRestResponse.getBody().getJSONObject("data").getJSONArray("users").getJSONObject(0).getString("email");
      String apiEmail =
            apiRestResponse.getBody().getJSONObject("data").getJSONArray("users").getJSONObject(0).getString("email");
      
      Assert.assertEquals(esbEmail, apiEmail);
      
      String esbRegistrationTime =
            esbRestResponse.getBody().getJSONObject("data").getJSONArray("users").getJSONObject(0)
                  .getString("registration_time");
      String apiRegistrationTime =
            apiRestResponse.getBody().getJSONObject("data").getJSONArray("users").getJSONObject(0)
                  .getString("registration_time");
      
      Assert.assertEquals(esbRegistrationTime, apiRegistrationTime);
      
   }
   
   /**
    * Positive test case for listUsers method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListUsersWithMandatoryParameters" }, description = "planyo {listUsers} integration test with optional parameters.")
   public void testListUsersWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listUsers");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_optional.json");
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey") + "&method=list_users&email="
                  + connectorProperties.getProperty("email") + "&detail_level=4&page_size=1";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      int esbUserCount = esbRestResponse.getBody().getJSONObject("data").getJSONArray("users").length();
      int apiUserCount = apiRestResponse.getBody().getJSONObject("data").getJSONArray("users").length();
      
      Assert.assertEquals(esbUserCount, apiUserCount);
      
      String esbUserId =
            esbRestResponse.getBody().getJSONObject("data").getJSONArray("users").getJSONObject(0).getString("id");
      String apiUserId =
            apiRestResponse.getBody().getJSONObject("data").getJSONArray("users").getJSONObject(0).getString("id");
      
      Assert.assertEquals(esbUserId, apiUserId);
      
      String esbEmail =
            esbRestResponse.getBody().getJSONObject("data").getJSONArray("users").getJSONObject(0).getString("email");
      String apiEmail =
            apiRestResponse.getBody().getJSONObject("data").getJSONArray("users").getJSONObject(0).getString("email");
      
      Assert.assertEquals(esbEmail, apiEmail);
      
      JSONObject esbPropertiesObj =
            esbRestResponse.getBody().getJSONObject("data").getJSONArray("users").getJSONObject(0)
                  .getJSONObject("properties");
      
      JSONObject apiPropertiesObj =
            apiRestResponse.getBody().getJSONObject("data").getJSONArray("users").getJSONObject(0)
                  .getJSONObject("properties");
      
      Assert.assertNotEquals(esbPropertiesObj, null);
      Assert.assertNotEquals(apiPropertiesObj, null);
      
   }
   
   /**
    * Positive test case for getUserById method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListUsersWithOptionalParameters" }, description = "planyo {getUserById} integration test with mandatory parameters.")
   public void testGetUserByIdWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getUserById");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserById_mandatory.json");
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey") + "&method=get_user_data&user_id="
                  + connectorProperties.getProperty("userId");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      String esbUserId = esbRestResponse.getBody().getJSONObject("data").getString("id");
      String apiUserId = apiRestResponse.getBody().getJSONObject("data").getString("id");
      
      Assert.assertEquals(esbUserId, apiUserId);
      
      String esbEmail = esbRestResponse.getBody().getJSONObject("data").getString("email");
      String apiEmail = apiRestResponse.getBody().getJSONObject("data").getString("email");
      
      Assert.assertEquals(esbEmail, apiEmail);
      
      String esbFirstName = esbRestResponse.getBody().getJSONObject("data").getString("first_name");
      String apiFirstName = apiRestResponse.getBody().getJSONObject("data").getString("first_name");
      
      Assert.assertEquals(esbFirstName, apiFirstName);
      
   }
   
   /**
    * Positive test case for getUserById method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetUserByIdWithMandatoryParameters" }, description = "planyo {getUserById} integration test with optional parameters.")
   public void testGetUserByIdWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getUserById");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserById_optional.json");
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey") + "&method=get_user_data&email="
                  + connectorProperties.getProperty("email") + "&detail_level=4";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      String esbUserId = esbRestResponse.getBody().getJSONObject("data").getString("id");
      String apiUserId = apiRestResponse.getBody().getJSONObject("data").getString("id");
      
      Assert.assertEquals(esbUserId, apiUserId);
      
      String esbEmail = esbRestResponse.getBody().getJSONObject("data").getString("email");
      String apiEmail = apiRestResponse.getBody().getJSONObject("data").getString("email");
      
      Assert.assertEquals(esbEmail, apiEmail);
      
      JSONObject esbPropertiesObj = esbRestResponse.getBody().getJSONObject("data").getJSONObject("properties");
      
      JSONObject apiPropertiesObj = apiRestResponse.getBody().getJSONObject("data").getJSONObject("properties");
      
      Assert.assertNotEquals(esbPropertiesObj, null);
      Assert.assertNotEquals(apiPropertiesObj, null);
      
   }
   
   /**
    * Negative test case for getUserById method.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetUserByIdWithOptionalParameters" }, description = "planyo {getUserById} integration test with negative case.")
   public void testGetUserByIdWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getUserById");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserById_negative.json");
      
      int esbResponseCode = esbRestResponse.getBody().getInt("response_code");
      
      Assert.assertEquals(esbResponseCode, 3);
      
      String apiEndPoint = apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey") + "&method=get_user_data";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      int apiResponseCode = apiRestResponse.getBody().getInt("response_code");
      
      Assert.assertEquals(esbResponseCode, apiResponseCode);
      
      String esbResponseMessage = esbRestResponse.getBody().getString("response_message");
      String apiResponseMessage = apiRestResponse.getBody().getString("response_message");
      
      Assert.assertEquals(esbResponseMessage, apiResponseMessage);
      
   }
   
   /**
    * Positive test case for listVouchers method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetUserByIdWithNegativeCase" }, description = "planyo {listVouchers} integration test with mandatory parameters.")
   public void testListVouchersWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listVouchers");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listVouchers_mandatory.json");
      
      JSONObject esbVoucherObject = esbRestResponse.getBody().getJSONObject("data").getJSONObject("results");
      String voucherCode = esbVoucherObject.keys().next().toString();
      
      connectorProperties.setProperty("voucherCode", voucherCode);
      
      String apiEndPoint = apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey") + "&method=list_vouchers";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      int esbVoucherCount = esbRestResponse.getBody().getJSONObject("data").getJSONObject("results").length();
      int apiVoucherCount = apiRestResponse.getBody().getJSONObject("data").getJSONObject("results").length();
      
      Assert.assertEquals(esbVoucherCount, apiVoucherCount);
      
      String esbVoucherCode =
            esbRestResponse.getBody().getJSONObject("data").getJSONObject("results").getJSONObject(voucherCode)
                  .getString("code");
      
      String apiVoucherCode =
            apiRestResponse.getBody().getJSONObject("data").getJSONObject("results").getJSONObject(voucherCode)
                  .getString("code");
      
      Assert.assertEquals(esbVoucherCode, apiVoucherCode);
      
      String esbReservationStartDate =
            esbRestResponse.getBody().getJSONObject("data").getJSONObject("results").getJSONObject(voucherCode)
                  .getString("reservation_start_date");
      
      String apiReservationStartDate =
            apiRestResponse.getBody().getJSONObject("data").getJSONObject("results").getJSONObject(voucherCode)
                  .getString("reservation_start_date");
      
      Assert.assertEquals(esbReservationStartDate, apiReservationStartDate);
      
   }
   
   /**
    * Positive test case for listVouchers method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListVouchersWithMandatoryParameters" }, description = "planyo {listVouchers} integration test with optional parameters.")
   public void testListVouchersWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listVouchers");
      
      String voucherCode = connectorProperties.getProperty("voucherCode");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listVouchers_optional.json");
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey")
                  + "&method=list_vouchers&voucher_code_prefix=" + voucherCode;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      int esbVoucherCount = esbRestResponse.getBody().getJSONObject("data").getJSONObject("results").length();
      int apiVoucherCount = apiRestResponse.getBody().getJSONObject("data").getJSONObject("results").length();
      
      Assert.assertEquals(esbVoucherCount, apiVoucherCount);
      
      String esbVoucherCode =
            esbRestResponse.getBody().getJSONObject("data").getJSONObject("results").getJSONObject(voucherCode)
                  .getString("code");
      
      String apiVoucherCode =
            apiRestResponse.getBody().getJSONObject("data").getJSONObject("results").getJSONObject(voucherCode)
                  .getString("code");
      
      Assert.assertEquals(esbVoucherCode, apiVoucherCode);
      
      String esbResources =
            esbRestResponse.getBody().getJSONObject("data").getJSONObject("results").getJSONObject(voucherCode)
                  .getString("resources");
      
      String apiResources =
            apiRestResponse.getBody().getJSONObject("data").getJSONObject("results").getJSONObject(voucherCode)
                  .getString("resources");
      
      Assert.assertEquals(esbResources, apiResources);
      
   }
   
   /**
    * Negative test case for listVouchers method.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListVouchersWithOptionalParameters" }, description = "planyo {listVouchers} integration test with negative case.")
   public void testListVouchersWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listVouchers");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listVouchers_negative.json");
      
      int esbResponseCode = esbRestResponse.getBody().getInt("response_code");
      
      Assert.assertEquals(esbResponseCode, 3);
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey")
                  + "&method=list_vouchers&resource_id=INVALID";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      int apiResponseCode = apiRestResponse.getBody().getInt("response_code");
      
      Assert.assertEquals(esbResponseCode, apiResponseCode);
      
      String esbResponseMessage = esbRestResponse.getBody().getString("response_message");
      String apiResponseMessage = apiRestResponse.getBody().getString("response_message");
      
      Assert.assertEquals(esbResponseMessage, apiResponseMessage);
      
   }
   
   /**
    * Positive test case for listReservations method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListVouchersWithNegativeCase" }, description = "planyo {listReservations} integration test with mandatory parameters.")
   public void testListReservationsWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listReservations");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listReservations_mandatory.json");
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey") + "&method=list_reservations&start_time="
                  + connectorProperties.getProperty("reservationStartTimeMandatory") + "&end_time="
                  + connectorProperties.getProperty("reservationEndTimeMandatory");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      int esbReservationCount = esbRestResponse.getBody().getJSONObject("data").getJSONArray("results").length();
      int apiReservationCount = apiRestResponse.getBody().getJSONObject("data").getJSONArray("results").length();
      
      Assert.assertEquals(esbReservationCount, apiReservationCount);
      
      String esbReservationId =
            esbRestResponse.getBody().getJSONObject("data").getJSONArray("results").getJSONObject(0)
                  .getString("reservation_id");
      
      String apiReservationId =
            apiRestResponse.getBody().getJSONObject("data").getJSONArray("results").getJSONObject(0)
                  .getString("reservation_id");
      
      Assert.assertEquals(esbReservationId, apiReservationId);
      
      String esbStartTime =
            esbRestResponse.getBody().getJSONObject("data").getJSONArray("results").getJSONObject(0)
                  .getString("start_time");
      String apiStartTime =
            apiRestResponse.getBody().getJSONObject("data").getJSONArray("results").getJSONObject(0)
                  .getString("start_time");
      
      Assert.assertEquals(esbStartTime, apiStartTime);
      
      String esbResourceId =
            esbRestResponse.getBody().getJSONObject("data").getJSONArray("results").getJSONObject(0)
                  .getString("resource_id");
      String apiResourceId =
            apiRestResponse.getBody().getJSONObject("data").getJSONArray("results").getJSONObject(0)
                  .getString("resource_id");
      
      Assert.assertEquals(esbResourceId, apiResourceId);
      
   }
   
   /**
    * Positive test case for listReservations method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListReservationsWithMandatoryParameters" }, description = "planyo {listReservations} integration test with optional parameters.")
   public void testListReservationsWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listReservations");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listReservations_optional.json");
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey") + "&method=list_reservations&start_time="
                  + connectorProperties.getProperty("reservationStartTimeMandatory") + "&end_time="
                  + connectorProperties.getProperty("reservationEndTimeMandatory") + "&resource_id="
                  + connectorProperties.getProperty("resourceId");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      int esbReservationCount = esbRestResponse.getBody().getJSONObject("data").getJSONArray("results").length();
      int apiReservationCount = apiRestResponse.getBody().getJSONObject("data").getJSONArray("results").length();
      
      Assert.assertEquals(esbReservationCount, apiReservationCount);
      
      String esbReservationId =
            esbRestResponse.getBody().getJSONObject("data").getJSONArray("results").getJSONObject(0)
                  .getString("reservation_id");
      
      String apiReservationId =
            apiRestResponse.getBody().getJSONObject("data").getJSONArray("results").getJSONObject(0)
                  .getString("reservation_id");
      
      Assert.assertEquals(esbReservationId, apiReservationId);
      
      String esbEndTime =
            esbRestResponse.getBody().getJSONObject("data").getJSONArray("results").getJSONObject(0)
                  .getString("end_time");
      String apiEndTime =
            apiRestResponse.getBody().getJSONObject("data").getJSONArray("results").getJSONObject(0)
                  .getString("end_time");
      
      Assert.assertEquals(esbEndTime, apiEndTime);
      
      String esbResourceId =
            esbRestResponse.getBody().getJSONObject("data").getJSONArray("results").getJSONObject(0)
                  .getString("resource_id");
      
      Assert.assertEquals(esbResourceId, connectorProperties.getProperty("resourceId"));
      
      String apiResourceId =
            apiRestResponse.getBody().getJSONObject("data").getJSONArray("results").getJSONObject(0)
                  .getString("resource_id");
      
      Assert.assertEquals(apiResourceId, connectorProperties.getProperty("resourceId"));
      
   }
   
   /**
    * Negative test case for listReservations method.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListReservationsWithOptionalParameters" }, description = "planyo {listReservations} integration test with negative case.")
   public void testListReservationsWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listReservations");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listReservations_negative.json");
      
      int esbResponseCode = esbRestResponse.getBody().getInt("response_code");
      
      Assert.assertEquals(esbResponseCode, 3);
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey") + "&method=list_reservations";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      int apiResponseCode = apiRestResponse.getBody().getInt("response_code");
      
      Assert.assertEquals(esbResponseCode, apiResponseCode);
      
      String esbResponseMessage = esbRestResponse.getBody().getString("response_message");
      String apiResponseMessage = apiRestResponse.getBody().getString("response_message");
      
      Assert.assertEquals(esbResponseMessage, apiResponseMessage);
      
   }
   
   /**
    * Positive test case for searchReservations method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListReservationsWithNegativeCase" }, description = "planyo {searchReservations} integration test with mandatory parameters.")
   public void testSearchReservationsWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:searchReservations");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchReservations_mandatory.json");
      int esbRentalId =
            esbRestResponse.getBody().getJSONObject("data").getJSONArray("results").getJSONObject(0)
                  .getInt("rental_id");
      
      int esbReservationCount = esbRestResponse.getBody().getJSONObject("data").getJSONArray("results").length();
      int esbUserId =
            esbRestResponse.getBody().getJSONObject("data").getJSONArray("results").getJSONObject(0).getInt("user_id");
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey") + "&method=reservation_search&query="
                  + connectorProperties.getProperty("reservationId");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      int apiRentalId =
            apiRestResponse.getBody().getJSONObject("data").getJSONArray("results").getJSONObject(0)
                  .getInt("rental_id");
      int apiReservationCount = apiRestResponse.getBody().getJSONObject("data").getJSONArray("results").length();
      int apiUserId =
            esbRestResponse.getBody().getJSONObject("data").getJSONArray("results").getJSONObject(0).getInt("user_id");
      
      Assert.assertEquals(esbRentalId, apiRentalId);
      Assert.assertEquals(esbReservationCount, apiReservationCount);
      Assert.assertEquals(esbUserId, apiUserId);
   }
   
   /**
    * Positive test case for searchReservations method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSearchReservationsWithMandatoryParameters" }, description = "planyo {searchReservations} integration test with optional parameters.")
   public void testSearchReservationsWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:searchReservations");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchReservations_optional.json");
      
      int esbReservationCount = esbRestResponse.getBody().getJSONObject("data").getJSONArray("results").length();
      int esbFirstRentalId =
            esbRestResponse.getBody().getJSONObject("data").getJSONArray("results").getJSONObject(0)
                  .getInt("rental_id");
      int esbSecondRentalId =
            esbRestResponse.getBody().getJSONObject("data").getJSONArray("results").getJSONObject(1)
                  .getInt("rental_id");
      
      // Checking whether the list of reservations has been sorted according to the ascending order of the
      // rental id.
      Assert.assertTrue(esbSecondRentalId > esbFirstRentalId);
      
      String apiEndPoint =
            apiUrl
                  + "?api_key="
                  + connectorProperties.getProperty("apiKey")
                  + "&method=reservation_search&query="
                  + URLEncoder.encode(connectorProperties.getProperty("esbResourceNameMandatory"), Charset
                        .defaultCharset().toString()) + "&sort=rental_id";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      int apiReservationCount = apiRestResponse.getBody().getJSONObject("data").getJSONArray("results").length();
      
      int apiFirstRentalId =
            apiRestResponse.getBody().getJSONObject("data").getJSONArray("results").getJSONObject(0)
                  .getInt("rental_id");
      
      int apiSecondRentalId =
            apiRestResponse.getBody().getJSONObject("data").getJSONArray("results").getJSONObject(1)
                  .getInt("rental_id");
      
      Assert.assertEquals(esbFirstRentalId, apiFirstRentalId);
      Assert.assertEquals(esbSecondRentalId, apiSecondRentalId);
      Assert.assertEquals(esbReservationCount, apiReservationCount);
   }
   
   /**
    * Positive test case for getReservationById method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSearchReservationsWithOptionalParameters" }, description = "planyo {getReservationById} integration test with mandatory parameters.")
   public void testGetReservationByIdWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getReservationById");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getReservationById_mandatory.json");
      
      int esbResourceId = esbRestResponse.getBody().getJSONObject("data").getInt("resource_id");
      String esbReservationStatus = esbRestResponse.getBody().getJSONObject("data").getString("status");
      String esbReservationCreationTime = esbRestResponse.getBody().getJSONObject("data").getString("creation_time");
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey")
                  + "&method=get_reservation_data&reservation_id=" + connectorProperties.getProperty("reservationId");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      int apiResourceId = apiRestResponse.getBody().getJSONObject("data").getInt("resource_id");
      String apiReservationStatus = apiRestResponse.getBody().getJSONObject("data").getString("status");
      String apiReservationCreationTime = apiRestResponse.getBody().getJSONObject("data").getString("creation_time");
      
      Assert.assertEquals(esbResourceId, apiResourceId);
      Assert.assertEquals(esbReservationStatus, apiReservationStatus);
      Assert.assertEquals(esbReservationCreationTime, apiReservationCreationTime);
   }
   
   /**
    * Positive test case for getReservationById method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetReservationByIdWithMandatoryParameters" }, description = "planyo {getReservationById} integration test with optional parameters.")
   public void testGetReservationByIdWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getReservationById");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getReservationById_optional.json");
      
      int esbResourceId = esbRestResponse.getBody().getJSONObject("data").getInt("resource_id");
      String esbReservationCreationTime = esbRestResponse.getBody().getJSONObject("data").getString("creation_time");
      String esbUserText = esbRestResponse.getBody().getJSONObject("data").getString("user_text");
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey")
                  + "&method=get_reservation_data&reservation_id=" + connectorProperties.getProperty("reservationId")
                  + "&language=DE";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      int apiResourceId = apiRestResponse.getBody().getJSONObject("data").getInt("resource_id");
      String apiReservationCreationTime = apiRestResponse.getBody().getJSONObject("data").getString("creation_time");
      String apiUserText = apiRestResponse.getBody().getJSONObject("data").getString("user_text");
      
      Assert.assertEquals(esbResourceId, apiResourceId);
      Assert.assertEquals(esbReservationCreationTime, apiReservationCreationTime);
      Assert.assertEquals(esbUserText, apiUserText);
   }
   
   /**
    * Negative test case for getReservationById method.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetReservationByIdWithOptionalParameters" }, description = "planyo {getReservationById} integration test with negative case.")
   public void testGetReservationByIdWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getReservationById");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getReservationById_negative.json");
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey")
                  + "&method=get_reservation_data&reservation_id=Invalid";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getInt("response_code"),
            apiRestResponse.getBody().getInt("response_code"));
      Assert.assertEquals(esbRestResponse.getBody().getString("response_message"),
            apiRestResponse.getBody().getString("response_message"));
   }
   
   /**
    * Positive test case for setCustomProperties method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetReservationByIdWithNegativeCase" }, description = "planyo {setCustomProperties} integration test with mandatory parameters.")
   public void testSetCustomPropertiesWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:setCustomProperties");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setCustomProperties_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getInt("response_code"), 0);
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey")
                  + "&method=get_reservation_data&reservation_id=" + connectorProperties.getProperty("reservationId");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getInt("response_code"), 0);
      
      String apiPropertyValue =
            apiRestResponse.getBody().getJSONObject("data").getJSONObject("properties").getString("customPropertyName");
      
      Assert.assertTrue("null".equals(apiPropertyValue));
   }
   
   /**
    * Positive test case for setCustomProperties method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetCustomPropertiesWithMandatoryParameters" }, description = "planyo {setCustomProperties} integration test with optional parameters.")
   public void testSetCustomPropertiesWithOptionalParameters() throws IOException, JSONException {
   
      connectorProperties.setProperty("propertyValue", "customPropertyValue");
      esbRequestHeadersMap.put("Action", "urn:setCustomProperties");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setCustomProperties_optional.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getInt("response_code"), 0);
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey")
                  + "&method=get_reservation_data&reservation_id=" + connectorProperties.getProperty("reservationId");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getInt("response_code"), 0);
      
      String apiPropertyValue =
            apiRestResponse.getBody().getJSONObject("data").getJSONObject("properties").getString("customPropertyName");
      Assert.assertEquals(apiPropertyValue, connectorProperties.getProperty("propertyValue"));
   }
   
   /**
    * Positive test case for checkResourceAvailability method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetCustomPropertiesWithOptionalParameters" }, description = "planyo {checkResourceAvailability} integration test with mandatory parameters.")
   public void testCheckResourceAvailabilityWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:checkResourceAvailability");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_checkResourceAvailability_mandatory.json");
      
      String esbStatus = esbRestResponse.getBody().getJSONObject("data").getString("is_available");
      String esbQuantityAvailable = esbRestResponse.getBody().getJSONObject("data").getString("quantity_available");
      
      Assert.assertEquals(esbRestResponse.getBody().getInt("response_code"), 0);
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey")
                  + "&method=is_resource_available&start_time="
                  + connectorProperties.getProperty("reservationStartTimeMandatory") + "&end_time="
                  + connectorProperties.getProperty("reservationEndTimeMandatory") + "&quantity=1&resource_id="
                  + connectorProperties.getProperty("resourceId");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      String apiStatus = apiRestResponse.getBody().getJSONObject("data").getString("is_available");
      String apiQuantityAvailable = apiRestResponse.getBody().getJSONObject("data").getString("quantity_available");
      
      Assert.assertEquals(apiRestResponse.getBody().getInt("response_code"), 0);
      
      Assert.assertEquals(esbStatus, apiStatus);
      Assert.assertEquals(esbQuantityAvailable, apiQuantityAvailable);
   }
   
   /**
    * Negative test case for checkResourceAvailability method.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetCustomPropertiesWithMandatoryParameters" }, description = "planyo {checkResourceAvailability} integration test with negative case.")
   public void testCheckResourceAvailabilityWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:checkResourceAvailability");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_checkResourceAvailability_negative.json");
      
      String apiEndPoint =
            apiUrl + "?api_key=" + connectorProperties.getProperty("apiKey")
                  + "&method=is_resource_available&resource_id=Invalid&start_time="
                  + connectorProperties.getProperty("reservationStartTimeMandatory") + "&end_time="
                  + connectorProperties.getProperty("reservationEndTimeMandatory") + "&quantity=1";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("response_message"),
            apiRestResponse.getBody().getString("response_message"));
      Assert.assertEquals(esbRestResponse.getBody().getInt("response_code"),
            apiRestResponse.getBody().getInt("response_code"));
   }
   
}
