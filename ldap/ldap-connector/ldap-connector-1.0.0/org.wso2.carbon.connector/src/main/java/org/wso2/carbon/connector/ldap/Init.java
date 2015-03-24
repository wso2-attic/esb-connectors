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

import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

public class Init extends AbstractConnector{

	@Override
	public void connect(MessageContext messageContext) throws ConnectException {
		String providerUrl = (String)getParameter(messageContext, LDAPConstants.PROVIDER_URL);
		String securityPrincipal = (String)getParameter(messageContext, LDAPConstants.SECURITY_PRINCIPAL);
		String securityCredentials = (String)getParameter(messageContext, LDAPConstants.SECURITY_CREDENTIALS);
		LDAPUtils.storeAdminLoginDatails(messageContext, providerUrl, securityPrincipal, securityCredentials);
	}

}
