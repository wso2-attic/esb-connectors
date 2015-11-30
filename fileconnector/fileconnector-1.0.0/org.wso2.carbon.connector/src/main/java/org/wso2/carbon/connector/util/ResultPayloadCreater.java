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

package org.wso2.carbon.connector.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;

import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axis2.Constants;
import org.apache.axis2.builder.Builder;
import org.apache.axis2.builder.BuilderUtil;
import org.apache.axis2.format.DataSourceMessageBuilder;
import org.apache.axis2.format.ManagedDataSource;
import org.apache.axis2.format.ManagedDataSourceFactory;
import org.apache.axis2.transport.TransportUtils;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.SynapseException;
import org.apache.synapse.transport.passthru.util.BinaryRelayBuilder;
import org.codehaus.jettison.json.JSONException;


public class ResultPayloadCreater {

    private static final Log log = LogFactory.getLog(ResultPayloadCreater.class);
    
	/**
	 * Prepare pay load
	 * 
	 * @param messageContext
	 * @param element
	 */
	public void preparePayload(MessageContext messageContext, OMElement element) {
        SOAPBody soapBody = messageContext.getEnvelope().getBody();
        for (Iterator itr = soapBody.getChildElements(); itr.hasNext(); ) {
            OMElement child = (OMElement) itr.next();
            child.detach();
        }
        for (Iterator itr = element.getChildElements(); itr.hasNext(); ) {
            OMElement child = (OMElement) itr.next();
            soapBody.addChild(child);
        }
	}

	/**
	 * Create a OMElement
	 * 
	 * @param output
	 * @return
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws JSONException
	 */
	public OMElement performSearchMessages(String output) throws XMLStreamException,

	IOException, JSONException {
		OMElement resultElement;
		if (!output.equals("")) {
			resultElement = AXIOMUtil.stringToOM(output);
		} else {
			resultElement = AXIOMUtil.stringToOM("<result></></result>");
		}

		return resultElement;

	}

	/**
	 * Send error status
	 * 
	 * @param ctxt
	 * @param e
	 */

	public static void sendErrorStatus(MessageContext ctxt, Exception e) {
		ctxt.setProperty(SynapseConstants.ERROR_EXCEPTION, e);
		ctxt.setProperty(SynapseConstants.ERROR_MESSAGE, e.getMessage());
	}


    public static boolean buildFile(FileObject file, MessageContext msgCtx, String contentType, String streaming)
            throws SynapseException {

        ManagedDataSource dataSource = null;
        try {
            if (contentType == null || contentType.trim().equals("")) {
                if (file.getName().getExtension().toLowerCase().endsWith("xml")) {
                    contentType = "text/xml";
                } else if (file.getName().getExtension().toLowerCase().endsWith("txt")) {
                    contentType = "text/plain";
                }
            } else {
                // Extract the charset encoding from the configured content type and
                // set the CHARACTER_SET_ENCODING property as e.g. SOAPBuilder relies on this.
                String charSetEnc = null;
                try {
                    if (contentType != null) {
                        charSetEnc = new ContentType(contentType).getParameter("charset");
                    }
                } catch (ParseException ex) {
                    log.warn("Invalid encoding type.", ex);
                }
                msgCtx.setProperty(Constants.Configuration.CHARACTER_SET_ENCODING, charSetEnc);
            }
            if (log.isDebugEnabled()) {
                log.debug("Processed file : " + file + " of Content-type : " + contentType);
            }
            org.apache.axis2.context.MessageContext axis2MsgCtx = ((org.apache.synapse.core.axis2.Axis2MessageContext) msgCtx)
                    .getAxis2MessageContext();
            // Determine the message builder to use
            Builder builder;
            if (contentType == null) {
                log.debug("No content type specified. Using RELAY builder.");
                builder = new BinaryRelayBuilder();
            } else {
                int index = contentType.indexOf(';');
                String type = index > 0 ? contentType.substring(0, index) : contentType;
                builder = BuilderUtil.getBuilderFromSelector(type, axis2MsgCtx);
                if (builder == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("No message builder found for type '" + type + "'. Falling back to RELAY builder.");
                    }
                    builder = new BinaryRelayBuilder();
                }
            }

            // set the message payload to the message context
            InputStream in;
            if (builder instanceof DataSourceMessageBuilder && "true".equals(streaming)) {
                in = null;
                dataSource = ManagedDataSourceFactory.create(new FileObjectDataSource(file, contentType));
            } else {
                in = new AutoCloseInputStream(file.getContent().getInputStream());
                dataSource = null;
            }

            // Inject the message to the sequence.

            OMElement documentElement;
            if (in != null) {
                documentElement = builder.processDocument(in, contentType, axis2MsgCtx);
            } else {
                documentElement = ((DataSourceMessageBuilder) builder).processDocument(dataSource, contentType,
                        axis2MsgCtx);
            }
            
            //We need this to build the complete message before closing the stream
            documentElement.toString();
            
            msgCtx.setEnvelope(TransportUtils.createSOAPEnvelope(documentElement));

        } catch (SynapseException se) {
            throw se;
        } catch (Exception e) {
            log.error("Error while processing the file/folder", e);
            throw new SynapseException("Error while processing the file/folder", e);
        } finally {
            if (dataSource != null) {
                dataSource.destroy();
            }
        }
        return true;
    }
	
}
