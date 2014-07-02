package org.wso2.carbon.connector.util;

import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs.provider.sftp.SftpFileSystemConfigBuilder;

public class FTPSiteUtils {
	/**
	 * Get the default options for File system
	 * 
	 * @return
	 * @throws FileSystemException
	 */
	public static FileSystemOptions createDefaultOptions() throws FileSystemException {
		// Create SFTP options
		FileSystemOptions opts = new FileSystemOptions();

		FtpFileSystemConfigBuilder.getInstance().setPassiveMode(opts, true);
		// FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts,
		// true);
		// SSH Key checking
		SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");

		// Root directory set to user home
		SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);

		// Timeout is count by Milliseconds

		SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 100000);

		return opts;
	}
}
