package org.wso2.carbon.connector.fileconnectortest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.Value;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.connector.FileAppend;
import org.wso2.carbon.connector.FileCopy;
import org.wso2.carbon.connector.FileCopyInStream;
import org.wso2.carbon.connector.FileCreate;
import org.wso2.carbon.connector.FileDelete;
import org.wso2.carbon.connector.FileExist;
import org.wso2.carbon.connector.FileRename;
import org.wso2.carbon.connector.core.ConnectException;

public class FileConnectorTest {
	//
	private static final String TEST_TEMPLATE = "create";

	@BeforeClass(alwaysRun = true)
	public void setUp() throws Exception {

	}

	@AfterClass(alwaysRun = true)
	public void tearDown() throws Exception {

	}

	@Test
	public static void testFileTest() {
		Assert.assertFalse(false);
	}

	@Test
	public void testCreateFile() throws NoSuchMethodException, IllegalAccessException,
	                            InvocationTargetException {

		FileCreate fileCreate = new FileCreate();

		Method method =
		                FileCreate.class.getDeclaredMethod("createFile", String.class,
		                                                   String.class, String.class,
		                                                   String.class, String.class,
		                                                   Boolean.class);
		method.setAccessible(true);

		boolean output =
		                 (boolean) method.invoke(fileCreate,
		                                         "file://home/gayan/workspace/File-connector/files/",
		                                         "bbbbbtest.xml", "test", "UTF-8", "", false);

		Assert.assertEquals(output, true);
	}

	public void testAppendFile() throws NoSuchMethodException, IllegalAccessException,
	                            InvocationTargetException {

		FileAppend file = new FileAppend();

		Method method =
		                FileAppend.class.getDeclaredMethod("appendFile", String.class,
		                                                   String.class, String.class,
		                                                   String.class, String.class,
		                                                   String.class, Integer.class);
		method.setAccessible(true);

		boolean output =
		                 (boolean) method.invoke(file,
		                                         "file://home/gayan/workspace/File-connector/files/",
		                                         "bbbbbtest.xml", "test", "UTF8", "", "", 0);

		Assert.assertEquals(output, true);
	}

	@Test(dependsOnMethods = { "testCreateFile" })
	public void testRenameFile() throws NoSuchMethodException, IllegalAccessException,
	                            InvocationTargetException {

		FileRename file = new FileRename();

		Method method =
		                FileRename.class.getDeclaredMethod("renameFile", String.class,
		                                                   String.class, String.class, String.class);
		method.setAccessible(true);

		boolean output =
		                 (boolean) method.invoke(file,
		                                         "file://home/gayan/workspace/File-connector/files/",
		                                         "bbbbbtest.xml", "bbbbbrename.xml", "");

		Assert.assertEquals(output, true);
	}

	@Test
	public void testRenameNoFileFound() throws NoSuchMethodException, IllegalAccessException,
	                                   InvocationTargetException {
		FileRename file = new FileRename();

		Method method =
		                FileRename.class.getDeclaredMethod("renameFile", String.class,
		                                                   String.class, String.class, String.class);
		method.setAccessible(true);

		boolean output =
		                 (boolean) method.invoke(file,
		                                         "file://home/gayan/workspace/File-connector/files/",
		                                         "bbbbbtestnofile.xml", "bbbbbrename.xml", "");

		Assert.assertEquals(output, false);
	}

	@Test(dependsOnMethods = { "testRenameFile" })
	public void testDeleteFile() throws NoSuchMethodException, IllegalAccessException,
	                            InvocationTargetException {

		FileDelete file = new FileDelete();

		Method method =
		                FileDelete.class.getDeclaredMethod("deleteFile", String.class,
		                                                   String.class, String.class);
		method.setAccessible(true);

		boolean output =
		                 (boolean) method.invoke(file,
		                                         "file://home/gayan/workspace/File-connector/files/",
		                                         "bbbbbrename.xml", "");

		Assert.assertEquals(output, true);
	}

	@Test(dependsOnMethods = { "testRenameFile" })
	public void testDeleteNoFile() throws NoSuchMethodException, IllegalAccessException,
	                              InvocationTargetException {

		FileDelete file = new FileDelete();

		Method method =
		                FileDelete.class.getDeclaredMethod("deleteFile", String.class,
		                                                   String.class, String.class);
		method.setAccessible(true);

		boolean output =
		                 (boolean) method.invoke(file,
		                                         "file://home/gayan/workspace/File-connector/files/",
		                                         "bbbbbrenamenofile.xml", "");

		Assert.assertEquals(output, false);
	}

