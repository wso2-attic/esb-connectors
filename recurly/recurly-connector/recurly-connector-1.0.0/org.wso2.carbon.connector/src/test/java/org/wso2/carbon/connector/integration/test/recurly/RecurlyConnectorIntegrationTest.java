/*
 *  Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.integration.test.recurly;

import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.Base64;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class RecurlyConnectorIntegrationTest extends ConnectorIntegrationTestBase {
   
   private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
   
   private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
   
   private String apiUrl;
   
   /**
    * Set up the environment.
    */
   @BeforeClass(alwaysRun = true)
   public void setEnvironment() throws Exception {
   
      init("recurly-connector-1.0.0");
      
      esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
      esbRequestHeadersMap.put("Content-Type", "application/xml");
      esbRequestHeadersMap.put("Accept", "application/xml");
      
      apiRequestHeadersMap.putAll(esbRequestHeadersMap);
      
      final String base64AuthString = Base64.encode(connectorProperties.getProperty("apiKey").getBytes());
      apiRequestHeadersMap.put("Authorization", "Basic " + base64AuthString);
      
      apiUrl = connectorProperties.getProperty("apiUrl") + "/v2";
   }
   
   /**
    * Positive test case for createAccount method with mandatory parameters.
    */
   @Test(priority = 1, groups = { "wso2.esb" }, description = "recurly {createAccount} integration test with mandatory parameters.")
   public void testCreateAccountWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:createAccount");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAccount_mandatory.xml");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      
      String accountCode = connectorProperties.getProperty("accountCode");
      
      String apiEndPoint = apiUrl + "/accounts/" + accountCode;
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(getValueByExpression("//account_code", esbRestResponse.getBody()), accountCode);
      Assert.assertEquals(getValueByExpression("//hosted_login_token", esbRestResponse.getBody()),
            getValueByExpression("//hosted_login_token", apiRestResponse.getBody()));
   }
   
   /**
    * Positive test case for createAccount method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAccountWithMandatoryParameters" }, description = "recurly {createAccount} integration test with optional parameters.")
   public void testCreateAccountWithOptionalParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:createAccount");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAccount_optional.xml");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      
      String accountCode = connectorProperties.getProperty("accountCodeOpt");
      
      String apiEndPoint = apiUrl + "/accounts/" + accountCode;
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(getValueByExpression("//username", esbRestResponse.getBody()),
            getValueByExpression("//username", apiRestResponse.getBody()));
      Assert.assertEquals(getValueByExpression("//email", esbRestResponse.getBody()),
            getValueByExpression("//email", apiRestResponse.getBody()));
   }
   
   /**
    * Negative test case for createAccount method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAccountWithOptionalParameters" }, description = "recurly {createAccount} integration test with negative case.")
   public void testCreateAccountWithNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:createAccount");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createAccount_negative.xml");
      
      String apiEndPoint = apiUrl + "/accounts";
      RestResponse<OMElement> apiRestResponse =
            sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createAccount_negative.xml");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
      Assert.assertEquals(getValueByExpression("//error", esbRestResponse.getBody()),
            getValueByExpression("//error", apiRestResponse.getBody()));
   }
   
   /**
    * Positive test case for listAccounts method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateAccountWithNegativeCase" }, description = "recurly {listAccounts} integration test with mandatory parameters.")
   public void testListAccountsWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listAccounts");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAccounts_mandatory.xml");
      
      String apiEndPoint = apiUrl + "/accounts";
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(getValueByExpression("count(//account)", esbRestResponse.getBody()),
            getValueByExpression("count(//account)", apiRestResponse.getBody()));
      Assert.assertEquals(getValueByExpression("//account[0]/account_code", esbRestResponse.getBody()),
            getValueByExpression("//account[0]/account_code", apiRestResponse.getBody()));
   }
   
   /**
    * Positive test case for listAccounts method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListAccountsWithMandatoryParameters" }, description = "recurly {listAccounts} integration test with optional parameters.")
   public void testListAccountsWithOptionalParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listAccounts");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAccounts_optional.xml");
      
      String apiEndPoint = apiUrl + "/accounts?per_page=3&state=active";
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(getValueByExpression("count(//account)", esbRestResponse.getBody()),
            getValueByExpression("count(//account)", apiRestResponse.getBody()));
      Assert.assertEquals(getValueByExpression("//account[0]/account_code", esbRestResponse.getBody()),
            getValueByExpression("//account[0]/account_code", apiRestResponse.getBody()));
   }
   
   /**
    * Negative test case for listAccounts method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListAccountsWithOptionalParameters" }, description = "recurly {listAccounts} integration test with negative case.")
   public void testListAccountsWithNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listAccounts");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listAccounts_negative.xml");
      
      String apiEndPoint = apiUrl + "/accounts?state=invalid";
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(getValueByExpression("//symbol", esbRestResponse.getBody()),
            getValueByExpression("//symbol", apiRestResponse.getBody()));
   }
   
   /**
    * Positive test case for getAccountByCode method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListAccountsWithNegativeCase" }, description = "recurly {getAccountByCode} integration test with mandatory parameters.")
   public void testGetAccountByCodeWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:getAccountByCode");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAccountByCode_mandatory.xml");
      
      String apiEndPoint = apiUrl + "/accounts/" + connectorProperties.getProperty("accountCodeOpt");
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(getValueByExpression("//username", apiRestResponse.getBody()),
            getValueByExpression("//username", esbRestResponse.getBody()));
   }
   
   /**
    * Negative test case for getAccountByCode method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetAccountByCodeWithMandatoryParameters" }, description = "recurly {getAccountByCode} integration test with negative case.")
   public void testGetAccountByCodeWithNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:getAccountByCode");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getAccountByCode_negative.xml");
      
      String apiEndPoint = apiUrl + "/accounts/INVALID" + connectorProperties.getProperty("accountCode");
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(getValueByExpression("//description", apiRestResponse.getBody()),
            getValueByExpression("//description", esbRestResponse.getBody()));
   }
   
   /**
    * Positive test case for createPlan method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetAccountByCodeWithNegativeCase" }, description = "recurly {createPlan} integration test with mandatory parameters.")
   public void testCreatePlanWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:createPlan");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPlan_mandatory.xml");
      String planCode = connectorProperties.getProperty("planCode");
      String apiEndPoint = apiUrl + "/plans/" + planCode;
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(getValueByExpression("//plan_code", esbRestResponse.getBody()),
            getValueByExpression("//plan_code", apiRestResponse.getBody()));
      Assert.assertEquals(getValueByExpression("//name", esbRestResponse.getBody()),
            getValueByExpression("//name", apiRestResponse.getBody()));
   }
   
   /**
    * Positive test case for createPlan method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePlanWithMandatoryParameters" }, description = "recurly {createPlan} integration test with optional parameters.")
   public void testCreatePlanWithOptionalParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:createPlan");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPlan_optional.xml");
      String planCode = connectorProperties.getProperty("planCodeOptional");
      String apiEndPoint = apiUrl + "/plans/" + planCode;
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(getValueByExpression("//plan_code", esbRestResponse.getBody()),
            getValueByExpression("//plan_code", apiRestResponse.getBody()));
      Assert.assertEquals(getValueByExpression("//name", esbRestResponse.getBody()),
            getValueByExpression("//name", apiRestResponse.getBody()));
      Assert.assertEquals(getValueByExpression("//accountingCode", esbRestResponse.getBody()),
            getValueByExpression("//accountingCode", apiRestResponse.getBody()));
   }
   
   /**
    * Negative test case for createPlan method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePlanWithOptionalParameters" }, description = "recurly {createPlan} integration test for negative case.")
   public void testCreatePlanWithNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:createPlan");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPlan_negative.xml");
      String apiEndPoint = apiUrl + "/plans";
      RestResponse<OMElement> apiRestResponse =
            sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createPlan_negative.xml");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
      Assert.assertEquals(getValueByExpression("//error", esbRestResponse.getBody()),
            getValueByExpression("//error", apiRestResponse.getBody()));
   }
   
   /**
    * Positive test case for getPlanByCode method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePlanWithNegativeCase" }, description = "recurly {getPlanByCode} integration test with mandatory parameters.")
   public void testGetPlanByCodeWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:getPlanByCode");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPlanByCode_mandatory.xml");
      String planCode = connectorProperties.getProperty("planCode");
      String apiEndPoint = apiUrl + "/plans/" + planCode;
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(getValueByExpression("//plan_code", esbRestResponse.getBody()),
            getValueByExpression("//plan_code", apiRestResponse.getBody()));
      Assert.assertEquals(getValueByExpression("//name", esbRestResponse.getBody()),
            getValueByExpression("//name", apiRestResponse.getBody()));
   }
   
   /**
    * Negative test case for getPlanByCode method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetPlanByCodeWithMandatoryParameters" }, description = "recurly {getPlanByCode} integration test negative case.")
   public void testGetPlanByCodeWithNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:getPlanByCode");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPlanByCode_negative.xml");
      
      String apiEndPoint = apiUrl + "/plans/Invalid";
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(getValueByExpression("//error", esbRestResponse.getBody()),
            getValueByExpression("//error", apiRestResponse.getBody()));
   }
   
   /**
    * Positive test case for listPlans method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetPlanByCodeWithNegativeCase" }, description = "recurly {listPlans} integration test with mandatory parameters.")
   public void testListPlansWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listPlans");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPlans_mandatory.xml");
      
      String apiEndPoint = apiUrl + "/plans";
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(getValueByExpression("count(//plan)", esbRestResponse.getBody()),
            getValueByExpression("count(//plan)", apiRestResponse.getBody()));
      Assert.assertEquals(getValueByExpression("//plan[0]/plan_code", esbRestResponse.getBody()),
            getValueByExpression("//plan[0]/plan_code", apiRestResponse.getBody()));
   }
   
   /**
    * Positive test case for listPlans method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListPlansWithMandatoryParameters" }, description = "recurly {listPlans} integration test with optional parameters.")
   public void testListPlansWithOptionalParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listPlans");
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPlans_optional.xml");
      
      String perPage = connectorProperties.getProperty("perPage");
      
      String apiEndPoint = apiUrl + "/plans?per_page=" + perPage;
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(getValueByExpression("count(//plan)", esbRestResponse.getBody()),
            getValueByExpression("count(//plan)", apiRestResponse.getBody()));
      Assert.assertEquals(getValueByExpression("//plan[0]/plan_code", esbRestResponse.getBody()),
            getValueByExpression("//plan[0]/plan_code", apiRestResponse.getBody()));
      Assert.assertEquals(getValueByExpression("//plan[0]/plan_code", esbRestResponse.getBody()),
            getValueByExpression("//plan[0]/plan_code", apiRestResponse.getBody()));
   }
   
   /**
    * Positive test case for createSubscription method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListPlansWithOptionalParameters" }, description = "recurly {createSubscription} integration test with mandatory parameters.")
   public void testCreateSubscriptionWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:createSubscription");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createSubscription_mandatory.xml");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      String subscriptionId = getValueByExpression("//uuid", esbRestResponse.getBody());
      connectorProperties.setProperty("subscriptionId", subscriptionId);
      
      String apiEndPoint = apiUrl + "/subscriptions/" + subscriptionId;
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(getValueByExpression("//plan_code", apiRestResponse.getBody()),
            connectorProperties.getProperty("planCode"));
      Assert.assertEquals(getValueByExpression("//account/@href", apiRestResponse.getBody()), apiUrl + "/accounts/"
            + connectorProperties.getProperty("accountCodeOpt"));
   }
   
   /**
    * Positive test case for createSubscription method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateSubscriptionWithMandatoryParameters" }, description = "recurly {createSubscription} integration test with optional parameters.")
   public void testCreateSubscriptionWithOptionalParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:createSubscription");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createSubscription_optional.xml");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      String subscriptionId = getValueByExpression("//uuid", esbRestResponse.getBody());
      
      String apiEndPoint = apiUrl + "/subscriptions/" + subscriptionId;
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(getValueByExpression("//plan_code", apiRestResponse.getBody()),
            connectorProperties.getProperty("planCodeOptional"));
      Assert.assertEquals(getValueByExpression("//terms_and_conditions", apiRestResponse.getBody()),
            connectorProperties.getProperty("description"));
   }
   
   /**
    * Negative test case for createSubscription method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateSubscriptionWithOptionalParameters" }, description = "recurly {createSubscription} integration test with negative case.")
   public void testCreateSubscriptionWithNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:createSubscription");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createSubscription_negative.xml");
      
      String apiEndPoint = apiUrl + "/subscriptions";
      RestResponse<OMElement> apiRestResponse =
            sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createSubscription_negative.xml");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
      Assert.assertEquals(getValueByExpression("//error", esbRestResponse.getBody()),
            getValueByExpression("//error", apiRestResponse.getBody()));
   }
   
   /**
    * Positive test case for getSubscriptionById method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateSubscriptionWithNegativeCase" }, description = "recurly {getSubscriptionById} integration test with mandatory parameters.")
   public void testGetSubscriptionByIdWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:getSubscriptionById");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSubscriptionById_mandatory.xml");
      
      String apiEndPoint = apiUrl + "/subscriptions/" + connectorProperties.getProperty("subscriptionId");
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(getValueByExpression("//plan_code", esbRestResponse.getBody()),
            getValueByExpression("//plan_code", apiRestResponse.getBody()));
      Assert.assertEquals(getValueByExpression("//currency", esbRestResponse.getBody()),
            getValueByExpression("//currency", apiRestResponse.getBody()));
   }
   
   /**
    * Negative test case for getSubscriptionById method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetSubscriptionByIdWithMandatoryParameters" }, description = "recurly {getSubscriptionById} integration test with negative case.")
   public void testGetSubscriptionByIdWithNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:getSubscriptionById");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getSubscriptionById_negative.xml");
      
      String apiEndPoint = apiUrl + "/subscriptions/invalid";
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(getValueByExpression("//description", esbRestResponse.getBody()),
            getValueByExpression("//description", apiRestResponse.getBody()));
   }
   
   /**
    * Positive test case for listSubscriptions method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetSubscriptionByIdWithNegativeCase" }, description = "recurly {listSubscriptions} integration test with mandatory parameters.")
   public void testListSubscriptionsWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listSubscriptions");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSubscriptions_mandatory.xml");
      
      String apiEndPoint = apiUrl + "/subscriptions";
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(getValueByExpression("count(//subscription)", esbRestResponse.getBody()),
            getValueByExpression("count(//subscription)", apiRestResponse.getBody()));
      Assert.assertEquals(getValueByExpression("//subscription[0]/uuid", esbRestResponse.getBody()),
            getValueByExpression("//subscription[0]/uuid", apiRestResponse.getBody()));
   }
   
   /**
    * Positive test case for listSubscriptions method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListSubscriptionsWithMandatoryParameters" }, description = "recurly {listSubscriptions} integration test with optional parameters.")
   public void testListSubscriptionsWithOptionalParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listSubscriptions");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSubscriptions_optional.xml");
      
      String apiEndPoint = apiUrl + "/subscriptions?per_page=1&state=active";
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(getValueByExpression("count(//subscription)", esbRestResponse.getBody()),
            getValueByExpression("count(//subscription)", apiRestResponse.getBody()));
      Assert.assertEquals(getValueByExpression("//subscription[0]/uuid", esbRestResponse.getBody()),
            getValueByExpression("//subscription[0]/uuid", apiRestResponse.getBody()));
   }
   
   /**
    * Negative test case for listSubscriptions method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListSubscriptionsWithOptionalParameters" }, description = "recurly {listSubscriptions} integration test with negative case.")
   public void testListSubscriptionsWithNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listSubscriptions");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listSubscriptions_negative.xml");
      
      String apiEndPoint = apiUrl + "/subscriptions?state=invalid";
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(getValueByExpression("//description", esbRestResponse.getBody()),
            getValueByExpression("//description", apiRestResponse.getBody()));
   }
   
   /**
    * Positive test case for createCoupon method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListSubscriptionsWithNegativeCase" }, description = "recurly {createCoupon} integration test with mandatory parameters.")
   public void testCreateCouponWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:createCoupon");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCoupon_mandatory.xml");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      
      String apiEndPoint = apiUrl + "/coupons/" + connectorProperties.getProperty("couponCode");
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(getValueByExpression("//discount_percent", apiRestResponse.getBody()),
            connectorProperties.getProperty("discountPercentage"));
      Assert.assertEquals(getValueByExpression("//name", apiRestResponse.getBody()),
            connectorProperties.getProperty("nameString"));
   }
   
   /**
    * Positive test case for createCoupon method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCouponWithMandatoryParameters" }, description = "recurly {createCoupon} integration test with optional parameters.")
   public void testCreateCouponWithOptionalParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:createCoupon");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCoupon_optional.xml");
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
      
      String apiEndPoint = apiUrl + "/coupons/" + connectorProperties.getProperty("couponCodeOpt");
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(getValueByExpression("//discount_percent", apiRestResponse.getBody()),
            connectorProperties.getProperty("discountPercentage"));
      Assert.assertEquals(getValueByExpression("//description", apiRestResponse.getBody()),
            connectorProperties.getProperty("description"));
   }
   
   /**
    * Negative test case for createCoupon method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCouponWithOptionalParameters" }, description = "recurly {createCoupon} integration test with negative case.")
   public void testCreateCouponWithNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:createCoupon");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCoupon_negative.xml");
      
      String apiEndPoint = apiUrl + "/coupons";
      RestResponse<OMElement> apiRestResponse =
            sendXmlRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createCoupon_negative.xml");
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 422);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 422);
      Assert.assertEquals(getValueByExpression("//error", esbRestResponse.getBody()),
            getValueByExpression("//error", apiRestResponse.getBody()));
   }
   
   /**
    * Positive test case for getCouponByCode method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCouponWithNegativeCase" }, description = "recurly {getCouponByCode} integration test with mandatory parameters.")
   public void testGetCouponByCodeWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:getCouponByCode");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCouponByCode_mandatory.xml");
      
      String apiEndPoint = apiUrl + "/coupons/" + connectorProperties.getProperty("couponCode");
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(getValueByExpression("//discount_percent", apiRestResponse.getBody()),
            getValueByExpression("//discount_percent", esbRestResponse.getBody()));
      Assert.assertEquals(getValueByExpression("//name", apiRestResponse.getBody()),
            getValueByExpression("//name", esbRestResponse.getBody()));
   }
   
   /**
    * Negative test case for getCouponByCode method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetCouponByCodeWithMandatoryParameters" }, description = "recurly {getCouponByCode} integration test with negative case.")
   public void testGetCouponByCodeWithNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:getCouponByCode");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCouponByCode_negative.xml");
      
      String apiEndPoint = apiUrl + "/coupons/INVALID" + connectorProperties.getProperty("couponCode");
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
      Assert.assertEquals(getValueByExpression("//description", apiRestResponse.getBody()),
            getValueByExpression("//description", esbRestResponse.getBody()));
   }
   
   /**
    * Positive test case for listCoupons method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testGetCouponByCodeWithNegativeCase" }, description = "recurly {listCoupons} integration test with mandatory parameters.")
   public void testListCouponsWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listCoupons");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCoupons_mandatory.xml");
      
      String apiEndPoint = apiUrl + "/coupons";
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(getValueByExpression("count(//coupon)", esbRestResponse.getBody()),
            getValueByExpression("count(//coupon)", apiRestResponse.getBody()));
      Assert.assertEquals(getValueByExpression("//coupon[0]/coupon_code", esbRestResponse.getBody()),
            getValueByExpression("//coupon[0]/coupon_code", apiRestResponse.getBody()));
   }
   
   /**
    * Positive test case for listCoupons method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListCouponsWithMandatoryParameters" }, description = "recurly {listCoupons} integration test with optional parameters.")
   public void testListCouponsWithOptionalParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listCoupons");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCoupons_optional.xml");
      
      String apiEndPoint = apiUrl + "/coupons?per_page=1&state=redeemable";
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(getValueByExpression("count(//coupon)", esbRestResponse.getBody()),
            getValueByExpression("count(//coupon)", apiRestResponse.getBody()));
      Assert.assertEquals(getValueByExpression("//coupon[0]/coupon_code", esbRestResponse.getBody()),
            getValueByExpression("//coupon[0]/coupon_code", apiRestResponse.getBody()));
   }
   
   /**
    * Negative test case for listCoupons method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListCouponsWithOptionalParameters" }, description = "recurly {listCoupons} integration test with negative case.")
   public void testListCouponsWithNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listCoupons");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCoupons_negative.xml");
      
      String apiEndPoint = apiUrl + "/coupons?state=invalid";
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(getValueByExpression("//description", esbRestResponse.getBody()),
            getValueByExpression("//description", apiRestResponse.getBody()));
   }
   
   /**
    * Positive test case for listInvoices method with mandatory parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListCouponsWithNegativeCase" }, description = "recurly {listInvoices} integration test with mandatory parameters.")
   public void testListInvoicesWithMandatoryParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listInvoices");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoices_mandatory.xml");
      
      String apiEndPoint = apiUrl + "/invoices";
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(getValueByExpression("count(//invoice)", esbRestResponse.getBody()),
            getValueByExpression("count(//invoice)", apiRestResponse.getBody()));
      Assert.assertEquals(getValueByExpression("//invoice[0]/uuid", esbRestResponse.getBody()),
            getValueByExpression("//invoice[0]/uuid", apiRestResponse.getBody()));
   }
   
   /**
    * Positive test case for listInvoices method with optional parameters.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListInvoicesWithMandatoryParameters" }, description = "recurly {listInvoices} integration test with optional parameters.")
   public void testListInvoicesWithOptionalParameters() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listInvoices");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoices_optional.xml");
      
      String apiEndPoint = apiUrl + "/invoices?per_page=1&state=collected";
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
      Assert.assertEquals(getValueByExpression("count(//invoice)", esbRestResponse.getBody()),
            getValueByExpression("count(//invoice)", apiRestResponse.getBody()));
      Assert.assertEquals(getValueByExpression("//invoice[0]/uuid", esbRestResponse.getBody()),
            getValueByExpression("//invoice[0]/uuid", apiRestResponse.getBody()));
   }
   
   /**
    * Negative test case for listInvoices method.
    */
   @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListInvoicesWithOptionalParameters" }, description = "recurly {listInvoices} integration test with negative case.")
   public void testListInvoicesWithNegativeCase() throws Exception {
   
      esbRequestHeadersMap.put("Action", "urn:listInvoices");
      
      RestResponse<OMElement> esbRestResponse =
            sendXmlRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listInvoices_negative.xml");
      
      String apiEndPoint = apiUrl + "/invoices?state=invalid";
      RestResponse<OMElement> apiRestResponse = sendXmlRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
      
      Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
      Assert.assertEquals(getValueByExpression("//description", esbRestResponse.getBody()),
            getValueByExpression("//description", apiRestResponse.getBody()));
   }
}
