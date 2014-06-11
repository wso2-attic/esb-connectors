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

package org.wso2.connector.integration.test.base;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.TransportUtils;
import org.apache.axis2.wsdl.WSDLConstants;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.w3c.dom.Document;
import org.wso2.carbon.automation.api.clients.proxy.admin.ProxyServiceAdminClient;
import org.wso2.carbon.automation.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.utils.axis2client.ConfigurationContextProvider;
import org.wso2.carbon.esb.ESBIntegrationTest;
import org.wso2.carbon.mediation.library.stub.MediationLibraryAdminServiceStub;
import org.wso2.carbon.mediation.library.stub.upload.MediationLibraryUploaderStub;
import org.wso2.carbon.mediation.library.stub.upload.types.carbon.LibraryFileItem;
import org.wso2.carbon.proxyadmin.stub.ProxyServiceAdminProxyAdminException;
import org.xml.sax.SAXException;

/**
 * This class contains the common methods which are used by the integration test classes of the different
 * connectors.
 */
public abstract class ConnectorIntegrationTestBase extends ESBIntegrationTest {
    
    private String connectorName;
    
    private static final float SLEEP_TIMER_PROGRESSION_FACTOR = 0.5f;
    
    private MediationLibraryUploaderStub mediationLibUploadStub;
    
    private MediationLibraryAdminServiceStub adminServiceStub;
    
    private String repoLocation;
    
    private ProxyServiceAdminClient proxyAdmin;
    
    protected Properties connectorProperties;
    
    private String pathToProxiesDirectory;
    
    private String pathToRequestsDirectory;
    
    protected String proxyUrl;
    
    protected String pathToResourcesDirectory;

    protected static final int MULTIPART_TYPE_RELATED = 100001;
    
    /**
     * Set up the integration test environment.
     * 
     * @param connectorName String Name of the connector.
     * @throws Exception
     */
    protected void init(String connectorName) throws Exception {
    
        super.init();
        this.connectorName = connectorName;
        
        ConfigurationContextProvider configurationContextProvider = ConfigurationContextProvider.getInstance();
        ConfigurationContext cc = configurationContextProvider.getConfigurationContext();
        
        mediationLibUploadStub =
                new MediationLibraryUploaderStub(cc, esbServer.getBackEndUrl() + "MediationLibraryUploader");
        AuthenticateStub.authenticateStub("admin", "admin", mediationLibUploadStub);
        
        adminServiceStub =
                new MediationLibraryAdminServiceStub(cc, esbServer.getBackEndUrl() + "MediationLibraryAdminService");
        
        AuthenticateStub.authenticateStub("admin", "admin", adminServiceStub);
        
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            repoLocation = System.getProperty("connector_repo").replace("\\", "/");
        } else {
            repoLocation = System.getProperty("connector_repo").replace("/", "/");
        }
        
        proxyAdmin = new ProxyServiceAdminClient(esbServer.getBackEndUrl(), esbServer.getSessionCookie());
        
        String connectorFileName = connectorName + ".zip";
        uploadConnector(repoLocation, mediationLibUploadStub, connectorFileName);
        byte maxAttempts = 3;
        int sleepTimer = 30000;
        for (byte attemptCount = 0; attemptCount < maxAttempts; attemptCount++) {
            log.info("Sleeping for " + sleepTimer / 1000 + " seconds for connector to upload.");
            Thread.sleep(sleepTimer);
            String[] libraries = adminServiceStub.getAllLibraries();
            if (Arrays.asList(libraries).contains("{org.wso2.carbon.connector}" + connectorName)) {
                break;
            } else {
                log.info("Connector upload incomplete. Waiting...");
                sleepTimer *= SLEEP_TIMER_PROGRESSION_FACTOR;
            }
            
        }
        
        adminServiceStub.updateStatus("{org.wso2.carbon.connector}" + connectorName, connectorName,
                "org.wso2.carbon.connector", "enabled");
        
        connectorProperties = getConnectorConfigProperties(connectorName);
        
        pathToProxiesDirectory = repoLocation + connectorProperties.getProperty("proxyDirectoryRelativePath");
        pathToRequestsDirectory = repoLocation + connectorProperties.getProperty("requestDirectoryRelativePath");
        
        pathToResourcesDirectory = repoLocation + connectorProperties.getProperty("resourceDirectoryRelativePath");
        
