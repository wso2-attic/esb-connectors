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

package org.wso2.carbon.connector.integration.test.pipelinedeals;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class PipelineDealsConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap;
    
    private Map<String, String> apiRequestHeadersMap;
    
    private String authString;
    
    private String apiEndpointUrl;
    
    /**
     * All of the integration test methods have been made to sleep for 500 ms because the API imposes a rate limiting.
     */
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        
        init("pipelinedeals-connector-1.0.0");
        
        esbRequestHeadersMap = new HashMap<String, String>();
        apiRequestHeadersMap = new HashMap<String, String>();
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Accept", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        authString = "?api_key=" + connectorProperties.getProperty("apiKey");
        apiEndpointUrl = connectorProperties.getProperty("apiUrl") + "/api/v3";
        
    }
    
    /**
     * Positive test case for createCompany method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {createCompany} integration test with mandatory parameters.")
    public void testCreateCompanyWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:createCompany");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCompany_mandatory.json");
        
        final String companyIdMandatory = esbRestResponse.getBody().getString("id");
        
        final String apiEndpoint = apiEndpointUrl + "/companies/" + companyIdMandatory + ".json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("companyNameMandatory"), apiRestResponse.getBody()
                        .getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                        "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("owner_id"), apiRestResponse.getBody().getString(
                        "owner_id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("updated_at"), esbRestResponse.getBody().getString(
                        "updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("total_pipeline"), esbRestResponse.getBody().getString(
                        "total_pipeline"));
        
    }
    
    /**
     * Positive test case for createCompany method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {createCompany} integration test with optional parameters.")
    public void testCreateCompanyWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:createCompany");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCompany_optional.json");
        
        final String companyIdOptional = esbRestResponse.getBody().getString("id");
        connectorProperties.put("companyIdOptional", companyIdOptional);
        
        final String apiEndpoint = apiEndpointUrl + "/companies/" + companyIdOptional + ".json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("companyNameOptional"), apiRestResponse.getBody()
                        .getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("companyDescriptionOptional"), apiRestResponse.getBody()
                        .getString("description"));
        Assert.assertEquals(connectorProperties.getProperty("companyEmailOptional"), apiRestResponse.getBody()
                        .getString("email"));
        Assert.assertEquals(connectorProperties.getProperty("companyWebsiteOptional"), apiRestResponse.getBody()
                        .getString("web"));
        Assert.assertEquals(connectorProperties.getProperty("companyAddress1Optional"), apiRestResponse.getBody()
                        .getString("address_1"));
        Assert.assertEquals(connectorProperties.getProperty("companyAddress2Optional"), apiRestResponse.getBody()
                        .getString("address_2"));
        Assert.assertEquals(connectorProperties.getProperty("companyCityOptional"), apiRestResponse.getBody()
                        .getString("city"));
        Assert.assertEquals(connectorProperties.getProperty("companyStateOptional"), apiRestResponse.getBody()
                        .getString("state"));
        Assert.assertEquals(connectorProperties.getProperty("companyPostalCodeOptional"), apiRestResponse.getBody()
                        .getString("postal_code"));
        Assert.assertEquals(connectorProperties.getProperty("companyCountryOptional"), apiRestResponse.getBody()
                        .getString("country"));
        
    }
    
    /**
     * Negative test case for createCompany method.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {createCompany} integration test with negative case.")
    public void testCreateCompanyWithNegativeCase() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:createCompany");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCompany_negative.json");
        final JSONArray esbRestResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndpoint = apiEndpointUrl + "/companies.json" + authString;
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_createCompany_negative.json");
        final JSONArray apiRestResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponseArray.getJSONObject(0).getString("humanized_field"), apiRestResponseArray
                        .getJSONObject(0).getString("humanized_field"));
        Assert.assertEquals(esbRestResponseArray.getJSONObject(0).getString("field"), apiRestResponseArray
                        .getJSONObject(0).getString("field"));
        Assert.assertEquals(esbRestResponseArray.getJSONObject(0).getString("msg"), apiRestResponseArray.getJSONObject(
                        0).getString("msg"));
        
    }
    
    /**
     * Method name: updateCompany
     * Test scenario: Mandatory
     * Reason to skip: In updateCompany method nothing needs to be necessarily (mandatory) updated.
     */
    
    /**
     * Positive test case for updateCompany method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {updateCompany} integration test with optional parameters.", dependsOnMethods = { "testCreateCompanyWithOptionalParameters" })
    public void testUpdateCompanyWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:updateCompany");
        final String apiEndpoint =
                        apiEndpointUrl + "/companies/" + connectorProperties.getProperty("companyIdOptional") + ".json"
                                        + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCompany_optional.json");
        
        RestResponse<JSONObject> apiRestResponse2 = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponse.getBody().getString("name"), apiRestResponse2.getBody()
                        .getString("name"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("description"), apiRestResponse2.getBody()
                        .getString("description"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("email"), apiRestResponse2.getBody().getString(
                        "email"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("web"), apiRestResponse2.getBody().getString("web"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("country"), apiRestResponse2.getBody().getString(
                        "country"));
        Assert.assertEquals(connectorProperties.getProperty("companyNameUpdated"), apiRestResponse2.getBody()
                        .getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("companyDescriptionUpdated"), apiRestResponse2.getBody()
                        .getString("description"));
        Assert.assertEquals(connectorProperties.getProperty("companyEmailUpdated"), apiRestResponse2.getBody()
                        .getString("email"));
        Assert.assertEquals(connectorProperties.getProperty("companyWebsiteUpdated"), apiRestResponse2.getBody()
                        .getString("web"));
        Assert.assertEquals(connectorProperties.getProperty("companyCountryUpdated"), apiRestResponse2.getBody()
                        .getString("country"));
        
    }
    
    /**
     * Negative test case for updateCompany method.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {updateCompany} integration test with negative case.")
    public void testUpdateCompanyWithNegativeCase() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:updateCompany");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCompany_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/companies/invalid.json" + authString;
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_updateCompany_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 403);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 403);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for getCompany method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {getCompany} integration test with mandatory parameters.", dependsOnMethods = { "testCreateCompanyWithOptionalParameters" })
    public void testGetCompanyWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:getCompany");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCompany_mandatory.json");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/companies/" + connectorProperties.getProperty("companyIdOptional") + ".json"
                                        + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("description"), apiRestResponse.getBody().getString(
                        "description"));
        Assert.assertEquals(esbRestResponse.getBody().getString("email"), apiRestResponse.getBody().getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getString("web"), apiRestResponse.getBody().getString("web"));
        Assert.assertEquals(esbRestResponse.getBody().getString("country"), apiRestResponse.getBody().getString(
                        "country"));
        
    }
    
    /**
     * Positive test case for getCompany method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {getCompany} integration test with optional parameters.", dependsOnMethods = { "testCreateCompanyWithOptionalParameters" })
    public void testGetCompanyWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:getCompany");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCompany_optional.json");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/companies/" + connectorProperties.getProperty("companyIdOptional") + ".json"
                                        + authString + "&associations=notes";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("description"), apiRestResponse.getBody().getString(
                        "description"));
        Assert.assertEquals(esbRestResponse.getBody().getString("email"), apiRestResponse.getBody().getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getString("web"), apiRestResponse.getBody().getString("web"));
        Assert.assertEquals(esbRestResponse.getBody().getString("country"), apiRestResponse.getBody().getString(
                        "country"));
        
    }
    
    /**
     * Negative test case for getCompany method.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {getCompany} integration test with negative case.")
    public void testGetCompanyWithNegativeCase() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:getCompany");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCompany_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/companies/invalid.json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 403);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 403);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for listCompanies method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {listCompanies} integration test with mandatory parameters.", dependsOnMethods = {
                    "testCreateCompanyWithMandatoryParameters", "testCreateCompanyWithOptionalParameters" })
    public void testListCompaniesWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:listCompanies");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCompanies_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/companies.json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("name"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("created_at"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("updated_at"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").length(), apiRestResponse.getBody()
                        .getJSONArray("entries").length());
        
    }
    
    /**
     * Positive test case for listCompanies method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {listCompanies} integration test with optional parameters.", dependsOnMethods = {
                    "testCreateCompanyWithMandatoryParameters", "testCreateCompanyWithOptionalParameters" })
    public void testListCompaniesWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:listCompanies");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCompanies_optional.json");
        
        final String apiEndpoint = apiEndpointUrl + "/companies.json" + authString + "&page=1&per_page=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").length(), 1);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("entries").length(), 1);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("name"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("created_at"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("updated_at"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("updated_at"));
        
    }
    
    /**
     * Negative test case for listCompanies method.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {listCompanies} integration test with negative case.")
    public void testListCompaniesWithNegativeCase() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:listCompanies");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCompanies_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/companies.json" + authString + "&page=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for createPerson method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {createPerson} integration test with mandatory parameters.")
    public void testCreatePersonWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:createPerson");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPerson_mandatory.json");
        
        final String personIdMandatory = esbRestResponse.getBody().getString("id");
        
        final String apiEndpoint = apiEndpointUrl + "/people/" + personIdMandatory + ".json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("personEmailMandatory"), apiRestResponse.getBody()
                        .getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                        "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("updated_at"), apiRestResponse.getBody().getString(
                        "updated_at"));
        
    }
    
    /**
     * Positive test case for createPerson method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {createPerson} integration test with optional parameters.")
    public void testCreatePersonWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:createPerson");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPerson_optional.json");
        
        final String personIdOptional = esbRestResponse.getBody().getString("id");
        connectorProperties.put("personIdOptional", personIdOptional);
        
        final String apiEndpoint = apiEndpointUrl + "/people/" + personIdOptional + ".json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("personFirstNameOptional"), apiRestResponse.getBody()
                        .getString("first_name"));
        Assert.assertEquals(connectorProperties.getProperty("personLastNameOptional"), apiRestResponse.getBody()
                        .getString("last_name"));
        Assert.assertEquals(connectorProperties.getProperty("personSummaryOptional"), apiRestResponse.getBody()
                        .getString("summary"));
        Assert.assertEquals(connectorProperties.getProperty("personPositionOptional"), apiRestResponse.getBody()
                        .getString("position"));
        Assert.assertEquals(connectorProperties.getProperty("emailPersonOptional"), apiRestResponse.getBody()
                        .getString("email"));
        Assert.assertEquals(connectorProperties.getProperty("personTypeOptional"), apiRestResponse.getBody().getString(
                        "type"));
        
    }
    
    /**
     * Negative test case for createPerson method.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {createPerson} integration test with negative case.")
    public void testCreatePersonWithNegativeCase() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:createPerson");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPerson_negative.json");
        final JSONArray esbRestResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndpoint = apiEndpointUrl + "/people.json" + authString;
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createPerson_negative.json");
        final JSONArray apiRestResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponseArray.getJSONObject(0).getString("humanized_field"), apiRestResponseArray
                        .getJSONObject(0).getString("humanized_field"));
        Assert.assertEquals(esbRestResponseArray.getJSONObject(0).getString("field"), apiRestResponseArray
                        .getJSONObject(0).getString("field"));
        Assert.assertEquals(esbRestResponseArray.getJSONObject(0).getString("msg"), apiRestResponseArray.getJSONObject(
                        0).getString("msg"));
        
    }
    
    /**
     * Method name: updatePerson
     * Test scenario: Mandatory
     * Reason to skip: In updatePerson method nothing needs to be necessarily (mandatory) updated.
     */
    
    /**
     * Positive test case for updatePerson method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {updatePerson} integration test with optional parameters.", dependsOnMethods = { "testCreatePersonWithOptionalParameters" })
    public void testUpdatePersonWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:updatePerson");
        final String apiEndpoint =
                        apiEndpointUrl + "/people/" + connectorProperties.getProperty("personIdOptional") + ".json"
                                        + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updatePerson_optional.json");
        
        RestResponse<JSONObject> apiRestResponse2 = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponse.getBody().getString("first_name"), apiRestResponse2.getBody().getString(
                        "first_name"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("last_name"), apiRestResponse2.getBody().getString(
                        "last_name"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("position"), apiRestResponse2.getBody().getString(
                        "position"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("summary"), apiRestResponse2.getBody().getString(
                        "summary"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("email"), apiRestResponse2.getBody().getString(
                        "email"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("type"), apiRestResponse2.getBody()
                        .getString("type"));
        Assert.assertEquals(connectorProperties.getProperty("personFirstNameUpdated"), apiRestResponse2.getBody()
                        .getString("first_name"));
        Assert.assertEquals(connectorProperties.getProperty("personLastNameUpdated"), apiRestResponse2.getBody()
                        .getString("last_name"));
        Assert.assertEquals(connectorProperties.getProperty("personSummaryUpdated"), apiRestResponse2.getBody()
                        .getString("summary"));
        Assert.assertEquals(connectorProperties.getProperty("personPositionUpdated"), apiRestResponse2.getBody()
                        .getString("position"));
        Assert.assertEquals(connectorProperties.getProperty("personEmailUpdated"), apiRestResponse2.getBody()
                        .getString("email"));
        Assert.assertEquals(connectorProperties.getProperty("personTypeUpdated"), apiRestResponse2.getBody().getString(
                        "type"));
        
    }
    
    /**
     * Negative test case for updatePerson method.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {updatePerson} integration test with negative case.", dependsOnMethods = { "testCreatePersonWithOptionalParameters" })
    public void testUpdatePersonWithNegativeCase() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:updatePerson");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updatePerson_negative.json");
        final JSONArray esbRestResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndpoint =
                        apiEndpointUrl + "/people/" + connectorProperties.getProperty("personIdOptional") + ".json"
                                        + authString;
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_updatePerson_negative.json");
        final JSONArray apiRestResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponseArray.getJSONObject(0).getString("humanized_field"), apiRestResponseArray
                        .getJSONObject(0).getString("humanized_field"));
        Assert.assertEquals(esbRestResponseArray.getJSONObject(0).getString("field"), apiRestResponseArray
                        .getJSONObject(0).getString("field"));
        Assert.assertEquals(esbRestResponseArray.getJSONObject(0).getString("msg"), apiRestResponseArray.getJSONObject(
                        0).getString("msg"));
        
    }
    
    /**
     * Positive test case for getPerson method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {getPerson} integration test with mandatory parameters.", dependsOnMethods = { "testCreatePersonWithOptionalParameters" })
    public void testGetPersonWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:getPerson");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPerson_mandatory.json");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/people/" + connectorProperties.getProperty("personIdOptional") + ".json"
                                        + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("email"), apiRestResponse.getBody().getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getString("first_name"), apiRestResponse.getBody().getString(
                        "first_name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("summary"), apiRestResponse.getBody().getString(
                        "summary"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                        "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("updated_at"), apiRestResponse.getBody().getString(
                        "updated_at"));
        
    }
    
    /**
     * Positive test case for getPerson method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {getPerson} integration test with optional parameters.", dependsOnMethods = { "testCreatePersonWithOptionalParameters" })
    public void testGetPersonWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        final String apiEndpoint =
                        apiEndpointUrl + "/people/" + connectorProperties.getProperty("personIdOptional") + ".json"
                                        + authString;
        RestResponse<JSONObject> apiRestResponseWithoutAttributes =
                        sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        esbRequestHeadersMap.put("Action", "urn:getPerson");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPerson_optional.json");
        
        RestResponse<JSONObject> apiRestResponseWithAttributes =
                        sendJsonRestRequest(apiEndpoint + "&attrs=email", "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(esbRestResponse.getBody().length(), apiRestResponseWithoutAttributes.getBody().length());
        Assert.assertEquals(esbRestResponse.getBody().length(), apiRestResponseWithAttributes.getBody().length());
        
        Assert.assertEquals(esbRestResponse.getBody().getString("email"), apiRestResponseWithAttributes.getBody()
                        .getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getString("won_deals_total"), apiRestResponseWithAttributes
                        .getBody().getString("won_deals_total"));
        
    }
    
    /**
     * Negative test case for getPerson method.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {getPerson} integration test with negative case.")
    public void testGetPersonWithNegativeCase() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:getPerson");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPerson_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/people/invalid.json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for listPeople method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {listPeople} integration test with mandatory parameters.", dependsOnMethods = {
                    "testCreatePersonWithMandatoryParameters", "testCreatePersonWithOptionalParameters" })
    public void testListPeopleWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:listPeople");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPeople_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/people.json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").length(), apiRestResponse.getBody()
                        .getJSONArray("entries").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("email"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("updated_at"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("user_id"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("user_id"));
        
    }
    
    /**
     * Positive test case for listPeople method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {listPeople} integration test with optional parameters.", dependsOnMethods = {
                    "testCreatePersonWithMandatoryParameters", "testCreatePersonWithOptionalParameters" })
    public void testListPeopleWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:listPeople");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPeople_optional.json");
        
        final String apiEndpoint = apiEndpointUrl + "/people.json" + authString + "&page=1&per_page=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("email"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("updated_at"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("user_id"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("user_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").length(), 1);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").length(), 1);
        
    }
    
    /**
     * Negative test case for listPeople method.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {listPeople} integration test with negative case.")
    public void testListPeopleWithNegativeCase() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:listPeople");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPeople_negative.json");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/people.json" + authString + "&"
                                        + URLEncoder.encode("conditions[person_converted][from_date]=1", "UTF-8");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 500);
        
    }
    
    /**
     * Positive test case for createDeal method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {createDeal} integration test with mandatory parameters.")
    public void testCreateDealWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:createDeal");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDeal_mandatory.json");
        
        final String dealIdMandatory = esbRestResponse.getBody().getString("id");
        
        final String apiEndpoint = apiEndpointUrl + "/deals/" + dealIdMandatory + ".json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                        "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("updated_at"), apiRestResponse.getBody().getString(
                        "updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").getString("id"), apiRestResponse.getBody()
                        .getJSONObject("user").getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").getString("first_name"), apiRestResponse
                        .getBody().getJSONObject("user").getString("first_name"));
        
    }
    
    /**
     * Positive test case for createDeal method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     * @throws ParseException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {createDeal} integration test with optional parameters.", dependsOnMethods = { "testCreateCompanyWithOptionalParameters" })
    public void testCreateDealWithOptionalParameters() throws IOException, JSONException, InterruptedException,
                    ParseException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:createDeal");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDeal_optional.json");
        
        final String dealIdOptional = esbRestResponse.getBody().getString("id");
        connectorProperties.put("dealId", dealIdOptional);
        
        final String apiEndpoint = apiEndpointUrl + "/deals/" + dealIdOptional + ".json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("dealNameOptional"), apiRestResponse.getBody().getString(
                        "name"));
        Assert.assertEquals(connectorProperties.getProperty("dealSummaryOptional"), apiRestResponse.getBody()
                        .getString("summary"));
        final String expectedCloseDate =
                        new SimpleDateFormat("yyyy/MM/dd").format(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss z")
                                        .parse(connectorProperties.getProperty("dealClosedDateOptional")));
        Assert.assertEquals(expectedCloseDate, apiRestResponse.getBody().getString("expected_close_date"));
        Assert.assertEquals(connectorProperties.getProperty("dealArchivedOptional"), apiRestResponse.getBody()
                        .getString("is_archived"));
        Assert.assertEquals(Double.parseDouble(connectorProperties.getProperty("dealValueOptional")), apiRestResponse
                        .getBody().getDouble("value"));
        Assert.assertEquals(connectorProperties.getProperty("dealValueInCentsOptional"), apiRestResponse.getBody()
                        .getString("value_in_cents"));
        Assert.assertEquals(connectorProperties.getProperty("companyIdOptional"), apiRestResponse.getBody().getString(
                        "company_id"));
        
    }
    
    /**
     * Negative test case for createDeal method.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {createDeal} integration test with negative case.")
    public void testCreateDealWithNegativeCase() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:createDeal");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDeal_negative.json");
        final JSONArray esbRestResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndpoint = apiEndpointUrl + "/deals.json" + authString;
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createDeal_negative.json");
        final JSONArray apiRestResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponseArray.getJSONObject(0).getString("humanized_field"), apiRestResponseArray
                        .getJSONObject(0).getString("humanized_field"));
        Assert.assertEquals(esbRestResponseArray.getJSONObject(0).getString("field"), apiRestResponseArray
                        .getJSONObject(0).getString("field"));
        Assert.assertEquals(esbRestResponseArray.getJSONObject(0).getString("msg"), apiRestResponseArray.getJSONObject(
                        0).getString("msg"));
        
    }
    
    /**
     * Method name: updateDeal
     * Test scenario: Mandatory
     * Reason to skip: In updateDeal method nothing needs to be necessarily (mandatory) updated.
     */
    
    /**
     * Positive test case for updateDeal method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {updateDeal} integration test with optional parameters.", dependsOnMethods = { "testCreateDealWithOptionalParameters" })
    public void testUpdateDealWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateDeal");
        final String apiEndpoint =
                        apiEndpointUrl + "/deals/" + connectorProperties.getProperty("dealId") + ".json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateDeal_optional.json");
        
        RestResponse<JSONObject> apiRestResponse2 = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponse.getBody().getString("name"), apiRestResponse2.getBody()
                        .getString("name"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("summary"), apiRestResponse2.getBody().getString(
                        "summary"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("is_archived"), apiRestResponse2.getBody()
                        .getString("is_archived"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("value"), apiRestResponse2.getBody().getString(
                        "value"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("probability"), apiRestResponse2.getBody()
                        .getString("probability"));
        Assert.assertEquals(connectorProperties.getProperty("optionalDealNameUpdated"), apiRestResponse2.getBody()
                        .getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("optionalDealSummaryUpdated"), apiRestResponse2.getBody()
                        .getString("summary"));
        Assert.assertEquals(Boolean.parseBoolean(connectorProperties.getProperty("optionalDealIsArchivedUpdated")),
                        apiRestResponse2.getBody().getBoolean("is_archived"));
        Assert.assertEquals(Double.parseDouble(connectorProperties.getProperty("optionalDealValueUpdated")),
                        apiRestResponse2.getBody().getDouble("value"));
        Assert.assertEquals(Integer.parseInt(connectorProperties.getProperty("optionalDealProbabilityUpdated")),
                        apiRestResponse2.getBody().getInt("probability"));
        
    }
    
    /**
     * Negative test case for updateDeal method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {updateDeal} integration test with negative case.")
    public void testUpdateDealWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateDeal");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateDeal_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/deals/invalid.json" + authString;
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_updateDeal_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for getDeal method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {getDeal} integration test with mandatory parameters.", dependsOnMethods = { "testCreateDealWithOptionalParameters" })
    public void testGetDealWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:getDeal");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDeal_mandatory.json");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/deals/" + connectorProperties.getProperty("dealId") + ".json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").getString("first_name"), apiRestResponse
                        .getBody().getJSONObject("user").getString("first_name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("summary"), apiRestResponse.getBody().getString(
                        "summary"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                        "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("updated_at"), apiRestResponse.getBody().getString(
                        "updated_at"));
        
    }
    
    /**
     * Positive test case for getDeal method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {getDeal} integration test with optional parameters.", dependsOnMethods = { "testCreateDealWithOptionalParameters" })
    public void testGetDealWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        final String apiEndpoint =
                        apiEndpointUrl + "/deals/" + connectorProperties.getProperty("dealId") + ".json" + authString;
        RestResponse<JSONObject> apiRestResponseWithoutAttributes =
                        sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        esbRequestHeadersMap.put("Action", "urn:getDeal");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDeal_optional.json");
        
        RestResponse<JSONObject> apiRestResponseWithAttributes =
                        sendJsonRestRequest(apiEndpoint + "&attrs=name", "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(esbRestResponse.getBody().length(), apiRestResponseWithoutAttributes.getBody().length());
        Assert.assertEquals(esbRestResponse.getBody().length(), apiRestResponseWithAttributes.getBody().length());
        
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponseWithAttributes.getBody()
                        .getString("name"));
        
    }
    
    /**
     * Negative test case for getDeal method.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {getDeal} integration test with negative case.")
    public void testGetDealWithNegativeCase() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:getDeal");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDeal_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/deals/invalid.json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for listDeals method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {listDeals} integration test with mandatory parameters.", dependsOnMethods = {
                    "testCreateDealWithMandatoryParameters", "testUpdateDealWithOptionalParameters" })
    public void testListDealsWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:listDeals");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDeals_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/deals.json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").length(), apiRestResponse.getBody()
                        .getJSONArray("entries").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("updated_at"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("user_id"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("user_id"));
        
    }
    
    /**
     * Positive test case for listDeals method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {listDeals} integration test with optional parameters.", dependsOnMethods = {
                    "testCreateDealWithMandatoryParameters", "testUpdateDealWithOptionalParameters" })
    public void testListDealsWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:listDeals");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDeals_optional.json");
        
        final String apiEndpoint = apiEndpointUrl + "/deals.json" + authString + "&page=1&per_page=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").length(), 1);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").length(), 1);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("updated_at"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("user_id"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("user_id"));
        
    }
    
    /**
     * Negative test case for listDeals method.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(enabled = true, groups = { "wso2.esb" }, description = "pipelinedeals {listDeals} integration test with negative case.")
    public void testListDealsWithNegativeCase() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:listDeals");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDeals_negative.json");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/deals.json" + authString + "&"
                                        + URLEncoder.encode("conditions[deal_created][from_date]=INVALID", "UTF-8");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 500);
        
    }
    
    /**
     * Positive test case for createCalendarEntry method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {createCalendarEntry} integration test with mandatory parameters.")
    public void testCreateCalendarEntryWithMandatoryParameters() throws IOException, JSONException,
                    InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:createCalendarEntry");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_createCalendarEntry_mandatory.json");
        
        final String calendarEntryIdMandatory = esbRestResponse.getBody().getString("id");
        connectorProperties.put("calendarEntryIdMandatory", calendarEntryIdMandatory);
        
        final String apiEndpoint =
                        apiEndpointUrl + "/calendar_entries/" + calendarEntryIdMandatory + ".json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("calendarEntryNameMandatory"), apiRestResponse.getBody()
                        .getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                        "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("owner_id"), apiRestResponse.getBody().getString(
                        "owner_id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("updated_at"), apiRestResponse.getBody().getString(
                        "updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("active"), apiRestResponse.getBody()
                        .getString("active"));
    }
    
    /**
     * Positive test case for createCalendarEntry method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {createCalendarEntry} integration test with optional parameters.")
    public void testCreateCalendarEntryWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:createCalendarEntry");
        
        connectorProperties.setProperty("calendarEntryTypeOptional", "CalendarEvent");
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_createCalendarEntry_optional.json");
        
        final String calendarEntryIdOptional = esbRestResponse.getBody().getString("id");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/calendar_entries/" + calendarEntryIdOptional + ".json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final String apiStartTime = apiRestResponse.getBody().getString("start_time").substring(0, 19);
        final String apiEndTime = apiRestResponse.getBody().getString("end_time").substring(0, 19);
        
        Assert.assertEquals(connectorProperties.getProperty("calendarEntryDescriptionOptional"), apiRestResponse
                        .getBody().getString("description"));
        Assert.assertEquals(connectorProperties.getProperty("calendarEntryTypeOptional"), apiRestResponse.getBody()
                        .getString("type"));
        Assert.assertEquals(connectorProperties.getProperty("calendarEntryIsActiveOptional"), apiRestResponse.getBody()
                        .getString("active"));
        Assert.assertEquals(connectorProperties.getProperty("calendarEntryStartTimeOptional"), apiStartTime);
        Assert.assertEquals(connectorProperties.getProperty("calendarEntryEndTimeOptional"), apiEndTime);
    }
    
    /**
     * Negative test case for createCalendarEntry method.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {createCalendarEntry} integration test with negative case.")
    public void testCreateCalendarEntryWithNegativeCase() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:createCalendarEntry");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_createCalendarEntry_negative.json");
        final JSONArray esbRestResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndpoint = apiEndpointUrl + "/calendar_entries.json" + authString;
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_createCalendarEntry_negative.json");
        final JSONArray apiRestResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponseArray.getJSONObject(0).getString("humanized_field"), apiRestResponseArray
                        .getJSONObject(0).getString("humanized_field"));
        Assert.assertEquals(esbRestResponseArray.getJSONObject(0).getString("field"), apiRestResponseArray
                        .getJSONObject(0).getString("field"));
        Assert.assertEquals(esbRestResponseArray.getJSONObject(0).getString("msg"), apiRestResponseArray.getJSONObject(
                        0).getString("msg"));
    }
    
    /**
     * Method name: updateCalendarEntry
     * Test scenario: Mandatory
     * Reason to skip: In updateCalendarEntry method nothing needs to be necessarily (mandatory) updated.
     */
    
    /**
     * Positive test case for updateCalendarEntry method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {updateCalendarEntry} integration test with optional parameters.", dependsOnMethods = { "testCreateCalendarEntryWithMandatoryParameters" })
    public void testUpdateCalendarEntryWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:updateCalendarEntry");
        final String apiEndpoint =
                        apiEndpointUrl + "/calendar_entries/"
                                        + connectorProperties.getProperty("calendarEntryIdMandatory") + ".json"
                                        + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        connectorProperties.setProperty("calendarEntryActiveUpdated", "false");
        connectorProperties.setProperty("calendarEntryCompleteUpdated", "true");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCalendarEntry_optional.json");
        
        RestResponse<JSONObject> apiRestResponse2 = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponse.getBody().getString("name"), apiRestResponse2.getBody()
                        .getString("name"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("description"), apiRestResponse2.getBody()
                        .getString("description"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("active"), apiRestResponse2.getBody().getString(
                        "active"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("complete"), apiRestResponse2.getBody().getString(
                        "complete"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("due_date"), apiRestResponse2.getBody().getString(
                        "due_date"));
        Assert.assertEquals(connectorProperties.getProperty("calendarEntryNameUpdated"), apiRestResponse2.getBody()
                        .getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("calendarEntryDescriptionUpdated"), apiRestResponse2
                        .getBody().getString("description"));
        Assert.assertEquals(connectorProperties.getProperty("calendarEntryActiveUpdated"), apiRestResponse2.getBody()
                        .getString("active"));
        Assert.assertEquals(connectorProperties.getProperty("calendarEntryCompleteUpdated"), apiRestResponse2.getBody()
                        .getString("complete"));
        Assert.assertEquals(connectorProperties.getProperty("calendarEntryDueDateUpdated"), apiRestResponse2.getBody()
                        .getString("due_date"));
    }
    
    /**
     * Negative test case for updateCalendarEntry method.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {updateCalendarEntry} integration test with negative case.")
    public void testUpdateCalendarEntryWithNegativeCase() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:updateCalendarEntry");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_updateCalendarEntry_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/calendar_entries/INVALID.json" + authString;
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap,
                                        "api_updateCalendarEntry_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for getCalendarEntry method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {getCalendarEntry} integration test with mandatory parameters.", dependsOnMethods = { "testCreateCalendarEntryWithMandatoryParameters" })
    public void testGetCalendarEntryWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:getCalendarEntry");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_getCalendarEntry_mandatory.json");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/calendar_entries/"
                                        + connectorProperties.getProperty("calendarEntryIdMandatory") + ".json"
                                        + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("description"), apiRestResponse.getBody().getString(
                        "description"));
        Assert.assertEquals(esbRestResponse.getBody().getString("owner_id"), apiRestResponse.getBody().getString(
                        "owner_id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                        "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("updated_at"), apiRestResponse.getBody().getString(
                        "updated_at"));
    }
    
    /**
     * Positive test case for getCalendarEntry method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {getCalendarEntry} integration test with optional parameters.", dependsOnMethods = { "testCreateCalendarEntryWithMandatoryParameters" })
    public void testGetCalendarEntryWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:getCalendarEntry");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_getCalendarEntry_optional.json");
        
        String apiEndpoint =
                        apiEndpointUrl + "/calendar_entries/"
                                        + connectorProperties.getProperty("calendarEntryIdMandatory") + ".json"
                                        + authString;
        RestResponse<JSONObject> apiRestResponseOne = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        apiEndpoint =
                        apiEndpointUrl + "/calendar_entries/"
                                        + connectorProperties.getProperty("calendarEntryIdMandatory") + ".json"
                                        + authString + "&attrs=id,name,description,created_at";
        RestResponse<JSONObject> apiRestResponseTwo = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponseOne.getBody().length(), esbRestResponse.getBody().length());
        Assert.assertEquals(apiRestResponseTwo.getBody().length(), esbRestResponse.getBody().length());
        Assert.assertEquals(apiRestResponseTwo.getBody().getString("id"), esbRestResponse.getBody().getString("id"));
        Assert.assertEquals(apiRestResponseTwo.getBody().getString("name"), esbRestResponse.getBody().getString("name"));
        Assert.assertEquals(apiRestResponseTwo.getBody().getString("description"), esbRestResponse.getBody().getString(
                        "description"));
        Assert.assertEquals(apiRestResponseTwo.getBody().getString("created_at"), esbRestResponse.getBody().getString(
                        "created_at"));
    }
    
    /**
     * Negative test case for getCalendarEntry method.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {getCalendarEntry} integration test with negative case.")
    public void testGetCalendarEntryWithNegativeCase() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:getCalendarEntry");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_getCalendarEntry_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/calendar_entries/INVALID.json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listCalendarEntries method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {listCalendarEntries} integration test with mandatory parameters.", dependsOnMethods = {
                    "testCreateCalendarEntryWithMandatoryParameters", "testCreateCalendarEntryWithOptionalParameters" })
    public void testListCalendarEntriesWithMandatoryParameters() throws IOException, JSONException,
                    InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:listCalendarEntries");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_listCalendarEntries_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/calendar_entries.json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("name"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("created_at"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("updated_at"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").length(), apiRestResponse.getBody()
                        .getJSONArray("entries").length());
    }
    
    /**
     * Positive test case for listCalendarEntries method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {listCalendarEntries} integration test with optional parameters.", dependsOnMethods = {
                    "testCreateCalendarEntryWithMandatoryParameters", "testCreateCalendarEntryWithOptionalParameters" })
    public void testListCalendarEntriesWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:listCalendarEntries");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_listCalendarEntries_optional.json");
        
        final String apiEndpoint = apiEndpointUrl + "/calendar_entries.json" + authString + "&page=1&per_page=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("name"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("created_at"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("updated_at"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").length(), 1);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("entries").length(), 1);
    }
    
    /**
     * Negative test case for listCalendarEntries method.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {listCalendarEntries} integration test with negative case.")
    public void testListCalendarEntriesWithNegativeCase() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:listCalendarEntries");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_listCalendarEntries_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/calendar_entries.json" + authString + "&page=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getBody().getString("errors"), apiRestResponse.getBody()
                        .getString("errors"));
    }
    
    /**
     * Method name: createDocument
     * Test scenario: Mandatory
     * Reason to skip: There are no parameters returned in the response which can be asserted.
     */
    
    /**
     * Positive test case for createDocument method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {createDocument} integration test with optional parameters.", dependsOnMethods = { "testCreateDealWithOptionalParameters" })
    public void testCreateDocumentWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:createDocument");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDocument_optional.json");
        
        final String documentIdOptional = esbRestResponse.getBody().getString("id");
        connectorProperties.put("documentIdOptional", documentIdOptional);
        
        final String userId = esbRestResponse.getBody().getString("owner_id");
        connectorProperties.put("userId", userId);
        
        final String apiEndpoint = apiEndpointUrl + "/documents/" + documentIdOptional + ".json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("documentTitleOptional"), apiRestResponse.getBody()
                        .getString("title"));
        Assert.assertEquals(connectorProperties.getProperty("dealId"), apiRestResponse.getBody().getString("deal_id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                        "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("document_type"), apiRestResponse.getBody().getString(
                        "document_type"));
        Assert.assertEquals(esbRestResponse.getBody().getString("owner_id"), apiRestResponse.getBody().getString(
                        "owner_id"));
    }
    
    /**
     * Negative test case for createDocument method.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {createDocument} integration test with negative case.")
    public void testCreateDocumentWithNegativeCase() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:createDocument");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDocument_negative.json");
        final JSONArray esbRestResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndpoint = apiEndpointUrl + "/documents.json" + authString;
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_createDocument_negative.json");
        final JSONArray apiRestResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponseArray.getJSONObject(0).getString("humanized_field"), apiRestResponseArray
                        .getJSONObject(0).getString("humanized_field"));
        Assert.assertEquals(esbRestResponseArray.getJSONObject(0).getString("field"), apiRestResponseArray
                        .getJSONObject(0).getString("field"));
        Assert.assertEquals(esbRestResponseArray.getJSONObject(0).getString("msg"), apiRestResponseArray.getJSONObject(
                        0).getString("msg"));
    }
    
    /**
     * Positive test case for getDocument method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {getDocument} integration test with mandatory parameters.", dependsOnMethods = { "testCreateDocumentWithOptionalParameters" })
    public void testGetDocumentWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:getDocument");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDocument_mandatory.json");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/documents/" + connectorProperties.getProperty("documentIdOptional")
                                        + ".json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("title"), apiRestResponse.getBody().getString("title"));
        Assert.assertEquals(esbRestResponse.getBody().getString("upload_status"), apiRestResponse.getBody().getString(
                        "upload_status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("deal_id"), apiRestResponse.getBody().getString(
                        "deal_id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                        "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("updated_at"), apiRestResponse.getBody().getString(
                        "updated_at"));
    }
    
    /**
     * Positive test case for getDocument method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {getDocument} integration test with optional parameters.", dependsOnMethods = { "testCreateDocumentWithOptionalParameters" })
    public void testGetDocumentWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:getDocument");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDocument_optional.json");
        
        String apiEndpoint =
                        apiEndpointUrl + "/documents/" + connectorProperties.getProperty("documentIdOptional")
                                        + ".json" + authString;
        RestResponse<JSONObject> apiRestResponseOne = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        apiEndpoint =
                        apiEndpointUrl + "/documents/" + connectorProperties.getProperty("documentIdOptional")
                                        + ".json" + authString + "&attrs=id,title,upload_status,created_at";
        RestResponse<JSONObject> apiRestResponseTwo = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponseOne.getBody().length(), esbRestResponse.getBody().length());
        Assert.assertEquals(apiRestResponseTwo.getBody().length(), esbRestResponse.getBody().length());
        Assert.assertEquals(apiRestResponseTwo.getBody().getString("id"), esbRestResponse.getBody().getString("id"));
        Assert.assertEquals(apiRestResponseTwo.getBody().getString("title"), esbRestResponse.getBody().getString(
                        "title"));
        Assert.assertEquals(apiRestResponseTwo.getBody().getString("upload_status"), esbRestResponse.getBody()
                        .getString("upload_status"));
        Assert.assertEquals(apiRestResponseTwo.getBody().getString("created_at"), esbRestResponse.getBody().getString(
                        "created_at"));
    }
    
    /**
     * Negative test case for getDocument method.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {getDocument} integration test with negative case.")
    public void testGetDocumentWithNegativeCase() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:getDocument");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDocument_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/documents/INVALID.json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listDocuments method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {listDocuments} integration test with mandatory parameters.", dependsOnMethods = { "testCreateDocumentWithOptionalParameters" })
    public void testListDocumentsWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:listDocuments");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDocuments_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/documents.json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("title"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("title"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("created_at"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("updated_at"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").length(), apiRestResponse.getBody()
                        .getJSONArray("entries").length());
    }
    
    /**
     * Positive test case for listDocuments method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {listDocuments} integration test with optional parameters.", dependsOnMethods = { "testCreateDocumentWithOptionalParameters" })
    public void testListDocumentsWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:listDocuments");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDocuments_optional.json");
        
        final String apiEndpoint = apiEndpointUrl + "/documents.json" + authString + "&page=1&per_page=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("title"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("title"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("created_at"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("updated_at"),
                        apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).getString("updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").length(), 1);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("entries").length(), 1);
    }
    
    /**
     * Negative test case for listDocuments method.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {listDocuments} integration test with negative case.")
    public void testListDocumentsWithNegativeCase() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:listDocuments");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDocuments_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/documents.json" + authString + "&page=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for getAccountDetails method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {getAccountDetails} integration test with mandatory parameters.")
    public void testGetAccountDetailsWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:getAccountDetails");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_getAccountDetails_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/account.json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("currency_id"), apiRestResponse.getBody().getString(
                        "currency_id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("milestone_event_category_id"), apiRestResponse
                        .getBody().getString("milestone_event_category_id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("yearly_seats_paid_for"), apiRestResponse.getBody()
                        .getString("yearly_seats_paid_for"));
        Assert.assertEquals(esbRestResponse.getBody().getString("total_company_count"), apiRestResponse.getBody()
                        .getString("total_company_count"));
    }
    
    /**
     * Positive test case for getAccountDetails method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {getAccountDetails} integration test with optional parameters.")
    public void testGetAccountDetailsWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:getAccountDetails");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                                        "esb_getAccountDetails_optional.json");
        
        String apiEndpoint = apiEndpointUrl + "/account.json" + authString;
        RestResponse<JSONObject> apiRestResponseOne = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        apiEndpoint =
                        apiEndpointUrl + "/account.json" + authString
                                        + "&attrs=id,milestone_event_category_id,total_company_count,currency_id";
        RestResponse<JSONObject> apiRestResponseTwo = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponseOne.getBody().length(), esbRestResponse.getBody().length());
        Assert.assertEquals(apiRestResponseTwo.getBody().length(), esbRestResponse.getBody().length());
        Assert.assertEquals(apiRestResponseTwo.getBody().getString("id"), esbRestResponse.getBody().getString("id"));
        Assert.assertEquals(apiRestResponseTwo.getBody().getString("milestone_event_category_id"), esbRestResponse
                        .getBody().getString("milestone_event_category_id"));
        Assert.assertEquals(apiRestResponseTwo.getBody().getString("total_company_count"), esbRestResponse.getBody()
                        .getString("total_company_count"));
        Assert.assertEquals(apiRestResponseTwo.getBody().getString("total_company_count"), esbRestResponse.getBody()
                        .getString("total_company_count"));
    }
    
    /**
     * Method name: getAccountDetails
     * Test scenario: Negative
     * Reason to skip: The method doesn't have any parameters to test the negative case.
     */
    
    /**
     * Positive test case for getUser method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {getUser} integration test with mandatory parameters.", dependsOnMethods = { "testCreateDocumentWithOptionalParameters" })
    public void testGetUserWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:getUser");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUser_mandatory.json");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/users/" + connectorProperties.getProperty("userId") + ".json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("first_name"), apiRestResponse.getBody().getString(
                        "first_name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("last_name"), apiRestResponse.getBody().getString(
                        "last_name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("account_id"), apiRestResponse.getBody().getString(
                        "account_id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("email"), apiRestResponse.getBody().getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getString("api_key"), apiRestResponse.getBody().getString(
                        "api_key"));
    }
    
    /**
     * Positive test case for getUser method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {getUser} integration test with optional parameters.", dependsOnMethods = { "testCreateDocumentWithOptionalParameters" })
    public void testGetUserWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:getUser");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUser_optional.json");
        
        String apiEndpoint =
                        apiEndpointUrl + "/users/" + connectorProperties.getProperty("userId") + ".json" + authString;
        RestResponse<JSONObject> apiRestResponseOne = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        apiEndpoint =
                        apiEndpointUrl + "/users/" + connectorProperties.getProperty("userId") + ".json" + authString
                                        + "&attrs=id,first_name,last_name,email,api_key";
        RestResponse<JSONObject> apiRestResponseTwo = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponseOne.getBody().length(), esbRestResponse.getBody().length());
        Assert.assertEquals(apiRestResponseTwo.getBody().length(), esbRestResponse.getBody().length());
        Assert.assertEquals(apiRestResponseTwo.getBody().getString("api_key"), esbRestResponse.getBody().getString(
                        "api_key"));
        Assert.assertEquals(apiRestResponseTwo.getBody().getString("first_name"), esbRestResponse.getBody().getString(
                        "first_name"));
        Assert.assertEquals(apiRestResponseTwo.getBody().getString("last_name"), esbRestResponse.getBody().getString(
                        "last_name"));
        Assert.assertEquals(apiRestResponseTwo.getBody().getString("email"), esbRestResponse.getBody().getString(
                        "email"));
    }
    
    /**
     * Negative test case for getUser method.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "pipelinedeals {getUser} integration test with negative case.")
    public void testGetUserWithNegativeCase() throws IOException, JSONException, InterruptedException {
        Thread.sleep(500);
        esbRequestHeadersMap.put("Action", "urn:getUser");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUser_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/users/INVALID.json" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 403);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 403);
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
}