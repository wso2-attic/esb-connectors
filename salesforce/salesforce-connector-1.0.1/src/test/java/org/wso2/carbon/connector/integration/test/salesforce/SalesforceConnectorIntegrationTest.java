/**
 * Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.integration.test.salesforce;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.context.ConfigurationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.proxy.admin.ProxyServiceAdminClient;
import org.wso2.carbon.automation.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.test.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.automation.utils.axis2client.ConfigurationContextProvider;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.carbon.esb.ESBIntegrationTest;
import org.wso2.carbon.mediation.library.stub.MediationLibraryAdminServiceStub;
import org.wso2.carbon.mediation.library.stub.upload.MediationLibraryUploaderStub;

import javax.activation.DataHandler;
import java.io.File;
import java.net.URL;
import java.util.Properties;

public class SalesforceConnectorIntegrationTest extends ESBIntegrationTest {

    private static final String CONNECTOR_NAME = "salesforce";

    private MediationLibraryUploaderStub mediationLibUploadStub = null;

    private MediationLibraryAdminServiceStub adminServiceStub = null;

    private String pathToProxiesDirectory = null;

    private String pathToRequestsDirectory = null;

    private ProxyServiceAdminClient proxyAdmin;

    private String repoLocation = null;

    private Properties salesforceConnectorProperties = null;

    private String salesforceConnectorFileName = "salesforce-connector-1.0.1.zip";

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
            repoLocation = System.getProperty("connector_repo").replace("/", "\\");
        } else {
            repoLocation = System.getProperty("connector_repo").replace("/", "/");
        }

        proxyAdmin =
                new ProxyServiceAdminClient(esbServer.getBackEndUrl(),
                        esbServer.getSessionCookie());

        ConnectorIntegrationUtil.uploadConnector(repoLocation, mediationLibUploadStub,
                salesforceConnectorFileName);
        Thread.sleep(30000);

        adminServiceStub.updateStatus("{org.wso2.carbon.connectors}" + CONNECTOR_NAME, CONNECTOR_NAME,
                "org.wso2.carbon.connectors", "enabled");

        salesforceConnectorProperties =
                ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);

        pathToProxiesDirectory = repoLocation + salesforceConnectorProperties.getProperty("proxyDirectoryRelativePath");
        pathToRequestsDirectory = repoLocation + salesforceConnectorProperties.getProperty("requestDirectoryRelativePath");


    }

    @Test(groups = {"wso2.esb"}, enabled = false, description = "Salesforce {describeGlobal} integration test.")
    public void testSalesforceDescribeGlobal() throws Exception {
        final String methodName = "describeGlobal";
        final String omString = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://connector.esb.wso2.org\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "   <per:config>\n" +
                "   <per:clientId>" +
                salesforceConnectorProperties.get("clientId") +
                "</per:clientId>\n" +
                "   <per:clientSecret>" +
                salesforceConnectorProperties.get("clientSecret") +
                "</per:clientSecret>\n" +
                "   <per:refreshToken>" +
                salesforceConnectorProperties.get("refreshToken") +
                "</per:refreshToken>\n" +
                "   <per:apiVersion>" +
                salesforceConnectorProperties.get("apiVersion") +
                "</per:apiVersion>\n" +
                "   </per:config>\n" +
                "   </soapenv:Body>\n" + "</soapenv:Envelope>";

        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator
                + CONNECTOR_NAME + "_" + methodName + ".xml")));

        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getRequest = AXIOMUtil.stringToOM(omString);
        String proxyName = CONNECTOR_NAME + "_" + methodName;
        OMElement response =
                axisServiceClient.sendReceive(getRequest,
                        getProxyServiceURL(proxyName), "mediate");

        try {
            Assert.assertTrue(response.toString().contains("describeGlobalResponse"));
            Assert.assertEquals(((OMElement) (((((OMElement) (response.getChildrenWithLocalName("result")
                    .next())).getChildrenWithLocalName("maxBatchSize").next())))).getText(), "200");
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }

    @Test(groups = {"wso2.esb"}, enabled = true, description = "Salesforce {getUserInfo} integration test.")
    public void testSalesforceGetUserInfo() throws Exception {

        final String methodName = "getUserInfo";
        final String omString = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://connector.esb.wso2.org\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "   <per:config>\n" +
                "   <per:clientId>" +
                salesforceConnectorProperties.get("clientId") +
                "</per:clientId>\n" +
                "   <per:clientSecret>" +
                salesforceConnectorProperties.get("clientSecret") +
                "</per:clientSecret>\n" +
                "   <per:refreshToken>" +
                salesforceConnectorProperties.get("refreshToken") +
                "</per:refreshToken>\n" +
                "   <per:apiVersion>" +
                salesforceConnectorProperties.get("apiVersion") +
                "</per:apiVersion>\n" +
                "   </per:config>\n" +
                "   </soapenv:Body>\n" + "</soapenv:Envelope>";

        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator
                + CONNECTOR_NAME + "_" + methodName + ".xml")));

        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getRequest = AXIOMUtil.stringToOM(omString);
        String proxyName = CONNECTOR_NAME + "_" + methodName;
        OMElement response =
                axisServiceClient.sendReceive(getRequest,
                        getProxyServiceURL(proxyName), "mediate");

        try {
            Assert.assertTrue(response.toString().contains("getUserInfoResponse"));
            Assert.assertTrue(((OMElement) (((((OMElement) (response.getChildrenWithLocalName("result")
                    .next())).getChildrenWithLocalName("userId").next())))).getText().length() > 0);
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }

    @Test(groups = {"wso2.esb"}, enabled = false, description = "Salesforce {describeSObject} integration test.")
    public void testSalesforceDescribeSObject() throws Exception {

        final String methodName = "describeSobject";
        final String omString = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://connector.esb.wso2.org\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "   <per:config>\n" +
                "   <per:clientId>" +
                salesforceConnectorProperties.get("clientId") +
                "</per:clientId>\n" +
                "   <per:clientSecret>" +
                salesforceConnectorProperties.get("clientSecret") +
                "</per:clientSecret>\n" +
                "   <per:refreshToken>" +
                salesforceConnectorProperties.get("refreshToken") +
                "</per:refreshToken>\n" +
                "   <per:apiVersion>" +
                salesforceConnectorProperties.get("apiVersion") +
                "</per:apiVersion>\n" +
                "   </per:config>\n" +
                "   <per:describeSobject>\n" +
                "   <per:sObject>" +
                salesforceConnectorProperties.get("sObject") +
                "</per:sObject>\n" +
                "   </per:describeSobject>\n" +
                "   </soapenv:Body>\n" + "</soapenv:Envelope>";

        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator
                + CONNECTOR_NAME + "_" + methodName + ".xml")));

        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getRequest = AXIOMUtil.stringToOM(omString);
        String proxyName = CONNECTOR_NAME + "_" + methodName;
        OMElement response =
                axisServiceClient.sendReceive(getRequest,
                        getProxyServiceURL(proxyName), "mediate");

        try {
            Assert.assertTrue(response.toString().contains("describeSObjectResponse"));
            Assert.assertEquals(((OMElement) (((((OMElement) (response.getChildrenWithLocalName("result")
                    .next())).getChildrenWithLocalName("name").next())))).getText(), salesforceConnectorProperties.get("sObject"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }

    @Test(groups = {"wso2.esb"}, enabled = false, description = "Salesforce {describeSObjects} integration test.")
    public void testSalesforceDescribeSObjects() throws Exception {

        final String methodName = "describeSobjects";
        final String omString = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://connector.esb.wso2.org\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "   <per:config>\n" +
                "   <per:clientId>" +
                salesforceConnectorProperties.get("clientId") +
                "</per:clientId>\n" +
                "   <per:clientSecret>" +
                salesforceConnectorProperties.get("clientSecret") +
                "</per:clientSecret>\n" +
                "   <per:refreshToken>" +
                salesforceConnectorProperties.get("refreshToken") +
                "</per:refreshToken>\n" +
                "   <per:apiVersion>" +
                salesforceConnectorProperties.get("apiVersion") +
                "</per:apiVersion>\n" +
                "   </per:config>\n" +
                "   <per:describeSobjects>\n" +
                "   <per:sobject1>" +
                salesforceConnectorProperties.get("sObjectType1") +
                "</per:sobject1>\n" +
                "   <per:sobject2>" +
                salesforceConnectorProperties.get("sObjectType2") +
                "</per:sobject2>\n" +
                "   </per:describeSobjects>\n" +
                "   </soapenv:Body>\n" + "</soapenv:Envelope>";

        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator
                + CONNECTOR_NAME + "_" + methodName + ".xml")));

        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getRequest = AXIOMUtil.stringToOM(omString);
        String proxyName = CONNECTOR_NAME + "_" + methodName;
        OMElement response =
                axisServiceClient.sendReceive(getRequest,
                        getProxyServiceURL(proxyName), "mediate");

        try {
            Assert.assertTrue(response.toString().contains("describeSObjectsResponse"));
            Assert.assertEquals(((OMElement) (((((OMElement) (response.getChildrenWithLocalName("result")
                    .next())).getChildrenWithLocalName("name").next())))).getText(), salesforceConnectorProperties.get("sObjectType1"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }

    @Test(groups = {"wso2.esb"}, enabled = false, description = "Salesforce {query} integration test.")
    public void testSalesforceQuery() throws Exception {
        final String methodName = "query";
        final String omString = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://connector.esb.wso2.org\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "   <per:config>\n" +
                "   <per:clientId>" +
                salesforceConnectorProperties.get("clientId") +
                "</per:clientId>\n" +
                "   <per:clientSecret>" +
                salesforceConnectorProperties.get("clientSecret") +
                "</per:clientSecret>\n" +
                "   <per:refreshToken>" +
                salesforceConnectorProperties.get("refreshToken") +
                "</per:refreshToken>\n" +
                "   <per:apiVersion>" +
                salesforceConnectorProperties.get("apiVersion") +
                "</per:apiVersion>\n" +
                "   </per:config>\n" +
                "   <per:query>\n" +
                "   <per:batchSize>" +
                salesforceConnectorProperties.get("batchSize") +
                "</per:batchSize>\n" +
                "   <per:queryString>" +
                salesforceConnectorProperties.get("queryString") +
                "</per:queryString>\n" +
                "   </per:query>\n" +
                "   </soapenv:Body>\n" + "</soapenv:Envelope>";
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator
                + CONNECTOR_NAME + "_" + methodName + ".xml")));

        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getRequest = AXIOMUtil.stringToOM(omString);
        String proxyName = CONNECTOR_NAME + "_" + methodName;
        OMElement response =
                axisServiceClient.sendReceive(getRequest,
                        getProxyServiceURL(proxyName), "mediate");
        try {
            Assert.assertTrue(response.toString().contains("queryResponse"));
            Assert.assertTrue(((OMElement) (((((OMElement) (response.getChildrenWithLocalName("result")
                    .next())).getChildrenWithLocalName("done").next())))).getText().length() > 0);
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }

    @Test(groups = {"wso2.esb"}, enabled = false, description = "Salesforce {queryAll} integration test.")
    public void testSalesforceQueryAll() throws Exception {
        final String methodName = "queryAll";
        final String omString = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://connector.esb.wso2.org\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "   <per:config>\n" +
                "   <per:clientId>" +
                salesforceConnectorProperties.get("clientId") +
                "</per:clientId>\n" +
                "   <per:clientSecret>" +
                salesforceConnectorProperties.get("clientSecret") +
                "</per:clientSecret>\n" +
                "   <per:refreshToken>" +
                salesforceConnectorProperties.get("refreshToken") +
                "</per:refreshToken>\n" +
                "   <per:apiVersion>" +
                salesforceConnectorProperties.get("apiVersion") +
                "</per:apiVersion>\n" +
                "   </per:config>\n" +
                "   <per:queryAll>\n" +
                "   <per:batchSize>" +
                salesforceConnectorProperties.get("batchSize") +
                "</per:batchSize>\n" +
                "   <per:queryString>" +
                salesforceConnectorProperties.get("queryString") +
                "</per:queryString>\n" +
                "   </per:queryAll>\n" +
                "   </soapenv:Body>\n" + "</soapenv:Envelope>";
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator
                + CONNECTOR_NAME + "_" + methodName + ".xml")));

        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getRequest = AXIOMUtil.stringToOM(omString);
        String proxyName = CONNECTOR_NAME + "_" + methodName;
        OMElement response =
                axisServiceClient.sendReceive(getRequest,
                        getProxyServiceURL(proxyName), "mediate");
        try {
            Assert.assertTrue(response.toString().contains("queryAllResponse"));
            Assert.assertTrue(((OMElement) (((((OMElement) (response.getChildrenWithLocalName("result")
                    .next())).getChildrenWithLocalName("done").next())))).getText().length() > 0);
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }

    @Test(groups = {"wso2.esb"}, enabled = false, description = "Salesforce {queryMore} integration test.")
    public void testSalesforceQueryMore() throws Exception {
        final String methodName = "queryMore";
        final String omString = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://connector.esb.wso2.org\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "   <per:config>\n" +
                "   <per:clientId>" +
                salesforceConnectorProperties.get("clientId") +
                "</per:clientId>\n" +
                "   <per:clientSecret>" +
                salesforceConnectorProperties.get("clientSecret") +
                "</per:clientSecret>\n" +
                "   <per:refreshToken>" +
                salesforceConnectorProperties.get("refreshToken") +
                "</per:refreshToken>\n" +
                "   <per:apiVersion>" +
                salesforceConnectorProperties.get("apiVersion") +
                "</per:apiVersion>\n" +
                "   </per:config>\n" +
                "   <per:queryMore>\n" +
                "   <per:batchSize>" +
                salesforceConnectorProperties.get("batchSize") +
                "</per:batchSize>\n" +
                "   <per:queryString>" +
                salesforceConnectorProperties.get("queryString") +
                "</per:queryString>\n" +
                "   </per:queryMore>\n" +
                "   </soapenv:Body>\n" + "</soapenv:Envelope>";
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator
                + CONNECTOR_NAME + "_" + methodName + ".xml")));

        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getRequest = AXIOMUtil.stringToOM(omString);
        String proxyName = CONNECTOR_NAME + "_" + methodName;
        OMElement response =
                axisServiceClient.sendReceive(getRequest,
                        getProxyServiceURL(proxyName), "mediate");
        try {
            Assert.assertTrue(((OMElement) (((((OMElement) (response.getChildrenWithLocalName("result")
                    .next())).getChildrenWithLocalName("queryLocator").next())))).getText().length() > 0);
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }

    @Test(groups = {"wso2.esb"}, enabled = false, description = "Salesforce {search} integration test.")
    public void testSalesforceSearch() throws Exception {
        final String methodName = "search";
        final String omString = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://connector.esb.wso2.org\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "   <per:config>\n" +
                "   <per:clientId>" +
                salesforceConnectorProperties.get("clientId") +
                "</per:clientId>\n" +
                "   <per:clientSecret>" +
                salesforceConnectorProperties.get("clientSecret") +
                "</per:clientSecret>\n" +
                "   <per:refreshToken>" +
                salesforceConnectorProperties.get("refreshToken") +
                "</per:refreshToken>\n" +
                "   <per:apiVersion>" +
                salesforceConnectorProperties.get("apiVersion") +
                "</per:apiVersion>\n" +
                "   </per:config>\n" +
                "   <per:search>\n" +
                "   <per:searchString>" +
                salesforceConnectorProperties.get("searchString") +
                "</per:searchString>\n" +
                "   </per:search>\n" +
                "   </soapenv:Body>\n" + "</soapenv:Envelope>";
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator
                + CONNECTOR_NAME + "_" + methodName + ".xml")));

        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getRequest = AXIOMUtil.stringToOM(omString);
        String proxyName = CONNECTOR_NAME + "_" + methodName;
        OMElement response =
                axisServiceClient.sendReceive(getRequest,
                        getProxyServiceURL(proxyName), "mediate");
        try {
            Assert.assertTrue(response.toString().contains("searchResponse"));
            Assert.assertTrue(response.toString().contains("searchRecords"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }

    @Test(groups = {"wso2.esb"}, enabled = false, description = "Salesforce {sendMail} integration test.")
    public void testSalesforceSendMail() throws Exception {
        final String methodName = "sendEMail";

        final String omString = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://connector.esb.wso2.org\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "   <per:config>\n" +
                "   <per:clientId>" +
                salesforceConnectorProperties.get("clientId") +
                "</per:clientId>\n" +
                "   <per:clientSecret>" +
                salesforceConnectorProperties.get("clientSecret") +
                "</per:clientSecret>\n" +
                "   <per:refreshToken>" +
                salesforceConnectorProperties.get("refreshToken") +
                "</per:refreshToken>\n" +
                "   <per:apiVersion>" +
                salesforceConnectorProperties.get("apiVersion") +
                "</per:apiVersion>\n" +
                "   <per:bccSender>" +
                salesforceConnectorProperties.get("bccSender") +
                "</per:bccSender>\n" +
                "   <per:emailPriority>" +
                salesforceConnectorProperties.get("emailPriority") +
                "</per:emailPriority>\n" +
                "   <per:replyTo>" +
                salesforceConnectorProperties.get("replyTo") +
                "</per:replyTo>\n" +
                "   <per:saveAsActivity>" +
                salesforceConnectorProperties.get("saveAsActivity") +
                "</per:saveAsActivity>\n" +
                "   <per:senderDisplayName>" +
                salesforceConnectorProperties.get("senderDisplayName") +
                "</per:senderDisplayName>\n" +
                "   <per:subject>" +
                salesforceConnectorProperties.get("subject") +
                "</per:subject>\n" +
                "   <per:useSignature>" +
                salesforceConnectorProperties.get("useSignature") +
                "</per:useSignature>\n" +
                "   <per:targetObjectId>" +
                salesforceConnectorProperties.get("targetObjectId") +
                "</per:targetObjectId>\n" +
                "   <per:plainTextBody>" +
                salesforceConnectorProperties.get("plainTextBody") +
                "</per:plainTextBody>\n" +
                "   </per:config>\n" +
                "   </soapenv:Body>\n" + "</soapenv:Envelope>";

        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator
                + CONNECTOR_NAME + "_" + methodName + ".xml")));
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getRequest = AXIOMUtil.stringToOM(omString);
        String proxyName = CONNECTOR_NAME + "_" + methodName;
        OMElement response =
                axisServiceClient.sendReceive(getRequest,
                        getProxyServiceURL(proxyName), "mediate");
        Assert.assertTrue(response.toString().contains("sendEmailResponse"));
        Assert.assertEquals(((OMElement) (((((OMElement) (response.getChildrenWithLocalName("result")
                .next())).getChildrenWithLocalName("success").next())))).getText(), "true");

    }

    @Test(groups = {"wso2.esb"}, description = "Salesforce {retrieve} integration test.")
    public void testSalesforceRetrieve() throws Exception {
        final String methodName = "retrieve";

        final String omString = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://connector.esb.wso2.org\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "   <per:config>\n" +
                "   <per:clientId>" +
                salesforceConnectorProperties.get("clientId") +
                "</per:clientId>\n" +
                "   <per:clientSecret>" +
                salesforceConnectorProperties.get("clientSecret") +
                "</per:clientSecret>\n" +
                "   <per:refreshToken>" +
                salesforceConnectorProperties.get("refreshToken") +
                "</per:refreshToken>\n" +
                "   <per:apiVersion>" +
                salesforceConnectorProperties.get("apiVersion") +
                "</per:apiVersion>\n" +
                "   </per:config>\n" +
                "   <per:retrieve>\n" +
                "   <per:id1>" +
                salesforceConnectorProperties.get("retrieveId1") +
                "</per:id1>\n" +
                "   <per:id2>" +
                salesforceConnectorProperties.get("retrieveId2") +
                "</per:id2>\n" +
                "   <per:fieldList>" +
                salesforceConnectorProperties.get("fieldList") +
                "</per:fieldList>\n" +
                "   <per:objectType>" +
                salesforceConnectorProperties.get("objectType") +
                "</per:objectType>\n" +
                "   </per:retrieve>\n" +
                "   </soapenv:Body>\n" + "</soapenv:Envelope>";

        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator
                + CONNECTOR_NAME + "_" + methodName + ".xml")));
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getRequest = AXIOMUtil.stringToOM(omString);
        String proxyName = CONNECTOR_NAME + "_" + methodName;
        OMElement response =
                axisServiceClient.sendReceive(getRequest,
                        getProxyServiceURL(proxyName), "mediate");
        Assert.assertTrue(response.toString().contains("retrieveResponse"));
        Assert.assertTrue(response.toString().contains(salesforceConnectorProperties.get("objectType").toString()));
    }


    @Test(groups = {"wso2.esb"}, enabled = false, description = "Salesforce {setPassword} integration test.")
    public void testSalesforceSetPassword() throws Exception {
        final String methodName = "setPassword";
        final String omString = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://connector.esb.wso2.org\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "   <per:config>\n" +
                "   <per:clientId>" +
                salesforceConnectorProperties.get("clientId") +
                "</per:clientId>\n" +
                "   <per:clientSecret>" +
                salesforceConnectorProperties.get("clientSecret") +
                "</per:clientSecret>\n" +
                "   <per:refreshToken>" +
                salesforceConnectorProperties.get("refreshToken") +
                "</per:refreshToken>\n" +
                "   <per:apiVersion>" +
                salesforceConnectorProperties.get("apiVersion") +
                "</per:apiVersion>\n" +
                "   </per:config>\n" +
                "   <per:setPassword>\n" +
                "   <per:userId>" +
                salesforceConnectorProperties.get("userId") +
                "</per:userId>\n" +
                "   <per:password>" +
                salesforceConnectorProperties.get("password") +
                "</per:password>\n" +
                "   </per:setPassword>\n" +
                "   </soapenv:Body>\n" + "</soapenv:Envelope>";
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator
                + CONNECTOR_NAME + "_" + methodName + ".xml")));

        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getRequest = AXIOMUtil.stringToOM(omString);
        String proxyName = CONNECTOR_NAME + "_" + methodName;
        OMElement response =
                axisServiceClient.sendReceive(getRequest,
                        getProxyServiceURL(proxyName), "mediate");
        try {
            Assert.assertTrue(response.toString().contains("setPasswordResponse"));
            Assert.assertTrue(response.toString().contains("result"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }

    @Test(groups = {"wso2.esb"}, enabled = false, description = "Salesforce {create} integration test.")
    public void testSalesforceCreate() throws Exception {
        final String methodName = "create";
        final String omString = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://connector.esb.wso2.org\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "   <per:config>\n" +
                "   <per:clientId>" +
                salesforceConnectorProperties.get("clientId") +
                "</per:clientId>\n" +
                "   <per:clientSecret>" +
                salesforceConnectorProperties.get("clientSecret") +
                "</per:clientSecret>\n" +
                "   <per:refreshToken>" +
                salesforceConnectorProperties.get("refreshToken") +
                "</per:refreshToken>\n" +
                "   <per:apiVersion>" +
                salesforceConnectorProperties.get("apiVersion") +
                "</per:apiVersion>\n" +
                "   </per:config>\n" +
                "   <per:create>\n" +
                "   <per:sObjectName>" +
                salesforceConnectorProperties.get("sObjectName") +
                "</per:sObjectName>\n" +
                "   </per:create>\n" +
                "   </soapenv:Body>\n" + "</soapenv:Envelope>";
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator
                + CONNECTOR_NAME + "_" + methodName + ".xml")));

        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getRequest = AXIOMUtil.stringToOM(omString);
        String proxyName = CONNECTOR_NAME + "_" + methodName;
        OMElement response =
                axisServiceClient.sendReceive(getRequest,
                        getProxyServiceURL(proxyName), "mediate");
        try {
            Assert.assertTrue(response.toString().contains("createResponse"));
            Assert.assertEquals(((OMElement) (((((OMElement) (response.getChildrenWithLocalName("result")
                    .next())).getChildrenWithLocalName("success").next())))).getText(), "true");
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }

    @Test(groups = {"wso2.esb"}, enabled = false, description = "Salesforce {update} integration test.")
    public void testSalesforceUpdate() throws Exception {
        final String methodName = "update";
        final String omString = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://connector.esb.wso2.org\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "   <per:config>\n" +
                "   <per:clientId>" +
                salesforceConnectorProperties.get("clientId") +
                "</per:clientId>\n" +
                "   <per:clientSecret>" +
                salesforceConnectorProperties.get("clientSecret") +
                "</per:clientSecret>\n" +
                "   <per:refreshToken>" +
                salesforceConnectorProperties.get("refreshToken") +
                "</per:refreshToken>\n" +
                "   <per:apiVersion>" +
                salesforceConnectorProperties.get("apiVersion") +
                "</per:apiVersion>\n" +
                "   </per:config>\n" +
                "   <per:update>\n" +
                "   <per:sObjectId>" +
                salesforceConnectorProperties.get("updateSObjectId") +
                "</per:sObjectId>\n" +
                "   <per:sObjectName>" +
                salesforceConnectorProperties.get("updateSObjectName") +
                "</per:sObjectName>\n" +
                "   </per:update>\n" +
                "   </soapenv:Body>\n" + "</soapenv:Envelope>";
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator
                + CONNECTOR_NAME + "_" + methodName + ".xml")));

        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getRequest = AXIOMUtil.stringToOM(omString);
        String proxyName = CONNECTOR_NAME + "_" + methodName;
        OMElement response =
                axisServiceClient.sendReceive(getRequest,
                        getProxyServiceURL(proxyName), "mediate");
        try {
            Assert.assertTrue(response.toString().contains("updateResponse"));
            Assert.assertEquals(((OMElement) (((((OMElement) (response.getChildrenWithLocalName("result")
                    .next())).getChildrenWithLocalName("success").next())))).getText(), "true");
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }

    @Test(groups = {"wso2.esb"}, enabled = false, description = "Salesforce {delete} integration test.")
    public void testSalesforceDelete() throws Exception {

        final String methodName = "delete";
        final String omString = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://connector.esb.wso2.org\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "   <per:config>\n" +
                "   <per:clientId>" +
                salesforceConnectorProperties.get("clientId") +
                "</per:clientId>\n" +
                "   <per:clientSecret>" +
                salesforceConnectorProperties.get("clientSecret") +
                "</per:clientSecret>\n" +
                "   <per:refreshToken>" +
                salesforceConnectorProperties.get("refreshToken") +
                "</per:refreshToken>\n" +
                "   <per:apiVersion>" +
                salesforceConnectorProperties.get("apiVersion") +
                "</per:apiVersion>\n" +
                "   </per:config>\n" +
                "   <per:delete>\n" +
                "   <per:id>" +
                salesforceConnectorProperties.get("deleteObjectId") +
                "</per:id>\n" +
                "   </per:delete>\n" +
                "   </soapenv:Body>\n" + "</soapenv:Envelope>";

        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator
                + CONNECTOR_NAME + "_" + methodName + ".xml")));

        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getRequest = AXIOMUtil.stringToOM(omString);
        String proxyName = CONNECTOR_NAME + "_" + methodName;
        OMElement response =
                axisServiceClient.sendReceive(getRequest,
                        getProxyServiceURL(proxyName), "mediate");

        try {
            Assert.assertTrue(response.toString().contains("deleteResponse"));
            Assert.assertEquals(((OMElement) (((((OMElement) (response.getChildrenWithLocalName("result")
                    .next())).getChildrenWithLocalName("success").next())))).getText(), "true");

        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }

    }

    @Test(groups = {"wso2.esb"}, enabled = false, description = "Salesforce {undelete} integration test.")
    public void testSalesforceUndelete() throws Exception {

        final String methodName = "undelete";
        final String omString = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://connector.esb.wso2.org\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "   <per:config>\n" +
                "   <per:clientId>" +
                salesforceConnectorProperties.get("clientId") +
                "</per:clientId>\n" +
                "   <per:clientSecret>" +
                salesforceConnectorProperties.get("clientSecret") +
                "</per:clientSecret>\n" +
                "   <per:refreshToken>" +
                salesforceConnectorProperties.get("refreshToken") +
                "</per:refreshToken>\n" +
                "   <per:apiVersion>" +
                salesforceConnectorProperties.get("apiVersion") +
                "</per:apiVersion>\n" +
                "   </per:config>\n" +
                "   <per:undelete>\n" +
                "   <per:id>" +
                salesforceConnectorProperties.get("undeleteObjectId") +
                "</per:id>\n" +
                "   </per:undelete>\n" +
                "   </soapenv:Body>\n" + "</soapenv:Envelope>";

        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator
                + CONNECTOR_NAME + "_" + methodName + ".xml")));

        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getRequest = AXIOMUtil.stringToOM(omString);
        String proxyName = CONNECTOR_NAME + "_" + methodName;
        OMElement response =
                axisServiceClient.sendReceive(getRequest,
                        getProxyServiceURL(proxyName), "mediate");

        try {
            Assert.assertTrue(response.toString().contains("undeleteResponse"));
            Assert.assertEquals(((OMElement) (((((OMElement) (response.getChildrenWithLocalName("result")
                    .next())).getChildrenWithLocalName("success").next())))).getText(), "true");
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }

    }

    @Test(groups = {"wso2.esb"}, enabled = false, description = "Salesforce {emptyRecycleBin} integration test.")
    public void testSalesforceEmptyRecycleBin() throws Exception {

        final String methodName = "emptyRecycleBin";
        final String omString = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://connector.esb.wso2.org\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "   <per:config>\n" +
                "   <per:clientId>" +
                salesforceConnectorProperties.get("clientId") +
                "</per:clientId>\n" +
                "   <per:clientSecret>" +
                salesforceConnectorProperties.get("clientSecret") +
                "</per:clientSecret>\n" +
                "   <per:refreshToken>" +
                salesforceConnectorProperties.get("refreshToken") +
                "</per:refreshToken>\n" +
                "   <per:apiVersion>" +
                salesforceConnectorProperties.get("apiVersion") +
                "</per:apiVersion>\n" +
                "   </per:config>\n" +
                "   <per:emptyRecycleBin>\n" +
                "   <per:id>" +
                salesforceConnectorProperties.get("emptyRecycleBinId") +
                "</per:id>\n" +
                "   </per:emptyRecycleBin>\n" +
                "   </soapenv:Body>\n" + "</soapenv:Envelope>";

        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator
                + CONNECTOR_NAME + "_" + methodName + ".xml")));

        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getRequest = AXIOMUtil.stringToOM(omString);
        String proxyName = CONNECTOR_NAME + "_" + methodName;
        OMElement response =
                axisServiceClient.sendReceive(getRequest,
                        getProxyServiceURL(proxyName), "mediate");

        try {
            Assert.assertTrue(response.toString().contains("emptyRecycleBinResponse"));
            Assert.assertEquals(((OMElement) (((((OMElement) (response.getChildrenWithLocalName("result")
                    .next())).getChildrenWithLocalName("success").next())))).getText(), "true");
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }

    }

    @Test(groups = {"wso2.esb"}, enabled = false, description = "Salesforce {upsert} integration test.")
    public void testSalesforceUpsert() throws Exception {
        final String methodName = "upsert";
        final String omString = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://connector.esb.wso2.org\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "   <per:config>\n" +
                "   <per:clientId>" +
                salesforceConnectorProperties.get("clientId") +
                "</per:clientId>\n" +
                "   <per:clientSecret>" +
                salesforceConnectorProperties.get("clientSecret") +
                "</per:clientSecret>\n" +
                "   <per:refreshToken>" +
                salesforceConnectorProperties.get("refreshToken") +
                "</per:refreshToken>\n" +
                "   <per:apiVersion>" +
                salesforceConnectorProperties.get("apiVersion") +
                "</per:apiVersion>\n" +
                "   </per:config>\n" +
                "   <per:upsert>\n" +
                "   <per:sObjectId>" +
                salesforceConnectorProperties.get("upsertSObjectId") +
                "</per:sObjectId>\n" +
                "   <per:sObjectUpdateName>" +
                salesforceConnectorProperties.get("upsertSObjectName") +
                "</per:sObjectUpdateName>\n" +
                "   <per:sObjectName>" +
                salesforceConnectorProperties.get("upsertSObjectName2") +
                "</per:sObjectName>\n" +
                "   </per:upsert>\n" +
                "   </soapenv:Body>\n" + "</soapenv:Envelope>";
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator
                + CONNECTOR_NAME + "_" + methodName + ".xml")));

        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getRequest = AXIOMUtil.stringToOM(omString);
        String proxyName = CONNECTOR_NAME + "_" + methodName;
        OMElement response =
                axisServiceClient.sendReceive(getRequest,
                        getProxyServiceURL(proxyName), "mediate");
        try {
            Assert.assertTrue(response.toString().contains("upsertResponse"));
            Assert.assertEquals(((OMElement) (((((OMElement) (response.getChildrenWithLocalName("result")
                    .next())).getChildrenWithLocalName("success").next())))).getText(), "true");
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }

    @Test(groups = {"wso2.esb"}, enabled = false, description = "Salesforce {ResetPassword} integration test.")
    public void testSalesforceResetPassword() throws Exception {
        final String methodName = "resetPassword";
        final String omString = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:per=\"http://connector.esb.wso2.org\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "   <per:config>\n" +
                "   <per:clientId>" +
                salesforceConnectorProperties.get("clientId") +
                "</per:clientId>\n" +
                "   <per:clientSecret>" +
                salesforceConnectorProperties.get("clientSecret") +
                "</per:clientSecret>\n" +
                "   <per:refreshToken>" +
                salesforceConnectorProperties.get("refreshToken") +
                "</per:refreshToken>\n" +
                "   <per:apiVersion>" +
                salesforceConnectorProperties.get("apiVersion") +
                "</per:apiVersion>\n" +
                "   </per:config>\n" +
                "   <per:resetPassword>\n" +
                "   <per:userId>" +
                salesforceConnectorProperties.get("userId") +
                "</per:userId>\n" +
                "   </per:resetPassword>\n" +
                "   </soapenv:Body>\n" + "</soapenv:Envelope>";
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator
                + CONNECTOR_NAME + "_" + methodName + ".xml")));

        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getRequest = AXIOMUtil.stringToOM(omString);
        String proxyName = CONNECTOR_NAME + "_" + methodName;
        OMElement response =
                axisServiceClient.sendReceive(getRequest,
                        getProxyServiceURL(proxyName), "mediate");
        try {
            Assert.assertTrue(response.toString().contains("resetPasswordResponse"));
            Assert.assertTrue(((OMElement) (((((OMElement) (response.getChildrenWithLocalName("result")
                    .next())).getChildrenWithLocalName("password").next())))).getText().length() > 0);
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }

}

