package org.wso2.carbon.connector.evernote.util;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Data;
import com.evernote.thrift.TException;
import org.apache.axiom.om.*;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.wso2.carbon.connector.core.util.ConnectorUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Properties;

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

public class EvernoteUtil {

    public static final String NOTE_TITLE = "title";
    public static final String NOTE_CONTENT = "content";
    public static final String NOTEBOOK_GUID = "notebookGuid";
    public static final String SOURCE_URL ="sourceURL";
    public static final String MIME = "mime";
    public static final String FILE_NAME ="fileName";
    public static final String NOTE_GUID ="noteGuid";
    public static final String WITH_CONTENT = "withContent";
    public static final String WITH_RESOURCES_DATA = "withResourcesData";
    public static final String WITH_RESOURCES_RECOGNITION = "withResourcesRecognition";
    public static final String WITH_RESOURCES_ALTERNATE_DATA = "withResourcesAlternateData";
    public static final String NOTE_ACTIVE = "active";
    public static final String TAG_NAME = "tagName";
    public static final String TAG_PARENT_GUID = "parentGuid";
    public static final String TAG_GUID = "tagGuid";
    public static final String SEARCH_NAME = "searchName" ;
    public static final String QUERY = "query";
    public static final String SEARCH_GUID = "searchGuid";
    public static final String NOTEBOOK_NAME = "notebookName";
    public static final String NOTEBOOK_DEFAULT ="defaultNotebook" ;
    public static final String EMAIL = "email" ;
    public static final String SHARED_ID ="id" ;
    public static final String ALLOW_PREVIEW = "allowPreview";
    private static final OMFactory fac = OMAbstractFactory.getOMFactory();
    private static final OMNamespace omNs = fac.createOMNamespace("http://wso2.org/evernote/adaptor",
            "evernote");



    public static String lookupTemplateParamater(MessageContext ctxt, String paramName){
        return (String)ConnectorUtils.lookupTemplateParamater(ctxt,paramName);
    }

    public synchronized static NoteStoreClient getNoteStoreClient(MessageContext ctxt) throws TException, EDAMUserException, EDAMSystemException {
        Axis2MessageContext axis2mc = (Axis2MessageContext)ctxt;
        axis2mc.getAxis2MessageContext();
        String devToken = (String)axis2mc.getAxis2MessageContext().getOperationContext().getProperty("evernote.devToken");
        String noteStoreUrl = (String)axis2mc.getAxis2MessageContext().getOperationContext().getProperty("evernote.noteStoreUrl");
        String devTokenType = (String)axis2mc.getAxis2MessageContext().getOperationContext().getProperty("evernote.devTokenType");
        EvernoteAuth auth = null;


        if(devTokenType.equalsIgnoreCase("PRODUCTION")) {
            auth = new EvernoteAuth(EvernoteService.PRODUCTION, devToken);
        }
        else if(devTokenType.equalsIgnoreCase("YINXIANG")) {
            auth = new EvernoteAuth(EvernoteService.YINXIANG, devToken);
        }
        else if (devTokenType.equalsIgnoreCase("SANDBOX")){
            auth = new EvernoteAuth(EvernoteService.SANDBOX, devToken);
        }
        if (auth != null) {
            auth.setNoteStoreUrl(noteStoreUrl);
        }
        ClientFactory factory = new ClientFactory(auth);
        return factory.createNoteStoreClient();
    }

    public static OMElement parseResponse(String strMessageKey) {
        String strResponse = getMessage(strMessageKey);
        OMElement omElement = fac.createOMElement("response", omNs);
        OMElement subValue = fac.createOMElement("message", omNs);
        subValue.addChild(fac.createOMText(omElement, strResponse));
        omElement.addChild(subValue);
        return omElement;
    }

    private static String getMessage(String strMessageKey) {
        Properties prop = new Properties();
        try {
            prop.load(EvernoteUtil.class.getResourceAsStream("/messages/message.properties"));

            return (String) prop.getProperty(strMessageKey);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "Error getting the message for key:" + strMessageKey;
    }

    public static OMElement addElement(OMElement omElement, String strElement, String strValue) {
        OMElement subValue = fac.createOMElement(strElement, omNs);
        subValue.addChild(fac.createOMText(omElement, strValue));
        omElement.addChild(subValue);
        return omElement;
    }



    public static OMElement createOMElement(String strElement){
        return fac.createOMElement(strElement,omNs);
    }

    public static OMElement addAttribute(OMElement omElement,String name, String value){
        OMAttribute attribute = fac.createOMAttribute(name, null, value);
        omElement.addAttribute(attribute);
        return  omElement;
    }

    public static void preparePayload(MessageContext messageContext, OMElement element) {
        SOAPBody soapBody = messageContext.getEnvelope().getBody();
        for (Iterator itr = soapBody.getChildElements(); itr.hasNext();) {
            OMElement child = (OMElement) itr.next();
            child.detach();
        }
        for (Iterator itr = element.getChildElements(); itr.hasNext();) {
            OMElement child = (OMElement) itr.next();
            soapBody.addChild(child);
        }

    }

    public static Data readFileAsData(String locationURL,String mime) throws IOException, NoSuchAlgorithmException {
        URL url = new URL(locationURL);
        URLConnection connection = url.openConnection();

        if (!connection.getContentType().equalsIgnoreCase(mime)) {
            return null;
        }
        InputStream in = url.openStream();
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        byte[] block = new byte[10240];
        int len;
        while ((len = in.read(block)) >= 0) {
            byteOut.write(block, 0, len);
        }
        in.close();
        byte[] body = byteOut.toByteArray();

        Data data = new Data();
        data.setSize(body.length);
        data.setBodyHash(MessageDigest.getInstance("MD5").digest(body));
        data.setBody(body);
        return data;


    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte hashByte : bytes) {
            int intVal = 0xff & hashByte;
            if (intVal < 0x10) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(intVal));
        }
        return sb.toString();
    }

    public static String booleanToString(String boolVal){
        if(boolVal!=null) {
            if (boolVal.equalsIgnoreCase("true") || boolVal.equalsIgnoreCase("false")) {
                return boolVal.toLowerCase();
            }
        }
        return null;
    }

    public static void preparePayload(MessageContext messageContext, String errCode) {
        OMElement omElement = fac.createOMElement("error", omNs);
        OMElement subValue = fac.createOMElement("errorMessage", omNs);
        subValue.addChild(fac.createOMText(omElement, errCode));
        omElement.addChild(subValue);
        preparePayload(messageContext, omElement);
    }

    public static void handleException(Exception e,String message,String errCode, MessageContext messageContext){
        messageContext.setProperty(SynapseConstants.ERROR_EXCEPTION, e.toString());
        messageContext.setProperty(SynapseConstants.ERROR_MESSAGE, message);
        messageContext.setProperty(SynapseConstants.ERROR_CODE, errCode);
        preparePayload(messageContext, errCode);
    }


}
