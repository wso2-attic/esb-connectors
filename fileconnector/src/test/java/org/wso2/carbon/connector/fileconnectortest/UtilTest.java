package org.wso2.carbon.connector.fileconnectortest;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.util.ArchiveType;
import org.wso2.carbon.connector.util.FileCompressUtil;

public class UtilTest {

	@Test
	public void testZipCompressFile() throws IOException {

		FileCompressUtil fileCompress = new FileCompressUtil();
		String fileLocation = "/home/gayan/workspace/File-connector/files/";
		File inputDirectory = new File(fileLocation.toString());

		final String[] SUFFIX = { "xls", "xml" };

		Collection<File> fileList = FileUtils.listFiles(inputDirectory, SUFFIX, true);

		fileCompress.compressFiles(fileList, new File(fileLocation + "ziptest"), ArchiveType.ZIP);
		File outPut = new File(fileLocation + "ziptest" + ".zip");

		Assert.assertEquals(outPut.exists(), true);

	}

	@Test
	public void testTarCompressFile() throws IOException {

		FileCompressUtil fileCompress = new FileCompressUtil();
		String fileLocation = "/home/gayan/workspace/File-connector/files/";
		File inputDirectory = new File(fileLocation.toString());

		final String[] SUFFIX = { "xls", "xml" };

		Collection<File> fileList = FileUtils.listFiles(inputDirectory, SUFFIX, true);

		fileCompress.compressFiles(fileList, new File(fileLocation + "tartest"),
		                           ArchiveType.TAR_GZIP);
		File outPut = new File(fileLocation + "tartest" + ".tar.gz");

		Assert.assertEquals(outPut.exists(), true);

	}
}
