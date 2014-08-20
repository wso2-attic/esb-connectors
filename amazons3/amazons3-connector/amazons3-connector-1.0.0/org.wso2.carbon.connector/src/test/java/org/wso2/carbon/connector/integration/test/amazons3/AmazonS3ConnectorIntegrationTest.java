
package org.wso2.carbon.connector.integration.test.amazons3;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.context.ConfigurationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.wso2.carbon.automation.api.clients.proxy.admin.ProxyServiceAdminClient;
import org.wso2.carbon.automation.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.automation.utils.axis2client.ConfigurationContextProvider;
import org.wso2.carbon.connector.integration.test.common.ConnectorIntegrationUtil;
import org.wso2.carbon.connector.integration.test.common.MultipartFormdataProcessor;
import org.wso2.carbon.connector.integration.test.common.RestResponse;
import org.wso2.carbon.esb.ESBIntegrationTest;
import org.wso2.carbon.mediation.library.stub.MediationLibraryAdminServiceStub;
import org.wso2.carbon.mediation.library.stub.upload.MediationLibraryUploaderStub;
import org.wso2.carbon.proxyadmin.stub.ProxyServiceAdminProxyAdminException;

public class AmazonS3ConnectorIntegrationTest extends ESBIntegrationTest {
    
    private static final String CONNECTOR_NAME = "amazons3";
    
    private MediationLibraryUploaderStub mediationLibUploadStub = null;
    
    private MediationLibraryAdminServiceStub adminServiceStub = null;
    
    private ProxyServiceAdminClient proxyAdmin;
    
    private String repoLocation = null;
    
    private String amazons3ConnectorFileName = CONNECTOR_NAME + "-connector-1.0.0.zip";
    
    private Properties amazons3ConnectorProperties = null;
    
    private String pathToProxiesDirectory = null;
    
    private String pathToRequestsDirectory = null;
    
    private static final String CONTENT_TYPE_APPLICATION_XML = "application/xml";
    
    private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private String authorizationCode = null;
    
    private String dateValue = null;
    
    private String authorizationCodeNew = null;
    
    private String dateValueNew = null;
    
    private String pathToResourcesDirectory = null;
    
    private String uploadIdMandatory = null;
    
    private String uploadIdOptional = null;
    
