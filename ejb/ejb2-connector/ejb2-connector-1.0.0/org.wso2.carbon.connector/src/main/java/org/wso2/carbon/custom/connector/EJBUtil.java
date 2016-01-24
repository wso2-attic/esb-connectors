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

import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.databinding.typemapping.SimpleTypeMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.Value;
import org.wso2.carbon.connector.core.util.ConnectorUtils;

import javax.ejb.EJBHome;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.xml.stream.XMLStreamException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class EJBUtil {
    private static final Log log = LogFactory.getLog(EJBUtil.class);

    /**
     * @param instance instance of an ejb object
     * @param method   method that we want to call
     * @param args     arguments object for method
     * @return invoke arguments into method and return the value which actual method returns
     */
    public static Object invokeInstanceMethod(Object instance, Method method, Object[] args) {
        Class[] paramTypes = method.getParameterTypes();
        if (paramTypes.length != args.length) {
            handleException("Provided argument count does not match method the "
                    + "parameter count of method '" + method.getName() + "'. Argument count = "
                    + args.length + ", method parameter count = " + paramTypes.length);
        }
        Object[] processedArgs = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; ++i) {
            if (args[i] == null || paramTypes[i].isAssignableFrom(args[i].getClass())) {
                processedArgs[i] = args[i];
            } else if (SimpleTypeMapper.isSimpleType(paramTypes[i])) {
                try {
                    processedArgs[i] = SimpleTypeMapper.getSimpleTypeObject(paramTypes[i],
                            AXIOMUtil.stringToOM("<a>" + args[i] + "</a>"));
                } catch (XMLStreamException e) {
                    handleException("XMLStreamException ", e);
                }
            } else {
                handleException("Incompatible argument found in " + i + "th argument "
                        + "for '" + method.getName() + "' method.");
            }
        }
        try {
            return method.invoke(instance, processedArgs);
        } catch (IllegalAccessException |InvocationTargetException e ) {
            handleException("Error while invoking '" + method.getName() + "' method "
                    + "via reflection.", e);
        } //catch (InvocationTargetException e) {
//            handleException("Error while invoking '" + method.getName() + "' method "
//                    + "via reflection.", e);
//        }
        return null;
    }

    /**
     * @param aClass     class of our target ejb remote
     * @param methodName name of the method
     * @param argCount   number of arguments
     * @return extract the value's from properties and make its as hashable
     */
    public static Method resolveMethod(Class aClass, String methodName, int argCount) {
        Method resolvedMethod = null;
        for (Method method : aClass.getDeclaredMethods()) {
            if (method.getName().equals(methodName) &&
                    method.getParameterTypes().length == argCount) {
                if (resolvedMethod == null) {
                    resolvedMethod = method;
                } else {
                    handleException("More than one '" + methodName + "' methods " +
                            "that take " + argCount + " arguments are found in '" +
                            aClass.getName() + "' class.");
                }
            }
        }
        return resolvedMethod;
    }

    /**
     * @param messageContext message contest
     * @param operationName  name of the operation
     * @return extract the value's from properties and make its as hashable
     */
    public static Object[] buildArguments(MessageContext messageContext, String operationName) {
        Hashtable<String, String> argValues = getParameters(messageContext, operationName);
        Set<String> argSet = argValues.keySet();
        Object[] args = new Object[argValues.size()];
        int i = 0;
        for (String aSet : argSet) {
            args[i] = argValues.get(aSet);
            i++;
        }
        return args;
    }

    /**
     * @param messageContext message contest
     * @param operationName  Name of the operation
     * @return extract the value's from properties and make its as hashable
     */
    public static Hashtable getParameters(MessageContext messageContext, String operationName) {
        Hashtable dynamicValues = new Hashtable();
        String key;
        if (operationName.equals(EJBConstants.INIT)) {
            key = ((String) getParameter(messageContext, EJBConstants.KEY));
        } else {
            key = EJBConstants.PARAMETER;
        }
        key = operationName + ":" + key;
        Map<String, Object> propertiesMap = (((Axis2MessageContext) messageContext).getProperties());
        Set prop = messageContext.getPropertyKeySet();
        Value probValues;
        for (String stringValue : (String[]) prop.toArray(new String[prop.size()])) {
            if (stringValue.startsWith(key)) {
                probValues = (Value) propertiesMap.get(stringValue);
                dynamicValues.put(stringValue.substring(key.length() + 1, stringValue.length())
                        , probValues.getKeyValue());
                messageContext.getPropertyKeySet().remove(stringValue);
            }
        }
        return dynamicValues;
    }

    protected static Object getParameter(MessageContext messageContext, String paramName) {
        return ConnectorUtils.lookupTemplateParamater(messageContext, paramName);
    }

    public static void handleException(String message) {
        log.error(message);
        throw new SynapseException(message);
    }

    public static void handleException(String message, Exception e) {
        log.error(message);
        throw new SynapseException(message, e);
    }

    /**
     * @param messageContext messageContext
     * @param jndiName       jndi name
     * @return ejb remote object
     */
    public static Object getEJBObject(MessageContext messageContext, String jndiName) {
        Object ejbObject = null;
        try {
            InitialContext context = new InitialContext((Properties) messageContext.getProperty(EJBConstants.JNDI_PROPERTIES));
            Object obj = context.lookup(getParameter(messageContext, jndiName).toString());
            EJBHome ejbHome = (EJBHome) PortableRemoteObject.narrow(obj, EJBHome.class);
            Method method = ejbHome.getClass().getDeclaredMethod(EJBConstants.CREATE);
            if (method != null) {
                ejbObject = method.invoke(ejbHome);
            } else handleException("ejb home is missing ");
        } catch (IllegalAccessException e) {
            handleException("Failed to get ejb Object because of IllegalAccessException ", e);
        } catch (InvocationTargetException e) {
            handleException("Failed to get ejb Object because of InvocationTargetException ", e);
        } catch (NoSuchMethodException e) {
            handleException("Failed lookup because of create method not exist " + e.getMessage());
        } catch (NamingException e) {
            handleException("Failed lookup because of NamingException ", e);
        }
        return ejbObject;
    }
}