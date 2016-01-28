/**
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
                .getProperty("apiVersion") + "/subscriptions/" + connectorProperties.getProperty("subscriptionKey") + "?charge-detail=" +
                connectorProperties.getProperty("chargeDetail");
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
}
