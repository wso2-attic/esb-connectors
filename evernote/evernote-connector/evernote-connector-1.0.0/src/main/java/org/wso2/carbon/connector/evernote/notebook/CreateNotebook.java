/**
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.evernote.notebook;

import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.TException;
import org.apache.axiom.om.OMElement;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.SynapseLog;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.evernote.util.EvernoteUtil;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;


public class CreateNotebook extends AbstractConnector {
    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
        try {
            SynapseLog log = getLog(messageContext);
            log.auditLog("Start : createNotebook");
            NoteStoreClient noteStoreClient = EvernoteUtil.getNoteStoreClient(messageContext);
            Notebook notebook = new Notebook();

            String notebookName = EvernoteUtil.lookupTemplateParamater(messageContext,EvernoteUtil.NOTEBOOK_NAME);
            String defaultNotebook = EvernoteUtil.lookupTemplateParamater(messageContext,EvernoteUtil.NOTEBOOK_DEFAULT);

            if(notebookName!=null&&!notebookName.trim().equalsIgnoreCase("")){
                notebook.setName(notebookName);
            }
            if (defaultNotebook!=null&&!defaultNotebook.equalsIgnoreCase("")){
                notebook.setDefaultNotebook(Boolean.parseBoolean(defaultNotebook));
            }

            Notebook createdNotebook = noteStoreClient.createNotebook(notebook);
            OMElement omResponse = EvernoteUtil.parseResponse("notebook.create.success");
            OMElement omNotebook = EvernoteUtil.createOMElement("notebook");
            EvernoteUtil.addAttribute(omNotebook, "guid", createdNotebook.getGuid());
            omResponse.addChild(omNotebook);
            EvernoteUtil.preparePayload(messageContext,omResponse);
            log.auditLog("Stop : createNotebook");
        } catch (TException e) {
            log.error(e.getMessage());
            EvernoteUtil.handleException(e, e.getMessage(), "20", messageContext);
            throw new SynapseException(e);
        } catch (EDAMUserException e) {
            log.error(e.getParameter());
            EvernoteUtil.handleException(e,e.getParameter(), e.getErrorCode().getValue()+"", messageContext);
            throw new SynapseException(e);
        } catch (EDAMSystemException e) {
            log.error(e.getMessage());
            EvernoteUtil.handleException(e,e.getMessage() ,e.getErrorCode().getValue()+"", messageContext);
            throw new SynapseException(e);
        } catch (Exception e){
            log.error(e.getMessage());
            EvernoteUtil.handleException(e,"Invalid Input" ,"21", messageContext);
            throw new SynapseException(e);
        }

    }
}
