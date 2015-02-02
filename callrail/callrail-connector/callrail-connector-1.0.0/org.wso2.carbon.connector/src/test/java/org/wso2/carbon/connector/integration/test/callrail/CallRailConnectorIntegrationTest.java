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
package org.wso2.carbon.connector.integration.test.callrail;

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

public class CallRailConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> parametersMap = new HashMap<String, String>();
    
    private String apiUrl;
    
    private String perPage;
    private String page;
    private long timeOut;
    
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("callrail-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiUrl=connectorProperties.getProperty("apiUrl");
        perPage=connectorProperties.getProperty("perPage");
        page=connectorProperties.getProperty("page");
        timeOut=Long.parseLong(connectorProperties.getProperty("timeOut"));
        
        apiRequestHeadersMap.put("Authorization", "Token token=" + connectorProperties.getProperty("apiKey"));
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
    }
    
    /**
     * Positive test case for listUsers method with mandatory parameters.
     */
    @Test(priority = 1, description = "callrail {listUsers} integration test with mandatory parameters.")
    public void testListUsersWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listUsers");
        String apiEndPoint=apiUrl+"/v1/users.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_mandatory.json");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"), esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_records"), esbRestResponse.getBody().getString("total_records"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("users").length(), esbRestResponse.getBody().getJSONArray("users").length());
    }
    
    /**
     * Positive test case for listUsers method with optional parameters.
     */
    @Test(priority = 1, description = "callrail {listUsers} integration test with optional parameters.")
    public void testListUsersWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listUsers");
        
        
        String apiEndPoint=apiUrl+"/v1/users.json?per_page="+perPage+"&page="+page;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_optional.json");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"), esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_records"), esbRestResponse.getBody().getString("total_records"));
        Assert.assertEquals(apiRestResponse.getBody().getString("page"), esbRestResponse.getBody().getString("page"));
        Assert.assertEquals(apiRestResponse.getBody().getString("per_page"), esbRestResponse.getBody().getString("per_page"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("users").length(), esbRestResponse.getBody().getJSONArray("users").length());
    }
    
    /**
     * Positive test case for listCalls method with mandatory parameters.
     */
    @Test(priority = 1, description = "callrail {listCalls} integration test with mandatory parameters.")
    public void testListCallsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listCalls");
        String apiEndPoint=apiUrl+"/v1/calls.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCalls_mandatory.json");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"), esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_records"), esbRestResponse.getBody().getString("total_records"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("calls").length(), esbRestResponse.getBody().getJSONArray("calls").length());
    }
    
    /**
     * Positive test case for listCalls method with optional parameters.
     */
    @Test(priority = 1, description = "callrail {listCalls} integration test with optional parameters.")
    public void testListCallsWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listCalls");
        
        String apiEndPoint=apiUrl+"/v1/calls.json?per_page="+perPage+"&page="+page+"&start_date="+connectorProperties.getProperty("startDate")+"&company_id="+connectorProperties.getProperty("companyId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCalls_optional.json");
       
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"), esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_records"), esbRestResponse.getBody().getString("total_records"));
        Assert.assertEquals(apiRestResponse.getBody().getString("page"), esbRestResponse.getBody().getString("page"));
        Assert.assertEquals(apiRestResponse.getBody().getString("per_page"), esbRestResponse.getBody().getString("per_page"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("calls").length(), esbRestResponse.getBody().getJSONArray("calls").length());
    }
    
    
    /**
     * Positive test case for listTags method with mandatory parameters.
     */
    @Test(priority = 1, description = "callrail {listTags} integration test with mandatory parameters.")
    public void testListTagsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listTags");
        String apiEndPoint=apiUrl+"/v1/companies/"+connectorProperties.getProperty("companyId")+"/tags.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTags_mandatory.json");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"), esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_records"), esbRestResponse.getBody().getString("total_records"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("tags").length(), esbRestResponse.getBody().getJSONArray("tags").length());
    }
    
    /**
     * Positive test case for listTags method with optional parameters.
     */
    @Test(priority = 1, description = "callrail {listTags} integration test with optional parameters.")
    public void testListTagsWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listTags");
        
       
       
        String apiEndPoint=apiUrl+"/v1/companies/"+connectorProperties.getProperty("companyId")+"/tags.json?per_page="+perPage+"&page="+page;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTags_optional.json");
       
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"), esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_records"), esbRestResponse.getBody().getString("total_records"));
        Assert.assertEquals(apiRestResponse.getBody().getString("page"), esbRestResponse.getBody().getString("page"));
        Assert.assertEquals(apiRestResponse.getBody().getString("per_page"), esbRestResponse.getBody().getString("per_page"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("tags").length(), esbRestResponse.getBody().getJSONArray("tags").length());
    }
    
    /**
     * Positive test case for updateCall method with mandatory parameters.
     */
    @Test(priority = 1, description = "callrail {updateCall} integration test with mandatory parameters.")
    public void testUpdateCallWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateCall");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCall_mandatory.json",parametersMap);
       
        Assert.assertEquals(204, esbRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for updateCall method with optional parameters.
     * @throws InterruptedException 
     */
    @Test(priority = 1, description = "callrail {updateCall} integration test with optional parameters.")
    public void testUpdateCallWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(timeOut);
        
        esbRequestHeadersMap.put("Action", "urn:updateCall");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCall_optional.json");
        
        Assert.assertEquals(204, esbRestResponse.getHttpStatusCode());
    }
    
    /**
     * Negative test case for updateCall method.
     * @throws InterruptedException 
     */
    @Test(priority = 1, description = "callrail {updateCall} integration test with negative case.")
    public void testUpdateCallWithNegativeCase() throws IOException, JSONException, InterruptedException {
        
        Thread.sleep(timeOut);
        esbRequestHeadersMap.put("Action", "urn:updateCall");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCall_negative.json",parametersMap);
       
        Assert.assertEquals(400, esbRestResponse.getHttpStatusCode(),"This is happen due to exceeding api limit");
    }
    
    /**
     * Positive test case for listUsersForSessionTrackerCallAlerts method with mandatory parameters.
     */
    @Test(priority = 1, description = "callrail {listUsersForSessionTrackerCallAlerts} integration test with mandatory parameters.")
    public void testListUsersForSessionTrackerCallAlertsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listUsersForSessionTrackerCallAlerts");
        String apiEndPoint=apiUrl+"/v1/companies/"+connectorProperties.getProperty("companyId")+"/session_trackers/"+connectorProperties.getProperty("sessionTrackerId")+"/call_alerts.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsersForSessionTrackerCallAlerts_mandatory.json");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"), esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_records"), esbRestResponse.getBody().getString("total_records"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("call_alerts").length(), esbRestResponse.getBody().getJSONArray("call_alerts").length());
    
    }
    
    /**
     * Positive test case for listUsersForSessionTrackerCallAlerts method with optional parameters.
     */
    @Test(priority = 1, description = "callrail {listUsersForSessionTrackerCallAlerts} integration test with optional parameters.")
    public void testListUsersForSessionTrackerCallAlertsWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listUsersForSessionTrackerCallAlerts");
        
       String apiEndPoint=apiUrl+"/v1/companies/"+connectorProperties.getProperty("companyId")+"/session_trackers/"+connectorProperties.getProperty("sessionTrackerId")+"/call_alerts.json?per_page="+perPage+"&page="+page;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsersForSessionTrackerCallAlerts_optional.json");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"), esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_records"), esbRestResponse.getBody().getString("total_records"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("call_alerts").length(), esbRestResponse.getBody().getJSONArray("call_alerts").length());
    
    }
    
    /**
     * Positive test case for listUsersForSessionTrackerSmsAlerts method with mandatory parameters.
     */
    @Test(priority = 1, description = "callrail {listUsersForSessionTrackerSmsAlerts} integration test with mandatory parameters.")
    public void testListUsersForSessionTrackerSmsAlertsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listUsersForSessionTrackerSmsAlerts");
        
        String apiEndPoint=apiUrl+"/v1/companies/"+connectorProperties.getProperty("companyId")+"/session_trackers/"+connectorProperties.getProperty("sessionTrackerId")+"/sms_alerts.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsersForSessionTrackerSmsAlerts_mandatory.json");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"), esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_records"), esbRestResponse.getBody().getString("total_records"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("sms_alerts").length(), esbRestResponse.getBody().getJSONArray("sms_alerts").length());
    
    }
    
    /**
     * Positive test case for listUsersForSessionTrackerSmsAlerts method with optional parameters.
     */
    @Test(priority = 1, description = "callrail {listUsersForSessionTrackerSmsAlerts} integration test with optional parameters.")
    public void testListUsersForSessionTrackerSmsAlertsWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listUsersForSessionTrackerSmsAlerts");
        
       
        
        String apiEndPoint=apiUrl+"/v1/companies/"+connectorProperties.getProperty("companyId")+"/session_trackers/"+connectorProperties.getProperty("sessionTrackerId")+"/sms_alerts.json?per_page="+perPage+"&page="+page;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsersForSessionTrackerSmsAlerts_optional.json");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"), esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_records"), esbRestResponse.getBody().getString("total_records"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("sms_alerts").length(), esbRestResponse.getBody().getJSONArray("sms_alerts").length());
    
    }
    
    /**
     * Positive test case for listUsersForSourceTrackerCallAlerts method with mandatory parameters.
     */
    @Test(priority = 1, description = "callrail {listUsersForSourceTrackerCallAlerts} integration test with mandatory parameters.")
    public void testListUsersForSourceTrackerCallAlertsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listUsersForSourceTrackerCallAlerts");
        String apiEndPoint=apiUrl+"/v1/companies/"+connectorProperties.getProperty("companyId")+"/source_trackers/"+connectorProperties.getProperty("sourceTrackerId")+"/call_alerts.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsersForSourceTrackerCallAlerts_mandatory.json");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"), esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_records"), esbRestResponse.getBody().getString("total_records"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("call_alerts").length(), esbRestResponse.getBody().getJSONArray("call_alerts").length());
    
    }
    
    /**
     * Positive test case for listUsersForSourceTrackerCallAlerts method with optional parameters.
     */
    @Test(priority = 1, description = "callrail {listUsersForSourceTrackerCallAlerts} integration test with optional parameters.")
    public void testListUsersForSourceTrackerCallAlertsWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listUsersForSourceTrackerCallAlerts");
        
        String apiEndPoint=apiUrl+"/v1/companies/"+connectorProperties.getProperty("companyId")+"/source_trackers/"+connectorProperties.getProperty("sourceTrackerId")+"/call_alerts.json?per_page="+perPage+"&page="+page;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
       
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsersForSourceTrackerCallAlerts_optional.json");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"), esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_records"), esbRestResponse.getBody().getString("total_records"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("call_alerts").length(), esbRestResponse.getBody().getJSONArray("call_alerts").length());
    
    }
    
    /**
     * Positive test case for listUsersForSourceTrackerSmsAlerts method with mandatory parameters.
     */
    @Test(priority = 1, description = "callrail {listUsersForSourceTrackerSmsAlerts} integration test with mandatory parameters.")
    public void testListUsersForSourceTrackerSmsAlertsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listUsersForSourceTrackerSmsAlerts");
        String apiEndPoint=apiUrl+"/v1/companies/"+connectorProperties.getProperty("companyId")+"/source_trackers/"+connectorProperties.getProperty("sourceTrackerId")+"/sms_alerts.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsersForSourceTrackerSmsAlerts_mandatory.json");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"), esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_records"), esbRestResponse.getBody().getString("total_records"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("sms_alerts").length(), esbRestResponse.getBody().getJSONArray("sms_alerts").length());
    
    }
    
    /**
     * Positive test case for listUsersForSourceTrackerSmsAlerts method with optional parameters.
     */
    @Test(priority = 1, description = "callrail {listUsersForSourceTrackerSmsAlerts} integration test with optional parameters.")
    public void testListUsersForSourceTrackerSmsAlertsWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listUsersForSourceTrackerSmsAlerts");
        
        String apiEndPoint=apiUrl+"/v1/companies/"+connectorProperties.getProperty("companyId")+"/source_trackers/"+connectorProperties.getProperty("sourceTrackerId")+"/sms_alerts.json?per_page="+perPage+"&page="+page;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsersForSourceTrackerSmsAlerts_optional.json");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"), esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_records"), esbRestResponse.getBody().getString("total_records"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("sms_alerts").length(), esbRestResponse.getBody().getJSONArray("sms_alerts").length());
    
    }
    
    /**
     * Positive test case for listSourceTrackers method with mandatory parameters.
     */
    @Test(priority = 1, description = "callrail {listSourceTrackers} integration test with mandatory parameters.")
    public void testListSourceTrackersWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listSourceTrackers");

        String apiEndPoint=apiUrl+"/v1/companies/"+connectorProperties.getProperty("companyId")+"/source_trackers.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSourceTrackers_mandatory.json");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"), esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_records"), esbRestResponse.getBody().getString("total_records"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("source_trackers").length(), esbRestResponse.getBody().getJSONArray("source_trackers").length());
    
    }  
    
    /**
     * Positive test case for listSourceTrackers method with optional parameters.
     */
    @Test(priority = 1, description = "callrail {listSourceTrackers} integration test with optional parameters.")
    public void testListSourceTrackersWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listSourceTrackers");


        String apiEndPoint=apiUrl+"/v1/companies/"+connectorProperties.getProperty("companyId")+"/source_trackers.json?per_page="+perPage+"&page="+page;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSourceTrackers_optional.json");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"), esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_records"), esbRestResponse.getBody().getString("total_records"));
        Assert.assertEquals(apiRestResponse.getBody().getString("page"), esbRestResponse.getBody().getString("page"));
        Assert.assertEquals(apiRestResponse.getBody().getString("per_page"), esbRestResponse.getBody().getString("per_page"));       
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("source_trackers").length(), esbRestResponse.getBody().getJSONArray("source_trackers").length());    
    }  
    
    /**
     * Positive test case for listSessionTrackers method with mandatory parameters.
     */
    @Test(priority = 1, description = "callrail {listSessionTrackers} integration test with mandatory parameters.")
    public void testListSessionTrackersWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listSessionTrackers");

        String apiEndPoint=apiUrl+"/v1/companies/"+connectorProperties.getProperty("companyId")+"/session_trackers.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSessionTrackers_mandatory.json");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"), esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_records"), esbRestResponse.getBody().getString("total_records"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("session_trackers").length(), esbRestResponse.getBody().getJSONArray("session_trackers").length());
    
    }  
    
    /**
     * Positive test case for listSessionTrackers method with optional parameters.
     */
    @Test(priority = 1, description = "callrail {listSessionTrackers} integration test with optional parameters.")
    public void testListSessionTrackersWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listSessionTrackers");


        String apiEndPoint=apiUrl+"/v1/companies/"+connectorProperties.getProperty("companyId")+"/session_trackers.json?per_page="+perPage+"&page="+page;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSessionTrackers_optional.json");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"), esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_records"), esbRestResponse.getBody().getString("total_records"));
        Assert.assertEquals(apiRestResponse.getBody().getString("page"), esbRestResponse.getBody().getString("page"));
        Assert.assertEquals(apiRestResponse.getBody().getString("per_page"), esbRestResponse.getBody().getString("per_page"));       
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("session_trackers").length(), esbRestResponse.getBody().getJSONArray("session_trackers").length());    
    }  
    
    /**
     * Positive test case for listCompanies method with mandatory parameters.
     */
    @Test(priority = 1, description = "callrail {listCompanies} integration test with mandatory parameters.")
    public void testListCompaniesWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listCompanies");
        String apiEndPoint=apiUrl+"/v1/companies.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCompanies_mandatory.json");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"), esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_records"), esbRestResponse.getBody().getString("total_records"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("companies").length(), esbRestResponse.getBody().getJSONArray("companies").length());
    }
    
    /**
     * Positive test case for listCompanies method with optional parameters.
     */
    @Test(priority = 1, description = "callrail {listCompanies} integration test with optional parameters.")
    public void testListCompaniesWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listCompanies");
        
        
        String apiEndPoint=apiUrl+"/v1/companies.json?per_page="+perPage+"&page="+page;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCompanies_optional.json");
       
        Assert.assertEquals(apiRestResponse.getBody().getString("total_pages"), esbRestResponse.getBody().getString("total_pages"));
        Assert.assertEquals(apiRestResponse.getBody().getString("total_records"), esbRestResponse.getBody().getString("total_records"));
        Assert.assertEquals(apiRestResponse.getBody().getString("page"), esbRestResponse.getBody().getString("page"));
        Assert.assertEquals(apiRestResponse.getBody().getString("per_page"), esbRestResponse.getBody().getString("per_page"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("companies").length(), esbRestResponse.getBody().getJSONArray("companies").length());
    }    
    
    
    
  
}
