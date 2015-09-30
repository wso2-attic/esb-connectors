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

import java.util.Locale;

import org.apache.synapse.MessageContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.carbon.connector.braintree.Constants.ErrorConstants;
import org.wso2.carbon.connector.braintree.Constants.JSONKeys;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.BusinessAddressRequest;
import com.braintreegateway.BusinessRequest;
import com.braintreegateway.FundingRequest;
import com.braintreegateway.IndividualAddressRequest;
import com.braintreegateway.IndividualRequest;
import com.braintreegateway.MerchantAccount;
import com.braintreegateway.MerchantAccount.FundingDestination;
import com.braintreegateway.MerchantAccountRequest;
import com.braintreegateway.Result;
import com.braintreegateway.exceptions.AuthenticationException;
import com.braintreegateway.exceptions.AuthorizationException;
import com.braintreegateway.exceptions.BraintreeException;
import com.braintreegateway.exceptions.NotFoundException;
import com.google.gson.Gson;

/**
 * BraintreeGetTransaction - Create Merchant Account.
 * 
 * @see https://www.braintreepayments.com/docs/java/merchant_accounts/create
 */
public final class CreateMerchantAccount extends AbstractBrainTreeConnector {
    
    /**
     * Instance variable to hold the MessageContext object passed in via the Synapse template.
     */
    private MessageContext messageContext;
    
    /**
     * Connector method which is executed at the specified point within the corresponding Synapse template
     * within the connector.
     * 
     * @param msgContext Synapse Message Context
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
            
            // creating a merchant account & convert to JSON format and set to
            // messageContext
            messageContext.setProperty(Constants.RESULT,
                    new Gson().toJson(createMerchantAccount(braintreeGateway, getMerchantAccountRequest())));
            
        } catch (AuthorizationException nfe) {
            errorMessage = ErrorConstants.INVALID_AUTHERIZATION_MSG;
            log.error(errorMessage, nfe);
            storeErrorResponseStatus(messageContext, errorMessage, ErrorConstants.ERROR_CODE_AUTHERIZATION_EXCEPTION);
            handleException(errorMessage, nfe, messageContext);
        } catch (NotFoundException re) {
            errorMessage = ErrorConstants.INVALID_RESOURCE_MSG;
            log.error(errorMessage, re);
            storeErrorResponseStatus(messageContext, errorMessage, ErrorConstants.ERROR_CODE_NOT_FOUND_EXCEPTION);
            handleException(errorMessage, re, messageContext);
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
        } catch (RuntimeException re) {
            errorMessage = ErrorConstants.GENERIC_ERROR_MSG;
            log.error(errorMessage, re);
            storeErrorResponseStatus(messageContext, errorMessage, ErrorConstants.ERROR_CODE_RUNTIME_EXCEPTION);
            handleException(errorMessage, re, messageContext);
        }
    }
    
    /**
     * Creates the Merchant Account create request.
     * 
     * @return the MerchantAccountRequest
     * @throws JSONException the json exception
     */
    private MerchantAccountRequest getMerchantAccountRequest() throws JSONException {
    
        // Instantiating a MerchantAccountRequest.
        final MerchantAccountRequest request = new MerchantAccountRequest();
        
        final String masterMerchanAccountId = (String) messageContext.getProperty(Constants.MASTER_MERCHANT_ACCOUNT_ID);
        if (masterMerchanAccountId != null && !masterMerchanAccountId.isEmpty()) {
            request.masterMerchantAccountId(masterMerchanAccountId);
        }
        
        final String merchanAccountId = (String) messageContext.getProperty(Constants.MERCHANT_ACCOUNT_ID);
        if (merchanAccountId != null && !merchanAccountId.isEmpty()) {
            request.id(merchanAccountId);
        }
        
        final String tosAccepted = (String) messageContext.getProperty(Constants.TOS_ACCEPTED);
        if (tosAccepted != null && !tosAccepted.isEmpty()) {
            request.tosAccepted(Boolean.parseBoolean(tosAccepted));
        }
        
        setFundingDetails(setBusinessDetails(setInvidualDetails(request)));
        
        return request;
    }
    
