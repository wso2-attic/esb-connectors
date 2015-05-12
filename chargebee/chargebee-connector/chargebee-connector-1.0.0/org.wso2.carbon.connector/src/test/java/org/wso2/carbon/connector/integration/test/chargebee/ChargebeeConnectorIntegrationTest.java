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

package org.wso2.carbon.connector.integration.test.chargebee;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class ChargebeeConnectorIntegrationTest extends ConnectorIntegrationTestBase {
   
   private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
   
   private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
   
   private String apiUrl;
   
   private String currentTimeString;
   
   /**
    * Set up the environment.
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {
   
      init("chargebee-connector-1.0.0");
      
      esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
      esbRequestHeadersMap.put("Content-Type", "application/json");
      
      // Generates the Base64 encoded apiKey to be used for the authorization
      // of direct API calls via Authorization header
      final String token = connectorProperties.getProperty("apiKey") + ":";
      byte[] encodedToken = Base64.encodeBase64(token.getBytes());
      
      apiRequestHeadersMap.put("Accept-Charset", "UTF-8");
      apiRequestHeadersMap.put("Authorization", "Basic " + new String(encodedToken));
      apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");
      
      currentTimeString = String.valueOf(System.currentTimeMillis());
      apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v1";
   }
   
   /**
    * Positive test case for createPlan method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, description = "chargerbee {createPlan} integration test with mandatory parameters.")
   public void testCreatePlanWithMandatoryParameters() throws IOException, JSONException {
   
      String createPlanId = "createPlanIdMandatory_" + currentTimeString;
      String createPlanName = "createPlanNameMandatory_" + currentTimeString;
      connectorProperties.setProperty("createPlanIdMandatory", createPlanId);
      connectorProperties.setProperty("createPlanNameMandatory", createPlanName);
      
      esbRequestHeadersMap.put("Action", "urn:createPlan");
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPlan_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      final String apiEndPoint = apiUrl + "/plans/" + createPlanId;
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("plan").getString("id"), createPlanId);
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("plan").getString("name"), createPlanName);
      
   }
   
   /**
    * Positive test case for createPlan method with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, description = "chargerbee {createPlan} integration test with optional parameters.")
   public void testCreatePlanWithOptionalParameters() throws IOException, JSONException {
   
      String createPlanId = "createPlanIdOptional_" + currentTimeString;
      String createPlanName = "createPlanNameOptional_" + currentTimeString;
      String createPlanInvoiceName = "createPlanInvoiceName_" + currentTimeString;
      
      connectorProperties.setProperty("createPlanIdOptional", createPlanId);
      connectorProperties.setProperty("createPlanNameOptional", createPlanName);
      connectorProperties.setProperty("createPlanInvoiceName", createPlanInvoiceName);
      
      esbRequestHeadersMap.put("Action", "urn:createPlan");
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPlan_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      final String apiEndPoint = apiUrl + "/plans/" + createPlanId;
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      final JSONObject apiPlanObject = apiRestResponse.getBody().getJSONObject("plan");
      
      Assert.assertEquals(apiPlanObject.getString("price"), connectorProperties.getProperty("discountAmount"));
      Assert.assertEquals(apiPlanObject.getString("invoice_name"),createPlanInvoiceName);
      Assert.assertEquals(apiPlanObject.getString("id"), createPlanId);
      Assert.assertEquals(apiPlanObject.getString("name"), createPlanName);
      Assert.assertEquals(apiPlanObject.getString("invoice_name"), createPlanInvoiceName);
   }
   
   /**
    * Negative test case for createPlan method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePlanWithMandatoryParameters" }, description = "chargerbee {createPlan} integration test with negative case.")
   public void testCreatePlanWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createPlan");
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPlan_negative.json");
      final String apiEndPoint = apiUrl + "/plans";
      final RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createPlan_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
   }
   
   /**
    * Positive test case for getPlan method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePlanWithMandatoryParameters" }, description = "chargerbee {getPlan} integration test with mandatory parameters.")
   public void testGetPlanWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getPlan");
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPlan_mandatory.json");
      
      final String apiEndPoint = apiUrl + "/plans/" + connectorProperties.getProperty("createPlanIdMandatory");
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("plan").getString("name"), apiRestResponse.getBody()
            .getJSONObject("plan").getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("plan").getString("price"), apiRestResponse.getBody()
            .getJSONObject("plan").getString("price"));
   }
   
   /**
    * Positive test case for listPlans method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePlanWithMandatoryParameters" }, description = "chargerbee {listPlans} integration test with mandatory parameters.")
   public void testListPlansWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listPlans");
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPlans_mandatory.json");
      final JSONArray esbPlansListArray = esbRestResponse.getBody().getJSONArray("list");
      
      final String apiEndPoint = apiUrl + "/plans";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONArray apiPlansListArray = apiRestResponse.getBody().getJSONArray("list");
      
      Assert.assertEquals(esbPlansListArray.length(), apiPlansListArray.length());
      Assert.assertEquals(esbPlansListArray.getJSONObject(0).getJSONObject("plan").getString("id"), apiPlansListArray
            .getJSONObject(0).getJSONObject("plan").getString("id"));
      Assert.assertEquals(esbPlansListArray.getJSONObject(0).getJSONObject("plan").getString("name"), apiPlansListArray
            .getJSONObject(0).getJSONObject("plan").getString("name"));
   }
   
   /**
    * Positive test case for listPlans method with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePlanWithMandatoryParameters" }, description = "chargerbee {listPlans} integration test with optional parameters.")
   public void testListPlansWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listPlans");
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPlans_optional.json");
      final JSONArray esbPlansListArray = esbRestResponse.getBody().getJSONArray("list");
      
      final String apiEndPoint = apiUrl + "/plans?limit=1";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONArray apiPlansListArray = apiRestResponse.getBody().getJSONArray("list");
      
      Assert.assertEquals(esbPlansListArray.length(), 1);
      Assert.assertEquals(esbPlansListArray.length(), apiPlansListArray.length());
      Assert.assertEquals(esbPlansListArray.getJSONObject(0).getJSONObject("plan").getString("id"), apiPlansListArray
            .getJSONObject(0).getJSONObject("plan").getString("id"));
      Assert.assertEquals(esbPlansListArray.getJSONObject(0).getJSONObject("plan").getString("name"), apiPlansListArray
            .getJSONObject(0).getJSONObject("plan").getString("name"));
   }
   
   /**
    * Negative test case for listPlans method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, description = "chargerbee {listPlans} integration test with negative case.")
   public void testListPlansWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listPlans");
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPlans_negative.json");
      
      final String apiEndPoint = apiUrl + "/plans?limit=0";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
   }
   
   /**
    * Positive test case for createCoupon method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, description = "chargebee {createCoupon} integration test with mandatory parameters.")
   public void testCreateCouponWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createCoupon");
      String createCouponId = "createCouponIdMandatory_" + currentTimeString;
      String createCouponName = "createCouponNameMandatory_" + currentTimeString;
      connectorProperties.setProperty("couponIdMand", createCouponId);
      connectorProperties.setProperty("couponNameMand", createCouponName);
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCoupon_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      final String couponId = esbRestResponse.getBody().getJSONObject("coupon").getString("id");
      connectorProperties.setProperty("couponId", couponId);
      
      final String apiEndPoint = apiUrl + "/coupons/" + couponId;
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("coupon");
      
      Assert.assertEquals(apiResponseObject.getString("id"), connectorProperties.getProperty("couponIdMand"));
      Assert.assertEquals(apiResponseObject.getString("name"), connectorProperties.getProperty("couponNameMand"));
      Assert.assertEquals(apiResponseObject.getString("discount_amount"),
            connectorProperties.getProperty("discountAmount"));
      Assert.assertEquals(apiResponseObject.getString("duration_type"), connectorProperties.getProperty("durationType"));
   }
   
   /**
    * Positive test case for createCoupon method with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, description = "chargebee {createCoupon} integration test with optional parameters.")
   public void testCreateCouponWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createCoupon");
      
      String createCouponId = "createCouponIdOptional_" + currentTimeString;
      String createCouponName = "createCouponNameOptional_" + currentTimeString;
      connectorProperties.setProperty("couponIdOpt", createCouponId);
      connectorProperties.setProperty("couponNameOpt", createCouponName);
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCoupon_optional.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      final String couponId = esbRestResponse.getBody().getJSONObject("coupon").getString("id");
      
      final String apiEndPoint = apiUrl + "/coupons/" + couponId;
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("coupon");
      
      Assert.assertEquals(apiResponseObject.getString("invoice_name"),
            connectorProperties.getProperty("invoiceNameOpt"));
      Assert.assertEquals(apiResponseObject.getString("invoice_notes"),
            connectorProperties.getProperty("invoiceNotesOpt"));
      Assert.assertEquals(apiResponseObject.getString("valid_till"), connectorProperties.getProperty("validTill"));
      Assert.assertEquals(apiResponseObject.getString("max_redemptions"),
            connectorProperties.getProperty("maxRedemptions"));
   }
   
   /**
    * Negative test case for createCoupon method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, description = "chargebee {createCoupon} integration test with negative case.")
   public void testCreateCouponWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createCoupon");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCoupon_negative.json");
      
      final String apiEndPoint = apiUrl + "/coupons";
      final RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createCoupon_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_msg"),
            apiRestResponse.getBody().getString("error_msg"));
   }
   
   /**
    * Positive test case for getCoupon method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCouponWithMandatoryParameters" }, description = "chargebee {getCoupon} integration test with mandatory parameters.")
   public void testGetCouponWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getCoupon");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCoupon_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      final JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("coupon");
      
      final String apiEndPoint = apiUrl + "/coupons/" + connectorProperties.getProperty("couponId");
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      
      final JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("coupon");
      
      Assert.assertEquals(esbResponseObject.getString("name"), apiResponseObject.getString("name"));
      Assert.assertEquals(esbResponseObject.getString("discount_type"), apiResponseObject.getString("discount_type"));
      Assert.assertEquals(esbResponseObject.getString("apply_on"), apiResponseObject.getString("apply_on"));
      Assert.assertEquals(esbResponseObject.getLong("created_at"), apiResponseObject.getLong("created_at"));
   }
   
   /**
    * Positive test case for listCoupons method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCouponWithMandatoryParameters" }, description = "chargebee {listCoupons} integration test with mandatory parameters.")
   public void testListCouponsWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listCoupons");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCoupons_mandatory.json");
      
      final int esbResponseArrayLength = esbRestResponse.getBody().getJSONArray("list").length();
      final JSONObject esbResponseObjectOne =
            esbRestResponse.getBody().getJSONArray("list").getJSONObject(0).getJSONObject("coupon");
      
      final String apiEndPoint = apiUrl + "/coupons";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      final int apiResponseArrayLength = apiRestResponse.getBody().getJSONArray("list").length();
      final JSONObject apiResponseObjectOne =
            apiRestResponse.getBody().getJSONArray("list").getJSONObject(0).getJSONObject("coupon");
      
      Assert.assertEquals(esbResponseArrayLength, apiResponseArrayLength);
      
      Assert.assertEquals(esbResponseObjectOne.getString("id"), apiResponseObjectOne.getString("id"));
      Assert.assertEquals(esbResponseObjectOne.getString("name"), apiResponseObjectOne.getString("name"));
      Assert.assertEquals(esbResponseObjectOne.getString("discount_type"),
            apiResponseObjectOne.getString("discount_type"));
      Assert.assertEquals(esbResponseObjectOne.getString("apply_on"), apiResponseObjectOne.getString("apply_on"));
      Assert.assertEquals(esbResponseObjectOne.getString("created_at"), apiResponseObjectOne.getString("created_at"));
   }
   
   /**
    * Positive test case for listCoupons method with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCouponWithMandatoryParameters" }, description = "chargebee {listCoupons} integration test with optional parameters.")
   public void testListCouponsWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listCoupons");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCoupons_optional.json");
      int esbResponseArrayLength = esbRestResponse.getBody().getJSONArray("list").length();
      
      String apiEndPoint = apiUrl + "/coupons?limit=1";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      int apiResponseArrayLength = apiRestResponse.getBody().getJSONArray("list").length();
      
      final JSONObject esbResponseObjectOne =
            esbRestResponse.getBody().getJSONArray("list").getJSONObject(0).getJSONObject("coupon");
      final JSONObject apiResponseObjectOne =
            apiRestResponse.getBody().getJSONArray("list").getJSONObject(0).getJSONObject("coupon");
      Assert.assertEquals(esbResponseArrayLength, 1);
      Assert.assertEquals(esbResponseArrayLength, apiResponseArrayLength);
      Assert.assertEquals(esbResponseObjectOne.getString("id"), apiResponseObjectOne.getString("id"));
      Assert.assertEquals(esbResponseObjectOne.getString("name"), apiResponseObjectOne.getString("name"));
      Assert.assertEquals(esbResponseObjectOne.getString("discount_type"),
            apiResponseObjectOne.getString("discount_type"));
      Assert.assertEquals(esbResponseObjectOne.getString("apply_on"), apiResponseObjectOne.getString("apply_on"));
      Assert.assertEquals(esbResponseObjectOne.getString("created_at"), apiResponseObjectOne.getString("created_at"));
      
   }
   
   /**
    * Negative test case for listCoupons method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, description = "chargebee {listCoupons} integration test with negative case.")
   public void testListCouponsWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listCoupons");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCoupons_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      
      final String apiEndPoint = apiUrl + "/coupons?limit=INVALID";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_msg"),
            apiRestResponse.getBody().getString("error_msg"));
   }
   
   /**
    * Positive test case for createCustomer method with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, description = "chargebee {createCustomer} integration test with optional parameters.")
   public void testCreateCustomerWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createCustomer");
      String createCustomerId = "createCustomerId_" + currentTimeString;
      connectorProperties.setProperty("customerId", createCustomerId);
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomer_optional.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      final String customerId = esbRestResponse.getBody().getJSONObject("customer").getString("id");
      
      final String apiEndPoint = apiUrl + "/customers/" + customerId;
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("customer");
      Assert.assertEquals(apiResponseObject.getString("id"), connectorProperties.getProperty("customerId"));
      Assert.assertEquals(apiResponseObject.getString("first_name"), connectorProperties.getProperty("firstName"));
      Assert.assertEquals(apiResponseObject.getString("email"), connectorProperties.getProperty("email"));
      Assert.assertEquals(apiResponseObject.getString("last_name"), connectorProperties.getProperty("lastName"));
      Assert.assertEquals(apiResponseObject.getString("company"), connectorProperties.getProperty("companyName"));
   }
   
   /**
    * Negative test case for createCustomer method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCustomerWithOptionalParameters" }, description = "chargebee {createCustomer} integration test with negative case.")
   public void testCreateCustomerWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createCustomer");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomer_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      
      final String apiEndPoint = apiUrl + "/customers";
      final RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createCustomer_negative.json");
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_msg"),
            apiRestResponse.getBody().getString("error_msg"));
   }
   
   /**
    * Positive test case for getCustomer method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCustomerWithOptionalParameters" }, description = "chargebee {getCustomer} integration test with mandatory parameters.")
   public void testGetCustomerWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getCustomer");
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCustomer_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      String customerId = connectorProperties.getProperty("customerId");
      
      final String apiEndPoint = apiUrl + "/customers/" + customerId;
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      final JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("customer");
      final JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("customer");
      Assert.assertEquals(esbResponseObject.getString("first_name"), apiResponseObject.getString("first_name"));
      Assert.assertEquals(esbResponseObject.getString("email"), apiResponseObject.getString("email"));
      Assert.assertEquals(esbResponseObject.getString("last_name"), apiResponseObject.getString("last_name"));
      Assert.assertEquals(esbResponseObject.getString("company"), apiResponseObject.getString("company"));
   }
   
   /**
    * Positive test case for listCustomers method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCustomerWithOptionalParameters" }, description = "chargebee {listCustomers} integration test with mandatory parameters.")
   public void testListCustomersWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listCustomers");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomers_mandatory.json");
      
      final int esbResponseArrayLength = esbRestResponse.getBody().getJSONArray("list").length();
      final JSONObject esbResponseObjectOne =
            esbRestResponse.getBody().getJSONArray("list").getJSONObject(0).getJSONObject("customer");
      
      final String apiEndPoint = apiUrl + "/customers";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final int apiResponseArrayLength = apiRestResponse.getBody().getJSONArray("list").length();
      final JSONObject apiResponseObjectOne =
            apiRestResponse.getBody().getJSONArray("list").getJSONObject(0).getJSONObject("customer");
      
      Assert.assertEquals(esbResponseArrayLength, apiResponseArrayLength);
      
      Assert.assertEquals(esbResponseObjectOne.getString("id"), apiResponseObjectOne.getString("id"));
      Assert.assertEquals(esbResponseObjectOne.getInt("created_at"), apiResponseObjectOne.getInt("created_at"));
      Assert.assertEquals(esbResponseObjectOne.getString("first_name"), apiResponseObjectOne.getString("first_name"));
      Assert.assertEquals(esbResponseObjectOne.getString("email"), apiResponseObjectOne.getString("email"));
   }
   
   /**
    * positive test case for listCustomers method optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCustomerWithOptionalParameters" }, description = "chargebee {listCustomers} integration test with optional parameters.")
   public void testListCustomersWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listCustomers");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomers_optional.json");
      
      final int esbResponseArrayLength = esbRestResponse.getBody().getJSONArray("list").length();
      final JSONObject esbResponseObjectOne =
            esbRestResponse.getBody().getJSONArray("list").getJSONObject(0).getJSONObject("customer");
      Assert.assertEquals(esbResponseArrayLength, 1);
      final String apiEndPoint = apiUrl + "/customers?limit=1";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      final int apiResponseArrayLength = apiRestResponse.getBody().getJSONArray("list").length();
      final JSONObject apiResponseObjectOne =
            apiRestResponse.getBody().getJSONArray("list").getJSONObject(0).getJSONObject("customer");
      
      Assert.assertEquals(esbResponseArrayLength, apiResponseArrayLength);
      
      Assert.assertEquals(esbResponseObjectOne.getString("id"), apiResponseObjectOne.getString("id"));
      Assert.assertEquals(esbResponseObjectOne.getInt("created_at"), apiResponseObjectOne.getInt("created_at"));
      Assert.assertEquals(esbResponseObjectOne.getString("first_name"), apiResponseObjectOne.getString("first_name"));
      Assert.assertEquals(esbResponseObjectOne.getString("email"), apiResponseObjectOne.getString("email"));
   }
   
   /**
    * Negative test case for listCustomers method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, description = "chargebee {listCustomers} integration test with negative case.")
   public void testListCustomersWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listCustomers");
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomers_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      
      final String apiEndPoint = apiUrl + "/invoices?limit=0";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
   }
   
   /**
    * Positive test case for createSubscriptionForCustomer method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePlanWithMandatoryParameters",
         "testCreatePlanWithOptionalParameters", "testCreateCustomerWithOptionalParameters" }, description = "chargebee {createSubscriptionForCustomer} integration test with mandatory parameters.")
   public void testCreateSubscriptionForCustomerWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createSubscriptionForCustomer");
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                  "esb_createSubscriptionForCustomer_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      final String subscriptionIdMandatory = esbRestResponse.getBody().getJSONObject("subscription").getString("id");
      connectorProperties.setProperty("subscriptionIdMandatory", subscriptionIdMandatory);
      
      final String apiEndPoint = apiUrl + "/subscriptions/" + subscriptionIdMandatory;
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("subscription").getString("plan_id"),
            connectorProperties.getProperty("createPlanIdMandatory"));
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("customer").getString("id"),
            connectorProperties.getProperty("customerId"));
   }
   
   /**
    * Positive test case for createSubscriptionForCustomer method with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateSubscriptionForCustomerWithMandatoryParameters" }, description = "chargebee {createSubscriptionForCustomer} integration test with optional parameters.")
   public void testCreateSubscriptionForCustomerWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createSubscriptionForCustomer");
      
      // Create subscription start timestamp by adding three days to current time.
      final String trialEndTimeStamp = String.valueOf(System.currentTimeMillis() / 1000 + (86400 * 3));
      connectorProperties.setProperty("trialEndTimeStamp", trialEndTimeStamp);
      
      final String subscriptionIdOptional = "subscriptionOpt_" + currentTimeString;
      connectorProperties.setProperty("subscriptionIdOptional", subscriptionIdOptional);
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                  "esb_createSubscriptionForCustomer_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      final String apiEndPoint = apiUrl + "/subscriptions/" + subscriptionIdOptional;
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      
      final JSONObject subscriptionObj = apiRestResponse.getBody().getJSONObject("subscription");
      final JSONObject shippingAddressObj = subscriptionObj.getJSONObject("shipping_address");
      
      Assert.assertEquals(subscriptionObj.getString("trial_end"), connectorProperties.getProperty("trialEndTimeStamp"));
      Assert.assertEquals(subscriptionObj.getString("invoice_notes"), connectorProperties.getProperty("notes"));
      Assert.assertEquals(shippingAddressObj.getString("first_name"), connectorProperties.getProperty("firstName"));
      Assert.assertEquals(shippingAddressObj.getString("last_name"), connectorProperties.getProperty("lastName"));
      Assert.assertEquals(shippingAddressObj.getString("email"), connectorProperties.getProperty("email"));
   }
   
   /**
    * Negative test case for createSubscriptionForCustomer method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateSubscriptionForCustomerWithOptionalParameters" }, description = "chargebee {createSubscriptionForCustomer} integration test with negative case.")
   public void testCreateSubscriptionForCustomerWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createSubscriptionForCustomer");
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                  "esb_createSubscriptionForCustomer_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      
      final String apiEndPoint = apiUrl + "/customers/--INVALID--/subscriptions";
      final RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                  "api_createSubscriptionForCustomer_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
   }
   
   /**
    * Positive test case for updateSubscription method with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateSubscriptionForCustomerWithNegativeCase" }, description = "chargebee {updateSubscription} integration test with optional parameters.")
   public void testUpdateSubscriptionWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateSubscription");
      final String apiEndPoint = apiUrl + "/subscriptions/" + connectorProperties.getProperty("subscriptionIdOptional");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      
      final JSONObject subscriptionObj = apiRestResponse.getBody().getJSONObject("subscription");
      final JSONObject shippingAddressObj = subscriptionObj.getJSONObject("shipping_address");
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateSubscription_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      final JSONObject updatedSubscriptionObj = apiRestResponse.getBody().getJSONObject("subscription");
      final JSONObject updatedShippingAddressObj = updatedSubscriptionObj.getJSONObject("shipping_address");
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertNotEquals(subscriptionObj.getString("plan_id"), updatedSubscriptionObj.getString("plan_id"));
      Assert.assertNotEquals(subscriptionObj.getString("invoice_notes"),
            updatedSubscriptionObj.getString("invoice_notes"));
      Assert.assertNotEquals(subscriptionObj.getJSONObject("shipping_address").getString("first_name"),
            updatedSubscriptionObj.getJSONObject("shipping_address").getString("first_name"));
      Assert.assertNotEquals(shippingAddressObj.getString("last_name"),
            updatedShippingAddressObj.getString("last_name"));
      Assert.assertNotEquals(shippingAddressObj.getString("email"), updatedShippingAddressObj.getString("email"));
   }
   
   /**
    * Negative test case for updateSubscription method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateSubscriptionWithOptionalParameters" }, description = "chargebee {updateSubscription} integration test with negative case.")
   public void testUpdateSubscriptionWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateSubscription");
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateSubscription_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      
      final String apiEndPoint =
            apiUrl + "/subscriptions/" + connectorProperties.getProperty("subscriptionIdMandatory");
      final RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateSubscription_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
   }
   
   /**
    * Positive test case for getSubscription method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateSubscriptionWithNegativeCase" }, description = "chargebee {getSubscription} integration test with mandatory parameters.")
   public void testGetSubscriptionWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getSubscription");
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSubscription_mandatory.json");
      
      final String apiEndPoint =
            apiUrl + "/subscriptions/" + connectorProperties.getProperty("subscriptionIdMandatory");
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      final JSONObject esbSubscriptionObj = esbRestResponse.getBody().getJSONObject("subscription");
      final JSONObject apiSubscriptionObj = apiRestResponse.getBody().getJSONObject("subscription");
      
      Assert.assertEquals(esbSubscriptionObj.getString("plan_id"), apiSubscriptionObj.getString("plan_id"));
      Assert.assertEquals(esbSubscriptionObj.getString("created_at"), apiSubscriptionObj.getString("created_at"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("customer").getString("id"), apiRestResponse
            .getBody().getJSONObject("customer").getString("id"));
   }
   
   /**
    * Positive test case for listSubscriptions method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, description = "chargebee {listSubscriptions} integration test with mandatory parameters.")
   public void testListSubscriptionsWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listSubscriptions");
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSubscriptions_mandatory.json");
      final JSONArray esbSubscriptionListArray = esbRestResponse.getBody().getJSONArray("list");
      
      final String apiEndPoint = apiUrl + "/subscriptions";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONArray apiSubscriptionListArray = apiRestResponse.getBody().getJSONArray("list");
      
      Assert.assertEquals(esbSubscriptionListArray.length(), apiSubscriptionListArray.length());
      
      final JSONObject esbSubscriptionFirstObj =
            esbSubscriptionListArray.getJSONObject(0).getJSONObject("subscription");
      final JSONObject apiSubscriptionFirstObj =
            apiSubscriptionListArray.getJSONObject(0).getJSONObject("subscription");
      
      Assert.assertEquals(esbSubscriptionFirstObj.getString("id"), apiSubscriptionFirstObj.getString("id"));
      Assert.assertEquals(esbSubscriptionFirstObj.getString("plan_id"), apiSubscriptionFirstObj.getString("plan_id"));
      Assert.assertEquals(esbSubscriptionFirstObj.getString("plan_quantity"),
            apiSubscriptionFirstObj.getString("plan_quantity"));
      Assert.assertEquals(esbSubscriptionFirstObj.getString("status"), apiSubscriptionFirstObj.getString("status"));
   }
   
   /**
    * Positive test case for listSubscriptions method with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListSubscriptionsWithMandatoryParameters" }, description = "chargebee {listSubscriptions} integration test with optional parameters.")
   public void testListSubscriptionsWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listSubscriptions");
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSubscriptions_optional.json");
      final JSONArray esbSubscriptionListArray = esbRestResponse.getBody().getJSONArray("list");
      Assert.assertEquals(esbSubscriptionListArray.length(), 1);
      
      final String apiEndPoint = apiUrl + "/subscriptions?limit=1";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final JSONArray apiSubscriptionListArray = apiRestResponse.getBody().getJSONArray("list");
      
      Assert.assertEquals(esbSubscriptionListArray.length(), apiSubscriptionListArray.length());
      
      final JSONObject esbSubscriptionFirstObj =
            esbSubscriptionListArray.getJSONObject(0).getJSONObject("subscription");
      final JSONObject apiSubscriptionFirstObj =
            apiSubscriptionListArray.getJSONObject(0).getJSONObject("subscription");
      
      Assert.assertEquals(esbSubscriptionFirstObj.getString("id"), apiSubscriptionFirstObj.getString("id"));
      Assert.assertEquals(esbSubscriptionFirstObj.getString("plan_id"), apiSubscriptionFirstObj.getString("plan_id"));
      Assert.assertEquals(esbSubscriptionFirstObj.getString("plan_quantity"),
            apiSubscriptionFirstObj.getString("plan_quantity"));
      Assert.assertEquals(esbSubscriptionFirstObj.getString("status"), apiSubscriptionFirstObj.getString("status"));
   }
   
   /**
    * Negative test case for listSubscriptions method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListSubscriptionsWithOptionalParameters" }, description = "chargebee {listSubscriptions} integration test with negative case.")
   public void testListSubscriptionsWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listSubscriptions");
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSubscriptions_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      
      final String apiEndPoint = apiUrl + "/subscriptions?limit=0";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
   }
   
   /**
    * Positive test case for cancelSubscription method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListSubscriptionsWithNegativeCase" }, description = "chargebee {cancelSubscription} integration test with mandatory parameters.")
   public void testCancelSubscriptionWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:cancelSubscription");
      
      final String apiEndPoint =
            apiUrl + "/subscriptions/" + connectorProperties.getProperty("subscriptionIdMandatory");
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_cancelSubscription_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      final RestResponse<JSONObject> apiRestResponseCancelled =
            sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponseCancelled.getHttpStatusCode(), 200);
      Assert.assertNotEquals(apiRestResponseCancelled.getBody().getJSONObject("subscription").getString("status"),
            apiRestResponse.getBody().getJSONObject("subscription").getString("status"));
      Assert.assertEquals(apiRestResponseCancelled.getBody().getJSONObject("subscription").getString("status"),
            "cancelled");
   }
   
   /**
    * Positive test case for cancelSubscription method with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCancelSubscriptionWithMandatoryParameters" }, description = "chargebee {cancelSubscription} integration test with optional parameters.")
   public void testCancelSubscriptionWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:cancelSubscription");
      
      final String apiEndPoint = apiUrl + "/subscriptions/" + connectorProperties.getProperty("subscriptionIdOptional");
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_cancelSubscription_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      final RestResponse<JSONObject> apiRestResponseCancelled =
            sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponseCancelled.getHttpStatusCode(), 200);
      
      // Check whether the in_trial status is not changed and scheduled to be cancelled.
      Assert.assertEquals(apiRestResponseCancelled.getBody().getJSONObject("subscription").getString("status"),
            apiRestResponse.getBody().getJSONObject("subscription").getString("status"));
      Assert.assertEquals(apiRestResponseCancelled.getBody().getJSONObject("subscription").getString("status"),
            "in_trial");
      Assert.assertFalse(apiRestResponse.getBody().getJSONObject("subscription").has("cancelled_at"));
      Assert.assertEquals(connectorProperties.getProperty("trialEndTimeStamp"), apiRestResponseCancelled.getBody()
            .getJSONObject("subscription").getString("cancelled_at"));
   }
   
   /**
    * Negative test case for cancelSubscription method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCancelSubscriptionWithOptionalParameters" }, description = "chargebee {cancelSubscription} integration test with negative case.")
   public void testCancelSubscriptionWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:cancelSubscription");
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_cancelSubscription_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 409);
      
      final String apiEndPoint =
            apiUrl + "/subscriptions/" + connectorProperties.getProperty("subscriptionIdMandatory") + "/cancel";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
   }
   
   /**
    * Positive test case for listInvoices method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateSubscriptionForCustomerWithOptionalParameters" }, description = "chargebee {listInvoices} integration test with mandatory parameters.")
   public void testListInvoicesWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listInvoices");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoices_mandatory.json");
      final int esbResponseArrayLength = esbRestResponse.getBody().getJSONArray("list").length();
      final JSONObject esbResponseObjectOne =
            esbRestResponse.getBody().getJSONArray("list").getJSONObject(0).getJSONObject("invoice");
      
      final String apiEndPoint = apiUrl + "/invoices";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      final int apiResponseArrayLength = apiRestResponse.getBody().getJSONArray("list").length();
      final JSONObject apiResponseObjectOne =
            apiRestResponse.getBody().getJSONArray("list").getJSONObject(0).getJSONObject("invoice");
      
      Assert.assertEquals(esbResponseArrayLength, apiResponseArrayLength);
      Assert.assertEquals(esbResponseObjectOne.getString("id"), apiResponseObjectOne.getString("id"));
      Assert.assertEquals(esbResponseObjectOne.getString("customer_id"), apiResponseObjectOne.getString("customer_id"));
      Assert.assertEquals(esbResponseObjectOne.getString("status"), apiResponseObjectOne.getString("status"));
      Assert.assertEquals(esbResponseObjectOne.getInt("amount"), apiResponseObjectOne.getInt("amount"));
   }
   
   /**
    * Positive test case for listInvoices method with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateSubscriptionForCustomerWithOptionalParameters" }, description = "chargebee {listInvoices} integration test with optional parameters.")
   public void testListInvoicesWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listInvoices");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoices_optional.json");
      
      final int esbResponseArrayLength = esbRestResponse.getBody().getJSONArray("list").length();
      final JSONObject esbResponseObjectOne =
            esbRestResponse.getBody().getJSONArray("list").getJSONObject(0).getJSONObject("invoice");
      
      final String paidOnAfter = connectorProperties.getProperty("paidOnAfter");
      final String apiEndPoint = apiUrl + "/invoices?limit=1&paid_on_after=" + paidOnAfter;
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      final int apiResponseArrayLength = apiRestResponse.getBody().getJSONArray("list").length();
      final JSONObject apiResponseObjectOne =
            apiRestResponse.getBody().getJSONArray("list").getJSONObject(0).getJSONObject("invoice");
      
      if (Integer.parseInt(paidOnAfter) > esbResponseObjectOne.getInt("paid_on")) {
         Assert.fail("Invoices which returns are not mathcing with paid)on parameter.");
      }
      Assert.assertEquals(esbResponseArrayLength, apiResponseArrayLength);
      Assert.assertEquals(esbResponseArrayLength, 1);
      Assert.assertEquals(esbResponseObjectOne.getString("id"), apiResponseObjectOne.getString("id"));
      Assert.assertEquals(esbResponseObjectOne.getString("customer_id"), apiResponseObjectOne.getString("customer_id"));
      
   }
   
   /**
    * Negative test case for listInvoices method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, description = "chargebee {listInvoices} integration test with negative case.")
   public void testListInvoicesWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listInvoices");
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoices_negative.json");
      
      final String apiEndPoint = apiUrl + "/invoices?limit=0";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
   }
   
   /**
    * Positive test case for listEvents method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateSubscriptionWithOptionalParameters" }, description = "chargebee {listEvents} integration test with mandatory parameters.")
   public void testListEventsWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listEvents");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listEvents_mandatory.json");
      
      final int esbResponseArrayLength = esbRestResponse.getBody().getJSONArray("list").length();
      final JSONObject esbResponseObjectOne =
            esbRestResponse.getBody().getJSONArray("list").getJSONObject(0).getJSONObject("event");
      final String apiEndPoint = apiUrl + "/events";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      String eventId = esbResponseObjectOne.getString("id");
      JSONObject apiResponseObjectOne = null;
      final int apiResponseArrayLength = apiRestResponse.getBody().getJSONArray("list").length();
      
      boolean isObjectMatch = false;
      // Iterate through api response until finding matching object.
      for (int i = 0; i < apiResponseArrayLength; i++) {
         apiResponseObjectOne = apiRestResponse.getBody().getJSONArray("list").getJSONObject(i).getJSONObject("event");
         if (apiResponseObjectOne.getString("id").equals(eventId)) {
            isObjectMatch = true;
            break;
         }
      }
      if (isObjectMatch) {
         Assert.assertEquals(esbResponseArrayLength, apiResponseArrayLength);
         Assert.assertEquals(esbResponseObjectOne.getString("id"), apiResponseObjectOne.getString("id"));
         Assert.assertEquals(esbResponseObjectOne.getString("event_type"), apiResponseObjectOne.getString("event_type"));
         Assert.assertEquals(esbResponseObjectOne.getInt("occurred_at"), apiResponseObjectOne.getInt("occurred_at"));
      } else {
         Assert.fail("No matching object found.");
      }
   }
   
   /**
    * Positive test case for listEvents method with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateSubscriptionWithOptionalParameters" }, description = "chargebee {listEvents} integration test with optional parameters.")
   public void testListEventsWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listEvents");
      
      String eventOccurredAfter = connectorProperties.getProperty("eventOccurredAfter");
      String eventOccurredBefore = connectorProperties.getProperty("eventOccurredBefore");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listEvents_optional.json");
      final String apiEndPoint =
            apiUrl + "/events?limit=1&start_time=" + eventOccurredAfter + "&end_time=" + eventOccurredBefore
                  + "&webhook_status=not_configured&event_type=customer_created";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      final int esbResponseArrayLength = esbRestResponse.getBody().getJSONArray("list").length();
      final JSONObject esbResponseObjectOne =
            esbRestResponse.getBody().getJSONArray("list").getJSONObject(0).getJSONObject("event");
      
      final int apiResponseArrayLength = apiRestResponse.getBody().getJSONArray("list").length();
      final JSONObject apiResponseObjectOne =
            apiRestResponse.getBody().getJSONArray("list").getJSONObject(0).getJSONObject("event");
      boolean isTimeBetween = false;
      if ((Integer.parseInt(eventOccurredAfter) <= esbResponseObjectOne.getInt("occurred_at"))
            && (Integer.parseInt(eventOccurredBefore) >= esbResponseObjectOne.getInt("occurred_at"))) {
         isTimeBetween = true;
      }
      
      Assert.assertEquals(esbResponseArrayLength, 1);
      Assert.assertEquals(esbResponseArrayLength, apiResponseArrayLength);
      Assert.assertEquals(esbResponseObjectOne.getString("id"), apiResponseObjectOne.getString("id"));
      Assert.assertEquals(esbResponseObjectOne.getString("event_type"), apiResponseObjectOne.getString("event_type"));
      Assert.assertTrue(isTimeBetween);
      Assert.assertEquals(esbResponseObjectOne.getInt("occurred_at"), apiResponseObjectOne.getInt("occurred_at"));
      
   }
   
   /**
    * Negative test case for listEvents method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, description = "chargebee {listEvents} integration test with negative case.")
   public void testListEventsWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listEvents");
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listEvents_negative.json");
      final String apiEndPoint = apiUrl + "/events?limit=0";
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
            apiRestResponse.getBody().getString("error_code"));
   }
   
}
