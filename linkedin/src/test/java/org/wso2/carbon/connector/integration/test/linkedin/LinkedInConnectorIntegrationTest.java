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

package org.wso2.carbon.connector.integration.test.linkedin;

import java.net.URL;
import java.util.Properties;

import javax.activation.DataHandler;

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

public class LinkedInConnectorIntegrationTest extends ESBIntegrationTest {
    
    private static final String CONNECTOR_NAME = "linkedin";
    
    private MediationLibraryUploaderStub mediationLibUploadStub = null;
    
    private MediationLibraryAdminServiceStub adminServiceStub = null;
    
    private ProxyServiceAdminClient proxyAdmin;
    
    private String repoLocation = null;
    
    private String linkedinConnectorFileName = CONNECTOR_NAME + ".zip";
    
    private Properties linkedinConnectorProperties = null;
    
    private String pathToProxiesDirectory = null;
    
    private String pathToRequestsDirectory = null;
    
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
        
        ConnectorIntegrationUtil.uploadConnector(repoLocation, mediationLibUploadStub, linkedinConnectorFileName);
        log.info("Sleeping for " + 30000 / 1000 + " seconds while waiting for synapse import");
        Thread.sleep(30000);
        
        adminServiceStub.updateStatus("{org.wso2.carbon.connector}" + CONNECTOR_NAME, CONNECTOR_NAME,
                "org.wso2.carbon.connector", "enabled");
        
