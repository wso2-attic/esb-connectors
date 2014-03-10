/*
 * Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.connector.googledrive;

import java.io.IOException;
import java.io.InputStream;

import com.google.api.client.http.AbstractInputStreamContent;

/**
 * FileContent class to take an input stream instead of the traditional java.io.File object. This class
 * extends the Google Drive Java SDK's AbstractInputStreamContent class in order to manage file inserts and
 * content updates.
 */
public class GoogleDriveFileContent extends AbstractInputStreamContent {
    
    /**
     * Input Stream object for the File Content.
     */
    private final InputStream inputStream;
    
    /**
     * Constructor to initialize a Google Drive file content object.
     * 
     * @param type Content type or {@code null} for none
     * @param inputStrm The input stream
     */
    public GoogleDriveFileContent(final String type, final InputStream inputStrm) {
    
        super(type);
        this.inputStream = inputStrm;
    }
    
    /**
     * Returns the length of a given input stream.
     * 
     * @return Length of the input stream
     * @throws IOException If an error occur when parsing the input stream.
     */
    public final long getLength() throws IOException {
    
        return inputStream.available();
    }
    
    /**
     * Returns whether this is a resumable content upload.
     * 
     * @return true if upload type is resumable
     */
    public final boolean retrySupported() {
    
        return true;
    }
    
    /**
     * Get the inputstream object.
     * 
     * @return The inputstream set by the object
     * @throws IOException If an error occur on Google Drive API end.
     */
    @Override
    public final InputStream getInputStream() throws IOException {
    
        return this.inputStream;
    }
    
}
