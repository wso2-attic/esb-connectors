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


public class GooglePlusPeopleTest extends ESBIntegrationTest {

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
     * Mandatory parameter test case for getPeople method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {getPeople} integration test with mandatory parameters.")
    public void testGetPeopleWithMandatoryParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getPeople.txt";
        String methodName = "getPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requestJsonString);
            Assert.assertEquals("plus#person", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for getPeople method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {getPeople} integration test with mandatory and optional parameters.")
    public void testGetPeopleWithOptionalParams() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getPeopleOptionalParams.txt";
        String methodName = "getPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requestJsonString);
            Assert.assertEquals("plus#person", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative parameter test case for getPeople method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {getPeople} integration test with Negative parameters.")
    public void testGetPeopleWithNegativeParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getPeopleUnhappy.txt";
        String methodName = "getPeople";
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
     * Mandatory parameter test case for searchPeople method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {searchPeople} integration test with mandatory parameters.")
    public void testSearchPeopleWithMandatoryParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeople.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requestJsonString);
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchPeople method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {searchPeople} integration test with mandatory and optional parameters.")
    public void testSearchPeopleWithOptionalParams() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requestJsonString);
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative parameter test case for searchPeople method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {searchPeople} integration test with Negative parameters.")
    public void testSearchPeopleWithNegativeParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleUnhappy.txt";
        String methodName = "searchPeople";
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

