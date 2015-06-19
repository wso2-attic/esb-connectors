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

public class PlaylistItemsIntegrationTest extends ESBIntegrationTest {

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

    @Test(enabled=true, groups = {"wso2.esb"}, description = "YouTube{getPlaylistItemsId} integration test.")
    public void testGetPlaylistItemsWithMandatoryParametersPartId() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "PlaylistItems/getPlaylistItemsId.txt";
        String methodName = "getPlaylistItems";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "PlaylistItems/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("snippet"),
                YouTubeConnectorProperties.getProperty("playlistItemsId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#playlistItemListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=true, groups = {"wso2.esb"}, description = "YouTube{getPlaylistItemsPlaylistId} integration test.")
    public void testGetPlaylistItemsWithMandatoryParametersPartPlaylistId() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "PlaylistItems/getPlaylistItemsId.txt";
        String methodName = "PlaylistItemsList";

        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "PlaylistItems/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("snippet"),
                YouTubeConnectorProperties.getProperty("playlistId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#playlistItemListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=true, groups = {"wso2.esb"}, description = "YouTube{getPlaylistItemsOptionalParameter} " +
            "integration test.")
    public void testGetPlaylistItemsWithMandatoryParametersPartMaxResults() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "PlaylistItems/getPlaylistItemsOptionalParameter.txt";
        String methodName = "getPlaylistItems";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "PlaylistItems/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("snippet"),
                YouTubeConnectorProperties.getProperty("playlistId"),
                YouTubeConnectorProperties.getProperty("maxResults"),
                YouTubeConnectorProperties.getProperty("pageToken"),
                YouTubeConnectorProperties.getProperty("playlistItemsvideoId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#playlistItemListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=true, groups = {"wso2.esb"}, description = "YouTube{insertPlaylistItemsSnippet} integration test.")
    public void testInsertPlaylistItemsWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "PlaylistItems/insertPlaylistItemsSnippet.txt";
        String methodName = "insertPlaylistItems";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "PlaylistItems/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("snippet"),
                YouTubeConnectorProperties.getProperty("PlaylistItemsInsertplaylistId"),
                YouTubeConnectorProperties.getProperty("PlaylistItemsInsertkind"),
                YouTubeConnectorProperties.getProperty("PlaylistItemsInsertvideoId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#playlistItem", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=true, groups = {"wso2.esb"}, description = "YouTube{insertPlaylistItemsOptionalParameter} integration test.")
    public void testInsertPlaylistItemsWithOptionalParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "PlaylistItems/insertPlaylistItemsOptionalParameter.txt";
        String methodName = "insertPlaylistItems";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "PlaylistItems/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("snippet"),
                YouTubeConnectorProperties.getProperty("PlaylistItemsInsertplaylistId"),
                YouTubeConnectorProperties.getProperty("PlaylistItemsInsertkind"),
                YouTubeConnectorProperties.getProperty("PlaylistItemsInsertvideoId"),
                YouTubeConnectorProperties.getProperty("PlaylistItemsInsertposition"),
                YouTubeConnectorProperties.getProperty("PlaylistItemsInsertnote"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#playlistItem", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"}, description = "YouTube{updatePlaylistItems} integration test.")
    public void testUpdatePlaylistItemsUpdatetWithParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "PlaylistItems/updatePlaylistItems.txt";
        String methodName = "updatePlaylistItems";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "PlaylistItems/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("snippet"),
                YouTubeConnectorProperties.getProperty("PlaylistItemsInsertplaylistId"),
                YouTubeConnectorProperties.getProperty("PlaylistItemsUpdateplaylistId"),
                YouTubeConnectorProperties.getProperty("PlaylistItemsUpdatekind"),
                YouTubeConnectorProperties.getProperty("PlaylistItemsUpdatevideoId"),
                YouTubeConnectorProperties.getProperty("PlaylistItemsUpdatenote"),
                YouTubeConnectorProperties.getProperty("PlaylistItemsUpdatestartAt"),
                YouTubeConnectorProperties.getProperty("PlaylistItemsUpdateendAt"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#playlistItem", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=true, groups = {"wso2.esb"}, description = "YouTube{deletePlaylistItems} integration test.")
    public void testDeletePlaylistItemsWithParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "PlaylistItems/deletePlaylistItems.txt";
        String methodName = "deletePlaylistItems";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "PlaylistItems/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString, YouTubeConnectorProperties.getProperty("apiUrl"),
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("PlaylistItemsDeleteid"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 204);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
}
