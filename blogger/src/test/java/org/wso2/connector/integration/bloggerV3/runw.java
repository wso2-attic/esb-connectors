package org.wso2.connector.integration.bloggerV3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
public class runw {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
    String path = "hed.txt";
    String propl = "userID,fields";
    String meth = "oo";
    
    String es = "";
    String props = "\n";
    String param = "\n";
    BufferedReader reader = null;
    try {
        reader = new BufferedReader(new FileReader(path));
        String line = null;
       
        
        while ((line = reader.readLine()) != null) {
        	es = es + line + "pos";
        }
        
    } catch (IOException ioe) {
    	System.out.println("Error reading request from file.");
    } finally {
        if (reader != null) {
            try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    String jsonp = "";
    
	props = "";
	
	String e[] = es.split("pos");
	for(int i=0; i<e.length; i++){
		props = props + "\n " + 	"<header name=\"" + e[i].split(":")[0] + "\" scope=\"transport\" action=\"remove\" />";

		
	}
	
	props = props + "\n " + 	"<header name=\"" + "etag" + "\" scope=\"transport\" action=\"remove\" />";

	
	System.out.println(props);

		/*
es = es.replace("prop_body", props);
es = es.replace("meth_name", meth);
es = es.replace("meth_body", param);

System.out.println(es);

Writer writer;

writer = null;

try {
    writer = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream("/home/poshitha/wso2svn/carbon-turing/turing/products/esb/4.8.1/modules/integration/blogger-connnector-test/src/test/resources/artifacts/ESB/config/proxies/blogger/" + meth + ".xml"), "utf-8"));
    writer.write(es);
} catch (IOException ex) {
  // report
} finally {
   try {writer.close();} catch (Exception ex) {}
}

writer = null;

try {
    writer = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream("/home/poshitha/wso2svn/carbon-turing/turing/products/esb/4.8.1/modules/integration/blogger-connnector-test/src/test/resources/artifacts/ESB/config/restRequests/blogger/" + meth + "_mandatory.txt"), "utf-8"));
    writer.write("{ \"apiUrl\":\"%s\",\n \"accessToken\":\"%s\" " + jsonp + "\n }");
} catch (IOException ex) {
  // report
} finally {
   try {writer.close();} catch (Exception ex) {}
}

writer = null;

try {
    writer = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream("/home/poshitha/wso2svn/carbon-turing/turing/products/esb/4.8.1/modules/integration/blogger-connnector-test/src/test/resources/artifacts/ESB/config/restRequests/blogger/" + meth + "_optional.txt"), "utf-8"));
    writer.write("{ \"apiUrl\":\"%s\",\n \"accessToken\":\"%s\" " + jsonp + "\n }");
} catch (IOException ex) {
  // report
} finally {
   try {writer.close();} catch (Exception ex) {}
}

writer = null;

try {
    writer = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream("/home/poshitha/wso2svn/carbon-turing/turing/products/esb/4.8.1/modules/integration/blogger-connnector-test/src/test/resources/artifacts/ESB/config/restRequests/blogger/" + meth + "_negative.txt"), "utf-8"));
    writer.write("{ \"apiUrl\":\"%s\",\n \"accessToken\":\"%s\" " + jsonp + "\n }");
} catch (IOException ex) {
  // report
} finally {
   try {writer.close();} catch (Exception ex) {}
}


*/
	}
	


}
