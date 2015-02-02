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

package org.wso2.carbon.connector.integration.test.zohorecruit;

import java.io.IOException;
import java.net.URLEncoder;
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

public class ZohorecruitConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("zohorecruit-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");
        
    }
    
    /**
     * Positive test case for addRecords method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "zohorecruit {addRecords} integration test with mandatory parameters")
    public void testAddRecordsWithMandatoryParameters() throws IOException, JSONException {
    
    	esbRequestHeadersMap.put("Action", "urn:addRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addRecords_mandatory.json");
        
        String recordId =
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                        .getJSONObject("recorddetail").getJSONObject("FL").get("content").toString();
        connectorProperties.put("recordId", recordId);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/ats/private/json/JobOpenings/getRecordById?authtoken="
                        + connectorProperties.getProperty("authToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&id="
                        + connectorProperties.getProperty("recordId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        String apiRecordId = apiRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("JobOpenings").getJSONObject("row")
        		.getJSONArray("FL").getJSONObject(0).getString("content");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(recordId,apiRecordId);
    }
    
    /**
     * Positive test case for addRecords method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddRecordsWithMandatoryParameters" }, description = "zohorecruit {addRecords} integration test with optional parameters.")
    public void testAddRecordsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:addRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addRecords_optional.json");
        
        String recordId =
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                        .getJSONObject("recorddetail").getJSONObject("FL").get("content").toString();
        connectorProperties.put("recordId", recordId);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/ats/private/json/JobOpenings/getRecordById?authtoken="
                        + connectorProperties.getProperty("authToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&id="
                        + connectorProperties.getProperty("recordId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        String apiRecordId = apiRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("JobOpenings").getJSONObject("row")
        		.getJSONArray("FL").getJSONObject(0).getString("content");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(recordId,apiRecordId);
    }
    
    /**
     * Negative test case for addRecords method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAddRecordsWithOptionalParameters" }, description = "zohorecruit {addRecords} integration test with negative Case.")
    public void testAddRecordsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:addRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addRecords_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/ats/private/json/JobOpenings/addRecords?authtoken="
                        + connectorProperties.getProperty("authToken");
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_addRecords_negative.json");
        String esbErrorCode = esbRestResponse.getBody().getJSONObject("response").getJSONObject("error").getString("code");
        String apiErrorCode = apiRestResponse.getBody().getJSONObject("response").getJSONObject("error").getString("code");
        String esbMessage =  esbRestResponse.getBody().getJSONObject("response").getJSONObject("error").getString("message");
        String apiMessage =  esbRestResponse.getBody().getJSONObject("response").getJSONObject("error").getString("message");
        
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbErrorCode,apiErrorCode);
        Assert.assertEquals(esbMessage,apiMessage);
    }
    
    /**
     * Positive test case for getRecords method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "zohorecruit {getRecords} integration test with mandatory parameters")
    public void testGetRecordsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecords_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/ats/private/json/JobOpenings/getRecords?authtoken="
                        + connectorProperties.getProperty("authToken") + "&scope="
                        + connectorProperties.getProperty("scope");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
       
        int esbArrayLength = esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                .getJSONObject("JobOpenings").getJSONArray("row").length();
        int apiArrayLenght = apiRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                .getJSONObject("JobOpenings").getJSONArray("row").length();
        String esbContent =   esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                .getJSONObject("JobOpenings").getJSONArray("row").getJSONObject(0).getJSONArray("FL")
                .getJSONObject(0).getString("content");
        String apiContent = apiRestResponse.getBody().getJSONObject("response")
                .getJSONObject("result").getJSONObject("JobOpenings").getJSONArray("row").getJSONObject(0)
                .getJSONArray("FL").getJSONObject(0).getString("content");
        String esbVal =  esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                .getJSONObject("JobOpenings").getJSONArray("row").getJSONObject(0).getJSONArray("FL")
                .getJSONObject(1).getString("val");
        String apiVal = apiRestResponse.getBody().getJSONObject("response")
                .getJSONObject("result").getJSONObject("JobOpenings").getJSONArray("row").getJSONObject(0)
                .getJSONArray("FL").getJSONObject(1).getString("val");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbArrayLength,apiArrayLenght);
        Assert.assertEquals(esbContent, apiContent);
        Assert.assertEquals(esbVal,apiVal );
        
    }
    
    /**
     * Positive test case for getRecords method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetRecordsWithMandatoryParameters" }, description = "zohorecruit {getRecords} integration test with optional parameters")
    public void testGetRecordsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecords_optional.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/ats/private/json/JobOpenings/getRecords?authtoken="
                        + connectorProperties.getProperty("authToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&fromIndex="
                        + connectorProperties.getProperty("getRecordsFromIndex") + "&toIndex="
                        + connectorProperties.getProperty("getRecordsToIndex") + "&sortColumnString="
                        + connectorProperties.getProperty("getRecordsSortColumnString");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        int esbArrayLength = esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                .getJSONObject("JobOpenings").getJSONArray("row").length();
        int apiArrayLenght = apiRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                .getJSONObject("JobOpenings").getJSONArray("row").length();
        String esbContent =   esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                .getJSONObject("JobOpenings").getJSONArray("row").getJSONObject(0).getJSONArray("FL")
                .getJSONObject(0).getString("content");
        String apiContent = apiRestResponse.getBody().getJSONObject("response")
                .getJSONObject("result").getJSONObject("JobOpenings").getJSONArray("row").getJSONObject(0)
                .getJSONArray("FL").getJSONObject(0).getString("content");
        String esbVal =  esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                .getJSONObject("JobOpenings").getJSONArray("row").getJSONObject(0).getJSONArray("FL")
                .getJSONObject(1).getString("val");
        String apiVal = apiRestResponse.getBody().getJSONObject("response")
                .getJSONObject("result").getJSONObject("JobOpenings").getJSONArray("row").getJSONObject(0)
                .getJSONArray("FL").getJSONObject(1).getString("val");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbArrayLength,apiArrayLenght);
        Assert.assertEquals(esbContent, apiContent);
        Assert.assertEquals(esbVal,apiVal );
    }
    
    /**
     * Negative test case for getRecords method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetRecordsWithOptionalParameters" }, description = "zohorecruit {getRecords} integration test with negative Case.")
    public void testGetRecordsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecords_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/ats/private/json/xxx/getRecords?authtoken="
                        + connectorProperties.getProperty("authToken") + "&scope="
                        + connectorProperties.getProperty("scope");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(esbRestResponse.getBody().getString("output"), apiRestResponse.getBody()
                .getString("output"));
    }
    
    /**
     * Positive test case for getRecordById method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetRecordsWithNegativeCase" }, description = "zohorecruit {getRecordById} integration test with mandatory parameters")
    public void testGetRecordByIdWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getRecordById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecordById_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/ats/private/json/JobOpenings/getRecordById?authtoken="
                        + connectorProperties.getProperty("authToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&id="
                        + connectorProperties.getProperty("getRecordId");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String esbNo = esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                .getJSONObject("JobOpenings").getJSONObject("row").getString("no");
        String apiNo = 
                apiRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                .getJSONObject("JobOpenings").getJSONObject("row").getString("no") ;
        String esbConent =  esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                .getJSONObject("JobOpenings").getJSONObject("row").getJSONArray("FL").getJSONObject(0)
                .getString("content");
        String apiContent= apiRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                .getJSONObject("JobOpenings").getJSONObject("row").getJSONArray("FL").getJSONObject(0)
                .getString("content");
        String esbValue =esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                .getJSONObject("JobOpenings").getJSONObject("row").getJSONArray("FL").getJSONObject(0)
                .getString("val");
        String apiValue = apiRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                .getJSONObject("JobOpenings").getJSONObject("row").getJSONArray("FL").getJSONObject(0)
                .getString("val");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbNo,apiNo);
        Assert.assertEquals(esbConent,apiContent);
        Assert.assertEquals(esbValue,apiValue);
    }
    
    /**
     * Positive test case for getRecordById method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetRecordByIdWithMandatoryParameters" }, description = "zohorecruit {getRecordById} integration test with optional parameters")
    public void testGetRecordByIdWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getRecordById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecordById_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/ats/private/json/JobOpenings/getRecordById?authtoken="
                        + connectorProperties.getProperty("authToken") + "&id="
                        + connectorProperties.getProperty("getRecordId") + "&scope="
                        + connectorProperties.getProperty("scope") + "&selectColumns="
                        + connectorProperties.getProperty("getRecordByIdSelectColumns");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String esbNo = esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                .getJSONObject("JobOpenings").getJSONObject("row").getString("no");
        String apiNo = apiRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                .getJSONObject("JobOpenings").getJSONObject("row").getString("no");
        String esbContent =esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                .getJSONObject("JobOpenings").getJSONObject("row").getJSONArray("FL").getJSONObject(2)
                .getString("content");
        String apiContent = apiRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                .getJSONObject("JobOpenings").getJSONObject("row").getJSONArray("FL").getJSONObject(2)
                .getString("content");
        String esbVal =  esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                .getJSONObject("JobOpenings").getJSONObject("row").getJSONArray("FL").getJSONObject(2)
                .getString("val");
        String apiVal = apiRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                .getJSONObject("JobOpenings").getJSONObject("row").getJSONArray("FL").getJSONObject(2)
                .getString("val");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbNo,apiNo);
        Assert.assertEquals(esbContent,apiContent);
        Assert.assertEquals(esbVal ,apiVal);
    }
    
    /**
     * Negative test case for getRecordById method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetRecordByIdWithOptionalParameters" }, description = "zohorecruit {getRecordById} integration test with negative Case.")
    public void testGetRecordByIdWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getRecordById");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRecordById_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/ats/private/json/JobOpenings/getRecordById?authtoken="
                        + connectorProperties.getProperty("authToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&id=xxx";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(esbRestResponse.getBody().getString("output"), apiRestResponse.getBody()
                .getString("output"));
    }
    
    /**
     * Positive test case for updateRecords method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetRecordByIdWithNegativeCase" }, description = "zohorecruit {updateRecords} integration test with mandatory parameters")
    public void testUpdateRecordsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateRecords_mandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/ats/private/json/JobOpenings/getRecordById?authtoken="
                        + connectorProperties.getProperty("authToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&id="
                        + connectorProperties.getProperty("jobId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray jsonAry =
                apiRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                        .getJSONObject("JobOpenings").getJSONObject("row").getJSONArray("FL");
        
        String postingTitle = "";
        //loop the array to find the required json object
        for (int i = 0; i < jsonAry.length(); i++) {            
            JSONObject flJson = new JSONObject(jsonAry.get(i).toString());            
            if ("Posting title".equals(flJson.get("val"))) {
                postingTitle = flJson.getString("content");
                break;
            }
        }
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(connectorProperties.getProperty("updatePostingTitle"), postingTitle);
    }
    
    /**
     * Negative test case for updateRecords method.
     */
    
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateRecordsWithMandatoryParameters" }, description = "zohorecruit {updateRecords} integration test with negative case")
    public void testUpdateRecordsWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateRecords");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateRecords_negative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/ats/private/json/JobOpenings/updateRecords?"
                        + "authtoken=" + connectorProperties.getProperty("authToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&id="
                        + connectorProperties.getProperty("recordId") + "&xmlData="
                        + URLEncoder.encode("<Invalid></Invalid>", "UTF-8");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        JSONObject apiJsonObj = apiRestResponse.getBody().getJSONObject("response").getJSONObject("error");
        JSONObject esbJsonObj = esbRestResponse.getBody().getJSONObject("response").getJSONObject("error");
        Assert.assertEquals(esbJsonObj.get("message").toString(), apiJsonObj.get("message").toString());
        Assert.assertEquals(esbJsonObj.get("code").toString(), apiJsonObj.get("code").toString());
    }
    
    /**
     * Positive test case for associateJobOpening method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateRecordsWithNegativeCase" }, description = "zohorecruit {associateJobOpening} integration test with mandatory parameters")
    public void testAssociateJobOpeningWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:associateJobOpening");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_associateJobOpening_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl")
                        + "/ats/private/json/JobOpenings/getAssociatedCandidates?authtoken="
                        + connectorProperties.getProperty("authToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&jobId="
                        + connectorProperties.getProperty("jobRecordIdAssociate");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        String candidateId = "";
        JSONObject resultRow=apiRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("JobOpenings");
        //Checking whether row is a JSON object or an array
        if (resultRow.get("row") instanceof JSONObject) {
            candidateId = resultRow.getJSONObject("row").getJSONArray("FL").getJSONObject(0).get("content").toString();
        } else {
            JSONArray row =	resultRow.getJSONArray("row");
            for (int i = 0; i < row.length(); i++) {
                if (row.getJSONObject(i).getJSONArray("FL").getJSONObject(0).get("content").toString()
                        .equals(connectorProperties.getProperty("candidateId"))) {
                    candidateId = row.getJSONObject(i).getJSONArray("FL").getJSONObject(0).get("content").toString();
                    break;
                }
                
            }
        }
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(candidateId, connectorProperties.getProperty("candidateRecordIdOptional"));
    }
    
    /**
     * Negative test case for associateJobOpening method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAssociateJobOpeningWithMandatoryParameters" }, description = "zohorecruit {associateJobOpening} integration test with negative Case.")
    public void testAssociateJobOpeningWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:associateJobOpening");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_associateJobOpening_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl")
                        + "/ats/private/json/JobOpenings/getAssociatedCandidates?authtoken="
                        + connectorProperties.getProperty("authToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&jobId=-";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(esbRestResponse.getBody().getString("output"), apiRestResponse.getBody()
                .getString("output"));
    }
    
    /**
     * Positive test case for getAssociatedCandidates method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testAssociateJobOpeningWithMandatoryParameters" }, description = "zohorecruit {getAssociatedCandidates} integration test with mandatory parameters.")
    public void testGetAssociatedCandidatesWithMadatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAssociatedCandidates");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_getAssociatedCandidates_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/ats/private/json/JobOpenings/getAssociatedCandidates?"
                        + "authtoken=" + connectorProperties.getProperty("authToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&jobId="
                        + connectorProperties.getProperty("jobRecordIdAssociate");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(
                esbRestResponse.getBody().getJSONObject("response").getJSONObject("result")
                        .getJSONObject("JobOpenings").toString(), apiRestResponse.getBody().getJSONObject("response")
                        .getJSONObject("result").getJSONObject("JobOpenings").toString());
        
    }
    
    /**
     * Negative test case for getAssociatedCandidates method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetAssociatedCandidatesWithMadatoryParameters" }, description = "zohorecruit {getAssociatedCandidates} integration test with negative case.")
    public void testGetAssociatedCandidatesNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getAssociatedCandidates");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAssociatedCandidates_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/ats/private/json/JobOpenings/getAssociatedCandidates?"
                        + "authtoken=" + connectorProperties.getProperty("authToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&jobId=-1";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertTrue(esbRestResponse.getBody().getJSONObject("response").has("nodata"));
        Assert.assertTrue(apiRestResponse.getBody().getJSONObject("response").has("nodata"));
        
    }
    
    /**
     * Positive test case for changeStatus method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetAssociatedCandidatesNegativeCase" }, description = "zohorecruit {changeStatus} integration test with mandatory parameters")
    public void testChangeStatusWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:changeStatus");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_changeStatus_mandatory.json");
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/ats/private/json/Candidates/getRecordById?authtoken="
                        + connectorProperties.getProperty("authToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&id="
                        + connectorProperties.getProperty("candidateRecordId");
        
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        final JSONArray resultArray =
                apiRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Candidates")
                        .getJSONObject("row").getJSONArray("FL");
        
        String changedStatus = "";
        for (int i = 0; i < resultArray.length(); i++) {
            JSONObject elemObject = (JSONObject) resultArray.get(i);
            if ("Resume status".equals(elemObject.getString("val"))) {
                changedStatus = elemObject.getString("content");
                break;
            }
        }
        
        Assert.assertEquals(connectorProperties.getProperty("changeStatus"), changedStatus);
    }
    
    /**
     * Positive test case for changeStatus method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testChangeStatusWithMandatoryParameters" }, description = "zohorecruit {changeStatus} integration test with optional parameters.")
    public void testChangeStatusWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:changeStatus");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_changeStatus_optional.json");
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/ats/private/json/Candidates/getRecordById?authtoken="
                        + connectorProperties.getProperty("authToken") + "&scope="
                        + connectorProperties.getProperty("scope") + "&id="
                        + connectorProperties.getProperty("candidateRecordIdOptional");
        
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        final JSONArray resultArray =
                apiRestResponse.getBody().getJSONObject("response").getJSONObject("result").getJSONObject("Candidates")
                        .getJSONObject("row").getJSONArray("FL");
        
        String changedStatus = "Invalid";
        for (int i = 0; i < resultArray.length(); i++) {
            JSONObject elemObject = (JSONObject) resultArray.get(i);
            if ("Resume status".equals(elemObject.getString("val"))) {
                changedStatus = elemObject.getString("content");
                break;
            }
        }
        
        Assert.assertEquals(connectorProperties.getProperty("changeStatus"), changedStatus);
    }
    
    /**
     * Negative test case for changeStatus method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testChangeStatusWithOptionalParameters" }, description = "zohorecruit {changeStatus} integration test with negative case.")
    public void testChangeStatusWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:changeStatus");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_changeStatus_negative.json");
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/ats/private/json/Candidates/changeStatus?authtoken="
                        + connectorProperties.getProperty("authToken");
        final RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_changeStatus_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 500);
        Assert.assertEquals(esbRestResponse.getBody().getString("output"), apiRestResponse.getBody()
                .getString("output"));
    }
    
}
