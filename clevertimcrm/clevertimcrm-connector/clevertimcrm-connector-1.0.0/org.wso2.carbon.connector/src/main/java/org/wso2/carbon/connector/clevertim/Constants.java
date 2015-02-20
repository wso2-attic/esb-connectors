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

package org.wso2.carbon.connector.clevertim;

/**
 * Constants class for Clevertim Connector.
 */
public final class Constants {
    
    /**
     * Instantiates a new constants.
     */
    private Constants() {
    
    }
    
    /**
     * CleverTim ID.
     */
    public static final String ID = "uri.var.id";
    
    /**
     * CleverTim NAME.
     */
    public static final String NAME = "uri.var.name";
    
    /**
     * CleverTim DESCRIPTION.
     */
    public static final String DESCRIPTION = "uri.var.description";
    
    /**
     * CleverTim STATUS.
     */
    public static final String STATUS = "uri.var.status";
    
    /**
     * CleverTim LEAD_USER.
     */
    public static final String LEAD_USER = "uri.var.leadUser";
    
    /**
     * CleverTim VALUE.
     */
    public static final String VALUE = "uri.var.value";
    
    /**
     * CleverTim CURRENCY.
     */
    public static final String CURRENCY = "uri.var.currency";
    
    /**
     * CleverTim CUSTOM_FIELDS.
     */
    public static final String CUSTOM_FIELDS = "uri.var.customFields";
    
    /**
     * CleverTim CUSTOMER.
     */
    public static final String CUSTOMER = "uri.var.customer";
    
    /**
     * CleverTim TAGS.
     */
    public static final String TAGS = "uri.var.tags";
    
    /**
     * CleverTim FIRST_NAME.
     */
    public static final String FIRST_NAME = "uri.var.firstName";
    
    /**
     * CleverTim LAST_NAME.
     */
    public static final String LAST_NAME = "uri.var.lastName";
    
    /**
     * CleverTim TITLE.
     */
    public static final String TITLE = "uri.var.title";
    
    /**
     * CleverTim IS_COMPANY.
     */
    public static final String IS_COMPANY = "uri.var.isCompany";
    
    /**
     * CleverTim COMPANY_ID.
     */
    public static final String COMPANY_ID = "uri.var.companyId";
    
    /**
     * CleverTim EMAIL.
     */
    public static final String EMAIL = "uri.var.email";
    
    /**
     * CleverTim PHONES.
     */
    public static final String PHONES = "uri.var.phones";
    
    /**
     * CleverTim WEBSITE.
     */
    public static final String WEBSITE = "uri.var.website";
    
    /**
     * CleverTim ADDRESS.
     */
    public static final String ADDRESS = "uri.var.address";
    
    /**
     * CleverTim CITY.
     */
    public static final String CITY = "uri.var.city";
    
    /**
     * CleverTim POST_CODE.
     */
    public static final String POST_CODE = "uri.var.postCode";
    
    /**
     * CleverTim COUNTRY.
     */
    public static final String COUNTRY = "uri.var.country";
    
    /**
     * CleverTim SOCIAL_MEDIA_IDS.
     */
    public static final String SOCIAL_MEDIA_IDS = "uri.var.socialMediaIds";
    
    /**
     * CleverTim CUSTOMER_TYPE.
     */
    public static final String CUSTOMER_TYPE = "uri.var.customerType";
    
    /**
     * CleverTim COMPANY_NAME.
     */
    public static final String COMPANY_NAME = "uri.var.companyName";
    
    /**
     * Error code constant for JSON building exception.
     */
    public static final int ERROR_CODE_JSON_EXCEPTION = 700003;
    
    /**
     * Invalid JSON constant.
     */
    public static final String INVALID_JSON_MSG = "Invalid json request.";
    
    /**
     * Class to contain constants related to JSON keys for back end requests.
     */
    public static class JSONKeys {
        
        /**
         * JSON Request Key: is.
         */
        public static final String ID = "id";
        
        /**
         * JSON Request Key: is.
         */
        public static final String NAME = "name";
        
        /**
         * JSON Request Key: value.
         */
        public static final String VALUE = "value";
        
        /**
         * JSON Request Key: description.
         */
        public static final String DESCRIPTION = "description";
        
        /**
         * JSON Request Key: status.
         */
        public static final String STATUS = "status";
        
        /**
         * JSON Request Key: leadUser.
         */
        public static final String LEAD_USER = "leadUser";
        
        /**
         * JSON Request Key: currency.
         */
        public static final String CURRENCY = "currency";
        
        /**
         * JSON Request Key: cust.
         */
        public static final String CUSTOMER = "cust";
        
        /**
         * JSON Request Key: tags.
         */
        public static final String TAGS = "tags";
        
        /**
         * JSON Request Key: cf.
         */
        public static final String CUSTOM_FIELD = "cf";
        
        /**
         * JSON Request Key: fn.
         */
        public static final String FIRST_NAME = "fn";
        
        /**
         * JSON Request Key: ln.
         */
        public static final String LAST_NAME = "ln";
        
        /**
         * JSON Request Key: title.
         */
        public static final String TITLE = "title";
        
        /**
         * JSON Request Key: is_company.
         */
        public static final String IS_COMPANY = "is_company";
        
        /**
         * JSON Request Key: companyId.
         */
        public static final String COMPANY_ID = "companyId";
        
        /**
         * JSON Request Key: email.
         */
        public static final String EMAIL = "email";
        
        /**
         * JSON Request Key: phones.
         */
        public static final String PHONES = "phones";
        
        /**
         * JSON Request Key: website.
         */
        public static final String WEBSITE = "website";
        
        /**
         * JSON Request Key: address.
         */
        public static final String ADDRESS = "address";
        
        /**
         * JSON Request Key: city.
         */
        public static final String CITY = "city";
        
        /**
         * JSON Request Key: postcode.
         */
        public static final String POST_CODE = "postcode";
        
        /**
         * JSON Request Key: country.
         */
        public static final String COUNTRY = "country";
        
        /**
         * JSON Request Key: smids.
         */
        public static final String SOCIAL_MEDIA_IDS = "smids";
        
        /**
         * JSON Request Key: ctype.
         */
        public static final String CUSTOMER_TYPE = "ctype";
        
        /**
         * JSON Request Key: cn.
         */
        public static final String COMPANY_NAME = "cn";
        
    }
}
