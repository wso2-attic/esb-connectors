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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.synapse.MessageContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Subscription;
import com.braintreegateway.SubscriptionSearchRequest;
import com.braintreegateway.exceptions.BraintreeException;
import com.google.gson.Gson;

/**
 * BrainTreeSearchSubscriptions - Searches through subscriptions.
 * 
 * @see https://www.braintreepayments.com/docs/java/subscriptions/search
 */
public final class SearchSubscriptions extends AbstractBrainTreeConnector {

    /** The message context. */
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

            // creating a subscription search & convert to JSON format and set
            // to messageContext
            messageContext
                    .setProperty(
                            Constants.RESULT,
                            new Gson()
                                    .toJson(braintreeGateway
                                            .subscription()
                                            .search(setBillingDetails(setTrialDetails(
                                            		setStatus(
                                            				setPlans(setPrice(new SubscriptionSearchRequest()))))))));

        } catch (NumberFormatException nfe) {
            errorMessage = Constants.ErrorConstants.INVALID_NUMBER_FORMAT_MSG;
            log.error(errorMessage, nfe);
            storeErrorResponseStatus(messageContext, errorMessage,
                    Constants.ErrorConstants.ERROR_CODE_NUMBER_FORMAT_EXCEPTION);
            handleException(errorMessage, nfe, messageContext);
        } catch (BraintreeException be) {
            errorMessage = be.getMessage();
            log.error(errorMessage, be);
            storeErrorResponseStatus(messageContext, errorMessage,
                    Constants.ErrorConstants.ERROR_CODE_BRAINTREE_EXCEPTION);
            handleException(errorMessage, be, messageContext);
        } catch (JSONException jsone) {
            errorMessage = Constants.ErrorConstants.INVALID_JSON_MSG;
            log.error(errorMessage, jsone);
            storeErrorResponseStatus(messageContext, errorMessage,
                    Constants.ErrorConstants.ERROR_CODE_JSON_EXCEPTION);
            handleException(errorMessage, jsone, messageContext);
        } catch (ParseException pe) {
            errorMessage = Constants.ErrorConstants.PARSER_EXCEPTION_MSG;
            log.error(errorMessage, pe);
            storeErrorResponseStatus(messageContext, errorMessage,
                    Constants.ErrorConstants.ERROR_CODE_PARSER_EXCEPTION);
            handleException(errorMessage, pe, messageContext);
        } catch (RuntimeException re) {
            errorMessage = Constants.ErrorConstants.GENERIC_ERROR_MSG;
            log.error(errorMessage, re);
            storeErrorResponseStatus(messageContext, errorMessage,
                    Constants.ErrorConstants.ERROR_CODE_RUNTIME_EXCEPTION);
            handleException(errorMessage, re, messageContext);
        }
    }

    /**
     * Sets the price search parameters.
     * 
     * @param subscriptionSearchRequest
     *            the subscription search request
     * @return the subscription search request object with the relevant values
     *         set
     * @throws JSONException
     *             Thrown on JSON parsing error
     */
    private SubscriptionSearchRequest setPrice(final SubscriptionSearchRequest subscriptionSearchRequest)
            throws JSONException {

        String value = "";
        JSONArray searchParamArray = null;
        JSONObject jObject = null;
        String tempString = (String) messageContext.getProperty(Constants.PRICE);

        if (tempString != null && !tempString.isEmpty()) {
            // Search params for billing cycles remaining
            searchParamArray = new JSONArray(tempString);
            for (int i = 0; i < searchParamArray.length(); i++) {
                jObject = searchParamArray.getJSONObject(i);
                if (jObject.has(Constants.JSONKeys.IS)) {
                    subscriptionSearchRequest.price().is(jObject.getInt(Constants.JSONKeys.IS));
                }
                if (jObject.has(Constants.JSONKeys.GREATER_THAN_OR_EQUAL_TO)
                        && !(value = jObject.getString(Constants.JSONKeys.GREATER_THAN_OR_EQUAL_TO)).isEmpty()) {
                    subscriptionSearchRequest.price().greaterThanOrEqualTo(value);
                }
                if (jObject.has(Constants.JSONKeys.LESS_THAN_OR_EQUAL_TO)
                        && !(value = jObject.getString(Constants.JSONKeys.LESS_THAN_OR_EQUAL_TO)).isEmpty()) {
                    subscriptionSearchRequest.price().lessThanOrEqualTo(value);
                }
                if (jObject.has(Constants.JSONKeys.BETWEEN)) {
                    JSONArray betweenArray = jObject.getJSONArray(Constants.JSONKeys.BETWEEN);
                    subscriptionSearchRequest.price().between(betweenArray.getInt(0), betweenArray.getInt(1));
                }
            }
        }
        return subscriptionSearchRequest;
    }

    /**
     * Sets the plans.
     * 
     * @param subscriptionSearchRequest
     *            the subscription search request
     * @return A Subscription Search object.
     * @throws JSONException
     *             Thrown on JSON parsing error.
     * @throws ParseException
     *             Thrown on Date parsing error.
     */
    private SubscriptionSearchRequest setPlans(final SubscriptionSearchRequest subscriptionSearchRequest)
            throws JSONException, ParseException {

        String value = "";
        JSONArray searchParamArray = null;
        JSONObject jObject = null;
        String tempString = (String) messageContext.getProperty(Constants.PLAN_ID);

        if (tempString != null && !tempString.isEmpty()) {
            // Search params for planId
            searchParamArray = new JSONArray(tempString);

            for (int i = 0; i < searchParamArray.length(); i++) {
                jObject = searchParamArray.getJSONObject(i);
                if (jObject.has(Constants.JSONKeys.IS)
                        && !(value = jObject.getString(Constants.JSONKeys.IS)).isEmpty()) {
                    subscriptionSearchRequest.planId().is(value);
                }

                if (jObject.has(Constants.JSONKeys.IN)) {
                    JSONArray planIdInParamsArray = jObject.getJSONArray(Constants.JSONKeys.IN);
                    List<String> planIdInParamsList = new ArrayList<String>();
                    for (int j = 0; j < planIdInParamsArray.length(); j++) {
                        planIdInParamsList.add(planIdInParamsArray.getString(j));
                    }
                    subscriptionSearchRequest.planId().in(planIdInParamsList);
                }
            }

        }
        return subscriptionSearchRequest;
    }

    /**
     * Sets the status.
     * 
     * @param subscriptionSearchRequest
     *            the subscription search request
     * @return A Subscription Search object.
     * @throws JSONException
     *             Thrown on JSON parsing error.
     * @throws ParseException
     *             Thrown on Date parsing error.
     */
    private SubscriptionSearchRequest setStatus(final SubscriptionSearchRequest subscriptionSearchRequest)
            throws JSONException, ParseException {

        String value = "";
        JSONArray searchParamArray = null;
        JSONObject jObject = null;

        String tempString = (String) messageContext.getProperty(Constants.STATUS);

        if (tempString != null && !tempString.isEmpty()) {
            // search params for status
            searchParamArray = new JSONArray(tempString);

            Map<String, Subscription.Status> subscriptionStatusMap = buildSubscriptionStatusMap();

            for (int i = 0; i < searchParamArray.length(); i++) {
                jObject = searchParamArray.getJSONObject(i);
                if (jObject.has(Constants.JSONKeys.IS)
                        && !(value = jObject.getString(Constants.JSONKeys.IS)).isEmpty()) {
                    subscriptionSearchRequest.status().is(subscriptionStatusMap.get(value));
                }
                if (jObject.has(Constants.JSONKeys.IN)) {
                    JSONArray statusInParamsArray = jObject.getJSONArray(Constants.JSONKeys.IN);
                    List<Subscription.Status> statusInParamsList = new ArrayList<Subscription.Status>();
                    for (int j = 0; j < statusInParamsArray.length(); j++) {
                        statusInParamsList.add(subscriptionStatusMap.get(statusInParamsArray.getString(j)));
                    }
                    subscriptionSearchRequest.status().in(statusInParamsList);
                }
            }

        }
        return subscriptionSearchRequest;
    }

    /**
     * Sets the trial details.
     * 
     * @param subscriptionSearchRequest
     *            the subscription search request
     * @return A Subscription Search object.
     * @throws JSONException
     *             Thrown on JSON parsing error.
     * @throws ParseException
     *             Thrown on Date parsing error.
     */
    private SubscriptionSearchRequest setTrialDetails(final SubscriptionSearchRequest subscriptionSearchRequest)
            throws JSONException, ParseException {

        String value = "";
        JSONArray searchParamArray = null;
        JSONObject jObject = null;

        String tempString = (String) messageContext.getProperty(Constants.IN_TRIAL_PERIOD);

        if (tempString != null && !tempString.isEmpty()) {
            subscriptionSearchRequest.inTrialPeriod().is(Boolean.valueOf(tempString));
        }

        tempString = (String) messageContext.getProperty(Constants.DAYS_PAST_DUE);
        if (tempString != null && !tempString.isEmpty()) {
            // Search params for days past due
            searchParamArray = new JSONArray(tempString);
            for (int i = 0; i < searchParamArray.length(); i++) {
                jObject = searchParamArray.getJSONObject(i);
                if (jObject.has(Constants.JSONKeys.IS)
                        && !(value = jObject.getString(Constants.JSONKeys.IS)).isEmpty()) {
                    subscriptionSearchRequest.daysPastDue().is(value);

                }
                if (jObject.has(Constants.JSONKeys.GREATER_THAN_OR_EQUAL_TO)
                        && !(value = jObject.getString(Constants.JSONKeys.GREATER_THAN_OR_EQUAL_TO)).isEmpty()) {
                    subscriptionSearchRequest.daysPastDue().greaterThanOrEqualTo(value);
                }
                if (jObject.has(Constants.JSONKeys.LESS_THAN_OR_EQUAL_TO)
                        && !(value = jObject.getString(Constants.JSONKeys.LESS_THAN_OR_EQUAL_TO)).isEmpty()) {
                    subscriptionSearchRequest.daysPastDue().lessThanOrEqualTo(value);
                }
                if (jObject.has(Constants.JSONKeys.BETWEEN)) {
                    JSONArray betweenArray = jObject.getJSONArray(Constants.JSONKeys.BETWEEN);
                    subscriptionSearchRequest.daysPastDue().between(betweenArray.getInt(0), betweenArray.getInt(1));
                }

            }
        }
        return subscriptionSearchRequest;
    }

    /**
     * Sets the billing details.
     * 
     * @param subscriptionSearchRequest
     *            the subscription search request
     * @return A Subscription Search object.
     * @throws JSONException
     *             Thrown on JSON parsing error.
     * @throws ParseException
     *             Thrown on Date parsing error.
     */
    private SubscriptionSearchRequest setBillingDetails(final SubscriptionSearchRequest subscriptionSearchRequest)
            throws JSONException, ParseException {

        String value = "";
        JSONArray searchParamArray = null;
        JSONObject jObject = null;

        String tempString = (String) messageContext.getProperty(Constants.NEXT_BILLING_DATE);

        if (tempString != null && !tempString.isEmpty()) {
            // Search params for next billing date
            searchParamArray = new JSONArray(tempString);
            DateFormat dateFormatter = new SimpleDateFormat(Constants.SEARCH_DATE_FORMATTER);
            Calendar nextBillingDateCalendar = Calendar.getInstance();
            for (int i = 0; i < searchParamArray.length(); i++) {
                jObject = searchParamArray.getJSONObject(i);
                if (jObject.has(Constants.JSONKeys.LESS_THAN_OR_EQUAL_TO)
                        && !(value = jObject.getString(Constants.JSONKeys.LESS_THAN_OR_EQUAL_TO)).isEmpty()) {
                    Date lessThanOrEqualToDate = dateFormatter.parse(value);
                    nextBillingDateCalendar.setTime(lessThanOrEqualToDate);
                    subscriptionSearchRequest.nextBillingDate().lessThanOrEqualTo(nextBillingDateCalendar);
                }
                if (jObject.has(Constants.JSONKeys.GREATER_THAN_OR_EQUAL_TO)
                        && !(value = jObject.getString(Constants.JSONKeys.GREATER_THAN_OR_EQUAL_TO)).isEmpty()) {
                    Date greaterThanOrEqualToDate = dateFormatter.parse(value);
                    nextBillingDateCalendar.setTime(greaterThanOrEqualToDate);
                    subscriptionSearchRequest.nextBillingDate().greaterThanOrEqualTo(nextBillingDateCalendar);
                }
            }
        }

        tempString = (String) messageContext.getProperty(Constants.BILLING_CYCLES_REMAINING);

        if (tempString != null && !tempString.isEmpty()) {
            // Search params for billing cycles remaining
            searchParamArray = new JSONArray(tempString);
            for (int i = 0; i < searchParamArray.length(); i++) {
                jObject = searchParamArray.getJSONObject(i);
                if (jObject.has(Constants.JSONKeys.IS)) {
                    subscriptionSearchRequest.billingCyclesRemaining().is(jObject.getInt(Constants.JSONKeys.IS));
                }
                if (jObject.has(Constants.JSONKeys.GREATER_THAN_OR_EQUAL_TO)
                        && !(value = jObject.getString(Constants.JSONKeys.GREATER_THAN_OR_EQUAL_TO)).isEmpty()) {
                    subscriptionSearchRequest.billingCyclesRemaining().greaterThanOrEqualTo(value);
                }
                if (jObject.has(Constants.JSONKeys.LESS_THAN_OR_EQUAL_TO)
                        && !(value = jObject.getString(Constants.JSONKeys.LESS_THAN_OR_EQUAL_TO)).isEmpty()) {
                    subscriptionSearchRequest.billingCyclesRemaining().lessThanOrEqualTo(value);
                }
                if (jObject.has(Constants.JSONKeys.BETWEEN)) {
                    JSONArray betweenArray = jObject.getJSONArray(Constants.JSONKeys.BETWEEN);
                    subscriptionSearchRequest.billingCyclesRemaining().between(betweenArray.getInt(0),
                            betweenArray.getInt(1));
                }
            }

        }

        return subscriptionSearchRequest;
    }

}
