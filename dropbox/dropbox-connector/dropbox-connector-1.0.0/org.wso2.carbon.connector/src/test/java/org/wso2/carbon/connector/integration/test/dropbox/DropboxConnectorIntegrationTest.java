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

package org.wso2.carbon.connector.integration.test.dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
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


public class DropboxConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> headersMap = new HashMap<String, String>();
    
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("dropbox-connector-1.0.0");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.put("Accept-Charset", "UTF-8");
        apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");
        apiRequestHeadersMap.put("Authorization", "Bearer "+connectorProperties.getProperty("accessToken"));
        
    }

    /**
     * Positive test case for uploadFile
     * 
     * @throws NoSuchAlgorithmException
     */
    @Test(priority = 1, description = "dropbox {uploadFile} integration test positive case.")
    public void testUploadFile() throws IOException, JSONException, NoSuchAlgorithmException {
    
        headersMap.put("Action", "urn:uploadFile");
        headersMap.put("Content-Type", connectorProperties.getProperty("uploadContentType"));
       
        String requestString =
                proxyUrl + "?apiUrl=" + connectorProperties.getProperty("contentApiUrl") + "&accessToken="
                        + connectorProperties.getProperty("accessToken") + "&root="
                        + connectorProperties.getProperty("root") + "&path="
                        + connectorProperties.getProperty("fileName") + "&locale="
                        + connectorProperties.getProperty("locale") + "&overwrite=true&contentLength="
                        + connectorProperties.getProperty("contentLength");
        
        MultipartFormdataProcessor multipartProcessor = new MultipartFormdataProcessor(requestString, headersMap);
        File file = new File(pathToResourcesDirectory + connectorProperties.getProperty("uploadSourcePath"));
        multipartProcessor.addFiletoRequestBody(file);
        RestResponse<JSONObject> esbRestResponse=multipartProcessor.processAttachmentForJsonResponse();
        
        String apiRequestString =
                connectorProperties.getProperty("contentApiUrl") + "/1/files/"
                        + connectorProperties.getProperty("root") + "/"
                        + connectorProperties.getProperty("fileName");
        InputStream is = processForInputStream(apiRequestString, "GET", apiRequestHeadersMap,null,null);
        
        String downloadedFileHash = getFileHash(is);
        String originalFileHash = getFileHash(new FileInputStream(file));
        
        Assert.assertEquals(downloadedFileHash, originalFileHash);
        
    }
    
    /**
     * Positive test case for ChunkUploadWithCommitChunk
     * 
     * @throws NoSuchAlgorithmException
     */
    @Test(priority = 1, description = "dropbox {ChunkUploadWithCommitChunk} integration test positive case.")
    public void testChunkUploadWithCommitChunk() throws IOException, JSONException, NoSuchAlgorithmException {
    
        headersMap.put("Action", "urn:chunckUpload");
        headersMap.put("Content-Type", connectorProperties.getProperty("chunckUploadContentType"));
        
        
        File file = new File(pathToResourcesDirectory + connectorProperties.getProperty("chunkUploadSourcePath"));
        long contentLength = file.length();
        FileInputStream is = new FileInputStream(file);
        int bufferSize = Integer.parseInt(connectorProperties.getProperty("bufferSize"));
        byte[] bytesPortion = new byte[bufferSize];
        int byteNumber = 0;
        
        String uploadId = "";
        String offest = "";
        int attempts = 0;
        MultipartFormdataProcessor multipartProcessor;
        RestResponse<JSONObject> esbRestResponse;
        
        while (is.read(bytesPortion, 0, bufferSize) != -1) {
            attempts++;
            
            long bytesLeft = contentLength - byteNumber;
            
            if (bytesLeft < bufferSize) {
                
                // copy the bytesPortion array into a smaller array containing only the remaining bytes
                bytesPortion = Arrays.copyOf(bytesPortion, (int) bytesLeft);
                // This just makes it so it doesn't throw an IndexOutOfBounds exception on the next while
                // iteration. It shouldn't get past another iteration
                bufferSize = (int) bytesLeft;
                
            }
            String requestString;
            if (attempts == 1) {
                requestString =
                        proxyUrl + "?apiUrl=" + connectorProperties.getProperty("contentApiUrl")
                                + "&accessToken=" + connectorProperties.getProperty("accessToken");
                
            } else {
                requestString =
                        proxyUrl + "?apiUrl=" + connectorProperties.getProperty("contentApiUrl")
                                + "&accessToken=" + connectorProperties.getProperty("accessToken") + "&uploadId="
                                + uploadId + "&offset=" + offest;
            }
            
            multipartProcessor = new MultipartFormdataProcessor(requestString, headersMap);
            multipartProcessor.addChunckedFiletoRequestBody(bytesPortion);
            esbRestResponse = multipartProcessor.processAttachmentForJsonResponse();
            
            
            if (attempts == 1) {
                uploadId = esbRestResponse.getBody().getString("upload_id");
                offest = esbRestResponse.getBody().getString("offset");
                connectorProperties.put("uploadId", uploadId);
            }
            uploadId = esbRestResponse.getBody().getString("upload_id");
            offest = esbRestResponse.getBody().getString("offset");
            connectorProperties.put("uploadId", uploadId);
            
            byteNumber += bytesPortion.length;
        }
        
        // Run Chunk Commit method
        esbRequestHeadersMap.put("Action", "urn:commitChunk");
        RestResponse<JSONObject> commitChunkESBRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "commitChunk.json");
        
        String apiRequestString =
                connectorProperties.getProperty("contentApiUrl") + "/1/files/"
                        + connectorProperties.getProperty("root") + "/"
                        + connectorProperties.getProperty("chunckUploadDestinationPath");
        InputStream downloadedFile = processForInputStream(apiRequestString, "GET", apiRequestHeadersMap,null,null);
        
        String downloadedFileHash = getFileHash(downloadedFile);
        String originalFileHash = getFileHash(new FileInputStream(file));
        
        Assert.assertEquals(downloadedFileHash, originalFileHash);
        
    }
    
    /**
     * Positive test case for getMetadata method with mandatory parameters.
     * 
     */
    @Test(priority = 1, dependsOnMethods = {"testUploadFile"}, description = "dropbox {getMetadata} integration test with mandatory parameters.")
    public void testGetMetadataWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getMetadata");
        String apiEndPoint =
                connectorProperties.getProperty("dropboxApiUrl") + "/1/metadata/"+connectorProperties.getProperty("root") 
                		+ "/"+ connectorProperties.getProperty("fileName");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMetadata_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("icon"), apiRestResponse.getBody().get("icon"));
        Assert.assertEquals(esbRestResponse.getBody().get("thumb_exists"), apiRestResponse.getBody().get("thumb_exists"));
        Assert.assertEquals(esbRestResponse.getBody().get("revision"), apiRestResponse.getBody().get("revision"));
        connectorProperties.put("rev", esbRestResponse.getBody().get("rev").toString());
        
    }
    
    /**
     * Positive test case for getMetadata method with optional parameters.
     * 
     */
    @Test(priority = 1, dependsOnMethods = {"testGetMetadataWithMandatoryParameters"}, description = "dropbox {getMetadata} integration test with optional parameters.")
    public void testGetMetadataWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getMetadata");
        String apiEndPoint =
                connectorProperties.getProperty("dropboxApiUrl") + "/1/metadata/"+connectorProperties.getProperty("root") 
                		+ "/"+ connectorProperties.getProperty("fileName")+"?locale="+ connectorProperties.getProperty("locale");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMetadata_optional.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("icon"), apiRestResponse.getBody().get("icon"));
        Assert.assertEquals(esbRestResponse.getBody().get("thumb_exists"), apiRestResponse.getBody().get("thumb_exists"));
        Assert.assertEquals(esbRestResponse.getBody().get("revision"), apiRestResponse.getBody().get("revision"));
        
    }
    
    /**
     * Negative test case for getMetadata method.
     * 
     */
    @Test(priority = 1, dependsOnMethods = {"testGetMetadataWithOptionalParameters"},description = "dropbox {getMetadata} integration test negative case.")
    public void testGetMetadataWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getMetadata");
        String apiEndPoint =
                connectorProperties.getProperty("dropboxApiUrl") + "/1/metadata/invalid/"+ connectorProperties.getProperty("fileName");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getMetadata_negative.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
        
    }
    
    /**
     * Positive test case for getFile method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testGetMetadataWithNegativeCase"}, description = "dropbox {getFile} integration test with mandatory parameters.")
    public void testGetFileWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getFile");
        String apiEndPoint =
                connectorProperties.getProperty("contentApiUrl") + "/1/files/"+connectorProperties.getProperty("root") 
                		+ "/"+ connectorProperties.getProperty("fileName");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFile_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHeadersMap().get("x-dropbox-metadata"), apiRestResponse.getHeadersMap().get("x-dropbox-metadata"));
        
    }
    
    /**
     * Positive test case for getFile method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testGetFileWithMandatoryParameters"}, description = "dropbox {getFile} integration test with optional parameters.")
    public void testGetFileWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getFile");
        String apiEndPoint =
                connectorProperties.getProperty("contentApiUrl") + "/1/files/"+connectorProperties.getProperty("root") 
                		+ "/"+ connectorProperties.getProperty("fileName")+"?rev="+connectorProperties.getProperty("rev");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFile_optional.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHeadersMap().get("x-dropbox-metadata"), apiRestResponse.getHeadersMap().get("x-dropbox-metadata"));
        
    }
    
    /**
     * Negative test case for getFile method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testGetFileWithOptionalParameters"}, description = "dropbox {getFile} integration test negative case.")
    public void testGetFileWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getFile");
        String apiEndPoint =
                connectorProperties.getProperty("contentApiUrl") + "/1/files/"+connectorProperties.getProperty("root") 
                		+ "/"+ connectorProperties.getProperty("fileName")+"?rev=a";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFile_negative.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
        
    }
    
    /**
     * Positive test case for search method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testGetFileWithNegativeCase"}, description = "dropbox {search} integration test with mandatory parameters.")
    public void testSearchWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:search");
        String apiEndPoint =
                connectorProperties.getProperty("dropboxApiUrl") + "/1/search/"+connectorProperties.getProperty("root") 
                		+ "?query="+connectorProperties.getProperty("fileName");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_search_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray esbResponseJsonArray = new JSONArray(esbRestResponse.getBody().get("output").toString());
        JSONArray apiResponseJsonArray = new JSONArray(apiRestResponse.getBody().get("output").toString());
        Assert.assertEquals(esbResponseJsonArray.getJSONObject(0).get("path").toString(), apiResponseJsonArray.getJSONObject(0).get("path").toString());
        Assert.assertEquals(esbResponseJsonArray.getJSONObject(0).get("rev").toString(), apiResponseJsonArray.getJSONObject(0).get("rev").toString());
        
    }
    
    /**
     * Positive test case for search method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testSearchWithMandatoryParameters"}, description = "dropbox {search} integration test with optional parameters.")
    public void testSearchWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:search");
        String apiEndPoint =
                connectorProperties.getProperty("dropboxApiUrl") + "/1/search/"+connectorProperties.getProperty("root") 
                		+ "?query="+connectorProperties.getProperty("fileName")+"&file_limit=1";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_search_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        JSONArray esbResponseJsonArray = new JSONArray(esbRestResponse.getBody().get("output").toString());
        JSONArray apiResponseJsonArray = new JSONArray(apiRestResponse.getBody().get("output").toString());
        Assert.assertEquals(esbResponseJsonArray.getJSONObject(0).get("path").toString(), apiResponseJsonArray.getJSONObject(0).get("path").toString());
        Assert.assertEquals(esbResponseJsonArray.getJSONObject(0).get("rev").toString(), apiResponseJsonArray.getJSONObject(0).get("rev").toString());
        
    }
    
    /**
     * search method negative case.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, groups = { "wso2.esb" },dependsOnMethods = {"testSearchWithOptionalParameters"}, description = "dropbox {search} integration test negative case.")
    public void testSearchWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:search");
        String apiEndPoint =
                connectorProperties.getProperty("dropboxApiUrl") + "/1/search/"+connectorProperties.getProperty("root") 
                		+ "?query="+connectorProperties.getProperty("fileName")+"&file_limit=a";
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_search_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error").toString());
        
    }
    
    /**
     * Positive test case for getRevisionMetadata method with mandatory parameters.
     * 
     */
    @Test(priority = 1, dependsOnMethods = {"testSearchWithNegativeCase"}, description = "dropbox {getRevisionMetadata} integration test with mandatory parameters.")
    public void testGetRevisionMetadataWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getRevisionMetadata");		
        String apiEndPoint =
                connectorProperties.getProperty("dropboxApiUrl") + "/1/revisions?root="+connectorProperties.getProperty("root") 
                		+ "&path="+ connectorProperties.getProperty("fileName");
        				
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRevisionMetadata_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbArray = new JSONArray(esbRestResponse.getBody().get("output").toString());
        JSONArray apiArray = new JSONArray(apiRestResponse.getBody().get("output").toString());
        Assert.assertEquals(esbArray.getJSONObject(0).get("revision").toString(), apiArray.getJSONObject(0).get("revision").toString());
        Assert.assertEquals(esbArray.getJSONObject(0).get("modified").toString(), apiArray.getJSONObject(0).get("modified").toString());
        Assert.assertEquals(esbArray.getJSONObject(0).get("mime_type").toString(), apiArray.getJSONObject(0).get("mime_type").toString());
    }	
	
	/**
     * Negative test case for getRevisionMetadata method.
     * 
     */
    @Test(priority = 1, dependsOnMethods = {"testGetRevisionMetadataWithOptionalParameters"}, description = "dropbox {getRevisionMetadata} integration test with negative case.")
    public void testGetRevisionMetadataWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getRevisionMetadata");		
        String apiEndPoint =
                connectorProperties.getProperty("dropboxApiUrl") + "/1/revisions?root="+connectorProperties.getProperty("root") + "&path=invalid";
        				
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRevisionMetadata_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error")); 
    }
	
    /**
     * Positive test case for getRevisionMetadata method with optional parameters.
     * 
     */
    @Test(priority = 1, dependsOnMethods = {"testGetRevisionMetadataWithMandatoryParameters"}, description = "dropbox {getRevisionMetadata} integration test with optional parameters.")
    public void testGetRevisionMetadataWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:getRevisionMetadata");		
        String apiEndPoint =
                connectorProperties.getProperty("dropboxApiUrl") + "/1/revisions?root="+connectorProperties.getProperty("root") 
                		+ "&path="+ connectorProperties.getProperty("fileName")+ "&locale=fr-FR";
        				
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRevisionMetadata_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        JSONArray esbArray = new JSONArray(esbRestResponse.getBody().get("output").toString());
        JSONArray apiArray = new JSONArray(apiRestResponse.getBody().get("output").toString());
        Assert.assertEquals(esbArray.getJSONObject(0).get("revision").toString(), apiArray.getJSONObject(0).get("revision").toString());
        Assert.assertEquals(esbArray.getJSONObject(0).get("modified").toString(), apiArray.getJSONObject(0).get("modified").toString());
        Assert.assertEquals(esbArray.getJSONObject(0).get("mime_type").toString(), apiArray.getJSONObject(0).get("mime_type").toString());
    }
    
    /**
     * Positive test case for restoreFile method with mandatory parameters.
     * 
     */
    @Test(priority = 1, dependsOnMethods = {"testGetRevisionMetadataWithNegativeCase"}, description = "dropbox {restoreFile} integration test with mandatory parameters.")
    public void testRestoreFileWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:restoreFile");
        String apiEndPoint =
                connectorProperties.getProperty("dropboxApiUrl") + "/1/restore/"+connectorProperties.getProperty("root") 
                		+ "/"+connectorProperties.getProperty("fileName")+"?rev="+ connectorProperties.getProperty("rev");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_restoreFile_mandatory.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("rev"), apiRestResponse.getBody().get("rev"));
        Assert.assertEquals(esbRestResponse.getBody().get("thumb_exists"), apiRestResponse.getBody().get("thumb_exists"));
        Assert.assertEquals(esbRestResponse.getBody().get("revision"), apiRestResponse.getBody().get("revision"));
        
    }
    
    /**
     * Positive test case for restoreFile method with optional parameters.
     * 
     */
    @Test(priority = 1, dependsOnMethods = {"testRestoreFileWithMandatoryParameters"}, description = "dropbox {restoreFile} integration test with optional parameters.")
    public void testRestoreFileWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:restoreFile");
        String apiEndPoint =
        		connectorProperties.getProperty("dropboxApiUrl") + "/1/restore/"+connectorProperties.getProperty("root") 
        		+ "/"+ connectorProperties.getProperty("fileName")+"?rev="+ connectorProperties.getProperty("rev");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_restoreFile_optional.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("rev"), apiRestResponse.getBody().get("rev"));
        Assert.assertEquals(esbRestResponse.getBody().get("thumb_exists"), apiRestResponse.getBody().get("thumb_exists"));
        Assert.assertEquals(esbRestResponse.getBody().get("revision"), apiRestResponse.getBody().get("revision"));
        
    }
    
    /**
     * Negative test case for restoreFile method.
     * 
     */
    @Test(priority = 1, dependsOnMethods = {"testRestoreFileWithOptionalParameters"}, description = "dropbox {restoreFile} integration test negative case.")
    public void testRestoreFileWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:restoreFile");
        String apiEndPoint =
                connectorProperties.getProperty("dropboxApiUrl") + "/1/restore/"+ connectorProperties.getProperty("root")+"/invalid"+"?rev="+ connectorProperties.getProperty("rev");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_restoreFile_negative.json");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
        
    }
    
    
    /**
     * Positive test case for share method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testRestoreFileWithNegativeCase"}, description = "dropbox {share} integration test with mandatory parameters.")
    public void testShareWithMandatoryParameters() throws IOException, JSONException {
   
        esbRequestHeadersMap.put("Action", "urn:share");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_share_mandatory.json");
        
        //Direct API Call - Retrieves share data 
        String apiEndPoint =
                connectorProperties.getProperty("dropboxApiUrl") + "/1/media/" + connectorProperties.getProperty("root") + "/" + connectorProperties.getProperty("fileName");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap , "api_share_mandatory.json");
        
        Assert.assertEquals(esbRestResponse.getBody().get("url").toString(), apiRestResponse.getBody().get("url").toString());

    }
    

    /**
     * Positive test case for share method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testShareWithMandatoryParameters"}, description = "dropbox {share} integration test with optional parameters.")
    public void testShareWithoptionalParameters() throws IOException, JSONException {
   
        esbRequestHeadersMap.put("Action", "urn:share");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_share_optional.json");
        
      //Direct API Call - Retrieves share data
        String apiEndPoint =
                connectorProperties.getProperty("dropboxApiUrl") + "/1/media/" + connectorProperties.getProperty("root") + "/" + connectorProperties.getProperty("fileName");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap , "api_share_optional.json");
        
        Assert.assertEquals(esbRestResponse.getBody().get("url").toString(), apiRestResponse.getBody().get("url").toString());

    }
    

    /**
     * Negative test case for share method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = {"testShareWithoptionalParameters"}, description = "dropbox {share} integration test with negative parameters.")
    public void testShareWithNegativeCase() throws IOException, JSONException {
   
        esbRequestHeadersMap.put("Action", "urn:share");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_share_negative.json");
        
      //Direct API Call - Retrieves share data
        String apiEndPoint =
                connectorProperties.getProperty("dropboxApiUrl") + "/1/media/" + connectorProperties.getProperty("root") + "/" + "----------";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap , "api_share_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error").toString());

    }
    
    
    /**
     * Positive test case for createFolder method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 2, dependsOnMethods = {"testShareWithNegativeCase"}, description = "dropbox {createFolder} integration test with mandatory parameters.")
    public void testCreateFolderWithMandatoryParameters() throws IOException, JSONException {
   
        esbRequestHeadersMap.put("Action", "urn:createFolder");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createFolder_mandatory.json");
        
        //Direct API Call - Retrieves meta data of the created folder to compare with
        String apiEndPoint =
                connectorProperties.getProperty("dropboxApiUrl") + "/1/metadata/" + connectorProperties.getProperty("root") + "/" + connectorProperties.getProperty("folderName1");
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertTrue((Boolean)(esbRestResponse.getBody().get("is_dir")));
        Assert.assertEquals(esbRestResponse.getBody().get("modified").toString(), apiRestResponse.getBody().get("modified").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("icon").toString(), "folder");
        
    }
    
    /**
     * Positive test case for createFolder method with optional parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testCreateFolderWithMandatoryParameters"}, description = "dropbox {createFolder} integration test with optional parameters.")
    public void testCreateFolderWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createFolder");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createFolder_optional.json");
        
        //Direct API Call - Retrieves meta data of the created folder to compare with
        String apiEndPoint =
                connectorProperties.getProperty("dropboxApiUrl") + "/1/metadata/" + connectorProperties.getProperty("root") + "/" + connectorProperties.getProperty("folderName2") + "?locale=fr-FR";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertTrue((Boolean)(esbRestResponse.getBody().get("is_dir")));
        Assert.assertEquals(esbRestResponse.getBody().get("modified").toString(), apiRestResponse.getBody().get("modified").toString());
        Assert.assertEquals(esbRestResponse.getBody().get("icon").toString(), "folder");
        Assert.assertEquals(esbRestResponse.getBody().get("size").toString(), apiRestResponse.getBody().get("size").toString());        
    }
    
    /**
     * Negative test case for createFolder method.
     * Negative scenario is checked by trying to create already existing folder created by testCreateFolderWithMandatoryParameters method
     */
    @Test(priority = 2, dependsOnMethods = {"testCreateFolderWithOptionalParameters"}, description = "dropbox {createFolder} integration test with negative case.")
    public void testCreateFolderWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:createFolder");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createFolder_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("dropboxApiUrl") + "/1/fileops/create_folder";
        
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createFolder_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 403);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error").toString());
    }
        /**
     * Positive test case for move method with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 2, dependsOnMethods = {"testCreateFolderWithNegativeCase"}, description = "dropbox {move} integration test with mandatory parameters.")
    public void testMoveWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:move");
        String apiEndPoint =
                connectorProperties.getProperty("contentApiUrl") + "/1/files/"+connectorProperties.getProperty("root") 
                		+ "/"+ connectorProperties.getProperty("fileName");
        RestResponse<JSONObject> firstApiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_move_mandatory.json");
        RestResponse<JSONObject> secondApiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(!firstApiRestResponse.getBody().has("error") && secondApiRestResponse.getBody().has("error"));
        connectorProperties.put("filePath", esbRestResponse.getBody().get("path").toString());
    }
    
    /**
     * Positive test case for move method with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 2, dependsOnMethods = {"testMoveWithMandatoryParameters"}, description = "dropbox {move} integration test with optional parameters.")
    public void testMoveWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:move");
        String apiEndPoint =
                connectorProperties.getProperty("contentApiUrl") + "/1/files/"+connectorProperties.getProperty("root") 
                		+ "/"+ connectorProperties.getProperty("filePath");
        RestResponse<JSONObject> firstApiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_move_optional.json");
        RestResponse<JSONObject> secondApiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(!firstApiRestResponse.getBody().has("error") && secondApiRestResponse.getBody().has("error"));
        connectorProperties.put("filePath", esbRestResponse.getBody().get("path").toString());
    }
    
    /**
     * Negative test case for move method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 2, dependsOnMethods = {"testMoveWithOptionalParameters"}, description = "dropbox {move} integration test negative case.")
    public void testMoveNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:move");
        String apiEndPoint =
                connectorProperties.getProperty("contentApiUrl") + "/1/files/"+connectorProperties.getProperty("root") 
                		+ "/"+ connectorProperties.getProperty("filePath");
        RestResponse<JSONObject> firstApiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_move_optional.json");
        RestResponse<JSONObject> secondApiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertTrue(!firstApiRestResponse.getBody().has("error") && !secondApiRestResponse.getBody().has("error"));
    }
    
    
    
	  /**
	    * Positive test case for copy method with mandatory parameters.
	    * 
	    * @throws JSONException
	    * @throws IOException
	    */
	   @Test(priority = 2, dependsOnMethods = { "testMoveNegativeCase" }, description = "dropbox {copy} integration test with mandatory parameters.")
	   public void testCopyWithMandatoryParameters() throws IOException, JSONException {
	  
	       esbRequestHeadersMap.put("Action", "urn:copy");
	       
	       RestResponse<JSONObject> esbRestResponse =
	               sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_copy_mandatory.json");
	       
	       // Direct API Call - Retrieves meta data of the copied file or folder to compare with
	       String apiEndPoint =
	               connectorProperties.getProperty("dropboxApiUrl") + "/1/metadata/" + connectorProperties.getProperty("root") + "/" + connectorProperties.getProperty("folderName1") + "/" + connectorProperties.getProperty("fileName");
	       
	       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
	       
	       Assert.assertEquals(esbRestResponse.getBody().get("is_dir").toString() , apiRestResponse.getBody().get("is_dir").toString());
	       Assert.assertEquals(esbRestResponse.getBody().get("modified").toString(), apiRestResponse.getBody().get("modified").toString());
	       Assert.assertEquals(esbRestResponse.getBody().get("path").toString(), apiRestResponse.getBody().get("path").toString());
	       
	       //Deletes the created file or folder using a direct call to API
	       apiEndPoint =
	               connectorProperties.getProperty("dropboxApiUrl") + "/1/fileops/delete?root=" + connectorProperties.getProperty("root") + "&path=" + connectorProperties.getProperty("toPath");
	       sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
	   }
	   
	   
	   /**
	    * Positive test case for copy method with optional parameters.
	    * 
	    * @throws JSONException
	    * @throws IOException
	    */
	   @Test(priority = 2, dependsOnMethods = { "testCopyWithMandatoryParameters" }, description = "dropbox {copy} integration test with optional parameters.")
	   public void testCopyWithOptionalParameters() throws IOException, JSONException {
	  
	       esbRequestHeadersMap.put("Action", "urn:copy");
	       
	       RestResponse<JSONObject> esbRestResponse =
	               sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_copy_optional.json");
	       
	       // Direct API Call - Retrieves meta data of the copied file or folder to compare with
	       String apiEndPoint =
	               connectorProperties.getProperty("dropboxApiUrl") + "/1/metadata/" + connectorProperties.getProperty("root") + "/" + connectorProperties.getProperty("folderName2") + "/" + connectorProperties.getProperty("fileName") + "?locale=fr-FR";
	       
	       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
	       
	       Assert.assertEquals(esbRestResponse.getBody().get("is_dir").toString() , apiRestResponse.getBody().get("is_dir").toString());
	       Assert.assertEquals(esbRestResponse.getBody().get("modified").toString(), apiRestResponse.getBody().get("modified").toString());
	       Assert.assertEquals(esbRestResponse.getBody().get("size").toString(), apiRestResponse.getBody().get("size").toString());
	       
	   }
	   
	   /**
	    * Negative test case for copy method.
	    * Negative scenario is checked by trying to copy a file or folder to a place which tries to override an existing file or folder 
	    */
	    @Test(priority = 2, dependsOnMethods = { "testCopyWithOptionalParameters" }, description = "dropbox {copy} integration test with negative case.")
	    public void testCopyWithNegativeCase() throws IOException, JSONException {
	   
	       esbRequestHeadersMap.put("Action", "urn:copy");
	       
	       RestResponse<JSONObject> esbRestResponse =
	               sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_copy_negative.json");
	       
	       String apiEndPoint =
	               connectorProperties.getProperty("dropboxApiUrl") + "/1/fileops/copy";
	       
	       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap , "api_copy_negative.json");
	       
	       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 403);
	       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
	       Assert.assertEquals(esbRestResponse.getBody().get("error").toString(), apiRestResponse.getBody().get("error").toString());
	    }
    
    /**
     * 
     * Positive test case for delete method with mandatory parameters.
     * 
     */
    @Test(priority = 2, dependsOnMethods = {"testCopyWithNegativeCase"}, description = "dropbox {delete} integration test with mandatory parameters.")
    public void testDeleteWithMandatoryParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:delete");		
        String apiEndPoint =
                connectorProperties.getProperty("dropboxApiUrl") + "/1/files/"+connectorProperties.getProperty("root") 
                		+ "/"+connectorProperties.getProperty("folderName1");
        				
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_delete_mandatory.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
		Assert.assertEquals(esbRestResponse.getBody().get("is_deleted").toString(), "true");
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);   
    }	
	
    /**
     * Positive test case for delete method with optional parameters.
     * 
     */
    @Test(priority = 2, dependsOnMethods = {"testDeleteWithMandatoryParameters"}, description = "dropbox {delete} integration test with optional parameters.")
    public void testDeleteWithOptionalParameters() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:delete");		
        String apiEndPoint =
                connectorProperties.getProperty("dropboxApiUrl") + "/1/files/"+connectorProperties.getProperty("root") 
                		+ "/"+connectorProperties.getProperty("folderName2");
        				
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_delete_optional.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
		Assert.assertEquals(esbRestResponse.getBody().get("is_deleted").toString(), "true");
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
	
	/**
     * Negative test case for delete method.
     * 
     */
    @Test(priority = 2, dependsOnMethods = {"testDeleteWithOptionalParameters"}, description = "dropbox {delete} integration test with negative case.")
    public void testDeleteWithNegativeCase() throws IOException, JSONException {
    
        esbRequestHeadersMap.put("Action", "urn:delete");		
        String apiEndPoint =
                connectorProperties.getProperty("dropboxApiUrl") + "/1/files/"+connectorProperties.getProperty("root") 
                		+ "/InvalidPath";
        				
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_delete_negative.json");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    @AfterClass(alwaysRun = true)
    public void cleanUp() throws Exception {
        String apiEndPoint =
                connectorProperties.getProperty("dropboxApiUrl") + "/1/fileops/delete";
        RestResponse<JSONObject> response = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_cleanup.json");
	connectorProperties.put("fileName", connectorProperties.getProperty("chunckUploadDestinationPath"));
	response = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_cleanup.json");
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
