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

package org.wso2.carbon.connector.integration.test.simplenote;

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

public class SimplenoteConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private String apiRequestUrl;
    
    private String authString;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        
        init("simplenote-connector-1.0.0");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        apiRequestUrl = connectorProperties.getProperty("apiUrl") + "/api2";        
        authString = "?auth=" + connectorProperties.getProperty("authToken") + "&email=" + connectorProperties.getProperty("email");
    }
    
    /**
     * Positive test case for createNote method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "Simplenote {createNote} integration test with mandatory parameters.")
    public void testCreateNoteWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createNote");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNote_mandatory.json");
        
        final String noteKey = esbRestResponse.getBody().getString("key");        
        connectorProperties.setProperty("noteKey", noteKey);
        
        final String apiEndPoint = apiRequestUrl + "/data/" + noteKey + authString;        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("createNoteMandatoryContent"),
                apiRestResponse.getBody().getString("content"));
        Assert.assertEquals(esbRestResponse.getBody().getString("deleted"),
                apiRestResponse.getBody().getString("deleted"));
        Assert.assertEquals(esbRestResponse.getBody().getString("modifydate"),
                apiRestResponse.getBody().getString("modifydate"));
    }
    
    /**
     * Positive test case for createNote method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "Simplenote {createNote} integration test with optional parameters.")
    public void testCreateNoteWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createNote");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNote_optional.json");        
        final String noteKeyOptional = esbRestResponse.getBody().getString("key");
        
        final String apiEndPoint = apiRequestUrl + "/data/" + noteKeyOptional + authString;        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("createNoteOptionalContent"),
                apiRestResponse.getBody().getString("content"));
        Assert.assertEquals(connectorProperties.getProperty("createNoteCreateDate"),
                apiRestResponse.getBody().getString("createdate"));
        Assert.assertEquals(esbRestResponse.getBody().getString("deleted"),
                apiRestResponse.getBody().getString("deleted"));
    }
    
    /**
     * Positive test case for updateNote method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateNoteWithMandatoryParameters" },
            description = "Simplenote {updateNote} integration test with optional parameters.")
    public void testUpdateNoteWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateNote");        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateNote_optional.json");   
        
        final String apiEndPoint = apiRequestUrl + "/data/" + connectorProperties.getProperty("noteKey") + authString;        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("updateNoteOptionalContent"),
                apiRestResponse.getBody().getString("content"));
        Assert.assertEquals(connectorProperties.getProperty("updateNoteCreateDate"),
                apiRestResponse.getBody().getString("createdate"));
        Assert.assertEquals(esbRestResponse.getBody().getString("modifydate"),
                apiRestResponse.getBody().getString("modifydate"));
    }
    
    /**
     * Positive test case for getNote method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateNoteWithMandatoryParameters" },
            description = "Simplenote {getNote} integration test with mandatory parameters.")
    public void testGetNoteWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getNote");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getNote_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/data/" + connectorProperties.getProperty("noteKey") + authString;        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("key"),
                apiRestResponse.getBody().getString("key"));
        Assert.assertEquals(esbRestResponse.getBody().getString("createdate"),
                apiRestResponse.getBody().getString("createdate"));
        Assert.assertEquals(esbRestResponse.getBody().getString("modifydate"),
                apiRestResponse.getBody().getString("modifydate"));
    }
    
    /**
     * Positive test case for listNoteIndexes method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateNoteWithMandatoryParameters", "testCreateNoteWithOptionalParameters" },
            description = "Simplenote {listNoteIndexes} integration test with mandatory parameters.")
    public void testListNoteIndexesWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listNoteIndexes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listNoteIndexes_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/index" + authString;        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        // Assert Array Length.
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").length(), apiRestResponse.getBody()
                .getJSONArray("data").length());
        
        // Assert attributes of the first note returned.
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("key"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("key"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("createdate"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("createdate"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("modifydate"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("modifydate"));
    }
    
    /**
     * Positive test case for listNoteIndexes method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateNoteWithMandatoryParameters", "testCreateNoteWithOptionalParameters" },
            description = "Simplenote {listNoteIndexes} integration test with optional parameters.")
    public void testListNoteIndexesWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listNoteIndexes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listNoteIndexes_optional.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/index" + authString + "&length=" + connectorProperties.getProperty("length");        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        // Assert Array Length.
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").length(), Integer
                .parseInt(connectorProperties.getProperty("length")));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("data").length(), Integer
                .parseInt(connectorProperties.getProperty("length")));
        
        // Assert attributes of the first note returned.
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("key"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("key"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("createdate"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("createdate"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("modifydate"),
                apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).getString("modifydate"));
    }
    
    /**
     * Negative test case for listNoteIndexes method.
     */
    @Test(groups = { "wso2.esb" }, description = "Simplenote {listNoteIndexes} integration test with negative case.")
    public void testListNoteIndexesWithNegativeParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listNoteIndexes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listNoteIndexes_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/index" + authString + "&since=INVALID";        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        // Assert the status code (Nothing is returned in the body.)
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
    }
    
    /**
     * Positive test case for createTag method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "Simplenote {createTag} integration test with mandatory parameters.")
    public void testcreateTagWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createTag");        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTag_mandatory.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/tags/" + connectorProperties.getProperty("esbCreatetagNameMandatory") + authString;        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("esbCreatetagNameMandatory"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("index"),
                apiRestResponse.getBody().getString("index"));
        Assert.assertEquals(esbRestResponse.getBody().getString("version"),
                apiRestResponse.getBody().getString("version"));
    }
    
    /**
     * Positive test case for createTag method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "Simplenote {createTag} integration test with optional parameters.")
    public void testcreateTagWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createTag");        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTag_optional.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/tags/" + connectorProperties.getProperty("esbCreatetagNameOptional") + authString;        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("esbCreatetagNameOptional"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("esbIndexOptional"),
                apiRestResponse.getBody().getString("index"));
        Assert.assertEquals(esbRestResponse.getBody().getString("version"),
                apiRestResponse.getBody().getString("version"));
    }
    
    /**
     * Positive test case for getTag method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testcreateTagWithMandatoryParameters" },
            description = "Simplenote {getTag} integration test with mandatory parameters.")
    public void testGetTagWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getTag");        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTag_mandatory.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/tags/" + connectorProperties.getProperty("esbCreatetagNameMandatory") + authString;        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("name"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("index"),
                apiRestResponse.getBody().getString("index"));
        Assert.assertEquals(esbRestResponse.getBody().getString("version"),
                apiRestResponse.getBody().getString("version"));
    }
    
}
