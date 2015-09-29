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

package org.wso2.carbon.connector.integration.test.twilioRest;

import org.apache.synapse.core.axis2.SOAPUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.integration.test.common.Base64Coder;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class twilioRestConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        init("twilioRest-connector-1.0.0");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");
        final String authString = connectorProperties.getProperty("accountSid") + ":" + connectorProperties.getProperty("authToken");
        final String base64AuthString = Base64Coder.encodeString(authString);
        apiRequestHeadersMap.put("Authorization", "Basic " + base64AuthString);
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
    }

    /**
     * Positive test case for createSubAccount method with optional parameters.
     */
    @Test(priority = 1, description = "twilioRest {createSubAccount} integration test with optional parameters.")
    public void testcreateSubAccountWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createSubAccount");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") +
                "/Accounts.json?FriendlyName=" + connectorProperties.getProperty("friendlyName");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createSubAccountOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("friendly_name"), apiRestResponse.getBody().getJSONArray("accounts").getJSONObject(0).get("friendly_name"));
        Assert.assertEquals(esbRestResponse.getBody().get("friendly_name"), connectorProperties.getProperty("friendlyName"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
    }

    /**
     * Positive test case for getAccount method with mandatory parameters.
     */
    @Test(priority = 1, description = "twilioRest {getAccount} integration test with mandatory parameters.")
    public void testgetAccountWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getAccount");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/"
                + connectorProperties.getProperty("subAccountSid") + ".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccountMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
    }

    /**
     * Negative test case for getAccount method.
     */
    @Test(priority = 1, description = "twilioRest {getAccount} integration test negative case.")
    public void testgetAccountWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getAccount");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" +
                connectorProperties.getProperty("subAccountSidNeg") + ".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccountNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for getAccountList method with optional parameters.
     */
    @Test(priority = 1, description = "twilioRest {getAccountList} integration test with optional parameter.")
    public void testgetAccountListWithOptionalParameters() throws IOException, JSONException {

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts.json?Status=" +
                connectorProperties.getProperty("status");
        esbRequestHeadersMap.put("Action", "urn:getAccountsList");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccountListOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("accounts").getJSONObject(0).get("status"),
                apiRestResponse.getBody().getJSONArray("accounts").getJSONObject(0).get("status"));
    }

    /**
     * Negative test case for getAccountList method.
     */
    @Test(priority = 1, description = "twilioRest {getAccountList} integration test negative case.")
    public void testgetAccountListWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getAccountList");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts.json?Status=" +
                connectorProperties.getProperty("statusNeg");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAccountListNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());

    }

    /**
     * Positive test case for updateAccount method with mandatory parameters.
     */
    @Test(priority = 1, description = "twilioRest {updateAccount} integration test with mandatory parameter.")
    public void testupdateAccountWithMandatoryParameters() throws IOException, JSONException {

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" +
                connectorProperties.getProperty("subAccountSid") + ".json";
        esbRequestHeadersMap.put("Action", "urn:updateAccount");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "updateAccountMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("sid"), apiRestResponse.getBody().get("sid"));
    }


    /**
     * Positive test case for updateAccount method with optional parameters.
     */
    @Test(priority = 1, description = "twilioRest {updateAccount} integration test with optional parameter.")
    public void testupdateAccountWithOptionalParameters() throws IOException, JSONException {

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" +
                connectorProperties.getProperty("subAccountSid") + ".json";
        esbRequestHeadersMap.put("Action", "urn:updateAccount");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "updateAccountOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
    }


    /**
     * Negative test case for updateAccount method.
     */
    @Test(priority = 1, description = "twilioRest {updateAccount} integration test negative case.")
    public void testupdateAccountWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updateAccount");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") +
                "/Accounts/" + connectorProperties.getProperty("subAccountSidNeg") + ".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "updateAccountNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for sendSms method with mandatory parameters.
     */
    @Test(priority = 1, description = "twilioRest {sendSms} integration test with mandatory parameters.")
    public void testsendSmsWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:sendSms");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid") +
                "/SMS/Messages.json";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "sendSmsMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("from"), apiRestResponse.getBody().getJSONArray("sms_messages").getJSONObject(0).get("from"));
    }

    /**
     * Positive test case for sendSms method with optional parameters.
     */
    @Test(priority = 1, description = "twilioRest {sendSms} integration test with optional parameters.")
    public void testsendSmsWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:sendSms");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid") +
                "/SMS/Messages.json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "sendSmsOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("from"), apiRestResponse.getBody().getJSONArray("sms_messages").getJSONObject(0).get("from"));
        Assert.assertEquals(esbRestResponse.getBody().get("sid"), apiRestResponse.getBody().getJSONArray("sms_messages").getJSONObject(0).get("sid"));
    }

    /**
     * Negative test case for sendSms method.
     */
    @Test(priority = 1, description = "twilioRest {sendSms} integration test negative case.")
    public void testsendSmsWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:sendSms");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSidNeg") +
                "/SMS/Messages.json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "sendSmsNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }


    /**
     * Positive test case for getSms method with mandatory parameters.
     */
    @Test(priority = 1, description = "twilioRest {getSms} integration test with mandatory parameters.")
    public void testgetSmsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getSms");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid")
                + "/SMS/Messages/" + connectorProperties.getProperty("messageSid") + ".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getSmsMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));
    }


    /**
     * Negative test case for getSms method.
     */
    @Test(priority = 1, description = "twilioRest {getSms} integration test negative case.")
    public void testgetSmsWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getSms");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid")
                + "/SMS/Messages/" + connectorProperties.getProperty("messageSidNeg") + ".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getSmsNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());

    }


    /**
     * Positive test case for getSmsList method with optional parameters.
     */
    @Test(priority = 1, description = "twilioRest {getSmsList} integration test with optional parameter.")
    public void testgetSmsListWithOptionalParameters() throws IOException, JSONException {

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" +
                connectorProperties.getProperty("accountSid") + "/SMS/Messages.json?From=" + connectorProperties.getProperty("from");
        esbRequestHeadersMap.put("Action", "urn:getSmsList");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getSmsListOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("end"), apiRestResponse.getBody().get("end"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("sms_messages").getJSONObject(0).get("sid"),
                apiRestResponse.getBody().getJSONArray("sms_messages").getJSONObject(0).get("sid"));

    }

    /**
     * Negative test case for getSmsList method.
     */
    @Test(priority = 1, description = "twilioRest {getSmsList} integration test negative case.")
    public void testgetSmsListWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getSmsList");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion")
                + "/Accounts/" + connectorProperties.getProperty("accountSid") + "/SMS/Messages.json?From=" + connectorProperties.getProperty("fromNeg");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getSmsListNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for getNewCall method with mandatory parameters.
     */
    @Test(priority = 1, description = "twilioRest {getNewCall} integration test with mandatory parameters.")
    public void testgetNewCallWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getNewCall");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" +
                connectorProperties.getProperty("accountSid") + "/Calls.json";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getNewCallMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
