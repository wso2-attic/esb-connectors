/*
 * Copyright (c) 2014-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.connector.integration.test.gplus;

import org.apache.axis2.context.ConfigurationContext;
import org.json.JSONObject;
import org.json.JSONArray;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
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

public class GooglePlusTestCase extends ESBIntegrationTest {
    private ProxyServiceAdminClient proxyAdmin;
    private String pathToProxiesDirectory = null;
    private String pathToRequestsDirectory = null;
    private Properties googlePlusConnectorProperties = null;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();

        ConfigurationContextProvider configurationContextProvider =
                ConfigurationContextProvider.getInstance();
        ConfigurationContext cc = configurationContextProvider.getConfigurationContext();
        MediationLibraryUploaderStub mediationLibUploadStub = new MediationLibraryUploaderStub(cc,
                esbServer
                        .getBackEndUrl() +
                        "MediationLibraryUploader");
        AuthenticateStub.authenticateStub("admin", "admin", mediationLibUploadStub);

        MediationLibraryAdminServiceStub adminServiceStub = new MediationLibraryAdminServiceStub(cc,
                esbServer
                        .getBackEndUrl() +
                        "MediationLibraryAdminService");

        AuthenticateStub.authenticateStub("admin", "admin", adminServiceStub);

        String repoLocation;
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            repoLocation = System.getProperty("connector_repo").replace("/", "\\");
        } else {
            repoLocation = System.getProperty("connector_repo").replace("/", "/");
        }
        proxyAdmin = new ProxyServiceAdminClient(esbServer.getBackEndUrl(),
                esbServer.getSessionCookie());

        String CONNECTOR_NAME = "googleplus-connector-1.0.0";
        String googlePlusConnectorFileName = CONNECTOR_NAME + ".zip";
        ConnectorIntegrationUtil
                .uploadConnector(repoLocation, mediationLibUploadStub, googlePlusConnectorFileName);
        log.info("Sleeping for " + 60000 / 1000 + " seconds while waiting for synapse import");
        Thread.sleep(60000);
        adminServiceStub
                .updateStatus("{org.wso2.carbon.connector}" + "googleplus", "googleplus",
                        "org.wso2.carbon.connector", "enabled");

        googlePlusConnectorProperties =
                ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);

        pathToProxiesDirectory = repoLocation + googlePlusConnectorProperties
                .getProperty("proxyDirectoryRelativePath");
        pathToRequestsDirectory = repoLocation + googlePlusConnectorProperties
                .getProperty("requestDirectoryRelativePath");
    }

    @Override
    protected void cleanup() {
        axis2Client.destroy();
    }
    /* Test cases for Activities */

    /**
     * Mandatory parameter test case for getActivity method.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListActivityWithMandatoryParams"},
            description = "GooglePlus {getActivity} integration test with mandatory parameters.")
    public void testGetActivityWithMandatoryParams() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getActivities.txt";
        String methodName = "getActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("plus#activity", responseJson.getString("kind"));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for getActivity method.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListActivityWithMandatoryParams"},
            description = "GooglePlus {getActivity} integration test with mandatory and optional parameters.")
    public void testGetActivityWithOptionalParams() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getActivitiesOptionalParams.txt";
        String methodName = "getActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {

            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("plus#activity", responseJson.getString("kind"));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getActivity method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {getActivity} integration test with Negative parameters.")
    public void testGetActivityWithNegativeParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getActivitiesUnhappy.txt";
        String methodName = "getActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            int statusCode = ConnectorIntegrationUtil
                    .sendRequestToRetrieveHeaders(getProxyServiceURL(methodName), modifiedJsonString);

            Assert.assertTrue(statusCode == 404 || statusCode == 403);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for listActivity method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listActivity} integration test with mandatory parameters.")
    public void testListActivityWithMandatoryParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listActivities.txt";
        String methodName = "listActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("userId", googlePlusConnectorProperties.getProperty("userId"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            JSONArray jArray = responseJson.getJSONArray("items");
            for(int i =0; i<jArray.length(); i++) {
                int comments = jArray.getJSONObject(i).getJSONObject("object").getJSONObject("replies").getInt("totalItems");
                if (comments >= 1) {
                    googlePlusConnectorProperties.setProperty("activityId", jArray.getJSONObject(i).getString("id"));
                    break;
                }
            }
            if(responseJson.has("nextPageToken")) {
                googlePlusConnectorProperties.setProperty("listActivitiesPageToken", responseJson.getString("nextPageToken"));
            }else {
                googlePlusConnectorProperties.setProperty("listActivitiesPageToken", "");
            }
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listActivity method with maxResults Optional Parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListActivityWithMandatoryParams"},
            description = "GooglePlus {listActivity} integration test with mandatory and maxResults optional parameter.")
    public void testListActivityWithOneOptionalParam1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listActivitiesOptionalParams.txt";
        String methodName = "listActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("userId", googlePlusConnectorProperties.getProperty("userId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"pageToken", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listActivity method with fields Optional Parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListActivityWithMandatoryParams"},
            description = "GooglePlus {listActivity} integration test with mandatory and fields optional parameter.")
    public void testListActivityWithOneOptionalParam3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listActivitiesOptionalParams.txt";
        String methodName = "listActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("userId", googlePlusConnectorProperties.getProperty("userId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listActivity method with maxResults,fields Optional Parameters.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListActivityWithMandatoryParams"},
            description = "GooglePlus {listActivity} integration test with mandatory and maxResults,fields optional parameters.")
    public void testListActivityWithTwoOptionalParam3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listActivitiesOptionalParams.txt";
        String methodName = "listActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("userId", googlePlusConnectorProperties.getProperty("userId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for listActivity method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listActivity} integration test with Negative parameters.")
    public void testListActivityWithNegativeParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listActivitiesUnhappy.txt";
        String methodName = "listActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            int statusCode = ConnectorIntegrationUtil
                    .sendRequestToRetrieveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 404 || statusCode == 403);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for searchActivities method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {searchActivities} integration test with mandatory parameters.")
    public void testSearchActivityWithMandatoryParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivities.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            log.info("Sleep for 30 seconds");
            Thread.sleep(3000);

            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            if(responseJson.has("nextPageToken")) {
                googlePlusConnectorProperties.setProperty("searchActivitiesPageToken", responseJson.getString("nextPageToken"));
            }else {
                googlePlusConnectorProperties.setProperty("searchActivitiesPageToken", "");
            }
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method.
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and optional parameters.")
    public void testSearchActivityWithOptionalParams() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with maxResults optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and maxResults optional parameters.")
    public void testSearchActivityWithOneOptionalParams1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"language", "orderBy", "pageToken", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with orderBy optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and orderBy optional parameters.")
    public void testSearchActivityWithOneOptionalParams2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters =
                    {"language", "maxResults", "pageToken", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with pageToken optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and pageToken optional parameters.")
    public void testSearchActivityWithOneOptionalParams3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"language", "maxResults", "orderBy", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with fields.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and fields optional parameters.")
    public void testSearchActivityWithOneOptionalParams4() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters =
                    {"language", "maxResults", "orderBy", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with language optional parameter
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and language optional parameters.")
    public void testSearchActivityWithOneOptionalParams5() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters =
                    {"language", "maxResults", "orderBy", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with language,maxResults,orderBy,pageToken .
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and language,maxResults,orderBy,pageToken optional parameters.")
    public void testSearchActivityWithFourOptionalParams1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with language,maxResults,orderBy,fields .
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and language,maxResults,orderBy,fields optional parameters.")
    public void testSearchActivityWithFourOptionalParams2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with language,maxResults,pageToken,fields .
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and language,maxResults,pageToken,fields optional parameters.")
    public void testSearchActivityWithFourOptionalParams3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"orderBy"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with language,orderBy,pageToken,fields .
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and language,orderBy,pageToken,fields optional parameters.")
    public void testSearchActivityWithFourOptionalParams4() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with maxResults,orderBy,pageToken,fields.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and maxResults,orderBy,pageToken,fields optional parameters.")
    public void testSearchActivityWithFourOptionalParams5() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"language"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with language,maxResults,fields optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and language,maxResults,fields optional parameters.")
    public void testSearchActivityWithThreeOptionalParams1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"orderBy", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with language,orderBy,pageToken optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and language,orderBy,pageToken optional parameters.")
    public void testSearchActivityWithThreeOptionalParams2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with language,orderBy,fields optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and language,orderBy,fields optional parameters.")
    public void testSearchActivityWithThreeOptionalParams3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with language,pageToken,fields.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and language,pageToken,fields optional parameters.")
    public void testSearchActivityWithThreeOptionalParams4() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "orderBy"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with maxResults,orderBy,pageToken optional parameter
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and maxResults,orderBy,pageToken optional parameters.")
    public void testSearchActivityWithThreeOptionalParams5() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"language", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with maxResults,orderBy,fields optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and maxResults,orderBy,fields optional parameters.")
    public void testSearchActivityWithThreeOptionalParams6() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"language", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with maxResults,pageToken,fields optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and maxResults,pageToken,fields optional parameters.")
    public void testSearchActivityWithThreeOptionalParams7() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"language", "orderBy"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with orderBy,pageToken,fields optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and orderBy,pageToken,fields optional parameters.")
    public void testSearchActivityWithThreeOptionalParams8() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"language", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with language,maxResults,orderBy.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and language,maxResults,orderBy optional parameters.")
    public void testSearchActivityWithThreeOptionalParams9() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"pageToken", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with language,maxResults,pageToken optional parameter
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and language,maxResults,pageToken optional parameters.")
    public void testSearchActivityWithThreeOptionalParams10() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"orderBy", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with orderBy,pageToken  optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and orderBy,pageToken optional parameters.")
    public void testSearchActivityWithTwoOptionalParams1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"language", "maxResults", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with maxResults,fields optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "googleplus {searchActivities} integration test with mandatory and maxResults,fields optional parameters.")
    public void testSearchActivityWithTwoOptionalParams2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"language", "orderBy", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with maxResults,pageToken optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and maxResults,pageToken optional parameters.")
    public void testSearchActivityWithTwoOptionalParams3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"language", "orderBy", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with maxResults,orderBy.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and maxResults,orderBy optional parameters.")
    public void testSearchActivityWithTwoOptionalParams4() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"language", "pageToken", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with language,fields optional parameter
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and language,fields optional parameters.")
    public void testSearchActivityWithTwoOptionalParams5() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "orderBy", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with language,pageToken optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and language,pageToken optional parameters.")
    public void testSearchActivityWithTwoOptionalParams6() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "orderBy", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with language,orderBy optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and language,orderBy optional parameters.")
    public void testSearchActivityWithTwoOptionalParams7() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "pageToken", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with language,maxResults optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and language,maxResults optional parameters.")
    public void testSearchActivityWithTwoOptionalParams8() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"orderBy", "pageToken", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with pageToken,fields.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and pageToken,fields optional parameters.")
    public void testSearchActivityWithTwoOptionalParams9() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"language", "maxResults", "orderBy"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchActivities method with orderBy,fields optional parameter
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchActivityWithMandatoryParams"},
            description = "GooglePlus {searchActivities} integration test with mandatory and orderBy,fields optional parameters.")
    public void testSearchActivityWithTwoOptionalParams10() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesOptionalParams.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchActivitiesPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"language", "maxResults", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#activityFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative parameter test case for searchActivities method.
     */

    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {searchActivities} integration test with Negative parameters.")
    public void testSearchActivityWithNegativeParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "searchActivitiesUnhappy.txt";
        String methodName = "searchActivities";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            int statusCode = ConnectorIntegrationUtil
                    .sendRequestToRetrieveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals(statusCode, 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


    /* Test cases for comments */

    /**
     * Mandatory parameter test case for getComments method.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListCommentsWithMandatoryParams"},
            description = "GooglePlus {getComments} integration test with mandatory parameters.")
    public void testGetCommentsWithMandatoryParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getComments.txt";
        String methodName = "getComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("commentId", googlePlusConnectorProperties.getProperty("commentId"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {

            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("plus#comment", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for getComments method.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListCommentsWithMandatoryParams"},
            description = "GooglePlus {getComments} integration test with mandatory and optional parameters.")
    public void testGetCommentsWithOptionalParams() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getCommentsOptionalParams.txt";
        String methodName = "getComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("commentId", googlePlusConnectorProperties.getProperty("commentId"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("plus#comment", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative parameter test case for getComments method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {getComments} integration test with Negative parameters.")
    public void testGetCommentsWithNegativeParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getCommentsUnhappy.txt";
        String methodName = "getComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            int statusCode = ConnectorIntegrationUtil
                    .sendRequestToRetrieveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 404 || statusCode == 403);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for listComments method.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListActivityWithMandatoryParams"},
            description = "GooglePlus {listComments} integration test with mandatory parameters.")
    public void testListCommentsWithMandatoryParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listComments.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {

            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            JSONArray jArray = responseJson.getJSONArray("items");
            googlePlusConnectorProperties.setProperty("commentId", jArray.getJSONObject(0).getString("id"));
            if(responseJson.has("nextPageToken")) {
                googlePlusConnectorProperties.setProperty("listCommentsPageToken", responseJson.getString("nextPageToken"));
            }else {
                googlePlusConnectorProperties.setProperty("listCommentsPageToken", "");
            }
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listComments method.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListCommentsWithMandatoryParams"},
            description = "GooglePlus {listComments} integration test with mandatory and optional parameters.")
    public void testListCommentsWithOptionalParams() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listCommentsPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative parameter test case for listComments method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listComments} integration test with Negative parameters.")
    public void testListCommentsWithNegativeParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsUnhappy.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {

            int statusCode = ConnectorIntegrationUtil
                    .sendRequestToRetrieveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(statusCode == 404 || statusCode == 403);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter maxResults check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListCommentsWithMandatoryParams"},
            description = "GooglePlus {listComments} integration test with optional parameter maxResults.")
    public void testListCommentsOneOptionalParam1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listCommentsPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"pageToken", "sortOrder", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter pageToken check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListCommentsWithMandatoryParams"},
            description = "GooglePlus {listComments} integration test with optional parameter pageToken.")
    public void testListCommentsOneOptionalParam2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listCommentsPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "sortOrder", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter sortOrder check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListCommentsWithMandatoryParams"},
            description = "GooglePlus {listComments} integration test with optional parameter sortOrder.")
    public void testListCommentsOneOptionalParam3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listCommentsPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "pageToken", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter fields check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListCommentsWithMandatoryParams"},
            description = "GooglePlus {listComments} integration test with optional parameter fields.")
    public void testListCommentsOneOptionalParam4() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listCommentsPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "pageToken", "sortOrder"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListComments method with maxResults,pageToken  optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListCommentsWithMandatoryParams"},
            description = "GooglePlus {ListComments} integration test with mandatory and maxResults,pageToken optional parameters.")
    public void testListCommentsWithTwoOptionalParams1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listCommentsPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"sortOrder", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListComments method with maxResults,sortOrder optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListCommentsWithMandatoryParams"},
            description = "GooglePlus {ListComments} integration test with mandatory and maxResults,sortOrder optional parameters.")
    public void testListCommentsWithTwoOptionalParams2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listCommentsPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"fields", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListComments method with maxResults,fields optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListCommentsWithMandatoryParams"},
            description = "GooglePlus {ListComments} integration test with mandatory and maxResults,fields optional parameters.")
    public void testListCommentsWithTwoOptionalParams3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listCommentsPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"sortOrder", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListComments method with pageToken,sortOrder.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListCommentsWithMandatoryParams"},
            description = "GooglePlus {ListComments} integration test with mandatory and pageToken,sortOrder optional parameters.")
    public void testListCommentsWithTwoOptionalParams4() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listCommentsPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListComments method with pageToken,fields optional parameter
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListCommentsWithMandatoryParams"},
            description = "GooglePlus {ListComments} integration test with mandatory and language,fields optional parameters.")
    public void testListCommentsWithTwoOptionalParams5() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listCommentsPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "sortOrder"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListComments method with sortOrder,fields optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListCommentsWithMandatoryParams"},
            description = "GooglePlus {ListComments} integration test with mandatory and sortOrder,fields optional parameters.")
    public void testListCommentsWithTwoOptionalParams6() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listCommentsPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter "pageToken", "sortOrder", "fields"  check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListCommentsWithMandatoryParams"},
            description = "GooglePlus {listComments} integration test with optional parameter pageToken, sortOrder, fields.")
    public void testListCommentsThreeOptionalParam1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listCommentsPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter "maxResults", "sortOrder", "fields" check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListCommentsWithMandatoryParams"},
            description = "GooglePlus {listComments} integration test with optional parameter maxResults, sortOrder, fields.")
    public void testListCommentsThreeOptionalParam2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listCommentsPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter "maxResults", "pageToken", "fields"  check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListCommentsWithMandatoryParams"},
            description = "GooglePlus {listComments} integration test with optional parameter maxResults, pageToken, fields.")
    public void testListCommentsThreeOptionalParam3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listCommentsPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"sortOrder"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter "maxResults", "pageToken", "sortOrder" check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListCommentsWithMandatoryParams"},
            description = "GooglePlus {listComments} integration test with optional parameter maxResults, pageToken, sortOrder.")
    public void testListCommentsThreeOptionalParam4() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listCommentsOptionalParams.txt";
        String methodName = "listComments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listCommentsPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            Assert.assertEquals("plus#commentFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /* Test cases for Moments */

    /**
     * Mandatory parameter test case for listMoments method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listMoments} integration test with mandatory parameters.")
    public void testListMomentsWithMandatoryParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listMoments.txt";
        String methodName = "listMoments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {

            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            junit.framework.Assert.assertEquals("plus#momentsFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listMoments method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listMoments} integration test with mandatory and optional parameters.")
    public void testListMomentsWithOptionalParams() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listMomentsOptionalParams.txt";
        String methodName = "listMoments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            junit.framework.Assert.assertEquals("plus#momentsFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative parameter test case for listMoments method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listMoments} integration test with Negative parameters.")
    public void testListMomentsWithNegativeParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listMomentsUnhappy.txt";
        String methodName = "listMoments";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            int statusCode = ConnectorIntegrationUtil
                    .sendRequestToRetrieveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            junit.framework.Assert.assertEquals(statusCode, 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


    /* Test cases for People*/

    /**
     * Mandatory parameter test case for getPeople method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {getPeople} integration test with mandatory parameters.")
    public void testGetPeopleWithMandatoryParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getPeople.txt";
        String methodName = "getPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("userId", googlePlusConnectorProperties.getProperty("userId"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {

            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            junit.framework.Assert.assertEquals("plus#person", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for getPeople method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {getPeople} integration test with mandatory and optional parameters.")
    public void testGetPeopleWithOptionalParams() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getPeopleOptionalParams.txt";
        String methodName = "getPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("userId", googlePlusConnectorProperties.getProperty("userId"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            junit.framework.Assert.assertEquals("plus#person", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative parameter test case for getPeople method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {getPeople} integration test with Negative parameters.")
    public void testGetPeopleWithNegativeParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "getPeopleUnhappy.txt";
        String methodName = "getPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {

            int statusCode = ConnectorIntegrationUtil
                    .sendRequestToRetrieveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            junit.framework.Assert.assertTrue(statusCode == 404 || statusCode == 403);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for searchPeople method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {searchPeople} integration test with mandatory parameters.")
    public void testSearchPeopleWithMandatoryParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeople.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            if(responseJson.has("nextPageToken")) {
                googlePlusConnectorProperties.setProperty("searchPeoplePageToken", responseJson.getString("nextPageToken"));
            }else {
                googlePlusConnectorProperties.setProperty("searchPeoplePageToken", "");
            }
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchPeople method.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchPeopleWithMandatoryParams"},
            description = "GooglePlus {searchPeople} integration test with mandatory and optional parameters.")
    public void testSearchPeopleWithOptionalParams() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative parameter test case for searchPeople method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {searchPeople} integration test with Negative parameters.")
    public void testSearchPeopleWithNegativeParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleUnhappy.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {

            int statusCode = ConnectorIntegrationUtil
                    .sendRequestToRetrieveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            junit.framework.Assert.assertEquals(statusCode, 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter maxResults check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"},  dependsOnMethods = {"testSearchPeopleWithMandatoryParams"},
            description = "GooglePlus {searchPeople} integration test with optional parameter maxResults.")
    public void testSearchPeopleOneOptionalParam1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"pageToken", "language", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter pageToken check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchPeopleWithMandatoryParams"},
            description = "GooglePlus {searchPeople} integration test with optional parameter pageToken.")
    public void testSearchPeopleOneOptionalParam2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "language", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter language check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchPeopleWithMandatoryParams"},
            description = "GooglePlus {searchPeople} integration test with optional parameter language.")
    public void testSearchPeopleOneOptionalParam3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "pageToken", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter fields check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchPeopleWithMandatoryParams"},
            description = "GooglePlus {searchPeople} integration test with optional parameter fields.")
    public void testSearchPeopleOneOptionalParam4() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "pageToken", "language"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for SearchPeople method with maxResults,pageToken  optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchPeopleWithMandatoryParams"},
            description = "GooglePlus {SearchPeople} integration test with mandatory and maxResults,pageToken optional parameters.")
    public void testSearchPeopleWithTwoOptionalParams1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"language", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for SearchPeople method with maxResults,language optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchPeopleWithMandatoryParams"},
            description = "GooglePlus {SearchPeople} integration test with mandatory and maxResults,language optional parameters.")
    public void testSearchPeopleWithTwoOptionalParams2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"fields", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for SearchPeople method with maxResults,fields optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchPeopleWithMandatoryParams"},
            description = "GooglePlus {SearchPeople} integration test with mandatory and maxResults,fields optional parameters.")
    public void testSearchPeopleWithTwoOptionalParams3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"language", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for SearchPeople method with pageToken,language.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchPeopleWithMandatoryParams"},
            description = "GooglePlus {SearchPeople} integration test with mandatory and pageToken,language optional parameters.")
    public void testSearchPeopleWithTwoOptionalParams4() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for SearchPeople method with pageToken,fields optional parameter
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchPeopleWithMandatoryParams"},
            description = "GooglePlus {SearchPeople} integration test with mandatory and language,fields optional parameters.")
    public void testSearchPeopleWithTwoOptionalParams5() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "language"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for SearchPeople method with language,fields optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchPeopleWithMandatoryParams"},
            description = "GooglePlus {SearchPeople} integration test with mandatory and language,fields optional parameters.")
    public void testSearchPeopleWithTwoOptionalParams6() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter "pageToken", "language", "fields"  check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchPeopleWithMandatoryParams"},
            description = "GooglePlus {searchPeople} integration test with optional parameter pageToken, language, fields.")
    public void testSearchPeopleThreeOptionalParam1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter "maxResults", "language", "fields" check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchPeopleWithMandatoryParams"},
            description = "GooglePlus {searchPeople} integration test with optional parameter maxResults, language, fields.")
    public void testSearchPeopleThreeOptionalParam2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter "maxResults", "pageToken", "fields"  check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchPeopleWithMandatoryParams"},
            description = "GooglePlus {searchPeople} integration test with optional parameter maxResults, pageToken, fields.")
    public void testSearchPeopleThreeOptionalParam3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"language"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter "maxResults", "pageToken", "language" check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testSearchPeopleWithMandatoryParams"},
            description = "GooglePlus {searchPeople} integration test with optional parameter maxResults, pageToken, language.")
    public void testSearchPeopleThreeOptionalParam4() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchPeopleOptionalParams.txt";
        String methodName = "searchPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("searchPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for listPeople method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listPeople} integration test with mandatory parameters.")
    public void testListPeopleWithMandatoryParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listPeople.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {

            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            if(responseJson.has("nextPageToken")) {
                googlePlusConnectorProperties.setProperty("listPeoplePageToken", responseJson.getString("nextPageToken"));
            }else {
                googlePlusConnectorProperties.setProperty("listPeoplePageToken", "");
            }
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listPeople method.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListPeopleWithMandatoryParams"},
            description = "GooglePlus {listPeople} integration test with mandatory and optional parameters.")
    public void testListPeopleWithOptionalParams() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative parameter test case for listPeople method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listPeople} integration test with Negative parameters.")
    public void testListPeopleWithNegativeParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleUnhappy.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            int statusCode = ConnectorIntegrationUtil
                    .sendRequestToRetrieveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            junit.framework.Assert.assertEquals(statusCode, 400);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter maxResults check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListPeopleWithMandatoryParams"},
            description = "GooglePlus {listPeople} integration test with optional parameter maxResults.")
    public void testListPeopleOneOptionalParam1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"pageToken", "orderBy", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter pageToken check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListPeopleWithMandatoryParams"},
            description = "GooglePlus {listPeople} integration test with optional parameter pageToken.")
    public void testListPeopleOneOptionalParam2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "orderBy", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter orderBy check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListPeopleWithMandatoryParams"},
            description = "GooglePlus {listPeople} integration test with optional parameter orderBy.")
    public void testListPeopleOneOptionalParam3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "pageToken", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter fields check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListPeopleWithMandatoryParams"},
            description = "GooglePlus {listPeople} integration test with optional parameter fields.")
    public void testListPeopleOneOptionalParam4() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "pageToken", "orderBy"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListPeople method with maxResults,pageToken  optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListPeopleWithMandatoryParams"},
            description = "GooglePlus {ListPeople} integration test with mandatory and maxResults,pageToken optional parameters.")
    public void testListPeopleWithTwoOptionalParams1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"orderBy", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListPeople method with maxResults,orderBy optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListPeopleWithMandatoryParams"},
            description = "GooglePlus {ListPeople} integration test with mandatory and maxResults,orderBy optional parameters.")
    public void testListPeopleWithTwoOptionalParams2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"fields", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListPeople method with maxResults,fields optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListPeopleWithMandatoryParams"},
            description = "GooglePlus {ListPeople} integration test with mandatory and maxResults,fields optional parameters.")
    public void testListPeopleWithTwoOptionalParams3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"orderBy", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListPeople method with pageToken,orderBy.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListPeopleWithMandatoryParams"},
            description = "GooglePlus {ListPeople} integration test with mandatory and pageToken,orderBy optional parameters.")
    public void testListPeopleWithTwoOptionalParams4() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListPeople method with pageToken,fields optional parameter
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListPeopleWithMandatoryParams"},
            description = "GooglePlus {ListPeople} integration test with mandatory and language,fields optional parameters.")
    public void testListPeopleWithTwoOptionalParams5() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "orderBy"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for ListPeople method with orderBy,fields optional parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListPeopleWithMandatoryParams"},
            description = "GooglePlus {ListPeople} integration test with mandatory and orderBy,fields optional parameters.")
    public void testListPeopleWithTwoOptionalParams6() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter "pageToken", "orderBy", "fields"  check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListPeopleWithMandatoryParams"},
            description = "GooglePlus {listPeople} integration test with optional parameter pageToken, orderBy, fields.")
    public void testListPeopleThreeOptionalParam1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter "maxResults", "orderBy", "fields" check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListPeopleWithMandatoryParams"},
            description = "GooglePlus {listPeople} integration test with optional parameter maxResults, orderBy, fields.")
    public void testListPeopleThreeOptionalParam2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter "maxResults", "pageToken", "fields"  check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListPeopleWithMandatoryParams"},
            description = "GooglePlus {listPeople} integration test with optional parameter maxResults, pageToken, fields.")
    public void testListPeopleThreeOptionalParam3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"orderBy"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter "maxResults", "pageToken", "orderBy" check
     *
     * @throws Exception
     */

    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListPeopleWithMandatoryParams"},
            description = "GooglePlus {listPeople} integration test with optional parameter maxResults, pageToken, orderBy.")
    public void testListPeopleThreeOptionalParam4() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listPeopleOptionalParams.txt";
        String methodName = "listPeople";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listPeoplePageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for listPeople method.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListActivityWithMandatoryParams"},
            description = "GooglePlus {listByActivity} integration test with mandatory parameters.")
    public void testListByActivityWithMandatoryParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listByActivity.txt";
        String methodName = "listByActivity";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {

            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            if(responseJson.has("nextPageToken")) {
                googlePlusConnectorProperties.setProperty("listByActivityPageToken", responseJson.getString("nextPageToken"));
            }else {
                googlePlusConnectorProperties.setProperty("listByActivityPageToken", "");
            }
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listPeople method.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListByActivityWithMandatoryParams"},
            description = "GooglePlus {listByActivity} integration test with mandatory and optional parameters.")
    public void testListByActivityWithOptionalParams() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listByActivityOptionalParams.txt";
        String methodName = "listByActivity";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listByActivityPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listByActivity method with maxResults Optional Parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListByActivityWithMandatoryParams"},
            description = "GooglePlus {listByActivity} integration test with mandatory and maxResults optional parameter.")
    public void testListByActivityWithOneOptionalParam1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listByActivityOptionalParams.txt";
        String methodName = "listByActivity";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listByActivityPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"pageToken", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listByActivity method with pageToken Optional Parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListByActivityWithMandatoryParams"},
            description = "GooglePlus {listByActivity} integration test with mandatory and pageToken optional parameter.")
    public void testListByActivityWithOneOptionalParam2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listByActivityOptionalParams.txt";
        String methodName = "listByActivity";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listByActivityPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listByActivity method with fields Optional Parameter.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListByActivityWithMandatoryParams"},
            description = "GooglePlus {listByActivity} integration test with mandatory and fields optional parameter.")
    public void testListByActivityWithOneOptionalParam3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listByActivityOptionalParams.txt";
        String methodName = "listByActivity";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listByActivityPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults", "pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listByActivity method with maxResults,pageToken Optional Parameters.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListByActivityWithMandatoryParams"},
            description = "GooglePlus {listByActivity} integration test with mandatory and maxResults,pageToken optional parameters.")
    public void testListByActivityWithTwoOptionalParam1() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listByActivityOptionalParams.txt";
        String methodName = "listByActivity";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listByActivityPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"fields"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listByActivity method with pageToken,fields Optional Parameters.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListByActivityWithMandatoryParams"},
            description = "GooglePlus {listByActivity} integration test with mandatory and pageToken,,fields optional parameters.")
    public void testListByActivityWithTwoOptionalParam2() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listByActivityOptionalParams.txt";
        String methodName = "listByActivity";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listByActivityPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"maxResults"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for listByActivity method with maxResults,fields Optional Parameters.
     */
    @Test(groups = {"wso2.esb"}, dependsOnMethods = {"testListByActivityWithMandatoryParams"},
            description = "GooglePlus {listByActivity} integration test with mandatory and maxResults,fields optional parameters.")
    public void testListByActivityWithTwoOptionalParam3() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "listByActivityOptionalParams.txt";
        String methodName = "listByActivity";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("activityId", googlePlusConnectorProperties.getProperty("activityId"));
        jsonObject.append("pageToken", googlePlusConnectorProperties.getProperty("listByActivityPageToken"));
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {
            String[] unneededOptionalParameters = {"pageToken"};
            String requiredJsonString =
                    ConnectorIntegrationUtil.getRequiredJsonString(modifiedJsonString, unneededOptionalParameters);
            JSONObject responseJson = ConnectorIntegrationUtil
                    .sendRequest(getProxyServiceURL(methodName), requiredJsonString);
            junit.framework.Assert.assertEquals("plus#peopleFeed", responseJson.getString("kind"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative parameter test case for listPeople method.
     */
    @Test(groups = {"wso2.esb"},
            description = "GooglePlus {listByActivity} integration test with Negative parameters.")
    public void testListByActivityWithNegativeParams() throws Exception {

        String jsonRequestFilePath = pathToRequestsDirectory + "listByActivityUnhappy.txt";
        String methodName = "listByActivity";
        final String requestJsonString =
                ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonObject = new JSONObject(requestJsonString);
        jsonObject.append("apiUrl", googlePlusConnectorProperties.getProperty("apiUrl"));
        jsonObject.append("clientId", googlePlusConnectorProperties.getProperty("clientId"));
        jsonObject.append("clientSecret", googlePlusConnectorProperties.getProperty("clientSecret"));
        jsonObject.append("refreshToken", googlePlusConnectorProperties.getProperty("refreshToken"));
        String modifiedJsonString = jsonObject.toString().replace("[","").replace("]","");
        try {

            int statusCode = ConnectorIntegrationUtil
                    .sendRequestToRetrieveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            junit.framework.Assert.assertTrue(statusCode == 404 || statusCode == 403);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
}