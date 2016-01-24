package org.wso2.carbon.connector.integration.test.fhir;
/*
 *
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 * /
 */

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class fhirConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    private String multipartProxyUrl;

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        log.info("pass");
        init("fhir-connector-1.0.0");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");

        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Content-Type", "application/json");

    }

    /**
     * Positive test case for readResource method with mandatory parameters.
     */
    @Test(enabled = true, description = "fhir {readResource} integration test with mandatory parameters.")
    public void readResourceWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "readResource";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "readResourceMandatory.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/" + connectorProperties.getProperty("type");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for readResource method with optional parameters.
     */
    @Test(enabled = true, description = "fhir {readResource} integration test with optional parameters.")
    public void readResourceWithOptionalParameters() throws IOException, JSONException {
        String methodName = "readResource";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "readResourceOptional.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/" + connectorProperties.getProperty("type") + "?_format=" + connectorProperties.getProperty("format");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entry").getJSONObject(0).getString("title"),
                apiRestResponse.getBody().getJSONArray("entry").getJSONObject(0).getString("title"));
    }

    /**
     * Negative test case for readResource method.
     */
    @Test(enabled = true, description = "fhir {readResource} integration test in negative case.")
    public void readResourceWithNegativeCase() throws IOException, JSONException {
        String methodName = "readResource";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "readResourceNegative.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/" + connectorProperties.getProperty("wrongType") + "?_format=" + connectorProperties.getProperty("format");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getConformance method with mandatory parameters.
     */
    @Test(enabled = true, description = "fhir {getConformance} integration test with mandatory parameters.")
    public void getConformanceWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "getConformance";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getConformanceMandatory.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/metadata";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for getConformance method with optional parameters.
     */
    @Test(enabled = true, description = "fhir {getConformance} integration test with optional parameters.")
    public void getConformanceWithOptionalParameters() throws IOException, JSONException {
        String methodName = "getConformance";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getConformanceOptional.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/metadata" + "?_format=" + connectorProperties.getProperty("format");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for readSpecificResourceById method with optional parameters.
     */
    @Test(enabled = true, description = "fhir {readSpecificResourceById} integration test with optional parameters.")
    public void readSpecificResourceByIdWithOptionalParameters() throws IOException, JSONException {
        String methodName = "readSpecificResourceById";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "readSpecificResourceByIdOptional.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/" + connectorProperties.getProperty("type") + "/" + connectorProperties.getProperty("id") + "?_format=" + connectorProperties.getProperty("format");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("name").getJSONObject(0).getString("use"),
                apiRestResponse.getBody().getJSONArray("name").getJSONObject(0).getString("use"));
    }

    /**
     * Negative test case for readSpecificResourceById method.
     */
    @Test(enabled = true, description = "fhir {readSpecificResourceById} integration test for negative parameters.")
    public void readSpecificResourceByIdWithNegativeParameters() throws IOException, JSONException {
        String methodName = "readSpecificResourceById";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "readSpecificResourceByIdNegative.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/" + connectorProperties.getProperty("type") + "/" + connectorProperties.getProperty("wrongId") + "?_format=" + connectorProperties.getProperty("format");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for vReadResource method with mandatory parameters.
     */
    @Test(enabled = true, description = "fhir {vReadResource} integration test with mandatory parameters.")
    public void vReadResourceWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "vReadResource";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "vReadResourceMandatory.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/" + connectorProperties.getProperty("type") + "/" + connectorProperties.getProperty("logicalId") + "/_history/" + connectorProperties.getProperty("versionId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for vReadResource method with optional parameters.
     */
    @Test(enabled = true, description = "fhir {vReadResource} integration test with optional parameters.")
    public void vReadResourceWithOptionalParameters() throws IOException, JSONException {
        String methodName = "vReadResource";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "vReadResourceOptional.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/" + connectorProperties.getProperty("type") + "/" + connectorProperties.getProperty("logicalId") + "/_history/" + connectorProperties.getProperty("versionId") + "?_format=" + connectorProperties.getProperty("format");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("name").getJSONObject(0).getString("use"),
                apiRestResponse.getBody().getJSONArray("name").getJSONObject(0).getString("use"));
    }

    /**
     * Negative test case for vReadResource method.
     */
    @Test(enabled = true, description = "fhir {vReadResource} integration test for negative parameters.")
    public void vReadResourceWithNegativeParameters() throws IOException, JSONException {
        String methodName = "vReadResource";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "vReadResourceNegative.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/" + connectorProperties.getProperty("type") + "/" + connectorProperties.getProperty("logicalId") + "/_history/" + connectorProperties.getProperty("wrongVersionId") + "?_format=" + connectorProperties.getProperty("format");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for search method with mandatory parameters.
     */
    @Test(enabled = true, description = "fhir {search} integration test with mandatory parameters.")
    public void searchWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "search";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "searchMandatory.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/" + connectorProperties.getProperty("type");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for search method with optional parameters.
     */
    @Test(enabled = true, description = "fhir {search} integration test with optional parameters.")
    public void searchWithOptionalParameters() throws IOException, JSONException {
        String methodName = "search";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "searchOptional.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/" + connectorProperties.getProperty("type") + "?_format=" + connectorProperties.getProperty("format");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Negative test case for search method.
     */
    @Test(enabled = true, description = "fhir {search} integration test for negative parameters.")
    public void searchWithNegativeParameters() throws IOException, JSONException {
        String methodName = "search";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "searchNegative.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/" + connectorProperties.getProperty("wrongType") + "?_format=" + connectorProperties.getProperty("format");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for searchPost method with mandatory parameters.
     */
    @Test(enabled = true, description = "fhir {searchPost} integration test with mandatory parameters.")
    public void searchPostWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "searchPost";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "searchMandatory.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/" + connectorProperties.getProperty("type") + "/_search";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for searchPost method with optional parameters.
     */
    @Test(enabled = true, description = "fhir {searchPost} integration test with optional parameters.")
    public void searchPostWithOptionalParameters() throws IOException, JSONException {
        String methodName = "searchPost";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "searchOptional.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/" + connectorProperties.getProperty("type") + "/_search?_format=" + connectorProperties.getProperty("format");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Negative test case for searchPost method.
     */
    @Test(enabled = true, description = "fhir {searchPost} integration test for negative parameters.")
    public void searchPostWithNegativeParameters() throws IOException, JSONException {
        String methodName = "searchPost";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "searchNegative.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/" + connectorProperties.getProperty("wrongType") + "/_search?_format=" + connectorProperties.getProperty("format");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for history method with mandatory parameters.
     */
    @Test(enabled = true, description = "fhir {history} integration test with mandatory parameters.")
    public void historyWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "history";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "historyMandatory.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/" + connectorProperties.getProperty("type") + "/" + connectorProperties.getProperty("idForHistory") + "/_history";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for history method with optional parameters.
     */
    @Test(enabled = true, description = "fhir {history} integration test with optional parameters.")
    public void historyWithOptionalParameters() throws IOException, JSONException {
        String methodName = "history";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "historyOptional.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/" + connectorProperties.getProperty("type") + "/" + connectorProperties.getProperty("idForHistory") + "/_history?_format=" + connectorProperties.getProperty("format");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entry").getJSONObject(0).getString("title"),
                apiRestResponse.getBody().getJSONArray("entry").getJSONObject(0).getString("title"));
    }

    /**
     * Negative test case for history method.
     */
    @Test(enabled = true, description = "fhir {history} integration test for negative parameters.")
    public void historyWithNegativeParameters() throws IOException, JSONException {
        String methodName = "history";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "historyNegative.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/" + connectorProperties.getProperty("type") + "/" + connectorProperties.getProperty("wrongId") + "/_history?_format=" + connectorProperties.getProperty("format");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for historyAll method with mandatory parameters.
     */
    @Test(enabled = true, description = "fhir {historyAll} integration test with mandatory parameters.")
    public void historyAllWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "historyAll";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "historyAllMandatory.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/_history";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for historyAll method with optional parameters.
     */
    @Test(enabled = true, description = "fhir {historyAll} integration test with optional parameters.")
    public void historyAllWithOptionalParameters() throws IOException, JSONException {
        String methodName = "historyAll";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "historyAllOptional.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/_history?_format=" + connectorProperties.getProperty("format");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entry").getJSONObject(0).getString("title"),
                apiRestResponse.getBody().getJSONArray("entry").getJSONObject(0).getString("title"));
    }

    /**
     * Positive test case for historyType method with mandatory parameters.
     */
    @Test(enabled = true, description = "fhir {historyType} integration test with mandatory parameters.")
    public void historyTypeWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "historyType";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "historyTypeMandatory.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/" + connectorProperties.getProperty("type") + "/_history";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for historyType method with optional parameters.
     */
    @Test(enabled = true, description = "fhir {historyType} integration test with optional parameters.")
    public void historyTypeWithOptionalParameters() throws IOException, JSONException {
        String methodName = "historyType";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "historyTypeOptional.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/" + connectorProperties.getProperty("type") + "/_history?_format=" + connectorProperties.getProperty("format");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entry").getJSONObject(0).getString("title"),
                apiRestResponse.getBody().getJSONArray("entry").getJSONObject(0).getString("title"));
    }

    /**
     * Negative test case for historyType method.
     */
    @Test(enabled = true, description = "fhir {historyType} integration test for negative parameters.")
    public void historyTypeWithNegativeParameters() throws IOException, JSONException {
        String methodName = "historyType";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "historyTypeNegative.json");
        final String apiEndPoint = connectorProperties.getProperty("base") + "/" + connectorProperties.getProperty("wrongType") + "/_history?_format=" + connectorProperties.getProperty("format");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
    }
}