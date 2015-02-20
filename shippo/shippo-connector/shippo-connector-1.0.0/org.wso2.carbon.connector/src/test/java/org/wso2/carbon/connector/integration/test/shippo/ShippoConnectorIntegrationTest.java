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

package org.wso2.carbon.connector.integration.test.shippo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class ShippoConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("shippo-connector-1.0.0");
        
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Accept", "application/json");
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        String token = connectorProperties.getProperty("username") + ":" + connectorProperties.getProperty("password");
        
        // Encoding token into base 64
        byte[] encodedToken = Base64.encodeBase64(token.getBytes());
        apiRequestHeadersMap.put("Authorization", "Basic " + new String(encodedToken));
        
    }
    
    /**
     * Positive test case for createCustomsItem method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Shippo {createCustomsItem} integration test with mandatory parameters.")
    public void testCreateCustomsItemWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:createCustomsItem");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomsItem_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        String esbCustomsItemObjectId = esbRestResponse.getBody().getString("object_id");
        
        connectorProperties.setProperty("itemsObjectId", esbCustomsItemObjectId);
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/customs/items/" + esbCustomsItemObjectId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("description"),
                apiRestResponse.getBody().getString("description"));
        
        Assert.assertEquals(esbRestResponse.getBody().getString("mass_unit"),
                apiRestResponse.getBody().getString("mass_unit"));
        
    }
    
    /**
     * Positive test case for createCustomsItem method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCustomsItemWithMandatoryParameters" }, description = "Shippo {createCustomsItem} integration test with optional parameters.")
    public void testCreateCustomsItemWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCustomsItem");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomsItem_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        String esbCustomsItemObjectId = esbRestResponse.getBody().getString("object_id");
        
        connectorProperties.setProperty("itemsObjectId1", esbCustomsItemObjectId);
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/customs/items/" + esbCustomsItemObjectId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("tariff_number"),
                apiRestResponse.getBody().getString("tariff_number"));
        
        Assert.assertEquals(esbRestResponse.getBody().getString("metadata"),
                apiRestResponse.getBody().getString("metadata"));
        
    }
    
    /**
     * Negative test case for createCustomsItem method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCustomsItemWithOptionalParameters" }, description = "Shippo {createCustomsItem} integration test for negative case.")
    public void testCreateCustomsItemNegativeCase() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:createCustomsItem");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCustomsItem_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/customs/items";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createCustomsItem_negative.json");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("description").getString(0), apiRestResponse
                .getBody().getJSONArray("description").getString(0));
    }
    
    /**
     * Positive test case for listCustomsItems method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCustomsItemNegativeCase" }, description = "Shippo {listCustomsItems} integration test with mandatory parameters.")
    public void testListCustomsItemsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCustomsItems");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomsItems_mandatory.json");
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/customs/items";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("count"), apiRestResponse.getBody().getInt("count"));
        
        JSONArray esbResultsArray = esbRestResponse.getBody().getJSONArray("results");
        
        if (esbResultsArray.length() > 0) {
            JSONArray apiResultsArray = apiRestResponse.getBody().getJSONArray("results");
            Assert.assertEquals(esbResultsArray.getJSONObject(0).getString("object_id"),
                    apiResultsArray.getJSONObject(0).getString("object_id"));
            Assert.assertEquals(esbResultsArray.getJSONObject(0).getString("object_created"), apiResultsArray
                    .getJSONObject(0).getString("object_created"));
        } else {
            Assert.assertTrue(false);
        }
        
    }
    
    /**
     * Positive test case for listCustomsItems method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListCustomsItemsWithMandatoryParameters" }, description = "Shippo {listCustomsItems} integration test with optional parameters.")
    public void testListCustomsItemsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCustomsItems");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomsItems_optional.json");
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/customs/items?results=2";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResultsArray = esbRestResponse.getBody().getJSONArray("results");
        JSONArray apiResultsArray = apiRestResponse.getBody().getJSONArray("results");
        
        Assert.assertEquals(esbResultsArray.length(), apiResultsArray.length());
        
        if (esbResultsArray.length() > 0) {
            Assert.assertEquals(esbResultsArray.getJSONObject(0).getString("object_id"),
                    apiResultsArray.getJSONObject(0).getString("object_id"));
            Assert.assertEquals(esbResultsArray.getJSONObject(0).getString("object_created"), apiResultsArray
                    .getJSONObject(0).getString("object_created"));
        } else {
            Assert.assertTrue(false);
        }
        
    }
    
    /**
     * Negative test case for listCustomsItems method. Uses an invalid page number.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListCustomsItemsWithOptionalParameters" }, description = "Shippo {listCustomsItems} integration test for negative case.")
    public void testListCustomsItemsNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCustomsItems");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCustomsItems_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/customs/items?page=INVALID";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("detail"), apiRestResponse.getBody()
                .getString("detail"));
    }
    
    /**
     * Positive test case for createCustomsDeclaration method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListCustomsItemsNegativeCase" }, description = "Shippo {createCustomsDeclaration} integration test with mandatory parameters.")
    public void testCreateCustomsDeclarationWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCustomsDeclaration");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_createCustomsDeclaration_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        String esbCustomsDeclarationtId = esbRestResponse.getBody().getString("object_id");
        
        connectorProperties.setProperty("CustomsDeclarationtId", esbCustomsDeclarationtId);
        
        String apiEndpoint =
                connectorProperties.getProperty("apiUrl") + "/v1/customs/declarations/" + esbCustomsDeclarationtId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("object_created"),
                apiRestResponse.getBody().getString("object_created"));
        
        Assert.assertEquals(esbRestResponse.getBody().getString("non_delivery_option"), apiRestResponse.getBody()
                .getString("non_delivery_option"));
        
        Assert.assertEquals(esbRestResponse.getBody().getString("contents_type"),
                apiRestResponse.getBody().getString("contents_type"));
    }
    
    /**
     * Positive test case for createCustomsDeclaration method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCustomsDeclarationWithMandatoryParameters" }, description = "Shippo {createCustomsDeclaration} integration test with optional parameters.")
    public void testCreateCustomsDeclarationWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCustomsDeclaration");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_createCustomsDeclaration_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        String esbCustomsDeclarationtId = esbRestResponse.getBody().getString("object_id");
        
        String apiEndpoint =
                connectorProperties.getProperty("apiUrl") + "/v1/customs/declarations/" + esbCustomsDeclarationtId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("object_created"),
                apiRestResponse.getBody().getString("object_created"));
        
        Assert.assertEquals(esbRestResponse.getBody().getString("non_delivery_option"), apiRestResponse.getBody()
                .getString("non_delivery_option"));
        
        Assert.assertEquals(connectorProperties.getProperty("contentsExplanation"), apiRestResponse.getBody()
                .getString("contents_explanation"));
    }
    
    /**
     * Negative test case for createCustomsDeclaration method. Uses an invalid customs item Id.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCustomsDeclarationWithOptionalParameters" }, description = "Shippo {createCustomsDeclaration} integration test for negative case.")
    public void testCreateCustomsDeclarationNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCustomsDeclaration");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_createCustomsDeclaration_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/customs/declarations";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                        "api_createCustomsDeclaration_negative.json");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getString(0), esbRestResponse.getBody()
                .getJSONArray("items").getString(0));
    }
    
    /**
     * Positive test case for createAddress method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCustomsDeclarationNegativeCase" }, description = "Shippo {createAddress} integration test with mandatory parameters.")
    public void testCreateAddressWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAddress");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAddress_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        String esbAddressObjectId = esbRestResponse.getBody().getString("object_id");
        
        connectorProperties.setProperty("addressIdMandatory", esbAddressObjectId);
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/addresses/" + esbAddressObjectId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("object_purpose"),
                apiRestResponse.getBody().getString("object_purpose"));
        
        Assert.assertEquals(esbRestResponse.getBody().getString("country"),
                apiRestResponse.getBody().getString("country"));
        
    }
    
    /**
     * Positive test case for createAddress method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAddressWithMandatoryParameters" }, description = "Shippo {createAddress} integration test with optional parameters.")
    public void testCreateAddressWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAddress");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAddress_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        String esbAddressObjectId = esbRestResponse.getBody().getString("object_id");
        
        connectorProperties.setProperty("addressIdOptional", esbAddressObjectId);
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/addresses/" + esbAddressObjectId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("company"), apiRestResponse.getBody().getString("company"));
        
        Assert.assertEquals(esbRestResponse.getBody().getString("street_no"),
                apiRestResponse.getBody().getString("street_no"));
        
    }
    
    /**
     * Negative test case for createAddress method. Provides an invalid object purpose.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAddressWithOptionalParameters" }, description = "Shippo {createAddress} integration test for negative case.")
    public void testCreateAddressNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAddress");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAddress_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/addresses";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createAddress_negative.json");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("object_purpose").getString(0), apiRestResponse
                .getBody().getJSONArray("object_purpose").getString(0));
    }
    
    /**
     * Positive test case for listAddresses method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAddressNegativeCase" }, description = "Shippo {listAddresses} integration test with mandatory parameters.")
    public void testListAddressesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listAddresses");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAddresses_mandatory.json");
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/addresses";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("count"), apiRestResponse.getBody().getInt("count"));
        
        JSONArray esbResultsArray = esbRestResponse.getBody().getJSONArray("results");
        
        if (esbResultsArray.length() > 0) {
            JSONArray apiResultsArray = apiRestResponse.getBody().getJSONArray("results");
            Assert.assertEquals(esbResultsArray.getJSONObject(0).getString("object_id"),
                    apiResultsArray.getJSONObject(0).getString("object_id"));
            Assert.assertEquals(esbResultsArray.getJSONObject(0).getString("object_owner"), apiResultsArray
                    .getJSONObject(0).getString("object_owner"));
        } else {
            Assert.assertTrue(false);
        }
        
    }
    
    /**
     * Positive test case for listAddresses method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListAddressesWithMandatoryParameters" }, description = "Shippo {listAddresses} integration test with optional parameters.")
    public void testListAddressesWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listAddresses");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAddresses_optional.json");
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/addresses?results=2";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResultsArray = esbRestResponse.getBody().getJSONArray("results");
        JSONArray apiResultsArray = apiRestResponse.getBody().getJSONArray("results");
        
        Assert.assertEquals(esbResultsArray.length(), apiResultsArray.length());
        
        if (esbResultsArray.length() > 0) {
            Assert.assertEquals(esbResultsArray.getJSONObject(0).getString("object_id"),
                    apiResultsArray.getJSONObject(0).getString("object_id"));
            Assert.assertEquals(esbResultsArray.getJSONObject(0).getString("object_created"), apiResultsArray
                    .getJSONObject(0).getString("object_created"));
        } else {
            Assert.assertTrue(false);
        }
        
    }
    
    /**
     * Negative test case for listAddresses method. Uses an invalid page number.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListAddressesWithOptionalParameters" }, description = "Shippo {listAddresses} integration test for negative case.")
    public void testListAddressesNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listAddresses");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAddresses_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/addresses?page=INVALID";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("detail"), apiRestResponse.getBody()
                .getString("detail"));
    }
    
    /**
     * Positive test case for createShipment method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListAddressesNegativeCase" }, description = "Shippo {createShipment} integration test with mandatory parameters.")
    public void testCreateShipmentWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createShipment");
        
        // Since parcel object id is required for shipment creation, first creates a parcel object using a
        // direct API call.
        
        String apiEndpointForParcel = connectorProperties.getProperty("apiUrl") + "/v1/parcels";
        
        RestResponse<JSONObject> apiRestResponseForParcel =
                sendJsonRestRequest(apiEndpointForParcel, "POST", apiRequestHeadersMap,
                        "api_createParcel_mandatory.json");
        
        Assert.assertEquals(apiRestResponseForParcel.getHttpStatusCode(), 201);
        
        connectorProperties.setProperty("parcelObjectId", apiRestResponseForParcel.getBody().getString("object_id"));
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createShipment_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        String shipmentObjectId = esbRestResponse.getBody().getString("object_id");
        connectorProperties.setProperty("shipmentId", shipmentObjectId);
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/shipments/" + shipmentObjectId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("object_id"),
                apiRestResponse.getBody().getString("object_id"));
        
        Assert.assertEquals(esbRestResponse.getBody().getString("object_owner"),
                apiRestResponse.getBody().getString("object_owner"));
        
    }
    
    /**
     * Positive test case for createShipment method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateShipmentWithMandatoryParameters" }, description = "Shippo {createShipment} integration test with optional parameters.")
    public void testcreateShipmentWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createShipment");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createShipment_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
        String apiEndpoint =
                connectorProperties.getProperty("apiUrl") + "/v1/shipments/"
                        + esbRestResponse.getBody().getString("object_id");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("object_id"),
                apiRestResponse.getBody().getString("object_id"));
        
        Assert.assertEquals(connectorProperties.getProperty("CustomsDeclarationtId"), apiRestResponse.getBody()
                .getString("customs_declaration"));
        
    }
    
    /**
     * Negative test case for createShipment method. Provides an invalid object purpose.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testcreateShipmentWithOptionalParameters" }, description = "Shippo {createShipment} integration test with negative case.")
    public void testcreateShipmentNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createShipment");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createShipment_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/shipments";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createShipment_negative.json");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("object_purpose").getString(0), apiRestResponse
                .getBody().getJSONArray("object_purpose").getString(0));
        
    }
    
    /**
     * Positive test case for listShipments method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testcreateShipmentNegativeCase" }, description = "Shippo {listShipments} integration test with mandatory parameters.")
    public void testListShipmentsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listShipments");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listShipments_mandatory.json");
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/shipments";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("count"), apiRestResponse.getBody().getInt("count"));
        
        JSONArray esbResultsArray = esbRestResponse.getBody().getJSONArray("results");
        
        if (esbResultsArray.length() > 0) {
            JSONArray apiResultsArray = apiRestResponse.getBody().getJSONArray("results");
            Assert.assertEquals(esbResultsArray.getJSONObject(0).getString("object_id"),
                    apiResultsArray.getJSONObject(0).getString("object_id"));
        } else {
            Assert.assertTrue(false);
        }
        
    }
    
    /**
     * Positive test case for listShipments method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListShipmentsWithMandatoryParameters" }, description = "Shippo {listShipments} integration test with optional parameters.")
    public void testListShipmentsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listShipments");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listShipments_optional.json");
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/shipments?results=2";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResultsArray = esbRestResponse.getBody().getJSONArray("results");
        JSONArray apiResultsArray = apiRestResponse.getBody().getJSONArray("results");
        
        Assert.assertEquals(esbResultsArray.length(), apiResultsArray.length());
        
        if (esbResultsArray.length() > 0) {
            Assert.assertEquals(esbResultsArray.getJSONObject(0).getString("object_id"),
                    apiResultsArray.getJSONObject(0).getString("object_id"));
            Assert.assertEquals(esbResultsArray.getJSONObject(0).getString("object_created"), apiResultsArray
                    .getJSONObject(0).getString("object_created"));
        } else {
            Assert.assertTrue(false);
        }
        
    }
    
    /**
     * Negative case for listShipments method. Uses an invalid page number.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListShipmentsWithOptionalParameters" }, description = "Shippo {listShipments} integration test with negative case.")
    public void testListShipmentsNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listShipments");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listShipments_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/shipments?page=INVALID";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("detail"), apiRestResponse.getBody()
                .getString("detail"));
        
    }
    
    /**
     * Positive test case for listTransactions method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListShipmentsNegativeCase" }, description = "Shippo {listTransactions} integration test with mandatory parameters.")
    public void testListTransactionsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTransactions");
        
        // Direct API call to list all Rate objects and extracts the first Rate object's id.
        // Then uses a Direct call to create a transaction before listing all the transactions.
        // This was done since, there are no transaction creation calls in earlier test cases.
        
        String apiRatesEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/rates";
        
        RestResponse<JSONObject> apiRestResponseForRates =
                sendJsonRestRequest(apiRatesEndpoint, "GET", apiRequestHeadersMap);
        
        String apiRateId =
                apiRestResponseForRates.getBody().getJSONArray("results").getJSONObject(0).getString("object_id");
        
        connectorProperties.setProperty("rateId", apiRateId);
        
        String apiTransactionsEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/transactions";
        
        RestResponse<JSONObject> apiRestResponseForTransactionCreation =
                sendJsonRestRequest(apiTransactionsEndpoint, "POST", apiRequestHeadersMap,
                        "api_createTransaction_mandatory.json");
        
        Assert.assertEquals(apiRestResponseForTransactionCreation.getHttpStatusCode(), 201);
        
        // End of Transaction creation, following codes tests the listTransaction method.
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTransactions_mandatory.json");
        
        JSONArray esbTransactionsArray = esbRestResponse.getBody().getJSONArray("results");
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/transactions";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiTransactionsArray = apiRestResponse.getBody().getJSONArray("results");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("count"), apiRestResponse.getBody().getString("count"));
        
        if (esbTransactionsArray.length() > 0) {
            JSONObject esbTransaction = esbTransactionsArray.getJSONObject(0);
            JSONObject apiTransaction = apiTransactionsArray.getJSONObject(0);
            
            Assert.assertEquals(esbTransaction.getString("object_id"), apiTransaction.getString("object_id"));
            Assert.assertEquals(esbTransaction.getString("object_created"), apiTransaction.getString("object_created"));
        } else {
            Assert.assertTrue(false);
        }
    }
    
    /**
     * Positive test case for listTransactions method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListTransactionsWithMandatoryParameters" }, description = "Shippo {listTransactions} integration test with optional parameters.")
    public void testListTransactionsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTransactions");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTransactions_optional.json");
        
        JSONArray esbTransactionsArray = esbRestResponse.getBody().getJSONArray("results");
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/transactions?results=1";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiTransactionsArray = apiRestResponse.getBody().getJSONArray("results");
        
        Assert.assertEquals(esbTransactionsArray.length(), apiTransactionsArray.length());
        
        if (esbTransactionsArray.length() > 0) {
            JSONObject esbTransaction = esbTransactionsArray.getJSONObject(0);
            JSONObject apiTransaction = apiTransactionsArray.getJSONObject(0);
            
            Assert.assertEquals(esbTransaction.getString("object_id"), apiTransaction.getString("object_id"));
            Assert.assertEquals(esbTransaction.getString("object_created"), apiTransaction.getString("object_created"));
        } else {
            Assert.assertTrue(false);
        }
    }
    
    /**
     * Negative test case for listTransactions method. Uses an invalid page number.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListTransactionsWithOptionalParameters" }, description = "Shippo {listTransactions} integration test negative case.")
    public void testListTransactionsNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTransactions");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTransactions_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/v1/transactions?page=INVALID";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("detail"), apiRestResponse.getBody()
                .getString("detail"));
    }
    
}
