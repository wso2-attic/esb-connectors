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

package org.wso2.carbon.connector.integration.test.clio;

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

public class ClioConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private final Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private final Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private String apiEndpointUrl;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("clio-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Accept", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("accessToken"));
        
        apiEndpointUrl = connectorProperties.getProperty("apiUrl") + "/api/v2";
        
    }
    
    /**
     * Positive test case for createContact method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "clio {createContact} integration test with mandatory parameters.")
    public void testCreateContactWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_mandatory.json");
        
        final String contactIdMandatory = esbRestResponse.getBody().getJSONObject("contact").getString("id");
        connectorProperties.put("contactIdMandatory", contactIdMandatory);
        
        final String apiEndpoint = apiEndpointUrl + "/contacts/" + contactIdMandatory;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(contactIdMandatory, apiRestResponse.getBody().getJSONObject("contact").getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("name"), apiRestResponse
                .getBody().getJSONObject("contact").getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("type"), apiRestResponse
                .getBody().getJSONObject("contact").getString("type"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("created_at"), apiRestResponse
                .getBody().getJSONObject("contact").getString("created_at"));
        
    }
    
    /**
     * Positive test case for createContact method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "clio {createContact} integration test with optional parameters.")
    public void testCreateContactWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_optional.json");
        
        final String contactIdOptional = esbRestResponse.getBody().getJSONObject("contact").getString("id");
        connectorProperties.put("contactIdOptional", contactIdOptional);
        
        final String apiEndpoint = apiEndpointUrl + "/contacts/" + contactIdOptional;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(contactIdOptional, apiRestResponse.getBody().getJSONObject("contact").getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("name"), apiRestResponse
                .getBody().getJSONObject("contact").getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("prefix"), apiRestResponse
                .getBody().getJSONObject("contact").getString("prefix"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("title"), apiRestResponse
                .getBody().getJSONObject("contact").getString("title"));
        
    }
    
    /**
     * Negative test case for createContact method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "clio {createContact} integration test with negative case.")
    public void testCreateContactWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/contacts";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createContact_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        
    }
    
    /**
     * Positive test case for getContact method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithMandatoryParameters" }, description = "clio {getContact} integration test with mandatory parameters.")
    public void testGetContactWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContact_mandatory.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/contacts/" + connectorProperties.getProperty("contactIdMandatory");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("contactIdMandatory"), apiRestResponse.getBody()
                .getJSONObject("contact").getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("name"), apiRestResponse
                .getBody().getJSONObject("contact").getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("type"), apiRestResponse
                .getBody().getJSONObject("contact").getString("type"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("created_at"), apiRestResponse
                .getBody().getJSONObject("contact").getString("created_at"));
        
    }
    
    /**
     * Positive test case for listContacts method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateContactWithOptionalParameters" }, description = "clio {listContacts} integration test with mandatory parameters.")
    public void testListContactsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/contacts";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("records"),
                apiRestResponse.getBody().getString("records"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("type"),
                apiRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("type"));
        
    }
    
    /**
     * Positive test case for listContacts method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateContactWithOptionalParameters" }, description = "clio {listContacts} integration test with optional parameters.")
    public void testListContactsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_optional.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/contacts?type=Person&query=" + connectorProperties.getProperty("updateFirstName");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("records"),
                apiRestResponse.getBody().getString("records"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("type"),
                apiRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("type"));
        
    }
    
    /**
     * Negative test case for listContacts method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "clio {listContacts} integration test with negative case.")
    public void testListContactsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/contacts?type=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        
    }
    
    /**
     * Positive test case for updateContact method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithMandatoryParameters" }, description = "clio {updateContact} integration test with optional parameters.")
    public void testUpdateContactWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateContact");
        final String apiEndpoint =
                apiEndpointUrl + "/contacts/" + connectorProperties.getProperty("contactIdMandatory");
        
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateContact_optional.json");
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        // Checking not equals with the previous values.
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getJSONObject("contact").getString("first_name"),
                apiRestResponseAfter.getBody().getJSONObject("contact").getString("first_name"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getJSONObject("contact").getString("last_name"),
                apiRestResponseAfter.getBody().getJSONObject("contact").getString("last_name"));
        
        // Checking equality with the updated values.
        Assert.assertEquals(apiRestResponseAfter.getBody().getJSONObject("contact").getString("first_name"),
                connectorProperties.getProperty("updateFirstName"));
        Assert.assertEquals(apiRestResponseAfter.getBody().getJSONObject("contact").getString("last_name"),
                connectorProperties.getProperty("updateLastName"));
        
    }
    
    /**
     * Negative test case for updateContact method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithMandatoryParameters" }, description = "clio {updateContact} integration test with negative case.")
    public void testUpdateContactWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateContact_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/contacts/" + connectorProperties.getProperty("contactIdMandatory");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_updateContact_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        
    }
    
    /**
     * Positive test case for createMatter method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithMandatoryParameters" }, description = "clio {createMatter} integration test with mandatory parameters.")
    public void testCreateMatterWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createMatter");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createMatter_mandatory.json");
        
        final String matterIdMandatory = esbRestResponse.getBody().getJSONObject("matter").getString("id");
        connectorProperties.put("matterIdMandatory", matterIdMandatory);
        
        final String apiEndpoint = apiEndpointUrl + "/matters/" + matterIdMandatory;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(matterIdMandatory, apiRestResponse.getBody().getJSONObject("matter").getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("matter").getString("description"), apiRestResponse
                .getBody().getJSONObject("matter").getString("description"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("matter").getString("status"), apiRestResponse
                .getBody().getJSONObject("matter").getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("matter").getJSONObject("client").getString("id"),
                apiRestResponse.getBody().getJSONObject("matter").getJSONObject("client").getString("id"));
        
    }
    
    /**
     * Positive test case for createMatter method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateContactWithOptionalParameters" }, description = "clio {createMatter} integration test with optional parameters.")
    public void testCreateMatterWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createMatter");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createMatter_optional.json");
        
        final String matterIdOptional = esbRestResponse.getBody().getJSONObject("matter").getString("id");
        connectorProperties.put("matterIdOptional", matterIdOptional);
        
        final String apiEndpoint = apiEndpointUrl + "/matters/" + matterIdOptional;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(matterIdOptional, apiRestResponse.getBody().getJSONObject("matter").getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("matter").getString("open_date"), apiRestResponse
                .getBody().getJSONObject("matter").getString("open_date"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("matter").getString("close_date"), apiRestResponse
                .getBody().getJSONObject("matter").getString("close_date"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("matter").getString("billable"), apiRestResponse
                .getBody().getJSONObject("matter").getString("billable"));
        
    }
    
    /**
     * Negative test case for createMatter method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "clio {createMatter} integration test with negative case.")
    public void testCreateMatterWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createMatter");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createMatter_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/matters";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createMatter_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        
    }
    
    /**
     * Positive test case for getMatter method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateMatterWithMandatoryParameters" }, description = "clio {getMatter} integration test with mandatory parameters.")
    public void testGetMatterWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getMatter");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMatter_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/matters/" + connectorProperties.getProperty("matterIdMandatory");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("matter").getString("id"), apiRestResponse
                .getBody().getJSONObject("matter").getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("matter").getJSONObject("client").getString("id"),
                apiRestResponse.getBody().getJSONObject("matter").getJSONObject("client").getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("matter").getString("status"), apiRestResponse
                .getBody().getJSONObject("matter").getString("status"));
        
    }
    
    /**
     * Positive test case for listMatters method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateMatterWithMandatoryParameters",
            "testCreateMatterWithOptionalParameters" }, description = "clio {listMatters} integration test with mandatory parameters.")
    public void testListMattersWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listMatters");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listMatters_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/matters";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("matters").length(), apiRestResponse.getBody()
                .getJSONArray("matters").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("matters").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("matters").getJSONObject(0).getString("id"));
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONArray("matters").getJSONObject(0).getString("display_number"),
                apiRestResponse.getBody().getJSONArray("matters").getJSONObject(0).getString("display_number"));
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONArray("matters").getJSONObject(0).getString("description"),
                esbRestResponse.getBody().getJSONArray("matters").getJSONObject(0).getString("description"));
        
    }
    
    /**
     * Positive test case for listMatters method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateMatterWithMandatoryParameters",
            "testCreateMatterWithOptionalParameters" }, description = "clio {listMatters} integration test with optional parameters.")
    public void testListMattersWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listMatters");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listMatters_optional.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/matters?client_id=" + connectorProperties.getProperty("contactIdMandatory");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("matters").length(), 1);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("matters").length(), 1);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("matters").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("matters").getJSONObject(0).getString("id"));
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONArray("matters").getJSONObject(0).getString("description"),
                apiRestResponse.getBody().getJSONArray("matters").getJSONObject(0).getString("description"));
        
    }
    
    /**
     * Negative test case for listMatters method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "clio {listMatters} integration test with negative case.")
    public void testListMattersWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listMatters");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listMatters_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/matters?created_since=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        
    }
    
    /**
     * Positive test case for getBill method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "clio {getBill} integration test with mandatory parameters.")
    public void testGetBillWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getBill");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBill_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/bills/" + connectorProperties.getProperty("billId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("bill").getString("id"), apiRestResponse.getBody()
                .getJSONObject("bill").getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("bill").getString("status"), apiRestResponse
                .getBody().getJSONObject("bill").getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("bill").getString("issued_at"), apiRestResponse
                .getBody().getJSONObject("bill").getString("issued_at"));
        
    }
    
    /**
     * Positive test case for listBills method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "clio {listBills} integration test with mandatory parameters.")
    public void testListBillsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listBills");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listBills_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/bills";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("bills").length(), esbRestResponse.getBody()
                .getJSONArray("bills").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("bills").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("bills").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("bills").getJSONObject(0).getString("issued_at"),
                apiRestResponse.getBody().getJSONArray("bills").getJSONObject(0).getString("issued_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("bills").getJSONObject(0).getString("status"),
                esbRestResponse.getBody().getJSONArray("bills").getJSONObject(0).getString("status"));
        
    }
    
    /**
     * Positive test case for listBills method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "clio {listBills} integration test with optional parameters.")
    public void testListBillsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listBills");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listBills_optional.json");
        
        final String apiEndpoint = apiEndpointUrl + "/bills?limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("bills").length(),
                Integer.parseInt(connectorProperties.getProperty("limit")));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("bills").length(),
                Integer.parseInt(connectorProperties.getProperty("limit")));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("bills").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("bills").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("bills").getJSONObject(0).getString("status"),
                esbRestResponse.getBody().getJSONArray("bills").getJSONObject(0).getString("status"));
        
    }
    
    /**
     * Negative test case for listBills method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "clio {listBills} integration test with negative case.")
    public void testListBillsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listBills");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listBills_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/bills?updated_since=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        
    }
    
    /**
     * Positive test case for getUser method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "clio {getUser} integration test with mandatory parameters.")
    public void testGetUserWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getUser");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUser_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/users/" + connectorProperties.getProperty("userId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").getString("id"), apiRestResponse.getBody()
                .getJSONObject("user").getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").getString("email"), apiRestResponse
                .getBody().getJSONObject("user").getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").getString("first_name"), esbRestResponse
                .getBody().getJSONObject("user").getString("first_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").getString("subscription_plan"),
                esbRestResponse.getBody().getJSONObject("user").getString("subscription_plan"));
        
    }
    
    /**
     * Positive test case for createTask method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "clio {createTask} integration test with mandatory parameters.")
    public void testCreateTaskWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_mandatory.json");
        
        final String taskIdMandatory = esbRestResponse.getBody().getJSONObject("task").getString("id");
        connectorProperties.put("taskIdMandatory", taskIdMandatory);
        
        final String apiEndpoint = apiEndpointUrl + "/tasks/" + taskIdMandatory;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("taskNameMandatory"), apiRestResponse.getBody()
                .getJSONObject("task").getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("taskPriorityMandatory"), apiRestResponse.getBody()
                .getJSONObject("task").getString("priority"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").getString("created_at"), apiRestResponse
                .getBody().getJSONObject("task").getString("created_at"));
    }
    
    /**
     * Positive test case for createTask method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "clio {createTask} integration test with optional parameters.")
    public void testCreateTaskWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_optional.json");
        
        final String taskIdOptional = esbRestResponse.getBody().getJSONObject("task").getString("id");
        
        final String apiEndpoint = apiEndpointUrl + "/tasks/" + taskIdOptional;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("taskDescriptionOptional"), apiRestResponse.getBody()
                .getJSONObject("task").getString("description"));
        Assert.assertEquals(connectorProperties.getProperty("taskDueAtOptional"), apiRestResponse.getBody()
                .getJSONObject("task").getString("due_at"));
        Assert.assertEquals(connectorProperties.getProperty("taskIsPrivateOptional"), apiRestResponse.getBody()
                .getJSONObject("task").getString("is_private"));
    }
    
    /**
     * Negative test case for createTask method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "clio {createTask} integration test with optional parameters.")
    public void testCreateTaskWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/tasks";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createTask_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").getString("base"), apiRestResponse
                .getBody().getJSONObject("errors").getString("base"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").getString("name"), apiRestResponse
                .getBody().getJSONObject("errors").getString("name"));
    }
    
    /**
     * Positive test case for updateTask method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTaskWithMandatoryParameters" }, description = "clio {updateTask} integration test with optional parameters.")
    public void testUpdateTaskWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateTask");
        final String apiEndpoint = apiEndpointUrl + "/tasks/" + connectorProperties.getProperty("taskIdMandatory");
        
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTask_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        // Checking inequality with previous values.
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getJSONObject("task").getString("name"),
                apiRestResponseAfter.getBody().getJSONObject("task").getString("name"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getJSONObject("task").getString("name"),
                apiRestResponseAfter.getBody().getJSONObject("task").getString("is_private"));
        
        // Checking equality with new values.
        Assert.assertEquals(connectorProperties.getProperty("updatedTaskName"), apiRestResponseAfter.getBody()
                .getJSONObject("task").getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("updatedTaskIsPrivate"), apiRestResponseAfter.getBody()
                .getJSONObject("task").getString("is_private"));
    }
    
    /**
     * Negative test case for updateTask method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "clio {updateTask} integration test with negative case.")
    public void testUpdateTaskWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTask_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/tasks/" + connectorProperties.getProperty("taskIdMandatory");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_updateTask_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").getString("priority"), apiRestResponse
                .getBody().getJSONObject("errors").getString("priority"));
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
    }
    
    /**
     * Positive test case for getTask method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTaskWithMandatoryParameters" }, description = "clio {getTask} integration test with mandatory parameters.")
    public void testGetTaskWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTask_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/tasks/" + connectorProperties.getProperty("taskIdMandatory");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").getString("name"), apiRestResponse
                .getBody().getJSONObject("task").getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").getString("is_statute_of_limitations"),
                apiRestResponse.getBody().getJSONObject("task").getString("is_statute_of_limitations"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").getString("created_at"), apiRestResponse
                .getBody().getJSONObject("task").getString("created_at"));
    }
    
    /**
     * Positive test case for listTasks method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTaskWithMandatoryParameters",
            "testCreateTaskWithOptionalParameters" }, description = "clio {listTasks} integration test with mandatory parameters.")
    public void testListTasksWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTasks");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/tasks";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tasks").length(), apiRestResponse.getBody()
                .getJSONArray("tasks").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tasks").getJSONObject(0).getString("name"),
                apiRestResponse.getBody().getJSONArray("tasks").getJSONObject(0).getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tasks").getJSONObject(0).getString("priority"),
                apiRestResponse.getBody().getJSONArray("tasks").getJSONObject(0).getString("priority"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tasks").getJSONObject(0).getString("due_at"),
                apiRestResponse.getBody().getJSONArray("tasks").getJSONObject(0).getString("due_at"));
        
    }
    
    /**
     * Positive test case for listTasks method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTaskWithMandatoryParameters",
            "testCreateTaskWithOptionalParameters" }, description = "clio {listTasks} integration test with optional parameters.")
    public void testListTasksWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTasks");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_optional.json");
        
        final String apiEndpoint = apiEndpointUrl + "/tasks?completed=true";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final int lengthEsbResponse = esbRestResponse.getBody().getJSONArray("tasks").length();
        final int lengthAPIResponse = apiRestResponse.getBody().getJSONArray("tasks").length();
        
        // Assert the Array length
        Assert.assertEquals(lengthEsbResponse, lengthAPIResponse);
        
        // Assert the status of the first element.
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tasks").getJSONObject(0).getBoolean("complete"),
                true);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("tasks").getJSONObject(0).getBoolean("complete"),
                true);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tasks").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("tasks").getJSONObject(0).getString("id"));
        
        // Assert the status of the last element.
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tasks").getJSONObject(lengthEsbResponse - 1)
                .getBoolean("complete"), true);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("tasks").getJSONObject(lengthAPIResponse - 1)
                .getBoolean("complete"), true);
        
    }
    
    /**
     * Negative test case for listTasks method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "clio {listTasks} integration test with negative case.")
    public void testListTasksWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTasks");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/tasks?assignee_id=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        
    }
    
}
