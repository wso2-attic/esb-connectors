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
package org.wso2.carbon.connector.integration.test.wunderlist;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

public class WunderListConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap;
    private Map<String, String> apiRequestHeadersMap;
    
    private String apiUrl;
    
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("wunderlist-connector-1.0.0");
        
        esbRequestHeadersMap = new HashMap<String, String>();
        apiRequestHeadersMap = new HashMap<String, String>();
        
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Accept", "application/json");
        
        apiRequestHeadersMap.put("X-Access-Token", connectorProperties.getProperty("accessToken"));
        apiRequestHeadersMap.put("X-Client-ID", connectorProperties.getProperty("clientId"));
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        apiUrl=connectorProperties.getProperty("apiUrl")+"/api/v1";
        
    }
    
    /**
     * Positive test case for createList method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "wunderlist {createList} integration test with mandatory parameters.")
    public void testCreateListWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:createList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createList_mandatory.json");
        
        final String listId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("listId", listId);
        final String listRevision = esbRestResponse.getBody().getString("revision");
        connectorProperties.setProperty("listRevision", listRevision);
        
        final String apiEndPoint = apiUrl +"/lists/"+ listId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("title"), connectorProperties.get("listTitleMand"));
    }
    
    /**
     * Method Name: createList
     * Skipped Case: optional case
     * Reason: No optional parameter(s) to assert. 
     */

    /**
     * Negative test case for createList method .
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "wunderlist {createList} integration test with negative case.")
    public void testCreateListWithNegativeCase() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:createList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createList_negative.json");
        
        
        final JSONObject esbResponseErrorObject=esbRestResponse.getBody().getJSONObject("error");
        
        final String apiEndPoint = apiUrl + "/lists";
        RestResponse<JSONObject> apiRestResponse =
        sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createList_negative.json");
        
        final JSONObject apiResponseErrorObject=apiRestResponse.getBody().getJSONObject("error");
        
        Assert.assertEquals(esbResponseErrorObject.get("type"),apiResponseErrorObject.get("type"));
        Assert.assertEquals(esbResponseErrorObject.get("translation_key"),apiResponseErrorObject.get("translation_key"));
        Assert.assertEquals(esbResponseErrorObject.get("message"),apiResponseErrorObject.get("message"));
        Assert.assertEquals(esbResponseErrorObject.getJSONArray("title").get(0),apiResponseErrorObject.getJSONArray("title").get(0));
    }
    
    /**
     * Positive test case for getList method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateListWithMandatoryParameters"}, description = "wunderlist {getList} integration test with mandatory parameters.")
    public void testGetListWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:getList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getList_mandatory.json");

        final String apiEndPoint = apiUrl +"/lists/"+ connectorProperties.getProperty("listId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("title"), apiRestResponse.getBody().getString("title"));
        Assert.assertEquals(esbRestResponse.getBody().getString("owner_type"), apiRestResponse.getBody().getString("owner_type"));
        Assert.assertEquals(esbRestResponse.getBody().getString("owner_id"), apiRestResponse.getBody().getString("owner_id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("list_type"), apiRestResponse.getBody().getString("list_type"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString("created_at")); 
    }
    
    /**
     * Method Name: getList
     * Skipped Case: optional case
     * Reason: No optional parameter(s) to assert. 
     */
    
    /**
     * Negative test case for getList method .
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "wunderlist {getList} integration test with negative case.")
    public void testGetListWithNegativeCase() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:getList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getList_negative.json");
        
        final String apiEndPoint = apiUrl +"/lists/INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listLists method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateListWithMandatoryParameters"}, description = "wunderlist {listLists} integration test with mandatory parameters.")
    public void testListListsWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:listLists");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listLists_mandatory.json");
        
        String responseArrayString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(responseArrayString);

        final String apiEndPoint = apiUrl +"/lists";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        responseArrayString=apiRestResponse.getBody().getString("output");
        
        final JSONArray apiResponseArray=new JSONArray(responseArrayString);
   
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("id"), apiResponseArray.getJSONObject(0).get("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("title"), apiResponseArray.getJSONObject(0).get("title"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("owner_type"), apiResponseArray.getJSONObject(0).get("owner_type"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("revision"), apiResponseArray.getJSONObject(0).get("revision"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("created_at"), apiResponseArray.getJSONObject(0).get("created_at"));
    }
    
    /**
     * Method Name: listLists
     * Skipped Case: optional case
     * Reason: No optional parameter(s) to assert. 
     */
    
    /**
     * Method Name: listLists
     * Skipped Case: negative case
     * Reason: No parameter(s) to test negative case. 
     */
    
    /**
     * Method Name: updateList
     * Skipped Case: mandatory case
     * Reason: No mandatory parameter(s) to assert. 
     */
    
    /**
     * Positive test case for updateList method with optional parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" },dependsOnMethods={"testCreateListWithMandatoryParameters"}, description = "wunderlist {updateList} integration test with optional parameters.")
    public void testUpdateListWithOptionalParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:updateList");
        
        final String apiEndPoint = apiUrl +"/lists/"+ connectorProperties.getProperty("listId");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateList_optional.json");

        final RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("title"), apiRestResponseAfter.getBody().getString("title"));
        Assert.assertEquals(apiRestResponseAfter.getBody().getString("title"), connectorProperties.get("listTitleUpdate"));
    }
    
    /**
     * Negative test case for updateList method .
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testUpdateListWithOptionalParameters"}, description = "wunderlist {updateList} integration test with negative case.")
    public void testUpdateListWithNegativeCase() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:updateList");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateList_negative.json");
        final JSONObject esbResponseErrorObject=esbRestResponse.getBody().getJSONObject("error");
        
        final String apiEndPoint = apiUrl + "/lists/"+connectorProperties.getProperty("listId");
        RestResponse<JSONObject> apiRestResponse =
        sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateList_negative.json");
        final JSONObject apiResponseErrorObject=apiRestResponse.getBody().getJSONObject("error");
        
        Assert.assertEquals(esbResponseErrorObject.get("type"),apiResponseErrorObject.get("type"));
        Assert.assertEquals(esbResponseErrorObject.get("translation_key"),apiResponseErrorObject.get("translation_key"));
        Assert.assertEquals(esbResponseErrorObject.get("message"),apiResponseErrorObject.get("message"));
        Assert.assertEquals(esbResponseErrorObject.get("revision_conflict"),apiResponseErrorObject.get("revision_conflict"));
    }
    
    /**
     * Positive test case for createFolder method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testUpdateListWithOptionalParameters"}, description = "wunderlist {createFolder} integration test with mandatory parameters.")
    public void testCreateFolderWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:createFolder");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createFolder_mandatory.json");
        
        final String folderId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("folderId", folderId);
        final String folderRevision = esbRestResponse.getBody().getString("revision");
        connectorProperties.setProperty("folderRevision", folderRevision);
        final String userId=esbRestResponse.getBody().getString("user_id");
        connectorProperties.setProperty("userId", userId);
        
        final String apiEndPoint = apiUrl +"/folders/"+ folderId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("title"), connectorProperties.get("folderNameMand"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("list_ids").get(0).toString(), connectorProperties.get("listId"));
 
    }
    
    /**
     * Method Name: createFolder
     * Skipped Case: optional case
     * Reason: No optional parameter(s) to assert. 
     */
    
    /**
     * Negative test case for createFolder method .
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "wunderlist {createFolder} integration test with negative case.")
    public void testCreateFolderWithNegativeCase() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:createFolder");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createFolder_negative.json");
        
        
        final JSONObject esbResponseErrorObject=esbRestResponse.getBody().getJSONObject("error");
        
        final String apiEndPoint = apiUrl + "/folders";
        RestResponse<JSONObject> apiRestResponse =
        sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createFolder_negative.json");
        
        final JSONObject apiResponseErrorObject=apiRestResponse.getBody().getJSONObject("error");
        
        Assert.assertEquals(esbResponseErrorObject.get("type"),apiResponseErrorObject.get("type"));
        Assert.assertEquals(esbResponseErrorObject.get("translation_key"),apiResponseErrorObject.get("translation_key"));
        Assert.assertEquals(esbResponseErrorObject.get("message"),apiResponseErrorObject.get("message"));
        Assert.assertEquals(esbResponseErrorObject.getJSONArray("title").get(0),apiResponseErrorObject.getJSONArray("title").get(0));
    }
    
    /**
     * Positive test case for getFolder method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateFolderWithMandatoryParameters"}, description = "wunderlist {getFolder} integration test with mandatory parameters.")
    public void testGetFolderWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:getFolder");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFolder_mandatory.json");

        final String apiEndPoint = apiUrl +"/folders/"+ connectorProperties.getProperty("folderId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("title"), apiRestResponse.getBody().getString("title"));
        Assert.assertEquals(esbRestResponse.getBody().getString("user_id"), apiRestResponse.getBody().getString("user_id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("revision"), apiRestResponse.getBody().getString("revision"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_by_id"), apiRestResponse.getBody().getString("created_by_id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString("created_at")); 
    }
    
    /**
     * Method Name: getFolder
     * Skipped Case: optional case
     * Reason: No optional parameter(s) to assert. 
     */
    
    /**
     * Negative test case for getFolder method .
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "wunderlist {getFolder} integration test with negative case.")
    public void testGetFolderWithNegativeCase() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:getFolder");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFolder_negative.json");
        
        final String apiEndPoint = apiUrl +"/folders/INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listFolders method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateFolderWithMandatoryParameters"}, description = "wunderlist {listFolders} integration test with mandatory parameters.")
    public void testListFoldersWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:listFolders");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listFolders_mandatory.json");
        
        String responseArrayString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(responseArrayString);

        final String apiEndPoint = apiUrl +"/folders";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        responseArrayString=apiRestResponse.getBody().getString("output");
        
        final JSONArray apiResponseArray=new JSONArray(responseArrayString);
   
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("id"), apiResponseArray.getJSONObject(0).get("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("title"), apiResponseArray.getJSONObject(0).get("title"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("user_id"), apiResponseArray.getJSONObject(0).get("user_id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("type"), apiResponseArray.getJSONObject(0).get("type"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("created_at"), apiResponseArray.getJSONObject(0).get("created_at"));
    }
    
    /**
     * Method Name: listFolders
     * Skipped Case: optional case
     * Reason: No optional parameter(s) to assert. 
     */
    
    /**
     * Method Name: listFolders
     * Skipped Case: negative case
     * Reason: No parameter(s) to test negative case. 
     */
    
    /**
     * Method Name: updateFolder
     * Skipped Case: mandatory case
     * Reason: No mandatory parameter(s) to assert. 
     */
    
    /**
     * Positive test case for updateFolder method with optional parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateFolderWithMandatoryParameters"}, description = "wunderlist {updateFolder} integration test with optional parameters.")
    public void testUpdateFolderWithOptionalParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:updateFolder");
        
        //Creating a new list to update the folder list IDs
        String apiEndPoint = apiUrl + "/lists";
        RestResponse<JSONObject> apiCreateListRestResponse =
        sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateFolder_optional.json");
        
        if(apiCreateListRestResponse.getHttpStatusCode()!=201){
        	Assert.assertFalse(true, "Pre-requisites failure. Creating a new list to add a new folder failed.");
        }
        
        final String newListId=apiCreateListRestResponse.getBody().getString("id");
        connectorProperties.setProperty("newListId", newListId);
        
        apiEndPoint = apiUrl +"/folders/"+ connectorProperties.getProperty("folderId");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateFolder_optional.json");

        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("title"), apiRestResponseAfter.getBody().getString("title"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getJSONArray("list_ids").get(0), apiRestResponseAfter.getBody().getJSONArray("list_ids").get(0));
        Assert.assertEquals(apiRestResponseAfter.getBody().getString("title"), connectorProperties.get("folderNameUpdate"));
        Assert.assertEquals(apiRestResponseAfter.getBody().getJSONArray("list_ids").get(0).toString(), connectorProperties.get("newListId"));
    }
    
    /**
     * Negative test case for updateFolder method .
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testUpdateFolderWithOptionalParameters"}, description = "wunderlist {updateFolder} integration test with negative case.")
    public void testUpdateFolderWithNegativeCase() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:updateFolder");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateFolder_negative.json");
        final JSONObject esbResponseErrorObject=esbRestResponse.getBody().getJSONObject("error");
        
        final String apiEndPoint = apiUrl + "/folders/"+connectorProperties.getProperty("folderId");
        RestResponse<JSONObject> apiRestResponse =
        sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateFolder_negative.json");
        final JSONObject apiResponseErrorObject=apiRestResponse.getBody().getJSONObject("error");
        
        Assert.assertEquals(esbResponseErrorObject.get("type"),apiResponseErrorObject.get("type"));
        Assert.assertEquals(esbResponseErrorObject.get("translation_key"),apiResponseErrorObject.get("translation_key"));
        Assert.assertEquals(esbResponseErrorObject.get("message"),apiResponseErrorObject.get("message"));
        Assert.assertEquals(esbResponseErrorObject.get("revision_conflict"),apiResponseErrorObject.get("revision_conflict"));
    }
    
    /**
     * Positive test case for addMember method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testUpdateFolderWithOptionalParameters"}, description = "wunderlist {addMember} integration test with mandatory parameters.")
    public void testAddMemberWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:addMember");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addMember_mandatory.json");
        
        final String memberId = esbRestResponse.getBody().getString("id");
        
        final String apiEndPoint = apiUrl +"/memberships";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final String responseArrayString=apiRestResponse.getBody().getString("output");
        final JSONArray apiResponseArray=new JSONArray(responseArrayString);
        
        boolean hasMember=false;
        for(int i=0; i<apiResponseArray.length(); i++){
        	JSONObject member=apiResponseArray.getJSONObject(i);
        	String id=member.get("id").toString();
        	if(id.equals(memberId)){
        		Assert.assertEquals(member.get("user_id").toString(), connectorProperties.get("userId"));
        		hasMember=true;
        		break;
        	}
        }
        Assert.assertTrue(hasMember,"Member not found.");
    }
    
    /**
     * Positive test case for addMember method with optional parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testUpdateFolderWithOptionalParameters"}, description = "wunderlist {addMember} integration test with optional parameters.")
    public void testAddMemberWithOptionalParameters() throws IOException, JSONException{
    	
    	final String muted="false";
    	connectorProperties.setProperty("muted", muted);
    	
    	//Creating a new list to add member
    	String apiEndPoint = apiUrl + "/lists";
        RestResponse<JSONObject> apiCreateListRestResponse =
        sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_addMember_optional.json");
        
        if(apiCreateListRestResponse.getHttpStatusCode()!=201){
        	Assert.assertFalse(true, "Pre-requisites failure. Creating new list to add a member failed.");
        }
        
        final String newListId=apiCreateListRestResponse.getBody().getString("id");
        connectorProperties.setProperty("listIdTwo", newListId);
    
        esbRequestHeadersMap.put("Action", "urn:addMember");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addMember_optional.json");
        
        final String memberId = esbRestResponse.getBody().getString("id");
        
        apiEndPoint = apiUrl +"/memberships";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        final String responseArrayString=apiRestResponse.getBody().getString("output");
        
        final JSONArray apiResponseArray=new JSONArray(responseArrayString);
        
        boolean hasMember=false;
        for(int i=0; i<apiResponseArray.length(); i++){
        	JSONObject member=apiResponseArray.getJSONObject(i);
        	String id=member.get("id").toString();
        	if(id.equals(memberId)){
        		hasMember=true;
        		break;
        	}
        }
        Assert.assertTrue(hasMember,"Member not found.");
    }
    
    /**
     * Negative test case for addMember method .
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testAddMemberWithOptionalParameters"}, description = "wunderlist {addMember} integration test with negative case.")
    public void testAddMemberWithNegativeCase() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:addMember");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addMember_negative.json");
        final JSONObject esbResponseErrorObject=esbRestResponse.getBody().getJSONObject("error");
        
        final String apiEndPoint = apiUrl + "/memberships";
        RestResponse<JSONObject> apiRestResponse =
        sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_addMember_negative.json");
        final JSONObject apiResponseErrorObject=apiRestResponse.getBody().getJSONObject("error");
        
        Assert.assertEquals(esbResponseErrorObject.get("type"),apiResponseErrorObject.get("type"));
        Assert.assertEquals(esbResponseErrorObject.get("translation_key"),apiResponseErrorObject.get("translation_key"));
        Assert.assertEquals(esbResponseErrorObject.get("message"),apiResponseErrorObject.get("message"));
        Assert.assertEquals(esbResponseErrorObject.getJSONArray("user_id or email").get(0).toString(),apiResponseErrorObject.getJSONArray("user_id or email").get(0).toString());
    }
    
    /**
     * Positive test case for listMembers method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testAddMemberWithMandatoryParameters", "testAddMemberWithOptionalParameters"}, description = "wunderlist {listMembers} integration test with mandatory parameters.")
    public void testListMembersWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:listMembers");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listMembers_mandatory.json");
        
        String responseArrayString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(responseArrayString);

        final String apiEndPoint = apiUrl +"/memberships";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        responseArrayString=apiRestResponse.getBody().getString("output");
        
        final JSONArray apiResponseArray=new JSONArray(responseArrayString);
   
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("id"), apiResponseArray.getJSONObject(0).get("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("type"), apiResponseArray.getJSONObject(0).get("type"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("owner"), apiResponseArray.getJSONObject(0).get("owner"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("revision"), apiResponseArray.getJSONObject(0).get("revision"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("state"), apiResponseArray.getJSONObject(0).get("state"));
    }
    
    /**
     * Method Name: listMembers
     * Skipped Case: optional case
     * Reason: No optional parameter(s) to assert. 
     */
    
    /**
     * Method Name: listMembers
     * Skipped Case: negative case
     * Reason: No parameter(s) to negative case. 
     */
    
    /**
     * Positive test case for createTask method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testAddMemberWithOptionalParameters"}, description = "wunderlist {createTask} integration test with mandatory parameters.")
    public void testCreateTaskWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:createTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_mandatory.json");
        
        final String taskId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("taskId", taskId);
        final String taskRevision = esbRestResponse.getBody().getString("revision");
        connectorProperties.setProperty("taskRevision", taskRevision);
        
        final String apiEndPoint = apiUrl +"/tasks/"+ taskId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("list_id"), connectorProperties.get("listId"));
        Assert.assertEquals(apiRestResponse.getBody().getString("title"), connectorProperties.get("taskTitleMand"));
    }
    
    /**
     * Positive test case for createTask method with optional parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testAddMemberWithOptionalParameters"}, description = "wunderlist {createTask} integration test with optional parameters.")
    public void testCreateTaskWithOptionalParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:createTask");
        
        connectorProperties.setProperty("taskStarred", "true");
        connectorProperties.setProperty("taskCompleted", "false");
        connectorProperties.setProperty("taskRecurrenceType", "week");
        connectorProperties.setProperty("taskRecurrenceCount", "2");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_optional.json");
        final String taskId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("taskIdOpt", taskId);
        final String taskRevision = esbRestResponse.getBody().getString("revision");
        connectorProperties.setProperty("taskRevisionOpt", taskRevision);
        
        final String apiEndPoint = apiUrl +"/tasks/"+ taskId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("due_date"), connectorProperties.get("taskDueDate"));
        Assert.assertEquals(apiRestResponse.getBody().getString("starred"), connectorProperties.get("taskStarred"));
        Assert.assertEquals(apiRestResponse.getBody().getString("completed"), connectorProperties.get("taskCompleted"));
        Assert.assertEquals(apiRestResponse.getBody().getString("recurrence_type"), connectorProperties.get("taskRecurrenceType"));
        Assert.assertEquals(apiRestResponse.getBody().getString("recurrence_count"), connectorProperties.get("taskRecurrenceCount")); 
    }
    
    /**
     * Negative test case for createTask method .
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testAddMemberWithOptionalParameters"}, description = "wunderlist {createTask} integration test with negative case.")
    public void testCreateTaskWithNegativeCase() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:createTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTask_negative.json");
        final JSONObject esbResponseErrorObject=esbRestResponse.getBody().getJSONObject("error");
        
        final String apiEndPoint = apiUrl + "/tasks";
        RestResponse<JSONObject> apiRestResponse =
        sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createTask_negative.json");
        
        final JSONObject apiResponseErrorObject=apiRestResponse.getBody().getJSONObject("error");
        
        Assert.assertEquals(esbResponseErrorObject.get("type"),apiResponseErrorObject.get("type"));
        Assert.assertEquals(esbResponseErrorObject.get("translation_key"),apiResponseErrorObject.get("translation_key"));
        Assert.assertEquals(esbResponseErrorObject.get("message"),apiResponseErrorObject.get("message"));
        Assert.assertEquals(esbResponseErrorObject.getJSONArray("title").get(0),apiResponseErrorObject.getJSONArray("title").get(0));
    }
    
    /**
     * Positive test case for getTask method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateTaskWithMandatoryParameters"}, description = "wunderlist {getTask} integration test with mandatory parameters.")
    public void testGetTaskWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:getTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTask_mandatory.json");
        

        final String apiEndPoint = apiUrl +"/tasks/"+ connectorProperties.getProperty("taskId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("title"), apiRestResponse.getBody().getString("title"));
        Assert.assertEquals(esbRestResponse.getBody().getString("revision"), apiRestResponse.getBody().getString("revision"));
        Assert.assertEquals(esbRestResponse.getBody().getString("type"), apiRestResponse.getBody().getString("type"));
        Assert.assertEquals(esbRestResponse.getBody().getString("starred"), apiRestResponse.getBody().getString("starred"));
        Assert.assertEquals(esbRestResponse.getBody().getString("created_at"), apiRestResponse.getBody().getString("created_at")); 
    }
    
    /**
     * Method Name: getTask
     * Skipped Case: optional case
     * Reason: No optional parameter(s) to assert. 
     */
    
    /**
     * Negative test case for getTask method .
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "wunderlist {getTask} integration test with negative case.")
    public void testGetTaskWithNegativeCase() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:getTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTask_negative.json");
        
        final String apiEndPoint = apiUrl +"/tasks/INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listTasks method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateTaskWithMandatoryParameters"}, description = "wunderlist {listTasks} integration test with mandatory parameters.")
    public void testListTasksWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:listTasks");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_mandatory.json");
        
        String responseArrayString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(responseArrayString);

        final String apiEndPoint = apiUrl +"/tasks?list_id="+connectorProperties.getProperty("listId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        responseArrayString=apiRestResponse.getBody().getString("output");
        
        final JSONArray apiResponseArray=new JSONArray(responseArrayString);
   
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("id"), apiResponseArray.getJSONObject(0).get("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("title"), apiResponseArray.getJSONObject(0).get("title"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("revision"), apiResponseArray.getJSONObject(0).get("revision"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("type"), apiResponseArray.getJSONObject(0).get("type"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("created_at"), apiResponseArray.getJSONObject(0).get("created_at"));
    }
    
    /**
     * Positive test case for listTasks method with optional parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateTaskWithMandatoryParameters"}, description = "wunderlist {listTasks} integration test with optional parameters.")
    public void testListTasksWithOptionalParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:listTasks");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_optional.json");
        
        String responseArrayString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(responseArrayString);

        final String apiEndPoint = apiUrl +"/tasks?list_id="+connectorProperties.getProperty("listId")+"&completed="+connectorProperties.getProperty("taskCompleted");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        responseArrayString=apiRestResponse.getBody().getString("output");
        
        final JSONArray apiResponseArray=new JSONArray(responseArrayString);
   
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("completed"), apiResponseArray.getJSONObject(0).get("completed"));
        Assert.assertEquals(esbResponseArray.getJSONObject(esbResponseArray.length()-1).get("completed"), apiResponseArray.getJSONObject(esbResponseArray.length()-1).get("completed"));
    }
    
    /**
     * Method Name: listTasks
     * Skipped Case: negative case
     * Reason: No parameter(s) to test negative case. 
     */
    
    /**
     * Negative test case for listTasks method .
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "wunderlist {listTasks} integration test with negative case.")
    public void testListTasksWithNegativeCase() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:listTasks");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTasks_negative.json");
        final JSONObject esbResponseErrorObject=esbRestResponse.getBody().getJSONObject("error");
        
        final String apiEndPoint = apiUrl +"/tasks?list_id=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponseErrorObject=apiRestResponse.getBody().getJSONObject("error");
        
        Assert.assertEquals(esbResponseErrorObject.get("type"),apiResponseErrorObject.get("type"));
        Assert.assertEquals(esbResponseErrorObject.get("translation_key"),apiResponseErrorObject.get("translation_key"));
        Assert.assertEquals(esbResponseErrorObject.get("message"),apiResponseErrorObject.get("message"));
    }
    
    /**
     * Method Name: updateTask
     * Skipped Case: mandatory case
     * Reason: No mandatory parameter(s) to assert. 
     */
   
    /**
     * Positive test case for updateTask method with optional parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" },dependsOnMethods={"testCreateTaskWithOptionalParameters"}, description = "wunderlist {updateTask} integration test with optional parameters.")
    public void testUpdateTaskWithOptionalParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:updateTask");
        
        connectorProperties.setProperty("taskStarredUpdate", "false");
        connectorProperties.setProperty("taskCompletedUpdate", "true");
        connectorProperties.setProperty("taskRecurrenceTypeUpdate", "day");
        connectorProperties.setProperty("taskRecurrenceCountUpdate", "4");
        
        final String apiEndPoint = apiUrl +"/tasks/"+ connectorProperties.getProperty("taskIdOpt");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTask_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("title"), apiRestResponseAfter.getBody().getString("title"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("starred"), apiRestResponseAfter.getBody().getString("starred"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("completed"), apiRestResponseAfter.getBody().getString("completed"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("recurrence_type"), apiRestResponseAfter.getBody().getString("recurrence_type"));
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("recurrence_count"), apiRestResponseAfter.getBody().getString("recurrence_count"));
        Assert.assertEquals(apiRestResponseAfter.getBody().getString("title"), connectorProperties.get("taskTitleUpdate"));
        Assert.assertEquals(apiRestResponseAfter.getBody().getString("starred"), connectorProperties.get("taskStarredUpdate"));
        Assert.assertEquals(apiRestResponseAfter.getBody().getString("completed"), connectorProperties.get("taskCompletedUpdate"));
        Assert.assertEquals(apiRestResponseAfter.getBody().getString("recurrence_type"), connectorProperties.get("taskRecurrenceTypeUpdate"));
        Assert.assertEquals(apiRestResponseAfter.getBody().getString("recurrence_count"), connectorProperties.get("taskRecurrenceCountUpdate"));
    }
    
    /**
     * Negative test case for updateTask method .
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testUpdateTaskWithOptionalParameters"}, description = "wunderlist {updateTask} integration test with negative case.")
    public void testUpdateTaskWithNegativeCase() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:updateTask");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTask_negative.json");
        final JSONObject esbResponseErrorObject=esbRestResponse.getBody().getJSONObject("error");
        
        final String apiEndPoint = apiUrl + "/tasks/"+connectorProperties.getProperty("taskIdOpt");
        RestResponse<JSONObject> apiRestResponse =
        sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateTask_negative.json");
        final JSONObject apiResponseErrorObject=apiRestResponse.getBody().getJSONObject("error");

        Assert.assertEquals(esbResponseErrorObject.get("type"),apiResponseErrorObject.get("type"));
        Assert.assertEquals(esbResponseErrorObject.get("translation_key"),apiResponseErrorObject.get("translation_key"));
        Assert.assertEquals(esbResponseErrorObject.get("message"),apiResponseErrorObject.get("message"));
        Assert.assertEquals(esbResponseErrorObject.get("revision_conflict").toString(),apiResponseErrorObject.get("revision_conflict").toString());
    }
    
    /**
     * Positive test case for deleteTask method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testGetTaskWithMandatoryParameters","testListTasksWithOptionalParameters"}, description = "wunderlist {deleteTask} integration test with mandatory parameters.")
    public void testDeleteTaskWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:deleteTask");
        
        final String apiEndPoint = apiUrl +"/tasks/"+ connectorProperties.getProperty("taskId");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteTask_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        
        Assert.assertEquals(apiRestResponseBefore.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponseAfter.getHttpStatusCode(), 404);
    }

    /**
     * Method Name: deleteTask
     * Skipped Case: optional case
     * Reason: No optional parameter(s) to assert. 
     */
    
    /**
     * Method Name: deleteTask
     * Skipped Case: negative case
     * Reason: No parameter(s) to test negative case. 
     */
    
    /**
     * Positive test case for createNote method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testUpdateTaskWithOptionalParameters"}, description = "wunderlist {createNote} integration test with mandatory parameters.")
    public void testCreateNoteWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:createNote");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNote_mandatory.json");
                
        final String noteId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("noteId", noteId);
        final String noteRevision = esbRestResponse.getBody().getString("revision");
        connectorProperties.setProperty("noteRevision", noteRevision);
        
        final String apiEndPoint = apiUrl +"/notes/"+ noteId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getBody().getString("content"), connectorProperties.get("contentMand"));
        Assert.assertEquals(apiRestResponse.getBody().getString("task_id"), connectorProperties.get("taskIdOpt"));
    }
    
    /**
     * Method Name: createNote
     * Skipped Case: optional case
     * Reason: No optional parameter(s) to assert. 
     */
    
    /**
     * Negative test case for createNote method .
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateNoteWithMandatoryParameters"}, description = "wunderlist {createNote} integration test with negative case.")
    public void testCreateNoteWithNegativeCase() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:createNote");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createNote_negative.json");
        final JSONObject esbResponseErrorObject=esbRestResponse.getBody().getJSONObject("error");
        
        final String apiEndPoint = apiUrl + "/notes";
        final RestResponse<JSONObject> apiRestResponse =
        sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createNote_negative.json");
        final JSONObject apiResponseErrorObject=apiRestResponse.getBody().getJSONObject("error");
        
        Assert.assertEquals(esbResponseErrorObject.get("type"),apiResponseErrorObject.get("type"));
        Assert.assertEquals(esbResponseErrorObject.get("translation_key"),apiResponseErrorObject.get("translation_key"));
        Assert.assertEquals(esbResponseErrorObject.get("message"),apiResponseErrorObject.get("message"));
    }
    
    /**
     * Positive test case for getNote method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateNoteWithMandatoryParameters"}, description = "wunderlist {getNote} integration test with mandatory parameters.")
    public void testGetNoteWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:getNote");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getNote_mandatory.json");

        final String apiEndPoint = apiUrl +"/notes/"+ connectorProperties.getProperty("noteId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("content"), apiRestResponse.getBody().getString("content"));
        Assert.assertEquals(esbRestResponse.getBody().getString("type"), apiRestResponse.getBody().getString("type"));
        Assert.assertEquals(esbRestResponse.getBody().getString("task_id"), apiRestResponse.getBody().getString("task_id"));
        Assert.assertEquals(esbRestResponse.getBody().getString("revision"), apiRestResponse.getBody().getString("revision"));
    }
    
    /**
     * Method Name: getNote
     * Skipped Case: optional case
     * Reason: No optional parameter(s) to assert. 
     */
    
    /**
     * Negative test case for getNote method .
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "wunderlist {getNote} integration test with negative case.")
    public void testGetNoteWithNegativeCase() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:getNote");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getNote_negative.json");
        
        final String apiEndPoint = apiUrl +"/notes/INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getString("error"), apiRestResponse.getBody().getString("error"));
    }
    
    /**
     * Positive test case for listNotes method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateNoteWithMandatoryParameters"}, description = "wunderlist {listNotes} integration test with mandatory parameters.")
    public void testListNotesWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:listNotes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listNotes_mandatory.json");
        
        String responseArrayString=esbRestResponse.getBody().getString("output");
        JSONArray esbResponseArray=new JSONArray(responseArrayString);

        final String apiEndPoint = apiUrl +"/notes?task_id="+connectorProperties.getProperty("taskIdOpt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        responseArrayString=apiRestResponse.getBody().getString("output");
        
        JSONArray apiResponseArray=new JSONArray(responseArrayString);
   
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("id"), apiResponseArray.getJSONObject(0).get("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("content"), apiResponseArray.getJSONObject(0).get("content"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("revision"), apiResponseArray.getJSONObject(0).get("revision"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("type"), apiResponseArray.getJSONObject(0).get("type"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("task_id"), apiResponseArray.getJSONObject(0).get("task_id"));
    }
    
    /**
     * Method Name: getNote
     * Skipped Case: optional case
     * Reason: No optional parameter(s) to assert. 
     */
    
    /**
     * Negative test case for listNotes method .
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "wunderlist {listNotes} integration test with negative case.")
    public void testListNotesWithNegativeCase() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:listNotes");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listNotes_negative.json");
        final JSONObject esbResponseErrorObject=esbRestResponse.getBody().getJSONObject("error");
        
        final String apiEndPoint = apiUrl +"/notes";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponseErrorObject=apiRestResponse.getBody().getJSONObject("error");
        
        Assert.assertEquals(esbResponseErrorObject.get("type"), apiResponseErrorObject.get("type"));
        Assert.assertEquals(esbResponseErrorObject.get("translation_key"), apiResponseErrorObject.get("translation_key"));
        Assert.assertEquals(esbResponseErrorObject.get("message"), apiResponseErrorObject.get("message"));
        Assert.assertEquals(esbResponseErrorObject.getJSONArray("task_id or list_id").getString(0), apiResponseErrorObject.getJSONArray("task_id or list_id").getString(0));
    }
    
    /**
     * Method Name: updateNote
     * Skipped Case: mandatory case
     * Reason: No mandatory parameter(s) to assert. 
     */
    
    /**
     * Positive test case for updateNote method with optional parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" },dependsOnMethods={"testCreateNoteWithMandatoryParameters"}, description = "wunderlist {updateNote} integration test with optional parameters.")
    public void testUpdateNoteWithOptionalParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:updateNote");
        
        final String apiEndPoint = apiUrl +"/notes/"+ connectorProperties.getProperty("noteId");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateNote_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("content"), apiRestResponseAfter.getBody().getString("content"));
        Assert.assertEquals(apiRestResponseAfter.getBody().getString("content"), connectorProperties.get("contentUpdate"));
    }
    
    /**
     * Negative test case for updateNote method .
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testUpdateNoteWithOptionalParameters"}, description = "wunderlist {updateNote} integration test with negative case.")
    public void testUpdateNoteWithNegativeCase() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:updateNote");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateNote_negative.json");
        final JSONObject esbResponseErrorObject=esbRestResponse.getBody().getJSONObject("error");
        
        final String apiEndPoint = apiUrl + "/notes/"+connectorProperties.getProperty("noteId");
        RestResponse<JSONObject> apiRestResponse =
        sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateNote_negative.json");
        final JSONObject apiResponseErrorObject=apiRestResponse.getBody().getJSONObject("error");
        
        Assert.assertEquals(esbResponseErrorObject.get("type"),apiResponseErrorObject.get("type"));
        Assert.assertEquals(esbResponseErrorObject.get("translation_key"),apiResponseErrorObject.get("translation_key"));
        Assert.assertEquals(esbResponseErrorObject.get("message"),apiResponseErrorObject.get("message"));
        Assert.assertEquals(esbResponseErrorObject.get("revision_conflict").toString(),apiResponseErrorObject.get("revision_conflict").toString());
    }
    
    /**
     * Positive test case for createReminder method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testUpdateNoteWithOptionalParameters"}, description = "wunderlist {createReminder} integration test with mandatory parameters.")
    public void testCreateReminderWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:createReminder");
        
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date()); 
		calendar.add(Calendar.DATE, 2); 
		final String reminderDate = dateFormat.format(calendar.getTime());
        connectorProperties.setProperty("reminderDate", reminderDate);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createReminder_mandatory.json");
                
        final String reminderId = esbRestResponse.getBody().getString("id");
        connectorProperties.setProperty("reminderId", reminderId);
        final String reminderRevision = esbRestResponse.getBody().getString("revision");
        connectorProperties.setProperty("reminderRevision", reminderRevision);
        
        final String apiEndPoint = apiUrl +"/reminders/"+ reminderId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String apiReminderDate=apiRestResponse.getBody().getString("date");
        final String[] dateArray=apiReminderDate.split("T");
        apiReminderDate=dateArray[0];
        
        Assert.assertEquals(apiRestResponse.getBody().getString("task_id"), connectorProperties.get("taskIdOpt"));
        Assert.assertEquals(apiReminderDate, reminderDate);
    }
    
    /**
     * Method Name: createReminder
     * Skipped Case: optional case
     * Reason: No optional parameter(s) to assert. 
     */
    
    /**
     * Negative test case for createReminder method .
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateReminderWithMandatoryParameters"}, description = "wunderlist {createReminder} integration test with negative case.")
    public void testCreateReminderWithNegativeCase() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:createReminder");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createReminder_negative.json");
        final JSONObject esbResponseErrorObject=esbRestResponse.getBody().getJSONObject("error");
        
        final String apiEndPoint = apiUrl + "/reminders";
        RestResponse<JSONObject> apiRestResponse =
        sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createReminder_negative.json");
        final JSONObject apiResponseErrorObject=apiRestResponse.getBody().getJSONObject("error");
        
        Assert.assertEquals(esbResponseErrorObject.get("type"),apiResponseErrorObject.get("type"));
        Assert.assertEquals(esbResponseErrorObject.get("translation_key"),apiResponseErrorObject.get("translation_key"));
        Assert.assertEquals(esbResponseErrorObject.get("message"),apiResponseErrorObject.get("message"));
    }
    
    /**
     * Positive test case for listReminders method with mandatory parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testCreateReminderWithMandatoryParameters"}, description = "wunderlist {listReminders} integration test with mandatory parameters.")
    public void testListRemindersWithMandatoryParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:listReminders");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listReminders_mandatory.json");
        
        String responseArrayString=esbRestResponse.getBody().getString("output");
        final JSONArray esbResponseArray=new JSONArray(responseArrayString);
        
        final String apiEndPoint = apiUrl +"/reminders?task_id="+connectorProperties.getProperty("taskIdOpt");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        responseArrayString=apiRestResponse.getBody().getString("output");
       
        final JSONArray apiResponseArray=new JSONArray(responseArrayString);
   
        Assert.assertEquals(esbResponseArray.length(), apiResponseArray.length());
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("id"), apiResponseArray.getJSONObject(0).get("id"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("date"), apiResponseArray.getJSONObject(0).get("date"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("revision"), apiResponseArray.getJSONObject(0).get("revision"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("type"), apiResponseArray.getJSONObject(0).get("type"));
        Assert.assertEquals(esbResponseArray.getJSONObject(0).get("created_at"), apiResponseArray.getJSONObject(0).get("created_at"));
    }
    
    /**
     * Method Name: listReminders
     * Skipped Case: optional case
     * Reason: No optional parameter(s) to assert. 
     */
    
    /**
     * Negative test case for listReminders method .
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, description = "wunderlist {listReminders} integration test with negative case.")
    public void testListRemindersWithNegativeCase() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:listReminders");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listReminders_negative.json");
        final JSONObject esbResponseErrorObject=esbRestResponse.getBody().getJSONObject("error");
        
        final String apiEndPoint = apiUrl +"/reminders?task_id=INVALID";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponseErrorObject=apiRestResponse.getBody().getJSONObject("error");
        
        Assert.assertEquals(esbResponseErrorObject.get("type"), apiResponseErrorObject.get("type"));
        Assert.assertEquals(esbResponseErrorObject.get("translation_key"), apiResponseErrorObject.get("translation_key"));
        Assert.assertEquals(esbResponseErrorObject.get("message"), apiResponseErrorObject.get("message"));
    }
    
    /**
     * Method Name: updateReminder
     * Skipped Case: mandatory case
     * Reason: No mandatory parameter(s) to assert. 
     */
    
    /**
     * Positive test case for updateReminder method with optional parameters.
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" },dependsOnMethods={"testCreateNoteWithMandatoryParameters"}, description = "wunderlist {updateReminder} integration test with optional parameters.")
    public void testUpdateReminderWithOptionalParameters() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:updateReminder");
        
        String apiEndPoint = apiUrl +"/reminders/"+ connectorProperties.getProperty("reminderId");
        RestResponse<JSONObject> apiRestResponseBefore = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date()); 
		calendar.add(Calendar.DATE, 5); 
		String reminderDateUpdate = dateFormat.format(calendar.getTime());
		connectorProperties.setProperty("reminderDateUpdate", reminderDateUpdate);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateReminder_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfter = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        String apiReminderDateAfter=apiRestResponseAfter.getBody().getString("date");
        String[] dateArray=apiReminderDateAfter.split("T");
        apiReminderDateAfter=dateArray[0];
        
        Assert.assertNotEquals(apiRestResponseBefore.getBody().getString("date"), apiRestResponseAfter.getBody().getString("date"));
        Assert.assertEquals(apiReminderDateAfter, connectorProperties.get("reminderDateUpdate"));
    }
    
    /**
     * Negative test case for updateReminder method .
     * @throws JSONException 
     * @throws IOException 
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods={"testUpdateReminderWithOptionalParameters"}, description = "wunderlist {updateReminder} integration test with negative case.")
    public void testUpdateReminderWithNegativeCase() throws IOException, JSONException{
    
        esbRequestHeadersMap.put("Action", "urn:updateReminder");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateReminder_negative.json");
       JSONObject esbResponseErrorObject=esbRestResponse.getBody().getJSONObject("error");
        
        
        String apiEndPoint = apiUrl + "/reminders/"+connectorProperties.getProperty("reminderId");
        RestResponse<JSONObject> apiRestResponse =
        sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateReminder_negative.json");
        JSONObject apiResponseErrorObject=apiRestResponse.getBody().getJSONObject("error");
        
        Assert.assertEquals(esbResponseErrorObject.get("type"),apiResponseErrorObject.get("type"));
        Assert.assertEquals(esbResponseErrorObject.get("translation_key"),apiResponseErrorObject.get("translation_key"));
        Assert.assertEquals(esbResponseErrorObject.get("message"),apiResponseErrorObject.get("message"));
        Assert.assertEquals(esbResponseErrorObject.get("revision_conflict").toString(),apiResponseErrorObject.get("revision_conflict").toString());
    }
    
    
    
}
