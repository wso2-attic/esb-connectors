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

package org.wso2.carbon.connector.integration.test.pipedrive;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

public class PipedriveConnectorIntegrationTest extends ConnectorIntegrationTestBase {
   
   private Map<String, String> esbRequestHeadersMap;
   
   private Map<String, String> apiRequestHeadersMap;
   
   private String apiUrl;
   
   private long currentTimeString;
   
   private Calendar cal;
   
   private SimpleDateFormat dateTimeFormat;
   
   private SimpleDateFormat dateFormat;
   
   /**
    * Set up the environment.
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {
   
      init("pipedrive-connector-1.0.0");
      esbRequestHeadersMap = new HashMap<String, String>();
      apiRequestHeadersMap = new HashMap<String, String>();
      
      esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
      esbRequestHeadersMap.put("Content-Type", "application/json");
      
      apiRequestHeadersMap.putAll(esbRequestHeadersMap);
      
      apiUrl = connectorProperties.getProperty("apiUrl") + "/v1/";
      currentTimeString = System.currentTimeMillis();
      dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      cal = Calendar.getInstance();
   }
   
   /**
    * Positive test case for getUser method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "pipedrive {getUser} integration test with mandatory parameters.")
   public void testGetUserWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getUser");
      JSONObject esbJSONResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUser_mandatory.json").getBody();
      Assert.assertEquals(esbJSONResponse.getBoolean("success"), true);
      JSONObject esbJSONData = esbJSONResponse.getJSONObject("data");
      
      String apiEndPoint =
            apiUrl + "users/" + connectorProperties.getProperty("userId") + "?api_token="
                  + connectorProperties.getProperty("apiToken");
      
      JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getBoolean("success"), true);
      JSONObject apiJSONData = apiJSONResponse.getJSONObject("data");
      
      Assert.assertEquals(esbJSONData.getString("id"), apiJSONData.getString("id"));
      Assert.assertEquals(esbJSONData.getString("name"), apiJSONData.getString("name"));
      Assert.assertEquals(esbJSONData.getString("email"), apiJSONData.getString("email"));
      Assert.assertEquals(esbJSONData.getString("created"), apiJSONData.getString("created"));
   }
   
   /**
    * Positive test case for getUser method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetUserWithMandatoryParameters" }, description = "pipedrive {getUser} integration test with optional parameters.")
   public void testGetUserWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getUser");
      
      JSONObject esbJSONResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUser_optional.json").getBody();
      Assert.assertEquals(esbJSONResponse.getString("success"), "true");
      JSONObject esbJSONData = esbJSONResponse.getJSONObject("data");
      
      String apiEndPoint =
            apiUrl + "users:(id,name,email,last_login,created)/" + connectorProperties.getProperty("userId")
                  + "?api_token=" + connectorProperties.getProperty("apiToken");
      
      JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getString("success"), "true");
      JSONObject apiJSONData = apiJSONResponse.getJSONObject("data");
      
      Assert.assertFalse(esbJSONData.has("default_currency"));
      Assert.assertFalse(apiJSONData.has("default_currency"));
      Assert.assertEquals(esbJSONData.getString("id"), apiJSONData.getString("id"));
      Assert.assertEquals(esbJSONData.getString("name"), apiJSONData.getString("name"));
      Assert.assertEquals(esbJSONData.getString("email"), apiJSONData.getString("email"));
      Assert.assertEquals(esbJSONData.getString("created"), apiJSONData.getString("created"));
   }
   
   /**
    * Positive test case for createOrganization method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetUserWithOptionalParameters" }, description = "pipedrive {createOrganization} integration test with mandatory parameters.")
   public void testCreateOrganizationWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createOrganization");
      
      String esbOrgNameMandatory = "esbOrgNameMandatory_" + currentTimeString;
      connectorProperties.setProperty("esbOrgNameMandatory", esbOrgNameMandatory);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createOrganization_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), true);
      
      JSONObject esbJSONData = esbRestResponse.getBody().getJSONObject("data");
      
      String organizationIdMandatory = esbJSONData.getString("id");
      
      connectorProperties.setProperty("organizationIdMandatory", organizationIdMandatory);
      
      String apiEndPoint =
            apiUrl + "organizations/" + organizationIdMandatory + "?api_token="
                  + connectorProperties.getProperty("apiToken");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), true);
      JSONObject apiJSONData = apiRestResponse.getBody().getJSONObject("data");
      
      Assert.assertEquals(esbJSONData.getString("id"), apiJSONData.getString("id"));
      Assert.assertEquals(esbOrgNameMandatory, apiJSONData.getString("name"));
      Assert.assertEquals(esbJSONData.getJSONObject("owner_id").getString("email"),
            apiJSONData.getJSONObject("owner_id").getString("email"));
      Assert.assertEquals(esbJSONData.getString("add_time"), apiJSONData.getString("add_time"));
   }
   
   /**
    * Positive test case for createOrganization method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateOrganizationWithMandatoryParameters" }, description = "pipedrive {createOrganization} integration test with optional parameters.")
   public void testCreateOrganizationWithOptionalParameters() throws IOException, JSONException {
   
      String dateTime = dateTimeFormat.format(cal.getTime());
      connectorProperties.setProperty("addedTime", dateTime);
      
      esbRequestHeadersMap.put("Action", "urn:createOrganization");
      
      String esbOrgNameOptional = "esbOrgNameOptional_" + currentTimeString;
      connectorProperties.setProperty("esbOrgNameOptional", esbOrgNameOptional);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createOrganization_optional.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), true);
      
      JSONObject esbJSONData = esbRestResponse.getBody().getJSONObject("data");
      
      String organizationIdOptional = esbJSONData.getString("id");
      
      connectorProperties.setProperty("organizationIdOptional", organizationIdOptional);
      
      String apiEndPoint =
            apiUrl + "organizations/" + organizationIdOptional + "?api_token="
                  + connectorProperties.getProperty("apiToken");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), true);
      JSONObject apiJSONData = apiRestResponse.getBody().getJSONObject("data");
      
      Assert.assertEquals(esbJSONData.getString("id"), apiJSONData.getString("id"));
      Assert.assertEquals(esbOrgNameOptional, apiJSONData.getString("name"));
      Assert.assertEquals(esbJSONData.getString("add_time"), apiJSONData.getString("add_time"));
   }
   
   /**
    * Positive test case for createPerson method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateOrganizationWithOptionalParameters" }, description = "pipedrive {createPerson} integration test with mandatory parameters.")
   public void testCreatePersonWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createPerson");
      
      String esbPersonNameMandatory = "esbPersonNameMandatory_" + currentTimeString;
      connectorProperties.setProperty("esbPersonNameMandatory", esbPersonNameMandatory);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPerson_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), true);
      
      JSONObject esbJSONData = esbRestResponse.getBody().getJSONObject("data");
      String personIdMandatory = esbJSONData.getString("id");
      connectorProperties.setProperty("personIdMandatory", personIdMandatory);
      
      String apiEndPoint =
            apiUrl + "persons/" + personIdMandatory + "?api_token=" + connectorProperties.getProperty("apiToken");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), true);
      JSONObject apiJSONData = apiRestResponse.getBody().getJSONObject("data");
      
      Assert.assertEquals(esbJSONData.getString("id"), apiJSONData.getString("id"));
      Assert.assertEquals(esbPersonNameMandatory, apiJSONData.getString("name"));
      Assert.assertEquals(esbJSONData.getJSONArray("email").getJSONObject(0).getString("value"), apiJSONData
            .getJSONArray("email").getJSONObject(0).getString("value"));
      Assert.assertEquals(esbJSONData.getString("add_time"), apiJSONData.getString("add_time"));
   }
   
   /**
    * Positive test case for createPerson method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePersonWithMandatoryParameters" }, description = "pipedrive {createPerson} integration test with optional parameters.")
   public void testCreatePersonWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createPerson");
      
      String esbPersonNameOptional = "esbPersonNameOptional_" + currentTimeString;
      connectorProperties.setProperty("esbPersonNameOptional", esbPersonNameOptional);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPerson_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), true);
      
      JSONObject esbJSONData = esbRestResponse.getBody().getJSONObject("data");
      String personIdOptional = esbJSONData.getString("id");
      connectorProperties.setProperty("personIdOptional", personIdOptional);
      
      String apiEndPoint =
            apiUrl + "persons/" + personIdOptional + "?api_token=" + connectorProperties.getProperty("apiToken");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), true);
      JSONObject apiJSONData = apiRestResponse.getBody().getJSONObject("data");
      
      Assert.assertEquals(esbJSONData.getString("id"), apiJSONData.getString("id"));
      Assert.assertEquals(esbPersonNameOptional, apiJSONData.getString("name"));
      Assert.assertEquals(connectorProperties.getProperty("organizationIdMandatory"),
            apiJSONData.getJSONObject("org_id").getString("value"));
      Assert.assertEquals(connectorProperties.getProperty("userId"),
            apiJSONData.getJSONObject("owner_id").getString("value"));
      Assert.assertEquals(connectorProperties.getProperty("email"), apiJSONData.getJSONArray("email").getJSONObject(0)
            .getString("value"));
      Assert.assertEquals(connectorProperties.getProperty("phoneNumber"), apiJSONData.getJSONArray("phone")
            .getJSONObject(0).getString("value"));
      Assert.assertEquals("1", apiJSONData.getString("visible_to"));
   }
   
   /**
    * Negative test case for createPerson method. Provides an invalid organization id.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePersonWithOptionalParameters" }, description = "pipedrive {createPerson} integration test with negative case.")
   public void testCreatePersonNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createPerson");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPerson_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), false);
      
      String apiEndPoint = apiUrl + "persons?api_token=" + connectorProperties.getProperty("apiToken");
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createPerson_negative.json");
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), false);
      
      Assert.assertEquals(apiRestResponse.getBody().getString("error"), esbRestResponse.getBody().getString("error"));
   }
   
   /**
    * Positive test case for getPerson method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePersonNegativeCase" }, description = "pipedrive {getPerson} integration test with mandatory parameters.")
   public void testGetPersonWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getPerson");
      
      JSONObject esbJSONResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPerson_mandatory.json").getBody();
      Assert.assertEquals(esbJSONResponse.getBoolean("success"), true);
      JSONObject esbJSONData = esbJSONResponse.getJSONObject("data");
      
      String apiEndPoint =
            apiUrl + "persons/" + connectorProperties.getProperty("personIdMandatory") + "?api_token="
                  + connectorProperties.getProperty("apiToken");
      
      JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getBoolean("success"), true);
      JSONObject apiJSONData = apiJSONResponse.getJSONObject("data");
      
      Assert.assertEquals(esbJSONData.getString("id"), apiJSONData.getString("id"));
      Assert.assertEquals(esbJSONData.getString("name"), apiJSONData.getString("name"));
      Assert.assertEquals(esbJSONData.getJSONArray("email").getJSONObject(0).getString("value"), apiJSONData
            .getJSONArray("email").getJSONObject(0).getString("value"));
   }
   
   /**
    * Positive test case for getPerson method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetPersonWithMandatoryParameters" }, description = "pipedrive {getPerson} integration test with optional parameters.")
   public void testGetPersonWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getPerson");
      
      JSONObject esbJSONResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPerson_optional.json").getBody();
      Assert.assertEquals(esbJSONResponse.getBoolean("success"), true);
      JSONObject esbJSONData = esbJSONResponse.getJSONObject("data");
      
      String apiEndPoint =
            apiUrl + "persons:(name,company_id,email)/" + connectorProperties.getProperty("personIdMandatory")
                  + "?api_token=" + connectorProperties.getProperty("apiToken");
      
      JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getBoolean("success"), true);
      JSONObject apiJSONData = apiJSONResponse.getJSONObject("data");
      
      Assert.assertFalse(esbJSONData.has("id"));
      Assert.assertFalse(apiJSONData.has("id"));
      Assert.assertEquals(esbJSONData.getString("name"), apiJSONData.getString("name"));
      Assert.assertEquals(esbJSONData.getString("company_id"), apiJSONData.getString("company_id"));
      Assert.assertEquals(esbJSONData.getJSONArray("email").getJSONObject(0).getString("value"), apiJSONData
            .getJSONArray("email").getJSONObject(0).getString("value"));
   }
   
   /**
    * Positive test case for createDeal method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetPersonWithOptionalParameters" }, description = "pipedrive {createDeal} integration test with mandatory parameters.")
   public void testCreateDealWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createDeal");
      
      String esbDealTitleMandatory = "esbDealTitleMandatory_" + currentTimeString;
      connectorProperties.setProperty("esbDealTitleMandatory", esbDealTitleMandatory);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDeal_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), true);
      
      JSONObject esbJSONData = esbRestResponse.getBody().getJSONObject("data");
      String dealIdMandatory = esbJSONData.getString("id");
      
      connectorProperties.setProperty("dealIdMandatory", dealIdMandatory);
      
      String apiEndPoint =
            apiUrl + "deals/" + dealIdMandatory + "?api_token=" + connectorProperties.getProperty("apiToken");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), true);
      JSONObject apiJSONData = apiRestResponse.getBody().getJSONObject("data");
      
      Assert.assertEquals(esbJSONData.getString("id"), apiJSONData.getString("id"));
      Assert.assertEquals(esbDealTitleMandatory, apiJSONData.getString("title"));
      Assert.assertEquals(esbJSONData.getString("add_time"), apiJSONData.getString("add_time"));
   }
   
   /**
    * Positive test case for createDeal method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDealWithMandatoryParameters" }, description = "pipedrive {createDeal} integration test with optional parameters.")
   public void testCreateDealWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createDeal");
      
      String esbDealTitleOptional = "esbDealTitleOptional_" + currentTimeString;
      connectorProperties.setProperty("esbDealTitleOptional", esbDealTitleOptional);
      String dealLostReason = "dealLostReason_" + currentTimeString;
      connectorProperties.setProperty("dealLostReason", dealLostReason);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDeal_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), true);
      
      JSONObject esbJSONData = esbRestResponse.getBody().getJSONObject("data");
      String dealIdOptional = esbJSONData.getString("id");
      connectorProperties.setProperty("dealIdOptional", dealIdOptional);
      
      String apiEndPoint =
            apiUrl + "deals/" + dealIdOptional + "?api_token=" + connectorProperties.getProperty("apiToken");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), true);
      JSONObject apiJSONData = apiRestResponse.getBody().getJSONObject("data");
      
      Assert.assertEquals(esbJSONData.getString("id"), apiJSONData.getString("id"));
      Assert.assertEquals(esbDealTitleOptional, apiJSONData.getString("title"));
      Assert.assertEquals(apiJSONData.getString("value"), connectorProperties.getProperty("dealValue"));
      Assert.assertEquals(apiJSONData.getString("currency"), connectorProperties.getProperty("dealCurrency"));
      Assert.assertEquals(apiJSONData.getString("status"), "lost");
      Assert.assertEquals(dealLostReason, apiJSONData.getString("lost_reason"));
   }
   
   /**
    * Negative test case for createDeal method. Provides and invalid stage id.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDealWithOptionalParameters" }, description = "pipedrive {createDeal} integration test with negative case.")
   public void testCreateDealNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createDeal");
      
      String esbDealTitleMandatory = "esbDealTitleMandatory_" + currentTimeString;
      connectorProperties.setProperty("esbDealTitleMandatory", esbDealTitleMandatory);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDeal_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), false);
      
      String apiEndPoint = apiUrl + "deals?api_token=" + connectorProperties.getProperty("apiToken");
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createDeal_negative.json");
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), false);
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), esbRestResponse.getBody().getString("error"));
   }
   
   /**
    * Positive test case for updateDeal method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDealNegativeCase" }, description = "pipedrive {updateDeal} integration test with optional parameters.")
   public void testUpdateDealWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateDeal");
      
      String apiEndPoint =
            apiUrl + "deals/" + connectorProperties.getProperty("dealIdOptional") + "?api_token="
                  + connectorProperties.getProperty("apiToken");
      
      RestResponse<JSONObject> apiRestResponseBeforeUpdate =
            sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponseBeforeUpdate.getBody().getBoolean("success"), true);
      JSONObject apiJSONDataBeforeUpdate = apiRestResponseBeforeUpdate.getBody().getJSONObject("data");
      
      String esbUpdateDealTitleMandatory = "esbUpdateDealTitleMandatory_" + currentTimeString;
      connectorProperties.setProperty("esbUpdateDealTitleMandatory", esbUpdateDealTitleMandatory);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateDeal_optional.json");
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), true);
      
      RestResponse<JSONObject> apiRestResponseAfterUpdate =
            sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponseAfterUpdate.getBody().getBoolean("success"), true);
      JSONObject apiJSONDataAfterUpdate = apiRestResponseAfterUpdate.getBody().getJSONObject("data");
      
      String pipeLineId = apiJSONDataAfterUpdate.getString("pipeline_id");
      connectorProperties.setProperty("pipeLineId", pipeLineId);
      
      Assert.assertEquals(esbUpdateDealTitleMandatory, apiJSONDataAfterUpdate.getString("title"));
      Assert.assertNotEquals(apiJSONDataBeforeUpdate.getString("title"), apiJSONDataAfterUpdate.getString("title"));
      Assert.assertEquals(apiJSONDataAfterUpdate.getInt("value"), 5455);
      Assert.assertNotEquals(apiJSONDataBeforeUpdate.getString("value"), apiJSONDataAfterUpdate.getString("value"));
      Assert.assertEquals(apiJSONDataAfterUpdate.getString("currency"), "USD");
      Assert.assertNotEquals(apiJSONDataBeforeUpdate.getString("currency"),
            apiJSONDataAfterUpdate.getString("currency"));
      Assert.assertEquals("open", apiJSONDataAfterUpdate.getString("status"));
      Assert.assertNotEquals(apiJSONDataBeforeUpdate.getString("status"), apiJSONDataAfterUpdate.getString("status"));
      Assert.assertEquals(connectorProperties.getProperty("updateDealStageId"),
            apiJSONDataAfterUpdate.getString("stage_id"));
      Assert.assertNotEquals(apiJSONDataBeforeUpdate.getString("stage_id"),
            apiJSONDataAfterUpdate.getString("stage_id"));
   }
   
   /**
    * Negative test case for updateDeal method. Provides and invalid stage id.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateDealWithOptionalParameters" }, description = "pipedrive {updateDeal} integration test with negative case.")
   public void testUpdateDealNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateDeal");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateDeal_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), false);
      
      String apiEndPoint =
            apiUrl + "deals/" + connectorProperties.getProperty("dealIdMandatory") + "?api_token="
                  + connectorProperties.getProperty("apiToken");
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateDeal_negative.json");
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), false);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), esbRestResponse.getBody().getString("error"));
   }
   
   /**
    * Positive test case for listPersons method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateDealNegativeCase" }, description = "pipedrive {listPersons} integration test with mandatory parameters.")
   public void testListPersonsWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listPersons");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPersons_mandatory.json");
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), true);
      
      JSONArray esbJSONData = esbRestResponse.getBody().getJSONArray("data");
      
      String apiEndPoint = apiUrl + "persons?api_token=" + connectorProperties.getProperty("apiToken");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), true);
      
      JSONArray apiJSONData = apiRestResponse.getBody().getJSONArray("data");
      
      Assert.assertEquals(esbJSONData.getJSONObject(0).getInt("id"), apiJSONData.getJSONObject(0).getInt("id"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getInt("company_id"),
            apiJSONData.getJSONObject(0).getInt("company_id"));
      Assert.assertEquals(esbJSONData.length(), apiJSONData.length());
   }
   
   /**
    * Positive test case for listPersons method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListPersonsWithMandatoryParameters" }, description = "pipedrive {listPersons} integration test with optional parameters.")
   public void testListPersonsWithOptionalParameters() throws IOException, JSONException {
   
      // Creating another person with open deal to be compared.
      connectorProperties.setProperty("testDealTitle", "testDealTitle_" + currentTimeString);
      String apiEndPointString = apiUrl + "deals?api_token=" + connectorProperties.getProperty("apiToken");
      RestResponse<JSONObject> apiRestResponseCreateDeal =
            sendJsonRestRequest(apiEndPointString, "POST", apiRequestHeadersMap, "api_createDeal_optional.json");
      
      Assert.assertEquals(apiRestResponseCreateDeal.getHttpStatusCode(), 201);
      
      esbRequestHeadersMap.put("Action", "urn:listPersons");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPersons_optional.json");
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), true);
      
      JSONArray esbJSONData = esbRestResponse.getBody().getJSONArray("data");
      
      Assert.assertTrue(esbJSONData.length() != 0 && esbJSONData.length() < 3);
      Assert.assertTrue(esbJSONData.getJSONObject(0).getInt("open_deals_count") > 0);
      
      String apiEndPoint =
            apiUrl + "persons:(name,id,add_time,update_time,open_deals_count)?api_token="
                  + connectorProperties.getProperty("apiToken") + "&start=" + connectorProperties.getProperty("start")
                  + "&limit=2" + "&sort=id asc, name desc" + "&filter_id=11";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), true);
      
      JSONArray apiJSONData = apiRestResponse.getBody().getJSONArray("data");
      
      Assert.assertTrue(apiJSONData.length() != 0 && apiJSONData.length() < 3);
      Assert.assertTrue(apiJSONData.getJSONObject(0).getInt("open_deals_count") > 0);
      
      Assert.assertEquals(esbJSONData.length(), apiJSONData.length());
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("name"), apiJSONData.getJSONObject(0)
            .getString("name"));
      
      // Checking whether the list of persons has been sorted according to the ascending order of the person
      // id.
      int esbFirstPersonId = esbJSONData.getJSONObject(0).getInt("id");
      int esbSecondPersonId = esbJSONData.getJSONObject(1).getInt("id");
      
      Assert.assertTrue(esbSecondPersonId > esbFirstPersonId);
      
      int apiFirstPersonId = apiJSONData.getJSONObject(0).getInt("id");
      Assert.assertEquals(esbFirstPersonId, apiFirstPersonId);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("additional_data").getJSONObject("pagination")
            .getInt("start"), 0);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("additional_data").getJSONObject("pagination")
            .getInt("start"), apiRestResponse.getBody().getJSONObject("additional_data").getJSONObject("pagination")
            .getInt("start"));
   }
   
   /**
    * Negative test case for listPersons method. Provides an invalid property.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListPersonsWithOptionalParameters" }, description = "pipedrive {listPersons} integration test with negative case.")
   public void testListPersonsWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listPersons");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPersons_negative.json");
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), false);
      
      String apiEndPoint =
            apiUrl + "persons?api_token=" + connectorProperties.getProperty("apiToken") + "&filter_id=INVALID";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), false);
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
   }
   
   /**
    * Positive test case for getDeal method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListPersonsWithNegativeCase" }, description = "pipedrive {getDeal} integration test with mandatory parameters.")
   public void testGetDealWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getDeal");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDeal_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), true);
      
      String esbUserId = esbRestResponse.getBody().getJSONObject("data").getJSONObject("user_id").getString("id");
      String esbDealTitle = esbRestResponse.getBody().getJSONObject("data").getString("title");
      
      String apiEndPoint =
            apiUrl + "deals/" + connectorProperties.getProperty("dealIdMandatory") + "?api_token="
                  + connectorProperties.getProperty("apiToken");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), true);
      
      String apiUserId = apiRestResponse.getBody().getJSONObject("data").getJSONObject("user_id").getString("id");
      String apiDealTitle = apiRestResponse.getBody().getJSONObject("data").getString("title");
      
      Assert.assertEquals(esbUserId, apiUserId);
      Assert.assertEquals(esbDealTitle, apiDealTitle);
   }
   
   /**
    * Positive test case for getDeal method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetDealWithMandatoryParameters" }, description = "pipedrive {getDeal} integration test with optional parameters.")
   public void testGetDealWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getDeal");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDeal_optional.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), true);
      
      String esbDealAddTime = esbRestResponse.getBody().getJSONObject("data").getString("add_time");
      String esbDealTitle = esbRestResponse.getBody().getJSONObject("data").getString("title");
      String apiEndPoint =
            apiUrl + "deals/" + connectorProperties.getProperty("dealIdOptional") + "?api_token="
                  + connectorProperties.getProperty("apiToken");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), true);
      
      String apiDealAddTime = apiRestResponse.getBody().getJSONObject("data").getString("add_time");
      String apiDealTitle = apiRestResponse.getBody().getJSONObject("data").getString("title");
      
      Assert.assertEquals(esbDealAddTime, apiDealAddTime);
      Assert.assertEquals(esbDealTitle, apiDealTitle);
   }
   
   /**
    * Positive test case for listDeals method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetDealWithOptionalParameters" }, description = "pipedrive {listDeals} integration test with mandatory parameters.")
   public void testListDealsWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listDeals");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDeals_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), true);
      
      JSONArray esbJSONData = esbRestResponse.getBody().getJSONArray("data");
      
      String apiEndPoint = apiUrl + "deals?api_token=" + connectorProperties.getProperty("apiToken");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), true);
      
      JSONArray apiJSONData = apiRestResponse.getBody().getJSONArray("data");
      
      Assert.assertEquals(esbJSONData.length(), apiJSONData.length());
      Assert.assertEquals(esbJSONData.getJSONObject(0).getJSONObject("user_id").getInt("id"), apiJSONData
            .getJSONObject(0).getJSONObject("user_id").getInt("id"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("add_time"),
            apiJSONData.getJSONObject(0).getString("add_time"));
   }
   
   /**
    * Positive test case for listDeals method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListDealsWithMandatoryParameters" }, description = "pipedrive {listDeals} integration test with optional parameters.")
   public void testListDealsWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listDeals");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDeals_optional.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), true);
      
      JSONArray esbJSONData = esbRestResponse.getBody().getJSONArray("data");
      
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("status"), "open");
      Assert.assertTrue(esbJSONData.length() != 0 && esbJSONData.length() < 3);
      
      String apiEndPoint =
            apiUrl + "deals:(id,title,user_id,status)?api_token=" + connectorProperties.getProperty("apiToken")
                  + "&limit=2&filter_id=1" + "&start=0&owned_by_you=1";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), true);
      
      JSONArray apiJSONData = apiRestResponse.getBody().getJSONArray("data");
      
      Assert.assertEquals(apiJSONData.getJSONObject(0).getString("status"), "open");
      Assert.assertTrue(apiJSONData.length() != 0 && apiJSONData.length() < 3);
      
      Assert.assertEquals(esbJSONData.length(), apiJSONData.length());
      
      Assert.assertEquals(esbJSONData.getJSONObject(0).getInt("id"), apiJSONData.getJSONObject(0).getInt("id"));
      
      Assert.assertEquals(esbJSONData.getJSONObject(0).getJSONObject("user_id").getInt("id"), apiJSONData
            .getJSONObject(0).getJSONObject("user_id").getInt("id"));
      
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("title"),
            apiJSONData.getJSONObject(0).getString("title"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("additional_data").getJSONObject("pagination")
            .getInt("start"), 0);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("additional_data").getJSONObject("pagination")
            .getInt("start"), apiRestResponse.getBody().getJSONObject("additional_data").getJSONObject("pagination")
            .getInt("start"));
   }
   
   /**
    * Negative test case for listDeals method.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListDealsWithOptionalParameters" }, description = "pipedrive {listDeals} integration test with negative case.")
   public void testListDealsWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listDeals");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDeals_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), false);
      
      String apiEndPoint =
            apiUrl + "deals?api_token=" + connectorProperties.getProperty("apiToken") + "&filter_id=INVALID";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), false);
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error_info"),
            apiRestResponse.getBody().getString("error_info"));
   }
   
   /**
    * Positive test case for listDealFollowers method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListDealsWithNegativeCase" }, description = "pipedrive {listDealFollowers} integration test with mandatory parameters.")
   public void testListDealFollowersWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listDealFollowers");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDealFollowers_mandatory.json");
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), true);
      
      JSONArray esbJSONData = esbRestResponse.getBody().getJSONArray("data");
      
      String apiEndPoint =
            apiUrl + "deals/" + connectorProperties.getProperty("dealIdMandatory") + "/followers?api_token="
                  + connectorProperties.getProperty("apiToken");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), true);
      
      JSONArray apiJSONData = apiRestResponse.getBody().getJSONArray("data");
      
      Assert.assertEquals(esbJSONData.get(0), apiJSONData.get(0));
      Assert.assertEquals(esbJSONData.length(), apiJSONData.length());
   }
   
   /**
    * Positive test case for searchPersons method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListDealFollowersWithMandatoryParameters" }, description = "pipedrive {searchPersons} integration test with mandatory parameters.")
   public void testSearchPersonsWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:searchPersons");
      String searchTerm = String.valueOf(currentTimeString);
      connectorProperties.setProperty("searchTerm", searchTerm);
      JSONObject esbJSONResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchPersons_mandatory.json").getBody();
      Assert.assertEquals(esbJSONResponse.getBoolean("success"), true);
      JSONArray esbJSONDataArray = esbJSONResponse.getJSONArray("data");
      Assert.assertTrue(esbJSONDataArray.getJSONObject(0).getString("name").contains(searchTerm));
      
      String apiEndPoint =
            apiUrl + "persons/find?api_token=" + connectorProperties.getProperty("apiToken") + "&term=" + searchTerm;
      
      JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getBoolean("success"), true);
      JSONArray apiJSONDataArray = apiJSONResponse.getJSONArray("data");
      Assert.assertTrue(apiJSONDataArray.getJSONObject(0).getString("name").contains(searchTerm));
      
      Assert.assertEquals(esbJSONDataArray.length(), apiJSONDataArray.length());
      Assert.assertEquals(esbJSONDataArray.getJSONObject(0).getInt("id"), apiJSONDataArray.getJSONObject(0)
            .getInt("id"));
      Assert.assertEquals(esbJSONDataArray.getJSONObject(0).getString("name"), apiJSONDataArray.getJSONObject(0)
            .getString("name"));
   }
   
   /**
    * Positive test case for searchPersons method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSearchPersonsWithMandatoryParameters" }, description = "pipedrive {searchPersons} integration test with optional parameters.")
   public void testSearchPersonsWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:searchPersons");
      String searchTerm = connectorProperties.getProperty("email");
      
      JSONObject esbJSONResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchPersons_optional.json").getBody();
      Assert.assertEquals(esbJSONResponse.getBoolean("success"), true);
      JSONArray esbJSONDataArray = esbJSONResponse.getJSONArray("data");
      JSONObject esbJSONData = esbJSONDataArray.getJSONObject(0);
      
      Assert.assertTrue(esbJSONData.getString("email").contains(searchTerm));
      Assert.assertFalse(esbJSONData.has("id"));
      Assert.assertTrue(esbJSONDataArray.length() != 0 && esbJSONDataArray.length() < 2);
      
      String apiEndPoint =
            apiUrl + "persons:(name,email,org_id)/find?api_token=" + connectorProperties.getProperty("apiToken")
                  + "&term=" + searchTerm + "&org_id=" + connectorProperties.getProperty("organizationIdMandatory")
                  + "&start=0&limit=1&search_by_email=1";
      
      JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getBoolean("success"), true);
      JSONArray apiJSONDataArray = apiJSONResponse.getJSONArray("data");
      JSONObject apiJSONData = esbJSONDataArray.getJSONObject(0);
      
      Assert.assertTrue(apiJSONData.getString("email").contains(searchTerm));
      Assert.assertFalse(apiJSONData.has("id"));
      Assert.assertTrue(apiJSONDataArray.length() != 0 && apiJSONDataArray.length() < 2);
      
      Assert.assertEquals(esbJSONDataArray.length(), apiJSONDataArray.length());
      Assert.assertEquals(esbJSONData.getString("name"), apiJSONData.getString("name"));
      Assert.assertEquals(esbJSONData.getString("email"), apiJSONData.getString("email"));
      Assert.assertEquals(esbJSONData.getString("org_id"), connectorProperties.getProperty("organizationIdMandatory"));
      Assert.assertEquals(esbJSONData.getInt("org_id"), apiJSONData.getInt("org_id"));
      
      Assert.assertEquals(esbJSONResponse.getJSONObject("additional_data").getJSONObject("pagination").getInt("start"),
            0);
      Assert.assertEquals(esbJSONResponse.getJSONObject("additional_data").getJSONObject("pagination").getInt("start"),
            apiJSONResponse.getJSONObject("additional_data").getJSONObject("pagination").getInt("start"));
   }
   
   /**
    * Positive test case for searchOrganizations method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSearchPersonsWithOptionalParameters" }, description = "pipedrive {searchOrganizations} integration test with mandatory parameters.")
   public void testSearchOrganizationsWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:searchOrganizations");
      String searchTerm = connectorProperties.getProperty("searchTerm");
      
      JSONObject esbJSONResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchOrganizations_mandatory.json")
                  .getBody();
      Assert.assertEquals(esbJSONResponse.getBoolean("success"), true);
      JSONArray esbJSONDataArray = esbJSONResponse.getJSONArray("data");
      Assert.assertTrue(esbJSONDataArray.getJSONObject(0).getString("name").contains(searchTerm));
      
      String apiEndPoint =
            apiUrl + "organizations/find?api_token=" + connectorProperties.getProperty("apiToken") + "&term="
                  + searchTerm;
      
      JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getBoolean("success"), true);
      JSONArray apiJSONDataArray = apiJSONResponse.getJSONArray("data");
      Assert.assertTrue(apiJSONDataArray.getJSONObject(0).getString("name").contains(searchTerm));
      
      Assert.assertEquals(esbJSONDataArray.length(), apiJSONDataArray.length());
      Assert.assertEquals(esbJSONDataArray.getJSONObject(0).getInt("id"), apiJSONDataArray.getJSONObject(0)
            .getInt("id"));
      Assert.assertEquals(esbJSONDataArray.getJSONObject(0).getString("name"), apiJSONDataArray.getJSONObject(0)
            .getString("name"));
   }
   
   /**
    * Positive test case for searchOrganizations method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSearchOrganizationsWithMandatoryParameters" }, description = "pipedrive {searchOrganizations} integration test with optional parameters.")
   public void testSearchOrganizationsWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:searchOrganizations");
      String searchTerm = connectorProperties.getProperty("searchTerm");
      
      JSONObject esbJSONResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchOrganizations_optional.json")
                  .getBody();
      Assert.assertEquals(esbJSONResponse.getBoolean("success"), true);
      JSONArray esbJSONDataArray = esbJSONResponse.getJSONArray("data");
      JSONObject esbJSONData = esbJSONDataArray.getJSONObject(0);
      
      Assert.assertTrue(esbJSONData.getString("name").contains(searchTerm));
      Assert.assertFalse(esbJSONData.has("id"));
      Assert.assertTrue(esbJSONDataArray.length() != 0 && esbJSONDataArray.length() < 2);
      
      String apiEndPoint =
            apiUrl + "organizations:(name,visible_to)/find?api_token=" + connectorProperties.getProperty("apiToken")
                  + "&term=" + searchTerm + "&start=0&limit=1";
      
      JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getBoolean("success"), true);
      JSONArray apiJSONDataArray = apiJSONResponse.getJSONArray("data");
      JSONObject apiJSONData = esbJSONDataArray.getJSONObject(0);
      
      Assert.assertTrue(apiJSONData.getString("name").contains(searchTerm));
      Assert.assertFalse(apiJSONData.has("id"));
      Assert.assertTrue(apiJSONDataArray.length() != 0 && apiJSONDataArray.length() < 2);
      
      Assert.assertEquals(esbJSONDataArray.length(), apiJSONDataArray.length());
      Assert.assertEquals(esbJSONData.getString("name"), apiJSONData.getString("name"));
      Assert.assertEquals(esbJSONData.getString("visible_to"), apiJSONData.getString("visible_to"));
      Assert.assertEquals(esbJSONResponse.getJSONObject("additional_data").getJSONObject("pagination").getInt("start"),
            0);
      Assert.assertEquals(esbJSONResponse.getJSONObject("additional_data").getJSONObject("pagination").getInt("start"),
            apiJSONResponse.getJSONObject("additional_data").getJSONObject("pagination").getInt("start"));
   }
   
   /**
    * Positive test case for listActivityTypes method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSearchOrganizationsWithOptionalParameters" }, description = "pipedrive {listActivityTypes} integration test with mandatory parameters.")
   public void testListActivityTypesWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listActivityTypes");
      
      JSONObject esbJSONResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listActivityTypes_mandatory.json")
                  .getBody();
      Assert.assertEquals(esbJSONResponse.getBoolean("success"), true);
      JSONArray esbJSONDataArray = esbJSONResponse.getJSONArray("data");
      
      String apiEndPoint = apiUrl + "activityTypes?api_token=" + connectorProperties.getProperty("apiToken");
      
      JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getBoolean("success"), true);
      JSONArray apiJSONDataArray = apiJSONResponse.getJSONArray("data");
      
      Assert.assertEquals(esbJSONDataArray.length(), apiJSONDataArray.length());
      Assert.assertEquals(esbJSONDataArray.getJSONObject(0).getInt("id"), apiJSONDataArray.getJSONObject(0)
            .getInt("id"));
      Assert.assertEquals(esbJSONDataArray.getJSONObject(0).getString("name"), apiJSONDataArray.getJSONObject(0)
            .getString("name"));
   }
   
   /**
    * Positive test case for listActivityTypes method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListActivityTypesWithMandatoryParameters" }, description = "pipedrive {listActivityTypes} integration test with optional parameters.")
   public void testListActivityTypesWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listActivityTypes");
      
      JSONObject esbJSONResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listActivityTypes_optional.json")
                  .getBody();
      Assert.assertEquals(esbJSONResponse.getBoolean("success"), true);
      JSONArray esbJSONDataArray = esbJSONResponse.getJSONArray("data");
      Assert.assertFalse(esbJSONDataArray.getJSONObject(0).has("id"));
      
      String apiEndPoint =
            apiUrl + "activityTypes:(name,color)?api_token=" + connectorProperties.getProperty("apiToken");
      
      JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getBoolean("success"), true);
      JSONArray apiJSONDataArray = apiJSONResponse.getJSONArray("data");
      Assert.assertFalse(apiJSONDataArray.getJSONObject(0).has("id"));
      
      Assert.assertEquals(esbJSONDataArray.length(), apiJSONDataArray.length());
      Assert.assertEquals(esbJSONDataArray.getJSONObject(0).getString("name"), apiJSONDataArray.getJSONObject(0)
            .getString("name"));
      Assert.assertEquals(esbJSONDataArray.getJSONObject(0).getString("color"), apiJSONDataArray.getJSONObject(0)
            .getString("color"));
   }
   
   /**
    * Positive test case for createActivity method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListActivityTypesWithOptionalParameters" }, description = "pipedrive {createActivity} integration test with mandatory parameters.")
   public void testCreateActivityWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createActivity");
      connectorProperties.setProperty("activityType", connectorProperties.getProperty("activityType").toLowerCase().replaceAll(" ", "_"));
      
      String esbActivitySubjectMandatory = "esbActivitySubjectMandatory_" + currentTimeString;
      connectorProperties.setProperty("esbActivitySubjectMandatory", esbActivitySubjectMandatory);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createActivity_mandatory.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), true);
      
      JSONObject esbJSONData = esbRestResponse.getBody().getJSONObject("data");
      String activityIdMandatory = esbJSONData.getString("id");
      connectorProperties.setProperty("activityIdMandatory", activityIdMandatory);
      
      String apiEndPoint =
            apiUrl + "activities/" + activityIdMandatory + "?api_token=" + connectorProperties.getProperty("apiToken");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), true);
      JSONObject apiJSONData = apiRestResponse.getBody().getJSONObject("data");
      
      Assert.assertEquals(esbJSONData.getString("id"), apiJSONData.getString("id"));
      Assert.assertEquals(esbActivitySubjectMandatory, apiJSONData.getString("subject"));
      Assert.assertEquals(esbJSONData.getString("add_time"), apiJSONData.getString("add_time"));
      Assert.assertEquals(esbJSONData.getString("due_date"), apiJSONData.getString("due_date"));
      Assert.assertEquals(esbJSONData.getString("due_time"), apiJSONData.getString("due_time"));
   }
   
   /**
    * Positive test case for createActivity method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateActivityWithMandatoryParameters" }, description = "pipedrive {createActivity} integration test with optional parameters.")
   public void testCreateActivityWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createActivity");
      
      String esbActivitySubjectOptional = "esbActivitySubjectOptional_" + currentTimeString;
      connectorProperties.setProperty("esbActivitySubjectOptional", esbActivitySubjectOptional);
      String activityNote = "activityNote_" + currentTimeString;
      connectorProperties.setProperty("activityNote", activityNote);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createActivity_optional.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), true);
      
      JSONObject esbJSONData = esbRestResponse.getBody().getJSONObject("data");
      String activityIdOptional = esbJSONData.getString("id");
      connectorProperties.setProperty("activityIdOptional", activityIdOptional);
      
      String apiEndPoint =
            apiUrl + "activities/" + activityIdOptional + "?api_token=" + connectorProperties.getProperty("apiToken");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), true);
      JSONObject apiJSONData = apiRestResponse.getBody().getJSONObject("data");
      
      Assert.assertEquals(esbJSONData.getString("id"), apiJSONData.getString("id"));
      Assert.assertEquals(esbActivitySubjectOptional, apiJSONData.getString("subject"));
      Assert.assertEquals(apiJSONData.getString("duration"), connectorProperties.getProperty("activityDuration"));
      Assert.assertEquals(activityNote, apiJSONData.getString("note"));
      Assert.assertEquals(esbJSONData.getString("add_time"), apiJSONData.getString("add_time"));
   }
   
   /**
    * Negative test case for createActivity method. Provides an invalid type.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateActivityWithOptionalParameters" }, description = "pipedrive {createActivity} integration test with negative case.")
   public void testCreateActivityNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createActivity");
      
      String esbActivitySubjectMandatory = "esbActivitySubjectMandatory" + currentTimeString;
      connectorProperties.setProperty("esbActivitySubjectMandatory", esbActivitySubjectMandatory);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createActivity_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), false);
      
      String apiEndPoint = apiUrl + "activities?api_token=" + connectorProperties.getProperty("apiToken");
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createActivity_negative.json");
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), false);
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), esbRestResponse.getBody().getString("error"));
   }
   
   /**
    * Positive test case for getActivity method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateActivityNegativeCase" }, description = "pipedrive {getActivity} integration test with mandatory parameters.")
   public void testGetActivityWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getActivity");
      
      JSONObject esbJSONResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getActivity_mandatory.json").getBody();
      Assert.assertEquals(esbJSONResponse.getBoolean("success"), true);
      JSONObject esbJSONData = esbJSONResponse.getJSONObject("data");
      
      String apiEndPoint =
            apiUrl + "activities/" + connectorProperties.getProperty("activityIdMandatory") + "?api_token="
                  + connectorProperties.getProperty("apiToken");
      
      JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getBoolean("success"), true);
      JSONObject apiJSONData = apiJSONResponse.getJSONObject("data");
      
      Assert.assertEquals(esbJSONData.getString("id"), apiJSONData.getString("id"));
      Assert.assertEquals(esbJSONData.getString("subject"), apiJSONData.getString("subject"));
      Assert.assertEquals(esbJSONData.getString("user_id"), apiJSONData.getString("user_id"));
      Assert.assertEquals(esbJSONData.getString("company_id"), apiJSONData.getString("company_id"));
      Assert.assertEquals(esbJSONData.getString("add_time"), apiJSONData.getString("add_time"));
   }
   
   /**
    * Positive test case for getActivity method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetActivityWithMandatoryParameters" }, description = "pipedrive {getActivity} integration test with optional parameters.")
   public void testGetActivityWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getActivity");
      
      JSONObject esbJSONResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getActivity_optional.json").getBody();
      Assert.assertEquals(esbJSONResponse.getBoolean("success"), true);
      JSONObject esbJSONData = esbJSONResponse.getJSONObject("data");
      
      String apiEndPoint =
            apiUrl + "activities:(id,subject,user_id,company_id,add_time)/"
                  + connectorProperties.getProperty("activityIdMandatory") + "?api_token="
                  + connectorProperties.getProperty("apiToken");
      
      JSONObject apiJSONResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap).getBody();
      Assert.assertEquals(apiJSONResponse.getBoolean("success"), true);
      JSONObject apiJSONData = apiJSONResponse.getJSONObject("data");
      
      Assert.assertFalse(esbJSONData.has("owner_name"));
      Assert.assertFalse(apiJSONData.has("owner_name"));
      Assert.assertEquals(esbJSONData.getString("id"), apiJSONData.getString("id"));
      Assert.assertEquals(esbJSONData.getString("subject"), apiJSONData.getString("subject"));
      Assert.assertEquals(esbJSONData.getString("user_id"), apiJSONData.getString("user_id"));
      Assert.assertEquals(esbJSONData.getString("company_id"), apiJSONData.getString("company_id"));
      Assert.assertEquals(esbJSONData.getString("add_time"), apiJSONData.getString("add_time"));
   }
   
   /**
    * Positive test case for listActivities method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetActivityWithOptionalParameters" }, description = "pipedrive {listActivities} integration test with mandatory parameters.")
   public void testListActivitiesWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listActivities");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listActivities_mandatory.json");
      JSONArray esbJSONData = esbRestResponse.getBody().getJSONArray("data");
      String apiEndPoint = apiUrl + "activities?api_token=" + connectorProperties.getProperty("apiToken");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      JSONArray apiJSONData = apiRestResponse.getBody().getJSONArray("data");
      
      Assert.assertEquals(esbJSONData.length(), apiJSONData.length());
      Assert.assertEquals(esbJSONData.getJSONObject(0).getInt("id"), apiJSONData.getJSONObject(0).getInt("id"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("subject"),
            apiJSONData.getJSONObject(0).getString("subject"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("company_id"),
            apiJSONData.getJSONObject(0).getString("company_id"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("user_id"),
            apiJSONData.getJSONObject(0).getString("user_id"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("type"), apiJSONData.getJSONObject(0)
            .getString("type"));
   }
   
   /**
    * Positive test case for listActivities method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListActivitiesWithMandatoryParameters" }, description = "pipedrive {listActivities} integration test with optional parameters.")
   public void testListActivitiesWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listActivities");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listActivities_optional.json");
      
      JSONArray esbJSONData = esbRestResponse.getBody().getJSONArray("data");
      String apiEndPoint =
            apiUrl + "activities:(id,subject,user_id,company_id,type)?api_token="
                  + connectorProperties.getProperty("apiToken");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      JSONArray apiJSONData = apiRestResponse.getBody().getJSONArray("data");
      
      Assert.assertEquals(esbJSONData.length(), apiJSONData.length());
      Assert.assertEquals(esbJSONData.getJSONObject(0).getInt("id"), apiJSONData.getJSONObject(0).getInt("id"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("subject"),
            apiJSONData.getJSONObject(0).getString("subject"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("company_id"),
            apiJSONData.getJSONObject(0).getString("company_id"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("user_id"),
            apiJSONData.getJSONObject(0).getString("user_id"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("type"), apiJSONData.getJSONObject(0)
            .getString("type"));
   }
   
   /**
    * Negative test case for listActivities method.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListActivitiesWithOptionalParameters" }, description = "pipedrive {listActivities} integration test with negative case.")
   public void testListActivitiesWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listActivities");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listActivities_negative.json");
      String apiEndPoint =
            apiUrl + "activities?api_token=" + connectorProperties.getProperty("apiToken") + "&type=INVALID";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"),
            apiRestResponse.getBody().getBoolean("success"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
   }
   
   /**
    * Positive test case for listOrganizations method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListActivitiesWithNegativeCase" }, description = "pipedrive {listOrganizations} integration test with mandatory parameters.")
   public void testListOrganizationsWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listOrganizations");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listOrganizations_mandatory.json");
      JSONArray esbJSONData = esbRestResponse.getBody().getJSONArray("data");
      String apiEndPoint = apiUrl + "organizations?api_token=" + connectorProperties.getProperty("apiToken");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      JSONArray apiJSONData = apiRestResponse.getBody().getJSONArray("data");
      
      Assert.assertEquals(esbJSONData.length(), apiJSONData.length());
      Assert.assertEquals(esbJSONData.getJSONObject(0).getInt("id"), apiJSONData.getJSONObject(0).getInt("id"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("name"), apiJSONData.getJSONObject(0)
            .getString("name"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("company_id"),
            apiJSONData.getJSONObject(0).getString("company_id"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("add_time"),
            apiJSONData.getJSONObject(0).getString("add_time"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("update_time"), apiJSONData.getJSONObject(0)
            .getString("update_time"));
   }
   
   /**
    * Positive test case for listOrganizations method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListOrganizationsWithMandatoryParameters" }, description = "pipedrive {listOrganizations} integration test with optional parameters.")
   public void testListOrganizationsWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listOrganizations");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listOrganizations_optional.json");
      
      JSONArray esbJSONData = esbRestResponse.getBody().getJSONArray("data");
      String apiEndPoint =
            apiUrl + "organizations:(id,name,company_id,add_time,update_time)?api_token="
                  + connectorProperties.getProperty("apiToken");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      JSONArray apiJSONData = apiRestResponse.getBody().getJSONArray("data");
      
      Assert.assertEquals(esbJSONData.length(), apiJSONData.length());
      Assert.assertEquals(esbJSONData.getJSONObject(0).getInt("id"), apiJSONData.getJSONObject(0).getInt("id"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("name"), apiJSONData.getJSONObject(0)
            .getString("name"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("company_id"),
            apiJSONData.getJSONObject(0).getString("company_id"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("add_time"),
            apiJSONData.getJSONObject(0).getString("add_time"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("update_time"), apiJSONData.getJSONObject(0)
            .getString("update_time"));
   }
   
   /**
    * Negative test case for listOrganizations method.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListOrganizationsWithOptionalParameters" }, description = "pipedrive {listOrganizations} integration test with negative case.")
   public void testListOrganizationsWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listOrganizations");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listOrganizations_negative.json");
      
      String apiEndPoint =
            apiUrl + "organizations?api_token=" + connectorProperties.getProperty("apiToken") + "&filter_id=INVALID";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"),
            apiRestResponse.getBody().getBoolean("success"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
   }
   
   /**
    * Positive test case for listDealsTimeline method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListOrganizationsWithNegativeCase" }, description = "pipedrive {listDealsTimeline} integration test with mandatory parameters.")
   public void testListDealsTimelineWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listDealsTimeline");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDealsTimeline_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), true);
      JSONArray esbJSONData = esbRestResponse.getBody().getJSONArray("data");
      Assert.assertTrue(esbJSONData.length() != 0 && esbJSONData.length() < 3);
      
      String apiEndPoint =
            apiUrl + "deals/timeline?api_token=" + connectorProperties.getProperty("apiToken") + "&start_date="
                  + connectorProperties.getProperty("addedTime") + "&interval=day&amount=2&field_key=add_time";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), true);
      
      JSONArray apiJSONData = apiRestResponse.getBody().getJSONArray("data");
      Assert.assertEquals(esbJSONData.length(), apiJSONData.length());
      Assert.assertTrue(apiJSONData.length() != 0 && apiJSONData.length() < 3);
      
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("period_start"), apiJSONData.getJSONObject(0)
            .getString("period_start"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("period_end"),
            apiJSONData.getJSONObject(0).getString("period_end"));
      Assert.assertEquals(esbJSONData.getJSONObject(1).getString("period_start"), apiJSONData.getJSONObject(1)
            .getString("period_start"));
      Assert.assertEquals(esbJSONData.getJSONObject(1).getString("period_end"),
            apiJSONData.getJSONObject(1).getString("period_end"));
   }
   
   /**
    * Positive test case for listDealsTimeline method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListDealsTimelineWithMandatoryParameters" }, description = "pipedrive {listDealsTimeline} integration test with optional parameters.")
   public void testListDealsTimelineWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listDealsTimeline");
      String addedDate = dateFormat.format(cal.getTime());
      connectorProperties.setProperty("addedDate", addedDate);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDealsTimeline_optional.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), true);
      JSONArray esbJSONData = esbRestResponse.getBody().getJSONArray("data");
      
      Assert.assertTrue(esbJSONData.length() != 0 && esbJSONData.length() < 3);
      
      int esbDealId = esbJSONData.getJSONObject(0).getJSONArray("deals").getJSONObject(0).getInt("id");
      int esbPipeLineId = esbJSONData.getJSONObject(0).getJSONArray("deals").getJSONObject(0).getInt("pipeline_id");
      
      String apiEndPoint =
            apiUrl + "deals:(deals)/timeline?api_token=" + connectorProperties.getProperty("apiToken") + "&start_date="
                  + addedDate + "&interval=day&amount=2&field_key=add_time&user_id="
                  + connectorProperties.getProperty("userId") + "&pipeline_id="
                  + connectorProperties.getProperty("pipeLineId") + "&exclude_deals=0";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), true);
      
      JSONArray apiJSONData = apiRestResponse.getBody().getJSONArray("data");
      
      Assert.assertTrue(apiJSONData.length() != 0 && apiJSONData.length() < 3);
      
      int apiDealId = apiJSONData.getJSONObject(0).getJSONArray("deals").getJSONObject(0).getInt("id");
      int apiPipeLineId = apiJSONData.getJSONObject(0).getJSONArray("deals").getJSONObject(0).getInt("pipeline_id");
      
      Assert.assertEquals(esbJSONData.length(), apiJSONData.length());
      Assert.assertEquals(esbDealId, apiDealId);
      Assert.assertEquals(esbPipeLineId, apiPipeLineId);
   }
   
   /**
    * Negative test case for listDealsTimeline method.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListDealsTimelineWithOptionalParameters" }, description = "pipedrive {listDealsTimeline} integration test with negative case.")
   public void testListDealsTimelineWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listDealsTimeline");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDealsTimeline_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), false);
      String apiEndPoint =
            apiUrl + "deals/timeline?api_token=" + connectorProperties.getProperty("apiToken") + "&start_date="
                  + connectorProperties.getProperty("addedDate") + "&interval=INVALID&amount=2" + "&field_key="
                  + connectorProperties.getProperty("fieldKey");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), false);
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"),
            apiRestResponse.getBody().getBoolean("success"));
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
   }
   
   /**
    * Positive test case for listDealProducts method with mandatory parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListDealsTimelineWithNegativeCase" }, description = "pipedrive {listDealProducts} integration test with mandatory parameters.")
   public void testListDealProductsWithMandatoryParameters() throws IOException, JSONException {
   
      // Creates Products for a Deal prior to testing, Note that the Products can only be added via API calls.
      // Hence, Product adding will be done within the test case rather than expecting users to execute the
      // API calls and provide related details.
      String dealId = connectorProperties.getProperty("dealIdMandatory");
      addProductsToDeal(dealId);
      
      esbRequestHeadersMap.put("Action", "urn:listDealProducts");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDealProducts_mandatory.json");
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), true);
      JSONArray esbJSONData = esbRestResponse.getBody().getJSONArray("data");
      
      String apiEndPoint =
            apiUrl + "deals/" + dealId + "/products?api_token=" + connectorProperties.getProperty("apiToken");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), true);
      JSONArray apiJSONData = apiRestResponse.getBody().getJSONArray("data");
      
      Assert.assertEquals(esbJSONData.length(), apiJSONData.length());
      Assert.assertEquals(esbJSONData.getJSONObject(0).getInt("id"), apiJSONData.getJSONObject(0).getInt("id"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("name"), apiJSONData.getJSONObject(0)
            .getString("name"));
      Assert.assertEquals(esbJSONData.getJSONObject(0).getString("add_time"),
            apiJSONData.getJSONObject(0).getString("add_time"));
      
   }
   
   /**
    * Positive test case for listDealProducts method with optional parameters.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListDealProductsWithMandatoryParameters" }, description = "pipedrive {listDealProducts} integration test with optional parameters.")
   public void testListDealProductsWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listDealProducts");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDealProducts_optional.json");
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), true);
      JSONArray esbJSONData = esbRestResponse.getBody().getJSONArray("data");
      
      String apiEndPoint =
            apiUrl + "deals:(item_price,add_time,id,product)/" + connectorProperties.getProperty("dealIdMandatory")
                  + "/products?api_token=" + connectorProperties.getProperty("apiToken")
                  + "&include_product_data=1&limit=1&start=0";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), true);
      JSONArray apiJSONData = apiRestResponse.getBody().getJSONArray("data");
      
      Assert.assertEquals(esbJSONData.getJSONObject(0).getInt("id"), apiJSONData.getJSONObject(0).getInt("id"));
      
      Assert.assertEquals(esbJSONData.length(), 1);
      Assert.assertEquals(esbJSONData.length(), apiJSONData.length());
      Assert.assertTrue(esbJSONData.getJSONObject(0).has("product"));
      Assert.assertTrue(apiJSONData.getJSONObject(0).has("product"));
      Assert.assertFalse(esbJSONData.getJSONObject(0).has("name"));
      Assert.assertFalse(apiJSONData.getJSONObject(0).has("name"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("additional_data").getJSONObject("pagination")
            .getInt("start"), 0);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("additional_data").getJSONObject("pagination")
            .getInt("start"), apiRestResponse.getBody().getJSONObject("additional_data").getJSONObject("pagination")
            .getInt("start"));
   }
   
   /**
    * Negative test case for listDealProducts method.
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListDealProductsWithOptionalParameters" }, description = "pipedrive {listDealProducts} integration test with negative case.")
   public void testListDealProductsNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listDealProducts");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDealProducts_negative.json");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"), false);
      
      String apiEndPoint = apiUrl + "/deals/INVALID/products?api_token=" + connectorProperties.getProperty("apiToken");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getBody().getBoolean("success"), false);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
      
   }
   
   /**
    * This method adds two Products to the deal dynamically in order to test the listDealProducts method.
    * 
    * @param dealId to add the Products
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws JSONException if JSON exception occurred.
    */
   private void addProductsToDeal(String dealId) throws IOException, JSONException {
   
      String apiEndPointCreateProduct = apiUrl + "products?api_token=" + connectorProperties.getProperty("apiToken");
      String apiEndPointAddProduct =
            apiUrl + "deals/" + dealId + "/products?api_token=" + connectorProperties.getProperty("apiToken");
      connectorProperties.setProperty("dealId", dealId);
      
      for (int i = 1; i < 3; i++) {
         
         String productName = "product" + i + "_" + currentTimeString;
         connectorProperties.setProperty("productName", productName);
         
         // Create Product
         RestResponse<JSONObject> apiRestResponseCreateProduct =
               sendJsonRestRequest(apiEndPointCreateProduct, "POST", apiRequestHeadersMap, "api_createProduct.json");
         Assert.assertEquals(apiRestResponseCreateProduct.getHttpStatusCode(), 201);
         Assert.assertEquals(apiRestResponseCreateProduct.getBody().getBoolean("success"), true);
         connectorProperties.setProperty("productId", apiRestResponseCreateProduct.getBody().getJSONObject("data")
               .getString("id"));
         
         // Add Product to Deal
         connectorProperties.setProperty("itemPrice", connectorProperties.getProperty("itemPrice" + i));
         connectorProperties.setProperty("itemQuantity", connectorProperties.getProperty("itemQuantity" + i));
         RestResponse<JSONObject> apiRestResponseAddProduct =
               sendJsonRestRequest(apiEndPointAddProduct, "POST", apiRequestHeadersMap, "api_addProductToDeal.json");
         Assert.assertEquals(apiRestResponseAddProduct.getHttpStatusCode(), 201);
         Assert.assertEquals(apiRestResponseAddProduct.getBody().getBoolean("success"), true);
         
      }
      
   }
}
