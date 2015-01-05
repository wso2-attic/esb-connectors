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

package org.wso2.carbon.connector.evernote.note;

import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Data;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;
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
import java.util.Arrays;
import java.util.Date;



public class UpdateNote extends AbstractConnector {
    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
        try {
            SynapseLog log = getLog(messageContext);
            log.auditLog("Start : UpdateNote");
            NoteStoreClient noteStoreClient = EvernoteUtil.getNoteStoreClient(messageContext);
            String title = EvernoteUtil.lookupTemplateParamater(messageContext, EvernoteUtil.NOTE_TITLE);
            String content = EvernoteUtil.lookupTemplateParamater(messageContext, EvernoteUtil.NOTE_CONTENT);
            String notebookGuid = EvernoteUtil.lookupTemplateParamater(messageContext, EvernoteUtil.NOTEBOOK_GUID);
            String sourceURL = EvernoteUtil.lookupTemplateParamater(messageContext, EvernoteUtil.SOURCE_URL);
            String mime = EvernoteUtil.lookupTemplateParamater(messageContext, EvernoteUtil.MIME);
            String fileName = EvernoteUtil.lookupTemplateParamater(messageContext, EvernoteUtil.FILE_NAME);
            String note_active = EvernoteUtil.lookupTemplateParamater(messageContext, EvernoteUtil.NOTE_ACTIVE);
            String noteGuid = EvernoteUtil.lookupTemplateParamater(messageContext, EvernoteUtil.NOTE_GUID);
            String tagName = EvernoteUtil.lookupTemplateParamater(messageContext, EvernoteUtil.TAG_NAME);
            boolean isResourceExists = false;
            Note note = noteStoreClient.getNote(noteGuid, true, true, true, true);

            Resource resource = new Resource();


            if (title != null && !title.trim().equalsIgnoreCase("") && noteGuid != null && !noteGuid.trim().equalsIgnoreCase("")) {

                if (fileName != null && !fileName.trim().equalsIgnoreCase("") && mime != null && !mime.trim().equalsIgnoreCase("") && sourceURL != null && !sourceURL.trim().equalsIgnoreCase("")) {
                    ResourceAttributes resourceAttributes = new ResourceAttributes();
                    resourceAttributes.setFileName(fileName);
                    resourceAttributes.setSourceURL(sourceURL);
                    resourceAttributes.setAttachment(true);


                    Data data = EvernoteUtil.readFileAsData(resourceAttributes.getSourceURL(), mime);
                    if (data == null) {
                        log.auditError("File format and mime type error");
                    }else {
                        resource.setAttributes(resourceAttributes);
                        resource.setData(data);
                        resource.setMime(mime);
                        note.addToResources(resource);
                        isResourceExists = true;
                    }
                }


                if (note_active.equalsIgnoreCase("true") || note_active.equalsIgnoreCase("false")) {
                    note.setActive(Boolean.parseBoolean(note_active.toLowerCase()));
                }

                if (notebookGuid != null && !notebookGuid.trim().equalsIgnoreCase("")) {
                    note.setNotebookGuid(notebookGuid);
                }
                note.setTitle(title);
                StringBuilder noteContent = new StringBuilder();

                noteContent.append("<?xml version=" + '"' + "1.0" + '"' + " encoding=" + '"' + "UTF-8" + '"' + "?>");
                noteContent.append("<!DOCTYPE en-note SYSTEM " + '"' + "http://xml.evernote.com/pub/enml2.dtd" + '"' + ">");
                noteContent.append("<en-note>");

                if (content != null && content.trim().equalsIgnoreCase("")) {
                    noteContent.append(content).append("<br></br>");
                }
                //check is there any resources exist

                if (isResourceExists || note.getResources() != null) {
                    for (Resource r : note.getResources()) {
                        String hexHash = EvernoteUtil.bytesToHex(r.getData().getBodyHash());
                        noteContent.append("<en-media type=" + '"').append(r.getMime());
                        noteContent.append('"');
                        noteContent.append(" hash=");
                        noteContent.append('"');
                        noteContent.append(hexHash);
                        noteContent.append('"');
                        noteContent.append("/>");
                    }
                }
                noteContent.append("</en-note>");
                note.setContent(noteContent.toString());


                if (tagName != null && !tagName.trim().equalsIgnoreCase("")) {
                    note.addToTagNames(tagName);
                }

                Note note1 = noteStoreClient.updateNote(note);
                OMElement omResponse = EvernoteUtil.parseResponse("note.update.success");
                OMElement omNoteMeta = EvernoteUtil.createOMElement("note-metadata");
                EvernoteUtil.addElement(omNoteMeta, "guid", note1.getGuid());
                EvernoteUtil.addElement(omNoteMeta, "contentHash", Arrays.toString(note1.getContentHash()));
                EvernoteUtil.addElement(omNoteMeta, "contentLength", note1.getContentLength() + "");
                EvernoteUtil.addElement(omNoteMeta, "created", new Date(note1.getCreated()).toString());
                EvernoteUtil.addElement(omNoteMeta, "updated", new Date(note1.getUpdated()).toString());
                EvernoteUtil.addElement(omNoteMeta, "active", note1.isActive() + "");
                EvernoteUtil.addElement(omNoteMeta, "updateSequenceNum", note1.getUpdateSequenceNum() + "");
                EvernoteUtil.addElement(omNoteMeta, "notebookGuid", note1.getNotebookGuid());
                EvernoteUtil.addElement(omNoteMeta, "attributes", note1.getAttributes().toString());
                omResponse.addChild(omNoteMeta);
                EvernoteUtil.preparePayload(messageContext, omResponse);
                log.auditLog("Stop : UpdateNote");
            }


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
