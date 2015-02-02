/**
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

package org.wso2.carbon.connector.braintree;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;

import org.apache.synapse.MessageContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.carbon.connector.braintree.Constants.ErrorConstants;
import org.wso2.carbon.connector.braintree.Constants.JSONKeys;

import com.braintreegateway.AddModificationRequest;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ModificationsRequest;
import com.braintreegateway.Result;
import com.braintreegateway.Subscription;
import com.braintreegateway.SubscriptionOptionsRequest;
import com.braintreegateway.SubscriptionRequest;
import com.braintreegateway.UpdateModificationRequest;
import com.braintreegateway.exceptions.AuthenticationException;
import com.braintreegateway.exceptions.AuthorizationException;
import com.braintreegateway.exceptions.BraintreeException;
import com.braintreegateway.exceptions.NotFoundException;
import com.google.gson.Gson;

/**
 * BraintreeGetTransaction - Update Subscription.
 * 
 * @see https://www.braintreepayments.com/docs/java/subscriptions/update
 */
public final class UpdateSubscription extends AbstractBrainTreeConnector {

    /**
     * Instance variable to hold the MessageContext object passed in via the
     * Synapse template.
     */
    private MessageContext messageContext;

    /**
     * Connector method which is executed at the specified point within the
     * corresponding Synapse template within the connector.
     * 
     * @param msgContext
     *            Synapse Message Context.
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public void connect(final MessageContext msgContext) {

        messageContext = msgContext;

        String errorMessage = null;
        try {

            // instantiating and authenticating a braintreeGateway
            final BraintreeGateway braintreeGateway = getBrainTreeService(messageContext);

            // remove the request from the payload
            messageContext.getEnvelope().getBody().getFirstElement().detach();

            // creating a transaction & convert to JSON format and set to
            // messageContext
            final String subscriptionId = (String) messageContext.getProperty(Constants.SUBSCRIPTION_ID).toString();

            messageContext.setProperty(Constants.RESULT, new Gson().toJson(updateSubscription(braintreeGateway,
                    createSubscriptionRequest(), subscriptionId)));

        } catch (NotFoundException nfe) {
            errorMessage = ErrorConstants.INVALID_RESOURCE_MSG;
            log.error(errorMessage, nfe);
            storeErrorResponseStatus(messageContext, errorMessage, ErrorConstants.ERROR_CODE_NOT_FOUND_EXCEPTION);
            handleException(errorMessage, nfe, messageContext);
        } catch (ParseException re) {
            errorMessage = ErrorConstants.PARSER_EXCEPTION_MSG;
            log.error(errorMessage, re);
            storeErrorResponseStatus(messageContext, errorMessage, ErrorConstants.ERROR_CODE_PARSER_EXCEPTION);
            handleException(errorMessage, re, messageContext);
        } catch (AuthorizationException nfe) {
            errorMessage = ErrorConstants.INVALID_AUTHERIZATION_MSG;
            log.error(errorMessage, nfe);
            storeErrorResponseStatus(messageContext, errorMessage, ErrorConstants.ERROR_CODE_AUTHERIZATION_EXCEPTION);
            handleException(errorMessage, nfe, messageContext);
        } catch (AuthenticationException au) {
            errorMessage = ErrorConstants.INVALID_AUTHENTICATION_MSG;
            log.error(errorMessage, au);
            storeErrorResponseStatus(messageContext, errorMessage, ErrorConstants.ERROR_CODE_AUTHENTICATION_EXCEPTION);
            handleException(errorMessage, au, messageContext);
        } catch (BraintreeException be) {
            errorMessage = be.getMessage();
            log.error(errorMessage, be);
            storeErrorResponseStatus(messageContext, errorMessage, ErrorConstants.ERROR_CODE_BRAINTREE_EXCEPTION);
            handleException(errorMessage, be, messageContext);
        } catch (JSONException nfe) {
            errorMessage = ErrorConstants.INVALID_JSON_MSG;
            log.error(errorMessage, nfe);
            storeErrorResponseStatus(messageContext, errorMessage, ErrorConstants.ERROR_CODE_JSON_EXCEPTION);
            handleException(errorMessage, nfe, messageContext);
        } catch (RuntimeException re) {
            errorMessage = ErrorConstants.GENERIC_ERROR_MSG;
            log.error(errorMessage, re);
            storeErrorResponseStatus(messageContext, errorMessage, ErrorConstants.ERROR_CODE_RUNTIME_EXCEPTION);
            handleException(errorMessage, re, messageContext);
        }
    }

    /**
     * This method gets the updateRequest and updates the Subscription
     * identified by the provided subscriptionId.
     * 
     * @param braintreeGateway
     *            Authentication Gateway Token
     * @param request
     *            update request for subscription
     * @param existingSubscriptionId
     *            ID of the subscription to update
     * @return Result is returned
     */
    private Result<Subscription> updateSubscription(final BraintreeGateway braintreeGateway,
            final SubscriptionRequest request, final String existingSubscriptionId) {
        return braintreeGateway.subscription().update(existingSubscriptionId, request);
    }

