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

public class ActivitiesIntegrationTest extends ESBIntegrationTest {
    
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
            description = "YouTube{getActivitiesSnippetAndChannelId} integration test.")
    public void testGetActivitiesWithMandatoryParametersSnippetAndChannelId() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/getActivitiesSnippetAndChannelId.txt";
        String methodName = "getActivities";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("Snippet"),
                YouTubeConnectorProperties.getProperty("ChannelId"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#activityListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"}, 
            description = "YouTube{getActivitiesIdAndHome} integration test.")
    public void testGetActivitiesWithMandatoryParametersIdAndHome() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/getActivitiesIdAndHome.txt";
        String methodName = "getActivities";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("Id"),
                YouTubeConnectorProperties.getProperty("Home"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#activityListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    
    @Test(enabled=false, groups = {"wso2.esb"}, 
            description = "YouTube{getActivitiesContentDetailsAndMine} integration test.")
    public void testGetActivitiesWithMandatoryParametersContentDetailsAndMine() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/getActivitiesContentDetailsAndMine.txt";
        String methodName = "getActivities";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("ContentDetails"),
                YouTubeConnectorProperties.getProperty("Mine"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("youtube#activityListResponse", jsonObject.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"}, 
            description = "YouTube{getActivitiesInvalidSnippetAndChannelId} integration test.")
    public void testGetActivitiesWithInvalidSnippetAndChannelId() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/getActivitiesSnippetAndChannelId.txt";
        String methodName = "getActivities";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("invalidPartId"),
                YouTubeConnectorProperties.getProperty("mine"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
   
    @Test(enabled=false, groups = {"wso2.esb"}, 
            description = "YouTube{getActivitiesInvalidHome} integration test.")
    public void testGetActivitiesWithInvalidParameterHome() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/getActivitiesIdAndHome.txt";
        String methodName = "getActivities";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "Activities/" + methodName + ".xml";
        String modifiedJsonString = String.format(jsonString,
                YouTubeConnectorProperties.getProperty("client_id"),
                YouTubeConnectorProperties.getProperty("client_secret"),
                YouTubeConnectorProperties.getProperty("grant_type"),
                YouTubeConnectorProperties.getProperty("refresh_token"),
                YouTubeConnectorProperties.getProperty("Id"),
                YouTubeConnectorProperties.getProperty("invalidhome"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    @Test(enabled=false, groups = {"wso2.esb"},
            description = "YouTube{getActivitiesMaxResults} integration test.")
    public void testGetActivitiesWithOptionalParameterMaxResults() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/getActivitiesMineAndMaxResults.txt";
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
                YouTubeConnectorProperties.getProperty("MaxResults"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("4", jsonObject.getJSONObject("pageInfo").getString("resultsPerPage"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{getActivitiesInvalidMaxResults} integration test.")
    public void testGetActivitiesWithOptionalInvalidParameterMaxResults() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "Activities/getActivitiesMineAndMaxResults.txt";
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
                YouTubeConnectorProperties.getProperty("invalidmaxResults"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
/*
    @Test(groups = {"wso2.esb"}, description = "YouTube{getActivitiesPageToken} integration test.")
    public void testGetActivitiesWithOptionalParameterPageToken() throws Exception {
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
                YouTubeConnectorProperties.getProperty("PageToken"));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("CAUQAA", jsonObject.getJSONObject("pageInfo").getString("resultsPerPage"));
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