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

package org.wso2.carbon.connector.integration.test.box;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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


public class BoxConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> headersMap = new HashMap<String, String>();
    
    private String multipartProxyUrl;
    
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("box-connector-1.0.0");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.put("Accept-Charset", "UTF-8");
        apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");
        apiRequestHeadersMap.put("Authorization", "Bearer "+connectorProperties.getProperty("accessToken"));
        
        String multipartPoxyName = connectorProperties.getProperty("multipartProxyName");                
        multipartProxyUrl = getProxyServiceURL(multipartPoxyName);
        listUsers();
        
    }
    
    /**
     * Positive test case for createFolder method with mandatory parameters.
     * 
     */
    @Test(priority = 1, description = "box {createFolder} integration test with mandatory parameters.")
    public void testCreateFolderWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createFolder");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createFolder_mandatory.json");
        connectorProperties.put("folderId", esbRestResponse.getBody().get("id").toString());
        
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/folders/"+esbRestResponse.getBody().get("id");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
        Assert.assertTrue(apiRestResponse.getBody().has("name"));
        Assert.assertTrue(apiRestResponse.getBody().has("created_at"));
    }
    
    /**
     * Positive test case for createFolder method with optional parameters.
     * 
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateFolderWithMandatoryParameters"}, description = "box {createFolder} integration test with optional parameters.")
    public void testCreateFolderWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createFolder");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createFolder_optional.json");
        connectorProperties.put("folderId2", esbRestResponse.getBody().get("id").toString());
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/folders/"+esbRestResponse.getBody().get("id");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
        Assert.assertTrue(apiRestResponse.getBody().has("name"));
        Assert.assertTrue(apiRestResponse.getBody().has("created_at"));
        Assert.assertTrue(!esbRestResponse.getBody().has("created_at"));
        Assert.assertTrue(esbRestResponse.getBody().has("name"));
    }
    
    /**
     * Negative test case for createFolder.
     * 
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateFolderWithOptionalParameters"}, description = "box {createFolder} integration test negative case.")
    public void testCreateFolderNegativeCase() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:createFolder");
        
		Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createFolder_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/folders/";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createFolder_negative.json");
        Assert.assertEquals(esbRestResponse.getBody().get("code").toString(),apiRestResponse.getBody().get("code").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("status").toString(),apiRestResponse.getBody().get("status").toString());
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404 );
    }
    
    /**
     * Positive test case for listUsers method with mandatory parameters.
     * 
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateFolderNegativeCase"}, description = "box {listUsers} integration test with mandatory parameters.")
    public void testListUsersWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:listUsers");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/users/";
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_mandatory.json"); 
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").length(), apiRestResponse.getBody().getJSONArray("entries").length() );
        Assert.assertEquals(esbRestResponse.getBody().get("total_count"), apiRestResponse.getBody().get("total_count"));
    }
    
    /**
     * Positive test case for listUsers method with optional parameters.
     * 
     */
    @Test(priority = 1, dependsOnMethods = {"testListUsersWithMandatoryParameters"}, description = "box {listUsers} integration test with optional parameters.")
    public void testListUsersWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listUsers");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/users?limit=1";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_optional.json"); 
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").length(), apiRestResponse.getBody().getJSONArray("entries").length() );
        Assert.assertEquals(esbRestResponse.getBody().get("total_count"), apiRestResponse.getBody().get("total_count"));

    }
    
    /**
     * Negative test case for listUsers method.
     * 
     */
    @Test(priority = 1, dependsOnMethods = {"testListUsersWithOptionalParameters"}, description = "box {listUsers} integration test negative case.")
    public void testListUsersNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listUsers");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/users?limit=-2";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_negative.json"); 
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("code").toString(),apiRestResponse.getBody().get("code").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("status").toString(),apiRestResponse.getBody().get("status").toString());
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400 );
    }
    
    /**
     * Positive test case for copyFolder method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testListUsersNegativeCase"}, description = "box {copyFolder} integration test with mandatory parameters.")
    public void testCopyFolderWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:copyFolder");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_copyFolder_mandatory.json");
        
        String copiedFolderId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("cleanupFolderId1", esbRestResponse.getBody().get("id").toString());
        
        String apiEndPoint = connectorProperties.getProperty("boxApiUrl") + "/2.0/folders/" + copiedFolderId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject copiedFolderParent = apiRestResponse.getBody().getJSONObject("parent");
        
        Assert.assertEquals(connectorProperties.get("parentId"), copiedFolderParent.get("id"));
        
    }
    
    /**
     * Positive test case for copyFolder method with optional parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testCopyFolderWithMandatoryParameters"}, description = "box {copyFolder} integration test with optional parameters.")
    public void testCopyFolderWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:copyFolder");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_copyFolder_optional.json");
        
        String copiedFolderId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("cleanupFolderId2", esbRestResponse.getBody().get("id").toString());
        
        String apiEndPoint = connectorProperties.getProperty("boxApiUrl") + "/2.0/folders/" + copiedFolderId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONObject copiedFolder = apiRestResponse.getBody();
        
        Assert.assertEquals(copiedFolder.get("name"), connectorProperties.get("copyFolderName"));
        
    }
    
    /**
     * Negative test case for copyFolder method with Negative case.
     */
    @Test(priority = 1, dependsOnMethods = { "testCopyFolderWithOptionalParameters" }, description = "box {copyFolder} integration test with Negative case.")
    public void testCopyFolderWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:copyFolder");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_copyFolder_optional.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/folders/"
                        + connectorProperties.getProperty("folderId") + "/copy";
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_copyFolder_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for shareFolder method with optional parameters.
     * 
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateFolderWithOptionalParameters"}, description = "box {shareFolder} integration test with optional parameters.")
    public void testShareFolderWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:shareFolder");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/folders/"+connectorProperties.getProperty("folderId");   
        RestResponse<JSONObject> apiRestResponseBeforeShare = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_shareFolder_optional.json");
        RestResponse<JSONObject> apiRestResponseAfterShare = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponseBeforeShare.getBody().isNull("shared_link"));
        Assert.assertTrue(apiRestResponseAfterShare.getBody().getJSONObject("shared_link").get("access").toString().equals("collaborators"));
    }
    
    /**
     * Negative test case for shareFolder method.
     * 
     */
    @Test(priority = 1, dependsOnMethods = {"testShareFolderWithOptionalParameters"}, description = "box {shareFolder} integration test negative case.")
    public void testShareFolderNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:shareFolder");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/folders/"+connectorProperties.getProperty("folderId");   
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_shareFolder_negative.json");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_shareFolder_negative.json");
        Assert.assertEquals(esbRestResponse.getBody().get("code").toString(),apiRestResponse.getBody().get("code").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("status").toString(),apiRestResponse.getBody().get("status").toString());
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400 );
    }
    
    /**
     * Positive test case for updateFolderInformation method with optional parameters.
     * 
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateFolderWithMandatoryParameters"}, description = "box {updateFolderInformation} integration test with mandatory parameters.")
    public void testUpdateFolderInformationWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateFolderInformation");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/folders/"+connectorProperties.getProperty("folderId");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateFolderInformation_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("type"), apiRestResponse.getBody().get("type"));
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("description"), apiRestResponse.getBody().get("description"));
        
    }
    
    /**
     * Negative test case for updateFolderInformation method.
     * 
     */
    @Test(priority = 1, dependsOnMethods = {"testUpdateFolderInformationWithOptionalParameters"}, description = "box {updateFolderInformation} integration test with mandatory parameters.")
    public void testUpdateFolderInformationWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateFolderInformation");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/folders/invalid";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateFolderInformation_negative.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for getFolderInformation method with mandatory parameters.
     * 
     */
    @Test(priority = 1, dependsOnMethods = {"testUpdateFolderInformationWithOptionalParameters"}, description = "box {getFolderInformation} integration test with mandatory parameters.")
    public void testGetFolderInformationWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getFolderInformation");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/folders/"+connectorProperties.getProperty("folderId");        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFolderInformation_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		
        Assert.assertEquals(esbRestResponse.getBody().get("content_created_at"), apiRestResponse.getBody().get("content_created_at"));
        Assert.assertEquals(esbRestResponse.getBody().get("type"), apiRestResponse.getBody().get("type"));       
        Assert.assertEquals(esbRestResponse.getBody().get("size"), apiRestResponse.getBody().get("size"));
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));       
    }  
 
    /**
     * Positive test case for getFolderInformation method with optional parameters.
     * 
     */
    @Test(priority = 1, dependsOnMethods = {"testGetFolderInformationWithMandatoryParameters"}, description = "box {getFolderInformation} integration test with optional parameters.")
    public void testGetFolderInformationWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getFolderInformation");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/folders/"+connectorProperties.getProperty("folderId")+"?fields=content_created_at,created_at,id,modified_at,type";        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFolderInformation_optional.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		
        Assert.assertEquals(esbRestResponse.getBody().get("content_created_at"), apiRestResponse.getBody().get("content_created_at")); 
        Assert.assertEquals(esbRestResponse.getBody().get("created_at"), apiRestResponse.getBody().get("created_at"));       
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("modified_at"), apiRestResponse.getBody().get("modified_at"));     
        Assert.assertEquals(esbRestResponse.getBody().get("type"), apiRestResponse.getBody().get("type"));
    }     

    /**
     * Negative test case for getFolderInformation method.
     * 
     */
    @Test(priority = 1, dependsOnMethods = {"testGetFolderInformationWithOptionalParameters"}, description = "box {getFolderInformation} integration test with Negative test case.")
    public void testGetFolderInformationWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getFolderInformation");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/folders/xxx";        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFolderInformation_negative.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("code"), apiRestResponse.getBody().get("code"));
        Assert.assertEquals(esbRestResponse.getBody().get("help_url"), apiRestResponse.getBody().get("help_url"));       
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));       
    }
    
    /**
     * Positive test case for uploadFile method.
     * @throws NoSuchAlgorithmException 
     */
    @Test(priority = 1, dependsOnMethods = {"testCreateFolderWithMandatoryParameters"}, description = "box {uploadFile} integration test.")
    public void testuploadFile() throws IOException, JSONException, NoSuchAlgorithmException {
    
        
        
        headersMap.put("Authorization", "Bearer " + connectorProperties.getProperty("accessToken"));
       
        
        MultipartFormdataProcessor multipartProcessor =
                new MultipartFormdataProcessor(multipartProxyUrl, headersMap);
        
       
        multipartProcessor.addFormDataToRequest("parent_id", connectorProperties.getProperty("folderId"));
        multipartProcessor.addFileToRequest("file", connectorProperties.getProperty("uploadFileName"),null,connectorProperties.getProperty("targetFileName"));
        
        RestResponse<JSONObject> esbRestResponse = multipartProcessor.processForJsonResponse();
       
        JSONArray entriseArray=esbRestResponse.getBody().getJSONArray("entries");
        
        String fileId=entriseArray.getJSONObject(0).getString("id");
        connectorProperties.put("fileId", fileId);
        String apiEndPoint = connectorProperties.getProperty("boxApiUrl") + "/2.0/files/" + fileId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        File file = new File(pathToResourcesDirectory + connectorProperties.getProperty("uploadFileName"));
        String originalFileHash = getFileHash(new FileInputStream(file));
        String uploadedFileHash=apiRestResponse.getBody().getString("sha1");
        
        Assert.assertEquals(originalFileHash, uploadedFileHash);
        
    }
    
    /**
     * Positive test case for moveFolder method with mandatory parameters.
     */
    @Test(priority = 1, dependsOnMethods = {"testuploadFile"}, description = "box {moveFolder} integration test with mandatory parameters.")
    public void testMoveFolderWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:moveFolder");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_moveFolder_mandatory.json");
        
        String movedFolderId = esbRestResponse.getBody().getString("id");
        
        String apiEndPoint = connectorProperties.getProperty("boxApiUrl") + "/2.0/folders/" + movedFolderId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        
        
        Assert.assertTrue(apiRestResponse.getBody().has("id"));
        Assert.assertEquals(apiRestResponse.getBody().get("type"), "folder");
        Assert.assertTrue(apiRestResponse.getBody().getString("name").contains(connectorProperties.getProperty("sourceUserName")));
        
    }
    
    /**
     * Negative test case for moveFolder method with Negative case.
     */
    @Test(priority = 1, dependsOnMethods = {"testMoveFolderWithMandatoryParameters"}, description = "box {moveFolder} integration test with Negative case.")
    public void testMoveFolderWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:moveFolder");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_moveFolder_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/users/"+connectorProperties.getProperty("sourceUserId")+"/folders/0";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap,"api_moveFolder_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for getFileInformation method with mandatory parameters.
     * 
     */
    @Test(priority = 2, dependsOnMethods = {"testuploadFile"}, description = "box {getFileInformation} integration test with mandatory parameters.")
    public void testGetFileInformationWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getFileInformation");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/files/"+connectorProperties.getProperty("fileId");        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFileInformation_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		
        Assert.assertEquals(esbRestResponse.getBody().get("content_created_at"), apiRestResponse.getBody().get("content_created_at"));
        Assert.assertEquals(esbRestResponse.getBody().get("sha1"), apiRestResponse.getBody().get("sha1"));       
        Assert.assertEquals(esbRestResponse.getBody().get("size"), apiRestResponse.getBody().get("size"));
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));       
    }  
 
    /**
     * Positive test case for getFileInformation method with optional parameters.
     * 
     */
    @Test(priority = 2, dependsOnMethods = {"testGetFileInformationWithMandatoryParameters"}, description = "box {getFileInformation} integration test with optional parameters.")
    public void testGetFileInformationWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getFileInformation");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/files/"+connectorProperties.getProperty("fileId")+"?fields=created_at,sha1,size,modified_at,type,id";        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFileInformation_optional.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		
        Assert.assertEquals(esbRestResponse.getBody().get("created_at"), apiRestResponse.getBody().get("created_at")); 
        Assert.assertEquals(esbRestResponse.getBody().get("sha1"), apiRestResponse.getBody().get("sha1"));       
        Assert.assertEquals(esbRestResponse.getBody().get("size"), apiRestResponse.getBody().get("size"));
        Assert.assertEquals(esbRestResponse.getBody().get("modified_at"), apiRestResponse.getBody().get("modified_at"));     
        Assert.assertEquals(esbRestResponse.getBody().get("type"), apiRestResponse.getBody().get("type"));
        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));   
    }     

    /**
     * Negative test case for getFileInformation method.
     * 
     */
    @Test(priority = 2, dependsOnMethods = {"testGetFileInformationWithOptionalParameters"}, description = "box {getFileInformation} integration test with Negative test case.")
    public void testGetFileInformationtWithNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getFileInformation");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/files/xxx";        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFileInformation_negative.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("code"), apiRestResponse.getBody().get("code"));
        Assert.assertEquals(esbRestResponse.getBody().get("help_url"), apiRestResponse.getBody().get("help_url"));       
        Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
        Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));       
    }
    
    /**
     * Positive test case for updateFileInformation method with optional parameters.
     * 
     */
    @Test(priority = 2, dependsOnMethods = {"testGetFileInformationtWithNegativeCase"}, description = "box {updateFileInformation} integration test with mandatory parameters.")
    public void testUpdateFileInformationWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateFileInformation");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/files/"+connectorProperties.getProperty("fileId");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateFileInformation_optional.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("type"), apiRestResponse.getBody().get("type"));
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("description"), apiRestResponse.getBody().get("description"));
        
    }
    
    /**
     * Negative test case for updateFileInformation method.
     * 
     */
    @Test(priority = 2, dependsOnMethods = {"testUpdateFileInformationWithOptionalParameters"}, description = "box {updateFileInformation} integration test with mandatory parameters.")
    public void testUpdateFileInformationWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:updateFileInformation");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/files/invalid";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateFileInformation_negative.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for downloadFile method with mandatory parameters.
     * 
     */
    @Test(priority = 2, dependsOnMethods = {"testUpdateFileInformationWithNegativeCase"}, description = "box {downloadFile} integration test with mandatory parameters.")
    public void downLoadFileWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:downloadFile");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/files/"+connectorProperties.getProperty("fileId") + "/content";
        
        InputStream downloadedFileApiResponse = processForInputStream(apiEndPoint, "GET", apiRequestHeadersMap , null , null);
        InputStream downloadedFileESBResponse = processForInputStream(proxyUrl, "POST", esbRequestHeadersMap, "esb_downloadFile_mandatory.json" , null);
        
        byte[] buffer = new byte[1024];
        byte[] buffer1 = new byte[1024];
        int len , len1;
        
        // Read from inputStream to avoid content being stuck in stream
        while ((len = downloadedFileApiResponse.read(buffer)) != -1) { }        
        while ((len1 = downloadedFileESBResponse.read(buffer1)) != -1) { }

		try {
			String downloadedFileHashFromApi = getFileHash(downloadedFileApiResponse);
			String downloadedFileHashFromESB = getFileHash(downloadedFileESBResponse);
		    Assert.assertEquals(downloadedFileHashFromApi, downloadedFileHashFromESB);
		} catch (NoSuchAlgorithmException e) {
			Assert.assertTrue(false);
		}
        
    }
    
    /**
     * Positive test case for downloadFile method with optional parameters.
     *
     */
    @Test(priority = 2, dependsOnMethods = {"downLoadFileWithMandatoryParameters"}, description = "box {downloadFile} integration test with optional parameters.")
    public void downLoadFileWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:downloadFile");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/files/"+connectorProperties.getProperty("fileId") + "/content?version=0";
        
        InputStream downloadedFileApiResponse = processForInputStream(apiEndPoint, "GET", apiRequestHeadersMap , null , null);
        InputStream downloadedFileESBResponse = processForInputStream(proxyUrl, "POST", esbRequestHeadersMap, "esb_downloadFile_optional.json" , null);
        
        byte[] buffer = new byte[1024];
        byte[] buffer1 = new byte[1024];
        int len , len1;
        
        // Read from inputStream to avoid content being stuck in stream
        while ((len = downloadedFileApiResponse.read(buffer)) != -1) { }        
        while ((len1 = downloadedFileESBResponse.read(buffer1)) != -1) { }

		try {
			String downloadedFileHashFromApi = getFileHash(downloadedFileApiResponse);
			String downloadedFileHashFromESB = getFileHash(downloadedFileESBResponse);
		    Assert.assertEquals(downloadedFileHashFromApi, downloadedFileHashFromESB);
		} catch (NoSuchAlgorithmException e) {
			Assert.assertTrue(false);
		}
        
    }
    
    /**
     * Negative test case for downloadFile method.
     * 
     */
    @Test(priority = 2, dependsOnMethods = {"downLoadFileWithOptionalParameters"}, description = "box {downloadFile} integration test with Negative case.")
    public void downLoadFileWithNegativeTestCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:downloadFile");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/files/"+connectorProperties.getProperty("fileId") + "/content?version=100000";

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_downloadFile_negative.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("code").toString(), apiRestResponse.getBody().get("code").toString());
        
    }
    
    /**
     * Positive test case for shareFile method with optional parameters.
     * 
     */
    @Test(priority = 2, dependsOnMethods = {"downLoadFileWithNegativeTestCase"}, description = "box {shareFile} integration test with mandatory parameters.")
    public void testShareFileWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:shareFile");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/files/"+connectorProperties.getProperty("fileId");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_shareFile_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        JSONObject esbResponseJsonArray = new JSONObject(esbRestResponse.getBody().getJSONObject("shared_link").toString());

        JSONObject apiResponseJsonArray = new JSONObject(apiRestResponse.getBody().get("shared_link").toString());
        Assert.assertEquals(esbResponseJsonArray.get("url").toString(), apiResponseJsonArray.get("url").toString());
        Assert.assertEquals(esbResponseJsonArray.get("download_url").toString(), apiResponseJsonArray.get("download_url").toString());
        Assert.assertEquals(esbResponseJsonArray.get("access").toString(), apiResponseJsonArray.get("access").toString());
        
    }
    
    /**
     * Negative test case for shareFile method.
     * 
     */
    @Test(priority = 2, dependsOnMethods = {"testShareFileWithOptionalParameters"}, description = "box {shareFile} integration test with mandatory parameters.")
    public void testShareFileWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:shareFile");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/files/invalid";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_shareFile_negative.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for copyFile method with mandatory parameters.
     * 
     */
    @Test(priority = 2, dependsOnMethods = { "testShareFileWithNegativeCase" }, description = "box {copyFile} integration test with mandatory parameters.")
    public void testCopyFileWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:copyFile");       
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_copyFile_mandatory.json");
        String copiedFileId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("copiedFileId", copiedFileId);
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/files/" + copiedFileId; 
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("content_created_at"), apiRestResponse.getBody().get("content_created_at"));
        Assert.assertEquals(esbRestResponse.getBody().get("sha1"), apiRestResponse.getBody().get("sha1"));       
        Assert.assertEquals(esbRestResponse.getBody().get("size"), apiRestResponse.getBody().get("size"));
        Assert.assertEquals(esbRestResponse.getBody().get("type"), apiRestResponse.getBody().get("type"));       
    }
     
    /**
     * Positive test case for copyFile method with optional parameters.
     * 
     */
    @Test(priority = 2,dependsOnMethods = { "testCopyFileWithMandatoryParameters" }, description = "box {copyFile} integration test with optional parameters.")
    public void testCopyFileWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:copyFile");       
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_copyFile_optional.json");
        String copiedFileId = esbRestResponse.getBody().getString("id");
        connectorProperties.put("cleanupFileId", esbRestResponse.getBody().get("id").toString());
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/files/" + copiedFileId+"?fields=content_created_at,item_status,name,sha1"; 
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		
        Assert.assertEquals(esbRestResponse.getBody().get("content_created_at"), apiRestResponse.getBody().get("content_created_at"));
        Assert.assertEquals(esbRestResponse.getBody().get("item_status"), apiRestResponse.getBody().get("item_status"));       
        Assert.assertEquals(esbRestResponse.getBody().get("name"), apiRestResponse.getBody().get("name"));
        Assert.assertEquals(esbRestResponse.getBody().get("sha1"), apiRestResponse.getBody().get("sha1"));       
    }   
    
   /**
    * Negative test case for copyFile method with Negative case.
    */
   @Test(priority = 2, dependsOnMethods = { "testCopyFileWithOptionalParameters" }, description = "box {copyFile} integration test with Negative case.")
   public void testCopyFileWithNegativeCase() throws IOException, JSONException {
   
       esbRequestHeadersMap.put("Action", "urn:copyFile");
       
       RestResponse<JSONObject> esbRestResponse =
               sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_copyFile_negative.json");
       
       String apiEndPoint =
               connectorProperties.getProperty("boxApiUrl") + "/2.0/files/"+ connectorProperties.getProperty("fileId") + "/copy";
       RestResponse<JSONObject> apiRestResponse =
               sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_copyFile_negative.json");
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());        
   }
   
   /**
    * Positive test case for createComment method with mandatory parameters.
    */
   @Test(priority = 2, dependsOnMethods = { "testCopyFileWithNegativeCase" }, description = "box {createComment} integration test with mandatory parameters.")
   public void testCreateCommentWithMandatoryParameters() throws IOException, JSONException {
   
       esbRequestHeadersMap.put("Action", "urn:createComment");
       
       RestResponse<JSONObject> esbRestResponse =
               sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_mandatory.json");
       
       String createdCommentId = esbRestResponse.getBody().getString("id");
       connectorProperties.put("commentId", createdCommentId);
       
       String apiEndPoint = connectorProperties.getProperty("boxApiUrl") + "/2.0/comments/" + createdCommentId;
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       Assert.assertEquals(connectorProperties.get("commentMessage"), apiRestResponse.getBody().get("message"));
       
   }

   /**
    * Positive test case for createComment method with optional parameters.
    */
   @Test(priority = 2, dependsOnMethods = { "testCreateCommentWithMandatoryParameters" }, description = "box {createComment} integration test with optional parameters.")
   public void testCreateCommentWithOptionalParameters() throws IOException, JSONException {
   
       esbRequestHeadersMap.put("Action", "urn:createComment");
       
       RestResponse<JSONObject> esbRestResponse =
               sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_optional.json");
       
       String createdCommentId = esbRestResponse.getBody().getString("id");
       connectorProperties.put("commentId", createdCommentId);
       
       String apiEndPoint = connectorProperties.getProperty("boxApiUrl") + "/2.0/comments/" + createdCommentId;
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       Assert.assertEquals(esbRestResponse.getBody().get("message"),apiRestResponse.getBody().get("message"));
       Assert.assertTrue(esbRestResponse.getBody().has("created_at"));
       Assert.assertFalse(esbRestResponse.getBody().has("created_by"));
       
   }
   
   /**
    * Negative test case for createComment method with Negative case.
    */
   @Test(priority = 2, dependsOnMethods = { "testCreateCommentWithOptionalParameters" }, description = "box {createComment} integration test with Negative case.")
   public void testCreateCommentWithNegativeCase() throws IOException, JSONException {
   
       esbRequestHeadersMap.put("Action", "urn:createComment");
       
       RestResponse<JSONObject> esbRestResponse =
               sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComment_mandatory.json");
       
       String apiEndPoint = connectorProperties.getProperty("boxApiUrl") + "/2.0/comments";
       RestResponse<JSONObject> apiRestResponse =
               sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createComment_negative.json");
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
       
   }
   
   /**
    * Positive test case for getSharedItem method with mandatory parameters.  
    * 
    */
   @Test(priority = 2, dependsOnMethods = { "testCreateCommentWithNegativeCase" }, description = "box {getSharedItem} integration test with mandatory parameters.")
   public void getSharedItemWithMandatoryParameters() throws IOException, JSONException {
   
       esbRequestHeadersMap.put("Action", "urn:getSharedItem");
       
       apiRequestHeadersMap.put("BoxApi", "shared_link=" + connectorProperties.getProperty("sharedLink"));
       
       String apiEndPoint =
               connectorProperties.getProperty("boxApiUrl") + "/2.0/shared_items";
       
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSharedItem_mandatory.json");
               
       Assert.assertEquals(esbRestResponse.getBody().get("id").toString(), apiRestResponse.getBody().get("id").toString());
       Assert.assertEquals(esbRestResponse.getBody().get("sha1").toString(), apiRestResponse.getBody().get("sha1").toString());
       Assert.assertEquals(esbRestResponse.getBody().get("name").toString(), apiRestResponse.getBody().get("name").toString());

   }
   
   /**
    * Positive test case for getSharedItem method with optional parameters.  
    * 
    */
   @Test(priority = 2, dependsOnMethods = { "getSharedItemWithMandatoryParameters" }, description = "box {getSharedItem} integration test with optional parameters.")
   public void getSharedItemWithOptionalParameters() throws IOException, JSONException {
   
       esbRequestHeadersMap.put("Action", "urn:getSharedItem");
       
       apiRequestHeadersMap.put("BoxApi", "shared_link=" + connectorProperties.getProperty("sharedLink") + "&shared_link_password=" + connectorProperties.getProperty("sharedLinkPassword"));
       
       String apiEndPoint =
               connectorProperties.getProperty("boxApiUrl") + "/2.0/shared_items";
       
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSharedItem_optional.json");
               
       Assert.assertEquals(esbRestResponse.getBody().get("id").toString(), apiRestResponse.getBody().get("id").toString());
       Assert.assertEquals(esbRestResponse.getBody().get("sha1").toString(), apiRestResponse.getBody().get("sha1").toString());
       Assert.assertEquals(esbRestResponse.getBody().get("name").toString(), apiRestResponse.getBody().get("name").toString());

   }
   
   /**
    * Negative test case for getSharedItem method.  
    * 
    */
   @Test(priority = 2, dependsOnMethods = { "getSharedItemWithOptionalParameters" }, description = "box {getSharedItem} integration test with Negative Case.")
   public void getSharedItemWithNegativeCase() throws IOException, JSONException {
   
       esbRequestHeadersMap.put("Action", "urn:getSharedItem");
       
       String sharedLink = connectorProperties.get("sharedLink").toString();        
       String invalidSharedLink = "";
       connectorProperties.put("sharedLink", invalidSharedLink);
       
       apiRequestHeadersMap.put("BoxApi", "shared_link=" + invalidSharedLink);
       
       String apiEndPoint =
               connectorProperties.getProperty("boxApiUrl") + "/2.0/shared_items";
       
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSharedItem_negative.json");
               
       Assert.assertEquals(esbRestResponse.getHttpStatusCode() , 404);
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
       Assert.assertEquals(esbRestResponse.getBody().get("code").toString(), apiRestResponse.getBody().get("code").toString());

       connectorProperties.put("sharedLink", sharedLink);
   }
   
   /**
    * Positive test case for search method with mandatory parameters.
    */
   @Test(priority = 2, dependsOnMethods = { "getSharedItemWithNegativeCase" }, description = "box {search} integration test with mandatory parameters.")
   public void testSearchWithMandatoryParameters() throws IOException, JSONException {
   
       esbRequestHeadersMap.put("Action", "urn:search");
       
       RestResponse<JSONObject> esbRestResponse =
               sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_search_mandatory.json");
       
       String apiEndPoint =
               connectorProperties.getProperty("boxApiUrl") + "/2.0/search?query="
                       + connectorProperties.getProperty("query");
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       Assert.assertEquals(esbRestResponse.getBody().get("total_count"), apiRestResponse.getBody().get("total_count"));
       Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").length(), apiRestResponse.getBody()
               .getJSONArray("entries").length());
       
   }
   
   /**
    * Positive test case for search method with optional parameters.
    */
   @Test(priority = 2, dependsOnMethods = { "testSearchWithMandatoryParameters" }, description = "box {search} integration test with optional parameters.")
   public void testSearchWithOptionalParameters() throws IOException, JSONException {
   
       esbRequestHeadersMap.put("Action", "urn:search");
       
       RestResponse<JSONObject> esbRestResponse =
               sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_search_optional.json");
       
       String apiEndPoint =
               connectorProperties.getProperty("boxApiUrl") + "/2.0/search?query="
                       + connectorProperties.getProperty("query") + "&limit=5&offset=10";
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       Assert.assertEquals(esbRestResponse.getBody().get("total_count"), apiRestResponse.getBody().get("total_count"));
       Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").length(), apiRestResponse.getBody()
               .getJSONArray("entries").length());
       
   }
   
   /**
    * Negative test case for search method with Negative case.
    */
   @Test(priority = 2, dependsOnMethods = { "testSearchWithOptionalParameters" }, description = "box {search} integration test with Negative case.")
   public void testSearchWithNegativeCase() throws IOException, JSONException {
   
       esbRequestHeadersMap.put("Action", "urn:search");
       
       RestResponse<JSONObject> esbRestResponse =
               sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_search_negative.json");
       
       String apiEndPoint =
               connectorProperties.getProperty("boxApiUrl") + "/2.0/search?query="
                       + connectorProperties.getProperty("query") + "&limit=3&offset=10";
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
       
   }
   
   /**
    * Positive test case for listFileComments with mandatory parameters.
    * 
    */
   @Test(priority = 3, dependsOnMethods = { "testSearchWithNegativeCase" }, description = "box {listFileComments} integration test with mandatory parameters.")
   public void listFileCommentsWithMandatoryParameters() throws IOException, JSONException {
   
       esbRequestHeadersMap.put("Action", "urn:listFileComments");
       String apiEndPoint =
               connectorProperties.getProperty("boxApiUrl") + "/2.0/files/"+connectorProperties.getProperty("fileId")+"/comments";
       RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listFileComments_mandatory.json"); 
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").length(), apiRestResponse.getBody().getJSONArray("entries").length() );
       Assert.assertEquals(esbRestResponse.getBody().get("total_count"), apiRestResponse.getBody().get("total_count"));

   }
   
   /**
    * Positive test case for listFileComments with optional parameters.
    * 
    */
   @Test(priority = 3, dependsOnMethods = { "listFileCommentsWithMandatoryParameters" }, description = "box {listFileComments} integration test with optional parameters.")
   public void listFileCommentsWithOptionalParameters() throws IOException, JSONException {
   
       esbRequestHeadersMap.put("Action", "urn:listFileComments");
       String apiEndPoint =
               connectorProperties.getProperty("boxApiUrl") + "/2.0/files/"+connectorProperties.getProperty("fileId")+"/comments?fields=message";
       RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listFileComments_optional.json"); 
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").length(), apiRestResponse.getBody().getJSONArray("entries").length() );
       Assert.assertEquals(esbRestResponse.getBody().get("total_count"), apiRestResponse.getBody().get("total_count"));      Assert.assertEquals(esbRestResponse.getBody().getJSONArray("entries").toString(), apiRestResponse.getBody().getJSONArray("entries").toString() );

   }
   
   /**
    * Negative test case for listFileComments.
    * 
    */
   @Test(priority = 3, dependsOnMethods = { "listFileCommentsWithOptionalParameters" }, description = "box {listFileComments} integration test negative case.")
   public void listFileCommentsNegativeCase() throws IOException, JSONException {
   
       esbRequestHeadersMap.put("Action", "urn:listFileComments");
       String apiEndPoint =
               connectorProperties.getProperty("boxApiUrl") + "/2.0/files/123/comments";
       RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listFileComments_negative.json"); 
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       Assert.assertEquals(esbRestResponse.getBody().get("code").toString(),apiRestResponse.getBody().get("code").toString());
       Assert.assertEquals(esbRestResponse.getBody().get("status").toString(),apiRestResponse.getBody().get("status").toString());
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404 );

   }
   
   /**
    * Positive test case for updateComment method with mandatory parameters.
    * 
    */
   @Test(priority = 3, dependsOnMethods = { "listFileCommentsWithOptionalParameters" }, description = "box {updateComment} integration test with mandatory parameters.")
   public void updateCommentWithMandatoryParameters() throws IOException, JSONException {
   
       esbRequestHeadersMap.put("Action", "urn:updateComment");
       String apiEndPoint =
               connectorProperties.getProperty("boxApiUrl") + "/2.0/comments/"+connectorProperties.getProperty("commentId");
       
       RestResponse<JSONObject> apiRestResponseBeforeUpdate = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateComment_mandatory.json");
       
       RestResponse<JSONObject> apiRestResponseAfterUpdate = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       Assert.assertTrue(!apiRestResponseBeforeUpdate.getBody().get("message").toString().equals(apiRestResponseAfterUpdate.getBody().get("message").toString()));

   }
   
   /**
    * Positive test case for updateComment method with optional parameters.
    * 
    */
   @Test(priority = 3, dependsOnMethods = { "updateCommentWithMandatoryParameters" }, description = "box {updateComment} integration test with optional parameters.")
   public void updateCommentWithOptionalParameters() throws IOException, JSONException {
   
       esbRequestHeadersMap.put("Action", "urn:updateComment");
       String apiEndPoint =
               connectorProperties.getProperty("boxApiUrl") + "/2.0/comments/"+connectorProperties.getProperty("commentId");
       
       RestResponse<JSONObject> apiRestResponseBeforeUpdate = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateComment_optional.json");
       
       RestResponse<JSONObject> apiRestResponseAfterUpdate = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       Assert.assertTrue(!apiRestResponseBeforeUpdate.getBody().get("message").toString().equals(apiRestResponseAfterUpdate.getBody().get("message").toString()));
       Assert.assertFalse(esbRestResponse.getBody().has("created_at"));
   }
   
   /**
    * Negative test case for updateComment.
    * 
    */
   @Test(priority = 3, dependsOnMethods = { "updateCommentWithOptionalParameters" }, description = "box {updateComment} integration test negative case.")
   public void updateCommentNegativeCase() throws IOException, JSONException {
   
       esbRequestHeadersMap.put("Action", "urn:updateComment");
       String apiEndPoint =
               connectorProperties.getProperty("boxApiUrl") + "/2.0/comments/1234";   
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateComment_negative.json");
       RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateComment_negative.json");
       Assert.assertEquals(esbRestResponse.getBody().get("code").toString(),apiRestResponse.getBody().get("code").toString());
       Assert.assertEquals(esbRestResponse.getBody().get("status").toString(),apiRestResponse.getBody().get("status").toString());
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404 );
       
   }
   
   /**
    * Positive test case for getComment method with mandatory parameters.
    * 
    */
   @Test(priority = 3, dependsOnMethods = { "updateCommentNegativeCase" }, description = "box {getComment} integration test with mandatory parameters.")
   public void testGetCommentWithMandatoryParameters() throws IOException, JSONException {
       esbRequestHeadersMap.put("Action", "urn:getComment");
       String apiEndPoint =
               connectorProperties.getProperty("boxApiUrl") + "/2.0/comments/"+connectorProperties.getProperty("commentId");        
       RestResponse<JSONObject> esbRestResponse =
               sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getComment_mandatory.json");
       
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		
       Assert.assertEquals(esbRestResponse.getBody().get("created_at"), apiRestResponse.getBody().get("created_at"));
       Assert.assertEquals(esbRestResponse.getBody().get("type"), apiRestResponse.getBody().get("type"));       
       Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
       Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));       
   }     
   
   /**
    * Positive test case for getComment method with optional parameters.
    * 
    */
   @Test(priority = 3, dependsOnMethods = { "testGetCommentWithMandatoryParameters" }, description = "box {getComment} integration test with optional parameters.")
   public void testGetCommentWithOptionalParameters() throws IOException, JSONException {
       esbRequestHeadersMap.put("Action", "urn:getComment");
       String apiEndPoint =
               connectorProperties.getProperty("boxApiUrl") + "/2.0/comments/"+connectorProperties.getProperty("commentId")+"?fields=created_at,id,message";        
       RestResponse<JSONObject> esbRestResponse =
               sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getComment_optional.json");
       
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		
       Assert.assertEquals(esbRestResponse.getBody().get("created_at"), apiRestResponse.getBody().get("created_at"));   
       Assert.assertEquals(esbRestResponse.getBody().get("id"), apiRestResponse.getBody().get("id"));
       Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));       
   }    

   /**
    * Negative test case for getComment method.
    * 
    */
   @Test(priority = 3, dependsOnMethods = { "testGetCommentWithOptionalParameters" }, description = "box {getComment} integration test with Negative test case.")
   public void testGetCommentWithNegativeCase() throws IOException, JSONException {
       esbRequestHeadersMap.put("Action", "urn:getComment");
       String apiEndPoint =
       		 connectorProperties.getProperty("boxApiUrl") + "/2.0/comments/XXX";         
       RestResponse<JSONObject> esbRestResponse =
               sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getComment_negative.json");
       
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
       Assert.assertEquals(esbRestResponse.getBody().get("code"), apiRestResponse.getBody().get("code"));
       Assert.assertEquals(esbRestResponse.getBody().get("help_url"), apiRestResponse.getBody().get("help_url"));       
       Assert.assertEquals(esbRestResponse.getBody().get("message"), apiRestResponse.getBody().get("message"));
       Assert.assertEquals(esbRestResponse.getBody().get("status"), apiRestResponse.getBody().get("status"));       
   } 
    
    /**
     * Positive test case for deleteComment method with mandatory parameters.
     * 
     */
    @Test(priority = 4, dependsOnMethods = { "testGetCommentWithNegativeCase" }, description = "box {deleteComment} integration test with mandatory parameters.")
    public void testDeleteCommentWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteComment");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/comments/"+connectorProperties.getProperty("commentId");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteComment_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Negative test case for deleteComment method.
     * 
     */
    @Test(priority = 4, dependsOnMethods = { "testDeleteCommentWithMandatoryParameters" }, description = "box {deleteComment} integration test with mandatory parameters.")
    public void testDeleteCommentWithNegativeCase() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteComment");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/files/invalid";
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteComment_negative.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for deleteFile method with mandatory parameters.
     * 
     */
    @Test(priority = 4, dependsOnMethods = { "testDeleteCommentWithNegativeCase" }, description = "box {deleteFile} integration test with mandatory parameters.")
    public void deleteFileWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteFile");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/files/"+connectorProperties.getProperty("fileId");
        
        RestResponse<JSONObject> apiRestResponseBeforeDelete = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteFile_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponseAfterDelete = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(apiRestResponseBeforeDelete.getBody().has("id"));
        Assert.assertEquals(apiRestResponseAfterDelete.getHttpStatusCode() , 404);
        Assert.assertEquals(apiRestResponseAfterDelete.getBody().get("code").toString(), "trashed");

    }
    
    /**
     * Positive test case for deleteFile method with optional parameters.
     * 
     */
    @Test(priority = 4, dependsOnMethods = { "deleteFileWithMandatoryParameters" }, description = "box {deleteFile} integration test with optional parameters.")
    public void deleteFileWithOptionalParameters() throws IOException, JSONException, InterruptedException {
        esbRequestHeadersMap.put("Action", "urn:deleteFile");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/files/"+connectorProperties.getProperty("copiedFileId");
        
        RestResponse<JSONObject> apiRestResponseBeforeDelete = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        // Retrieves the etag of the folder by direct call to be included in ESB call
        connectorProperties.put("ifMatch", apiRestResponseBeforeDelete.getBody().get("etag"));
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteFile_optional.json");

    	
        RestResponse<JSONObject> apiRestResponseAfterDelete = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertTrue(apiRestResponseBeforeDelete.getBody().has("id"));
        Assert.assertEquals(apiRestResponseAfterDelete.getHttpStatusCode() , 404);
        Assert.assertEquals(apiRestResponseAfterDelete.getBody().get("code").toString(), "trashed");

    }

    /**
     * Negative test case for deleteFile.
     * 
     */
    @Test(priority = 4, dependsOnMethods = { "deleteFileWithOptionalParameters" }, description = "box {deleteFile} integration test negative case.")
    public void testDeleteFileNegativeCase() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteFile");
        
    	Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteFile_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/files/1234";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("code").toString(),apiRestResponse.getBody().get("code").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("status").toString(),apiRestResponse.getBody().get("status").toString());
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404 );
    }
    
    /**
     * Positive test case for deleteFolder method with mandatory parameters.
     * 
     */
    @Test(priority = 4, dependsOnMethods = { "deleteFileWithOptionalParameters" }, description = "box {deleteFodler} integration test with mandatory parameters.")
    public void deleteFolderWithMandatoryParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteFolder");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/folders/"+connectorProperties.getProperty("folderId");
        
        RestResponse<JSONObject> apiRestResponseBeforeDelete = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteFolder_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponseAfterDelete = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponseBeforeDelete.getBody().get("id").toString(), connectorProperties.getProperty("folderId"));
        Assert.assertEquals(apiRestResponseAfterDelete.getHttpStatusCode() , 404);
        Assert.assertEquals(apiRestResponseAfterDelete.getBody().get("code").toString(), "trashed");

    }
    
    /**
     * Positive test case for deleteFolder method with optional parameters.
     * 
     */
    @Test(priority = 4, dependsOnMethods = { "deleteFolderWithMandatoryParameters" }, description = "box {deleteFodler} integration test with optional parameters.")
    public void deleteFolderWithOptionalParameters() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteFolder");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/folders/"+connectorProperties.getProperty("folderId2");
        
        RestResponse<JSONObject> apiRestResponseBeforeDelete = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        // Retrieves the etag of the folder by direct call to be included in ESB call
        connectorProperties.put("ifMatch", apiRestResponseBeforeDelete.getBody().get("etag"));
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteFolder_optional.json");
        
        RestResponse<JSONObject> apiRestResponseAfterDelete = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(apiRestResponseBeforeDelete.getBody().get("id").toString(), connectorProperties.getProperty("folderId2"));
        Assert.assertEquals(apiRestResponseAfterDelete.getHttpStatusCode() , 404);
        Assert.assertEquals(apiRestResponseAfterDelete.getBody().get("code").toString(), "trashed");

    }

    /**
     * Negative test case for deleteFolder method.
     * 
     */
    @Test(priority = 4, dependsOnMethods = {"deleteFolderWithOptionalParameters"}, description = "box {deleteFodler} integration test with negative case.")
    public void testDeleteFolderWithNegativeCase() throws IOException, JSONException, InterruptedException {
    
        esbRequestHeadersMap.put("Action", "urn:deleteFolder");

        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteFolder_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/folders/"+connectorProperties.getProperty("folderId2") + "?recursive=true";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap, "api_deleteFolder_negative.json");
      
        Assert.assertEquals(esbRestResponse.getHttpStatusCode() , 404);
        Assert.assertEquals(esbRestResponse.getBody().get("code").toString(), apiRestResponse.getBody().get("code").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("type").toString(), apiRestResponse.getBody().get("type").toString());
        
    }
    
    @AfterClass(alwaysRun = true)
    public void cleanUp() throws Exception {
        String apiEndPointFolder1 =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/folders/"+connectorProperties.getProperty("cleanupFolderId1");
        RestResponse<JSONObject> response1 = sendJsonRestRequest(apiEndPointFolder1, "DELETE", apiRequestHeadersMap);
        
        String apiEndPointFolder2 =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/folders/"+connectorProperties.getProperty("cleanupFolderId2");
        RestResponse<JSONObject> response2 = sendJsonRestRequest(apiEndPointFolder2, "DELETE", apiRequestHeadersMap);
        
        String apiEndPointFile =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/files/"+connectorProperties.getProperty("cleanupFileId");
        RestResponse<JSONObject> response3 = sendJsonRestRequest(apiEndPointFile, "DELETE", apiRequestHeadersMap);
    }
    
    /**
     * Method used to get source user account id.
     * 
     */
    private void listUsers() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:listUsers");
        String apiEndPoint =
                connectorProperties.getProperty("boxApiUrl") + "/2.0/users?fields=name,id";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray array=apiRestResponse.getBody().getJSONArray("entries");
        for(int i=0;i<array.length();i++){
        	JSONObject entry=array.getJSONObject(i);
        	if(entry.getString("name").equals(connectorProperties.getProperty("sourceUserName"))){
        		connectorProperties.put("sourceUserId", entry.getString("id"));
        		break;
        		
        	}
        }

    }
    
	    private String getFileHash(InputStream in) throws IOException, NoSuchAlgorithmException {
	        
	        MessageDigest md = MessageDigest.getInstance("SHA1");
	        
	        byte[] dataBytes = new byte[1024];
	        
	        int nread = 0;
	        
	        while ((nread = in.read(dataBytes)) != -1) {
	            md.update(dataBytes, 0, nread);
	        };
	        
	        byte[] mdbytes = md.digest();
	        
	        // convert the byte to hex format
	        StringBuffer sb = new StringBuffer("");
	        for (int i = 0; i < mdbytes.length; i++) {
	            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        return sb.toString();
	    }
    
}
