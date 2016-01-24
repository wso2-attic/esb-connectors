/**
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.custom.connector;

import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;

import java.lang.reflect.Method;

public class CallEJBStatefulBean extends AbstractConnector {

    @Override
    public void connect(MessageContext messageContext) {
        Object ejbObj;
        String methodName = (String) getParameter(messageContext, EJBConstants.METHOD_NAME);
        String returnName = (String) getParameter(messageContext, EJBConstants.RETURN);
        if (getParameter(messageContext, EJBConstants.RETURN) == null) {
            returnName = EJBConstants.RESPONSE;
        }
        if (messageContext.getProperty(EJBConstants.EJB_OBJECT) == null) {
            ejbObj = EJBUtil.getEJBObject(messageContext, EJBConstants.JNDI_NAME);
            messageContext.setProperty(EJBConstants.EJB_OBJECT, ejbObj);
        } else {
            ejbObj = messageContext.getProperty(EJBConstants.EJB_OBJECT);
        }
        Object[] args = EJBUtil.buildArguments(messageContext, EJBConstants.STATEFUL);
        Method method = EJBUtil.resolveMethod(ejbObj.getClass(), methodName, args.length);
        Object obj = EJBUtil.invokeInstanceMethod(ejbObj, method, args);
        if (!method.getReturnType().toString().equals(EJBConstants.VOID)) {
            messageContext.setProperty(returnName, obj);
        } else {
            messageContext.setProperty(EJBConstants.RESPONSE, EJBConstants.SUCCESS);
        }
    }
}