    /**
     * Optional parameter maxResults check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {searchPeople} integration test with optional parameter maxResults.")
    public void testSearchPeopleOneOptionalParam1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"pageToken", "language", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
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
            description = "GooglePlus {searchPeople} integration test with optional parameter pageToken.")
    public void testSearchPeopleOneOptionalParam2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"maxResults", "language", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter language check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {searchPeople} integration test with optional parameter language.")
    public void testSearchPeopleOneOptionalParam3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
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
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
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
            description = "GooglePlus {searchPeople} integration test with optional parameter fields.")
    public void testSearchPeopleOneOptionalParam4() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"maxResults", "pageToken", "language"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for SearchPeople method with maxResults,pageToken  optional parameter.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {SearchPeople} integration test with mandatory and maxResults,pageToken optional parameters.")
    public void testSearchPeopleWithTwoOptionalParams1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"language", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for SearchPeople method with maxResults,language optional parameter.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {SearchPeople} integration test with mandatory and maxResults,language optional parameters.")
    public void testSearchPeopleWithTwoOptionalParams2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
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
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for SearchPeople method with maxResults,fields optional parameter.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {SearchPeople} integration test with mandatory and maxResults,fields optional parameters.")
    public void testSearchPeopleWithTwoOptionalParams3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"language", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for SearchPeople method with pageToken,language.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {SearchPeople} integration test with mandatory and pageToken,language optional parameters.")
    public void testSearchPeopleWithTwoOptionalParams4() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
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
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for SearchPeople method with pageToken,fields optional parameter
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {SearchPeople} integration test with mandatory and language,fields optional parameters.")
    public void testSearchPeopleWithTwoOptionalParams5() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"maxResults", "language"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for SearchPeople method with language,fields optional parameter.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {SearchPeople} integration test with mandatory and language,fields optional parameters.")
    public void testSearchPeopleWithTwoOptionalParams6() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
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
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter "pageToken", "language", "fields"  check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {searchPeople} integration test with optional parameter pageToken, language, fields.")
    public void testSearchPeopleThreeOptionalParam1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
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
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter "maxResults", "language", "fields" check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {searchPeople} integration test with optional parameter maxResults, language, fields.")
    public void testSearchPeopleThreeOptionalParam2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
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
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
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
            description = "GooglePlus {searchPeople} integration test with optional parameter maxResults, pageToken, fields.")
    public void testSearchPeopleThreeOptionalParam3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"language"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter "maxResults", "pageToken", "language" check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {searchPeople} integration test with optional parameter maxResults, pageToken, language.")
    public void testSearchPeopleThreeOptionalParam4() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
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
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for listPeople method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listPeople} integration test with mandatory parameters.")
    public void testListPeopleWithMandatoryParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listPeople.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requestJsonString);
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listPeople method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listPeople} integration test with mandatory and optional parameters.")
    public void testListPeopleWithOptionalParams() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requestJsonString);
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative parameter test case for listPeople method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listPeople} integration test with Negative parameters.")
    public void testListPeopleWithNegativeParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleUnhappy.txt";
        String methodName = "listPeople";
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

    /**
     * Optional parameter maxResults check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listPeople} integration test with optional parameter maxResults.")
    public void testListPeopleOneOptionalParam1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"pageToken", "orderBy", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
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
            description = "GooglePlus {listPeople} integration test with optional parameter pageToken.")
    public void testListPeopleOneOptionalParam2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"maxResults", "orderBy", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter orderBy check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listPeople} integration test with optional parameter orderBy.")
    public void testListPeopleOneOptionalParam3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
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
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
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
            description = "GooglePlus {listPeople} integration test with optional parameter fields.")
    public void testListPeopleOneOptionalParam4() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"maxResults", "pageToken", "orderBy"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListPeople method with maxResults,pageToken  optional parameter.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {ListPeople} integration test with mandatory and maxResults,pageToken optional parameters.")
    public void testListPeopleWithTwoOptionalParams1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"orderBy", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListPeople method with maxResults,orderBy optional parameter.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {ListPeople} integration test with mandatory and maxResults,orderBy optional parameters.")
    public void testListPeopleWithTwoOptionalParams2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
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
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListPeople method with maxResults,fields optional parameter.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {ListPeople} integration test with mandatory and maxResults,fields optional parameters.")
    public void testListPeopleWithTwoOptionalParams3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"orderBy", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListPeople method with pageToken,orderBy.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {ListPeople} integration test with mandatory and pageToken,orderBy optional parameters.")
    public void testListPeopleWithTwoOptionalParams4() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
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
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListPeople method with pageToken,fields optional parameter
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {ListPeople} integration test with mandatory and language,fields optional parameters.")
    public void testListPeopleWithTwoOptionalParams5() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"maxResults", "orderBy"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListPeople method with orderBy,fields optional parameter.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {ListPeople} integration test with mandatory and orderBy,fields optional parameters.")
    public void testListPeopleWithTwoOptionalParams6() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
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
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter "pageToken", "orderBy", "fields"  check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listPeople} integration test with optional parameter pageToken, orderBy, fields.")
    public void testListPeopleThreeOptionalParam1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
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
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter "maxResults", "orderBy", "fields" check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listPeople} integration test with optional parameter maxResults, orderBy, fields.")
    public void testListPeopleThreeOptionalParam2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
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
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
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
            description = "GooglePlus {listPeople} integration test with optional parameter maxResults, pageToken, fields.")
    public void testListPeopleThreeOptionalParam3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"orderBy"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter "maxResults", "pageToken", "orderBy" check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listPeople} integration test with optional parameter maxResults, pageToken, orderBy.")
    public void testListPeopleThreeOptionalParam4() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
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
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for listPeople method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listByActivity} integration test with mandatory parameters.")
    public void testListByActivityWithMandatoryParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listByActivity.txt";
        String methodName = "listByActivity";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {

            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requestJsonString);
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listPeople method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listByActivity} integration test with mandatory and optional parameters.")
    public void testListByActivityWithOptionalParams() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listByActivityOptionalParams.txt";
        String methodName = "listByActivity";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requestJsonString);
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listByActivity method with maxResults Optional Parameter.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listByActivity} integration test with mandatory and maxResults optional parameter.")
    public void testListByActivityWithOneOptionalParam1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listByActivityOptionalParams.txt";
        String methodName = "listByActivity";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            String[] unneededOptionalParameters = {"pageToken", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(requestJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listByActivity method with pageToken Optional Parameter.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listByActivity} integration test with mandatory and pageToken optional parameter.")
    public void testListByActivityWithOneOptionalParam2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listByActivityOptionalParams.txt";
        String methodName = "listByActivity";
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
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listByActivity method with fields Optional Parameter.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listByActivity} integration test with mandatory and fields optional parameter.")
    public void testListByActivityWithOneOptionalParam3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listByActivityOptionalParams.txt";
        String methodName = "listByActivity";
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
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listByActivity method with maxResults,pageToken Optional Parameters.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listByActivity} integration test with mandatory and maxResults,pageToken optional parameters.")
    public void testListByActivityWithTwoOptionalParam1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listByActivityOptionalParams.txt";
        String methodName = "listByActivity";
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
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listByActivity method with pageToken,fields Optional Parameters.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listByActivity} integration test with mandatory and pageToken,,fields optional parameters.")
    public void testListByActivityWithTwoOptionalParam2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listByActivityOptionalParams.txt";
        String methodName = "listByActivity";
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
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listByActivity method with maxResults,fields Optional Parameters.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listByActivity} integration test with mandatory and maxResults,fields optional parameters.")
    public void testListByActivityWithTwoOptionalParam3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listByActivityOptionalParams.txt";
        String methodName = "listByActivity";
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
            Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative parameter test case for listPeople method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listByActivity} integration test with Negative parameters.")
    public void testListByActivityWithNegativeParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listByActivityUnhappy.txt";
        String methodName = "listByActivity";
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

}
