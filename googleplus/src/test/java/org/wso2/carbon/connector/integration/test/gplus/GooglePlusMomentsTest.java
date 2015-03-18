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

import junit.framework.Assert;
import org.apache.axis2.context.ConfigurationContext;
import org.json.JSONObject;
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


public class GooglePlusMomentsTest extends ESBIntegrationTest {

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
     * Mandatory parameter test case for listMoments method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listMoments} integration test with mandatory parameters.")
    public void testListMomentsWithMandatoryParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listMoments.txt";
        String methodName = "listMoments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requestJsonString);
            Assert.assertEquals("plus#momentsFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listMoments method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listMoments} integration test with mandatory and optional parameters.")
    public void testListMomentsWithOptionalParams() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listMomentsOptionalParams.txt";
        String methodName = "listMoments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requestJsonString);
            Assert.assertEquals("plus#momentsFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative parameter test case for listMoments method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listMoments} integration test with Negative parameters.")
    public void testListMomentsWithNegativeParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listMomentsUnhappy.txt";
        String methodName = "listMoments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int statusCode = ConnectorIntegrationUtil
                    .sendRequestToRetrieveHeaders(getProxyServiceURL(methodName), requestJsonString);
            Assert.assertEquals(statusCode, 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
}
