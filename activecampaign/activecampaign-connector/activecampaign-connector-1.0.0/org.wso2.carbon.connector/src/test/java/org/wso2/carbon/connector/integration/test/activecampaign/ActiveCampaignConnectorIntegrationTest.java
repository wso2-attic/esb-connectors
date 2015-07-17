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

package org.wso2.carbon.connector.integration.test.activecampaign;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class ActiveCampaignConnectorIntegrationTest extends ConnectorIntegrationTestBase {
   
   private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
   
   private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
   
   private String apiUrl;
   
   /**
    * Set up the environment.
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {
   
      init("activecampaign-connector-1.0.0");
      
      esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
      
      apiRequestHeadersMap.putAll(esbRequestHeadersMap);
      apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");
      
      esbRequestHeadersMap.put("Content-Type", "application/json");
      
      apiUrl =
            connectorProperties.getProperty("apiUrl") + "/admin/api.php?api_key="
                  + connectorProperties.getProperty("apiKey") + "&api_output=json" + "&api_action=";

       connectorProperties.setProperty("emailMandatory", System.currentTimeMillis() + connectorProperties
               .getProperty("emailMandatory"));
       connectorProperties.setProperty("emailOptional", System.currentTimeMillis() + connectorProperties
               .getProperty("emailOptional"));
   }
   
   /**
    * Positive test case for createMailingList method with mandatory parameters.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "activecampaign {createMailingList} integration test with mandatory parameters.")
   public void testCreateMailingListWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createMailingList");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createMailingList_mandatory.json");
      
      String listId = esbRestResponse.getBody().getString("id");
      
      connectorProperties.put("listId", listId);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint = apiUrl + "list_view&id=" + connectorProperties.getProperty("listId");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getString("name"), connectorProperties.getProperty("subject"));
      Assert.assertEquals(apiRestResponse.getBody().getString("sender_name"),
            connectorProperties.getProperty("subject"));
   }
   
   /**
    * Positive test case for createMailingList method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateMailingListWithMandatoryParameters" }, description = "activecampaign {createMailingList} integration test with optional parameters.")
   public void testCreateMailingListWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createMailingList");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createMailingList_optional.json");
      
      String listId = esbRestResponse.getBody().getString("id");
      connectorProperties.put("listId", listId);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint = apiUrl + "list_view&id=" + connectorProperties.getProperty("listId");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getString("name"), connectorProperties.getProperty("subject"));
      Assert.assertEquals(apiRestResponse.getBody().getString("sender_name"),
            connectorProperties.getProperty("subject"));
   }
   
   /**
    * Negative test case for createMailingList method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateMailingListWithOptionalParameters" }, description = "activecampaign {createMailingList} integration test with negative case.")
   public void testCreateMailingListWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createMailingList");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createMailingList_negative.json");
      
      String apiEndPoint = apiUrl + "list_add";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createMailingList_negative.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getString("result_message"),
            apiRestResponse.getBody().getString("result_message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("result_code"),
            apiRestResponse.getBody().getString("result_code"));
   }
   
   /**
    * Positive test case for listMailingLists method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateMailingListWithNegativeCase" }, description = "activecampaign {listMailingLists} integration test with mandatory parameters.")
   public void testListMailingListsWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listMailingLists");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listMailingLists_mandatory.json");
      
      String apiEndPoint = apiUrl + "list_list&ids=all";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("0").getString("id"), apiRestResponse.getBody()
            .getJSONObject("0").getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("0").getString("name"), apiRestResponse.getBody()
            .getJSONObject("0").getString("name"));
   }
   
   /**
    * Positive test case for listMailingLists method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListMailingListsWithMandatoryParameters" }, description = "activecampaign {listMailingLists} integration test with optional parameters.")
   public void testListMailingListsWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listMailingLists");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listMailingLists_optional.json");
      
      String apiEndPoint = apiUrl + "list_list&ids=all&global_fields=true&filters[name]="+ URLEncoder.encode(connectorProperties.getProperty("subject"), "UTF-8");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("0").getString("id"), apiRestResponse.getBody()
            .getJSONObject("0").getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("0").getString("name"), apiRestResponse.getBody()
            .getJSONObject("0").getString("name"));
   }
   
   /**
    * Negative test case for listMailingLists method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListMailingListsWithOptionalParameters" }, description = "activecampaign {listMailingLists} integration test with negative case.")
   public void testListMailingListsWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listMailingLists");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listMailingLists_negative.json");
      
      String apiEndPoint = apiUrl + "list_list&ids=all&filters[name]=INVALID";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("result_message"),
            apiRestResponse.getBody().getString("result_message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("result_code"),
            apiRestResponse.getBody().getString("result_code"));
   }
   
   /**
    * Positive test case for createContact method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListMailingListsWithNegativeCase" }, description = "activecampaign {createContact} integration test with mandatory parameters.")
   public void testCreateContactWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createContact");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_mandatory.json");
      
      String contactId = esbRestResponse.getBody().getString("subscriber_id");
      
      Assert.assertEquals(esbRestResponse.getBody().getString("result_message"), "Contact added");
      
      String contactEmailMandatory = connectorProperties.getProperty("emailMandatory");
      
      String apiEndPoint = apiUrl + "contact_view_email&email=" + contactEmailMandatory;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getString("id"), contactId);
      Assert.assertEquals(apiRestResponse.getBody().getString("email"), contactEmailMandatory);
   }
   
   /**
    * Positive test case for createContact method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithMandatoryParameters" }, description = "activecampaign {createContact} integration test with optional parameters.")
   public void testCreateContactWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createContact");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_optional.json");
      
      String contactId = esbRestResponse.getBody().getString("subscriber_id");
      
      Assert.assertEquals(esbRestResponse.getBody().getString("result_message"), "Contact added");
      
      String apiEndPoint = apiUrl + "contact_view_email&email=" + connectorProperties.getProperty("emailOptional");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getString("id"), contactId);
      Assert.assertEquals(apiRestResponse.getBody().getString("first_name"),
            connectorProperties.getProperty("firstName"));
      Assert.assertEquals(apiRestResponse.getBody().getString("orgname"), connectorProperties.getProperty("orgName"));
   }
   
   /**
    * Negative test case for createContact method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, description = "activecampaign {createContact} integration test with negative case.")
   public void testCreateContactWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createContact");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_negative.json");
      
      String apiEndPoint = apiUrl + "contact_add";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createContact_negative.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getString("result_code"),
            apiRestResponse.getBody().getString("result_code"));
      Assert.assertEquals(esbRestResponse.getBody().getString("result_message"),
            apiRestResponse.getBody().getString("result_message"));
   }
   
   /**
    * Positive test case for getContactByEmail method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithNegativeCase" }, description = "activecampaign {getContactByEmail} integration test with mandatory parameters.")
   public void testGetContactByEmailWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getContactByEmail");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactByEmail_mandatory.json");
      
      String apiEndPoint = apiUrl + "contact_view_email&email=" + connectorProperties.getProperty("emailOptional");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getString("first_name"),
            apiRestResponse.getBody().getString("first_name"));
      Assert.assertEquals(esbRestResponse.getBody().getString("email"), apiRestResponse.getBody().getString("email"));
   }
   
   /**
    * Negative test case for getContactByEmail method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetContactByEmailWithMandatoryParameters" }, description = "activecampaign {getContactByEmail} integration test with negative case.")
   public void testGetContactByEmailWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getContactByEmail");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactByEmail_negative.json");
      
      String apiEndPoint = apiUrl + "contact_view_email&email=INVALID";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("result_code"),
            apiRestResponse.getBody().getString("result_code"));
      Assert.assertEquals(esbRestResponse.getBody().getString("result_message"),
            apiRestResponse.getBody().getString("result_message"));
   }
   
   /**
    * Positive test case for createMessage method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetContactByEmailWithNegativeCase" }, description = "activecampaign {createMessage} integration test with mandatory parameters.")
   public void testCreateMessageWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createMessage");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createMessage_mandatory.json");
      String messageId = esbRestResponse.getBody().getString("id");
      connectorProperties.put("messageId", messageId);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint = apiUrl + "message_view&id=" + messageId;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getString("subject"), connectorProperties.getProperty("subject"));
      Assert.assertEquals(apiRestResponse.getBody().getString("fromemail"), connectorProperties.getProperty("email"));
   }
   
   /**
    * Negative test case for createMessage method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateMessageWithMandatoryParameters" }, description = "activecampaign {createMessage} integration test with negative case.")
   public void testCreateMessageWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createMessage");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createMessage_negative.json");
      
      String apiEndPoint = apiUrl + "message_add";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createMessage_negative.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getString("result_message"),
            apiRestResponse.getBody().getString("result_message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("result_code"),
            apiRestResponse.getBody().getString("result_code"));
   }
   
   /**
    * Positive test case for getMessageById method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateMessageWithNegativeCase" }, description = "activecampaign {getMessageById} integration test with mandatory parameters.")
   public void testGetMessageByIdWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getMessageById");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMessageById_mandatory.json");
      
      String apiEndPoint = apiUrl + "message_view&id=" + connectorProperties.getProperty("messageId");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getString("subject"), connectorProperties.getProperty("subject"));
      Assert.assertEquals(esbRestResponse.getBody().getString("fromemail"), connectorProperties.getProperty("email"));
   }
   
   /**
    * Negative test case for getMessageById method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetMessageByIdWithMandatoryParameters" }, description = "activecampaign {getMessageById} integration test with negative case.")
   public void testGetMessageByIdWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getMessageById");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMessageById_negative.json");
      
      String apiEndPoint = apiUrl + "message_view&id=Invalid";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("result_message"),
            apiRestResponse.getBody().getString("result_message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("result_code"),
            apiRestResponse.getBody().getString("result_code"));
   }
   
   /**
    * Positive test case for listMessages method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetMessageByIdWithNegativeCase" }, description = "activecampaign {listMessages} integration test with mandatory parameters.")
   public void testListMessagesWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listMessages");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listMessages_mandatory.json");
      
      String apiEndPoint = apiUrl + "message_list&ids=all";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("0").getString("id"), apiRestResponse.getBody()
            .getJSONObject("0").getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("0").getString("subject"), apiRestResponse.getBody()
            .getJSONObject("0").getString("subject"));
   }
   
   /**
    * Positive test case for listMessages method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListMessagesWithMandatoryParameters" }, description = "activecampaign {listMessages} integration test with optional parameters.")
   public void testListMessagesWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listMessages");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listMessages_optional.json");
      
      String apiEndPoint = apiUrl + "message_list&ids=all&page=1";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("0").getString("id"), apiRestResponse.getBody()
            .getJSONObject("0").getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("0").getString("subject"), apiRestResponse.getBody()
            .getJSONObject("0").getString("subject"));
   }
   
   /**
    * Negative test case for listMessages method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListMessagesWithOptionalParameters" }, description = "activecampaign {listMessages} integration test with negative case.")
   public void testListMessagesWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listMessages");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listMessages_negative.json");
      
      String apiEndPoint = apiUrl + "message_list&ids=Invalid";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("result_message"),
            apiRestResponse.getBody().getString("result_message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("result_code"),
            apiRestResponse.getBody().getString("result_code"));
   }
   
   /**
    * Positive test case for createCampaign method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListMessagesWithNegativeCase" }, description = "activecampaign {createCampaign} integration test with mandatory parameters.")
   public void testCreateCampaignWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createCampaign");
      
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.MONTH, 1);
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      connectorProperties.put("sdate", dateFormat.format(cal.getTime()));
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCampaign_mandatory.json");
      String campaignId = esbRestResponse.getBody().getString("id");
      connectorProperties.put("campaignId", campaignId);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint = apiUrl + "campaign_list&ids=" + campaignId;
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("0").getString("name"),
            connectorProperties.getProperty("name"));
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("0").getString("type"),
            connectorProperties.getProperty("type"));
   }
   
   /**
    * Positive test case for createCampaign method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCampaignWithMandatoryParameters" }, description = "activecampaign {createCampaign} integration test with optional parameters.")
   public void testCreateCampaignWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createCampaign");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCampaign_optional.json");
      String campaignId = esbRestResponse.getBody().getString("id");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint = apiUrl + "campaign_list&ids=" + campaignId;
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("0").getString("name"),
            connectorProperties.getProperty("name"));
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("0").getString("type"),
            connectorProperties.getProperty("type"));
   }
   
   /**
    * Negative test case for createCampaign method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCampaignWithOptionalParameters" }, description = "activecampaign {createCampaign} integration test negative case.")
   public void testCreateCampaignNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createCampaign");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCampaign_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint = apiUrl + "campaign_create";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createCampaign_negative.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getString("result_message"),
            apiRestResponse.getBody().getString("result_message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("result_code"),
            apiRestResponse.getBody().getString("result_code"));
   }
   
   /**
    * Positive test case for getCampaignClickers method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCampaignNegativeCase" }, description = "activecampaign {getCampaignClickers} integration test with mandatory parameters.")
   public void testGetCampaignClickersWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getCampaignClickers");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCampaignClickers_mandatory.json");
      
      String apiEndPoint =
            apiUrl + "campaign_report_link_list&campaignid=" + connectorProperties.getProperty("campaignId1")
                  + "&messageid=" + connectorProperties.getProperty("messageId1");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("0").getString("a_unique"), apiRestResponse.getBody()
            .getJSONObject("0").getString("a_unique"));
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONObject("0").getJSONArray("info").getJSONObject(0).getString("email"),
            apiRestResponse.getBody().getJSONObject("0").getJSONArray("info").getJSONObject(0).getString("email"));
   }
   
   /**
    * Negative test case for getCampaignClickers method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetCampaignClickersWithMandatoryParameters" }, description = "activecampaign {getCampaignClickers} integration test with negative case.")
   public void testGetCampaignClickersWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getCampaignClickers");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCampaignClickers_negative.json");
      
      String apiEndPoint =
            apiUrl + "campaign_report_link_list&campaignid=Invalid&messageid="
                  + connectorProperties.getProperty("messageId");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("result_message"),
            apiRestResponse.getBody().getString("result_message"));
      Assert.assertEquals(esbRestResponse.getBody().getString("result_code"),
            apiRestResponse.getBody().getString("result_code"));
   }
   
   /**
    * Positive test case for listCampaigns method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetCampaignClickersWithNegativeCase" }, description = "activecampaign {listCampaigns} integration test with mandatory parameters.")
   public void testListCampaignsWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listCampaigns");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCampaigns_mandatory.json");
      
      String apiEndPoint = apiUrl + "campaign_list&ids=all";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("0").getString("id"), apiRestResponse.getBody()
            .getJSONObject("0").getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("0").getString("name"), apiRestResponse.getBody()
            .getJSONObject("0").getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("0").getString("cdate"), apiRestResponse.getBody()
            .getJSONObject("0").getString("cdate"));
   }
   
   /**
    * Positive test case for listCampaigns method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListCampaignsWithMandatoryParameters" }, description = "activecampaign {listCampaigns} integration test with optional parameters.")
   public void testListCampaignsWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listCampaigns");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCampaigns_optional.json");
      
      String apiEndPoint =
            apiUrl + "campaign_list&ids=all&filters[name]=" + connectorProperties.getProperty("name")
                  + "&filters[type]=" + connectorProperties.getProperty("type");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("0").getString("id"), apiRestResponse.getBody()
            .getJSONObject("0").getString("id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("0").getString("name"), apiRestResponse.getBody()
            .getJSONObject("0").getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("0").getString("type"), apiRestResponse.getBody()
            .getJSONObject("0").getString("type"));
   }
   
   /**
    * Negative test case for listCampaigns method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListCampaignsWithOptionalParameters" }, description = "activecampaign {listCampaigns} integration test with negative case.")
   public void testListCampaignsWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listCampaigns");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCampaigns_negative.json");
      
      String apiEndPoint = apiUrl + "campaign_list&ids=INVALID";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getString("result_code"),
            apiRestResponse.getBody().getString("result_code"));
      Assert.assertEquals(esbRestResponse.getBody().getString("result_message"),
            apiRestResponse.getBody().getString("result_message"));
   }
   
}