//            Assert.assertEquals(esbRestResponse.getBody().get("sid"), apiRestResponse.getBody().getJSONArray("calls").getJSONObject(0).get("sid"));
    }

    /**
     * Positive test case for getNewCall method with optional parameters.
     */
    @Test(priority = 1, description = "twilioRest {getNewCall} integration test with optional parameters.")
    public void testgetNewCallWithoptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getNewCall");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" +
                connectorProperties.getProperty("accountSid") + "/Calls.json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getNewCallOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
//            Assert.assertEquals(esbRestResponse.getBody().get("sid"), apiRestResponse.getBody().getJSONArray("calls").getJSONObject(0).get("sid"));
    }

    /**
     * Negative test case for getNewCall method.
     */
    @Test(priority = 1, description = "twilioRest {getNewCall} integration test negative case.")
    public void testgetNewCallWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getNewCall");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" +
                connectorProperties.getProperty("accountSid") + "/Calls.json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getNewCallNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for getCall method with mandatory parameters.
     */
    @Test(priority = 1, description = "twilioRest {getCall} integration test with mandatory parameters.")
    public void testgetCallWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getCall");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid")
                + "/Calls/" + connectorProperties.getProperty("callSid") + ".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCallMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }


    /**
     * Negative test case for getCall method.
     */
    @Test(priority = 1, description = "twilioRest {getCall} integration test negative case.")
    public void testgetCallWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getCall");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid")
                + "/Calls/" + connectorProperties.getProperty("callSidNeg") + ".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCallNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }


    /**
     * Positive test case for getCallList method with optional parameters.
     */
    @Test(priority = 1, description = "twilioRest {getCallList} integration test with optional parameter.")
    public void testgetCallListWithOptionalParameters() throws IOException, JSONException {

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid")
                + "/Calls.json?From=" + connectorProperties.getProperty("fromCall");
        esbRequestHeadersMap.put("Action", "urn:getCallList");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCallListOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("end"), apiRestResponse.getBody().get("end"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("calls").getJSONObject(0).get("from"), apiRestResponse.getBody().getJSONArray("calls").getJSONObject(0).get("from"));
    }


    /**
     * Negative test case for getCallList method.
     */
    @Test(priority = 1, description = "twilioRest {getCallList} integration test negative case.")
    public void testgetCallListWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getCallList");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion")
                + "/Accounts/" + connectorProperties.getProperty("accountSid") + "/Calls.json?From=" + connectorProperties.getProperty("fromNeg");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getCallListNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }


    /*
     * Positive test case for getRecording method with mandatory parameters.
     */
    @Test(priority = 1, description = "twilioRest {getRecording} integration test with mandatory parameters.")
    public void testgetRecordingWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getRecording");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid")
                + "/Recordings/" + connectorProperties.getProperty("recordingSid") + ".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getRecordingMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }


    /**
     * Negative test case for getRecording method.
     */
    @Test(priority = 1, description = "twilioRest {getRecording} integration test negative case.")
    public void testgetRecordingWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getRecording");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid")
                + "/Recordings/" + connectorProperties.getProperty("recordingSidNeg") + ".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getRecordingNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());

    }

    /**
     * Positive test case for getRecordingList method with optional parameters.
     */
    @Test(priority = 1, description = "twilioRest {getRecordingList} integration test with optional parameter.")
    public void testgetRecordingListWithOptionalParameters() throws IOException, JSONException {
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid")
                + "/Recordings.json?CallSid=" + connectorProperties.getProperty("callSid");
        esbRequestHeadersMap.put("Action", "urn:getRecordingList");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getRecordingListOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("end"), apiRestResponse.getBody().get("end"));