        linkedinConnectorProperties = ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);
        
        pathToProxiesDirectory = repoLocation + linkedinConnectorProperties.getProperty("proxyDirectoryRelativePath");
        pathToRequestsDirectory = repoLocation + linkedinConnectorProperties.getProperty("requestDirectoryRelativePath");
        
    }
    
    @Override
    protected void cleanup() {
        axis2Client.destroy();
    }
    
    /**
     * Mandatory parameter test case for sendActivity method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {sendActivity} integration test with mandatory parameters.")
    public void testSendActivityWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "sendActivity_mandatory.txt";
        String methodName = "linkedin_sendActivity";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 201);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Negative test case for sendActivity method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {sendActivity} integration test with negative case.")
    public void testSendActivityWithNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "sendActivity_negative.txt";
        String methodName = "linkedin_sendActivity";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * mandatory parameter test case for getStatus method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {getStatus} integration test with mandatory parameters.")
    public void testGetStatusWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getStatus_mandatory.txt";
        String methodName = "linkedin_getStatus";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("currentStatus"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Optional parameter test case for getStatus method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {getStatus} integration test with optional parameters.")
    public void testGetStatusWithOptionalParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getStatus_optional.txt";
        String methodName = "linkedin_getStatus";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("publicProfileUrl"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("currentStatus"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * negative test case for getStatus method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {getStatus} integration test with negative case.")
    public void testGetStatusWithNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getStatus_negative.txt";
        String methodName = "linkedin_getStatus";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Mandatory parameter test case for viewJobs method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {viewJobs} integration test with mandatory parameter.")
    public void testViewJobsWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "viewJobs_mandatory.txt";
        String methodName = "linkedin_viewJobs";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("jobId"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("company"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Optional parameter test case for viewJobs method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {viewJobs} integration test with optional parameters.")
    public void testViewJobsWithOptionalParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "viewJobs_optional.txt";
        String methodName = "linkedin_viewJobs";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("jobId"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("company"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Negative parameter test case for viewJobs method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {viewJobs} integration test.")
    public void testViewJobsWithNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "viewJobs_negative.txt";
        String methodName = "linkedin_viewJobs";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Mandatory parameter test case for sendMessage method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {sendMessage} integration test.")
    public void testSendMessageWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "sendMessage_mandatory.txt";
        String methodName = "linkedin_sendMessage";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"),linkedinConnectorProperties.getProperty("memberId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 201);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Negative parameter test case for SendMessage method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {SendMessage} integration test.")
    public void testSendMessageWithNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "sendMessage_negative.txt";
        String methodName = "linkedin_sendMessage";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Mandatory parameter test case for followCompanyPage method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {followCompanyPage} integration test.")
    public void testFollowCompanyPageWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "followCompanyPage_mandatory.txt";
        String methodName = "linkedin_followCompanyPage";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"),linkedinConnectorProperties.getProperty("followCompanyId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 201);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Negative parameter test case for followCompanyPage method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {followCompanyPage} integration test.")
    public void testFollowCompanyPageWithNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "followCompanyPage_negative.txt";
        String methodName = "linkedin_followCompanyPage";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Mandatory parameter test case for accessOutOfNetworkProfiles method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {accessOutOfNetworkProfiles} integration test.")
    public void testAccessOutOfNetworkProfilesWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "accessOutOfNetworkProfiles_mandatory.txt";
        String methodName = "linkedin_accessOutOfNetworkProfiles";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("memberId"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("firstName"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    
    /**
     * Negative parameter test case for accessOutOfNetworkProfiles method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {accessOutOfNetworkProfiles} integration test.")
    public void testAccessOutOfNetworkProfilesNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "accessOutOfNetworkProfiles_negative.txt";
        String methodName = "linkedin_accessOutOfNetworkProfiles";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Mandatory parameter test case for shareResources method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {shareResources} integration test.")
    public void testShareResourcesWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "shareResources_mandatory.txt";
        String methodName = "linkedin_shareResources";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("updateKey"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Optional parameter test case for shareResources method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {shareResources} integration test.")
    public void testShareResourcesWithOptionalParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "shareResources_optional.txt";
        String methodName = "linkedin_shareResources";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("updateKey"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Negative test case for shareResources method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {shareResources} integration test.")
    public void testShareResourcesNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "shareResources_negative.txt";
        String methodName = "linkedin_shareResources";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Mandatory parameter test case for getProfile method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {getProfile} integration test.")
    public void testGetProfileByMemberIdWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getProfile_mandatory.txt";
        String methodName = "linkedin_getLinkedInProfile";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"),linkedinConnectorProperties.getProperty("memberId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("firstName"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Secure Url test case for getProfile method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {getProfile} integration test.")
    public void testGetProfileByMemberIdWithSecureUrl() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getProfile_secureUrl.txt";
        String methodName = "linkedin_getLinkedInProfile";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("memberId"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("firstName"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Negative test case for getProfile method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {getProfile} integration test.")
    public void testGetProfileByMemberIdNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getProfile_negative.txt";
        String methodName = "linkedin_getLinkedInProfile";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Mandatory parameter test case for getProfile (by public URL) method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {getProfile by public URL} integration test.")
    public void testGetProfileByPublicUrlWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getProfileByPublicUrl_mandatory.txt";
        String methodName = "linkedin_getLinkedInProfile";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("publicProfileUrl"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("firstName"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Negative test case for getProfile (by public URL) method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {getProfile by public URL} integration test.")
    public void testGetProfileByPublicUrlNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getProfileByPublicUrl_negative.txt";
        String methodName = "linkedin_getLinkedInProfile";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Mandatory parameter test case for getNetworkUpdates method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {getNetworkUpdates} integration test.")
    public void testGetNetworkUpdatesWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getNetworkUpdates_mandatory.txt";
        String methodName = "linkedin_getNetworkUpdates";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("_total"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Negative test case for getNetworkUpdates method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {getNetworkUpdates} integration test.")
    public void testGetNetworkUpdatesNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getNetworkUpdates_negative.txt";
        String methodName = "linkedin_getNetworkUpdates";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, "AAAA");
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * Optional parameter test case for getNetworkUpdates method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {getNetworkUpdates} integration test.")
    public void testGetNetworkUpdatesWithOptionalParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getNetworkUpdates_optional.txt";
        String methodName = "linkedin_getNetworkUpdates";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("_total"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * mandatory parameter test case for companyLookUp method.
     */
    
    @Test(groups = { "wso2.esb" }, description = "linkedin {companyLookUp} integration test.")
    public void testCompanyLookUpWithCompanyIdWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "companyLookupWithCompanyId_mandatory.txt";
        String methodName = "linkedin_companyLookUp";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"),linkedinConnectorProperties.getProperty("companyId"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for companyLookUp method.
     */
    
    @Test(groups = { "wso2.esb" }, description = "linkedin {companyLookUp} integration test.")
    public void testCompanyLookUpWithCompanyIdNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "companyLookupWithCompanyId_negative.txt";
        String methodName = "linkedin_companyLookUp";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * mandatory parameter test case for companyLookUp method.
     */
    
    @Test(groups = { "wso2.esb" }, description = "linkedin {companyLookUp} integration test.")
    public void testCompanyLookUpWithCompanyAdminWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "companyLookupWithCompanyAdmin_mandatory.txt";
        String methodName = "linkedin_companyLookUp";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("_total"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for companyLookUp method.
     */
    
    @Test(groups = { "wso2.esb" }, description = "linkedin {companyLookUp} integration test.")
    public void testCompanyLookUpWithCompanyAdminNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "companyLookupWithCompanyAdmin_negative.txt";
        String methodName = "linkedin_companyLookUp";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * mandatory parameter test case for companyLookUp method.
     */
    
    @Test(groups = { "wso2.esb" }, description = "linkedin {companyLookUp} integration test.")
    public void testCompanyLookUpWithCompanyIdAndUniversalNameWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath =
                pathToRequestsDirectory + "companyLookupWithCompanyIdAndUniversalName_mandatory.txt";
        String methodName = "linkedin_companyLookUp";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"),linkedinConnectorProperties.getProperty("companyId"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("_total"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for companyLookUp method.
     */
    
    @Test(groups = { "wso2.esb" }, description = "linkedin {companyLookUp} integration test.")
    public void testCompanyLookUpWithCompanyIdAndUniversalNameNegativeCase() throws Exception {
    
        String jsonRequestFilePath =
                pathToRequestsDirectory + "companyLookupWithCompanyIdAndUniversalName_negative.txt";
        String methodName = "linkedin_companyLookUp";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * mandatory parameter test case for companyLookUp method.
     */
    
    @Test(groups = { "wso2.esb" }, description = "linkedin {companyLookUp} integration test.")
    public void testCompanyLookUpWithEmailDomainsWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "companyLookupWithEmailDomains_mandatory.txt";
        String methodName = "linkedin_companyLookUp";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("_total"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for companyLookUp method.
     */
    
    @Test(groups = { "wso2.esb" }, description = "linkedin {companyLookUp} integration test.")
    public void testCompanyLookUpWithEmailDomainsNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "companyLookupWithEmailDomains_negative.txt";
        String methodName = "linkedin_companyLookUp";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * mandatory parameter test case for companyLookUp method.
     */
    
    @Test(groups = { "wso2.esb" }, description = "linkedin {companyLookUp} integration test.")
    public void testCompanyLookUpWithUniversalNameWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "companyLookupWithUniversalName_mandatory.txt";
        String methodName = "linkedin_companyLookUp";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for companyLookUp method.
     */
    
    @Test(groups = { "wso2.esb" }, description = "linkedin {companyLookUp} integration test.")
    public void testCompanyLookUpWithUniversalNameNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "companyLookupWithUniversalName_negative.txt";
        String methodName = "linkedin_companyLookUp";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for invitePeople method.
     */
    
    @Test(groups = { "wso2.esb" }, description = "linkedin {invitePeople} integration test.")
    public void testInvitePeopleNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "invitePeople_negative.txt";
        String methodName = "linkedin_invitePeople";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 500);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for invitePeople by memberId method.
     */
    
    @Test(groups = { "wso2.esb" }, description = "linkedin {invitePeople} integration test.")
    public void testInvitePeopleByMemberIdNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "invitePeopleByMemberId_negative.txt";
        String methodName = "linkedin_invitePeople";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 500);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * mandatory parameter test case for getConnections method.
     */
    
    @Test(groups = { "wso2.esb" }, description = "linkedin {getConnections} integration test.")
    public void testGetConnectionsDefaultMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getConnectionsDefault_mandatory.txt";
        String methodName = "linkedin_getConnections";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("values"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * optional parameter test case for getConnections method.
     */
    
    @Test(groups = { "wso2.esb" }, description = "linkedin {getConnections} integration test.")
    public void testGetConnectionsDefaultOptionalParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getConnectionsDefault_optional.txt";
        String methodName = "linkedin_getConnections";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("values"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * mandatory parameter filtered test case for getConnections method.
     */
    
    @Test(groups = { "wso2.esb" }, description = "linkedin {getConnections} integration test.")
    public void testGetConnectionsDefaultMandatoryParametersFiltered() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getConnectionsDefault_filtered.txt";
        String methodName = "linkedin_getConnections";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject =
                    ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("values"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for getConnections method.
     */
    
    @Test(groups = { "wso2.esb" }, description = "linkedin {getConnections} integration test.")
    public void testGetConnectionsDefaultNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getConnectionsDefault_negative.txt";
        String methodName = "linkedin_getConnections";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, "aaaaa");
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for getConnections method.
     */
    
    @Test(groups = { "wso2.esb" }, description = "linkedin {getConnections} integration test.")
    public void testGetConnectionsWithIdNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getConnectionsWithId_negative.txt";
        String methodName = "linkedin_getConnections";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * mandatory parameter test case for getConnections method.
     */
    
    @Test(groups = { "wso2.esb" }, description = "linkedin {getConnections} integration test.")
    public void testGetConnectionsWithPublicProfileUrlMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getConnectionsWithPublicProfileUrl_mandatory.txt";
        String methodName = "linkedin_getConnections";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("myPublicUrl"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("values"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * optional parameter test case for getConnections method.
     */
    
    @Test(groups = { "wso2.esb" }, description = "linkedin {getConnections} integration test.")
    public void testGetConnectionsWithPublicProfileUrlOptionalParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getConnectionsWithPublicProfileUrl_optional.txt";
        String methodName = "linkedin_getConnections";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("myPublicUrl"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("values"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * mandatory parameter filtered test case for getConnections method.
     */
    
    @Test(groups = { "wso2.esb" }, description = "linkedin {getConnections} integration test.")
    public void testGetConnectionsWithPublicProfileUrlMandatoryParametersFiltered() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getConnectionsWithPublicProfileUrl_filtered.txt";
        String methodName = "linkedin_getConnections";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("myPublicUrl"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("values"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * Negative test case for getConnections method.
     */
    
    @Test(groups = { "wso2.esb" }, description = "linkedin {getConnections} integration test.")
    public void testGetConnectionsWithPublicProfileUrlNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getConnectionsWithPublicProfileUrl_negative.txt";
        String methodName = "linkedin_getConnections";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * mandatory parameter test case for searchCompanyPage method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {searchCompanyPage} integration test.")
    public void testSearchCompanyPageWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "searchCompanyPage_mandatory.txt";
        String methodName = "linkedin_searchCompanyPage";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("companies"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * negative parameter test case for searchCompanyPage method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {searchCompanyPage} integration test.")
    public void testSearchCompanyPageWithNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "searchCompanyPage_negative.txt";
        String methodName = "linkedin_searchCompanyPage";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, "aaaaa");
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * optional parameter test case for searchCompanyPage method.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {searchCompanyPage} integration test.")
    public void testSearchCompanyPageWithOptionalParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "searchCompanyPage_optional.txt";
        String methodName = "linkedin_searchCompanyPage";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("companies"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    /**
     * mandatory parameter test case for searchCompanyPage method with search criteria.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {searchCompanyPage} integration test.")
    public void testSearchCompanyPageWithSearchCriteriaWithMandatoryParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "searchCompanyPageWithSearchCriteria_mandatory.txt";
        String methodName = "linkedin_searchCompanyPage";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("companies"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * negative parameter test case for searchCompanyPage method with search criteria.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {searchCompanyPage} integration test.")
    public void testSearchCompanyPageWithSearchCriteriaWithNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "searchCompanyPageWithSearchCriteria_negative.txt";
        String methodName = "linkedin_searchCompanyPage";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * optional parameter test case for searchCompanyPage method with search criteria.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {searchCompanyPage} integration test.")
    public void testSearchCompanyPageWithSearchCriteriaWithOptionalParameters() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "searchCompanyPageWithSearchCriteria_optional.txt";
        String methodName = "linkedin_searchCompanyPage";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("companies"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
    
    /**
     * negative parameter test case for getCompanyStatistics method with search criteria.
     */
    @Test(groups = { "wso2.esb" }, description = "linkedin {getCompanyStatistics} integration test.")
    public void testgetCompanyStatisticsWithNegativeCase() throws Exception {
    
        String jsonRequestFilePath = pathToRequestsDirectory + "getCompanyStatistics_negative.txt";
        String methodName = "linkedin_getCompanyStatistics";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("companyId"));
        
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 403);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
        
    }
}
