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

import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.braintree.Constants.ErrorConstants;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.exceptions.AuthenticationException;
import com.braintreegateway.exceptions.AuthorizationException;
import com.braintreegateway.exceptions.BraintreeException;
import com.braintreegateway.exceptions.NotFoundException;
import com.google.gson.Gson;

/**
 * BraintreeGetTransaction - Refund Transaction.
 * 
 * @see https://www.braintreepayments.com/docs/java/transactions/refund
 */
public final class RefundTransaction extends AbstractBrainTreeConnector {
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param messageContext Synapse Message Context.
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public void connect(final MessageContext messageContext) {
    
        final String transactionId = (String) messageContext.getProperty(Constants.TRANSACTIONID);
        final String amountString = (String) messageContext.getProperty(Constants.AMOUNT);
        BigDecimal amount = null;
        String errorMessage = null;
        
        // Get the Refund amount for partial refund. Evaluates to false for full
        // refunds
        if (amountString != null && !amountString.isEmpty()) {
            amount = new BigDecimal(Float.parseFloat(amountString));
        }
        
        try {
            
            // instantiating and authenticating a braintreeGateway
            final BraintreeGateway braintreeGateway =
                    new BraintreeGateway(Environment.SANDBOX,
                            (String) messageContext.getProperty(Constants.MERCHANT_ID),
                            (String) messageContext.getProperty(Constants.PUBLIC_KEY),
                            (String) messageContext.getProperty(Constants.PRIVATE_KEY));
            
            messageContext.getEnvelope().getBody().getFirstElement().detach();
            
            // Creating a transaction & convert to JSON format and set to
            // messageContext
            messageContext.setProperty(Constants.RESULT,
                    new Gson().toJson(refundTransaction(braintreeGateway, transactionId, amount)));
            
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
        } catch (RuntimeException re) {
            errorMessage = ErrorConstants.GENERIC_ERROR_MSG;
            log.error(errorMessage, re);
            storeErrorResponseStatus(messageContext, errorMessage, ErrorConstants.ERROR_CODE_RUNTIME_EXCEPTION);
            handleException(errorMessage, re, messageContext);
        }
    }
    
    /**
     * Refunds the transaction.
     *
     * @param braintreeGateway Authentication Gateway Token
     * @param transactionId the transaction Id to refund
     * @param amount to refund
     * @return the result
     * @throws BraintreeException the Braintree Exception
     */
    private Result<Transaction> refundTransaction(final BraintreeGateway braintreeGateway, final String transactionId,
            final BigDecimal amount) throws BraintreeException {
    
        Result<Transaction> result;
        if (amount == null) {
            result = braintreeGateway.transaction().refund(transactionId);
        } else {
            result = braintreeGateway.transaction().refund(transactionId, amount);
        }
        return result;
    }
    
}
