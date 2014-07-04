package org.wso2.carbon.connector.evernote.note;

import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.*;
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

public class CreateNote extends AbstractConnector {

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
        try {
            SynapseLog log = getLog(messageContext);
            log.auditLog("Start : createNote");
            String title = EvernoteUtil.lookupTemplateParamater(messageContext, EvernoteUtil.NOTE_TITLE);
            String content = EvernoteUtil.lookupTemplateParamater(messageContext, EvernoteUtil.NOTE_CONTENT);
            String notebookGuid = EvernoteUtil.lookupTemplateParamater(messageContext, EvernoteUtil.NOTEBOOK_GUID);
            String sourceURL = EvernoteUtil.lookupTemplateParamater(messageContext, EvernoteUtil.SOURCE_URL);
            String mime = EvernoteUtil.lookupTemplateParamater(messageContext, EvernoteUtil.MIME);
            String fileName = EvernoteUtil.lookupTemplateParamater(messageContext, EvernoteUtil.FILE_NAME);
            String tagName = EvernoteUtil.lookupTemplateParamater(messageContext, EvernoteUtil.TAG_NAME);

            NoteStoreClient noteStoreClient = EvernoteUtil.getNoteStoreClient(messageContext);
            String hexHash = null;
            Note note = new Note();
            note.setActive(true);
            note.setTitle(title);

            if(tagName!=null&&!tagName.trim().equalsIgnoreCase("")){
                note.addToTagNames(tagName);
            }

            //parentNotebook is optional;  if omitted default notebook is used
            if (notebookGuid != null&&!notebookGuid.trim().equals("")) {
                Notebook parentNotebook = noteStoreClient.getNotebook(notebookGuid);
                if (parentNotebook != null && parentNotebook.isSetGuid()) {
                    note.setGuid(parentNotebook.getGuid());
                }
            }



            String noteContent = "<?xml version=" + '"' + "1.0" + '"' + " encoding=" + '"' + "UTF-8" + '"' + "?>";
            noteContent += "<!DOCTYPE en-note SYSTEM " + '"' + "http://xml.evernote.com/pub/enml2.dtd" + '"' + ">";
            noteContent += "<en-note>";
            if (content != null && !content.trim().equalsIgnoreCase("")) {
                noteContent += content;
            }


            if (fileName != null && !fileName.trim().equalsIgnoreCase("") && sourceURL != null && !sourceURL.trim().equalsIgnoreCase("")) {
                ResourceAttributes resourceAttributes = new ResourceAttributes();
                resourceAttributes.setFileName(fileName);
                resourceAttributes.setSourceURL(sourceURL);
                resourceAttributes.setAttachment(true);

                Data data = EvernoteUtil.readFileAsData(resourceAttributes.getSourceURL(), mime);
                if (data == null) {
                    log.auditError("File format and mime type error");
                    //file format error
                } else {
                    Resource resource = new Resource();
                    resource.setAttributes(resourceAttributes);
                    resource.setData(data);
                    resource.setMime(mime);
                    hexHash = EvernoteUtil.bytesToHex(data.getBodyHash());
                    note.addToResources(resource);
                    noteContent += "<en-media type=" + '"' + resource.getMime() + '"' + " hash=" + '"' + hexHash + '"' + "/>";

                }

            }


            noteContent += "</en-note>";
            note.setContent(noteContent);

            //api call
            Note createdNote = noteStoreClient.createNote(note);

            OMElement omResponse = EvernoteUtil.parseResponse("note.create.success");
            OMElement omNote = EvernoteUtil.createOMElement("note");
            EvernoteUtil.addAttribute(omNote, "guid", createdNote.getGuid());
            EvernoteUtil.addElement(omNote, "title", note.getTitle());
            EvernoteUtil.addElement(omNote, "content", content);
            omResponse.addChild(omNote);
            OMElement omResource = EvernoteUtil.createOMElement("resource");

            if(mime!=null&&!mime.trim().equalsIgnoreCase("")) {
                EvernoteUtil.addElement(omResource, "mime", mime);
            }
            if(hexHash!=null) {
                EvernoteUtil.addElement(omResource, "hash", hexHash);
            }
            omResponse.addChild(omResource);
            EvernoteUtil.preparePayload(messageContext, omResponse);
            log.auditLog("Stop : createNote");

        }catch (TException e) {
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
        } catch (EDAMNotFoundException e) {
            log.error(e.getIdentifier());
            EvernoteUtil.handleException(e,e.getIdentifier() ,"22", messageContext);
            throw new SynapseException(e);
        } catch (IOException e) {
            log.error(e.getMessage());
            EvernoteUtil.handleException(e,"Invalid Input" ,"21", messageContext);
            throw new SynapseException(e);
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
            EvernoteUtil.handleException(e,"Invalid Input" ,"21", messageContext);
            throw new SynapseException(e);
        }catch (Exception e){
            log.error(e.getMessage());
            EvernoteUtil.handleException(e,"Invalid Input" ,"21", messageContext);
            throw new SynapseException(e);
        }


    }
}
