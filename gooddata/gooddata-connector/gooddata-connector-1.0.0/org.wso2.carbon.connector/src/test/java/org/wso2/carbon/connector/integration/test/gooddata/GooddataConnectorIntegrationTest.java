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

package org.wso2.carbon.connector.integration.test.gooddata;

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

public class GooddataConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("gooddata-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Content-Type", "application/json");
        apiRequestHeadersMap.put("Accept", "application/json");
        setCookieHeader();
    }
    
    /**
     * This method sets the Cookie header, containing the authorization details, to be used in direct API
     * calls.
     * 
     * @throws IOException
     * @throws JSONException
     */
    private void setCookieHeader() throws IOException, JSONException {
    
        String apiLoginEndPoint = connectorProperties.getProperty("apiUrl") + "/gdc/account/login";
        
        RestResponse<JSONObject> apiRestLoginResponse =
                sendJsonRestRequest(apiLoginEndPoint, "POST", apiRequestHeadersMap, "api_login.json");
        
        String sstCookie = apiRestLoginResponse.getHeadersMap().get("Set-Cookie").get(0);
        
        // Added userId property
        String profileStr = apiRestLoginResponse.getBody().getJSONObject("userLogin").get("profile").toString();
        connectorProperties.setProperty("userId", profileStr.substring(profileStr.lastIndexOf('/') + 1));        
        if (sstCookie == null) {
            throw new IllegalArgumentException();
        }        
        String apiTokenEndPoint = connectorProperties.getProperty("apiUrl") + "/gdc/account/token";        
        apiRequestHeadersMap.put("Cookie", sstCookie);        
        RestResponse<JSONObject> apiRestTokenResponse =
                sendJsonRestRequest(apiTokenEndPoint, "GET", apiRequestHeadersMap);
        
        String ttCookie = apiRestTokenResponse.getHeadersMap().get("Set-Cookie").get(0);
        apiRequestHeadersMap.put("Cookie", ttCookie);
    }
    
    /**
     * Positive test case for createProjectModel method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "gooddata {createProjectModel} integration test with mandatory parameters")
    public void testCreateProjectModelWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createProjectModel");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProjectModel_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/gdc/md/" + connectorProperties.getProperty("projectId")
                        + "/ldm/manage2";
        connectorProperties.put("taskId", esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0)
                .get("link").toString().split("/")[esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0)
                .get("link").toString().split("/").length - 2]);
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createProjectModel_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").getJSONObject(0).get("category"),
                apiRestResponse.getBody().getJSONArray("entries").getJSONObject(0).get("category"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").length(), apiRestResponse.getBody()
                .getJSONArray("entries").length());
    }
    
    /**
     * Negative test case for createProjectModel method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectModelWithMandatoryParameters" }, description = "gooddata {createProjectModel} integration test with mandatory parameters")
    public void testCreateProjectModelNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createProjectModel");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProjectModel_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/gdc/md/invalid/ldm/manage2";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createProjectModel_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertTrue(esbRestResponse.getBody().has("error"));
        Assert.assertTrue(apiRestResponse.getBody().has("error"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message").toString(), apiRestResponse
                .getBody().getJSONObject("error").get("message").toString());
    }
    
    /**
     * Positive test case for createReport method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectModelNegativeCase" }, description = "gooddata {createReport} integration test with mandatory parameters.")
    public void testCreateReportWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createReport");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createReport_mandatory.json");
        
        String reportURI = esbRestResponse.getBody().get("uri").toString();
        connectorProperties.setProperty("reportURI", reportURI);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/gdc/md/" + connectorProperties.getProperty("projectId")
                        + "/obj";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createReport_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().get("uri"));
        Assert.assertFalse(("").equals(esbRestResponse.getBody().get("uri").toString()));
    }
    
    /**
     * Positive test case for createReport method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateReportWithMandatoryParameters" }, description = "gooddata {createReport} integration test with optional parameters.")
    public void testCreateReportWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createReport");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createReport_optional.json");
        
        String reportURI = esbRestResponse.getBody().get("uri").toString();
        connectorProperties.setProperty("reportURI", reportURI);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/gdc/md/" + connectorProperties.getProperty("projectId")
                        + "/obj";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createReport_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertNotNull(esbRestResponse.getBody().get("uri"));
        Assert.assertFalse(("").equals(esbRestResponse.getBody().get("uri").toString()));
    }
    
    /**
     * Negative test case for createReport method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateReportWithOptionalParameters" }, description = "gooddata {createReport} integration test foe negative case.")
    public void testCreateReportWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createReport");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createReport_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/gdc/md/-/obj";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createReport_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("parameters").toString(),
                apiRestResponse.getBody().getJSONObject("error").get("parameters").toString());
    }
    
    /**
     * Positive test case for saveReport method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateReportWithNegativeCase" }, description = "gooddata {saveReport} integration test with mandatory parameters.")
    public void testSaveReportWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:saveReport");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_saveReport_mandatory.json");
        
        String reportURI = esbRestResponse.getBody().get("uri").toString();
        connectorProperties.setProperty("reportURI", reportURI);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/gdc/md/" + connectorProperties.getProperty("projectId")
                        + "/query/reports";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        org.json.JSONArray reportList = apiRestResponse.getBody().getJSONObject("query").getJSONArray("entries");
        
        for (int i = 0; i < reportList.length(); i++) {
            JSONObject obj = (JSONObject) (reportList.get(i));
            if (reportURI.equals(obj.get("link"))) {
                Assert.assertEquals(obj.get("title"), connectorProperties.getProperty("title"));
                Assert.assertEquals(obj.get("summary"), connectorProperties.getProperty("summary"));
                return;
            }
        }
        // In case if previous assertions were skipped
        Assert.assertFalse(true);
    }
    
    /**
     * Positive test case for saveReport method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSaveReportWithMandatoryParameters" }, description = "gooddata {saveReport} integration test with optional parameters.")
    public void testSaveReportWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:saveReport");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_saveReport_optional.json");
        
        String reportURI = esbRestResponse.getBody().get("uri").toString();
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/gdc/md/" + connectorProperties.getProperty("projectId")
                        + "/query/reports";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        org.json.JSONArray reportList = apiRestResponse.getBody().getJSONObject("query").getJSONArray("entries");
        
        for (int i = 0; i < reportList.length(); i++) {
            JSONObject obj = (JSONObject) (reportList.get(i));
            if (reportURI.equals(obj.get("link"))) {
                Assert.assertEquals(obj.get("title"), connectorProperties.getProperty("title"));
                Assert.assertEquals(obj.get("summary"), connectorProperties.getProperty("summary"));
                return;
            }
        }
        // In case if previous assertions were skipped
        Assert.assertFalse(true);
    }
    
    /**
     * Negative test case for saveReport method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSaveReportWithOptionalParameters" }, description = "gooddata {saveReport} integration test for Negative case.")
    public void testSaveReportWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:saveReport");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_saveReport_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/gdc/md/-/obj";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_saveReport_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("parameters").toString(),
                apiRestResponse.getBody().getJSONObject("error").get("parameters").toString());
    }
    
    /**
     * Positive test case for listReports method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSaveReportWithNegativeCase" }, description = "gooddata {listReports} integration test with mandatory parameters.")
    public void testListReportsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listReports");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_saveReport_mandatory.json");
        
        JSONObject esbResObj =
                (JSONObject) (esbRestResponse.getBody().getJSONObject("query").getJSONArray("entries").get(0));
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/gdc/md/" + connectorProperties.getProperty("projectId")
                        + "/query/reports";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResObj =
                (JSONObject) (apiRestResponse.getBody().getJSONObject("query").getJSONArray("entries").get(0));
        
        Assert.assertEquals(esbResObj.get("link"), apiResObj.get("link"));
        Assert.assertEquals(esbResObj.get("title"), apiResObj.get("title"));
        Assert.assertEquals(esbResObj.get("summary"), apiResObj.get("summary"));
    }
    
    /**
     * Negative test case for listReports method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListReportsWithMandatoryParameters" }, description = "gooddata {listReports} integration test for Negative case.")
    public void testListReportsWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listReports");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listReports_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/gdc/md/-/query/reports";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("parameters").toString(),
                apiRestResponse.getBody().getJSONObject("error").get("parameters").toString());
    }
    
    /**
     * Positive test case for getDataset method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListReportsWithNegativeCase" }, description = "gooddata {getDataset} integration test with mandatory parameters.")
    public void testGetDatasetWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getDataset");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDataset_mandatory.json");
        
        JSONObject esbResObj =
                (JSONObject) (esbRestResponse.getBody().getJSONObject("query").getJSONArray("entries").get(0));
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/gdc/md/" + connectorProperties.getProperty("projectId")
                        + "/query/datasets";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONObject apiResObj =
                (JSONObject) (apiRestResponse.getBody().getJSONObject("query").getJSONArray("entries").get(0));
        
        Assert.assertEquals(esbResObj.get("link"), apiResObj.get("link"));
        Assert.assertEquals(esbResObj.get("title"), apiResObj.get("title"));
        Assert.assertEquals(esbResObj.get("author"), apiResObj.get("author"));
    }
    
    /**
     * Negative test case for getDataset method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetDatasetWithMandatoryParameters" }, description = "gooddata {getDataset} integration test for negative case.")
    public void testGetDatasetWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getDataset");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDataset_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/gdc/md/-/query/datasets";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("parameters").toString(),
                apiRestResponse.getBody().getJSONObject("error").get("parameters").toString());
    }
    
    /**
     * Positive test case for getTaskStatus method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetDatasetWithNegativeCase" }, description = "gooddata {getTaskStatus} integration test with mandatory parameters.")
    public void testGetTaskStatusWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getTaskStatus");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTaskStatus_mandatory.json");
        
        esbRestResponse.getBody().getJSONObject("wTaskStatus").get("poll").toString();
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/gdc/md/" + connectorProperties.getProperty("projectId")
                        + "/tasks/" + connectorProperties.getProperty("taskId") + "/status";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("wTaskStatus").get("poll").toString(),
                apiRestResponse.getBody().getJSONObject("wTaskStatus").get("poll").toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("wTaskStatus").get("status").toString(),
                apiRestResponse.getBody().getJSONObject("wTaskStatus").get("status").toString());
    }
    
    /**
     * Negative test case for getTaskStatus method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetTaskStatusWithMandatoryParameters" }, description = "gooddata {getTaskStatus} integration test for negative case.")
    public void testGetTaskStatusWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getTaskStatus");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTaskStatus_optional.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/gdc/md/" + connectorProperties.getProperty("projectId")
                        + "/tasks/-/status";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("parameters").get(0)
                .toString(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("parameters").get(0)
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("parameters").get(1)
                .toString(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("parameters").get(1)
                .toString());
    }
    
    /**
     * Negative test case for executeReport method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetTaskStatusWithNegativeCase" }, description = "gooddata {executeReport} integration test for negative case.")
    public void executeReportWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:executeReport");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_executeReport_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/gdc/xtab2/executor3";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_executeReport_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("parameters").get(0)
                .toString(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("parameters").get(0)
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message").toString(), apiRestResponse
                .getBody().getJSONObject("error").get("message").toString());
    }
    
    /**
     * Positive test case for uploadData method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "executeReportWithNegativeCase" }, description = "gooddata {uploadData} integration test with mandatory parameters")
    public void testUploadDataWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:uploadData");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_uploadData_mandatory.json");
        Thread.sleep(Integer.parseInt(connectorProperties.getProperty("sleepTime").toString()));
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/gdc/md/" + connectorProperties.getProperty("projectId")
                        + "/etl/pull";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_uploadData_mandatory.json");
        connectorProperties.put("uploadTaskId",
                esbRestResponse.getBody().getJSONObject("pullTask").get("uri").toString().split("/")[esbRestResponse
                        .getBody().getJSONObject("pullTask").get("uri").toString().split("/").length - 1]);
        Assert.assertNotNull(esbRestResponse.getBody().getJSONObject("pullTask").get("uri"));
        Assert.assertFalse(esbRestResponse.getBody().getJSONObject("pullTask").get("uri").equals(""));
        Assert.assertNotNull(apiRestResponse.getBody().getJSONObject("pullTask").get("uri"));
        Assert.assertFalse(apiRestResponse.getBody().getJSONObject("pullTask").get("uri").equals(""));
        
    }
    
    /**
     * Negative test case for uploadData method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUploadDataWithMandatoryParameters" }, description = "gooddata {uploadData} integration test negative case")
    public void testUploadDataWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:uploadData");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_uploadData_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/gdc/md/" + connectorProperties.getProperty("projectId")
                        + "/etl/pull";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_uploadData_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertTrue(esbRestResponse.getBody().has("error"));
        Assert.assertTrue(apiRestResponse.getBody().has("error"));
    }
    
    /**
     * Positive test case for getUploadStatus method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUploadDataWithNegativeCase" }, description = "gooddata {getUploadStatus} integration test with mandatory parameters")
    public void testGetUploadStatusWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getUploadStatus");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUploadStatus_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/gdc/md/" + connectorProperties.getProperty("projectId")
                        + "/etl/task/" + connectorProperties.getProperty("uploadTaskId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertTrue(esbRestResponse.getBody().has("taskStatus"));
        Assert.assertTrue(apiRestResponse.getBody().has("taskStatus"));
    }
    
    /**
     * Negative test case for getUploadStatus method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetUploadStatusWithMandatoryParameters" }, description = "gooddata {getUploadStatus} integration test negative case")
    public void testGetUploadStatusNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getUploadStatus");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUploadStatus_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/gdc/md/" + connectorProperties.getProperty("projectId")
                        + "/etl/task/12345";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertTrue(esbRestResponse.getBody().has("error"));
        Assert.assertTrue(apiRestResponse.getBody().has("error"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message").toString(), apiRestResponse
                .getBody().getJSONObject("error").get("message").toString());
    }
}
