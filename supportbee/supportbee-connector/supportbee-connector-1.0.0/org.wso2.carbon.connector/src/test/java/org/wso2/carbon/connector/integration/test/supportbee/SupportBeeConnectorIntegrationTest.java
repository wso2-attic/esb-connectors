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
package org.wso2.carbon.connector.integration.test.supportbee;

import java.io.IOException;
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

public class SupportBeeConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap;
    
    private Map<String, String> apiRequestHeadersMap;
    
    private String apiRequestUrl;
    
    private String authString;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        esbRequestHeadersMap = new HashMap<String, String>();
        apiRequestHeadersMap = new HashMap<String, String>();
        
        init("supportbee-connector-1.0.0");
        
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Accept", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        apiRequestUrl = connectorProperties.getProperty("apiUrl");
        
        authString = "?auth_token=" + connectorProperties.getProperty("authToken");
    }
    
    /**
     * Positive test case for createTicket method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {createTicket} integration test with mandatory parameters.")
    public void testCreateTicketWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createTicket");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTicket_mandatory.json");
        final String ticketIdMandatory = esbRestResponse.getBody().getJSONObject("ticket").getString("id");
        connectorProperties.setProperty("ticketIdMandatory", ticketIdMandatory);
        
        final String apiEndPoint = apiRequestUrl + "/tickets/" + ticketIdMandatory + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("requesterEmail"), apiRestResponse.getBody().getJSONObject(
                        "ticket").getJSONObject("requester").getString("email"));
        Assert.assertEquals(connectorProperties.getProperty("text"), apiRestResponse.getBody().getJSONObject("ticket")
                        .getJSONObject("content").getString("text"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getString("created_at"), apiRestResponse
                        .getBody().getJSONObject("ticket").getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getString("summary"), apiRestResponse
                        .getBody().getJSONObject("ticket").getString("summary"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getJSONObject("source").getString("web"),
                        apiRestResponse.getBody().getJSONObject("ticket").getJSONObject("source").getString("web"));
    }
    
    /**
     * Positive test case for createTicket method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {createTicket} integration test with optional parameters.", dependsOnMethods = { "testCreateTicketWithMandatoryParameters" })
    public void testCreateTicketWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createTicket");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTicket_optional.json");
        final String ticketIdOptional = esbRestResponse.getBody().getJSONObject("ticket").getString("id");
        connectorProperties.setProperty("ticketIdOptional", ticketIdOptional);
        
        final String apiEndPoint = apiRequestUrl + "/tickets/" + ticketIdOptional + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("requesterEmail"), apiRestResponse.getBody().getJSONObject(
                        "ticket").getJSONObject("requester").getString("email"));
        Assert.assertEquals(connectorProperties.getProperty("text"), apiRestResponse.getBody().getJSONObject("ticket")
                        .getJSONObject("content").getString("text"));
        Assert.assertEquals(connectorProperties.getProperty("copiedEmails"), apiRestResponse.getBody().getJSONObject(
                        "ticket").getJSONArray("cc").getJSONObject(0).getString("email"));
        Assert.assertEquals(connectorProperties.getProperty("subject"), apiRestResponse.getBody().getJSONObject(
                        "ticket").getString("subject"));
        Assert.assertEquals(connectorProperties.getProperty("requesterName"), apiRestResponse.getBody().getJSONObject(
                        "ticket").getJSONObject("requester").getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("html"), apiRestResponse.getBody().getJSONObject("ticket")
                        .getJSONObject("content").getString("html"));
    }
    
    /**
     * Negative test case for createTicket method.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {createTicket} integration test with negative case.")
    public void testCreateTicketWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createTicket");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTicket_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/tickets" + authString;
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createTicket_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for getTicket method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {getTicket} integration test with mandatory parameters.", dependsOnMethods = { "testCreateTicketWithOptionalParameters" })
    public void testGetTicketWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getTicket");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTicket_mandatory.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/tickets/" + connectorProperties.getProperty("ticketIdOptional") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getJSONObject("requester").getString(
                        "email"), apiRestResponse.getBody().getJSONObject("ticket").getJSONObject("requester")
                        .getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getJSONObject("content")
                        .getString("text"), apiRestResponse.getBody().getJSONObject("ticket").getJSONObject("content")
                        .getString("text"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getJSONArray("cc").getJSONObject(0)
                        .getString("email"), apiRestResponse.getBody().getJSONObject("ticket").getJSONArray("cc")
                        .getJSONObject(0).getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getString("subject"), apiRestResponse
                        .getBody().getJSONObject("ticket").getString("subject"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getJSONObject("requester").getString(
                        "name"), apiRestResponse.getBody().getJSONObject("ticket").getJSONObject("requester")
                        .getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ticket").getJSONObject("content")
                        .getString("html"), apiRestResponse.getBody().getJSONObject("ticket").getJSONObject("content")
                        .getString("html"));
    }
    
    /**
     * Negative test case for getTicket method.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {getTicket} integration test with negative case.")
    public void testGetTicketWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getTicket");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTicket_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/tickets/123456" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listTickets method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {listTickets} integration test with mandatory parameters.", dependsOnMethods = { "testGetTicketWithMandatoryParameters" })
    public void testListTicketsWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listTickets");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTickets_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/tickets" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tickets").length(), apiRestResponse.getBody()
                        .getJSONArray("tickets").length());
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("tickets").getJSONObject(0).getJSONObject(
                        "requester").getString("email"), apiRestResponse.getBody().getJSONArray("tickets")
                        .getJSONObject(0).getJSONObject("requester").getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tickets").getJSONObject(0).getJSONObject("content")
                        .getString("text"), apiRestResponse.getBody().getJSONArray("tickets").getJSONObject(0)
                        .getJSONObject("content").getString("text"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tickets").getJSONObject(0).getString("created_at"),
                        apiRestResponse.getBody().getJSONArray("tickets").getJSONObject(0).getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tickets").getJSONObject(0).getString("summary"),
                        apiRestResponse.getBody().getJSONArray("tickets").getJSONObject(0).getString("summary"));
    }
    
    /**
     * Positive test case for listTickets method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {listTickets} integration test with optional parameters.", dependsOnMethods = { "testListTicketsWithMandatoryParameters" })
    public void testListTicketsWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listTickets");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTickets_optional.json");
        
        final String apiEndPoint = apiRequestUrl + "/tickets" + authString + "&page=1&per_page=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tickets").length(), 1);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("tickets").length(), 1);
        
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("tickets").getJSONObject(0).getJSONObject(
                        "requester").getString("email"), apiRestResponse.getBody().getJSONArray("tickets")
                        .getJSONObject(0).getJSONObject("requester").getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tickets").getJSONObject(0).getJSONObject("content")
                        .getString("text"), apiRestResponse.getBody().getJSONArray("tickets").getJSONObject(0)
                        .getJSONObject("content").getString("text"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tickets").getJSONObject(0).getString("created_at"),
                        apiRestResponse.getBody().getJSONArray("tickets").getJSONObject(0).getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tickets").getJSONObject(0).getString("summary"),
                        apiRestResponse.getBody().getJSONArray("tickets").getJSONObject(0).getString("summary"));
    }
    
    /**
     * Negative test case for listTickets method.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {listTickets} integration test with negative case.")
    public void testListTicketWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listTickets");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTickets_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/tickets" + authString + "&until=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for addLabel method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {addLabel} integration test with mandatory parameters.", dependsOnMethods = { "testListTicketsWithOptionalParameters" })
    public void testAddLabelWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:addLabel");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addLabel_mandatory.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/tickets/" + connectorProperties.getProperty("ticketIdOptional") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("labelName"), apiRestResponse.getBody().getJSONObject(
                        "ticket").getJSONArray("labels").getJSONObject(0).getString("name"));
    }
    
    /**
     * Negative test case for addLabel method.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {addLabel} integration test with negative case.", dependsOnMethods = { "testAddLabelWithMandatoryParameters" })
    public void testAddLabelWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:addLabel");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addLabel_negative.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/tickets/" + connectorProperties.getProperty("ticketIdOptional")
                                        + "/labels/INVALID" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Positive test case for removeLabel method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {removeLabel} integration test with mandatory parameters.", dependsOnMethods = { "testAddLabelWithNegativeCase" })
    public void testRemoveLabelWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:removeLabel");
        final String apiEndPoint =
                        apiRequestUrl + "/tickets/" + connectorProperties.getProperty("ticketIdOptional") + authString;
        RestResponse<JSONObject> apiRestResponsBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_removeLabel_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponsBefore.getBody().getJSONObject("ticket").getJSONArray("labels").length(),
                        0);
        Assert.assertEquals(connectorProperties.getProperty("labelName"), apiRestResponsBefore.getBody().getJSONObject(
                        "ticket").getJSONArray("labels").getJSONObject(0).getString("name"));
        Assert.assertEquals(apiRestResponseAfter.getBody().getJSONObject("ticket").getJSONArray("labels").length(), 0);
    }
    
    /**
     * Negative test case for removeLabel method.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {removeLabel} integration test with negative case.", dependsOnMethods = { "testRemoveLabelWithMandatoryParameters" })
    public void testRemoveLabelWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:removeLabel");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_removeLabel_negative.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/tickets/" + connectorProperties.getProperty("ticketIdOptional") + "/labels/"
                                        + connectorProperties.getProperty("labelName") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listLabels method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {listLabels} integration test with mandatory parameters.")
    public void testListLabelsWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listLabels");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listLabels_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/labels" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("labels").length(), apiRestResponse.getBody()
                        .getJSONArray("labels").length());
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("labels").getJSONObject(0).getString("name"),
                        apiRestResponse.getBody().getJSONArray("labels").getJSONObject(0).getString("name"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("labels").getJSONObject(0).getString("color"),
                        apiRestResponse.getBody().getJSONArray("labels").getJSONObject(0).getString("color"));
    }
    
    /**
     * Positive test case for createReply method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {createReply} integration test with mandatory parameters.", dependsOnMethods = { "testRemoveLabelWithNegativeCase" })
    public void testCreateReplyWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createReply");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createReply_mandatory.json");
        final String replyIdMandatory = esbRestResponse.getBody().getJSONObject("reply").getString("id");
        
        final String apiEndPoint =
                        apiRequestUrl + "/tickets/" + connectorProperties.getProperty("ticketIdOptional") + "/replies/"
                                        + replyIdMandatory + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("textReplyMandatory"), apiRestResponse.getBody()
                        .getJSONObject("reply").getJSONObject("content").getString("text"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("reply").getString("created_at"), apiRestResponse
                        .getBody().getJSONObject("reply").getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("reply").getString("summary"), apiRestResponse
                        .getBody().getJSONObject("reply").getString("summary"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("reply").getJSONObject("replier").getString("id"),
                        apiRestResponse.getBody().getJSONObject("reply").getJSONObject("replier").getString("id"));
    }
    
    /**
     * Positive test case for createReply method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {createReply} integration test with optional parameters.", dependsOnMethods = { "testCreateReplyWithMandatoryParameters" })
    public void testCreateReplyWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createReply");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createReply_optional.json");
        final String replyIdOptional = esbRestResponse.getBody().getJSONObject("reply").getString("id");
        connectorProperties.setProperty("replyIdOptional", replyIdOptional);
        
        final String apiEndPoint =
                        apiRequestUrl + "/tickets/" + connectorProperties.getProperty("ticketIdOptional") + "/replies/"
                                        + replyIdOptional + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("textReplyOptional"), apiRestResponse.getBody()
                        .getJSONObject("reply").getJSONObject("content").getString("text"));
        Assert.assertEquals(connectorProperties.getProperty("html"), apiRestResponse.getBody().getJSONObject("reply")
                        .getJSONObject("content").getString("html"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("reply").getString("created_at"), apiRestResponse
                        .getBody().getJSONObject("reply").getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("reply").getString("summary"), apiRestResponse
                        .getBody().getJSONObject("reply").getString("summary"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("reply").getJSONObject("replier").getString("id"),
                        apiRestResponse.getBody().getJSONObject("reply").getJSONObject("replier").getString("id"));
    }
    
    /**
     * Negative test case for createReply method.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {createReply} integration test with negative case.")
    public void testCreateReplyWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createReply");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createReply_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/tickets/456789/replies" + authString;
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createReply_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for getReply method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {getReply} integration test with mandatory parameters.", dependsOnMethods = { "testCreateReplyWithOptionalParameters" })
    public void testGetReplyWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getReply");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getReply_mandatory.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/tickets/" + connectorProperties.getProperty("ticketIdOptional") + "/replies/"
                                        + connectorProperties.getProperty("replyIdOptional") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(
                        esbRestResponse.getBody().getJSONObject("reply").getJSONObject("content").getString("text"),
                        apiRestResponse.getBody().getJSONObject("reply").getJSONObject("content").getString("text"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("reply").getString("created_at"), apiRestResponse
                        .getBody().getJSONObject("reply").getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("reply").getString("summary"), apiRestResponse
                        .getBody().getJSONObject("reply").getString("summary"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("reply").getJSONObject("replier").getString("id"),
                        apiRestResponse.getBody().getJSONObject("reply").getJSONObject("replier").getString("id"));
    }
    
    /**
     * Negative test case for getReply method.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {getReply} integration test with negative case.", dependsOnMethods = { "testGetReplyWithMandatoryParameters" })
    public void testGetReplyWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getReply");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getReply_negative.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/tickets/" + connectorProperties.getProperty("ticketIdOptional")
                                        + "/replies/987654" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listReplies method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {listReplies} integration test with mandatory parameters.", dependsOnMethods = { "testGetReplyWithNegativeCase" })
    public void testListRepliesWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listReplies");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listReplies_mandatory.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/tickets/" + connectorProperties.getProperty("ticketIdOptional") + "/replies"
                                        + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("replies").length(), apiRestResponse.getBody()
                        .getJSONArray("replies").length());
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("replies").getJSONObject(0).getJSONObject("replier")
                        .getString("email"), apiRestResponse.getBody().getJSONArray("replies").getJSONObject(0)
                        .getJSONObject("replier").getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("replies").getJSONObject(0).getString("created_at"),
                        apiRestResponse.getBody().getJSONArray("replies").getJSONObject(0).getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("replies").getJSONObject(0).getString("summary"),
                        apiRestResponse.getBody().getJSONArray("replies").getJSONObject(0).getString("summary"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("replies").getJSONObject(0).getString("merged"),
                        apiRestResponse.getBody().getJSONArray("replies").getJSONObject(0).getString("merged"));
    }
    
    /**
     * Negative test case for listReplies method.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {listReplies} integration test with negative case.")
    public void testListRepliesWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listReplies");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listReplies_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/tickets/654987/replies" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listAgents method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {listAgents} integration test with mandatory parameters.", dependsOnMethods = { "testListRepliesWithMandatoryParameters" })
    public void testListAgentsWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listAgents");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAgents_mandatory.json");
        connectorProperties.setProperty("userId", esbRestResponse.getBody().getJSONArray("users").getJSONObject(0)
                        .getString("id"));
        
        final String apiEndPoint = apiRequestUrl + "/users" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("users").length(), apiRestResponse.getBody()
                        .getJSONArray("users").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("users").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("users").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("users").getJSONObject(0).getString("email"),
                        apiRestResponse.getBody().getJSONArray("users").getJSONObject(0).getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("users").getJSONObject(0).getString("name"),
                        apiRestResponse.getBody().getJSONArray("users").getJSONObject(0).getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("users").getJSONObject(0).getString("agent"),
                        apiRestResponse.getBody().getJSONArray("users").getJSONObject(0).getString("agent"));
    }
    
    /**
     * Positive test case for listAgents method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {listAgents} integration test with optional parameters.", dependsOnMethods = { "testListAgentsWithMandatoryParameters" })
    public void testListAgentsWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listAgents");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAgents_optional.json");
        
        final String apiEndPoint = apiRequestUrl + "/users" + authString + "&with_invited=true";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("users").length(), apiRestResponse.getBody()
                        .getJSONArray("users").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("users").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("users").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("users").getJSONObject(0).getString("email"),
                        apiRestResponse.getBody().getJSONArray("users").getJSONObject(0).getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("users").getJSONObject(0).getString("name"),
                        apiRestResponse.getBody().getJSONArray("users").getJSONObject(0).getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("users").getJSONObject(0).getString("agent"),
                        apiRestResponse.getBody().getJSONArray("users").getJSONObject(0).getString("agent"));
    }
    
    /**
     * Positive test case for getUser method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {getUser} integration test with mandatory parameters.", dependsOnMethods = { "testListAgentsWithOptionalParameters" })
    public void testGetUserWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getUser");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUser_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/users/" + connectorProperties.getProperty("userId") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").getString("id"), apiRestResponse.getBody()
                        .getJSONObject("user").getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").getString("email"), apiRestResponse
                        .getBody().getJSONObject("user").getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").getString("name"), apiRestResponse
                        .getBody().getJSONObject("user").getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").getString("agent"), apiRestResponse
                        .getBody().getJSONObject("user").getString("agent"));
    }
    
    /**
     * Negative test case for getUser method.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {getUser} integration test with negative case.")
    public void testGetUserWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getUser");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUser_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/users/999999" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for assignUser method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {assignUser} integration test with mandatory parameters.", dependsOnMethods = { "testGetUserWithMandatoryParameters" })
    public void testAssignUserWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:assignUser");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_assignUser_mandatory.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/tickets/" + connectorProperties.getProperty("ticketIdOptional") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("userId"), apiRestResponse.getBody()
                        .getJSONObject("ticket").getJSONObject("current_assignee").getJSONObject("user")
                        .getString("id"));
    }
    
    /**
     * Negative test case for assignUser method.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {assignUser} integration test with negative case.", dependsOnMethods = { "testAssignUserWithMandatoryParameters" })
    public void testAssignUserWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:assignUser");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_assignUser_negative.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/tickets/" + connectorProperties.getProperty("ticketIdOptional")
                                        + "/assignments" + authString;
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndPoint, "POST", esbRequestHeadersMap, "api_assignUser_negative.json");
        
        // Asserting status 409 Conflict
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 409);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 409);
    }
    
    /**
     * Positive test case for createComment method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {createComment} integration test with mandatory parameters.", dependsOnMethods = { "testAssignUserWithNegativeCase" })
    public void testCreateCommentWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createComment");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_mandatory.json");
        final String commentIdMandatory = esbRestResponse.getBody().getJSONObject("comment").getString("id");
        
        final String apiEndPoint =
                        apiRequestUrl + "/tickets/" + connectorProperties.getProperty("ticketIdOptional") + "/comments"
                                        + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONArray commentsArray = apiRestResponse.getBody().getJSONArray("comments");
        
        for (int i = 0; i < commentsArray.length(); i++) {
            if (commentsArray.getJSONObject(i).getString("id").equals(commentIdMandatory)) {
                Assert.assertEquals(connectorProperties.getProperty("textCommentMandatory"), commentsArray
                                .getJSONObject(i).getJSONObject("content").getString("text"));
                Assert.assertEquals(commentsArray.getJSONObject(i).getString("created_at"), commentsArray
                                .getJSONObject(i).getString("created_at"));
                Assert.assertEquals(commentsArray.getJSONObject(i).getJSONObject("ticket").getString("comments_count"),
                                commentsArray.getJSONObject(i).getJSONObject("ticket").getString("comments_count"));
                Assert.assertEquals(commentsArray.getJSONObject(i).getJSONObject("commenter").getString("id"),
                                commentsArray.getJSONObject(i).getJSONObject("commenter").getString("id"));
                Assert.assertEquals(commentsArray.getJSONObject(i).getJSONObject("commenter").getString("email"),
                                commentsArray.getJSONObject(i).getJSONObject("commenter").getString("email"));
                break;
            }
        }
        
    }
    
    /**
     * Positive test case for createComment method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {createComment} integration test with optional parameters.", dependsOnMethods = { "testCreateCommentWithMandatoryParameters" })
    public void testCreateCommentWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createComment");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_optional.json");
        final String commentIdOptional = esbRestResponse.getBody().getJSONObject("comment").getString("id");
        
        final String apiEndPoint =
                        apiRequestUrl + "/tickets/" + connectorProperties.getProperty("ticketIdOptional") + "/comments"
                                        + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONArray commentsArray = apiRestResponse.getBody().getJSONArray("comments");
        
        for (int i = 0; i < commentsArray.length(); i++) {
            if (commentsArray.getJSONObject(i).getString("id").equals(commentIdOptional)) {
                Assert.assertEquals(connectorProperties.getProperty("textCommentOptional"), commentsArray
                                .getJSONObject(i).getJSONObject("content").getString("text"));
                Assert.assertEquals(connectorProperties.getProperty("html"), commentsArray.getJSONObject(i)
                                .getJSONObject("content").getString("html"));
                Assert.assertEquals(commentsArray.getJSONObject(i).getString("created_at"), commentsArray
                                .getJSONObject(i).getString("created_at"));
                Assert.assertEquals(commentsArray.getJSONObject(i).getJSONObject("ticket").getString("comments_count"),
                                commentsArray.getJSONObject(i).getJSONObject("ticket").getString("comments_count"));
                Assert.assertEquals(commentsArray.getJSONObject(i).getJSONObject("commenter").getString("id"),
                                commentsArray.getJSONObject(i).getJSONObject("commenter").getString("id"));
                Assert.assertEquals(commentsArray.getJSONObject(i).getJSONObject("commenter").getString("email"),
                                commentsArray.getJSONObject(i).getJSONObject("commenter").getString("email"));
                break;
            }
        }
    }
    
    /**
     * Negative test case for createComment method.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {createComment} integration test with negative case.")
    public void testCreateCommentWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createComment");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/tickets/111111/comments" + authString;
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                                        "api_createComment_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listComments method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {listComments} integration test with mandatory parameters.", dependsOnMethods = { "testCreateCommentWithOptionalParameters" })
    public void testListCommentsWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listComments");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listComments_mandatory.json");
        
        final String apiEndPoint =
                        apiRequestUrl + "/tickets/" + connectorProperties.getProperty("ticketIdOptional") + "/comments"
                                        + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("comments").length(), apiRestResponse.getBody()
                        .getJSONArray("comments").length());
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("comments").getJSONObject(0)
                        .getJSONObject("content").getString("text"), apiRestResponse.getBody().getJSONArray("comments")
                        .getJSONObject(0).getJSONObject("content").getString("text"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("comments").getJSONObject(0)
                        .getJSONObject("content").getString("html"), apiRestResponse.getBody().getJSONArray("comments")
                        .getJSONObject(0).getJSONObject("content").getString("html"));
        Assert.assertEquals(
                        esbRestResponse.getBody().getJSONArray("comments").getJSONObject(0).getString("created_at"),
                        apiRestResponse.getBody().getJSONArray("comments").getJSONObject(0).getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("comments").getJSONObject(0).getJSONObject(
                        "commenter").getString("email"), apiRestResponse.getBody().getJSONArray("comments")
                        .getJSONObject(0).getJSONObject("commenter").getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("comments").getJSONObject(0).getJSONObject(
                        "commenter").getString("id"), apiRestResponse.getBody().getJSONArray("comments").getJSONObject(
                        0).getJSONObject("commenter").getString("id"));
    }
    
    /**
     * Negative test case for listComments method.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {listComments} integration test with negative case.")
    public void testListCommentsWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listComments");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listComments_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/tickets/222222/comments" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for archiveTicket method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {archiveTicket} integration test with mandatory parameters.", dependsOnMethods = { "testListCommentsWithMandatoryParameters" })
    public void testArchiveTicketWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:archiveTicket");
        final String apiEndPoint =
                        apiRequestUrl + "/tickets/" + connectorProperties.getProperty("ticketIdOptional") + authString;
        
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", esbRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_archiveTicket_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponseBefore.getBody().getJSONObject("ticket").getBoolean("archived"), false);
        Assert.assertEquals(apiRestResponseAfter.getBody().getJSONObject("ticket").getBoolean("archived"), true);
    }
    
    /**
     * Negative test case for archiveTicket method.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {archiveTicket} integration test with negative case.")
    public void testArchiveTicketWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:archiveTicket");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_archiveTicket_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/tickets/333333/archive" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for trashTicket method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {trashTicket} integration test with mandatory parameters.", dependsOnMethods = { "testArchiveTicketWithMandatoryParameters" })
    public void testTrashTicketWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:trashTicket");
        final String apiEndPoint =
                        apiRequestUrl + "/tickets/" + connectorProperties.getProperty("ticketIdOptional") + authString;
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_trashTicket_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponseBefore.getBody().getJSONObject("ticket").getBoolean("trash"), false);
        Assert.assertEquals(apiRestResponseAfter.getBody().getJSONObject("ticket").getBoolean("trash"), true);
    }
    
    /**
     * Negative test case for trashTicket method.
     */
    @Test(groups = { "wso2.esb" }, description = "supportbee {trashTicket} integration test with negative case.")
    public void testTrashTicketWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:trashTicket");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_trashTicket_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/tickets/444444/trash" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
}