//                Assert.assertEquals(esbRestResponse.getBody().getJSONArray("recordings").getJSONObject(0).get("call_sid"),
//                        apiRestResponse.getBody().getJSONArray("recordings").getJSONObject(0).get("call_sid"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }


    /**
     * Negative test case for getRecordingList method.
     */
    @Test(priority = 1, description = "twilioRest {getRecordingList} integration test negative case.")
    public void testgetRecordingListWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getRecordingList");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid")
                + "/Recordings.json?CallSid=" + connectorProperties.getProperty("callSidNeg");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getRecordingListNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }


    /**
     * Positive test case for deleteRecording method with mandatory parameters.
     * If you have valid recordingSid then change enable as true
     */
    @Test(enabled= false,priority = 1, description = "twilioRest {deleteRecording} integration test with mandatory parameters.")
    public void testdeleteRecordingWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deleteRecording");
        String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/"+connectorProperties.getProperty("apiVersion")+"/Accounts/"+connectorProperties.getProperty("accountSid")
                            +"/Recordings/" + connectorProperties.getProperty("recordingSid")+".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "deleteRecordingMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
   }


    /**
     * Negative test case for deleteRecording method.
     */
    @Test(priority = 1, description = "twilioRest {deleteRecording} integration test negative case.")
    public void testdeleteRecordingWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deleteRecording");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid")
                + "/Recordings/" + connectorProperties.getProperty("recordingSidNeg") + ".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "deleteRecordingNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());

    }


    /**
     * Positive test case for getTranscription method with mandatory parameters.
     */
    @Test(priority = 1, description = "twilioRest {getTranscription} integration test with mandatory parameters.")
    public void testgetTranscriptionWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getTranscription");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid")
                + "/Transcriptions/" + connectorProperties.getProperty("transcriptionSid") + ".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getTranscriptionMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
