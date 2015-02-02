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

package org.wso2.carbon.connector.integration.test.canvas;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
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

public class CanvasConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> headersMap = new HashMap<String, String>();
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("canvas-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("accessToken"));
        apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");
        setCanvasAccoutId();
        
    }
    
    private void setCanvasAccoutId() throws IOException, JSONException {
    
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v1/course_accounts";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        String accountId = apiResponseArray.getJSONObject(0).getString("id");
        connectorProperties.put("accountId", accountId);
        
    }
    
    /**
     * Test createCourse method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "canvas {createCourse} integration test with mandatory parameters")
    public void testCreateCourseWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCourse");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCourse_mandatory.json");
        
        String courseId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("courseId", courseId);
        connectorProperties.put("contextCode", "course_" + courseId);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/accounts/"
                        + connectorProperties.getProperty("accountId") + "/courses/"
                        + connectorProperties.getProperty("courseId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        // Assert ID, name and course_code
        Assert.assertEquals(courseId, apiRestResponse.getBody().getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("course_code"),
                apiRestResponse.getBody().getString("course_code"));
        
    }
    
    /**
     * Test createCourse method with Optional Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCourseWithMandatoryParameters" }, description = "canvas {createCourse} integration test with optional parameters")
    public void testCreateCourseWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCourse");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCourse_optional.json");
        
        String courseId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("courseIdOptional", courseId);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/accounts/"
                        + connectorProperties.getProperty("accountId") + "/courses/"
                        + connectorProperties.getProperty("courseIdOptional");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        // Assert ID, name and description
        Assert.assertEquals(courseId, apiRestResponse.getBody().getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("course_code"),
                apiRestResponse.getBody().getString("course_code"));
        
    }
    
    /**
     * Test getCourse method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCourseWithMandatoryParameters" }, description = "canvas {getCourse} integration test with mandatory parameters.")
    public void testGetCourseWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getCourse");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCourse_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/accounts/"
                        + connectorProperties.getProperty("accountId") + "/courses/"
                        + connectorProperties.getProperty("courseId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        // Assert ID, name and course_code
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("course_code"),
                apiRestResponse.getBody().getString("course_code"));
        
    }
    
    /**
     * Test getCourse method with Optional Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCourseWithOptionalParameters" }, description = "canvas {getCourse} integration test with optional parameters.")
    public void testGetCourseWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getCourse");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCourse_optional.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/accounts/"
                        + connectorProperties.getProperty("accountId") + "/courses/"
                        + connectorProperties.getProperty("courseIdOptional")
                        + "?include[]=all_courses&include[]=permissions";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        // Assert ID, name and course_code
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("course_code"),
                apiRestResponse.getBody().getString("course_code"));
        
    }
    
    /**
     * Test listCourses method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCourseWithMandatoryParameters",
            "testCreateCourseWithOptionalParameters" }, description = "canvas {listCourses} integration test with mandatory parameters.")
    public void testListCoursesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCourses");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCourses_mandatory.json");
        JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api/v1/courses";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("name"), apiResponseArray.getJSONObject(0)
                .getString("name"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("course_code"),
                apiResponseArray.getJSONObject(0).getString("course_code"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("start_at"), apiResponseArray.getJSONObject(0)
                .getString("start_at"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("end_at"), apiResponseArray.getJSONObject(0)
                .getString("end_at"));
        
    }
    
    /**
     * Test listCourses method with Optional Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCourseWithMandatoryParameters",
            "testCreateCourseWithOptionalParameters" }, description = "canvas {listCourses} integration test with optional parameters.")
    public void testListCoursesWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCourses");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCourses_optional.json");
        JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/courses" + "?include[]=term&include[]=sections";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        // Term details
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getJSONObject("term").getString("id"), apiResponseArray
                .getJSONObject(0).getJSONObject("term").getString("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getJSONObject("term").getString("name"), apiResponseArray
                .getJSONObject(0).getJSONObject("term").getString("name"));
        
        // Section details
        Assert.assertEquals(
                esbResponseArray.getJSONObject(0).getJSONArray("sections").getJSONObject(0).getString("id"),
                apiResponseArray.getJSONObject(0).getJSONArray("sections").getJSONObject(0).getString("id"));
        Assert.assertEquals(
                esbResponseArray.getJSONObject(0).getJSONArray("sections").getJSONObject(0).getString("name"),
                apiResponseArray.getJSONObject(0).getJSONArray("sections").getJSONObject(0).getString("name"));
        
    }
    
    /**
     * Test getCourseUser method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListCourseUsersWithMandatoryParameters" }, description = "canvas {getCourseUser} integration test with mandatory parameters.")
    public void testGetCourseUserWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getCourseUser");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCourseUser_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/courses/"
                        + connectorProperties.getProperty("courseId") + "/users/"
                        + connectorProperties.getProperty("userId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        // Assert ID, name and description
        Assert.assertEquals(esbRestResponse.getBody().getString("id"), apiRestResponse.getBody().getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("sortable_name"),
                apiRestResponse.getBody().getString("sortable_name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("login_id"),
                apiRestResponse.getBody().getString("login_id"));
        
    }
    
    /**
     * Test listCourseUsers method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCourseWithMandatoryParameters" }, description = "canvas {listCourseUsers} integration test with mandatory parameters.")
    public void testListCourseUsersWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCourseUsers");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCourseUsers_mandatory.json");
        JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        connectorProperties.put("userId", esbResponseArray.getJSONObject(0).getString("id"));
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/courses/"
                        + connectorProperties.getProperty("courseId") + "/users";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("name"), apiResponseArray.getJSONObject(0)
                .getString("name"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("sortable_name"), apiResponseArray
                .getJSONObject(0).getString("sortable_name"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("login_id"), apiResponseArray.getJSONObject(0)
                .getString("login_id"));
        
    }
    
    /**
     * Test listCourseUsers method with Optional Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCourseWithOptionalParameters" }, description = "canvas {listCourseUsers} integration test with optional parameters.")
    public void testListCourseUsersWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCourseUsers");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCourseUsers_optional.json");
        JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/courses/"
                        + connectorProperties.getProperty("courseIdOptional") + "/users"
                        + "?include[]=email&include[]=enrollments";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        
        // ID and Email details
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("email"), apiResponseArray.getJSONObject(0)
                .getString("email"));
        
        // Enrollment details
        Assert.assertEquals(
                esbResponseArray.getJSONObject(0).getJSONArray("enrollments").getJSONObject(0).getString("role"),
                apiResponseArray.getJSONObject(0).getJSONArray("enrollments").getJSONObject(0).getString("role"));
        Assert.assertEquals(
                esbResponseArray.getJSONObject(0).getJSONArray("enrollments").getJSONObject(0).getString("course_id"),
                apiResponseArray.getJSONObject(0).getJSONArray("enrollments").getJSONObject(0).getString("course_id"));
        
    }
    
    /**
     * Test createCalendarEvent method with Mandatory Parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateCourseWithMandatoryParameters" }, description = "Canvas {createCalendarEvent} integration test with mandatory parameters.")
    public void testCreateCalendarEventWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCalendarEvent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCalendarEvent_mandatory.json");
        String calenderEventId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("calenderEventId", calenderEventId);
        
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v1/calendar_events/" + calenderEventId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        Assert.assertEquals(connectorProperties.getProperty("contextCode"),
                apiRestResponse.getBody().getString("context_code"));
        Assert.assertEquals(sdf.format(new Date()), apiRestResponse.getBody().getString("created_at").split("T")[0]);
        
    }
    
    /**
     * Test createCalendarEvent method with Optional Parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateCourseWithMandatoryParameters" }, description = "Canvas {createCalendarEvent} integration test with optional parameters.")
    public void testCreateCalendarEventWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createCalendarEvent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCalendarEvent_optional.json");
        
        String calenderEventId = esbRestResponse.getBody().getString("id");
        
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v1/calendar_events/" + calenderEventId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("contextCode"),
                apiRestResponse.getBody().getString("context_code"));
        Assert.assertEquals(connectorProperties.getProperty("eventTitle"), apiRestResponse.getBody().getString("title"));
        Assert.assertEquals(connectorProperties.getProperty("eventDescription"),
                apiRestResponse.getBody().getString("description"));
        
    }
    
    /**
     * Test createCalendarEvent method with Negative Case.
     */
    @Test(priority = 2, description = "Canvas {createCalendarEvent} integration test with  negative case.")
    public void testCreateCalendarEventWithNegativeCase() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(5000);
        esbRequestHeadersMap.put("Action", "urn:createCalendarEvent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCalendarEvent_negative.json");
        
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v1/calendar_events";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiUrl, "POST", apiRequestHeadersMap, "api_createCalenderEvent_negative.json");
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("message"),
                esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("message"));
        
    }
    
    /**
     * Test getCalendarEvent method with Mandatory Parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateCalendarEventWithMandatoryParameters" }, description = "Canvas {getCalendarEvent} integration test with mandatory parameters.")
    public void testGetCalendarEventWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getCalendarEvent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCalendarEvent_mandatory.json");
        
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v1/calendar_events/"
                        + connectorProperties.getProperty("calenderEventId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("created_at"),
                esbRestResponse.getBody().getString("created_at"));
        Assert.assertEquals(apiRestResponse.getBody().getString("title"), esbRestResponse.getBody().getString("title"));
        Assert.assertEquals(apiRestResponse.getBody().getString("context_code"),
                esbRestResponse.getBody().getString("context_code"));
        Assert.assertEquals(apiRestResponse.getBody().getString("workflow_state"),
                esbRestResponse.getBody().getString("workflow_state"));
        
    }
    
    /**
     * Test listCalendarEvents method with Mandatory Parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateCalendarEventWithMandatoryParameters" }, description = "Canvas {listCalendarEvents} integration test with mandatory parameters.")
    public void testListCalendarEventsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCalendarEvents");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCalendarEvents_mandatory.json");
        
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v1/calendar_events?start_date="
                        + connectorProperties.getProperty("calenderEventStartDate") + "T00:00:00Z";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        
        if (apiResponseArray.length() > 0) {
            JSONObject esbFirstResult = esbResponseArray.getJSONObject(0);
            JSONObject apiFirstResult = apiResponseArray.getJSONObject(0);
            
            Assert.assertEquals(apiFirstResult.getString("id"), esbFirstResult.getString("id"));
            Assert.assertEquals(apiFirstResult.getString("title"), esbFirstResult.getString("title"));
            Assert.assertEquals(apiFirstResult.getString("context_code"), esbFirstResult.getString("context_code"));
        }
        
    }
    
    /**
     * Test listCalendarEvents method with Optional Parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateCalendarEventWithMandatoryParameters" }, description = "Canvas {listCalendarEvents} integration test with optional parameters.")
    public void testListCalendarEventsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCalendarEvents");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCalendarEvents_optional.json");
        
        final String apiUrl =
                connectorProperties.getProperty("apiUrl")
                        + "/api/v1/calendar_events?all_events=true&per_page=4&start_date="
                        + connectorProperties.getProperty("calenderEventStartDate") + "T00:00:00Z";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        
        if (apiResponseArray.length() > 0) {
            JSONObject esbFirstResult = esbResponseArray.getJSONObject(0);
            JSONObject apiFirstResult = apiResponseArray.getJSONObject(0);
            
            Assert.assertEquals(apiFirstResult.getString("id"), esbFirstResult.getString("id"));
            Assert.assertEquals(apiFirstResult.getString("title"), esbFirstResult.getString("title"));
            Assert.assertEquals(apiFirstResult.getString("context_code"), esbFirstResult.getString("context_code"));
        }
        
    }
    
    /**
     * Test listCalendarEvents method with Negative case.
     */
    @Test(priority = 2, description = "Canvas {listCalendarEvents} integration test with negative case.")
    public void testListCalendarEventsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listCalendarEvents");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCalendarEvents_negative.json");
        
        final String apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v1/calendar_events?start_date=14/10/21";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("errors").getString("start_date"), esbRestResponse
                .getBody().getJSONObject("errors").getString("start_date"));
        
    }
    
    /**
     * Test search method with Optional Parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateCalendarEventWithMandatoryParameters" }, description = "Canvas {search} integration test with optional parameters.")
    public void testSearchWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:search");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_search_optional.json");
        
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v1/search/recipients?context="
                        + connectorProperties.getProperty("contextCode");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        
        if (apiResponseArray.length() > 0) {
            JSONObject esbFirstResult = esbResponseArray.getJSONObject(0);
            JSONObject apiFirstResult = apiResponseArray.getJSONObject(0);
            
            Assert.assertEquals(apiFirstResult.getString("id"), esbFirstResult.getString("id"));
            Assert.assertEquals(apiFirstResult.getString("name"), esbFirstResult.getString("name"));
            Assert.assertEquals(apiFirstResult.getString("avatar_url"), esbFirstResult.getString("avatar_url"));
        }
        
    }
    
    /**
     * Positive test case for createDiscussionTopic method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCourseWithMandatoryParameters" }, description = "canvas {createDiscussionTopic} integration test with mandatory parameters")
    public void testCreateDiscussionTopicWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createDiscussionTopic");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDiscussionTopic_mandatory.json");
        
        String topicId = esbRestResponse.getBody().getString("id");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/courses/"
                        + connectorProperties.getProperty("courseId") + "/discussion_topics/" + topicId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        // get current Date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        // get topic created date
        String topicPostedAt = apiRestResponse.getBody().getString("posted_at");
        String apiPostedAt = topicPostedAt.substring(0, topicPostedAt.indexOf('T'));
        
        Assert.assertEquals(currentDate, apiPostedAt);
    }
    
    /**
     * Positive test case for createDiscussionTopic method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCourseWithMandatoryParameters" }, description = "canvas {createDiscussionTopic} integration test with optional parameters")
    public void testCreateDiscussionTopicWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createDiscussionTopic");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDiscussionTopic_optional.json");
        
        // Adding the returned discussion topic id to property file
        String topicId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("topicId", topicId);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/courses/"
                        + connectorProperties.getProperty("courseId") + "/discussion_topics/" + topicId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        // get current Date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        // get topic created date
        String topicPostedAt = apiRestResponse.getBody().getString("posted_at");
        String apiPostedAt = topicPostedAt.substring(0, topicPostedAt.indexOf('T'));
        
        Assert.assertEquals(currentDate, apiPostedAt);
        Assert.assertEquals(connectorProperties.getProperty("title"), apiRestResponse.getBody().getString("title"));
        Assert.assertEquals(connectorProperties.getProperty("message"), apiRestResponse.getBody().getString("message"));
    }
    
    /**
     * Negative test case for createDiscussionTopic method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCourseWithMandatoryParameters" }, description = "canvas {createDiscussionTopic} integration test negative case.")
    public void testCreateDiscussionTopicNegativeCase() throws IOException, JSONException, InterruptedException {
    
        Thread.sleep(5000);
        esbRequestHeadersMap.put("Action", "urn:createDiscussionTopic");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createDiscussionTopic_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/courses/"
                        + connectorProperties.getProperty("courseId") + "/discussion_topics";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                        "api_createDiscussionTopic_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
        JSONObject esbErrorsObject = esbRestResponse.getBody().getJSONObject("errors");
        
        String esbErrorAttribute =
                esbErrorsObject.getJSONArray("discussion_type").getJSONObject(0).getString("attribute");
        String esbErrorMessage = esbErrorsObject.getJSONArray("discussion_type").getJSONObject(0).getString("message");
        
        JSONObject apiErrorsObject = apiRestResponse.getBody().getJSONObject("errors");
        
        String apiErrorAttribute =
                apiErrorsObject.getJSONArray("discussion_type").getJSONObject(0).getString("attribute");
        String apiErrorMessage = apiErrorsObject.getJSONArray("discussion_type").getJSONObject(0).getString("message");
        
        Assert.assertEquals(esbErrorAttribute, apiErrorAttribute);
        Assert.assertEquals(esbErrorMessage, apiErrorMessage);
        
    }
    
    /**
     * Positive test case for getDiscussionTopic method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDiscussionTopicWithOptionalParameters" }, description = "Canvas {getDiscussionTopic} integration test with mandatory parameters.")
    public void testGetDiscussionTopicWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getDiscussionTopic");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getDiscussionTopic_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/courses/"
                        + connectorProperties.getProperty("courseId") + "/discussion_topics/"
                        + connectorProperties.getProperty("topicId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("discussion_type"), esbRestResponse.getBody()
                .getString("discussion_type"));
        Assert.assertEquals(apiRestResponse.getBody().getString("title"), esbRestResponse.getBody().getString("title"));
        Assert.assertEquals(apiRestResponse.getBody().getString("published"),
                esbRestResponse.getBody().getString("published"));
        
    }
    
    /**
     * Positive test case for listDiscussionTopics method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCourseWithMandatoryParameters" }, description = "Canvas {listDiscussionTopics} integration test with mandatory parameters.")
    public void testListDiscussionTopicsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listDiscussionTopics");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDiscussionTopics_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/courses/"
                        + connectorProperties.getProperty("courseId") + "/discussion_topics";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        
        if (apiResponseArray.length() > 0) {
            JSONObject esbFirstResult = esbResponseArray.getJSONObject(0);
            JSONObject apiFirstResult = apiResponseArray.getJSONObject(0);
            
            Assert.assertEquals(apiFirstResult.getString("id"), esbFirstResult.getString("id"));
            Assert.assertEquals(apiFirstResult.getString("title"), esbFirstResult.getString("title"));
            Assert.assertEquals(apiFirstResult.getString("message"), esbFirstResult.getString("message"));
        }
        
    }
    
    /**
     * Positive test case for listDiscussionTopics method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDiscussionTopicWithOptionalParameters" }, description = "Canvas {listDiscussionTopics} integration test with optional parameters.")
    public void testListDiscussionTopicsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listDiscussionTopics");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listDiscussionTopics_optional.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/courses/"
                        + connectorProperties.getProperty("courseId")
                        + "/discussion_topics?order_by=position&scope=unlocked";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        
        if (apiResponseArray.length() > 0) {
            JSONObject esbFirstResult = esbResponseArray.getJSONObject(0);
            JSONObject apiFirstResult = apiResponseArray.getJSONObject(0);
            
            Assert.assertEquals(apiFirstResult.getString("id"), esbFirstResult.getString("id"));
            Assert.assertEquals(apiFirstResult.getString("title"), esbFirstResult.getString("title"));
            Assert.assertEquals(apiFirstResult.getString("message"), esbFirstResult.getString("message"));
        }
        
    }
    
    /**
     * Test createEntry method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDiscussionTopicWithOptionalParameters" }, description = "Canvas {createEntry} integration test with Mandatory parameters.")
    public void testCreateEntryWithMandatoryParameters() throws IOException, JSONException {
    
        headersMap.put("Action", "urn:createEntry");
        
        final String requestString =
                proxyUrl + "?courseId=" + connectorProperties.getProperty("courseId") + "&topicId="
                        + connectorProperties.getProperty("topicId") + "&apiUrl="
                        + connectorProperties.getProperty("apiUrl") + "&accessToken="
                        + connectorProperties.getProperty("accessToken");
        
        MultipartFormdataProcessor multipartProcessor = new MultipartFormdataProcessor(requestString, headersMap);
        
        RestResponse<JSONObject> esbRestResponse = multipartProcessor.processForJsonResponse();
        String entryId = esbRestResponse.getBody().getString("id");
        String userId = esbRestResponse.getBody().getString("user_id");
        
        connectorProperties.put("entryId", entryId);
        
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v1/courses/"
                        + connectorProperties.getProperty("courseId") + "/discussion_topics/"
                        + connectorProperties.getProperty("topicId") + "/entry_list?ids[]=" + entryId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        JSONObject apiFirstElement = apiResponseArray.getJSONObject(0);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        Assert.assertEquals(apiFirstElement.getString("created_at").split("T")[0], sdf.format(new Date()));
        Assert.assertEquals(apiFirstElement.getString("user_id"), userId);
    }
    
    /**
     * Test createEntry method with Optional Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateDiscussionTopicWithOptionalParameters" }, description = "Canvas {createEntry} integration test with optional parameters.")
    public void testCreateEntryWithOptionalParameters() throws IOException, JSONException {
    
        headersMap.put("Action", "urn:createEntry");
        
        final String requestString =
                proxyUrl + "?courseId=" + connectorProperties.getProperty("courseId") + "&topicId="
                        + connectorProperties.getProperty("topicId") + "&apiUrl="
                        + connectorProperties.getProperty("apiUrl") + "&accessToken="
                        + connectorProperties.getProperty("accessToken");
        
        MultipartFormdataProcessor multipartProcessor = new MultipartFormdataProcessor(requestString, headersMap);
        File file = new File(pathToResourcesDirectory + connectorProperties.getProperty("attachmentFileName"));
        multipartProcessor.addFileToRequest("attachment", file, URLConnection.guessContentTypeFromName(file.getName()));
        multipartProcessor.addFormDataToRequest("message", connectorProperties.getProperty("entryMessage"));
        
        RestResponse<JSONObject> esbRestResponse = multipartProcessor.processForJsonResponse();
        
        String entryId = esbRestResponse.getBody().getString("id");
        
        final String apiUrl =
                connectorProperties.getProperty("apiUrl") + "/api/v1/courses/"
                        + connectorProperties.getProperty("courseId") + "/discussion_topics/"
                        + connectorProperties.getProperty("topicId") + "/entry_list?ids[]=" + entryId;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiUrl, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        JSONObject apiFirstElement = apiResponseArray.getJSONObject(0);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        Assert.assertEquals(connectorProperties.getProperty("entryMessage"), apiFirstElement.getString("message"));
        Assert.assertEquals(connectorProperties.getProperty("attachmentFileName"),
                apiFirstElement.getJSONObject("attachment").getString("filename"));
        Assert.assertEquals(apiFirstElement.getString("created_at").split("T")[0], sdf.format(new Date()));
    }
    
    /**
     * Positive test case for updateEntry method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateEntryWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Canvas {updateEntry} integration test with optional parameters.")
    public void testUpdateEntryWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateEntry");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/courses/"
                        + connectorProperties.getProperty("courseId") + "/discussion_topics/"
                        + connectorProperties.getProperty("topicId") + "/entry_list?ids[]="
                        + connectorProperties.getProperty("entryId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject apiResponseObject = new JSONArray(apiRestResponse.getBody().getString("output")).getJSONObject(0);
        String originalEntryMessage = apiResponseObject.getString("message");
        String originalUpdatedTime = apiResponseObject.getString("updated_at");
        
        sendJsonRestRequest(proxyUrl, "PUT", esbRequestHeadersMap, "esb_updateEntry_optional.json");
        
        apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        apiResponseObject = new JSONArray(apiRestResponse.getBody().getString("output")).getJSONObject(0);
        
        String updatedEntryMessage = apiResponseObject.getString("message");
        String UpdatedTime = apiResponseObject.getString("updated_at");
        
        Assert.assertNotEquals(originalEntryMessage, updatedEntryMessage);
        Assert.assertNotEquals(originalUpdatedTime, UpdatedTime);
        
    }
    
    /**
     * Test listEntries method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateEntryWithMandatoryParameters" }, description = "canvas {listEntries} integration test with mandatory parameters.")
    public void testListEntriesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listEntries");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listEntries_mandatory.json");
        JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/courses/"
                        + connectorProperties.getProperty("courseId") + "/discussion_topics/"
                        + connectorProperties.getProperty("topicId") + "/entries";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("user_id"), apiResponseArray.getJSONObject(0)
                .getString("user_id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("message"), apiResponseArray.getJSONObject(0)
                .getString("message"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("updated_at"), apiResponseArray
                .getJSONObject(0).getString("updated_at"));
        
    }
    
    /**
     * Test listEntries method with Optional Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateEntryWithMandatoryParameters" }, description = "canvas {listEntries} integration test with optional parameters.")
    public void testListEntriesWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listEntries");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listEntries_optional.json");
        JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/courses/"
                        + connectorProperties.getProperty("courseId") + "/discussion_topics/"
                        + connectorProperties.getProperty("topicId") + "/entry_list?ids[]="
                        + connectorProperties.getProperty("entryId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        // Only one entry should be returned.
        Assert.assertEquals(esbResponseArray.length(), 1);
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        
        // ID and user_id details
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("id"), apiResponseArray.getJSONObject(0)
                .getString("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("message"), apiResponseArray.getJSONObject(0)
                .getString("message"));
        
    }
    
    /**
     * Test listEntries method with Negative Case.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateEntryWithMandatoryParameters" }, description = "canvas {listEntries} integration test with negative case.")
    public void testListEntriesWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listEntries");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listEntries_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/courses/"
                        + connectorProperties.getProperty("courseId") + "/discussion_topics/"
                        + connectorProperties.getProperty("topicId") + "/entry_list?ids[]=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("message"),
                apiRestResponse.getBody().getJSONArray("errors").getJSONObject(0).getString("message"));
        
    }
    
    /**
     * Test deleteEntry method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListEntriesWithNegativeCase",
            "testUpdateEntryWithOptionalParameters" }, description = "canvas {deleteEntry} integration test with mandatory parameters.", priority = 2)
    public void testDeleteEntryWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteEntry");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api/v1/courses/"
                        + connectorProperties.getProperty("courseId") + "/discussion_topics/"
                        + connectorProperties.getProperty("topicId") + "/entry_list?ids[]="
                        + connectorProperties.getProperty("entryId");
        
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArrayBefore = new JSONArray(apiRestResponseBefore.getBody().getString("output"));
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteEntry_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArrayAfter = new JSONArray(apiRestResponseAfter.getBody().getString("output"));
        
        // Deleted key is not there in the response before the entry was deleted.
        Assert.assertEquals(apiResponseArrayBefore.getJSONObject(0).has("deleted"), false);
        // Deleted key is there with value 'true' in the response after the entry was deleted.
        Assert.assertEquals(apiResponseArrayAfter.getJSONObject(0).getBoolean("deleted"), true);
        
    }
    
}
