/*
 *
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 * /
 */
package org.wso2.carbon.connector.integration.test.AcquiaContextDb;

import java.lang.String;
import java.lang.System;
import java.net.URL;
import java.util.Properties;
import javax.activation.DataHandler;

import net.minidev.json.JSONArray;
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

public class AcquiaContextDbConnectorIntegrationTest extends ESBIntegrationTest {
   private static final String CONNECTOR_NAME = "AcquiaContextDb";

   private MediationLibraryUploaderStub mediationLibUploadStub = null;

   private MediationLibraryAdminServiceStub adminServiceStub = null;

   private ProxyServiceAdminClient proxyAdmin;

   private String repoLocation = null;

   private String AcquiaContextDbConnectorFileName = "AcquiaContextDb.zip";

   private Properties AcquiaContextDbConnectorProperties = null;

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

      ConnectorIntegrationUtil.uploadConnector(repoLocation, mediationLibUploadStub, AcquiaContextDbConnectorFileName);
      log.info("Sleeping for " + 30000 / 1000 + " seconds while waiting for synapse import");
      Thread.sleep(30000);

      adminServiceStub.updateStatus("{org.wso2.carbon.connector}" + CONNECTOR_NAME, CONNECTOR_NAME,
            "org.wso2.carbon.connector", "enabled");

