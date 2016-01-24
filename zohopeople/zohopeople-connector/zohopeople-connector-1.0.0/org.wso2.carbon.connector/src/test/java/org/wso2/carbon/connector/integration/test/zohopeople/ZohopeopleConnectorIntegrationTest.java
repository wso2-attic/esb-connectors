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

package org.wso2.carbon.connector.integration.test.zohopeople;

import java.io.IOException;
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

public class ZohopeopleConnectorIntegrationTest extends ConnectorIntegrationTestBase {
   
   private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
   
   private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
   
   /**
    * Set up the environment.
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {
   
      init("zohopeople-connector-1.0.0");
      
      esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
      esbRequestHeadersMap.put("Content-Type", "application/json");
      
      apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");
      
   }
   
   /**
    * Positive test case for createAttendance method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, description = "zohopeople {createAttendance} integration test with mandatory parameters")
   public void testCreateAttendanceWithMandatoryParameters() throws IOException, JSONException {
   
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
      Calendar cal = Calendar.getInstance();
      connectorProperties.setProperty("checkIn", dateFormat.format(cal.getTime()));
      // Incrementing the hours by 2.
      cal.add(Calendar.HOUR, 2);
      connectorProperties.setProperty("checkOut", dateFormat.format(cal.getTime()));
      
      esbRequestHeadersMap.put("Action", "urn:createAttendance");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAttendance_mandatory.json");
      JSONArray esbOutArray = new JSONArray(esbRestResponse.getBody().getString("output"));
      
      String apiEndPoint =
            connectorProperties.getProperty("apiUrl") + "/people/api/attendance?authtoken="
                  + connectorProperties.getProperty("accessToken");
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createAttendance_mandatory.json");
      JSONArray apiOutArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiOutArray.getJSONObject(0).getString("punchIn"),
            esbOutArray.getJSONObject(0).getString("punchIn"));
      Assert.assertEquals(apiOutArray.getJSONObject(1).getString("tdate"),
            esbOutArray.getJSONObject(1).getString("tdate"));
      
   }
   
   /**
    * Positive test case for createAttendance method with optional parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testCreateAttendanceWithMandatoryParameters" }, description = "zohopeople {createAttendance} integration test with optional parameters.")
   public void testCreateAttendanceWithOptionalParameters() throws IOException, JSONException {
   
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
      Calendar cal = Calendar.getInstance();
      connectorProperties.setProperty("checkIn", dateFormat.format(cal.getTime()));
      // Incrementing the hours by 4.
      cal.add(Calendar.HOUR, 4);
      connectorProperties.setProperty("checkOut", dateFormat.format(cal.getTime()));
      
      esbRequestHeadersMap.put("Action", "urn:createAttendance");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAttendance_optional.json");
      JSONArray esbOutArray = new JSONArray(esbRestResponse.getBody().getString("output"));
      String apiEndPoint =
            connectorProperties.getProperty("apiUrl") + "/people/api/attendance?authtoken="
                  + connectorProperties.getProperty("accessToken");
      
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createAttendance_optional.json");
      JSONArray apiOutArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiOutArray.getJSONObject(0).getString("punchIn"),
            esbOutArray.getJSONObject(0).getString("punchIn"));
      Assert.assertEquals(apiOutArray.getJSONObject(1).getString("tdate"),
            esbOutArray.getJSONObject(1).getString("tdate"));
   }
   
   /**
    * Negative test case for createAttendance method.
    */
   @Test(priority = 1, dependsOnMethods = { "testCreateAttendanceWithOptionalParameters" }, description = "zohopeople {createAttendance} integration test with negative Case.")
   public void testCreateAttendanceWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createAttendance");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAttendance_negative.json");
      JSONArray esbOutArray = new JSONArray(esbRestResponse.getBody().getString("output"));
      
      String apiEndPoint =
            connectorProperties.getProperty("apiUrl") + "/people/api/attendance?authtoken="
                  + connectorProperties.getProperty("accessToken");
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createAttendance_negative.json");
      JSONArray apiOutArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      
      Assert.assertEquals(apiOutArray.getJSONObject(0).getString("error"),
            esbOutArray.getJSONObject(0).getString("error"));
   }
   
   /**
    * Positive test case for getLeaveTypes method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAttendanceWithNegativeCase" }, description = "ZohoPeople {getLeaveTypes} integration test with mandatory parameters")
   public void testGetLeaveTypesWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:getLeaveTypes");
      String apiEndPoint =
            connectorProperties.getProperty("apiUrl") + "/people/api/leave/getLeaveTypes?authtoken="
                  + connectorProperties.getProperty("accessToken") + "&userId="
                  + connectorProperties.getProperty("employeeId");
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getLeaveTypes_mandatory.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getJSONArray("result").length(),
            apiRestResponse.getBody().getJSONObject("response").getJSONArray("result").length());
      
      JSONObject responseESB =
            esbRestResponse.getBody().getJSONObject("response").getJSONArray("result").getJSONObject(0);
      JSONObject responseAPI =
            apiRestResponse.getBody().getJSONObject("response").getJSONArray("result").getJSONObject(0);
      
      Assert.assertEquals(responseESB.getString("Name"), responseAPI.getString("Name"));
      Assert.assertEquals(responseESB.getString("Id"), responseAPI.getString("Id"));
      connectorProperties.setProperty("leaveTypeId", responseESB.getString("Id"));
   }
   
   /**
    * Negative test case for getLeaveTypes method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetLeaveTypesWithMandatoryParameters" }, description = "ZohoPeople {getLeaveTypes} integration test for negative case")
   public void testGetLeaveTypesNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:getLeaveTypes");
      String apiEndPoint =
            connectorProperties.getProperty("apiUrl") + "/people/api/leave/getLeaveTypes?authtoken="
                  + connectorProperties.getProperty("accessToken") + "&userId=INVALID_USER_ID";
      
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getLeaveTypes_negative.json");
      
      Assert.assertEquals(esbRestResponse.getBody().getJSONObject("response").getString("status"), apiRestResponse
            .getBody().getJSONObject("response").getString("status"));
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONObject("response").getJSONObject("errors").getString("message"),
            apiRestResponse.getBody().getJSONObject("response").getJSONObject("errors").getString("message"));
      Assert.assertEquals(
            esbRestResponse.getBody().getJSONObject("response").getJSONObject("errors").getString("code"),
            apiRestResponse.getBody().getJSONObject("response").getJSONObject("errors").getString("code"));
   }
   
   /**
    * Positive test case for createLeave method with mandatory parameters.
    */
   @Test(priority = 1, dependsOnMethods = { "testGetLeaveTypesNegativeCase" }, description = "zohopeople {createLeave} integration test with mandatory parameters")
   public void testCreateLeaveWithMandatoryParameters() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createLeave");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createLeave_mandatory.json");
      JSONArray esbOutArray = new JSONArray(esbRestResponse.getBody().getString("output"));
      
      String apiEndPoint =
            connectorProperties.getProperty("apiUrl") + "/people/api/leave/records?authtoken="
                  + connectorProperties.getProperty("accessToken");
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createLeave_mandatory.json");
      JSONArray apiOutArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiOutArray.getJSONObject(0).getString("message"),
            esbOutArray.getJSONObject(0).getString("message"));
      
   }
   
   /**
    * Negative test case for createLeave method.
    */
   @Test(priority = 1, dependsOnMethods = { "testCreateLeaveWithMandatoryParameters" }, description = "zohopeople {createLeave} integration test with negative Case.")
   public void testCreateLeaveWithNegativeCase() throws IOException, JSONException {
   
      esbRequestHeadersMap.put("Action", "urn:createLeave");
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createLeave_negative.json");
      JSONArray esbOutArray = new JSONArray(esbRestResponse.getBody().getString("output"));
      
      String apiEndPoint =
            connectorProperties.getProperty("apiUrl") + "/people/api/leave/records?authtoken="
                  + connectorProperties.getProperty("accessToken");
      RestResponse<JSONObject> apiRestResponse =
            sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createLeave_negative.json");
      JSONArray apiOutArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      
      Assert.assertEquals(esbRestResponse.getBody().getString("output"), apiRestResponse.getBody().getString("output"));
      Assert.assertEquals(esbOutArray.getJSONObject(0).getJSONArray("message").getJSONObject(0).getString("From"),
            apiOutArray.getJSONObject(0).getJSONArray("message").getJSONObject(0).getString("From"));
   }
   
   /**
    * Positive test case for createRecord method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateLeaveWithNegativeCase" }, description = "ZohoPeople {createRecord} integration test with mandatory parameters")
   public void testCreateRecordWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:createRecord");
      String apiEndPointBeforeESBCall =
            connectorProperties.getProperty("apiUrl") + "/people/api/forms/EmployeeInactiveView/records?authtoken="
                  + connectorProperties.getProperty("accessToken");
      RestResponse<JSONObject> apiRestResponseBeforeEsbCall =
            sendJsonRestRequest(apiEndPointBeforeESBCall, "GET", apiRequestHeadersMap);
      
      // Creating a unique employee id and a employee email by appending the date string
      SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
      connectorProperties.put("emplyeeIdMandatory", "test_id_" + dateFormat.format(new Date()));
      connectorProperties.put("emplyeeEmailMandatory", "test_email_" + dateFormat.format(new Date()).toString().replace(":","") + "@gmail.com");
      sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRecord_mandatory.json");
      String apiEndPointAfterESBCall =
            connectorProperties.getProperty("apiUrl") + "/people/api/forms/EmployeeInactiveView/records?authtoken="
                  + connectorProperties.getProperty("accessToken");
      RestResponse<JSONObject> apiRestResponseAfterEsbCall =
            sendJsonRestRequest(apiEndPointAfterESBCall, "GET", apiRequestHeadersMap);
      
      Assert.assertFalse(apiRestResponseBeforeEsbCall.getBody().toString()
            .contains(connectorProperties.getProperty("emplyeeIdMandatory")));
      Assert.assertTrue(apiRestResponseAfterEsbCall.getBody().toString()
            .contains(connectorProperties.getProperty("emplyeeIdMandatory")));
   }
   
   /**
    * Negative test case for createRecord method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateRecordWithMandatoryParameters" }, description = "ZohoPeople {createRecord} integration test negative case")
   public void testCreateRecordNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:createRecord");
      String apiEndPointBeforeESBCall =
            connectorProperties.getProperty("apiUrl") + "/people/api/forms/EmployeeInactiveView/records?authtoken="
                  + connectorProperties.getProperty("accessToken");
      RestResponse<JSONObject> apiRestResponseBeforeEsbCall =
            sendJsonRestRequest(apiEndPointBeforeESBCall, "GET", apiRequestHeadersMap);
      
      // Creating a unique employee id and a employee email by appending the date string
      SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
      connectorProperties.put("emplyeeIdNegative", "test_id_" + dateFormat.format(new Date()));
      connectorProperties.put("emplyeeEmailNegative", "test_email_" + dateFormat.format(new Date()) + "@gmail.com");
      
      sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRecord_negative.json");
      String apiEndPointAfterESBCall =
            connectorProperties.getProperty("apiUrl") + "/people/api/forms/EmployeeInactiveView/records?authtoken="
                  + connectorProperties.getProperty("accessToken");
      RestResponse<JSONObject> apiRestResponseAfterEsbCall =
            sendJsonRestRequest(apiEndPointAfterESBCall, "GET", apiRequestHeadersMap);
      
      Assert.assertFalse(apiRestResponseBeforeEsbCall.getBody().toString()
            .contains(connectorProperties.getProperty("emplyeeIdNegative")));
      Assert.assertFalse(apiRestResponseAfterEsbCall.getBody().toString()
            .contains(connectorProperties.getProperty("emplyeeIdNegative")));
   }
   
   /**
    * Positive test case for updateRecord method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateRecordNegativeCase" }, description = "ZohoPeople {updateRecord} integration test with mandatory parameters")
   public void testUpdateRecordWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:updateRecord");
      String apiEndPointBeforeESBCall =
            connectorProperties.getProperty("apiUrl") + "/people/api/forms/P_Employee/getRecordByID?authtoken="
                  + connectorProperties.getProperty("accessToken") + "&recordId="
                  + connectorProperties.getProperty("recordId");
      RestResponse<JSONObject> apiRestResponseBeforeEsbCall =
            sendJsonRestRequest(apiEndPointBeforeESBCall, "GET", apiRequestHeadersMap);
      
      // Creating a new name to update using the time
      SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
      connectorProperties.put("updatedEmployeeName", "emp_name_" + dateFormat.format(new Date()));
      
      sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateRecord_mandatory.json");
      
      String apiEndPointAfterESBCall =
            connectorProperties.getProperty("apiUrl") + "/people/api/forms/P_Employee/getRecordByID?authtoken="
                  + connectorProperties.getProperty("accessToken") + "&recordId="
                  + connectorProperties.getProperty("recordId");
      RestResponse<JSONObject> apiRestResponseAfterEsbCall =
            sendJsonRestRequest(apiEndPointAfterESBCall, "GET", apiRequestHeadersMap);
      
      Assert.assertNotEquals(apiRestResponseBeforeEsbCall.getBody().getJSONObject("response").getJSONArray("result")
            .getJSONObject(0).getJSONObject("Basic Info").get("FirstName").toString(), apiRestResponseAfterEsbCall
            .getBody().getJSONObject("response").getJSONArray("result").getJSONObject(0).getJSONObject("Basic Info")
            .getString("FirstName"));
      Assert.assertEquals(apiRestResponseAfterEsbCall.getBody().getJSONObject("response").getJSONArray("result")
            .getJSONObject(0).getJSONObject("Basic Info").getString("FirstName"),
            connectorProperties.get("updatedEmployeeName"));
   }
   
   /**
    * Negative test case for updateRecord method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateRecordWithMandatoryParameters" }, description = "ZohoPeople {updateRecord} integration test negative case")
   public void testUpdateRecordNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:updateRecord");
      String apiEndPointBeforeESBCall =
            connectorProperties.getProperty("apiUrl") + "/people/api/forms/P_Employee/getRecordByID?authtoken="
                  + connectorProperties.getProperty("accessToken") + "&recordId="
                  + connectorProperties.getProperty("recordId");
      RestResponse<JSONObject> apiRestResponseBeforeEsbCall =
            sendJsonRestRequest(apiEndPointBeforeESBCall, "GET", apiRequestHeadersMap);
      
      // Creating a new name to update using the time
      SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
      connectorProperties.put("updatedEmployeeName", dateFormat.format(new Date()));
      sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateRecord_negative.json");
      
      String apiEndPointAfterESBCall =
            connectorProperties.getProperty("apiUrl") + "/people/api/forms/P_Employee/getRecordByID?authtoken="
                  + connectorProperties.getProperty("accessToken") + "&recordId="
                  + connectorProperties.getProperty("recordId");
      RestResponse<JSONObject> apiRestResponseAfterEsbCall =
            sendJsonRestRequest(apiEndPointAfterESBCall, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(apiRestResponseBeforeEsbCall.getBody().getJSONObject("response").getJSONArray("result")
            .getJSONObject(0).getJSONObject("Basic Info").getString("FirstName"), apiRestResponseAfterEsbCall
            .getBody().getJSONObject("response").getJSONArray("result").getJSONObject(0).getJSONObject("Basic Info")
            .getString("FirstName"));
   }
   
   /**
    * Positive test case for getRecord method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateRecordNegativeCase" }, description = "ZohoPeople {getRecord} integration test with mandatory parameters")
   public void testGetRecordWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:getRecord");
      String apiEndPoint =
            connectorProperties.getProperty("apiUrl") + "/people/api/forms/P_Employee/getRecordByID?authtoken="
                  + connectorProperties.getProperty("accessToken") + "&recordId="
                  + connectorProperties.getProperty("recordId");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecord_mandatory.json");
      
      JSONObject responseESB =
            esbRestResponse.getBody().getJSONObject("response").getJSONArray("result").getJSONObject(0);
      JSONObject responseAPI =
            apiRestResponse.getBody().getJSONObject("response").getJSONArray("result").getJSONObject(0);
      
      Assert.assertTrue(esbRestResponse.getHttpStatusCode() == 200);
      Assert.assertEquals(responseESB.getJSONObject("Basic Info").toString(), responseAPI.getJSONObject("Basic Info")
            .toString());
      Assert.assertEquals(responseESB.getJSONObject("Work").toString(), responseAPI.getJSONObject("Work").toString());
   }
   
   /**
    * Negative test case for getRecord method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetRecordWithMandatoryParameters" }, description = "ZohoPeople {getRecord} integration test negative case")
   public void testGetRecordNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:getRecord");
      String apiEndPoint =
            connectorProperties.getProperty("apiUrl") + "/people/api/forms/invalid/getRecordByID?authtoken="
                  + connectorProperties.getProperty("accessToken") + "&recordId="
                  + connectorProperties.getProperty("recordId");
      RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      JSONArray apiJsonArray = new JSONArray(apiRestResponse.getBody().getString("output"));
      
      RestResponse<JSONObject> esbRestResponse =
            sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecord_negative.json");
      JSONArray esbJsonArray = new JSONArray(esbRestResponse.getBody().getString("output"));
      
      Assert.assertEquals(esbJsonArray.getJSONObject(0).getString("message"),
            apiJsonArray.getJSONObject(0).getString("message"));
      Assert.assertEquals(esbJsonArray.getJSONObject(0).getString("errorcode"),
            apiJsonArray.getJSONObject(0).getString("errorcode"));
   }
   
}
