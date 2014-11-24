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
import org.json.JSONException;
import org.json.JSONObject;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.BusinessAddressRequest;
import com.braintreegateway.BusinessRequest;
import com.braintreegateway.FundingRequest;
import com.braintreegateway.IndividualAddressRequest;
import com.braintreegateway.IndividualRequest;
import com.braintreegateway.MerchantAccount.FundingDestination;
import com.braintreegateway.MerchantAccountRequest;
import com.braintreegateway.exceptions.AuthenticationException;
import com.braintreegateway.exceptions.AuthorizationException;
import com.braintreegateway.exceptions.BraintreeException;
import com.braintreegateway.exceptions.NotFoundException;
import com.google.gson.Gson;

/**
 * BraintreeUpdateMerchantAccount - Update merchant account.
 * 
 * @see https://www.braintreepayments.com/docs/java/merchant_accounts/update
 */
public final class UpdateMerchantAccount extends AbstractBrainTreeConnector {
    
    /**
     * Instance variable to hold the MessageContext object passed in via the Synapse template.
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
        String errorMessage;
        try {
            // instantiating and authenticating a braintreeGateway
            final BraintreeGateway braintreeGateway = getBrainTreeService(messageContext);
            
            // remove the request from the payload
            messageContext.getEnvelope().getBody().getFirstElement().detach();
            
            // update a merchant account & convert to JSON format and set to
            // messageContext
            messageContext.setProperty(
                    Constants.RESULT,
                    new Gson().toJson(braintreeGateway.merchantAccount().update(
                            (String) messageContext.getProperty(Constants.MERCHANT_ACCOUNT_ID),
                            getMerchantAccountRequest())));
            
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
        } catch (NotFoundException re) {
            errorMessage = Constants.ErrorConstants.INVALID_RESOURCE_MSG;
            log.error(errorMessage, re);
            storeErrorResponseStatus(messageContext, errorMessage,
                    Constants.ErrorConstants.ERROR_CODE_NOT_FOUND_EXCEPTION);
            handleException(errorMessage, re, messageContext);
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
     * Gets the merchant account request from messageContext.
     * 
     * @return the merchant account request
     * @throws JSONException the jSON exception
     */
    private MerchantAccountRequest getMerchantAccountRequest() throws JSONException {
    
        // instantiating a merchantAccountRequest
        final MerchantAccountRequest request = new MerchantAccountRequest();
        
        final String masterMerchantAccountId = (String) msgContext.getProperty(Constants.MASTER_MERCHANT_ACCOUNT_ID);
        
        // Check merchant account Id is available
        if (masterMerchantAccountId != null && !masterMerchantAccountId.isEmpty()) {
            request.masterMerchantAccountId(masterMerchantAccountId);
        }
        getIndividualRequest(getIndividualAddressRequest(getBussiness(getBussinessAddress(getFunding(request)))));
        return request;
    }
    
    /**
     * This request sets funding details for the Merchant Account request.
     * 
     * @param request the request
     * @return the funding
     * @throws JSONException the jSON exception
     */
    private MerchantAccountRequest getFunding(final MerchantAccountRequest request) throws JSONException {
    
        final FundingRequest funding = request.funding();
        final String fundingDetails = (String) msgContext.getProperty(Constants.FUNDING);
        
        // Check funding details are available
        if (fundingDetails != null && !fundingDetails.isEmpty()) {
            String value;
            final JSONObject fundingObj = new JSONObject(fundingDetails);
            
            // Check inserted destination with provided destination enumerator
            if (fundingObj.has(Constants.JSONKeys.FUNDING_DESTINATION)
                    && !(value = fundingObj.getString(Constants.JSONKeys.FUNDING_DESTINATION)).isEmpty()) {
                for (FundingDestination destination : FundingDestination.values()) {
                    if (destination.name().equalsIgnoreCase(value)) {
                        funding.destination(destination);
                        break;
                    }
                }
            }
            if (fundingObj.has(Constants.JSONKeys.FUNDING_EMAIL)
                    && !(value = fundingObj.getString(Constants.JSONKeys.FUNDING_EMAIL)).isEmpty()) {
                funding.email(value);
            }
            if (fundingObj.has(Constants.JSONKeys.FUNDING_MOBILE_PHONE)
                    && !(value = fundingObj.getString(Constants.JSONKeys.FUNDING_MOBILE_PHONE)).isEmpty()) {
                funding.mobilePhone(value);
            }
            if (fundingObj.has(Constants.JSONKeys.FUNDING_ACCOUNT_NUMBER)
                    && !(value = fundingObj.getString(Constants.JSONKeys.FUNDING_ACCOUNT_NUMBER)).isEmpty()) {
                funding.accountNumber(value);
            }
            if (fundingObj.has(Constants.JSONKeys.FUNDING_ROUTING_NUMBER)
                    && !(value = fundingObj.getString(Constants.JSONKeys.FUNDING_ROUTING_NUMBER)).isEmpty()) {
                funding.routingNumber(value);
            }
        }
        return funding.done();
    }
    
