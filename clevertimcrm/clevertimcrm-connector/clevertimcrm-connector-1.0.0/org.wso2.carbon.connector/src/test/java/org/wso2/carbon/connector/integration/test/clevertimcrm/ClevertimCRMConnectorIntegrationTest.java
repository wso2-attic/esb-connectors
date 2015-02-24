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

package org.wso2.carbon.connector.integration.test.clevertimcrm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;
import org.apache.axiom.om.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ClevertimCRMConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("clevertimcrm-connector-1.0.0");
        
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        // Create base64-encoded auth string using apiKey and password
        final String authString = connectorProperties.getProperty("apiKey") + ":X";
        final String base64AuthString = Base64.encode(authString.getBytes());
        
        apiRequestHeadersMap.put("Authorization", "Basic " + base64AuthString);
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
    }
    
    /**
     * Positive test case for createCase method with mandatory parameters.
     */
    @Test(priority = 1, description = "clevertimcrm {createCase} integration test with mandatory parameters.")
    public void testCreateCaseWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCase");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCase_mandatory.json");
        System.out.println("response---"+esbRestResponse.getBody());
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("content");
        String caseId = esbResponseArray.getJSONObject(0).getString("id");
        
        connectorProperties.put("caseId", caseId);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/case/" + caseId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertEquals(connectorProperties.getProperty("caseName"),
                apiResponseArray.getJSONObject(0).getString("name"));
        
    }
    
    /**
     * Positive test case for createCase method with optional parameters.
     */
    @Test(priority = 1, description = "clevertimcrm {createCase} integration test with optional parameters.")
    public void testCreateCaseWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCase");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCase_optional.json");
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("content");
        String caseId = esbResponseArray.getJSONObject(0).getString("id");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/case/" + caseId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertEquals(connectorProperties.getProperty("caseName"),
                apiResponseArray.getJSONObject(0).getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("description"), apiResponseArray.getJSONObject(0)
                .getString("description"));
        
    }
    
    /**
     * Negative test case for createCase method.
     */
    @Test(priority = 1, description = "clevertimcrm {createCase} integration test negative case.")
    public void testCreateCaseWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCase");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCase_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/case";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createCase_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for listCases method with mandatory parameters.
     */
    @Test(priority = 1, description = "clevertimcrm {listCases} integration test with mandatory parameters.")
    public void testListCasesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCases");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCases_mandatory.json");
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("content");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/case";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        
        if (esbResponseArray.length() > 0 && apiResponseArray.length() > 0) {
            
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                    .getString("id"));
            
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("name"), apiResponseArray.getJSONObject(0)
                    .getString("name"));
        }
    }
    
    /**
     * Positive test case for updateCase method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateCaseWithMandatoryParameters" }, description = "clevertimcrm {updateCase} integration test with optional parameters.")
    public void testUpdateCaseWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateCase");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/case/" + connectorProperties.getProperty("caseId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        String originalName = apiResponseArray.getJSONObject(0).getString("name");
        String originalDesc = apiResponseArray.getJSONObject(0).getString("description");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCase_optional.json");
        
        apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertNotEquals(originalName, apiResponseArray.getJSONObject(0).getString("name"));
        Assert.assertNotEquals(originalDesc, apiResponseArray.getJSONObject(0).getString("description"));
        
    }
    
    /**
     * Negative test case for updateCase method.
     */
    @Test(priority = 1, description = "clevertimcrm {updateCase} integration test negative case.")
    public void testUpdateCaseWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateCase");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCase_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/case/0";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateCase_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for createContact method with mandatory parameters.
     */
    @Test(priority = 1, description = "clevertimcrm {createContact} integration test with mandatory parameters.")
    public void testCreateContactWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createContact");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_mandatory.json");
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("content");
        String contactId = esbResponseArray.getJSONObject(0).getString("id");
        
        connectorProperties.put("contactId", contactId);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/contact/" + contactId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertEquals(connectorProperties.getProperty("contactFirstName"), apiResponseArray.getJSONObject(0)
                .getString("fn"));
        
    }
    
    /**
     * Positive test case for createContact method with optional parameters.
     */
    @Test(priority = 1, description = "clevertimcrm {createContact} integration test with optional parameters.")
    public void testCreateContactWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createContact");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_optional.json");
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("content");
        String contactId = esbResponseArray.getJSONObject(0).getString("id");
        connectorProperties.put("contactIdOpt", contactId);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/contact/" + contactId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertEquals(connectorProperties.getProperty("contactFirstName"), apiResponseArray.getJSONObject(0)
                .getString("fn"));
        Assert.assertEquals(connectorProperties.getProperty("contactLastName"), apiResponseArray.getJSONObject(0)
                .getString("ln"));
        Assert.assertEquals(connectorProperties.getProperty("contactTitle"), apiResponseArray.getJSONObject(0)
                .getString("title"));
        
    }
    
    /**
     * Negative test case for createContact method.
     */
    @Test(priority = 1, description = "clevertimcrm {createContact} integration test negative case.")
    public void testCreateContactWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createContact");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/contact";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createContact_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for listContacts method with mandatory parameters.
     */
    @Test(priority = 1, description = "clevertimcrm {listContacts} integration test with mandatory parameters.")
    public void testListContactsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_mandatory.json");
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("content");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/contact";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        if (esbResponseArray.length() > 0 && apiResponseArray.length() > 0) {
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                    .getString("id"));
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("is_company"), apiResponseArray
                    .getJSONObject(0).getString("is_company"));
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("fn"), apiResponseArray.getJSONObject(0)
                    .getString("fn"));
            
        }
    }
    
    /**
     * Positive test case for createNote method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateContactWithMandatoryParameters" }, description = "clevertimcrm {createNote} integration test with mandatory parameters.")
    public void testCreateNoteWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createNote");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNote_mandatory.json");
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("content");
        String noteId = esbResponseArray.getJSONObject(0).getString("id");
        
        connectorProperties.put("noteId", noteId);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/note/" + noteId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertEquals(connectorProperties.getProperty("contactId"), apiResponseArray.getJSONObject(0)
                .getJSONArray("cust").getString(0));
        Assert.assertEquals(connectorProperties.getProperty("description"), apiResponseArray.getJSONObject(0)
                .getString("desc"));
        
    }
    
    /**
     * Positive test case for createNote method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateCaseWithMandatoryParameters",
            "testCreateContactWithMandatoryParameters" }, description = "clevertimcrm {createNote} integration test with optional parameters.")
    public void testCreateNoteWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createNote");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNote_optional.json");
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("content");
        String noteId = esbResponseArray.getJSONObject(0).getString("id");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/note/" + noteId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertEquals(connectorProperties.getProperty("contactId"), apiResponseArray.getJSONObject(0)
                .getJSONArray("cust").getString(0));
        Assert.assertEquals(connectorProperties.getProperty("caseId"),
                apiResponseArray.getJSONObject(0).getString("case"));
        Assert.assertEquals(connectorProperties.getProperty("description"), apiResponseArray.getJSONObject(0)
                .getString("desc"));
        
    }
    
    /**
     * Negative test case for createNote method.
     */
    @Test(priority = 1, description = "clevertimcrm {createNote} integration test negative case.")
    public void testCreateNoteWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createNote");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNote_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/note";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createNote_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for updateNote method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateNoteWithMandatoryParameters",
            "testCreateContactWithOptionalParameters" }, description = "clevertimcrm {updateNote} integration test with optional parameters.")
    public void testUpdateNoteWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateNote");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/note/" + connectorProperties.getProperty("noteId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        String originalCustomer = apiResponseArray.getJSONObject(0).getJSONArray("cust").getString(0);
        String originalDesc = apiResponseArray.getJSONObject(0).getString("desc");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateNote_optional.json");
        
        apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertNotEquals(originalCustomer, apiResponseArray.getJSONObject(0).getJSONArray("cust").getString(0));
        Assert.assertNotEquals(originalDesc, apiResponseArray.getJSONObject(0).getString("desc"));
    }
    
    /**
     * Negative test case for updateNote method.
     */
    @Test(priority = 1, description = "clevertimcrm {updateNote} integration test negative case.")
    public void testUpdateNoteWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateNote");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateNote_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/note/0";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateNote_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for listNotes method with mandatory parameters.
     */
    @Test(priority = 1, description = "clevertimcrm {listNotes} integration test with mandatory parameters.")
    public void testListNotesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listNotes");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listNotes_mandatory.json");
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("content");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/note";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        
        if (esbResponseArray.length() < 0) {
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                    .getString("id"));
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("desc"), apiResponseArray.getJSONObject(0)
                    .getString("desc"));
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("case"), apiResponseArray.getJSONObject(0)
                    .getString("case"));
        }
    }
    
    /**
     * Positive test case for listComments method with mandatory parameters.
     */
    @Test(priority = 1, description = "clevertimcrm {listComments} integration test with mandatory parameters.")
    public void testListCommentsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listComments");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listComments_mandatory.json");
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("content");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/comment";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        
        if (esbResponseArray.length() > 0 && apiResponseArray.length() > 0) {
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                    .getString("id"));
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("comment"),
                    apiResponseArray.getJSONObject(0).getString("comment"));
        }
        
    }
    
    /**
     * Positive test case for createComment method with mandatory parameters.
     */
    @Test(priority = 1, description = "clevertimcrm {createComment} integration test with mandatory parameters.")
    public void testCreateCommentsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createComment");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_mandatory.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("content");
        String commentId = esbResponseArray.getJSONObject(0).getString("id");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/comment/" + commentId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("nid"),
                connectorProperties.getProperty("noteId"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("comment"),
                connectorProperties.getProperty("comment"));
        
    }
    
    /**
     * Negative test case for createComment method.
     */
    @Test(priority = 1, description = "clevertimcrm {createComment} integration test with negative case.")
    public void testCreateCommentsWithNegative() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createComment");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/comment";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createComment_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listOpportunities method with mandatory parameters.
     */
    @Test(priority = 1, description = "clevertimcrm {listOpportunities} integration test with mandatory parameters.")
    public void testListOpportunitiesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listOpportunities");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listOpportunities_mandatory.json");
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("content");
        System.out.println("dhfjbd"+esbRestResponse.getBody());
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/opportunity";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        
        if (esbResponseArray.length() > 0 && apiResponseArray.length() > 0) {
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                    .getString("id"));
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("name"), apiResponseArray.getJSONObject(0)
                    .getString("name"));
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("lm"), apiResponseArray
                    .getJSONObject(0).getString("lm"));
        }
        
    }
    
    /**
     * Positive test case for createOpportunity method with mandatory parameters.
     */
    @Test(priority = 1, description = "clevertimcrm {createOpportunity} integration test with mandatory parameters.")
    public void testCreateOpportunityWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createOpportunity");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createOpportunity_mandatory.json");
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("content");
        String opportunityId = esbResponseArray.getJSONObject(0).getString("id");
        connectorProperties.put("opportunityId", opportunityId);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/opportunity/" + opportunityId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("name"),
                connectorProperties.getProperty("opportunityName"));
        
    }
    
    /**
     * Positive test case for createOpportunity method with optional parameters.
     */
    @Test(priority = 1, description = "clevertimcrm {createOpportunity} integration test with optional parameters.")
    public void testCreateOpportunityWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createOpportunity");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createOpportunity_optional.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("content");
        String opportunityId = esbResponseArray.getJSONObject(0).getString("id");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/opportunity/" + opportunityId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("name"),
                connectorProperties.getProperty("opportunityName"));
        
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("description"),
                connectorProperties.getProperty("opportunityDesc"));
        
    }
    
    /**
     * Negative test case for createOpportunity method.
     */
    @Test(priority = 1, description = "clevertimcrm {createOpportunity} integration test with negative case.")
    public void testCreateOpportunityWithNegative() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createOpportunity");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createOpportunity_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/opportunity";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createOpportunity_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for updateOpportunity method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateOpportunityWithMandatoryParameters" }, description = "clevertimcrm {updateOpportunity} integration test with optional parameters.")
    public void testUpdateOpportunityWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateOpportunity");
        
        String opportunityId = connectorProperties.getProperty("opportunityId");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/opportunity/" + opportunityId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        String originalName = apiResponseArray.getJSONObject(0).getString("name");
        String originalDesc = apiResponseArray.getJSONObject(0).getString("description");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateOpportunity_optional.json");
        
        apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertNotEquals(originalName, apiResponseArray.getJSONObject(0).getString("name"));
        Assert.assertNotEquals(originalDesc, apiResponseArray.getJSONObject(0).getString("description"));
        
    }
    
    /**
     * Negative test case for updateOpportunity method.
     */
    @Test(priority = 1, description = "clevertimcrm {updateOpportunity} integration test with negative case.")
    public void testUpdateOpportunityWithNegative() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateOpportunity");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateOpportunity_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/opportunity/00";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateOpportunity_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for createTask method with mandatory parameters.
     */
    @Test(priority = 1, description = "clevertimcrm {createTask} integration test with mandatory parameters.")
    public void testCreateTaskWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTask");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_mandatory.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("content");
        String taskId = esbResponseArray.getJSONObject(0).getString("id");
        
        connectorProperties.put("taskId", taskId);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/task/" + taskId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertEquals(connectorProperties.getProperty("taskName"),
                apiResponseArray.getJSONObject(0).getString("name"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("location"), "");
    }
    
    /**
     * Positive test case for createTask method with optional parameters.
     */
    @Test(priority = 1, description = "clevertimcrm {createTask} integration test with optional parameters.")
    public void testCreateTaskWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTask");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_optional.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("content");
        String taskId = esbResponseArray.getJSONObject(0).getString("id");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/task/" + taskId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("name"),
                connectorProperties.getProperty("taskName"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("location"),
                connectorProperties.getProperty("taskLocation"));
    }
    
    /**
     * Negative test case for createTask method.
     */
    @Test(priority = 1, description = "clevertimcrm {createTask} integration test negative case.")
    public void testCreateTaskWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTask");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/task/";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createTask_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for updateTask method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateTaskWithMandatoryParameters" }, description = "clevertimcrm {updateTask} integration test with optional parameters.")
    public void testUpdateTaskWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateTask");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/task/" + connectorProperties.getProperty("taskId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        String originalTaskName = apiResponseArray.getJSONObject(0).getString("name");
        String originalLocation = apiResponseArray.getJSONObject(0).getString("location");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTask_optional.json");
        
        apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertNotEquals(originalTaskName, apiResponseArray.getJSONObject(0).getString("name"));
        Assert.assertNotEquals(originalLocation, apiResponseArray.getJSONObject(0).getString("location"));
        
    }
    
    /**
     * Negative test case for updateTask method.
     */
    @Test(priority = 1, description = "clevertimcrm {updateTask} integration test negative case.")
    public void testUpdateTaskWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateTask");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTask_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/task/0";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateTask_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
        
    }
    
    /**
     * Positive test case for listTasks method with mandatory parameters.
     */
    @Test(priority = 1, description = "clevertimcrm {listTasks} integration test with mandatory parameters.")
    public void testListTaskssWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTasks");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_mandatory.json");
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("content");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/task";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        
        if (esbResponseArray.length() > 0 && apiResponseArray.length() > 0) {
            
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("name"), apiResponseArray.getJSONObject(0)
                    .getString("name"));
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("is_deleted"), apiResponseArray
                    .getJSONObject(0).getString("is_deleted"));
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("location"), apiResponseArray
                    .getJSONObject(0).getString("location"));
            
        }else{
            Assert.fail("No Tasks found.");
        }
    }
    
    /**
     * Positive test case for createCompany method with mandatory parameters.
     */
    @Test(priority = 1, description = "clevertimcrm {createCompany} integration test with mandatory parameters.")
    public void testCreateComapnyWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCompany");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCompany_mandatory.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("content");
        String companyId = esbResponseArray.getJSONObject(0).getString("id");
        
        connectorProperties.put("companyId", companyId);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/company/" + companyId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertEquals(connectorProperties.getProperty("companyName"),
                apiResponseArray.getJSONObject(0).getString("cn"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("lm"), esbResponseArray.getJSONObject(0).getString("lm"));
    }
    
    /**
     * Positive test case for createCompany method with optional parameters.
     */
    @Test(priority = 1, description = "clevertimcrm {createCompany} integration test with optional parameters.")
    public void testCreateComapnyWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCompany");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCompany_optional.json");
        System.out.println("dfbkjfggh"+esbRestResponse.getBody());
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("content");
        String companyId = esbResponseArray.getJSONObject(0).getString("id");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/company/" + companyId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertEquals(connectorProperties.getProperty("companyName"),
                apiResponseArray.getJSONObject(0).getString("cn"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("description"), esbResponseArray.getJSONObject(0).getString("description"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("city"), esbResponseArray.getJSONObject(0).getString("city"));
    }
    
    /**
     * Negative test case for createCompany method.
     */
    @Test(priority = 1, description = "clevertimcrm {createCompany} integration test negative case.")
    public void testCreateCompanyWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCompany");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCompany_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/company/";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createCompany_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for listCompanies method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateComapnyWithMandatoryParameters","testCreateComapnyWithOptionalParameters" },priority = 1, description = "clevertimcrm {listCompanies} integration test with mandatory parameters.")
    public void testListCompaniesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCompanies");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCompanies_mandatory.json");
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("content");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/company";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        
        if (esbResponseArray.length() > 0 && apiResponseArray.length() > 0) {
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                    .getString("id"));
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("cn"), apiResponseArray.getJSONObject(0)
                    .getString("cn"));
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("lm"), apiResponseArray
                    .getJSONObject(0).getString("lm"));
        }else{
            Assert.fail("No Companies found.");
        }
        
    }
    
    /**
     * Positive test case for getCompany method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateComapnyWithMandatoryParameters" },priority = 1, description = "clevertimcrm {getCompany} integration test with mandatory parameters.")
    public void testGetCompanyWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getCompany");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCompany_mandatory.json");
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("content");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/company/"+connectorProperties.getProperty("companyId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("content");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        
        if (esbResponseArray.length() ==1 && apiResponseArray.length() ==1) {
            Assert.assertEquals(connectorProperties.getProperty("companyId"), apiResponseArray.getJSONObject(0)
                    .getString("id"));
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("cn"), apiResponseArray.getJSONObject(0)
                    .getString("cn"));
            Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("lm"), apiResponseArray
                    .getJSONObject(0).getString("lm"));
        }else{
            Assert.fail("No Company found for given id.");
        }
        
    }
}
