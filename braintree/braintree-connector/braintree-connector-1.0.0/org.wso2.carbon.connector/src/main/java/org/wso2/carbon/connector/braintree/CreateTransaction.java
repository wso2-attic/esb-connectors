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

package org.wso2.carbon.connector.braintree;

import java.math.BigDecimal;

import org.apache.synapse.MessageContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.carbon.connector.braintree.Constants.ErrorConstants;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionAddressRequest;
import com.braintreegateway.TransactionCreditCardRequest;
import com.braintreegateway.TransactionOptionsRequest;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.exceptions.AuthenticationException;
import com.braintreegateway.exceptions.AuthorizationException;
import com.braintreegateway.exceptions.BraintreeException;
import com.braintreegateway.exceptions.NotFoundException;
import com.google.gson.Gson;

/**
 * BraintreeCreateTransaction - creates a transaction.
 * 
 * @see https://www.braintreepayments.com/docs/java/transactions/create
 */
public final class CreateTransaction extends AbstractBrainTreeConnector {
    
    /**
     * The message context.
     */
    private MessageContext msgContext;
    
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
            
            // instantiating and authenticating a braintreeGateway
            final BraintreeGateway braintreeGateway = getBrainTreeService(messageContext);
            
            // remove the request from the payload
            messageContext.getEnvelope().getBody().getFirstElement().detach();
            