	@Test
	public void testFileExist() throws NoSuchMethodException, IllegalAccessException,
	                           InvocationTargetException {

		FileExist file = new FileExist();

		Method method =
		                FileExist.class.getDeclaredMethod("isFileExist", String.class,
		                                                  String.class, String.class);
		method.setAccessible(true);

		boolean output =
		                 (boolean) method.invoke(file,
		                                         "file://home/gayan/workspace/File-connector/files/",
		                                         "aaaavfs.xml", "");

		Assert.assertEquals(output, true);
	}

	@Test
	public void testNoFileExist() throws NoSuchMethodException, IllegalAccessException,
	                             InvocationTargetException {

		FileExist file = new FileExist();

		Method method =
		                FileExist.class.getDeclaredMethod("isFileExist", String.class,
		                                                  String.class, String.class);
		method.setAccessible(true);

		boolean output =
		                 (boolean) method.invoke(file,
		                                         "file://home/gayan/workspace/File-connector/files/",
		                                         "aaaavfsnofile.xml", "");

		Assert.assertEquals(output, false);
	}

	@Test(enabled = true)
	public void testCoypLargeFile() throws NoSuchMethodException, IllegalAccessException,
	                               InvocationTargetException {
		FileCopyInStream file = new FileCopyInStream();

		Method method =
		                FileCopyInStream.class.getDeclaredMethod("copyLargeFiles", String.class,
		                                                         String.class, String.class);
		method.setAccessible(true);

		boolean output =
		                 (boolean) method.invoke(file,
		                                         "file://media/gayan/Project/Tut/Training/ws02/TestLarge/",
		                                         "Introduction_to_ESB0.mp4",
		                                         "ftp://gayan:ws02@localhost:21/media/gayan/Project/FTP/");

		Assert.assertEquals(output, true);
	}

	@Test(expectedExceptions = InvocationTargetException.class)
	public void testCoypNoLargeFile() throws NoSuchMethodException, IllegalAccessException,
	                                 InvocationTargetException, IOException {
		FileCopyInStream file = new FileCopyInStream();

		Method method =
		                FileCopyInStream.class.getDeclaredMethod("copyLargeFiles", String.class,
		                                                         String.class, String.class);
		method.setAccessible(true);

		boolean output =
		                 (boolean) method.invoke(file,
		                                         "file://media/gayan/Project/Tut/Training/ws02/TestLarge/",
		                                         "NoFile.mp4",
		                                         "ftp://gayan:ws02@localhost:21/media/gayan/Project/FTP/");

		Assert.assertEquals(output, false);
	}

	@Test(expectedExceptions = InvocationTargetException.class)
	public void testCoypNoFile() throws NoSuchMethodException, IllegalAccessException,
	                            InvocationTargetException, IOException {
		FileCopy file = new FileCopy();

		Method method =
		                FileCopy.class.getDeclaredMethod("copyFile", String.class, String.class,
		                                                 String.class, boolean.class);
		method.setAccessible(true);

		boolean output =
		                 (boolean) method.invoke(file,
		                                         "file://media/gayan/Project/Tut/Training/ws02/TestLarge/",
		                                         "TestNoFile.xml",
		                                         "ftp://gayan:ws02@localhost:21/media/gayan/Project/FTP/",
		                                         false);

		Assert.assertEquals(output, false);
	}

	public static void testFileCreateTest() throws AxisFault, ConnectException {
		System.out.println("tets started");

		org.apache.axis2.context.MessageContext axis2Ctx =
		                                                   new org.apache.axis2.context.MessageContext();

		SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
		org.apache.axiom.soap.SOAPEnvelope envelope = fac.getDefaultEnvelope();
		axis2Ctx.setEnvelope(envelope);
		axis2Ctx.setProperty("filelocation", "file://home/gayan/workspace/File-connector/files/");
		axis2Ctx.setProperty("file", "aaaa1111.xml");
		MessageContext synCtx = new Axis2MessageContext(axis2Ctx, null, null);
		Collection<String> collection = new java.util.ArrayList<String>();
		collection.add("filelocation");
		collection.add("file");
		synCtx.setProperty("filelocation",
		                   new Value("file://home/gayan/workspace/File-connector/files/"));
		synCtx.setProperty("file", new Value("aaaa1111.xml"));

		System.out.println("file delete started");
		FileDelete fileCreate = new FileDelete();

		fileCreate.connect(synCtx);
		System.out.println("file delete finisheild");

		Assert.assertTrue(((Axis2MessageContext) synCtx).getAxis2MessageContext().getEnvelope()
		                                                .getFirstElement() != null);

	}

}
