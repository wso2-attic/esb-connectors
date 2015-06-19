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

package org.wso2.carbon.connector.integration.test.confluence;

import java.util.Iterator;
import java.util.Properties;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.context.ConfigurationContext;
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
import org.json.JSONObject;
import org.wso2.carbon.automation.api.clients.localentry.LocalEntriesAdminClient;

import java.net.URL;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

public class ConfluenceConnectorIntegrationTest extends ESBIntegrationTest {

	private static final String CONNECTOR_NAME = "confluence";
	private MediationLibraryUploaderStub mediationLibUploadStub = null;
	private MediationLibraryAdminServiceStub adminServiceStub = null;
	private ProxyServiceAdminClient proxyAdmin;
	private String repoLocation = null;
	private String connectorFileName = CONNECTOR_NAME + ".zip";
	private Properties connectorProperties = null;
	private String pathToProxiesDirectory = null;
	private String pathToRequestsDirectory = null;
	private LocalEntriesAdminClient localEntryAdmin = null;
	private String pathToAPIRequestDirectory = null;

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
			repoLocation = System.getProperty("connector_repo").replace("\\",
					"/");
		} else {
			repoLocation = System.getProperty("connector_repo").replace("/",
					"/");
		}

		proxyAdmin = new ProxyServiceAdminClient(esbServer.getBackEndUrl(),
				esbServer.getSessionCookie());

		ConnectorIntegrationUtil.uploadConnector(repoLocation,
				mediationLibUploadStub, connectorFileName);
		log.info("Sleeping for " + 30000 / 1000
				+ " seconds while waiting for synapse import");
		Thread.sleep(30000);

		adminServiceStub.updateStatus("{org.wso2.carbon.connectors}"
				+ CONNECTOR_NAME, CONNECTOR_NAME, "org.wso2.carbon.connectors",
				"enabled");

		connectorProperties = ConnectorIntegrationUtil
				.getConnectorConfigProperties(CONNECTOR_NAME);

		pathToProxiesDirectory = repoLocation
				+ connectorProperties.getProperty("proxyDirectoryRelativePath");
		pathToRequestsDirectory = repoLocation
				+ connectorProperties
						.getProperty("requestDirectoryRelativePath");
		pathToAPIRequestDirectory = repoLocation
				+ connectorProperties
						.getProperty("soapRequestDirectoryRelativePath");

		localEntryAdmin = new LocalEntriesAdminClient(
				esbServer.getBackEndUrl(), esbServer.getSessionCookie());

		final String configKeyFilePath = pathToProxiesDirectory
				+ "configKey.xml";
		OMElement localEntry = AXIOMUtil.stringToOM(String.format(
				ConnectorIntegrationUtil.getFileContent(configKeyFilePath),
				connectorProperties.getProperty("username"),
				connectorProperties.getProperty("password"),
				connectorProperties.getProperty("uri")));
		localEntryAdmin.addLocalEntry(localEntry);
		Thread.sleep(20000);

	}

	@Override
	protected void cleanup() {
		axis2Client.destroy();
	}

	/* confluence Administrator Methods */

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [init] method. Logging using an username, a password and uri Positive case")
	public void testLoginPositive() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "init.txt";
		String methodName = "init";
		String assertMethodName = "logout";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("username"),
				connectorProperties.getProperty("password"),
				connectorProperties.getProperty("uri"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String token = omElement.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ assertMethodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String
					.format(apiSoapRequest, token);
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().getText(), "true", omElement.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [init] method. Logging using wrong username password combination")
	public void testLoginNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "init.txt";
		String methodName = "init";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("username"),
				connectorProperties.getProperty("invalidPassword"),
				connectorProperties.getProperty("uri"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertEquals(statusCode, 500);
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [exportSite] method")
	public void testExportSite() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "exportSitePositive.txt";
		String methodName = "exportSite";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString, "true");

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, "true");
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().getLocalName(), omElement
					.getFirstElement().getLocalName());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getClusterInformation] method positive")
	public void testgetClusterInformation() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getClusterInformation.txt";
		String methodName = "getClusterInformation";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString);

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String
					.format(apiSoapRequest, token);
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getClusterInformation] method positive")
	public void testgetClusterNodeStatuses() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getClusterNodeStatuses.txt";
		String methodName = "getClusterNodeStatuses";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString);

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String
					.format(apiSoapRequest, token);
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [isPluginEnabled] method positive")
	public void testisPluginEnabled() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "isPluginEnabled.txt";
		String methodName = "isPluginEnabled";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString, "plugin1");

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, "plugin1");
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().getLocalName(), omElement
					.getFirstElement().getLocalName());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [isPluginEnabled] method negative")
	public void testisPluginEnabledNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "isPluginEnabled_negative.txt";
		String methodName = "isPluginEnabled";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString);

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(
					omElement.getLocalName().equals("isPluginEnabledResponse"),
					omElement.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	/* Confluence Attachments management methods */

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [removeAttachment] method positive")
	public void testremoveAttachment() throws Exception {
		String jsonRequestFilePath = pathToRequestsDirectory
				+ "removeAttachment.txt";
		String methodName = "removeAttachment";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("pageID"),
				connectorProperties.getProperty("removeAttachment"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("pageID"),
					connectorProperties.getProperty("removeAttachment"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);
			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getLocalName(), "Fault");

			// Assert.assertTrue(
			// omElement.getLocalName().equals("removeAttachmentResponse"),
			// omElement.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [removeAttachment] method negative")
	public void testremoveAttachmentNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "removeAttachment_negative.txt";
		String methodName = "removeAttachment";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("pageID"),
				connectorProperties.getProperty("invalidAttachment"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("pageID"),
					connectorProperties.getProperty("invalidAttachment"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	/* confluence attachments retrieval methods */
	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getAttachment] method positive")
	public void testgetAttachment() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getAttachment.txt";
		String methodName = "getAttachment";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("pageID"),
				connectorProperties.getProperty("attachment"),
				connectorProperties.getProperty("versionNumber"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("pageID"),
					connectorProperties.getProperty("attachment"),
					connectorProperties.getProperty("versionNumber"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getAttachment] method negative")
	public void testgetAttachmentNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getAttachment.txt";
		String methodName = "getAttachment";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("pageID"),
				connectorProperties.getProperty("invalidAttachment"),
				connectorProperties.getProperty("versionNumber"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("pageID"),
					connectorProperties.getProperty("invalidAttachment"),
					connectorProperties.getProperty("versionNumber"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getAttachmentData] method positive")
	public void testgetAttachmentData() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getAttachment.txt";
		String methodName = "getAttachmentData";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("pageID"),
				connectorProperties.getProperty("attachment"),
				connectorProperties.getProperty("versionNumber"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);
			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("pageID"),
					connectorProperties.getProperty("attachment"),
					connectorProperties.getProperty("versionNumber"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().getLocalName(), omElement
					.getFirstElement().getLocalName());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getAttachmentData] method negative")
	public void testgetAttachmentDataNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getAttachment.txt";
		String methodName = "getAttachmentData";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("pageID"),
				connectorProperties.getProperty("invalidAttachment"),
				connectorProperties.getProperty("versionNumber"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);
			
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("pageID"),
					connectorProperties.getProperty("invalidAttachment"),
					connectorProperties.getProperty("versionNumber"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	/* Confluence Authentication Methods */
	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [logout] method positive")
	public void testLogout() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "logout.txt";
		String methodName = "logout";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString);

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			Assert.assertTrue(
					omElement.getLocalName().equals("logoutResponse"),
					omElement.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	/* Confluence Blog Methods */
	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getBlogEntries] method positive")
	public void testgetBlogEntries() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getBlogEntries.txt";
		String methodName = "getBlogEntries";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("spaceId"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("spaceId"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getBlogEntries] method negative")
	public void testgetBlogEntriesNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getBlogEntries.txt";
		String methodName = "getBlogEntries";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidSpaceId"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);
			
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidSpaceId"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getBlogEntry] method positive")
	public void testgetBlogEntry() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getBlogEntry.txt";
		String methodName = "getBlogEntry";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("blogPageId"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("blogPageId"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getBlogEntry] method negative")
	public void testgetBlogEntryNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getBlogEntry.txt";
		String methodName = "getBlogEntry";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidPageID"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidPageID"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	/* Confluence General methods */
	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getServerInfo] method positive")
	public void testgetServerInfo() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getServerInfo.txt";
		String methodName = "getServerInfo";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString);

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String
					.format(apiSoapRequest, token);
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	/* Confluence labels methods */
	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [addLabelByNameToSpace] method positive")
	public void testaddLabelByNameToSpace() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "addLabelByNameToSpace.txt";
		String methodName = "addLabelByNameToSpace";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("lable"),
				connectorProperties.getProperty("spaceId"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ "getLabelContentByName" + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("lable"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().getLocalName(),
					"getLabelContentByNameReturn");
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [addLabelByNameToSpace] method negative")
	public void testaddLabelByNameToSpaceNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "addLabelByNameToSpace.txt";
		String methodName = "addLabelByNameToSpace";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("lable"),
				connectorProperties.getProperty("invalidSpaceId"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("lable"),
					connectorProperties.getProperty("invalidSpaceId"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getLabelContentByName] method positive")
	public void testgetLabelContentByName() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getLabelContentByName.txt";
		String methodName = "getLabelContentByName";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("lable"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("lable"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getLabelContentByName] method negative")
	public void testgetLabelContentByNameNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getLabelContentByName.txt";
		String methodName = "getLabelContentByName";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidLable"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidLable"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getMostPopularLabels] method positive")
	public void testgetMostPopularLabels() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getMostPopularLabels.txt";
		String methodName = "getMostPopularLabels";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString, "10");

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, "10");
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getMostPopularLabels] method negative")
	public void testgetMostPopularLabelsNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getMostPopularLabels.txt";
		String methodName = "getMostPopularLabels";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString, "-10");

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, "-10");
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getRecentlyUsedLabels] method positive")
	public void testgetRecentlyUsedLabels() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getRecentlyUsedLabels.txt";
		String methodName = "getRecentlyUsedLabels";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString, "10");

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, "10");
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getRecentlyUsedLabels] method negative")
	public void testgetRecentlyUsedLabelsNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getRecentlyUsedLabels.txt";
		String methodName = "getRecentlyUsedLabels";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString, "-10");

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, "-10");
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getSpacesWithLabel] method positive")
	public void testgetSpacesWithLabel() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getSpacesWithLabel.txt";
		String methodName = "getSpacesWithLabel";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("lable"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("lable"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getSpacesWithLabel] method negative")
	public void testgetSpacesWithLabelNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getSpacesWithLabel.txt";
		String methodName = "getSpacesWithLabel";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString, "invalidLable");

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, "invalidLable");
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	/* Confluence Page Management */
	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [movePage] method positive")
	public void testmovePage() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "movePage.txt";
		String methodName = "movePage";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("movePageID"),
				connectorProperties.getProperty("moveTargetPageID"),
				connectorProperties.getProperty("pageManagementPosition"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(
					omElement.getLocalName().equals("movePageResponse"),
					omElement.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [movePage] method negative")
	public void testmovePageNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "movePage.txt";
		String methodName = "movePage";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidPageID"),
				connectorProperties.getProperty("moveTargetPageID"),
				connectorProperties.getProperty("pageManagementPosition"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidPageID"),
					connectorProperties.getProperty("moveTargetPageID"),
					connectorProperties.getProperty("pageManagementPosition"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [movePageToTopLevel] method positive")
	public void testmovePageToTopLevel() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "movePageToTopLevel.txt";
		String methodName = "movePageToTopLevel";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("movePageID2"),
				connectorProperties.getProperty("spaceId"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(
					omElement.getLocalName().equals(
							"movePageToTopLevelResponse"), omElement.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [movePageToTopLevel] method negative")
	public void testmovePageToTopLevelNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "movePageToTopLevel.txt";
		String methodName = "movePageToTopLevel";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidPageID"),
				connectorProperties.getProperty("spaceId"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidPageID"),
					connectorProperties.getProperty("spaceId"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [removePage] method positive")
	public void testremovePage() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "removePage.txt";
		String methodName = "removePage";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("removePageID"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("removePageID"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().getText(), "true");
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [removePage] method negative")
	public void testremovePageNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "removePage.txt";
		String methodName = "removePage";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidPageID"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidPageID"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	/* Confluence page permission */
	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getContentPermissionSets] method positive")
	public void testgetContentPermissionSets() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getContentPermissionSets.txt";
		String methodName = "getContentPermissionSets";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("pageID"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("pageID"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getContentPermissionSets] method negative")
	public void testgetContentPermissionSetsNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getContentPermissionSets.txt";
		String methodName = "getContentPermissionSets";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidPageID"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidPageID"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	/* confluence page retrieval */
	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getPages] method positive")
	public void testgetPages() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getPages.txt";
		String methodName = "getPages";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("spaceId"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("spaceId"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getPages] method negative")
	public void testgetPagesNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getPages.txt";
		String methodName = "getPages";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidSpaceId"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidSpaceId"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getPage] method positive")
	public void testgetPage() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getPage.txt";
		String methodName = "getPage";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("pageID"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("pageID"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getPage] method negative")
	public void testgetPageNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getPage.txt";
		String methodName = "getPage";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidPageID"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidPageID"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	/* Confluence Permissions */
	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [addAnonymousPermissionToSpace] method positive")
	public void testaddAnonymousPermissionToSpace() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "addAnonymousPermissionToSpace.txt";
		String methodName = "addAnonymousPermissionToSpace";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("permissionPermit"),
				connectorProperties.getProperty("spaceId"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(
					omElement.getLocalName().equals(
							"addAnonymousPermissionToSpaceResponse"),
					omElement.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [addAnonymousPermissionToSpace] method negative")
	public void testaddAnonymousPermissionToSpaceNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "addAnonymousPermissionToSpace.txt";
		String methodName = "addAnonymousPermissionToSpace";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("spaceId"),
				connectorProperties.getProperty("permissionInvalidPermit"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("spaceId"),
					connectorProperties.getProperty("permissionInvalidPermit"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [addPermissionToSpace] method positive")
	public void testaddPermissionToSpace() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "addPermissionToSpace.txt";
		String methodName = "addPermissionToSpace";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("permissionPermit"),
				connectorProperties.getProperty("entity"),
				connectorProperties.getProperty("spaceId"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(
					omElement.getLocalName().equals(
							"addPermissionToSpaceResponse"),
					omElement.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [addPermissionToSpace] method negative")
	public void testaddPermissionToSpaceNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "addPermissionToSpace.txt";
		String methodName = "addPermissionToSpace";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("permissionPermit"),
				connectorProperties.getProperty("entity"),
				connectorProperties.getProperty("invalidSpaceId"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("permissionPermit"),
					connectorProperties.getProperty("entity"),
					connectorProperties.getProperty("invalidSpaceId"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getPagePermissions] method positive")
	public void testgetPagePermissions() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getPagePermissions.txt";
		String methodName = "getPagePermissions";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("pageID"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("pageID"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getPagePermissions] method negative")
	public void testgetPagePermissionsNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getPagePermissions.txt";
		String methodName = "getPagePermissions";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidPageID"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidPageID"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getPermissions] method positive")
	public void testgetPermissions() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getPermissions.txt";
		String methodName = "getPermissions";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("spaceId"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("spaceId"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getPermissions] method negative")
	public void testgetPermissionsNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getPermissions.txt";
		String methodName = "getPermissions";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidSpaceId"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidSpaceId"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getPermissionsForUser] method positive")
	public void testgetPermissionsForUser() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getPermissionsForUser.txt";
		String methodName = "getPermissionsForUser";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("spaceId"),
				connectorProperties.getProperty("userName"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("spaceId"),
					connectorProperties.getProperty("userName"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getPermissionsForUser] method negative")
	public void testgetPermissionsForUserNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getPermissionsForUser.txt";
		String methodName = "getPermissionsForUser";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidSpaceId"),
				connectorProperties.getProperty("userName"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidSpaceId"),
					connectorProperties.getProperty("userName"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getSpaceLevelPermissions] method positive")
	public void testgetSpaceLevelPermissions() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getSpaceLevelPermissions.txt";
		String methodName = "getSpaceLevelPermissions";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString);

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String
					.format(apiSoapRequest, token);
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [removeAllPermissionsForGroup] method positive")
	public void testremoveAllPermissionsForGroup() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "removeAllPermissionsForGroup.txt";
		String methodName = "removeAllPermissionsForGroup";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("removePermissiongroup"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(
					omElement.getLocalName().equals(
							"removeAllPermissionsForGroupResponse"),
					omElement.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [removeAllPermissionsForGroup] method negative")
	public void testremoveAllPermissionsForGroupNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "removeAllPermissionsForGroup.txt";
		String methodName = "removeAllPermissionsForGroup";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidGroupName"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidGroupName"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [removeAnonymousPermissionFromSpace] method positive")
	public void testremoveAnonymousPermissionFromSpace() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "removeAnonymousPermissionFromSpace.txt";
		String methodName = "removeAnonymousPermissionFromSpace";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("permissionPermit"),
				connectorProperties.getProperty("removePermissionSpace"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(
					omElement.getLocalName().equals(
							"removeAnonymousPermissionFromSpaceResponse"),
					omElement.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [removeAnonymousPermissionFromSpace] method negative")
	public void testremoveAnonymousPermissionFromSpaceNegative()
			throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "removeAnonymousPermissionFromSpace.txt";
		String methodName = "removeAnonymousPermissionFromSpace";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("permissionInvalidPermit"),
				connectorProperties.getProperty("invalidSpaceId"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("permissionInvalidPermit"),
					connectorProperties.getProperty("invalidSpaceId"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [removeGlobalPermission] method positive")
	public void testremoveGlobalPermission() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "removeGlobalPermission.txt";
		String methodName = "removeGlobalPermission";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("permissionPermit"),
				connectorProperties.getProperty("removePermissiongroup"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(
					omElement.getLocalName().equals(
							"removeGlobalPermissionResponse"),
					omElement.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [removeGlobalPermission] method negative")
	public void testremoveGlobalPermissionNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "removeGlobalPermission.txt";
		String methodName = "removeGlobalPermission";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("permissionPermit"),
				connectorProperties.getProperty("invalidGroupName"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("permissionPermit"),
					connectorProperties.getProperty("invalidGroupName"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [removePermissionFromSpace] method positive")
	public void testremovePermissionFromSpace() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "removePermissionFromSpace.txt";
		String methodName = "removePermissionFromSpace";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("permissionPermit"),
				connectorProperties.getProperty("removePermissionEntity"),
				connectorProperties.getProperty("removePermissionSpace"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(
					omElement.getLocalName().equals(
							"removePermissionFromSpaceResponse"),
					omElement.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [removePermissionFromSpace] method negative")
	public void testremovePermissionFromSpaceNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "removePermissionFromSpace.txt";
		String methodName = "removePermissionFromSpace";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("permissionInvalidPermit"),
				connectorProperties.getProperty("removePermissionEntity"),
				connectorProperties.getProperty("invalidSpaceId"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("permissionInvalidPermit"),
					connectorProperties.getProperty("removePermissionEntity"),
					connectorProperties.getProperty("invalidSpaceId"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getSpace] method positive")
	public void testgetSpace() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getSpace.txt";
		String methodName = "getSpace";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("spaceId"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("spaceId"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getSpace] method negative")
	public void testgetSpaceNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getSpace.txt";
		String methodName = "getSpace";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidSpaceId"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidSpaceId"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [addGroup] method positive")
	public void testaddGroup() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "addGroup.txt";
		String methodName = "addGroup";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("addgroupGroupName"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ "hasGroup" + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String
					.format(apiSoapRequest, token, connectorProperties
							.getProperty("addgroupGroupName"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().getText(), "true");
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [addGroup] method negative")
	public void testaddGroupNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "addGroup.txt";
		String methodName = "addGroup";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("addGroupInvalidName"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("addGroupInvalidName"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [addUser] method positive with Mandotory Parameters")
	public void testaddUser() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "addUser.txt";
		String methodName = "addUser";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("addUserEmail"),
				connectorProperties.getProperty("addUserFullName"),
				connectorProperties.getProperty("addUserUserName"), "",
				connectorProperties.getProperty("addUserPassword"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ "getUser" + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("addUserUserName"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().getLocalName(), "getUserReturn");
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [addUser] method Negative with existing user")
	public void testaddUserNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "addUser.txt";
		String methodName = "addUser";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("addUserEmail"),
				connectorProperties.getProperty("addUserFullName"),
				connectorProperties.getProperty("addUserUserName"), "",
				connectorProperties.getProperty("addUserPassword"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("addUserEmail"),
					connectorProperties.getProperty("addUserFullName"),
					connectorProperties.getProperty("addUserUserName"), "",
					connectorProperties.getProperty("addUserPassword"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [addUser] method positive with optional parameters")
	public void testaddUserOptional() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "addUser.txt";
		String methodName = "addUser";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("addUserEmailOptional"),
				connectorProperties.getProperty("addUserFullNameOptional"),
				connectorProperties.getProperty("addUserUserNameOptional"),
				connectorProperties.getProperty("addUserURL"),
				connectorProperties.getProperty("addUserPassword"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ "getUser" + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("addUserUserNameOptional"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().getLocalName(), "getUserReturn");
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [addUserToGroup] method positive with Mandotory Parameters")
	public void testaddUserToGroup() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "addUserToGroup.txt";
		String methodName = "addUserToGroup";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("addUserUserName"),
				connectorProperties.getProperty("addgroupGroupName"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ "getUserGroups" + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("addUserUserName"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			boolean hasGroupName = false;
			Iterator<OMElement> iter = apiRespond.getFirstElement()
					.getFirstElement().getFirstElement().getChildElements();

			while (iter.hasNext()) {
				OMElement o = iter.next();
				if (o.getText().equals(
						connectorProperties.getProperty("addgroupGroupName")))
					hasGroupName = true;
			}

			Assert.assertTrue(hasGroupName);
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [addUserToGroup] method Negative with existing user")
	public void testaddUserToGroupNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "addUserToGroup.txt";
		String methodName = "addUserToGroup";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidUserName"),
				connectorProperties.getProperty("addgroupGroupName"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidUserName"),
					connectorProperties.getProperty("addgroupGroupName"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [changeMyPassword] method positive with Mandotory Parameters")
	public void testchangeMyPassword() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "changeMyPassword.txt";
		String methodName = "changeMyPassword";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("password"),
				connectorProperties.getProperty("password"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);

			Assert.assertEquals(resp.getFirstElement().getFirstElement()
					.getLocalName(), "loginResponse");
			;

		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [changeMyPassword] method Negative with existing user")
	public void testchangeMyPasswordNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "changeMyPassword.txt";
		String methodName = "changeMyPassword";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidPassword"),
				connectorProperties.getProperty("password"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidPassword"),
					connectorProperties.getProperty("password"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [changeUserPassword] method positive with Mandotory Parameters")
	public void testchangeUserPassword() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "changeUserPassword.txt";
		String methodName = "changeUserPassword";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("addUserUserName"),
				connectorProperties.getProperty("addUserPassword2"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);
			Assert.assertTrue(
					omElement.getLocalName().equals(
							"changeUserPasswordResponse"), omElement.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [changeUserPassword] method Negative with existing user")
	public void testchangeUserPasswordNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "changeUserPassword.txt";
		String methodName = "changeUserPassword";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidUserName"),
				connectorProperties.getProperty("addUserPassword2"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidUserName"),
					connectorProperties.getProperty("addUserPassword2"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [deactivateUser] method positive with Mandotory Parameters")
	public void testdeactivateUser() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "deactivateUser.txt";
		String methodName = "deactivateUser";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("addUserUserName"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("addUserUserName"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getLocalName(), "Fault");
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [deactivateUser] method Negative with existing user")
	public void testdeactivateUserNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "deactivateUser.txt";
		String methodName = "deactivateUser";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidUserName"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidUserName"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getActiveUsers] method positive with Mandotory Parameters")
	public void testgetActiveUsers() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getActiveUsers.txt";
		String methodName = "getActiveUsers";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString, "true");

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, "true");
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getActiveUsers] method Negative with existing user")
	public void testgetActiveUsersNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getActiveUsers.txt";
		String methodName = "getActiveUsers";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString, "invalidboolean");
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,"invalidboolean");
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getGroups] method positive with Mandotory Parameters")
	public void testgetGroups() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getGroups.txt";
		String methodName = "getGroups";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString);

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String
					.format(apiSoapRequest, token);
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getUser] method positive with Mandotory Parameters")
	public void testgetUser() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getUser.txt";
		String methodName = "getUser";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("userName"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("userName"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getUser] method Negative with existing user")
	public void testgetUserNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "getUser.txt";
		String methodName = "getUser";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidUserName"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidUserName"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getUserGroups] method positive with Mandotory Parameters")
	public void testgetUserGroups() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getUserGroups.txt";
		String methodName = "getUserGroups";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("userName"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("userName"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getUserGroups] method Negative with existing user")
	public void testgetUserGroupsNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getUserGroups.txt";
		String methodName = "getUserGroups";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidUserName"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidUserName"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getUserInformation] method positive with Mandotory Parameters")
	public void testgetUserInformationGroups() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getUserInformation.txt";
		String methodName = "getUserInformation";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("userName"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("userName"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getFirstElement().toString(), omElement.getFirstElement()
					.toString());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [getUserInformation] method Negative with existing user")
	public void testgetUserInformationNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "getUserInformation.txt";
		String methodName = "getUserInformation";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidUserName"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidUserName"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [removeUser] method positive with Mandotory Parameters")
	public void testremoveUser() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "removeUser.txt";
		String methodName = "removeUser";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("removeUsername"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("removeUsername"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(apiRespond.getFirstElement().getFirstElement()
					.getLocalName(), "Fault");
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [removeUser] method Negative with existing user")
	public void testremoveUserNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory + "removeUser.txt";
		String methodName = "removeUser";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidUserName"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidUserName"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [removeUserFromGroup] method positive with Mandotory Parameters")
	public void testremoveUserFromGroup() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "removeUserFromGroup.txt";
		String methodName = "removeUserFromGroup";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("addUserUserName"),
				connectorProperties.getProperty("addgroupGroupName"));

		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ "getUserGroups" + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token, connectorProperties.getProperty("addUserUserName"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertNotEquals(apiRespond.getFirstElement()
					.getFirstElement().getFirstElement().getFirstElement()
					.getText(),
					connectorProperties.getProperty("addgroupGroupName"));
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}

	@Test(priority = 1, groups = { "wso2.esb" }, description = "confluence [removeUserFromGroup] method Negative with existing user")
	public void testremoveUserFromGroupNegative() throws Exception {

		String jsonRequestFilePath = pathToRequestsDirectory
				+ "addUserToGroup.txt";
		String methodName = "addUserToGroup";

		final String jsonString = ConnectorIntegrationUtil
				.getFileContent(jsonRequestFilePath);
		final String proxyFilePath = "file:///" + pathToProxiesDirectory
				+ methodName + ".xml";
		String modifiedJsonString = String.format(jsonString,
				connectorProperties.getProperty("invalidUserName"),
				connectorProperties.getProperty("addgroupGroupName"));
		proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

		try {
			int statusCode = ConnectorIntegrationUtil
					.sendRequestToRetriveHeaders(
							getProxyServiceURL(methodName), modifiedJsonString);

			OMElement omElement = ConnectorIntegrationUtil.sendXMLRequest(
					getProxyServiceURL(methodName), modifiedJsonString);

			String initFilePath = pathToAPIRequestDirectory + "init.xml";
			final String initSoapRequest = ConnectorIntegrationUtil
					.getFileContent(initFilePath);
			String modifiedSoapRequest = String.format(initSoapRequest,
					connectorProperties.getProperty("username"),
					connectorProperties.getProperty("password"));
			OMElement resp = ConnectorIntegrationUtil
					.sendXMLRequest(connectorProperties.getProperty("uri"),
							modifiedSoapRequest);
			String token = resp.getFirstElement().getFirstElement()
					.getFirstElement().getText();

			String apiSoapRequestFilePath = pathToAPIRequestDirectory
					+ methodName + ".xml";
			final String apiSoapRequest = ConnectorIntegrationUtil
					.getFileContent(apiSoapRequestFilePath);
			String modifiedAPISoapRequest = String.format(apiSoapRequest,
					token,
					connectorProperties.getProperty("invalidUserName"),
					connectorProperties.getProperty("addgroupGroupName"));
			OMElement apiRespond = ConnectorIntegrationUtil.sendXMLRequest(
					connectorProperties.getProperty("uri"),
					modifiedAPISoapRequest);

			Assert.assertEquals(statusCode, 500);
			Assert.assertEquals(
					omElement.getText(),
					((OMElement) (apiRespond.getFirstElement()
							.getFirstElement()
							.getChildrenWithLocalName("faultstring").next()))
							.getText());
		} finally {
			proxyAdmin.deleteProxy(methodName);
		}

	}
}