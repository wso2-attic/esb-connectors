/*
*  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.connector.integration.test.spotify;

import org.apache.axis2.context.ConfigurationContext;
import org.json.JSONArray;
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

public class SpotifyConnectorIntegrationTest extends ESBIntegrationTest {
    private static final String CONNECTOR_NAME = "spotify";

    private MediationLibraryUploaderStub mediationLibUploadStub = null;

    private MediationLibraryAdminServiceStub adminServiceStub = null;

    private ProxyServiceAdminClient proxyAdmin;

    private String repoLocation = null;

    private String spotifyConnectorFileName = CONNECTOR_NAME + "-connector-1.0.0.zip";

    private Properties spotifyConnectorProperties = null;

    private String pathToProxiesDirectory = null;

    private String pathToRequestsDirectory = null;

    private String accessToken, accessCode;

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
        ConnectorIntegrationUtil.uploadConnector(repoLocation, mediationLibUploadStub, spotifyConnectorFileName);
        log.info("Sleeping for " + 30000 / 1000 + " seconds while waiting for synapse import");
        Thread.sleep(30000);
        adminServiceStub.updateStatus("{org.wso2.carbon.connector}" + CONNECTOR_NAME, CONNECTOR_NAME,
                "org.wso2.carbon.connector", "enabled");
        spotifyConnectorProperties = ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);
        pathToProxiesDirectory = repoLocation + spotifyConnectorProperties.getProperty("proxyDirectoryRelativePath");
        pathToRequestsDirectory = repoLocation + spotifyConnectorProperties.getProperty("requestDirectoryRelativePath");
    }

    @Override
    protected void cleanup() {
        axis2Client.destroy();
    }

    /**
     * Mandatory parameter test case for getCode method.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, description = "spotify {getCodeForAccessToken} integration test " +
            "with mandatory parameter.")
    public void testGetCode() throws Exception {
        // Invoking the testGetCode method to derive the code which will be used to get access token
        String jsonRequestFilePath = pathToRequestsDirectory + "getCodeForAccessToken.txt";
        String methodName = "spotify_getCodeForAccessToken";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            accessCode = jsonObject.getString("code");
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getToken from code method.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"testGetCode"},
            description = "spotify {testTokenFromCode} integration test " +
                    "with mandatory parameter.")
    public void testTokenFromCode() throws Exception {
        // Invoking the testTokenFromCode method to derive the code which will be used to get access token
        String jsonRequestFilePath = pathToRequestsDirectory + "getAccessTokenFromAuthorization.txt";
        String methodName = "spotify_getAccessTokenFromAuthorization";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, accessCode, spotifyConnectorProperties.getProperty("redirectUri"), spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            accessToken = jsonObject.getString("access-token");
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }

    }

    /**
     * Mandatory parameter test case for getAnArtist method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getAnArtist} integration test with mandatory parameter.")
    public void testGetAnArtistWithMandatoryParameter() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAnArtist_Mandatory.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("artistId1"));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            JSONObject jsonObject = new JSONObject(modifiedJsonString);
            Assert.assertEquals(jsonObject.getString("artistId"), jsonResponse.get("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getAnArtist method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getAnArtist} integration test with negative case.")
    public void testGetAnArtistWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAnArtist_Negative.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getSeveralArtists method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getSeveralArtists} integration test with mandatory parameter.")
    public void testGetSeveralArtistsWithMandatoryParameter() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSeveralArtists_Mandatory.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("artistId1") + "," + spotifyConnectorProperties.getProperty("artistId2"));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            JSONObject jsonObject = new JSONObject(modifiedJsonString);
            String[] ids = jsonObject.getString("artistIds").split(",");
            JSONArray artists = jsonResponse.getJSONArray("artists");
            boolean exi = false;
            for (int i = 0; i < artists.length(); i++) {
                JSONObject a = (JSONObject) artists.get(i);
                if (a.getString("id").equals(ids[0]) || a.getString("id").equals(ids[1])) {
                    exi = true;
                }
            }
            Assert.assertEquals(exi, true);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getSeveralArtists method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getSeveralArtists} integration test with negative case.")
    public void testGetSeveralArtistsWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSeveralArtists_Negative.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            String jsonObj = jsonObject.getString("artists");
            Assert.assertEquals(jsonObj, "[null,null]");
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getAlbumsOfAnArtist method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getAlbumsOfAnArtist} integration test with mandatory parameter.")
    public void testGetAlbumsOfAnArtist() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAlbumsOfAnArtist_Mandatory.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("artistIdToGetAlbums"));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("items"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getAlbumsOfAnArtist method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getAlbumsOfAnArtist} integration test with negative case.")
    public void testGetAlbumsOfAnArtistWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAlbumsOfAnArtist_Negative.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional test case for getAlbumsOfAnArtist method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getAlbumsOfAnArtist} integration test with optional case.")
    public void testGetAlbumsOfAnArtistWithOptionalCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAlbumsOfAnArtist_Optional.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("artistIdToGetAlbums"));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("items"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


    /**
     * Mandatory parameter test case for getTopTracksOfAnArtist method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getTopTracksOfAnArtist} integration test with mandatory parameters.")
    public void testGetTopTracksOfAnArtistWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getTopTracksOfAnArtist_Mandatory.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("artistId1"));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            JSONObject jsonObject = new JSONObject(modifiedJsonString);
            JSONArray artists = ((JSONObject) jsonResponse.getJSONArray("tracks").get(0)).getJSONArray("artists");
            boolean exi = false;
            for (int i = 0; i < artists.length(); i++) {
                JSONObject a = (JSONObject) artists.get(i);
                if (a.getString("id").equals(jsonObject.getString("artistId"))) {
                    exi = true;
                }
            }
            Assert.assertEquals(exi, true);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getTopTracksOfAnArtist method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getTopTracksOfAnArtist} integration test with negative case.")
    public void testGetTopTracksOfAnArtistWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getTopTracksOfAnArtist_Negative.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


    /**
     * Mandatory parameter test case for getRelatedArtistsToAnArtist method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getRelatedArtistsToAnArtist} integration test with mandatory parameters.")
    public void testGetRelatedArtistsToAnArtistWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getRelatedArtistsToAnArtist_Mandatory.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("artistId1"));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("artists"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getRelatedArtistsToAnArtist method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getRelatedArtistsToAnArtist} integration test with negative case.")
    public void testGetRelatedArtistsToAnArtistWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getRelatedArtistsToAnArtist_Negative.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getATrack method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getATrack} integration test with mandatory parameters.")
    public void testGetATrackWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getATrack_Mandatory.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("trackId1"));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            JSONObject jsonObject = new JSONObject(modifiedJsonString);
            Assert.assertEquals(jsonObject.getString("trackId"), jsonResponse.get("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getATrack method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getATrack} integration test with negative case.")
    public void testGetATrackWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getATrack_Negative.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getSeveralTracks method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getSeveralTracks} integration test with mandatory parameters.")
    public void testGetSeveralTracksWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSeveralTracks_Mandatory.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("trackId1") + "," + spotifyConnectorProperties.getProperty("trackId2"));
        try {
            JSONObject jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            JSONObject jsonObject = new JSONObject(modifiedJsonString);
            String[] ids = jsonObject.getString("trackIds").split(",");
            JSONArray tracks = jsonResponse.getJSONArray("tracks");
            boolean exi = false;
            for (int i = 0; i < tracks.length(); i++) {
                JSONObject t = (JSONObject) tracks.get(i);
                if (t.getString("id").equals(ids[0]) || t.getString("id").equals(ids[1])) {
                    exi = true;
                }
            }
            Assert.assertEquals(exi, true);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getSeveralTracks method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getSeveralTracks} integration test with negative case.")
    public void testGetSeveralTracksWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSeveralTracks_Negative.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            JSONObject jsonObject = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            String jsonObj = jsonObject.getString("tracks");
            Assert.assertEquals(jsonObj, "[null,null]");
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for getProfileOfCurrentUser method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getProfileOfCurrentUser} " +
            "integration test.")
    public void getProfileOfCurrentUser() throws Exception {
        String methodName = "spotify";
        String jsonRequestFilePath = pathToRequestsDirectory + "getToken.txt";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"));
        JSONObject jsonObject = new JSONObject(modifiedJsonString);
        jsonObject.append("methodName", "getProfileOfCurrentUser");
        modifiedJsonString = jsonObject.toString().replace("[\"getProfileOfCurrentUser\"]", "\"getProfileOfCurrentUser\"");
        JSONObject jsonResponse = null;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for checkTracksOfCurrentUser method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"getTracksOfCurrentUser"}, description = "spotify {checkTracksOfCurrentUser} integration test.")
    public void checkTracksOfCurrentUserWithMandatoryCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "checkTracksOfCurrentUser_Mandatory.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"), spotifyConnectorProperties.getProperty("myTrackId") + "," + spotifyConnectorProperties.getProperty("myTrackId1"));
        try {
            String jsonResponse = ConnectorIntegrationUtil.sendRequestString(getProxyServiceURL(methodName), modifiedJsonString).toString();
            Assert.assertTrue(jsonResponse.contains("[ true, true ]"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Invalid parameter test case for checkTracksOfCurrentUser method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {checkTracksOfCurrentUser} integration test.")
    public void checkTracksOfCurrentUserWithInvalidCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "checkTracksOfCurrentUser_Invalid.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative parameter test case for checkTracksOfCurrentUser method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {checkTracksOfCurrentUser} integration test.")
    public void checkTracksOfCurrentUserWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "checkTracksOfCurrentUser_Negative.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"));
        try {
            String jsonResponse = ConnectorIntegrationUtil.sendRequestString(getProxyServiceURL(methodName), modifiedJsonString).toString();
            Assert.assertTrue(jsonResponse.contains("[ false, false ]"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for getTracksOfCurrentUser method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getTracksOfCurrentUser} integration test.")
    public void getTracksOfCurrentUserWithOptionalParameters() throws Exception {
        String methodName = "spotify";
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String jsonRequestFilePath = pathToRequestsDirectory + "getTracksOfCurrentUser_Optional.txt";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"));
        JSONObject jsonResponse = null;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            String jsonObj = jsonResponse.getString("total");
            Assert.assertNotEquals(jsonObj, 0);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Test case for getTracksOfCurrentUser method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getTracksOfCurrentUser} integration test.")
    public void getTracksOfCurrentUser() throws Exception {
        String methodName = "spotify";
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String jsonRequestFilePath = pathToRequestsDirectory + "getTracksOfCurrentUser_Mandatory.txt";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"));
        JSONObject jsonResponse = null;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            String myTrackId = ((JSONObject) jsonResponse.getJSONArray("items").get(0)).getJSONObject("track").getString("id");
            String myTrackId1 = ((JSONObject) jsonResponse.getJSONArray("items").get(1)).getJSONObject("track").getString("id");
            spotifyConnectorProperties.setProperty("myTrackId", myTrackId);
            spotifyConnectorProperties.setProperty("myTrackId1", myTrackId1);
            String jsonObj = jsonResponse.getString("total");
            Assert.assertNotEquals(jsonObj, 0);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for saveTracksForCurrentUser method.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, description = "spotify {saveTracksOfCurrentUser} integration test.")
    public void saveTracksForCurrentUser() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "saveTracksForCurrentUser_Mandatory.txt";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String methodName = "spotify";
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"), spotifyConnectorProperties.getProperty("trackId1") + "," + spotifyConnectorProperties.getProperty("trackId2"));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Invalid parameter test case for saveTracksForCurrentUser method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {saveTracksOfCurrentUser} integration test.")
    public void saveTracksForCurrentUserWithInvalidCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "saveTracksForCurrentUser_Invalid.txt";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String methodName = "spotify";
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for removeTracksOfCurrentUser method.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, dependsOnMethods = {"getTracksOfCurrentUser"}, description = "spotify {removeTracksOfCurrentUser} integration test.")
    public void removeTracksOfCurrentUserWithMandatoryParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "removeTracksOfCurrentUser_Mandatory.txt";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String methodName = "spotify";
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"), spotifyConnectorProperties.getProperty("myTrackId"));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Invalid test case for removeTracksOfCurrentUser method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {removeTracksOfCurrentUser} integration test.")
    public void removeTracksOfCurrentUserWithInvalidCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "removeTracksOfCurrentUser_Invalid.txt";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String methodName = "spotify";
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getAnAlbumMetadata method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getAnAlbumMetadata} integration test with mandatory parameter.")
    public void testGetAnAlbumMetadataWithMandatoryParameter() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAnAlbumMetadata_Mandatory.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("albumId1"));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            JSONObject jsonObject = new JSONObject(modifiedJsonString);
            Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("albumId"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getAnAlbumMetadata method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getAnAlbumMetadata} integration test with negative case.")
    public void testGetAnAlbumMetadataWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAnAlbumMetadata_Negative.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "non existing id");
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getSeveralAlbums method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getSeveralAlbums} integration test with mandatory parameter.")
    public void testGetSeveralAlbumsWithMandatoryParameter() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSeveralAlbums_Mandatory.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("albumId1") + "," + spotifyConnectorProperties.getProperty("albumId2"));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.length() > 0);
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getSrveralAlbums method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getSrveralAlbums} integration test with negative case.")
    public void testGetSeveralAlbumsWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSeveralAlbums_Negative.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            String jsonObj = jsonResponse.getString("albums");
            Assert.assertEquals(jsonObj, "[null]");

        } finally {

            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getAnAlbumsTracks method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getAnAlbumsTracks} integration test with mandatory parameter.")
    public void testGetAnAlbumsTracksWithMandatoryParameter() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAnAlbumsTracks_Mandatory.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("albumId1"));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.length() > 0);
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for getAnAlbumsTracks method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getAnAlbumsTracks} integration test with optional parameter.")
    public void testGetAnAlbumsTracksWithOptionalParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAnAlbumsTracks_Optional.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("albumId1"));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            JSONObject jsonObject = new JSONObject(modifiedJsonString);
            Assert.assertEquals(jsonResponse.getString("limit"), jsonObject.get("spotifylimit"));
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getAnAlbumsTracks method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getAnAlbumsTracks} integration test with negative case.")
    public void testGetAnAlbumsTracksWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAnAlbumsTracks_Negative.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "non existing id");
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for searchForAnItem method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {searchForAnItem} integration test with mandatory parameter.")
    public void testSearchForAnItemWithMandatoryParameter() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchForAnItem_Mandatory.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("searchQueryForArtist"));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.length() > 0);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchForAnItem method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {searchForAnItem} integration test with optional parameter.")
    public void testSearchForAnItemWithOptionalParameters() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchForAnItem_Optional.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("albumId1"));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            JSONObject jsonObject = new JSONObject(jsonString);
            Assert.assertEquals(jsonResponse.getJSONObject("albums").getString("limit"), jsonObject.get("spotifylimit"));
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for searchForAnItem method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {searchForAnItem} integration test with negative case.")
    public void testSearchForAnItemWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchForAnItem_Negative.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), jsonString);
            Assert.assertEquals(jsonResponse.getJSONObject("albums").getString("total"), "0");
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getAListUsersPlaylists method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getAListUsersPlaylists} integration test with mandatory parameter.")
    public void testGetAListUsersPlaylistsMandatoryParameter() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAListUsersPlaylists_Mandatory.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"), spotifyConnectorProperties.getProperty("userId"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.length() > 0);
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for getAListUsersPlaylists method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {GetAListUsersPlaylists} integration test with optional parameter.")
    public void testGetAListUsersPlaylistsOptionalParameter() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAListUsersPlaylists_Optional.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"), spotifyConnectorProperties.getProperty("userId"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            JSONObject jsonObject = new JSONObject(jsonString);
            Assert.assertEquals(jsonResponse.getString("limit"), jsonObject.get("spotifylimit"));
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for GetAListUsersPlaylists method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {GetAListUsersPlaylists} integration test with negative case.")
    public void testGetAListUsersPlaylistsWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAListUsersPlaylists_Negative.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals(jsonResponse.getString("total"), "0");
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getAPlaylist method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAPlaylistMandatoryParameter"}, description = "spotify {getAListUsersLPlaylists} integration test with mandatory parameter.")
    public void testGetAPlaylistMandatoryParameter() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAPlaylist_Mandatory.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"), spotifyConnectorProperties.getProperty("userId"), spotifyConnectorProperties.getProperty("playListId"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            JSONObject jsonObject = new JSONObject(modifiedJsonString);
            Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("playListId"));
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for getALPlaylist method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAPlaylistMandatoryParameter"}, description = "spotify {getAPlaylist} integration test with optional parameter.")
    public void testGetAPlaylistOptionalParameter() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAPlaylist_Optional.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"), spotifyConnectorProperties.getProperty("userId"), spotifyConnectorProperties.getProperty("playListId"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertFalse(jsonResponse.getJSONObject("owner").has("href"));
        } finally {

            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getAPlaylist method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {getAPlaylist} integration test with negative case.")
    public void testGetAPlaylistWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getAPlaylist_Negative.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        int headerResponse;
        try {
            headerResponse = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals(headerResponse, 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for createAPlaylist method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {createAPlaylist} integration test with mandatory parameter.")
    public void testCreateAPlaylistMandatoryParameter() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createAPlaylist_Mandatory.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"), spotifyConnectorProperties.getProperty("userId"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            spotifyConnectorProperties.setProperty("playListId", jsonResponse.getString("id"));
            JSONObject jsonObject = new JSONObject(jsonString);
            Assert.assertEquals(jsonResponse.getString("name"), jsonObject.get("name"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for createAPlaylist method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {createAPlaylist} integration test with optional parameter.")
    public void testCreateAPlaylistOptionalParameter() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createAPlaylist_Optional.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"), spotifyConnectorProperties.getProperty("userId"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            JSONObject jsonObject = new JSONObject(jsonString);
            Assert.assertEquals(jsonResponse.getString("name"), jsonObject.get("name"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for createAPlaylist method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {createAPlaylist} integration test with negative case.")
    public void testCreateAPlaylistWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "createAPlaylist_Negative.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        int headerResponse;
        try {
            headerResponse = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals(headerResponse, 403);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for addTracksToAPlaylist from queryParameter method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAPlaylistMandatoryParameter"}, description = "spotify {addTracksToAPlaylistFromURI} integration test with mandatory parameter.")
    public void testAddTracksToAPlaylistFromURIWithMandatory() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "addTracksToAPlaylistFromURI_Mandatory.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"), spotifyConnectorProperties.getProperty("userId"), spotifyConnectorProperties.getProperty("playListId"), "spotify:track:" + spotifyConnectorProperties.getProperty("trackId1") + ",spotify:track:" + spotifyConnectorProperties.getProperty("trackId2"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        int headerResponse;
        try {
            headerResponse = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals(headerResponse, 201);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for addTracksToAPlaylistFromURI from queryParameter method with optional parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAPlaylistMandatoryParameter"}, description = "spotify {addTracksToAPlaylistFromURI} integration test with optional parameter.")
    public void testAddTracksToAPlaylistFromURIWithOptional() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "addTracksToAPlaylistFromURI_Optional.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"), spotifyConnectorProperties.getProperty("userId"), spotifyConnectorProperties.getProperty("playListId"), "spotify:track:" + spotifyConnectorProperties.getProperty("trackId1") + ",spotify:track:" + spotifyConnectorProperties.getProperty("trackId2"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        int headerResponse;
        try {
            headerResponse = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals(headerResponse, 201);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for addTracksToAPlaylistFromURI method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {addTracksToAPlaylistFromURI} integration test with negative case.")
    public void testAddTracksToAPlaylistFromURIWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "addTracksToAPlaylistFromURI_Negative.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        int headerResponse;
        try {
            headerResponse = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue((headerResponse == 400) || (headerResponse == 403));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for addTracksToAPlaylist from requestBody method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAPlaylistMandatoryParameter"}, description = "spotify {addTracksToAPlaylistFromRequestBody} integration test with mandatory parameter.")
    public void testAddTracksToAPlaylistFromRequestBody() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "addTracksToAPlaylistFromRequestBody.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"), spotifyConnectorProperties.getProperty("userId"), spotifyConnectorProperties.getProperty("playListId"), "[\"spotify:track:" + spotifyConnectorProperties.getProperty("trackId1") + "\",\"spotify:track:" + spotifyConnectorProperties.getProperty("trackId2") + "\"]");
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        int headerResponse;
        try {
            headerResponse = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals(headerResponse, 201);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for addTracksToAPlaylistFromRequestBody method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {addTracksToAPlaylistFromRequestBody} integration test with negative case.")
    public void testAddTracksToAPlaylistFromRequestBodyWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "addTracksToAPlaylistFromURI_Negative.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        int headerResponse;
        try {
            headerResponse = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue((headerResponse == 400) || (headerResponse == 403));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for changeAPlaylistsDetails method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAPlaylistMandatoryParameter"}, description = "spotify {changeAPlaylistsDetails} integration test with mandatory parameter.")
    public void testChangeAPlaylistsDetailsMandatoryParameter() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "changeAPlaylistsDetails_Mandatory.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"), spotifyConnectorProperties.getProperty("userId"), spotifyConnectorProperties.getProperty("playListId"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        int headerResponse;
        try {
            headerResponse = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals(headerResponse, 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for changeAPlaylistsDetails method with optional parameter name.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAPlaylistMandatoryParameter"}, description = "spotify {changeAPlaylistsDetails} integration test with optional parameter.")
    public void testChangeAPlaylistsDetailsOptionalParameterName() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "changeAPlaylistsDetails_OptionalName.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"), spotifyConnectorProperties.getProperty("userId"), spotifyConnectorProperties.getProperty("playListId"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        int headerResponse;
        try {
            headerResponse = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals(headerResponse, 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for changeAPlaylistsDetails method with optional parameter public.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAPlaylistMandatoryParameter"}, description = "spotify {changeAPlaylistsDetails} integration test with optional parameter.")
    public void testChangeAPlaylistsDetailsOptionalParameterPublic() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "changeAPlaylistsDetails_OptionalPublic.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"), spotifyConnectorProperties.getProperty("userId"), spotifyConnectorProperties.getProperty("playListId"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        int headerResponse;
        try {
            headerResponse = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals(headerResponse, 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for changeAPlaylistsDetails method.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {changeAPlaylistsDetails} integration test with negative case.")
    public void testChangeAPlaylistsDetailsWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "changeAPlaylistsDetails_Negative.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        int headerResponse;
        try {
            headerResponse = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue((headerResponse == 400) || (headerResponse == 403));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for replaceAPlaylistsTracksFromURI method
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAPlaylistMandatoryParameter"}, description = "spotify {replaceAPlaylistsTracksFromURI} integration test with mandatory parameter.")
    public void testReplaceAPlaylistsTracksWithPositiveCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "replaceAPlaylistsTracksFromURI_Positive.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"), spotifyConnectorProperties.getProperty("userId"), spotifyConnectorProperties.getProperty("playListId"), "spotify:track:" + spotifyConnectorProperties.getProperty("trackId3") + ",spotify:track:" + spotifyConnectorProperties.getProperty("trackId4"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        int headerResponse;
        try {
            headerResponse = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals(headerResponse, 201);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for replaceAPlaylistsTracksFromURI method
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {replaceAPlaylistsTracksFromURI} integration test with negative case.")
    public void testReplaceAPlaylistsTracksWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "replaceAPlaylistsTracksFromURI_Negative.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        int headerResponse;
        try {
            headerResponse = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue((headerResponse == 400) || (headerResponse == 403));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Positive test case for replaceAPlaylistsTracksFromRequestBody method
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testCreateAPlaylistMandatoryParameter"}, description = "spotify {replaceAPlaylistsTracksFromURI} integration test with mandatory parameter.")
    public void testReplaceAPlaylistsTracksFromRequestBodyWithPositiveCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "replaceAPlaylistsTracksFromRequestBody_Positive.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"), spotifyConnectorProperties.getProperty("userId"), spotifyConnectorProperties.getProperty("playListId"), "[\"spotify:track:" + spotifyConnectorProperties.getProperty("trackId3") + "\",\"spotify:track:" + spotifyConnectorProperties.getProperty("trackId4") + "\"]");
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        int headerResponse;
        try {
            headerResponse = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals(headerResponse, 201);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for replaceAPlaylistsTracksFromRequestBody method
     */
    @Test(enabled = true, groups = {"wso2.esb"}, description = "spotify {replaceAPlaylistsTracksFromURI} integration test with negative case.")
    public void testReplaceAPlaylistsTracksFromRequestBodyWithNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "replaceAPlaylistsTracksFromRequestBody_Negative.txt";
        String methodName = "spotify";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, spotifyConnectorProperties.getProperty("clientId"), spotifyConnectorProperties.getProperty("clientSecret"), spotifyConnectorProperties.getProperty("grantType"), spotifyConnectorProperties.getProperty("refreshToken"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        int headerResponse;
        try {
            headerResponse = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue((headerResponse == 400) || (headerResponse == 403));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
}