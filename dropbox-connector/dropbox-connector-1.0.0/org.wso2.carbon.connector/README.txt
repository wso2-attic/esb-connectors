Product: Integration tests for WSO2 ESB DropBox connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

STEPS:

 1. Make sure the ESB 4.8.1 zip file with latest patches available at "Integration_Test/products/esb/4.8.1/modules/distribution/target/"

 2. This ESB should be configured as below;
	Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml).

   <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
   
   <messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
   
   <messageFormatter contentType="application/json" class="org.apache.synapse.commons.json.JsonStreamFormatter"/>
   
   <messageBuilder contentType="application/json" class="org.apache.synapse.commons.json.JsonStreamBuilder"/>
   
   <messageFormatter contentType="application/x-www-form-urlencoded" class="org.apache.axis2.transport.http.XFormURLEncodedFormatter"/>
   
   <messageBuilder contentType="application/x-www-form-urlencoded" class="org.apache.synapse.commons.builders.XFormURLEncodedBuilder"/>
   
   <messageFormatter contentType="text/javascript" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>	
   
   <messageBuilder contentType="text/javascript" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
   
   <messageFormatter contentType="application/octet-stream" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>	
   
   <messageBuilder contentType="application/octet-stream" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
   
   Enable the relevant message builders and formatters in axis2 configuration file when testing file upload methods.
   
		Eg: Below mentioned message formatter and the builder should be enabled when uploading ".png" files to test file upload methods.
		
		<messageFormatter contentType="image/png" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
		<messageBuilder contentType="image/png" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
		
 3. Make sure "integration-base" project is placed at "Integration_Test/products/esb/4.8.1/modules/integration/"

 4. Navigate to "Integration_Test/products/esb/4.8.1/modules/integration/integration-base" and run the following command.
      $ mvn clean install

 5. Add following dependancy to the file "Integration_Test/products/esb/4.8.1/modules/integration/connectors/pom.xml"
	<dependency>

		<groupId>org.wso2.esb</groupId>

		<artifactId>org.wso2.connector.integration.test.base</artifactId>

		<version>4.8.1</version>

		<scope>system</scope>

		<systemPath>${basedir}/../integration-base/target/org.wso2.connector.integration.test.base-4.8.1.jar</systemPath>

	</dependency>

 6. Make sure the dropbox test suite is enabled (as given below) and all other test suites are commented in the following file - "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/testng.xml"  
    <test name="Dropbox-Connector-Test" preserve-order="true" verbose="2">
        <packages>
            <package name="org.wso2.carbon.connector.integration.test.dropbox"/>
        </packages>
    </test>

 7. Create a DropBox account and derive the access token:
	i) 	Using the URL "https://www.dropbox.com/" create a DropBox account.
	ii) Derive the access token by following the instructions at "https://www.dropbox.com/developers/core/docs#oa2-authorize".

	
 8. Copy the main and the test folders from the provided DropBox connector source bundle to the location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/"

 9. Update the DropBox properties file at location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/connector/config" as below.
   
		i) accessToken - Use the access token you got from step 8.

	Following properties should be changed to facilitate the file upload test cases. Values should be set based on the properties of the file which will uploaded. 
	
		ii) uploadContentType - Content type of the file (uploadFile method).
		iii) uploadSourcePath - File name with the extension (uploadFile method).
		iv) contentLength - Content length of the file (uploadFile method).
		v) chunckUploadContentType - Content type of the file (chunkUpload method).
		vi) chunkUploadSourcePath - File name with the extension (chunkUpload method).
		vii) chunckUploadDestinationPath - Destination path of the file (chunkUpload method).
		viii) bufferSize - Size of a single chunk. (chunkUpload method)
		
	It is optional to change the below mentioned properties unless you need to change the file and folder names, created during the integration test run.
	
		ix) folderName1 - Folder name to test craeteFolder method.
		x) folderName2 - Folder name to test createFolder method.
		xi) fileName - File name of the uploaded file.
		xii) root - This parameter should always be set to "dropbox".
		
 10. Navigate to "Integration_Test/products/esb/4.8.1/modules/integration/connectors/" and run the following command.
      $ mvn clean install

 NOTE : Following DropBox account, can be used for run the integration tests.
    Username : dropboxconnector2014@gmail.com
    Password : dropboxcon
