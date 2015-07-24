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

package org.wso2.carbon.connector.integration.test.mandrill;

import java.io.IOException;
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

public class MandrillConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private final Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private final Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private String apiEndpointUrl;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("mandrill-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Accept", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        apiEndpointUrl = connectorProperties.getProperty("apiUrl") + "/api/1.0";
    }
    
    /**
     * Positive test case for sendMessage method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "mandrill {sendMessage} integration test with mandatory parameters.")
    public void testSendMessageWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:sendMessage");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendMessage_mandatory.json");
        final JSONArray responseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        /*
         * Since the message details cannot be retrieved using testSearchInformationOfMessage method until the
         * message is delivered, which takes an variable amount of time, assertion is done based on the
         * response received for sendMessage method. Therefore an optional case for sendMessage is not
         * included in the test suite as it would be identical to the mandatory case.
         */
        
        Assert.assertNotNull(responseArray.getJSONObject(0).getString("_id"));
        Assert.assertEquals(responseArray.getJSONObject(0).getString("reject_reason"), "null");
        Assert.assertEquals(responseArray.getJSONObject(0).getString("email"),
                connectorProperties.getProperty("toEmail"));
        Assert.assertEquals(responseArray.getJSONObject(0).getString("status").toLowerCase(), "sent");
    }
    
    /**
     * Negative test case for sendMessage method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "mandrill {sendMessage} integration test with negative case.")
    public void testSendMessageWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:sendMessage");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendMessage_negative.json");
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndpoint = apiEndpointUrl + "/messages/send.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_sendMessage_negative.json");
        JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("status"), apiResponseArray.getJSONObject(0)
                .getString("status"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("status").toLowerCase(),
                "rejected");
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("reject_reason"), apiResponseArray
                .getJSONObject(0).getString("reject_reason"));
    }
    
    /**
     * Positive test case for searchInformationOfMessage method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(groups = { "wso2.esb" }, description = "mandrill {searchInformationOfMessage} integration test with mandatory parameters.")
    public void testSearchInformationOfMessageWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:searchInformationOfMessage");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_searchInformationOfMessage_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/messages/info.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                        "api_searchInformationOfMessage_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("state"), apiRestResponse.getBody().getString("state"));
        Assert.assertEquals(esbRestResponse.getBody().getString("email"), apiRestResponse.getBody().getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getString("sender"), apiRestResponse.getBody()
                .getString("sender"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tags").getString(0), apiRestResponse.getBody()
                .getJSONArray("tags").getString(0));
    }
    
    /**
     * Negative test case for searchInformationOfMessage method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "mandrill {searchInformationOfMessage} integration test with negative case.")
    public void testSearchInformationOfMessageWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:searchInformationOfMessage");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_searchInformationOfMessage_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/messages/info.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                        "api_searchInformationOfMessage_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
    }
    
    /**
     * Positive test case for searchMessages method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "mandrill {searchMessages} integration test with mandatory parameters.", dependsOnMethods = { "testSendMessageWithMandatoryParameters" })
    public void testSearchMessagesWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:searchMessages");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchMessages_mandatory.json");
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndpoint = apiEndpointUrl + "/messages/search.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_searchMessages_mandatory.json");
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("_id"), apiResponseArray.getJSONObject(0)
                .getString("_id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("subject"), apiResponseArray.getJSONObject(0)
                .getString("subject"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("email"), apiResponseArray.getJSONObject(0)
                .getString("email"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("sender"), apiResponseArray.getJSONObject(0)
                .getString("sender"));
    }
    
    /**
     * Positive test case for searchMessages method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "mandrill {searchMessages} integration test with optional parameters.", dependsOnMethods = { "testSendMessageWithMandatoryParameters" })
    public void testSearchMessagesWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:searchMessages");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchMessages_optional.json");
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndpoint = apiEndpointUrl + "/messages/search.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_searchMessages_optional.json");
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbRestResponse.getBody().length(), apiRestResponse.getBody().length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("_id"), apiResponseArray.getJSONObject(0)
                .getString("_id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("subject"), apiResponseArray.getJSONObject(0)
                .getString("subject"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("sender"),
                connectorProperties.getProperty("fromEmail"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("sender"),
                connectorProperties.getProperty("fromEmail"));
    }
    
    /**
     * Negative test case for searchMessages method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "mandrill {searchMessages} integration test with negative case.")
    public void testSearchMessagesWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:searchMessages");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchMessages_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/messages/search.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_searchMessages_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("code"), apiRestResponse.getBody().getString("code"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
    }
    
    /**
     * Positive test case for searchTimeSeriesOfMessages method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "mandrill {searchTimeSeriesOfMessages} integration test with optional parameters.")
    public void testSearchTimeSeriesOfMessagesWithOptionalParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:searchTimeSeriesOfMessages");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_searchTimeSeriesOfMessages_optional.json");
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndpoint = apiEndpointUrl + "/messages/search-time-series.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                        "api_searchTimeSeriesOfMessages_optional.json");
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("time"), apiResponseArray.getJSONObject(0)
                .getString("time"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("sent"), apiResponseArray.getJSONObject(0)
                .getString("sent"));
    }
    
    /**
     * Negative test case for searchTimeSeriesOfMessages method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "mandrill {searchTimeSeriesOfMessages} integration test with negative case.")
    public void testSearchTimeSeriesOfMessagesWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:searchTimeSeriesOfMessages");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_searchTimeSeriesOfMessages_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/messages/search-time-series.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                        "api_searchTimeSeriesOfMessages_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
    }
    
    /**
     * Positive test case for searchMessageContent method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, enabled = false,description = "mandrill {searchMessageContent} integration test with mandatory parameters.")
    public void testSearchMessageContentWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:searchMessageContent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchMessageContent_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/messages/content.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                        "api_searchMessageContent_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("to").getString("email"),
                apiRestResponse.getBody().getJSONObject("to").getString("email"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tags").getString(0),
                apiRestResponse.getBody().getJSONArray("tags").getString(0));
        
        Assert.assertEquals(esbRestResponse.getBody().getString("subject"),
                apiRestResponse.getBody().getString("subject"));
        Assert.assertEquals(esbRestResponse.getBody().getString("from_email"),
                apiRestResponse.getBody().getString("from_email"));
    }
    
    /**
     * Negative test case for searchMessageContent method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "mandrill {searchMessageContent} integration test with negative case.")
    public void testSearchMessageContentWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:searchMessageContent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_searchMessageContent_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/messages/content.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_searchMessageContent_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
    }
    
    /**
     * Positive test case for listSendersCalls method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException 
     */
    @Test(groups = { "wso2.esb" }, description = "mandrill {listSendersCalls} integration test with mandatory parameters.")
    public void testListSendersCallsWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listSendersCalls");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSendersCalls_mandatory.json");
        final JSONArray esbResponseArray = new JSONArray(esbRestResponse.getBody().getString("output"));
        
        final String apiEndpoint = apiEndpointUrl + "/senders/list.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap, "api_listSendersCalls_mandatory.json");
        final JSONArray apiResponseArray = new JSONArray(apiRestResponse.getBody().getString("output"));
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("sent"), apiResponseArray.getJSONObject(0)
                .getString("sent"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("opens"), apiResponseArray.getJSONObject(0)
                .getString("opens"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("address"), apiResponseArray.getJSONObject(0)
                .getString("address"));
    }
    
    /**
     * Positive test case for getInformationOfSendersCalls method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = { "wso2.esb" }, description = "mandrill {getInformationOfSendersCalls} integration test with mandatory parameters.")
    public void testGetInformationOfSendersCallsWithMandatoryParameters() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getInformationOfSendersCalls");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_getInformationOfSendersCalls_mandatory.json");
        
        final String apiEndpoint = apiEndpointUrl + "/senders/info.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                        "api_getInformationOfSendersCalls_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("sent"), apiRestResponse.getBody().getString("sent"));
        Assert.assertEquals(esbRestResponse.getBody().getString("unique_opens"),
                apiRestResponse.getBody().getString("unique_opens"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("stats").getJSONObject("today").getString("sent"),
                apiRestResponse.getBody().getJSONObject("stats").getJSONObject("today").getString("sent"));
    }
    
    /**
     * Negative test case for getInformationOfSendersCalls method.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException 
     * @throws NumberFormatException 
     */
    @Test(groups = { "wso2.esb" }, description = "mandrill {getInformationOfSendersCalls} integration test with negative case.")
    public void testGetInformationOfSendersCallsWithNegativeCase() throws IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:getInformationOfSendersCalls");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_getInformationOfSendersCalls_negative.json");
        
        final String apiEndpoint = apiEndpointUrl + "/senders/info.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap,
                        "api_getInformationOfSendersCalls_negative.json");
        
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), apiRestResponse.getBody()
                .getString("status"));
        Assert.assertEquals(esbRestResponse.getBody().getString("message"),
                apiRestResponse.getBody().getString("message"));
        Assert.assertEquals(esbRestResponse.getBody().getString("name"), apiRestResponse.getBody().getString("name"));
    }
    
}
