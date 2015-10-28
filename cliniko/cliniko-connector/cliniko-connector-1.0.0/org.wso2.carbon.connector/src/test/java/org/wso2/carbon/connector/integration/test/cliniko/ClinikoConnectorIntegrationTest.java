/*
 *  Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.integration.test.cliniko;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class ClinikoConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap;
    private Map<String, String> apiRequestHeadersMap;
    
    private String apiEndpointUrl;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        
        init("cliniko-connector-1.0.0");
        
        esbRequestHeadersMap = new HashMap<String, String>();
        apiRequestHeadersMap = new HashMap<String, String>();
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Accept", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        final String authString = connectorProperties.getProperty("apiKey") + ":";
        apiRequestHeadersMap.put("Authorization", "Basic " + Base64.encode(authString.getBytes()));
        
        apiEndpointUrl = connectorProperties.getProperty("apiUrl") + "/v1";
        
    }
    
    /**
     * Positive test case for listAppointmentTypes method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {listAppointmentTypes} integration test with mandatory parameters.")
    public void testListAppointmentTypesWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listAppointmentTypes");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listAppointmentTypes_mandatory.json");
                
        final String appointmentTypeId = esbRestResponse.getBody().getJSONArray("appointment_types").getJSONObject(0)
                .getString("id");
        connectorProperties.put("appointmentTypeId", appointmentTypeId);
        
        final String apiEndpoint = apiEndpointUrl + "/appointment_types";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("total_entries"), apiRestResponse.getBody().getInt(
                "total_entries"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointment_types").getJSONObject(0).getString(
                "id"), apiRestResponse.getBody().getJSONArray("appointment_types").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointment_types").getJSONObject(0).getString(
                "created_at"), apiRestResponse.getBody().getJSONArray("appointment_types").getJSONObject(0).getString(
                        "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointment_types").getJSONObject(0).getString(
                "color"), apiRestResponse.getBody().getJSONArray("appointment_types").getJSONObject(0).getString(
                        "color"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointment_types").getJSONObject(0).getInt(
                "duration_in_minutes"), apiRestResponse.getBody().getJSONArray("appointment_types").getJSONObject(0)
                        .getInt("duration_in_minutes"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointment_types").getJSONObject(0).getString(
                "name"), apiRestResponse.getBody().getJSONArray("appointment_types").getJSONObject(0).getString(
                        "name"));
                        
    }
    
    /**
     * Positive test case for listAppointmentTypes method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {listAppointmentTypes} integration test with optional parameters.")
    public void testListAppointmentTypesWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listAppointmentTypes");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listAppointmentTypes_optional.json");
                
        final String apiEndpoint = apiEndpointUrl + "/appointment_types?per_page=1&page=2";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointment_types").length(), 1);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointment_types").getJSONObject(0).getString(
                "id"), esbRestResponse.getBody().getJSONArray("appointment_types").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointment_types").getJSONObject(0).getString(
                "updated_at"), apiRestResponse.getBody().getJSONArray("appointment_types").getJSONObject(0).getString(
                        "updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointment_types").getJSONObject(0).getString(
                "color"), apiRestResponse.getBody().getJSONArray("appointment_types").getJSONObject(0).getString(
                        "color"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointment_types").getJSONObject(0).getInt(
                "duration_in_minutes"), apiRestResponse.getBody().getJSONArray("appointment_types").getJSONObject(0)
                        .getInt("duration_in_minutes"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointment_types").getJSONObject(0).getString(
                "name"), apiRestResponse.getBody().getJSONArray("appointment_types").getJSONObject(0).getString(
                        "name"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("appointment_types").length(), 1);
        
    }
    
    /**
     * Negative test case for listAppointmentTypes method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "cliniko {listAppointmentTypes} integration test with negative case.")
    public void testListAppointmentTypesWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listAppointmentTypes");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listAppointmentTypes_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/practitioners/22222222/appointment_types";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for getAppointmentType method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {getAppointmentType} integration test with mandatory parameters.", dependsOnMethods = {
                    "testListAppointmentTypesWithMandatoryParameters" })
    public void testGetAppointmentTypeWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getAppointmentType");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getAppointmentType_mandatory.json");
                
        final String apiEndpoint = apiEndpointUrl + "/appointment_types/" + connectorProperties.getProperty(
                "appointmentTypeId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("color"), apiRestResponse.getBody().getString("color"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("duration_in_minutes"), apiRestResponse.getBody().getInt(
                "duration_in_minutes"));
                
    }
    
    /**
     * Method name: getAppointmentType Test scenario: Optional Reason to skip: There are no optional
     * Test scenario: Optional 
     * Reason to skip: There are no optional parameters in the method to be tested.
     */
    
    /**
     * Negative test case for getAppointmentType method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "cliniko {getAppointmentType} integration test with negative case.")
    public void testGetAppointmentTypeWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getAppointmentType");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getAppointmentType_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/appointment_types/22222222";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for createAppointment method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {createAppointment} integration test with mandatory parameters.", dependsOnMethods = {
                    "testCreatePatientWithOptionalParameters", "testListAppointmentTypesWithMandatoryParameters",
                    "testListBusinessesWithMandatoryParameters", "testListPractitionersWithMandatoryParameters" })
    public void testCreateAppointmentWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createAppointment");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createAppointment_mandatory.json");
                
        final String mandatoryAppointmentId = esbRestResponse.getBody().getString("id");
        
        final String apiEndpoint = apiEndpointUrl + "/appointments/" + mandatoryAppointmentId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("mandatoryAppointmentStartTime"), apiRestResponse.getBody()
                .getString("appointment_start"));
        Assert.assertEquals(connectorProperties.getProperty("optionalPatientId"), apiRestResponse.getBody()
                .getJSONObject("patient").getJSONObject("links").getString("self").split("/")[5]);
        Assert.assertEquals(connectorProperties.getProperty("practitionerId"), apiRestResponse.getBody().getJSONObject(
                "practitioner").getJSONObject("links").getString("self").split("/")[5]);
        Assert.assertEquals(connectorProperties.getProperty("appointmentTypeId"), apiRestResponse.getBody()
                .getJSONObject("appointment_type").getJSONObject("links").getString("self").split("/")[5]);
        Assert.assertEquals(connectorProperties.getProperty("businessId"), apiRestResponse.getBody().getJSONObject(
                "business").getJSONObject("links").getString("self").split("/")[5]);
                
    }
    
    /**
     * Positive test case for createAppointment method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {createAppointment} integration test with optional parameters.", dependsOnMethods = {
                    "testListAppointmentTypesWithMandatoryParameters", "testListBusinessesWithMandatoryParameters",
                    "testCreatePatientWithOptionalParameters", "testListPractitionersWithMandatoryParameters" })
    public void testCreateAppointmentWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createAppointment");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createAppointment_optional.json");
                
        final String optionalAppointmentId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("optionalAppointmentId", optionalAppointmentId);
        
        final String apiEndpoint = apiEndpointUrl + "/appointments/" + optionalAppointmentId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("mandatoryAppointmentEndTime"), apiRestResponse.getBody()
                .getString("appointment_end"));
        Assert.assertEquals(connectorProperties.getProperty("mandatoryAppointmentStartTime"), apiRestResponse.getBody()
                .getString("appointment_start"));
        Assert.assertEquals(connectorProperties.getProperty("appointmentNotes"), apiRestResponse.getBody().getString(
                "notes"));
        Assert.assertEquals(connectorProperties.getProperty("optionalPatientId"), apiRestResponse.getBody()
                .getJSONObject("patient").getJSONObject("links").getString("self").split("/")[5]);
        Assert.assertEquals(connectorProperties.getProperty("practitionerId"), apiRestResponse.getBody().getJSONObject(
                "practitioner").getJSONObject("links").getString("self").split("/")[5]);
        Assert.assertEquals(connectorProperties.getProperty("appointmentTypeId"), apiRestResponse.getBody()
                .getJSONObject("appointment_type").getJSONObject("links").getString("self").split("/")[5]);
        Assert.assertEquals(connectorProperties.getProperty("businessId"), apiRestResponse.getBody().getJSONObject(
                "business").getJSONObject("links").getString("self").split("/")[5]);
        Assert.assertEquals(apiRestResponse.getBody().getBoolean("did_not_arrive"), true);
        Assert.assertEquals(apiRestResponse.getBody().getBoolean("patient_arrived"), true);
        
    }
    
    /**
     * Negative test case for createAppointment method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {createAppointment} integration test with negative case.", dependsOnMethods = {
                    "testListAppointmentTypesWithMandatoryParameters", "testListBusinessesWithMandatoryParameters",
                    "testListPractitionersWithMandatoryParameters" })
    public void testCreateAppointmentWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createAppointment");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createAppointment_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/appointments";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                "api_createAppointment_negative.json");
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").getString("patient_id"), apiRestResponse
                .getBody().getJSONObject("errors").getString("patient_id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString(
                "message"));
                
    }

    /**
     * Method name: updateAppointment 
     * Test scenario: Mandatory 
     * Reason to skip: There are no mandatory parameters that need to be updated.
     */
    
    /**
     * Positive test case for updateAppointment method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {updateAppointment} integration test with optional parameters.", dependsOnMethods = {
                    "testCreateAppointmentWithOptionalParameters" })
    public void testUpdateAppointmentWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateAppointment");
        final String apiEndpoint = apiEndpointUrl + "/appointments/" + connectorProperties.getProperty(
                "optionalAppointmentId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_updateAppointment_optional.json");
                
        RestResponse<JSONObject> apiRestResponse2 = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponse.getBody().getString("appointment_start"), apiRestResponse2.getBody()
                .getString("appointment_start"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("appointment_end"), apiRestResponse2.getBody()
                .getString("appointment_end"));
        Assert.assertNotEquals(apiRestResponse.getBody().getString("notes"), apiRestResponse2.getBody().getString(
                "notes"));
        Assert.assertNotEquals(apiRestResponse.getBody().getBoolean("patient_arrived"), apiRestResponse2.getBody()
                .getBoolean("patient_arrived"));
        Assert.assertNotEquals(apiRestResponse.getBody().getBoolean("did_not_arrive"), apiRestResponse2.getBody()
                .getBoolean("did_not_arrive"));
        Assert.assertEquals(connectorProperties.getProperty("appointmentEndTimeUpdated"), apiRestResponse2.getBody()
                .getString("appointment_end"));
        Assert.assertEquals(connectorProperties.getProperty("appointmentStartTimeUpdated"), apiRestResponse2.getBody()
                .getString("appointment_start"));
        Assert.assertEquals(connectorProperties.getProperty("appointmentNoteUpdates"), apiRestResponse2.getBody()
                .getString("notes"));
        Assert.assertEquals(apiRestResponse2.getBody().getBoolean("patient_arrived"), false);
        Assert.assertEquals(apiRestResponse2.getBody().getBoolean("did_not_arrive"), false);
        
    }
    
    /**
     * Negative test case for updateAppointment method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {updateAppointment} integration test with negative case.", dependsOnMethods = {
                    "testCreateAppointmentWithOptionalParameters" })
    public void testUpdateAppointmentWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateAppointment");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_updateAppointment_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/appointments/" + connectorProperties.getProperty(
                "optionalAppointmentId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap,
                "api_updateAppointment_negative.json");
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").getString("base"), apiRestResponse
                .getBody().getJSONObject("errors").getString("base"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString(
                "message"));
                
    }
    
    /**
     * Positive test case for getAppointment method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {getAppointment} integration test with mandatory parameters.", dependsOnMethods = {
                    "testCreateAppointmentWithOptionalParameters" })
    public void testGetAppointmentWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getAppointment");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getAppointment_mandatory.json");
                
        final String apiEndpoint = apiEndpointUrl + "/appointments/" + connectorProperties.getProperty(
                "optionalAppointmentId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("appointment_start"), apiRestResponse.getBody()
                .getString("appointment_start"));
        Assert.assertEquals(esbRestResponse.getBody().getString("appointment_end"), apiRestResponse.getBody().getString(
                "appointment_end"));
        Assert.assertEquals(esbRestResponse.getBody().getString("notes"), apiRestResponse.getBody().getString("notes"));
        
    }
    
    /**
     * Method name: getAppointment 
     * Test scenario: Optional 
     * Reason to skip: There are no optional parameters to be tested.
     */
    
    /**
     * Negative test case for getAppointment method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "cliniko {getAppointment} integration test with negative case.")
    public void testGetAppointmentWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getAppointment");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getAppointment_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/appointments/444444444";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for listAppointments method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {listAppointments} integration test with mandatory parameters.", dependsOnMethods = {
                    "testCreateAppointmentWithMandatoryParameters", "testCreateAppointmentWithOptionalParameters" })
    public void testListAppointmentsWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listAppointments");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listAppointments_mandatory.json");
                
        final String apiEndpoint = apiEndpointUrl + "/appointments";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("total_entries"), apiRestResponse.getBody().getInt(
                "total_entries"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                "created_at"), apiRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                        "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                "appointment_start"), apiRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                        "appointment_start"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                "appointment_end"), apiRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                        "appointment_end"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getBoolean(
                "did_not_arrive"), apiRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getBoolean(
                        "did_not_arrive"));
                        
    }
    
    /**
     * Positive test case for listAppointments method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {listAppointments} integration test with optional parameters.", dependsOnMethods = {
                    "testCreateAppointmentWithMandatoryParameters", "testCreateAppointmentWithOptionalParameters" })
    public void testListAppointmentsWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listAppointments");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listAppointments_optional.json");
                
        final String apiEndpoint = apiEndpointUrl + "/practitioners/" + connectorProperties.getProperty(
                "practitionerId") + "/appointments";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("total_entries"), apiRestResponse.getBody().getInt(
                "total_entries"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                "created_at"), apiRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                        "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                "appointment_start"), apiRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                        "appointment_start"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                "appointment_end"), apiRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                        "appointment_end"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getBoolean(
                "did_not_arrive"), apiRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getBoolean(
                        "did_not_arrive"));
                        
    }
    
    /**
     * Negative test case for listAppointments method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "cliniko {listAppointments} integration test with negative case.")
    public void testListAppointmentsWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listAppointments");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listAppointments_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/appointments?sort=appointment";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        
    }
    
    /**
     * Positive test case for listCancelledAppointments method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {listCancelledAppointments} integration test with mandatory parameters.")
    public void testListCancelledAppointmentsWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listCancelledAppointments");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listCancelledAppointments_mandatory.json");
                
        final String apiEndpoint = apiEndpointUrl + "/appointments/cancelled";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("total_entries"), apiRestResponse.getBody().getInt(
                "total_entries"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                "created_at"), apiRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                        "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                "appointment_start"), apiRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                        "appointment_start"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                "appointment_end"), apiRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                        "appointment_end"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getBoolean(
                "did_not_arrive"), apiRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getBoolean(
                        "did_not_arrive"));
                        
    }
    
    /**
     * Positive test case for listCancelledAppointments method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {listCancelledAppointments} integration test with optional parameters.")
    public void testListCancelledAppointmentsWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listCancelledAppointments");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listCancelledAppointments_optional.json");
                
        final String apiEndpoint = apiEndpointUrl
                + "/appointments/cancelled?page=1&per_page=1&sort=appointment_start&order=desc";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointments").length(), 1);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                "created_at"), apiRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                        "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                "appointment_start"), apiRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                        "appointment_start"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                "appointment_end"), apiRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getString(
                        "appointment_end"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getBoolean(
                "did_not_arrive"), apiRestResponse.getBody().getJSONArray("appointments").getJSONObject(0).getBoolean(
                        "did_not_arrive"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("appointments").length(), 1);
        
    }
    
    /**
     * Negative test case for listCancelledAppointments method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {listCancelledAppointments} integration test with negative case.")
    public void testListCancelledAppointmentsWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listCancelledAppointments");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listCancelledAppointments_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/appointments/cancelled?page=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString(
                "status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for listAvailableTimes method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {listAvailableTimes} integration test with mandatory parameters.", dependsOnMethods = {
                    "testListBusinessesWithMandatoryParameters", "testListPractitionersWithMandatoryParameters",
                    "testListAppointmentTypesWithMandatoryParameters" })
    public void testListAvailableTimesWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listAvailableTimes");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listAvailableTimes_mandatory.json");
                
        final String apiEndpoint = apiEndpointUrl + "/businesses/" + connectorProperties.getProperty("businessId")
                + "/practitioners/" + connectorProperties.getProperty("practitionerId") + "/appointment_types/"
                + connectorProperties.getProperty("appointmentTypeId") + "/available_times?from=" + connectorProperties
                        .getProperty("availableFromDate") + "&to=" + connectorProperties.getProperty("availableToDate");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("total_entries"), apiRestResponse.getBody().getInt(
                "total_entries"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("available_times").getJSONObject(0).getString(
                "appointment_start"), apiRestResponse.getBody().getJSONArray("available_times").getJSONObject(0)
                        .getString("appointment_start"));
                        
    }
    
    /**
     * Positive test case for listAvailableTimes method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {listAvailableTimes} integration test with optional parameters.", dependsOnMethods = {
                    "testListBusinessesWithMandatoryParameters", "testListPractitionersWithMandatoryParameters",
                    "testListAppointmentTypesWithMandatoryParameters" })
    public void testListAvailableTimesWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listAvailableTimes");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listAvailableTimes_optional.json");
                
        final String apiEndpoint = apiEndpointUrl + "/businesses/" + connectorProperties.getProperty("businessId")
                + "/practitioners/" + connectorProperties.getProperty("practitionerId") + "/appointment_types/"
                + connectorProperties.getProperty("appointmentTypeId") + "/available_times?from=" + connectorProperties
                        .getProperty("availableFromDate") + "&to=" + connectorProperties.getProperty("availableToDate")
                + "&sort=appointment_start&order=desc";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("total_entries"), apiRestResponse.getBody().getInt(
                "total_entries"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("available_times").getJSONObject(0).getString(
                "appointment_start"), apiRestResponse.getBody().getJSONArray("available_times").getJSONObject(0)
                        .getString("appointment_start"));
                        
    }
    
    /**
     * Negative test case for listAvailableTimes method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {listAvailableTimes} integration test with negative case.", dependsOnMethods = {
                    "testListAppointmentTypesWithMandatoryParameters", "testListBusinessesWithMandatoryParameters",
                    "testListPractitionersWithMandatoryParameters" })
    public void testListAvailableTimesWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listAvailableTimes");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listAvailableTimes_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/businesses/" + connectorProperties.getProperty("businessId")
                + "/practitioners/" + connectorProperties.getProperty("practitionerId") + "/appointment_types/"
                + connectorProperties.getProperty("appointmentTypeId") + "/available_times";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        
    }
    
    /**
     * Positive test case for getNextAvailableTime method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {getNextAvailableTime} integration test with mandatory parameters.", dependsOnMethods = {
                    "testListBusinessesWithMandatoryParameters", "testListPractitionersWithMandatoryParameters",
                    "testListAppointmentTypesWithMandatoryParameters" })
    public void testGetNextAvailableTimeWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getNextAvailableTime");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getNextAvailableTime_mandatory.json");
                
        final String apiEndpoint = apiEndpointUrl + "/businesses/" + connectorProperties.getProperty("businessId")
                + "/practitioners/" + connectorProperties.getProperty("practitionerId") + "/appointment_types/"
                + connectorProperties.getProperty("appointmentTypeId") + "/next_available_time";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("appointment_start"), apiRestResponse.getBody()
                .getString("appointment_start"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("links").getString("self"), apiRestResponse
                .getBody().getJSONObject("links").getString("self"));
                
    }
    
    /**
     * Method name: getNextAvailableTime 
     * Test scenario: Optional
     * Reason to skip: There are no optional parameters to be tested.
     */
    
    /**
     * Negative test case for getNextAvailableTime method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {getNextAvailableTime} integration test with negative case.", dependsOnMethods = {
                    "testListBusinessesWithMandatoryParameters", "testListPractitionersWithMandatoryParameters" })
    public void testGetNextAvailableTimeWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getNextAvailableTime");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getNextAvailableTime_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/businesses/" + connectorProperties.getProperty("businessId")
                + "/practitioners/" + connectorProperties.getProperty("practitionerId")
                + "/appointment_types/777777/next_available_time";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for getBusiness method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {getBusiness} integration test with mandatory parameters.", dependsOnMethods = {
                    "testListBusinessesWithMandatoryParameters" })
    public void testGetBusinessWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getBusiness");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getBusiness_mandatory.json");
                
        final String apiEndpoint = apiEndpointUrl + "/businesses/" + connectorProperties.getProperty("businessId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("updated_at"), apiRestResponse.getBody().getString(
                "updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("business_name"), apiRestResponse.getBody().getString(
                "business_name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("country"), apiRestResponse.getBody().getString(
                "country"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("practitioners").getJSONObject("links").getString(
                "self"), apiRestResponse.getBody().getJSONObject("practitioners").getJSONObject("links").getString(
                        "self"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("appointments").getJSONObject("links").getString(
                "self"), apiRestResponse.getBody().getJSONObject("appointments").getJSONObject("links").getString(
                        "self"));
                        
    }
    
    /**
     * Method name: getBusiness 
     * Test scenario: Optional 
     * Reason to skip: There are no optional parameters to be tested.
     */
    
    /**
     * Negative test case for getBusiness method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "cliniko {getBusiness} integration test with negative case.")
    public void testGetBusinessWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getBusiness");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getBusiness_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/businesses/33333333";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for listBusinesses method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "cliniko {listBusinesses} integration test with mandatory parameters.")
    public void testListBusinessesWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listBusinesses");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listBusinesses_mandatory.json");
                
        final String businessId = esbRestResponse.getBody().getJSONArray("businesses").getJSONObject(0).getString("id");
        connectorProperties.put("businessId", businessId);
        
        final String apiEndpoint = apiEndpointUrl + "/businesses";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("total_entries"), apiRestResponse.getBody().getInt(
                "total_entries"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("businesses").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("businesses").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("businesses").getJSONObject(0).getString(
                "created_at"), apiRestResponse.getBody().getJSONArray("businesses").getJSONObject(0).getString(
                        "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("businesses").getJSONObject(0).getString(
                "business_name"), apiRestResponse.getBody().getJSONArray("businesses").getJSONObject(0).getString(
                        "business_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("businesses").getJSONObject(0).getString("country"),
                apiRestResponse.getBody().getJSONArray("businesses").getJSONObject(0).getString("country"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("businesses").getJSONObject(0).getString(
                "updated_at"), apiRestResponse.getBody().getJSONArray("businesses").getJSONObject(0).getString(
                        "updated_at"));
                        
    }
    
    /**
     * Positive test case for listBusinesses method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "cliniko {listBusinesses} integration test with optional parameters.")
    public void testListBusinessesWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listBusinesses");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listBusinesses_optional.json");
                
        final String apiEndpoint = apiEndpointUrl + "/businesses?per_page=1&page=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("businesses").length(), 1);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("businesses").getJSONObject(0).getString("id"),
                esbRestResponse.getBody().getJSONArray("businesses").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("businesses").getJSONObject(0).getString(
                "updated_at"), apiRestResponse.getBody().getJSONArray("businesses").getJSONObject(0).getString(
                        "updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("businesses").getJSONObject(0).getString(
                "business_name"), apiRestResponse.getBody().getJSONArray("businesses").getJSONObject(0).getString(
                        "business_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("businesses").getJSONObject(0).getString("country"),
                apiRestResponse.getBody().getJSONArray("businesses").getJSONObject(0).getString("country"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("businesses").getJSONObject(0).getJSONObject(
                "practitioners").getJSONObject("links").getString("self"), apiRestResponse.getBody().getJSONArray(
                        "businesses").getJSONObject(0).getJSONObject("practitioners").getJSONObject("links").getString(
                                "self"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("businesses").length(), 1);
        
    }
    
    /**
     * Negative test case for listBusinesses method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "cliniko {listBusinesses} integration test with negative case.")
    public void testListBusinessesWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listBusinesses");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listBusinesses_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/businesses?sort=name";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        
    }
    
    /**
     * Positive test case for getInvoice method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {getInvoice} integration test with mandatory parameters.", dependsOnMethods = {
                    "testListInvoicesWithMandatoryParameters" })
    public void testGetInvoiceWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getInvoice");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getInvoice_mandatory.json");
                
        final String apiEndpoint = apiEndpointUrl + "/invoices/" + connectorProperties.getProperty("invoiceId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("invoice_to"), apiRestResponse.getBody().getString(
                "invoice_to"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("status"), apiRestResponse.getBody().getInt("status"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("number"), apiRestResponse.getBody().getInt("number"));
        Assert.assertEquals(esbRestResponse.getBody().getDouble("net_amount"), apiRestResponse.getBody().getDouble(
                "net_amount"));
                
    }
    
    /**
     * Method name: getInvoice 
     * Test scenario: Optional 
     * Reason to skip: There are no optional parameters to be tested.
     */
    
    /**
     * Negative test case for getInvoice method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "cliniko {getInvoice} integration test with negative case.")
    public void testGetInvoiceWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getInvoice");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getInvoice_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/invoices/999999999";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for listInvoices method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {listInvoices} integration test with mandatory parameters.", dependsOnMethods = {
                    "testCreateMedicalAlertWithMandatoryParameters" })
    public void testListInvoicesWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listInvoices");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listInvoices_mandatory.json");
                
        final String invoiceId = esbRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getString("id");
        connectorProperties.put("invoiceId", invoiceId);
        
        final String apiEndpoint = apiEndpointUrl + "/invoices";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("total_entries"), apiRestResponse.getBody().getInt(
                "total_entries"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getString("created_at"),
                apiRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getString("issue_date"),
                apiRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getString("issue_date"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getDouble("net_amount"),
                apiRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getDouble("net_amount"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getString("invoice_to"),
                apiRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getString("invoice_to"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getInt("number"),
                apiRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getInt("number"));
                
    }
    
    /**
     * Positive test case for listInvoices method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {listInvoices} integration test with optional parameters.", dependsOnMethods = {
                    "testCreateMedicalAlertWithMandatoryParameters" })
    public void testListInvoicesWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listInvoices");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listInvoices_optional.json");
                
        final String apiEndpoint = apiEndpointUrl + "/invoices?q=" + URLEncoder.encode("issue_date:>=2015-10-08",
                "UTF-8");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("total_entries"), apiRestResponse.getBody().getInt(
                "total_entries"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getString("created_at"),
                apiRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getString("issue_date"),
                apiRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getString("issue_date"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getDouble("net_amount"),
                apiRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getDouble("net_amount"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getString("invoice_to"),
                apiRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getString("invoice_to"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getInt("number"),
                apiRestResponse.getBody().getJSONArray("invoices").getJSONObject(0).getInt("number"));
                
    }
    
    /**
     * Negative test case for listInvoices method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "cliniko {listInvoices} integration test with negative case.")
    public void testListInvoicesWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listInvoices");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listInvoices_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/practitioners/55555/invoices";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for getMedicalAlert method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {getMedicalAlert} integration test with mandatory parameters.", dependsOnMethods = {
                    "testCreateMedicalAlertWithMandatoryParameters" })
    public void testGetMedicalAlertWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getMedicalAlert");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getMedicalAlert_mandatory.json");
                
        final String apiEndpoint = apiEndpointUrl + "/medical_alerts/" + connectorProperties.getProperty(
                "mandatoryMedicalAlertId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("updated_at"), apiRestResponse.getBody().getString(
                "updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("patient").getJSONObject("links").getString("self")
                .split("/")[5], apiRestResponse.getBody().getJSONObject("patient").getJSONObject("links").getString(
                        "self").split("/")[5]);
                        
    }
    
    /**
     * Method name: getMedicalAlert 
     * Test scenario: Optional 
     * Reason to skip: There are no optional parameters to be tested.
     */
    
    /**
     * Negative test case for getMedicalAlert method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "cliniko {getMedicalAlert} integration test with negative case.")
    public void testGetMedicalAlertWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getMedicalAlert");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getMedicalAlert_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/medical_alerts/555555555";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for listMedicalAlerts method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {listMedicalAlerts} integration test with mandatory parameters.", dependsOnMethods = {
                    "testCreateMedicalAlertWithMandatoryParameters" })
    public void testListMedicalAlertsWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listMedicalAlerts");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listMedicalAlerts_mandatory.json");
                
        final String apiEndpoint = apiEndpointUrl + "/medical_alerts";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("total_entries"), apiRestResponse.getBody().getInt(
                "total_entries"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("medical_alerts").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("medical_alerts").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("medical_alerts").getJSONObject(0).getString(
                "created_at"), apiRestResponse.getBody().getJSONArray("medical_alerts").getJSONObject(0).getString(
                        "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("medical_alerts").getJSONObject(0).getString("name"),
                apiRestResponse.getBody().getJSONArray("medical_alerts").getJSONObject(0).getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("medical_alerts").getJSONObject(0).getString(
                "updated_at"), apiRestResponse.getBody().getJSONArray("medical_alerts").getJSONObject(0).getString(
                        "updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("medical_alerts").getJSONObject(0).getJSONObject(
                "patient").getJSONObject("links").getString("self").split("/")[5], apiRestResponse.getBody()
                        .getJSONArray("medical_alerts").getJSONObject(0).getJSONObject("patient").getJSONObject("links")
                        .getString("self").split("/")[5]);
                        
    }
    
    /**
     * Positive test case for listMedicalAlerts method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {listMedicalAlerts} integration test with optional parameters.", dependsOnMethods = {
                    "testCreateMedicalAlertWithMandatoryParameters" })
    public void testListMedicalAlertsWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listMedicalAlerts");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listMedicalAlerts_optional.json");
                
        final String apiEndpoint = apiEndpointUrl + "/patients/" + connectorProperties.getProperty("optionalPatientId")
                + "/medical_alerts";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("total_entries"), apiRestResponse.getBody().getInt(
                "total_entries"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("medical_alerts").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("medical_alerts").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("medical_alerts").getJSONObject(0).getString(
                "created_at"), apiRestResponse.getBody().getJSONArray("medical_alerts").getJSONObject(0).getString(
                        "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("medical_alerts").getJSONObject(0).getString("name"),
                apiRestResponse.getBody().getJSONArray("medical_alerts").getJSONObject(0).getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("medical_alerts").getJSONObject(0).getString(
                "updated_at"), apiRestResponse.getBody().getJSONArray("medical_alerts").getJSONObject(0).getString(
                        "updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("medical_alerts").getJSONObject(0).getJSONObject(
                "patient").getJSONObject("links").getString("self").split("/")[5], apiRestResponse.getBody()
                        .getJSONArray("medical_alerts").getJSONObject(0).getJSONObject("patient").getJSONObject("links")
                        .getString("self").split("/")[5]);
                        
    }
    
    /**
     * Negative test case for listMedicalAlerts method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "cliniko {listMedicalAlerts} integration test with negative case.")
    public void testListMedicalAlertsWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listMedicalAlerts");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listMedicalAlerts_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/medical_alerts?sort=patient_name";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        
    }

    /**
     * Positive test case for updateMedicalAlert method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {updateMedicalAlert} integration test with optional parameters.", dependsOnMethods = {
                    "testCreateMedicalAlertWithMandatoryParameters" })
    public void testUpdateMedicalAlertWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateMedicalAlert");
        final String apiEndpoint = apiEndpointUrl + "/medical_alerts/" + connectorProperties.getProperty(
                "mandatoryMedicalAlertId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_updateMedicalAlert_optional.json");
                
        RestResponse<JSONObject> apiRestResponse2 = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponse.getBody().getString("name"), apiRestResponse2.getBody().getString(
                "name"));
        Assert.assertEquals(connectorProperties.getProperty("medicalAlertNameUpdated"), apiRestResponse2.getBody()
                .getString("name"));
                
    }
    
    /**
     * Negative test case for updateMedicalAlert method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {updateMedicalAlert} integration test with negative case.", dependsOnMethods = {
                    "testCreateAppointmentWithOptionalParameters" })
    public void testUpdateMedicalAlertWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateMedicalAlert");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_updateMedicalAlert_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/medical_alerts/999999";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap,
                "api_updateMedicalAlert_negative.json");
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for createPatient method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "cliniko {createPatient} integration test with mandatory parameters.")
    public void testCreatePatientWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createPatient");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createPatient_mandatory.json");
                
        final String mandatoryPatientId = esbRestResponse.getBody().getString("id");
        
        final String apiEndpoint = apiEndpointUrl + "/patients/" + mandatoryPatientId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("mandatoryPatientFirstName"), apiRestResponse.getBody()
                .getString("first_name"));
        Assert.assertEquals(connectorProperties.getProperty("mandatoryPatientLastName"), apiRestResponse.getBody()
                .getString("last_name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("medical_alerts").getJSONObject("links").getString(
                "self"), apiRestResponse.getBody().getJSONObject("medical_alerts").getJSONObject("links").getString(
                        "self"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("appointments").getJSONObject("links").getString(
                "self"), apiRestResponse.getBody().getJSONObject("appointments").getJSONObject("links").getString(
                        "self"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("links").getString("self"), apiRestResponse
                .getBody().getJSONObject("links").getString("self"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("invoices").getJSONObject("links").getString(
                "self"), apiRestResponse.getBody().getJSONObject("invoices").getJSONObject("links").getString("self"));
                
    }
    
    /**
     * Positive test case for createPatient method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "cliniko {createPatient} integration test with optional parameters.")
    public void testCreatePatientWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createPatient");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createPatient_optional.json");
                
        final String optionalPatientId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("optionalPatientId", optionalPatientId);
        
        final String apiEndpoint = apiEndpointUrl + "/patients/" + optionalPatientId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("optionalPatientAddressLine1"), apiRestResponse.getBody()
                .getString("address_1"));
        Assert.assertEquals(connectorProperties.getProperty("optionalPatientAddressLine2"), apiRestResponse.getBody()
                .getString("address_2"));
        Assert.assertEquals(connectorProperties.getProperty("optionalPatientAddressLine3"), apiRestResponse.getBody()
                .getString("address_3"));
        Assert.assertEquals(connectorProperties.getProperty("optionalPatientCity"), apiRestResponse.getBody().getString(
                "city"));
        Assert.assertEquals(connectorProperties.getProperty("optionalPatientCountry"), apiRestResponse.getBody()
                .getString("country"));
        Assert.assertEquals(connectorProperties.getProperty("optionalPatientDateOfBirth"), apiRestResponse.getBody()
                .getString("date_of_birth"));
        Assert.assertEquals(connectorProperties.getProperty("optionalPatientEmail"), apiRestResponse.getBody()
                .getString("email"));
        Assert.assertEquals(connectorProperties.getProperty("optionalPatientFirstName"), apiRestResponse.getBody()
                .getString("first_name"));
        Assert.assertEquals(connectorProperties.getProperty("optionalPatientGender"), apiRestResponse.getBody()
                .getString("gender"));
        Assert.assertEquals(connectorProperties.getProperty("optionalPatientLastName"), apiRestResponse.getBody()
                .getString("last_name"));
                
    }
    
    /**
     * Negative test case for createPatient method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "cliniko {createPatient} integration test with negative case.")
    public void testCreatePatientWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createPatient");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createPatient_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/patients";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                "api_createPatient_negative.json");
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").getString("first_name"), apiRestResponse
                .getBody().getJSONObject("errors").getString("first_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").getString("last_name"), apiRestResponse
                .getBody().getJSONObject("errors").getString("last_name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString(
                "message"));
                
    }
    
    /**
     * Positive test case for getPatient method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {getPatient} integration test with mandatory parameters.", dependsOnMethods = {
                    "testCreatePatientWithOptionalParameters" })
    public void testGetPatientWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getPatient");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getPatient_mandatory.json");
                
        final String apiEndpoint = apiEndpointUrl + "/patients/" + connectorProperties.getProperty("optionalPatientId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("first_name"), apiRestResponse.getBody().getString(
                "first_name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("city"), apiRestResponse.getBody().getString("city"));
        Assert.assertEquals(esbRestResponse.getBody().getString("country"), apiRestResponse.getBody().getString(
                "country"));
        Assert.assertEquals(esbRestResponse.getBody().getString("email"), apiRestResponse.getBody().getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getString("date_of_birth"), apiRestResponse.getBody().getString(
                "date_of_birth"));
        Assert.assertEquals(esbRestResponse.getBody().getString("address_1"), apiRestResponse.getBody().getString(
                "address_1"));
                
    }
    
    /**
     * Method name: getPatient 
     * Test scenario: Optional
     * Reason to skip: There are no optional parameters to be tested.
     */
    
    /**
     * Negative test case for getPatient method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "cliniko {getPatient} integration test with negative case.")
    public void testGetPatientWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getPatient");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getPatient_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/patients/11111111";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for listPatients method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {listPatients} integration test with mandatory parameters.", dependsOnMethods = {
                    "testCreatePatientWithMandatoryParameters", "testCreatePatientWithOptionalParameters" })
    public void testListPatientsWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listPatients");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listPatients_mandatory.json");
                
        final String apiEndpoint = apiEndpointUrl + "/patients";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("patients").length(), apiRestResponse.getBody()
                .getJSONArray("patients").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("patients").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("patients").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("patients").getJSONObject(0).getString("created_at"),
                apiRestResponse.getBody().getJSONArray("patients").getJSONObject(0).getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("patients").getJSONObject(0).getString("first_name"),
                apiRestResponse.getBody().getJSONArray("patients").getJSONObject(0).getString("first_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("patients").getJSONObject(0).getString("last_name"),
                apiRestResponse.getBody().getJSONArray("patients").getJSONObject(0).getString("last_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("patients").getJSONObject(0).getString("updated_at"),
                apiRestResponse.getBody().getJSONArray("patients").getJSONObject(0).getString("updated_at"));
                
    }
    
    /**
     * Positive test case for listPatients method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {listPatients} integration test with optional parameters.", dependsOnMethods = {
                    "testCreatePatientWithMandatoryParameters", "testCreatePatientWithOptionalParameters" })
    public void testListPatientsWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listPatients");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listPatients_optional.json");
                
        final String apiEndpoint = apiEndpointUrl + "/patients?per_page=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("patients").length(), 1);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("patients").getJSONObject(0).getString("id"),
                esbRestResponse.getBody().getJSONArray("patients").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("patients").getJSONObject(0).getString("created_at"),
                apiRestResponse.getBody().getJSONArray("patients").getJSONObject(0).getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("patients").getJSONObject(0).getString("first_name"),
                apiRestResponse.getBody().getJSONArray("patients").getJSONObject(0).getString("first_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("patients").getJSONObject(0).getString("last_name"),
                apiRestResponse.getBody().getJSONArray("patients").getJSONObject(0).getString("last_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("patients").getJSONObject(0).getString("updated_at"),
                apiRestResponse.getBody().getJSONArray("patients").getJSONObject(0).getString("updated_at"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("patients").length(), 1);
        
    }
    
    /**
     * Negative test case for listPatients method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "cliniko {listPatients} integration test with negative case.")
    public void testListPatientsWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listPatients");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listPatients_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/patients?page=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody().getString(
                "status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
        
    }
    
    /**
     * Positive test case for getPractitioner method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {getPractitioner} integration test with mandatory parameters.", dependsOnMethods = {
                    "testListPractitionersWithMandatoryParameters" })
    public void testGetPractitionerWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getPractitioner");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getPractitioner_mandatory.json");
                
        final String apiEndpoint = apiEndpointUrl + "/practitioners/" + connectorProperties.getProperty(
                "practitionerId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("title"), apiRestResponse.getBody().getString("title"));
        Assert.assertEquals(esbRestResponse.getBody().getString("first_name"), apiRestResponse.getBody().getString(
                "first_name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("last_name"), apiRestResponse.getBody().getString(
                "last_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("appointments").getJSONObject("links").getString(
                "self"), apiRestResponse.getBody().getJSONObject("appointments").getJSONObject("links").getString(
                        "self"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("practitioner_reference_numbers").getJSONObject(
                "links").getString("self"), apiRestResponse.getBody().getJSONObject("practitioner_reference_numbers")
                        .getJSONObject("links").getString("self"));
                        
    }
    
    /**
     * Method name: getPractitioner
     * Test scenario: Optional 
     * Reason to skip: There are no optional parameters to be tested.
     */
    
    /**
     * Negative test case for getPractitioner method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "cliniko {getPractitioner} integration test with negative case.")
    public void testGetPractitionerWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getPractitioner");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getPractitioner_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/practitioners/333333";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for listPractitioners method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {listPractitioners} integration test with mandatory parameters.")
    public void testListPractitionersWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listPractitioners");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listPractitioners_mandatory.json");
                
        final String practitionerId = esbRestResponse.getBody().getJSONArray("practitioners").getJSONObject(0)
                .getString("id");
        connectorProperties.put("practitionerId", practitionerId);
        
        final String apiEndpoint = apiEndpointUrl + "/practitioners";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("total_entries"), apiRestResponse.getBody().getInt(
                "total_entries"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("practitioners").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("practitioners").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("practitioners").getJSONObject(0).getString(
                "created_at"), apiRestResponse.getBody().getJSONArray("practitioners").getJSONObject(0).getString(
                        "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("practitioners").getJSONObject(0).getString(
                "first_name"), apiRestResponse.getBody().getJSONArray("practitioners").getJSONObject(0).getString(
                        "first_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("practitioners").getJSONObject(0).getString(
                "last_name"), apiRestResponse.getBody().getJSONArray("practitioners").getJSONObject(0).getString(
                        "last_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("practitioners").getJSONObject(0).getString("title"),
                apiRestResponse.getBody().getJSONArray("practitioners").getJSONObject(0).getString("title"));
                
    }
    
    /**
     * Positive test case for listPractitioners method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {listPractitioners} integration test with optional parameters.")
    public void testListPractitionersWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listPractitioners");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listPractitioners_optional.json");
                
        final String apiEndpoint = apiEndpointUrl + "/practitioners?per_page=1&page=1&sort=created_at";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("practitioners").length(), 1);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("practitioners").getJSONObject(0).getString("id"),
                esbRestResponse.getBody().getJSONArray("practitioners").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("practitioners").getJSONObject(0).getString(
                "updated_at"), apiRestResponse.getBody().getJSONArray("practitioners").getJSONObject(0).getString(
                        "updated_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("practitioners").getJSONObject(0).getString(
                "display_name"), apiRestResponse.getBody().getJSONArray("practitioners").getJSONObject(0).getString(
                        "display_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("practitioners").getJSONObject(0).getString("title"),
                apiRestResponse.getBody().getJSONArray("practitioners").getJSONObject(0).getString("title"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("practitioners").getJSONObject(0).getJSONObject(
                "appointment_types").getJSONObject("links").getString("self"), apiRestResponse.getBody().getJSONArray(
                        "practitioners").getJSONObject(0).getJSONObject("appointment_types").getJSONObject("links")
                        .getString("self"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("practitioners").length(), 1);
        
    }
    
    /**
     * Negative test case for listPractitioners method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "cliniko {listPractitioners} integration test with negative case.")
    public void testListPractitionersWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listPractitioners");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listPractitioners_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/practitioners?sort=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        
    }
    
    /**
     * Positive test case for getProduct method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {getProduct} integration test with mandatory parameters.", dependsOnMethods = {
                    "testListProductsWithMandatoryParameters" })
    public void testGetProductWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getProduct");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getProduct_mandatory.json");
                
        final String apiEndpoint = apiEndpointUrl + "/products/" + connectorProperties.getProperty("productId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString(
                "created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString(
                "code"));
        Assert.assertEquals(esbRestResponse.getBody().getInt("stock_level"), apiRestResponse.getBody().getInt(
                "stock_level"));
        Assert.assertEquals(esbRestResponse.getBody().getDouble("cost_price"), apiRestResponse.getBody().getDouble(
                "cost_price"));
                
    }
    
    /**
     * Method name: getProduct
     * Test scenario: Optional 
     * Reason to skip: There are no optional parameters to be tested.
     */
    
    /**
     * Negative test case for getProduct method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "cliniko {getProduct} integration test with negative case.")
    public void testGetProductWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getProduct");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getProduct_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/products/88888888";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Positive test case for listProducts method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {listProducts} integration test with mandatory parameters.", dependsOnMethods = {
                    "testCreateMedicalAlertWithMandatoryParameters" })
    public void testListProductsWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listProducts");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listProducts_mandatory.json");
                
        final String productId = esbRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("id");
        connectorProperties.put("productId", productId);
        
        final String apiEndpoint = apiEndpointUrl + "/products";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("total_entries"), apiRestResponse.getBody().getInt(
                "total_entries"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("created_at"),
                apiRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("name"),
                apiRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("products").getJSONObject(0).getDouble("cost_price"),
                apiRestResponse.getBody().getJSONArray("products").getJSONObject(0).getDouble("cost_price"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("code"),
                apiRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("products").getJSONObject(0).getInt("stock_level"),
                apiRestResponse.getBody().getJSONArray("products").getJSONObject(0).getInt("stock_level"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("notes"),
                apiRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("notes"));
                
    }
    
    /**
     * Positive test case for listProducts method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {listProducts} integration test with optional parameters.", dependsOnMethods = {
                    "testCreateMedicalAlertWithMandatoryParameters" })
    public void testListProductsWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listProducts");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listProducts_optional.json");
                
        final String apiEndpoint = apiEndpointUrl + "/products?page=1&per_page=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("products").length(), 1);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("id"),
                apiRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("created_at"),
                apiRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("created_at"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("name"),
                apiRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("products").getJSONObject(0).getDouble("cost_price"),
                apiRestResponse.getBody().getJSONArray("products").getJSONObject(0).getDouble("cost_price"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("code"),
                apiRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("products").getJSONObject(0).getInt("stock_level"),
                apiRestResponse.getBody().getJSONArray("products").getJSONObject(0).getInt("stock_level"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("notes"),
                apiRestResponse.getBody().getJSONArray("products").getJSONObject(0).getString("notes"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("products").length(), 1);
        
    }
    
    /**
     * Negative test case for listProducts method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "cliniko {listProducts} integration test with negative case.")
    public void testListProductsWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listProducts");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_listProducts_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/products?sort=item_bar_code";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        
    }
    
    /**
     * Positive test case for getSettings method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {getSettings} integration test with mandatory parameters.", dependsOnMethods = {
                    "testListProductsWithMandatoryParameters" })
    public void testGetSettingsWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getSettings");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getSettings_mandatory.json");
                
        final String apiEndpoint = apiEndpointUrl + "/settings";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("account").getString("name"), apiRestResponse
                .getBody().getJSONObject("account").getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("account").getString("email_from"), apiRestResponse
                .getBody().getJSONObject("account").getString("email_from"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("account").getString("country"), apiRestResponse
                .getBody().getJSONObject("account").getString("country"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("account").getJSONObject("admin").getString(
                "first_name"), apiRestResponse.getBody().getJSONObject("account").getJSONObject("admin").getString(
                        "first_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("account").getJSONObject("admin").getString(
                "last_name"), apiRestResponse.getBody().getJSONObject("account").getJSONObject("admin").getString(
                        "last_name"));
                        
    }
    
    
    /**
     * Method name: getSettings 
     * Test scenario: Optional
     * Reason to skip: There are no optional parameters to process.
     */
    
    /**
     * Method name: getSettings 
     * Test scenario: Negative
     * Reason to skip: There are no parameters to be tested for the negative case.
     */
    
    /**
     * Positive test case for createMedicalAlert method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {createMedicalAlert} integration test with mandatory parameters.", dependsOnMethods = {
                    "testCreatePatientWithOptionalParameters" })
    public void testCreateMedicalAlertWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createMedicalAlert");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createMedicalAlert_mandatory.json");
                
        final String mandatoryMedicalAlertId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("mandatoryMedicalAlertId", mandatoryMedicalAlertId);
        
        final String apiEndpoint = apiEndpointUrl + "/medical_alerts/" + mandatoryMedicalAlertId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("medicalAlertName"), apiRestResponse.getBody().getString(
                "name"));
        Assert.assertEquals(connectorProperties.getProperty("optionalPatientId"), apiRestResponse.getBody()
                .getJSONObject("patient").getJSONObject("links").getString("self").split("/")[5]);
                
    }
    
    /**
     * Method name: createMedicalAlert
     * Test scenario: Optional 
     * Reason to skip: There are no optional parameters to be tested.
     */
    
    /**
     * Negative test case for createMedicalAlert method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {
            "wso2.esb" }, description = "cliniko {createMedicalAlert} integration test with negative case.", dependsOnMethods = {
                    "testCreatePatientWithOptionalParameters" })
    public void testCreateMedicalAlertWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createMedicalAlert");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createMedicalAlert_negative.json");
                
        final String apiEndpoint = apiEndpointUrl + "/medical_alerts";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                "api_createMedicalAlert_negative.json");
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("errors").getString("name"), apiRestResponse
                .getBody().getJSONObject("errors").getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString(
                "message"));
                
    }
    
}