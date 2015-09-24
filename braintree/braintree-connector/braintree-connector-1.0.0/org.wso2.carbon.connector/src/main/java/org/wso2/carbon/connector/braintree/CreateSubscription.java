/*
 * Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.connector.braintree;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.synapse.MessageContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.braintreegateway.AddModificationRequest;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ModificationsRequest;
import com.braintreegateway.Result;
import com.braintreegateway.Subscription;
import com.braintreegateway.Subscription.DurationUnit;
import com.braintreegateway.SubscriptionRequest;
import com.braintreegateway.UpdateModificationRequest;
import com.braintreegateway.exceptions.AuthenticationException;
import com.braintreegateway.exceptions.BraintreeException;
import com.google.gson.Gson;

/**
 * BraintreeCreateSubscription - This class creates a new subscription. Its required a payment method token
 * from a created credit card and a plan id from a plan created via the control panel. The subscription will
 * then be created using the price, trial duration (if any), billing cycle etc. of the plan.
 * 
 * @see https://www.braintreepayments.com/docs/java/subscriptions/create
 */
public final class CreateSubscription extends AbstractBrainTreeConnector {
    
    /** The Synapse Message Context. */
    private MessageContext msgContext = null;
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param messageContext Synapse Message Context.
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public void connect(final MessageContext messageContext) {
    
        this.msgContext = messageContext;
        String errorMessage = null;
        
        try {
            
            // instantiating and authenticating a braintreeGateway.
            BraintreeGateway braintreeGateway = getBrainTreeService(msgContext);
            
            // remove the request from the payload.
            msgContext.getEnvelope().getBody().getFirstElement().detach();
            
            // creates a new subscription and convert the response to JSON
            // format and set to messageContext.
            msgContext.setProperty(Constants.RESULT, new Gson().toJson(createSubscription(braintreeGateway)));
        } catch (ParseException pe) {
            errorMessage = Constants.ErrorConstants.PARSER_EXCEPTION_MSG;
            log.error(errorMessage, pe);
            storeErrorResponseStatus(messageContext, errorMessage, 
                    Constants.ErrorConstants.ERROR_CODE_PARSER_EXCEPTION);
            handleException(errorMessage, pe, messageContext);
        } catch (NumberFormatException nfe) {
            errorMessage = Constants.ErrorConstants.INVALID_NUMBER_FORMAT_MSG;
            log.error(errorMessage, nfe);
            storeErrorResponseStatus(messageContext, errorMessage,
                    Constants.ErrorConstants.ERROR_CODE_NUMBER_FORMAT_EXCEPTION);
            handleException(errorMessage, nfe, messageContext);
        } catch (JSONException je) {
            errorMessage = Constants.ErrorConstants.INVALID_JSON_MSG;
            log.error(errorMessage, je);
            storeErrorResponseStatus(messageContext, errorMessage, Constants.ErrorConstants.ERROR_CODE_JSON_EXCEPTION);
            handleException(errorMessage, je, messageContext);
        } catch (AuthenticationException au) {
            errorMessage = Constants.ErrorConstants.INVALID_AUTHENTICATION_MSG;
            log.error(errorMessage, au);
            storeErrorResponseStatus(messageContext, errorMessage,
                    Constants.ErrorConstants.ERROR_CODE_AUTHENTICATION_EXCEPTION);
            handleException(errorMessage, au, messageContext);
        } catch (BraintreeException be) {
            errorMessage = be.getMessage();
            log.error(errorMessage, be);
            storeErrorResponseStatus(messageContext, errorMessage,
                    Constants.ErrorConstants.ERROR_CODE_BRAINTREE_EXCEPTION);
            handleException(errorMessage, be, messageContext);
        } catch (RuntimeException re) {
            errorMessage = Constants.ErrorConstants.GENERIC_ERROR_MSG;
            log.error(errorMessage, re);
            storeErrorResponseStatus(messageContext, errorMessage,
                    Constants.ErrorConstants.ERROR_CODE_RUNTIME_EXCEPTION);
            handleException(errorMessage, re, messageContext);
        }
    }
    
