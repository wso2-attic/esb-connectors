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

public class LiveStreamsIntegrationTest extends ESBIntegrationTest {

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
            description = "YouTube{getLiveStreamsParametersSnippetAndMine} integration test.")
    public void testGetLiveStreamsWithParametersSnippetAndMine() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "LiveStreams/getLiveStreamsSnippetAndMine.txt";
        String methodName = "getLiveStreams";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveStreams/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("snippet"),
                YouTubeConnectorProperties.getProperty("mine"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#liveStreamListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{getLiveStreamsParameteraIdAndIdAndOptionalParameters} integration test.")
    public void testGetLiveStreamsWithParametersIdAndIdAndOptionalParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "LiveStreams/getLiveStreamsIdAndIdAndOptionalParameter" +
                ".txt";
        String methodName = "getLiveStreams";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveStreams/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("id"),
                YouTubeConnectorProperties.getProperty("idLiveStreams"),
                YouTubeConnectorProperties.getProperty("maxResults"),
                YouTubeConnectorProperties.getProperty("pageToken"),
                YouTubeConnectorProperties.getProperty("fields"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#liveStreamListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{insertLiveStreamsParametersSnippetAndCdn} integration test.")
    public void testInsertLiveStreamsWithParametersSnippetAndCdn() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "LiveStreams/insertLiveStreamsSnippetAndCdn.txt";
        String methodName = "insertLiveStreams";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveStreams/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partLiveStreams"),
                YouTubeConnectorProperties.getProperty("titleLiveStreams"),
                YouTubeConnectorProperties.getProperty("format"),
                YouTubeConnectorProperties.getProperty("descriptionLiveStreams"),
                YouTubeConnectorProperties.getProperty("ingestionType"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#liveStream", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{updateLiveStreams} integration test.")
    public void testUpdateLiveStreamsWithParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "LiveStreams/updateLiveStreams.txt";
        String methodName = "updateLiveStreams";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveStreams/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("partLiveStreams"),
                YouTubeConnectorProperties.getProperty("idLiveStreams"),
                YouTubeConnectorProperties.getProperty("updateTitleLiveStreams"),
                YouTubeConnectorProperties.getProperty("format"),
                YouTubeConnectorProperties.getProperty("ingestionType"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#liveStream", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{deleteLiveStreams} integration test.")
    public void testDeleteLiveStreamsWithParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "LiveStreams/deleteLiveStreams.txt";
        String methodName = "deleteLiveStreams";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "LiveStreams/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("idDeleteLiveStreams"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName),
                    modifiedJsonString);
            Assert.assertTrue(responseHeader == 204);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
}