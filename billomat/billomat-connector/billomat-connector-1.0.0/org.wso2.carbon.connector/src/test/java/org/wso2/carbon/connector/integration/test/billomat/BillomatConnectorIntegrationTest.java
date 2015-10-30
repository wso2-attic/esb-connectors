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

package org.wso2.carbon.connector.integration.test.billomat;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class BillomatConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap;
    
    private Map<String, String> apiRequestHeadersMap;
    
    private String authString;
    
    private String apiEndpointUrl;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("billomat-connector-1.0.0");
        esbRequestHeadersMap = new HashMap<String, String>();
        apiRequestHeadersMap = new HashMap<String, String>();
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Accept", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        authString = "?api_key=" + connectorProperties.getProperty("apiKey");
        apiEndpointUrl = connectorProperties.getProperty("apiUrl") + "/api";
        
    }
    
    /**
     * Positive test case for createClient method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {createClient} integration test with mandatory parameters.")
    public void testCreateClientWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createClient");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createClient_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONObject("client");
        final String clientId = esbResponse.getString("id");
        connectorProperties.put("clientId", clientId);
        
        final String apiEndpoint =
                apiEndpointUrl + "/clients/" + connectorProperties.getProperty("clientId") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONObject("client");
        
        Assert.assertEquals(esbResponse.getString("number"), apiResponse.getString("number"));
        Assert.assertEquals(esbResponse.getString("created"), apiResponse.getString("created"));
        Assert.assertEquals(esbResponse.getString("client_number"), apiResponse.getString("client_number"));
    }
    
    /**
     * Positive test case for createClient method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {createClient} integration test with optional parameters.")
    public void testCreateClientWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createClient");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createClient_optional.json");
        
        final String clientIdOpt = esbRestResponse.getBody().getJSONObject("client").getString("id");
        
        final String apiEndpoint = apiEndpointUrl + "/clients/" + clientIdOpt + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONObject("client");
        
        final String clientNumber = apiResponse.getString("client_number");
        connectorProperties.put("clientCNumber", clientNumber);
        
        Assert.assertEquals(connectorProperties.getProperty("clientName"), apiResponse.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("clientNumberPre"), apiResponse.getString("number_pre"));
        Assert.assertEquals(connectorProperties.getProperty("clientNumber"), apiResponse.getString("number"));
        Assert.assertEquals(connectorProperties.getProperty("clientSalutation"), apiResponse.getString("salutation"));
        Assert.assertEquals(connectorProperties.getProperty("clientFirstName"), apiResponse.getString("first_name"));
    }
    
    /**
     * Negative test case for createClient method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {createClient} integration test with negative case.")
    public void testCreateClientWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createClient");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createClient_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/clients" + authString;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createClient_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").get("error"), apiRestResponse.getBody()
                .getJSONObject("errors").get("error"));
    }
    
    /**
     * Positive test case for getClient method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {getClient} integration test with mandatory parameters.",
            dependsOnMethods = { "testCreateClientWithMandatoryParameters" })
    public void testGetClientWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getClient");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getClient_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONObject("client");
        
        final String apiEndpoint =
                apiEndpointUrl + "/clients/" + connectorProperties.getProperty("clientId") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONObject("client");
        
        Assert.assertEquals(esbResponse.getString("due_days"), apiResponse.getString("due_days"));
        Assert.assertEquals(esbResponse.getString("created"), apiResponse.getString("created"));
        Assert.assertEquals(esbResponse.getString("client_number"), apiResponse.getString("client_number"));
    }
    
    /**
     * Negative test case for getClient method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {getClient} integration test with negative case.")
    public void testGetClientWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getClient");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getClient_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/clients/invalid" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").get("error"), apiRestResponse.getBody()
                .getJSONObject("errors").get("error"));
    }
    
    /**
     * Positive test case for listClients method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {listClients} integration test with mandatory parameters.",
            dependsOnMethods = { "testCreateClientWithMandatoryParameters" })
    public void testListClientsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listClients");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listClients_mandatory.json");
        
        final JSONObject esbResponse =
                esbRestResponse.getBody().getJSONObject("clients").getJSONArray("client").getJSONObject(0);
        
        final String apiEndpoint = apiEndpointUrl + "/clients/" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse =
                apiRestResponse.getBody().getJSONObject("clients").getJSONArray("client").getJSONObject(0);
        
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(esbResponse.getString("created"), apiResponse.getString("created"));
        Assert.assertEquals(esbResponse.getString("client_number"), apiResponse.getString("client_number"));
    }
    
    /**
     * Positive test case for listClients method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {listClients} integration test with optional parameters.",
            dependsOnMethods = { "testCreateClientWithOptionalParameters" })
    public void testListClientsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listClients");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listClients_optional.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/clients/" + authString + "&name="
                        + URLEncoder.encode(connectorProperties.getProperty("clientName"), "UTF-8") + "&first_name="
                        + connectorProperties.getProperty("clientFirstName") + "&last_name="
                        + connectorProperties.getProperty("clientLastName") + "&client_number="
                        + URLEncoder.encode(connectorProperties.getProperty("clientCNumber"), "UTF-8") + "&per_page="
                        + connectorProperties.getProperty("perPage");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONObject("clients").getJSONObject("client");
        
        Assert.assertEquals(connectorProperties.getProperty("clientName"), apiResponse.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("clientFirstName"), apiResponse.getString("first_name"));
        Assert.assertEquals(connectorProperties.getProperty("clientLastName"), apiResponse.getString("last_name"));
        Assert.assertEquals(connectorProperties.getProperty("clientCNumber"), apiResponse.getString("client_number"));
        Assert.assertEquals(connectorProperties.getProperty("perPage"),
                apiRestResponse.getBody().getJSONObject("clients").getString("@per_page"));
    }
    
    /**
     * Positive test case for updateClient method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {updateClient} integration test with optional parameters.",
            dependsOnMethods = { "testCreateClientWithMandatoryParameters" })
    public void testUpdateClientWithOptionalParameters() throws IOException, JSONException {
    
        final String apiEndPoint =
                apiEndpointUrl + "/clients/" + connectorProperties.getProperty("clientId") + authString;
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponseBefore = apiRestResponseBefore.getBody().getJSONObject("client");
        
        esbRequestHeadersMap.put("Action", "urn:updateClient");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateClient_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponseAfter = apiRestResponseAfter.getBody().getJSONObject("client");
        
        Assert.assertNotEquals(apiResponseBefore.getString("name"), apiResponseAfter.getString("name"));
        Assert.assertNotEquals(apiResponseBefore.getString("first_name"), apiResponseAfter.getString("first_name"));
        Assert.assertNotEquals(apiResponseBefore.getString("last_name"), apiResponseAfter.getString("last_name"));
        Assert.assertNotEquals(apiResponseBefore.getString("salutation"), apiResponseAfter.getString("salutation"));
        Assert.assertNotEquals(apiResponseBefore.getString("number_pre"), apiResponseAfter.getString("number_pre"));
        
        Assert.assertEquals(connectorProperties.getProperty("updatedClientName"), apiResponseAfter.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("updatedClientSalutation"),
                apiResponseAfter.getString("salutation"));
        Assert.assertEquals(connectorProperties.getProperty("updatedClientNumberPre"),
                apiResponseAfter.getString("number_pre"));
        Assert.assertEquals(connectorProperties.getProperty("updatedClientFirstName"),
                apiResponseAfter.getString("first_name"));
        Assert.assertEquals(connectorProperties.getProperty("updatedClientLastName"),
                apiResponseAfter.getString("last_name"));
    }
    
    /**
     * Negative test case for updateClient method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {updateClient} integration test with negative case.",
            dependsOnMethods = { "testCreateClientWithMandatoryParameters" })
    public void testUpdateClientWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateClient");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateClient_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/clients/" + connectorProperties.getProperty("clientId") + authString;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_updateClient_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").get("error"), apiRestResponse.getBody()
                .getJSONObject("errors").get("error"));
    }
    
    /**
     * Positive test case for createContact method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {createContact} integration test with mandatory parameters.",
            dependsOnMethods = { "testCreateClientWithMandatoryParameters" })
    public void testCreateContactWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONObject("contact");
        
        final String contactId = esbResponse.getString("id");
        connectorProperties.put("contactId", contactId);
        
        final String apiEndpoint =
                apiEndpointUrl + "/contacts/" + connectorProperties.getProperty("contactId") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONObject("contact");
        
        Assert.assertEquals(connectorProperties.getProperty("clientId"), apiResponse.getString("client_id"));
        Assert.assertEquals(esbResponse.getString("created"), apiResponse.getString("created"));
        Assert.assertEquals(esbResponse.getString("country_code"), apiResponse.getString("country_code"));
    }
    
    /**
     * Positive test case for createContact method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {createContact} integration test with optional parameters.",
            dependsOnMethods = { "testCreateClientWithMandatoryParameters" })
    public void testCreateContactWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_optional.json");
        
        final String contactId = esbRestResponse.getBody().getJSONObject("contact").getString("id");
        
        final String apiEndpoint = apiEndpointUrl + "/contacts/" + contactId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONObject("contact");
        
        Assert.assertEquals(connectorProperties.getProperty("contactName"), apiResponse.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("contactSalutation"), apiResponse.getString("salutation"));
        Assert.assertEquals(connectorProperties.getProperty("contactFirstName"), apiResponse.getString("first_name"));
        Assert.assertEquals(connectorProperties.getProperty("contactCity"), apiResponse.getString("city"));
        Assert.assertEquals(connectorProperties.getProperty("contactEmail"), apiResponse.getString("email"));
    }
    
    /**
     * Negative test case for createContact method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {createContact} integration test with negative case.",
            dependsOnMethods = { "testCreateClientWithMandatoryParameters" })
    public void testCreateContactWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/contacts" + authString;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createContact_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").get("error"), apiRestResponse.getBody()
                .getJSONObject("errors").get("error"));
    }
    
    /**
     * Positive test case for getContact method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {getContact} integration test with mandatory parameters.",
            dependsOnMethods = { "testCreateContactWithMandatoryParameters" })
    public void testGetContactWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContact_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONObject("contact");
        
        final String apiEndpoint =
                apiEndpointUrl + "/contacts/" + connectorProperties.getProperty("contactId") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONObject("contact");
        
        Assert.assertEquals(esbResponse.getString("created"), apiResponse.getString("created"));
        Assert.assertEquals(esbResponse.getString("client_id"), apiResponse.getString("client_id"));
        Assert.assertEquals(esbResponse.getString("country_code"), apiResponse.getString("country_code"));
    }
    
    /**
     * Negative test case for getContact method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {getContact} integration test with negative case.")
    public void testGetContactWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContact_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/contacts/invalid" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").get("error"), apiRestResponse.getBody()
                .getJSONObject("errors").get("error"));;
    }
    
    /**
     * Positive test case for listContacts method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {listContacts} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateContactWithMandatoryParameters", "testCreateContactWithOptionalParameters" })
    public void testListContactsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_mandatory.json");
        
        final JSONObject esbResponse =
                esbRestResponse.getBody().getJSONObject("contacts").getJSONArray("contact").getJSONObject(0);
        
        final String apiEndpoint =
                apiEndpointUrl + "/contacts/" + authString + "&client_id="
                        + connectorProperties.getProperty("clientId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse =
                apiRestResponse.getBody().getJSONObject("contacts").getJSONArray("contact").getJSONObject(0);
        
        Assert.assertEquals(esbResponse.getString("created"), apiResponse.getString("created"));
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(esbResponse.getString("country_code"), apiResponse.getString("country_code"));
    }
    
    /**
     * Positive test case for listContacts method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {listContacts} integration test with optional parameters.", dependsOnMethods = {
            "testCreateContactWithMandatoryParameters", "testCreateContactWithOptionalParameters" })
    public void testListContactsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONObject("contacts").getJSONObject("contact");
        
        final String apiEndpoint =
                apiEndpointUrl + "/contacts/" + authString + "&client_id="
                        + connectorProperties.getProperty("clientId") + "&per_page="
                        + connectorProperties.getProperty("perPage") + "&page="
                        + connectorProperties.getProperty("page");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONObject("contacts").getJSONObject("contact");
        
        Assert.assertEquals(connectorProperties.getProperty("perPage"),
                apiRestResponse.getBody().getJSONObject("contacts").getString("@per_page"));
        Assert.assertEquals(connectorProperties.getProperty("page"), apiRestResponse.getBody()
                .getJSONObject("contacts").getString("@page"));
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(esbResponse.getString("created"), apiResponse.getString("created"));
        Assert.assertEquals(esbResponse.getString("country_code"), apiResponse.getString("country_code"));
    }
    
    /**
     * Negative test case for listContacts method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {listContacts} integration test with negative case.")
    public void testListContactsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/contacts" + authString + "&client_id=";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        // When client ID is not provided the API responds with '403' forbidden
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 403);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 403);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").get("error"), apiRestResponse.getBody()
                .getJSONObject("errors").get("error"));;
    }
    
    /**
     * Positive test case for updateContact method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {updateContact} integration test with optional parameters.",
            dependsOnMethods = { "testCreateContactWithMandatoryParameters" })
    public void testUpdateContactWithOptionalParameters() throws IOException, JSONException {
    
        final String apiEndPoint =
                apiEndpointUrl + "/contacts/" + connectorProperties.getProperty("contactId") + authString;
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponseBefore = apiRestResponseBefore.getBody().getJSONObject("contact");
        
        esbRequestHeadersMap.put("Action", "urn:updateContact");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateContact_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponseAfter = apiRestResponseAfter.getBody().getJSONObject("contact");
        
        Assert.assertNotEquals(apiResponseBefore.getString("name"), apiResponseAfter.getString("name"));
        Assert.assertNotEquals(apiResponseBefore.getString("first_name"), apiResponseAfter.getString("first_name"));
        Assert.assertNotEquals(apiResponseBefore.getString("city"), apiResponseAfter.getString("city"));
        Assert.assertNotEquals(apiResponseBefore.getString("salutation"), apiResponseAfter.getString("salutation"));
        Assert.assertNotEquals(apiResponseBefore.getString("email"), apiResponseAfter.getString("email"));
        
        Assert.assertEquals(connectorProperties.getProperty("updatedContactName"), apiResponseAfter.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("updatedContactSalutation"),
                apiResponseAfter.getString("salutation"));
        Assert.assertEquals(connectorProperties.getProperty("updatedContactFirstName"),
                apiResponseAfter.getString("first_name"));
        Assert.assertEquals(connectorProperties.getProperty("updatedContactEmail"), apiResponseAfter.getString("email"));
        Assert.assertEquals(connectorProperties.getProperty("updateContactCity"), apiResponseAfter.getString("city"));
    }
    
    /**
     * Negative test case for updateContact.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {updateContact} integration test with negative case.",
            dependsOnMethods = { "testCreateContactWithMandatoryParameters" })
    public void testUpdateContactWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateContact_negative.json");
        
        final String apiEndPoint =
                apiEndpointUrl + "/contacts/" + connectorProperties.getProperty("contactId") + authString;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateContact_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").get("error"), apiRestResponse.getBody()
                .getJSONObject("errors").get("error"));
    }
    
    /**
     * Positive test case for createInvoice method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {createInvoice} integration test with mandatory parameters.",
            dependsOnMethods = { "testCreateClientWithMandatoryParameters" })
    public void testCreateInvoiceWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONObject("invoice");
        
        final String invoiceId = esbResponse.getString("id");
        connectorProperties.put("invoiceId", invoiceId);
        
        final String apiEndpoint =
                apiEndpointUrl + "/invoices/" + connectorProperties.getProperty("invoiceId") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONObject("invoice");
        
        Assert.assertEquals(connectorProperties.getProperty("clientId"), apiResponse.getString("client_id"));
        Assert.assertEquals(esbResponse.getString("created"), apiResponse.getString("created"));
        Assert.assertEquals(esbResponse.getString("invoice_number"), apiResponse.getString("invoice_number"));
        Assert.assertEquals(esbResponse.getString("due_days"), apiResponse.getString("due_days"));
        Assert.assertEquals(esbResponse.getString("quote"), apiResponse.getString("quote"));
    }
    
    /**
     * Positive test case for createInvoice method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {createInvoice} integration test with optional parameters.", dependsOnMethods = {
            "testCreateContactWithMandatoryParameters" })
    public void testCreateInvoiceWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_optional.json");
        
        final String invoiceId = esbRestResponse.getBody().getJSONObject("invoice").getString("id");
        connectorProperties.put("invoiceIdOpt", invoiceId);
        
        final String apiEndpoint = apiEndpointUrl + "/invoices/" + invoiceId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONObject("invoice");
        
        Assert.assertEquals(connectorProperties.getProperty("contactId"), apiResponse.getString("contact_id"));
        Assert.assertEquals(connectorProperties.getProperty("invoiceNumberPre"), apiResponse.getString("number_pre"));
        Assert.assertEquals(connectorProperties.getProperty("invoiceNumber"), apiResponse.getString("number"));
        Assert.assertEquals(connectorProperties.getProperty("invoiceTitle"), apiResponse.getString("title"));
        Assert.assertEquals(connectorProperties.getProperty("invoiceDate"), apiResponse.getString("date"));
    }
    
    /**
     * Negative test case for createInvoice method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {createInvoice} integration test with negative case.",
            dependsOnMethods = { "testCreateClientWithMandatoryParameters" })
    public void testCreateInvoiceWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_negative.json");
        final String apiEndPoint = apiEndpointUrl + "/invoices/" + authString;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createInvoice_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").get("error"), apiRestResponse.getBody()
                .getJSONObject("errors").get("error"));
    }
    
    /**
     * Positive test case for getInvoice method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {getInvoice} integration test with mandatory parameters.",
            dependsOnMethods = { "testCreateInvoiceWithMandatoryParameters" })
    public void testGetInvoiceWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getInvoice");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getInvoice_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONObject("invoice");
        
        final String apiEndpoint =
                apiEndpointUrl + "/invoices/" + connectorProperties.getProperty("invoiceId") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONObject("invoice");
        
        Assert.assertEquals(esbResponse.getString("client_id"), apiResponse.getString("client_id"));
        Assert.assertEquals(esbResponse.getString("created"), apiResponse.getString("created"));
        Assert.assertEquals(esbResponse.getString("invoice_number"), apiResponse.getString("invoice_number"));
        Assert.assertEquals(esbResponse.getString("due_days"), apiResponse.getString("due_days"));
        Assert.assertEquals(esbResponse.getString("quote"), apiResponse.getString("quote"));
    }
    
    /**
     * Negative case for getInvoice method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {getInvoice} integration test with negative case.",
            dependsOnMethods = { "testCreateClientWithMandatoryParameters" })
    public void testGetInvoiceWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getInvoice");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getInvoice_negative.json");
        final String apiEndpoint = apiEndpointUrl + "/invoices/invalid" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").get("error"), apiRestResponse.getBody()
                .getJSONObject("errors").get("error"));
    }
    
    /**
     * Positive test case for listInvoices method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {listInvoices} integration test with mandatory parameters.",
            dependsOnMethods = { "testCreateInvoiceWithMandatoryParameters", "testCreateInvoiceWithOptionalParameters"})
    public void testListInvoicesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listInvoices");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoices_mandatory.json");
        
        final JSONObject esbResponse =
                esbRestResponse.getBody().getJSONObject("invoices").getJSONArray("invoice").getJSONObject(0);
        
        final String apiEndpoint = apiEndpointUrl + "/invoices/" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse =
                apiRestResponse.getBody().getJSONObject("invoices").getJSONArray("invoice").getJSONObject(0);
        
        Assert.assertEquals(esbResponse.getString("client_id"), apiResponse.getString("client_id"));
        Assert.assertEquals(esbResponse.getString("created"), apiResponse.getString("created"));
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(esbResponse.getString("due_days"), apiResponse.getString("due_days"));
        Assert.assertEquals(esbResponse.getString("quote"), apiResponse.getString("quote"));
    }
    
    /**
     * Positive test case for listInvoices method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {listInvoices} integration test with optional parameters.", dependsOnMethods = {
            "testCreateInvoiceWithMandatoryParameters", "testCreateInvoiceWithOptionalParameters",
            "testCreateContactWithMandatoryParameters" })
    public void testListInvoicesWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listInvoices");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoices_optional.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/invoices/" + authString + "&client_id="
                        + connectorProperties.getProperty("clientId") + "&per_page="
                        + connectorProperties.getProperty("perPage") + "&page="
                        + connectorProperties.getProperty("page") + "&contact_id="
                        + connectorProperties.getProperty("contactId") + "&status="
                        + connectorProperties.getProperty("invoiceStatus");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONObject("invoices").getJSONObject("invoice");
        
        Assert.assertEquals(connectorProperties.getProperty("contactId"), apiResponse.getString("contact_id"));
        Assert.assertEquals(connectorProperties.getProperty("clientId"), apiResponse.getString("client_id"));
        Assert.assertEquals(connectorProperties.getProperty("invoiceStatus"), apiResponse.getString("status"));
        Assert.assertEquals(connectorProperties.getProperty("page"), apiRestResponse.getBody()
                .getJSONObject("invoices").getString("@page"));
        Assert.assertEquals(connectorProperties.getProperty("perPage"),
                apiRestResponse.getBody().getJSONObject("invoices").getString("@page"));
    }
    
    /**
     * Negative test case for listInvoices method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {listInvoices} integration test with negative case.")
    public void testListInvoicesWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listInvoices");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoices_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/invoices" + authString + "&status=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").get("error"), apiRestResponse.getBody()
                .getJSONObject("errors").get("error"));
    }
    
    /**
     * Positive test case for updateInvoice method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {updateInvoice} integration test with optional parameters.",
            dependsOnMethods = { "testCreateInvoiceWithMandatoryParameters" })
    public void testUpdateInvoiceWithOptionalParameters() throws IOException, JSONException {
    
        final String apiEndPoint =
                apiEndpointUrl + "/invoices/" + connectorProperties.getProperty("invoiceId") + authString;
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponseBefore = apiRestResponseBefore.getBody().getJSONObject("invoice");
        
        esbRequestHeadersMap.put("Action", "urn:updateInvoice");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateInvoice_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponseAfter = apiRestResponseAfter.getBody().getJSONObject("invoice");
        
        Assert.assertNotEquals(apiResponseBefore.getString("title"), apiResponseAfter.getString("title"));
        Assert.assertNotEquals(apiResponseBefore.getString("number"), apiResponseAfter.getString("number"));
        Assert.assertNotEquals(apiResponseBefore.getString("number_pre"), apiResponseAfter.getString("number_pre"));
        Assert.assertNotEquals(apiResponseBefore.getString("date"), apiResponseAfter.getString("date"));
        Assert.assertNotEquals(apiResponseBefore.getString("discount_date"),
                apiResponseAfter.getString("discount_date"));
        
        Assert.assertEquals(connectorProperties.getProperty("invoiceDiscountDate"),
                apiResponseAfter.getString("discount_date"));
        Assert.assertEquals(connectorProperties.getProperty("invoiceNumberPre"),
                apiResponseAfter.getString("number_pre"));
        Assert.assertEquals(connectorProperties.getProperty("invoiceNumber"), apiResponseAfter.getString("number"));
        Assert.assertEquals(connectorProperties.getProperty("invoiceTitle"), apiResponseAfter.getString("title"));
        Assert.assertEquals(connectorProperties.getProperty("invoiceDate"), apiResponseAfter.getString("date"));
    }
    
    /**
     * Negative test case for updateInvoice method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {updateInvoice} integration test with negative case.",
            dependsOnMethods = { "testCreateInvoiceWithMandatoryParameters" })
    public void testUpdateInvoiceWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateInvoice");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateInvoice_negative.json");
        
        final String apiEndPoint =
                apiEndpointUrl + "/invoices/" + connectorProperties.getProperty("invoiceId") + authString;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateInvoice_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").get("error"), apiRestResponse.getBody()
                .getJSONObject("errors").get("error"));
    }
    
    /**
     * Positive test case for completeInvoice method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {completeInvoice} integration test with mandatory parameters.",
            dependsOnMethods = { "testCreateInvoiceWithOptionalParameters" })
    public void testCompleteInvoiceWithMandatoryParameters() throws IOException, JSONException {
    
        final String apiEndPoint =
                apiEndpointUrl + "/invoices/" + connectorProperties.getProperty("invoiceIdOpt") + authString;
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponseBefore = apiRestResponseBefore.getBody().getJSONObject("invoice");
        
        esbRequestHeadersMap.put("Action", "urn:completeInvoice");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_completeInvoice_mandatory.json");

        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponseAfter = apiRestResponseAfter.getBody().getJSONObject("invoice");
        
        Assert.assertNotEquals(apiResponseBefore.getString("status"), apiResponseAfter.getString("status"));
        
        Assert.assertEquals(connectorProperties.getProperty("invoiceStatus"),apiResponseAfter.getString("status"));
    }
    
    /**
     * Method name: completeInvoice
     * Test scenario: Optional
     * Reason to skip: There are no optional parameters to process.
     */
    
    /**
     * Negative test case for completeInvoice method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {completeInvoice} integration test with negative case.",
            dependsOnMethods = { "testCreateInvoiceWithMandatoryParameters" })
    public void testCompleteInvoiceWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:completeInvoice");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_completeInvoice_negative.json");
        final String apiEndPoint =
                apiEndpointUrl + "/invoices/" + connectorProperties.getProperty("invoiceId") +"/complete"+ authString;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_completeInvoice_negative.json");
       
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").get("error"), apiRestResponse.getBody()
                .getJSONObject("errors").get("error"));
    }
    
    /**
     * Positive test case for sendInvoice method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {sendInvoice} integration test with mandatory parameters.",
            dependsOnMethods = { "testCompleteInvoiceWithMandatoryParameters" })
    public void testSendInvoiceWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:sendInvoice");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendInvoice_mandatory.json");
        
        final String apiEndPoint =
                apiEndpointUrl + "/invoices/" + connectorProperties.getProperty("invoiceIdOpt") +"/email"+ authString;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_sendInvoice_mandatory.json");
        
        //Only the status code is being asserted because no response is given
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        
    }
    
    /**
     * Method name: sendInvoice
     * Test scenario: Optional
     * Reason to skip: The optional test case is also same as the mandatory test case since no response is given.
     */
    
    /**
     * Negative test case for sendInvoice method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {sendInvoice} integration test with negative case.",
            dependsOnMethods = { "testSendInvoiceWithMandatoryParameters" })
    public void testSendInvoiceWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:sendInvoice");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendInvoice_negative.json");
        
        final String apiEndPoint =
                apiEndpointUrl + "/invoices/" + connectorProperties.getProperty("invoiceIdOpt") +"/email"+ authString;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_sendInvoice_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").get("error"), apiRestResponse.getBody()
                .getJSONObject("errors").get("error"));
    }
    
    /**
     * Positive test case for createInvoiceItem method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {createInvoiceItem} integration test with mandatory parameters.",
            dependsOnMethods = { "testCreateInvoiceWithMandatoryParameters" })
    public void testCreateInvoiceItemWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoiceItem");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoiceItem_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONObject("invoice-item");
        
        final String itemId = esbResponse.getString("id");
        connectorProperties.put("itemId", itemId);
        
        final String apiEndpoint =
                apiEndpointUrl + "/invoice-items/" + connectorProperties.getProperty("itemId") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONObject("invoice-item");
        
        Assert.assertEquals(connectorProperties.getProperty("invoiceId"), apiResponse.getString("invoice_id"));
        Assert.assertEquals(esbResponse.getString("position"), apiResponse.getString("position"));
        Assert.assertEquals(esbResponse.getString("quantity"), apiResponse.getString("quantity"));
        Assert.assertEquals(esbResponse.getString("total_net"), apiResponse.getString("total_net"));
        Assert.assertEquals(esbResponse.getString("total_gross"), apiResponse.getString("total_gross"));
    }
    
    /**
     * Positive test case for createInvoiceItem method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {createInvoiceItem} integration test with optional parameters.",
            dependsOnMethods = { "testCreateInvoiceWithMandatoryParameters" })
    public void testCreateInvoiceItemWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoiceItem");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoiceItem_optional.json");
        
        final String itemId = esbRestResponse.getBody().getJSONObject("invoice-item").getString("id");
        
        final String apiEndpoint = apiEndpointUrl + "/invoice-items/" + itemId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONObject("invoice-item");
        
        Assert.assertEquals(connectorProperties.getProperty("invoiceItemTitle"), apiResponse.getString("title"));
        Assert.assertEquals(connectorProperties.getProperty("invoiceItemDescription"),
                apiResponse.getString("description"));
        Assert.assertEquals(connectorProperties.getProperty("invoiceItemUnit"), apiResponse.getString("unit"));
        Assert.assertEquals(connectorProperties.getProperty("invoiceItemUnitPrice"),
                apiResponse.getString("unit_price"));
        Assert.assertEquals(connectorProperties.getProperty("invoiceItemQuantity"), apiResponse.getString("quantity"));
    }
    
    /**
     * Negative test case for createInvoiceItem method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {createInvoiceItem} integration test with negative test case.",
            dependsOnMethods = { "testCreateInvoiceWithMandatoryParameters" })
    public void testCreateInvoiceItemWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoiceItem");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoiceItem_negative.json");
        
        final String apiEndPoint = apiEndpointUrl + "/invoice-items/" + authString;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createInvoiceItem_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").get("error"), apiRestResponse.getBody()
                .getJSONObject("errors").get("error"));
    }
    
    /**
     * Positive test case for getInvoiceItem method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {getInvoiceItem} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateInvoiceItemWithMandatoryParameters" })
    public void testGetInvoiceItemWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getInvoiceItem");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getInvoiceItem_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONObject("invoice-item");
        
        final String invoiceItemId = esbResponse.getString("id");
        connectorProperties.put("invoiceItemId", invoiceItemId);
        
        final String apiEndpoint =
                apiEndpointUrl + "/invoice-items/" + connectorProperties.getProperty("itemId") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONObject("invoice-item");
        
        Assert.assertEquals(connectorProperties.getProperty("invoiceId"), apiResponse.getString("invoice_id"));
        Assert.assertEquals(esbResponse.getString("position"), apiResponse.getString("position"));
        Assert.assertEquals(esbResponse.getString("quantity"), apiResponse.getString("quantity"));
        Assert.assertEquals(esbResponse.getString("total_net"), apiResponse.getString("total_net"));
        Assert.assertEquals(esbResponse.getString("total_gross"), apiResponse.getString("total_gross"));
    }
    
    /**
     * Negative test case for getInvoiceItem method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {getInvoiceItem} integration test with negative test case.")
    public void testGetInvoiceItemWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getInvoiceItem");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getInvoiceItem_negative.json");
        
        final String apiEndPoint = apiEndpointUrl + "/invoice-items/invalid" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").get("error"), apiRestResponse.getBody()
                .getJSONObject("errors").get("error"));
    }
    
    /**
     * Positive test case for listInvoiceItems method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {listInvoiceItems} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateInvoiceItemWithMandatoryParameters", "testCreateInvoiceItemWithOptionalParameters" })
    public void testListInvoiceItemsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listInvoiceItems");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoiceItems_mandatory.json");
        
        final JSONObject esbResponse =
                esbRestResponse.getBody().getJSONObject("invoice-items").getJSONArray("invoice-item").getJSONObject(0);
        
        final String apiEndpoint =
                apiEndpointUrl + "/invoice-items/" + authString + "&invoice_id="
                        + connectorProperties.getProperty("invoiceId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse =
                apiRestResponse.getBody().getJSONObject("invoice-items").getJSONArray("invoice-item").getJSONObject(0);
        
        Assert.assertEquals(connectorProperties.getProperty("invoiceId"), apiResponse.getString("invoice_id"));
        Assert.assertEquals(esbResponse.getString("position"), apiResponse.getString("position"));
        Assert.assertEquals(esbResponse.getString("quantity"), apiResponse.getString("quantity"));
        Assert.assertEquals(esbResponse.getString("total_net"), apiResponse.getString("total_net"));
        Assert.assertEquals(esbResponse.getString("total_gross"), apiResponse.getString("total_gross"));
    }
    
    /**
     * Positive test case for listInvoiceItems method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {listInvoiceItems} integration test with optional parameters.", dependsOnMethods = {
            "testCreateInvoiceItemWithMandatoryParameters", "testCreateInvoiceItemWithOptionalParameters" })
    public void testListInvoiceItemsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listInvoiceItems");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoiceItems_optional.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/invoice-items/" + authString + "&invoice_id="
                        + connectorProperties.getProperty("invoiceId") + "&page="
                        + connectorProperties.getProperty("page") + "&per_page="
                        + connectorProperties.getProperty("perPage");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse =
                apiRestResponse.getBody().getJSONObject("invoice-items").getJSONObject("invoice-item");
        
        Assert.assertEquals(connectorProperties.getProperty("invoiceId"), apiResponse.getString("invoice_id"));
        Assert.assertEquals(connectorProperties.getProperty("page"),
                apiRestResponse.getBody().getJSONObject("invoice-items").getString("@page"));
        Assert.assertEquals(connectorProperties.getProperty("perPage"),
                apiRestResponse.getBody().getJSONObject("invoice-items").getString("@per_page"));
    }
    
    /**
     * Negative test case for listInvoiceItems method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {listInvoiceItems} integration test with mandatory parameters.")
    public void testListInvoiceItemsWithNegative() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listInvoiceItems");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoiceItems_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/invoice-items/" + authString + "&invoice_id=";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        // When client ID is not provided the API responds with '403' forbidden
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 403);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 403);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").get("error"), apiRestResponse.getBody()
                .getJSONObject("errors").get("error"));
    }
    
    /**
     * Positive test case for updateInvoiceItem method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {updateInvoiceItem} integration test with optional parameters.",
            dependsOnMethods = { "testCreateInvoiceItemWithMandatoryParameters" })
    public void testUpdateInvoiceItemWithOptionalParameters() throws IOException, JSONException {
    
        final String apiEndPoint =
                apiEndpointUrl + "/invoice-items/" + connectorProperties.getProperty("itemId") + authString;
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponseBefore = apiRestResponseBefore.getBody().getJSONObject("invoice-item");
        
        esbRequestHeadersMap.put("Action", "urn:updateInvoiceItem");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateInvoiceItem_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponseAfter = apiRestResponseAfter.getBody().getJSONObject("invoice-item");
        
        Assert.assertNotEquals(apiResponseBefore.getString("title"), apiResponseAfter.getString("title"));
        Assert.assertNotEquals(apiResponseBefore.getString("description"), apiResponseAfter.getString("description"));
        Assert.assertNotEquals(apiResponseBefore.getString("unit_price"), apiResponseAfter.getString("unit_price"));
        Assert.assertNotEquals(apiResponseBefore.getString("quantity"), apiResponseAfter.getString("quantity"));
        Assert.assertNotEquals(apiResponseBefore.getString("unit"), apiResponseAfter.getString("unit"));
        
        Assert.assertEquals(connectorProperties.getProperty("invoiceItemTitle"), apiResponseAfter.getString("title"));
        Assert.assertEquals(connectorProperties.getProperty("invoiceItemDescription"),
                apiResponseAfter.getString("description"));
        Assert.assertEquals(connectorProperties.getProperty("invoiceItemUnit"), apiResponseAfter.getString("unit"));
        Assert.assertEquals(connectorProperties.getProperty("invoiceItemUnitPrice"),
                apiResponseAfter.getString("unit_price"));
        Assert.assertEquals(connectorProperties.getProperty("invoiceItemQuantity"),
                apiResponseAfter.getString("quantity"));
    }
    
    /**
     * Negative test case for updateInvoiceItem method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {updateInvoiceItem} integration test with negative case.")
    public void testUpdateInvoiceItemWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateInvoiceItem");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateInvoiceItem_negative.json");
        
        final String apiEndPoint = apiEndpointUrl + "/invoice-items/invalid" + authString;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateInvoiceItem_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").get("error"), apiRestResponse.getBody()
                .getJSONObject("errors").get("error"));
    }
    
    /**
     * Positive test case for createDeliveryNote method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {createDeliveryNote} integration test with mandatory parameters.",
            dependsOnMethods = { "testCreateClientWithMandatoryParameters" })
    public void testCreateDeliveryNoteWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createDeliveryNote");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDeliveryNote_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONObject("delivery-note");
        
        final String deliveryNoteId = esbResponse.getString("id");
        connectorProperties.put("deliveryNoteId", deliveryNoteId);
        
        final String apiEndpoint =
                apiEndpointUrl + "/delivery-notes/" + connectorProperties.getProperty("deliveryNoteId") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONObject("delivery-note");
        
        Assert.assertEquals(connectorProperties.getProperty("clientId"), apiResponse.getString("client_id"));
        Assert.assertEquals(esbResponse.getString("created"), apiResponse.getString("created"));
        Assert.assertEquals(esbResponse.getString("status"), apiResponse.getString("status"));
        Assert.assertEquals(esbResponse.getString("delivery_note_number"), apiResponse.getString("delivery_note_number"));
        Assert.assertEquals(esbResponse.getString("date"), apiResponse.getString("date"));
    }
    
    /**
     * Positive test case for createDeliveryNote method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {createDeliveryNote} integration test with optional parameters.",
            dependsOnMethods = { "testCreateClientWithMandatoryParameters","testCreateContactWithMandatoryParameters" })
    public void testCreateDeliveryNoteWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createDeliveryNote");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDeliveryNote_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONObject("delivery-note");
        
        final String deliveryNoteId = esbResponse.getString("id");
        final String deliveryNoteNumber = esbResponse.getString("delivery_note_number");
        connectorProperties.put("deliveryNoteId", deliveryNoteId);
        connectorProperties.put("deliveryNoteFullNumber", deliveryNoteNumber);
        
        final String apiEndpoint =
                apiEndpointUrl + "/delivery-notes/" + connectorProperties.getProperty("deliveryNoteId") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONObject("delivery-note");
        
        Assert.assertEquals(connectorProperties.getProperty("contactId"), apiResponse.getString("contact_id"));
        Assert.assertEquals(connectorProperties.getProperty("deliveryNoteNumberPrefix"), apiResponse.getString("number_pre"));
        Assert.assertEquals(connectorProperties.getProperty("deliveryNoteNumber"), apiResponse.getString("number"));
        Assert.assertEquals(connectorProperties.getProperty("deliveryNoteTitle"), apiResponse.getString("title"));
        Assert.assertEquals(connectorProperties.getProperty("deliveryNoteDate"), apiResponse.getString("date"));
    }
    
    /**
     * Negative test case for createDeliveryNote method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {createDeliveryNote} integration test with negative case.",
            dependsOnMethods = { "testCreateClientWithMandatoryParameters" })
    public void testCreateDeliveryNoteWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createDeliveryNote");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDeliveryNote_negative.json");
        final String apiEndPoint = apiEndpointUrl + "/delivery-notes/" + authString;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createDeliveryNote_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").get("error"), apiRestResponse.getBody()
                .getJSONObject("errors").get("error"));
    }
    
    /**
     * Positive test case for getDeliveryNote method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {getDeliveryNote} integration test with mandatory parameters.",
            dependsOnMethods = { "testCreateDeliveryNoteWithMandatoryParameters" })
    public void testGetDeliveryNoteWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getDeliveryNote");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDeliveryNote_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONObject("delivery-note");

        final String apiEndpoint =
                apiEndpointUrl + "/delivery-notes/" + connectorProperties.getProperty("deliveryNoteId") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONObject("delivery-note");
        
        Assert.assertEquals(esbResponse.getString("client_id"), apiResponse.getString("client_id"));
        Assert.assertEquals(esbResponse.getString("created"), apiResponse.getString("created"));
        Assert.assertEquals(esbResponse.getString("status"), apiResponse.getString("status"));
        Assert.assertEquals(esbResponse.getString("delivery_note_number"), apiResponse.getString("delivery_note_number"));
        Assert.assertEquals(esbResponse.getString("date"), apiResponse.getString("date"));
    }
    
    /**
     * Method name: getDeliveryNote
     * Test scenario: Optional
     * Reason to skip: In getDeliveryNote method there is only one paramter which is a mandatory one.
     */
    
    /**
     * Negative case for getDeliveryNote method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {getDeliveryNote} integration test with negative case.")
    public void testGetDeliveryNoteWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getDeliveryNote");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDeliveryNote_negative.json");
        final String apiEndpoint = apiEndpointUrl + "/delivery-notes/invalid" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").get("error"), apiRestResponse.getBody()
                .getJSONObject("errors").get("error"));
    }
    
    /**
     * Positive test case for listDeliveryNotes method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {listDeliveryNotes} integration test with mandatory parameters.",
            dependsOnMethods = { "testCreateDeliveryNoteWithMandatoryParameters", "testCreateDeliveryNoteWithOptionalParameters"})
    public void testListDeliveryNotesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listDeliveryNotes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDeliveryNotes_mandatory.json");
        
        final JSONObject esbResponse =
                esbRestResponse.getBody().getJSONObject("delivery-notes").getJSONArray("delivery-note").getJSONObject(0);
        
        final String apiEndpoint = apiEndpointUrl + "/delivery-notes/" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse =
                apiRestResponse.getBody().getJSONObject("delivery-notes").getJSONArray("delivery-note").getJSONObject(0);
        
        Assert.assertEquals(esbResponse.getString("client_id"), apiResponse.getString("client_id"));
        Assert.assertEquals(esbResponse.getString("created"), apiResponse.getString("created"));
        Assert.assertEquals(esbResponse.getString("status"), apiResponse.getString("status"));
        Assert.assertEquals(esbResponse.getString("delivery_note_number"), apiResponse.getString("delivery_note_number"));
        Assert.assertEquals(esbResponse.getString("date"), apiResponse.getString("date"));
    }
    
    /**
     * Positive test case for listDeliveryNotes method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {listDeliveryNotes} integration test with mandatory parameters.",
            dependsOnMethods = { "testCreateDeliveryNoteWithMandatoryParameters", "testCreateDeliveryNoteWithOptionalParameters"})
    public void testListDeliveryNotesWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listDeliveryNotes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDeliveryNotes_optional.json");
        
        final JSONObject esbResponse =
                esbRestResponse.getBody().getJSONObject("delivery-notes").getJSONObject("delivery-note");
        
        final String apiEndpoint =
                apiEndpointUrl + "/delivery-notes/" + authString + "&client_id="
                        + connectorProperties.getProperty("clientId") + "&delivery_note_number="
                        + connectorProperties.getProperty("deliveryNoteFullNumber") + "&from="
                        + connectorProperties.getProperty("deliveryNoteDate") + "&contact_id="
                        + connectorProperties.getProperty("contactId") + "&status="
                        + connectorProperties.getProperty("deliveryNoteStatus");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONObject("delivery-notes").getJSONObject("delivery-note");
        
        Assert.assertEquals(connectorProperties.getProperty("contactId"), apiResponse.getString("contact_id"));
        Assert.assertEquals(connectorProperties.getProperty("clientId"), apiResponse.getString("client_id"));
        Assert.assertEquals(connectorProperties.getProperty("deliveryNoteStatus"), apiResponse.getString("status"));
        Assert.assertEquals(connectorProperties.getProperty("deliveryNoteFullNumber"), apiResponse.getString("delivery_note_number"));

    }
    
    /**
     * Negative test case for listDeliveryNotes method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "billomat {listDeliveryNotes} integration test with negative case.")
    public void testListDeliveryNotesWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listDeliveryNotes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDeliveryNotes_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/delivery-notes" + authString + "&status=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").get("error"), apiRestResponse.getBody()
                .getJSONObject("errors").get("error"));
    }
    
}
