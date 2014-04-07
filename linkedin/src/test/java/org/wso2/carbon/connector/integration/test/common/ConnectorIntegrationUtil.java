
package org.wso2.carbon.connector.integration.test.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.mediation.library.stub.upload.MediationLibraryUploaderStub;
import org.wso2.carbon.mediation.library.stub.upload.types.carbon.LibraryFileItem;

public class ConnectorIntegrationUtil {
    
    public static final String ESB_CONFIG_LOCATION = "artifacts" + File.separator + "ESB" + File.separator + "config";
    
    private static final Log log = LogFactory.getLog(ConnectorIntegrationUtil.class);
    
    public static void uploadConnector(String repoLocation, MediationLibraryUploaderStub mediationLibUploadStub,
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
    
    public static int sendRequestToRetriveHeaders(String addUrl, String query) throws IOException, JSONException {
    
        String charset = "UTF-8";
        URLConnection connection = new URL(addUrl).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", charset);
        connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);
        
        OutputStream output = null;
        try {
            output = connection.getOutputStream();
            output.write(query.getBytes(charset));
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException logOrIgnore) {
                    log.error("Error while closing the connection");
                }
            }
        }
        
        HttpURLConnection httpConn = (HttpURLConnection) connection;
        int responseCode = httpConn.getResponseCode();
        
        return responseCode;
    }
    
    public static int sendRequestToRetriveHeaders(String addUrl, String query, String contentType) throws IOException,
            JSONException {
    
        String charset = "UTF-8";
        URLConnection connection = new URL(addUrl).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", charset);
        connection.setRequestProperty("Content-Type", contentType + ";charset=" + charset);
        
        OutputStream output = null;
        try {
            output = connection.getOutputStream();
            output.write(query.getBytes(charset));
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException logOrIgnore) {
                    log.error("Error while closing the connection");
                }
            }
        }
        
        HttpURLConnection httpConn = (HttpURLConnection) connection;
        int responseCode = httpConn.getResponseCode();
        
        return responseCode;
    }
    
    public static JSONObject sendRequest(String addUrl, String query) throws IOException, JSONException {
    
        String charset = "UTF-8";
        URLConnection connection = new URL(addUrl).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", charset);
        connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);
        OutputStream output = null;
        try {
            output = connection.getOutputStream();
            output.write(query.getBytes(charset));
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException logOrIgnore) {
                    log.error("Error while closing the connection");
                }
            }
        }
        
        HttpURLConnection httpConn = (HttpURLConnection) connection;
        InputStream response;
        
        if (httpConn.getResponseCode() >= 400) {
            response = httpConn.getErrorStream();
        } else {
            response = connection.getInputStream();
        }
        
        String out = "{}";
        if (response != null) {
            StringBuilder sb = new StringBuilder();
            byte[] bytes = new byte[1024];
            int len;
            while ((len = response.read(bytes)) != -1) {
                sb.append(new String(bytes, 0, len));
            }
            
            if (!sb.toString().trim().isEmpty()) {
                out = sb.toString();
            }
        }
        
        JSONObject jsonObject = new JSONObject(out);
        
        return jsonObject;
    }
    
    public static OMElement sendXMLRequest(String addUrl, String query) throws MalformedURLException, IOException,
            XMLStreamException {
    
        String charset = "UTF-8";
        URLConnection connection = new URL(addUrl).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", charset);
        connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);
        OutputStream output = null;
        try {
            output = connection.getOutputStream();
            output.write(query.getBytes(charset));
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException logOrIgnore) {
                    log.error("Error while closing the connection");
                }
            }
        }
        
        HttpURLConnection httpConn = (HttpURLConnection) connection;
        InputStream response;
        
        if (httpConn.getResponseCode() >= 400) {
            response = httpConn.getErrorStream();
        } else {
            response = connection.getInputStream();
        }
        
        String out = "{}";
        if (response != null) {
            StringBuilder sb = new StringBuilder();
            byte[] bytes = new byte[1024];
            int len;
            while ((len = response.read(bytes)) != -1) {
                sb.append(new String(bytes, 0, len));
            }
            
            if (!sb.toString().trim().isEmpty()) {
                out = sb.toString();
            }
        }
        
        OMElement omElement = AXIOMUtil.stringToOM(out);
        
        return omElement;
        
    }
    
    public static Properties getConnectorConfigProperties(String connectorName) {
    
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
    
    public static OMElement sendReceive(OMElement payload, String endPointReference, String operation,
            String contentType) throws AxisFault {
    
        ServiceClient sender;
        Options options;
        OMElement response = null;
        if (log.isDebugEnabled()) {
            log.debug("Service Endpoint : " + endPointReference);
            log.debug("Service Operation : " + operation);
            log.debug("Payload : " + payload);
        }
        try {
            sender = new ServiceClient();
            options = new Options();
            options.setTo(new EndpointReference(endPointReference));
            options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
            options.setTimeOutInMilliSeconds(45000);
            options.setAction("urn:" + operation);
            options.setSoapVersionURI(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
            options.setProperty(Constants.Configuration.MESSAGE_TYPE, contentType);
            sender.setOptions(options);
            
            response = sender.sendReceive(payload);
            if (log.isDebugEnabled()) {
                log.debug("Response Message : " + response);
            }
        } catch (AxisFault axisFault) {
            log.error(axisFault.getMessage());
            throw new AxisFault("AxisFault while getting response :" + axisFault.getMessage(), axisFault);
        }
        return response;
    }
    
    /**
     * Method to read in contents of a file as String
     * 
     * @param path
     * @return String contents of file
     * @throws IOException
     */
    public static String getFileContent(String path) throws IOException {
    
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
            String line = null;
            
            String ls = System.getProperty("line.separator");
            
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            
        } catch (IOException ioe) {
            log.error("Error reading request from file.", ioe);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return stringBuilder.toString();
        
    }
    
    /**
     * Convert first letter of a string to upper case
     * 
     * @param string
     * @return <strong>String</strong> with the first letter as upper case
     */
    public static String firstToUpperCase(String string) {
    
        String post = string.substring(1, string.length());
        String first = ("" + string.charAt(0)).toUpperCase();
        return first + post;
    }
    
}
