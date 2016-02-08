/*
 *  Copyright (c) 2005-2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.integration.test.googleprediction;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

import java.util.HashMap;
import java.util.Map;

public class GooglePredictionConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> parametersMap = new HashMap<String, String>();
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("googleprediction-connector-1.0.0");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);

        String apiEndpointUrl = "https://www.googleapis.com/oauth2/v3/token?grant_type=refresh_token&client_id="+connectorProperties.getProperty("clientId")+
                "&client_secret="+connectorProperties.getProperty("clientSecret")+"&refresh_token="+connectorProperties.getProperty("refreshToken");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpointUrl, "POST", apiRequestHeadersMap);
        final String accessToken = apiRestResponse.getBody().getString("access_token");
        connectorProperties.put("accessToken", accessToken);
        apiRequestHeadersMap.put("Authorization", "Bearer " + accessToken);
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
    }
    
    /**
     * Positive test case for predictTrainedModel method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testInsertTrainedModelWithOptionalParameters" }, groups = { "wso2.esb" }, description = "GooglePrediction {predictTrainedModel} integration test with mandatory parameters.")
    public void testPredictTrainedModelWithMandatoryParameters() throws Exception {
    
        RestResponse<JSONObject> esbRestResponse = null;
        esbRequestHeadersMap.put("Action", "urn:predictTrainedModel");
        parametersMap.put("modelId", connectorProperties.getProperty("modelId"));
        
        long timeout = Long.parseLong(connectorProperties.getProperty("trainModuleTimeout"));
        
        // Wait and retry until the model is created and trained.
        for (long count = 0; count < timeout;) {
            esbRestResponse =
                    sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                            "esb_predictTrainedModel_mandatory.json", parametersMap);
            
            if (esbRestResponse.getHttpStatusCode() == 200) {
                break;
            }
            count = count + Long.parseLong(connectorProperties.getProperty("timeOutDefault"));
            Thread.sleep(count);
        }
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(
                        connectorProperties.getProperty("apiUrl") + "/prediction/v1.6/projects/"
                                + connectorProperties.getProperty("project") + "/trainedmodels/"
                                + connectorProperties.getProperty("modelId") + "/predict", "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("id"), esbRestResponse.getBody().getString("id"));
        Assert.assertEquals(apiRestResponse.getBody().getString("kind").split("#")[0], esbRestResponse.getBody()
                .getString("kind").split("#")[0]);
        // trainingStatus becomes 'DONE' once the prediction is completed.
        Assert.assertEquals(apiRestResponse.getBody().getString("trainingStatus"), "DONE");
    }
    
    /**
     * Positive test case for predictTrainedModel method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testPredictTrainedModelWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "GooglePrediction {predictTrainedModel} integration test with optional parameters.")
    public void testPredictTrainedModelWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:predictTrainedModel");
        parametersMap.put("modelId", connectorProperties.getProperty("modelId"));
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_predictTrainedModel_optional.json",
                        parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(
                        connectorProperties.getProperty("apiUrl") + "/prediction/v1.6/projects/"
                                + connectorProperties.getProperty("project") + "/trainedmodels/"
                                + connectorProperties.getProperty("modelId") + "/predict", "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("id"), esbRestResponse.getBody().getString("id"));
        Assert.assertEquals(apiRestResponse.getBody().getString("kind").split("#")[0], esbRestResponse.getBody()
                .getString("kind").split("#")[0]);
        // trainingStatus becomes 'DONE' once the prediction is completed.
        Assert.assertEquals(apiRestResponse.getBody().getString("trainingStatus"), "DONE");
    }
    
    /**
     * Negative test case for predictTrainedModel method.
     */
    @Test(priority = 1, dependsOnMethods = { "testPredictTrainedModelWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "GooglePrediction {predictTrainedModel} integration test with negative case.")
    public void testPredictTrainedModelWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:predictTrainedModel");
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_predictTrainedModel_negative.json",
                        parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/prediction/v1.6/projects/"
                        + connectorProperties.getProperty("project") + "/trainedmodels/INVALID/predict", "GET",
                        apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").getString("message"), esbRestResponse
                .getBody().getJSONObject("error").getString("message"));
        
    }
    
    /**
     * Positive test case for insertTrainedModel method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "GooglePrediction {insertTrainedModel} integration test with mandatory parameters.")
    public void testInsertTrainedModelWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:insertTrainedModel");
        parametersMap.put("modelId", connectorProperties.getProperty("modelId"));
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_insertTrainedModel_mandatory.json",
                        parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(
                        connectorProperties.getProperty("apiUrl") + "/prediction/v1.6/projects/"
                                + connectorProperties.getProperty("project") + "/trainedmodels/"
                                + connectorProperties.getProperty("modelId"), "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("id"), esbRestResponse.getBody().getString("id"));
        Assert.assertEquals(apiRestResponse.getBody().getString("kind").split("#")[0], esbRestResponse.getBody()
                .getString("kind").split("#")[0]);
        
    }
    
    /**
     * Positive test case for insertTrainedModel method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testInsertTrainedModelWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "GooglePrediction {insertTrainedModel} integration test with optional parameters.")
    public void testInsertTrainedModelWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:insertTrainedModel");
        parametersMap.put("modelId", connectorProperties.getProperty("modelId"));
        parametersMap.put("storageDataLocation", connectorProperties.getProperty("storageDataLocation"));
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_insertTrainedModel_optional.json",
                        parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(
                        connectorProperties.getProperty("apiUrl") + "/prediction/v1.6/projects/"
                                + connectorProperties.getProperty("project") + "/trainedmodels/"
                                + connectorProperties.getProperty("modelId"), "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("id"), esbRestResponse.getBody().getString("id"));
        Assert.assertEquals(apiRestResponse.getBody().getString("kind").split("#")[0], esbRestResponse.getBody()
                .getString("kind").split("#")[0]);
        Assert.assertEquals(connectorProperties.getProperty("storageDataLocation"), esbRestResponse.getBody()
                .getString("storageDataLocation"));
        
    }
    
    /**
     * Negative test case for insertTrainedModel method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "GooglePrediction {insertTrainedModel} integration test with negative case.")
    public void testInsertTrainedModelWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:insertTrainedModel");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_insertTrainedModel_negative.json",
                        parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/prediction/v1.6/projects/"
                        + connectorProperties.getProperty("project") + "/trainedmodels", "POST", apiRequestHeadersMap,
                        "api_insertTrainedModel_negative.json");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("error").getString("message"), esbRestResponse
                .getBody().getJSONObject("error").getString("message"));
        
    }
    
    /**
     * Positive test case for getAnalyzeTrainedModel method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testPredictTrainedModelWithOptionalParameters" }, groups = { "wso2.esb" }, description = "GooglePrediction {getAnalyzeTrainedModel} integration test with mandatory parameters.")
    public void testGetAnalyzeTrainedModelWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getAnalyzeTrainedModel");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAnalyzeTrainedModel_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(
                        connectorProperties.getProperty("apiUrl") + "/prediction/v1.6/projects/"
                                + connectorProperties.getProperty("project") + "/trainedmodels/"
                                + connectorProperties.getProperty("modelId") + "/analyze", "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("kind"), apiRestResponse.getBody().getString("kind"));
    }
    
    /**
     * Positive test case for getAnalyzeTrainedModel method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testPredictTrainedModelWithOptionalParameters" }, groups = { "wso2.esb" }, description = "GooglePrediction {getAnalyzeTrainedModel} integration test with optional parameters.")
    public void testGetAnalyzeTrainedModelWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getAnalyzeTrainedModel");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAnalyzeTrainedModel_optional.json");
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(
                        connectorProperties.getProperty("apiUrl") + "/prediction/v1.6/projects/"
                                + connectorProperties.getProperty("project") + "/trainedmodels/"
                                + connectorProperties.getProperty("modelId") + "/analyze", "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("dataDescription").getJSONObject("outputFeature")
                .toString(), apiRestResponse.getBody().getJSONObject("dataDescription").getJSONObject("outputFeature")
                .toString());
    }
    
    /**
     * Negative test case for getAnalyzeTrainedModel method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "GooglePrediction {getAnalyzeTrainedModel} integration test for negative case.")
    public void testGetAnalyzeTrainedModelNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getAnalyzeTrainedModel");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAnalyzeTrainedModel_negative.json");
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/prediction/v1.6/projects/"
                        + connectorProperties.getProperty("project") + "/trainedmodels/INVALID/analyze", "GET",
                        apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
                .getBody().getJSONObject("error").getString("message"));
    }
    
    /**
     * Positive test case for updateTrainedModel method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testPredictTrainedModelWithOptionalParameters" }, groups = { "wso2.esb" }, description = "GooglePrediction {updateTrainedModel} integration test with mandatory parameters.")
    public void testUpdateTrainedModelWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateTrainedModel");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTrainedModel_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(
                        connectorProperties.getProperty("apiUrl") + "/prediction/v1.6/projects/"
                                + connectorProperties.getProperty("project") + "/trainedmodels/"
                                + connectorProperties.getProperty("modelId"), "PUT", apiRequestHeadersMap,
                        "api_updateTrainedModel_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("kind"), apiRestResponse.getBody().getString("kind"));
    }
    
    /**
     * Positive test case for updateTrainedModel method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testUpdateTrainedModelWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "GooglePrediction {updateTrainedModel} integration test with optional parameters.")
    public void testUpdateTrainedModelWithOptionalParameters() throws Exception {
    
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        esbRequestHeadersMap.put("Action", "urn:updateTrainedModel");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTrainedModel_optional.json");
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(
                        connectorProperties.getProperty("apiUrl") + "/prediction/v1.6/projects/"
                                + connectorProperties.getProperty("project") + "/trainedmodels/"
                                + connectorProperties.getProperty("modelId") + "?fields=id", "PUT",
                        apiRequestHeadersMap, "api_updateTrainedModel_optional.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }
    
    /**
     * Negative test case for updateTrainedModel method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "GooglePrediction {updateTrainedModel} integration test for negative case.")
    public void testUpdateTrainedModelNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateTrainedModel");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTrainedModel_negative.json");
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/prediction/v1.6/projects/"
                        + connectorProperties.getProperty("project") + "/trainedmodels/INVALID", "GET",
                        apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
                .getBody().getJSONObject("error").getString("message"));
    }
    
    /**
     * Positive test case for listTrainedModels method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "GooglePrediction {listTrainedModels} integration test with mandatory parameters.")
    public void testListTrainedModelsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listTrainedModels");
        
        parametersMap.put("project", connectorProperties.getProperty("project"));
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTrainedModels_mandatory.json",
                        parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/prediction/v1.6/projects/"
                        + connectorProperties.getProperty("project") + "/trainedmodels/list", "GET",
                        apiRequestHeadersMap);
        
        int noOfModels = apiRestResponse.getBody().getJSONArray("items").length();
        
        Assert.assertEquals(apiRestResponse.getBody().getString("kind"), esbRestResponse.getBody().getString("kind"));
        Assert.assertEquals(noOfModels, esbRestResponse.getBody().getJSONArray("items").length());
        
        // Iterating through list and asserting the id and kind values
        for (int i = 0; i < noOfModels; i++) {
            Assert.assertEquals(apiRestResponse.getBody().getJSONArray("items").getJSONObject(i).getString("id"),
                    esbRestResponse.getBody().getJSONArray("items").getJSONObject(i).getString("id"));
            Assert.assertEquals(apiRestResponse.getBody().getJSONArray("items").getJSONObject(i).getString("kind"),
                    esbRestResponse.getBody().getJSONArray("items").getJSONObject(i).getString("kind"));
        }
    }
    
    /**
     * Positive test case for listTrainedModels method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testPredictTrainedModelWithOptionalParameters" }, groups = { "wso2.esb" }, description = "GooglePrediction {listTrainedModels} integration test with optional parameters.")
    public void testListTrainedModelsWithOptionalParameters() throws Exception {
    
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        esbRequestHeadersMap.put("Action", "urn:listTrainedModels");
        
        parametersMap.put("project", connectorProperties.getProperty("project"));
        parametersMap.put("maxResults", connectorProperties.getProperty("maxResults"));
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTrainedModels_optional.json",
                        parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(
                        connectorProperties.getProperty("apiUrl") + "/prediction/v1.6/projects/"
                                + connectorProperties.getProperty("project") + "/trainedmodels/list?maxResults="
                                + connectorProperties.getProperty("maxResults") + "&pageToken="
                                + connectorProperties.getProperty("pageToken"), "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("kind"), esbRestResponse.getBody().getString("kind"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("items").length(), esbRestResponse.getBody()
                .getJSONArray("items").length());
        Assert.assertTrue(apiRestResponse.getBody().getJSONArray("items").length() <= Integer
                .parseInt(connectorProperties.getProperty("maxResults")));
        
    }
    
    /**
     * Negative test case for listTrainedModels method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "GooglePrediction {listTrainedModels} integration test with Negative case.")
    public void testListTrainedModelsWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listTrainedModels");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTrainedModels_invalid.json",
                        parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(connectorProperties.getProperty("apiUrl")
                        + "/prediction/v1.6/projects/INVALID/trainedmodels/list", "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
                .getBody().getJSONObject("error").getString("message"));
        
    }
    
    /**
     * Positive test case for deleteTrainedModel method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testListTrainedModelsWithMandatoryParameters",
            "testUpdateTrainedModelWithOptionalParameters" }, groups = { "wso2.esb" }, description = "GooglePrediction {deleteTrainedModel} integration test with mandatory parameters.")
    public void testDeleteTrainedModelWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteTrainedModel");
        
        parametersMap.put("project", connectorProperties.getProperty("project"));
        parametersMap.put("modelId", connectorProperties.getProperty("modelId"));
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteTrainedModel_mandatory.json",
                        parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(
                        connectorProperties.getProperty("apiUrl") + "/prediction/v1.6/projects/"
                                + connectorProperties.getProperty("project") + "/trainedmodels/"
                                + connectorProperties.getProperty("modelId"), "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());

    }
    
    /**
     * Negative test case for deleteTrainedModel method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "GooglePrediction {deleteTrainedModel} integration test with Negative case.")
    public void testDeleteTrainedModelWithNegativeCase() throws Exception {
    
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        esbRequestHeadersMap.put("Action", "urn:deleteTrainedModel");
        
        parametersMap.put("project", connectorProperties.getProperty("project"));
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteTrainedModel_invalid.json",
                        parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/prediction/v1.6/projects/"
                        + connectorProperties.getProperty("project") + "/trainedmodels/INVALID", "DELETE",
                        apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());

    }
    
    /**
     * Positive test case for predictHostedModel method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "GooglePrediction {predictHostedModel} integration test with mandatory parameters.")
    public void testPredictHostedModelWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:predictHostedModel");
        
        parametersMap.put("project", connectorProperties.getProperty("projectForHost"));
        parametersMap.put("hostedModelName", connectorProperties.getProperty("hostedModelName"));
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_predictHostedModel_mandatory.json",
                        parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(
                        connectorProperties.getProperty("apiUrl") + "/prediction/v1.6/projects/"
                                + connectorProperties.getProperty("projectForHost") + "/hostedmodels/"
                                + connectorProperties.getProperty("hostedModelName") + "/predict", "POST",
                        apiRequestHeadersMap, "api_predictHostedModel.json", null);
        
        int lenghtOfResponse = esbRestResponse.getBody().getJSONArray("outputMulti").length();
        
        Assert.assertEquals(apiRestResponse.getBody().getString("kind"), esbRestResponse.getBody().getString("kind"));
        Assert.assertEquals(apiRestResponse.getBody().getString("id"), esbRestResponse.getBody().getString("id"));
        Assert.assertEquals(apiRestResponse.getBody().getString("outputLabel"),
                esbRestResponse.getBody().getString("outputLabel"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("outputMulti").length(), lenghtOfResponse);
        
        // Iterating through list and asserting the label and score values
        for (int i = 0; i < lenghtOfResponse; i++) {
            Assert.assertEquals(
                    apiRestResponse.getBody().getJSONArray("outputMulti").getJSONObject(i).getString("label"),
                    esbRestResponse.getBody().getJSONArray("outputMulti").getJSONObject(i).getString("label"));
            Assert.assertEquals(
                    apiRestResponse.getBody().getJSONArray("outputMulti").getJSONObject(i).getString("score"),
                    esbRestResponse.getBody().getJSONArray("outputMulti").getJSONObject(i).getString("score"));
        }
    }
    
    /**
     * Negative test case for predictHostedModel method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "GooglePrediction {predictHostedModel} integration test with Negative case.")
    public void testPredictHostedModelWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:predictHostedModel");
        
        parametersMap.put("project", connectorProperties.getProperty("projectForHost"));
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_predictHostedModel_invalid.json",
                        parametersMap);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(connectorProperties.getProperty("apiUrl") + "/prediction/v1.6/projects/"
                        + connectorProperties.getProperty("projectForHost") + "/hostedmodels/INVALID/predict", "POST",
                        apiRequestHeadersMap, "api_predictHostedModel.json", null);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
                .getBody().getJSONObject("error").getString("message"));
        
    }
    
}
