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

package org.wso2.carbon.connector.integration.test.printavo;

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

public class PrintavoConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap;
    
    private Map<String, String> apiRequestHeadersMap;
    
    private String authString;
    
    private String apiEndpointUrl;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        
        init("printavo-connector-1.0.0");
        
        esbRequestHeadersMap = new HashMap<String, String>();
        apiRequestHeadersMap = new HashMap<String, String>();
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Accept", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        authString = "?key=" + connectorProperties.getProperty("apiKey");
        apiEndpointUrl = connectorProperties.getProperty("apiUrl") + "/api/v1";
        
    }
    
    /**
     * Positive test case for createCustomer method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     * @throws NumberFormatException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {createCustomer} integration test with mandatory parameters.")
    public void testCreateCustomerWithMandatoryParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:createCustomer");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomer_mandatory.json");
        String createdDateESB = esbRestResponse.getBody().getString("created_at");
        String[] createdDateWithoutTimeESB = createdDateESB.split("T");
        
        final String customerId = esbRestResponse.getBody().getString("id");
        
        final String apiEndpoint = apiEndpointUrl + "/customers/" + customerId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        String createdDateAPI = esbRestResponse.getBody().getString("created_at");
        String[] createdDateWithoutTimeAPI = createdDateAPI.split("T");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("url"), apiRestResponse.getBody().getString("url"));
        Assert.assertEquals(connectorProperties.getProperty("customerFirstName"), apiRestResponse.getBody().getString(
                        "first_name"));
        Assert.assertEquals(createdDateWithoutTimeESB[0], createdDateWithoutTimeAPI[0]);
        Assert.assertEquals(esbRestResponse.getBody().getString("public_page_url"), apiRestResponse.getBody()
                        .getString("public_page_url"));
        
    }
    
    /**
     * Positive test case for createCustomer method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {createCustomer} integration test with optional parameters.", dependsOnMethods = {
                    "testCreateCustomerWithMandatoryParameters" })
    public void testCreateCustomerWithOptionalParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:createCustomer");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomer_optional.json");
        
        final String customerIdOptional = esbRestResponse.getBody().getString("id");
        connectorProperties.put("customerIdOptional", customerIdOptional);
        
        final String apiEndpoint = apiEndpointUrl + "/customers/" + customerIdOptional + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("optionalCustomerFirstName"), apiRestResponse.getBody()
                        .getString("first_name"));
        Assert.assertEquals(connectorProperties.getProperty("optionalCustomerLastName"), apiRestResponse.getBody()
                        .getString("last_name"));
        Assert.assertEquals(connectorProperties.getProperty("optionalCustomerCompany"), apiRestResponse.getBody()
                        .getString("company"));
        Assert.assertEquals(connectorProperties.getProperty("optionalCustomerEmail"), apiRestResponse.getBody()
                        .getString("email"));
        Assert.assertEquals(connectorProperties.getProperty("optionalCustomerPhone"), apiRestResponse.getBody()
                        .getString("phone"));
        
    }
    
    /**
     * Negative test case for createCustomer method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {createCustomer} integration test with negative case.", dependsOnMethods = {
                    "testCreateCustomerWithOptionalParameters" })
    public void testCreateCustomerWithNegativeCase() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:createCustomer");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomer_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/customers" + authString;
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_createCustomer_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        Assert.assertEquals(esbRestResponse.getBody().getString("details"), apiRestResponse.getBody().getString(
                        "details"));
        
    }
    
    /**
     * Positive test case for getCustomer method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {getCustomer} integration test with mandatory parameters.",
                    dependsOnMethods = { "testCreateCustomerWithNegativeCase" })
    public void testGetCustomerWithMandatoryParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:getCustomer");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCustomer_mandatory.json");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/customers/" + connectorProperties.getProperty("customerIdOptional")
                                        + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("first_name"), apiRestResponse.getBody().getString(
                        "first_name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("last_name"), apiRestResponse.getBody().getString(
                        "last_name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("public_page_url"), apiRestResponse.getBody()
                        .getString("public_page_url"));
        Assert.assertEquals(esbRestResponse.getBody().getString("orders_count"), apiRestResponse.getBody().getString(
                        "orders_count"));
        Assert.assertEquals(esbRestResponse.getBody().getString("company"), apiRestResponse.getBody().getString(
                        "company"));
        
    }
    
    /**
     * Method name: getCustomer
     * Test scenario: Optional
     * Reason to skip: There are no optional parameters in
     * this method.
     */
    
    /**
     * Negative test case for getCustomer method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {getCustomer} integration test with negative case.",
                    dependsOnMethods = { "testGetCustomerWithMandatoryParameters" })
    public void testGetCustomerWithNegativeCase() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:getCustomer");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCustomer_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/customers/invalid" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for listCustomers method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {listCustomers} integration test with mandatory parameters.",
                    dependsOnMethods = { "testGetCustomerWithNegativeCase" })
    public void testListCustomersWithMandatoryParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:listCustomers");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomers_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/customers" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(1).getString("id"),
                        apiRestResponse.getBody().getJSONArray("data").getJSONObject(1).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(1).getString("first_name"),
                        apiRestResponse.getBody().getJSONArray("data").getJSONObject(1).getString("first_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(1).getString("created_at"),
                        apiRestResponse.getBody().getJSONArray("data").getJSONObject(1).getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(1).getString("tax_exempt"),
                        apiRestResponse.getBody().getJSONArray("data").getJSONObject(1).getString("tax_exempt"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(1).getString("last_name"),
                        apiRestResponse.getBody().getJSONArray("data").getJSONObject(1).getString("last_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").getString("total_count"), apiRestResponse
                        .getBody().getJSONObject("meta").getString("total_count"));
        
    }
    
    /**
     * Positive test case for listCustomers method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {listCustomers} integration test with optional parameters.",
                    dependsOnMethods = { "testListCustomersWithMandatoryParameters" })
    public void testListCustomersWithOptionalParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:listCustomers");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomers_optional.json");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/customers" + authString + "&per_page="
                                        + connectorProperties.getProperty("perPage") + "&page="
                                        + connectorProperties.getProperty("page");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("perPage"), apiRestResponse.getBody().getJSONObject("meta")
                        .getString("per_page"));
        Assert.assertEquals(connectorProperties.getProperty("page"), apiRestResponse.getBody().getJSONObject("meta")
                        .getString("page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("first_name"),
                        apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("first_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0)
                        .getString("public_page_url"), apiRestResponse.getBody().getJSONArray("data").getJSONObject(0)
                        .getString("public_page_url"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("orders_count"),
                        apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("orders_count"));
        
    }
    
    /**
     * Negative test case for listCustomers method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {listCustomers} integration test with negative case.",
                    dependsOnMethods = { "testListCustomersWithOptionalParameters" })
    public void testListCustomersWithNegativeCase() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:listCustomers");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomers_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/customers" + authString + "&per_page=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Method name: updateCustomer
     * Test scenario: Mandatory
     * Reason to skip: All the parameters are optional in
     * this method.
     */
    
    /**
     * Positive test case for updateCustomer method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {updateCustomer} integration test with optional parameters.",
                    dependsOnMethods = { "testListCustomersWithNegativeCase" })
    public void testUpdateCustomerWithOptionalParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:updateCustomer");
        final String apiEndpoint =
                        apiEndpointUrl + "/customers/" + connectorProperties.getProperty("customerIdOptional")
                                        + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCustomer_optional.json");
        
        RestResponse<JSONObject> apiRestResponse2 = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("tax_exempt"), apiRestResponse2.getBody().getString(
                        "tax_exempt"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("tax_resale_num"), apiRestResponse2.getBody()
                        .getString("tax_resale_num"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("email"), apiRestResponse2.getBody().getString(
                        "email"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("fax"), apiRestResponse2.getBody().getString("fax"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("phone"), apiRestResponse2.getBody().getString(
                        "phone"));
        
    }
    
    /**
     * Negative test case for updateCustomer method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {updateCustomer} integration test with negative case.",
                    dependsOnMethods = { "testUpdateCustomerWithOptionalParameters" })
    public void testUpdateCustomerWithNegativeCase() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:updateCustomer");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCustomer_negative.json");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/customers/" + connectorProperties.getProperty("customerIdOptional")
                                        + authString;
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap,
                                        "api_updateCustomer_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        Assert.assertEquals(esbRestResponse.getBody().getString("details"), apiRestResponse.getBody().getString(
                        "details"));
        
    }
    
    /**
     * Positive test case for createOrder method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {createOrder} integration test with mandatory parameters.",
                    dependsOnMethods = { "testUpdateCustomerWithNegativeCase" })
    public void testCreateOrderWithMandatoryParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:createOrder");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createOrder_mandatory.json");
        
        final String orderId = esbRestResponse.getBody().getString("id");
        
        final String apiEndpoint = apiEndpointUrl + "/orders/" + orderId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("orderUserId"), apiRestResponse.getBody().getString(
                        "user_id"));
        Assert.assertEquals(connectorProperties.getProperty("customerIdOptional"), apiRestResponse.getBody().getString(
                        "customer_id"));
        Assert.assertEquals(connectorProperties.getProperty("orderStatusId"), apiRestResponse.getBody().getString(
                        "orderstatus_id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("visual_id"), apiRestResponse.getBody().getString(
                        "visual_id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
        
    }
    
    /**
     * Positive test case for createOrder method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {createOrder} integration test with optional parameters.",
                    dependsOnMethods = { "testCreateOrderWithMandatoryParameters" })
    public void testCreateOrderWithOptionalParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:createOrder");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createOrder_optional.json");
        
        final String orderIdOptional = esbRestResponse.getBody().getString("id");
        connectorProperties.put("orderIdOptional", orderIdOptional);
        
        final String apiEndpoint = apiEndpointUrl + "/orders/" + orderIdOptional + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("orderSalesTax"), apiRestResponse.getBody().getString(
                        "sales_tax"));
        Assert.assertEquals(connectorProperties.getProperty("orderDiscountAsPercentage"), apiRestResponse.getBody()
                        .getString("discount_as_percentage"));
        Assert.assertEquals(connectorProperties.getProperty("orderDiscount"), apiRestResponse.getBody().getString(
                        "discount"));
        Assert.assertEquals(connectorProperties.getProperty("orderProductionNotes"), apiRestResponse.getBody()
                        .getString("production_notes"));
        Assert.assertEquals(connectorProperties.getProperty("orderNickname"), apiRestResponse.getBody().getString(
                        "order_nickname"));
        
    }
    
    /**
     * Negative test case for createOrder method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {createOrder} integration test with negative case.",
                    dependsOnMethods = { "testCreateOrderWithOptionalParameters" })
    public void testCreateOrderWithNegativeCase() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:createOrder");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createOrder_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/orders" + authString;
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createOrder_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for getOrder method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {getOrder} integration test with mandatory parameters.",
                    dependsOnMethods = { "testCreateOrderWithNegativeCase" })
    public void testGetOrderWithMandatoryParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:getOrder");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getOrder_mandatory.json");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/orders/" + connectorProperties.getProperty("orderIdOptional") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("production_notes"), apiRestResponse.getBody()
                        .getString("production_notes"));
        Assert.assertEquals(esbRestResponse.getBody().getString("public_hash"), apiRestResponse.getBody().getString(
                        "public_hash"));
        Assert.assertEquals(esbRestResponse.getBody().getString("user_id"), apiRestResponse.getBody().getString(
                        "user_id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("order_nickname"), apiRestResponse.getBody().getString(
                        "order_nickname"));
        Assert.assertEquals(esbRestResponse.getBody().getString("discount"), apiRestResponse.getBody().getString(
                        "discount"));
        
    }
    
    /**
     * Method name: getOrder Test scenario: Optional Reason to skip: There are no optional parameters in this
     * method.
     */
    
    /**
     * Negative test case for getOrder method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {getOrder} integration test with negative case.",
                    dependsOnMethods = { "testGetOrderWithMandatoryParameters" })
    public void testGetOrderWithNegativeCase() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:getOrder");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getOrder_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/orders/invalid" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for listOrders method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {listOrders} integration test with mandatory parameters.",
                    dependsOnMethods = { "testGetOrderWithNegativeCase" })
    public void testListOrdersWithMandatoryParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:listOrders");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listOrders_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/orders" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").getString("total_count"), apiRestResponse
                        .getBody().getJSONObject("meta").getString("total_count"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("user_id"),
                        apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("user_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString(
                        "production_notes"), apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString(
                        "production_notes"));
        
    }
    
    /**
     * Positive test case for listOrders method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {listOrders} integration test with optional parameters.",
                    dependsOnMethods = { "testListOrdersWithMandatoryParameters" })
    public void testListOrdersWithOptionalParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:listOrders");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listOrders_optional.json");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/orders" + authString + "&per_page="
                                        + connectorProperties.getProperty("perPage") + "&page="
                                        + connectorProperties.getProperty("page");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("page"), apiRestResponse.getBody().getJSONObject("meta")
                        .getString("page"));
        Assert.assertEquals(connectorProperties.getProperty("perPage"), apiRestResponse.getBody().getJSONObject("meta")
                        .getString("per_page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("customer_id"),
                        apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("customer_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("user_id"),
                        esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("user_id"));
        
    }
    
    /**
     * Negative test case for listOrders method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {listOrders} integration test with negative case.",
                    dependsOnMethods= {"testListOrdersWithOptionalParameters"})
    public void testListOrdersWithNegativeCase() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:listOrders");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listOrders_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/orders" + authString + "&page=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Method name: updateOrder Test scenario: Mandatory Reason to skip: All the parameters are optional in
     * this method.
     */
    
    /**
     * Positive test case for updateOrder method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {updateOrder} integration test with optional parameters.",
                    dependsOnMethods = { "testListOrdersWithNegativeCase" })
    public void testUpdateOrderWithOptionalParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:updateOrder");
        final String apiEndpoint =
                        apiEndpointUrl + "/orders/" + connectorProperties.getProperty("orderIdOptional") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateOrder_optional.json");
        
        RestResponse<JSONObject> apiRestResponse2 = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponse.getBody().getString("sales_tax"), apiRestResponse2.getBody().getString(
                        "sales_tax"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("discount_as_percentage"), apiRestResponse2
                        .getBody().getString("discount_as_percentage"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("discount"), apiRestResponse2.getBody().getString(
                        "discount"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("production_notes"), apiRestResponse2.getBody()
                        .getString("production_notes"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("order_nickname"), apiRestResponse2.getBody()
                        .getString("order_nickname"));
        
    }
    
    /**
     * Negative test case for updateOrder method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {updateOrder} integration test with negative case.",
                    dependsOnMethods = { "testUpdateOrderWithOptionalParameters" })
    public void testUpdateOrderWithNegativeCase() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:updateOrder");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateOrder_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/orders/invalid" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for deleteOrder method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {deleteOrder} integration test with mandatory parameters.",
                    dependsOnMethods = { "testDeleteExpenseWithNegativeCase" })
    public void testDeleteOrderWithMandatoryParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:deleteOrder");
        final String apiEndpoint =
                        apiEndpointUrl + "/orders/" + connectorProperties.getProperty("orderIdOptional") + authString;
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeout")));
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteOrder_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponse2 = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse2.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Method name: deleteOrder
     * Test scenario: Optional
     * Reason to skip: There are no optional parameters in
     * this method.
     */
    
    /**
     * Negative test case for deleteOrder method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {deleteOrder} integration test with negative case.",
                    dependsOnMethods = { "testDeleteOrderWithMandatoryParameters" })
    public void testDeleteOrderWithNegativeCase() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:deleteOrder");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteOrder_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/orders/invalid" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for createProduct method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {createProduct} integration test with mandatory parameters.",
                    dependsOnMethods = { "testUpdateOrderWithNegativeCase" })
    public void testCreateProductWithMandatoryParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:createProduct");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProduct_mandatory.json");
        
        final String productId = esbRestResponse.getBody().getString("id");
        String createdDateESB = esbRestResponse.getBody().getString("created_at");
        String updatedDateESB = esbRestResponse.getBody().getString("updated_at");
        String[] createdDateWithoutTimeESB = createdDateESB.split("T");
        String[] updatedDateWithoutTimeESB = updatedDateESB.split("T");
        
        final String apiEndpoint = apiEndpointUrl + "/products/" + productId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        String createdDateAPI = apiRestResponse.getBody().getString("created_at");
        String updatedDateAPI = apiRestResponse.getBody().getString("updated_at");
        String[] createdDateWithoutTimeAPI = createdDateAPI.split("T");
        String[] updatedDateWithoutTimeAPI = updatedDateAPI.split("T");
        
        Assert.assertEquals(createdDateWithoutTimeESB[0], createdDateWithoutTimeAPI[0]);
        Assert.assertEquals(updatedDateWithoutTimeESB[0], updatedDateWithoutTimeAPI[0]);
        
    }
    
    /**
     * Positive test case for createProduct method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {createProduct} integration test with optional parameters.",
                    dependsOnMethods = { "testCreateProductWithMandatoryParameters" })
    public void testCreateProductWithOptionalParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:createProduct");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProduct_optional.json");
        
        final String productIdOptional = esbRestResponse.getBody().getString("id");
        connectorProperties.put("productIdOptional", productIdOptional);
        
        final String apiEndpoint = apiEndpointUrl + "/products/" + productIdOptional + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("productPricePer"), apiRestResponse.getBody().getString(
                        "price_per"));
        Assert.assertEquals(connectorProperties.getProperty("styleNumber"), apiRestResponse.getBody().getString(
                        "style_number"));
        Assert.assertEquals(connectorProperties.getProperty("brand"), apiRestResponse.getBody().getString("brand"));
        Assert.assertEquals(connectorProperties.getProperty("size"), apiRestResponse.getBody().getString("size"));
        
    }
    
    /**
     * Negative test case for createProduct method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {createProduct} integration test with negative case.", dependsOnMethods = {
                    "testCreateProductWithOptionalParameters" })
    public void testCreateProductWithNegativeCase() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:createProduct");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProduct_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/products" + authString;
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_createProduct_negative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for getProduct method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {getProduct} integration test with mandatory parameters.",
                    dependsOnMethods = { "testCreateProductWithNegativeCase" })
    public void testGetProductWithMandatoryParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:getProduct");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProduct_mandatory.json");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/products/" + connectorProperties.getProperty("productIdOptional")
                                        + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("price_per"), apiRestResponse.getBody().getString(
                        "price_per"));
        Assert.assertEquals(esbRestResponse.getBody().getString("brand"), apiRestResponse.getBody().getString("brand"));
        Assert.assertEquals(esbRestResponse.getBody().getString("size"), apiRestResponse.getBody().getString("size"));
        Assert.assertEquals(esbRestResponse.getBody().getString("style_number"), apiRestResponse.getBody().getString(
                        "style_number"));
        
    }
    
    /**
     * Method name: getProduct Test scenario: Optional Reason to skip: There are no optional parameters in
     * this method.
     */
    
    /**
     * Negative test case for getProduct method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {getProduct} integration test with negative case.",
                    dependsOnMethods = { "testGetProductWithMandatoryParameters" })
    public void testGetProductWithNegativeCase() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:getProduct");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProduct_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/products/invalid" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for listProducts method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {listProducts} integration test with mandatory parameters.",
                    dependsOnMethods = { "testGetProductWithNegativeCase" })
    public void testListProductsWithMandatoryParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:listProducts");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProducts_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/products" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").getString("per_page"), apiRestResponse
                        .getBody().getJSONObject("meta").getString("per_page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").getString("total_count"), apiRestResponse
                        .getBody().getJSONObject("meta").getString("total_count"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"));
        
    }
    
    /**
     * Positive test case for listProducts method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {listProducts} integration test with optional parameters.",
                    dependsOnMethods = { "testListProductsWithMandatoryParameters" })
    public void testListProductsWithOptionalParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:listProducts");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProducts_optional.json");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/products" + authString + "&per_page="
                                        + connectorProperties.getProperty("perPage") + "&page="
                                        + connectorProperties.getProperty("page");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").getString("page"), apiRestResponse
                        .getBody().getJSONObject("meta").getString("page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").getString("per_page"), apiRestResponse
                        .getBody().getJSONObject("meta").getString("per_page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"));
        
    }
    
    /**
     * Negative test case for listProducts method.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {listProducts} integration test with negative case.",
                    dependsOnMethods = { "testListProductsWithOptionalParameters" })
    public void testListProductsWithNegativeCase() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:listProducts");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProducts_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/products" + authString + "&page=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Method name: updateProduct Test scenario: Mandatory Reason to skip: All the parameters are optional in
     * this method.
     */
    
    /**
     * Positive test case for updateProduct method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {updateProduct} integration test with optional parameters.",
                    dependsOnMethods = { "testListProductsWithNegativeCase" })
    public void testUpdateProductWithOptionalParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:updateProduct");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateProduct_optional.json");
        final String apiEndpoint2 =
                        apiEndpointUrl + "/products/" + connectorProperties.getProperty("productIdOptional")
                                        + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint2, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("productPricePerUpdated"), apiRestResponse.getBody()
                        .getString("price_per"));
        Assert.assertEquals(connectorProperties.getProperty("productStyleNumberUpdated"), apiRestResponse.getBody()
                        .getString("style_number"));
        Assert.assertEquals(connectorProperties.getProperty("productBrandUpdated"), apiRestResponse.getBody()
                        .getString("brand"));
        Assert.assertEquals(connectorProperties.getProperty("productSizeUpdated"), apiRestResponse.getBody().getString(
                        "size"));
        
    }
    
    /**
     * Negative test case for updateProduct method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {updateProduct} integration test with negative case.",
                    dependsOnMethods = { "testUpdateProductWithOptionalParameters" })
    public void testUpdateProductWithNegativeCase() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:updateProduct");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateProduct_negative.json");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/products/" + connectorProperties.getProperty("productIdOptional")
                                        + authString;
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_updateProduct_negative.json");
        
        Assert.assertNotEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for createPayment method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {createPayment} integration test with mandatory parameters.",
                    dependsOnMethods = { "testUpdateProductWithNegativeCase" })
    public void testCreatePaymentWithMandatoryParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:createPayment");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPayment_mandatory.json");
        
        final String paymentId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("paymentId", paymentId);
        String createdDateESB = esbRestResponse.getBody().getString("created_at");
        String[] partsESB = createdDateESB.split("T");
        
        final String apiEndpoint = apiEndpointUrl + "/payments/" + paymentId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        String createdDateAPI = esbRestResponse.getBody().getString("created_at");
        String[] partsAPI = createdDateAPI.split("T");
        
        Assert.assertEquals(connectorProperties.getProperty("paymentName"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("paymentAmount"), apiRestResponse.getBody().getString(
                        "amount"));
        Assert.assertEquals(partsESB[0], partsAPI[0]);
        
    }
    
    /**
     * Method name: createPayment
     * Test scenario: Optional
     * Reason to skip: There are no optional parameters in
     * this method.
     */
    
    /**
     * Negative test case for createPayment method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {createPayment} integration test with negative case.",
                    dependsOnMethods = { "testCreatePaymentWithMandatoryParameters" })
    public void testCreatePaymentWithNegativeCase() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:createPayment");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPayment_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/payments/" + authString;
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_createPayment_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for getPayment method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {getPayment} integration test with mandatory parameters.",
                    dependsOnMethods = { "testCreatePaymentWithNegativeCase" })
    public void testGetPaymentWithMandatoryParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:getPayment");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPayment_mandatory.json");
        String transactionDateESB = esbRestResponse.getBody().getString("transaction_date");
        String createdDateESB = esbRestResponse.getBody().getString("created_at");
        String[] transactionDateWithoutTimeESB = transactionDateESB.split("T");
        String[] createdDateWithoutTimeESB = createdDateESB.split("T");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/payments/" + connectorProperties.getProperty("paymentId") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        String transactionDateAPI = esbRestResponse.getBody().getString("transaction_date");
        String createdDateAPI = apiRestResponse.getBody().getString("created_at");
        String[] transactionDateWithoutTimeAPI = transactionDateAPI.split("T");
        String[] createdDateWithoutTimeAPI = createdDateAPI.split("T");
        
        Assert.assertEquals(createdDateWithoutTimeESB[0], createdDateWithoutTimeAPI[0]);
        Assert.assertEquals(esbRestResponse.getBody().getString("amount"), apiRestResponse.getBody()
                        .getString("amount"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(transactionDateWithoutTimeESB[0], transactionDateWithoutTimeAPI[0]);
        
    }
    
    /**
     * Method name: getPayment Test scenario: Optional Reason to skip: There are no optional parameters in
     * this method.
     */
    
    /**
     * Negative test case for getPayment method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {getPayment} integration test with negative case.",
                    dependsOnMethods = { "testGetPaymentWithMandatoryParameters" })
    public void testGetPaymentWithNegativeCase() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:getPayment");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPayment_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/payments/invalid" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for listPayments method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {listPayments} integration test with mandatory parameters.",
                    dependsOnMethods = { "testGetPaymentWithNegativeCase" })
    public void testListPaymentsWithMandatoryParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:listPayments");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPayments_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/payments" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").getString("per_page"), apiRestResponse
                        .getBody().getJSONObject("meta").getString("per_page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("order_id"),
                        apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("order_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("created_at"),
                        apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("created_at"));
        
    }
    
    /**
     * Positive test case for listPayments method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {listPayments} integration test with optional parameters.",
                    dependsOnMethods = { "testListPaymentsWithMandatoryParameters" })
    public void testListPaymentsWithOptionalParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:listPayments");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPayments_optional.json");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/payments" + authString + "&per_page="
                                        + connectorProperties.getProperty("perPage") + "&page="
                                        + connectorProperties.getProperty("page");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").getString("page"), apiRestResponse
                        .getBody().getJSONObject("meta").getString("page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").getString("per_page"), apiRestResponse
                        .getBody().getJSONObject("meta").getString("per_page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString(
                        "transaction_date"), apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString(
                        "transaction_date"));
        
    }
    
    /**
     * Negative test case for listPayments method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {listPayments} integration test with negative case.",
                    dependsOnMethods = { "testListPaymentsWithOptionalParameters" })
    public void testListPaymentsWithNegativeCase() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:listPayments");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPayments_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/payments" + authString + "&per_page=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for createExpense method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {createExpense} integration test with mandatory parameters.",
                    dependsOnMethods = { "testListPaymentsWithNegativeCase" })
    public void testCreateExpenseWithMandatoryParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:createExpense");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createExpense_mandatory.json");
        final String expenseId = esbRestResponse.getBody().getString("id");
        String createdDateESB = esbRestResponse.getBody().getString("created_at");
        String[] createdDateWithoutTimeESB = createdDateESB.split("T");
        
        final String apiEndpoint = apiEndpointUrl + "/expenses/" + expenseId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        String createdDateAPI = esbRestResponse.getBody().getString("created_at");
        String transactionDateAPI = apiRestResponse.getBody().getString("transaction_date");
        String expenceAmount = apiRestResponse.getBody().getString("amount");
        String[] createdDateWithoutTimeAPI = createdDateAPI.split("T");
        String[] partsAPITransaction = transactionDateAPI.split("T");
        String[] expenceAmountWithoutSigns = expenceAmount.split("-");
        
        Assert.assertEquals(connectorProperties.getProperty("expenseTransactionDate").split("T")[0],
                        partsAPITransaction[0]);
        Assert.assertEquals(connectorProperties.getProperty("expenseName"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("expenseAmount"), expenceAmountWithoutSigns[1]);
        Assert.assertEquals(createdDateWithoutTimeESB[0], createdDateWithoutTimeAPI[0]);
        
    }
    
    /**
     * Positive test case for createExpense method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {createExpense} integration test with optional parameters.",
                    dependsOnMethods = { "testCreateExpenseWithMandatoryParameters" })
    public void testCreateExpenseWithOptionalParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:createExpense");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createExpense_optional.json");
        final String expensesIdOptional = esbRestResponse.getBody().getString("id");
        connectorProperties.put("expensesIdOptional", expensesIdOptional);
        
        final String apiEndpoint = apiEndpointUrl + "/expenses/" + expensesIdOptional + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        String transactionDateAPI = apiRestResponse.getBody().getString("transaction_date");
        String expenceAmount = apiRestResponse.getBody().getString("amount");
        String[] partsAPITransaction = transactionDateAPI.split("T");
        String[] expenceAmountWithoutSigns = expenceAmount.split("-");
        
        Assert.assertEquals(connectorProperties.getProperty("expenseTransactionDate").split("T")[0],
                        partsAPITransaction[0]);
        Assert.assertEquals(connectorProperties.getProperty("expenseName"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("expenseAmount"), expenceAmountWithoutSigns[1]);
        Assert.assertEquals(connectorProperties.getProperty("orderIdOptional"), apiRestResponse.getBody().getString(
                        "order_id"));
        
    }
    
    /**
     * Negative test case for createExpense method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {createExpense} integration test with negative case.",
                    dependsOnMethods = { "testCreateExpenseWithOptionalParameters" })
    public void testCreateExpenseWithNegativeCase() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:createExpense");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createExpense_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/expenses" + authString;
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                                        "api_createExpense_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for getExpense method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {getExpense} integration test with mandatory parameters.",
                    dependsOnMethods = { "testCreateExpenseWithNegativeCase" })
    public void testGetExpenseWithMandatoryParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:getExpense");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getExpense_mandatory.json");
        String transactionDateESB = esbRestResponse.getBody().getString("transaction_date");
        String[] partsESBTransaction = transactionDateESB.split("T");
        String createdDateESB = esbRestResponse.getBody().getString("created_at");
        String[] createdDateWithoutTimeESB = createdDateESB.split("T");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/expenses/" + connectorProperties.getProperty("expensesIdOptional")
                                        + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        String transactionDateAPI = apiRestResponse.getBody().getString("transaction_date");
        String[] partsAPITransaction = transactionDateAPI.split("T");
        String createdDateAPI = esbRestResponse.getBody().getString("created_at");
        String[] createdDateWithoutTimeAPI = createdDateAPI.split("T");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("order_id"), apiRestResponse.getBody().getString(
                        "order_id"));
        Assert.assertEquals(partsESBTransaction[0], partsAPITransaction[0]);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("amount"), esbRestResponse.getBody()
                        .getString("amount"));
        Assert.assertEquals(createdDateWithoutTimeESB[0], createdDateWithoutTimeAPI[0]);
        
    }
    
    /**
     * Method name: getExpense
     * Test scenario: Optional
     * Reason to skip: There are no optional parameters in
     * this method.
     */
    
    /**
     * Negative test case for getExpense method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {getExpense} integration test with negative case.",
                    dependsOnMethods = { "testGetExpenseWithMandatoryParameters" })
    public void testGetExpenseWithNegativeCase() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:getExpense");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getExpense_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/expenses/invalid" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for listExpenses method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {listExpenses} integration test with mandatory parameters.",
                    dependsOnMethods = { "testGetExpenseWithNegativeCase" })
    public void testListExpensesWithMandatoryParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:listExpenses");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listExpenses_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/expenses" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").getString("total_count"), apiRestResponse
                        .getBody().getJSONObject("meta").getString("total_count"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").getString("total_pages"), apiRestResponse
                        .getBody().getJSONObject("meta").getString("total_pages"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"),
                        apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("name"),
                        apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("amount"),
                        apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("amount"));
        
    }
    
    /**
     * Positive test case for listExpenses method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {listExpenses} integration test with optional parameters.",
                    dependsOnMethods = { "testListExpensesWithMandatoryParameters" })
    public void testListExpensesWithOptionalParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:listExpenses");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listExpenses_optional.json");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/expenses" + authString + "&per_page="
                                        + connectorProperties.getProperty("perPage") + "&page="
                                        + connectorProperties.getProperty("page");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").getString("total_pages"), apiRestResponse
                        .getBody().getJSONObject("meta").getString("total_pages"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").getString("page"), apiRestResponse
                        .getBody().getJSONObject("meta").getString("page"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"),
                        esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("name"),
                        esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("amount"),
                        esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("amount"));
        
    }
    
    /**
     * Negative test case for listExpenses method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {listExpenses} integration test with negative case.",
                    dependsOnMethods = { "testListExpensesWithOptionalParameters" })
    public void testListExpensesWithNegativeCase() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:listExpenses");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listExpenses_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/expenses" + authString + "&per_page=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for updateExpense method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {updateExpense} integration test with mandatory parameters.",
                    dependsOnMethods = { "testListExpensesWithNegativeCase" })
    public void testUpdateExpenseWithMandatoryParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:updateExpense");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateExpense_mandatory.json");
        String updatedDateESB = esbRestResponse.getBody().getString("updated_at");
        String[] updatedDateWithoutTimeESB = updatedDateESB.split("T");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/expenses/" + connectorProperties.getProperty("expensesIdOptional")
                                        + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        String updatedDateAPI = esbRestResponse.getBody().getString("updated_at");
        String[] updatedDateWithoutTimeAPI = updatedDateAPI.split("T");
        
        Assert.assertEquals(updatedDateWithoutTimeESB[0], updatedDateWithoutTimeAPI[0]);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("amount"), apiRestResponse.getBody()
                        .getString("amount"));
        
    }
    
    /**
     * Positive test case for updateExpense method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {updateExpense} integration test with optional parameters.",
                    dependsOnMethods = { "testUpdateExpenseWithMandatoryParameters" })
    public void testUpdateExpenseWithOptionalParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:updateExpense");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateExpense_optional.json");
        String transactionDateESB = esbRestResponse.getBody().getString("transaction_date");
        String[] transactionDateWithoutTimeESB = transactionDateESB.split("T");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/expenses/" + connectorProperties.getProperty("expensesIdOptional")
                                        + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        String transactionDateAPI = apiRestResponse.getBody().getString("transaction_date");
        String[] transactionDateWithoutTimeAPI = transactionDateAPI.split("T");
        
        Assert.assertEquals(transactionDateWithoutTimeESB[0], transactionDateWithoutTimeAPI[0]);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("amount"), apiRestResponse.getBody()
                        .getString("amount"));
        
    }
    
    /**
     * Negative test case for updateExpense method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {updateExpense} integration test with negative case.",
                    dependsOnMethods = { "testUpdateExpenseWithOptionalParameters" })
    public void testUpdateExpenseWithNegativeCase() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:updateExpense");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateExpense_negative.json");
        
        final String apiEndpoint =
                        apiEndpointUrl + "/expenses/" + connectorProperties.getProperty("expensesIdOptional")
                                        + authString;
        RestResponse<JSONObject> apiRestResponse =
                        sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_updateExpense_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        Assert.assertEquals(esbRestResponse.getBody().getString("details"), apiRestResponse.getBody().getString(
                        "details"));
        
    }
    
    /**
     * Positive test case for deleteExpense method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {deleteExpense} integration test with mandatory parameters.",
                    dependsOnMethods = { "testUpdateExpenseWithNegativeCase" })
    public void testDeleteExpenseWithMandatoryParameters() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:deleteExpense");
        final String apiEndpoint =
                        apiEndpointUrl + "/expenses/" + connectorProperties.getProperty("expensesIdOptional")
                                        + authString;
        sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteExpense_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponse2 = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse2.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Method name: deleteExpense
     * Test scenario: Optional
     * Reason to skip: There are no optional parameters in
     * this method.
     */
    /*
   
   *//**
     * Negative test case for deleteExpense method.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     * @throws NumberFormatException
     */
    @Test(groups = { "wso2.esb" }, description = "printavo {deleteExpense} integration test with negative case.",
                    dependsOnMethods = { "testDeleteExpenseWithMandatoryParameters" })
    public void testDeleteExpenseWithNegativeCase() throws IOException, JSONException, NumberFormatException,
                    InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:deleteExpense");
        RestResponse<JSONObject> esbRestResponse =
                        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteExpense_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/expenses/invalid" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
}