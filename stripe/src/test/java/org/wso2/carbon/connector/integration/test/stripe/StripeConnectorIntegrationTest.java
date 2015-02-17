package org.wso2.carbon.connector.integration.test.stripe;

import java.lang.String;
import java.lang.System;
import java.net.URL;
import java.util.Properties;
import javax.activation.DataHandler;

import net.minidev.json.JSONArray;
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

public class StripeConnectorIntegrationTest extends ESBIntegrationTest {
   private static final String CONNECTOR_NAME = "stripe";

   private MediationLibraryUploaderStub mediationLibUploadStub = null;

   private MediationLibraryAdminServiceStub adminServiceStub = null;

   private ProxyServiceAdminClient proxyAdmin;

   private String repoLocation = null;

   private String stripeConnectorFileName = "stripe-connector-1.0.0.zip";

   private Properties stripeConnectorProperties = null;

   private String pathToProxiesDirectory = null;

   private String pathToRequestsDirectory = null;

   private String apiUrl = "https://api.stripe.com/v1/";

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

      ConnectorIntegrationUtil.uploadConnector(repoLocation, mediationLibUploadStub, stripeConnectorFileName);
      log.info("Sleeping for " + 30000 / 1000 + " seconds while waiting for synapse import");
      Thread.sleep(30000);

      adminServiceStub.updateStatus("{org.wso2.carbon.connector}" + CONNECTOR_NAME, CONNECTOR_NAME,
            "org.wso2.carbon.connector", "enabled");

      stripeConnectorProperties = ConnectorIntegrationUtil.getConnectorConfigProperties(CONNECTOR_NAME);

