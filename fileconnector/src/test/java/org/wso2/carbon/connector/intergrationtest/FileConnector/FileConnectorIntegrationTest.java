/**
 * Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.intergrationtest.FileConnector;

import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Arrays;

import javax.activation.DataHandler;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
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
import org.wso2.carbon.connector.intergrationtest.common.ConnectorIntegrationUtil;
import org.wso2.carbon.esb.ESBIntegrationTest;
import org.wso2.carbon.mediation.library.stub.MediationLibraryAdminServiceStub;
import org.wso2.carbon.mediation.library.stub.upload.MediationLibraryUploaderStub;
import org.wso2.carbon.proxyadmin.stub.ProxyServiceAdminProxyAdminException;

public class FileConnectorIntegrationTest extends ESBIntegrationTest {

	private static final String CONNECTOR_NAME = "FileConnector";

	private static final String CONNECTOR_NAME_ZIP = "fileconnector";

	private static final float SLEEP_TIMER_PROGRESSION_FACTOR = 0.5f;

	private MediationLibraryUploaderStub mediationLibUploadStub = null;

	private MediationLibraryAdminServiceStub adminServiceStub = null;

	private ProxyServiceAdminClient proxyAdmin;

	private String repoLocation = null;

	private String fileConnectorFileName = CONNECTOR_NAME_ZIP + ".zip";

	private static String fileId;

	private static String commentId;

	private static String folderId;

	@BeforeClass(alwaysRun = false)
	public void setEnvironment() throws Exception {

		super.init();

		ConfigurationContextProvider configurationContextProvider =
		                                                            ConfigurationContextProvider.getInstance();
		ConfigurationContext cc = configurationContextProvider.getConfigurationContext();
		mediationLibUploadStub =
		                         new MediationLibraryUploaderStub(cc, esbServer.getBackEndUrl() +
		                                                              "MediationLibraryUploader");
		AuthenticateStub.authenticateStub("admin", "admin", mediationLibUploadStub);

		adminServiceStub =
		                   new MediationLibraryAdminServiceStub(cc, esbServer.getBackEndUrl() +
		                                                            "MediationLibraryAdminService");

		AuthenticateStub.authenticateStub("admin", "admin", adminServiceStub);

		if (System.getProperty("os.name").toLowerCase().contains("windows")) {

			repoLocation = System.getProperty("connector_repo").replace("/", "\\");
		} else {

			repoLocation = System.getProperty("connector_repo").replace("/", "/");
		}
		log.info("REPOLOCATION:" + repoLocation);

		proxyAdmin =
		             new ProxyServiceAdminClient(esbServer.getBackEndUrl(),
		                                         esbServer.getSessionCookie());

		ConnectorIntegrationUtil.uploadConnector(repoLocation, mediationLibUploadStub,
		                                         fileConnectorFileName);
		byte maxAttempts = 3;
		int sleepTimer = 30000;
		for (byte attemptCount = 0; attemptCount < maxAttempts; attemptCount++) {
			Thread.sleep(sleepTimer);
			String[] libraries = adminServiceStub.getAllLibraries();
			if (Arrays.asList(libraries).contains("{org.wso2.carbon.connector}" +
			                                              CONNECTOR_NAME_ZIP)) {
				break;
			} else {
				sleepTimer *= SLEEP_TIMER_PROGRESSION_FACTOR;
			}

		}

		adminServiceStub.updateStatus("{org.wso2.carbon.connector}" + CONNECTOR_NAME_ZIP,
		                              CONNECTOR_NAME_ZIP, "org.wso2.carbon.connector", "enabled");

	}

	@Override
	protected void cleanup() {

		axis2Client.destroy();
	}

	@Test(groups = { "wso2.esb" }, priority = 2, description = "File creation integration Tests")
	public void testFileCreation() throws RemoteException, ProxyServiceAdminProxyAdminException {

		final String methodName = "create";
		final String omString =
		                        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.insertcomment\">\n"
		                                + "   <soapenv:Header/>\n"
		                                + "   <soapenv:Body>\n"
		                                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
		                                + "   </soapenv:Body>\n"
		                                + "</soapenv:Envelope>";
		try {
			proxyAdmin.addProxyService(new DataHandler(
			                                           new URL(
			                                                   "file:" +
			                                                           File.separator +
			                                                           File.separator +
			                                                           ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
			                                                           ConnectorIntegrationUtil.ESB_CONFIG_LOCATION +
			                                                           File.separator + "proxies" +
			                                                           File.separator +
			                                                           CONNECTOR_NAME +
			                                                           File.separator +
			                                                           CONNECTOR_NAME + "_" +
			                                                           methodName + ".xml")));
			OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);

			OperationClient mepClient = null;

			mepClient =
			            ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
			                                                                          getProxyServiceURL(CONNECTOR_NAME +
			                                                                                             "_" +
			                                                                                             methodName)),
			                                                    requestEnvelope);

			mepClient.execute(true);
			MessageContext responseMsgCtx =
			                                mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);

			Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("success"));
		} catch (Exception e) {

		} finally {
			proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
		}
	}

	@Test(groups = { "wso2.esb" }, priority = 2, description = "File deletion integration Tests")
	public void testFileDeletion() throws RemoteException, ProxyServiceAdminProxyAdminException {

		final String methodName = "delete";
		final String omString =
		                        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.insertcomment\">\n"
		                                + "   <soapenv:Header/>\n"
		                                + "   <soapenv:Body>\n"
		                                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
		                                + "   </soapenv:Body>\n"
		                                + "</soapenv:Envelope>";
		try {
			proxyAdmin.addProxyService(new DataHandler(
			                                           new URL(
			                                                   "file:" +
			                                                           File.separator +
			                                                           File.separator +
			                                                           ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
			                                                           ConnectorIntegrationUtil.ESB_CONFIG_LOCATION +
			                                                           File.separator + "proxies" +
			                                                           File.separator +
			                                                           CONNECTOR_NAME +
			                                                           File.separator +
			                                                           CONNECTOR_NAME + "_" +
			                                                           methodName + ".xml")));
			OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);

			OperationClient mepClient = null;

			mepClient =
			            ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
			                                                                          getProxyServiceURL(CONNECTOR_NAME +
			                                                                                             "_" +
			                                                                                             methodName)),
			                                                    requestEnvelope);

			mepClient.execute(true);
			MessageContext responseMsgCtx =
			                                mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);

			Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("success"));
		} catch (Exception e) {

		} finally {
			proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
		}

	}

	@Test(groups = { "wso2.esb" }, priority = 2, description = "File rename integration Tests")
	public void testFileRename() throws RemoteException, ProxyServiceAdminProxyAdminException {

		final String methodName = "rename";
		final String omString =
		                        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.insertcomment\">\n"
		                                + "   <soapenv:Header/>\n"
		                                + "   <soapenv:Body>\n"
		                                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
		                                + "   </soapenv:Body>\n"
		                                + "</soapenv:Envelope>";
		try {
			proxyAdmin.addProxyService(new DataHandler(
			                                           new URL(
			                                                   "file:" +
			                                                           File.separator +
			                                                           File.separator +
			                                                           ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
			                                                           ConnectorIntegrationUtil.ESB_CONFIG_LOCATION +
			                                                           File.separator + "proxies" +
			                                                           File.separator +
			                                                           CONNECTOR_NAME +
			                                                           File.separator +
			                                                           CONNECTOR_NAME + "_" +
			                                                           methodName + ".xml")));
			OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);

			OperationClient mepClient = null;

			mepClient =
			            ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
			                                                                          getProxyServiceURL(CONNECTOR_NAME +
			                                                                                             "_" +
			                                                                                             methodName)),
			                                                    requestEnvelope);

			mepClient.execute(true);
			MessageContext responseMsgCtx =
			                                mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);

			Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("success"));

		} catch (Exception e) {

		} finally {
			proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
		}

	}

	@Test(groups = { "wso2.esb" }, priority = 2, description = "File copy integration Tests")
	public void testFileCopy() throws RemoteException, ProxyServiceAdminProxyAdminException {

		final String methodName = "copy";
		final String omString =
		                        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.insertcomment\">\n"
		                                + "   <soapenv:Header/>\n"
		                                + "   <soapenv:Body>\n"
		                                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
		                                + "   </soapenv:Body>\n"
		                                + "</soapenv:Envelope>";
		try {
			proxyAdmin.addProxyService(new DataHandler(
			                                           new URL(
			                                                   "file:" +
			                                                           File.separator +
			                                                           File.separator +
			                                                           ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
			                                                           ConnectorIntegrationUtil.ESB_CONFIG_LOCATION +
			                                                           File.separator + "proxies" +
			                                                           File.separator +
			                                                           CONNECTOR_NAME +
			                                                           File.separator +
			                                                           CONNECTOR_NAME + "_" +
			                                                           methodName + ".xml")));
			OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);

			OperationClient mepClient = null;

			mepClient =
			            ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
			                                                                          getProxyServiceURL(CONNECTOR_NAME +
			                                                                                             "_" +
			                                                                                             methodName)),
			                                                    requestEnvelope);

			mepClient.execute(true);
			MessageContext responseMsgCtx =
			                                mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);

			Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("success"));
		} catch (Exception e) {

		} finally {
			proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
		}

	}

	@Test(groups = { "wso2.esb" }, priority = 2, description = "File large copy integration Tests")
	public void testFileCopyLarge() throws RemoteException, ProxyServiceAdminProxyAdminException {
		final String methodName = "copylarge";
		final String omString =
		                        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.insertcomment\">\n"
		                                + "   <soapenv:Header/>\n"
		                                + "   <soapenv:Body>\n"
		                                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
		                                + "   </soapenv:Body>\n"
		                                + "</soapenv:Envelope>";
		try {
			proxyAdmin.addProxyService(new DataHandler(
			                                           new URL(
			                                                   "file:" +
			                                                           File.separator +
			                                                           File.separator +
			                                                           ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
			                                                           ConnectorIntegrationUtil.ESB_CONFIG_LOCATION +
			                                                           File.separator + "proxies" +
			                                                           File.separator +
			                                                           CONNECTOR_NAME +
			                                                           File.separator +
			                                                           CONNECTOR_NAME + "_" +
			                                                           methodName + ".xml")));
			OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);

			OperationClient mepClient = null;

			mepClient =
			            ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
			                                                                          getProxyServiceURL(CONNECTOR_NAME +
			                                                                                             "_" +
			                                                                                             methodName)),
			                                                    requestEnvelope);

			mepClient.execute(true);
			MessageContext responseMsgCtx =
			                                mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);

			Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("success"));
		} catch (Exception e) {

		} finally {
			proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
		}

	}

	@Test(groups = { "wso2.esb" }, priority = 2, description = "File search integration Tests")
	public void testFileSearch() throws RemoteException, ProxyServiceAdminProxyAdminException {
		final String methodName = "search";
		final String omString =
		                        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.insertcomment\">\n"
		                                + "   <soapenv:Header/>\n"
		                                + "   <soapenv:Body>\n"
		                                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
		                                + "   </soapenv:Body>\n"
		                                + "</soapenv:Envelope>";
		try {
			proxyAdmin.addProxyService(new DataHandler(
			                                           new URL(
			                                                   "file:" +
			                                                           File.separator +
			                                                           File.separator +
			                                                           ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
			                                                           ConnectorIntegrationUtil.ESB_CONFIG_LOCATION +
			                                                           File.separator + "proxies" +
			                                                           File.separator +
			                                                           CONNECTOR_NAME +
			                                                           File.separator +
			                                                           CONNECTOR_NAME + "_" +
			                                                           methodName + ".xml")));
			OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);

			OperationClient mepClient = null;

			mepClient =
			            ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
			                                                                          getProxyServiceURL(CONNECTOR_NAME +
			                                                                                             "_" +
			                                                                                             methodName)),
			                                                    requestEnvelope);

			mepClient.execute(true);
			MessageContext responseMsgCtx =
			                                mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);

			Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("success"));
		} catch (Exception e) {

		} finally {
			proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
		}

	}

	@Test(groups = { "wso2.esb" }, priority = 2, description = "File archive integration Tests")
	public void testFileArchive() throws RemoteException, ProxyServiceAdminProxyAdminException {

		final String methodName = "archive";
		final String omString =
		                        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.insertcomment\">\n"
		                                + "   <soapenv:Header/>\n"
		                                + "   <soapenv:Body>\n"
		                                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
		                                + "   </soapenv:Body>\n"
		                                + "</soapenv:Envelope>";
		try {
			proxyAdmin.addProxyService(new DataHandler(
			                                           new URL(
			                                                   "file:" +
			                                                           File.separator +
			                                                           File.separator +
			                                                           ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
			                                                           ConnectorIntegrationUtil.ESB_CONFIG_LOCATION +
			                                                           File.separator + "proxies" +
			                                                           File.separator +
			                                                           CONNECTOR_NAME +
			                                                           File.separator +
			                                                           CONNECTOR_NAME + "_" +
			                                                           methodName + ".xml")));
			OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);

			OperationClient mepClient = null;

			mepClient =
			            ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
			                                                                          getProxyServiceURL(CONNECTOR_NAME +
			                                                                                             "_" +
			                                                                                             methodName)),
			                                                    requestEnvelope);

			mepClient.execute(true);
			MessageContext responseMsgCtx =
			                                mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);

			Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("success"));
		} catch (Exception e) {

		} finally {
			proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
		}

	}

	@Test(groups = { "wso2.esb" }, priority = 2, description = "File append integration Tests")
	public void testFileAppend() throws RemoteException, ProxyServiceAdminProxyAdminException {

		final String methodName = "create";
		final String omString =
		                        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:wso2.connector.googledrive.insertcomment\">\n"
		                                + "   <soapenv:Header/>\n"
		                                + "   <soapenv:Body>\n"
		                                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
		                                + "   </soapenv:Body>\n"
		                                + "</soapenv:Envelope>";
		try {
			proxyAdmin.addProxyService(new DataHandler(
			                                           new URL(
			                                                   "file:" +
			                                                           File.separator +
			                                                           File.separator +
			                                                           ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION +
			                                                           ConnectorIntegrationUtil.ESB_CONFIG_LOCATION +
			                                                           File.separator + "proxies" +
			                                                           File.separator +
			                                                           CONNECTOR_NAME +
			                                                           File.separator +
			                                                           CONNECTOR_NAME + "_" +
			                                                           methodName + ".xml")));
			OMElement requestEnvelope = AXIOMUtil.stringToOM(omString);

			OperationClient mepClient = null;

			mepClient =
			            ConnectorIntegrationUtil.buildMEPClient(new EndpointReference(
			                                                                          getProxyServiceURL(CONNECTOR_NAME +
			                                                                                             "_" +
			                                                                                             methodName)),
			                                                    requestEnvelope);

			mepClient.execute(true);
			MessageContext responseMsgCtx =
			                                mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);

			Assert.assertTrue(responseMsgCtx.getEnvelope().getBody().toString().contains("success"));
		} catch (Exception e) {

		} finally {
			proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
		}

	}

}