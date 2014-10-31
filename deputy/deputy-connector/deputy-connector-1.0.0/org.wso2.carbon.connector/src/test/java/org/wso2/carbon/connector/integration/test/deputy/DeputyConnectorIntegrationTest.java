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

package org.wso2.carbon.connector.integration.test.deputy;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

public class DeputyConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private String apiRequestUrl;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("deputy-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Accept", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Authorization", "OAuth " + connectorProperties.getProperty("accessToken"));
        
        apiRequestUrl = connectorProperties.getProperty("apiUrl") + "/api/v1";
    }
    
    /**
     * Positive test case for createEmployee method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "deputy {createEmployee} integration test with mandatory parameters.")
    public void testCreateEmployeeWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createEmployee");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEmployee_mandatory.json");
        
        String employeeId = esbRestResponse.getBody().getString("Id");
        connectorProperties.setProperty("employeeId", employeeId);
        
        String apiEndPoint = apiRequestUrl + "/resource/Employee/" + employeeId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(connectorProperties.getProperty("employeeFirstName"),
                apiRestResponse.getBody().getString("FirstName"));
        Assert.assertEquals(connectorProperties.getProperty("employeeLastName"),
                apiRestResponse.getBody().getString("LastName"));
        Assert.assertEquals(connectorProperties.getProperty("companyId"), apiRestResponse.getBody()
                .getString("Company"));
        
    }
    
    /**
     * Positive test case for createEmployee method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateEmployeeWithMandatoryParameters" }, description = "deputy {createEmployee} integration test with optional parameters.")
    public void testCreateEmployeeWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createEmployee");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEmployee_optional.json");
        
        String employeeIdOptional = esbRestResponse.getBody().getString("Id");
        connectorProperties.setProperty("employeeIdOptional", employeeIdOptional);
        
        String apiEndPoint = apiRequestUrl + "/resource/Employee/" + employeeIdOptional;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(connectorProperties.getProperty("employeeEmail"),
                esbRestResponse.getBody().getString("Email"));
        Assert.assertEquals(connectorProperties.getProperty("employeeDateOfBirth"),
                String.valueOf(apiRestResponse.getBody().getString("DateOfBirth").split("T")[0]));
    }
    
    /**
     * Negative test case for createEmployee method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateEmployeeWithOptionalParameters" }, description = "deputy {createEmployee} integration test with negative case.")
    public void testCreateEmployeeWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createEmployee");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEmployee_negative.json");
        
        String apiEndPoint = apiRequestUrl + "/addemployee";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createEmployee_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
                .getBody().getJSONObject("error").getString("message"));
    }
    
    /**
     * Positive test case for updateEmployee method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateEmployeeWithNegativeCase" }, description = "deputy {updateEmployee} integration test with mandatory parameters.")
    public void testUpdateEmployeeWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateEmployee");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateEmployee_mandatory.json");
        
        String apiEndPoint = apiRequestUrl + "/resource/Employee/" + connectorProperties.getProperty("employeeId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(connectorProperties.getProperty("employeeId"), apiRestResponse.getBody().getString("Id"));
        Assert.assertEquals(connectorProperties.getProperty("companyId"), apiRestResponse.getBody()
                .getString("Company"));
        Assert.assertEquals(connectorProperties.getProperty("employeeFirstName"),
                apiRestResponse.getBody().getString("FirstName"));
        
    }
    
    /**
     * Positive test case for updateEmployee method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateEmployeeWithMandatoryParameters" }, description = "deputy {updateEmployee} integration test with optional parameters.")
    public void testUpdateEmployeeWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateEmployee");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateEmployee_optional.json");
        
        String apiEndPoint =
                apiRequestUrl + "/resource/Employee/" + connectorProperties.getProperty("employeeIdOptional");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(connectorProperties.getProperty("companyId"), apiRestResponse.getBody()
                .getString("Company"));
        Assert.assertEquals(connectorProperties.getProperty("updateEmployeeFirstName"), apiRestResponse.getBody()
                .getString("FirstName"));
        
    }
    
    /**
     * Negative test case for updateEmployee method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateEmployeeWithOptionalParameters" }, description = "deputy {updateEmployee} integration test with negative case.")
    public void testUpdateEmployeeWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateEmployee");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateEmployee_negative.json");
        
        String apiEndPoint = apiRequestUrl + "/supervise/employee/" + connectorProperties.getProperty("employeeId");
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateEmployee_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
                .getBody().getJSONObject("error").getString("message"));
    }
    
    /**
     * Positive test case for createRoster method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateEmployeeWithNegativeCase" }, description = "deputy {createRoster} integration test with mandatory parameters.")
    public void testCreateRosterWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createRoster");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRoster_mandatory.json");
        
        final String rosterId = esbRestResponse.getBody().getString("Id");
        final String apiEndPoint = apiRequestUrl + "/resource/Roster/" + rosterId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(connectorProperties.getProperty("employeeId"),
                apiRestResponse.getBody().getString("Employee"));
        Assert.assertEquals(connectorProperties.getProperty("intOpunitId"),
                apiRestResponse.getBody().getString("OperationalUnit"));
    }
    
    /**
     * Positive test case for createRoster method with Optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateRosterWithMandatoryParameters" }, description = "deputy {createRoster} integration test with optional parameters.")
    public void testCreateRosterWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createRoster");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRoster_optional.json");
        
        final String rosterId = esbRestResponse.getBody().getString("Id");
        final String apiEndPoint = apiRequestUrl + "/resource/Roster/" + rosterId;
        
        connectorProperties.setProperty("rosterId", rosterId);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(connectorProperties.getProperty("employeeIdOptional"),
                apiRestResponse.getBody().getString("Employee"));
        
        if (connectorProperties.getProperty("blnPublish").equals("1")) {
            Assert.assertEquals(apiRestResponse.getBody().getString("Published"), "true");
        } else {
            Assert.assertEquals(apiRestResponse.getBody().getString("Published"), "false");
        }
    }
    
    /**
     * Negative test case for createRoster method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateRosterWithOptionalParameters" }, description = "deputy {createRoster} integration test with negative case.")
    public void testCreateRostereWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createRoster");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRoster_negative.json");
        
        String apiEndPoint = apiRequestUrl + "/supervise/roster";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createRoster_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
                .getBody().getJSONObject("error").getString("message"));
    }
    
    /**
     * Positive test case for updateRoster method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateRostereWithNegativeCase" }, description = "deputy {updateRoster} integration test with mandatory parameters.")
    public void testUpdateRosterWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateRoster");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateRoster_mandatory.json");
        
        final String rosterId = esbRestResponse.getBody().getString("Id");
        final String apiEndPoint = apiRequestUrl + "/resource/Roster/" + rosterId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(connectorProperties.getProperty("rosterId"), apiRestResponse.getBody().getString("Id"));
        Assert.assertEquals(connectorProperties.getProperty("intStartTimestamp"),
                apiRestResponse.getBody().getString("StartTime"));
    }
    
    /**
     * Positive test case for updateRoster method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateRosterWithMandatoryParameters" }, description = "deputy {updateRoster} integration test with Optional parameters.")
    public void testUpdateRosterWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateRoster");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateRoster_optional.json");
        
        final String rosterId = esbRestResponse.getBody().getString("Id");
        final String apiEndPoint = apiRequestUrl + "/resource/Roster/" + rosterId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(connectorProperties.getProperty("rosterId"), apiRestResponse.getBody().getString("Id"));
        Assert.assertEquals(connectorProperties.getProperty("intOpunitId"),
                apiRestResponse.getBody().getString("OperationalUnit"));
    }
    
    /**
     * Negative test case for updateRoster method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateRosterWithOptionalParameters" }, description = "deputy {updateRoster} integration test with negative case.")
    public void testUpdateRostereWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateRoster");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateRoster_negative.json");
        
        String apiEndPoint = apiRequestUrl + "/supervise/roster";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateRoster_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
                .getBody().getJSONObject("error").getString("message"));
    }
    
    /**
     * Positive test case for createLeave method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateRostereWithNegativeCase" }, description = "deputy {createLeave} integration test with mandatory parameters.")
    public void testCreateLeaveWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createLeave");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createLeave_mandatory.json");
        
        final String leaveId = esbRestResponse.getBody().getString("Id");
        final String apiEndPoint = apiRequestUrl + "/resource/Leave/" + leaveId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(connectorProperties.getProperty("employeeId"),
                apiRestResponse.getBody().getString("Employee"));
        Assert.assertEquals(connectorProperties.getProperty("leaveStatus"),
                apiRestResponse.getBody().getString("Status"));
    }
    
    /**
     * Positive test case for createLeave method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateLeaveWithMandatoryParameters" }, description = "deputy {createLeave} integration test with optional parameters.")
    public void testCreateLeaveWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createLeave");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createLeave_optional.json");
        
        final String leaveId = esbRestResponse.getBody().getString("Id");
        final String apiEndPoint = apiRequestUrl + "/resource/Leave/" + leaveId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(connectorProperties.getProperty("employeeIdOptional"),
                apiRestResponse.getBody().getString("Employee"));
        Assert.assertEquals(connectorProperties.getProperty("leaveApprovalComment"), apiRestResponse.getBody()
                .getString("ApprovalComment"));
    }
    
    /**
     * Negative test case for createLeave method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateLeaveWithOptionalParameters" }, description = "deputy {createLeave} integration test with negative case.")
    public void testCreaetLeaveWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createLeave");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createLeave_negative.json");
        
        String apiEndPoint = apiRequestUrl + "/supervise/leave";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createLeave_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
                .getBody().getJSONObject("error").getString("message"));
    }
    
    /**
     * Positive test case for getLeave method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreaetLeaveWithNegativeCase" }, description = "deputy {getLeave} integration test with mandatory parameters.")
    public void testGetLeaveWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getLeave");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getLeave_mandatory.json");
        JSONArray esbOutArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        String apiEndPoint = apiRequestUrl + "/supervise/leave/" + connectorProperties.getProperty("employeeId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiOutArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbOutArray.getJSONObject(0).getString("Employee"),
                apiOutArray.getJSONObject(0).getString("Employee"));
        Assert.assertEquals(esbOutArray.getJSONObject(0).getString("Id"), apiOutArray.getJSONObject(0).getString("Id"));
    }
    
    /**
     * Negative test case for getLeave method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetLeaveWithMandatoryParameters" }, description = "deputy {getLeave} integration test with negative case.")
    public void testGetLeaveWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getLeave");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getLeave_negative.json");
        
        String apiEndPoint = apiRequestUrl + "/supervise/leave/-2";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
                .getBody().getJSONObject("error").getString("message"));
    }
    
    /**
     * Positive test case for getObject method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetLeaveWithNegativeCase" }, description = "deputy {getObject} integration test with mandatory parameters.")
    public void testGetObjectWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getObject");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getObject_mandatory.json");
        JSONArray esbOutArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndPoint = apiRequestUrl + "/resource/Employee";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiOutArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbOutArray.length(), apiOutArray.length());
        Assert.assertEquals(esbOutArray.getJSONObject(0).getString("Company"),
                apiOutArray.getJSONObject(0).getString("Company"));
        Assert.assertEquals(esbOutArray.getJSONObject(0).getString("FirstName"), apiOutArray.getJSONObject(0)
                .getString("FirstName"));
    }
    
    /**
     * Positive test case for getObject method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetObjectWithMandatoryParameters" }, description = "deputy {getObject} integration test with optional parameters.")
    public void testGetObjectWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getObject");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getObject_optional.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/resource/Employee/" + connectorProperties.getProperty("employeeId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("Company"),
                apiRestResponse.getBody().getString("Company"));
        Assert.assertEquals(esbRestResponse.getBody().getString("FirstName"),
                apiRestResponse.getBody().getString("FirstName"));
    }
    
    /**
     * Negative test case for getObject method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetObjectWithOptionalParameters" }, description = "deputy {getObject} integration test with negative case.")
    public void testGetObjectWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getObject");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getObject_negative.json");
        
        String apiEndPoint = apiRequestUrl + "/resource/InvalidObject/" + connectorProperties.getProperty("employeeId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 401);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 401);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
                .getBody().getJSONObject("error").getString("message"));
    }
    
    /**
     * Positive test case for queryObject method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetObjectWithNegativeCase" }, description = "deputy {queryObject} integration test with mandatory parameters.")
    public void testQueryObjectWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:queryObject");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_queryObject_mandatory.json");
        JSONArray esbOutArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndPoint =
                apiRequestUrl + "/resource/Leave/QUERY";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_queryObject_mandatory.json");
        JSONArray apiOutArray = new JSONArray(apiRestResponse.getBody().getString("output"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbOutArray.getJSONObject(0).getString("DateStart"),
        		apiOutArray.getJSONObject(0).getString("DateStart"));
        Assert.assertEquals(esbOutArray.getJSONObject(0).getString("Id"),
        		apiOutArray.getJSONObject(0).getString("Id"));
    }
    
    /**
     * Positive test case for queryObject method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testQueryObjectWithMandatoryParameters" }, description = "deputy {queryObject} integration test with optional parameters.")
    public void testQueryObjectWithOptionalParameters() throws IOException, JSONException, ParseException {
    
        esbRequestHeadersMap.put("Action", "urn:queryObject");
       
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date convertedDate = (Date) formatter.parse(connectorProperties.getProperty("leaveStartDate"));
        Calendar c = Calendar.getInstance();
	    c.setTime(convertedDate);
    	connectorProperties.setProperty("month", String.valueOf(c.get(Calendar.MONTH) + 1));
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_queryObject_optional.json");
        JSONArray esbOutArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndPoint =
                apiRequestUrl + "/resource/Leave/QUERY";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_queryObject_optional.json");
        JSONArray apiOutArray = new JSONArray(apiRestResponse.getBody().getString("output"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbOutArray.getJSONObject(0).getString("DateStart"),
        		apiOutArray.getJSONObject(0).getString("DateStart"));
        Assert.assertEquals(esbOutArray.getJSONObject(0).getString("Id"),
        		apiOutArray.getJSONObject(0).getString("Id"));
    }
    
    /**
     * Negative test case for queryObject method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testQueryObjectWithOptionalParameters" }, description = "deputy {queryObject} integration test with negative case.")
    public void testQueryObjectWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:queryObject");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_queryObject_negative.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/resource/Leave/QUERY";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_queryObject_negative.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"),
        		apiRestResponse
                .getBody().getJSONObject("error").getString("message"));
    }
    
    /**
     * Positive test case for updateObject method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testQueryObjectWithNegativeCase" }, description = "deputy {updateObject} integration test with optional parameters.")
    public void testUpdateObjectWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateObject");
        connectorProperties.setProperty("employeeGender", "1");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateObject_optional.json");
        
        final String apiEndPoint =
                apiRequestUrl + "/resource/Employee/" + connectorProperties.getProperty("employeeId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(connectorProperties.getProperty("employeeFirstName"),
                apiRestResponse.getBody().getString("OtherName"));
        Assert.assertEquals(connectorProperties.getProperty("employeeGender"),
                apiRestResponse.getBody().getString("Gender"));
    }
    
    /**
     * Negative test case for updateObject method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateObjectWithOptionalParameters" }, description = "deputy {updateObject} integration test with negative case.")
    public void testUpdateObjectNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateObject");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateObject_negative.json");
        
        final String apiEndPoint = apiRequestUrl + "/resource/Employee/INVALID";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateObject_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getString("message"), apiRestResponse
                .getBody().getJSONObject("error").getString("message"));
    }
    
}
