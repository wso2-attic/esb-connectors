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
package org.wso2.carbon.connector.integration.test.formstack;

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

public class FormstackConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap;
    
    private Map<String, String> apiRequestHeadersMap;
    
    private String apiRequestUrl;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        esbRequestHeadersMap = new HashMap<String, String>();
        apiRequestHeadersMap = new HashMap<String, String>();
        
        init("formstack-connector-1.0.0");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        apiRequestHeadersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("accessToken"));
        apiRequestUrl = connectorProperties.getProperty("apiUrl") + "/api/v2";
    }
    
    /**
     * Positive test case for getForm method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {getForm} integration test with mandatory parameters.")
    public void testGetFormWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getForm");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getForm_mandatory.json");
        
        final String apiEndPoint = apiRequestUrl + "/form/" + connectorProperties.getProperty("formId") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("viewkey"), apiRestResponse.getBody().getString(
                "viewkey"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("fields").length(), apiRestResponse.getBody()
                .getJSONArray("fields").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("fields").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("fields").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("url"), apiRestResponse.getBody().getString("url"));
    }
    
    /**
     * Negative test case for getForm method.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {getForm} integration test with negative case.")
    public void testGetFormWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getForm");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getForm_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/form/123456789.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listForms method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {listForms} integration test with mandatory parameters.")
    public void testListFormsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listForms");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listForms_mandatory.json");
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("forms");
        
        final String apiEndPoint = apiRequestUrl + "/form.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("forms");
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("name"), apiResponseArray.getJSONObject(0)
                .getString("name"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("created"), apiResponseArray.getJSONObject(0)
                .getString("created"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("url"), apiResponseArray.getJSONObject(0)
                .getString("url"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("submissions"), apiResponseArray.getJSONObject(
                0).getString("submissions"));
    }
    
    /**
     * Positive test case for listForms method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {listForms} integration test with optional parameters.")
    public void testListFormsWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listForms");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listForms_optional.json");
        final JSONArray esbResponseArray =
                esbRestResponse.getBody().getJSONObject("forms").getJSONArray(
                        connectorProperties.getProperty("folderName"));
        
        final String apiEndPoint = apiRequestUrl + "/form.json?folders=true";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        // Forms are put into an array categorized by the folder they belong to.
        final JSONArray apiResponseArray =
                apiRestResponse.getBody().getJSONObject("forms").getJSONArray(
                        connectorProperties.getProperty("folderName"));
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("name"), apiResponseArray.getJSONObject(0)
                .getString("name"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("created"), apiResponseArray.getJSONObject(0)
                .getString("created"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("url"), apiResponseArray.getJSONObject(0)
                .getString("url"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("submissions"), apiResponseArray.getJSONObject(
                0).getString("submissions"));
    }
    
    /**
     * Positive test case for copyForm method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {copyForm} integration test with mandatory parameters.")
    public void testCopyFormWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:copyForm");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_copyForm_mandatory.json");
      
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("fields");
        
        final String apiEndPoint = apiRequestUrl + "/form/" + connectorProperties.getProperty("formId") + "/copy.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("fields");
       
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("label"), apiResponseArray.getJSONObject(0)
                .getString("label"));
        Assert.assertEquals(esbResponseArray.getJSONObject(1).getString("label"), apiResponseArray.getJSONObject(1)
                .getString("label"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("name"), apiResponseArray.getJSONObject(0)
                .getString("name"));
        Assert.assertEquals(esbResponseArray.getJSONObject(1).getString("name"), apiResponseArray.getJSONObject(1)
                .getString("name"));
    }
    
    /**
     * Negative test case for copyForm method.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {copyForm} integration test with negative case.")
    public void testCopyFormWithWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:copyForm");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_copyForm_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/form/123456789/copy.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listSubmissions method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {listSubmissions} integration test with mandatory parameters.")
    public void testListSubmissionsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listSubmissions");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSubmissions_mandatory.json");
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("submissions");
        
        final String apiEndPoint =
                apiRequestUrl + "/form/" + connectorProperties.getProperty("formId") + "/submission.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("submissions");
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("payment_status"), apiResponseArray
                .getJSONObject(0).getString("payment_status"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("user_agent"), apiResponseArray
                .getJSONObject(0).getString("user_agent"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("read"), apiResponseArray.getJSONObject(0)
                .getString("read"));
    }
    
    /**
     * Positive test case for listSubmissions method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {listSubmissions} integration test with optional parameters.")
    public void testListSubmissionsWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listSubmissions");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSubmissions_optional.json");
       
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("submissions");
        final String submissionId = esbResponseArray.getJSONObject(0).getString("id");
        connectorProperties.setProperty("submissionId", submissionId);
        
        final String apiEndPoint =
                apiRequestUrl
                        + "/form/"
                        + connectorProperties.getProperty("formId")
                        + "/submission.json?data=true&expand_data=true&page=1&per_page=2&sort=DESC&data=true&expand_data=true";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("submissions");
        
        Assert.assertEquals(esbResponseArray.length(), 2);
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("remote_addr"), apiResponseArray.getJSONObject(
                0).getString("remote_addr"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("user_agent"), apiResponseArray
                .getJSONObject(0).getString("user_agent"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("read"), apiResponseArray.getJSONObject(0)
                .getString("read"));
    }
    
    /**
     * Negative test case for listSubmissions method.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {listSubmissions} integration test with negative case.")
    public void testListSubmissionsWithWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listSubmissions");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSubmissions_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/form/123456789/submission.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for getSubmission method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {getSubmission} integration test with mandatory parameters.", dependsOnMethods = { "testListSubmissionsWithOptionalParameters" })
    public void testGetSubmissionWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getSubmission");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSubmission_mandatory.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/submission/" + connectorProperties.getProperty("submissionId") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("user_agent"), apiRestResponse.getBody().getString(
                "user_agent"));
        Assert.assertEquals(esbRestResponse.getBody().getString("form"), apiRestResponse.getBody().getString("form"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").length(), apiRestResponse.getBody()
                .getJSONArray("data").length());
        Assert.assertEquals(esbRestResponse.getBody().getString("payment_status"), apiRestResponse.getBody().getString(
                "payment_status"));
    }
    
    /**
     * Positive test case for getSubmission method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {getSubmission} integration test with optional parameters.", dependsOnMethods = { "testListSubmissionsWithOptionalParameters" })
    public void testGetSubmissionWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getSubmission");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSubmission_optional.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/submission/" + connectorProperties.getProperty("submissionId")
                        + ".json?encryption_password=dfdfdfdf";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("user_agent"), apiRestResponse.getBody().getString(
                "user_agent"));
        Assert.assertEquals(esbRestResponse.getBody().getString("form"), apiRestResponse.getBody().getString("form"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").length(), apiRestResponse.getBody()
                .getJSONArray("data").length());
        Assert.assertEquals(esbRestResponse.getBody().getString("payment_status"), apiRestResponse.getBody().getString(
                "payment_status"));
    }
    
    /**
     * Negative test case for getSubmission method.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {getSubmission} integration test with negative case.")
    public void testGetSubmissionWithWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getSubmission");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSubmission_negative.json");
        final String apiEndPoint = apiRequestUrl + "/submission/123456789.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for updateSubmission method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {updateSubmission} integration test with optional parameters.", dependsOnMethods = { "testGetSubmissionWithOptionalParameters" })
    public void testUpdateSubmissionWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateSubmission");
        
        final String apiEndPoint =
                apiRequestUrl + "/submission/" + connectorProperties.getProperty("submissionId") + ".json";
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateSubmission_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponseBefore.getBody().get("timestamp"), apiRestResponseAfter.getBody().get(
                "timestamp"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().get("user_agent"), apiRestResponseAfter.getBody().get(
                "user_agent"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().get("remote_addr"), apiRestResponseAfter.getBody().get(
                "remote_addr"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().get("payment_status"), apiRestResponseAfter.getBody()
                .get("payment_status"));
        
    }
    
    /**
     * Negative test case for updateSubmission method.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {updateSubmission} integration test with negative case.")
    public void testUpdateSubmissionWithWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateSubmission");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateSubmission_negative.json");
        final String apiEndPoint = apiRequestUrl + "/submission/123456789.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for createNotificationEmail method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {createNotificationEmail} integration test with mandatory parameters.")
    public void testCreateNotificationEmailWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createNotificationEmail");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_createNotificationEmail_mandatory.json");
        final String notificationIdMandatory = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("notificationIdMandatory", notificationIdMandatory);
        
        final String apiEndPoint = apiRequestUrl + "/notification/" + notificationIdMandatory + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("from_type"), apiRestResponse.getBody().getString(
                "from_type"));
        Assert.assertEquals(esbRestResponse.getBody().getString("from_value"), apiRestResponse.getBody().getString(
                "from_value"));
        Assert.assertEquals(esbRestResponse.getBody().getString("recipients"), apiRestResponse.getBody().getString(
                "recipients"));
        Assert.assertEquals(esbRestResponse.getBody().getString("subject"), apiRestResponse.getBody().getString(
                "subject"));
        Assert.assertEquals(esbRestResponse.getBody().getString("type"), apiRestResponse.getBody().getString("type"));
    }
    
    /**
     * Positive test case for createNotificationEmail method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {createNotificationEmail} integration test with optional parameters.")
    public void testCreateNotificationEmailWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createNotificationEmail");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNotificationEmail_optional.json");
        final String notificationIdOptional = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("notificationIdOptional", notificationIdOptional);
        
        final String apiEndPoint = apiRequestUrl + "/notification/" + notificationIdOptional + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("hide_empty"), apiRestResponse.getBody().getString(
                "hide_empty"));
        Assert.assertEquals(esbRestResponse.getBody().getString("show_section"), apiRestResponse.getBody().getString(
                "show_section"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString(
                "message"));
        Assert.assertEquals(esbRestResponse.getBody().getString("attach_limit"), apiRestResponse.getBody().getString(
                "attach_limit"));
    }
    
    /**
     * Negative test case for createNotificationEmail method.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {createNotificationEmail} integration test with negative case.")
    public void testCreateNotificationEmailWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createNotificationEmail");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNotificationEmail_negative.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/form/" + connectorProperties.getProperty("formId") + "/notification.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                        "api_createNotificationEmail_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for getNotificationEmail method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {getNotificationEmail} integration test with mandatory parameters.", dependsOnMethods = { "testCreateNotificationEmailWithOptionalParameters" })
    public void testGetNotificationEmailWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getNotificationEmail");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getNotificationEmail_mandatory.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/notification/" + connectorProperties.getProperty("notificationIdOptional") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("from_type"), apiRestResponse.getBody().getString(
                "from_type"));
        Assert.assertEquals(esbRestResponse.getBody().getString("from_value"), apiRestResponse.getBody().getString(
                "from_value"));
        Assert.assertEquals(esbRestResponse.getBody().getString("recipients"), apiRestResponse.getBody().getString(
                "recipients"));
        Assert.assertEquals(esbRestResponse.getBody().getString("subject"), apiRestResponse.getBody().getString(
                "subject"));
        Assert.assertEquals(esbRestResponse.getBody().getString("type"), apiRestResponse.getBody().getString("type"));
    }
    
    /**
     * Negative test case for getNotificationEmail method.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {getNotificationEmail} integration test with negative case.")
    public void testGetNotificationEmailWithWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getNotificationEmail");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getNotificationEmail_negative.json");
        final String apiEndPoint = apiRequestUrl + "/notification/123456789.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for updateNotificationEmail method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {updateNotificationEmail} integration test with optional parameters.", dependsOnMethods = { "testGetNotificationEmailWithMandatoryParameters" })
    public void testUpdateNotificationEmailWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateNotificationEmail");
        
        final String apiEndPoint =
                apiRequestUrl + "/notification/" + connectorProperties.getProperty("notificationIdOptional") + ".json";
        RestResponse<JSONObject> apiRestResponseFirst = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateNotificationEmail_optional.json");
        
        RestResponse<JSONObject> apiRestResponseSecond = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponseFirst.getBody().get("name"), apiRestResponseSecond.getBody().get("name"));
        Assert.assertNotEquals(apiRestResponseFirst.getBody().get("message"), apiRestResponseSecond.getBody().get(
                "message"));
        Assert.assertNotEquals(apiRestResponseFirst.getBody().get("from_value"), apiRestResponseSecond.getBody().get(
                "from_value"));
        Assert.assertNotEquals(apiRestResponseFirst.getBody().get("recipients"), apiRestResponseSecond.getBody().get(
                "recipients"));
        Assert.assertNotEquals(apiRestResponseFirst.getBody().get("subject"), apiRestResponseSecond.getBody().get(
                "subject"));
        
    }
    
    /**
     * Negative test case for updateNotificationEmail method.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {updateNotificationEmail} integration test with negative case.")
    public void testUpdateNotificationEmailWithWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateNotificationEmail");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateNotificationEmail_negative.json");
        final String apiEndPoint = apiRequestUrl + "/notification/123456789.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listNotificationEmails method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {listNotificationEmails} integration test with mandatory parameters.", dependsOnMethods = {
            "testUpdateNotificationEmailWithOptionalParameters", "testCreateNotificationEmailWithMandatoryParameters" })
    public void testListNotificationEmailsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listNotificationEmails");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listNotificationEmails_mandatory.json");
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("notifications");
        
        final String apiEndPoint =
                apiRequestUrl + "/form/" + connectorProperties.getProperty("formId") + "/notification.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("notifications");
        
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("name"), apiResponseArray.getJSONObject(0)
                .getString("name"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("recipients"), apiResponseArray
                .getJSONObject(0).getString("recipients"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("subject"), apiResponseArray.getJSONObject(0)
                .getString("subject"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("editor"), apiResponseArray.getJSONObject(0)
                .getString("editor"));
    }
    
    /**
     * Negative test case for listNotificationEmails method.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {listNotificationEmails} integration test with negative case.")
    public void testListNotificationEmailsWithWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listNotificationEmails");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listNotificationEmails_negative.json");
        final String apiEndPoint = apiRequestUrl + "/form/123456789/notification.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for createConfirmationEmail method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {createConfirmationEmail} integration test with mandatory parameters.")
    public void testCreateConfirmationEmailWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createConfirmationEmail");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_createConfirmationEmail_mandatory.json");
        final String confirmationIdMandatory = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("confirmationIdMandatory", confirmationIdMandatory);
        
        final String apiEndPoint = apiRequestUrl + "/confirmation/" + confirmationIdMandatory + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("to_field"), apiRestResponse.getBody().getString(
                "to_field"));
        Assert.assertEquals(esbRestResponse.getBody().getString("sender"), apiRestResponse.getBody()
                .getString("sender"));
        Assert.assertEquals(esbRestResponse.getBody().getString("subject"), apiRestResponse.getBody().getString(
                "subject"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString(
                "message"));
    }
    
    /**
     * Positive test case for createConfirmationEmail method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {createConfirmationEmail} integration test with optional parameters.")
    public void testCreateConfirmationEmailWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createConfirmationEmail");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createConfirmationEmail_optional.json");
        final String confirmationIdOptional = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("confirmationIdOptional", confirmationIdOptional);
        
        final String apiEndPoint = apiRequestUrl + "/confirmation/" + confirmationIdOptional + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("to_field"), apiRestResponse.getBody().getString(
                "to_field"));
        Assert.assertEquals(esbRestResponse.getBody().getString("sender"), apiRestResponse.getBody()
                .getString("sender"));
        Assert.assertEquals(esbRestResponse.getBody().getString("sender"), apiRestResponse.getBody()
                .getString("sender"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString(
                "message"));
    }
    
    /**
     * Negative test case for createConfirmationEmail method.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {createConfirmationEmail} integration test with negative case.")
    public void testCreateConfirmationEmailWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createConfirmationEmail");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createConfirmationEmail_negative.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/form/" + connectorProperties.getProperty("formId") + "/confirmation.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                        "api_createConfirmationEmail_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for getConfirmationEmail method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {getConfirmationEmail} integration test with mandatory parameters.", dependsOnMethods = { "testCreateConfirmationEmailWithOptionalParameters" })
    public void testGetConfirmationEmailWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getConfirmationEmail");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getConfirmationEmail_mandatory.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/confirmation/" + connectorProperties.getProperty("confirmationIdOptional") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("to_field"), apiRestResponse.getBody().getString(
                "to_field"));
        Assert.assertEquals(esbRestResponse.getBody().getString("sender"), apiRestResponse.getBody()
                .getString("sender"));
        Assert.assertEquals(esbRestResponse.getBody().getString("subject"), apiRestResponse.getBody().getString(
                "subject"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString(
                "message"));
    }
    
    /**
     * Negative test case for getConfirmationEmail method.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {getConfirmationEmail} integration test with negative case.")
    public void testGetConfirmationEmailWithWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getConfirmationEmail");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getConfirmationEmail_negative.json");
        final String apiEndPoint = apiRequestUrl + "/confirmation/123456789.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for updateConfirmationEmail method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {updateConfirmationEmail} integration test with optional parameters.", dependsOnMethods = { "testGetConfirmationEmailWithMandatoryParameters" })
    public void testUpdateConfirmationEmailWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateConfirmationEmail");
        
        final String apiEndPoint =
                apiRequestUrl + "/confirmation/" + connectorProperties.getProperty("confirmationIdOptional") + ".json";
        RestResponse<JSONObject> apiRestResponseFirst = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateConfirmationEmail_optional.json");
        
        RestResponse<JSONObject> apiRestResponseSecond = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponseFirst.getBody().get("name"), apiRestResponseSecond.getBody().get("name"));
        Assert.assertNotEquals(apiRestResponseFirst.getBody().get("message"), apiRestResponseSecond.getBody().get(
                "message"));
        Assert.assertNotEquals(apiRestResponseFirst.getBody().get("subject"), apiRestResponseSecond.getBody().get(
                "subject"));
        Assert.assertNotEquals(apiRestResponseFirst.getBody().get("sender"), apiRestResponseSecond.getBody().get(
                "sender"));
        Assert.assertNotEquals(apiRestResponseFirst.getBody().get("delay"), apiRestResponseSecond.getBody()
                .get("delay"));
        
    }
    
    /**
     * Negative test case for updateConfirmationEmail method.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {updateConfirmationEmail} integration test with negative case.")
    public void testUpdateConfirmationEmailWithWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateConfirmationEmail");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateConfirmationEmail_negative.json");
        final String apiEndPoint = apiRequestUrl + "/confirmation/123456789.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listConfirmationEmails method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {listConfirmationEmails} integration test with mandatory parameters.", dependsOnMethods = {
            "testUpdateConfirmationEmailWithOptionalParameters", "testCreateConfirmationEmailWithOptionalParameters" })
    public void testListConfirmationEmailsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listConfirmationEmails");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listConfirmationEmails_mandatory.json");
        
        final JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("confirmations");
        
        final String apiEndPoint =
                apiRequestUrl + "/form/" + connectorProperties.getProperty("formId") + "/confirmation.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("confirmations");
        
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("name"), apiResponseArray.getJSONObject(0)
                .getString("name"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("sender"), apiResponseArray.getJSONObject(0)
                .getString("sender"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("subject"), apiResponseArray.getJSONObject(0)
                .getString("subject"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("type"), apiResponseArray.getJSONObject(0)
                .getString("type"));
    }
    
    /**
     * Negative test case for listConfirmationEmails method.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {listConfirmationEmails} integration test with negative case.")
    public void testListConfirmationEmailsWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:listConfirmationEmails");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listConfirmationEmails_negative.json");
        final String apiEndPoint = apiRequestUrl + "/form/123456789/confirmation.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for deleteForm method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {deleteForm} integration test with mandatory parameters.")
    public void testDeleteFormWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:deleteForm");
        
        final String apiEndPoint = apiRequestUrl + "/form/" + connectorProperties.getProperty("deleteFormId") + ".json";
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteForm_mandatory.json");
        
        Assert.assertEquals(connectorProperties.getProperty("deleteFormId"),esbRestResponse.getBody().getString("id"));
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponseBefore.getBody().getString("id"),connectorProperties.getProperty("deleteFormId") );
        Assert.assertEquals(apiRestResponseAfter.getBody().getString("status"), "error");
    }
    
    /**
     * Method name: deleteForm
     * Test scenario: Optional
     * Reason to skip: In deleteForm method there is only one parameter which is a mandatory one.
     */
    
    /**
     * Negative test case for deleteForm method.
     */
    @Test(groups = { "wso2.esb" }, description = "formstack {deleteForm} integration test with negative case.")
    public void testDeleteFormWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:deleteForm");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteForm_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/form/invalid.json";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString("status"));
    }
}
