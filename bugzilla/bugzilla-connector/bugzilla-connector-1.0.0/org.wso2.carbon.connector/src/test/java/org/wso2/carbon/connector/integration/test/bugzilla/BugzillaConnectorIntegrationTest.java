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

package org.wso2.carbon.connector.integration.test.bugzilla;

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

public class BugzillaConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private final Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private final Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private String authString;
    
    private String apiEndpointUrl;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("bugzilla-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Accept", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        authString = "?Bugzilla_api_key=" + connectorProperties.getProperty("apiKey");
        apiEndpointUrl = connectorProperties.getProperty("apiUrl");
    }
    
    /**
     * Positive test case for createProduct method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {createProduct} integration test with mandatory parameters.")
    public void testCreateProductWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createProduct");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProduct_mandatory.json");
        
        final String productId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("productId", productId);
        
        final String apiEndpoint = apiEndpointUrl + "/product/" + productId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("products").getJSONObject(0);
        
        Assert.assertEquals(connectorProperties.getProperty("productName"), apiResponse.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("productDescription"), apiResponse.getString("description"));
        Assert.assertEquals(connectorProperties.getProperty("productVersion"), apiResponse.getJSONArray("versions")
                .getJSONObject(0).getString("name"));
    }
    
    /**
     * Positive test case for createProduct method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {createProduct} integration test with optional parameters.")
    public void testCreateProductWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createProduct");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProduct_optional.json");
        
        final String productIdOpt = esbRestResponse.getBody().getString("id");
        connectorProperties.put("productIdOpt", productIdOpt);
        
        final String apiEndpoint = apiEndpointUrl + "/product/" + productIdOpt + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("products").getJSONObject(0);
        
        Assert.assertEquals(connectorProperties.getProperty("productNameOpt"), apiResponse.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("productDescriptionOpt"),
                apiResponse.getString("description"));
        Assert.assertEquals(connectorProperties.getProperty("productConfirmation"),
                apiResponse.getString("has_unconfirmed"));
        Assert.assertEquals(connectorProperties.getProperty("productIsOpen"), apiResponse.getString("is_active"));  
    }
    
    /**
     * Negative test case for createProduct method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {createProduct} integration test with negative case.")
    public void testCreateProductWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createProduct");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProduct_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/product" + authString;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createProduct_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
    }
    
    /**
     * Positive test case for searchProducts method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {searchProducts} integration test with mandatory parameters.")
    public void testSearchProductsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchProducts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchProducts_mandatory.json");
        
        JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("products").getJSONObject(0);
        
        final String apiEndpoint =
                apiEndpointUrl + "/product" + authString + "&ids=" + connectorProperties.getProperty("productId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("products").getJSONObject(0);
        
        Assert.assertEquals(apiResponse.getString("name"), esbResponse.getString("name"));
        Assert.assertEquals(apiResponse.getString("description"), esbResponse.getString("description"));
        Assert.assertEquals(apiResponse.getString("has_unconfirmed"), esbResponse.getString("has_unconfirmed"));
    }
    
    /**
     * Positive test case for searchProducts method with includeFields parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {searchProducts} integration test with includeFields parameters.", dependsOnMethods = { "testCreateProductWithMandatoryParameters" })
    public void testSearchProductsWithIncludeFieldsParameter() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchProducts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchProducts_includeFields.json");
        
        JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("products").getJSONObject(0);
        
        final String apiEndpoint =
                apiEndpointUrl + "/product" + authString + "&ids=" + connectorProperties.getProperty("productId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("products").getJSONObject(0);
        
        Assert.assertEquals(apiResponse.getString("name"), esbResponse.getString("name"));
        Assert.assertEquals(apiResponse.getString("id"), esbResponse.getString("id"));
        Assert.assertNotEquals(apiResponse.has("is_active"), esbResponse.has("is_active"));
        Assert.assertNotEquals(apiResponse.has("has_unconfirmed"), esbResponse.has("has_unconfirmed"));
    }
    
    /**
     * Positive test case for searchProducts method with excludeFields parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {searchProducts} integration test with excludeFields parameters.", dependsOnMethods = { "testCreateProductWithMandatoryParameters" })
    public void testSearchProductsWithExcludeFieldsParameter() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchProducts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchProducts_excludeFields.json");
        
        JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("products").getJSONObject(0);
        
        final String apiEndpoint =
                apiEndpointUrl + "/product" + authString + "&ids=" + connectorProperties.getProperty("productId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("products").getJSONObject(0);
        
        Assert.assertEquals(apiResponse.getString("id"), esbResponse.getString("id"));
        Assert.assertEquals(apiResponse.getString("is_active"), esbResponse.getString("is_active"));
        Assert.assertNotEquals(apiResponse.has("name"), esbResponse.has("name"));
        Assert.assertNotEquals(apiResponse.has("description"), esbResponse.has("description"));
        Assert.assertNotEquals(apiResponse.has("classification"), esbResponse.has("classification"));
    }
    
    /**
     * Negative test case for getProduct method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {getProduc} integration test with negative case.")
    public void testSearchProductsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchProducts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchProducts_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/product" + authString;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap, "api_searchProducts_negative.json");
        
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
    @Test(groups = { "wso2.esb" }, description = "bugzilla {createUser} integration test with mandatory parameters.")
    public void testCreateUserWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createUser");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createUser_mandatory.json");
        
        final String userId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("userId", userId);
        
        final String apiEndpoint = apiEndpointUrl + "/user/" + userId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("users").getJSONObject(0);
        
        Assert.assertEquals(connectorProperties.getProperty("emailMand"), apiResponse.getString("email"));
    }
    
    /**
     * Positive test case for createUser method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {createUser} integration test with optional parameters.")
    public void testCreateUserWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createUser");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createUser_optional.json");
        
        final String userId = esbRestResponse.getBody().getString("id");
        
        final String apiEndpoint = apiEndpointUrl + "/user/" + userId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("users").getJSONObject(0);
        
        Assert.assertEquals(connectorProperties.getProperty("emailOpt"), apiResponse.getString("email"));
        Assert.assertEquals(connectorProperties.getProperty("userName"), apiResponse.getString("real_name"));
    }
    
    /**
     * Test createUser method with Negative Case.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {createUser} integration test with  negative case.")
    public void testCreateUserWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createUser");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createUser_negative.json");
        
        JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint = apiEndpointUrl + "/user" + authString;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createUser_negative.json");
        
        JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(apiResponse.get("code"), esbResponse.get("code"));
        Assert.assertEquals(apiResponse.get("error"), esbResponse.get("error"));
        Assert.assertEquals(apiResponse.get("message"), esbResponse.get("message"));
    }
    
    /**
     * Positive test case for searchUsers method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateUserWithMandatoryParameters" }, description = "bugzilla {searchUsers} integration test with optional parameters.")
    public void testSearchUsersWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchUsers");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchUsers_optional.json");
        
        JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("users").getJSONObject(0);
        
        final String apiEndpoint =
                apiEndpointUrl + "/user" + authString + "&ids=" + connectorProperties.getProperty("userId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("users").getJSONObject(0);
        
        Assert.assertEquals(apiResponse.getString("id"), esbResponse.getString("id"));
        Assert.assertEquals(apiResponse.getString("name"), esbResponse.getString("name"));
        Assert.assertEquals(apiResponse.getString("real_name"), esbResponse.getString("real_name"));
        Assert.assertEquals(apiResponse.getString("can_login"), esbResponse.getString("can_login"));
        Assert.assertEquals(apiResponse.getString("email_enabled"), esbResponse.getString("email_enabled"));
    }
    
    /**
     * Test searchUsers method with Negative Case.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {searchUsers} integration test with  negative case.")
    public void testSearchUsersWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchUsers");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchUsers_negative.json");
        
        JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint = apiEndpointUrl + "/user" + authString + "&ids=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(apiResponse.get("code"), esbResponse.get("code"));
        Assert.assertEquals(apiResponse.get("error"), esbResponse.get("error"));
        Assert.assertEquals(apiResponse.get("message"), esbResponse.get("message"));
    }
    
    /**
     * Positive test case for createComponent method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {createComponent} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateProductWithMandatoryParameters", "testCreateUserWithMandatoryParameters" })
    public void testCreateComponentWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createComponent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComponent_mandatory.json");
        
        final String componentId = esbRestResponse.getBody().getString("id");
        
        final String apiEndpoint =
                apiEndpointUrl + "/product/" + connectorProperties.getProperty("productId") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponse =
                apiRestResponse.getBody().getJSONArray("products").getJSONObject(0).getJSONArray("components")
                        .getJSONObject(0);
        
        Assert.assertEquals(componentId, apiResponse.getString("id"));
        Assert.assertEquals(connectorProperties.getProperty("componentName"), apiResponse.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("componentDescription"),
                apiResponse.getString("description"));
    }
    
    /**
     * Positive test case for createComponent method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {createComponent} integration test with optional parameters.", dependsOnMethods = {
            "testCreateProductWithOptionalParameters", "testCreateUserWithMandatoryParameters" })
    public void testCreateComponentWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createComponent");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComponent_optional.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/product/" + connectorProperties.getProperty("productIdOpt") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponse =
                apiRestResponse.getBody().getJSONArray("products").getJSONObject(0).getJSONArray("components")
                        .getJSONObject(0);
        
        Assert.assertEquals(connectorProperties.getProperty("componentName"), apiResponse.getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("componentDescription"),
                apiResponse.getString("description"));
        Assert.assertEquals(connectorProperties.getProperty("componentIsOpen"), apiResponse.getString("is_active"));
    }
    
    /**
     * Negative test case for createComponent method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {createComponent} integration test with negative case.")
    public void testCreateComponentWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createComponent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComponent_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/component" + authString;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createComponent_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
    }
    
    /**
     * Positive test case for createBug method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {createBug} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateProductWithMandatoryParameters", "testCreateComponentWithMandatoryParameters" })
    public void testCreateBugWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createBug");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createBug_mandatory.json");
        
        final String bugId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("bugId", bugId);
        
        final String apiEndpoint = apiEndpointUrl + "/bug/" + bugId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("bugs").getJSONObject(0);
        Assert.assertEquals(connectorProperties.getProperty("productName"), apiResponse.getString("product"));
        Assert.assertEquals(connectorProperties.getProperty("componentName"), apiResponse.getString("component"));
        Assert.assertEquals(connectorProperties.getProperty("bugSummary"), apiResponse.getString("summary"));
        Assert.assertEquals(connectorProperties.getProperty("productVersion"), apiResponse.getString("version"));
        Assert.assertEquals(connectorProperties.getProperty("bugOPSys").toLowerCase(), apiResponse.getString("op_sys")
                .toLowerCase());
        Assert.assertEquals(connectorProperties.getProperty("bugPlatform").toLowerCase(),
                apiResponse.getString("platform").toLowerCase());
        Assert.assertEquals(connectorProperties.getProperty("bugSeverity").toLowerCase(),
                apiResponse.getString("severity").toLowerCase());
        Assert.assertEquals(connectorProperties.getProperty("bugPriority").toLowerCase(),
                apiResponse.getString("priority").toLowerCase());
    }
    
    /**
     * Positive test case for createBug method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {createBug} integration test with optional parameters.", dependsOnMethods = {
            "testCreateProductWithMandatoryParameters", "testCreateComponentWithMandatoryParameters",
            "testCreateUserWithMandatoryParameters", "testCreateUserWithOptionalParameters" })
    public void testCreateBugWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createBug");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createBug_optional.json");
        
        final String bugId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("bugIdOpt", bugId);
        
        final String apiEndpoint = apiEndpointUrl + "/bug/" + bugId + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("bugs").getJSONObject(0);
        
        Assert.assertEquals(connectorProperties.getProperty("bugAlias"), apiResponse.getJSONArray("alias").getString(0));
        Assert.assertEquals(connectorProperties.getProperty("bugStatus").toLowerCase(), apiResponse.getString("status")
                .toLowerCase());
        Assert.assertEquals(connectorProperties.getProperty("cfValue"),
                apiResponse.getString(connectorProperties.getProperty("cfName")));
        Assert.assertEquals(connectorProperties.getProperty("emailMand"), apiResponse.getString("assigned_to"));
        Assert.assertEquals(connectorProperties.getProperty("emailMand"), apiResponse.getJSONArray("cc_detail")
                .getJSONObject(0).getString("email")); 
    }
    
    /**
     * Negative test case for createBug method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {createBug} integration test with negative parameters.")
    public void testCreateBugWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createBug");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createBug_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/bug" + authString;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_createBug_negative.json");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getString("error"), esbRestResponse.getBody().getString("error"));
        Assert.assertEquals(apiRestResponse.getBody().getString("message"),
                esbRestResponse.getBody().getString("message"));
        Assert.assertEquals(apiRestResponse.getBody().getString("code"), esbRestResponse.getBody().getString("code"));   
    }
    
    /**
     * Positive test case for getBug method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {getBug} integration test with mandatory parameters.", dependsOnMethods = { "testCreateBugWithMandatoryParameters" })
    public void testGetBugWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getBug");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBug_mandatory.json");
        
        JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("bugs").getJSONObject(0);
        
        final String apiEndpoint = apiEndpointUrl + "/bug/" + connectorProperties.getProperty("bugId") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("bugs").getJSONObject(0);
        
        Assert.assertEquals(apiResponse.getString("product"), esbResponse.getString("product"));
        Assert.assertEquals(apiResponse.getString("component"), esbResponse.getString("component"));
        Assert.assertEquals(apiResponse.getString("summary"), esbResponse.getString("summary"));
        Assert.assertEquals(apiResponse.getString("version"), esbResponse.getString("version"));
        Assert.assertEquals(apiResponse.getString("op_sys"), esbResponse.getString("op_sys"));
        Assert.assertEquals(apiResponse.getString("platform"), esbResponse.getString("platform"));
        Assert.assertEquals(apiResponse.getString("severity"), esbResponse.getString("severity"));
        Assert.assertEquals(apiResponse.getString("priority"), esbResponse.getString("priority"));
    }
    
    /**
     * Positive test case for getBug method with includeFields parameter.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {getBug} integration test with includeFields parameter.", dependsOnMethods = { "testCreateBugWithMandatoryParameters" })
    public void testGetBugWithIncludeFieldsParameter() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getBug");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBug_includeFields.json");
        
        JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("bugs").getJSONObject(0);
        
        final String apiEndpoint = apiEndpointUrl + "/bug/" + connectorProperties.getProperty("bugId") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("bugs").getJSONObject(0);
        
        Assert.assertEquals(apiResponse.getString("product"), esbResponse.getString("product"));
        Assert.assertEquals(apiResponse.getString("component"), esbResponse.getString("component"));
        Assert.assertNotEquals(apiResponse.has("summary"), esbResponse.has("summary"));
        Assert.assertNotEquals(apiResponse.has("severity"), esbResponse.has("severity"));
        Assert.assertNotEquals(apiResponse.has("platform"), esbResponse.has("platform")); 
    }
    
    /**
     * Positive test case for getBug method with excludeFields parameter.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {getBug} integration test with excludeFields parameter.", dependsOnMethods = { "testCreateBugWithMandatoryParameters" })
    public void testGetBugWithExcludeFieldsParameter() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getBug");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBug_excludeFields.json");
        
        JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("bugs").getJSONObject(0);
        
        final String apiEndpoint = apiEndpointUrl + "/bug/" + connectorProperties.getProperty("bugId") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("bugs").getJSONObject(0);
        
        Assert.assertEquals(apiResponse.getString("severity"), esbResponse.getString("severity"));
        Assert.assertEquals(apiResponse.getString("platform"), esbResponse.getString("platform"));
        Assert.assertNotEquals(apiResponse.has("product"), esbResponse.has("product"));
        Assert.assertNotEquals(apiResponse.has("component"), esbResponse.has("component"));
        Assert.assertNotEquals(apiResponse.has("op_sys"), esbResponse.has("op_sys"));
    }
    
    /**
     * Positive test case for searchBugs method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {searchBugs} integration test with mandatory parameters.", dependsOnMethods = {
            "testCreateBugWithMandatoryParameters", "testCreateBugWithOptionalParameters" })
    public void testSearchBugsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchBugs");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchBugs_mandatory.json");
        
        JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("bugs").getJSONObject(0);
        
        final String apiEndpoint = apiEndpointUrl + "/bug" + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("bugs").getJSONObject(0);
        
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("bugs").length(), esbRestResponse.getBody()
                .getJSONArray("bugs").length());
        Assert.assertEquals(apiResponse.getString("id"), esbResponse.getString("id"));
        Assert.assertEquals(apiResponse.getString("product"), esbResponse.getString("product"));
        Assert.assertEquals(apiResponse.getString("component"), esbResponse.getString("component"));
        Assert.assertEquals(apiResponse.getString("summary"), esbResponse.getString("summary"));  
    }
    
    /**
     * Positive test case for searchBugs method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {searchBugs} integration test with optional parameters.", dependsOnMethods = {
            "testCreateBugWithMandatoryParameters", "testCreateBugWithOptionalParameters" })
    public void testSearchBugsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchBugs");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchBugs_optional.json");
        
        JSONObject esbResponse = esbRestResponse.getBody().getJSONArray("bugs").getJSONObject(0);
        
        final String apiEndpoint =
                apiEndpointUrl + "/bug" + authString + "&op_sys=" + connectorProperties.getProperty("bugOPSys")
                        + "&platform=" + connectorProperties.getProperty("bugPlatform") + "&severity="
                        + connectorProperties.getProperty("bugSeverity") + "&priority="
                        + connectorProperties.getProperty("bugPriority") + "&version="
                        + connectorProperties.getProperty("productVersion");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponse = apiRestResponse.getBody().getJSONArray("bugs").getJSONObject(0);
        
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("bugs").length(), esbRestResponse.getBody()
                .getJSONArray("bugs").length());
        Assert.assertEquals(apiResponse.getString("id"), esbResponse.getString("id"));
        Assert.assertEquals(apiResponse.getString("product"), esbResponse.getString("product"));
        Assert.assertEquals(apiResponse.getString("component"), esbResponse.getString("component"));
        Assert.assertEquals(apiResponse.getString("summary"), esbResponse.getString("summary"));  
    }
    
    /**
     * Negative test case for searchBugs method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {searchBugs} integration test with negative parameters.")
    public void testSearchBugsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchBugs");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchBugs_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/bug" + authString + "&creation_time=Invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getString("error"), esbRestResponse.getBody().getString("error"));
        Assert.assertEquals(apiRestResponse.getBody().getString("message"),
                esbRestResponse.getBody().getString("message"));
        Assert.assertEquals(apiRestResponse.getBody().getString("code"), esbRestResponse.getBody().getString("code"));  
    }
    
    /**
     * Positive test case for updateBug method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {updateBug} integration test with optional parameters.", dependsOnMethods = {
            "testCreateBugWithOptionalParameters", "testCreateUserWithMandatoryParameters" })
    public void testUpdateBugWithOptionalParameters() throws IOException, JSONException {
    
        final String apiEndpoint = apiEndpointUrl + "/bug/" + connectorProperties.getProperty("bugIdOpt") + authString;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponseBeforeUpdate = apiRestResponse.getBody().getJSONArray("bugs").getJSONObject(0);
        
        esbRequestHeadersMap.put("Action", "urn:updateBug");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateBug_optional.json");
        apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        JSONObject apiResponseAfterUpdate = apiRestResponse.getBody().getJSONArray("bugs").getJSONObject(0);
        
        Assert.assertNotEquals(apiResponseBeforeUpdate.get("summary"), apiResponseAfterUpdate.get("summary"));
        Assert.assertNotEquals(apiResponseBeforeUpdate.get("deadline"), apiResponseAfterUpdate.get("deadline"));
        Assert.assertNotEquals(apiResponseBeforeUpdate.get("whiteboard"), apiResponseAfterUpdate.get("whiteboard"));
        Assert.assertNotEquals(apiResponseBeforeUpdate.get("url"), apiResponseAfterUpdate.get("url"));
    }
    
    /**
     * Negative test case for updateBug method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "bugzilla {updateBug} integration test with  negative case.")
    public void testUpdateBugWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateBug");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateBug_negative.json");
        
        JSONObject esbResponse = esbRestResponse.getBody();
        
        final String apiEndpoint = apiEndpointUrl + "/bug/" + connectorProperties.getProperty("bugId") + authString;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap, "api_updateBug_negative.json");
        
        JSONObject apiResponse = apiRestResponse.getBody();
        
        Assert.assertEquals(apiResponse.get("code"), esbResponse.get("code"));
        Assert.assertEquals(apiResponse.get("error"), esbResponse.get("error"));
        Assert.assertEquals(apiResponse.get("message"), esbResponse.get("message"));
    }
    
}
