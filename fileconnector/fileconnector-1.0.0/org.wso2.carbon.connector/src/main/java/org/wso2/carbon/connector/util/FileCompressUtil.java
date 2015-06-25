package org.wso2.carbon.connector.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileCompressUtil {

	private static Log log = LogFactory.getLog(FileCompressUtil.class);

	/**
	 * Compress the files based on the archive type
	 * 
	 * 
	 * @param files
	 * @param file
	 * @param archiveType
	 * @throws IOException
	 */
	public void compressFiles(Collection files, File file, ArchiveType archiveType)
	                                                                               throws IOException {
		log.info("Compressing " + files.size() + " to " + file.getAbsoluteFile());
		// Create the output stream for the output file
		FileOutputStream fos;
		switch (archiveType) {
			case TAR_GZIP:
				fos = new FileOutputStream(new File(file.getCanonicalPath() + ".tar" + ".gz"));
				// Wrap the output file stream in streams that will tar and gzip
				// everything
				TarArchiveOutputStream taos =
				                              new TarArchiveOutputStream(
				                                                         new GZIPOutputStream(
				                                                                              new BufferedOutputStream(
				                                                                                                       fos)));
				// TAR has an 8 gig file limit by default, this gets around that
				taos.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR);
				// to get past the 8 gig limit; TAR originally didn't support
				// long file names, so enable the
				// support for it
				taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
				// Get to putting all the files in the compressed output file
				Iterator iterator = files.iterator();
				while (iterator.hasNext()) {
					File f = (File) iterator.next();
					addFilesToCompression(taos, f, ".", ArchiveType.TAR_GZIP);
					// do something to object here...
				}

				// Close everything up
				taos.close();
				fos.close();
				break;
			case ZIP:
				fos = new FileOutputStream(new File(file.getCanonicalPath() + ".zip"));
				// Wrap the output file stream in streams that will tar and zip
				// everything
				ZipArchiveOutputStream zaos =
				                              new ZipArchiveOutputStream(
				                                                         new BufferedOutputStream(
				                                                                                  fos));
				zaos.setEncoding("UTF-8");
				zaos.setCreateUnicodeExtraFields(ZipArchiveOutputStream.UnicodeExtraFieldPolicy.ALWAYS);

				// Get to putting all the files in the compressed output file
				Iterator iterator1 = files.iterator();
				while (iterator1.hasNext()) {
					File f = (File) iterator1.next();
					addFilesToCompression(zaos, f, ".", ArchiveType.ZIP);
					// do something to object here...
				}

				// Close everything up
				zaos.close();
				fos.close();
				break;
		}
	}

	/**
	 * Add the files to compression
	 * 
	 * @param taos
	 * @param file
	 * @param dir
	 * @param archiveType
	 * @throws IOException
	 */
	private void addFilesToCompression(ArchiveOutputStream taos, File file, String dir,
	                                   ArchiveType archiveType) throws IOException {

		// Create an entry for the file
		switch (archiveType) {

			case TAR_GZIP:
				taos.putArchiveEntry(new TarArchiveEntry(file, dir + "/" + file.getName()));
				break;

			case ZIP:
				taos.putArchiveEntry(new ZipArchiveEntry(file, dir + "/" + file.getName()));
				break;
		}

		if (file.isFile()) {
			// Add the file to the archive
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			IOUtils.copy(bis, taos);
			taos.closeArchiveEntry();
			bis.close();

		} else if (file.isDirectory()) {
			// close the archive entry
			taos.closeArchiveEntry();
			// go through all the files in the directory and using recursion,
			// add them to the archive

			for (File childFile : file.listFiles()) {
				addFilesToCompression(taos, childFile, file.getName(), archiveType);
			}
		}

	}
}