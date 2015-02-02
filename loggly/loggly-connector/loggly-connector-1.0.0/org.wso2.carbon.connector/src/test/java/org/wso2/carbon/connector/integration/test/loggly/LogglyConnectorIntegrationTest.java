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
package org.wso2.carbon.connector.integration.test.loggly;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;


import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;
import org.apache.axiom.om.OMElement;
import org.apache.commons.codec.binary.Base64;

public class LogglyConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> parametersMap = new HashMap<String, String>();
    
    private String binaryUploadProxyUrl;
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("loggly-connector-1.0.0");
        
        String authorizationString = connectorProperties.getProperty("username")+":"+connectorProperties.getProperty("password");
        String authorizationToken = new String(Base64.encodeBase64(authorizationString.getBytes()));
        apiRequestHeadersMap.put("Authorization", "Basic "+authorizationToken);
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        binaryUploadProxyUrl = getProxyServiceURL("loggly_binaryUpload");
    }
    
 
    /**
     * Positive test case for uploadLogFile method with mandatory parameters.
     * @throws JSONException 
     * @throws XMLStreamException 
     */    
    @Test(priority = 1, description = "Loggly {uploadLogFile} integration test with mandatory parameters.")
    public void testUploadLogFileWithMandatoryParameters() throws IOException, JSONException, XMLStreamException{
    
    	binaryUploadProxyUrl += "?apiUrl="+connectorProperties.getProperty("logglyApiUrl") + "&token="+connectorProperties.getProperty("token");
        String apiEndpoint = connectorProperties.getProperty("logglyApiUrl") + "/bulk/"+connectorProperties.getProperty("token") + "/tag/file_upload";
        final MultipartFormdataProcessor esbFileRequestProcessor = new MultipartFormdataProcessor(binaryUploadProxyUrl);
        final File file = new File(pathToResourcesDirectory + connectorProperties.getProperty("txtFileName"));
        esbFileRequestProcessor.addFiletoRequestBody(file);
        final RestResponse<JSONObject> esbRestResponse = esbFileRequestProcessor.processAttachmentForJsonResponse();
        
        final MultipartFormdataProcessor apiFileRequestProcessor = new MultipartFormdataProcessor(apiEndpoint);
        final File apiFile = new File(pathToResourcesDirectory + connectorProperties.getProperty("txtFileName"));
        apiFileRequestProcessor.addFiletoRequestBody(apiFile);
        final RestResponse<JSONObject> apiRestResponse = apiFileRequestProcessor.processAttachmentForJsonResponse();
        
        Assert.assertTrue(esbRestResponse.getHttpStatusCode()==200);
        Assert.assertEquals(esbRestResponse.getBody().get("response").toString() , apiRestResponse.getBody().get("response").toString());
     
    }
    
    
    /**
     * Positive test case for createSearchQuery method with mandatory parameters.
     * @throws JSONException 
     */    
    @Test(priority = 1, description = "Loggly {createSearchQuery} integration test with mandatory parameters.")
    public void testCreateSearchQueryWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:createSearchQuery");
        String apiEndPoint = connectorProperties.getProperty("logglyAccountApiUrl")+"/apiv2/search?q="+connectorProperties.getProperty("query");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createSearchQuery_mandatory.json");
        RestResponse<JSONObject> apiRestResponse =
        		sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(esbRestResponse.getBody().has("rsid") && apiRestResponse.getBody().has("rsid"));
        Assert.assertTrue(esbRestResponse.getBody().getJSONObject("rsid").get("status").toString()!= null && apiRestResponse.getBody().getJSONObject("rsid").get("status").toString() != null);
        Assert.assertTrue(esbRestResponse.getBody().getJSONObject("rsid").get("date_from").toString() != null && apiRestResponse.getBody().getJSONObject("rsid").get("date_from").toString() != null);
    }
    
    /**
     * Positive test case for createSearchQuery method with optional parameters.
     * @throws JSONException 
     */    
    @Test(priority = 1, dependsOnMethods = { "testCreateSearchQueryWithMandatoryParameters" }, description = "Loggly {createSearchQuery} integration test with optional parameters.")
    public void testCreateSearchQueryWithOptionalParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:createSearchQuery");
        String apiEndPoint = connectorProperties.getProperty("logglyAccountApiUrl")+"/apiv2/search?q="+connectorProperties.getProperty("query")+"&size=50";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createSearchQuery_optional.json");
        RestResponse<JSONObject> apiRestResponse =
        		sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        connectorProperties.put("rsid", esbRestResponse.getBody().getJSONObject("rsid").get("id").toString());
        Assert.assertTrue(esbRestResponse.getBody().has("rsid") && apiRestResponse.getBody().has("rsid"));
        Assert.assertTrue(esbRestResponse.getBody().getJSONObject("rsid").get("status").toString()!= null && apiRestResponse.getBody().getJSONObject("rsid").get("status").toString() != null);
        Assert.assertTrue(esbRestResponse.getBody().getJSONObject("rsid").get("date_from").toString() != null && apiRestResponse.getBody().getJSONObject("rsid").get("date_from").toString() != null);
    }
    
    /**
     * Negative test case for createSearchQuery method.
     * @throws JSONException 
     */    
    @Test(priority = 1, dependsOnMethods = { "testCreateSearchQueryWithOptionalParameters" }, description = "Loggly {createSearchQuery} integration test with negative parameters.")
    public void testCreateSearchQueryNegativeCase() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:createSearchQuery");
        String apiEndPoint = connectorProperties.getProperty("logglyAccountApiUrl")+"/apiv2/search?q=" + connectorProperties.getProperty("query")+"&size=xxx";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createSearchQuery_negative.json");
        RestResponse<JSONObject> apiRestResponse =
        		sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(esbRestResponse.getHttpStatusCode() == 400 && apiRestResponse.getHttpStatusCode() == 400);
    }
    
    /**
     * Positive test case for getAccountInfo method with mandatory parameters.
     * @throws JSONException 
     */    
    @Test(priority = 1, dependsOnMethods = { "testCreateSearchQueryNegativeCase" }, description = "Loggly {getAccountInfo} integration test with mandatory parameters.")
    public void testGetAccountInfoWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:getAccountInfo");
        String apiEndPoint = connectorProperties.getProperty("logglyAccountApiUrl")+"/apiv2/customer";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAccountInfo_mandatory.json");
        RestResponse<JSONObject> apiRestResponse =
        		sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("tokens").toString() , apiRestResponse.getBody().get("tokens").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("subdomain").toString() , apiRestResponse.getBody().get("subdomain").toString());
    }
    
    /**
     * Positive test case for getSearchedEvents method with mandatory parameters.
     * @throws JSONException 
     */    
    @Test(priority = 1, dependsOnMethods = { "testGetAccountInfoWithMandatoryParameters" }, description = "Loggly {getSearchedEvents} integration test with mandatory parameters.")
    public void testGetSearchedEventsWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:getSearchedEvents");
        String apiEndPoint = connectorProperties.getProperty("logglyAccountApiUrl")+"/apiv2/events?rsid="+connectorProperties.getProperty("rsid");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSearchedEvents_mandatory.json");
        RestResponse<JSONObject> apiRestResponse =
        		sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("total_events").toString() , apiRestResponse.getBody().get("total_events").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("page").toString() , apiRestResponse.getBody().get("page").toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("events").length() , apiRestResponse.getBody().getJSONArray("events").length());
    }
    
    /**
     * Positive test case for getSearchedEvents method with optional parameters.
     * @throws JSONException 
     */    
    @Test(priority = 1, dependsOnMethods = { "testGetSearchedEventsWithMandatoryParameters" }, description = "Loggly {getSearchedEvents} integration test with optional parameters.")
    public void testGetSearchedEventsWithOptionalParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:getSearchedEvents");
        String apiEndPoint = connectorProperties.getProperty("logglyAccountApiUrl")+"/apiv2/events?page=0&rsid="+connectorProperties.getProperty("rsid");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSearchedEvents_optional.json");
        RestResponse<JSONObject> apiRestResponse =
        		sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("total_events").toString() , apiRestResponse.getBody().get("total_events").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("page").toString() , apiRestResponse.getBody().get("page").toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("events").length() , apiRestResponse.getBody().getJSONArray("events").length());
    } 
    
    /**
     * Negative test case for getSearchedEvents method
     * @throws JSONException 
     */    
    @Test(priority = 1, dependsOnMethods = { "testGetSearchedEventsWithOptionalParameters" }, description = "Loggly {getSearchedEvents} integration test with optional parameters.")
    public void testGetSearchedEventsNegativeCase() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:getSearchedEvents");
        String apiEndPoint = connectorProperties.getProperty("logglyAccountApiUrl") + "/apiv2/events?rsid=---";
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSearchedEvents_negative.json");
        RestResponse<JSONObject> apiRestResponse =
        		sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(esbRestResponse.getHttpStatusCode() == 404 && apiRestResponse.getHttpStatusCode() == 404);
    } 
    
    /**
     * Positive test case for getSearchFields method with mandatory parameters.
     * @throws JSONException 
     */    
    @Test(priority = 1, dependsOnMethods = { "testGetSearchedEventsNegativeCase" }, description = "Loggly {getSearchFields} integration test with mandatory parameters.")
    public void testGetSearchFieldsWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:getSearchFields");
        String apiEndPoint = connectorProperties.getProperty("logglyAccountApiUrl")+"/apiv2/fields?q="+connectorProperties.getProperty("query");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSearchFields_mandatory.json");
        RestResponse<JSONObject> apiRestResponse =
        		sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(esbRestResponse.getBody().has("fields") && apiRestResponse.getBody().has("fields"));
        Assert.assertTrue(esbRestResponse.getBody().has("rsid") && apiRestResponse.getBody().has("rsid"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("fields").length() , apiRestResponse.getBody().getJSONArray("fields").length());
    }
    
    /**
     * Positive test case for getSearchFields method with optional parameters.
     * @throws JSONException 
     */    
    @Test(priority = 1, dependsOnMethods = { "testGetSearchedEventsNegativeCase" }, description = "Loggly {getSearchFields} integration test with optional parameters.")
    public void testGetSearchFieldsWithOptionalParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:getSearchFields");
        String apiEndPoint = connectorProperties.getProperty("logglyAccountApiUrl")+"/apiv2/fields?facet_size=10&q="+connectorProperties.getProperty("query");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSearchFields_optional.json");
        RestResponse<JSONObject> apiRestResponse =
        		sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(esbRestResponse.getBody().has("fields") && apiRestResponse.getBody().has("fields"));
        Assert.assertTrue(esbRestResponse.getBody().has("rsid") && apiRestResponse.getBody().has("rsid"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("fields").length() , apiRestResponse.getBody().getJSONArray("fields").length());
    }
    
    /**
     * Negative test case for getSearchFields method.
     * @throws JSONException 
     */    
    @Test(priority = 1, dependsOnMethods = { "testGetSearchFieldsWithOptionalParameters" }, description = "Loggly {getSearchFields} integration test negative case")
    public void testGetSearchFieldsNegativeCase() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:getSearchFields");
        String apiEndPoint = connectorProperties.getProperty("logglyAccountApiUrl")+"/apiv2/fields?facet_size=abc&q="+connectorProperties.getProperty("query");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSearchFields_negative.json");
        RestResponse<JSONObject> apiRestResponse =
        		sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(esbRestResponse.getHttpStatusCode() == 400 && apiRestResponse.getHttpStatusCode() == 400);
    }
    
    /**
     * Positive test case for sendBulkLogs method with mandatory parameters.
     * @throws JSONException 
     */    
    @Test(priority = 1, dependsOnMethods = { "testGetSearchFieldsNegativeCase" }, description = "Loggly {sendBulkLogs} integration test with mandatory parameters.")
    public void testSendBulkLogsWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:sendBulkLogs");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_sendBulkLogs_mandatory.json");

        Assert.assertTrue(("ok").equals(esbRestResponse.getBody().get("response").toString()));
    }
}
