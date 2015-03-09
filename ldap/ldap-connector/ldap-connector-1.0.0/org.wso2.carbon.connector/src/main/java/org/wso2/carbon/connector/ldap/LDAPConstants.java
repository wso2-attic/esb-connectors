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

package org.wso2.carbon.connector.ldap;

public class LDAPConstants {
	public static final String PROVIDER_URL="providerUrl";
	public static final String SECURITY_PRINCIPAL="securityPrincipal";
	public static final String SECURITY_CREDENTIALS	="securityCredentials";
    public static final String CONNECTOR_NAMESPACE = "http://org.wso2.esbconnectors.ldap";

    public static final class ErrorConstants{
        public static final int SEARCH_ERROR = 7000001;
        public static final int INVALID_LDAP_CREDENTIALS = 7000002;
        public static final int ADD_ENTRY_ERROR = 7000003;
        public static final int UPDATE_ENTRY_ERROR = 7000004;
        public static final int DELETE_ENTRY_ERROR = 7000005;
        public static final int ENTRY_DOESNOT_EXISTS_ERROR = 7000006;

    }
}
