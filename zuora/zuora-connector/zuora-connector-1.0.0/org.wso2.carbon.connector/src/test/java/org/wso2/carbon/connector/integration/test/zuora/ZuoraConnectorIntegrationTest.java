/**
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * <p/>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.connector.integration.test.zuora;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.apache.commons.codec.binary.Base64;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ZuoraConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init("zuora-connector-1.0.0");

        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");

        apiRequestHeadersMap.putAll(esbRequestHeadersMap);

        String authHeader = connectorProperties.getProperty("apiAccessKeyId") + ":" + connectorProperties.
                getProperty("apiSecretAccessKey");
        String encodedAuthorization = new String(Base64.encodeBase64(authHeader.getBytes()));

        apiRequestHeadersMap.put("Authorization", "Basic " + encodedAuthorization);
        apiRequestHeadersMap.put("Content-Type", "application/json");
        apiRequestHeadersMap.put("Accept", "application/json");
    }

    /**
     * Positive test case for getInvoices method with mandatory parameters..
     */
    @Test(enabled = true, description = "zuora {getInvoices} integration test with mandatory" +
            " parameters.")
    public void testGetInvoicesWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getInvoices");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "getInvoices_mandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties
                .getProperty("apiVersion") + "/transactions/invoices/accounts/" + connectorProperties.
                getProperty("accountKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getInvoices method with mandatory parameters..
     */
    @Test(enabled = true, description = "zuora {getInvoices} integration test with mandatory" +
            " parameters.")
    public void testGetInvoicesWithNegativeParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getInvoices");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "getInvoices_negative.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties
                .getProperty("apiVersion") + "/transactions/invoices/accounts/";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getTransferredPayments method with mandatory parameters..
     */
    @Test(enabled = true, description = "zuora {getTransferredPayments} integration test with mandatory" +
            " parameters.")
    public void testGetTransferredPaymentsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getTransferredPayments");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "getTransferredPayments_mandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties
                .getProperty("apiVersion") + "/transactions/payments/accounts/" + connectorProperties.
                getProperty("accountKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getTransferredPayments method with mandatory parameters..
     */
    @Test(enabled = true, description = "zuora {getTransferredPayments} integration test with mandatory" +
            " parameters.")
    public void testGetTransferredPaymentsWithNegativeParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getTransferredPayments");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "getTransferredPayments_negative.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties
                .getProperty("apiVersion") + "/transactions/payments/accounts/";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 404);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for createAndCollectInvoice method with mandatory parameters..
     */
    @Test(priority = 1, enabled = true, description = "zuora {createAndCollectInvoice} integration test with mandatory" +
            " parameters.")
    public void testCreateAndCollectInvoiceWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createAndCollectInvoice");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createAndCollectInvoice_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("success"), "false");
    }

    /**
     * Positive test case for createSubscription method mandatory parameters.
     */
    @Test(priority = 1, enabled = true, description = "zuora {createSubscription} integration test with mandatory" +
            " parameters.")
    public void testCreateSubscriptionWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createSubscription");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createSubscription_mandatory.json");

        String subscriptionId = esbRestResponse.getBody().getString("subscriptionId");
        connectorProperties.setProperty("subscriptionIdMandatory", subscriptionId);
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties
                .getProperty("apiVersion") + "/subscriptions/" + connectorProperties.getProperty("subscriptionIdMandatory");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().getString("id"), connectorProperties.getProperty("subscriptionIdMandatory"));
    }

    /**
     * Positive test case for createSubscription method optional parameters.
     */
    @Test(priority = 1, enabled = true, dependsOnMethods = {"testCreateSubscriptionWithMandatoryParameters"},
            description = "zuora {createSubscription} integration test with optional" + " parameters.")
    public void testCreateSubscriptionWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createSubscription");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "createSubscription_optional.json");

        String subscriptionId = esbRestResponse.getBody().getString("subscriptionId");

        connectorProperties.setProperty("subscriptionIdOptional", subscriptionId);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties
                .getProperty("apiVersion") + "/subscriptions/" + connectorProperties.getProperty("subscriptionIdOptional");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().getString("id"), connectorProperties.getProperty("subscriptionIdOptional"));
    }

    /**
     *
     * Positive test case for getSubscriptions method with mandatory parameters..
     */
    @Test(enabled = true, priority = 1, dependsOnMethods = {"testCreateSubscriptionWithOptionalParameters"},
            description = "zuora {getSubscriptions} integration test with mandatory" + " parameters.")
    public void testGetSubscriptionsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getSubscriptions");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "getSubscriptions_mandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties
                .getProperty("apiVersion") + "/subscriptions/accounts/" + connectorProperties.getProperty("accountKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getSubscriptions method with optional parameters.
     */
    @Test(enabled = true, priority = 1, dependsOnMethods = {"testGetSubscriptionsWithMandatoryParameters"},
            description = "zuora {getSubscriptions} integration test with optional" + " parameters.")
    public void testGetSubscriptionsWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getSubscriptions");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "getSubscriptions_optional.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties
                .getProperty("apiVersion") + "/subscriptions/accounts/" + connectorProperties.getProperty("accountKey") +
                "?charge-detail=" + connectorProperties.getProperty("chargeDetail") + "&pageSize=" +
                connectorProperties.getProperty("pageSize");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getSubscriptionsByKey method with mandatory parameters.
     */
    @Test(enabled = true, priority = 2, dependsOnMethods = {"testGetSubscriptionsWithOptionalParameters"},
            description = "zuora {getSubscriptionsByKey} integration test with mandatory" + " parameters.")
    public void testGetSubscriptionsByKeyWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getSubscriptionsByKey");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "getSubscriptionsByKey_mandatory.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties
                .getProperty("apiVersion") + "/subscriptions/" + connectorProperties.getProperty("subscriptionKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getSubscriptionsByKey method with optional parameters.
     */
    @Test(priority = 2, enabled = true, dependsOnMethods = {"testGetSubscriptionsByKeyWithMandatoryParameters"},
            description = "zuora {getSubscriptionsByKey} integration test with optional" + " parameters.")
    public void testGetSubscriptionsByKeyWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getSubscriptionsByKey");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "getSubscriptionsByKey_optional.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties
                .getProperty("apiVersion") + "/subscriptions/" + connectorProperties.getProperty("subscriptionKey") +
                "?charge-detail=" + connectorProperties.getProperty("chargeDetail");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }


    /**
     * Positive test case for updateSubscription method mandatory parameters.
     */
    @Test(priority = 2, enabled = true, dependsOnMethods = {"testGetSubscriptionsByKeyWithOptionalParameters"},
            description = "zuora {updateSubscription} integration test with mandatory" + " parameters.")
    public void testUpdateSubscriptionWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateSubscription");
        System.out.println("\n\n\n\n\n\n\n" + connectorProperties.getProperty("subscriptionIdMandatory"));
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "updateSubscription_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("success"), "true");
    }

    /**
     * Positive test case for previewSubscription method mandatory parameters.
     */
    @Test(priority = 2, enabled = true, dependsOnMethods = {"testUpdateSubscriptionWithMandatoryParameters"},
            description = "zuora {previewSubscription} integration test with mandatory" + " parameters.")
    public void testPreviewSubscriptionWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:previewSubscription");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "previewSubscription_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("success"), "true");
    }

    /**
     * Positive test case for previewSubscription method optional parameters.
     */
    @Test(priority = 2, enabled = true, dependsOnMethods = {"testPreviewSubscriptionWithMandatoryParameters"},
            description = "zuora {previewSubscription} integration test with optional" + " parameters.")
    public void testPreviewSubscriptionWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:previewSubscription");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "previewSubscription_optional.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("success"), "true");
    }

    /**
     * Positive test case for renewSubscriptions method mandatory parameters.
     */
    @Test(priority = 2, enabled = true, dependsOnMethods = {"testPreviewSubscriptionWithOptionalParameters"},
            description = "zuora {renewSubscriptions} integration test with mandatory" + " parameters.")
    public void testRenewSubscriptionsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:renewSubscriptions");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "renewSubscriptions_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("success"), "true");
    }

    /**
     * Positive test case for renewSubscriptions method optional parameters.
     */
    @Test(priority = 2, enabled = false, dependsOnMethods = {"testRenewSubscriptionsWithMandatoryParameters"},
            description = "zuora {renewSubscriptions} integration test with optional" + " parameters.")
    public void testRenewSubscriptionsWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:renewSubscriptions");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "renewSubscriptions_optional.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("success"), "true");
    }

    /**
     * Positive test case for cancelSubscriptions method mandatory parameters.
     */
    @Test(priority = 3, enabled = true, dependsOnMethods = {"testRenewSubscriptionsWithMandatoryParameters"},
            description = "zuora {cancelSubscriptions} integration test with mandatory" + " parameters.")
    public void testCancelSubscriptionsWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:cancelSubscriptions");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "cancelSubscriptions_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("success"), "true");
    }

 /**
     * Positive test case for checkConnections method with mandatory parameters..
     */
    @Test(enabled = true, description = "zuora {checkConnections} integration test with mandatory" +
            " parameters.")
    public void testCheckConnection() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:checkConnections");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "checkConnections.json");
        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties
                .getProperty("apiVersion") + "/connections";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST",
                apiRequestHeadersMap);
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().toString(), apiRestResponse.getBody().toString());
    }

    /**
     * Positive test case for createAccount method mandatory parameters.
     */
    @Test(priority = 1, description = "zuora {createAccounts} integration test with mandatory" +
            " parameters.")
    public void testCreateAccountWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createAccounts");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "createAccounts_mandatory.json");

        final String accountId = esbRestResponse.getBody().getString("accountId");
        final String accountKey = esbRestResponse.getBody().getString("accountNumber");

        connectorProperties.setProperty("accountId", accountId);
        connectorProperties.setProperty("accountKey", accountKey);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" +
                connectorProperties.getProperty("apiVersion") + "/accounts" + "/" + accountId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("basicInfo").
                getString("accountNumber"), accountKey);

    }

    /**
     * Positive test case for createAccount method optional parameters.
     */
    @Test(priority = 1, description = "zuora {createAccounts} integration test with optional" +
            " parameters.")
    public void testCreateAccountWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createAccounts");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "createAccounts_optional.json");

        String accountId = esbRestResponse.getBody().getString("accountId");
        String accountNumber = esbRestResponse.getBody().getString("accountNumber");

        connectorProperties.setProperty("accountId", accountId);
        connectorProperties.setProperty("accountNumber", accountNumber);

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" +
                connectorProperties.getProperty("apiVersion") + "/accounts" + "/" + accountId;
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("basicInfo").
                getString("accountNumber"), accountNumber);
    }

    /**
     * Negative test case for createAccount method.
     */
    @Test(enabled = true, description = "zuora {createAccounts} integration test with negative " +
            "case ")
    public void testCreateAccountNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createAccounts");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "createAccounts_negative.json");

        Assert.assertEquals(esbRestResponse.getBody().getString("success"), "false");
    }

    /**
     * Positive test case for getAccount method mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testCreateAccountWithMandatoryParameters"},
            description = "zuora {getAccounts} integration test with mandatory parameters.")
    public void testGetAccountWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getAccounts");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "getAccounts_mandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" +
                connectorProperties.getProperty("apiVersion") + "/accounts" + "/"
                + connectorProperties.get("accountKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().toString(), esbRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAccount method.
     */
    @Test(enabled = true, description = "zuora {getAccounts} integration test with negative case ")
    public void testGetAccountNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getAccounts");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "getAccounts_negative.json");

        Assert.assertEquals(esbRestResponse.getBody().getString("success"), "false");
    }

    /**
     * Positive test case for getAccountsSummary method mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testCreateAccountWithMandatoryParameters"},
            description = "zuora {getAccountsSummary} integration test with mandatory parameters.")
    public void testGetAccountSummaryWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getAccountsSummary");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "getAccountsSummary_mandatory.json");
        String accountNumber = esbRestResponse.getBody().getJSONObject("basicInfo").
                getString("accountNumber");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" +
                connectorProperties.getProperty("apiVersion") + "/accounts" + "/" +
                connectorProperties.get("accountKey") + "/summary";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().toString(), esbRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getAccountsSummary method.
     */
    @Test(enabled = true, description = "zuora {getAccountsSummary} integration test with negative case ")
    public void testCreateAccountSummaryNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getAccountsSummary");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "getAccountsSummary_negative.json");

        Assert.assertEquals(esbRestResponse.getBody().getString("success"), "false");
    }

    /**
     * Positive test case for updateAccount method mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testCreateAccountWithMandatoryParameters"},
            description = "zuora {updateAccounts} integration test with mandatory parameters.")
    public void testUpdateAccountWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateAccounts");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "updateAccounts_mandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties
                .getProperty("apiVersion") + "/accounts" + "/" +
                connectorProperties.getProperty("accountKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "PUT",
                apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().toString(), esbRestResponse.getBody().toString());
    }

    /**
     * Positive test case for updateAccount method optional parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testCreateAccountWithMandatoryParameters"},
            description = "zuora {updateAccounts} integration test with optional " +
                    "parameters.")
    public void testUpdateAccountWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateAccounts");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "updateAccounts_optional.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" +
                connectorProperties.getProperty("apiVersion") + "/accounts" + "/" +
                connectorProperties.getProperty("accountKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().getJSONObject("basicInfo").getString("name"),
                connectorProperties.getProperty("updateName"));
    }

    /**
     * Negative test case for updateAccount method.
     */
    @Test(enabled = true, description = "zuora {updateAccounts} integration test with negative case ")
    public void testUpdateAccountNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updateAccounts");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "getAccountsSummary_negative.json");

        Assert.assertEquals(esbRestResponse.getBody().getString("success"), "false");
    }

    /**
     * Positive test case for getPayments method mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testCreateAccountWithMandatoryParameters"},
            description = "zuora {getPayments} integration test with mandatory parameters.")
    public void testGetPaymentWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getPayments");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getPayments_mandatory" +
                        ".json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties
                .getProperty("apiVersion") + "/payment-methods/credit-cards/accounts" + "/"
                + connectorProperties.getProperty("accountKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().toString(), esbRestResponse.getBody().toString());
    }

    /**
     * Positive test case for getPayments method optional parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testCreateAccountWithMandatoryParameters"},
            description = "zuora {getPayments} integration test with optional " +
                    "parameters.")
    public void testGetPaymentWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getPayments");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "getPayments_optional" +
                        ".json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" +
                connectorProperties.getProperty("apiVersion") + "/payment-methods/credit-cards/accounts" + "/" +
                connectorProperties.getProperty("accountKey") + "?pageSize=" +
                connectorProperties.getProperty("pageSize");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().toString(), esbRestResponse.getBody().toString());
    }

    /**
     * Negative test case for getPayments method.
     */
    @Test(enabled = true, description = "zuora {getPayments} integration test with negative case ")
    public void testGetPaymentNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:getPayments");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "getPayments_negative.json");

        Assert.assertEquals(esbRestResponse.getBody().getString("success"), "false");
    }

    /**
     * Positive test case for createPayments method mandatory parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testCreateAccountWithMandatoryParameters"},
            description = "zuora {createPayments} integration test with mandatory parameters.")
    public void testCreatePaymentWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createPayments");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "createPayments_mandatory.json");
        final String paymentMethodId = esbRestResponse.getBody().getString("paymentMethodId");

        connectorProperties.setProperty("paymentMethodId", paymentMethodId);


        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties
                .getProperty("apiVersion") + "/payment-methods/credit-cards/accounts" + "/"
                + connectorProperties.getProperty("accountKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("creditCards").getJSONObject
                (0).getString("id"), esbRestResponse.getBody().getString("paymentMethodId"));
    }

    /**
     * Positive test case for createPayments method optional parameters.
     */
    @Test(priority = 2, dependsOnMethods = {"testCreateAccountWithMandatoryParameters"},
            description = "zuora {createPayments} integration test with optional " +
                    "parameters.")
    public void testCreatePaymentWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createPayments");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "createPayments_optional.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties
                .getProperty("apiVersion") + "/payment-methods/credit-cards/accounts" + "/"
                + connectorProperties.getProperty("accountKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("creditCards").getJSONObject
                (0).getString("id"), esbRestResponse.getBody().getString("paymentMethodId"));
    }

    /**
     * Negative test case for createPayments method.
     */
    @Test(enabled = true, description = "zuora {createPayments} integration test with negative case ")
    public void testCreatePaymentNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:createPayments");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "createPayments_negative.json");

        Assert.assertEquals(esbRestResponse.getBody().getString("success"), "false");
    }

    /**
     * Positive test case for updatePayments method mandatory parameters.
     */
    @Test(priority = 3, dependsOnMethods = {"testCreatePaymentWithMandatoryParameters"},
            description = "zuora {updatePayments} integration test with mandatory parameters.")
    public void testUpdatePaymentWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updatePayments");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "updatePayments_mandatory.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties
                .getProperty("apiVersion") + "/payment-methods/credit-cards/accounts" + "/"
                + connectorProperties.getProperty("accountKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("creditCards").getJSONObject
                (0).getString("id"), esbRestResponse.getBody().getString("paymentMethodId"));
    }

    /**
     * Positive test case for updatePayments method optional parameters.
     */
    @Test(priority = 3, dependsOnMethods = {"testCreatePaymentWithOptionalParameters"},
            description = "zuora {updatePayments} integration test with optional " +
                    "parameters.")
    public void testUpdatePaymentWithOptionalParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updatePayments");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "updatePayments_optional.json");

        String apiEndPoint = connectorProperties.getProperty("apiUrl") + "/" + connectorProperties
                .getProperty("apiVersion") + "/payment-methods/credit-cards/accounts" + "/"
                + connectorProperties.getProperty("accountKey");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET",
                apiRequestHeadersMap);

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(apiRestResponse.getBody().getJSONArray("creditCards").getJSONObject
                        (0).getJSONObject("cardHolderInfo").getString("cardHolderName"),
                connectorProperties.getProperty("cardHolderName"));
    }

    /**
     * Negative test case for updatePayments method.
     */
    @Test(enabled = true, description = "zuora {updatePayments} integration test with negative case ")
    public void testUpdatePaymentNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:updatePayments");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "updatePayments_negative.json");

        Assert.assertEquals(esbRestResponse.getBody().getString("success"), "false");
    }

    /**
     * Positive test case for deletePayments method mandatory parameters.
     */
    @Test(priority = 4, dependsOnMethods = {"testCreatePaymentWithMandatoryParameters",
            "testUpdatePaymentWithOptionalParameters","testUpdatePaymentWithMandatoryParameters"},
            description = "zuora {deletePayments} integration test with mandatory parameters.")
    public void testDeletePaymentWithMandatoryParameters() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:deletePayments");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "deletePayments_mandatory.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbRestResponse.getBody().getString("success"), "true");
    }

    /**
     * Negative test case for deletePayments method.
     */
    @Test(enabled = true, description = "zuora {deletePayments} integration test with negative case ")
    public void testDeletePaymentNegativeCase() throws IOException, JSONException {
        esbRequestHeadersMap.put("Action", "urn:deletePayments");

        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
                        "deletePayments_negative.json");

        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 403);
    }
}
