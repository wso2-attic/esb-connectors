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

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.exceptions.AuthenticationException;
import com.braintreegateway.exceptions.BraintreeException;
import com.braintreegateway.exceptions.NotFoundException;
import com.google.gson.Gson;

/**
 * BraintreeSubmitTransactionForSettlement - submit a transaction for settlement.
 * 
 * @see https://www.braintreepayments.com/docs/java/transactions/submit_for_settlement
 */
public final class SubmitTransactionForSettlement extends AbstractBrainTreeConnector {
    
    /**
     * Instance variable to hold the MessageContext object passed in via the Synapse template.
     */
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
            /*
             * submit a transaction for settlement and convert the response to JSON format and set to
             * messageContext.
             */
            msgContext.setProperty(Constants.RESULT,
                    new Gson().toJson(submitTransactionForSettlement(braintreeGateway)));
        } catch (NotFoundException re) {
            errorMessage = Constants.ErrorConstants.INVALID_RESOURCE_MSG;
            log.error(errorMessage, re);
            storeErrorResponseStatus(messageContext, errorMessage,
                    Constants.ErrorConstants.ERROR_CODE_NOT_FOUND_EXCEPTION);
            handleException(errorMessage, re, messageContext);
        } catch (NumberFormatException nfe) {
            errorMessage = Constants.ErrorConstants.INVALID_NUMBER_FORMAT_MSG;
            log.error(errorMessage, nfe);
            storeErrorResponseStatus(messageContext, errorMessage,
                    Constants.ErrorConstants.ERROR_CODE_NUMBER_FORMAT_EXCEPTION);
            handleException(errorMessage, nfe, messageContext);
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
     * This method submit transaction for settlement. Also possible to specify portion of the total
     * authorization amount to settle with provided transaction ID.
     *
     * @param braintreeGateway The braintree gateway.
     * @return The result of Transaction type.
     * @throws NotFoundException The not found exception.
     */
    private Result<Transaction> submitTransactionForSettlement(final BraintreeGateway braintreeGateway)
            throws NotFoundException {
    
        final String transactionId = (String) msgContext.getProperty(Constants.TRANSACTIONID);
        final String partialAmount = (String) msgContext.getProperty(Constants.PARTIAL_AMOUNT);
        Result<Transaction> result = null;
        
        if (partialAmount != null && !partialAmount.isEmpty()) {
            result = braintreeGateway.transaction().submitForSettlement(transactionId, new BigDecimal(partialAmount));
        } else {
            result = braintreeGateway.transaction().submitForSettlement(transactionId);
        }
        
        return result;
    }
}
