/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.connector.integration.test.gmailRest;
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

public class GmailConnectorIntegrationTest extends ConnectorIntegrationTestBase{

    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    private Map<String, String> headersMap = new HashMap<String, String>();

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init("gmailRest-connector-2.0.0");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        String authorization = connectorProperties.getProperty("accessToken");
        apiRequestHeadersMap.put("Authorization", "Bearer "+ authorization);
    }

    /**
     * Positive test case for listAllMails method with mandatory parameters.
     */
    @Test(enabled = true, description = "gmailRest {listAllMails} integration test with mandatory parameter.")
    public void testGetAListOfContactsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmailRest_listAllMails";
        String apiEndPoint =
                        connectorProperties.getProperty("apiUrl") +"/"+
                        connectorProperties.getProperty("apiVersion") +"/users/"+
                        connectorProperties.getProperty("userId") +"/messages";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "listAllMailsMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for listAllMails method with optional parameters.
     */
    @Test(enabled = true, description = "gmailRest {listAllMails} integration test with optional parameter.")
    public void testGetAListOfContactsWithOptionalParameters() throws IOException, JSONException {
        String methodName = "gmailRest_listAllMails";
        String apiEndPoint =
                        connectorProperties.getProperty("apiUrl") +"/"+
                        connectorProperties.getProperty("apiVersion") +"/users/"+
                        connectorProperties.getProperty("userId") +"/messages?includeSpamTrash="+
                        connectorProperties.getProperty("includeSpamTrash") +"&pageToken="+
                        connectorProperties.getProperty("pageToken") +"&labelIds="+
                        connectorProperties.getProperty("labelIds") +"&q="+
                        connectorProperties.getProperty("q")+"&maxResults="+
                        connectorProperties.getProperty("maxResults");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "listAllMailsOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAMail method with mandatory parameters.
     */
    @Test(enabled = true, description = "gmailRest {getAMail} integration test with mandatory parameter.")
    public void testGetAMailWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmailRest_getAMail";
        String apiEndPoint =
                        connectorProperties.getProperty("apiUrl") +"/"+
                        connectorProperties.getProperty("apiVersion") +"/users/"+
                        connectorProperties.getProperty("userId") +"/messages/"+
                        connectorProperties.getProperty("mailId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAMailMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAMail method with optional parameters.
     */
    @Test(enabled = true, description = "gmailRest {getAMail} integration test with optional parameter.")
    public void testGetAMailWithOptionalParameters() throws IOException, JSONException {
        String methodName = "gmailRest_getAMail";
        String apiEndPoint =
                        connectorProperties.getProperty("apiUrl") +"/"+
                        connectorProperties.getProperty("apiVersion") +"/users/"+
                        connectorProperties.getProperty("userId") +"/messages/"+
                        connectorProperties.getProperty("mailId")+"?format="+
                        connectorProperties.getProperty("format");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAMailOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for listLabels method with mandatory parameters.
     */
    @Test(enabled = true, description = "gmailRest {listLabels} integration test with mandatory parameter.")
    public void testListLabelsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmailRest_listLabels";
        String apiEndPoint =
                        connectorProperties.getProperty("apiUrl") +"/"+
                        connectorProperties.getProperty("apiVersion") +"/users/"+
                        connectorProperties.getProperty("userId") +"/labels";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "listLabelsMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getALabel method with mandatory parameters.
     */
    @Test(enabled = true, description = "gmailRest {getALabel} integration test with mandatory parameter.")
    public void testGetALabelWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmailRest_getALabel";
        String apiEndPoint =
                        connectorProperties.getProperty("apiUrl") +"/"+
                        connectorProperties.getProperty("apiVersion") +"/users/"+
                        connectorProperties.getProperty("userId") +"/labels/"+
                        connectorProperties.getProperty("labelId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getALabelMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for listAllThreads method with mandatory parameters.
     */
    @Test(enabled = true, description = "gmailRest {listAllThreads} integration test with mandatory parameter.")
    public void testListAllThreadsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmailRest_listAllThreads";
        String apiEndPoint =
                        connectorProperties.getProperty("apiUrl") +"/"+
                        connectorProperties.getProperty("apiVersion") +"/users/"+
                        connectorProperties.getProperty("userId") +"/threads";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "listAllThreadsMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for listAllThreads method with optional parameters.
     */
    @Test(enabled = true, description = "gmailRest {listAllThreads} integration test with optional parameter.")
    public void testListAllThreadsWithOptionalParameters() throws IOException, JSONException {
        String methodName = "gmailRest_listAllThreads";
        String apiEndPoint =
                        connectorProperties.getProperty("apiUrl") +"/"+
                        connectorProperties.getProperty("apiVersion") +"/users/"+
                        connectorProperties.getProperty("userId") +"/threads?includeSpamTrash="+
                        connectorProperties.getProperty("includeSpamTrash") +"&pageToken="+
                        connectorProperties.getProperty("pageToken") +"&labelIds="+
                        connectorProperties.getProperty("labelIds") +"&q="+
                        connectorProperties.getProperty("q")+"&maxResults="+
                        connectorProperties.getProperty("maxResults");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "listAllThreadsOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAThread method with mandatory parameters.
     */
    @Test(enabled = true, description = "gmailRest {getAThread} integration test with mandatory parameter.")
    public void testGetAThreadWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmailRest_getAThread";
        String apiEndPoint =
                        connectorProperties.getProperty("apiUrl") +"/"+
                        connectorProperties.getProperty("apiVersion") +"/users/"+
                        connectorProperties.getProperty("userId") +"/threads/"+
                        connectorProperties.getProperty("threadId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAThreadMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAThread method with optional parameters.
     */
    @Test(enabled = true, description = "gmailRest {getAThread} integration test with optional parameter.")
    public void testGetAThreadWithOptionalParameters() throws IOException, JSONException {
        String methodName = "gmailRest_getAThread";
        String apiEndPoint =
                        connectorProperties.getProperty("apiUrl") +"/"+
                        connectorProperties.getProperty("apiVersion") +"/users/"+
                        connectorProperties.getProperty("userId") +"/threads/"+
                        connectorProperties.getProperty("threadId")+"?format="+
                        connectorProperties.getProperty("format");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getAThreadOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for listDrafts method with mandatory parameters.
     */
    @Test(enabled = true, description = "gmailRest {listDrafts} integration test with mandatory parameter.")
    public void testListDraftsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmailRest_listDrafts";
        String apiEndPoint =
                        connectorProperties.getProperty("apiUrl") +"/"+
                        connectorProperties.getProperty("apiVersion") +"/users/"+
                        connectorProperties.getProperty("userId") +"/drafts";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "listDraftsMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for listDrafts method with optional parameters.
     */
    @Test(enabled = true, description = "gmailRest {listDrafts} integration test with optional parameter.")
    public void testListDraftsWithOptionalParameters() throws IOException, JSONException {
        String methodName = "gmailRest_listDrafts";
        String apiEndPoint =
                        connectorProperties.getProperty("apiUrl") +"/"+
                        connectorProperties.getProperty("apiVersion") +"/users/"+
                        connectorProperties.getProperty("userId") +"/drafts?includeSpamTrash="+
                        connectorProperties.getProperty("includeSpamTrash") +"&pageToken="+
                        connectorProperties.getProperty("pageToken") +"&labelIds="+
                        connectorProperties.getProperty("labelIds") +"&q="+
                        connectorProperties.getProperty("q")+"&maxResults="+
                        connectorProperties.getProperty("maxResults");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "listDraftsOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getADraft method with mandatory parameters.
     */
    @Test(enabled = true, description = "gmailRest {getADraft} integration test with mandatory parameter.")
    public void testGetADraftWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmailRest_getADraft";
        String apiEndPoint =
                        connectorProperties.getProperty("apiUrl") +"/"+
                        connectorProperties.getProperty("apiVersion") +"/users/"+
                        connectorProperties.getProperty("userId") +"/drafts/"+
                        connectorProperties.getProperty("draftId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getADraftMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getADraft method with optional parameters.
     */
    @Test(enabled = true, description = "gmailRest {getADraft} integration test with optional parameter.")
    public void testGetADraftWithOptionalParameters() throws IOException, JSONException {
        String methodName = "gmailRest_getADraft";
        String apiEndPoint =
                        connectorProperties.getProperty("apiUrl") +"/"+
                        connectorProperties.getProperty("apiVersion") +"/users/"+
                        connectorProperties.getProperty("userId") +"/drafts/"+
                        connectorProperties.getProperty("draftId")+"?format="+
                        connectorProperties.getProperty("format");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getADraftOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getUserProfile method with mandatory parameters.
     */
    @Test(enabled = true, description = "gmailRest {getUserProfile} integration test with mandatory parameter.")
    public void testGetUserProfileWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmailRest_getUserProfile";
        String apiEndPoint =
                        connectorProperties.getProperty("apiUrl") +"/"+
                        connectorProperties.getProperty("apiVersion") +"/users/"+
                        connectorProperties.getProperty("userId") +"/profile";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "getUserProfileMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for listTheHistory method with mandatory parameters.
     */
    @Test(enabled = true, description = "gmailRest {listTheHistory} integration test with mandatory parameter.")
    public void testListTheHistoryWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmailRest_listTheHistory";
        String apiEndPoint =
                        connectorProperties.getProperty("apiUrl") +"/"+
                        connectorProperties.getProperty("apiVersion") +"/users/"+
                        connectorProperties.getProperty("userId") +"/history?startHistoryId="+
                        connectorProperties.getProperty("startHistoryId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "listTheHistoryMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for listTheHistory method with optional parameters.
     */
    @Test(enabled = true, description = "gmailRest {listTheHistory} integration test with optional parameter.")
    public void testListTheHistoryWithOptionalParameters() throws IOException, JSONException {
        String methodName = "gmailRest_listTheHistory";
        String apiEndPoint =
                        connectorProperties.getProperty("apiUrl") +"/"+
                        connectorProperties.getProperty("apiVersion") +"/users/"+
                        connectorProperties.getProperty("userId") +"/history?startHistoryId="+
                        connectorProperties.getProperty("startHistoryId")+"&labelId="+
                        connectorProperties.getProperty("labelId")+"&maxResults="+
                        connectorProperties.getProperty("maxResults");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "listTheHistoryOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }


    /**
     * Positive test case for createLabels method with mandatory parameters.
     */
    @Test(enabled = true, description = "gmailRest {createLabels} integration test with mandatory parameter.")
    public void testCreateLabelsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmailRest_createLabels";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "createLabelsMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        String messageId = esbRestResponse.getBody().get("id").toString();
        String apiEndPoint =
                        connectorProperties.getProperty("apiUrl") +"/"+
                        connectorProperties.getProperty("apiVersion") +"/users/"+
                        connectorProperties.getProperty("userId") +"/labels/"+
                        messageId;

        System.out.println(apiEndPoint);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("name").toString(), connectorProperties.getProperty("labelNameMandatory"));
    }

    /**
     * Positive test case for createLabels method with optional parameters.
     */
    @Test(enabled = true, description = "gmailRest {createLabels} integration test with optional parameter.")
    public void testCreateLabelsWithOptionalParameters() throws IOException, JSONException {
        String methodName = "gmailRest_createLabels";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "createLabelsOptional.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        String messageId = esbRestResponse.getBody().get("id").toString();
        String apiEndPoint =
                        connectorProperties.getProperty("apiUrl") +"/"+
                        connectorProperties.getProperty("apiVersion") +"/users/"+
                        connectorProperties.getProperty("userId") +"/labels/"+
                        messageId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("name").toString(), connectorProperties.getProperty("labelNameOptional"));
    }

    /**
     * Positive test case for createAMail method with mandatory parameters.
     */
    @Test(enabled = false, description = "gmailRest {createAMail} integration test with mandatory parameter.")
    public void testCreateAMailWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmailRest_createAMail";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "createAMailMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        String messageId = esbRestResponse.getBody().get("id").toString();
        String apiEndPoint =
                        connectorProperties.getProperty("apiUrl") +"/"+
                        connectorProperties.getProperty("apiVersion") +"/users/"+
                        connectorProperties.getProperty("userId") +"/messages/"+
                        messageId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("name").toString(), connectorProperties.getProperty("labelName"));
    }
}
