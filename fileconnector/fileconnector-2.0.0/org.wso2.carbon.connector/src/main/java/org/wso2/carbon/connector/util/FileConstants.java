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

public final class FileConstants {
    public static final String FILE_LOCATION = "source";
    public static final String CONTENT_TYPE = "contentType";
    public static final String FILE_PATTERN = "filePattern";
    public static final String NEW_FILE_LOCATION = "destination";
    public static final String CONTENT = "inputContent";
    public static final String ENCODING = "encoding";
    public static final String NAMESPACE = "ns";
    public static final String RESULT = "result";
    public static final String FILE = "file";
    public static final String FILECON = "http://org.wso2.esbconnectors.FileConnector";
    public static final int BUFFER_SIZE = 4096;
    public static final String DEFAULT_ENCODING = "UTF8";
    public static final String SET_TIME_OUT = "setTimeout";
    public static final String SET_PASSIVE_MODE = "setPassiveMode";
    public static final String SET_SO_TIMEOUT = "setSoTimeout";
    public static final String SET_STRICT_HOST_KEY_CHECKING = "setStrictHostKeyChecking";
    public static final String SET_USER_DIRISROOT = "setUserDirIsRoot";
    public static final int TIME_OUT = 100000;
    public static final String START_TAG = "<result><success>";
    public static final String END_TAG = "</success></result>";
    public static final String FILE_EXIST_START_TAG = "<result><fileExist>";
    public static final String FILE_EXIST_END_TAG = "</fileExist></result>";

}