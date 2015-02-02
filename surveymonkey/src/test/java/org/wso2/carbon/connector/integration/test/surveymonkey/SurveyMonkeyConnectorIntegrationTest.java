/**
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

package org.wso2.carbon.connector.integration.test.surveymonkey;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class SurveyMonkeyConnectorIntegrationTest extends ConnectorIntegrationTestBase {

	private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        
        init("surveymonkey");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("accessToken"));
        
    }
    
    /**
     * Positive test case for getSurveyList method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "surveymonkey {getSurveyList} integration test with mandatory parameters.")
    public void testGetSurveyListWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getSurveyList");
        String apiEndPoint = "https://api.surveymonkey.net/v2/surveys/get_survey_list?api_key=" + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSurveyList_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page"), apiRestResponse.getBody().getJSONObject("data").get("page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page_size"), apiRestResponse.getBody().getJSONObject("data").get("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("surveys").length(), apiRestResponse.getBody().getJSONObject("data").getJSONArray("surveys").length());
        
    }
    
    /**
     * Positive test case for getSurveyList method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "surveymonkey {getSurveyList} integration test with optional parameters.")
    public void testGetSurveyListWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getSurveyList");
        String apiEndPoint = "https://api.surveymonkey.net/v2/surveys/get_survey_list?api_key=" + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSurveyList_optional.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getSurveyList_optional.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page"), apiRestResponse.getBody().getJSONObject("data").get("page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page_size"), apiRestResponse.getBody().getJSONObject("data").get("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("surveys").length(), apiRestResponse.getBody().getJSONObject("data").getJSONArray("surveys").length());
        
    }
    
    /**
     * Negative test case for getSurveyList method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "surveymonkey {getSurveyList} integration test with negative case.")
    public void testGetSurveyListWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getSurveyList");
        String apiEndPoint = "https://api.surveymonkey.net/v2/surveys/get_survey_list?api_key=" + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSurveyList_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getSurveyList_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().get("errmsg"), apiRestResponse.getBody().get("errmsg"));
        
    }
    
    /**
     * Positive test case for getCollectorList method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "surveymonkey {getCollectorList} integration test with mandatory parameters.")
    public void testGetCollectorListWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getCollectorList");
        String apiEndPoint = "https://api.surveymonkey.net/v2/surveys/get_collector_list?api_key=" + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCollectorList_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getCollectorList_mandatory.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page"), apiRestResponse.getBody().getJSONObject("data").get("page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page_size"), apiRestResponse.getBody().getJSONObject("data").get("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("collectors").length(), apiRestResponse.getBody().getJSONObject("data").getJSONArray("collectors").length());
        
    }
    
    /**
     * Optional test case for getCollectorList method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "surveymonkey {getCollectorList} integration test with optional parameters.")
    public void testGetCollectorListWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getCollectorList");
        String apiEndPoint = "https://api.surveymonkey.net/v2/surveys/get_collector_list?api_key=" + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCollectorList_optional.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getCollectorList_optional.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page"), apiRestResponse.getBody().getJSONObject("data").get("page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page_size"), apiRestResponse.getBody().getJSONObject("data").get("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("collectors").length(), apiRestResponse.getBody().getJSONObject("data").getJSONArray("collectors").length());
        
    }
    
    /**
     * Negative test case for getCollectorList method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "surveymonkey {getCollectorList} integration test with negative parameters.")
    public void testGetCollectorListWithNegativeParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getCollectorList");
        String apiEndPoint = "https://api.surveymonkey.net/v2/surveys/get_collector_list?api_key=" + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCollectorList_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getCollectorList_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().get("errmsg"), apiRestResponse.getBody().get("errmsg"));
        
    }
    
    /**
     * Positive test case for getRespondentList method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "surveymonkey {getRespondentList} integration test with mandatory parameters.")
    public void testGetRespondentListWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getRespondentList");
        String apiEndPoint = "https://api.surveymonkey.net/v2/surveys/get_respondent_list?api_key=" + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRespondentList_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getRespondentList_mandatory.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page"), apiRestResponse.getBody().getJSONObject("data").get("page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page_size"), apiRestResponse.getBody().getJSONObject("data").get("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("respondents").length(), apiRestResponse.getBody().getJSONObject("data").getJSONArray("respondents").length());
        
    }
    
    /**
     * Optional test case for getRespondentList method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "surveymonkey {getRespondentList} integration test with optional parameters.")
    public void testGetRespondentListWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getRespondentList");
        String apiEndPoint = "https://api.surveymonkey.net/v2/surveys/get_respondent_list?api_key=" + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRespondentList_optional.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getRespondentList_optional.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page"), apiRestResponse.getBody().getJSONObject("data").get("page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("page_size"), apiRestResponse.getBody().getJSONObject("data").get("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONArray("respondents").length(), apiRestResponse.getBody().getJSONObject("data").getJSONArray("respondents").length());
        
    }
    
    /**
     * Negative test case for getRespondentList method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "surveymonkey {getRespondentList} integration test with negative parameters.")
    public void testGetRespondentListWithNegativeParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getRespondentList");
        String apiEndPoint = "https://api.surveymonkey.net/v2/surveys/get_respondent_list?api_key=" + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRespondentList_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getRespondentList_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().get("errmsg"), apiRestResponse.getBody().get("errmsg"));
        
    }
    
    /**
     * Positive test case for getSurveyDetails method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "surveymonkey {getSurveyDetails} integration test with mandatory parameters.")
    public void testGetSurveyDetailsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getSurveyDetails");
        String apiEndPoint = "https://api.surveymonkey.net/v2/surveys/get_survey_details?api_key=" + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSurveyDetails_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getSurveyDetails_mandatory.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("survey_id"), apiRestResponse.getBody().getJSONObject("data").get("survey_id"));
        
    }
    
    /**
     * Negative test case for getSurveyDetails method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "surveymonkey {getSurveyDetails} integration test with negative case.")
    public void testGetSurveyDetailsWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getSurveyDetails");
        String apiEndPoint = "https://api.surveymonkey.net/v2/surveys/get_survey_details?api_key=" + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSurveyDetails_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getSurveyDetails_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().get("errmsg"), apiRestResponse.getBody().get("errmsg"));
        
    }
    
    /**
     * Mandatory test case for getUserDetails method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "surveymonkey {getUserDetails} integration test with mandatory case.")
    public void testGetUserDetailsWithMandatoryCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getUserDetails");
        String apiEndPoint = "https://api.surveymonkey.net/v2/user/get_user_details?api_key=" + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserDetails_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").getJSONObject("user_details").get("username"), apiRestResponse.getBody().getJSONObject("data").getJSONObject("user_details").get("username"));
        
    }
    
    /**
     * Mandatory test case for getResponses method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "surveymonkey {getResponses} integration test with mandatory case.")
    public void testGetResponsesWithMandatoryCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getResponses");
        String apiEndPoint = "https://api.surveymonkey.net/v2/surveys/get_responses?api_key=" + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getResponses_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getResponses_mandatory.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));        
        Assert.assertEquals(((JSONObject)esbRestResponse.getBody().getJSONArray("data").get(0)).get("respondent_id"),((JSONObject)apiRestResponse.getBody().getJSONArray("data").get(0)).get("respondent_id"));
        
    }
    
    /**
     * Negative test case for getResponses method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "surveymonkey {getResponses} integration test with negative case.")
    public void testGetResponsesWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getResponses");
        String apiEndPoint = "https://api.surveymonkey.net/v2/surveys/get_responses?api_key=" + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getResponses_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getResponses_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().get("errmsg"), apiRestResponse.getBody().get("errmsg"));
        
    }
    
    /**
     * Mandatory test case for getResponseCounts method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "surveymonkey {getResponseCounts} integration test with mandatory case.")
    public void testGetResponseCountsWithMandatoryCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getResponseCounts");
        String apiEndPoint = "https://api.surveymonkey.net/v2/surveys/get_response_counts?api_key=" + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getResponseCounts_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getResponseCounts_mandatory.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("data").get("started"), apiRestResponse.getBody().getJSONObject("data").get("started"));
        
    }
    
    /**
     * Negative test case for getResponseCounts method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "surveymonkey {getResponseCounts} integration test with negative case.")
    public void testGetResponsesCountsWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getResponseCounts");
        String apiEndPoint = "https://api.surveymonkey.net/v2/surveys/get_response_counts?api_key=" + connectorProperties.getProperty("apiKey");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getResponseCounts_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getResponseCounts_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().get("errmsg"), apiRestResponse.getBody().get("errmsg"));
        
    }
    
}