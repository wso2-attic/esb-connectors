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
package org.wso2.carbon.connector.evernote.note;

import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.NoteSortOrder;
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
import java.util.Date;
import java.util.List;



public class FindNotesMetaData extends AbstractConnector {
    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
        try {
            SynapseLog log = getLog(messageContext);
            log.auditLog("Start : findNotesMetaData");
            NoteStoreClient noteStoreClient = EvernoteUtil.getNoteStoreClient(messageContext);
            NoteFilter noteFilter = new NoteFilter();


            boolean ascending = false;
            String ascendingStr = EvernoteUtil.lookupTemplateParamater(messageContext, "ascending");
            if (ascendingStr != null && ascendingStr.trim().equalsIgnoreCase("")) {
                ascending = Boolean.parseBoolean(ascendingStr);
            }
            String notebookGuid = EvernoteUtil.lookupTemplateParamater(messageContext, EvernoteUtil.NOTEBOOK_GUID);
            String words = EvernoteUtil.lookupTemplateParamater(messageContext, "words");
            boolean inactive = false;
            String inactiveStr = EvernoteUtil.lookupTemplateParamater(messageContext, "inactive");
            if (inactiveStr != null && inactiveStr.trim().equalsIgnoreCase("")) {
                inactive = Boolean.parseBoolean(inactiveStr);
            }
            String emphasized = EvernoteUtil.lookupTemplateParamater(messageContext, "emphasized");
            //set NoteFilter
            noteFilter.setOrder(NoteSortOrder.CREATED.getValue());


            noteFilter.setNotebookGuid(notebookGuid);

            noteFilter.setAscending(ascending);

            noteFilter.setWords(words);


            noteFilter.setInactive(inactive);
            if (emphasized != null && !emphasized.equalsIgnoreCase("")) {
                noteFilter.setEmphasized(emphasized);
            }


            NotesMetadataResultSpec notesMetadataResultSpec = new NotesMetadataResultSpec();

            //set NotesMetadataResultSpec
            boolean includeTitle = false;
            String includeTitleStr = EvernoteUtil.lookupTemplateParamater(messageContext, "includeTitle");
            if (includeTitleStr != null) {
                includeTitle = Boolean.parseBoolean(includeTitleStr);
            }
            notesMetadataResultSpec.setIncludeTitle(includeTitle);


            boolean includeContentLength = false;
            String includeContentLengthStr = EvernoteUtil.lookupTemplateParamater(messageContext, "includeContentLength");
            if (includeContentLengthStr != null) {
                includeContentLength = Boolean.parseBoolean(includeContentLengthStr);
            }
            notesMetadataResultSpec.setIncludeContentLength(includeContentLength);


            boolean includeCreated = false;
            String includeCreatedStr = EvernoteUtil.lookupTemplateParamater(messageContext, "includeCreated");
            if (includeCreatedStr != null) {
                includeCreated = Boolean.parseBoolean(includeCreatedStr);
            }
            notesMetadataResultSpec.setIncludeCreated(includeCreated);


            boolean includeUpdated = false;
            String includeUpdatedStr = EvernoteUtil.lookupTemplateParamater(messageContext, "includeUpdated");
            if (includeUpdatedStr != null) {
                includeUpdated = Boolean.parseBoolean(includeUpdatedStr);
            }
            notesMetadataResultSpec.setIncludeUpdated(includeUpdated);


            boolean includeDeleted = false;
            String includeDeletedStr = EvernoteUtil.lookupTemplateParamater(messageContext, "includeDeleted");
            if (includeDeletedStr != null) {
                includeDeleted = Boolean.parseBoolean(includeDeletedStr);
            }
            notesMetadataResultSpec.setIncludeDeleted(includeDeleted);


            boolean includeUpdateSequenceNum = false;
            String includeUpdateSequenceNumStr = EvernoteUtil.lookupTemplateParamater(messageContext, "includeUpdateSequenceNum");
            if (includeUpdateSequenceNumStr != null) {
                includeUpdateSequenceNum = Boolean.parseBoolean(includeUpdateSequenceNumStr);
            }
            notesMetadataResultSpec.setIncludeUpdateSequenceNum(includeUpdateSequenceNum);


            boolean includeNotebookGuid = false;
            String includeNotebookGuidStr = EvernoteUtil.lookupTemplateParamater(messageContext, "includeNotebookGuid");
            if (includeNotebookGuidStr != null) {
                includeNotebookGuid = Boolean.parseBoolean(includeNotebookGuidStr);
            }
            notesMetadataResultSpec.setIncludeNotebookGuid(includeNotebookGuid);


            boolean includeTagGuids = false;
            String includeTagGuidsStr = EvernoteUtil.lookupTemplateParamater(messageContext, "includeTagGuids");
            if (includeTagGuidsStr != null) {
                includeTagGuids = Boolean.parseBoolean(includeTagGuidsStr);
            }
            notesMetadataResultSpec.setIncludeTagGuids(includeTagGuids);


            boolean includeAttributes = false;
            String includeAttributesStr = EvernoteUtil.lookupTemplateParamater(messageContext, "includeAttributes");
            if (includeAttributesStr != null) {
                includeAttributes = Boolean.parseBoolean(includeAttributesStr);
            }
            notesMetadataResultSpec.setIncludeAttributes(includeAttributes);


            boolean includeLargestResourceMime = false;
            String includeLargestResourceMimeStr = EvernoteUtil.lookupTemplateParamater(messageContext, "includeLargestResourceMime");
            if (includeLargestResourceMimeStr != null) {
                includeLargestResourceMime = Boolean.parseBoolean(includeLargestResourceMimeStr);
            }
            notesMetadataResultSpec.setIncludeLargestResourceMime(includeLargestResourceMime);


            boolean includeLargestResourceSize = false;
            String includeLargestResourceSizeStr = EvernoteUtil.lookupTemplateParamater(messageContext, "includeLargestResourceSize");
            if (includeLargestResourceSizeStr != null) {
                includeLargestResourceSize = Boolean.parseBoolean(includeLargestResourceSizeStr);
            }
            notesMetadataResultSpec.setIncludeLargestResourceSize(includeLargestResourceSize);


            String offSetStr = EvernoteUtil.lookupTemplateParamater(messageContext, "offset");
            String maxNotesStr = EvernoteUtil.lookupTemplateParamater(messageContext, "maxNotes");

            //<parameter name="includeCreated"/>
            if (offSetStr != null && !offSetStr.trim().equalsIgnoreCase("") && maxNotesStr != null && !maxNotesStr.trim().equalsIgnoreCase("")) {

                int offSet = Integer.parseInt(offSetStr);
                int maxNotes = Integer.parseInt(maxNotesStr);


                //api call
                NotesMetadataList notesMetadata = noteStoreClient.findNotesMetadata(noteFilter, offSet, maxNotes, notesMetadataResultSpec);
                OMElement omResponse = EvernoteUtil.parseResponse("find.notes.metadata");
                OMElement omMetaData = EvernoteUtil.createOMElement("notes-metadata");


                List<NoteMetadata> notes = notesMetadata.getNotes();

                for (NoteMetadata next : notes) {

                    OMElement omNote = EvernoteUtil.createOMElement("note");
                    next = notesMetadata.getNotesIterator().next();
                    EvernoteUtil.addAttribute(omNote, "guid", next.getGuid());
                    if (includeTitle) {
                        EvernoteUtil.addElement(omNote, "title", next.getTitle());
                    }
                    if (includeAttributes) {
                        EvernoteUtil.addElement(omNote, "attributes", next.getAttributes().toString());
                    }
                    if (includeContentLength) {
                        EvernoteUtil.addElement(omNote, "contentLength", next.getContentLength() + "");
                    }
                    if (includeCreated) {
                        EvernoteUtil.addElement(omNote, "created", new Date(next.getCreated()).toString());
                    }
                    if (includeDeleted) {
                        EvernoteUtil.addElement(omNote, "deleted", new Date(next.getDeleted()).toString());
                    }
                    if (includeLargestResourceMime) {
                        EvernoteUtil.addElement(omNote, "largestResourceMime", next.getLargestResourceMime());
                    }
                    if (includeLargestResourceSize) {
                        EvernoteUtil.addElement(omNote, "largestResourceMime", next.getLargestResourceSize() + "");
                    }
                    if (includeNotebookGuid) {
                        EvernoteUtil.addElement(omNote, "notebookGuid", next.getLargestResourceMime());
                    }
                    if (includeTagGuids) {
                        OMElement omTagGuids = EvernoteUtil.createOMElement("tagGuids");
                        for (String tagGuid : next.getTagGuids()) {
                            EvernoteUtil.addElement(omTagGuids, "tagGuid", tagGuid);
                        }
                        omNote.addChild(omTagGuids);
                    }
                    if (includeUpdated) {
                        EvernoteUtil.addElement(omNote, "updated", new Date(next.getUpdated()).toString());
                    }
                    if (includeUpdateSequenceNum) {
                        EvernoteUtil.addElement(omNote, "updateSequenceNumber", next.getUpdateSequenceNum() + "");
                    }
                    omMetaData.addChild(omNote);
                }
                omResponse.addChild(omMetaData);
                EvernoteUtil.preparePayload(messageContext, omResponse);
                log.auditLog("Stop : findNotesMetaData");
            }
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
        } catch (EDAMNotFoundException e) {
            log.error(e.getIdentifier());
            EvernoteUtil.handleException(e,e.getIdentifier() ,"22", messageContext);
            throw new SynapseException(e);
        } catch (Exception e){
            log.error(e.getMessage());
            EvernoteUtil.handleException(e,"Invalid Input" ,"21", messageContext);
            throw new SynapseException(e);
        }

    }
}