    /**
     * This method creates a new subscription.
     * 
     * @param braintreeGateway The braintree gateway.
     * @return Subscription result.
     * @throws NumberFormatException The number format exception.
     * @throws JSONException The JSON exception.
     * @throws ParseException The parse exception.
     */
    private Result<Subscription> createSubscription(final BraintreeGateway braintreeGateway) throws JSONException,
            ParseException {
    
        final String paymentMethodToken = (String) msgContext.getProperty(Constants.PAYMENT_METHOD_TOKEN);
        final String planId = (String) msgContext.getProperty(Constants.PLAN_ID);
        final String price = (String) msgContext.getProperty(Constants.PRICE);
        final boolean trialPeriod = Boolean.parseBoolean((String) msgContext.getProperty(Constants.TRIAL_PERIOD));
        final String trialDuration = (String) msgContext.getProperty(Constants.TRIAL_DURATION);
        final String billingDetails = (String) msgContext.getProperty(Constants.BILLING_DETAILS);
        final String subscriptionId = (String) msgContext.getProperty(Constants.SUBSCRIPTION_ID);
        final String merchantAccountId = (String) msgContext.getProperty(Constants.MERCHANT_ACCOUNT_ID);
        final boolean doNotInheritAddOnsOrDiscounts =
                Boolean.parseBoolean((String) msgContext.getProperty(Constants.DONOT_INHERIT_ADDONS_DISCOUNTS));
        
        final SubscriptionRequest request = new SubscriptionRequest();
        
        request.paymentMethodToken(paymentMethodToken);
        request.planId(planId);
        
        if (price != null && !price.isEmpty()) {
            request.price(new BigDecimal(price));
        }
        
        // set trialPeriod details for new subscription.
        request.trialPeriod(trialPeriod);
        
        if (trialDuration != null && !trialDuration.isEmpty()) {
            final JSONObject jsonDurationObj = new JSONObject(trialDuration);
            
            if (jsonDurationObj.has(Constants.JSONKeys.MONTHS)) {
                request.trialDurationUnit(DurationUnit.MONTH);
                request.trialDuration(jsonDurationObj.getInt(Constants.JSONKeys.MONTHS));
            }
            
            if (jsonDurationObj.has(Constants.JSONKeys.DAYS)) {
                request.trialDurationUnit(DurationUnit.DAY);
                request.trialDuration(jsonDurationObj.getInt(Constants.JSONKeys.DAYS));
            }
        }
        
        request.trialPeriod(trialPeriod);
        
        // set billing details for new subscription.
        if (billingDetails != null && !billingDetails.isEmpty()) {
            final JSONObject jsonBillingObj = new JSONObject(billingDetails);
            
            if (jsonBillingObj.has(Constants.JSONKeys.START_IMMEDIATELY)) {
                request.options().startImmediately(jsonBillingObj.getBoolean(Constants.JSONKeys.START_IMMEDIATELY))
                        .done();
            }
            
            if (jsonBillingObj.has(Constants.JSONKeys.FIRST_BILLING_DATE)) {
                final Calendar date = Calendar.getInstance();
                final SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat(Constants.DATE_FORMATTER, Locale.getDefault());
                date.setTime(simpleDateFormat.parse(jsonBillingObj.getString(Constants.JSONKeys.FIRST_BILLING_DATE)));
                request.firstBillingDate(date);
            }
            
            if (jsonBillingObj.has(Constants.JSONKeys.BILLING_DAY_OF_MONTH)) {
                request.billingDayOfMonth(jsonBillingObj.getInt(Constants.JSONKeys.BILLING_DAY_OF_MONTH));
            }
        }
        
        request.id(subscriptionId);
        request.merchantAccountId(merchantAccountId);
        
        // sets addOns and discounts for new subscription.
        addAddOnsForSubscription(addDiscountsForSubscription(request));
        
        // to create a subscription without inheriting any add-ons or discounts
        // from the plan.
        request.options().doNotInheritAddOnsOrDiscounts(doNotInheritAddOnsOrDiscounts).done();
        
        return braintreeGateway.subscription().create(request);
    }
    
