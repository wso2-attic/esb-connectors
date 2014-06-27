package org.wso2.carbon.connector.util;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPBody;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.codehaus.jettison.json.JSONException;

public class ResultPayloadCreater {

	/**
	 * Prepare pay load
	 * 
	 * @param messageContext
	 * @param element
	 */
	public void preparePayload(MessageContext messageContext, OMElement element) {

		SOAPBody soapBody = messageContext.getEnvelope().getBody();
		soapBody.addChild(element);

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
}
