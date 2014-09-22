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

package org.wso2.carbon.connector.integration.test.peoplehr;

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

public class PeoplehrConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("peoplehr-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
    }
    
    /**
     * Positive test case for createEmployee method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "peoplehr {createEmployee} integration test with mandatory parameters.")
    public void testCreateEmployeeWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createEmployee");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEmployee_mandatory.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Employee";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createEmployee_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"), "false");
        Assert.assertEquals(apiRestResponse.getBody().getString("isError"), "false");
        Assert.assertEquals(connectorProperties.getProperty("firstName"),
                apiRestResponse.getBody().getJSONObject("Result").getJSONObject("FirstName").getString("DisplayValue"));
        Assert.assertEquals(connectorProperties.getProperty("lastName"),
                apiRestResponse.getBody().getJSONObject("Result").getJSONObject("LastName").getString("DisplayValue"));
        Assert.assertEquals(connectorProperties.getProperty("gender"), apiRestResponse.getBody()
                .getJSONObject("Result").getJSONObject("Gender").getString("DisplayValue"));
        
    }
    
    /**
     * Positive test case for createEmployee method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateEmployeeWithMandatoryParameters" }, description = "peoplehr {createEmployee} integration test with optional parameters.")
    public void testCreateEmployeeWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createEmployee");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEmployee_optional.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Employee";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createEmployee_optional.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"), "false");
        Assert.assertEquals(apiRestResponse.getBody().getString("isError"), "false");
        Assert.assertEquals(connectorProperties.getProperty("empEmail"),
                apiRestResponse.getBody().getJSONObject("Result").getJSONObject("EmailId").getString("DisplayValue"));
        Assert.assertEquals(connectorProperties.getProperty("empDateOfBirth"),
                apiRestResponse.getBody().getJSONObject("Result").getJSONObject("DateOfBirth")
                        .getString("DisplayValue"));
        Assert.assertEquals(connectorProperties.getProperty("location"),
                apiRestResponse.getBody().getJSONObject("Result").getJSONObject("Location").getString("DisplayValue"));
        
    }
    
    /**
     * Negative test case for createEmployee method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateEmployeeWithOptionalParameters" }, description = "peoplehr {createEmployee} integration test with negative case.")
    public void testCreateEmployeeWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createEmployee");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEmployee_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Employee";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createEmployee_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"),
                apiRestResponse.getBody().getString("isError"));
        Assert.assertEquals(esbRestResponse.getBody().getString("Status"), apiRestResponse.getBody()
                .getString("Status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("Message"),
                apiRestResponse.getBody().getString("Message"));
        
    }
    
    /**
     * Positive test case for getEmployee method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateEmployeeWithNegativeCase" }, description = "peoplehr {getEmployee} integration test with mandatory parameters.")
    public void testGetEmployeeWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getEmployee");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEmployee_mandatory.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Employee";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getEmployee_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"), "false");
        Assert.assertEquals(apiRestResponse.getBody().getString("isError"), "false");
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONObject("Result").getJSONObject("FirstName").getString("DisplayValue"),
                apiRestResponse.getBody().getJSONObject("Result").getJSONObject("FirstName").getString("DisplayValue"));
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONObject("Result").getJSONObject("LastName").getString("DisplayValue"),
                apiRestResponse.getBody().getJSONObject("Result").getJSONObject("LastName").getString("DisplayValue"));
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONObject("Result").getJSONObject("Gender").getString("DisplayValue"),
                apiRestResponse.getBody().getJSONObject("Result").getJSONObject("Gender").getString("DisplayValue"));
        
    }
    
    /**
     * Negative test case for getEmployee method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetEmployeeWithMandatoryParameters" }, description = "peoplehr {getEmployee} integration test with negative case.")
    public void testGetEmployeeWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getEmployee");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEmployee_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Employee";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getEmployee_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"),
                apiRestResponse.getBody().getString("isError"));
        Assert.assertEquals(esbRestResponse.getBody().getString("Status"), apiRestResponse.getBody()
                .getString("Status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("Message"),
                apiRestResponse.getBody().getString("Message"));
    }
    
    /**
     * Positive test case for updateEmployee method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetEmployeeWithNegativeCase" }, description = "peoplehr {updateEmployee} integration test with optional parameters.")
    public void testUpdateEmployeeWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateEmployee");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateEmployee_optional.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Employee";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateEmployee_optional.json");
        
        Assert.assertFalse(Boolean.parseBoolean(esbRestResponse.getBody().getString("isError")));
        Assert.assertEquals(connectorProperties.getProperty("firstNameUpdated"), apiRestResponse.getBody()
                .getJSONObject("Result").getJSONObject("FirstName").getString("DisplayValue"));
        Assert.assertEquals(connectorProperties.getProperty("empEmailUpdated"), apiRestResponse.getBody()
                .getJSONObject("Result").getJSONObject("EmailId").getString("DisplayValue"));
    }
    
    /**
     * Negative test case for updateEmployee method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateEmployeeWithOptionalParameters" }, description = "peoplehr {updateEmployee} integration test with negative case.")
    public void testUpdateEmployeeNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateEmployee");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateEmployee_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Employee";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateEmployee_negative.json");
        
        Assert.assertTrue(Boolean.parseBoolean(esbRestResponse.getBody().getString("isError")));
        Assert.assertTrue(Boolean.parseBoolean(apiRestResponse.getBody().getString("isError")));
        Assert.assertEquals(apiRestResponse.getBody().getString("Message"),
                esbRestResponse.getBody().getString("Message"));
    }
    
    /**
     * Positive test case for createTimesheet method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateEmployeeNegativeCase" }, description = "peoplehr {createTimesheet} integration test with mandatory parameters.")
    public void testCreateTimesheetWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTimesheet");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTimesheet_mandatory.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Timesheet";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createTimesheet_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"), "false");
        Assert.assertEquals(connectorProperties.getProperty("timesheetDate"),
                apiRestResponse.getBody().getJSONArray("Result").getJSONObject(0).getString("TimesheetDate"));
        Assert.assertEquals(connectorProperties.getProperty("timeIn1").toString() + ":00", apiRestResponse.getBody()
                .getJSONArray("Result").getJSONObject(0).getString("TimeIn1"));
        
    }
    
    /**
     * Positive test case for createTimesheet method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTimesheetWithMandatoryParameters" }, description = "peoplehr {createTimesheet} integration test with optional parameters.")
    public void testCreateTimesheetWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTimesheet");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTimesheet_optional.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Timesheet";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createTimesheet_optional.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"), "false");
        Assert.assertEquals(connectorProperties.getProperty("timesheetDateOpt"), apiRestResponse.getBody()
                .getJSONArray("Result").getJSONObject(0).getString("TimesheetDate"));
        Assert.assertEquals(connectorProperties.getProperty("timeIn1").toString() + ":00", apiRestResponse.getBody()
                .getJSONArray("Result").getJSONObject(0).getString("TimeIn1"));
    }
    
    /**
     * Negative test case for createTimesheet method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTimesheetWithOptionalParameters" }, description = "peoplehr {createTimesheet} integration test with negative case.")
    public void testCreateTimesheetWithNegativeParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createTimesheet");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTimesheet_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Timesheet";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createTimesheet_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"), "true");
        Assert.assertEquals(esbRestResponse.getBody().getString("Message"),
                apiRestResponse.getBody().getString("Message"));
    }
    
    /**
     * Positive test case for getTimesheet method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTimesheetWithNegativeParameters" }, description = "peoplehr {getTimesheet} integration test with mandatory parameters.")
    public void testGetTimesheetWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getTimesheet");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTimesheet_mandatory.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Timesheet";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getTimesheet_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"), "false");
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Result").length(), apiRestResponse.getBody()
                .getJSONArray("Result").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Result").getJSONObject(0)
                .getString("TimesheetDate"), apiRestResponse.getBody().getJSONArray("Result").getJSONObject(0)
                .getString("TimesheetDate"));
    }
    
    /**
     * Positive test case for getTimesheet method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetTimesheetWithMandatoryParameters" }, description = "peoplehr {getTimesheet} integration test with optional parameters.")
    public void testGetTimesheetWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getTimesheet");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTimesheet_optional.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Timesheet";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getTimesheet_optional.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"), "false");
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Result").length(), apiRestResponse.getBody()
                .getJSONArray("Result").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Result").getJSONObject(0)
                .getString("TimesheetDate"), apiRestResponse.getBody().getJSONArray("Result").getJSONObject(0)
                .getString("TimesheetDate"));
    }
    
    /**
     * Negative test case for getTimesheet method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetTimesheetWithOptionalParameters" }, description = "peoplehr {getTimesheet} integration negative case.")
    public void testGetTimesheetNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getTimesheet");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTimesheet_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Timesheet";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getTimesheet_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"), "true");
        Assert.assertEquals(apiRestResponse.getBody().getString("isError"), "true");
        Assert.assertEquals(esbRestResponse.getBody().getString("Message"),
                apiRestResponse.getBody().getString("Message"));
    }
    
    /**
     * Positive test case for updateTimesheet method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetTimesheetNegativeCase" }, description = "peoplehr {updateTimesheet} integration test with mandatory parameters.")
    public void testUpdateTimesheetWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateTimesheet");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTimesheet_mandatory.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Timesheet";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateTimesheet_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"), "false");
        Assert.assertEquals(connectorProperties.getProperty("timeIn1update").toString() + ":00", apiRestResponse
                .getBody().getJSONArray("Result").getJSONObject(0).getString("TimeIn1"));
        Assert.assertEquals(connectorProperties.getProperty("timesheetDate"),
                apiRestResponse.getBody().getJSONArray("Result").getJSONObject(0).getString("TimesheetDate"));
    }
    
    /**
     * Positive test case for updateTimesheet method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateTimesheetWithMandatoryParameters" }, description = "peoplehr {updateTimesheet} integration test with optional parameters.")
    public void testUpdateTimesheetWithOptionalyParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateTimesheet");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTimesheet_optional.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Timesheet";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateTimesheet_optional.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"), "false");
        Assert.assertEquals(connectorProperties.getProperty("timeIn1update").toString() + ":00", apiRestResponse
                .getBody().getJSONArray("Result").getJSONObject(0).getString("TimeIn1"));
        Assert.assertEquals(connectorProperties.getProperty("timesheetDate"),
                apiRestResponse.getBody().getJSONArray("Result").getJSONObject(0).getString("TimesheetDate"));
        Assert.assertEquals(connectorProperties.getProperty("comments"),
                apiRestResponse.getBody().getJSONArray("Result").getJSONObject(0).getString("Comments"));
    }
    
    /**
     * Negative test case for updateTimesheet method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateTimesheetWithOptionalyParameters" }, description = "peoplehr {updateTimesheet} integration negative case.")
    public void testUpdateTimesheetNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateTimesheet");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTimesheet_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Timesheet";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateTimesheet_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"), "true");
        Assert.assertEquals(apiRestResponse.getBody().getString("isError"), "true");
        Assert.assertEquals(esbRestResponse.getBody().getString("Message"),
                apiRestResponse.getBody().getString("Message"));
    }
    
    /**
     * Positive test case for createProjectTimesheet method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateTimesheetNegativeCase" }, description = "peoplehr {createProjectTimesheet} integration test with mandatory parameters.")
    public void testCreateProjectTimesheetWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createProjectTimesheet");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProjectTimesheet_mandatory.json");
        
        String transactionIdMandatory = esbRestResponse.getBody().getString("Result").split("=")[1];
        
        connectorProperties.put("transactionIdMandatory", transactionIdMandatory);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/ProjectTimesheet";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                        "api_createProjectTimesheet_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"), "false");
        Assert.assertEquals(apiRestResponse.getBody().getString("isError"), "false");
        
        Assert.assertEquals(connectorProperties.getProperty("projectTimesheetDate"), apiRestResponse.getBody()
                .getJSONObject("Result").getString("ProjectTimesheetDate"));
        Assert.assertEquals(connectorProperties.getProperty("timesheetProject"), apiRestResponse.getBody()
                .getJSONObject("Result").getString("TimesheetProject"));
    }
    
    /**
     * Positive test case for createProjectTimesheet method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectTimesheetWithMandatoryParameters" }, description = "peoplehr {createProjectTimesheet} integration test with optional parameters.")
    public void testCreateProjectTimesheetWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createProjectTimesheet");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProjectTimesheet_optional.json");
        
        String transactionIdOptional = esbRestResponse.getBody().getString("Result").split("=")[1];
        connectorProperties.put("transactionIdOptional", transactionIdOptional);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/ProjectTimesheet";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                        "api_createProjectTimesheet_optional.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"), "false");
        Assert.assertEquals(apiRestResponse.getBody().getString("isError"), "false");
        
        Assert.assertEquals(connectorProperties.getProperty("projectTimeSheetquantity"), apiRestResponse.getBody()
                .getJSONObject("Result").getString("Quantity"));
        Assert.assertEquals(connectorProperties.getProperty("projectTimeSheetTask"), apiRestResponse.getBody()
                .getJSONObject("Result").getString("TimesheetTask"));
        Assert.assertEquals(connectorProperties.getProperty("projectTimeSheetNotes"), apiRestResponse.getBody()
                .getJSONObject("Result").getString("Notes"));
    }
    
    /**
     * Negative test case for createProjectTimesheet method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectTimesheetWithOptionalParameters" }, description = "peoplehr {createProjectTimesheet} integration test with negative case.")
    public void testCreateProjectTimesheetWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createProjectTimesheet");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProjectTimesheet_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/ProjectTimesheet";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                        "api_createProjectTimesheet_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"),
                apiRestResponse.getBody().getString("isError"));
        Assert.assertEquals(esbRestResponse.getBody().getString("Status"), apiRestResponse.getBody()
                .getString("Status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("Message"),
                apiRestResponse.getBody().getString("Message"));
    }
    
    /**
     * Positive test case for getProjectTimesheet method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectTimesheetWithNegativeCase" }, description = "peoplehr {getProjectTimesheet} integration test with mandatory parameters.")
    public void testGetProjectTimesheetWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getProjectTimesheet");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProjectTimesheet_mandatory.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/ProjectTimesheet";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getProjectTimesheet_mandatory.json");
        
        Assert.assertFalse(Boolean.parseBoolean(esbRestResponse.getBody().getString("isError")));
        Assert.assertEquals(esbRestResponse.getBody().get("Message"), apiRestResponse.getBody().get("Message"));
        Assert.assertEquals(esbRestResponse.getBody().get("Status"), apiRestResponse.getBody().get("Status"));
    }
    
    /**
     * Positive test case for getProjectTimesheet method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetProjectTimesheetWithMandatoryParameters" }, description = "peoplehr {getProjectTimesheet} integration test with optional parameters.")
    public void testGetProjectTimesheetWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getProjectTimesheet");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProjectTimesheet_optional.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/ProjectTimesheet";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getProjectTimesheet_optional.json");
        
        Assert.assertFalse(Boolean.parseBoolean(esbRestResponse.getBody().getString("isError")));
        Assert.assertEquals(esbRestResponse.getBody().get("Message"), apiRestResponse.getBody().get("Message"));
        Assert.assertEquals(esbRestResponse.getBody().get("Status"), apiRestResponse.getBody().get("Status"));
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONArray("Result").getJSONObject(0).get("ProjectTimesheetDate"),
                connectorProperties.get("timesheetStartDate"));
    }
    
    /**
     * Negative test case for getProjectTimesheet method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetProjectTimesheetWithOptionalParameters" }, description = "peoplehr {getProjectTimesheet} integration test with negative case.")
    public void testGetProjectTimesheetWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getProjectTimesheet");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProjectTimesheet_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/ProjectTimesheet";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getProjectTimesheet_negative.json");
        
        Assert.assertTrue(Boolean.parseBoolean(esbRestResponse.getBody().getString("isError")));
        Assert.assertEquals(esbRestResponse.getBody().get("Message"), apiRestResponse.getBody().get("Message"));
        Assert.assertEquals(esbRestResponse.getBody().get("Status"), apiRestResponse.getBody().get("Status"));
    }
    
    /**
     * Positive test case for updateProjectTimesheet method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetProjectTimesheetWithNegativeCase" }, description = "peoplehr {updateProjectTimesheet} integration test with mandatory parameters.")
    public void testUpdateProjectTimesheetWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateProjectTimesheet");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateProjectTimesheet_mandatory.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/ProjectTimesheet";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                        "api_updateProjectTimesheet_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"), "false");
        Assert.assertEquals(apiRestResponse.getBody().getString("isError"), "false");
        
        Assert.assertEquals(connectorProperties.getProperty("updatedTimesheetProject"), apiRestResponse.getBody()
                .getJSONObject("Result").getString("TimesheetProject"));
        
    }
    
    /**
     * Positive test case for updateProjectTimesheet method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateProjectTimesheetWithMandatoryParameters" }, description = "peoplehr {updateProjectTimesheet} integration test with optional parameters.")
    public void testUpdateProjectTimesheetWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateProjectTimesheet");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateProjectTimesheet_optional.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/ProjectTimesheet";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                        "api_updateProjectTimesheet_optional.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"), "false");
        Assert.assertEquals(apiRestResponse.getBody().getString("isError"), "false");
        
        Assert.assertEquals(connectorProperties.getProperty("updatedProjectTimesheetQuantity"), apiRestResponse
                .getBody().getJSONObject("Result").getString("Quantity"));
        Assert.assertEquals(connectorProperties.getProperty("updatedProjectTimesheetTask"), apiRestResponse.getBody()
                .getJSONObject("Result").getString("TimesheetTask"));
        Assert.assertEquals(connectorProperties.getProperty("updatedprojectTimeSheetNotes"), apiRestResponse.getBody()
                .getJSONObject("Result").getString("Notes"));
    }
    
    /**
     * Negative test case for updateProjectTimesheet method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateProjectTimesheetWithOptionalParameters" }, description = "peoplehr {updateProjectTimesheet} integration test with negative case.")
    public void testUpdateProjectTimesheetWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateProjectTimesheet");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateProjectTimesheet_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/ProjectTimesheet";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                        "api_updateProjectTimesheet_negative.json");
        
        System.out.println("esbRestResponse>>>>>" + esbRestResponse.getBody());
        System.out.println("apiRestResponse>>>>>" + apiRestResponse.getBody());
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"),
                apiRestResponse.getBody().getString("isError"));
        Assert.assertEquals(esbRestResponse.getBody().getString("Status"), apiRestResponse.getBody()
                .getString("Status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("Message"),
                apiRestResponse.getBody().getString("Message"));
    }
    
    /**
     * Positive test case for createSalary method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateProjectTimesheetWithNegativeCase" }, description = "peoplehr {createSalary} integration test with mandatory parameters.")
    public void testCreateSalaryWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createSalary");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createSalary_mandatory.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Salary";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createSalary_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"), "false");
        Assert.assertEquals(apiRestResponse.getBody().getString("isError"), "false");
        
        Assert.assertEquals(connectorProperties.getProperty("effectiveFromDate"), apiRestResponse.getBody()
                .getJSONArray("Result").getJSONObject(0).getString("EffectiveFrom"));
        Assert.assertEquals(connectorProperties.getProperty("salaryType"),
                apiRestResponse.getBody().getJSONArray("Result").getJSONObject(0).getString("SalaryType"));
        Assert.assertEquals(connectorProperties.getProperty("salaryAmount"),
                apiRestResponse.getBody().getJSONArray("Result").getJSONObject(0).getString("SalaryAmount"));
    }
    
    /**
     * Positive test case for createSalary method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateSalaryWithMandatoryParameters" }, description = "peoplehr {createSalary} integration test with optional parameters.")
    public void testCreateSalaryWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createSalary");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createSalary_optional.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Salary";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createSalary_optional.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"), "false");
        Assert.assertEquals(apiRestResponse.getBody().getString("isError"), "false");
        
        Assert.assertEquals(connectorProperties.getProperty("effectiveFromDate"), apiRestResponse.getBody()
                .getJSONArray("Result").getJSONObject(0).getString("EffectiveFrom"));
        Assert.assertEquals(connectorProperties.getProperty("salaryType"),
                apiRestResponse.getBody().getJSONArray("Result").getJSONObject(0).getString("SalaryType"));
        Assert.assertEquals(connectorProperties.getProperty("salaryAmount"),
                apiRestResponse.getBody().getJSONArray("Result").getJSONObject(0).getString("SalaryAmount"));
        Assert.assertEquals(connectorProperties.getProperty("createSalaryComments"), apiRestResponse.getBody()
                .getJSONArray("Result").getJSONObject(0).getString("Comments"));
    }
    
    /**
     * Negative test case for createSalary method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateSalaryWithOptionalParameters" }, description = "peoplehr {createSalary} integration test with negative case.")
    public void testCreateSalaryWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createSalary");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createSalary_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Salary";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createSalary_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("isError"),
                apiRestResponse.getBody().getString("isError"));
        Assert.assertEquals(esbRestResponse.getBody().getString("Status"), apiRestResponse.getBody()
                .getString("Status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("Message"),
                apiRestResponse.getBody().getString("Message"));
    }
    
    /**
     * Positive test case for getSalary method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateSalaryWithNegativeCase" }, description = "peoplehr {getSalary} integration test with mandatory parameters.")
    public void testGetSalaryWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getSalary");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSalary_mandatory.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Salary";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getSalary_mandatory.json");
        
        Assert.assertFalse(Boolean.parseBoolean(esbRestResponse.getBody().getString("isError")));
        Assert.assertFalse(Boolean.parseBoolean(apiRestResponse.getBody().getString("isError")));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Result").getJSONObject(0).getString("SalaryType"),
                apiRestResponse.getBody().getJSONArray("Result").getJSONObject(0).getString("SalaryType"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Result").getJSONObject(0)
                .getString("EffectiveFrom"), apiRestResponse.getBody().getJSONArray("Result").getJSONObject(0)
                .getString("EffectiveFrom"));
    }
    
    /**
     * Negative test case for getSalary method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetSalaryWithMandatoryParameters" }, description = "peoplehr {getSalary} integration test with negative case.")
    public void testGetSalaryNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getSalary");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSalary_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Salary";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getSalary_negative.json");
        
        Assert.assertTrue(Boolean.parseBoolean(esbRestResponse.getBody().getString("isError")));
        Assert.assertTrue(Boolean.parseBoolean(apiRestResponse.getBody().getString("isError")));
        Assert.assertEquals(apiRestResponse.getBody().getString("Message"),
                esbRestResponse.getBody().getString("Message"));
    }
    
    /**
     * Positive test case for createAbsenceRecord method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetSalaryNegativeCase" }, description = "peoplehr {createAbsenceRecord} integration test with mandatory parameters.")
    public void testCreateAbsenceRecordWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAbsenceRecord");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAbsenceRecord_mandatory.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Absence";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createAbsenceRecord_mandatory.json");
        
        Assert.assertFalse(Boolean.parseBoolean(esbRestResponse.getBody().getString("isError")));
        Assert.assertEquals(connectorProperties.getProperty("leaveStartDate"),
                apiRestResponse.getBody().getJSONArray("Result").getJSONObject(0).getString("StartDate"));
        Assert.assertEquals(connectorProperties.getProperty("leaveEndDate"),
                apiRestResponse.getBody().getJSONArray("Result").getJSONObject(0).getString("EndDate"));
    }
    
    /**
     * Positive test case for createAbsenceRecord method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAbsenceRecordWithMandatoryParameters" }, description = "peoplehr {createAbsenceRecord} integration test with optional parameters.")
    public void testCreateAbsenceRecordWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAbsenceRecord");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAbsenceRecord_optional.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Absence";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createAbsenceRecord_optional.json");
        
        Assert.assertFalse(Boolean.parseBoolean(esbRestResponse.getBody().getString("isError")));
        Assert.assertEquals(connectorProperties.getProperty("leavePaidStatus"),
                apiRestResponse.getBody().getJSONArray("Result").getJSONObject(0).getString("AbsencePaidStatus"));
        Assert.assertEquals(connectorProperties.getProperty("leaveComment"),
                apiRestResponse.getBody().getJSONArray("Result").getJSONObject(0).getJSONArray("Comments")
                        .getJSONObject(0).getString("Comments"));
    }
    
    /**
     * Negative test case for createAbsenceRecord method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAbsenceRecordWithOptionalParameters" }, description = "peoplehr {createAbsenceRecord} integration test with negative case.")
    public void testCreateAbsenceRecordNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAbsenceRecord");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAbsenceRecord_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Absence";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createAbsenceRecord_negative.json");
        
        Assert.assertTrue(Boolean.parseBoolean(esbRestResponse.getBody().getString("isError")));
        Assert.assertTrue(Boolean.parseBoolean(apiRestResponse.getBody().getString("isError")));
        Assert.assertEquals(apiRestResponse.getBody().getString("Message"),
                esbRestResponse.getBody().getString("Message"));
    }
    
    /**
     * Positive test case for getAbsenceRecord method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAbsenceRecordNegativeCase" }, description = "peoplehr {getAbsenceRecord} integration test with mandatory parameters.")
    public void testGetAbsenceRecordWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAbsenceRecord");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAbsenceRecord_mandatory.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Absence";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getAbsenceRecord_mandatory.json");
        
        Assert.assertFalse(Boolean.parseBoolean(esbRestResponse.getBody().getString("isError")));
        Assert.assertEquals(esbRestResponse.getBody().get("Message"), apiRestResponse.getBody().get("Message"));
        Assert.assertEquals(esbRestResponse.getBody().get("Status"), apiRestResponse.getBody().get("Status"));
    }
    
    /**
     * Positive test case for getAbsenceRecord method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetAbsenceRecordWithMandatoryParameters" }, description = "peoplehr {getAbsenceRecord} integration test with optional parameters.")
    public void testGetAbsenceRecordWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAbsenceRecord");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAbsenceRecord_optional.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Absence";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getAbsenceRecord_optional.json");
        
        Assert.assertFalse(Boolean.parseBoolean(esbRestResponse.getBody().getString("isError")));
        Assert.assertEquals(esbRestResponse.getBody().get("Message"), apiRestResponse.getBody().get("Message"));
        Assert.assertEquals(esbRestResponse.getBody().get("Status"), apiRestResponse.getBody().get("Status"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("Result").getJSONObject(0).get("StartDate"),
                connectorProperties.get("leaveStartDate"));
    }
    
    /**
     * Negative test case for getAbsenceRecord method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetAbsenceRecordWithOptionalParameters" }, description = "peoplehr {getAbsenceRecord} integration test with negative case.")
    public void testGetAbsenceRecordWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAbsenceRecord");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAbsenceRecord_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Absence";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getAbsenceRecord_negative.json");
        
        Assert.assertTrue(Boolean.parseBoolean(esbRestResponse.getBody().getString("isError")));
        Assert.assertEquals(esbRestResponse.getBody().get("Message"), apiRestResponse.getBody().get("Message"));
        Assert.assertEquals(esbRestResponse.getBody().get("Status"), apiRestResponse.getBody().get("Status"));
    }
    
    /**
     * Positive test case for updateAbsenceRecord method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetAbsenceRecordWithNegativeCase" }, description = "peoplehr {updateAbsenceRecord} integration test with mandatory parameters.")
    public void testUpdateAbsenceRecordWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateAbsenceRecord");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateAbsenceRecord_mandatory.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Absence";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateAbsenceRecord_mandatory.json");
        
        Assert.assertFalse(Boolean.parseBoolean(esbRestResponse.getBody().getString("isError")));
        Assert.assertEquals(esbRestResponse.getBody().get("Status"), 0);
        Assert.assertEquals(connectorProperties.get("leaveNewStartDate"),
                apiRestResponse.getBody().getJSONArray("Result").getJSONObject(0).get("StartDate"));
    }
    
    /**
     * Positive test case for updateAbsenceRecord method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateAbsenceRecordWithMandatoryParameters" }, description = "peoplehr {updateAbsenceRecord} integration test with optional parameters.")
    public void testUpdateAbsenceRecordWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateAbsenceRecord");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateAbsenceRecord_optional.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Absence";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateAbsenceRecord_optional.json");
        
        Assert.assertFalse(Boolean.parseBoolean(esbRestResponse.getBody().getString("isError")));
        Assert.assertEquals(esbRestResponse.getBody().get("Status"), 0);
        Assert.assertEquals(connectorProperties.get("leaveNewStartDate"),
                apiRestResponse.getBody().getJSONArray("Result").getJSONObject(0).get("StartDate"));
        Assert.assertEquals(connectorProperties.get("leaveComment"), apiRestResponse.getBody().getJSONArray("Result")
                .getJSONObject(0).getJSONArray("Comments").getJSONObject(0).get("Comments"));
    }
    
    /**
     * Negative test case for updateAbsenceRecord method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateAbsenceRecordWithOptionalParameters" }, description = "peoplehr {updateAbsenceRecord} integration test with negative case.")
    public void testUpdateAbsenceRecordWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateAbsenceRecord");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateAbsenceRecord_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/Absence";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateAbsenceRecord_negative.json");
        
        Assert.assertTrue(Boolean.parseBoolean(esbRestResponse.getBody().getString("isError")));
        Assert.assertEquals(esbRestResponse.getBody().get("Message"), apiRestResponse.getBody().get("Message"));
        Assert.assertEquals(esbRestResponse.getBody().get("Status"), apiRestResponse.getBody().get("Status"));
    }
}