    /**
     * Sets the funding details.
     * 
     * @param request the request
     * @return the MerchantAccountRequest
     * @throws JSONException the jSON exception
     */
    private MerchantAccountRequest setFundingDetails(final MerchantAccountRequest request) throws JSONException {
    
        // Common String Object Reference Variable.
        String value;
        
        // Extracting and setting Funding Details.
        String fundingDetails = (String) messageContext.getProperty(Constants.FUNDING);
        if (fundingDetails != null && !fundingDetails.isEmpty()) {
            final JSONObject fundingObject = new JSONObject(fundingDetails);
            
            FundingRequest funding = request.funding();
            
            if (fundingObject.has(JSONKeys.EMAIL) && !(value = fundingObject.getString(JSONKeys.EMAIL)).isEmpty()) {
                funding.email(value);
            }
            
            if (fundingObject.has(JSONKeys.MOBILE_PHONE)
                    && !(value = fundingObject.getString(JSONKeys.MOBILE_PHONE)).isEmpty()) {
                funding.mobilePhone(value);
            }
            
            if (fundingObject.has(JSONKeys.ACCOUNT_NUMBER)
                    && !(value = fundingObject.getString(JSONKeys.ACCOUNT_NUMBER)).isEmpty()) {
                funding.accountNumber(value);
            }
            
            if (fundingObject.has(JSONKeys.ROUTING_NUMBER)
                    && !(value = fundingObject.getString(JSONKeys.ROUTING_NUMBER)).isEmpty()) {
                funding.routingNumber(value);
            }
            
            if (fundingObject.has(JSONKeys.DESTINATION)
                    && !(value = fundingObject.getString(JSONKeys.DESTINATION)).isEmpty()) {
                funding.destination(FundingDestination.valueOf(value.toUpperCase(Locale.US)));
            }
            funding.done();
        }
        return request;
    }
    
    /**
     * Sets the invidual details.
     * 
     * @param request the request
     * @return the MerchantAccountRequest
     * @throws JSONException
     */
    private MerchantAccountRequest setInvidualDetails(final MerchantAccountRequest request) throws JSONException {
    
        // Common String Object Reference Variable.
        String value;
        
        // Extracting and setting Individual Details.
        String individualDetails = (String) messageContext.getProperty(Constants.INDIVIDUAL_DETAILS);
        if (individualDetails != null && !individualDetails.isEmpty()) {
            final JSONObject individualObject = new JSONObject(individualDetails);
            
            IndividualRequest individual = request.individual();
            
            if (individualObject.has(JSONKeys.FIRST_NAME)
                    && !(value = individualObject.getString(JSONKeys.FIRST_NAME)).isEmpty()) {
                individual.firstName(value);
            }
            
            if (individualObject.has(JSONKeys.LAST_NAME)
                    && !(value = individualObject.getString(JSONKeys.LAST_NAME)).isEmpty()) {
                individual.lastName(value);
            }
            
            if (individualObject.has(JSONKeys.EMAIL) &&
                    !(value = individualObject.getString(JSONKeys.EMAIL)).isEmpty()) {
                individual.email(value);
            }
            
            if (individualObject.has(JSONKeys.PHONE) &&
                    !(value = individualObject.getString(JSONKeys.PHONE)).isEmpty()) {
                individual.phone(value);
            }
            
            if (individualObject.has(JSONKeys.DATE_OF_BIRTH)
                    && !(value = individualObject.getString(JSONKeys.DATE_OF_BIRTH)).isEmpty()) {
                individual.dateOfBirth(value);
            }
            
            if (individualObject.has(JSONKeys.SSN) && !(value = individualObject.getString(JSONKeys.SSN)).isEmpty()) {
                individual.ssn(value);
            }
            
            // Extracting and setting Individual Address Details.
            final JSONObject individualAddressObject = individualObject.getJSONObject(JSONKeys.ADDRESS);
            IndividualAddressRequest individualAddress = individual.address();
            
            if (individualAddressObject.has(JSONKeys.STREET_ADDRESS)
                    && !(value = individualAddressObject.getString(JSONKeys.STREET_ADDRESS)).isEmpty()) {
                individualAddress.streetAddress(value);
            }
            
            if (individualAddressObject.has(JSONKeys.LOCALITY)
                    && !(value = individualAddressObject.getString(JSONKeys.LOCALITY)).isEmpty()) {
                individualAddress.locality(value);
            }
            
            if (individualAddressObject.has(JSONKeys.REGION)
                    && !(value = individualAddressObject.getString(JSONKeys.REGION)).isEmpty()) {
                individualAddress.region(value);
            }
            
            if (individualAddressObject.has(JSONKeys.POSTAL_CODE)
                    && !(value = individualAddressObject.getString(JSONKeys.POSTAL_CODE)).isEmpty()) {
                individualAddress.postalCode(value);
            }
            
            individualAddress.done();
            individual.done();
            
        }
        return request;
    }
    
