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
package org.wso2.carbon.connector.integration.test.tradegecko;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class TradegeckoConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> parametersMap = new HashMap<String, String>();
    
    private String mandatoryStockAdjustmentId;
    
    private String optionalStockAdjustmentId;
    
    private String stockAdjustmentLineItemId;
    
    
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("tradegecko-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("accessToken"));
        
    }
    
    /**
     * Positive test case for listProducts method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Tradegecko {listProducts} integration test with mandatory parameters.")
    public void testListProductsMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listProducts");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProducts_mandatory.json");
        
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/products/";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResultArray = esbRestResponse.getBody().getJSONArray("products");
        JSONArray apiResultArray = apiRestResponse.getBody().getJSONArray("products");
        
        Assert.assertEquals(esbResultArray.length(), apiResultArray.length());
        
        Assert.assertEquals(esbResultArray.getJSONObject(0).getInt("id"), apiResultArray.getJSONObject(0).getInt("id"));
        Assert.assertEquals(esbResultArray.getJSONObject(0).getString("name"), apiResultArray.getJSONObject(0).getString("name"));
       
      
    }
    
    /**
     * Positive test case for getProduct method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testListProductsMandatoryParameters" }, groups = { "wso2.esb" }, description = "Tradegecko {getProduct} integration test with mandatory parameters.")
    public void testGetProductMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getProduct");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProduct_mandatory.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/products/" + connectorProperties.getProperty("productId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject esbProductObj = esbRestResponse.getBody().getJSONObject("product");
        JSONObject apiProductObj = apiRestResponse.getBody().getJSONObject("product");
       
        Assert.assertEquals(esbProductObj.getInt("id"), apiProductObj.getInt("id"));
        Assert.assertEquals(esbProductObj.getString("name"), apiProductObj.getString("name"));
        Assert.assertEquals(esbProductObj.getString("status"), apiProductObj.getString("status"));
      
    }
    
    /**
     * Negative test case for getProduct method.
     */
    @Test(dependsOnMethods = { "testGetProductMandatoryParameters" }, groups = { "wso2.esb" }, description = "Tradegecko {getProduct} integration test negative case.")
    public void testGetProductNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getProduct");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProduct_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/products/INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("type"), apiRestResponse.getBody().getString("type"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString("message"));
      
    }
    
    
    /**
     * Positive test case for createInvoice method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testGetProductNegativeCase" }, groups = { "wso2.esb" }, description = "Tradegecko {createInvoice} integration test with mandatory parameters.")
    public void testCreateInvoiceMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_mandatory.json");
        
        JSONObject esbInvoiceObj = esbRestResponse.getBody().getJSONObject("invoice");
        int esbInvoiceId = esbInvoiceObj.getInt("id");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/invoices/" + esbInvoiceId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiInvoiceObj = apiRestResponse.getBody().getJSONObject("invoice");
        int apiInvoiceId = apiInvoiceObj.getInt("id");
        
        connectorProperties.setProperty("invoiceId", String.valueOf(apiInvoiceId));
        
        Assert.assertEquals(esbInvoiceId, apiInvoiceId);
        
        JSONArray esbLineItems = esbRestResponse.getBody().getJSONArray("invoice_line_items");
        JSONArray apiLineItems = apiRestResponse.getBody().getJSONArray("invoice_line_items");
        
        String esbQuantity = esbLineItems.getJSONObject(0).getString("quantity");
        String apiQuantity = apiLineItems.getJSONObject(0).getString("quantity");
        
        Assert.assertEquals(esbQuantity, apiQuantity);
        
        String esbOrderLineItemId = esbLineItems.getJSONObject(0).getString("order_line_item_id");
        String apiOrderLineItemId  = apiLineItems.getJSONObject(0).getString("order_line_item_id");
        
        connectorProperties.setProperty("orderLineItemId", esbOrderLineItemId);
       
        Assert.assertEquals(esbOrderLineItemId, apiOrderLineItemId);
      
    }
    
    /**
     * Positive test case for createInvoice method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateInvoiceMandatoryParameters" }, groups = { "wso2.esb" }, description = "Tradegecko {createInvoice} integration test with optional parameters.")
    public void testCreateInvoiceOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_optional.json");
        
        JSONObject esbInvoiceObj = esbRestResponse.getBody().getJSONObject("invoice");
        int esbInvoiceId = esbInvoiceObj.getInt("id");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/invoices/" + esbInvoiceId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiInvoiceObj = apiRestResponse.getBody().getJSONObject("invoice");
        int apiInvoiceId = apiInvoiceObj.getInt("id");
        
        Assert.assertEquals(esbInvoiceId, apiInvoiceId);
        
        int esbBillingAddressID = esbInvoiceObj.getInt("billing_address_id");
        int apiBillingAddressID = apiInvoiceObj.getInt("billing_address_id");
        
        Assert.assertEquals(esbBillingAddressID, apiBillingAddressID);
        
        String esbNotes = esbInvoiceObj.getString("notes");
        String apiNotes = apiInvoiceObj.getString("notes");
       
        Assert.assertEquals(esbNotes, apiNotes);
      
    }
    
    /**
     * Negative test case for createInvoice method.
     */
    @Test(dependsOnMethods = { "testCreateInvoiceOptionalParameters" }, groups = { "wso2.esb" }, description = "Tradegecko {createInvoice} integration test negative case.")
    public void testCreateInvoiceNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoice");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoice_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/invoices/";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createInvoice_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("type"), apiRestResponse.getBody().getString("type"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString("message"));
      
    }
    
    /**
     * Positive test case for createInvoiceLineItem method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateInvoiceNegativeCase" }, groups = { "wso2.esb" }, description = "Tradegecko {createInvoiceLineItem} integration test with mandatory parameters.")
    public void testCreateInvoiceLineItemMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoiceLineItem");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoiceLineItem_mandatory.json");
        
        JSONObject esbInvoiceLineItemObj = esbRestResponse.getBody().getJSONObject("invoice_line_item");
        int esbInvoiceLineItemId = esbInvoiceLineItemObj.getInt("id");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/invoice_line_items/" + esbInvoiceLineItemId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiInvoiceLineItemObj = apiRestResponse.getBody().getJSONObject("invoice_line_item");
        int apiInvoiceLineItemId = apiInvoiceLineItemObj.getInt("id");
        
        Assert.assertEquals(esbInvoiceLineItemId, apiInvoiceLineItemId);
        
        Assert.assertEquals(esbInvoiceLineItemObj.getString("quantity"), apiInvoiceLineItemObj.getString("quantity"));
       
        Assert.assertEquals(esbInvoiceLineItemObj.getInt("order_line_item_id"), apiInvoiceLineItemObj.getInt("order_line_item_id"));
      
    }
    
    /**
     * Positive test case for createInvoiceLineItem method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateInvoiceLineItemMandatoryParameters" }, groups = { "wso2.esb" }, description = "Tradegecko {createInvoiceLineItem} integration test with optional parameters.")
    public void testCreateInvoiceLineItemOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoiceLineItem");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoiceLineItem_optional.json");
        
        JSONObject esbInvoiceLineItemObj = esbRestResponse.getBody().getJSONObject("invoice_line_item");
        int esbInvoiceLineItemId = esbInvoiceLineItemObj.getInt("id");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/invoice_line_items/" + esbInvoiceLineItemId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiInvoiceLineItemObj = apiRestResponse.getBody().getJSONObject("invoice_line_item");
        int apiInvoiceLineItemId = apiInvoiceLineItemObj.getInt("id");
        
        Assert.assertEquals(esbInvoiceLineItemId, apiInvoiceLineItemId);
        
        Assert.assertEquals(esbInvoiceLineItemObj.getString("quantity"), apiInvoiceLineItemObj.getString("quantity"));
       
        Assert.assertEquals(esbInvoiceLineItemObj.getInt("position"), apiInvoiceLineItemObj.getInt("position"));
      
    }
    
    /**
     * Negative test case for createInvoiceLineItem method.
     */
    @Test(dependsOnMethods = { "testCreateInvoiceLineItemOptionalParameters" }, groups = { "wso2.esb" }, description = "Tradegecko {createInvoiceLineItem} integration test negative case.")
    public void testCreateInvoiceLineItemNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createInvoiceLineItem");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createInvoiceLineItem_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/invoice_line_items/";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createInvoiceLineItem_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("type"), apiRestResponse.getBody().getString("type"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString("message"));
      
    }
    /**
     * Positive test case for createStockAdjustment method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "Tradegecko {createStockAdjustment} integration test with mandatory parameters.")
    public void testCreateStockAdjustmentWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createStockAdjustment");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createStockAdjustment_mandatory.json");
        
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/stock_adjustments/";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject esbResultObject = esbRestResponse.getBody().getJSONObject("stock_adjustment");
        
        mandatoryStockAdjustmentId = esbResultObject.getString("id");
        
        JSONArray apiResultArray = apiRestResponse.getBody().getJSONArray("stock_adjustments");
        JSONObject apiResultObject = null;
        for (int i = 0; i < apiResultArray.length(); i++) {
        	apiResultObject = apiResultArray.getJSONObject(i);
        	if (apiResultObject.getString("id").equals(mandatoryStockAdjustmentId)) {
        		break;
        	}
        }
        Assert.assertNotNull(apiResultObject);

        Assert.assertEquals(esbResultObject.getString("adjustment_number"), apiResultObject.getString("adjustment_number"));
        Assert.assertEquals(esbResultObject.getString("notes"), apiResultObject.getString("notes"));

      
    }
    /**
     * Positive test case for createStockAdjustment method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "Tradegecko {createStockAdjustment} integration test with optional parameters.")
    public void testCreateStockAdjustmentWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createStockAdjustment");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createStockAdjustment_optional.json");
        
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/stock_adjustments/";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject esbResultObject = esbRestResponse.getBody().getJSONObject("stock_adjustment");
        
        optionalStockAdjustmentId = esbResultObject.getString("id");
        
        stockAdjustmentLineItemId = esbResultObject.getJSONArray("stock_adjustment_line_item_ids").getString(0);
        
        JSONArray apiResultArray = apiRestResponse.getBody().getJSONArray("stock_adjustments");
        JSONObject apiResultObject = null;
        for (int i = 0; i < apiResultArray.length(); i++) {
        	apiResultObject = apiResultArray.getJSONObject(i);
        	if (apiResultObject.getString("id").equals(optionalStockAdjustmentId)) {
        		break;
        	}
        }
        Assert.assertNotNull(apiResultObject);

        Assert.assertEquals(esbResultObject.getString("adjustment_number"), apiResultObject.getString("adjustment_number"));
        Assert.assertEquals(esbResultObject.getString("notes"), apiResultObject.getString("notes"));

      
    }
    /**
     * Negative test case for createStockAdjustment method.
     */
    @Test(groups = { "wso2.esb" }, description = "Tradegecko {createStockAdjustment} integration test negative case.")
    public void testCreateStockAdjustmentNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createStockAdjustment");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createStockAdjustment_negative.json");
        
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/stock_adjustments/";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createStockAdjustment_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").getJSONArray("reason").getString(0), 
        		apiRestResponse.getBody().getJSONObject("errors").getJSONArray("reason").getString(0));

      
    }
    
    /**
     * Positive test case for createStockAdjustmentLineItem method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testCreateStockAdjustmentWithMandatoryParameters"}, 
    		groups = { "wso2.esb" }, description = "Tradegecko {createStockAdjustmentLineItem} integration test with mandatory parameters.")
    public void testCreateStockAdjustmentLineItemWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createStockAdjustmentLineItem");
        parametersMap.put("mandatoryStockAdjustmentId", mandatoryStockAdjustmentId);
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, 
        		"esb_createStockAdjustmentLineItem_mandatory.json", parametersMap);
        
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("stock_adjustment_line_item");
        
        String mandatoryStockAdjustmentLineItemId = esbResponseObject.getString("id");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/stock_adjustment_line_items/" + mandatoryStockAdjustmentLineItemId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("stock_adjustment_line_item");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiResponseObject.getString("variant_id"), connectorProperties.getProperty("stockVariantId"));
        

      
    }
    /**
     * Positive test case for createStockAdjustmentLineItem method with optional parameters.
     */
    @Test(dependsOnMethods = {"testCreateStockAdjustmentWithOptionalParameters"}, 
    		groups = { "wso2.esb" }, description = "Tradegecko {createStockAdjustmentLineItem} integration test with optional parameters.")
    public void testCreateStockAdjustmentLineItemWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createStockAdjustmentLineItem");
        parametersMap.put("optionalStockAdjustmentId", optionalStockAdjustmentId);
        parametersMap.put("stockAdjustmentLineItemId", stockAdjustmentLineItemId);
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, 
        		"esb_createStockAdjustmentLineItem_optional.json", parametersMap);
        
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("stock_adjustment_line_item");
        
        String optionalStockAdjustmentLineItemId = esbResponseObject.getString("id");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/stock_adjustment_line_items/" + optionalStockAdjustmentLineItemId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("stock_adjustment_line_item");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiResponseObject.getString("variant_id"), connectorProperties.getProperty("optionalStockVariantId"));
        

      
    }
    
    /**
     * Negative test case for createStockAdjustmentLineItem method.
     */
    @Test(dependsOnMethods = {"testCreateStockAdjustmentWithMandatoryParameters", "testCreateStockAdjustmentLineItemWithMandatoryParameters"}, 
    		groups = { "wso2.esb" }, description = "Tradegecko {createStockAdjustmentLineItem} integration test negative case.")
    public void testCreateStockAdjustmentLineItemNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createStockAdjustmentLineItem");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, 
        		"esb_createStockAdjustmentLineItem_negative.json", parametersMap);
        
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/stock_adjustment_line_items/";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
        		"api_createStockAdjustmentLineItem_negative.json", parametersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 500);
        

      
    }
    
}
