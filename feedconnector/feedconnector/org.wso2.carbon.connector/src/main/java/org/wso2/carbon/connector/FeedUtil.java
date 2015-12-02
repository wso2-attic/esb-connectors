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
package org.wso2.carbon.connector;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.synapse.MessageContext;

/**
 * Response to ESB
 */
public class FeedUtil {

    public void InjectMessage(MessageContext messageContext, String text) {
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = omFactory.createOMNamespace(FeedConstant.Status, FeedConstant.ns);
        OMElement result = omFactory.createOMElement(FeedConstant.result, ns);
        OMElement messageElement = omFactory.createOMElement(FeedConstant.Result, ns);
        messageElement.setText(text);
        result.addChild(messageElement);
        messageContext.getEnvelope().getBody().addChild(result);
    }

    public  static  Factory getFactory(){
        Abdera abdera = new Abdera();
        return abdera.getFactory();
    }

    public  static AbderaClient getAbderaClient(){
        Abdera abdera = new Abdera();
        return new AbderaClient(abdera);
    }
}
