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

import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.braintree.Constants.ErrorConstants;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CreditCard;
import com.braintreegateway.CreditCardAddressRequest;
import com.braintreegateway.CreditCardRequest;
import com.braintreegateway.Result;
import com.braintreegateway.exceptions.AuthenticationException;
import com.braintreegateway.exceptions.BraintreeException;
import com.braintreegateway.exceptions.NotFoundException;
import com.google.gson.Gson;

/**
 * BraintreeGetTransaction - Update CreditCard.
 * 
 * @see https://www.braintreepayments.com/docs/java/credit_cards/update
 */
public final class UpdateCreditCard extends AbstractBrainTreeConnector {

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
            BraintreeGateway braintreeGateway = getBrainTreeService(messageContext);

            // remove the request from the payload
            messageContext.getEnvelope().getBody().getFirstElement().detach();

            // creating a transaction & convert to JSON format and set to
            // messageContext
            messageContext.setProperty("result",
                    new Gson().toJson(updateCreditCard(braintreeGateway, getCreditCardUpdateRequest())));

        } catch (NotFoundException nfe) {
            errorMessage = ErrorConstants.INVALID_RESOURCE_MSG;
            log.error(errorMessage, nfe);
            storeErrorResponseStatus(messageContext, errorMessage, ErrorConstants.ERROR_CODE_NOT_FOUND_EXCEPTION);
            handleException(errorMessage, nfe, messageContext);
        } catch (AuthenticationException au) {
            errorMessage = ErrorConstants.INVALID_AUTHENTICATION_MSG;
            log.error(errorMessage, au);
            storeErrorResponseStatus(messageContext, errorMessage, ErrorConstants.ERROR_CODE_AUTHENTICATION_EXCEPTION);
            handleException(errorMessage, au, messageContext);
        } catch (NumberFormatException nfe) {
            errorMessage = ErrorConstants.INVALID_NUMBER_FORMAT_MSG;
            log.error(errorMessage, nfe);
            storeErrorResponseStatus(messageContext, errorMessage, ErrorConstants.ERROR_CODE_NUMBER_FORMAT_EXCEPTION);
            handleException(errorMessage, nfe, messageContext);
        } catch (BraintreeException be) {
            errorMessage = be.getMessage();
            log.error(errorMessage, be);
            storeErrorResponseStatus(messageContext, errorMessage, ErrorConstants.ERROR_CODE_SERVICE_EXCEPTION);
            handleException(errorMessage, be, messageContext);
        } catch (RuntimeException re) {
            errorMessage = ErrorConstants.GENERIC_ERROR_MSG;
            log.error(errorMessage, re);
            storeErrorResponseStatus(messageContext, errorMessage, ErrorConstants.ERROR_CODE_RUNTIME_EXCEPTION);
            handleException(errorMessage, re, messageContext);
        }
    }

    /**
     * Gets the transaction request from messageContext.
     * 
     * @return CreditCardRequest
     */
    private CreditCardRequest getCreditCardUpdateRequest() {

        // instantiating a updateRequest
        CreditCardRequest updateRequest = new CreditCardRequest().customerId((String) messageContext
                .getProperty(Constants.CUSTOMER_ID));

        // Check and add creditCardNo.
        String creditCardNo = (String) messageContext.getProperty(Constants.CREDIT_CARD_NO);
        if (creditCardNo != null && !creditCardNo.isEmpty()) {
            updateRequest.number(creditCardNo);
        }

        // Check and add expirationDate.
        String expirationDate = (String) messageContext.getProperty(Constants.CREDIT_CARD_EXPIRATION_DATE);
        if (expirationDate != null && !expirationDate.isEmpty()) {
            updateRequest.expirationDate(expirationDate);
        }

        // Check and add expirationMonth.
        String expirationMonth = (String) messageContext.getProperty(Constants.CREDIT_CARD_EXPIRATION_MONTH);
        if (expirationMonth != null && !expirationMonth.isEmpty()) {
            updateRequest.expirationMonth(expirationMonth);
        }

        // Check and add expirationYear.
        String expirationYear = (String) messageContext.getProperty(Constants.CREDIT_CARD_EXPIRATION_YEAR);
        if (expirationYear != null && !expirationYear.isEmpty()) {
            updateRequest.expirationYear(expirationYear);
        }

        CreditCardAddressRequest addressRequest = updateRequest.billingAddress();
        // Check and add billingStreetAddress.
        String billingStreetAddress = (String) messageContext.getProperty(Constants.BILLING_STREET_ADDRESS);
        if (billingStreetAddress != null && !billingStreetAddress.isEmpty()) {
            addressRequest.streetAddress(billingStreetAddress);
        }

        // Check and add billingFirstName.
        String billingFirstName = (String) messageContext.getProperty(Constants.BILLING_FIRST_NAME);
        if (billingFirstName != null && !billingFirstName.isEmpty()) {
            addressRequest.firstName(billingFirstName);
        }

        // Check and add billingLastName.
        String billingLastName = (String) messageContext.getProperty(Constants.BILLING_LAST_NAME);
        if (billingLastName != null && !billingLastName.isEmpty()) {
            addressRequest.lastName(billingLastName);
        }

        // Check and add billingLocality.
        String billingLocality = (String) messageContext.getProperty(Constants.BILLING_LOCALITY);
        if (billingLocality != null && !billingLocality.isEmpty()) {
            addressRequest.locality(billingLocality);
        }

        // Check and add billingRegion.
        String billingRegion = (String) messageContext.getProperty(Constants.BILLING_REGION);
        if (billingRegion != null && !billingRegion.isEmpty()) {
            addressRequest.region(billingRegion);
        }

        // Check and add billingPostalCode.
        String billingPostalCode = (String) messageContext.getProperty(Constants.BILLING_POSTAL_CODE);
        if (billingPostalCode != null && !billingPostalCode.isEmpty()) {
            addressRequest.postalCode(billingPostalCode);
        }

        // Check and add options - makeDefault.
        String makeDefault = (String) messageContext.getProperty(Constants.MAKE_DEFAULT);
        if (makeDefault != null && !makeDefault.isEmpty()) {
            updateRequest.options().makeDefault(Boolean.valueOf(makeDefault)).done();
        }

        // Check and add options - updateExisting.
        String updateExisting = (String) messageContext.getProperty(Constants.UPDATE_EXISTING);
        if (updateExisting != null && !updateExisting.isEmpty()) {
            addressRequest.options().updateExisting(Boolean.valueOf(updateExisting)).done();
        }

        return updateRequest;
    }

    /**
     * Updates the Credit Card.
     * 
     * @param braintreeGateway
     *            the braintree gateway
     * @param updateRequest
     *            CreditCard update request
     * @return Result<CreditCard>
     * @throws BraintreeServiceException
     *             the braintree service exception
     */
    private Result<CreditCard> updateCreditCard(final BraintreeGateway braintreeGateway,
            final CreditCardRequest updateRequest) throws BraintreeException {

        String token = (String) messageContext.getProperty(Constants.TOKEN);
        return braintreeGateway.creditCard().update(token, updateRequest);
    }
}
