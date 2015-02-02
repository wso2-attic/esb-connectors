/*
 * Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.connector.integration.test.apns;

import static org.wso2.carbon.connector.apns.Utils.SOAPResponseConstants.NS_URI_APNS;
import static org.wso2.carbon.connector.apns.Utils.SOAPResponseConstants.TAG_DISPATCH_TO_DEVICE_RESULT;
import static org.wso2.carbon.connector.apns.Utils.SOAPResponseConstants.TAG_SUCCESSFUL;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.namespace.QName;

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
import org.wso2.carbon.connector.apns.PushNotificationRequest;
import org.wso2.carbon.connector.apns.Utils;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.carbon.esb.ESBIntegrationTest;
import org.wso2.carbon.mediation.library.stub.MediationLibraryAdminServiceStub;
import org.wso2.carbon.mediation.library.stub.upload.MediationLibraryUploaderStub;

/**
 * This test class covers scenarios of 'init' connector and 'dispatchToDevice'
 */
public class PushNotificationIntegrationTest extends ESBIntegrationTest {

    private static final String PACKAGE_NAME = "org.wso2.carbon.connector";
    private static final String CONNECTOR_NAME = "apns";

    private static final float SLEEP_TIMER_PROGRESSION_FACTOR = 0.5f;

    private static final String PROPERTY_KEY_APNS_CERTIFICATE_FILENAME = "certificateFilename";
    private static final String PROPERTY_KEY_DEVICE_TOKEN = "deviceToken";
    private static final String PROPERTY_KEY_CERTIFICATE_PASSWORD = "certificatePassword";
    
    private MediationLibraryUploaderStub mediationLibUploadStub = null;
    private MediationLibraryAdminServiceStub adminServiceStub = null;
    private ProxyServiceAdminClient proxyAdmin;

    private String repoLocation = null;

    private String apnsConnectorFileName = CONNECTOR_NAME + ".zip";

    private Properties apnsProperties = null;
    