    private String uploadPartETag = null;
    
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
        } else {
            repoLocation = System.getProperty("connector_repo").replace("/", "/");
        }
        
        proxyAdmin = new ProxyServiceAdminClient(esbServer.getBackEndUrl(), esbServer.getSessionCookie());
        
        ConnectorIntegrationUtil.uploadConnector(repoLocation, mediationLibUploadStub, amazons3ConnectorFileName);
        log.info("Sleeping for " + 30000 / 1000 + " seconds while waiting for synapse import");
        Thread.sleep(30000);
        
        adminServiceStub.updateStatus("{org.wso2.carbon.connector}" + CONNECTOR_NAME, CONNECTOR_NAME,
                "org.wso2.carbon.connector", "enabled");
        
        amazons3ConnectorProperties = ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);
        
        pathToProxiesDirectory = repoLocation + amazons3ConnectorProperties.getProperty("proxyDirectoryRelativePath");
        pathToRequestsDirectory =
                repoLocation + amazons3ConnectorProperties.getProperty("requestDirectoryRelativePath");
        pathToResourcesDirectory =
                repoLocation + amazons3ConnectorProperties.getProperty("resourceDirectoryRelativePath");
        
        final String proxyFilePath = "file:///" + pathToProxiesDirectory + "amazons3.xml";
        final String multipartProxyPath = "file:///" + pathToProxiesDirectory + "amazons3_multipart.xml";
        
        proxyAdmin.addProxyService(new DataHandler(new URL(multipartProxyPath)));
        proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
    }
    
    @Override
    protected void cleanup() throws RemoteException, ProxyServiceAdminProxyAdminException {
    
        proxyAdmin.deleteProxy("amazons3");
        axis2Client.destroy();
    }
    
    /**
     * Positive test case for createBucket method for web site configurations.
     */
    @Test(groups = { "wso2.esb" }, description = "AmazonS3 {createBucket} integration test with mandatory parameters.")
    public void testCreateBucketForWebSiteConfig() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createBucket");
        
        String xmlRequestFilePath = pathToRequestsDirectory + "createBucket_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_7"),
                        amazons3ConnectorProperties.getProperty("bucketName_3"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap); 
        
        Assert.assertTrue(statusCode == 200);
        
    }
        
    /**
     * Positive test case for createBucket method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "AmazonS3 {createBucket} integration test with mandatory parameters.")
    public void testCreateBucketWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createBucket");
        
        String xmlRequestFilePath = pathToRequestsDirectory + "createBucket_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_1"),
                        amazons3ConnectorProperties.getProperty("bucketName_1"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap); 
        
        Assert.assertTrue(statusCode == 200);
        
    }
    
    /**
     * Positive test case for createBucket method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "AmazonS3 {createBucket} integration test with optional parameters.")
    public void testCreateBucketWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createBucket");
        String xmlRequestFilePath = pathToRequestsDirectory + "createBucket_optional.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_2"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"));
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);

        Assert.assertTrue(statusCode == 200);
    }
    
    /**
     * Negative test case for createBucket method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateBucketWithOptionalParameters" }, description = "AmazonS3 {createBucket} integration test with negative case.")
    public void testCreateBucketWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createBucket");
        String xmlRequestFilePath = pathToRequestsDirectory + "createBucket_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_2"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"));
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 403);
        
    }
    
    /**
     * Positive test case for createBucketPolicy method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateBucketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {createBucketPolicy} integration test with mandatory parameters.")
    public void testCreateBucketPolicyWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createBucketPolicy");
        String xmlRequestFilePath = pathToRequestsDirectory + "createBucketPolicy_mandatory.txt";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedjsonString =
                String.format(jsonString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_5"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"));
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedjsonString, CONTENT_TYPE_APPLICATION_JSON, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 204);
    }
    
    /**
     * Negative test case for createBucketPolicy method.
     */
    @Test(dependsOnMethods = { "testCreateBucketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {createBucketPolicy} integration test with negative case.")
    public void testCreateBucketPolicyWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createBucketPolicy");
        String xmlRequestFilePath = pathToRequestsDirectory + "createBucketPolicy_negative.txt";
        
        final String jsonString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedjsonString =
                String.format(jsonString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_4"));
        Thread.sleep(Long.parseLong(amazons3ConnectorProperties.getProperty("timeOut")));
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedjsonString, CONTENT_TYPE_APPLICATION_JSON, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 400);
    }
    
    /**
     * Positive test case for createBucketWebsiteConfiguration method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateBucketForWebSiteConfig" }, groups = { "wso2.esb" }, description = "AmazonS3 {createBucketWebsiteConfiguration} integration test with mandatory parameters.")
    public void testCreateBucketWebsiteConfigurationWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createBucketWebsiteConfiguration");
        String xmlRequestFilePath = pathToRequestsDirectory + "createBucketWebsiteConfiguration_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_3"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_7"));
                
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);

        Assert.assertTrue(statusCode == 200);
    }
    
    /**
     * Negative test case for createBucketWebsiteConfiguration method.
     */
    @Test(dependsOnMethods = { "testCreateBucketForWebSiteConfig" }, groups = { "wso2.esb" }, description = "AmazonS3 {createBucketWebsiteConfiguration} integration test with negative case.")
    public void testCreateBucketWebsiteConfigurationWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createBucketWebsiteConfiguration");
        String xmlRequestFilePath = pathToRequestsDirectory + "createBucketWebsiteConfiguration_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_3"));
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 403);
    }
    
    /**
     * Positive test case for setBucketACL method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateBucketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {setBucketACL} integration test with mandatory parameters.")
    public void testSetBucketACLWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:setBucketACL");
        
        String xmlRequestFilePath = pathToRequestsDirectory + "setBucketACL_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_5"),
                        amazons3ConnectorProperties.getProperty("ownerId"),
                        amazons3ConnectorProperties.getProperty("displayName"),
                        amazons3ConnectorProperties.getProperty("ownerId"),
                        amazons3ConnectorProperties.getProperty("displayName"),
                        amazons3ConnectorProperties.getProperty("ownerId"),
                        amazons3ConnectorProperties.getProperty("displayName"));
        
        Thread.sleep(Long.parseLong(amazons3ConnectorProperties.getProperty("timeOut")));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        
        Assert.assertTrue(statusCode == 200);
    }
    
    /**
     * Negative test case for setBucketACL method.
     */
    @Test(dependsOnMethods = { "testCreateBucketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {setBucketACL} integration test with negative case.")
    public void testSetBucketACLWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:setBucketACL");
        String xmlRequestFilePath = pathToRequestsDirectory + "setBucketACL_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_5"));
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 403);
        
    }
    
    /**
     * Positive test case for getBuckets method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateBucketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getBuckets} integration test with mandatory parameters.")
    public void testGetBucketsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getBuckets");
        String xmlRequestFilePath = pathToRequestsDirectory + "getBuckets_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 200);
    }
    
    /**
     * Positive test case for getBuckets method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateBucketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getBuckets} integration test with optional parameters.")
    public void testGetBucketsWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getBuckets");
        String xmlRequestFilePath = pathToRequestsDirectory + "getBuckets_optional.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"));
        Thread.sleep(Long.parseLong(amazons3ConnectorProperties.getProperty("timeOut")));
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 200);
    }
    
    /**
     * Negative test case for getBuckets method.
     */
    @Test(groups = { "wso2.esb" }, description = "AmazonS3 {getBuckets} integration test with negative case.")
    public void testGetBucketsWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getBuckets");
        String xmlRequestFilePath = pathToRequestsDirectory + "getBuckets_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"));
        Thread.sleep(Long.parseLong(amazons3ConnectorProperties.getProperty("timeOut")));
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 403);
        
    }
    
    /**
     * Positive test case for getBucketVersioning method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateBucketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getBucketVersioning} integration test with mandatory parameters.")
    public void testGetBucketVersioningWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getBucketVersioning");
        String xmlRequestFilePath = pathToRequestsDirectory + "getBucketVersioning_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_4"));
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 200);
        
    }
    
    /**
     * Negative test case for getBucketVersioning method.
     */
    @Test(dependsOnMethods = { "testCreateBucketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getBucketVersioning} integration test with negative case.")
    public void testGetBucketVersioningWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getBucketVersioning");
        String xmlRequestFilePath = pathToRequestsDirectory + "getBucketVersioning_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 403);
        
    }
    
    /**
     * Positive test case for getBucketRequestPayment method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateBucketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getBucketRequestPayment} integration test with mandatory parameters.")
    public void testGetBucketRequestPaymentWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getBucketRequestPayment");
        String xmlRequestFilePath = pathToRequestsDirectory + "getBucketRequestPayment_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_4"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 200);
        
    }
    
    /**
     * Negative test case for getBucketRequestPayment method.
     */
    @Test(dependsOnMethods = { "testCreateBucketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getBucketRequestPayment} integration test with negative case.")
    public void testGetBucketRequestPaymentWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getBucketRequestPayment");
        String xmlRequestFilePath = pathToRequestsDirectory + "getBucketRequestPayment_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"));
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);

        Assert.assertTrue(statusCode == 403);        
    }
    
    /**
     * Positive test case for getBucketACL method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateBucketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getBucketACL} integration test with mandatory parameters.")
    public void testGetBucketACLWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getBucketACL");
        String xmlRequestFilePath = pathToRequestsDirectory + "getBucketACL_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_4"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 200);
        
    }
    
    /**
     * Negative test case for getBucketACL method.
     */
    @Test(dependsOnMethods = { "testCreateBucketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getBucketACL} integration test with negative case.")
    public void testGetBucketACLWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getBucketACL");
        String xmlRequestFilePath = pathToRequestsDirectory + "getBucketACL_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 403);
    }
    
    /**
     * Positive test case for getBucketLifeCycle method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateBucketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getBucketLifeCycle} integration test with mandatory parameters.")
    public void testGetBucketLifeCycleWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getBucketLifeCycle");
        String xmlRequestFilePath = pathToRequestsDirectory + "getBucketLifeCycle_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_4"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 404);
        
    }
    
    /**
     * Negative test case for getBucketLifeCycle method.
     */
    @Test(dependsOnMethods = { "testCreateBucketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getBucketLifeCycle} integration test with negative case.")
    public void testGetBucketLifeCycleWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getBucketLifeCycle");
        String xmlRequestFilePath = pathToRequestsDirectory + "getBucketLifeCycle_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 403);
    }
    
    /**
     * Positive test case for getWebSiteConfiguration method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateBucketWebsiteConfigurationWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getWebSiteConfiguration} integration test with mandatory parameters.")
    public void testGetWebSiteConfigurationWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getWebSiteConfiguration");
        String xmlRequestFilePath = pathToRequestsDirectory + "getWebSiteConfiguration_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_3"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_7"));
        
        Thread.sleep(Long.parseLong(amazons3ConnectorProperties.getProperty("timeOut")));
        
       int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap); 

        Assert.assertTrue(statusCode == 200);
    }
    
    /**
     * Negative test case for getWebSiteConfiguration method.
     */
    @Test(dependsOnMethods = { "testCreateBucketWebsiteConfigurationWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getWebSiteConfiguration} integration test with negative case.")
    public void testGetWebSiteConfigurationWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getWebSiteConfiguration");
        String xmlRequestFilePath = pathToRequestsDirectory + "getWebSiteConfiguration_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_3"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 403);
    }
    
    /**
     * Positive test case for getBucketPolicy method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateBucketPolicyWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getBucketPolicy} integration test with mandatory parameters.")
    public void testGetBucketPolicyWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getBucketPolicy");
        String xmlRequestFilePath = pathToRequestsDirectory + "getBucketPolicy_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_4"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 200);
        
    }
    
    /**
     * Negative test case for getBucketPolicy method.
     */
    @Test(dependsOnMethods = { "testCreateBucketPolicyWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getBucketPolicy} integration test with negative case.")
    public void testGetBucketPolicyWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getBucketPolicy");
        String xmlRequestFilePath = pathToRequestsDirectory + "getBucketPolicy_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"));
        Thread.sleep(Long.parseLong(amazons3ConnectorProperties.getProperty("timeOut")));
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 403);
    }
    
    /**
     * Positive test case for getAuthorization method for upload part with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateBucketWithOptionalParameters" }, description = "AmazonS3 {completeMultipartUpload} integration test with optional parameters.")
    public void testGetAuthorizationForCreateObjectWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getAuthorization");
        
        String xmlRequestFilePath = pathToRequestsDirectory + "getAuthorization_createObject_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"));
        
        Thread.sleep(Long.parseLong(amazons3ConnectorProperties.getProperty("timeOut")));
        
        final String response =
                ConnectorIntegrationUtil.getResponseViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
       
 	if (response != null) {
            OMElement element = AXIOMUtil.stringToOM(response); 
	    Iterator<OMElement> iterator = element.getChildren();
	    while(iterator.hasNext()){
		OMElement childElement = (OMElement) iterator.next();
		String localName = childElement.getLocalName();
		if("authentication".equals(localName)){
 		   authorizationCodeNew = childElement.getText(); 
		}else if("date".equals(localName)){
		   dateValueNew = childElement.getText();
		}  
	    }	     
        }
        Assert.assertNotNull(authorizationCodeNew);
        Assert.assertNotNull(dateValueNew);
    }
    
     /**
     * Positive test case for createObject method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetAuthorizationForCreateObjectWithMandatoryParameters" }, description = "AmazonS3 {createObject} integration test with mandatory parameters.")
    public void testCreateObjectWithMandatoryParameters() throws Exception {
    
        String objectName = amazons3ConnectorProperties.getProperty("objectName");
        
        esbRequestHeadersMap.put("Action", "urn:createObject");
        esbRequestHeadersMap.put("Authorization", authorizationCodeNew);
        esbRequestHeadersMap.put("x-amz-date", dateValueNew);
        
        final String bucketUrl = amazons3ConnectorProperties.getProperty("bucketUrl_5");
        
        // Call through the ESB.
        final String requestString = getProxyServiceURL("multipart") + "?bucketUrl=" + bucketUrl + "/";
        final MultipartFormdataProcessor multipartProcessor = new MultipartFormdataProcessor();
        
        final File file = new File(pathToResourcesDirectory + objectName);
        
        // Form Fields
        final Map<String, String> formFields = new HashMap<String, String>();
        formFields.put("key", objectName);
        formFields.put("acl", "public-read-write");
        formFields.put("success_action_status", "200");
        
        final int responseCode =
                multipartProcessor.sendRequestWithSingleFile(requestString, formFields, esbRequestHeadersMap, file);
        
        Assert.assertTrue(responseCode == 200);
    }
    
    /**
     * To test the method deleteMultipleObjects.
     */
    @Test(dependsOnMethods = { "testCreateBucketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {createObject} integration test with mandatory parameters.")
    public void toDeleteObject1() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createObject");
        String xmlRequestFilePath = pathToRequestsDirectory + "createObject_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_4"),
                        amazons3ConnectorProperties.getProperty("objectName_1"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 200);
        
    }
    
    /**
     * To test the method deleteMultipleObjects.
     */
    @Test(dependsOnMethods = { "testCreateBucketWithOptionalParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {createObject} integration test with mandatory parameters.")
    public void toDeleteObject2() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createObject");
        String xmlRequestFilePath = pathToRequestsDirectory + "createObject_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_4"),
                        amazons3ConnectorProperties.getProperty("objectName_2"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 200);
    }
    
    /**
     * Positive test case for createObjectACL method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateObjectCopyMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {createObjectACL} integration test with mandatory parameters.")
    public void testCreateObjectACLMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createObjectACL");
        String xmlRequestFilePath = pathToRequestsDirectory + "createObjectACL_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_4"),
                        amazons3ConnectorProperties.getProperty("destinationObjectName"),
                        amazons3ConnectorProperties.getProperty("ownerId"),
                        amazons3ConnectorProperties.getProperty("ownerdisplayName"));

        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 200);
        
    }
    
    /**
     * Negative test case for createObjectACL method.
     */
    @Test(dependsOnMethods = { "testCreateObjectWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {createObjectACL} integration test with negative case.")
    public void testCreateObjectACLWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createObjectACL");
        String xmlRequestFilePath = pathToRequestsDirectory + "createObjectACL_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_4"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 404);
        
    }
    
    /**
     * Positive test case for createObjectCopy method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateObjectWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {createObjectCopy} integration test with mandatory parameters.")
    public void testCreateObjectCopyMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createObjectCopy");
        String xmlRequestFilePath = pathToRequestsDirectory + "createObjectCopy_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_4"),
                        amazons3ConnectorProperties.getProperty("destinationObjectName"),
                        amazons3ConnectorProperties.getProperty("copySource"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 200);
        
    }
    
    /**
     * Negative test case for createObjectCopy method.
     */
    @Test(dependsOnMethods = { "testCreateObjectWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {createObjectCopy} integration test with negative case.")
    public void testCreateObjectCopyWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:createObjectCopy");
        String xmlRequestFilePath = pathToRequestsDirectory + "createObjectCopy_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("destinationObjectName"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 403);
        
    }
    
    /**
     * Positive test case for getObjectsInBucket method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateObjectWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getObjectsInBucket} integration test with mandatory parameters.")
    public void testGetObjectsInBucketWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getObjectsInBucket");
        String xmlRequestFilePath = pathToRequestsDirectory + "getObjectsInBucket_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_4"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 200);
        
    }
    
    /**
     * Negative test case for getObjectsInBucket method.
     */
    @Test(dependsOnMethods = { "testCreateObjectWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getObjectsInBucket} integration test with negative case.")
    public void testGetObjectsInBucketWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getObjectsInBucket");
        String xmlRequestFilePath = pathToRequestsDirectory + "getObjectsInBucket_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 403);
        
    }
    
    /**
     * Positive test case for getBucketObjectVersions method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateObjectWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getBucketObjectVersions} integration test with mandatory parameters.")
    public void testGetBucketObjectVersionsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getBucketObjectVersions");
        String xmlRequestFilePath = pathToRequestsDirectory + "getBucketObjectVersions_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_4"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 200);
        
    }
    
    /**
     * Positive test case for getBucketObjectVersions method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateObjectWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getBucketObjectVersions} integration test with optional parameters.")
    public void testGetBucketObjectVersionsWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getBucketObjectVersions");
        String xmlRequestFilePath = pathToRequestsDirectory + "getBucketObjectVersions_optional.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_4"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 200);
        
    }
    
    /**
     * Negative test case for getBucketObjectVersions method.
     */
    @Test(dependsOnMethods = { "testCreateObjectWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getBucketObjectVersions} integration test with negative case.")
    public void testGetBucketObjectVersionsWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getBucketObjectVersions");
        String xmlRequestFilePath = pathToRequestsDirectory + "getBucketObjectVersions_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 403);
        
    }
    
    /**
     * Positive test case for getObject method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateObjectWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getObject} integration test with mandatory parameters.")
    public void testGetObjectWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getObject");
        String xmlRequestFilePath = pathToRequestsDirectory + "getObject_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_5"),
                        amazons3ConnectorProperties.getProperty("objectName"));
        
        Thread.sleep(Long.parseLong(amazons3ConnectorProperties.getProperty("timeOut")));
        String response =
                ConnectorIntegrationUtil.getResponseViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(response.contains("getObjectResponse"));
        Assert.assertTrue(response.contains("data"));
        
    }
    
    /**
     * Positive test case for getObject method with optional parameters.
     */
    @Test(dependsOnMethods = { "testCreateObjectWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getObject} integration test with optional parameters.")
    public void testGetObjectWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getObject");
        String xmlRequestFilePath = pathToRequestsDirectory + "getObject_optional.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_5"),
                        amazons3ConnectorProperties.getProperty("objectName"),
                        amazons3ConnectorProperties.getProperty("ifModifiedSince"));
        
        String response =
                ConnectorIntegrationUtil.getResponseViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(response.contains("getObjectResponse"));
        Assert.assertTrue(response.contains("data"));
        
    }
    
    /**
     * Negative test case for getObject method.
     */
    @Test(dependsOnMethods = { "testCreateObjectWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getObject} integration test with negative case.")
    public void testGetObjectWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getObject");
        String xmlRequestFilePath = pathToRequestsDirectory + "getObject_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_5"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 404);
        
    }
    
    /**
     * Positive test case for getObjectMetaData method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateObjectWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getObjectMetadata} integration test with mandatory parameters.")
    public void testGetObjectMetaDataWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getObjectMetaData");
        String xmlRequestFilePath = pathToRequestsDirectory + "getObjectMetaData_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_4"),
                        amazons3ConnectorProperties.getProperty("objectName"));
        int statusCode =
                ConnectorIntegrationUtil.sendRequestToRetriveHeaders(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML);
        Assert.assertTrue(statusCode == 200);
        
    }
    
    /**
     * Negative test case for getObjectMetaData method.
     */
    @Test(dependsOnMethods = { "testCreateObjectWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {getObjectMetaData} integration test with negative case.")
    public void testGetObjectMetaDataWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getObjectMetaData");
        String xmlRequestFilePath = pathToRequestsDirectory + "getObjectMetaData_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_4"),
                        amazons3ConnectorProperties.getProperty("objectName"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 403);
        
    }
    
    /**
     * Positive test case for checkBucketPermission method with mandatory parameters.
     */
    @Test(dependsOnMethods = { "testCreateBucketForWebSiteConfig" }, groups = { "wso2.esb" }, description = "AmazonS3 {checkBucketPermission} integration test with mandatory parameters.")
    public void testCheckBucketPermissionWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:checkBucketPermission");
        String xmlRequestFilePath = pathToRequestsDirectory + "checkBucketPermission_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_3"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_7"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 200);
        
    }
    
    /**
     * Negative test case for checkBucketPermission method.
     */
    @Test(dependsOnMethods = { "testCreateBucketForWebSiteConfig" }, groups = { "wso2.esb" }, description = "AmazonS3 {checkBucketPermission} integration test with negative case.")
    public void testCheckBucketPermissionWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:checkBucketPermission");
        String xmlRequestFilePath = pathToRequestsDirectory + "checkBucketPermission_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 403);
        
    }
    
    /**
     * Mandatory parameter test case for deleteObject method.
     */
    @Test(dependsOnMethods = { "testGetObjectWithOptionalParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {deleteObject} integration test with mandatory parameter.")
    public void testDeleteObjectWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteObject");
        String xmlRequestFilePath = pathToRequestsDirectory + "deleteObject_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("objectName"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_4"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 204);
        
    }
    
    /**
     * Negative test case for deleteObject method.
     */
    @Test(dependsOnMethods = { "testGetObjectWithOptionalParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {deleteObject} integration test negative case.")
    public void testDeleteObjectWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteObject");
        String xmlRequestFilePath = pathToRequestsDirectory + "deleteObject_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("objectName"));
        Thread.sleep(Long.parseLong(amazons3ConnectorProperties.getProperty("timeOut")));
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 403);
        
    }
    
    /**
     * Mandatory parameter test case for deleteMultipleObjects method.
     */
    @Test(dependsOnMethods = { "toDeleteObject1", "toDeleteObject2" }, groups = { "wso2.esb" }, description = "AmazonS3 {deleteMultipleObjects} integration test with mandatory parameter.")
    public void testDeleteMultipleObjectsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteMultipleObjects");
        String xmlRequestFilePath = pathToRequestsDirectory + "deleteMultipleObjects_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_4"),
                        amazons3ConnectorProperties.getProperty("objectName_1"),
                        amazons3ConnectorProperties.getProperty("objectName_2"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 200);
        
    }
    
    /**
     * Negative test case for deleteMultipleObjects method.
     */
    @Test(dependsOnMethods = { "toDeleteObject1", "toDeleteObject2" }, groups = { "wso2.esb" }, description = "AmazonS3 {deleteMultipleObjects} integration test negative case.")
    public void testDeleteMultipleObjectsWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteMultipleObjects");
        String xmlRequestFilePath = pathToRequestsDirectory + "deleteMultipleObjects_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("objectName_1"),
                        amazons3ConnectorProperties.getProperty("objectName_2"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 403);
        
    }
    
    /**
     * Mandatory parameter test case for deleteBucketWebsite method.
     */
    @Test(dependsOnMethods = { "testGetWebSiteConfigurationWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {deleteBucketWebsite} integration test with mandatory parameter.")
    public void testDeleteBucketWebsiteWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteBucketWebsiteConfiguration");
        String xmlRequestFilePath = pathToRequestsDirectory + "deleteBucketWebsite_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_3"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_7"));
        Thread.sleep(Long.parseLong(amazons3ConnectorProperties.getProperty("timeOut")));
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 204);
        
    }
    
    /**
     * Negative test case for deleteBucketWebsite method.
     */
    @Test(dependsOnMethods = { "testGetWebSiteConfigurationWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {deleteBucketWebsite} integration test negative case.")
    public void testDeleteBucketWebsiteWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteBucketWebsiteConfiguration");
        String xmlRequestFilePath = pathToRequestsDirectory + "deleteBucketWebsite_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_3"));
        Thread.sleep(Long.parseLong(amazons3ConnectorProperties.getProperty("timeOut")));
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 403);
        
    }
    
    /**
     * Mandatory Parameter test case for deleteBucketPolicy method.
     */
    @Test(dependsOnMethods = { "testGetBucketPolicyWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {deleteBucketPolicy} integration test with mandatory parameters.")
    public void testDeleteBucketPolicyWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteBucketWebsiteConfiguration");
        String xmlRequestFilePath = pathToRequestsDirectory + "deleteBucketPolicy_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_4"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 204);
        
    }
    
    /**
     * Negative test case for deleteBucketPolicy method.
     */
    @Test(dependsOnMethods = { "testDeleteMultipleObjectsWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {deleteBucketPolicy} integration test with negative case.")
    public void testDeleteBucketPolicyWitNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteBucketWebsiteConfiguration");
        String xmlRequestFilePath = pathToRequestsDirectory + "deleteBucketPolicy_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"));
        Thread.sleep(Long.parseLong(amazons3ConnectorProperties.getProperty("timeOut")));
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 403);
        
    }
    
    /**
     * Mandatory Parameter test case for deleteBucket method.
     */
    @Test(dependsOnMethods = { "testCreateBucketWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {deleteBucket} integration test with mandatory parameters.")
    public void testDeleteBucketWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteBucket");
        String xmlRequestFilePath = pathToRequestsDirectory + "deleteBucket_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_1"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_3"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 204);
        
    }
    
    /**
     * Negative test case for deleteBucket method.
     */
    @Test(dependsOnMethods = { "testGetBucketsWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {deleteBucket} integration test with negative case.")
    public void testDeleteBucketWitNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:deleteBucket");
        String xmlRequestFilePath = pathToRequestsDirectory + "deleteBucket_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_1"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 403);
        
    }
    
    /**
     * Positive test case for initMultipartUpload method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateBucketWithOptionalParameters" }, description = "AmazonS3 {initMultipartUpload} integration test with mandatory parameters.")
    public void testInitMultipartUploadWithMandatoryParameters() throws Exception {
    
        final String uploadIDPreFix = "<UploadId>";
        final String uploadIDPostFix = "</UploadId>";
        esbRequestHeadersMap.put("Action", "urn:initMultipartUpload");
        String xmlRequestFilePath = pathToRequestsDirectory + "initMultipartUpload_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_5"),
                        amazons3ConnectorProperties.getProperty("objectName_6"));
        
        String response =
                ConnectorIntegrationUtil.getResponseViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        uploadIdMandatory =
                response.substring(response.indexOf(uploadIDPreFix) + uploadIDPreFix.length(),
                        response.indexOf(uploadIDPostFix));
        
        Assert.assertTrue(response.contains("InitiateMultipartUploadResult"));
        Assert.assertTrue(response.contains(amazons3ConnectorProperties.getProperty("objectName_6")));
    }
    
    /**
     * Positive test case for initMultipartUpload method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateBucketWithOptionalParameters" }, description = "AmazonS3 {initMultipartUpload} integration test with optional parameters.")
    public void testInitMultipartUploadWithOptionalParameters() throws Exception {
    
        final String uploadIDPreFix = "<UploadId>";
        final String uploadIDPostFix = "</UploadId>";
        esbRequestHeadersMap.put("Action", "urn:initMultipartUpload");
        String xmlRequestFilePath = pathToRequestsDirectory + "initMultipartUpload_optional.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_5"),
                        amazons3ConnectorProperties.getProperty("objectName_6"));
        
        String response =
                ConnectorIntegrationUtil.getResponseViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);

        uploadIdOptional =
                response.substring(response.indexOf(uploadIDPreFix) + uploadIDPreFix.length(),
                        response.indexOf(uploadIDPostFix));
        
        Assert.assertTrue(response.contains("InitiateMultipartUploadResult"));
        Assert.assertTrue(response.contains(amazons3ConnectorProperties.getProperty("objectName_6")));
    }
    
    /**
     * Negative test case for initMultipartUpload method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateBucketWithMandatoryParameters" }, description = "AmazonS3 {initMultipartUpload} integration test with negative case.")
    public void testInitMultipartUploadWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:initMultipartUpload");
        String xmlRequestFilePath = pathToRequestsDirectory + "initMultipartUpload_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_5"),
                        amazons3ConnectorProperties.getProperty("objectName_2"));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 403);
    }
    
    /**
     * Positive test case for abortMultipartUpload method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testInitMultipartUploadWithOptionalParameters" }, description = "AmazonS3 {abortMultipartUpload} integration test with mandatory parameters.")
    public void testAbortMultipartUplaodWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:abortMultipartUpload");
        
        final String xmlRequestFilePath = pathToRequestsDirectory + "abortMultipartUpload_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_5"),
                        amazons3ConnectorProperties.getProperty("objectName_6"), uploadIdOptional);
        
        final int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 204);
    }
    
    /**
     * Positive test case for abortMultipartUpload method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testInitMultipartUploadWithOptionalParameters" }, description = "AmazonS3 {abortMultipartUpload} integration test with optional parameters.")
    public void testAbortMultipartUplaodWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:abortMultipartUpload");
        
        final String xmlRequestFilePath = pathToRequestsDirectory + "abortMultipartUpload_optional.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_5"),
                        amazons3ConnectorProperties.getProperty("objectName_6"), uploadIdOptional);
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 204);
    }
    
    /**
     * Negative test case for abortMultipartUpload method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testInitMultipartUploadWithOptionalParameters" }, description = "AmazonS3 {abortMultipartUpload} integration test with negative case.")
    public void testAbortMultipartUplaodWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:abortMultipartUpload");
        String xmlRequestFilePath = pathToRequestsDirectory + "abortMultipartUpload_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        
        Thread.sleep(Long.parseLong(amazons3ConnectorProperties.getProperty("timeOut")));
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME), xmlString,
                        CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        
        Assert.assertTrue(statusCode == 403);
        
    }
    
    /**
     * Positive test case for getAuthorization method for upload part with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testInitMultipartUploadWithMandatoryParameters" }, description = "AmazonS3 {completeMultipartUpload} integration test with optional parameters.")
    public void testGetAuthorizationForUploadPartWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:getAuthorization");
        
        String xmlRequestFilePath = pathToRequestsDirectory + "getAuthorization_uploadPart_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("objectName_6"), uploadIdMandatory);
        
        Thread.sleep(2000);
        
        final String response =
                ConnectorIntegrationUtil.getResponseViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        
        if (response != null) {
            OMElement element = AXIOMUtil.stringToOM(response); 
	    Iterator<OMElement> iterator = element.getChildren();
	    while(iterator.hasNext()){
		OMElement childElement = (OMElement) iterator.next();
		String localName = childElement.getLocalName();
		if("authentication".equals(localName)){
 		   authorizationCode = childElement.getText(); 
		}else if("date".equals(localName)){
		   dateValue = childElement.getText();
		}  
	    }	      
        }
        
        Assert.assertNotNull(authorizationCode);
        Assert.assertNotNull(dateValue);
    }
    
    /**
     * Positive test case for uploadPart method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetAuthorizationForUploadPartWithMandatoryParameters" }, description = "AmazonS3 {uploadPart} integration test with mandatory parameters.")
    public void testUploadPartWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:uploadPart"); 

        esbRequestHeadersMap.put("Authorization", authorizationCode);
        esbRequestHeadersMap.put("x-amz-date", dateValue);
        esbRequestHeadersMap.put("Content-Type", "text/plain");
        
        final String uploadId = uploadIdMandatory;
        final String objectName = amazons3ConnectorProperties.getProperty("objectName_6");
        final String bucketUrl = amazons3ConnectorProperties.getProperty("bucketUrl_5");
        
        // Call through the ESB.
        final String requestString =
                getProxyServiceURL("multipart") + "?objectName=" + objectName + "&uploadId=" + uploadId
                        + "&partNumber=1&bucketUrl=" + bucketUrl;
        
        final MultipartFormdataProcessor multipartProcessor =
                new MultipartFormdataProcessor(requestString, esbRequestHeadersMap, "PUT");
        
        final File file = new File(pathToResourcesDirectory + "testFile1.txt");
        multipartProcessor.addFileToRequestPlain(file);
        
        final RestResponse<OMElement> esbRestResponse = multipartProcessor.process();
        
        Assert.assertTrue(esbRestResponse.getHttpStatusCode() == 200);
        Assert.assertNotNull(esbRestResponse.getHeadersMap().get("ETag"));
        
        uploadPartETag = esbRestResponse.getHeadersMap().get("ETag").get(0);
    }
    
    /**
     * Positive test case for listMultipartUploads method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testUploadPartWithMandatoryParameters" }, description = "AmazonS3 {listMultipartUploads} integration test with mandatory parameters.")
    public void testListMultipartUploadsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listMultipartUploads");
        String xmlRequestFilePath = pathToRequestsDirectory + "listMultipartUploads_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_5"));
        String response =
                ConnectorIntegrationUtil.getResponseViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(response.contains("ListMultipartUploadsResult"));
        Assert.assertTrue(response.contains("<Upload>"));
    }
    
    /**
     * Positive test case for listMultipartUploads method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListMultipartUploadsWithMandatoryParameters" }, description = "AmazonS3 {listMultipartUploads} integration test with optional parameters.")
    public void testListMultipartUploadsWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listMultipartUploads");
        String xmlRequestFilePath = pathToRequestsDirectory + "listMultipartUploads_optional.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_5"));
        
        Thread.sleep(Long.parseLong(amazons3ConnectorProperties.getProperty("timeOut")));
        
        String response =
                ConnectorIntegrationUtil.getResponseViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);

        Assert.assertTrue(response.contains("ListMultipartUploadsResult"));
        Assert.assertTrue(response.contains("<MaxUploads>1000</MaxUploads>"));
    }
    
    /**
     * Negative test case for listMultipartUploads method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListMultipartUploadsWithMandatoryParameters" }, description = "AmazonS3 {listMultipartUploads} integration test negative case with optional parameters.")
    public void testListMultipartUploadsWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listMultipartUploads");
        String xmlRequestFilePath = pathToRequestsDirectory + "listMultipartUploads_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_5"));
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 403);
    }
    
    /**
     * Positive test case for completeMultipartUpload method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListMultipartUploadsWithMandatoryParameters" }, description = "AmazonS3 {completeMultipartUpload} integration test with mandatory parameters.")
    public void testCompleteMultipartUplaodWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:completeMultipartUpload");
        
        String xmlRequestFilePath = pathToRequestsDirectory + "completeMultipartUpload_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("objectName_6"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_5"), uploadIdMandatory, uploadPartETag);
        
        Thread.sleep(Long.parseLong(amazons3ConnectorProperties.getProperty("timeOut")));
        
        final String response =
                ConnectorIntegrationUtil.getResponseViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(response.contains("ETag"));
        Assert.assertTrue(response.contains(amazons3ConnectorProperties.getProperty("objectName_6")));
    }
    
    /**
     * Positive test case for completeMultipartUpload method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListMultipartUploadsWithMandatoryParameters" }, description = "AmazonS3 {completeMultipartUpload} integration test with optional parameters.")
    public void testCompleteMultipartUplaodWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:completeMultipartUpload");
        
        String xmlRequestFilePath = pathToRequestsDirectory + "completeMultipartUpload_optional.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("objectName_6"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_5"), uploadIdMandatory, uploadPartETag);
        
        Thread.sleep(Long.parseLong(amazons3ConnectorProperties.getProperty("timeOut")));
        
        final String response =
                ConnectorIntegrationUtil.getResponseViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        
        Assert.assertTrue(response.contains("ETag"));
        Assert.assertTrue(response.contains(amazons3ConnectorProperties.getProperty("objectName_6")));
    }
    
    /**
     * Negative test case for completeMultipartUpload method.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListMultipartUploadsWithMandatoryParameters" }, description = "AmazonS3 {completeMultipartUpload} integration test with negative case.")
    public void testCompleteMultipartUplaodWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:completeMultipartUpload");
        String xmlRequestFilePath = pathToRequestsDirectory + "completeMultipartUpload_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        Thread.sleep(2000);
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME), xmlString,
                        CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        
        Assert.assertTrue(statusCode == 403);
        
    }
    
    /**
     * Mandatory parameter test case for ListParts method.
     */
    @Test(dependsOnMethods = { "testInitMultipartUploadWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {listParts} integration test with mandatory parameter.")
    public void testListPartsWithMandatoryParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listParts");
        String xmlRequestFilePath = pathToRequestsDirectory + "listParts_mandatory.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedxmlString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"), uploadIdMandatory,
                        amazons3ConnectorProperties.getProperty("objectName_6"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_5"));
        
        Thread.sleep(Long.parseLong(amazons3ConnectorProperties.getProperty("timeOut")));
        
        String response =
                ConnectorIntegrationUtil.getResponseViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedxmlString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);

        Assert.assertTrue(response.contains("ListPartsResult"));
        Assert.assertTrue(response.contains(amazons3ConnectorProperties.getProperty("bucketName_2")));
        
    }
    
    /**
     * Positive test case for ListParts method with optional parameters.
     */
    @Test(dependsOnMethods = { "testInitMultipartUploadWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {listParts} integration test with optional parameters.")
    public void testListPartsWithOptionalParameters() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listParts");
        String xmlRequestFilePath = pathToRequestsDirectory + "listParts_optional.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("encodingType"), uploadIdMandatory,
                        amazons3ConnectorProperties.getProperty("objectName_6"),
                        amazons3ConnectorProperties.getProperty("maxParts"),
                        amazons3ConnectorProperties.getProperty("partNumberMarker"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_5"));
        
        Thread.sleep(Long.parseLong(amazons3ConnectorProperties.getProperty("timeOut")));
        
        String response =
                ConnectorIntegrationUtil.getResponseViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(response.contains(amazons3ConnectorProperties.getProperty("encodingType")));
        Assert.assertTrue(response.contains(amazons3ConnectorProperties.getProperty("maxParts")));
    }
    
    /**
     * Negative test case for listPart method.
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = { "testInitMultipartUploadWithMandatoryParameters" }, groups = { "wso2.esb" }, description = "AmazonS3 {listParts} integration test with negative case.")
    public void testListPartsWithNegativeCase() throws Exception {
    
        esbRequestHeadersMap.put("Action", "urn:listParts");
        String xmlRequestFilePath = pathToRequestsDirectory + "listParts_negative.txt";
        
        final String xmlString = ConnectorIntegrationUtil.getFileContent(xmlRequestFilePath);
        final String modifiedXMLString =
                String.format(xmlString, amazons3ConnectorProperties.getProperty("accessKeyId"),
                        amazons3ConnectorProperties.getProperty("secretAccessKey"),
                        amazons3ConnectorProperties.getProperty("bucketName_2"),
                        amazons3ConnectorProperties.getProperty("objectName_6"),
                        amazons3ConnectorProperties.getProperty("bucketUrl_5"));
        
        Thread.sleep(Long.parseLong(amazons3ConnectorProperties.getProperty("timeOut")));
        
        int statusCode =
                ConnectorIntegrationUtil.sendRequestViaSingleProxy(getProxyServiceURL(CONNECTOR_NAME),
                        modifiedXMLString, CONTENT_TYPE_APPLICATION_XML, esbRequestHeadersMap);
        Assert.assertTrue(statusCode == 403);
        
    } 
}