    /**
     * This request sets business details for the Merchant Account request.
     * 
     * @param businessRequest the business request
     * @return the business
     * @throws JSONException the jSON exception
     */
    private MerchantAccountRequest getBussiness(final BusinessRequest businessRequest) throws JSONException {
    
        final String businessDetails = (String) msgContext.getProperty(Constants.BUSSINESS_DETAILS);
        
        if (businessDetails != null && !businessDetails.isEmpty()) {
            final JSONObject bussinessObj = new JSONObject(businessDetails);
            
            String value;
            if (bussinessObj.has(Constants.JSONKeys.BUSSINESS_LEGAL_NAME)
                    && !(value = bussinessObj.getString(Constants.JSONKeys.BUSSINESS_LEGAL_NAME)).isEmpty()) {
                businessRequest.legalName(value);
            }
            if (bussinessObj.has(Constants.JSONKeys.BUSSINESS_DBA_NAME)
                    && !(value = bussinessObj.getString(Constants.JSONKeys.BUSSINESS_DBA_NAME)).isEmpty()) {
                businessRequest.dbaName(value);
            }
            if (bussinessObj.has(Constants.JSONKeys.BUSSINESS_TAX_ID)
                    && !(value = bussinessObj.getString(Constants.JSONKeys.BUSSINESS_TAX_ID)).isEmpty()) {
                businessRequest.taxId(value);
            }
        }
        return businessRequest.done();
    }
    
    /**
     * This request get Business Address details and set for business request.
     * 
     * @param request the request
     * @return the business address
     * @throws JSONException the jSON exception
     */
    private BusinessRequest getBussinessAddress(final MerchantAccountRequest request) throws JSONException {
    
        final BusinessAddressRequest businessaddress = request.business().address();
        final String businessDetails = (String) msgContext.getProperty(Constants.BUSSINESS_DETAILS);
        
        if (businessDetails != null && !businessDetails.isEmpty()) {
            final JSONObject businessJSON =
                    new JSONObject(businessDetails).getJSONObject(Constants.JSONKeys.BUSSINESS_ADDRESS);
            
            String value;
            if (businessJSON.has(Constants.JSONKeys.BUSSINESS_STREET_ADDRESS)
                    && !(value = businessJSON.getString(Constants.JSONKeys.BUSSINESS_STREET_ADDRESS)).isEmpty()) {
                businessaddress.streetAddress(value);
            }
            if (businessJSON.has(Constants.JSONKeys.BUSSINESS_LOCALITY)
                    && !(value = businessJSON.getString(Constants.JSONKeys.BUSSINESS_LOCALITY)).isEmpty()) {
                businessaddress.locality(value);
            }
            if (businessJSON.has(Constants.JSONKeys.BUSSINESS_REGION)
                    && !(value = businessJSON.getString(Constants.JSONKeys.BUSSINESS_REGION)).isEmpty()) {
                businessaddress.region(value);
            }
            if (businessJSON.has(Constants.JSONKeys.BUSSINESS_POSTAL_CODE)
                    && !(value = businessJSON.getString(Constants.JSONKeys.BUSSINESS_POSTAL_CODE)).isEmpty()) {
                businessaddress.postalCode(value);
            }
        }
        return businessaddress.done();
    }
    