        File folder = new File(pathToProxiesDirectory);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String fileName = listOfFiles[i].getName();
                if (fileName.endsWith(".xml") || fileName.endsWith(".XML")) {
                    proxyAdmin
                            .addProxyService(new DataHandler(new URL("file:///" + pathToProxiesDirectory + fileName)));
                }
            }
        }
        
        proxyUrl = getProxyServiceURL(connectorName);
        
    }
    
    /**
     * Clean up the ESB.
     * 
     * @throws ProxyServiceAdminProxyAdminException
     * @throws RemoteException @
     */
    @AfterClass(alwaysRun = true)
    public void cleanUpEsb() throws RemoteException, ProxyServiceAdminProxyAdminException {
    
        proxyAdmin.deleteProxy(connectorName);
    }
    
    /**
     * Overloaded method for sendRestRequest, where the request file is not necessary.
     * 
     * @param endPoint String End point URL.
     * @param httpMethod String HTTP method type (GET, POST, PUT etc.).
     * @param headersMap Map<String, String> Headers need to send to the end point .
     * @return RestResponse object.
     * @throws JSONException
     * @throws IOException
     */
    protected RestResponse<JSONObject> sendJsonRestRequest(String endPoint, String httpMethod,
            Map<String, String> headersMap) throws IOException, JSONException {
    
        return this.sendJsonRestRequest(endPoint, httpMethod, headersMap, null, null);
    }
    
    /**
     * @param endPoint
     * @param httpMethod
     * @param headersMap
     * @return
     * @throws XMLStreamException
     * @throws IOException
     */
    protected RestResponse<OMElement> sendXmlRestRequest(String endPoint, String httpMethod,
            Map<String, String> headersMap) throws IOException, XMLStreamException {
    
        return this.sendXmlRestRequest(endPoint, httpMethod, headersMap, null, null);
    }
    
    /**
     * Overloaded method for sendRestRequest, where parameter replacement is not necessary.
     * 
     * @param endPoint String End point URL.
     * @param httpMethod String HTTP method type (GET, POST, PUT etc.)
     * @param headersMap Map<String, String> Headers need to send to the end point.
     * @param requestFileName String File name of the file which contains request body data.
     * @return RestResponse object.
     * @throws JSONException
     * @throws IOException
     */
    protected RestResponse<JSONObject> sendJsonRestRequest(String endPoint, String httpMethod,
            Map<String, String> headersMap, String requestFileName) throws IOException, JSONException {
    
        return this.sendJsonRestRequest(endPoint, httpMethod, headersMap, requestFileName, null);
    }
    
    /**
     * @param endPoint
     * @param httpMethod
     * @param headersMap
     * @param requestFileName
     * @return
     * @throws XMLStreamException
     * @throws IOException
     */
    protected RestResponse<OMElement> sendXmlRestRequest(String endPoint, String httpMethod,
            Map<String, String> headersMap, String requestFileName) throws IOException, XMLStreamException {
    
        return this.sendXmlRestRequest(endPoint, httpMethod, headersMap, requestFileName, null);
    }
    
    /**
     * @param endPoint
     * @param httpMethod
     * @param headersMap
     * @param requestFileName
     * @param parametersMap
     * @return
     * @throws IOException
     * @throws JSONException @
     */
    protected RestResponse<JSONObject> sendJsonRestRequest(String endPoint, String httpMethod,
            Map<String, String> headersMap, String requestFileName, Map<String, String> parametersMap)
            throws IOException, JSONException {
    
        HttpURLConnection httpConnection =
                writeRequest(endPoint, httpMethod, RestResponse.JSON_TYPE, headersMap, requestFileName, parametersMap);
        
        String responseString = readResponse(httpConnection);
        
        RestResponse<JSONObject> restResponse = new RestResponse<JSONObject>();
        restResponse.setHttpStatusCode(httpConnection.getResponseCode());
        restResponse.setHeadersMap(httpConnection.getHeaderFields());
        
        if (responseString != null) {
            JSONObject jsonObject = null;
            if (isValidJSON(responseString)) {
                jsonObject = new JSONObject(responseString);
            } else {
                jsonObject = new JSONObject();
                jsonObject.put("output", responseString);
            }
            
            restResponse.setBody(jsonObject);
        }
        
        return restResponse;
    }
    
    /**
     * @param endPoint
     * @param httpMethod
     * @param headersMap
     * @param requestFileName
     * @param parametersMap
     * @return
     * @throws IOException
     * @throws XMLStreamException @
     */
    protected RestResponse<OMElement> sendXmlRestRequest(String endPoint, String httpMethod,
            Map<String, String> headersMap, String requestFileName, Map<String, String> parametersMap)
            throws IOException, XMLStreamException {
    
        HttpURLConnection httpConnection =
                writeRequest(endPoint, httpMethod, RestResponse.XML_TYPE, headersMap, requestFileName, parametersMap);
        
        String responseString = readResponse(httpConnection);
        
        RestResponse<OMElement> restResponse = new RestResponse<OMElement>();
        restResponse.setHttpStatusCode(httpConnection.getResponseCode());
        restResponse.setHeadersMap(httpConnection.getHeaderFields());
        
        if (responseString != null) {
            restResponse.setBody(AXIOMUtil.stringToOM(responseString));
        }
        
        return restResponse;
    }
    
    /**
     * Send a SOAP request via a MEPClient without attachments and parameters.
     * 
     * @param endpoint The URL of the endpoint to send the request to.
     * @param soapRequestFileName Path to the SOAP request file
     * @return The SOAP Envelope that is returned as a result of the request.
     * @throws XMLStreamException Thrown on failure to build OM Element from the file's contents.
     * @throws IOException Thrown on failure to send the request.
     */
    protected SOAPEnvelope sendSOAPRequest(String endpoint, String soapRequestFileName) throws XMLStreamException,
            IOException {
    
        return sendSOAPRequest(endpoint, soapRequestFileName, null);
    }
    
    /**
     * Send a SOAP request via a MEPClient without attachments.
     * 
     * @param endpoint The URL of the endpoint to send the request to.
     * @param soapRequestFileName Path to the SOAP request file
     * @param parametersMap A map containing key value pairs to be parameterized in the request.
     * @return The SOAP Envelope that is returned as a result of the request.
     * @throws XMLStreamException Thrown on failure to build OM Element from the file's contents.
     * @throws IOException Thrown on failure to send the request.
     */
    protected SOAPEnvelope sendSOAPRequest(String endpoint, String soapRequestFileName,
            Map<String, String> parametersMap) throws XMLStreamException, IOException {
    
        OMElement requestEnvelope = AXIOMUtil.stringToOM(loadRequestFromFile(soapRequestFileName, parametersMap));
        OperationClient mepClient =
                buildMEPClient(new EndpointReference(getProxyServiceURL(endpoint)), requestEnvelope);
        
        mepClient.execute(true);
        return mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE).getEnvelope();
    }
    
    /**
     * Send a SOAP request via a MEPClient without attachments.
     * 
     * @param endpoint The URL of the end point to send the request to.
     * @param soapRequestFileName Path to the SOAP request file
     * @param parametersMap A map containing key value pairs to be parameterized in the request.
     * @param attachmentMap A map containing content IDs and paths to files which needed to be added to the
     *        SOAP request as attachments.
     * @return The SOAP Envelope that is returned as a result of the request.
     * @throws XMLStreamException Thrown on failure to build OM Element from the file's contents.
     * @throws IOException Thrown on failure to send the request.
     */
    protected SOAPEnvelope sendSOAPRequest(String endpoint, String soapRequestFileName,
            Map<String, String> parametersMap, Map<String, String> attachmentMap) throws XMLStreamException,
            IOException {
    
        OMElement requestEnvelope = AXIOMUtil.stringToOM(loadRequestFromFile(soapRequestFileName, parametersMap));
        Map<String, DataHandler> dataHandlerMap = new HashMap<String, DataHandler>();
        for (String contentId : attachmentMap.keySet()) {
            dataHandlerMap.put(contentId, new DataHandler(new FileDataSource(new File(pathToRequestsDirectory
                    + attachmentMap.get(contentId)))));
        }
        OperationClient mepClient =
                buildMEPClient(new EndpointReference(getProxyServiceURL(endpoint)), requestEnvelope, dataHandlerMap);
        
        mepClient.execute(true);
        return mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE).getEnvelope();
    }
    
    /**
     * Send REST request and return RestResponse object.
     * 
     * @param endPoint String End point URL.
     * @param httpMethod String HTTP method type (GET, POST, PUT etc.)
     * @param headersMap Map<String, String> Headers need to send to the end point.
     * @param requestFileName String File name of the file which contains request body data.
     * @param parametersMap Map<String, String> Additional parameters which is not predefined in the
     *        properties file.
     * @return RestResponse object.
     * @throws IOException @
     */
    private HttpURLConnection writeRequest(String endPoint, String httpMethod, byte responseType,
            Map<String, String> headersMap, String requestFileName, Map<String, String> parametersMap)
            throws IOException {
    
        String requestData = "";
        
        if (requestFileName != null && !requestFileName.isEmpty()) {
            
            requestData = loadRequestFromFile(requestFileName, parametersMap);
            
        } else if (responseType == RestResponse.JSON_TYPE) {
            requestData = "{}";
        }
        OutputStream output = null;
        
        URL url = new URL(endPoint);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setRequestMethod(httpMethod);
        
        for (String key : headersMap.keySet()) {
            httpConnection.setRequestProperty(key, headersMap.get(key));
        }
        
        if (httpMethod.equalsIgnoreCase("POST") || httpMethod.equalsIgnoreCase("PUT")) {
            httpConnection.setDoOutput(true);
            try {
                
                output = httpConnection.getOutputStream();
                output.write(requestData.getBytes(Charset.defaultCharset()));
                
            } finally {
                
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException logOrIgnore) {
                        log.error("Error while closing the connection");
                    }
                }
                
            }
        }
        
        return httpConnection;
    }
    
    /**
     * Load a request from a file, provided by a filename.
     * 
     * @param requestFileName The name of the file to load the request from.
     * @param parametersMap Map of parameters to replace within the parametrized values of the request.
     * @return String contents of the file.
     * @throws IOException Thrown on inability to read from the file.
     */
    private String loadRequestFromFile(String requestFileName, Map<String, String> parametersMap) throws IOException {
    
        String requestFilePath;
        String requestData;
        requestFilePath = pathToRequestsDirectory + requestFileName;
        requestData = getFileContent(requestFilePath);
        Properties prop = (Properties) connectorProperties.clone();
        
        if (parametersMap != null) {
            prop.putAll(parametersMap);
        }
        
        Matcher matcher = Pattern.compile("%s\\(([A-Za-z0-9]*)\\)", Pattern.DOTALL).matcher(requestData);
        while (matcher.find()) {
            String key = matcher.group(1);
            requestData = requestData.replaceAll("%s\\(" + key + "\\)", Matcher.quoteReplacement(prop.getProperty(key)));
        }
        return requestData;
    }
    
    /**
     * @param con
     * @return
     * @throws IOException
     */
    private String readResponse(HttpURLConnection con) throws IOException {
    
        InputStream responseStream = null;
        String responseString = null;
        
        if (con.getResponseCode() >= 400) {
            responseStream = con.getErrorStream();
        } else {
            responseStream = con.getInputStream();
        }
        
        if (responseStream != null) {
            
            StringBuilder stringBuilder = new StringBuilder();
            byte[] bytes = new byte[1024];
            int len;
            
            while ((len = responseStream.read(bytes)) != -1) {
                stringBuilder.append(new String(bytes, 0, len));
            }
            
            if (!stringBuilder.toString().trim().isEmpty()) {
                responseString = stringBuilder.toString();
            }
            
        }
        
        return responseString;
    }
    
    /**
     * Overloaded method for {@link #loadObjectFromFile(String, Map) loadObjectFromFile} without parameter
     * map.
     * 
     * @param filePath file name including path to the XML serialized file.
     * @return the de-serialized object, user can cast this to the object type specified in the XML.
     * @throws IOException if file path is null or empty as well as if there's any exception while reading the
     *         XML file.
     * @see #loadObjectFromFile(String, Map)
     */
    
    protected Object loadObjectFromFile(String fileName) throws IOException {
    
        return this.loadObjectFromFile(fileName, null);
        
    }
    
    /**
     * This method de-serialize XML object graph. In addition if there are parameterized strings such as
     * <code>%s(accessToken)</code> in the XML file, those will be parsed and and replace with the values
     * specified in the Connection Properties resource file or the parameter map passed in to this method. <br>
     * <br>
     * <b>Example XML file.</b><br>
     * 
     * <pre>
     * {@code
     * <?xml version="1.0" encoding="UTF-8"?> 
     * <java class="java.beans.XMLDecoder"> 
     *  <object class="test.base.Person"> 
     *   <void property="address"> 
     *    <object class="test.base.Address"> 
     *     <void property="city"> 
     *      <string>Test City</string> 
     *     </void> 
     *     <void property="country"> 
     *      <string>Test Country</string> 
     *     </void> 
     *     <void property="street"> 
     *      <string>Test street</string> 
     *     </void> 
     *    </object> 
     *   </void> 
     *  <void property="age"> 
     *    <int>20</int> 
     *   </void> 
     *   <void property="name"> 
     *    <string>Test Person Name</string> 
     *   </void> 
     *  </object> 
     * </java>
     *  }
     * </pre>
     * 
     * @param filePath file name including path to the XML serialized file.
     * @param paramMap map containing key value pairs where key being the parameter specified in the XML file
     *        if parameter in XML is <code>%s(accessToken)</code>, the key should be just
     *        <code>accessToken</code>.
     * @return the de-serialized object, user can cast this to the object type specified in the XML.
     * @throws IOException if file path is null or empty as well as if there's any exception while reading the
     *         XML file.
     */
    protected Object loadObjectFromFile(String fileName, Map<String, String> paramMap) throws IOException {
    
        String filePath = pathToRequestsDirectory + fileName;
        if (filePath == null || filePath.isEmpty()) {
            throw new IOException("File path cannot be null or empty.");
        }
        
        Object retObj = null;
        BufferedInputStream bi = null;
        XMLDecoder decoder = null;
        
        try {
            bi = new BufferedInputStream(new FileInputStream(filePath));
            byte[] buf = new byte[bi.available()];
            bi.read(buf);
            String content = new String(buf);
            
            if (connectorProperties != null) {
                // We don't need to change the original connection properties in case same key is sent with
                // different value.
                Properties prop = (Properties) connectorProperties.clone();
                if (paramMap != null) {
                    prop.putAll(paramMap);
                }
                Matcher matcher = Pattern.compile("%s\\(([A-Za-z0-9]*)\\)", Pattern.DOTALL).matcher(content);
                while (matcher.find()) {
                    String key = matcher.group(1);
                    content = content.replaceAll("%s\\(" + key + "\\)", Matcher.quoteReplacement(prop.getProperty(key)));
                }
            }
            
            ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes(Charset.defaultCharset()));
            
            decoder = new XMLDecoder(in);
            retObj = decoder.readObject();
        } finally {
            if (bi != null) {
                bi.close();
            }
            if (decoder != null) {
                decoder.close();
            }
        }
        
        return retObj;
    }
    
    /**
     * Upload the given connector.
     * 
     * @param repoLocation
     * @param mediationLibUploadStub
     * @param strFileName
     * @throws MalformedURLException
     * @throws RemoteException
     */
    private void uploadConnector(String repoLocation, MediationLibraryUploaderStub mediationLibUploadStub,
            String strFileName) throws MalformedURLException, RemoteException {
    
        List<LibraryFileItem> uploadLibraryInfoList = new ArrayList<LibraryFileItem>();
        LibraryFileItem uploadedFileItem = new LibraryFileItem();
        uploadedFileItem.setDataHandler(new DataHandler(new URL("file:" + "///" + repoLocation + "/" + strFileName)));
        uploadedFileItem.setFileName(strFileName);
        uploadedFileItem.setFileType("zip");
        uploadLibraryInfoList.add(uploadedFileItem);
        LibraryFileItem[] uploadServiceTypes = new LibraryFileItem[uploadLibraryInfoList.size()];
        uploadServiceTypes = uploadLibraryInfoList.toArray(uploadServiceTypes);
        mediationLibUploadStub.uploadLibrary(uploadServiceTypes);
        
    }
    
    /**
     * Method to build a MEP Client with an attachment in the method context.
     * 
     * @param endpoint The endpoint to configure the client for.
     * @param request The request to add as a SOAP envelope
     * @param attachmentDataHandler The attachment to add to the message context.
     * @param attachmentContentId The content ID for the attachment.
     * @return The built MEP Client
     * @throws AxisFault on failure to initialize the client.
     */
    private OperationClient buildMEPClient(EndpointReference endpoint, OMElement request,
            Map<String, DataHandler> attachmentMap) throws AxisFault {
    
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
     * @param endpoint The endpoint to configure the client for.
     * @param request The request to add as a SOAP envelope
     * @return The built MEP Client
     * @throws AxisFault on failure to initialize the client.
     */
    private OperationClient buildMEPClient(EndpointReference endpoint, OMElement request) throws AxisFault {
    
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
    
    /**
     * Get connector configuration properties.
     * 
     * @param connectorName
     * @return
     */
    private Properties getConnectorConfigProperties(String connectorName) {
    
        String connectorConfigFile = null;
        ProductConstant.init();
        try {
            connectorConfigFile =
                    ProductConstant.SYSTEM_TEST_SETTINGS_LOCATION + File.separator + "artifacts" + File.separator
                            + "ESB" + File.separator + "connector" + File.separator + "config" + File.separator
                            + connectorName + ".properties";
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
     * Method to read in contents of a file as String
     * 
     * @param path
     * @return String contents of file
     * @throws IOException
     */
    private String getFileContent(String path) throws IOException {
    
        String fileContent = null;
        BufferedInputStream bfist = new BufferedInputStream(new FileInputStream(path));
        
        try {
            byte[] buf = new byte[bfist.available()];
            bfist.read(buf);
            fileContent = new String(buf);
        } catch (IOException ioe) {
            log.error("Error reading request from file.", ioe);
        } finally {
            if (bfist != null) {
                bfist.close();
            }
        }
        
        return fileContent;
        
    }
    
    /**
     * Method to validate whether incomming string is parsable as JSON.
     * 
     * @param json
     * @return boolean
     */
    private boolean isValidJSON(String json) {
    
        try {
            new JSONObject(json);
            return true;
        } catch (JSONException ex) {
            return false;
        }
    }
    
    @Override
    protected void cleanup() {
    
        axis2Client.destroy();
    }
    
    /**
     * Gets the element value by xpath expression.
     * 
     * @param xPathExpression the x path expression
     * @param element the element
     * @return the element by expression
     * @throws XMLStreamException the xML stream exception
     * @throws SAXException the sAX exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ParserConfigurationException the parser configuration exception
     * @throws XPathExpressionException the x path expression exception
     */
    public String getValueByExpression(String xPathExpression, OMElement element) throws XMLStreamException,
            SAXException, IOException, ParserConfigurationException, XPathExpressionException {
    
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(new ByteArrayInputStream(element.toString().getBytes()));
        XPath xPath = XPathFactory.newInstance().newXPath();
        return xPath.compile(xPathExpression).evaluate(xmlDocument);
    }
    
    protected InputStream processForInputStream(String endPoint, String httpMethod, Map<String, String> headersMap,
            String requestFileName, Map<String, String> parametersMap) throws IOException, JSONException {
    
        HttpURLConnection httpConnection =
                writeRequest(endPoint, httpMethod, RestResponse.JSON_TYPE, headersMap, requestFileName, parametersMap);
        
        InputStream responseStream = null;
        
        if (httpConnection.getResponseCode() >= 400) {
            responseStream = httpConnection.getErrorStream();
        } else {
            responseStream = httpConnection.getInputStream();
        }
        return responseStream;
    }
    
    /**
     * Inner class to handle Multipart data
     */
    protected class MultipartFormdataProcessor {
        
        private final String boundary = "----=_wso2IntegTest" + System.currentTimeMillis();
        
        OutputStream httpStream;
        
        HttpURLConnection httpURLConnection;
        
        final String LINE_FEED = "\r\n";
        
        public MultipartFormdataProcessor(String endPointUrl) throws IOException {
        
            init(endPointUrl, Charset.defaultCharset().toString(), null);
        }
        
        public MultipartFormdataProcessor(String endPointUrl, Map<String, String> httpHeaders) throws IOException {
        
            init(endPointUrl, Charset.defaultCharset().toString(), httpHeaders);
        }
        
        public MultipartFormdataProcessor(String endPointUrl, String charSet, Map<String, String> httpHeaders)
                throws IOException {
        
            init(endPointUrl, charSet, httpHeaders);
        }
        
        public MultipartFormdataProcessor(String endPointUrl, String Charset) throws IOException {
        
            init(endPointUrl, Charset, null);
        }
        
        public MultipartFormdataProcessor(String endPointUrl, Map<String, String> httpHeaders,
                int multipartType) throws IOException {
        
            init(endPointUrl, httpHeaders, multipartType);
        }
        
        private void init(String endPointUrl, String Charset, Map<String, String> httpHeaders) throws IOException {
        
            URL endpoint;
            
            endpoint = new URL(endPointUrl);
            
            httpURLConnection = (HttpURLConnection) endpoint.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + "\"" + boundary
                    + "\"");
            httpURLConnection.setRequestProperty("User-Agent", "Wso2ESB intergration test");
            // httpURLConnection.setRequestProperty("Content-Length", 332);
            // check for custom headers
            
            if (httpHeaders != null && !httpHeaders.isEmpty()) {
                Set<String> headerKeys = httpHeaders.keySet();
                String key = null;
                String value = null;
                for (Iterator<String> i = headerKeys.iterator(); i.hasNext();) {
                    key = i.next();
                    value = httpHeaders.get(key);
                    httpURLConnection.setRequestProperty(key, value);
                    
                }
            }
            
            httpStream = httpURLConnection.getOutputStream();
            
        }
        
        private void init(String endPointUrl, Map<String, String> httpHeaders, int multipartType)
                throws IOException {
        
            URL endpoint;
            
            endpoint = new URL(endPointUrl);
            
            httpURLConnection = (HttpURLConnection) endpoint.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            switch (multipartType) {
                case MULTIPART_TYPE_RELATED:
                    httpURLConnection.setRequestProperty("Content-Type", "multipart/related; boundary=" + "\""
                            + boundary + "\"");
                    break;
                default:
                    httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + "\""
                            + boundary + "\"");
            }
            
            httpURLConnection.setRequestProperty("User-Agent", "Wso2ESB intergration test");
            // httpURLConnection.setRequestProperty("Content-Length", 332);
            // check for custom headers
            
            if (httpHeaders != null && !httpHeaders.isEmpty()) {
                //remove content type header as we have already set it
                httpHeaders.remove("Content-Type");
                Set<String> headerKeys = httpHeaders.keySet();
                String key = null;
                String value = null;
                for (Iterator<String> i = headerKeys.iterator(); i.hasNext();) {
                    key = i.next();
                    value = httpHeaders.get(key);
                    httpURLConnection.setRequestProperty(key, value);
                    
                }
            }
            
            httpStream = httpURLConnection.getOutputStream();
            
        }
        
        public void addMetadataToMultipartRelatedRequest(String filename, String contentType, String charset,
                Map<String, String> parametersMap) throws IOException {
        
            StringBuilder builder = new StringBuilder();
            
            builder.append(LINE_FEED);
            builder.append("--").append(boundary).append(LINE_FEED);
            
            builder.append("Content-Type: " + contentType + "; charset=" + charset).append(LINE_FEED)
                                .append(LINE_FEED);
            
            builder.append(loadRequestFromFile(filename, parametersMap));
            
            builder.append(LINE_FEED);
            
            httpStream.write(builder.toString().getBytes());
            httpStream.flush();
        }
        
        public void addFileToMultipartRelatedRequest(String fileName, String contentId)
                throws IOException {
        
            File file = null;
            
            InputStream inputStream = null;
            try {
                
                fileName = pathToResourcesDirectory + fileName;
                
                file = new File(fileName);
                String contentType;
                
                inputStream = new FileInputStream(file);
                contentType = HttpURLConnection.guessContentTypeFromName(fileName);
                inputStream.close();
                
                addFileToMultipartRelatedRequest(fileName, file, contentType, contentId);
                
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                
            }
        }
        
        public void addFileToMultipartRelatedRequest(String fileName, File file, 
                String contentType, String contentId) throws IOException {
            FileInputStream inputStream = null;
            try {
                StringBuilder builder = new StringBuilder();
                
                builder.append("--").append(boundary).append(LINE_FEED);
                
                builder.append(
                        "Content-Disposition: attachment; filename=\"" + fileName + "\"")
                        .append(LINE_FEED);

                builder.append("Content-Type: " + contentType).append(LINE_FEED);
                builder.append("content-id: <" + contentId + ">").append(LINE_FEED).append(LINE_FEED);
                
                httpStream.write(builder.toString().getBytes());
                
                httpStream.flush();
                
                // process File
                inputStream = new FileInputStream(file);
                byte[] buffer = new byte[10485760];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    httpStream.write(buffer, 0, bytesRead);
                }
                httpStream.flush();
                inputStream.close();
                httpStream.write(LINE_FEED.getBytes());
                httpStream.flush();
                
            } finally {
                if (inputStream != null)
                    inputStream.close();
            }
        }
        
        public void addFormDataToRequest(String fieldName, String fieldValue) throws IOException {
        
            addFormDataToRequest(fieldName, fieldValue, Charset.defaultCharset().toString());
        }
        
        public void addFormDataToRequest(String fieldName, String fieldValue, String charset) throws IOException {
        
            StringBuilder builder = new StringBuilder();
            
            builder.append(LINE_FEED);
            builder.append("--").append(boundary).append(LINE_FEED);
            
            builder.append("Content-Type: text/plain; charset=" + charset).append(LINE_FEED);
            builder.append("Content-Disposition: form-data; name=\"" + fieldName + "\"").append(LINE_FEED);
            
            builder.append(LINE_FEED);
            builder.append(fieldValue).append(LINE_FEED);
            
            httpStream.write(builder.toString().getBytes());
            httpStream.flush();
        }
        
        public void addFileToRequest(String fieldName, String fileName, String contentType) throws IOException {
        
            fileName = pathToResourcesDirectory + fileName;
            File file = new File(fileName);
            addFileToRequest(fieldName, file, contentType);
        }
        
        public void addFileToRequest(String fieldName, String fileName, String contentType, String targetFileName)
                throws IOException {
        
            fileName = pathToResourcesDirectory + fileName;
            File file = new File(fileName);
            if (contentType == null) {
                contentType = URLConnection.guessContentTypeFromName(file.getName());
            }
            addFileToRequest(fieldName, file, contentType, targetFileName);
        }
        
        public void addFileToRequest(String fieldName, String fileName) throws IOException {
        
            File file = null;
            
            InputStream inputStream = null;
            try {
                
                fileName = pathToResourcesDirectory + fileName;
                
                file = new File(fileName);
                String contentType;
                
                inputStream = new FileInputStream(file);
                contentType = HttpURLConnection.guessContentTypeFromStream(inputStream);
                inputStream.close();
                
                addFileToRequest(fieldName, file, contentType);
                
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                
            }
        }
        
        public void addFileToRequest(String fieldName, File file) throws IOException {
        
            InputStream inputStream = null;
            try {
                String contentType;
                
                inputStream = new FileInputStream(file);
                contentType = HttpURLConnection.guessContentTypeFromStream(inputStream);
                inputStream.close();
                
                addFileToRequest(fieldName, file, contentType);
                
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                
            }
            
        }
        
        public void addFileToRequest(String fieldName, File file, String contentType, String fileName)
                throws IOException {
        
            FileInputStream inputStream = null;
            try {
                StringBuilder builder = new StringBuilder();
                
                builder.append("--").append(boundary).append(LINE_FEED);
                
                builder.append(
                        "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"")
                        .append(LINE_FEED);
                /*
                 * builder.append( "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" +
                 * "file" + "\"").append(LINE_FEED);
                 */
                builder.append("Content-Type: " + contentType).append(LINE_FEED);
                builder.append("Content-Transfer-Encoding: binary").append(LINE_FEED).append(LINE_FEED);
                
                httpStream.write(builder.toString().getBytes());
                
                httpStream.flush();
                
                // process File
                inputStream = new FileInputStream(file);
                byte[] buffer = new byte[10485760];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    httpStream.write(buffer, 0, bytesRead);
                }
                httpStream.flush();
                inputStream.close();
                httpStream.write(LINE_FEED.getBytes());
                httpStream.flush();
                
            } finally {
                if (inputStream != null)
                    inputStream.close();
            }
            
        }
        
        public void addFileToRequest(String fieldName, File file, String contentType) throws IOException {
        
            addFileToRequest(fieldName, file, contentType, file.getName());
            
        }
        
        public String processForStringResponse() throws IOException {
        
            StringBuilder builder = new StringBuilder();
            
            builder.append("--").append(boundary).append("--").append(LINE_FEED);
            httpStream.write(builder.toString().getBytes());
            
            httpStream.flush();
            
            return readResponse(httpURLConnection);
            
        }
        
        public RestResponse<JSONObject> processForJsonResponse() throws IOException, JSONException {
        
            StringBuilder builder = new StringBuilder();
            
            builder.append("--").append(boundary).append("--").append(LINE_FEED);
            httpStream.write(builder.toString().getBytes());
            
            httpStream.flush();
            
            String responseString = readResponse(httpURLConnection);
            RestResponse<JSONObject> restResponse = new RestResponse<JSONObject>();
            restResponse.setHttpStatusCode(httpURLConnection.getResponseCode());
            restResponse.setHeadersMap(httpURLConnection.getHeaderFields());
            
            if (responseString != null) {
                JSONObject jsonObject = null;
                if (isValidJSON(responseString)) {
                    jsonObject = new JSONObject(responseString);
                } else {
                    jsonObject = new JSONObject();
                    jsonObject.put("output", responseString);
                }
                
                restResponse.setBody(jsonObject);
            }
            return restResponse;
            
        }
        
        public RestResponse<OMElement> processForXmlResponse() throws IOException, XMLStreamException {
        
            StringBuilder builder = new StringBuilder();
            
            builder.append("--").append(boundary).append("--").append(LINE_FEED);
            httpStream.write(builder.toString().getBytes());
            
            httpStream.flush();
            
            String responseString = readResponse(httpURLConnection);
            RestResponse<OMElement> restResponse = new RestResponse<OMElement>();
            restResponse.setHttpStatusCode(httpURLConnection.getResponseCode());
            restResponse.setHeadersMap(httpURLConnection.getHeaderFields());
            
            if (responseString != null) {
                restResponse.setBody(AXIOMUtil.stringToOM(responseString));
            }
            
            return restResponse;
            
        }
        
        public void addFiletoRequestBody(File file) throws IOException {
        
            FileInputStream inputStream = null;
            try {
                
                // process File
                inputStream = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    httpStream.write(buffer, 0, bytesRead);
                }
                httpStream.flush();
                inputStream.close();
                
            } finally {
                if (inputStream != null)
                    inputStream.close();
            }
        }
        
        public void addChunckedFiletoRequestBody(byte[] bytesPortion) throws IOException {
        
            httpStream.write(bytesPortion);
            
            httpStream.flush();
            
        }
        
        public RestResponse<JSONObject> processAttachmentForJsonResponse() throws IOException, JSONException {
        
            String responseString = readResponse(httpURLConnection);
            RestResponse<JSONObject> restResponse = new RestResponse<JSONObject>();
            restResponse.setHttpStatusCode(httpURLConnection.getResponseCode());
            restResponse.setHeadersMap(httpURLConnection.getHeaderFields());
            
            if (responseString != null) {
                JSONObject jsonObject = null;
                if (isValidJSON(responseString)) {
                    jsonObject = new JSONObject(responseString);
                } else {
                    jsonObject = new JSONObject();
                    jsonObject.put("output", responseString);
                }
                
                restResponse.setBody(jsonObject);
            }
            return restResponse;
            
        }
        
        public RestResponse<OMElement> processAttachmentForXmlResponse() throws IOException, XMLStreamException {
        
            final String responseString = readResponse(httpURLConnection);
            final RestResponse<OMElement> restResponse = new RestResponse<OMElement>();
            restResponse.setHttpStatusCode(httpURLConnection.getResponseCode());
            restResponse.setHeadersMap(httpURLConnection.getHeaderFields());
            
            if (responseString != null) {
                restResponse.setBody(AXIOMUtil.stringToOM(responseString));
            }
            
            return restResponse;
            
        }
        
    }
}
