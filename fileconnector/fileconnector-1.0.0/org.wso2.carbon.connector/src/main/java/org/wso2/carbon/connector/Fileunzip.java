/*
 *  Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.codehaus.jettison.json.JSONException;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.util.FileUnzipUtil;
import org.wso2.carbon.connector.util.ResultPayloadCreater;


public class Fileunzip extends AbstractConnector implements Connector {

    private static Log log = LogFactory.getLog(Fileunzip.class);

    public void connect(MessageContext messageContext) throws ConnectException {

        String fileLocation =
                getParameter(messageContext, "filelocation") == null ? "" : getParameter(
                        messageContext,
                        "filelocation").toString();

        String newFileLocation =
                getParameter(messageContext, "newfilelocation") == null ? "" : getParameter(
                        messageContext,
                        "newfilelocation").toString();

        boolean resultStatus;
        try {
            resultStatus = new FileUnzipUtil().unzip(fileLocation, newFileLocation, messageContext);
        } catch (Exception e) {
            handleException(e.getMessage(), messageContext);
            resultStatus = false;
        }
        generateResults(messageContext, resultStatus);
    }

    private void generateResults(MessageContext messageContext, boolean resultStatus) {
        ResultPayloadCreater resultPayload = new ResultPayloadCreater();

        String responses = "<unzip><success>" + resultStatus + "</success></unzip>";

        try {
            OMElement element = resultPayload.performSearchMessages(responses);
            resultPayload.preparePayload(messageContext, element);
        } catch (XMLStreamException e) {
            log.error(e.getMessage());
            handleException(e.getMessage(), messageContext);
        } catch (IOException e) {
            log.error(e.getMessage());
            handleException(e.getMessage(), messageContext);
        } catch (JSONException e) {
            log.error(e.getMessage());
            handleException(e.getMessage(), messageContext);
        }
    }
}


