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

package org.wso2.carbon.connector.integration.test.hubspot;

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

public class HubspotConnectorIntegrationTest extends ConnectorIntegrationTestBase {
   
   private Map<String, String> esbRequestHeadersMap;
   
   private Map<String, String> apiRequestHeadersMap;
   
   private Map<String, String> parametersMap;
   
   private String apiUrl;
   
   private String apiKey;
   
   private long currentTimeString;
   
   /**
    * Set up the environment.
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {
   
      init("hubspot-connector-1.0.0");
      
      esbRequestHeadersMap = new HashMap<String, String>();
      apiRequestHeadersMap = new HashMap<String, String>();
      parametersMap = new HashMap<String, String>();
      
      esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
      esbRequestHeadersMap.put("Content-Type", "application/json");
      
      apiRequestHeadersMap.putAll(esbRequestHeadersMap);
      
      apiUrl = connectorProperties.getProperty("apiUrl");
      apiKey = connectorProperties.getProperty("apiKey");
      
      setProperties();
      createPrerequisites();
      
   }
   
   /**
    * Method to set dynamic properties for test cases.
    */
   private void setProperties() {
      
      currentTimeString = System.currentTimeMillis();
   
      final String emailMandatory = "hubspot_man" + currentTimeString + "@hubspot.com";
      connectorProperties.put("emailMandatory", emailMandatory);
      
      final String emailOptional = "hubspot_opt" + currentTimeString + "@hubspot.com";
      connectorProperties.put("emailOptional", emailOptional);
        
   }
   
