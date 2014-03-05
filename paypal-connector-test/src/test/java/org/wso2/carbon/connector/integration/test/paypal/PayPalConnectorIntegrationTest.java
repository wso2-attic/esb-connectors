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

package org.wso2.carbon.connector.integration.test.paypal;

import java.util.Properties;

import org.apache.axis2.context.ConfigurationContext;
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
import org.json.JSONObject;

import java.net.URL;

import javax.activation.DataHandler;

public class PayPalConnectorIntegrationTest extends ESBIntegrationTest {
    
    private static final String CONNECTOR_NAME = "paypal";
    
    private MediationLibraryUploaderStub mediationLibUploadStub = null;
    
    private MediationLibraryAdminServiceStub adminServiceStub = null;
    
    private ProxyServiceAdminClient proxyAdmin;
    
    private String repoLocation = null;
    
    private String paypalConnectorFileName = CONNECTOR_NAME + ".zip";
    
    private Properties paypalConnectorProperties = null;
    
    private String pathToProxiesDirectory = null;
    
    private String pathToRequestsDirectory = null;
    
    // Variables for store results of dependent methods
    private String createPaymentResultPaymentId;
    
    private String createPaymentResultSaleId;
    
    private String createPaymentResultAuthorizationId;
    
    private String refundSaleResultId;
    
    private String captureAuthorizationResultId;
    
