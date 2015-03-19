/**
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 **/

package org.wso2.carbon.connector.interation.test.codeplex;

import org.apache.axis2.context.ConfigurationContext;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.proxy.admin.ProxyServiceAdminClient;
import org.wso2.carbon.automation.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.automation.utils.axis2client.ConfigurationContextProvider;
import org.wso2.carbon.connector.interation.test.common.ConnectorIntegrationUtil;
import org.wso2.carbon.esb.ESBIntegrationTest;
import org.wso2.carbon.mediation.library.stub.MediationLibraryAdminServiceStub;
import org.wso2.carbon.mediation.library.stub.upload.MediationLibraryUploaderStub;

import javax.activation.DataHandler;
import java.net.URL;
import java.util.Properties;


/**
 * This class contains project related test cases for codeplex connector for esb.
 */
public class CodeplexProjectsTest extends ESBIntegrationTest {

    private static final String CONNECTOR_NAME = "codeplex";

    private MediationLibraryUploaderStub mediationLibUploadStub = null;

    private MediationLibraryAdminServiceStub adminServiceStub = null;

    private ProxyServiceAdminClient proxyAdmin;

    private String repoLocation = null;

    private Properties codeplexConnectorProperties = null;

    private String pathToProxiesDirectory = null;

    private String pathToRequestsDirectory = null;

    /**
     *
     * This class will log in to esb instance as admin user to execute codeplex project
     * related test cases.
     *
     * @throws java.lang.Exception
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();

        ConfigurationContextProvider configurationContextProvider = ConfigurationContextProvider.getInstance();
        ConfigurationContext cc = configurationContextProvider.getConfigurationContext();
        mediationLibUploadStub =
                new MediationLibraryUploaderStub(cc, esbServer.getBackEndUrl() + "MediationLibraryUploader");
        AuthenticateStub.authenticateStub("admin", "admin", mediationLibUploadStub);

        adminServiceStub =
                new MediationLibraryAdminServiceStub(cc, esbServer.getBackEndUrl() + "MediationLibraryAdminService");

        AuthenticateStub.authenticateStub("admin", "admin", adminServiceStub);

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            repoLocation = System.getProperty("connector_repo").replace("/", "\\");
        } else {
            repoLocation = System.getProperty("connector_repo").replace("/", "/");
        }
        proxyAdmin = new ProxyServiceAdminClient(esbServer.getBackEndUrl(), esbServer.getSessionCookie());

        codeplexConnectorProperties = ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);

        pathToProxiesDirectory = repoLocation + codeplexConnectorProperties.getProperty("proxyDirectoryRelativePath");
        pathToRequestsDirectory = repoLocation + codeplexConnectorProperties.getProperty("requestDirectoryRelativePath");

    }


    @Override
    protected void cleanup() {
        axis2Client.destroy();
    }


    /**
     * Mandatory parameter test case for getProjects method.
     * @throws java.lang.Exception
     */
    @Test(groups = {"wso2.esb"}, priority = 4,
          description = "codeplex {getProjects} integration test with mandatory parameter.")
    public void testGetProjectsWithMandatoryParameter() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getProjects_mandatory.txt";
        String methodName = "codeplexGetProjectsProxy";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        int headerResponse;
        try {
            headerResponse = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
                                                                                  jsonString);

            Assert.assertEquals(headerResponse, 200);
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test negative case for getProjects method.
     * @throws java.lang.Exception
     */
    @Test(groups = {"wso2.esb"}, priority = 5,
          description = "codeplex {getProjects} integration test with negative case.")
    public void testGetProjectsWithNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getProjects_negative.txt";
        String methodName = "codeplexGetProjectsProxy";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        int headerResponse;
        try {
            headerResponse = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
                                                                                  jsonString);

            Assert.assertEquals(headerResponse, 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


    /**
     * Test case for getAuthenticatedUserProjects method.
     * @throws java.lang.Exception
     */
    @Test(groups = {"wso2.esb"}, priority = 6,
          description = "codeplex {getAuthenticatedUserProjects} integration test.")
    public void testGetAuthenticatedUserProjects() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getAuthenticatedUser.txt";
        String methodName = "codeplexGetAuthenticatedUserProjectsProxy";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        String modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                                  codeplexConnectorProperties.getProperty("clientSecret"),
                                                  codeplexConnectorProperties.getProperty("refreshToken"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        int headerResponse;
        try {
            headerResponse = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
                                                                                  modifiedJsonString);
            Assert.assertEquals(headerResponse, 200);
        } finally {

            proxyAdmin.deleteProxy(methodName);

        }
    }

    /**
     * Test negative case for getAuthenticatedUserProjects method.
     * @throws java.lang.Exception
     */
    @Test(groups = {"wso2.esb"}, priority = 7,
          description = "codeplex {getAuthenticatedUserProjects} integration test for negative case.")
    public void testGetAuthenticatedUserProjectsWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAuthenticatedUserProjects_negative.txt";
        String methodName = "codeplexGetAuthenticatedUserProjectsProxy";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);

            Assert.assertEquals(jsonResponse.getString("Message"), "Authentication required.");
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


}