//                Assert.assertEquals(esbRestResponse.getBody().get("sid"),apiRestResponse.getBody().get("sid"));
    }

    /**
     * Negative test case for getTranscription method.
     */
    @Test(priority = 1, description = "twilioRest {getTranscription} integration test negative case.")
    public void testgetTranscriptionWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getTranscription");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid")
                + "/Transcriptions/" + connectorProperties.getProperty("transcriptionSidNeg") + ".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getTranscriptionNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }


    /**
     * Positive test case for getTranscriptionList method with mandatory parameters.
     */
    @Test(priority = 1, description = "twilioRest {getTranscriptionList} integration test with mandatory parameter.")
    public void testgetTranscriptionListWithMandatoryParameters() throws IOException, JSONException {

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid")
                + "/Transcriptions.json";
        esbRequestHeadersMap.put("Action", "urn:getTranscriptionList");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getTranscriptionListMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("end"), apiRestResponse.getBody().get("end"));
//                Assert.assertEquals(esbRestResponse.getBody().getJSONArray("transcriptions").getJSONObject(0).get("sid"),
//                        apiRestResponse.getBody().getJSONArray("transcriptions").getJSONObject(0).get("sid"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());

    }

    /**
     * Negative test case for getTranscriptionList method.
     */
    @Test(priority = 1, description = "twilioRest {getTranscriptionList} integration test negative case.")
    public void testgetTranscriptionListWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getTranscriptionList");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid")
                + "/Transcriptions.json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getTranscriptionListNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for modifyLiveCall method with mandatory parameters.
     */
    @Test(priority = 1, description = "twilioRest {modifyLiveCall} integration test with mandatory parameters.")
    public void testmodifyLiveCallWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:modifyLiveCall");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") +
                "/Accounts/" + connectorProperties.getProperty("accountSid") + "/Calls/" + connectorProperties.getProperty("callSid") + ".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "modifyLiveCallMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("sid"), apiRestResponse.getBody().get("sid"));
        Assert.assertEquals(esbRestResponse.getBody().get("from"), apiRestResponse.getBody().get("from"));
    }

    /**
     * Positive test case for modifyLiveCall method with optional parameters.
     */
    @Test(priority = 1, description = "twilioRest {modifyLiveCall} integration test with optional parameters.")
    public void testmodifyLiveCallWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:modifyLiveCall");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") +
                "/Accounts/" + connectorProperties.getProperty("accountSid") + "/Calls/" + connectorProperties.getProperty("callSid") + ".json";
        System.out.println("\n\n\n\n\n\n\n\n apiendpoint = " + apiEndPoint );
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "modifyLiveCallOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("sid"),apiRestResponse.getBody().get("sid"));
        Assert.assertEquals(esbRestResponse.getBody().get("from"),apiRestResponse.getBody().get("from"));

    }

    /**
     * Negative test case for modifyLiveCall method.
     */
    @Test(priority = 1, description = "twilioRest {modifyLiveCall} integration test negative case.")
    public void testmodifyLiveCallWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:modifyLiveCall");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") +
                "/Accounts/" + connectorProperties.getProperty("acountSid") + "/Calls/" + connectorProperties.getProperty("callSid") + ".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "modifyLiveCallNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for purchasePhoneNumber method with mandatory parameters.
     */
    @Test(priority = 1, description = "twilioRest {purchasePhoneNumber} integration test with mandatory parameters.")
    public void testpurchasePhoneNumberWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:purchasePhoneNumber");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") +
                "/Accounts/" + connectorProperties.getProperty("accountSid") + "/IncomingPhoneNumbers.json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "purchasePhoneNumberMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
