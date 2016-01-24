/**
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import com.google.gdata.data.contacts.ContactGroupFeed;
import com.google.gdata.util.ServiceException;

/**
 * This class retrieves contact groups by sending an authorized GET request using query parameters such as
 * requesting contact groups created or updated in a given date range, or published by a particular author.
 * This class refers {@link = AbstractGoogleContactsConnector}
 * 
 * @see https
 *      ://developers.google.com/google-apps/contacts/v3/#retrieving_contact_groups_using_query_parameters
 */
public final class RetrieveContactGroupsByQueryMediator extends AbstractGoogleContactsConnector {
    
    /**
     * This method allows a user to request a set of contact groups that match specified criteria, such as
     * requesting contact groups updated after a given date.
     * 
     * @param messageContext ESB message context.
     */
    public void connect(final MessageContext messageContext) {
    
        // Getting query parameters map with values passed in via proxy.
        final Map<String, String> queryParamMap =
                buildParameterMap(new String[]{Constants.QUERY, Constants.MAX_RESULT, Constants.START_INDEX,
                        Constants.UPDATED_MIN, Constants.ORDER_BY, Constants.SHOW_DELETED,
                        Constants.REQUIRE_ALL_DELETED, Constants.SORT_ORDER, Constants.GROUP}, messageContext);
        
        // Build the end point request URL created for contacts.
        final StringBuilder requestUrl = getRequestURLBuilder(messageContext, Constants.REQUEST_URL_GROUPS);
        requestUrl.append(Constants.REQUEST_URL_GENERIC_END);
        
        try {
            // Authenticate with google contacts API and get returning contacts Service.
            final ContactsService contactsService = getContactService(messageContext);
            
            // Create query to set query parameters before send the request.
            final Query contactQuery = setParamsForQuery(new Query(new URL(requestUrl.toString())), queryParamMap);
            
            // Get the ContactGroupFeed feed for parameter set query
            final ContactGroupFeed resultFeed = contactsService.query(contactQuery, ContactGroupFeed.class);
            
            // Build the result Envelope.
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
            log.error("Error formatting parsed date: ", nfe);
            storeErrorResponseStatus(messageContext, nfe, Constants.ERROR_CODE_NUMBER_FORMAT_EXCEPTION);
            handleException("Error formatting parsed date: ", nfe, messageContext);
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
     * This method add all query parameters for query.
     * 
     * @param contactQuery The contact query param
     * @param queryParamMap The query param map
     * @return Query query
     */
    private Query setParamsForQuery(final Query contactQuery, final Map<String, String> queryParamMap) {
    
        // Set FullTextQuery parameter.
        final String query = queryParamMap.get(Constants.QUERY);
        if (query != null && !query.isEmpty()) {
            contactQuery.setFullTextQuery(query);
        }
        
        // Set MaxResults query parameter.
        final String maxResults = queryParamMap.get(Constants.MAX_RESULT);
        if (maxResults != null && !maxResults.isEmpty()) {
            contactQuery.setMaxResults(Integer.parseInt(maxResults));
        }
        
        // Set StartIndex query parameter.
        final String startIndex = queryParamMap.get(Constants.START_INDEX);
        if (startIndex != null && !startIndex.isEmpty()) {
            contactQuery.setStartIndex(Integer.parseInt(startIndex));
        }
        
        // Set UpdatedMin query parameter.
        final String updatedMin = queryParamMap.get(Constants.UPDATED_MIN);
        if (updatedMin != null && !updatedMin.isEmpty()) {
            contactQuery.setUpdatedMin(DateTime.parseDateTime(updatedMin));
        }
        
        // Set OrderBy query parameter.
        final String orderBy = queryParamMap.get(Constants.ORDER_BY);
        if (orderBy != null && !orderBy.isEmpty()) {
            contactQuery.setStringCustomParameter(Constants.PARAM_ORDER_BY, orderBy);
        }
        
        // Set showDeleted query parameter.
        final String showDeleted = queryParamMap.get(Constants.SHOW_DELETED);
        if (showDeleted != null && !showDeleted.isEmpty()) {
            contactQuery.setStringCustomParameter(Constants.PARAM_SHOW_DELETED, showDeleted);
        }
        
        // Set requireAllDeleted query parameter.
        final String allDeleted = queryParamMap.get(Constants.REQUIRE_ALL_DELETED);
        if (allDeleted != null && !allDeleted.isEmpty()) {
            contactQuery.setStringCustomParameter(Constants.PARAM_REQUIRE_ALL_DELETED, allDeleted);
        }
        
        // Set sortOrder query parameter.
        final String sortOrder = queryParamMap.get(Constants.SORT_ORDER);
        if (sortOrder != null && !sortOrder.isEmpty()) {
            contactQuery.setStringCustomParameter(Constants.PARAM_SORT_ORDER, sortOrder);
        }
        
        // Set group query parameter.
        final String group = queryParamMap.get(Constants.GROUP);
        if (group != null && !group.isEmpty()) {
            contactQuery.setStringCustomParameter(Constants.GROUP, group);
        }
        
        return contactQuery;
    }
}
