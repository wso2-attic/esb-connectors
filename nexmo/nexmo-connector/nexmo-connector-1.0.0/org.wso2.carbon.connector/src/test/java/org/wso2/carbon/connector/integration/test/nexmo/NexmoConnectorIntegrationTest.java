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

package org.wso2.carbon.connector.integration.test.nexmo;

import java.io.IOException;
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

public class NexmoConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("nexmo-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");
    }
    
    /**
     * Positive test case for getBalance method with mandatory parameters.
     */
    @Test(priority = 1, description = "nexmo {getBalance} integration test with mandatory parameters.")
    public void testGetBalanceWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getBalance");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getBalance_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/account/get-balance?api_key="
                        + connectorProperties.getProperty("apiKey") + "&api_secret="
                        + connectorProperties.getProperty("apiSecret");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("value"), apiRestResponse.getBody().getString("value"));
    }
    
    /**
     * Positive test case for getOutboundPricing method with mandatory parameters.
     */
    @Test(priority = 1, description = "nexmo {getOutboundPricing} integration test with mandatory parameters.")
    public void testGetOutboundPricingWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getOutboundPricing");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getOutboundPricing_mandatory.json");
        
        JSONArray esbResponseArray = esbRestResponse.getBody().getJSONArray("networks");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/account/get-pricing/outbound?api_key="
                        + connectorProperties.getProperty("apiKey") + "&api_secret="
                        + connectorProperties.getProperty("apiSecret") + "&country=US";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray apiResponseArray = apiRestResponse.getBody().getJSONArray("networks");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("country"),
                apiRestResponse.getBody().getString("country"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("code"), apiResponseArray.getJSONObject(0)
                .getString("code"));
    }
    
    /**
     * Positive test case for sendMessage method with mandatory parameters.
     */
    @Test(priority = 1, description = "nexmo {sendMessage} integration test with mandatory parameters.")
    public void testSendMessageWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:sendMessage");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendMessage_mandatory.json");
        
        JSONArray esbMessagesArray = esbRestResponse.getBody().getJSONArray("messages");
        String messageId = esbMessagesArray.getJSONObject(0).getString("message-id");
        connectorProperties.put("messageId", messageId);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/search/message?api_key="
                        + connectorProperties.getProperty("apiKey") + "&api_secret="
                        + connectorProperties.getProperty("apiSecret") + "&id=" + messageId;
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("message-count"), 1);
        Assert.assertEquals(esbMessagesArray.getJSONObject(0).getString("to"), apiRestResponse.getBody()
                .getString("to"));
        Assert.assertEquals(esbMessagesArray.getJSONObject(0).getString("message-price"), apiRestResponse.getBody()
                .getString("price"));
        Assert.assertTrue(apiRestResponse.getBody().getString("body")
                .contains(connectorProperties.getProperty("message")));
        
    }
    
    /**
     * Positive test case for sendMessage method with optional parameters.
     */
    @Test(dependsOnMethods = { "testSendMessageWithMandatoryParameters" }, description = "nexmo {sendMessage} integration test with optional parameters.")
    public void testSendMessageWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:sendMessage");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendMessage_optional.json");
        
        JSONArray esbMessagesArray = esbRestResponse.getBody().getJSONArray("messages");
        String messageId2 = esbMessagesArray.getJSONObject(0).getString("message-id");
        connectorProperties.put("messageId2", messageId2);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/search/message?api_key="
                        + connectorProperties.getProperty("apiKey") + "&api_secret="
                        + connectorProperties.getProperty("apiSecret") + "&id=" + messageId2;
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("message-count"), 1);
        Assert.assertEquals(esbMessagesArray.getJSONObject(0).getString("to"), apiRestResponse.getBody()
                .getString("to"));
        Assert.assertEquals(esbMessagesArray.getJSONObject(0).getString("client-ref"),
                connectorProperties.getProperty("clientRef"));
        Assert.assertTrue(apiRestResponse.getBody().getString("body")
                .contains(connectorProperties.getProperty("message")));
    }
    
    /**
     * Negative test case for sendMessage method.
     */
    @Test(dependsOnMethods = { "testSendMessageWithOptionalParameters" }, description = "nexmo {sendMessage} integration test with negative cases.")
    public void testSendMessageWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:sendMessage");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendMessage_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/sms/json";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_sendMessage_negative.json");
        
        JSONArray esbMessagesArray = esbRestResponse.getBody().getJSONArray("messages");
        JSONArray apiMessagesArray = apiRestResponse.getBody().getJSONArray("messages");
        
        Assert.assertEquals(esbMessagesArray.getJSONObject(0).getString("status"), apiMessagesArray.getJSONObject(0)
                .getString("status"));
        Assert.assertEquals(esbMessagesArray.getJSONObject(0).getString("error-text"), apiMessagesArray
                .getJSONObject(0).getString("error-text"));
        
    }
    
    /**
     * Positive test case for searchMessage method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testSendMessageWithMandatoryParameters" }, description = "nexmo {searchMessage} integration test with mandatory parameters.")
    public void testSearchMessageWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchMessage");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchMessage_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/search/message?api_key="
                        + connectorProperties.getProperty("apiKey") + "&api_secret="
                        + connectorProperties.getProperty("apiSecret") + "&id="
                        + connectorProperties.getProperty("messageId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("to"), apiRestResponse.getBody().getString("to"));
        Assert.assertEquals(esbRestResponse.getBody().getString("body"), apiRestResponse.getBody().getString("body"));
        Assert.assertEquals(esbRestResponse.getBody().getString("network"),
                apiRestResponse.getBody().getString("network"));
    }
    
    /**
     * Positive test case for searchMessages method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testSendMessageWithNegativeCase" }, description = "nexmo {searchMessages} integration test with mandatory parameters.")
    public void testSearchMessagesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchMessages");
        
        final String currentDateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        connectorProperties.setProperty("currentDateString", currentDateString);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchMessages_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/search/messages?api_key="
                        + connectorProperties.getProperty("apiKey") + "&api_secret="
                        + connectorProperties.getProperty("apiSecret") + "&date=" + currentDateString + "&to="
                        + connectorProperties.getProperty("recipientPhoneNumber");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbItemsArray = esbRestResponse.getBody().getJSONArray("items");
        JSONArray apiItemsArray = apiRestResponse.getBody().getJSONArray("items");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("count"), apiRestResponse.getBody().getString("count"));
        if (apiItemsArray.length() > 0 && esbItemsArray.length() > 0) {
            Assert.assertEquals(esbItemsArray.length(), apiItemsArray.length());
            Assert.assertEquals(esbItemsArray.getJSONObject(0).getString("body"), apiItemsArray.getJSONObject(0)
                    .getString("body"));
            Assert.assertEquals(esbItemsArray.getJSONObject(0).getString("message-id"), apiItemsArray.getJSONObject(0)
                    .getString("message-id"));
        }else {
            Assert.assertTrue(false);
        }
    }
    
    /**
     * Positive test case for searchMessages method with optional parameters.
     */
    @Test(dependsOnMethods = { "testSendMessageWithOptionalParameters" }, description = "nexmo {searchMessages} integration test with optional parameters.")
    public void testSearchMessagesWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchMessages");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchMessages_optional.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/search/messages?api_key="
                        + connectorProperties.getProperty("apiKey") + "&api_secret="
                        + connectorProperties.getProperty("apiSecret") + "&ids="
                        + connectorProperties.getProperty("messageId") + "&ids="
                        + connectorProperties.getProperty("messageId2");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbItemsArray = esbRestResponse.getBody().getJSONArray("items");
        JSONArray apiItemsArray = apiRestResponse.getBody().getJSONArray("items");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("count"), apiRestResponse.getBody().getString("count"));
        if (apiItemsArray.length() > 0 && esbItemsArray.length() > 0) {
            Assert.assertEquals(esbItemsArray.length(), apiItemsArray.length());
            Assert.assertEquals(esbItemsArray.getJSONObject(0).getString("body"), apiItemsArray.getJSONObject(0)
                    .getString("body"));
            Assert.assertEquals(esbItemsArray.getJSONObject(0).getString("message-id"), apiItemsArray.getJSONObject(0)
                    .getString("message-id"));
        }else {
            Assert.assertTrue(false);
        }
    }
    
    /**
     * Negative test case for searchMessages method.
     */
    @Test(priority = 1, description = "nexmo {searchMessages} integration test with negative cases.")
    public void testSearchMessagesWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchMessages");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchMessages_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/search/messages?api_key="
                        + connectorProperties.getProperty("apiKey") + "&api_secret="
                        + connectorProperties.getProperty("apiSecret") + "&date="
                        + connectorProperties.getProperty("invalidDate") + "&to="
                        + connectorProperties.getProperty("recipientPhoneNumber");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("error-code"),
                apiRestResponse.getBody().getString("error-code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error-code-label"), apiRestResponse.getBody()
                .getString("error-code-label"));
    }
    
    /**
     * Positive test case for textToSpeech method with mandatory parameters.
     */
    @Test(priority = 1, description = "nexmo {textToSpeech} integration test with mandatory parameters.")
    public void testTextToSpeechWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:textToSpeech");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_textToSpeech_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("status"), 0);
        Assert.assertEquals(esbRestResponse.getBody().getString("to"),
                connectorProperties.getProperty("recipientPhoneNumber"));
    }
    
    /**
     * Positive test case for textToSpeech method with optional parameters.
     */
    @Test(priority = 1, description = "nexmo {textToSpeech} integration test with optional parameters.")
    public void testTextToSpeechWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:textToSpeech");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_textToSpeech_optional.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("status"), 0);
        Assert.assertEquals(esbRestResponse.getBody().getString("to"),
                connectorProperties.getProperty("recipientPhoneNumber"));
    }
    
    /**
     * Negative test case for textToSpeech method.
     */
    @Test(priority = 1, description = "nexmo {textToSpeech} integration test with negative cases.")
    public void testTextToSpeechWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:textToSpeech");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_textToSpeech_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/tts/json?api_key="
                        + connectorProperties.getProperty("apiKey") + "&api_secret="
                        + connectorProperties.getProperty("apiSecret");
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_textToSpeech_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("status"), apiRestResponse.getBody().getInt("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error-text"),
                apiRestResponse.getBody().getString("error-text"));
    }
    
    /**
     * Positive test case for makeCall method with mandatory parameters.
     */
    @Test(priority = 1, description = "nexmo {makeCall} integration test with mandatory parameters.")
    public void testMakeCallWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:makeCall");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_makeCall_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("status"), 0);
        Assert.assertEquals(esbRestResponse.getBody().getString("to"),
                connectorProperties.getProperty("recipientPhoneNumber"));
    }
    
    /**
     * Positive test case for makeCall method with optional parameters.
     */
    @Test(priority = 1, description = "nexmo {makeCall} integration test with optional parameters.")
    public void testMakeCallWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:makeCall");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_makeCall_optional.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("status"), 0);
        Assert.assertEquals(esbRestResponse.getBody().getString("to"),
                connectorProperties.getProperty("recipientPhoneNumber"));
    }
    
    /**
     * Negative test case for makeCall method.
     */
    @Test(priority = 1, description = "nexmo {makeCall} integration test with negative cases.")
    public void testMakeCallWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:makeCall");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_makeCall_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/call/json";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_makeCall_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getInt("status"), apiRestResponse.getBody().getInt("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error-text"),
                apiRestResponse.getBody().getString("error-text"));
    }
    
    /**
     * Positive test case for searchInboundNumbers method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "nexmo {searchInboundNumbers} integration test with mandatory parameters.")
    public void testSearchInboundNumbersWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchInboundNumbers");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchInboundNumbers_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/number/search?api_key="
                        + connectorProperties.getProperty("apiKey") + "&api_secret="
                        + connectorProperties.getProperty("apiSecret") + "&country=US";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("count"), apiRestResponse.getBody().getString("count"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("numbers").getJSONObject(0).getString("msisdn"),
                apiRestResponse.getBody().getJSONArray("numbers").getJSONObject(0).getString("msisdn"));
    }
    
    /**
     * Positive test case for searchInboundNumbers method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSearchInboundNumbersWithMandatoryParameters" }, description = "nexmo {searchInboundNumbers} integration test with optional parameters.")
    public void testSearchInboundNumbersWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchInboundNumbers");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchInboundNumbers_optional.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/number/search?api_key="
                        + connectorProperties.getProperty("apiKey") + "&api_secret="
                        + connectorProperties.getProperty("apiSecret") + "&country=US&size=2&index=1";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("numbers").length(), apiRestResponse.getBody()
                .getJSONArray("numbers").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("numbers").getJSONObject(0).getString("msisdn"),
                apiRestResponse.getBody().getJSONArray("numbers").getJSONObject(0).getString("msisdn"));
    }
    
    /**
     * Negative test case for searchInboundNumbers method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSearchInboundNumbersWithOptionalParameters" }, description = "nexmo {searchInboundNumbers} integration test with negative cases.")
    public void testSearchInboundNumbersWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchInboundNumbers");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchInboundNumbers_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/number/search?api_key="
                        + connectorProperties.getProperty("apiKey") + "&api_secret="
                        + connectorProperties.getProperty("apiSecret") + "&country=INVALID";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 420);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 420);
    }
    
    /**
     * Positive test case for searchRejectedMessages method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSearchMessagesWithMandatoryParameters" }, description = "nexmo {searchRejectedMessages} integration test with mandatory parameters.")
    public void testSearchRejectedMessagesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchRejectedMessages");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchRejectedMessages_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/search/rejections?api_key="
                        + connectorProperties.getProperty("apiKey") + "&api_secret="
                        + connectorProperties.getProperty("apiSecret") + "&date="
                        + connectorProperties.getProperty("currentDateString");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("count"), apiRestResponse.getBody().getString("count"));
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).getString("date-received"),
                apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).getString("date-received"));
    }
    
    /**
     * Positive test case for searchRejectedMessages method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSearchRejectedMessagesWithMandatoryParameters" }, description = "nexmo {searchRejectedMessages} integration test with optional parameters.")
    public void testSearchRejectedMessagesWithOptionalParameters() throws IOException, JSONException,
            InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:searchRejectedMessages");
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchRejectedMessages_optional.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/search/rejections?api_key="
                        + connectorProperties.getProperty("apiKey") + "&api_secret="
                        + connectorProperties.getProperty("apiSecret") + "&date="
                        + connectorProperties.getProperty("currentDateString") + "&to="
                        + connectorProperties.getProperty("recipientPhoneNumber");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("count"), apiRestResponse.getBody().getString("count"));
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONArray("items").getJSONObject(0).getString("date-received"),
                apiRestResponse.getBody().getJSONArray("items").getJSONObject(0).getString("date-received"));
    }
    
    /**
     * Negative test case for searchRejectedMessages method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testSearchRejectedMessagesWithOptionalParameters" }, description = "nexmo {searchRejectedMessages} integration test with negative cases.")
    public void testSearchRejectedMessagesWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:searchRejectedMessages");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchRejectedMessages_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/search/rejections?api_key="
                        + connectorProperties.getProperty("apiKey") + "&api_secret="
                        + connectorProperties.getProperty("apiSecret") + "&date=";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("error-code"),
                apiRestResponse.getBody().getString("error-code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("error-code-label"), apiRestResponse.getBody()
                .getString("error-code-label"));
    }
    
}
