/*
 * Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved. WSO2 Inc. licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.connector.quickbooks.auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import oauth.signpost.http.HttpRequest;

/**
 * Customized Http Request class to build request so that direct request is not sent during Oauth 1.0 signing.
 */
public class QuickBooksHttpRequest implements HttpRequest {
    
    /**
     * Request Headers Map.
     */
    private Map<String, String> headers = new HashMap<String, String>();
    
    /**
     * HTTP request method.
     */
    private String method;
    
    /**
     * HTTP request URL.
     */
    private String requestUrl;
    
    /**
     * HTTP request content type.
     */
    private String contentType;
    
    /**
     * Get all headers of the request.
     * 
     * @return a map containing the headers as key value pairs.
     */
    public final Map<String, String> getAllHeaders() {
    
        return headers;
    }
    
    /**
     * Get a single header from the request.
     * 
     * @param headerKey the string key of the header value to be returned.
     * @return the relevant header value as a string.
     */
    public final String getHeader(final String headerKey) {
    
        return headers.get(headerKey);
    }
    
    /**
     * Get an InputStream from the message payload. (overridden method)
     * 
     * @return the inputstream from the payload.
     * @throws IOException if IOException occurs.
     */
    public final InputStream getMessagePayload() throws IOException {
    
        return null;
    }
    
    /**
     * Set a single header.
     * 
     * @param headerKey the key of the header.
     * @param headerValue the value of the header.
     */
    public final void setHeader(final String headerKey, final String headerValue) {
    
        headers.put(headerKey, headerValue);
    }
    
    /**
     * Unwrap the request for sending (Overridden method).
     * 
     * @return the unwrapped request as an object.
     */
    public final Object unwrap() {
    
        return null;
    }
    
    /**
     * Get the HTTP request method.
     * 
     * @return the HTTP method as a string.
     */
    public final String getMethod() {
    
        return method;
    }
    
    /**
     * Sets the HTTP method for the request.
     * 
     * @param requestMethod The method to be set.
     */
    public final void setMethod(final String requestMethod) {
    
        this.method = requestMethod;
    }
    
    /**
     * Gets the URL the request needs to be sent to.
     * 
     * @return the Request URL.
     */
    public final String getRequestUrl() {
    
        return requestUrl;
    }
    
    /**
     * Sets the URL for the request.
     * 
     * @param requestURL The Request URL.
     */
    public final void setRequestUrl(final String requestURL) {
    
        this.requestUrl = requestURL;
    }
    
    /**
     * Returns the content type of the request.
     * 
     * @return the Content type as a string.
     */
    public final String getContentType() {
    
        return contentType;
    }
    
    /**
     * Sets the content type of the request.
     * 
     * @param requestContentType The Content Type to be set as a string.
     */
    
    public final void setContentType(final String requestContentType) {
    
        this.contentType = requestContentType;
    }
}