    /**
     * This request sets individual details for the merchant account request.
     * 
     * @param individual the individual
     * @return the individual request
     * @throws JSONException the jSON exception
     */
    private MerchantAccountRequest getIndividualRequest(final IndividualRequest individual) throws JSONException {
    
        final String individualDetails = (String) msgContext.getProperty(Constants.INDIVIDUAL_DETAILS);
        if (individualDetails != null && !individualDetails.isEmpty()) {
            final JSONObject individualObj = new JSONObject(individualDetails);
            
            String value;
            if (individualObj.has(Constants.JSONKeys.INDIVIDUAL_FIRST_NAME)
                    && !(value = individualObj.getString(Constants.JSONKeys.INDIVIDUAL_FIRST_NAME)).isEmpty()) {
                individual.firstName(value);
            }
            if (individualObj.has(Constants.JSONKeys.INDIVIDUAL_LAST_NAME)
                    && !(value = individualObj.getString(Constants.JSONKeys.INDIVIDUAL_LAST_NAME)).isEmpty()) {
                individual.lastName(value);
            }
            if (individualObj.has(Constants.JSONKeys.INDIVIDUAL_EMAIL)
                    && !(value = individualObj.getString(Constants.JSONKeys.INDIVIDUAL_EMAIL)).isEmpty()) {
                individual.email(value);
            }
            if (individualObj.has(Constants.JSONKeys.INDIVIDUAL_PHONE)
                    && !(value = individualObj.getString(Constants.JSONKeys.INDIVIDUAL_PHONE)).isEmpty()) {
                individual.phone(value);
            }
            if (individualObj.has(Constants.JSONKeys.INDIVIDUAL_DATE_OF_BIRTH)
                    && !(value = individualObj.getString(Constants.JSONKeys.INDIVIDUAL_DATE_OF_BIRTH)).isEmpty()) {
                individual.dateOfBirth(value);
            }
            if (individualObj.has(Constants.JSONKeys.INDIVIDUAL_SSN)
                    && !(value = individualObj.getString(Constants.JSONKeys.INDIVIDUAL_SSN)).isEmpty()) {
                individual.ssn(value);
            }
        }
        return individual.done();
    }
    
    /**
     * This request sets individual address details for the Individual request.
     * 
     * @param request the request
     * @return the individual address request
     * @throws JSONException the jSON exception
     */
    private IndividualRequest getIndividualAddressRequest(final MerchantAccountRequest request) throws JSONException {
    
        final IndividualAddressRequest individualAddress = request.individual().address();
        final String individualDetails = (String) msgContext.getProperty(Constants.INDIVIDUAL_DETAILS);
        if (individualDetails != null && !individualDetails.isEmpty()) {
            final JSONObject individualObj =
                    new JSONObject(individualDetails).getJSONObject(Constants.JSONKeys.INDIVIDUAL_ADDRESS);
            
            String value;
            if (individualObj.has(Constants.JSONKeys.INDIVIDUAL_STREET_ADDRESS)
                    && !(value = individualObj.getString(Constants.JSONKeys.INDIVIDUAL_STREET_ADDRESS)).isEmpty()) {
                individualAddress.streetAddress(value);
            }
            if (individualObj.has(Constants.JSONKeys.INDIVIDUAL_LOCALITY)
                    && !(value = individualObj.getString(Constants.JSONKeys.INDIVIDUAL_LOCALITY)).isEmpty()) {
                individualAddress.locality(value);
            }
            if (individualObj.has(Constants.JSONKeys.INDIVIDUAL_REGION)
                    && !(value = individualObj.getString(Constants.JSONKeys.INDIVIDUAL_REGION)).isEmpty()) {
                individualAddress.region(value);
            }
            if (individualObj.has(Constants.JSONKeys.INDIVIDUAL_POSTAL_CODE)
                    && !(value = individualObj.getString(Constants.JSONKeys.INDIVIDUAL_POSTAL_CODE)).isEmpty()) {
                individualAddress.postalCode(value);
            }
        }
        return individualAddress.done();
    }
}
