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

public class LiveBroadcastsIntegrationTest extends ESBIntegrationTest {

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

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{getLiveBroadcastsIdAndBroadcastStatus} integration test.")
    public void testGetLiveBroadcastsWithParametersIdAndBroadcastStatus() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "LiveBroadcasts/getLiveBroadcastsIdAndBroadcastStatus.txt";
        String methodName = "getLiveBroadcasts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveBroadcasts/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("id"),
                YouTubeConnectorProperties.getProperty("broadcastStatus"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#liveBroadcastListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{getLiveBroadcastsInvalidIdAndBroadcastStatus} integration test.")
    public void testGetLiveBroadcastsWithParametersInvalidIdAndBroadcastStatus() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "LiveBroadcasts/getLiveBroadcastsIdAndBroadcastStatus.txt";
        String methodName = "getLiveBroadcasts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveBroadcasts/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invalidPart"),
                YouTubeConnectorProperties.getProperty("broadcastStatus"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("xxx", jsonObject.getJSONObject("error").getString("message"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{getLiveBroadcastsIdAndInvalidBroadcastStatus} integration test.")
    public void testGetLiveBroadcastsWithParametersIdAndInvalidBroadcastStatus() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "LiveBroadcasts/getLiveBroadcastsIdAndBroadcastStatus.txt";
        String methodName = "getLiveBroadcasts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveBroadcasts/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("id"),
                YouTubeConnectorProperties.getProperty("invalidBroadcastStatus"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{getLiveBroadcastsSnippetAndId} integration test.")
    public void testGetLiveBroadcastsWithParametersSnippetAndId() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "LiveBroadcasts/getLiveBroadcastsSnippetAndId.txt";
        String methodName = "getLiveBroadcasts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveBroadcasts/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("snippet"),
                YouTubeConnectorProperties.getProperty("liveBroadcastsId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#liveBroadcastListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{getLiveBroadcastsContentDetailsAndMine} integration test.")
    public void testGetLiveBroadcastsWithParametersContentDetailsAndMine() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "LiveBroadcasts/getLiveBroadcastsContentDetailsAndMine.txt";
        String methodName = "getLiveBroadcasts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveBroadcasts/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("contentDetails"),
                YouTubeConnectorProperties.getProperty("mine"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#liveBroadcastListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{getLiveBroadcastsContentDetailsAndInvalidMine} integration test.")
    public void testGetLiveBroadcastsWithParametersContentDetailsAndInvalidMine() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "LiveBroadcasts/getLiveBroadcastsContentDetailsAndMine.txt";
        String methodName = "getLiveBroadcasts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveBroadcasts/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("contentDetails"),
                YouTubeConnectorProperties.getProperty("invalidMine"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("Invalid boolean value: 'xxx'.", jsonObject.getJSONObject("error").getString("message"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{getLiveBroadcastsContentDetailsAndMineAndOptionalParameters} integration test.")
    public void testGetLiveBroadcastsWithParametersContentDetailsAndMineAndOptionalParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + 
                "LiveBroadcasts/getLiveBroadcastsContentDetailsAndMineAndOptionalParameters.txt";
        String methodName = "getLiveBroadcasts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveBroadcasts/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("contentDetails"),
                YouTubeConnectorProperties.getProperty("mine"),
                YouTubeConnectorProperties.getProperty("maxResults"),
                YouTubeConnectorProperties.getProperty("pageToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#liveBroadcastListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{getLiveBroadcastsContentDetailsAndMineAndInvalidMaxResults} integration test.")
    public void testGetLiveBroadcastsWithParametersContentDetailsAndMineAndInvalidMaxResults() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory +
                "LiveBroadcasts/getLiveBroadcastsContentDetailsAndMineAndOptionalParameters.txt";
        String methodName = "getLiveBroadcasts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveBroadcasts/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("contentDetails"),
                YouTubeConnectorProperties.getProperty("mine"),
                YouTubeConnectorProperties.getProperty("invalidMaxResults"),
                YouTubeConnectorProperties.getProperty("pageToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("Invalid unsigned integer value: '-1'.", jsonObject.getJSONObject("error").getString("message"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{getLiveBroadcastsContentDetailsAndMineAndInvalidPageToken} integration test.")
    public void testGetLiveBroadcastsWithParametersContentDetailsAndMineAndInvalidPageToken() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory +
                "LiveBroadcasts/getLiveBroadcastsContentDetailsAndMineAndOptionalParameters.txt";
        String methodName = "getLiveBroadcasts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveBroadcasts/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("contentDetails"),
                YouTubeConnectorProperties.getProperty("mine"),
                YouTubeConnectorProperties.getProperty("maxResults"),
                YouTubeConnectorProperties.getProperty("invalidPageToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("Bad Request", jsonObject.getJSONObject("error").getString("message"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{getLiveBroadcastsContentDetailsAndMineAndFields} integration test.")
    public void testGetLiveBroadcastsWithParametersContentDetailsAndMineAndFields() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory +
                "LiveBroadcasts/getLiveBroadcastsContentDetailsAndMineAndFields.txt";
        String methodName = "getLiveBroadcasts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveBroadcasts/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("contentDetails"),
                YouTubeConnectorProperties.getProperty("mine"),
                YouTubeConnectorProperties.getProperty("fields"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#liveBroadcastListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{insertLiveBroadcastsSnippetAndStatus} integration test.")
    public void testInsertLiveBroadcastsWithMandotaryParametersSnippetAndStatus() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "LiveBroadcasts/insertLiveBroadcastsSnippetAndStatus.txt";
        String methodName = "insertLiveBroadcasts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveBroadcasts/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("part"),
                YouTubeConnectorProperties.getProperty("scheduledEndTime"),
                YouTubeConnectorProperties.getProperty("scheduledStartTime"),
                YouTubeConnectorProperties.getProperty("liveBroadcaststitle"),
                YouTubeConnectorProperties.getProperty("privacyStatus"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#liveBroadcast", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{insertLiveBroadcastsSnippetAndStatusAndInvalidScheduledEndTime} integration test.")
    public void testInsertLiveBroadcastsWithMandotaryParametersSnippetAndStatusAndInvalidScheduledEndTime() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "LiveBroadcasts/insertLiveBroadcastsSnippetAndStatus.txt";
        String methodName = "insertLiveBroadcasts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveBroadcasts/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("part"),
                YouTubeConnectorProperties.getProperty("InvalidScheduledEndTime"),
                YouTubeConnectorProperties.getProperty("scheduledStartTime"),
                YouTubeConnectorProperties.getProperty("liveBroadcaststitle"),
                YouTubeConnectorProperties.getProperty("privacyStatus"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{insertLiveBroadcastsSnippetAndStatusAndInvalidScheduledStartTime} integration test.")
    public void testInsertLiveBroadcastsWithMandotaryParametersSnippetAndStatusAndInvalidScheduledStartTime() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "LiveBroadcasts/insertLiveBroadcastsSnippetAndStatus.txt";
        String methodName = "insertLiveBroadcasts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveBroadcasts/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("part"),
                YouTubeConnectorProperties.getProperty("scheduledEndTime"),
                YouTubeConnectorProperties.getProperty("InvalidScheduledStartTime"),
                YouTubeConnectorProperties.getProperty("liveBroadcaststitle"),
                YouTubeConnectorProperties.getProperty("privacyStatus"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{insertLiveBroadcastsSnippetAndStatusAndInvalidPrivacyStatus} integration test.")
    public void testInsertLiveBroadcastsWithMandotaryParametersSnippetAndStatusAndInvalidPrivacyStatus() throws 
            Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "LiveBroadcasts/insertLiveBroadcastsSnippetAndStatus.txt";
        String methodName = "insertLiveBroadcasts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveBroadcasts/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("part"),
                YouTubeConnectorProperties.getProperty("scheduledEndTime"),
                YouTubeConnectorProperties.getProperty("scheduledStartTime"),
                YouTubeConnectorProperties.getProperty("liveBroadcaststitle"),
                YouTubeConnectorProperties.getProperty("invalidPrivacyStatus"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("Invalid value for: xxx is not a valid value",
                    jsonObject.getJSONObject("error").getString("message"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{insertLiveBroadcastsSnippetAndStatusAndContentDetails} integration test.")
    public void testInsertLiveBroadcastsWithParametersSnippetAndStatusAndContentDetails() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + 
                "LiveBroadcasts/insertLiveBroadcastsSnippetAndStatusContentDetails.txt";
        String methodName = "insertLiveBroadcasts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveBroadcasts/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("allPart"),
                YouTubeConnectorProperties.getProperty("scheduledEndTime"),
                YouTubeConnectorProperties.getProperty("scheduledStartTime"),
                YouTubeConnectorProperties.getProperty("liveBroadcaststitle"),
                YouTubeConnectorProperties.getProperty("privacyStatus"),
                YouTubeConnectorProperties.getProperty("enableMonitorStream"),
                YouTubeConnectorProperties.getProperty("broadcastStreamDelayMs"),
                YouTubeConnectorProperties.getProperty("enableDvr"),
                YouTubeConnectorProperties.getProperty("enableContentEncryption"),
                YouTubeConnectorProperties.getProperty("enableEmbed"),
                YouTubeConnectorProperties.getProperty("recordFromStart"),
                YouTubeConnectorProperties.getProperty("startWithSlate"),
                YouTubeConnectorProperties.getProperty("enableClosedCaptions"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#liveBroadcast", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{insertLiveBroadcastsSnippetAndStatusAndContentDetailsAndFields} integration test.")
    public void testInsertLiveBroadcastsWithParametersSnippetAndStatusAndContentDetailsAndFields() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory +
                "LiveBroadcasts/insertLiveBroadcastsSnippetAndStatusContentDetailsFields.txt";
        String methodName = "insertLiveBroadcasts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveBroadcasts/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("allPart"),
                YouTubeConnectorProperties.getProperty("scheduledEndTime"),
                YouTubeConnectorProperties.getProperty("scheduledStartTime"),
                YouTubeConnectorProperties.getProperty("liveBroadcaststitle"),
                YouTubeConnectorProperties.getProperty("privacyStatus"),
                YouTubeConnectorProperties.getProperty("enableMonitorStream"),
                YouTubeConnectorProperties.getProperty("broadcastStreamDelayMs"),
                YouTubeConnectorProperties.getProperty("enableDvr"),
                YouTubeConnectorProperties.getProperty("enableContentEncryption"),
                YouTubeConnectorProperties.getProperty("enableEmbed"),
                YouTubeConnectorProperties.getProperty("recordFromStart"),
                YouTubeConnectorProperties.getProperty("startWithSlate"),
                YouTubeConnectorProperties.getProperty("enableClosedCaptions"),
                YouTubeConnectorProperties.getProperty("fields"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#liveBroadcast", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{updateLiveBroadcastsSnippetAndStatusAndContentDetails} integration test.")
    public void testUpdateLiveBroadcastsWithParametersSnippetAndStatusAndContentDetails() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory +
                "LiveBroadcasts/updateLiveBroadcastsSnippetAndStatusContentDetails.txt";
        String methodName = "updateLiveBroadcasts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveBroadcasts/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("allPart"),
                YouTubeConnectorProperties.getProperty("idLiveBroadcasts"),
                YouTubeConnectorProperties.getProperty("scheduledEndTime"),
                YouTubeConnectorProperties.getProperty("scheduledStartTime"),
                YouTubeConnectorProperties.getProperty("liveBroadcaststitle"),
                YouTubeConnectorProperties.getProperty("privacyStatus"),
                YouTubeConnectorProperties.getProperty("enableMonitorStream"),
                YouTubeConnectorProperties.getProperty("broadcastStreamDelayMs"),
                YouTubeConnectorProperties.getProperty("enableDvr"),
                YouTubeConnectorProperties.getProperty("enableContentEncryption"),
                YouTubeConnectorProperties.getProperty("enableEmbed"),
                YouTubeConnectorProperties.getProperty("recordFromStart"),
                YouTubeConnectorProperties.getProperty("startWithSlate"),
                YouTubeConnectorProperties.getProperty("enableClosedCaptions"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#liveBroadcast", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{updateLiveBroadcastsSnippetAndStatusAndContentDetailsOptinalParameter} integration" +
                    " test.")
    public void testUpdateLiveBroadcastsWithParametersSnippetAndStatusAndContentDetailsOptionalParmeter() throws
            Exception {
        String jsonRequestFilePath = pathToRequestsDirectory +
                "LiveBroadcasts/updateLiveBroadcastsSnippetAndStatusContentDetailsOptional.txt";
        String methodName = "updateLiveBroadcasts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveBroadcasts/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("allPart"),
                YouTubeConnectorProperties.getProperty("idLiveBroadcasts"),
                YouTubeConnectorProperties.getProperty("scheduledEndTime"),
                YouTubeConnectorProperties.getProperty("scheduledStartTime"),
                YouTubeConnectorProperties.getProperty("liveBroadcaststitle"),
                YouTubeConnectorProperties.getProperty("privacyStatus"),
                YouTubeConnectorProperties.getProperty("enableMonitorStream"),
                YouTubeConnectorProperties.getProperty("broadcastStreamDelayMs"),
                YouTubeConnectorProperties.getProperty("enableDvr"),
                YouTubeConnectorProperties.getProperty("enableContentEncryption"),
                YouTubeConnectorProperties.getProperty("enableEmbed"),
                YouTubeConnectorProperties.getProperty("recordFromStart"),
                YouTubeConnectorProperties.getProperty("startWithSlate"),
                YouTubeConnectorProperties.getProperty("enableClosedCaptions"),
                YouTubeConnectorProperties.getProperty("descriptionLiveBroadcasts"),
                YouTubeConnectorProperties.getProperty("fields"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#liveBroadcast", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{bindLiveBroadcastsParametersIdAndPart} integration test.")
    public void testBindLiveBroadcastsWithParameterParametersIdAndPart() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "LiveBroadcasts/bindLiveBroadcastsParametersIdAndPart.txt";
        String methodName = "bindLiveBroadcasts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveBroadcasts/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("idLiveBroadcasts"),
                YouTubeConnectorProperties.getProperty("snippet"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#liveBroadcast", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{bindLiveBroadcastsParametersIdAndPartAndFields} integration test.")
    public void testBindLiveBroadcastsWithParameterParametersIdAndPartAndFields() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + 
                "LiveBroadcasts/bindLiveBroadcastsParametersIdAndPartFields.txt";
        String methodName = "bindLiveBroadcasts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveBroadcasts/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("idLiveBroadcasts"),
                YouTubeConnectorProperties.getProperty("snippet"),
                YouTubeConnectorProperties.getProperty("fields"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#liveBroadcast", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{deleteLiveBroadcasts} integration test.")
    public void testDeleteLiveBroadcasts() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "LiveBroadcasts/deleteLiveBroadcasts.txt";
        String methodName = "deleteLiveBroadcasts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveBroadcasts/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("idLiveBroadcastsDelete"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
                    modifiedJsonString);
            Assert.assertTrue(responseHeader == 204);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{getActivitiesInvalidPageToken} integration test.")
    public void testGetActivitiesWithOptionalInvalidParameterPageToken() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/getActivitiesMineAndPageToken.txt";
        String methodName = "getActivities";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("Id"),
                YouTubeConnectorProperties.getProperty("Mine"),
                YouTubeConnectorProperties.getProperty("invalidpageToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListPublishedAfter} integration test.")
    public void testActivitiesListWithOptionalParametersPublishedAfter() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListminePublishedAfter.txt";
        String methodName = "ActivitiesList";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partid"),
                YouTubeConnectorProperties.getProperty("mine"),
                YouTubeConnectorProperties.getProperty("publishedAfter"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListInvalidPublishedAfter} integration test.")
    public void testActivitiesListWithOptionalInvalidParametersPublishedAfter() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListminePublishedAfter.txt";
        String methodName = "ActivitiesList";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partid"),
                YouTubeConnectorProperties.getProperty("mine"),
                YouTubeConnectorProperties.getProperty("invalidpublishedAfter"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListPublishedBefore} integration test.")
    public void testActivitiesListWithOptionalParametersPublishedBefore() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListminePublishedBefore.txt";
        String methodName = "ActivitiesList";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partid"),
                YouTubeConnectorProperties.getProperty("mine"),
                YouTubeConnectorProperties.getProperty("publishedBefore"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListInvalidPublishedBefore} integration test.")
    public void testActivitiesListWithOptionalInvalidParametersPublishedBefore() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListminePublishedBefore.txt";
        String methodName = "ActivitiesList";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partid"),
                YouTubeConnectorProperties.getProperty("mine"),
                YouTubeConnectorProperties.getProperty("invalidpublishedBefore"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListregionCode} integration test.")
    public void testActivitiesListWithOptionalParametersPublishedBefore() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListmineRegionCode.txt";
        String methodName = "ActivitiesList";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partid"),
                YouTubeConnectorProperties.getProperty("mine"),
                YouTubeConnectorProperties.getProperty("regionCode"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesListInvalidRegionCode} integration test.")
    public void testActivitiesListWithOptionalInvalidParametersPublishedBefore() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesListmineRegionCode.txt";
        String methodName = "ActivitiesList";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partid"),
                YouTubeConnectorProperties.getProperty("mine"),
                YouTubeConnectorProperties.getProperty("invalidregionCode"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesInsertSnippet} integration test.")
    public void testActivitiesInserttWithMandatoryParametersPartSnippet() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesInsertmineSnippet.txt";
        String methodName = "ActivitiesInsertSnippet";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partsnippet"),
                YouTubeConnectorProperties.getProperty("description"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesInsertInvalidSnippet} integration test.")
    public void testActivitiesInserttWithInvalidParametersPartSnippet() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesInsertmineSnippet.txt";
        String methodName = "ActivitiesInsertSnippet";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invalidpartsnippet"),
                YouTubeConnectorProperties.getProperty("description"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesInsertSnippetAndContentDetails} integration test.")
    public void testActivitiesInserttWithMandatoryParametersPartSnippetAndContentDetails() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesInsertmineSnippetAndContentDetails.txt";
        String methodName = "ActivitiesInsertSnippetAndContentDetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("snippetandcontentdetails"),
                YouTubeConnectorProperties.getProperty("kind"),
                YouTubeConnectorProperties.getProperty("videoId"),
                YouTubeConnectorProperties.getProperty("description"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{ActivitiesInsertInvalidSnippetAndContentDetails} integration test.")
    public void testActivitiesInserttWithInvalidParametersPartSnippetAndContentDetails() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/ActivitiesInsertmineSnippetAndContentDetails.txt";
        String methodName = "ActivitiesInsertSnippetAndContentDetails";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invalidpartsnippet"),
                YouTubeConnectorProperties.getProperty("kind"),
                YouTubeConnectorProperties.getProperty("videoId"),
                YouTubeConnectorProperties.getProperty("description"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
*/
}