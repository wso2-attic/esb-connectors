
package org.wso2.carbon.connector.integration.test.redmine;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class RedmineConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> parametersMap = new HashMap<String, String>();
    
    private String userId;
    
    private String projectId;
    
    private String projectIdOptional;
    
    private String issueId;
    
    private String optionalIssueId;
    
    private String optionalUserId;
    
    private String timeEntryId1;
    
    private String timeEntryId2;
    
    private String timeEntryId3;
    
    private String attachmentId;
    
    private String attachmentToken;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("redmine-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiRequestHeadersMap.put("X-Redmine-API-Key", connectorProperties.getProperty("apiKey"));
        
    }
    
    /**
     * Positive test case for createUser method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Redmine {createUser} integration test with mandatory parameters.")
    public void testCreateUserWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createUser");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createUser_mandatory.json");
        userId = esbRestResponse.getBody().getJSONObject("user").getString("id");
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/users/" + userId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").get("id"), apiRestResponse.getBody().getJSONObject("user").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").get("login"), apiRestResponse.getBody().getJSONObject("user").get("login"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").get("mail"), apiRestResponse.getBody().getJSONObject("user").get("mail"));
        
    }
    
    /**
     * Optional test case for createUser method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Redmine {createUser} integration test with mandatory parameters.")
    public void testCreateUserWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createUser");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createUser_optional.json");
        optionalUserId = esbRestResponse.getBody().getJSONObject("user").getString("id");
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/users/" + optionalUserId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").get("id"), apiRestResponse.getBody().getJSONObject("user").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").get("login"), apiRestResponse.getBody().getJSONObject("user").get("login"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").get("mail"), apiRestResponse.getBody().getJSONObject("user").get("mail"));
        
    }
    
    /**
     * Negative test case for createUser method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Redmine {createUser} integration test for negative case.")
    public void testCreateUserNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createUser");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createUser_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/users.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createUser_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        //Assert.assertEquals(esbRestResponse.getBody().toString().contains("errors"), apiRestResponse.getBody().toString().contains("errors"));
                
    }
    
    /**
     * Positive test case for getUser method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateUserWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Redmine {getUser} integration test with mandatory parameters.")
    public void testGetUserWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getUser");
        parametersMap.put("userId", userId);
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUser_mandatory.json", parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/users/" + userId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").get("id"), apiRestResponse.getBody().getJSONObject("user").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").get("login"), apiRestResponse.getBody().getJSONObject("user").get("login"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for getUser method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateUserWithMandatoryParameters" }, description = "Redmine {getUser} integration test with optional parameters.")
    public void testGetUserWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getUser");
        parametersMap.put("userId", userId);
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUser_optional.json", parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/users/" + userId + ".json?include=groups,memberships";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").get("id"), apiRestResponse.getBody().getJSONObject("user").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").get("login"), apiRestResponse.getBody().getJSONObject("user").get("login"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("user").getJSONArray("groups").toString(), 
                apiRestResponse.getBody().getJSONObject("user").getJSONArray("groups").toString());
        
    }
    
    /**
     * Negative test case for getUser method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateUserWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Redmine {getUser} integration test negative case.")
    public void testGetUserNegativeCase() throws Exception {
    
        String invalidUserId = "garbage_string21341231321afd"+ userId;
        esbRequestHeadersMap.put("Action", "urn:getUser");
        parametersMap.put("userId", invalidUserId);
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getUser_negative.json", parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/users/" + invalidUserId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for listUsers method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods= {"testCreateUserWithMandatoryParameters"},
            description = "Redmine {listUsers} integration test with mandatory parameters.")
    public void testListUsersWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listUsers");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_mandatory.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/users.json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("total_count"), apiRestResponse.getBody().get("total_count"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("users").getJSONObject(0).get("id"), 
                apiRestResponse.getBody().getJSONArray("users").getJSONObject(0).get("id"));
        
    }
    
    /**
     * Positive test case for listUsers method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods= {"testCreateUserWithMandatoryParameters"},
            description = "Redmine {listUsers} integration test with optional parameters.")
    public void testListUsersWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listUsers");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_optional.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/users.json?status=1&name=" 
                        + connectorProperties.getProperty("createUserLogin");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().get("total_count"), apiRestResponse.getBody().get("total_count"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("users").getJSONObject(0).get("id"), 
                apiRestResponse.getBody().getJSONArray("users").getJSONObject(0).get("id"));
         
    }
    
    /**
     * Negative test case for listUsers method with mandatory parameters.
     */
    @Test(dependsOnMethods= {"testCreateUserWithMandatoryParameters"},
            groups = { "wso2.esb" }, description = "Redmine {listUsers} integration test negative case.")
    public void testListUsersNegativeCase() throws Exception {
        esbRequestHeadersMap.put("Action", "urn:listUsers");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listUsers_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 401);
        
    }
    
    /**
     * Positive test case for updateUser method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateUserWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Redmine {updateUser} integration test with mandatory parameters.")
    public void testUpdateUserWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateUser");
        parametersMap.put("userId", userId);
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateUser_mandatory.json", parametersMap);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/users/" + userId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("user").getString("id"), userId);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("user").get("login"), connectorProperties.getProperty("updateUserLogin"));
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("user").get("mail"), connectorProperties.getProperty("updateUserMail"));
        
    }
    
    /**
     * Positive test case for updateUser method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateUserWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Redmine {updateUser} integration test with optional parameters.")
    public void testUpdateUserWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateUser");
        parametersMap.put("userId", userId);
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateUser_optional.json", parametersMap);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/users/" + userId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("user").getString("id"), userId);
        
    }
    
    /**
     * Negative test case for updateUser method.
     */
    @Test(dependsOnMethods = { "testCreateUserWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Redmine {updateUser} integration test for negative case.")
    public void testUpdateUserNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateUser");
        parametersMap.put("userId", userId);
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateUser_negative.json", parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/users/" + userId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateUser_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        //Assert.assertEquals(esbRestResponse.getBody().toString().contains("errors"), apiRestResponse.getBody().toString().contains("errors"));
        
    }
    
    /**
     * Positive test case for deleteUser method with mandatory parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "Redmine {deleteUser} integration test with mandatory parameters.")
    public void testDeleteUserWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteUser");
        parametersMap.put("userId", userId);
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteUser_mandatory.json", parametersMap);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/users/" + userId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Negative test case for deleteUser method with mandatory parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "Redmine {deleteUser} integration test negative case.")
    public void testDeleteUserNegativeCase() throws Exception {
    
        String invalidUserId =  "garbage_string21341231321" + userId;
        esbRequestHeadersMap.put("Action", "urn:deleteUser");
        parametersMap.put("userId", invalidUserId);
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteUser_negative.json",
                parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/users/" + invalidUserId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for getAttachment method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testGetIssueWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Redmine {getAttachment} integration test with mandatory parameters.")
    public void testGetAttachmentWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getAttachment");
        parametersMap.put("attachmentId", attachmentId);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAttachment_mandatory.json",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/attachments/" + attachmentId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("attachment").getString("id"), apiRestResponse
                .getBody().getJSONObject("attachment").getString("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("attachment").getString("filename"),
                apiRestResponse.getBody().getJSONObject("attachment").getString("filename"));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Negative test case for getAttachment method.
     */
    @Test(dependsOnMethods = { "testGetIssueWithOptionalParameters" }, groups = { "wso2.esb" }, description = "Redmine {getAttachment} integration test negative case.")
    public void testGetAttachmentNegativeCase() throws Exception {
    
        String invalidAttachmentId = "garbage_value21341231321afd";
        esbRequestHeadersMap.put("Action", "urn:getAttachment");
        parametersMap.put("attachmentId", invalidAttachmentId);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAttachment_negative.json",
                        parametersMap);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/attachments/" + invalidAttachmentId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for addAttachment method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateUserWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Redmine {addAttachment} integration test with mandatory parameters.")
    public void testAddAttachmentWithMandatoryParameters() throws Exception {
    
        Map<String, String> attachmentHeadersMap = new HashMap<String, String>();
        attachmentHeadersMap.putAll(esbRequestHeadersMap);
        attachmentHeadersMap.put("Action", "urn:addAttachment");
        attachmentHeadersMap.put("Content-Type", connectorProperties.getProperty("attachmentContentType"));
        String endPointUrl =
                proxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&apiKey="
                        + connectorProperties.getProperty("apiKey") + "&responseType=json";
        
        MultipartFormdataProcessor fileRequestProcessor =
                new MultipartFormdataProcessor(endPointUrl, attachmentHeadersMap);
        
        File file = new File(pathToResourcesDirectory + connectorProperties.getProperty("attachmentFileName"));
        
        fileRequestProcessor.addFiletoRequestBody(file);
        RestResponse<JSONObject> esbRestResponse = fileRequestProcessor.processAttachmentForJsonResponse();
        attachmentToken = esbRestResponse.getBody().getJSONObject("upload").getString("token");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
        
    }
    
    /**
     * Negative test case for addAttachment method.
     */
    @Test(dependsOnMethods = { "testCreateUserWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Redmine {addAttachment} integration test negative case.")
    public void testAddAttachmentNegativeCase() throws Exception {
    
        Map<String, String> attachmentHeadersMap = new HashMap<String, String>();
        attachmentHeadersMap.putAll(esbRequestHeadersMap);
        attachmentHeadersMap.put("Action", "urn:addAttachment");
        attachmentHeadersMap.put("Content-Type", connectorProperties.getProperty("attachmentContentType"));
        String endPointUrl =
                proxyUrl + "?apiUrl=" + connectorProperties.getProperty("apiUrl") + "&apiKey="
                        + connectorProperties.getProperty("apiKey") + "1&responseType=json";
        
        MultipartFormdataProcessor fileRequestProcessor =
                new MultipartFormdataProcessor(endPointUrl, attachmentHeadersMap);
        
        File file = new File(pathToResourcesDirectory + connectorProperties.getProperty("attachmentFileName"));
        
        fileRequestProcessor.addFiletoRequestBody(file);
        RestResponse<JSONObject> esbRestResponse = fileRequestProcessor.processAttachmentForJsonResponse();
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 401);
        
    }
    
    /**
     * Positive test case for createProject method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Redmine {createProject} integration test with mandatory parameters.")
    public void testCreateProjectWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createProject");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_mandatory.json");
        projectId = esbRestResponse.getBody().getJSONObject("project").getString("id");
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/projects/" + projectId + ".json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("project").get("id"), apiRestResponse.getBody()
                .getJSONObject("project").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("project").get("name"), apiRestResponse.getBody()
                .getJSONObject("project").get("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("project").get("identifier"), apiRestResponse
                .getBody().getJSONObject("project").get("identifier"));
    }
    
    /**
     * Positive test case for createProject method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "Redmine {createProject} integration test with optional parameters.")
    public void testCreateProjectWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createProject");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_optional.json");
        projectIdOptional = esbRestResponse.getBody().getJSONObject("project").getString("id");
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + projectIdOptional + ".json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("project").get("id"), apiRestResponse.getBody()
                .getJSONObject("project").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("project").get("name"), apiRestResponse.getBody()
                .getJSONObject("project").get("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("project").get("description"), apiRestResponse
                .getBody().getJSONObject("project").get("description"));
    }
    
    /**
     * Negative test case for createProject method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "Redmine {createProject} integration test with negative case.")
    public void testCreateProjectWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createProject");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProject_negative.json");
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/projects.json";
        final RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createProject_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for getProject method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "Redmine {getProject} integration test with mandatory parameters.")
    public void testGetProjectWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getProject");
        parametersMap.put("projectId", projectId);
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProject_mandatory.json",
                        parametersMap);
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/projects/" + projectId + ".json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("project").get("id"), apiRestResponse.getBody()
                .getJSONObject("project").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("project").get("name"), apiRestResponse.getBody()
                .getJSONObject("project").get("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("project").get("identifier"), apiRestResponse
                .getBody().getJSONObject("project").get("identifier"));
    }
    
    /**
     * Positive test case for getProject method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectWithOptionalParameters" }, description = "Redmine {getProject} integration test with optional parameters.")
    public void testGetProjectWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getProject");
        parametersMap.put("projectId", projectIdOptional);
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProject_optional.json",
                        parametersMap);
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + projectIdOptional + ".json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("project").get("id"), apiRestResponse.getBody()
                .getJSONObject("project").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("project").get("name"), apiRestResponse.getBody()
                .getJSONObject("project").get("name"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("project").get("identifier"), apiRestResponse
                .getBody().getJSONObject("project").get("identifier"));
    }
    
    /**
     * Negative test case for getProject method.
     */
    @Test(groups = { "wso2.esb" }, description = "Redmine {getProject} integration test with negative case.")
    public void testGetProjectWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getProject");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getProject_negative.json");
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/projects/0.json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for updateProject method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetProjectWithMandatoryParameters" }, description = "Redmine {updateProject} integration test with mandatory parameters.")
    public void testUpdateProjectWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateProject");
        parametersMap.put("projectId", projectId);
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateProject_mandatory.json",
                        parametersMap);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/projects/" + projectId + ".json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(connectorProperties.getProperty("updateProjectName"), apiRestResponse.getBody()
                .getJSONObject("project").get("name"));
    }
    
    /**
     * Positive test case for updateProject method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetProjectWithOptionalParameters" }, description = "Redmine {updateProject} integration test with optional parameters.")
    public void testUpdateProjectWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateProject");
        parametersMap.put("projectId", projectIdOptional);
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateProject_optional.json",
                        parametersMap);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/projects/" + projectIdOptional + ".json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(connectorProperties.getProperty("updateProjectName"), apiRestResponse.getBody()
                .getJSONObject("project").get("name"));
        Assert.assertEquals(connectorProperties.getProperty("updateProjectDescription"), apiRestResponse.getBody()
                .getJSONObject("project").get("description"));
    }
    
    /**
     * Negative test case for updateProject method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateProjectWithMandatoryParameters",
            "testUpdateProjectWithOptionalParameters" }, description = "Redmine {updateProject} integration test with negative case.")
    public void testUpdateProjectWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateProject");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateProject_negative.json");
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/projects/0.json";
        final RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap, "api_updateProject_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for listProjects method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateProjectWithMandatoryParameters",
            "testUpdateProjectWithOptionalParameters" }, description = "Redmine {listProjects} integration test with mandatory parameters.")
    public void testListProjectsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listProjects");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjects_mandatory.json");
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/projects.json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("total_count"), apiRestResponse.getBody().get("total_count"));
        Assert.assertEquals(((JSONObject) esbRestResponse.getBody().getJSONArray("projects").get(0)).get("id"),
                ((JSONObject) apiRestResponse.getBody().getJSONArray("projects").get(0)).get("id"));
    }
    
    /**
     * Negative test case for listProjects method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateProjectWithMandatoryParameters",
            "testUpdateProjectWithOptionalParameters" }, description = "Redmine {listProjects} integration test with negative case.")
    public void testListProjectsWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listProjects");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProjects_negative.json");
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/projects.json";
        
        apiRequestHeadersMap.put("X-Redmine-API-Key", "000" + connectorProperties.getProperty("apiKey"));
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        apiRequestHeadersMap.put("X-Redmine-API-Key", connectorProperties.getProperty("apiKey"));
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for deleteProject method with mandatory parameters.
     */
    @Test(priority = 4, groups = { "wso2.esb" }, description = "Redmine {deleteProject} integration test with mandatory parameters.")
    public void testDeleteProjectWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteProject");
        parametersMap.put("projectId", connectorProperties.getProperty("createProjectIdentifier"));
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteProject_mandatory.json",
                        parametersMap);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/projects/" + projectId + ".json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Negative test case for deleteProject method.
     */
    @Test(priority = 4, groups = { "wso2.esb" }, description = "Redmine {deleteProject} integration test with negative case.")
    public void testDeleteProjectWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteProject");
        parametersMap.put("projectId", projectId);
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteProject_negative.json",
                        parametersMap);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/projects/" + projectId + ".json";
        final RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for createIssue method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "Redmine {createIssue} integration test with mandatory parameters.")
    public void testCreateIssueWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createIssue");
        parametersMap.put("projectId", projectId);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createIssue_mandatory.json", parametersMap);
        issueId = esbRestResponse.getBody().getJSONObject("issue").getString("id");
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/issues/" + issueId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("issue").get("subject"), apiRestResponse.getBody()
                .getJSONObject("issue").get("subject"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("issue").get("description"), apiRestResponse
                .getBody().getJSONObject("issue").get("description"));
        
    }
    
    /**
     * Positive test case for createIssue method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateUserWithOptionalParameters","testCreateProjectWithMandatoryParameters",
            "testAddAttachmentWithMandatoryParameters" }, description = "Redmine {createIssue} integration test with optional parameters.")
    public void testCreateIssueWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createIssue");
        parametersMap.put("projectId", projectId);
        parametersMap.put("attachmentToken", attachmentToken);
        parametersMap.put("optionalUserId", optionalUserId);
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createIssue_optional.json", parametersMap);
        optionalIssueId = esbRestResponse.getBody().getJSONObject("issue").getString("id");
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/issues/" + optionalIssueId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("issue").get("subject"), apiRestResponse.getBody()
                .getJSONObject("issue").get("subject"));
        //Assert.assertEquals(esbRestResponse.getBody().getJSONObject("issue").getJSONObject("fixed_version").get("id"),
        //        apiRestResponse.getBody().getJSONObject("issue").getJSONObject("fixed_version").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("issue").getString("created_on"),
                apiRestResponse.getBody().getJSONObject("issue").getString("created_on"));
        
    }
    
    /**
     * Negative test case for createIssue method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, description = "Redmine {createIssue} integration test with negative case.")
    public void testCreateIssueWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createIssue");
        parametersMap.put("projectId", projectId);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createIssue_negative.json", parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/issues.json";
        
        RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createIssue_negative.json", parametersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for getIssue method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateIssueWithMandatoryParameters" }, description = "Redmine {getIssue} integration test with mandatory parameters.")
    public void testGetIssueWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getIssue");
        parametersMap.put("issueId", issueId);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getIssue_mandatory.json",
                        parametersMap);
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/issues/" + issueId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("issue").get("subject"), apiRestResponse.getBody()
                .getJSONObject("issue").get("subject"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("issue").get("description"), apiRestResponse
                .getBody().getJSONObject("issue").get("description"));
    }
    
    /**
     * Positive test case for getIssue method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateIssueWithOptionalParameters" }, description = "Redmine {getIssue} integration test with optional parameters.")
    public void testGetIssueWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getIssue");
        parametersMap.put("issueId", optionalIssueId);
        parametersMap.put("include", "attachments,journals,children,relations,changesets,watchers");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getIssue_optional.json", parametersMap);
        
        String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/issues/" + optionalIssueId + ".json?include=watchers";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        attachmentId =
                ((JSONObject) esbRestResponse.getBody().getJSONObject("issue").getJSONArray("attachments").get(0))
                        .getString("id");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("issue").get("subject"), apiRestResponse.getBody()
                .getJSONObject("issue").get("subject"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("issue").getJSONArray("watchers").getJSONObject(0)
                .get("id"), apiRestResponse.getBody().getJSONObject("issue").getJSONArray("watchers").getJSONObject(0)
                .get("id"));
    }
    
    /**
     * Negative test case for getIssue method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateIssueWithMandatoryParameters" }, description = "Redmine {getIssue} integration test with mandatory parameters.")
    public void testGetIssueWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getIssue");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getIssue_negative.json");
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/issues/" + -1 + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for updateIssue method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetIssueWithMandatoryParameters" }, description = "Redmine {updateIssue} integration test with mandatory parameters.")
    public void testUpdateIssueWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateIssue");
        parametersMap.put("issueId", issueId);
        parametersMap.put("projectId", projectId);
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateIssue_mandatory.json",
                        parametersMap);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/issues/" + issueId + ".json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(connectorProperties.getProperty("updateIssueSubject"), apiRestResponse.getBody()
                .getJSONObject("issue").get("subject"));
        Assert.assertEquals(connectorProperties.getProperty("updateIssueDesc"), apiRestResponse.getBody()
                .getJSONObject("issue").get("description"));
    }
    
    /**
     * Positive test case for updateIssue method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetIssueWithOptionalParameters" }, description = "Redmine {updateIssue} integration test with optional parameters.")
    public void testUpdateIssueWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateIssue");
        parametersMap.put("optionalIssueId", issueId);
        parametersMap.put("projectId", projectId);
        parametersMap.put("attachmentToken", attachmentToken);
        parametersMap.put("optionalUserId", optionalUserId);
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateIssue_optional.json",
                        parametersMap);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/issues/" + issueId + ".json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(connectorProperties.getProperty("updateIssueSubject"), apiRestResponse.getBody()
                .getJSONObject("issue").get("subject"));
    }
    
    /**
     * Negative test case for updateIssue method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetIssueWithOptionalParameters" }, description = "Redmine {updateIssue} integration test with negative case.")
    public void testUpdateIssueWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateIssue");
        parametersMap.put("projectId", projectId);
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateIssue_negative.json", parametersMap);
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/issues/0.json";
        final RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateIssue_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for listIssues method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateIssueWithMandatoryParameters" }, description = "Redmine {listIssues} integration test with mandatory parameters.")
    public void testListIssuesWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listIssues");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listIssues_mandatory.json");
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/issues.json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("total_count"), apiRestResponse.getBody().get("total_count"));
        Assert.assertEquals(((JSONObject) esbRestResponse.getBody().getJSONArray("issues").get(0)).get("id"),
                ((JSONObject) apiRestResponse.getBody().getJSONArray("issues").get(0)).get("id"));
    }
    
    /**
     * Positive test case for listIssues method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateIssueWithMandatoryParameters" }, description = "Redmine {listIssues} integration test with optional parameters.")
    public void testListIssuesWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listIssues");
        parametersMap.put("projectId", projectId);
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listIssues_optional.json",
                        parametersMap);
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/issues.json?project_id=" + projectId
                        + "&status_id=open&sort=updated_on:desc&offset=0";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("total_count"), apiRestResponse.getBody().get("total_count"));
        Assert.assertEquals(((JSONObject) esbRestResponse.getBody().getJSONArray("issues").get(0)).get("id"),
                ((JSONObject) apiRestResponse.getBody().getJSONArray("issues").get(0)).get("id"));
        Assert.assertEquals(esbRestResponse.getBody().get("offset"), apiRestResponse.getBody().get("offset"));
    }
    
    /**
     * Negative test case for listIssues method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateIssueWithMandatoryParameters" }, description = "Redmine {listIssues} integration test with negative case.")
    public void testListIssuesWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listIssues");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listIssues_negative.json");
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl")
                        + "/issues.Invalid?project_id=1&status_id=open&sort=updated_on:desc&offset=5";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
    
    /**
     * Positive test case for deleteIssue method with mandatory parameters.
     */
    @Test(priority = 3, groups = { "wso2.esb" }, description = "Redmine {deleteIssue} integration test with mandatory parameters.")
    public void testDeleteIssueWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteIssue");
        parametersMap.put("issueId", issueId);
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteIssue_mandatory.json",
                        parametersMap);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/issues/" + issueId + ".json";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Negative test case for deleteIssue method with mandatory parameters.
     */
    @Test(priority = 3, groups = { "wso2.esb" }, description = "Redmine {deleteIssue} integration test with negative case.")
    public void testDeleteIssueWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteIssue");
        
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteIssue_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }
    
    /**
     * Positive test case for createTimeEntry method with mandatory parameters(With only issueId).
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateIssueWithMandatoryParameters" }, description = "Redmine {createTimeEntry} integration test with mandatory parameters.")
    public void testCreateTimeEntryWithMandatoryParametersOnlyIssueId() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createTimeEntry");
        parametersMap.put("issueId", issueId);
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_createTimeEntry_mandatory_issueId.json", parametersMap);
        timeEntryId1 = esbRestResponse.getBody().getJSONObject("time_entry").getString("id");
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/time_entries/" + timeEntryId1 + ".json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("time_entry").get("id"), apiRestResponse.getBody()
                .getJSONObject("time_entry").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("time_entry").getJSONObject("issue").get("id"),
                apiRestResponse.getBody().getJSONObject("time_entry").getJSONObject("issue").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("time_entry").get("hours"), apiRestResponse
                .getBody().getJSONObject("time_entry").get("hours"));
        
    }
    
    /**
     * Positive test case for createTimeEntry method with mandatory parameters(With only projectId).
     */
    @Test(dependsOnMethods = { "testCreateProjectWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "Redmine {createTimeEntry} integration test with mandatory parameters.")
    public void testCreateTimeEntryWithMandatoryParametersOnlyProjectId() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createTimeEntry");
        parametersMap.put("projectId", projectId);
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_createTimeEntry_mandatory_projectId.json", parametersMap);
        timeEntryId2 = esbRestResponse.getBody().getJSONObject("time_entry").getString("id");
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/time_entries/" + timeEntryId2 + ".json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("time_entry").get("id"), apiRestResponse.getBody()
                .getJSONObject("time_entry").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("time_entry").getJSONObject("project").get("id"),
                apiRestResponse.getBody().getJSONObject("time_entry").getJSONObject("project").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("time_entry").get("hours"), apiRestResponse
                .getBody().getJSONObject("time_entry").get("hours"));
        
    }
    
    /**
     * Positive test case for createTimeEntry method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateIssueWithOptionalParameters" }, description = "Redmine {createTimeEntry} integration test with optional parameters.")
    public void testCreateTimeEntryWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createTimeEntry");
        parametersMap.put("issueId", optionalIssueId);
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTimeEntry_optional.json",
                        parametersMap);
        timeEntryId3 = esbRestResponse.getBody().getJSONObject("time_entry").getString("id");
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/time_entries/" + timeEntryId3 + ".json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("time_entry").get("id"), apiRestResponse.getBody()
                .getJSONObject("time_entry").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("time_entry").getJSONObject("issue").get("id"),
                apiRestResponse.getBody().getJSONObject("time_entry").getJSONObject("issue").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("time_entry").get("comments"), apiRestResponse
                .getBody().getJSONObject("time_entry").get("comments"));
        
    }
    
    /**
     * Negative test case for createTimeEntry method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTimeEntryWithMandatoryParametersOnlyIssueId" }, description = "Redmine {createTimeEntry} integration test with negative case.")
    public void testCreateTimeEntryWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createTimeEntry");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createTimeEntry_negative.json");
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/time_entries.json";
        final RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createTimeEntry_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for updateTimeEntry method with mandatory parameters(With only issueId).
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTimeEntryWithMandatoryParametersOnlyIssueId" }, description = "Redmine {updateTimeEntry} integration test with mandatory parameters.")
    public void testUpdateTimeEntryWithMandatoryParametersOnlyIssueId() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateTimeEntry");
        parametersMap.put("timeEntryId1", timeEntryId1);
        parametersMap.put("issueId", issueId);
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_updateTimeEntry_mandatory_issueId.json", parametersMap);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/time_entries/" + timeEntryId1 + ".json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(parametersMap.get("timeEntryId1"), apiRestResponse.getBody().getJSONObject("time_entry")
                .getString("id"));
        Assert.assertEquals(parametersMap.get("issueId"), apiRestResponse.getBody().getJSONObject("time_entry")
                .getJSONObject("issue").getString("id"));
        Assert.assertEquals(connectorProperties.getProperty("spentOn"),
                apiRestResponse.getBody().getJSONObject("time_entry").getString("spent_on"));
        
    }
    
    /**
     * Positive test case for updateTimeEntry method with mandatory parameters(With only projectId).
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTimeEntryWithMandatoryParametersOnlyProjectId" }, description = "Redmine {updateTimeEntry} integration test with mandatory parameters.")
    public void testUpdateTimeEntryWithMandatoryParametersOnlyProjectId() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateTimeEntry");
        parametersMap.put("timeEntryId2", timeEntryId2);
        parametersMap.put("projectId", projectId);
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "esb_updateTimeEntry_mandatory_projectId.json", parametersMap);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/time_entries/" + timeEntryId2 + ".json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(parametersMap.get("timeEntryId2"), apiRestResponse.getBody().getJSONObject("time_entry")
                .getString("id"));
        Assert.assertEquals(connectorProperties.getProperty("spentOn"),
                apiRestResponse.getBody().getJSONObject("time_entry").getString("spent_on"));
        Assert.assertEquals(connectorProperties.getProperty("hours"),
                apiRestResponse.getBody().getJSONObject("time_entry").getString("hours"));
        
    }
    
    /**
     * Positive test case for updateTimeEntry method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTimeEntryWithOptionalParameters" }, description = "Redmine {updateTimeEntry} integration test with optional parameters.")
    public void testUpdateTimeEntryWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateTimeEntry");
        parametersMap.put("timeEntryId3", timeEntryId3);
        parametersMap.put("issueId", issueId);
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTimeEntry_optional.json",
                        parametersMap);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/time_entries/" + timeEntryId3 + ".json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(parametersMap.get("timeEntryId3"), apiRestResponse.getBody().getJSONObject("time_entry")
                .getString("id"));
        Assert.assertEquals(parametersMap.get("issueId"), apiRestResponse.getBody().getJSONObject("time_entry")
                .getJSONObject("issue").getString("id"));
        Assert.assertEquals(connectorProperties.getProperty("comments"),
                apiRestResponse.getBody().getJSONObject("time_entry").getString("comments"));
        
    }
    
    /**
     * Negative test case for updateTimeEntry method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUpdateTimeEntryWithMandatoryParametersOnlyIssueId" }, description = "Redmine {updateTimeEntry} integration test with negative case.")
    public void testUpdateTimeEntryWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:updateTimeEntry");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateTimeEntry_negative.json",
                        parametersMap);
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/time_entries/Invalid.json";
        final RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "PUT", apiRequestHeadersMap, "api_updateTimeEntry_negative.json");
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for deleteTimeEntry method with mandatory parameters.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "Redmine {deleteTimeEntry} integration test with mandatory parameters.")
    public void testDeleteTimeEntryWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteTimeEntry");
        parametersMap.put("timeEntryId1", timeEntryId1);
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteTimeEntry_mandatory.json",
                        parametersMap);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/time_entries/" + timeEntryId1 + ".json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        
    }
    
    /**
     * Negative test case for deleteTimeEntry method.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "Redmine {deleteTimeEntry} integration test with negative case.")
    public void testDeleteTimeEntryWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteTimeEntry");
        parametersMap.put("timeEntryId1", timeEntryId1);
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteTimeEntry_negative.json",
                        parametersMap);
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/time_entries/" + timeEntryId1 + ".json";
        final RestResponse<JSONObject> apiRestResponse =
                sendJsonRestRequest(apiEndPoint, "DELETE", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for getTimeEntry method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTimeEntryWithMandatoryParametersOnlyIssueId" }, description = "Redmine {getTimeEntry} integration test with mandatory parameters.")
    public void testGetTimeEntryWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getTimeEntry");
        parametersMap.put("timeEntryId1", timeEntryId1);
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTimeEntry_mandatory.json",
                        parametersMap);
        
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeOut")));
        
        final String apiEndPoint =
                connectorProperties.getProperty("apiUrl") + "/time_entries/" + timeEntryId1 + ".json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("time_entry").get("id"), apiRestResponse.getBody()
                .getJSONObject("time_entry").get("id"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("time_entry").get("hours"), apiRestResponse
                .getBody().getJSONObject("time_entry").get("hours"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("time_entry").get("spent_on"), apiRestResponse
                .getBody().getJSONObject("time_entry").get("spent_on"));
        
    }
    
    /**
     * Negative test case for getTimeEntry method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateTimeEntryWithMandatoryParametersOnlyIssueId" }, description = "Redmine {getTimeEntry} integration test with negative case.")
    public void testGetTimeEntryWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getTimeEntry");
        
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getTimeEntry_negative.json");
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/time_entries/Invalid.json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        
    }
    
    /**
     * Positive test case for listTimeEntries method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetTimeEntryWithMandatoryParameters" }, description = "Redmine {listTimeEntries} integration test with mandatory parameters.")
    public void testListTimeEntriesWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listTimeEntries");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTimeEntries_mandatory.json");
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/time_entries.json";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getBody().get("total_count"), apiRestResponse.getBody().get("total_count"));
        Assert.assertEquals(((JSONObject) esbRestResponse.getBody().getJSONArray("time_entries").get(0)).get("id"),
                ((JSONObject) apiRestResponse.getBody().getJSONArray("time_entries").get(0)).get("id"));
    }
    
    /**
     * Negative test case for listTimeEntries method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetTimeEntryWithMandatoryParameters" }, description = "Redmine {listTimeEntries} integration test with negative case.")
    public void testListTimeEntriesWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listTimeEntries");
        final RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listTimeEntries_negative.json");
        
        final String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/time_entries.Invalid";
        final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
        
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
    }
  
}
