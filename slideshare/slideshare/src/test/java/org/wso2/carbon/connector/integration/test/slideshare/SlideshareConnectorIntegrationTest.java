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
package org.wso2.carbon.connector.integration.test.slideshare;

import org.apache.axis2.context.ConfigurationContext;
import org.testng.Assert;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;

import javax.activation.DataHandler;
import java.net.URL;
import java.util.Properties;

import org.apache.axis2.context.ConfigurationContext;
import org.json.JSONObject;
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

public class SlideshareConnectorIntegrationTest extends ESBIntegrationTest {

    private static final String CONNECTOR_NAME = "slideshare";

    private MediationLibraryUploaderStub mediationLibUploadStub = null;

    private MediationLibraryAdminServiceStub adminServiceStub = null;

    private ProxyServiceAdminClient proxyAdmin;

    private String repoLocation = null;

    private String slideshareConnectorFileName = "slideshare.zip";

    private Properties slideshareConnectorProperties = null;

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

        ConnectorIntegrationUtil.uploadConnector(repoLocation, mediationLibUploadStub, slideshareConnectorFileName);
        log.info("Sleeping for " + 30000 / 1000 + " seconds while waiting for synapse import");
        Thread.sleep(30000);
        adminServiceStub.updateStatus("{org.wso2.carbon.connector}" + CONNECTOR_NAME, CONNECTOR_NAME,
                "org.wso2.carbon.connector", "enabled");
        slideshareConnectorProperties = ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);
        pathToProxiesDirectory = repoLocation + slideshareConnectorProperties.getProperty("proxyDirectoryRelativePath");
        pathToRequestsDirectory = repoLocation + slideshareConnectorProperties.getProperty("requestDirectoryRelativePath");

    }

    /**
     * Mandatory parameter  test case for   getSlideshow method
     */
    @Test(groups = {"wso2.esb"}, description = "slideshare {getSlideshow} integration test with mandatory parameter.")
    public void getSlideshow() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSlideshow.txt";
        String methodName = "slideshare_getSlideshow";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info("response:" + responseHeader);
             Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter  test case for   getSlideshow method
     */
    @Test(groups = {"wso2.esb"}, description = "slideshare {getSlideshow} integration test with mandatory parameter.")
    public void getSlideshowOptional() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSlideshowOptional.txt";
        String methodName = "slideshare_getSlideshow";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info("response:" + responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getSlideshow method
     */
    @Test(enabled = false, groups = {"wso2.esb"}, description = "slideshare {getSlideshow} integration test with Negative parameter.")
    public void getSlideshowNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSlideshowNegativeCase.txt";
        String methodName = "slideshare_getSlideshow";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for deleteSlideshow method.
     */
    @Test(groups = {"wso2.esb"}, description = "slideshare {deleteSlideshow} integration test with mandatory parameter.")
    public void deleteSlideshow() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "deleteSlideshow.txt";
        String methodName = "slideshare_deleteSlideshow";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info("response:" + responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for deleteSlideshow method
     */
    @Test(enabled = false, groups = {"wso2.esb"}, description = "slideshare {deleteSlideshow} integration test with Negative parameter.")
    public void deleteSlideshowNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "deleteSlideshowNegativeCase.txt";
        String methodName = "slideshare_deleteSlideshow";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getSlideshowByTag method.
     */
    @Test(groups = {"wso2.esb"}, description = "slideshare {getSlideshowByTag} integration test with mandatory parameter.")
    public void getSlideshowByTag() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSlideshowByTag.txt";
        String methodName = "slideshare_getSlideshowByTag";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info(responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * optional parameter test case for getSlideshowByTag method.
     */
    @Test(groups = {"wso2.esb"}, description = "slideshare {getSlideshowByTag} integration test with mandatory parameter.")
    public void getSlideshowByTagOptional() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSlideshowByTagOptional.txt";
        String methodName = "slideshare_getSlideshowByTag";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info(responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getSlideshowByTag method
     */
    @Test(enabled = false, groups = {"wso2.esb"}, description = "slideshare {getSlideshowByTag} integration test with Negative parameter.")
    public void getSlideshowByTagNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSlideshowByTagNegativeCase.txt";
        String methodName = "slideshare_getSlideshowByTag";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


    /**
     * Mandatory parameter test case for getSlideshowsByGroup method.
     */
    @Test(groups = {"wso2.esb"}, description = "slideshare {getSlideshowsByGroup} integration test with mandatory parameter.")
    public void getSlideshowsByGroup() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSlideshowsByGroup.txt";
        String methodName = "slideshare_getSlideshowsByGroup";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info(responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for getSlideshowsByGroup method.
     */
    @Test(groups = {"wso2.esb"}, description = "slideshare {getSlideshowsByGroup} integration test with mandatory parameter.")
    public void getSlideshowsByGroupOptional() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSlideshowsByGroupOptional.txt";
        String methodName = "slideshare_getSlideshowsByGroup";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info(responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getSlideshowsByGroup method
     */
    @Test(enabled = false, groups = {"wso2.esb"}, description = "slideshare {getSlideshowsByGroup} integration test with Negative parameter.")
    public void getSlideshowsByGroupNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSlideshowsByGroupNegativeCase.txt";
        String methodName = "slideshare_getSlideshowsByGroup";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getSlideshowsByUser method.
     */
    @Test(groups = {"wso2.esb"}, description = "slideshare {getSlideshowsByUser} integration test with mandatory parameter.")
    public void getSlideshowsByUser() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSlideshowsByUser.txt";
        String methodName = "slideshare_getSlideshowsByUser";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info(responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for getSlideshowsByUser method.
     */
    @Test(groups = {"wso2.esb"}, description = "slideshare {getSlideshowsByUser} integration test with mandatory parameter.")
    public void getSlideshowsByUserOptional() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSlideshowsByUserOptional.txt";
        String methodName = "slideshare_getSlideshowsByUser";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info(responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getSlideshowsByUser method
     */
    @Test(enabled = false, groups = {"wso2.esb"}, description = "slideshare {getSlideshowsByUser} integration test with Negative parameter.")
    public void getSlideshowsByUserNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getSlideshowsByUserNegativeCase.txt";
        String methodName = "slideshare_getSlideshowsByUser";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for searchSlideshows method.
     */
    @Test(groups = {"wso2.esb"}, description = "slideshare {searchSlideshows} integration test with mandatory parameter.")
    public void searchSlideshows() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchSlideshows.txt";
        String methodName = "slideshare_searchSlideshows";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info(responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for searchSlideshows method.
     */
    @Test(groups = {"wso2.esb"}, description = "slideshare {searchSlideshows} integration test with mandatory parameter.")
    public void searchSlideshowsOptional() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchSlideshowsOptional.txt";
        String methodName = "slideshare_searchSlideshows";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info(responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for searchSlideshows method
     */
    @Test(enabled = false, groups = {"wso2.esb"}, description = "slideshare {searchSlideshows} integration test with Negative parameter.")
    public void searchSlideshowsNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "searchSlideshowsNegativeCase.txt";
        String methodName = "slideshare_searchSlideshows";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for editExistingSlideshow method.
     */

    @Test(groups = {"wso2.esb"}, description = "slideshare {editExistingSlideshow} integration test with mandatory parameter.")
    public void editExistingSlideshow() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "editExistingSlideshow.txt";
        String methodName = "slideshare_editExistingSlideshow";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info(responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Optional parameter test case for editExistingSlideshow method.
     */

    @Test(groups = {"wso2.esb"}, description = "slideshare {editExistingSlideshow} integration test with mandatory parameter.")
    public void editExistingSlideshowOptional() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "editExistingSlideshowOptional.txt";
        String methodName = "slideshare_editExistingSlideshow";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info(responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for editExistingSlideshow method
     */
    @Test(enabled = false, groups = {"wso2.esb"}, description = "slideshare {editExistingSlideshow} integration test with Negative parameter.")
    public void editExistingSlideshowNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "editExistingSlideshowNegativeCase.txt";
        String methodName = "slideshare_editExistingSlideshow";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for uploadSlideshow method.
     */
    @Test(groups = {"wso2.esb"}, description = "slideshare {uploadSlideshow} integration test with mandatory parameter.")
    public void uploadSlideshow() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "uploadSlideshow.txt";
        String methodName = "slideshare_uploadSlideshow";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info(responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    /**
     * Optional parameter test case for uploadSlideshow method.
     */
    @Test(groups = {"wso2.esb"}, description = "slideshare {uploadSlideshow} integration test with mandatory parameter.")
    public void uploadSlideshowOptional() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "uploadSlideshowOptional.txt";
        String methodName = "slideshare_uploadSlideshow";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info(responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }


    /**
     * Negative test case for uploadSlideshow method
     */
    @Test(enabled = false, groups = {"wso2.esb"}, description = "slideshare {uploadSlideshow} integration test with Negative parameter.")
    public void uploadSlideshowNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "uploadSlideshowNegativeCase.txt";
        String methodName = "slideshare_uploadSlideshow";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for addFavorite method.
     */
    @Test(groups = {"wso2.esb"}, description = "slideshare {addFavorite} integration test with mandatory parameter.")
    public void addFavorite() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "addFavorite.txt";
        String methodName = "slideshare_addFavorite";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info(responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for addFavorite method
     */
    @Test(enabled = false, groups = {"wso2.esb"}, description = "slideshare {addFavorite} integration test with Negative parameter.")
    public void addFavoriteNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "addFavorite.txt";
        String methodName = "slideshare_addFavorite";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for checkFavorite method.
     */
    @Test(groups = {"wso2.esb"}, description = "slideshare {checkFavorite} integration test with mandatory parameter.")
    public void checkFavorite() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "checkFavorite.txt";
        String methodName = "slideshare_checkFavorite";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info(responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * //     * Negative test case for checkFavorite method
     * //
     */
    @Test(enabled = false, groups = {"wso2.esb"}, description = "slideshare {checkFavorite} integration test with Negative parameter.")
    public void checkFavoriteNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "checkFavorite.txt";
        String methodName = "slideshare_checkFavorite";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getUserCampaignLeads method.
     */
    @Test(groups = {"wso2.esb"}, description = "slideshare {getUserCampaignLeads} integration test with mandatory parameter.")
    public void getUserCampaignLeads() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserCampaignLeads.txt";
        String methodName = "slideshare_getUserCampaignLeads";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info(responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getUserCampaignLeads method
     */
    @Test(enabled = false, groups = {"wso2.esb"}, description = "slideshare {getUserCampaignLeads} integration test with Negative parameter.")
    public void getUserCampaignLeadsNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserCampaignLeads.txt";
        String methodName = "slideshare_getUserCampaignLeads";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getUserCampaigns method.
     */
    @Test(groups = {"wso2.esb"}, description = "slideshare {getUserCampaigns} integration test with mandatory parameter.")
    public void getUserCampaigns() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserCampaigns.txt";
        String methodName = "slideshare_getUserCampaigns";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info(responseHeader);
            Assert.assertTrue(responseHeader == 200);

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getUserCampaigns method.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, description = "slideshare {getUserCampaigns} integration test with Negative parameter.")
    public void getUserCampaignsNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserCampaigns.txt";
        String methodName = "slideshare_getUserCampaigns";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getUserLeads method.
     */
    @Test(groups = {"wso2.esb"}, description = "slideshare {getUserLeads} integration test with mandatory parameter.")
    public void getUserLeads() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserLeads.txt";
        String methodName = "slideshare_getUserLeads";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info(responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getUserContacts method.
     */
    @Test(groups = {"wso2.esb"}, description = "slideshare {getUserContacts} integration test with mandatory parameter.")
    public void getUserContacts() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserContacts.txt";
        String methodName = "slideshare_getUserContacts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info(responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getUserContacts method.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, description = "slideshare {getUserContacts} integration test with Negative parameter.")
    public void getUserContactsNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserContacts.txt";
        String methodName = "slideshare_getUserContacts";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getUserFavorites method.
     */
    @Test(groups = {"wso2.esb"}, description = "slideshare {getUserFavorites} integration test with mandatory parameter.")
    public void getUserFavorites() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserFavorites.txt";
        String methodName = "slideshare_getUserFavorites";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info(responseHeader);
            Assert.assertTrue(responseHeader == 200);

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getUserFavorites method.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, description = "slideshare {getUserFavorites} integration test with Negative parameter.")
    public void getUserFavoritesNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserFavorites.txt";
        String methodName = "slideshare_getUserFavorites";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getUserGroups method.
     */

    @Test(groups = {"wso2.esb"}, description = "slideshare {getUserGroups} integration test with mandatory parameter.")
    public void getUserGroups() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserGroups.txt";
        String methodName = "slideshare_getUserGroups";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info(responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test case for getUserGroups method.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, description = "slideshare {getUserGroups} integration test with Negative parameter.")
    public void getUserGroupsNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserGroups.txt";
        String methodName = "slideshare_getUserGroups";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Mandatory parameter test case for getUserTags method.
     */
    @Test(groups = {"wso2.esb"}, description = "slideshare {getUserTags} integration test with mandatory parameter.")
    public void getUserTags() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserTags.txt";
        String methodName = "slideshare_getUserTags";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            log.info(responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     * Negative test casefor getUserTags method.
     */
    @Test(enabled = false, groups = {"wso2.esb"}, description = "slideshare {getUserTags} integration test with Negative parameter.")
    public void getUserTagsNegative() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "getUserTags.txt";
        String methodName = "slideshare_getUserTags";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), jsonString);
            Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
}