    /**
     * This methods creates the update request for subscriptions.
     * 
     * @return SubscriptionRequest
     * @throws JSONException
     *             the json exception
     * @throws ParseException
     *             the parse exception
     */
    private SubscriptionRequest createSubscriptionRequest() throws JSONException, ParseException {

        return setPaymentDetails(setDateDetails(setBillingDetails(setAddons(setDiscounts(new SubscriptionRequest())))));
    }

    /**
     * Sets the payment details.
     * 
     * @param request
     *            the request
     * @return SubscriptionRequest
     * @throws JSONException
     *             the json exception
     * @throws ParseException
     *             the parse exception
     */
    private SubscriptionRequest setPaymentDetails(final SubscriptionRequest request) throws JSONException,
            ParseException {

        // Check and add newSubscriptionId.
        String newSubscriptionId = (String) messageContext.getProperty(Constants.NEW_SUBSCRIPTION_ID);
        if (newSubscriptionId != null && !newSubscriptionId.isEmpty()) {
            request.id(newSubscriptionId);
        }

        // Check and add paymentMethodToken | Should be a valid token.
        String paymentMethodToken = (String) messageContext.getProperty(Constants.PAYMENT_METHOD_TOKEN);
        if (paymentMethodToken != null && !paymentMethodToken.isEmpty()) {
            request.paymentMethodToken(paymentMethodToken);
        }

        // Check and add paymentMethodNonce | Should be a valid token.
        String paymentMethodNonce = (String) messageContext.getProperty(Constants.PAYMENT_METHOD_NONCE);
        if (paymentMethodNonce != null && !paymentMethodNonce.isEmpty()) {
            request.paymentMethodNonce(paymentMethodNonce);
        }

        // Check and add planId.
        String planId = (String) messageContext.getProperty(Constants.PLAN_ID);
        if (planId != null && !planId.isEmpty()) {
            request.planId(planId);
        }

        // Check and add price.
        String price = (String) messageContext.getProperty(Constants.PRICE);
        if (price != null && !price.isEmpty()) {
            request.price(new BigDecimal(price));
        }
        return request;
    }

    /**
     * Sets the date details.
     * 
     * @param request
     *            the request
     * @return SubscriptionRequest
     * @throws JSONException
     *             the json exception
     * @throws ParseException
     *             the parse exception
     */
    private SubscriptionRequest setDateDetails(final SubscriptionRequest request) throws JSONException, ParseException {

        // Check and add merchantAccountId.
        String merchantAccountId = (String) messageContext.getProperty(Constants.MERCHANT_ACCOUNT_ID);
        if (merchantAccountId != null && !merchantAccountId.isEmpty()) {
            request.merchantAccountId(merchantAccountId);
        }

        // Check and add billingDayOfMonth.
        String billingDayOfMonth = (String) messageContext.getProperty(Constants.BILLING_DAY_OF_MONTH);
        if (billingDayOfMonth != null && !billingDayOfMonth.isEmpty()) {
            request.billingDayOfMonth(Integer.valueOf(billingDayOfMonth));
        }

        // Check and add firstBillingDate.
        String firstBillingDate = (String) messageContext.getProperty(Constants.FIRST_BILLING_DATE);
        if (firstBillingDate != null && !firstBillingDate.isEmpty()) {
            request.firstBillingDate(getCalender(firstBillingDate));
        }

        // Check and add trialDuration.
        String trialDuration = (String) messageContext.getProperty(Constants.TRIAL_DURATION);
        if (trialDuration != null && !trialDuration.isEmpty()) {
            request.trialDuration(Integer.valueOf(trialDuration));
        }

        // Check and add trialDurationUnit.
        String trialDurationUnit = (String) messageContext.getProperty(Constants.TRIAL_DURATION_UNIT);
        if (trialDurationUnit != null && !trialDurationUnit.isEmpty()) {
            request.trialDurationUnit(Subscription.DurationUnit.valueOf(trialDurationUnit.toUpperCase(Locale.US)));
        }

        // Check and add hasTrialPeriod.
        String hasTrialPeriod = (String) messageContext.getProperty(Constants.HAS_TRIAL_PERIOD);
        if (hasTrialPeriod != null && !hasTrialPeriod.isEmpty()) {
            request.trialPeriod(Boolean.valueOf(hasTrialPeriod));
        }

        // Check and add neverExpires.
        String neverExpires = (String) messageContext.getProperty(Constants.NEVER_EXPIRES);
        if (neverExpires != null && !neverExpires.isEmpty()) {
            request.neverExpires(Boolean.valueOf(neverExpires));
        }
        return request;
    }

