/**
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.integration.test.surveymonkey;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class SurveyMonkeyConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private long timeOut;
    
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("surveymonkey-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("accessToken"));
        
        timeOut = Long.parseLong(connectorProperties.getProperty("timeOut"));
    }
    
    /**
     * Positive test case for getSurveyList method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "surveymonkey {getSurveyList} integration test with mandatory parameters.")
    public void testGetSurveyListWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getSurveyList");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/surveys/get_survey_list?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSurveyList_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page"), apiRestResponse.getBody()
                .getJSONObject("data").get("page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page_size"), apiRestResponse.getBody()
                .getJSONObject("data").get("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("surveys").length(),
                apiRestResponse.getBody().getJSONObject("data").getJSONArray("surveys").length());
        
    }
    
    /**
     * Positive test case for getSurveyList method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetSurveyListWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "surveymonkey {getSurveyList} integration test with optional parameters.")
    public void testGetSurveyListWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getSurveyList");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/surveys/get_survey_list?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSurveyList_optional.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getSurveyList_optional.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page"), apiRestResponse.getBody()
                .getJSONObject("data").get("page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page_size"), apiRestResponse.getBody()
                .getJSONObject("data").get("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("surveys").length(),
                apiRestResponse.getBody().getJSONObject("data").getJSONArray("surveys").length());
        
    }
    
    /**
     * Negative test case for getSurveyList method.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetSurveyListWithOptionalParameters" }, groups = { "wso2.esb" }, description = "surveymonkey {getSurveyList} integration test with negative case.")
    public void testGetSurveyListWithNegativeCase() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getSurveyList");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/surveys/get_survey_list?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSurveyList_negative.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getSurveyList_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().get("errmsg"), apiRestResponse.getBody().get("errmsg"));
        
    }
    
    /**
     * Positive test case for getCollectorList method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetSurveyListWithNegativeCase" }, groups = { "wso2.esb" }, description = "surveymonkey {getCollectorList} integration test with mandatory parameters.")
    public void testGetCollectorListWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getCollectorList");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/surveys/get_collector_list?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCollectorList_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getCollectorList_mandatory.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page"), apiRestResponse.getBody()
                .getJSONObject("data").get("page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page_size"), apiRestResponse.getBody()
                .getJSONObject("data").get("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("collectors").length(),
                apiRestResponse.getBody().getJSONObject("data").getJSONArray("collectors").length());
        
    }
    
    /**
     * Optional test case for getCollectorList method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetCollectorListWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "surveymonkey {getCollectorList} integration test with optional parameters.")
    public void testGetCollectorListWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getCollectorList");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/surveys/get_collector_list?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCollectorList_optional.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getCollectorList_optional.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page"), apiRestResponse.getBody()
                .getJSONObject("data").get("page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page_size"), apiRestResponse.getBody()
                .getJSONObject("data").get("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("collectors").length(),
                apiRestResponse.getBody().getJSONObject("data").getJSONArray("collectors").length());
        
    }
    
    /**
     * Negative test case for getCollectorList method.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetCollectorListWithOptionalParameters" }, groups = { "wso2.esb" }, description = "surveymonkey {getCollectorList} integration test with negative parameters.")
    public void testGetCollectorListWithNegativeParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getCollectorList");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/surveys/get_collector_list?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCollectorList_negative.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getCollectorList_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().get("errmsg"), apiRestResponse.getBody().get("errmsg"));
        
    }
    
    /**
     * Positive test case for getRespondentList method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetCollectorListWithNegativeParameters" }, groups = { "wso2.esb" }, description = "surveymonkey {getRespondentList} integration test with mandatory parameters.")
    public void testGetRespondentListWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getRespondentList");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/surveys/get_respondent_list?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRespondentList_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getRespondentList_mandatory.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page"), apiRestResponse.getBody()
                .getJSONObject("data").get("page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page_size"), apiRestResponse.getBody()
                .getJSONObject("data").get("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("respondents").length(),
                apiRestResponse.getBody().getJSONObject("data").getJSONArray("respondents").length());
        
    }
    
    /**
     * Optional test case for getRespondentList method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetRespondentListWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "surveymonkey {getRespondentList} integration test with optional parameters.")
    public void testGetRespondentListWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getRespondentList");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/surveys/get_respondent_list?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRespondentList_optional.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getRespondentList_optional.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page"), apiRestResponse.getBody()
                .getJSONObject("data").get("page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page_size"), apiRestResponse.getBody()
                .getJSONObject("data").get("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("respondents").length(),
                apiRestResponse.getBody().getJSONObject("data").getJSONArray("respondents").length());
        
    }
    
    /**
     * Negative test case for getRespondentList method.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetRespondentListWithOptionalParameters" }, groups = { "wso2.esb" }, description = "surveymonkey {getRespondentList} integration test with negative parameters.")
    public void testGetRespondentListWithNegativeParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getRespondentList");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/surveys/get_respondent_list?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRespondentList_negative.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getRespondentList_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().get("errmsg"), apiRestResponse.getBody().get("errmsg"));
        
    }
    
    /**
     * Positive test case for getSurveyDetails method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetRespondentListWithNegativeParameters" }, groups = { "wso2.esb" }, description = "surveymonkey {getSurveyDetails} integration test with mandatory parameters.")
    public void testGetSurveyDetailsWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getSurveyDetails");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/surveys/get_survey_details?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSurveyDetails_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getSurveyDetails_mandatory.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("survey_id"), apiRestResponse.getBody()
                .getJSONObject("data").get("survey_id"));
        
    }
    
    /**
     * Negative test case for getSurveyDetails method.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetSurveyDetailsWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "surveymonkey {getSurveyDetails} integration test with negative case.")
    public void testGetSurveyDetailsWithNegativeCase() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getSurveyDetails");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/surveys/get_survey_details?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSurveyDetails_negative.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getSurveyDetails_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().get("errmsg"), apiRestResponse.getBody().get("errmsg"));
        
    }
    
    /**
     * Mandatory test case for getUserDetails method.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetSurveyDetailsWithNegativeCase" }, groups = { "wso2.esb" }, description = "surveymonkey {getUserDetails} integration test with mandatory case.")
    public void testGetUserDetailsWithMandatoryCase() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getUserDetails");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/user/get_user_details?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserDetails_mandatory.txt");
        // Thread.sleep(timeOut);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONObject("data").getJSONObject("user_details").get("username"),
                apiRestResponse.getBody().getJSONObject("data").getJSONObject("user_details").get("username"));
        
    }
    
    /**
     * Mandatory test case for getResponses method.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetUserDetailsWithMandatoryCase" }, groups = { "wso2.esb" }, description = "surveymonkey {getResponses} integration test with mandatory case.")
    public void testGetResponsesWithMandatoryCase() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getResponses");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/surveys/get_responses?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getResponses_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getResponses_mandatory.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(((JSONObject) esbRestResponse.getBody().getJSONArray("data").get(0)).get("respondent_id"),
                ((JSONObject) apiRestResponse.getBody().getJSONArray("data").get(0)).get("respondent_id"));
        
    }
    
    /**
     * Negative test case for getResponses method.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetResponsesWithMandatoryCase" }, groups = { "wso2.esb" }, description = "surveymonkey {getResponses} integration test with negative case.")
    public void testGetResponsesWithNegativeCase() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getResponses");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/surveys/get_responses?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getResponses_negative.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getResponses_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().get("errmsg"), apiRestResponse.getBody().get("errmsg"));
        
    }
    
    /**
     * Mandatory test case for getResponseCounts method.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetResponsesWithNegativeCase" }, groups = { "wso2.esb" }, description = "surveymonkey {getResponseCounts} integration test with mandatory case.")
    public void testGetResponseCountsWithMandatoryCase() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getResponseCounts");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/surveys/get_response_counts?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getResponseCounts_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getResponseCounts_mandatory.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("started"), apiRestResponse.getBody()
                .getJSONObject("data").get("started"));
        
    }
    
    /**
     * Negative test case for getResponseCounts method.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetResponseCountsWithMandatoryCase" }, groups = { "wso2.esb" }, description = "surveymonkey {getResponseCounts} integration test with negative case.")
    public void testGetResponsesCountsWithNegativeCase() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:getResponseCounts");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/surveys/get_response_counts?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getResponseCounts_negative.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getResponseCounts_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().get("errmsg"), apiRestResponse.getBody().get("errmsg"));
        
    }
    
    /**
     * Mandatory test case for createFlow method.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetResponsesCountsWithNegativeCase" }, groups = { "wso2.esb" }, description = "surveymonkey {createFlow} integration test with mandatory case.")
    public void testCreateFlowWithMandatoryCase() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:createFlow");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createFlow_mandatory.txt");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        String createdSurvey =
                esbRestResponse.getBody().getJSONObject("data").getJSONObject("survey").getString("survey_id");
        connectorProperties.setProperty("createdSurvey", createdSurvey);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/surveys/get_survey_details?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createFlow_mandatory.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), 0);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getJSONObject("title").getString("text"),
                connectorProperties.getProperty("title"));
    }
    
    /**
     * Optional test case for createFlow method.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateFlowWithMandatoryCase" }, groups = { "wso2.esb" }, description = "surveymonkey {createFlow} integration test with optional parameters.")
    public void testCreateFlowWithOptionalCase() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:createFlow");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createFlow_optional.txt");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        JSONObject responseData = esbRestResponse.getBody().getJSONObject("data");
        String createdSurveyOpt = responseData.getJSONObject("survey").getString("survey_id");
        connectorProperties.setProperty("createdSurveyOpt", createdSurveyOpt);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/surveys/get_survey_details?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createFlow_mandatory.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), 0);
        Assert.assertEquals(responseData.getJSONObject("collector").getString("name"),
                connectorProperties.getProperty("title"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("data").getJSONObject("title").getString("text"),
                connectorProperties.getProperty("title"));
    }
    
    /**
     * Negative test case for createFlow method.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateFlowWithOptionalCase" }, groups = { "wso2.esb" }, description = "surveymonkey {createFlow} integration test with negative case.")
    public void testCreateFlowWithNegativeCase() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:createFlow");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v2/batch/create_flow?api_key="
                        + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createFlow_negative.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createFlow_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().get("errmsg"), apiRestResponse.getBody().get("errmsg"));
    }
    
}
