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

package org.wso2.carbon.connector.integration.test.surveygizmo;

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

public class SurveygizmoConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    private Map<String, String> parametersMap = new HashMap<String, String>();
    private String authParam;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init("surveygizmo-connector-1.0.0");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");
        authParam =
                "user:md5=" + connectorProperties.getProperty("userName") + ":"
                        + connectorProperties.getProperty("password");
    }
    
    /**
     * Test createSurvey method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "surveygizmo {createSurvey} integration test with mandatory parameters.")
    public void testCreateSurveyWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createSurvey");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createSurvey_mandatory.json");
        String surveyId = esbRestResponse.getBody().getJSONObject("data").getString("id");
        connectorProperties.put("surveyId", surveyId);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v4/survey/" + surveyId + "?" + authParam;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("title"),
                connectorProperties.getProperty("surveyTitle"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("_type").toLowerCase(),
                connectorProperties.getProperty("surveyType"));
    }
    
    /**
     * Test createSurvey method with Optional Parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "surveygizmo {createSurvey} integration test with optional parameters.")
    public void testCreateSurveyWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createSurvey");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createSurvey_optional.json");
        String surveyId = esbRestResponse.getBody().getJSONObject("data").getString("id");
        connectorProperties.put("surveyIdOptional", surveyId);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v4/survey/" + surveyId + "?" + authParam;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("title"),
                connectorProperties.getProperty("surveyTitle"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("_type").toLowerCase(),
                connectorProperties.getProperty("surveyType"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("internal_title"),
                connectorProperties.getProperty("surveyInternalTitle"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("theme"),
                connectorProperties.getProperty("surveyTheme"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("status"),
                connectorProperties.getProperty("surveyStatus"));
    }
    
    /**
     * Test createSurvey method with Negative Case.
     */
    @Test(groups = { "wso2.esb" }, description = "surveygizmo {createSurvey} integration test with negative case.")
    public void testCreateSurveyWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createSurvey");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createSurvey_negative.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v4/survey?" + authParam;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_createSurvey_negative.json");
        Assert.assertEquals(apiRestResponse.getBody().getString("result_ok"),
                esbRestResponse.getBody().getString("result_ok"));
        Assert.assertEquals(apiRestResponse.getBody().getString("code"), esbRestResponse.getBody().getString("code"));
        Assert.assertEquals(apiRestResponse.getBody().getString("message"),
                esbRestResponse.getBody().getString("message"));
    }
    
    /**
     * Test updateSurvey method with Optional Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateSurveyWithOptionalParameters" }, description = "surveygizmo {updateSurvey} integration test with optional parameters.")
    public void testUpdateSurveyWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateSurvey");
        String surveyId = connectorProperties.getProperty("surveyIdOptional");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v4/survey/" + surveyId + "?" + authParam;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        String originalInternalTitle = apiRestResponse.getBody().getJSONObject("data").getString("internal_title");
        String originalStatus = apiRestResponse.getBody().getJSONObject("data").getString("status");
        String originalTheme = apiRestResponse.getBody().getJSONObject("data").getString("theme");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateSurvey_optional.json");
        apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        String updatedInternalTitle = apiRestResponse.getBody().getJSONObject("data").getString("internal_title");
        String updatedStatus = apiRestResponse.getBody().getJSONObject("data").getString("status");
        String updatedTheme = apiRestResponse.getBody().getJSONObject("data").getString("theme");
        Assert.assertNotEquals(originalInternalTitle, updatedInternalTitle);
        Assert.assertNotEquals(originalStatus, updatedStatus);
        Assert.assertNotEquals(originalTheme, updatedTheme);
    }
    
    /**
     * Test updateSurvey method with Negative Case.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateSurveyWithOptionalParameters" }, description = "surveygizmo {updateSurvey} integration test with negative case.")
    public void testUpdateSurveyWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateSurvey");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateSurvey_negative.json");
        
        String surveyId = connectorProperties.getProperty("surveyIdOptional");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v4/survey/" + surveyId + "?" + authParam;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateSurvey_negative.json");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("result_ok"),
                esbRestResponse.getBody().getString("result_ok"));
        Assert.assertEquals(apiRestResponse.getBody().getString("code"), esbRestResponse.getBody().getString("code"));
        Assert.assertEquals(apiRestResponse.getBody().getString("message"),
                esbRestResponse.getBody().getString("message"));
    }
    
    /**
     * Test getSurveyById method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateSurveyWithMandatoryParameters" }, description = "surveygizmo {getSurveyById} integration test with mandatory parameters.")
    public void testGetSurveyByIdWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getSurveyById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSurveyById_mandatory.json");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v4/survey/" + connectorProperties.getProperty("surveyId")
                        + "?" + authParam;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("id"), esbRestResponse.getBody()
                .getJSONObject("data").getString("id"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("status"), esbRestResponse
                .getBody().getJSONObject("data").getString("status"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("title"), esbRestResponse
                .getBody().getJSONObject("data").getString("title"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("created_on"), esbRestResponse
                .getBody().getJSONObject("data").getString("created_on"));
        
    }
    
    /**
     * Test getSurveyById method with optional Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateSurveyWithMandatoryParameters" }, description = "surveygizmo {getSurveyById} integration test with optional parameters.")
    public void testGetSurveyByIdWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getSurveyById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSurveyById_optional.json");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v4/survey/" + connectorProperties.getProperty("surveyId")
                        + "?metaonly=true&" + authParam;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().has("pages"), esbRestResponse.getBody().has("pages"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("id"), esbRestResponse.getBody()
                .getJSONObject("data").getString("id"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("status"), esbRestResponse
                .getBody().getJSONObject("data").getString("status"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("title"), esbRestResponse
                .getBody().getJSONObject("data").getString("title"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("created_on"), esbRestResponse
                .getBody().getJSONObject("data").getString("created_on"));
    }
    
    /**
     * Test listSurveys method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateSurveyWithMandatoryParameters",
            "testCreateSurveyWithOptionalParameters" }, description = "surveygizmo {listSurveys} integration test with mandatory parameters.")
    public void testListSurveysWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listSurveys");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSurveys_mandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v4/survey?" + authParam;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray esbDataArray = esbRestResponse.getBody().getJSONArray("data");
        JSONArray apiDataArray = apiRestResponse.getBody().getJSONArray("data");
        Assert.assertEquals(apiRestResponse.getBody().getString("total_count"),
                esbRestResponse.getBody().getString("total_count"));
        Assert.assertEquals(apiDataArray.getJSONObject(0).getString("id"), esbDataArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(apiDataArray.getJSONObject(0).getString("status"),
                esbDataArray.getJSONObject(0).getString("status"));
        Assert.assertEquals(apiDataArray.getJSONObject(0).getString("title"),
                esbDataArray.getJSONObject(0).getString("title"));
        Assert.assertEquals(apiDataArray.getJSONObject(0).getString("created_on"), esbDataArray.getJSONObject(0)
                .getString("created_on"));
    }
    
    /**
     * Test listSurveys method with optional Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateSurveyWithMandatoryParameters",
            "testCreateSurveyWithOptionalParameters" }, description = "surveygizmo {listSurveys} integration test with optional parameters.")
    public void testListSurveysWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listSurveys");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSurveys_optional.json");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v4/survey?" + authParam + "&page=2&resultsperpage=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray esbDataArray = esbRestResponse.getBody().getJSONArray("data");
        JSONArray apiDataArray = apiRestResponse.getBody().getJSONArray("data");
        Assert.assertEquals(apiRestResponse.getBody().getString("total_count"),
                esbRestResponse.getBody().getString("total_count"));
        Assert.assertEquals(apiRestResponse.getBody().getString("page"), esbRestResponse.getBody().getString("page"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"),
                esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("results_per_page"), esbRestResponse.getBody()
                .getString("results_per_page"));
        Assert.assertEquals(apiDataArray.getJSONObject(0).getString("id"), esbDataArray.getJSONObject(0)
                .getString("id"));
    }
    
    /**
     * Test listSurveys method with negative case.
     */
    @Test(groups = { "wso2.esb" }, description = "surveygizmo {listSurveys} integration test with negative case.")
    public void testListSurveysWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listSurveys");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSurveys_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v4/survey?" + authParam + "&filter[field][0]=createdon";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getString("code"), esbRestResponse.getBody().getString("code"));
        Assert.assertEquals(apiRestResponse.getBody().getString("message"),
                esbRestResponse.getBody().getString("message"));
        
    }
    
    /**
     * Test createCampaign method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateSurveyWithMandatoryParameters" }, description = "surveygizmo {createCampaign} integration test with mandatory parameters.")
    public void testCreateCampaignWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCampaign");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCampaign_mandatory.json");
        String esbCampaignId = esbRestResponse.getBody().getJSONObject("data").getString("id");
        connectorProperties.setProperty("campaignId", esbCampaignId);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v4/survey/" + connectorProperties.getProperty("surveyId")
                        + "/surveycampaign/" + esbCampaignId + "?" + authParam;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("_subtype"),
                connectorProperties.getProperty("campaignType"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("name"),
                connectorProperties.getProperty("campaignName"));
    }
    
    /**
     * Test createCampaign method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateSurveyWithMandatoryParameters" }, description = "surveygizmo {createCampaign} integration test with optional parameters.")
    public void testCreateCampaignWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCampaign");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCampaign_optional.json");
        String esbCampaignId = esbRestResponse.getBody().getJSONObject("data").getString("id");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v4/survey/" + connectorProperties.getProperty("surveyId")
                        + "/surveycampaign/" + esbCampaignId + "?" + authParam;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("_subtype"),
                connectorProperties.getProperty("campaignType"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("name"),
                connectorProperties.getProperty("campaignName"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("status").toLowerCase(),
                connectorProperties.getProperty("campaignStatus"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("language"),
                connectorProperties.getProperty("campaignLanguage"));
    }
    
    /**
     * Test createCampaign method with negative case.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateSurveyWithMandatoryParameters" }, description = "surveygizmo {createCampaign} integration test with negative case.")
    public void testCreateCampaignWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCampaign");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCampaign_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v4/survey/" + connectorProperties.getProperty("surveyId")
                        + "/surveycampaign?" + authParam;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_createCampaign_negative.json");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("result_ok"),
                esbRestResponse.getBody().getString("result_ok"));
        Assert.assertEquals(apiRestResponse.getBody().getString("code"), esbRestResponse.getBody().getString("code"));
        Assert.assertEquals(apiRestResponse.getBody().getString("message"),
                esbRestResponse.getBody().getString("message"));
        
    }
    
    /**
     * Test updateCampaign method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCampaignWithMandatoryParameters" }, description = "surveygizmo {updateCampaign} integration test with optional parameters.")
    public void testUpdateCampaignWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateCampaign");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v4/survey/" + connectorProperties.getProperty("surveyId")
                        + "/surveycampaign/" + connectorProperties.getProperty("campaignId") + "?" + authParam;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        String orginalName = apiRestResponse.getBody().getJSONObject("data").getString("name");
        String orginalLanguage = apiRestResponse.getBody().getJSONObject("data").getString("language");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCampaign_optional.json");
        apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        String updatedName = apiRestResponse.getBody().getJSONObject("data").getString("name");
        String updatedLanguage = apiRestResponse.getBody().getJSONObject("data").getString("language");
        Assert.assertNotEquals(orginalName, updatedName);
        Assert.assertNotEquals(orginalLanguage, updatedLanguage);
    }
    
    /**
     * Test updateCampaign method with negative case.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCampaignWithMandatoryParameters" }, description = "surveygizmo {updateCampaign} integration test with negative case.")
    public void testUpdateCampaignWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateCampaign");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCampaign_negative.json");
        
        String apiEndPoint =
        		connectorProperties.getProperty("apiUrl") + "/v4/survey/" + connectorProperties.getProperty("surveyId")
                + "/surveycampaign/" + connectorProperties.getProperty("campaignId") + "?" + authParam;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateCampaign_negative.json");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("result_ok"),
                esbRestResponse.getBody().getString("result_ok"));
        Assert.assertEquals(apiRestResponse.getBody().getString("code"), esbRestResponse.getBody().getString("code"));
        Assert.assertEquals(apiRestResponse.getBody().getString("message"),
                esbRestResponse.getBody().getString("message"));
        
    }
    
    /**
     * Test getCampaignById method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCampaignWithMandatoryParameters" }, description = "surveygizmo {getCampaignById} integration test with mandatory parameters.")
    public void testGetCampaignByIdWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getCampaignById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCampaignById_mandatory.json");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v4/survey/" + connectorProperties.getProperty("surveyId")
                        + "/surveycampaign/" + connectorProperties.getProperty("campaignId") + "?" + authParam;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getString("result_ok"),
                esbRestResponse.getBody().getString("result_ok"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("id"), esbRestResponse.getBody()
                .getJSONObject("data").getString("id"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("status"), esbRestResponse
                .getBody().getJSONObject("data").getString("status"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getString("name"), esbRestResponse
                .getBody().getJSONObject("data").getString("name"));
    }
    
    /**
     * Test listCampaigns method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCampaignWithMandatoryParameters",
            "testCreateCampaignWithOptionalParameters" }, description = "surveygizmo {listCampaigns} integration test with mandatory parameters.")
    public void testListCampaignsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCampaigns");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCampaigns_mandatory.json");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v4/survey/" + connectorProperties.getProperty("surveyId")
                        + "/surveycampaign?" + authParam;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray esbDataArray = esbRestResponse.getBody().getJSONArray("data");
        JSONArray apiDataArray = apiRestResponse.getBody().getJSONArray("data");
        Assert.assertEquals(apiRestResponse.getBody().getString("total_count"),
                esbRestResponse.getBody().getString("total_count"));
        Assert.assertEquals(apiDataArray.getJSONObject(0).getString("id"), esbDataArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(apiDataArray.getJSONObject(0).getString("status"),
                esbDataArray.getJSONObject(0).getString("status"));
        Assert.assertEquals(apiDataArray.getJSONObject(0).getString("name"),
                esbDataArray.getJSONObject(0).getString("name"));
        Assert.assertEquals(apiDataArray.getJSONObject(0).getString("datecreated"), esbDataArray.getJSONObject(0)
                .getString("datecreated"));
    }
    
    /**
     * Test listCampaigns method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCampaignWithMandatoryParameters",
            "testCreateCampaignWithOptionalParameters" }, description = "surveygizmo {listCampaigns} integration test with optional parameters.")
    public void testListCampaignsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCampaigns");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCampaigns_optional.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v4/survey/" + connectorProperties.getProperty("surveyId")
                        + "/surveycampaign?" + authParam + "&page=2&resultsperpage=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbDataArray = esbRestResponse.getBody().getJSONArray("data");
        JSONArray apiDataArray = apiRestResponse.getBody().getJSONArray("data");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("total_count"),
                esbRestResponse.getBody().getString("total_count"));
        Assert.assertEquals(apiRestResponse.getBody().getString("page"), esbRestResponse.getBody().getString("page"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"),
                esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("results_per_page"), esbRestResponse.getBody()
                .getString("results_per_page"));
        Assert.assertEquals(apiDataArray.getJSONObject(0).getString("id"), esbDataArray.getJSONObject(0)
                .getString("id"));
        
    }
    
    /**
     * Test listCampaigns method with negative case.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCampaignWithMandatoryParameters",
            "testCreateCampaignWithOptionalParameters" }, description = "surveygizmo {listCampaigns} integration test with negative case.")
    public void testListCampaignsWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listCampaigns");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCampaigns_negative.json");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v4/survey/" + connectorProperties.getProperty("surveyId")
                        + "/surveycampaign?" + authParam + "&page=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getString("code"), esbRestResponse.getBody().getString("code"));
        Assert.assertEquals(apiRestResponse.getBody().getString("message"),
                esbRestResponse.getBody().getString("message"));
    }
    
    /**
     * Test listContactLists method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "surveygizmo {listContactLists} integration test with mandatory parameters.")
    public void testListContactListsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listContactLists");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContactLists_mandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v4/contactlist?"+authParam;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        connectorProperties.put("contactListId", apiRestResponse.getBody().getJSONObject("0").getString("iGroupID"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("0").getString("iGroupID"), esbRestResponse.getBody()
                .getJSONObject("0").getString("iGroupID"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("0").getString("dDateModifed"), esbRestResponse
                .getBody().getJSONObject("0").getString("dDateModifed"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("0").getString("sGroupName"), esbRestResponse
                .getBody().getJSONObject("0").getString("sGroupName"));

    }
    
    /**
     * Test listContactLists method with optional  Parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "surveygizmo {listContactLists} integration test with optional parameters.")
    public void testListContactListsWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listContactLists");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContactLists_optional.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v4/contactlist?"+authParam+"&page=1&resultsperpage=2";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("0").getString("iGroupID"), esbRestResponse.getBody()
                .getJSONObject("0").getString("iGroupID"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("0").getString("dDateModifed"), esbRestResponse
                .getBody().getJSONObject("0").getString("dDateModifed"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("0").getString("sGroupName"), esbRestResponse
                .getBody().getJSONObject("0").getString("sGroupName"));
    }
    
    /**
     * Test getContactListById method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" },dependsOnMethods = { "testListContactListsWithMandatoryParameters"}, description = "surveygizmo {getContactListById} integration test with mandatory parameters.")
    public void testGetContactListByIdWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getContactListById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactListById_mandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v4/contactlist/"+connectorProperties.getProperty("contactListId") +"?"+authParam;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("0").getString("iCustomerID"), esbRestResponse.getBody()
                .getJSONObject("0").getString("iCustomerID"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("0").getString("dDateAdded"), esbRestResponse
                .getBody().getJSONObject("0").getString("dDateAdded"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("0").getString("eStatus"), esbRestResponse
                .getBody().getJSONObject("0").getString("eStatus"));
    }
    
    /**
     * Test getContactListById method with optional Parameters.
     */
    @Test(groups = { "wso2.esb" },dependsOnMethods = { "testListContactListsWithMandatoryParameters"}, description = "surveygizmo {getContactListById} integration test with optional parameters.")
    public void testGetContactListByIdWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getContactListById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactListById_optional.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v4/contactlist/"+connectorProperties.getProperty("contactListId") +"?"+authParam+"&page=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
    
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("0").getString("iCustomerID"), esbRestResponse.getBody()
                .getJSONObject("0").getString("iCustomerID"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("0").getString("dDateAdded"), esbRestResponse
                .getBody().getJSONObject("0").getString("dDateAdded"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("0").getString("eStatus"), esbRestResponse
                .getBody().getJSONObject("0").getString("eStatus"));
         
    }
    
    /**
     * Test listResponses method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "surveygizmo {listResponses} integration test with mandatory parameters.")
    public void testListResponsesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listResponses");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listResponses_mandatory.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v4/survey/"+connectorProperties.getProperty("surveyIdToListResponse")+"/surveyresponse?"+authParam;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbDataArray = esbRestResponse.getBody().getJSONArray("data");
        JSONArray apiDataArray = apiRestResponse.getBody().getJSONArray("data");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("total_count"),
                esbRestResponse.getBody().getString("total_count"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"),
                esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiDataArray.getJSONObject(0).getString("id"), esbDataArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(apiDataArray.getJSONObject(0).getString("status"),
                esbDataArray.getJSONObject(0).getString("status"));
        
         
    }
    
    /**
     * Test listResponses method with optional Parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "surveygizmo {listResponses} integration test with optional parameters.")
    public void testListResponsesWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listResponses");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listResponses_optional.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v4/survey/"+connectorProperties.getProperty("surveyIdToListResponse")+"/surveyresponse?"+authParam+"&page=2&resultsperpage=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray esbDataArray = esbRestResponse.getBody().getJSONArray("data");
        JSONArray apiDataArray = apiRestResponse.getBody().getJSONArray("data");
        Assert.assertEquals(apiRestResponse.getBody().getString("total_count"),
                esbRestResponse.getBody().getString("total_count"));
        Assert.assertEquals(apiRestResponse.getBody().getString("page"), esbRestResponse.getBody().getString("page"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"),
                esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("results_per_page"), esbRestResponse.getBody()
                .getString("results_per_page"));
        Assert.assertEquals(apiDataArray.getJSONObject(0).getString("id"), esbDataArray.getJSONObject(0)
                .getString("id"));
    }
    
    /**
     * Test listResponses method with negative case.
     */
    @Test(groups = { "wso2.esb" }, description = "surveygizmo {listResponses} integration test with negative case.")
    public void testListResponsesWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listResponses");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listResponses_negative.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v4/survey/"+connectorProperties.getProperty("surveyIdToListResponse")+"/surveyresponse?"+authParam+"&page=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getString("result_ok"),
                esbRestResponse.getBody().getString("result_ok"));
        Assert.assertEquals(apiRestResponse.getBody().getString("code"), esbRestResponse.getBody().getString("code"));
        Assert.assertEquals(apiRestResponse.getBody().getString("message"),
                esbRestResponse.getBody().getString("message"));

    }
}
