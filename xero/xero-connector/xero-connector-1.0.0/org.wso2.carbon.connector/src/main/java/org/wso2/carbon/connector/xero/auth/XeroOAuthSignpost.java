/*
 * Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved. WSO2 Inc. licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.connector.xero.auth;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.signature.AuthorizationHeaderSigningStrategy;

import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.wso2.carbon.connector.core.AbstractConnector;

/**
 * Class XeroOAuthSignpost which helps to generate authentication header for Xero WSO2 ESB
 * Connector.
 */
public final class XeroOAuthSignpost extends AbstractConnector {
    
    /**
     * Connect method which is generating authentication signature of the connector for each request.
     * 
     * @param messageContext ESB messageContext.
     */
    @Override
    public void connect(final MessageContext messageContext) {
    
        // Generate the signing string.
        final String requestMethod = messageContext.getProperty(XeroConstants.REQUEST_METHOD).toString().toUpperCase();
        final String requestParams = messageContext.getProperty(XeroConstants.PARAMS).toString();
        
        String requestUrl =
                messageContext.getProperty(XeroConstants.API_URL).toString()
                        + messageContext.getProperty(XeroConstants.URI_APPENDER).toString()
                        + messageContext.getProperty(XeroConstants.URI_REMAINDER).toString();
        
        if (!requestParams.isEmpty()) {
            
            requestUrl += "?" + requestParams;
        }
        
        final String consumerKey = messageContext.getProperty(XeroConstants.CONSUMER_KEY).toString();
        final String consumerSecret = messageContext.getProperty(XeroConstants.CONSUMER_SECRET).toString();
        final String accessToken = messageContext.getProperty(XeroConstants.ACCESS_TOKEN).toString();
        final String accessTokenSecret = messageContext.getProperty(XeroConstants.ACCESS_TOKEN_SECRET).toString();
        
        final XeroHttpRequest request = new XeroHttpRequest();
        request.setRequestUrl(requestUrl);
        request.setMethod(requestMethod);
        
        // Generate the Authorization and get response through signpost.
        final OAuthConsumer consumer = new DefaultOAuthConsumer(consumerKey, consumerSecret);
        consumer.setTokenWithSecret(accessToken, accessTokenSecret);
        consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
        HttpRequest response;
        try {
            
            response = consumer.sign(request);
            messageContext.setProperty("auth", response.getHeader(OAuth.HTTP_AUTHORIZATION_HEADER));
            
        } catch (OAuthMessageSignerException omse) {
            log.error("Error occured in connector", omse);
            storeErrorResponseStatus(messageContext, omse, XeroConstants.OAUTH_MESSAGE_SIGNER_EXCEPTION);
            handleException("Error occured in connector", omse, messageContext);
        } catch (OAuthExpectationFailedException oefe) {
            log.error("Error occured in connector", oefe);
            storeErrorResponseStatus(messageContext, oefe, XeroConstants.OAUTH_EXPECTATION_FAILED_EXCEPTION);
            handleException("Error occured in connector", oefe, messageContext);
        } catch (OAuthCommunicationException oce) {
            log.error("Error occured in connector", oce);
            storeErrorResponseStatus(messageContext, oce, XeroConstants.OAUTH_COMMUNICATION_EXCEPTION);
            handleException("Error occured in connector", oce, messageContext);
        }
        
    }
    
    /**
     * Add a Throwable to a message context, the message from the throwable is embedded as the Synapse
     * Constant ERROR_MESSAGE.
     * 
     * @param ctxt message context to which the error tags need to be added
     * @param throwable Throwable that needs to be parsed and added
     * @param errorCode errorCode mapped to the exception
     */
    public void storeErrorResponseStatus(final MessageContext ctxt, final Throwable throwable, final int errorCode) {
    
        ctxt.setProperty(SynapseConstants.ERROR_CODE, errorCode);
        ctxt.setProperty(SynapseConstants.ERROR_MESSAGE, throwable.getMessage());
        ctxt.setFaultResponse(true);
    }
    
}