   /**
    * Method to create Pre-Requisites to execute test suite.
    * @throws JSONException 
    * @throws IOException 
    */
   private void createPrerequisites() throws IOException, JSONException{
      String apiEndpoint =
            apiUrl + "/deals/v1/deal/recent/created?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      if(apiRestResponse.getBody().getJSONArray("results").length() == 0){
         apiEndpoint =
               apiUrl + "/deals/v1/deal?hapikey=" + apiKey;
         apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,"api_createDeal.json");
         Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200,"pre-requisites failed. Please create at least one deal before run.");
      }
      
      apiEndpoint =
            apiUrl + "/companies/v2/companies/recent/created?hapikey=" + apiKey;
      apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      if(apiRestResponse.getBody().getJSONArray("results").length() == 0){
         apiEndpoint =
               apiUrl + "/companies/v2/companies?hapikey=" + apiKey;
         apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,"api_createCompany.json");
         Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200,"pre-requisites failed. Please create at least one company before run.");
      }
   }
   
   /**
    * Positive test case for createContact method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "hubspot {createContact} integration test with mandatory parameters.")
   public void testCreateContactWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createContact");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_mandatory.json");
      final String contactId = esbRestResponse.getBody().getString("vid");
      connectorProperties.put("contactIdMandatory", contactId);
      
      final String apiEndpoint =
            apiUrl + "/contacts/v1/contact/vid/" + contactId + "/profile?hapikey=" + apiKey + "&property=email";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(connectorProperties.getProperty("emailMandatory"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("email").getString("value"));
      
   }
   
   /**
    * Positive test case for createContact with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "hubspot {createContact} integration test with optional parameters.")
   public void testCreateContactWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createContact");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_optional.json");
      final String contactId = esbRestResponse.getBody().getString("vid");
      connectorProperties.put("contactIdOptional", contactId);
      
      final String apiEndpoint = apiUrl + "/contacts/v1/contact/vid/" + contactId + "/profile?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(connectorProperties.getProperty("emailOptional"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("email").getString("value"));
      Assert.assertEquals(connectorProperties.getProperty("contactFirstName"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("firstname").getString("value"));
      Assert.assertEquals(connectorProperties.getProperty("contactLastName"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("lastname").getString("value"));
      Assert.assertEquals(connectorProperties.getProperty("contactWebsite"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("website").getString("value"));
      Assert.assertEquals(connectorProperties.getProperty("contactAddress"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("phone").getString("value"));
      Assert.assertEquals(connectorProperties.getProperty("contactPhone"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("address").getString("value"));
      
   }
   
   /**
    * Negative test case for createContact method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, description = "hubspot {createContact} integration test with negative case.")
   public void testCreateContactWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createContact");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_negative.json");
      
      final  String apiEndpoint = apiUrl + "/contacts/v1/contact?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createContact_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 409);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 409);
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
   }
   
   /**
    * Positive test case for getContactById method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, description = "hubspot {getContactById} integration test with mandatory parameters.")
   public void testGetContactByIdMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getContactById");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactById_mandatory.json");
      
      final String apiEndpoint =
            apiUrl + "/contacts/v1/contact/vid/" + connectorProperties.getProperty("contactIdOptional")
                  + "/profile?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONObject("properties").getJSONObject("email").getString("value"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("email").getString("value"));
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONObject("properties").getJSONObject("firstname").getString("value"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("firstname").getString("value"));
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONObject("properties").getJSONObject("lastname").getString("value"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("lastname").getString("value"));
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONObject("properties").getJSONObject("website").getString("value"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("website").getString("value"));
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONObject("properties").getJSONObject("phone").getString("value"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("phone").getString("value"));
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONObject("properties").getJSONObject("address").getString("value"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("address").getString("value"));
      
   }
   
   /**
    * Positive test case for getContactById method with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, description = "hubspot {getContactById} integration test with optional parameters.")
   public void testGetContactByIdOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getContactById");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactById_optional.json");
      
      final String apiEndpoint =
            apiUrl + "/contacts/v1/contact/vid/" + connectorProperties.getProperty("contactIdOptional")
                  + "/profile?hapikey=" + apiKey
                  + "&property=firstname&property=lastname&propertyMode=value_and_history";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONObject("properties").getJSONObject("firstname").getString("value"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("firstname").getString("value"));
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONObject("properties").getJSONObject("lastname").getString("value"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("lastname").getString("value"));
      Assert.assertFalse(esbRestResponse.getBody().getJSONObject("properties").has("website"));
      Assert.assertFalse(apiRestResponse.getBody().getJSONObject("properties").has("website"));
      Assert.assertFalse(esbRestResponse.getBody().getJSONObject("properties").has("phone"));
      Assert.assertFalse(apiRestResponse.getBody().getJSONObject("properties").has("phone"));
      Assert.assertTrue(esbRestResponse.getBody().getJSONObject("properties").getJSONObject("firstname")
            .has("versions"));
      Assert.assertTrue(apiRestResponse.getBody().getJSONObject("properties").getJSONObject("firstname")
            .has("versions"));
      Assert.assertTrue(esbRestResponse.getBody().getJSONObject("properties").getJSONObject("lastname").has("versions"));
      Assert.assertTrue(apiRestResponse.getBody().getJSONObject("properties").getJSONObject("lastname").has("versions"));
      
   }
   
   /**
    * Negative test case for getContactById method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, description = "hubspot {getContactById} integration test with negative case.")
   public void testGetContactByIdWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getContactById");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactById_negative.json");
      
      final String apiEndpoint =
            apiUrl + "/contacts/v1/contact/vid/" + connectorProperties.getProperty("contactIdOptional")
                  + "/profile?hapikey=" + apiKey + "&propertyMode=value";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
   }
   
   /**
    * Positive test case for listContacts method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithMandatoryParameters",
         "testCreateContactWithOptionalParameters" }, description = "hubspot {listContacts} integration test with mandatory parameters.")
   public void testListContactsWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listContacts");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_mandatory.json");
      
      final String apiEndpoint = apiUrl + "/contacts/v1/lists/all/contacts/all?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("contacts").length(), apiRestResponse.getBody()
            .getJSONArray("contacts").length());
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("vid"),
            apiRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("vid"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("addedAt"),
            apiRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("addedAt"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("is-contact"),
            apiRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("is-contact"));
      Assert.assertEquals(esbRestResponse.getBody().getString("has-more"),
            apiRestResponse.getBody().getString("has-more"));
      Assert.assertEquals(esbRestResponse.getBody().getString("vid-offset"),
            apiRestResponse.getBody().getString("vid-offset"));
      
   }
   
   /**
    * Positive test case for listContacts method with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithMandatoryParameters",
         "testCreateContactWithOptionalParameters" }, description = "hubspot {listContacts} integration test with optional parameters.")
   public void testListContactsWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listContacts");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_optional.json");
      
      final String apiEndpoint =
            apiUrl + "/contacts/v1/lists/all/contacts/all?hapikey=" + apiKey
                  + "&property=email&propertyMode=value_and_history&count=1&vidOffset=1";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("contacts").length(), 1);
      Assert.assertEquals(apiRestResponse.getBody().getJSONArray("contacts").length(), 1);
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("vid"),
            apiRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("vid"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("addedAt"),
            apiRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("addedAt"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("is-contact"),
            apiRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("is-contact"));
      Assert.assertEquals(esbRestResponse.getBody().getString("has-more"),
            apiRestResponse.getBody().getString("has-more"));
      Assert.assertEquals(esbRestResponse.getBody().getString("vid-offset"),
            apiRestResponse.getBody().getString("vid-offset"));
      Assert.assertTrue(esbRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getJSONObject("properties")
            .getJSONObject("email").has("versions"));
      Assert.assertTrue(apiRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getJSONObject("properties")
            .getJSONObject("email").has("versions"));
      
   }
   
   /**
    * Negative test case for listContacts method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, description = "hubspot {listContacts} integration test with negative case.")
   public void testListContactsWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listContacts");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_negative.json");
      
      final String apiEndpoint =
            apiUrl + "/contacts/v1/lists/all/contacts/all?hapikey=" + apiKey
                  + "&property=email&propertyMode=value&count=1&vidOffset=1";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
   }
   
   /**
    * Method Name: updateContact 
    * Skipped Case: mandatory case 
    * Reason: No mandatory parameter(s)
    */
   
   /**
    * Positive test case for updateContact method  with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithMandatoryParameters" }, description = "hubspot {updateContact} integration test with optional parameters.")
   public void testUpdateContactWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateContact");
      
      final String apiEndpoint =
            apiUrl + "/contacts/v1/contact/vid/" + connectorProperties.getProperty("contactIdMandatory")
                  + "/profile?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertFalse(apiRestResponse.getBody().getJSONObject("properties").has("firstname"));
      Assert.assertFalse(apiRestResponse.getBody().getJSONObject("properties").has("lastname"));
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateContact_optional.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
      
      apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertTrue(apiRestResponse.getBody().getJSONObject("properties").has("firstname"));
      Assert.assertTrue(apiRestResponse.getBody().getJSONObject("properties").has("lastname"));
      Assert.assertEquals(connectorProperties.getProperty("contactFirstName"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("firstname").getString("value"));
      Assert.assertEquals(connectorProperties.getProperty("contactLastName"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("lastname").getString("value"));
      
   }
   
   /**
    * Negative test case for updateContact method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, description = "hubspot {updateContact} integration test with negative case.")
   public void testUpdateContactWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateContact");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateContact_negative.json");
      
      final String apiEndpoint =
            apiUrl + "/contacts/v1/contact/vid/" + connectorProperties.getProperty("contactIdMandatory")
                  + "/profile?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_updateContact_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
   }
   
   /**
    * Method Name: createCompany 
    * Skipped Case: mandatory case 
    * Reason: No mandatory parameter(s)
    */
   
   /**
    * Positive test case for createCompany with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "hubspot {createCompany} integration test with optional parameters.")
   public void testCreateCompanyWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createCompany");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCompany_optional.json");
      final String companyId = esbRestResponse.getBody().getString("companyId");
      connectorProperties.put("companyIdOptional", companyId);
      
      final String apiEndpoint = apiUrl + "/companies/v2/companies/" + companyId + "?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(connectorProperties.getProperty("companyName"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("name").getString("value"));
      Assert.assertEquals(connectorProperties.getProperty("companyDescription"), apiRestResponse.getBody()
            .getJSONObject("properties").getJSONObject("description").getString("value"));
      Assert.assertEquals(connectorProperties.getProperty("companyCountry"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("country").getString("value"));
      Assert.assertEquals(connectorProperties.getProperty("companyCity"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("city").getString("value"));
      Assert.assertEquals(connectorProperties.getProperty("companyWebsite"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("website").getString("value"));
      
   }
   
   /**
    * Negative test case for createCompany method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "hubspot {createCompany} integration test with negative case.")
   public void testCreateCompanyWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createCompany");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCompany_negative.json");
      
      final String apiEndpoint = apiUrl + "/companies/v2/companies?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createCompany_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
      
   }
   
   /**
    * Method Name: updateCompany 
    * Skipped Case: mandatory case 
    * Reason: No mandatory parameter(s)
    */
   
   /**
    * Positive test case for updateCompany with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCompanyWithOptionalParameters" }, description = "hubspot {updateCompany} integration test with optional parameters.")
   public void testUpdateCompanyWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateCompany");
      
      final String apiEndpoint =
            apiUrl + "/companies/v2/companies/" + connectorProperties.getProperty("companyIdOptional") + "?hapikey="
                  + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertFalse(apiRestResponse.getBody().getJSONObject("properties").has("address"));
      Assert.assertFalse(apiRestResponse.getBody().getJSONObject("properties").has("timezone"));
      Assert.assertFalse(apiRestResponse.getBody().getJSONObject("properties").has("founded_year"));
      
      final String companyAddress = "25, Main Street";
      final String companyTimeZone = "America/New_York";
      final String companyFoundedYear = "2007";
      parametersMap.put("companyAddress", companyAddress);
      parametersMap.put("companyTimeZone", companyTimeZone);
      parametersMap.put("companyFoundedYear", companyFoundedYear);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCompany_optional.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertTrue(apiRestResponse.getBody().getJSONObject("properties").has("address"));
      Assert.assertTrue(apiRestResponse.getBody().getJSONObject("properties").has("timezone"));
      Assert.assertTrue(apiRestResponse.getBody().getJSONObject("properties").has("founded_year"));
      
   }
   
   /**
    * Negative test case for updateCompany method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateCompanyWithOptionalParameters" }, description = "hubspot {updateCompany} integration test with negative case.")
   public void testUpdateCompanyWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateCompany");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCompany_negative.json",
                  parametersMap);
      
      final String apiEndpoint = apiUrl + "/companies/v2/companies/00?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_updateCompany_negative.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
   }
   
   /**
    * Positive test case for getCompany method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateCompanyWithOptionalParameters" }, description = "hubspot {getCompany} integration test with mandatory parameters.")
   public void testGetCompanyWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getCompany");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCompany_mandatory.json");
      
      final String apiEndpoint =
            apiUrl + "/companies/v2/companies/" + connectorProperties.getProperty("companyIdOptional") + "?hapikey="
                  + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONObject("properties").getJSONObject("country").getString("value"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("country").getString("value"));
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONObject("properties").getJSONObject("website").getString("value"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("website").getString("value"));
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONObject("properties").getJSONObject("address").getString("value"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("address").getString("value"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("properties").getJSONObject("city")
            .getString("value"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("city").getString("value"));
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONObject("properties").getJSONObject("description").getString("value"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("description").getString("value"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("properties").getJSONObject("name")
            .getString("value"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("name").getString("value"));
      
   }
   
   /**
    * Method Name: getCompany 
    * Skipped Case: optional case 
    * Reason: No optional parameter(s)
    */
   
   /**
    * Negative test case for getCompany method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "hubspot {getCompany} integration test with negative case.")
   public void testGetCompanyWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getCompany");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCompany_negative.json");
      
      final String apiEndpoint = apiUrl + "/companies/v2/companies/00?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap, "api_updateCompany_negative.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
   }
   
   /**
    * Positive test case for addContactToCompany method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    * @throws InterruptedException
    *            if Thread interrupted.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithMandatoryParameters",
         "testUpdateCompanyWithOptionalParameters" }, description = "hubspot {addContactToCompany} integration test with mandatory parameters.")
   public void testAddContactToCompanyWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
   
      esbRequestHeadersMap.put("Action", "urn:addContactToCompany");
      Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeout")));
      String apiEndpoint =
            apiUrl + "/companies/v2/companies/" + connectorProperties.getProperty("companyIdOptional") + "?hapikey="
                  + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertFalse(apiRestResponse.getBody().getJSONObject("properties").has("num_associated_contacts"));
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addContactToCompany_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeout")));
     
      apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertTrue(apiRestResponse.getBody().getJSONObject("properties").has("num_associated_contacts"));
      
      apiEndpoint =
            apiUrl + "/contacts/v1/contact/vid/" + connectorProperties.getProperty("contactIdMandatory")
                  + "/profile?hapikey=" + apiKey;
      apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(connectorProperties.getProperty("companyIdOptional"), apiRestResponse.getBody()
            .getJSONObject("properties").getJSONObject("associatedcompanyid").getString("value"));
      
   }
   
   /**
    * Method Name: addContactToCompany 
    * Skipped Case: optional case 
    * Reason: No optional parameter(s)
    */
   
   /**
    * Negative test case for addContactToCompany method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "hubspot {addContactToCompany} integration test with negative case.")
   public void testAddContactToCompanyWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:addContactToCompany");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addContactToCompany_negative.json");
      
      final String apiEndpoint = apiUrl + "/companies/v2/companies/00/contacts/00?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
   }
   
   /**
    * Positive test case for listRecentlyCreatedCompanies method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetCompanyWithMandatoryParameters" }, description = "hubspot {listRecentlyCreatedCompanies} integration test with mandatory parameters.")
   public void testListRecentlyCreatedCompaniesWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listRecentlyCreatedCompanies");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                  "esb_listRecentlyCreatedCompanies_mandatory.json");
      
      final String apiEndpoint = apiUrl + "/companies/v2/companies/recent/created?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").length(), apiRestResponse.getBody()
            .getJSONArray("results").length());
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("companyId"),
            apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("companyId"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).length(), apiRestResponse
            .getBody().getJSONArray("results").getJSONObject(0).length());
      Assert.assertEquals(esbRestResponse.getBody().getString("hasMore"), apiRestResponse.getBody()
            .getString("hasMore"));
      Assert.assertEquals(esbRestResponse.getBody().getString("offset"), apiRestResponse.getBody().getString("offset"));
      Assert.assertEquals(esbRestResponse.getBody().getString("total"), apiRestResponse.getBody().getString("total"));
      
   }
   
   /**
    * Positive test case for listRecentlyCreatedCompanies method with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetCompanyWithMandatoryParameters" }, description = "hubspot {listRecentlyCreatedCompanies} integration test with optional parameters.")
   public void testListRecentlyCreatedCompaniesWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listRecentlyCreatedCompanies");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                  "esb_listRecentlyCreatedCompanies_optional.json");
      
      final String apiEndpoint = apiUrl + "/companies/v2/companies/recent/created?hapikey=" + apiKey + "&count=1&offset=1";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").length(), apiRestResponse.getBody()
            .getJSONArray("results").length());
      Assert.assertEquals(esbRestResponse.getBody().getString("hasMore"), apiRestResponse.getBody()
            .getString("hasMore"));
      Assert.assertEquals(esbRestResponse.getBody().getString("offset"), apiRestResponse.getBody().getString("offset"));
      Assert.assertEquals(esbRestResponse.getBody().getString("total"), apiRestResponse.getBody().getString("total"));
      
   }
   
   /**
    * Negative test case for listRecentlyCreatedCompanies method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "hubspot {listRecentlyCreatedCompanies} integration test with negative case.")
   public void testListRecentlyCreatedCompaniesWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listRecentlyCreatedCompanies");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                  "esb_listRecentlyCreatedCompanies_negative.json");
      
      final String apiEndpoint =
            apiUrl + "/companies/v2/companies/recent/created?hapikey=" + apiKey + "&count=invalid&offset=invalid";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      
   }
   
   /**
    * Positive test case for listRecentlyModifiedCompanies method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetCompanyWithMandatoryParameters" }, description = "hubspot {listRecentlyModifiedCompanies} integration test with mandatory parameters.")
   public void testListRecentlyModifiedCompaniesWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listRecentlyModifiedCompanies");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                  "esb_listRecentlyModifiedCompanies_mandatory.json");
      
      final String apiEndpoint = apiUrl + "/companies/v2/companies/recent/modified?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").length(), apiRestResponse.getBody()
            .getJSONArray("results").length());
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("companyId"),
            apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("companyId"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).length(), apiRestResponse
            .getBody().getJSONArray("results").getJSONObject(0).length());
      Assert.assertEquals(esbRestResponse.getBody().getString("hasMore"), apiRestResponse.getBody()
            .getString("hasMore"));
      Assert.assertEquals(esbRestResponse.getBody().getString("offset"), apiRestResponse.getBody().getString("offset"));
      Assert.assertEquals(esbRestResponse.getBody().getString("total"), apiRestResponse.getBody().getString("total"));
      
   }
   
   /**
    * Positive test case for listRecentlyModifiedCompanies method with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetCompanyWithMandatoryParameters" }, description = "hubspot {listRecentlyModifiedCompanies} integration test with optional parameters.")
   public void testListRecentlyModifiedCompaniesWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listRecentlyModifiedCompanies");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                  "esb_listRecentlyModifiedCompanies_optional.json");
      
      final String apiEndpoint = apiUrl + "/companies/v2/companies/recent/modified?hapikey=" + apiKey + "&count=1&offset=1";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").length(), apiRestResponse.getBody()
            .getJSONArray("results").length());
      Assert.assertEquals(esbRestResponse.getBody().getString("hasMore"), apiRestResponse.getBody()
            .getString("hasMore"));
      Assert.assertEquals(esbRestResponse.getBody().getString("offset"), apiRestResponse.getBody().getString("offset"));
      Assert.assertEquals(esbRestResponse.getBody().getString("total"), apiRestResponse.getBody().getString("total"));
      
   }
   
   /**
    * Negative test case for listRecentlyModifiedCompanies method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "hubspot {listRecentlyModifiedCompanies} integration test with negative case.")
   public void testListRecentlyModifiedCompaniesWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listRecentlyModifiedCompanies");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                  "esb_listRecentlyModifiedCompanies_negative.json");
      
      final String apiEndpoint =
            apiUrl + "/companies/v2/companies/recent/modified?hapikey=" + apiKey + "&count=invalid&offset=invalid";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      
   }
   
   /**
    * Method Name: createDeal 
    * Skipped Case: mandatory case 
    * Reason: No mandatory parameter(s)
    */
   
   /**
    * Positive test case for createDeal method with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testAddContactToCompanyWithMandatoryParameters" }, description = "hubspot {createDeal} integration test with optional parameters.")
   public void testCreateDealWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createDeal");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDeal_optional.json");
      final String dealId = esbRestResponse.getBody().getString("dealId");
      connectorProperties.put("dealIdOptional", dealId);
      
      final String apiEndpoint = apiUrl + "/deals/v1/deal/" + dealId + "?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(connectorProperties.getProperty("dealName"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("dealname").getString("value"));
      Assert.assertEquals(connectorProperties.getProperty("dealAmount"),
            apiRestResponse.getBody().getJSONObject("properties").getJSONObject("amount").getString("value"));
      Assert.assertEquals(connectorProperties.getProperty("companyIdOptional"), apiRestResponse.getBody()
            .getJSONObject("associations").getJSONArray("associatedCompanyIds").getString(0));
      Assert.assertEquals(connectorProperties.getProperty("contactIdMandatory"), apiRestResponse.getBody()
            .getJSONObject("associations").getJSONArray("associatedVids").getString(0));
      
   }
   
   /**
    * Negative test case for createDeal method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "hubspot {createDeal} integration test with negative case.")
   public void testCreateDealWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createDeal");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDeal_negative.json");
      
      final String apiEndpoint = apiUrl + "/deals/v1/deal?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createDeal_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
      
   }
   
   /**
    * Method Name: updateDeal 
    * Skipped Case: mandatory case 
    * Reason: No mandatory parameter(s)
    */
   
   /**
    * Positive test case for updateDeal method with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDealWithOptionalParameters" }, description = "hubspot {updateDeal} integration test with optional parameters.")
   public void testUpdateDealWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateDeal");
      final String apiEndpoint =
            apiUrl + "/deals/v1/deal/" + connectorProperties.getProperty("dealIdOptional") + "?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertFalse(apiRestResponse.getBody().getJSONObject("properties").has("dealstage"));
      Assert.assertFalse(apiRestResponse.getBody().getJSONObject("properties").has("dealtype"));
      
      String dealStage = "appointmentscheduled";
      String dealType = "newbusiness";
      parametersMap.put("dealStage", dealStage);
      parametersMap.put("dealType", dealType);
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateDeal_optional.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertTrue(apiRestResponse.getBody().getJSONObject("properties").has("dealstage"));
      Assert.assertEquals(dealStage, apiRestResponse.getBody().getJSONObject("properties").getJSONObject("dealstage")
            .getString("value"));
      Assert.assertTrue(apiRestResponse.getBody().getJSONObject("properties").has("dealtype"));
      Assert.assertEquals(dealType, apiRestResponse.getBody().getJSONObject("properties").getJSONObject("dealtype")
            .getString("value"));
      
   }
   
   /**
    * Negative test case for updateDeal method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDealWithOptionalParameters" }, description = "hubspot {updateDeal} integration test with negative case.")
   public void testUpdateDealWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateDeal");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateDeal_negative.json");
      
      final String apiEndpoint =
            apiUrl + "/deals/v1/deal/" + connectorProperties.getProperty("dealIdOptional") + "?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_updateDeal_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
      
   }
   
   /**
    * Positive test case for getRecentlyCreatedDeals method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateDealWithOptionalParameters" }, description = "hubspot {getRecentlyCreatedDeals} integration test with mandatory parameters.")
   public void testGetRecentlyCreatedDealsWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getRecentlyCreatedDeals");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecentlyCreatedDeals_mandatory.json");
      
      final String apiEndpoint = apiUrl + "/deals/v1/deal/recent/created?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").length(), apiRestResponse.getBody()
            .getJSONArray("results").length());
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("dealId"),
            apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("dealId"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).length(), apiRestResponse
            .getBody().getJSONArray("results").getJSONObject(0).length());
      Assert.assertEquals(esbRestResponse.getBody().getString("hasMore"), apiRestResponse.getBody()
            .getString("hasMore"));
      Assert.assertEquals(esbRestResponse.getBody().getString("offset"), apiRestResponse.getBody().getString("offset"));
      Assert.assertEquals(esbRestResponse.getBody().getString("total"), apiRestResponse.getBody().getString("total"));
      
   }
   
   /**
    * Positive test case for getRecentlyCreatedDeals method with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateDealWithOptionalParameters" }, description = "hubspot {getRecentlyCreatedDeals} integration test with optional parameters.")
   public void testGetRecentlyCreatedDealsWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getRecentlyCreatedDeals");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecentlyCreatedDeals_optional.json");
      
      final String apiEndpoint = apiUrl + "/deals/v1/deal/recent/created?hapikey=" + apiKey + "&count=1&offset=1";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").length(), apiRestResponse.getBody()
            .getJSONArray("results").length());
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("dealId"),
            apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("dealId"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).length(), apiRestResponse
            .getBody().getJSONArray("results").getJSONObject(0).length());
      Assert.assertEquals(esbRestResponse.getBody().getString("hasMore"), apiRestResponse.getBody()
            .getString("hasMore"));
      Assert.assertEquals(esbRestResponse.getBody().getString("offset"), apiRestResponse.getBody().getString("offset"));
      Assert.assertEquals(esbRestResponse.getBody().getString("total"), apiRestResponse.getBody().getString("total"));
      
   }
   
   /**
    * Negative test case for getRecentlyCreatedDeals method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateDealWithOptionalParameters" }, description = "hubspot {getRecentlyCreatedDeals} integration test with negative case.")
   public void testGetRecentlyCreatedDealsWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getRecentlyCreatedDeals");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecentlyCreatedDeals_negative.json");
      
      final String apiEndpoint = apiUrl + "/deals/v1/deal/recent/created?hapikey=" + apiKey + "&count=invalid&offset=invalid";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      
   }
   
   /**
    * Positive test case for getRecentlyModifiedDeals method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateDealWithOptionalParameters" }, description = "hubspot {getRecentlyModifiedDeals} integration test with mandatory parameters.")
   public void testGetRecentlyModifiedDealsWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getRecentlyModifiedDeals");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecentlyModifiedDeals_mandatory.json");
      
      final String apiEndpoint = apiUrl + "/deals/v1/deal/recent/modified?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").length(), apiRestResponse.getBody()
            .getJSONArray("results").length());
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("dealId"),
            apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("dealId"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).length(), apiRestResponse
            .getBody().getJSONArray("results").getJSONObject(0).length());
      Assert.assertEquals(esbRestResponse.getBody().getString("hasMore"), apiRestResponse.getBody()
            .getString("hasMore"));
      Assert.assertEquals(esbRestResponse.getBody().getString("offset"), apiRestResponse.getBody().getString("offset"));
      Assert.assertEquals(esbRestResponse.getBody().getString("total"), apiRestResponse.getBody().getString("total"));
      
   }
   
   /**
    * Positive test case for getRecentlyModifiedDeals method with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateDealWithOptionalParameters" }, description = "hubspot {getRecentlyModifiedDeals} integration test with optional parameters.")
   public void testGetRecentlyModifiedDealsWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getRecentlyModifiedDeals");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecentlyModifiedDeals_optional.json");
      
      final String apiEndpoint = apiUrl + "/deals/v1/deal/recent/modified?hapikey=" + apiKey + "&count=1&offset=1";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").length(), apiRestResponse.getBody()
            .getJSONArray("results").length());
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("dealId"),
            apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("dealId"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).length(), apiRestResponse
            .getBody().getJSONArray("results").getJSONObject(0).length());
      Assert.assertEquals(esbRestResponse.getBody().getString("hasMore"), apiRestResponse.getBody()
            .getString("hasMore"));
      Assert.assertEquals(esbRestResponse.getBody().getString("offset"), apiRestResponse.getBody().getString("offset"));
      Assert.assertEquals(esbRestResponse.getBody().getString("total"), apiRestResponse.getBody().getString("total"));
      
   }
   
   /**
    * Negative test case for getRecentlyModifiedDeals method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateDealWithOptionalParameters" }, description = "hubspot {getRecentlyModifiedDeals} integration test with negative case.")
   public void testGetRecentlyModifiedDealsWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getRecentlyModifiedDeals");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecentlyModifiedDeals_negative.json");
      
      final String apiEndpoint =
            apiUrl + "/deals/v1/deal/recent/modified?hapikey=" + apiKey + "&count=invalid&offset=invalid";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      
   }
   
   /**
    * Positive test case for createEngagement method with mandatory parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "hubspot {createEngagement} integration test with mandatory parameters.")
   public void testCreateEngagementWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createEngagement");
      final String engagementType = "NOTE";
      parametersMap.put("engagementType", engagementType);
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEngagement_mandatory.json",
                  parametersMap);
      final String engagementId = esbRestResponse.getBody().getJSONObject("engagement").getString("id");
      connectorProperties.put("engagementIdMandatory", engagementId);
      
      final String apiEndpoint = apiUrl + "/engagements/v1/engagements/" + engagementId + "?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(engagementType, apiRestResponse.getBody().getJSONObject("engagement").getString("type"));
      
   }
   
   /**
    * Positive test case for createEngagement method with optional parameters.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDealWithOptionalParameters" }, description = "hubspot {createEngagement} integration test with optional parameters.")
   public void testCreateEngagementWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createEngagement");
      final String engagementType = "NOTE";
      final String metadataBody = "Sample Note Added.";
      final String uid = Long.toString(currentTimeString);
      parametersMap.put("engagementType", engagementType);
      parametersMap.put("metadataBody", metadataBody);
      parametersMap.put("uid", uid);
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEngagement_optional.json",
                  parametersMap);
      final String engagementId = esbRestResponse.getBody().getJSONObject("engagement").getString("id");
      connectorProperties.put("engagementIdOptional", engagementId);
      
      final String apiEndpoint = apiUrl + "/engagements/v1/engagements/" + engagementId + "?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(engagementType, apiRestResponse.getBody().getJSONObject("engagement").getString("type"));
      Assert.assertEquals(metadataBody, apiRestResponse.getBody().getJSONObject("metadata").getString("body"));
      Assert.assertEquals(uid, apiRestResponse.getBody().getJSONObject("engagement").getString("uid"));
      Assert.assertEquals(connectorProperties.getProperty("contactIdMandatory"), apiRestResponse.getBody()
            .getJSONObject("associations").getJSONArray("contactIds").getString(0));
      Assert.assertEquals(connectorProperties.getProperty("companyIdOptional"), apiRestResponse.getBody()
            .getJSONObject("associations").getJSONArray("companyIds").getString(0));
      Assert.assertEquals(connectorProperties.getProperty("dealIdOptional"),
            apiRestResponse.getBody().getJSONObject("associations").getJSONArray("dealIds").getString(0));
      
   }
   
   /**
    * Negative test case for createEngagement method.
    * 
    * @throws IOException
    *            Signals that an I/O exception has occurred.
    * @throws JSONException
    *            if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDealWithOptionalParameters" }, description = "hubspot {createEngagement} integration test with negative case.")
   public void testCreateEngagementWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createEngagement");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEngagement_negative.json");
      
      final String apiEndpoint = apiUrl + "/engagements/v1/engagements?hapikey=" + apiKey;
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createEngagement_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody()
            .getString("message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
      
   }
   
}