    /**
     * Sets the business details.
     * 
     * @param request the request
     * @return the MerchantAccountRequest
     * @throws JSONException the jSON exception
     */
    private MerchantAccountRequest setBusinessDetails(final MerchantAccountRequest request) throws JSONException {
    
        // Common String Object Reference Variable.
        String value;
        
        // Extracting and setting Business Details.
        String businessDetails = (String) messageContext.getProperty(Constants.BUSINESS_DETAILS);
        if (businessDetails != null && !businessDetails.isEmpty()) {
            final JSONObject businessObject = new JSONObject(businessDetails);
            
            BusinessRequest business = request.business();
            
            if (businessObject.has(JSONKeys.LEGAL_NAME)
                    && !(value = businessObject.getString(JSONKeys.LEGAL_NAME)).isEmpty()) {
                business.legalName(value);
            }
            
            if (businessObject.has(JSONKeys.DBA_NAME)
                    && !(value = businessObject.getString(JSONKeys.DBA_NAME)).isEmpty()) {
                business.dbaName(value);
            }
            
            if (businessObject.has(JSONKeys.TAX_ID) &&
                    !(value = businessObject.getString(JSONKeys.TAX_ID)).isEmpty()) {
                business.taxId(value);
            }
            
            // Extracting and setting Business Address Details.
            JSONObject businessAddressObject = businessObject.getJSONObject(JSONKeys.ADDRESS);
            BusinessAddressRequest businessAddress = business.address();
            
            if (businessAddressObject.has(JSONKeys.STREET_ADDRESS)
                    && !(value = businessAddressObject.getString(JSONKeys.STREET_ADDRESS)).isEmpty()) {
                businessAddress.streetAddress(value);
            }
            
            if (businessAddressObject.has(JSONKeys.LOCALITY)
                    && !(value = businessAddressObject.getString(JSONKeys.LOCALITY)).isEmpty()) {
                businessAddress.locality(value);
            }
            
            if (businessAddressObject.has(JSONKeys.REGION)
                    && !(value = businessAddressObject.getString(JSONKeys.REGION)).isEmpty()) {
                businessAddress.region(value);
            }
            
            if (businessAddressObject.has(JSONKeys.POSTAL_CODE)
                    && !(value = businessAddressObject.getString(JSONKeys.POSTAL_CODE)).isEmpty()) {
                businessAddress.postalCode(value);
            }
            
            businessAddress.done();
            business.done();
            
        }
        
        return request;
    }
    
    /**
     * Creates the merchant account.
     * 
     * @param braintreeGateway Authenticated braintree gateway instance
     * @param request Created MerchantAccountRequest
     * @return Result<MerchantAccount>
     * @throws BraintreeServiceException the braintree service exception
     */
    private Result<MerchantAccount> createMerchantAccount(final BraintreeGateway braintreeGateway,
            final MerchantAccountRequest request) throws BraintreeException {
    
        return braintreeGateway.merchantAccount().create(request);
    }
    
}
