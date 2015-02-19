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

package org.wso2.carbon.connector.integration.test.freshdesk;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
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

public class FreshdeskConnectorIntegrationTest extends ConnectorIntegrationTestBase {
   
   private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
   
   private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
   
   private SimpleDateFormat sdf;
   
   /**
    * Set up the environment.
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {
   
      init("freshdesk-connector-2.0.0");
      sdf = new SimpleDateFormat("yyyy-MM-dd");
      esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
      esbRequestHeadersMap.put("Content-Type", "application/json");
      
      apiRequestHeadersMap.putAll(esbRequestHeadersMap);
      final String authString = connectorProperties.getProperty("apiKey") + ":X";
      final String authorizationHeader = "Basic " + Base64.encode(authString.getBytes());
      apiRequestHeadersMap.put("Authorization", authorizationHeader);
   }
   
   /**
    * Positive test case for createTicket method with mandatory parameters.
    */
   @Test(priority = 1, description = "FreshDesk {createTicket} integration test with mandatory parameters.")
   public void testCreateTicketWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createTicket");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTicket_mandatory.json");
      String ticketId = esbRestResponse.getBody().getJSONObject("helpdesk_ticket").getString("display_id");
      connectorProperties.put("ticketId", ticketId);
      
      final String apiUrl = connectorProperties.getProperty("apiUrl") + "/helpdesk/tickets/" + ticketId + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      final String createdAt =
            apiRestResponse.getBody().getJSONObject("helpdesk_ticket").getString("created_at").split("T")[0];
      
      final String email[] = connectorProperties.getProperty("email").split("@");
      final String name = email[email.length - 2];
      
      Assert.assertEquals(createdAt, sdf.format(new Date()));
      Assert.assertEquals(apiRestResponse.getBody().getJSONObject("helpdesk_ticket").getString("requester_name")
            .toLowerCase(), name.toLowerCase());
      
   }
   
   /**
    * Positive test case for createTicket method with optional parameters.
    */
   @Test(priority = 1, description = "FreshDesk {createTicket} integration test with optional parameters.")
   public void testCreateTicketWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createTicket");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTicket_optional.json");
      final String ticketId = esbRestResponse.getBody().getJSONObject("helpdesk_ticket").getString("display_id");
      connectorProperties.put("ticketIdOptional", ticketId);
      
      final String apiUrl = connectorProperties.getProperty("apiUrl") + "/helpdesk/tickets/" + ticketId + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      final String createdAt =
            apiRestResponse.getBody().getJSONObject("helpdesk_ticket").getString("created_at").split("T")[0];
      
      Assert.assertEquals(createdAt, sdf.format(new Date()));
      Assert.assertEquals(connectorProperties.getProperty("ticketDescription"), apiRestResponse.getBody()
            .getJSONObject("helpdesk_ticket").getString("description"));
      Assert.assertEquals(connectorProperties.getProperty("source"),
            apiRestResponse.getBody().getJSONObject("helpdesk_ticket").getString("source"));
      Assert.assertEquals(connectorProperties.getProperty("status"),
            apiRestResponse.getBody().getJSONObject("helpdesk_ticket").getString("status"));
      
   }
   
   /**
    * Negative test case for createTicket method with mandatory parameters.
    */
   @Test(priority = 1, description = "FreshDesk {createTicket} integration test with negative case.")
   public void testCreateTicketWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createTicket");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTicket_negative.json");
      
      final String apiUrl = connectorProperties.getProperty("apiUrl") + "/helpdesk/tickets.json";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiUrl, "POST", apiRequestHeadersMap, "api_createTicket_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      
   }
   
   /**
    * Positive test case for getTicket method with mandatory parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testCreateTicketWithMandatoryParameters" }, description = "FreshDesk {getTicket} integration test with mandatory parameters.")
   public void testGetTicketWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getTicket");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTicket_mandatory.json");
      
      final String apiUrl =
            connectorProperties.getProperty("apiUrl") + "/helpdesk/tickets/"
                  + connectorProperties.getProperty("ticketId") + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("helpdesk_ticket").getString("requester_name"),
            apiRestResponse.getBody().getJSONObject("helpdesk_ticket").getString("requester_name"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("helpdesk_ticket").getString("description"),
            apiRestResponse.getBody().getJSONObject("helpdesk_ticket").getString("description"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("helpdesk_ticket").getString("requester_id"),
            apiRestResponse.getBody().getJSONObject("helpdesk_ticket").getString("requester_id"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("helpdesk_ticket").getString("display_id"),
            apiRestResponse.getBody().getJSONObject("helpdesk_ticket").getString("display_id"));
      
   }
   
   /**
    * Negative test case for getTicket method with mandatory parameters.
    */
   @Test(priority = 1, description = "FreshDesk {getTicket} integration test with negative case.")
   public void testGetTicketWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getTicket");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTicket_negative.json");
      
      final String apiUrl = connectorProperties.getProperty("apiUrl") + "/helpdesk/tickets/INVALID.json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").getString("error"), apiRestResponse
            .getBody().getJSONObject("errors").getString("error"));
      
   }
   
   /**
    * Positive test case for listCustomers method with mandatory parameters.
    */
   @Test(priority = 1, description = "FreshDesk {listCustomers} integration test with mandatory parameters.")
   public void testListCustomersWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listCustomers");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomers_mandatory.json");
      JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
      
      final String apiUrl = connectorProperties.getProperty("apiUrl") + "/customers.json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      final int length = esbResponseArray.length();
      
      Assert.assertEquals(length, apiResponseArray.length());
      if (length > 0) {
         Assert.assertEquals(esbResponseArray.getJSONObject(0).getJSONObject("customer").getString("id"),
               apiResponseArray.getJSONObject(0).getJSONObject("customer").getString("id"));
      }
   }
   
   /**
    * Positive test case for listCustomers method with optional parameters.
    */
   @Test(priority = 1, description = "FreshDesk {listCustomers} integration test with optional parameters.")
   public void testListCustomersWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listCustomers");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomers_optional.json");
      final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
      
      final String apiUrl =
            connectorProperties.getProperty("apiUrl") + "/customers.json?letter="
                  + connectorProperties.getProperty("letter");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      
      final int length = esbResponseArray.length();
      Assert.assertEquals(length, apiResponseArray.length());
      // Asserting the id's of the customers returned as part of the response.
      if (length > 0) {
         
         Assert.assertEquals(esbResponseArray.getJSONObject(0).getJSONObject("customer").getString("id"),
               apiResponseArray.getJSONObject(0).getJSONObject("customer").getString("id"));
         
      }
      
   }
   
   /**
    * Positive test case for listTickets method with mandatory parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testCreateTicketWithMandatoryParameters" }, description = "FreshDesk {listTickets} integration test with mandatory parameters.")
   public void testListTicketsWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listTickets");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTickets_mandatory.json");
      final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
      
      final String apiUrl = connectorProperties.getProperty("apiUrl") + "/helpdesk/tickets.json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      
      final int length = esbResponseArray.length();
      
      Assert.assertEquals(length, apiResponseArray.length());
      // Asserting the id's of the tickets that were returned as part of the
      // response.
      if (length > 0) {
         Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
               .getString("id"));
      }
      
   }
   
   /**
    * Positive test case for listTickets method with optional parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testCreateTicketWithMandatoryParameters" }, description = "FreshDesk {listTickets} integration test with optional parameters.")
   public void testListTicketsWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listTickets");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTickets_optional.json");
      final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
      
      final String apiUrl =
            connectorProperties.getProperty("apiUrl") + "/helpdesk/tickets.json?company_name="
                  + connectorProperties.getProperty("companyName") + "&filter_name="
                  + connectorProperties.getProperty("filterName");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      
      final int length = esbResponseArray.length();
      Assert.assertEquals(length, apiResponseArray.length());
      // Asserting the id's of the tickets that were returned as part of the
      // response.
      if (length > 0) {
         Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
               .getString("id"));
      }
      
   }
   
   /**
    * Positive test case for updateTicket method with optional parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testCreateTicketWithMandatoryParameters" }, description = "FreshDesk {updateTicket} integration test with optional parameters.")
   public void testUpdateTicketWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateTicket");
      
      final String apiUrl =
            connectorProperties.getProperty("apiUrl") + "/helpdesk/tickets/"
                  + connectorProperties.getProperty("ticketId") + ".json";
      
      RestResponse<JSONObject> apiRestResponseInitial = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      final String initialSubject =
            apiRestResponseInitial.getBody().getJSONObject("helpdesk_ticket").getString("subject");
      final String initialPriority =
            apiRestResponseInitial.getBody().getJSONObject("helpdesk_ticket").getString("priority_name");
      
      sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTicket_optional.json");
      
      RestResponse<JSONObject> apiRestResponseEventual = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      final String eventualSubject =
            apiRestResponseEventual.getBody().getJSONObject("helpdesk_ticket").getString("subject");
      final String eventualPriority =
            apiRestResponseEventual.getBody().getJSONObject("helpdesk_ticket").getString("priority_name");
      
      Assert.assertNotEquals(initialSubject, eventualSubject);
      Assert.assertNotEquals(initialPriority, eventualPriority);
   }
   
   /**
    * Positive test case for updateTicket method with negative case.
    */
   @Test(priority = 1, description = "FreshDesk {updateTicket} integration test with negative case.")
   public void testUpdateTicketWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:updateTicket");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTicket_negative.json");
      
      final String apiUrl =
            connectorProperties.getProperty("apiUrl") + "/helpdesk/tickets/"
                  + connectorProperties.getProperty("ticketId") + ".json";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiUrl, "PUT", apiRequestHeadersMap, "api_updateTicket_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").getString(0), apiRestResponse.getBody()
            .getJSONArray("errors").getString(0));
      
   }
   
   /**
    * Positive test case for assignTicket method with mandatory parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testCreateTicketWithMandatoryParameters",
         "testListUsersWithMandatoryParameters" }, description = "FreshDesk {assignTicket} integration test with mandatory parameters.")
   public void testAssignTicketWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:assignTicket");
      
      // Get the responder_id of the ticket - Get ticket call
      final String apiUrlGetTicket =
            connectorProperties.getProperty("apiUrl") + "/helpdesk/tickets/"
                  + connectorProperties.getProperty("ticketId") + ".json";
      RestResponse<JSONObject> apiRestResponseGetTicket =
            sendJsonRestRequest(apiUrlGetTicket, "GET", apiRequestHeadersMap);
      
      // Initially responder_id should be null
      Assert.assertTrue(apiRestResponseGetTicket.getBody().getJSONObject("helpdesk_ticket").getString("responder_id")
            .equals("null"));
      
      // Assign the ticket to a user/agent
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_assignTicket_mandatory.json");
      final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
      final String responderName =
            esbResponseArray.getJSONObject(0).getJSONObject("ticket").getString("responder_name");
      
      // Get the responder_id of the ticket - Get ticket call
      apiRestResponseGetTicket = sendJsonRestRequest(apiUrlGetTicket, "GET", apiRequestHeadersMap);
      final String responderId =
            apiRestResponseGetTicket.getBody().getJSONObject("helpdesk_ticket").getString("responder_id");
      
      // Get the corresponding name of the user whose responder_id is obtained
      // above
      final String apiUrlGetUser = connectorProperties.getProperty("apiUrl") + "/contacts/" + responderId + ".json";
      RestResponse<JSONObject> apiRestResponseGetUser = sendJsonRestRequest(apiUrlGetUser, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(responderId, connectorProperties.getProperty("userId"));
      Assert.assertEquals(responderName, apiRestResponseGetUser.getBody().getJSONObject("user").getString("name"));
      
   }
   
   /**
    * Negative test case for assignTicket method with mandatory parameters.
    */
   @Test(priority = 1, description = "FreshDesk {assignTicket} integration test with negative case.")
   public void testAssignTicketWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:assignTicket");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_assignTicket_negative.json");
      
      final String apiUrl =
            connectorProperties.getProperty("apiUrl") + "/helpdesk/tickets/"
                  + connectorProperties.getProperty("ticketId") + "/assign.json?responder_id=INVALID";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "PUT", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").getString("error"), apiRestResponse
            .getBody().getJSONObject("errors").getString("error"));
      
   }
   
   /**
    * Positive test case for listUsers method with mandatory parameters.
    */
   @Test(priority = 1, description = "FreshDesk {listUsers} integration test with mandatory parameters.")
   public void testListUsersWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listUsers");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_mandatory.json");
      JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
      
      final String apiUrl =
            connectorProperties.getProperty("apiUrl") + "/contacts.json?state="
                  + connectorProperties.getProperty("state");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      
      final int length = esbResponseArray.length();
      
      Assert.assertEquals(length, apiResponseArray.length());
      // Asserting the id's and emails of the users returned as part of the
      // response.
      if (length > 0) {
         Assert.assertEquals(esbResponseArray.getJSONObject(0).getJSONObject("user").getString("id"), apiResponseArray
               .getJSONObject(0).getJSONObject("user").getString("id"));
         Assert.assertEquals(esbResponseArray.getJSONObject(0).getJSONObject("user").getString("email"),
               apiResponseArray.getJSONObject(0).getJSONObject("user").getString("email"));
         connectorProperties.put("userId", esbResponseArray.getJSONObject(0).getJSONObject("user").getString("id"));
      }
      
   }
   
   /**
    * Positive test case for listUsers method with optional parameters.
    */
   @Test(priority = 1, description = "FreshDesk {listUsers} integration test with optional parameters.")
   public void testListUsersWithOptionalParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:listUsers");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_optional.json");
      final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
      
      final String query = "email is " + connectorProperties.getProperty("userEmail");
      final String apiUrl =
            connectorProperties.getProperty("apiUrl") + "/contacts.json?query=" + URLEncoder.encode(query, "UTF-8")
                  + "&state=" + connectorProperties.getProperty("state");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      
      final int length = esbResponseArray.length();
      Assert.assertEquals(length, apiResponseArray.length());
      // Asserting the id's and emails of the users returned as part of the
      // response.
      if (length > 0) {
         Assert.assertEquals(esbResponseArray.getJSONObject(0).getJSONObject("user").getString("id"), apiResponseArray
               .getJSONObject(0).getJSONObject("user").getString("id"));
         Assert.assertEquals(esbResponseArray.getJSONObject(0).getJSONObject("user").getString("email"),
               apiResponseArray.getJSONObject(0).getJSONObject("user").getString("email"));
      }
      
   }
   
   /**
    * Positive test case for addNote method with mandatory parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testCreateTicketWithMandatoryParameters" }, description = "FreshDesk {addNote} integration test with mandatory parameters.")
   public void testAddNoteWithMandatoryParameters() throws IOException, JSONException {
   
      final String apiUrl =
            connectorProperties.getProperty("apiUrl") + "/helpdesk/tickets/"
                  + connectorProperties.getProperty("ticketId") + ".json";
      RestResponse<JSONObject> apiRestResponse1 = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      final JSONArray apiNotes1 = apiRestResponse1.getBody().getJSONObject("helpdesk_ticket").getJSONArray("notes");
      
      esbRequestHeadersMap.put("Action", "urn:addNote");
      sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addNote_mandatory.json");
      
      RestResponse<JSONObject> apiRestResponse2 = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      final JSONArray apiNotes2 = apiRestResponse2.getBody().getJSONObject("helpdesk_ticket").getJSONArray("notes");
      final JSONObject note = (JSONObject) apiNotes2.getJSONObject(0).get("note");
      final String createdAt = note.getString("created_at").split("T")[0];
      
      Assert.assertNotEquals(apiNotes1, apiNotes2);
      Assert.assertEquals(createdAt, sdf.format(new Date()));
      
   }
   
   /**
    * Positive test case for addNote method with optional parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testCreateTicketWithOptionalParameters" }, description = "FreshDesk {addNote} integration test with optional parameters.")
   public void testAddNoteWithOptionalParameters() throws IOException, JSONException {
   
      final String apiUrl =
            connectorProperties.getProperty("apiUrl") + "/helpdesk/tickets/"
                  + connectorProperties.getProperty("ticketIdOptional") + ".json";
      
      RestResponse<JSONObject> apiRestResponse1 = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      final JSONArray apiNotes1 = apiRestResponse1.getBody().getJSONObject("helpdesk_ticket").getJSONArray("notes");
      
      esbRequestHeadersMap.put("Action", "urn:addNote");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addNote_optional.json");
      
      RestResponse<JSONObject> apiRestResponse2 = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      final JSONArray apiNotes2 = apiRestResponse2.getBody().getJSONObject("helpdesk_ticket").getJSONArray("notes");
      
      Assert.assertNotEquals(apiNotes1, apiNotes2);
      
      JSONObject note = new JSONObject();
      final String noteId = esbRestResponse.getBody().getJSONObject("note").getString("id");
      
      // The position of the note object in API response changes time to time.
      if (noteId.equals(apiNotes2.getJSONObject(0).getJSONObject("note").getString("id"))) {
         note = apiNotes2.getJSONObject(0).getJSONObject("note");
      } else if (noteId.equals(apiNotes2.getJSONObject(1).getJSONObject("note").getString("id"))) {
         note = apiNotes2.getJSONObject(1).getJSONObject("note");
      } else if (noteId.equals(apiNotes2.getJSONObject(2).getJSONObject("note").getString("id"))) {
         note = apiNotes2.getJSONObject(2).getJSONObject("note");
      }
      
      Assert.assertEquals(connectorProperties.getProperty("noteBody"), note.getString("body"));
      final String createdAt = note.getString("created_at").split("T")[0];
      Assert.assertEquals(createdAt, sdf.format(new Date()));
      Assert.assertEquals(connectorProperties.getProperty("private"), note.getString("private"));
   }
   
   /**
    * Positive test case for getUser method with mandatory parameters.
    */
   @Test(priority = 1, description = "FreshDesk {getUser} integration test with mandatory parameters.")
   public void testGetUserWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getUser");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUser_mandatory.json");
      
      final String apiUrl =
            connectorProperties.getProperty("apiUrl") + "/contacts/" + connectorProperties.getProperty("userId")
                  + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").getString("email"), apiRestResponse.getBody()
            .getJSONObject("user").getString("email"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").getString("name"), apiRestResponse.getBody()
            .getJSONObject("user").getString("name"));
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").getString("created_at"), apiRestResponse
            .getBody().getJSONObject("user").getString("created_at"));
   }
   
   /**
    * Negative test case for getUser method.
    */
   @Test(priority = 1, description = "FreshDesk {getUser} integration test with negative case.")
   public void testGetUserWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getUser");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUser_negative.json");
      
      final String apiUrl = connectorProperties.getProperty("apiUrl") + "/contacts/invalid.json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").toString(), apiRestResponse.getBody()
            .getJSONObject("errors").toString());
   }
   
   /**
    * Positive test case for createTopic method with mandatory parameters.
    */
   @Test(priority = 1, description = "FreshDesk {createTopic} integration test with mandatory parameters.")
   public void testCreateTopicWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createTopic");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTopic_mandatory.json");
      JSONObject topic = esbRestResponse.getBody().getJSONObject("topic");
      String topicId = topic.getString("id");
      connectorProperties.put("topicId", topicId);
      
      final String apiUrl =
            connectorProperties.getProperty("apiUrl") + "/categories/" + connectorProperties.getProperty("categoryId")
                  + "/forums/" + connectorProperties.getProperty("forumId") + "/topics/"
                  + connectorProperties.getProperty("topicId") + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      JSONObject apiTopic = apiRestResponse.getBody().getJSONObject("topic");
      JSONArray posts = apiTopic.getJSONArray("posts");
      
      Assert.assertEquals(topic.getString("title"), connectorProperties.getProperty("subject"));
      Assert.assertEquals(topic.getString("forum_id"), connectorProperties.getProperty("forumId"));
      Assert.assertEquals(posts.getJSONObject(0).getString("body"), connectorProperties.getProperty("noteBody"));
   }
   
   /**
    * Negative test case for createTopic method.
    */
   @Test(priority = 1, description = "FreshDesk {createTopic} integration test with negative case.")
   public void testCreateTopicWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createTopic");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTopic_negative.json");
      
      final String apiUrl =
            connectorProperties.getProperty("apiUrl") + "/categories/" + connectorProperties.getProperty("categoryId")
                  + "/forums/" + connectorProperties.getProperty("forumId") + "/topics.json";
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiUrl, "POST", apiRequestHeadersMap, "api_createTopic_negative.json");
      
      JSONArray esb = new JSONArray(esbRestResponse.getBody().getString("output"));
      String esbTitle = (String) esb.getJSONArray(0).get(0);
      String esbError = (String) esb.getJSONArray(0).get(1);
      JSONArray api = new JSONArray(apiRestResponse.getBody().getString("output"));
      String apiTitle = (String) api.getJSONArray(0).get(0);
      String apiError = (String) api.getJSONArray(0).get(1);
      
      Assert.assertEquals(esbTitle, apiTitle);
      Assert.assertEquals(esbError, apiError);
   }
   
   /**
    * Positive test case for getTopic method with mandatory parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testCreateTopicWithMandatoryParameters" }, description = "FreshDesk {getTopic} integration test with mandatory parameters.")
   public void testGetTopicWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getTopic");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTopic_mandatory.json");
      
      final String apiUrl =
            connectorProperties.getProperty("apiUrl") + "/categories/" + connectorProperties.getProperty("categoryId")
                  + "/forums/" + connectorProperties.getProperty("forumId") + "/topics/"
                  + connectorProperties.getProperty("topicId") + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      
      JSONObject esbTopic = esbRestResponse.getBody().getJSONObject("topic");
      JSONObject apiTopic = apiRestResponse.getBody().getJSONObject("topic");
      
      Assert.assertEquals(esbTopic.getString("id"), apiTopic.getString("id"));
      Assert.assertEquals(esbTopic.getJSONArray("posts").getJSONObject(0).getString("id"),
            apiTopic.getJSONArray("posts").getJSONObject(0).getString("id"));
   }
   
   /**
    * Negative test case for getTopic method.
    */
   @Test(priority = 1, description = "FreshDesk {getTopic} integration test with negative case.")
   public void testGetTopicWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:getTopic");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTopic_negative.json");
      
      final String apiUrl =
            connectorProperties.getProperty("apiUrl") + "/categories/" + connectorProperties.getProperty("categoryId")
                  + "/forums/" + connectorProperties.getProperty("forumId") + "/topics/.json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "POST", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
   }
   
   /**
    * Positive test case for createPost method with mandatory parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testCreateTopicWithMandatoryParameters" }, description = "FreshDesk {createPost} integration test with mandatory parameters.")
   public void testCreatePostWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createPost");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPost_mandatory.json");
      JSONObject posts = esbRestResponse.getBody().getJSONObject("post");
      String postId = posts.getString("id");
      connectorProperties.put("postId", postId);
      
      final String apiUrl =
            connectorProperties.getProperty("apiUrl") + "/categories/" + connectorProperties.getProperty("categoryId")
                  + "/forums/" + connectorProperties.getProperty("forumId") + "/topics/"
                  + connectorProperties.getProperty("topicId") + ".json";
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      JSONObject topic = apiRestResponse.getBody().getJSONObject("topic");
      JSONArray postsInTopic = topic.getJSONArray("posts");
      
      Assert.assertEquals(posts.getString("forum_id"), connectorProperties.getProperty("forumId"));
      Assert.assertEquals(posts.getString("topic_id"), connectorProperties.getProperty("topicId"));
      Assert.assertEquals(postsInTopic.getJSONObject(1).getString("body"), connectorProperties.getProperty("subject"));
   }
   
   /**
    * Negative test case for createPost method.
    */
   @Test(priority = 1, description = "FreshDesk {createPost} integration test with negative case.")
   public void testCreatePostWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createPost");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPost_negative.json");
      
      final String apiUrl =
            connectorProperties.getProperty("apiUrl") + "/posts.json?forum_id="
                  + connectorProperties.getProperty("forumId") + "&category_id=invalid&topic_id="
                  + connectorProperties.getProperty("topicId");
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiUrl, "POST", apiRequestHeadersMap, "api_createPost_negative.json");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 302);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 302);
   }
   
   /**
    * Positive test case for deletePost method with mandatory parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testCreatePostWithMandatoryParameters" }, description = "FreshDesk {deletePost} integration test with mandatory parameters.")
   public void testDeletePostWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:deletePost");
      final String apiUrl =
            connectorProperties.getProperty("apiUrl") + "/categories/" + connectorProperties.getProperty("categoryId")
                  + "/forums/" + connectorProperties.getProperty("forumId") + "/topics/"
                  + connectorProperties.getProperty("topicId") + ".json";
      RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      
      JSONObject topicBefore = apiRestResponseBefore.getBody().getJSONObject("topic");
      JSONArray postsBefore = topicBefore.getJSONArray("posts");
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deletePost_mandatory.json");
      
      RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
      JSONObject topicAfter = apiRestResponseAfter.getBody().getJSONObject("topic");
      JSONArray postsAfter = topicAfter.getJSONArray("posts");
      
      Assert.assertNotEquals(postsBefore.length(), postsAfter.length());
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
   }
   
   /**
    * Negative test case for deletePost method.
    */
   @Test(priority = 1, description = "FreshDesk {deletePost} integration test with negative case.")
   public void testDeletePostWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:deletePost");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deletePost_negative.json");
      
      final String apiUrl =
            connectorProperties.getProperty("apiUrl") + "/posts/invalid.json?forum_id="
                  + connectorProperties.getProperty("forumId") + "&category_id=invalid&topic_id="
                  + connectorProperties.getProperty("topicId");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "DELETE", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 302);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 302);
   }
   
}
