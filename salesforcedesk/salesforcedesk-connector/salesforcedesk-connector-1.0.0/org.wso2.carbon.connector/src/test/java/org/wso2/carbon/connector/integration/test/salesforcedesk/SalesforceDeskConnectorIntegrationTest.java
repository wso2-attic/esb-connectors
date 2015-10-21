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
package org.wso2.carbon.connector.integration.test.salesforcedesk;

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

public class SalesforceDeskConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private String apiUrl;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        
        init("salesforcedesk-connector-1.0.0");
        
        String authorizationString =
                connectorProperties.getProperty("email") + ":" + connectorProperties.getProperty("password");
        String authorizationToken = new String(Base64.encodeBase64(authorizationString.getBytes()));
        
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Authorization", "Basic " + authorizationToken);
        
        apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v2";
        
    }
    
    /**
     * Positive test case for listReplies method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateReplyWithMandatoryParameters", "testCreateReplyWithOptionalParameters" }, groups = { "wso2.esb" }, description = "SalesforceDesk {listReplies} integration test with mandatory parameters.")
    public void testListRepliesWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listReplies");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listReplies_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        final String caseId = connectorProperties.getProperty("caseIdWithAttachment");
        final String apiEndPoint = apiUrl + "/cases/" + caseId + "/replies";
        
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final int resultsCount = esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries").length();
        
        Assert.assertEquals(esbRestResponse.getBody().getString("total_entries"), apiRestResponse.getBody().getString(
                "total_entries"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries").getJSONObject(
                0).getString("id"), apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries")
                .getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries").getJSONObject(
                0).getString("body"), apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries")
                .getJSONObject(0).getString("body"));
        if (resultsCount > 1) {
            Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries")
                    .getJSONObject(resultsCount - 1).getString("id"), apiRestResponse.getBody().getJSONObject(
                    "_embedded").getJSONArray("entries").getJSONObject(resultsCount - 1).getString("id"));
            Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries")
                    .getJSONObject(resultsCount - 1).getString("body"), apiRestResponse.getBody().getJSONObject(
                    "_embedded").getJSONArray("entries").getJSONObject(resultsCount - 1).getString("body"));
            
        }
    }
    
    /**
     * Positive test case for listReplies method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateReplyWithMandatoryParameters", "testCreateReplyWithOptionalParameters" }, groups = { "wso2.esb" }, description = "SalesforceDesk {listReplies} integration test with optional parameters.")
    public void testListRepliesWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listReplies");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listReplies_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        final String caseId = connectorProperties.getProperty("caseIdWithAttachment");
        final String apiEndPoint =
                apiUrl + "/cases/" + caseId + "/replies?sort_direction=desc&sort_field=created_at&page=1&per_page=3";
        
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final int resultsCount = esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries").length();
        
        Assert.assertEquals(esbRestResponse.getBody().getString("total_entries"), apiRestResponse.getBody().getString(
                "total_entries"));
        Assert.assertEquals(esbRestResponse.getBody().getString("page"), apiRestResponse.getBody().getString("page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries").getJSONObject(
                0).getString("body"), apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries")
                .getJSONObject(0).getString("body"));
        if (resultsCount > 1) {
            Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries")
                    .getJSONObject(resultsCount - 1).getString("id"), apiRestResponse.getBody().getJSONObject(
                    "_embedded").getJSONArray("entries").getJSONObject(resultsCount - 1).getString("id"));
            Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries")
                    .getJSONObject(resultsCount - 1).getString("body"), apiRestResponse.getBody().getJSONObject(
                    "_embedded").getJSONArray("entries").getJSONObject(resultsCount - 1).getString("body"));
            Assert.assertTrue(Integer.parseInt(apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray(
                    "entries").getJSONObject(0).getString("id")) > Integer.parseInt(apiRestResponse.getBody()
                    .getJSONObject("_embedded").getJSONArray("entries").getJSONObject(1).getString("id")));
        }
    }
    
    /**
     * Negative test case for listReplies method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {listReplies} integration test with negative case.")
    public void testListRepliesWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listReplies");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listReplies_negative.json");
        
        final String apiEndPoint = apiUrl + "/cases/INVALID/replies";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString(
                "message"));
        
    }
    
    /**
     * Positive test case for forwardCase method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateCaseWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "SalesforceDesk {forwardCase} integration test with mandatory parameters.")
    public void testForwardCaseWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:forwardCase");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_forwardCase_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "Created");
    }
    
    /**
     * Positive test case for forwardCase method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateCaseWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "SalesforceDesk {forwardCase} integration test with optional parameters.")
    public void testForwardCaseWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:forwardCase");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_forwardCase_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "Created");
    }
    
    /**
     * Negative test case for forwardCase method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateCaseWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "SalesforceDesk {forwardCase} integration test with negative case.")
    public void testForwardCaseWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:forwardCase");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_forwardCase_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), "to parameter is required");
        
    }
    
    /**
     * Positive test case for createReply method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateCaseWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "SalesforceDesk {createReply} integration test with mandatory parameters.")
    public void testCreateReplyWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createReply");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createReply_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        final String href = esbRestResponse.getBody().getJSONObject("_links").getJSONObject("self").getString("href");
        
        final String apiEndPoint = apiUrl + href.split("v2")[1];
        
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("body"), connectorProperties.getProperty("caseBody"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("updated_at"), apiRestResponse.getBody().getString(
                "updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("direction"), apiRestResponse.getBody().getString(
                "direction"));
        
    }
    
    /**
     * Positive test case for createReply method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateCaseWithOptionalParameters" }, groups = { "wso2.esb" }, description = "SalesforceDesk {createReply} integration test with optional parameters.")
    public void testCreateReplyWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createReply");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createReply_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        final String apiEndPoint = esbRestResponse.getHeadersMap().get("Location").get(0);
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getString("body"), connectorProperties.getProperty("caseBody"));
        Assert.assertEquals(apiRestResponse.getBody().getString("created_at"), connectorProperties
                .getProperty("caseCreatedAt"));
        Assert.assertEquals(apiRestResponse.getBody().getString("updated_at"), connectorProperties
                .getProperty("caseUpdatedAt"));
        Assert.assertEquals(apiRestResponse.getBody().getString("direction"), connectorProperties
                .getProperty("caseDirection"));
        Assert.assertEquals(apiRestResponse.getBody().getString("status"), connectorProperties
                .getProperty("caseStatus"));
        
    }
    
    /**
     * Negative test case for createReply method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateReplyWithOptionalParameters" }, groups = { "wso2.esb" }, description = "SalesforceDesk {createReply} integration test with negative case.")
    public void testCreateReplyWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createReply");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createReply_negative.json");
        
        final String caseId = connectorProperties.getProperty("caseIdOpt");
        final String apiEndPoint = apiUrl + "/cases/" + caseId + "/replies";
        final RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createReply_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 409);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 409);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString(
                "message"));
        
    }
    
    /**
     * Positive test case for listNotes method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateNoteWithMandatoryParameters", "testCreateNoteWithOptionalParameters" }, groups = { "wso2.esb" }, description = "SalesforceDesk {listNotes} integration test with mandatory parameters.")
    public void testListNotesWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listNotes");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listNotes_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        final String caseId = connectorProperties.getProperty("caseIdMand");
        final String apiEndPoint = apiUrl + "/cases/" + caseId + "/notes";
        
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final int resultsCount = esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries").length();
        
        Assert.assertEquals(esbRestResponse.getBody().getString("total_entries"), apiRestResponse.getBody().getString(
                "total_entries"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries").getJSONObject(
                0).getString("id"), apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries")
                .getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries").getJSONObject(
                0).getString("body"), apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries")
                .getJSONObject(0).getString("body"));
        if (resultsCount > 1) {
            Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries")
                    .getJSONObject(resultsCount - 1).getString("id"), apiRestResponse.getBody().getJSONObject(
                    "_embedded").getJSONArray("entries").getJSONObject(resultsCount - 1).getString("id"));
            Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries")
                    .getJSONObject(resultsCount - 1).getString("body"), apiRestResponse.getBody().getJSONObject(
                    "_embedded").getJSONArray("entries").getJSONObject(resultsCount - 1).getString("body"));
            
        }
    }
    
    /**
     * Positive test case for listNotes method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateNoteWithMandatoryParameters", "testCreateNoteWithOptionalParameters" }, groups = { "wso2.esb" }, description = "SalesforceDesk {listNotes} integration test with optional parameters.")
    public void testListNotesWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listNotes");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listNotes_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        final String caseId = connectorProperties.getProperty("caseIdMand");
        final String apiEndPoint = apiUrl + "/cases/" + caseId + "/notes?sort_direction=page=1&per_page=3";
        
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final int resultsCount = esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries").length();
        
        Assert.assertEquals(esbRestResponse.getBody().getString("total_entries"), apiRestResponse.getBody().getString(
                "total_entries"));
        Assert.assertEquals(esbRestResponse.getBody().getString("page"), apiRestResponse.getBody().getString("page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries").getJSONObject(
                0).getString("id"), apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries")
                .getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries").getJSONObject(
                0).getString("body"), apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries")
                .getJSONObject(0).getString("body"));
        if (resultsCount > 1) {
            Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries")
                    .getJSONObject(resultsCount - 1).getString("id"), apiRestResponse.getBody().getJSONObject(
                    "_embedded").getJSONArray("entries").getJSONObject(resultsCount - 1).getString("id"));
            Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries")
                    .getJSONObject(resultsCount - 1).getString("body"), apiRestResponse.getBody().getJSONObject(
                    "_embedded").getJSONArray("entries").getJSONObject(resultsCount - 1).getString("body"));
            
        }
    }
    
    /**
     * Negative test case for listNotes method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {listNotes} integration test with negative case.")
    public void testListNotesWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listNotes");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listNotes_negative.json");
        
        final String apiEndPoint = apiUrl + "/cases/INVALID/notes";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString(
                "message"));
        
    }
    
    /**
     * Positive test case for showNote method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateNoteWithOptionalParameters" }, groups = { "wso2.esb" }, description = "SalesforceDesk {showNote} integration test with mandatory parameters.")
    public void testShowNoteWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:showNote");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_showNote_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        final String caseId = connectorProperties.getProperty("caseIdMand");
        final String noteId = connectorProperties.getProperty("caseNoteId");
        final String apiEndPoint = apiUrl + "/cases/" + caseId + "/notes/" + noteId;
        
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("updated_at"), apiRestResponse.getBody().getString(
                "updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("body"), apiRestResponse.getBody().getString("body"));
        Assert.assertEquals(esbRestResponse.getBody().getString("erased_at"), apiRestResponse.getBody().getString(
                "erased_at"));
        
    }
    
    /**
     * Positive test case for showNote method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateNoteWithOptionalParameters" }, groups = { "wso2.esb" }, description = "SalesforceDesk {showNote} integration test with optional parameters.")
    public void testShowNoteWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:showNote");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_showNote_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        final String caseId = connectorProperties.getProperty("caseIdMand");
        final String noteId = connectorProperties.getProperty("caseNoteId");
        
        final String apiFirstEndPoint = apiUrl + "/cases/" + caseId + "/notes/" + noteId + "?fields=id";
        final String apiSecondEndPoint = apiUrl + "/cases/" + caseId + "/notes/" + noteId;
        
        final RestResponse<JSONObject> apiFirstRestResponse =
                sendJsonRestRequest(apiFirstEndPoint, "GET", apiRequestHeadersMap);
        final RestResponse<JSONObject> apiSecondRestResponse =
                sendJsonRestRequest(apiSecondEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().length(), apiFirstRestResponse.getBody().length());
        Assert.assertNotEquals(esbRestResponse.getBody().length(), apiSecondRestResponse.getBody().length());
        
    }
    
    /**
     * Negative test case for showNote method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {showNote} integration test with negative case.")
    public void testShowNoteWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:showNote");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_showNote_negative.json");
        
        final String apiEndPoint = apiUrl + "/cases/INVALID/notes/INVALID";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString(
                "message"));
        
    }
    
    /**
     * Positive test case for showCaseAttachment method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {showCaseAttachment} integration test with mandatory parameters.")
    public void testShowCaseAttachmentWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:showCaseAttachment");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_showCaseAttachment_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        final String caseId = connectorProperties.getProperty("caseIdWithAttachment");
        final String attachmentId = connectorProperties.getProperty("caseAttachmentId");
        final String apiEndPoint = apiUrl + "/cases/" + caseId + "/attachments/" + attachmentId;
        
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("updated_at"), apiRestResponse.getBody().getString(
                "updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("file_name"), apiRestResponse.getBody().getString(
                "file_name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("erased_at"), apiRestResponse.getBody().getString(
                "erased_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("content_type"), apiRestResponse.getBody().getString(
                "content_type"));
        
    }
    
    /**
     * Positive test case for showCaseAttachment method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {showCaseAttachment} integration test with optional parameters.")
    public void testShowCaseAttachmentWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:showCaseAttachment");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_showCaseAttachment_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        final String caseId = connectorProperties.getProperty("caseIdWithAttachment");
        final String attachmentId = connectorProperties.getProperty("caseAttachmentId");
        
        final String apiFirstEndPoint = apiUrl + "/cases/" + caseId + "/attachments/" + attachmentId + "?fields=id";
        final String apiSecondEndPoint = apiUrl + "/cases/" + caseId + "/attachments/" + attachmentId;
        
        final RestResponse<JSONObject> apiFirstRestResponse =
                sendJsonRestRequest(apiFirstEndPoint, "GET", apiRequestHeadersMap);
        final RestResponse<JSONObject> apiSecondRestResponse =
                sendJsonRestRequest(apiSecondEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().length(), apiFirstRestResponse.getBody().length());
        Assert.assertNotEquals(esbRestResponse.getBody().length(), apiSecondRestResponse.getBody().length());
        
    }
    
    /**
     * Negative test case for showCaseAttachment method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {showCaseAttachment} integration test with negative case.")
    public void testShowCaseAttachmentWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:showCaseAttachment");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_showCaseAttachment_negative.json");
        
        final String apiEndPoint = apiUrl + "/cases/INVALID/attachments/INVALID";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString(
                "message"));
        
    }
    
    /**
     * Positive test case for showCompany method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {showCompany} integration test with mandatory parameters.")
    public void testShowCompanyWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:showCompany");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_showCompany_mandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        final String companyId = connectorProperties.getProperty("companyIdMand");
        final String apiEndPoint = apiUrl + "/companies/" + companyId;
        
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("updated_at"), apiRestResponse.getBody().getString(
                "updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("domains"), apiRestResponse.getBody().getString(
                "domains"));
        
    }
    
    /**
     * Positive test case for showCompany method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {showCompany} integration test with optional parameters.")
    public void testShowCompanyWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:showCompany");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_showCompany_optional.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        final String companyId = connectorProperties.getProperty("companyIdMand");
        
        final String apiFirstEndPoint = apiUrl + "/companies/" + companyId + "?fields=id";
        final String apiSecondEndPoint = apiUrl + "/companies/" + companyId;
        
        final RestResponse<JSONObject> apiFirstRestResponse =
                sendJsonRestRequest(apiFirstEndPoint, "GET", apiRequestHeadersMap);
        final RestResponse<JSONObject> apiSecondRestResponse =
                sendJsonRestRequest(apiSecondEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().length(), apiFirstRestResponse.getBody().length());
        Assert.assertNotEquals(esbRestResponse.getBody().length(), apiSecondRestResponse.getBody().length());
        
    }
    
    /**
     * Negative test case for showCompany method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {showCompany} integration test with negative case.")
    public void testShowCompanyWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:showCompany");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_showCompany_negative.json");
        
        final String apiEndPoint = apiUrl + "/companies/INVALID";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString(
                "message"));
        
    }
    
    /**
     * Positive test case for createNote method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateCaseWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "SalesforceDesk {createNote} integration test with mandatory parameters.")
    public void testCreateNoteWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createNote");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNote_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        final String noteId = esbRestResponse.getBody().getString("id");
        
        final String caseId = connectorProperties.getProperty("caseIdMand");
        final String apiEndPoint = apiUrl + "/cases/" + caseId + "/notes/" + noteId;
        
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("body"), connectorProperties.getProperty("caseBody"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("updated_at"), apiRestResponse.getBody().getString(
                "updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("erased_at"), apiRestResponse.getBody().getString(
                "erased_at"));
        
    }
    
    /**
     * Positive test case for createNote method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateCaseWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "SalesforceDesk {createNote} integration test with optional parameters.")
    public void testCreateNoteWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createNote");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNote_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        final String noteId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("caseNoteId", noteId);
        final String caseId = connectorProperties.getProperty("caseIdMand");
        final String apiFirstEndPoint =
                apiUrl + "/cases/" + caseId + "/notes/" + noteId + "?fields=updated_at,created_at,body,id";
        final String apiSecondEndPoint = apiUrl + "/cases/" + caseId + "/notes/" + noteId;
        
        final RestResponse<JSONObject> apiFirstRestResponse =
                sendJsonRestRequest(apiFirstEndPoint, "GET", apiRequestHeadersMap);
        final RestResponse<JSONObject> apiSecondRestResponse =
                sendJsonRestRequest(apiSecondEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiFirstRestResponse.getBody().getString("body"), connectorProperties
                .getProperty("caseBody"));
        Assert.assertEquals(apiFirstRestResponse.getBody().getString("created_at"), connectorProperties
                .getProperty("caseCreatedAt"));
        Assert.assertEquals(apiFirstRestResponse.getBody().getString("updated_at"), connectorProperties
                .getProperty("caseUpdatedAt"));
        
        Assert.assertEquals(esbRestResponse.getBody().length(), apiFirstRestResponse.getBody().length());
        Assert.assertNotEquals(esbRestResponse.getBody().length(), apiSecondRestResponse.getBody().length());
        
    }
    
    /**
     * Negative test case for createNote method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateCaseWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "SalesforceDesk {createNote} integration test with negative case.")
    public void testCreateNoteWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createNote");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNote_negative.json");
        
        final String caseId = connectorProperties.getProperty("caseIdMand");
        final String apiEndPoint = apiUrl + "/cases/" + caseId + "/notes";
        final RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createNote_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString(
                "message"));
        Assert.assertEquals(esbRestResponse.getBody().getString("errors"), apiRestResponse.getBody()
                .getString("errors"));
        
    }
    
    /**
     * Positive test case for createArticle method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateTopicWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "SalesforceDesk {createArticle} integration test with mandatory parameters.")
    public void testCreateArticleWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createArticle");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createArticle_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        final String articleId = esbRestResponse.getBody().getString("id");
        final String apiEndPoint = apiUrl + "/articles/" + articleId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final String linksTopicUrl =
                apiRestResponse.getBody().getJSONObject("_links").getJSONObject("topic").getString("href");
        final String topicId = linksTopicUrl.split("/")[4];
        
        Assert.assertEquals(apiRestResponse.getBody().getString("subject"), connectorProperties
                .getProperty("articleSubject"));
        Assert.assertEquals(topicId, connectorProperties.getProperty("topicIdMand"));
    }
    
    /**
     * Positive test case for createArticle method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateTopicWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "SalesforceDesk {createArticle} integration test with optional parameters.")
    public void testCreateArticleWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createArticle");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createArticle_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        final String articleId = esbRestResponse.getBody().getString("id");
        final String apiEndPoint = apiUrl + "/articles/" + articleId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final String articleQuickCode = connectorProperties.getProperty("articleQuickCodeOpt").toUpperCase();
        
        Assert.assertEquals(apiRestResponse.getBody().getString("body"), connectorProperties
                .getProperty("articleBodyOpt"));
        Assert.assertEquals(apiRestResponse.getBody().getString("quickcode"), articleQuickCode);
        Assert.assertEquals(apiRestResponse.getBody().getString("keywords"), connectorProperties
                .getProperty("articleKeywordsOpt"));
        Assert.assertEquals(apiRestResponse.getBody().getString("publish_at"), connectorProperties
                .getProperty("articlePublishAtOpt"));
        Assert.assertEquals(apiRestResponse.getBody().getString("internal_notes"), connectorProperties
                .getProperty("articleNotesOpt"));
    }
    
    /**
     * Negative test case for createArticle method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {createArticle} integration test with negative case.")
    public void testCreateArticleWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createArticle");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createArticle_negative.json");
        
        final String apiEndPoint = apiUrl + "/articles";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createArticle_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString(
                "message"));
    }
    
    /**
     * Positive test case for listArticles method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateArticleWithMandatoryParameters",
            "testCreateArticleWithOptionalParameters" }, description = "SalesforceDesk {listArticles} integration test with mandatory parameters.")
    public void testListArticlesWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listArticles");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listArticles_mandatory.json");
        final JSONArray esbArticles = esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        final JSONObject esbFirstArticleObject = esbArticles.getJSONObject(0);
        final JSONObject esbLastArticleObject = esbArticles.getJSONObject(esbArticles.length() - 1);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        final String apiEndPoint = apiUrl + "/articles";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONArray apiArticles = apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        final JSONObject apiFirstArticleObject = apiArticles.getJSONObject(0);
        final JSONObject apiLastArticleObject = apiArticles.getJSONObject(esbArticles.length() - 1);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("total_entries"), apiRestResponse.getBody().getString(
                "total_entries"));
        Assert.assertEquals(esbFirstArticleObject.getString("id"), apiFirstArticleObject.getString("id"));
        Assert.assertEquals(esbFirstArticleObject.getString("subject"), apiFirstArticleObject.getString("subject"));
        Assert.assertEquals(esbLastArticleObject.getString("id"), apiLastArticleObject.getString("id"));
        Assert.assertEquals(esbLastArticleObject.getString("subject"), apiLastArticleObject.getString("subject"));
    }
    
    /**
     * Positive test case for listArticles method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateArticleWithMandatoryParameters",
            "testCreateArticleWithOptionalParameters" }, description = "SalesforceDesk {listArticles} integration test with optional parameters.")
    public void testListArticlesWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listArticles");
        
        final String fields = "id,subject";
        connectorProperties.setProperty("articleFields", fields);
        final String perPage = "1";
        connectorProperties.setProperty("articlePerPage", perPage);
        final String page = "2";
        connectorProperties.setProperty("articlePage", page);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listArticles_optional.json");
        final JSONArray esbArticles = esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        final JSONObject esbArticleObject = esbArticles.getJSONObject(0);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        String apiEndPoint = apiUrl + "/articles";
        RestResponse<JSONObject> apiRestResponseWithoutOptParams =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONArray apiArticlesWithoutOptParams =
                apiRestResponseWithoutOptParams.getBody().getJSONObject("_embedded").getJSONArray("entries");
        final JSONObject apiArticleObjectWithoutOptParams = apiArticlesWithoutOptParams.getJSONObject(0);
        
        apiEndPoint = apiUrl + "/articles?page=" + page + "&per_page=" + perPage + "&fields=" + fields;
        RestResponse<JSONObject> apiRestResponseWithOptParams =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONArray apiArticlesWithOptParams =
                apiRestResponseWithOptParams.getBody().getJSONObject("_embedded").getJSONArray("entries");
        final JSONObject apiArticleObjectWithOptParams = apiArticlesWithOptParams.getJSONObject(0);
        
        Assert.assertNotEquals(esbArticleObject.length(), apiArticleObjectWithoutOptParams.length());
        Assert.assertNotEquals(esbArticleObject.has("body"), apiArticleObjectWithoutOptParams.has("body"));
        Assert.assertEquals(esbRestResponse.getBody().getString("page"), apiRestResponseWithOptParams.getBody()
                .getString("page"));
        Assert.assertEquals(esbArticleObject.length(), apiArticleObjectWithOptParams.length());
        Assert.assertEquals(esbArticleObject.has("id"), apiArticleObjectWithOptParams.has("id"));
    }
    
    /**
     * Test case: testListArticlesWithNegativeCase. Status: Skipped. Reason : There are no any negative case
     * to assert.
     */
    
    /**
     * Positive test case for showArticleAttachment method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {showArticleAttachment} integration test with mandatory parameters.")
    public void testShowArticleAttachmentWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:showArticleAttachment");
        
        final String articleId = connectorProperties.getProperty("articleIdWithAttachment");
        final String attachmentId = connectorProperties.getProperty("articleAttachmentId");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_showArticleAttachment_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        final String apiEndPoint = apiUrl + "/articles/" + articleId + "/attachments/" + attachmentId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("file_name"), apiRestResponse.getBody().getString(
                "file_name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("updated_at"), apiRestResponse.getBody().getString(
                "updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("size"), apiRestResponse.getBody().getString("size"));
        Assert.assertEquals(esbRestResponse.getBody().getString("url"), apiRestResponse.getBody().getString("url"));
    }
    
    /**
     * Positive test case for showArticleAttachment method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {showArticleAttachment} integration test with optional parameters.")
    public void testShowArticleAttachmentWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:showArticleAttachment");
        
        final String articleId = connectorProperties.getProperty("articleIdWithAttachment");
        final String attachmentId = connectorProperties.getProperty("articleAttachmentId");
        final String fields = "file_name,size";
        connectorProperties.setProperty("articleAttachmentFields", fields);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_showArticleAttachment_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        String apiEndPoint = apiUrl + "/articles/" + articleId + "/attachments/" + attachmentId;
        RestResponse<JSONObject> apiRestResponseWithoutOptParams =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        apiEndPoint = apiUrl + "/articles/" + articleId + "/attachments/" + attachmentId + "?fields=" + fields;
        RestResponse<JSONObject> apiRestResponseWithOptParams =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(esbRestResponse.getBody().length(), apiRestResponseWithoutOptParams.getBody().length());
        Assert.assertNotEquals(esbRestResponse.getBody().has("id"), apiRestResponseWithoutOptParams.getBody().has("id"));
        Assert.assertNotEquals(esbRestResponse.getBody().has("created_at"), apiRestResponseWithoutOptParams.getBody()
                .has("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().length(), apiRestResponseWithOptParams.getBody().length());
        Assert.assertEquals(esbRestResponse.getBody().has("file_name"), apiRestResponseWithOptParams.getBody().has(
                "file_name"));
        Assert.assertEquals(esbRestResponse.getBody().has("size"), apiRestResponseWithOptParams.getBody().has("size"));
    }
    
    /**
     * Negative test case for showArticleAttachment method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {showArticleAttachment} integration test with negative case.")
    public void testShowArticleAttachmentWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:showArticleAttachment");
        
        final String articleId = connectorProperties.getProperty("articleIdWithAttachment");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_showArticleAttachment_negative.json");
        
        final String apiEndPoint = apiUrl + "/articles/" + articleId + "/attachments/invalid";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "api_createArticle_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString(
                "message"));
    }
    
    /**
     * Positive test case for searchArticle method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateArticleWithMandatoryParameters",
            "testCreateArticleWithOptionalParameters" }, description = "SalesforceDesk {searchArticle} integration test with mandatory parameters.")
    public void testSearchArticleWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
        
        Thread.sleep(5000);
        
        esbRequestHeadersMap.put("Action", "urn:searchArticle");
        
        final String topicIds = connectorProperties.getProperty("topicIdMand");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchArticle_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        JSONArray esbArticlesArray = esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        final String apiEndPoint = apiUrl + "/articles/search?topic_ids=" + topicIds;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiArticlesArray = apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("total_entries"), apiRestResponse.getBody().getString(
                "total_entries"));
        Assert.assertEquals(esbArticlesArray.length(), apiArticlesArray.length());
        Assert.assertEquals(esbArticlesArray.getJSONObject(0).getString("id"), apiArticlesArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbArticlesArray.getJSONObject(esbArticlesArray.length() - 1).getString("id"),
                apiArticlesArray.getJSONObject(apiArticlesArray.length() - 1).getString("id"));
        
    }
    
    /**
     * Positive test case for searchArticle method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateArticleWithMandatoryParameters",
            "testCreateArticleWithOptionalParameters" }, description = "SalesforceDesk {searchArticle} integration test with optional parameters.")
    public void testSearchArticleWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        
        Thread.sleep(5000);
        
        esbRequestHeadersMap.put("Action", "urn:searchArticle");
        
        final String topicIds = connectorProperties.getProperty("topicIdMand");
        final String page = "1";
        final String perPage = "1";
        final String fields = "id,subject";
        connectorProperties.setProperty("serchArticlesPage", page);
        connectorProperties.setProperty("serchArticlesPerPage", perPage);
        connectorProperties.setProperty("serchArticlesFields", fields);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchArticle_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        JSONArray esbArticlesArray = esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        final String apiEndPoint =
                apiUrl + "/articles/search?topic_ids=" + topicIds + "&page=" + page + "&per_page=" + perPage
                        + "&fields=" + fields;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiArticlesArray = apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("page"), page);
        Assert.assertEquals(esbArticlesArray.length(), Integer.parseInt(perPage));
        Assert.assertEquals(esbArticlesArray.length(), apiArticlesArray.length());
        Assert.assertEquals(esbArticlesArray.getJSONObject(0).has("id"), true);
        Assert.assertEquals(esbArticlesArray.getJSONObject(0).has("id"), apiArticlesArray.getJSONObject(0).has("id"));
        Assert.assertEquals(esbArticlesArray.getJSONObject(0).has("subject"), true);
        Assert.assertEquals(esbArticlesArray.getJSONObject(0).has("subject"), apiArticlesArray.getJSONObject(0).has(
                "subject"));
        Assert.assertEquals(esbArticlesArray.getJSONObject(0).has("body"), false);
        Assert.assertEquals(esbArticlesArray.getJSONObject(0).has("body"), apiArticlesArray.getJSONObject(0)
                .has("body"));
    }
    
    /**
     * Negative test case for searchArticle method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {searchArticle} integration test with negative case.")
    public void testSearchArticleWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:searchArticle");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchArticle_negative.json");
        
        final String apiEndPoint = apiUrl + "/articles/search";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString(
                "message"));
    }
    
    /**
     * Positive test case for createCase method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateCustomerWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "SalesforceDesk {createCase} integration test with mandatory parameters.")
    public void testCreateCaseWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createCase");
        
        final String caseType = "email";
        final String messageDirection = "in";
        connectorProperties.setProperty("caseTypeMand", caseType);
        connectorProperties.setProperty("caseMessageDirectionMand", messageDirection);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCase_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        final String caseId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("caseIdMand", caseId);
        
        final String apiEndPoint = apiUrl + "/cases/" + caseId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("type"), connectorProperties
                .getProperty("caseTypeMand"));
        Assert.assertEquals(apiRestResponse.getBody().getString("subject"), connectorProperties
                .getProperty("caseMessageSubjectMand"));
        Assert.assertEquals(apiRestResponse.getBody().getString("blurb"), connectorProperties
                .getProperty("caseMessageBodyMand"));
    }
    
    /**
     * Positive test case for createCase method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateCustomerWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "SalesforceDesk {createCase} integration test with optional parameters.")
    public void testCreateCaseWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createCase");
        
        final String caseType = "phone";
        final String messageDirection = "in";
        connectorProperties.setProperty("caseTypeOpt", caseType);
        connectorProperties.setProperty("caseMessageDirectionOpt", messageDirection);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCase_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        final String caseId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("caseIdOpt", caseId);
        
        final String apiEndPoint = apiUrl + "/cases/" + caseId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("priority"), connectorProperties
                .getProperty("casePriority"));
        Assert.assertEquals(apiRestResponse.getBody().getString("external_id"), connectorProperties
                .getProperty("caseExternalId"));
        Assert.assertEquals(apiRestResponse.getBody().getString("language"), connectorProperties.getProperty(
                "caseLanguage").toLowerCase());
        Assert.assertEquals(apiRestResponse.getBody().getString("description"), connectorProperties
                .getProperty("caseDescription"));
        Assert.assertEquals(apiRestResponse.getBody().getString("locked_until"), connectorProperties
                .getProperty("caseLockedUntil"));
    }
    
    /**
     * Negative test case for createCase method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {createCase} integration test with negative case.")
    public void testCreateCaseWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createCase");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCase_negative.json");
        
        final String apiEndPoint = apiUrl + "/cases";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createCase_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString(
                "message"));
    }
    
    /**
     * Positive test case for searchCases method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCaseWithMandatoryParameters",
            "testCreateCaseWithOptionalParameters" }, description = "SalesforceDesk {searchCases} integration test with mandatory parameters.")
    public void testSearchCasesWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
        
        Thread.sleep(5000);
        
        esbRequestHeadersMap.put("Action", "urn:searchCases");
        
        final String caseIds =
                connectorProperties.getProperty("caseIdMand") + "," + connectorProperties.getProperty("caseIdOpt");
        connectorProperties.setProperty("caseIds", caseIds);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchCases_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        JSONArray esbCaseArray = esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        final String apiEndPoint = apiUrl + "/cases/search?case_id=" + caseIds;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiCaseArray = apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("total_entries"), apiRestResponse.getBody().getString(
                "total_entries"));
        Assert.assertEquals(esbCaseArray.length(), apiCaseArray.length());
        Assert.assertEquals(esbCaseArray.getJSONObject(0).getString("id"), apiCaseArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbCaseArray.getJSONObject(esbCaseArray.length() - 1).getString("id"), apiCaseArray
                .getJSONObject(apiCaseArray.length() - 1).getString("id"));
        
    }
    
    /**
     * Positive test case for searchCases method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateArticleWithMandatoryParameters",
            "testCreateArticleWithOptionalParameters" }, description = "SalesforceDesk {searchCases} integration test with optional parameters.")
    public void testsearchCasesWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        
        Thread.sleep(5000);
        
        esbRequestHeadersMap.put("Action", "urn:searchCases");
        
        final String caseIds =
                connectorProperties.getProperty("caseIdMand") + "," + connectorProperties.getProperty("caseIdOpt");
        final String page = "1";
        final String perPage = "1";
        final String fields = "id,created_at";
        connectorProperties.setProperty("caseIds", caseIds);
        connectorProperties.setProperty("serchCasesPage", page);
        connectorProperties.setProperty("serchCasesPerPage", perPage);
        connectorProperties.setProperty("serchCasesFields", fields);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchCases_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        JSONArray esbCaseArray = esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        final String apiEndPoint =
                apiUrl + "/cases/search?case_id=" + caseIds + "&page=" + page + "&per_page=" + perPage + "&fields="
                        + fields;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiCaseArray = apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("page"), page);
        Assert.assertEquals(esbCaseArray.length(), Integer.parseInt(perPage));
        Assert.assertEquals(esbCaseArray.length(), apiCaseArray.length());
        Assert.assertEquals(esbCaseArray.getJSONObject(0).has("id"), true);
        Assert.assertEquals(esbCaseArray.getJSONObject(0).has("id"), apiCaseArray.getJSONObject(0).has("id"));
        Assert.assertEquals(esbCaseArray.getJSONObject(0).has("created_at"), true);
        Assert.assertEquals(esbCaseArray.getJSONObject(0).has("created_at"), apiCaseArray.getJSONObject(0).has(
                "created_at"));
        Assert.assertEquals(esbCaseArray.getJSONObject(0).has("blurb"), false);
        Assert.assertEquals(esbCaseArray.getJSONObject(0).has("blurb"), apiCaseArray.getJSONObject(0).has("blurb"));
    }
    
    /**
     * Negative test case for searchCases method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {searchCases} integration test with negative case.")
    public void testSearchCasesWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:searchCases");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchCases_negative.json");
        
        final String apiEndPoint = apiUrl + "/cases/search";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString(
                "message"));
    }
    
    /**
     * Positive test case for createCompany method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {createCompany} integration test with mandatory parameters.")
    public void testCreateCompanyWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createCompany");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCompany_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        connectorProperties.setProperty("companyIdMand", esbRestResponse.getBody().getString("id"));
        
        final String apiEndPoint = apiUrl + "/companies/" + connectorProperties.getProperty("companyIdMand");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getString("name"), connectorProperties
                .getProperty("companyNameMand"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
    }
    
    /**
     * Positive test case for createCompany method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {createCompany} integration test with optional parameters.")
    public void testCreateCompanyWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createCompany");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCompany_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        connectorProperties.setProperty("companyIdOpt", esbRestResponse.getBody().getString("id"));
        
        final String apiEndPoint = apiUrl + "/companies/" + connectorProperties.getProperty("companyIdOpt");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("domains").get(0), connectorProperties
                .getProperty("companyDomain"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("custom_fields").getString("employer_id"),
                connectorProperties.getProperty("companyCustmFieldVal"));
    }
    
    /**
     * Negative test case for createCompany method. Provides the same value as company name.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "salesforceDesk {createCompany} integration test with negative case.")
    public void testCreateCompanyWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createCompany");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCompany_negative.json");
        
        final String apiEndPoint = apiUrl + "/companies";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createCompany_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getBody().getString("errors"), apiRestResponse.getBody()
                .getString("errors"));
    }
    
    /**
     * Positive test case for listGroups method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {listGroups} integration test with mandatory parameters.")
    public void testListGroupsWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listGroups");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listGroups_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        final String apiEndPoint = apiUrl + "/groups";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("total_entries"), apiRestResponse.getBody().getString(
                "total_entries"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries").getJSONObject(
                0).getString("id"), apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries")
                .getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries").getJSONObject(
                0).getString("name"), apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries")
                .getJSONObject(0).getString("name"));
    }
    
    /**
     * Positive test case for listGroups method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {listGroups} integration test with optional parameters.")
    public void testListGroupsWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listGroups");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listGroups_optional.json");
        final JSONArray esbGroups = esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        String apiEndPoint = apiUrl + "/groups";
        
        RestResponse<JSONObject> apiResponseWithoutOptParams =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONArray apiGroupsWithoutOptParams =
                apiResponseWithoutOptParams.getBody().getJSONObject("_embedded").getJSONArray("entries");
        final JSONObject apiGroupsObjectWithoutOptParams = apiGroupsWithoutOptParams.getJSONObject(0);
        
        apiEndPoint = apiUrl + "/groups?page=1&per_page=2&fields=id&sort_field=id&sort_direction=desc";
        
        RestResponse<JSONObject> apiRestResponseWithOptParams =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONArray apiGroupsWithOptParams =
                apiRestResponseWithOptParams.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        // Checking whether the total group count is equal in both esbRestResponse and apiRestResponse with
        // optional parameters.
        Assert.assertEquals(esbRestResponse.getBody().getString("total_entries"), apiRestResponseWithOptParams
                .getBody().getString("total_entries"));
        
        // Checking the functionality of 'fields' parameter.
        Assert.assertNotEquals(esbGroups.getJSONObject(0).has("name"), apiGroupsObjectWithoutOptParams.has("name"));
        Assert.assertEquals(esbGroups.getJSONObject(0).has("id"), apiGroupsObjectWithoutOptParams.has("id"));
        
        // Checking whether the IDs of elements had sorted according to the sort field provided.
        final Integer esbFirstGroupId = esbGroups.getJSONObject(0).getInt("id");
        final Integer esbSecondGroupId = esbGroups.getJSONObject(1).getInt("id");
        final Integer apiFirstGroupId = apiGroupsWithOptParams.getJSONObject(0).getInt("id");
        Assert.assertTrue(esbFirstGroupId > esbSecondGroupId);
        Assert.assertEquals(esbFirstGroupId, apiFirstGroupId);
        
        // Checking the functionality of 'perPage' and 'page' parameters.
        Assert.assertEquals(esbGroups.length(), apiGroupsWithOptParams.length());
        Assert.assertEquals(esbRestResponse.getBody().getInt("page"), apiRestResponseWithOptParams.getBody().getInt(
                "page"));
    }
    
    /**
     * Test case: testListGroupsWithNegativeCase. Status: Skipped. Reason : Cannot generate a negative
     * response for the method providing invalid parameters.
     */
    
    /**
     * Positive test case for listUsers method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {listUsers} integration test with mandatory parameters.")
    public void testListUsersWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listUsers");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        final String apiEndPoint = apiUrl + "/users";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("total_entries"), apiRestResponse.getBody().getString(
                "total_entries"));
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries").getJSONObject(
                0).getString("id"), apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries")
                .getJSONObject(0).getString("id"));
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries").getJSONObject(
                0).getString("name"), apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries")
                .getJSONObject(0).getString("name"));
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries").getJSONObject(
                0).getString("email"), apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries")
                .getJSONObject(0).getString("email"));
        
    }
    
    /**
     * Positive test case for listUsers method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {listUsers} integration test with optional parameters.")
    public void testListUsersWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listUsers");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_optional.json");
        JSONArray esbUsers = esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        final String apiEndPoint = apiUrl + "/users?page=1&per_page=2&sort_field=id&sort_direction=desc&avatar_size=48";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiUsers = apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("total_entries"), apiRestResponse.getBody().getString(
                "total_entries"));
        Assert.assertEquals(esbUsers.getJSONObject(0).getString("id"), apiUsers.getJSONObject(0).getString("id"));
        Assert.assertEquals(esbUsers.getJSONObject(0).getString("avatar"), apiUsers.getJSONObject(0)
                .getString("avatar"));
        
        // Checking whether the IDs of elements had sorted according to the sort field provided.
        final Integer esbFirstUserId = esbUsers.getJSONObject(0).getInt("id");
        final Integer esbSecondUserId = esbUsers.getJSONObject(1).getInt("id");
        final Integer apiFirstUserId = apiUsers.getJSONObject(0).getInt("id");
        Assert.assertTrue(esbFirstUserId > esbSecondUserId);
        Assert.assertEquals(esbFirstUserId, apiFirstUserId);
        
        // Checking the functionality of 'perPage' and 'page' parameters.
        Assert.assertEquals(esbUsers.length(), apiUsers.length());
        Assert.assertEquals(esbRestResponse.getBody().getInt("page"), apiRestResponse.getBody().getInt("page"));
    }
    
    /**
     * Test case: testListUsersWithNegativeCase. Status: Skipped. Reason : Cannot generate a negative response
     * for the method providing invalid parameters.
     */
    
    /**
     * Positive test case for createTopic method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {createTopic} integration test with mandatory parameters.")
    public void testCreateTopicWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createTopic");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTopic_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        connectorProperties.setProperty("topicIdMand", esbRestResponse.getBody().getString("id"));
        
        final String apiEndPoint = apiUrl + "/topics/" + connectorProperties.getProperty("topicIdMand");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getString("name"), connectorProperties
                .getProperty("topicNameMand"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
    }
    
    /**
     * Positive test case for createTopic method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {createTopic} integration test with optional parameters.")
    public void testCreateTopicWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createTopic");
        
        String topicInsupportCenter = "true";
        connectorProperties.setProperty("topicInsupportCenter", topicInsupportCenter);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTopic_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        connectorProperties.setProperty("topicIdOpt", esbRestResponse.getBody().getString("id"));
        
        final String apiEndPoint = apiUrl + "/topics/" + connectorProperties.getProperty("topicIdOpt");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().has("name"), false);
        Assert.assertEquals(esbRestResponse.getBody().has("allow_questions"), true);
        Assert.assertEquals(apiRestResponse.getBody().getString("allow_questions"), connectorProperties
                .getProperty("topicAllowQuestions"));
        Assert.assertEquals(apiRestResponse.getBody().getString("description"), connectorProperties
                .getProperty("topicDescription"));
        Assert.assertEquals(apiRestResponse.getBody().getString("in_support_center"), connectorProperties
                .getProperty("topicInsupportCenter"));
    }
    
    /**
     * Negative test case for createTopic method. Trying to create a topic without providing a name.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {createTopic} integration test with negative case.")
    public void testCreateTopicWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createTopic");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTopic_negative.json");
        
        final String apiEndPoint = apiUrl + "/topics";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createTopic_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getBody().getString("errors"), apiRestResponse.getBody()
                .getString("errors"));
    }
    
    /**
     * Positive test case for showTopic method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTopicWithMandatoryParameters" }, description = "SalesforceDesk {showTopic} integration test with mandatory parameters.")
    public void testShowTopicWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:showTopic");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_showTopic_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        final String apiEndPoint = apiUrl + "/topics/" + connectorProperties.getProperty("topicIdMand");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("updated_at"), apiRestResponse.getBody().getString(
                "updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("description"), apiRestResponse.getBody().getString(
                "description"));
        Assert.assertEquals(esbRestResponse.getBody().getString("position"), apiRestResponse.getBody().getString(
                "position"));
    }
    
    /**
     * Positive test case for showTopic method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTopicWithOptionalParameters" }, description = "SalesforceDesk {showTopic} integration test with optional parameters.")
    public void testShowTopicWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:showTopic");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_showTopic_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        final String apiEndPoint =
                apiUrl + "/topics/" + connectorProperties.getProperty("topicIdOpt")
                        + "?fields=name,description,position";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().has("id"), false);
        Assert.assertEquals(apiRestResponse.getBody().has("id"), false);
        Assert.assertEquals(esbRestResponse.getBody().has("name"), true);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().has("description"), true);
        Assert.assertEquals(esbRestResponse.getBody().getString("description"), apiRestResponse.getBody().getString(
                "description"));
        Assert.assertEquals(esbRestResponse.getBody().has("position"), true);
        Assert.assertEquals(esbRestResponse.getBody().getString("position"), apiRestResponse.getBody().getString(
                "position"));
    }
    
    /**
     * Negative test case for showTopic method. Trying to retrieve the topic providing an invalid id.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {createTopic} integration test with negative case.")
    public void testShowTopicWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:showTopic");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_showTopic_negative.json");
        
        final String apiEndPoint = apiUrl + "/topics/INVALID";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString(
                "message"));
    }
    
    /**
     * Positive test case for listTopics method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTopicWithMandatoryParameters",
            "testCreateTopicWithOptionalParameters" }, description = "SalesforceDesk {listTopics} integration test with mandatory parameters.")
    public void testListTopicsWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listTopics");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTopics_mandatory.json");
        JSONArray esbTopics = esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        final String apiEndPoint = apiUrl + "/topics";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiTopics = apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("total_entries"), apiRestResponse.getBody().getInt(
                "total_entries"));
        Assert.assertEquals(esbTopics.getJSONObject(0).getString("id"), apiTopics.getJSONObject(0).getString("id"));
        Assert.assertEquals(esbTopics.getJSONObject(0).getString("position"), apiTopics.getJSONObject(0).getString(
                "position"));
        Assert.assertEquals(esbTopics.getJSONObject(0).getString("description"), apiTopics.getJSONObject(0).getString(
                "description"));
        
    }
    
    /**
     * Positive test case for listTopics method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTopicWithMandatoryParameters",
            "testCreateTopicWithOptionalParameters" }, description = "SalesforceDesk {listTopics} integration test with optional parameters.")
    public void testListTopicsWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listTopics");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTopics_optional.json");
        
        JSONArray esbTopics = esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        final String apiEndPoint =
                apiUrl + "/topics?page=1&perPage=2&in_support_center=true&sort_field=id&sort_direction=desc";
        apiRequestHeadersMap.put("Accept-Language", "en");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiTopics = apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("total_entries"), apiRestResponse.getBody().getInt(
                "total_entries"));
        Assert.assertEquals(esbTopics.length(), esbTopics.length());
        Assert.assertEquals(esbRestResponse.getBody().getInt("page"), apiRestResponse.getBody().getInt("page"));
        Assert.assertEquals(esbTopics.getJSONObject(0).getString("id"), apiTopics.getJSONObject(0).getString("id"));
        Assert.assertEquals(esbTopics.getJSONObject(0).getString("locale"), apiTopics.getJSONObject(0).getString(
                "locale"));
        
        // Checking whether the IDs of elements had sorted according to the sort field provided.
        final Integer esbFirstTopicId = esbTopics.getJSONObject(0).getInt("id");
        final Integer esbSecondTopicId = esbTopics.getJSONObject(1).getInt("id");
        final Integer apiFirstTopicId = apiTopics.getJSONObject(0).getInt("id");
        Assert.assertTrue(esbFirstTopicId > esbSecondTopicId);
        Assert.assertEquals(esbFirstTopicId, apiFirstTopicId);
    }
    
    /**
     * Test case: testListTopicsWithNegativeCase. Status: Skipped. Reason : Cannot generate a negative
     * response for the method providing invalid values.
     */
    
    /**
     * Positive test case for createCustomer method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {createCustomer} integration test with mandatory parameters.")
    public void testCreateCustomerWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createCustomer");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomer_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        connectorProperties.setProperty("customerIdMand", esbRestResponse.getBody().getString("id"));
        
        final String apiEndPoint = apiUrl + "/customers/" + connectorProperties.getProperty("customerIdMand");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("updated_at"), apiRestResponse.getBody().getString(
                "updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("avatar"), apiRestResponse.getBody()
                .getString("avatar"));
    }
    
    /**
     * Positive test case for createCustomer method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {createCustomer} integration test with optional parameters.")
    public void testCreateCustomerWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createCustomer");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomer_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        connectorProperties.setProperty("customerIdOpt", esbRestResponse.getBody().getString("id"));
        
        final String apiEndPoint = apiUrl + "/customers/" + connectorProperties.getProperty("customerIdOpt");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("first_name"), apiRestResponse.getBody().getString(
                "first_name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("last_name"), apiRestResponse.getBody().getString(
                "last_name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("locked_until"), apiRestResponse.getBody().getString(
                "locked_until"));
        Assert.assertEquals(esbRestResponse.getBody().getString("external_id"), apiRestResponse.getBody().getString(
                "external_id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("access_private_portal"), apiRestResponse.getBody()
                .getString("access_private_portal"));
    }
    
    /**
     * Negative test case for createCustomer method. providing invalid email type.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {createCustomer} integration test with negative case.")
    public void testCreateCustomerWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createCustomer");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomer_negative.json");
        
        final String apiEndPoint = apiUrl + "/customers";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createCustomer_negative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getBody().getString("errors"), apiRestResponse.getBody()
                .getString("errors"));
    }
    
    /**
     * Positive test case for listCustomers method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {listCustomers} integration test with mandatory parameters.")
    public void testListCustomersWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listCustomers");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomers_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        JSONArray esbUsers = esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        final String apiEndPoint = apiUrl + "/customers";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiUsers = apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("total_entries"), apiRestResponse.getBody().getString(
                "total_entries"));
        Assert.assertEquals(esbUsers.length(), apiUsers.length());
        Assert.assertEquals(esbUsers.getJSONObject(0).getString("id"), apiUsers.getJSONObject(0).getString("id"));
        Assert.assertEquals(esbUsers.getJSONObject(0).getString("created_at"), apiUsers.getJSONObject(0).getString(
                "created_at"));
        Assert.assertEquals(esbUsers.getJSONObject(0).getString("updated_at"), apiUsers.getJSONObject(0).getString(
                "updated_at"));
        Assert.assertEquals(esbUsers.getJSONObject(0).getString("avatar"), apiUsers.getJSONObject(0)
                .getString("avatar"));
    }
    
    /**
     * Positive test case for listCustomers method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {listCustomers} integration test with optional parameters.")
    public void testListCustomersWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listCustomers");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomers_optional.json");
        JSONArray esbUsers = esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        String apiEndPoint =
                apiUrl + "/customers?page=1&per_page=2&fields=updated_at,company,title,avatar&avatar_size=48";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiUsers = apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("total_entries"), apiRestResponse.getBody().getString(
                "total_entries"));
        
        Assert.assertEquals(esbUsers.getJSONObject(0).getString("avatar"), apiUsers.getJSONObject(0)
                .getString("avatar"));
        
        Assert.assertEquals(esbUsers.getJSONObject(0).has("id"), false);
        Assert.assertEquals(apiUsers.getJSONObject(0).has("id"), false);
        Assert.assertEquals(esbUsers.getJSONObject(0).has("updated_at"), true);
        Assert.assertEquals(esbUsers.getJSONObject(0).getString("updated_at"), apiUsers.getJSONObject(0).getString(
                "updated_at"));
        Assert.assertEquals(esbUsers.getJSONObject(0).has("company"), true);
        Assert.assertEquals(esbUsers.getJSONObject(0).getString("company"), apiUsers.getJSONObject(0).getString(
                "company"));
        Assert.assertEquals(esbUsers.getJSONObject(0).has("title"), true);
        Assert.assertEquals(esbUsers.getJSONObject(0).getString("title"), apiUsers.getJSONObject(0).getString("title"));
        
        // Checking the functionality of 'perPage' and 'page' parameters.
        Assert.assertEquals(esbUsers.length(), apiUsers.length());
        Assert.assertEquals(esbRestResponse.getBody().getInt("page"), apiRestResponse.getBody().getInt("page"));
    }
    
    /**
     * Test case: testListCustomersWithNegativeCase. Status: Skipped. Reason : Cannot generate a negative
     * response for the method providing invalid values.
     */
    
    /**
     * Positive test case for listCasesForCompany method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {listCasesForCompany} integration test with mandatory parameters.")
    public void testListCasesForCompanyWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listCasesForCompany");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCasesForCompany_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        JSONArray esbUsers = esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        String apiEndPoint = apiUrl + "/companies/" + connectorProperties.getProperty("caseCompanyId") + "/cases";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiUsers = apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("total_entries"), apiRestResponse.getBody().getString(
                "total_entries"));
        Assert.assertEquals(esbUsers.length(), apiUsers.length());
        Assert.assertEquals(esbUsers.getJSONObject(0).getString("id"), apiUsers.getJSONObject(0).getString("id"));
        Assert.assertEquals(esbUsers.getJSONObject(0).getString("created_at"), apiUsers.getJSONObject(0).getString(
                "created_at"));
        Assert.assertEquals(esbUsers.getJSONObject(0).getString("updated_at"), apiUsers.getJSONObject(0).getString(
                "updated_at"));
        Assert.assertEquals(esbUsers.getJSONObject(0).getString("status"), apiUsers.getJSONObject(0)
                .getString("status"));
    }
    
    /**
     * Positive test case for listCasesForCompany method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {listCasesForCompany} integration test with optional parameters.")
    public void testListCasesForCompanyWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listCasesForCompany");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCasesForCompany_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        JSONArray esbUsers = esbRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        final String apiEndPoint =
                apiUrl + "/companies/" + connectorProperties.getProperty("caseCompanyId")
                        + "/cases?page=1&per_page=2&embed=customer&fields=_embedded,status,created_at&customer_id="
                        + connectorProperties.getProperty("caseCustomerId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiUsers = apiRestResponse.getBody().getJSONObject("_embedded").getJSONArray("entries");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("total_entries"), apiRestResponse.getBody().getString(
                "total_entries"));
        Assert.assertEquals(esbUsers.length(), apiUsers.length());
        
        // Checking the functionality of 'fields' parameter.
        Assert.assertEquals(esbUsers.getJSONObject(0).has("id"), false);
        Assert.assertEquals(apiUsers.getJSONObject(0).has("id"), false);
        Assert.assertEquals(esbUsers.getJSONObject(0).has("status"), true);
        Assert.assertEquals(esbUsers.getJSONObject(0).getString("status"), apiUsers.getJSONObject(0)
                .getString("status"));
        Assert.assertEquals(esbUsers.getJSONObject(0).has("created_at"), true);
        Assert.assertEquals(esbUsers.getJSONObject(0).getString("created_at"), apiUsers.getJSONObject(0).getString(
                "created_at"));
        
        // Checking the functionality of 'perPage' and 'page' parameters.
        Assert.assertEquals(esbUsers.length(), apiUsers.length());
        Assert.assertEquals(esbRestResponse.getBody().getInt("page"), apiRestResponse.getBody().getInt("page"));
        
        // Checking the functionality of 'customerId' and 'embed' parameter.
        Assert.assertEquals(esbUsers.getJSONObject(0).getJSONObject("_embedded").getJSONObject("customer").getString(
                "id"), apiUsers.getJSONObject(0).getJSONObject("_embedded").getJSONObject("customer").getString("id"));
    }
    
    /**
     * Negative test case for listCasesForCompany method. Trying to retrieve the cases providing an invalid
     * company Id.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "SalesforceDesk {listCasesForCompany} integration test with negative case.")
    public void testListCasesForCompanyNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listCasesForCompany");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCasesForCompany_negative.json");
        
        final String apiEndPoint = apiUrl + "/companies/INVALID/cases";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString(
                "message"));
    }
    
}
