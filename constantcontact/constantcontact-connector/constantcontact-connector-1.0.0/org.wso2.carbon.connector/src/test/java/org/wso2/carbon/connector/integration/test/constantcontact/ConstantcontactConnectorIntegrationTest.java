/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.connector.integration.test.constantcontact;

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

public class ConstantcontactConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    private String apiRequestUrl;

    private String apiKey;

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        init("constantcontact-connector-1.0.0");

        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");

        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("apiToken"));

        apiRequestUrl = connectorProperties.getProperty("apiUrl") + "/v2";
        apiKey = "?api_key=" + connectorProperties.getProperty("apiKey");
        connectorProperties.setProperty("campaignName", System.currentTimeMillis() + connectorProperties
                .getProperty("campaignName"));
        connectorProperties.setProperty("campaignNameOptional", System.currentTimeMillis() + connectorProperties
                .getProperty("campaignNameOptional"));
        connectorProperties.setProperty("contactEmailAddresses", System.currentTimeMillis() + connectorProperties
                .getProperty("contactEmailAddresses"));
        connectorProperties.setProperty("contactEmailAddressesOptional", System.currentTimeMillis() + connectorProperties
                .getProperty("contactEmailAddressesOptional"));
        connectorProperties.setProperty("contactEmailAddressesUpdate", System.currentTimeMillis() + connectorProperties
                .getProperty("contactEmailAddressesUpdate"));
    }

    /**
     * Positive test case for createCampaign method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "Constantcontact {createCampaign} integration test with mandatory parameters.")
    public void testCreateCampaignWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createCampaign");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCampaign_mandatory.json");
        String campaignId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("campaignId", campaignId);

        String apiEndPoint = apiRequestUrl + "/emailmarketing/campaigns/" + campaignId + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getBody().getString("subject"), connectorProperties.getProperty("subject"));
        Assert.assertEquals(apiRestResponse.getBody().getString("name"), connectorProperties.getProperty("campaignName"));
    }

    /**
     * Positive test case for createCampaign method with optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "Constantcontact {createCampaign} integration test with optional parameters.")
    public void testCreateCampaignWithOptionalParameters() throws IOException, JSONException, InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:createCampaign");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCampaign_optional.json");
        String campaignId2 = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("campaignId2", campaignId2);

        String apiEndPoint = apiRequestUrl + "/emailmarketing/campaigns/" + campaignId2 + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getBody().getString("subject"), connectorProperties.getProperty("subject"));
        Assert.assertEquals(apiRestResponse.getBody().getString("name"), connectorProperties.getProperty("campaignNameOptional"));
        Assert.assertEquals(apiRestResponse.getBody().getString("greeting_string"), connectorProperties.getProperty("subject"));
    }

    /**
     * Negative test case for createCampaign method.
     */
    @Test(groups = {"wso2.esb"}, description = "Constantcontact {createCampaign} integration test with negative case.")
    public void testCreateCampaignWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createCampaign");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCampaign_negative.json");

        String apiEndPoint = apiRequestUrl + "/emailmarketing/campaigns/" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createCampaign_negative.json");
        JSONArray esbArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiArray = new JSONArray(apiRestResponse.getBody().getString("output"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbArray.getJSONObject(0).getString("error_message"), apiArray.getJSONObject(0).getString("error_message"));
    }

    /**
     * Positive test case for createContactList method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "ConstantContact {createContactList} integration test with mandatory parameters.")
    public void testCreateContactListWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createContactList");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContactList_mandatory.json");
        String listId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("listId", listId);

        String apiEndPoint = apiRequestUrl + "/lists/" + listId + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);

        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));

    }

    /**
     * Negative test case for createContactList method.
     */
    @Test(groups = {"wso2.esb"}, description = "ConstantContact {createContactList} integration test with negative case.")
    public void testCreateContactListWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createContactList");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContactList_negative.json");
        String apiEndPoint = apiRequestUrl + "/lists" + apiKey;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createContactList_negative.json");

        JSONArray esbJsonArrayResponse = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiJsonArrayResponse = new JSONArray(apiRestResponse.getBody().getString("output"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);

        Assert.assertEquals(esbJsonArrayResponse.getJSONObject(0).getString("error_message"), apiJsonArrayResponse
                .getJSONObject(0).getString("error_message"));

    }

    /**
     * Positive test case for createContact method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testCreateContactListWithMandatoryParameters"}, description = "ConstantContact {createContact} integration test with mandatory parameters.")
    public void testCreateContactWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createContact");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_mandatory.json");
        String contactId = esbRestResponse.getBody().getString("id");

        String apiEndPoint = apiRequestUrl + "/contacts/" + contactId + apiKey;

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);

        Assert.assertEquals(esbRestResponse.getBody().getString("created_date"),
                apiRestResponse.getBody().getString("created_date"));
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));

    }

    /**
     * Positive test case for createContact method with optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "ConstantContact {createContact} integration test with Optional parameters.")
    public void testCreateContactWithOPtionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createContact");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_optional.json");
        String contactId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("contactId", contactId);
        String apiEndPoint = apiRequestUrl + "/contacts/" + contactId + apiKey;

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);

        Assert.assertEquals(esbRestResponse.getBody().getString("created_date"),
                apiRestResponse.getBody().getString("created_date"));
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));

    }

    /**
     * Negative test case for createContact method.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testCreateContactWithMandatoryParameters"}, description = "ConstantContact {createContact} integration test with negative case.")
    public void testCreateContactWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createContact");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createContact_negative.json");

        String apiEndPoint = apiRequestUrl + "/contacts" + apiKey;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createContact_negative.json");

        JSONArray esbJsonArrayResponse = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiJsonArrayResponse = new JSONArray(apiRestResponse.getBody().getString("output"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 409);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 409);

        Assert.assertEquals(esbJsonArrayResponse.getJSONObject(0).getString("error_message"), apiJsonArrayResponse
                .getJSONObject(0).getString("error_message"));
    }

    /**
     * Positive test case for getContactById method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testCreateContactWithOPtionalParameters"}, description = "ConstantContact {getContactById} integration test with mandatory parameters.")
    public void testGetContactByIdWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getContactById");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactById_mandatory.json");

        String contactId = connectorProperties.getProperty("contactId");

        String apiEndPoint = apiRequestUrl + "/contacts/" + contactId + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);

        Assert.assertEquals(esbRestResponse.getBody().getString("created_date"),
                apiRestResponse.getBody().getString("created_date"));
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));

    }

    /**
     * Negative test case for getContactById method.
     */
    @Test(groups = {"wso2.esb"}, description = "ConstantContact {getContactById} integration test with negative case.")
    public void testGetContactByIdWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getContactById");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactById_negative.json");

        String apiEndPoint = apiRequestUrl + "/contacts/1" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONArray esbJsonArrayResponse = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiJsonArrayResponse = new JSONArray(apiRestResponse.getBody().getString("output"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);

        Assert.assertEquals(esbJsonArrayResponse.getJSONObject(0).getString("error_message"), apiJsonArrayResponse
                .getJSONObject(0).getString("error_message"));

    }

    /**
     * Positive test case for getContactListById method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testCreateContactListWithMandatoryParameters"}, description = "ConstantContact {getContactById} integration test with mandatory parameters.")
    public void testGetContactListByIdWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getContactListById");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactListById_mandatory.json");

        String listId = connectorProperties.getProperty("listId");

        String apiEndPoint = apiRequestUrl + "/lists/" + listId + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);

        Assert.assertEquals(esbRestResponse.getBody().getString("created_date"),
                apiRestResponse.getBody().getString("created_date"));
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));

    }

    /**
     * Negative test case for getContactListById method.
     */
    @Test(groups = {"wso2.esb"}, description = "ConstantContact {getContactListById} integration test with negative case.")
    public void testGetContactListByIdWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getContactListById");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getContactListById_negative.json");

        String apiEndPoint = apiRequestUrl + "/lists/1" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONArray esbJsonArrayResponse = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiJsonArrayResponse = new JSONArray(apiRestResponse.getBody().getString("output"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);

        Assert.assertEquals(esbJsonArrayResponse.getJSONObject(0).getString("error_message"), apiJsonArrayResponse
                .getJSONObject(0).getString("error_message"));

    }

    /**
     * Positive test case for listContactLists method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testCreateContactListWithMandatoryParameters"}, description = "ConstantContact {listContactLists} integration test with mandatory parameters.")
    public void testListContactListsWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listContactLists");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContactLists_mandatory.json");

        String apiEndPoint = apiRequestUrl + "/lists" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONArray esbJsonArrayResponse = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiJsonArrayResponse = new JSONArray(apiRestResponse.getBody().getString("output"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);

        Assert.assertEquals(esbJsonArrayResponse.getJSONObject(0).getString("name"), apiJsonArrayResponse
                .getJSONObject(0).getString("name"));
        Assert.assertEquals(esbJsonArrayResponse.getJSONObject(0).getString("created_date"), apiJsonArrayResponse
                .getJSONObject(0).getString("created_date"));

    }

    /**
     * Positive test case for listContactLists method with optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "ConstantContact {listContactLists} integration test with optional parameters.")
    public void testListContactListsWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listContactLists");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContactLists_optional.json");

        String modifiedSince = connectorProperties.getProperty("modifiedSince");

        String apiEndPoint = apiRequestUrl + "/lists" + apiKey + "&modified_since=" + modifiedSince;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONArray esbJsonArrayResponse = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiJsonArrayResponse = new JSONArray(apiRestResponse.getBody().getString("output"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);

        Assert.assertEquals(esbJsonArrayResponse.getJSONObject(0).getString("name"), apiJsonArrayResponse
                .getJSONObject(0).getString("name"));
        Assert.assertEquals(esbJsonArrayResponse.getJSONObject(0).getString("created_date"), apiJsonArrayResponse
                .getJSONObject(0).getString("created_date"));

    }

    /**
     * Negative test case for listContactLists method.
     */
    @Test(groups = {"wso2.esb"}, description = "ConstantContact {listContactLists} integration test with negative case.")
    public void testListContactListsWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listContactLists");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContactLists_negative.json");

        String apiEndPoint = apiRequestUrl + "/lists" + apiKey + "&modified_since=2014-12-03T07:17:40.000-5:00";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONArray esbJsonArrayResponse = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiJsonArrayResponse = new JSONArray(apiRestResponse.getBody().getString("output"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);

        Assert.assertEquals(esbJsonArrayResponse.getJSONObject(0).getString("error_message"), apiJsonArrayResponse
                .getJSONObject(0).getString("error_message"));

    }

    /**
     * Positive test case for listContactsInaList method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testCreateContactWithMandatoryParameters"}, description = "ConstantContact {listContactsInaList} integration test with mandatory parameters.")
    public void testListContactsInaListsWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listContactsInaList");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContactsInaList_mandatory.json");

        String listId = connectorProperties.getProperty("listId");

        String apiEndPoint = apiRequestUrl + "/lists/" + listId + "/contacts" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("first_name"),
                apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("first_name"));

    }

    /**
     * Positive test case for listContactsInaList method with optional parameters.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListContactsInaListsWithMandatoryParameters"}, description = "ConstantContact {listContactsInaList} integration test with mandatory parameters.")
    public void testListContactsInaListsWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listContactsInaList");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContactsInaList_optional.json");

        String listId = connectorProperties.getProperty("listId");
        String limit = connectorProperties.getProperty("limit");
        String modifiedSince = connectorProperties.getProperty("modifiedSince");

        String apiEndPoint =
                apiRequestUrl + "/lists/" + listId + "/contacts" + apiKey + "&modified_since=" + modifiedSince
                        + "&limit=" + limit;

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("first_name"),
                apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("first_name"));

    }

    /**
     * Negative test case for listContactsInaList method.
     */
    @Test(groups = {"wso2.esb"}, description = "ConstantContact {listContactsInaList} integration test with negative case.")
    public void testListContactsInaListWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listContactsInaList");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listContactsInaList_negative.json");

        String apiEndPoint = apiRequestUrl + "/lists/1/contacts" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONArray esbJsonArrayResponse = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiJsonArrayResponse = new JSONArray(apiRestResponse.getBody().getString("output"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);

        Assert.assertEquals(esbJsonArrayResponse.getJSONObject(0).getString("error_message"), apiJsonArrayResponse
                .getJSONObject(0).getString("error_message"));

    }

    /**
     * Positive test case for createSchedule method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testCreateCampaignWithOptionalParameters"}, description = "ConstantContact {createSchedule} integration test with mandatory parameters.")
    public void testCreateScheduleWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createSchedule");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createSchedule_mandatory.json");

        String campaignId = connectorProperties.getProperty("campaignId2");

        String apiEndPoint = apiRequestUrl + "/emailmarketing/campaigns/" + campaignId + "/schedules" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONArray apiJsonArrayResponse = new JSONArray(apiRestResponse.getBody().getString("output"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);

        Assert.assertEquals(esbRestResponse.getBody().getString("id"),
                apiJsonArrayResponse.getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("scheduled_date"), apiJsonArrayResponse
                .getJSONObject(0).getString("scheduled_date"));

    }

    /**
     * Negative test case for createSchedule method.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testCreateScheduleWithMandatoryParameters"}, description = "ConstantContact {createSchedule} integration test with mandatory parameters.")
    public void testCreateScheduleWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createSchedule");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createSchedule_mandatory.json");

        String campaignId = connectorProperties.getProperty("campaignId2");

        String apiEndPoint = apiRequestUrl + "/emailmarketing/campaigns/" + campaignId + "/schedules" + apiKey;
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createSchedule_negative.json");

        JSONArray esbJsonArrayResponse = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiJsonArrayResponse = new JSONArray(apiRestResponse.getBody().getString("output"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);

        Assert.assertEquals(esbJsonArrayResponse.getJSONObject(0).getString("error_message"), apiJsonArrayResponse
                .getJSONObject(0).getString("error_message"));

    }

    /**
     * Positive test case for getSchedule method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testCreateScheduleWithNegativeCase"}, description = "ConstantContact {getSchedule} integration test with mandatory parameters.")
    public void testGetScheduleWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getSchedule");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSchedule_mandatory.json");

        String campaignId = connectorProperties.getProperty("campaignId2");

        String apiEndPoint = apiRequestUrl + "/emailmarketing/campaigns/" + campaignId + "/schedules" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONArray esbJsonArrayResponse = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiJsonArrayResponse = new JSONArray(apiRestResponse.getBody().getString("output"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);

        Assert.assertEquals(esbJsonArrayResponse.getJSONObject(0).getString("id"), apiJsonArrayResponse
                .getJSONObject(0).getString("id"));
        Assert.assertEquals(esbJsonArrayResponse.getJSONObject(0).getString("scheduled_date"), apiJsonArrayResponse
                .getJSONObject(0).getString("scheduled_date"));

    }

    /**
     * Negative test case for getSchedule method.
     */
    @Test(groups = {"wso2.esb"}, description = "ConstantContact {getSchedule} integration test with negative case.")
    public void testGetScheduleWithNegative() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getSchedule");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSchedule_negative.json");

        String apiEndPoint = apiRequestUrl + "/emailmarketing/campaigns/1/schedules" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONArray esbJsonArrayResponse = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiJsonArrayResponse = new JSONArray(apiRestResponse.getBody().getString("output"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);

        Assert.assertEquals(esbJsonArrayResponse.getJSONObject(0).getString("error_message"), apiJsonArrayResponse
                .getJSONObject(0).getString("error_message"));

    }

    /**
     * Positive test case for listCampaignClicks method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "ConstantContact {listCampaignClicks} integration test with mandatory parameters.")
    public void testListCampaignClicksWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listCampaignClicks");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCampaignClicks_mandatory.json");

        String campaignId = connectorProperties.getProperty("trackCampaignId");

        String apiEndPoint = apiRequestUrl + "/emailmarketing/campaigns/" + campaignId + "/tracking/clicks" + apiKey;

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("link_id"),
                apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("link_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("click_date"),
                apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("click_date"));
    }

    /**
     * Positive test case for listCampaignClicks method with optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "ConstantContact {listCampaignClicks} integration test with optional parameters.")
    public void testListCampaignClicksWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listCampaignClicks");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCampaignClicks_optional.json");

        String campaignId = connectorProperties.getProperty("trackCampaignId");
        String limit = connectorProperties.getProperty("limit");
        String createdSince = connectorProperties.getProperty("createdSince");

        String apiEndPoint =
                apiRequestUrl + "/emailmarketing/campaigns/" + campaignId + "/tracking/clicks" + apiKey + "&limit="
                        + limit + "&created_since=" + createdSince;

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("link_id"),
                apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("link_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("click_date"),
                apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("click_date"));
    }

    /**
     * Negative test case for listCampaignClicks method.
     */
    @Test(groups = {"wso2.esb"}, description = "ConstantContact {listCampaignClicks} integration test with negative case.")
    public void testListCampaignClicksWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listCampaignClicks");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCampaignClicks_negative.json");

        String campaignId = connectorProperties.getProperty("trackCampaignId");
        String createdSince = connectorProperties.getProperty("createdSince");

        String apiEndPoint =
                apiRequestUrl + "/emailmarketing/campaigns/" + campaignId + "/tracking/clicks" + apiKey
                        + "&limit=0&created_since=" + createdSince;

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONArray esbJsonArrayResponse = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiJsonArrayResponse = new JSONArray(apiRestResponse.getBody().getString("output"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);

        Assert.assertEquals(esbJsonArrayResponse.getJSONObject(0).getString("error_message"), apiJsonArrayResponse
                .getJSONObject(0).getString("error_message"));
    }

    /**
     * Positive test case for listCampaignOpeners method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "ConstantContact {listCampaignOpeners} integration test with mandatory parameters.")
    public void testListCampaignOpenersWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listCampaignOpeners");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCampaignOpeners_mandatory.json");

        String campaignId = connectorProperties.getProperty("trackCampaignId");

        String apiEndPoint = apiRequestUrl + "/emailmarketing/campaigns/" + campaignId + "/tracking/opens" + apiKey;

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("contact_id"),
                apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("contact_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("open_date"),
                apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("open_date"));
    }

    /**
     * Positive test case for listCampaignOpeners method with optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "ConstantContact {listCampaignOpeners} integration test with optional parameters.")
    public void testListCampaignOpenersWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listCampaignOpeners");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCampaignOpeners_optional.json");

        String campaignId = connectorProperties.getProperty("trackCampaignId");
        String limit = connectorProperties.getProperty("limit");
        String createdSince = connectorProperties.getProperty("createdSince");

        String apiEndPoint =
                apiRequestUrl + "/emailmarketing/campaigns/" + campaignId + "/tracking/opens" + apiKey + "&limit="
                        + limit + "&created_since=" + createdSince;

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("contact_id"),
                apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("contact_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("open_date"),
                apiRestResponse.getBody().getJSONArray("results").getJSONObject(0).getString("open_date"));
    }

    /**
     * Negative test case for listCampaignOpeners method.
     */
    @Test(groups = {"wso2.esb"}, description = "ConstantContact {listCampaignOpeners} integration test with negative case.")
    public void testListCampaignOpenersWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listCampaignOpeners");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCampaignOpeners_negative.json");

        String campaignId = connectorProperties.getProperty("trackCampaignId");
        String createdSince = connectorProperties.getProperty("createdSince");

        String apiEndPoint =
                apiRequestUrl + "/emailmarketing/campaigns/" + campaignId + "/tracking/opens" + apiKey
                        + "&limit=0&created_since=" + createdSince;

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONArray esbJsonArrayResponse = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiJsonArrayResponse = new JSONArray(apiRestResponse.getBody().getString("output"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);

        Assert.assertEquals(esbJsonArrayResponse.getJSONObject(0).getString("error_message"), apiJsonArrayResponse
                .getJSONObject(0).getString("error_message"));
    }

    /**
     * Positive test case for updateContact method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListContactsInaListsWithOptionalParameters"}, description = "ConstantContact {updateContact} integration test with mandatory parameters.")
    public void testUpdateContactWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updateContact");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateContact_mandatory.json");

        String contactId = connectorProperties.getProperty("contactId");

        String apiEndPoint = apiRequestUrl + "/contacts/" + contactId + apiKey;

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONArray esbJsonArrayResponse = new JSONArray(esbRestResponse.getBody().getString("email_addresses"));
        JSONArray apiJsonArrayResponse = new JSONArray(apiRestResponse.getBody().getString("email_addresses"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);

        Assert.assertEquals(esbJsonArrayResponse.getJSONObject(0).getString("email_address"), apiJsonArrayResponse
                .getJSONObject(0).getString("email_address"));

    }

    /**
     * Positive test case for updateContact method with optional parameters.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testUpdateContactWithMandatoryParameters"}, description = "ConstantContact {updateContact} integration test with Optional parameters.")
    public void testUpdateContactWithOPtionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updateContact");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateContact_optional.json");

        String contactId = connectorProperties.getProperty("contactId");

        String apiEndPoint = apiRequestUrl + "/contacts/" + contactId + apiKey;

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);

        Assert.assertEquals(esbRestResponse.getBody().getString("first_name"),
                apiRestResponse.getBody().getString("first_name"));

    }

    /**
     * Negative test case for updateContact method.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testUpdateContactWithMandatoryParameters"}, description = "ConstantContact {updateContact} integration test with negative case.")
    public void testUpdateContactWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updateContact");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateContact_negative.json");

        String contactId = connectorProperties.getProperty("contactId");

        String apiEndPoint = apiRequestUrl + "/contacts/" + contactId + apiKey;

        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateContact_negative.json");

        JSONArray esbJsonArrayResponse = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiJsonArrayResponse = new JSONArray(apiRestResponse.getBody().getString("output"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);

        Assert.assertEquals(esbJsonArrayResponse.getJSONObject(0).getString("error_message"), apiJsonArrayResponse
                .getJSONObject(0).getString("error_message"));
    }

    /**
     * Positive test case for getCampaignById method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testCreateCampaignWithMandatoryParameters"}, description = "Constantcontact {getCampaignById} integration test with mandatory parameters.")
    public void testGetCampaignByIdWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getCampaignById");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCampaignById_mandatory.json");

        String apiEndPoint = apiRequestUrl + "/emailmarketing/campaigns/" + connectorProperties.getProperty("campaignId") + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getString("subject"), apiRestResponse.getBody().getString("subject"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("text_content"), apiRestResponse.getBody().getString("text_content"));
    }

    /**
     * Negative test case for getCampaignById method.
     */
    @Test(groups = {"wso2.esb"}, description = "Constantcontact {getCampaignById} integration test with negative case.")
    public void testGetCampaignByIdWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getCampaignById");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCampaignById_negative.json");

        String apiEndPoint = apiRequestUrl + "/emailmarketing/campaigns/invalid" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray esbArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiArray = new JSONArray(apiRestResponse.getBody().getString("output"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbArray.getJSONObject(0).getString("error_message"), apiArray.getJSONObject(0).getString("error_message"));
    }

    /**
     * Positive test case for listCampaigns method with mandatory parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "Constantcontact {listCampaigns} integration test with mandatory parameters.")
    public void testListCampaignsWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listCampaigns");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCampaigns_mandatory.json");

        String apiEndPoint = apiRequestUrl + "/emailmarketing/campaigns" + apiKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray esbResultArray = new JSONArray(esbRestResponse.getBody().getString("results"));
        JSONArray apiResultArray = new JSONArray(apiRestResponse.getBody().getString("results"));

        Assert.assertEquals(esbResultArray.getJSONObject(0).getString("id"), apiResultArray.getJSONObject(0).getString("id"));
        Assert.assertEquals(esbResultArray.getJSONObject(0).getString("modified_date"), apiResultArray.getJSONObject(0).getString("modified_date"));
        Assert.assertEquals(esbResultArray.getJSONObject(1).getString("id"), apiResultArray.getJSONObject(1).getString("id"));
        Assert.assertEquals(esbResultArray.getJSONObject(1).getString("modified_date"), apiResultArray.getJSONObject(1).getString("modified_date"));
    }

    /**
     * Positive test case for listCampaigns method with optional parameters.
     */
    @Test(groups = {"wso2.esb"}, description = "Constantcontact {listCampaigns} integration test with optional parameters.")
    public void testListCampaignsWithOptionalParameters() throws IOException, JSONException, InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:listCampaigns");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCampaigns_optional.json");

        String apiEndPoint = apiRequestUrl + "/emailmarketing/campaigns" + apiKey + "&limit=" + connectorProperties.getProperty("limit");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray esbResultArray = new JSONArray(esbRestResponse.getBody().getString("results"));
        JSONArray apiResultArray = new JSONArray(apiRestResponse.getBody().getString("results"));
        int limit = Integer.parseInt(connectorProperties.getProperty("limit"));

        Assert.assertEquals(limit, esbResultArray.length());
        Assert.assertEquals(esbResultArray.getJSONObject(0).getString("id"), apiResultArray.getJSONObject(0).getString("id"));
        Assert.assertEquals(esbResultArray.getJSONObject(0).getString("modified_date"), apiResultArray.getJSONObject(0).getString("modified_date"));
        Assert.assertEquals(esbResultArray.getJSONObject(1).getString("id"), apiResultArray.getJSONObject(1).getString("id"));
        Assert.assertEquals(esbResultArray.getJSONObject(1).getString("modified_date"), apiResultArray.getJSONObject(1).getString("modified_date"));
    }

}