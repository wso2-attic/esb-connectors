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

package org.wso2.carbon.connector.integration.test.ronin;

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

public class RoninConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap;
    
    private Map<String, String> apiRequestHeadersMap;
    
    private String apiEndpointUrl;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        esbRequestHeadersMap = new HashMap<String, String>();
        
        apiRequestHeadersMap = new HashMap<String, String>();
        
        init("ronin-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Accept", "application/json");
        
        // Create base64-encoded auth string using apiToken
        final String apiToken = connectorProperties.getProperty("apiToken");
        final String authString = apiToken + ":" + apiToken;
        final String base64AuthString = Base64.encode(authString.getBytes());
        
        apiRequestHeadersMap.put("Authorization", "Basic " + base64AuthString);
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        apiEndpointUrl = connectorProperties.getProperty("apiUrl");
        
        checkPreRequisites();
        
    }
    
    /**
     * Method to check Pre-Requisites are completed or not. If not this is failing Test suite.
     * 
     * @throws JSONException
     * @throws IOException
     */
    private void checkPreRequisites() throws IOException, JSONException {
    
        // Check At least two estimates are created or not
        String apiEndpoint = apiEndpointUrl + "/estimates";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        if (!(apiRestResponse.getBody().getJSONArray("estimates").length() >= 2)) {
            Assert.fail("Pre-requisites are not compleatd. Please create atleast two Estimates.");
        }
        // Check At least two invoices are created or not
        apiEndpoint = apiEndpointUrl + "/invoices";
        apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        if (!(apiRestResponse.getBody().getJSONArray("invoices").length() >= 2)) {
            Assert.fail("Pre-requisites are not compleatd. Please create atleast two Invoices.");
        }
    }
    
    /**
     * Positive test case for createClient method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "ronin {createClient} integration test with mandatory parameters.")
    public void testCreateClientWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createClient");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createClient_mandatory.json");
        
        final String clientId = esbRestResponse.getBody().getString("id");
        
        final String apiEndpoint = apiEndpointUrl + "/clients/" + clientId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("cilentName"), apiRestResponse.getBody().getString("name"));
        
    }
    
    /**
     * Positive test case for createClient method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "ronin {createClient} integration test with optional parameters.")
    public void testCreateClientWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createClient");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createClient_optional.json");
        
        final String clientId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("clientId", clientId);
        
        final String apiEndpoint = apiEndpointUrl + "/clients/" + clientId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("cilentNameOpt"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("cilentAddress"),
                apiRestResponse.getBody().getString("address"));
        Assert.assertEquals(connectorProperties.getProperty("cilentAddress2"),
                apiRestResponse.getBody().getString("address_2"));
        Assert.assertEquals(connectorProperties.getProperty("cilentCity"), apiRestResponse.getBody().getString("city"));
        Assert.assertEquals(connectorProperties.getProperty("cilentCountry"),
                apiRestResponse.getBody().getString("country"));
        Assert.assertEquals(connectorProperties.getProperty("cilentState"), apiRestResponse.getBody()
                .getString("state"));
        
    }
    
    /**
     * Negative test case for createClient method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "ronin {createClient} integration test with negative case.")
    public void testCreateClientNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createClient");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createClient_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/clients/";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createClient_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("name").getString(0), apiRestResponse.getBody()
                .getJSONArray("name").getString(0));
        
    }
    
    /**
     * Positive test case for getClient method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateClientWithOptionalParameters" }, description = "ronin {getClient} integration test with mandatory parameters.")
    public void testGetClientWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getClient");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getClient_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/clients/" + connectorProperties.getProperty("clientId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("address"),
                apiRestResponse.getBody().getString("address"));
        Assert.assertEquals(esbRestResponse.getBody().getString("address_2"),
                apiRestResponse.getBody().getString("address_2"));
        Assert.assertEquals(esbRestResponse.getBody().getString("city"), apiRestResponse.getBody().getString("city"));
        Assert.assertEquals(esbRestResponse.getBody().getString("country"),
                apiRestResponse.getBody().getString("country"));
        Assert.assertEquals(esbRestResponse.getBody().getString("state"), apiRestResponse.getBody().getString("state"));
        
    }
    
    /**
     * Negative test case for getClient method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "ronin {getClient} integration test with negative case.")
    public void testGetClientNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getClient");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getClient_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/clients/INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for listClients method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateClientWithMandatoryParameters",
            "testCreateClientWithOptionalParameters" }, description = "ronin {listClients} integration test with mandatory parameters.")
    public void testListClientWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listClients");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listClients_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/clients";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject esbClientObject = esbRestResponse.getBody().getJSONArray("clients").getJSONObject(0);
        JSONObject apiClientObject = apiRestResponse.getBody().getJSONArray("clients").getJSONObject(0);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("page_size"),
                apiRestResponse.getBody().getString("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getString("page_count"),
                apiRestResponse.getBody().getString("page_count"));
        Assert.assertEquals(esbRestResponse.getBody().getString("total_count"),
                apiRestResponse.getBody().getString("total_count"));
        Assert.assertEquals(esbClientObject.getString("id"), apiClientObject.getString("id"));
        Assert.assertEquals(esbClientObject.getString("name"), apiClientObject.getString("name"));
        
    }
    
    /**
     * Positive test case for listClients method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateClientWithMandatoryParameters",
            "testCreateClientWithOptionalParameters" }, description = "ronin {listClients} integration test with optional parameters.")
    public void testListClientWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listClients");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listClients_optional.json");
        
        final String apiEndpoint = apiEndpointUrl + "/clients?page=2";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("page_size"),
                apiRestResponse.getBody().getString("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getString("page_count"),
                apiRestResponse.getBody().getString("page_count"));
        Assert.assertEquals(esbRestResponse.getBody().getString("total_count"),
                apiRestResponse.getBody().getString("total_count"));
        
    }
    
    /**
     * Positive test case for createContact method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateClientWithOptionalParameters" }, description = "ronin {createContact} integration test with mandatory parameters.")
    public void testCreateContactWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_mandatory.json");
        
        final String contactId = esbRestResponse.getBody().getString("id");
        
        final String apiEndpoint = apiEndpointUrl + "/contacts/" + contactId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("contactName"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("contactEmail"),
                apiRestResponse.getBody().getString("email"));
        
    }
    
    /**
     * Positive test case for createContact method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateClientWithOptionalParameters" }, description = "ronin {createContact} integration test with optional parameters.")
    public void testCreateContactWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_optional.json");
        
        final String contactId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("contactId", contactId);
        
        final String apiEndpoint = apiEndpointUrl + "/contacts/" + contactId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("contactNameOpt"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("contactEmailOpt"),
                apiRestResponse.getBody().getString("email"));
        Assert.assertEquals(connectorProperties.getProperty("contactTitleOpt"),
                apiRestResponse.getBody().getString("title"));
        Assert.assertEquals(connectorProperties.getProperty("contactMobileOpt"),
                apiRestResponse.getBody().getString("mobile"));
        Assert.assertEquals(connectorProperties.getProperty("contactPhoneOpt"),
                apiRestResponse.getBody().getString("phone"));
        Assert.assertEquals(connectorProperties.getProperty("contactExtOpt"), apiRestResponse.getBody()
                .getString("ext"));
        
    }
    
    /**
     * Negative test case for createContact method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateClientWithOptionalParameters" }, description = "ronin {createContact} integration test with negative case.")
    public void testCreateContactNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/clients/" + connectorProperties.getProperty("clientId") + "/contacts";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createContact_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("name").getString(0), apiRestResponse.getBody()
                .getJSONArray("name").getString(0));
        
    }
    
    /**
     * Positive test case for getContact method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, description = "ronin {getContact} integration test with mandatory parameters.")
    public void testGetContactWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContact_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/contacts/" + connectorProperties.getProperty("contactId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("email"), apiRestResponse.getBody().getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getString("ext"), apiRestResponse.getBody().getString("ext"));
        Assert.assertEquals(esbRestResponse.getBody().getString("mobile"), apiRestResponse.getBody()
                .getString("mobile"));
        Assert.assertEquals(esbRestResponse.getBody().getString("phone"), apiRestResponse.getBody().getString("phone"));
        Assert.assertEquals(esbRestResponse.getBody().getString("title"), apiRestResponse.getBody().getString("title"));
        
    }
    
    /**
     * Negative test case for getContact method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "ronin {getContact} integration test with negative case.")
    public void testGetContactNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContact_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/contacts/INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for listContacts method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithMandatoryParameters",
            "testCreateContactWithOptionalParameters" }, description = "ronin {listClients} integration test with mandatory parameters.")
    public void testListContactsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/contacts";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject esbContactObject = esbRestResponse.getBody().getJSONArray("contacts").getJSONObject(0);
        JSONObject apiContactObject = apiRestResponse.getBody().getJSONArray("contacts").getJSONObject(0);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("page_size"),
                apiRestResponse.getBody().getString("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getString("page_count"),
                apiRestResponse.getBody().getString("page_count"));
        Assert.assertEquals(esbRestResponse.getBody().getString("total_count"),
                apiRestResponse.getBody().getString("total_count"));
        Assert.assertEquals(esbContactObject.getString("id"), apiContactObject.getString("id"));
        Assert.assertEquals(esbContactObject.getString("name"), apiContactObject.getString("name"));
        
    }
    
    /**
     * Positive test case for listContacts method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithMandatoryParameters",
            "testCreateContactWithOptionalParameters" }, description = "ronin {listClients} integration test with optional parameters.")
    public void testListContactsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_optional.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/clients/" + connectorProperties.getProperty("clientId")
                        + "/contacts?page=2&page_size=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject esbContactObject = esbRestResponse.getBody().getJSONArray("contacts").getJSONObject(0);
        JSONObject apiContactObject = apiRestResponse.getBody().getJSONArray("contacts").getJSONObject(0);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("page_size"),
                apiRestResponse.getBody().getString("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getString("page_count"),
                apiRestResponse.getBody().getString("page_count"));
        Assert.assertEquals(esbRestResponse.getBody().getString("total_count"),
                apiRestResponse.getBody().getString("total_count"));
        Assert.assertEquals(esbContactObject.getString("id"), apiContactObject.getString("id"));
        Assert.assertEquals(esbContactObject.getString("name"), apiContactObject.getString("name"));
        
    }
    
    /**
     * Positive test case for createProject method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "ronin {createProject} integration test with mandatory parameters.")
    public void testCreateProjectWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createProject");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_mandatory.json");
        
        final String projectId = esbRestResponse.getBody().getString("id");
        
        final String apiEndpoint = apiEndpointUrl + "/projects/" + projectId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("projectName"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(Double.parseDouble(connectorProperties.getProperty("projectRate")),
                Double.parseDouble(apiRestResponse.getBody().getString("rate")));
        
    }
    
    /**
     * Positive test case for createProject method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "ronin {createProject} integration test with optional parameters.")
    public void testCreateProjectWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createProject");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_optional.json");
        
        final String projectId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("projectId", projectId);
        
        final String apiEndpoint = apiEndpointUrl + "/projects/" + projectId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("projectNameOpt"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(Double.parseDouble(connectorProperties.getProperty("projectRateOpt")),
                Double.parseDouble(apiRestResponse.getBody().getString("rate")));
        Assert.assertEquals(connectorProperties.getProperty("projectBudgetTypeOpt"), apiRestResponse.getBody()
                .getString("budget_type"));
        Assert.assertEquals(connectorProperties.getProperty("projectTypeOpt"),
                apiRestResponse.getBody().getString("project_type"));
        Assert.assertEquals(connectorProperties.getProperty("projectDescriptionOpt"), apiRestResponse.getBody()
                .getString("description"));
        Assert.assertEquals(connectorProperties.getProperty("projectCurrencyCodeOpt"), apiRestResponse.getBody()
                .getString("currency_code"));
        Assert.assertEquals(connectorProperties.getProperty("projectEndDateOpt"),
                apiRestResponse.getBody().getString("end_date"));
        
    }
    
    /**
     * Negative test case for createProject method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "ronin {createProject} integration test with negative case.")
    public void testCreateProjectNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createProject");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/projects";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createProject_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("name").getString(0), apiRestResponse.getBody()
                .getJSONArray("name").getString(0));
        
    }
    
    /**
     * Positive test case for getProject method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectWithOptionalParameters" }, description = "ronin {getProject} integration test with mandatory parameters.")
    public void testGetProjectWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getProject");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProject_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/projects/" + connectorProperties.getProperty("projectId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("rate"), apiRestResponse.getBody().getString("rate"));
        Assert.assertEquals(esbRestResponse.getBody().getString("budget_type"),
                apiRestResponse.getBody().getString("budget_type"));
        Assert.assertEquals(esbRestResponse.getBody().getString("project_type"),
                apiRestResponse.getBody().getString("project_type"));
        Assert.assertEquals(esbRestResponse.getBody().getString("description"),
                apiRestResponse.getBody().getString("description"));
        Assert.assertEquals(esbRestResponse.getBody().getString("currency_code"),
                apiRestResponse.getBody().getString("currency_code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("end_date"),
                apiRestResponse.getBody().getString("end_date"));
        
    }
    
    /**
     * Negative test case for getProject method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "ronin {getProject} integration test with negative case.")
    public void testGetProjectNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getProject");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProject_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/projects/INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for listProjects method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectWithMandatoryParameters",
            "testCreateProjectWithOptionalParameters" }, description = "ronin {listClients} integration test with mandatory parameters.")
    public void testListProjectsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listProjects");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjects_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/projects";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject esbProjectObject = esbRestResponse.getBody().getJSONArray("projects").getJSONObject(0);
        JSONObject apiProjectObject = apiRestResponse.getBody().getJSONArray("projects").getJSONObject(0);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("page_size"),
                apiRestResponse.getBody().getString("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getString("page_count"),
                apiRestResponse.getBody().getString("page_count"));
        Assert.assertEquals(esbRestResponse.getBody().getString("total_count"),
                apiRestResponse.getBody().getString("total_count"));
        Assert.assertEquals(esbProjectObject.getString("id"), apiProjectObject.getString("id"));
        Assert.assertEquals(esbProjectObject.getString("name"), apiProjectObject.getString("name"));
        Assert.assertEquals(Double.parseDouble(esbProjectObject.getString("rate")),
                Double.parseDouble(apiProjectObject.getString("rate")));
        
    }
    
    /**
     * Positive test case for listProjects method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectWithMandatoryParameters",
            "testCreateProjectWithOptionalParameters", "testCreateClientWithOptionalParameters" }, description = "ronin {listClients} integration test with optional parameters.")
    public void testListProjectsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listProjects");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjects_optional.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/clients/" + connectorProperties.getProperty("clientId") + "/projects";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("page_size"),
                apiRestResponse.getBody().getString("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getString("page_count"),
                apiRestResponse.getBody().getString("page_count"));
        Assert.assertEquals(esbRestResponse.getBody().getString("total_count"),
                apiRestResponse.getBody().getString("total_count"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("projects").length(), apiRestResponse.getBody()
                .getJSONArray("projects").length());
    }
    
    /**
     * Negative test case for listProjects method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "ronin {getProject} integration test with negative case.")
    public void testListProjectsNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listProjects");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjects_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/clients/INVALID/projects";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for createTask method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "ronin {createTask} integration test with mandatory parameters.")
    public void testCreateTaskWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_mandatory.json");
        
        final String taskId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("taskId", taskId);
        final String apiEndpoint = apiEndpointUrl + "/tasks/" + taskId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("taskTitle"), apiRestResponse.getBody().getString("title"));
        
    }
    
    /**
     * Positive test case for createTask method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectWithOptionalParameters" }, description = "ronin {createTask} integration test with optional parameters.")
    public void testCreateTaskWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_optional.json");
        
        final String taskId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("taskIdOpt", taskId);
        
        final String apiEndpoint = apiEndpointUrl + "/tasks/" + taskId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("taskTitleOpt"),
                apiRestResponse.getBody().getString("title"));
        Assert.assertEquals(connectorProperties.getProperty("taskDescriptionOpt"),
                apiRestResponse.getBody().getString("description"));
        Assert.assertEquals(connectorProperties.getProperty("taskDueDateOpt"),
                apiRestResponse.getBody().getString("due_date"));
        Assert.assertEquals(connectorProperties.getProperty("taskCompletedOpt"),
                apiRestResponse.getBody().getString("complete"));
        Assert.assertEquals(connectorProperties.getProperty("projectId"),
                apiRestResponse.getBody().getString("project_id"));
        
    }
    
    /**
     * Negative test case for createTask method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "ronin {createTask} integration test with negative case.")
    public void testCreateTaskNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/tasks";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createTask_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("title").getString(0), apiRestResponse.getBody()
                .getJSONArray("title").getString(0));
        
    }
    
    /**
     * Positive test case for getTask method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTaskWithOptionalParameters" }, description = "ronin {getTask} integration test with mandatory parameters.")
    public void testGetTaskWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTask_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/tasks/" + connectorProperties.getProperty("taskIdOpt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("title"), apiRestResponse.getBody().getString("title"));
        Assert.assertEquals(esbRestResponse.getBody().getString("complete"),
                apiRestResponse.getBody().getString("complete"));
        Assert.assertEquals(esbRestResponse.getBody().getString("description"),
                apiRestResponse.getBody().getString("description"));
        Assert.assertEquals(esbRestResponse.getBody().getString("due_date"),
                apiRestResponse.getBody().getString("due_date"));
        Assert.assertEquals(esbRestResponse.getBody().getString("project_id"),
                apiRestResponse.getBody().getString("project_id"));
        
    }
    
    /**
     * Negative test case for getTask method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "ronin {getTask} integration test with negative case.")
    public void testGetTaskNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTask_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/tasks/INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for listTasks method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTaskWithMandatoryParameters",
            "testCreateTaskWithOptionalParameters" }, description = "ronin {listClients} integration test with mandatory parameters.")
    public void testListTasksWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTasks");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/tasks";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject esbTaskObject = esbRestResponse.getBody().getJSONArray("tasks").getJSONObject(0);
        JSONObject apiTaskObject = apiRestResponse.getBody().getJSONArray("tasks").getJSONObject(0);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("page_size"),
                apiRestResponse.getBody().getString("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getString("page_count"),
                apiRestResponse.getBody().getString("page_count"));
        Assert.assertEquals(esbRestResponse.getBody().getString("total_count"),
                apiRestResponse.getBody().getString("total_count"));
        Assert.assertEquals(esbTaskObject.getString("id"), apiTaskObject.getString("id"));
        Assert.assertEquals(esbTaskObject.getString("title"), apiTaskObject.getString("title"));
        
    }
    
    /**
     * Positive test case for listTasks method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTaskWithMandatoryParameters",
            "testCreateTaskWithOptionalParameters" }, description = "ronin {listClients} integration test with optional parameters.")
    public void testListTasksWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTasks");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_optional.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/projects/" + connectorProperties.getProperty("projectId") + "/tasks";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject esbTaskObject = esbRestResponse.getBody().getJSONArray("tasks").getJSONObject(0);
        JSONObject apiTaskObject = apiRestResponse.getBody().getJSONArray("tasks").getJSONObject(0);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("page_size"),
                apiRestResponse.getBody().getString("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getString("page_count"),
                apiRestResponse.getBody().getString("page_count"));
        Assert.assertEquals(esbRestResponse.getBody().getString("total_count"),
                apiRestResponse.getBody().getString("total_count"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tasks").length(), apiRestResponse.getBody()
                .getJSONArray("tasks").length());
        Assert.assertEquals(esbTaskObject.getString("id"), apiTaskObject.getString("id"));
        Assert.assertEquals(esbTaskObject.getString("title"), apiTaskObject.getString("title"));
        Assert.assertEquals(esbTaskObject.getString("project_id"), apiTaskObject.getString("project_id"));
    }
    
    /**
     * Negative test case for listTasks method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "ronin {listTasks} integration test with negative case.")
    public void testListTasksNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTasks");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/projects/INVALID/tasks";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for updateTask method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTaskWithMandatoryParameters",
            "testCreateClientWithOptionalParameters" }, description = "ronin {updateTask} integration test with optional parameters.")
    public void testUpdateTaskWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateTask");
        final String apiEndpoint = apiEndpointUrl + "/tasks/" + connectorProperties.getProperty("taskId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final String taskTitle = apiRestResponse.getBody().getString("title");
        final String taskDescription = apiRestResponse.getBody().getString("description");
        final String taskDueDate = apiRestResponse.getBody().getString("due_date");
        final String taskClientId = apiRestResponse.getBody().getString("client_id");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTask_optional.json");
        
        apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final String taskTitleUpdated = apiRestResponse.getBody().getString("title");
        final String taskDescriptionUpdated = apiRestResponse.getBody().getString("description");
        final String taskDueDateUpdated = apiRestResponse.getBody().getString("due_date");
        final String taskClientIdUpdated = apiRestResponse.getBody().getString("client_id");
        
        Assert.assertNotEquals(taskTitle, taskTitleUpdated);
        Assert.assertNotEquals(taskDescription, taskDescriptionUpdated);
        Assert.assertNotEquals(taskDueDate, taskDueDateUpdated);
        Assert.assertNotEquals(taskClientId, taskClientIdUpdated);
        
    }
    
    /**
     * Negative test case for updateTask method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTaskWithMandatoryParameters" }, description = "ronin {updateTask} integration test with negative case.")
    public void testUpdateTaskNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTask_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/tasks/" + connectorProperties.getProperty("taskId");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_updateTask_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("title").getString(0), apiRestResponse.getBody()
                .getJSONArray("title").getString(0));
        
    }
    
    /**
     * Positive test case for listEstimates method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "ronin {listEstimates} integration test with mandatory parameters.")
    public void testListEstimatesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listEstimates");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listEstimates_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/estimates";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject esbEstimateObject = esbRestResponse.getBody().getJSONArray("estimates").getJSONObject(0);
        JSONObject apiEstimateObject = apiRestResponse.getBody().getJSONArray("estimates").getJSONObject(0);
        
        connectorProperties.put("estimateId", esbEstimateObject.getString("id"));
        
        Assert.assertEquals(esbRestResponse.getBody().getString("page_size"),
                apiRestResponse.getBody().getString("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getString("page_count"),
                apiRestResponse.getBody().getString("page_count"));
        Assert.assertEquals(esbRestResponse.getBody().getString("total_count"),
                apiRestResponse.getBody().getString("total_count"));
        Assert.assertEquals(esbEstimateObject.getString("id"), apiEstimateObject.getString("id"));
        Assert.assertEquals(esbEstimateObject.getString("title"), apiEstimateObject.getString("title"));
        Assert.assertEquals(esbEstimateObject.getString("total_cost"), apiEstimateObject.getString("total_cost"));
        Assert.assertEquals(esbEstimateObject.getString("currency_code"), apiEstimateObject.getString("currency_code"));
        
    }
    
    /**
     * Positive test case for listEstimates method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "ronin {listEstimates} integration test with optional parameters.")
    public void testListEstimatesWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listEstimates");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listEstimates_optional.json");
        
        final String apiEndpoint = apiEndpointUrl + "/estimates?page=2";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("page_size"),
                apiRestResponse.getBody().getString("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getString("page_count"),
                apiRestResponse.getBody().getString("page_count"));
        Assert.assertEquals(esbRestResponse.getBody().getString("total_count"),
                apiRestResponse.getBody().getString("total_count"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("estimates").length(), apiRestResponse.getBody()
                .getJSONArray("estimates").length());
        
    }
    
    /**
     * Negative test case for listEstimates method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "ronin {listEstimates} integration test with negative case.")
    public void testListEstimatesNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listEstimates");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listEstimates_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/projects/INVALID/estimates";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for getEstimate method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListEstimatesWithMandatoryParameters" }, description = "ronin {getEstimate} integration test with mandatory parameters.")
    public void testGetEstimateWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getEstimate");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEstimate_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/estimates/" + connectorProperties.getProperty("estimateId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("title"), apiRestResponse.getBody().getString("title"));
        Assert.assertEquals(esbRestResponse.getBody().getString("total_cost"),
                apiRestResponse.getBody().getString("total_cost"));
        Assert.assertEquals(esbRestResponse.getBody().getString("currency_code"),
                apiRestResponse.getBody().getString("currency_code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("estimate_items").length(), apiRestResponse
                .getBody().getJSONArray("estimate_items").length());
        
    }
    
    /**
     * Negative test case for getEstimate method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "ronin {getEstimate} integration test with negative case.")
    public void testGetEstimateNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getEstimate");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEstimate_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/estimates/INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for listInvoices method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "ronin {listInvoices} integration test with mandatory parameters.")
    public void testListInvoicesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listInvoices");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoices_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/invoices";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject esbEstimateObject = esbRestResponse.getBody().getJSONArray("invoices").getJSONObject(0);
        JSONObject apiEstimateObject = apiRestResponse.getBody().getJSONArray("invoices").getJSONObject(0);
        
        connectorProperties.put("invoiceId", esbEstimateObject.getString("id"));
        
        Assert.assertEquals(esbRestResponse.getBody().getString("page_size"),
                apiRestResponse.getBody().getString("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getString("page_count"),
                apiRestResponse.getBody().getString("page_count"));
        Assert.assertEquals(esbRestResponse.getBody().getString("total_count"),
                apiRestResponse.getBody().getString("total_count"));
        Assert.assertEquals(esbEstimateObject.getString("id"), apiEstimateObject.getString("id"));
        Assert.assertEquals(esbEstimateObject.getString("title"), apiEstimateObject.getString("title"));
        Assert.assertEquals(esbEstimateObject.getString("total_cost"), apiEstimateObject.getString("total_cost"));
        Assert.assertEquals(esbEstimateObject.getString("currency_code"), apiEstimateObject.getString("currency_code"));
        
    }
    
    /**
     * Positive test case for listInvoices method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "ronin {listInvoices} integration test with optional parameters.")
    public void testListInvoicesWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listInvoices");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoices_optional.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/invoices?updated_since=" + connectorProperties.getProperty("invoiceUpdatedSince");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("page_size"),
                apiRestResponse.getBody().getString("page_size"));
        Assert.assertEquals(esbRestResponse.getBody().getString("page_count"),
                apiRestResponse.getBody().getString("page_count"));
        Assert.assertEquals(esbRestResponse.getBody().getString("total_count"),
                apiRestResponse.getBody().getString("total_count"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("invoices").length(), apiRestResponse.getBody()
                .getJSONArray("invoices").length());
        
    }
    
    /**
     * Negative test case for listInvoices method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "ronin {listInvoices} integration test with negative case.")
    public void testListInvoicesNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listInvoices");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoices_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/clients/INVALID/invoices";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for getInvoice method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListInvoicesWithMandatoryParameters" }, description = "ronin {getInvoice} integration test with mandatory parameters.")
    public void testGetInvoiceWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getInvoice");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getInvoice_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/invoices/" + connectorProperties.getProperty("invoiceId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("title"), apiRestResponse.getBody().getString("title"));
        Assert.assertEquals(esbRestResponse.getBody().getString("total_cost"),
                apiRestResponse.getBody().getString("total_cost"));
        Assert.assertEquals(esbRestResponse.getBody().getString("currency_code"),
                apiRestResponse.getBody().getString("currency_code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("invoice_items").length(), apiRestResponse.getBody()
                .getJSONArray("invoice_items").length());
        
    }
    
    /**
     * Negative test case for getInvoice method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "ronin {getInvoice} integration test with negative case.")
    public void testGetInvoiceNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getInvoice");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getInvoice_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/invoices/INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for createInvoicePayment method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetInvoiceWithMandatoryParameters" }, description = "ronin {createInvoicePayment} integration test with mandatory parameters.")
    public void testCreateInvoicePaymentWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoicePayment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoicePayment_mandatory.json");
        
        final String paymentId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("paymentId", paymentId);
        final String apiEndpoint = apiEndpointUrl + "/invoices/" + connectorProperties.getProperty("invoiceId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONArray paymentDetails = apiRestResponse.getBody().getJSONArray("payments");
        JSONObject paymentObj = null;
        
        for (int i = 0; i < paymentDetails.length(); i++) {
            paymentObj = paymentDetails.getJSONObject(i);
            if (paymentId.equals(paymentObj.getString("id"))) {
                break;
            }
        }
        
        Assert.assertNotNull(paymentObj);
        Assert.assertEquals(Double.parseDouble(connectorProperties.getProperty("amount")), Double.parseDouble(paymentObj.getString("amount")));
        
    }
    
    /**
     * Positive test case for createInvoicePayment method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetInvoiceWithMandatoryParameters" }, description = "ronin {createInvoicePayment} integration test with optional parameters.")
    public void testCreateInvoicePaymentWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoicePayment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoicePayment_optional.json");
        
        final String paymentId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("paymentId", paymentId);
        final String apiEndpoint = apiEndpointUrl + "/invoices/" + connectorProperties.getProperty("invoiceId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONArray paymentDetails = apiRestResponse.getBody().getJSONArray("payments");
        JSONObject paymentObj = null;
        
        for (int i = 0; i < paymentDetails.length(); i++) {
            paymentObj = paymentDetails.getJSONObject(i);
            if (paymentId.equals(paymentObj.getString("id"))) {
                break;
            }
        }
        
        Assert.assertNotNull(paymentObj);
        Assert.assertEquals(Double.parseDouble(connectorProperties.getProperty("amount")), Double.parseDouble(paymentObj.getString("amount")));
        Assert.assertEquals(connectorProperties.getProperty("paymentNote"), paymentObj.getString("note"));
        Assert.assertEquals(connectorProperties.getProperty("paymentReceivedOn"), paymentObj.getString("received_on"));
        Assert.assertEquals(connectorProperties.getProperty("transactionFeeAmount"),
                paymentObj.getString("transaction_fee_amount"));
        
    }
}