//                Assert.assertEquals(esbRestResponse.getBody().get("sid"), apiRestResponse.getBody().getJSONArray("incoming_phone_numbers").getJSONObject(0).get("sid"));
//                Assert.assertEquals(esbRestResponse.getBody().get("phone_number"), apiRestResponse.getBody().getJSONArray("incoming_phone_numbers").getJSONObject(0).get("phone_number"));
    }

    /**
     * Positive test case for purchasePhoneNumber method with optional parameters.
     */
    @Test(priority = 1, description = "twilioRest {purchasePhoneNumber} integration test with optional parameters.")
    public void testpurchasePhoneNumberWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:purchasePhoneNumber");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") +
                "/Accounts/" + connectorProperties.getProperty("accountSid") + "/IncomingPhoneNumbers.json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "purchasePhoneNumberOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
//                Assert.assertEquals(esbRestResponse.getBody().get("sid"), apiRestResponse.getBody().getJSONArray("incoming_phone_numbers").getJSONObject(0).get("sid"));
//                Assert.assertEquals(esbRestResponse.getBody().get("phone_number"), apiRestResponse.getBody().getJSONArray("incoming_phone_numbers").getJSONObject(0).get("phone_number"));
//                Assert.assertEquals(esbRestResponse.getBody().get("friendly_name"), apiRestResponse.getBody().getJSONArray("incoming_phone_numbers").getJSONObject(0).get("friendly_name"));

    }

    /**
     * Negative test case for purchasePhoneNumber method.
     */
    @Test(priority = 1, description = "twilioRest {purchasePhoneNumber} integration test negative case.")
    public void testpurchasePhoneNumberWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:purchasePhoneNumber");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") +
                "/Accounts/" + connectorProperties.getProperty("accountSid") + "/IncomingPhoneNumbers.json";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "purchasePhoneNumberNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);

    }

    /**
     * Positive test case for getIncomingPhoneNumber method with mandatory parameters.
     */
    @Test(priority = 1, description = "twilioRest {getIncomingPhoneNumber} integration test with mandatory parameters.")
    public void testgetIncomingPhoneNumberWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getIncomingPhoneNumber");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid")
                + "/IncomingPhoneNumbers/" + connectorProperties.getProperty("incomingCallerId") + ".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getIncomingPhoneNumberMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getIncomingPhoneNumber method.
     */
    @Test(priority = 1, description = "twilioRest {getIncomingPhoneNumber} integration test negative case.")
    public void testgetIncomingPhoneNumberWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getIncomingPhoneNumber");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid")
                + "/IncomingPhoneNumbers/" + connectorProperties.getProperty("incomingCallerIdNeg") + ".json";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getIncomingPhoneNumberNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }


    /**
     * Positive test case for getIncomingPhoneNumberList method with optional parameters.
     */
    @Test(priority = 1, description = "twilioRest {getIncomingPhoneNumberList} integration test with optional parameters.")
    public void testgetIncomingPhoneNumberListWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getIncomingPhoneNumberList");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid")
                + "/IncomingPhoneNumbers.json?PhoneNumber=" + connectorProperties.getProperty("incomingPhoneNumber");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getIncomingPhoneNumberListOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("incoming_phone_numbers").getJSONObject(0).get("sid"),
                apiRestResponse.getBody().getJSONArray("incoming_phone_numbers").getJSONObject(0).get("sid"));
    }

    /**
     * Negative test case for getIncomingPhoneNumberList method.
     */
    @Test(priority = 1, description = "twilioRest {getIncomingPhoneNumberList} integration test negative case.")
    public void testgetIncomingPhoneNumberListWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getIncomingPhoneNumberList");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid")
                + "/IncomingPhoneNumbers.json?PhoneNumber=" + connectorProperties.getProperty("phoneNumberNeg");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getIncomingPhoneNumberListNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }


    /**
     * Positive test case for removeIncomingPhoneNumber method with mandatory parameters.
     *
     */
    @Test(enabled= false,priority = 1, description = "twilioRest {removeIncomingPhoneNumber} integration test with mandatory parameters.")
    public void testremoveIncomingPhoneNumberWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:removeIncomingPhoneNumber");
        String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/"+connectorProperties.getProperty("apiVersion")+"/Accounts/"+connectorProperties.getProperty("accountSid")
                +"/IncomingPhoneNumbers/" + connectorProperties.getProperty("incomingCallerId")+".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "removeIncomingPhoneNumberMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Negative test case for removeIncomingPhoneNumber method.
     */
    @Test(priority = 1, description = "twilioRest {removeIncomingPhoneNumber} integration test negative case.")
    public void testremoveIncomingPhoneNumberWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:removeIncomingPhoneNumber");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid")
                + "/IncomingPhoneNumbers/" + connectorProperties.getProperty("incomingCallerIdNeg") + ".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "removeIncomingPhoneNumberNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for updateIncomingPhoneNumber method with mandatory parameters.
     */
    @Test(priority = 1, description = "twilioRest {updateIncomingPhoneNumber} integration test with mandatory parameters.")
    public void testupdateIncomingPhoneNumberWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updateIncomingPhoneNumber");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" +
                connectorProperties.getProperty("accountSid") + "/IncomingPhoneNumbers/" + connectorProperties.getProperty("incomingCallerId") + ".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "updateIncomingPhoneNumberMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("sid"), apiRestResponse.getBody().get("sid"));
    }

    /**
     * Positive test case for updateIncomingPhoneNumber method with optional parameters.
     */
    @Test(priority = 1, description = "twilioRest {updateIncomingPhoneNumber} integration test with optional parameters.")
    public void testupdateIncomingPhoneNumberWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updateIncomingPhoneNumber");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" +
                connectorProperties.getProperty("accountSid") + "/IncomingPhoneNumbers/" + connectorProperties.getProperty("incomingCallerId") + ".json?FriendlyName=" +
                connectorProperties.getProperty("friendlyName");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "updateIncomingPhoneNumberOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        System.out.println("\n\n\n\n\n\n api " + apiRestResponse.getBody().toString());
        System.out.println("\n\n\n\n\n\n esb " + esbRestResponse.getBody().toString());
        Assert.assertEquals(esbRestResponse.getBody().get("friendly_name"), apiRestResponse.getBody().get("friendly_name"));
    }

    /**
     * Negative test case for updateIncomingPhoneNumber method.
     */
    @Test(priority = 1, description = "twilioRest {updateIncomingPhoneNumber} integration test negative case.")
    public void testupdateIncomingPhoneNumberWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updateIncomingPhoneNumber");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" +
                connectorProperties.getProperty("accountSid") + "/IncomingPhoneNumbers/" + connectorProperties.getProperty("incomingCallerIdNeg") + ".json";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "updateIncomingPhoneNumberNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for addOutgoingPhoneNumber method with mandatory parameters.
     */
    @Test(priority = 1, description = "twilioRest {addOutgoingPhoneNumber} integration test with mandatory parameters.")
    public void testaddOutgoingPhoneNumberWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:addOutgoingPhoneNumber");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" +
                connectorProperties.getProperty("accountSid") + "/OutgoingCallerIds.json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "addOutgoingPhoneNumberMandatory.json");

        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
