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
 * This class contains integration test cases related to Hooks in codeplex connector for esb
 */
public class CodeplexHooksTest extends ESBIntegrationTest {

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
     * This class will log in to esb instance as admin user to execute codeplex hooks
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


    /**
     * Test case for getSupportedHooks method.
     *
     * @throws java.lang.Exception
     */
    @Test(groups = {"wso2.esb"}, priority = 8, description = "codeplex {getSupportedHooks} integration test.")
    public void testGetSupportedHooks() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getSupportedHooks_mandatory.txt";
        String methodName = "codeplexGetSupportedHooksProxy";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        String modifiedJsonString = String.format(jsonString,
                                                  codeplexConnectorProperties.getProperty("clientId"),
                                                  codeplexConnectorProperties.getProperty("clientSecret"),
                                                  codeplexConnectorProperties.getProperty("refreshToken"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        JSONObject jsonResponse;
        int supportedHookCount;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            supportedHookCount = Integer.parseInt(jsonResponse.get("Count").toString());
            Assert.assertTrue(supportedHookCount >= 1);
        } finally {
            proxyAdmin.deleteProxy(methodName);

        }
    }

    /**
     * Test negative case for getSupportedHooks method.
     *
     * @throws java.lang.Exception
     */
    @Test(groups = {"wso2.esb"}, priority = 9,
          description = "codeplex {getSupportedHooks} integration test for negative case.")
    public void testGetSupportedHooksWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSupportedHooks_negative.txt";
        String methodName = "codeplexGetSupportedHooksProxy";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        String modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                                  codeplexConnectorProperties.getProperty("clientSecret"),
                                                  codeplexConnectorProperties.getProperty("refreshToken"));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);

            Assert.assertEquals(jsonResponse.getString("Message"), "Project does not exist.");
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


    /**
     * Test case for createSubscription method with mandatory parameters.
     * @throws java.lang.Exception
     */
    @Test(groups = {"wso2.esb"}, priority = 10,
          description = "codeplex {createSubscription} integration test with mandatory parameters.")
    public void testCreateSubscriptionWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "createSubscriptions_mandatory.txt";
        String methodName = "codeplexCreateSubscriptionsProxy";

        String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        String modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                                  codeplexConnectorProperties.getProperty("clientSecret"),
                                                  codeplexConnectorProperties.getProperty("refreshToken"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        JSONObject jsonResponse;
        int subscriptionId;

        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            subscriptionId = Integer.parseInt(jsonResponse.get("Id").toString());

            Assert.assertTrue(subscriptionId >= 1);
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }

        jsonRequestFilePath = pathToRequestsDirectory + "deleteSubscription_mandatory.txt";
        methodName = "codeplexDeleteSubscriptionProxy";

        jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                           codeplexConnectorProperties.getProperty("clientSecret"),
                                           codeplexConnectorProperties.getProperty("refreshToken"), subscriptionId);
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Test case for createSubscription method with optional parameters.
     * @throws java.lang.Exception
     */
    @Test(groups = {"wso2.esb"}, priority = 10,
          description = "codeplex {createSubscription} integration test with optional parameters.")
    public void testCreateSubscriptionWithOptionalParameters() throws Exception {

        log.info("sleep 10 seconds as codeplex api have limit for number of rest calls");
        Thread.sleep(10000);

        String jsonRequestFilePath = pathToRequestsDirectory + "createSubscriptions_optional.txt";
        String methodName = "codeplexCreateSubscriptionsProxy";

        String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        String modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                                  codeplexConnectorProperties.getProperty("clientSecret"),
                                                  codeplexConnectorProperties.getProperty("refreshToken"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        JSONObject jsonResponse;
        int subscriptionId;

        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            subscriptionId = Integer.parseInt(jsonResponse.get("Id").toString());

            Assert.assertTrue(subscriptionId >= 1);
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }

        jsonRequestFilePath = pathToRequestsDirectory + "deleteSubscription_mandatory.txt";
        methodName = "codeplexDeleteSubscriptionProxy";

        jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                           codeplexConnectorProperties.getProperty("clientSecret"),
                                           codeplexConnectorProperties.getProperty("refreshToken"), subscriptionId);
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Negative test case for createSubscription method.
     * @throws java.lang.Exception
     */
    @Test(groups = {"wso2.esb"}, priority = 11,
          description = "codeplex {createSubscription} integration test with negative test case.")
    public void testCreateSubscriptionWithNegativeCase() throws Exception {

        log.info("sleep 10 seconds as codeplex api have limit for number of rest calls");
        Thread.sleep(10000);

        String jsonRequestFilePath = pathToRequestsDirectory + "createSubscriptions_negative.txt";
        String methodName = "codeplexCreateSubscriptionsProxy";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        String modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                                  codeplexConnectorProperties.getProperty("clientSecret"),
                                                  codeplexConnectorProperties.getProperty("refreshToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        JSONObject response;

        try {
            response = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals(response.getString("Message"), "There was an issue with one or more input values.");
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for getSubscription method with mandatory parameters.
     * @throws java.lang.Exception
     */
    @Test(groups = {"wso2.esb"}, priority = 12,
          description = "codeplex {getSubscription} integration test with mandatory parameters.")
    public void testGetSubscriptionWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "createSubscriptions_mandatory.txt";
        String methodName = "codeplexCreateSubscriptionsProxy";

        String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        String modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                                  codeplexConnectorProperties.getProperty("clientSecret"),
                                                  codeplexConnectorProperties.getProperty("refreshToken"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        JSONObject jsonResponse;
        int subscriptionId;

        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            subscriptionId = Integer.parseInt(jsonResponse.get("Id").toString());

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

        jsonRequestFilePath = pathToRequestsDirectory + "getSubscription_mandatory.txt";
        methodName = "codeplexGetSubscriptionProxy";

        jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                           codeplexConnectorProperties.getProperty("clientSecret"),
                                           codeplexConnectorProperties.getProperty("refreshToken"), subscriptionId);

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));


        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals(jsonResponse.getInt("Id"), subscriptionId);
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }

        jsonRequestFilePath = pathToRequestsDirectory + "deleteSubscription_mandatory.txt";
        methodName = "codeplexDeleteSubscriptionProxy";

        jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                           codeplexConnectorProperties.getProperty("clientSecret"),
                                           codeplexConnectorProperties.getProperty("refreshToken"), subscriptionId);
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Negative test case for getSubscription method.
     * @throws java.lang.Exception
     */
    @Test(groups = {"wso2.esb"}, priority = 13,
          description = "codeplex {getSubscription} integration test with negative test case.")
    public void testGetSubscriptionWithNegativeCase() throws Exception {

        log.info("sleep 10 seconds as codeplex api have limit for number of rest calls");
        Thread.sleep(10000);

        String jsonRequestFilePath = pathToRequestsDirectory + "getSubscription_negative.txt";
        String methodName = "codeplexGetSubscriptionProxy";

        String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        String modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                                  codeplexConnectorProperties.getProperty("clientSecret"),
                                                  codeplexConnectorProperties.getProperty("refreshToken"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        JSONObject jsonResponse;

        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals(jsonResponse.getString("Message"), "The subscription was not found.");
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


    /**
     * Test case for getHookSubscriptions method with mandatory parameters.
     * @throws java.lang.Exception
     */
    @Test(groups = {"wso2.esb"}, priority = 14,
          description = "codeplex {getHookSubscriptions} integration test with mandatory parameters.")
    public void testGetHookSubscriptionsWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "createSubscriptions_mandatory.txt";
        String methodName = "codeplexCreateSubscriptionsProxy";

        String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        String modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                                  codeplexConnectorProperties.getProperty("clientSecret"),
                                                  codeplexConnectorProperties.getProperty("refreshToken"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        JSONObject jsonResponse;
        int subscriptionId;

        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            subscriptionId = Integer.parseInt(jsonResponse.get("Id").toString());

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

        jsonRequestFilePath = pathToRequestsDirectory + "getHookSubscriptions_mandatory.txt";
        methodName = "codeplexGetHookSubscriptionsProxy";

        jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                           codeplexConnectorProperties.getProperty("clientSecret"),
                                           codeplexConnectorProperties.getProperty("refreshToken"), subscriptionId);
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.getInt("Count") > 0);
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }

        jsonRequestFilePath = pathToRequestsDirectory + "deleteSubscription_mandatory.txt";
        methodName = "codeplexDeleteSubscriptionProxy";

        jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                           codeplexConnectorProperties.getProperty("clientSecret"),
                                           codeplexConnectorProperties.getProperty("refreshToken"), subscriptionId);
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Negative test case for getHookSubscriptions method.
     * @throws java.lang.Exception
     */
    @Test(groups = {"wso2.esb"}, priority = 15,
          description = "codeplex {getHookSubscriptions} integration test with negative test case.")
    public void testGetHookSubscriptionsWithNegativeCase() throws Exception {

        log.info("sleep 10 seconds as codeplex api have limit for number of rest calls");
        Thread.sleep(10000);

        String jsonRequestFilePath = pathToRequestsDirectory + "createSubscriptions_mandatory.txt";
        String methodName = "codeplexCreateSubscriptionsProxy";

        String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        String modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                                  codeplexConnectorProperties.getProperty("clientSecret"),
                                                  codeplexConnectorProperties.getProperty("refreshToken"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        JSONObject jsonResponse;
        int subscriptionId;

        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            subscriptionId = Integer.parseInt(jsonResponse.get("Id").toString());

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

        jsonRequestFilePath = pathToRequestsDirectory + "getHookSubscriptions_negative.txt";
        methodName = "codeplexGetHookSubscriptionsProxy";

        jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                           codeplexConnectorProperties.getProperty("clientSecret"),
                                           codeplexConnectorProperties.getProperty("refreshToken"),
                                           subscriptionId);
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals(jsonResponse.getString("Message"), "Project does not exist.");
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Test case for DeleteSubscription method with mandatory parameters.
     * @throws java.lang.Exception
     */
    @Test(groups = {"wso2.esb"}, priority = 16,
          description = "codeplex {DeleteSubscription} integration test with mandatory parameters.")
    public void testDeleteSubscriptionWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "createSubscriptions_mandatory.txt";
        String methodName = "codeplexCreateSubscriptionsProxy";

        String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        String modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                                  codeplexConnectorProperties.getProperty("clientSecret"),
                                                  codeplexConnectorProperties.getProperty("refreshToken"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        JSONObject jsonResponse;
        int subscriptionId;

        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            subscriptionId = Integer.parseInt(jsonResponse.get("Id").toString());

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

        jsonRequestFilePath = pathToRequestsDirectory + "deleteSubscription_mandatory.txt";
        methodName = "codeplexDeleteSubscriptionProxy";

        jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                           codeplexConnectorProperties.getProperty("clientSecret"),
                                           codeplexConnectorProperties.getProperty("refreshToken"),
                                           subscriptionId);
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        int responseHeader;
        try {

            responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
                                                                                  modifiedJsonString);
            Assert.assertEquals(responseHeader, 204);
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Negative test case for DeleteSubscription method.
     * @throws java.lang.Exception
     */
    @Test(groups = {"wso2.esb"}, priority = 17,
          description = "codeplex {DeleteSubscription} integration test for negative case.")
    public void testDeleteSubscriptionWithNegativeCase() throws Exception {

        log.info("sleep 10 seconds as codeplex api have limit for number of rest calls");

        Thread.sleep(10000);

        String jsonRequestFilePath = pathToRequestsDirectory + "deleteSubscription_negative.txt";
        String methodName = "codeplexDeleteSubscriptionProxy";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        String modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                                  codeplexConnectorProperties.getProperty("clientSecret"),
                                                  codeplexConnectorProperties.getProperty("refreshToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        int responseHeader;

        try {

            responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
                                                                                  modifiedJsonString);
            Assert.assertEquals(responseHeader, 404);

        } finally {

            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Test case for updateSubscription method with mandatory parameters.
     * @throws java.lang.Exception
     */
    @Test(groups = {"wso2.esb"}, priority = 18,
          description = "codeplex {updateSubscription} integration test with mandatory parameters.")
    public void testUpdateSubscriptionWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "createSubscriptions_mandatory.txt";
        String methodName = "codeplexCreateSubscriptionsProxy";

        String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        String modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                                  codeplexConnectorProperties.getProperty("clientSecret"),
                                                  codeplexConnectorProperties.getProperty("refreshToken"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        JSONObject jsonResponse;
        int subscriptionId;

        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            subscriptionId = Integer.parseInt(jsonResponse.get("Id").toString());

        } finally {

            proxyAdmin.deleteProxy(methodName);
        }

        jsonRequestFilePath = pathToRequestsDirectory + "updateSubscription_mandatory.txt";
        methodName = "codeplexUpdateSubscriptionProxy";

        jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                           codeplexConnectorProperties.getProperty("clientSecret"),
                                           codeplexConnectorProperties.getProperty("refreshToken"),
                                           subscriptionId);
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        JSONObject response;
        try {

            response = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals(response.getJSONArray("Events").getJSONObject(0).getString("Name"), "Code Change");
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }

        jsonRequestFilePath = pathToRequestsDirectory + "deleteSubscription_mandatory.txt";
        methodName = "codeplexDeleteSubscriptionProxy";

        jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                           codeplexConnectorProperties.getProperty("clientSecret"),
                                           codeplexConnectorProperties.getProperty("refreshToken"),
                                           subscriptionId);
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Test case for updateSubscription method with all optional parameters.
     * @throws java.lang.Exception
     */
    @Test(groups = {"wso2.esb"}, priority = 19,
          description = "codeplex {updateSubscription} integration test with all optional parameters.")
    public void testUpdateSubscriptionWithOptionalParameters() throws Exception {

        log.info("sleep 10 seconds as codeplex api have limit for number of rest calls");
        Thread.sleep(10000);

        String jsonRequestFilePath = pathToRequestsDirectory + "createSubscriptions_mandatory.txt";
        String methodName = "codeplexCreateSubscriptionsProxy";

        String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        String modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                                  codeplexConnectorProperties.getProperty("clientSecret"),
                                                  codeplexConnectorProperties.getProperty("refreshToken"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        JSONObject jsonResponse;
        int subscriptionId;

        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            subscriptionId = Integer.parseInt(jsonResponse.get("Id").toString());

        } finally {

            proxyAdmin.deleteProxy(methodName);
        }

        jsonRequestFilePath = pathToRequestsDirectory + "updateSubscription_all_optional.txt";
        methodName = "codeplexUpdateSubscriptionProxy";

        jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                           codeplexConnectorProperties.getProperty("clientSecret"),
                                           codeplexConnectorProperties.getProperty("refreshToken"),
                                           subscriptionId);
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        JSONObject response;
        try {

            response = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals(response.getJSONArray("Events").getJSONObject(0).getString("Name"), "Code Change");
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }

        jsonRequestFilePath = pathToRequestsDirectory + "deleteSubscription_mandatory.txt";
        methodName = "codeplexDeleteSubscriptionProxy";

        jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                           codeplexConnectorProperties.getProperty("clientSecret"),
                                           codeplexConnectorProperties.getProperty("refreshToken"),
                                           subscriptionId);
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Negative test case for updateSubscription method.
     * @throws java.lang.Exception
     */
    @Test(groups = {"wso2.esb"}, priority = 20,
          description = "codeplex {updateSubscription} integration test for negative case.")
    public void testUpdateSubscriptionWithNegativeCase() throws Exception {

        log.info("sleep 10 seconds as codeplex api have limit for number of rest calls");
        Thread.sleep(10000);

        String jsonRequestFilePath = pathToRequestsDirectory + "updateSubscription_negative.txt";
        String methodName = "codeplexUpdateSubscriptionProxy";

        String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        String modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                                  codeplexConnectorProperties.getProperty("clientSecret"),
                                                  codeplexConnectorProperties.getProperty("refreshToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        JSONObject response;
        try {

            response = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals(response.getString("Message"), "Project does not exist.");
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }


    }


    /**
     * Test case for updateSubscription method with optional parameter one.
     * @throws java.lang.Exception
     */
    @Test(groups = {"wso2.esb"}, priority = 21,
          description = "codeplex {updateSubscription} integration test with optional parameter one.")
    public void testUpdateSubscriptionWithOptionalParameterOne() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "createSubscriptions_mandatory.txt";
        String methodName = "codeplexCreateSubscriptionsProxy";

        String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        String modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                                  codeplexConnectorProperties.getProperty("clientSecret"),
                                                  codeplexConnectorProperties.getProperty("refreshToken"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        JSONObject jsonResponse;
        int subscriptionId;

        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            subscriptionId = Integer.parseInt(jsonResponse.get("Id").toString());

        } finally {

            proxyAdmin.deleteProxy(methodName);
        }

        jsonRequestFilePath = pathToRequestsDirectory + "updateSubscription_optional_1.txt";
        methodName = "codeplexUpdateSubscriptionProxy";

        jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                           codeplexConnectorProperties.getProperty("clientSecret"),
                                           codeplexConnectorProperties.getProperty("refreshToken"),
                                           subscriptionId);
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        JSONObject response;
        try {

            response = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals(response.getJSONArray("Events").getJSONObject(0).getString("Name"), "Code Change");
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }

        jsonRequestFilePath = pathToRequestsDirectory + "deleteSubscription_mandatory.txt";
        methodName = "codeplexDeleteSubscriptionProxy";

        jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                           codeplexConnectorProperties.getProperty("clientSecret"),
                                           codeplexConnectorProperties.getProperty("refreshToken"),
                                           subscriptionId);
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Test case for updateSubscription method with optional parameter two.
     * @throws java.lang.Exception
     */
    @Test(groups = {"wso2.esb"}, priority = 21,
          description = "codeplex {updateSubscription} integration test with optional parameter two.")
    public void testUpdateSubscriptionWithOptionalParameterTwo() throws Exception {

        log.info("sleep 10 seconds as codeplex api have limit for number of rest calls");

        Thread.sleep(10000);

        String jsonRequestFilePath = pathToRequestsDirectory + "createSubscriptions_mandatory.txt";
        String methodName = "codeplexCreateSubscriptionsProxy";

        String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        String modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                                  codeplexConnectorProperties.getProperty("clientSecret"),
                                                  codeplexConnectorProperties.getProperty("refreshToken"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        JSONObject jsonResponse;
        int subscriptionId;

        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            subscriptionId = Integer.parseInt(jsonResponse.get("Id").toString());

        } finally {

            proxyAdmin.deleteProxy(methodName);
        }

        jsonRequestFilePath = pathToRequestsDirectory + "updateSubscription_optional_2.txt";
        methodName = "codeplexUpdateSubscriptionProxy";

        jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                           codeplexConnectorProperties.getProperty("clientSecret"),
                                           codeplexConnectorProperties.getProperty("refreshToken"),
                                           subscriptionId);
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        JSONObject response;
        try {

            response = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals(response.getJSONArray("Events").getJSONObject(0).getString("Name"), "Code Change");
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }

        jsonRequestFilePath = pathToRequestsDirectory + "deleteSubscription_mandatory.txt";
        methodName = "codeplexDeleteSubscriptionProxy";

        jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                           codeplexConnectorProperties.getProperty("clientSecret"),
                                           codeplexConnectorProperties.getProperty("refreshToken"),
                                           subscriptionId);
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


    /**
     * Test case for updateSubscription method with optional parameter three.
     * @throws java.lang.Exception
     */
    @Test(groups = {"wso2.esb"}, priority = 22,
          description = "codeplex {updateSubscription} integration test with optional parameter three.")
    public void testUpdateSubscriptionWithOptionalParameterThree() throws Exception {

        log.info("sleep 10 seconds as codeplex api have limit for number of rest calls");

        Thread.sleep(10000);

        String jsonRequestFilePath = pathToRequestsDirectory + "createSubscriptions_mandatory.txt";
        String methodName = "codeplexCreateSubscriptionsProxy";

        String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        String modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                                  codeplexConnectorProperties.getProperty("clientSecret"),
                                                  codeplexConnectorProperties.getProperty("refreshToken"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        JSONObject jsonResponse;
        int subscriptionId;

        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            subscriptionId = Integer.parseInt(jsonResponse.get("Id").toString());

        } finally {

            proxyAdmin.deleteProxy(methodName);
        }

        jsonRequestFilePath = pathToRequestsDirectory + "updateSubscription_optional_3.txt";
        methodName = "codeplexUpdateSubscriptionProxy";

        jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                           codeplexConnectorProperties.getProperty("clientSecret"),
                                           codeplexConnectorProperties.getProperty("refreshToken"),
                                           subscriptionId);
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        JSONObject response;
        try {

            response = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals(response.getJSONArray("Events").getJSONObject(0).getString("Name"), "Code Change");
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }

        jsonRequestFilePath = pathToRequestsDirectory + "deleteSubscription_mandatory.txt";
        methodName = "codeplexDeleteSubscriptionProxy";

        jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";

        modifiedJsonString = String.format(jsonString, codeplexConnectorProperties.getProperty("clientId"),
                                           codeplexConnectorProperties.getProperty("clientSecret"),
                                           codeplexConnectorProperties.getProperty("refreshToken"),
                                           subscriptionId);
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }


}
