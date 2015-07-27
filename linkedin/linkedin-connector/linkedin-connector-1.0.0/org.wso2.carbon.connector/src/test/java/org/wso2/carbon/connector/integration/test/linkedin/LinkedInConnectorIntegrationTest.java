/**
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * <p/>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.connector.integration.test.linkedin;

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
     * Mandatory parameter test case for viewJobs method.
     */
    @Test(groups = {"wso2.esb"}, description = "linkedin {viewJobs} integration test with mandatory parameter.")
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
    @Test(groups = {"wso2.esb"}, description = "linkedin {viewJobs} integration test with optional parameters.")
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
    @Test(groups = {"wso2.esb"}, description = "linkedin {viewJobs} integration test.")
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
     * Negative parameter test case for followCompanyPage method.
     */
    @Test(groups = {"wso2.esb"}, description = "linkedin {followCompanyPage} integration test.")
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
    @Test(groups = {"wso2.esb"}, description = "linkedin {accessOutOfNetworkProfiles} integration test.")
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
    @Test(groups = {"wso2.esb"}, description = "linkedin {accessOutOfNetworkProfiles} integration test.")
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
     * Negative test case for shareResources method.
     */
    @Test(groups = {"wso2.esb"}, description = "linkedin {shareResources} integration test.")
    public void testShareResourcesNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "shareResources_negative.txt";
        String methodName = "linkedin_shareResources";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getProfile method.
     */
    @Test(groups = {"wso2.esb"}, description = "linkedin {getProfile} integration test.")
    public void testGetProfileByMemberIdWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getProfile_mandatory.txt";
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
     * Secure Url test case for getProfile method.
     */
    @Test(groups = {"wso2.esb"}, description = "linkedin {getProfile} integration test.")
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
    @Test(groups = {"wso2.esb"}, description = "linkedin {getProfile} integration test.")
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
     * Mandatory parameter test case for getAdditionalProfileFields_mandatory method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getProfile with Additional Fields} integration test.")
    public void testGetAdditionalProfileFields_mandatory() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getAdditionalProfileFields_mandatory.txt";
        String methodName = "linkedin_getAdditionalProfileFields";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /** Negative test case for getAdditionalProfileFields  method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getProfile with Additional Fields} integration test.")
    public void testGetAdditionalProfileFieldsNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getAdditionalProfileFields_negative.txt";
        String methodName = "linkedin_getAdditionalProfileFields";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken1"), linkedinConnectorProperties.getProperty("apiUrl"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getProfiledataBasic_mandatory method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getProfile with Basic Fields} integration test.")
    public void testGetProfiledataBasic_mandatory() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getProfiledataBasic_mandatory.txt";
        String methodName = "linkedin_getProfiledataBasic";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("firstName"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getProfiledataBasic  method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getProfile with basic Fields} integration test.")
    public void testGetProfiledataBasicNegativeCase() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getProfiledataBasic_negative.txt";
        String methodName = "linkedin_getProfiledataBasic";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getHistoricalFollowers_mandatory method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getHistoricalFollowers with Mandatory  Fields} integration test.")
    public void testGetHistoricalFollowers_mandatory() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getHistoricalFollowers_mandatory.txt";
        String methodName = "linkedin_getHistoricalFollowers";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"), linkedinConnectorProperties.getProperty("timeGranularity"), linkedinConnectorProperties.getProperty("startTimestamp"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("values"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for getHistoricalFollowers method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getHistoricalFollowers with Additional Fields} integration test.")
    public void testGetHistoricalFollowers_optional() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getHistoricalFollowers_optional.txt";
        String methodName = "linkedin_getHistoricalFollowers";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"), linkedinConnectorProperties.getProperty("timeGranularity"), linkedinConnectorProperties.getProperty("startTimestamp"), linkedinConnectorProperties.getProperty("updateKey"), linkedinConnectorProperties.getProperty("endTimestamp"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("values"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     //     * Negative parameter test case for getHistoricalFollowers_negative method.
     //     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getHistoricalFollowers with negative  Fields} integration test.")
    public void testGetHistoricalFollowers_negative() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getHistoricalFollowers_negative.txt";
        String methodName = "linkedin_getHistoricalFollowers";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"), linkedinConnectorProperties.getProperty("timeGranularity"), linkedinConnectorProperties.getProperty("startTimestamp"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for listCompanyByMember method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {listCompanyByMember with Mandatory  Fields} integration test.")
    public void testListCompanyByMember_mandatory() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listCompanyByMember_mandatory.txt";
        String methodName = "linkedin_listCompanyByMember";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("_total"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listCompanyByMember method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {listCompanyByMember with Optional  Fields} integration test.")
    public void testListCompanyByMember_optional() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listCompanyByMember_optional.txt";
        String methodName = "linkedin_listCompanyByMember";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("start"), linkedinConnectorProperties.getProperty("count"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("_total"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     //     * Negative parameter test case for listCompanyByMember method.
     //     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {listCompanyByMember with negative  Fields} integration test.")
    public void testListCompanyByMember_negative() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listCompanyByMember_negative.txt";
        String methodName = "linkedin_listCompanyByMember";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken1"), linkedinConnectorProperties.getProperty("apiUrl"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for checkAdminMember method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {checkAdminMember with Mandatory  Fields} integration test.")
    public void testCheckAdminMember_mandatory() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "checkAdminMember_mandatory.txt";
        String methodName = "linkedin_checkAdminMember";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     //     * Negative parameter test case for checkAdminMember method.
     //     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {checkAdminMember with negative  Fields} integration test.")
    public void testCheckAdminMember_negative() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "checkAdminMember_negative.txt";
        String methodName = "linkedin_checkAdminMember";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


    /**
     * Mandatory parameter test case for getCompanyProfile method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getCompanyProfile with Mandatory  Fields} integration test.")
    public void testGetCompanyProfile_mandatory() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getCompanyProfile_mandatory.txt";
        String methodName = "linkedin_getCompanyProfile";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for getAdditionalProfileInfo method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getAdditionalProfileInfo with Mandatory  Fields} integration test.")
    public void testGetAdditionalProfileInfo_optional() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getCompanyProfileInfo_optional.txt";
        String methodName = "linkedin_getAdditionalProfileInfo";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"), linkedinConnectorProperties.getProperty("properties"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("name"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


    /**
     * Mandatory parameter test case for checkShare method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {checkShare with Mandatory  Fields} integration test.")
    public void testCheckShare_mandatory() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "checkShare_mandatory.txt";
        String methodName = "linkedin_checkShare";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     //     * Negative parameter test case for checkShare method.
     //     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {checkShare with negative  Fields} integration test.")
    public void testCheckShare_negative() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "checkShare_negative.txt";
        String methodName = "linkedin_checkShare";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getcommentsForSpecificUpdate method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getcommentsForSpecificUpdate with Additional Fields} integration test.")
    public void testGetcommentsForSpecificUpdate_mandatory() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getcommentsForSpecificUpdate_mandatory.txt";
        String methodName = "linkedin_getcommentsForSpecificUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"), linkedinConnectorProperties.getProperty("updateKey"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("_total"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /** Negative test case for getcommentsForSpecificUpdate  method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getcommentsForSpecificUpdate with negative fields} integration test.")
    public void testGetcommentsForSpecificUpdate_negative() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getcommentsForSpecificUpdate_negative.txt";
        String methodName = "linkedin_getcommentsForSpecificUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"), linkedinConnectorProperties.getProperty("updateKey"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getCompanySpecificUpdate method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getCompanySpecificUpdate with mandatory Fields} integration test.")
    public void testGetCompanySpecificUpdate_mandatory() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getCompanySpecificUpdate_mandatory.txt";
        String methodName = "linkedin_getCompanySpecificUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"), linkedinConnectorProperties.getProperty("updateKey"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("isCommentable"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /** Negative test case for getCompanySpecificUpdate  method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getCompanySpecificUpdate with negative fields} integration test.")
    public void testGetCompanySpecificUpdate_negative() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getCompanySpecificUpdate_negative.txt";
        String methodName = "linkedin_getCompanySpecificUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"), linkedinConnectorProperties.getProperty("updateKey"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getCompanyUpdate method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getCompanyUpdate with mandatory Fields} integration test.")
    public void testGetCompanyUpdate_mandatory() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getCompanyUpdate_mandatory.txt";
        String methodName = "linkedin_getCompanyUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("_count"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for getCompanyUpdate method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getCompanyUpdate with Optional  Fields} integration test.")
    public void testGetCompanyUpdate_optional() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getCompanyUpdate_optional.txt";
        String methodName = "linkedin_getCompanyUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"), linkedinConnectorProperties.getProperty("start"), linkedinConnectorProperties.getProperty("count"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("_count"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     //     * Negative parameter test case for getCompanyUpdate method.
     //     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getCompanyUpdate with negative  Fields} integration test.")
    public void testGetCompanyUpdate_negative() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getCompanyUpdate_negative.txt";
        String methodName = "linkedin_getCompanyUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"), linkedinConnectorProperties.getProperty("start"), linkedinConnectorProperties.getProperty("count"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getfollowers method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getfollowers with Mandatory  Fields} integration test.")
    public void testGetfollowers_mandatory() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getfollowers_mandatory.txt";
        String methodName = "linkedin_getfollowers";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for getfollowers method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getfollowers with optional  Fields} integration test.")
    public void testGetfollowers_optional() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getfollowers_optional.txt";
        String methodName = "linkedin_getfollowers";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     //     * Negative parameter test case for getfollowers method.
     //     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getfollowers with negative  Fields} integration test.")
    public void testGetfollowers_negative() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getfollowers_negative.txt";
        String methodName = "linkedin_getfollowers";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"), linkedinConnectorProperties.getProperty("start"), linkedinConnectorProperties.getProperty("count"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getMemberProfile method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getMemberProfile with mandatory Fields} integration test.")
    public void testGetMemberProfile_mandatory() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getMemberProfile_mandatory.txt";
        String methodName = "linkedin_getMemberProfile";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"), linkedinConnectorProperties.getProperty("updateKey"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonObject.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /** Negative test case for getMemberProfile  method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getMemberProfile with negative fields} integration test.")
    public void testGetMemberProfile_negative() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getMemberProfile_negative.txt";
        String methodName = "linkedin_getMemberProfile";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"), linkedinConnectorProperties.getProperty("updateKey"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getLikesForCompanyUpdate method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getLikesForCompanyUpdate with mandatory Fields} integration test.")
    public void testGetLikesForCompanyUpdate_mandatory() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getLikesForCompanyUpdate_mandatory.txt";
        String methodName = "linkedin_getLikesForCompanyUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"), linkedinConnectorProperties.getProperty("updateKey"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /** Negative test case for getLikesForCompanyUpdate  method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "linkedin {getLikesForCompanyUpdate with negative fields} integration test.")
    public void testGetLikesForCompanyUpdate_negative() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getLikesForCompanyUpdate_negative.txt";
        String methodName = "linkedin_getLikesForCompanyUpdate";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("apiUrl"), linkedinConnectorProperties.getProperty("companyId"), linkedinConnectorProperties.getProperty("updateKey"));

        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


    /**
     * Negative test case for getNetworkUpdates method.
     */
    @Test(groups = {"wso2.esb"}, description = "linkedin {getNetworkUpdates} integration test.")
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
     * mandatory parameter test case for companyLookUp method.
     */

    @Test(groups = {"wso2.esb"}, description = "linkedin {companyLookUp} integration test.")
    public void testCompanyLookUpWithCompanyIdWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "companyLookupWithCompanyId_mandatory.txt";
        String methodName = "linkedin_companyLookUp";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("companyId"));

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

    @Test(groups = {"wso2.esb"}, description = "linkedin {companyLookUp} integration test.")
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

    @Test(groups = {"wso2.esb"}, description = "linkedin {companyLookUp} integration test.")
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

    @Test(groups = {"wso2.esb"}, description = "linkedin {companyLookUp} integration test.")
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

    @Test(groups = {"wso2.esb"}, description = "linkedin {companyLookUp} integration test.")
    public void testCompanyLookUpWithCompanyIdAndUniversalNameWithMandatoryParameters() throws Exception {

        String jsonRequestFilePath =
                pathToRequestsDirectory + "companyLookupWithCompanyIdAndUniversalName_mandatory.txt";
        String methodName = "linkedin_companyLookUp";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, linkedinConnectorProperties.getProperty("accessToken"), linkedinConnectorProperties.getProperty("companyId"));

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

    @Test(groups = {"wso2.esb"}, description = "linkedin {companyLookUp} integration test.")
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
     * Negative test case for companyLookUp method.
     */

    @Test(groups = {"wso2.esb"}, description = "linkedin {companyLookUp} integration test.")
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
     * Negative test case for companyLookUp method.
     */

    @Test(groups = {"wso2.esb"}, description = "linkedin {companyLookUp} integration test.")
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
     * mandatory parameter test case for searchCompanyPage method.
     */
    @Test(groups = {"wso2.esb"}, description = "linkedin {searchCompanyPage} integration test.")
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
    @Test(groups = {"wso2.esb"}, description = "linkedin {searchCompanyPage} integration test.")
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
    @Test(groups = {"wso2.esb"}, description = "linkedin {searchCompanyPage} integration test.")
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
    @Test(groups = {"wso2.esb"}, description = "linkedin {searchCompanyPage} integration test.")
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
    @Test(groups = {"wso2.esb"}, description = "linkedin {searchCompanyPage} integration test.")
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
    @Test(groups = {"wso2.esb"}, description = "linkedin {searchCompanyPage} integration test.")
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


}
