/**
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.integration.test.teamwork;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;
import org.wso2.carbon.connector.integration.test.common.Base64Coder;


public class TeamworkConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> headersMap = new HashMap<String, String>();

    private String multipartProxyUrl;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        init("teamwork-connector-1.0.0");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");

        apiRequestHeadersMap.put("Accept-Charset", "UTF-8");
        apiRequestHeadersMap.put("Content-Type", "application/json");
        String userpassword = connectorProperties.getProperty("apiKey") + ":" + "";
        String encodedAuthorization = Base64Coder.encodeString( userpassword );
        apiRequestHeadersMap.put("Authorization", "Basic "+ encodedAuthorization);

        String multipartPoxyName = connectorProperties.getProperty("multipartProxyName");
        multipartProxyUrl = getProxyServiceURL(multipartPoxyName);
    }

    /**
     * Positive test case for getAccountDetails method with mandatory parameters.
     */
    @Test(enabled = true, description = "teamwork {getAccountDetails} integration test with mandatory parameter.")
    public void testGetAccountDetailsWithMandatoryParameters() throws IOException, JSONException {
        String methodName = "tw_getAccountDetails";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/account.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAccountDetailsMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("account").get("id"), apiRestResponse.getBody().getJSONObject("account").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("account").get("name"), apiRestResponse.getBody().getJSONObject("account").get("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("account").get("code"), apiRestResponse.getBody().getJSONObject("account").get("code"));
    }

    /**
     * Positive test case for getAuthenticateDetails method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getAuthenticateDetails} integration test with mandatory parameter.")
    public void testGetAuthenticateDetailsWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getAuthenticateDetails";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/authenticate.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAuthenticateDetailsMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("account").get("id"), apiRestResponse.getBody().getJSONObject("account").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("account").get("name"), apiRestResponse.getBody().getJSONObject("account").get("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("account").get("userId"), apiRestResponse.getBody().getJSONObject("account").get("userId"));
    }

    /**
     * Positive test case for getLatestActivity method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getLatestActivity} integration test with mandatory parameter.")
    public void testGetLatestActivityWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getLatestActivity";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/latestActivity.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getLatestActivityMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("activity").toString(), apiRestResponse.getBody().get("activity").toString());
    }

    /**
     * Positive test case for getLatestActivity method with optional parameters.
     */
    @Test(enabled = false, description = "teamwork {getLatestActivity} integration test with optional parameter.")
    public void testGetLatestActivityWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_getLatestActivity";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/latestActivity.json?maxItems=" + connectorProperties.getProperty("maxItems") + "&onlyStarred=" + connectorProperties.getProperty("onlyStarred");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getLatestActivityOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("activity").toString(), apiRestResponse.getBody().get("activity").toString());
    }

    /**
     * Positive test case for getLatestActivityForAProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getLatestActivityForAProject} integration test with mandatory parameter.")
    public void testGetLatestActivityForAProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getLatestActivityForAProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/latestActivity.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getLatestActivityForAProjectMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("activity").toString(), apiRestResponse.getBody().get("activity").toString());
    }

    /**
     * Positive test case for getLatestActivityForAProject method with optional parameters.
     */
    @Test(enabled = false, description = "teamwork {getLatestActivityForAProject} integration test with optional parameter.")
    public void testGetLatestActivityForAProjectWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_getLatestActivityForAProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/latestActivity.json?maxItems=" + connectorProperties.getProperty("maxItems");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getLatestActivityForAProjectOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("activity").toString(), apiRestResponse.getBody().get("activity").toString());
    }

    /**
     * Negative test case for getLatestActivityForAProject method with optional parameters.
     */
    @Test(enabled = false, description = "teamwork {getLatestActivityForAProject} integration test with optional parameter.")
    public void testGetLatestActivityForAProjectNegativeCase() throws IOException, JSONException {

        String methodName = "tw_getLatestActivityForAProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("invalidProjectId") + "/latestActivity.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getLatestActivityForAProjectNegative.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("MESSAGE"), apiRestResponse.getBody().get("MESSAGE"));
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), apiRestResponse.getBody().get("STATUS"));
    }

    /**
     * Positive test case for deleteActivity method with optional parameters.
     */
    @Test(enabled = false, description = "teamwork {deleteActivity} integration test with optional parameter.")
    public void testDeleteActivitytWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deleteActivity";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteActivityMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for createEvent method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {createEvent} integration test with mandatory parameter.")
    public void testCreateEventWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_createEvent";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_createEventMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);

        String eventId = esbRestResponse.getBody().get("id").toString();
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/calendarevents/" + eventId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("event").get("title"), connectorProperties.getProperty("eventTitle"));
    }

    /**
     * Positive test case for updateEvent method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {updateEvent} integration test with mandatory parameter.")
    public void testUpdateEventWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_updateEvent";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_updateEventMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

        String eventId = connectorProperties.getProperty("updateEventId");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/calendarevents/" + eventId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("event").get("title"), connectorProperties.getProperty("UpdateEventTitle"));
    }

    /**
     * Positive test case for getAllEvents method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getAllEvents} integration test with mandatory parameter.")
    public void testGetAllEventsWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getAllEvents";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAllEventsMandatory.json");
        JSONArray esbEventsArray = esbRestResponse.getBody().getJSONArray("events");

        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/calendarevents.json?startdate=" + connectorProperties.getProperty("startDate") + "&endDate=" + connectorProperties.getProperty("endDate");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiEventsArray = apiRestResponse.getBody().getJSONArray("events");

        Assert.assertEquals(esbEventsArray.length(), apiEventsArray.length());
        if (esbEventsArray.length() > 0 && apiEventsArray.length() > 0) {

            Assert.assertEquals(esbEventsArray.getJSONObject(0).getString("id"), apiEventsArray.getJSONObject(0)
                    .getString("id"));
            Assert.assertEquals(esbEventsArray.getJSONObject(0).getString("title"), apiEventsArray.getJSONObject(0)
                    .getString("title"));
        } else {
            Assert.assertTrue(false);
        }
    }

    /**
     * Positive test case for getAllEvents method with optional parameters.
     */
    @Test(enabled = false, description = "teamwork {getAllEvents} integration test with optional parameter.")
    public void testGetAllEventsWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_getAllEvents";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAllEventsOptional.json");
        JSONArray esbEventsArray = esbRestResponse.getBody().getJSONArray("events");

        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/calendarevents.json?startdate=" + connectorProperties.getProperty("startDate") + "&endDate=" + connectorProperties.getProperty("endDate")
                        + "&showDeleted=" + connectorProperties.getProperty("showDeleted") + "&updatedAfterDate=" + connectorProperties.getProperty("updatedAfterDate") + "&page=" + connectorProperties.getProperty("page");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiEventsArray = apiRestResponse.getBody().getJSONArray("events");

        Assert.assertEquals(esbEventsArray.length(), apiEventsArray.length());
        if (esbEventsArray.length() > 0 && apiEventsArray.length() > 0) {

            Assert.assertEquals(esbEventsArray.getJSONObject(0).getString("id"), apiEventsArray.getJSONObject(0)
                    .getString("id"));
            Assert.assertEquals(esbEventsArray.getJSONObject(0).getString("title"), apiEventsArray.getJSONObject(0)
                    .getString("title"));
        } else {
            Assert.assertTrue(false);
        }
    }

    /**
     * Positive test case for getAllEventTypes method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getAllEventTypes} integration test with mandatory parameter.")
    public void testGetAllEventTypesWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getAllEventTypes";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAllEventTypesMandatory.json");
        JSONArray esbEventTypesArray = esbRestResponse.getBody().getJSONArray("eventtypes");

        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/calendareventtypes.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiEventTypesArray = apiRestResponse.getBody().getJSONArray("eventtypes");

        Assert.assertEquals(esbEventTypesArray.length(), apiEventTypesArray.length());
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), apiRestResponse.getBody().get("STATUS"));
        if (esbEventTypesArray.length() > 0 && apiEventTypesArray.length() > 0) {

            Assert.assertEquals(esbEventTypesArray.getJSONObject(0).getString("id"), apiEventTypesArray.getJSONObject(0)
                    .getString("id"));
            Assert.assertEquals(esbEventTypesArray.getJSONObject(0).getString("name"), apiEventTypesArray.getJSONObject(0)
                    .getString("name"));
        }
    }

    /**
     * Positive test case for getEvent method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getEvent} integration test with mandatory parameter.")
    public void testGetEventWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getEvent";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getEventMandatory.json");

        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/calendarevents/" + connectorProperties.getProperty("eventId") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("event").get("id"), apiRestResponse.getBody().getJSONObject("event").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("event").get("title"), apiRestResponse.getBody().getJSONObject("event").get("title"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("event").get("where"), apiRestResponse.getBody().getJSONObject("event").get("where"));
    }

    /**
     * Positive test case for deleteEvent method with optional parameters.
     */
    @Test(enabled = false, description = "teamwork {deleteEvent} integration test with optional parameter.")
    public void testDeleteEventWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deleteEvent";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteEventMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for createFileCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {createFileCategory} integration test with mandatory parameter.")
    public void testCreateFileCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_createFileCategory";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_createFileCategoryMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);

        String fileCategoryId = esbRestResponse.getBody().get("categoryId").toString();
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/fileCategories/" + fileCategoryId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("category").get("name"), connectorProperties.getProperty("categoryName"));
    }

    /**
     * Positive test case for createLinkCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {createLinkCategory} integration test with mandatory parameter.")
    public void testCreateLinkCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_createLinkCategory";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_createLinkCategoryMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);

        String linkCategoryId = esbRestResponse.getBody().get("categoryId").toString();
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/linkCategories/" + linkCategoryId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("category").get("name"), connectorProperties.getProperty("categoryName"));
    }

    /**
     * Positive test case for createMessageCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {createMessageCategory} integration test with mandatory parameter.")
    public void testCreateMessageCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_createMessageCategory";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_createMessageCategoryMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);

        String messageCategoryId = esbRestResponse.getBody().get("categoryId").toString();
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/messageCategories/" + messageCategoryId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("category").get("name"), connectorProperties.getProperty("categoryName"));
    }

    /**
     * Positive test case for createNotebookCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {createNotebookCategory} integration test with mandatory parameter.")
    public void testCreateNotebookCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_createNotebookCategory";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_createNotebookCategoryMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);

        String notebookCategoryId = esbRestResponse.getBody().get("categoryId").toString();
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/notebookCategories/" + notebookCategoryId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("category").get("name"), connectorProperties.getProperty("categoryName"));
    }

    /**
     * Positive test case for createProjectCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {createProjectCategory} integration test with mandatory parameter.")
    public void testCreateProjectCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_createProjectCategory";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_createProjectCategoryMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);

        String projectCategoryId = esbRestResponse.getBody().get("categoryId").toString();
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projectCategories/" + projectCategoryId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("category").get("name"), connectorProperties.getProperty("categoryName"));
    }

    /**
     * Positive test case for updateFileCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {updateFileCategory} integration test with mandatory parameter.")
    public void testUpdateFileCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_updateFileCategory";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_updateFileCategoryMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

        String fileCategoryId = connectorProperties.getProperty("updateFileCategoryId");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/fileCategories/" + fileCategoryId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("category").get("name"), connectorProperties.getProperty("updateCategoryName"));
    }

    /**
     * Positive test case for updateLinkCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {updateLinkCategory} integration test with mandatory parameter.")
    public void testUpdateLinkCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_updateLinkCategory";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_updateLinkCategoryMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

        String linkCategoryId = connectorProperties.getProperty("updateLinkCategoryId");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/linkCategories/" + linkCategoryId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("category").get("name"), connectorProperties.getProperty("updateCategoryName"));
    }

    /**
     * Positive test case for updateMessageCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {updateMessageCategory} integration test with mandatory parameter.")
    public void testUpdateMessageCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_updateMessageCategory";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_updateMessageCategoryMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

        String messageCategoryId = connectorProperties.getProperty("updateMessageCategoryId");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/messageCategories/" + messageCategoryId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("category").get("name"), connectorProperties.getProperty("updateCategoryName"));
    }

    /**
     * Positive test case for updateNotebookCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {updateNotebookCategory} integration test with mandatory parameter.")
    public void testUpdateNotebookCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_updateNotebookCategory";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_updateNotebookCategoryMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

        String notebookCategoryId = connectorProperties.getProperty("updateNotebookCategoryId");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/notebookCategories/" + notebookCategoryId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("category").get("name"), connectorProperties.getProperty("updateCategoryName"));
    }

    /**
     * Positive test case for updateProjectCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {updateProjectCategory} integration test with mandatory parameter.")
    public void testUpdateProjectCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_updateProjectCategory";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_updateProjectCategoryMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

        String projectCategoryId = connectorProperties.getProperty("updateProjectCategoryId");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projectCategories/" + projectCategoryId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("category").get("name"), connectorProperties.getProperty("updateCategoryName"));
    }

    /**
     * Positive test case for getAllFileCategoriesOfProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getAllFileCategoriesOfProject} integration test with mandatory parameter.")
    public void testGetAllFileCategoriesOfProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getAllFileCategoriesOfProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/fileCategories.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAllFileCategoriesOfProjectMandatory.json");
        JSONArray esbCategoriesArray = esbRestResponse.getBody().getJSONArray("categories");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiCategoriesArray = apiRestResponse.getBody().getJSONArray("categories");

        Assert.assertEquals(esbCategoriesArray.length(), apiCategoriesArray.length());
        if (esbCategoriesArray.length() > 0 && apiCategoriesArray.length() > 0) {

            Assert.assertEquals(esbCategoriesArray.getJSONObject(0).getString("id"), apiCategoriesArray.getJSONObject(0).getString("id"));
            Assert.assertEquals(esbCategoriesArray.getJSONObject(0).getString("name"), apiCategoriesArray.getJSONObject(0).getString("name"));
        }
    }

    /**
     * Positive test case for getAllLinkCategoriesOfProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getAllLinkCategoriesOfProject} integration test with mandatory parameter.")
    public void testGetAllLinkCategoriesOfProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getAllLinkCategoriesOfProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/linkCategories.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAllLinkCategoriesOfProjectMandatory.json");
        JSONArray esbCategoriesArray = esbRestResponse.getBody().getJSONArray("categories");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiCategoriesArray = apiRestResponse.getBody().getJSONArray("categories");

        Assert.assertEquals(esbCategoriesArray.length(), apiCategoriesArray.length());
        if (esbCategoriesArray.length() > 0 && apiCategoriesArray.length() > 0) {

            Assert.assertEquals(esbCategoriesArray.getJSONObject(0).getString("id"), apiCategoriesArray.getJSONObject(0).getString("id"));
            Assert.assertEquals(esbCategoriesArray.getJSONObject(0).getString("name"), apiCategoriesArray.getJSONObject(0).getString("name"));
        }
    }

    /**
     * Positive test case for getAllMessageCategoriesOfProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getAllMessageCategoriesOfProject} integration test with mandatory parameter.")
    public void testGetAllMessageCategoriesOfProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getAllMessageCategoriesOfProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/messageCategories.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAllMessageCategoriesOfProjectMandatory.json");
        JSONArray esbCategoriesArray = esbRestResponse.getBody().getJSONArray("categories");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiCategoriesArray = apiRestResponse.getBody().getJSONArray("categories");

        Assert.assertEquals(esbCategoriesArray.length(), apiCategoriesArray.length());
        if (esbCategoriesArray.length() > 0 && apiCategoriesArray.length() > 0) {

            Assert.assertEquals(esbCategoriesArray.getJSONObject(0).getString("id"), apiCategoriesArray.getJSONObject(0).getString("id"));
            Assert.assertEquals(esbCategoriesArray.getJSONObject(0).getString("name"), apiCategoriesArray.getJSONObject(0).getString("name"));
        }
    }

    /**
     * Positive test case for getAllNotebookCategoriesOfProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getAllNotebookCategoriesOfProject} integration test with mandatory parameter.")
    public void testGetAllNotebookCategoriesOfProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getAllNotebookCategoriesOfProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/notebookCategories.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAllNotebookCategoriesOfProjectMandatory.json");
        JSONArray esbCategoriesArray = esbRestResponse.getBody().getJSONArray("categories");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiCategoriesArray = apiRestResponse.getBody().getJSONArray("categories");

        Assert.assertEquals(esbCategoriesArray.length(), apiCategoriesArray.length());
        if (esbCategoriesArray.length() > 0 && apiCategoriesArray.length() > 0) {

            Assert.assertEquals(esbCategoriesArray.getJSONObject(0).getString("id"), apiCategoriesArray.getJSONObject(0).getString("id"));
            Assert.assertEquals(esbCategoriesArray.getJSONObject(0).getString("name"), apiCategoriesArray.getJSONObject(0).getString("name"));
        }
    }

    /**
     * Positive test case for getAllProjectCategories method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getAllProjectCategories} integration test with mandatory parameter.")
    public void testGetAllProjectCategoriesWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getAllProjectCategories";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projectCategories.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAllProjectCategoriesMandatory.json");
        JSONArray esbCategoriesArray = esbRestResponse.getBody().getJSONArray("categories");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiCategoriesArray = apiRestResponse.getBody().getJSONArray("categories");

        Assert.assertEquals(esbCategoriesArray.length(), apiCategoriesArray.length());
        if (esbCategoriesArray.length() > 0 && apiCategoriesArray.length() > 0) {

            Assert.assertEquals(esbCategoriesArray.getJSONObject(0).getString("id"), apiCategoriesArray.getJSONObject(0).getString("id"));
            Assert.assertEquals(esbCategoriesArray.getJSONObject(0).getString("name"), apiCategoriesArray.getJSONObject(0).getString("name"));
        }
    }

    /**
     * Positive test case for getFileCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getFileCategory} integration test with mandatory parameter.")
    public void testGetFileCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getFileCategory";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/fileCategories/" + connectorProperties.getProperty("updateFileCategoryId") + ".json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getFileCategoryMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("category").get("id"), apiRestResponse.getBody().getJSONObject("category").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("category").get("name"), apiRestResponse.getBody().getJSONObject("category").get("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("category").get("project-id"), apiRestResponse.getBody().getJSONObject("category").get("project-id"));
    }

    /**
     * Positive test case for getLinkCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getLinkCategory} integration test with mandatory parameter.")
    public void testGetLinkCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getLinkCategory";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/linkCategories/" + connectorProperties.getProperty("updateLinkCategoryId") + ".json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getLinkCategoryMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("category").get("id"), apiRestResponse.getBody().getJSONObject("category").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("category").get("name"), apiRestResponse.getBody().getJSONObject("category").get("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("category").get("project-id"), apiRestResponse.getBody().getJSONObject("category").get("project-id"));
    }

    /**
     * Positive test case for getMessageCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getMessageCategory} integration test with mandatory parameter.")
    public void testGetMessageCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getMessageCategory";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/messageCategories/" + connectorProperties.getProperty("updateMessageCategoryId") + ".json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getMessageCategoryMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("category").get("id"), apiRestResponse.getBody().getJSONObject("category").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("category").get("name"), apiRestResponse.getBody().getJSONObject("category").get("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("category").get("project-id"), apiRestResponse.getBody().getJSONObject("category").get("project-id"));
    }

    /**
     * Positive test case for getNotebookCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getNotebookCategory} integration test with mandatory parameter.")
    public void testGetNotebookCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getNotebookCategory";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/notebookCategories/" + connectorProperties.getProperty("updateNotebookCategoryId") + ".json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getNotebookCategoryMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("category").get("id"), apiRestResponse.getBody().getJSONObject("category").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("category").get("name"), apiRestResponse.getBody().getJSONObject("category").get("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("category").get("project-id"), apiRestResponse.getBody().getJSONObject("category").get("project-id"));
    }

    /**
     * Positive test case for getProjectCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getProjectCategory} integration test with mandatory parameter.")
    public void testGetProjectCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getProjectCategory";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projectCategories/" + connectorProperties.getProperty("updateProjectCategoryId") + ".json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getProjectCategoryMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("category").get("id"), apiRestResponse.getBody().getJSONObject("category").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("category").get("name"), apiRestResponse.getBody().getJSONObject("category").get("name"));
    }

    /**
     * Positive test case for deleteFileCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {deleteFileCategory} integration test with mandatory parameter.")
    public void testDeleteFileCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deleteFileCategory";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteFileCategoryMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for deleteLinkCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {deleteLinkCategory} integration test with mandatory parameter.")
    public void testDeleteLinkCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deleteLinkCategory";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteLinkCategoryMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for deleteMessageCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {deleteMessageCategory} integration test with mandatory parameter.")
    public void testDeleteMessageCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deleteMessageCategory";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteMessageCategoryMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for deleteNotebookCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {deleteNotebookCategory} integration test with mandatory parameter.")
    public void testDeleteNotebookCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deleteNotebookCategory";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteNotebookCategoryMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for deleteProjectCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {deleteProjectCategory} integration test with mandatory parameter.")
    public void testDeleteProjectCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deleteProjectCategory";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteProjectCategoryMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for updateComment method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {updateComment} integration test with mandatory parameter.")
    public void testUpdateCommentWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_updateComment";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_updateCommentMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

        String commentId = connectorProperties.getProperty("updateCommentId");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/comments/" + commentId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("comment").get("body"), connectorProperties.getProperty("commentBody"));
    }

    /**
     * Positive test case for getComment method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getComment} integration test with mandatory parameter.")
    public void testGetCommentWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getComment";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/comments/" + connectorProperties.getProperty("updateCommentId") + ".json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getCommentMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("comment").get("id"), apiRestResponse.getBody().getJSONObject("comment").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("comment").get("body"), apiRestResponse.getBody().getJSONObject("comment").get("body"));
    }

    /**
     * Positive test case for getRecentComments method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getRecentComments} integration test with mandatory parameter.")
    public void testGetRecentCommentsWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getRecentComments";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("commentResource") + "/" + connectorProperties.getProperty("commentResourceId") + "/comments.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getRecentCommentsMandatory.json");
        JSONArray esbCommentsArray = esbRestResponse.getBody().getJSONArray("comments");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiCommentsArray = apiRestResponse.getBody().getJSONArray("comments");

        Assert.assertEquals(esbCommentsArray.length(), apiCommentsArray.length());
        if (esbCommentsArray.length() > 0 && apiCommentsArray.length() > 0) {

            Assert.assertEquals(esbCommentsArray.getJSONObject(0).getString("id"), apiCommentsArray.getJSONObject(0).getString("id"));
            Assert.assertEquals(esbCommentsArray.getJSONObject(0).getString("body"), apiCommentsArray.getJSONObject(0).getString("body"));
        }
    }

    /**
     * Positive test case for getRecentComments method with optional parameters.
     */
    @Test(enabled = false, description = "teamwork {getRecentComments} integration test with optional parameter.")
    public void testGetRecentCommentsWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_getRecentComments";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("commentResource") + "/" + connectorProperties.getProperty("commentResourceId") + "/comments.json?page=" + connectorProperties.getProperty("page") + "&pageSize=" + connectorProperties.getProperty("pageSize");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getRecentCommentsOptional.json");
        JSONArray esbCommentsArray = esbRestResponse.getBody().getJSONArray("comments");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiCommentsArray = apiRestResponse.getBody().getJSONArray("comments");

        Assert.assertEquals(esbCommentsArray.length(), apiCommentsArray.length());
        if (esbCommentsArray.length() > 0 && apiCommentsArray.length() > 0) {

            Assert.assertEquals(esbCommentsArray.getJSONObject(0).getString("id"), apiCommentsArray.getJSONObject(0).getString("id"));
            Assert.assertEquals(esbCommentsArray.getJSONObject(0).getString("body"), apiCommentsArray.getJSONObject(0).getString("body"));
        }
    }

    /**
     * Positive test case for deleteComment method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {deleteComment} integration test with mandatory parameter.")
    public void testDeleteCommentWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deleteComment";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteCommentMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for createCompany method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {createCompany} integration test with mandatory parameter.")
    public void testCreateCompanyWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_createCompany";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_createCompanyMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for createCompany method with optional parameters.
     */
    @Test(enabled = false, description = "teamwork {createCompany} integration test with optional parameter.")
    public void testCreateCompanyWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_createCompany";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_createCompanyOptional.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for updateCompany method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {updateCompany} integration test with mandatory parameter.")
    public void testUpdateCompanyWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_updateCompany";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_updateCompanyMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

        String companyId = connectorProperties.getProperty("updateCompanyId");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/companies/" + companyId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("company").get("name"), connectorProperties.getProperty("updateCompanyName"));
    }

    /**
     * Positive test case for updateCompany method with optional parameters.
     */
    @Test(enabled = false, description = "teamwork {updateCompany} integration test with optional parameter.")
    public void testUpdateCompanyWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_updateCompany";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_updateCompanyOptional.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

        String companyId = connectorProperties.getProperty("updateCompanyId");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/companies/" + companyId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("company").get("name"), connectorProperties.getProperty("optionalUpdateCompanyName"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("company").get("city"), connectorProperties.getProperty("optionalCity"));
    }

    /**
     * Positive test case for getAllCompanies method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getAllCompanies} integration test with mandatory parameter.")
    public void testGetAllCompaniesWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getAllCompanies";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/companies.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAllCompaniesMandatory.json");
        JSONArray esbCompaniesArray = esbRestResponse.getBody().getJSONArray("companies");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiCompaniesArray = apiRestResponse.getBody().getJSONArray("companies");

        Assert.assertEquals(esbCompaniesArray.length(), apiCompaniesArray.length());
        if (esbCompaniesArray.length() > 0 && apiCompaniesArray.length() > 0) {

            Assert.assertEquals(esbCompaniesArray.getJSONObject(0).getString("id"), apiCompaniesArray.getJSONObject(0).getString("id"));
            Assert.assertEquals(esbCompaniesArray.getJSONObject(0).getString("name"), apiCompaniesArray.getJSONObject(0).getString("name"));
            Assert.assertEquals(esbCompaniesArray.getJSONObject(0).getString("company_name_url"), apiCompaniesArray.getJSONObject(0).getString("company_name_url"));
        }
    }

    /**
     * Positive test case for getCompaniesWithinAProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getCompaniesWithinAProject} integration test with mandatory parameter.")
    public void testGetCompaniesWithinAProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getCompaniesWithinAProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/companies.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getCompaniesWithinAProjectMandatory.json");
        JSONArray esbCompaniesArray = esbRestResponse.getBody().getJSONArray("companies");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiCompaniesArray = apiRestResponse.getBody().getJSONArray("companies");

        Assert.assertEquals(esbCompaniesArray.length(), apiCompaniesArray.length());
        if (esbCompaniesArray.length() > 0 && apiCompaniesArray.length() > 0) {

            Assert.assertEquals(esbCompaniesArray.getJSONObject(0).getString("id"), apiCompaniesArray.getJSONObject(0).getString("id"));
            Assert.assertEquals(esbCompaniesArray.getJSONObject(0).getString("name"), apiCompaniesArray.getJSONObject(0).getString("name"));
            Assert.assertEquals(esbCompaniesArray.getJSONObject(0).getString("company_name_url"), apiCompaniesArray.getJSONObject(0).getString("company_name_url"));
        }
    }

    /**
     * Positive test case for getCompany method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getCompany} integration test with mandatory parameter.")
    public void testGetCompanyWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getCompany";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/companies/" + connectorProperties.getProperty("companyId") + ".json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getCompanyMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("company").get("id"), apiRestResponse.getBody().getJSONObject("company").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("company").get("name"), apiRestResponse.getBody().getJSONObject("company").get("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("company").get("company_name_url"), apiRestResponse.getBody().getJSONObject("company").get("company_name_url"));
    }

    /**
     * Positive test case for deleteCompany method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {deleteCompany} integration test with mandatory parameter.")
    public void testDeleteCompanyWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deleteCompany";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteCompanyMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for getFilesOnAProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getFilesOnAProject} integration test with mandatory parameter.")
    public void testGetFilesOnAProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getFilesOnAProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/files.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getFilesOnAProjectMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getFile method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getFile} integration test with mandatory parameter.")
    public void testGetFileWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getFile";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/files/" + connectorProperties.getProperty("fileId") + ".json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getFileMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("file").get("id"), apiRestResponse.getBody().getJSONObject("file").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("file").get("name"), apiRestResponse.getBody().getJSONObject("file").get("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("file").get("project-id"), apiRestResponse.getBody().getJSONObject("file").get("project-id"));
    }

    /**
     * Positive test case for deleteFileFromProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {deleteFileFromProject} integration test with mandatory parameter.")
    public void testDeleteFileFromProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deleteFileFromProject";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteFileFromProjectMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for uploadFile method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {uploadFile} integration test with mandatory parameter.")
    public void testuploadFile() throws IOException, JSONException, NoSuchAlgorithmException {

        String esbEndpPoint = multipartProxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&apiKey="
                + connectorProperties.getProperty("apiKey");

        MultipartFormdataProcessor multipartProcessor = new MultipartFormdataProcessor(esbEndpPoint, headersMap);
        multipartProcessor.addFileToRequest("file", connectorProperties.getProperty("uploadFileName"), null, connectorProperties.getProperty("targetFileName"));
        RestResponse<JSONObject> esbRestResponse = multipartProcessor.processForJsonResponse();

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertTrue(esbRestResponse.getBody().getJSONObject("pendingFile").has("ref"));
    }

    /**
     * Positive test case for updateLink method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {updateLink} integration test with mandatory parameter.")
    public void testUpdateLinkWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_updateLink";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_updateLinkMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

        String linkId = connectorProperties.getProperty("updateLinkId");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/links/" + linkId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("link").get("name"), connectorProperties.getProperty("linkName"));
    }

    /**
     * Positive test case for getLinksOnProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getLinksOnProject} integration test with mandatory parameter.")
    public void testGetLinksOnProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getLinksOnProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/links.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getLinksOnProjectMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getLink method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getLink} integration test with mandatory parameter.")
    public void testGetLinkWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getLink";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/links/" + connectorProperties.getProperty("updateLinkId") + ".json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getLinkMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("link").get("id"), apiRestResponse.getBody().getJSONObject("link").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("link").get("name"), apiRestResponse.getBody().getJSONObject("link").get("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("link").get("created-by-userId"), apiRestResponse.getBody().getJSONObject("link").get("created-by-userId"));
    }

    /**
     * Positive test case for getAllLinks method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getAllLinks} integration test with mandatory parameter.")
    public void testGetAllLinksWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getAllLinks";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/links.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAllLinksMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for deleteLink method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {deleteLink} integration test with mandatory parameter.")
    public void testDeleteLinkWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deleteLink";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteLinkMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for updateMessageReply method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {updateMessageReply} integration test with mandatory parameter.")
    public void testUpdateMessageReplyWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_updateMessageReply";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_updateMessageReplyMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

        String messageReplyId = connectorProperties.getProperty("updateMessageReplyId");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/messageReplies/" + messageReplyId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("messageReplies").getJSONObject(0).get("body"), connectorProperties.getProperty("replyMessageBody"));
    }

    /**
     * Positive test case for getMessageReply method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getMessageReply} integration test with mandatory parameter.")
    public void testGetMessageReplyWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getMessageReply";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/messageReplies/" + connectorProperties.getProperty("updateMessageReplyId") + ".json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getMessageReplyMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("messageReplies").getJSONObject(0).get("body"), apiRestResponse.getBody().getJSONArray("messageReplies").getJSONObject(0).get("body"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("messageReplies").getJSONObject(0).get("author_id"), apiRestResponse.getBody().getJSONArray("messageReplies").getJSONObject(0).get("author_id"));
    }

    /**
     * Positive test case for getRepliesToMessage method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getRepliesToMessage} integration test with mandatory parameter.")
    public void testGetRepliesToMessageWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getRepliesToMessage";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/messages/" + connectorProperties.getProperty("replyMessageId") + "/replies.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getRepliesToMessageMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getRepliesToMessage method with optional parameters.
     */
    @Test(enabled = false, description = "teamwork {getRepliesToMessage} integration test with optional parameter.")
    public void testGetRepliesToMessageWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_getRepliesToMessage";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/messages/" + connectorProperties.getProperty("replyMessageId") + "/replies.json?page=" + connectorProperties.getProperty("page") + "&pageSize=" + connectorProperties.getProperty("pageSize");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getRepliesToMessageOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for deleteMessageReply method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {deleteMessageReply} integration test with mandatory parameter.")
    public void testDeleteMessageReplyWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deleteMessageReply";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteMessageReplyMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for updateMessage method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {updateMessage} integration test with mandatory parameter.")
    public void testUpdateMessageWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_updateMessage";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_updateMessageMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

        String messageId = connectorProperties.getProperty("updateMessageId");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/posts/" + messageId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("post").get("body"), connectorProperties.getProperty("messageBody"));
    }

    /**
     * Positive test case for getLatestMessages method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getLatestMessages} integration test with mandatory parameter.")
    public void testGetLatestMessagesWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getLatestMessages";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/posts.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getLatestMessagesMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getArchivedMessages method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getArchivedMessages} integration test with mandatory parameter.")
    public void testGetArchivedMessagesWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getArchivedMessages";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/posts/archive.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getArchivedMessagesMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getMessagesByCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getMessagesByCategory} integration test with mandatory parameter.")
    public void testGetMessagesByCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getMessagesByCategory";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/cat/" + connectorProperties.getProperty("updateMessageCategoryId") + "/posts.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getMessagesByCategoryMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getArchivedMessagesByCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getArchivedMessagesByCategory} integration test with mandatory parameter.")
    public void testGetArchivedMessagesByCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getArchivedMessagesByCategory";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/cat/" + connectorProperties.getProperty("updateMessageCategoryId") + "/posts/archive.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getArchivedMessagesByCategoryMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getMessage method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getMessage} integration test with mandatory parameter.")
    public void testGetMessageWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getMessage";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/posts/" + connectorProperties.getProperty("updateMessageId") + ".json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getMessageMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("post").get("id"), apiRestResponse.getBody().getJSONObject("post").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("post").get("body"), apiRestResponse.getBody().getJSONObject("post").get("body"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("post").get("author-id"), apiRestResponse.getBody().getJSONObject("post").get("author-id"));
    }

    /**
     * Positive test case for archiveAMessage method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {archiveAMessage} integration test with mandatory parameter.")
    public void testArchiveAMessageWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_archiveAMessage";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_archiveAMessageMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for unarchiveAMessage method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {unarchiveAMessage} integration test with mandatory parameter.")
    public void testUnarchiveAMessageWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_unarchiveAMessage";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_unarchiveAMessageMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        ;
    }

    /**
     * Positive test case for deleteMessage method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {deleteMessage} integration test with mandatory parameter.")
    public void testDeleteMessageWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deleteMessage";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteMessageMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for getMilestonesOnAProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getMilestonesOnAProject} integration test with mandatory parameter.")
    public void testGetMilestonesOnAProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getMilestonesOnAProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/milestones.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getMilestonesOnAProjectMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getMilestonesOnAProject method with optional parameters.
     */
    @Test(enabled = false, description = "teamwork {getMilestonesOnAProject} integration test with optional parameter.")
    public void testGetMilestonesOnAProjectWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_getMilestonesOnAProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/milestones.json?find=" + connectorProperties.getProperty("find") + "&getProgress=" + connectorProperties.getProperty("getProgress");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getMilestonesOnAProjectOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllMilestones method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getAllMilestones} integration test with mandatory parameter.")
    public void testGetAllMilestonesWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getAllMilestones";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/milestones.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAllMilestonesMandatory.json");
        JSONArray esbMilestonesArray = esbRestResponse.getBody().getJSONArray("milestones");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiMilestonesArray = apiRestResponse.getBody().getJSONArray("milestones");

        Assert.assertEquals(esbMilestonesArray.length(), apiMilestonesArray.length());
        if (esbMilestonesArray.length() > 0 && apiMilestonesArray.length() > 0) {

            Assert.assertEquals(esbMilestonesArray.getJSONObject(0).getString("id"), apiMilestonesArray.getJSONObject(0).getString("id"));
            Assert.assertEquals(esbMilestonesArray.getJSONObject(0).getString("title"), apiMilestonesArray.getJSONObject(0).getString("title"));
            Assert.assertEquals(esbMilestonesArray.getJSONObject(0).getString("creator-id"), apiMilestonesArray.getJSONObject(0).getString("creator-id"));
        }
    }

    /**
     * Positive test case for getAllMilestones method with optional parameters.
     */
    @Test(enabled = false, description = "teamwork {getAllMilestones} integration test with optional parameter.")
    public void testGetAllMilestonesWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_getAllMilestones";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/milestones.json?find="
                        + connectorProperties.getProperty("find") + "&getProgress=" + connectorProperties.getProperty("getProgress");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAllMilestonesOptional.json");
        JSONArray esbMilestonesArray = esbRestResponse.getBody().getJSONArray("milestones");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiMilestonesArray = apiRestResponse.getBody().getJSONArray("milestones");

        Assert.assertEquals(esbMilestonesArray.length(), apiMilestonesArray.length());
        if (esbMilestonesArray.length() > 0 && apiMilestonesArray.length() > 0) {

            Assert.assertEquals(esbMilestonesArray.getJSONObject(0).getString("id"), apiMilestonesArray.getJSONObject(0).getString("id"));
            Assert.assertEquals(esbMilestonesArray.getJSONObject(0).getString("title"), apiMilestonesArray.getJSONObject(0).getString("title"));
            Assert.assertEquals(esbMilestonesArray.getJSONObject(0).getString("creator-id"), apiMilestonesArray.getJSONObject(0).getString("creator-id"));
        }
    }

    /**
     * Positive test case for getMilestone method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getMilestone} integration test with mandatory parameter.")
    public void testGetMilestoneWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getMilestone";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/milestones/" + connectorProperties.getProperty("milestoneId") + ".json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getMilestoneMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("milestone").get("id"), apiRestResponse.getBody().getJSONObject("milestone").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("milestone").get("title"), apiRestResponse.getBody().getJSONObject("milestone").get("title"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("milestone").get("creator-id"), apiRestResponse.getBody().getJSONObject("milestone").get("creator-id"));
    }

    /**
     * Positive test case for getMilestone method with optional parameters.
     */
    @Test(enabled = false, description = "teamwork {getMilestone} integration test with optional parameter.")
    public void testGetMilestoneWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_getMilestone";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/milestones/" + connectorProperties.getProperty("milestoneId") + ".json?showTaskLists="
                        + connectorProperties.getProperty("showTaskLists") + "&getProgress=" + connectorProperties.getProperty("getProgress") + "&showTasks=" + connectorProperties.getProperty("showTasks");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getMilestoneOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("milestone").get("id"), apiRestResponse.getBody().getJSONObject("milestone").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("milestone").get("title"), apiRestResponse.getBody().getJSONObject("milestone").get("title"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("milestone").get("percentageComplete"), apiRestResponse.getBody().getJSONObject("milestone").get("percentageComplete"));
    }

    /**
     * Positive test case for completeAMilestone method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {completeAMilestone} integration test with mandatory parameter.")
    public void testCompleteAMilestoneWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_completeAMilestone";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_completeAMilestoneMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for uncompleteAMilestone method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {uncompleteAMilestone} integration test with mandatory parameter.")
    public void testUncompleteAMilestoneWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_uncompleteAMilestone";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_uncompleteAMilestoneMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for deleteMilestone method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {deleteMilestone} integration test with mandatory parameter.")
    public void testDeleteMilestoneWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deleteMilestone";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteMilestoneMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for updateNotebook method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {updateNotebook} integration test with mandatory parameter.")
    public void testUpdateNotebookWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_updateNotebook";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_updateNotebookMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

        String notebookId = connectorProperties.getProperty("updateNotebookId");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/notebooks/" + notebookId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("notebook").get("name"), connectorProperties.getProperty("notebookName"));
    }

    /**
     * Positive test case for getNotebooksOnProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getNotebooksOnProject} integration test with mandatory parameter.")
    public void testGetNotebooksOnProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getNotebooksOnProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/notebooks.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getNotebooksOnProjectMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getNotebooksOnProject method with optional parameters.
     */
    @Test(enabled = false, description = "teamwork {getNotebooksOnProject} integration test with optional parameter.")
    public void testGetNotebooksOnProjectWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_getNotebooksOnProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/notebooks.json?includeContent="+connectorProperties.getProperty("notebookIncludeContent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getNotebooksOnProjectOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getNotebooksInCategory method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getNotebooksInCategory} integration test with mandatory parameter.")
    public void testGetNotebooksInCategoryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getNotebooksInCategory";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/notebookCategories/" + connectorProperties.getProperty("notebookCategoryId") + "/notebooks.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getNotebooksInCategoryMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getNotebooksInCategory method with optional parameters.
     */
    @Test(enabled = false, description = "teamwork {getNotebooksInCategory} integration test with optional parameter.")
    public void testGetNotebooksInCategoryWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_getNotebooksInCategory";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/notebookCategories/" + connectorProperties.getProperty("notebookCategoryId") + "/notebooks.json?includeContent="+connectorProperties.getProperty("notebookIncludeContent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getNotebooksInCategoryOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllNotebooks method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getAllNotebooks} integration test with mandatory parameter.")
    public void testGetAllNotebooksWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getAllNotebooks";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/notebooks.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAllNotebooksMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllNotebooks method with optional parameters.
     */
    @Test(enabled = false, description = "teamwork {getAllNotebooks} integration test with optional parameter.")
    public void testGetAllNotebooksWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_getAllNotebooks";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/notebooks.json?includeContent="+connectorProperties.getProperty("notebookIncludeContent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAllNotebooksOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getNotebook method with mandatory parameters.
     */
    @Test(enabled=false, description = "teamwork {getNotebook} integration test with mandatory parameter.")
    public void testGetNotebookWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getNotebook";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/notebooks/"+connectorProperties.getProperty("updateNotebookId")+".json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getNotebookMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("notebook").get("id"), apiRestResponse.getBody().getJSONObject("notebook").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("notebook").get("name"), apiRestResponse.getBody().getJSONObject("notebook").get("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("notebook").get("created-by-userfirstname"), apiRestResponse.getBody().getJSONObject("notebook").get("created-by-userfirstname"));
    }

    /**
     * Positive test case for lockNotebook method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {lockNotebook} integration test with mandatory parameter.")
    public void testLockNotebookWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_lockNotebook";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_lockNotebookMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for unlockNotebook method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {unlockNotebook} integration test with mandatory parameter.")
    public void testUnlockNotebookWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_unlockNotebook";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_unlockNotebookMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for deleteNotebook method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {deleteNotebook} integration test with mandatory parameter.")
    public void testDeleteNotebookWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deleteNotebook";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteNotebookMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for createUser method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {createUser} integration test with mandatory parameter.")
    public void testCreateUserWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_createUser";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_createUserMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for updateUser method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {updateUser} integration test with mandatory parameter.")
    public void testUpdateUserWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_updateUser";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_updateUserMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

        String userId = connectorProperties.getProperty("updateUserId");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/people/" + userId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("person").get("first-name"), connectorProperties.getProperty("updateFirstName"));
    }

    /**
     * Positive test case for getPeopleInProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getPeopleInProject} integration test with mandatory parameter.")
    public void testGetPeopleInProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getPeopleInProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/people.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getPeopleInProjectMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getPeopleInCompany method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getPeopleInCompany} integration test with mandatory parameter.")
    public void testGetPeopleInCompanyWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getPeopleInCompany";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/companies/" + connectorProperties.getProperty("userCompanyId") + "/people.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getPeopleInCompanyMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getPeople method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getPeople} integration test with mandatory parameter.")
    public void testGetPeopleWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getPeople";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/people.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getPeopleMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getPeople method with optional parameters.
     */
    @Test(enabled = false, description = "teamwork {getPeople} integration test with optional parameter.")
    public void testGetPeopleWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_getPeople";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/people.json?page="+connectorProperties.getProperty("page")+ "&pageSize="+connectorProperties.getProperty("pageSize");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getPeopleOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getPerson method with mandatory parameters.
     */
    @Test(enabled=false, description = "teamwork {getPerson} integration test with mandatory parameter.")
    public void testGetPersonWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getPerson";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/people/"+connectorProperties.getProperty("personId")+".json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getPersonMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("person").get("id"), apiRestResponse.getBody().getJSONObject("person").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("person").get("first-name"), apiRestResponse.getBody().getJSONObject("person").get("first-name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("person").get("user-name"), apiRestResponse.getBody().getJSONObject("person").get("user-name"));
    }

    /**
     * Positive test case for getCurrentUser method with mandatory parameters.
     */
    @Test(enabled=false, description = "teamwork {getCurrentUser} integration test with mandatory parameter.")
    public void testGetCurrentUserWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getCurrentUser";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/me.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getCurrentUserMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("person").get("id"), apiRestResponse.getBody().getJSONObject("person").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("person").get("first-name"), apiRestResponse.getBody().getJSONObject("person").get("first-name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("person").get("user-name"), apiRestResponse.getBody().getJSONObject("person").get("user-name"));
    }

    /**
     * Positive test case for getAPIKeyForPeopleOnAccount method with mandatory parameters.
     */
    @Test(enabled=false, description = "teamwork {getAPIKeyForPeopleOnAccount} integration test with mandatory parameter.")
    public void testGetAPIKeyForPeopleOnAccountWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getAPIKeyForPeopleOnAccount";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/people/APIKeys.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAPIKeyForPeopleOnAccountMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("people").getJSONObject(0).get("id"), apiRestResponse.getBody().getJSONArray("people").getJSONObject(0).get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("people").getJSONObject(0).get("first-name"), apiRestResponse.getBody().getJSONArray("people").getJSONObject(0).get("first-name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("people").getJSONObject(0).get("userAPIKey"), apiRestResponse.getBody().getJSONArray("people").getJSONObject(0).get("userAPIKey"));
    }

    /**
     * Positive test case for deleteUser method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {deleteUser} integration test with mandatory parameter.")
    public void testDeleteUserWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deleteUser";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteUserMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for createCurrentUserStatus method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {createCurrentUserStatus} integration test with mandatory parameter.")
    public void testCreateCurrentUserStatusWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_createCurrentUserStatus";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_createCurrentUserStatusMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for createUserStatus method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {createUserStatus} integration test with mandatory parameter.")
    public void testCreateUserStatusWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_createUserStatus";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_createUserStatusMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for updateCurrentUserStatus method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {updateCurrentUserStatus} integration test with mandatory parameter.")
    public void testUpdateCurrentUserStatusWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_updateCurrentUserStatus";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_updateCurrentUserStatusMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/me/status.json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("userStatus").get("status"), connectorProperties.getProperty("updateStatus"));
    }

    /**
     * Positive test case for updatePeopleStatus method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {updatePeopleStatus} integration test with mandatory parameter.")
    public void testUpdatePeopleStatusWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_updatePeopleStatus";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_updatePeopleStatusMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for updateUserStatus method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {updateUserStatus} integration test with mandatory parameter.")
    public void testUpdateUserStatusWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_updateUserStatus";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_updateUserStatusMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

        String userId = connectorProperties.getProperty("statusUserId");
        String statusId = connectorProperties.getProperty("userStatusId");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/people/" + userId + "/status.json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("userStatus").get("status"), connectorProperties.getProperty("updateStatus"));
    }

    /**
     * Positive test case for getCurrentUserStatus method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getCurrentUserStatus} integration test with optional parameter.")
    public void testGetCurrentUserStatusWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getCurrentUserStatus";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/me/status.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getCurrentUserStatusMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getEverybodyStatus method with mandatory parameters.
     */
    @Test(enabled=false, description = "teamwork {getEverybodyStatus} integration test with mandatory parameter.")
    public void testGetEverybodyStatusWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getEverybodyStatus";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/people/status.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getEverybodyStatusMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getUserStatus method with mandatory parameters.
     */
    @Test(enabled=false, description = "teamwork {getUserStatus} integration test with mandatory parameter.")
    public void testGetUserStatusWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getUserStatus";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/people/"+ connectorProperties.getProperty("statusUserId")+"/status.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getUserStatusMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for deleteCurrentUserStatus method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {deleteCurrentUserStatus} integration test with mandatory parameter.")
    public void testDeleteCurrentUserStatusWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deleteCurrentUserStatus";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteCurrentUserStatusMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for deletePeopleStatus method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {deletePeopleStatus} integration test with mandatory parameter.")
    public void testDeletePeopleStatusWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deletePeopleStatus";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deletePeopleStatusMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for deleteUserStatus method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {deleteUserStatus} integration test with mandatory parameter.")
    public void testDeleteUserStatusWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deleteUserStatus";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteUserStatusMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for addUserToProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {addUserToProject} integration test with mandatory parameter.")
    public void testAddUserToProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_addUserToProject";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_addUserToProjectMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for updateUserPermissionOnProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {updateUserPermissionOnProject} integration test with mandatory parameter.")
    public void testUpdateUserPermissionOnProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_updateUserPermissionOnProject";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_updateUserPermissionOnProjectMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("updatePermissionProjectId") + "/people/" + connectorProperties.getProperty("permissionUserId") + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("people").getJSONObject(0).getJSONObject("permissions").get("add-tasks"), connectorProperties.getProperty("addTasks"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("people").getJSONObject(0).getJSONObject("permissions").get("view-time"), connectorProperties.getProperty("viewTime"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("people").getJSONObject(0).getJSONObject("permissions").get("add-messages"), connectorProperties.getProperty("addMessages"));
    }

    /**
     * Positive test case for getUserPermissionsOnProject method with mandatory parameters.
     */
    @Test(enabled=false, description = "teamwork {getUserPermissionsOnProject} integration test with mandatory parameter.")
    public void testGetUserPermissionsOnProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getUserPermissionsOnProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/"+connectorProperties.getProperty("permissionProjectId")+"/people/"+connectorProperties.getProperty("permissionUserId")+".json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getUserPermissionsOnProjectMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for removeUserFromProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {removeUserFromProject} integration test with mandatory parameter.")
    public void testRemoveUserFromProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_removeUserFromProject";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_removeUserFromProjectMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for listRolesOnProject method with mandatory parameters.
     */
    @Test(enabled=false, description = "teamwork {listRolesOnProject} integration test with mandatory parameter.")
    public void testGetListRolesOnProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_listRolesOnProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/"+connectorProperties.getProperty("projectId")+"/projectroles.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_listRolesOnProjectMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for createProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {createProject} integration test with mandatory parameter.")
    public void testCreateProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_createProject";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_createProjectMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for updateProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {updateProject} integration test with mandatory parameter.")
    public void testUpdateProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_updateProject";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_updateProjectMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

        String projectId = connectorProperties.getProperty("updateProjectId");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + projectId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("project").get("name"), connectorProperties.getProperty("projectName"));
    }

    /**
     * Positive test case for getStarredProjects method with mandatory parameters.
     */
    @Test(enabled=false, description = "teamwork {getStarredProjects} integration test with mandatory parameter.")
    public void testGetStarredProjectsWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getStarredProjects";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/starred.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getStarredProjectsMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getProject method with mandatory parameters.
     */
    @Test(enabled=false, description = "teamwork {getProject} integration test with mandatory parameter.")
    public void testGetProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/"+connectorProperties.getProperty("projectId")+".json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getProjectMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getProject method with optional parameters.
     */
    @Test(enabled=false, description = "teamwork {getProject} integration test with optional parameter.")
    public void testGetProjectWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_getProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/"+connectorProperties.getProperty("projectId")+".json?includePeople="
                        +connectorProperties.getProperty("projectIncludePeople");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getProjectOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllProjects method with mandatory parameters.
     */
    @Test(enabled=false, description = "teamwork {getAllProjects} integration test with mandatory parameter.")
    public void testGetAllProjectsWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getAllProjects";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAllProjectsMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllProjects method with optional parameters.
     */
    @Test(enabled=false, description = "teamwork {getAllProjects} integration test with optional parameter.")
    public void testGetAllProjectsWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_getAllProjects";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects.json?status="+connectorProperties.getProperty("projectStatus")+"&updatedAfterDate="
                        +connectorProperties.getProperty("projectUpdatedAfterDate")+"&updatedAfterTime"+connectorProperties.getProperty("projectUpdatedAfterTime")+"&orderBy"
                        +connectorProperties.getProperty("projectOrderby")+"&createdAfterDate"+connectorProperties.getProperty("projectCreatedAfterDate")+"&createdAfterTime="
                        +connectorProperties.getProperty("projectCreatedAfterTime")+"&includePeople="+connectorProperties.getProperty("projectIncludePeople")+"&page="+connectorProperties.getProperty("page");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAllProjectsOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for starAProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {starAProject} integration test with mandatory parameter.")
    public void testStarAProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_starAProject";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_starAProjectMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for unstarAProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {unstarAProject} integration test with mandatory parameter.")
    public void testUnstarAProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_unstarAProject";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_unstarAProjectMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for deleteProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {deleteProject} integration test with mandatory parameter.")
    public void testDeleteProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deleteProject";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteProjectMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for createTaskList method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {createTaskList} integration test with mandatory parameter.")
    public void testCreateTaskListWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_createTaskList";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_createTaskListMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for updateTaskList method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {updateTaskList} integration test with mandatory parameter.")
    public void testUpdateTaskListWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_updateTaskList";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_updateTaskListMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

        String toDoListId = connectorProperties.getProperty("updateToDoListId");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/todo_lists/" + toDoListId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("todo-list").get("name"), connectorProperties.getProperty("updateTaskListName"));
    }

    /**
     * Positive test case for getAllTemplateTaskLists method with mandatory parameters.
     */
    @Test(enabled=false, description = "teamwork {getAllTemplateTaskLists} integration test with mandatory parameter.")
    public void testGetAllTemplateTaskListsWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getAllTemplateTaskLists";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/tasklists/templates.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAllTemplateTaskListsMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getTaskList method with mandatory parameters.
     */
    @Test(enabled=false, description = "teamwork {getTaskList} integration test with mandatory parameter.")
    public void testGetTaskListWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getTaskList";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/todo_lists/"+connectorProperties.getProperty("updateToDoListId")+".json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getTaskListMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getTaskList method with optional parameters.
     */
    @Test(enabled=false, description = "teamwork {getTaskList} integration test with optional parameter.")
    public void testGetTaskListWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_getTaskList";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/todo_lists/"+connectorProperties.getProperty("updateToDoListId")+".json?filter="+connectorProperties.getProperty("taskListStatus")+
                        "&showTasks="+connectorProperties.getProperty("taskListShowTasks")+"&status="+connectorProperties.getProperty("taskListStatus");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getTaskListOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getTaskListsOnProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getTaskListsOnProject} integration test with mandatory parameter.")
    public void testGetTaskListsOnProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getTaskListsOnProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/todo_lists.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getTaskListsOnProjectMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getTaskListsOnProject method with optional parameters.
     */
    @Test(enabled = false, description = "teamwork {getTaskListsOnProject} integration test with optional parameter.")
    public void testGetTaskListsOnProjectWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_getTaskListsOnProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/todo_lists.json?filter="+connectorProperties.getProperty("taskListFilter")+
                        "&showMilestones="+connectorProperties.getProperty("taskListShowMilestones")+"&showTasks="+connectorProperties.getProperty("taskListShowTasks")+"&getOverdueCount="+
                        connectorProperties.getProperty("taskListGetOverdueCount")+"&getCompletedCount="+connectorProperties.getProperty("taskListGetCompletedCount")+"&status="+
                        connectorProperties.getProperty("taskListStatus")+"&includeOverdue="+connectorProperties.getProperty("taskListIncludeOverdue")+"&getSubTasks="+connectorProperties.getProperty("taskListGetSubTasks")
                        +"&nestSubTasks="+connectorProperties.getProperty("taskListNestSubTasks")+"&responsible-party-id="+connectorProperties.getProperty("taskListResponsiblePartyId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getTaskListsOnProjectOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for reorderTaskLists method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {reorderTaskLists} integration test with mandatory parameter.")
    public void testReorderTaskListsWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_reorderTaskLists";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_reorderTaskListsMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for deleteTaskList method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {deleteTaskList} integration test with mandatory parameter.")
    public void testDeleteTaskListWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deleteTaskList";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteTaskListMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for createTask method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {createTask} integration test with mandatory parameter.")
    public void testCreateTaskWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_createTask";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_createTaskMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for updateTask method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {updateTask} integration test with mandatory parameter.")
    public void testUpdateTaskWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_updateTask";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_updateTaskMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

        String taskId = connectorProperties.getProperty("updateTaskId");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/tasks/" + taskId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("todo-item").get("content"), connectorProperties.getProperty("updateTaskContent"));
    }

    /**
     * Positive test case for getAllTasks method with mandatory parameters.
     */
    @Test(enabled=false, description = "teamwork {getAllTasks} integration test with mandatory parameter.")
    public void testGetAllTasksWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getAllTasks";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/tasks.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAllTasksMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllTasks method with optional parameters.
     */
    @Test(enabled=false, description = "teamwork {getAllTasks} integration test with optional parameter.")
    public void testGetAllTasksWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_getAllTasks";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/tasks.json?filter="+connectorProperties.getProperty("taskFilter")+
                        "&page="+connectorProperties.getProperty("page")+"&pageSize="+connectorProperties.getProperty("pageSize")+"&startdate="+
                        connectorProperties.getProperty("taskStartDate")+"&enddate="+connectorProperties.getProperty("taskEndDate")+"&updatedAfterDate="+
                        connectorProperties.getProperty("taskUpdatedAfterDate")+"&showDeleted="+connectorProperties.getProperty("taskShowDeleted")+"&includeCompletedTasks="+connectorProperties.getProperty("taskIncludeCompletedTasks")
                        +"&includeCompletedSubtasks="+connectorProperties.getProperty("taskIncludeCompletedSubTasks")+"&creator-ids="+connectorProperties.getProperty("taskCreatorIds")+"&include="+connectorProperties.getProperty("taskInclude")+
                        "&responsible-party-ids="+connectorProperties.getProperty("responsiblePartyIdForTask")+"&sort="+connectorProperties.getProperty("taskSort")+"&getSubTasks="+
                        connectorProperties.getProperty("taskGetSubTasks")+"&nestSubTasks="+connectorProperties.getProperty("taskNestSubTasks")+"&getFiles="+
                        connectorProperties.getProperty("taskGetFiles")+"&dataSet="+connectorProperties.getProperty("taskDataSet")+"&includeToday="+
                        connectorProperties.getProperty("taskIncludeToday")+"&ignore-start-dates="+connectorProperties.getProperty("taskIgnoreStartDates");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAllTasksOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getTask method with mandatory parameters.
     */
    @Test(enabled=false, description = "teamwork {getTask} integration test with mandatory parameter.")
    public void testGetTaskWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getTask";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/tasks/"+connectorProperties.getProperty("updateTaskId")+".json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getTaskMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getTask method with optional parameters.
     */
    @Test(enabled=false, description = "teamwork {getTask} integration test with optional parameter.")
    public void testGetTaskWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_getTask";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/tasks/"+connectorProperties.getProperty("updateTaskId")+".json?getFiles="+connectorProperties.getProperty("taskGetFiles")+
                        "&dataSet="+connectorProperties.getProperty("taskDataSet")+"&nestSubTasks="+connectorProperties.getProperty("taskNestSubTasks")+"&includeCompletedSubtasks="+connectorProperties.getProperty("taskIncludeCompletedTasks");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getTaskOptional.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getTasksOnProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getTasksOnProject} integration test with mandatory parameter.")
    public void testGetTasksOnProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getTasksOnProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/tasks.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getTasksOnProjectMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getTasksOnProject method with optional parameters.
     */
    @Test(enabled = false, description = "teamwork {getTasksOnProject} integration test with optional parameter.")
    public void testGetTasksOnProjectWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_getTasksOnProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/tasks.json?filter="+connectorProperties.getProperty("taskFilter")+
                        "&page="+connectorProperties.getProperty("page")+"&pageSize="+connectorProperties.getProperty("pageSize")+"&startdate="+
                        connectorProperties.getProperty("taskStartDate")+"&enddate="+connectorProperties.getProperty("taskEndDate")+"&updatedAfterDate="+
                        connectorProperties.getProperty("taskUpdatedAfterDate")+"&showDeleted="+connectorProperties.getProperty("taskShowDeleted")+"&includeCompletedTasks="+connectorProperties.getProperty("taskIncludeCompletedTasks")
                        +"&includeCompletedSubtasks="+connectorProperties.getProperty("taskIncludeCompletedSubTasks")+"&creator-ids="+connectorProperties.getProperty("taskCreatorIds")+"&include="+connectorProperties.getProperty("taskInclude")+
                        "&responsible-party-ids="+connectorProperties.getProperty("responsiblePartyIdForTask")+"&sort="+connectorProperties.getProperty("taskSort")+"&getSubTasks="+
                        connectorProperties.getProperty("taskGetSubTasks")+"&nestSubTasks="+connectorProperties.getProperty("taskNestSubTasks")+"&getFiles="+
                        connectorProperties.getProperty("taskGetFiles")+"&dataSet="+connectorProperties.getProperty("taskDataSet")+"&includeToday="+
                        connectorProperties.getProperty("taskIncludeToday")+"&ignore-start-dates="+connectorProperties.getProperty("taskIgnoreStartDates");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getTasksOnProjectOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getTasksOnTaskList method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getTasksOnTaskList} integration test with mandatory parameter.")
    public void testGetTasksOnTaskListWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getTasksOnTaskList";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/tasklists/" + connectorProperties.getProperty("taskListIdForTask") + "/tasks.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getTasksOnTaskListMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getTasksOnTaskList method with optional parameters.
     */
    @Test(enabled = false, description = "teamwork {getTasksOnTaskList} integration test with optional parameter.")
    public void testGetTasksOnTaskListWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_getTasksOnTaskList";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/tasklists/" + connectorProperties.getProperty("taskListIdForTask") + "/tasks.json?filter="+connectorProperties.getProperty("taskFilter")+
                        "&page="+connectorProperties.getProperty("page")+"&pageSize="+connectorProperties.getProperty("pageSize")+"&startdate="+
                        connectorProperties.getProperty("taskStartDate")+"&enddate="+connectorProperties.getProperty("taskEndDate")+"&updatedAfterDate="+
                        connectorProperties.getProperty("taskUpdatedAfterDate")+"&showDeleted="+connectorProperties.getProperty("taskShowDeleted")+"&includeCompletedTasks="+connectorProperties.getProperty("taskIncludeCompletedTasks")
                        +"&includeCompletedSubtasks="+connectorProperties.getProperty("taskIncludeCompletedSubTasks")+"&creator-ids="+connectorProperties.getProperty("taskCreatorIds")+"&include="+connectorProperties.getProperty("taskInclude")+
                        "&responsible-party-ids="+connectorProperties.getProperty("responsiblePartyIdForTask")+"&sort="+connectorProperties.getProperty("taskSort")+"&getSubTasks="+
                        connectorProperties.getProperty("taskGetSubTasks")+"&nestSubTasks="+connectorProperties.getProperty("taskNestSubTasks")+"&getFiles="+
                        connectorProperties.getProperty("taskGetFiles")+"&dataSet="+connectorProperties.getProperty("taskDataSet")+"&includeToday="+
                        connectorProperties.getProperty("taskIncludeToday")+"&ignore-start-dates="+connectorProperties.getProperty("taskIgnoreStartDates");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getTasksOnTaskListOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for reorderTasks method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {reorderTasks} integration test with mandatory parameter.")
    public void testReorderTasktsWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_reorderTasks";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_reorderTasksMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for completeATask method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {completeATask} integration test with mandatory parameter.")
    public void testCompleteATaskWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_completeATask";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_completeATaskMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for uncompleteATask method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {uncompleteATask} integration test with mandatory parameter.")
    public void testUncompleteATaskWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_uncompleteATask";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_uncompleteATaskMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for deleteTask method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {deleteTask} integration test with mandatory parameter.")
    public void testDeleteTaskWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deleteTask";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteTaskMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }

    /**
     * Positive test case for updateTimeEntry method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {updateTimeEntry} integration test with mandatory parameter.")
    public void testUpdateTimeEntryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_updateTimeEntry";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_updateTimeEntryMandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);

        String timeEntryId = connectorProperties.getProperty("updateTimeEntryId");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/time_entries/" + timeEntryId + ".json";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("time-entry").get("description"), connectorProperties.getProperty("timeEntryDescription"));
    }

    /**
     * Positive test case for getTimeEntry method with mandatory parameters.
     */
    @Test(enabled=false, description = "teamwork {getTimeEntry} integration test with mandatory parameter.")
    public void testGetTimeEntryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getTimeEntry";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/time_entries/"+connectorProperties.getProperty("updateTimeEntryId")+".json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getTimeEntryMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getTasksOnProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getTimeEntriesForToDoItem} integration test with mandatory parameter.")
    public void testGetTimeEntriesForToDoItemWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getTimeEntriesForToDoItem";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/todo_items/" + connectorProperties.getProperty("toDoItemId") + "/time_entries.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getTimeEntriesForToDoItemMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getTimeEntriesForProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getTimeEntriesForProject} integration test with mandatory parameter.")
    public void testGetTimeEntriesForProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getTimeEntriesForProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/time_entries.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getTimeEntriesForProjectMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getTimeEntriesForProject method with optional parameters.
     */
    @Test(enabled = false, description = "teamwork {getTimeEntriesForProject} integration test with optional parameter.")
    public void testGetTimeEntriesForProjectWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_getTimeEntriesForProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/time_entries.json?page="+connectorProperties.getProperty("page")+
                        "&fromdate="+connectorProperties.getProperty("timeEntryFromDate")+"&fromtime="+connectorProperties.getProperty("timeEntryFromTime")+"&todate="+
                        connectorProperties.getProperty("timeEntryToDate")+"&totime="+connectorProperties.getProperty("timeEntryToTime")+"&sortorder="+
                        connectorProperties.getProperty("timeEntrySortOrder")+"&userId="+connectorProperties.getProperty("timeEntryUserId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getTimeEntriesForProjectOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllTimeEntries method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getAllTimeEntries} integration test with mandatory parameter.")
    public void testGetAllTimeEntriesWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getAllTimeEntries";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/time_entries.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAllTimeEntriesMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getAllTimeEntries method with optional parameters.
     */
    @Test(enabled = false, description = "teamwork {getAllTimeEntries} integration test with optional parameter.")
    public void testGetAllTimeEntriesWithOptionalParameters() throws IOException, JSONException {

        String methodName = "tw_getAllTimeEntries";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/time_entries.json?page="+connectorProperties.getProperty("page")+
                        "&fromdate="+connectorProperties.getProperty("timeEntryFromDate")+"&fromtime="+connectorProperties.getProperty("timeEntryFromTime")+"&todate="+
                        connectorProperties.getProperty("timeEntryToDate")+"&totime="+connectorProperties.getProperty("timeEntryToTime")+"&sortorder="+
                        connectorProperties.getProperty("timeEntrySortOrder")+"&userId="+connectorProperties.getProperty("timeEntryUserId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getAllTimeEntriesOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getTimeTotalsOnProject method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getTimeTotalsOnProject} integration test with mandatory parameter.")
    public void testGetTimeTotalsOnProjectWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getTimeTotalsOnProject";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + connectorProperties.getProperty("projectId") + "/time/total.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getTimeTotalsOnProjectMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getTimeTotalsOnTask method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getTimeTotalsOnTask} integration test with mandatory parameter.")
    public void testGetTimeTotalsOnTaskWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getTimeTotalsOnTask";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/tasks/" + connectorProperties.getProperty("toDoItemId") + "/time/total.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getTimeTotalsOnTaskMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getTimeTotalsOnTaskList method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {getTimeTotalsOnTaskList} integration test with mandatory parameter.")
    public void testGetTimeTotalsOnTaskListWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_getTimeTotalsOnTaskList";
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/tasklists/" + connectorProperties.getProperty("taskListIdForTask") + "/time/total.json";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_getTimeTotalsOnTaskListMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for deleteTimeEntry method with mandatory parameters.
     */
    @Test(enabled = false, description = "teamwork {deleteTimeEntry} integration test with mandatory parameter.")
    public void testDeleteTimeEntryWithMandatoryParameters() throws IOException, JSONException {

        String methodName = "tw_deleteTimeEntry";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(getProxyServiceURL(methodName), "POST", esbRequestHeadersMap, "esb_deleteTimeEntryMandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("STATUS"), "OK");
    }
}