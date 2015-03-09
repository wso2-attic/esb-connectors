/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
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

package org.wso2.carbon.connector.integration.test.gmail;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.proxy.admin.ProxyServiceAdminClient;
import org.wso2.carbon.automation.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.utils.axis2client.ConfigurationContextProvider;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.carbon.esb.ESBIntegrationTest;
import org.wso2.carbon.mediation.library.stub.MediationLibraryAdminServiceStub;
import org.wso2.carbon.mediation.library.stub.upload.MediationLibraryUploaderStub;

public class GmailConnectorIntegrationTestForSendAttachment extends ESBIntegrationTest {

    private static final String CONNECTOR_NAME = "gmail";

    private static final float SLEEP_TIMER_PROGRESSION_FACTOR = 0.5f;

    private static final String[] fileNames = new String[]{"test.txt", "smile.png"};

    private MediationLibraryUploaderStub mediationLibUploadStub = null;

    private MediationLibraryAdminServiceStub adminServiceStub = null;

    private ProxyServiceAdminClient proxyAdmin;

    private String repoLocation = null;

    private String gmailConnectorFileName = CONNECTOR_NAME + ".zip";

    private Properties gmailConnectorProperties = null;

    private String labelName = "IntegrationTestingLabel";

    private String mailThreadID = null;

    private String mailMessageID = null;

    private String deleteMailThreadID = null;

    private String deleteMailMessageID = null;

    private String mailSubject = null;

    private String deleteMailSubject = null;

    Map<String, DataHandler> attachmentMap;

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

        log.warn("REPOLOCATION:" + repoLocation);

        proxyAdmin = new ProxyServiceAdminClient(esbServer.getBackEndUrl(), esbServer.getSessionCookie());
        ConnectorIntegrationUtil.uploadConnector(repoLocation, mediationLibUploadStub, gmailConnectorFileName);
        byte maxAttempts = 10;
        int sleepTimer = 30000;
        for (byte attemptCount = 0; attemptCount < maxAttempts; attemptCount++) {
            Thread.sleep(sleepTimer);
            String[] libraries = adminServiceStub.getAllLibraries();
            if (Arrays.asList(libraries).contains("{org.wso2.carbon.connector}" + CONNECTOR_NAME)) {
                break;
            } else {
                sleepTimer *= SLEEP_TIMER_PROGRESSION_FACTOR;
            }
        }

        adminServiceStub.updateStatus("{org.wso2.carbon.connector}" + CONNECTOR_NAME, CONNECTOR_NAME, "org.wso2.carbon.connector", "enabled");
        gmailConnectorProperties = ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);

        // Initializing the local variables which are used in the test cases
        mailSubject = "Gmail Integration Testing (Random ID:" + UUID.randomUUID().toString() + ")";
        deleteMailSubject = "[This will be deleted] Gmail Integration Testing (Random ID:" + UUID.randomUUID().toString() + ")";
        attachmentMap = new HashMap<String, DataHandler>();
        for (String fileName : fileNames) {
            attachmentMap.put(fileName, new DataHandler(
                    new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                            + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                            + File.separator + fileName))));
        }
    }

    @Override
    protected void cleanup() {
        axis2Client.destroy();
    }

    @Test(enabled = false,groups = {"wso2.esb"},priority = 1,
            description = "Gmail {sendMail} method {optional parameters} Integration Tests")
    public void testSendMailOptionalParams() throws Exception {
        final String methodName = "sendMail";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.sendmail\">\n"
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n"
                        + "		<root>\n"
                        + "   		<urn:username>" + gmailConnectorProperties.get("userId") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("accessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:subject>" + this.mailSubject + "</urn:subject>\n"
                        + "   		<urn:toRecipients>" + gmailConnectorProperties.get("recipientsSet1") + "," + gmailConnectorProperties.get("userId") + "</urn:toRecipients>\n"
                        + "   		<urn:ccRecipients>" + gmailConnectorProperties.get("recipientsSet2") + "</urn:ccRecipients>\n"
                        + "   		<urn:bccRecipients>" + gmailConnectorProperties.get("recipientsSet3") + "</urn:bccRecipients>\n"
                        + "   		<urn:textContent>This is the text content of the e-mail message.</urn:textContent>\n"
                        + "   		<urn:attachmentIDs>" + StringUtils.join(fileNames, ',') + "</urn:attachmentIDs>\n"
                        + "		</root>\n"
                        + "   </soapenv:Body>\n"
                        + "</soapenv:Envelope>";

        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
                + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator
                + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                requestEnvelope, attachmentMap);
        try {
            mepClient.execute(true);
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            String  body = responseMsgCtx.getEnvelope().getBody().toString();
            Assert.assertTrue(body.contains("sendMailResponse"), "Correct response tag name is found");
            Assert.assertTrue(body.contains(this.mailSubject), "Expected mail subject is found in the response");
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
            // Waiting for the mail to be delivered to the mail box
            int sleepTimer = 20000;
            Thread.sleep(sleepTimer);
        }
    }


}