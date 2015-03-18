/*
 * Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.connector.integration.test.gplus;

import org.apache.axis2.context.ConfigurationContext;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.proxy.admin.ProxyServiceAdminClient;
import org.wso2.carbon.automation.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.automation.utils.axis2client.ConfigurationContextProvider;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.carbon.esb.ESBIntegrationTest;
import org.wso2.carbon.mediation.library.stub.MediationLibraryAdminServiceStub;
import org.wso2.carbon.mediation.library.stub.upload.MediationLibraryUploaderStub;

import javax.activation.DataHandler;
import java.net.URL;
import java.util.Properties;


public class GooglePlusCommentsTest extends ESBIntegrationTest {
    private ProxyServiceAdminClient proxyAdmin;

    private String pathToProxiesDirectory = null;

    private String pathToRequestsDirectory = null;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();

        ConfigurationContextProvider configurationContextProvider =
                ConfigurationContextProvider.getInstance();
        ConfigurationContext cc = configurationContextProvider.getConfigurationContext();
        MediationLibraryUploaderStub mediationLibUploadStub = new MediationLibraryUploaderStub(cc,
                esbServer
                        .getBackEndUrl() +
                        "MediationLibraryUploader");
        AuthenticateStub.authenticateStub("admin", "admin", mediationLibUploadStub);

        MediationLibraryAdminServiceStub adminServiceStub = new MediationLibraryAdminServiceStub(cc,
                esbServer
                        .getBackEndUrl() +
                        "MediationLibraryAdminService");

        AuthenticateStub.authenticateStub("admin", "admin", adminServiceStub);

        String repoLocation;
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            repoLocation = System.getProperty("connector_repo").replace("/", "\\");
        } else {
            repoLocation = System.getProperty("connector_repo").replace("/", "/");
        }
        proxyAdmin = new ProxyServiceAdminClient(esbServer.getBackEndUrl(),
                esbServer.getSessionCookie());

        String CONNECTOR_NAME = "googleplus-connector-1.0.0";
        Properties googlePlusConnectorProperties =
                ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);

        pathToProxiesDirectory = repoLocation + googlePlusConnectorProperties
                .getProperty("proxyDirectoryRelativePath");
        pathToRequestsDirectory = repoLocation + googlePlusConnectorProperties
                .getProperty("requestDirectoryRelativePath");
    }

    @Override
    protected void cleanup() {
        axis2Client.destroy();
    }

    /**
     * Mandatory parameter test case for getComments method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {getComments} integration test with mandatory parameters.")
    public void testGetCommentsWithMandatoryParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getComments.txt";
        String methodName = "getComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requestJsonString);
            Assert.assertEquals("plus#comment", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for getComments method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {getComments} integration test with mandatory and optional parameters.")
    public void testGetCommentsWithOptionalParams() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getCommentsOptionalParams.txt";
        String methodName = "getComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requestJsonString);
            Assert.assertEquals("plus#comment", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative parameter test case for getComments method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {getComments} integration test with Negative parameters.")
    public void testGetCommentsWithNegativeParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getCommentsUnhappy.txt";
        String methodName = "getComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int statusCode = ConnectorIntegrationUtil
                    .sendRequestToRetrieveHeaders(getProxyServiceURL(methodName), requestJsonString);
            Assert.assertTrue(statusCode == 404 || statusCode == 403);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for listComments method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listComments} integration test with mandatory parameters.")
    public void testListCommentsWithMandatoryParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listComments.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requestJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listComments method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listComments} integration test with mandatory and optional parameters.")
    public void testListCommentsWithOptionalParams() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requestJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative parameter test case for listComments method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listComments} integration test with Negative parameters.")
    public void testListCommentsWithNegativeParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsUnhappy.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            int statusCode = ConnectorIntegrationUtil
                    .sendRequestToRetrieveHeaders(getProxyServiceURL(methodName), requestJsonString);
            Assert.assertTrue(statusCode == 404 || statusCode == 403);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter maxResults check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listComments} integration test with optional parameter maxResults.")
    public void testListCommentsOneOptionalParam1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"pageToken", "sortOrder", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter pageToken check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listComments} integration test with optional parameter pageToken.")
    public void testListCommentsOneOptionalParam2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"maxResults", "sortOrder", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter sortOrder check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listComments} integration test with optional parameter sortOrder.")
    public void testListCommentsOneOptionalParam3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"maxResults", "pageToken", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter fields check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listComments} integration test with optional parameter fields.")
    public void testListCommentsOneOptionalParam4() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"maxResults", "pageToken", "sortOrder"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListComments method with maxResults,pageToken  optional parameter.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {ListComments} integration test with mandatory and maxResults,pageToken optional parameters.")
    public void testListCommentsWithTwoOptionalParams1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"sortOrder", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListComments method with maxResults,sortOrder optional parameter.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {ListComments} integration test with mandatory and maxResults,sortOrder optional parameters.")
    public void testListCommentsWithTwoOptionalParams2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"fields", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListComments method with maxResults,fields optional parameter.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {ListComments} integration test with mandatory and maxResults,fields optional parameters.")
    public void testListCommentsWithTwoOptionalParams3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"sortOrder", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListComments method with pageToken,sortOrder.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {ListComments} integration test with mandatory and pageToken,sortOrder optional parameters.")
    public void testListCommentsWithTwoOptionalParams4() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"maxResults", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListComments method with pageToken,fields optional parameter
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {ListComments} integration test with mandatory and language,fields optional parameters.")
    public void testListCommentsWithTwoOptionalParams5() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"maxResults", "sortOrder"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListComments method with sortOrder,fields optional parameter.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {ListComments} integration test with mandatory and sortOrder,fields optional parameters.")
    public void testListCommentsWithTwoOptionalParams6() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"maxResults", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter "pageToken", "sortOrder", "fields"  check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listComments} integration test with optional parameter pageToken, sortOrder, fields.")
    public void testListCommentsThreeOptionalParam1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"maxResults"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter "maxResults", "sortOrder", "fields" check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listComments} integration test with optional parameter maxResults, sortOrder, fields.")
    public void testListCommentsThreeOptionalParam2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter "maxResults", "pageToken", "fields"  check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listComments} integration test with optional parameter maxResults, pageToken, fields.")
    public void testListCommentsThreeOptionalParam3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"sortOrder"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter "maxResults", "pageToken", "sortOrder" check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listComments} integration test with optional parameter maxResults, pageToken, sortOrder.")
    public void testListCommentsThreeOptionalParam4() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


}
