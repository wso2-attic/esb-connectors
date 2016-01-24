/**
 Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 WSO2 Inc. licenses this file to you under the Apache License,
 Version 2.0 (the "License"); you may not use this file except
 in compliance with the License.
 You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
*/
package org.wso2.carbon.connector.integration.test.YouTube;

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
import java.lang.System;
import java.net.URL;
import java.util.Properties;

public class ChannelsIntegrationTest extends ESBIntegrationTest {
    private static final String CONNECTOR_NAME = "YouTube";
    private MediationLibraryUploaderStub mediationLibUploadStub = null;
    private MediationLibraryAdminServiceStub adminServiceStub = null;
    private ProxyServiceAdminClient proxyAdmin;
    private String repoLocation = null;
    private String YouTubeConnectorFileName = CONNECTOR_NAME + ".zip";
    private Properties YouTubeConnectorProperties = null;
    private String pathToProxiesDirectory = null;
    private String pathToRequestsDirectory = null;
    private String myGroupId = null;
    
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        ConfigurationContextProvider configurationContextProvider = ConfigurationContextProvider.getInstance();
        ConfigurationContext cc = configurationContextProvider.getConfigurationContext();
        mediationLibUploadStub = new MediationLibraryUploaderStub(cc, esbServer.getBackEndUrl() + "MediationLibraryUploader");
        AuthenticateStub.authenticateStub("admin", "admin", mediationLibUploadStub);
        adminServiceStub = new MediationLibraryAdminServiceStub(cc, esbServer.getBackEndUrl() + "MediationLibraryAdminService");
        AuthenticateStub.authenticateStub("admin", "admin", adminServiceStub);
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            repoLocation = System.getProperty("connector_repo").replace("/", "\\");
        } else {
            repoLocation = System.getProperty("connector_repo").replace("/", "/");
        }
        proxyAdmin = new ProxyServiceAdminClient(esbServer.getBackEndUrl(), esbServer.getSessionCookie());
        ConnectorIntegrationUtil.uploadConnector(repoLocation, mediationLibUploadStub, YouTubeConnectorFileName);
        log.info("Sleeping for " + 30000 / 1000 + " seconds while waiting for synapse import");
        Thread.sleep(30000);
        adminServiceStub.updateStatus("{org.wso2.carbon.connector}" + CONNECTOR_NAME, CONNECTOR_NAME,
                "org.wso2.carbon.connector", "enabled");
        YouTubeConnectorProperties = ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);
        pathToProxiesDirectory = repoLocation + YouTubeConnectorProperties.getProperty("proxyDirectoryRelativePath");
        pathToRequestsDirectory = repoLocation + YouTubeConnectorProperties.getProperty("requestDirectoryRelativePath");
    }
    @Override
    protected void cleanup() {
        axis2Client.destroy();
    }

    @Test(enabled=false, groups = {"wso2.esb"}, description = "YouTube{getChannelsAuditDetailsAndCategoryId} integration test.")
    public void testGetChannelsWithMandatoryParametersAuditDetailsAndCategoryId() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/getChannelsAuditDetailsAndCategoryId.txt";
        String methodName = "getChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("auditDetails"),
                YouTubeConnectorProperties.getProperty("categoryId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channelListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"}, description = "YouTube{getChannelsInvalidPart} integration test.")
    public void testGetChannelsWithMandatoryParameterInvalidPart() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/getChannelsAuditDetailsAndCategoryId.txt";
        String methodName = "getChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invalidPart"),
                YouTubeConnectorProperties.getProperty("categoryId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("xxx", jsonObject.getJSONObject("error").getString("message"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"}, description = "YouTube{getChannelsInvalidCategoryId} integration test.")
    public void testgetChannelsWithInvalidIdMandatoryParameterCategoryId() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/getChannelsAuditDetailsAndCategoryId.txt";
        String methodName = "getChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("auditDetails"),
                YouTubeConnectorProperties.getProperty("invalidCategoryId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("Channel category not found.", jsonObject.getJSONObject("error").getString("message"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"}, description = "YouTube{getChannelsBrandingSettingsAndForUsername} integration test.")
    public void testGetChannelsWithMandatoryParametersBrandingSettingsAndForUsername() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/getChannelsbrandingSettingsAndForUsername.txt";
        String methodName = "getChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("brandingSettings"),
                YouTubeConnectorProperties.getProperty("forUsername"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channelListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{getChannelsBrandingSettingsAndInvalidForUsername} integration test.")
    public void testGetChannelsWithInvalidMandatoryParametersBrandingSettingsAndForUsername() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/getChannelsbrandingSettingsAndForUsername.txt";
        String methodName = "getChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("brandingSettings"),
                YouTubeConnectorProperties.getProperty("invalidForUsername"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channelListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    @Test(enabled=false, groups = {"wso2.esb"}, description = "YouTube{getChannelsContentDetailsAndId} integration test.")
    public void testGetChannelsWithMandatoryParametersContentDetailsAndId() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/getChannelsContentDetailsAndId.txt";
        String methodName = "getChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("contentDetails"),
                YouTubeConnectorProperties.getProperty("channelId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channelListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"}, description = "YouTube{getChannelsInvalidId} integration test.")
    public void testGetChannelsWithInvalidMandatoryParameterId() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/getChannelsContentDetailsAndId.txt";
        String methodName = "getChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("contentDetails"),
                YouTubeConnectorProperties.getProperty("invalidChannelId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channelListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"}, description = "YouTube{getChannelsContentOwnerDetailsAndMine} integration " +"test.")
    public void testGetChannelsWithMandatoryParametersContentOwnerDetailsAndMine() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/getChannelsContentOwnerDetailsAndMine.txt";
        String methodName = "getChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("contentOwnerDetails"),
                YouTubeConnectorProperties.getProperty("mine"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channelListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"}, description = "YouTube{getChannelsInvalidMine} integration " +"test.")
    public void testGetChannelsWithInvalidMandatoryParameterMine() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/getChannelsContentOwnerDetailsAndMine.txt";
        String methodName = "getChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ContentOwnerDetails"),
                YouTubeConnectorProperties.getProperty("invalidMine"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("Invalid boolean value: 'xxx'.", jsonObject.getJSONObject("error").getString("message"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"}, description = "YouTube{getChannelsIdAndMySubscribers} integration test.")
    public void testGetChannelsWithMandatoryParametersIdAndMySubscribers() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/getChannelsIdAndMySubscribers.txt";
        String methodName = "getChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("id"),
                YouTubeConnectorProperties.getProperty("mySubscribers"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channelListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"}, description = "YouTube{getChannelsInvalidMySubscribers} integration " +
            "test.")
    public void testGetChannelsWithInvalidMandatoryParameterMySubscribers() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/getChannelsIdAndMySubscribers.txt";
        String methodName = "getChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("id"),
                YouTubeConnectorProperties.getProperty("invalidMySubscribers"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("Invalid boolean value: 'xxx'.", jsonObject.getJSONObject("error").getString("message"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"}, description =
            "YouTube{getChannelsInvideoPromotionAndCategoryIdAndOptional} integration test.")
    public void testGetChannelsWithInvideoPromotionAndCategoryIdAndOptionalParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/getChannelsInvideoPromotionAndCategoryIdAndOptional.txt";
        String methodName = "getChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invideoPromotion"),
                YouTubeConnectorProperties.getProperty("categoryId"),
                YouTubeConnectorProperties.getProperty("maxResults"),
                YouTubeConnectorProperties.getProperty("pageToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channelListResponse", jsonObject.getString("kind"));
            Assert.assertEquals("CAUQAQ", jsonObject.getString("prevPageToken"));
            Assert.assertEquals("4", jsonObject.getJSONObject("pageInfo").getString("resultsPerPage"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"}, description =
            "YouTube{getChannelsInvideoPromotionAndCategoryIdAndInvalidMaxResult} integration test.")
    public void testGetChannelsWithInvideoPromotionAndCategoryIdAndInvalidMaxResultParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/getChannelsInvideoPromotionAndCategoryIdAndOptional.txt";
        String methodName = "getChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("InvideoPromotion"),
                YouTubeConnectorProperties.getProperty("CategoryId"),
                YouTubeConnectorProperties.getProperty("invalidMaxResults"),
                YouTubeConnectorProperties.getProperty("PageToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("Invalid unsigned integer value: '-1'.", jsonObject.getJSONObject("error").getString
                    ("message"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"}, description =
            "YouTube{getChannelsInvideoPromotionAndCategoryIdAndInvalidPageToken} integration test.")
    public void testGetChannelsWithInvideoPromotionAndCategoryIdAndInvalidPageTokenParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/getChannelsInvideoPromotionAndCategoryIdAndOptional.txt";
        String methodName = "getChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invideoPromotion"),
                YouTubeConnectorProperties.getProperty("categoryId"),
                YouTubeConnectorProperties.getProperty("maxResults"),
                YouTubeConnectorProperties.getProperty("invalidPageToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{getChannelsInvideoPromotionAndCategoryIdAndOptionalFields} integration test.")
    public void testGetChannelsWithInvideoPromotionAndCategoryIdAndOptionalFieldsParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + 
                "Channels/getChannelsInvideoPromotionAndCategoryIdAndOptionalFields.txt";
        String methodName = "getChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invideoPromotion"),
                YouTubeConnectorProperties.getProperty("categoryId"),
                YouTubeConnectorProperties.getProperty("maxResults"),
                YouTubeConnectorProperties.getProperty("pageToken"),
                YouTubeConnectorProperties.getProperty("fields"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channelListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description ="YouTube{updateChannelsBrandingSettingsChannelsParameters} integration test.")
    public void testUpdateChannelsWithBrandingSettingsChannelsParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + 
                "Channels/updateChannelsBrandingSettingsChannelsParameters.txt";
        String methodName = "updateChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("brandingSettings"),
                YouTubeConnectorProperties.getProperty("channelId"),
                YouTubeConnectorProperties.getProperty("channelDescription"),
                YouTubeConnectorProperties.getProperty("keywords"),
                YouTubeConnectorProperties.getProperty("moderateComments"),
                YouTubeConnectorProperties.getProperty("showRelatedChannels"),
                YouTubeConnectorProperties.getProperty("showBrowseView"),
                YouTubeConnectorProperties.getProperty("unsubscribedTrailer"),
                YouTubeConnectorProperties.getProperty("profileColor"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description ="YouTube{updateChannelsBrandingSettingsChannelsParametersInvalidProfileColor} integration " +
                    "test.")
    public void testUpdateChannelsWithBrandingSettingsParametersInvalidProfileColor() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + 
                "Channels/updateChannelsBrandingSettingsChannelsParameters.txt";
        String methodName = "updateChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("brandingSettings"),
                YouTubeConnectorProperties.getProperty("channelId"),
                YouTubeConnectorProperties.getProperty("channelDescription"),
                YouTubeConnectorProperties.getProperty("keywords"),
                YouTubeConnectorProperties.getProperty("moderateComments"),
                YouTubeConnectorProperties.getProperty("showRelatedChannels"),
                YouTubeConnectorProperties.getProperty("showBrowseView"),
                YouTubeConnectorProperties.getProperty("unsubscribedTrailer"),
                YouTubeConnectorProperties.getProperty("invalidProfileColor"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("Channel branding validation failed.", jsonObject.getJSONObject("error").getString
                    ("message"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{updateChannelsParmeterFeaturedChannelsTitle} integration test.")
    public void testUpdateChannelsWithParametersFeaturedChannelsTitle() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/updateChannelsParameterFeaturedChannelsTitle.txt";
        String methodName = "updateChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("brandingSettings"),
                YouTubeConnectorProperties.getProperty("channelId"),
                YouTubeConnectorProperties.getProperty("featuredChannelsTitle"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{updateChannelsParameterFeaturedChannelsUrls} integration test.")
    public void testUpdateChannelsWithParametersFeaturedChannelsUrls() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/updateChannelsParmeterFeaturedChannelsUrls.txt";
        String methodName = "updateChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("brandingSettings"),
                YouTubeConnectorProperties.getProperty("channelId"),
                YouTubeConnectorProperties.getProperty("featuredChannelsUrls"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{updateChannelsParameterDefaultTab} integration test.")
    public void testUpdateChannelsWithParameterDefaultTab() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/updateChannelsParmeterDefaultTab.txt";
        String methodName = "updateChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("brandingSettings"),
                YouTubeConnectorProperties.getProperty("channelId"),
                YouTubeConnectorProperties.getProperty("defaultTab"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{updateChannelsParameterFields} integration test.")
    public void testUpdateChannelsWithParameterFields() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/updateChannelsParmeterFields.txt";
        String methodName = "updateChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("brandingSettings"),
                YouTubeConnectorProperties.getProperty("channelId"),
                YouTubeConnectorProperties.getProperty("defaultTab"),
                YouTubeConnectorProperties.getProperty("fields"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    @Test(enabled=false, groups = {"wso2.esb"}, 
            description = "YouTube{updateChannelsBrandingSettingsImageParameters} integration test.")
    public void testUpdateChannelstWithBrandingSettingsImageParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/updateChannelsBrandingSettingsImageParameters.txt";
        String methodName = "updateChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("brandingSettings"),
                YouTubeConnectorProperties.getProperty("channelId"),
                YouTubeConnectorProperties.getProperty("default"),
                YouTubeConnectorProperties.getProperty("language"),
                YouTubeConnectorProperties.getProperty("value"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{updateChannelsInvideoPromotionPositionParameters} integration test.")
    public void testUpdateChannelsWithInvideoPromotionPositionParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/updateChannelsInvideoPromotionPositionParameters.txt";
        String methodName = "updateChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invideoPromotion"),
                YouTubeConnectorProperties.getProperty("channelId"),
                YouTubeConnectorProperties.getProperty("positionType"),
                YouTubeConnectorProperties.getProperty("cornerPosition"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("corner", jsonObject.getJSONObject("invideoPromotion").getJSONObject("position").getString("type"));
            Assert.assertEquals("bottomLeft", jsonObject.getJSONObject("invideoPromotion").getJSONObject("position").getString("cornerPosition"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{updateChannelsInvideoPromotionPositionParametersInvalidType} integration test.")
    public void testUpdateChannelsWithInvideoPromotionPositionParametersInvalidType() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/updateChannelsInvideoPromotionPositionParameters.txt";
        String methodName = "updateChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invideoPromotion"),
                YouTubeConnectorProperties.getProperty("channelId"),
                YouTubeConnectorProperties.getProperty("invalidPositionType"),
                YouTubeConnectorProperties.getProperty("cornerPosition"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("Invalid value for: xxx is not a valid value", jsonObject.getJSONObject("error").getString
                    ("message"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{updateChannelsInvideoPromotionPositionParametersInvalidCornerPosition} " +
                    "integration test.")
    public void testUpdateChannelsWithInvideoPromotionPositionParametersInvalidCornerPosition() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/updateChannelsInvideoPromotionPositionParameters.txt";
        String methodName = "updateChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invideoPromotion"),
                YouTubeConnectorProperties.getProperty("channelId"),
                YouTubeConnectorProperties.getProperty("positionType"),
                YouTubeConnectorProperties.getProperty("invalidCornerPosition"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("Invalid value for: xxx is not a valid value", jsonObject.getJSONObject("error").getString
                    ("message"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{updateChannelsInvideoPromotionDefaultTimingParameters} integration test.")
    public void testUpdateChannelsWithInvideoPromotionDefaultTimingParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/updateChannelsInvideoPromotionDefaultTimingParameters.txt";
        String methodName = "updateChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invideoPromotion"),
                YouTubeConnectorProperties.getProperty("channelId"),
                YouTubeConnectorProperties.getProperty("defaultTimingType"),
                YouTubeConnectorProperties.getProperty("defaultTimingOffsetMs"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{updateChannelsInvideoPromotionDefaultTimingParametersInvalidDefaultTimingType} integration test.")
    public void testUpdateChannelsWithInvideoPromotionDefaultTimingParametersInvalidDefaultTimingType() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/updateChannelsInvideoPromotionDefaultTimingParameters.txt";
        String methodName = "updateChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invideoPromotion"),
                YouTubeConnectorProperties.getProperty("channelId"),
                YouTubeConnectorProperties.getProperty("invalidDefaultTimingType"),
                YouTubeConnectorProperties.getProperty("defaultTimingOffsetMs"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("Invalid value for: xxx is not a valid value", jsonObject.getJSONObject("error").getString
                    ("message"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{updateChannelsInvideoPromotionDefaultTimingParametersInvalidDefaultTimingOffsetMs} integration test.")
    public void testUpdateChannelsWithInvideoPromotionDefaultTimingParametersInvalidDefaultTimingOffsetMs() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/updateChannelsInvideoPromotionDefaultTimingParameters.txt";
        String methodName = "updateChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invideoPromotion"),
                YouTubeConnectorProperties.getProperty("channelId"),
                YouTubeConnectorProperties.getProperty("defaultTimingType"),
                YouTubeConnectorProperties.getProperty("invalidDefaultTimingOffsetMs"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("Invalid value for UnsignedLong: -1", jsonObject.getJSONObject("error").getString("message"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"}, 
            description = "YouTube{updateChannelsInvideoPromotionItemsParameters} integration test.")
    public void testUpdateChannelstWithInvideoPromotionItemsParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/updateChannelsInvideoPromotionItemsParameters.txt";
        String methodName = "updateChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invideoPromotion"),
                YouTubeConnectorProperties.getProperty("channelId"),
                YouTubeConnectorProperties.getProperty("idType"),
                YouTubeConnectorProperties.getProperty("videoId"),
                YouTubeConnectorProperties.getProperty("timingType"),
                YouTubeConnectorProperties.getProperty("defaultTimingOffsetMs"),
                YouTubeConnectorProperties.getProperty("customMessage"),
                YouTubeConnectorProperties.getProperty("promotedByContentOwner"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{updateChannelsInvideoPromotionItemsParametersInvalidVideoId} integration test.")
    public void testUpdateChannelstWithInvideoPromotionItemsParametersInvalidVideoId() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/updateChannelsInvideoPromotionItemsParameters.txt";
        String methodName = "updateChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invideoPromotion"),
                YouTubeConnectorProperties.getProperty("channelId"),
                YouTubeConnectorProperties.getProperty("idType"),
                YouTubeConnectorProperties.getProperty("invalidVideoId"),
                YouTubeConnectorProperties.getProperty("timingType"),
                YouTubeConnectorProperties.getProperty("defaultTimingOffsetMs"),
                YouTubeConnectorProperties.getProperty("customMessage"),
                YouTubeConnectorProperties.getProperty("promotedByContentOwner"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{updateChannelsInvideoPromotionItemsParametersInvalidInvalidTimingType} integration test.")
    public void testUpdateChannelstWithInvideoPromotionItemsParametersInvalidTimingType() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/updateChannelsInvideoPromotionItemsParameters.txt";
        String methodName = "updateChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invideoPromotion"),
                YouTubeConnectorProperties.getProperty("channelId"),
                YouTubeConnectorProperties.getProperty("idType"),
                YouTubeConnectorProperties.getProperty("videoId"),
                YouTubeConnectorProperties.getProperty("invalidTimingType"),
                YouTubeConnectorProperties.getProperty("defaultTimingOffsetMs"),
                YouTubeConnectorProperties.getProperty("customMessage"),
                YouTubeConnectorProperties.getProperty("promotedByContentOwner"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("Invalid value for: xxx is not a valid value", jsonObject.getJSONObject("error").getString("message"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{updateChannelsInvideoPromotionItemsParametersWebsiteUrl} integration test.")
    public void testUpdateChannelstWithInvideoPromotionItemsParameterswebsiteUrl() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/updateChannelsInvideoPromotionItemsParameterswebsiteUrl.txt";
        String methodName = "updateChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invideoPromotion"),
                YouTubeConnectorProperties.getProperty("channelId"),
                YouTubeConnectorProperties.getProperty("idTypeWebsite"),
                YouTubeConnectorProperties.getProperty("websiteUrl"),
                YouTubeConnectorProperties.getProperty("timingType"),
                YouTubeConnectorProperties.getProperty("defaultTimingOffsetMs"),
                YouTubeConnectorProperties.getProperty("customMessage"),
                YouTubeConnectorProperties.getProperty("promotedByContentOwner"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#channel", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{updateChannelsInvideoPromotionItemsParametersInvalidWebsiteUrl} integration test.")
    public void testUpdateChannelstWithInvideoPromotionItemsParametersInvalidWebsiteUrl() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Channels/updateChannelsInvideoPromotionItemsParameterswebsiteUrl.txt";
        String methodName = "updateChannels";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Channels/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invideoPromotion"),
                YouTubeConnectorProperties.getProperty("channelId"),
                YouTubeConnectorProperties.getProperty("idTypeWebsite"),
                YouTubeConnectorProperties.getProperty("invalidWebsiteUrl"),
                YouTubeConnectorProperties.getProperty("timingType"),
                YouTubeConnectorProperties.getProperty("defaultTimingOffsetMs"),
                YouTubeConnectorProperties.getProperty("customMessage"),
                YouTubeConnectorProperties.getProperty("promotedByContentOwner"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 403);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
}