      AcquiaContextDbConnectorProperties = ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);

      pathToProxiesDirectory = repoLocation + AcquiaContextDbConnectorProperties.getProperty("proxyDirectoryRelativePath");
      pathToRequestsDirectory = repoLocation + AcquiaContextDbConnectorProperties.getProperty("requestDirectoryRelativePath");

   }

   @Override
   protected void cleanup() {
      axis2Client.destroy();
   }

    /**
     *
     * Positive test case for getAllSegmants method.
     */
      @Test(enabled = true, groups = {"wso2.esb"}, description = "AcquiaContextDb {getAllSegmants} integration test with positive case.")
       public void getAllSegmants() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "AcquiaContextDb_getAllSegments.txt";
        String methodName = "AcquiaContextDb_getAllSegments";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, AcquiaContextDbConnectorProperties.getProperty("secretKey"), AcquiaContextDbConnectorProperties.getProperty("accessKey"), AcquiaContextDbConnectorProperties.getProperty("accountId"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    /**
     * Negative test case for getAllSegmentsNegativeCase method with negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "AcquiaContextDb {getAllSegmentsNegativeCase} integration test with negative parameter.")
    public void getAllSegmantsNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "AcquiaContextDb_getAllSegmentsNegativeCase.txt";
        String methodName = "AcquiaContextDb_getAllSegments";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, AcquiaContextDbConnectorProperties.getProperty("secretKey"), AcquiaContextDbConnectorProperties.getProperty("accessKey"), AcquiaContextDbConnectorProperties.getProperty("accountId"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            log.info("response:" + responseHeader);
             Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    /**
     *
     * Positive test case for deteteEvent method.
     */
    @Test(enabled=true,groups = {"wso2.esb"}, description = "AcquiaContextDb {deteteEvent} ")
    public void deteteEvent() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "AcquiaContextDb_deleteEvent.txt";
        String methodName = "AcquiaContextDb_deteteEvent";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, AcquiaContextDbConnectorProperties.getProperty("secretKey"), AcquiaContextDbConnectorProperties.getProperty("accessKey"), AcquiaContextDbConnectorProperties.getProperty("accountId"),AcquiaContextDbConnectorProperties.getProperty("eventName"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            log.info("response:" + responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    /**
          * Negative test case for deteteEvent method with negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "AcquiaContextDb {deleteEventNegativeCase} integration test with negative parameter.")
    public void deleteEventNegativeCaseNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "AcquiaContextDb_deleteEventNegativeCase.txt";
        String methodName = "AcquiaContextDb_deteteEvent";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, AcquiaContextDbConnectorProperties.getProperty("secretKey"), AcquiaContextDbConnectorProperties.getProperty("accessKey"), AcquiaContextDbConnectorProperties.getProperty("accountId"),AcquiaContextDbConnectorProperties.getProperty("eventName"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            log.info("response:" + responseHeader);
             Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     *
     * Positive test case for exportVisitorData method with mendatory parameters.
     */
    @Test(enabled = true,groups = {"wso2.esb"}, description = "AcquiaContextDb {exportVisitorData} ")
    public void exportVisitorData() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "AcquiaContextDb_exportVisitorData.txt";
        String methodName = "AcquiaContextDb_exportVisitorData";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, AcquiaContextDbConnectorProperties.getProperty("secretKey"), AcquiaContextDbConnectorProperties.getProperty("accessKey"), AcquiaContextDbConnectorProperties.getProperty("accountId"),AcquiaContextDbConnectorProperties.getProperty("startDate"), AcquiaContextDbConnectorProperties.getProperty("dataExport"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("status_id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
       /**
         *
         * Positive test case for exportVisitorData  with optional parameters.
        */
    @Test(enabled = true,groups = {"wso2.esb"}, description = "AcquiaContextDb {exportVisitorDataIOptional} ")
    public void exportVisitorDataOptional() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "AcquiaContextDb_exportVisitorDataIOptional.txt";
        String methodName = "AcquiaContextDb_exportVisitorData";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, AcquiaContextDbConnectorProperties.getProperty("secretKey"), AcquiaContextDbConnectorProperties.getProperty("accessKey"), AcquiaContextDbConnectorProperties.getProperty("accountId"),AcquiaContextDbConnectorProperties.getProperty("startDate"), AcquiaContextDbConnectorProperties.getProperty("dataExport"),AcquiaContextDbConnectorProperties.getProperty("identifierTypes"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("status_id"));
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    /**
              * Negative test case for exportVisitorData method with negative parameters.
     */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "AcquiaContextDb {exportVisitorDataNegativeCase} integration test with negative parameter.")
    public void exportVisitorDataNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "AcquiaContextDb_exportVisitorDataNegativeCase.txt";
        String methodName = "AcquiaContextDb_exportVisitorData";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, AcquiaContextDbConnectorProperties.getProperty("secretKey"), AcquiaContextDbConnectorProperties.getProperty("accessKey"), AcquiaContextDbConnectorProperties.getProperty("accountId"), AcquiaContextDbConnectorProperties.getProperty("dataExport"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            log.info("response:" + responseHeader);
             Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     *
     * Positive test case for createEvent method.
     */
    // @Test(enabled=false,groups = {"wso2.esb"}, description = "AcquiaContextDb {createEvent} ")
    @Test(enabled=false,groups = {"wso2.esb"}, description = "AcquiaContextDb {createEvent} ")
    public void createEvent() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "AcquiaContextDb_createEvent.txt";
        String methodName = "AcquiaContextDb_createEvent";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, AcquiaContextDbConnectorProperties.getProperty("secretKey"), AcquiaContextDbConnectorProperties.getProperty("accessKey"), AcquiaContextDbConnectorProperties.getProperty("accountId"),AcquiaContextDbConnectorProperties.getProperty("eventName"), AcquiaContextDbConnectorProperties.getProperty("type"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        try {

            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            log.info("response:" + responseHeader);
            Assert.assertTrue(responseHeader == 201);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
   /**
        * Negative test case for createEvent method with negative parameters.
         */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "AcquiaContextDb {createEventNegativeCase} integration test with negative parameter.")
    public void createEventNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "AcquiaContextDb_createEventNegativeCase.txt";
        String methodName = "AcquiaContextDb_createEvent";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, AcquiaContextDbConnectorProperties.getProperty("secretKey"), AcquiaContextDbConnectorProperties.getProperty("accessKey"), AcquiaContextDbConnectorProperties.getProperty("accountId"),AcquiaContextDbConnectorProperties.getProperty("eventName"), AcquiaContextDbConnectorProperties.getProperty("type"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            log.info("response:" + responseHeader);
             Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
     *
     * Positive test case for getVisitorQuery method.
     */
    @Test(enabled = true,groups = {"wso2.esb"}, description = "AcquiaContextDb {getVisitorQuery} ")
    public void getVisitorQuery() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "AcquiaContextDb_getVisitorQuery.txt";
        String methodName = "AcquiaContextDb_getVisitorQuery";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
            String modifiedJsonString = String.format(jsonString, AcquiaContextDbConnectorProperties.getProperty("secretKey"), AcquiaContextDbConnectorProperties.getProperty("accessKey"), AcquiaContextDbConnectorProperties.getProperty("accountId"),AcquiaContextDbConnectorProperties.getProperty("identifier"),AcquiaContextDbConnectorProperties.getProperty("identifierType"),AcquiaContextDbConnectorProperties.getProperty("personTables"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
          JSONObject jsonResponse;
        try {
          jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(jsonResponse.has("person"));
            Assert.assertTrue(jsonResponse.has("identifiers"));
            Assert.assertTrue(jsonResponse.has("touches"));

        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
        /**
        * Negative test case for getVisitorQuery method with negative parameters.
         */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "AcquiaContextDb {getVisitorQueryNegativeCase} integration test with negative parameter.")
    public void getVisitorQueryNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "AcquiaContextDb_getVisitorQueryNegativeCase.txt";
        String methodName = "AcquiaContextDb_getVisitorQuery";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
            String modifiedJsonString = String.format(jsonString, AcquiaContextDbConnectorProperties.getProperty("secretKey"), AcquiaContextDbConnectorProperties.getProperty("accessKey"), AcquiaContextDbConnectorProperties.getProperty("accountId"),AcquiaContextDbConnectorProperties.getProperty("identifier"),AcquiaContextDbConnectorProperties.getProperty("identifierType"),AcquiaContextDbConnectorProperties.getProperty("personTables"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            log.info("response:" + responseHeader);
             Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

       /**
          *
        * Positive test case for getExportVisitorDataStatus method.
     */
    @Test(enabled = true,groups = {"wso2.esb"}, description = "AcquiaContextDb {getExportVisitorDataStatus} ")
    public void getExportVisitorDataStatus() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "AcquiaContextDb_getExportVisitorDataStatus.txt";
        String methodName = "AcquiaContextDb_getExportVisitorDataStatus";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, AcquiaContextDbConnectorProperties.getProperty("secretKey"), AcquiaContextDbConnectorProperties.getProperty("accessKey"), AcquiaContextDbConnectorProperties.getProperty("accountId"),AcquiaContextDbConnectorProperties.getProperty("statusId"));
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
        * Negative test case for getExportVisitorData method with negative parameters.
         */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "AcquiaContextDb {getExportVisitorDataStatusNegativeCase} integration test with negative parameter.")
    public void getExportVisitorDataStatusNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "AcquiaContextDb_getExportVisitorDataStatusNegativeCase.txt";
        String methodName = "AcquiaContextDb_getExportVisitorDataStatus";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
            String modifiedJsonString = String.format(jsonString, AcquiaContextDbConnectorProperties.getProperty("secretKey"), AcquiaContextDbConnectorProperties.getProperty("accessKey"), AcquiaContextDbConnectorProperties.getProperty("accountId"),AcquiaContextDbConnectorProperties.getProperty("statusId"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            log.info("response:" + responseHeader);
             Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

    /**
    * Positive test case for getSegmentsById method.
    */


    @Test(enabled = true, groups = {"wso2.esb"}, description = "AcquiaContextDb {getSegmentsById} integration test with positive case.")
    public void getSegmentsById() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "AcquiaContextDb_getSegmentsById.txt";
        String methodName = "AcquiaContextDb_getSegmentsById";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
        String modifiedJsonString = String.format(jsonString, AcquiaContextDbConnectorProperties.getProperty("secretKey"), AcquiaContextDbConnectorProperties.getProperty("accessKey"), AcquiaContextDbConnectorProperties.getProperty("accountId"),AcquiaContextDbConnectorProperties.getProperty("identifier"), AcquiaContextDbConnectorProperties.getProperty("identifierType"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        JSONObject jsonResponse;
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
        /**
        * Negative test case for getSegmentsById method with negative parameters.
         */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "AcquiaContextDb {getSegmentsByIdNegativeCase} integration test with negative parameter.")
    public void getSegmentsByIdNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "AcquiaContextDb_getSegmentsByIdNegativeCase.txt";
        String methodName = "AcquiaContextDb_getSegmentsById";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
            String modifiedJsonString = String.format(jsonString, AcquiaContextDbConnectorProperties.getProperty("secretKey"), AcquiaContextDbConnectorProperties.getProperty("accessKey"), AcquiaContextDbConnectorProperties.getProperty("accountId"),AcquiaContextDbConnectorProperties.getProperty("identifier"), AcquiaContextDbConnectorProperties.getProperty("identifierType"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            log.info("response:" + responseHeader);
             Assert.assertTrue(responseHeader == 404);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
    /**
      * Positive test case for importEvent method.
        */
    @Test(enabled = true,groups = {"wso2.esb"}, description = "AcquiaContextDb {importEvent} ")
    public void importEvent() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "AcquiaContextDb_importEvent.txt";
        String methodName = "AcquiaContextDb_importEvent";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
            String modifiedJsonString = String.format(jsonString, AcquiaContextDbConnectorProperties.getProperty("secretKey"), AcquiaContextDbConnectorProperties.getProperty("accessKey"), AcquiaContextDbConnectorProperties.getProperty("accountId"),AcquiaContextDbConnectorProperties.getProperty("payload"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        try {

            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            log.info("response:" + responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }

       /**
         * Positive test case for importEvent method with optional parameters.
        */
    @Test(enabled = true,groups = {"wso2.esb"}, description = "AcquiaContextDb {importEvent} ")
    public void importEventOptional() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "AcquiaContextDb_importEventOptional.txt";
        String methodName = "AcquiaContextDb_importEvent";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
            String modifiedJsonString = String.format(jsonString, AcquiaContextDbConnectorProperties.getProperty("secretKey"), AcquiaContextDbConnectorProperties.getProperty("accessKey"), AcquiaContextDbConnectorProperties.getProperty("accountId"),AcquiaContextDbConnectorProperties.getProperty("payload"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        try {

            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            log.info("response:" + responseHeader);
            Assert.assertTrue(responseHeader == 200);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }
        /**
        * Negative test case for importEvent method with negative parameters.
         */
    @Test(enabled=true, groups = {"wso2.esb"}, description = "AcquiaContextDb {importEventNegativeCase} integration test with negative parameter.")
    public void importEventNegativeCase() throws Exception {
        String jsonRequestFilePath = pathToRequestsDirectory + "AcquiaContextDb_importEventNegativeCase.txt";
        String methodName = "AcquiaContextDb_importEvent";
        final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
            String modifiedJsonString = String.format(jsonString, AcquiaContextDbConnectorProperties.getProperty("secretKey"), AcquiaContextDbConnectorProperties.getProperty("accessKey"), AcquiaContextDbConnectorProperties.getProperty("accountId"),AcquiaContextDbConnectorProperties.getProperty("payload"));
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
        try {
            int responseHeader = ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(methodName), modifiedJsonString);
            log.info("response:" + responseHeader);
             Assert.assertTrue(responseHeader == 401);
        } finally {
            proxyAdmin.deleteProxy(methodName);
        }
    }



}
