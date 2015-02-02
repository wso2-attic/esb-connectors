
package org.wso2.carbon.connector.integration.test.common;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;

public class MultipartFormdataProcessor {
    
    private PrintWriter httpWriter;
    
    private String method;
    
    private final String boundary = "wso2IntegTestUploadPart_123456";;
    
    OutputStream outputStream;
    
    HttpURLConnection httpURLConnection;
    
    DataOutputStream dos;
    
    String lineEnd = "\r\n";
    
    String twoHyphens = "--";
    
    /**
     * Instantiates a new multipart formdata processor.
     */
    public MultipartFormdataProcessor() {
    
    }
    
    /**
     * Instantiates a new multipart formdata processor.
     * 
     * @param endPointUrl the end point url
     * @param method the method
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public MultipartFormdataProcessor(String endPointUrl, String method) throws IOException {
    
        init(endPointUrl, Charset.defaultCharset().toString(), null, method);
    }
    
    /**
     * Instantiates a new multipart formdata processor.
     * 
     * @param endPointUrl the end point url
     * @param httpHeaders the http headers
     * @param method the method
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public MultipartFormdataProcessor(String endPointUrl, Map<String, String> httpHeaders, String method)
            throws IOException {
    
        init(endPointUrl, Charset.defaultCharset().toString(), httpHeaders, method);
    }
    
    /**
     * Instantiates a new multipart formdata processor.
     * 
     * @param endPointUrl the end point url
     * @param charSet the char set
     * @param httpHeaders the http headers
     * @param method the method
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public MultipartFormdataProcessor(String endPointUrl, String charSet, Map<String, String> httpHeaders, String method)
            throws IOException {
    
        init(endPointUrl, charSet, httpHeaders, method);
    }
    
    /**
     * Instantiates a new multipart formdata processor.
     * 
     * @param endPointUrl the end point url
     * @param Charset the charset
     * @param method the method
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public MultipartFormdataProcessor(String endPointUrl, String Charset, String method) throws IOException {
    
        init(endPointUrl, Charset, null, method);
    }
    
    /**
     * Inits the.
     * 
     * @param endPointUrl the end point url
     * @param Charset the charset
     * @param httpHeaders the http headers
     * @param method the method
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void init(String endPointUrl, String Charset, Map<String, String> httpHeaders, String method)
            throws IOException {
    
        URL endpoint;
        this.method = method;
        endpoint = new URL(endPointUrl);
        
        httpURLConnection = (HttpURLConnection) endpoint.openConnection();
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setRequestMethod(method);
        
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
        
        outputStream = httpURLConnection.getOutputStream();
        
        httpWriter = new PrintWriter(new OutputStreamWriter(outputStream, Charset), true);
        
    }
    
    /**
     * Adds the form data to request.
     * 
     * @param fieldName the field name
     * @param fieldValue the field value
     */
    public void addFormDataToRequest(String fieldName, String fieldValue) {
    
        addFormDataToRequest(fieldName, fieldValue, Charset.defaultCharset().toString());
    }
    
    /**
     * Adds the form data to request.
     * 
     * @param fieldName the field name
     * @param fieldValue the field value
     * @param charset the charset
     */
    public void addFormDataToRequest(String fieldName, String fieldValue, String charset) {
    
        final String LINE_FEED = "\n";
        
        httpWriter.append("--").append(boundary).append(LINE_FEED);
        httpWriter.append("Content-Type: text/plain; charset=" + charset).append(LINE_FEED);
        httpWriter.append("Content-Disposition: form-data; name=\"" + fieldName + "\"").append(LINE_FEED);
        
        httpWriter.append(LINE_FEED);
        httpWriter.append(fieldValue).append(LINE_FEED);
        httpWriter.flush();
    }
    
    /**
     * Adds the file to request.
     * 
     * @param fieldName the field name
     * @param file the file
     * @param contentType the content type
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public int sendRequestWithSingleFile(String endPointUrl, Map<String, String> formFields,
            Map<String, String> headersMap, File file) throws IOException {
    
        FileInputStream inputStream = null;
        String boundaryName = "--=_wso2IntegTestUploadPart_001";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        HttpURLConnection conn;
        
        try {
            
            // open a URL connection
            URL url = new URL(endPointUrl);
            
            // Open a HTTP connection to the URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            
            // Use a post method.
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            
            if (headersMap != null && !headersMap.isEmpty()) {
                for (Entry<String, String> entry : headersMap.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundaryName);
            
            dos = new DataOutputStream(conn.getOutputStream());
            addFormData(formFields, file, boundaryName);
            // create a buffer of maximum size
            
            FileInputStream fileInputStream = new FileInputStream(file);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            
            // read file and write it into form...
            
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            
            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundaryName + twoHyphens + lineEnd);
             
            // close streams
            fileInputStream.close();
            dos.flush();
            dos.close();
            
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
        return conn.getResponseCode();
    }
    
    /**
     * Adds the form data.
     * 
     * @param formFieldsMap the form fields
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void addFormData(Map<String, String> formFieldsMap, File file, String boundryName) throws IOException {
    
        for (Entry<String, String> entry : formFieldsMap.entrySet()) {
            // add parameters
            dos.writeBytes(twoHyphens + boundryName + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + lineEnd + lineEnd);
            dos.writeBytes(entry.getValue() + lineEnd);
        }
        
        if (file != null) {
            // Send a binary file
            dos.writeBytes(twoHyphens + boundryName + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + file.getName() + "\""
                    + lineEnd);
            dos.writeBytes(lineEnd);
        }
        
    }
    
    /**
     * Adds the file to request plain.
     * 
     * @param file the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void addFileToRequestPlain(File file) throws IOException {
    
        FileInputStream inputStream = null;
        try {
            final String LINE_FEED = "\n";
            
            // process File
            inputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();
            httpWriter.append(LINE_FEED);
            httpWriter.flush();
            
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
    }
    
    /**
     * Read response.
     * 
     * @param con the con
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
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
     * Process the response.
     * 
     * @return the RestResponse object containing the response.
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws XMLStreamException Signals that a XMLStream exception.
     */
    public RestResponse<OMElement> process() throws IOException, XMLStreamException {
    
        final String LINE_FEED = "\n";
        
        httpWriter.append(LINE_FEED);
        
        if ("POST".equals(method)) {
            httpWriter.append("--").append(boundary).append("--").append(LINE_FEED);
        }
        
        httpWriter.flush();
        httpWriter.close();
        
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
