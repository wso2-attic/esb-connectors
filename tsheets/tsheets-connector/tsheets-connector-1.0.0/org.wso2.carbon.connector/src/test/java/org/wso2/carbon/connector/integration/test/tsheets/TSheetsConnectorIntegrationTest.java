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
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.connector.integration.test.tsheets;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class TSheetsConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("tsheets-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("accessToken"));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = new Date();
        date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(sdf.format(date));
        date.setMonth(date.getMonth() - 1);
        String timeSheetTwoEnd=sdf.format(date)+"-07:00";
        date.setDate(date.getDate() - 1);
        String timeSheetTwoStart=sdf.format(date)+"-07:00";
        date.setMonth(date.getMonth() - 1);
        String timeSheetOneEnd=sdf.format(date)+"-07:00";
        date.setDate(date.getDate()-1);
        String timeSheetOneStart=sdf.format(date)+"-07:00";
        connectorProperties.setProperty("timeSheetOneStart", timeSheetOneStart);
        connectorProperties.setProperty("timeSheetOneEnd", timeSheetOneEnd);
        connectorProperties.setProperty("timeSheetTwoStart", timeSheetTwoStart);
        connectorProperties.setProperty("timeSheetTwoEnd", timeSheetTwoEnd);
    }
    
    /**
     * Positive test case for listUsers method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "tsheets {listUsers} integration test with mandatory parameters.")
    public void testListUsersWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:listUsers");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_mandatory.json");
        
        JSONObject esbUsers = esbRestResponse.getBody().getJSONObject("results").getJSONObject("users");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v1/users";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiUsers = apiRestResponse.getBody().getJSONObject("results").getJSONObject("users");
        
        Iterator<String> esbUserKeySet = esbUsers.keys();
        
        Assert.assertEquals(apiUsers.length(), esbUsers.length());
        
        int count = 0;
        while (esbUserKeySet.hasNext()) {
            
            String userKey = esbUserKeySet.next();
            JSONObject esbUser = esbUsers.getJSONObject(userKey);
            JSONObject apiUser = apiUsers.getJSONObject(userKey);
            
            Assert.assertEquals(apiUser.getString("first_name"), esbUser.getString("first_name"));
            Assert.assertEquals(apiUser.getString("last_name"), esbUser.getString("last_name"));
            Assert.assertEquals(apiUser.getString("username"), esbUser.getString("username"));
            Assert.assertEquals(apiUser.getString("email"), esbUser.getString("email"));
            
            count++;
            
            if (count == 1) {
                connectorProperties.setProperty("userId", userKey);
            }
            
            if (count > 1) {
                break;
            }
            
        }
        
    }
    
    /**
     * Positive test case for listUsers method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "tsheets {listUsers} integration test with optional parameters.")
    public void testListUsersWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listUsers");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_optional.json");
        
        JSONObject esbUsers = esbRestResponse.getBody().getJSONObject("results").getJSONObject("users");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v1/users?page=2&per_page=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiUsers = apiRestResponse.getBody().getJSONObject("results").getJSONObject("users");
        
        Iterator<String> esbUserKeySet = esbUsers.keys();
        
        Assert.assertEquals(apiUsers.length(), esbUsers.length());
        
        while (esbUserKeySet.hasNext()) {
            
            String userKey = esbUserKeySet.next();
            JSONObject esbUser = esbUsers.getJSONObject(userKey);
            JSONObject apiUser = apiUsers.getJSONObject(userKey);
            
            Assert.assertEquals(apiUser.getString("first_name"), esbUser.getString("first_name"));
            Assert.assertEquals(apiUser.getString("last_name"), esbUser.getString("last_name"));
            Assert.assertEquals(apiUser.getString("username"), esbUser.getString("username"));
            Assert.assertEquals(apiUser.getString("email"), esbUser.getString("email"));
            
        }
        
    }
    
    /**
     * Negative test case for listUsers method .
     */
    @Test(groups = { "wso2.esb" }, description = "tsheets {listUsers} integration test with negative case.")
    public void testListUsersWithNegative() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listUsers");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_negative.json");
        
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("error");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v1/users?ids=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("error");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseObject.getString("code"), esbResponseObject.getString("code"));
        Assert.assertEquals(apiResponseObject.getString("message"), esbResponseObject.getString("message"));
        
    }
    
    /**
     * Positive test case for addJobCodes method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "tsheets {addJobCodes} integration test with mandatory parameters.")
    public void testAddJobCodesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:addJobCodes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addJobCodes_mandatory.json");
        
        JSONObject esbJobCodeOne =
                esbRestResponse.getBody().getJSONObject("results").getJSONObject("jobcodes").getJSONObject("1");
        String jobCodeOneId = esbJobCodeOne.get("id").toString();
        connectorProperties.setProperty("jobCodeId", jobCodeOneId);
        
        JSONObject esbJobCodeTwo =
                esbRestResponse.getBody().getJSONObject("results").getJSONObject("jobcodes").getJSONObject("2");
        String jobCodeTwoId = esbJobCodeTwo.get("id").toString();
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/jobcodes?ids=" + jobCodeOneId + "," + jobCodeTwoId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiJobCodeOne =
                apiRestResponse.getBody().getJSONObject("results").getJSONObject("jobcodes")
                        .getJSONObject(jobCodeOneId);
        JSONObject apiJobCodeTwo =
                apiRestResponse.getBody().getJSONObject("results").getJSONObject("jobcodes")
                        .getJSONObject(jobCodeTwoId);
        
        Assert.assertEquals(apiJobCodeOne.getString("name"), connectorProperties.get("jobCodeOneName"));
        Assert.assertEquals(apiJobCodeTwo.getString("name"), connectorProperties.get("jobCodeTwoName"));
        
    }
    
    /**
     * Negative test case for addJobCodes method.
     */
    @Test(groups = { "wso2.esb" }, description = "tsheets {addJobCodes} integration test with negative case.")
    public void testAddJobCodesWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:addJobCodes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addJobCodes_negative.json");
        
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("error");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v1/jobcodes";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_addJobCodes_negative.json");
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("error");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseObject.getString("code"), esbResponseObject.getString("code"));
        Assert.assertEquals(apiResponseObject.getString("message"), esbResponseObject.getString("message"));
        
    }
    
    /**
     * Positive test case for listJobCodes method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddJobCodesWithMandatoryParameters" }, description = "tsheets {listJobCodes} integration test with mandatory parameters.")
    public void testListJobCodesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listJobCodes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listJobCodes_mandatory.json");
        
        JSONObject esbJobCodes = esbRestResponse.getBody().getJSONObject("results").getJSONObject("jobcodes");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v1/jobcodes";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiJobCodes = apiRestResponse.getBody().getJSONObject("results").getJSONObject("jobcodes");
        
        Iterator<String> esbJobCodesKeySet = esbJobCodes.keys();
        int count = 0;
        
        while (esbJobCodesKeySet.hasNext()) {
            
            String jobCodeKey = esbJobCodesKeySet.next();
            JSONObject esbJobCode = esbJobCodes.getJSONObject(jobCodeKey);
            JSONObject apiJobCode = apiJobCodes.getJSONObject(jobCodeKey);
            
            Assert.assertEquals(apiJobCode.getString("name"), esbJobCode.getString("name"));
            Assert.assertEquals(apiJobCode.getString("created"), esbJobCode.getString("created"));
            Assert.assertEquals(apiJobCode.getString("last_modified"), esbJobCode.getString("last_modified"));
            
            count++;
            
            if (count > 1) {
                break;
            }
            
        }
        
    }
    
    /**
     * Positive test case for listJobCodes method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddJobCodesWithMandatoryParameters" }, description = "tsheets {listJobCodes} integration test with optional parameters.")
    public void testListJobCodesWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listJobCodes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listJobCodes_optional.json");
        
        JSONObject esbJobCodes = esbRestResponse.getBody().getJSONObject("results").getJSONObject("jobcodes");
        
        int esbJobCodesCount = esbJobCodes.length();
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v1/jobcodes?page=2&per_page=1";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiJobCodes = apiRestResponse.getBody().getJSONObject("results").getJSONObject("jobcodes");
        
        int apiJobCodesCount = esbJobCodes.length();
        
        Assert.assertEquals(apiJobCodesCount, esbJobCodesCount);
        
        Iterator<String> esbJobCodesKeySet = esbJobCodes.keys();
        
        while (esbJobCodesKeySet.hasNext()) {
            
            String jobCodeKey = esbJobCodesKeySet.next();
            JSONObject esbJobCode = esbJobCodes.getJSONObject(jobCodeKey);
            JSONObject apiJobCode = apiJobCodes.getJSONObject(jobCodeKey);
            
            Assert.assertEquals(apiJobCode.getString("id"), esbJobCode.getString("id"));
            Assert.assertEquals(apiJobCode.getString("created"), esbJobCode.getString("created"));
            Assert.assertEquals(apiJobCode.getString("last_modified"), esbJobCode.getString("last_modified"));
            
        }
        
    }
    
    /**
     * Negative test case for listJobCodes method.
     */
    @Test(groups = { "wso2.esb" }, description = "tsheets {listJobCodes} integration test with negative case.")
    public void testListJobCodesWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listJobCodes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listJobCodes_negative.json");
        
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("error");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v1/jobcodes?page=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("error");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseObject.getString("code"), esbResponseObject.getString("code"));
        Assert.assertEquals(apiResponseObject.getString("message"), esbResponseObject.getString("message"));
        
    }
    
    /**
     * Positive test case for addTimeSheets method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListUsersWithMandatoryParameters",
            "testAddJobCodesWithMandatoryParameters" }, description = "tsheets {addTimeSheets} integration test with mandatory parameters.")
    public void testAddTimeSheetsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:addTimeSheets");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addTimeSheets_mandatory.json");
        
        JSONObject esbTimeSheetOne =
                esbRestResponse.getBody().getJSONObject("results").getJSONObject("timesheets").getJSONObject("1");
        String timeSheetId1 = esbTimeSheetOne.getString("id");
        
        JSONObject esbTimeSheetTwo =
                esbRestResponse.getBody().getJSONObject("results").getJSONObject("timesheets").getJSONObject("2");
        String timeSheetId2 = esbTimeSheetTwo.getString("id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/timesheets?ids=" + timeSheetId1 + ","
                        + timeSheetId2;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiTimeSheetOne =
                apiRestResponse.getBody().getJSONObject("results").getJSONObject("timesheets")
                        .getJSONObject(timeSheetId1);
        JSONObject apiTimeSheetTwo =
                apiRestResponse.getBody().getJSONObject("results").getJSONObject("timesheets")
                        .getJSONObject(timeSheetId2);
        
        Assert.assertEquals(apiTimeSheetOne.getString("start"), esbTimeSheetOne.getString("start"));
        Assert.assertEquals(apiTimeSheetOne.getString("end"), esbTimeSheetOne.getString("end"));
        Assert.assertEquals(apiTimeSheetOne.getString("last_modified"), esbTimeSheetOne.getString("last_modified"));
        Assert.assertEquals(apiTimeSheetTwo.getString("end"), esbTimeSheetTwo.getString("end"));
        
    }
    
    /**
     * Negative test case for addTimeSheets method .
     */
    @Test(groups = { "wso2.esb" }, description = "tsheets {addTimeSheets} integration test with negative case.")
    public void testAddTimeSheetsWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:addTimeSheets");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addTimeSheets_negative.json");

        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("results").getJSONObject("timesheets").getJSONObject("1");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v1/timesheets";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_addTimeSheets_negative.json");

        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("results").getJSONObject("timesheets").getJSONObject("1");
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseObject.getString("_status_code"), esbResponseObject.getString("_status_code"));
        Assert.assertEquals(apiResponseObject.getString("_status_message"), esbResponseObject.getString("_status_message"));

    }
    
    /**
     * Positive test case for listTimeSheets method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddTimeSheetsWithMandatoryParameters" }, description = "tsheets {listTimeSheets} integration test with mandatory parameters.")
    public void testListTimeSheetsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTimeSheets");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTimeSheets_mandatory.json");
        
        JSONObject esbTimeSheets = esbRestResponse.getBody().getJSONObject("results").getJSONObject("timesheets");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/timesheets?start_date="
                        + connectorProperties.getProperty("timeSheetOneStart") + "&end_date="
                        + connectorProperties.getProperty("timeSheetOneEnd");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiTimeSheets = apiRestResponse.getBody().getJSONObject("results").getJSONObject("timesheets");
        
        Iterator<String> esbTimeSheetKeySet = esbTimeSheets.keys();
        int count = 0;
        
        while (esbTimeSheetKeySet.hasNext()) {
            
            String userKey = esbTimeSheetKeySet.next();
            JSONObject esbTimeSheet = esbTimeSheets.getJSONObject(userKey);
            JSONObject apiTimeSheet = apiTimeSheets.getJSONObject(userKey);
            
            Assert.assertEquals(apiTimeSheet.getString("user_id"), esbTimeSheet.getString("user_id"));
            Assert.assertEquals(apiTimeSheet.getString("jobcode_id"), esbTimeSheet.getString("jobcode_id"));
            Assert.assertEquals(apiTimeSheet.getString("start"), esbTimeSheet.getString("start"));
            Assert.assertEquals(apiTimeSheet.getString("end"), esbTimeSheet.getString("end"));
            
            count++;
            
            if (count > 1) {
                break;
            }
            
        }
        
    }
    
    /**
     * Positive test case for listTimeSheets method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListUsersWithMandatoryParameters",
            "testAddTimeSheetsWithMandatoryParameters" }, description = "tsheets {listTimeSheets} integration test with optional parameters.")
    public void testListTimeSheetsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTimeSheets");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTimeSheets_optional.json");
        
        JSONObject esbTimeSheets = esbRestResponse.getBody().getJSONObject("results").getJSONObject("timesheets");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/timesheets?start_date="
                        + connectorProperties.getProperty("timeSheetOneStart") + "&end_date="
                        + connectorProperties.getProperty("timeSheetOneEnd") + "&user_ids="
                        + connectorProperties.getProperty("userId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiTimeSheets = apiRestResponse.getBody().getJSONObject("results").getJSONObject("timesheets");
        
        Iterator<String> esbTimeSheetKeySet = esbTimeSheets.keys();
        int count = 0;
        
        while (esbTimeSheetKeySet.hasNext()) {
            
            String userKey = esbTimeSheetKeySet.next();
            JSONObject esbTimeSheet = esbTimeSheets.getJSONObject(userKey);
            JSONObject apiTimeSheet = apiTimeSheets.getJSONObject(userKey);
            
            Assert.assertEquals(apiTimeSheet.getString("user_id"), esbTimeSheet.getString("user_id"));
            Assert.assertEquals(apiTimeSheet.getString("jobcode_id"), esbTimeSheet.getString("jobcode_id"));
            Assert.assertEquals(apiTimeSheet.getString("start"), esbTimeSheet.getString("start"));
            Assert.assertEquals(apiTimeSheet.getString("end"), esbTimeSheet.getString("end"));
            
            count++;
            
            if (count > 1) {
                break;
            }
            
        }
        
    }
    
    /**
     * Negative test case for listTimeSheets method .
     */
    @Test(groups = { "wso2.esb" }, description = "tsheets {listTimeSheets} integration test with negative case.")
    public void testListTimeSheetsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listTimeSheets");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTimeSheets_negative.json");
        
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("error");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v1/timesheets";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("error");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseObject.getString("code"), esbResponseObject.getString("code"));
        Assert.assertEquals(apiResponseObject.getString("message"), esbResponseObject.getString("message"));
        
    }
    
    /**
     * Positive test case for addJobCodeAssignments method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" },dependsOnMethods = { "testListUsersWithMandatoryParameters",
    "testAddJobCodesWithMandatoryParameters" }, description = "tsheets {addJobCodeAssignments} integration test with mandatory parameters.")
    public void testAddJobCodeAssignmentsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:addJobCodeAssignments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addJobCodeAssignments_mandatory.json");
        
        JSONObject esbJobCodeAssignment =
                esbRestResponse.getBody().getJSONObject("results").getJSONObject("jobcode_assignments").getJSONObject("1");
        String jobCodeAssignmentId= esbJobCodeAssignment.getString("id");
        String jobCodeAssignmentUserId = esbJobCodeAssignment.getString("user_id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/jobcode_assignments?user_ids=" + jobCodeAssignmentUserId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiJobCodeAssignment =
                apiRestResponse.getBody().getJSONObject("results").getJSONObject("jobcode_assignments")
                        .getJSONObject(jobCodeAssignmentId);
        
        Assert.assertEquals(connectorProperties.getProperty("userId"), apiJobCodeAssignment.getString("user_id"));
        Assert.assertEquals(connectorProperties.getProperty("jobCodeId"), apiJobCodeAssignment.getString("jobcode_id"));
    }
    
    /**
     * Negative test case for addJobCodeAssignments method .
     */
    @Test(groups = { "wso2.esb" }, description = "tsheets {addJobCodeAssignments} integration test with negative case.")
    public void testAddJobCodeAssignmentsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:addJobCodeAssignments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addJobCodeAssignments_negative.json");
        
        JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("results").getJSONObject("jobcode_assignments").getJSONObject("1");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v1/jobcode_assignments";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_addJobCodeAssignments_negative.json");
        
        JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("results").getJSONObject("jobcode_assignments").getJSONObject("1");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiResponseObject.getString("_status_code"), esbResponseObject.getString("_status_code"));
        Assert.assertEquals(apiResponseObject.getString("_status_message"), esbResponseObject.getString("_status_message"));
        Assert.assertEquals(apiResponseObject.getString("_status_extra"), esbResponseObject.getString("_status_extra"));
        
    }
    
    /**
     * Positive test case for listJobCodeAssignments method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddJobCodeAssignmentsWithMandatoryParameters" }, description = "tsheets {listJobCodeAssignments} integration test with mandatory parameters.")
    public void testListJobCodeAssignmentsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listJobCodeAssignments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listJobCodeAssignments_mandatory.json");
        
        JSONObject esbJobAssignments = esbRestResponse.getBody().getJSONObject("results").getJSONObject("jobcode_assignments");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/jobcode_assignments";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiJobAssignments = apiRestResponse.getBody().getJSONObject("results").getJSONObject("jobcode_assignments");
        
        Iterator<String> esbTimeSheetKeySet = esbJobAssignments.keys();
        int count = 0;
        
        while (esbTimeSheetKeySet.hasNext()) {
            
            String userKey = esbTimeSheetKeySet.next();
            JSONObject esbJobAssignment = esbJobAssignments.getJSONObject(userKey);
            JSONObject apiJobAssignment = apiJobAssignments.getJSONObject(userKey);
            
            Assert.assertEquals(apiJobAssignment.getString("id"), esbJobAssignment.getString("id"));
            Assert.assertEquals(apiJobAssignment.getString("user_id"), esbJobAssignment.getString("user_id"));
            Assert.assertEquals(apiJobAssignment.getString("jobcode_id"), esbJobAssignment.getString("jobcode_id"));
            Assert.assertEquals(apiJobAssignment.getString("created"), esbJobAssignment.getString("created"));
            
            count++;
            
            if (count > 1) {
                break;
            }
        }   
      }
        
        /**
         * Positive test case for listJobCodeAssignments method with optional parameters.
         */
    	@Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddJobCodeAssignmentsWithMandatoryParameters" }, description = "tsheets {listJobCodeAssignments} integration test with optional parameters.")
        public void testListJobCodeAssignmentsWithOptionalParameters() throws IOException, JSONException {
        
            esbRequestHeadersMap.put("Action", "urn:listJobCodeAssignments");
            RestResponse<JSONObject> esbRestResponse =
                    sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listJobCodeAssignments_optional.json");
            
            JSONObject esbJobAssignments = esbRestResponse.getBody().getJSONObject("results").getJSONObject("jobcode_assignments");
            
            String apiEndPoint =
                    connectorProperties.getProperty("apiUrl") + "/api/v1/jobcode_assignments?page=2&per_page=1";
            RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
            
            JSONObject apiJobAssignments = apiRestResponse.getBody().getJSONObject("results").getJSONObject("jobcode_assignments");
            
            Iterator<String> esbTimeSheetKeySet = esbJobAssignments.keys();
            int count = 0;
            
            while (esbTimeSheetKeySet.hasNext()) {
                
                String userKey = esbTimeSheetKeySet.next();
                JSONObject esbJobAssignment = esbJobAssignments.getJSONObject(userKey);
                JSONObject apiJobAssignment = apiJobAssignments.getJSONObject(userKey);
                
                Assert.assertEquals(apiJobAssignment.getString("id"), esbJobAssignment.getString("id"));
                Assert.assertEquals(apiJobAssignment.getString("user_id"), esbJobAssignment.getString("user_id"));
                Assert.assertEquals(apiJobAssignment.getString("jobcode_id"), esbJobAssignment.getString("jobcode_id"));
                Assert.assertEquals(apiJobAssignment.getString("created"), esbJobAssignment.getString("created"));
                
                count++;
                
                if (count > 1) {
                    break;
                }
                
            }
        
    }
    	
    	/**
         * Negative test case for listJobCodeAssignments method .
         */
        @Test(groups = { "wso2.esb" }, description = "tsheets {listJobCodeAssignments} integration test with negative case.")
        public void testListJobCodeAssignmentsWithNegativeCase() throws IOException, JSONException {
        
            esbRequestHeadersMap.put("Action", "urn:listJobCodeAssignments");
            RestResponse<JSONObject> esbRestResponse =
                    sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listJobCodeAssignments_negative.json");
            
            JSONObject esbResponseObject = esbRestResponse.getBody().getJSONObject("error");
            
            String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v1/jobcode_assignments?page=invalid";
            RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
            
            JSONObject apiResponseObject = apiRestResponse.getBody().getJSONObject("error");
            
            Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
            Assert.assertEquals(apiResponseObject.getString("code"), esbResponseObject.getString("code"));
            Assert.assertEquals(apiResponseObject.getString("message"), esbResponseObject.getString("message"));
            
        }
    
    
}
