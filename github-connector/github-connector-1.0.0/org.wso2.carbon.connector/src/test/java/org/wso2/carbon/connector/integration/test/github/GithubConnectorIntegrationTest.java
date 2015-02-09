/**
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

package org.wso2.carbon.connector.integration.test.github;

import java.io.IOException;
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

public class GithubConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    long timeOut;
    
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("github");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.put("Accept-Charset", "UTF-8");
        apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");
        apiRequestHeadersMap.put("Authorization", "token " + connectorProperties.getProperty("accessToken"));
        timeOut = Long.parseLong(connectorProperties.getProperty("timeOut"));
    }
    
    /**
     * Positive test case for createBlob method with mandatory parameters.
     */
    @Test(priority = 1, description = "github {createBlob} integration test with mandatory parameters.")
    public void testCreateBlobWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createBlob");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createBlob_mandatory.json");
        String blobSHA = esbRestResponse.getBody().get("sha").toString();
        if (blobSHA != null) {
            connectorProperties.put("blobSHA", blobSHA);
        }
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/git/blobs/" + blobSHA;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("sha"), apiRestResponse.getBody().get("sha"));
        Assert.assertEquals(esbRestResponse.getBody().get("url"), apiRestResponse.getBody().get("url"));
        Thread.sleep(timeOut);
        // This method will create Tag for getTag using newly created blob
        createTag();
    }
    
    /**
     * Negative test case for createBlob.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateBlobWithMandatoryParameters" }, description = "github {createBlob} integration test negative case.")
    public void testCreateBlobNegativeCase() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createBlob");
        
        Thread.sleep(timeOut);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createBlob_negative.json");
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/123wso2connector" + "/"
                        + connectorProperties.getProperty("repo") + "/git/blobs/";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", esbRequestHeadersMap, "api_createBlob_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("message"), esbRestResponse.getBody().get("message"));
    }
    
    /**
     * Positive test case for getBlob method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateBlobNegativeCase" }, description = "github {getBlob} integration test with mandatory parameters.")
    public void testGetBlobWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:getBlob");
        Thread.sleep(timeOut);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBlob_mandatory.json");
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/git/blobs/"
                        + connectorProperties.getProperty("blobSHA");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("sha"), apiRestResponse.getBody().get("sha"));
        Assert.assertEquals(esbRestResponse.getBody().get("size"), apiRestResponse.getBody().get("size"));
        Assert.assertEquals(esbRestResponse.getBody().get("url"), apiRestResponse.getBody().get("url"));
        
    }
    
    /**
     * Negative test case for getBlob method.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetBlobWithMandatoryParameters" }, description = "github {getBlob} integration test with negative case.")
    public void testGetBlobWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getBlob");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBlob_neagative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/git/blobs/Invalid";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        
    }
    
    /**
     * Positive test case for createCommit method with mandatory parameters
     */
    @Test(priority = 1, dependsOnMethods = { "testGetBlobWithNegativeCase" }, description = "github {createCommit} integration test with mandatory parameters.")
    public void testCreateCommitWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createCommit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCommit_mandatory.json");
        
        connectorProperties.put("commitSha", esbRestResponse.getBody().getJSONObject("tree").get("sha"));
        Thread.sleep(timeOut);
        String apiEndPoint = esbRestResponse.getBody().getJSONObject("tree").get("url").toString();
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("tree").get("url"),
                apiRestResponse.getBody().get("url"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("tree").get("sha"),
                apiRestResponse.getBody().get("sha"));
    }
    
    /**
     * Positive test case for createCommit method with optional parameters
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateCommitWithMandatoryParameters" }, description = "github {createCommit} integration test with optional parameters.")
    public void testCreateCommitWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createCommit");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCommit_optional.json");
        
        connectorProperties.put("commitSha", esbRestResponse.getBody().getJSONObject("commit").get("sha"));
        Thread.sleep(timeOut);
        
        String apiEndPoint = esbRestResponse.getBody().getJSONObject("commit").get("url").toString();
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("commit").get("url"), apiRestResponse.getBody()
                .get("url"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("commit").get("sha"), apiRestResponse.getBody()
                .get("sha"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("commit").get("author").toString(), apiRestResponse
                .getBody().getJSONObject("author").toString());
    }
    
    /**
     * Negative test case for createCommit method
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateCommitWithOptionalParameters" }, description = "github {createCommit} integration test negative case.")
    public void testCreateCommitNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCommit");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCommit_negative.json");
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/git/trees";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createCommit_negative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("message").toString(),
                apiRestResponse.getBody().get("message").toString());
    }
    
    /**
     * Positive test case for getCommit method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateCommitNegativeCase" }, description = "github {getCommit} integration test with mandatory parameters.")
    public void testGetCommitWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:getCommit");
        Thread.sleep(timeOut);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCommit_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/git/commits/"
                        + connectorProperties.getProperty("commitSha");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("url"), apiRestResponse.getBody().getString("url"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("committer").getString("name"), apiRestResponse
                .getBody().getJSONObject("committer").getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("author").getString("name"), apiRestResponse
                .getBody().getJSONObject("author").getString("name"));
    }
    
    /**
     * Positive test case for getCommit method negative case.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetCommitWithMandatoryParameters" }, description = "github {getCommit} integration test with negative case.")
    public void testGetCommitWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getCommit");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCommit_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/git/commits/Invalid";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        
    }
    
    /**
     * Positive test case for createIssue method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetCommitWithNegativeCase" }, description = "github {createIssue} integration test with mandatory parameters.")
    public void testCreateIssueWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createIssue");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createIssue_mandatory.json");
        
        String issueNumebr = esbRestResponse.getBody().getString("number");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/issues/" + issueNumebr;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().get("title"), connectorProperties.getProperty("issueTitle"));
        
    }
    
    /**
     * Positive test case for createIssue method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateIssueWithMandatoryParameters"}, description = "github {createIssue} integration test with optional parameters.")
    public void testCreateIssueWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createIssue");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createIssue_optional.json");
        
        String issueNumebr = esbRestResponse.getBody().getString("number");
        connectorProperties.put("issueNumber", esbRestResponse.getBody().getString("number").toString());
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/issues/" + issueNumebr;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().get("title"), connectorProperties.get("issueTitle"));
        Assert.assertEquals(apiRestResponse.getBody().get("body"), connectorProperties.get("issueBody"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("assignee").get("login"),
                connectorProperties.getProperty("owner"));
        
    }
    
    /**
     * Negative test case for createIssue method.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateIssueWithOptionalParameters" }, description = "github {createIssue} integration test with negative case.")
    public void testCreateIssueWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createIssue");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createIssue_neagative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/issues";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createIssue_neagative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        
    }
    
    /**
     * Positive test case for editIssue method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateIssueWithNegativeCase" }, description = "github {editIssue} integration test with mandatory parameters.")
    public void testEditIssueWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:editIssue");
        Thread.sleep(timeOut);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editIssue_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/issues/"
                        + connectorProperties.getProperty("issueNumber");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("body").toString(), apiRestResponse.getBody().get("body")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().get("title").toString(), apiRestResponse.getBody().get("title")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().get("comments_url").toString(),
                apiRestResponse.getBody().get("comments_url").toString());
    }
    
    /**
     * Positive test case for editIssue method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testEditIssueWithMandatoryParameters" }, description = "github {editIssue} integration test with optional parameters.")
    public void testEditIssueWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:editIssue");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editIssue_optional.json");
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/issues/"
                        + connectorProperties.getProperty("issueNumber");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("body").toString(), apiRestResponse.getBody().get("body")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().get("state").toString(), apiRestResponse.getBody().get("state")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().get("user").toString(), apiRestResponse.getBody().get("user")
                .toString());
    }
    
    /**
     * Negative test case for editIssue.
     */
    @Test(priority = 1, dependsOnMethods = { "testEditIssueWithOptionalParameters" }, description = "github {editIssue} integration test negative case.")
    public void testEditIssueNegativeCase() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:editIssue");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_editIssue_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/issues/Invalid";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_editIssue_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("message").toString(),
                apiRestResponse.getBody().get("message").toString());
        
    }
    
    /**
     * Positive test case for getIssue method with mandatory parameters
     */
    @Test(priority = 1, dependsOnMethods = { "testEditIssueNegativeCase" }, description = "github {getIssue} integration test case with mandatory parameters.")
    public void testGetIssueWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getIssue");
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/issues/"
                        + connectorProperties.getProperty("issueNumber");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getIssue_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().get("id").toString(), apiRestResponse.getBody().get("id")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().get("number").toString(), apiRestResponse.getBody().get("number")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().get("url").toString(), apiRestResponse.getBody().get("url")
                .toString());
    }
    
    /**
     * Negative test case for getIssue method
     */
    @Test(priority = 1, dependsOnMethods = { "testGetIssueWithMandatoryParameters" }, description = "github {getIssue} integration test case with negative parameters.")
    public void testGetIssueWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getIssue");
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/issues/Invalid";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getIssue_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("message").toString(),
                apiRestResponse.getBody().get("message").toString());
    }
    
    /**
     * Positive test case for searchIssues method with mandatory parameters
     */
    @Test(priority = 1, dependsOnMethods = { "testGetIssueWithNegativeCase" }, description = "github {searchIssues} integration test case with mandatory parameters.")
    public void testSearchIssuesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchIssues");
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/search/issues?q=user:"
                        + connectorProperties.getProperty("owner");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchIssues_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().get("total_count"), apiRestResponse.getBody().get("total_count"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for searchIssues method with optional parameters
     */
    @Test(priority = 1, dependsOnMethods = { "testSearchIssuesWithMandatoryParameters" }, description = "github {searchIssues} integration test case with optional parameters.")
    public void testSearchIssuesWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchIssues");
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/search/issues?q=user:"
                        + connectorProperties.getProperty("owner") + "&sort=comments&order=asc";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchIssues_optional.json");
        
        Assert.assertEquals(esbRestResponse.getBody().get("total_count"), apiRestResponse.getBody().get("total_count"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Negative test case for searchIssues method negative
     */
    @Test(priority = 1, dependsOnMethods = { "testSearchIssuesWithOptionalParameters" }, description = "github {searchIssues} integration test with negative case.")
    public void testSearchIssuesWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchIssues");
        String apiEndPoint = connectorProperties.getProperty("githubApiUrl") + "/search/issues";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchIssues_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        
    }
    
    /**
     * Positive test case for listRepositoryIssues method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testSearchIssuesWithNegativeCase" }, description = "github {listRepositoryIssues} integration test with mandatory parameters.")
    public void testListRepositoryIssuesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listRepositoryIssues");
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/issues";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listRepositoryIssues_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbArray.length(), apiArray.length());
        
       
        if (esbArray.length() > 0 && apiArray.length() > 0) {
            JSONObject esbFirstElement = esbArray.getJSONObject(0);
            JSONObject apiFirstElement = apiArray.getJSONObject(0);
            Assert.assertEquals(esbFirstElement.get("number"), apiFirstElement.get("number"));
        }
        
       
    }
    
    /**
     * Positive test case for listRepositoryIssues method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testListRepositoryIssuesWithMandatoryParameters" }, description = "github {listRepositoryIssues} integration test with optional parameters.")
    public void testListRepositoryIssuesWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listRepositoryIssues");
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/issues?milestone=*&state=open&assignee="
                        + connectorProperties.getProperty("owner") + "&creator="
                        + connectorProperties.getProperty("owner")
                        + "&sort=created&direction=asc";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listRepositoryIssues_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbArray.length(), apiArray.length());
       
       
        if (esbArray.length() > 0 && apiArray.length() > 0) {
            JSONObject esbFirstElement = esbArray.getJSONObject(0);
            JSONObject apiFirstElement = apiArray.getJSONObject(0);
            Assert.assertEquals(esbFirstElement.get("number"), apiFirstElement.get("number"));
        }
        
    }
    
    /**
     * Negative test case for listRepositoryIssues method.
     */
    @Test(priority = 1, dependsOnMethods = { "testListRepositoryIssuesWithOptionalParameters" }, description = "github {listRepositoryIssues} integration test with negative test case.")
    public void testListRepositoryIssuesNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listRepositoryIssues");
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/InvalidRepoName/issues";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listRepositoryIssues_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        
    }
    
    /**
     * Positive test case for createIssueComment method with mandatory parameters
     */
    @Test(priority = 1, dependsOnMethods = { "testListRepositoryIssuesNegativeCase" }, description = "github {createIssueComment} integration test case with mandatory parameters.")
    public void testCreateIssueCommentWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createIssueComment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createIssueComment_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/issues/comments/"
                        + esbRestResponse.getBody().get("id").toString();
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id").toString(), apiRestResponse.getBody().get("id")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().get("created_at").toString(),
                apiRestResponse.getBody().get("created_at").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("url").toString(), apiRestResponse.getBody().get("url")
                .toString());
    }
    
    /**
     * Negative test case for createIssueComment method
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateIssueCommentWithMandatoryParameters" }, description = "github {createIssueComment} integration test case with negative parameters.")
    public void testCreateIssueCommentWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createIssueComment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createIssueComment_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/issues/" + "Invalid" + "comments";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createIssueComment_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("message").toString(),
                apiRestResponse.getBody().get("message").toString());
    }
    
    /**
     * Positive test case for listIssueComments method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateIssueCommentWithNegativeCase" }, description = "github {listIssueComments} integration test with mandatory parameters.")
    public void testListIssueCommentsWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:listIssueComments");
        Thread.sleep(timeOut);
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/issues/"
                        + connectorProperties.getProperty("issueNumber") + "/comments";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listIssueComments_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbArray.length(), apiArray.length());
       
       
        if (esbArray.length() > 0 && apiArray.length() > 0) {
            JSONObject esbFirstElement = esbArray.getJSONObject(0);
            JSONObject apiFirstElement = apiArray.getJSONObject(0);
            Assert.assertEquals(esbFirstElement.get("id"), apiFirstElement.get("id"));
        }
    }
    
    /**
     * Negative test case for listIssueComments method.
     */
    @Test(priority = 1, dependsOnMethods = { "testListIssueCommentsWithMandatoryParameters" }, description = "github {listIssueComments} integration test with nagative test case.")
    public void testListIssueCommentsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listIssueComments");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/issues/InvalidNumber/comments";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listIssueComments_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
    }
    
    /**
     * Positive test case for getPullRequest method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testListIssueCommentsWithNegativeCase" }, description = "github {getPullRequest} integration test with mandatory parameters.")
    public void testGetPullRequestWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getPullRequest");
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/pulls/"
                        + connectorProperties.getProperty("pullRequestNumber");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPullRequest_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("url"), apiRestResponse.getBody().get("url"));
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("html_url"), apiRestResponse.getBody().get("html_url"));
        Assert.assertEquals(esbRestResponse.getBody().get("title"), apiRestResponse.getBody().get("title"));
        Assert.assertEquals(esbRestResponse.getBody().get("created_at"), apiRestResponse.getBody().get("created_at"));
    }
    
    /**
     * Negative test case for getPullRequest method.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetPullRequestWithMandatoryParameters" }, description = "github {getPullRequest} integration test with negative test case.")
    public void testGetPullRequestNegativeCase() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:getPullRequest");
        Thread.sleep(timeOut);
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/pulls/InvalidNumber";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPullRequest_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
    }
    
    /**
     * Positive test case for listPullRequests method with mandatory parameters
     */
    @Test(priority = 1, dependsOnMethods = { "testGetPullRequestNegativeCase" }, description = "github {listPullRequests} integration test with mandatory parameters.")
    public void listPullRequestsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listPullRequests");
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/pulls";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPullRequests_mandatory.json");
        JSONArray esbResponseJsonArray = new JSONArray(esbRestResponse.getBody().get("output").toString());
        JSONArray apiResponseJsonArray = new JSONArray(apiRestResponse.getBody().get("output").toString());
        
        
        Assert.assertEquals(esbResponseJsonArray.length(), apiResponseJsonArray.length());
       
       
        if (esbResponseJsonArray.length() > 0 && apiResponseJsonArray.length() > 0) {
            JSONObject esbFirstElement = esbResponseJsonArray.getJSONObject(0);
            JSONObject apiFirstElement = apiResponseJsonArray.getJSONObject(0);
            Assert.assertEquals(esbFirstElement.get("number"), apiFirstElement.get("number"));
        }
    }
    
    /**
     * Positive test case for listPullRequests method with optional parameters
     */
    @Test(priority = 1, dependsOnMethods = { "listPullRequestsWithMandatoryParameters" }, description = "github {listPullRequests} integration test with optional parameters.")
    public void listPullRequestsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listPullRequests");
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/pulls?state=open";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPullRequests_optional.json");
        
        JSONArray esbResponseJsonArray = new JSONArray(esbRestResponse.getBody().get("output").toString());
        JSONArray apiResponseJsonArray = new JSONArray(apiRestResponse.getBody().get("output").toString());
        
        
        Assert.assertEquals(esbResponseJsonArray.length(), apiResponseJsonArray.length());
       
       
        if (esbResponseJsonArray.length() > 0 && apiResponseJsonArray.length() > 0) {
            JSONObject esbFirstElement = esbResponseJsonArray.getJSONObject(0);
            JSONObject apiFirstElement = apiResponseJsonArray.getJSONObject(0);
            Assert.assertEquals(esbFirstElement.get("number"), apiFirstElement.get("number"));
        }
    }
    
    /**
     * Negative test case for listPullRequests method
     */
    @Test(priority = 1, dependsOnMethods = { "listPullRequestsWithOptionalParameters" }, description = "github {listPullRequests} integration test negative case.")
    public void listPullRequestsNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listPullRequests");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPullRequests_negative.json");
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/Invalid" + "/"
                        + connectorProperties.getProperty("repo") + "/pulls";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_editIssue_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("message").toString(),
                apiRestResponse.getBody().get("message").toString());
        
    }
    
    /**
     * Positive test case for listPullRequestCommits method with mandatory parameters
     */
    @Test(priority = 1, dependsOnMethods = { "listPullRequestsNegativeCase" }, description = "github {listPullRequestsFiles} integration test case with mandatory parameters.")
    public void listPullRequestsCommitsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listPullRequestCommits");
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/pulls/"
                        + connectorProperties.getProperty("pullRequestNumber") + "/commits";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPullRequestCommits_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray esbResponseJsonArray = new JSONArray(esbRestResponse.getBody().get("output").toString());
        JSONArray apiResponseJsonArray = new JSONArray(apiRestResponse.getBody().get("output").toString());
        
        
        Assert.assertEquals(esbResponseJsonArray.length(), apiResponseJsonArray.length());
       
       
        if (esbResponseJsonArray.length() > 0 && apiResponseJsonArray.length() > 0) {
            JSONObject esbFirstElement = esbResponseJsonArray.getJSONObject(0);
            JSONObject apiFirstElement = apiResponseJsonArray.getJSONObject(0);
            Assert.assertEquals(esbFirstElement.get("sha"), apiFirstElement.get("sha"));
        }
        
    }
    
    /**
     * Negative test case for listPullRequestCommits method
     */
    @Test(priority = 1, dependsOnMethods = { "listPullRequestsCommitsWithMandatoryParameters" }, description = "github {listPullRequestsFiles} integration test negative case.")
    public void listPullRequestsCommitsNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listPullRequestCommits");
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/pulls/" + "Invalid" + "/commits";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPullRequestCommits_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
    }
    
    /**
     * Positive test case for mergePullRequest method with Mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "listPullRequestsCommitsNegativeCase" }, description = "github {mergePullRequest} integration test with Mandatory parameters.")
    public void testMergePullRequestWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:mergePullRequest");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_mergePullRequest_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/pulls/"
                        + connectorProperties.getProperty("pullRequestNumber") + "/merge";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 204);
        
    }
    
    /**
     * Positive test case for mergePullRequest method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testMergePullRequestWithMandatoryParameters" }, description = "github {mergePullRequest} integration test with optional parameters.")
    public void testMergePullRequestWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:mergePullRequest");
        Thread.sleep(timeOut);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_mergePullRequest_optional.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/pulls/"
                        + connectorProperties.getProperty("pullRequestNumberOptional") + "/merge";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 204);
        
    }
    
    /**
     * Negative test case for mergePullRequest method.
     */
    @Test(priority = 1, dependsOnMethods = { "testMergePullRequestWithOptionalParameters" }, description = "github {mergePullRequest} integration test with Negative case.")
    public void testMergePullRequestWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:mergePullRequest");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_mergePullRequest_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/pulls/"
                        + connectorProperties.getProperty("pullRequestNumber") + "/merge";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        
    }
    
    /**
     * Positive test case for performAMerge method with mandatory parameters.
     */
    @Test(priority = 1, description = "github {performAMerge} integration test with mandatory parameters.")
    public void testPerformAMergeWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:performAMerge");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_performAMerge_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/merges";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_performAMerge_mandatory.json");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 204);
    }
    
    /**
     * Positive test case for performAMerge method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testPerformAMergeWithMandatoryParameters" }, description = "github {performAMerge} integration test with mandatory parameters.")
    public void testPerformAMergeWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:performAMerge");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_performAMerge_optional.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/merges";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_performAMerge_optional.json");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 204);
    }
    
    /**
     * Negative test case for performAMerge.
     */
    @Test(priority = 1, dependsOnMethods = { "testPerformAMergeWithOptionalParameters" }, description = "github {performAMerge} integration test negative case.")
    public void testPerformAMergeNegativeCase() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:performAMerge");
        
        Thread.sleep(timeOut);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_performAMerge_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/merges";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_performAMerge_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        
    }
    
    /**
     * Positive test case for getUser method with mandatory parameters.
     */
    @Test(priority = 1, description = "github {getUser} integration test with mandatory parameters.")
    public void testGetUserWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getUser");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUser_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/users/" + connectorProperties.getProperty("user");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("type"), apiRestResponse.getBody().get("type"));
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("public_repos"), apiRestResponse.getBody()
                .get("public_repos"));
    }
    
    /**
     * Negative test case for getUser.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetUserWithMandatoryParameters" }, description = "github {getUser} integration test negative case.")
    public void testGetUserNegativeCase() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:getUser");
        
        Thread.sleep(timeOut);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUser_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("githubApiUrl") + "/users/qwe654qwe";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("message").toString(),
                apiRestResponse.getBody().get("message").toString());
        
    }
    
    /**
     * Positive test case for getTag method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetUserNegativeCase" }, description = "github {getTag} integration test with mandatory parameters.")
    public void testGetTagWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getTag");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTag_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/git/tags/"
                        + connectorProperties.getProperty("tagSHA");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("url"), apiRestResponse.getBody().getString("url"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("tagger").getString("name"), apiRestResponse
                .getBody().getJSONObject("tagger").getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("object").getString("sha"), apiRestResponse
                .getBody().getJSONObject("object").getString("sha"));
    }
    
    /**
     * Positive test case for getTag method negative case.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetTagWithMandatoryParameters" }, description = "github {getTag} integration test with negative case.")
    public void testGetTagWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getTag");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTagt_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/git/tags/Invalid";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("message").toString(),
                apiRestResponse.getBody().get("message").toString());
    }
    
    /**
     * Positive test case for listForks method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetTagWithNegativeCase" }, description = "github {listForks} integration test with mandatory parameters.")
    public void testListForksWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listForks");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/forks";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listForks_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseJsonArray = new JSONArray(esbRestResponse.getBody().get("output").toString());
        JSONArray apiResponseJsonArray = new JSONArray(apiRestResponse.getBody().get("output").toString());
        
        
        Assert.assertEquals(esbResponseJsonArray.length(), apiResponseJsonArray.length());
        
       
        if (esbResponseJsonArray.length() > 0 && apiResponseJsonArray.length() > 0) {
            JSONObject esbFirstElement = esbResponseJsonArray.getJSONObject(0);
            JSONObject apiFirstElement = apiResponseJsonArray.getJSONObject(0);
            Assert.assertEquals(esbFirstElement.get("id"), apiFirstElement.get("id"));
        }
    }
    
    /**
     * Positive test case for listForks method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testListForksWithMandatoryParameters" }, description = "github {listForks} integration test with optional parameters.")
    public void testListForksWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listForks");
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/forks?sort=newest";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listForks_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseJsonArray = new JSONArray(esbRestResponse.getBody().get("output").toString());
        JSONArray apiResponseJsonArray = new JSONArray(apiRestResponse.getBody().get("output").toString());
        
        
        Assert.assertEquals(esbResponseJsonArray.length(), apiResponseJsonArray.length());
        
       
        if (esbResponseJsonArray.length() > 0 && apiResponseJsonArray.length() > 0) {
            JSONObject esbFirstElement = esbResponseJsonArray.getJSONObject(0);
            JSONObject apiFirstElement = apiResponseJsonArray.getJSONObject(0);
            Assert.assertEquals(esbFirstElement.get("id"), apiFirstElement.get("id"));
        }
    }
    
    /**
     * Negative test case for listForks method.
     */
    @Test(priority = 1, dependsOnMethods = { "testListForksWithOptionalParameters" }, description = "github {listForks} integration test with negative test case.")
    public void testListForksIssuesNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listForks");
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/forks?sort=xxx";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listForks_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
    }
    
    /**
     * Positive test case for listCollaborators method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testListForksIssuesNegativeCase" }, description = "github {listCollaborators} integration test with mandatory parameters.")
    public void testListCollaboratorsWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:listCollaborators");
        Thread.sleep(timeOut);
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/collaborators";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCollaborators_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseJsonArray = new JSONArray(esbRestResponse.getBody().get("output").toString());
        JSONArray apiResponseJsonArray = new JSONArray(apiRestResponse.getBody().get("output").toString());
        
        
        Assert.assertEquals(esbResponseJsonArray.length(), apiResponseJsonArray.length());
       
       
        if (esbResponseJsonArray.length() > 0 && apiResponseJsonArray.length() > 0) {
            JSONObject esbFirstElement = esbResponseJsonArray.getJSONObject(0);
            JSONObject apiFirstElement = apiResponseJsonArray.getJSONObject(0);
            Assert.assertEquals(esbFirstElement.get("id"), apiFirstElement.get("id"));
        }
    }
    
    /**
     * Negative test case for listCollaborators method.
     */
    @Test(priority = 1, dependsOnMethods = { "testListCollaboratorsWithMandatoryParameters" }, description = "github {listCollaborators} integration test with nagative test case.")
    public void testListCollaboratorsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCollaborators");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/InvalidRepo/collaborators";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCollaborators_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
    }
    
    /**
     * Positive test case for getNotifications method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testListCollaboratorsWithNegativeCase" }, description = "github {getNotifications} integration test with mandatory parameters.")
    public void testGetNotificationsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getNotifications");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getNotifications_mandatory.json");
        
        String apiEndPoint = connectorProperties.getProperty("githubApiUrl") + "/notifications";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray esbResponseJsonArray = new JSONArray(esbRestResponse.getBody().get("output").toString());
        JSONArray apiResponseJsonArray = new JSONArray(apiRestResponse.getBody().get("output").toString());
        
        Assert.assertEquals(esbResponseJsonArray.length(), apiResponseJsonArray.length());
       
       if (esbResponseJsonArray.length() > 0 && apiResponseJsonArray.length() > 0) {
            JSONObject esbFirstElement = esbResponseJsonArray.getJSONObject(0);
            JSONObject apiFirstElement = apiResponseJsonArray.getJSONObject(0);
            Assert.assertEquals(esbFirstElement.get("id"), apiFirstElement.get("id"));
        }
    }
    
    /**
     * Positive test case for getNotifications method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetNotificationsWithMandatoryParameters" }, description = "github {getNotifications} integration test with optional parameters.")
    public void testGetNotificationsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getNotifications");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getNotifications_optional.json");
        
        String apiEndPoint = connectorProperties.getProperty("githubApiUrl") + "/notifications?all=true";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray esbResponseJsonArray = new JSONArray(esbRestResponse.getBody().get("output").toString());
        JSONArray apiResponseJsonArray = new JSONArray(apiRestResponse.getBody().get("output").toString());
        
        Assert.assertEquals(esbResponseJsonArray.length(), apiResponseJsonArray.length());
       
       if (esbResponseJsonArray.length() > 0 && apiResponseJsonArray.length() > 0) {
            JSONObject esbFirstElement = esbResponseJsonArray.getJSONObject(0);
            JSONObject apiFirstElement = apiResponseJsonArray.getJSONObject(0);
            Assert.assertEquals(esbFirstElement.get("id"), apiFirstElement.get("id"));
        }
    }
    
    /**
     * Negative test case for getNotifications method.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetNotificationsWithOptionalParameters" }, description = "github {getNotifications} integration test with negative case.")
    public void testGetNotificationsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getNotifications");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getNotifications_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("githubApiUrl") + "/notifications?since=invalid";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        
    }
    
    /**
     * Positive test case for getRepositoryNotifications method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetNotificationsWithNegativeCase" }, description = "github {getRepositoryNotifications} integration test with mandatory parameters.")
    public void testGetRepositoryNotificationsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getRepositoryNotifications");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_getRepositoryNotifications_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/notifications";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray esbResponseJsonArray = new JSONArray(esbRestResponse.getBody().get("output").toString());
        JSONArray apiResponseJsonArray = new JSONArray(apiRestResponse.getBody().get("output").toString());
        
        Assert.assertEquals(esbResponseJsonArray.length(), apiResponseJsonArray.length());
       
       if (esbResponseJsonArray.length() > 0 && apiResponseJsonArray.length() > 0) {
            JSONObject esbFirstElement = esbResponseJsonArray.getJSONObject(0);
            JSONObject apiFirstElement = apiResponseJsonArray.getJSONObject(0);
            Assert.assertEquals(esbFirstElement.get("id"), apiFirstElement.get("id"));
        }
    }
    
    /**
     * Positive test case for getRepositoryNotifications method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetRepositoryNotificationsWithMandatoryParameters" }, description = "github {getRepositoryNotifications} integration test with optional parameters.")
    public void testGetRepositoryNotificationsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getRepositoryNotifications");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_getRepositoryNotifications_optional.json");
        
               
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/notifications?all=true";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray esbResponseJsonArray = new JSONArray(esbRestResponse.getBody().get("output").toString());
        JSONArray apiResponseJsonArray = new JSONArray(apiRestResponse.getBody().get("output").toString());
        
        Assert.assertEquals(esbResponseJsonArray.length(), apiResponseJsonArray.length());
       
       if (esbResponseJsonArray.length() > 0 && apiResponseJsonArray.length() > 0) {
            JSONObject esbFirstElement = esbResponseJsonArray.getJSONObject(0);
            JSONObject apiFirstElement = apiResponseJsonArray.getJSONObject(0);
            Assert.assertEquals(esbFirstElement.get("id"), apiFirstElement.get("id"));
        }
    }
    
    /**
     * Negative test case for getRepositoryNotifications method.
     */
    @Test(priority = 1, dependsOnMethods = { "testGetRepositoryNotificationsWithOptionalParameters" }, description = "github {getRepositoryNotifications} integration test with negative case.")
    public void testGetRepositoryNotificationsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getRepositoryNotifications");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_getRepositoryNotifications_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/notifications?since=invalid";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        
    }
    
    /**
     * Method to create tag.
     */
    private void createTag() throws IOException, JSONException {
    
        String apiEndPoint =
                connectorProperties.getProperty("githubApiUrl") + "/repos/" + connectorProperties.getProperty("owner")
                        + "/" + connectorProperties.getProperty("repo") + "/git/tags";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createTag_environment.json");
        String tagSHA = apiRestResponse.getBody().getString("sha");
        connectorProperties.put("tagSHA", tagSHA);
    }
    
}
