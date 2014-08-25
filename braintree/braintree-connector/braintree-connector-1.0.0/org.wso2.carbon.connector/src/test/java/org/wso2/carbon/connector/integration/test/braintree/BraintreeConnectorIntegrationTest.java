/*
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

package org.wso2.carbon.connector.integration.test.braintree;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CreditCard;
import com.braintreegateway.CreditCardRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.MerchantAccount;
import com.braintreegateway.MerchantAccountRequest;
import com.braintreegateway.ResourceCollection;
import com.braintreegateway.Result;
import com.braintreegateway.SettlementBatchSummary;
import com.braintreegateway.Subscription;
import com.braintreegateway.SubscriptionRequest;
import com.braintreegateway.SubscriptionSearchRequest;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.TransactionSearchRequest;
import com.braintreegateway.exceptions.BraintreeException;
import com.braintreegateway.exceptions.NotFoundException;

public class BraintreeConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

    private Map<String, String> parametersMap = new HashMap<String, String>();

    private BraintreeGateway gateway;

    private String resourceNotFoundExceptionMessage;

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        init("braintree-connector-1.0.0");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");

        Environment environment = null;
        if (connectorProperties.getProperty("environment").equalsIgnoreCase("sandbox")) {
            environment = Environment.SANDBOX;
        } else if (connectorProperties.getProperty("environment").equalsIgnoreCase("production")) {
            environment = Environment.PRODUCTION;
        } else if (connectorProperties.getProperty("environment").equalsIgnoreCase("development")) {
            environment = Environment.DEVELOPMENT;
        }

        gateway = new BraintreeGateway(environment, connectorProperties.getProperty("merchantId"),
                connectorProperties.getProperty("publicKey"), connectorProperties.getProperty("privateKey"));

        resourceNotFoundExceptionMessage = connectorProperties.getProperty("resourceNotFoundException");

    }

    /**
     * Positive test case for createCreditCard with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Braintree {createCreditCard} integration test with mandatory parameters.")
    public void testCreateCreditCardWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createCreditCard");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createCreditCard_mandatory.json");
        final JSONObject jObject = esbRestResponse.getBody().getJSONObject("target");

        final String creditCardToken = jObject.getString("token");
        connectorProperties.setProperty("token", creditCardToken);

        final CreditCard creditCard = gateway.creditCard().find(creditCardToken);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(creditCardToken, creditCard.getToken());
        Assert.assertEquals(jObject.getString("customerId"), creditCard.getCustomerId());
        Assert.assertEquals(jObject.getString("expirationMonth"), creditCard.getExpirationMonth());
        Assert.assertEquals(jObject.getString("expirationYear"), creditCard.getExpirationYear());
    }

    /**
     * Positive test case for createCreditCard with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Braintree {createCreditCard} integration test with optional parameters.")
    public void testCreateCreditCardWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createCreditCard");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createCreditCard_optional.json");
        final JSONObject jObject = esbRestResponse.getBody().getJSONObject("target");

        final String creditCardToken = jObject.getString("token");
        connectorProperties.setProperty("updateToken", creditCardToken);

        final CreditCard creditCard = gateway.creditCard().find(creditCardToken);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(creditCardToken, creditCard.getToken());
        Assert.assertEquals(jObject.getString("customerId"), creditCard.getCustomerId());
        Assert.assertEquals(jObject.getString("expirationMonth"), creditCard.getExpirationMonth());
        Assert.assertEquals(jObject.getString("expirationYear"), creditCard.getExpirationYear());

        // Asserting the optional cardholderName
        Assert.assertEquals(jObject.getString("cardholderName"), creditCard.getCardholderName());
        Assert.assertEquals(jObject.getString("cardholderName"), connectorProperties.getProperty("cardholderName"));
    }

    /**
     * Negative test case for createCreditCard.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Braintree {createCreditCard} integration test with negative case.")
    public void testCreateCreditCardWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createCreditCard");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createCreditCard_negative.json");
        final String esbErrorMessage = esbRestResponse.getBody().getJSONObject("errors").getJSONObject("nestedErrors")
                .getJSONObject("credit-card").getJSONArray("errors").getJSONObject(0).getString("message");

        final CreditCardRequest request = new CreditCardRequest()
                .customerId(connectorProperties.getProperty("customerId")).number("INVALID")
                .expirationDate(connectorProperties.getProperty("expirationDate"));

        Result<CreditCard> result = gateway.creditCard().create(request);

        Assert.assertEquals(esbErrorMessage, result.getMessage());
    }

    /**
     * Positive test case for updateCreditCard with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateCreditCardWithMandatoryParameters" }, description = "Braintree {updateCreditCard} integration test with Optional parameters.")
    public void testUpdateCreditCardWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updateCreditCard");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_updateCreditCard_optional.json");
        final JSONObject jObject = esbRestResponse.getBody().getJSONObject("target");

        final String expirationDate = connectorProperties.getProperty("expirationDateUpdate");
        final String updatedMonth = expirationDate.split("/")[0];
        final String updatedYear = expirationDate.split("/")[1];

        Assert.assertEquals(jObject.getString("expirationMonth"), updatedMonth);
        Assert.assertEquals(jObject.getString("expirationYear"), updatedYear);

        final CreditCard creditCard = gateway.creditCard().find(connectorProperties.getProperty("token"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(jObject.getString("expirationMonth"), creditCard.getExpirationMonth());
        Assert.assertEquals(jObject.getString("expirationYear"), creditCard.getExpirationYear());

    }

    /**
     * Negative test case for updateCreditCard.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Braintree {updateCreditCard} integration test with negative case.", expectedExceptions = NotFoundException.class)
    public void testUpdateCreditCardWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updateCreditCard");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_updateCreditCard_negative.json");
        Assert.assertEquals(esbRestResponse.getBody().getString("error_message"), resourceNotFoundExceptionMessage);
        final CreditCardRequest updateRequest = new CreditCardRequest().expirationDate(connectorProperties
                .getProperty("expirationDateUpdate"));
        gateway.creditCard().update("INVALID", updateRequest);
    }

    /**
     * Positive test case for getCreditCard with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateCreditCardWithMandatoryParameters" }, description = "Braintree {getCreditCard} integration test with Mandatory parameters.")
    public void testGetCreditCardWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getCreditCard");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getCreditCard_mandatory.json");
        final CreditCard creditCard = gateway.creditCard().find(connectorProperties.getProperty("token"));
        Assert.assertEquals(esbRestResponse.getBody().getString("last4"), creditCard.getLast4());
        Assert.assertEquals(esbRestResponse.getBody().getString("customerId"), creditCard.getCustomerId());
        Assert.assertEquals(esbRestResponse.getBody().getString("uniqueNumberIdentifier"),
                creditCard.getUniqueNumberIdentifier());
    }

    /**
     * Negative test case for getCreditCard method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Braintree {getCreditCard} integration negative test case.", expectedExceptions = NotFoundException.class)
    public void testGetCreditCardNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getCreditCard");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getCreditCard_negative.json");
        Assert.assertEquals(esbRestResponse.getBody().getString("error_message"), resourceNotFoundExceptionMessage);
        gateway.creditCard().find("INVALID");
    }

    /**
     * Positive test case for createSubscription with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateCreditCardWithMandatoryParameters" }, description = "Braintree {createSubscription} integration test with mandatory parameters.")
    public void testCreateSubscriptionWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createSubscription");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createSubscription_mandatory.json");
        final JSONObject jObject = esbRestResponse.getBody().getJSONObject("target");

        final String subscriptionId = jObject.getString("id");
        connectorProperties.setProperty("subscriptionId", subscriptionId);

        Subscription subscription = gateway.subscription().find(subscriptionId);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(subscriptionId, subscription.getId());
        Assert.assertEquals(jObject.getString("merchantAccountId"), subscription.getMerchantAccountId());
        Assert.assertEquals(jObject.getString("paymentMethodToken"), subscription.getPaymentMethodToken());
        Assert.assertEquals(jObject.getString("planId"), subscription.getPlanId());
        Assert.assertEquals(jObject.getJSONArray("addOns").length(), subscription.getAddOns().size());
        Assert.assertEquals(jObject.getJSONArray("discounts").length(), subscription.getDiscounts().size());
    }

    /**
     * Positive test case for createSubscription with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateCreditCardWithMandatoryParameters" }, description = "Braintree {createSubscription} integration test with optional parameters.")
    public void testCreateSubscriptionWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createSubscription");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createSubscription_optional.json");
        final JSONObject jObject = esbRestResponse.getBody().getJSONObject("target");

        final String subscriptionId = jObject.getString("id");
        Subscription subscription = gateway.subscription().find(subscriptionId);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(subscriptionId, subscription.getId());
        Assert.assertEquals(subscriptionId, connectorProperties.getProperty("opionalSubscriptionId"));
        Assert.assertEquals(jObject.getString("merchantAccountId"), subscription.getMerchantAccountId());
        Assert.assertEquals(jObject.getString("paymentMethodToken"), subscription.getPaymentMethodToken());
        Assert.assertEquals(jObject.getString("planId"), subscription.getPlanId());
    }

    /**
     * Negative test case for createSubscription.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Braintree {createSubscription} integration test with negative case.")
    public void testCreateSubscriptionWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createSubscription");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createSubscription_negative.json");
        final String esbErrorMessage = esbRestResponse.getBody().getJSONObject("errors").getJSONObject("nestedErrors")
                .getJSONObject("subscription").getJSONArray("errors").getJSONObject(0).getString("message");

        final SubscriptionRequest request = new SubscriptionRequest().paymentMethodToken("INVALID").planId(
                connectorProperties.getProperty("planId"));
        final Result<Subscription> result = gateway.subscription().create(request);

        Assert.assertEquals(esbErrorMessage, result.getMessage());
    }

    /**
     * Positive test case for updateSubscription with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateSubscriptionWithMandatoryParameters",
            "testCreateCreditCardWithOptionalParameters" }, description = "Braintree {updateSubscription} integration test with Optional parameters.")
    public void testUpdateSubscriptionWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updateSubscription");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_updateSubscription_optional.json");
        final JSONObject jObject = esbRestResponse.getBody().getJSONObject("target");

        final String newSubscriptionId = jObject.getString("id");
        final Subscription subscription = gateway.subscription().find(newSubscriptionId);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(jObject.getString("id"), subscription.getId());
        Assert.assertEquals(jObject.getString("id"), connectorProperties.getProperty("updateSubscriptionId"));
        Assert.assertNotEquals(jObject.getString("id"), connectorProperties.getProperty("subscriptionId"));

        Assert.assertEquals(jObject.getString("paymentMethodToken"), subscription.getPaymentMethodToken());
        Assert.assertEquals(jObject.getString("paymentMethodToken"), connectorProperties.getProperty("updateToken"));
        Assert.assertNotEquals(jObject.getString("paymentMethodToken"), connectorProperties.getProperty("token"));
    }

    /**
     * Negative test case for updateSubscription.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Braintree {updateSubscription} integration test with negative case.", expectedExceptions = NotFoundException.class)
    public void testUpdateSubscriptionWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updateSubscription");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_updateSubscription_negative.json");

        Assert.assertEquals(esbRestResponse.getBody().getString("error_message"), resourceNotFoundExceptionMessage);

        final SubscriptionRequest request = new SubscriptionRequest().id(connectorProperties
                .getProperty("newSubscriptionId"));
        gateway.subscription().update("INVALID", request);

    }

    /**
     * Positive test case for searchSubscriptions with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateSubscriptionWithMandatoryParameters",
            "testCreateSubscriptionWithOptionalParameters" }, description = "Braintree {searchSubscriptions} integration test with Mandatory parameters.")
    public void testSearchSubscriptionsWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:searchSubscriptions");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_searchSubscriptions_mandatory.json");

        final SubscriptionSearchRequest apiRequest = new SubscriptionSearchRequest();
        final ResourceCollection<Subscription> apiResultCollection = gateway.subscription().search(apiRequest);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("ids").length(),
                apiResultCollection.getMaximumSize());

        if (esbRestResponse.getBody().getJSONArray("ids").length() > 0) {
            Assert.assertEquals(esbRestResponse.getBody().getJSONArray("ids").getString(0), apiResultCollection
                    .getFirst().getId());
        }
    }

    /**
     * Positive test case for searchSubscriptions with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateSubscriptionWithMandatoryParameters",
            "testCreateSubscriptionWithOptionalParameters" }, description = "Braintree {searchSubscriptions} integration test with optional parameters.")
    public void testSearchSubscriptionsWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:searchSubscriptions");

        final String isInTrialPeriod = "true";
        final String billingCycles = "10";
        parametersMap.put("searchSubscriptionsIsInTrialPeriod", isInTrialPeriod);
        parametersMap.put("billingCyclesRemainingIsLessThanOrEqualTo", billingCycles);

        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_searchSubscriptions_optional.json", parametersMap);

        final SubscriptionSearchRequest apiRequest = new SubscriptionSearchRequest().inTrialPeriod()
                .is(Boolean.valueOf(isInTrialPeriod)).billingCyclesRemaining().lessThanOrEqualTo(billingCycles);
        final ResourceCollection<Subscription> apiResultCollection = gateway.subscription().search(apiRequest);

        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("ids").length(),
                apiResultCollection.getMaximumSize());

        if (esbRestResponse.getBody().getJSONArray("ids").length() > 0) {
            Assert.assertEquals(esbRestResponse.getBody().getJSONArray("ids").getString(0), apiResultCollection
                    .getFirst().getId());
        }
    }

    /**
     * Positive test case for getSubscription with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateSubscriptionWithMandatoryParameters" }, description = "Braintree {getSubscription} integration test with Mandatory parameters.")
    public void testGetSubscriptionWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getSubscription");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getSubscription_mandatory.json");

        final Subscription apiResult = gateway.subscription().find(connectorProperties.getProperty("subscriptionId"));

        Assert.assertEquals(esbRestResponse.getBody().getString("billingDayOfMonth"), apiResult.getBillingDayOfMonth()
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().getString("hasTrialPeriod"), apiResult.hasTrialPeriod()
                .toString());
        Assert.assertEquals(esbRestResponse.getBody().getString("paymentMethodToken"),
                apiResult.getPaymentMethodToken());

    }

    /**
     * Negative test case for getSubscription method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Braintree {getSubscription} integration negative test case.", expectedExceptions = NotFoundException.class)
    public void testGetSubscriptionNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getSubscription");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getSubscription_negative.json");

        Assert.assertEquals(esbRestResponse.getBody().getString("error_message"), resourceNotFoundExceptionMessage);

        gateway.subscription().find("INVALID");

    }

    /**
     * Positive test case for cancelSubscription with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testUpdateSubscriptionWithOptionalParameters",
            "testSearchSubscriptionsWithMandatoryParameters", "testSearchSubscriptionsWithOptionalParameters",
            "testGetSubscriptionWithMandatoryParameters" }, description = "Braintree {cancelSubscription} integration test with Mandatory parameters.", expectedExceptions = { NotFoundException.class })
    public void testCancelSubscriptionWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:cancelSubscription");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_cancelSubscription_mandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        gateway.subscription().find(connectorProperties.getProperty("subscriptionId"));
    }

    /**
     * Negative test case for cancelSubscription.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Braintree {cancelSubscription} integration test with negative case.", expectedExceptions = { NotFoundException.class })
    public void testCancelSubscriptionWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:cancelSubscription");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_cancelSubscription_negative.json");
        Assert.assertEquals(esbRestResponse.getBody().getString("error_message"), resourceNotFoundExceptionMessage);
        gateway.subscription().cancel("INVALID");
    }

    /**
     * Positive test case for deleteCreditCard with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCancelSubscriptionWithMandatoryParameters" }, description = "Braintree {deleteCreditCard} integration test with Mandatory parameters.", expectedExceptions = { NotFoundException.class })
    public void testDeleteCreditCardWitMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deleteCreditCard");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_deleteCreditCard_mandatory.json");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        gateway.creditCard().find(connectorProperties.getProperty("token"));
    }

    /**
     * Negative test case for deleteCreditCard.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Braintree {deleteCreditCard} integration test with negative case.", expectedExceptions = NotFoundException.class)
    public void testDeleteCreditCardWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deleteCreditCard");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_deleteCreditCard_negative.json");
        Assert.assertEquals(esbRestResponse.getBody().getString("error_message"), resourceNotFoundExceptionMessage);
        gateway.creditCard().delete("INVALID");

    }

    /**
     * Positive test case for createTransaction with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Braintree {createTransaction} integration test with mandatory parameters.")
    public void testCreateTransactionWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createTransaction");
        parametersMap.put("amount", "1000");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createTransaction_mandatory.json", parametersMap);
        final JSONObject jObject = esbRestResponse.getBody().getJSONObject("target");

        final String transactionId = (String) jObject.get("id");
        connectorProperties.setProperty("transactionId", transactionId);

        final Transaction transaction = gateway.transaction().find(transactionId);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(transactionId, transaction.getId());
        Assert.assertEquals(jObject.get("avsPostalCodeResponseCode"), transaction.getAvsPostalCodeResponseCode());
        Assert.assertEquals(jObject.get("merchantAccountId"), transaction.getMerchantAccountId());
    }

    /**
     * Positive test case for createTransaction with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     * @throws InterruptedException
     * @throws NumberFormatException
     */
    @Test(dependsOnMethods = { "testCreateTransactionWithMandatoryParameters" }, description = "Braintree {createTransaction} integration test with optional parameters.")
    public void testCreateTransactionWithOptionalParameters() throws IOException, JSONException, NumberFormatException,
            InterruptedException {

        esbRequestHeadersMap.put("Action", "urn:createTransaction");
        Thread.sleep(Long.parseLong(connectorProperties.getProperty("timeDelay")));
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createTransaction_optional.json");
        final JSONObject jObject = esbRestResponse.getBody().getJSONObject("target");

        final String transactionIdOptional = (String) jObject.get("id");
        connectorProperties.setProperty("transactionIdOptional", transactionIdOptional);

        final Transaction transaction = gateway.transaction().find(transactionIdOptional);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(transactionIdOptional, transaction.getId());
        Assert.assertEquals(jObject.get("avsPostalCodeResponseCode"), transaction.getAvsPostalCodeResponseCode());
        Assert.assertEquals(jObject.get("merchantAccountId"), transaction.getMerchantAccountId());

    }

    /**
     * Negative test case for createTransaction.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Braintree {createTransaction} integration test for negative case.")
    public void testCreateTransactionWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createTransaction");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createTransaction_negative.json");
        final String errorMessage = esbRestResponse.getBody().getString("error_message");

        final TransactionRequest request = new TransactionRequest().amount(new BigDecimal(-1))
                .orderId(connectorProperties.getProperty("orderId"))
                .merchantAccountId(connectorProperties.getProperty("masterMerchantAccountId")).creditCard()
                .number(connectorProperties.getProperty("creditCardNumber"))
                .expirationDate(connectorProperties.getProperty("expirationDate")).done();

        final Result<Transaction> result = gateway.transaction().sale(request);

        Assert.assertEquals(errorMessage, result.getMessage());

    }

    /**
     * Positive test case for submitTransactionForSettlement with mandatory
     * parameters.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws JSONException
     *             The JSON exception.
     */
    @Test(dependsOnMethods = { "testCreateTransactionWithMandatoryParameters" }, description = "Braintree {submitTransactionForSettlement} integration test with mandatory parameters.")
    public void testSubmitTransactionForSettlementWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:submitTransactionForSettlement");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_submitTransaction_mandatory.json");

        final JSONObject jObject = esbRestResponse.getBody().getJSONObject("target");
        final Transaction transaction = gateway.transaction().find(connectorProperties.getProperty("transactionId"));

        Assert.assertEquals(jObject.getString("id"), connectorProperties.getProperty("transactionId"));
        Assert.assertEquals(jObject.getString("status"), transaction.getStatus().toString());
        Assert.assertEquals(jObject.getString("merchantAccountId"), transaction.getMerchantAccountId());
    }

    /**
     * Positive test case for submitTransactionForSettlement with optional
     * parameters.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws JSONException
     *             The JSON exception.
     */
    @Test(dependsOnMethods = { "testCreateTransactionWithOptionalParameters" }, description = "Braintree {submitTransactionForSettlement} integration test with optional parameters.")
    public void testSubmitTransactionForSettlementWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:submitTransactionForSettlement");
        parametersMap.put("partialAmount", "500");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_submitTransaction_optional.json", parametersMap);

        final JSONObject jObject = esbRestResponse.getBody().getJSONObject("target");
        final Transaction transaction = gateway.transaction().find(
                connectorProperties.getProperty("transactionIdOptional"));

        Assert.assertEquals(jObject.getString("id"), connectorProperties.getProperty("transactionIdOptional"));
        Assert.assertEquals(jObject.getString("status"), transaction.getStatus().toString());
        Assert.assertEquals(jObject.getString("amount"), transaction.getAmount().setScale(1).toString());
    }

    /**
     * Negative test case for submitTransactionForSettlement method.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws JSONException
     *             The JSON exception.
     */
    @Test(dependsOnMethods = { "testCreateTransactionWithMandatoryParameters",
            "testSubmitTransactionForSettlementWithMandatoryParameters" }, description = "Braintree {submitTransactionForSettlement} integration negative test case.")
    public void testSubmitTransactionForSettlementNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:submitTransactionForSettlement");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_submitTransaction_negative.json");

        final JSONObject jObject = esbRestResponse.getBody().getJSONObject("errors").getJSONObject("nestedErrors")
                .getJSONObject("transaction").getJSONArray("errors").getJSONObject(0);

        final Result<Transaction> result = gateway.transaction().submitForSettlement(
                connectorProperties.getProperty("transactionId"));

        Assert.assertEquals(jObject.getString("code"), result.getErrors().getAllDeepValidationErrors().get(0).getCode()
                .toString());
        Assert.assertEquals(jObject.getString("message"), result.getMessage());

    }

    /**
     * Positive test case for getSettlementBatchSummary with mandatory
     * parameters.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws JSONException
     *             The JSON exception.
     * @throws ParseException
     *             The parse exception.
     * @throws NumberFormatException
     */
    @Test(dependsOnMethods = { "testSubmitTransactionForSettlementWithMandatoryParameters",
            "testSubmitTransactionForSettlementWithOptionalParameters" }, description = "Braintree {getSettlementBatchSummary} integration test with mandatory parameters.")
    public void testGetSettlementBatchSummaryWithMandatoryParameters() throws IOException, JSONException,
            ParseException, NumberFormatException {

        esbRequestHeadersMap.put("Action", "urn:getSettlementBatchSummary");

        parametersMap.put("date", new SimpleDateFormat("M/dd/yyyy").format(new Date()).toString());
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getSettlementSummary_mandatory.json", parametersMap);

        final Result<SettlementBatchSummary> result = gateway.settlementBatchSummary().generate(Calendar.getInstance());

        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("target").getJSONArray("records").length(), result
                .getTarget().getRecords().size());

    }

    /**
     * Negative test case for getSettlementBatchSummary method.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws JSONException
     *             The JSON exception.
     * @throws ParseException
     *             The parse exception.
     */
    @Test(priority = 1, description = "Braintree {getSettlementBatchSummary} integration negative test case.")
    public void testGetSettlementBatchSummaryNegativeCase() throws IOException, JSONException, ParseException {

        esbRequestHeadersMap.put("Action", "urn:getSettlementBatchSummary");
        parametersMap.put("date", new SimpleDateFormat("M/dd/yyyy").format(new Date()).toString());
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getSettlementSummary_negative.json", parametersMap);

        final JSONObject jObject = esbRestResponse.getBody().getJSONObject("errors").getJSONObject("nestedErrors")
                .getJSONObject("settlement-batch-summary").getJSONArray("errors").getJSONObject(0);

        final Result<SettlementBatchSummary> result = gateway.settlementBatchSummary().generate(Calendar.getInstance(),
                "INVALID");

        Assert.assertEquals(jObject.getString("code"), result.getErrors().getAllDeepValidationErrors().get(0).getCode()
                .toString());
        Assert.assertEquals(jObject.getString("message"), result.getMessage());

    }

    /**
     * Positive test case for getTransaction with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateTransactionWithMandatoryParameters" }, description = "Braintree {getTransaction} integration test with Mandatory parameters.")
    public void testGetTransactionWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getTransaction");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getTransaction_mandatory.json");
        final Transaction transaction = gateway.transaction().find(connectorProperties.getProperty("transactionId"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().get("id"), transaction.getId());
        Assert.assertEquals(esbRestResponse.getBody().get("avsPostalCodeResponseCode"),
                transaction.getAvsPostalCodeResponseCode());
        Assert.assertEquals(esbRestResponse.getBody().get("merchantAccountId"), transaction.getMerchantAccountId());
    }

    /**
     * Negative test case for getTransaction.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Braintree {getTransaction} integration test for negative case.", expectedExceptions = NotFoundException.class)
    public void testGetTransactionWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getTransaction");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getTransaction_negative.json");
        Assert.assertEquals(esbRestResponse.getBody().getString("error_message"), resourceNotFoundExceptionMessage);
        gateway.transaction().find("INVALID");

    }

    /**
     * Positive test case for searchTransactions with mandatory parameters. This
     * method indirectly depends on the createTransaction methods for it to
     * return a non-empty array of searched values.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateTransactionWithMandatoryParameters",
            "testCreateTransactionWithOptionalParameters" }, description = "Braintree {searchTransactions} integration test with mandatory parameters.")
    public void testSearchTransactionsWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:searchTransactions");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_searchTransactions_mandatory.json");

        final TransactionSearchRequest request = new TransactionSearchRequest();
        final ResourceCollection<Transaction> collection = gateway.transaction().search(request);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("ids").length(), collection.getMaximumSize());
    }

    /**
     * Positive test case for searchTransactions with optional parameters. This
     * method has indirect dependency on the createTransaction methods for it to
     * return a non-empty array of searched values.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateTransactionWithMandatoryParameters",
            "testCreateTransactionWithOptionalParameters" }, description = "Braintree {searchTransactions} integration test with Optional parameters.")
    public void testSearchTransactionsWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:searchTransactions");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_searchTransactions_optional.json");

        final TransactionSearchRequest request = new TransactionSearchRequest().creditCardNumber().is(
                connectorProperties.getProperty("creditCardNumber"));
        final ResourceCollection<Transaction> collection = gateway.transaction().search(request);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONArray("ids").length(), collection.getMaximumSize());
    }

    /**
     * Positive test case for voidTransaction with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testGetSettlementBatchSummaryWithMandatoryParameters" }, description = "Braintree {voidTransaction} integration test with Mandatory parameters.")
    public void testVoidTransactionWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:voidTransaction");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_voidTransaction_mandatory.json");
        Assert.assertEquals(esbRestResponse.getBody().getString("status"), "VOIDED");
        final Transaction apiResult = gateway.transaction().find(connectorProperties.getProperty("transactionId"));
        Assert.assertEquals(apiResult.getStatus().toString(), "VOIDED");

    }

    /**
     * Negative test case for voidTransaction method.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Braintree {voidTransaction} integration negative test case.", expectedExceptions = NotFoundException.class)
    public void testVoidTransactionNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:voidTransaction");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_voidTransaction_negative.json");
        Assert.assertEquals(esbRestResponse.getBody().getString("error_message"), resourceNotFoundExceptionMessage);
        gateway.transaction().find("INVALID");
    }

    /**
     * Positive test case for refundTransaction with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testVoidTransactionWithMandatoryParameters" }, description = "Braintree {refundTransaction} integration test with Mandatory parameters.")
    public void testRefundTransactionWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:refundTransaction");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_refundTransaction_mandatory.json");

        // If refundTransaction fails.
        if (esbRestResponse.getBody().has("errors")) {
            final String esbErrorMessage = esbRestResponse.getBody().getJSONObject("errors")
                    .getJSONObject("nestedErrors").getJSONObject("transaction").getJSONArray("errors").getJSONObject(0)
                    .getString("message");
            final String expectedErrorMessage = connectorProperties.getProperty("expectedRefundExceptionMessage");
            if (esbErrorMessage.equals(expectedErrorMessage)) {
                Assert.fail("Braintree: Transaction needs to be settled before it can be refunded!\n"
                        + "Settlement of Transaction is a batch process which is executted by automated Scheduller.\n"
                        + "The following error is reported from the backend when trying to refund a transaction that has not yet been settled:\n"
                        + esbErrorMessage);
            } else {
                Assert.fail("Assetion Failed: " + esbErrorMessage);
            }
        }
        // If Refunding Transaction succeeds.
        else {
            final Transaction transaction = gateway.transaction()
                    .find(connectorProperties.getProperty("transactionId"));
            Assert.assertEquals(esbRestResponse.getBody().getJSONObject("target").getString("id"), transaction
                    .getRefundIds().get(0));
        }
    }

    /**
     * Positive test case for refundTransaction with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testVoidTransactionWithMandatoryParameters" }, description = "Braintree {refundTransaction} integration test with Optional parameters.")
    public void testRefundTransactionWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:refundTransaction");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_refundTransaction_optional.json");

        // If refundTransaction fails.
        if (esbRestResponse.getBody().has("errors")) {
            final String esbErrorMessage = esbRestResponse.getBody().getJSONObject("errors")
                    .getJSONObject("nestedErrors").getJSONObject("transaction").getJSONArray("errors").getJSONObject(0)
                    .getString("message");
            final String expectedErrorMessage = connectorProperties.getProperty("expectedRefundExceptionMessage");
            if (esbErrorMessage.equals(expectedErrorMessage)) {
                Assert.fail("Braintree: Transaction needs to be settled before it can be refunded!\n"
                        + "Settlement of Transaction is a batch process which is executted by automated Scheduller.\n"
                        + "The following error is reported from the backend when trying to refund a transaction that has not yet been settled:\n"
                        + esbErrorMessage);
            } else {
                Assert.fail("Assetion Failed: " + esbErrorMessage);
            }
        }
        // If Refunding Transaction succeeds.
        else {
            final Transaction transaction = gateway.transaction()
                    .find(connectorProperties.getProperty("transactionId"));
            Assert.assertEquals(esbRestResponse.getBody().getJSONObject("target").getString("id"), transaction
                    .getRefundIds().get(0));
        }
    }

    /**
     * Negative test case for refundTransaction.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Braintree {refundTransaction} integration test with negative case.", expectedExceptions = NotFoundException.class)
    public void testRefundTransactionWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:refundTransaction");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_refundTransaction_negative.json");
        Assert.assertEquals(esbRestResponse.getBody().getString("error_message"), resourceNotFoundExceptionMessage);
        gateway.transaction().refund("INVALID");
    }

    /**
     * Positive test case for createMerchantAccount with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Braintree {createMerchantAccount} integration test with mandatory parameters.")
    public void testCreateMerchantAccountWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createMerchantAccount");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createMerchantAccount_mandatory.json");
        final JSONObject jObject = esbRestResponse.getBody().getJSONObject("target");

        final String merchantAccountId = (String) jObject.getString("id");
        connectorProperties.setProperty("merchantAccountId", merchantAccountId);

        final MerchantAccount merchantAccount = gateway.merchantAccount().find(merchantAccountId);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(jObject.getJSONObject("masterMerchantAccount").get("status").toString(), merchantAccount
                .getStatus().toString());
        Assert.assertEquals(merchantAccountId, merchantAccount.getId());
        Assert.assertEquals(jObject.getJSONObject("masterMerchantAccount").get("id"), merchantAccount
                .getMasterMerchantAccount().getId());
    }

    /**
     * Positive test case for createMerchantAccount with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Braintree {createMerchantAccount} integration test with optional parameters.")
    public void testCreateMerchantAccountWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createMerchantAccount");
        parametersMap.put("optionalLegalName", "optionalLegalName");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createMerchantAccount_optional.json", parametersMap);
        final JSONObject jObject = esbRestResponse.getBody().getJSONObject("target");

        final String merchantAccountIdOptional = (String) jObject.get("id");
        connectorProperties.setProperty("merchantAccountIdOptional", merchantAccountIdOptional);

        final MerchantAccount merchantAccount = gateway.merchantAccount().find(merchantAccountIdOptional);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(merchantAccountIdOptional, merchantAccount.getId());
        Assert.assertEquals(parametersMap.get("optionalLegalName"), merchantAccount.getBusinessDetails().getLegalName());
        Assert.assertEquals(jObject.getJSONObject("masterMerchantAccount").get("status").toString(), merchantAccount
                .getStatus().toString());
        Assert.assertEquals(jObject.getJSONObject("masterMerchantAccount").get("id"), merchantAccount
                .getMasterMerchantAccount().getId());
    }

    /**
     * Negative test case for createMerchantAccount.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Braintree {createMerchantAccount} integration test for negative case.")
    public void testCreateMerchantAccountWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createMerchantAccount");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_createMerchantAccount_negative.json");
        final String esbErrorMessage = esbRestResponse.getBody().getJSONObject("errors").getJSONObject("nestedErrors")
                .getJSONObject("merchant-account").getJSONArray("errors").getJSONObject(0).getString("message");

        final MerchantAccountRequest request = new MerchantAccountRequest().individual().firstName("test")
                .lastName("sam").email("aaa@bbbb.com").phone("5553334444").dateOfBirth("1981-11-19").address()
                .streetAddress("111 Main St").locality("Chicago").region("IL").postalCode("60622").done().done()
                .funding().destination(MerchantAccount.FundingDestination.EMAIL).email("funding@blueladders.com")
                .done().tosAccepted(true).masterMerchantAccountId("INVALID");

        final Result<MerchantAccount> result = gateway.merchantAccount().create(request);

        Assert.assertEquals(esbErrorMessage, result.getMessage());

    }

    /**
     * Positive test case for getMerchantAccount with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateMerchantAccountWithMandatoryParameters" }, description = "Braintree {getMerchantAccount} integration test with Mandatory parameters.")
    public void testGetMerchantAccountWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getMerchantAccount");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getMerchantAccount_mandatory.json");

        final MerchantAccount merchantAccount = gateway.merchantAccount().find(
                connectorProperties.getProperty("merchantAccountId"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("masterMerchantAccount").get("status").toString(),
                merchantAccount.getStatus().toString());
        Assert.assertEquals(connectorProperties.getProperty("merchantAccountId"), merchantAccount.getId());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("masterMerchantAccount").get("id"), merchantAccount
                .getMasterMerchantAccount().getId());
    }

    /**
     * Negative test case for getMerchantAccount.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Braintree {getMerchantAccount} integration test for negative case.", expectedExceptions = NotFoundException.class)
    public void testGetMerchantAccountWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getMerchantAccount");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_getMerchantAccount_negative.json");
        Assert.assertEquals(esbRestResponse.getBody().getString("error_message"), resourceNotFoundExceptionMessage);
        gateway.merchantAccount().find("INVALID");
    }

    /**
     * Positive test case for updateMerchantAccount with mandatory parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testGetMerchantAccountWithMandatoryParameters" }, description = "Braintree {updateMerchantAccount} integration test with mandatory parameters.")
    public void testUpdateMerchantAccountWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updateMerchantAccount");
        parametersMap.put("individualName", "testName");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_updateMerchantAccount_mandatory.json", parametersMap);

        final MerchantAccount merchantAccount = gateway.merchantAccount().find(
                connectorProperties.getProperty("merchantAccountId"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(parametersMap.get("individualName"), merchantAccount.getIndividualDetails().getFirstName());
        Assert.assertEquals(connectorProperties.getProperty("merchantAccountId"), merchantAccount.getId());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("target").getJSONObject("masterMerchantAccount")
                .get("id"), merchantAccount.getMasterMerchantAccount().getId());
    }

    /**
     * Positive test case for updateMerchantAccount with optional parameters.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(dependsOnMethods = { "testCreateMerchantAccountWithOptionalParameters" }, description = "Braintree {updateMerchantAccount} integration test with optional parameters.")
    public void testUpdateMerchantAccountWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updateMerchantAccount");
        parametersMap.put("businessLegalName", "optionalLegalName");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_updateMerchantAccount_optional.json", parametersMap);

        final MerchantAccount merchantAccount = gateway.merchantAccount().find(
                connectorProperties.getProperty("merchantAccountIdOptional"));

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(parametersMap.get("businessLegalName"), merchantAccount.getBusinessDetails().getLegalName());
        Assert.assertEquals(connectorProperties.getProperty("merchantAccountIdOptional"), merchantAccount.getId());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("target").getJSONObject("masterMerchantAccount")
                .get("id"), merchantAccount.getMasterMerchantAccount().getId());
    }

    /**
     * Negative test case for updateMerchantAccount.
     * 
     * @throws JSONException
     * @throws IOException
     */
    @Test(priority = 1, description = "Braintree {updateMerchantAccount} integration test for negative case.", expectedExceptions = NotFoundException.class)
    public void testUpdateMerchantAccountWithNegativeCase() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updateMerchantAccount");
        parametersMap.put("individualName", "testName");
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                "esb_updateMerchantAccount_negative.json", parametersMap);
        Assert.assertEquals(esbRestResponse.getBody().getString("error_message"), resourceNotFoundExceptionMessage);
        gateway.merchantAccount().find("INVALID");
    }

}
