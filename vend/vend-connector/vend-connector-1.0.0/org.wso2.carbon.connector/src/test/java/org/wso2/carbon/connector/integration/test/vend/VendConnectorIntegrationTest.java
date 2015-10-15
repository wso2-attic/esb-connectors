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
package org.wso2.carbon.connector.integration.test.vend;

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

public class VendConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap;
    
    private Map<String, String> apiRequestHeadersMap;
    
    private String apiEndpointUrl;
    
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("vend-connector-1.0.0");
        esbRequestHeadersMap = new HashMap<String, String>();
        apiRequestHeadersMap = new HashMap<String, String>();
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Accept", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        apiEndpointUrl = connectorProperties.getProperty("apiUrl") + "/api";
        connectorProperties.setProperty("productHandle", connectorProperties.getProperty("productHandle") + Math.round(1000000*Math.random()));
        connectorProperties.setProperty("productName", connectorProperties.getProperty("productName") + Math.round(1000000*Math.random()));
        connectorProperties.setProperty("updatedProductName", connectorProperties.getProperty("updatedProductName") + Math.round(1000000*Math.random()));
    }
    
    /**
     * Positive test case for getAccessTokenFromAuthorizationCode method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {getAccessTokenFromAuthorizationCode} integration test with mandatory parameters.")
    public void testGetAccessTokenFromAuthorizationCodeWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAccessTokenFromAuthorizationCode");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAccessTokenFromAuthorizationCode_mandatory.json");

        final String refreshToken = esbRestResponse.getBody().getString("refresh_token");
        connectorProperties.put("refreshToken", refreshToken);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().getString("refresh_token"));
        Assert.assertNotNull(esbRestResponse.getBody().getString("access_token"));
        
    }
    
    /**
     * Method name: getAccessTokenFromAuthorizationCode
     * Test scenario: Optional
     * Reason to skip: There are no optional parameters to be tested.
     */    
    
    /**
     * Negative test case for getAccessTokenFromAuthorizationCode.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {getAccessTokenFromAuthorizationCode} integration test with negative case.",
    dependsOnMethods = { "testGetAccessTokenFromAuthorizationCodeWithMandatoryParameters" })
    public void testGetAccessTokenFromAuthorizationCodeWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAccessTokenFromAuthorizationCode");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAccessTokenFromAuthorizationCode_negative.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 401);
        Assert.assertNotNull(esbRestResponse.getBody().getString("error"));
        Assert.assertNotNull(esbRestResponse.getBody().getString("error_description"));
    }
    
    /**
     * Positive test case for getAccessTokenFromRefreshToken method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {getAccessTokenFromRefreshToken} integration test with mandatory parameters.",
    dependsOnMethods = { "testGetAccessTokenFromAuthorizationCodeWithNegativeCase" })
    public void testGetAccessTokenFromRefreshTokenWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAccessTokenFromRefreshToken");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAccessTokenFromRefreshToken_mandatory.json");

        final String accessToken = esbRestResponse.getBody().getString("access_token");
        connectorProperties.put("accessToken", accessToken);
        apiRequestHeadersMap.put("Authorization", "Bearer " + accessToken);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().getString("token_type"));
        Assert.assertNotNull(esbRestResponse.getBody().getString("access_token"));
        
    }
    
    /**
     * Method name: getAccessTokenFromRefreshToken
     * Test scenario: Optional
     * Reason to skip: There are no optional parameters to be tested.
     */ 
    
    /**
     * Method name: getAccessTokenFromRefreshToken
     * Test scenario: Negative
     * Reason to skip: There are no any negative parameters in this method.
     */
    
    /**
     * Positive test case for createCustomer method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {createCustomer} integration test with mandatory parameters.",
    dependsOnMethods = { "testGetAccessTokenFromRefreshTokenWithMandatoryParameters" })
    public void testCreateCustomerWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCustomer");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomer_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONObject("customer");
        final String customerId = esbResponse.getString("id");
        connectorProperties.put("customerId", customerId);
        
        final String apiEndpoint = apiEndpointUrl + "/customers?id="+customerId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("customers").getJSONObject(0);
        
        Assert.assertEquals(esbResponse.getString("customer_code"), apiResponse.getString("customer_code"));
        Assert.assertEquals(esbResponse.getString("customer_group_id"), apiResponse.getString("customer_group_id"));
        Assert.assertEquals(esbResponse.getString("customer_group_name"), apiResponse.getString("customer_group_name"));
        Assert.assertEquals(esbResponse.getString("updated_at"), apiResponse.getString("updated_at"));
    }
    
    /**
     * Positive test case for createCustomer method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {createCustomer} integration test with optional parameters.",
    dependsOnMethods = { "testCreateCustomerWithMandatoryParameters" })
    public void testCreateCustomerWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCustomer");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomer_optional.json");

        final JSONObject esbResponse = esbRestResponse.getBody().getJSONObject("customer");
        final String customerIdOpt = esbResponse.getString("id");
        connectorProperties.put("customerIdOpt", customerIdOpt);
        
        final String apiEndpoint = apiEndpointUrl + "/customers?id="+customerIdOpt;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        final JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("customers").getJSONObject(0);
        
        Assert.assertEquals(connectorProperties.getProperty("customerCompany"), apiResponse.getString("company_name"));
        Assert.assertEquals(connectorProperties.getProperty("customerFirstName"), apiResponse.getString("first_name"));
        Assert.assertEquals(connectorProperties.getProperty("customerLastName"), apiResponse.getString("last_name"));
        Assert.assertEquals(connectorProperties.getProperty("customerEmail"), apiResponse.getString("email"));
        Assert.assertEquals(connectorProperties.getProperty("customerMobile"), apiResponse.getString("mobile"));
    }
    
    /**
     * Method name: createCustomer
     * Test scenario: Negative
     * Reason to skip: API is ignoring invalid values for all the parameters while creating a customer.
     */
    
    /**
     * Positive test case for listCustomers method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {listCustomers} integration test with mandatory parameters.",
    dependsOnMethods = { "testCreateCustomerWithOptionalParameters" })
    public void testListCustomersWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCustomers");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomers_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("customers").getJSONObject(0);
        
        final String apiEndpoint = apiEndpointUrl + "/customers";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("customers").getJSONObject(0);
       
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("customers").length(), apiRestResponse.getBody().getJSONArray("customers").length());
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(esbResponse.getString("customer_code"), apiResponse.getString("customer_code"));
        Assert.assertEquals(esbResponse.getString("updated_at"), apiResponse.getString("updated_at"));
        Assert.assertEquals(esbResponse.getString("customer_group_id"), apiResponse.getString("customer_group_id"));
    }
    
    /**
     * Positive test case for listCustomers method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {listCustomers} integration test with optional parameters.", 
    dependsOnMethods = { "testListCustomersWithMandatoryParameters" })
    public void testListCustomersWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCustomers");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomers_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("customers").getJSONObject(0);
        
        final String apiEndpoint = apiEndpointUrl + "/customers?id="+connectorProperties.getProperty("customerId")+"&since="+URLEncoder.encode(connectorProperties.getProperty("customerSinceDate"), "UTF-8") ;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        final JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("customers").getJSONObject(0);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("customers").length(), 1);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("customers").length(),1);
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(esbResponse.getString("customer_code"), apiResponse.getString("customer_code"));
        Assert.assertEquals(esbResponse.getString("updated_at"), apiResponse.getString("updated_at"));
        Assert.assertEquals(esbResponse.getString("customer_group_id"), apiResponse.getString("customer_group_id"));
    }
    
    /**
     * Method name: listCustomers
     * Test scenario: Negative
     * Reason to skip: Cannot test with invalid values, For whatever the invalid values listCustomer method returns an empty array.
     */
    
    /**
     * Method name: updateCustomer 
     * Test scenario: Mandatory  
     * Reason to skip: There are no mandatory parameters in this method.
     */
    
    /**
     * Positive test case for updateCustomer method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {updateCustomer} integration test with optional parameters.",
    dependsOnMethods = { "testListCustomersWithOptionalParameters" })
    public void testUpdateCustomerWithOptionalParameters() throws IOException, JSONException {
    
        final String apiEndpoint = apiEndpointUrl + "/customers?id="+connectorProperties.getProperty("customerIdOpt");
        
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponseBefore = apiRestResponseBefore.getBody().getJSONArray("customers").getJSONObject(0);
        
        esbRequestHeadersMap.put("Action", "urn:updateCustomer");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateCustomer_optional.json");

        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponseAfter = apiRestResponseAfter.getBody().getJSONArray("customers").getJSONObject(0);
        
        Assert.assertNotEquals(apiResponseBefore.getString("mobile"), apiResponseAfter.getString("mobile"));
        Assert.assertNotEquals(apiResponseBefore.getString("first_name"), apiResponseAfter.getString("first_name"));
        Assert.assertNotEquals(apiResponseBefore.getString("last_name"), apiResponseAfter.getString("last_name"));
        Assert.assertNotEquals(apiResponseBefore.getString("company_name"), apiResponseAfter.getString("company_name"));
        Assert.assertNotEquals(apiResponseBefore.getString("email"), apiResponseAfter.getString("email"));
        
        Assert.assertEquals(connectorProperties.getProperty("updatedCustomerFirstName"), apiResponseAfter.getString("first_name"));
        Assert.assertEquals(connectorProperties.getProperty("updatedCustomerLastName"),
                apiResponseAfter.getString("last_name"));
        Assert.assertEquals(connectorProperties.getProperty("updatedCustomerEmail"),
                apiResponseAfter.getString("email"));
        Assert.assertEquals(connectorProperties.getProperty("updatedCustomerCompany"),
                apiResponseAfter.getString("company_name"));
        Assert.assertEquals(connectorProperties.getProperty("updatedCustomerMobile"),
                apiResponseAfter.getString("mobile"));
    }
    
    /**
     * Method name: updateCustomer
     * Test scenario: Negative
     * Reason to skip: There are no negative parameters in this method.
     */
    

    /**
     * Positive test case for createProduct method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {createProduct} integration test with mandatory parameters.",
    dependsOnMethods = { "testUpdateCustomerWithOptionalParameters" })
    public void testCreateProductWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createProduct");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProduct_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONObject("product");
        final String productId = esbResponse.getString("id");
        connectorProperties.put("productId", productId);
        
        final String apiEndpoint = apiEndpointUrl + "/products/"+productId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("products").getJSONObject(0);
        
        Assert.assertEquals(connectorProperties.getProperty("productHandle"), apiResponse.getString("handle"));
        Assert.assertEquals(connectorProperties.getProperty("productSKU"), apiResponse.getString("sku"));
        Assert.assertEquals(connectorProperties.getProperty("productRetailPrice"), apiResponse.getString("price"));
    }
    
    /**
     * Positive test case for createProduct method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {createProduct} integration test with optional parameters.",
    dependsOnMethods = { "testCreateProductWithMandatoryParameters" })
    public void testCreateProductWithOptinalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createProduct");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProduct_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONObject("product");
        final String productIdOpt = esbResponse.getString("id");
        connectorProperties.put("productIdOpt", productIdOpt);
        
        final String apiEndpoint = apiEndpointUrl + "/products/"+productIdOpt;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("products").getJSONObject(0);
        
        Assert.assertEquals(connectorProperties.getProperty("productHandle"), apiResponse.getString("handle"));
        Assert.assertEquals(connectorProperties.getProperty("productSKU"), apiResponse.getString("sku"));
        Assert.assertEquals(connectorProperties.getProperty("productRetailPrice"), apiResponse.getString("price"));
        Assert.assertEquals(connectorProperties.getProperty("productName"), apiResponse.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("productDescription"), apiResponse.getString("description"));
        Assert.assertEquals(connectorProperties.getProperty("productBrandName"), apiResponse.getString("brand_name"));
        Assert.assertEquals(connectorProperties.getProperty("productSupplyPrice"), apiResponse.getString("supply_price"));
    }
    
    
    /**
     * Negative test case for createProduct.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {createProduct} integration test with negative case.",
    dependsOnMethods = { "testCreateProductWithOptinalParameters" })
    public void testCreateProductWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createProduct");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProduct_negative.json");

        final String apiEndPoint = apiEndpointUrl + "/products";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createProduct_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody()
                .getString("error"));
    }
    
    /**
     * Positive test case for getProduct method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {getProduct} integration test with mandatory parameters.",
    dependsOnMethods = { "testCreateProductWithNegativeCase" })
    public void testGetProductWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getProduct");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProduct_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("products").getJSONObject(0);

        final String apiEndpoint = apiEndpointUrl + "/products/"+connectorProperties.getProperty("productId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("products").getJSONObject(0);
        
        Assert.assertEquals(connectorProperties.getProperty("productHandle"), apiResponse.getString("handle"));
        Assert.assertEquals(connectorProperties.getProperty("productSKU"), apiResponse.getString("sku"));
        Assert.assertEquals(connectorProperties.getProperty("productRetailPrice"), apiResponse.getString("price"));
        Assert.assertEquals(esbResponse.getString("track_inventory"), apiResponse.getString("track_inventory"));
        Assert.assertEquals(esbResponse.getString("tax_id"), apiResponse.getString("tax_id"));
    }
    
    
    /**
     * Method name: getProduct
     * Test scenario: Optional
     * Reason to skip: There are no optional parameters to be tested.
     */
    
    /**
     * Method name: getProduct
     * Test scenario: Negative
     * Reason to skip: There are no parameters to be tested for the negative case.
     */
    
    /**
     * Positive test case for listProducts method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {listProducts} integration test with mandatory parameters.",
    dependsOnMethods = { "testGetProductWithMandatoryParameters" })
    public void testListProductsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listProducts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProducts_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("products").getJSONObject(0);

        final String apiEndpoint = apiEndpointUrl + "/products";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("products").getJSONObject(0);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("products").length(), apiRestResponse.getBody().getJSONArray("products").length());
        Assert.assertEquals(esbResponse.getString("price"), apiResponse.getString("price"));
        Assert.assertEquals(esbResponse.getString("handle"), apiResponse.getString("handle"));
        Assert.assertEquals(esbResponse.getString("sku"), apiResponse.getString("sku"));
        Assert.assertEquals(esbResponse.getString("track_inventory"), apiResponse.getString("track_inventory"));
        Assert.assertEquals(esbResponse.getString("tax_id"), apiResponse.getString("tax_id"));
    }
  
    /**
     * Positive test case for listProducts method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {listProducts} integration test with optional parameters.",
    dependsOnMethods = { "testListProductsWithMandatoryParameters"})
    public void testListProductsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listProducts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProducts_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("products").getJSONObject(0);

        final String apiEndpoint = apiEndpointUrl + "/products?order_by="+connectorProperties.getProperty("productOrderBy")+
                "&order_direction="+connectorProperties.getProperty("productOrderDirection")+
                "&active="+connectorProperties.getProperty("productActive");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("products").getJSONObject(0);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("products").length(), apiRestResponse.getBody().getJSONArray("products").length());
        Assert.assertEquals(esbResponse.getString("price"), apiResponse.getString("price"));
        Assert.assertEquals(esbResponse.getString("handle"), apiResponse.getString("handle"));
        Assert.assertEquals(esbResponse.getString("sku"), apiResponse.getString("sku"));
        Assert.assertEquals(esbResponse.getString("track_inventory"), apiResponse.getString("track_inventory"));
        Assert.assertEquals(esbResponse.getString("active"), apiResponse.getString("active"));
    }
    
    /**
     * Method name: listProducts
     * Test scenario: Negative
     * Reason to skip: If one of the parameters are not given or invalid, all the products will be retrieved.
     */
    
    /**
     * Method name: updateProduct 
     * Test scenario: Mandatory  
     * Reason to skip: There are no mandatory parameters in this method.
     */
    
    /**
     * Positive test case for updateProduct method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {updateProduct} integration test with optional parameters.",
    dependsOnMethods = { "testListProductsWithOptionalParameters" })
    public void testUpdateProductWithOptionalParameters() throws IOException, JSONException {
    
        final String apiEndpoint = apiEndpointUrl + "/products?id="+connectorProperties.getProperty("productIdOpt");
        
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponseBefore = apiRestResponseBefore.getBody().getJSONArray("products").getJSONObject(0);
        
        esbRequestHeadersMap.put("Action", "urn:updateProduct");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateProduct_optional.json");

        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponseAfter = apiRestResponseAfter.getBody().getJSONArray("products").getJSONObject(0);
        
        Assert.assertNotEquals(apiResponseBefore.getString("name"), apiResponseAfter.getString("name"));
        Assert.assertNotEquals(apiResponseBefore.getString("description"), apiResponseAfter.getString("description"));
        Assert.assertNotEquals(apiResponseBefore.getString("sku"), apiResponseAfter.getString("sku"));
        Assert.assertNotEquals(apiResponseBefore.getString("brand_name"), apiResponseAfter.getString("brand_name"));
        Assert.assertNotEquals(apiResponseBefore.getString("supply_price"), apiResponseAfter.getString("supply_price"));
        
        Assert.assertEquals(connectorProperties.getProperty("updatedProductSKU"), apiResponseAfter.getString("sku"));
        Assert.assertEquals(connectorProperties.getProperty("updatedProductName"),
                apiResponseAfter.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("updatedProductDescription"),
                apiResponseAfter.getString("description"));
        Assert.assertEquals(connectorProperties.getProperty("updatedProductBrandName"),
                apiResponseAfter.getString("brand_name"));
        Assert.assertEquals(connectorProperties.getProperty("updatedProductSupplyPrice"),
                apiResponseAfter.getString("supply_price"));
    }
    
    /**
     * Method name: updateProduct
     * Test scenario: Negative
     * Reason to skip: There are no negative parameters in this method.
     */
    
    /**
     * Positive test case for listRegisters method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {listRegisters} integration test with mandatory parameters.",
    dependsOnMethods = { "testUpdateProductWithOptionalParameters" })
    public void testListRegistersWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listRegisters");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listRegisters_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("registers").getJSONObject(0);
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("registers");

        final String apiEndpoint = apiEndpointUrl + "/registers";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("registers").getJSONObject(0);
        
        for (int i = 0; i < esbResponseArray.length(); i++) {
            if(connectorProperties.getProperty("registerName").equals(esbResponseArray.getJSONObject(i).getString("name"))) {
                final String registerId = esbResponseArray.getJSONObject(i).getString("id");
                connectorProperties.put("registerId", registerId);
            }
           
        }

        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(esbResponse.getString("receipt_header"), apiResponse.getString("receipt_header"));
        Assert.assertEquals(esbResponse.getString("print_receipt"), apiResponse.getString("print_receipt"));
        Assert.assertEquals(esbResponse.getString("outlet_id"), apiResponse.getString("outlet_id"));
        Assert.assertEquals(esbResponse.getString("name"), apiResponse.getString("name"));
    }
    
    /**
     * Method name: listRegister
     * Test scenario: Optional
     * Reason to skip: There are no optional parameters to be tested.
     */
    
    /**
     * Method name: listRegister
     * Test scenario: Negative
     * Reason to skip: There are no negative parameters to be tested.
     */
    
    /**
     * Positive test case for openRegister method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {openRegister} integration test with mandatory parameters.",
    dependsOnMethods = { "testListRegistersWithMandatoryParameters" })
    public void testOpenRegisterWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:openRegister");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_openRegister_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONObject("register");

        Assert.assertNotNull(esbResponse.getString("register_open_time"));
        Assert.assertEquals(esbResponse.getString("register_close_time"), "");
    }
    
    /**
     * Method name: openRegister
     * Test scenario: Optional
     * Reason to skip: There are no optional parameters to be tested.
     */
    
    /**
     * Negative test case for openRegister method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {openRegister} integration test with negative case.",
    dependsOnMethods = { "testOpenRegisterWithMandatoryParameters" })
    public void testOpenRegisterWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:openRegister");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_openRegister_mandatory.json");

        final String apiEndpoint = apiEndpointUrl + "/registers/"+connectorProperties.getProperty("registerId")+"/open";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody()
                .getString("error"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody()
                .getString("error"));
    }
    
    
    /**
     * Positive test case for closeRegister method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {closeRegister} integration test with mandatory parameters.",
    dependsOnMethods = { "testOpenRegisterWithNegativeCase" })
    public void testcloseRegisterWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:closeRegister");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_closeRegister_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONObject("register");
        
        Assert.assertNotNull(esbResponse.getString("register_open_time"));
        Assert.assertNotNull(esbResponse.getString("register_close_time"));
    }
    
    /**
     * Method name: closeRegister
     * Test scenario: Optional
     * Reason to skip: There are no optional parameters to be tested.
     */
    
    /**
     * Negative test case for closeRegister method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {closeRegister} integration test with negative case.",
    dependsOnMethods = { "testcloseRegisterWithMandatoryParameters" })
    public void testCloseRegisterWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:closeRegister");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_openRegister_mandatory.json");

        final String apiEndpoint = apiEndpointUrl + "/registers/"+connectorProperties.getProperty("registerId")+"/close";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody()
                .getString("error"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody()
                .getString("error"));
    }
    
    /**
     * Positive test case for createRegisterSale method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {createRegisterSale} integration test with mandatory parameters.",
    dependsOnMethods = { "testCloseRegisterWithNegativeCase" })
    public void testCreateRegisterSaleWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createRegisterSale");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRegisterSale_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONObject("register_sale");
        final String registerSaleId = esbResponse.getString("id");
        connectorProperties.put("registerSaleId", registerSaleId);
        
        final String apiEndpoint = apiEndpointUrl + "/register_sales/"+registerSaleId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("register_sales").getJSONObject(0);
        
        Assert.assertEquals(esbResponse.getString("register_id"), apiResponse.getString("register_id"));
        Assert.assertEquals(esbResponse.getString("market_id"), apiResponse.getString("market_id"));
        Assert.assertEquals(esbResponse.getString("user_id"), apiResponse.getString("user_id"));
        Assert.assertEquals(esbResponse.getString("customer_id"), apiResponse.getString("customer_id"));
        Assert.assertEquals(esbResponse.getString("created_at"), apiResponse.getString("created_at"));
    }
    
    /**
     * Positive test case for createRegisterSale method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {createRegisterSale} integration test with optional parameters.",
    dependsOnMethods = { "testCreateRegisterSaleWithMandatoryParameters" })
    public void testCreateRegisterSaleWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createRegisterSale");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRegisterSale_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONObject("register_sale");
        final String registerSaleIdOpt = esbResponse.getString("id");
        connectorProperties.put("registerSaleIdOpt", registerSaleIdOpt);
        
        final String apiEndpoint = apiEndpointUrl + "/register_sales/"+registerSaleIdOpt;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("register_sales").getJSONObject(0);
        
        Assert.assertEquals(connectorProperties.getProperty("saleDate"), apiResponse.getString("sale_date"));
        Assert.assertEquals(connectorProperties.getProperty("saleNote"), apiResponse.getString("note"));
        Assert.assertEquals(connectorProperties.getProperty("salePrice"), apiResponse.getString("total_price"));
        Assert.assertEquals(connectorProperties.getProperty("registerId"), apiResponse.getString("register_id"));
        Assert.assertEquals(connectorProperties.getProperty("customerId"), apiResponse.getString("customer_id"));
    }
    
    /**
     * Negative test case for createRegisterSale.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {createRegisterSale} integration test with negative case.",
    dependsOnMethods = { "testCreateRegisterSaleWithOptionalParameters" })
    public void testCreateRegisterSaleWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createRegisterSale");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRegisterSale_negative.json");
        
        final String apiEndPoint = apiEndpointUrl + "/register_sales";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createRegisterSales_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody()
                .getString("error"));
    }
    
    /**
     * Positive test case for getRegisterSale method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {getRegisterSale} integration test with mandatory parameters.",
    dependsOnMethods = { "testCreateRegisterSaleWithNegativeCase" })
    public void testGetRegisterSaleWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getRegisterSale");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRegisterSale_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("register_sales").getJSONObject(0);

        final String apiEndpoint = apiEndpointUrl + "/register_sales/"+connectorProperties.getProperty("registerSaleId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("register_sales").getJSONObject(0);
        
        Assert.assertEquals(esbResponse.getString("register_id"), apiResponse.getString("register_id"));
        Assert.assertEquals(esbResponse.getString("market_id"), apiResponse.getString("market_id"));
        Assert.assertEquals(esbResponse.getString("user_id"), apiResponse.getString("user_id"));
        Assert.assertEquals(esbResponse.getString("customer_id"), apiResponse.getString("customer_id"));
        Assert.assertEquals(esbResponse.getString("created_at"), apiResponse.getString("created_at"));
    }
    
    /**
     * Method name: getRegisterSale
     * Test scenario: Optional
     * Reason to skip: There are no optional parameters to be tested.
     */
    
    /**
     * Method name: getRegisterSale
     * Test scenario: Negative
     * Reason to skip: If an invalid ID is passed it will return an empty array.
     */
    
    /**
     * Positive test case for listOutlets method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {listOutlets} integration test with mandatory parameters.",
    dependsOnMethods = { "testGetRegisterSaleWithMandatoryParameters" })
    public void testListOtletsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listOutlets");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listOutlets_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("outlets").getJSONObject(0);
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("outlets");
        final String outletName = "Main Outlet";
        for (int i = 0; i < esbResponseArray.length(); i++) {
            if(outletName.equals(esbResponseArray.getJSONObject(i).getString("name"))) {
                final String outletId = esbResponseArray.getJSONObject(i).getString("id");
                connectorProperties.put("consignmentOutletId", outletId);
            }
           
        }

        final String apiEndpoint = apiEndpointUrl + "/outlets";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("outlets").getJSONObject(0);
        
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(esbResponse.getString("retailer_id"), apiResponse.getString("retailer_id"));
        Assert.assertEquals(esbResponse.getString("name"), apiResponse.getString("name"));
        Assert.assertEquals(esbResponse.getString("time_zone"), apiResponse.getString("time_zone"));
        Assert.assertEquals(esbResponse.getString("tax_id"), apiResponse.getString("tax_id"));
    }
    
    /**
     * Method name: listOutlets 
     * Test scenario: Optional 
     * Reason to skip: There are no optional parameters to be tested.
     */
    
    /**
     * Method name: listOutlets 
     * Test scenario: Negative 
     * Reason to skip: There are no negative parameters in this method.
     */
    
    /**
     * Positive test case for listRegisterSales method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {listRegisterSales} integration test with mandatory parameters.",
    dependsOnMethods = { "testListOtletsWithMandatoryParameters" })
    public void testListRegisterSalesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listRegisterSales");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listRegisterSales_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("register_sales").getJSONObject(0);

        final String apiEndpoint = apiEndpointUrl + "/register_sales";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("register_sales").getJSONObject(0);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("register_sales").length(), apiRestResponse.getBody().getJSONArray("register_sales").length());
        Assert.assertEquals(esbResponse.getString("register_id"), apiResponse.getString("register_id"));
        Assert.assertEquals(esbResponse.getString("market_id"), apiResponse.getString("market_id"));
        Assert.assertEquals(esbResponse.getString("user_id"), apiResponse.getString("user_id"));
        Assert.assertEquals(esbResponse.getString("customer_id"), apiResponse.getString("customer_id"));
        Assert.assertEquals(esbResponse.getString("created_at"), apiResponse.getString("created_at"));
    }
    
    /**
     * Positive test case for listRegisterSales method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {listRegisterSales} integration test with optional parameters.",
    dependsOnMethods = { "testListRegisterSalesWithMandatoryParameters" })
    public void testListRegisterSalesWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listRegisterSales");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listRegisterSales_optional.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("register_sales").getJSONObject(0);

        final String apiEndpoint = apiEndpointUrl + "/register_sales?status[]="+connectorProperties.getProperty("saleStatus")+
                "&since="+URLEncoder.encode(connectorProperties.getProperty("saleSinceDate"), "UTF-8")+
                "&outlet_id"+connectorProperties.getProperty("consignmentOutletId");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("register_sales").getJSONObject(0);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("register_sales").length(), apiRestResponse.getBody().getJSONArray("register_sales").length());
        Assert.assertEquals(esbResponse.getString("register_id"), apiResponse.getString("register_id"));
        Assert.assertEquals(esbResponse.getString("market_id"), apiResponse.getString("market_id"));
        Assert.assertEquals(esbResponse.getString("customer_id"), apiResponse.getString("customer_id"));
        Assert.assertEquals(esbResponse.getString("status"), apiResponse.getString("status"));
    }
    
    /**
     * Negative test case for listRegisterSales.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {listRegisterSales} integration test with negative case.",
    dependsOnMethods = { "testListRegisterSalesWithOptionalParameters" })
    public void testListRegisterSalesWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listRegisterSales");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listRegisterSales_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/register_sales?since=abc";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody()
                .getString("error"));
    }
    
    /**
     * Positive test case for listPaymentTypes method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {listPaymentTypes} integration test with mandatory parameters.",
    dependsOnMethods = { "testListRegisterSalesWithNegativeCase" })
    public void testListPaymentTypesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listPaymentTypes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPaymentTypes_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("payment_types").getJSONObject(0);

        final String apiEndpoint = apiEndpointUrl + "/payment_types";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("payment_types").getJSONObject(0);
        
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(esbResponse.getString("name"), apiResponse.getString("name"));
        Assert.assertEquals(esbResponse.getString("payment_type_id"), apiResponse.getString("payment_type_id"));
    }
    
    /**
     * Method name: listPaymentTypes 
     * Test scenario: Optional 
     * Reason to skip: There are no optional parameters in this method.
     */
    
    /**
     * Method name: listPaymentTypes 
     * Test scenario: Negative 
     * Reason to skip: There are no negative parameters in this method.
     */
    
    /**
     * Positive test case for createSupplier method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {createSupplier} integration test with mandatory parameters.",
    dependsOnMethods = { "testListPaymentTypesWithMandatoryParameters" })
    public void testCreateSupplierWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createSupplier");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createSupplier_mandatory.json");

        final String supplierId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("supplierId", supplierId);
        
        final String apiEndpoint = apiEndpointUrl + "/supplier/" + supplierId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("supplierNameMandatory"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("retailer_id"), apiRestResponse.getBody().getString("retailer_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("first_name"), apiRestResponse.getBody().getJSONObject("contact").getString("first_name"));
    }
    
    /**
     * Positive test case for createSupplier method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {createSupplier} integration test with optional parameters.",
    dependsOnMethods = { "testCreateSupplierWithMandatoryParameters" })
    public void testCreateSupplierWithOptionalParameters() throws IOException, JSONException {
       
       esbRequestHeadersMap.put("Action", "urn:createSupplier");
       RestResponse<JSONObject> esbRestResponse =
               sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createSupplier_optional.json");

       final String supplierIdOptional = esbRestResponse.getBody().getString("id");
       connectorProperties.put("supplierIdOptional", supplierIdOptional);
       
       final String apiEndpoint = apiEndpointUrl + "/supplier/" + supplierIdOptional;
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
       
       Assert.assertEquals(connectorProperties.getProperty("supplierNameOptional"), apiRestResponse.getBody().getString("name"));
       Assert.assertEquals(connectorProperties.getProperty("supplierDescription"), apiRestResponse.getBody().getString("description"));
       Assert.assertEquals(connectorProperties.getProperty("supplierCompanyName"), apiRestResponse.getBody().getJSONObject("contact").getString("company_name"));
       Assert.assertEquals(connectorProperties.getProperty("contactFirstName"), apiRestResponse.getBody().getJSONObject("contact").getString("first_name"));
       Assert.assertEquals(connectorProperties.getProperty("contactLastName"), apiRestResponse.getBody().getJSONObject("contact").getString("last_name"));
       Assert.assertEquals(connectorProperties.getProperty("contactPhone"), apiRestResponse.getBody().getJSONObject("contact").getString("phone"));
       Assert.assertEquals(connectorProperties.getProperty("contactMobile"), apiRestResponse.getBody().getJSONObject("contact").getString("mobile"));
       
    }
    
    /**
     * Method name: createSupplier 
     * Test scenario: Negative 
     * Reason to skip: There are no negative parameters in this method.
     */
    
    /**
     * Positive test case for getSupplier method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {getSupplier} integration test with mandatory parameters.",
    dependsOnMethods = { "testCreateSupplierWithOptionalParameters" })
    public void testGetSupplierWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getSupplier");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSupplier_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/supplier/" + connectorProperties.getProperty("supplierIdOptional");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("description"), apiRestResponse.getBody().getString("description"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("company_name"), apiRestResponse.getBody().getJSONObject("contact").getString("company_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("last_name"), apiRestResponse.getBody().getJSONObject("contact").getString("last_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("phone"), apiRestResponse.getBody().getJSONObject("contact").getString("phone"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("contact").getString("mobile"), apiRestResponse.getBody().getJSONObject("contact").getString("mobile"));
    }
    
    /**
     * Method name: getSupplier 
     * Test scenario: Optional 
     * Reason to skip: There are no optional parameters in this method.
     */
    
    /**
     * Negative test case for getSupplier method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "beetrack {getSupplier} integration test with negative case.",
    dependsOnMethods = { "testGetSupplierWithMandatoryParameters" })
    public void testGetSupplierWithNegativeCase() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:getSupplier");
       RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSupplier_negative.json");
       
       final String apiEndpoint = apiEndpointUrl + "/supplier/invalid";
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
       Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listSuppliers method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {listSuppliers} integration test with mandatory parameters.",
    dependsOnMethods = { "testGetSupplierWithNegativeCase" })
    public void testListSuppliersWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listSuppliers");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSuppliers_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("suppliers").getJSONObject(0);
        
        final String apiEndpoint = apiEndpointUrl + "/supplier";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("suppliers").getJSONObject(0);
        
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(esbResponse.getString("name"), apiResponse.getString("name"));
        Assert.assertEquals(esbResponse.getString("retailer_id"), apiResponse.getString("retailer_id"));
        Assert.assertEquals(esbResponse.getJSONObject("contact").getString("email"), apiResponse.getJSONObject("contact").getString("email"));
        Assert.assertEquals(esbResponse.getJSONObject("contact").getString("physical_country_id"), apiResponse.getJSONObject("contact").getString("physical_country_id"));
    }
    
    /**
     * Method name: listSuppliers 
     * Test scenario: Optional 
     * Reason to skip: There are no optional parameters in this method.
     */
    
    /**
     * Method name: listSuppliers 
     * Test scenario: Negative 
     * Reason to skip: There are no parameters to make negative in this method.
     */
    
    /**
     * Positive test case for createConsignment method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {createConsignment} integration test with mandatory parameters.",
    dependsOnMethods = { "testListSuppliersWithMandatoryParameters" })
    public void testCreateConsignmentWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createConsignment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createConsignment_mandatory.json");

        final String consignmentId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("consignmentId", consignmentId);
        
        final String apiEndpoint = apiEndpointUrl + "/consignment/" + consignmentId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("consignment_date"), apiRestResponse.getBody().getString("consignment_date"));
        Assert.assertEquals(connectorProperties.getProperty("consignmentOutletId"), apiRestResponse.getBody().getString("outlet_id"));
        Assert.assertEquals(connectorProperties.getProperty("consignmentType"), apiRestResponse.getBody().getString("type"));
        Assert.assertEquals(esbRestResponse.getBody().getString("retailer_id"), apiRestResponse.getBody().getString("retailer_id"));
    }
    
    /**
     * Positive test case for createConsignment method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {createConsignment} integration test with optional parameters.",
    dependsOnMethods = { "testCreateConsignmentWithMandatoryParameters" })
    public void testCreateConsignmentWithOptionalParameters() throws IOException, JSONException {
       
       esbRequestHeadersMap.put("Action", "urn:createConsignment");
       RestResponse<JSONObject> esbRestResponse =
               sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createConsignment_optional.json");

       final String consignmentIdOptional = esbRestResponse.getBody().getString("id");
       connectorProperties.put("consignmentIdOptional", consignmentIdOptional);
       
       final String apiEndpoint = apiEndpointUrl + "/consignment/" + consignmentIdOptional;
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
       

       Assert.assertEquals(connectorProperties.getProperty("consignmentName"), apiRestResponse.getBody().getString("name"));
       Assert.assertEquals(connectorProperties.getProperty("consignmentDate"), apiRestResponse.getBody().getString("consignment_date"));
       Assert.assertEquals(connectorProperties.getProperty("consignmentDueAt"), apiRestResponse.getBody().getString("due_at"));
       Assert.assertEquals(connectorProperties.getProperty("consignmentStatus"), apiRestResponse.getBody().getString("status"));
       Assert.assertEquals(esbRestResponse.getBody().getString("retailer_id"), apiRestResponse.getBody().getString("retailer_id"));
       
    }
    
    /**
     * Negative test case for createConsignment method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "beetrack {createConsignment} integration test with negative case.", 
    dependsOnMethods = { "testCreateConsignmentWithOptionalParameters" })
    public void testCreateConsignmentWithNegativeCase() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:createConsignment");
       RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createConsignment_negative.json");
       
       final String apiEndpoint = apiEndpointUrl + "/consignment";
       RestResponse<JSONObject> apiRestResponse =
             sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createConsignment_negative.json");
       
       Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
       Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
       Assert.assertEquals(esbRestResponse.getBody().getString("details"), apiRestResponse.getBody().getString("details"));
       
    }
    
    /**
     * Positive test case for listConsignments method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {listConsignments} integration test with mandatory parameters.",
    dependsOnMethods = { "testCreateConsignmentWithNegativeCase" })
    public void testListConsignmentsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listConsignments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listConsignments_mandatory.json");
        
        final JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("consignments").getJSONObject(0);
        
        final String apiEndpoint = apiEndpointUrl + "/consignment";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        final JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("consignments").getJSONObject(0);
        
        Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
        Assert.assertEquals(esbResponse.getString("name"), apiResponse.getString("name"));
        Assert.assertEquals(esbResponse.getString("type"), apiResponse.getString("type"));
        Assert.assertEquals(esbResponse.getString("outlet_id"), apiResponse.getString("outlet_id"));
        Assert.assertEquals(esbResponse.getString("consignment_date"), apiResponse.getString("consignment_date"));
    }
    
    /**
     * Method name: listSuppliers 
     * Test scenario: Optional 
     * Reason to skip: There are no optional parameters in this method.
     */
    
    /**
     * Method name: listSuppliers 
     * Test scenario: Negative 
     * Reason to skip: There are no parameters to make negative in this method.
     */
    
    /**
     * Method name: updateConsignment 
     * Test scenario: Mandatory  
     * Reason to skip: There are no mandatory parameters in this method.
     */
    
    /**
     * Positive test case for updateConsignment method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {updateConsignment} integration test with optional parameters.",
    dependsOnMethods = { "testListConsignmentsWithMandatoryParameters" })
    public void testUpdateConsignmentWithOptionalParameters() throws IOException, JSONException {
       
       esbRequestHeadersMap.put("Action", "urn:updateConsignment");
       
       final String apiEndpoint = apiEndpointUrl + "/consignment/" + connectorProperties.getProperty("consignmentId");
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
       
       RestResponse<JSONObject> esbRestResponse =
               sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateConsignment_optional.json");

       final String consignmentIdOptional = esbRestResponse.getBody().getString("id");
       connectorProperties.put("consignmentIdOptional", consignmentIdOptional);
       
       RestResponse<JSONObject> apiRestResponse2 = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);

       Assert.assertNotEquals(apiRestResponse.getBody().getString("name"), apiRestResponse2.getBody().getString("name"));
       Assert.assertNotEquals(apiRestResponse.getBody().getString("consignment_date"), apiRestResponse2.getBody().getString("consignment_date"));
       Assert.assertNotEquals(apiRestResponse.getBody().getString("due_at"), apiRestResponse2.getBody().getString("due_at"));
       Assert.assertEquals(esbRestResponse.getBody().getString("retailer_id"), apiRestResponse.getBody().getString("retailer_id"));
       
    }
    
    /**
     * Negative test case for updateConsignment method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "beetrack {updateConsignment} integration test with negative case.", 
    dependsOnMethods = { "testUpdateConsignmentWithOptionalParameters" })
    public void testUpdateConsignmentWithNegativeCase() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:updateConsignment");
       RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateConsignment_negative.json");
       
       final String apiEndpoint = apiEndpointUrl + "/consignment/"+ connectorProperties.getProperty("consignmentId");
       RestResponse<JSONObject> apiRestResponse =
             sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_updateConsignment_negative.json");
       
       Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
       Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
       Assert.assertEquals(esbRestResponse.getBody().getString("details"), apiRestResponse.getBody().getString("details"));
       
    }
    
    /**
     * Positive test case for createConsignmentProduct method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {createConsignmentProduct} integration test with mandatory parameters.",
    dependsOnMethods = { "testUpdateConsignmentWithNegativeCase" })
    public void testCreateConsignmentProductWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createConsignmentProduct");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createConsignmentProduct_mandatory.json");

        final String consignmentProductId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("consignmentProductId", consignmentProductId);
        
        final String apiEndpoint = apiEndpointUrl + "/consignment_product/" + consignmentProductId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("productId"), apiRestResponse.getBody().getString("product_id"));
        Assert.assertEquals(connectorProperties.getProperty("consignmentId"), apiRestResponse.getBody().getString("consignment_id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("sequence_number"), apiRestResponse.getBody().getString("sequence_number"));
        
    }
    
    /**
     * Positive test case for createConsignmentProduct method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {createConsignmentProduct} integration test with optional parameters.",
    dependsOnMethods = { "testCreateConsignmentProductWithMandatoryParameters" })
    public void testCreateConsignmentProductWithOptionalParameters() throws IOException, JSONException {
       
       esbRequestHeadersMap.put("Action", "urn:createConsignmentProduct");
       RestResponse<JSONObject> esbRestResponse =
               sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createConsignmentProduct_optional.json");

       final String consignmentIdOptional = esbRestResponse.getBody().getString("id");
       connectorProperties.put("consignmentIdOptional", consignmentIdOptional);

       final String consignmentProductIdOptional = esbRestResponse.getBody().getString("id");
       
       final String apiEndpoint = apiEndpointUrl + "/consignment_product/" + consignmentProductIdOptional;
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
       
       Assert.assertEquals(connectorProperties.getProperty("productId"), apiRestResponse.getBody().getString("product_id"));
       Assert.assertEquals(connectorProperties.getProperty("consignmentId"), apiRestResponse.getBody().getString("consignment_id"));
       Assert.assertEquals(connectorProperties.getProperty("sequenceNumber"), apiRestResponse.getBody().getString("sequence_number"));
       Assert.assertEquals(connectorProperties.getProperty("cost"), apiRestResponse.getBody().getString("cost"));;
       
    }
    
    /**
     * Negative test case for createConsignmentProduct method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "beetrack {createConsignmentProduct} integration test with negative case.", 
    dependsOnMethods = { "testCreateConsignmentProductWithOptionalParameters" })
    public void testCreateConsignmentProductWithNegativeCase() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:createConsignmentProduct");
       RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createConsignmentProduct_negative.json");
       
       final String apiEndpoint = apiEndpointUrl + "/consignment_product";
       RestResponse<JSONObject> apiRestResponse =
             sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createConsignmentProduct_negative.json");
       
       Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
       Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
       Assert.assertEquals(esbRestResponse.getBody().getString("details"), apiRestResponse.getBody().getString("details"));
       
    }
    
    /**
     * Method name: listConsignmentProducts 
     * Test scenario: Mandatory  
     * Reason to skip: There are no mandatory parameters in this method.
     */
    
    /**
     * Positive test case for listConsignmentProducts method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "vend {listConsignmentProducts} integration test with optional parameters.",
    dependsOnMethods = { "testCreateConsignmentProductWithNegativeCase" })
    public void testListConsignmentProductsWithOptionalParameters() throws IOException, JSONException {
       
       esbRequestHeadersMap.put("Action", "urn:listConsignmentProducts");
       
       RestResponse<JSONObject> esbRestResponse =
               sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listConsignmentProducts_optional.json");
       
       final JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("consignment_products").getJSONObject(0);

       final String apiEndpoint = apiEndpointUrl + "/consignment_product?product_id=" + connectorProperties.getProperty("productId");
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
       
       final JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("consignment_products").getJSONObject(0);

       Assert.assertEquals(esbResponse.getString("sequence_number"), apiResponse.getString("sequence_number"));
       Assert.assertEquals(esbResponse.getString("consignment_id"), apiResponse.getString("consignment_id"));
       Assert.assertEquals(esbResponse.getString("id"), apiResponse.getString("id"));
       Assert.assertEquals(esbResponse.getString("count"), apiResponse.getString("count"));
       Assert.assertEquals(esbResponse.getString("product_id"), apiResponse.getString("product_id"));
       
    }
    
    /**
     * Negative test case for listConsignmentProducts method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "beetrack {listConsignmentProducts} integration test with negative case.", 
    dependsOnMethods = { "testListConsignmentProductsWithOptionalParameters" })
    public void testListConsignmentProductsWithNegativeCase() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:listConsignmentProducts");
       RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listConsignmentProducts_negative.json");
       
       final String apiEndpoint = apiEndpointUrl + "/consignment_product?product_id=invalid";
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
       
       Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
       Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
       Assert.assertEquals(esbRestResponse.getBody().getString("details"), apiRestResponse.getBody().getString("details"));
       
    }
    
  
}
