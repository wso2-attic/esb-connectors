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

package org.wso2.carbon.connector.googlecontacts;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.synapse.MessageContext;

import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.util.ServiceException;

/**
 * Class to retrieve contacts by sending an authorized GET request using query parameters such as requesting
 * contacts created or updated in a given date range, or published by a particular author.
 * 
 * @see https
 *      ://developers.google.com/google-apps/contacts/v3/?hl=ja#retrieving_contacts_using_query_parameters
 */
public final class RetrieveContactsByQueryMediator extends AbstractGoogleContactsConnector {
    
    /**
     * This method retrieving contacts according to provided query parameters of a particular user.
     * 
     * @param messageContext ESB message context.
     */
    public void connect(final MessageContext messageContext) {
    
        // Getting query parameters map with values passed in via proxy.
        final Map<String, String> queryParamMap =
                buildParameterMap(new String[] {Constants.QUERY, Constants.MAX_RESULT, Constants.START_INDEX,
                        Constants.UPDATED_MIN, Constants.ORDER_BY, Constants.SHOW_DELETED,
                        Constants.REQUIRE_ALL_DELETED, Constants.SORT_ORDER, Constants.GROUP }, messageContext);
        
        // Build the end point request URL created for contacts.
        final StringBuilder requestUrl = getRequestURLBuilder(messageContext, Constants.REQUEST_URL_CONTACTS);
        requestUrl.append(Constants.REQUEST_URL_GENERIC_END);
        
        try {
            
            // Authenticate with google contacts API and get returning contacts Service.
            final ContactsService contactsService = getContactService(messageContext);
            
            // Create query to set query parameters before send the request.
            final URL feedUrl = new URL(requestUrl.toString());
            Query contactQuery = new Query(feedUrl);
            
            // Set FullTextQuery parameter.
            final String query = queryParamMap.get(Constants.QUERY);
            if (!query.isEmpty()) {
                contactQuery.setFullTextQuery(query);
            }
            
            // Set MaxResults query parameter.
            final String maxResults = queryParamMap.get(Constants.MAX_RESULT);
            if (!maxResults.isEmpty()) {
                contactQuery.setMaxResults(Integer.parseInt(maxResults));
            }
            
            // Set StartIndex query parameter.
            final String startIndex = queryParamMap.get(Constants.START_INDEX);
            if (!startIndex.isEmpty()) {
                contactQuery.setStartIndex(Integer.parseInt(startIndex));
            }
            
            // Set UpdatedMin query parameter.
            final String updatedMin = queryParamMap.get(Constants.UPDATED_MIN);
            if (!updatedMin.isEmpty()) {
                contactQuery.setUpdatedMin(DateTime.parseDateTime(updatedMin));
            }
            
            // Set OrderBy query parameter.
            contactQuery =
                    setCustomParameters(contactQuery, queryParamMap, Constants.PARAM_ORDER_BY, Constants.ORDER_BY);
            // Set showDeleted query parameter.
            contactQuery =
                    setCustomParameters(contactQuery, queryParamMap, Constants.PARAM_SHOW_DELETED,
                            Constants.SHOW_DELETED);
            // Set requireAllDeleted query parameter.
            contactQuery =
                    setCustomParameters(contactQuery, queryParamMap, Constants.PARAM_REQUIRE_ALL_DELETED,
                            Constants.REQUIRE_ALL_DELETED);
            // Set sortOrder query parameter.
            contactQuery =
                    setCustomParameters(contactQuery, queryParamMap, Constants.PARAM_SORT_ORDER, Constants.SORT_ORDER);
            // Set group query parameter.
            contactQuery = setCustomParameters(contactQuery, queryParamMap, Constants.GROUP, Constants.GROUP);
            
            // Submit the request with the query parameters.
            final ContactFeed resultFeed = contactsService.query(contactQuery, ContactFeed.class);
            
            // Build the result Envelope.
            messageContext.getEnvelope().detach();
            messageContext.setEnvelope(getGDataSOAPEnvelope(resultFeed, contactsService));
            
        } catch (XMLStreamException xmlse) {
            log.error("Error parsing XML stream: ", xmlse);
            storeErrorResponseStatus(messageContext, xmlse, Constants.ERROR_CODE_XML_STRM_PARSE_FAILURE);
            handleException("Error parsing XML stream: ", xmlse, messageContext);
        } catch (MalformedURLException mue) {
            log.error("Error reading contact entry from built URL: ", mue);
            storeErrorResponseStatus(messageContext, mue, Constants.ERROR_CODE_MALFORMED_URL_EXCEPTION);
            handleException("Error reading contact entry from built URL: ", mue, messageContext);
        } catch (NumberFormatException nfe) {
            log.error("Invalid number or date format: ", nfe);
            storeErrorResponseStatus(messageContext, nfe, Constants.ERROR_CODE_NUMBER_FORMAT_EXCEPTION);
            handleException("Invalid number or date format: ", nfe, messageContext);
        } catch (ServiceException se) {
            log.error("Service unavailable: ", se);
            storeErrorResponseStatus(messageContext, se, Constants.ERROR_CODE_SERVICE_EXCEPTION);
            handleException("Service unavailable: ", se, messageContext);
        } catch (IOException io) {
            log.error("Failed to access entry: ", io);
            storeErrorResponseStatus(messageContext, io, Constants.ERROR_CODE_IO_EXCEPTION);
            handleException("Failed to access entry: ", io, messageContext);
        } catch (RuntimeException re) {
            log.error("Error occured in connector: ", re);
            storeErrorResponseStatus(messageContext, re, Constants.ERROR_CODE_RUNTIME_EXCEPTION);
            handleException("Error occured in connector: ", re, messageContext);
        }
    }
    
    /**
     * This method assign custom query parameters for the contact query if query parameters are available.
     * 
     * @param contactQuery the constructed query object.
     * @param paramValues the query parameter values map.
     * @param paramKey the query parameter key map.
     * @param key the parameter key value.
     * @return the constructed query object with custom parameters.
     */
    
    private Query setCustomParameters(final Query contactQuery, final Map<String, String> paramValues,
            final String paramKey, final String key) {
    
        if (!paramValues.get(key).isEmpty()) {
            contactQuery.setStringCustomParameter(paramKey, paramValues.get(key));
        }
        
        return contactQuery;
    }
    
}
