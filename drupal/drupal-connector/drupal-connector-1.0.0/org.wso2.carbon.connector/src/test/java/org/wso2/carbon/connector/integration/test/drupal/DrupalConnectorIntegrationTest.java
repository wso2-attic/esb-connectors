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
package org.wso2.carbon.connector.integration.test.drupal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;
import org.apache.axiom.om.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DrupalConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private String apiUrl;
    private String page;
    private String pageSize;
    
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        
        init("drupal-connector-1.0.0");
        
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        // Create base64-encoded auth string using username and password
        final String authString = connectorProperties.getProperty("userName") + ":"+connectorProperties.getProperty("password");
        final String base64AuthString = Base64.encode(authString.getBytes());
        
        apiRequestHeadersMap.put("Authorization", "Basic " + base64AuthString);
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        connectorProperties.setProperty("responseType", "json");
        
        apiUrl=connectorProperties.getProperty("apiUrl");
        
        page="2";
        pageSize="1";
        
    }
    
    /**
     * Positive test case for createNode method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "drupal {createNode} integration test with mandatory parameters.")
    public void testCreateNodeWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createNode");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNode_mandatory.json");
        final String nodeIdMand=esbRestResponse.getBody().getString("nid");
        final String nodeUrl=esbRestResponse.getBody().getString("uri")+".json";
        
        connectorProperties.setProperty("nodeIdMand", nodeIdMand);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(nodeUrl, "GET", apiRequestHeadersMap);
        final JSONObject apiResponseObject=apiRestResponse.getBody();
        
        Assert.assertEquals(connectorProperties.getProperty("nodeTitleMand"), apiResponseObject.getString("title"));
        Assert.assertEquals(connectorProperties.getProperty("nodeTypeMand"), apiResponseObject.getString("type"));
    }
    
    /**
     * Positive test case for createNode method with optional parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "drupal {createNode} integration test with optional parameters.")
    public void testCreateNodeWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createNode");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNode_optional.json");
        final String nodeIdOpt=esbRestResponse.getBody().getString("nid");
        final String nodeUrl=esbRestResponse.getBody().getString("uri")+".json";
        
        connectorProperties.setProperty("nodeIdOpt", nodeIdOpt);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(nodeUrl, "GET", apiRequestHeadersMap);
        final JSONObject apiResponseObject=apiRestResponse.getBody();
        final JSONObject apiNodeBodyObject=apiResponseObject.getJSONObject("body").getJSONArray("und").getJSONObject(0);
        final JSONObject apiNodeCustFieldObject=apiResponseObject.getJSONObject(connectorProperties.getProperty("nodeCustFieldLabel")).getJSONArray("und").getJSONObject(0);
        
        Assert.assertEquals(connectorProperties.getProperty("nodeComment"), apiResponseObject.getString("comment"));
        Assert.assertEquals(connectorProperties.getProperty("nodeBodyValue"), apiNodeBodyObject.getString("value"));
        Assert.assertEquals(connectorProperties.getProperty("nodeBodySummary"), apiNodeBodyObject.getString("summary"));
        Assert.assertEquals(connectorProperties.getProperty("nodeBodyFormat"), apiNodeBodyObject.getString("format"));
        Assert.assertEquals(connectorProperties.getProperty("nodeCustFieldValue"), apiNodeCustFieldObject.getString("value"));
    }
    
    /**
     * Negative test case for createNode method.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "drupal {createNode} integration test negative case.")
    public void testCreateNodeWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createNode");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNode_negative.json");
        final JSONObject esbErrorObject=esbRestResponse.getBody().getJSONObject("form_errors");
        
        final String apiEndPoint = apiUrl + "/node.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createNode_negative.json");
        final JSONObject apiErrorObject=apiRestResponse.getBody().getJSONObject("form_errors");
        
        Assert.assertEquals(esbErrorObject.getString("title"), apiErrorObject.getString("title"));        
    }
    
    /**
     * Positive test case for getNode method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateNodeWithMandatoryParameters"}, description = "drupal {getNode} integration test with mandatory parameters.")
    public void testGetNodeWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getNode");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getNode_mandatory.json");
        final JSONObject esbNodeObject=esbRestResponse.getBody();
        
        final String apiEndPoint = apiUrl + "/node/"+connectorProperties.getProperty("nodeIdMand")+".json";
        RestResponse<JSONObject> apiRestResponse =sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONObject apiNodeObject=apiRestResponse.getBody();
        
        Assert.assertEquals(esbNodeObject.getString("title"), apiNodeObject.getString("title"));
        Assert.assertEquals(esbNodeObject.getString("status"), apiNodeObject.getString("status"));
        Assert.assertEquals(esbNodeObject.getString("type"), apiNodeObject.getString("type"));
        Assert.assertEquals(esbNodeObject.getString("created"), apiNodeObject.getString("created"));
        Assert.assertEquals(esbNodeObject.getString("language"), apiNodeObject.getString("language"));
    }
    
    /**
     * Test case: testGetNodeWithOptionalParameters. 
     * Status: Skipped.
     * Reason : There is no any optional case to assert.
     */
    
    /**
     * Negative test case for getNode method.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "drupal {getNode} integration test negative case.")
    public void testGetNodeWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getNode");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getNode_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        
        final String esbResponseString=esbRestResponse.getBody().getString("output");
        final JSONArray esbErrorResponseArray=new JSONArray(esbResponseString);

        final String apiEndPoint = apiUrl + "/node/INVALID.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final String apiResponseString=apiRestResponse.getBody().getString("output");
        final JSONArray apiErrorResponseArray=new JSONArray(apiResponseString);
        
        Assert.assertEquals(esbErrorResponseArray.getString(0), apiErrorResponseArray.getString(0));        
    }
    
    /**
     * Positive test case for listNodes method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateNodeWithMandatoryParameters", "testCreateNodeWithOptionalParameters"}, description = "drupal {listNodes} integration test with mandatory parameters.")
    public void testListNodesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listNodes");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listNodes_mandatory.json");
        final String esbResponseString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(esbResponseString);
        
        final String apiEndPoint = apiUrl + "/node.json";
        RestResponse<JSONObject> apiRestResponse =sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final String apiResponseString=apiRestResponse.getBody().getString("output");
        final JSONArray apiResponseArray=new JSONArray(apiResponseString);
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("nid"), apiResponseArray.getJSONObject(0).getString("nid"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("title"), apiResponseArray.getJSONObject(0).getString("title"));
        Assert.assertEquals(esbResponseArray.getJSONObject(esbResponseArray.length()-1).getString("nid"), apiResponseArray.getJSONObject(esbResponseArray.length()-1).getString("nid"));
        Assert.assertEquals(esbResponseArray.getJSONObject(esbResponseArray.length()-1).getString("title"), apiResponseArray.getJSONObject(apiResponseArray.length()-1).getString("title"));
    }
    
    /**
     * Positive test case for listNodes method with optional parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateNodeWithMandatoryParameters", "testCreateNodeWithOptionalParameters"}, description = "drupal {listNodes} integration test with optional parameters.")
    public void testListNodesWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listNodes");

        final String fields="nid,type";
        connectorProperties.setProperty("nodesPage", page);
        connectorProperties.setProperty("nodesPageSize", pageSize);
        connectorProperties.setProperty("nodesFields", fields);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listNodes_optional.json");
        final String esbResponseString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(esbResponseString);
        
        Assert.assertEquals(esbResponseArray.length(), Integer.parseInt(pageSize));
        
        final String apiEndPoint = apiUrl + "/node.json?page="+page+"&fields="+fields+"&pagesize="+pageSize;
        RestResponse<JSONObject> apiRestResponse =sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final String apiResponseString=apiRestResponse.getBody().getString("output");
        final JSONArray apiResponseArray=new JSONArray(apiResponseString);
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).has("nid"), apiResponseArray.getJSONObject(0).has("nid"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).has("type"), apiResponseArray.getJSONObject(0).has("type"));
    }
    
    /**
     * Test case: testListNodesWithNegativeCase. 
     * Status: Skipped.
     * Reason : There is no any negative case to assert.
     */
    
    /**
     * Test case: testUpdateNodeWithMandatoryParameters. 
     * Status: Skipped.
     * Reason : There is no any mandatory case to assert.
     */
    
    /**
     * Positive test case for updateNode method with optional parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateNodeWithOptionalParameters"}, description = "drupal {updateNode} integration test with optional parameters.")
    public void testUpdateNodeWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateNode");
        
        final String apiEndPoint = apiUrl + "/node/"+connectorProperties.getProperty("nodeIdOpt")+".json";
        RestResponse<JSONObject> apiRestResponseBeforeUpdate =sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateNode_optional.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        
        RestResponse<JSONObject> apiRestResponseAfterUpdate =sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponseBeforeUpdate.getBody().getString("title"), apiRestResponseAfterUpdate.getBody().getString("title"));
        Assert.assertNotEquals(apiRestResponseBeforeUpdate.getBody().getString("comment"), apiRestResponseAfterUpdate.getBody().getString("comment"));
        Assert.assertNotEquals(apiRestResponseBeforeUpdate.getBody().getJSONObject("body").getJSONArray("und").getJSONObject(0).getString("value"),
        		apiRestResponseAfterUpdate.getBody().getJSONObject("body").getJSONArray("und").getJSONObject(0).getString("value"));
        Assert.assertNotEquals(apiRestResponseBeforeUpdate.getBody().getJSONObject(connectorProperties.getProperty("nodeCustFieldLabel")).getJSONArray("und").getJSONObject(0).getString("value"), 
        		apiRestResponseAfterUpdate.getBody().getJSONObject(connectorProperties.getProperty("nodeCustFieldLabel")).getJSONArray("und").getJSONObject(0).getString("value"));
        Assert.assertEquals(apiRestResponseAfterUpdate.getBody().getString("title"), connectorProperties.getProperty("nodeTitleUpdate"));
        Assert.assertEquals(apiRestResponseAfterUpdate.getBody().getString("comment"), connectorProperties.getProperty("nodeCommentUpdate"));
        Assert.assertEquals(apiRestResponseAfterUpdate.getBody().getJSONObject("body").getJSONArray("und").getJSONObject(0).getString("value"), 
        		connectorProperties.getProperty("nodeBodyValueUpdate"));
        Assert.assertEquals(apiRestResponseAfterUpdate.getBody().getJSONObject(connectorProperties.getProperty("nodeCustFieldLabel")).getJSONArray("und").getJSONObject(0).getString("value"), 
        		connectorProperties.getProperty("nodeCustFieldValueUpdate"));              
    }
    
    /**
     * Test case: testUpdateNodeWithNegativeCase. 
     * Status: Skipped.
     * Reason : There is no any negative case to assert.
     */
    
    /**
     * Positive test case for listNodeAttachments method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "drupal {listNodeAttachments} integration test with mandatory parameters.")
    public void testListNodeAttachmentsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listNodeAttachments");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listNodeAttachments_mandatory.json");
        final String esbResponseString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(esbResponseString);
        
        Assert.assertNotEquals(esbResponseArray.length(), 0, "Pre-requisite Failed. No Attachmment(s) to given node.");
        
        final String apiEndPoint = apiUrl + "/node/"+connectorProperties.getProperty("nodeIdWithAttachment")+"/files.json";
        RestResponse<JSONObject> apiRestResponse =sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final String apiResponseString=apiRestResponse.getBody().getString("output");
        final JSONArray apiResponseArray=new JSONArray(apiResponseString);
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("fid"), apiResponseArray.getJSONObject(0).getString("fid"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("filename"), apiResponseArray.getJSONObject(0).getString("filename"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("filemime"), apiResponseArray.getJSONObject(0).getString("filemime"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("status"), apiResponseArray.getJSONObject(0).getString("status"));
    }
    
    /**
     * Test case: testListNodeAttachmentsWithMandatoryParameters. 
     * Status: Skipped.
     * Reason : There is no any optional case to assert.
     */
    
    /**
     * Negative test case for listNodeAttachments method.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "drupal {listNodeAttachments} integration test negative case.")
    public void testListNodeAttachmentsWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listNodeAttachments");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listNodeAttachments_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        
        final String esbResponseString=esbRestResponse.getBody().getString("output");
        final JSONArray esbErrorResponseArray=new JSONArray(esbResponseString);

        final String apiEndPoint = apiUrl + "/node/INVALID/files.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final String apiResponseString=apiRestResponse.getBody().getString("output");
        final JSONArray apiErrorResponseArray=new JSONArray(apiResponseString);
        
        Assert.assertEquals(esbErrorResponseArray.getString(0), apiErrorResponseArray.getString(0));        
    }
    
    /**
     * Positive test case for createComment method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateNodeWithOptionalParameters"}, description = "drupal {createComment} integration test with mandatory parameters.")
    public void testCreateCommentWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createComment");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_mandatory.json");
        final String commentIdMand=esbRestResponse.getBody().getString("cid");
        final String commentUrl=esbRestResponse.getBody().getString("uri")+".json";
        
        connectorProperties.setProperty("commentIdMand", commentIdMand);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(commentUrl, "GET", apiRequestHeadersMap);
        final JSONObject apiResponseObject=apiRestResponse.getBody();
        
        Assert.assertEquals(connectorProperties.getProperty("nodeIdOpt"), apiResponseObject.getString("nid"));
        Assert.assertEquals(connectorProperties.getProperty("commentBodyValueMand"), apiResponseObject.getJSONObject("comment_body").getJSONArray("und").getJSONObject(0).getString("value"));
    }
    
    /**
     * Positive test case for createComment method with optional parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateNodeWithOptionalParameters"}, description = "drupal {createComment} integration test with optional parameters.")
    public void testCreateCommentWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createComment");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_optional.json");
        final String commentIdOpt=esbRestResponse.getBody().getString("cid");
        final String commentUrl=esbRestResponse.getBody().getString("uri")+".json";
        
        connectorProperties.setProperty("commentIdOpt", commentIdOpt);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(commentUrl, "GET", apiRequestHeadersMap);
        final JSONObject apiResponseObject=apiRestResponse.getBody();
        
        Assert.assertEquals(connectorProperties.getProperty("commentSubject"), apiResponseObject.getString("subject"));
        Assert.assertEquals(connectorProperties.getProperty("commentCustFieldValue"), 
        		apiResponseObject.getJSONObject(connectorProperties.getProperty("commentCustFieldLabel")).getJSONArray("und").getJSONObject(0).getString("value"));
    }
    
    /**
     * Negative test case for createComment method.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "drupal {createComment} integration test negative case.")
    public void testCreateCommentWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createComment");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_negative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 406);
        final JSONArray esbResponseArray=new JSONArray(esbRestResponse.getBody().getString("output"));
        final String esbErrorMessage=esbResponseArray.getString(0);
        
        final String apiEndPoint = apiUrl + "/comment.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createComment_negative.json");
        final JSONArray apiResponseArray=new JSONArray(apiRestResponse.getBody().getString("output"));
        final String apiErrorMessage=apiResponseArray.getString(0);
        
        Assert.assertEquals(apiErrorMessage, esbErrorMessage);        
    }
    
    /**
     * Positive test case for getComment method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateCommentWithMandatoryParameters"}, description = "drupal {getComment} integration test with mandatory parameters.")
    public void testGetCommentWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getComment");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getComment_mandatory.json");
        final JSONObject esbCommentObject=esbRestResponse.getBody();
        
        final String apiEndPoint = apiUrl + "/comment/"+connectorProperties.getProperty("commentIdMand")+".json";
        RestResponse<JSONObject> apiRestResponse =sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONObject apiCommentObject=apiRestResponse.getBody();
        
        Assert.assertEquals(esbCommentObject.getString("uid"), apiCommentObject.getString("uid"));
        Assert.assertEquals(esbCommentObject.getString("subject"), apiCommentObject.getString("subject"));
        Assert.assertEquals(esbCommentObject.getString("created"), apiCommentObject.getString("created"));
        Assert.assertEquals(esbCommentObject.getString("status"), apiCommentObject.getString("status"));
        Assert.assertEquals(esbCommentObject.getString("node_type"), apiCommentObject.getString("node_type"));
    }
    
    /**
     * Test case: testGetCommentWithOptionalParameters. 
     * Status: Skipped.
     * Reason : There is no any optional case to assert.
     */
    
    /**
     * Negative test case for getComment method.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "drupal {getComment} integration test negative case.")
    public void testGetCommentWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getComment");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getComment_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 500);

        final String apiEndPoint = apiUrl + "/comment/INVALID.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());        
    }
    
    /**
     * Positive test case for listComments method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateCommentWithMandatoryParameters", "testCreateCommentWithOptionalParameters"}, description = "drupal {listComments} integration test with mandatory parameters.")
    public void testListCommentsWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listComments");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listComments_mandatory.json");
        final String esbResponseString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(esbResponseString);
        
        final String apiEndPoint = apiUrl + "/comment.json";
        RestResponse<JSONObject> apiRestResponse =sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final String apiResponseString=apiRestResponse.getBody().getString("output");
        final JSONArray apiResponseArray=new JSONArray(apiResponseString);
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("cid"), apiResponseArray.getJSONObject(0).getString("cid"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("subject"), apiResponseArray.getJSONObject(0).getString("subject"));
        Assert.assertEquals(esbResponseArray.getJSONObject(esbResponseArray.length()-1).getString("cid"), apiResponseArray.getJSONObject(esbResponseArray.length()-1).getString("cid"));
        Assert.assertEquals(esbResponseArray.getJSONObject(esbResponseArray.length()-1).getString("subject"), apiResponseArray.getJSONObject(apiResponseArray.length()-1).getString("subject"));
    }
    
    /**
     * Positive test case for listComments method with optional parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateCommentWithMandatoryParameters", "testCreateCommentWithOptionalParameters"}, description = "drupal {listComments} integration test with optional parameters.")
    public void testListCommentsWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listComments");

        final String fields="cid,subject";
        connectorProperties.setProperty("commentsPage", page);
        connectorProperties.setProperty("commentsPageSize", pageSize);
        connectorProperties.setProperty("commentsFields", fields);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listComments_optional.json");
        final String esbResponseString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(esbResponseString);
        
        Assert.assertEquals(esbResponseArray.length(), Integer.parseInt(pageSize));
        
        final String apiEndPoint = apiUrl + "/comment.json?page="+page+"&fields="+fields+"&pagesize="+pageSize;
        RestResponse<JSONObject> apiRestResponse =sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final String apiResponseString=apiRestResponse.getBody().getString("output");
        final JSONArray apiResponseArray=new JSONArray(apiResponseString);
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).has("cid"), apiResponseArray.getJSONObject(0).has("cid"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).has("subject"), apiResponseArray.getJSONObject(0).has("subject"));
   
       
    }
    
    /**
     * Test case: testListCommentsWithNegativeCase. 
     * Status: Skipped.
     * Reason : There is no any negative case to assert.
     */
    
    /**
     * Positive test case for createFile method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "drupal {createFile} integration test with mandatory parameters.")
    public void testCreateFileWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createFile");
        
        String base64EncodedFile="";
        final String actualFileName=connectorProperties.getProperty("fileMand");
        final File file =new File(pathToResourcesDirectory,actualFileName);
        
        //covert file into base64 encoded string
        base64EncodedFile=encodeFileToBase64String(file);
       
        connectorProperties.setProperty("fileContentMand", base64EncodedFile);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createFile_mandatory.json");
        final String fileIdMand=esbRestResponse.getBody().getString("fid");
        final String fileUrl=esbRestResponse.getBody().getString("uri")+".json";
        
        connectorProperties.setProperty("fileIdMand", fileIdMand);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(fileUrl, "GET", apiRequestHeadersMap);
        final JSONObject apiResponseObject=apiRestResponse.getBody();
        
        Assert.assertEquals(connectorProperties.getProperty("fileContentMand"), apiResponseObject.getString("file"));
        Assert.assertEquals(connectorProperties.getProperty("fileNameMand"), apiResponseObject.getString("filename"));
    }
    
    /**
     * Positive test case for createFile method with optional parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "drupal {createFile} integration test with optional parameters.")
    public void testCreateFileWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createFile");
        
        String base64EncodedFile="";
        final String actualFileName=connectorProperties.getProperty("fileOpt");
        final File file =new File(pathToResourcesDirectory,actualFileName);
        
        //covert file into base64 encoded string
        base64EncodedFile=encodeFileToBase64String(file);
       
        connectorProperties.setProperty("fileContentOpt", base64EncodedFile);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createFile_optional.json");
        final String fileIdOpt=esbRestResponse.getBody().getString("fid");
        final String fileUrl=esbRestResponse.getBody().getString("uri")+".json";
        
        connectorProperties.setProperty("fileIdOpt", fileIdOpt);
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(fileUrl, "GET", apiRequestHeadersMap);
        final JSONObject apiResponseObject=apiRestResponse.getBody();
        
        Assert.assertEquals(connectorProperties.getProperty("fileStatus"), apiResponseObject.getString("status"));
    }
    
    /**
     * Negative test case for createFile method.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "drupal {createFile} integration test negative case.")
    public void testCreateFileWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createFile");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createFile_negative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 500);
        final String esbResponseString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(esbResponseString);
        
        final String apiEndPoint = apiUrl + "/file.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createFile_negative.json");
        final String apiResponseString=apiRestResponse.getBody().getString("output");
        final JSONArray apiResponseArray=new JSONArray(apiResponseString);
        
        Assert.assertEquals(esbResponseArray.getString(0), apiResponseArray.getString(0));        
    }
    
    /**
     * Positive test case for getFile method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateFileWithMandatoryParameters"}, description = "drupal {getFile} integration test with mandatory parameters.")
    public void testGetFileWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getFile");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFile_mandatory.json");
        final JSONObject esbFileObject=esbRestResponse.getBody();
        
        final String apiEndPoint = apiUrl + "/file/"+connectorProperties.getProperty("fileIdMand")+".json";
        RestResponse<JSONObject> apiRestResponse =sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONObject apiFileObject=apiRestResponse.getBody();
        
        Assert.assertEquals(esbFileObject.getString("filename"), apiFileObject.getString("filename"));
        Assert.assertEquals(esbFileObject.getString("uri"), apiFileObject.getString("uri"));
        Assert.assertEquals(esbFileObject.getString("filemime"), apiFileObject.getString("filemime"));
        Assert.assertEquals(esbFileObject.getString("filesize"), apiFileObject.getString("filesize"));
        Assert.assertEquals(esbFileObject.getString("status"), apiFileObject.getString("status"));
    }
    
    /**
     * Test case: testGetFileWithOptionalParameters. 
     * Status: Skipped.
     * Reason : There is no any optional case to assert.
     */
    
    /**
     * Negative test case for getFile method.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "drupal {getFile} integration test negative case.")
    public void testGetFileWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getFile");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFile_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 406);
        
        final String esbResponseString=esbRestResponse.getBody().getString("output");
        final JSONArray esbErrorResponseArray=new JSONArray(esbResponseString);

        final String apiEndPoint = apiUrl + "/file/invalid.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final String apiResponseString=apiRestResponse.getBody().getString("output");
        final JSONArray apiErrorResponseArray=new JSONArray(apiResponseString);
        
        Assert.assertEquals(esbErrorResponseArray.getString(0), apiErrorResponseArray.getString(0));        
    }
    
    /**
     * Positive test case for listFiles method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateFileWithMandatoryParameters", "testCreateFileWithOptionalParameters"}, description = "drupal {listFiles} integration test with mandatory parameters.")
    public void testListFilesWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listFiles");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listFiles_mandatory.json");
        final String esbResponseString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(esbResponseString);
        
        final String apiEndPoint = apiUrl + "/file.json";
        RestResponse<JSONObject> apiRestResponse =sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final String apiResponseString=apiRestResponse.getBody().getString("output");
        final JSONArray apiResponseArray=new JSONArray(apiResponseString);
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("fid"), apiResponseArray.getJSONObject(0).getString("fid"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("filename"), apiResponseArray.getJSONObject(0).getString("filename"));
        Assert.assertEquals(esbResponseArray.getJSONObject(esbResponseArray.length()-1).getString("fid"), apiResponseArray.getJSONObject(esbResponseArray.length()-1).getString("fid"));
        Assert.assertEquals(esbResponseArray.getJSONObject(esbResponseArray.length()-1).getString("filename"), apiResponseArray.getJSONObject(apiResponseArray.length()-1).getString("filename"));
    }
    
    /**
     * Positive test case for listFiles method with optional parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateFileWithMandatoryParameters", "testCreateFileWithOptionalParameters"}, description = "drupal {listFiles} integration test with optional parameters.")
    public void testListFilesWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listFiles");

        final String fields="fid,filename";
        connectorProperties.setProperty("filesPage", page);
        connectorProperties.setProperty("filesPageSize", pageSize);
        connectorProperties.setProperty("filesFields", fields);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listFiles_optional.json");
        final String esbResponseString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(esbResponseString);
        
        Assert.assertEquals(esbResponseArray.length(), Integer.parseInt(pageSize));
        
        final String apiEndPoint = apiUrl + "/file.json?page="+page+"&fields="+fields+"&pagesize="+pageSize;
        RestResponse<JSONObject> apiRestResponse =sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final String apiResponseString=apiRestResponse.getBody().getString("output");
        final JSONArray apiResponseArray=new JSONArray(apiResponseString);
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).has("fid"), apiResponseArray.getJSONObject(0).has("fid"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).has("filename"), apiResponseArray.getJSONObject(0).has("filename"));
    }
    
    /**
     * Test case: testListFilesWithNegativeCase. 
     * Status: Skipped.
     * Reason : There is no any negative case to assert.
     */
    
    /**
     * Positive test case for listUsers method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "drupal {listUsers} integration test with mandatory parameters.")
    public void testListUsersWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listUsers");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_mandatory.json");
        final String esbResponseString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(esbResponseString);
        final String userId=esbResponseArray.getJSONObject(0).getString("uid");
        connectorProperties.setProperty("userIdMand", userId);
        
        final String apiEndPoint = apiUrl + "/user.json";
        RestResponse<JSONObject> apiRestResponse =sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final String apiResponseString=apiRestResponse.getBody().getString("output");
        final JSONArray apiResponseArray=new JSONArray(apiResponseString);
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("uid"), apiResponseArray.getJSONObject(0).getString("uid"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).getString("name"), apiResponseArray.getJSONObject(0).getString("name"));
        Assert.assertEquals(esbResponseArray.getJSONObject(esbResponseArray.length()-1).getString("uid"), apiResponseArray.getJSONObject(esbResponseArray.length()-1).getString("uid"));
        Assert.assertEquals(esbResponseArray.getJSONObject(esbResponseArray.length()-1).getString("name"), apiResponseArray.getJSONObject(apiResponseArray.length()-1).getString("name"));
    }
    
    /**
     * Positive test case for listUsers method with optional parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" },description = "drupal {listUsers} integration test with optional parameters.")
    public void testListUsersWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listUsers");
        
        final String page="1";
        final String pageSize="1";
        final String fields="uid,name";
        connectorProperties.setProperty("usersPage", page);
        connectorProperties.setProperty("usersPageSize", pageSize);
        connectorProperties.setProperty("usersFields", fields);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_optional.json");
        final String esbResponseString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(esbResponseString);
        
        Assert.assertEquals(esbResponseArray.length(), Integer.parseInt(pageSize));
        
        final String apiEndPoint = apiUrl + "/user.json?page="+page+"&fields="+fields+"&pagesize="+pageSize;
        RestResponse<JSONObject> apiRestResponse =sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final String apiResponseString=apiRestResponse.getBody().getString("output");
        final JSONArray apiResponseArray=new JSONArray(apiResponseString);
        
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).has("uid"), apiResponseArray.getJSONObject(0).has("uid"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).has("name"), apiResponseArray.getJSONObject(0).has("name"));
    }
    
    /**
     * Test case: testListUsersWithNegativeCase. 
     * Status: Skipped.
     * Reason : There is no any negative case to assert.
     */
    
    /**
     * Positive test case for getUser method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testListUsersWithMandatoryParameters"}, description = "drupal {getUser} integration test with mandatory parameters.")
    public void testGetUserWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getUser");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUser_mandatory.json");
        final JSONObject esbFileObject=esbRestResponse.getBody();
        
        final String apiEndPoint = apiUrl + "/user/"+connectorProperties.getProperty("userIdMand")+".json";
        RestResponse<JSONObject> apiRestResponse =sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONObject apiFileObject=apiRestResponse.getBody();
        
        Assert.assertEquals(esbFileObject.getString("name"), apiFileObject.getString("name"));
        Assert.assertEquals(esbFileObject.getString("mail"), apiFileObject.getString("mail"));
        Assert.assertEquals(esbFileObject.getString("created"), apiFileObject.getString("created"));
        Assert.assertEquals(esbFileObject.getString("status"), apiFileObject.getString("status"));
        Assert.assertEquals(esbFileObject.getString("signature_format"), apiFileObject.getString("signature_format"));
    }
    
    /**
     * Test case: testGetUserWithOptionalParameters. 
     * Status: Skipped.
     * Reason : There is no any optional case to assert.
     */
    
    /**
     * Negative test case for getUser method.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "drupal {getUser} integration test negative case.")
    public void testGetUserWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getUser");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUser_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 406);
        
        final String esbResponseString=esbRestResponse.getBody().getString("output");
        final JSONArray esbErrorResponseArray=new JSONArray(esbResponseString);

        final String apiEndPoint = apiUrl + "/user/invalid.json";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final String apiResponseString=apiRestResponse.getBody().getString("output");
        final JSONArray apiErrorResponseArray=new JSONArray(apiResponseString);
        
        Assert.assertEquals(esbErrorResponseArray.getString(0), apiErrorResponseArray.getString(0));        
    }

    
    /**
     * Returns Base64 encoded File String
     *
     * @param  file  source file to encode to base64 encoded String
     * @throws IOException 
     */
    public static String encodeFileToBase64String(final File file) throws IOException{
    	
    	String encodedFileString=""; 
    	
    	FileInputStream fileInputStream=null;
    	byte[] encodedBytes = new byte[(int) file.length()];
        
    	//convert file into array of bytes
	    fileInputStream = new FileInputStream(file);
	    fileInputStream.read(encodedBytes);
	    fileInputStream.close();
    	
	    //convert byte array into base64 encoded String
    	encodedFileString= Base64.encode(encodedBytes);
    	return encodedFileString;
    }
    
}
