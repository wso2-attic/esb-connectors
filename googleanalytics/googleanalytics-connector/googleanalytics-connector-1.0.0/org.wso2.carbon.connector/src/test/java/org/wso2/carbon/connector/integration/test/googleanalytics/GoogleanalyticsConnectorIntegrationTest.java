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

package org.wso2.carbon.connector.integration.test.googleanalytics;

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

public class GoogleanalyticsConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap;
    
    private Map<String, String> apiRequestHeadersMap;
    
    private String apiEndpointUrl;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("googleanalytics-connector-1.0.0");
        esbRequestHeadersMap = new HashMap<String, String>();
        apiRequestHeadersMap = new HashMap<String, String>();
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Accept", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        apiRequestHeadersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("accessToken"));
        
        apiEndpointUrl = connectorProperties.getProperty("apiUrl") + "/analytics/v3";
    }
    
    /**
     * Positive test case for getAccessTokenFromRefreshToken method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {getAccessTokenFromRefreshToken} integration test with mandatory parameters.")
    public void testGetAccessTokenFromRefreshToken() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAccessTokenFromRefreshToken");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAccessTokenFromRefreshToken.json");
        
        final String accessToken = esbRestResponse.getBody().getString("access_token");
        connectorProperties.put("accessToken", accessToken);
        apiRequestHeadersMap.put("Authorization", "Bearer " + accessToken);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().getString("token_type"));
        Assert.assertNotNull(esbRestResponse.getBody().getString("access_token"));
        
    }
    
    /**
     * Positive test case for getReportData method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {getReportData} integration test with mandatory parameters.", dependsOnMethods = {
            "testGetAccessTokenFromRefreshToken", "testListAccountSummariesWithMandatoryParameters" })
    public void testGetReportDataWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getReportData");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getReportData_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/data/ga?ids=" + connectorProperties.getProperty("coreReportIds") + "&start-date="
                        + connectorProperties.getProperty("coreReportStartDate") + "&end-date="
                        + connectorProperties.getProperty("coreReportEndDate") + "&metrics="
                        + connectorProperties.getProperty("coreReportMetrics");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(esbResponse.getString("selfLink"), apiResponse.getString("selfLink"));
        Assert.assertEquals(esbResponse.getJSONObject("query").getString("start-date"),
                apiResponse.getJSONObject("query").getString("start-date"));
        Assert.assertEquals(esbResponse.getJSONObject("query").getString("end-date"), apiResponse
                .getJSONObject("query").getString("end-date"));
    }
    
    /**
     * Positive test case for getReportData method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {getReportData} integration test with optional parameters.", dependsOnMethods = {
            "testGetAccessTokenFromRefreshToken", "testListAccountSummariesWithMandatoryParameters" })
    public void testGetReportDataWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getReportData");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getReportData_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/data/ga?ids=" + connectorProperties.getProperty("coreReportIds") + "&start-date="
                        + connectorProperties.getProperty("coreReportStartDate") + "&end-date="
                        + connectorProperties.getProperty("coreReportEndDate") + "&metrics="
                        + connectorProperties.getProperty("coreReportMetrics") + "&max-results="
                        + connectorProperties.getProperty("coreReportMaxResults") + "&start-index="
                        + connectorProperties.getProperty("coreReportStartIndex");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(esbResponse.getString("selfLink"), apiResponse.getString("selfLink"));
        Assert.assertEquals(connectorProperties.getProperty("coreReportMaxResults"), apiResponse.getJSONObject("query")
                .getString("max-results"));
        Assert.assertEquals(connectorProperties.getProperty("coreReportStartIndex"), apiResponse.getJSONObject("query")
                .getString("start-index"));
    }
    
    /**
     * Negative test case for getReportData method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {getReportData} integration test with negative case.", dependsOnMethods = {
            "testGetReportDataWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testGetReportDataWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getReportData");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getReportData_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/data/ga?ids=invalid&start-date="
                        + connectorProperties.getProperty("coreReportStartDate") + "&end-date="
                        + connectorProperties.getProperty("coreReportEndDate") + "&metrics="
                        + connectorProperties.getProperty("coreReportMetrics");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for listAccountSummaries method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listAccountSummaries} integration test with mandatory parameters.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
    public void testListAccountSummariesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listAccountSummaries");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAccountSummaries_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();

        final String apiEndpoint = apiEndpointUrl + "/management/accountSummaries";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("username"), apiResponse.getString("username"));
        Assert.assertEquals(esbResponse.getString("itemsPerPage"), apiResponse.getString("itemsPerPage"));
        Assert.assertEquals(esbResponse.getString("totalResults"), apiResponse.getString("totalResults"));
    }
    
    /**
     * Positive test case for listAccountSummaries method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listAccountSummaries} integration test with optional parameters.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
    public void testListAccountSummariesWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listAccountSummaries");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAccountSummaries_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accountSummaries?max-results="
                        + connectorProperties.getProperty("accountSumMaxResults") + "&start-index="
                        + connectorProperties.getProperty("accountSumStartIndex");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(connectorProperties.getProperty("accountSumStartIndex"),
                apiResponse.getString("startIndex"));
        Assert.assertEquals(connectorProperties.getProperty("accountSumMaxResults"),
                apiResponse.getString("itemsPerPage"));
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("username"), apiResponse.getString("username"));
    }
    
    /**
     * Negative test case for listAccountSummaries method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listAccountSummaries} integration test with negative case.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
    public void testListAccountSummariesWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listAccountSummaries");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAccountSummaries_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/management/accountSummaries?max-results=abc";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for listAccounts method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listAccounts} integration test with mandatory parameters.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
    public void testListAccountsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listAccounts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAccounts_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint = apiEndpointUrl + "/management/accounts";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("username"), apiResponse.getString("username"));
        Assert.assertEquals(esbResponse.getString("totalResults"), apiResponse.getString("totalResults"));
        Assert.assertEquals(esbResponse.getString("itemsPerPage"), apiResponse.getString("itemsPerPage"));
    }
    
    /**
     * Positive test case for listAccounts method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listAccounts} integration test with optional parameters.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
    public void testListAccountsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listAccounts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAccounts_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts?max-results="
                        + connectorProperties.getProperty("accountMaxResults") + "&start-index="
                        + connectorProperties.getProperty("accountStartIndex");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(connectorProperties.getProperty("accountStartIndex"), apiResponse.getString("startIndex"));
        Assert.assertEquals(connectorProperties.getProperty("accountMaxResults"), apiResponse.getString("itemsPerPage"));
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("username"), apiResponse.getString("username"));
    }
    
    /**
     * Negative test case for listAccounts method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listAccounts} integration test with negative case.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
    public void testListAccountsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listAccounts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAccounts_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/management/accounts?max-results=abc";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for createFilter method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {createFilter} integration test with mandatory parameters.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
    public void testCreateFilterWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createFilter");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createFilter_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String filterId = esbResponse.getString("id");
        connectorProperties.put("filterId", filterId);
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId") + "/filters/"
                        + filterId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(esbResponse.getString("selfLink"), apiResponse.getString("selfLink"));
        Assert.assertEquals(connectorProperties.getProperty("filterName"), apiResponse.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("filterType"), apiResponse.getString("type"));
        Assert.assertEquals(esbResponse.getString("created"), apiResponse.getString("created"));
    }
    
    /**
     * Positive test case for createFilter method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {createFilter} integration test with optional parameters.", dependsOnMethods = {
            "testGetAccessTokenFromRefreshToken", "testListAccountSummariesWithMandatoryParameters" })
    public void testCreateFilterWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createFilter");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createFilter_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String filterId = esbResponse.getString("id");
        connectorProperties.put("filterIdOpt", filterId);
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId") + "/filters/"
                        + filterId + "?fields=" + connectorProperties.getProperty("fields");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final String apiEndpointField =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId") + "/filters/"
                        + filterId;
        RestResponse<JSONObject> apiRestResponseWithField =
                sendJsonRestRequest(apiEndpointField, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertFalse(esbResponse.has("selfLink"));
        Assert.assertFalse(apiResponse.has("selfLink"));
        Assert.assertTrue(apiRestResponseWithField.getBody().has("selfLink"));
    }
    
    /**
     * Negative test case for createFilter method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {createFilter} integration test with negative case.", dependsOnMethods = {
            "testCreateFilterWithMandatoryParameters", "testGetAccessTokenFromRefreshToken",
            "testListAccountSummariesWithMandatoryParameters" })
    public void testCreateFilterWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createFilter");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createFilter_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId") + "/filters";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createFilter_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for updateFilter method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {updateFilter} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateFilterWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testUpdateFilterWithMandatoryParameters() throws IOException, JSONException {
    
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId") + "/filters/"
                        + connectorProperties.getProperty("filterId");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponseBefore = apiRestResponseBefore.getBody();
        
        esbRequestHeadersMap.put("Action", "urn:updateFilter");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateFilter_mandatory.json");
        
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponseAfter = apiRestResponseAfter.getBody();
        
        Assert.assertNotEquals(apiResponseBefore.getString("name"), apiResponseAfter.getString("name"));
        Assert.assertNotEquals(apiResponseBefore.getString("type"), apiResponseAfter.getString("type"));
        
        Assert.assertEquals(connectorProperties.getProperty("filterUpdatedName"), apiResponseAfter.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("filterUpdatedType"), apiResponseAfter.getString("type"));
        
    }
    
    /**
     * Method name: updateFilter 
     * Test scenario: Optional 
     * Reason to skip: No optional parameters to be updated.
     */
    
    /**
     * Negative test case for updateFilter method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {updateFilter} integration test with negative case.", dependsOnMethods = {
            "testCreateFilterWithMandatoryParameters", "testUpdateFilterWithMandatoryParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testUpdatefilterWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateFilter");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateFilter_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId") + "/filters/"
                        + connectorProperties.getProperty("filterId");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_updateFilter_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Method name: patchFilter 
     * Test scenario: Mandatory 
     * Reason to skip: No mandatory parameters to be updated.
     */
    
    /**
     * Positive test case for patchFilter method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {patchFilter} integration test with optional parameters.", dependsOnMethods = {"testCreateFilterWithOptionalParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testPatchFilterWithOptinalParameters() throws IOException, JSONException {
    
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId") + "/filters/"
                        + connectorProperties.getProperty("filterIdOpt");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponseBefore = apiRestResponseBefore.getBody();
        
        esbRequestHeadersMap.put("Action", "urn:patchFilter");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_patchFilter_optional.json");
        
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponseAfter = apiRestResponseAfter.getBody();
        
        Assert.assertNotEquals(apiResponseBefore.getString("name"), apiResponseAfter.getString("name"));
        Assert.assertNotEquals(apiResponseBefore.getString("type"), apiResponseAfter.getString("type"));
        
        Assert.assertEquals(connectorProperties.getProperty("filterUpdatedName"), apiResponseAfter.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("filterUpdatedType"),  apiResponseAfter.getString("type"));
        
    }
    
    /**
     * Negative test case for patchFilter method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {patchFilter} integration test with negative case.", dependsOnMethods = {
            "testCreateFilterWithMandatoryParameters", "testPatchFilterWithOptinalParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testPatchfilterWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:patchFilter");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_patchFilter_negative.json");

        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId") + "/filters/"
                        + connectorProperties.getProperty("filterId");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_patchFilter_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for listFilters method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listFilters} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateFilterWithMandatoryParameters", "testCreateFilterWithOptionalParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testListFiltersWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listFilters");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listFilters_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId") + "/filters";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("username"), apiResponse.getString("username"));
        Assert.assertEquals(esbResponse.getString("totalResults"), apiResponse.getString("totalResults"));
        Assert.assertEquals(esbResponse.getString("itemsPerPage"), apiResponse.getString("itemsPerPage"));
    }
    
    /**
     * Positive test case for listFilters method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listFilters} integration test with optional parameters.", dependsOnMethods = {
            "testCreateFilterWithOptionalParameters", "testGetAccessTokenFromRefreshToken" })
    public void testListFiltersWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listFilters");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listFilters_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/filters?max-results=" + connectorProperties.getProperty("coreReportMaxResults")
                        + "&start-index=" + connectorProperties.getProperty("coreReportStartIndex")
                        + "&prettyPrint=true";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("username"), apiResponse.getString("username"));
        Assert.assertEquals(connectorProperties.getProperty("coreReportStartIndex"),
                apiResponse.getString("startIndex"));
        Assert.assertEquals(connectorProperties.getProperty("coreReportMaxResults"),
                apiResponse.getString("itemsPerPage"));
        Assert.assertEquals(esbResponse.getJSONArray("items").length(), apiResponse.getJSONArray("items").length());
    }
    
    /**
     * Negative test case for listFilters method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listFilters} integration test with negative case.", dependsOnMethods = {
            "testListFiltersWithOptionalParameters", "testListFiltersWithOptionalParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testListFiltersWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listFilters");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listFilters_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/filters?max-results=abc&start-index="
                        + connectorProperties.getProperty("coreReportStartIndex") + "&prettyPrint=true";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for deleteFilter method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {deleteFilter} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateFilterWithMandatoryParameters", "testPatchfilterWithNegativeCase","testUpdateFilterWithMandatoryParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testDeleteFilterWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteFilter");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteFilter_mandatory.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId") + "/filters/"
                        + connectorProperties.getProperty("filterId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 403);
    }
    
    /**
     * Method name: deleteFilter 
     * Test scenario: Optional 
     * Reason to skip: An optional test case cannot be written.
     */
    
    /**
     * Negative test case for deleteFilter method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {deleteFilter} integration test with negative case.", dependsOnMethods = {
            "testCreateFilterWithMandatoryParameters", "testPatchfilterWithNegativeCase",
            "testGetAccessTokenFromRefreshToken" })
    public void testDeleteFilterWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteFilter");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteFilter_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/filters/invalid";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "DELETE", apiRequestHeadersMap, "api_deleteFilter_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for createAccountUserLink method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {createAccountUserLink} integration test with mandatory parameters.", dependsOnMethods = {
            "testListAccountSummariesWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testCreateAccountUserLinkWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAccountUserLink");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAccountUserLink_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String userLinkId = esbResponse.getString("id");
        connectorProperties.put("userLinkId", userLinkId);
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/entityUserLinks";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
       
        final JSONArray itemArray = apiRestResponse.getBody().getJSONArray("items");
        
        String id;
        String email = "";
        String permissions = "";
        String selfLink = "";
        for (int i = 0; i < itemArray.length(); i++) {
            id = itemArray.getJSONObject(i).getString("id");
            if (id.equals(userLinkId)) {
                permissions = itemArray.getJSONObject(i).getJSONObject("permissions").toString();
                email = itemArray.getJSONObject(i).getJSONObject("userRef").getString("email");
                selfLink = itemArray.getJSONObject(i).getString("selfLink");
                break;
            }
        }
        
        Assert.assertEquals(connectorProperties.getProperty("userLinkEmail"), email);
        Assert.assertEquals(esbResponse.getJSONObject("permissions").toString(), permissions);
        Assert.assertEquals(esbResponse.getString("selfLink"), selfLink);
    }
    
    /**
     * Method name: createAccountUserLink 
     * Test scenario: Optional 
     * Reason to skip: The optional query parameters cannot be used to test the functionality of the method.
     */
    
    /**
     * Negative test case for createAccountUserLink method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {createAccountUserLink} integration test with negative case.", dependsOnMethods = {
            "testListAccountSummariesWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testCreateAccountUserLinkWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAccountUserLink");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAccountUserLink_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/entityUserLinks";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                        "api_createAccountUserLink_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for listAccountUserLinks method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listAccountUserLinks} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateAccountUserLinkWithMandatoryParameters", "testListAccountSummariesWithMandatoryParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testListAccountUserLinksWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listAccountUserLinks");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAccountUserLinks_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/entityUserLinks";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("totalResults"), apiResponse.getString("totalResults"));
        Assert.assertEquals(esbResponse.getString("itemsPerPage"), apiResponse.getString("itemsPerPage"));
        Assert.assertEquals(esbResponse.getString("startIndex"), apiResponse.getString("startIndex"));
    }
    
    /**
     * Positive test case for listAccountUserLinks method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listAccountUserLinks} integration test with optional parameters.", dependsOnMethods = {
            "testCreateFilterWithMandatoryParameters", "testCreateFilterWithOptionalParameters",
            "testListAccountSummariesWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testListAccountUserLinksWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listAccountUserLinks");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAccountUserLinks_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/entityUserLinks?max-results=" + connectorProperties.getProperty("linksMaxResults")
                        + "&start-index=" + connectorProperties.getProperty("coreReportStartIndex")
                        + "&prettyPrint=true" + "&fields=" + connectorProperties.getProperty("listLinkFields");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("totalResults"), apiResponse.getString("totalResults"));
        Assert.assertEquals(connectorProperties.getProperty("coreReportStartIndex"),
                apiResponse.getString("startIndex"));
        Assert.assertEquals(esbResponse.getJSONArray("items").length(),
                Integer.parseInt(connectorProperties.getProperty("linksMaxResults")));
        Assert.assertEquals(esbResponse.getJSONArray("items").length(), apiResponse.getJSONArray("items").length());
    }
    
    /**
     * Negative test case for listAccountUserLinks method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listAccountUserLinks} integration test with negative case.", dependsOnMethods = {
            "testListAccountSummariesWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testListAccountUserLinksWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listAccountUserLinks");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAccountUserLinks_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/entityUserLinks?max-results=abc&start-index="
                        + connectorProperties.getProperty("coreReportStartIndex") + "&prettyPrint=true";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for updateAccountUserLink method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {updateAccountUserLink} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateAccountUserLinkWithMandatoryParameters", "testListAccountSummariesWithMandatoryParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testUpdateAccountUserLinkWithMandatoryParameters() throws IOException, JSONException {
    
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/entityUserLinks";
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final String userLinkId = connectorProperties.getProperty("userLinkId");
        final JSONArray itemArrayBefore = apiRestResponseBefore.getBody().getJSONArray("items");
        
        String id;
        String permissions = "";
        for (int i = 0; i < itemArrayBefore.length(); i++) {
            id = itemArrayBefore.getJSONObject(i).getString("id");
            if (id.equals(userLinkId)) {
                permissions = itemArrayBefore.getJSONObject(i).getJSONObject("permissions").toString();
                break;
            }
        }
        
        esbRequestHeadersMap.put("Action", "urn:updateAccountUserLink");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updatedAccountUserLink_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONArray itemArrayAfter = apiRestResponseAfter.getBody().getJSONArray("items");
        
        String idAfter;
        String permissionsAfter = "";
        for (int i = 0; i < itemArrayAfter.length(); i++) {
            idAfter = itemArrayAfter.getJSONObject(i).getString("id");
            if (idAfter.equals(userLinkId)) {
                permissionsAfter = itemArrayAfter.getJSONObject(i).getJSONObject("permissions").toString();
                break;
            }
        }
        
        Assert.assertNotEquals(permissions, permissionsAfter);
        Assert.assertEquals(esbResponse.getJSONObject("permissions").toString(), permissionsAfter);
    }
    
    /**
     * Method name: updateAccountUserLink 
     * Test scenario: Optional 
     * Reason to skip: The optional query parameters cannot be used to test the functionality of the method.
     */
    
    /**
     * Negative test case for updateAccountUserLink method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {updateAccountUserLink} integration test with negative case.", dependsOnMethods = {
            "testCreateAccountUserLinkWithMandatoryParameters", "testListAccountSummariesWithMandatoryParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testUpdateAccountUserLinkWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateAccountUserLink");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateAccountUserLink_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/entityUserLinks/" + connectorProperties.getProperty("userLinkId");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_updateAccountUserLink_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for deleteAccountUserLink method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {deleteAccountUserLink} integration test with mandatory parameters.", dependsOnMethods = {
            "testUpdateAccountUserLinkWithMandatoryParameters", "testListAccountSummariesWithMandatoryParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testDeleteAccountUserLinkWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteAccountUserLink");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteAccountUserLink_mandatory.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/entityUserLinks/" + connectorProperties.getProperty("userLinkId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Method name: deleteAccountUserLink 
     * Test scenario: Optional Reason to skip: The optional query parameters cannot be used to test the functionality of the method.
     */
    
    /**
     * Negative test case for deleteAccountUserLink method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {deleteAccountUserLink} integration test with negative case.", dependsOnMethods = {
            "testListAccountSummariesWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testDeleteAccountUserLinkWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteAccountUserLink");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteAccountUserLink_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/entityUserLinks/invalid";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "DELETE", apiRequestHeadersMap,
                        "api_deleteAccountUserLink_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for listSegments method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listSegments} integration test with mandatory parameters.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
    public void testListSegmentsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listSegments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSegments_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint = apiEndpointUrl + "/management/segments";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();

        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("username"), apiResponse.getString("username"));
        Assert.assertEquals(esbResponse.getString("totalResults"), apiResponse.getString("totalResults"));
        Assert.assertEquals(esbResponse.getString("itemsPerPage"), apiResponse.getString("itemsPerPage"));
    }
    
    /**
     * Positive test case for listSegments method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listSegments} integration test with optional parameters.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
    public void testListSegmentsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listSegments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSegments_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/segments?max-results="
                        + connectorProperties.getProperty("segmentMaxResults") + "&start-index="
                        + connectorProperties.getProperty("segmentStartIndex");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(connectorProperties.getProperty("segmentStartIndex"), apiResponse.getString("startIndex"));
        Assert.assertEquals(connectorProperties.getProperty("segmentMaxResults"), apiResponse.getString("itemsPerPage"));
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("username"), apiResponse.getString("username"));
    }
    
    /**
     * Negative test case for listSegments method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listSegments} integration test with negative case.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
    public void testListSegmentsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listSegments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSegments_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/management/segments?max-results=abc";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for listCustomDataSources method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listCustomDataSources} integration test with mandatory parameters.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
    public void testListCustomDataSourcesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCustomDataSources");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomDataSources_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customDataSources";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("username"), apiResponse.getString("username"));
        Assert.assertEquals(esbResponse.getString("totalResults"), apiResponse.getString("totalResults"));
        Assert.assertEquals(esbResponse.getString("itemsPerPage"), apiResponse.getString("itemsPerPage"));
    }
    
    /**
     * Positive test case for listCustomDataSources method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listCustomDataSources} integration test with optional parameters.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
    public void testListCustomDataSourcesWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCustomDataSources");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomDataSources_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
       
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId")
                        + "/customDataSources?max-results="
                        + connectorProperties.getProperty("customDataSourceMaxResults") + "&start-index="
                        + connectorProperties.getProperty("customDataSourceStartIndex");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(connectorProperties.getProperty("customDataSourceStartIndex"),
                apiResponse.getString("startIndex"));
        Assert.assertEquals(connectorProperties.getProperty("customDataSourceMaxResults"),
                apiResponse.getString("itemsPerPage"));
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("username"), apiResponse.getString("username"));
    }
    
    /**
     * Negative test case for listCustomDataSources method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listCustomDataSources} integration test with negative case.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
    public void testListCustomDataSourcesWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCustomDataSources");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomDataSources_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId")
                        + "/customDataSources?max-results=abc";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for createAdWordsLink method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {createAdWordsLink} integration test with mandatory parameters.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
    public void testCreateAdWordsLinkWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAdWordsLink");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAdWordsLink_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String adWordsLinkId = esbResponse.getString("id");
        connectorProperties.put("adWordsLinkId", adWordsLinkId);
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/entityAdWordsLinks/"
                        + adWordsLinkId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(connectorProperties.getProperty("adWordsLinkName"), apiResponse.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("adWordsLinkCustomerId"),
                apiResponse.getJSONArray("adWordsAccounts").getJSONObject(0).getString("customerId"));
        Assert.assertEquals(esbResponse.getString("selfLink"), apiResponse.getString("selfLink"));
    }
    
    /**
     * Method name: createAdWordsLink 
     * Test scenario: Optional 
     * Reason to skip: No optional parameters to be created.
     */
    
    /**
     * Negative test case for createAdWordsLink method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {createAdWordsLink} integration test with negative case.", dependsOnMethods = {
            "testCreateAdWordsLinkWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testCreateAdWordsLinkWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAdWordsLink");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAdWordsLink_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/entityAdWordsLinks";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createAdWordsLink_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for getAdWordsLink method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {getAdWordsLink} integration test with mandatory parameters.", dependsOnMethods = {
            "testGetAccessTokenFromRefreshToken", "testCreateAdWordsLinkWithMandatoryParameters" })
    public void testGetAdWordsLinkWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAdWordsLink");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAdWordsLink_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/entityAdWordsLinks/"
                        + connectorProperties.getProperty("adWordsLinkId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
       
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(esbResponse.getString("selfLink"), apiResponse.getString("selfLink"));
    }
    
    /**
     * Positive test case for getAdWordsLink method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {getAdWordsLink} integration test with optional parameters.", dependsOnMethods = {
            "testGetAccessTokenFromRefreshToken", "testCreateAdWordsLinkWithMandatoryParameters" })
    public void testGetAdWordsLinkWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAdWordsLink");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAdWordsLink_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/entityAdWordsLinks/"
                        + connectorProperties.getProperty("adWordsLinkId") + "?prettyPrint="
                        + connectorProperties.getProperty("prettyPrint") + "&fields="
                        + connectorProperties.getProperty("fields");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final String apiEndpointNoFields =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/entityAdWordsLinks/"
                        + connectorProperties.getProperty("adWordsLinkId");
        RestResponse<JSONObject> apiRestResponseWithField =
                sendJsonRestRequest(apiEndpointNoFields, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertFalse(esbResponse.has("selfLink"));
        Assert.assertFalse(apiResponse.has("selfLink"));
        Assert.assertTrue(apiRestResponseWithField.getBody().has("selfLink"));
        
    }
    
    /**
     * Negative test case for getAdWordsLink method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {getAdWordsLink} integration test with negative case.", dependsOnMethods = {
            "testGetAdWordsLinkWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testGetAdWordsLinkWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAdWordsLink");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAdWordsLink_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId")
                        + "/entityAdWordsLinks/invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 403);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 403);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for listAdWordsLinks method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listAdWordsLinks} integration test with mandatory parameters.", dependsOnMethods = {
            "testGetAdWordsLinkWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testListAdWordsLinksWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listAdWordsLinks");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAdWordsLinks_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/entityAdWordsLinks";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("totalResults"), apiResponse.getString("totalResults"));
        Assert.assertEquals(esbResponse.getString("itemsPerPage"), apiResponse.getString("itemsPerPage"));
        Assert.assertEquals(esbResponse.getJSONArray("items").length(), apiResponse.getJSONArray("items").length());
        
    }
    
    /**
     * Positive test case for listAdWordsLinks method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listAdWordsLinks} integration test with optional parameters.", dependsOnMethods = {
            "testGetAdWordsLinkWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testListAdWordsLinksWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listAdWordsLinks");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAdWordsLinks_optional.json");
       
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId")
                        + "/entityAdWordsLinks?max-results=" + connectorProperties.getProperty("adWordsLinkMaxResults")
                        + "&start-index=" + connectorProperties.getProperty("adWordsLinkStartIndex");
     
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(connectorProperties.getProperty("adWordsLinkMaxResults"),
                apiResponse.getString("itemsPerPage"));
        Assert.assertEquals(connectorProperties.getProperty("adWordsLinkStartIndex"),
                apiResponse.getString("startIndex"));
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("totalResults"), apiResponse.getString("totalResults"));
        
    }
    
    /**
     * Negative test case for listAdWordsLinks method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listAdWordsLinks} integration test with negative case.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
    public void testListAdWordsLinksWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listAdWordsLinks");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAdWordsLinks_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId")
                        + "/entityAdWordsLinks?max-results=abc";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for updateAdWordsLink method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {updateAdWordsLink} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateAdWordsLinkWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testUpdateAdWordsLinkWithMandatoryParameters() throws IOException, JSONException {
    
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/entityAdWordsLinks/"
                        + connectorProperties.getProperty("adWordsLinkId");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponseBefore = apiRestResponseBefore.getBody();
       
        esbRequestHeadersMap.put("Action", "urn:updateAdWordsLink");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateAdWordsLink_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponseAfter = apiRestResponseAfter.getBody();
        
        Assert.assertNotEquals(apiResponseBefore.getString("name"), apiResponseAfter.getString("name"));
        
        Assert.assertEquals(connectorProperties.getProperty("adWordsLinkUpdatedName"),
                apiResponseAfter.getString("name"));
    }
    
    /**
     * Method name: updateAdWordsLink 
     * Test scenario: Optional 
     * Reason to skip: No optional parameters to be updated.
     */
    
    /**
     * Negative test case for updateAdWordsLink method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {updateAdWordsLink} integration test with negative case.", dependsOnMethods = {
            "testCreateAdWordsLinkWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testUpdateAdWordsLinkWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateAdWordsLink");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateAdWordsLink_negative.json");
       
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId")
                        + "/entityAdWordsLinks/invalid";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_updateAdWordsLink_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Method name: patchAdWordsLink 
     * Test scenario: mandatory 
     * Reason to skip: No optional parameters to be updated.
     */
    /**
     * Positive test case for patchAdWordsLink method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {patchAdWordsLink} integration test with optional parameters.", dependsOnMethods = {
            "testCreateAdWordsLinkWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testPatchAdWordsLinkWithOptinalParameters() throws IOException, JSONException {
    
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/entityAdWordsLinks/"
                        + connectorProperties.getProperty("adWordsLinkId");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        esbRequestHeadersMap.put("Action", "urn:patchAdWordsLink");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_patchAdWordsLink_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponseAfter = apiRestResponseAfter.getBody();
        
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("name"), apiRestResponseAfter.getBody()
                .getString("name"));
        
        Assert.assertEquals(connectorProperties.getProperty("adWordsLinkPatchName"), apiResponseAfter.getString("name"));
        
    }
    
    /**
     * Negative test case for patchAdWordsLink method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {patchAdWordsLink} integration test with negative case.", dependsOnMethods = {
            "testCreateAdWordsLinkWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testPatchAdWordsLinkWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:patchAdWordsLink");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_patchAdWordsLink_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/entityAdWordsLinks/"
                        + connectorProperties.getProperty("adWordsLinkId");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_patchAdWordsLink_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for createCustomDimension method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {createCustomDimension} integration test with mandatory parameters.", dependsOnMethods = {
            "testListAccountSummariesWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testCreateCustomDimensionWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCustomDimension");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomDimension_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String dimensionId = esbResponse.getString("id");
        connectorProperties.put("dimensionId", dimensionId);
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customDimensions/"
                        + dimensionId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(esbResponse.getString("selfLink"), apiResponse.getString("selfLink"));
        Assert.assertEquals(connectorProperties.getProperty("dimensionName"), apiResponse.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("dimensionScope"), apiResponse.getString("scope"));
        Assert.assertEquals(connectorProperties.getProperty("dimensionActive"), apiResponse.getString("active"));
    }
    
    /**
     * Positive test case for createCustomDimension method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {createCustomDimension} integration test with optional parameters.", dependsOnMethods = {
            "testListAccountSummariesWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testCreateCustomDimensionWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCustomDimension");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomDimension_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
     
        final String dimensionIdOpt = esbResponse.getString("id");
        
        connectorProperties.put("dimensionIdOpt", dimensionIdOpt);
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customDimensions/"
                        + dimensionIdOpt + "?fields=" + connectorProperties.getProperty("fields");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final String apiEndpoint1 =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customDimensions/"
                        + dimensionIdOpt;
        RestResponse<JSONObject> apiRestResponseWithField =
                sendJsonRestRequest(apiEndpoint1, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertFalse(esbResponse.has("selfLink"));
        Assert.assertFalse(apiResponse.has("selfLink"));
        Assert.assertTrue(apiRestResponseWithField.getBody().has("selfLink"));
    }
    
    /**
     * Negative test case for createCustomDimension method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {createCustomDimension} integration test with negative case.", dependsOnMethods = {
            "testCreateCustomDimensionWithMandatoryParameters", "testListAccountSummariesWithMandatoryParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testCreateDimensionWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCustomDimension");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomDimension_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customDimensions";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                        "api_createCustomDimension_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for getCustomDimension method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {getCustomDimension} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateCustomDimensionWithMandatoryParameters", "testListAccountSummariesWithMandatoryParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testGetCustomDimensionWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getCustomDimension");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCustomDimension_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customDimensions/"
                        + connectorProperties.getProperty("dimensionId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(esbResponse.getString("selfLink"), apiResponse.getString("selfLink"));
        Assert.assertEquals(esbResponse.getString("name"), apiResponse.getString("name"));
        Assert.assertEquals(esbResponse.getString("created"), apiResponse.getString("created"));
    }
    
    /**
     * Positive test case for getCustomDimension method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {getCustomDimension} integration test with optional parameters.", dependsOnMethods = {
            "testCreateCustomDimensionWithMandatoryParameters", "testListAccountSummariesWithMandatoryParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testGetCustomDimensionWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getCustomDimension");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCustomDimension_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customDimensions/"
                        + connectorProperties.getProperty("dimensionId") + "?fields="
                        + connectorProperties.getProperty("fields");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final String apiEndpoint1 =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customDimensions/"
                        + connectorProperties.getProperty("dimensionId");
        RestResponse<JSONObject> apiRestResponseWithField =
                sendJsonRestRequest(apiEndpoint1, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertFalse(esbResponse.has("selfLink"));
        Assert.assertFalse(apiResponse.has("selfLink"));
        Assert.assertTrue(apiRestResponseWithField.getBody().has("selfLink"));
        
    }
    
    /**
     * Negative test case for getCustomDimension method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {getCustomDimension} integration test with negative case.", dependsOnMethods = {
            "testListAccountSummariesWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testGetCustomDimensionWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getCustomDimension");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCustomDimension_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId")
                        + "/customDimensions/invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for listCustomDimensions method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listCustomDimensions} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateCustomDimensionWithMandatoryParameters", "testListAccountSummariesWithMandatoryParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testListCustomDimensionsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCustomDimensions");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomDimension_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customDimensions";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("username"), apiResponse.getString("username"));
        Assert.assertEquals(esbResponse.getString("itemsPerPage"), apiResponse.getString("itemsPerPage"));
        Assert.assertEquals(esbResponse.getString("totalResults"), apiResponse.getString("totalResults"));
    }
    
    /**
     * Positive test case for listCustomDimensions method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listCustomDimensions} integration test with optional parameters.", dependsOnMethods = {
            "testCreateCustomDimensionWithMandatoryParameters", "testCreateCustomDimensionWithOptionalParameters",
            "testListAccountSummariesWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testListCustomDimensionsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCustomDimensions");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomDimensions_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId")
                        + "/customDimensions?max-results=" + connectorProperties.getProperty("coreReportMaxResults")
                        + "&start-index=" + connectorProperties.getProperty("coreReportStartIndex")
                        + "&prettyPrint=true" + "&fields=" + connectorProperties.getProperty("listFields");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(connectorProperties.getProperty("coreReportStartIndex"),
                apiResponse.getString("startIndex"));
        Assert.assertEquals(esbResponse.getJSONArray("items").length(),
                Integer.parseInt(connectorProperties.getProperty("coreReportMaxResults")));
        Assert.assertEquals(esbResponse.getJSONArray("items").length(), apiResponse.getJSONArray("items").length());
    }
    
    /**
     * Negative test case for listCustomDimensions method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listCustomDimensions} integration test with negative case.", dependsOnMethods = {
            "testListAccountSummariesWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testListCustomDimensionsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCustomDimensions");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomDimensions_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId")
                        + "/customDimensions?max-results=abc&start-index="
                        + connectorProperties.getProperty("coreReportStartIndex") + "&prettyPrint=true" + "&fields="
                        + connectorProperties.getProperty("listFields");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for updateCustomDimension method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {updateCustomDimension} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateCustomDimensionWithMandatoryParameters", "testListAccountSummariesWithMandatoryParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testUpdateCustomDimensionWithMandatoryParameters() throws IOException, JSONException {
    
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customDimensions/"
                        + connectorProperties.getProperty("dimensionId");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        esbRequestHeadersMap.put("Action", "urn:updateCustomDimension");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCustomDimension_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponseBefore = apiRestResponseBefore.getBody();
        final JSONObject apiResponseAfter = apiRestResponseAfter.getBody();
        
        Assert.assertNotEquals(apiResponseBefore.getString("name"), apiResponseAfter.getString("name"));
        Assert.assertNotEquals(apiResponseBefore.getString("scope"), apiResponseAfter.getString("scope"));
        Assert.assertNotEquals(apiResponseBefore.getString("active"), apiResponseAfter.getString("active"));
        
        Assert.assertEquals(connectorProperties.getProperty("updatedDimensionName"), apiResponseAfter.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("updatedDimensionScope"),
                apiResponseAfter.getString("scope"));
        Assert.assertEquals(connectorProperties.getProperty("updatedDimensionActive"),
                apiResponseAfter.getString("active"));
        
    }
    
    /**
     * Method name: updateCustomDimension 
     * Test scenario: Optional 
     * Reason to skip: The optional query parameters cannot be used to test the functionality of the method.
     */
    
    /**
     * Negative test case for updateCustomDimension method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {updateCustomDimension} integration test with negative case.", dependsOnMethods = {
            "testUpdateCustomDimensionWithMandatoryParameters", "testListAccountSummariesWithMandatoryParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testUpdateCustomDimensionWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateCustomDimension");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCustomDimension_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customDimensions/"
                        + connectorProperties.getProperty("dimensionId");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_updateCustomDimension_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Method name: patchCustomDimension 
     * Test scenario: Mandatory 
     * Reason to skip: The are no mandatory parameters to be updated.
     */
    
    /**
     * Positive test case for patchCustomDimension method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {patchCustomDimension} integration test with optional parameters.", dependsOnMethods = {
            "testCreateCustomDimensionWithOptionalParameters", "testListAccountSummariesWithMandatoryParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testPatchCustomDimensionWithOptinalParameters() throws IOException, JSONException {
    
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customDimensions/"
                        + connectorProperties.getProperty("dimensionIdOpt");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        esbRequestHeadersMap.put("Action", "urn:patchCustomDimension");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_patchCustomDimension_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponseBefore = apiRestResponseBefore.getBody();
        final JSONObject apiResponseAfter = apiRestResponseAfter.getBody();
        
        Assert.assertNotEquals(apiResponseBefore.getString("name"), apiResponseAfter.getString("name"));
        Assert.assertNotEquals(apiResponseBefore.getString("scope"), apiResponseAfter.getString("scope"));
        Assert.assertNotEquals(apiResponseBefore.getString("active"), apiResponseAfter.getString("active"));
        
        Assert.assertEquals(connectorProperties.getProperty("patchDimensionName"), apiResponseAfter.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("patchDimensionScope"), apiResponseAfter.getString("scope"));
        Assert.assertEquals(connectorProperties.getProperty("patchDimensionActive"),
                apiResponseAfter.getString("active"));
        
    }
    
    /**
     * Negative test case for patchCustomDimension method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {patchCustomDimension} integration test with negative case.", dependsOnMethods = {
            "testCreateCustomDimensionWithOptionalParameters", "testListAccountSummariesWithMandatoryParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testPatchCustomDimensionWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:patchCustomDimension");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_patchCustomDimension_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customDimensions/"
                        + connectorProperties.getProperty("dimensionId");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_patchCustomDimension_negative.json");
        
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for createCustomMetrics method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {createCustomMetrics} integration test with mandatory parameters.", dependsOnMethods = {
            "testListAccountSummariesWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testCreateCustomMetricsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCustomMetrics");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomMetrics_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String metricsId = esbResponse.getString("id");
        connectorProperties.put("metricsId", metricsId);
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customMetrics/"
                        + metricsId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(esbResponse.getString("selfLink"), apiResponse.getString("selfLink"));
        Assert.assertEquals(connectorProperties.getProperty("metricsName"), apiResponse.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("metricsScope"), apiResponse.getString("scope"));
        Assert.assertEquals(connectorProperties.getProperty("metricsType"), apiResponse.getString("type"));
        Assert.assertEquals(esbResponse.getString("created"), apiResponse.getString("created"));
    }
    
    /**
     * Positive test case for createCustomMetrics method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {createCustomMetrics} integration test with optional parameters.", dependsOnMethods = {
            "testListAccountSummariesWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testCreateCustomMetricsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCustomMetrics");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomMetrics_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String metricsOptId = esbResponse.getString("id");
        connectorProperties.put("metricsOptId", metricsOptId);
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customMetrics/"
                        + metricsOptId + "?fields=" + connectorProperties.getProperty("fields");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final String apiEndpoint1 =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customMetrics/"
                        + metricsOptId;
        RestResponse<JSONObject> apiRestResponseWithField =
                sendJsonRestRequest(apiEndpoint1, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertFalse(esbResponse.has("selfLink"));
        Assert.assertFalse(apiResponse.has("selfLink"));
        Assert.assertTrue(apiRestResponseWithField.getBody().has("selfLink"));
    }
    
    /**
     * Negative test case for createCustomMetrics method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {createCustomMetrics} integration test with negative case.", dependsOnMethods = {
            "testCreateCustomDimensionWithMandatoryParameters", "testListAccountSummariesWithMandatoryParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testCreateCustomMetricsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCustomMetrics");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomMetrics_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customMetrics";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createCustomMetrics_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for getCustomMetrics method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {getCustomMetrics} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateCustomMetricsWithMandatoryParameters", "testListAccountSummariesWithMandatoryParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testGetCustomMetricsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getCustomMetrics");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCustomMetrics_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customMetrics/"
                        + connectorProperties.getProperty("metricsId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(esbResponse.getString("selfLink"), apiResponse.getString("selfLink"));
        Assert.assertEquals(esbResponse.getString("name"), apiResponse.getString("name"));
        Assert.assertEquals(esbResponse.getString("created"), apiResponse.getString("created"));
    }
    
    /**
     * Positive test case for getCustomMetrics method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {getCustomMetrics} integration test with optional parameters.", dependsOnMethods = {
            "testCreateCustomMetricsWithMandatoryParameters", "testListAccountSummariesWithMandatoryParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testGetCustomMetricsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getCustomMetrics");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCustomMetrics_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customMetrics/"
                        + connectorProperties.getProperty("metricsId") + "?fields="
                        + connectorProperties.getProperty("fields");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final String apiEndpoint1 =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customMetrics/"
                        + connectorProperties.getProperty("metricsId");
        RestResponse<JSONObject> apiRestResponseWithField =
                sendJsonRestRequest(apiEndpoint1, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertFalse(esbResponse.has("selfLink"));
        Assert.assertFalse(apiResponse.has("selfLink"));
        Assert.assertTrue(apiRestResponseWithField.getBody().has("selfLink"));
        
    }
    
    /**
     * Negative test case for getCustomMetrics method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {getCustomMetrics} integration test with negative case.", dependsOnMethods = {
            "testListAccountSummariesWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testGetCustomMetricsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getCustomMetrics");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCustomMetrics_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId")
                        + "/customMetrics/invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for listCustomMetrics method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listCustomMetrics} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateCustomMetricsWithMandatoryParameters", "testListAccountSummariesWithMandatoryParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testListCustomMetricsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCustomMetrics");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomMetrics_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customMetrics";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("username"), apiResponse.getString("username"));
        Assert.assertEquals(esbResponse.getString("itemsPerPage"), apiResponse.getString("itemsPerPage"));
        Assert.assertEquals(esbResponse.getString("totalResults"), apiResponse.getString("totalResults"));
    }
    
    /**
     * Positive test case for listCustomMetrics method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listCustomMetrics} integration test with optional parameters.", dependsOnMethods = {
            "testCreateCustomMetricsWithOptionalParameters", "testCreateCustomMetricsWithMandatoryParameters",
            "testListAccountSummariesWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testListCustomMetricsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCustomMetrics");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomMetrics_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId")
                        + "/customMetrics?max-results=" + connectorProperties.getProperty("coreReportMaxResults")
                        + "&start-index=" + connectorProperties.getProperty("coreReportStartIndex")
                        + "&prettyPrint=true" + "&fields=" + connectorProperties.getProperty("listFields");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(connectorProperties.getProperty("coreReportStartIndex"),
                apiResponse.getString("startIndex"));
        Assert.assertEquals(esbResponse.getJSONArray("items").length(),
                Integer.parseInt(connectorProperties.getProperty("coreReportMaxResults")));
        Assert.assertEquals(esbResponse.getJSONArray("items").length(), apiResponse.getJSONArray("items").length());
    }
    
    /**
     * Negative test case for listCustomMetrics method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listCustomMetrics} integration test with negative case.", dependsOnMethods = {
            "testListAccountSummariesWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testListCustomMetricsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCustomMetrics");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomMetrics_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId")
                        + "/customMetrics?max-results=abc&start-index="
                        + connectorProperties.getProperty("coreReportStartIndex") + "&prettyPrint=true" + "&fields="
                        + connectorProperties.getProperty("listFields");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for updateCustomMetrics method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {updateCustomMetrics} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateCustomMetricsWithMandatoryParameters", "testListAccountSummariesWithMandatoryParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testUpdateCustomMetricsWithMandatoryParameters() throws IOException, JSONException {
    
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customMetrics/"
                        + connectorProperties.getProperty("metricsId");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        esbRequestHeadersMap.put("Action", "urn:updateCustomMetrics");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCustomMetrics_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponseBefore = apiRestResponseBefore.getBody();
        final JSONObject apiResponseAfter = apiRestResponseAfter.getBody();
        
        Assert.assertNotEquals(apiResponseBefore.getString("name"), apiResponseAfter.getString("name"));
        Assert.assertNotEquals(apiResponseBefore.getString("scope"), apiResponseAfter.getString("scope"));
        Assert.assertNotEquals(apiResponseBefore.getString("active"), apiResponseAfter.getString("active"));
        
        Assert.assertEquals(connectorProperties.getProperty("updatedMetricsName"), apiResponseAfter.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("updatedMetricsScope"), apiResponseAfter.getString("scope"));
        Assert.assertEquals(connectorProperties.getProperty("updatedMetricsActive"),
                apiResponseAfter.getString("active"));
        
    }
    
    /**
     * Method name: updateCustomDimension 
     * Test scenario: Optional 
     * Reason to skip: The optional query parameters cannot be used to test the functionality of the method.
     */
    
    /**
     * Negative test case for updateCustomMetrics method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {updateCustomMetrics} integration test with negative case.", dependsOnMethods = {
            "testUpdateCustomMetricsWithMandatoryParameters", "testListAccountSummariesWithMandatoryParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testUpdateCustomMetricsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateCustomMetrics");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCustomMetrics_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customMetrics/"
                        + connectorProperties.getProperty("metricsId");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_updateCustomMetrics_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Method name: patchCustomMetrics 
     * Test scenario: Mandatory 
     * Reason to skip: The are no mandatory parameters to be updated.
     */
    
    /**
     * Positive test case for patchCustomMetrics method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {patchCustomMetrics} integration test with optional parameters.", dependsOnMethods = {
            "testCreateCustomMetricsWithOptionalParameters", "testListAccountSummariesWithMandatoryParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testPatchCustomMetricsWithOptinalParameters() throws IOException, JSONException {
    
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customMetrics/"
                        + connectorProperties.getProperty("metricsOptId");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        esbRequestHeadersMap.put("Action", "urn:patchCustomMetrics");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_patchCustomMetrics_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponseBefore = apiRestResponseBefore.getBody();
        final JSONObject apiResponseAfter = apiRestResponseAfter.getBody();
        
        Assert.assertNotEquals(apiResponseBefore.getString("name"), apiResponseAfter.getString("name"));
        Assert.assertNotEquals(apiResponseBefore.getString("scope"), apiResponseAfter.getString("scope"));
        Assert.assertNotEquals(apiResponseBefore.getString("active"), apiResponseAfter.getString("active"));
        
        Assert.assertEquals(connectorProperties.getProperty("updatedMetricsName"), apiResponseAfter.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("updatedMetricsScope"), apiResponseAfter.getString("scope"));
        Assert.assertEquals(connectorProperties.getProperty("updatedMetricsActive"),
                apiResponseAfter.getString("active"));
        
    }
    
    /**
     * Negative test case for patchCustomMetrics method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {patchCustomMetrics} integration test with negative case.", dependsOnMethods = {
            "testUpdateCustomMetricsWithMandatoryParameters", "testListAccountSummariesWithMandatoryParameters",
            "testGetAccessTokenFromRefreshToken" })
    public void testPatchCustomMetricsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:patchCustomMetrics");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_patchCustomMetrics_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/customMetrics/"
                        + connectorProperties.getProperty("metricsId");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_patchCustomMetrics_negative.json");
        
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for createExperiment method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {createExperiment} integration test with mandatory parameters.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
    public void testCreateExperimentWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createExperiment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createExperiments_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String experimentId = esbResponse.getString("id");
        connectorProperties.put("experimentId", experimentId);
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/profiles/"
                        + connectorProperties.getProperty("experimentProfileId") + "/experiments/" + experimentId;
       
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(esbResponse.getString("internalWebPropertyId"),
                apiResponse.getString("internalWebPropertyId"));
        Assert.assertEquals(connectorProperties.getProperty("experimentName"), apiResponse.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("experimentStatus"), apiResponse.getString("status"));
        
    }
    
    /**
     * Positive test case for createExperiment method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {createExperiment} integration test with optional parameters.", dependsOnMethods = { "testGetAccessTokenFromRefreshToken" })
    public void testCreateExperimentWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createExperiment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createExperiments_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String experimentId = esbResponse.getString("id");
        connectorProperties.put("experimentIdOptional", experimentId);
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/profiles/"
                        + connectorProperties.getProperty("experimentProfileId") + "/experiments/" + experimentId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(connectorProperties.getProperty("experimentName"), apiResponse.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("experimentStatus"), apiResponse.getString("status"));
        Assert.assertEquals(connectorProperties.getProperty("experimentDescription"),
                apiResponse.getString("description"));
        
    }
    
    /**
     * Negative test case for createExperiment method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {createExperiment} integration test with negative case.", dependsOnMethods = {
            "testCreateExperimentWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testCreateExperimentWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createExperiment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createExperiments_negative.json");
       
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/profiles/"
                        + connectorProperties.getProperty("experimentProfileId") + "/experiments";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createExperiments_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for getExperiment method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {getExperiment} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateExperimentWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testGetExperimentWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getExperiment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getExperiments_mandatory.json");
       
        final JSONObject esbResponse = esbRestResponse.getBody();
      
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/profiles/"
                        + connectorProperties.getProperty("experimentProfileId") + "/experiments/"
                        + connectorProperties.getProperty("experimentId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(esbResponse.getString("selfLink"), apiResponse.getString("selfLink"));
        Assert.assertEquals(esbResponse.getString("status"), apiResponse.getString("status"));
    }
    
    /**
     * Positive test case for getExperiment method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {getExperiment} integration test with optional parameters.", dependsOnMethods = {
            "testCreateExperimentWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testGetExperimentWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getExperiment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getExperiments_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/profiles/"
                        + connectorProperties.getProperty("experimentProfileId") + "/experiments/"
                        + connectorProperties.getProperty("experimentId") + "?prettyPrint="
                        + connectorProperties.getProperty("prettyPrint") + "&fields="
                        + connectorProperties.getProperty("fields");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final String apiEndpointNoFields =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/profiles/"
                        + connectorProperties.getProperty("experimentProfileId") + "/experiments/"
                        + connectorProperties.getProperty("experimentId");
        RestResponse<JSONObject> apiRestResponseWithField =
                sendJsonRestRequest(apiEndpointNoFields, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertFalse(esbResponse.has("selfLink"));
        Assert.assertFalse(apiResponse.has("selfLink"));
        Assert.assertTrue(apiRestResponseWithField.getBody().has("selfLink"));
        
    }
    
    /**
     * Negative test case for getExperiment method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {getExperiment} integration test with negative case.", dependsOnMethods = {
            "testGetExperimentWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testGetExperimentWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getExperiment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getExperiments_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/profiles/"
                        + connectorProperties.getProperty("experimentProfileId") + "/experiments/invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for listExperiments method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listExperiments} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateExperimentWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testListExperimentsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listExperiments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listExperiments_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/profiles/"
                        + connectorProperties.getProperty("experimentProfileId") + "/experiments";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("username"), apiResponse.getString("username"));
        Assert.assertEquals(esbResponse.getString("totalResults"), apiResponse.getString("totalResults"));
        Assert.assertEquals(esbResponse.getString("itemsPerPage"), apiResponse.getString("itemsPerPage"));
    }
    
    /**
     * Positive test case for listExperiments method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listExperiments} integration test with optional parameters.", dependsOnMethods = {
            "testCreateExperimentWithMandatoryParameters", "testGetAccessTokenFromRefreshToken",
            "testCreateExperimentWithOptionalParameters" })
    public void testListExperimentsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listExperiments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listExperiments_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/profiles/"
                        + connectorProperties.getProperty("experimentProfileId") + "/experiments?max-results="
                        + connectorProperties.getProperty("experimentMaxResults") + "&start-index="
                        + connectorProperties.getProperty("experimentStartIndex");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(connectorProperties.getProperty("experimentStartIndex"),
                apiResponse.getString("startIndex"));
        Assert.assertEquals(connectorProperties.getProperty("experimentMaxResults"),
                apiResponse.getString("itemsPerPage"));
        Assert.assertEquals(esbResponse.getString("startIndex"), apiResponse.getString("startIndex"));
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        
    }
    
    /**
     * Negative test case for listExperiments method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listExperiments} integration test with negative case.", dependsOnMethods = {
            "testCreateExperimentWithMandatoryParameters", "testGetAccessTokenFromRefreshToken",
            "testCreateExperimentWithOptionalParameters" })
    public void testListExperimentsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listExperiments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listExperiments_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/profiles/"
                        + connectorProperties.getProperty("experimentProfileId") + "/experiments?max-results=abc";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for updateExperiment method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {updateExperiment} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateExperimentWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testUpdateExperimentWithMandatoryParameters() throws IOException, JSONException {
    
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/profiles/"
                        + connectorProperties.getProperty("experimentProfileId") + "/experiments/"
                        + connectorProperties.getProperty("experimentId");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponseBefore = apiRestResponseBefore.getBody();
        
        esbRequestHeadersMap.put("Action", "urn:updateExperiment");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateExperiment_mandatory.json");
  
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponseAfter = apiRestResponseAfter.getBody();
        
        Assert.assertNotEquals(apiResponseBefore.getString("name"), apiResponseAfter.getString("name"));
        Assert.assertNotEquals(apiResponseBefore.getString("status"), apiResponseAfter.getString("status"));
        
        Assert.assertEquals(connectorProperties.getProperty("experimentUpdatedName"),
                apiResponseAfter.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("experimentUpdatedStatus"),
                apiResponseAfter.getString("status"));
        
    }
    
    /**
     * Positive test case for updateExperiment method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {updateExperiment} integration test with optional parameters.", dependsOnMethods = {
            "testCreateExperimentWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testUpdateExperimentWithOptionalParameters() throws IOException, JSONException {
    
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/profiles/"
                        + connectorProperties.getProperty("experimentProfileId") + "/experiments/"
                        + connectorProperties.getProperty("experimentId");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponseBefore = apiRestResponseBefore.getBody();
        
        esbRequestHeadersMap.put("Action", "urn:updateExperiment");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateExperiment_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponseAfter = apiRestResponseAfter.getBody();
        
        Assert.assertNotEquals(apiResponseBefore.getString("editableInGaUi"),
                apiResponseAfter.getString("editableInGaUi"));
        Assert.assertEquals(connectorProperties.getProperty("experimentUpdatedDescription"),
                apiResponseAfter.getString("description"));
        Assert.assertEquals(connectorProperties.getProperty("experimentUpdatedEditableInGaUi"),
                apiResponseAfter.getString("editableInGaUi"));
        
    }
    
    /**
     * Negative test case for updateExperiment method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {updateExperiment} integration test with negative case.", dependsOnMethods = {
            "testCreateExperimentWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testUpdateExperimentWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateExperiment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateExperiment_negative.json");
    
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/profiles/"
                        + connectorProperties.getProperty("experimentProfileId") + "/experiments/"
                        + connectorProperties.getProperty("experimentId");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_updateExperiment_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Method name: patchExperiment 
     * Test scenario: Mandatory 
     * Reason to skip: No mandatory parameters to be updated.
     */
    /**
     * Positive test case for patchExperiment method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {patchExperiment} integration test with optional parameters.", dependsOnMethods = {
            "testCreateExperimentWithOptionalParameters", "testGetAccessTokenFromRefreshToken" })
    public void testPatchExperimentWithOptionalParameters() throws IOException, JSONException {
    
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/profiles/"
                        + connectorProperties.getProperty("experimentProfileId") + "/experiments/"
                        + connectorProperties.getProperty("experimentIdOptional");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponseBefore = apiRestResponseBefore.getBody();
        
        esbRequestHeadersMap.put("Action", "urn:patchExperiment");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_patchExperiment_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponseAfter = apiRestResponseAfter.getBody();
        
        Assert.assertNotEquals(apiResponseBefore.getString("name"), apiResponseAfter.getString("name"));
        Assert.assertNotEquals(apiResponseBefore.getString("status"), apiResponseAfter.getString("status"));
        
        Assert.assertEquals(connectorProperties.getProperty("experimentUpdatedName"),
                apiResponseAfter.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("experimentUpdatedStatus"),
                apiResponseAfter.getString("status"));
        
    }
    
    /**
     * Negative test case for patchExperiment method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {patchExperiment} integration test with negative case.", dependsOnMethods = {
            "testCreateExperimentWithOptionalParameters", "testGetAccessTokenFromRefreshToken" })
    public void testPatchExperimentWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:patchExperiment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_patchExperiment_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/management/accounts/" + connectorProperties.getProperty("accountId")
                        + "/webproperties/" + connectorProperties.getProperty("webPropertyId") + "/profiles/"
                        + connectorProperties.getProperty("experimentProfileId") + "/experiments/"
                        + connectorProperties.getProperty("experimentIdOptional");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_patchExperiment_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for listMultiChannelFunnelsReportData method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listMultiChannelFunnelsReportData} integration test with mandatory parameters.", dependsOnMethods = {
            "testListAccountSummariesWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testListMultiChannelFunnelReportDataWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listMultiChannelFunnelsReportData");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_listMultiChannelFunnelData_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/data/mcf?ids=" + connectorProperties.getProperty("multiChannelIds") + "&start-date="
                        + connectorProperties.getProperty("multiChannelStartDate") + "&end-date="
                        + connectorProperties.getProperty("multiChannelEndDate") + "&metrics="
                        + connectorProperties.getProperty("multiChannelMetrics");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getJSONObject("query").getString("start-date"),
                apiResponse.getJSONObject("query").getString("start-date"));
        Assert.assertEquals(esbResponse.getJSONObject("query").getString("end-date"), apiResponse
                .getJSONObject("query").getString("end-date"));
        Assert.assertEquals(esbResponse.getString("itemsPerPage"), apiResponse.getString("itemsPerPage"));
        Assert.assertEquals(esbResponse.getString("totalResults"), apiResponse.getString("totalResults"));
    }
    
    /**
     * Positive test case for listMultiChannelFunnelsReportData method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listMultiChannelFunnelsReportData} integration test with optional parameters.", dependsOnMethods = {
            "testListAccountSummariesWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testListMultiChannelFunnelReportDataWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listMultiChannelFunnelsReportData");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_listMultiChannelFunnelData_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/data/mcf?ids=" + connectorProperties.getProperty("multiChannelIds") + "&start-date="
                        + connectorProperties.getProperty("multiChannelStartDate") + "&end-date="
                        + connectorProperties.getProperty("multiChannelEndDate") + "&metrics="
                        + connectorProperties.getProperty("multiChannelMetrics") + "&max-results="
                        + connectorProperties.getProperty("coreReportMaxResults") + "&start-index="
                        + connectorProperties.getProperty("coreReportStartIndex");;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(connectorProperties.getProperty("coreReportMaxResults"), apiResponse.getJSONObject("query")
                .getString("max-results"));
        Assert.assertEquals(connectorProperties.getProperty("coreReportStartIndex"), apiResponse.getJSONObject("query")
                .getString("start-index"));
        Assert.assertEquals(esbResponse.getString("itemsPerPage"), apiResponse.getString("itemsPerPage"));
        Assert.assertEquals(esbResponse.getString("totalResults"), apiResponse.getString("totalResults"));
    }
    
    /**
     * Negative test case for listMultiChannelFunnelsReportData method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listMultiChannelFunnelsReportData} integration test with negative case.", dependsOnMethods = {
            "testListAccountSummariesWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testListMultiChannelFunnelsReportDataWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listMultiChannelFunnelsReportData");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_listMultiChannelFunnelData_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/data/mcf?ids=" + connectorProperties.getProperty("multiChannelIds") + "&start-date="
                        + connectorProperties.getProperty("multiChannelStartDate") + "&end-date=invalid&metrics="
                        + connectorProperties.getProperty("multiChannelMetrics");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
    /**
     * Positive test case for listConfigurationData method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listConfigurationData} integration test with mandatory parameters.", dependsOnMethods = {
            "testListAccountSummariesWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testListConfigurationDataWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listConfigurationData");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listConfigurationData_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/metadata/" + connectorProperties.getProperty("reportType") + "/columns";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("etag"), apiResponse.getString("etag"));
        Assert.assertEquals(esbResponse.getString("totalResults"), apiResponse.getString("totalResults"));
        Assert.assertEquals(esbResponse.getJSONArray("items").length(), apiResponse.getJSONArray("items").length());
    }
    
    /**
     * Positive test case for listConfigurationData method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listConfigurationData} integration test with optional parameters.", dependsOnMethods = {
            "testListAccountSummariesWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testListConfigurationDataWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listConfigurationData");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listConfigurationData_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint =
                apiEndpointUrl + "/metadata/" + connectorProperties.getProperty("reportType") + "/columns?fields="
                        + connectorProperties.getProperty("configFields");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final String apiEndpoint1 =
                apiEndpointUrl + "/metadata/" + connectorProperties.getProperty("reportType") + "/columns";
        RestResponse<JSONObject> apiRestResponseWithoutFields =
                sendJsonRestRequest(apiEndpoint1, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(esbResponse.getString("kind"), apiResponse.getString("kind"));
        Assert.assertEquals(esbResponse.getString("etag"), apiResponse.getString("etag"));
        Assert.assertFalse(esbResponse.has("totalResults"));
        Assert.assertFalse(apiResponse.has("totalResults"));
        Assert.assertTrue(apiRestResponseWithoutFields.getBody().has("totalResults"));
    }
    
    /**
     * Negative test case for listConfigurationData method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "googleanalytics {listConfigurationData} integration test with negative case.", dependsOnMethods = {
            "testListAccountSummariesWithMandatoryParameters", "testGetAccessTokenFromRefreshToken" })
    public void testListConfigurationDataWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listConfigurationData");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listConfigurationData_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/metadata/invalid/columns";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }
    
}