    /**
     * This method sets the discounts for new subscription.
     * 
     * @param request The subscription request.
     * @return The modified subscription request.
     * @throws JSONException The JSON exception.
     */
    private SubscriptionRequest addDiscountsForSubscription(final SubscriptionRequest request) throws JSONException {
    
        final String discounts = (String) msgContext.getProperty(Constants.DISCOUNTS);
        
        if (discounts != null && !discounts.isEmpty()) {
            final JSONObject discountsObj = new JSONObject(discounts);
            
            if (discountsObj.has(Constants.JSONKeys.ADD)) {
                final AddModificationRequest addModificationRequest = request.discounts().add();
                final JSONArray addArray = discountsObj.getJSONArray(Constants.JSONKeys.ADD);
                
                for (int i = 0; i < addArray.length(); i++) {
                    final JSONObject addObj = addArray.getJSONObject(i);
                    
                    if (addObj.has(Constants.JSONKeys.INHERITED_FROM_ID)) {
                        addModificationRequest.inheritedFromId(addObj.getString(Constants.JSONKeys.INHERITED_FROM_ID));
                    }
                    
                    if (addObj.has(Constants.JSONKeys.AMOUNT)) {
                        addModificationRequest.amount(new BigDecimal(addObj.getInt(Constants.JSONKeys.AMOUNT)));
                    }
                    
                    if (addObj.has(Constants.JSONKeys.QUANTITY)) {
                        addModificationRequest.quantity(addObj.getInt(Constants.JSONKeys.QUANTITY));
                    }
                    
                    if (addObj.has(Constants.JSONKeys.NEVER_EXPIRES)) {
                        addModificationRequest.neverExpires(addObj.getBoolean(Constants.JSONKeys.NEVER_EXPIRES));
                    }
                    
                    if (addObj.has(Constants.JSONKeys.NO_OF_BILLING_CYCLES)) {
                        addModificationRequest.numberOfBillingCycles(addObj
                                .getInt(Constants.JSONKeys.NO_OF_BILLING_CYCLES));
                    }
                    
                    addModificationRequest.done();
                }
                
            }
            
            if (discountsObj.has(Constants.JSONKeys.UPDATE)) {
                final ModificationsRequest modificationRequest = request.discounts();
                final JSONArray updateArray = discountsObj.getJSONArray(Constants.JSONKeys.UPDATE);
                
                for (int i = 0; i < updateArray.length(); i++) {
                    final JSONObject updateObj = updateArray.getJSONObject(i);
                    
                    final UpdateModificationRequest updateModificationRequest =
                            modificationRequest.update(updateObj.getString(Constants.JSONKeys.EXISTING_ID));
                    
                    if (updateObj.has(Constants.JSONKeys.AMOUNT)) {
                        updateModificationRequest.amount(new BigDecimal(updateObj.getInt(Constants.JSONKeys.AMOUNT)));
                    }
                    
                    if (updateObj.has(Constants.JSONKeys.QUANTITY)) {
                        updateModificationRequest.quantity(updateObj.getInt(Constants.JSONKeys.QUANTITY));
                    }
                    
                    if (updateObj.has(Constants.JSONKeys.NEVER_EXPIRES)) {
                        updateModificationRequest.neverExpires(updateObj.getBoolean(Constants.JSONKeys.NEVER_EXPIRES));
                    }
                    
                    if (updateObj.has(Constants.JSONKeys.NO_OF_BILLING_CYCLES)) {
                        updateModificationRequest.numberOfBillingCycles(updateObj
                                .getInt(Constants.JSONKeys.NO_OF_BILLING_CYCLES));
                    }
                    
                    updateModificationRequest.done();
                }
                
            }
            
            if (discountsObj.has(Constants.JSONKeys.REMOVE)) {
                final JSONArray reomveArray = discountsObj.getJSONArray(Constants.JSONKeys.REMOVE);
                final List<String> idList = new ArrayList<String>();
                
                for (int i = 0; i < reomveArray.length(); i++) {
                    idList.add(reomveArray.getString(i));
                }
                
                request.discounts().remove(idList).done();
                
            }
        }
        
        return request;
    }
    
