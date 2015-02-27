package org.wso2.carbon.connector.integration.test.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.TransportUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.mediation.library.stub.upload.MediationLibraryUploaderStub;
import org.wso2.carbon.mediation.library.stub.upload.types.carbon.LibraryFileItem;

public class ConnectorIntegrationUtil {

	public static final String ESB_CONFIG_LOCATION = "artifacts" + File.separator + "ESB" +
	                                                 File.separator + "config";

	private static final Log log = LogFactory.getLog(ConnectorIntegrationUtil.class);

	public static void uploadConnector(String repoLocation,
	                                   MediationLibraryUploaderStub mediationLibUploadStub,
	                                   String strFileName) throws MalformedURLException,
	                                                      RemoteException {

		List<LibraryFileItem> uploadLibraryInfoList = new ArrayList<LibraryFileItem>();
		LibraryFileItem uploadedFileItem = new LibraryFileItem();
		uploadedFileItem.setDataHandler(new DataHandler(new URL("file:" + "///" + repoLocation +
		                                                        "/" + strFileName)));
		uploadedFileItem.setFileName(strFileName);
		uploadedFileItem.setFileType("zip");
		uploadLibraryInfoList.add(uploadedFileItem);
		LibraryFileItem[] uploadServiceTypes = new LibraryFileItem[uploadLibraryInfoList.size()];
		uploadServiceTypes = uploadLibraryInfoList.toArray(uploadServiceTypes);
		mediationLibUploadStub.uploadLibrary(uploadServiceTypes);
	}

	public static Properties getConnectorConfigProperties(String connectorName) {

		String connectorConfigFile = null;
		ProductConstant.init();
		try {
			connectorConfigFile =
			                      ProductConstant.SYSTEM_TEST_SETTINGS_LOCATION + File.separator +
			                              "artifacts" + File.separator + "ESB" + File.separator +
			                              "connector" + File.separator + "config" + File.separator +
			                              connectorName + ".properties";
			File connectorPropertyFile = new File(connectorConfigFile);
			InputStream inputStream = null;
			if (connectorPropertyFile.exists()) {
				inputStream = new FileInputStream(connectorPropertyFile);
			}

			if (inputStream != null) {
				Properties prop = new Properties();
				prop.load(inputStream);
				inputStream.close();
				return prop;
			}

		} catch (IOException ignored) {
			log.error("automation.properties file not found, please check your configuration");
		}

		return null;
	}

	/**
	 * Method to build a MEP Client with an attachment in the method context.
	 * 
	 * @param endpoint
	 *            The endpoint to configure the client for.
	 * @param request
	 *            The request to add as a SOAP envelope
	 * @param attachmentDataHandler
	 *            The attachment to add to the message context.
	 * @param attachmentContentId
	 *            The content ID for the attachment.
	 * @return The built MEP Client
	 * @throws AxisFault
	 *             on failure to initialize the client.
	 */
	public static OperationClient buildMEPClientWithAttachment(EndpointReference endpoint,
	                                                           OMElement request,
	                                                           Map<String, DataHandler> attachmentMap)
	                                                                                                  throws AxisFault {

		ServiceClient serviceClient = new ServiceClient();

		Options serviceOptions = new Options();
		serviceOptions.setProperty(Constants.Configuration.ENABLE_SWA, Constants.VALUE_TRUE);
		serviceOptions.setTo(endpoint);
		serviceOptions.setAction("mediate");
		serviceClient.setOptions(serviceOptions);
		MessageContext messageContext = new MessageContext();

		SOAPEnvelope soapEnvelope = TransportUtils.createSOAPEnvelope(request);
		messageContext.setEnvelope(soapEnvelope);

		for (String contentId : attachmentMap.keySet()) {
			messageContext.addAttachment(contentId, attachmentMap.get(contentId));
		}

		OperationClient mepClient = serviceClient.createClient(ServiceClient.ANON_OUT_IN_OP);
		mepClient.addMessageContext(messageContext);
		return mepClient;
	}

	/**
	 * Method to build a MEP with a specified soap envelope.
	 * 
	 * @param endpoint
	 *            The endpoint to configure the client for.
	 * @param request
	 *            The request to add as a SOAP envelope
	 * @return The built MEP Client
	 * @throws AxisFault
	 *             on failure to initialize the client.
	 */
	public static OperationClient buildMEPClient(EndpointReference endpoint, OMElement request)
	                                                                                           throws AxisFault {

		ServiceClient serviceClient = new ServiceClient();

		Options serviceOptions = new Options();
		serviceOptions.setTo(endpoint);
		serviceOptions.setAction("mediate");
		serviceClient.setOptions(serviceOptions);
		MessageContext messageContext = new MessageContext();

		SOAPEnvelope soapEnvelope = TransportUtils.createSOAPEnvelope(request);
		messageContext.setEnvelope(soapEnvelope);
		OperationClient mepClient = serviceClient.createClient(ServiceClient.ANON_OUT_IN_OP);
		mepClient.addMessageContext(messageContext);
		return mepClient;
	}
}
