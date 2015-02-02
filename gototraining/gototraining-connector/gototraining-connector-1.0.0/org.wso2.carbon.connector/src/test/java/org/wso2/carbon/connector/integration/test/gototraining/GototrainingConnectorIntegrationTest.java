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

package org.wso2.carbon.connector.integration.test.gototraining;

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

public class GototrainingConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    private final Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    private final Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    private String apiRequestUrl;

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        init("gototraining-connector-1.0.0");

        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Accept", "application/json");

        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Authorization", "OAuth oauth_token=" + connectorProperties.getProperty("accessToken"));

        apiRequestUrl = connectorProperties.getProperty("apiUrl") + "/G2T/rest";
    }

    /**
     * Positive test case for createTraining method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "gototraining {createTraining} integration test with mandatory parameters.")
    public void testCreateTrainingWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createTraining");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createTraining_mandatory.json");

        final String trainingKey = esbRestResponse.getBody().getString("output");
        connectorProperties.setProperty("trainingKeyMandatory", trainingKey.substring(1, trainingKey.length() - 1));

        final String apiEndPoint = apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey")
                + "/trainings/" + connectorProperties.getProperty("trainingKeyMandatory");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(connectorProperties.getProperty("createTrainingStartDate"), apiRestResponse.getBody()
                .getJSONArray("times").getJSONObject(0).getString("startDate"));
        Assert.assertEquals(connectorProperties.getProperty("createTrainingEndDate"), apiRestResponse.getBody()
                .getJSONArray("times").getJSONObject(0).getString("endDate"));
        Assert.assertEquals(connectorProperties.getProperty("createTrainingDescription"), apiRestResponse.getBody()
                .getString("description"));
    }

    /**
     * Positive test case for createTraining method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "gototraining {createTraining} integration test with optional parameters.")
    public void testCreateTrainingWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createTraining");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createTraining_optional.json");

        final String trainingKey = esbRestResponse.getBody().getString("output");
        connectorProperties.setProperty("trainingKeyOptional", trainingKey.substring(1, trainingKey.length() - 1));

        final String apiEndPoint = apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey")
                + "/trainings/" + connectorProperties.getProperty("trainingKeyOptional");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(connectorProperties.getProperty("createTrainingTimeZone"), apiRestResponse.getBody()
                .getString("timeZone"));
        Assert.assertEquals(Boolean.parseBoolean(connectorProperties.getProperty("createTrainingDisableWebRegistration")),
                apiRestResponse.getBody().getJSONObject("registrationSettings").getBoolean("disableWebRegistration"));
        Assert.assertEquals( Boolean.parseBoolean(connectorProperties.getProperty("createTrainingDisableConfirmationEmail")),
                apiRestResponse.getBody().getJSONObject("registrationSettings").getBoolean("disableConfirmationEmail"));
    }

    /**
     * Negative test case for createTraining method.
     */
    @Test(groups = { "wso2.esb" }, description = "gototraining {createTraining} integration test with negative case.")
    public void testCreateTrainingWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createTraining");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createTraining_negative.json");

        final String apiEndPoint = apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey")
                + "/trainings";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_createTraining_negative.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getString("errorCode"),
                apiRestResponse.getBody().getString("errorCode"));
        Assert.assertEquals(esbRestResponse.getBody().getString("description"),
                apiRestResponse.getBody().getString("description"));
    }

    /**
     * Positive test case for updateTraining method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTrainingWithMandatoryParameters" },
            description = "gototraining {updateTraining} integration test with mandatory parameters.")
    public void testUpdateTrainingWithMandatoryParameters() throws IOException, JSONException, InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:updateTraining");

        final String apiEndPoint = apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey")
                + "/trainings/" + connectorProperties.getProperty("trainingKeyMandatory");

        RestResponse<JSONObject> apiRestResponseBeforeUpdate = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap);
        final String nameBeforeUpdate = apiRestResponseBeforeUpdate.getBody().getString("name");
        final String descriptionBeforeUpdate = apiRestResponseBeforeUpdate.getBody().getString("description");

        // Update method, upon successful operation, returns only the status code 204 No entity with an empty body.
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTraining_mandatory.json");

        RestResponse<JSONObject> apiRestResponseAfterUpdate = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap);
        final String nameAfterUpdate = apiRestResponseAfterUpdate.getBody().getString("name");
        final String descriptionAfterUpdate = apiRestResponseAfterUpdate.getBody().getString("description");

        Assert.assertNotEquals(nameBeforeUpdate, nameAfterUpdate);
        Assert.assertNotEquals(descriptionBeforeUpdate, descriptionAfterUpdate);
        Assert.assertEquals(connectorProperties.getProperty("updateTrainingName"), nameAfterUpdate);
        Assert.assertEquals(connectorProperties.getProperty("updateTrainingDescription"), descriptionAfterUpdate);

        // Timeout to overcome API rate requirement.
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("updateTimeout")));
    }

    /**
     * Positive test case for updateTraining method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTrainingWithOptionalParameters" },
            description = "gototraining {updateTraining} integration test with optional parameters.")
    public void testUpdateTrainingWithOptionalParameters() throws IOException, JSONException, InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:updateTraining");

        final String apiEndPoint = apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey")
                + "/trainings/" + connectorProperties.getProperty("trainingKeyOptional");

        RestResponse<JSONObject> apiRestResponseBeforeUpdate = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap);
        final String startDateBeforeUpdate = apiRestResponseBeforeUpdate.getBody().getJSONArray("times")
                .getJSONObject(0).getString("startDate");
        final String endDateBeforeUpdate = apiRestResponseBeforeUpdate.getBody().getJSONArray("times").getJSONObject(0)
                .getString("endDate");

        // Update method, upon successful operation, returns only the status code 204 No entity with an empty body.
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTraining_optional.json");

        RestResponse<JSONObject> apiRestResponseAfterUpdate = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap);
        final String startDateAfterUpdate = apiRestResponseAfterUpdate.getBody().getJSONArray("times").getJSONObject(0)
                .getString("startDate");
        final String endDateAfterUpdate = apiRestResponseAfterUpdate.getBody().getJSONArray("times").getJSONObject(0)
                .getString("endDate");

        Assert.assertNotEquals(startDateBeforeUpdate, startDateAfterUpdate);
        Assert.assertNotEquals(endDateBeforeUpdate, endDateAfterUpdate);
        Assert.assertEquals(connectorProperties.getProperty("updateTrainingStartDate"), startDateAfterUpdate);
        Assert.assertEquals(connectorProperties.getProperty("updateTrainingEndDate"), endDateAfterUpdate);

        // Timeout to overcome API rate requirement.
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("updateTimeout")));
    }

    /**
     * Negative test case for updateTraining method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTrainingWithMandatoryParameters" },
            description = "gototraining {updateTraining} integration test with negative case.")
    public void testUpdateTrainingWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updateTraining");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_updateTraining_negative.json");

        String apiEndPoint = apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey")
                + "/trainings/" + connectorProperties.getProperty("trainingKeyMandatory") + "/times";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap,
                "api_updateTraining_negative.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getString("errorCode"),
                apiRestResponse.getBody().getString("errorCode"));
        Assert.assertEquals(esbRestResponse.getBody().getString("description"),
                apiRestResponse.getBody().getString("description"));
    }

    /**
     * Positive test case for getTraining method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateTrainingWithMandatoryParameters" },
            groups = { "wso2.esb" }, description = "gototraining {getTraining} integration test with mandatory parameters.")
    public void testGetTrainingWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getTraining");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getTraining_mandatory.json");

        final String apiEndPoint = apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey")
                + "/trainings/" + connectorProperties.getProperty("trainingKeyMandatory");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("times").getJSONObject(0).getString("startDate"),
                apiRestResponse.getBody().getJSONArray("times").getJSONObject(0).getString("startDate"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("times").getJSONObject(0).getString("endDate"),
                apiRestResponse.getBody().getJSONArray("times").getJSONObject(0).getString("endDate"));
        Assert.assertEquals(esbRestResponse.getBody().getString("description"),
                apiRestResponse.getBody().getString("description"));
    }

    /**
     * Positive test case for listTrainings method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = {"testCreateTrainingWithMandatoryParameters",
            "testCreateTrainingWithOptionalParameters"}, description = "gototraining {listTrainings} integration test with mandatory parameters.")
    public void testListTrainingsWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listTrainings");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listTrainings_mandatory.json");

        final String apiEndPoint = apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey")
                + "/trainings/";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        final JSONArray esbArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        final JSONArray apiArray = new JSONArray(apiRestResponse.getBody().getString("output"));

        Assert.assertEquals(esbArray.length(), apiArray.length());
        Assert.assertEquals(esbArray.getJSONObject(0).getString("name"), apiArray.getJSONObject(0).getString("name"));
        Assert.assertEquals(esbArray.getJSONObject(0).getString("description"),
                apiArray.getJSONObject(0).getString("description"));
        Assert.assertEquals(esbArray.getJSONObject(0).getJSONArray("times").getJSONObject(0).getString("startDate"),
                apiArray.getJSONObject(0).getJSONArray("times").getJSONObject(0).getString("startDate"));
    }

    /**
     * Positive test case for listOrganizers method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateTrainingWithMandatoryParameters" },
            groups = { "wso2.esb" }, description = "gototraining {listOrganizers} integration test with mandatory parameters.")
    public void testListOrganizersWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listOrganizers");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listOrganizers_mandatory.json");

        final String apiEndPoint = apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey")
                + "/trainings/" + connectorProperties.getProperty("trainingKeyMandatory") + "/organizers";

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        final JSONArray esbArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        final JSONArray apiArray = new JSONArray(apiRestResponse.getBody().getString("output"));

        Assert.assertEquals(esbArray.length(), apiArray.length());
        Assert.assertEquals(esbArray.getJSONObject(0).getString("givenName"),
                apiArray.getJSONObject(0).getString("givenName"));
        Assert.assertEquals(esbArray.getJSONObject(0).getString("email"), apiArray.getJSONObject(0).getString("email"));
        Assert.assertEquals(esbArray.getJSONObject(0).getString("surname"),
                apiArray.getJSONObject(0).getString("surname"));
    }

    /**
     * Positive test case for addRegistrant method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTrainingWithMandatoryParameters" },
            description = "gototraining {addRegistrant} integration test with mandatory parameters.")
    public void testAddRegistrantWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:addRegistrant");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_addRegistrant_mandatory.json");
        
        final String registrantKey = esbRestResponse.getBody().getString("registrantKey");
        connectorProperties.setProperty("registrantKey", registrantKey);

        final String apiEndPoint = apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey")
                + "/trainings/" + connectorProperties.getProperty("trainingKeyMandatory") + "/registrants/"
                + registrantKey;

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(registrantKey, apiRestResponse.getBody().getString("registrantKey"));
        Assert.assertEquals(connectorProperties.getProperty("email"), apiRestResponse.getBody().getString("email"));
        Assert.assertEquals(connectorProperties.getProperty("givenName"),
                apiRestResponse.getBody().getString("givenName"));
        Assert.assertEquals(connectorProperties.getProperty("surname"), apiRestResponse.getBody().getString("surname"));
    }

    /**
     * Negative test case for addRegistrant method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTrainingWithMandatoryParameters" },
            description = "gototraining {addRegistrant} integration test with negative case.")
    public void testAddRegistrantWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:addRegistrant");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_addRegistrant_negative.json");

        final String apiEndPoint = apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey")
                + "/trainings/" + connectorProperties.getProperty("trainingKeyMandatory") + "/registrants";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                "api_addRegistrant_negative.json");

        // Asserting status code 409 Conflict
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 409);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 409);
        Assert.assertEquals(esbRestResponse.getBody().getString("registrantKey"),
                apiRestResponse.getBody().getString("registrantKey"));
        Assert.assertEquals(esbRestResponse.getBody().getString("errorCode"),
                apiRestResponse.getBody().getString("errorCode"));
        Assert.assertEquals(esbRestResponse.getBody().getString("description"),
                apiRestResponse.getBody().getString("description"));

    }

    /**
     * Positive test case for getRegistrant method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddRegistrantWithMandatoryParameters" },
            description = "getRegistrant {getRegistrant} integration test with mandatory parameters.")
    public void testGetRegistrantWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getRegistrant");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getRegistrant_mandatory.json");

        final String apiEndPoint = apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey")
                + "/trainings/" + connectorProperties.getProperty("trainingKeyMandatory") + "/registrants/"
                + connectorProperties.getProperty("registrantKey");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().getString("email"), apiRestResponse.getBody().getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("joinUrl"),
                apiRestResponse.getBody().getString("joinUrl"));
    }

    /**
     * Positive test case for listRegistrants method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddRegistrantWithMandatoryParameters" },
            description = "gototraining {listRegistrants} integration test with mandatory parameters.")
    public void testListRegistrantsWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listRegistrants");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listRegistrants_mandatory.json");

        final String apiEndPoint = apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey")
                + "/trainings/" + connectorProperties.getProperty("trainingKeyMandatory") + "/registrants";

        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        final JSONArray esbArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        final JSONArray apiArray = new JSONArray(apiRestResponse.getBody().getString("output"));

        Assert.assertEquals(esbArray.length(), apiArray.length());
        Assert.assertEquals(esbArray.getJSONObject(0).getString("givenName"),
                apiArray.getJSONObject(0).getString("givenName"));
        Assert.assertEquals(esbArray.getJSONObject(0).getString("email"), apiArray.getJSONObject(0).getString("email"));
        Assert.assertEquals(esbArray.getJSONObject(0).getString("status"), apiArray.getJSONObject(0)
                .getString("status"));
    }

}
