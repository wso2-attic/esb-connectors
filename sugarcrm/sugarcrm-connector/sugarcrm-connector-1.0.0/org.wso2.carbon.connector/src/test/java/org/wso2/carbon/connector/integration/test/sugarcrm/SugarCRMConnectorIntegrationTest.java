
package org.wso2.carbon.connector.integration.test.sugarcrm;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import javax.activation.DataHandler;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
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
    private String fileURLSeperator = null;
    private String sugarCrmConnectorFileName = "sugarcrm-connector-1.0.0.zip";
    private Properties sugarCrmConnectorProperties = null;
    
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
            fileURLSeperator = "///";
        } else {
            repoLocation = System.getProperty("connector_repo").replace("/", "/");
            fileURLSeperator = "";
        }
        proxyAdmin = new ProxyServiceAdminClient(esbServer.getBackEndUrl(), esbServer.getSessionCookie());
        ConnectorIntegrationUtil.uploadConnector(repoLocation, mediationLibUploadStub, sugarCrmConnectorFileName);
        Thread.sleep(30000);
        adminServiceStub.updateStatus("{org.wso2.carbon.connector}sugarcrm", "sugarcrm", "org.wso2.carbon.connector",
                "enabled");
        proxyAdmin.addProxyService(new DataHandler(new URL("file:" + fileURLSeperator + File.separator + File.separator
                + ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + ConnectorIntegrationUtil.ESB_CONFIG_LOCATION
                + File.separator + "proxies" + File.separator + CONNECTOR_NAME + File.separator + CONNECTOR_NAME
                + ".xml")));
        sugarCrmConnectorProperties = ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);
    }
    
    @Override
    protected void cleanup() {
    
        axis2Client.destroy();
    }
    
    /**
     * mandatory parameter test case for createAccount method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {createAccount} Mandatory Parameters integration test.")
    public void testCreateAccountMandatoryParams() throws Exception {
        final String methodName = "createAccount";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n" + "       </soapenv:Body>\n" + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement createAccountMandatoryParamRequest = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(createAccountMandatoryParamRequest, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertNotEquals(((OMElement) (response.getChildrenWithLocalName("return").next())).getText(), "0");
    }

    /**
     * optional parameter test case for createAccount method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {createAccount} Optional Parameters integration test.")
    public void testCreateAccountOptionalParams() throws Exception {
        final String methodName = "createAccount";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:name>Janaka</sug:name>\n"
                        + "               <sug:phone>012123232</sug:phone>\n"
                        + "               <sug:website>www.yahoo.com</sug:website>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testCreateAccountOptionalParams = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testCreateAccountOptionalParams, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertNotEquals(((OMElement) (response.getChildrenWithLocalName("return").next())).getText(), "0");
    }

    /**
     * Negative test case for createAccount method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {createAccount} Negative case integration test.")
    public void testCreateAccountNegativeCase() throws Exception {
        final String methodName = "createAccount";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>wrong password</sug:password>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testCreateAccountNegativeCase = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testCreateAccountNegativeCase, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertEquals(((OMElement) (response.getChildrenWithLocalName("return").next())).getText(), "0");
    }

    /**
     * mandatory parameter test case for createCase method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {createCase} Mandatory Parameters integration test.")
    public void testCreateCaseMandatoryParams() throws Exception {
        final String methodName = "createCase";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "             <sug:name></sug:name>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement createCaseMandatoryParamRequest = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(createCaseMandatoryParamRequest, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertNotEquals(((OMElement) (response.getChildrenWithLocalName("return").next())).getText(), "0");
    }

    /**
     * optional parameter test case for createCase method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {createCase} Optional Parameters integration test.")
    public void testCreateCaseOptionalParams() throws Exception {
        final String methodName = "createCase";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "             <sug:name>testcase</sug:name>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testCreateCaseOptionalParams = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testCreateCaseOptionalParams, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertNotEquals(((OMElement) (response.getChildrenWithLocalName("return").next())).getText(), "0");
    }

    /**
     * Negative test case for createCase method.
     */

    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {createCase} Negative case integration test.")
    public void testCreateCaseNegativeCase() throws Exception {
        final String methodName = "createCase";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>wrong password</sug:password>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testCreateCaseNegativeCase = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient
                        .sendReceive(testCreateCaseNegativeCase, getProxyServiceURL(CONNECTOR_NAME), "mediate");
        Assert.assertEquals(((OMElement) (response.getChildrenWithLocalName("return").next())).getText(), "0");
    }

    /**
     * mandatory parameter test case for createLead method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {createLead} Mandatory Parameters integration test.")
    public void testCreateLeadMandatoryParams() throws Exception {
        final String methodName = "createLead";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "             <sug:firstName></sug:firstName>\n"
                        + "             <sug:lastName></sug:lastName>\n"
                        + "             <sug:emailAddress></sug:emailAddress>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement createLeadMandatoryParamRequest = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(createLeadMandatoryParamRequest, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertNotEquals(((OMElement) (response.getChildrenWithLocalName("return").next())).getText(), "0");
    }

    /**
     * optional parameter test case for createLead method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {createLead} Optional Parameters integration test.")
    public void testCreateLeadOptionalParams() throws Exception {
        final String methodName = "createLead";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "             <sug:firstName>Chamath</sug:firstName>\n"
                        + "             <sug:lastName>Wijerathne</sug:lastName>\n"
                        + "             <sug:emailAddress>anuradhika.wije@gmail.com</sug:emailAddress>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n" + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testCreateLeadOptionalParams = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testCreateLeadOptionalParams, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertNotEquals(((OMElement) (response.getChildrenWithLocalName("return").next())).getText(), "0");
    }

    /**
     * Negative test case for createLead method.
     */

    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {createLead} Negative case integration test.")
    public void testCreateLeadNegativeCase() throws Exception {

        final String methodName = "createLead";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>wrong password</sug:password>\n"
                        + "             <sug:firstName></sug:firstName>\n"
                        + "             <sug:lastName></sug:lastName>\n"
                        + "             <sug:emailAddress></sug:emailAddress>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testCreateLeadNegativeCase = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient
                        .sendReceive(testCreateLeadNegativeCase, getProxyServiceURL(CONNECTOR_NAME), "mediate");
        Assert.assertEquals(((OMElement) (response.getChildrenWithLocalName("return").next())).getText(), "0");
    }

    /**
     * mandatory parameter test case for createOpportunity method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {createOpportunity} Mandatory Parameters integration test.")
    public void testCreateOpportunityMandatoryParams() throws Exception {
        final String methodName = "createOpportunity";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "             <sug:name></sug:name>\n"
                        + "             <sug:amount></sug:amount>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n" + "       </soapenv:Body>\n" + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement createOpportunityMandatoryParamRequest = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(createOpportunityMandatoryParamRequest,
                        getProxyServiceURL(CONNECTOR_NAME), "mediate");
        Assert.assertNotEquals(((OMElement) (response.getChildrenWithLocalName("return").next())).getText(), "0");
    }

    /**
     * optional parameter test case for createOpportunity method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {createOpportunity} Optional Parameters integration test.")
    public void testCreateOpportunityOptionalParams() throws Exception {
        final String methodName = "createOpportunity";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "             <sug:name>Chamath</sug:name>\n"
                        + "             <sug:amount>400</sug:amount>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testCreateOpportunityOptionalParams = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testCreateOpportunityOptionalParams, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertNotEquals(((OMElement) (response.getChildrenWithLocalName("return").next())).getText(), "0");
    }

    /**
     * Negative test case for createOpportunity method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {createOpportunity} Negative case integration test.")
    public void testCreateOpportunityNegativeCase() throws Exception {
        final String methodName = "createOpportunity";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>wrong password</sug:password>\n"
                        + "             <sug:name></sug:name>\n"
                        + "             <sug:amount></sug:amount>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n" + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testCreateOpportunityNegativeCase = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testCreateOpportunityNegativeCase, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertEquals(((OMElement) (response.getChildrenWithLocalName("return").next())).getText(), "0");
    }

    /**
     * mandatory parameter test case for createSession method.
     */

    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {createSession} Mandatory Parameters integration test.")
    public void testCreateSessionMandatoryParams() throws Exception {
        final String methodName = "createSession";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n" + "       </soapenv:Body>\n" + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement createSessionMandatoryParamRequest = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(createSessionMandatoryParamRequest, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertEquals(((OMElement) (response.getChildrenWithLocalName("return").next())).getText(), "Success");
    }

    /**
     * Negative test case for createSession method.
     */

    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {createSession} Negative case integration test.")
    public void testCreateSessionNegativeCase() throws Exception {
        final String methodName = "createSession";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>wrong password</sug:password>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testCreateSessionNegativeCase = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testCreateSessionNegativeCase, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertEquals(((OMElement) (response.getChildrenWithLocalName("return").next())).getText(), "Failed");
    }

    /**
     * mandatory parameter test case for getDocumentRevision method.
     */

    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {getDocumentRevision} Mandatory Parameters integration test.")
    public void testgetDocumentRevisionMandatoryParams() throws Exception {
        final String methodName = "getDocumentRevision";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "<sug:version>1</sug:version>\n"
                        + "<sug:appname>appfd</sug:appname>\n"
                        + "             <sug:id>91820068-4db8-8105-8a3a-52421f3a29bf</sug:id>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getDocumentRevisionMandatoryParamRequest = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(getDocumentRevisionMandatoryParamRequest,
                        getProxyServiceURL(CONNECTOR_NAME), "mediate");
        Assert.assertNotEquals(((OMElement) (response.getFirstElement().getFirstElement()
                .getChildrenWithLocalName("id").next())).getText(), "");
    }

    /**
     * Negative test case for getDocumentRevision method.
     */

    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {getDocumentRevision} Negative case integration test.")
    public void testGetDocumentRevisionNegativeCase() throws Exception {
        final String methodName = "getDocumentRevision";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "<sug:version>1</sug:version>\n"
                        + "<sug:appname>appfd</sug:appname>\n"
                        + "             <sug:id>wrong document id</sug:id>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testGetDocumentRevisionNegativeCase = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testGetDocumentRevisionNegativeCase, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertEquals(((OMElement) (((OMElement) (((OMElement) (response.getChildrenWithLocalName("return")
                .next())).getChildrenWithLocalName("error").next())).getChildrenWithLocalName("name").next()))
                .getText(), "No Records");
    }

    /**
     * mandatory parameter test case for getEntryByID method.
     */

    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {getEntryByID} Mandatory Parameters integration test.")
    public void testGetEntryByIdMandatoryParams() throws Exception {
        final String methodName = "getEntryByID";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "             <sug:moduleName>Accounts</sug:moduleName>\n"
                        + "             <sug:id>e01a0f28-64b9-cbd5-e7d6-523cc7b96953</sug:id>\n"
                        + "             <sug:selectFields>\n"
                        + "             <item></item>\n"
                        + "             <item></item>\n"
                        + "             </sug:selectFields>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getEntryByIdMandatoryParamRequest = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(getEntryByIdMandatoryParamRequest, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertEquals(((OMElement) (((OMElement) (((OMElement) (((OMElement) (response
                .getChildrenWithLocalName("return").next())).getChildrenWithLocalName("field_list").next()))
                .getChildrenWithLocalName("item").next())).getChildrenWithLocalName("label").next())).getText(), "ID");

    }

    /**
     * optional parameter test case for getEntryByID method.
     */

    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {getEntryByID} Optional Parameters integration test.")
    public void testGetEntryByIdOptionalParams() throws Exception {
        final String methodName = "getEntryByID";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "             <sug:moduleName>Accounts</sug:moduleName>\n"
                        + "             <sug:id>e01a0f28-64b9-cbd5-e7d6-523cc7b96953</sug:id>\n"
                        + "             <sug:selectFields>\n"
                        + "             <item>assigned_user_name</item>\n"
                        + "             <item>modified_by_name</item>\n"
                        + "             </sug:selectFields>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n" + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testGetEntryByIdOptionalParams = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testGetEntryByIdOptionalParams, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertEquals(((OMElement) (((OMElement) (((OMElement) (((OMElement) (response
                .getChildrenWithLocalName("return").next())).getChildrenWithLocalName("field_list").next()))
                .getChildrenWithLocalName("item").next())).getChildrenWithLocalName("label").next())).getText(), "ID");
    }

    /**
     * Negative test case for getEntryByID method.
     */

    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {getEntryByID} Negative case integration test.")
    public void testGetEntryByIdNegativeCase() throws Exception {
        final String methodName = "getEntryByID";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "             <sug:moduleName>AccountsNegative</sug:moduleName>\n"
                        + "             <sug:id></sug:id>\n"
                        + "             <sug:selectFields>\n"
                        + "             </sug:selectFields>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n" + "       </soapenv:Body>\n" + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testGetEntryByIdNegativeCase = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testGetEntryByIdNegativeCase, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertEquals(((OMElement) (((OMElement) (((OMElement) (response.getChildrenWithLocalName("return")
                .next())).getChildrenWithLocalName("error").next())).getChildrenWithLocalName("name").next()))
                .getText(), "Module Does Not Exist");
    }

    /**
     * mandatory parameter test case for getEntryList method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {getEntryList} Mandatory Parameters integration test.")
    public void testGetEntryListMandatoryParams() throws Exception {
        final String methodName = "getEntryList";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "             <sug:moduleName>Accounts</sug:moduleName>\n"
                        + "             <sug:query></sug:query>\n"
                        + "             <sug:orderby></sug:orderby>\n"
                        + "             <sug:offset></sug:offset>\n"
                        + "             <sug:selectFields>\n"
                        + "             <item></item>\n"
                        + "             <item></item>\n"
                        + "             </sug:selectFields>\n"
                        + "             <sug:maxResults></sug:maxResults>\n"
                        + "             <sug:deleted ></sug:deleted>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getEntryListMandatoryParamRequest = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(getEntryListMandatoryParamRequest, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertNotEquals(((OMElement) (((OMElement) (response.getChildrenWithLocalName("return").next()))
                .getChildrenWithLocalName("result_count").next())).getText(), "0");
    }

    /**
     * optional parameter test case for getEntryList method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {getEntryList} Optional Parameters integration test.")
    public void testGetEntryListOptionalParams() throws Exception {
        final String methodName = "getEntryList";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "             <sug:moduleName>Calls</sug:moduleName>\n"
                        + "             <sug:query>calls.name like '%e%'</sug:query>\n"
                        + "             <sug:orderby>name</sug:orderby>\n"
                        + "             <sug:offset>0</sug:offset>\n"
                        + "             <sug:selectFields>\n"
                        + "             <item>assigned_user_name</item>\n"
                        + "             <item>modified_by_name</item>\n"
                        + "             </sug:selectFields>\n"
                        + "             <sug:maxResults>4</sug:maxResults>\n"
                        + "             <sug:deleted>0</sug:deleted>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testGetEntryListOptionalParams = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testGetEntryListOptionalParams, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertNotEquals(((OMElement) (((OMElement) (response.getChildrenWithLocalName("return").next()))
                .getChildrenWithLocalName("result_count").next())).getText(), "0");
    }

    /**
     * Negative test case for getEntryList method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {getEntryList} Negative case integration test.")
    public void testGetEntryListNegativeCase() throws Exception {
        final String methodName = "getEntryList";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "             <sug:moduleName>Invalid module name</sug:moduleName>\n"
                        + "             <sug:query></sug:query>\n"
                        + "             <sug:orderby></sug:orderby>\n"
                        + "             <sug:offset></sug:offset>\n"
                        + "             <sug:selectFields>\n"
                        + "             <item></item>\n"
                        + "             <item></item>\n"
                        + "             </sug:selectFields>\n"
                        + "             <sug:maxResults></sug:maxResults>\n"
                        + "             <sug:deleted ></sug:deleted>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testGetEntryListNegativeCase = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testGetEntryListNegativeCase, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertEquals(((OMElement) (((OMElement) (((OMElement) (response.getChildrenWithLocalName("return")
                .next())).getChildrenWithLocalName("error").next())).getChildrenWithLocalName("name").next()))
                .getText(), "Access Denied");
    }

    /**
     * mandatory parameter test case for getModuleFields method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {getModuleFields} Mandatory Parameters integration test.")
    public void testGetModuleFieldsMandatoryParams() throws Exception {
        final String methodName = "getModuleFields";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "             <sug:moduleName>Calls</sug:moduleName>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getModuleFieldsMandatoryParamRequest = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(getModuleFieldsMandatoryParamRequest, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertEquals(((OMElement) (((OMElement) (((OMElement) (((OMElement) (response
                .getChildrenWithLocalName("return").next())).getChildrenWithLocalName("module_fields").next()))
                .getChildrenWithLocalName("item").next())).getChildrenWithLocalName("label").next())).getText(), "ID");
    }

    /**
     * Negative test case for getModuleFields method.
     */

    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {getModuleFields} Negative case invalid module name integration test.")
    public void testGetModuleFieldsNegativeCaseInvalidModuleName() throws Exception {
        final String methodName = "getModuleFields";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "             <sug:moduleName>invalid module name</sug:moduleName>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testGetModuleFieldsNegativeCase = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testGetModuleFieldsNegativeCase, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertEquals(((OMElement) (((OMElement) (((OMElement) (response.getChildrenWithLocalName("return")
                .next())).getChildrenWithLocalName("error").next())).getChildrenWithLocalName("name").next()))
                .getText(), "Module Does Not Exist");
    }

    /**
     * mandatory parameter test case for getMultipleEntriesByIDs method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {getMultipleEntriesByIDs} Mandatory Parameters integration test.")
    public void testGetMultipleEntriesByIDsMandatoryParams() throws Exception {
        final String methodName = "getMultipleEntries";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "             <sug:moduleName>Accounts</sug:moduleName>\n"
                        + "             <sug:ids>\n"
                        + "             <item></item>\n"
                        + "             <item></item>\n"
                        + "             </sug:ids>\n"
                        + "             <sug:selectFields>\n"
                        + "             <item></item>\n"
                        + "             <item></item>\n"
                        + "             </sug:selectFields>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getMultipleEntriesByIDsMandatoryParamRequest = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(getMultipleEntriesByIDsMandatoryParamRequest,
                        getProxyServiceURL(CONNECTOR_NAME), "mediate");
        Assert.assertEquals(((OMElement) (((OMElement) (((OMElement) (((OMElement) (response
                .getChildrenWithLocalName("return").next())).getChildrenWithLocalName("entry_list").next()))
                .getChildrenWithLocalName("item").next())).getChildrenWithLocalName("module_name").next())).getText(),
                "Accounts");
    }

    /**
     * optional parameter test case for getMultipleEntriesByIDs method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {getMultipleEntriesByIDs} Optional Parameters integration test.")
    public void testGetMultipleEntriesByIDsOptionalParams() throws Exception {
        final String methodName = "getMultipleEntries";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "             <sug:version>1</sug:version>\n"
                        + "             <sug:appname>appfd</sug:appname>\n"
                        + "             <sug:moduleName>Accounts</sug:moduleName>\n"
                        + "             <sug:ids>\n"
                        + "             <sug:item>dbf915f7-ba39-0b9d-74e8-523cc75ad5ec</sug:item>\n"
                        + "             </sug:ids>\n"
                        + "             <sug:selectFields>\n"
                        + "             <sug:item>assigned_user_name</sug:item>\n"
                        + "             <sug:item>assigned_user_id</sug:item>\n"
                        + "             </sug:selectFields>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n" + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testGetMultipleEntriesByIDsOptionalParams = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testGetMultipleEntriesByIDsOptionalParams,
                        getProxyServiceURL(CONNECTOR_NAME), "mediate");
        Assert.assertEquals(((OMElement) (((OMElement) (((OMElement) (((OMElement) (response
                .getChildrenWithLocalName("return").next())).getChildrenWithLocalName("field_list").next()))
                .getChildrenWithLocalName("item").next())).getChildrenWithLocalName("label").next())).getText(), "ID");
    }

    /**
     * Negative test case for getMultipleEntriesByIDs method.
     */

    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {getMultipleEntriesByIDs} Negative case integration test.")
    public void testGetMultipleEntriesByIDsNegativeCase() throws Exception {
        final String methodName = "getMultipleEntries";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "             <sug:moduleName>invalid module name</sug:moduleName>\n"
                        + "             <sug:ids>\n"
                        + "             <item></item>\n"
                        + "             <item></item>\n"
                        + "             </sug:ids>\n"
                        + "             <sug:selectFields>\n"
                        + "             <item></item>\n"
                        + "             <item></item>\n"
                        + "             </sug:selectFields>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n" + "       </soapenv:Body>\n" + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testGetMultipleEntriesByIDsNegativeCase = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testGetMultipleEntriesByIDsNegativeCase,
                        getProxyServiceURL(CONNECTOR_NAME), "mediate");
        Assert.assertEquals(((OMElement) (((OMElement) (((OMElement) (response.getChildrenWithLocalName("return")
                .next())).getChildrenWithLocalName("error").next())).getChildrenWithLocalName("name").next()))
                .getText(), "Module Does Not Exist");
    }

    /**
     * mandatory parameter test case for getNoteAttachment method.
     */

    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {getNoteAttachment} Mandatory Parameters integration test.")
    public void testGetNoteAttachmentMandatoryParams() throws Exception {
        final String methodName = "getNoteAttachment";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "<sug:version>1</sug:version>\n"
                        + "<sug:appname>appfd</sug:appname>\n"
                        + "               <sug:id></sug:id>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n" + "       </soapenv:Body>\n" + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getNoteAttachmentMandatoryParamRequest = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(getNoteAttachmentMandatoryParamRequest,
                        getProxyServiceURL(CONNECTOR_NAME), "mediate");

        Assert.assertNotEquals(((OMElement) (((OMElement) (((OMElement) (response.getChildrenWithLocalName("return")
                .next())).getChildrenWithLocalName("note_attachment").next())).getChildrenWithLocalName("id").next()))
                .getText(), "0");
    }

    /**
     * optional parameter test case for getNoteAttachment method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {getNoteAttachment} optional Parameters integration test.")
    public void testGetNoteAttachmentOptionalParams() throws Exception {
        final String methodName = "getNoteAttachment";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "<sug:version>1</sug:version>\n"
                        + "<sug:appname>appfd</sug:appname>\n"
                        + "               <sug:id>f7a00afd-2c76-7358-af51-5243cc841cc2</sug:id>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getNoteAttachmentMandatoryParamRequest = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(getNoteAttachmentMandatoryParamRequest,
                        getProxyServiceURL(CONNECTOR_NAME), "mediate");
        Assert.assertNotEquals(((OMElement) (((OMElement) (((OMElement) (response.getChildrenWithLocalName("return")
                .next())).getChildrenWithLocalName("note_attachment").next())).getChildrenWithLocalName("id").next()))
                .getText(), "0");
    }

    /**
     * mandatory parameter test case for getEntriesCount method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {getEntriesCount} Mandatory Parameters integration test.")
    public void testGetEntriesCountMandatoryParams() throws Exception {
        final String methodName = "getEntriesCount";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "             <sug:module>Accounts</sug:module>\n"
                        + "             <sug:query></sug:query>\n"
                        + "             <sug:deleted></sug:deleted>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getEntriesCountMandatoryParamRequest = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(getEntriesCountMandatoryParamRequest, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertNotEquals(((OMElement) (((OMElement) (response.getChildrenWithLocalName("return").next()))
                .getChildrenWithLocalName("result_count").next())).getText(), "0");
    }

    /**
     * optional parameter test case for getEntriesCount method.
     */

    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {getEntriesCount} Optional Parameters integration test.")
    public void testGetEntriesCountOptionalParams() throws Exception {
        final String methodName = "getEntriesCount";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "             <sug:module>Accounts</sug:module>\n"
                        + "             <sug:query>accounts.name like '%Test%'</sug:query>\n"
                        + "             <sug:deleted>1</sug:deleted>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testGetEntriesCountOptionalParams = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testGetEntriesCountOptionalParams, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertNotEquals(((OMElement) (((OMElement) (response.getChildrenWithLocalName("return").next()))
                .getChildrenWithLocalName("result_count").next())).getText(), "");
    }

    /**
     * Negative test case for getEntriesCount method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {getEntriesCount} Negative case integration test.")
    public void testGetEntriesCountNegativeCase() throws Exception {
        final String methodName = "getEntriesCount";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "             <sug:module>invalid module name</sug:module>\n"
                        + "             <sug:query></sug:query>\n"
                        + "             <sug:deleted></sug:deleted>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n" + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testGetEntriesCountNegativeCase = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testGetEntriesCountNegativeCase, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertEquals(((OMElement) ((OMElement) (response.getChildrenWithLocalName("return").next()))
                .getChildrenWithLocalName("result_count").next()).getText(), "-1");
    }

    /**
     * optional parameter test case for searchByModule method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {searchByModule} Optional Parameters integration test.")
    public void testSearchByModuleOptionalParams() throws Exception {
        final String methodName = "searchByModule";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "<sug:searchstring>a</sug:searchstring>\n"
                        + "<sug:modules>\n"
                        + "   <item>Accounts</item>\n"
                        + "   <item>Leads</item>\n"
                        + "</sug:modules>\n"
                        + "<sug:offset></sug:offset>\n"
                        + "<sug:maxresults>10</sug:maxresults>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testSearchByModuleOptionalParams = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testSearchByModuleOptionalParams, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertNotEquals(((OMElement) (((OMElement) (response.getChildrenWithLocalName("return").next()))
                .getChildrenWithLocalName("result_count").next())).getText(), "");
    }

    /**
     * Negative test case for searchByModule method.
     */

    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {searchByModule} Negative case integration test.")
    public void testSearchByModuleNegativeCase() throws Exception {
        final String methodName = "searchByModule";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>invalid password</sug:password>\n"
                        + "<sug:searchstring>a</sug:searchstring>\n"
                        + "<sug:modules>\n"
                        + "   <item></item>\n"
                        + "   <item></item>\n"
                        + "</sug:modules>\n"
                        + "<sug:offset></sug:offset>\n"
                        + "<sug:maxresults></sug:maxresults>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n" + "       </soapenv:Body>\n" + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testSearchByModuleNegativeCase = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testSearchByModuleNegativeCase, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertEquals(((OMElement) (((OMElement) (((OMElement) (response.getChildrenWithLocalName("return")
                .next())).getChildrenWithLocalName("error").next())).getChildrenWithLocalName("name").next()))
                .getText(), "Invalid Login");
    }

    /**
     * mandatory parameter test case for setEntryList method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {setEntryList} Mandatory Parameters integration test.")
    public void testSetEntryListMandatoryParams() throws Exception {
        final String methodName = "setEntryList";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "<sug:moduleName>Calls</sug:moduleName>\n"
                        + "<sug:nameValueLists>\n"
                        + "   <name_value_list>\n"
                        + "      <name_value>\n"
                        + "         <name></name>\n"
                        + "         <value></value>\n"
                        + "      </name_value>\n"
                        + "   </name_value_list>\n"
                        + "   <name_value_list>\n"
                        + "      <name_value>\n"
                        + "         <name></name>\n"
                        + "         <value></value>\n"
                        + "      </name_value>\n"
                        + "   </name_value_list>\n"
                        + "</sug:nameValueLists>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n" + "       </soapenv:Body>\n" + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement setEntryListMandatoryParamRequest = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(setEntryListMandatoryParamRequest, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertNotEquals(((OMElement) (((OMElement) (((OMElement) (response.getChildrenWithLocalName("return")
                .next())).getChildrenWithLocalName("ids").next())).getChildrenWithLocalName("item").next())).getText(),
                "");
    }

    /**
     * optional parameter test case for setEntryList method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {setEntryList} Optional Parameters integration test.")
    public void testSetEntryListOptionalParams() throws Exception {
        final String methodName = "setEntryList";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "<sug:moduleName>Calls</sug:moduleName>\n"
                        + "<sug:nameValueLists>\n"
                        + "   <name_value_list>\n"
                        + "      <name_value>\n"
                        + "         <name>name</name>\n"
                        + "         <value>553232</value>\n"
                        + "      </name_value>\n"
                        + "   </name_value_list>\n"
                        + "   <name_value_list>\n"
                        + "      <name_value>\n"
                        + "         <name>name</name>\n"
                        + "         <value>443232</value>\n"
                        + "      </name_value>\n"
                        + "   </name_value_list>\n"
                        + "</sug:nameValueLists>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n" + "       </soapenv:Body>\n" + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testSetEntryListOptionalParams = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testSetEntryListOptionalParams, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertNotEquals(((OMElement) (((OMElement) (((OMElement) (response.getChildrenWithLocalName("return")
                .next())).getChildrenWithLocalName("ids").next())).getChildrenWithLocalName("item").next())).getText(),
                "");
    }

    /**
     * Negative test case for setEntryList method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {setEntryList} Negative case integration test.")
    public void testSetEntryListNegativeCase() throws Exception {
        final String methodName = "setEntryList";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "<sug:moduleName>invalid module name</sug:moduleName>\n"
                        + "<sug:nameValueLists>\n"
                        + "   <name_value_list>\n"
                        + "      <name_value>\n"
                        + "         <name></name>\n"
                        + "         <value></value>\n"
                        + "      </name_value>\n"
                        + "   </name_value_list>\n"
                        + "   <name_value_list>\n"
                        + "      <name_value>\n"
                        + "         <name></name>\n"
                        + "         <value></value>\n"
                        + "      </name_value>\n"
                        + "   </name_value_list>\n"
                        + "</sug:nameValueLists>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n" + "       </soapenv:Body>\n" + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testSetEntryListNegativeCase = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testSetEntryListNegativeCase, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertEquals(((OMElement) (((OMElement) (((OMElement) (response.getChildrenWithLocalName("return")
                .next())).getChildrenWithLocalName("error").next())).getChildrenWithLocalName("name").next()))
                .getText(), "Module Does Not Exist");
    }

    /**
     * mandatory parameter test case for setEntry method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {setEntry} Mandatory Parameters integration test.")
    public void testSetEntryMandatoryParams() throws Exception {
        final String methodName = "setEntry";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "<sug:moduleName>Calls</sug:moduleName>\n"
                        + "<sug:nameValueList>\n"
                        + "</sug:nameValueList>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n" + "       </soapenv:Body>\n" + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement setEntryMandatoryParamRequest = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(setEntryMandatoryParamRequest, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertNotEquals(((OMElement) (((OMElement) (response.getChildrenWithLocalName("return").next()))
                .getChildrenWithLocalName("id").next())).getText(), "");
    }

    /**
     * optional parameter test case for setEntry method.
     */

    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {setEntry} optional Parameters integration test.")
    public void testSetEntryOptionalParams() throws Exception {
        final String methodName = "setEntry";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "<sug:moduleName>Accounts</sug:moduleName>\n"
                        + "<sug:nameValueList>\n"
                        + "<nameValue>\n"
                        + " <name>name</name>\n"
                        + " <value>TestBank</value>\n"
                        + "</nameValue>\n"
                        + "</sug:nameValueList>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n" + "       </soapenv:Body>\n" + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement setEntryMandatoryParamRequest = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(setEntryMandatoryParamRequest, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertNotEquals(((OMElement) (((OMElement) (response.getChildrenWithLocalName("return").next()))
                .getChildrenWithLocalName("id").next())).getText(), "");
    }

    /**
     * Negative test case for setEntry method.
     */

    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {setEntry} Negative case integration test.")
    public void testSetEntryNegativeCase() throws Exception {
        final String methodName = "setEntry";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "<sug:moduleName>invalid module name</sug:moduleName>\n"
                        + "<sug:nameValueList>\n"
                        + "<nameValue>\n"
                        + " <name>name</name>\n"
                        + " <value>DEMOUSER1234_123-MMM222</value>\n"
                        + "</nameValue>\n"
                        + "</sug:nameValueList>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n" + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testSetEntryNegativeCase = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testSetEntryNegativeCase, getProxyServiceURL(CONNECTOR_NAME), "mediate");
        Assert.assertEquals(((OMElement) ((OMElement) (response.getChildrenWithLocalName("return").next()))
                .getChildrenWithLocalName("id").next()).getText(), "-1");
    }

    /**
     * mandatory parameter test case for getAvailableModules method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {getAvailableModules} Mandatory Parameters integration test.")
    public void testGetAvailableModulesMandatoryParams() throws Exception {
        final String methodName = "getAvailableModules";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "<sug:version>1</sug:version>\n"
                        + "<sug:appname>appfd</sug:appname>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n" + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement getAvailableModulesMandatoryParamRequest = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(getAvailableModulesMandatoryParamRequest,
                        getProxyServiceURL(CONNECTOR_NAME), "mediate");
        Assert.assertEquals(((OMElement) (((OMElement) (((OMElement) (response.getChildrenWithLocalName("return")
                .next())).getChildrenWithLocalName("modules").next())).getChildrenWithLocalName("item").next()))
                .getText(), "Calls");
    }

    /**
     * Negative test case for getAvailableModules method.
     */

    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {getAvailableModules} Negative case integration test.")
    public void testGetAvailableModulesNegativeCase() throws Exception {
        final String methodName = "getAvailableModules";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>invalid password</sug:password>\n"
                        + "<sug:version>1</sug:version>\n"
                        + "<sug:appname>appfd</sug:appname>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testGetAvailableModulesNegativeCase = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testGetAvailableModulesNegativeCase, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertEquals(((OMElement) (((OMElement) (((OMElement) (response.getChildrenWithLocalName("return")
                .next())).getChildrenWithLocalName("error").next())).getChildrenWithLocalName("name").next()))
                .getText(), "Invalid Session ID");
    }

    /**
     * mandatory parameter test case for createContact method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {createContact} Mandatory Parameters integration test.")
    public void testCreateContactMandatoryParams() throws Exception {
        final String methodName = "createContact";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "<sug:firstName></sug:firstName>\n"
                        + "<sug:lastName></sug:lastName>\n"
                        + "<sug:emailAddress></sug:emailAddress>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n" + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement createContactMandatoryParamRequest = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(createContactMandatoryParamRequest, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertNotEquals(((OMElement) (response.getChildrenWithLocalName("return").next())).getText(), "0");
    }

    /**
     * optional parameter test case for createContact method.
     */

    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {createContact} Optional Parameters integration test.")
    public void testCreateContactOptionalParams() throws Exception {
        final String methodName = "createContact";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "<sug:firstName>Chamath</sug:firstName>\n"
                        + "<sug:lastName>Ranathunga</sug:lastName>\n"
                        + "<sug:emailAddress>jrath@gtest.com</sug:emailAddress>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testCreateContactOptionalParams = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testCreateContactOptionalParams, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertNotEquals(((OMElement) (response.getChildrenWithLocalName("return").next())).getText(), "0");
    }

    /**
     * Negative test case for createContact method.
     */

    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {createContact} Negative case integration test.")
    public void testCreateContactNegativeCase() throws Exception {
        final String methodName = "createContact";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>invalid password</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "<sug:firstName></sug:firstName>\n"
                        + "<sug:lastName></sug:lastName>\n"
                        + "<sug:emailAddress></sug:emailAddress>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n" + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testCreateContactNegativeCase = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testCreateContactNegativeCase, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertEquals(((OMElement) (response.getChildrenWithLocalName("return").next())).getText(), "0");
    }

    /**
     * mandatory parameter test case for setRelationship method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {setRelationship} mandatory Parameters integration test.")
    public void testSetRelationshipMandatoryParams() throws Exception {
        final String methodName = "setRelationship";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "<sug:module1>Accounts</sug:module1>\n"
                        + "<sug:module1Id></sug:module1Id>\n"
                        + "<sug:module2>Contacts</sug:module2>\n"
                        + "<sug:module2Id></sug:module2Id>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n" + "       </soapenv:Body>\n" + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement setRelationshipMandatoryParamRequest = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(setRelationshipMandatoryParamRequest, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertEquals(((OMElement) (((OMElement) (response.getChildrenWithLocalName("return").next()))
                .getChildrenWithLocalName("number").next())).getText(), "0");
    }

    /**
     * optional parameter test case for setRelationship method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {setRelationship} optional Parameters integration test.")
    public void testSetRelationshipOptionalParams() throws Exception {
        final String methodName = "setRelationship";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "<sug:module1>Accounts</sug:module1>\n"
                        + "<sug:module1Id>dbfe5e42-4a28-9b86-1457-530638308a44</sug:module1Id>\n"
                        + "<sug:module2>Contacts</sug:module2>\n"
                        + "<sug:module2Id>e21bd475-0090-2448-082c-523cc7dff4d2</sug:module2Id>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement setRelationshipMandatoryParamRequest = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(setRelationshipMandatoryParamRequest, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertEquals(((OMElement) (((OMElement) (response.getChildrenWithLocalName("return").next()))
                .getChildrenWithLocalName("number").next())).getText(), "0");
    }

    /**
     * Negative test case for setRelationship method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {setRelationship} Negative case invalid module name integration test.")
    public void testSetRelationshipNegativeCaseInvalidModuleName() throws Exception {
        final String methodName = "setRelationship";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "<sug:module1>invalid module name</sug:module1>\n"
                        + "<sug:module1Id></sug:module1Id>\n"
                        + "<sug:module2>Contacts</sug:module2>\n"
                        + "<sug:module2Id>e21bd475-0090-2448-082c-523cc7dff4d2</sug:module2Id>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testSetRelationshipNegativeCase = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testSetRelationshipNegativeCase, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertEquals(((OMElement) (((OMElement) (response.getChildrenWithLocalName("return").next()))
                .getChildrenWithLocalName("name").next())).getText(), "Module Does Not Exist");
    }

    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {setRelationship} Negative case invalid module id integration test.")
    public void testSetRelationshipNegativeCaseInvalidModuleId() throws Exception {
        final String methodName = "setRelationship";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "<sug:module1>Accounts</sug:module1>\n"
                        + "<sug:module1Id>dbfe5e42-4a28-9b86-1457-530638308a44</sug:module1Id>\n"
                        + "<sug:module2>Contacts</sug:module2>\n"
                        + "<sug:module2Id>invalid module id</sug:module2Id>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testSetRelationshipNegativeCase = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(testSetRelationshipNegativeCase, getProxyServiceURL(CONNECTOR_NAME),
                        "mediate");
        Assert.assertEquals(((OMElement) (((OMElement) (response.getChildrenWithLocalName("return").next()))
                .getChildrenWithLocalName("number").next())).getText(), "0");
    }
    
    /**
     * mandatory parameter test case for setRelationships method.
     */
    @Test(groups = { "wso2.esb" }, description = "Sugar CRM {setRelationships} Mandatory Parameters integration test.")
    public void testSetRelationshipsMandatoryParams() throws Exception {
        final String methodName = "setRelationships";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>"
                        + sugarCrmConnectorProperties.getProperty("password")
                        + "</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + " <sug:relationshipLists>\n"
                        + "    <sug:setRelationshipValue>\n"
                        + "       <sug:module1>Accounts</sug:module1>\n"
                        + "       <sug:module1Id>9c942380-37f3-3017-1dbb-523cc7857ca9</sug:module1Id>\n"
                        + "       <sug:module2>Contacts</sug:module2>\n"
                        + "       <sug:module2Id>10e3aa57-beaa-1da9-f1ef-523cc7b678f5</sug:module2Id>\n"
                        + "    </sug:setRelationshipValue>\n"
                        + "    <sug:setRelationshipValue>\n"
                        + "       <sug:module1>Accounts</sug:module1>\n"
                        + "       <sug:module1Id>afaab2e4-5151-f1ca-44c9-523cc7721518</sug:module1Id>\n"
                        + "       <sug:module2>Contacts</sug:module2>\n"
                        + "       <sug:module2Id>f21800bb-699f-9e88-6a29-523cc7d35d75</sug:module2Id>\n"
                        + "    </sug:setRelationshipValue>\n"
                        + "</sug:relationshipLists> \n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement setRelationshipsMandatoryParamRequest = AXIOMUtil.stringToOM(omString);
        OMElement response =
                axisServiceClient.sendReceive(setRelationshipsMandatoryParamRequest,
                        getProxyServiceURL(CONNECTOR_NAME), "mediate");
        System.out.println("\n\n\n\n\n\n\n\n\n" + response.getText().toString() );
        Assert.assertNotEquals(((OMElement) (((OMElement) (response.getChildrenWithLocalName("return").next()))
                .getChildrenWithLocalName("created").next())).getText(), "0");
        System.out.println("\n\n\n\n\n\n\n\n" + ((OMElement) (((OMElement) (response.getChildrenWithLocalName("return").next()))
                .getChildrenWithLocalName("created").next())).getText().toString());
    }
    
    /**
     * Negative test case for setRelationships method.
     */

    @Test(expectedExceptions = AxisFault.class, groups = { "wso2.esb" }, description = "Sugar CRM {setRelationships} Negative case integration test.")
    public void testSetRelationshipsNegativeCase() throws Exception {
        final String methodName = "setRelationships";
        final String omString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sug=\"wso2.connector.sugarcrm\">\n"
                        + "   <soapenv:Header/>\n"
                        + "       <soapenv:Body>\n"
                        + "           <root>\n"
                        + "               <sug:appUri>"
                        + sugarCrmConnectorProperties.getProperty("appUri")
                        + "</sug:appUri>\n"
                        + "               <sug:userName>"
                        + sugarCrmConnectorProperties.getProperty("userName")
                        + "</sug:userName>\n"
                        + "               <sug:password>invalid password</sug:password>\n"
                        + "               <sug:version>1</sug:version>\n"
                        + "               <sug:appname>appfd</sug:appname>\n"
                        + "       <sug:module1>Accounts</sug:module1>\n"
                        + "       <sug:module1Id>9c942380-37f3-3017-1dbb-523cc7857ca9</sug:module1Id>\n"
                        + "       <sug:module2>Contacts</sug:module2>\n"
                        + "       <sug:module2Id>10e3aa57-beaa-1da9-f1ef-523cc7b678f5</sug:module2Id>\n"
                        + "<sug:method>"
                        + methodName
                        + "</sug:method>\n"
                        + "           </root>\n"
                        + "       </soapenv:Body>\n"
                        + " </soapenv:Envelope>";
        AxisServiceClient axisServiceClient = new AxisServiceClient();
        OMElement testSetRelationshipsNegativeCase = AXIOMUtil.stringToOM(omString);
        axisServiceClient.sendReceive(testSetRelationshipsNegativeCase, getProxyServiceURL(CONNECTOR_NAME), "mediate");
    }
    
    /**
     * Clean up the ESB.
     * 
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void cleanUpEsb() throws Exception {
        proxyAdmin.deleteProxy(CONNECTOR_NAME);
    }
    
}
