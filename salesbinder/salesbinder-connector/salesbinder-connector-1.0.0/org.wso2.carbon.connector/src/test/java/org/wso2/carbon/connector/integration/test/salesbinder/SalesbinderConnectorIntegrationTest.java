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

package org.wso2.carbon.connector.integration.test.salesbinder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;
import org.apache.axiom.om.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SalesbinderConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap;
    
    private Map<String, String> apiRequestHeadersMap;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("salesbinder-connector-1.0.0");
        
        esbRequestHeadersMap = new HashMap<String, String>();
        apiRequestHeadersMap = new HashMap<String, String>();
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        // Create base64-encoded auth string using apiKey and password
        final String authString = connectorProperties.getProperty("apiKey") + ":X";
        final String base64AuthString = Base64.encode(authString.getBytes());
        
        apiRequestHeadersMap.put("Authorization", "Basic " + base64AuthString);
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
    }
    
    /**
     * Positive test case for createContact method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "salesbinder {createContact} integration test with mandatory parameters.")
    public void testCreateContactWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 302);
        
        String contactLocationURL = esbRestResponse.getHeadersMap().get("Location").toString();
        contactLocationURL = contactLocationURL.substring(1, contactLocationURL.length() - 1);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(contactLocationURL, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Contact");
        
        String contactId = apiResponseObject.getString("id");
        connectorProperties.setProperty("contactId", contactId);
        
        Assert.assertEquals(apiResponseObject.getString("email_1"), connectorProperties.get("contactEmail"));
        
    }
    
    /**
     * Positive test case for createContact method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "salesbinder {createContact} integration test with optional parameters.")
    public void testCreateContactWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 302);
        
        String contactLocationURL = esbRestResponse.getHeadersMap().get("Location").toString();
        contactLocationURL = contactLocationURL.substring(1, contactLocationURL.length() - 1);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(contactLocationURL, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Contact");
        
        Assert.assertEquals(apiResponseObject.getString("email_1"), connectorProperties.get("contactEmailOpt"));
        Assert.assertEquals(apiResponseObject.getString("first_name"), connectorProperties.get("contactFirstName"));
        Assert.assertEquals(apiResponseObject.getString("last_name"), connectorProperties.get("contactLastName"));
        
    }
    
    /**
     * Negative test case for createContact method.
     */
    @Test(groups = { "wso2.esb" }, description = "salesbinder {createContact} integration test with negative case.")
    public void testCreateContactWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_negative.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("Errors");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/contacts.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createContact_negative.json");
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("Errors");
        
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getJSONObject("Error").getString("message"),
                esbResponseArray.getJSONObject(0).getJSONObject("Error").getString("message"));
        
    }
    
    /**
     * Positive test case for getContactById method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateContactWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "salesbinder {getContactById} integration test with mandatory parameters.")
    public void testGetContactByIdWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getContactById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactById_mandatory.json");
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("Contact");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/contacts/"
                        + connectorProperties.getProperty("contactId") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Contact");
        
        Assert.assertEquals(apiResponseObject.getString("email_1"), esbResponseObject.getString("email_1"));
        Assert.assertEquals(apiResponseObject.getString("created"), esbResponseObject.getString("created"));
        Assert.assertEquals(apiResponseObject.getString("modified"), esbResponseObject.getString("modified"));
    }
    
    /**
     * Positive test case for listContacts method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateContactWithMandatoryParameters", "testCreateContactWithOptionalParameters" }, groups = { "wso2.esb" }, description = "salesbinder {listContacts} integration test with mandatory parameters.")
    public void testListContactsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listContacts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContacts_mandatory.json");
        
        JSONObject esbResponseObject = esbRestResponse.getBody();
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/contacts.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody();
        
        Assert.assertEquals(apiResponseObject.getString("count"), esbResponseObject.getString("count"));
        Assert.assertEquals(apiResponseObject.getString("pages"), esbResponseObject.getString("pages"));
        
        JSONObject esbFirstContactObject =
                esbResponseObject.getJSONArray("Contacts").getJSONObject(0).getJSONObject("Contact");
        JSONObject apiFirstContactObject =
                apiResponseObject.getJSONArray("Contacts").getJSONObject(0).getJSONObject("Contact");
        
        Assert.assertEquals(apiFirstContactObject.getString("id"), esbFirstContactObject.getString("id"));
        Assert.assertEquals(apiFirstContactObject.getString("email_1"), esbFirstContactObject.getString("email_1"));
        Assert.assertEquals(apiFirstContactObject.getString("created"), esbFirstContactObject.getString("created"));
        
    }
    
    /**
     * Positive test case for addInventoryItem method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "freeagent {addInventoryItem} integration test with mandatory parameters.")
    public void testAddInventoryItemWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:addInventoryItem");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addInventoryItem_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 302);
        
        String itemLocationURL = esbRestResponse.getHeadersMap().get("Location").toString();
        itemLocationURL = itemLocationURL.substring(1, itemLocationURL.length() - 1);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(itemLocationURL, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Item");
        
        //String itemId = apiResponseObject.getString("id");
        //connectorProperties.setProperty("itemId", itemId);
        
        Assert.assertEquals(apiResponseObject.getString("name"), connectorProperties.get("itemName"));
        Assert.assertEquals(apiResponseObject.getString("cost"), connectorProperties.get("itemCost"));
        Assert.assertEquals(apiResponseObject.getString("multiple"), connectorProperties.get("itemMultiple"));
    }
    
    /**
     * Positive test case for addInventoryItem method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "freeagent {addInventoryItem} integration test with optional parameters.")
    public void testAddInventoryItemWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:addInventoryItem");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addInventoryItem_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 302);
        
        String itemLocationURL = esbRestResponse.getHeadersMap().get("Location").toString();
        itemLocationURL = itemLocationURL.substring(1, itemLocationURL.length() - 1);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(itemLocationURL, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Item");
        
        String itemId = apiResponseObject.getString("id");
        connectorProperties.setProperty("itemId", itemId);
        
        Assert.assertEquals(apiResponseObject.getString("name"), connectorProperties.get("itemName"));
        Assert.assertEquals(apiResponseObject.getString("cost"), connectorProperties.get("itemCost"));
        Assert.assertEquals(apiResponseObject.getString("multiple"), connectorProperties.get("itemMultiple"));
    }
    
    /**
     * Negative test case for addInventoryItem method.
     */
    @Test(groups = { "wso2.esb" }, description = "salesbinder {addInventoryItem} integration test with negative case.")
    public void testAddInventoryItemWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:addInventoryItem");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addInventoryItem_negative.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("Errors");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/items.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_addInventoryItem_negative.json");
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("Errors");
        
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getJSONObject("Error").getString("message"),
                esbResponseArray.getJSONObject(0).getJSONObject("Error").getString("message"));
        
    }
    
    /**
     * Positive test case for getInventoryItemById method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testAddInventoryItemWithOptionalParameters" }, groups = { "wso2.esb" }, description = "salesbinder {getInventoryItemById} integration test with mandatory parameters.")
    public void testGetInventoryItemByIdWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getInventoryItemById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getInventoryItemById_mandatory.json");
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("Item");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/items/" + connectorProperties.getProperty("itemId")
                        + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Item");
        
        Assert.assertEquals(apiResponseObject.getString("name"), esbResponseObject.getString("name"));
        Assert.assertEquals(apiResponseObject.getString("created"), esbResponseObject.getString("created"));
        Assert.assertEquals(apiResponseObject.getString("modified"), esbResponseObject.getString("modified"));
    }
    
    /**
     * Positive test case for listInventoryItems method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testAddInventoryItemWithOptionalParameters",
            "testAddInventoryItemWithOptionalParameters" }, groups = { "wso2.esb" }, description = "salesbinder {listInventoryItems} integration test with mandatory parameters.")
    public void testListInventoryItemsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listInventoryItems");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInventoryItems_mandatory.json");
        JSONObject esbResponseObject =
                esbRestResponse.getBody().getJSONArray("Items").getJSONObject(0).getJSONObject("Item");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/items.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject =
                apiRestResponse.getBody().getJSONArray("Items").getJSONObject(0).getJSONObject("Item");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("count"), esbRestResponse.getBody().getString("count"));
        Assert.assertEquals(apiResponseObject.getString("id"), esbResponseObject.getString("id"));
        Assert.assertEquals(apiResponseObject.getString("modified"), esbResponseObject.getString("modified"));
        Assert.assertEquals(apiResponseObject.getString("created"), esbResponseObject.getString("created"));
    }
    
    /**
     * Positive test case for listInventoryItems method with optional parameters.
     */
    @Test(dependsOnMethods = { "testAddInventoryItemWithOptionalParameters",
            "testAddInventoryItemWithOptionalParameters" }, groups = { "wso2.esb" }, description = "salesbinder {listInventoryItems} integration test with optional parameters.")
    public void testListInventoryItemsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listInventoryItems");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInventoryItems_optional.json");
        JSONObject esbResponseObject =
                esbRestResponse.getBody().getJSONArray("Items").getJSONObject(0).getJSONObject("Item");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/items.json?locationId="
                        + connectorProperties.getProperty("locationId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject =
                apiRestResponse.getBody().getJSONArray("Items").getJSONObject(0).getJSONObject("Item");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("count"), esbRestResponse.getBody().getString("count"));
        Assert.assertEquals(apiResponseObject.getString("id"), esbResponseObject.getString("id"));
        Assert.assertEquals(apiResponseObject.getString("modified"), esbResponseObject.getString("modified"));
        Assert.assertEquals(apiResponseObject.getString("created"), esbResponseObject.getString("created"));
    }
    
    /**
     * Positive test case for createDocument method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "salesbinder {createDocument} integration test with mandatory parameters.")
    public void testCreateDocumentWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createDocument");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDocument_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 302);
        
        String documentLocationURL = esbRestResponse.getHeadersMap().get("Location").toString();
        documentLocationURL = documentLocationURL.substring(1, documentLocationURL.length() - 1);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(documentLocationURL, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Document");
        
        String documentId = apiResponseObject.getString("id");
        connectorProperties.setProperty("documentId", documentId);
        
        Assert.assertEquals(apiResponseObject.getString("issue_date"), connectorProperties.get("issueDate"));
        
    }
    
    /**
     * Positive test case for createDocument method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateAccountWithMandatoryParameters" },groups = { "wso2.esb" }, description = "salesbinder {createDocument} integration test with optional parameters.")
    public void testCreateDocumentWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createDocument");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDocument_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 302);
        
        String documentLocationURL = esbRestResponse.getHeadersMap().get("Location").toString();
        documentLocationURL = documentLocationURL.substring(1, documentLocationURL.length() - 1);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(documentLocationURL, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Document");
        
        String documentId = apiResponseObject.getString("id");
        connectorProperties.setProperty("documentId", documentId);
        
        Assert.assertEquals(apiResponseObject.getString("issue_date"), connectorProperties.get("issueDate"));
        Assert.assertEquals(apiResponseObject.getString("shipping_address"), connectorProperties.get("shippingAddress"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("Customer").getString("id"), connectorProperties.get("accountId"));
        
    }
    
    /**
     * Negative test case for createDocument method.
     */
    @Test(groups = { "wso2.esb" }, description = "salesbinder {createDocument} integration test with negative case.")
    public void testCreateDocumentWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createDocument");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDocument_negative.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("Errors");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/documents.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createDocument_negative.json");
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("Errors");
        
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getJSONObject("Error").getString("message"),
                esbResponseArray.getJSONObject(0).getJSONObject("Error").getString("message"));

        
    }
    
    /**
     * Positive test case for getDocumentById method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateDocumentWithOptionalParameters" }, groups = { "wso2.esb" }, description = "salesbinder {getDocumentById} integration test with mandatory parameters.")
    public void testGetDocumentByIdWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getDocumentById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDocumentById_mandatory.json");
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("Document");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/documents/"
                        + connectorProperties.getProperty("documentId") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Document");
        
        Assert.assertEquals(apiResponseObject.getString("name"), esbResponseObject.getString("name"));
        Assert.assertEquals(apiResponseObject.getString("created"), esbResponseObject.getString("created"));
        Assert.assertEquals(apiResponseObject.getString("modified"), esbResponseObject.getString("modified"));
    }
    
    /**
     * Positive test case for listDocuments method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateDocumentWithOptionalParameters" }, groups = { "wso2.esb" }, description = "salesbinder {listDocuments} integration test with mandatory parameters.")
    public void testListDocumentsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listDocuments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDocuments_mandatory.json");
        JSONObject esbResponseObject =
                esbRestResponse.getBody().getJSONArray("Documents").getJSONObject(0).getJSONObject("Document");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/documents.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject =
                apiRestResponse.getBody().getJSONArray("Documents").getJSONObject(0).getJSONObject("Document");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("count"), esbRestResponse.getBody().getString("count"));
        Assert.assertEquals(apiResponseObject.getString("id"), esbResponseObject.getString("id"));
        Assert.assertEquals(apiResponseObject.getString("modified"), esbResponseObject.getString("modified"));
        Assert.assertEquals(apiResponseObject.getString("created"), esbResponseObject.getString("created"));
    }
    
    /**
     * Positive test case for listDocuments method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateDocumentWithOptionalParameters" }, groups = { "wso2.esb" }, description = "salesbinder {listDocuments} integration test with optional parameters.")
    public void testListDocumentsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listDocuments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDocuments_optional.json");
        JSONObject esbResponseObject =
                esbRestResponse.getBody().getJSONArray("Documents").getJSONObject(0).getJSONObject("Document");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/documents.json?contextId="
                        + connectorProperties.getProperty("documentContextId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject =
                apiRestResponse.getBody().getJSONArray("Documents").getJSONObject(0).getJSONObject("Document");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("count"), esbRestResponse.getBody().getString("count"));
        Assert.assertEquals(apiResponseObject.getString("id"), esbResponseObject.getString("id"));
        Assert.assertEquals(apiResponseObject.getString("modified"), esbResponseObject.getString("modified"));
        Assert.assertEquals(apiResponseObject.getString("created"), esbResponseObject.getString("created"));
    }
    
    /**
     * Positive test case for createAccount method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "salesbinder {createAccount} integration test with mandatory parameters.")
    public void testCreateAccountWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAccount");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAccount_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 302);
        
        String accountLocationURL = esbRestResponse.getHeadersMap().get("Location").toString();
        accountLocationURL = accountLocationURL.substring(1, accountLocationURL.length() - 1);

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(accountLocationURL, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject = apiRestResponse.getBody();
        
        String accountId = apiResponseObject.getJSONObject("Customer").getString("id");
        connectorProperties.setProperty("accountId", accountId);
        
        Assert.assertEquals(apiResponseObject.getJSONObject("Context").getString("id"),
                connectorProperties.get("accountContextId"));
        
    }
    
    /**
     * Positive test case for createAccount method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "salesbinder {createAccount} integration test with optional parameters.")
    public void testCreateAccountWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAccount");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAccount_optional.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 302);
        
        String accountLocationURL = esbRestResponse.getHeadersMap().get("Location").toString();
        accountLocationURL = accountLocationURL.substring(1, accountLocationURL.length() - 1);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(accountLocationURL, "GET", apiRequestHeadersMap);
        JSONObject apiCustomerObject = apiRestResponse.getBody().getJSONObject("Customer");
        
        Assert.assertEquals(apiCustomerObject.getString("name"), connectorProperties.get("accountName"));
        Assert.assertEquals(apiCustomerObject.getString("office_email"), connectorProperties.get("accountOfficeEmail"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("Context").getString("id"),
                connectorProperties.get("accountContextId"));
        
    }
    
    /**
     * Negative test case for createAccount method.
     */
    @Test(groups = { "wso2.esb" }, description = "salesbinder {createAccount} integration test with negative case.")
    public void testCreateAccountWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAccount");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAccount_negative.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("Errors");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/customers.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createAccount_negative.json");
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("Errors");
        
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getJSONObject("Error").getString("message"),
                esbResponseArray.getJSONObject(0).getJSONObject("Error").getString("message"));
      
        
    }
    
    /**
     * Positive test case for getAccountById method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateAccountWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "salesbinder {getAccountById} integration test with mandatory parameters.")
    public void testGetAccountByIdWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAccountById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAccountById_mandatory.json");
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("Customer");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/customers/"
                        + connectorProperties.getProperty("accountId") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("Customer");
        
        Assert.assertEquals(apiResponseObject.getString("name"), esbResponseObject.getString("name"));
        Assert.assertEquals(apiResponseObject.getString("created"), esbResponseObject.getString("created"));
        Assert.assertEquals(apiResponseObject.getString("modified"), esbResponseObject.getString("modified"));
    }
    
    /**
     * Positive test case for listAccounts method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateAccountWithMandatoryParameters","testCreateAccountWithOptionalParameters" }, groups = { "wso2.esb" }, description = "salesbinder {listAccounts} integration test with mandatory parameters.")
    public void testListAccountsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listAccounts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAccounts_mandatory.json");
        JSONObject esbResponseObject =
                esbRestResponse.getBody().getJSONArray("Customers").getJSONObject(0).getJSONObject("Customer");
        JSONObject esbResponseContextObject =
                esbRestResponse.getBody().getJSONArray("Customers").getJSONObject(0).getJSONObject("Context");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/customers.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject =
                apiRestResponse.getBody().getJSONArray("Customers").getJSONObject(0).getJSONObject("Customer");
        
        JSONObject apiResponseContextObject =
                apiRestResponse.getBody().getJSONArray("Customers").getJSONObject(0).getJSONObject("Context");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("count"), esbRestResponse.getBody().getString("count"));
        Assert.assertEquals(apiResponseObject.getString("id"), esbResponseObject.getString("id"));
        Assert.assertEquals(apiResponseContextObject.getString("id"), esbResponseContextObject.getString("id"));
        Assert.assertEquals(apiResponseObject.getString("created"), esbResponseObject.getString("created"));
    }
    
    /**
     * Positive test case for listAccounts method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateAccountWithMandatoryParameters","testCreateAccountWithOptionalParameters" }, groups = { "wso2.esb" }, description = "salesbinder {listAccounts} integration test with optional parameters.")
    public void testListAccountsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listAccounts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAccounts_optional.json");
        
        JSONObject esbResponseObject =
                esbRestResponse.getBody().getJSONArray("Customers").getJSONObject(0).getJSONObject("Customer");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/customers.json?contextId="
                        + connectorProperties.getProperty("accountContextId")+"&page=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject =
                apiRestResponse.getBody().getJSONArray("Customers").getJSONObject(0).getJSONObject("Customer");
        
        Assert.assertEquals(apiRestResponse.getBody().getString("count"), esbRestResponse.getBody().getString("count"));
        Assert.assertEquals(apiResponseObject.getString("id"), esbResponseObject.getString("id"));
        Assert.assertEquals(apiResponseObject.getString("modified"), esbResponseObject.getString("modified"));
        Assert.assertEquals(apiResponseObject.getString("created"), esbResponseObject.getString("created"));
    }
    
}
