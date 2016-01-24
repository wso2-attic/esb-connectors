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

package org.wso2.carbon.connector.integration.test.basecrm;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class BasecrmConnectorIntegrationTest extends ConnectorIntegrationTestBase {
   
   private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
   
   private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
   
   private Map<String, String> parametersMap = new HashMap<String, String>();
   
   private String salesApiEndpoint;
   
   private String leadsApiEndpoint;
   
   /**
    * Set up the environment.
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {
   
      init("basecrm-connector-1.0.0");
      
      esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
      esbRequestHeadersMap.put("Content-Type", "application/json");
      
      salesApiEndpoint = connectorProperties.getProperty("apiUrl") + "/api/v1/";
      leadsApiEndpoint = connectorProperties.getProperty("leadServiceUrl") + "/api/v1/";
      
      RestResponse<JSONObject> apiTokenResponse =
            sendJsonRestRequest(salesApiEndpoint + "authentication.json", "POST", esbRequestHeadersMap,
                  "api_login.json");
      String loginToken = apiTokenResponse.getBody().getJSONObject("authentication").getString("token");
      
      apiRequestHeadersMap.putAll(esbRequestHeadersMap);
      apiRequestHeadersMap.put("X-Pipejump-Auth", loginToken);
      apiRequestHeadersMap.put("X-Futuresimple-Token", loginToken);
      
      parametersMap.put("token", loginToken);
   }
   
   /**
    * Positive test case for createContacts method with mandatory parameters.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "BaseCRM {createContacts} integration test with mandatory parameters.")
   public void testCreateContactsWithMandatoryParameters() throws Exception {
   
      parametersMap.put("contactLastName", "LastName2");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContacts_mandatory.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String contactId = esbRestResponse.getBody().getJSONObject("contact").getString("id");
      connectorProperties.setProperty("contactIdMandatory", contactId);
      
      String apiEndPoint = salesApiEndpoint + "contacts/" + contactId + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(parametersMap.get("contactLastName"),
            apiRestResponse.getBody().getJSONObject("contact").get("last_name"));
   }
   
   /**
    * Positive test case for createContacts method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactsWithMandatoryParameters" }, description = "BaseCRM {createContacts} integration test with optional parameters.")
   public void testCreateContactsWithOptionalParameters() throws Exception {
   
      parametersMap.put("contactLastName", "LastName");
      parametersMap.put("contactFirstName", "FirstName");
      parametersMap.put("contactTitle", "Engineer");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContacts_optional.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String contactId = esbRestResponse.getBody().getJSONObject("contact").getString("id");
      connectorProperties.setProperty("contactIdOptional", contactId);
      
      String apiEndPoint = salesApiEndpoint + "contacts/" + contactId + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(parametersMap.get("contactFirstName"), apiRestResponse.getBody().getJSONObject("contact")
            .get("first_name"));
      Assert.assertEquals(parametersMap.get("contactTitle"),
            apiRestResponse.getBody().getJSONObject("contact").get("title"));
   }
   
   /**
    * Negative test case for createContacts method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactsWithOptionalParameters" }, description = "BaseCRM {createContacts} integration test negative case.")
   public void testCreateContactsNegativeCase() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContacts_negative.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
      
      String apiEndPoint = salesApiEndpoint + "contacts.json";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createContacts_negative.json");
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("last_name").get(0).toString(), apiRestResponse
            .getBody().getJSONArray("last_name").get(0).toString());
   }
   
   /**
    * Positive test case for getContactById method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactsNegativeCase" }, description = "BaseCRM {getContactById} integration test with mandatory parameters.")
   public void testGetContactByIdWithMandatoryParameters() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactById_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint =
            salesApiEndpoint + "contacts/" + connectorProperties.getProperty("contactIdOptional") + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("contact").get("first_name"),
            parametersMap.get("contactFirstName"));
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("contact").get("last_name"),
            parametersMap.get("contactLastName"));
   }
   
   /**
    * Negative test case for getContactById method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetContactByIdWithMandatoryParameters" }, description = "BaseCRM {getContactById} integration test negative case.")
   public void testGetContactByIdNegativeCase() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactById_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      
      String apiEndPoint = salesApiEndpoint + "contacts/Invaild.json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
   }
   
   /**
    * Positive test case for setContact method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetContactByIdNegativeCase" }, description = "BaseCRM {setContact} integration test with mandatory parameters.")
   public void testSetContactWithMandatoryParameters() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setContact_mandatory.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint =
            salesApiEndpoint + "contacts/" + connectorProperties.getProperty("contactIdMandatory") + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(connectorProperties.getProperty("contactIdMandatory"), apiRestResponse.getBody()
            .getJSONObject("contact").getString("id"));
   }
   
   /**
    * Positive test case for setContact method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetContactWithMandatoryParameters" }, description = "BaseCRM {setContact} integration test with optional parameters.")
   public void testSetContactWithOptionalParameters() throws Exception {
   
      parametersMap.put("contactLastName", "LastNameUpdated");
      parametersMap.put("contactFirstName", "FirstNameUpdated");
      parametersMap.put("contactTitle", "Director");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setContact_optional.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint =
            salesApiEndpoint + "contacts/" + connectorProperties.getProperty("contactIdOptional") + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(parametersMap.get("contactLastName"), apiRestResponse.getBody().getJSONObject("contact")
            .getString("last_name"));
      Assert.assertEquals(parametersMap.get("contactFirstName"), apiRestResponse.getBody().getJSONObject("contact")
            .getString("first_name"));
      Assert.assertEquals(parametersMap.get("contactTitle"), apiRestResponse.getBody().getJSONObject("contact")
            .getString("title"));
   }
   
   /**
    * Negative test case for setContact method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetContactWithOptionalParameters" }, description = "BaseCRM {setContact} integration test negative case.")
   public void testSetContactNegativeCase() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setContact_negative.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      
      String apiEndPoint =
            salesApiEndpoint + "contacts/" + connectorProperties.getProperty("contactIdMandatory") + "Invalid"
                  + ".json";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_setContact_negative.json");
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      
   }
   
   /**
    * Positive test case for listContacts method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetContactNegativeCase" }, description = "BaseCRM {listContacts} integration test with mandatory parameters.")
   public void testListContactsWithMandatoryParameters() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_mandatory.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      JSONArray esbContactsArray = new JSONArray(esbRestResponse.getBody().getString("output"));
      JSONObject esbContactObject = new JSONObject(esbContactsArray.getString(0));
      
      String apiEndPoint = salesApiEndpoint + "contacts.json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      JSONArray apiContactsArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      JSONObject apiContactObject = new JSONObject(apiContactsArray.getString(0));
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbContactObject.getJSONObject("contact").getString("id"),
            apiContactObject.getJSONObject("contact").getString("id"));
   }
   
   /**
    * Positive test case for createContactNote method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListContactsWithMandatoryParameters" }, description = "BaseCRM {createContactNote} integration test with mandatory parameters.")
   public void testCreateContactNoteWithMandatoryParameters() throws Exception {
   
      parametersMap.put("noteContent", "Contact Note Content");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContactNote_mandatory.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint =
            salesApiEndpoint + "contacts/" + connectorProperties.getProperty("contactIdMandatory") + "/notes.json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      JSONArray apiJsonArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      String apiContactId = apiJsonArray.getJSONObject(0).getJSONObject("note").getString("id");
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("note").getString("id"), apiContactId);
      Assert.assertEquals(parametersMap.get("noteContent"), apiJsonArray.getJSONObject(0).getJSONObject("note")
            .getString("content"));
      
   }
   
   /**
    * Negative test case for createContactNote method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactNoteWithMandatoryParameters" }, description = "BaseCRM {createContactNote} integration test negative case.")
   public void testCreateContactNoteNegativeCase() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContactNote_negative.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      
      String apiEndPoint = salesApiEndpoint + "contacts/INVALID/notes.json?";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getBody().get("message"), esbRestResponse.getBody().get("message"));
   }
   
   /**
    * Positive test case for getContactNotes method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactNoteNegativeCase" }, description = "BaseCRM {getContactNotes} integration test with mandatory parameters.")
   public void testGetContactNotesWithMandatoryParameters() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactNotes_mandatory.json",
                  parametersMap);
      
      JSONArray esbJsonArray = new JSONArray(esbRestResponse.getBody().getString("output"));
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint =
            salesApiEndpoint + "contacts/" + connectorProperties.getProperty("contactIdMandatory") + "/notes.json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      JSONArray apiJsonArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      
      String esbNoteContent = new JSONObject(esbJsonArray.getString(0)).getJSONObject("note").getString("content");
      String esbNoteId = new JSONObject(esbJsonArray.getString(0)).getJSONObject("note").getString("id");
      
      int esbNotesCount = new JSONArray(esbRestResponse.getBody().getString("output")).length();
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbNotesCount, apiJsonArray.getJSONObject(0).length());
      
      boolean matchContents = false;
      
      for (int i = 0; i < apiJsonArray.length(); i++) {
         JSONObject object = apiJsonArray.getJSONObject(i);
         if (esbNoteId.equals(object.getJSONObject("note").getString("id"))
               && esbNoteContent.equals(object.getJSONObject("note").getString("content"))) {
            matchContents = true;
            break;
         }
      }
      Assert.assertTrue(matchContents);
   }
   
   /**
    * Negative test case for getContactNotes method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetContactNotesWithMandatoryParameters" }, description = "BaseCRM {getContactNotes} integration test negative case.")
   public void testGetContactNotesNegativeCase() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactNotes_negative.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      
      String apiEndPoint = salesApiEndpoint + "/contacts/INVALID/notes.json?";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getBody().get("message"), esbRestResponse.getBody().get("message"));
   }
   
   /**
    * Positive test case for createContactReminder method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetContactNotesNegativeCase" }, description = "BaseCRM {createContactReminder} integration test with mandatory parameters.")
   public void testCreateContactReminderWithMandatoryParameters() throws Exception {
   
      parametersMap.put("reminderContent", "Contact Reminder Content");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContactReminder_mandatory.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint =
            salesApiEndpoint + "contacts/" + connectorProperties.getProperty("contactIdMandatory") + "/reminders.json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      JSONArray apiJsonArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      String apiContactId = apiJsonArray.getJSONObject(0).getJSONObject("reminder").getString("id");
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("reminder").getString("id"), apiContactId);
   }
   
   /**
    * Positive test case for createContactReminder method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactReminderWithMandatoryParameters" }, description = "BaseCRM {createContactReminder} integration test with optional parameters.")
   public void testCreateContactReminderWithOptionalParameters() throws Exception {
   
      parametersMap.put("reminderContent", "Contact Reminder Content Optional");
      parametersMap.put("reminderDone", "true");
      parametersMap.put("reminderRemind", "true");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContactReminder_optional.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint =
            salesApiEndpoint + "contacts/" + connectorProperties.getProperty("contactIdOptional") + "/reminders.json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      JSONArray apiJsonArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      String apiContactId = apiJsonArray.getJSONObject(0).getJSONObject("reminder").getString("id");
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("reminder").getString("id"), apiContactId);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("reminder").getString("done"), apiJsonArray
            .getJSONObject(0).getJSONObject("reminder").getString("done"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("reminder").getString("remind"), apiJsonArray
            .getJSONObject(0).getJSONObject("reminder").getString("remind"));
   }
   
   /**
    * Negative test case for createContactReminder method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactReminderWithOptionalParameters" }, description = "BaseCRM {createContactReminder} integration test negative case.")
   public void testCreateContactReminderNegativeCase() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContactReminder_negative.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      
      String apiEndPoint = salesApiEndpoint + "contacts/INVALID/reminders.json?";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getBody().get("message"), esbRestResponse.getBody().get("message"));
   }
   
   /**
    * Positive test case for getContactReminders method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactReminderNegativeCase" }, description = "BaseCRM {getContactReminders} integration test with mandatory parameters.")
   public void testGetContactRemindersWithMandatoryParameters() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactReminders_mandatory.json",
                  parametersMap);
      JSONArray esbJsonArray = new JSONArray(esbRestResponse.getBody().getString("output"));
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint =
            salesApiEndpoint + "contacts/" + connectorProperties.getProperty("contactIdMandatory") + "/reminders.json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      JSONArray apiJsonArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      
      int esbRemindersCount = new JSONArray(esbRestResponse.getBody().getString("output")).length();
      String esbReminderContent =
            new JSONObject(esbJsonArray.getString(0)).getJSONObject("reminder").getString("content");
      String esbReminderId = new JSONObject(esbJsonArray.getString(0)).getJSONObject("reminder").getString("id");
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRemindersCount, apiJsonArray.getJSONObject(0).length());
      
      boolean matchContents = false;
      
      for (int i = 0; i < apiJsonArray.length(); i++) {
         JSONObject object = apiJsonArray.getJSONObject(i);
         if (esbReminderId.equals(object.getJSONObject("reminder").getString("id"))
               && esbReminderContent.equals(object.getJSONObject("reminder").getString("content"))) {
            matchContents = true;
            break;
         }
      }
      Assert.assertTrue(matchContents);
   }
   
   /**
    * Negative test case for getContactReminders method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetContactRemindersWithMandatoryParameters" }, description = "BaseCRM {getContactReminders} integration test negative case.")
   public void testGetContactRemindersNegativeCase() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactReminders_negative.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      
      String apiEndPoint = salesApiEndpoint + "contacts/INVALID/reminders.json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getBody().get("message"), esbRestResponse.getBody().get("message"));
   }
   
   /**
    * Positive test case for createDeal method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetContactRemindersNegativeCase" }, description = "BaseCRM {createDeal} integration test with mandatory parameters.")
   public void testCreateDealWithMandatoryParameters() throws Exception {
   
      parametersMap.put("name", "Deal Name");
      parametersMap.put("entityId", connectorProperties.getProperty("contactIdMandatory"));
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDeal_mandatory.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String dealId = esbRestResponse.getBody().getJSONObject("deal").getString("id");
      connectorProperties.setProperty("dealIdMandatory", dealId);
      
      String apiEndPoint = salesApiEndpoint + "deals/" + dealId + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(parametersMap.get("name"), apiRestResponse.getBody().getJSONObject("deal").getString("name"));
      Assert.assertEquals(parametersMap.get("entityId"),
            apiRestResponse.getBody().getJSONObject("deal").getString("entity_id"));
   }
   
   /**
    * Positive test case for createDeal method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDealWithMandatoryParameters" }, description = "BaseCRM {createDeal} integration test with optional parameters.")
   public void testCreateDealWithOptionalParameters() throws Exception {
   
      parametersMap.put("name", "Deal Name Optional");
      parametersMap.put("entityId", connectorProperties.getProperty("contactIdMandatory"));
      parametersMap.put("scope", "200");
      parametersMap.put("hot", "true");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDeal_optional.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String dealId = esbRestResponse.getBody().getJSONObject("deal").getString("id");
      connectorProperties.setProperty("dealIdOptional", dealId);
      
      String apiEndPoint = salesApiEndpoint + "deals/" + dealId + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(parametersMap.get("name"), apiRestResponse.getBody().getJSONObject("deal").getString("name"));
      Assert.assertEquals(parametersMap.get("entityId"),
            apiRestResponse.getBody().getJSONObject("deal").getString("entity_id"));
      Assert.assertEquals(parametersMap.get("scope"), apiRestResponse.getBody().getJSONObject("deal")
            .getString("scope"));
      Assert.assertEquals(parametersMap.get("hot"), apiRestResponse.getBody().getJSONObject("deal").getString("hot"));
   }
   
   /**
    * Negative test case for createDeal method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDealWithOptionalParameters" }, description = "BaseCRM {createDeal} integration test negative case.")
   public void testCreateDealNegativeCase() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDeal_negative.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
      
      String apiEndPoint = salesApiEndpoint + "deals.json";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createDeal_negative.json");
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
   }
   
   /**
    * Positive test case for getContactDeals method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDealNegativeCase" }, description = "BaseCRM {getContactDeals} integration test with mandatory parameters.")
   public void testGetContactDealsWithMandatoryParameters() throws Exception {
   
      parametersMap.put("contactDealId", connectorProperties.getProperty("contactIdMandatory"));
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactDeals_mandatory.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      JSONArray esbJsonArray = new JSONArray(esbRestResponse.getBody().getString("output"));
      String esbContactID =
            new JSONObject(esbJsonArray.getJSONObject(0).getJSONObject("deal").getString("deal_account"))
                  .getString("id");
      
      String apiEndPoint = salesApiEndpoint + "contacts/" + parametersMap.get("contactDealId") + "/deals.json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      JSONArray apiJsonArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      
      String apiContactID =
            new JSONObject(apiJsonArray.getJSONObject(0).getJSONObject("deal").getString("deal_account"))
                  .getString("id");
      Assert.assertEquals(esbContactID, apiContactID);
   }
   
   /**
    * Negative test case for getContactDeals method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetContactDealsWithMandatoryParameters" }, description = "BaseCRM {getContactDeals} integration test negative case.")
   public void testGetContactDealsNegativeCase() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactDeals_negative.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      JSONArray esbJsonArray = new JSONArray(esbRestResponse.getBody().getString("output"));
      
      Assert.assertEquals(esbJsonArray.length(), 0);
      
      String apiEndPoint = salesApiEndpoint + "contacts/INVALID/deals.json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      
      JSONArray apiJsonArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      
      Assert.assertEquals(apiJsonArray.length(), 0);
   }
   
   /**
    * Positive test case for setDeal method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetContactDealsNegativeCase" }, description = "BaseCRM {setDeal} integration test with mandatory parameters.")
   public void testSetDealWithMandatoryParameters() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setDeal_mandatory.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String dealId = esbRestResponse.getBody().getJSONObject("deal").getString("id");
      
      String apiEndPoint = salesApiEndpoint + "deals/" + dealId + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(connectorProperties.getProperty("dealIdMandatory"),
            apiRestResponse.getBody().getJSONObject("deal").getString("id"));
   }
   
   /**
    * Positive test case for setDeal method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetDealWithMandatoryParameters" }, description = "BaseCRM {setDeal} integration test with optional parameters.")
   public void testSetDealWithOptionalParameters() throws Exception {
   
      parametersMap.put("name", "Deal Name Updated");
      parametersMap.put("scope", "250");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setDeal_optional.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String dealId = esbRestResponse.getBody().getJSONObject("deal").getString("id");
      
      String apiEndPoint = salesApiEndpoint + "deals/" + dealId + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(connectorProperties.getProperty("dealIdMandatory"),
            apiRestResponse.getBody().getJSONObject("deal").getString("id"));
      
      Assert.assertEquals(parametersMap.get("name"), apiRestResponse.getBody().getJSONObject("deal").getString("name"));
      Assert.assertEquals(parametersMap.get("scope"), apiRestResponse.getBody().getJSONObject("deal")
            .getString("scope"));
   }
   
   /**
    * Negative test case for setDeal method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetDealWithOptionalParameters" }, description = "BaseCRM {setDeal} integration test negative case.")
   public void testSetDealNegativeCase() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setDeal_negative.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      
      String apiEndPoint = salesApiEndpoint + "deals/" + "INVALID" + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
   }
   
   /**
    * Positive test case for listDeals method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetDealNegativeCase" }, description = "BaseCRM {listDeals} integration test with mandatory parameters.")
   public void testListDealsWithMandatoryParameters() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDeals_mandatory.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      JSONArray esbJsonArray = new JSONArray(esbRestResponse.getBody().getString("output"));
      int esbDealsCount = esbJsonArray.length();
      
      String apiEndPoint = salesApiEndpoint + "deals.json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      
      JSONArray apiJsonArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      int apiDealsCount = apiJsonArray.length();
      
      /*
       * List of deals are getting shuffled from request to request. Then first deal information of the response is
       * different from both ESB and API responses. Therefore we cannot do assertion on information inside first deal.
       */
      
      Assert.assertEquals(esbDealsCount, apiDealsCount);
   }
   
   /**
    * Positive test case for listDeals method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListDealsWithMandatoryParameters" }, description = "BaseCRM {listDeals} integration test with optional parameters.")
   public void testListDealsWithOptionalParameters() throws Exception {
   
      parametersMap.put("stage", "won");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDeals_optional.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      JSONArray esbJsonArray = new JSONArray(esbRestResponse.getBody().getString("output"));
      String esbStageCode = esbJsonArray.getJSONObject(0).getJSONObject("deal").getString("stage_code");
      
      String apiEndPoint = salesApiEndpoint + "deals.json?stage=" + parametersMap.get("stage");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      JSONArray apiJsonArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      String apiStageCode = apiJsonArray.getJSONObject(0).getJSONObject("deal").getString("stage_code");
      
      Assert.assertEquals(esbStageCode, apiStageCode);
   }
   
   /**
    * Negative test case for listDeals method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListDealsWithOptionalParameters" }, description = "BaseCRM {listDeals} integration test negative case.")
   public void testListDealsNegativeCase() throws Exception {
   
      parametersMap.put("stageNegative", "invalid");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDeals_negative.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      
      String apiEndPoint = salesApiEndpoint + "deals.json?stage=" + parametersMap.get("stageNegative");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
   }
   
   /**
    * Positive test case for createDealNote method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListDealsNegativeCase" }, description = "BaseCRM {createDealNote} integration test with mandatory parameters.")
   public void testCreateDealNoteWithMandatoryParameters() throws Exception {
   
      parametersMap.put("noteContent", "Deal Note Content");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDealNote_mandatory.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint =
            salesApiEndpoint + "deals/" + connectorProperties.getProperty("dealIdMandatory") + "/notes.json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      JSONArray apiJsonArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      String apiDealId = apiJsonArray.getJSONObject(0).getJSONObject("note").getString("id");
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("note").getString("id"), apiDealId);
      Assert.assertEquals(parametersMap.get("noteContent"), apiJsonArray.getJSONObject(0).getJSONObject("note")
            .getString("content"));
   }
   
   /**
    * Negative test case for createDealNote method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDealNoteWithMandatoryParameters" }, description = "BaseCRM {createDealNote} integration test negative case.")
   public void testCreateDealNoteNegativeCase() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDealNote_negative.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      
      String apiEndPoint = salesApiEndpoint + "deals/INVALID/notes.json?";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getBody().get("message"), esbRestResponse.getBody().get("message"));
   }
   
   /**
    * Positive test case for getDealNotes method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDealNoteNegativeCase" }, description = "BaseCRM {getDealNotes} integration test with mandatory parameters.")
   public void testGetDealNotesWithMandatoryParameters() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDealNotes_mandatory.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint =
            salesApiEndpoint + "deals/" + connectorProperties.getProperty("dealIdMandatory") + "/notes.json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      JSONArray apiJsonArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      
      int esbNotesCount = new JSONArray(esbRestResponse.getBody().getString("output")).length();
      JSONArray esbJsonArray = new JSONArray(esbRestResponse.getBody().getString("output"));
      String esbNoteContent = new JSONObject(esbJsonArray.getString(0)).getJSONObject("note").getString("content");
      String esbNoteId = new JSONObject(esbJsonArray.getString(0)).getJSONObject("note").getString("id");
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbNotesCount, apiJsonArray.getJSONObject(0).length());
      
      boolean matchContents = false;
      
      for (int i = 0; i < apiJsonArray.length(); i++) {
         JSONObject object = apiJsonArray.getJSONObject(i);
         if (esbNoteId.equals(object.getJSONObject("note").getString("id"))
               && esbNoteContent.equals(object.getJSONObject("note").getString("content"))) {
            matchContents = true;
            break;
         }
      }
      Assert.assertTrue(matchContents);
   }
   
   /**
    * Negative test case for getDealNotes method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetDealNotesWithMandatoryParameters" }, description = "BaseCRM {getDealNotes} integration test negative case.")
   public void testGetDealNotesNegativeCase() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDealNotes_negative.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      
      String apiEndPoint = salesApiEndpoint + "deals/INVALID/notes.json?";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getBody().get("message"), esbRestResponse.getBody().get("message"));
   }
   
   /**
    * Positive test case for createDealReminder method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetDealNotesNegativeCase" }, description = "BaseCRM {createDealReminder} integration test with mandatory parameters.")
   public void testCreateDealReminderWithMandatoryParameters() throws Exception {
   
      parametersMap.put("reminderContent", "Deal Reminder Content");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDealReminder_mandatory.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint =
            salesApiEndpoint + "deals/" + connectorProperties.getProperty("dealIdMandatory") + "/reminders.json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      JSONArray apiJsonArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      String apiDealId = apiJsonArray.getJSONObject(0).getJSONObject("reminder").getString("id");
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("reminder").getString("id"), apiDealId);
   }
   
   /**
    * Positive test case for createDealReminder method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDealReminderWithMandatoryParameters" }, description = "BaseCRM {createDealReminder} integration test with optional parameters.")
   public void testCreateDealReminderWithOptionalParameters() throws Exception {
   
      parametersMap.put("reminderContent", "Deal Reminder Content Optional");
      parametersMap.put("reminderDone", "true");
      parametersMap.put("reminderRemind", "true");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDealReminder_optional.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint =
            salesApiEndpoint + "deals/" + connectorProperties.getProperty("dealIdOptional") + "/reminders.json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      JSONArray apiJsonArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      String apiDealId = apiJsonArray.getJSONObject(0).getJSONObject("reminder").getString("id");
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("reminder").getString("id"), apiDealId);
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("reminder").getString("done"), apiJsonArray
            .getJSONObject(0).getJSONObject("reminder").getString("done"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("reminder").getString("remind"), apiJsonArray
            .getJSONObject(0).getJSONObject("reminder").getString("remind"));
   }
   
   /**
    * Negative test case for createDealReminder method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDealReminderWithOptionalParameters" }, description = "BaseCRM {createDealReminder} integration test negative case.")
   public void testCreateDealReminderNegativeCase() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDealReminder_negative.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      
      String apiEndPoint = salesApiEndpoint + "deals/INVALID/reminders.json?";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getBody().get("message"), esbRestResponse.getBody().get("message"));
   }
   
   /**
    * Positive test case for getDealReminders method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDealReminderNegativeCase" }, description = "BaseCRM {getDealReminders} integration test with mandatory parameters.")
   public void testGetDealRemindersWithMandatoryParameters() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDealReminders_mandatory.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint =
            salesApiEndpoint + "deals/" + connectorProperties.getProperty("dealIdMandatory") + "/reminders.json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      JSONArray apiJsonArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      
      int esbRemindersCount = new JSONArray(esbRestResponse.getBody().getString("output")).length();
      JSONArray esbJsonArray = new JSONArray(esbRestResponse.getBody().getString("output"));
      String esbReminderContent =
            new JSONObject(esbJsonArray.getString(0)).getJSONObject("reminder").getString("content");
      String esbReminderId = new JSONObject(esbJsonArray.getString(0)).getJSONObject("reminder").getString("id");
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRemindersCount, apiJsonArray.getJSONObject(0).length());
      
      boolean matchContents = false;
      
      for (int i = 0; i < apiJsonArray.length(); i++) {
         JSONObject object = apiJsonArray.getJSONObject(i);
         if (esbReminderId.equals(object.getJSONObject("reminder").getString("id"))
               && esbReminderContent.equals(object.getJSONObject("reminder").getString("content"))) {
            matchContents = true;
            break;
         }
      }
      Assert.assertTrue(matchContents);
   }
   
   /**
    * Negative test case for getDealReminders method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetDealRemindersWithMandatoryParameters" }, description = "BaseCRM {getDealReminders} integration test negative case.")
   public void testGetDealRemindersNegativeCase() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDealReminders_negative.json",
                  parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      
      String apiEndPoint = salesApiEndpoint + "deals/INVALID/reminders.json?";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getBody().get("message"), esbRestResponse.getBody().get("message"));
   }
   
   /**
    * Positive test case for createLead method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetDealRemindersNegativeCase" }, description = "BaseCRM {createLead} integration test with mandatory parameters.")
   public void testCreateLeadWithMandatoryParameters() throws Exception {
   
      parametersMap.put("leadLastName", "LeadLastName");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createLead_mandatory.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String leadId = esbRestResponse.getBody().getJSONObject("lead").getString("id");
      connectorProperties.setProperty("leadIdMandatory", leadId);
      
      String apiEndPoint = leadsApiEndpoint + "leads/" + leadId + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(parametersMap.get("leadLastName"),
            apiRestResponse.getBody().getJSONObject("lead").get("last_name"));
   }
   
   /**
    * Positive test case for createLead method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateLeadWithMandatoryParameters" }, description = "BaseCRM {createLead} integration test with optional parameters.")
   public void testCreateLeadWithOptionalParameters() throws Exception {
   
      parametersMap.put("leadLastName", "LeadLastName");
      parametersMap.put("leadFirstName", "LeadFirstName");
      parametersMap.put("leadEmail", "leademail@gmail.com");
      parametersMap.put("leadTitle", "Engineer");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createLead_optional.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String leadId = esbRestResponse.getBody().getJSONObject("lead").getString("id");
      connectorProperties.setProperty("leadIdOptional", leadId);
      
      String apiEndPoint = leadsApiEndpoint + "leads/" + leadId + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(parametersMap.get("leadFirstName"),
            apiRestResponse.getBody().getJSONObject("lead").get("first_name"));
      Assert.assertEquals(parametersMap.get("leadEmail"), apiRestResponse.getBody().getJSONObject("lead").get("email"));
      Assert.assertEquals(parametersMap.get("leadTitle"), apiRestResponse.getBody().getJSONObject("lead").get("title"));
   }
   
   /**
    * Negative test case for createLead method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateLeadWithOptionalParameters" }, description = "BaseCRM {createLead} integration test negative case.")
   public void testCreateLeadNegativeCase() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createLead_negative.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
      
      String apiEndPoint = leadsApiEndpoint + "leads.json";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createLead_negative.json");
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"),
            apiRestResponse.getBody().getBoolean("success"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").getJSONArray("base").getJSONObject(0)
            .getString("message"), apiRestResponse.getBody().getJSONObject("errors").getJSONArray("base")
            .getJSONObject(0).getString("message"));
   }
   
   /**
    * Positive test case for listLeads method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateLeadNegativeCase" }, description = "BaseCRM {listLeads} integration test with mandatory parameters.")
   public void testListLeadsWithMandatoryParameters() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listLeads_mandatory.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      boolean esbLeadsSuccess = Boolean.parseBoolean(esbRestResponse.getBody().getString("success"));
      int esbLeadsCount = esbRestResponse.getBody().getJSONObject("metadata").getInt("count");
      
      String apiEndPoint = leadsApiEndpoint + "leads.json";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      boolean apiLeadsSuccess = Boolean.parseBoolean(apiRestResponse.getBody().getString("success"));
      int apiLeadsCount = esbRestResponse.getBody().getJSONObject("metadata").getInt("count");
      
      /*
       * List of leads are getting shuffled from request to request. Then first lead information of the response is
       * different from both ESB and API responses. Therefore we cannot do assertion on information inside first lead.
       */
      
      Assert.assertEquals(esbLeadsSuccess, apiLeadsSuccess);
      Assert.assertEquals(esbLeadsCount, apiLeadsCount);
   }
   
   /**
    * Positive test case for listLeads method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListLeadsWithMandatoryParameters" }, description = "BaseCRM {listLeads} integration test with optional parameters.")
   public void testListLeadsWithOptionalParameters() throws Exception {
   
      parametersMap.put("sortBy", "first_name");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listLeads_optional.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      boolean esbLeadsSuccess = Boolean.parseBoolean(esbRestResponse.getBody().getString("success"));
      int esbLeadsCount = esbRestResponse.getBody().getJSONObject("metadata").getInt("count");
      JSONObject esbLeadFirstElement = new JSONObject(esbRestResponse.getBody().getJSONArray("items").getString(0));
      
      String apiEndPoint = leadsApiEndpoint + "leads.json?sort_by=" + parametersMap.get("sortBy");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      boolean apiLeadsSuccess = Boolean.parseBoolean(apiRestResponse.getBody().getString("success"));
      int apiLeadsCount = esbRestResponse.getBody().getJSONObject("metadata").getInt("count");
      JSONObject apiLeadFirstElement = new JSONObject(apiRestResponse.getBody().getJSONArray("items").getString(0));
      
      Assert.assertEquals(esbLeadsSuccess, apiLeadsSuccess);
      Assert.assertEquals(esbLeadsCount, apiLeadsCount);
      Assert.assertEquals(esbLeadFirstElement.getJSONObject("lead").getString("first_name"), apiLeadFirstElement
            .getJSONObject("lead").getString("first_name"));
   }
   
   /**
    * Positive test case for setLead method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListLeadsWithOptionalParameters" }, description = "BaseCRM {setLead} integration test with mandatory parameters.")
   public void testSetLeadWithMandatoryParameters() throws Exception {
   
      parametersMap.put("leadLastName", "LeadLastNameUpdated");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setLead_mandatory.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint = leadsApiEndpoint + "leads/" + connectorProperties.getProperty("leadIdMandatory") + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(parametersMap.get("leadLastName"),
            apiRestResponse.getBody().getJSONObject("lead").get("last_name"));
   }
   
   /**
    * Positive test case for setLead method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetLeadWithMandatoryParameters" }, description = "BaseCRM {setLead} integration test with optional parameters.")
   public void testSetLeadWithOptionalParameters() throws Exception {
   
      parametersMap.put("leadLastName", "LeadLastNameUpdated");
      parametersMap.put("leadFirstName", "LeadFirstNameUpdated");
      parametersMap.put("leadEmail", "leademailupdated@gmail.com");
      parametersMap.put("leadTitle", "CEO");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setLead_optional.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint = leadsApiEndpoint + "leads/" + connectorProperties.getProperty("leadIdOptional") + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(parametersMap.get("leadFirstName"),
            apiRestResponse.getBody().getJSONObject("lead").get("first_name"));
      Assert.assertEquals(parametersMap.get("leadEmail"), apiRestResponse.getBody().getJSONObject("lead").get("email"));
      Assert.assertEquals(parametersMap.get("leadTitle"), apiRestResponse.getBody().getJSONObject("lead").get("title"));
   }
   
   /**
    * Negative test case for setLead method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteLeadWithMandatoryParameters" }, description = "BaseCRM {setLead} integration test negative case.")
   public void testSetLeadNegativeCase() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setLead_negative.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      
      String apiEndPoint = leadsApiEndpoint + "leads/" + connectorProperties.getProperty("leadIdMandatory") + ".json";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_setLead_negative.json");
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"),
            apiRestResponse.getBody().getBoolean("success"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").getJSONArray("lead").getJSONObject(0)
            .getString("message"), apiRestResponse.getBody().getJSONObject("errors").getJSONArray("lead")
            .getJSONObject(0).getString("message"));
   }
   
   /**
    * Positive test case for deleteLead method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetLeadWithMandatoryParameters" }, description = "BaseCRM {deleteLead} integration test with mandatory parameters.")
   public void testDeleteLeadWithMandatoryParameters() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteLead_mandatory.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      
      String apiEndPoint = leadsApiEndpoint + "leads/" + connectorProperties.getProperty("leadIdMandatory") + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("errors").getJSONArray("lead").getJSONObject(0)
            .getString("message"), "Resource not found.");
   }
   
   /**
    * Negative test case for deleteLead method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSetLeadNegativeCase" }, description = "BaseCRM {deleteLead} integration test negative case.")
   public void testDeleteLeadNegativeCase() throws Exception {
   
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteLead_negative.json", parametersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      
      String apiEndPoint = leadsApiEndpoint + "leads/" + connectorProperties.getProperty("leadIdMandatory") + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"),
            apiRestResponse.getBody().getBoolean("success"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").getJSONArray("lead").getJSONObject(0)
            .getString("message"), apiRestResponse.getBody().getJSONObject("errors").getJSONArray("lead")
            .getJSONObject(0).getString("message"));
   }
   
}
