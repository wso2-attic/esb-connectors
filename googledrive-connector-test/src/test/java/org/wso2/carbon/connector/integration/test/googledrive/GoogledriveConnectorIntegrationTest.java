/**
 *  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.integration.test.googledrive;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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

public class GoogledriveConnectorIntegrationTest extends ESBIntegrationTest {
    
    private static final String CONNECTOR_NAME = "googledrive";
    
    private static final float SLEEP_TIMER_PROGRESSION_FACTOR = 0.5f;
    
    private MediationLibraryUploaderStub mediationLibUploadStub = null;
    
    private MediationLibraryAdminServiceStub adminServiceStub = null;
    
    private ProxyServiceAdminClient proxyAdmin;
    
    private String repoLocation = null;
    
    private String googledriveConnectorFileName = CONNECTOR_NAME + ".zip";
    
    private Properties googleDriveConnectorProperties = null;
    
    private static String fileId;
    
    private static String commentId;
    
    private static String folderId;
    
    
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
        // log.info("REPOLOCATION:" +repoLocation);
        
        proxyAdmin = new ProxyServiceAdminClient(esbServer.getBackEndUrl(), esbServer.getSessionCookie());
        
        ConnectorIntegrationUtil.uploadConnector(repoLocation, mediationLibUploadStub, googledriveConnectorFileName);
        byte maxAttempts = 3;
        int sleepTimer = 30000;
        for (byte attemptCount = 0; attemptCount < maxAttempts; attemptCount++ ) {
            Thread.sleep(sleepTimer);
            String [] libraries = adminServiceStub.getAllLibraries();
            if ( Arrays.asList(libraries).contains("{org.wso2.carbon.connector}" + CONNECTOR_NAME)) {
                break;
            } else {
                sleepTimer *= SLEEP_TIMER_PROGRESSION_FACTOR;
            }
                
        }

        adminServiceStub.updateStatus("{org.wso2.carbon.connector}" + CONNECTOR_NAME, CONNECTOR_NAME, "org.wso2.carbon.connector", "enabled");
        googleDriveConnectorProperties = ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);
        fileId = (String)googleDriveConnectorProperties.get("backupFileId");
        
    }
    
    @Override
    protected void cleanup() {
    
        axis2Client.destroy();
    }
    
    @Test(groups = {"wso2.esb"}, priority = 2, dependsOnMethods = {"testInsertFileMandatoryParams" },
            description = "Google Drive {getFile} method {mandatory parameters} Integration Tests")
    public void testGetFileMandatoryParams() throws Exception {
    
        final String methodName = "getFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.getfile\">\n" + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "       <root>\n" 
                        + "           <urn:useServiceAccount>"+ googleDriveConnectorProperties.get("useServiceAccount")+ "</urn:useServiceAccount>\n"
                        + "           <urn:serviceAccountEmail>"+ googleDriveConnectorProperties.get("serviceAccountEmail")+ "</urn:serviceAccountEmail>\n"
                        + "           <urn:certificatePassword>"+ googleDriveConnectorProperties.get("certificatePassword")+ "</urn:certificatePassword>\n"
                        + "           <urn:clientId>"+ googleDriveConnectorProperties.get("clientId")+ "</urn:clientId>\n"
                        + "           <urn:clientSecret>"+ googleDriveConnectorProperties.get("clientSecret")+ "</urn:clientSecret>\n"
                        + "           <urn:refreshToken>"+ googleDriveConnectorProperties.get("refreshToken")+ "</urn:refreshToken>\n"
                        + "           <urn:accessToken>"+ googleDriveConnectorProperties.get("accessToken")+ "</urn:accessToken>\n"
                        + "           <urn:fileId>" + fileId + "</urn:fileId>\n"
                        + "       </root>\n"
                        + "   </soapenv:Body>\n"
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE); 
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("getFileResult"));
            
            
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(groups = {"wso2.esb"},priority = 2,  dependsOnMethods = {"testInsertFileMandatoryParams" },
            description = "Google Drive {getFile} method {optional parameters} Integration Tests")
    public void testGetFileWithOptionalParams() throws Exception {
    
        final String methodName = "getFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.getfile\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "       <root>\n" 
                        + "           <urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "           <urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "           <urn:certificatePassword>" + googleDriveConnectorProperties.get("certificatePassword") + "</urn:certificatePassword>\n"
                        + "           <urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "           <urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "           <urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "           <urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "           <urn:fileId>" + fileId + "</urn:fileId>\n"
                        + "           <urn:updateViewedDate>false</urn:updateViewedDate>\n"
                        + "       </root>\n"
                        + "   </soapenv:Body>\n"
                        + "</soapenv:Envelope>";
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE); 
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("getFileResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(expectedExceptions = AxisFault.class,priority = 2, groups = {"wso2.esb"},
            dependsOnMethods = {"testInsertFileMandatoryParams" }, description = "Google Drive {getFile} method {negative scenario} Integration Tests")
    public void testGetFileNegativeScenario() throws Exception {
    
        final String methodName = "getFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.getfile\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "			<urn:fileId>ggghgjk</urn:fileId>\n" 
                        + "			<urn:updateViewedDate>false</urn:updateViewedDate>\n"
                        + "		</root>\n"
                        + "   </soapenv:Body>\n"
                        + "</soapenv:Envelope>";
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true);
            
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(groups = {"wso2.esb"},priority = 2, dependsOnMethods = {"testInsertFileMandatoryParams" },
            description = "Google Drive {insertComment} method {mandatory parameters} Integration Tests")
    public void testInsertCommentMandatoryParams() throws Exception {
    
        final String methodName = "insertComment";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.insertcomment\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "			<urn:fileId>" + fileId + "</urn:fileId>\n"
                        + "			<urn:content>Test21</urn:content>\n"
                        + "		</root>\n"
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE); 
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("insertCommentResult"));
            commentId = responseMsgCtx.getEnvelope().getBody().getFirstElement().getFirstChildWithName(new QName("commentId")).getText();
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(groups = {"wso2.esb"},priority = 2, dependsOnMethods = {"testInsertFileMandatoryParams" }, 
            description = "Google Drive {insertComment} method {optional parameters} Integration Tests")
    public void testInsertCommentOptionalParams() throws Exception {
    
        final String methodName = "insertComment";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.insertcomment\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "			<urn:fileId>" + fileId + "</urn:fileId>\n"
                        + "			<urn:content>Insert Comment Integration Test</urn:content>\n" 
                        + "         <urn:context.type>text/plain</urn:context.type>\n"
                        + "         <urn:context.value>insert comment test</urn:context.value>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("insertCommentResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(expectedExceptions = AxisFault.class,priority = 2, groups = {"wso2.esb"}, dependsOnMethods = {"testInsertFileMandatoryParams" },  description = "Google Drive {insertComment} method {negative scenario} Integration Tests")
    public void testInsertCommentNegativeScenario() throws Exception {
    
        final String methodName = "insertComment";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.insertcomment\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "			<urn:fileId></urn:fileId>\n"
                        + "			<urn:content>Test21</urn:content>\n"
                        + "			<urn:requestBody/>\n"
                        + "		</root>\n"
                        + "   </soapenv:Body>\n"
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true);
           
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(groups = {"wso2.esb"},priority = 2,
            description = "Google Drive {listFiles} method {mandatory parameters} Integration Tests")
    public void testlistFilesMandatoryParams() throws Exception {
    
        final String methodName = "listFiles";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.listfiles\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n" 
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("listFileResult"));
            /*Iterator <?> elementIterator = responseMsgCtx.getEnvelope().getBody().getFirstElement().getChildrenWithLocalName("items");
            while(elementIterator.hasNext()) {
                OMElement itemsElement = (OMElement)elementIterator.next();
                OMElement mimeTypeElement = itemsElement.getFirstChildWithName(new QName("mimeType"));
                if(mimeTypeElement.getText().equals("application/vnd.google-apps.folder")) {
                    folderId = itemsElement.getFirstChildWithName(new QName("id")).getText();
                    log.info("Folder id is: "+ folderId);
                    break;
                }
            }*/
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(groups = {"wso2.esb"},priority = 2,
            description = "Google Drive {listFiles} method {optional parameters} Integration Tests")
    public void testlistFilesWithOptionalParams() throws Exception {
    
        final String methodName = "listFiles";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.listfiles\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "			<urn:maxResults>2</urn:maxResults>\n"
                        + "			<urn:pageToken></urn:pageToken>\n"
                        + "         <urn:q></urn:q>\n" 
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("listFileResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(expectedExceptions = AxisFault.class,priority = 2, groups = {"wso2.esb"}, description = "Google Drive {listFiles} method {NegativeScenario} Integration Tests")
    public void testlistFilesNegativeScenario() throws Exception {
    
        final String methodName = "listFiles";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.listfiles\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "			<urn:maxResults>2</urn:maxResults>\n"
                        + "			<urn:pageToken>!o!|~EAIakQELEgBSiAEKfwpn-POxdf_____DE8X9yha84AD_Af_-Q29zbW8udXNlcigwMDAwMDAwMTEyNzlhMWE1VSkuZGlyX2VudHJ5KDQ2MDQ5Mjg0MjFcLjE0XC5zb1k3ckhGTTlFTjRXY295QmhXMHJvdykAARACISYGbvKZtY2vOQAAAACKTgwASAIgAfCElXwBDEAAIgsJpaF5EgEAAAAgBg</urn:pageToken>\n"
                        + "         <urn:q>title=</urn:q>\n" 
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true);
            
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(groups = {"wso2.esb"},priority = 2,
    		dependsOnMethods = {"testInsertFileMandatoryParams", "testInsertFileToFolderMandatoryParams" },
    		description = "Google Drive {getChild} method {mandatory parameters} Integration Tests")
    public void testgetChildMandatoryParams() throws Exception {
    
        final String methodName = "getChild";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.getchild\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "			<urn:childId>" + fileId + "</urn:childId>\n"
                        + "			<urn:folderId>" + folderId + "</urn:folderId>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("getChildResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(expectedExceptions = AxisFault.class,priority = 2, dependsOnMethods = {"testInsertFileMandatoryParams" }, groups = {"wso2.esb"}, description = "Google Drive {getChild} method {NegativeScenario} Integration Tests")
    public void testgetChildNegativeScenario() throws Exception {
    
        final String methodName = "getChild";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.getchild\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "         <urn:childId>covY7_uknzj0yxg0M8HkUYAMe98GvW61lsfk41fGj5U</urn:childId>\n"
                        + "         <urn:folderId>0B2Cr40vxAph5ZXNrYXBPdDl2UFE</urn:folderId>\n" + "		</root>\n" + "   </soapenv:Body>\n" + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true);
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(groups = {"wso2.esb"},priority = 1,
    		description = "Google Drive {insertFile} method {mandatory parameters} Integration Tests")
    public void testInsertFileMandatoryParams() throws Exception {
    
        final String methodName = "insertFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.insertfile\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n" 
                        + "		    <urn:uploadType>resumable</urn:uploadType>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
        	attachmentMap.put("file", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "test.txt"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("file", new DataHandler(
                    new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                            + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                            + File.separator + "test.txt"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        }
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("insertedFileResult"));
            fileId = responseMsgCtx.getEnvelope().getBody().getFirstElement().getFirstChildWithName(new QName("id")).getText();
            log.info("File ID is: " + fileId);
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }

    @Test(groups = {"wso2.esb"},priority = 1,
            description = "Google Drive {insertFile} method {folder insert} Integration Tests")
    public void testInsertFileWithFolderInsert() throws Exception {
    
        final String methodName = "insertFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.insertfile\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "     <root>\n" 
                        + "         <urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "         <urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "         <urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "         <urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "         <urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "         <urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n" 
                        + "         <urn:uploadType>resumable</urn:uploadType>\n"
                        + "         <urn:fileResource>\n"
                        + "             <mimeType>application/vnd.google-apps.folder</mimeType>\n"
                        + "         </urn:fileResource>\n"
                        + "     </root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
            Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
            attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("insertedFileResult"));
            folderId = responseMsgCtx.getEnvelope().getBody().getFirstElement().getFirstChildWithName(new QName("id")).getText();
            log.info("Folder ID is: " + fileId);
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }
    @Test(groups = {"wso2.esb"}, priority = 4,  
            description = "Google Drive {insertFile} method {optional parameters} Integration Tests")
    public void testInsertFileWithOptionalParams() throws Exception {
    
        final String methodName = "insertFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.insertfile\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "		    <urn:uploadType>resumable</urn:uploadType>\n"
                        + "		    <urn:convert>true</urn:convert>\n"
                        + "		    <urn:ocr>false</urn:ocr>\n"
                        + "		    <urn:ocrLanguage>en</urn:ocrLanguage>\n"
                        + "		    <urn:pinned>true</urn:pinned>\n"
                        + "		    <urn:timedTextLanguage>en</urn:timedTextLanguage>\n"
                        + "		    <urn:timedTextTrackName>test</urn:timedTextTrackName>\n"
                        + "		    <urn:useContentAsIndexableText>false</urn:useContentAsIndexableText>\n"
                        + "		    <urn:visibility>DEFAULT</urn:visibility>\n"
                        + "         <urn:fileResource>"
                        + "             <writersCanShare>true</writersCanShare>\n"
                        + "             <title>Inserted Resumable Optional Params File</title>\n"
                        + "             <description>Optional params file description</description>\n"
                        + "             <mimeType>text/plain</mimeType>\n"
                        + "         </urn:fileResource>\n"
                        + "         <urn:labels>\n"
                        + "             <hidden>true</hidden>\n"
                        + "             <restricted>false</restricted>\n"
                        + "             <starred>false</starred>\n"
                        + "         </urn:labels>\n"
                        + "         <urn:properties>\n"
                        + "             <property>\n"
                        + "                 <key>test</key>\n"
                        + "                 <kind>drive#property</kind>\n"
                        + "                 <value>Test</value>\n"
                        + "             </property>\n"
                        + "             <property>\n"
                        + "                 <key>another</key>\n"
                        + "                 <kind>drive#property</kind>\n"
                        + "                 <value>another</value>\n"
                        + "             </property>\n"
                        + "         </urn:properties>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
        	attachmentMap.put("file", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "test.txt"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("file", new DataHandler(
                    new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                            + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                            + File.separator + "test.txt"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        }
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("insertedFileResult"));
            fileId = responseMsgCtx.getEnvelope().getBody().getFirstElement().getFirstChildWithName(new QName("id")).getText();
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }
    @Test(expectedExceptions = AxisFault.class, 
    		groups = {"wso2.esb"}, priority = 1,
    		description = "Google Drive {insertFile} method {negative scenario} Integration Tests")
    public void testInsertFileNegativeScenario() throws Exception {
    
        final String methodName = "insertFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.insertfile\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                		+ "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>fdafs" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>fdadfs" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
        	attachmentMap.put("file", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "test.txt"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("file", new DataHandler(
                    new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                            + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                            + File.separator + "test.txt"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        }
        
        
        try {
            mepClient.execute(true);
           
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }
    @Test(groups = {"wso2.esb"},priority = 2,
            dependsOnMethods = {"testInsertFileMandatoryParams" }, 
            description = "Google Drive {updateFile} method {mandatory parameters with file content update} Integration Tests")
    public void testUpdateFileMandatoryParamsWithFileContentUpdate() throws Exception {
    
        final String methodName = "updateFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.updatefile\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "     <root>\n" 
                        + "         <urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "         <urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "         <urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "         <urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "         <urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "         <urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n" 
                        + "         <urn:uploadType>resumable</urn:uploadType>\n"
                        + "         <urn:fileId>" + fileId + "</urn:fileId>\n"    
                        + "     </root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
            Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
            attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            attachmentMap.put("file", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "update_test.txt"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
            attachmentMap.put("file", new DataHandler(
                    new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                            + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                            + File.separator + "update_test.txt"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        }
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("updateFileResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }
    @Test(groups = {"wso2.esb"},priority = 2,
            dependsOnMethods = {"testInsertFileMandatoryParams" }, 
            description = "Google Drive {updateFile} method {optional parameters} Integration Tests")
    public void testUpdateFileOptionalParams() throws Exception {
    
        final String methodName = "updateFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.updatefile\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "     <root>\n" 
                        + "         <urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "         <urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "         <urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "         <urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "         <urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "         <urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n" 
                        + "         <urn:fileId>" + fileId + "</urn:fileId>\n"
                        + "         <urn:newRevision>true</urn:newRevision>\n"
                        + "         <urn:fileResource>"
                        + "             <title>Updated Optional params File</title>\n"
                        + "             <description>Updated optional params File description</description>\n"
                        + "             <writersCanShare>true</writersCanShare>\n"
                        + "         </urn:fileResource>\n"
                        + "         <urn:properties>\n"
                        + "             <property>\n"
                        + "                 <key>test</key>\n"
                        + "                 <kind>drive#property</kind>\n"
                        + "                 <value>test property</value>\n"
                        + "             </property>\n"
                        + "         </urn:properties>\n"
                        + "     </root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
            Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
            attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("updateFileResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }
    @Test(groups = {"wso2.esb"},expectedExceptions = AxisFault.class, priority = 2,
            dependsOnMethods = {"testInsertFileMandatoryParams" },
            description = "Google Drive {updateFile} method {negative scenario} Integration Tests")
    public void testUpdateFileNegativeScenario() throws Exception {
    
        final String methodName = "updateFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.updatefile\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "     <root>\n" 
                        + "         <urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "         <urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "         <urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "         <urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "         <urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "         <urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n" 
                        + "         <urn:fileId>sdadasdsa</urn:fileId>\n"    
                        + "     </root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
            Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
            attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        try {
            mepClient.execute(true); 
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }
    @Test(groups = {"wso2.esb"},priority = 2, 
    		dependsOnMethods = {"testInsertFileMandatoryParams" }, description = "Google Drive {touchFile} method {mandatory parameters} Integration Tests")
    public void testTouchFileMandatoryParams() throws Exception {
    
        final String methodName = "touchFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.touchfile\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>"  + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n" 
                        + "			<urn:fileId>" + fileId + "</urn:fileId>\n" 
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("touchFileResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(expectedExceptions = AxisFault.class,priority = 2,
    		dependsOnMethods = {"testInsertFileMandatoryParams" }, groups = {"wso2.esb"}, description = "Google Drive {touchFile} method {negative scenario} Integration Tests")
    public void testTouchFileNegativeScenario() throws Exception {
    
        final String methodName = "touchFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.touchfile\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n" 
                        + "			<urn:fileId></urn:fileId>\n" 
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true);
            
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(groups = {"wso2.esb"},priority = 2, 
    		dependsOnMethods = {"testInsertFileMandatoryParams","testInsertCommentMandatoryParams" }, 
    	 description = "Google Drive {getCommentByID} method {mandatory parameters} Integration Tests")
    public void testgetCommentByIDMandatoryParams() throws Exception {
    
        final String methodName = "getCommentByID";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.getcommentbyid\">\n" + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "			<urn:fileId>" + fileId + "</urn:fileId>\n"
                        + "			<urn:commentId>"+ commentId +"</urn:commentId>\n"
                        + "		</root>\n"
                        + "   </soapenv:Body>\n" + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("getCommentResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(groups = {"wso2.esb"},priority = 2, 
    		dependsOnMethods = {"testInsertFileMandatoryParams","testInsertCommentMandatoryParams" },
    		description = "Google Drive {getCommentByID} method {optional parameters} Integration Tests")
    public void testgetCommentByIDOptionalParams() throws Exception {
    
        final String methodName = "getCommentByID";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.getcommentbyid\">\n" + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" + "		<root>\n" + "   		<urn:useServiceAccount>"
                        + googleDriveConnectorProperties.get("useServiceAccount")
                        + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>"
                        + googleDriveConnectorProperties.get("serviceAccountEmail")
                        + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>"
                        + googleDriveConnectorProperties.get("clientId")
                        + "</urn:clientId>\n"
                        + "			<urn:clientSecret>"
                        + googleDriveConnectorProperties.get("clientSecret")
                        + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>"
                        + googleDriveConnectorProperties.get("refreshToken")
                        + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>"
                        + googleDriveConnectorProperties.get("accessToken")
                        + "</urn:accessToken>\n"
                        + "			<urn:fileId>" + fileId + "</urn:fileId>\n"
                        + "			<urn:commentId>" + commentId + "</urn:commentId>\n"
                        + "			<urn:includeDeleted>true</urn:includeDeleted>\n" + "		</root>\n" + "   </soapenv:Body>\n" + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        }else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("getCommentResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(expectedExceptions = AxisFault.class,priority = 2, 
    		dependsOnMethods = {"testInsertFileMandatoryParams","testInsertCommentMandatoryParams" },
    		groups = {"wso2.esb"}, description = "Google Drive {getCommentByID} method {negative scenario} Integration Tests")
    public void testgetCommentByIDNegativeScenario() throws Exception {
    
        final String methodName = "getCommentByID";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.getcommentbyid\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "         <urn:fileId></urn:fileId>\n"
                        + "         <urn:commentId>AAAAAH5rOW0</urn:commentId>\n"
                        + "			<urn:includeDeleted>true</urn:includeDeleted>\n" 
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true);
           
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }
    
    @Test(groups = {"wso2.esb"},priority = 2, 
    		dependsOnMethods = {"testInsertFileMandatoryParams","testInsertCommentMandatoryParams", 
            "testgetCommentByIDMandatoryParams","testgetCommentByIDOptionalParams" },
    		description = "Google Drive {deleteComment} method {mandatory parameters} Integration Tests")
    public void testdeleteCommentMandatoryParams() throws Exception {
    
        final String methodName = "deleteComment";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.deletecomment\">\n" + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "         <urn:fileId>" + fileId + "</urn:fileId>\n"
                        + "         <urn:commentId>"+ commentId +"</urn:commentId>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("deleteCommentResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(expectedExceptions = AxisFault.class,priority = 2,
    		groups = {"wso2.esb"},
    		dependsOnMethods = {"testInsertFileMandatoryParams","testInsertCommentMandatoryParams",
                                "testgetCommentByIDMandatoryParams","testgetCommentByIDOptionalParams"},
    		description = "Google Drive {deleteComment} method {negative scenario} Integration Tests")
    public void testdeleteCommentNegativeScenario() throws Exception {
    
        final String methodName = "deleteComment";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.deletecomment\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "         <urn:fileId>dassdafa</urn:fileId>\n"
                        + "         <urn:commentId>AAAAAH5rdfsfdsOW0</urn:commentId>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true);
            
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(groups = {"wso2.esb"},priority = 2, 
    		dependsOnMethods = {"testInsertFileMandatoryParams" }, description = "Google Drive {trashFile} method {mandatory parameters} Integration Tests")
    public void testTrashFileMandatoryParams() throws Exception {
    
        final String methodName = "trashFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.trashfile\">\n" + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n" 
                        + "			<urn:fileId>" + fileId + "</urn:fileId>\n" 
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("trashedFileResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }
    
    @Test(expectedExceptions = AxisFault.class,priority = 2,
    		groups = {"wso2.esb"},dependsOnMethods = {"testInsertFileMandatoryParams" }, description = "Google Drive {trashFile} method {negative scenario} Integration Tests")
    public void testTrashFileNegativeScenario() throws Exception {
    
        final String methodName = "trashFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.trashfile\">\n" + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n" 
                        + "			<urn:fileId>dsfd</urn:fileId>\n" 
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true);
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(groups = {"wso2.esb"}, priority = 2, dependsOnMethods = {"testInsertFileWithFolderInsert"}, 
    		description = "Google Drive {ListChildren} method {mandatory parameters} Integration Tests")
    public void testListChildrenMandatoryParams() throws Exception {
    
        final String methodName = "listChildren";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.listchildren\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"                    
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n" 
                        + "			<urn:folderId>"+ folderId + "</urn:folderId>\n" 
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("listChildrenResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(expectedExceptions = AxisFault.class,priority = 2, dependsOnMethods = {"testInsertFileWithFolderInsert"},
    		groups = {"wso2.esb"}, description = "Google Drive {ListChildren} method {negative scenario} Integration Tests")
    public void testListChildrenNegativeScenario() throws Exception {
    
        final String methodName = "listChildren";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.listchildren\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n" 
                        + "			<urn:folderId>jkdlsajfl</urn:folderId>\n" 
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true);
            
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(groups = {"wso2.esb"},priority = 2, dependsOnMethods={"testInsertFileWithFolderInsert"}, 
    		description = "Google Drive {listChildren} method {optional parameters} Integration Tests")
    public void testListChildrenWithOptionalParams() throws Exception {
    
        final String methodName = "listChildren";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.listchildren\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "			<urn:folderId>"+ folderId + "</urn:folderId>\n"
                        + "			<urn:pageToken></urn:pageToken>\n"
                        + "			<urn:maxResults>5</urn:maxResults>\n" 
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("listChildrenResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(groups = {"wso2.esb"},priority = 2, 
    		dependsOnMethods = {"testInsertFileMandatoryParams","testTrashFileMandatoryParams" },
    		description = "Google Drive {untrashFile} method {Positive Scenario} Integration Tests")
    public void testUntrashFileMandatoryParams() throws Exception {
    
        final String methodName = "untrashFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.untrashfile\">\n" + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n" 
                        + "			<urn:fileId>" + fileId + "</urn:fileId>\n" 
                        + "		</root>\n" + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("untrashedFileResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(expectedExceptions = AxisFault.class,groups = {"wso2.esb"},priority = 2,
    		dependsOnMethods = {"testInsertFileMandatoryParams","testTrashFileMandatoryParams" }, 
    	description = "Google Drive {untrashFile} method {Negative Scenario} Integration Tests")
    public void testUntrashFileNegativeScenario() throws Exception {
    
        final String methodName = "untrashFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.untrashfile\">\n" + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n" 
                        + "			<urn:fileId>oDmU4cchC2XV8_3ngn0YhWSfdsKt5NYjQ_YR9mEdmIUKeY</urn:fileId>\n" 
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true);
            
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(groups = {"wso2.esb"},priority = 2, 
    		description = "Google Drive {listChangesForUser} method {mandatory parameters} Integration Tests")
    public void testListChangesForUserMandatoryParams() throws Exception {
    
        final String methodName = "listChangesForUser";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.listchangesforuser\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n" 
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("listChangesForUserResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }
    
    @Test(groups = {"wso2.esb"},priority = 2, 
    		description = "Google Drive {listChangesForUser} method {optional parameters} Integration Tests")
    public void testListChangesForUserWithOptionalParams() throws Exception {
    
        final String methodName = "listChangesForUser";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.listchangesforuser\">\n" + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "			<urn:includeDeleted>true</urn:includeDeleted>\n"
                        + "			<urn:pageToken></urn:pageToken>\n"
                        + "			<urn:startChangeId></urn:startChangeId>\n"
                        + "         <urn:includeSubscribed>true</urn:includeSubscribed>\n"
                        + "			<urn:maxResults>10</urn:maxResults>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("listChangesForUserResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }
    
    @Test(expectedExceptions = AxisFault.class, priority = 2,
    		groups = {"wso2.esb"}, description = "Google Drive {listChangesForUser} method {invalid parameters} Integration Tests")
    public void testListChangesForUserNegativeScenario() throws Exception {
    
        final String methodName = "listChangesForUser";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.listchangesforuser\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "1" + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>dasd" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>asdasd" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "		</root>\n"
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true);  
           
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
    }
    
    @Test(groups = {"wso2.esb"},priority = 2, 
    		dependsOnMethods = {"testInsertFileMandatoryParams","testInsertFileWithFolderInsert" }, 
    		description = "Google Drive {insertFileToFolder} method {mandatory parameters} Integration Tests")
    public void testInsertFileToFolderMandatoryParams() throws Exception {
    
        final String methodName = "insertFileToFolder";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.insertfiletofolder\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId")  + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "			<urn:fileId>" + fileId + "</urn:fileId>\n"
                        + "			<urn:folderId>" + folderId + "</urn:folderId>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("insertFileToFolderResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(expectedExceptions = AxisFault.class, priority = 2,
    		groups = {"wso2.esb"},
    		dependsOnMethods = {"testInsertFileMandatoryParams","testInsertFileWithFolderInsert" }, 
    		description = "Google Drive {insertFileToFolder} method {NegativeScenario} Integration Tests")
    public void testInsertFileToFolderNegativeScenario() throws Exception {
    
        final String methodName = "insertFileToFolder";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.insertfiletofolder\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "         <urn:fileId>1oDmU4cchC2XV8_3ngn0YhfdjklsWSKt5NYjQ_YR9mEdmIUKeY</urn:fileId>\n"
                        + "         <urn:folderId>0B2Cr40fdfdvxAph5ZXNrYXBPdDl2UFE</urn:folderId>\n" 
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true);  
            
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(groups = {"wso2.esb"},priority = 2, 
            dependsOnMethods = {"testInsertFileMandatoryParams"}, 
            description = "Google Drive {insertPermissionToFile} method {mandatory parameters} Integration Tests")
    public void testInsertPermissionToFileMandatoryParams() throws Exception {
    
        final String methodName = "insertPermissionToFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.insertpermissiontofile\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "     <root>\n" 
                        + "         <urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "         <urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "         <urn:clientId>" + googleDriveConnectorProperties.get("clientId")  + "</urn:clientId>\n"
                        + "         <urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "         <urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "         <urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "         <urn:fileId>" + fileId + "</urn:fileId>\n"
                        + "         <urn:requestBody>\n"
                        + "             <role>writer</role>\n"
                        + "             <type>user</type>\n"
                        + "             <value>wso2connector.abdera@gmail.com</value>\n"
                        + "         </urn:requestBody>\n"
                        + "     </root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
            Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
            attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("insertPermissionResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    @Test(groups = {"wso2.esb"},priority = 2, 
            dependsOnMethods = {"testInsertFileMandatoryParams"}, 
            description = "Google Drive {insertPermissionToFile} method {optional parameters} Integration Tests")
    public void testInsertPermissionToFileOptionalParams() throws Exception {
    
        final String methodName = "insertPermissionToFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.insertpermissiontofile\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "     <root>\n" 
                        + "         <urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "         <urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "         <urn:clientId>" + googleDriveConnectorProperties.get("clientId")  + "</urn:clientId>\n"
                        + "         <urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "         <urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "         <urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "         <urn:fileId>" + fileId + "</urn:fileId>\n"
                        + "         <urn:emailMessage>Hi, I'm sharing this file with you</urn:emailMessage>\n"
                        + "         <urn:requestBody>\n"
                        + "             <role>writer</role>\n"
                        + "             <type>user</type>\n"
                        + "             <value>wso2connector.abdera@gmail.com</value>\n"
                        + "         </urn:requestBody>\n"
                        + "         <urn:additionalRoles>\n"
                        + "             <role>commenter</role>\n"
                        + "         </urn:additionalRoles>\n"
                        + "     </root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
            Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
            attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("insertPermissionResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(groups = {"wso2.esb"},priority = 2, expectedExceptions = AxisFault.class,
            dependsOnMethods = {"testInsertFileMandatoryParams"}, 
            description = "Google Drive {insertPermissionToFile} method {negative scenario} Integration Tests")
    public void testInsertPermissionToFileNegativeScenario() throws Exception {
    
        final String methodName = "insertPermissionToFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.insertpermissiontofile\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "     <root>\n" 
                        + "         <urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "         <urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "         <urn:clientId>" + googleDriveConnectorProperties.get("clientId")  + "</urn:clientId>\n"
                        + "         <urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "         <urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "         <urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "         <urn:fileId>sfsfwsga</urn:fileId>\n"
                        + "     </root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
            Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
            attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    @Test(groups = {"wso2.esb"},priority = 2, 
            dependsOnMethods = {"testInsertFileMandatoryParams"}, 
            description = "Google Drive {listFilePermissions} method {mandatory parameters} Integration Tests")
    public void testListFilePermissionsMandatoryParams() throws Exception {
    
        final String methodName = "listFilePermissions";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.listfilepermissions\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "     <root>\n" 
                        + "         <urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "         <urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "         <urn:clientId>" + googleDriveConnectorProperties.get("clientId")  + "</urn:clientId>\n"
                        + "         <urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "         <urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "         <urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "         <urn:fileId>" + fileId + "</urn:fileId>\n"
                        + "     </root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
            Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
            attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("getPermissionsResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    @Test(groups = {"wso2.esb"},priority = 2, expectedExceptions = AxisFault.class,
            dependsOnMethods = {"testInsertFileMandatoryParams"}, 
            description = "Google Drive {lsitFilePermissions} method {negative scenario} Integration Tests")
    public void testListFilePermissionsNegativeScenario() throws Exception {
    
        final String methodName = "listFilePermissions";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.listfilepermissions\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "     <root>\n" 
                        + "         <urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "         <urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "         <urn:clientId>" + googleDriveConnectorProperties.get("clientId")  + "</urn:clientId>\n"
                        + "         <urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "         <urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "         <urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "         <urn:fileId>jkldald</urn:fileId>\n"
                        + "     </root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
            Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
            attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    @Test(groups = {"wso2.esb"},priority = 2, 
    		dependsOnMethods = {"testInsertFileMandatoryParams" }, description = "Google Drive {patchFile} method {mandatory parameters} Integration Tests")
    public void testPatchFileMandatoryParams() throws Exception {
    
        final String methodName = "patchFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.patchfile\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail")  + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>"  + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken")  + "</urn:accessToken>\n" 
                        + "			<urn:fileId>" + fileId + "</urn:fileId>\n" 
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("patchFileResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(groups = {"wso2.esb"},priority = 2, 
    		dependsOnMethods = {"testInsertFileMandatoryParams" }, description = "Google Drive {patchFile} method {optional parameters} Integration Tests")
    public void testPatchFileWithOptionalParams() throws Exception {
    
        final String methodName = "patchFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.patchfile\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "			<urn:fileId>" + fileId + "</urn:fileId>\n"
                        + "			<urn:convert>false</urn:convert>\n"
                        + "			<urn:newRevision>true</urn:newRevision>\n"
                        + "			<urn:ocr>false</urn:ocr>\n"
                        + "			<urn:ocrLanguage>en</urn:ocrLanguage>\n"
                        + "			<urn:pinned>false</urn:pinned>\n"
                        + "			<urn:setModifiedDate>false</urn:setModifiedDate>\n"
                        + "			<urn:timedTextLanguage>English</urn:timedTextLanguage>\n"
                        + "			<urn:timedTextTrackName>English</urn:timedTextTrackName>\n"
                        + "			<urn:updateViewedDate>false</urn:updateViewedDate>\n"
                        + "			<urn:useContentAsIndexableText>false</urn:useContentAsIndexableText>\n" 
                        + "         <urn:properties>\n"
                        + "             <property>\n"
                        + "                 <key>test</key>\n"
                        + "                 <kind>drive#property</kind>\n"
                        + "                 <value>Test</value>\n"
                        + "             </property>\n"
                        + "             <property>\n"
                        + "                 <key>another</key>\n"
                        + "                 <kind>drive#property</kind>\n"
                        + "                 <value>another</value>\n"
                        + "             </property>\n"
                        + "         </urn:properties>\n"
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("patchFileResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(expectedExceptions = AxisFault.class,priority = 2,
    		groups = {"wso2.esb"},dependsOnMethods = {"testInsertFileMandatoryParams" }, description = "Google Drive {patchFile} method {NegativeScenario} Integration Tests")
    public void testPatchFileNegativeScenario() throws Exception {
    
        final String methodName = "patchFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.patchfile\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n" 
                        + "			<urn:fileId>dfadfasd</urn:fileId>\n" 
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true);
            
            
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(groups = {"wso2.esb"},priority = 2, 
    		dependsOnMethods = {"testInsertFileMandatoryParams" }, description = "Google Drive {copyFile} method {Mandatory Parameters} Integration Tests")
    public void testCopyFileMandatoryParams() throws Exception {
    
        final String methodName = "copyFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.copyfile\">\n" + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n" 
                        + "			<urn:fileId>" + fileId + "</urn:fileId>\n" 
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("copiedFileResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(groups = {"wso2.esb"},priority = 2, 
    		dependsOnMethods = {"testInsertFileMandatoryParams" }, description = "Google Drive {copyFile} method {Optional Parameters} Integration Tests")
    public void testCopyFileWithOptionalParams() throws Exception {
    
        final String methodName = "copyFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.copyfile\">\n" + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n"
                        + "			<urn:fileId>" + fileId + "</urn:fileId>\n"
                        + "			<urn:convert>true</urn:convert>\n"
                        + "			<urn:ocr>false</urn:ocr>\n"
                        + "			<urn:ocrLanguage></urn:ocrLanguage>\n"
                        + "			<urn:pinned>true</urn:pinned>\n"
                        + "         <urn:fileResource>\n"
                        + "             <description>Copied File Description</description>\n"
                        + "             <title>Copied File</title>\n"
                        + "             <writersCanShare>true</writersCanShare>\n"
                        + "         </urn:fileResource>\n"
                        + "         <urn:labels>\n"
                        + "             <hidden>true</hidden>\n"
                        + "             <restricted>false</restricted>\n"
                        + "             <starred>false</starred>\n"
                        + "         </urn:labels>\n"
                        + "         <urn:properties>\n"
                        + "             <property>\n"
                        + "                 <key>test</key>\n"
                        + "                 <kind>drive#property</kind>\n"
                        + "                 <value>Test</value>\n"
                        + "             </property>\n"

                        + "         </urn:properties>\n"
                        + "		</root>\n"
                        + "   </soapenv:Body>\n"
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("copiedFileResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(expectedExceptions = AxisFault.class,priority = 2,
    		dependsOnMethods = {"testInsertFileMandatoryParams" }, groups = {"wso2.esb","googledrive.beforeDelete"}, description = "Google Drive {copyFile} method {Negative Scenario} Integration Tests")
    public void testCopyFileNegativeScenario() throws Exception {
    
        final String methodName = "copyFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.copyfile\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n" 
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true);
            
            
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(description = "Google Drive {deleteFile} method {mandatory parameters} Integration Tests", 
            priority = 3)
    public void testDeleteFileMandatoryParams() throws Exception {
    
        final String methodName = "deleteFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.deletefile\">\n" 
                		+ "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n" 
                        + "			<urn:fileId>" + fileId + "</urn:fileId>\n" 
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
            MessageContext responseMsgCtx = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            
            Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("deleteFileResult"));
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
    @Test(expectedExceptions = AxisFault.class, 
    		description = "Google Drive {deleteFile} method {negative scenario} Integration Tests", priority = 3)
    public void testDeleteFileNegativeScenario() throws Exception {
    
        final String methodName = "deleteFile";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.deletefile\">\n" 
                        + "   <soapenv:Header/>\n"
                        + "   <soapenv:Body>\n" 
                        + "		<root>\n" 
                        + "   		<urn:useServiceAccount>" + googleDriveConnectorProperties.get("useServiceAccount") + "</urn:useServiceAccount>\n"
                        + "   		<urn:serviceAccountEmail>" + googleDriveConnectorProperties.get("serviceAccountEmail") + "</urn:serviceAccountEmail>\n"
                        + "			<urn:clientId>" + googleDriveConnectorProperties.get("clientId") + "</urn:clientId>\n"
                        + "			<urn:clientSecret>" + googleDriveConnectorProperties.get("clientSecret") + "</urn:clientSecret>\n"
                        + "			<urn:refreshToken>" + googleDriveConnectorProperties.get("refreshToken") + "</urn:refreshToken>\n"
                        + "			<urn:accessToken>" + googleDriveConnectorProperties.get("accessToken") + "</urn:accessToken>\n" 
                        + "			<urn:fileId>jklijkol</urn:fileId>\n" 
                        + "		</root>\n" 
                        + "   </soapenv:Body>\n" 
                        + "</soapenv:Envelope>";
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + File.separator + File.separator + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_" + methodName + ".xml")));
        OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);
        
        OperationClient mepClient = null;
        if (Boolean.valueOf(googleDriveConnectorProperties.get("useServiceAccount").toString()) == true) {
        	Map<String, DataHandler> attachmentMap = new HashMap<String,DataHandler>();
        	attachmentMap.put("certificate", new DataHandler(
                            new FileDataSource(new File(ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION 
                                    + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                                    + File.separator + "googledrive_certificate.p12"))));
            mepClient = ConnectorIntegrationUtil.buildMEPClientWithAttachment(new EndpointReference(
                        getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope, attachmentMap);
        } else {
            mepClient = ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
                    getProxyServiceURL(CONNECTOR_NAME + "_" + methodName)), requestEnvelope);
        }
        
        
        try {
            mepClient.execute(true); 
        } finally {
            proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
        }
        
    }
    
}
