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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.connector.integration.test.gotowebinar;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class GotowebinarConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private String apiRequestUrl;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("gotowebinar-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap
                .put("Authorization", "OAuth oauth_token=" + connectorProperties.getProperty("accessToken"));
        
        apiRequestUrl = connectorProperties.getProperty("apiUrl") + "/G2W/rest";
        
        // Validate Pre-requisites, if not Tests are skipped.
        if (!validate()) {
            Assert.fail("Pre-requisites mentioned in the Readme file are not accomplished in order to run this Test Suite.");
        }
        connectorProperties.setProperty("emailOpt", System.currentTimeMillis() + connectorProperties
                .getProperty("emailOpt"));
        connectorProperties.setProperty("email", System.currentTimeMillis() + connectorProperties
                .getProperty("email"));
    }
    
    /**
     * Method to validate whether pre-requisites are accomplished.
     * 
     * @return boolean validation status.
     */
    private boolean validate() throws IOException, JSONException {
    
        boolean isValidSession = false;
        boolean isAnyUpcomming = false;
        
        Calendar calendar = Calendar.getInstance();
        DateFormat isoTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        
        String toTime = isoTimeFormat.format(calendar.getTime());
        calendar.add(Calendar.YEAR, -1);
        String fromTime = isoTimeFormat.format(calendar.getTime());
        
        // Get all Historical webinars with in last year.
        String apiEndPoint =
                apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey")
                        + "/historicalWebinars?fromTime=" + fromTime + "&toTime=" + toTime;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray historicalWebinarArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        outerloop: for (int i = 0; i < historicalWebinarArray.length(); i++) {
            String webinarKey = historicalWebinarArray.getJSONObject(i).getString("webinarKey");
            
            // Get all session details which belongs to the listed webinar.
            apiEndPoint =
                    apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey") + "/webinars/"
                            + webinarKey + "/sessions";
            apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
            JSONArray webinarSessionArray = new JSONArray(apiRestResponse.getBody().getString("output"));
            
            for (int j = 0; j < webinarSessionArray.length(); j++) {
                
                int noOfRegistrants = webinarSessionArray.getJSONObject(j).getInt("registrantsAttended");
                
                // If session has one or more registrants all the required properties are set and loop will be
                // break
                if (noOfRegistrants > 0) {
                    String sessionKey = webinarSessionArray.getJSONObject(j).getString("sessionKey");
                    connectorProperties.put("sessionKey", sessionKey);
                    connectorProperties.put("webinarKey", webinarKey);
                    connectorProperties.put("fromTime", historicalWebinarArray.getJSONObject(i).getJSONArray("times")
                            .getJSONObject(0).getString("startTime"));
                    connectorProperties.put("toTime", historicalWebinarArray.getJSONObject(i).getJSONArray("times")
                            .getJSONObject(0).getString("endTime"));
                    isValidSession = true;
                    break outerloop;
                }
            }
            
        }
        
        // List all upcoming webinars
        apiEndPoint =
                apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey") + "/upcomingWebinars";
        apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        // If there are one or more upcoming webinars #upcommingWebinarKey property will be set
        if (apiResponseArray.length() > 0) {
            isAnyUpcomming = true;
            connectorProperties.put("upcommingWebinarKey", apiResponseArray.getJSONObject(0).getString("webinarKey"));
        }
        
        return (isValidSession && isAnyUpcomming);
    }
    
    /**
     * Test getWebinarById method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "gotowebinar {getWebinarById} integration test with mandatory parameters.")
    public void testGetWebinarByIdWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getWebinarById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getWebinarById_mandatory.json");
        
        String apiEndPoint =
                apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey") + "/webinars/"
                        + connectorProperties.getProperty("webinarKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("numberOfRegistrants"), esbRestResponse.getBody()
                .getString("numberOfRegistrants"));
        Assert.assertEquals(apiRestResponse.getBody().getString("subject"),
                esbRestResponse.getBody().getString("subject"));
        Assert.assertEquals(apiRestResponse.getBody().getString("timeZone"),
                esbRestResponse.getBody().getString("timeZone"));
        
    }
    
    /**
     * Test listSessions method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "gotowebinar {listSessions} integration test with mandatory parameters.")
    public void testListSessionsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listSessions");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSessions_mandatory.json");
        
        String apiEndPoint =
                apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey") + "/webinars/"
                        + connectorProperties.getProperty("webinarKey") + "/sessions";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("sessionKey"), esbResponseArray
                .getJSONObject(0).getString("sessionKey"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("registrantsAttended"), esbResponseArray
                .getJSONObject(0).getString("registrantsAttended"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("startTime"), esbResponseArray.getJSONObject(0)
                .getString("startTime"));
        
    }
    
    /**
     * Test getSessionById method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "gotowebinar {getSessionById} integration test with mandatory parameters.")
    public void testGetSessionByIdWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getSessionById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSessionById_mandatory.json");
        
        String apiEndPoint =
                apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey") + "/webinars/"
                        + connectorProperties.getProperty("webinarKey") + "/sessions/"
                        + connectorProperties.getProperty("sessionKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("email"), esbResponseArray.getJSONObject(0)
                .getString("email"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("attendanceTimeInSeconds"), esbResponseArray
                .getJSONObject(0).getString("attendanceTimeInSeconds"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("registrantKey"), esbResponseArray
                .getJSONObject(0).getString("registrantKey"));
        
    }
    
    /**
     * Test listSessionAttendees method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "gotowebinar {listSessionAttendees} integration test with mandatory parameters.")
    public void testListSessionAttendeesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listSessionAttendees");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSessionAttendees_mandatory.json");
        
        String apiEndPoint =
                apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey") + "/webinars/"
                        + connectorProperties.getProperty("webinarKey") + "/sessions/"
                        + connectorProperties.getProperty("sessionKey") + "/attendees";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("email"), esbResponseArray.getJSONObject(0)
                .getString("email"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("attendanceTimeInSeconds"), esbResponseArray
                .getJSONObject(0).getString("attendanceTimeInSeconds"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("registrantKey"), esbResponseArray
                .getJSONObject(0).getString("registrantKey"));
        
    }
    
    /**
     * Test createRegistrant method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "gotowebinar {createRegistrant} integration test with mandatory parameters.")
    public void testCreateRegistrantWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createRegistrant");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRegistrant_mandatory.json");
        if (esbRestResponse.getHttpStatusCode() == 409) {
            Assert.fail("The user is already registered.");
        }
        String registrantKey = esbRestResponse.getBody().getString("registrantKey");
        connectorProperties.setProperty("registrantKey", registrantKey);
        
        String apiEndPoint =
                apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey") + "/webinars/"
                        + connectorProperties.getProperty("upcommingWebinarKey") + "/registrants/"
                        + connectorProperties.getProperty("registrantKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("firstName"),
                connectorProperties.getProperty("firstName"));
        Assert.assertEquals(apiRestResponse.getBody().getString("lastName"),
                connectorProperties.getProperty("lastName"));
        Assert.assertEquals(apiRestResponse.getBody().getString("email"), connectorProperties.getProperty("email"));
        
    }
    
    /**
     * Test createRegistrant method with Optional Parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "gotowebinar {createRegistrant} integration test with optional parameters.")
    public void testCreateRegistrantWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createRegistrant");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRegistrant_optional.json");
        if (esbRestResponse.getHttpStatusCode() == 409) {
            Assert.fail("The user is already registered.");
        }
        
        String registrantKey = esbRestResponse.getBody().getString("registrantKey");
        
        String apiEndPoint =
                apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey") + "/webinars/"
                        + connectorProperties.getProperty("upcommingWebinarKey") + "/registrants/" + registrantKey;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("organization"),
                connectorProperties.getProperty("organization"));
        Assert.assertEquals(apiRestResponse.getBody().getString("industry"),
                connectorProperties.getProperty("industry"));
        Assert.assertEquals(apiRestResponse.getBody().getString("jobTitle"),
                connectorProperties.getProperty("jobTitle"));
        
    }
    
    /**
     * Test createRegistrant method with Negative case.
     */
    @Test(groups = { "wso2.esb" }, description = "gotowebinar {createRegistrant} integration test with negative case.")
    public void testCreateRegistrantWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createRegistrant");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRegistrant_negative.json");

        String apiEndPoint =
                apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey") + "/webinars/"
                        + connectorProperties.getProperty("invalidUpcommingWebinarKey") + "/registrants";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createRegistrant_negative.json");

        Assert.assertEquals(apiRestResponse.getBody().getString("errorCode"),
                esbRestResponse.getBody().getString("errorCode"));
        Assert.assertEquals(apiRestResponse.getBody().getString("description"),
                esbRestResponse.getBody().getString("description"));

    }
    
    /**
     * Test listRegistrants method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "gotowebinar {listRegistrants} integration test with mandatory parameters.")
    public void testListRegistrantsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listRegistrants");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listRegistrants_mandatory.json");
        
        String apiEndPoint =
                apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey") + "/webinars/"
                        + connectorProperties.getProperty("webinarKey") + "/registrants/";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("email"), esbResponseArray.getJSONObject(0)
                .getString("email"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("firstName"), esbResponseArray.getJSONObject(0)
                .getString("firstName"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("registrantKey"), esbResponseArray
                .getJSONObject(0).getString("registrantKey"));
        
    }
    
    /**
     * Test getRegistrantById method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateRegistrantWithMandatoryParameters" }, description = "gotowebinar {getRegistrantById} integration test with mandatory parameters.")
    public void testGetRegistrantByIdWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getRegistrantById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRegistrantById_mandatory.json");
        
        String apiEndPoint =
                apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey") + "/webinars/"
                        + connectorProperties.getProperty("upcommingWebinarKey") + "/registrants/"
                        + connectorProperties.getProperty("registrantKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("email"), esbRestResponse.getBody().getString("email"));
        Assert.assertEquals(apiRestResponse.getBody().getString("firstName"),
                esbRestResponse.getBody().getString("firstName"));
        Assert.assertEquals(apiRestResponse.getBody().getString("registrationDate"), esbRestResponse.getBody()
                .getString("registrationDate"));
        Assert.assertEquals(apiRestResponse.getBody().getString("status"), esbRestResponse.getBody()
                .getString("status"));
        
    }
    
    
    
    /**
     * Test listHistoricalWebinars method with Optional Parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "gotowebinar {listHistoricalWebinars} integration test with optional parameters.")
    public void testListHistoricalWebinarsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listHistoricalWebinars");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listHistoricalWebinars_optional.json");
        
        String apiEndPoint =
                apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey")
                        + "/historicalWebinars?fromTime=" + connectorProperties.getProperty("fromTime") + "&toTime="
                        + connectorProperties.getProperty("toTime");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("subject"), esbResponseArray.getJSONObject(0)
                .getString("subject"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("webinarKey"), esbResponseArray
                .getJSONObject(0).getString("webinarKey"));
        
    }
    
    /**
     * Test listHistoricalWebinars method with Negative case.
     */
    @Test(groups = { "wso2.esb" }, description = "gotowebinar {listHistoricalWebinars} integration test with negative case.")
    public void testListHistoricalWebinarsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listHistoricalWebinars");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listHistoricalWebinars_negative.json");
        
        String apiEndPoint =
                apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey")
                        + "/historicalWebinars?fromTime=" + connectorProperties.getProperty("fromTime")
                        + "&toTime=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(apiRestResponse.getBody().getString("errorCode"),
                esbRestResponse.getBody().getString("errorCode"));
        Assert.assertEquals(apiRestResponse.getBody().getString("description"),
                esbRestResponse.getBody().getString("description"));
        
    }
    
    /**
     * Test listUpcomingWebinars method with Mandatory Parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "gotowebinar {listUpcomingWebinars} integration test with mandatory parameters.")
    public void testListUpcomingWebinarsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listUpcomingWebinars");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUpcomingWebinars_mandatory.json");
        
        String apiEndPoint =
                apiRequestUrl + "/organizers/" + connectorProperties.getProperty("organizerKey") + "/upcomingWebinars";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(apiResponseArray.length(), esbResponseArray.length());
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("subject"), esbResponseArray.getJSONObject(0)
                .getString("subject"));
        Assert.assertEquals(apiResponseArray.getJSONObject(0).getString("registrationUrl"), esbResponseArray
                .getJSONObject(0).getString("registrationUrl"));
        Assert.assertEquals(
                apiResponseArray.getJSONObject(0).getJSONArray("times").getJSONObject(0).getString("startTime"),
                esbResponseArray.getJSONObject(0).getJSONArray("times").getJSONObject(0).getString("startTime"));
        
    }
    
}
