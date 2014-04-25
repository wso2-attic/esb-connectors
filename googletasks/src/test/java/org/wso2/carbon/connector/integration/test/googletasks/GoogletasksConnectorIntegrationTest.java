/**
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.connector.integration.test.googletasks;

import java.net.URL;
import java.util.Properties;

import javax.activation.DataHandler;

import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.carbon.connector.integration.test.common.RestResponse;
import org.apache.axis2.context.ConfigurationContext;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.proxy.admin.ProxyServiceAdminClient;
import org.wso2.carbon.automation.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.automation.utils.axis2client.ConfigurationContextProvider;
import org.wso2.carbon.esb.ESBIntegrationTest;
import org.wso2.carbon.mediation.library.stub.MediationLibraryAdminServiceStub;
import org.wso2.carbon.mediation.library.stub.upload.MediationLibraryUploaderStub;

public class GoogletasksConnectorIntegrationTest extends ESBIntegrationTest {

	private static final String CONNECTOR_NAME = "googletasks";

	private MediationLibraryUploaderStub mediationLibUploadStub = null;

	private MediationLibraryAdminServiceStub adminServiceStub = null;

	private ProxyServiceAdminClient proxyAdmin;

	private String repoLocation = null;

	private String googletasksConnectorFileName = CONNECTOR_NAME + ".zip";

	private Properties googletasksConnectorProperties = null;

	private String pathToProxiesDirectory = null;

	private String pathToRequestsDirectory = null;

	private String accessToken = null;

	private JSONObject parameters = null;

	private String taskListId = null;

	private String taskId = null;

	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {
		super.init();
		ConfigurationContextProvider configurationContextProvider =
		                                                            ConfigurationContextProvider.getInstance();
		ConfigurationContext cc = configurationContextProvider.getConfigurationContext();

		mediationLibUploadStub =
		                         new MediationLibraryUploaderStub(cc, esbServer.getBackEndUrl() +
		                                                              "MediationLibraryUploader");
		AuthenticateStub.authenticateStub("admin", "admin", mediationLibUploadStub);

		adminServiceStub =
		                   new MediationLibraryAdminServiceStub(cc, esbServer.getBackEndUrl() +
		                                                            "MediationLibraryAdminService");

		AuthenticateStub.authenticateStub("admin", "admin", adminServiceStub);

		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			repoLocation = System.getProperty("connector_repo").replace("\\", "/");
		} else {
			repoLocation = System.getProperty("connector_repo").replace("/", "/");
		}

		proxyAdmin =
		             new ProxyServiceAdminClient(esbServer.getBackEndUrl(),
		                                         esbServer.getSessionCookie());

		ConnectorIntegrationUtil.uploadConnector(repoLocation, mediationLibUploadStub,
		                                         googletasksConnectorFileName);
		log.info("Sleeping for " + 10000 / 1000 + " seconds while waiting for synapse import");
		Thread.sleep(10000);

		adminServiceStub.updateStatus("{org.wso2.carbon.connector}" + CONNECTOR_NAME,
		                              CONNECTOR_NAME, "org.wso2.carbon.connector", "enabled");

		googletasksConnectorProperties =
		                                 ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);

		pathToProxiesDirectory =
		                         repoLocation +
		                                 googletasksConnectorProperties.getProperty("proxyDirectoryRelativePath");

		pathToRequestsDirectory =
		                          repoLocation +
		                                  googletasksConnectorProperties.getProperty("requestDirectoryRelativePath");

		// Invoking the getAccessToken method to derive the access token
		// which will be used in other test cases
		String jsonRequestFilePath = pathToRequestsDirectory + "getAccessToken.txt";
		String methodName = "googletasks_getAccessToken";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		String modifiedJsonString =
		                            String.format(jsonString,
		                                          googletasksConnectorProperties.getProperty("clientId"),
		                                          googletasksConnectorProperties.getProperty("clientSecret"),
		                                          googletasksConnectorProperties.getProperty("refreshToken"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse restResponse =
			                            ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                 modifiedJsonString);
			accessToken = restResponse.getBody().get("access_token").toString();

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Override
	protected void cleanup() {
		axis2Client.destroy();
	}

	/**
	 * Test cases for google task lists
	 */

	/**
	 * Mandatory parameter test case for listTaskLists method.
	 */
	@Test(groups = { "wso2.esb" }, description = "googletasks {listTaskLists} integration test with mandatory parameters.")
	public void testListTaskListsWithMandatoryParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "listTaskLists_mandatory.txt";
		String methodName = "googletasks_listTaskLists";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

		String modifiedJsonString = String.format(jsonString, accessToken);

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);
			String apiEPR = "https://www.googleapis.com/tasks/v1/users/@me/lists";
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);
			log.info("\n\n\n" + apiResponse.getBody());
			Assert.assertTrue(esbResponse.getBody().get("etag")
			                             .equals(apiResponse.getBody().get("etag")));
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Optional parameter test case for listTaskLists method.
	 */
	@Test(groups = { "wso2.esb" }, description = "googletasks {listTaskLists} integration test with optional parameters.")
	public void testListTaskListsWithOptionalParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "listTaskLists_optional.txt";
		String methodName = "googletasks_listTaskLists";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);
			String apiEPR =
			                "https://www.googleapis.com/tasks/v1/users/@me/lists?" + "maxResults=" +
			                        parameters.get("maxResults") + "&pageToken=" +
			                        parameters.get("pageToken");
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);
			Assert.assertTrue(esbResponse.getBody().get("etag")
			                             .equals(apiResponse.getBody().get("etag")));
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Negative Optional parameter test case for listTaskLists method.
	 */
	@Test(groups = { "wso2.esb" }, description = "googletasks {listTaskLists} integration test with optional parameters.")
	public void testListTaskListsWithNegativeOptionalParameters() throws Exception {

		String jsonRequestFilePath =
		                             pathToRequestsDirectory +
		                                     "listTaskLists_negative_optional.txt";
		String methodName = "googletasks_listTaskLists";

		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);
			String apiEPR =
			                "https://www.googleapis.com/tasks/v1/users/@me/lists?" + "maxResults=" +
			                        parameters.get("maxResults") + "&pageToken=" +
			                        parameters.get("pageToken");
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);
			Assert.assertTrue(esbResponse.getResponseCode() == 400 |
			                  esbResponse.getResponseCode() == 404);
			Assert.assertTrue(apiResponse.getResponseCode() == 400 |
			                  apiResponse.getResponseCode() == 404);
			Assert.assertEquals(esbResponse.getBody().getJSONObject("error").get("message"),
			                    apiResponse.getBody().getJSONObject("error").get("message"));
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Mandatory parameter test case for insertTaskList method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 1, description = "googletasks {insertTaskList} integration test with mandatory parameters.")
	public void testInsertTaskListWithMandatoryParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "insertTaskList_mandatory.txt";
		String methodName = "googletasks_insertTaskList";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			String apiEPR =
			                "https://www.googleapis.com/tasks/v1/users/@me/lists/" +
			                        esbResponse.getBody().get("id").toString();
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);
			taskListId = esbResponse.getBody().get("id").toString();
			Assert.assertEquals(esbResponse.getBody().get("title"),
			                    apiResponse.getBody().get("title"));
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Negative parameter test case for insertTaskList method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 1, description = "googletasks {insertTaskList} integration test with negative parameters.")
	public void testInsertTaskListWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "insertTaskList_negative.txt";
		String methodName = "googletasks_insertTaskList";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			Assert.assertEquals(esbResponse.getResponseCode(), 400);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Mandatory parameter test case for getTaskList method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 2, description = "googletasks {getTaskList} integration test with mandatory parameters.")
	public void testGetTaskListWithMandatoryParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getTaskList_mandatory.txt";
		String methodName = "googletasks_getTaskList";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);
			String apiEPR = "https://www.googleapis.com/tasks/v1/users/@me/lists/" + taskListId;
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);
			Assert.assertTrue(esbResponse.getBody().get("etag")
			                             .equals(apiResponse.getBody().get("etag")));
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Negative parameter test case for getTaskList method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 2, description = "googletasks {getTaskList} integration test with negative parameters.")
	public void testGetTaskListWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getTaskList_negative.txt";
		String methodName = "googletasks_getTaskList";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);
			String apiEPR =
			                "https://www.googleapis.com/tasks/v1/users/@me/lists/" +
			                        parameters.get("tasklist_id");
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);

			Assert.assertTrue(esbResponse.getResponseCode() == 400 |
			                  esbResponse.getResponseCode() == 404);
			Assert.assertTrue(apiResponse.getResponseCode() == 400 |
			                  apiResponse.getResponseCode() == 404);
			Assert.assertEquals(esbResponse.getBody().getJSONObject("error").get("message"),
			                    apiResponse.getBody().getJSONObject("error").get("message"));
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Mandatory parameter test case for updateTaskList method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 2, description = "googletasks {updateTaskList} integration test with mandatory parameters.")
	public void testUpdateTaskListWithMandatoryParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "updateTaskList_mandatory.txt";
		String methodName = "googletasks_updateTaskList";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			String apiEPR = "https://www.googleapis.com/tasks/v1/users/@me/lists/" + taskListId;
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);
			Assert.assertEquals(esbResponse.getBody().get("title"),
			                    apiResponse.getBody().get("title"));
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Negative parameter test case for updateTaskList method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 2, description = "googletasks {updateTaskList} integration test with negative parameters.")
	public void testUpdateTaskListWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "updateTaskList_negative.txt";
		String methodName = "googletasks_updateTaskList";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			String apiEPR =
			                "https://www.googleapis.com/tasks/v1/users/@me/lists/" +
			                        parameters.get("tasklist_id");
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);
			Assert.assertEquals(esbResponse.getResponseCode(), 400);
			Assert.assertEquals(apiResponse.getResponseCode(), 400);
			Assert.assertEquals(esbResponse.getBody().getJSONObject("error").get("message"),
			                    apiResponse.getBody().getJSONObject("error").get("message"));
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Mandatory parameter test case for patchTaskList method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 3, description = "googletasks {patchTaskList} integration test with mandatory parameters.")
	public void testPatchTaskListWithMandatoryParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "patchTaskList_mandatory.txt";
		String methodName = "googletasks_patchTaskList";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			String apiEPR = "https://www.googleapis.com/tasks/v1/users/@me/lists/" + taskListId;

			Assert.assertEquals(esbResponse.getResponseCode(), 200);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Negative parameter test case for patchTaskList method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 3, description = "googletasks {patchTaskList} integration test with negative parameters.")
	public void testPatchTaskListWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "patchTaskList_negative.txt";
		String methodName = "googletasks_patchTaskList";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			String apiEPR =
			                "https://www.googleapis.com/tasks/v1/users/@me/lists/" +
			                        parameters.get("tasklist_id");
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);
			Assert.assertEquals(esbResponse.getResponseCode(), 400);
			Assert.assertEquals(apiResponse.getResponseCode(), 400);
			Assert.assertEquals(esbResponse.getBody().getJSONObject("error").get("message"),
			                    apiResponse.getBody().getJSONObject("error").get("message"));
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Mandatory parameter test case for deleteTaskList method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 10, description = "googletasks {deleteTaskList} integration test with mandatory parameters.")
	public void testDeleteTaskListWithMandatoryParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "deleteTaskList_mandatory.txt";
		String methodName = "googletasks_deleteTaskList";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);
			String apiEPR = "https://www.googleapis.com/tasks/v1/users/@me/lists/" + taskListId;
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);
			Assert.assertEquals(esbResponse.getResponseCode(), 204);

			Assert.assertTrue(apiResponse.getResponseCode() == 400 |
			                  apiResponse.getResponseCode() == 404);
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Negative parameter test case for deleteTaskList method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 10, description = "googletasks {deleteTaskList} integration test with negative parameters.")
	public void testDeleteTaskListWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "deleteTaskList_negative.txt";
		String methodName = "googletasks_deleteTaskList";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);
			String apiEPR = "https://www.googleapis.com/tasks/v1/users/@me/lists/" + taskListId;
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);
			Assert.assertTrue(esbResponse.getResponseCode() == 400 |
			                  esbResponse.getResponseCode() == 404 |
			                  esbResponse.getResponseCode() == -1);
			Assert.assertTrue(apiResponse.getResponseCode() == 400 |
			                  apiResponse.getResponseCode() == 404 |
			                  esbResponse.getResponseCode() == -1);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Test cases for google tasks
	 */

	/**
	 * Mandatory parameter test case for listTasks method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 3, description = "googletasks {listTasks} integration test with mandatory parameters.")
	public void testListTasksWithMandatoryParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "listTasks_mandatory.txt";
		String methodName = "googletasks_listTasks";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			String apiEPR = "https://www.googleapis.com/tasks/v1/lists/" + taskListId + "/tasks";
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);

			Assert.assertTrue(esbResponse.getBody().get("etag")
			                             .equals(apiResponse.getBody().get("etag")));
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Negative parameter test case for listTasks method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 3, description = "googletasks {listTasks} integration test with negative parameters.")
	public void testListTasksWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "listTasks_negative.txt";
		String methodName = "googletasks_listTasks";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			String apiEPR =
			                "https://www.googleapis.com/tasks/v1/lists/" +
			                        parameters.get("tasklist_id") + "/tasks";
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);

			Assert.assertEquals(esbResponse.getResponseCode(), 400);
			Assert.assertEquals(apiResponse.getResponseCode(), 400);
			Assert.assertEquals(esbResponse.getBody().getJSONObject("error").get("message"),
			                    apiResponse.getBody().getJSONObject("error").get("message"));
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Optional parameter test case for listTasks method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 3, description = "googletasks {listTasks} integration test with optional parameters.")
	public void testListTasksWithOptionalParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "listTasks_optional.txt";
		String methodName = "googletasks_listTasks";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			String apiEPR =
			                "https://www.googleapis.com/tasks/v1/lists/" + taskListId + "/tasks?" +
			                        "completedMax=" + parameters.getString("completedMax") +
			                        "&completedMin" + parameters.getString("completedMin") +
			                        "&dueMax=" + parameters.getString("dueMax") + "&maxResults=" +
			                        parameters.getString("maxResults") + "&pageToken=" +
			                        parameters.getString("pageToken") + "&showCompleted=" +
			                        parameters.getString("showCompleted") + "&showDeleted=" +
			                        parameters.getString("showDeleted") + "&updatedMin" +
			                        parameters.getString("updatedMin") + "&showHidden=" +
			                        parameters.getString("showHidden");
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);
			Assert.assertEquals(esbResponse.getBody().get("etag"), apiResponse.getBody()
			                                                                  .get("etag"));
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Negative Optional parameter test case for listTasks method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 3, description = "googletasks {listTasks} integration test with negative optional parameters.")
	public void testListTasksWithNegativeOptionalParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "listTasks_negative_optional.txt";
		String methodName = "googletasks_listTasks";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			String apiEPR =
			                "https://www.googleapis.com/tasks/v1/lists/" + taskListId + "/tasks?" +
			                        "completedMax=" + parameters.getString("completedMax") +
			                        "&completedMin" + parameters.getString("completedMin") +
			                        "&dueMax=" + parameters.getString("dueMax") + "&maxResults=" +
			                        parameters.getString("maxResults") + "&pageToken=" +
			                        parameters.getString("pageToken") + "&showCompleted=" +
			                        parameters.getString("showCompleted") + "&showDeleted=" +
			                        parameters.getString("showDeleted") + "&updatedMin" +
			                        parameters.getString("updatedMin") + "&showHidden=" +
			                        parameters.getString("showHidden");
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);

			Assert.assertEquals(esbResponse.getResponseCode(), 400);
			Assert.assertEquals(apiResponse.getResponseCode(), 400);
			Assert.assertEquals(esbResponse.getBody().getJSONObject("error").get("message"),
			                    apiResponse.getBody().getJSONObject("error").get("message"));
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Mandatory parameter test case for insertTask method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 4, description = "googletasks {insertTask} integration test with mandatory parameters.")
	public void testInsertTaskWithMandatoryParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "insertTask_mandatory.txt";
		String methodName = "googletasks_insertTask";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			String apiEPR =
			                "https://www.googleapis.com/tasks/v1/lists/" + taskListId + "/tasks/" +
			                        esbResponse.getBody().get("id").toString();
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);
			taskId = esbResponse.getBody().get("id").toString();

			Assert.assertEquals(esbResponse.getBody().get("title"),
			                    apiResponse.getBody().get("title"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Negative parameter test case for insertTask method.
	 */
	// @Test(groups = { "wso2.esb" }, priority = 4, description =
	// "googletasks {insertTask} integration test with negative parameters.")
	public void testInsertTaskWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "insertTask_negative.txt";
		String methodName = "googletasks_insertTask";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			Assert.assertTrue(esbResponse.getResponseCode() == 400);
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Optional parameter test case for insertTask method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 4, description = "googletasks {insertTask} integration test with optional parameters.")
	public void testInsertTaskWithOptionalParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "insertTask_optional.txt";
		String methodName = "googletasks_insertTask";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			String apiEPR =
			                "https://www.googleapis.com/tasks/v1/lists/" + taskListId + "/tasks/" +
			                        esbResponse.getBody().get("id").toString();
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);

			log.info("\n\n\nesb\n\n" + esbResponse.getBody());
			log.info("\n\n\napi\n\n" + apiResponse.getBody());

			Assert.assertEquals(esbResponse.getBody().get("title"),
			                    apiResponse.getBody().get("title"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Negative Optional parameter test case for insertTask method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 4, description = "googletasks {insertTask} integration test with negative optional parameters.")
	public void testInsertTaskWithNegativeOptionalParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "insertTask_negative_optional.txt";
		String methodName = "googletasks_insertTask";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			Assert.assertEquals(esbResponse.getResponseCode(), 400);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Mandatory parameter test case for getTask method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 5, description = "googletasks {getTask} integration test with mandatory parameters.")
	public void testGetTaskWithMandatoryParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getTask_mandatory.txt";
		String methodName = "googletasks_getTask";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId, taskId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			String apiEPR =
			                "https://www.googleapis.com/tasks/v1/lists/" + taskListId + "/tasks/" +
			                        taskId;
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);

			Assert.assertTrue(esbResponse.getBody().get("etag")
			                             .equals(apiResponse.getBody().get("etag")));
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Negative parameter test case for getTask method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 5, description = "googletasks {getTask} integration test with negative parameters.")
	public void testGetTaskWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getTask_negative.txt";
		String methodName = "googletasks_getTask";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			String apiEPR =
			                "https://www.googleapis.com/tasks/v1/lists/" + taskListId + "/tasks/" +
			                        parameters.get("task_id");
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);

			Assert.assertEquals(esbResponse.getResponseCode(), 400);
			Assert.assertEquals(esbResponse.getResponseCode(), 400);
			Assert.assertEquals(esbResponse.getBody().getJSONObject("error").get("message"),
			                    apiResponse.getBody().getJSONObject("error").get("message"));
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Mandatory parameter test case for clearTask method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 5, description = "googletasks {clearTask} integration test with mandatory parameters.")
	public void testClearTaskWithMandatoryParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "clearTask_mandatory.txt";
		String methodName = "googletasks_clearTask";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			Assert.assertEquals(esbResponse.getResponseCode(), 204);
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Negative parameter test case for clearTask method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 5, description = "googletasks {clearTask} integration test with negative parameters.")
	public void testClearTaskWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "clearTask_negative.txt";
		String methodName = "googletasks_clearTask";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);
			Assert.assertTrue(esbResponse.getResponseCode() == 400 |
			                  esbResponse.getResponseCode() == -1);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Mandatory parameter test case for moveTask method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 6, description = "googletasks {moveTask} integration test with mandatory parameters.")
	public void testMoveTaskWithMandatoryParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "moveTask_mandatory.txt";
		String methodName = "googletasks_moveTask";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId, taskId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			Assert.assertEquals(esbResponse.getResponseCode(), 200);
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Negative parameter test case for moveTask method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 6, description = "googletasks {moveTask} integration test with negative parameters.")
	public void testMoveTaskWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "moveTask_negative.txt";
		String methodName = "googletasks_moveTask";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			Assert.assertEquals(esbResponse.getResponseCode(), 503);
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Optional parameter test case for moveTask method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 6, description = "googletasks {moveTask} integration test with optional parameters.")
	public void testMoveTaskWithOptionalParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "moveTask_optional.txt";
		String methodName = "googletasks_moveTask";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			Assert.assertEquals(esbResponse.getResponseCode(), 200);
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Negative Optional parameter test case for moveTask method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 6, description = "googletasks {moveTask} integration test with negative optional parameters.")
	public void testMoveTaskWithNegativeOptionalParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "moveTask_negative_optional.txt";
		String methodName = "googletasks_moveTask";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			Assert.assertEquals(esbResponse.getResponseCode(), 400);
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Mandatory parameter test case for updateTask method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 7, description = "googletasks {updateTask} integration test with mandatory parameters.")
	public void testUpdateTaskWithMandatoryParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "updateTask_mandatory.txt";
		String methodName = "googletasks_updateTask";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId, taskId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			String apiEPR =
			                "https://www.googleapis.com/tasks/v1/lists/" + taskListId + "/tasks/" +
			                        taskId;
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);
			Assert.assertEquals(esbResponse.getBody().get("title"),
			                    apiResponse.getBody().get("title"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Negative parameter test case for updateTask method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 7, description = "googletasks {updateTask} integration test with negative parameters.")
	public void testUpdateTaskWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "updateTask_negative.txt";
		String methodName = "googletasks_updateTask";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			Assert.assertTrue(esbResponse.getResponseCode() == 400 |
			                  esbResponse.getResponseCode() == 404);
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Optional parameter test case for updateTask method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 7, description = "googletasks {updateTask} integration test with optional parameters.")
	public void testUpdateTaskWithOptionalParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "updateTask_optional.txt";
		String methodName = "googletasks_updateTask";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId, taskId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			String apiEPR =
			                "https://www.googleapis.com/tasks/v1/lists/" + taskListId + "/tasks/" +
			                        taskId;
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);
			Assert.assertEquals(esbResponse.getBody().get("notes"),
			                    apiResponse.getBody().get("notes"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Negative optional parameter test case for updateTask method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 7, description = "googletasks {updateTask} integration test with negative optional parameters.")
	public void testUpdateTaskWithNegativeOptionalParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "updateTask_negative_optional.txt";
		String methodName = "googletasks_updateTask";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId, taskId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			Assert.assertTrue(esbResponse.getResponseCode() == 400 |
			                  esbResponse.getResponseCode() == 404);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Mandatory parameter test case for patchTask method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 8, description = "googletasks {patchTask} integration test with mandatory parameters.")
	public void testPatchTaskWithMandatoryParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "patchTask_mandatory.txt";
		String methodName = "googletasks_patchTask";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId, taskId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			String apiEPR =
			                "https://www.googleapis.com/tasks/v1/lists/" + taskListId + "/tasks/" +
			                        taskId;
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);
			Assert.assertEquals(esbResponse.getBody().get("title"),
			                    apiResponse.getBody().get("title"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Negative parameter test case for patchTask method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 8, description = "googletasks {updateTask} integration test with negative parameters.")
	public void testPatchTaskWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "patchTask_negative.txt";
		String methodName = "googletasks_patchTask";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			String apiEPR = "https://www.googleapis.com/tasks/v1/lists/" + taskListId + "/tasks/";

			Assert.assertTrue(esbResponse.getResponseCode() == 400 |
			                  esbResponse.getResponseCode() == 404);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Optional parameter test case for patchTask method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 8, description = "googletasks {patchTask} integration test with optional parameters.")
	public void testPatchTaskWithOptionalParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "patchTask_optional.txt";
		String methodName = "googletasks_patchTask";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId, taskId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			String apiEPR =
			                "https://www.googleapis.com/tasks/v1/lists/" + taskListId + "/tasks/" +
			                        taskId;
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "GET",
			                                                                      modifiedJsonString,
			                                                                      accessToken);
			Assert.assertEquals(esbResponse.getBody().get("notes"),
			                    apiResponse.getBody().get("notes"));

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Negative parameter test case for patchTask method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 8, description = "googletasks {updateTask} integration test with negative parameters.")
	public void testPatchTaskWithNegativeOptionalParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "patchTask_negative_optional.txt";
		String methodName = "googletasks_patchTask";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId, taskId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			Assert.assertTrue(esbResponse.getResponseCode() == 400 |
			                  esbResponse.getResponseCode() == 404);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Mandatory parameter test case for deleteTask method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 9, description = "googletasks {deleteTask} integration test with mandatory parameters.")
	public void testDeleteTaskWithMandatoryParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "deleteTask_mandatory.txt";
		String methodName = "googletasks_deleteTask";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId, taskId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			String apiEPR =
			                "https://www.googleapis.com/tasks/v1/lists/" + taskListId + "/tasks/" +
			                        taskId;
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "DELETE",
			                                                                      modifiedJsonString,
			                                                                      accessToken);
			Assert.assertEquals(esbResponse.getResponseCode(), 204);
			Assert.assertEquals(apiResponse.getResponseCode(), 204);
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}

	/**
	 * Negative parameter test case for deleteTask method.
	 */
	@Test(groups = { "wso2.esb" }, priority = 9, description = "googletasks {deleteTask} integration test with negative parameters.")
	public void testDeleteTaskWithNegativeParameters() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "deleteTask_negative.txt";
		String methodName = "googletasks_deleteTask";
		final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
		String modifiedJsonString = String.format(jsonString, accessToken, taskListId);
		parameters = new JSONObject(jsonString);

		final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			RestResponse esbResponse =
			                           ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName),
			                                                                modifiedJsonString);

			String apiEPR =
			                "https://www.googleapis.com/tasks/v1/lists/" + taskListId + "/tasks/" +
			                        parameters.get("task_id");
			RestResponse apiResponse =
			                           ConnectorIntegrationUtil.sendDirectRequest(apiEPR,
			                                                                      "DELETE",
			                                                                      modifiedJsonString,
			                                                                      accessToken);

			Assert.assertTrue(esbResponse.getResponseCode() == 400 |
			                  esbResponse.getResponseCode() == -1);
			Assert.assertEquals(apiResponse.getResponseCode(), 400);

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}
	}
}
