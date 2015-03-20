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
package org.wso2.carbon.connector.integration.test.livechat;

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

public class LivechatConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap;    
    private Map<String, String> apiRequestHeadersMap;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("livechat-connector-1.0.0");
        
        esbRequestHeadersMap = new HashMap<String, String>();
        apiRequestHeadersMap = new HashMap<String, String>(); 
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        // Create base64-encoded auth string using login and apiKey
        final String authString = connectorProperties.getProperty("login") + ":"+connectorProperties.getProperty("apiKey");
        final String base64AuthString = Base64.encode(authString.getBytes());
        
        apiRequestHeadersMap.put("Authorization", "Basic " + base64AuthString);
        apiRequestHeadersMap.put("X-API-Version", "2");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
    }
    
    /**
     * Positive test case for createAgent method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "livechat {createAgent} integration test with mandatory parameters.")
    public void testCreateAgentWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAgent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAgent_mandatory.json");
        
        String agentLogin = esbRestResponse.getBody().getString("login");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/agents/" + agentLogin;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("name"), connectorProperties.get("agentNameMand"));
        Assert.assertEquals(apiRestResponse.getBody().getString("login"), connectorProperties.get("agentLoginMand"));
    }
    
    /**
     * Positive test case for createAgent method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "livechat {createAgent} integration test with optional parameters.")
    public void testCreateAgentWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAgent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAgent_optional.json");
        
        String agentLogin = esbRestResponse.getBody().getString("login");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/agents/" + agentLogin;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("job_title"), connectorProperties.get("jobTitle"));
        Assert.assertEquals(apiRestResponse.getBody().getString("login_status"), connectorProperties.get("loginStatus"));
    }
    
    /**
     * Negative test case for createAgent method .
     */
    @Test(groups = { "wso2.esb" }, description = "livechat {createAgent} integration test with negative case.")
    public void testCreateAgentWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAgent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAgent_negative.json");
        
        JSONArray esbErrorsArray = esbRestResponse.getBody().getJSONArray("errors");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/agents/";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createAgent_negative.json");
        
        JSONArray apiErrorsArray = apiRestResponse.getBody().getJSONArray("errors");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        
        Assert.assertEquals(apiErrorsArray.get(0).toString(),esbErrorsArray.get(0).toString());
           
    }
    
    /**
     * Positive test case for listAgents method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateAgentWithMandatoryParameters","testCreateAgentWithOptionalParameters"}, description = "livechat {listAgents} integration test with mandatory parameters.")
    public void testListAgentsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listAgents");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAgents_mandatory.json");
        
        String esbResponseString=esbRestResponse.getBody().getString("output");
        JSONArray esbResponseArray=new JSONArray(esbResponseString);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/agents";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String apiResponseString=esbRestResponse.getBody().getString("output");
        JSONArray apiResponseArray=new JSONArray(apiResponseString);
        
        Assert.assertEquals(apiRestResponse.getBody().length(),esbRestResponse.getBody().length());
        
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("name"),esbResponseArray.getJSONObject(0).getString("name"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("login"),esbResponseArray.getJSONObject(0).getString("login")); 
    }
    
    /**
     * Negative test case for listAgents method .
     */
    @Test(groups = { "wso2.esb" }, description = "livechat {listAgents} integration test with negative case.")
    public void testListAgentsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listAgents");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAgents_negative.json");
        
        JSONArray esbErrorsArray = esbRestResponse.getBody().getJSONArray("errors");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/agents?status=INVALID";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiErrorsArray = apiRestResponse.getBody().getJSONArray("errors");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        
        Assert.assertEquals(apiErrorsArray.get(0).toString(),esbErrorsArray.get(0).toString());
    }
    
    /**
     * Positive test case for getAgentByLoginId method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateAgentWithMandatoryParameters"}, description = "livechat {getAgentByLoginId} integration test with mandatory parameters.")
    public void testGetAgentByLoginIdWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAgentByLoginId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAgentById_mandatory.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/agents/"+connectorProperties.getProperty("agentLoginMand");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().get("login"),esbRestResponse.getBody().get("login"));
        Assert.assertEquals(apiRestResponse.getBody().get("name"),esbRestResponse.getBody().get("name"));
        Assert.assertEquals(apiRestResponse.getBody().get("login_status"),esbRestResponse.getBody().get("login_status"));
    }
    
    /**
     * Positive test case for createGroup method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateAgentWithMandatoryParameters"}, description = "livechat {createGroup} integration test with mandatory parameters.")
    public void testCreateGroupWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createGroup");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createGroup_mandatory.json");
        
        String groupId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("groupId", groupId);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/groups/" + groupId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("name"), connectorProperties.get("groupName"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("agents").get(0), connectorProperties.get("agentLoginMand"));
    }
    
    /**
     * Positive test case for createGroup method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateAgentWithMandatoryParameters"}, description = "livechat {createGroup} integration test with optional parameters.")
    public void testCreateGroupWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createGroup");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createGroup_optional.json");
        
        String groupId = esbRestResponse.getBody().getString("id");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/groups/" + groupId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("language"), connectorProperties.get("language"));
    }
    
    /**
     * Negative test case for createGroup method .
     */
    @Test(groups = { "wso2.esb" }, description = "livechat {createGroup} integration test with negative case.")
    public void testCreateGroupWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createGroup");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createGroup_negative.json");
        
        JSONArray esbErrorsArray = esbRestResponse.getBody().getJSONArray("errors");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/groups/";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createAgent_negative.json");
        
        JSONArray apiErrorsArray = apiRestResponse.getBody().getJSONArray("errors");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        
        
        Assert.assertEquals(apiErrorsArray.get(0).toString(),esbErrorsArray.get(0).toString());
           
    }
    
    /**
     * Positive test case for listGroups method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateGroupWithMandatoryParameters","testCreateGroupWithOptionalParameters"}, description = "livechat {listAgents} integration test with mandatory parameters.")
    public void testListGroupsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listGroups");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listGroups_mandatory.json");
        
        String esbResponseString=esbRestResponse.getBody().getString("output");
        JSONArray esbResponseArray=new JSONArray(esbResponseString);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/groups";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String apiResponseString=esbRestResponse.getBody().getString("output");
        JSONArray apiResponseArray=new JSONArray(apiResponseString);
        
        Assert.assertEquals(apiRestResponse.getBody().length(),esbRestResponse.getBody().length());
        
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("name"),esbResponseArray.getJSONObject(0).getString("name"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("status"),esbResponseArray.getJSONObject(0).getString("status"));   
    }
    
    /**
     * Positive test case for getGroupById method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateGroupWithMandatoryParameters"}, description = "livechat {getGroupById} integration test with mandatory parameters.")
    public void testGetGroupByIdWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getGroupById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getGroupById_mandatory.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/groups/"+connectorProperties.getProperty("groupId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(apiRestResponse.getBody().getString("id"), connectorProperties.getProperty("groupId"));
        Assert.assertEquals(apiRestResponse.getBody().getString("name"), esbRestResponse.getBody().getString("name"));
        Assert.assertEquals(apiRestResponse.getBody().getString("language"), esbRestResponse.getBody().getString("language"));
    }
    
    /**
     * Positive test case for listChats method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "livechat {listChats} integration test with mandatory parameters.")
    public void testListChatsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listChats");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listChats_mandatory.json");

        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("chats");
        
        String chatId = esbResponseArray.getJSONObject(0).getString("id");
        connectorProperties.setProperty("chatId", chatId);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/chats";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("chats");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("total"), esbRestResponse.getBody().getString("total"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("id"), esbResponseArray.getJSONObject(0).getString("id"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("started"), esbResponseArray.getJSONObject(0).getString("started"));
    }
	
	/**
     * Positive test case for listChats method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "livechat {listChats} integration test with optional parameters.")
    public void testListChatsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listChats");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listChats_optional.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("chats");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/chats?date_to="+connectorProperties.getProperty("endDate")+"&date_from="+connectorProperties.getProperty("startDate");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("chats");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("total"), esbRestResponse.getBody().getString("total"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("id"), esbResponseArray.getJSONObject(0).getString("id"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("started"), esbResponseArray.getJSONObject(0).getString("started"));
    }
    
    /**
     * Negative test case for listChats method.
     */
    @Test(groups = { "wso2.esb" }, description = "livechat {listChats} integration test with negative case.")
    public void testListChatsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listChats");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listChats_negative.json");
        
        JSONArray esbErrorArray = esbRestResponse.getBody().getJSONArray("errors");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/chats?&date_from=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiErrorArray = esbRestResponse.getBody().getJSONArray("errors");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        
        
        Assert.assertEquals(apiErrorArray.get(0).toString(),esbErrorArray.get(0).toString());
    }

    /**
     * Positive test case for sendChatTranscriptByEmail method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testListChatsWithMandatoryParameters" },groups = { "wso2.esb" }, description = "livechat {sendChatTranscriptByEmail} integration test with mandatory parameters.")
    public void testSendChatTranscriptByEmailWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:sendChatTranscriptByEmail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendChatTranscriptByEmail_mandatory.json");
   
        String message = "E-mail has been sent to "+connectorProperties.getProperty("toEmail")+".";
        connectorProperties.setProperty("message", message);
  
        Assert.assertEquals(connectorProperties.get("message"),esbRestResponse.getBody().getString("result"));
    }
    
    /**
     * Negative test case for sendChatTranscriptByEmail method.
     */
    @Test(dependsOnMethods = { "testListChatsWithMandatoryParameters" },groups = { "wso2.esb" }, description = "livechat {sendChatTranscriptByEmail} integration test with negative case.")
    public void testSendChatTranscriptByEmailWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:sendChatTranscriptByEmail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendChatTranscriptByEmail_negative.json");
        
        JSONArray esbErrorArray = esbRestResponse.getBody().getJSONArray("errors");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/chats/" +connectorProperties.getProperty("chatId")+"/send_transcript";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_sendChatTranscriptByEmail_negative.json");
        
        JSONArray apiErrorArray = apiRestResponse.getBody().getJSONArray("errors");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        
        
       Assert.assertEquals(apiErrorArray.get(0).toString(),esbErrorArray.get(0).toString());
 
    }    

    /**
     * Positive test case for getChatById method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testListChatsWithMandatoryParameters" },groups = { "wso2.esb" }, description = "livechat {getChatById} integration test with mandatory parameters.")
    public void testGetChatByIdWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getChatById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getChatById_mandatory.json");
      
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/chats/"+connectorProperties.getProperty("chatId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("id"), connectorProperties.getProperty("chatId"));
        Assert.assertEquals(apiRestResponse.getBody().getString("visitor_name"), esbRestResponse.getBody().getString("visitor_name"));
        Assert.assertEquals(apiRestResponse.getBody().getString("started"), esbRestResponse.getBody().getString("started"));
    }

    /**
     * Positive test case for createTicket method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "livechat {createTicket} integration test with mandatory parameters.")
    public void testCreateTicketWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTicket");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTicket_mandatory.json");
        
        String ticketId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("ticketId", ticketId);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/tickets/" + ticketId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiEventsArray = apiRestResponse.getBody().getJSONArray("events");
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("requester").getString("mail"), connectorProperties.get("requesterEmail"));
        Assert.assertEquals(apiEventsArray.getJSONObject(0).getString("message"), connectorProperties.get("message"));
    }
    
    /**
     * Positive test case for createTicket method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "livechat {createTicket} integration test with optional parameters.")
    public void testCreateTicketWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTicket");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTicket_optional.json");
        
        String ticketId = esbRestResponse.getBody().getString("id");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/tickets/" + ticketId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiEventsArray = apiRestResponse.getBody().getJSONArray("events");
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("requester").getString("mail"), connectorProperties.get("requesterEmail"));
        Assert.assertEquals(apiEventsArray.getJSONObject(0).getString("message"), connectorProperties.get("message"));
        Assert.assertEquals(apiRestResponse.getBody().getString("subject"), connectorProperties.get("subject"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("assignee").getString("id"), connectorProperties.get("agentLoginMand"));  
    }
    
    /**
     * Negative test case for createTicket method .
     */
    @Test(groups = { "wso2.esb" }, description = "livechat {createTicket} integration test with negative case.")
    public void testCreateTicketWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTicket");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTicket_negative.json");
        
        JSONArray esbErrorsArray = esbRestResponse.getBody().getJSONArray("error");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/tickets";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createTicket_negative.json");
        
        JSONArray apiErrorsArray = apiRestResponse.getBody().getJSONArray("error");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorsArray.getString(0), esbErrorsArray.getString(0)); 
    }
    
    /**
     * Positive test case for getTicketById method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" },dependsOnMethods ={"testCreateTicketWithMandatoryParameters"}, description = "livechat {getTicketById} integration test with mandatory parameters.")
    public void testGetTicketByIdWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getTicketById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTicketById_mandatory.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/tickets/" + connectorProperties.getProperty("ticketId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbEventsArray = esbRestResponse.getBody().getJSONArray("events");
        JSONArray apiEventsArray = apiRestResponse.getBody().getJSONArray("events");
        
        Assert.assertEquals(apiEventsArray.getJSONObject(0).getString("message"), esbEventsArray.getJSONObject(0).getString("message"));
        Assert.assertEquals(apiEventsArray.getJSONObject(0).getString("date"), esbEventsArray.getJSONObject(0).getString("date"));
        Assert.assertEquals(apiRestResponse.getBody().getString("status"), esbRestResponse.getBody().getString("status"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("requester").getString("mail"), esbRestResponse.getBody().getJSONObject("requester").getString("mail"));
    }
    
    /**
     * Positive test case for listTickets method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" },dependsOnMethods ={"testCreateTicketWithMandatoryParameters","testCreateTicketWithOptionalParameters"}, description = "livechat {listTickets} integration test with mandatory parameters.")
    public void testListTicketsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTickets");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTickets_mandatory.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/tickets";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbTicketsArray = esbRestResponse.getBody().getJSONArray("tickets");
        JSONArray apiTicketsArray = apiRestResponse.getBody().getJSONArray("tickets");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("pages"), esbRestResponse.getBody().getString("pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total"), esbRestResponse.getBody().getString("total"));
        Assert.assertEquals(apiTicketsArray.getJSONObject(0).getString("id"), esbTicketsArray.getJSONObject(0).getString("id"));
        Assert.assertEquals(apiTicketsArray.getJSONObject(0).getJSONObject("requester").getString("mail"), esbTicketsArray.getJSONObject(0).getJSONObject("requester").getString("mail"));
        Assert.assertEquals(apiTicketsArray.getJSONObject(0).getJSONArray("events").getJSONObject(0).getString("message"), esbTicketsArray.getJSONObject(0).getJSONArray("events").getJSONObject(0).getString("message"));  
    }
    
    /**
     * Positive test case for listTickets method with optional parameters.
     * @throws InterruptedException 
     */
    @Test(groups = { "wso2.esb" },dependsOnMethods ={"testCreateTicketWithMandatoryParameters","testCreateTicketWithOptionalParameters"}, description = "livechat {listTickets} integration test with optional parameters.")
    public void testListTicketsWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:listTickets");
        
        Thread.sleep(15000);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTickets_optional.json");
       
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/tickets?assignee="+connectorProperties.getProperty("agentLoginMand")+"&order=asc";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbTicketsArray = esbRestResponse.getBody().getJSONArray("tickets");
        JSONArray apiTicketsArray = apiRestResponse.getBody().getJSONArray("tickets");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("pages"), esbRestResponse.getBody().getString("pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total"), esbRestResponse.getBody().getString("total"));
        Assert.assertEquals(apiTicketsArray.getJSONObject(0).getString("id"), esbTicketsArray.getJSONObject(0).getString("id"));
        Assert.assertEquals(apiTicketsArray.getJSONObject(0).getJSONObject("requester").getString("mail"), esbTicketsArray.getJSONObject(0).getJSONObject("requester").getString("mail"));
        Assert.assertEquals(apiTicketsArray.getJSONObject(0).getJSONArray("events").getJSONObject(0).getString("message"), esbTicketsArray.getJSONObject(0).getJSONArray("events").getJSONObject(0).getString("message"));  
    }
    
    /**
     * Negative test case for listTickets method .
     */
    @Test(groups = { "wso2.esb" }, description = "livechat {listTickets} integration test with negative case.")
    public void testListTicketsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTickets");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTickets_negative.json");
        
        JSONArray esbErrorsArray = esbRestResponse.getBody().getJSONArray("errors");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/tickets?order=INVALID";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiErrorsArray = apiRestResponse.getBody().getJSONArray("errors");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiErrorsArray.getString(0), esbErrorsArray.getString(0));
    }

}