    /**
     * This method sets the addOns for new subscription.
     * 
     * @param request The subscription request.
     * @return The modified subscription request.
     * @throws JSONException The JSON exception.
     */
    private SubscriptionRequest addAddOnsForSubscription(final SubscriptionRequest request) throws JSONException {
    
        final String addOns = (String) msgContext.getProperty(Constants.ADDONS);
        
        if (addOns != null && !addOns.isEmpty()) {
            final JSONObject addOnsObj = new JSONObject(addOns);
            
            if (addOnsObj.has(Constants.JSONKeys.ADD)) {
                final AddModificationRequest addModificationRequest = request.addOns().add();
                final JSONArray addArray = addOnsObj.getJSONArray(Constants.JSONKeys.ADD);
                
                for (int i = 0; i < addArray.length(); i++) {
                    final JSONObject addObj = addArray.getJSONObject(i);
                    
                    if (addObj.has(Constants.JSONKeys.INHERITED_FROM_ID)) {
                        addModificationRequest.inheritedFromId(addObj.getString(Constants.JSONKeys.INHERITED_FROM_ID));
                    }
                    
                    if (addObj.has(Constants.JSONKeys.AMOUNT)) {
                        addModificationRequest.amount(new BigDecimal(addObj.getInt(Constants.JSONKeys.AMOUNT)));
                    }
                    
                    if (addObj.has(Constants.JSONKeys.QUANTITY)) {
                        addModificationRequest.quantity(addObj.getInt(Constants.JSONKeys.QUANTITY));
                    }
                    
                    if (addObj.has(Constants.JSONKeys.NEVER_EXPIRES)) {
                        addModificationRequest.neverExpires(addObj.getBoolean(Constants.JSONKeys.NEVER_EXPIRES));
                    }
                    
                    if (addObj.has(Constants.JSONKeys.NO_OF_BILLING_CYCLES)) {
                        addModificationRequest.numberOfBillingCycles(addObj
                                .getInt(Constants.JSONKeys.NO_OF_BILLING_CYCLES));
                    }
                    
                    addModificationRequest.done();
                }
            }
            
            if (addOnsObj.has(Constants.JSONKeys.UPDATE)) {
                final ModificationsRequest modificationRequest = request.addOns();
                final JSONArray updateArray = addOnsObj.getJSONArray(Constants.JSONKeys.UPDATE);
                
                for (int i = 0; i < updateArray.length(); i++) {
                    final JSONObject updateObj = updateArray.getJSONObject(i);
                    
                    final UpdateModificationRequest updateModificationRequest =
                            modificationRequest.update(updateObj.getString(Constants.JSONKeys.EXISTING_ID));
                    
                    if (updateObj.has(Constants.JSONKeys.AMOUNT)) {
                        updateModificationRequest.amount(new BigDecimal(updateObj.getInt(Constants.JSONKeys.AMOUNT)));
                    }
                    
                    if (updateObj.has(Constants.JSONKeys.QUANTITY)) {
                        updateModificationRequest.quantity(updateObj.getInt(Constants.JSONKeys.QUANTITY));
                    }
                    
                    if (updateObj.has(Constants.JSONKeys.NEVER_EXPIRES)) {
                        updateModificationRequest.neverExpires(updateObj.getBoolean(Constants.JSONKeys.NEVER_EXPIRES));
                    }
                    
                    if (updateObj.has(Constants.JSONKeys.NO_OF_BILLING_CYCLES)) {
                        updateModificationRequest.numberOfBillingCycles(updateObj
                                .getInt(Constants.JSONKeys.NO_OF_BILLING_CYCLES));
                    }
                    
                    updateModificationRequest.done();
                }
            }
            
            if (addOnsObj.has(Constants.JSONKeys.REMOVE)) {
                final JSONArray reomveArray = addOnsObj.getJSONArray(Constants.JSONKeys.REMOVE);
                final List<String> idList = new ArrayList<String>();
                
                for (int i = 0; i < reomveArray.length(); i++) {
                    idList.add(reomveArray.getString(i));
                }
                
                request.addOns().remove(idList).done();
            }
        }
        
        return request;
    }
    
}
