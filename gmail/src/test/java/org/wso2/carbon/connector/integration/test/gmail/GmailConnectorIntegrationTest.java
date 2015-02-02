/**
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

public class GmailConnectorIntegrationTest extends ESBIntegrationTest {

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
		byte maxAttempts = 3;
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
		attachmentMap = new HashMap<String,DataHandler>();
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
    
    @Test(groups = {"wso2.esb"},priority = 4,
    		description = "Gmail {sendMail} method {mandatory parameters} Integration Tests")
    public void testSendMailMandatoryParams() throws Exception {
    	final String methodName = "sendMail";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.sendmail\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:toRecipients>" + gmailConnectorProperties.get("recipientsSet1") + "</urn:toRecipients>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("sendMailResponse"), "Validated the response node name");
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }
    
    @Test(groups = {"wso2.esb"},priority = 1,
    		description = "Gmail {sendMail} method {optional parameters} Integration Tests")
    public void testSendMailOptionalParams() throws Exception {
    	final String methodName = "sendMail";    	
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.sendmail\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:subject>" + this.mailSubject + "</urn:subject>\n"
                        + "   		<urn:toRecipients>" + gmailConnectorProperties.get("recipientsSet1") + "," + gmailConnectorProperties.get("userEmailAddress") + "</urn:toRecipients>\n"
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
    
    @Test(expectedExceptions = AxisFault.class, groups = {"wso2.esb"},priority = 4,
    		description = "Gmail {sendMail} method {mandetory negative params 1} Integration Tests")
    public void testSendMailMandatoryNegativeParams1() throws Exception {
    	final String methodName = "sendMail";
    	
    	// Sending attachment IDs when the belonging files are not stored in the message context.
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.sendmail\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken> " + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:subject>" + this.mailSubject + "</urn:subject>\n"
                        + "   		<urn:textContent>This is the text content of the e-mail message.</urn:textContent>\n"
                        + "   		<urn:toRecipients>" + gmailConnectorProperties.get("recipientsSet1") + "</urn:toRecipients>\n"
                        + "   		<urn:attachmentIDs>" + StringUtils.join(fileNames, ',') + "</urn:attachmentIDs>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                 requestEnvelope);       
        try {
            mepClient.execute(true); 
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }
    
    @Test(expectedExceptions = AxisFault.class, groups = {"wso2.esb"},priority = 4,
    		description = "Gmail {sendMail} method {mandatory negative params 2} Integration Tests")
    public void testSendMailMandatoryNegativeParams2() throws Exception {
    	final String methodName = "sendMail";
    	final String[] fileNames = new String[]{"test.txt", "smile.png"};
    	
    	// Sending an e-mail message without any recipients.
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.sendmail\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken> " + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:subject>" + this.mailSubject + "</urn:subject>\n"
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
        Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        for (String fileName : fileNames) {
        	 attachmentMap.put(fileName, new DataHandler(
        	                                                  new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
        	                                                          + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
        	                                                          + File.separator + fileName))));
        }
        mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope, attachmentMap);          
        try {
            mepClient.execute(true); 
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }
    
    @Test(groups = {"wso2.esb"},priority = 2, dependsOnMethods = {"testSendMailOptionalParams" },
    		description = "Gmail {searchMails} method {mandatory parameters} Integration Tests")
    public void testSearchMailsMandatoryParams() throws Exception {
    	final String methodName = "searchMails";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.searchmails\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:gmailSearchTerm>" + "Subject:\"" + this.mailSubject + "\"" + "</urn:gmailSearchTerm>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        OperationClient mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            String  body = responseMsgCtx.getEnvelope().getBody().toString();
            Assert.assertTrue(body.contains("gmailSearchMailsResponse"), "Correct response tag name is found");
            Assert.assertTrue(body.contains(this.mailSubject), "Expected mail subject is found in the response");
            
            this.mailThreadID =  ((OMElement)responseMsgCtx.getEnvelope().getBody().getFirstElement().getFirstElement().getFirstElement().getChildrenWithLocalName("threadID").next()).getText();
            log.info("Thread ID is updated with:" + mailThreadID);
            this.mailMessageID = ((OMElement)responseMsgCtx.getEnvelope().getBody().getFirstElement().getFirstElement().getFirstElement().getChildrenWithLocalName("messageID").next()).getText();
            log.info("Message ID is updated with:" + mailMessageID);
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }
   
    @Test(groups = {"wso2.esb"},priority = 4, dependsOnMethods = {"testSendMailOptionalParams" },
    		description = "Gmail {searchMails} method {optional parameters} Integration Tests")
    public void testSearchMailsOptionalParams() throws Exception {
    	final String methodName = "searchMails";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.searchmails\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:gmailSearchTerm>" + "Subject:\"" + this.mailSubject + "\"" + "</urn:gmailSearchTerm>\n"
                        + "   		<urn:batchNumber>1</urn:batchNumber>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            String  body = responseMsgCtx.getEnvelope().getBody().toString();
            Assert.assertTrue(body.contains("gmailSearchMailsResponse"), "Correct response tag name is found"); 
            Assert.assertTrue(body.contains(this.mailSubject), "Expected mail subject is found in the response");
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }
 
    @Test(expectedExceptions = AxisFault.class, groups = {"wso2.esb"},priority = 4, dependsOnMethods = {"testSendMailOptionalParams" },
    		description = "Gmail {searchMails} method {optional negative params} Integration Tests")
    public void testSearchMailsOptionalNegativeParams() throws Exception {
    	final String methodName = "searchMails";
    	
    	// Sending an invalid batch number, 0.
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.searchmails\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:gmailSearchTerm>" + "Subject:\"" + this.mailSubject + "\"" + "</urn:gmailSearchTerm>\n"
                        + "   		<urn:batchNumber>0</urn:batchNumber>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true); 
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }

    @Test(groups = {"wso2.esb"},priority = 3, dependsOnMethods = {"testSearchMailsMandatoryParams" },
    		description = "Gmail {setLabels} method {mandatory parameters} Integration Tests")
    public void testSetLabelsMandatoryParams() throws Exception {
    	final String methodName = "setLabels";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.setlabels\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:threadID>" + this.mailThreadID + "</urn:threadID>\n"
                        + "   		<urn:labels>" + this.labelName + "</urn:labels>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            String  body = responseMsgCtx.getEnvelope().getBody().toString();
            Assert.assertTrue(body.contains("gmailSetLabelsResponse"), "Correct response tag name is found");
            Assert.assertTrue(body.contains(this.mailSubject), "Expected mail subject is found in the response");
            Assert.assertTrue(body.contains(this.labelName), "Expected label is found in the response");
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }
    
    @Test(expectedExceptions = AxisFault.class, groups = {"wso2.esb"},priority = 4,
    		description = "Gmail {setLabels} method {mandatory negative params} Integration Tests")
    public void testSetLabelsMandatoryNegativeParams() throws Exception {
    	final String methodName = "setLabels";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.setlabels\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:threadID>invalidThreadID</urn:threadID>\n"
                        + "   		<urn:labels>" + this.labelName + "</urn:labels>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true); 
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }

    @Test(groups = {"wso2.esb"},priority = 4, dependsOnMethods = {"testSetLabelsMandatoryParams" },
    		description = "Gmail {listAllMails} method {mandatory parameters} Integration Tests")
    public void testListAllMailsMandatoryParams() throws Exception {
    	final String methodName = "listAllMails";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.listallmails\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            String  body = responseMsgCtx.getEnvelope().getBody().toString();
            Assert.assertTrue(body.contains("gmailListAllMailsResponse"), "Correct response tag name is found");
            Assert.assertTrue(body.contains(this.mailSubject), "Expected mail subject is found in the response");
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }
    
    @Test(groups = {"wso2.esb"},priority = 4, dependsOnMethods = {"testSetLabelsMandatoryParams" },
    		description = "Gmail {listAllMails} method {optional parameters} Integration Tests")
    public void testListAllMailsOptionalParams() throws Exception {
    	final String methodName = "listAllMails";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.listallmails\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:label>" + this.labelName + "</urn:label>\n"
                        + "   		<urn:batchNumber>1</urn:batchNumber>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            String  body = responseMsgCtx.getEnvelope().getBody().toString();
            Assert.assertTrue(body.contains("gmailListAllMailsResponse"), "Correct response tag name is found");
            Assert.assertTrue(body.contains(this.mailSubject), "Expected mail subject is found in the response");
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }

    @Test(expectedExceptions = AxisFault.class, groups = {"wso2.esb"},priority = 4, dependsOnMethods = {"testSetLabelsMandatoryParams" },
    		description = "Gmail {listAllMails} method {optional negative params} Integration Tests")
    public void testListAllMailsOptionalNegativeParams() throws Exception {
    	final String methodName = "listAllMails";
    	
    	// Sending an invalid batch number, a negative value
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.listallmails\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:label>" + this.labelName + "</urn:label>\n"
                        + "   		<urn:batchNumber>-5</urn:batchNumber>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true); 
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }

    @Test(groups = {"wso2.esb"},priority = 4, dependsOnMethods = {"testSetLabelsMandatoryParams" },
    		description = "Gmail {listAllUnreadMails} method {mandatory parameters} Integration Tests")
    public void testListAllUnreadMailsMandatoryParams() throws Exception {
    	final String methodName = "listAllUnreadMails";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.listallunreadmails\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            String  body = responseMsgCtx.getEnvelope().getBody().toString();
            Assert.assertTrue(body.contains("gmailListAllUnreadMailsResponse"), "Correct response tag name is found");
            Assert.assertTrue(body.contains(this.mailSubject), "Expected mail subject is found in the response");
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }
    
    @Test(groups = {"wso2.esb"},priority = 4, dependsOnMethods = {"testSetLabelsMandatoryParams" },
    		description = "Gmail {listAllUnreadMails} method {optional parameters} Integration Tests")
    public void testListAllUnreadMailsOptionalParams() throws Exception {
    	final String methodName = "listAllUnreadMails";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.listallunreadmails\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:batchNumber>1</urn:batchNumber>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            String  body = responseMsgCtx.getEnvelope().getBody().toString();
            Assert.assertTrue(body.contains("gmailListAllUnreadMailsResponse"), "Correct response tag name is found");
            Assert.assertTrue(body.contains(this.mailSubject), "Expected mail subject is found in the response");
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }
    
    @Test(expectedExceptions = AxisFault.class, groups = {"wso2.esb"},priority = 4, dependsOnMethods = {"testSetLabelsMandatoryParams" },
    		description = "Gmail {listAllUnreadMails} method {optional negative params} Integration Tests")
    public void testListAllUnreadMailsOptionalNegativeParams() throws Exception {
    	final String methodName = "listAllUnreadMails";
    	
    	// Sending an invalid batch number, non integer value
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.listallunreadmails\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:batchNumber>abc</urn:batchNumber>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true);
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }

    @Test(groups = {"wso2.esb"},priority = 1, dependsOnMethods = {"testSetLabelsMandatoryParams" },
    		description = "Gmail {listAllUnreadMailsUnderLabel} method {mandatory parameters} Integration Tests")
    public void testListAllUnreadMailsUnderLabelMandatoryParams() throws Exception {
    	final String methodName = "listAllUnreadMailsUnderLabel";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.listallunreadmailsunderlabel\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:label>" + this.labelName + "</urn:label>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            String  body = responseMsgCtx.getEnvelope().getBody().toString();
            Assert.assertTrue(body.contains("gmailListAllUnreadMailsUnderLabelResponse"), "Correct response tag name is found");
            Assert.assertTrue(body.contains(this.mailSubject), "Expected mail subject is found in the response");
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }

    @Test(groups = {"wso2.esb"},priority = 4, dependsOnMethods = {"testSetLabelsMandatoryParams" },
    		description = "Gmail {listAllUnreadMailsUnderLabel} method {optional parameters} Integration Tests")
    public void testListAllUnreadMailsUnderLabelOptionalParams() throws Exception {
    	final String methodName = "listAllUnreadMailsUnderLabel";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.listallunreadmailsunderlabel\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:label>" + this.labelName + "</urn:label>\n"
                        + "   		<urn:batchNumber>1</urn:batchNumber>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);        
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            String  body = responseMsgCtx.getEnvelope().getBody().toString();
            Assert.assertTrue(body.contains("gmailListAllUnreadMailsUnderLabelResponse"), "Correct response tag name is found");
            Assert.assertTrue(body.contains(this.mailSubject), "Expected mail subject is found in the response");
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }

    @Test(expectedExceptions = AxisFault.class, groups = {"wso2.esb"},priority = 4, dependsOnMethods = {"testSetLabelsMandatoryParams" },
    		description = "Gmail {listAllUnreadMailsUnderLabel} method {optional negative params} Integration Tests")
    public void testListAllUnreadMailsUnderLabelOptionalNegativeParams() throws Exception {
    	final String methodName = "listAllUnreadMailsUnderLabel";
    	
    	// Sending an invalid batch number, non integer value
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.listallunreadmailsunderlabel\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:label>" + this.labelName + "</urn:label>\n"
                        + "   		<urn:batchNumber>1.4</urn:batchNumber>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true); 
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }

	@Test(groups = { "wso2.esb" }, priority = 5, dependsOnMethods = {
	                                                                 "testListAllUnreadMailsMandatoryParams",
	                                                                 "testListAllUnreadMailsOptionalParams",
	                                                                 "testListAllUnreadMailsUnderLabelMandatoryParams",
	                                                                 "testListAllUnreadMailsUnderLabelOptionalParams",
	                                                                 "testListAllUnreadMailsOptionalNegativeParams",
	                                                                 "testListAllUnreadMailsUnderLabelOptionalNegativeParams" }, description = "Gmail {readMailMessage} method {mandatory parameters} Integration Tests")
    public void testReadMailMessageMandatoryParams() throws Exception {
    	final String methodName = "readMailMessage";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.readmailmessage\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:messageID>" + this.mailMessageID + "</urn:messageID>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            String  body = responseMsgCtx.getEnvelope().getBody().toString();
            Assert.assertTrue(body.contains("gmailReadMailMessageResponse"), "Correct response tag name is found");
            Assert.assertTrue(body.contains(this.mailSubject), "Expected mail subject is found in the response");
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }
    
	@Test(expectedExceptions = AxisFault.class, groups = { "wso2.esb" }, priority = 4, 
			description = "Gmail {readMailMessage} method {mandatory negative params} Integration Tests")
    public void testReadMailMessageMandatoryNegativeParams() throws Exception {
    	final String methodName = "readMailMessage";
    	
    	// Sending an invalid message ID as an negative scenario
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.readmailmessage\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:messageID>InvalidMessageID</urn:messageID>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true); 
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }
    
	@Test(groups = { "wso2.esb" }, priority = 5, dependsOnMethods = {
	                                                                 "testListAllUnreadMailsMandatoryParams",
	                                                                 "testListAllUnreadMailsOptionalParams",
	                                                                 "testListAllUnreadMailsUnderLabelMandatoryParams",
	                                                                 "testListAllUnreadMailsUnderLabelOptionalParams",
	                                                                 "testListAllUnreadMailsOptionalNegativeParams",
	                                                                 "testListAllUnreadMailsUnderLabelOptionalNegativeParams" }, description = "Gmail {readMailThread} method {mandatory parameters} Integration Tests")
    public void testReadMailThreadMandatoryParams() throws Exception {
    	final String methodName = "readMailThread";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.readmailthread\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:threadID>" + this.mailThreadID + "</urn:threadID>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            String  body = responseMsgCtx.getEnvelope().getBody().toString();
            Assert.assertTrue(body.contains("gmailReadMailThreadResponse"), "Correct response tag name is found");
            Assert.assertTrue(body.contains(this.mailSubject), "Expected mail subject is found in the response");
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }
    
    @Test(expectedExceptions = AxisFault.class, groups = {"wso2.esb"},priority = 4, 
    		description = "Gmail {readMailThread} method {mandetory negative params} Integration Tests")
    public void testReadMailThreadMandatoryNegativeParams() throws Exception {
    	final String methodName = "readMailThread";
    	
    	//Sending an invalid thread ID
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.readmailthread\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:threadID>14648703677792733054</urn:threadID>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true); 
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }
    
    @Test(groups = {"wso2.esb"}, priority = 2, description = "Gmail {passwordAuthentication} method {mandatory params} Integration Tests")
    public void testPasswordAuthenticationMandatoryParams() throws Exception {
    	final String methodName = "passwordAuthentication";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.passwordauthentication\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:password> " + gmailConnectorProperties.get("password") + "</urn:password>\n"
                        + "   		<urn:subject>" + deleteMailSubject + "</urn:subject>\n"
                        + "   		<urn:textContent>This is the text content of the e-mail message.</urn:textContent>\n"
                        + "   		<urn:toRecipients>" + gmailConnectorProperties.get("recipientsSet1") + "," + gmailConnectorProperties.get("userEmailAddress") + "</urn:toRecipients>\n"
                        + "   		<urn:attachmentIDs>" + StringUtils.join(fileNames, ',') + "</urn:attachmentIDs>\n"
                        + "   		<urn:gmailSearchTerm>"  + "Subject:\"" + this.deleteMailSubject + "\"" +  "</urn:gmailSearchTerm>\n"
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
            Assert.assertTrue(body.contains("gmailSearchMailsResponse"), "Correct response tag name is found");
            Assert.assertTrue(body.contains(this.deleteMailSubject), "Expected mail subject is found in the response");
            
            this.deleteMailThreadID =  ((OMElement)responseMsgCtx.getEnvelope().getBody().getFirstElement().getFirstElement().getFirstElement().getChildrenWithLocalName("threadID").next()).getText();
            log.info("Delete mail thread ID is updated with:" + deleteMailThreadID);
            this.deleteMailMessageID = ((OMElement)responseMsgCtx.getEnvelope().getBody().getFirstElement().getFirstElement().getFirstElement().getChildrenWithLocalName("messageID").next()).getText();
            log.info("Delete mail message ID is updated with:" + deleteMailMessageID);
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }

    @Test(expectedExceptions = AxisFault.class, groups = {"wso2.esb"}, priority = 2, 
    		description = "Gmail {passwordAuthentication} method {mandatory negative params} Integration Tests")
    public void testPasswordAuthenticationMandatoryNegativeParams() throws Exception {
    	final String methodName = "passwordAuthentication";
    	final String subject = "Password Authentication Negetive Scenario";
    	// Sending invalid user name and password
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.passwordauthentication\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>invalidUsername</urn:username>\n"
                        + "   		<urn:password>InvalidPassword</urn:password>\n"
                        + "   		<urn:subject>" + subject + "</urn:subject>\n"
                        + "   		<urn:textContent>This is the text content of the e-mail message.</urn:textContent>\n"
                        + "   		<urn:toRecipients>" + gmailConnectorProperties.get("recipientsSet1") + "</urn:toRecipients>\n"
                        + "   		<urn:attachmentIDs>" + StringUtils.join(fileNames, ',') + "</urn:attachmentIDs>\n"
                        + "   		<urn:gmailSearchTerm>"  + "Subject:\"" + subject + "\"" +  "</urn:gmailSearchTerm>\n"
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
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }

    @Test(groups = {"wso2.esb"},priority = 3, dependsOnMethods = {"testPasswordAuthenticationMandatoryParams" },
    		description = "Gmail {deleteMailMessage} method {mandatory parameters} Integration Tests")
    public void testDeleteMailMessageMandatoryParams() throws Exception {
    	final String methodName = "deleteMailMessage";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.deletemailmessage\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:messageID>" + this.deleteMailMessageID + "</urn:messageID>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            String  body = responseMsgCtx.getEnvelope().getBody().toString();
            Assert.assertTrue(body.contains("gmailDeleteMailMessageResponse"), "Correct response tag name is found");
            Assert.assertTrue(body.contains(this.deleteMailSubject), "Expected mail subject is found in the response");
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }

    @Test(expectedExceptions = AxisFault.class, groups = {"wso2.esb"}, priority = 4, dependsOnMethods = {"testDeleteMailMessageMandatoryParams" },
    		description = "Gmail {deleteMailMessage} method {mandatory negative params} Integration Tests")
    public void testDeleteMailMessageMandatoryNegativeParams() throws Exception {
    	final String methodName = "deleteMailMessage";
    	
    	// Sending a message ID which does not exist in the mail box. (The e-mail message which belongs to the given message
    	// ID is already deleted by "testDeleteMailMessageMandatoryParams".
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.deletemailmessage\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:messageID>" + this.deleteMailMessageID + "</urn:messageID>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true); 
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }

    @Test(groups = {"wso2.esb"},priority = 4, dependsOnMethods = {"testDeleteMailMessageMandatoryParams" },
    		description = "Gmail {deleteMailThread} method {mandatory parameters} Integration Tests")
    public void testDeleteMailThreadMandatoryParams() throws Exception {
    	final String methodName = "deleteMailThread";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.deletemailthread\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:threadID>" + this.deleteMailThreadID + "</urn:threadID>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            String  body = responseMsgCtx.getEnvelope().getBody().toString();
            Assert.assertTrue(body.contains("gmailDeleteMailThreadResponse"), "Correct response tag name is found");
            Assert.assertTrue(body.contains(this.deleteMailSubject), "Expected mail subject is found in the response");
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }

    @Test(expectedExceptions = AxisFault.class, groups = {"wso2.esb"},priority = 5, dependsOnMethods = {"testDeleteMailThreadMandatoryParams" },
    		description = "Gmail {deleteMailThread} method {mandatory negative params} Integration Tests")
    public void testDeleteMailthreadMandatoryNegativeParams() throws Exception {
    	final String methodName = "deleteMailThread";
    	
    	// Sending a thread ID which does not exist in the mail box. (The given e-mail thread already 
    	// deleted by "testDeleteMailMessageMandatoryParams".
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.gmail.deletemailthread\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:username>" + gmailConnectorProperties.get("userEmailAddress") + "</urn:username>\n"
                        + "   		<urn:oauthAccessToken>" + gmailConnectorProperties.get("oauthAccessToken") + "</urn:oauthAccessToken>\n"
                        + "   		<urn:threadID>" + this.deleteMailThreadID + "</urn:threadID>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                                           + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION + File.separator + "proxies" + File.separator 
                                                           + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        OperationClient mepClient = null;
        mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)),
                                                                          requestEnvelope);        
        try {
            mepClient.execute(true); 
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }    	
    }
}
