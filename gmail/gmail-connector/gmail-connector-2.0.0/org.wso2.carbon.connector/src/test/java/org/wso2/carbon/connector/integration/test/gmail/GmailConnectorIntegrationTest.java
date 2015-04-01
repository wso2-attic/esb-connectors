package org.wso2.carbon.connector.integration.test.gmail;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;
import javax.activation.DataHandler;

public class GmailConnectorIntegrationTest extends ConnectorIntegrationTestBase{

    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    private Map<String, String> headersMap = new HashMap<String, String>();

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init("gmail");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        String authorization = connectorProperties.getProperty("accessToken");
        apiRequestHeadersMap.put("Authorization", "Bearer "+ authorization);
    }

    /**
     * Positive test case for listAllMails method with mandatory parameters.
     */
    @Test(enabled = true, description = "gmail {listAllMails} integration test with mandatory parameter.")
    public void testGetAListOfContactsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmail_listAllMails";
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
    @Test(enabled = true, description = "gmail {listAllMails} integration test with optional parameter.")
    public void testGetAListOfContactsWithOptionalParameters() throws IOException, JSONException {
        String methodName = "gmail_listAllMails";
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
    @Test(enabled = true, description = "gmail {getAMail} integration test with mandatory parameter.")
    public void testGetAMailWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmail_getAMail";
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
    @Test(enabled = true, description = "gmail {getAMail} integration test with optional parameter.")
    public void testGetAMailWithOptionalParameters() throws IOException, JSONException {
        String methodName = "gmail_getAMail";
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
    @Test(enabled = true, description = "gmail {listLabels} integration test with mandatory parameter.")
    public void testListLabelsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmail_listLabels";
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
    @Test(enabled = true, description = "gmail {getALabel} integration test with mandatory parameter.")
    public void testGetALabelWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmail_getALabel";
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
    @Test(enabled = true, description = "gmail {listAllThreads} integration test with mandatory parameter.")
    public void testListAllThreadsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmail_listAllThreads";
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
    @Test(enabled = true, description = "gmail {listAllThreads} integration test with optional parameter.")
    public void testListAllThreadsWithOptionalParameters() throws IOException, JSONException {
        String methodName = "gmail_listAllThreads";
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
    @Test(enabled = true, description = "gmail {getAThread} integration test with mandatory parameter.")
    public void testGetAThreadWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmail_getAThread";
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
    @Test(enabled = true, description = "gmail {getAThread} integration test with optional parameter.")
    public void testGetAThreadWithOptionalParameters() throws IOException, JSONException {
        String methodName = "gmail_getAThread";
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
    @Test(enabled = true, description = "gmail {listDrafts} integration test with mandatory parameter.")
    public void testListDraftsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmail_listDrafts";
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
    @Test(enabled = true, description = "gmail {listDrafts} integration test with optional parameter.")
    public void testListDraftsWithOptionalParameters() throws IOException, JSONException {
        String methodName = "gmail_listDrafts";
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
    @Test(enabled = true, description = "gmail {getADraft} integration test with mandatory parameter.")
    public void testGetADraftWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmail_getADraft";
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
    @Test(enabled = true, description = "gmail {getADraft} integration test with optional parameter.")
    public void testGetADraftWithOptionalParameters() throws IOException, JSONException {
        String methodName = "gmail_getADraft";
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
    @Test(enabled = true, description = "gmail {getUserProfile} integration test with mandatory parameter.")
    public void testGetUserProfileWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmail_getUserProfile";
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
     * Positive test case for getUserProfile method with mandatory parameters.
     */
    @Test(enabled = true, description = "gmail {listTheHistory} integration test with mandatory parameter.")
    public void testListTheHistoryWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmail_listTheHistory";
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
     * Positive test case for getUserProfile method with optional parameters.
     */
    @Test(enabled = true, description = "gmail {listTheHistory} integration test with optional parameter.")
    public void testListTheHistoryWithOptionalParameters() throws IOException, JSONException {
        String methodName = "gmail_listTheHistory";
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
    @Test(enabled = false, description = "gmail {createLabels} integration test with mandatory parameter.")
    public void testCreateLabelsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmail_createLabels";
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
    @Test(enabled = false, description = "gmail {createLabels} integration test with optional parameter.")
    public void testCreateLabelsWithOptionalParameters() throws IOException, JSONException {
        String methodName = "gmail_createLabels";
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
    @Test(enabled = false, description = "gmail {createAMail} integration test with mandatory parameter.")
    public void testCreateAMailWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "gmail_createAMail";
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
