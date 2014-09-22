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

package org.wso2.carbon.connector.integration.test.campaignmonitor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class CampaignmonitorConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("campaignmonitor-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("accessToken"));
    }
    
    /**
     * Positive test case for createDraftCampaign method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "campaignmonitor {createDraftCampaign} integration test with mandatory parameters")
    public void testCreateDraftCampaignWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createDraftCampaign");
        
        // Creating a unique name by appending the date string
        connectorProperties.put("campaignName", "Test Name " + new Date().toString());
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDraftCampaign_mandatory.json");
        
        // Adding the returned campaign id to property file
        String campaignId = esbRestResponse.getBody().get("string").toString();
        connectorProperties.put("campaignId", campaignId);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.1/campaigns/"
                        + connectorProperties.getProperty("campaignId") + "/summary.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertTrue(apiRestResponse.getBody().has("Recipients"));
        Assert.assertTrue(apiRestResponse.getBody().has("TotalOpened"));
        Assert.assertTrue(apiRestResponse.getBody().has("Clicks"));
    }
    
    /**
     * Positive test case for createDraftCampaign method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDraftCampaignWithMandatoryParameters" }, description = "campaignmonitor {createDraftCampaign} integration test with optional parameters")
    public void testCreateDraftCampaignWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createDraftCampaign");
        
        // Creating a unique name by appending the date string
        connectorProperties.put("campaignName", "Test Name Optional " + new Date().toString());
        
        // Retrieving the segmentId
        String apiEndPointForSegments =
                connectorProperties.getProperty("apiUrl") + "/api/v3.1/lists/"
                        + connectorProperties.getProperty("listId") + "/segments.json";
        RestResponse<JSONObject> apiRestResponseForSegments =
                sendJsonRestRequest(apiEndPointForSegments, "GET", apiRequestHeadersMap);
        JSONArray segmentArray = new JSONArray(apiRestResponseForSegments.getBody().get("output").toString());
        connectorProperties.put("segmentId", segmentArray.getJSONObject(0).get("SegmentID").toString());
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDraftCampaig_optional.json");
        
        // Adding the returned campaign id to property file
        String campaignId = esbRestResponse.getBody().get("string").toString();
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.1/campaigns/"
                        + campaignId + "/summary.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertTrue(apiRestResponse.getBody().has("Recipients"));
        Assert.assertTrue(apiRestResponse.getBody().has("TotalOpened"));
        Assert.assertTrue(apiRestResponse.getBody().has("Clicks"));
    }
    
    /**
     * Negative test case for createDraftCampaign method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDraftCampaignWithOptionalParameters" }, description = "campaignmonitor {createDraftCampaign} integration test negative case")
    public void testCreateDraftCampaignNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createDraftCampaign");
        // Creating a unique name by appending the date string
        connectorProperties.put("campaignName", "Test Name Negative " + new Date().toString());
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDraftCampain_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.1/campaigns/"
                        + connectorProperties.getProperty("clientId") + ".json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createDraftCampaign_optional.json");
     
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("Result").get("Message").toString(),
                apiRestResponse.getBody().get("Message").toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("Result").get("Code").toString(), apiRestResponse.getBody().get("Code")
                .toString());
        
    }
    
    /**
     * Positive test case for sendCampaign method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDraftCampaignNegativeCase" }, description = "campaignmonitor {sendCampaign} integration test with mandatory parameters")
    public void testSendCampaignWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:sendCampaign");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendCampaign_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.1/campaigns/"
                        + connectorProperties.getProperty("campaignId") + "/send.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_sendCampaign_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertTrue(apiRestResponse.getBody().has("Code"));
        Assert.assertTrue(apiRestResponse.getBody().has("Message"));
        
    }
    
    /**
     * Negative test case for sendCampaign method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSendCampaignWithMandatoryParameters" }, description = "campaignmonitor {sendCampaign} integration test negative case")
    public void testSendCampaignNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:sendCampaign");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendCampaign_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.1/campaigns/"
                        + connectorProperties.getProperty("campaignId") + "/send.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_sendCampaign_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getBody().get("Code").toString(), esbRestResponse.getBody().get("Code")
                .toString());
        Assert.assertEquals(apiRestResponse.getBody().get("Message").toString(),
                esbRestResponse.getBody().get("Message").toString());
        
    }
    
    /**
     * Positive test case for listClients method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSendCampaignNegativeCase" }, description = "campaignmonitor {listClients} integration test with mandatory parameters")
    public void testListClientsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listClients");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listClients_mandatory.json");
        
        JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().get("output").toString());
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.1/clients.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().get("output").toString());
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        if (esbResponseArray.length() > 0) {
            Assert.assertEquals(esbResponseArray.getJSONObject(0).get("ClientID").toString(), apiResponseArray
                    .getJSONObject(0).get("ClientID").toString());
            Assert.assertEquals(esbResponseArray.getJSONObject(0).get("Name").toString(), apiResponseArray
                    .getJSONObject(0).get("Name").toString());
        }
    }
    
    /**
     * Positive test case for listEmailCampaignClickers method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListClientsWithMandatoryParameters" }, description = "campaignmonitor {listEmailCampaignClickers} integration test with mandatory parameters")
    public void testListEmailCampaignClickersWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listEmailCampaignClickers");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_listEmailCampaignClickers_mandatory.json");
        
        JSONArray esbResultsArray = esbRestResponse.getBody().getJSONArray("Results");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.1/campaigns/"
                        + connectorProperties.getProperty("campaignId") + "/clicks.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiResultsArray = apiRestResponse.getBody().getJSONArray("Results");
        
        Assert.assertEquals(esbRestResponse.getBody().get("TotalNumberOfRecords").toString(), apiRestResponse.getBody()
                .get("TotalNumberOfRecords").toString());
        Assert.assertEquals(esbResultsArray.length(), apiResultsArray.length());
        if (esbResultsArray.length() > 0) {
            Assert.assertEquals(esbResultsArray.getJSONObject(0).get("EmailAddress").toString(), apiResultsArray
                    .getJSONObject(0).get("EmailAddress").toString());
            Assert.assertEquals(esbResultsArray.getJSONObject(0).get("ListID").toString(), apiResultsArray
                    .getJSONObject(0).get("ListID").toString());
        }
    }
    
    /**
     * Positive test case for listEmailCampaignClickers method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListEmailCampaignClickersWithMandatoryParameters" }, description = "campaignmonitor {listEmailCampaignClickers} integration test with optional parameters")
    public void testListEmailCampaignClickersWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listEmailCampaignClickers");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_listEmailCampaignClickers_optional.json");
        JSONArray esbResultsArray = esbRestResponse.getBody().getJSONArray("Results");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.1/campaigns/"
                + connectorProperties.getProperty("campaignId") + "/clicks.json?pagesize=100";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResultsArray = apiRestResponse.getBody().getJSONArray("Results");
        
        Assert.assertEquals(esbRestResponse.getBody().get("TotalNumberOfRecords").toString(), apiRestResponse.getBody()
                .get("TotalNumberOfRecords").toString());      
        Assert.assertEquals(esbResultsArray.length(), apiResultsArray.length());
        if (esbResultsArray.length() > 0) {
            Assert.assertEquals(esbResultsArray.getJSONObject(0).get("EmailAddress").toString(), apiResultsArray
                    .getJSONObject(0).get("EmailAddress").toString());
            Assert.assertEquals(esbResultsArray.getJSONObject(0).get("ListID").toString(), apiResultsArray
                    .getJSONObject(0).get("ListID").toString());
        }
    }
    
    /**
     * Negative test case for listEmailCampaignClickers method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListEmailCampaignClickersWithOptionalParameters" }, description = "campaignmonitor {listEmailCampaignClickers} integration test with negative case")
    public void testListEmailCampaignClickersWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listEmailCampaignClickers");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_listEmailCampaignClickers_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.1/campaigns/"
                        + connectorProperties.getProperty("campaignId") + "/clicks.json?pagesize=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().get("Code").toString(), apiRestResponse.getBody().get("Code")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().get("Message").toString(),
                apiRestResponse.getBody().get("Message").toString());
    }
    
    /**
     * Positive test case for listEmailCampaignOpeners method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListEmailCampaignClickersWithNegativeCase" }, description = "campaignmonitor {listEmailCampaignOpeners} integration test with mandatory parameters")
    public void testListEmailCampaignOpenersWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listEmailCampaignOpeners");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_listEmailCampaignOpeners_mandatory.json");
        
        JSONArray esbResultsArray = esbRestResponse.getBody().getJSONArray("Results");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.1/campaigns/"
                        + connectorProperties.getProperty("campaignId") + "/opens.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiResultsArray = apiRestResponse.getBody().getJSONArray("Results");
        
        Assert.assertEquals(esbRestResponse.getBody().get("TotalNumberOfRecords").toString(), apiRestResponse.getBody()
                .get("TotalNumberOfRecords").toString());
        Assert.assertEquals(esbResultsArray.length(), apiResultsArray.length());
        if (esbResultsArray.length() > 0) {
            Assert.assertEquals(esbResultsArray.getJSONObject(0).get("EmailAddress").toString(), apiResultsArray
                    .getJSONObject(0).get("EmailAddress").toString());
            Assert.assertEquals(esbResultsArray.getJSONObject(0).get("ListID").toString(), apiResultsArray
                    .getJSONObject(0).get("ListID").toString());
        }
    }
    
    /**
     * Positive test case for listEmailCampaignOpeners method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListEmailCampaignOpenersWithMandatoryParameters" }, description = "campaignmonitor {listEmailCampaignOpeners} integration test with optional parameters")
    public void testListEmailCampaignOpenersWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listEmailCampaignOpeners");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_listEmailCampaignOpeners_optional.json");
        
        Assert.assertEquals(esbRestResponse.getBody().get("ResultsOrderedBy").toString(), "list");
        Assert.assertEquals(esbRestResponse.getBody().get("OrderDirection").toString(), "desc");
        Assert.assertEquals(esbRestResponse.getBody().get("PageSize").toString(), "100");
    }
    
    /**
     * Negative test case for listEmailCampaignOpeners method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListEmailCampaignOpenersWithOptionalParameters" }, description = "campaignmonitor {listEmailCampaignOpeners} integration test with negative case")
    public void testListEmailCampaignOpenersWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listEmailCampaignOpeners");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_listEmailCampaignOpeners_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.1/campaigns/"
                        + connectorProperties.getProperty("campaignId") + "/opens.json?pagesize=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().get("Code").toString(), apiRestResponse.getBody().get("Code")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().get("Message").toString(),
                apiRestResponse.getBody().get("Message").toString());
    }
    
    /**
     * Positive test case for listCampaigns method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListEmailCampaignOpenersWithNegativeCase" }, description = "campaignmonitor {listCampaigns} integration test with mandatory parameters")
    public void testListCampaignsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listCampaigns");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCampaigns_mandatory.json");
        
        JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().get("output").toString());
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.1/clients/"
                        + connectorProperties.getProperty("clientId") + "/drafts.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().get("output").toString());
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        if (esbResponseArray.length() > 0) {
            Assert.assertEquals(esbResponseArray.getJSONObject(0).get("CampaignID").toString(), apiResponseArray
                    .getJSONObject(0).get("CampaignID").toString());
            Assert.assertEquals(esbResponseArray.getJSONObject(0).get("Name").toString(), apiResponseArray
                    .getJSONObject(0).get("Name").toString());
        }
    }
    
    /**
     * Negative test case for listCampaigns method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListCampaignsWithMandatoryParameters" }, description = "campaignmonitor {listCampaigns} integration test with negative case")
    public void testListCampaignsWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listCampaigns");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCampaigns_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.1/clients/-/drafts.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 401);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 401);
        Assert.assertEquals(esbRestResponse.getBody().get("Code").toString(), apiRestResponse.getBody().get("Code")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().get("Message").toString(),
                apiRestResponse.getBody().get("Message").toString());
    }
    
    /**
     * Positive test case for listSubscriberLists method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListCampaignsWithNegativeCase" }, description = "campaignmonitor {listSubscriberLists} integration test with mandatory parameters")
    public void testListSubscriberListsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listSubscriberLists");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSubscriberLists_mandatory.json");
        
        JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().get("output").toString());
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v3.1/clients/"
                        + connectorProperties.getProperty("clientId") + "/lists.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().get("output").toString());
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        if (esbResponseArray.length() > 0) {
            Assert.assertEquals(esbResponseArray.getJSONObject(0).get("ListID").toString(), apiResponseArray
                    .getJSONObject(0).get("ListID").toString());
            Assert.assertEquals(esbResponseArray.getJSONObject(0).get("Name").toString(), apiResponseArray
                    .getJSONObject(0).get("Name").toString());
        }
    }
    
    /**
     * Negative test case for listSubscriberLists method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListSubscriberListsWithMandatoryParameters" }, description = "campaignmonitor {listSubscriberLists} integration test with negative case")
    public void testListSubscriberListsWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listSubscriberLists");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSubscriberLists_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v3.1/clients/-/lists.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 401);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 401);
        Assert.assertEquals(esbRestResponse.getBody().get("Code").toString(), apiRestResponse.getBody().get("Code")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().get("Message").toString(),
                apiRestResponse.getBody().get("Message").toString());
    }
}
