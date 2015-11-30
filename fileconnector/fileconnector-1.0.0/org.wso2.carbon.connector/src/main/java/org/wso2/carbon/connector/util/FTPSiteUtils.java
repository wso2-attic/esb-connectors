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

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.ftps.FtpsFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

public class FTPSiteUtils {
	/**
	 * Get the default options for File system
	 * 
	 * @return
	 * @throws FileSystemException
	 */
	public static FileSystemOptions createDefaultOptions() throws FileSystemException {
		FileSystemOptions opts = new FileSystemOptions();

		// SSH Key checking
		SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");

		// Root directory set to user home
		SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);

		// Timeout is count by Milliseconds

		SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 100000);

		FtpFileSystemConfigBuilder.getInstance().setPassiveMode(opts, true);

		FtpFileSystemConfigBuilder.getInstance().setSoTimeout(opts, 100000);

		FtpsFileSystemConfigBuilder.getInstance().setPassiveMode(opts, true);
		
		return opts;

	}
}