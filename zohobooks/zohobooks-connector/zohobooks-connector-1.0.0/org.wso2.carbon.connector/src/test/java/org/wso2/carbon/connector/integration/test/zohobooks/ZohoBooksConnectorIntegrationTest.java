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