    /**
     * Initializes the ESB instance with relevant artifacts.
     * 
     * @throws Exception
     *             When initialization fails.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

	super.init();

	ConfigurationContextProvider configurationContextProvider = ConfigurationContextProvider
		.getInstance();
	ConfigurationContext cc = configurationContextProvider
		.getConfigurationContext();

	mediationLibUploadStub = new MediationLibraryUploaderStub(cc,
		esbServer.getBackEndUrl() + "MediationLibraryUploader");
	AuthenticateStub.authenticateStub("admin", "admin",
		mediationLibUploadStub);

	adminServiceStub = new MediationLibraryAdminServiceStub(cc,
		esbServer.getBackEndUrl() + "MediationLibraryAdminService");
	AuthenticateStub.authenticateStub("admin", "admin", adminServiceStub);

	if (System.getProperty("os.name").toLowerCase().contains("windows")) {
	    repoLocation = System.getProperty("connector_repo").replace("/",
		    "\\");
	} else {
	    repoLocation = System.getProperty("connector_repo").replace("/",
		    "/");
	}

	proxyAdmin = new ProxyServiceAdminClient(esbServer.getBackEndUrl(),
		esbServer.getSessionCookie());

	ConnectorIntegrationUtil.uploadConnector(repoLocation,
		mediationLibUploadStub, apnsConnectorFileName);

	byte maxAttempts = 3;
	int sleepTimer = 30000;
	boolean isConnectorAvailable = false;
	for (byte attemptCount = 0; attemptCount < maxAttempts; attemptCount++) {
	    Thread.sleep(sleepTimer);
	    String[] libraries = adminServiceStub.getAllLibraries();
	    if (Arrays.asList(libraries).contains(
		    String.format("{%s}%s", PACKAGE_NAME, CONNECTOR_NAME))) {
		isConnectorAvailable = true;
		break;
	    } else {
		sleepTimer *= SLEEP_TIMER_PROGRESSION_FACTOR;
	    }
	}

	if (isConnectorAvailable) {
	    adminServiceStub.updateStatus(
		    String.format("{%s}%s", PACKAGE_NAME, CONNECTOR_NAME),
		    CONNECTOR_NAME, PACKAGE_NAME, "enabled");
	} else {
	    Assert.fail("Connector is not available");
	}

	apnsProperties = ConnectorIntegrationUtil
		.getConnectorConfigProperties(CONNECTOR_NAME);

    }

    /**
     * Test case to verify that the connector works only with mandatory
     * parameters.
     * 
     * @throws Exception
     */
    @Test(groups = { "org.wso2.carbon.connector.apns" }, priority = 1, description = "Integration test to cover mandatory parameters.")
    public void testSendPushNotificationWithMandatoryParams() throws Exception {

	// Add proxy service
	String proxyServiceName = "apns_push";

	URL proxyUrl = new URL("file:" + File.separator + File.separator
		+ ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
		+ ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator
		+ "proxies" + File.separator + CONNECTOR_NAME + File.separator
		+ proxyServiceName + ".xml");

	proxyAdmin.addProxyService(new DataHandler(proxyUrl));

	// Construct and send the request.

	String environment = PushNotificationRequest.SANDBOX_DESTINATION;
	String certificateAttachmentName = "certificate";
	String password = apnsProperties.getProperty(PROPERTY_KEY_CERTIFICATE_PASSWORD);

	String deviceToken = apnsProperties.getProperty(PROPERTY_KEY_DEVICE_TOKEN);

	String requestTemplate = ConnectorIntegrationUtil
		.getRequest("with_mandatory_params");
	String request = String.format(requestTemplate, environment,
		certificateAttachmentName, password, deviceToken);
	OMElement requestEnvelope = AXIOMUtil.stringToOM(request);

	String certificateFileName = apnsProperties.getProperty(PROPERTY_KEY_APNS_CERTIFICATE_FILENAME);
	Map<String, DataHandler> attachmentMap = new HashMap<String, DataHandler>();
	attachmentMap.put(certificateAttachmentName, new DataHandler(
		new FileDataSource(new File(
			ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
				+ ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
				+ File.separator + "auth" + File.separator
				+ certificateFileName))));

	OperationClient mepClient = ConnectorIntegrationUtil
		.buildMEPClientWithAttachment(new EndpointReference(
			getProxyServiceURL(proxyServiceName)), requestEnvelope,
			attachmentMap);

	// Test the result.
	try {
	    mepClient.execute(true);

	    MessageContext responseMsgCtx = mepClient
		    .getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);

	    OMElement resultParentTag = responseMsgCtx
		    .getEnvelope()
		    .getBody()
		    .getFirstChildWithName(
			    new QName(NS_URI_APNS,
				    TAG_DISPATCH_TO_DEVICE_RESULT));

	    OMElement successfulTag = resultParentTag
		    .getFirstChildWithName(new QName(NS_URI_APNS,
			    TAG_SUCCESSFUL));
	    Assert.assertEquals("true", successfulTag.getText());

	} finally {
	    proxyAdmin.deleteProxy(proxyServiceName);
	}
    }

    /**
     * Test case to verify that the connectors works as expected with optional
     * parameters.
     * 
     * @throws Exception
     */
    @Test(groups = { "org.wso2.carbon.connector.apns" }, priority = 1, description = "Integration test to cover optional parameters")
    public void testSendPushNotificationOptionalParams() throws Exception {

	// Add proxy service
	String proxyServiceName = "apns_push";

	URL proxyUrl = new URL("file:" + File.separator + File.separator
		+ ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
		+ ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator
		+ "proxies" + File.separator + CONNECTOR_NAME + File.separator
		+ proxyServiceName + ".xml");

	proxyAdmin.addProxyService(new DataHandler(proxyUrl));

	// Construct and send the request.

	String environment = PushNotificationRequest.SANDBOX_DESTINATION;
	String certificateAttachmentName = "certificate";
	String password = apnsProperties.getProperty(PROPERTY_KEY_CERTIFICATE_PASSWORD);

	String deviceToken = apnsProperties.getProperty(PROPERTY_KEY_DEVICE_TOKEN);

	String alert = String.format("Test Message : %s", UUID.randomUUID()
		.toString());
	int badge = new Random().nextInt(10);
	String sound = String.format("sound_%s", new Random().nextInt(20));

	String requestTemplate = ConnectorIntegrationUtil
		.getRequest("with_optional_params");
	String request = String.format(requestTemplate, environment,
		certificateAttachmentName, password, deviceToken, alert,
		Integer.toString(badge), sound);
	OMElement requestEnvelope = AXIOMUtil.stringToOM(request);

	String certificateFileName = apnsProperties.getProperty(PROPERTY_KEY_APNS_CERTIFICATE_FILENAME);
	Map<String, DataHandler> attachmentMap = new HashMap<String, DataHandler>();
	attachmentMap.put(certificateAttachmentName, new DataHandler(
		new FileDataSource(new File(
			ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
				+ ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
				+ File.separator + "auth" + File.separator
				+ certificateFileName))));

	OperationClient mepClient = ConnectorIntegrationUtil
		.buildMEPClientWithAttachment(new EndpointReference(
			getProxyServiceURL(proxyServiceName)), requestEnvelope,
			attachmentMap);
	try {
	    mepClient.execute(true);

	    MessageContext responseMsgCtx = mepClient
		    .getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);

	    OMElement resultParentTag = responseMsgCtx
		    .getEnvelope()
		    .getBody()
		    .getFirstChildWithName(
			    new QName(NS_URI_APNS,
				    TAG_DISPATCH_TO_DEVICE_RESULT));

	    OMElement successfulTag = resultParentTag
		    .getFirstChildWithName(new QName(NS_URI_APNS,
			    TAG_SUCCESSFUL));
	    Assert.assertEquals("true", successfulTag.getText());

	} finally {
	    proxyAdmin.deleteProxy(proxyServiceName);
	}
    }

    /**
     * Test case to verify that the connector properly handles errors when the
     * certificate is missing.
     * 
     * @throws Exception
     */
    @Test(groups = { "org.wso2.carbon.connector.apns" }, priority = 1, description = "Integration test to cover error handling logic when the certificate is not available.", expectedExceptions = AxisFault.class, expectedExceptionsMessageRegExp = ".*"
	    + Utils.Errors.ERROR_CODE_INVALID_CERTIFICATE_INFO + ".*")
    public void testSendPushNotificationWithNoCertificate() throws Exception {

	// Add proxy service
	String proxyServiceName = "apns_push";

	URL proxyUrl = new URL("file:" + File.separator + File.separator
		+ ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
		+ ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator
		+ "proxies" + File.separator + CONNECTOR_NAME + File.separator
		+ proxyServiceName + ".xml");

	proxyAdmin.addProxyService(new DataHandler(proxyUrl));

	// Construct and send the request.

	String environment = PushNotificationRequest.SANDBOX_DESTINATION;
	String certificateAttachmentName = "certificate";
	String password = apnsProperties.getProperty(PROPERTY_KEY_CERTIFICATE_PASSWORD);

	String deviceToken = apnsProperties.getProperty(PROPERTY_KEY_DEVICE_TOKEN);

	String requestTemplate = ConnectorIntegrationUtil
		.getRequest("with_mandatory_params");
	String request = String.format(requestTemplate, environment,
		certificateAttachmentName, password, deviceToken);
	OMElement requestEnvelope = AXIOMUtil.stringToOM(request);

	OperationClient mepClient = ConnectorIntegrationUtil.buildMEPClient(
		new EndpointReference(getProxyServiceURL(proxyServiceName)),
		requestEnvelope);
	try {
	    mepClient.execute(true);
	} finally {
	    proxyAdmin.deleteProxy(proxyServiceName);
	}
    }

    /**
     * Test case to verify that the connector properly handles errors when
     * certificate password is wrong.
     * 
     * @throws Exception
     */
    @Test(groups = { "org.wso2.carbon.connector.apns" }, priority = 1, description = "Integration test to cover error handling logic when the certificate password is wrong", expectedExceptions = AxisFault.class, expectedExceptionsMessageRegExp = ".*"
	    + Utils.Errors.ERROR_CODE_INVALID_CERTIFICATE_INFO + ".*")
    public void testSendPushNotificationWithWrongPassword() throws Exception {

	// Add proxy service
	String proxyServiceName = "apns_push";

	URL proxyUrl = new URL("file:" + File.separator + File.separator
		+ ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
		+ ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator
		+ "proxies" + File.separator + CONNECTOR_NAME + File.separator
		+ proxyServiceName + ".xml");

	proxyAdmin.addProxyService(new DataHandler(proxyUrl));

	// Construct and send the request.

	String environment = PushNotificationRequest.SANDBOX_DESTINATION;
	String certificateAttachmentName = "certificate";
	String password = "wrong_password";

	String deviceToken = apnsProperties.getProperty(PROPERTY_KEY_DEVICE_TOKEN);

	String requestTemplate = ConnectorIntegrationUtil
		.getRequest("with_mandatory_params");
	String request = String.format(requestTemplate, environment,
		certificateAttachmentName, password, deviceToken);
	OMElement requestEnvelope = AXIOMUtil.stringToOM(request);

	String certificateFileName = apnsProperties.getProperty(PROPERTY_KEY_APNS_CERTIFICATE_FILENAME);
	Map<String, DataHandler> attachmentMap = new HashMap<String, DataHandler>();
	attachmentMap.put(certificateAttachmentName, new DataHandler(
		new FileDataSource(new File(
			ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
				+ ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
				+ File.separator + "auth" + File.separator
				+ certificateFileName))));

	OperationClient mepClient = ConnectorIntegrationUtil
		.buildMEPClientWithAttachment(new EndpointReference(
			getProxyServiceURL(proxyServiceName)), requestEnvelope,
			attachmentMap);
	try {
	    mepClient.execute(true);
	} finally {
	    proxyAdmin.deleteProxy(proxyServiceName);
	}
    }

    /**
     * Test case to verify that the connector properly handles errors when the payload is too long.
     */
    @Test(groups = { "org.wso2.carbon.connector.apns" }, priority = 1, description = "Integration test to cover error handling logic when the payload is too long", expectedExceptions = AxisFault.class, expectedExceptionsMessageRegExp = ".*"
	    + Utils.Errors.ERROR_CODE_PAYLOAD_ERROR + ".*")
    public void testSendPushNotificationWithTooLongPayload() throws Exception {

	// Add proxy service
	String proxyServiceName = "apns_push";

	URL proxyUrl = new URL("file:" + File.separator + File.separator
		+ ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
		+ ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator
		+ "proxies" + File.separator + CONNECTOR_NAME + File.separator
		+ proxyServiceName + ".xml");

	proxyAdmin.addProxyService(new DataHandler(proxyUrl));

	// Construct and send the request.

	String environment = PushNotificationRequest.SANDBOX_DESTINATION;
	String certificateAttachmentName = "certificate";
	String password = apnsProperties.getProperty(PROPERTY_KEY_CERTIFICATE_PASSWORD);

	String deviceToken = apnsProperties.getProperty(PROPERTY_KEY_DEVICE_TOKEN);
	String alert = String.format("Message : %s",
		StringUtils.repeat("x", 256));
	String sound = "sound";
	int badge = 1;

	String requestTemplate = ConnectorIntegrationUtil
		.getRequest("with_optional_params");
	String request = String.format(requestTemplate, environment,
		certificateAttachmentName, password, deviceToken, alert, badge,
		sound);
	OMElement requestEnvelope = AXIOMUtil.stringToOM(request);

	String certificateFileName = apnsProperties.getProperty(PROPERTY_KEY_APNS_CERTIFICATE_FILENAME);
	Map<String, DataHandler> attachmentMap = new HashMap<String, DataHandler>();
	attachmentMap.put(certificateAttachmentName, new DataHandler(
		new FileDataSource(new File(
			ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
				+ ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
				+ File.separator + "auth" + File.separator
				+ certificateFileName))));

	OperationClient mepClient = ConnectorIntegrationUtil
		.buildMEPClientWithAttachment(new EndpointReference(
			getProxyServiceURL(proxyServiceName)), requestEnvelope,
			attachmentMap);

	try {
	    mepClient.execute(true);
	} finally {
	    proxyAdmin.deleteProxy(proxyServiceName);
	}
    }

}