    /**
     * Sets the billing details.
     * 
     * @param request
     *            the request
     * @return SubscriptionRequest
     * @throws JSONException
     *             the json exception
     * @throws ParseException
     *             the parse exception
     */
    private SubscriptionRequest setBillingDetails(final SubscriptionRequest request) throws JSONException,
            ParseException {

        // Check and add numberOfBillingCycles.
        String numberOfBillingCycles = (String) messageContext.getProperty(Constants.NUMBER_OF_BILLING_CYCLES);
        if (numberOfBillingCycles != null && !numberOfBillingCycles.isEmpty()) {
            request.numberOfBillingCycles(Integer.valueOf(numberOfBillingCycles));
        }

        // Check and add options.
        String options = (String) messageContext.getProperty(Constants.OPTIONS);
        if (options != null && !options.isEmpty()) {
            JSONObject optionsObject = new JSONObject(options);

            SubscriptionOptionsRequest optionsRequest = request.options();
            // Check and add prorateCharges.
            if (optionsObject.has(JSONKeys.PRORATE_CHARGES)) {
                optionsRequest.prorateCharges(optionsObject.getBoolean(JSONKeys.PRORATE_CHARGES));
            }

            // Check and add revertSubscriptionOnProrationFailure.
            if (optionsObject.has(JSONKeys.REVERT_SUBSCRIPTION_ON_PRORATION_FAILURE)) {
                optionsRequest.prorateCharges(optionsObject
                        .getBoolean(JSONKeys.REVERT_SUBSCRIPTION_ON_PRORATION_FAILURE));
            }

            // Check and add replaceAllAddOnsAndDiscounts.
            if (optionsObject.has(JSONKeys.REPLACE_ALL_ADDONS_AND_DISCOUNTS)) {
                optionsRequest.prorateCharges(optionsObject.getBoolean(JSONKeys.REPLACE_ALL_ADDONS_AND_DISCOUNTS));
            }
            optionsRequest.done();
        }
        return request;
    }

    /**
     * Sets the addons.
     * 
     * @param request
     *            the request
     * @return SubscriptionRequest
     * @throws JSONException
     *             the json exception
     * @throws ParseException
     *             the parse exception
     */
    private SubscriptionRequest setAddons(final SubscriptionRequest request) throws JSONException, ParseException {

        final ModificationsRequest addOnRequest = request.addOns();

        // Check and add addOns.
        String addOns = (String) messageContext.getProperty(Constants.ADDONS);
        if (addOns != null && !addOns.isEmpty()) {
            JSONObject jObject = new JSONObject(addOns);

            // Add AddOns.
            if (jObject.has(JSONKeys.ADD)) {
                JSONArray addAddOnsArray = jObject.getJSONArray(JSONKeys.ADD);
                int length = addAddOnsArray.length();
                if (length != 0) {
                    for (int i = 0; i < length; i++) {
                        JSONObject addOnsObject = addAddOnsArray.getJSONObject(i);
                        setValues(addOnRequest.add(), addOnsObject);
                    }
                }
            }

            // Update Addons.
            if (jObject.has(JSONKeys.UPDATE)) {
                JSONArray updateAddOnsArray = jObject.getJSONArray(JSONKeys.UPDATE);
                for (int i = 0; i < updateAddOnsArray.length(); i++) {
                    JSONObject addOnsObject = updateAddOnsArray.getJSONObject(i);
                    if (addOnsObject.has(JSONKeys.ADDONS_ID)) {
                        setValues(addOnRequest.update(addOnsObject.getString(JSONKeys.ADDONS_ID)), addOnsObject);
                    }
                }
            }

            // Remove Addons.
            if (jObject.has(JSONKeys.REMOVE)) {
                JSONArray removeAddOnsArray = jObject.getJSONArray(JSONKeys.REMOVE);
                for (int i = 0; i < removeAddOnsArray.length(); i++) {
                    JSONObject addOnsObject = removeAddOnsArray.getJSONObject(i);
                    if (addOnsObject.has(JSONKeys.ADDONS_ID)) {
                        addOnRequest.remove(addOnsObject.getString(JSONKeys.ADDONS_ID));
                    }
                }
            }
            return addOnRequest.done();
        }
        return request;
    }

