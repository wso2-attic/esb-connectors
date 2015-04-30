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

package org.wso2.connector.integration.test.base;

import java.util.List;
import java.util.Map;

/**
 * This class represent the response of a REST request.
 */
public class RestResponse<T> {
    
    public static final byte JSON_TYPE = 1;
    
    public static final byte XML_TYPE = 2;
    
    private int httpStatusCode;
    
    private Map<String, List<String>> headersMap;
    
    private T body;
    
    public int getHttpStatusCode() {
    
        return httpStatusCode;
    }
    
    public void setHttpStatusCode(int httpStatusCode) {
    
        this.httpStatusCode = httpStatusCode;
    }
    
    public Map<String, List<String>> getHeadersMap() {
    
        return headersMap;
    }
    
    public void setHeadersMap(Map<String, List<String>> headersMap) {
    
        this.headersMap = headersMap;
    }
    
    public T getBody() {
    
        return body;
    }
    
    public void setBody(T body) {
    
        this.body = body;
    }
}
