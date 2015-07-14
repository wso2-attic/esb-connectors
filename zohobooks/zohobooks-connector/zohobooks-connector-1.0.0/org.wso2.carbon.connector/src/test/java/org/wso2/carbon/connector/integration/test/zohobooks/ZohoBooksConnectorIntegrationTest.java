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

package org.wso2.carbon.connector.integration.test.zohobooks;

import java.io.IOException;
import java.net.URLEncoder;
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

public class ZohoBooksConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private final Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();    
    private final Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();    
    private String authString;    
    private String apiEndpointUrl;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("zohobooks-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Accept", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        authString =
                "?authtoken=" + connectorProperties.getProperty("authToken") + "&organization_id="
                        + connectorProperties.getProperty("organizationId");
        apiEndpointUrl = connectorProperties.getProperty("apiUrl") + "/api/v3";
        
        //set purchaseAccountId property to connector properties.
        setPurchaseAccountId();

        connectorProperties.setProperty("itemNameMandatory", System.currentTimeMillis() + connectorProperties
                .getProperty("itemNameMandatory"));
        connectorProperties.setProperty("itemNameOptional", System.currentTimeMillis() + connectorProperties
                .getProperty("itemNameOptional"));
        connectorProperties.setProperty("invoiceNumber", System.currentTimeMillis() + connectorProperties
                .getProperty("invoiceNumber"));
        connectorProperties.setProperty("contactPersonLastName", System.currentTimeMillis() + connectorProperties
                .getProperty("contactPersonLastName"));
        connectorProperties.setProperty("contactPersonFirstName", System.currentTimeMillis() + connectorProperties
                .getProperty("contactPersonFirstName"));
        connectorProperties.setProperty("contactNameOptional", System.currentTimeMillis() + connectorProperties
                .getProperty("contactNameOptional"));
        connectorProperties.setProperty("contactNameMandatory", System.currentTimeMillis() + connectorProperties
                .getProperty("contactNameMandatory"));
        connectorProperties.setProperty("projectName", System.currentTimeMillis() + connectorProperties
                .getProperty("projectName"));
        connectorProperties.setProperty("projectNameOptional", System.currentTimeMillis() + connectorProperties
                .getProperty("projectNameOptional"));
        connectorProperties.setProperty("userName", System.currentTimeMillis() + connectorProperties
                .getProperty("userName"));
        connectorProperties.setProperty("email", System.currentTimeMillis() + connectorProperties
                .getProperty("email"));
        connectorProperties.setProperty("taskName", System.currentTimeMillis() + connectorProperties
                .getProperty("taskName"));
        connectorProperties.setProperty("taskNameOpt", System.currentTimeMillis() + connectorProperties
                .getProperty("taskNameOpt"));
    }
    
    /**
     * Positive test case for createItem method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createItem} integration test with mandatory parameters.")
    public void testCreateItemWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createItem");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createItem_mandatory.json");
        final String itemIdMandatory = esbRestResponse.getBody().getJSONObject("item").getString("item_id");
        connectorProperties.put("itemIdMandatory", itemIdMandatory);
        
        final String apiEndpoint = apiEndpointUrl + "/items/" + itemIdMandatory + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(itemIdMandatory, apiRestResponse.getBody().getJSONObject("item").getString("item_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("item").getString("name"), apiRestResponse
                .getBody().getJSONObject("item").getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("item").getString("rate"), apiRestResponse
                .getBody().getJSONObject("item").getString("rate"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("item").getString("name"),
                connectorProperties.getProperty("itemNameMandatory"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("item").getString("rate"),
                connectorProperties.getProperty("rate"));
        
    }
    
    /**
     * Positive test case for createItem method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createItem} integration test with optional parameters.")
    public void testCreateItemWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createItem");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createItem_optional.json");
        
        final String itemIdOptional = esbRestResponse.getBody().getJSONObject("item").getString("item_id");
        connectorProperties.put("itemIdOptional", itemIdOptional);
        
        final String apiEndpoint = apiEndpointUrl + "/items/" + itemIdOptional + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("item").getString("purchase_account_id"), apiRestResponse
                .getBody().getJSONObject("item").getString("purchase_account_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("item").getString("unit"), apiRestResponse
                .getBody().getJSONObject("item").getString("unit"));
        
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("item").getString("purchase_account_id"),
                connectorProperties.getProperty("purchaseAccountId"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("item").getString("unit"),
                connectorProperties.getProperty("unit"));
        
    }
    
    /**
     * Negative test case for createItem method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createItem} integration test with negative case.")
    public void testCreateItemWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createItem");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createItem_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/items" + authString + "&JSONString="
                        + URLEncoder.encode("{\"rate\" : 25.0}", "UTF-8");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        
    }
    
    /**
     * Positive test case for listItems method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {listItems} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateItemWithMandatoryParameters", "testCreateItemWithOptionalParameters" })
    public void testListItemsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listItems");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listItems_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/items" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").length(), apiRestResponse.getBody()
                .getJSONArray("items").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).getString("item_id"),
                apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).getString("item_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).getString("item_name"),
                apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).getString("item_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).getString("item_type"),
                apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).getString("item_type"));
        
    }
    
    /**
     * Positive test case for listItems method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {listItems} integration test with optional parameters.", dependsOnMethods = {
            "testCreateItemWithMandatoryParameters", "testCreateItemWithOptionalParameters" })
    public void testListItemsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listItems");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listItems_optional.json");
        final String apiEndpoint =
                apiEndpointUrl + "/items" + authString + "&name="
                        + URLEncoder.encode(connectorProperties.getProperty("itemNameOptional"), "UTF-8");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").length(), 1);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("items").length(), 1);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).getString("status"),
                apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).getString("name"),
                apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).getString("name"));
        
    }
    
    /**
     * Negative test case for listItems method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {listItems} integration test with negative case.")
    public void testListItemsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listItems");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listItems_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/items" + authString + "&tax_id=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        
    }
    
    /**
     * Positive test case for getItem method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {getItem} integration test with mandatory parameters.", dependsOnMethods = { "testCreateItemWithMandatoryParameters" })
    public void testGetItemWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getItem");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getItem_mandatory.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/items/" + connectorProperties.getProperty("itemIdMandatory") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("item").getString("name"), apiRestResponse
                .getBody().getJSONObject("item").getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("item").getString("status"), apiRestResponse
                .getBody().getJSONObject("item").getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("item").getString("rate"), apiRestResponse
                .getBody().getJSONObject("item").getString("rate"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("item").getString("account_id"), apiRestResponse
                .getBody().getJSONObject("item").getString("account_id"));
        
    }
    
    /**
     * Positive test case for createContact method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createContact} integration test with mandatory parameters.")
    public void testCreateContactWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_mandatory.json");
        
        final String contactIdMandatory = esbRestResponse.getBody().getJSONObject("contact").getString("contact_id");
        connectorProperties.put("contactIdMandatory", contactIdMandatory);
        
        final String apiEndpoint = apiEndpointUrl + "/contacts/" + contactIdMandatory + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(contactIdMandatory,
                apiRestResponse.getBody().getJSONObject("contact").getString("contact_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("contact_name"),
                apiRestResponse.getBody().getJSONObject("contact").getString("contact_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("created_time"),
                apiRestResponse.getBody().getJSONObject("contact").getString("created_time"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("last_modified_time"),
                apiRestResponse.getBody().getJSONObject("contact").getString("last_modified_time"));
        
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("contact").getString("contact_name"),
                connectorProperties.getProperty("contactNameMandatory"));
        
    }
    
    /**
     * Positive test case for createContact method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createContact} integration test with optional parameters.")
    public void testCreateContactWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_optional.json");
      
        final String contactIdOptional = esbRestResponse.getBody().getJSONObject("contact").getString("contact_id");
        connectorProperties.put("contactIdOptional", contactIdOptional);
        
        final String contactPersonId =
                esbRestResponse.getBody().getJSONObject("contact").getJSONArray("contact_persons").getJSONObject(0)
                        .getString("contact_person_id");
        connectorProperties.put("contactPersonId", contactPersonId);
        
        final String apiEndpoint = apiEndpointUrl + "/contacts/" + contactIdOptional + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("notes"), apiRestResponse
                .getBody().getJSONObject("contact").getString("notes"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("website"), apiRestResponse
                .getBody().getJSONObject("contact").getString("website"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("company_name"),
                apiRestResponse.getBody().getJSONObject("contact").getString("company_name"));
        
        Assert.assertEquals(connectorProperties.getProperty("website"),
                apiRestResponse.getBody().getJSONObject("contact").getString("website"));
        Assert.assertEquals(connectorProperties.getProperty("companyName"),
                apiRestResponse.getBody().getJSONObject("contact").getString("company_name"));
        Assert.assertEquals(connectorProperties.getProperty("notes"), apiRestResponse.getBody()
                .getJSONObject("contact").getString("notes"));
        
    }
    
    /**
     * Negative test case for createContact method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createContact} integration test with negative case.")
    public void testCreateContactWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/contacts" + authString + "&JSONString="
                        + URLEncoder.encode("{\"contact_name\":\"\"}", "UTF-8");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        
    }
    
    /**
     * Positive test case for getContact method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {getContact} integration test with mandatory parameters.", dependsOnMethods = { "testCreateContactWithMandatoryParameters" })
    public void testGetContactWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContact_mandatory.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/contacts/" + connectorProperties.getProperty("contactIdMandatory") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("contact_name"),
                apiRestResponse.getBody().getJSONObject("contact").getString("contact_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("created_time"),
                apiRestResponse.getBody().getJSONObject("contact").getString("created_time"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("last_modified_time"),
                apiRestResponse.getBody().getJSONObject("contact").getString("last_modified_time"));
        
    }
    
    /**
     * Positive test case for listContacts method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {listContacts} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateContactWithMandatoryParameters", "testCreateContactWithOptionalParameters" })
    public void testListContactsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/contacts" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("contacts").length(), apiRestResponse.getBody()
                .getJSONArray("contacts").length());
        
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("contact_id"),
                apiRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("contact_id"));
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("contact_name"),
                apiRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("contact_name"));
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("created_time"),
                apiRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("created_time"));
        
    }
    
    /**
     * Positive test case for listContacts method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {listContacts} integration test with optional parameters.", dependsOnMethods = {
            "testCreateContactWithMandatoryParameters", "testCreateContactWithOptionalParameters" })
    public void testListContactsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_optional.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/contacts" + authString + "&contact_name_startswith="
                        + URLEncoder.encode(connectorProperties.getProperty("contactNameMandatory"), "UTF-8");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("contacts").length(), 1);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("contacts").length(), 1);
        
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("first_name"),
                apiRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("first_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("status"),
                apiRestResponse.getBody().getJSONArray("contacts").getJSONObject(0).getString("status"));
        
    }
    
    /**
     * Negative test case for listContacts method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {listContacts} integration test with negative case.")
    public void testListContactsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/contacts" + authString + "&sort_column=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        
    }
    
    /**
     * Positive test case for createInvoice method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createInvoice} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateItemWithMandatoryParameters", "testCreateContactWithMandatoryParameters" })
    public void testCreateInvoiceWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_mandatory.json");

        final String invoiceId = esbRestResponse.getBody().getJSONObject("invoice").getString("invoice_id");
        connectorProperties.put("invoiceId", invoiceId);
        
        final String apiEndpoint = apiEndpointUrl + "/invoices/" + invoiceId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("invoice").getString("customer_id"),
                apiRestResponse.getBody().getJSONObject("invoice").getString("customer_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("invoice").getJSONArray("line_items")
                .getJSONObject(0).getString("item_id"), apiRestResponse.getBody().getJSONObject("invoice")
                .getJSONArray("line_items").getJSONObject(0).getString("item_id"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("invoice").getString("customer_id"),
                connectorProperties.getProperty("contactIdMandatory"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("invoice").getJSONArray("line_items")
                .getJSONObject(0).getString("item_id"), connectorProperties.getProperty("itemIdMandatory"));
        
    }
    
    /**
     * Positive test case for createInvoice method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createInvoice} integration test with optional parameters.", dependsOnMethods = {
            "testCreateItemWithMandatoryParameters", "testCreateContactWithOptionalParameters" })
    public void testCreateInvoiceWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_optional.json");
     
        final String invoiceIdOptional = esbRestResponse.getBody().getJSONObject("invoice").getString("invoice_id");
        connectorProperties.put("invoiceIdOptional", invoiceIdOptional);
        
        final String apiEndpoint = apiEndpointUrl + "/invoices/" + invoiceIdOptional + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("invoice").getString("invoice_number"),
                apiRestResponse.getBody().getJSONObject("invoice").getString("invoice_number"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("invoice").getString("due_date"), apiRestResponse
                .getBody().getJSONObject("invoice").getString("due_date"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("invoice").getString("notes"), apiRestResponse
                .getBody().getJSONObject("invoice").getString("notes"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("invoice").getString("invoice_number"),
                connectorProperties.getProperty("invoiceNumber"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("invoice").getString("due_date"),
                connectorProperties.getProperty("invoiceDueDate"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("invoice").getString("notes"),
                connectorProperties.getProperty("notes"));
        
    }
    
    /**
     * Negative test case for createInvoice method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createInvoice} integration test with negative case.")
    public void testCreateInvoiceWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/invoices" + authString + "&JSONString="
                        + URLEncoder.encode("{\"customer_id\":\"INVALID\"}", "UTF-8");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                esbRestResponse.getBody().getString("message"));
        
    }
    
    /**
     * Positive test case for getInvoice method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {getInvoice} integration test with mandatory parameters.", dependsOnMethods = { "testCreateInvoiceWithMandatoryParameters" })
    public void testGetInvoiceWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getInvoice");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getInvoice_mandatory.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/invoices/" + connectorProperties.getProperty("invoiceId") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("invoice").getString("invoice_number"),
                apiRestResponse.getBody().getJSONObject("invoice").getString("invoice_number"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("invoice").getString("customer_id"),
                apiRestResponse.getBody().getJSONObject("invoice").getString("customer_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("invoice").getJSONArray("line_items")
                .getJSONObject(0).getString("item_id"), apiRestResponse.getBody().getJSONObject("invoice")
                .getJSONArray("line_items").getJSONObject(0).getString("item_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("invoice").getString("currency_id"),
                apiRestResponse.getBody().getJSONObject("invoice").getString("currency_id"));
        
    }
    
    /**
     * Positive test case for listInvoices method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {listInvoices} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateInvoiceWithMandatoryParameters", "testCreateInvoiceWithOptionalParameters" })
    public void testListInvoicesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listInvoices");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoices_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/invoices" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getString("invoice_id"),
                apiRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getString("invoice_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("invoices").getJSONObject(0)
                .getString("customer_id"), apiRestResponse.getBody().getJSONArray("invoices").getJSONObject(0)
                .getString("customer_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("invoices").getJSONObject(0)
                .getString("currency_id"), apiRestResponse.getBody().getJSONArray("invoices").getJSONObject(0)
                .getString("currency_id"));
        
    }
    
    /**
     * Positive test case for listInvoices method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {listInvoices} integration test with optional parameters.", dependsOnMethods = {
            "testCreateInvoiceWithMandatoryParameters", "testCreateInvoiceWithOptionalParameters" })
    public void testListInvoicesWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listInvoices");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoices_optional.json");
        
        final String apiEndpoint = apiEndpointUrl + "/invoices" + authString + "&per_page=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("invoices").length(), 1);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("invoices").length(), 1);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("invoices").getJSONObject(0)
                .getString("customer_id"), esbRestResponse.getBody().getJSONArray("invoices").getJSONObject(0)
                .getString("customer_id"));
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getString("invoice_number"),
                esbRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getString("invoice_number"));
        
    }
    
    /**
     * Negative test case for listInvoices method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {listInvoices} integration test with negative case.")
    public void testListInvoicesWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listInvoices");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoices_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/invoices" + authString + "&item_id=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        
    }
    
    /**
     * Positive test case for emailInvoices method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {emailInvoices} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateInvoiceWithOptionalParameters", "testCreateContactWithOptionalParameters" })
    public void testEmailInvoicesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:emailInvoices");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_emailInvoices_mandatory.json");
        
        final String apiEndpoint =
                apiEndpointUrl
                        + "/invoices/email"
                        + authString
                        + "&invoice_ids="
                        + connectorProperties.getProperty("invoiceIdOptional")
                        + "&JSONString="
                        + URLEncoder.encode(
                                "{\"contacts\":[{\"contact_id\": \""
                                        + connectorProperties.getProperty("contactIdOptional")
                                        + "\",\"email\": true,\"snail_mail\": false}]}", "UTF-8");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        
    }
    
    /**
     * Negative test case for emailInvoices method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {emailInvoices} integration test with negative case.")
    public void testEmailInvoicesWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:emailInvoices");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_emailInvoices_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/invoices/email" + authString + "&invoice_ids=INVALID&contacts="
                        + URLEncoder.encode("[]", "UTF-8");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        
    }
    
    /**
     * Positive test case for createPurchaseOrder method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createPurchaseOrder} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateItemWithOptionalParameters", "testCreateContactWithMandatoryParameters" })
    public void testCreatePurchaseOrderWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPurchaseOrder");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPurchaseOrder_mandatory.json");
        
        final String purchaseOrderId = esbRestResponse.getBody().getJSONObject("purchaseorder").getString("purchaseorder_id");
        
        connectorProperties.put("purchaseOrderId", purchaseOrderId);
        
        final String apiEndpoint = apiEndpointUrl + "/purchaseorders/" + purchaseOrderId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
 
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("purchaseorder").getJSONArray("line_items")
                .getJSONObject(0).getString("item_id"), apiRestResponse.getBody().getJSONObject("purchaseorder")
                .getJSONArray("line_items").getJSONObject(0).getString("item_id"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("purchaseorder").getString("vendor_id"),
                connectorProperties.getProperty("contactIdMandatory"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("purchaseorder").getJSONArray("line_items")
                .getJSONObject(0).getString("item_id"), connectorProperties.getProperty("itemIdOptional"));
        
    }
    
    /**
     * Positive test case for createPurchaseOrder method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createPurchaseOrder} integration test with optional parameters.", dependsOnMethods = {
            "testCreateItemWithOptionalParameters", "testCreateContactWithOptionalParameters" })
    public void testCreatePurchaseOrderWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPurchaseOrder");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPurchaseOrder_optional.json");
        
        final String purchaseOrderId = esbRestResponse.getBody().getJSONObject("purchaseorder").getString("purchaseorder_id");
     
        final String apiEndpoint = apiEndpointUrl + "/purchaseorders/" + purchaseOrderId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
 
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("purchaseorder").getJSONArray("line_items")
                .getJSONObject(0).getString("item_id"), apiRestResponse.getBody().getJSONObject("purchaseorder")
                .getJSONArray("line_items").getJSONObject(0).getString("item_id"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("purchaseorder").getString("vendor_id"),
                connectorProperties.getProperty("contactIdOptional"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("purchaseorder").getJSONArray("line_items")
                .getJSONObject(0).getString("item_id"), connectorProperties.getProperty("itemIdOptional"));
        
    }
    
    /**
     * Negative test case for createPurchaseOrder method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createPurchaseOrder} integration test with negative case.")
    public void testCreatePurchaseOrderWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPurchaseOrder");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPurchaseOrder_negative.json");
        final String apiEndpoint =
                apiEndpointUrl + "/purchaseorders" + authString + "&JSONString="
                        + URLEncoder.encode("{\"vendor_id\":\"INVALID\"}", "UTF-8");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                esbRestResponse.getBody().getString("message"));
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        
    }
    
    /**
     * Positive test case for createEstimate method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createEstimate} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateItemWithMandatoryParameters", "testCreateContactWithMandatoryParameters" })
    public void testCreateEstimateWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createEstimate");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEstimate_mandatory.json");
        
        final String estimateId = esbRestResponse.getBody().getJSONObject("estimate").getString("estimate_id");
        
        connectorProperties.put("estimateId", estimateId);
        
        final String apiEndpoint = apiEndpointUrl + "/estimates/" + estimateId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
 
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("estimate").getJSONArray("line_items")
                .getJSONObject(0).getString("item_id"), apiRestResponse.getBody().getJSONObject("estimate")
                .getJSONArray("line_items").getJSONObject(0).getString("item_id"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("estimate").getString("customer_id"),
                connectorProperties.getProperty("contactIdMandatory"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("estimate").getJSONArray("line_items")
                .getJSONObject(0).getString("item_id"), connectorProperties.getProperty("itemIdMandatory"));
        
    }
    
    /**
     * Positive test case for createEstimate method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createEstimate} integration test with optional parameters.", dependsOnMethods = {
            "testCreateItemWithMandatoryParameters", "testCreateContactWithOptionalParameters" })
    public void testCreateEstimateWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createEstimate");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEstimate_optional.json");
        
        final String estimateId = esbRestResponse.getBody().getJSONObject("estimate").getString("estimate_id");
        
        connectorProperties.put("estimateId", estimateId);
        
        final String apiEndpoint = apiEndpointUrl + "/estimates/" + estimateId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
 
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("estimate").getJSONArray("line_items")
                .getJSONObject(0).getString("item_id"), apiRestResponse.getBody().getJSONObject("estimate")
                .getJSONArray("line_items").getJSONObject(0).getString("item_id"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("estimate").getString("customer_id"),
                connectorProperties.getProperty("contactIdOptional"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("estimate").getJSONArray("line_items")
                .getJSONObject(0).getString("item_id"), connectorProperties.getProperty("itemIdMandatory"));
        
    }
    
    /**
     * Negative test case for createEstimate method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createEstimate} integration test with negative case.")
    public void testCreateEstimateWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createEstimate");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEstimate_negative.json");
        final String apiEndpoint =
                apiEndpointUrl + "/purchaseorders" + authString + "&JSONString="
                        + URLEncoder.encode("{\"customer_id\":\"INVALID\"}", "UTF-8");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                esbRestResponse.getBody().getString("message"));
        
    }
    
    /**
     * Positive test case for createCustomerPayment method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createCustomerPayment} integration test with mandatory parameters.", dependsOnMethods = {"testCreateContactWithMandatoryParameters" })
    public void testCreateCustomerPaymentWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCustomerPayment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomerPayment_mandatory.json");
        
        final String estimateId = esbRestResponse.getBody().getJSONObject("payment").getString("payment_id");
        
        
        final String apiEndpoint = apiEndpointUrl + "/customerpayments/" + estimateId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        final String payementMode = apiRestResponse.getBody().getJSONObject("payment").getString("payment_mode");
        connectorProperties.put("paymentMode", payementMode);
 
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("payment").getString("customer_id"), connectorProperties.getProperty("contactIdMandatory"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("payment").getString("date"), connectorProperties.getProperty("paymentDate"));
        Assert.assertEquals(Double.parseDouble(apiRestResponse.getBody().getJSONObject("payment").getString("amount")), Double.parseDouble(connectorProperties.getProperty("paymentAmount")));
    }
    
    /**
     * Positive test case for createCustomerPayment method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createCustomerPayment} integration test with optional parameters.", dependsOnMethods = {"testCreateContactWithMandatoryParameters" })
    public void testCreateCustomerPaymentWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCustomerPayment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomerPayment_optional.json");
        
        final String estimateId = esbRestResponse.getBody().getJSONObject("payment").getString("payment_id");
        
        
        final String apiEndpoint = apiEndpointUrl + "/customerpayments/" + estimateId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
 
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("payment").getString("description"), connectorProperties.getProperty("paymentDescription"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("payment").getString("reference_number"), connectorProperties.getProperty("paymentReferenceNumber"));
    }
    
    /**
     * Negative test case for createCustomerPayment method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createCustomerpayment} integration test with negative case.")
    public void testCreateCustomerPaymentsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCustomerPayment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomerPayement_negative.json");
        final String apiEndpoint =
                apiEndpointUrl + "/customerpayments" + authString + "&JSONString="
                        + URLEncoder.encode("{\"customer_id\":\"INVALID\"}", "UTF-8");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                esbRestResponse.getBody().getString("message"));
        
    }

    /**
     * Positive test case for listCustomerPayments method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {listCustomerPayments} integration test with mandatory parameters.", dependsOnMethods = { "testCreateCustomerPaymentWithMandatoryParameters" })
    public void testListCustomerPaymentsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCustomerPayments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomerPayments_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/customerpayments" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("customerpayments").length(), apiRestResponse
                .getBody().getJSONArray("customerpayments").length());
        
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONArray("customerpayments").getJSONObject(0).getString("payment_id"),
                apiRestResponse.getBody().getJSONArray("customerpayments").getJSONObject(0).getString("payment_id"));
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONArray("customerpayments").getJSONObject(0).getString("payment_number"),
                apiRestResponse.getBody().getJSONArray("customerpayments").getJSONObject(0).getString("payment_number"));
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONArray("customerpayments").getJSONObject(0).getString("customer_name"),
                apiRestResponse.getBody().getJSONArray("customerpayments").getJSONObject(0).getString("customer_name"));
    }
    
    /**
     * Positive test case for listCustomerPayments method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {listCustomerPayments} integration test with optional parameters.", dependsOnMethods = { "testCreateCustomerPaymentWithMandatoryParameters" })
    public void testListCustomerPaymentsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCustomerPayments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomerPayments_optional.json");

        final String apiEndpoint =
                apiEndpointUrl + "/customerpayments" + authString + "&customer_name="
                        + URLEncoder.encode(connectorProperties.getProperty("contactNameMandatory"), "UTF-8")
                        + "&payment_mode=" + URLEncoder.encode(connectorProperties.getProperty("paymentMode"), "UTF-8")
                        + "&reference_number=" + connectorProperties.getProperty("paymentReferenceNumber");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("customerpayments").length(), apiRestResponse
                .getBody().getJSONArray("customerpayments").length());
        
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONArray("customerpayments").getJSONObject(0).getString("payment_id"),
                apiRestResponse.getBody().getJSONArray("customerpayments").getJSONObject(0).getString("payment_id"));
        Assert.assertEquals(
                apiRestResponse.getBody().getJSONArray("customerpayments").getJSONObject(0).getString("customer_name"),
                connectorProperties.getProperty("contactNameMandatory"));
        Assert.assertEquals(
                apiRestResponse.getBody().getJSONArray("customerpayments").getJSONObject(0).getString("payment_mode"),
                connectorProperties.getProperty("paymentMode"));
        Assert.assertEquals(
                apiRestResponse.getBody().getJSONArray("customerpayments").getJSONObject(0)
                        .getString("reference_number"), connectorProperties.getProperty("paymentReferenceNumber"));
    }
    
    /**
     * Negative test case for listCustomerPayments method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {listCustomerPayments} integration test with negative case.")
    public void testListCustomerPaymentsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCustomerPayments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomerPayments_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/customerpayments" + authString + "&customer_id=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        
    }
    
    
    /**
     * Positive test case for createUser method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
     @Test(groups = { "wso2.esb" }, description =
    "zohobooks {createUser} integration test with mandatory parameters.")
    public void testCreateUserWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createUser");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createUser_mandatory.json");
        
        final String userId = esbRestResponse.getBody().getJSONObject("user").getString("user_id");
        connectorProperties.put("userId", userId);
        final String apiEndpoint = apiEndpointUrl + "/users/" + userId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("user").getString("name"),
                connectorProperties.getProperty("userName"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("user").getString("user_role"),
                connectorProperties.getProperty("userRole"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("user").getJSONArray("email_ids").getJSONObject(0)
                .getString("email"), connectorProperties.getProperty("email"));
        
    }
    
    /**
     * Negative test case for createUser method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createUser} integration test with negative case.")
    public void testCreateUserWithNegative() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createUser");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createUser_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/users/" + authString + "&JSONString="
                        + URLEncoder.encode("{\"name\":\"\"}", "UTF-8");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        
    }
    
    /**
     * Positive test case for getUser method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {getUser} integration test with mandatory parameters.",dependsOnMethods = {"testCreateUserWithMandatoryParameters" })
    public void testGetUserWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getUser");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUser_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/users/" + connectorProperties.getProperty("userId") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("user").getString("name"), esbRestResponse
                .getBody().getJSONObject("user").getString("name"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("user").getString("user_role"), esbRestResponse
                .getBody().getJSONObject("user").getString("user_role"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("user").getString("role_id"), esbRestResponse
                .getBody().getJSONObject("user").getString("role_id"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("user").getJSONArray("email_ids").getJSONObject(0)
                .getString("email"), esbRestResponse.getBody().getJSONObject("user").getJSONArray("email_ids")
                .getJSONObject(0).getString("email"));
        
    }

    
    /**
     * Positive test case for createProject method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createProject} integration test with mandatory parameters.", dependsOnMethods = {"testCreateContactWithMandatoryParameters" })
    public void testCreateProjectWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createProject");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_mandatory.json");
        
        final String projectId = esbRestResponse.getBody().getJSONObject("project").getString("project_id");
        connectorProperties.put("projectId", projectId);
        
        final String apiEndpoint = apiEndpointUrl + "/projects/" + projectId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
 
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("project").getString("project_name"), connectorProperties.getProperty("projectName"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("project").getString("customer_id"), connectorProperties.getProperty("contactIdMandatory"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("project").getString("created_time"), esbRestResponse.getBody().getJSONObject("project").getString("created_time"));  
     
   }
    
    /**
     * Positive test case for createProject method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createProject} integration test with optional parameters.", dependsOnMethods = {"testCreateContactWithMandatoryParameters" })
    public void testCreateProjectWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createProject");
        
        connectorProperties.put("budgetType", "hours_per_staff");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_optional.json");
        
        final String projectIdOpt = esbRestResponse.getBody().getJSONObject("project").getString("project_id");
        connectorProperties.put("projectIdOpt", projectIdOpt);

        final String apiEndpoint = apiEndpointUrl + "/projects/" + projectIdOpt + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
 
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("project").getString("project_name"), connectorProperties.getProperty("projectNameOptional"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("project").getString("description"), connectorProperties.getProperty("projectDescription"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("project").getString("budget_type"), connectorProperties.getProperty("budgetType"));  
        
   }
    
    /**
     * Negative test case for createProject method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createProject} integration test with negative case.")
    public void testCreateProjectWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createProject");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/projects" + authString + "&JSONString="
                        + URLEncoder.encode("{\"project_name\":\"INVALID\"}", "UTF-8");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap);
 
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                esbRestResponse.getBody().getString("message"));
        
   }
    
    /**
     * Positive test case for getProject method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {getProject} integration test with mandatory parameters.", dependsOnMethods = { "testCreateProjectWithMandatoryParameters" })
    public void testGetProjectWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getProject");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProject_mandatory.json");

        final String apiEndpoint =
                apiEndpointUrl + "/projects/" + connectorProperties.getProperty("projectId") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("project").getString("project_name"), connectorProperties.getProperty("projectName"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("project").getString("customer_id"), connectorProperties.getProperty("contactIdMandatory"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("project").getString("created_time"), esbRestResponse.getBody().getJSONObject("project").getString("created_time"));  
    }
    
    /**
     * Positive test case for listProjects method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {listProjects} integration test with mandatory parameters.", dependsOnMethods = {"testCreateProjectWithMandatoryParameters","testCreateProjectWithOptionalParameters"})
    public void testListProjectsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listProjects");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProject_mandatory.json");
        
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("projects");
        
        final String apiEndpoint = apiEndpointUrl + "/projects/"+ authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("projects");
        
        Assert.assertEquals(apiResponseArray.length(),esbResponseArray.length());
        Assert.assertEquals(apiResponseArray.getJSONObject(0) .getString("project_name"), esbResponseArray.getJSONObject(0).getString("project_name"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0) .getString("customer_id"), esbResponseArray.getJSONObject(0).getString("customer_id"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0) .getString("status"), esbResponseArray.getJSONObject(0).getString("status"));
   }
    
    /**
     * Positive test case for listProjects method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {listProjects} integration test with optional parameters.", dependsOnMethods = {"testCreateProjectWithMandatoryParameters","testCreateProjectWithOptionalParameters"})
    public void testListProjectsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listProjects");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProject_optional.json");
        
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("projects");
        
        final String apiEndpoint = apiEndpointUrl + "/projects/"+ authString+"&customer_id="+connectorProperties.getProperty("contactIdMandatory")+"&filter_by=Status.Active";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("projects");
        
        Assert.assertEquals(apiResponseArray.length(),esbResponseArray.length());
        Assert.assertEquals(apiResponseArray.getJSONObject(0) .getString("project_name"), esbResponseArray.getJSONObject(0).getString("project_name"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0) .getString("customer_id"), esbResponseArray.getJSONObject(0).getString("customer_id"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0) .getString("status"), esbResponseArray.getJSONObject(0).getString("status"));
   }
    
    /**
     * Negative test case for listProjects method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {listProjects} integration test with negative case.")
    public void testListProjectsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listProjects");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProject_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/projects/"+ authString+"&customer_id=invalid&filter_by=Status.Active";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
 
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                esbRestResponse.getBody().getString("message"));
      
   }
    
    /**
     * Positive test case for assignUsersToProject method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {assignUsersToProject} integration test with mandatory parameters.", dependsOnMethods = {"testCreateProjectWithMandatoryParameters" })
    public void testAssignUserToProjectWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:assignUsersToProject");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_assignUsersToProject_mandatory.json");

        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("users");
        
        boolean isExist= false;
        for(int i=0;i<esbResponseArray.length();i++) {
        	if(connectorProperties.getProperty("taskUserId").equals(esbResponseArray.getJSONObject(i).getString("user_id"))) {
        		isExist=true;
        		Assert.assertTrue(true, "User has been assigned to the project");
        		break;
        	}
        }
        
        if(isExist==false) {
        	Assert.fail("User is not assigned for the project");
        }
   
   }
   
    /**
     * Negative test case for assignUsersToProject method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {assignUsersToProject} integration test with negative case.", dependsOnMethods = {"testCreateProjectWithMandatoryParameters","testCreateUserWithMandatoryParameters" })
    public void testAssignUserToProjectWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:assignUsersToProject");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_assignUsersToProject_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/projects/"+ connectorProperties.getProperty("projectId") + authString+ URLEncoder.encode("{\"users\": [\"user_id\":\"invalid\"]}", "UTF-8");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
 
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                esbRestResponse.getBody().getString("message"));
      
   }
    
    /**
     * Positive test case for createTask method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createTask} integration test with mandatory parameters.", dependsOnMethods = {"testCreateProjectWithMandatoryParameters" })
    public void testCreateTaskWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_mandatory.json");
        
        final String taskId = esbRestResponse.getBody().getJSONObject("task").getString("task_id");
        connectorProperties.put("taskId", taskId);
        
        final String apiEndpoint = apiEndpointUrl + "/projects/" + connectorProperties.getProperty("projectId")+"/tasks/"+taskId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
 
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("task").getString("project_id"), connectorProperties.getProperty("projectId"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("task").getString("task_name"), connectorProperties.getProperty("taskName"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("task").getString("project_name"), connectorProperties.getProperty("projectName"));  
     
   }
    
    /**
     * Positive test case for createTask method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createTask} integration test with optional parameters.", dependsOnMethods = {"testCreateProjectWithMandatoryParameters" })
    public void testCreateTaskWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_optional.json");
        
        final String taskId1 = esbRestResponse.getBody().getJSONObject("task").getString("task_id");
        connectorProperties.put("taskIdOpt", taskId1);
        
        final String apiEndpoint = apiEndpointUrl + "/projects/" + connectorProperties.getProperty("projectId")+"/tasks/"+taskId1 + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
 
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("task").getString("project_id"), connectorProperties.getProperty("projectId"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("task").getString("task_name"), connectorProperties.getProperty("taskNameOpt"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("task").getString("description"), connectorProperties.getProperty("taskDescription"));  
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("task").getString("rate"), connectorProperties.getProperty("taskRate")); 
   }
    
    /**
     * Negative test case for createTask method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createTask} integration test with negative case.", dependsOnMethods = {"testCreateProjectWithMandatoryParameters" })
    public void testCreateTaskWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_negative.json");
 
        final String apiEndpoint = apiEndpointUrl + "/projects/" + connectorProperties.getProperty("projectId")+"/tasks"+ authString+ "&JSONString="
                + URLEncoder.encode("{\"task_name\":\"negative task\",\"rate\":\"abc\"}", "UTF-8");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap);
   
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                esbRestResponse.getBody().getString("message")); 
   }
    
    /**
     * Positive test case for getTask method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {getTask} integration test with mandatory parameters.", dependsOnMethods = { "testCreateTaskWithMandatoryParameters" })
    public void testGetTaskWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTask_mandatory.json");
        
        final JSONObject esbResponseTaskObject=esbRestResponse.getBody().getJSONObject("task");
       
        final String apiEndpoint =
                apiEndpointUrl + "/projects/" + connectorProperties.getProperty("projectId")+"/tasks/" +connectorProperties.getProperty("taskId")+ authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponseTaskObject=apiRestResponse.getBody().getJSONObject("task");
        
        Assert.assertEquals(apiResponseTaskObject.getString("project_name"), esbResponseTaskObject.getString("project_name"));
        Assert.assertEquals(apiResponseTaskObject.getString("task_name"), esbResponseTaskObject.getString("task_name"));
        Assert.assertEquals(apiResponseTaskObject.getString("project_id"), esbResponseTaskObject.getString("project_id"));
    }
    
    /**
     * Positive test case for listTasks method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {listTasks} integration test with mandatory parameters.", dependsOnMethods = {"testCreateProjectWithMandatoryParameters","testCreateTaskWithMandatoryParameters"})
    public void testListTasksWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTasks");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_mandatory.json");
        
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("task");
        
        final String apiEndpoint = apiEndpointUrl + "/projects/"+connectorProperties.getProperty("projectId")+"/tasks"+ authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("task");
        
        Assert.assertEquals(apiResponseArray.length(),esbResponseArray.length());
        Assert.assertEquals(apiResponseArray.getJSONObject(0) .getString("project_name"), esbResponseArray.getJSONObject(0).getString("project_name"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0) .getString("task_name"), esbResponseArray.getJSONObject(0).getString("task_name"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0) .getString("customer_name"), esbResponseArray.getJSONObject(0).getString("customer_name"));
   }
    
    /**
     * Positive test case for listTasks method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {listTasks} integration test with optional parameters.", dependsOnMethods = {"testCreateProjectWithMandatoryParameters","testCreateTaskWithMandatoryParameters"})
    public void testListTasksWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTasks");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_optional.json");
        
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("task");
        JSONObject esbPageContext = esbRestResponse.getBody().getJSONObject("page_context");
        
        final String apiEndpoint = apiEndpointUrl + "/projects/"+connectorProperties.getProperty("projectId")+"/tasks"+ authString+"&sort_column=task_name&per_page=2";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("task");
        JSONObject apiPageContext = apiRestResponse.getBody().getJSONObject("page_context");
        
        Assert.assertEquals(apiResponseArray.length(),esbResponseArray.length());
        Assert.assertEquals(apiResponseArray.getJSONObject(0) .getString("project_name"), esbResponseArray.getJSONObject(0).getString("project_name"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0) .getString("task_name"), esbResponseArray.getJSONObject(0).getString("task_name"));
        Assert.assertEquals(apiPageContext.getString("sort_column"),esbPageContext.getString("sort_column"));
   }
    
    /**
     * Negative test case for listTasks method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {listTasks} integration test with negative case.", dependsOnMethods = {"testCreateProjectWithMandatoryParameters" })
    public void testListTasksWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTasks");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/projects/"+connectorProperties.getProperty("projectId")+"/tasks"+ authString+"&sort_column=invalid&per_page=2";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
 
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                esbRestResponse.getBody().getString("message"));
      
   }
    
    /**
     * Positive test case for createTimeEntry method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createTimeEntry} integration test with mandatory parameters.",dependsOnMethods = {"testCreateProjectWithMandatoryParameters","testCreateTaskWithMandatoryParameters","testAssignUserToProjectWithMandatoryParameters" })
    public void testCreateTimeEntryWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTimeEntry");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTimeEntry_mandatory.json");
       
        final String timeEntryId = esbRestResponse.getBody().getJSONObject("time_entry").getString("time_entry_id");
        connectorProperties.put("timeEntryId", timeEntryId);
        
        final String apiEndpoint = apiEndpointUrl + "/projects/timeentries/" + timeEntryId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiTimeEntryObject=apiRestResponse.getBody().getJSONObject("time_entry");
        
        Assert.assertEquals(connectorProperties.getProperty("projectId"), apiTimeEntryObject.getString("project_id"));
        Assert.assertEquals(connectorProperties.getProperty("taskId"), apiTimeEntryObject.getString("task_id"));
        Assert.assertEquals(connectorProperties.getProperty("taskUserId"), apiTimeEntryObject.getString("user_id"));
        Assert.assertEquals(connectorProperties.getProperty("logDate"), apiTimeEntryObject.getString("log_date"));
        Assert.assertEquals(connectorProperties.getProperty("logTime"), apiTimeEntryObject.getString("log_time"));        
    }
    
    /**
     * Positive test case for createTimeEntry method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createTimeEntry} integration test with optional parameters.",dependsOnMethods = {"testCreateProjectWithMandatoryParameters","testCreateTaskWithOptionalParameters","testAssignUserToProjectWithMandatoryParameters" })
    public void testCreateTimeEntryWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTimeEntry");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTimeEntry_optional.json");
        
        final String timeEntryId = esbRestResponse.getBody().getJSONObject("time_entry").getString("time_entry_id");
        connectorProperties.put("timeEntryId", timeEntryId);
        
        final String apiEndpoint = apiEndpointUrl + "/projects/timeentries/" + timeEntryId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiTimeEntryObject=apiRestResponse.getBody().getJSONObject("time_entry");
        
        Assert.assertEquals(connectorProperties.getProperty("isBillable"), apiTimeEntryObject.getString("is_billable"));
        Assert.assertEquals(connectorProperties.getProperty("timeEntryNotes"), apiTimeEntryObject.getString("notes"));      
    }
    
    /**
     * Negative test case for createTimeEntry method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {createTimeEntry} integration test with negative case.")
    public void testCreateTimeEntryWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTimeEntry");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTimeEntry_negative.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/projects/timeentries" + authString + "&JSONString="
                        + URLEncoder.encode("{\"project_id\": \"INVALID\"}", "UTF-8");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        
    }
    
    /**
     * Positive test case for getTimeEntry method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {getTimeEntry} integration test with mandatory parameters.", dependsOnMethods = { "testCreateTimeEntryWithMandatoryParameters" })
    public void testGetTimeEntryWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getTimeEntry");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTimeEntry_mandatory.json");
        
        JSONObject esbTimeEntryObject=esbRestResponse.getBody().getJSONObject("time_entry");
        
        final String apiEndpoint =
                apiEndpointUrl + "/projects/timeentries/" + connectorProperties.getProperty("timeEntryId") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiTimeEntryObject=apiRestResponse.getBody().getJSONObject("time_entry");
        
        Assert.assertEquals(apiTimeEntryObject.getString("project_id"), esbTimeEntryObject.getString("project_id"));
        Assert.assertEquals(apiTimeEntryObject.getString("task_id"), esbTimeEntryObject.getString("task_id"));
        Assert.assertEquals(apiTimeEntryObject.getString("user_id"), esbTimeEntryObject.getString("user_id"));
        Assert.assertEquals(apiTimeEntryObject.getString("log_date"), esbTimeEntryObject.getString("log_date"));
        
    }
    
    /**
     * Positive test case for listTimeEntries method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {listTimeEntries} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateTimeEntryWithMandatoryParameters", "testCreateTimeEntryWithOptionalParameters" })
    public void testListTimeEntriesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTimeEntries");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTimeEntries_mandatory.json");
        
        final JSONArray esbTimeEntriesArray=esbRestResponse.getBody().getJSONArray("time_entries");
        
        final String apiEndpoint = apiEndpointUrl + "/projects/timeentries" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONArray apiTimeEntriesArray=apiRestResponse.getBody().getJSONArray("time_entries");
        
        Assert.assertEquals(apiTimeEntriesArray.length(),esbTimeEntriesArray.length());
        
        Assert.assertEquals(apiTimeEntriesArray.getJSONObject(0).getString("time_entry_id"), esbTimeEntriesArray.getJSONObject(0).getString("time_entry_id"));
        Assert.assertEquals(apiTimeEntriesArray.getJSONObject(0).getString("project_id"), esbTimeEntriesArray.getJSONObject(0).getString("project_id"));
        Assert.assertEquals(apiTimeEntriesArray.getJSONObject(0).getString("task_id"), esbTimeEntriesArray.getJSONObject(0).getString("task_id"));
        Assert.assertEquals(apiTimeEntriesArray.getJSONObject(0).getString("user_id"), esbTimeEntriesArray.getJSONObject(0).getString("user_id"));
        Assert.assertEquals(apiTimeEntriesArray.getJSONObject(0).getString("created_time"), esbTimeEntriesArray.getJSONObject(0).getString("created_time"));
    }
    
    /**
     * Positive test case for listTimeEntries method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {listTimeEntries} integration test with optional parameters.", dependsOnMethods = {
            "testCreateTimeEntryWithMandatoryParameters", "testCreateTimeEntryWithOptionalParameters" })
    public void testListTimeEntriesWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTimeEntries");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTimeEntries_optional.json");
        
        final JSONObject esbPageContextObject=esbRestResponse.getBody().getJSONObject("page_context");
        final JSONArray esbTimeEntriesArray=esbRestResponse.getBody().getJSONArray("time_entries");
        
        final String apiEndpoint = apiEndpointUrl + "/projects/timeentries" + authString+"&page=2&per_page=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiPageContextObject=apiRestResponse.getBody().getJSONObject("page_context");

        Assert.assertEquals(esbTimeEntriesArray.length(),1);
        
        Assert.assertEquals(apiPageContextObject.getString("page"),esbPageContextObject.getString("page"));
        Assert.assertEquals(apiPageContextObject.getString("per_page"),esbPageContextObject.getString("per_page"));
    }
    
    /**
     * Negative test case for listTimeEntries method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "zohobooks {listTimeEntries} integration test with negative case.")
    public void testListTimeEntriesWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTimeEntries");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTimeEntries_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/projects/timeentries" + authString + "&page=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(),esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getString("code"),esbRestResponse.getBody().getString("code"));
        Assert.assertEquals(apiRestResponse.getBody().getString("message"),esbRestResponse.getBody().getString("message"));
    }
    
    /**
     * Setting purchase account ID property.
     * 
     * @throws JSONException
     * @throws IOException
     */
    private void setPurchaseAccountId() throws IOException, JSONException{
    	 final String accounts = apiEndpointUrl + "/chartofaccounts" + authString+"&filter_by=AccountType.Expense";
         RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(accounts, "GET", apiRequestHeadersMap);
         String accountId = apiRestResponse.getBody().getJSONArray("chartofaccounts").getJSONObject(0).getString("account_id");
         connectorProperties.put("purchaseAccountId", accountId);
    }
    
}
