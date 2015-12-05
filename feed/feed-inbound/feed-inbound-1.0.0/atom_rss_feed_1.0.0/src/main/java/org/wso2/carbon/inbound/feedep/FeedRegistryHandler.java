/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.inbound.feedep;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.inbound.endpoint.persistence.ServiceReferenceHolder;
import org.wso2.carbon.registry.api.Registry;
import org.wso2.carbon.registry.api.RegistryException;
import org.wso2.carbon.registry.api.Resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**Read/Write operations with registry*/
public class FeedRegistryHandler {
    private static final Log log = LogFactory.getLog(FeedRegistryHandler.class.getName());
    private Resource resource;
    private Registry registry;
    private Object obj;

    public FeedRegistryHandler() {
        try {
            registry = ServiceReferenceHolder.getInstance().getRegistry();
        } catch (RegistryException e) {
            log.error("error while handle with registry", e);
        }
    }

    public Object readFromRegistry(String resourcePath) {
        try {
            if (registry.resourceExists(resourcePath)) {
                resource = registry.get(resourcePath);
                byte[] content = (byte[]) resource.getContent();
                obj = toObject(content);
            }
        } catch (RegistryException e) {
            log.error("error while handle with registry", e);
        }
        return obj;
    }

    private Object toObject(byte[] arrayDate) {
        ByteArrayInputStream bis = new ByteArrayInputStream(arrayDate);
        ObjectInputStream in;
        try {
            in = new ObjectInputStream(bis);
            in.close();
            bis.close();
            return in.readObject();
        } catch (IOException e) {
            log.error("Error while reading the registry", e);
        } catch (ClassNotFoundException e) {
            log.error("unable to access readObject method ", e);
        }
        return null;
    }

    private byte[] toByteArray(Object date) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(date);
            oos.flush();
            oos.close();
            bos.close();
        } catch (IOException e) {
            log.error("Error while reading the registry", e);
        }
        return bos.toByteArray();
    }

    public void writeToRegistry(String resourceID, Object date) {
        try {
            resource = registry.newResource();
            resource.setContent(toByteArray(date));
            registry.put(resourceID, resource);
        } catch (RegistryException e) {
            log.error("error while handle with registry", e);
        }
    }

    public void deleteFromRegistry(String resourcePath) {
        try {
            registry.delete(resourcePath);
            if ((log.isDebugEnabled())) {
                log.debug(resourcePath + " Registry Deleted");
            }
        } catch (RegistryException e) {
            log.error("error while handle with registry", e);
        }
    }
}
