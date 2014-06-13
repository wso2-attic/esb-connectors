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

package org.wso2.carbon.connector.integration.test.quickbooks;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.signature.AuthorizationHeaderSigningStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.integration.test.quickbooks.QuickBooksHttpRequest;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class QuickbooksConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private String companyId;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("quickbooks");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Accept", "application/json");
        
        companyId = connectorProperties.getProperty("companyId");
        
    }
    
    /**
     * Positive test case for createAccount method with mandatory parameters.
     */
    @Test(priority = 1, description = "quickbooks {createAccount} integration test with mandatory parameters.")
    public void testCreateAccountWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAccount");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAccount_mandatory.json");
        
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("Account");
        String accountId = esbResponseObject.getString("Id");
        connectorProperties.put("expenseAccountRef", accountId);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/account/" + accountId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Account");
        
        Assert.assertEquals(connectorProperties.getProperty("accountNameMandatory"),
                apiResponseObject.getString("Name"));
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        
    }
    
    /**
     * Positive test case for createAccount method with optional parameters.
     */
    @Test(priority = 1, description = "quickbooks {createAccount} integration test with optional parameters.")
    public void testCreateAccountWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAccount");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAccount_optional.json");
        
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("Account");
        String accountId = esbResponseObject.getString("Id");
        connectorProperties.put("bankAccoutId", accountId);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/account/" + accountId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Account");
        
        Assert.assertEquals(connectorProperties.getProperty("accountNameOptional"), apiResponseObject.getString("Name"));
        Assert.assertEquals("LKR", apiResponseObject.getJSONObject("CurrencyRef").getString("value"));
        Assert.assertEquals("Savings", apiResponseObject.getString("AccountSubType"));
        Assert.assertEquals("Test description", apiResponseObject.getString("Description"));
        
    }
    
    /**
     * Negative test case for createAccount.
     */
    @Test(priority = 1, description = "quickbooks {createAccount} integration test with negative case.")
    public void testCreateAccountWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAccount");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAccount_negative.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/account/";
        String OAuthHeader = getOAuthHeader("POST", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createAccount_negative.json");
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("Message"), esbResponseArray.getJSONObject(0)
                .getString("Message"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("code"), esbResponseArray.getJSONObject(0)
                .getString("code"));
    }
    
    /**
     * Positive test case for createCustomer method with mandatory parameters.
     */
    @Test(priority = 1, description = "quickbooks {createCustomer} integration test with mandatory parameters.")
    public void testCreateCustomerWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCustomer");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomer_mandatory.json");
        
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("Customer");
        String customerId = esbResponseObject.getString("Id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/customer/" + customerId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Customer");
        
        Assert.assertEquals(connectorProperties.getProperty("customerNameMandatory"),
                apiResponseObject.getString("FamilyName"));
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        
    }
    
    /**
     * Positive test case for createCustomer method with optional parameters.
     */
    @Test(priority = 1, description = "quickbooks {createCustomer} integration test with optional parameters.")
    public void testCreateCustomerWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCustomer");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomer_optional.json");
        
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("Customer");
        String customerId = esbResponseObject.getString("Id");
        connectorProperties.put("customerRef", customerId);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/customer/" + customerId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Customer");
        
        Assert.assertEquals(connectorProperties.getProperty("customerNameOptional"),
                apiResponseObject.getString("FamilyName"));
        Assert.assertEquals("+947111", apiResponseObject.getJSONObject("PrimaryPhone").getString("FreeFormNumber"));
        Assert.assertEquals("WSO2", apiResponseObject.getString("CompanyName"));
        
    }
    
    /**
     * Negative test case for createCustomer.
     */
    @Test(priority = 1, description = "quickbooks {createCustomer} integration test with negative case.")
    public void testCreateCustomerWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCustomer");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomer_negative.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/customer/";
        String OAuthHeader = getOAuthHeader("POST", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createCustomer_negative.json");
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("Message"), esbResponseArray.getJSONObject(0)
                .getString("Message"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("code"), esbResponseArray.getJSONObject(0)
                .getString("code"));
    }
    
    /**
     * Positive test case for createVendor method with mandatory parameters.
     */
    @Test(priority = 1, description = "quickbooks {createVendor} integration test with mandatory parameters.")
    public void testCreateVendorWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createVendor");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createVendor_mandatory.json");
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("Vendor");
        String vendorId = esbResponseObject.getString("Id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/vendor/" + vendorId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Vendor");
        
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        Assert.assertEquals(esbResponseObject.getString("DisplayName"), apiResponseObject.getString("DisplayName"));
        Assert.assertEquals(esbResponseObject.getString("Active"), apiResponseObject.getString("Active"));
    }
    
    /**
     * Positive test case for createVendor method with optional parameters.
     */
    @Test(priority = 1, description = "quickbooks {createVendor} integration test with optional parameters.")
    public void testCreateVendorWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createVendor");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createVendor_optional.json");
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("Vendor");
        
        String vendorId = esbResponseObject.getString("Id");
        connectorProperties.put("vendorRef", vendorId);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/vendor/" + vendorId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Vendor");
        
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        Assert.assertEquals(esbResponseObject.getString("TaxIdentifier"), apiResponseObject.getString("TaxIdentifier"));
        Assert.assertEquals(esbResponseObject.getString("GivenName"), apiResponseObject.getString("GivenName"));
        Assert.assertEquals(esbResponseObject.getJSONObject("AlternatePhone").getString("FreeFormNumber"),
                apiResponseObject.getJSONObject("AlternatePhone").getString("FreeFormNumber"));
    }
    
    /**
     * Negative test case for createVendor.
     */
    @Test(priority = 1, description = "quickbooks {createVendor} integration test with negative case.")
    public void testCreateVendorWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createVendor");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createVendor_negative.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/vendor/";
        String OAuthHeader = getOAuthHeader("POST", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createVendor_negative.json");
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("Message"), esbResponseArray.getJSONObject(0)
                .getString("Message"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("code"), esbResponseArray.getJSONObject(0)
                .getString("code"));
    }
    
    /**
     * Positive test case for createItem method with mandatory parameters.
     */
    @Test(priority = 1, description = "quickbooks {createItem} integration test with mandatory parameters.")
    public void tesCreateItemWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createItem");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createItem_mandatory.json");
        
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("Item");
        String itemId = esbResponseObject.getString("Id");
        connectorProperties.put("ItemRef1", itemId);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/item/" + itemId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Item");
        
        Assert.assertEquals(connectorProperties.getProperty("itemNameMandatory"), apiResponseObject.getString("Name"));
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        
    }
    
    /**
     * Positive test case for createItem method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateAccountWithMandatoryParameters" }, description = "quickbooks {createItem} integration test with optional parameters.")
    public void tesCreateItemWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createItem");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createItem_optional.json");
        
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("Item");
        String itemId = esbResponseObject.getString("Id");
        connectorProperties.put("ItemRef2", itemId);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/item/" + itemId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Item");
        
        Assert.assertEquals(connectorProperties.getProperty("itemNameOptional"), apiResponseObject.getString("Name"));
        Assert.assertEquals("Item description", apiResponseObject.getString("Description"));
        Assert.assertEquals("1500", apiResponseObject.getString("QtyOnHand"));
        Assert.assertEquals(connectorProperties.getProperty("inventoryStartDate"), apiResponseObject.getString("InvStartDate"));
        
    }
    
    /**
     * Negative test case for createItem.
     */
    @Test(priority = 1, description = "quickbooks {createItem} integration test with negative case.")
    public void testCreateItemWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createItem");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createItem_negative.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/item/";
        String OAuthHeader = getOAuthHeader("POST", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createItem_negative.json");
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("Message"), esbResponseArray.getJSONObject(0)
                .getString("Message"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("code"), esbResponseArray.getJSONObject(0)
                .getString("code"));
    }
    
    /**
     * Positive test case for createPurchase method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateAccountWithMandatoryParameters",
            "testCreateAccountWithOptionalParameters" }, description = "quickbooks {createPurchase} integration test with mandatory parameters.")
    public void tesCreatePurchaseWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPurchase");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPurchase_mandatory.json");
        
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("Purchase");
        String purchaseId = esbResponseObject.getString("Id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/purchase/" + purchaseId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Purchase");
        JSONArray apiLineArray = apiResponseObject.getJSONArray("Line");
        
        Assert.assertEquals(connectorProperties.getProperty("expenseAccountRef"), apiLineArray.getJSONObject(0)
                .getJSONObject("AccountBasedExpenseLineDetail").getJSONObject("AccountRef").getString("value"));
        Assert.assertEquals(connectorProperties.getProperty("bankAccoutId"),
                apiResponseObject.getJSONObject("AccountRef").getString("value"));
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        
    }
    
    /**
     * Positive test case for createPurchase method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateCustomerWithOptionalParameters",
            "tesCreatePurchaseWithMandatoryParameters" }, description = "quickbooks {createPurchase} integration test with optional parameters.")
    public void tesCreatePurchaseWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPurchase");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPurchase_optional.json");
        
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("Purchase");
        String purchaseId = esbResponseObject.getString("Id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/purchase/" + purchaseId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Purchase");
        
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        Assert.assertEquals("LKR", apiResponseObject.getJSONObject("CurrencyRef").getString("value"));
        Assert.assertEquals("Private Note", apiResponseObject.getString("PrivateNote"));
        Assert.assertEquals("NeedToPrint", apiResponseObject.getString("PrintStatus"));
        Assert.assertEquals(connectorProperties.getProperty("customerRef"), apiResponseObject
                .getJSONObject("EntityRef").getString("value"));
        
    }
    
    /**
     * Negative test case for createPurchase.
     */
    @Test(priority = 1, description = "quickbooks {createPurchase} integration test with negative case.")
    public void testCreatePurchaseWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPurchase");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPurchase_negative.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/purchase/";
        String OAuthHeader = getOAuthHeader("POST", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createPurchase_negative.json");
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("Message"), esbResponseArray.getJSONObject(0)
                .getString("Message"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("code"), esbResponseArray.getJSONObject(0)
                .getString("code"));
    }
    
    /**
     * Positive test case for createPurchaseOrder method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "tesCreateItemWithOptionalParameters",
            "testCreateVendorWithOptionalParameters" }, description = "quickbooks {createPurchaseOrder} integration test with mandatory parameters.")
    public void tesCreatePurchaseOrderWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPurchaseOrder");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPurchaseOrder_mandatory.json");
        
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("PurchaseOrder");
        String purchaseOrderId = esbResponseObject.getString("Id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/purchaseorder/"
                        + purchaseOrderId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("PurchaseOrder");
        JSONArray apiLineArray = apiResponseObject.getJSONArray("Line");
        
        Assert.assertEquals(connectorProperties.getProperty("ItemRef2"),
                apiLineArray.getJSONObject(0).getJSONObject("ItemBasedExpenseLineDetail").getJSONObject("ItemRef")
                        .getString("value"));
        Assert.assertEquals(connectorProperties.getProperty("vendorRef"), apiResponseObject.getJSONObject("VendorRef")
                .getString("value"));
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        
    }
    
    /**
     * Positive test case for createPurchaseOrder method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "tesCreateItemWithOptionalParameters",
            "testCreateVendorWithOptionalParameters" }, description = "quickbooks {createPurchaseOrder} integration test with optional parameters.")
    public void tesCreatePurchaseOrderWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPurchaseOrder");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPurchaseOrder_optional.json");
        
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("PurchaseOrder");
        String purchaseOrderId = esbResponseObject.getString("Id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/purchaseorder/"
                        + purchaseOrderId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("PurchaseOrder");
        
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        Assert.assertEquals("LKR", apiResponseObject.getJSONObject("CurrencyRef").getString("value"));
        Assert.assertEquals("Private Note", apiResponseObject.getString("PrivateNote"));
        Assert.assertEquals(connectorProperties.getProperty("txnDate"), apiResponseObject.getString("TxnDate"));
        Assert.assertEquals("Open", apiResponseObject.getString("POStatus"));
        
    }
    
    /**
     * Negative test case for createPurchaseOrder.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateVendorWithOptionalParameters" }, description = "quickbooks {createPurchaseOrder} integration test with negative case.")
    public void testCreatePurchaseOrderWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPurchaseOrder");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPurchaseOrder_negative.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/purchaseorder/";
        String OAuthHeader = getOAuthHeader("POST", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createPurchaseOrder_negative.json");
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("Message"), esbResponseArray.getJSONObject(0)
                .getString("Message"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("code"), esbResponseArray.getJSONObject(0)
                .getString("code"));
    }
    
    /**
     * Positive test case for createBill method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testCreateAccountWithMandatoryParameters",
            "testCreateVendorWithOptionalParameters" }, description = "quickbooks {createBill} integration test with mandatory parameters.")
    public void testCreateBillWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createBill");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createBill_mandatory.json");
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("Bill");
        String billId = esbResponseObject.getString("Id");
        connectorProperties.put("billPaymentTxn1Id", billId);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/bill/" + billId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Bill");
        Assert.assertEquals(billId, apiResponseObject.getString("Id"));
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        
    }
    
    /**
     * Positive test case for createBill method with optional parameters
     */
    @Test(priority = 2, dependsOnMethods = { "testCreateAccountWithMandatoryParameters",
            "testCreateVendorWithOptionalParameters" }, description = "quickbooks {createBill} integration test with optional parameters.")
    public void testCreateBillWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createBill");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createBill_optional.json");
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("Bill");
        String billId = esbResponseObject.getString("Id");
        connectorProperties.put("billPaymentTxn2Id", billId);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/bill/" + billId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Bill");
        
        Assert.assertEquals(billId, apiResponseObject.getString("Id"));
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        
    }
    
    /**
     * Negative test case for createBill.
     */
    @Test(priority = 2, description = "quickbooks {createBill} integration test negative case.")
    public void testCreateBillNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createBill");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createBill_negative.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/bill/";
        String OAuthHeader = getOAuthHeader("POST", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createBill_negative.json");
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("Message"), esbResponseArray.getJSONObject(0)
                .getString("Message"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("code"), esbResponseArray.getJSONObject(0)
                .getString("code"));
    }
    
    /**
     * Positive test case for createInvoice method with mandatory parameters.
     */
    @Test(priority = 1, description = "quickbooks {createInvoice} integration test with mandatory parameters.")
    public void testCreateInvoiceWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_mandatory.json");
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("Invoice");
        String invoiceId = esbResponseObject.getString("Id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/invoice/" + invoiceId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Invoice");
        Assert.assertEquals(invoiceId, apiResponseObject.getString("Id"));
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        
    }
    
    /**
     * Positive test case for createInvoice method with optional parameters
     */
    @Test(priority = 1, description = "quickbooks {createInvoice} integration test with optional parameters.")
    public void testCreateInvoiceWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_optional.json");
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("Invoice");
        String invoiceId = esbResponseObject.getString("Id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/invoice/" + invoiceId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Invoice");
        Assert.assertEquals(invoiceId, apiResponseObject.getString("Id"));
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        Assert.assertEquals(connectorProperties.getProperty("docNumber"), apiResponseObject.getString("DocNumber"));
    }
    
    /**
     * Negative test case for createInvoice.
     */
    @Test(priority = 1, description = "quickbooks {createInvoice} integration test negative case.")
    public void testCreateInvoiceNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_negative.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/invoice/";
        String OAuthHeader = getOAuthHeader("POST", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createInvoice_negative.json");
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("Message"), esbResponseArray.getJSONObject(0)
                .getString("Message"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("code"), esbResponseArray.getJSONObject(0)
                .getString("code"));
    }
    
    /**
     * Positive test case for createMemo method with mandatory parameters.
     */
    @Test(priority = 1, description = "quickbooks {createMemo} integration test with mandatory parameters.")
    public void testCreateMemoWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createMemo");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createMemo_mandatory.json");
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("CreditMemo");
        String creditMemoId = esbResponseObject.getString("Id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/creditmemo/" + creditMemoId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("CreditMemo");
        Assert.assertEquals(creditMemoId, apiResponseObject.getString("Id"));
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        
    }
    
    /**
     * Positive test case for createMemo method with optional parameters
     */
    @Test(priority = 1, description = "quickbooks {createMemo} integration test with optional parameters.")
    public void testCreateMemoWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createMemo");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createMemo_optional.json");
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("CreditMemo");
        String creditMemoId = esbResponseObject.getString("Id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/creditmemo/" + creditMemoId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("CreditMemo");
        Assert.assertEquals(creditMemoId, apiResponseObject.getString("Id"));
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        Assert.assertEquals(esbResponseObject.getString("PrivateNote"), apiResponseObject.getString("PrivateNote"));
        Assert.assertEquals(esbResponseObject.getString("CustomerMemo"), apiResponseObject.getString("CustomerMemo"));
    }
    
    /**
     * Negative test case for createMemo.
     */
    @Test(priority = 1, description = "quickbooks {createMemo} integration test negative case.")
    public void testCreateMemoNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createMemo");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createMemo_negative.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/invoice/";
        String OAuthHeader = getOAuthHeader("POST", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createMemo_negative.json");
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("Message"), esbResponseArray.getJSONObject(0)
                .getString("Message"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("code"), esbResponseArray.getJSONObject(0)
                .getString("code"));
    }
    
    /**
     * Positive test case for createSalesReceipt method with mandatory parameters.
     */
    @Test(priority = 1, description = "quickbooks {createSalesReceipt} integration test with mandatory parameters.")
    public void testCreateSalesReceiptWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createSalesReceipt");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createSalesReceipt_mandatory.json");
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("SalesReceipt");
        String salesReceiptId = esbResponseObject.getString("Id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/salesreceipt/"
                        + salesReceiptId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("SalesReceipt");
        Assert.assertEquals(salesReceiptId, apiResponseObject.getString("Id"));
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        
    }
    
    /**
     * Positive test case for createSalesReceipt method with optional parameters
     */
    @Test(priority = 1, description = "quickbooks {createSalesReceipt} integration test with optional parameters.")
    public void testCreateSalesReceiptWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createSalesReceipt");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createSalesReceipt_optional.json");
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("SalesReceipt");
        String salesReceiptId = esbResponseObject.getString("Id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/salesreceipt/"
                        + salesReceiptId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("SalesReceipt");
        Assert.assertEquals(salesReceiptId, apiResponseObject.getString("Id"));
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        Assert.assertEquals(esbResponseObject.getString("PrivateNote"), apiResponseObject.getString("PrivateNote"));
        Assert.assertEquals(connectorProperties.getProperty("docNumber"), apiResponseObject.getString("DocNumber"));
    }
    
    /**
     * Negative test case for createSalesReceipt.
     */
    @Test(priority = 1, description = "quickbooks {createSalesReceipt} integration test negative case.")
    public void testCreateSalesReceiptNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createSalesReceipt");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createSalesReceipt_negative.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/salesreceipt/";
        String OAuthHeader = getOAuthHeader("POST", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createSalesReceipt_negative.json");
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("Message"), esbResponseArray.getJSONObject(0)
                .getString("Message"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("code"), esbResponseArray.getJSONObject(0)
                .getString("code"));
    }
    
    /**
     * Positive test case for createPayment method with mandatory parameters.
     */
    @Test(priority = 1, description = "quickbooks {createPayment} integration test with mandatory parameters.")
    public void testCreatePaymentWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPayment");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPayment_mandatory.json");
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("Payment");
        String paymentId = esbResponseObject.getString("Id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/payment/" + paymentId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Payment");
        
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        Assert.assertEquals(esbResponseObject.getString("TxnDate"), apiResponseObject.getString("TxnDate"));
    }
    
    /**
     * Positive test case for createPayment method with optional parameters.
     */
    @Test(priority = 1, description = "quickbooks {createPayment} integration test with optional parameters.")
    public void testCreatePaymentWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPayment");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPayment_optional.json");
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("Payment");
        String paymentId = esbResponseObject.getString("Id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/payment/" + paymentId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Payment");
        
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        Assert.assertEquals(esbResponseObject.getString("TxnDate"), apiResponseObject.getString("TxnDate"));
        Assert.assertEquals(esbResponseObject.getString("ProcessPayment"),
                apiResponseObject.getString("ProcessPayment"));
        Assert.assertEquals(esbResponseObject.getString("PrivateNote"), apiResponseObject.getString("PrivateNote"));
    }
    
    /**
     * Negative test case for createPayment.
     */
    @Test(priority = 1, description = "quickbooks {createPayment} integration test with negative case.")
    public void testCreatePaymentWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPayment");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPayment_negative.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/payment/";
        String OAuthHeader = getOAuthHeader("POST", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createPayment_negative.json");
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("Message"), esbResponseArray.getJSONObject(0)
                .getString("Message"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("code"), esbResponseArray.getJSONObject(0)
                .getString("code"));
    }
    
    /**
     * Positive test case for createEstimate method with mandatory parameters.
     */
    @Test(priority = 1, description = "quickbooks {createEstimate} integration test with mandatory parameters.")
    public void testCreateEstimateWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createEstimate");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEstimate_mandatory.json");
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("Estimate");
        String estimateId = esbResponseObject.getString("Id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/estimate/" + estimateId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Estimate");
        
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        Assert.assertEquals(esbResponseObject.getString("DocNumber"), apiResponseObject.getString("DocNumber"));
        Assert.assertEquals(esbResponseObject.getString("ApplyTaxAfterDiscount"),
                apiResponseObject.getString("ApplyTaxAfterDiscount"));
        Assert.assertEquals(esbResponseObject.getString("TxnDate"), apiResponseObject.getString("TxnDate"));
    }
    
    /**
     * Positive test case for createEstimate method with optional parameters.
     */
    @Test(priority = 1, description = "quickbooks {createEstimate} integration test with optional parameters.")
    public void testCreateEstimateWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createEstimate");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEstimate_optional.json");
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("Estimate");
        
        String estimateId = esbResponseObject.getString("Id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/estimate/" + estimateId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Estimate");
        
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        Assert.assertEquals(esbResponseObject.getString("DocNumber"), apiResponseObject.getString("DocNumber"));
        Assert.assertEquals(esbResponseObject.getString("ApplyTaxAfterDiscount"),
                apiResponseObject.getString("ApplyTaxAfterDiscount"));
        Assert.assertEquals(esbResponseObject.getString("ExpirationDate"),
                apiResponseObject.getString("ExpirationDate"));
        Assert.assertEquals(esbResponseObject.getJSONObject("BillAddr").getString("City"), apiResponseObject
                .getJSONObject("BillAddr").getString("City"));
        Assert.assertEquals(esbResponseObject.getString("ShipDate"), apiResponseObject.getString("ShipDate"));
        Assert.assertEquals(esbResponseObject.getJSONObject("BillEmail").getString("Address"), apiResponseObject
                .getJSONObject("BillEmail").getString("Address"));
        
    }
    
    /**
     * Negative test case for createEstimate.
     */
    @Test(priority = 1, description = "quickbooks {createEstimate} integration test with negative case.")
    public void testCreateEstimateWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createEstimate");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEstimate_negative.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/estimate/";
        String OAuthHeader = getOAuthHeader("POST", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createEstimate_negative.json");
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("Message"), esbResponseArray.getJSONObject(0)
                .getString("Message"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("code"), esbResponseArray.getJSONObject(0)
                .getString("code"));
    }
    
    /**
     * Positive test case for createBillPayment method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testCreateAccountWithMandatoryParameters",
            "testCreateVendorWithOptionalParameters", "testCreateCustomerWithOptionalParameters",
            "testCreateBillWithMandatoryParameters", "testCreateBillWithOptionalParameters" }, description = "quickbooks {createBillPayment} integration test with mandatory parameters.")
    public void testCreateBillPaymentWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createBillPayment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createBillPayment_mandatory.json");
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("BillPayment");
        String billPaymentId = esbResponseObject.getString("Id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/billpayment/"
                        + billPaymentId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("BillPayment");
        
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        Assert.assertEquals(esbResponseObject.getString("domain"), apiResponseObject.getString("domain"));
        Assert.assertEquals(esbResponseObject.getString("SyncToken"), apiResponseObject.getString("SyncToken"));
        Assert.assertEquals(esbResponseObject.getString("TxnDate"), apiResponseObject.getString("TxnDate"));
    }
    
    /**
     * Positive test case for createBillPayment method with optional parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testCreateAccountWithMandatoryParameters",
            "testCreateVendorWithOptionalParameters", "testCreateCustomerWithOptionalParameters",
            "testCreateBillWithMandatoryParameters", "testCreateBillWithOptionalParameters" }, description = "quickbooks {createBillPayment} integration test with mandatory parameters.")
    public void testCreateBillPaymentWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createBillPayment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createBillPayment_optional.json");
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("BillPayment");
        String billPaymentId = esbResponseObject.getString("Id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/billpayment/"
                        + billPaymentId;
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("BillPayment");
        
        Assert.assertEquals(esbResponseObject.getJSONObject("MetaData").getString("CreateTime"), apiResponseObject
                .getJSONObject("MetaData").getString("CreateTime"));
        Assert.assertEquals(esbResponseObject.getString("domain"), apiResponseObject.getString("domain"));
        Assert.assertEquals(esbResponseObject.getString("SyncToken"), apiResponseObject.getString("SyncToken"));
        Assert.assertEquals(esbResponseObject.getString("PrivateNote"), apiResponseObject.getString("PrivateNote"));
        Assert.assertEquals(esbResponseObject.getString("sparse"), apiResponseObject.getString("sparse"));
    }
    
    /**
     * Negative test case for createBillPayment.
     */
    @Test(priority = 2, description = "quickbooks {createBillPayment} integration test with negative case.")
    public void testCreateBillPaymentWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createBillPayment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createBillPayment_negative.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/billpayment/";
        String OAuthHeader = getOAuthHeader("POST", apiEndPoint);
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createBillPayment_negative.json");
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("Message"), esbResponseArray.getJSONObject(0)
                .getString("Message"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("code"), esbResponseArray.getJSONObject(0)
                .getString("code"));
    }
    
    /**
     * Positive test case for query method with mandatory parameters.
     */
    @Test(priority = 1, description = "quickbooks {query} integration test with mandatory parameters.")
    public void testQueryWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:query");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_query_mandatory.json");
        
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("QueryResponse");
        JSONArray esbAccountArray = esbResponseObject.getJSONArray("Account");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId
                        + "/query?query=select%20*%20from%20Account%20ORDERBY%20Id%20MAXRESULTS%2010";
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("QueryResponse");
        JSONArray apiAccountArray = apiResponseObject.getJSONArray("Account");
        
        Assert.assertEquals(esbAccountArray.length(), apiAccountArray.length());
        Assert.assertEquals(esbAccountArray.getJSONObject(0).getString("Name"), apiAccountArray.getJSONObject(0)
                .getString("Name"));
        Assert.assertEquals(esbAccountArray.getJSONObject(0).getString("AccountType"), apiAccountArray.getJSONObject(0)
                .getString("AccountType"));
        Assert.assertEquals(esbResponseObject.getString("maxResults"), apiResponseObject.getString("maxResults"));
        Assert.assertEquals(esbResponseObject.getString("startPosition"), apiResponseObject.getString("startPosition"));
        
    }
    
    /**
     * Negative test case for query.
     */
    @Test(priority = 1, description = "quickbooks {query} integration test with negative case.")
    public void testQueryWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:query");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_query_negative.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/v3/company/" + companyId + "/query?query=%20";
        String OAuthHeader = getOAuthHeader("GET", apiEndPoint);
        
        apiRequestHeadersMap.put("Authorization", OAuthHeader);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONObject("Fault").getJSONArray("Error");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("Message"), esbResponseArray.getJSONObject(0)
                .getString("Message"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("code"), esbResponseArray.getJSONObject(0)
                .getString("code"));
    }
    
    // generating authentication signature
    private String getOAuthHeader(String requestMethod, String requestUrl) {
    
        String OAuthHeader = null;
        
        final String consumerKey = connectorProperties.getProperty("consumerKey");
        final String consumerSecret = connectorProperties.getProperty("consumerSecret");
        final String accessToken = connectorProperties.getProperty("accessToken");
        final String accessTokenSecret = connectorProperties.getProperty("accessTokenSecret");
        
        final QuickBooksHttpRequest request = new QuickBooksHttpRequest();
        request.setRequestUrl(requestUrl);
        request.setMethod(requestMethod);
        
        // Generate the Authorization and get response through signpost.
        final OAuthConsumer consumer = new DefaultOAuthConsumer(consumerKey, consumerSecret);
        consumer.setTokenWithSecret(accessToken, accessTokenSecret);
        consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
        HttpRequest response;
        try {
            
            response = consumer.sign(request);
            
            OAuthHeader = response.getHeader(OAuth.HTTP_AUTHORIZATION_HEADER);
            
        } catch (OAuthMessageSignerException omse) {
            log.error("Error occured in connector", omse);
            
        } catch (OAuthExpectationFailedException oefe) {
            log.error("Error occured in connector", oefe);
            
        } catch (OAuthCommunicationException oce) {
            log.error("Error occured in connector", oce);
            
        }
        
        return OAuthHeader;
        
    }
    
}