            // creating a transaction & convert to JSON format and set to messageContext
            messageContext.setProperty(Constants.RESULT,
                    new Gson().toJson(createTransaction(braintreeGateway, getTransactionRequest())));
            
        } catch (NotFoundException re) {
            errorMessage = ErrorConstants.INVALID_RESOURCE_MSG;
            log.error(errorMessage, re);
            storeErrorResponseStatus(messageContext, errorMessage, ErrorConstants.ERROR_CODE_NOT_FOUND_EXCEPTION);
            handleException(errorMessage, re, messageContext);
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
        } catch (AuthorizationException nfe) {
            errorMessage = Constants.ErrorConstants.INVALID_AUTHERIZATION_MSG;
            log.error(errorMessage, nfe);
            storeErrorResponseStatus(messageContext, errorMessage,
                    Constants.ErrorConstants.ERROR_CODE_AUTHERIZATION_EXCEPTION);
            handleException(errorMessage, nfe, messageContext);
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
        } catch (RuntimeException re) {
            errorMessage = Constants.ErrorConstants.GENERIC_ERROR_MSG;
            log.error(errorMessage, re);
            storeErrorResponseStatus(messageContext, errorMessage,
                    Constants.ErrorConstants.ERROR_CODE_RUNTIME_EXCEPTION);
            handleException(errorMessage, re, messageContext);
        }
    }
    
    /**
     * Gets the transaction request from messageContext.
     * 
     * @return the transaction request
     * @throws JSONException the jSON exception
     */
    private TransactionRequest getTransactionRequest() throws JSONException {
    
        String orderId = (String) msgContext.getProperty(Constants.ORDER_ID);
        String merchantAccountId = (String) msgContext.getProperty(Constants.MERCHANT_ACCOUNT_ID);
        String customFieldRequest = (String) msgContext.getProperty(Constants.CUSTOM_FIELDS);
        
        // instantiating a transactionRequest
        final TransactionRequest request = new TransactionRequest();
        request.amount(new BigDecimal((String) msgContext.getProperty(Constants.AMOUNT)));
        request.customerId((String) msgContext.getProperty(Constants.CUSTOMER_ID));
        request.paymentMethodToken((String) msgContext.getProperty(Constants.PAYMENT_METHOD_TOKEN));
        request.recurring(Boolean.valueOf((String) msgContext.getProperty(Constants.RECURRING)));
        request.channel((String) msgContext.getProperty(Constants.CHANNEL));
        
        if (orderId != null && !orderId.isEmpty()) {
            request.orderId(orderId);
        }
        if (merchantAccountId != null && !merchantAccountId.isEmpty()) {
            request.merchantAccountId(merchantAccountId);
        }
        
        addCreditCardRequest(addCustomerRequest(addBillingTransactionAddress
                (addShippingTransactionAddress(addTransactionOptions(request)))));
        
        if (customFieldRequest != null && !customFieldRequest.isEmpty()) {
            JSONArray jsonArray = new JSONArray(customFieldRequest);
            for (int count = 0; count < jsonArray.length(); count++) {
                JSONObject result = jsonArray.getJSONObject(count);
                for (String key : JSONObject.getNames(result)) {
                    request.customField(key, result.getString(key));
                }
            }
        }
        
        return request;
    }
    
    /**
     * add credit card request details for transaction.
     * 
     * @param request the request
     * @return the transaction request
     * @throws JSONException the jSON exception
     */
    private TransactionRequest addCreditCardRequest(final TransactionRequest request) throws JSONException {
    
        String creditCard = (String) msgContext.getProperty(Constants.CREDIT_CARD);
        final TransactionCreditCardRequest cardRequest = request.creditCard();
        if (creditCard != null && !creditCard.isEmpty()) {
            final JSONObject creditCardObj = new JSONObject(creditCard);
            
            String value;
            if (creditCardObj.has(Constants.JSONKeys.TOKEN)
                    && !(value = creditCardObj.getString(Constants.JSONKeys.TOKEN)).isEmpty()) {
                cardRequest.token(value);
            }
            if (creditCardObj.has(Constants.JSONKeys.CREDIT_CARD_NO)
                    && !(value = creditCardObj.getString(Constants.JSONKeys.CREDIT_CARD_NO)).isEmpty()) {
                cardRequest.number(value);
            }
            if (creditCardObj.has(Constants.JSONKeys.CREDIT_CARD_EXPIRATION_DATE)
                    && !(value = creditCardObj.getString(Constants.JSONKeys.CREDIT_CARD_EXPIRATION_DATE)).isEmpty()) {
                cardRequest.expirationDate(value);
            }
            if (creditCardObj.has(Constants.JSONKeys.CREDIT_CARD_EXPIRATION_MONTH)
                    && !(value = creditCardObj.getString(Constants.JSONKeys.CREDIT_CARD_EXPIRATION_MONTH)).isEmpty()) {
                cardRequest.expirationMonth(value);
            }
            if (creditCardObj.has(Constants.JSONKeys.CREDIT_CARD_EXPIRATION_YEAR)
                    && !(value = creditCardObj.getString(Constants.JSONKeys.CREDIT_CARD_EXPIRATION_YEAR)).isEmpty()) {
                cardRequest.expirationYear(value);
            }
            if (creditCardObj.has(Constants.JSONKeys.CARD_HOLDER_NAME)
                    && !(value = creditCardObj.getString(Constants.JSONKeys.CARD_HOLDER_NAME)).isEmpty()) {
                cardRequest.cardholderName(value);
            }
            if (creditCardObj.has(Constants.JSONKeys.CVV)
                    && !(value = creditCardObj.getString(Constants.JSONKeys.CVV)).isEmpty()) {
                cardRequest.cvv(value);
            }
        }
        cardRequest.done();
        return request;
    }
    
    /**
     * add customer request details for transaction.
     * 
     * @param request the request
     * @return the transaction request
     * @throws JSONException the jSON exception
     */
    private TransactionRequest addCustomerRequest(final TransactionRequest request) throws JSONException {
    
        String customer = (String) msgContext.getProperty(Constants.CUSTOMER_DETAILS);
        final CustomerRequest customerRequest = request.customer();
        if (customer != null && !customer.isEmpty()) {
            final JSONObject customerObj = new JSONObject(customer);
            if (customerObj.has(Constants.JSONKeys.ID)) {
                customerRequest.id(customerObj.getString(Constants.JSONKeys.ID));
            }
            if (customerObj.has(Constants.JSONKeys.FIRST_NAME)) {
                customerRequest.firstName(customerObj.getString(Constants.JSONKeys.FIRST_NAME));
            }
            if (customerObj.has(Constants.JSONKeys.LAST_NAME)) {
                customerRequest.lastName(customerObj.getString(Constants.JSONKeys.LAST_NAME));
            }
            if (customerObj.has(Constants.JSONKeys.COMPANY)) {
                customerRequest.company(customerObj.getString(Constants.JSONKeys.COMPANY));
            }
            if (customerObj.has(Constants.JSONKeys.PHONE)) {
                customerRequest.phone(customerObj.getString(Constants.JSONKeys.PHONE));
            }
            if (customerObj.has(Constants.JSONKeys.FAX)) {
                customerRequest.fax(customerObj.getString(Constants.JSONKeys.FAX));
            }
            if (customerObj.has(Constants.JSONKeys.WEB_SITE)) {
                customerRequest.website(customerObj.getString(Constants.JSONKeys.WEB_SITE));
            }
            if (customerObj.has(Constants.JSONKeys.EMAIL)) {
                customerRequest.email(customerObj.getString(Constants.JSONKeys.EMAIL));
            }
        }
        customerRequest.done();
        return request;
    }
    
    /**
     * add billing address request details for transaction.
     * 
     * @param request the request
     * @return the transaction request
     * @throws JSONException the jSON exception
     */
    private TransactionRequest addBillingTransactionAddress(final TransactionRequest request) throws JSONException {
    
        String billingAddress = (String) msgContext.getProperty(Constants.BILLING_ADDRESS);
        final TransactionAddressRequest addressRequest = request.billingAddress();
        if (billingAddress != null && !billingAddress.isEmpty()) {
            
            final JSONObject billingAddressObj = new JSONObject(billingAddress);
            if (billingAddressObj.has(Constants.JSONKeys.COUNTRY_NAME)) {
                addressRequest.countryName(billingAddressObj.getString(Constants.JSONKeys.COUNTRY_NAME));
            }
            if (billingAddressObj.has(Constants.JSONKeys.FIRST_NAME)) {
                addressRequest.firstName(billingAddressObj.getString(Constants.JSONKeys.FIRST_NAME));
            }
            if (billingAddressObj.has(Constants.JSONKeys.LAST_NAME)) {
                addressRequest.lastName(billingAddressObj.getString(Constants.JSONKeys.FIRST_NAME));
            }
            if (billingAddressObj.has(Constants.JSONKeys.COMPANY)) {
                addressRequest.company(billingAddressObj.getString(Constants.JSONKeys.COMPANY));
            }
            if (billingAddressObj.has(Constants.JSONKeys.STREET_ADDRESS)) {
                addressRequest.streetAddress(billingAddressObj.getString(Constants.JSONKeys.STREET_ADDRESS));
            }
            if (billingAddressObj.has(Constants.JSONKeys.EXTENDED_ADDRESS)) {
                addressRequest.extendedAddress(billingAddressObj.getString(Constants.JSONKeys.EXTENDED_ADDRESS));
            }
            if (billingAddressObj.has(Constants.JSONKeys.LOCALITY)) {
                addressRequest.locality(billingAddressObj.getString(Constants.JSONKeys.LOCALITY));
            }
            if (billingAddressObj.has(Constants.JSONKeys.REGION)) {
                addressRequest.region(billingAddressObj.getString(Constants.JSONKeys.REGION));
            }
            if (billingAddressObj.has(Constants.JSONKeys.POSTAL_CODE)) {
                addressRequest.postalCode(billingAddressObj.getString(Constants.JSONKeys.POSTAL_CODE));
            }
            if (billingAddressObj.has(Constants.JSONKeys.COUNTRY_CODE_ALPHA2)) {
                addressRequest.countryCodeAlpha2(billingAddressObj.getString(Constants.JSONKeys.COUNTRY_CODE_ALPHA2));
            }
            if (billingAddressObj.has(Constants.JSONKeys.COUNTRY_CODE_ALPHA3)) {
                addressRequest.countryCodeAlpha3(billingAddressObj.getString(Constants.JSONKeys.COUNTRY_CODE_ALPHA3));
            }
            if (billingAddressObj.has(Constants.JSONKeys.BILLING_COUNTRY_CODE_NUMERIC)) {
                addressRequest.countryCodeNumeric(billingAddressObj
                        .getString(Constants.JSONKeys.BILLING_COUNTRY_CODE_NUMERIC));
            }
        }
        addressRequest.done();
        return request;
    }
    
    /**
     * add shipping address request details for transaction.
     * 
     * @param request the request
     * @return the transaction request
     * @throws JSONException the jSON exception
     */
    private TransactionRequest addShippingTransactionAddress(final TransactionRequest request) throws JSONException {
    
        String shippingAddress = (String) msgContext.getProperty(Constants.SHIPPING_ADDRESS);
        final TransactionAddressRequest shippingRequest = request.shippingAddress();
        if (shippingAddress != null && !shippingAddress.isEmpty()) {
            
            final JSONObject shippingAddressObj = new JSONObject(shippingAddress);
            if (shippingAddressObj.has(Constants.JSONKeys.FIRST_NAME)) {
                shippingRequest.firstName(shippingAddressObj.getString(Constants.JSONKeys.FIRST_NAME));
            }
            if (shippingAddressObj.has(Constants.JSONKeys.LAST_NAME)) {
                shippingRequest.lastName(shippingAddressObj.getString(Constants.JSONKeys.LAST_NAME));
            }
            if (shippingAddressObj.has(Constants.JSONKeys.COMPANY)) {
                shippingRequest.company(shippingAddressObj.getString(Constants.JSONKeys.COMPANY));
            }
            if (shippingAddressObj.has(Constants.JSONKeys.STREET_ADDRESS)) {
                shippingRequest.streetAddress(shippingAddressObj.getString(Constants.JSONKeys.STREET_ADDRESS));
            }
            if (shippingAddressObj.has(Constants.JSONKeys.EXTENDED_ADDRESS)) {
                shippingRequest.extendedAddress(shippingAddressObj.getString(Constants.JSONKeys.EXTENDED_ADDRESS));
            }
            if (shippingAddressObj.has(Constants.JSONKeys.LOCALITY)) {
                shippingRequest.locality(shippingAddressObj.getString(Constants.JSONKeys.LOCALITY));
            }
            if (shippingAddressObj.has(Constants.JSONKeys.REGION)) {
                shippingRequest.region(shippingAddressObj.getString(Constants.JSONKeys.REGION));
            }
            if (shippingAddressObj.has(Constants.JSONKeys.POSTAL_CODE)) {
                shippingRequest.postalCode(shippingAddressObj.getString(Constants.JSONKeys.POSTAL_CODE));
            }
            if (shippingAddressObj.has(Constants.JSONKeys.COUNTRY_CODE_ALPHA2)) {
                shippingRequest.countryCodeAlpha2(shippingAddressObj.getString(Constants.JSONKeys.COUNTRY_CODE_ALPHA2));
            }
        }
        shippingRequest.done();
        return request;
    }
    
    /**
     * add selected option details for transaction.
     * 
     * @param request the request
     * @return the transaction request
     * @throws JSONException the jSON exception
     */
    private TransactionRequest addTransactionOptions(final TransactionRequest request) throws JSONException {
    
        String options = (String) msgContext.getProperty(Constants.OPTIONS);
        final TransactionOptionsRequest optionRequest = request.options();
        if (options != null && !options.isEmpty()) {
            final JSONObject optionsObj = new JSONObject(options);
            
            if (optionsObj.has(Constants.JSONKeys.SUBMIT_FOR_SETTLEMENT)) {
                optionRequest.submitForSettlement(Boolean.valueOf(optionsObj
                        .getString(Constants.JSONKeys.SUBMIT_FOR_SETTLEMENT)));
            }
            if (optionsObj.has(Constants.JSONKeys.STORE_IN_VAULT)) {
                optionRequest.storeInVault(Boolean.valueOf(optionsObj.getString(Constants.JSONKeys.STORE_IN_VAULT)));
            }
            if (optionsObj.has(Constants.JSONKeys.ADD_BILLING_ADDRESS_TO_PAYMENT)) {
                optionRequest.addBillingAddressToPaymentMethod(Boolean.valueOf(optionsObj
                        .getString(Constants.JSONKeys.ADD_BILLING_ADDRESS_TO_PAYMENT)));
            }
            if (optionsObj.has(Constants.JSONKeys.STORE_SHIPPING_ADDRESS_IN_VAULT)) {
                optionRequest.storeShippingAddressInVault(Boolean.valueOf(optionsObj
                        .getString(Constants.JSONKeys.STORE_SHIPPING_ADDRESS_IN_VAULT)));
            }
        }
        optionRequest.done();
        return request;
    }
    
    /**
     * Creates the transaction.
     * 
     * @param braintreeGateway the braintree gateway
     * @param request the request
     * @return the result
     * @throws BraintreeException the braintree service exception
     */
    private Result<Transaction> createTransaction(final BraintreeGateway braintreeGateway,
            final TransactionRequest request) throws BraintreeException {
    
        final Result<Transaction> result = braintreeGateway.transaction().sale(request);
        
        if (result != null && !result.isSuccess()) {
            final Transaction transaction = result.getTransaction();
            if (transaction != null) {
                throw new BraintreeException(transaction.getProcessorResponseText());
            } else {
                throw new BraintreeException(result.getMessage());
            }
        }
        return result;
    }
    
}
