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

package org.wso2.carbon.connector.integration.test.proworkflow;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class ProworkflowConnectorIntegrationTest extends ConnectorIntegrationTestBase {
   
   private static Map<String, String> esbRequestHeadersMap;
   
   private static Map<String, String> apiRequestHeadersMap;
   
   private static String apiUrl, currentTimeString;
   
   /**
    * Set up the environment.
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {
   
      init("proworkflow-connector-1.0.0");
      
      esbRequestHeadersMap = new HashMap<String, String>();
      apiRequestHeadersMap = new HashMap<String, String>();
      esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
      esbRequestHeadersMap.put("Content-Type", "application/json");
      
      // Create base64-encoded authorization header
      final String authString =
            connectorProperties.getProperty("username") + ":" + connectorProperties.getProperty("password");
      final String base64AuthString = Base64.encode(authString.getBytes());
      apiRequestHeadersMap.put("Authorization", "Basic " + base64AuthString);
      
      esbRequestHeadersMap.put("apikey", connectorProperties.getProperty("apiKey"));
      apiRequestHeadersMap.putAll(esbRequestHeadersMap);
      
      apiUrl = connectorProperties.getProperty("apiUrl");
      currentTimeString = String.valueOf(System.currentTimeMillis());
      connectorProperties.setProperty("currentTimeString", currentTimeString);
   }
   
   /**
    * Positive test case for createCompanies method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, description = "proworkflow {createCompanies} integration test with mandatory parameters.")
   public void testCreateCompaniesWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createCompanies");
      final String companyName = "companyNameMandatory_" + currentTimeString;
      connectorProperties.setProperty("companyNameMandatory", companyName);
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCompanies_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), "Success");
      final String companyIdMandatory =
            esbRestResponse.getBody().getJSONArray("details").getJSONObject(0).getString("id");
      connectorProperties.setProperty("companyIdMandatory", companyIdMandatory);
      
      String apiEndPoint = apiUrl + "/companies/" + companyIdMandatory;
      
      final JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getString("status"), "Success");
      JSONObject apiJSONData = apiJSONResponse.getJSONObject("company");
      
      Assert.assertEquals(companyName, apiJSONData.getString("name"));
   }
   
   /**
    * Positive test case for createCompanies method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCompaniesWithMandatoryParameters" }, description = "proworkflow {createCompanies} integration test with optional parameters.")
   public void testCreateCompaniesWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createCompanies");
      final String companyName = "companyNameOptional_" + currentTimeString;
      connectorProperties.setProperty("companyNameOptional", companyName);
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCompanies_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), "Success");
      final String companyIdOptional =
            esbRestResponse.getBody().getJSONArray("details").getJSONObject(0).getString("id");
      connectorProperties.setProperty("companyIdOptional", companyIdOptional);
      
      final String apiEndPoint = apiUrl + "/companies/" + companyIdOptional;
      
      final JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getString("status"), "Success");
      final JSONObject apiJSONData = apiJSONResponse.getJSONObject("company");
      
      Assert.assertEquals(companyName, apiJSONData.getString("name"));
      Assert.assertEquals(connectorProperties.getProperty("city"), apiJSONData.getString("city"));
      Assert.assertEquals(connectorProperties.getProperty("country"), apiJSONData.getString("country"));
      Assert.assertEquals(connectorProperties.getProperty("email"), apiJSONData.getString("email"));
      Assert.assertEquals(connectorProperties.getProperty("phoneNumber"), apiJSONData.getString("phone"));
      Assert.assertEquals(connectorProperties.getProperty("zipCode"), apiJSONData.getString("zipcode"));
   }
   
   /**
    * Negative test case for createCompanies method.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCompaniesWithOptionalParameters" }, description = "proworkflow {createCompanies} integration test with negative case.")
   public void testCreateCompaniesNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createCompanies");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCompanies_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), "Error");
      
      final String apiEndPoint = apiUrl + "/companies";
      
      final RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createCompanies_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getBody().getString("status"), "Error");
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("details").getString(0), apiRestResponse.getBody()
            .getJSONArray("details").getString(0));
      
   }
   
   /**
    * Positive test case for createContacts method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCompaniesNegativeCase" }, description = "proworkflow {createContacts} integration test with mandatory parameters.")
   public void testCreateContactsWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createContacts");
      final String firstName = "contactFirstNameMandatory_" + currentTimeString;
      connectorProperties.setProperty("contactFirstNameMandatory", firstName);
      final String lastName = "contactLastNameMandatory_" + currentTimeString;
      connectorProperties.setProperty("contactLastNameMandatory", lastName);
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContacts_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), "Success");
      final String contactIdMandatory =
            esbRestResponse.getBody().getJSONArray("details").getJSONObject(0).getString("id");
      connectorProperties.setProperty("contactIdMandatory", contactIdMandatory);
      
      final String apiEndPoint = apiUrl + "/contacts/" + contactIdMandatory;
      
      final JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getString("status"), "Success");
      final JSONObject apiJSONData = apiJSONResponse.getJSONObject("contact");
      
      Assert.assertEquals(firstName, apiJSONData.getString("firstname"));
      Assert.assertEquals(lastName, apiJSONData.getString("lastname"));
      Assert.assertEquals(connectorProperties.getProperty("companyIdMandatory"), apiJSONData.getString("companyid"));
   }
   
   /**
    * Positive test case for createContacts method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactsWithMandatoryParameters" }, description = "proworkflow {createContacts} integration test with optional parameters.")
   public void testCreateContactsWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createContacts");
      final String firstName = "contactFirstNameOptional_" + currentTimeString;
      connectorProperties.setProperty("contactFirstNameOptional", firstName);
      final String lastName = "contactLastNameOptional_" + currentTimeString;
      connectorProperties.setProperty("contactLastNameOptional", lastName);
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContacts_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), "Success");
      final String contactIdOptional =
            esbRestResponse.getBody().getJSONArray("details").getJSONObject(0).getString("id");
      connectorProperties.setProperty("contactIdOptional", contactIdOptional);
      
      final String apiEndPoint = apiUrl + "/contacts/" + contactIdOptional;
      
      final JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getString("status"), "Success");
      final JSONObject apiJSONData = apiJSONResponse.getJSONObject("contact");
      
      Assert.assertEquals(firstName, apiJSONData.getString("firstname"));
      Assert.assertEquals(lastName, apiJSONData.getString("lastname"));
      Assert.assertEquals(connectorProperties.getProperty("city"), apiJSONData.getString("city"));
      Assert.assertEquals(connectorProperties.getProperty("country"), apiJSONData.getString("country"));
      Assert.assertEquals(connectorProperties.getProperty("email"), apiJSONData.getString("email"));
      Assert.assertEquals(connectorProperties.getProperty("phoneNumber"), apiJSONData.getString("mobilephone"));
      Assert.assertEquals(connectorProperties.getProperty("zipCode"), apiJSONData.getString("zipcode"));
   }
   
   /**
    * Negative test case for createContacts method.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactsWithOptionalParameters" }, description = "proworkflow {createContacts} integration test with negative case.")
   public void testCreateContactsNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createContacts");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContacts_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), "Error");
      
      final String apiEndPoint = apiUrl + "/contacts";
      
      final RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createContacts_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getBody().getString("status"), "Error");
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("details").getString(0), apiRestResponse.getBody()
            .getJSONArray("details").getString(0));
   }
   
   /**
    * Positive test case for getContact method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactsNegativeCase" }, description = "proworkflow {getContact} integration test with mandatory parameters.")
   public void testGetContactWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getContact");
      
      final JSONObject esbJSONResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContact_mandatory.json").getBody();
      Assert.assertEquals(esbJSONResponse.getString("status"), "Success");
      final JSONObject esbJSONData = esbJSONResponse.getJSONObject("contact");
      
      final String apiEndPoint = apiUrl + "/contacts/" + connectorProperties.getProperty("contactIdMandatory");
      
      final JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getString("status"), "Success");
      final JSONObject apiJSONData = apiJSONResponse.getJSONObject("contact");
      
      Assert.assertEquals(esbJSONData.getString("id"), apiJSONData.getString("id"));
      Assert.assertEquals(esbJSONData.getString("lastname"), apiJSONData.getString("lastname"));
      Assert.assertEquals(esbJSONData.getString("lastmodified"), apiJSONData.getString("lastmodified"));
   }
   
   /**
    * Negative test case for getContact method.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetContactWithMandatoryParameters" }, description = "proworkflow {getContact} integration test with negative case.")
   public void testGetContactNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getContact");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContact_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), "Error");
      
      final String apiEndPoint = apiUrl + "/contacts/INVALID";
      
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getBody().getString("status"), "Error");
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("details").getString(0), apiRestResponse.getBody()
            .getJSONArray("details").getString(0));
   }
   
   /**
    * Positive test case for createQuote method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetContactNegativeCase" }, description = "proworkflow {createQuote} integration test with mandatory parameters.")
   public void testCreateQuoteWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createQuote");
      final String quoteTitle = "quoteTitleMandatory_" + currentTimeString;
      connectorProperties.setProperty("quoteTitleMandatory", quoteTitle);
      final String lineItemName = "lineItemNameMandatory_" + currentTimeString;
      connectorProperties.setProperty("lineItemNameMandatory", lineItemName);
      final String fixedCostItemsName = "fixedCostItemsNameMandatory_" + currentTimeString;
      connectorProperties.setProperty("fixedCostItemsNameMandatory", fixedCostItemsName);
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createQuote_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), "Success");
      final String quoteIdMandatory =
            esbRestResponse.getBody().getJSONArray("details").getJSONObject(0).getString("id");
      connectorProperties.setProperty("quoteIdMandatory", quoteIdMandatory);
      
      final String apiEndPoint = apiUrl + "/quotes/" + quoteIdMandatory;
      
      final JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getString("status"), "Success");
      final JSONObject apiJSONData = apiJSONResponse.getJSONObject("quote");
      
      Assert.assertEquals(quoteTitle, apiJSONData.getString("title"));
      Assert.assertEquals(lineItemName, apiJSONData.getJSONArray("lineitems").getJSONObject(0).getString("name"));
      Assert.assertEquals(Float.valueOf(connectorProperties.getProperty("lineItemCost")).toString(), apiJSONData
            .getJSONArray("lineitems").getJSONObject(0).getString("cost"));
      Assert.assertEquals(fixedCostItemsName,
            apiJSONData.getJSONArray("fixedcostitems").getJSONObject(0).getString("name"));
      Assert.assertEquals(Float.valueOf(connectorProperties.getProperty("fixedCostItemsCost")).toString(), apiJSONData
            .getJSONArray("fixedcostitems").getJSONObject(0).getString("cost"));
   }
   
   /**
    * Positive test case for createQuote method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateQuoteWithMandatoryParameters" }, description = "proworkflow {createQuote} integration test with optional parameters.")
   public void testCreateQuoteWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createQuote");
      final String quoteTitle = "quoteTitleOptional_" + currentTimeString;
      connectorProperties.setProperty("quoteTitleOptional", quoteTitle);
      final String lineItemName = "lineItemNameOptional_" + currentTimeString;
      connectorProperties.setProperty("lineItemNameOptional", lineItemName);
      final String fixedCostItemsName = "fixedCostItemsNameOptional_" + currentTimeString;
      connectorProperties.setProperty("fixedCostItemsNameOptional", fixedCostItemsName);
      final String quoteDescription = "quoteDescription_" + currentTimeString;
      connectorProperties.setProperty("quoteDescription", quoteDescription);
      final String discountDescription = "discountDescription_" + currentTimeString;
      connectorProperties.setProperty("discountDescription", discountDescription);
      final String quoteNumber = "quoteNumber_" + currentTimeString;
      connectorProperties.setProperty("quoteNumber", quoteNumber);
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createQuote_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), "Success");
      final String quoteIdOptional = esbRestResponse.getBody().getJSONArray("details").getJSONObject(0).getString("id");
      connectorProperties.setProperty("quoteIdOptional", quoteIdOptional);
      
      final String apiEndPoint = apiUrl + "/quotes/" + quoteIdOptional;
      
      final JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getString("status"), "Success");
      final JSONObject apiJSONData = apiJSONResponse.getJSONObject("quote");
      
      Assert.assertEquals(quoteTitle, apiJSONData.getString("title"));
      Assert.assertEquals(lineItemName, apiJSONData.getJSONArray("lineitems").getJSONObject(0).getString("name"));
      Assert.assertEquals(Float.valueOf(connectorProperties.getProperty("lineItemCost")).toString(), apiJSONData
            .getJSONArray("lineitems").getJSONObject(0).getString("cost"));
      Assert.assertEquals(quoteDescription, apiJSONData.getString("description"));
      Assert.assertEquals(discountDescription, apiJSONData.getString("discountdescription"));
      Assert.assertEquals(connectorProperties.getProperty("quoteNumber"), apiJSONData.getString("number"));
      Assert.assertEquals(connectorProperties.getProperty("taxRate"), apiJSONData.getString("taxrate"));
      Assert.assertEquals(Float.valueOf(connectorProperties.getProperty("quoteDiscountValue")).toString(),
            apiJSONData.getString("discountvalue"));
   }
   
   /**
    * Negative test case for createQuote method.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateQuoteWithOptionalParameters" }, description = "proworkflow {createQuote} integration test with negative case.")
   public void testCreateQuoteNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createQuote");
      final String quoteTitle = "quoteTitleNegative_" + currentTimeString;
      connectorProperties.setProperty("quoteTitleNegative", quoteTitle);
      final String lineItemName = "lineItemNameNegative_" + currentTimeString;
      connectorProperties.setProperty("lineItemNameNegative", lineItemName);
      final String fixedCostItemsName = "fixedCostItemsNameNegative_" + currentTimeString;
      connectorProperties.setProperty("fixedCostItemsNameNegative", fixedCostItemsName);
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createQuote_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), "Error");
      
      final String apiEndPoint = apiUrl + "/quotes";
      
      final RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createQuote_negative.json");
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getBody().getString("status"), "Error");
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("details").getString(0), apiRestResponse.getBody()
            .getJSONArray("details").getString(0));
   }
   
   /**
    * Positive test case for getQuote method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateQuoteNegativeCase" }, description = "proworkflow {getQuote} integration test with mandatory parameters.")
   public void testGetQuoteWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getQuote");
      
      final JSONObject esbJSONResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getQuote_mandatory.json").getBody();
      Assert.assertEquals(esbJSONResponse.getString("status"), "Success");
      final JSONObject esbJSONData = esbJSONResponse.getJSONObject("quote");
      
      final String apiEndPoint = apiUrl + "/quotes/" + connectorProperties.getProperty("quoteIdMandatory");
      
      final JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getString("status"), "Success");
      final JSONObject apiJSONData = apiJSONResponse.getJSONObject("quote");
      
      Assert.assertEquals(esbJSONData.getString("id"), apiJSONData.getString("id"));
      Assert.assertEquals(esbJSONData.getString("title"), apiJSONData.getString("title"));
      Assert.assertEquals(esbJSONData.getString("lastmodified"), apiJSONData.getString("lastmodified"));
   }
   
   /**
    * Negative test case for getQuote method.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetQuoteWithMandatoryParameters" }, description = "proworkflow {getQuote} integration test with negative case.")
   public void testGetQuoteNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getQuote");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getQuote_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), "Error");
      
      final String apiEndPoint = apiUrl + "/quotes/INVALID";
      
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getBody().getString("status"), "Error");
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("details").getString(0), apiRestResponse.getBody()
            .getJSONArray("details").getString(0));
   }
   
   /**
    * Positive test case for listQuotes method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetQuoteNegativeCase" }, description = "proworkflow {listQuotes} integration test with mandatory parameters.")
   public void testListQuotesWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listQuotes");
      
      final JSONObject esbJSONResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listQuotes_mandatory.json").getBody();
      Assert.assertEquals(esbJSONResponse.getString("status"), "Success");
      final JSONArray esbJSONArray = esbJSONResponse.getJSONArray("quotes");
      
      final String apiEndPoint = apiUrl + "/quotes";
      
      final JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getString("status"), "Success");
      final JSONArray apiJSONArray = apiJSONResponse.getJSONArray("quotes");
      
      Assert.assertEquals(esbJSONResponse.getString("count"), apiJSONResponse.getString("count"));
      Assert.assertEquals(esbJSONArray.getJSONObject(0).getString("id"), apiJSONArray.getJSONObject(0).getString("id"));
      Assert.assertEquals(esbJSONArray.getJSONObject(0).getString("title"),
            apiJSONArray.getJSONObject(0).getString("title"));
      Assert.assertEquals(esbJSONArray.getJSONObject(0).getString("quoteddate"), apiJSONArray.getJSONObject(0)
            .getString("quoteddate"));
   }
   
   /**
    * Positive test case for listQuotes method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListQuotesWithMandatoryParameters" }, description = "proworkflow {listQuotes} integration test with optional parameters.")
   public void testListQuotesWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listQuotes");
      
      final JSONObject esbJSONResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listQuotes_optional.json").getBody();
      Assert.assertEquals(esbJSONResponse.getString("status"), "Success");
      final JSONArray esbJSONArray = esbJSONResponse.getJSONArray("quotes");
      
      final String apiEndPoint =
            apiUrl + "/quotes?fields=all&searchdescription=" + currentTimeString + "&searchnumber=" + currentTimeString
                  + "&searchtitle=" + currentTimeString + "&taxable=true&type=quote";
      
      final JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getString("status"), "Success");
      final JSONArray apiJSONArray = apiJSONResponse.getJSONArray("quotes");
      
      Assert.assertEquals(esbJSONResponse.getString("count"), apiJSONResponse.getString("count"));
      Assert.assertEquals(esbJSONArray.getJSONObject(0).getString("id"), apiJSONArray.getJSONObject(0).getString("id"));
      Assert.assertEquals(esbJSONArray.getJSONObject(0).getString("title"),
            apiJSONArray.getJSONObject(0).getString("title"));
      Assert.assertTrue(apiJSONArray.getJSONObject(0).getString("title").contains(currentTimeString));
      
      Assert.assertEquals(esbJSONArray.getJSONObject(0).getString("description"), apiJSONArray.getJSONObject(0)
            .getString("description"));
      Assert.assertTrue(apiJSONArray.getJSONObject(0).getString("description").contains(currentTimeString));
      Assert.assertEquals(esbJSONArray.getJSONObject(0).getString("number"),
            apiJSONArray.getJSONObject(0).getString("number"));
      Assert.assertTrue(apiJSONArray.getJSONObject(0).getString("number").contains(currentTimeString));
      Assert.assertEquals(esbJSONArray.getJSONObject(0).getString("type"),
            apiJSONArray.getJSONObject(0).getString("type"));
      Assert.assertEquals(apiJSONArray.getJSONObject(0).getString("type"), "Quote");
      Assert.assertEquals(esbJSONArray.getJSONObject(0).getString("taxable"),
            apiJSONArray.getJSONObject(0).getString("taxable"));
      Assert.assertEquals(apiJSONArray.getJSONObject(0).getBoolean("taxable"), true);
      
   }
   
   /**
    * Negative test case for listQuotes method.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListQuotesWithOptionalParameters" }, description = "proworkflow {listQuotes} integration test with negative case.")
   public void testListQuotesNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listQuotes");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listQuotes_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), "Error");
      
      final String apiEndPoint = apiUrl + "/quotes?type=INVALID";
      
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getBody().getString("status"), "Error");
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("details").getString(0), apiRestResponse.getBody()
            .getJSONArray("details").getString(0));
   }
   
   /**
    * Positive test case for getCompany method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListQuotesNegativeCase" }, description = "proworkflow {getCompany} integration test with mandatory parameters.")
   public void testGetCompanyWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getCompany");
      
      final JSONObject esbJSONResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCompany_mandatory.json").getBody();
      Assert.assertEquals(esbJSONResponse.getString("status"), "Success");
      final JSONObject esbJSONData = esbJSONResponse.getJSONObject("company");
      
      final String apiEndPoint = apiUrl + "/companies/" + connectorProperties.getProperty("companyIdMandatory");
      
      final JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getString("status"), "Success");
      final JSONObject apiJSONData = apiJSONResponse.getJSONObject("company");
      
      Assert.assertEquals(esbJSONData.getString("id"), apiJSONData.getString("id"));
      Assert.assertEquals(esbJSONData.getString("name"), apiJSONData.getString("name"));
      Assert.assertEquals(esbJSONData.getString("type"), apiJSONData.getString("type"));
   }
   
   /**
    * Negative test case for getCompany method.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetCompanyWithMandatoryParameters" }, description = "proworkflow {getCompany} integration test with negative case.")
   public void testGetCompanyNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getCompany");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCompany_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), "Error");
      
      final String apiEndPoint = apiUrl + "/companies/INVALID";
      
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getBody().getString("status"), "Error");
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("details").getString(0), apiRestResponse.getBody()
            .getJSONArray("details").getString(0));
   }
   
   /**
    * Positive test case for listContacts method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetCompanyNegativeCase" }, description = "proworkflow {listContacts} integration test with mandatory parameters.")
   public void testListContactsWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listContacts");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_mandatory.json");
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), "Success");
      final JSONArray esbJSONData = esbRestResponse.getBody().getJSONArray("contacts");
      final String apiEndPoint = apiUrl + "/contacts";
      
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getBody().getString("status"), "Success");
      
      final JSONArray apiJSONData = apiRestResponse.getBody().getJSONArray("contacts");
      Assert.assertEquals(esbJSONData.getJSONObject(0).getInt("id"), apiJSONData.getJSONObject(0).getInt("id"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("firstname"),
            apiJSONData.getJSONObject(0).getString("firstname"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("lastname"),
            apiJSONData.getJSONObject(0).getString("lastname"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getInt("companyid"),
            apiJSONData.getJSONObject(0).getInt("companyid"));
      Assert.assertEquals(esbJSONData.length(), apiJSONData.length());
   }
   
   /**
    * Positive test case for listContacts method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListContactsWithMandatoryParameters" }, description = "proworkflow {listContacts} integration test with optional parameters.")
   public void testListContactsWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listContacts");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_optional.json");
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), "Success");
      final JSONArray esbJSONData = esbRestResponse.getBody().getJSONArray("contacts");
      final String apiEndPoint =
            apiUrl + "/contacts?fields=all&search=" + currentTimeString + "&searchname=" + currentTimeString
                  + "&sortorder=asc&pending=false&type=client";
      
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getBody().getString("status"), "Success");
      
      final JSONArray apiJSONData = apiRestResponse.getBody().getJSONArray("contacts");
      Assert.assertEquals(esbJSONData.getJSONObject(0).getInt("id"), apiJSONData.getJSONObject(0).getInt("id"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("firstname"),
            apiJSONData.getJSONObject(0).getString("firstname"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("lastname"),
            apiJSONData.getJSONObject(0).getString("lastname"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getInt("companyid"),
            apiJSONData.getJSONObject(0).getInt("companyid"));
      Assert.assertEquals(esbJSONData.length(), apiJSONData.length());
   }
   
   /**
    * Negative test case for listContacts method.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListContactsWithOptionalParameters" }, description = "proworkflow {listContacts} integration test with negative case.")
   public void testListContactsNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listContacts");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), "Error");
      
      final String apiEndPoint = apiUrl + "/contacts?type=INVALID";
      
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getBody().getString("status"), "Error");
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("details").getString(0), apiRestResponse.getBody()
            .getJSONArray("details").getString(0));
   }
   
   /**
    * Positive test case for listCompanies method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListContactsNegativeCase" }, description = "proworkflow {listCompanies} integration test with mandatory parameters.")
   public void testListCompaniesWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listCompanies");
      
      final JSONObject esbJSONResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCompanies_mandatory.json").getBody();
      
      Assert.assertEquals(esbJSONResponse.getString("status"), "Success");
      final JSONArray esbJSONData = esbJSONResponse.getJSONArray("companies");
      
      final String apiEndPoint = apiUrl + "/companies";
      
      final JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getString("status"), "Success");
      final JSONArray apiJSONData = apiJSONResponse.getJSONArray("companies");
      
      Assert.assertEquals(esbJSONData.getJSONObject(0).getInt("id"), apiJSONData.getJSONObject(0).getInt("id"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("name"), apiJSONData.getJSONObject(0)
            .getString("name"));
      Assert.assertEquals(esbJSONResponse.getString("count"), apiJSONResponse.getString("count"));
   }
   
   /**
    * Positive test case for listCompanies method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListCompaniesWithMandatoryParameters" }, description = "proworkflow {listCompanies} integration test with optional parameters.")
   public void testListCompaniesWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listCompanies");
      
      final JSONObject esbJSONResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCompanies_optional.json").getBody();
      Assert.assertEquals(esbJSONResponse.getString("status"), "Success");
      final JSONArray esbJSONData = esbJSONResponse.getJSONArray("companies");
      
      final String apiEndPoint =
            apiUrl + "/companies?fields=name,type,email&id=" + connectorProperties.getProperty("companyIdMandatory")
                  + "," + connectorProperties.getProperty("companyIdOptional")
                  + "&type=client&searchname=companyName&searchemail=" + connectorProperties.getProperty("email");
      
      final JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getString("status"), "Success");
      final JSONArray apiJSONData = apiJSONResponse.getJSONArray("companies");
      
      Assert.assertEquals(esbJSONData.getJSONObject(0).getInt("id"), apiJSONData.getJSONObject(0).getInt("id"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("name"), apiJSONData.getJSONObject(0)
            .getString("name"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("type"), apiJSONData.getJSONObject(0)
            .getString("type"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("email"),
            apiJSONData.getJSONObject(0).getString("email"));
      Assert.assertEquals(esbJSONResponse.getString("count"), apiJSONResponse.getString("count"));
   }
   
   /**
    * Negative test case for listCompanies method.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListCompaniesWithOptionalParameters" }, description = "proworkflow {listCompanies} integration test with negative case.")
   public void testListCompaniesNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listCompanies");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCompanies_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), "Error");
      
      final String apiEndPoint = apiUrl + "/companies?type=INVALID";
      
      final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getBody().getString("status"), "Error");
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("details").getString(0), apiRestResponse.getBody()
            .getJSONArray("details").getString(0));
   }
   
   /**
    * Positive test case for updateQuote method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListCompaniesNegativeCase" }, description = "proworkflow {updateQuote} integration test with optional parameters.")
   public void testUpdateQuoteWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateQuote");
      
      final String apiEndPoint = apiUrl + "/quotes/" + connectorProperties.getProperty("quoteIdOptional");
      
      final RestResponse<JSONObject> apiRestResponseBeforeUpdate =
            sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponseBeforeUpdate.getBody().getString("status"), "Success");
      final JSONObject apiJSONDataBeforeUpdate = apiRestResponseBeforeUpdate.getBody().getJSONObject("quote");
      
      final String esbUpdateQuoteTitle = "esbUpdateQuoteTitle" + currentTimeString;
      connectorProperties.setProperty("esbUpdateQuoteTitle", esbUpdateQuoteTitle);
      connectorProperties
            .setProperty("quoteUpdateDiscountDescription", "quoteDiscountDescription_" + currentTimeString);
      connectorProperties.setProperty("quoteUpdateNumber", "quoteUpdateNumber_" + currentTimeString);
      
      final JSONObject esbJSONResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateQuote_optional.json").getBody();
      Assert.assertEquals(esbJSONResponse.getString("status"), "Success");
      
      final JSONObject apiJSONResponseAfterUpdate =
            sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponseAfterUpdate.getString("status"), "Success");
      final JSONObject apiJSONDataAfterUpdate = apiJSONResponseAfterUpdate.getJSONObject("quote");
      
      Assert.assertEquals(esbUpdateQuoteTitle, apiJSONDataAfterUpdate.getString("title"));
      Assert.assertNotEquals(apiJSONDataBeforeUpdate.getString("discountvalue"),
            apiJSONDataAfterUpdate.getString("discountvalue"));
      Assert.assertNotEquals(apiJSONDataBeforeUpdate.getString("status"), apiJSONDataAfterUpdate.getString("status"));
      Assert.assertNotEquals(apiJSONDataBeforeUpdate.getString("description"),
            apiJSONDataAfterUpdate.getString("description"));
      Assert.assertNotEquals(apiJSONDataBeforeUpdate.getString("number"), apiJSONDataAfterUpdate.getString("number"));
   }
   
   /**
    * Negative test case for updateQuote method.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateQuoteWithOptionalParameters" }, description = "proworkflow {updateQuote} integration test with negative case.")
   public void testUpdateQuoteWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateQuote");
      
      final RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateQuote_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), "Error");
      
      final String apiEndPoint = apiUrl + "/quotes/" + connectorProperties.getProperty("quoteIdOptional");
      
      final RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateQuote_negative.json");
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getBody().getString("status"), "Error");
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("details").getString(0), apiRestResponse.getBody()
            .getJSONArray("details").getString(0));
   }
}
