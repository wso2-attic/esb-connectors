/*
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
 */
package org.wso2.carbon.connector.integration.test;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.context.ConfigurationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.axis2client.ConfigurationContextProvider;
import org.wso2.carbon.connector.integration.test.util.ConnectorIntegrationUtil;
import org.wso2.carbon.mediation.library.stub.MediationLibraryAdminServiceStub;
import org.wso2.carbon.mediation.library.stub.upload.MediationLibraryUploaderStub;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import java.util.Properties;

public class RMConnectorIntegrationTest extends ESBIntegrationTest {

    private static final String CONNECTOR_NAME = "reliable-message";

    private String reliableMessageConnectorFileName = "reliable-message-connector.zip";

    private MediationLibraryUploaderStub mediationLibUploadStub = null;

    private MediationLibraryAdminServiceStub adminServiceStub = null;

    private String repoLocation = null;

    private Properties reliableMessagingConnectorProperties = null;

    private String pathToRequestsDirectory = null;

    private AutomationContext automationContext = null;


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        super.init();
        automationContext = new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN);

        ConfigurationContextProvider configurationContextProvider = ConfigurationContextProvider.getInstance();
        ConfigurationContext cc = configurationContextProvider.getConfigurationContext();

        mediationLibUploadStub =
                new MediationLibraryUploaderStub(cc, automationContext.getContextUrls().getBackEndUrl() + "MediationLibraryUploader");
        AuthenticateStub.authenticateStub("admin", "admin", mediationLibUploadStub);
        adminServiceStub =
                new MediationLibraryAdminServiceStub(cc, automationContext.getContextUrls().getBackEndUrl() + "MediationLibraryAdminService");

        AuthenticateStub.authenticateStub("admin", "admin", adminServiceStub);

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            repoLocation = System.getProperty("connector_repo").replace("/", "\\");
        } else {
            repoLocation = System.getProperty("connector_repo").replace("/", "/");
        }

        ConnectorIntegrationUtil.uploadConnector(repoLocation, mediationLibUploadStub, reliableMessageConnectorFileName);
        log.info("Sleeping for " + 30000 / 1000 + " seconds while waiting for synapse import");
        Thread.sleep(30000);

        adminServiceStub.updateStatus("{org.wso2.carbon.connector}" + CONNECTOR_NAME, CONNECTOR_NAME,
                                      "org.wso2.carbon.connector", "enabled");

        reliableMessagingConnectorProperties = ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);
        pathToRequestsDirectory = repoLocation + reliableMessagingConnectorProperties.getProperty("requestDirectoryPath");

        String synapseConfigPath = reliableMessagingConnectorProperties.getProperty("synapseConfigDirectoryPath");

        loadESBConfigurationFromClasspath(synapseConfigPath + "synapseRmConnectorConfig.xml");

    }

    @Test(enabled = true, description = "Send reliable message success request")
    public void sendRmEnableRequest() throws Exception {

        String methodName = "rmsend";
        String actualResponse = "Hello Gil";
        String pathToMessage = pathToRequestsDirectory + "greetMeRequest.xml";

        final String soapRequestString = ConnectorIntegrationUtil
                .getFileContent(pathToMessage);

        OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
                getProxyServiceURLHttp(methodName), soapRequestString);

        Assert.assertEquals(omElement.getFirstElement().getText(), actualResponse, "Expected response not found");

    }

}