    private String storeCreditCardResultId;
    
    
    
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
            repoLocation = System.getProperty("connector_repo").replace("\\", "/");
        } else {
            repoLocation = System.getProperty("connector_repo").replace("/", "/");
        }
        
        proxyAdmin = new ProxyServiceAdminClient(esbServer.getBackEndUrl(), esbServer.getSessionCookie());
        
        ConnectorIntegrationUtil.uploadConnector(repoLocation, mediationLibUploadStub, paypalConnectorFileName);
        log.info("Sleeping for " + 30000 / 1000 + " seconds while waiting for synapse import");
        Thread.sleep(30000);
        
        adminServiceStub.updateStatus("{org.wso2.carbon.connector}" + CONNECTOR_NAME, CONNECTOR_NAME,
                "org.wso2.carbon.connector", "enabled");
        
        paypalConnectorProperties = ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);
        
        pathToProxiesDirectory = repoLocation + paypalConnectorProperties.getProperty("proxyDirectoryRelativePath");
        pathToRequestsDirectory = repoLocation + paypalConnectorProperties.getProperty("requestDirectoryRelativePath");
        
    }
    
    @Override
    protected void cleanup() {
        axis2Client.destroy();
    }
    
    /**
     * Positive test case for createPayment method with mandatory parameters - paypal payment.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {createPayment} integration test with mandatory parameters - paypal payment.")
    public void testCreatePaypalPaymentWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "createPayment_paypal_mandatory.txt";
        String methodName = "paypal_createPayment";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("create_time"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Positive test case for createPayment method with optional parameters - paypal payment.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {createPayment} integration test with optional parameters - paypal payment.")
    public void testCreatePaypalPaymentWithOptionalParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "createPayment_paypal_optional.txt";
        String methodName = "paypal_createPayment";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("create_time"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for createPayment method - paypal payment.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {createPayment} integration test with negative case - paypal payment.")
    public void testCreatePaypalPaymentWithNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "createPayment_paypal_negative.txt";
        String methodName = "paypal_createPayment";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int statusCode = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Positive test case for createPayment method with mandatory parameters - credit card payment.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {createPayment} integration test with mandatory parameters - credit card payment.")
    public void testCreateCreditCardPaymentWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "createPayment_credit_card_mandatory.txt";
        String methodName = "paypal_createPayment";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("create_time"));
            createPaymentResultPaymentId = jsonObject.getString("id");
            createPaymentResultSaleId = jsonObject.getJSONArray("transactions").getJSONObject(0).getJSONArray("related_resources").getJSONObject(0).getJSONObject("sale").getString("id");
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Positive test case for createPayment method with optional parameters - credit card payment.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {createPayment} integration test with optional parameters - credit card payment.")
    public void testCreateCreditCardPaymentWithOptionalParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "createPayment_credit_card_optional.txt";
        String methodName = "paypal_createPayment";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("create_time"));
            createPaymentResultAuthorizationId = jsonObject.getJSONArray("transactions").getJSONObject(0).getJSONArray("related_resources").getJSONObject(0).getJSONObject("authorization").getString("id");
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Positive test case for createPayment method with optional parameters - credit card payment (this will run in order to resolve dependencies).
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {createPayment} integration test with optional parameters - credit card payment.")
    public void testCreateCreditCardPaymentWithOptionalParametersForResolveDependencies1() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "createPayment_credit_card_optional.txt";
        String methodName = "paypal_createPayment";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("create_time"));
            createPaymentResultAuthorizationId = jsonObject.getJSONArray("transactions").getJSONObject(0).getJSONArray("related_resources").getJSONObject(0).getJSONObject("authorization").getString("id");
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Positive test case for createPayment method with optional parameters - credit card payment (this will run in order to resolve dependencies).
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {createPayment} integration test with optional parameters - credit card payment.")
    public void testCreateCreditCardPaymentWithOptionalParametersForResolveDependencies2() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "createPayment_credit_card_optional.txt";
        String methodName = "paypal_createPayment";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("create_time"));
            createPaymentResultAuthorizationId = jsonObject.getJSONArray("transactions").getJSONObject(0).getJSONArray("related_resources").getJSONObject(0).getJSONObject("authorization").getString("id");
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for createPayment method - credit card payment.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {createPayment} integration test with negative case - credit card payment.")
    public void testCreateCreditCardPaymentWithNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "createPayment_credit_card_negative.txt";
        String methodName = "paypal_createPayment";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int statusCode = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Positive test case for executeApprovedPayment method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {executeApprovedPayment} integration test with mandatory parameters.")
    public void testExecuteApprovedPaymentWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "executeApprovedPayment_mandatory.txt";
        String methodName = "paypal_executeApprovedPayment";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"), paypalConnectorProperties.getProperty("paypalPaymentId_1"), paypalConnectorProperties.getProperty("payerId_1"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Positive test case for executeApprovedPayment method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {executeApprovedPayment} integration test with optional parameters.")
    public void testExecuteApprovedPaymentWithOptionalParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "executeApprovedPayment_optional.txt";
        String methodName = "paypal_executeApprovedPayment";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"), paypalConnectorProperties.getProperty("paypalPaymentId_2"), paypalConnectorProperties.getProperty("payerId_2"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for executeApprovedPayment method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {executeApprovedPayment} integration test with negative case.")
    public void testExecuteApprovedPaymentWithNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "executeApprovedPayment_negative.txt";
        String methodName = "paypal_executeApprovedPayment";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int statusCode = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Positive test case for lookupPayment method.
     */
    @Test(dependsOnMethods = {"testCreateCreditCardPaymentWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "paypal {lookupPayment} integration test with mandatory parameters.")
    public void testLookupPaymentWithMandatoryParameters() throws Exception {
        
        String jsonRequestFilePath = pathToRequestsDirectory + "lookupPayment_mandatory.txt";
        String methodName = "paypal_lookupPayment";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"), createPaymentResultPaymentId);
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("create_time"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for lookupPayment method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal { lookupPayment } integration test with negative case.")
    public void testLookupPaymentWithNegetiveCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "lookupPayment_negative.txt";
        String methodName = "paypal_lookupPayment";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int statusCode = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Positive test case for listPayments method.
     */
    @Test(dependsOnMethods = {"testCreateCreditCardPaymentWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "paypal {listPayments} integration test with mandatory parameters.")
    public void testListPaymentsWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "listPayments_mandatory.txt";
        String methodName = "paypal_listPaymentResources";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("count"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Optional test case for listPayments method.
     */
    @Test(dependsOnMethods = {"testCreateCreditCardPaymentWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "paypal {listPayments} integration test with optional parameters.")
    public void testListPaymentswithOptionalParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "listPayments_optional.txt";
        String methodName = "paypal_listPaymentResources";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("count"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for listPayments method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {listPayments} integration test with negative case.")
    public void testListPaymentswithNegetiveCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "listPayments_negetive.txt";
        String methodName = "paypal_listPaymentResources";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int statusCode = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Positive test case for lookupSales method.
     */
    @Test(dependsOnMethods = {"testCreateCreditCardPaymentWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "paypal {lookupSale} integration test with mandatory parameters.")
    public void testLookupSaleWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "lookupSale_mandatory.txt";
        String methodName = "paypal_lookupSale";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"), createPaymentResultSaleId);
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for lookupSales method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {lookupSale} integration test with negative case.")
    public void testLookupSaleWithNegetiveCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "lookupSale_negetive.txt";
        String methodName = "paypal_lookupSale";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int statusCode = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Positive test case for refundSale method.
     */
    @Test(dependsOnMethods = {"testCreateCreditCardPaymentWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "paypal {refundSale} integration test with mandatory parameters.")
    public void testRefundSaleWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "refundSale_mandatory.txt";
        String methodName = "paypal_refundSale";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"), createPaymentResultSaleId);
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("create_time"));
            refundSaleResultId = jsonObject.getString("id");
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for refundSale method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {refundSale} integration test with negative case.")
    public void testRefundSalewithNegetiveCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "refundSale_negetive.txt";
        String methodName = "paypal_refundSale";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int statusCode = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Positive test case for lookupRefund method.
     */
    @Test(dependsOnMethods = {"testRefundSaleWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "paypal {lookupRefund} integration test with mandatory parameters.")
    public void testLookupRefundWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "lookupRefund_mandatory.txt";
        String methodName = "paypal_lookupRefund";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"), refundSaleResultId);
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("create_time"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for lookupRefund method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {lookupRefund} integration test with negative case.")
    public void testLookupRefundWithNegetiveCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "lookupRefund_negetive.txt";
        String methodName = "paypal_lookupRefund";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int statusCode = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Mandatory parameter test case for lookupAuthorization method.
     */
    @Test(dependsOnMethods = {"testCreateCreditCardPaymentWithOptionalParameters"}, priority = 1, groups = { "wso2.esb" }, description = "paypal {lookupAuthorization} integration test with mandatory parameters.")
    public void testLookupAuthorizationWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "lookupAuthorization_mandotary.txt";
        String methodName = "paypal_lookupAuthorization";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"), createPaymentResultAuthorizationId);
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("create_time"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for lookupAuthorization method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {lookupAuthorization} integration test with negative case.")
    public void testLookupAuthorizationWithNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "lookupAuthorization_negative.txt";
        String methodName = "paypal_lookupAuthorization";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int statusCode = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Positive test case for captureAuthorization method with mandatory parameters.
     */
    @Test(dependsOnMethods = {"testCreateCreditCardPaymentWithOptionalParameters"}, priority = 1, groups = { "wso2.esb" }, description = "paypal {captureAuthorization} integration test with mandatory parameters.")
    public void testCaptureAuthorizationWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "captureAuthorization_mandatory.txt";
        String methodName = "paypal_captureAuthorization";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"), createPaymentResultAuthorizationId);
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("create_time"));
            captureAuthorizationResultId = jsonObject.getString("id");
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Positive test case for captureAuthorization method with optional parameters.
     */
    @Test(dependsOnMethods = {"testCreateCreditCardPaymentWithOptionalParametersForResolveDependencies1"}, priority = 1, groups = { "wso2.esb" }, description = "paypal {captureAuthorization} integration test with optional parameters.")
    public void testCaptureAuthorizationWithOptionalParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "captureAuthorization_optional.txt";
        String methodName = "paypal_captureAuthorization";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"), createPaymentResultAuthorizationId);
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("create_time"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Negative test case for captureAuthorization method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {captureAuthorization} integration test with negative case.")
    public void testCaptureAuthorizationWithNegetiveCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "captureAuthorization_negetive.txt";
        String methodName = "paypal_captureAuthorization";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int statusCode = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Negative test case for reAuthorization method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {reAuthorization} integration test with negative case.")
    public void testReAuthorizationNegative() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "reAuthorization_negative.txt";
        String methodName = "paypal_reAuthorization";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int statusCode = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Positive test case for voidAuthorization method.
     */
    @Test(dependsOnMethods = {"testCreateCreditCardPaymentWithOptionalParametersForResolveDependencies2"}, priority = 1, groups = { "wso2.esb" }, description = "paypal {voidAuthorization} integration test with mandatory parameters.")
    public void testVoidAuthorizationWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "voidAuthorization_mandatory.txt";
        String methodName = "paypal_voidAuthorization";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"), createPaymentResultAuthorizationId);
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for voidAuthorization method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {voidAuthorization} integration test with negative case.")
    public void testVoidAuthorizationWithNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "voidAuthorization_negative.txt";
        String methodName = "paypal_voidAuthorization";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int statusCode = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Positive test case for lookupCapturedPayment method.
     */
    @Test(dependsOnMethods = {"testCaptureAuthorizationWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "paypal {lookupCapturedPayment} integration test with mandatory parameters.")
    public void testLookupCapturedPaymentWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "lookupCapturedPayment_mandatory.txt";
        String methodName = "paypal_lookupCapturedPayment";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"), captureAuthorizationResultId);
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for lookupCapturedPayment method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {lookupCapturedPayment} integration test with negative case.")
    public void testLookupCapturedPaymentWithNegetiveCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "lookupCapturedPayment_negative.txt";
        String methodName = "paypal_lookupCapturedPayment";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int statusCode = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Positive test case for refundCapturedPayment method.
     */
    @Test(dependsOnMethods = {"testCaptureAuthorizationWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "paypal {refundCapturedPayment} integration test with mandatory parameters.")
    public void testRefundCapturedPaymentWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "refundCapturedPayment_mandatory.txt";
        String methodName = "paypal_refundCapturedPayment";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"), captureAuthorizationResultId);
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("create_time"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for refundCapturedPayment method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal { lookupPayment } integration test with negative case.")
    public void testRefundCapturedPaymentWithNegetiveCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "refundCapturedPayment_negetive.txt";
        String methodName = "paypal_refundCapturedPayment";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int statusCode = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Mandatory parameter test case for storeCreditCardDetails method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {storeCreditCardDetails} integration test with mandatory parameters.")
    public void testStoreCreditCardDetailsWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "storecreditcard_mandatory.txt";
        String methodName = "paypal_storeCreditCard";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("create_time"));
            storeCreditCardResultId = jsonObject.getString("id");
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Optional parameter test case for storeCreditCardDetails method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {storeCreditCardDetails} integration test with optional parameters.")
    public void testStoreCreditCardDetailsWithOptionalParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "storeCreditcard_optional.txt";
        String methodName = "paypal_storeCreditCard";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("create_time"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for storeCreditCardDetails method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {storeCreditCardDetails} integration test with negative case.")
    public void testStoreCreditCardDetailsWithNegetiveCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "storeCreditcard_negative.txt";
        String methodName = "paypal_storeCreditCard";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int statusCode = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Positive test case for lookupStoredCreditCard method.
     */
    @Test(dependsOnMethods = {"testStoreCreditCardDetailsWithMandatoryParameters"}, priority = 1, groups = { "wso2.esb" }, description = "paypal {lookupStoredCreditCard} integration test with mandatory parameters.")
    public void testLookupStoredCreditCardWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "lookupStoredCreditCard_mandatory.txt";
        String methodName = "paypal_lookupStoredCreditCard";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"), storeCreditCardResultId);
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for lookupStoredCreditCard method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {lookupStoredCreditCard} integration test with negative case.")
    public void testLookupStoredCreditCardWithNegetiveCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "lookupStoredCreditCard_negetive.txt";
        String methodName = "paypal_lookupStoredCreditCard";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int statusCode = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Positive test case for deleteCreditCardDetails method.
     */
    @Test(priority = 2, groups = { "wso2.esb" }, description = "paypal {deleteCreditCardDetails} integration test with mandatory parameters.")
    public void testDeleteCreditCardDetailsWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "deleteStoredCreditCard_mandatory.txt";
        String methodName = "paypal_deleteCreditCardDetails";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"), storeCreditCardResultId);
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int statusCode = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 204);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for deleteCreditCardDetails method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {deleteCreditCardDetails} integration test with negative case.")
    public void testDeleteCreditCardDetailsWithNegetiveCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "deleteStoredCreditCard_negetive.txt";
        String methodName = "paypal_deleteCreditCardDetails";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int statusCode = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for grantTokenFromAuthorization method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {grantTokenFromAuthorization} integration test with negative case.")
    public void testGrantTokenFromAuthorizationWithNegetiveCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "grantTokenFromAuthorization_negetive.txt";
        String methodName = "paypal_grantTokenFromAuthorization";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("clientId"), paypalConnectorProperties.getProperty("clientSecret"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int statusCode = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Positive test case for grantTokenFromRefreshToken method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {grantTokenFromRefreshToken} integration test with mandatory parameters.")
    public void testGrantTokenFromRefreshTokenWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "grantTokenFromRefreshToken_mandatory.txt";
        String methodName = "paypal_grantTokenFromRefreshToken";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("clientId"), paypalConnectorProperties.getProperty("clientSecret"), paypalConnectorProperties.getProperty("refreshToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("access_token"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Positive test case for grantTokenFromRefreshToken method with optional parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {grantTokenFromRefreshToken} integration test with optional parameters.")
    public void testGrantTokenFromRefreshTokenWithOptionalParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "grantTokenFromRefreshToken_optional.txt";
        String methodName = "paypal_grantTokenFromRefreshToken";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("clientId"), paypalConnectorProperties.getProperty("clientSecret"), paypalConnectorProperties.getProperty("refreshToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("access_token"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for grantTokenFromRefreshToken method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {grantTokenFromRefreshToken} integration test with negative case.")
    public void testGrantTokenFromRefreshTokenWithNegetiveCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "grantTokenFromRefreshToken_negetive.txt";
        String methodName = "paypal_grantTokenFromRefreshToken";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("clientId"), paypalConnectorProperties.getProperty("clientSecret"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int statusCode = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Positive test case for getUserInformation method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal {getUserInformation} integration test with mandatory parameters.")
    public void testGetUserInformationWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getuserInformation_mandatory.txt";
        String methodName = "paypal_getUserInformation";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("user_id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for getUserInformation method.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "paypal { getUserInformation } integration test with negative case.")
    public void testGetUserInformationWithNegetiveCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getuserInformation_negetive.txt";
        String methodName = "paypal_getUserInformation";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml ";
        String modifiedJsonString = String.format(jsonString, paypalConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int statusCode = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
}
