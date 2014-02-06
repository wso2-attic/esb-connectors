package org.wso2.carbon.connector.integration.test.sugarcrm;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;

import javax.activation.DataHandler;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.context.ConfigurationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.proxy.admin.ProxyServiceAdminClient;
import org.wso2.carbon.automation.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.utils.axis2client.AxisServiceClient;
import org.wso2.carbon.automation.utils.axis2client.ConfigurationContextProvider;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.carbon.esb.ESBIntegrationTest;
import org.wso2.carbon.mediation.library.stub.MediationLibraryAdminServiceStub;
import org.wso2.carbon.mediation.library.stub.upload.MediationLibraryUploaderStub;

public class SugarCRMConnectorIntegrationTest extends ESBIntegrationTest {
	private static final String CONNECTOR_NAME = "sugarcrm";
	
	private MediationLibraryUploaderStub mediationLibUploadStub = null;

	private MediationLibraryAdminServiceStub adminServiceStub = null;

	private ProxyServiceAdminClient proxyAdmin;

	private String repoLocation = null;

	private String sugarCrmConnectorFileName = "sugarcrm.zip";
	
	private Properties sugarCrmConnectorProperties = null;

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
				mediationLibUploadStub, sugarCrmConnectorFileName);
		Thread.sleep(30000);

		adminServiceStub.updateStatus(
				"{org.wso2.carbon.connector}sugarcrm-connector-1.0.0",
				"sugarcrm-connector-1.0.0", "org.wso2.carbon.connector",
				"enabled");
		sugarCrmConnectorProperties = ConnectorIntegrationUtil
				.getConnectorConfigProperties(CONNECTOR_NAME);

	}

	@Override
	protected void cleanup() {
		axis2Client.destroy();
	}

	@Test(groups = { "wso2.esb" }, description = "Sugar CRM integration tests.")
	public void testSugarCRM() throws Exception {

		String[] methodsToTest = sugarCrmConnectorProperties
				.get("methodsToTest").toString().split(",");
		AxisServiceClient axisServiceClient = new AxisServiceClient();
		
		for (int i = 0; i < methodsToTest.length; i++) {
			String methodName = methodsToTest[i];
			log.info("Running " + methodName + " integration test.");
			

			proxyAdmin.addProxyService(new DataHandler(new URL("file:"
					+ File.separator + File.separator
					+ ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
					+ ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
					+ File.separator + "proxies" + File.separator
					+ CONNECTOR_NAME + File.separator + CONNECTOR_NAME + "_"
					+ methodName + ".xml")));
			OMElement getRequest = AXIOMUtil
					.stringToOM(ConnectorIntegrationUtil
							.readSoapRequestFile(
									File.separator
											+ File.separator
											+ ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION
											+ ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
											+ File.separator + "soapRequests"
											+ File.separator + CONNECTOR_NAME
											+ File.separator + methodName
											+ ".xml", Charset.defaultCharset()));
			OMElement response = axisServiceClient.sendReceive(getRequest,
					getProxyServiceURL(CONNECTOR_NAME + "_" + methodName),
					"mediate");
			log.info(ConnectorIntegrationUtil.firstToUpperCase(CONNECTOR_NAME)
					+ methodName + " response:" + response.toString());
			Assert.assertTrue(response.toString().contains(
					(String) sugarCrmConnectorProperties.getProperty(methodName
							+ "ExpectedResult")));
			proxyAdmin.deleteProxy(CONNECTOR_NAME + "_" + methodName);
		}

	}

}
