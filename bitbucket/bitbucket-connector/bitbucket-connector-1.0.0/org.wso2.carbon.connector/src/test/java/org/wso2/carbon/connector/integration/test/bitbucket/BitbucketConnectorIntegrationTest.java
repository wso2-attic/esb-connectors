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

package org.wso2.carbon.connector.integration.test.bitbucket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class BitbucketConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private long timeOut;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("bitbucket-connector-1.0.0");
        
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        timeOut = Long.parseLong(connectorProperties.getProperty("timeOut"));
        // Create base64-encoded auth string from using username and password
        
        final String authString =
                connectorProperties.getProperty("username") + ":" + connectorProperties.getProperty("password");
        final String base64AuthString = Base64.encode(authString.getBytes());
        
        apiRequestHeadersMap.put("Authorization", "Basic " + base64AuthString);
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
    }
    
    /**
     * Positive test case for createRepository method with mandatory parameters.
     */
    @Test(priority = 1, description = "bitbucket {createRepository} integration test with mandatory parameters.")
    public void testCreateRepositoryWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createRepository");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/"
                        + connectorProperties.getProperty("repoSlugMandatory");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRepository_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("scm"), apiRestResponse.getBody().get("scm"));
        Assert.assertEquals(esbRestResponse.getBody().get("is_private"), apiRestResponse.getBody().get("is_private"));
        connectorProperties.put("repositoryName", esbRestResponse.getBody().get("name").toString());
        
    }
    
    /**
     * Positive test case for createRepository method with optional parameters.
     */
    @Test(priority = 1, description = "bitbucket {createRepository} integration test with optional parameters.")
    public void testCreateRepositoryWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createRepository");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/"
                        + connectorProperties.getProperty("repoSlugOptional");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRepository_optional.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("description"), apiRestResponse.getBody().get("description"));
        Assert.assertEquals(esbRestResponse.getBody().get("is_private"), apiRestResponse.getBody().get("is_private"));
        
    }
    
    /**
     * Negative test case for createRepository method.
     * 
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateRepositoryWithMandatoryParameters" }, description = "bitbucket {createRepository} integration test negative case.")
    public void testCreateRepositoryWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createRepository");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/"
                        + connectorProperties.getProperty("repoSlugMandatory");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRepository_negative.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error")
                .toString());
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for getRepository method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateRepositoryWithNegativeCase" }, description = "bitbucket {getRepository} integration test with mandatory parameters.")
    public void testGetRepositoryWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getRepository");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/"
                        + connectorProperties.getProperty("repoSlugMandatory");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRepository_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("scm"), apiRestResponse.getBody().get("scm"));
        Assert.assertEquals(esbRestResponse.getBody().get("is_private"), apiRestResponse.getBody().get("is_private"));
        connectorProperties.put("repositoryName", esbRestResponse.getBody().get("name").toString());
        
    }
    
    /**
     * Negative test case for getRepository method.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetRepositoryWithMandatoryParameters" }, description = "bitbucket {getRepository} integration test negative case.")
    public void testGetRepositoryWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getRepository");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/invalid";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRepository_negative.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error")
                .toString());
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for createPullRequest method with mandatory parameters.
     */
    @Test(priority = 1, description = "bitbucket {createPullRequest} integration test with mandatory parameters.")
    public void testCreatePullRequestWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPullRequest");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPullRequest_mandatory.json");
        
        String pullRequestId = esbRestResponse.getBody().get("id").toString();
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/pullrequests/" + pullRequestId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        
        Assert.assertEquals(esbRestResponse.getBody().get("title"), apiRestResponse.getBody().get("title"));
        Assert.assertEquals(esbRestResponse.getBody().get("close_source_branch"),
                apiRestResponse.getBody().get("close_source_branch"));
        Assert.assertEquals(esbRestResponse.getBody().get("created_on"), apiRestResponse.getBody().get("created_on"));
        connectorProperties.put("pullRequestIdMandatory", pullRequestId);
        
    }
    
    /**
     * Positive test case for createPullRequest method with optional parameters.
     */
    @Test(priority = 1, description = "bitbucket {createPullRequest} integration test with optional parameters.")
    public void testCreatePullRequestWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPullRequest");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPullRequest_optional.json");
        String pullRequestId2 = esbRestResponse.getBody().get("id").toString();
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/pullrequests/" + pullRequestId2;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        
        Assert.assertEquals(esbRestResponse.getBody().get("title"), apiRestResponse.getBody().get("title"));
        Assert.assertEquals(esbRestResponse.getBody().get("description"), apiRestResponse.getBody().get("description"));
        Assert.assertEquals(esbRestResponse.getBody().get("created_on"), apiRestResponse.getBody().get("created_on"));
        
    }
    
    /**
     * Negative test case for createPullRequest method.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreatePullRequestWithOptionalParameters" }, description = "bitbucket {createPullRequest} integration test with negative case.")
    public void testCreatePullRequestWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPullRequest");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/pullrequests";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPullRequest_negative.json");
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createPullRequest_negative.json");
        
        String esbErrorMessage = esbRestResponse.getBody().getJSONObject("error").getString("message");
        String apiErrorMessage = apiRestResponse.getBody().getJSONObject("error").getString("message");
        
        Assert.assertEquals(esbErrorMessage, apiErrorMessage);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for getPullRequest method with mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = { "testCreatePullRequestWithMandatoryParameters" }, description = "bitbucket {getPullRequest} integration test with mandatory parameters.")
    public void testGetPullRequestWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getPullRequest");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/pullrequests/" + connectorProperties.getProperty("pullRequestIdMandatory");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPullRequest_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("title"), apiRestResponse.getBody().get("title"));
        Assert.assertEquals(esbRestResponse.getBody().get("state"), apiRestResponse.getBody().get("state"));
        Assert.assertEquals(esbRestResponse.getBody().get("created_on"), apiRestResponse.getBody().get("created_on"));
        Assert.assertEquals(esbRestResponse.getBody().get("updated_on"), apiRestResponse.getBody().get("updated_on"));
        
    }
    
    /**
     * Negative test case for getPullRequest method.
     */
    @Test(priority = 2, description = "bitbucket {getPullRequest} integration test with negative case.")
    public void testGetPullRequestWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getPullRequest");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/pullrequests/xxx";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPullRequest_negative.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String esbErrorMessage = esbRestResponse.getBody().getJSONObject("error").getString("message");
        String apiErrorMessage = apiRestResponse.getBody().getJSONObject("error").getString("message");
        
        Assert.assertEquals(esbErrorMessage, apiErrorMessage);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for listPullRequest method with mandatory parameters.
     */
    @Test(priority = 1, description = "bitbucket {listPullRequest} integration test with mandatory parameters.")
    public void testListPullRequestWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listPullRequest");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/pullrequests";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPullRequest_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbValuesArray = esbRestResponse.getBody().getJSONArray("values");
        JSONArray apiValuesArray = apiRestResponse.getBody().getJSONArray("values");
        
        Assert.assertEquals(esbRestResponse.getBody().get("pagelen"), apiRestResponse.getBody().get("pagelen"));
        Assert.assertEquals(esbRestResponse.getBody().get("size"), apiRestResponse.getBody().get("size"));
        Assert.assertEquals(esbValuesArray.getJSONObject(0).getString("description"), apiValuesArray.getJSONObject(0).getString("description"));
        Assert.assertEquals(esbValuesArray.getJSONObject(0).getString("id"), apiValuesArray.getJSONObject(0).getString("id"));
    }
    
    /**
     * Positive test case for listPullRequest method with optional parameters.
     */
    @Test(priority = 1, description = "bitbucket {listPullRequest} integration test with optional parameters.")
    public void testListPullRequestWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listPullRequest");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/pullrequests?state=OPEN";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPullRequest_optional.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbValuesArray = esbRestResponse.getBody().getJSONArray("values");
        JSONArray apiValuesArray = apiRestResponse.getBody().getJSONArray("values");
        
        Assert.assertEquals(esbRestResponse.getBody().get("pagelen"), apiRestResponse.getBody().get("pagelen"));
        Assert.assertEquals(esbRestResponse.getBody().get("size"), apiRestResponse.getBody().get("size"));
        Assert.assertEquals(esbValuesArray.getJSONObject(0).getString("description"), apiValuesArray.getJSONObject(0).getString("description"));
        Assert.assertEquals(esbValuesArray.getJSONObject(0).getString("state"), apiValuesArray.getJSONObject(0).getString("state"));
    }
    
    /**
     * Positive test case for updatePullRequest method with mandatory parameters.
     */
    @Test(priority = 1, description = "bitbucket {updatePullRequest} integration test with mandatory parameters.")
    public void testUpdatePullRequestWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updatePullRequest");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/pullrequests/" + connectorProperties.getProperty("pullRequestIdComment");
        
        RestResponse<JSONObject> apiRestResponse1 = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updatePullRequest_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponse2 = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("updatePullRequestMandarotyTitle"), apiRestResponse2.getBody().getString("title"));
        Assert.assertNotEquals(apiRestResponse1.getBody().getString("title"), apiRestResponse2.getBody().getString("title"));
        
    }
    
    /**
     * Positive test case for updatePullRequest method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testUpdatePullRequestWithMandatoryParameters" }, description = "bitbucket {updatePullRequest} integration test with optional parameters.")
    public void testUpdatePullRequestWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updatePullRequest");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/pullrequests/" + connectorProperties.getProperty("pullRequestIdComment");
        
        RestResponse<JSONObject> apiRestResponse1 = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updatePullRequest_optional.json");
        RestResponse<JSONObject> apiRestResponse2 = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponse1.getBody().getString("description"), apiRestResponse2.getBody().getString("description"));
        Assert.assertNotEquals(apiRestResponse1.getBody().getJSONObject("destination").getJSONObject("branch").getString("name"), apiRestResponse2.getBody().getJSONObject("destination").getJSONObject("branch").getString("name"));
        
    }
    
    /**
     * Negative test case for updatePullRequest method.
     */
    @Test(priority = 1, dependsOnMethods = { "testUpdatePullRequestWithOptionalParameters" }, description = "bitbucket {updatePullRequest} integration test with negative case.")
    public void testUpdatePullRequestWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updatePullRequest");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/pullrequests/" + connectorProperties.getProperty("pullRequestIdComment");
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updatePullRequest_negative.json");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updatePullRequest_negative.json");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").getString("message"), esbRestResponse
                .getBody().getJSONObject("error").getString("message"));
        
    }
    
    /**
     * Positive test case for mergePullRequest method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetPullRequestDiffWithNegativeCase" }, description = "bitbucket {mergePullRequest} integration test with mandatory parameters.")
    public void testMergePullRequestWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:mergePullRequest");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/pullrequests/" + connectorProperties.getProperty("pullRequestId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getString("state"), "OPEN");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_mergePullRequest_mandatory.json");

        apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("state"), "MERGED");
        
    }
    
    /**
     * Positive test case for mergePullRequest method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testMergePullRequestWithMandatoryParameters" }, description = "bitbucket {mergePullRequest} integration test with optional parameters.")
    public void testMergePullRequestWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:mergePullRequest");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/pullrequests/" + connectorProperties.getProperty("pullRequestId1");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getString("state"), "OPEN");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_mergePullRequest_optional.json");
        
        apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("state"), "MERGED");
        Assert.assertFalse(apiRestResponse.getBody().getBoolean("close_source_branch"));
        
    }
    
    /**
     * Negative test case for mergePullRequest method with negative case.
     */
    @Test(priority = 1, dependsOnMethods = { "testMergePullRequestWithOptionalParameters" }, description = "bitbucket {mergePullRequest} integration test with negative case.")
    public void testMergePullRequestWithNegative() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:mergePullRequest");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/pullrequests/" + connectorProperties.getProperty("pullRequestId") + "/merge";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_mergePullRequest_negative.json");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_mergePullRequest_negative.json");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").getString("message"), esbRestResponse
                .getBody().getJSONObject("error").getString("message"));
        
    }
    
    /**
     * Positive test case for getPullRequestDiff method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetPullRequestWithMandatoryParameters" }, description = "bitbucket {getPullRequestDiff} integration test with mandatory parameters.")
    public void testGetPullRequestDiffWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getPullRequestDiff");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/pullrequests/" + connectorProperties.getProperty("pullRequestId") + "/diff";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPullRequestDiff_mandatory.json");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getHeadersMap().get("Location").get(0).toString(), esbRestResponse
                .getHeadersMap().get("Location").get(0).toString());
        
    }
    
    /**
     * Negative test case for getPullRequestDiff method.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetPullRequestDiffWithMandatoryParameters" }, description = "bitbucket {getPullRequestDiff} integration test with negative case.")
    public void testGetPullRequestDiffWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getPullRequestDiff");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/pullrequests/-1/diff";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPullRequestDiff_negative.json");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").getString("message"), esbRestResponse
                .getBody().getJSONObject("error").getString("message"));
        
    }
    
    /**
     * Positive test case for discardPullRequest method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testMergePullRequestWithNegative" }, description = "bitbucket {discardPullRequest} integration test with mandatory parameters.")
    public void testDiscardPullRequestWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:discardPullRequest");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/pullrequests/" + connectorProperties.getProperty("pullRequestId2");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getString("state"), "OPEN");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_discardPullRequest_mandatory.json");
        
        apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("state"), "DECLINED");
        
    }
    
    /**
     * Positive test case for discardPullRequest method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testDiscardPullRequestWithMandatoryParameters" }, description = "bitbucket {discardPullRequest} integration test with optional parameters.")
    public void testDiscardPullRequestWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:discardPullRequest");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/pullrequests/" + connectorProperties.getProperty("pullRequestId3");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getString("state"), "OPEN");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_discardPullRequest_optional.json");
        
        apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("state"), "DECLINED");
        
    }
    
    /**
     * Negative test case for mergePullRequest method with negative case.
     */
    @Test(priority = 1, dependsOnMethods = { "testDiscardPullRequestWithOptionalParameters" }, description = "bitbucket {discardPullRequest} integration test with negative case.")
    public void testDiscardPullRequestWithNegative() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:discardPullRequest");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/pullrequests/" + connectorProperties.getProperty("pullRequestId") + "/decline";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_discardPullRequest_negative.json");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_discardPullRequest_negative.json");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").getString("message"), esbRestResponse
                .getBody().getJSONObject("error").getString("message"));
        
    }
    
    /**
     * Positive test case for listCommits method with mandatory parameters.
     */
    @Test(priority = 1, description = "bitbucket {listCommits} integration test with mandatory parameters.")
    public void testListCommitsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCommits");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/commits/" + connectorProperties.getProperty("revision");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCommits_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("values");
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("values");
        
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("message").toString(),
                apiResponseArray.getJSONObject(0).get("message").toString());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("author").toString(),
                apiResponseArray.getJSONObject(0).get("author").toString());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("repository").toString(),
               apiResponseArray.getJSONObject(0).get("repository").toString());
        
    }
    
    /**
     * Positive test case for listCommits method with optional parameters.
     */
    @Test(priority = 1, description = "bitbucket {listCommits} integration test with optional parameters.")
    public void testListCommitsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCommits");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/commits/" + connectorProperties.getProperty("branchOrTag");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCommits_optional.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = (JSONArray) esbRestResponse.getBody().get("values");
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("values");
        
        for (int i = 0; i < esbResponseArray.length(); i++) {
            JSONObject element = (JSONObject) esbResponseArray.get(i);
            Assert.assertNotEquals(connectorProperties.getProperty("exclude"), element.get("hash"));
        }
        Assert.assertNotEquals(esbResponseArray.length(), apiResponseArray.length());
    }
    
    /**
     * Negative test case for listCommits method.
     */
    @Test(priority = 1, description = "bitbucket {listCommits} integration test negative case.")
    public void testListCommitsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCommits");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/commits/invalid";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCommits_negative.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error")
                .toString());
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for listCommitComments method with mandatory parameters.
     */
    @Test(priority = 1, description = "bitbucket {listCommitComments} integration test with mandatory parameters.")
    public void testListCommitCommentsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCommitComments");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/commit/" + connectorProperties.getProperty("commentRevisionId") + "/comments";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCommitComments_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbValuesArray = esbRestResponse.getBody().getJSONArray("values");
        JSONArray apiValuesArray = apiRestResponse.getBody().getJSONArray("values");
        
        Assert.assertEquals(esbValuesArray.getJSONObject(0).getJSONObject("content").getString("raw"), apiValuesArray.getJSONObject(0).getJSONObject("content").getString("raw"));
        Assert.assertEquals(esbValuesArray.length(), apiValuesArray.length());
        Assert.assertEquals(esbValuesArray.getJSONObject(0).getString("created_on"), apiValuesArray.getJSONObject(0).getString("created_on"));
        Assert.assertEquals(esbValuesArray.getJSONObject(0).getString("id"), apiValuesArray.getJSONObject(0).getString("id"));
    }
    
    /**
     * Negative test case for listCommitComments method.
     */
    @Test(priority = 1, description = "bitbucket {listCommitComments} integration test negative case.")
    public void testListCommitCommentsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCommitComments");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/commit/xxxx/comments";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCommitComments_negative.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String esbErrorMessage = esbRestResponse.getBody().getJSONObject("error").getString("message");
        String apiErrorMessage = apiRestResponse.getBody().getJSONObject("error").getString("message");
        
        Assert.assertEquals(esbErrorMessage, apiErrorMessage);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for listPullRequestComments method with mandatory parameters.
     */
    @Test(priority = 1, description = "bitbucket {listPullRequestComments} integration test with mandatory parameters.")
    public void testListPullRequestCommentsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listPullRequestComments");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/pullrequests/" + connectorProperties.getProperty("pullRequestIdComment") + "/comments";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_listPullRequestComments_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("values");
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("values");
        
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("id").toString(),
                apiResponseArray.getJSONObject(0).get("id").toString());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("content").toString(),
                apiResponseArray.getJSONObject(0).get("content").toString());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("created_on").toString(),
                apiResponseArray.getJSONObject(0).get("created_on").toString());
        
    }
    
    /**
     * Negative test case for listPullRequestComments method.
     */
    @Test(priority = 1, description = "bitbucket {listPullRequestComments} integration test negative case.")
    public void testListPullRequestCommentsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listPullRequestComments");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/" + connectorProperties.getProperty("repoSlug")
                        + "/pullrequests/-1/comments";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPullRequestComments_negative.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error")
                .toString());
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for deleteRepository method with mandatory parameters.
     * 
     */
    @Test(priority = 1, dependsOnMethods = { "testGetRepositoryWithMandatoryParameters" }, description = "bitbucket {deleteRepository} integration test with mandatory parameters.")
    public void testdeleteRepositoryWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteRepository");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/"
                        + connectorProperties.getProperty("repoSlugMandatory");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteRepository_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Negative test case for deleteRepository method.
     * 
     */
    @Test(priority = 1, dependsOnMethods = { "testdeleteRepositoryWithMandatoryParameters" }, description = "bitbucket {deleteRepository} integration test with negative case.")
    public void testDeleteRepositoryWithNegativeCase() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteRepository");
        Thread.sleep(timeOut);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/2.0/repositories/"
                        + connectorProperties.getProperty("owner") + "/xxxx";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteRepository_negative.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        String esbErrorMessage = esbRestResponse.getBody().getJSONObject("error").getString("message");
        String apiErrorMessage = apiRestResponse.getBody().getJSONObject("error").getString("message");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbErrorMessage, apiErrorMessage);
    }
    
}