    /**
     * Sets the discounts.
     * 
     * @param request
     *            the request
     * @return SubscriptionRequest
     * @throws JSONException
     *             the json exception
     * @throws ParseException
     *             the parse exception
     */
    private SubscriptionRequest setDiscounts(final SubscriptionRequest request) throws JSONException, ParseException {

        final ModificationsRequest discountRequest = request.discounts();

        // Check and add discounts.
        String discounts = (String) messageContext.getProperty(Constants.DISCOUNTS);
        if (discounts != null && !discounts.isEmpty()) {
            JSONObject jObject = new JSONObject(discounts);

            // Add Discounts.
            if (jObject.has(JSONKeys.ADD)) {
                JSONArray addDiscountsArray = jObject.getJSONArray(JSONKeys.ADD);
                // For each of the Add Discount request
                for (int i = 0; i < addDiscountsArray.length(); i++) {
                    JSONObject discountObject = addDiscountsArray.getJSONObject(i);
                    setValues(discountRequest.add(), discountObject);
                }
            }

            // Update Discounts.
            if (jObject.has(JSONKeys.UPDATE)) {
                JSONArray updateAddOnsArray = jObject.getJSONArray(JSONKeys.UPDATE);
                for (int i = 0; i < updateAddOnsArray.length(); i++) {
                    JSONObject discountObject = updateAddOnsArray.getJSONObject(i);
                    if (discountObject.has(JSONKeys.DISCOUNT_ID)) {
                        setValues(discountRequest.update(discountObject.getString(JSONKeys.DISCOUNT_ID)),
                                discountObject);
                    }
                }
            }

            // Remove Discounts.
            if (jObject.has(JSONKeys.REMOVE)) {
                JSONArray removeAddOnsArray = jObject.getJSONArray(JSONKeys.REMOVE);
                for (int i = 0; i < removeAddOnsArray.length(); i++) {
                    JSONObject discountObject = removeAddOnsArray.getJSONObject(i);
                    if (discountObject.has(JSONKeys.DISCOUNT_ID)) {
                        discountRequest.remove(discountObject.getString(JSONKeys.DISCOUNT_ID));
                    }
                }
            }
            return discountRequest.done();
        }
        return request;
    }

    /**
     * Check and Set an UpdateModificationRequest.
     * 
     * @param request
     *            UpdateModificationRequest
     * @param jObject
     *            JSONObject to extract data from
     * @throws JSONException
     *             the json exception
     */
    private void setValues(final UpdateModificationRequest updateRequest, final JSONObject jObject)
            throws JSONException {

        String value;

        if (jObject.has(JSONKeys.AMOUNT) && !(value = jObject.getString(JSONKeys.AMOUNT)).isEmpty()) {
            updateRequest.amount(new BigDecimal(value));
        }

        if (jObject.has(JSONKeys.NO_OF_BILLING_CYCLES)) {
            updateRequest.numberOfBillingCycles(jObject.getInt(JSONKeys.NO_OF_BILLING_CYCLES));
        }

        if (jObject.has(JSONKeys.QUANTITY)) {
            updateRequest.quantity(jObject.getInt(JSONKeys.QUANTITY));
        }

        if (jObject.has(JSONKeys.NEVER_EXPIRES)) {
            updateRequest.neverExpires(jObject.getBoolean(JSONKeys.NEVER_EXPIRES));
        }

        updateRequest.done();
    }

    /**
     * Check and Set an AddModificationRequest.
     * 
     * @param request
     *            - AddModificationRequest
     * @param jObject
     *            - JSONObject to extract data from
     * @throws JSONException
     *             the json exception
     */
    private void setValues(final AddModificationRequest request, final JSONObject jObject) throws JSONException {

        // Set values to Add Modification Request.
        AddModificationRequest addRequest = request;

        String value;

        if (jObject.has(JSONKeys.INHERITED_FROM_ID)
                && !(value = jObject.getString(JSONKeys.INHERITED_FROM_ID)).isEmpty()) {
            addRequest.inheritedFromId(value);
        }

        if (jObject.has(JSONKeys.AMOUNT) && !(value = jObject.getString(JSONKeys.AMOUNT)).isEmpty()) {
            addRequest.amount(new BigDecimal(value));
        }

        if (jObject.has(JSONKeys.NO_OF_BILLING_CYCLES)) {
            addRequest.numberOfBillingCycles(jObject.getInt(JSONKeys.NO_OF_BILLING_CYCLES));
        }

        if (jObject.has(JSONKeys.QUANTITY)) {
            addRequest.quantity(jObject.getInt(JSONKeys.QUANTITY));
        }

        if (jObject.has(JSONKeys.NEVER_EXPIRES)) {
            addRequest.neverExpires(jObject.getBoolean(JSONKeys.NEVER_EXPIRES));
        }

        addRequest.done();
    }

}
