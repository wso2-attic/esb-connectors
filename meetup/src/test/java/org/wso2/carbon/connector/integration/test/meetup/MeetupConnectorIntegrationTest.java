/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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

package org.wso2.carbon.connector.integration.test.meetup;

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

import javax.activation.DataHandler;
import java.net.URL;
import java.util.Properties;

// TODO: Auto-generated Javadoc

/**
 * The Class MeetupConnectorIntegrationTest.
 */
public class MeetupConnectorIntegrationTest extends ESBIntegrationTest {

	/**
	 * The Constant CONNECTOR_NAME.
	 */
	protected static final String CONNECTOR_NAME = "meetup";

	/**
	 * The mediation lib upload stub.
	 */
	protected MediationLibraryUploaderStub mediationLibUploadStub;

	/**
	 * The admin service stub.
	 */
	protected MediationLibraryAdminServiceStub adminServiceStub;

	/**
	 * The proxy admin.
	 */
	protected ProxyServiceAdminClient proxyAdmin;

	/**
	 * The repo location.
	 */
	protected String repoLocation;

	/**
	 * The meetup connector file name.
	 */
	protected String meetupConnectorFileName = CONNECTOR_NAME + ".zip";

	/**
	 * The meetup connector properties.
	 */
	protected Properties meetupConnectorProperties;

	/**
	 * The path to proxies directory.
	 */
	protected String pathToProxiesDirectory;

	/**
	 * The path to requests directory.
	 */
	protected String pathToRequestsDirectory;

	/**
	 * Sets the environment.
	 *
	 * @throws Exception the exception
	 */
	@BeforeClass(alwaysRun = true)
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
		proxyAdmin =
				new ProxyServiceAdminClient(esbServer.getBackEndUrl(),
				                            esbServer.getSessionCookie());

		ConnectorIntegrationUtil.uploadConnector(repoLocation, mediationLibUploadStub,
		                                         meetupConnectorFileName);
		log.info("Sleeping for " + 20000 / 1000 + " seconds while waiting for synapse import");
		Thread.sleep(20000);

		adminServiceStub.updateStatus("{org.wso2.carbon.connector}" + CONNECTOR_NAME,
		                              CONNECTOR_NAME, "org.wso2.carbon.connector", "enabled");

		meetupConnectorProperties =
				ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);

		pathToProxiesDirectory =
				repoLocation +
				meetupConnectorProperties.getProperty("proxyDirectoryRelativePath");
		pathToRequestsDirectory =
				repoLocation +
				meetupConnectorProperties.getProperty("requestDirectoryRelativePath");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wso2.carbon.esb.ESBIntegrationTest#cleanup()
	 */
	@Override
	protected void cleanup() throws Exception {
		axis2Client.destroy();
	}

}
