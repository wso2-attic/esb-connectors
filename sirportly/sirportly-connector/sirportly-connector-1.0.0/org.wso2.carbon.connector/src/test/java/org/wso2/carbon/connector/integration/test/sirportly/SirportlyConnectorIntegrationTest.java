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

package org.wso2.carbon.connector.integration.test.sirportly;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.util.URLEncoder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class SirportlyConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap;    
    private Map<String, String> apiRequestHeadersMap;   
    private String apiUrl;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("sirportly-connector-1.0.0");
        
        esbRequestHeadersMap = new HashMap<String, String>();
        apiRequestHeadersMap = new HashMap<String, String>();
        
        apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2";
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        esbRequestHeadersMap.put("Content-Type", "application/json");
        apiRequestHeadersMap.put("X-Auth-Token", connectorProperties.getProperty("apiToken"));
        apiRequestHeadersMap.put("X-Auth-Secret", connectorProperties.getProperty("apiSecret"));
    }
    
    //There are no mandatory parameters in createContact method.
    
    /**
     * Positive test case for createContact method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {createContact} integration test with optional parameters.")
    public void testCreateContactWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createContact");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        final String contactId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("contactId", contactId);
        
        final String apiEndPoint = apiUrl + "/contacts/info?contact=" + contactId;
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("name"), connectorProperties.getProperty("name"));
        Assert.assertEquals(apiRestResponse.getBody().getString("reference"),
                connectorProperties.getProperty("contactReference"));
        Assert.assertEquals(apiRestResponse.getBody().getString("company"), connectorProperties.getProperty("name"));
        Assert.assertEquals(
                apiRestResponse.getBody().getJSONObject("overrides").getJSONObject("priority").getString("name"),
                connectorProperties.getProperty("priority"));
    }
    
    /**
     * Negative test case for createContact method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, description = "sirportly {createContact} integration test with negative case.")
    public void testCreateContactWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createContact");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        
        final String apiEndPoint = apiUrl + "/contacts/create";
        
        String name = connectorProperties.getProperty("name");
        URLEncoder encoder = new URLEncoder();
        String encodedName = encoder.encode(name);
        connectorProperties.setProperty("encodedName", encodedName);
        
        final RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createContact_negative.txt");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);       
        Assert.assertEquals(esbRestResponse.getBody().getString("reference"),
                apiRestResponse.getBody().getString("reference"));
    }
    
    /**
     * Positive test case for getContact method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, description = "sirportly {getContact} integration test with mandatory parameters.")
    public void testGetContactWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getContact");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContact_mandatory.json");
        
        final String apiEndPoint = apiUrl + "/contacts/info?contact=" + connectorProperties.getProperty("contactId");
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("abbreviated_name"), apiRestResponse.getBody()
                .getString("abbreviated_name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"),
                apiRestResponse.getBody().getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("pin"), apiRestResponse.getBody().getString("pin"));
    }
    
    //There are no optional parameters in getContact method.
    
    /**
     * Negative test case for getContact method.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {getContact} integration test with negative case.")
    public void testGetContactWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getContact");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContact_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        
        final String apiEndPoint = apiUrl + "/contacts/info?contact=Invalid";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);        
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listContacts method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {listContacts} integration test with mandatory parameters.")
    public void testListContactsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_mandatory.json");
        
        final String apiEndPoint = apiUrl + "/contacts/all";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("records");
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("records");
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(1).getString("id"), apiResponseArray.getJSONObject(1)
                .getString("id"));
    }
    
    /**
     * Positive test case for listContacts method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListContactsWithMandatoryParameters" }, description = "sirportly {listContacts} integration test with optional parameters.")
    public void testListContactsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_optional.json");
        
        final String apiEndPoint = apiUrl + "/contacts/all?page=1";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("records");
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("records");
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("pagination").getString("page"), apiRestResponse
                .getBody().getJSONObject("pagination").getString("page"));
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(1).getString("id"), apiResponseArray.getJSONObject(1)
                .getString("id"));
    }
    
    //There is no valid negative testcase to listContact method.
    
    /**
     * Positive test case for searchContacts method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {searchContacts} integration test with mandatory parameters.")
    public void testSearchContactsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchContacts");
        
        String name = connectorProperties.getProperty("name");
        URLEncoder encoder = new URLEncoder();
        String encodedName = encoder.encode(name);
        connectorProperties.setProperty("encodedName", encodedName);
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchContacts_mandatory.json");
        
        final String apiEndPoint = apiUrl + "/contacts/search?query=" + connectorProperties.getProperty("encodedName");
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String esbResponseArrayString = esbRestResponse.getBody().getString("output");
        JSONArray esbResponseArray = new JSONArray(esbResponseArrayString);
        String apiResponseArrayString = apiRestResponse.getBody().getString("output");
        JSONArray apiResponseArray = new JSONArray(apiResponseArrayString);
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getJSONObject("contact").getString("id"),
                apiResponseArray.getJSONObject(0).getJSONObject("contact").getString("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getJSONObject("contact").getString("name"),
                apiResponseArray.getJSONObject(0).getJSONObject("contact").getString("name"));
    }
    
    /**
     * Positive test case for searchContacts method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {searchContacts} integration test with optional parameters.")
    public void testSearchContactsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchContacts");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchContacts_optional.json");
        
        final String apiEndPoint =
                apiUrl + "/contacts/search?query=" + connectorProperties.getProperty("encodedName") + "&types="
                        + connectorProperties.getProperty("contactType");
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String esbResponseArrayString = esbRestResponse.getBody().getString("output");
        JSONArray esbResponseArray = new JSONArray(esbResponseArrayString);
        String apiResponseArrayString = apiRestResponse.getBody().getString("output");
        JSONArray apiResponseArray = new JSONArray(apiResponseArrayString);
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getJSONObject("contact").getString("id"),
                apiResponseArray.getJSONObject(0).getJSONObject("contact").getString("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getJSONObject("contact").getString("name"),
                apiResponseArray.getJSONObject(0).getJSONObject("contact").getString("name"));
    }
    
    /**
     * Negative test case for searchContacts method.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {searchContacts} integration test with negative case.")
    public void testSearchContactsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchContacts");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchContacts_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        
        final String apiEndPoint = apiUrl + "/contacts/search?query=a";
        
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
    }
    
    /**
     * Positive test case for addContactMethod method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, description = "sirportly {addContactMethod} integration test with mandatory parameters.")
    public void testAddContactMethodWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:addContactMethod");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addContactMethod_mandatory.json");
        
        final String apiEndPoint = apiUrl + "/contacts/info?contact=" + connectorProperties.getProperty("contactId");
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("contact_methods");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("data"),
                apiResponseArray.getJSONObject(0).getString("data"));
        Assert.assertEquals(esbRestResponse.getBody().getString("method_type"), apiResponseArray.getJSONObject(0)
                .getString("method_type"));
    }
    
  //There are no optional parameters in addContactMethod method.
    
    /**
     * Negative test case for addContactMethod method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, description = "sirportly {addContactMethod} integration test with negative case.")
    public void testAddContactMethodWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:addContactMethod");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addContactMethod_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        
        final String apiEndPoint = apiUrl + "/contacts/add_contact_method";
        
        final RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_addContactMethod_negative.txt");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("data"), apiRestResponse.getBody().getString("data"));
        Assert.assertEquals(esbRestResponse.getBody().getString("method_type"),
                apiRestResponse.getBody().getString("method_type"));
    }
    
    /**
     * Positive test case for getUser method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {getUser} integration test with mandatory parameters.")
    public void testGetUserWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getUser");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUser_mandatory.json");
        
        final String apiEndPoint = apiUrl + "/users/info?user=" + connectorProperties.getProperty("userId");
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("first_name"),
                apiRestResponse.getBody().getString("first_name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("job_title"),
                apiRestResponse.getBody().getString("job_title"));
        Assert.assertEquals(esbRestResponse.getBody().getString("email_address"),
                apiRestResponse.getBody().getString("email_address"));
    }
    
  //There are no optional parameters in getUser method.
    
    /**
     * Negative test case for getUser method.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {getUser} integration test with negative case.")
    public void testGetUserWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getUser");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUser_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        
        final String apiEndPoint = apiUrl + "/users/info?user=Invalid";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);       
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for createTicket method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {createTicket} integration test with mandatory parameters.")
    public void testCreateTicketWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTicket");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTicket_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        final String ticketReference = esbRestResponse.getBody().getString("reference");
        
        final String apiEndPoint = apiUrl + "/tickets/ticket?ticket=" + ticketReference;
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getString("subject"),
                connectorProperties.getProperty("ticketSubject"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("status").getString("name"),
                connectorProperties.getProperty("ticketStatus"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("priority").getString("name"),
                connectorProperties.getProperty("priority"));
    }
    
    /**
     * Positive test case for createTicket method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, description = "sirportly {createTicket} integration test with optional parameters.")
    public void testCreateTicketWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTicket");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTicket_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        final String ticketReference = esbRestResponse.getBody().getString("reference");
        connectorProperties.setProperty("ticketReference", ticketReference);
        
        final String apiEndPoint = apiUrl + "/tickets/ticket?ticket=" + ticketReference;
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("user").getString("id"),
                connectorProperties.getProperty("userId"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("team").getString("id"),
                connectorProperties.getProperty("team"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("department").getJSONObject("brand")
                .getString("id"), connectorProperties.getProperty("brand"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("contact").getString("id"),
                connectorProperties.getProperty("contactId"));
        Assert.assertEquals(apiRestResponse.getBody().getString("subject"),
                connectorProperties.getProperty("ticketSubject"));
    }
    
    /**
     * Negative test case for createTicket method.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {createTicket} integration test with negative case.")
    public void testCreateTicketWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTicket");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTicket_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        
        final String apiEndPoint = apiUrl + "/tickets/submit";
        
        final RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createTicket_negative.json");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").getString("department_id"),
                apiRestResponse.getBody().getJSONObject("errors").getString("department_id"));
    }
    
    /**
     * Positive test case for getTicket method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTicketWithOptionalParameters" }, description = "sirportly {getTicket} integration test with mandatory parameters.")
    public void testGetTicketWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getTicket");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTicket_mandatory.json");
        
        final String apiEndPoint =
                apiUrl + "/tickets/ticket?ticket=" + connectorProperties.getProperty("ticketReference");
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("subject"),
                apiRestResponse.getBody().getString("subject"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("name"), apiRestResponse
                .getBody().getJSONObject("status").getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("department").getString("name"), apiRestResponse
                .getBody().getJSONObject("department").getString("name"));
    }
    
    /**
     * Positive test case for getTicket method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {getTicket} integration test with optional parameters.")
    public void testGetTicketWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getTicket");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTicket_optional.json");
        
        final String apiEndPoint =
                apiUrl + "/tickets/ticket?ticket=" + connectorProperties.getProperty("ticketReference") + "&timers=1";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("subject"),
                apiRestResponse.getBody().getString("subject"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("status").getString("name"), apiRestResponse
                .getBody().getJSONObject("status").getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("department").getString("name"), apiRestResponse
                .getBody().getJSONObject("department").getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("updated_at"),
                apiRestResponse.getBody().getString("updated_at"));
    }
    
    /**
     * Negative test case for getTicket method.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {getTicket} integration test with negative case.")
    public void testGetTicketWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getTicket");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTicket_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        
        final String apiEndPoint = apiUrl + "/tickets/ticket?ticket=Invalid";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);       
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listTickets method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {listTickets} integration test with mandatory parameters.")
    public void testListTicketsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTickets");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTickets_mandatory.json");
        
        final String apiEndPoint = apiUrl + "/tickets/all";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("records").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("records").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("records").getJSONObject(0).getString("reference"),
                apiRestResponse.getBody().getJSONArray("records").getJSONObject(0).getString("reference"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("records").getJSONObject(0).getString("subject"),
                apiRestResponse.getBody().getJSONArray("records").getJSONObject(0).getString("subject"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("records").getJSONObject(0)
                .getJSONObject("priority").getString("id"), apiRestResponse.getBody().getJSONArray("records")
                .getJSONObject(0).getJSONObject("priority").getString("id"));
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONArray("records").getJSONObject(0).getJSONObject("department")
                        .getString("id"), apiRestResponse.getBody().getJSONArray("records").getJSONObject(0)
                        .getJSONObject("department").getString("id"));        
    }
    
    /**
     * Positive test case for listTickets method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {listTickets} integration test with optional parameters.")
    public void testListTicketsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTickets");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTickets_optional.json");
        
        final String apiEndPoint = apiUrl + "/tickets/all?order=desc&page=1&sort_by=created_at";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("records").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("records").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("records").getJSONObject(0).getString("reference"),
                apiRestResponse.getBody().getJSONArray("records").getJSONObject(0).getString("reference"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("records").getJSONObject(0).getString("subject"),
                apiRestResponse.getBody().getJSONArray("records").getJSONObject(0).getString("subject"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("records").getJSONObject(0)
                .getJSONObject("priority").getString("id"), apiRestResponse.getBody().getJSONArray("records")
                .getJSONObject(0).getJSONObject("priority").getString("id"));
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONArray("records").getJSONObject(0).getJSONObject("department")
                        .getString("id"), apiRestResponse.getBody().getJSONArray("records").getJSONObject(0)
                        .getJSONObject("department").getString("id"));       
    }
    
    /**
     * Negative test case for listTickets method.
     */
    @Test(groups = { "wso2.esb" }, description = "pagerduty {listTickets} integration test with negative case.")
    public void testListTicketsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTickets");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTickets_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        
        final String apiEndPoint = apiUrl + "/tickets/all?order=desc&page=1&sort_by=invalidFilter";
        
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listTicketsByFilter method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {listTicketsByFilter} integration test with mandatory parameters.")
    public void testListTicketsByFilterWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTicketsByFilter");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTicketsByFilter_mandatory.json");
        
        final String apiEndPoint = apiUrl + "/tickets/filter?filter=" + connectorProperties.getProperty("filterId");
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("records");
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("records");
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("filter").getString("name"), apiRestResponse
                .getBody().getJSONObject("filter").getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("filter").getString("description"), apiRestResponse
                .getBody().getJSONObject("filter").getString("description"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("reference"), apiResponseArray.getJSONObject(0)
                .getString("reference"));
    }
    
    /**
     * Positive test case for listTicketsByFilter method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {listTicketsByFilter} integration test with optional parameters.")
    public void testListTicketsByFilterWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTicketsByFilter");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTicketsByFilter_optional.json");
        
        final String apiEndPoint =
                apiUrl + "/tickets/filter?filter=" + connectorProperties.getProperty("filterId")
                        + "&conditions[status]=" + connectorProperties.getProperty("ticketStatusId")
                        + "&conditions[priority]=" + connectorProperties.getProperty("ticketPriorityId");
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray =
                esbRestResponse.getBody().getJSONObject("filter").getJSONObject("conditions").getJSONArray("all");
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("filter").getString("name"), apiRestResponse
                .getBody().getJSONObject("filter").getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("filter").getString("description"), apiRestResponse
                .getBody().getJSONObject("filter").getString("description"));
        Assert.assertEquals(esbResponseArray.getJSONObject(1).getString("data"),
                connectorProperties.getProperty("ticketStatusId"));
        Assert.assertEquals(esbResponseArray.getJSONObject(2).getString("data"),
                connectorProperties.getProperty("ticketPriorityId"));
    }
    
    /**
     * Negative test case for listTicketsByFilter method.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {listTicketsByFilter} integration test with negative case.")
    public void testListTicketsByFilterWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTicketsByFilter");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTicketsByFilter_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        
        final String apiEndPoint = apiUrl + "/tickets/filter?filter=Invalid";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);        
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
  //There are no mandatory parameters in updateTicket method.
    
    /**
     * Positive test case for updateTicket method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {updateTicket} integration test with optional parameters.", dependsOnMethods = { "testCreateTicketWithOptionalParameters" })
    public void testUpdateTicketWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateTicket");
        
        final String ticketReference = connectorProperties.getProperty("ticketReference");
        final String apiEndPoint = apiUrl + "/tickets/ticket?ticket=" + ticketReference;
        
        final RestResponse<JSONObject> apiRestResponseBefore =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTicket_optional.json");
        
        final RestResponse<JSONObject> apiRestResponseAfter =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getJSONObject("user").getString("id"),
                apiRestResponseAfter.getBody().getJSONObject("user").getString("id"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getJSONObject("status").getString("id"),
                apiRestResponseAfter.getBody().getJSONObject("status").getString("id"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getJSONObject("department").getString("id"),
                apiRestResponseAfter.getBody().getJSONObject("department").getString("id"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("subject"), apiRestResponseAfter.getBody()
                .getString("subject"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getJSONObject("priority").getString("name"),
                apiRestResponseAfter.getBody().getJSONObject("priority").getString("name"));       
    }
    
    /**
     * Negative test case for updateTicket method.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {updateTicket} integration test with negative case.", dependsOnMethods = { "testCreateTicketWithOptionalParameters" })
    public void testUpdateTicketWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateTicket");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTicket_negative.json");
        
        final String apiEndPoint = apiUrl + "/tickets/update";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));        
    }
    
    /**
     * Positive test case for addContentToTicket method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {addContentToTicket} integration test with mandatory parameters.", dependsOnMethods = { "testCreateTicketWithOptionalParameters" })
    public void testAddContentToTicketWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:addContentToTicket");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addContentToTicket_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),connectorProperties.getProperty("contentMessage"));       
    }
    
    /**
     * Positive test case for addContentToTicket method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {addContentToTicket} integration test with optional parameters.", dependsOnMethods = { "testCreateTicketWithOptionalParameters" })
    public void testAddContentToTicketWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:addContentToTicket");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addContentToTicket_optional.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                connectorProperties.getProperty("contentMessage"));
        Assert.assertEquals(esbRestResponse.getBody().getString("subject"), "Test");
        Assert.assertTrue(esbRestResponse.getBody().getBoolean("authenticated"));
    }
    
    /**
     * Negative test case for addContentToTicket method.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {addContentToTicket} integration test with negative case.", dependsOnMethods = { "testCreateTicketWithOptionalParameters" })
    public void testAddContentToTicketWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:addContentToTicket");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addContentToTicket_negative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").getJSONArray("base").get(0),"You must enter a message");       
    }
    
    /**
     * Positive test case for searchTickets method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {searchTickets} integration test with mandatory parameters.", dependsOnMethods = { "testCreateTicketWithOptionalParameters" })
    public void testSearchTicketsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchTickets");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchTickets_mandatory.json");
        
        final String apiEndPoint = apiUrl + "/tickets/search?query=test";
        
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("query"), apiRestResponse.getBody().getString("query"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("records").length(), apiRestResponse.getBody()
                .getJSONArray("records").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("pagination").getInt("page"), apiRestResponse
                .getBody().getJSONObject("pagination").getInt("page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("pagination").getInt("pages"), apiRestResponse
                .getBody().getJSONObject("pagination").getInt("pages"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("pagination").getInt("total_records"),
                apiRestResponse.getBody().getJSONObject("pagination").getInt("total_records"));        
    }
    
    /**
     * Positive test case for searchTickets method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {searchTickets} integration test with optional parameters.", dependsOnMethods = { "testCreateTicketWithOptionalParameters" })
    public void testSearchTicketsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchTickets");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchTickets_optional.json");
        
        final String apiEndPoint = apiUrl + "/tickets/search?query=test&page=1";
        
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("query"), apiRestResponse.getBody().getString("query"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("records").length(), apiRestResponse.getBody()
                .getJSONArray("records").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("pagination").getInt("page"), apiRestResponse
                .getBody().getJSONObject("pagination").getInt("page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("pagination").getInt("pages"), apiRestResponse
                .getBody().getJSONObject("pagination").getInt("pages"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("pagination").getInt("total_records"),
                apiRestResponse.getBody().getJSONObject("pagination").getInt("total_records"));       
    }
    
    /**
     * Negative test case for searchTickets method.
     */
    @Test(groups = { "wso2.esb" }, description = "sirportly {searchTickets} integration test with negative case.", dependsOnMethods = { "testCreateTicketWithOptionalParameters" })
    public void testSearchTicketsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchTickets");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchTickets_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        
        final String apiEndPoint = apiUrl + "/tickets/search?query=";
        
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));        
    }
    
}
