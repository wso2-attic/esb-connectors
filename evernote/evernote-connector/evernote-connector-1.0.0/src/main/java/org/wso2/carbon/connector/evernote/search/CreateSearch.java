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

package org.wso2.carbon.connector.evernote.search;

import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.QueryFormat;
import com.evernote.edam.type.SavedSearch;
import com.evernote.edam.type.SavedSearchScope;
import com.evernote.thrift.TException;
import org.apache.axiom.om.OMElement;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.SynapseLog;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.evernote.util.EvernoteUtil;



public class CreateSearch  extends AbstractConnector {
    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
        try {
            SynapseLog log = getLog(messageContext);
            log.auditLog("Start : createSearch");
            NoteStoreClient noteStoreClient = EvernoteUtil.getNoteStoreClient(messageContext);
            SavedSearch savedSearch = new SavedSearch();

            String name = EvernoteUtil.lookupTemplateParamater(messageContext,EvernoteUtil.SEARCH_NAME);
            String query = EvernoteUtil.lookupTemplateParamater(messageContext,EvernoteUtil.QUERY);


            SavedSearchScope scope = new SavedSearchScope();

            String includeAccountStr = EvernoteUtil.lookupTemplateParamater(messageContext,"includeAccount");
            if(includeAccountStr!=null&&includeAccountStr.trim().equalsIgnoreCase("")&&(includeAccountStr.equalsIgnoreCase("true")||includeAccountStr.equalsIgnoreCase("false"))){
                boolean includeAccount = Boolean.parseBoolean(includeAccountStr);
                scope.setIncludeAccount(includeAccount);
            }

            String includePersonalLinkedNotebooksStr = EvernoteUtil.lookupTemplateParamater(messageContext,"includePersonalLinkedNotebooks");
            if(includePersonalLinkedNotebooksStr!=null&&includePersonalLinkedNotebooksStr.trim().equalsIgnoreCase("")&&(includePersonalLinkedNotebooksStr.equalsIgnoreCase("true")||includePersonalLinkedNotebooksStr.equalsIgnoreCase("false"))){
                boolean includePersonalLinkedNotebooks = Boolean.parseBoolean(includePersonalLinkedNotebooksStr);
                scope.setIncludePersonalLinkedNotebooks(includePersonalLinkedNotebooks);
            }

            String includeBusinessLinkedNotebooksStr = EvernoteUtil.lookupTemplateParamater(messageContext,"includeBusinessLinkedNotebooks");
            if(includeBusinessLinkedNotebooksStr!=null&&includeBusinessLinkedNotebooksStr.trim().equalsIgnoreCase("")&&(includeBusinessLinkedNotebooksStr.equalsIgnoreCase("true")||includeBusinessLinkedNotebooksStr.equalsIgnoreCase("false"))){
                boolean includeBusinessLinkedNotebooks = Boolean.parseBoolean(includeBusinessLinkedNotebooksStr);
                scope.setIncludePersonalLinkedNotebooks(includeBusinessLinkedNotebooks);
            }

            savedSearch.setName(name);
            savedSearch.setQuery(query);
            //set optional parameter

            savedSearch.setScope(scope);
            SavedSearch search = noteStoreClient.createSearch(savedSearch);
            OMElement omResponse = EvernoteUtil.parseResponse("create.search.success");
            EvernoteUtil.addElement(omResponse,"search-guid",search.getGuid());
            EvernoteUtil.preparePayload(messageContext,omResponse );
            log.auditLog("Stop : createSearch");
        }  catch (TException e) {
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
        }  catch (Exception e){
            log.error(e.getMessage());
            EvernoteUtil.handleException(e,"Invalid Input" ,"21", messageContext);
            throw new SynapseException(e);
        }

    }
}
