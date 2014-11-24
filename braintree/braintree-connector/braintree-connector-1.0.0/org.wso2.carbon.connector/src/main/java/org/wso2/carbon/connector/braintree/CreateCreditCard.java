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

import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.braintree.Constants.ErrorConstants;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CreditCardAddressRequest;
import com.braintreegateway.CreditCardOptionsRequest;
import com.braintreegateway.CreditCardRequest;
import com.braintreegateway.exceptions.AuthenticationException;
import com.braintreegateway.exceptions.AuthorizationException;
import com.braintreegateway.exceptions.BraintreeException;
import com.braintreegateway.exceptions.NotFoundException;
import com.google.gson.Gson;

/**
 * BraintreeCreateCreditCard - creates a credit card.
 * 
 * @see https://www.braintreepayments.com/docs/java/credit_cards/create.
 */
public final class CreateCreditCard extends AbstractBrainTreeConnector {

    /**
     * The message context.
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
        String errorMessage;

        try {

            // instantiating and authenticating a braintreeGateway
            final BraintreeGateway braintreeGateway = getBrainTreeService(messageContext);

            // remove the request from the payload
            messageContext.getEnvelope().getBody().getFirstElement().detach();

            // creating a credit card & convert to JSON format and set to
            // messageContext
            messageContext.setProperty(Constants.RESULT,
                    new Gson().toJson(braintreeGateway.creditCard().create(getCreditCardRequest())));

        } catch (NotFoundException nfe) {
            errorMessage = Constants.ErrorConstants.INVALID_RESOURCE_MSG;
            log.error(errorMessage, nfe);
            storeErrorResponseStatus(messageContext, errorMessage, ErrorConstants.ERROR_CODE_NOT_FOUND_EXCEPTION);
            handleException(errorMessage, nfe, messageContext);
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
     * Gets the creditCard request from messageContext.
     * 
     * @return the creditCard request
     */
    private CreditCardRequest getCreditCardRequest() {

        // instantiating a CreditCardRequest
        final CreditCardRequest request = new CreditCardRequest().customerId(
                (String) messageContext.getProperty(Constants.CUSTOMER_ID)).number(
                (String) messageContext.getProperty(Constants.CREDIT_CARD_NO));

        // Check and Add Credit card cardholderName
        String cardholderName = (String) messageContext.getProperty(Constants.CARD_HOLDER_NAME);
        if (cardholderName != null && !cardholderName.isEmpty()) {
            request.cardholderName(cardholderName);
        }

        // Check and Add Credit card expirationDate
        String expirationDate = (String) messageContext.getProperty(Constants.CREDIT_CARD_EXPIRATION_DATE);
        if (expirationDate != null && !expirationDate.isEmpty()) {
            request.expirationDate(expirationDate);
        }

        // Check and Add Credit card expirationMonth
        String expirationMonth = (String) messageContext.getProperty(Constants.CREDIT_CARD_EXPIRATION_MONTH);
        if (expirationMonth != null && !expirationMonth.isEmpty()) {
            request.expirationMonth(expirationMonth);
        }

        // Check and Add Credit card expirationYear
        String expirationYear = (String) messageContext.getProperty(Constants.CREDIT_CARD_EXPIRATION_YEAR);
        if (expirationYear != null && !expirationYear.isEmpty()) {
            request.expirationYear(expirationYear);
        }

        // Check and Add Credit card token
        String token = (String) messageContext.getProperty(Constants.TOKEN);
        if (token != null && !token.isEmpty()) {
            request.token(token);
        }

        // Add credit card billing info
        addBillingAddressDetails(request);

        final CreditCardOptionsRequest options = request.options();

        String makeDefault = (String) messageContext.getProperty(Constants.MAKE_DEFAULT);
        if (makeDefault != null && !makeDefault.isEmpty()) {
            options.makeDefault(Boolean.valueOf(makeDefault));
        }

        String failOnDuplicate = (String) messageContext.getProperty(Constants.FAIL_ON_DUPLICATE_PAYMENT_METHOD);
        if (failOnDuplicate != null && !failOnDuplicate.isEmpty()) {
            options.failOnDuplicatePaymentMethod(Boolean.valueOf(failOnDuplicate)).done();
        }
        options.done();

        return request;
    }

    /**
     * Gets the creditCard address request from messageContext.
     * 
     * @param request
     *            the request
     */
    private void addBillingAddressDetails(final CreditCardRequest request) {

        // if the user sends the billingAddressId, an existing billing address
        // will be added
        final String billingAddress = (String) messageContext.getProperty(Constants.BILLING_ADDRESS_ID);

        final CreditCardAddressRequest address = request.billingAddress();
        final String firstName = (String) messageContext.getProperty(Constants.BILLING_FIRST_NAME);
        final String lastName = (String) messageContext.getProperty(Constants.BILLING_LAST_NAME);
        final String billingCompany = (String) messageContext.getProperty(Constants.BILLING_COMPANY);
        final String streetAddress = (String) messageContext.getProperty(Constants.BILLING_STREET_ADDRESS);
        final String extAddress = (String) messageContext.getProperty(Constants.BILLING_EXTENDED_ADDRESS);
        final String locality = (String) messageContext.getProperty(Constants.BILLING_LOCALITY);
        final String region = (String) messageContext.getProperty(Constants.BILLING_REGION);
        final String postalCode = (String) messageContext.getProperty(Constants.BILLING_POSTAL_CODE);
        final String ccAlpha = (String) messageContext.getProperty(Constants.BILLING_COUNTRY_CODE_ALPHA2);

        // If the user sends the following billingAddress fields, then a new
        // billing address will be
        // created and added.
        if (billingAddress != null && !billingAddress.isEmpty()) {
            request.billingAddressId(billingAddress);
        }

        if (firstName != null && !firstName.isEmpty()) {
            address.firstName(firstName);
        }

        if (lastName != null && !lastName.isEmpty()) {
            address.lastName(lastName);
        }

        if (billingCompany != null && !billingCompany.isEmpty()) {
            address.company(billingCompany);
        }

        if (streetAddress != null && !streetAddress.isEmpty()) {
            address.streetAddress(streetAddress);
        }

        if (extAddress != null && !extAddress.isEmpty()) {
            address.extendedAddress(extAddress);
        }

        if (locality != null && !locality.isEmpty()) {
            address.locality(locality);
        }

        if (region != null && !region.isEmpty()) {
            address.region(region);
        }

        if (postalCode != null && !postalCode.isEmpty()) {
            address.postalCode(postalCode);
        }

        if (ccAlpha != null && !ccAlpha.isEmpty()) {
            address.countryCodeAlpha2(ccAlpha);
        }
        address.done();
    }

}
