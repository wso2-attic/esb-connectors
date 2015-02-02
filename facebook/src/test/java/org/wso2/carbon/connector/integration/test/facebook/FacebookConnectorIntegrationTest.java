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

package org.wso2.carbon.connector.integration.test.facebook;

import java.io.IOException;
import java.util.Date;
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

public class FacebookConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> headersMap = new HashMap<String, String>();
    
    private long timeOut;
    
    private String multipartProxyUrl;
    
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("facebook");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.put("Accept-Charset", "UTF-8");
        apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");
        timeOut = Long.parseLong(connectorProperties.getProperty("timeOut"));
        
        String multipartPoxyName = connectorProperties.getProperty("multipartProxyName");
        
        multipartProxyUrl = getProxyServiceURL(multipartPoxyName);
        
    }
    
    /**
     * Positive test case for getEventDetails method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePageEventWithOptionalParameters" }, description = "facebook {getEventDetails} integration test with mandatory parameters.")
    public void testGetEventDetailsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getEventDetails");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("eventId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEventDetails_mandatory.txt");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("start_time"), apiRestResponse.getBody().get("start_time"));
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        
    }
    
    /**
     * Positive test case for getEventDetails method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetEventDetailsWithMandatoryParameters" }, description = "facebook {getEventDetails} integration test with optional parameters.")
    public void testGetEventDetailsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getEventDetails");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("eventId")
                        + "/?access_token=" + connectorProperties.getProperty("accessToken") + "&fields=owner";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEventDetails_optional.txt");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("owner").toString(), apiRestResponse.getBody().get("owner")
                .toString());
        
    }
    
    /**
     * Negative test case for getEventDetails method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetEventDetailsWithOptionalParameters" }, description = "facebook {getEventDetails} integration test with negative case.")
    public void testGetEventDetailsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getEventDetails");
        String apiEndPoint =
                "https://graph.facebook.com/Negative/?access_token=" + connectorProperties.getProperty("accessToken");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getEventDetails_negative.txt");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for updateEventTicketURI method with mandatory parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetEventDetailsWithNegativeCase" }, description = "facebook {updateEventTicketURI} integration test with mandatory parameters.")
    public void testUpdateEventTicketURIWithMandatoryParameters() throws IOException, JSONException,
            InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:updateEventTicketURI");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("eventId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken") + "&fields=ticket_uri";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        String originalTicketUri = "";
        if (apiRestResponse.getBody().has("ticket_uri")) {
            originalTicketUri = apiRestResponse.getBody().getString("ticket_uri");
        }
        Thread.sleep(timeOut);
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateEventTicketURI_mandatory.txt");
        
        apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String updatedTicketUri = "";
        
        if (apiRestResponse.getBody().has("ticket_uri")) {
            originalTicketUri = apiRestResponse.getBody().getString("ticket_uri");
        }
        
        Assert.assertNotEquals(originalTicketUri, updatedTicketUri);
        
    }
    
    /**
     * Negative test case for updateEventTicketURI.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateEventTicketURIWithMandatoryParameters" }, description = "facebook {updateEventTicketURI} integration test with negative case.")
    public void testUpdateEventTicketURIWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateEventTicketURI");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("eventId");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateEventTicketURI_negative.txt");
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateEventTicketURI_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error")
                .toString());
        
    }
    
    /**
     * Positive test case for createAttendingRSVP method with mandatory parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetEventDetailsWithMandatoryParameters" }, description = "facebook {createAttendingRSVP} integration test with mandatory parameters.")
    public void testCreateAttendingRSVPWithMandatoryParameters() throws IOException, JSONException,
            InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createAttendingRSVP");
        
        // This API call is to reset User attending status
        String apiResetEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("eventId") + "/declined";
        sendJsonRestRequest(apiResetEndPoint, "POST", apiRequestHeadersMap, "api_accessToken.txt");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRSVP_mandatory.txt");
        
        Thread.sleep(timeOut);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("eventId")
                        + "/attending?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray jsonArray = (JSONArray) apiRestResponse.getBody().get("data");
        boolean success = false;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject element = (JSONObject) jsonArray.get(i);
            
            if (element.get("id").toString().equals(connectorProperties.getProperty("userId"))) {
                success = true;
                break;
            }
            
        }
        
        Assert.assertTrue(success);
        
    }
    
    /**
     * Negative test case for createAttendingRSVP method .
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createAttendingRSVP} integration test with negative case.")
    public void testCreateAttendingRSVPWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAttendingRSVP");
        String apiEndPoint = "https://graph.facebook.com/Negative/attending";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRSVP_negative.txt");
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_accessToken.txt");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for createDeclinedRSVP method with mandatory parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetEventDetailsWithMandatoryParameters" }, description = "facebook {createDeclinedRSVP} integration test with mandatory parameters.")
    public void testCreateDeclinedRSVPWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createDeclinedRSVP");
        
        // This API call is to reset User attending status
        String apiResetEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("eventId") + "/attending";
        sendJsonRestRequest(apiResetEndPoint, "POST", apiRequestHeadersMap, "api_accessToken.txt");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRSVP_mandatory.txt");
        
        Thread.sleep(timeOut);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("eventId")
                        + "/declined?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray jsonArray = (JSONArray) apiRestResponse.getBody().get("data");
        boolean success = false;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject element = (JSONObject) jsonArray.get(i);
            
            if (element.get("id").toString().equals(connectorProperties.getProperty("userId"))) {
                success = true;
                break;
            }
            
        }
        
        Assert.assertTrue(success);
        
    }
    
    /**
     * Negative test case for createDeclinedRSVP method .
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createDeclinedRSVP} integration test with negative case.")
    public void testCreateDeclinedRSVPWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createDeclinedRSVP");
        String apiEndPoint = "https://graph.facebook.com/Negative/declined";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRSVP_negative.txt");
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_accessToken.txt");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for createMaybeRSVP method with mandatory parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetEventDetailsWithMandatoryParameters" }, description = "facebook {createMaybeRSVP} integration test with mandatory parameters.")
    public void testCreateMaybeRSVPWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createMaybeRSVP");
        
        // This API call is to reset User attending status
        String apiResetEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("eventId") + "/attending";
        sendJsonRestRequest(apiResetEndPoint, "POST", apiRequestHeadersMap, "api_accessToken.txt");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRSVP_mandatory.txt");
        
        Thread.sleep(timeOut);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("eventId")
                        + "/maybe?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray jsonArray = (JSONArray) apiRestResponse.getBody().get("data");
        boolean success = false;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject element = (JSONObject) jsonArray.get(i);
            
            if (element.get("id").toString().equals(connectorProperties.getProperty("userId"))) {
                success = true;
                break;
            }
            
        }
        
        Assert.assertTrue(success);
        
    }
    
    /**
     * Negative test case for createMaybeRSVP method .
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createMaybeRSVP} integration test with negative case.")
    public void testcreateMaybeRSVPWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createMaybeRSVP");
        String apiEndPoint = "https://graph.facebook.com/Negative/maybe";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createRSVP_negative.txt");
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_accessToken.txt");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for createEventInvitation method with mandatory parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetEventDetailsWithMandatoryParameters" }, description = "facebook {createEventInvitation} integration test with mandatory parameters.")
    public void testCreateEventInvitationWithMandatoryParameters() throws IOException, JSONException,
            InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createEventInvitation");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("eventId") + "/invited/"
                        + connectorProperties.getProperty("friendId") + "?access_token="
                        + connectorProperties.getProperty("accessToken");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        if (apiRestResponse.getBody().has("data") && apiRestResponse.getBody().getJSONArray("data").length() != 0) {
            String apiResetEndPoint =
                    connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("eventId")
                            + "/invited/" + connectorProperties.getProperty("friendId") + "?access_token="
                            + connectorProperties.getProperty("accessToken");
            sendJsonRestRequest(apiResetEndPoint, "DELETE", apiRequestHeadersMap);
        }
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEventInvitation_mandatory.txt");
        Thread.sleep(timeOut);
        apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertTrue(apiRestResponse.getBody().getJSONArray("data").length() != 0);
        
    }
    
    /**
     * Negative test case for createEventInvitation method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createEventInvitation} integration test with negative case.")
    public void testCreateEventInvitationWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createEventInvitation");
        String apiEndPoint = "https://graph.facebook.com/Negative/invited";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEventInvitation_negative.txt");
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createEventInvitation_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for deleteEventInvitation method with mandatory parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateEventInvitationWithMandatoryParameters" }, description = "facebook {deleteEventInvitation} integration test with mandatory parameters.")
    public void testDeleteEventInvitationWithMandatoryParameters() throws IOException, JSONException,
            InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteEventInvitation");
        String apiResetEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("eventId") + "/invited/"
                        + connectorProperties.getProperty("friendId") + "?access_token="
                        + connectorProperties.getProperty("accessToken");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteEventInvitation_mandatory.txt");
        Thread.sleep(timeOut);
        // This API call is to reset User invited status
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("eventId") + "/invited";
        sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createEventInvitation_mandatory.txt");
        Thread.sleep(timeOut);
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiResetEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("output").toString(), apiRestResponse.getBody().get("output")
                .toString());
        
    }
    
    /**
     * Negative test case for deleteEventInvitation method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {deleteEventInvitation} integration test with negative case.")
    public void testDeleteEventInvitationWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteEventInvitation");
        String apiEndPoint =
                "https://graph.facebook.com/Negative/invited/" + connectorProperties.getProperty("friendId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteEventInvitation_negative.txt");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for updateEvent method with optional parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetEventDetailsWithMandatoryParameters" }, description = "facebook {updateEvent} integration test with optional parameters.")
    public void testUpdateEventWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:updateEvent");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("eventId");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateEvent_optional.txt");
        Thread.sleep(timeOut);
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateEvent_optional.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("output").toString(), apiRestResponse.getBody().get("output")
                .toString());
        
    }
    
    /**
     * Negative test case for updateEvent method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {updateEvent} integration test with negative case.")
    public void testUpdateEventWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateEvent");
        String apiEndPoint = "https://graph.facebook.com/Negative";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateEvent_negative.txt");
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateEvent_optional.txt");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for createPostOnEventWall method with mandatory parameters. Some times direct call
     * giving an unexpected error.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetEventDetailsWithMandatoryParameters" }, description = "facebook {createPostOnEventWall} integration test with mandatory parameters.")
    public void testCreatePostOnEventWallWithMandatoryParameters() throws IOException, JSONException {
    
        try {
            esbRequestHeadersMap.put("Action", "urn:createPostOnEventWall");
            
            RestResponse<JSONObject> esbRestResponse =
                    sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                            "esb_createPostOnEventWall_mandatory.txt");
            
            esbRestResponse.getBody().get("id").toString();
            
            Thread.sleep(timeOut);
            
            String apiEndPoint =
                    connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("eventId") + "/feed";
            
            RestResponse<JSONObject> apiRestResponse =
                    sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                            "api_createPostOnEventWall_mandatory.txt");
            
            Assert.assertTrue(apiRestResponse.getBody().has("id") && esbRestResponse.getBody().has("id"));
            Assert.assertTrue(apiRestResponse.getBody().getString("id").contains("_")
                    && esbRestResponse.getBody().getString("id").contains("_"));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * Positive test case for createPostOnEventWall method with optional parameters. Some times direct call
     * giving an unexpected error.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteEventProfilePictureWithNegativeCase" }, description = "facebook {createPostOnEventWall} integration test with optional parameters.")
    public void testCreatePostOnEventWallWithOptionalParameters() throws IOException, JSONException,
            InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createPostOnEventWall");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPostOnEventWall_optional.txt");
        
        esbRestResponse.getBody().get("id").toString();
        
        Thread.sleep(timeOut);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("eventId") + "/feed";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createPostOnEventWall_optional.txt");
        
        Assert.assertTrue(apiRestResponse.getBody().has("id") && esbRestResponse.getBody().has("id"));
        Assert.assertTrue(apiRestResponse.getBody().getString("id").contains("_")
                && esbRestResponse.getBody().getString("id").contains("_"));
        
    }
    
    /**
     * Negative test case for createPostOnEventWall method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetEventDetailsWithMandatoryParameters" }, description = "facebook {createPostOnEventWall} integration test with negative.")
    public void testCreatePostOnEventWallWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPostOnEventWall");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("eventId") + "/feed";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPostOnEventWall_negative.txt");
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createPostOnEventWall_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for deleteEventProfilePicture method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetEventDetailsWithMandatoryParameters" }, description = "facebook {deleteEventProfilePicture} integration test with mandatory parameters.")
    public void testDeleteEventProfilePictureWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteEventProfilePicture");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("eventId")
                        + "/picture?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_deleteEventProfilePicture_mandatory.txt");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error")
                .toString());
        
    }
    
    /**
     * Negative test case for deleteEventProfilePicture method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteEventProfilePictureWithMandatoryParameters" }, description = "facebook {deleteEventProfilePicture} integration test with negative.")
    public void testDeleteEventProfilePictureWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteEventProfilePicture");
        
        String apiEndPoint =
                "https://graph.facebook.com/Negative/picture?access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_deleteEventProfilePicture_negative.txt");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for deleteEvent method with mandatory parameters.
     */
    @Test(priority = 3, groups = { "wso2.esb" }, description = "facebook {deleteEvent} integration test with mandatory parameters.")
    public void testDeleteEventWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteEvent");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("eventId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteEvent_mandatory.txt");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertTrue(apiRestResponse.getBody().has("error"));
        
    }
    
    /**
     * Negative test case for deleteEvent method.
     */
    @Test(priority = 3, groups = { "wso2.esb" }, description = "facebook {deleteEvent} integration test with nagative case.")
    public void testDeleteEventWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteEvent");
        
        String apiEndPoint =
                "https://graph.facebook.com/Negative?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteEvent_negative.txt");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error")
                .toString());
        
    }
    
    /**
     * Positive test case for getVideoDetails method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUploadVideoMandatoryParameters" }, description = "facebook {getVideoDetails} integration test with mandatory parameters.")
    public void testGetVideoDetailsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getVideoDetails");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getVideoDetails_mandatory.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("videoId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        
        Assert.assertEquals(esbRestResponse.getBody().get("source"), apiRestResponse.getBody().get("source"));
        
    }
    
    /**
     * Positive test case for getVideoDetails method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUploadVideoMandatoryParameters" }, description = "facebook {getVideoDetails} integration test with optional parameters.")
    public void testGetVideoDetailsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getVideoDetails");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getVideoDetails_optional.txt");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("videoId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken") + "&fields=from,picture";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("picture"), apiRestResponse.getBody().get("picture"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("from").get("name"), apiRestResponse.getBody()
                .getJSONObject("from").get("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("from").get("id"), apiRestResponse.getBody()
                .getJSONObject("from").get("id"));
        
    }
    
    /**
     * Negative test case for getVideoDetails method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getVideoDetails} integration test with negative.")
    public void testGetVideoDetailsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getVideoDetails");
        
        String apiEndPoint =
                "https://graph.facebook.com/Negative?access_token=" + connectorProperties.getProperty("accessToken")
                        + "&fields=from,picture";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getVideoDetails_negative.txt");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for createComment method with mandatory parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUploadVideoMandatoryParameters" }, description = "facebook {createComment} integration test with mandatory parameters.")
    public void testCreateCommentWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createComment");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_mandatory.txt");
        
        String commentId = esbRestResponse.getBody().get("id").toString();
        
        Thread.sleep(timeOut);
        connectorProperties.put("commentId", commentId);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + commentId + "?access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
        
    }
    
    /**
     * Negative test case for createComment method .
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUploadVideoMandatoryParameters" }, description = "facebook {createComment} integration test with negative case.")
    public void testCreateCommentWithNegativeCase() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createComment");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_negative.txt");
        
        Thread.sleep(timeOut);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("videoId") + "/comments";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_accessToken.txt");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for updateComment method with mandatory parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCommentWithMandatoryParameters" }, description = "facebook {updateComment} integration test with mandatory parameters.")
    public void testUpdateCommentWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:updateComment");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("commentId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String originalComment = "";
        if (apiRestResponse.getBody().has("message")) {
            originalComment = apiRestResponse.getBody().getString("message");
        }
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateComment_mandatory.txt");
        
        Thread.sleep(timeOut);
        
        apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String updatedComment = "";
        if (apiRestResponse.getBody().has("message")) {
            updatedComment = apiRestResponse.getBody().getString("message");
        }
        
        Assert.assertNotEquals(originalComment, updatedComment);
        
    }
    
    /**
     * Negative test case for updateComment method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateCommentWithMandatoryParameters" }, description = "facebook {updateComment} integration test with negative.")
    public void testUpdateCommentWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateComment");
        
        String apiEndPoint = "https://graph.facebook.com/Negative";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateComment_negative.txt");
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateComment_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for createLike method with mandatory parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCommentWithMandatoryParameters" }, description = "facebook {createLike} integration test with mandatory parameters.")
    public void testCreateLikeWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createLike");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("commentId")
                        + "/likes?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createLike_mandatory.txt");
        
        Thread.sleep(timeOut);
        
        apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertTrue(apiRestResponse.getBody().getJSONArray("data").length() != 0);
        
    }
    
    /**
     * Negative test case for CreateLike method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateLikeWithMandatoryParameters" }, description = "facebook {CreateLike} integration test with negative.")
    public void testCreateLikeWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createLike");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("commentId") + "/likes";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_accessToken.txt");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createLike_mandatory.txt");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for getCommentDetails method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateLikeWithMandatoryParameters" }, description = "facebook {getCommentDetails} integration test with mandatory parameters.")
    public void testGetCommentDetailsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getCommentDetails");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCommentDetails_mandatory.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("commentId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("from").get("name"), apiRestResponse.getBody()
                .getJSONObject("from").get("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("from").get("id"), apiRestResponse.getBody()
                .getJSONObject("from").get("id"));
        
    }
    
    /**
     * Positive test case for getCommentDetails method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetCommentDetailsWithMandatoryParameters" }, description = "facebook {getCommentDetails} integration test with optional parameters.")
    public void testGetCommentDetailsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getCommentDetails");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCommentDetails_optional.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("commentId")
                        + "/likes?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").length(), apiRestResponse.getBody()
                .getJSONArray("data").length());
        
    }
    
    /**
     * Negative test case for getCommentDetails method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetCommentDetailsWithOptionalParameters" }, description = "facebook {getCommentDetails} integration test with negative.")
    public void testGetCommentDetailsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getCommentDetails");
        
        String apiEndPoint = "https://graph.facebook.com/Negative/likes";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "api_accessToken.txt");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCommentDetails_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for deleteLike method with mandatory parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetCommentDetailsWithOptionalParameters" }, description = "facebook {deleteLike} integration test with mandatory parameters.")
    public void testDeleteLikeWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteLike");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createLike_mandatory.txt");
        
        Thread.sleep(timeOut);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("commentId")
                        + "/likes?access_token=" + connectorProperties.getProperty("accessToken");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertTrue(apiRestResponse.getBody().getJSONArray("data").length() == 0);
        
    }
    
    /**
     * Negative test case for deleteLike method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteLikeWithMandatoryParameters" }, description = "facebook {deleteLike} integration test with negative.")
    public void testDeleteLikeWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteLike");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("commentId") + "/likes";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap, "api_accessToken.txt");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createLike_mandatory.txt");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for deleteComment method with mandatory parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteLikeWithNegativeCase",
            "testUpdateCommentWithMandatoryParameters" }, description = "facebook {deleteComment} integration test with mandatory parameters.")
    public void testDeleteCommentWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteComment");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteComment_mandatory.txt");
        
        Thread.sleep(timeOut);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("commentId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertTrue(apiRestResponse.getBody().has("error"));
        
    }
    
    /**
     * Negative test case for deleteComment method .
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteCommentWithMandatoryParameters" }, description = "facebook {deleteComment} integration test with negative case.")
    public void testDeleteCommentWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteComment");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteComment_mandatory.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("commentId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for createUserNote method with mandatory parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createUserNote} integration test with mandatory parameters.")
    public void testCreateUserNoteWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createUserNote");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createUserNote_mandatory.txt");
        String noteId = esbRestResponse.getBody().get("id").toString();
        connectorProperties.put("noteId", noteId);
        Thread.sleep(timeOut);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("noteId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(noteId, apiRestResponse.getBody().get("id"));
        
    }
    
    /**
     * Negative test case for createUserNote method .
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateUserNoteWithMandatoryParameters" }, description = "facebook {createUserNote} integration test with negative case.")
    public void testCreateUserNoteWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createUserNote");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createUserNote_negative.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("userId") + "/notes";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_accessToken.txt");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for getNoteDetails method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateUserNoteWithNegativeCase" }, description = "facebook {getNoteDetails} integration test with mandatory parameters.")
    public void testGetNoteDetailsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getNoteDetails");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getNoteDetails_mandatory.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("noteId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        Assert.assertEquals(esbRestResponse.getBody().get("subject"), apiRestResponse.getBody().get("subject"));
        
    }
    
    /**
     * Positive test case for getNoteDetails method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateUserNoteWithNegativeCase" }, description = "facebook {getNoteDetails} integration test with optional parameters.")
    public void testGetNoteDetailsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getNoteDetails");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getNoteDetails_optional.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("noteId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken") + "&fields=id,created_time";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
        
    }
    
    /**
     * Negative test case for getNoteDetails method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetNoteDetailsWithOptionalParameters" }, description = "facebook {urn:getNoteDetails} integration test with negative case.")
    public void testGetNoteDetailsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getNoteDetails");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getNoteDetails_negative.txt");
        
        String apiEndPoint =
                "https://graph.facebook.com/Negative?access_token=" + connectorProperties.getProperty("accessToken");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for deleteNote method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetNoteDetailsWithNegativeCase" }, description = "facebook {deleteNote} integration test with mandatory parameters.")
    public void testDeleteNoteWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteNote");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteNote_mandatory.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("noteId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().has("error"));
        
    }
    
    /**
     * Negative test case for deleteNote method with Negative Case.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteNoteWithMandatoryParameters" }, description = "facebook {deleteNote} integration test with Negative Case.")
    public void testDeleteNoteWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteNote");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteNote_mandatory.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("noteId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for getAppAccessToken method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getAppAccessToken} integration test with mandatory parameters.")
    public void testgetAppAccessTokenWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAppAccessToken");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAppAccessToken_mandatory.txt");
        String appAccessToken = esbRestResponse.getBody().get("access_token").toString();
        
        connectorProperties.put("appAccessToken", appAccessToken);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("clientId")
                        + "?access_token=" + connectorProperties.getProperty("appAccessToken");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
        
    }
    
    /**
     * Negative test case for getAppAccessToken method .
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getAppAccessToken} integration test with negative case.")
    public void testgetAppAccessTokenWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAppAccessToken");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAppAccessToken_negative.txt");
        
        String apiEndPoint =
                "https://graph.facebook.com/oauth/access_token?client_id="
                        + connectorProperties.getProperty("clientId")
                        + "&client_secret=Negative&grant_type=client_credentials";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error")
                .toString());
        
    }
    
    /**
     * Positive test case for isFriend method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {isFriend} integration test with mandatory parameters.")
    public void testIsFriendWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:isFriend");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_isFriend_mandatory.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("sourceUserId")
                        + "/friends/" + connectorProperties.getProperty("targetUserId") + "?access_token="
                        + connectorProperties.getProperty("accessToken");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").length(), apiRestResponse.getBody()
                .getJSONArray("data").length());
        
    }
    
    /**
     * Positive test case for isFriend method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {isFriend} integration test with optional parameters.")
    public void testIsFriendWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:isFriend");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_isFriend_optional.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("sourceUserId")
                        + "/friends/" + connectorProperties.getProperty("targetUserId") + "?access_token="
                        + connectorProperties.getProperty("accessToken") + "&fields=name,gender,link";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("data");
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("data");
        
        if (esbResponseArray.length() != 0 && apiResponseArray.length() != 0) {
            Assert.assertEquals(((JSONObject) esbResponseArray.get(0)).get("name"),
                    ((JSONObject) apiResponseArray.get(0)).get("name"));
            Assert.assertEquals(((JSONObject) esbResponseArray.get(0)).get("gender"),
                    ((JSONObject) apiResponseArray.get(0)).get("gender"));
            Assert.assertEquals(((JSONObject) esbResponseArray.get(0)).get("link"),
                    ((JSONObject) apiResponseArray.get(0)).get("link"));
        } else {
            Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        }
        
    }
    
    /**
     * Negative test case for isFriend method .
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {isFriend} integration test with negative case.")
    public void testIsFriendWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:isFriend");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_isFriend_negative.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("sourceUserId")
                        + "/friends/Negative?access_token=" + connectorProperties.getProperty("accessToken");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for getMutualFriends method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getMutualFriends} integration test with mandatory parameters.")
    public void testGetMutualFriendsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getMutualFriends");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMutualFriends_mandatory.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("userAId")
                        + "/mutualfriends/" + connectorProperties.getProperty("userBId") + "?access_token="
                        + connectorProperties.getProperty("accessToken");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").length(), apiRestResponse.getBody()
                .getJSONArray("data").length());
        
    }
    
    /**
     * Positive test case for getMutualFriends method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getMutualFriends} integration test with optional parameters.")
    public void testGetMutualFriendsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getMutualFriends");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMutualFriends_optional.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("userAId")
                        + "/mutualfriends/" + connectorProperties.getProperty("userBId") + "?access_token="
                        + connectorProperties.getProperty("accessToken") + "&fields=name,gender,link";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("data");
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("data");
        
        if (esbResponseArray.length() != 0 && apiResponseArray.length() != 0) {
            Assert.assertEquals(((JSONObject) esbResponseArray.get(0)).get("name"),
                    ((JSONObject) apiResponseArray.get(0)).get("name"));
            Assert.assertEquals(((JSONObject) esbResponseArray.get(0)).get("gender"),
                    ((JSONObject) apiResponseArray.get(0)).get("gender"));
            Assert.assertEquals(((JSONObject) esbResponseArray.get(0)).get("link"),
                    ((JSONObject) apiResponseArray.get(0)).get("link"));
        } else {
            Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        }
        
    }
    
    /**
     * Negative test case for getMutualFriends method .
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getMutualFriends} integration test with negative case.")
    public void testGetMutualFriendsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getMutualFriends");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMutualFriends_negative.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("userAId")
                        + "/mutualfriends/Negative?access_token=" + connectorProperties.getProperty("accessToken");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for publishNotification method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testgetAppAccessTokenWithMandatoryParameters" }, description = "facebook {publishNotification} integration test with mandatory parameters.")
    public void testPublishNotificationWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:publishNotification");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_publishNotification_mandatory.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("userId")
                        + "/notifications/?access_token=" + connectorProperties.getProperty("accessToken");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray jsonArray = apiRestResponse.getBody().getJSONArray("data");
        
        boolean success = false;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject element = (JSONObject) jsonArray.get(i);
            JSONObject application = (JSONObject) element.get("application");
            
            if (application.get("id").toString().equals(connectorProperties.getProperty("clientId"))
                    && element.get("title").toString().contains(connectorProperties.getProperty("template"))) {
                success = true;
                break;
            }
            
        }
        
        Assert.assertTrue(success);
        
    }
    
    /**
     * Positive test case for publishNotification method with optional parameters.
     * @throws InterruptedException 
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testPublishNotificationWithMandatoryParameters" }, description = "facebook {publishNotification} integration test with optional parameters.")
    public void testPublishNotificationWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:publishNotification");
        Thread.sleep(timeOut);
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_publishNotification_optional.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("userId")
                        + "/notifications/?access_token=" + connectorProperties.getProperty("accessToken");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray jsonArray = apiRestResponse.getBody().getJSONArray("data");
        
        boolean success = false;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject element = (JSONObject) jsonArray.get(i);
            JSONObject application = (JSONObject) element.get("application");
            
            if (application.get("id").toString().equals(connectorProperties.getProperty("clientId"))
                    && element.get("title").toString().contains(connectorProperties.getProperty("template"))
                    && element.get("link").toString().contains(connectorProperties.getProperty("href"))) {
                success = true;
                break;
            }
            
        }
        
        Assert.assertTrue(success);
        
    }
    
    /**
     * Negative test case for publishNotification method .
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testPublishNotificationWithOptionalParameters" }, description = "facebook {publishNotification} integration test with negative case.")
    public void testPublishNotificationWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:publishNotification");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_publishNotification_negative.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("userAId")
                        + "/notifications";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_appAccessToken.txt");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for createAppAchievements method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testgetAppAccessTokenWithMandatoryParameters" }, description = "facebook {createAppAchievements} integration test with mandatory parameters.")
    public void testCreateAppAchievementsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAppAchievements");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("clientId")
                        + "/achievements?access_token=" + connectorProperties.getProperty("appAccessToken")
                        + "&achievement=" + connectorProperties.getProperty("achievementURL");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAppAchievements_mandatory.txt");
        
        apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("clientId")
                        + "/achievements?access_token=" + connectorProperties.getProperty("appAccessToken");
        
        apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray jsonArray = apiRestResponse.getBody().getJSONArray("data");
        
        boolean success = false;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject element = (JSONObject) jsonArray.get(i);
            JSONObject application = (JSONObject) element.get("application");
            
            if (application.get("id").toString().equals(connectorProperties.getProperty("clientId"))
                    && element.get("url").toString().equals(connectorProperties.getProperty("achievementURL"))) {
                success = true;
                break;
            }
            
        }
        
        Assert.assertTrue(success);
        
    }
    
    /**
     * Positive test case for createAppAchievements method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAppAchievementsWithMandatoryParameters" }, description = "facebook {createAppAchievements} integration test with optional parameters.")
    public void testCreateAppAchievementsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAppAchievements");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAppAchievements_optional.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("clientId")
                        + "/achievements?access_token=" + connectorProperties.getProperty("appAccessToken");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray jsonArray = apiRestResponse.getBody().getJSONArray("data");
        
        boolean success = false;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject element = (JSONObject) jsonArray.get(i);
            JSONObject application = (JSONObject) element.get("application");
            JSONObject context = (JSONObject) element.get("context");
            if (application.get("id").toString().equals(connectorProperties.getProperty("clientId"))
                    && element.get("url").toString().contains(connectorProperties.getProperty("achievementURL"))
                    && context.get("display_order").toString().equals(connectorProperties.getProperty("displayOrder"))) {
                success = true;
                break;
            }
            
        }
        
        Assert.assertTrue(success);
        
    }
    
    /**
     * Negative test case for createAppAchievements method .
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAppAchievementsWithMandatoryParameters" }, description = "facebook {createAppAchievements} integration test with negative case.")
    public void testCreateAppAchievementsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAppAchievements");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAppAchievements_negative.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("clientId")
                        + "/achievements";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createAppAchievements_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for deleteAppAchievements method with mandatory parameters.
     */
    @Test(priority = 3, groups = { "wso2.esb" }, description = "facebook {deleteAppAchievements} integration test with mandatory parameters.")
    public void testDeleteAppAchievementsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteAppAchievements");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteAppAchievements_mandatory.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("clientId")
                        + "/achievements?access_token=" + connectorProperties.getProperty("appAccessToken")
                        + "&achievement=" + connectorProperties.getProperty("achievementURL");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertTrue(apiRestResponse.getBody().has("error"));
        
    }
    
    /**
     * Negative test case for deleteAppAchievements method .
     */
    @Test(priority = 3, groups = { "wso2.esb" }, description = "facebook {deleteAppAchievements} integration test with negative case.")
    public void testDeleteAppAchievementsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteAppAchievements");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteAppAchievements_negative.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("clientId")
                        + "/achievements?access_token=" + connectorProperties.getProperty("appAccessToken")
                        + "&achievement=www.invalidAchievement.com/invalid";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for createUserAchievement method with mandatory parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAppAchievementsWithMandatoryParameters" }, description = "facebook {createUserAchievement} integration test with mandatory parameters.")
    public void testCreateUserAchievementWithMandatoryParameters() throws IOException, JSONException,
            InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createUserAchievement");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createUserAchievements_mandatory.txt");
        
        String achievementId = esbRestResponse.getBody().get("id").toString();
        Thread.sleep(timeOut);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + achievementId + "?access_token="
                        + connectorProperties.getProperty("accessToken");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
        
    }
    
    /**
     * Negative test case for createUserAchievement method .
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createUserAchievement} integration test with negative case.")
    public void testCreateUserAchievementWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createUserAchievement");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createUserAchievements_negative.txt");
        
        String apiEndPoint = "https://graph.facebook.com/me/achievements";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                        "api_createUserAchievements_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for deleteUserAchievement method with mandatory parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "facebook {deleteUserAchievement} integration test with mandatory parameters.")
    public void testDeleteUserAchievementWithMandatoryParameters() throws IOException, JSONException,
            InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteUserAchievement");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteUserAchievement_mandatory.txt");
        
        Thread.sleep(timeOut);
        String apiEndPoint =
                "https://graph.facebook.com/me/achievements?access_token="
                        + connectorProperties.getProperty("accessToken") + "&achievement="
                        + connectorProperties.getProperty("achievementURL");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertTrue(apiRestResponse.getBody().has("error"));
        
    }
    
    /**
     * Negative test case for deleteUserAchievement method .
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "facebook {createUserAchievement} integration test with negative case.")
    public void testDeleteUserAchievementWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteUserAchievement");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteUserAchievement_negative.txt");
        
        String apiEndPoint =
                "https://graph.facebook.com/me/achievements?access_token="
                        + connectorProperties.getProperty("accessToken") + "&achievement=www.negativeachievement.com/";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for createAppSubscription method with mandatory parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testgetAppAccessTokenWithMandatoryParameters" }, description = "facebook {createAppSubscription} integration test with mandatory parameters.")
    public void testCreateAppSubscriptionWithMandatoryParameters() throws IOException, JSONException,
            InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createAppSubscription");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAppSubscription_mandatory.txt");
        
        Thread.sleep(timeOut);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("appId")
                        + "/subscriptions?access_token=" + connectorProperties.getProperty("appAccessToken");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray jsonArray = apiRestResponse.getBody().getJSONArray("data");
        boolean succsess = false;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject element = jsonArray.getJSONObject(i);
            if (element.getString("callback_url").equals(connectorProperties.getProperty("callbackURL"))
                    && element.getString("object").equals(connectorProperties.getProperty("subscriptionObject"))) {
                succsess = true;
                break;
                
            }
        }
        Assert.assertTrue(succsess);
        
    }
    
    /**
     * Negative test case for createAppSubscription method .
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testgetAppAccessTokenWithMandatoryParameters" }, description = "facebook {createAppSubscription} integration test with negative case.")
    public void testCreateAppSubscriptionWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAppSubscription");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAppSubscription_negative.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("appId") + "/subscriptions";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createAppSubscription_negative.txt");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for deleteAppSubscription method with mandatory parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAppSubscriptionWithMandatoryParameters" }, description = "facebook {deleteAppSubscription} integration test with mandatory parameters.")
    public void testDeleteAppSubscriptionWithMandatoryParameters() throws IOException, JSONException,
            InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteAppSubscription");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteAppSubscription_mandatory.txt");
        
        Thread.sleep(timeOut);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("appId")
                        + "/subscriptions?access_token=" + connectorProperties.getProperty("appAccessToken");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray jsonArray = apiRestResponse.getBody().getJSONArray("data");
        boolean isDeleted = true;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject element = jsonArray.getJSONObject(i);
            if (element.getString("callback_url").equals(connectorProperties.getProperty("callbackURL"))
                    && element.getString("object").equals(connectorProperties.getProperty("subscriptionObject"))) {
                isDeleted = false;
                break;
                
            }
        }
        Assert.assertTrue(isDeleted);
        
    }
    
    /**
     * Negative test case for deleteAppSubscription method .
     */
    @Test(priority = 2, groups = { "wso2.esb" }, dependsOnMethods = { "testgetAppAccessTokenWithMandatoryParameters" }, description = "facebook {deleteAppSubscription} integration test with negative case.")
    public void testDeleteAppSubscriptionWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteAppSubscription");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteAppSubscription_negative.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("appId")
                        + "/subscriptions?object=negative&access_token="
                        + connectorProperties.getProperty("appAccessToken");;
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for getAppDetails method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testgetAppAccessTokenWithMandatoryParameters" }, description = "facebook {getAppDetails} integration test with mandatory parameters.")
    public void testGetAppDetailsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAppDetails");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("appId") + "?access_token="
                        + connectorProperties.getProperty("appAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAppDetails_mandatory.txt");
        
        Assert.assertEquals(esbRestResponse.getBody().get("category"), apiRestResponse.getBody().get("category"));
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        
    }
    
    /**
     * Positive test case for getEventDetails method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testgetAppAccessTokenWithMandatoryParameters" }, description = "facebook {getAppDetails} integration test with optional parameters.")
    public void testGetAppDetailsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAppDetails");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("appId")
                        + "/?access_token=" + connectorProperties.getProperty("appAccessToken") + "&fields=app_name";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAppDetails_optional.txt");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("app_name").toString(),
                apiRestResponse.getBody().get("app_name").toString());
        
    }
    
    /**
     * Negative test case for getAppDetails method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testgetAppAccessTokenWithMandatoryParameters" }, description = "facebook {getAppDetails} integration test with negative case.")
    public void testGetAppDetailsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAppDetails");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("invalid")
                        + "/?access_token=" + connectorProperties.getProperty("appAccessToken");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAppDetails_negative.txt");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for banAppUser method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testgetAppAccessTokenWithMandatoryParameters" }, description = "facebook {banAppUser} integration test with optional parameters.")
    public void testBanAppUserWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:banAppUser");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("appId") + "/banned";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_banAppUser_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_banAppUser_mandatory.txt");
        Assert.assertEquals(esbRestResponse.getBody().get("output").toString(), apiRestResponse.getBody().get("output")
                .toString());
        
    }
    
    /**
     * Negative test case for banAppUser method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testgetAppAccessTokenWithMandatoryParameters" }, description = "facebook {banAppUser} integration test with negative case.")
    public void testBanAppUserWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:banAppUser");
        String apiEndPoint = "https://graph.facebook.com/invalid/banned";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_banAppUser_negative.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_banAppUser_mandatory.txt");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for isAppUserBanned method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testBanAppUserWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "facebook {isAppUserBanned} integration test with mandatory parameters.")
    public void testIsAppUserBannedWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:isAppUserBanned");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("appId") + "/banned/"
                        + connectorProperties.getProperty("appUserId") + "?access_token="
                        + connectorProperties.getProperty("appAccessToken");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_isAppUserBanned_mandatory.txt");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("data").getString(0), apiRestResponse.getBody()
                .getJSONArray("data").getString(0));
        
    }
    
    /**
     * Negative test case for isAppUserBanned method.
     */
    @Test(priority = 1, dependsOnMethods = { "testBanAppUserWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "facebook {isAppUserBanned} integration test with negative case.")
    public void testIsAppUserBannedWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:isAppUserBanned");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("appId")
                        + "/banned/invalid" + "?access_token=" + connectorProperties.getProperty("appAccessToken");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_isAppUserBanned_negative.txt");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for unbanAppUser method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testBanAppUserWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "facebook {unbanAppUser} integration test with mandatory parameters.")
    public void testUnbanAppUserWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:unbanAppUser");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("appId") + "/banned/"
                        + connectorProperties.getProperty("appUserId") + "?access_token="
                        + connectorProperties.getProperty("appAccessToken");
        
        RestResponse<JSONObject> apiRestResponse1 = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_unbanAppUser_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse2 = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertNotEquals(apiRestResponse1.getBody().getJSONArray("data"), apiRestResponse2.getBody()
                .getJSONArray("data"));
        
    }
    
    /**
     * Negative test case for unbanAppUser method.
     */
    @Test(priority = 1, dependsOnMethods = { "testBanAppUserWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "facebook {unbanAppUser} integration test with negative case.")
    public void testUnbanAppUserWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:unbanAppUser");
        String apiEndPoint =
                "https://graph.facebook.com/invalid/banned/" + connectorProperties.getProperty("appUserId")
                        + "?access_token=" + connectorProperties.getProperty("appAccessToken");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_unbanAppUser_negative.txt");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for createAppUserGroup method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testgetAppAccessTokenWithMandatoryParameters" }, description = "facebook {createAppUserGroup} integration test with optional parameters.")
    public void testCreateAppUserGroupWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAppUserGroup");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAppUserGroup_mandatory.txt");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + esbRestResponse.getBody().get("id").toString()
                        + "?access_token=" + connectorProperties.getProperty("appAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        connectorProperties.put("groupId", esbRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(apiRestResponse.getBody().get("name").toString(),
                connectorProperties.getProperty("groupName"));
        
    }
    
    /**
     * Positive test case for createAppUserGroup method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testgetAppAccessTokenWithMandatoryParameters" }, description = "facebook {createAppUserGroup} integration test with optional parameters.")
    public void testCreateAppUserGroupWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAppUserGroup");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAppUserGroup_optional.txt");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + esbRestResponse.getBody().get("id").toString()
                        + "?access_token=" + connectorProperties.getProperty("appAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(apiRestResponse.getBody().get("name").toString(),
                connectorProperties.getProperty("groupName"));
        Assert.assertEquals(apiRestResponse.getBody().get("description").toString(),
                connectorProperties.getProperty("description"));
        
    }
    
    /**
     * Negative test case for createAppUserGroup method.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateAppUserGroupWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "facebook {createAppUserGroup} integration test with negative case.")
    public void testCreateAppUserGroupWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAppUserGroup");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAppUserGroup_negative.txt");
        String apiEndPoint = "https://graph.facebook.com/invalid/groups";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for deleteAppUserGroup method with mandatory parameters.
     */
    @Test(priority = 3, groups = { "wso2.esb" }, description = "facebook {deleteAppUserGroup} integration test with mandatory parameters.")
    public void testDeleteAppUserGroupWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteAppUserGroup");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("groupId")
                        + "?access_token=" + connectorProperties.getProperty("appAccessToken");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteAppUserGroup_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().has("error"));
        
    }
    
    /**
     * Negative test case for deleteAppUserGroup method.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateAppUserGroupWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "facebook {deleteAppUserGroup} integration test with negative case.")
    public void testDeleteAppUserGroupWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteAppUserGroup");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteAppUserGroup_negative.txt");
        String apiEndPoint = "https://graph.facebook.com/invalid/groups";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for getPageDetails method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getPageDetails} integration test with mandatory parameters.")
    public void testGetPageDetailsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getPageDetails");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("pageId")
                        + "?access_token=" + connectorProperties.getProperty("pageAccessToken");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPageDetails_mandatory.txt");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("category"), apiRestResponse.getBody().get("category"));
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        
    }
    
    /**
     * Positive test case for getPageDetails method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getPageDetails} integration test with optional parameters.")
    public void testGetPageDetailsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getPageDetails");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("pageId")
                        + "/?access_token=" + connectorProperties.getProperty("pageAccessToken") + "&fields=category";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPageDetails_optional.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("category").toString(),
                apiRestResponse.getBody().get("category").toString());
        
    }
    
    /**
     * Negative test case for getPageDetails method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getPageDetails} integration test with negative case.")
    public void testGetPageDetailsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getPageDetails");
        String apiEndPoint =
                "https://graph.facebook.com/invalid?access_token=" + connectorProperties.getProperty("pageAccessToken");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPageDetails_negative.txt");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for createPageAlbum method with mandatory parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createPageAlbum} integration test with optional parameters.")
    public void testCreatePageAlbumWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createPageAlbum");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPageAlbum_mandatory.txt");
        Thread.sleep(timeOut);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + esbRestResponse.getBody().get("id").toString()
                        + "?access_token=" + connectorProperties.getProperty("pageAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(apiRestResponse.getBody().get("name").toString(),
                connectorProperties.getProperty("albumName"));
        
    }
    
    /**
     * Positive test case for createPageAlbum method with optional parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createPageAlbum} integration test with optional parameters.")
    public void testCreatePageAlbumWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createPageAlbum");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPageAlbum_optional.txt");
        Thread.sleep(timeOut);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + esbRestResponse.getBody().get("id").toString()
                        + "?access_token=" + connectorProperties.getProperty("pageAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(apiRestResponse.getBody().get("name").toString(),
                connectorProperties.getProperty("albumName"));
        Assert.assertEquals(apiRestResponse.getBody().get("description").toString(),
                connectorProperties.getProperty("message"));
    }
    
    /**
     * Negative test case for createPageAlbum method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createPageAlbum} integration test with negative case.")
    public void testCreatePageAlbumWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPageAlbum");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPageAlbum_negative.txt");
        String apiEndPoint =
                "https://graph.facebook.com/invalid/albums?access_token="
                        + connectorProperties.getProperty("pageAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for blockUserFromPage method with mandatory parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {blockUserFromPage} integration test with optional parameters.")
    public void testBlockUserFromPageWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:blockUserFromPage");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("pageId")
                        + "/blocked?access_token=" + connectorProperties.getProperty("pageAccessToken");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_blockUserFromPage_mandatory.txt");
        
        Thread.sleep(timeOut);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject response = (JSONObject) apiRestResponse.getBody().getJSONArray("data").get(0);
        Assert.assertEquals(response.get("id").toString(), connectorProperties.getProperty("pageUsreId"));
        
    }
    
    /**
     * Negative test case for blockUserFromPage method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {blockUserFromPage} integration test with negative case.")
    public void testBlockUserFromPageWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:blockUserFromPage");
        String apiEndPoint = "https://graph.facebook.com/invalid/blocked";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_blockUserFromPage_negative.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_blockUserFromPage_mandatory.txt");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for unblockUserFromPage method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testBlockUserFromPageWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "facebook {unblockUserFromPage} integration test with mandatory parameters.")
    public void testUnblockUserFromPageWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:unblockUserFromPage");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("pageId") + "/blocked/"
                        + connectorProperties.getProperty("appUserId") + "?access_token="
                        + connectorProperties.getProperty("pageAccessToken");
        
        RestResponse<JSONObject> apiRestResponse1 = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_unblockUserFromPage_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse2 = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertNotEquals(apiRestResponse1.getBody().getJSONArray("data"), apiRestResponse2.getBody()
                .getJSONArray("data"));
        
    }
    
    /**
     * Negative test case for unblockUserFromPage method.
     */
    @Test(priority = 1, dependsOnMethods = { "testBlockUserFromPageWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "facebook {unblockUserFromPage} integration test with negative case.")
    public void testUnblockUserFromPageWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:unblockUserFromPage");
        String apiEndPoint =
                "https://graph.facebook.com/invalid/blocked/" + connectorProperties.getProperty("appUserId")
                        + "?access_token=" + connectorProperties.getProperty("pageAccessToken");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_unblockUserFromPage_negative.txt");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for createPageEvent method with mandatory parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createPageEvent} integration test with optional parameters.")
    public void testCreatePageEventWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createPageEvent");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPageEvent_mandatory.txt");
        String eventId = esbRestResponse.getBody().get("id").toString();
        connectorProperties.put("eventId", eventId);
        Thread.sleep(timeOut);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + eventId + "?access_token="
                        + connectorProperties.getProperty("pageAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(apiRestResponse.getBody().get("name"), connectorProperties.getProperty("eventName"));
        
    }
    
    /**
     * Positive test case for createPageEvent method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePageEventWithMandatoryParameters" }, description = "facebook {createPageEvent} integration test with optional parameters.")
    public void testCreatePageEventWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPageEvent");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPageEvent_optional.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + esbRestResponse.getBody().get("id").toString()
                        + "?access_token=" + connectorProperties.getProperty("pageAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(apiRestResponse.getBody().get("name"), connectorProperties.getProperty("eventName"));
        Assert.assertEquals(apiRestResponse.getBody().get("description"),
                connectorProperties.getProperty("description"));
    }
    
    /**
     * Negative test case for createPageEvent method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createPageEvent} integration test with negative case.")
    public void testCreatePageEventWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPageEvent");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPageEvent_negative.txt");
        String apiEndPoint = "https://graph.facebook.com/invalid/events";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for publishPagePost method with mandatory parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {publishPagePost} integration test with optional parameters.")
    public void testPublishPagePostWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:publishPagePost");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_publishPagePost_mandatory.txt");
        Thread.sleep(timeOut);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + esbRestResponse.getBody().get("id").toString()
                        + "?access_token=" + connectorProperties.getProperty("pageAccessToken");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(apiRestResponse.getBody().get("message"), connectorProperties.getProperty("message"));
        
    }
    
    /**
     * Positive test case for publishPagePost method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {publishPagePost} integration test with optional parameters.")
    public void testPublishPagePostWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:publishPagePost");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_publishPagePost_optional.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + esbRestResponse.getBody().get("id").toString()
                        + "?access_token=" + connectorProperties.getProperty("pageAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(apiRestResponse.getBody().get("message"), connectorProperties.getProperty("groupName"));
        Assert.assertEquals(apiRestResponse.getBody().get("description"),
                connectorProperties.getProperty("description"));
    }
    
    /**
     * Negative test case for publishPagePost method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {publishPagePost} integration test with negative case.")
    public void testPublishPagePostWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:publishPagePost");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_publishPagePost_negative.txt");
        String apiEndPoint = "https://graph.facebook.com/invalid/feed";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for updatePageDetails method with optional parameters.
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {updatePageDetails} integration test with optional parameters.")
    public void testUpdatePageDetailsWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:updatePageDetails");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("pageId")
                        + "?access_token=" + connectorProperties.getProperty("pageAccessToken");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updatePageDetails_optional.txt");
        
        Thread.sleep(timeOut);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().get("about"), connectorProperties.getProperty("about"));
        
    }
    
    /**
     * Negative test case for updatePageDetails method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {updatePageDetails} integration test with negative case.")
    public void testUpdatePageDetailsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updatePageDetails");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updatePageDetails_negative.txt");
        String apiEndPoint = "https://graph.facebook.com/invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for updateAppDetails method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testgetAppAccessTokenWithMandatoryParameters" }, description = "facebook {updateAppDetails} integration test with optional parameters.")
    public void testUpdateAppDetailsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateAppDetails");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("appId") + "?access_token="
                        + connectorProperties.getProperty("appAccessToken") + "&fields=canvas_url";
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateAppDetails_optional.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().get("canvas_url"), connectorProperties.getProperty("canvasUrl"));
        
    }
    
    /**
     * Negative test case for updateAppDetails method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testgetAppAccessTokenWithMandatoryParameters" }, description = "facebook {updateAppDetails} integration test with negative case.")
    public void testUpdateAppDetailsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateAppDetails");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateAppDetails_negative.txt");
        String apiEndPoint =
                "https://graph.facebook.com/invalid?access_token=" + connectorProperties.getProperty("appAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for createPageNote method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createPageNote} integration test with optional parameters.")
    public void testCreatePageNoteWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPageNote");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPageNote_mandatory.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + esbRestResponse.getBody().get("id").toString()
                        + "?access_token=" + connectorProperties.getProperty("pageAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertTrue(apiRestResponse.getBody().get("message").toString()
                .contains(connectorProperties.getProperty("description")));
    }
    
    /**
     * Negative test case for createPageNote method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createPageNote} integration test with negative case.")
    public void testCreatePageNoteWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPageNote");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPageNote_negative.txt");
        String apiEndPoint =
                "https://graph.facebook.com/invalid/notes?access_token="
                        + connectorProperties.getProperty("pageAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for updatePageSettings method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {updatePageSettings} integration test with optional parameters.")
    public void testUpdatePageSettingsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updatePageSettings");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updatePageSettings_mandatory.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("pageId")
                        + "/settings?access_token=" + connectorProperties.getProperty("pageAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().getJSONArray("data").get(0).toString()
                .contains(connectorProperties.getProperty("value")));
        
    }
    
    /**
     * Negative test case for updatePageSettings method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {updatePageSettings} integration test with negative case.")
    public void testUpdatePageSettingsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updatePageSettings");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updatePageSettings_negative.txt");
        String apiEndPoint =
                "https://graph.facebook.com/invalid/settings?access_token="
                        + connectorProperties.getProperty("pageAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for replyToConversation method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {replyToConversation} integration test with optional parameters.")
    public void testReplyToConversationWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:replyToConversation");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_replyToConversation_mandatory.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + esbRestResponse.getBody().get("id").toString()
                        + "?access_token=" + connectorProperties.getProperty("pageAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().toString().contains(connectorProperties.getProperty("description")));
    }
    
    /**
     * Negative test case for replyToConversation method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {replyToConversation} integration test with negative case.")
    public void testReplyToConversationWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:replyToConversation");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_replyToConversation_negative.txt");
        String apiEndPoint =
                "https://graph.facebook.com/invalid?access_token=" + connectorProperties.getProperty("pageAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for sendAppRequest method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {sendAppRequest} integration test with optional parameters.")
    public void testSendAppRequestWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:sendAppRequest");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendAppRequest_mandatory.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + esbRestResponse.getBody().get("request").toString()
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().toString().contains(connectorProperties.getProperty("message")));
    }
    
    /**
     * Positive test case for sendAppRequest method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {sendAppRequest} integration test with optional parameters.")
    public void testSendAppRequestWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:sendAppRequest");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendAppRequest_optional.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + esbRestResponse.getBody().get("request").toString()
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().toString().contains(connectorProperties.getProperty("message")));
        Assert.assertTrue(apiRestResponse.getBody().toString().contains(connectorProperties.getProperty("description")));
        
    }
    
    /**
     * Negative test case for sendAppRequest method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {sendAppRequest} integration test with negative case.")
    public void testSendAppRequestWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:sendAppRequest");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendAppRequest_negative.txt");
        String apiEndPoint = "https://graph.facebook.com/me/apprequests?access_token=invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for inviteMemberToGroup method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateAppUserGroupWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "facebook {inviteMemberToGroup} integration test with optional parameters.")
    public void testInviteMemberToGroupWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:inviteMemberToGroup");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_inviteMemberToGroup_mandatory.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("groupId")
                        + "/members?access_token=" + connectorProperties.getProperty("appAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().toString().contains(connectorProperties.getProperty("groupMember")));
    }
    
    /**
     * Negative test case for inviteMemberToGroup method.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateAppUserGroupWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "facebook {inviteMemberToGroup} integration test with negative case.")
    public void testInviteMemberToGroupWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:inviteMemberToGroup");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_inviteMemberToGroup_negative.txt");
        String apiEndPoint =
                "https://graph.facebook.com/invalid/members?access_token="
                        + connectorProperties.getProperty("appAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for removeMemberFromGroup method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testInviteMemberToGroupWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "facebook {removeMemberFromGroup} integration test with optional parameters.")
    public void testRemoveMemberFromGroupWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:removeMemberFromGroup");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("groupId")
                        + "/members?access_token=" + connectorProperties.getProperty("appAccessToken");
        RestResponse<JSONObject> apiRestResponse1 = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_removeMemberFromGroup_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse2 = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertNotEquals(apiRestResponse1.getBody().getJSONArray("data"), apiRestResponse2.getBody()
                .getJSONArray("data"));
    }
    
    /**
     * Negative test case for removeMemberFromGroup method.
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateAppUserGroupWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "facebook {removeMemberFromGroup} integration test with negative case.")
    public void testRemoveMemberFromGroupWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:removeMemberFromGroup");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_removeMemberFromGroup_negative.txt");
        String apiEndPoint =
                "https://graph.facebook.com/invalid/members?access_token="
                        + connectorProperties.getProperty("appAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for createAppUserRole method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAppUserGroupWithMandatoryParameters" }, description = "facebook {createAppUserRole} integration test with optional parameters.")
    public void testCreateAppUserRoleWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAppUserRole");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAppUserRole_mandatory.txt");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("appId")
                        + "/roles?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createAppUserRole_mandatory.txt");
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }
    
    /**
     * Negative test case for createAppUserRole method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAppUserGroupWithMandatoryParameters" }, description = "facebook {createAppUserRole} integration test with negative case.")
    public void testCreateAppUserRoleWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAppUserRole");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAppUserRole_negative.txt");
        String apiEndPoint =
                "https://graph.facebook.com/invalid/roles?access_token="
                        + connectorProperties.getProperty("appAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for deleteAppUserRole method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = { "testgetAppAccessTokenWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "facebook {deleteAppUserRole} integration test with optional parameters.")
    public void testDeleteAppUserRoleWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteAppUserRole");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("appId")
                        + "/roles?access_token=" + connectorProperties.getProperty("appAccessToken");
        RestResponse<JSONObject> apiRestResponse1 = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteAppUserRole_mandatory.txt");
        
        RestResponse<JSONObject> apiRestResponse2 = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponse1.getBody().getJSONArray("data"), apiRestResponse2.getBody()
                .getJSONArray("data"));
    }
    
    /**
     * Negative test case for deleteAppUserRole method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testgetAppAccessTokenWithMandatoryParameters" }, description = "facebook {deleteAppUserRole} integration test with negative case.")
    public void testDeleteAppUserRoleWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteAppUserRole");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteAppUserRole_negative.txt");
        String apiEndPoint =
                "https://graph.facebook.com/invalid/roles?access_token="
                        + connectorProperties.getProperty("appAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for setGroupCoverPhoto with mandatory parameters
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAppUserGroupWithMandatoryParameters" }, description = "facebook {setGroupCoverPhoto} integration test with mandatory parameters.")
    public void testSetGroupCoverPhotoMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:setGroupCoverPhoto");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setGroupCoverPhoto_mandatory.txt");
        Assert.assertTrue(esbRestResponse.getBody().toString().contains("true"));
    }
    
    /**
     * Negative test case for setGroupCoverPhoto
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {setGroupCoverPhoto} integration test negative case.")
    public void testSetGroupCoverPhotoNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:setGroupCoverPhoto");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_setGroupCoverPhoto_negative.txt");
        String apiEndPoint = "https://graph.facebook.com/invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for createPageMilestone method with mandatory parameters.
     * 
     * @throws InterruptedException
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createPageMilestone} integration test with mandatory parameters.")
    public void testCreatePageMilestoneMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:publishPageMilestone");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPageMilestone_mandatory.txt");
        connectorProperties.put("milestoneId", esbRestResponse.getBody().get("id").toString());
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("milestoneId")
                        + "?access_token=" + connectorProperties.getProperty("pageAccessToken");
        Thread.sleep(timeOut);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
    }
    
    /**
     * Negative test case for createPageMilestone.
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePageMilestoneMandatoryParameters" }, description = "facebook {createPageMilestone} integration test negative case.")
    public void testCreatePageMilestoneNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:publishPageMilestone");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("pageId") + "/milestones";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPageMilestone_negative.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createPageMilestone_negative.txt");
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for getPageMilestoneDetails method with mandatory parameters.
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePageMilestoneNegativeCase" }, description = "facebook {getPageMilestoneDetails} integration test with mandatory parameters.")
    public void testGetPageMilestoneDetailsMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getPageMilestoneDetails");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPageMilestoneDetails_mandatory.txt");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("pageId")
                        + "/milestones?access_token=" + connectorProperties.getProperty("pageAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("data").toString(), apiRestResponse.getBody().get("data")
                .toString());
    }
    
    /**
     * Positive test case for getPageMilestoneDetails method with optional parameters.
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetPageMilestoneDetailsMandatoryParameters" }, description = "facebook {getPageMilestoneDetails} integration test with optional parameters.")
    public void testGetPageMilestoneDetailsOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getPageMilestoneDetails");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPageMilestoneDetails_optional.txt");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("pageId")
                        + "/milestones?fields=id&access_token=" + connectorProperties.getProperty("pageAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("data").toString(), apiRestResponse.getBody().get("data")
                .toString());
    }
    
    /**
     * Negative test case for getPageMilestoneDetails method
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetPageMilestoneDetailsOptionalParameters" }, description = "facebook {getPageMilestoneDetails} integration test with optional parameters.")
    public void testGetPageMilestoneDetailsNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getPageMilestoneDetails");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPageMilestoneDetails_negative.txt");
        String apiEndPoint =
                "https://graph.facebook.com/invalid12342/milestones?fields=id&access_token="
                        + connectorProperties.getProperty("pageAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for updatePageMilestone method with optional parameters.
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetPageMilestoneDetailsNegativeCase" }, description = "facebook {updatePageMilestone} integration test with optional parameters.")
    public void testUpdatePageMilestoneOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updatePageMilestone");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("milestoneId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updatePageMilestone_optional.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updatePageMilestone_optional.txt");
        Assert.assertEquals(esbRestResponse.getBody().get("output").toString(), apiRestResponse.getBody().get("output")
                .toString());
    }
    
    /**
     * Negative test case for updatePageMilestone method
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUpdatePageMilestoneOptionalParameters" }, description = "facebook {updatePageMilestone} integration test negative case.")
    public void testUpdatePageMilestoneNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updatePageMilestone");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("milestoneId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updatePageMilestone_negative.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updatePageMilestone_negative.txt");
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Negative test case for deletePageMilestone method
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUpdatePageMilestoneNegativeCase" }, description = "facebook {deletePageMilestone} integration test negative case.")
    public void testDeletePageMilestoneNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deletePageMilestone");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("milestoneId")
                        + "?access_token=" + connectorProperties.getProperty("pageAccessToken");
        RestResponse<JSONObject> firstApiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deletePageMilestone_negative.txt");
        RestResponse<JSONObject> secondApiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(firstApiRestResponse.getBody().has("id") && secondApiRestResponse.getBody().has("id"));
    }
    
    /**
     * Positive test case for deletePageMilestone method with mandatory parameters
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testDeletePageMilestoneNegativeCase" }, description = "facebook {deletePageMilestone} integration test with optional parameters.")
    public void testDeletePageMilestoneMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deletePageMilestone");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("milestoneId")
                        + "?access_token=" + connectorProperties.getProperty("pageAccessToken");
        RestResponse<JSONObject> firstApiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deletePageMilestone_mandatory.txt");
        RestResponse<JSONObject> secondApiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(firstApiRestResponse.getBody().has("id") && secondApiRestResponse.getBody().has("error"));
    }
    
    /**
     * Positive test case for createFriendList method with mandatory parameters.
     * 
     * @throws InterruptedException
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createFriendList} integration test with mandatory parameters.")
    public void testCreateFriendlistMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createFriendList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createFriendlist_mandatory.txt");
        
        connectorProperties.put("friendlistId", esbRestResponse.getBody().get("id").toString());
        Thread.sleep(timeOut);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("friendlistId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
    }
    
    /**
     * Negative test case for createFriendList method parameters.
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateFriendlistMandatoryParameters" }, description = "facebook {createFriendList} integration test negative case.")
    public void testCreateFriendlistNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createFriendList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createFriendlist_negative.txt");
        String apiEndPoint = "https://graph.facebook.com/me/friendlists";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createFriendlist_negative.txt");
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for addMembersToFriendList method with mandatory parameters.
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateFriendlistNegativeCase" }, description = "facebook {addMembersToFriendList} integration test with mandatory parameters.")
    public void testAddMembersToFriendlistMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:addMembersToFriendList");
        String deriveFriendsApiEndPoint =
                "https://graph.facebook.com/me/friends?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> deriveFriendsApiRestResponse =
                sendJsonRestRequest(deriveFriendsApiEndPoint, "GET", apiRequestHeadersMap);
        connectorProperties.put("members", deriveFriendsApiRestResponse.getBody().getJSONArray("data").getJSONObject(0)
                .get("id").toString());
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addMembersToFriendlist_mandatory.txt");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("friendlistId")
                        + "/members?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("id").toString()
                .equals(connectorProperties.getProperty("members")));
    }
    
    /**
     * Negative test case for addMembersToFriendList method
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testAddMembersToFriendlistMandatoryParameters" }, description = "facebook {addMembersToFriendList} integration test negative case.")
    public void testAddMembersToFriendlistNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:addMembersToFriendList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addMembersToFriendlist_negative.txt");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("friendlistId")
                        + "/members";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
                        "api_addMembersToFriendlist_negative.txt");
        Assert.assertEquals(apiRestResponse.getBody().get("error").toString(), esbRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for removeMembersFromFriendList method with mandatory parameters.
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testAddMembersToFriendlistNegativeCase" }, description = "facebook {removeMembersFromFriendList} integration test with mandatory parameters.")
    public void testRemoveMembersFromFriendListMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:removeMembersFromFriendList");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_removeMembersFromFriendList_mandatory.txt");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("friendlistId")
                        + "/members?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().getJSONArray("data").length() == 0
                || !(apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("id").toString()
                        .equals(connectorProperties.getProperty("members"))));
    }
    
    /**
     * Negative test case for removeMembersFromFriendList
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testRemoveMembersFromFriendListMandatoryParameters" }, description = "facebook {removeMembersFromFriendList} integration test with mandatory parameters.")
    public void testRemoveMembersFromFriendListNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:removeMembersFromFriendList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_removeMembersFromFriendList_negative.txt");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("friendlistId")
                        + "/members?members=invalid12342&access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("error").toString(), esbRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for getFriendListDetails method with mandatory parameters.
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testRemoveMembersFromFriendListNegativeCase" }, description = "facebook {getFriendListDetails} integration test with mandatory parameters.")
    public void testGetFriendListDetailsMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getFriendListDetails");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFriendListDetails_mandatory.txt");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("friendlistId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("name").toString(), esbRestResponse.getBody().get("name")
                .toString());
    }
    
    /**
     * Positive test case for getFriendListDetails method with optional parameters.
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetFriendListDetailsMandatoryParameters" }, description = "facebook {getFriendListDetails} integration test with optional parameters.")
    public void testGetFriendListDetailsOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getFriendListDetails");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFriendListDetails_optional.txt");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("friendlistId")
                        + "?fields=id&access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("id").toString(), esbRestResponse.getBody().get("id")
                .toString());
    }
    
    /**
     * Negative test case for getFriendListDetails method
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetFriendListDetailsOptionalParameters" }, description = "facebook {getFriendListDetails} integration test negative case.")
    public void testGetFriendListDetailsNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getFriendListDetails");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFriendListDetails_negative.txt");
        String apiEndPoint =
                "https://graph.facebook.com/invalid12342?fields=id&access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("error").toString(), esbRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Negative test case for deleteFriendList method
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetFriendListDetailsNegativeCase" }, description = "facebook {deleteFriendList} integration test negative case.")
    public void testDeleteFriendListNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteFriendList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteFriendList_negative.txt");
        String apiEndPoint =
                "https://graph.facebook.com/invalid12342?access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("error").toString(), esbRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for deleteFriendList method with mandatory parameters
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testDeleteFriendListNegativeCase" }, description = "facebook {deleteFriendList} integration test with mandatory parameters.")
    public void testDeleteFriendListMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteFriendList");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("friendlistId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> firstApiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteFriendList_mandatory.txt");
        RestResponse<JSONObject> secondApiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(firstApiRestResponse.getBody().has("id") && secondApiRestResponse.getBody().has("error"));
    }
    
    /**
     * Positive test case for getAppReview method with mandatory parameters
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getAppReview} integration test with mandatory parameters.")
    public void testGetAppReviewMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAppReview");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("appId")
                        + "/reviews?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAppReview_mandatory.txt");
        Assert.assertEquals(apiRestResponse.getBody().get("data").toString(), esbRestResponse.getBody().get("data")
                .toString());
    }
    
    /**
     * Positive test case for getAppReview method with optional parameters
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetAppReviewMandatoryParameters" }, description = "facebook {getAppReview} integration test with optional parameters.")
    public void testGetAppReviewOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAppReview");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("appId")
                        + "/reviews?fields=id&access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAppReview_optional.txt");
        Assert.assertEquals(apiRestResponse.getBody().get("data").toString(), esbRestResponse.getBody().get("data")
                .toString());
    }
    
    /**
     * Negative test case for getAppReview method
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetAppReviewOptionalParameters" }, description = "facebook {getAppReview} integration test negative case.")
    public void testGetAppReviewNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAppReview");
        String apiEndPoint =
                "https://graph.facebook.com/invalid12342/reviews?access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAppReview_negative.txt");
        Assert.assertEquals(apiRestResponse.getBody().get("error").toString(), esbRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for getUserDetails method with mandatory parameters
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getUserDetails} integration test with mandatory parameters.")
    public void testGetUserDetailsMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getUserDetails");
        String apiEndPoint =
                "https://graph.facebook.com/me?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserDetails_mandatory.txt");
        Assert.assertEquals(apiRestResponse.getBody().get("name").toString(), esbRestResponse.getBody().get("name")
                .toString());
    }
    
    /**
     * Positive test case for getUserDetails method with optional parameters
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getUserDetails} integration test with optional parameters.")
    public void testGetUserDetailsOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getUserDetails");
        String apiEndPoint =
                "https://graph.facebook.com/me?fields=id&access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserDetails_optional.txt");
        Assert.assertEquals(apiRestResponse.getBody().get("id").toString(), esbRestResponse.getBody().get("id")
                .toString());
    }
    
    /**
     * Negative test case for getUserDetails method
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getUserDetails} integration test negative case.")
    public void testGetUserDetailsNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getUserDetails");
        String apiEndPoint =
                "https://graph.facebook.com/me?fields=abc&access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUserDetails_negative.txt");
        Assert.assertEquals(apiRestResponse.getBody().get("error").toString(), esbRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for createAlbum method with mandatory parameters
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createAlbum} integration test with mandatory parameters.")
    public void testCreateAlbumMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAlbum");
        connectorProperties.put("name", "test" + new Date().toString());
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAlbum_mandatory.txt");
        connectorProperties.put("albumId", esbRestResponse.getBody().get("id").toString());
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("albumId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
    }
    
    /**
     * Positive test case for createAlbum method with optional parameters
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAlbumMandatoryParameters" }, description = "facebook {createAlbum} integration test with optional parameters.")
    public void testCreateAlbumOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAlbum");
        connectorProperties.put("name", "test" + new Date().toString());
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAlbum_optional.txt");
        connectorProperties.put("albumId", esbRestResponse.getBody().get("id").toString());
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("albumId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
    }
    
    /**
     * Negative test case for createAlbum method
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAlbumOptionalParameters" }, description = "facebook {createAlbum} integration test negative case.")
    public void testCreateAlbumNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createAlbum");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAlbum_negative.txt");
        String apiEndPoint = "https://graph.facebook.com/me/albums";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createAlbum_negative.txt");
        Assert.assertEquals(apiRestResponse.getBody().get("error").toString(), esbRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for getAlbumDetails method with mandatory parameters
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAlbumNegativeCase" }, description = "facebook {getAlbumDetails} integration test with mandatory parameters.")
    public void testGetAlbumDetailsMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAlbumDetails");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAlbumDetails_mandatory.txt");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("albumId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("name").toString(), esbRestResponse.getBody().get("name")
                .toString());
    }
    
    /**
     * Positive test case for getAlbumDetails method with optional parameters
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetAlbumDetailsMandatoryParameters" }, description = "facebook {getAlbumDetails} integration test with optional parameters.")
    public void testGetAlbumDetailsOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAlbumDetails");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAlbumDetails_optional.txt");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("albumId")
                        + "?fields=id&access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("id").toString(), esbRestResponse.getBody().get("id")
                .toString());
    }
    
    /**
     * Negative test case for getAlbumDetails method
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetAlbumDetailsOptionalParameters" }, description = "facebook {getAlbumDetails} integration test negative case.")
    public void testGetAlbumDetailsNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAlbumDetails");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAlbumDetails_negative.txt");
        String apiEndPoint =
                "https://graph.facebook.com/invalid12342?fields=id&access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("error").toString(), esbRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for createEvent method with mandatory parameters
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createEvent} integration test with mandatory parameters.")
    public void testCreateEventMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createEvent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEvent_mandatory.txt");
        String eventId = esbRestResponse.getBody().getString("id").toString();
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + eventId + "?access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
    }
    
    /**
     * Positive test case for createEvent method with optional parameters
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateEventMandatoryParameters" }, description = "facebook {createEvent} integration test with optional parameters.")
    public void testCreateEventOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createEvent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEvent_optional.txt");
        String eventId = esbRestResponse.getBody().getString("id").toString();
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + eventId + "?access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
    }
    
    /**
     * Negative test case for createEvent method
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateEventOptionalParameters" }, description = "facebook {createEvent} integration test negative case.")
    public void testCreateEventNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createEvent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createEvent_negative.txt");
        String apiEndPoint = "https://graph.facebook.com/me/events";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createEvent_negative.txt");
        Assert.assertEquals(apiRestResponse.getBody().get("error").toString(), esbRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for createPost: post status method with mandatory parameters
     * 
     * @throws InterruptedException
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createPost} integration test with mandatory parameters.")
    public void testCreatePostStatusMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createPost");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPost_postStatus_mandatory.txt");
        
        connectorProperties.put("statusMessageId", esbRestResponse.getBody().getString("id").toString());
        
        Thread.sleep(timeOut);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("statusMessageId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
        
    }
    
    /**
     * Positive test case for createPost: post link method with mandatory parameters
     * 
     * @throws InterruptedException
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createPost} integration test with mandatory parameters.")
    public void testCreatePostLinkMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createPost");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPost_postLink_mandatory.txt");
        connectorProperties.put("postId", esbRestResponse.getBody().getString("id").toString());
        Thread.sleep(timeOut);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("postId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
    }
    
    /**
     * Positive test case for createPost: post link method with optional parameters
     * 
     * @throws InterruptedException
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createPost} integration test with optional parameters.")
    public void testCreatePostLinkOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createPost");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPost_postLink_optional.txt");
        connectorProperties.put("postId", esbRestResponse.getBody().getString("id").toString());
        Thread.sleep(timeOut);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("postId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
    }
    
    /**
     * Negative test case for createPost
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createPost} integration test negative case.")
    public void testCreatePostNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPost");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPost_negative.txt");
        String apiEndPoint = "https://graph.facebook.com/me/feed";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createPost_negative.txt");
        Assert.assertEquals(apiRestResponse.getBody().get("error").toString(), esbRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for getPost with mandatory parameters
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePostLinkOptionalParameters" }, description = "facebook {getPost} integration test with mandatory parameters.")
    public void testGetPostMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getPost");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPost_mandatory.txt");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("postId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("id").toString(), esbRestResponse.getBody().get("id")
                .toString());
        Assert.assertEquals(apiRestResponse.getBody().get("type").toString(), esbRestResponse.getBody().get("type")
                .toString());
    }
    
    /**
     * Positive test case for getPost with optional parameters
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetPostMandatoryParameters" }, description = "facebook {getPost} integration test with optional parameters.")
    public void testGetPostOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getPost");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPost_optional.txt");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("postId")
                        + "?fields=id&access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("id").toString(), esbRestResponse.getBody().get("id")
                .toString());
    }
    
    /**
     * Negative test case for getPost with optional parameters
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getPost} integration test negative case.")
    public void testGetPostNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getPost");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPost_negative.txt");
        String apiEndPoint =
                "https://graph.facebook.com/invalid12342?fields=id&access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Negative test case for deletePost
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetPostOptionalParameters" }, description = "facebook {deletePost} integration test negative case.")
    public void testDeletePostNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deletePost");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("postId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> firstApiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deletePost_negative.txt");
        RestResponse<JSONObject> secondApiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(firstApiRestResponse.getBody().has("id") && secondApiRestResponse.getBody().has("id"));
    }
    
    /**
     * Positive test case for deletePost with mandatory parameters
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testDeletePostNegativeCase" }, description = "facebook {deletePost} integration test with mandatory parameters.")
    public void testDeletePostMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deletePost");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("postId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> firstApiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deletePost_mandatory.txt");
        RestResponse<JSONObject> secondApiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(firstApiRestResponse.getBody().has("id") && secondApiRestResponse.getBody().has("error"));
    }
    
    /**
     * Positive test case for createPageOffer with mandatory parameters
     * 
     * @throws InterruptedException
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createPageOffer} integration test with mandatory parameters.")
    public void testCreatePageOfferMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createPageOffer");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPageOffer_mandatory.txt");
        connectorProperties.put("offerId", esbRestResponse.getBody().getString("id").toString());
        Thread.sleep(timeOut);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("offerId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
    }
    
    /**
     * Positive test case for createPageOffer with optional parameters
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePageOfferMandatoryParameters" }, description = "facebook {createPageOffer} integration test with optional parameters.")
    public void testCreatePageOfferOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPageOffer");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPageOffer_optional.txt");
        connectorProperties.put("offerId", esbRestResponse.getBody().getString("id").toString());
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("offerId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
    }
    
    /**
     * Negative test case for createPageOffer
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {createPageOffer} integration test negative case.")
    public void testCreatePageOfferNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPageOffer");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPageOffer_negative.txt");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("pageId") + "/offers";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createPageOffer_negative.txt");
        Assert.assertEquals(apiRestResponse.getBody().get("error").toString(), esbRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for getOfferDetails with mandatory parameters
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePageOfferOptionalParameters" }, description = "facebook {getOfferDetails} integration test with mandatory parameters.")
    public void testGetOfferDetailsMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getOfferDetails");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("offerId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getOfferDetails_mandatory.txt");
        Assert.assertEquals(apiRestResponse.getBody().get("id").toString(), esbRestResponse.getBody().get("id")
                .toString());
    }
    
    /**
     * Positive test case for getOfferDetails with optional parameters
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetOfferDetailsMandatoryParameters" }, description = "facebook {getOfferDetails} integration test with mandatory parameters.")
    public void testGetOfferDetailsOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getOfferDetails");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("offerId")
                        + "?fields=id&access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getOfferDetails_optional.txt");
        Assert.assertEquals(apiRestResponse.getBody().get("id").toString(), esbRestResponse.getBody().get("id")
                .toString());
    }
    
    /**
     * Negative test case for getOfferDetails
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testGetOfferDetailsOptionalParameters" }, description = "facebook {getOfferDetails} integration test negative case.")
    public void testGetOfferDetailsNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getOfferDetails");
        String apiEndPoint =
                "https://graph.facebook.com/invalid12342?fields=id&access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getOfferDetails_negative.txt");
        Assert.assertEquals(apiRestResponse.getBody().get("error").toString(), esbRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for getInsightMetric with mandatory parameters
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getInsightMetric} integration test with mandatory parameters.")
    public void testGetInsightMetricMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getInsightMetric");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("pageId")
                        + "/insights?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getInsightMetric_mandatory.txt");
        Assert.assertEquals(esbRestResponse.getBody().get("data").toString(), apiRestResponse.getBody().get("data")
                .toString());
    }
    
    /**
     * Positive test case for getInsightMetric with optional parameters
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getInsightMetric} integration test with optional parameters.")
    public void testGetInsightMetricOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getInsightMetric");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("pageId")
                        + "/insights?fields=id&access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getInsightMetric_optional.txt");
        Assert.assertEquals(esbRestResponse.getBody().get("data").toString(), apiRestResponse.getBody().get("data")
                .toString());
    }
    
    /**
     * Negative test case for getInsightMetric
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getInsightMetric} integration test negative case.")
    public void testGetInsightMetricNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getInsightMetric");
        String apiEndPoint =
                "https://graph.facebook.com/invalid12342/insights?fields=id&access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getInsightMetric_negative.txt");
        Assert.assertEquals(apiRestResponse.getBody().get("error").toString(), esbRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for createPhotoTag with mandatory parameters
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUploadPhotoMandatoryParameters" }, description = "facebook {createPhotoTag} integration test with mandatory parameters.")
    public void testCreatePhotoTagMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPhotoTag");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("photoId")
                        + "/tags?access_token=" + connectorProperties.getProperty("accessToken");
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPhotoTag_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("data").getJSONObject(0).get("id").toString(),
                connectorProperties.getProperty("friendId"));
    }
    
    /**
     * Negative test case for createPhotoTag
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePhotoTagMandatoryParameters" }, description = "facebook {createPhotoTag} integration test negative case.")
    public void testCreatePhotoTagNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createPhotoTag");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("photoId") + "/tags";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPhotoTag_negative.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createPhotoTag_negative.txt");
        Assert.assertEquals(apiRestResponse.getBody().get("error").toString(), esbRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for updatePhotoTag with mandatory parameters
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePhotoTagNegativeCase" }, description = "facebook {updatePhotoTag} integration test with mandatory parameters.")
    public void testUpdatePhotoTagMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updatePhotoTag");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("photoId") + "/tags";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updatePhotoTag_mandatory.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updatePhotoTag_mandatory.txt");
        Assert.assertEquals(esbRestResponse.getBody().get("output").toString(), apiRestResponse.getBody().get("output")
                .toString());
    }
    
    /**
     * Negative test case for updatePhotoTag
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUpdatePhotoTagMandatoryParameters" }, description = "facebook {updatePhotoTag} integration test negative case.")
    public void testUpdatePhotoTagNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updatePhotoTag");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("photoId") + "/tags";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updatePhotoTag_negative.txt");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updatePhotoTag_negative.txt");
        Assert.assertEquals(apiRestResponse.getBody().get("error").toString(), esbRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Negative test case for deletePhotoTag
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUpdatePhotoTagNegativeCase" }, description = "facebook {deletePhotoTag} integration test negative case.")
    public void testDeletePhotoTagNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deletePhotoTag");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("photoId")
                        + "/tags?access_token=" + connectorProperties.getProperty("accessToken") + "&to="
                        + connectorProperties.getProperty("friendId");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deletePhotoTag_negative.txt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("error").toString(), esbRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Negative test case for deletePhoto
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testDeletePhotoTagNegativeCase" }, description = "facebook {deletePhoto} integration test negative case.")
    public void testDeletePhotoMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deletePhoto");
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deletePhoto_negative.txt");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("photoId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().has("error"));
    }
    
    /**
     * Positive test case for getGroupDetails method with mandatory parameters.
     * 
     * @throws IOException, JSONException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAppUserGroupWithMandatoryParameters" }, description = "facebook {getGroupDetails} integration test with mandatory parameters.")
    public void testGetGroupDetailsWithMandatoryParameters() throws IOException, JSONException {
    
        // calling ESB to get group ID
        esbRequestHeadersMap.put("Action", "urn:getGroupDetails");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getGroupDetails_mandatory.txt");
        // calling API to get group ID
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("userGroupId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("id").toString(), apiRestResponse.getBody().get("id")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().get("name").toString(), apiRestResponse.getBody().get("name")
                .toString());
    }
    
    /**
     * Positive test case for getGroupDetails method with optional parameters.
     * 
     * @throws IOException, JSONException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAppUserGroupWithMandatoryParameters" }, description = "facebook {getGroupDetails} integration test with optional parameters.")
    public void testGetGroupDetailsWithOptionalParameters() throws IOException, JSONException {
    
        // calling ESB to get group ID
        esbRequestHeadersMap.put("Action", "urn:getGroupDetails");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getGroupDetails_optional.txt");
        // calling API to get group ID
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("userGroupId")
                        + "?fields=id&access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("id").toString(), apiRestResponse.getBody().get("id")
                .toString());
    }
    
    /**
     * Negative test case for getGroupDetails method with mandatory parameters.
     * 
     * @throws IOException, JSONException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAppUserGroupWithMandatoryParameters" }, description = "facebook {getGroupDetails} integration test with negative parameters.")
    public void testGetGroupDetailsWithNegativeParameters() throws IOException, JSONException {
    
        // calling ESB to get group ID
        esbRequestHeadersMap.put("Action", "urn:getGroupDetails");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getGroupDetails_negative.txt");
        
        String apiEndPoint =
                "https://graph.facebook.com/negative" + "?access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for getThread method with mandatory parameters.
     * 
     * @throws IOException, JSONException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getThread} integration test with mandatory parameters.")
    public void testGetThreadWithMandatoryParameters() throws IOException, JSONException {
    
        // calling ESB to get thread
        esbRequestHeadersMap.put("Action", "urn:getThread");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getThread_mandatory.txt");
        // calling API to get thread
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("threadId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("id").toString(), apiRestResponse.getBody().get("id")
                .toString());
        
    }
    
    /**
     * Positive test case for getThread method with optional parameters.
     * 
     * @throws IOException, JSONException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getThread} integration test with optional parameters.")
    public void testGetThreadWithOptionalParameters() throws IOException, JSONException {
    
        // calling ESB to get thread
        esbRequestHeadersMap.put("Action", "urn:getThread");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getThread_optional.txt");
        // calling API to get thread
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("threadId") + "?fields=id&"
                        + "access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("id").toString(), apiRestResponse.getBody().get("id")
                .toString());
    }
    
    /**
     * Negative test case for getThread method with mandatory parameters.
     * 
     * @throws IOException, JSONException
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getThread} integration test with negative parameters.")
    public void testGetThreadWithNegativeParameters() throws IOException, JSONException {
    
        // calling ESB to get thread
        esbRequestHeadersMap.put("Action", "urn:getThread");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getThread_negative.txt");
        // calling API to get thread
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "threadId/" + "?access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for getStatus method with mandatory parameters.
     * 
     * @throws IOException, JSONException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePostStatusMandatoryParameters" }, description = "facebook {getStatus} integration test with mandatory parameters.")
    public void testGetStatusWithMandatoryParameters() throws IOException, JSONException {
    
        // calling ESB to get status
        esbRequestHeadersMap.put("Action", "urn:getStatus");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getStatus_mandatory.txt");
        // calling API to get status
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("statusMessageId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("id").toString(), apiRestResponse.getBody().get("id")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().get("message").toString(),
                apiRestResponse.getBody().get("message").toString());
    }
    
    /**
     * Positive test case for getStatus method with optional parameters.
     * 
     * @throws IOException, JSONException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePostStatusMandatoryParameters" }, description = "facebook {getStatus} integration test with optional parameters.")
    public void testGetStatusWithOptionalParameters() throws IOException, JSONException {
    
        // calling ESB to get status
        esbRequestHeadersMap.put("Action", "urn:getStatus");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getStatus_optional.txt");
        // calling API to get status
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("statusMessageId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken") + "&fields=id";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("id").toString(), apiRestResponse.getBody().get("id")
                .toString());
        
    }
    
    /**
     * Negative test case for getStatus method with mandatory parameters.
     * 
     * @throws IOException, JSONException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getStatus} integration test with negative parameters.")
    public void testGetStatusWithNegativeParameters() throws IOException, JSONException {
    
        // calling ESB to get status
        esbRequestHeadersMap.put("Action", "urn:getStatus");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getStatus_negative.txt");
        // calling API to get status
        String apiEndPoint =
                "https://graph.facebook.com/negative/" + "?access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for getMessage method with mandatory parameters.
     * 
     * @throws IOException, JSONException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getMessage} integration test with mandatory parameters.")
    public void testGetMessageWithMandatoryParameters() throws IOException, JSONException {
    
        // calling ESB to get message
        esbRequestHeadersMap.put("Action", "urn:getMessage");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMessage_mandatory.txt");
        // calling API to get Message
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("messageId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("id").toString(), apiRestResponse.getBody().get("id")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().get("from").toString(), apiRestResponse.getBody().get("from")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().get("to").toString(), apiRestResponse.getBody().get("to")
                .toString());
    }
    
    /**
     * Positive test case for getMessage method with optional parameters.
     * 
     * @throws IOException, JSONException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getMessage} integration test with optional parameters.")
    public void testGetMessageWithOptionalParameters() throws IOException, JSONException {
    
        // calling ESB to get message
        esbRequestHeadersMap.put("Action", "urn:getMessage");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMessage_optional.txt");
        // calling API to get message
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("messageId")
                        + "?fields=id&access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("id").toString(), apiRestResponse.getBody().get("id")
                .toString());
    }
    
    /**
     * Negative test case for getMessage method with mandatory parameters.
     * 
     * @throws IOException, JSONException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {getMessage} integration test with negative parameters.")
    public void testGetMessageWithNegativeParameters() throws IOException, JSONException {
    
        // calling ESB to get status
        esbRequestHeadersMap.put("Action", "urn:getMessage");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMessage_negative.txt");
        // calling API to get message
        String apiEndPoint =
                "https://graph.facebook.com/negative/" + "?access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for getPhotoDetails method with mandatory parameters.
     * 
     * @throws IOException, JSONException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUploadPhotoMandatoryParameters" }, description = "facebook {getPhotoDetails} integration test with mandatory parameters.")
    public void testGetPhotoDetailsWithMandatoryParameters() throws IOException, JSONException {
    
        // calling ESB to get Photo Details
        esbRequestHeadersMap.put("Action", "urn:getPhotoDetails");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPhotoDetails_mandatory.txt");
        // calling API to get Photo Details
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("photoId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("id").toString(), apiRestResponse.getBody().get("id")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().get("from").toString(), apiRestResponse.getBody().get("from")
                .toString());
    }
    
    /**
     * Positive test case for getPhotoDetails method with optional parameters.
     * 
     * @throws IOException, JSONException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUploadPhotoMandatoryParameters" }, description = "facebook {getPhotoDetails} integration test with optional parameters.")
    public void testGetPhotoDetailsWithOptionalParameters() throws IOException, JSONException {
    
        // calling ESB to get photo details
        esbRequestHeadersMap.put("Action", "urn:getPhotoDetails");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPhotoDetails_optional.txt");
        // calling API to get photo details
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("photoId")
                        + "?fields=id&access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("id").toString(), apiRestResponse.getBody().get("id")
                .toString());
    }
    
    /**
     * Negative test case for getPhotoDetails method with mandatory parameters.
     * 
     * @throws IOException, JSONException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testUploadPhotoMandatoryParameters" }, description = "facebook {getPhotoDetails} integration test with negative parameters.")
    public void testGetPhotoDetailsWithNegativeParameters() throws IOException, JSONException {
    
        // calling ESB to get photo details
        esbRequestHeadersMap.put("Action", "urn:getPhotoDetails");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPhotoDetails_negative.txt");
        // calling API to get photo details
        String apiEndPoint =
                "https://graph.facebook.com/negative" + "?access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error")
                .toString());
    }
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAppUserGroupWithMandatoryParameters" }, description = "facebook {createGroupEvent} integration test with mandatory parameters.")
    public void testCreateGroupEventWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createGroupEvent");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createGroupEvent_mandatory.txt");
        connectorProperties.put("groupEventId", esbRestResponse.getBody().get("id").toString());
        // read event details
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("groupEventId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
    }
    
    /**
     * Positive test case for createGroupEvent method with optional parameters.
     * 
     * @throws IOException, JSONException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAppUserGroupWithMandatoryParameters" }, description = "facebook {createGroupEvent} integration test with optional parameters.")
    public void testCreateGroupEventWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createGroupEvent");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createGroupEvent_optional.txt");
        connectorProperties.put("groupEventId", esbRestResponse.getBody().get("id").toString());
        // read event details
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("groupEventId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
        
    }
    
    /*
     * createGroupEvent method with negative parameters.
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAppUserGroupWithMandatoryParameters" }, description = "facebook {createGroupEvent} integration test with negative parameters.")
    public void testCreateGroupEventWithNegativeParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createGroupEvent");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createGroupEvent_negative.txt");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("groupId") + "/events";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createGroupEvent_negative.txt");
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for createGroupPost method with mandatory parameters.
     * 
     * @throws IOException, JSONException
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAppUserGroupWithMandatoryParameters" }, description = "facebook {createGroupPost} integration test with mandatory parameters.")
    public void testCreateGroupPostWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createGroupPost");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createGroupPost_mandatory.txt");
        connectorProperties.put("groupEventPostId", esbRestResponse.getBody().get("id"));
        Thread.sleep(timeOut);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl")
                        + connectorProperties.getProperty("groupEventPostId").toString() + "/?access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
    }
    
    /**
     * Positive test case for createGroupPost method with optional parameters.
     * 
     * @throws IOException, JSONException
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAppUserGroupWithMandatoryParameters" }, description = "facebook {createGroupPost} integration test with optional parameters.")
    public void testCreateGroupPostWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createGroupPost");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createGroupPost_optional.txt");
        connectorProperties.put("groupEventPostId", esbRestResponse.getBody().get("id"));
        Thread.sleep(timeOut);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl")
                        + connectorProperties.getProperty("groupEventPostId").toString() + "/?access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
    }
    
    /*
     * createGroupPost method with negative parameters.
     */
    
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAppUserGroupWithMandatoryParameters" }, description = "facebook {createGroupPost} integration test with negative parameters.")
    public void testCreateGroupPostWithNegativeParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createGroupPost");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createGroupPost_negative.txt");
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("groupId") + "/feed";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createGroupPost_negative.txt");
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error")
                .toString());
    }
    
    /**
     * Positive test case for postPhotoToAlbum
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAlbumMandatoryParameters" }, description = "facebook {postPhotoToAlbum} integration test mandatory parameters.")
    public void testUploadPhotoMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:postPhotoToAlbum");
        
        headersMap.put("Action", "urn:postPhotoToAlbum");
        
        MultipartFormdataProcessor multipartProcessor =
                new MultipartFormdataProcessor(multipartProxyUrl + "?album_id="
                        + connectorProperties.getProperty("albumId"), headersMap);
        
        multipartProcessor.addFormDataToRequest("message", "via new ESb");
        multipartProcessor.addFormDataToRequest("access_token", connectorProperties.getProperty("accessToken"));
        multipartProcessor.addFileToRequest("source", connectorProperties.getProperty("imageName"));
        
        RestResponse<JSONObject> esbRestResponse = multipartProcessor.processForJsonResponse();
        
        String photoId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("photoId", photoId);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + photoId + "?access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
        
    }
    
    /**
     * Positive test case for updateEventProfilePicture
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePageEventWithOptionalParameters" }, description = "facebook {updateEventProfilePicture} integration test negative case.")
    public void testUpdateEventProfilePictureMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateEventProfilePicture");
        
        headersMap.put("Action", "urn:updateEventProfilePicture");
        
        MultipartFormdataProcessor multipartProcessor =
                new MultipartFormdataProcessor(multipartProxyUrl + "?eventId="
                        + connectorProperties.getProperty("eventId"), headersMap);
        
        multipartProcessor.addFormDataToRequest("access_token", connectorProperties.getProperty("accessToken"));
        multipartProcessor.addFileToRequest("source", connectorProperties.getProperty("imageName"));
        
        RestResponse<JSONObject> esbRestResponse = multipartProcessor.processForJsonResponse();
        Assert.assertEquals(esbRestResponse.getBody().get("output"), "true");
        
    }
    
    /**
     * Positive test case for postEventPhotos
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePageEventWithOptionalParameters" }, description = "facebook {postEventPhotos} integration test negative case.")
    public void testPostEventPhotosMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:postEventPhotos");
        
        headersMap.put("Action", "urn:postEventPhotos");
        
        MultipartFormdataProcessor multipartProcessor =
                new MultipartFormdataProcessor(multipartProxyUrl + "?eventId="
                        + connectorProperties.getProperty("eventId"), headersMap);
        
        multipartProcessor.addFormDataToRequest("message", "via new ESb");
        multipartProcessor.addFormDataToRequest("access_token", connectorProperties.getProperty("pageAccessToken"));
        multipartProcessor.addFileToRequest("source", connectorProperties.getProperty("imageName"));
        
        RestResponse<JSONObject> esbRestResponse = multipartProcessor.processForJsonResponse();
        
        String photoId = esbRestResponse.getBody().getString("id");
        
        Thread.sleep(timeOut);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + photoId + "?access_token="
                        + connectorProperties.getProperty("pageAccessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
        
    }
    
    /**
     * Positive test case for publishPhoto
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {publishPhoto} integration test negative case.")
    public void testPublishPhotoMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:publishPhoto");
        
        headersMap.put("Action", "urn:publishPhoto");
        
        MultipartFormdataProcessor multipartProcessor =
                new MultipartFormdataProcessor(multipartProxyUrl + "?user_id="
                        + connectorProperties.getProperty("userId"), headersMap);
        
        multipartProcessor.addFormDataToRequest("message", "via new ESb");
        multipartProcessor.addFormDataToRequest("access_token", connectorProperties.getProperty("accessToken"));
        multipartProcessor.addFileToRequest("source", connectorProperties.getProperty("imageName"));
        
        RestResponse<JSONObject> esbRestResponse = multipartProcessor.processForJsonResponse();
        String photoId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("photoId", photoId);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + photoId + "?access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
        
    }
    
    /**
     * Positive test case for addPhotoToPage
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {addPhotoToPage} integration test negative case.")
    public void testAddPhotoToPageMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:addPhotoToPage");
        
        headersMap.put("Action", "urn:addPhotoToPage");
        MultipartFormdataProcessor multipartProcessor =
                new MultipartFormdataProcessor(multipartProxyUrl + "?pageId="
                        + connectorProperties.getProperty("pageId"), headersMap);
        
        multipartProcessor.addFormDataToRequest("message", "via new ESb");
        multipartProcessor.addFormDataToRequest("access_token", connectorProperties.getProperty("accessToken"));
        multipartProcessor.addFileToRequest("source", connectorProperties.getProperty("imageName"));
        
        RestResponse<JSONObject> esbRestResponse = multipartProcessor.processForJsonResponse();
        String photoId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("photoId", photoId);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + photoId + "?access_token="
                        + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
        
    }
    
    /**
     * Positive test case for updatePagePicture
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {updatePagePicture} integration test negative case.")
    public void testUpdatePagePictureMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:updatePagePicture");
        
        headersMap.put("Action", "urn:updatePagePicture");
        Thread.sleep(timeOut);
        
        MultipartFormdataProcessor multipartProcessor =
                new MultipartFormdataProcessor(multipartProxyUrl + "?pageId="
                        + connectorProperties.getProperty("pageId"), headersMap);
        
        multipartProcessor.addFormDataToRequest("message", "via new ESb");
        multipartProcessor.addFormDataToRequest("access_token", connectorProperties.getProperty("pageAccessToken"));
        multipartProcessor.addFileToRequest("source", connectorProperties.getProperty("imageName"));
        
        RestResponse<JSONObject> esbRestResponse = multipartProcessor.processForJsonResponse();
        Assert.assertTrue(esbRestResponse.getBody().toString().contains("true"));
        
    }
    
    /**
     * Positive test case for uploadVideo
     * 
     * @throws InterruptedException
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {uploadVideo} integration test negative case.")
    public void testUploadVideoMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:uploadVideo");
        
        headersMap.put("Action", "urn:uploadVideo");
        
        MultipartFormdataProcessor multipartProcessor =
                new MultipartFormdataProcessor(multipartProxyUrl + "?resourceId="
                        + connectorProperties.getProperty("userId"), headersMap);
        
        multipartProcessor.addFormDataToRequest("message", "via new ESb");
        multipartProcessor.addFormDataToRequest("access_token", connectorProperties.getProperty("accessToken"));
        multipartProcessor.addFileToRequest("source", connectorProperties.getProperty("videoName"));
        
        RestResponse<JSONObject> esbRestResponse = multipartProcessor.processForJsonResponse();
        String videoId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("videoId", videoId);
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("videoUploadTimeOut")));
        Assert.assertTrue(esbRestResponse.getBody().has("id"));
        
    }
    
    /**
     * Positive test case for addPageVideo
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "facebook {addPageVideo} integration test negative case.")
    public void testAddPageVideoMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:addPageVideo");
        
        headersMap.put("Action", "urn:addPageVideo");
        MultipartFormdataProcessor multipartProcessor =
                new MultipartFormdataProcessor(multipartProxyUrl + "?page_id="
                        + connectorProperties.getProperty("pageId"), headersMap);
        
        multipartProcessor.addFormDataToRequest("description", "via new ESb");
        multipartProcessor.addFormDataToRequest("access_token", connectorProperties.getProperty("pageAccessToken"));
        multipartProcessor.addFileToRequest("source", "env.3gp");
        
        RestResponse<JSONObject> esbRestResponse = multipartProcessor.processForJsonResponse();
        
        Assert.assertTrue(esbRestResponse.getBody().has("id"));
        
    }
    
    /**
     * Revert Facebook Changes.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @AfterClass(alwaysRun = true)
    public void revertFacebookChanges() throws IOException, JSONException {
    
        // Remove user status messages after running all the methods to avoid application banning
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("statusMessageId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        // Delete Friend List after running all methods to avoid duplicate Friend list creation.
        
        apiEndPoint =
                connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("friendlistId")
                        + "?access_token=" + connectorProperties.getProperty("accessToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        if(apiRestResponse.getBody().has("id")){
            apiEndPoint =
                    connectorProperties.getProperty("apiUrl") + connectorProperties.getProperty("friendlistId")
                            + "?access_token=" + connectorProperties.getProperty("accessToken");  
            apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
            
        }
        
    }
    
}
