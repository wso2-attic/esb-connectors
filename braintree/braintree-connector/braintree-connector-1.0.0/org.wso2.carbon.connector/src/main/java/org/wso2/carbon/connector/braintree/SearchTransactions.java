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

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CreditCard;
import com.braintreegateway.DateRangeNode;
import com.braintreegateway.EqualityNode;
import com.braintreegateway.PartialMatchNode;
import com.braintreegateway.ResourceCollection;
import com.braintreegateway.TextNode;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionSearchRequest;
import com.braintreegateway.exceptions.AuthenticationException;
import com.braintreegateway.exceptions.AuthorizationException;
import com.braintreegateway.exceptions.BraintreeException;
import com.braintreegateway.exceptions.NotFoundException;
import com.google.gson.Gson;

/**
 * BraintreeGetTransaction - Search Transactions.
 * 
 * @see https://www.braintreepayments.com/docs/java/transactions/search
 */
public final class SearchTransactions extends AbstractBrainTreeConnector {
    
    /**
     * Instance variable to hold the MessageContext object passed in via the Synapse template.
     */
    private MessageContext messageContext;
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param msgContext Synapse Message Context.
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
            messageContext.setProperty(Constants.RESULT,
                    new Gson().toJson(searchTransaction(braintreeGateway, getTransactionSearchRequest())));
            
        } catch (NotFoundException nfe) {
            errorMessage = ErrorConstants.INVALID_RESOURCE_MSG;
            log.error(errorMessage, nfe);
            storeErrorResponseStatus(messageContext, errorMessage, ErrorConstants.ERROR_CODE_NOT_FOUND_EXCEPTION);
            handleException(errorMessage, nfe, messageContext);
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
        } catch (ParseException re) {
            errorMessage = ErrorConstants.PARSER_EXCEPTION_MSG;
            log.error(errorMessage, re);
            storeErrorResponseStatus(messageContext, errorMessage, ErrorConstants.ERROR_CODE_PARSER_EXCEPTION);
            handleException(errorMessage, re, messageContext);
        } catch (RuntimeException re) {
            errorMessage = ErrorConstants.GENERIC_ERROR_MSG;
            log.error(errorMessage, re);
            storeErrorResponseStatus(messageContext, errorMessage, ErrorConstants.ERROR_CODE_RUNTIME_EXCEPTION);
            handleException(errorMessage, re, messageContext);
        }
    }
    
    /**
     * Gets the transaction search request.
     * 
     * @return the transaction search request
     * @throws JSONException the json exception
     * @throws ParseException the parse exception
     */
    private TransactionSearchRequest getTransactionSearchRequest() throws JSONException, ParseException {
    
        return searchStatusChangeFields(searchTopLevelFields(
                searchShippingAddressFields(searchBillingAddressFields(searchCustomerFields(
                        searchCreditCardFields(new TransactionSearchRequest()))))));
    }
    
    /**
     * Setting a TextNode search criteria. TextNode can be queried as follows [is, isNot, startsWith,
     * endsWith, contains]
     * 
     * @param request TextNode<TransactionSearchRequest>
     * @param jsonObject Object which has the query data
     * @throws JSONException the json exception
     */
    private void setTextNode(final TextNode<TransactionSearchRequest> request, final JSONObject jsonObject)
            throws JSONException {
    
        String value;
        
        if (jsonObject.has(Constants.JSONKeys.IS) && 
                !(value = jsonObject.getString(Constants.JSONKeys.IS)).isEmpty()) {
            request.is(value);
        }
        if (jsonObject.has(JSONKeys.IS_NOT) && !(value = jsonObject.getString(Constants.JSONKeys.IS_NOT)).isEmpty()) {
            request.isNot(value);
        }
        if (jsonObject.has(JSONKeys.STARTS_WITH)
                && !(value = jsonObject.getString(Constants.JSONKeys.STARTS_WITH)).isEmpty()) {
            request.startsWith(value);
        }
        if (jsonObject.has(JSONKeys.ENDS_WITH)
                && !(value = jsonObject.getString(Constants.JSONKeys.ENDS_WITH)).isEmpty()) {
            request.endsWith(value);
        }
        if (jsonObject.has(JSONKeys.CONTAINS) &&
                !(value = jsonObject.getString(Constants.JSONKeys.CONTAINS)).isEmpty()) {
            request.contains(value);
        }
    }
    
    /**
     * Setting a PartialMatchNode search criteria. PartialMatchNode can be queried as follows [is, isNot,
     * startsWith, endsWith]
     * 
     * @param request PartialMatchNode<TransactionSearchRequest>
     * @param jsonObject Object which has the query data
     * @throws JSONException the json exception
     */
    private void setPartialMatchNode(final PartialMatchNode<TransactionSearchRequest> request,
            final JSONObject jsonObject) throws JSONException {
    
        String value;
        
        if (jsonObject.has(Constants.JSONKeys.IS) &&
                !(value = jsonObject.getString(Constants.JSONKeys.IS)).isEmpty()) {
            request.is(value);
        }
        if (jsonObject.has(JSONKeys.IS_NOT) && !(value = jsonObject.getString(Constants.JSONKeys.IS_NOT)).isEmpty()) {
            request.isNot(value);
        }
        if (jsonObject.has(JSONKeys.STARTS_WITH)
                && !(value = jsonObject.getString(Constants.JSONKeys.STARTS_WITH)).isEmpty()) {
            request.startsWith(value);
        }
        if (jsonObject.has(JSONKeys.ENDS_WITH)
                && !(value = jsonObject.getString(Constants.JSONKeys.ENDS_WITH)).isEmpty()) {
            request.endsWith(value);
        }
    }
    
    /**
     * Setting a EqualityNode search criteria. EqualityNode can be queried as follows [is, isNot]
     * 
     * @param request EqualityNode<TransactionSearchRequest>
     * @param jsonObject Object which has the query data
     * @throws JSONException the json exception
     */
    private void setEqualityNode(final EqualityNode<TransactionSearchRequest> request, final JSONObject jsonObject)
            throws JSONException {
    
        String value;
        
        if (jsonObject.has(Constants.JSONKeys.IS) &&
                !(value = jsonObject.getString(Constants.JSONKeys.IS)).isEmpty()) {
            request.is(value);
        }
        if (jsonObject.has(JSONKeys.IS_NOT) && !(value = jsonObject.getString(Constants.JSONKeys.IS_NOT)).isEmpty()) {
            request.isNot(value);
        }
    }
    
    /**
     * Setting a DateRangeNode search criteria. DateRangeNode can be queried as follows [lessThanOrEqualTo,
     * greaterThanOrEqualTo, between]
     * 
     * @param request DateRangeNode<TransactionSearchRequest>
     * @param jsonObject Object which has the query data
     * @throws JSONException the json exception
     * @throws ParseException the parse exception
     */
    private void setDateRangeNode(final DateRangeNode<TransactionSearchRequest> request, final JSONObject jsonObject)
            throws JSONException, ParseException {
    
        String value;
        
        if (jsonObject.has(Constants.JSONKeys.LESS_THAN_OR_EQUAL_TO)
                && !(value = jsonObject.getString(Constants.JSONKeys.LESS_THAN_OR_EQUAL_TO)).isEmpty()) {
            request.lessThanOrEqualTo(getCalender(value));
        }
        if (jsonObject.has(JSONKeys.GREATER_THAN_OR_EQUAL_TO)
                && !(value = jsonObject.getString(Constants.JSONKeys.GREATER_THAN_OR_EQUAL_TO)).isEmpty()) {
            request.greaterThanOrEqualTo(getCalender(value));
        }
        if (jsonObject.has(JSONKeys.BETWEEN) && 
                !(value = jsonObject.getString(Constants.JSONKeys.BETWEEN)).isEmpty()) {
            request.between(getCalender(value.split(Constants.SEPERATOR)[0]),
                    getCalender(value.split(Constants.SEPERATOR)[1]));
        }
    }
    
    /**
     * Adds the credit card fields search criteria.
     * 
     * @param request TransactionSearchRequest
     * @return the transaction search request
     * @throws JSONException the json exception
     */
    private TransactionSearchRequest searchCreditCardFields(final TransactionSearchRequest request)
            throws JSONException {
    
        final String creditCardFields = (String) messageContext.getProperty(Constants.CREDIT_CARD_FIELDS);
        
        // Only if the creditCardFields JSONObject is sent by the user
        if (creditCardFields != null && !creditCardFields.isEmpty()) {
            final JSONObject creditCardObject = new JSONObject(creditCardFields);
            // Search with Credit Card Number | PartialMatchNode
            if (creditCardObject.has(JSONKeys.CREDIT_CARD_NO)) {
                final JSONArray array = creditCardObject.getJSONArray(JSONKeys.CREDIT_CARD_NO);
                for (int index = 0; index < array.length(); index++) {
                    setPartialMatchNode(request.creditCardNumber(), array.getJSONObject(index));
                }
            }
            // Search with Credit Card Number | EqualityNode
            if (creditCardObject.has(JSONKeys.CREDIT_CARD_EXPIRATION_DATE)) {
                final JSONArray array = creditCardObject.getJSONArray(JSONKeys.CREDIT_CARD_EXPIRATION_DATE);
                for (int index = 0; index < array.length(); index++) {
                    setEqualityNode(request.creditCardExpirationDate(), array.getJSONObject(index));
                }
            }
            // Search with Credit Card Holder Name | TextNode
            if (creditCardObject.has(JSONKeys.HOLDER_NAME)) {
                final JSONArray array = creditCardObject.getJSONArray(JSONKeys.HOLDER_NAME);
                for (int index = 0; index < array.length(); index++) {
                    setTextNode(request.creditCardCardholderName(), array.getJSONObject(index));
                }
            }
            // Search with Credit Card Customer Location
            if (creditCardObject.has(JSONKeys.CUSTOMER_LOCATION)
                    && !creditCardObject.getString(JSONKeys.CUSTOMER_LOCATION).isEmpty()) {
                request.creditCardCustomerLocation().is(
                        CreditCard.CustomerLocation.valueOf(creditCardObject.getString(JSONKeys.CUSTOMER_LOCATION)));
            }
            // Search with Credit Card Type [is, in]
            if (creditCardObject.has(JSONKeys.TYPE)) {
                final JSONObject object = creditCardObject.getJSONObject(JSONKeys.TYPE);
                if (object != null) {
                    if (object.has(Constants.JSONKeys.IS) && !object.getString(Constants.JSONKeys.IS).isEmpty()) {
                        request.creditCardCardType().is(
                                CreditCard.CardType.valueOf(object.getString(Constants.JSONKeys.IS)));
                    } else if (object.has(Constants.JSONKeys.IN) &&
                            !object.getString(Constants.JSONKeys.IN).isEmpty()) {
                        final JSONArray array = object.getJSONArray(Constants.JSONKeys.IN);
                        final CreditCard.CardType[] enums = new CreditCard.CardType[array.length()];
                        for (int index = 0; index < enums.length; index++) {
                            enums[index] = CreditCard.CardType.valueOf(array.getString(index).toUpperCase(Locale.US));
                        }
                        request.creditCardCardType().in(enums);
                    }
                }
            }
        }
        return request;
    }
    
    /**
     * Sets the customer fields search criteria.
     * 
     * @param request TransactionSearchRequest
     * @return TransactionSearchRequest
     * @throws JSONException the json exception
     */
    private TransactionSearchRequest searchCustomerFields(final TransactionSearchRequest request) 
            throws JSONException {
    
        final String customerFields = (String) messageContext.getProperty(Constants.CUSTOMER_FIELDS);
        if (customerFields != null && !customerFields.isEmpty()) {
            
            final JSONObject customerFieldsObject = new JSONObject(customerFields);
            
            // Search Customer based on ID | TextNode.
            if (customerFieldsObject.has(JSONKeys.ID)) {
                final JSONArray array = customerFieldsObject.getJSONArray(JSONKeys.ID);
                for (int index = 0; index < array.length(); index++) {
                    setTextNode(request.customerId(), array.getJSONObject(index));
                }
            }
            
            // Search Customer based on First name | TextNode.
            if (customerFieldsObject.has(JSONKeys.FIRST_NAME)) {
                final JSONArray array = customerFieldsObject.getJSONArray(JSONKeys.FIRST_NAME);
                for (int index = 0; index < array.length(); index++) {
                    setTextNode(request.customerFirstName(), array.getJSONObject(index));
                }
            }
            
            // Search Customer based on Last name | TextNode.
            if (customerFieldsObject.has(JSONKeys.LAST_NAME)) {
                JSONArray array = customerFieldsObject.getJSONArray(JSONKeys.LAST_NAME);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.customerLastName(), array.getJSONObject(i));
                }
            }
            
            // Search Customer based on Company | TextNode.
            if (customerFieldsObject.has(JSONKeys.COMPANY)) {
                JSONArray array = customerFieldsObject.getJSONArray(JSONKeys.COMPANY);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.customerCompany(), array.getJSONObject(i));
                }
            }
            
            // Search Customer based on Email | TextNode.
            if (customerFieldsObject.has(JSONKeys.EMAIL)) {
                JSONArray array = customerFieldsObject.getJSONArray(JSONKeys.EMAIL);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.customerEmail(), array.getJSONObject(i));
                }
            }
            
            // Search Customer based on Phone | TextNode.
            if (customerFieldsObject.has(JSONKeys.PHONE)) {
                JSONArray array = customerFieldsObject.getJSONArray(JSONKeys.PHONE);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.customerPhone(), array.getJSONObject(i));
                }
            }
            
            // Search Customer based on Website | TextNode.
            if (customerFieldsObject.has(JSONKeys.WEBSITE)) {
                JSONArray array = customerFieldsObject.getJSONArray(JSONKeys.WEBSITE);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.customerWebsite(), array.getJSONObject(i));
                }
            }
            
            // Search Customer based on Fax | TextNode.
            if (customerFieldsObject.has(JSONKeys.FAX)) {
                JSONArray array = customerFieldsObject.getJSONArray(JSONKeys.FAX);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.customerFax(), array.getJSONObject(i));
                }
            }
        }
        return request;
    }
    
    /**
     * Sets the billing address fields search criteria.
     * 
     * @param request TransactionSearchRequest
     * @return the TransactionSearchRequest
     * @throws JSONException the json exception
     */
    private TransactionSearchRequest searchBillingAddressFields(final TransactionSearchRequest request)
            throws JSONException {
    
        final String billingAddressFields = (String) messageContext.getProperty(Constants.BILLING_ADDRESS_FIELDS);
        if (billingAddressFields != null && !billingAddressFields.isEmpty()) {
            
            JSONObject billingAddressFieldsObject = new JSONObject(billingAddressFields);
            
            // Search BillingAddress based on Region | TextNode.
            if (billingAddressFieldsObject.has(JSONKeys.REGION)) {
                JSONArray array = billingAddressFieldsObject.getJSONArray(JSONKeys.REGION);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.billingRegion(), array.getJSONObject(i));
                }
            }
            
            // Search BillingAddress based on Locality | TextNode.
            if (billingAddressFieldsObject.has(JSONKeys.LOCALITY)) {
                JSONArray array = billingAddressFieldsObject.getJSONArray(JSONKeys.LOCALITY);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.billingLocality(), array.getJSONObject(i));
                }
            }
            
            // Search BillingAddress based on ExtendedAddress | TextNode.
            if (billingAddressFieldsObject.has(JSONKeys.EXTENDED_ADDRESS)) {
                JSONArray array = billingAddressFieldsObject.getJSONArray(JSONKeys.EXTENDED_ADDRESS);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.billingExtendedAddress(), array.getJSONObject(i));
                }
            }
            
            // Search BillingAddress based on Fisrtname | TextNode.
            if (billingAddressFieldsObject.has(JSONKeys.FIRST_NAME)) {
                JSONArray array = billingAddressFieldsObject.getJSONArray(JSONKeys.FIRST_NAME);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.billingFirstName(), array.getJSONObject(i));
                }
            }
            
            // Search BillingAddress based on Lastname | TextNode.
            if (billingAddressFieldsObject.has(JSONKeys.LAST_NAME)) {
                JSONArray array = billingAddressFieldsObject.getJSONArray(JSONKeys.LAST_NAME);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.billingLastName(), array.getJSONObject(i));
                }
            }
            
            // Search BillingAddress based on StreetAddress | TextNode.
            if (billingAddressFieldsObject.has(JSONKeys.STREET_ADDRESS)) {
                JSONArray array = billingAddressFieldsObject.getJSONArray(JSONKeys.STREET_ADDRESS);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.billingStreetAddress(), array.getJSONObject(i));
                }
            }
            
            // Search BillingAddress based on Company | TextNode.
            if (billingAddressFieldsObject.has(JSONKeys.COMPANY)) {
                JSONArray array = billingAddressFieldsObject.getJSONArray(JSONKeys.COMPANY);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.billingCompany(), array.getJSONObject(i));
                }
            }
            
            // Search BillingAddress based on Country Name | TextNode.
            if (billingAddressFieldsObject.has(JSONKeys.COUNTRY_NAME)) {
                JSONArray array = billingAddressFieldsObject.getJSONArray(JSONKeys.COUNTRY_NAME);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.billingCountryName(), array.getJSONObject(i));
                }
            }
            
            // Search BillingAddress based on Postal Code | TextNode.
            if (billingAddressFieldsObject.has(JSONKeys.POSTAL_CODE)) {
                JSONArray array = billingAddressFieldsObject.getJSONArray(JSONKeys.POSTAL_CODE);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.billingPostalCode(), array.getJSONObject(i));
                }
            }
        }
        return request;
    }
    
    /**
     * Sets the shipping address fields search criteria.
     * 
     * @param request TransactionSearchRequest
     * @return TransactionSearchRequest
     * @throws JSONException the json exception
     */
    private TransactionSearchRequest searchShippingAddressFields(final TransactionSearchRequest request)
            throws JSONException {
    
        final String shippingAddressFields = (String) messageContext.getProperty(Constants.SHIPPING_ADDRESS_FIELDS);
        if (shippingAddressFields != null && !shippingAddressFields.isEmpty()) {
            
            JSONObject shippingAddressFieldsObject = new JSONObject(shippingAddressFields);
            
            // Search ShippingAddress based on Region | TextNode.
            if (shippingAddressFieldsObject.has(JSONKeys.REGION)) {
                JSONArray array = shippingAddressFieldsObject.getJSONArray(JSONKeys.REGION);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.shippingRegion(), array.getJSONObject(i));
                }
            }
            
            // Search ShippingAddress based on Locality | TextNode.
            if (shippingAddressFieldsObject.has(JSONKeys.LOCALITY)) {
                JSONArray array = shippingAddressFieldsObject.getJSONArray(JSONKeys.LOCALITY);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.shippingLocality(), array.getJSONObject(i));
                }
            }
            
            // Search ShippingAddress based on ExtendedAddress | TextNode.
            if (shippingAddressFieldsObject.has(JSONKeys.EXTENDED_ADDRESS)) {
                JSONArray array = shippingAddressFieldsObject.getJSONArray(JSONKeys.EXTENDED_ADDRESS);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.shippingExtendedAddress(), array.getJSONObject(i));
                }
            }
            
            // Search ShippingAddress based on Fisrtname | TextNode.
            if (shippingAddressFieldsObject.has(JSONKeys.FIRST_NAME)) {
                JSONArray array = shippingAddressFieldsObject.getJSONArray(JSONKeys.FIRST_NAME);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.shippingFirstName(), array.getJSONObject(i));
                }
            }
            
            // Search ShippingAddress based on Lastname | TextNode.
            if (shippingAddressFieldsObject.has(JSONKeys.LAST_NAME)) {
                JSONArray array = shippingAddressFieldsObject.getJSONArray(JSONKeys.LAST_NAME);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.shippingLastName(), array.getJSONObject(i));
                }
            }
            
            // Search ShippingAddress based on StreetAddress | TextNode.
            if (shippingAddressFieldsObject.has(JSONKeys.STREET_ADDRESS)) {
                JSONArray array = shippingAddressFieldsObject.getJSONArray(JSONKeys.STREET_ADDRESS);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.shippingStreetAddress(), array.getJSONObject(i));
                }
            }
            
            // Search ShippingAddress based on Company | TextNode.
            if (shippingAddressFieldsObject.has(JSONKeys.COMPANY)) {
                JSONArray array = shippingAddressFieldsObject.getJSONArray(JSONKeys.COMPANY);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.shippingCompany(), array.getJSONObject(i));
                }
            }
            
            // Search ShippingAddress based on Country Name | TextNode.
            if (shippingAddressFieldsObject.has(JSONKeys.COUNTRY_NAME)) {
                JSONArray array = shippingAddressFieldsObject.getJSONArray(JSONKeys.COUNTRY_NAME);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.shippingCountryName(), array.getJSONObject(i));
                }
            }
            
            // Search ShippingAddress based on Postal Code | TextNode.
            if (shippingAddressFieldsObject.has(JSONKeys.POSTAL_CODE)) {
                JSONArray array = shippingAddressFieldsObject.getJSONArray(JSONKeys.POSTAL_CODE);
                for (int i = 0; i < array.length(); i++) {
                    setTextNode(request.shippingPostalCode(), array.getJSONObject(i));
                }
            }
        }
        return request;
    }
    
    /**
     * Sets the status changes fields search criteria.
     * 
     * @param request TransactionSearchRequest
     * @return TransactionSearchRequest
     * @throws JSONException the json exception
     * @throws ParseException the parse exception
     */
    private TransactionSearchRequest searchStatusChangeFields(final TransactionSearchRequest request)
            throws JSONException, ParseException {
    
        final String statusChangeFields = (String) messageContext.getProperty(Constants.STATUS_CHANGES);
        if (statusChangeFields != null && !statusChangeFields.isEmpty()) {
            JSONObject statusChangeFieldsObject = new JSONObject(statusChangeFields);
            
            // Search by Status Change: settledAt | DateRangeNode.
            if (statusChangeFieldsObject.has(JSONKeys.SETTLED_AT)) {
                JSONArray array = statusChangeFieldsObject.getJSONArray(JSONKeys.SETTLED_AT);
                for (int i = 0; i < array.length(); i++) {
                    setDateRangeNode(request.settledAt(), array.getJSONObject(i));
                }
            }
            
            // Search by Status Change: createdAt | DateRangeNode.
            if (statusChangeFieldsObject.has(JSONKeys.CREATED_AT)) {
                JSONArray array = statusChangeFieldsObject.getJSONArray(JSONKeys.CREATED_AT);
                for (int i = 0; i < array.length(); i++) {
                    setDateRangeNode(request.createdAt(), array.getJSONObject(i));
                }
            }
            
            // Search by Status Change: voidedAt | DateRangeNode.
            if (statusChangeFieldsObject.has(JSONKeys.VOIDED_AT)) {
                JSONArray array = statusChangeFieldsObject.getJSONArray(JSONKeys.VOIDED_AT);
                for (int i = 0; i < array.length(); i++) {
                    setDateRangeNode(request.voidedAt(), array.getJSONObject(i));
                }
            }
            
            // Search by Status Change: processorDeclinedAt | DateRangeNode.
            if (statusChangeFieldsObject.has(JSONKeys.PROCESS_DECLINED_AT)) {
                JSONArray array = statusChangeFieldsObject.getJSONArray(JSONKeys.PROCESS_DECLINED_AT);
                for (int i = 0; i < array.length(); i++) {
                    setDateRangeNode(request.processorDeclinedAt(), array.getJSONObject(i));
                }
            }
            
            // Search by Status Change: submittedForSettlementAt |
            // DateRangeNode.
            if (statusChangeFieldsObject.has(JSONKeys.SUBMITTED_FOR_SETTLEMENT_AT)) {
                JSONArray array = statusChangeFieldsObject.getJSONArray(JSONKeys.SUBMITTED_FOR_SETTLEMENT_AT);
                for (int i = 0; i < array.length(); i++) {
                    setDateRangeNode(request.submittedForSettlementAt(), array.getJSONObject(i));
                }
            }
            
            // Search by Status Change: authorizedAt | DateRangeNode.
            if (statusChangeFieldsObject.has(JSONKeys.AUTHORIZED_AT)) {
                JSONArray array = statusChangeFieldsObject.getJSONArray(JSONKeys.AUTHORIZED_AT);
                for (int i = 0; i < array.length(); i++) {
                    setDateRangeNode(request.authorizedAt(), array.getJSONObject(i));
                }
            }
            
            // Search by Status Change: gatewayRejectedAt | DateRangeNode.
            if (statusChangeFieldsObject.has(JSONKeys.GATEWAY_REJECTED_AT)) {
                JSONArray array = statusChangeFieldsObject.getJSONArray(JSONKeys.GATEWAY_REJECTED_AT);
                for (int i = 0; i < array.length(); i++) {
                    setDateRangeNode(request.gatewayRejectedAt(), array.getJSONObject(i));
                }
            }
            
            // Search by Status Change: failedAt | DateRangeNode.
            if (statusChangeFieldsObject.has(JSONKeys.FAILED_AT)) {
                JSONArray array = statusChangeFieldsObject.getJSONArray(JSONKeys.FAILED_AT);
                for (int i = 0; i < array.length(); i++) {
                    setDateRangeNode(request.failedAt(), array.getJSONObject(i));
                }
            }
        }
        return request;
    }
    
    /**
     * Sets the top level fields search criteria.
     * 
     * @param request TransactionSearchRequest
     * @return TransactionSearchRequest
     * @throws JSONException the json exception
     */
    private TransactionSearchRequest searchTopLevelFields(final TransactionSearchRequest request)
            throws JSONException {
    
        // Search with OrderId | TextNode.
        final String orderId = (String) messageContext.getProperty(Constants.ORDER_ID);
        if (orderId != null && !orderId.isEmpty()) {
            JSONArray array = new JSONArray(orderId);
            for (int i = 0; i < array.length(); i++) {
                setTextNode(request.orderId(), array.getJSONObject(i));
            }
        }
        
        // Search with ProcessorAuthorizationCode | TextNode.
        final String processorAuthorizationCode =
                (String) messageContext.getProperty(Constants.PROCESSOR_AUTHORIZATION_CODE);
        if (processorAuthorizationCode != null && !processorAuthorizationCode.isEmpty()) {
            JSONArray array = new JSONArray(processorAuthorizationCode);
            for (int i = 0; i < array.length(); i++) {
                setTextNode(request.processorAuthorizationCode(), array.getJSONObject(i));
            }
        }
        
        // Search with PaymentMethodToken | TextNode.
        final String paymentMethodToken = (String) messageContext.getProperty(Constants.PAYMENT_METHOD_TOKEN);
        if (paymentMethodToken != null && !paymentMethodToken.isEmpty()) {
            JSONArray array = new JSONArray(paymentMethodToken);
            for (int i = 0; i < array.length(); i++) {
                setTextNode(request.paymentMethodToken(), array.getJSONObject(i));
            }
        }
        
        // Search with Amount.
        final String amount = (String) messageContext.getProperty(Constants.AMOUNT);
        if (amount != null && !amount.isEmpty()) {
            String value;
            JSONObject object = new JSONObject(amount);
            if (object.has(JSONKeys.GREATER_THAN_OR_EQUAL_TO)
                    && !(value = object.getString(JSONKeys.GREATER_THAN_OR_EQUAL_TO)).isEmpty()) {
                request.amount().greaterThanOrEqualTo(new BigDecimal(value));
            }
            if (object.has(JSONKeys.LESS_THAN_OR_EQUAL_TO)
                    && !(value = object.getString(JSONKeys.LESS_THAN_OR_EQUAL_TO)).isEmpty()) {
                request.amount().lessThanOrEqualTo(new BigDecimal(value));
            }
            if (object.has(JSONKeys.BETWEEN) && !(value = object.getString(JSONKeys.BETWEEN)).isEmpty()) {
                request.amount().between(new BigDecimal(value.split(Constants.SEPERATOR)[0]),
                        new BigDecimal(value.split(Constants.SEPERATOR)[1]));
            }
        }
        
        // Search with MerchantID.
        final String merchantAccountId = (String) messageContext.getProperty(Constants.MERCHANT_ACCOUNT_ID);
        if (merchantAccountId != null && !merchantAccountId.isEmpty()) {
            request.merchantAccountId().is(merchantAccountId);
        }
        return request;
    }
    
    /**
     * Searches the Transaction and returns a collection of results received.
     * 
     * @param braintreeGateway Authentication Gateway
     * @param request TransactionSearchRequest
     * @return TransactionSearchRequest
     * @throws BraintreeException the braintree exception
     */
    private ResourceCollection<Transaction> searchTransaction(final BraintreeGateway braintreeGateway,
            final TransactionSearchRequest request) throws BraintreeException {
    
        return braintreeGateway.transaction().search(request);
    }
    
}
