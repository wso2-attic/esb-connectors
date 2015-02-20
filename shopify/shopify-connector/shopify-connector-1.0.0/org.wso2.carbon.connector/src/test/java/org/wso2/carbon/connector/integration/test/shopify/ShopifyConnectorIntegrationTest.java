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

package org.wso2.carbon.connector.integration.test.shopify;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class ShopifyConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private String productId;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("shopify-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("X-Shopify-Access-Token", connectorProperties.getProperty("accessToken"));
    }
    
    /**
     * Positive test case for createCustomer method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "Shopify {createCustomer} integration test with mandatory parameters.")
    public void testCreateCustomerWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createCustomer");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomer_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        String customerId = esbRestResponse.getBody().getJSONObject("customer").getString("id");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/admin/customers/" + customerId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("customerEmail1"),
                apiRestResponse.getBody().getJSONObject("customer").getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("customer").getString("created_at"),
                apiRestResponse.getBody().getJSONObject("customer").getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("customer").getString("state"), apiRestResponse
                .getBody().getJSONObject("customer").getString("state"));
    }
    
    /**
     * Positive test case for createCustomer method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCustomerWithMandatoryParameters" }, description = "Shopify {createCustomer} integration test with optional parameters.")
    public void testCreateCustomerWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createCustomer");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomer_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        String customerId = esbRestResponse.getBody().getJSONObject("customer").getString("id");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/admin/customers/" + customerId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("customerEmail2"),
                apiRestResponse.getBody().getJSONObject("customer").getString("email"));
        Assert.assertEquals(connectorProperties.getProperty("customerFirstName"), apiRestResponse.getBody()
                .getJSONObject("customer").getString("first_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("customer").getString("created_at"),
                apiRestResponse.getBody().getJSONObject("customer").getString("created_at"));
    }
    
    /**
     * Negative test case for createCustomer method. Negative case was achieved by trying to create a customer
     * with the same email again.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCustomerWithOptionalParameters" }, description = "Shopify {createCustomer} integration test with negative case.")
    public void testCreateCustomerNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createCustomer");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomer_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/admin/customers.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createCustomer_negative.json");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").getString("email"), apiRestResponse
                .getBody().getJSONObject("errors").getString("email"));
    }
    
    /**
     * Positive test case for listCustomers method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCustomerNegativeCase" }, description = "Shopify {listCustomers} integration test with mandatory parameters.")
    public void testlistCustomersWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listCustomers");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomers_mandatory.json");
        
        JSONArray esbCustomerArray = esbRestResponse.getBody().getJSONArray("customers");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/admin/customers.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiCustomerArray = apiRestResponse.getBody().getJSONArray("customers");
        
        Assert.assertEquals(esbCustomerArray.length(), apiCustomerArray.length());
        
        if (esbCustomerArray.length() > 0 && apiCustomerArray.length() > 0) {
            
            Assert.assertEquals(esbCustomerArray.getJSONObject(0).getString("email"), apiCustomerArray.getJSONObject(0)
                    .getString("email"));
            Assert.assertEquals(esbCustomerArray.getJSONObject(0).getString("id"), apiCustomerArray.getJSONObject(0)
                    .getString("id"));
        } else {
            Assert.assertTrue(false);
        }
        
    }
    
    /**
     * Positive test case for listCustomers method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testlistCustomersWithMandatoryParameters" }, description = "Shopify {listCustomers} integration test with optional parameters.")
    public void testlistCustomersWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listCustomers");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomers_optional.json");
        
        JSONArray esbCustomerArray = esbRestResponse.getBody().getJSONArray("customers");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/admin/customers.json?fields=email,last_name";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiCustomerArray = apiRestResponse.getBody().getJSONArray("customers");
        
        Assert.assertEquals(esbCustomerArray.length(), apiCustomerArray.length());
        
        if (esbCustomerArray.length() > 0 && apiCustomerArray.length() > 0) {
            
            Assert.assertEquals(esbCustomerArray.getJSONObject(0).getString("email"), apiCustomerArray.getJSONObject(0)
                    .getString("email"));
            Assert.assertEquals(esbCustomerArray.getJSONObject(0).getString("last_name"), apiCustomerArray
                    .getJSONObject(0).getString("last_name"));
            Assert.assertFalse(esbCustomerArray.getJSONObject(0).has("id"));
            Assert.assertFalse(apiCustomerArray.getJSONObject(0).has("id"));
        } else {
            Assert.assertTrue(false);
        }
        
    }
    
    /**
     * Negative test case for listCustomers method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testlistCustomersWithOptionalParameters" }, description = "Shopify {listCustomers} integration test with negative case.")
    public void testlistCustomersNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listCustomers");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomers_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/admin/customers.json?limit=350";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("errors"), apiRestResponse.getBody()
                .getString("errors"));
        
    }
    
    /**
     * Positive test case for createProduct method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testlistCustomersNegativeCase" }, description = "Shopify {createProduct} integration test with mandatory parameters.")
    public void testCreateProductWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createProduct");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProduct_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        productId = esbRestResponse.getBody().getJSONObject("product").getString("id");
        connectorProperties.setProperty("productId", productId);
        
        String esbProductCreatedAt = esbRestResponse.getBody().getJSONObject("product").getString("created_at");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/admin/products/" + productId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String apiProductCreatedAt = apiRestResponse.getBody().getJSONObject("product").getString("created_at");
        
        Assert.assertEquals(esbProductCreatedAt, apiProductCreatedAt);
        
        String apiProductTitle = apiRestResponse.getBody().getJSONObject("product").getString("title");
        
        Assert.assertEquals(connectorProperties.getProperty("productTitle"), apiProductTitle);
        
        String apiProductType = apiRestResponse.getBody().getJSONObject("product").getString("product_type");
        
        Assert.assertEquals(connectorProperties.getProperty("productType"), apiProductType);
        
    }
    
    /**
     * Positive test case for createProduct method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProductWithMandatoryParameters" }, description = "Shopify {createProduct} integration test with optional parameters.")
    public void testCreateProductWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createProduct");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProduct_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        String esbProductId = esbRestResponse.getBody().getJSONObject("product").getString("id");
        
        connectorProperties.setProperty("productId", esbProductId);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/admin/products/" + esbProductId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String apiVendor = apiRestResponse.getBody().getJSONObject("product").getString("vendor");
        
        Assert.assertEquals(connectorProperties.getProperty("vendor"), apiVendor);
        
        String apiDescription = apiRestResponse.getBody().getJSONObject("product").getString("tags");
        
        Assert.assertEquals(connectorProperties.getProperty("tags"), apiDescription);
        
        String esbPublishedScope = esbRestResponse.getBody().getJSONObject("product").getString("published_scope");
        String apiPublishedScope = apiRestResponse.getBody().getJSONObject("product").getString("published_scope");
        
        Assert.assertEquals(esbPublishedScope, apiPublishedScope);
        
    }
    
    /**
     * Negative test case for createProduct method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProductWithOptionalParameters" }, description = "Shopify {createProduct} integration test for negative case.")
    public void testCreateProductNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createProduct");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProduct_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/admin/products.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createProduct_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
        String esbErrorMessage = esbRestResponse.getBody().getJSONObject("errors").getJSONArray("title").getString(0);
        String apiErrorMessage = apiRestResponse.getBody().getJSONObject("errors").getJSONArray("title").getString(0);
        
        Assert.assertEquals(esbErrorMessage, apiErrorMessage);
        
    }
    
    /**
     * Positive test case for listProducts method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProductNegativeCase" }, description = "Shopify {listProducts} integration test with mandatory parameters.")
    public void testListProductsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listProducts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProducts_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        int esbProductCount = esbRestResponse.getBody().getJSONArray("products").length();
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/admin/products.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        int apiProductCount = apiRestResponse.getBody().getJSONArray("products").length();
        
        Assert.assertEquals(esbProductCount, apiProductCount);
        
        String esbProductId = esbRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("id");
        String apiProductId = apiRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("id");
        
        Assert.assertEquals(esbProductId, apiProductId);
        
        String esbProductType =
                esbRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("product_type");
        String apiProductType =
                apiRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("product_type");
        
        Assert.assertEquals(esbProductType, apiProductType);
        
    }
    
    /**
     * Positive test case for listProducts method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListProductsWithMandatoryParameters" }, description = "Shopify {listProducts} integration test with optional parameters.")
    public void testListProductsWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listProducts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProducts_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        int esbProductCount = esbRestResponse.getBody().getJSONArray("products").length();
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/admin/products.json?vendor="
                        + connectorProperties.getProperty("vendor");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        int apiProductCount = apiRestResponse.getBody().getJSONArray("products").length();
        
        Assert.assertEquals(esbProductCount, apiProductCount);
        
        String esbProductId = esbRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("id");
        String apiProductId = apiRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("id");
        
        Assert.assertEquals(esbProductId, apiProductId);
        
        String esbVendor = esbRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("vendor");
        String apiVendor = apiRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("vendor");
        
        Assert.assertEquals(esbVendor, apiVendor);
        
    }
    
    /**
     * Negative test case for listProducts method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListProductsWithOptionalParameters" }, description = "Shopify {listProducts} integration test for negative case.")
    public void testListProductsNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listProducts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProducts_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/admin/products.json?limit=300";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
        String esbErrorMessage = esbRestResponse.getBody().getString("errors");
        String apiErrorMessage = apiRestResponse.getBody().getString("errors");
        
        Assert.assertEquals(esbErrorMessage, apiErrorMessage);
        
    }
    
    /**
     * Positive test case for createProductVariant method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListProductsNegativeCase" }, description = "Shopify {createProductVariant} integration test with mandatory parameters.")
    public void testCreateProductVariantWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createProductVariant");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProductVariant_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        String variantId = esbRestResponse.getBody().getJSONObject("variant").getString("id");
        connectorProperties.setProperty("variantId", variantId);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/admin/variants/" + variantId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String esbVariantCreatedAt = esbRestResponse.getBody().getJSONObject("variant").getString("created_at");
        String apiVariantCreatedAt = apiRestResponse.getBody().getJSONObject("variant").getString("created_at");
        
        Assert.assertEquals(esbVariantCreatedAt, apiVariantCreatedAt);
        
        String apiVariantTitle = apiRestResponse.getBody().getJSONObject("variant").getString("title");
        
        Assert.assertEquals(connectorProperties.getProperty("variantOpt1"), apiVariantTitle);
        
        String esbPrice = esbRestResponse.getBody().getJSONObject("variant").getString("price");
        String apiPrice = apiRestResponse.getBody().getJSONObject("variant").getString("price");
        
        Assert.assertEquals(esbPrice, apiPrice);
        
    }
    
    /**
     * Positive test case for createProductVariant method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProductVariantWithMandatoryParameters" }, description = "Shopify {createProductVariant} integration test with optional parameters.")
    public void testCreateProductVariantWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createProductVariant");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProductVariant_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        String variantId = esbRestResponse.getBody().getJSONObject("variant").getString("id");
        connectorProperties.setProperty("variantId", variantId);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/admin/variants/" + variantId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String esbVariantCreatedAt = esbRestResponse.getBody().getJSONObject("variant").getString("created_at");
        String apiVariantCreatedAt = apiRestResponse.getBody().getJSONObject("variant").getString("created_at");
        
        Assert.assertEquals(esbVariantCreatedAt, apiVariantCreatedAt);
        
        String esbVariantSku = esbRestResponse.getBody().getJSONObject("variant").getString("sku");
        String apiVariantSku = apiRestResponse.getBody().getJSONObject("variant").getString("sku");
        
        Assert.assertEquals(esbVariantSku, apiVariantSku);
        
        String esbBarcode = esbRestResponse.getBody().getJSONObject("variant").getString("barcode");
        String apiBarcode = apiRestResponse.getBody().getJSONObject("variant").getString("barcode");
        
        Assert.assertEquals(esbBarcode, apiBarcode);
        
    }
    
    /**
     * Negative test case for createProductVariant method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProductVariantWithOptionalParameters" }, description = "Shopify {createProductVariant} integration test for negative case.")
    public void testCreateProductVariantNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createProductVariant");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProductVariant_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/admin/products/"
                        + connectorProperties.getProperty("productId") + "/variants.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createProductVariant_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
        String esbErrorMessage = esbRestResponse.getBody().getJSONObject("errors").getJSONArray("base").getString(0);
        String apiErrorMessage = apiRestResponse.getBody().getJSONObject("errors").getJSONArray("base").getString(0);
        
        Assert.assertEquals(esbErrorMessage, apiErrorMessage);
        
    }
    
    /**
     * Positive test case for listProductVariants method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProductVariantNegativeCase" }, description = "Shopify {listProductVariants} integration test with mandatory parameters.")
    public void testListProductVariantsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listProductVariants");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProductVariants_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        int esbVariantCount = esbRestResponse.getBody().getJSONArray("variants").length();
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/admin/products/"
                        + connectorProperties.getProperty("productId") + "/variants.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        int apiVariantCount = apiRestResponse.getBody().getJSONArray("variants").length();
        
        Assert.assertEquals(esbVariantCount, apiVariantCount);
        
        String esbVariantId = esbRestResponse.getBody().getJSONArray("variants").getJSONObject(1).getString("id");
        String apiVariantId = apiRestResponse.getBody().getJSONArray("variants").getJSONObject(1).getString("id");
        
        Assert.assertEquals(esbVariantId, apiVariantId);
        
        String esbVariantOpt = esbRestResponse.getBody().getJSONArray("variants").getJSONObject(1).getString("option1");
        String apiVariantOpt = apiRestResponse.getBody().getJSONArray("variants").getJSONObject(1).getString("option1");
        
        Assert.assertEquals(esbVariantOpt, apiVariantOpt);
        
    }
    
    /**
     * Positive test case for listProductVariants method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListProductVariantsWithMandatoryParameters" }, description = "Shopify {listProductVariants} integration test with optional parameters.")
    public void testListProductVariantsWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listProductVariants");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProductVariants_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        int esbVariantCount = esbRestResponse.getBody().getJSONArray("variants").length();
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/admin/products/"
                        + connectorProperties.getProperty("productId") + "/variants.json?limit=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        int apiVariantCount = apiRestResponse.getBody().getJSONArray("variants").length();
        
        Assert.assertEquals(esbVariantCount, apiVariantCount);
        
        String esbVariantId = esbRestResponse.getBody().getJSONArray("variants").getJSONObject(0).getString("id");
        String apiVariantId = apiRestResponse.getBody().getJSONArray("variants").getJSONObject(0).getString("id");
        
        Assert.assertEquals(esbVariantId, apiVariantId);
        
        String esbVariantOpt =
                esbRestResponse.getBody().getJSONArray("variants").getJSONObject(0).getString("created_at");
        String apiVariantOpt =
                apiRestResponse.getBody().getJSONArray("variants").getJSONObject(0).getString("created_at");
        
        Assert.assertEquals(esbVariantOpt, apiVariantOpt);
        
    }
    
    /**
     * Negative test case for listProductVariants method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListProductVariantsWithOptionalParameters" }, description = "Shopify {listProductVariants} integration test for negative case.")
    public void testListProductVariantsNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listProductVariants");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProductVariants_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/admin/products/"
                        + connectorProperties.getProperty("productId") + "/variants.json?limit=300";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
        String esbErrorMessage = esbRestResponse.getBody().getString("errors");
        String apiErrorMessage = apiRestResponse.getBody().getString("errors");
        
        Assert.assertEquals(esbErrorMessage, apiErrorMessage);
        
    }
    
    /**
     * Positive test case for getProductById method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListProductVariantsNegativeCase" }, description = "Shopify {getProductById} integration test with mandatory parameters.")
    public void testGetProductByIdWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getProductById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProductById_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/admin/products/"
                        + connectorProperties.getProperty("productId") + ".json?fields=id,product_type,handle";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("product").getString("id"), apiRestResponse
                .getBody().getJSONObject("product").getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("product").getString("handle"), apiRestResponse
                .getBody().getJSONObject("product").getString("handle"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("product").getString("product_type"),
                apiRestResponse.getBody().getJSONObject("product").getString("product_type"));
        
    }
    
    /**
     * Positive test case for getProductById method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetProductByIdWithMandatoryParameters" }, description = "Shopify {getProductById} integration test with optional parameters.")
    public void testGetProductByIdWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getProductById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProductById_optional.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/admin/products/"
                        + connectorProperties.getProperty("productId") + ".json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("product").getString("id"), apiRestResponse
                .getBody().getJSONObject("product").getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("product").getString("handle"), apiRestResponse
                .getBody().getJSONObject("product").getString("handle"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("product").getString("vendor"), apiRestResponse
                .getBody().getJSONObject("product").getString("vendor"));
        
    }
    
    /**
     * Negative test case for getProductById method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetProductByIdWithOptionalParameters" }, description = "Shopify {getProductById} integration test with negative Case.")
    public void testGetProductByIdNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getProductById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProductById_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/admin/products/invalidProductId.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("errors"), apiRestResponse.getBody()
                .getString("errors"));
        
    }
    
    /**
     * Positive test case for createOrder method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetProductByIdNegativeCase" }, description = "Shopify {createOrder} integration test with mandatory parameters.")
    public void testCreateOrderWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createOrder");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createOrder_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        String orderId = esbRestResponse.getBody().getJSONObject("order").getString("id");
        connectorProperties.setProperty("orderId", orderId);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/admin/orders/" + orderId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(
                connectorProperties.getProperty("variantId"),
                apiRestResponse.getBody().getJSONObject("order").getJSONArray("line_items").getJSONObject(0)
                        .getString("variant_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("order").getString("name"), apiRestResponse
                .getBody().getJSONObject("order").getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("order").getString("created_at"), apiRestResponse
                .getBody().getJSONObject("order").getString("created_at"));
    }
    
    /**
     * Positive test case for createOrder method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateOrderWithMandatoryParameters" }, description = "Shopify {createOrder} integration test with optional parameters.")
    public void testCreateOrderWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createOrder");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createOrder_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        String orderId1 = esbRestResponse.getBody().getJSONObject("order").getString("id");
        connectorProperties.setProperty("orderId1", orderId1);
        
        JSONObject esbLineItem =
                esbRestResponse.getBody().getJSONObject("order").getJSONArray("line_items").getJSONObject(0);
        
        String lineItemId = esbLineItem.getString("id");
        connectorProperties.setProperty("lineItemId", lineItemId);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/admin/orders/" + orderId1 + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiLineItem =
                apiRestResponse.getBody().getJSONObject("order").getJSONArray("line_items").getJSONObject(0);
        
        Assert.assertEquals(connectorProperties.getProperty("variantId"), apiLineItem.getString("variant_id"));
        Assert.assertEquals(esbLineItem.getString("quantity"), apiLineItem.getString("quantity"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("order").getString("created_at"), apiRestResponse
                .getBody().getJSONObject("order").getString("created_at"));
    }
    
    /**
     * Negative test case for createOrder method. Negative case was achieved by providing an invalid variant
     * Id.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateOrderWithOptionalParameters" }, description = "Shopify {createOrder} integration test with negative case.")
    public void testCreateOrderNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createOrder");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createOrder_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/admin/orders.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createOrder_negative.json");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").getJSONArray("order").getString(0),
                apiRestResponse.getBody().getJSONObject("errors").getJSONArray("order").getString(0));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").getJSONArray("line_items").getString(0),
                apiRestResponse.getBody().getJSONObject("errors").getJSONArray("line_items").getString(0));
    }
    
    /**
     * Positive test case for listOrders method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateOrderNegativeCase" }, description = "Shopify {listOrders} integration test with mandatory parameters.")
    public void listOrdersWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listOrders");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listOrders_mandatory.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("orders");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/admin/orders.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("orders");
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        
        if (esbResponseArray.length() > 0) {
            
            Assert.assertEquals(esbResponseArray.getJSONObject(0).get("id").toString(),
                    apiResponseArray.getJSONObject(0).get("id").toString());
            
            Assert.assertEquals(esbResponseArray.getJSONObject(0).get("name").toString(), apiResponseArray
                    .getJSONObject(0).get("name").toString());
        } else {
            Assert.assertTrue(false);
        }
        
    }
    
    /**
     * Positive test case for listOrders method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "listOrdersWithMandatoryParameters" }, description = "Shopify {listOrders} integration test with optional parameters.")
    public void listOrdersWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listOrders");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listOrders_optional.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("orders");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/admin/orders.json" + "?fields=email,financial_status";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("orders");
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        
        if (esbResponseArray.length() > 0) {
            
            Assert.assertEquals(esbResponseArray.getJSONObject(0).get("email").toString(), apiResponseArray
                    .getJSONObject(0).get("email").toString());
            
            Assert.assertEquals(esbResponseArray.getJSONObject(0).get("financial_status").toString(), apiResponseArray
                    .getJSONObject(0).get("financial_status").toString());
            
            Assert.assertFalse(esbResponseArray.getJSONObject(0).has("id"));
            Assert.assertFalse(apiResponseArray.getJSONObject(0).has("id"));
            
        } else {
            Assert.assertTrue(false);
        }
    }
    
    /**
     * Negative test case for listOrders method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "listOrdersWithOptionalParameters" }, description = "Shopify {listOrders} integration test with negative case.")
    public void listOrdersNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listOrders");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listOrders_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/admin/orders.json" + "?limit=300";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().get("errors").toString(), apiRestResponse.getBody().get("errors")
                .toString());
        
    }
    
    /**
     * Positive test case for createFulfillment method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "listOrdersNegativeCase" }, description = "Shopify {createFulfillment} integration test with mandatory parameters.")
    public void createFulfillmentWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createFulfillment");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createFulfillment_mandatory.json");
        
        JSONObject esbJsonObject = esbRestResponse.getBody().getJSONObject("fulfillment");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/admin/orders/"
                        + connectorProperties.getProperty("orderId") + "/fulfillments/" + esbJsonObject.getString("id")
                        + ".json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiJsonObject = apiRestResponse.getBody().getJSONObject("fulfillment");
        
        Assert.assertEquals(esbJsonObject.get("id"), apiJsonObject.get("id"));
        Assert.assertEquals(esbJsonObject.get("order_id"), apiJsonObject.get("order_id"));
        Assert.assertEquals(esbJsonObject.get("tracking_number"), apiJsonObject.get("tracking_number"));
        
    }
    
    /**
     * Positive test case for createFulfillment method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "createFulfillmentWithMandatoryParameters" }, description = "Shopify {createFulfillment} integration test with optional parameters.")
    public void createFulfillmentWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createFulfillment");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createFulfillment_optional.json");
        
        JSONObject esbJsonObject = esbRestResponse.getBody().getJSONObject("fulfillment");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/admin/orders/"
                        + connectorProperties.getProperty("orderId1") + "/fulfillments/" + esbJsonObject.get("id")
                        + ".json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiJsonObject = apiRestResponse.getBody().getJSONObject("fulfillment");
        
        Assert.assertEquals(esbJsonObject.get("id"), apiJsonObject.get("id"));
        Assert.assertEquals(esbJsonObject.get("order_id"), apiJsonObject.get("order_id"));
        Assert.assertEquals(esbJsonObject.get("tracking_number"), apiJsonObject.get("tracking_number"));
        Assert.assertEquals(esbJsonObject.getJSONArray("line_items").getJSONObject(0).getString("id"), apiJsonObject
                .getJSONArray("line_items").getJSONObject(0).getString("id"));
        
    }
    
    /**
     * Negative test case for createFulfillment method. Trying to re-create a fulfillment for an order.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "createFulfillmentWithOptionalParameters" }, description = "Shopify {createFulfillment} integration test with negative case.")
    public void createFulfillmentNagativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createFulfillment");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createFulfillment_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/admin/orders/"
                        + connectorProperties.getProperty("orderId") + "/fulfillments.json";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createFulfillment_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getBody().get("errors").toString(), apiRestResponse.getBody().get("errors")
                .toString());
        
    }
    
    /**
     * Positive test case for listFulfillments method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "createFulfillmentNagativeCase" }, description = "Shopify {listFulfillments} integration test with mandatory parameters.")
    public void testlistFulfillmentsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listFulfillments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listfulFillments_mandatory.json");
        
        JSONArray esbFulfillmentArray = esbRestResponse.getBody().getJSONArray("fulfillments");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/admin/orders/"
                        + connectorProperties.getProperty("orderId") + "/fulfillments.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiFulfillmentrArray = apiRestResponse.getBody().getJSONArray("fulfillments");
        
        Assert.assertEquals(esbFulfillmentArray.length(), apiFulfillmentrArray.length());
        
        if (esbFulfillmentArray.length() > 0) {
            
            Assert.assertEquals(esbFulfillmentArray.getJSONObject(0).getString("id"), apiFulfillmentrArray
                    .getJSONObject(0).getString("id"));
            Assert.assertEquals(esbFulfillmentArray.getJSONObject(0).getString("created_at"), apiFulfillmentrArray
                    .getJSONObject(0).getString("created_at"));
        } else {
            Assert.assertTrue(false);
        }
    }
    
    /**
     * Positive test case for listFulfillments method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testlistFulfillmentsWithMandatoryParameters" }, description = "Shopify {listFulfillments} integration test with optional parameters.")
    public void testlistFulfillmentsWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listFulfillments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listfulFillments_optional.json");
        
        JSONArray esbFulfillmentArray = esbRestResponse.getBody().getJSONArray("fulfillments");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/admin/orders/"
                        + connectorProperties.getProperty("orderId") + "/fulfillments.json?fields=status,order_id";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiFulfillmentrArray = apiRestResponse.getBody().getJSONArray("fulfillments");
        
        Assert.assertEquals(esbFulfillmentArray.length(), apiFulfillmentrArray.length());
        
        if (esbFulfillmentArray.length() > 0) {
            
            Assert.assertEquals(esbFulfillmentArray.getJSONObject(0).getString("order_id"), apiFulfillmentrArray
                    .getJSONObject(0).getString("order_id"));
            Assert.assertEquals(esbFulfillmentArray.getJSONObject(0).getString("status"), apiFulfillmentrArray
                    .getJSONObject(0).getString("status"));
            Assert.assertFalse(esbFulfillmentArray.getJSONObject(0).has("created_at"));
            Assert.assertFalse(esbFulfillmentArray.getJSONObject(0).has("created_at"));
        } else {
            Assert.assertTrue(false);
        }
        
    }
    
    /**
     * Negative test case for listFulfillments method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testlistFulfillmentsWithOptionalParameters" }, description = "Shopify {listFulfillments} integration test with negative case.")
    public void testlistFulfillmentsNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listFulfillments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listfulFillments_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/admin/customers.json?limit=350";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("errors"), apiRestResponse.getBody()
                .getString("errors"));
        
    }
    
}
