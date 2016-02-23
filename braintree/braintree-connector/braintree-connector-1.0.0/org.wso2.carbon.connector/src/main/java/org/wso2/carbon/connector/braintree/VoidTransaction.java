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

import org.apache.synapse.MessageContext;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.ValidationError;
import com.braintreegateway.exceptions.AuthenticationException;
import com.braintreegateway.exceptions.BraintreeException;
import com.braintreegateway.exceptions.NotFoundException;
import com.google.gson.Gson;

/**
 * The Class BraintreeVoidTransaction.
 * 
 * @see https://www.braintreepayments.com/docs/java/transactions/void
 */
public final class VoidTransaction extends AbstractBrainTreeConnector {
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param messageContext Synapse Message Context.
     * @see org.wso2.carbon.connector.core.AbstractConnector#connect(org.apache.synapse.MessageContext)
     */
    public void connect(final MessageContext messageContext) {
    
        String errorMessage = null;
        try {
            // instantiating and authenticating a braintreeGateway
            BraintreeGateway braintreeGateway = getBrainTreeService(messageContext);
            // remove the request from the payload
            messageContext.getEnvelope().getBody().getFirstElement().detach();
            Result<Transaction> result =
                    braintreeGateway.transaction().voidTransaction(
                            (String) messageContext.getProperty(Constants.TRANSACTIONID));
            if (result.isSuccess()) {
                messageContext.setProperty(Constants.RESULT, new Gson().toJson(result.getTarget()));
            } else {
                for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
                    throw new BraintreeException(error.getMessage());
                }
            }
        } catch (NotFoundException re) {
            errorMessage = Constants.ErrorConstants.INVALID_RESOURCE_MSG;
            log.error(errorMessage, re);
            storeErrorResponseStatus(messageContext, errorMessage,
                    Constants.ErrorConstants.ERROR_CODE_NOT_FOUND_EXCEPTION);
            handleException(errorMessage, re, messageContext);
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
}
