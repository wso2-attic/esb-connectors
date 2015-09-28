/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.connector.integration.test.eloquaStandardAPI;

import java.io.IOException;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.lang.Byte;
import java.lang.String;
import java.lang.System;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;
import org.apache.commons.codec.binary.Base64;

public class EloquaConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    private String apiRequestUrl;

    private String apiKey;

    private String repoLocation = "";

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init("eloquaStandardAPI-connector-1.0.0");
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            repoLocation = System.getProperty("connector_repo").replace("\\", "/");
        } else {
            repoLocation = System.getProperty("connector_repo").replace("/", "/");
        }
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        byte[] encodedString = Base64.encodeBase64((connectorProperties.getProperty("siteName")
                + "\\" + connectorProperties.getProperty("username") + ":" + connectorProperties.getProperty("password"))
                .getBytes());
        apiRequestHeadersMap.put("Authorization", "Basic " + new String(encodedString));
        String apiEndPoint = "https://login.eloqua.com/id";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        apiRequestUrl = apiRestResponse.getBody().getJSONObject("urls").getJSONObject("apis").getJSONObject("rest").getString("standard").replace("{version}", "2.0");
        connectorProperties.setProperty("emailAddressToUpdate", System.currentTimeMillis() + connectorProperties
                .getProperty("emailAddressToUpdate"));
        connectorProperties.setProperty("emailAddress", System.currentTimeMillis() + connectorProperties
                .getProperty("emailAddress"));
        connectorProperties.setProperty("emailAddressToUpdateOptional", System.currentTimeMillis() + connectorProperties
                .getProperty("emailAddressToUpdateOptional"));
        connectorProperties.setProperty("emailAddressOptional", System.currentTimeMillis() + connectorProperties
                .getProperty("emailAddressOptional"));
        connectorProperties.setProperty("assetName", System.currentTimeMillis() + connectorProperties
                .getProperty("assetName"));
        connectorProperties.setProperty("contactListName", System.currentTimeMillis() + connectorProperties
                .getProperty("contactListName"));
        connectorProperties.setProperty("contactListNameOptional", System.currentTimeMillis() + connectorProperties
                .getProperty("contactListNameOptional"));
    }

    /**
     * Positive test case for createACampaign method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {createACampaign} integration test with " +
            "mandatory parameters.")
    public void testCreateACampaignWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "createACampaign");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createACampaignWithMandatoryParameters.json");
        String campaignId = esbRestResponse.getBody().getString("id");
        String apiEndPoint = apiRequestUrl + "assets/campaign/" + campaignId + "?depth=complete";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getBody().getString("name"),
                connectorProperties.getProperty("campaignName").replaceAll(" ", "%20"));
    }

    /**
     * Positive test case for createACampaign method with optional parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {createACampaign} integration test " +
            "with optional parameters.")
    public void testCreateACampaignWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        esbRequestHeadersMap.put("Action", "createACampaign");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createACampaignWithOptionalParameters.json");
        String campaignId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("campaignId", campaignId);
        String apiEndPoint = apiRequestUrl + "assets/campaign/" + campaignId + "?depth=complete";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getBody().getString("name"), connectorProperties.getProperty("campaignNameOptional").replaceAll(" ", "%20"));
        Assert.assertEquals(apiRestResponse.getBody().getString("startAt"), connectorProperties.getProperty("startAt"));
        Assert.assertEquals(apiRestResponse.getBody().getString("region"), connectorProperties.getProperty("region").replaceAll(" ", "%20"));
    }

    /**
     * Negative test case for createACampaign method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {createACampaign} integration test with " +
            "negative case.")
    public void testCreateACampaignWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "createACampaign");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createACampaignForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "assets/campaign";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_createACampaignForNegativeCase.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for createAContactList method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {createAContactList} integration test with " +
            "mandatory parameters.")
    public void testCreateAContactListWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "createAContactList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createAContactListWithMandatoryParameters.json");
        String contactListId = esbRestResponse.getBody().getString("id");
        String apiEndPoint = apiRequestUrl + "assets/contact/list/" + contactListId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
    }

    /**
     * Positive test case for createAContactList method with optional parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {createAContactList} integration test with " +
            "optional parameters.")
    public void testCreateAContactListWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "createAContactList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createAContactListWithOptionalParameters" +
                        ".json");
        String contactListId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("contactListId", contactListId);
        String apiEndPoint = apiRequestUrl + "assets/contact/list/" + contactListId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("scope"), apiRestResponse.getBody()
                .getString("scope"));
    }

    /**
     * Negative test case for createAContactList method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {createAContactList} integration test" +
            " with negative case.")
    public void testCreateAContactListWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "createAContactList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createAContactListForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "assets/contact/list";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createAContactListForNegativeCase.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for createAContact method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {createAContact} integration test with mandatory parameters.")
    public void testCreateAContactWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "createAContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createAContactWithMandatoryParameters.json");
        String contactId = esbRestResponse.getBody().getString("id");
        String apiEndPoint = apiRequestUrl + "data/contact/" + contactId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("emailAddress"),
                apiRestResponse.getBody().getString("emailAddress"));
    }

    /**
     * Positive test case for createAContact method with optional parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAnAccountWithOptionalParameters"},
            description = "Eloqua {createAContact} integration test with Optional parameters.")
    public void testCreateAContactWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "createAContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createAContactWithOptionalParameters.json");
        String contactId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("contactId", contactId);
        String apiEndPoint = apiRequestUrl + "data/contact/" + contactId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("emailAddress"),
                apiRestResponse.getBody().getString("emailAddress"));
        Assert.assertEquals(esbRestResponse.getBody().getString("firstName"), apiRestResponse.getBody()
                .getString("firstName"));
        Assert.assertEquals(esbRestResponse.getBody().getString("lastName"), apiRestResponse.getBody()
                .getString("lastName"));

    }

    /**
     * Negative test case for createAContact method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {createAContact} integration test with " +
            "negative case.")
    public void testCreateAContactWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "createAContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createAContactForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "data/contact";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createAContactForNegativeCase" +
                        ".json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for createAnEmailGroup method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {createAnEmailGroup} integration test with mandatory parameters.")
    public void testCreateAnEmailGroupWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "createAnEmailGroup");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createAnEmailGroupWithMandatoryParameters.json");
        String emailGroupId = esbRestResponse.getBody().getString("id");
        String apiEndPoint = apiRequestUrl + "assets/email/group/" + emailGroupId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("subscriptionLandingPageId"),
                apiRestResponse.getBody().getString("subscriptionLandingPageId"));
        Assert.assertEquals(esbRestResponse.getBody().getString("unsubscriptionLandingPageId"),
                apiRestResponse.getBody().getString("unsubscriptionLandingPageId"));
    }

    /**
     * Positive test case for createAnEmailGroup method with optional parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {createAnEmailGroup} integration test with " +
            "Optional parameters.")
    public void testCreateAnEmailGroupWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "createAnEmailGroup");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createAnEmailGroupWithOptionalParameters.json");
        String emailGroupId = esbRestResponse.getBody().getString("id");
        String apiEndPoint = apiRequestUrl + "assets/email/group/" + emailGroupId;
        connectorProperties.setProperty("emailGroupId", emailGroupId);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("subscriptionLandingPageId"),
                apiRestResponse.getBody().getString("subscriptionLandingPageId"));
        Assert.assertEquals(esbRestResponse.getBody().getString("unsubscriptionLandingPageId"),
                apiRestResponse.getBody().getString("unsubscriptionLandingPageId"));
        Assert.assertEquals(esbRestResponse.getBody().getString("isVisibleInPublicSubscriptionList"),
                apiRestResponse.getBody().getString("isVisibleInPublicSubscriptionList"));
    }

    /**
     * Negative test case for createAnEmailGroup method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {createAnEmailGroup} integration test with " +
            "negative case.")
    public void testCreateAnEmailGroupWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "createAnEmailGroup");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createAnEmailGroupForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "assets/email/group";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createAnEmailGroupForNegativeCase.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for createAnAccount method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {createAnAccount} integration test with " +
            "mandatory parameters.")
    public void testCreateAnAccountWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "createAnAccount");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createAnAccountWithMandatoryParameters.json");
        String accountId = esbRestResponse.getBody().getString("id");
        String apiEndPoint = apiRequestUrl + "data/account/" + accountId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"),
                apiRestResponse.getBody().getString("name"));
    }

    /**
     * Positive test case for createAnAccount method with optional parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "Eloqua {createAnAccount} integration test with Optional parameters.")
    public void testCreateAnAccountWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "createAnAccount");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createAnAccountWithOptionalParameters.json");
        String accountId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("accountId", accountId);
        String apiEndPoint = apiRequestUrl + "data/account/" + accountId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("address1"), apiRestResponse.getBody()
                .getString("address1"));
    }

    /**
     * Positive test case for createAnExternalAsset method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {createAnExternalAsset} integration test with mandatory parameters.")
    public void testCreateAnExternalAssetWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "createAnExternalAsset");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createAnExternalAssetWithMandatoryParameters.json");
        String externalAssetId = esbRestResponse.getBody().getString("id");
        String apiEndPoint = apiRequestUrl + "assets/external/" + externalAssetId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"),
                apiRestResponse.getBody().getString("name"));
    }

    /**
     * Positive test case for createAnExternalAsset method with optional parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {createAnExternalAsset} integration test with" +
            " Optional parameters.")
    public void testCreateAnExternalAssetWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "createAnExternalAsset");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createAnExternalAssetWithOptionalParameters.json");
        String externalAssetId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("externalAssetId", externalAssetId);
        String apiEndPoint = apiRequestUrl + "assets/external/" + externalAssetId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("externalAssetTypeId"), apiRestResponse.getBody()
                .getString("externalAssetTypeId"));
    }

    /**
     * Negative test case for createAnExternalAsset method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {createAnExternalAsset} integration test with" +
            " negative case.")
    public void testCreateAnExternalAssetWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "createAnExternalAsset");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createAnExternalAssetForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "assets/external";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createAnExternalAssetForNegativeCase" +
                        ".json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for createAVisitorNotification method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testGetCurrentUserWithOptionalParameters"}, description = "Eloqua {createAVisitorNotification} integration test" +
            " with mandatory parameters.")
    public void testCreateAVisitorNotificationWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "createAVisitorNotification");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createAVisitorNotificationWithMandatoryParameters.json");
        String visitorId = esbRestResponse.getBody().getString("id");
        String apiEndPoint = apiRequestUrl + "settings/notification/visitor/" + visitorId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("userId"),
                apiRestResponse.getBody().getString("userId"));
    }

    /**
     * Positive test case for createAVisitorNotification method with optional parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testGetCurrentUserWithOptionalParameters"},
            description = "Eloqua {createAVisitorNotification} integration test with Optional parameters.")
    public void testCreateAVisitorNotificationWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "createAVisitorNotification");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createAVisitorNotificationWithOptionalParameters.json");
        String visitorId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("visitorId", visitorId);
        String apiEndPoint = apiRequestUrl + "settings/notification/visitor/" + visitorId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("userId"),
                apiRestResponse.getBody().getString("userId"));
        Assert.assertEquals(esbRestResponse.getBody().getString("emailAddress"), apiRestResponse.getBody()
                .getString("emailAddress"));
    }

    /**
     * Negative test case for createAVisitorNotification method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testGetCurrentUserWithOptionalParameters"},
            description = "Eloqua {createAVisitorNotification} integration test with negative case.")
    public void testCreateAVisitorNotificationWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "createAVisitorNotification");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createAVisitorNotificationForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "settings/notification/visitor";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createAVisitorNotificationForNegativeCase.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for updateACampaign method with mandatory parameters.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateACampaignWithOptionalParameters"},
            description = "Eloqua {updateACampaign} integration test with mandatory parameters.")
    public void testUpdateACampaignWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateACampaign");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "updateACampaignWithMandatoryParameters.json");
        String campaignId = connectorProperties.getProperty("campaignId");
        String apiEndPoint = apiRequestUrl + "assets/campaign/" + campaignId + "?depth=complete";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().getString("name"), connectorProperties.getProperty("campaignName").replaceAll(" ", "%20"));
    }

    /**
     * Positive test case for updateACampaign method with optional parameters.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateACampaignWithOptionalParameters"}, description = "Eloqua {updateACampaign} integration test " +
            "with optional parameters.")
    public void testUpdateACampaignWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        esbRequestHeadersMap.put("Action", "updateACampaign");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateACampaignWithOptionalParameters.json");
        String campaignId = connectorProperties.getProperty("campaignId");
        String apiEndPoint = apiRequestUrl + "assets/campaign/" + campaignId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().getString("name"), connectorProperties.getProperty("campaignNameOptional").replaceAll(" ", "%20"));
        Assert.assertEquals(apiRestResponse.getBody().getString("startAt"), connectorProperties.getProperty("startAt"));
        Assert.assertEquals(apiRestResponse.getBody().getString("region"), connectorProperties.getProperty("region"));
    }

    /**
     * Negative test case for updateACampaign method.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateACampaignWithOptionalParameters"},
            description = "Eloqua {updateACampaign} integration test with negative case.")
    public void testUpdateACampaignWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateACampaign");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateACampaignForNegativeCase.json");
        String campaignId = connectorProperties.getProperty("campaignId");
        String apiEndPoint = apiRequestUrl + "assets/campaign/" + campaignId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap,
                "api_updateACampaignForNegativeCase.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for updateAContactList method with mandatory parameters.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAContactListWithOptionalParameters"},
            description = "Eloqua {updateAContactList} integration test with mandatory parameters.")
    public void testUpdateAContactListWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateAContactList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateAContactListWithMandatoryParameters.json");
        String contactListId = connectorProperties.getProperty("contactListId");
        String apiEndPoint = apiRequestUrl + "assets/contact/list/" + contactListId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
    }

    /**
     * Positive test case for updateAContactList method with optional parameters.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAContactListWithOptionalParameters"}, description = "Eloqua {updateAContactList} integration test with optional parameters.")
    public void testUpdateAContactListWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateAContactList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateAContactListWithOptionalParameters" +
                        ".json");
        String contactListId = connectorProperties.getProperty("contactListId");
        String apiEndPoint = apiRequestUrl + "assets/contact/list/" + contactListId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("scope"), apiRestResponse.getBody()
                .getString("scope"));
    }

    /**
     * Negative test case for updateAContactList method.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAContactListWithOptionalParameters"
    }, description = "Eloqua {updateAContactList} integration test" +
            " with negative case.")
    public void testUpdateAContactListWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateAContactList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateAContactListForNegativeCase.json");
        String contactListId = connectorProperties.getProperty("contactListId");
        String apiEndPoint = apiRequestUrl + "assets/contact/list/" + contactListId;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateAContactListForNegativeCase.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for updateAContact method with mandatory parameters.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAContactWithOptionalParameters"},
            description = "Eloqua {updateAContact} integration test with mandatory parameters.")
    public void testUpdateAContactWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateAContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateAContactWithMandatoryParameters.json");
        String contactId = connectorProperties.getProperty("contactId");
        String apiEndPoint = apiRequestUrl + "data/contact/" + contactId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("email"),
                apiRestResponse.getBody().getString("email"));
    }

    /**
     * Positive test case for updateAContact method with optional parameters.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAContactWithOptionalParameters"},
            description = "Eloqua {updateAContact} integration test with Optional parameters.")
    public void testUpdateAContactWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateAContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateAContactWithOptionalParameters.json");
        String contactId = connectorProperties.getProperty("contactId");
        String apiEndPoint = apiRequestUrl + "data/contact/" + contactId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("email"),
                apiRestResponse.getBody().getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getString("firstName"), apiRestResponse.getBody()
                .getString("firstName"));
        Assert.assertEquals(esbRestResponse.getBody().getString("lastName"), apiRestResponse.getBody()
                .getString("lastName"));

    }

    /**
     * Negative test case for updateAContact method.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAContactWithOptionalParameters"},
            description = "Eloqua {updateAContact} integration test with negative case.")
    public void testUpdateAContactWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateAContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateAContactForNegativeCase.json");
        String contactId = connectorProperties.getProperty("contactId");
        String apiEndPoint = apiRequestUrl + "data/contact/" + contactId;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateAContactForNegativeCase" +
                        ".json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for updateASubscribedEmailGroupOfAContact method with mandatory parameters.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testGetAllContactsWithOptionalParameters",
            "testCreateAnEmailGroupWithOptionalParameters",
            "testCreateAContactWithOptionalParameters"},
            description = "Eloqua {updateASubscribedEmailGroupOfAContact} integration test with mandatory parameters.")
    public void testUpdateASubscribedEmailGroupOfAContactWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateASubscribedEmailGroupOfAContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateASubscribedEmailGroupOfAContactWithMandatoryParameters.json");
        String contactId = connectorProperties.getProperty("contactId");
        String apiEndPoint = apiRequestUrl + "data/contact/" + contactId + "/email/groups/subscription";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("subscriptionLandingPageId"),
                apiRestResponse.getBody().getString("subscriptionLandingPageId"));
        Assert.assertEquals(esbRestResponse.getBody().getString("unsubscriptionLandingPageId"),
                apiRestResponse.getBody().getString("unsubscriptionLandingPageId"));
    }

    /**
     * Positive test case for updateASubscribedEmailGroupOfAContact method with optional parameters.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testGetAllContactsWithOptionalParameters",
            "testCreateAnEmailGroupWithOptionalParameters",
            "testCreateAContactWithOptionalParameters"}, description = "Eloqua {updateASubscribedEmailGroupOfAContact} integration test with Optional parameters.")
    public void testUpdateASubscribedEmailGroupOfAContactWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateASubscribedEmailGroupOfAContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateASubscribedEmailGroupOfAContactWithOptionalParameters.json");
        String contactId = connectorProperties.getProperty("contactId");
        String apiEndPoint = apiRequestUrl + "data/contact/" + contactId + "/email/groups/subscription";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("subscriptionLandingPageId"),
                apiRestResponse.getBody().getString("subscriptionLandingPageId"));
        Assert.assertEquals(esbRestResponse.getBody().getString("unsubscriptionLandingPageId"),
                apiRestResponse.getBody().getString("unsubscriptionLandingPageId"));
        Assert.assertEquals(esbRestResponse.getBody().getString("isVisibleInPublicSubscriptionList"),
                apiRestResponse.getBody().getString("isVisibleInPublicSubscriptionList"));
    }

    /**
     * Negative test case for updateASubscribedEmailGroupOfAContact method.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testGetAllContactsWithOptionalParameters",
            "testCreateAnEmailGroupWithOptionalParameters",
            "testCreateAContactWithOptionalParameters"}, description = "Eloqua {updateASubscribedEmailGroupOfAContact} " +
            "integration test with negative case.")
    public void testUpdateASubscribedEmailGroupOfAContactWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateASubscribedEmailGroupOfAContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateASubscribedEmailGroupOfAContactForNegativeCase.json");
        String contactId = connectorProperties.getProperty("contactId");
        String groupId = connectorProperties.getProperty("groupId");
        String apiEndPoint = apiRequestUrl + "data/contact/" + contactId + "/email/group/" + groupId + "/subscription";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateASubscribedEmailGroupOfAContactForNegativeCase.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for updateAnEmailGroup method with mandatory parameters.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAnEmailGroupWithOptionalParameters"},
            description = "Eloqua {updateAnEmailGroup} integration test with mandatory parameters.")
    public void testUpdateAnEmailGroupWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateAnEmailGroup");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap,
                        "updateAnEmailGroupWithMandatoryParameters.json");
        String emailGroupId = connectorProperties.getProperty("emailGroupId");
        String apiEndPoint = apiRequestUrl + "assets/email/group/" + emailGroupId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("subscriptionLandingPageId"),
                apiRestResponse.getBody().getString("subscriptionLandingPageId"));
        Assert.assertEquals(esbRestResponse.getBody().getString("unsubscriptionLandingPageId"),
                apiRestResponse.getBody().getString("unsubscriptionLandingPageId"));
    }

    /**
     * Positive test case for updateAnEmailGroup method with optional parameters.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAnEmailGroupWithOptionalParameters"
    }, description = "Eloqua {updateAnEmailGroup} integration test with Optional parameters.")
    public void testUpdateAnEmailGroupWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateAnEmailGroup");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap,
                        "updateAnEmailGroupWithOptionalParameters.json");
        String emailGroupId = connectorProperties.getProperty("emailGroupId");
        String apiEndPoint = apiRequestUrl + "assets/email/group/" + emailGroupId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("subscriptionLandingPageId"),
                apiRestResponse.getBody().getString("subscriptionLandingPageId"));
        Assert.assertEquals(esbRestResponse.getBody().getString("unsubscriptionLandingPageId"),
                apiRestResponse.getBody().getString("unsubscriptionLandingPageId"));
        Assert.assertEquals(esbRestResponse.getBody().getString("isVisibleInPublicSubscriptionList"),
                apiRestResponse.getBody().getString("isVisibleInPublicSubscriptionList"));
    }

    /**
     * Negative test case for updateAnEmailGroup method.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAnEmailGroupWithOptionalParameters"},
            description = "Eloqua {updateAnEmailGroup} integration test with negative case.")
    public void testUpdateAnEmailGroupWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateAnEmailGroup");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap,
                        "updateAnEmailGroupForNegativeCase.json");
        String emailGroupId = connectorProperties.getProperty("emailGroupId");
        String apiEndPoint = apiRequestUrl + "assets/email/group/" + emailGroupId;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap,
                        "api_updateAnEmailGroupForNegativeCase.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for updateAnAccount method with mandatory parameters.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAnAccountWithOptionalParameters"},
            description = "Eloqua {updateAnAccount} integration test with mandatory parameters.")
    public void testUpdateAnAccountWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateAnAccount");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateAnAccountWithMandatoryParameters.json");
        String accountId = connectorProperties.getProperty("accountId");
        String apiEndPoint = apiRequestUrl + "data/account/" + accountId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"),
                apiRestResponse.getBody().getString("name"));
    }

    /**
     * Positive test case for updateAnAccount method with optional parameters.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAnAccountWithOptionalParameters"},
            description = "Eloqua {updateAnAccount} integration test with Optional parameters.")
    public void testUpdateAnAccountWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateAnAccount");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateAnAccountWithOptionalParameters.json");
        String accountId = connectorProperties.getProperty("accountId");
        String apiEndPoint = apiRequestUrl + "data/account/" + accountId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("address1"), apiRestResponse.getBody()
                .getString("address1"));
    }

    /**
     * Negative test case for updateAnAccount method.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAnAccountWithOptionalParameters"},
            description = "Eloqua {updateAnAccount} integration test with negative case.")
    public void testUpdateAnAccountWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateAnAccount");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateAnAccountForNegativeCase.json");
        String accountId = connectorProperties.getProperty("accountId");
        String apiEndPoint = apiRequestUrl + "data/account/" + accountId;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateAnAccountForNegativeCase" +
                        ".json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for updateAnExternalAsset method with mandatory parameters.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAnExternalAssetWithOptionalParameters"
    }, description = "Eloqua {updateAnExternalAsset} integration test with mandatory parameters.")
    public void testUpdateAnExternalAssetWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateAnExternalAsset");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateAnExternalAssetWithMandatoryParameters.json");
        String externalAssetId = connectorProperties.getProperty("externalAssetId");
        String apiEndPoint = apiRequestUrl + "assets/external/" + externalAssetId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"),
                apiRestResponse.getBody().getString("name"));
    }

    /**
     * Positive test case for updateAnExternalAsset method with optional parameters.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {
            "testCreateAnExternalAssetWithOptionalParameters"},
            description = "Eloqua {updateAnExternalAsset} integration test with Optional parameters.")
    public void testUpdateAnExternalAssetWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateAnExternalAsset");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateAnExternalAssetWithOptionalParameters.json");
        String externalAssetId = connectorProperties.getProperty("externalAssetId");
        String apiEndPoint = apiRequestUrl + "assets/external/" + externalAssetId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("externalAssetTypeId"), apiRestResponse.getBody()
                .getString("externalAssetTypeId"));
    }

    /**
     * Negative test case for updateAnExternalAsset method.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAnExternalAssetWithOptionalParameters"
    }, description = "Eloqua {updateAnExternalAsset} integration test with negative case.")
    public void testUpdateAnExternalAssetWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateAnExternalAsset");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateAnExternalAssetForNegativeCase.json");
        String externalAssetId = connectorProperties.getProperty("externalAssetId");
        String apiEndPoint = apiRequestUrl + "assets/external/" + externalAssetId;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateAnExternalAssetForNegativeCase" +
                        ".json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for updateAVisitorNotification method with mandatory parameters.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAVisitorNotificationWithOptionalParameters"},
            description = "Eloqua {updateAVisitorNotification} integration test with mandatory parameters.")
    public void testUpdateAVisitorNotificationWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateAVisitorNotification");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateAVisitorNotificationWithMandatoryParameters.json");
        String visitorId = connectorProperties.getProperty("visitorId");
        String apiEndPoint = apiRequestUrl + "settings/notification/visitor/" + visitorId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("userId"),
                apiRestResponse.getBody().getString("userId"));
    }

    /**
     * Positive test case for updateAVisitorNotification method with optional parameters.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {
            "testCreateAVisitorNotificationWithOptionalParameters"},
            description = "Eloqua {updateAVisitorNotification} integration test with Optional parameters.")
    public void testUpdateAVisitorNotificationWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateAVisitorNotification");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateAVisitorNotificationWithOptionalParameters.json");
        String visitorId = connectorProperties.getProperty("visitorId");
        String apiEndPoint = apiRequestUrl + "settings/notification/visitor/" + visitorId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("userId"),
                apiRestResponse.getBody().getString("userId"));
        Assert.assertEquals(esbRestResponse.getBody().getString("emailAddress"), apiRestResponse.getBody()
                .getString("emailAddress"));

    }

    /**
     * Negative test case for updateAVisitorNotification method.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {
            "testCreateAVisitorNotificationWithOptionalParameters"},
            description = "Eloqua {updateAVisitorNotification} integration test with negative case.")
    public void testUpdateAVisitorNotificationWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateAVisitorNotification");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateAVisitorNotificationForNegativeCase.json");
        String visitorId = connectorProperties.getProperty("visitorId");
        String apiEndPoint = apiRequestUrl + "settings/notification/visitor/" + visitorId;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateAVisitorNotificationForNegativeCase.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for updateAUser method with mandatory parameters.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testGetCurrentUserWithOptionalParameters"},
            description = "Eloqua {updateAUser} integration test with mandatory parameters.")
    public void testUpdateAUserWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateAUser");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateAUserWithMandatoryParameters.json");
        String userId = connectorProperties.getProperty("userId");
        String apiEndPoint = apiRequestUrl + "system/user/" + userId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("userId"),
                apiRestResponse.getBody().getString("userId"));
    }

    /**
     * Positive test case for updateAUser method with optional parameters.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testGetCurrentUserWithOptionalParameters"}, description = "Eloqua {updateAUser} integration test with Optional parameters.")
    public void testUpdateAUserWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateAUser");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateAUserWithOptionalParameters.json");
        String userId = connectorProperties.getProperty("userId");
        String apiEndPoint = apiRequestUrl + "system/user/" + userId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("name"),
                apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("userId"),
                apiRestResponse.getBody().getString("userId"));
        Assert.assertEquals(esbRestResponse.getBody().getString("emailAddress"), apiRestResponse.getBody()
                .getString("emailAddress"));
    }

    /**
     * Negative test case for updateAUser method.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testGetCurrentUserWithOptionalParameters"},
            description = "Eloqua {updateAUser} integration test with negative case.")
    public void testUpdateAUserWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "updateAUser");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "updateAUserForNegativeCase.json");
        String userId = connectorProperties.getProperty("userId");
        String apiEndPoint = apiRequestUrl + "system/user/" + userId;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateAUserForNegativeCase.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAnAccount method with mandatory parameters.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"},
            dependsOnMethods = {"testCreateAnAccountWithOptionalParameters"},
            description = "Eloqua {getAnAccount} integration test with mandatory parameters.")
    public void testGetAnAccountWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAnAccount");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAnAccountWithMandatoryParameters" +
                        ".json");
        String accountId = connectorProperties.getProperty("accountId");
        String apiEndPoint = apiRequestUrl + "data/account/" + accountId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAnAccount method with optional parameters.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAnAccountWithOptionalParameters"},
            description = "Eloqua {getAnAccount} integration test with optional parameters.")
    public void testGetAnAccountWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAnAccount");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAnAccountWithOptionalParameters" +
                        ".json");
        String accountId = connectorProperties.getProperty("accountId");
        String optionalParameters = getOptionalParameters("getAnAccountWithOptionalParameters.json");
        String apiEndPoint = apiRequestUrl + "data/account/" + accountId + "?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAnAccount method.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAnAccountWithOptionalParameters"},
            description = "Eloqua {getAnAccount} integration test with negative case.")
    public void testGetAnAccountWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAnAccount");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAnAccountForNegativeCase.json");
        String invalidId = connectorProperties.getProperty("invalidId");
        String optionalParameters = getOptionalParameters("getAnAccountForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "data/account/" + invalidId + "?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * RETURNS 204
     * Positive test case for getActivitiesOfAContact method with mandatory parameters.
     */
    @Test(enabled = false, priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAContactWithOptionalParameters" },
            description = "Eloqua {getActivitiesOfAContact} integration test with mandatory parameters.")
    public void testGetActivitiesOfAContactWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getActivitiesOfAContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getActivitiesOfAContactWithMandatoryParameters" +
                        ".json");
        String contactId = connectorProperties.getProperty("contactId");
        String startDate = connectorProperties.getProperty("startDate");
        String endDate = connectorProperties.getProperty("endDate");
        String type = connectorProperties.getProperty("type");
        String apiEndPoint = apiRequestUrl.replace("2.0","1.0") + "data/activities/contact/" + contactId + "?startDate=" +startDate+ "&endDate=" +endDate+ "&type=" +type;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * RETURNS 204
     * Positive test case for getActivitiesOfAContact method with optional parameters.
     */
    @Test(enabled = false, priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAContactWithOptionalParameters" },
            description = "Eloqua {getActivitiesOfAContact} integration test with optional parameters.")
    public void testGetActivitiesOfAContactWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getActivitiesOfAContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getActivitiesOfAContactWithOptionalParameters.json");
        String contactId = connectorProperties.getProperty("contactId");
        String optionalParameters= getOptionalParameters("getActivitiesOfAContactWithOptionalParameters.json");
        String parameters= getOptionalParameters("getActivitiesOfAContactForNegativeCase.json");
        String apiEndPoint = apiRequestUrl.replace("2.0","1.0") + "data/activities/contact/" + contactId +
            "?"+ parameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }


    /**
     * Negative test case for getActivitiesOfAContact method.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAContactWithOptionalParameters"},
            description = "Eloqua {getActivitiesOfAContact} integration test with negative case.")
    public void testGetActivitiesOfAContactWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getActivitiesOfAContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getActivitiesOfAContactForNegativeCase.json");
        String invalidId = connectorProperties.getProperty("invalidId");
        String parameters = getOptionalParameters("getActivitiesOfAContactForNegativeCase.json");
        String apiEndPoint = apiRequestUrl.replace("2.0", "1.0") + "data/activities/contact/" + invalidId +
                "?" + parameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getACampaign method with mandatory parameters.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateACampaignWithOptionalParameters"},
            description = "Eloqua {getACampaign} integration test with mandatory parameters.")
    public void testGetACampaignWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getACampaign");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getACampaignWithMandatoryParameters" +
                        ".json");
        String campaignId = connectorProperties.getProperty("campaignId");
        String apiEndPoint = apiRequestUrl + "assets/campaign/" + campaignId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getACampaign method with optional parameters.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateACampaignWithOptionalParameters"},
            description = "Eloqua {getACampaign} integration test with optional parameters.")
    public void testGetACampaignWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getACampaign");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getACampaignWithOptionalParameters.json");
        String campaignId = connectorProperties.getProperty("campaignId");
        String optionalParameters = getOptionalParameters("getACampaignWithOptionalParameters.json");
        String apiEndPoint = apiRequestUrl + "assets/campaign/" + campaignId + "?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getACampaign method.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateACampaignWithOptionalParameters"},
            description = "Eloqua {getACampaign} integration test with negative case.")
    public void testGetACampaignWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getACampaign");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getACampaignForNegativeCase.json");
        String invalidId = connectorProperties.getProperty("invalidId");
        String optionalParameters = getOptionalParameters("getACampaignForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "assets/campaign/" + invalidId + "?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAContactList method with mandatory parameters.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAContactListWithOptionalParameters"},
            description = "Eloqua {getAContactList} integration test with mandatory parameters.")
    public void testGetAContactListWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAContactList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAContactListWithMandatoryParameters" +
                        ".json");
        String contactListId = connectorProperties.getProperty("contactListId");
        String apiEndPoint = apiRequestUrl + "assets/contact/list/" + contactListId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAContactList method with optional parameters.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAContactListWithOptionalParameters"},
            description = "Eloqua {getAContactList} integration test with optional parameters.")
    public void testGetAContactListWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAContactList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAContactListWithOptionalParameters.json");
        String contactListId = connectorProperties.getProperty("contactListId");
        String optionalParameters = getOptionalParameters("getAContactListWithOptionalParameters.json");
        String apiEndPoint = apiRequestUrl + "assets/contact/list/" + contactListId + "?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAContactList method.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAContactListWithOptionalParameters"
    }, description = "Eloqua {getAContactList} integration test with negative case.")
    public void testGetAContactListWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAContactList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAContactListForNegativeCase.json");
        String invalidId = connectorProperties.getProperty("invalidId");
        String optionalParameters = getOptionalParameters("getAContactListForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "assets/contact/list/" + invalidId + "?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAContact method with mandatory parameters.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAContactWithOptionalParameters"},
            description = "Eloqua {getAContact} integration test with mandatory parameters.")
    public void testGetAContactWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAContactWithMandatoryParameters" +
                        ".json");
        String contactId = connectorProperties.getProperty("contactId");
        String apiEndPoint = apiRequestUrl + "data/contact/" + contactId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAContact method with optional parameters.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAContactWithOptionalParameters"},
            description = "Eloqua {getAContact} integration test with optional parameters.")
    public void testGetAContactWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAContactWithOptionalParameters.json");
        String contactId = connectorProperties.getProperty("contactId");
        String optionalParameters = getOptionalParameters("getAContactWithOptionalParameters.json");
        String apiEndPoint = apiRequestUrl + "data/contact/" + contactId + "?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAContact method.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAContactWithOptionalParameters"},
            description = "Eloqua {getAContact} integration test with negative case.")
    public void testGetAContactWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAContactForNegativeCase.json");
        String invalidId = connectorProperties.getProperty("invalidId");
        String optionalParameters = getOptionalParameters("getAContactForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "data/contact/" + invalidId + "?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAnEmailGroup method with mandatory parameters.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAnEmailGroupWithOptionalParameters"},
            description = "Eloqua {getAnEmailGroup} integration test with mandatory parameters.")
    public void testGetAnEmailGroupWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAnEmailGroup");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAnEmailGroupWithMandatoryParameters" +
                        ".json");
        String emailGroupId = connectorProperties.getProperty("emailGroupId");
        String apiEndPoint = apiRequestUrl + "assets/email/group/" + emailGroupId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAnEmailGroup method with optional parameters.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAnEmailGroupWithOptionalParameters"},
            description = "Eloqua {getAnEmailGroup} integration test with optional parameters.")
    public void testGetAnEmailGroupWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAnEmailGroup");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAnEmailGroupWithOptionalParameters.json");
        String emailGroupId = connectorProperties.getProperty("emailGroupId");
        String optionalParameters = getOptionalParameters("getAnEmailGroupWithOptionalParameters.json");
        String apiEndPoint = apiRequestUrl + "assets/email/group/" + emailGroupId + "?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAnEmailGroup method.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAnEmailGroupWithOptionalParameters"},
            description = "Eloqua {getAnEmailGroup} integration test with negative case.")
    public void testGetAnEmailGroupWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAnEmailGroup");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAnEmailGroupForNegativeCase.json");
        String invalidId = connectorProperties.getProperty("invalidId");
        String optionalParameters = getOptionalParameters("getAnEmailGroupForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "assets/email/group/" + invalidId + "?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAnExternalAsset method with mandatory parameters.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {
            "testCreateAnExternalAssetWithOptionalParameters"},
            description = "Eloqua {getAnExternalAsset} integration test with mandatory parameters.")
    public void testGetAnExternalAssetWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAnExternalAsset");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAnExternalAssetWithMandatoryParameters" +
                        ".json");
        String externalAssetId = connectorProperties.getProperty("externalAssetId");
        String apiEndPoint = apiRequestUrl + "assets/external/" + externalAssetId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAnExternalAsset method with optional parameters.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {
            "testCreateAnExternalAssetWithOptionalParameters"},
            description = "Eloqua {getAnExternalAsset} integration test with optional parameters.")
    public void testGetAnExternalAssetWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAnExternalAsset");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAnExternalAssetWithOptionalParameters.json");
        String externalAssetId = connectorProperties.getProperty("externalAssetId");
        String optionalParameters = getOptionalParameters("getAnExternalAssetWithOptionalParameters.json");
        String apiEndPoint = apiRequestUrl + "assets/external/" + externalAssetId + "?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAnExternalAsset method.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAnExternalAssetWithOptionalParameters"
    }, description = "Eloqua {getAnExternalAsset} integration test with negative case.")
    public void testGetAnExternalAssetWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAnExternalAsset");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAnExternalAssetForNegativeCase.json");
        String invalidId = connectorProperties.getProperty("invalidId");
        String optionalParameters = getOptionalParameters("getAnExternalAssetForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "assets/external/" + invalidId + "?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAUser method with mandatory parameters.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testGetCurrentUserWithOptionalParameters"},
            description = "Eloqua {getAUser} integration test with mandatory parameters.")
    public void testGetAUserWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAUser");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAUserWithMandatoryParameters" +
                        ".json");
        String userId = connectorProperties.getProperty("userId");
        String apiEndPoint = apiRequestUrl + "system/user/" + userId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAUser method with optional parameters.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testGetCurrentUserWithOptionalParameters"},
            description = "Eloqua {getAUser} integration test with optional parameters.")
    public void testGetAUserWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAUser");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAUserWithOptionalParameters.json");
        String userId = connectorProperties.getProperty("userId");
        String optionalParameters = getOptionalParameters("getAUserWithOptionalParameters.json");
        String apiEndPoint = apiRequestUrl + "system/user/" + userId + "?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAUser method.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {"testGetCurrentUserWithOptionalParameters"},
            description = "Eloqua {getAUser} integration test with negative case.")
    public void testGetAUserWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAUser");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAUserForNegativeCase.json");
        String invalidId = connectorProperties.getProperty("invalidId");
        String optionalParameters = getOptionalParameters("getAUserForNegativeCase.json");
        String userId = connectorProperties.getProperty("userId");
        String apiEndPoint = apiRequestUrl + "system/user/" + userId + "?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getCurrentUser method with mandatory parameters.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"},
            description = "Eloqua {getCurrentUser} integration test with mandatory parameters.")
    public void testGetCurrentUserWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getCurrentUser");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentUserWithMandatoryParameters" +
                        ".json");
        String apiEndPoint = apiRequestUrl + "system/user/current";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getCurrentUser method with optional parameters.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"},
            description = "Eloqua {getCurrentUser} integration test with optional parameters.")
    public void testGetCurrentUserWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getCurrentUser");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentUserWithOptionalParameters.json");
        String userId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("userId", userId);
        String optionalParameters = getOptionalParameters("getCurrentUserWithOptionalParameters.json");
        String apiEndPoint = apiRequestUrl + "system/user/current?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getCurrentUser method.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, description = "Eloqua {getCurrentUser} integration test with " +
            "negative case.")
    public void testGetCurrentUserWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getCurrentUser");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCurrentUserForNegativeCase.json");
        String optionalParameters = getOptionalParameters("getCurrentUserForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "system/user/current?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAVisitorNotification method with mandatory parameters.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"},
            dependsOnMethods = {"testCreateAVisitorNotificationWithOptionalParameters"}, description = "Eloqua {getAVisitorNotification} integration test with mandatory parameters.")
    public void testGetAVisitorNotificationWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAVisitorNotification");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAVisitorNotificationWithMandatoryParameters" +
                        ".json");
        String visitorId = connectorProperties.getProperty("visitorId");
        String apiEndPoint = apiRequestUrl + "settings/notification/visitor/" + visitorId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAVisitorNotification method with optional parameters.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {
            "testCreateAVisitorNotificationWithOptionalParameters"},
            description = "Eloqua {getAVisitorNotification} integration test with optional parameters.")
    public void testGetAVisitorNotificationWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAVisitorNotification");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAVisitorNotificationWithOptionalParameters.json");
        String visitorId = connectorProperties.getProperty("visitorId");
        String optionalParameters = getOptionalParameters("getAVisitorNotificationWithOptionalParameters.json");
        String apiEndPoint = apiRequestUrl + "settings/notification/visitor/" + visitorId + "?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAVisitorNotification method.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, dependsOnMethods = {
            "testCreateAVisitorNotificationWithOptionalParameters"}, description = "Eloqua {getAVisitorNotification} integration test with negative case.")
    public void testGetAVisitorNotificationWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAVisitorNotification");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAVisitorNotificationForNegativeCase.json");
        String invalidId = connectorProperties.getProperty("invalidId");
        String optionalParameters = getOptionalParameters("getAVisitorNotificationForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "settings/notification/visitor/" + invalidId + "?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for deleteACampaign method with mandatory parameters.
     */
    @Test(enabled = true, priority = 2, groups = {"wso2.esb"},
            dependsOnMethods = {"testGetACampaignWithOptionalParameters"},
            description = "Eloqua {deleteACampaign} integration test with mandatory parameters.")
    public void testDeleteACampaignWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "deleteACampaign");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "deleteACampaignWithMandatoryParameters.json");
        String campaignId = connectorProperties.getProperty("campaignId");
        String apiEndPoint = apiRequestUrl + "assets/campaign/" + campaignId + "?depth=complete";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Negative test case for deleteACampaign method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {deleteACampaign} integration test with negative case.")
    public void testDeleteACampaignWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "deleteACampaign");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "deleteACampaignForNegativeCase.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for deleteAContactList method with mandatory parameters.
     */
    @Test(enabled = true, priority = 2, groups = {"wso2.esb"},
            dependsOnMethods = {"testGetAContactListWithOptionalParameters"},
            description = "Eloqua {deleteAContactList} " +
                    "integration test with mandatory parameters.")
    public void testDeleteAContactListWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "deleteAContactList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "deleteAContactListWithMandatoryParameters.json");
        String contactListId = connectorProperties.getProperty("contactListId");
        String apiEndPoint = apiRequestUrl + "assets/contact/list" + contactListId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Negative test case for deleteAContactList method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {deleteAContactList} integration test" +
            " with negative case.")
    public void testDeleteAContactListWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "deleteAContactList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "deleteAContactListForNegativeCase.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for deleteAContact method with mandatory parameters.
     */
    @Test(enabled = true, priority = 2, groups = {"wso2.esb"},
            dependsOnMethods = {"testGetAContactWithOptionalParameters",
                    "testGetSubscribedEmailGroupsWithOptionalParameters",
                    "testGetSubscribedEmailGroupsWithMandatoryParameters",
                    "testGetSubscribedEmailGroupsWithNegativeCase"
            }, description = "Eloqua {deleteAContact} " +
            "integration test with mandatory parameters.")
    public void testDeleteAContactWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "deleteAContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "deleteAContactWithMandatoryParameters.json");
        String contactId = connectorProperties.getProperty("contactId");
        String apiEndPoint = apiRequestUrl + "data/contact" + contactId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Negative test case for deleteAContact method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {deleteAContact} integration test with negative case.")
    public void testDeleteAContactWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "deleteAContact");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "deleteAContactForNegativeCase.json");
        String contactId = connectorProperties.getProperty("contactId");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for deleteAnEmailGroup method with mandatory parameters.
     */
    @Test(enabled = true, priority = 2, groups = {"wso2.esb"},
            dependsOnMethods = {"testGetAnEmailGroupWithOptionalParameters"},
            description = "Eloqua {deleteAnEmailGroup} integration test with mandatory parameters.")
    public void testDeleteAnEmailGroupWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "deleteAnEmailGroup");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "deleteAnEmailGroupWithMandatoryParameters.json");
        String emailGroupId = connectorProperties.getProperty("emailGroupId");
        String apiEndPoint = apiRequestUrl + "assets/email/group" + emailGroupId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Negative test case for deleteAnEmailGroup method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {deleteAnEmailGroup} integration test " +
            "with negative case.")
    public void testDeleteAnEmailGroupWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "deleteAnEmailGroup");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "deleteAnEmailGroupForNegativeCase.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for deleteAnAccount method with mandatory parameters.
     */
    @Test(enabled = true, priority = 2, groups = {"wso2.esb"},
            dependsOnMethods = {"testGetAnAccountWithOptionalParameters"},
            description = "Eloqua {deleteAnAccount} integration test with mandatory parameters.")
    public void testDeleteAnAccountWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "deleteAnAccount");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "deleteAnAccountWithMandatoryParameters.json");
        String accountId = connectorProperties.getProperty("accountId");
        String apiEndPoint = apiRequestUrl + "data/account" + accountId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Negative test case for deleteAnAccount method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {deleteAnAccount} integration test with negative case.")
    public void testDeleteAnAccountWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "deleteAnAccount");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "deleteAnAccountForNegativeCase.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for deleteAnExternalAsset method with mandatory parameters.
     */
    @Test(enabled = true, priority = 2, groups = {"wso2.esb"},
            dependsOnMethods = {"testGetAnExternalAssetWithOptionalParameters"},
            description = "Eloqua {deleteAnExternalAsset} integration test with mandatory parameters.")
    public void testDeleteAnExternalAssetWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "deleteAnExternalAsset");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "deleteAnExternalAssetWithMandatoryParameters.json");
        String externalAssetId = connectorProperties.getProperty("externalAssetId");
        String apiEndPoint = apiRequestUrl + "assets/external" + externalAssetId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Negative test case for deleteAnExternalAsset method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {deleteAnExternalAsset} integration test with negative case.")
    public void testDeleteAnExternalAssetWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "deleteAnExternalAsset");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "deleteAnExternalAssetForNegativeCase.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for deleteAVisitorNotification method with mandatory parameters.
     */
    @Test(enabled = true, priority = 2, groups = {"wso2.esb"},
            dependsOnMethods = {"testGetAVisitorNotificationWithOptionalParameters"},
            description = "Eloqua {deleteAVisitorNotification} integration test with mandatory parameters.")
    public void testDeleteAVisitorNotificationWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "deleteAVisitorNotification");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "deleteAVisitorNotificationWithMandatoryParameters.json");
        String visitorId = connectorProperties.getProperty("visitorId");
        String apiEndPoint = apiRequestUrl + "settings/notification/visitor" + visitorId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Negative test case for deleteAVisitorNotification method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {deleteAVisitorNotification} integration test with negative case.")
    public void testDeleteAVisitorNotificationWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "deleteAVisitorNotification");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "deleteAVisitorNotificationForNegativeCase.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for getAllVisitors method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "Eloqua {getAllVisitors} integration test with mandatory parameters.")
    public void testGetAllVisitorsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllVisitors");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllVisitorsWithMandatoryParameters" +
                        ".json");
        String apiEndPoint = apiRequestUrl + "data/visitors";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllVisitors method with optional parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "Eloqua {getAllVisitors} integration test with optional parameters.")
    public void testGetAllVisitorsWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllVisitors");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllVisitorsWithOptionalParameters.json");
        String optionalParameters = getOptionalParameters("getAllVisitorsWithOptionalParameters.json");
        String apiEndPoint = apiRequestUrl + "data/visitors?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAllVisitors method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {getAllVisitors} integration test with " +
            "negative case.")
    public void testGetAllVisitorsWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllVisitors");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllVisitorsForNegativeCase.json");
        String optionalParameters = getOptionalParameters("getAllVisitorsForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "data/visitors?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllVisitorNotifications method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testGetCurrentUserWithOptionalParameters"},
            description = "Eloqua {getAllVisitorNotifications} integration test with mandatory parameters.")
    public void testGetAllVisitorNotificationsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllVisitorNotifications");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllVisitorNotificationsWithMandatoryParameters" +
                        ".json");
        String userId = connectorProperties.getProperty("userId");
        String apiEndPoint = apiRequestUrl + "settings/notifications/visitor?user=" + userId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllVisitorNotifications method with optional parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testGetCurrentUserWithOptionalParameters"},
            description = "Eloqua {getAllVisitorNotifications} integration test with optional parameters.")
    public void testGetAllVisitorNotificationsWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllVisitorNotifications");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllVisitorNotificationsWithOptionalParameters.json");
        String optionalParameters = getOptionalParameters("getAllVisitorNotificationsWithOptionalParameters.json");
        String userId = connectorProperties.getProperty("userId");
        String apiEndPoint = apiRequestUrl + "settings/notifications/visitor?user=" + userId + "&" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAllVisitorNotifications method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testGetCurrentUserWithOptionalParameters"},
            description = "Eloqua {getAllVisitorNotifications} integration test with negative case.")
    public void testGetAllVisitorNotificationsWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllVisitorNotifications");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllVisitorNotificationsForNegativeCase.json");
        String optionalParameters = getOptionalParameters("getAllVisitorNotificationsForNegativeCase.json");
        String userId = connectorProperties.getProperty("userId");
        String apiEndPoint = apiRequestUrl + "settings/notifications/visitor?user=" + userId + "&" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllUsers method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "Eloqua {getAllUsers} integration test with mandatory parameters.")
    public void testGetAllUsersWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllUsers");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllUsersWithMandatoryParameters" +
                        ".json");
        String apiEndPoint = apiRequestUrl + "system/users";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllUsers method with optional parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "Eloqua {getAllUsers} integration test with optional parameters.")
    public void testGetAllUsersWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllUsers");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllUsersWithOptionalParameters.json");
        String optionalParameters = getOptionalParameters("getAllUsersWithOptionalParameters.json");
        String apiEndPoint = apiRequestUrl + "system/users?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAllUsers method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {getAllUsers} integration test with negative " +
            "case.")
    public void testGetAllUsersWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllUsers");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllUsersForNegativeCase.json");
        String optionalParameters = getOptionalParameters("getAllUsersForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "system/users?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllExternalAssetTypes method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "Eloqua {getAllExternalAssetTypes} integration test with mandatory parameters.")
    public void testGetAllExternalAssetTypesWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllExternalAssetTypes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllExternalAssetTypesWithMandatoryParameters" +
                        ".json");
        String apiEndPoint = apiRequestUrl + "assets/external/types";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllExternalAssetTypes method with optional parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "Eloqua {getAllExternalAssetTypes} integration test with optional parameters.")
    public void testGetAllExternalAssetTypesWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllExternalAssetTypes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllExternalAssetTypesWithOptionalParameters.json");
        String optionalParameters = getOptionalParameters("getAllExternalAssetTypesWithOptionalParameters.json");
        String apiEndPoint = apiRequestUrl + "assets/external/types?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAllExternalAssetTypes method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {getAllExternalAssetTypes} integration test " +
            "with negative case.")
    public void testGetAllExternalAssetTypesWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllExternalAssetTypes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllExternalAssetTypesForNegativeCase.json");
        String optionalParameters = getOptionalParameters("getAllExternalAssetTypesForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "assets/external/types?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllExternalAssets method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "Eloqua {getAllExternalAssets} integration test with mandatory parameters.")
    public void testGetAllExternalAssetsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllExternalAssets");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllExternalAssetsWithMandatoryParameters" +
                        ".json");
        String apiEndPoint = apiRequestUrl + "assets/externals";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllExternalAssets method with optional parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "Eloqua {getAllExternalAssets} integration test with optional parameters.")
    public void testGetAllExternalAssetsWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllExternalAssets");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllExternalAssetsWithOptionalParameters.json");
        String optionalParameters = getOptionalParameters("getAllExternalAssetsWithOptionalParameters.json");
        String apiEndPoint = apiRequestUrl + "assets/externals?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAllExternalAssets method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {getAllExternalAssets} integration test with " +
            "negative case.")
    public void testGetAllExternalAssetsWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllExternalAssets");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllExternalAssetsForNegativeCase.json");
        String optionalParameters = getOptionalParameters("getAllExternalAssetsForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "assets/externals?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllEmailGroups method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "Eloqua {getAllEmailGroups} integration test with mandatory parameters.")
    public void testGetAllEmailGroupsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllEmailGroups");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllEmailGroupsWithMandatoryParameters" +
                        ".json");
        String apiEndPoint = apiRequestUrl + "assets/email/groups";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllEmailGroups method with optional parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "Eloqua {getAllEmailGroups} integration test with optional parameters.")
    public void testGetAllEmailGroupsWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllEmailGroups");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllEmailGroupsWithOptionalParameters.json");
        String optionalParameters = getOptionalParameters("getAllEmailGroupsWithOptionalParameters.json");
        String apiEndPoint = apiRequestUrl + "assets/email/groups?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAllEmailGroups method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {getAllEmailGroups} integration test with " +
            "negative case.")
    public void testGetAllEmailGroupsWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllEmailGroups");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllEmailGroupsForNegativeCase.json");
        String optionalParameters = getOptionalParameters("getAllEmailGroupsForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "assets/email/groups?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllContacts method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "Eloqua {getAllContacts} integration test with mandatory parameters.")
    public void testGetAllContactsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllContacts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllContactsWithMandatoryParameters" +
                        ".json");
        String apiEndPoint = apiRequestUrl + "data/contacts";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllContacts method with optional parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "Eloqua {getAllContacts} integration test with optional parameters.")
    public void testGetAllContactsWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllContacts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllContactsWithOptionalParameters.json");
        String newContactId = new JSONArray(esbRestResponse.getBody().getString("elements")).getJSONObject
                (0).getString("id");
        connectorProperties.setProperty("newContactId", newContactId);
        String optionalParameters = getOptionalParameters("getAllContactsWithOptionalParameters.json");
        String apiEndPoint = apiRequestUrl + "data/contacts?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAllContacts method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {getAllContacts} integration test with " +
            "negative case.")
    public void testGetAllContactsWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllContacts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllContactsForNegativeCase.json");
        String optionalParameters = getOptionalParameters("getAllContactsForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "data/contacts?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllContactLists method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "Eloqua {getAllContactLists} integration test with mandatory parameters.")
    public void testGetAllContactListsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllContactLists");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllContactListsWithMandatoryParameters" +
                        ".json");
        String apiEndPoint = apiRequestUrl + "assets/contact/lists";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllContactLists method with optional parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "Eloqua {getAllContactLists} integration test with optional parameters.")
    public void testGetAllContactListsWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllContactLists");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllContactListsWithOptionalParameters.json");
        String optionalParameters = getOptionalParameters("getAllContactListsWithOptionalParameters.json");
        String apiEndPoint = apiRequestUrl + "assets/contact/lists?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAllContactLists method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {getAllContactLists} integration test with " +
            "negative case.")
    public void testGetAllContactListsWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllContactLists");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllContactListsForNegativeCase.json");
        String optionalParameters = getOptionalParameters("getAllContactListsForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "assets/contact/lists?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllCampaigns method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "Eloqua {getAllCampaigns} integration test with mandatory parameters.")
    public void testGetAllCampaignsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllCampaigns");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllCampaignsWithMandatoryParameters" +
                        ".json");
        String apiEndPoint = apiRequestUrl + "assets/campaigns";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllCampaigns method with optional parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "Eloqua {getAllCampaigns} integration test with optional parameters.")
    public void testGetAllCampaignsWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllCampaigns");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllCampaignsWithOptionalParameters.json");
        String optionalParameters = getOptionalParameters("getAllCampaignsWithOptionalParameters.json");
        String apiEndPoint = apiRequestUrl + "assets/campaigns?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAllCampaigns method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {getAllCampaigns} integration test with " +
            "negative case.")
    public void testGetAllCampaignsWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAllCampaigns");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAllCampaignsForNegativeCase.json");
        String optionalParameters = getOptionalParameters("getAllCampaignsForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "assets/campaigns?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getRecentCampaigns method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "Eloqua {getRecentCampaigns} integration test with mandatory parameters.")
    public void testGetRecentCampaignsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getRecentCampaigns");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getRecentCampaignsWithMandatoryParameters" +
                        ".json");
        String apiEndPoint = apiRequestUrl + "assets/campaigns/recent";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getRecentCampaigns method with optional parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "Eloqua {getRecentCampaigns} integration test with optional parameters.")
    public void testGetRecentCampaignsWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getRecentCampaigns");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getRecentCampaignsWithOptionalParameters.json");
        String optionalParameters = getOptionalParameters("getRecentCampaignsWithOptionalParameters.json");
        String apiEndPoint = apiRequestUrl + "assets/campaigns/recent?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getRecentCampaigns method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {getRecentCampaigns} integration test with " +
            "negative case.")
    public void testGetRecentCampaignsWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getRecentCampaigns");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getRecentCampaignsForNegativeCase.json");
        String optionalParameters = getOptionalParameters("getRecentCampaignsForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "assets/campaigns/recent?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAListOfAccounts method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "Eloqua {getAListOfAccounts} integration test with mandatory parameters.")
    public void testGetAListOfAccountsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAListOfAccounts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAListOfAccountsWithMandatoryParameters" +
                        ".json");
        String apiEndPoint = apiRequestUrl + "data/accounts";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAListOfAccounts method with optional parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "Eloqua {getAListOfAccounts} integration test with optional parameters.")
    public void testGetAListOfAccountsWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAListOfAccounts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAListOfAccountsWithOptionalParameters.json");
        String optionalParameters = getOptionalParameters("getAListOfAccountsWithOptionalParameters.json");
        String apiEndPoint = apiRequestUrl + "data/accounts?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAListOfAccounts method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "Eloqua {getAListOfAccounts} integration test with " +
            "negative case.")
    public void testGetAListOfAccountsWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getAListOfAccounts");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAListOfAccountsForNegativeCase.json");
        String optionalParameters = getOptionalParameters("getAListOfAccountsForNegativeCase.json");
        String apiEndPoint = apiRequestUrl + "data/accounts?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getSubscribedEmailGroups method with mandatory parameters.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"},
            description = "Eloqua {getSubscribedEmailGroups} integration test with mandatory parameters.")
    public void testGetSubscribedEmailGroupsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getSubscribedEmailGroups");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getSubscribedEmailGroupsWithMandatoryParameters" +
                        ".json");
        String contactId = connectorProperties.getProperty("contactId");
        String apiEndPoint = apiRequestUrl + "data/contact/" + contactId + "/email/groups/subscription";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getSubscribedEmailGroups method with optional parameters.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"},
            description = "Eloqua {getSubscribedEmailGroups} integration test with optional parameters.")
    public void testGetSubscribedEmailGroupsWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getSubscribedEmailGroups");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getSubscribedEmailGroupsWithOptionalParameters.json");
        String optionalParameters = getOptionalParameters("getSubscribedEmailGroupsWithOptionalParameters.json");
        String contactId = connectorProperties.getProperty("contactId");
        String apiEndPoint = apiRequestUrl + "data/contact/" + contactId + "/email/groups/subscription?" + optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getSubscribedEmailGroups method.
     */
    @Test(enabled = true, priority = 1, groups = {"wso2.esb"}, description = "Eloqua {getSubscribedEmailGroups} " +
            "integration test with negative case.")
    public void testGetSubscribedEmailGroupsWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "getSubscribedEmailGroups");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getSubscribedEmailGroupsForNegativeCase.json");
        String optionalParameters = getOptionalParameters("getSubscribedEmailGroupsForNegativeCase.json");
        String contactId = connectorProperties.getProperty("contactId");
        String apiEndPoint = apiRequestUrl + "data/contact/" + contactId + "/email/groups/subscription?" +
                optionalParameters;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    public String getOptionalParameters(String fileName) throws JSONException, IOException {
        JSONObject params = new JSONObject(loadRequestFromFile(fileName, null));
        String optionalParameters = "";
        Iterator keys = params.keys();
        while (keys.hasNext()) {
            String currentDynamicKey = (String) keys.next();
            String currentDynamicValue = params.getString(currentDynamicKey);
            if (!currentDynamicValue.equals(null) && !currentDynamicValue.equals("")) {
                if (optionalParameters.equals("")) {
                    optionalParameters = currentDynamicKey + "=" + currentDynamicValue;
                } else {
                    optionalParameters = optionalParameters + "&" + currentDynamicKey + "=" + currentDynamicValue;
                }
            }
        }
        return optionalParameters;
    }

    /**
     * Method to read in contents of a file as String
     *
     * @param path
     * @return String contents of file
     * @throws IOException
     */
    private String getFileContent(String path) throws IOException {
        String fileContent = null;
        BufferedInputStream bfist = new BufferedInputStream(new FileInputStream(path));
        try {
            byte[] buf = new byte[bfist.available()];
            bfist.read(buf);
            fileContent = new String(buf);
        } catch (IOException ioe) {
            log.error("Error reading request from file.", ioe);
        } finally {
            if (bfist != null) {
                bfist.close();
            }
        }
        return fileContent;
    }

    /**
     * Load a request from a file, provided by a filename.
     *
     * @param requestFileName The name of the file to load the request from.
     * @param parametersMap   Map of parameters to replace within the parametrized values of the request.
     * @return String contents of the file.
     * @throws IOException Thrown on inability to read from the file.
     */
    private String loadRequestFromFile(String requestFileName, Map<String, String> parametersMap) throws IOException {
        String requestFilePath;
        String requestData;
        String pathToRequestsDirectory = repoLocation + connectorProperties.getProperty("requestDirectoryRelativePath");
        requestFilePath = pathToRequestsDirectory + requestFileName;
        requestData = getFileContent(requestFilePath);
        Properties prop = (Properties) connectorProperties.clone();
        if (parametersMap != null) {
            prop.putAll(parametersMap);
        }
        Matcher matcher = Pattern.compile("%s\\(([A-Za-z0-9]*)\\)", Pattern.DOTALL).matcher(requestData);
        while (matcher.find()) {
            String key = matcher.group(1);
            if (!key.equals("username") && !key.equals("password") && !key.equals("siteName")) {
                requestData =
                        requestData.replaceAll("%s\\(" + key + "\\)", Matcher.quoteReplacement(prop.getProperty(key)));
            }
            if (key.equals("username") || key.equals("password") || key.equals("siteName")) {
                String str = "\"" + key + "\":\"%s\\(" + key + "\\)\",\n";
                requestData = requestData.replaceAll(str, "");
            }
            if (key.endsWith("Id")) {
                String prefix = requestData.trim().substring(requestData.indexOf("\"") + 1, requestData.indexOf(":") - 1);
                String str = "\"" + prefix + "\":\"%s\\(" + key + "\\)\",\n";
                requestData = requestData.trim().replaceAll(str, "");
            }
        }
        return requestData;
    }
}
