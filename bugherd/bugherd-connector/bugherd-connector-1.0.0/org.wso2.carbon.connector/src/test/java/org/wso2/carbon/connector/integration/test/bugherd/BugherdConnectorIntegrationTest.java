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

package org.wso2.carbon.connector.integration.test.bugherd;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class BugherdConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private Long timout;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        
        init("bugherd-connector-1.0.0");
        
        String authorizationString = connectorProperties.getProperty("apiKey") + ":x";
        String authorizationToken = new String(Base64.encodeBase64(authorizationString.getBytes()));
        
        esbRequestHeadersMap.put("Content-Type", "application/json");
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("Authorization", "Basic " + authorizationToken);
        
        timout = Long.parseLong(connectorProperties.getProperty("timeOut"));
        
        // timeout cannot be less than 0
        if (timout < 0) {
            timout = 0L;
        }
    }
    
    /**
     * Positive test case for listProjects method with mandatory parameters.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Bugherd {listProjects} integration test with optional parameters.")
    public void testListProjectsWithMandatoryParameters() throws IOException, JSONException, NumberFormatException,
            InterruptedException {
        
        Thread.sleep(timout);
        esbRequestHeadersMap.put("Action", "urn:listProjects");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjects_mandatory.json");
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/api_v2/projects.json";
        Thread.sleep(timout);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("projects").length(), apiRestResponse.getBody()
                .getJSONArray("projects").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").getInt("count"), apiRestResponse.getBody()
                .getJSONObject("meta").getInt("count"));
    }
    
    /**
     * Positive test case for listProjects method with optional parameters.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Bugherd {listProjects} integration test with optional parameters.")
    public void testListProjectsWithOptionalParameters() throws IOException, JSONException, NumberFormatException,
            InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:listProjects");
        Thread.sleep(timout);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjects_optional.json");
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/api_v2/projects/active.json";
        Thread.sleep(timout);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("projects").length(), apiRestResponse.getBody()
                .getJSONArray("projects").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").getInt("count"), apiRestResponse.getBody()
                .getJSONObject("meta").getInt("count"));
    }
    
    /**
     * Positive test case for createProjectTask method with mandatory parameters.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "bugherd {createProjectTask} integration test with mandatory parameters.")
    public void testCreateProjectTaskWithMandatoryParameters() throws NumberFormatException, InterruptedException,
            IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createProjectTask");
        Thread.sleep(timout);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProjectTask_mandatory.json");
        
        String taskId = esbRestResponse.getBody().getJSONObject("task").get("id").toString();
        connectorProperties.setProperty("taskId", taskId);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api_v2/projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/" + taskId + ".json";
        Thread.sleep(timout);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").get("project_id").toString(),
                apiRestResponse.getBody().getJSONObject("task").get("project_id").toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").get("created_at").toString(),
                apiRestResponse.getBody().getJSONObject("task").get("created_at").toString());
        Assert.assertEquals(taskId.toString(), apiRestResponse.getBody().getJSONObject("task").get("id").toString());
        
    }
    
    /**
     * Positive test case for createProjectTask method with optional parameters.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "bugherd {createProjectTask} integration test with optional parameters.")
    public void testCreateProjectTaskWithOptionalParameters() throws NumberFormatException, InterruptedException,
            IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createProjectTask");
        
        Thread.sleep(timout);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProjectTask_optional.json");
        String taskId = esbRestResponse.getBody().getJSONObject("task").get("id").toString();
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api_v2/projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/" + taskId + ".json";
        Thread.sleep(timout);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").get("priority").toString(), apiRestResponse
                .getBody().getJSONObject("task").get("priority").toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").get("status").toString(), apiRestResponse
                .getBody().getJSONObject("task").get("status").toString());
        Assert.assertEquals(taskId, apiRestResponse.getBody().getJSONObject("task").get("id").toString());
    }
    
    /**
     * Negative test case for createProjectTask method.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "bugherd {createProjectTask} integration test with negative case.")
    public void testCreateProjectTaskWithNegativeCase() throws NumberFormatException, InterruptedException,
            IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:createProjectTask");
        Thread.sleep(timout);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProjectTask_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api_v2/projects/INVALID/tasks.json";
        Thread.sleep(timout);
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createProjectTask_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
    }
    
    /**
     * Positive test case for updateProjectTask method with mandatory parameters.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateProjectTaskWithMandatoryParameters" }, description = "bugherd {updateProjectTask} integration test with mandatory parameters.")
    public void testUpdateProjectTaskWithMandatoryParameters() throws NumberFormatException, InterruptedException,
            IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateProjectTask");
        Thread.sleep(timout);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateProjectTask_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api_v2/projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/"
                        + connectorProperties.getProperty("taskId") + ".json";
        Thread.sleep(timout);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").get("project_id").toString(),
                apiRestResponse.getBody().getJSONObject("task").get("project_id").toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").get("created_at").toString(),
                apiRestResponse.getBody().getJSONObject("task").get("created_at").toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").get("local_task_id").toString(),
                apiRestResponse.getBody().getJSONObject("task").get("local_task_id").toString());
    }
    
    /**
     * Positive test case for updateProjectTask method with optional parameters.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateProjectTaskWithMandatoryParameters" }, description = "bugherd {updateProjectTask} integration test with optional parameters.")
    public void testUpdateProjectTaskWithOptionalParameters() throws NumberFormatException, InterruptedException,
            IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateProjectTask");
        Thread.sleep(timout);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateProjectTask_optional.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api_v2/projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/"
                        + connectorProperties.getProperty("taskId") + ".json";
        Thread.sleep(timout);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").get("project_id").toString(),
                apiRestResponse.getBody().getJSONObject("task").get("project_id").toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").get("status").toString(), apiRestResponse
                .getBody().getJSONObject("task").get("status").toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").get("priority").toString(), apiRestResponse
                .getBody().getJSONObject("task").get("priority").toString());
    }
    
    /**
     * Negative test case for updateProjectTask method.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(description = "bugherd {updateProjectTask} integration test with negative case.")
    public void testUpdateProjectTaskWithNegativeParameters() throws NumberFormatException, InterruptedException,
            IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:updateProjectTask");
        Thread.sleep(timout);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateProjectTask_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api_v2/projects/INVALID/tasks.json";
        Thread.sleep(timout);
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_updateProjectTask_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
    }
    
    /**
     * Positive test case for listProjectTasks method with mandatory parameters.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectTaskWithMandatoryParameters" }, description = "bugherd {listProjectTasks} integration test with mandatory parameters.")
    public void testListProjectTasksWithMandatoryParameters() throws NumberFormatException, InterruptedException,
            IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listProjectTasks");
        Thread.sleep(timout);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjectTasks_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api_v2/projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks.json";
        Thread.sleep(timout);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tasks").getJSONObject(0).get("id").toString(),
                apiRestResponse.getBody().getJSONArray("tasks").getJSONObject(0).get("id").toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("tasks").getJSONObject(0).get("created_at")
                .toString(), apiRestResponse.getBody().getJSONArray("tasks").getJSONObject(0).get("created_at")
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").get("count").toString(), apiRestResponse
                .getBody().getJSONObject("meta").get("count").toString());
    }
    
    /**
     * Negative test case for listProjectTasks method.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "bugherd {listProjectTasks} integration test with negative case.")
    public void testListProjectTasksWithNegativeCase() throws NumberFormatException, InterruptedException, IOException,
            JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:listProjectTasks");
        Thread.sleep(timout);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjectTasks_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/api_v2/projects/INVALID/tasks.json";
        Thread.sleep(timout);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
    }
    
    /**
     * Positive test case for addTaskComment method with mandatory parameters.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectTaskWithMandatoryParameters" }, description = "bugherd {addTaskComment} integration test with mandatory parameters.")
    public void testAddTaskCommentWithMandatoryParameters() throws NumberFormatException, InterruptedException,
            IOException, JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:addTaskComment");
        Thread.sleep(timout);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addTaskComment_mandatory.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api_v2/projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/"
                        + connectorProperties.getProperty("taskId") + "/comments.json";
        Thread.sleep(timout);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        int listLength = apiRestResponse.getBody().getJSONArray("comments").length();
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("comments").getJSONObject(listLength - 1).getString(
                "id"), esbRestResponse.getBody().getJSONObject("comment").getString("id"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("comments").getJSONObject(listLength - 1).getString(
                "created_at"), esbRestResponse.getBody().getJSONObject("comment").getString("created_at"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("comments").getJSONObject(listLength - 1).getString(
                "text"), esbRestResponse.getBody().getJSONObject("comment").getString("text"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("comments").getJSONObject(listLength - 1).getString(
                "user_id"), esbRestResponse.getBody().getJSONObject("comment").getJSONObject("user").getString("id"));
    }
    
    /**
     * Negative test case for addTaskComment method with mandatory parameters.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "bugherd {addTaskComment} integration test with negative case.")
    public void testAddTaskCommentWithNegativeCase() throws NumberFormatException, InterruptedException, IOException,
            JSONException {
        
        esbRequestHeadersMap.put("Action", "urn:addTaskComment");
        Thread.sleep(timout);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_addTaskComment_negative.json");
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/api_v2/projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/INVALID/comments.json";
        Thread.sleep(timout);
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_addTaskComment_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
    }
    
    /**
     * Positive test case for listTaskComments method with optional parameters.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = { "testAddTaskCommentWithMandatoryParameters" }, description = "Bugherd {listTaskComments} integration test with optional parameters.")
    public void testListTaskCommentsWithMandatoryParameters() throws IOException, JSONException, XMLStreamException,
            InterruptedException {
        
        Thread.sleep(timout);
        esbRequestHeadersMap.put("Action", "urn:listTaskComments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTaskComments_mandatory.json");
        
        Thread.sleep(timout);
        String apiEndpoint =
                connectorProperties.getProperty("apiUrl") + "/api_v2/projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/"
                        + connectorProperties.getProperty("taskId") + "/comments.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("comments").length(), apiRestResponse.getBody()
                .getJSONArray("comments").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").getInt("count"), apiRestResponse.getBody()
                .getJSONObject("meta").getInt("count"));
        
    }
    
    /**
     * Positive test case for listTaskComments method with optional parameters.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = { "testAddTaskCommentWithMandatoryParameters" }, description = "Bugherd {listTaskComments} integration test with optional parameters.")
    public void testListTaskCommentsWithOptionalParameters() throws IOException, JSONException, XMLStreamException,
            InterruptedException {
        
        Thread.sleep(timout);
        esbRequestHeadersMap.put("Action", "urn:listTaskComments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTaskComments_optional.json");
        
        String apiEndpoint =
                connectorProperties.getProperty("apiUrl") + "/api_v2/projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/"
                        + connectorProperties.getProperty("taskId") + "/comments.json?page="
                        + connectorProperties.getProperty("page");
        Thread.sleep(timout);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("comments").length(), apiRestResponse.getBody()
                .getJSONArray("comments").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").getInt("count"), apiRestResponse.getBody()
                .getJSONObject("meta").getInt("count"));
    }
    
    /**
     * Negative test case for listTaskComments method.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Bugherd {listTaskComments} integration test with negative case.")
    public void testListTaskCommentsWithNegativeCase() throws IOException, JSONException, NumberFormatException,
            InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:listTaskComments");
        Thread.sleep(timout);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTaskComments_negative.json");
        
        String apiEndpoint =
                connectorProperties.getProperty("apiUrl") + "/api_v2/projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/INVALID/comments.json";
        Thread.sleep(timout);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
    }
    
    /**
     * Positive test case for uploadTaskAttachment method with mandatory parameters.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectTaskWithMandatoryParameters" }, description = "Bugherd {uploadTaskAttachment} integration test with mandatory parameters.")
    public void testUploadTaskAttachmentWithMandatoryParameters() throws IOException, JSONException,
            NumberFormatException, InterruptedException {
        
        Thread.sleep(timout);
        esbRequestHeadersMap.put("Action", "urn:uploadTaskAttachment");
        
        final Map<String, String> attachmentHeadersMap = new HashMap<String, String>();
        attachmentHeadersMap.putAll(esbRequestHeadersMap);
        attachmentHeadersMap.put("Content-Type", connectorProperties.getProperty("fileContentType"));
        
        String esbEndpPoint =
                proxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&apiKey="
                        + connectorProperties.getProperty("apiKey") + "&projectId="
                        + connectorProperties.getProperty("projectId") + "&taskId="
                        + connectorProperties.getProperty("taskId");
        
        final MultipartFormdataProcessor fileRequestProcessor =
                new MultipartFormdataProcessor(esbEndpPoint, attachmentHeadersMap);
        File file = new File(pathToResourcesDirectory + connectorProperties.getProperty("fileMandatory"));
        fileRequestProcessor.addFiletoRequestBody(file);
        RestResponse<JSONObject> esbRestResponse = fileRequestProcessor.processAttachmentForJsonResponse();
        
        connectorProperties.setProperty("attachmentIdMandatory", esbRestResponse.getBody().getJSONObject("attachment")
                .getString("id"));
        
        String apiEndpoint =
                connectorProperties.getProperty("apiUrl") + "/api_v2/projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/"
                        + connectorProperties.getProperty("taskId") + "/attachments/"
                        + connectorProperties.getProperty("attachmentIdMandatory") + ".json";
        
        Thread.sleep(timout);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertTrue(esbRestResponse.getHttpStatusCode() == 201);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("attachment").getString("id"), apiRestResponse
                .getBody().getJSONObject("attachment").getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("attachment").getString("task_id"), apiRestResponse
                .getBody().getJSONObject("attachment").getString("task_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("attachment").getString("created_at"),
                apiRestResponse.getBody().getJSONObject("attachment").getString("created_at"));
    }
    
    /**
     * Positive test case for uploadTaskAttachment method with optional parameters.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = { "testCreateProjectTaskWithMandatoryParameters" }, description = "Bugherd {uploadTaskAttachment} integration test with optional parameters.")
    public void testUploadTaskAttachmentWithOptionalParameters() throws IOException, JSONException,
            NumberFormatException, InterruptedException {
        
        Thread.sleep(timout);
        esbRequestHeadersMap.put("Action", "urn:uploadTaskAttachment");
        
        final Map<String, String> attachmentHeadersMap = new HashMap<String, String>();
        attachmentHeadersMap.putAll(esbRequestHeadersMap);
        attachmentHeadersMap.put("Content-Type", connectorProperties.getProperty("fileContentType"));
        
        String esbEndpPoint =
                proxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&apiKey="
                        + connectorProperties.getProperty("apiKey") + "&projectId="
                        + connectorProperties.getProperty("projectId") + "&taskId="
                        + connectorProperties.getProperty("taskId") + "&fileName="
                        + connectorProperties.getProperty("fileNameOptional");
        
        final MultipartFormdataProcessor fileRequestProcessor =
                new MultipartFormdataProcessor(esbEndpPoint, attachmentHeadersMap);
        File file = new File(pathToResourcesDirectory + connectorProperties.getProperty("fileOptional"));
        fileRequestProcessor.addFiletoRequestBody(file);
        RestResponse<JSONObject> esbRestResponse = fileRequestProcessor.processAttachmentForJsonResponse();
        
        connectorProperties.setProperty("attachmentIdOptional", esbRestResponse.getBody().getJSONObject("attachment")
                .getString("id"));
        
        Thread.sleep(timout);
        String apiEndpoint =
                connectorProperties.getProperty("apiUrl") + "/api_v2/projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/"
                        + connectorProperties.getProperty("taskId") + "/attachments/"
                        + connectorProperties.getProperty("attachmentIdOptional") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertTrue(esbRestResponse.getHttpStatusCode() == 201);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("attachment").getString("id"), apiRestResponse
                .getBody().getJSONObject("attachment").getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("attachment").getString("file_name"),
                apiRestResponse.getBody().getJSONObject("attachment").getString("file_name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("attachment").getString("created_at"),
                apiRestResponse.getBody().getJSONObject("attachment").getString("created_at"));
    }
    
    /**
     * Negative test case for uploadTaskAttachment method.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Bugherd {uploadTaskAttachment} integration test with negative case.")
    public void testUploadTaskAttachmentWithNegativeCase() throws IOException, JSONException, NumberFormatException,
            InterruptedException {
        
        Thread.sleep(timout);
        esbRequestHeadersMap.put("Action", "urn:uploadTaskAttachment");
        
        final Map<String, String> attachmentHeadersMap = new HashMap<String, String>();
        attachmentHeadersMap.putAll(esbRequestHeadersMap);
        attachmentHeadersMap.put("Content-Type", connectorProperties.getProperty("fileContentType"));
        
        String esbEndpPoint =
                proxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&apiKey="
                        + connectorProperties.getProperty("apiKey") + "&projectId="
                        + connectorProperties.getProperty("projectId") + "&taskId=INVALID";
        
        final MultipartFormdataProcessor fileRequestProcessor =
                new MultipartFormdataProcessor(esbEndpPoint, attachmentHeadersMap);
        File file = new File(pathToResourcesDirectory + connectorProperties.getProperty("fileOptional"));
        fileRequestProcessor.addFiletoRequestBody(file);
        RestResponse<JSONObject> esbRestResponse = fileRequestProcessor.processAttachmentForJsonResponse();
        Thread.sleep(timout);
        String apiEndpoint =
                connectorProperties.getProperty("apiUrl") + "/api_v2/projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/INVALID/attachments/upload";
        
        attachmentHeadersMap.put("Content-Type", "application/binary");
        attachmentHeadersMap.put("Authorization", apiRequestHeadersMap.get("Authorization"));
        
        final MultipartFormdataProcessor apiFileRequestProcessor =
                new MultipartFormdataProcessor(apiEndpoint, attachmentHeadersMap);
        File fileOptional = new File(pathToResourcesDirectory + connectorProperties.getProperty("fileOptional"));
        apiFileRequestProcessor.addFiletoRequestBody(fileOptional);
        final RestResponse<JSONObject> apiRestResponse = apiFileRequestProcessor.processAttachmentForJsonResponse();
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
    }
    
    /**
     * Positive test case for listTaskAttachments method with optional parameters.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = { "testUploadTaskAttachmentWithMandatoryParameters",
            "testUploadTaskAttachmentWithOptionalParameters" }, description = "Bugherd {listTaskAttachments} integration test with optional parameters.")
    public void testListTaskAttachmentsWithMandatoryParameters() throws IOException, JSONException,
            NumberFormatException, InterruptedException {
        
        Thread.sleep(timout);
        esbRequestHeadersMap.put("Action", "urn:listTaskAttachments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTaskAttachments_mandatory.json");
        
        String apiEndpoint =
                connectorProperties.getProperty("apiUrl") + "/api_v2/projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/"
                        + connectorProperties.getProperty("taskId") + "/attachments.json";
        Thread.sleep(timout);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("attachments").length(), apiRestResponse.getBody()
                .getJSONArray("attachments").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").getInt("count"), apiRestResponse.getBody()
                .getJSONObject("meta").getInt("count"));
        
    }
    
    /**
     * Positive test case for listTaskAttachments method with optional parameters.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = { "testUploadTaskAttachmentWithMandatoryParameters",
            "testUploadTaskAttachmentWithOptionalParameters" }, description = "Bugherd {listTaskAttachments} integration test with optional parameters.")
    public void testListTaskAttachmentsWithOptionalParameters() throws IOException, JSONException,
            NumberFormatException, InterruptedException {
        
        Thread.sleep(timout);
        esbRequestHeadersMap.put("Action", "urn:listTaskAttachments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTaskAttachments_optional.json");
        
        Thread.sleep(timout);
        String apiEndpoint =
                connectorProperties.getProperty("apiUrl") + "/api_v2/projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/"
                        + connectorProperties.getProperty("taskId") + "/attachments.json?page="
                        + connectorProperties.getProperty("page");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("attachments").length(), apiRestResponse.getBody()
                .getJSONArray("attachments").length());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("meta").getInt("count"), apiRestResponse.getBody()
                .getJSONObject("meta").getInt("count"));
    }
    
    /**
     * Negative test case for listTaskAttachments method.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Bugherd {listTaskAttachments} integration test with negative case.")
    public void testListTaskAttachmentsWithNegativeCase() throws IOException, JSONException, NumberFormatException,
            InterruptedException {
        
        Thread.sleep(timout);
        esbRequestHeadersMap.put("Action", "urn:listTaskAttachments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTaskAttachments_negative.json");
        
        Thread.sleep(timout);
        String apiEndpoint =
                connectorProperties.getProperty("apiUrl") + "/api_v2/projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/INVALID/attachments.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
    }
    
    /**
     * Positive test case for deleteTaskAttachment method with mandatory parameters.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, dependsOnMethods = { "testListTaskAttachmentsWithMandatoryParameters" }, description = "Bugherd {deleteTaskAttachment} integration test with optional parameters.")
    public void testDeleteTaskAttachmentWithMandatoryParameters() throws IOException, JSONException,
            NumberFormatException, InterruptedException {
        
        Thread.sleep(timout);
        esbRequestHeadersMap.put("Action", "urn:deleteTaskAttachment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteTaskAttachments_mandatory.json");
        
        Thread.sleep(timout);
        String apiEndpoint =
                connectorProperties.getProperty("apiUrl") + "/api_v2/projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/"
                        + connectorProperties.getProperty("taskId") + "/attachments/"
                        + connectorProperties.getProperty("attachmentIdMandatory") + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Negative test case for deleteTaskAttachment method.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Bugherd {deleteTaskAttachment} integration test with negative case.")
    public void testDeleteTaskAttachmentWithNegativeCase() throws IOException, JSONException, XMLStreamException,
            InterruptedException {
        
        esbRequestHeadersMap.put("Action", "urn:deleteTaskAttachment");
        Thread.sleep(timout);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteTaskAttachments_mandatory.json");
        
        String apiEndpoint =
                connectorProperties.getProperty("apiUrl") + "/api_v2/projects/"
                        + connectorProperties.getProperty("projectId") + "/tasks/"
                        + connectorProperties.getProperty("taskId") + "/attachments/INVALID.json";
        Thread.sleep(timout);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), esbRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
    }
    
    /**
     *   Test case: testCreateProjectWithMandatoryParameters.      
     *   Status: Skipped.       
     *   Reason :  No mandatory parameter(s) to assert.      
     */
    
    /**
     * Positive test case for createProject method with optional parameters.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Bugherd {createProject} integration test with optional parameters.")
    public void testCreateProjectWithOptionalParameters() throws IOException, JSONException, NumberFormatException,
            InterruptedException {
        
        Thread.sleep(timout);
        
        esbRequestHeadersMap.put("Action", "urn:createProject");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_optional.json");
        
        String projectId = esbRestResponse.getBody().getJSONObject("project").getString("id");
        
        String apiEndpoint = connectorProperties.getProperty("apiUrl") + "/api_v2/projects/" + projectId + ".json";
        
        Thread.sleep(timout);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(connectorProperties.getProperty("projectDevurl"), apiRestResponse
                .getBody().getJSONObject("project").getString("devurl"));
        Assert.assertEquals(connectorProperties.getProperty("projectName"), apiRestResponse
                .getBody().getJSONObject("project").getString("name"));
        Assert.assertEquals(connectorProperties.getProperty("isActive"), apiRestResponse
                .getBody().getJSONObject("project").getString("is_active"));
        Assert.assertEquals(connectorProperties.getProperty("isPublic"), apiRestResponse
                .getBody().getJSONObject("project").getString("is_public"));
    }
    
    /**
     *  Test case: testCreateProjectWithNegativeCase.       
     *  Status: Skipped.       
     *  Reason : Unable to generate a negative result for the method.      
     */
    
    /**
     * Positive test case for showProjectTask method with mandatory parameters.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Bugherd {showProjectTask} integration test with optional parameters.")
    public void testShowProjectTaskWithMandatoryParameters() throws IOException, JSONException, NumberFormatException,
            InterruptedException {
        
        Thread.sleep(timout);
        
        esbRequestHeadersMap.put("Action", "urn:showProjectTask");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_showProjectTask_mandatory.json");
        
        String projectId = connectorProperties.getProperty("projectId");
        String taskId = connectorProperties.getProperty("taskId");
        String apiEndpoint =
                connectorProperties.getProperty("apiUrl") + "/api_v2/projects/" + projectId + "/tasks/" + taskId
                        + ".json";
        
        Thread.sleep(timout);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").getString("id"), apiRestResponse.getBody()
                .getJSONObject("task").getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").getString("priority_id"), apiRestResponse
                .getBody().getJSONObject("task").getString("priority_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").getString("project_id"), apiRestResponse
                .getBody().getJSONObject("task").getString("project_id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").getString("description"), apiRestResponse
                .getBody().getJSONObject("task").getString("description"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("task").getJSONArray("attachments")
                .getJSONObject(0).getString("id"), apiRestResponse.getBody().getJSONObject("task").getJSONArray(
                "attachments").getJSONObject(0).getString("id"));
    }
    
    
    /**
     *  Test case: testShowProjectTaskWithOptionalParameters.       
     *  Status: Skipped.       
     *  Reason : No optional parameter(s) to assert.      
     */
    
    
    /**
     * Negative test case for showProjectTask method.
     * 
     * @throws InterruptedException
     * @throws NumberFormatException
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Bugherd {showProjectTask} integration test with negative case.")
    public void testShowProjectTaskWithNegativeCase() throws IOException, JSONException, NumberFormatException,
            InterruptedException {
        
        Thread.sleep(timout);
        
        esbRequestHeadersMap.put("Action", "urn:showProjectTask");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_showProjectTask_negative.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        String projectId = connectorProperties.getProperty("projectId");
        String taskId = "1234";
        String apiEndpoint =
                connectorProperties.getProperty("apiUrl") + "/api_v2/projects/" + projectId + "/tasks/" + taskId
                        + ".json";
        
        Thread.sleep(timout);
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().get("error"), apiRestResponse.getBody().get("error"));
    }
    
}
