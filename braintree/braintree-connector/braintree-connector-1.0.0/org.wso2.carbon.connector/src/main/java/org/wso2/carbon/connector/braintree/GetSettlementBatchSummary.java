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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.apache.synapse.MessageContext;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.SettlementBatchSummary;
import com.braintreegateway.exceptions.AuthenticationException;
import com.braintreegateway.exceptions.BraintreeException;
import com.google.gson.Gson;

/**
 * BraintreeGetSettlementBatchSummary - Settlement Batch Summary gives with a summary of the transactions that
 * have settled on a given day aggregated by card type. This information can be optionally be aggregated by a
 * custom field.
 * 
 * @see https://www.braintreepayments.com/docs/java/settlement_batch_summaries/generate
 */
public final class GetSettlementBatchSummary extends AbstractBrainTreeConnector {
    
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
            
            /*
             * get summary of the transactions and convert the response to JSON format and set to
             * messageContext.
             */
            msgContext.setProperty(Constants.RESULT, new Gson().toJson(getSettlementBatchSummary(braintreeGateway)));
            
        } catch (ParseException pe) {
            errorMessage = Constants.ErrorConstants.PARSER_EXCEPTION_MSG;
            log.error(errorMessage, pe);
            storeErrorResponseStatus(messageContext, errorMessage, 
                    Constants.ErrorConstants.ERROR_CODE_PARSER_EXCEPTION);
            handleException(errorMessage, pe, messageContext);
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
     * This method presents a summary of the transactions that have settled on a given day or custom field
     * aggregated by card type.
     * 
     * @param braintreeGateway The braintree gateway.
     * @return The settlement batch summary as a list of maps.
     * @throws ParseException If the beginning of the specified string cannot be parsed.
     */
    private Result<SettlementBatchSummary> getSettlementBatchSummary(final BraintreeGateway braintreeGateway)
            throws ParseException {
    
        final String summaryDate = (String) msgContext.getProperty(Constants.DATE);
        Result<SettlementBatchSummary> result = null;
        
        if (summaryDate != null && !summaryDate.isEmpty()) {
            
            final Calendar calendar = Calendar.getInstance();
            final SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat(Constants.DATE_FORMATTER, Locale.getDefault());
            calendar.setTime(simpleDateFormat.parse(summaryDate));
            
            final String summaryCustomField = (String) msgContext.getProperty(Constants.CUSTOM_FIELD);
            if (summaryCustomField != null && !summaryCustomField.isEmpty()) {
                result = braintreeGateway.settlementBatchSummary().generate(calendar, summaryCustomField);
            } else {
                result = braintreeGateway.settlementBatchSummary().generate(calendar);
            }
            
        }
        
        return result;
    }
}