//                 Assert.assertEquals(esbRestResponse.getBody().get("sid"), apiRestResponse.getBody().getJSONArray("outgoing_caller_ids").getJSONObject(0).get("sid"));
    }

    /**
     * Positive test case for addOutgoingPhoneNumber method with optional parameters.
     */
    @Test(priority = 1, description = "twilioRest {addOutgoingPhoneNumber} integration test with optional parameters.")
    public void testaddOutgoingPhoneNumberWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:addOutgoingPhoneNumber");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" +
                connectorProperties.getProperty("accountSid") + "/OutgoingCallerIds.json?Extension=" + connectorProperties.getProperty("extension");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "addOutgoingPhoneNumberOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
//                 Assert.assertEquals(esbRestResponse.getBody().get("sid"), apiRestResponse.getBody().getJSONArray("outgoing_caller_ids").getJSONObject(0).get("sid"));

    }

    /**
     * Negative test case for addOutgoingPhoneNumber method.
     */
    @Test(priority = 1, description = "twilioRest {addOutgoingPhoneNumber} integration test negative case.")
    public void testaddOutgoingPhoneNumberWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:addOutgoingPhoneNumber");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" +
                connectorProperties.getProperty("accountSid") + "/OutgoingCallerIds.json";

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "addOutgoingPhoneNumberNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);

    }

    /**
     * Positive test case for getOutgoingPhoneNumber method with mandatory parameters.
     */
    @Test(priority = 1, description = "twilioRest {getOutgoingPhoneNumber} integration test with mandatory parameters.")
    public void testgetOutgoingPhoneNumberWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getOutgoingPhoneNumber");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" +
                connectorProperties.getProperty("accountSid") + "/OutgoingCallerIds/" + connectorProperties.getProperty("outgoingCallerId") + ".json";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getOutgoingPhoneNumberMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getOutgoingPhoneNumber method.
     */
    @Test(priority = 1, description = "twilioRest {getOutgoingPhoneNumber} integration test negative case.")
    public void testgetOutgoingPhoneNumberWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getOutgoingPhoneNumber");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" +
                connectorProperties.getProperty("accountSid") + "/OutgoingCallerIds/" + connectorProperties.getProperty("outgoingCallerIdNeg") + ".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getOutgoingPhoneNumberNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());

    }

    /**
     * Positive test case for getOutgoingPhoneNumberList method with optional parameters.
     */
    @Test(priority = 1, description = "twilioRest {getOutgoingPhoneNumberList} integration test with optional parameters.")
    public void testgetOutgoingPhoneNumberListWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getOutgoingPhoneNumberList");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" +
                connectorProperties.getProperty("accountSid") + "/OutgoingCallerIds.json?PhoneNumber=" + connectorProperties.getProperty("outgoingPhoneNumber");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getOutgoingPhoneNumberListOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("end"), apiRestResponse.getBody().get("end"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("outgoing_caller_ids").getJSONObject(0).get("phone_number"),
                apiRestResponse.getBody().getJSONArray("outgoing_caller_ids").getJSONObject(0).get("phone_number"));

    }

    /**
     * Negative test case for getOutgoingPhoneNumberList method.
     */
    @Test(priority = 1, description = "twilioRest {getOutgoingPhoneNumberList} integration test negative case.")
    public void testgetOutgoingPhoneNumberListWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getOutgoingPhoneNumberList");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" +
                connectorProperties.getProperty("accountSid") + "/OutgoingCallerIds.json?PhoneNumber=" + connectorProperties.getProperty("phoneNumberNeg");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getOutgoingPhoneNumberListNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for removeOutgoingPhoneNumber method with mandatory parameters.
     */
    @Test(enabled= false,priority = 1, description = "twilioRest {removeOutgoingPhoneNumber} integration test with mandatory parameters.")
    public void testremoveOutgoingPhoneNumberWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:removeOutgoingPhoneNumber");
        String apiEndPoint = connectorProperties.getProperty("apiUrl")+"/"+connectorProperties.getProperty("apiVersion")+"/Accounts/"+connectorProperties.getProperty("accountSid") +"/OutgoingCallerIds/" + connectorProperties.getProperty("outgoingCallerId")+".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "removeOutgoingPhoneNumberMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Negative test case for removeOutgoingPhoneNumber method.
     */
    @Test(priority = 1, description = "twilioRest {removeOutgoingPhoneNumber} integration test negative case.")
    public void testremoveOutgoingPhoneNumberWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:removeOutgoingPhoneNumber");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid") + "/OutgoingCallerIds/" + connectorProperties.getProperty("outgoingCallerIdNeg") + ".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "removeOutgoingPhoneNumberNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for updateOutgoingPhoneNumber method with mandatory parameters.
     */
    @Test(priority = 1, description = "twilioRest {updateOutgoingPhoneNumber} integration test with mandatory parameters.")
    public void testupdateOutgoingPhoneNumberWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updateOutgoingPhoneNumber");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" +
                connectorProperties.getProperty("accountSid") + "/OutgoingCallerIds/" + connectorProperties.getProperty("outgoingCallerId") + ".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "updateOutgoingPhoneNumberMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("sid"), apiRestResponse.getBody().get("sid"));
    }

    /**
     * Positive test case for updateOutgoingPhoneNumber method with optional parameters.
     */
    @Test(priority = 1, description = "twilioRest {updateOutgoingPhoneNumber} integration test with optional parameters.")
    public void testupdateOutgoingPhoneNumberWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updateOutgoingPhoneNumber");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" +
                connectorProperties.getProperty("accountSid") + "/OutgoingCallerIds/" + connectorProperties.getProperty("outgoingCallerId") + ".json?FriendlyName="
                + connectorProperties.getProperty("friendlyName");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "updateOutgoingPhoneNumberOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getBody().get("sid"), apiRestResponse.getBody().get("sid"));
        Assert.assertEquals(apiRestResponse.getBody().get("friendly_name"), apiRestResponse.getBody().get("friendly_name"));
    }

    /**
     * Negative test case for updateOutgoingPhoneNumber method.
     */
    @Test(priority = 1, description = "twilioRest {updateOutgoingPhoneNumber} integration test negative case.")
    public void testupdateOutgoingPhoneNumberWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updateOutgoingPhoneNumber");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" +
                connectorProperties.getProperty("accountSid") + "/OutgoingCallerIds/" + connectorProperties.getProperty("outgoingCallerId") + ".json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "updateOutgoingPhoneNumberNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }

    /**
     * Positive test case for getAvailableLocalNumbers method with mandatory parameters.
     */
    @Test(priority = 1, description = "twilioRest {getAvailableLocalNumbers} integration test with mandatory parameters.")
    public void testgetAvailableLocalNumbersWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getAvailableLocalNumbers");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid") + "/AvailablePhoneNumbers/" + connectorProperties.getProperty("country") + "/Local.json";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAvailableLocalNumbersMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("available_phone_numbers").getJSONObject(0).get("iso_country"),
                apiRestResponse.getBody().getJSONArray("available_phone_numbers").getJSONObject(0).get("iso_country"));
    }

    /**
     * Positive test case for getAvailableLocalNumbers method with optional parameters.
     */
    @Test(priority = 1, description = "twilioRest {getAvailableLocalNumbers} integration test with optional parameters.")
    public void testgetAvailableLocalNumbersWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getAvailableLocalNumbers");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid") +
                "/AvailablePhoneNumbers/" + connectorProperties.getProperty("country") + "/Local.json?AreaCode=" + connectorProperties.getProperty("areaCode");

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAvailableLocalNumbersOptional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("available_phone_numbers").getJSONObject(0).get("iso_country"),
                apiRestResponse.getBody().getJSONArray("available_phone_numbers").getJSONObject(0).get("iso_country"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("available_phone_numbers").getJSONObject(0).get("region"),
                apiRestResponse.getBody().getJSONArray("available_phone_numbers").getJSONObject(0).get("region"));

    }

    /**
     * Negative test case for getAvailableLocalNumbers method.
     */
    @Test(priority = 1, description = "twilioRest {getAvailableLocalNumbers} integration test negative case.")
    public void testgetAvailableLocalNumbersWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getAvailableLocalNumbers");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid") +
                "/AvailablePhoneNumbers/" + connectorProperties.getProperty("countryNeg") + "/Local.json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAvailableLocalNumbersNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());

    }

    /**
     * Positive test case for getAvailableTollFreeNumbers method with mandatory parameters.
     */
    @Test(priority = 1, description = "twilioRest {getAvailableTollFreeNumbers} integration test with mandatory parameters.")
    public void testgetAvailableTollFreeNumbersWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getAvailableTollFreeNumbers");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid") +
                "/AvailablePhoneNumbers/" + connectorProperties.getProperty("country") + "/TollFree.json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAvailableTollFreeNumbersMandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("available_phone_numbers").getJSONObject(0).get("iso_country"),
                apiRestResponse.getBody().getJSONArray("available_phone_numbers").getJSONObject(0).get("iso_country"));
    }

    /**
     * Negative test case for getAvailableTollFreeNumbers method.
     */
    @Test(priority = 1, description = "twilioRest {getAvailableTollFreeNumbers} integration test negative case.")
    public void testgetAvailableTollFreeNumbersWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getAvailableTollFreeNumbers");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties.getProperty("apiVersion") + "/Accounts/" + connectorProperties.getProperty("accountSid") +
                "/AvailablePhoneNumbers/" + connectorProperties.getProperty("countryNeg") + "/TollFree.json";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getAvailableTollFreeNumbersNegative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());

    }
}