      pathToProxiesDirectory = repoLocation + stripeConnectorProperties.getProperty("proxyDirectoryRelativePath");
      pathToRequestsDirectory = repoLocation + stripeConnectorProperties.getProperty("requestDirectoryRelativePath");

   }

   @Override
   protected void cleanup() {
      axis2Client.destroy();
   }

   /**
    * Positive test case with mandatory parameters for createTokenForCard method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createACardToken} integration test with mandatory parameters.")
   public void testCreateTokenForCardWithMandatoryParameter() throws Exception {

      String jsonRequestFilePath = pathToRequestsDirectory + "createTokenForCard_Mandatory.txt";
      String methodName = "stripe_createTokenForCard";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("grantType"), stripeConnectorProperties.getProperty("code"), stripeConnectorProperties.getProperty("redirectUri"), stripeConnectorProperties.getProperty("clientSecret"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("card").getString("exp_month"), jsonObject.get("expMonth"));
         Assert.assertEquals(jsonResponse.getJSONObject("card").getString("exp_year"), jsonObject.get("expYear"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with all optional parameters for createTokenForCard method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createTokenForCard} integration test with all optional parameters.")
   public void testCreateTokenForCardWithOptionalParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createTokenForCard_Optional.txt";
      String methodName = "stripe_createTokenForCard";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("grantType"), stripeConnectorProperties.getProperty("refreshToken"), stripeConnectorProperties.getProperty("clientSecret"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("card").getString("exp_month"), jsonObject.get("expMonth"));
         Assert.assertEquals(jsonResponse.getJSONObject("card").getString("exp_year"), jsonObject.get("expYear"));
         Assert.assertEquals(jsonResponse.getJSONObject("card").getString("name"), jsonObject.get("name"));
         Assert.assertEquals(jsonResponse.getJSONObject("card").getString("address_line1"), jsonObject.get("addressLine1"));
         Assert.assertEquals(jsonResponse.getJSONObject("card").getString("address_line2"), jsonObject.get("addressLine2"));
         Assert.assertEquals(jsonResponse.getJSONObject("card").getString("address_city"), jsonObject.get("city"));
         Assert.assertEquals(jsonResponse.getJSONObject("card").getString("address_state"), jsonObject.get("state"));
         Assert.assertEquals(jsonResponse.getJSONObject("card").getString("address_zip"), jsonObject.get("zip"));
         Assert.assertEquals(jsonResponse.getJSONObject("card").getString("address_country"), jsonObject.get("country"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with the optional parameter name for createTokenForCard method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createTokenForCard} integration test with the optional parameter name.")
   public void testCreateTokenForCardWithOptionalParameterName() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createTokenForCard_WithNameOptional.txt";
      String methodName = "stripe_createTokenForCard";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("grantType"), stripeConnectorProperties.getProperty("refreshToken"), stripeConnectorProperties.getProperty("clientSecret"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("card").getString("exp_month"), jsonObject.get("expMonth"));
         Assert.assertEquals(jsonResponse.getJSONObject("card").getString("exp_year"), jsonObject.get("expYear"));
         Assert.assertEquals(jsonResponse.getJSONObject("card").getString("name"), jsonObject.get("name"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with the optional parameter addressLine1 and addressLine2 for createTokenForCard method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createTokenForCard} integration test with the optional parameter name.")
   public void testCreateTokenForCardWithOptionalParameterAddress() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createTokenForCard_WithAddressOptional.txt";
      String methodName = "stripe_createTokenForCard";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("grantType"), stripeConnectorProperties.getProperty("refreshToken"), stripeConnectorProperties.getProperty("clientSecret"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("card").getString("exp_month"), jsonObject.get("expMonth"));
         Assert.assertEquals(jsonResponse.getJSONObject("card").getString("exp_year"), jsonObject.get("expYear"));
         Assert.assertEquals(jsonResponse.getJSONObject("card").getString("address_line1"), jsonObject.get("addressLine1"));
         Assert.assertEquals(jsonResponse.getJSONObject("card").getString("address_line2"), jsonObject.get("addressLine2"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for createTokenForCard method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createTokenForCard} integration test for negative case.")
   public void testCreateTokenForCardWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createTokenForCard_Negative.txt";
      String methodName = "stripe_createTokenForCard";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("grantType"), stripeConnectorProperties.getProperty("refreshToken"), stripeConnectorProperties.getProperty("clientSecret"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "Your card number is incorrect.");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with mandatory parameters for createTokenForBankAccount method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createTokenForBankAccount} integration test with mandatory parameters.")
   public void testCreateTokenForBankAccountWithMandatoryParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createABankAccountToken_Positive.txt";
      String methodName = "stripe_createTokenForBankAccount";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertTrue(jsonResponse.getJSONObject("bank_account").has("id"));
         Assert.assertEquals(jsonResponse.getString("object"), "token");
         Assert.assertEquals(jsonResponse.getJSONObject("bank_account").getString("country"), jsonObject.get("country"));
         Assert.assertEquals(jsonResponse.getJSONObject("bank_account").getString("routing_number"), jsonObject.get("routingNumber"));

      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for createTokenForBankAccount method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createTokenForBankAccount} integration test with negative case.")
   public void testCreateACardTokenWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createABankAccountToken_Negative.txt";
      String methodName = "stripe_createTokenForBankAccount";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("type"), "invalid_request_error");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for retrieveAnExistingToken method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingToken} integration test with positive case.")
   public void testRetrieveAnExistingTokenWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnExistingToken_Positive.txt";
      String methodName = "stripe_retrieveAnExistingToken";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("tokenId"));
         Assert.assertEquals(jsonResponse.getString("object"), "token");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for retrieveToken method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingToken} integration test with negative case.")
   public void testRetrieveAnExistingTokenWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnExistingToken_Negative.txt";
      String methodName = "stripe_retrieveAnExistingToken";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "There is no token with ID " + jsonObject.get("tokenId") + ".");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with mandatory parameters for createANewCustomer method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewCustomer} integration test with card parameters.")
   public void testCreateANewCustomerWithCardParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewCustomer_WithCard.txt";
      String methodName = "stripe_createANewCustomer";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("description"), jsonObject.get("description"));
         Assert.assertEquals(jsonResponse.getString("object"), "customer");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with optional parameters for createANewCustomer method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewCustomer} integration test with optional parameters.")
   public void testCreateANewCustomerWithOptionalParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewCustomer_Optional.txt";
      String methodName = "stripe_createANewCustomer";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("description"), jsonObject.get("description"));
         Assert.assertEquals(jsonResponse.getString("email"), jsonObject.get("email"));
         Assert.assertEquals(jsonResponse.getJSONObject("metadata").getString("checked"), jsonObject.getJSONObject("metadata").get("checked"));
         Assert.assertEquals(jsonResponse.getString("account_balance"), jsonObject.get("accountBalance"));
         Assert.assertEquals(jsonResponse.getJSONObject("discount").getJSONObject("coupon").getString("id"), jsonObject.get("coupon"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with plan for createANewCustomer method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewCustomer} integration test with plan optional parameters.")
   public void testCreateANewCustomerWithPlanOptionalParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewCustomer_WithPlan.txt";
      String methodName = "stripe_createANewCustomer";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("description"), jsonObject.get("description"));
         Assert.assertEquals(jsonResponse.getString("email"), jsonObject.get("email"));
         Assert.assertEquals(jsonResponse.getJSONObject("metadata").getString("checked"),
               jsonObject.getJSONObject("metadata").get("checked"));
         Assert.assertTrue(jsonResponse.has("id"));
         Assert.assertEquals(jsonResponse.getJSONObject("subscriptions").getJSONArray("data").getJSONObject(0).getJSONObject("plan").getString("id"),
               jsonObject.get("plan"));
         Assert.assertEquals(jsonResponse.getJSONObject("subscriptions").getJSONArray("data").getJSONObject(0).getString("trial_end"),
               jsonObject.get("trialEnd"));
         Assert.assertEquals(jsonResponse.getJSONObject("subscriptions").getJSONArray("data").getJSONObject(0).getString("quantity"),
               jsonObject.get("quantity"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for createANewCustomer method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewCustomer} integration test with negative case.")
   public void testCreateANewCustomerWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewCustomer_Negative.txt";
      String methodName = "stripe_createANewCustomer";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "There is no token with ID " + jsonObject.get("card")+".");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for retrieveAnExistingCustomer method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingCustomer} integration test with positive case.")
   public void testRetrieveAnExistingCustomerWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnExistingCustomer_Positive.txt";
      String methodName = "stripe_retrieveAnExistingCustomer";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("customerId"));
         Assert.assertEquals(jsonResponse.getString("object"), "customer");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for retrieveAnExistingCustomer method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingCustomer} integration test with negative case.")
   public void testRetrieveAnExistingCustomerWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnExistingCustomer_Negative.txt";
      String methodName = "stripe_retrieveAnExistingCustomer";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such customer: " + jsonObject.get("customerId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with mandatory parameters for updateACustomerDetails method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateACustomerDetails} integration test with mandatory parameters.")
   public void testUpdateACustomerDetailsWithCardParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateACustomerDetails_WithCard.txt";
      String methodName = "stripe_updateACustomerDetails";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("description"), jsonObject.get("description"));
         Assert.assertEquals(jsonResponse.getString("object"), "customer");
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with optional parameters for updateACustomerDetails method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateACustomerDetails} integration test with optional parameters.")
   public void testUpdateACustomerDetailWithOptionalParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateACustomerDetails_Optional.txt";
      String methodName = "stripe_updateACustomerDetails";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("description"), jsonObject.get("description"));
         Assert.assertEquals(jsonResponse.getString("email"), jsonObject.get("email"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for updateACustomerDetails method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateACustomerDetails} integration test with negative case.")
   public void testUpdateACustomerDetailWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateACustomerDetails_negative.txt";
      String methodName = "stripe_updateACustomerDetails";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such customer: " + jsonObject.get("customerId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for deleteAnExistingCustomer method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {deleteAnExistingCustomer} integration test with positive case.")
   public void testDeleteAnExistingCustomerWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "deleteAnExistingCustomer_Positive.txt";
      String methodName = "stripe_deleteAnExistingCustomer";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("customerId"));
         Assert.assertEquals(jsonResponse.getString("deleted"), "true");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for deleteAnExistingCustomer method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {deleteAnExistingCustomer} integration test with negative case.")
   public void testDeleteAnExistingCustomerWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "deleteAnExistingCustomer_Negative.txt";
      String methodName = "stripe_deleteAnExistingCustomer";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such customer: " + jsonObject.get("customerId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for getAListOfAllCustomers method with mandatory parameters.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getAListOfAllCustomers} integration test with mandatory parameters.")
   public void testGetAListOfAllCustomersWithPositiveCase() throws Exception {
      String methodName = "stripe_getAListOfAllCustomers";
      String modifiedJsonString = String.format("{\"apiKey\":\"%s\"}", stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/customers");
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case for getAListOfAllCustomers method with optional parameters.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getAListOfAllCustomers} integration test with optional parameters.")
   public void testGetAListOfAllCustomersWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "getAListOfAllCustomers_Optional.txt";
      String methodName = "stripe_getAListOfAllCustomers";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/customers");
         Assert.assertEquals(jsonResponse.getJSONArray("data").length(), jsonObject.get("limit"));
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case with card parameter for createANewCharge method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewCharge} integration test with card parameters.")
   public void testCreateANewChargeWithCardParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewCharge_WithCard.txt";
      String methodName = "stripe_createANewCharge";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("amount"), jsonObject.get("amount"));
         Assert.assertEquals(jsonResponse.getString("currency"), jsonObject.get("currency"));
         Assert.assertEquals(jsonResponse.getString("description"), jsonObject.get("description"));
         Assert.assertEquals(jsonResponse.getString("object"), "charge");

      } finally {

         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with customer parameter for createANewCharge method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewCharge} integration test with customer parameters.")
   public void testCreateANewChargeWithCustomerParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewCharge_WithCustomer.txt";
      String methodName = "stripe_createANewCharge";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("amount"), jsonObject.get("amount"));
         Assert.assertEquals(jsonResponse.getString("currency"), jsonObject.get("currency"));
         Assert.assertEquals(jsonResponse.getString("description"), jsonObject.get("description"));
         Assert.assertEquals(jsonResponse.getString("object"), "charge");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with optional parameters for createANewCharge method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewCharge} integration test with optional parameters.")
   public void testCreateANewChargeWithOptionalParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewCharge_Optional.txt";
      String methodName = "stripe_createANewCharge";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("amount"), jsonObject.get("amount"));
         Assert.assertEquals(jsonResponse.getString("currency"), jsonObject.get("currency"));
         Assert.assertEquals(jsonResponse.getString("description"), jsonObject.get("description"));
         Assert.assertEquals(jsonResponse.getJSONObject("shipping").getString("name"), jsonObject.get("shippingName"));
         Assert.assertEquals(jsonResponse.getJSONObject("shipping").getString("tracking_number"), jsonObject.get("trackingNumber"));
         Assert.assertEquals(jsonResponse.getJSONObject("shipping").getString("carrier"), jsonObject.get("carrier"));
         Assert.assertEquals(jsonResponse.getString("object"), "charge");

      } finally {

         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with optional parameters for createANewCharge method.
    */
   @Test(enabled = true, groups = {"wso2.esb"}, description = "stripe {createANewCharge} integration test with optional parameters.")
   public void testCreateANewChargeWithApplicationFeeParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewCharge_WithApplicationFee.txt";
      String methodName = "stripe_createANewCharge_WithAuthToken";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("grantType"), stripeConnectorProperties.getProperty("code"), stripeConnectorProperties.getProperty("redirectUri"), stripeConnectorProperties.getProperty("clientSecret"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("amount"), jsonObject.get("amount"));
         Assert.assertEquals(jsonResponse.getString("currency"), jsonObject.get("currency"));
         Assert.assertEquals(jsonResponse.getString("object"), "charge");

      } finally {

         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for createANewCharge method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewCharge} integration test with negative case.")
   public void testCreateANewChargeWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewCharge_Negative.txt";
      String methodName = "stripe_createANewCharge";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such customer: " + jsonObject.get("customer"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for retrieveAnExistingCharge method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingCharge} integration test with positive case.")
   public void testRetrieveAnExistingChargeWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnExistingCharge_Positive.txt";
      String methodName = "stripe_retrieveAnExistingCharge";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("chargeId"));
         Assert.assertEquals(jsonResponse.getString("object"), "charge");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for retrieveAnExistingCharge method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingCharge} integration test with negative case.")
   public void testRetrieveAnExistingChargeWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnExistingCharge_Negative.txt";
      String methodName = "stripe_retrieveAnExistingCharge";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such charge: " + jsonObject.get("chargeId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for updateAChargeDetails method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAChargeDetails} integration test with positive case.")
   public void testUpdateAChargeDetailsWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAChargeDetails_Positive.txt";
      String methodName = "stripe_updateAChargeDetails";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("chargeId"));
         Assert.assertEquals(jsonResponse.getString("description"), jsonObject.get("description"));
         Assert.assertEquals(jsonResponse.getString("object"), "charge");
         Assert.assertEquals(jsonResponse.getJSONObject("metadata").getString("firstName"), jsonObject.getJSONObject("metadata").get("firstName"));
         Assert.assertEquals(jsonResponse.getJSONObject("fraud_details").getString("user_report"), jsonObject.getJSONObject("fraudDetails").get("user_report"));

      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for updateAChargeDetails method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAChargeDetails} integration test with negative case.")
   public void testUpdateChargeDetailsWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAChargeDetails_Negative.txt";
      String methodName = "stripe_updateAChargeDetails";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "There is no payment with ID " + jsonObject.get("chargeId") + ".");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for getAListOfAllCharges method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getAListOfAllCharges} integration test with positive test case.")
   public void testGetAListOfAllChargesWithPositiveCase() throws Exception {
      String methodName = "stripe_getAListOfAllCharges";
      String modifiedJsonString = String.format("{\"apiKey\":\"%s\"}", stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/charges");
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * //    *Positive test case for getAListOfAllCharges method.
    * //
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getAListOfAllCharges} integration test with optional parameters.")
   public void testGetAListOfAllChargesWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "getAListOfAllCharges_Optional.txt";
      String methodName = "stripe_getAListOfAllCharges";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/charges");
         Assert.assertEquals(jsonResponse.getJSONArray("data").length(), jsonObject.get("limit"));
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case for captureACharge method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {captureACharge} integration test with positive case.")
   public void testCaptureAChargeWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "captureACharge_Positive.txt";
      String methodName = "stripe_captureACharge";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("chargeId"));
         Assert.assertEquals(jsonResponse.getString("captured"), "true");
         Assert.assertEquals(jsonResponse.getString("object"), "charge");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for captureACharge method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {captureACharge} integration test with negative case.")
   public void testCaptureAChargeWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "captureACharge_Negative.txt";
      String methodName = "stripe_captureACharge";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such charge: " + jsonObject.get("chargeId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with mandatory parameters for createANewCardForACustomer method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewCardForACustomer} integration test with card parameters.")
   public void testCreateANewCardForACustomerWithCardParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewCardForACustomer_WithCard.txt";
      String methodName = "stripe_createANewCardForACustomer";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("customer"), jsonObject.get("customerId"));
         Assert.assertEquals(jsonResponse.getString("object"), "card");
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with optional parameters for createANewCardForACustomer method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewCardForACustomer} integration test with optional parameters.")
   public void testCreateANewCardForACustomerWithOptionalParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewCardForACustomer_Optional.txt";
      String methodName = "stripe_createANewCardForACustomer";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("customer"), jsonObject.get("customerId"));
         Assert.assertEquals(jsonResponse.getString("object"), "card");

      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for createANewCardForACustomer method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewCardForACustomer} integration test with negative case.")
   public void testCreateANewCardForACustomerWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewCardForACustomer_Negative.txt";
      String methodName = "stripe_createANewCardForACustomer";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such customer: " + jsonObject.get("customerId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for retrieveAnExistingCard method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingCard} integration test with positive case.")
   public void testRetrieveAnExistingCardWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnExistingCard_Positive.txt";
      String methodName = "stripe_retrieveAnExistingCard";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("cardId"));
         Assert.assertEquals(jsonResponse.getString("customer"), jsonObject.get("customerId"));
         Assert.assertEquals(jsonResponse.getString("object"), "card");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for retrieveAnExistingCard method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingCard} integration test with negative case.")
   public void testRetrieveAnExistingCardWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnExistingCard_Negative.txt";
      String methodName = "stripe_retrieveAnExistingCard";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such customer: " + jsonObject.get("customerId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for updateACardDetails method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateACardDetails} integration test with positive case.")
   public void testUpdateACardDetailsWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateACardDetails_Positive.txt";
      String methodName = "stripe_updateACardDetails";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("cardId"));
         Assert.assertEquals(jsonResponse.getString("customer"), jsonObject.get("customerId"));
         Assert.assertEquals(jsonResponse.getString("name"), jsonObject.get("name"));
         Assert.assertEquals(jsonResponse.getString("address_line1"), jsonObject.get("addressLine1"));
         Assert.assertEquals(jsonResponse.getString("address_line2"), jsonObject.get("addressLine2"));
         Assert.assertEquals(jsonResponse.getString("name"), jsonObject.get("name"));
         Assert.assertEquals(jsonResponse.getString("object"), "card");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for updateACardDetails method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateACardDetails} integration test with negative case.")
   public void testUpdateACardDetailsNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateACardDetails_Negative.txt";
      String methodName = "stripe_updateACardDetails";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such customer: " + jsonObject.get("customerId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
   * Positive test case for deleteAnExistingCard method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {deleteAnExistingCard} integration test with positive case.")
   public void testDeleteAnExistingCardWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "deleteAnExistingCard_Positive.txt";
      String methodName = "stripe_deleteAnExistingCard";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("cardId"));
         Assert.assertEquals(jsonResponse.getString("deleted"), "true");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for deleteAnExistingCard method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {deleteAnExistingCard} integration test with negative case.")
   public void testDeleteAnExistingCardWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "deleteAnExistingCard_Negative.txt";
      String methodName = "stripe_deleteAnExistingCard";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such customer: " + jsonObject.get("customerId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for getAListOfAllCards method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getAListOfAllCharges} integration test with positive test case.")
   public void testGetAListOfAllCardsWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "getAListOfAllCards_Optional.txt";
      String methodName = "stripe_getAListOfAllCards";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/customers/" + jsonObject.get("customerId") + "/cards");
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for getAListOfAllCards method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getAListOfAllCharges} integration test with optional parameters.")
   public void testGetAListOfAllCardsWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "getAListOfAllCards_Optional.txt";
      String methodName = "stripe_getAListOfAllCards";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/customers/" + jsonObject.get("customerId") + "/cards");
         Assert.assertEquals(jsonResponse.getJSONArray("data").length(), jsonObject.get("limit"));
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with mandatory parameters for createANewRefund method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewRefund} integration test with card parameters.")
   public void testCreateANewRefundWithCardParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewRefund_Positive.txt";
      String methodName = "stripe_createANewRefund";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("charge"), jsonObject.get("chargeId"));
         Assert.assertTrue(jsonResponse.has("id"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with optional parameters for createANewRefund method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewRefund} integration test with optional parameters.")
   public void testCreateANewRefundWithOptionalParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewRefund_Optional.txt";
      String methodName = "stripe_createANewRefund";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("amount"), jsonObject.get("amount"));
         Assert.assertEquals(jsonResponse.getString("charge"), jsonObject.get("chargeId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for createANewRefund method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewRefund} integration test with negative case.")
   public void testCreateANewRefundForACustomerWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewRefund_Negative.txt";
      String methodName = "stripe_createANewRefund";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "There is no payment with ID " + jsonObject.get("chargeId") + ".");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for getAListOfAllRefunds method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getAListOfAllRefunds} integration test with positive test case.")
   public void testGetAListOfAllRefundsWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "getAListOfAllRefunds_Positive.txt";
      String methodName = "stripe_getAListOfAllRefunds";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/charges/" + jsonObject.get("chargeId") + "/refunds");
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for getAListOfAllRefunds method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getAListOfAllRefunds} integration test with optional parameters.")
   public void testGetAListOfAllRefundsWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "getAListOfAllRefunds_Optional.txt";
      String methodName = "stripe_getAListOfAllRefunds";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/charges/" + jsonObject.get("chargeId") + "/refunds");
         Assert.assertEquals(jsonResponse.getJSONArray("data").length(), jsonObject.get("limit"));
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for getAListOfAllRefunds method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getAListOfAllRefunds} integration test with negative case.")
   public void testGetAListOfAllRefundsWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "getAListOfAllRefunds_Negative.txt";
      String methodName = "stripe_getAListOfAllRefunds";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such charge: " + jsonObject.get("chargeId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for retrieveAnExistingRefund method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingRefund} integration test with positive case.")
   public void testRetrieveAnExistingRefundWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnExistingRefund_Positive.txt";
      String methodName = "stripe_retrieveAnExistingRefund";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("refundId"));
         Assert.assertEquals(jsonResponse.getString("charge"), jsonObject.get("chargeId"));
         Assert.assertEquals(jsonResponse.getString("object"), "refund");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for retrieveAnExistingRefund method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingRefund} integration test with negative case.")
   public void testRetrieveAnExistingRefundWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnExistingRefund_Negative.txt";
      String methodName = "stripe_retrieveAnExistingRefund";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such refund: " + jsonObject.get("refundId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for updateRefundDetails method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateRefundDetails} integration test with positive case.")
   public void testUpdateRefundDetailsWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateRefundDetails_Positive.txt";
      String methodName = "stripe_updateRefundDetails";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("refundId"));
         Assert.assertEquals(jsonResponse.getString("charge"), jsonObject.get("chargeId"));
         Assert.assertEquals(jsonResponse.getString("object"), "refund");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for updateRefundDetails method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateRefundDetails} integration test with optional parameters.")
   public void testUpdateRefundDetailsWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateRefundDetails_Optional.txt";
      String methodName = "stripe_updateRefundDetails";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("refundId"));
         Assert.assertEquals(jsonResponse.getString("charge"), jsonObject.get("chargeId"));
         Assert.assertEquals(jsonResponse.getJSONObject("metadata").getString("number"), jsonObject.getJSONObject("metadata").get("number"));
         Assert.assertEquals(jsonResponse.getString("object"), "refund");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for updateRefundDetails method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateRefundDetails} integration test with negative case.")
   public void testUpdateRefundDetailsWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateRefundDetails_Negative.txt";
      String methodName = "stripe_updateRefundDetails";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such refund: " + jsonObject.get("refundId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with mandatory parameters for createANewRecipient method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewRecipient} integration test with mandatory parameters.")
   public void testCreateANewRecipientWithMandatoryParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewRecipient_Mandatory.txt";
      String methodName = "stripe_createANewRecipient";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "recipient");
         Assert.assertEquals(jsonResponse.getString("type"), jsonObject.get("type"));
         Assert.assertEquals(jsonResponse.getString("name"), jsonObject.get("recipientName"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with optional parameter bankAccount for createANewRecipient method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewRecipient} integration test with bankAccount optional parameters.")
   public void testCreateANewRecipientWithBankAccountParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewRecipient_BankAccountOptional.txt";
      String methodName = "stripe_createANewRecipient";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "recipient");
         Assert.assertEquals(jsonResponse.getString("type"), jsonObject.get("type"));
         Assert.assertEquals(jsonResponse.getString("name"), jsonObject.get("recipientName"));
         Assert.assertEquals(jsonResponse.getJSONObject("active_account").getString("object"), "bank_account");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with optional parameter card for createANewRecipient method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewRecipient} integration test with card optional parameters.")
   public void testCreateANewRecipientWithCardParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewRecipient_cardOptional.txt";
      String methodName = "stripe_createANewRecipient";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "recipient");
         Assert.assertEquals(jsonResponse.getString("type"), jsonObject.get("type"));
         Assert.assertEquals(jsonResponse.getString("name"), jsonObject.get("recipientName"));
         Assert.assertTrue(Integer.parseInt(jsonResponse.getJSONObject("cards").getString("total_count")) > 0);

      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case with optional parameters of card details for createANewRecipient method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewRecipient} integration test with  optional parameters of card details.")
   public void testCreateANewRecipientWithCardDetailsParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewRecipient_cardDetailsOptional.txt";
      String methodName = "stripe_createANewRecipient";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "recipient");
         Assert.assertEquals(jsonResponse.getString("type"), jsonObject.get("type"));
         Assert.assertEquals(jsonResponse.getString("name"), jsonObject.get("recipientName"));
         Assert.assertTrue(Integer.parseInt(jsonResponse.getJSONObject("cards").getString("total_count")) > 0);

      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Negative test case for createANewRecipient method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewRecipient} integration test with  optional parameters of card details.")
   public void testCreateANewRecipientWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewRecipient_Negative.txt";
      String methodName = "stripe_createANewRecipient";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "Name must contain first name and last name.");

      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case for retrieveAnExistingRecipient method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingRecipient} integration test with positive case.")
   public void testRetrieveAnExistingRecipientWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnExistingRecipient_Positive.txt";
      String methodName = "stripe_retrieveAnExistingRecipient";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("recipientId"));
         Assert.assertEquals(jsonResponse.getString("object"), "recipient");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for retrieveAnExistingCustomer method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingCustomer} integration test with negative case.")
   public void testRetrieveAnExistingRecipientWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnExistingRecipient_Negative.txt";
      String methodName = "stripe_retrieveAnExistingRecipient";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such recipient: " + jsonObject.get("recipientId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with mandatory parameters for updateARecipientDetails method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateACustomerDetails} integration test with mandatory parameters.")
   public void testUpdateARecipientDetailsWithCardParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateARecipientDetails_Mandatory.txt";
      String methodName = "stripe_updateARecipientDetails";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("recipientId"));
         Assert.assertEquals(jsonResponse.getString("object"), "recipient");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with optional parameters for updateARecipientDetails method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateARecipientDetails} integration test with optional parameters.")
   public void testUpdateARecipientDetailWithOptionalParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateARecipientDetails_Optional.txt";
      String methodName = "stripe_updateARecipientDetails";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("recipientId"));
         Assert.assertEquals(jsonResponse.getString("description"), jsonObject.get("description"));
         Assert.assertEquals(jsonResponse.getString("name"), jsonObject.get("recipientName"));
         Assert.assertEquals(jsonResponse.getString("email"), jsonObject.get("email"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for updateARecipientDetails method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateACustomerDetails} integration test with negative case.")
   public void testUpdateARecipientDetailWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateARecipientDetails_negative.txt";
      String methodName = "stripe_updateARecipientDetails";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such recipient: " + jsonObject.get("recipientId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for deleteAnExistingRecipient method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {deleteAnExistingRecipient} integration test with positive case.")
   public void testDeleteAnExistingRecipientWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "deleteAnExistingRecipient_Positive.txt";
      String methodName = "stripe_deleteAnExistingRecipient";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("recipientId"));
         Assert.assertEquals(jsonResponse.getString("deleted"), "true");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for deleteAnExistingRecipient method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {deleteAnExistingRecipient} integration test with negative case.")
   public void testDeleteAnExistingRecipientWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "deleteAnExistingRecipient_Negative.txt";
      String methodName = "stripe_deleteAnExistingRecipient";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such recipient: " + jsonObject.get("recipientId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for getAListOfAllRecipients method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getAListOfAllRecipients} integration test with mandatory parameters.")
   public void testGetAListOfAllRecipientsWithPositiveCase() throws Exception {
      String methodName = "stripe_getAListOfAllRecipients";
      String modifiedJsonString = String.format("{\"apiKey\":\"%s\"}", stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/recipients");
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case for getAListOfAllRecipients method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getAListOfAllRecipients} integration test with optional parameters.")
   public void testGetAListOfAllRecipientsWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "getAListOfAllRecipients_Optional.txt";
      String methodName = "stripe_getAListOfAllRecipients";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/recipients");
         Assert.assertEquals(jsonResponse.getJSONArray("data").length(), jsonObject.get("limit"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with mandatory parameters for createANewInvoiceItem method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewInvoiceItem} integration test with mandatory parameters.")
   public void testCreateANewInvoiceItemWithMandatoryParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewInvoiceItem_Mandatory.txt";
      String methodName = "stripe_createANewInvoiceItem";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("amount"), jsonObject.get("amount"));
         Assert.assertEquals(jsonResponse.getString("currency"), jsonObject.get("currency"));
         Assert.assertEquals(jsonResponse.getString("object"), "invoiceitem");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with optional parameters for createANewInvoiceItem method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewInvoiceItem} integration test with optional parameters.")
   public void testCreateANewInvoiceItemWithOptionalParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewInvoiceItem_Optional.txt";
      String methodName = "stripe_createANewInvoiceItem";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("amount"), jsonObject.get("amount"));
         Assert.assertEquals(jsonResponse.getString("currency"), jsonObject.get("currency"));
         Assert.assertEquals(jsonResponse.getString("object"), "invoiceitem");
         Assert.assertEquals(jsonResponse.getString("description"), jsonObject.get("description"));
         Assert.assertEquals(jsonResponse.getJSONObject("metadata").getString("checked"), jsonObject.getJSONObject("metadata").get("checked"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for createANewInvoiceItem method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewInvoiceItem} integration test with negative case.")
   public void testCreateANewInvoiceItemWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewInvoiceItem_Negative.txt";
      String methodName = "stripe_createANewInvoiceItem";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such customer: " + jsonObject.get("customer"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for retrieveAnInvoiceItem method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnInvoiceItem} integration test with positive case.")
   public void testRetrieveAnInvoiceItemWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnInvoiceItem_Positive.txt";
      String methodName = "stripe_retrieveAnInvoiceItem";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("invoiceItemId"));
         Assert.assertEquals(jsonResponse.getString("object"), "invoiceitem");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for retrieveAnInvoiceItem method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnInvoiceItem} integration test with negative case.")
   public void testRetrieveAnInvoiceItemWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnInvoiceItem_Negative.txt";
      String methodName = "stripe_retrieveAnInvoiceItem";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such invoiceitem: " + jsonObject.get("invoiceItemId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with mandatory parameters for updateAnInvoiceItem method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnInvoiceItem} integration test with mandatory parameters.")
   public void testUpdateAnInvoiceItemWithMandatoryParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnInvoiceItem_Mandatory.txt";
      String methodName = "stripe_updateAnInvoiceItem";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("invoiceItemId"));
         Assert.assertEquals(jsonResponse.getString("object"), "invoiceitem");

      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case with optional parameters for updateAnInvoiceItem method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnInvoiceItem} integration test with optional parameters.")
   public void testUpdateAnInvoiceItemWithOptionalParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnInvoiceItem_Optional.txt";
      String methodName = "stripe_updateAnInvoiceItem";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("invoiceItemId"));
         Assert.assertEquals(jsonResponse.getString("amount"), jsonObject.get("amount"));
         Assert.assertEquals(jsonResponse.getString("description"), jsonObject.get("description"));
         Assert.assertEquals(jsonResponse.getJSONObject("metadata").getString("checked"), jsonObject.getJSONObject("metadata").get("checked"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for updateAnInvoiceItem method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnInvoiceItem} integration test with negative case.")
   public void testUpdateAnInvoiceItemWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnInvoiceItem_negative.txt";
      String methodName = "stripe_updateAnInvoiceItem";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such invoiceitem: " + jsonObject.get("invoiceItemId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for deleteAnInvoiceItem method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {deleteAnInvoiceItem} integration test with positive case.")
   public void testDeleteAnInvoiceItemWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "deleteAnInvoiceItem_Positive.txt";
      String methodName = "stripe_deleteAnInvoiceItem";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("invoiceItemId"));
         Assert.assertEquals(jsonResponse.getString("deleted"), "true");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for deleteAnInvoiceItem method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {deleteAnInvoiceItem} integration test with negative case.")
   public void testDeleteAnInvoiceItemWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "deleteAnInvoiceItem_Negative.txt";
      String methodName = "stripe_deleteAnInvoiceItem";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such invoiceitem: " + jsonObject.get("invoiceItemId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for getAListOfAllInvoiceItems method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getAListOfAllInvoiceItems} integration test with mandatory parameters.")
   public void testGetAListOfAllInvoiceItemsWithPositiveCase() throws Exception {
      String methodName = "stripe_getAListOfAllInvoiceItems";
      String modifiedJsonString = String.format("{\"apiKey\":\"%s\"}", stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/invoiceitems");
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case for getAListOfInvoices method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getAListOfAllInvoiceItems} integration test with optional parameters.")
   public void testGetAListOfAllInvoiceItemsWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "getAListOfAllInvoiceItems_Optional.txt";
      String methodName = "stripe_getAListOfAllInvoiceItems";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/invoiceitems");
         Assert.assertEquals(jsonResponse.getJSONArray("data").length(), jsonObject.get("limit"));
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case with mandatory parameters for createAnInvoice method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createAnInvoice} integration test with mandatory parameters.")
   public void testCreateAnInvoiceWithMandatoryParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createAnInvoice_Mandatory.txt";
      String methodName = "stripe_createAnInvoice";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("customer"), jsonObject.get("customer"));
         Assert.assertEquals(jsonResponse.getString("object"), "invoice");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with optional parameters for createAnInvoice method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createAnInvoice} integration test with optional parameters.")
   public void testCreateAnInvoiceWithOptionalParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createAnInvoice_Optional.txt";
      String methodName = "stripe_createAnInvoice";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("customer"), jsonObject.get("customer"));
         Assert.assertEquals(jsonResponse.getString("object"), "invoice");
         Assert.assertEquals(jsonResponse.getString("description"), jsonObject.get("description"));
         Assert.assertEquals(jsonResponse.getString("statement_descriptor"), jsonObject.get("statementDescriptor"));
         Assert.assertEquals(jsonResponse.getString("subscription"), jsonObject.get("subscription"));
         Assert.assertEquals(jsonResponse.getString("tax_percent"), jsonObject.get("taxPercent"));
         Assert.assertEquals(jsonResponse.getJSONObject("metadata").getString("checked"), jsonObject.getJSONObject("metadata").get("checked"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for createAnInvoice method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createAnInvoice} integration test with negative case.")
   public void testCreateAnInvoiceWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createAnInvoice_Negative.txt";
      String methodName = "stripe_createAnInvoice";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such customer: " + jsonObject.get("customer"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for retrieveAnExistingInvoice method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingInvoice} integration test with positive case.")
   public void testRetrieveAnExistingInvoiceWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnExistingInvoice_Positive.txt";
      String methodName = "stripe_retrieveAnExistingInvoice";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("invoiceId"));
         Assert.assertEquals(jsonResponse.getString("object"), "invoice");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for retrieveAnExistingInvoice method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingInvoice} integration test with negative case.")
   public void testRetrieveAnExistingInvoiceWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnExistingInvoice_Negative.txt";
      String methodName = "stripe_retrieveAnExistingInvoice";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such invoice: " + jsonObject.get("invoiceId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for retrieveAnInvoicesLineItems method with mandatory parameters.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingInvoice} integration test with positive case.")
   public void testRetrieveAnExistingInvoiceWithMandatoryParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnInvoicesLineItems_Mandatory.txt";
      String methodName = "stripe_retrieveAnInvoicesLineItems";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/invoices/" + jsonObject.get("invoiceId") + "/lines");
         Assert.assertEquals(jsonResponse.getString("object"), "list");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for retrieveAnInvoicesLineItems method with optional parameters.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnInvoicesLineItems} integration test with positive case.")
   public void testRetrieveAnInvoicesLineItemsWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnInvoicesLineItems_Optional.txt";
      String methodName = "stripe_retrieveAnInvoicesLineItems";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/invoices/" + jsonObject.get("invoiceId") + "/lines");
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getJSONArray("data").length(), jsonObject.get("limit"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for retrieveAnInvoicesLineItems method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnInvoicesLineItems} integration test with negative case.")
   public void testRetrieveAnInvoicesLineItemsWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnInvoicesLineItems_Negative.txt";
      String methodName = "stripe_retrieveAnExistingInvoice";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such invoice: " + jsonObject.get("invoiceId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for retrieveAnUpcomingInvoice method with mandatory parameters.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnUpcomingInvoice} integration test with positive case.")
   public void testRetrieveAnUpcomingInvoiceWithMandatoryParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnUpcomingInvoice_Mandatory.txt";
      String methodName = "stripe_retrieveAnUpcomingInvoice";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("lines").getString("url"), "/v1/invoices/upcoming/lines?customer=" + jsonObject.get("customer"));
         Assert.assertEquals(jsonResponse.getJSONObject("lines").getString("object"), "list");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for retrieveAnUpcomingInvoice method with optional parameters.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnUpcomingInvoice} integration test with positive case.")
   public void testRetrieveAnUpcomingInvoiceWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnUpcomingInvoice_Optional.txt";
      String methodName = "stripe_retrieveAnUpcomingInvoice";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("lines").getString("url"), "/v1/invoices/upcoming/lines?customer=" + jsonObject.get("customer"));
         Assert.assertEquals(jsonResponse.getJSONObject("lines").getString("object"), "list");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for retrieveAnUpcomingInvoice method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnUpcomingInvoice} integration test with negative case.")
   public void testRetrieveAnUpcomingInvoiceWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnUpcomingInvoice_Negative.txt";
      String methodName = "stripe_retrieveAnUpcomingInvoice";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such customer: " + jsonObject.get("customer"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with mandatory parameters for updateAnInvoice method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnInvoice} integration test with mandatory parameters.")
   public void testUpdateAnInvoiceWithCardParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnInvoice_Mandatory.txt";
      String methodName = "stripe_updateAnInvoice";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("invoiceId"));
         Assert.assertEquals(jsonResponse.getString("object"), "invoice");

      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case with optional parameters for updateAnInvoice method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnInvoice} integration test with optional parameters.")
   public void testUpdateAnInvoiceWithOptionalParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnInvoice_Optional.txt";
      String methodName = "stripe_updateAnInvoice";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("invoiceId"));
         Assert.assertEquals(jsonResponse.getString("description"), jsonObject.get("description"));
         Assert.assertEquals(jsonResponse.getJSONObject("metadata").getString("checked"), jsonObject.getJSONObject("metadata").get("checked"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for updateAnInvoice method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnInvoice} integration test with negative case.")
   public void testUpdateAnInvoiceWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnInvoice_negative.txt";
      String methodName = "stripe_updateAnInvoice";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such invoice: " + jsonObject.get("invoiceId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for payAnInvoice method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {payAnInvoice} integration test with positive case.")
   public void testPayAnInvoiceWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "payAnInvoice_Positive.txt";
      String methodName = "stripe_payAnInvoice";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("invoiceId"));
         Assert.assertEquals(jsonResponse.getString("object"), "invoice");
         Assert.assertEquals(jsonResponse.getString("closed"), "true");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for payAnInvoice method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {payAnInvoice} integration test with negative case.")
   public void testPayAnInvoiceWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "payAnInvoice_Negative.txt";
      String methodName = "stripe_payAnInvoice";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such invoice: " + jsonObject.get("invoiceId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for getAListOfInvoices method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getAListOfInvoices} integration test with mandatory parameters.")
   public void testGetAListOfInvoicesWithPositiveCase() throws Exception {
      String methodName = "stripe_getAListOfInvoices";
      String modifiedJsonString = String.format("{\"apiKey\":\"%s\"}", stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/invoices");
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case for getAListOfInvoices method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getAListOfInvoices} integration test with optional parameters.")
   public void testGetAListOfInvoicesWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "getAListOfInvoices_Optional.txt";
      String methodName = "stripe_getAListOfInvoices";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/invoices");
         Assert.assertEquals(jsonResponse.getJSONArray("data").length(), jsonObject.get("limit"));
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case with mandatory parameters for createATransfer method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createATransfer} integration test with mandatory parameters.")
   public void testCreateANewTransferWithMandatoryParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createATransfer_Mandatory.txt";
      String methodName = "stripe_createATransfer";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "transfer");
         Assert.assertEquals(jsonResponse.getString("amount"), jsonObject.get("amount"));
         Assert.assertEquals(jsonResponse.getString("currency"), jsonObject.get("currency"));
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case with optional parameter createATransfer method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createATransfer} integration test with optional parameters.")
   public void testCreateATransferWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createATransfer_Optional.txt";
      String methodName = "stripe_createATransfer";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "transfer");
         Assert.assertEquals(jsonResponse.getString("amount"), jsonObject.get("amount"));
         Assert.assertEquals(jsonResponse.getString("currency"), jsonObject.get("currency"));
         Assert.assertEquals(jsonResponse.getString("recipient"), jsonObject.get("recipient"));
         Assert.assertEquals(jsonResponse.getString("description"), jsonObject.get("description"));
         Assert.assertEquals(jsonResponse.getString("statement_descriptor"), jsonObject.get("statementDescriptor"));
         Assert.assertEquals(jsonResponse.getJSONObject("bank_account").getString("id"), jsonObject.get("bankAccount"));
         Assert.assertEquals(jsonResponse.getJSONObject("metadata").getString("checked"), jsonObject.getJSONObject("metadata").get("checked"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for createANewTransfer method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createATransfer} integration test with  optional parameters of card details.")
   public void testCreateATransferWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createATransfer_Negative.txt";
      String methodName = "stripe_createATransfer";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such recipient: " + jsonObject.get("recipient"));

      } finally {

         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for retrieveAnExistingTransfer method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingTransfer} integration test with positive case.")
   public void testRetrieveAnExistingTransferWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnExistingTransfer_Positive.txt";
      String methodName = "stripe_retrieveAnExistingTransfer";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("transferId"));
         Assert.assertEquals(jsonResponse.getString("object"), "transfer");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for retrieveAnExistingTransfer method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingTransfer} integration test with negative case.")
   public void testRetrieveAnExistingTransferWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnExistingTransfer_Negative.txt";
      String methodName = "stripe_retrieveAnExistingTransfer";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such transfer: " + jsonObject.get("transferId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with mandatory parameters for updateAnExistingTransfer method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnExistingTransfer} integration test with mandatory parameters.")
   public void testUpdateAnExistingTransferWithCardParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnExistingTransfer_Mandatory.txt";
      String methodName = "stripe_updateAnExistingTransfer";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("transferId"));
         Assert.assertEquals(jsonResponse.getString("object"), "transfer");

      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case with optional parameters for updateAnExistingTransfer method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnExistingTransfer} integration test with optional parameters.")
   public void testUpdateAnExistingTransferWithOptionalParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnExistingTransfer_Optional.txt";
      String methodName = "stripe_updateAnExistingTransfer";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("transferId"));
         Assert.assertEquals(jsonResponse.getString("description"), jsonObject.get("description"));
         Assert.assertEquals(jsonResponse.getJSONObject("metadata").getString("checked"), jsonObject.getJSONObject("metadata").get("checked"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for updateAnExistingTransfer method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnExistingTransfer} integration test with negative case.")
   public void testUpdateAnExistingTransferWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnExistingTransfer_negative.txt";
      String methodName = "stripe_updateAnExistingTransfer";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such transfer: " + jsonObject.get("transferId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for cancelATransfer method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {cancelATransfer} integration test with positive case.")
   public void testCancelATransferWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "cancelATransfer_Positive.txt";
      String methodName = "stripe_cancelATransfer";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("transferId"));
         Assert.assertEquals(jsonResponse.getString("status"), "canceled");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for cancelATransfer method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {cancelATransfer} integration test with negative case.")
   public void testCancelATransferWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "cancelATransfer_Negative.txt";
      String methodName = "stripe_cancelATransfer";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such transfer: " + jsonObject.get("transferId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for getAListOfAllTransfers method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getAListOfAllTransfers} integration test with mandatory parameters.")
   public void testGetAListOfAllTransfersWithPositiveCase() throws Exception {
      String methodName = "stripe_getAListOfAllTransfers";
      String modifiedJsonString = String.format("{\"apiKey\":\"%s\"}", stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/transfers");
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for getAListOfAllTransfers method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getAListOfAllTransfers} integration test with optional parameters.")
   public void testGetAListOfAllTransfersWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "getAListOfAllTransfers_Optional.txt";
      String methodName = "stripe_getAListOfAllTransfers";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/transfers");
         Assert.assertEquals(jsonResponse.getJSONArray("data").length(), jsonObject.get("limit"));
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case for retrieveAccountDetails method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAccountDetails} integration test with mandatory parameters.")
   public void testRetrieveAccountDetailsWithCardParameter() throws Exception {
      String methodName = "stripe_retrieveAccountDetails";
      String modifiedJsonString = String.format("{\"apiKey\":\"%s\"}", stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         Assert.assertTrue(jsonResponse.has("id"));
         Assert.assertTrue(jsonResponse.has("display_name"));
         Assert.assertTrue(jsonResponse.has("email"));
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case for getCurrentAccountBalance method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getCurrentAccountBalance} integration test with mandatory parameters.")
   public void testGetCurrentAccountBalanceWithCardParameter() throws Exception {
      String methodName = "stripe_getCurrentAccountBalance";
      String modifiedJsonString = String.format("{\"apiKey\":\"%s\"}", stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         Assert.assertTrue(jsonResponse.has("pending"));
         Assert.assertTrue(jsonResponse.has("available"));
         Assert.assertEquals(jsonResponse.getString("object"), "balance");
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for retrieveABalanceTransaction method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveABalanceTransaction} integration test with positive case.")
   public void testRetrieveABalanceTransactionWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveABalanceTransaction_Positive.txt";
      String methodName = "stripe_retrieveABalanceTransaction";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("transactionId"));
         Assert.assertEquals(jsonResponse.getString("object"), "balance_transaction");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for retrieveABalanceTransaction method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveABalanceTransaction} integration test with negative case.")
   public void testRetrieveABalanceTransactionWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveABalanceTransaction_Negative.txt";
      String methodName = "stripe_retrieveABalanceTransaction";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such balancetransaction: " + jsonObject.get("transactionId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for getListOfBalanceHistory method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getListOfBalanceHistory} integration test with mandatory parameters.")
   public void testGetListOfBalanceHistoryWithPositiveCase() throws Exception {
      String methodName = "stripe_getListOfBalanceHistory";
      String modifiedJsonString = String.format("{\"apiKey\":\"%s\"}", stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/balance/history");
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for getListOfBalanceHistory method with optional parameters.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getListOfBalanceHistory} integration test with optional parameters.")
   public void testGetListOfBalanceHistoryWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "getListOfBalanceHistory_Optional.txt";
      String methodName = "stripe_getListOfBalanceHistory";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/balance/history");
         Assert.assertEquals(jsonResponse.getJSONArray("data").length(), jsonObject.get("limit"));
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case with mandatory parameters for createANewPlan method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewPlan} integration test with mandatory parameters.")
   public void testCreateANewPlanWithMandatoryParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewPlan_Mandatory.txt";
      String methodName = "stripe_createANewPlan";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("amount"), jsonObject.get("amount"));
         Assert.assertEquals(jsonResponse.getString("currency"), jsonObject.get("currency"));
         Assert.assertEquals(jsonResponse.getString("name"), jsonObject.get("name"));
         Assert.assertEquals(jsonResponse.getString("interval"), jsonObject.get("interval"));

      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case with optional parameter createANewPlan method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewPlan} integration test with optional parameters.")
   public void testCreateANewPlanWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewPlan_Optional.txt";
      String methodName = "stripe_createANewPlan";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("amount"), jsonObject.get("amount"));
         Assert.assertEquals(jsonResponse.getString("currency"), jsonObject.get("currency"));
         Assert.assertEquals(jsonResponse.getString("name"), jsonObject.get("name"));
         Assert.assertEquals(jsonResponse.getString("interval"), jsonObject.get("interval"));
         Assert.assertEquals(jsonResponse.getString("interval_count"), jsonObject.get("intervalCount"));
         Assert.assertEquals(jsonResponse.getJSONObject("metadata").getString("checked"), jsonObject.getJSONObject("metadata").get("checked"));
         Assert.assertEquals(jsonResponse.getString("statement_descriptor"), jsonObject.get("statementDescriptor"));
         Assert.assertEquals(jsonResponse.getString("trial_period_days"), jsonObject.get("trialPeriodDays"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for createANewPlan method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewPlan} integration test with  optional parameters of card details.")
   public void testCreateANewPlanWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewPlan_Negative.txt";
      String methodName = "stripe_createANewPlan";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "Invalid interval: must be one of month, week, or year");

      } finally {

         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for retrieveAnExistingPlan method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingPlan} integration test with positive case.")
   public void testRetrieveAnExistingPlanWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnExistingPlan_Positive.txt";
      String methodName = "stripe_retrieveAnExistingPlan";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("planId"));
         Assert.assertEquals(jsonResponse.getString("object"), "plan");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for retrieveAnExistingPlan method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingPlan} integration test with negative case.")
   public void testRetrieveAnExistingPlanWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnExistingPlan_Negative.txt";
      String methodName = "stripe_retrieveAnExistingPlan";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such plan: " + jsonObject.get("planId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with mandatory parameters for updateAnExistingPlan method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnExistingPlan} integration test with mandatory parameters.")
   public void testUpdateAnExistingPlanWithMandatoryParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnExistingPlan_Mandatory.txt";
      String methodName = "stripe_updateAnExistingPlan";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("planId"));
         Assert.assertEquals(jsonResponse.getString("object"), "plan");

      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case with name parameter for updateAnExistingPlan method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnExistingPlan} integration test with name parameters.")
   public void testUpdateAnExistingPlanWithNameParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnExistingPlan_WithName.txt";
      String methodName = "stripe_updateAnExistingPlan";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("planId"));
         Assert.assertEquals(jsonResponse.getString("name"), jsonObject.get("name"));
         Assert.assertEquals(jsonResponse.getString("object"), "plan");
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case with metadata parameter for updateAnExistingPlan method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnExistingPlan} integration test with metadata parameter.")
   public void testUpdateAnExistingPlanWithMetadataParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnExistingPlan_WithMetadata.txt";
      String methodName = "stripe_updateAnExistingPlan";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("planId"));
         Assert.assertEquals(jsonResponse.getJSONObject("metadata").getString("firstName"), jsonObject.getJSONObject("metadata").get("firstName"));
         Assert.assertEquals(jsonResponse.getString("object"), "plan");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with optional parameters for updateAnExistingPlan method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnExistingPlan} integration test with optional parameters.")
   public void testUpdateAnExistingPlanWithOptionalParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnExistingPlan_Optional.txt";
      String methodName = "stripe_updateAnExistingPlan";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("planId"));
         Assert.assertEquals(jsonResponse.getString("name"), jsonObject.get("name"));
         Assert.assertEquals(jsonResponse.getString("statement_descriptor"), jsonObject.get("statementDescriptor"));
         Assert.assertEquals(jsonResponse.getJSONObject("metadata").getString("firstName"), jsonObject.getJSONObject("metadata").get("firstName"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for updateAnExistingPlan method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnExistingPlan} integration test with negative case.")
   public void testUpdateAnExistingPlanWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnExistingPlan_negative.txt";
      String methodName = "stripe_updateAnExistingPlan";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such plan: " + jsonObject.get("planId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for deleteAnExistingPlan method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {deleteAnExistingPlan} integration test with positive case.")
   public void testDeleteAnExistingPlanWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "deleteAnExistingPlan_Positive.txt";
      String methodName = "stripe_deleteAnExistingPlan";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("planId"));
         Assert.assertEquals(jsonResponse.getString("deleted"), "true");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for deleteAnExistingPlan method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {deleteAnExistingPlan} integration test with negative case.")
   public void testDeleteAnExistingPlanWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "deleteAnExistingPlan_Negative.txt";
      String methodName = "stripe_deleteAnExistingPlan";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such plan: " + jsonObject.get("planId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for getListOfAllPlans method with mandatory parameters.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getListOfAllPlans} integration test with mandatory parameters.")
   public void testGetListOfAllPlansWithPositiveCase() throws Exception {
      String methodName = "stripe_getListOfAllPlans";
      String modifiedJsonString = String.format("{\"apiKey\":\"%s\"}", stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/plans");
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case for getListOfAllPlans method with optional parameters.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getListOfAllPlans} integration test with optional parameters.")
   public void testGetListOfAllPlansWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "getListOfAllPlans_Optional.txt";
      String methodName = "stripe_getListOfAllPlans";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/plans");
         Assert.assertEquals(jsonResponse.getJSONArray("data").length(), jsonObject.get("limit"));
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case with mandatory parameters for createANewSubscription method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewSubscription} integration test with mandatory parameters.")
   public void testCreateANewSubscriptionWithMandatoryParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewSubscription_Mandatory.txt";
      String methodName = "stripe_createANewSubscription";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("plan").getString("id"), jsonObject.get("plan"));
         Assert.assertEquals(jsonResponse.getString("customer"), jsonObject.get("customerId"));
         Assert.assertEquals(jsonResponse.getString("object"), "subscription");
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case with optional parameter createANewSubscription method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewSubscription} integration test with optional parameters.")
   public void testCreateANewSubscriptionWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewSubscription_Optional.txt";
      String methodName = "stripe_createANewSubscription";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("plan").getString("id"), jsonObject.get("plan"));
         Assert.assertEquals(jsonResponse.getString("customer"), jsonObject.get("customerId"));
         Assert.assertEquals(jsonResponse.getString("quantity"), jsonObject.get("quantity"));
         Assert.assertEquals(jsonResponse.getJSONObject("metadata").getString("checked"), jsonObject.getJSONObject("metadata").get("checked"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for createANewSubscription method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewPlan} integration test with  optional parameters of card details.")
   public void testCreateANewSubscriptionWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewSubscription_Negative.txt";
      String methodName = "stripe_createANewSubscription";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such customer: " + jsonObject.getString("customerId"));

      } finally {

         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for retrieveAnExistingSubscription method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingSubscription} integration test with positive case.")
   public void testRetrieveAnExistingSubscriptionWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnExistingSubscription_Positive.txt";
      String methodName = "stripe_retrieveAnExistingSubscription";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("subscriptionId"));
         Assert.assertEquals(jsonResponse.getString("object"), "subscription");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for retrieveAnExistingSubscription method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingSubscription} integration test with negative case.")
   public void testRetrieveAnExistingSubscriptionWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnExistingSubscription_Negative.txt";
      String methodName = "stripe_retrieveAnExistingSubscription";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "Customer " + jsonObject.get("customerId") + " does not have a subscription with ID " + jsonObject.get("subscriptionId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with mandatory parameters for updateAnExistingSubscription method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnExistingSubscription} integration test with mandatory parameters.")
   public void testUpdateAnExistingSubscriptionWithMandatoryParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnExistingSubscription_Mandatory.txt";
      String methodName = "stripe_updateAnExistingSubscription";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("subscriptionId"));
         Assert.assertEquals(jsonResponse.getString("object"), "subscription");

      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case with card parameter for updateAnExistingSubscription method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnExistingSubscription} integration test with card parameters.")
   public void testUpdateAnExistingSubscriptionWithCardParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnExistingSubscription_WithCard.txt";
      String methodName = "stripe_updateAnExistingSubscription";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("subscriptionId"));
         Assert.assertEquals(jsonResponse.getString("object"), "subscription");

      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case with plan parameter for updateAnExistingSubscription method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnExistingSubscription} integration test with plan parameter.")
   public void testUpdateAnExistingSubscriptionWithPlanParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnExistingSubscription_WithPlan.txt";
      String methodName = "stripe_updateAnExistingSubscription";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("subscriptionId"));
         Assert.assertEquals(jsonResponse.getJSONObject("plan").getString("id"), jsonObject.get("plan"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with optional parameters for updateAnExistingSubscription method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnExistingSubscription} integration test with optional parameters.")
   public void testUpdateAnExistingSubscriptionWithOptionalParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnExistingSubscription_Optional.txt";
      String methodName = "stripe_updateAnExistingSubscription";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("subscriptionId"));
         Assert.assertEquals(jsonResponse.getJSONObject("metadata").getString("firstName"), jsonObject.getJSONObject("metadata").get("firstName"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for updateAnExistingSubscription method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnExistingSubscription} integration test with negative case.")
   public void testUpdateAnExistingSubscriptionWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnExistingSubscription_negative.txt";
      String methodName = "stripe_updateAnExistingSubscription";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "Customer " + jsonObject.get("customerId") + " does not have a subscription with ID " + jsonObject.get("subscriptionId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with mandatory parameters for cancelASubscription method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {cancelASubscription} integration test with mandatory parameters.")
   public void testCancelASubscriptionWithMandatoryParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "cancelASubscription_Mandatory.txt";
      String methodName = "stripe_cancelASubscription";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("subscriptionId"));
         Assert.assertEquals(jsonResponse.getString("status"), "canceled");
         Assert.assertEquals(jsonResponse.getString("cancel_at_period_end"), "false");
         Assert.assertEquals(jsonResponse.getString("object"), "subscription");

      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case with optional parameters for cancelASubscription method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {cancelASubscription} integration test with optional parameters.")
   public void testCancelASubscriptionWithOptionalParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "cancelASubscription_Optional.txt";
      String methodName = "stripe_cancelASubscription";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("subscriptionId"));
         Assert.assertEquals(jsonResponse.getString("status"), "active");
         Assert.assertEquals(jsonResponse.getString("cancel_at_period_end"), "true");
         Assert.assertEquals(jsonResponse.getString("object"), "subscription");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for cancelASubscription method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {cancelASubscription} integration test with negative case.")
   public void testCancelASubscriptionWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "cancelASubscription_negative.txt";
      String methodName = "stripe_cancelASubscription";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "Customer " + jsonObject.get("customerId") + " does not have a subscription with ID " + jsonObject.get("subscriptionId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for listActiveSubscriptions method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {listActiveSubscriptions} integration test with mandatory parameters.")
   public void testListActiveSubscriptionsWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "listActiveSubscriptions_Mandatory.txt";
      String methodName = "stripe_listActiveSubscriptions";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/customers/" + jsonObject.get("customerId") + "/subscriptions");
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case for listActiveSubscriptions method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {listActiveSubscriptions} integration test with optional parameters.")
   public void testListActiveSubscriptionsWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "listActiveSubscriptions_Optional.txt";
      String methodName = "stripe_listActiveSubscriptions";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/customers/" + jsonObject.get("customerId") + "/subscriptions");
         Assert.assertEquals(jsonResponse.getJSONArray("data").length(), jsonObject.get("limit"));
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case for retrieveAnApplicationFee method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnApplicationFee} integration test with positive case.")
   public void testRetrieveAnApplicationFeeWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnApplicationFee_Positive.txt";
      String methodName = "stripe_retrieveAnApplicationFee";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("applicationFeeId"));
         Assert.assertEquals(jsonResponse.getString("object"), "application_fee");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for retrieveAnApplicationFee method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnApplicationFee} integration test with negative case.")
   public void testRetrieveAnApplicationFeeWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnApplicationFee_Negative.txt";
      String methodName = "stripe_retrieveAnApplicationFee";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such application fee: " + jsonObject.get("applicationFeeId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for getListAllApplicationFees method with mandatory parameters.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getListAllApplicationFees} integration test with mandatory parameters.")
   public void testGetListAllApplicationFeesWithPositiveCase() throws Exception {
      String methodName = "stripe_getListOfAllApplicationFees";
      String modifiedJsonString = String.format("{\"apiKey\":\"%s\"}", stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/application_fees");
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case for getListAllApplicationFees method with optional parameters.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getListAllApplicationFees} integration test with optional parameters.")
   public void testGetListAllApplicationFeesWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "getListAllApplicationFees_Optional.txt";
      String methodName = "stripe_getListOfAllApplicationFees";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/application_fees");
         Assert.assertEquals(jsonResponse.getJSONArray("data").length(), jsonObject.get("limit"));
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case with mandatory parameters for createANewApplicationFeeRefund method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewApplicationFeeRefund} integration test with mandatory parameters.")
   public void testCreateANewApplicationFeeRefundWithMandatoryParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewApplicationFeeRefund_Mandatory.txt";
      String methodName = "stripe_createANewApplicationFeeRefund";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("fee"), jsonObject.get("applicationFeeId"));
         Assert.assertEquals(jsonResponse.getString("object"), "fee_refund");
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case with optional parameter createANewApplicationFeeRefund method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewApplicationFeeRefund} integration test with optional parameters.")
   public void testCreateANewApplicationFeeRefundWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewApplicationFeeRefund_Optional.txt";
      String methodName = "stripe_createANewApplicationFeeRefund";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("fee"), jsonObject.get("applicationFeeId"));
         Assert.assertEquals(jsonResponse.getString("object"), "fee_refund");
         Assert.assertEquals(jsonResponse.getString("amount"), jsonObject.get("amount"));
         Assert.assertEquals(jsonResponse.getJSONObject("metadata").getString("checked"), jsonObject.getJSONObject("metadata").get("checked"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for createANewApplicationFeeRefund method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createANewApplicationFeeRefund} integration test with  optional parameters of card details.")
   public void testCreateANewApplicationFeeRefundWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createANewApplicationFeeRefund_Negative.txt";
      String methodName = "stripe_createANewApplicationFeeRefund";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such application fee: " + jsonObject.getString("applicationFeeId"));

      } finally {

         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for retrieveAnApplicationFeeRefund method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnApplicationFeeRefund} integration test with positive case.")
   public void testRetrieveAnApplicationFeeRefundWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnApplicationFeeRefund_Positive.txt";
      String methodName = "stripe_retrieveAnApplicationFeeRefund";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("fee"), jsonObject.get("applicationFeeId"));
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("refundId"));
         Assert.assertEquals(jsonResponse.getString("object"), "fee_refund");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for retrieveAnApplicationFeeRefund method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnApplicationFeeRefund} integration test with negative case.")
   public void testRetrieveAnApplicationFeeRefundWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnApplicationFeeRefund_Negative.txt";
      String methodName = "stripe_retrieveAnApplicationFeeRefund";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such feerefund: " + jsonObject.get("refundId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with mandatory parameters for updateAnExistingApplicationFeeRefund method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnExistingApplicationFeeRefund} integration test with mandatory parameters.")
   public void testUpdateAnExistingApplicationFeeRefundWithMandatoryParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnExistingApplicationFeeRefund_mandatory.txt";
      String methodName = "stripe_updateAnExistingApplicationFeeRefund";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("fee"), jsonObject.get("applicationFeeId"));
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("refundId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with optional parameters for updateAnExistingApplicationFeeRefund method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnExistingApplicationFeeRefund} integration test with optional parameters.")
   public void testUpdateAnExistingApplicationFeeRefundWithOptionalParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnExistingApplicationFeeRefund_Optional.txt";
      String methodName = "stripe_updateAnExistingApplicationFeeRefund";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("fee"), jsonObject.get("applicationFeeId"));
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("refundId"));
         Assert.assertEquals(jsonResponse.getJSONObject("metadata").getString("firstName"), jsonObject.getJSONObject("metadata").get("firstName"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for updateAnExistingApplicationFeeRefund method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnExistingApplicationFeeRefund} integration test with negative case.")
   public void testUpdateAnExistingApplicationFeeRefundWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnExistingApplicationFeeRefund_negative.txt";
      String methodName = "stripe_updateAnExistingApplicationFeeRefund";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such feerefund: " + jsonObject.get("refundId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for getListAllApplicationFeeRefunds method with mandatory parameters.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getListAllApplicationFeeRefunds} integration test with mandatory parameters.")
   public void testGetListAllApplicationFeeRefundsWithMandatoryParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "getListAllApplicationFeeRefunds_mandatory.txt";
      String methodName = "stripe_getListAllApplicationFeeRefunds";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/application_fees/" + jsonObject.get("applicationFeeId") + "/refunds");
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case for getListAllApplicationFeeRefunds method with optional parameters.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getListAllApplicationFeeRefunds} integration test with optional parameters.")
   public void testGetListAllApplicationFeeRefundsWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "getListAllApplicationFeeRefunds_Optional.txt";
      String methodName = "stripe_getListAllApplicationFeeRefunds";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/application_fees/" + jsonObject.get("applicationFeeId") + "/refunds");
         Assert.assertEquals(jsonResponse.getJSONArray("data").length(), jsonObject.get("limit"));
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Negative test case for getListAllApplicationFeeRefunds method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getListAllApplicationFeeRefunds} integration test with negative case.")
   public void testGetListAllApplicationFeeRefundsWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "getListAllApplicationFeeRefunds_negative.txt";
      String methodName = "stripe_getListAllApplicationFeeRefunds";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such application fee: " + jsonObject.get("applicationFeeId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with mandatory parameters(amount_off) for createACoupon method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createACoupon} integration test with amount_off mandatory parameters.")
   public void testCreateACouponWithAmountOffParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createACoupon_WithAmountOff.txt";
      String methodName = "stripe_createACoupon";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("amount_off"), jsonObject.get("amountOff"));
         Assert.assertEquals(jsonResponse.getString("duration"), jsonObject.get("duration"));
         Assert.assertEquals(jsonResponse.getString("object"), "coupon");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case with mandatory parameters(percentage_off) for createACoupon method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createACoupon} integration test with percentage_off mandatory parameters.")
   public void testCreateACouponWithPercentageOffParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createACoupon_WithPercentageOff.txt";
      String methodName = "stripe_createACoupon";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("percent_off"), jsonObject.get("percentOff"));
         Assert.assertEquals(jsonResponse.getString("duration"), jsonObject.get("duration"));
         Assert.assertEquals(jsonResponse.getString("duration_in_months"), jsonObject.get("durationInMonths"));
         Assert.assertEquals(jsonResponse.getString("object"), "coupon");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case with optional parameter createACoupon method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createACoupon} integration test with optional parameters.")
   public void testCreateACouponWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createACoupon_Optional.txt";
      String methodName = "stripe_createACoupon";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("percent_off"), jsonObject.get("percentOff"));
         Assert.assertEquals(jsonResponse.getString("duration"), jsonObject.get("duration"));
         Assert.assertEquals(jsonResponse.getString("duration_in_months"), jsonObject.get("durationInMonths"));
         Assert.assertEquals(jsonResponse.getString("object"), "coupon");
         Assert.assertEquals(jsonResponse.getJSONObject("metadata").getString("checked"), jsonObject.getJSONObject("metadata").get("checked"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for createACoupon method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {createACoupon} integration test with  optional parameters of card details.")
   public void testCreateACouponWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "createACoupon_Negative.txt";
      String methodName = "stripe_createACoupon";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "Invalid duration: must be one of repeating, once, or forever");

      } finally {

         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for retrieveAnExistingCoupon method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingCoupon} integration test with positive case.")
   public void testRetrieveAnExistingCouponWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnExistingCoupon_Positive.txt";
      String methodName = "stripe_retrieveAnExistingCoupon";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("couponId"));
         Assert.assertEquals(jsonResponse.getString("object"), "coupon");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for retrieveAnExistingCoupon method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnExistingCoupon} integration test with negative case.")
   public void testRetrieveAnExistingCouponWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnExistingCoupon_Negative.txt";
      String methodName = "stripe_retrieveAnExistingCoupon";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such coupon: " + jsonObject.get("couponId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with mandatory parameters for updateAnExistingCoupon method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnExistingCoupon} integration test with mandatory parameters.")
   public void testUpdateAnExistingCouponWithMandatoryParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnExistingCoupon_mandatory.txt";
      String methodName = "stripe_updateAnExistingCoupon";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "coupon");
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("couponId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case with optional parameters for updateAnExistingCoupon method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnExistingCoupon} integration test with optional parameters.")
   public void testUpdateAnExistingCouponWithOptionalParameter() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnExistingCoupon_Optional.txt";
      String methodName = "stripe_updateAnExistingCoupon";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "coupon");
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("couponId"));
         Assert.assertEquals(jsonResponse.getJSONObject("metadata").getString("firstName"), jsonObject.getJSONObject("metadata").get("firstName"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for updateAnExistingCoupon method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {updateAnExistingCoupon} integration test with negative case.")
   public void testUpdateAnExistingCouponWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "updateAnExistingCoupon_negative.txt";
      String methodName = "stripe_updateAnExistingCoupon";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such coupon: " + jsonObject.get("couponId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for deleteAnExistingCoupon method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {deleteAnExistingCoupon} integration test with positive case.")
   public void testDeleteAnExistingCouponWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "deleteAnExistingCoupon_Positive.txt";
      String methodName = "stripe_deleteAnExistingCoupon";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("couponId"));
         Assert.assertEquals(jsonResponse.getString("deleted"), "true");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for deleteAnExistingCoupon method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {deleteAnExistingCoupon} integration test with negative case.")
   public void testDeleteAnExistingCouponWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "deleteAnExistingCoupon_Negative.txt";
      String methodName = "stripe_deleteAnExistingCoupon";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such coupon: " + jsonObject.get("couponId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for getAListOfAllCoupons method with mandatory parameters.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getAListOfAllCoupons} integration test with mandatory parameters.")
   public void testGetAListOfAllCouponsWithPositiveCase() throws Exception {
      String methodName = "stripe_getAListOfAllCoupons";
      String modifiedJsonString = String.format("{\"apiKey\":\"%s\"}", stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/coupons");
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case for getAListOfAllCoupons method with optional parameters.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getAListOfAllCoupons} integration test with optional parameters.")
   public void testGetAListOfAllCouponsWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "getAListOfAllCoupons_Optional.txt";
      String methodName = "stripe_getAListOfAllCoupons";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/coupons");
         Assert.assertEquals(jsonResponse.getJSONArray("data").length(), jsonObject.get("limit"));
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case for deleteACustomerDiscount method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {deleteACustomerDiscount} integration test with positive case.")
   public void testDeleteACustomerDiscountWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "deleteACustomerDiscount_Positive.txt";
      String methodName = "stripe_deleteACustomerDiscount";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         Assert.assertEquals(jsonResponse.getString("deleted"), "true");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for deleteACustomerDiscount method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {deleteACustomerDiscount} integration test with negative case.")
   public void testDeleteACustomerDiscountWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "deleteACustomerDiscount_Negative.txt";
      String methodName = "stripe_deleteACustomerDiscount";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such customer: " + jsonObject.get("customerId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for deleteASubscriptionDiscount method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {deleteASubscriptionDiscount} integration test with positive case.")
   public void testDeleteASubscriptionDiscountWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "deleteASubscriptionDiscount_Positive.txt";
      String methodName = "stripe_deleteASubscriptionDiscount";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         Assert.assertEquals(jsonResponse.getString("deleted"), "true");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for deleteASubscriptionDiscount method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {deleteASubscriptionDiscount} integration test with negative case.")
   public void testDeleteASubscriptionDiscountWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "deleteASubscriptionDiscount_Negative.txt";
      String methodName = "stripe_deleteASubscriptionDiscount";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "Customer " + jsonObject.get("customerId") + " does not have a subscription with ID " + jsonObject.get("subscriptionId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for retrieveAnEvent method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnEvent} integration test with positive case.")
   public void testRetrieveAnEventWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnEvent_Positive.txt";
      String methodName = "stripe_retrieveAnEvent";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("eventId"));
         Assert.assertEquals(jsonResponse.getString("object"), "event");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for retrieveAnEvent method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAnEvent} integration test with negative case.")
   public void testRetrieveAnEventWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAnEvent_Negative.txt";
      String methodName = "stripe_retrieveAnEvent";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such event: " + jsonObject.get("eventId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for getListOfEvents method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getAListOfAllRecipients} integration test with mandatory parameters.")
   public void testGetListOfEventsWithPositiveCase() throws Exception {
      String methodName = "stripe_getListOfEvents";
      String modifiedJsonString = String.format("{\"apiKey\":\"%s\"}", stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/events");
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case for getListOfEvents method with optional parameters.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getListOfEvents} integration test with optional parameters.")
   public void testGetListOfEventsWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "getListOfEvents_Optional.txt";
      String methodName = "stripe_getListOfEvents";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/events");
         Assert.assertEquals(jsonResponse.getJSONArray("data").length(), jsonObject.get("limit"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for retrieveAFileUpload method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAFileUpload} integration test with positive case.")
   public void testRetrieveAFileUploadWithPositiveCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAFileUpload_Positive.txt";
      String methodName = "stripe_retrieveAFileUpload";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("id"), jsonObject.get("fileId"));
         Assert.assertEquals(jsonResponse.getString("object"), "file_upload");
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Negative test case for retrieveAFileUpload method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {retrieveAFileUpload} integration test with negative case.")
   public void testRetrieveAFileUploadWithNegativeCase() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "retrieveAFileUpload_Negative.txt";
      String methodName = "stripe_retrieveAFileUpload";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));

      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getJSONObject("error").getString("message"), "No such fileupload: " + jsonObject.get("fileId"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }

   /**
    * Positive test case for getListOfAllFileUploads method.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getListOfAllFileUploads} integration test with mandatory parameters.")
   public void testGetListOfAllFileUploadsWithPositiveCase() throws Exception {
      String methodName = "stripe_getListOfAllFileUploads";
      String modifiedJsonString = String.format("{\"apiKey\":\"%s\"}", stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/files");
      } finally {

         proxyAdmin.deleteProxy(methodName);
      }

   }

   /**
    * Positive test case for getListOfAllFileUploads method with optional parameters.
    */
   @Test(enabled = false, groups = {"wso2.esb"}, description = "stripe {getListOfAllFileUploads} integration test with optional parameters.")
   public void testGetListOfAllFileUploadsWithOptionalParameters() throws Exception {
      String jsonRequestFilePath = pathToRequestsDirectory + "getListOfAllFileUploads_Optional.txt";
      String methodName = "stripe_getListOfAllFileUploads";
      final String jsonString = ConnectorIntegrationUtil.getFileContent(jsonRequestFilePath);
      String modifiedJsonString = String.format(jsonString, stripeConnectorProperties.getProperty("apiKey"));
      final String proxyFilePath = "file:///" + pathToProxiesDirectory + methodName + ".xml";
      proxyAdmin.addProxyService(new DataHandler(new URL(proxyFilePath)));
      JSONObject jsonResponse;
      try {
         jsonResponse = ConnectorIntegrationUtil.sendRequest(getProxyServiceURL(methodName), modifiedJsonString);
         JSONObject jsonObject = new JSONObject(jsonString);
         Assert.assertEquals(jsonResponse.getString("object"), "list");
         Assert.assertEquals(jsonResponse.getString("url"), "/v1/files");
         Assert.assertEquals(jsonResponse.getJSONArray("data").length(), jsonObject.get("limit"));
      } finally {
         proxyAdmin.deleteProxy(methodName);
      }
   }
}
