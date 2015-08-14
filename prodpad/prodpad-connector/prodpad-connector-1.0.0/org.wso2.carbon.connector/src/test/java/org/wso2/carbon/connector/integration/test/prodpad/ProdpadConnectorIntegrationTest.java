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

package org.wso2.carbon.connector.integration.test.prodpad;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.util.URLEncoder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class ProdpadConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap;
    
    private Map<String, String> apiRequestHeadersMap;
    
    private String apiEndpointUrl;
    
    private String apiKey;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("prodpad-connector-1.0.0");
        
        esbRequestHeadersMap = new HashMap<String, String>();
        
        apiRequestHeadersMap = new HashMap<String, String>();
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        apiEndpointUrl = connectorProperties.getProperty("apiUrl") + "/v1";
        apiKey = connectorProperties.getProperty("apiKey");
        setProperties();
        
    }
    
    /**
     * Setting the parameters to connector properties file.
     * 
     * @throws JSONException
     * @throws IOException
     */
    public void setProperties() throws IOException, JSONException {
    
        String apiEndpoint = apiEndpointUrl + "/statuses?apikey=" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        JSONArray apiResultArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        connectorProperties.put("statusId", apiResultArray.getJSONObject(0).getString("id"));
        connectorProperties.put("ideaStatus", apiResultArray.getJSONObject(1).getString("status"));
        connectorProperties.put("statusIdUpdated", apiResultArray.getJSONObject(1).getString("id"));
        
        apiEndpoint = apiEndpointUrl + "/users?apikey=" + apiKey;
        apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        apiResultArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        connectorProperties.put("userId", apiResultArray.getJSONObject(0).getString("id"));
        
        apiEndpoint = apiEndpointUrl + "/personas?apikey=" + apiKey;
        apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        if (apiRestResponse.getHttpStatusCode() == 404) {
            Assert.fail("Pre-requisites are not compleatd. Please create a persona.");
        }
        apiResultArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        connectorProperties.put("personaId", apiResultArray.getJSONObject(0).getString("id"));
        connectorProperties.put("persona", apiResultArray.getJSONObject(0).getString("name"));
        
        apiEndpoint = apiEndpointUrl + "/tags?apikey=" + apiKey;
        apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        apiResultArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        connectorProperties.put("tags", apiResultArray.getJSONObject(0).getString("id"));
        
        apiEndpoint = apiEndpointUrl + "/products?apikey=" + apiKey + "&group=true";
        apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        apiResultArray = apiRestResponse.getBody().getJSONArray("products");
        if (apiResultArray.length() == 0) {
            Assert.fail("Pre-requisites are not compleatd. Please create a product.");
        }
        apiResultArray = apiRestResponse.getBody().getJSONArray("productlines");
        if (apiResultArray.length() == 0) {
            Assert.fail("Pre-requisites are not compleatd. Please create a product line.");
        }
        apiResultArray = apiRestResponse.getBody().getJSONArray("products");
        connectorProperties.put("product", apiResultArray.getJSONObject(0).getString("name"));
        
    }
    
    /**
     * Positive test case for listProducts method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "prodpad {listProducts} integration test with mandatory parameters.")
    public void testListProductsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listProducts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProducts_mandatory.json");
        JSONArray esbOutputArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndpoint = apiEndpointUrl + "/products?apikey=" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        JSONArray apiOutputArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbOutputArray.length(), apiOutputArray.length());
        connectorProperties.put("productId", esbOutputArray.getJSONObject(0).getString("id"));
        Assert.assertEquals(esbOutputArray.getJSONObject(0).getString("id"),
                apiOutputArray.getJSONObject(0).getString("id"));
        Assert.assertEquals(esbOutputArray.getJSONObject(0).getString("name"), apiOutputArray.getJSONObject(0)
                .getString("name"));
        Assert.assertEquals(esbOutputArray.getJSONObject(0).getString("description"), apiOutputArray.getJSONObject(0)
                .getString("description"));
        Assert.assertEquals(esbOutputArray.getJSONObject(0).getString("created_at"), apiOutputArray.getJSONObject(0)
                .getString("created_at"));
        
    }
    
    /**
     * Positive test case for listProducts method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "prodpad {listProducts} integration test with optional parameters.")
    public void testListProductsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listProducts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProducts_optional.json");
        JSONArray esbProductArray = esbRestResponse.getBody().getJSONArray("products");
        JSONArray esbProductLineArray = esbRestResponse.getBody().getJSONArray("productlines");
        
        final String apiEndpoint = apiEndpointUrl + "/products?apikey=" + apiKey + "&group=true";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        JSONArray apiProductArray = apiRestResponse.getBody().getJSONArray("products");
        JSONArray apiProductLineArray = apiRestResponse.getBody().getJSONArray("productlines");
        
        Assert.assertEquals(esbProductArray.length(), apiProductArray.length());
        Assert.assertEquals(esbProductLineArray.length(), apiProductLineArray.length());
        Assert.assertEquals(esbProductLineArray.getJSONObject(0).getString("id"), apiProductLineArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbProductLineArray.getJSONObject(0).getString("name"), apiProductLineArray
                .getJSONObject(0).getString("name"));
        
    }
    
    /**
     * Method Name: listProducts Skipped Case: negative case Reason: No parameter(s) to test negative case.
     */
    
    /**
     * Positive test case for getProduct method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListProductsWithMandatoryParameters" }, description = "prodpad {getProduct} integration test with mandatory parameters.")
    public void testGetProductWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getProduct");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProduct_mandatory.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/products/" + connectorProperties.getProperty("productId") + "?apikey=" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("description"),
                apiRestResponse.getBody().getString("description"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"),
                apiRestResponse.getBody().getString("created_at"));
        
    }
    
    /**
     * Positive test case for getProduct method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListProductsWithMandatoryParameters" }, description = "prodpad {getProduct} integration test with optional parameters.")
    public void testGetProductWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getProduct");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProduct_optional.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/products/" + connectorProperties.getProperty("productId") + "?apikey=" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertTrue(esbRestResponse.getBody().has("mockups"));
        Assert.assertFalse(apiRestResponse.getBody().has("mockups"));
        Assert.assertTrue(esbRestResponse.getBody().has("personas"));
        Assert.assertFalse(apiRestResponse.getBody().has("personas"));
        Assert.assertTrue(esbRestResponse.getBody().has("files"));
        Assert.assertFalse(apiRestResponse.getBody().has("files"));
        
    }
    
    /**
     * Negative test case for getProduct method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "prodpad {getProduct} integration test with negative case.")
    public void testGetProductWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getProduct");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProduct_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/products/INVALID?apikey=" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("success"),
                apiRestResponse.getBody().getString("success"));
        Assert.assertEquals(esbRestResponse.getBody().getString("developer_message"), apiRestResponse.getBody()
                .getString("developer_message"));
        Assert.assertEquals(esbRestResponse.getBody().getString("user_message"),
                apiRestResponse.getBody().getString("user_message"));
        
    }
    
    /**
     * Positive test case for createIdea method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "prodpad {createIdea} integration test with mandatory parameters.")
    public void testcreateIdeaWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createIdea");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createIdea_mandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        final String ideaId = esbRestResponse.getBody().getString("id");
        
        final String apiEndpoint = apiEndpointUrl + "/ideas/" + ideaId + "?apikey=" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("title"), connectorProperties.getProperty("title"));
        
    }
    
    /**
     * Positive test case for createIdea method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListProductsWithMandatoryParameters" }, description = "prodpad {createIdea} integration test with optional parameters.")
    public void testcreateIdeaWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createIdea");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createIdea_optional.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        final String ideaId = esbRestResponse.getBody().getString("id");
        JSONArray userStoryArray = esbRestResponse.getBody().getJSONArray("user_stories");
        JSONArray commentArray = esbRestResponse.getBody().getJSONArray("comments");
        JSONArray productArray = esbRestResponse.getBody().getJSONArray("products");
        final String userStoryId = userStoryArray.getJSONObject(0).getString("id");
        connectorProperties.setProperty("userStoryId", userStoryId);
        connectorProperties.setProperty("ideaId", ideaId);
        
        final String apiEndpoint = apiEndpointUrl + "/ideas/" + ideaId + "?apikey=" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("title"), connectorProperties.getProperty("title"));
        Assert.assertEquals(productArray.getJSONObject(0).getString("id"), connectorProperties.getProperty("productId"));
        Assert.assertEquals(commentArray.getJSONObject(0).getString("comment"),
                connectorProperties.getProperty("comment"));
        Assert.assertEquals(userStoryArray.getJSONObject(0).getString("story"),
                connectorProperties.getProperty("userStory"));
        Assert.assertEquals(userStoryArray.getJSONObject(0).getString("acceptance_criteria"),
                connectorProperties.getProperty("userStoryAcceptanceCriteria"));
        
    }
    
    /**
     * Negative test case for createIdea method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "prodpad {createIdea} integration test with negative case.")
    public void testCreateIdeaWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createIdea");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createIdea_negative.json");
        final String apiEndPoint = apiEndpointUrl + "/ideas?apikey=" + apiKey;
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createIdea_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("success"),
                apiRestResponse.getBody().getString("success"));
        Assert.assertEquals(esbRestResponse.getBody().getString("developer_message"), apiRestResponse.getBody()
                .getString("developer_message"));
        Assert.assertEquals(esbRestResponse.getBody().getString("user_message"),
                apiRestResponse.getBody().getString("user_message"));
        
    }
    
    /**
     * Positive test case for getIdea method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testcreateIdeaWithOptionalParameters" }, description = "prodpad {getIdea} integration test with mandatory parameters.")
    public void testGetIdeaWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getIdea");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getIdea_mandatory.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/ideas/" + connectorProperties.getProperty("ideaId") + "?apikey=" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("title"), apiRestResponse.getBody().getString("title"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"),
                apiRestResponse.getBody().getString("created_at"));
        
    }
    
    /**
     * Positive test case for getIdea method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testcreateIdeaWithOptionalParameters" }, description = "prodpad {getIdea} integration test with optional parameters.")
    public void testGetIdeaWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getIdea");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getIdea_optional.json");
        
        final String apiEndpoint =
                apiEndpointUrl + "/ideas/" + connectorProperties.getProperty("ideaId") + "?apikey=" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("title"), apiRestResponse.getBody().getString("title"));
        Assert.assertTrue(esbRestResponse.getBody().has("mockups"));
        Assert.assertFalse(apiRestResponse.getBody().has("mockups"));
        Assert.assertTrue(esbRestResponse.getBody().has("files"));
        Assert.assertFalse(apiRestResponse.getBody().has("files"));
        
    }
    
    /**
     * Negative test case for getIdea method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "prodpad {getIdea} integration test with negative case.")
    public void testGetIdeaWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getIdea");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getIdea_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/ideas/INVALID?apikey=" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("success"),
                apiRestResponse.getBody().getString("success"));
        Assert.assertEquals(esbRestResponse.getBody().getString("developer_message"), apiRestResponse.getBody()
                .getString("developer_message"));
        Assert.assertEquals(esbRestResponse.getBody().getString("user_message"),
                apiRestResponse.getBody().getString("user_message"));
        
    }
    
    /**
     * Positive test case for listIdeas method with mandatory parameters.
     * 
     * @throws IOException
     * @throws JSONException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateIdeaStatusWithOptionalParameters" }, description = "prodpad {listIdeas} integration test with mandatory parameters.")
    public void testListIdeasWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:listIdeas");
        Thread.sleep(20000);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listIdeas_mandatory.json");
        JSONArray esbIdeasArray = esbRestResponse.getBody().getJSONArray("ideas");
        
        final String apiEndPoint = apiEndpointUrl + "/ideas?apikey=" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiIdeasArray = apiRestResponse.getBody().getJSONArray("ideas");
        
        Assert.assertEquals(esbIdeasArray.length(), apiIdeasArray.length());
        Assert.assertEquals(esbIdeasArray.getJSONObject(0).getString("id"),
                apiIdeasArray.getJSONObject(0).getString("id"));
        Assert.assertEquals(esbIdeasArray.getJSONObject(0).getString("project_id"), apiIdeasArray.getJSONObject(0)
                .getString("project_id"));
        Assert.assertEquals(esbIdeasArray.getJSONObject(0).getString("title"), apiIdeasArray.getJSONObject(0)
                .getString("title"));
        Assert.assertEquals(esbIdeasArray.getJSONObject(0).getJSONObject("account").getString("id"), apiIdeasArray
                .getJSONObject(0).getJSONObject("account").getString("id"));
    }
    
    /**
     * Positive test case for listIdeas method with optional parameters.
     * 
     * @throws IOException
     * @throws JSONException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateIdeaStatusWithOptionalParameters" }, description = "prodpad {listIdeas} integration test with optional parameters.")
    public void testListIdeasWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:listIdeas");
        Thread.sleep(20000);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listIdeas_optional.json");
        JSONArray esbIdeasArray = esbRestResponse.getBody().getJSONArray("ideas");
        
        URLEncoder encoder = new URLEncoder();
        final String persona = encoder.encode(connectorProperties.getProperty("persona"));
        final String status = encoder.encode(connectorProperties.getProperty("ideaStatus"));
        final String tags = encoder.encode(connectorProperties.getProperty("tags"));
        final String product = encoder.encode(connectorProperties.getProperty("product"));
        
        final String apiEndPoint =
                apiEndpointUrl + "/ideas?apikey=" + apiKey + "&page=1&tags=" + tags + "&product=" + product
                        + "&persona=" + persona + "&status=" + status;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiIdeasArray = apiRestResponse.getBody().getJSONArray("ideas");
        Assert.assertEquals(esbIdeasArray.length(), apiIdeasArray.length());
        Assert.assertEquals(esbIdeasArray.getJSONObject(0).getString("id"),
                apiIdeasArray.getJSONObject(0).getString("id"));
        Assert.assertEquals(esbIdeasArray.getJSONObject(0).getString("project_id"), apiIdeasArray.getJSONObject(0)
                .getString("project_id"));
        Assert.assertEquals(esbIdeasArray.getJSONObject(0).getString("title"), apiIdeasArray.getJSONObject(0)
                .getString("title"));
        Assert.assertEquals(esbIdeasArray.getJSONObject(0).getJSONObject("account").getString("id"), apiIdeasArray
                .getJSONObject(0).getJSONObject("account").getString("id"));
    }
    
    /**
     * Method Name: listIdeas Skipped Case: negative case Reason: No parameter(s) to test negative case.
     */
    
    /**
     * Positive test case for updateIdeaStatus method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testcreateIdeaWithOptionalParameters" }, description = "prodpad {updateIdeaStatus} integration test with mandatory parameters.")
    public void testUpdateIdeaStatusWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateIdeaStatus");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateIdeaStatus_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        final String ideaId = esbRestResponse.getBody().getString("id");
        
        final String apiEndpoint = apiEndpointUrl + "/ideas/" + ideaId + "?apikey=" + apiKey + "&expand=true";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("status").getString("id"),
                connectorProperties.getProperty("statusId"));
        
    }
    
    /**
     * Positive test case for updateIdeaStatus method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testcreateIdeaWithOptionalParameters" }, description = "prodpad {updateIdeaStatus} integration test with optional parameters.")
    public void testUpdateIdeaStatusWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateIdeaStatus");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateIdeaStatus_optional.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        String ideaId = esbRestResponse.getBody().getString("id");
        String commentId = esbRestResponse.getBody().getJSONObject("comments").getString("id");
        
        final String apiEndpoint = apiEndpointUrl + "/ideas/" + ideaId + "/comments?apikey=" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        JSONArray apiOutputArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        JSONObject commentObj = null;
        for (int i = 0; i < apiOutputArray.length(); i++) {
            commentObj = apiOutputArray.getJSONObject(i);
            if (commentId.equals(commentObj.getString("id"))) {
                break;
            }
        }
        Assert.assertEquals(commentObj.getString("comment"), connectorProperties.getProperty("comment"));
        Assert.assertEquals(commentObj.getJSONObject("status").getString("id"),
                connectorProperties.getProperty("statusIdUpdated"));
        Assert.assertEquals(commentObj.getJSONObject("created_by").getString("id"),
                connectorProperties.getProperty("userId"));
    }
    
    /**
     * Negative test case for updateIdeaStatus method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testcreateIdeaWithOptionalParameters" }, description = "prodpad {updateIdeaStatus} integration test with negative case.")
    public void testUpdateIdeaStatusWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateIdeaStatus");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateIdeaStatus_negative.json");
        
        final String apiEndPoint =
                apiEndpointUrl + "/ideas/" + connectorProperties.getProperty("ideaId") + "/statuses?apikey=" + apiKey;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateIdeaStatus_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("success"),
                apiRestResponse.getBody().getString("success"));
        Assert.assertEquals(esbRestResponse.getBody().getString("developer_message"), apiRestResponse.getBody()
                .getString("developer_message"));
        Assert.assertEquals(esbRestResponse.getBody().getString("user_message"),
                apiRestResponse.getBody().getString("user_message"));
        
    }
    
    /**
     * Positive test case for getIdeaComments method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testcreateIdeaWithOptionalParameters" }, description = "prodpad {getIdeaComments} integration test with mandatory parameters.")
    public void testGetIdeaCommentsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getIdeaComments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getIdeaComments_mandatory.json");
        JSONArray esbOutputArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndpoint =
                apiEndpointUrl + "/ideas/" + connectorProperties.getProperty("ideaId") + "/comments?apikey=" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        JSONArray apiOutputArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbOutputArray.length(), apiOutputArray.length());
        Assert.assertEquals(esbOutputArray.getJSONObject(0).getString("id"),
                apiOutputArray.getJSONObject(0).getString("id"));
        Assert.assertEquals(esbOutputArray.getJSONObject(0).getString("comment"), apiOutputArray.getJSONObject(0)
                .getString("comment"));
        Assert.assertEquals(esbOutputArray.getJSONObject(0).getString("created_at"), apiOutputArray.getJSONObject(0)
                .getString("created_at"));
        
    }
    
    /**
     * Method Name: getIdeaComments Skipped Case: optional case Reason: No optional parameter(s) to assert.
     */
    
    /**
     * Negative test case for getIdeaComments method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "prodpad {getIdeaComments} integration test with negative case.")
    public void testGetIdeaCommentsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getIdeaComments");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getIdeaComments_negative.json");
        
        final String apiEndPoint = apiEndpointUrl + "/ideas/INVALID/comments?apikey=" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("success"),
                apiRestResponse.getBody().getString("success"));
        Assert.assertEquals(esbRestResponse.getBody().getString("developer_message"), apiRestResponse.getBody()
                .getString("developer_message"));
        Assert.assertEquals(esbRestResponse.getBody().getString("user_message"),
                apiRestResponse.getBody().getString("user_message"));
        
    }
    
    /**
     * Positive test case for getIdeaUserStories method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testcreateIdeaWithOptionalParameters" }, description = "prodpad {getIdeaUserStories} integration test with mandatory parameters.")
    public void testGetIdeaUserStoriesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getIdeaUserStories");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getIdeaUserStories_mandatory.json");
        JSONArray esbOutputArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndpoint =
                apiEndpointUrl + "/ideas/" + connectorProperties.getProperty("ideaId") + "/userstories?apikey="
                        + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        JSONArray apiOutputArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbOutputArray.length(), apiOutputArray.length());
        Assert.assertEquals(esbOutputArray.getJSONObject(0).getString("id"),
                apiOutputArray.getJSONObject(0).getString("id"));
        Assert.assertEquals(esbOutputArray.getJSONObject(0).getString("story"), apiOutputArray.getJSONObject(0)
                .getString("story"));
        
    }
    
    /**
     * Method Name: getIdeaUserStories Skipped Case: optional case Reason: No optional parameter(s) to assert.
     */
    
    /**
     * Negative test case for getIdeaUserStories method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "prodpad {getIdeaUserStories} integration test with negative case.")
    public void testGetIdeaUserStoriesWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getIdeaUserStories");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getIdeaUserStories_negative.json");
        
        final String apiEndPoint = apiEndpointUrl + "/ideas/INVALID/userstories?apikey=" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("success"),
                apiRestResponse.getBody().getString("success"));
        Assert.assertEquals(esbRestResponse.getBody().getString("developer_message"), apiRestResponse.getBody()
                .getString("developer_message"));
        Assert.assertEquals(esbRestResponse.getBody().getString("user_message"),
                apiRestResponse.getBody().getString("user_message"));
        
    }
    
    /**
     * Positive test case for getUserStory method with mandatory parameters.
     * 
     * @throws IOException
     * @throws JSONException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testcreateIdeaWithOptionalParameters" }, description = "prodpad {getUserStory} integration test with mandatory parameters.")
    public void testGetUserStoryWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getUserStory");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserStory_mandatory.json");
        
        final String apiEndPoint =
                apiEndpointUrl + "/userstories/" + connectorProperties.getProperty("userStoryId") + "?apikey=" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("title"), apiRestResponse.getBody().getString("title"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"),
                apiRestResponse.getBody().getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("updated_at"),
                apiRestResponse.getBody().getString("updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("ideas").getString("id"), apiRestResponse.getBody()
                .getJSONObject("ideas").getString("id"));
    }
    
    /**
     * Method Name: getUserStory Skipped Case: optional case Reason: No optional parameter(s) to assert.
     */
    
    /**
     * Negative test case for getUserStory method.
     */
    @Test(groups = { "wso2.esb" }, description = "prodpad {getUserStory} integration test with negative case.")
    public void testGetUserStoryWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getUserStory");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserStory_negative.json");
        
        final String apiEndPoint = apiEndpointUrl + "/userstories/INVALID?apikey=" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getBoolean("success"),
                apiRestResponse.getBody().getBoolean("success"));
        Assert.assertEquals(esbRestResponse.getBody().getString("developer_message"), apiRestResponse.getBody()
                .getString("developer_message"));
        Assert.assertEquals(esbRestResponse.getBody().getString("user_message"),
                apiRestResponse.getBody().getString("user_message"));
    }
    
    /**
     * Positive test case for listUserStories method with mandatory parameters.
     * 
     * @throws IOException
     * @throws JSONException
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testcreateIdeaWithOptionalParameters" }, description = "prodpad {listUserStories} integration test with mandatory parameters.")
    public void testListUserStoriesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listUserStories");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUserStories_mandatory.json");
        JSONArray esbUserStoryArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndPoint = apiEndpointUrl + "/userstories?apikey=" + apiKey;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiUserStoryArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbUserStoryArray.length(), apiUserStoryArray.length());
        Assert.assertEquals(esbUserStoryArray.getJSONObject(0).getString("id"), apiUserStoryArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbUserStoryArray.getJSONObject(0).getString("title"), apiUserStoryArray.getJSONObject(0)
                .getString("title"));
        Assert.assertEquals(esbUserStoryArray.getJSONObject(0).getString("created_at"), apiUserStoryArray
                .getJSONObject(0).getString("created_at"));
        Assert.assertEquals(esbUserStoryArray.getJSONObject(0).getString("updated_at"), apiUserStoryArray
                .getJSONObject(0).getString("updated_at"));
    }
    
    /**
     * Method Name: listUserStories Skipped Case: optional case Reason: No optional parameter(s) to assert.
     */
    
    /**
     * Method Name: listUserStories Skipped Case: negative case Reason: No parameter(s) to test negative case.
     */
    
}
