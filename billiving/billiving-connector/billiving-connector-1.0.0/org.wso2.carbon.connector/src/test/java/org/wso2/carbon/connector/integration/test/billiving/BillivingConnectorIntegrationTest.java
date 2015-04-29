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
package org.wso2.carbon.connector.integration.test.billiving;

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

public class BillivingConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    final private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    final private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private String apiUrl;
    
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("billiving-connector-1.0.0");
        
        apiUrl = connectorProperties.getProperty("apiUrl");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        // Create base64-encoded auth string using login and apiKey
        final String authString = connectorProperties.getProperty("accessToken") + ":X";
        final String base64AuthString = Base64.encode(authString.getBytes());
        
        apiRequestHeadersMap.put("Authorization", "Basic " + base64AuthString);
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
    }
    
    /**
     * Positive test case for createClient method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "Billiving {createClient} integration test with mandatory parameters.")
    public void testCreateClientWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createClient");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createClient_mandatory.json");

        String clientId = esbRestResponse.getBody().getString("Id");
        connectorProperties.put("clientId", clientId);
        
        String apiEndPoint = apiUrl + "/api2/v1/clients/" + clientId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        //This is the only parameter returns 
        Assert.assertEquals(apiRestResponse.getBody().getString("Email"), connectorProperties.get("clientEmail"));
    }
    
    /**
     * Positive test case for createClient method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateClientWithMandatoryParameters" }, description = "Billiving {createClient} integration test with optional parameters.")
    public void testCreateClientWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createClient");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createClient_optional.json");
        
        String clientId2 = esbRestResponse.getBody().getString("Id");
        connectorProperties.put("clientId2", clientId2);
        
        String apiEndPoint = apiUrl + "/api2/v1/clients/" + clientId2;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(clientId2, apiRestResponse.getBody().getString("Id"));
        Assert.assertEquals(apiRestResponse.getBody().getString("Email"), connectorProperties.get("clientEmail"));
        Assert.assertEquals(apiRestResponse.getBody().getString("Telephone1"), connectorProperties.get("telephoneNumber"));
        Assert.assertEquals(apiRestResponse.getBody().getString("InternalNotes"), connectorProperties.getProperty("internalNotes"));
        Assert.assertEquals(apiRestResponse.getBody().getString("ContactName"), connectorProperties.getProperty("internalNotes"));
        Assert.assertEquals(apiRestResponse.getBody().getString("Address1"), connectorProperties.getProperty("internalNotes"));
    }
    
    /**
     * Negative test case for createClient method.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateClientWithOptionalParameters" }, description = "Billiving {createClient} integration test with negative case.")
    public void testCreateClientWithNegativeCase() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:createClient");
       
       RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createClient_negative.json");
       
       String apiEndPoint = apiUrl + "/api2/v1/clients";
       RestResponse<JSONObject> apiRestResponse =
             sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createClient_negative.json");
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
       
    }
    
    /**
     * Positive test case for getClient method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "Billiving {getClient} integration test with mandatory parameters.")
    public void testGetClientWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getClient");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getClient_mandatory.json");
        
        String apiEndPoint = apiUrl + "/api2/v1/clients/" + connectorProperties.getProperty("clientId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("ContactName"), apiRestResponse.getBody().getString("ContactName"));
        Assert.assertEquals(esbRestResponse.getBody().getString("OrganizationName"), apiRestResponse.getBody().getString("OrganizationName"));
        Assert.assertEquals(esbRestResponse.getBody().getString("Email"), apiRestResponse.getBody().getString("Email"));
    }
    
    /**
     * Positive test case for listClients method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetClientWithMandatoryParameters" }, description = "Billiving {listClients} integration test with mandatory parameters.")
    public void testListClientsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listClients");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listClients_mandatory.json");
        
        String esbResponseArrayString = esbRestResponse.getBody().getString("output");
        JSONArray esbResponseArray = new JSONArray(esbResponseArrayString);
        JSONObject esbObject = esbResponseArray.getJSONObject(0);
        
        String apiEndPoint = apiUrl + "/api2/v1/clients";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String apiResponseArrayString = apiRestResponse.getBody().getString("output");
        JSONArray apiResponseArray = new JSONArray(apiResponseArrayString);
        JSONObject apiObject = apiResponseArray.getJSONObject(0);
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbObject.getInt("Id"), apiObject.getInt("Id"));
        Assert.assertEquals(esbObject.getString("OrganizationName"), apiObject.getString("OrganizationName"));
        Assert.assertEquals(esbObject.getString("Email"), apiObject.getString("Email"));
    }
    
    /**
     * Positive test case for listClients method with optional parameters.
     */
    @Test(dependsOnMethods = { "testListClientsWithMandatoryParameters" }, description = "Billiving {listClients} integration test with optional parameters.")
    public void testListClientsWithOptionalParameters() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:listClients");
       
       RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listClients_optional.json");
       
       String esbResponseArrayString = esbRestResponse.getBody().getString("output");
       JSONArray esbResponseArray = new JSONArray(esbResponseArrayString);
       JSONObject esbObject = esbResponseArray.getJSONObject(0);
       
       String apiEndPoint = apiUrl + "/api2/v1/clients?Top=2";
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       String apiResponseArrayString = apiRestResponse.getBody().getString("output");
       JSONArray apiResponseArray = new JSONArray(apiResponseArrayString);
       JSONObject apiObject = apiResponseArray.getJSONObject(0);
       
       Assert.assertEquals(esbObject.getInt("Id"), apiObject.getInt("Id"));
       Assert.assertEquals(esbObject.getString("Email"), apiObject.getString("Email"));
    }
    
    /**
     * Negative test case for listClients method.
     */
    @Test(priority = 1, dependsOnMethods = { "testListClientsWithOptionalParameters" }, description = "Billiving {listClients} integration test with negative case.")
    public void testListClientsWithNegativeCase() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:listClients");
       
       RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listClients_negative.json");

       String esbResponseArrayString = esbRestResponse.getBody().getString("output");
       JSONArray esbResponseArray = new JSONArray(esbResponseArrayString);
       JSONObject esbObject = esbResponseArray.getJSONObject(0);
       
       String apiEndPoint = apiUrl + "/api2/v1/clients?StatusId=Invalid";
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       String apiResponseArrayString = apiRestResponse.getBody().getString("output");
       JSONArray apiResponseArray = new JSONArray(apiResponseArrayString);
       JSONObject apiObject = apiResponseArray.getJSONObject(0);
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
       Assert.assertEquals(esbObject.getString("Key"), apiObject.getString("Key"));
       Assert.assertEquals(esbObject.getString("Value"), apiObject.getString("Value"));
       
    }
    
    /**
     * Positive test case for createInvoice method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListClientsWithNegativeCase" }, description = "Billiving {createInvoice} integration test with mandatory parameters.")
    public void testCreateInvoiceWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_mandatory.json");
        String invoiceId = esbRestResponse.getBody().getString("Id");
        connectorProperties.put("invoiceId", invoiceId);
        
        String apiEndPoint = apiUrl + "/api2/v1/invoices/" + invoiceId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("Uri"), apiRestResponse.getBody().getString("Uri"));
        Assert.assertEquals(apiRestResponse.getBody().getString("ClientId"), connectorProperties.getProperty("clientId"));
    }
    
    /**
     * Positive test case for createInvoice method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateInvoiceWithMandatoryParameters" }, description = "Billiving {createInvoice} integration test with optional parameters.")
    public void testCreateInvoiceWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_optional.json");
        
        String invoiceId2 = esbRestResponse.getBody().getString("Id");
        connectorProperties.put("invoiceId2", invoiceId2);
        
        String apiEndPoint = apiUrl + "/api2/v1/invoices/" + invoiceId2;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        String apiDocItemsArrayString = apiRestResponse.getBody().getString("DocItems");
        JSONArray apiDocItemsArray = new JSONArray(apiDocItemsArrayString);
        JSONObject apiDocItemsObject = apiDocItemsArray.getJSONObject(0);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("ClientId"), connectorProperties.getProperty("clientId"));
        Assert.assertEquals(apiDocItemsObject.getString("ItemId"), connectorProperties.getProperty("ItemId"));
        Assert.assertEquals(apiDocItemsObject.getString("ItemDescription"), connectorProperties.getProperty("ItemDescription"));
        Assert.assertEquals(apiRestResponse.getBody().getString("Shipping"), connectorProperties.getProperty("shipping"));
        Assert.assertEquals(apiRestResponse.getBody().getString("InternalNotes"), connectorProperties.getProperty("internalNotes"));
    }
    
    /**
     * Negative test case for createInvoice method.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateInvoiceWithOptionalParameters" }, description = "Billiving {createInvoice} integration test with negative case.")
    public void testcreateInvoiceWithNegativeCase() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:createInvoice");
       
       RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_negative.json");
       
       String apiEndPoint = apiUrl + "/api2/v1/invoices";
       RestResponse<JSONObject> apiRestResponse =
             sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createInvoice_negative.json");
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
       
    }
    
    /**
     * Positive test case for getInvoice method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testcreateInvoiceWithNegativeCase" }, description = "Billiving {getInvoice} integration test with mandatory parameters.")
    public void testGetInvoiceWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getInvoice");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getInvoice_mandatory.json");
        
        String apiEndPoint = apiUrl + "/api2/v1/invoices/" + connectorProperties.getProperty("invoiceId2");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("IssueDate"), apiRestResponse.getBody().getString("IssueDate"));
        Assert.assertEquals(esbRestResponse.getBody().getString("CurrencyId"), apiRestResponse.getBody().getString("CurrencyId"));
        Assert.assertEquals(esbRestResponse.getBody().getString("ClientId"), apiRestResponse.getBody().getString("ClientId"));
    }
    
    /**
     * Positive test case for listInvoices method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetInvoiceWithMandatoryParameters" }, description = "Billiving {listInvoices} integration test with mandatory parameters.")
    public void testListInvoicesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listInvoices");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoices_mandatory.json");
        
        String esbResponseArrayString = esbRestResponse.getBody().getString("output");
        JSONArray esbResponseArray = new JSONArray(esbResponseArrayString);
        JSONObject esbObject = esbResponseArray.getJSONObject(0);
        
        String apiEndPoint = apiUrl + "/api2/v1/invoices";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String apiResponseArrayString = apiRestResponse.getBody().getString("output");
        JSONArray apiResponseArray = new JSONArray(apiResponseArrayString);
        JSONObject apiObject = apiResponseArray.getJSONObject(0);
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbObject.getInt("Id"), apiObject.getInt("Id"));
        Assert.assertEquals(esbObject.getString("IssueDate"), apiObject.getString("IssueDate"));
        Assert.assertEquals(esbObject.getString("OrganizationName"), apiObject.getString("OrganizationName"));
    }
    
    /**
     * Positive test case for listInvoices method with optional parameters.
     */
    @Test(dependsOnMethods = { "testListClientsWithMandatoryParameters" }, description = "Billiving {listInvoices} integration test with optional parameters.")
    public void testListInvoicesWithOptionalParameters() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:listInvoices");
       
       RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoices_optional.json");
       
       String esbResponseArrayString = esbRestResponse.getBody().getString("output");
       JSONArray esbResponseArray = new JSONArray(esbResponseArrayString);
       JSONObject esbObject = esbResponseArray.getJSONObject(0);
       
       String apiEndPoint = apiUrl + "/api2/v1/invoices?Top=2";
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       String apiResponseArrayString = apiRestResponse.getBody().getString("output");
       JSONArray apiResponseArray = new JSONArray(apiResponseArrayString);
       JSONObject apiObject = apiResponseArray.getJSONObject(0);
       
       Assert.assertEquals(esbObject.getInt("Id"), apiObject.getInt("Id"));
       Assert.assertEquals(esbObject.getString("IssueDate"), apiObject.getString("IssueDate"));
       Assert.assertEquals(connectorProperties.getProperty("status"), apiObject.getString("Status"));
       Assert.assertEquals(connectorProperties.getProperty("status"), apiObject.getString("PayStatus"));
    }
    
    /**
     * Negative test case for listInvoices method.
     */
    @Test(priority = 1, dependsOnMethods = { "testListInvoicesWithOptionalParameters" }, description = "Billiving {listInvoices} integration test with negative case.")
    public void testListInvoicesWithNegativeCase() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:listInvoices");
       
       RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoices_negative.json");

       String esbResponseArrayString = esbRestResponse.getBody().getString("output");
       JSONArray esbResponseArray = new JSONArray(esbResponseArrayString);
       JSONObject esbObject = esbResponseArray.getJSONObject(0);
       
       String apiEndPoint = apiUrl + "/api2/v1/invoices?Status=Invalid";
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       String apiResponseArrayString = apiRestResponse.getBody().getString("output");
       JSONArray apiResponseArray = new JSONArray(apiResponseArrayString);
       JSONObject apiObject = apiResponseArray.getJSONObject(0);
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
       Assert.assertEquals(esbObject.getString("Key"), apiObject.getString("Key"));
       Assert.assertEquals(esbObject.getString("Value"), apiObject.getString("Value"));
       
    }
    
  
}
