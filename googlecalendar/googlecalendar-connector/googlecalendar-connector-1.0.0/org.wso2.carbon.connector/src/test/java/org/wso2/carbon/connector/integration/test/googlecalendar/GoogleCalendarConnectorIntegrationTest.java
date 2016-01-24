/**
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

package org.wso2.carbon.connector.integration.test.googlecalendar;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

/**
 * Integration test class for Google Calendar connector.
 */
public class GoogleCalendarConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> parametersMap = new HashMap<String, String>();
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        
        init("googlecalendar-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("accessToken"));
        
    }
    
    /**
     * Positive test case for createCalendar method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "googlecalendar {createCalendar} integration test with mandatory parameters.")
    public void testCreateCalendarWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createCalendar");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCalendar_mandatory.txt");
        parametersMap.put("calendarId", esbRestResponse.getBody().getString("id"));
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().get("summary"), apiRestResponse.getBody().get("summary"));
        
    }
    
    /**
     * Positive test case for createCalendar method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "googlecalendar {createCalendar} integration test with optional parameters.")
    public void testCreateCalendarWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createCalendar");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCalendar_optional.txt");
        parametersMap.put("calendarId2", esbRestResponse.getBody().getString("id"));
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + esbRestResponse.getBody().getString("id");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("summary"), apiRestResponse.getBody().get("summary"));
        Assert.assertEquals(esbRestResponse.getBody().get("description"), apiRestResponse.getBody().get("description"));
        Assert.assertEquals(esbRestResponse.getBody().get("location"), apiRestResponse.getBody().get("location"));
        
    }
    
    /**
     * Negative test case for createCalendar method.
     */
    @Test(groups = { "wso2.esb" }, description = "googlecalendar {createCalendar} integration test with negative case.")
    public void testCreateCalendarWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createCalendar");
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars";
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCalendar_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createCalendar_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
        
    }
    
    /**
     * Positive test case for patchCalendar method with optional parameters.
     */
    @Test(dependsOnMethods = {"testCreateCalendarWithMandatoryParameters"}, groups = { "wso2.esb" }, description = "googlecalendar {patchCalendar} integration test with optional parameters.")
    public void testPatchCalendarWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:patchCalendar");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_patchCalendar_optional.txt", parametersMap);
        Thread.sleep(3000);
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "?fields=description,id,kind,location,summary,timeZone";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("timeZone"), apiRestResponse.getBody().get("timeZone"));
        Assert.assertEquals(esbRestResponse.getBody().get("summary"), apiRestResponse.getBody().get("summary"));
        
    }
    
    /**
     * Negative test case for patchCalendar method.
     */
    @Test(dependsOnMethods = {"testCreateCalendarWithMandatoryParameters"}, groups = { "wso2.esb" }, description = "googlecalendar {patchCalendar} integration test with negative case.")
    public void testPatchCalendarWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:patchCalendar");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_patchCalendar_negative.txt", parametersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), "Invalid Value");
        
    }
    
    /**
     * Positive test case for getCalendar method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testCreateCalendarWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {getCalendar} integration test with mandatory parameters.")
    public void testGetCalendarWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getCalendar");
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCalendar_mandatory.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().get("summary"), apiRestResponse.getBody().get("summary"));
        
    }
    
    /**
     * Positive test case for getCalendar method with optional parameters.
     */
    @Test(dependsOnMethods = {"testCreateCalendarWithMandatoryParameters"}, groups = { "wso2.esb" }, description = "googlecalendar {getCalendar} integration test with optional parameters.")
    public void testGetCalendarWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getCalendar");
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "?fields=etag,id,summary,timeZone";
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCalendar_optional.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("timeZone"), apiRestResponse.getBody().get("timeZone"));
        Assert.assertEquals(esbRestResponse.getBody().get("summary"), apiRestResponse.getBody().get("summary"));
        
    }
    
    /**
     * Negative test case for getCalendar method.
     */
    @Test(dependsOnMethods = {"testCreateCalendarWithMandatoryParameters"}, groups = { "wso2.esb" }, description = "googlecalendar {getCalendar} integration test with negative case.")
    public void testGetCalendarWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getCalendar");
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "?fields=id,TEST";
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCalendar_negative.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
        
    }
    
    /**
     * Positive test case for clearCalendar method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testClearCalendarWithNegativeCase"}, groups = { "wso2.esb" }, description = "googlecalendar {clearCalendar} integration test with mandatory parameters.")
    public void testClearCalendarWithMandatoryParameters() throws Exception {
        
        esbRequestHeadersMap.put("Action", "urn:clearCalendar");
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + connectorProperties.getProperty("emailAddress") + "/events";
        
        RestResponse<JSONObject> apiCreateEventRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createEvent_mandatory.txt");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_clearCalendar_mandatory.txt");
        
        apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + connectorProperties.getProperty("emailAddress") + "/events/" + apiCreateEventRestResponse.getBody().getString("id");
        RestResponse<JSONObject> apiGetEventRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiCreateEventRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiCreateEventRestResponse.getBody().get("status"), "confirmed");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiGetEventRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiGetEventRestResponse.getBody().get("status"), "cancelled");
        Assert.assertNotEquals(apiCreateEventRestResponse.getBody().get("sequence"), apiGetEventRestResponse.getBody().get("sequence"));
        
        Thread.sleep(5000);
        
    }
    
    /**
     * Negative test case for clearCalendar method.
     */
    @Test(dependsOnMethods = {"testCreateCalendarWithMandatoryParameters"}, priority = 3, groups = { "wso2.esb" }, description = "googlecalendar {clearCalendar} integration test with negative case.")
    public void testClearCalendarWithNegativeCase() throws Exception {
        
        esbRequestHeadersMap.put("Action", "urn:clearCalendar");
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/clear";
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_clearCalendar_negative.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
        Thread.sleep(5000);
        
    }
    
    /**
     * Positive test case for deleteCalendar method with mandatory parameters.
     */
    @Test(priority = 5, groups = { "wso2.esb" }, description = "googlecalendar {deleteCalendar} integration test with mandatory parameters.")
    public void testDeleteCalendarWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteCalendar");
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteCalendar_mandatory.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Thread.sleep(5000);
    }
    
    /**
     * Negative test case for deleteCalendar method.
     */
    @Test(priority = 5, groups = { "wso2.esb" }, description = "googlecalendar {deleteCalendar} integration test with negative case.")
    public void testDeleteCalendarWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteCalendar");
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/asd";
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteCalendar_negative.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
        Thread.sleep(5000);
    }
    
    /**
     * Positive test case for createCalendarEntry method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testCreateCalendarWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {createCalendarEntry} integration test with mandatory parameters.")
    public void testCreateCalendarEntryWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createCalendarEntry");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCalendarEntry_mandatory.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/users/me/calendarList/" + parametersMap.get("calendarId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().get("colorId"), apiRestResponse.getBody().get("colorId"));
        Assert.assertEquals(esbRestResponse.getBody().get("accessRole"), apiRestResponse.getBody().get("accessRole"));       
    }
    
    /**
     * Positive test case for createCalendarEntry method with optional parameters.
     */
    @Test(dependsOnMethods = {"testCreateCalendarWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {createCalendarEntry} integration test with optional parameters.")
    public void testCreateCalendarEntryWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createCalendarEntry");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCalendarEntry_optional.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/users/me/calendarList/" + parametersMap.get("calendarId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().get("colorId"), apiRestResponse.getBody().get("colorId"));
        Assert.assertEquals(esbRestResponse.getBody().get("backgroundColor"), apiRestResponse.getBody().get("backgroundColor"));
        Assert.assertEquals(esbRestResponse.getBody().get("foregroundColor"), apiRestResponse.getBody().get("foregroundColor"));
        Assert.assertEquals(esbRestResponse.getBody().get("summaryOverride"), apiRestResponse.getBody().get("summaryOverride"));
    }
    
    /**
     * Negative test case for createCalendarEntry method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {createCalendarEntry} integration test with negative case.")
    public void testCreateCalendarEntryWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createCalendarEntry");               
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/users/me/calendarList";               
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCalendarEntry_negative.txt", parametersMap);              
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createCalendarEntry_negative.txt", parametersMap);
                
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length()); 
        
    }

    /**
     * Positive test case for patchCalendarEntry method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testCreateCalendarWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {patchCalendarEntry} integration test with mandatory parameters.")
    public void testPatchCalendarEntryWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:patchCalendarEntry");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_patchCalendarEntry_mandatory.txt", parametersMap);
                
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/users/me/calendarList/" + parametersMap.get("calendarId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().get("colorId"), apiRestResponse.getBody().get("colorId"));
        Assert.assertEquals(esbRestResponse.getBody().get("accessRole"), apiRestResponse.getBody().get("accessRole"));     
    }   
    
     /**
     * Positive test case for patchCalendarEntry method with optional parameters.
     */
    @Test(dependsOnMethods = {"testCreateCalendarWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {patchCalendarEntry} integration test with optional parameters.")
    public void testPatchCalendarEntryWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:patchCalendarEntry");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_patchCalendarEntry_optional.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/users/me/calendarList/" + parametersMap.get("calendarId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().get("colorId"), apiRestResponse.getBody().get("colorId"));
        Assert.assertEquals(esbRestResponse.getBody().get("summaryOverride"), apiRestResponse.getBody().get("summaryOverride"));   
        
    }
    
    /**
     * Negative test case for patchCalendarEntry method.
     */
    @Test(dependsOnMethods = {"testCreateCalendarWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {patchCalendarEntry} integration test with negative case.")
    public void testPatchCalendarEntryWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:patchCalendarEntry");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_patchCalendarEntry_negative.txt", parametersMap);
               
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertTrue(esbRestResponse.getBody().getJSONObject("error").getString("message").contains("Invalid boolean value"));
        
    }  
    
    /**
     * Positive test case for getCalendarEntry method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testCreateCalendarWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {getCalendarEntry} integration test with mandatory parameters.")
    public void testGetCalendarEntryWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getCalendarEntry");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCalendarEntry_mandatory.txt", parametersMap);
                
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/users/me/calendarList/" + parametersMap.get("calendarId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("colorId"), apiRestResponse.getBody().get("colorId"));
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));     
    }   
    
    /**
     * Positive test case for getCalendarEntry method with optional parameters.
     */
    @Test(dependsOnMethods = {"testCreateCalendarWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {getCalendarEntry} integration test with optional parameters.")
    public void testGetCalendarEntryWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getCalendarEntry");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCalendarEntry_optional.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/users/me/calendarList/" + parametersMap.get("calendarId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("accessRole"), apiRestResponse.getBody().get("accessRole"));   
        
    }
    
    /**
     * Negative test case for getCalendarEntry method.
     */
    @Test(dependsOnMethods = {"testCreateCalendarWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {getCalendarEntry} integration test with negative case.")
    public void testGetCalendarEntryWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getCalendarEntry");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCalendarEntry_negative.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/users/me/calendarList/" + parametersMap.get("calendarId") + "?fields=xxx";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());       
    }

    /**
     * Positive test case for listCalendarEntries method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {listCalendarEntries} integration test with mandatory parameters.")
    public void testListCalendarEntriesWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listCalendarEntries");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCalendarEntries_mandatory.txt", parametersMap);
                        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/users/me/calendarList";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").length(), apiRestResponse.getBody().getJSONArray("items").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).get("id"), apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).get("summary"), apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).get("summary"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).get("accessRole"), apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).get("accessRole")); 
    }
    
    /**
     * Positive test case for listCalendarEntries method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {listCalendarEntries} integration test with optional parameters.")
    public void testListCalendarEntriesWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listCalendarEntries");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCalendarEntries_optional.txt", parametersMap);
    
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/users/me/calendarList?maxResults=20&minAccessRole=owner&showHidden=true&fields=kind,items,etag";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").length(), apiRestResponse.getBody().getJSONArray("items").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).get("id"), apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).get("summary"), apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).get("summary"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).get("accessRole"), apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).get("accessRole")); 
    }
    
    /**
     * Negative test case for listCalendarEntries method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {listCalendarEntries} integration test with negative case.")
    public void testListCalendarEntriesWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listCalendarEntries");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCalendarEntries_negative.txt", parametersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertTrue(esbRestResponse.getBody().getJSONObject("error").getString("message").contains("Invalid "));    
    }
    
    /**
     * Positive test case for deleteCalendarEntry method with mandatory parameters.
     */
    @Test(priority = 5, groups = { "wso2.esb" }, description = "googlecalendar {deleteCalendarEntry} integration test with mandatory parameters.")
    public void testDeleteCalendarEntryWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteCalendarEntry");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteCalendarEntry_mandatory.txt", parametersMap);
                
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/users/me/calendarList/" + parametersMap.get("calendarId2");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);       
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);   
        Thread.sleep(5000);
    }   
    
    /**
     * Negative test case for deleteCalendarEntry method.
     */
    @Test(priority = 5, groups = { "wso2.esb" }, description = "googlecalendar {deleteCalendarEntry} integration test with negative case.")
    public void testDeleteCalendarEntryWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteCalendarEntry");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteCalendarEntry_negative.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/users/me/calendarList/xxx";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());      
        Thread.sleep(5000);  
    }
    
    /**
     * Positive test case for createAcl method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testCreateCalendarWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {createAcl} integration test with mandatory parameters.")
    public void testCreateAclWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createAcl");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAcl_mandatory.txt", parametersMap);
        parametersMap.put("ruleId", esbRestResponse.getBody().getString("id"));
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/acl/" + parametersMap.get("ruleId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().get("role"), apiRestResponse.getBody().get("role"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("scope").get("value"), apiRestResponse.getBody().getJSONObject("scope").get("value"));
        
    }
    
    /**
     * Positive test case for createAcl method with optional parameters.
     */
    @Test(dependsOnMethods = {"testCreateCalendarWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {createAcl} integration test with optional parameters.")
    public void testCreateAclWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createAcl");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAcl_optional.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/acl/" + esbRestResponse.getBody().get("id");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().get("role"), apiRestResponse.getBody().get("role"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("scope").get("value"), apiRestResponse.getBody().getJSONObject("scope").get("value"));
        
    }
    
    /**
     * Negative test case for createAcl method.
     */
    @Test(dependsOnMethods = {"testCreateCalendarWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {createAcl} integration test with negative case.")
    public void testCreateAclWithNegativeParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createAcl");
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/acl";
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAcl_negative.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createAcl_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
        
    }
    
    /**
     * Positive test case for patchAcl method with optional parameters.
     */
    @Test(dependsOnMethods = {"testCreateAclWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {patchAcl} integration test with optional parameters.")
    public void testPatchAclWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:patchAcl");
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/acl/" + parametersMap.get("ruleId");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_patchAcl_optional.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().get("role"), apiRestResponse.getBody().get("role"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("scope").get("value"), apiRestResponse.getBody().getJSONObject("scope").get("value"));
        
    }
    
    /**
     * Negative test case for patchAcl method.
     */
    @Test(dependsOnMethods = {"testCreateCalendarWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {patchAcl} integration test with negative case.")
    public void testPatchAclWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:patchAcl");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_patchAcl_negative.txt", parametersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), "Invalid resource id value.");
        
    }
    
    /**
     * Positive test case for getAcl method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testCreateAclWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {getAcl} integration test with mandatory parameters.")
    public void testGetAclWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getAcl");
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/acl/" + parametersMap.get("ruleId");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAcl_mandatory.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().get("role"), apiRestResponse.getBody().get("role"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("scope").get("value"), apiRestResponse.getBody().getJSONObject("scope").get("value"));
        
    }
    
    /**
     * Positive test case for getAcl method with optional parameters.
     */
    @Test(dependsOnMethods = {"testCreateAclWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {getAcl} integration test with optional parameters.")
    public void testGetAclWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getAcl");
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/acl/" + parametersMap.get("ruleId") + "?fields=id,scope/value";
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAcl_optional.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("scope").get("value"), apiRestResponse.getBody().getJSONObject("scope").get("value"));
        
    }
    
    /**
     * Negative test case for getAcl method.
     */
    @Test(dependsOnMethods = {"testCreateAclWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {getAcl} integration test with negative case.")
    public void testGetAclWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getAcl");
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/acl/" + parametersMap.get("ruleId") + "?fields=id,TEST";
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAcl_negative.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
        
    }
    
    /**
     * Positive test case for listAcl method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testCreateAclWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {listAcl} integration test with mandatory parameters.")
    public void testListAclWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listAcl");
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/acl";
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAcl_mandatory.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").length(), apiRestResponse.getBody().getJSONArray("items").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).get("id"), apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).get("role"), apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).get("role"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).getJSONObject("scope").get("value"), apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).getJSONObject("scope").get("value"));
        
    }
    
    /**
     * Positive test case for listAcl method with optional parameters.
     */
    @Test(dependsOnMethods = {"testCreateAclWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {listAcl} integration test with optional parameters.")
    public void testListAclWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listAcl");
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/acl?fields=kind,items";
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAcl_optional.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").length(), apiRestResponse.getBody().getJSONArray("items").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).get("id"), apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).get("role"), apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).get("role"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).getJSONObject("scope").get("value"), apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).getJSONObject("scope").get("value"));
        
    }
    
    /**
     * Negative test case for listAcl method.
     */
    @Test(dependsOnMethods = {"testCreateAclWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {listAcl} integration test with negative case.")
    public void testListAclWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listAcl");
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/acl?fields=kind,TEST";
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAcl_negative.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
        
    }
    
    /**
     * Positive test case for deleteAcl method with mandatory parameters.
     */
    @Test(priority = 4, groups = { "wso2.esb" }, description = "googlecalendar {deleteAcl} integration test with mandatory parameters.")
    public void testDeleteAclWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteAcl");
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/acl/" + parametersMap.get("ruleId");
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteAcl_mandatory.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getBody().get("role"), "none");
        Thread.sleep(5000);
    }
    
    /**
     * Negative test case for deleteAcl method.
     */
    @Test(priority = 4, groups = { "wso2.esb" }, description = "googlecalendar {deleteAcl} integration test with negative case.")
    public void testDeleteAclWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteAcl");
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/acl/abc";
        
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteAcl_negative.txt", parametersMap);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
        Thread.sleep(5000);
    }
    
    /**
     * Positive test case for createEvent method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testCreateCalendarWithOptionalParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {createEvent} integration test with mandatory parameters.")
    public void testCreateEventWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createEvent");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEvent_mandatory.txt", parametersMap);
        parametersMap.put("eventId", esbRestResponse.getBody().getString("id"));
        parametersMap.put("iCalUID", esbRestResponse.getBody().getString("iCalUID"));
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events/" + parametersMap.get("eventId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().get("iCalUID"), apiRestResponse.getBody().get("iCalUID"));
        
    }
    
    /**
     * Positive test case for createEvent method with optional parameters.
     */
    @Test(dependsOnMethods = {"testCreateEventWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {createEvent} integration test with optional parameters.")
    public void testCreateEventWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createEvent");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEvent_optional.txt", parametersMap);
        parametersMap.put("eventId2", esbRestResponse.getBody().getString("id"));
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events/" + esbRestResponse.getBody().getString("id") + "?maxAttendees=10";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        
    }
    
    /**
     * Negative test case for createEvent method.
     */
    @Test(dependsOnMethods = {"testCreateEventWithOptionalParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {createEvent} integration test with negative case.")
    public void testCreateEventWithNegativeParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createEvent");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEvent_negative.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createEvent_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
        
    }
    
    /**
     * Positive test case for getEvent method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testCreateEventWithOptionalParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {getEvent} integration test with mandatory parameters.")
    public void testGetEventWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getEvent");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEvent_mandatory.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events/" + parametersMap.get("eventId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().get("iCalUID"), apiRestResponse.getBody().get("iCalUID"));
        
    }
    
    /**
     * Positive test case for getEvent method with optional parameters.
     */
    @Test(dependsOnMethods = {"testGetEventWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {getEvent} integration test with optional parameters.")
    public void testGetEventWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getEvent");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEvent_optional.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events/" + parametersMap.get("eventId") + "?alwaysIncludeEmail=true&maxAttendees=10";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().get("iCalUID"), apiRestResponse.getBody().get("iCalUID"));
        
    }
    
    /**
     * Negative test case for getEvent method.
     */
    @Test(dependsOnMethods = {"testGetEventWithOptionalParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {getEvent} integration test with negative case.")
    public void testGetEventWithNegativeParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getEvent");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEvent_negative.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events/sssss";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
        
    }
    
    /**
     * Positive test case for getEventInstances method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testCreateEventWithOptionalParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {getEventInstances} integration test with mandatory parameters.")
    public void testGetEventInstancesWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getEventInstances");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEventInstances_mandatory.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events/" + parametersMap.get("eventId") + "/instances";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "api_getEventInstances_mandatory.txt", parametersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().get("summary"), apiRestResponse.getBody().get("summary"));
        Assert.assertEquals(esbRestResponse.getBody().get("accessRole"), apiRestResponse.getBody().get("accessRole"));
        
    }
    
    /**
     * Positive test case for getEventInstances method with optional parameters.
     */
    @Test(dependsOnMethods = {"testGetEventInstancesWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {getEventInstances} integration test with optional parameters.")
    public void testGetEventInstancesWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getEventInstances");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEventInstances_optional.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events/" + parametersMap.get("eventId") + "/instances?alwaysIncludeEmail=true";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "api_getEventInstances_optional.txt", parametersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().get("summary"), apiRestResponse.getBody().get("summary"));
        Assert.assertEquals(esbRestResponse.getBody().get("accessRole"), apiRestResponse.getBody().get("accessRole"));
        
    }
    
    /**
     * Negative test case for getEventInstances method.
     */
    @Test(dependsOnMethods = {"testGetEventInstancesWithOptionalParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {getEventInstances} integration test with negative case.")
    public void testGetEventInstancesWithNegativeParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getEventInstances");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEventInstances_negative.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/sssss/events/sssss/instances";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
        
    }
    
    /**
     * Positive test case for getFreebusy method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testCreateCalendarWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {getFreebusy} integration test with mandatory parameters.")
    public void testGetFreebusyWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getFreebusy");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFreebusy_mandatory.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/freeBusy";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getFreebusy_mandatory.txt", parametersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().get("timeMin"), apiRestResponse.getBody().get("timeMin"));
        Assert.assertEquals(esbRestResponse.getBody().get("timeMax"), apiRestResponse.getBody().get("timeMax"));
        
    }
    
    /**
     * Positive test case for getFreebusy method with optional parameters.
     */
    @Test(dependsOnMethods = {"testGetFreebusyWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {getFreebusy} integration test with optional parameters.")
    public void testGetFreebusyWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getFreebusy");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFreebusy_optional.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/freeBusy";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getFreebusy_optional.txt", parametersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().get("timeMin"), apiRestResponse.getBody().get("timeMin"));
        Assert.assertEquals(esbRestResponse.getBody().get("timeMax"), apiRestResponse.getBody().get("timeMax"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("calendars").toString(), apiRestResponse.getBody().getJSONObject("calendars").toString());
        
    }
    
    /**
     * Negative test case for getFreebusy method.
     */
    @Test(dependsOnMethods = {"testGetFreebusyWithOptionalParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {getFreebusy} integration test with negative case.")
    public void testGetFreebusyWithNegativeParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getFreebusy");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFreebusy_negative.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/freeBusy";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_getFreebusy_negative.txt", parametersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
        
    }
    
    /**
     * Positive test case for importEvents method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testCreateEventWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {importEvents} integration test with mandatory parameters.")
    public void testImportEventsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:importEvents");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_importEvents_mandatory.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events/" + esbRestResponse.getBody().get("id");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().get("iCalUID"), apiRestResponse.getBody().get("iCalUID"));
        
    }
    
    /**
     * Positive test case for importEvents method with optional parameters.
     */
    @Test(dependsOnMethods = {"testImportEventsWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {importEvents} integration test with optional parameters.")
    public void testImportEventsWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:importEvents");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_importEvents_optional.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events/" + esbRestResponse.getBody().get("id");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("kind"), apiRestResponse.getBody().get("kind"));
        Assert.assertEquals(esbRestResponse.getBody().get("iCalUID"), apiRestResponse.getBody().get("iCalUID"));
        
    }
    
    /**
     * Negative test case for importEvents method.
     */
    @Test(dependsOnMethods = {"testImportEventsWithOptionalParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {importEvents} integration test with negative case.")
    public void testImportEventsWithNegativeParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:importEvents");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_importEvents_negative.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events/import";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_importEvents_negative.txt", parametersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
        
    }
    
    /**
     * Positive test case for deleteEvent method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testCreateEventWithMandatoryParameters"}, priority = 4, groups = { "wso2.esb" }, description = "googlecalendar {deleteEvent} integration test with mandatory parameters.")
    public void testDeleteEventWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteEvent");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteEvent_mandatory.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events/" + parametersMap.get("eventId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getBody().get("status"), "cancelled");
        Thread.sleep(5000);
    }
    
    /**
     * Positive test case for deleteEvent method with optional parameters.
     */
    @Test(dependsOnMethods = {"testCreateEventWithOptionalParameters"}, priority = 4, groups = { "wso2.esb" }, description = "googlecalendar {deleteEvent} integration test with optional parameters.")
    public void testDeleteEventWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteEvent");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteEvent_optional.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events/" + parametersMap.get("eventId2") + "?sendNotifications=true";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getBody().get("status"), "cancelled");
        Thread.sleep(5000);
    }
    
    /**
     * Negative test case for deleteEvent method.
     */
    @Test(priority = 4, groups = { "wso2.esb" }, description = "googlecalendar {deleteEvent} integration test with negative case.")
    public void testDeleteEventWithNegativeParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteEvent");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteEvent_negative.txt", parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events/ssssssssssssss";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
        parametersMap.remove("eventId");
        parametersMap.remove("eventId2");
        Thread.sleep(5000);
    }
    
    /**
     * Positive test case for createQuickAddEvents method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testCreateCalendarWithOptionalParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {createQuickAddEvents} integration test with mandatory parameters.")
    public void testCreateQuickAddEventsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createQuickAddEvents");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createQuickAddEvents_mandatory.txt" , parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events/" + esbRestResponse.getBody().getString("id");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("summary"), apiRestResponse.getBody().get("summary"));
        
    }
    
    /**
     * Positive test case for createQuickAddEvents method with optional parameters.
     */
    @Test(dependsOnMethods = {"testCreateQuickAddEventsWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {createQuickAddEvents} integration test with optional parameters.")
    public void testCreateQuickAddEventsWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createQuickAddEvents");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createQuickAddEvents_optional.txt" , parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events/" + esbRestResponse.getBody().getString("id");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("summary"), apiRestResponse.getBody().get("summary"));
        Assert.assertEquals(esbRestResponse.getBody().get("iCalUID"), apiRestResponse.getBody().get("iCalUID"));        
        
    }
    
    /**
     * Negative test case for createQuickAddEvents method.
     */
    @Test(dependsOnMethods = {"testCreateQuickAddEventsWithOptionalParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {createQuickAddEvents} integration test with negative parameters.")
    public void testCreateQuickAddEventsWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createQuickAddEvents");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createQuickAddEvents_negative.txt" , parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/sssss/events/quickAdd?text=testCreateQuickAddEventNegative";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());//specific values add
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));        
        
    }
    
    /**
     * Positive test case for listEvents method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testCreateCalendarWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {listEvents} integration test with mandatory parameters.")
    public void testListEventsWithMandatoryParameters() throws Exception {
        
        esbRequestHeadersMap.put("Action", "urn:listEvents");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listEvents_mandatory.txt" , parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
 
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").length(), apiRestResponse.getBody().getJSONArray("items").length());
        Assert.assertEquals(esbRestResponse.getBody().get("summary"), apiRestResponse.getBody().get("summary"));
        Assert.assertEquals(esbRestResponse.getBody().get("accessRole"), apiRestResponse.getBody().get("accessRole"));
        
    }
    
    /**
     * Positive test case for listEvents method with optional parameters.
     */
    @Test(dependsOnMethods = {"testListEventsWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {listEvents} integration test with optional parameters.")
    public void testListEventsWithOptionalParameters() throws Exception {
        
        esbRequestHeadersMap.put("Action", "urn:listEvents");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listEvents_optional.txt" , parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events?alwaysIncludeEmail=true";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
 
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("items").length(), apiRestResponse.getBody().getJSONArray("items").length());
        Assert.assertEquals(esbRestResponse.getBody().get("summary").toString(), apiRestResponse.getBody().get("summary").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("accessRole").toString(), apiRestResponse.getBody().get("accessRole").toString());
        
    }
    
    /**
     * Negative test case for listEvents method.
     */
    @Test(dependsOnMethods = {"testListEventsWithOptionalParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {listEvents} integration test with negative parameters.")
    public void testListEventsWithNegativeCase() throws Exception {
        
        esbRequestHeadersMap.put("Action", "urn:listEvents");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listEvents_ negative.txt" , parametersMap);
        
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/sssss/events";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());//specific values add
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        
    }
    
    /**
     * Positive test case for patchEvents method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testCreateEventWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {patchEvents} integration test with mandatory parameters.")
    public void testPatchEventsWithMandatoryParameters() throws Exception {
      
        esbRequestHeadersMap.put("Action", "urn:patchEvents");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_patchEvents_mandatory.txt" , parametersMap);
        
        // Get events from getEvent api call to compare against patched event
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events/" + parametersMap.get("eventId") ;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("organizer").get("email").toString(), apiRestResponse.getBody().getJSONObject("organizer").get("email").toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("creator").get("email").toString(), apiRestResponse.getBody().getJSONObject("creator").get("email").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("kind").toString(), apiRestResponse.getBody().get("kind").toString());
    }
    
    /**
     * Positive test case for patchEvents method with optional parameters.
     */
    @Test(dependsOnMethods = {"testCreateEventWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {patchEvents} integration test with optional parameters.")
    public void testPatchEventsWithOptionalParameters() throws Exception {
   
        esbRequestHeadersMap.put("Action", "urn:patchEvents");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_patchEvents_optional.txt" , parametersMap);
       
        // Get events from getEvent api call to compare against patched event
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events/" + parametersMap.get("eventId") + "?maxAttendees=10&alwaysIncludeEmail=true";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("iCalUID").toString(), apiRestResponse.getBody().get("iCalUID").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("summary").toString(), apiRestResponse.getBody().get("summary").toString());
        
    }
   
   
    /**
     * Negative test case for patchEvents method.
     */
    @Test(dependsOnMethods = {"testCreateEventWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {patchEvents} integration test with negative case.")
    public void testPatchEventsWithNegativeCase() throws Exception {
     
        esbRequestHeadersMap.put("Action", "urn:patchEvents");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_patchEvents_negative.txt" , parametersMap);
      
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), "Invalid field selection etagXX");
        Assert.assertTrue(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length() > 0);

    }
  
  
    /**
     * Positive test case for moveEvent method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testCreateEventWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {moveEvent} integration test with mandatory parameters.")
    public void testMoveEventWithMandatoryParameters() throws Exception {
     
        esbRequestHeadersMap.put("Action", "urn:moveEvent");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_moveEvent_mandatory.txt" , parametersMap);

        // Get events from getEvent api call to compare against patched event
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events/" + parametersMap.get("eventId") ;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("organizer").get("email").toString(), apiRestResponse.getBody().getJSONObject("organizer").get("email").toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("creator").get("email").toString(), apiRestResponse.getBody().getJSONObject("creator").get("email").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("kind").toString(), apiRestResponse.getBody().get("kind").toString());
        
    }
     
    /**
    * Positive test case for moveEvent method with optional parameters.
    */
    @Test(dependsOnMethods = {"testCreateEventWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {moveEvent} integration test with optional parameters.")
    public void testMoveEventWithOptionalParameters() throws Exception {
     
       esbRequestHeadersMap.put("Action", "urn:moveEvent");
       RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_moveEvent_optional.txt" , parametersMap);
       
       // Get events from getEvent api call to compare against patched event
       String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events/" + parametersMap.get("eventId");
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       Assert.assertEquals(esbRestResponse.getBody().get("iCalUID").toString(), apiRestResponse.getBody().get("iCalUID").toString());
       Assert.assertEquals(esbRestResponse.getBody().getJSONObject("end").get("dateTime").toString(), apiRestResponse.getBody().getJSONObject("end").get("dateTime").toString());
       Assert.assertEquals(esbRestResponse.getBody().getJSONObject("start").get("dateTime").toString(), apiRestResponse.getBody().getJSONObject("start").get("dateTime").toString());
       
    }   
    
    /**
     * Negative test case for moveEvent method.
     */
    @Test(dependsOnMethods = {"testCreateEventWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "googlecalendar {moveEvent} integration test with Negative case.")
    public void testMoveEventWithNegativeCase() throws Exception {
     
        esbRequestHeadersMap.put("Action", "urn:moveEvent");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_moveEvent_negative.txt" , parametersMap);
      
        String apiEndPoint = "https://www.googleapis.com/calendar/v3/calendars/" + parametersMap.get("calendarId") + "/events/" + parametersMap.get("eventId") + "/move?destination=" + parametersMap.get("calendarId2") + "&fields=creatorXX%2Csummary";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap , "api_moveEvent_negative.txt");     
      
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("code"), apiRestResponse.getBody().getJSONObject("error").get("code"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").get("message"), apiRestResponse.getBody().getJSONObject("error").get("message"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length(), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors").length());
      
    }
    
}
