/**
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * <p/>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.connector.integration.test.ejb2;

import junit.framework.Assert;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class Ejb2ConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    Properties p = new Properties();
    InitialContext ctx;

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init("ejb2-connector-1.0.0");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Accept", "application/json");
        p.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
        p.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
        p.put(Context.PROVIDER_URL, "localhost");
        ctx = new InitialContext(p);
    }

    @Test(enabled = true, description = "Stateless Bean Jboss")
    public void statelessBean() throws Exception {

        String methodName = "ejb2Stateless";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName)
                , "GET", esbRequestHeadersMap, "stateless.json");
        Assert.assertEquals(esbRestResponse.getBody().get("Result")
                , checkStateless.getFromStateless(ctx));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

    @Test(enabled = true, description = "Stateful Bean Jboss")
    public void statefulBean() throws Exception {
        String methodName = "ejb2Stateful";
        RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(getProxyServiceURL(methodName)
                , "GET", esbRequestHeadersMap, "stateful.json");
        Assert.assertEquals(esbRestResponse.getBody().get("Result")
                , checkStateful.getFromStaeful(ctx));
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

}
