Product: Integration tests for WSO2 ESB DropBox connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 14.04
 - WSO2 ESB 4.9.0-Alpha

STEPS:

 1. Make sure the ESB 4.9.0-Alpha zip file with latest patches available at "{DROPBOX_CONNECTOR_HOME}/dropbox-connector/dropbox-connector-1.0.0/org.wso2.carbon.connector/repository/"

 2. This ESB should be configured as below;
	Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml).

   <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
   
   <messageFormatter contentType="application/x-www-form-urlencoded" class="org.apache.axis2.transport.http.XFormURLEncodedFormatter"/>
   
   <messageFormatter contentType="text/javascript" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>	
   
   <messageFormatter contentType="application/octet-stream" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>	
   
   <messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
   
   <messageBuilder contentType="application/x-www-form-urlencoded" class="org.apache.synapse.commons.builders.XFormURLEncodedBuilder"/>
   
   <messageBuilder contentType="text/javascript" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
   
   <messageBuilder contentType="application/octet-stream" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
   
   Enable the relevant message builders and formatters in axis2 configuration file when testing file upload methods.
   
		Eg: Below mentioned message formatter and the builder should be enabled when uploading ".png" files to test file upload methods.
		
		<messageFormatter contentType="image/png" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

		<messageBuilder contentType="image/png" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
		


 3. Create a DropBox account and derive the access token:
	i) 	Using the URL "https://www.dropbox.com/" create a DropBox account.
	ii) Derive the access token by following the instructions at "https://www.dropbox.com/developers/core/docs#oa2-authorize".


 4. Update the DropBox properties file at location "{DROPBOX_CONNECTOR_HOME}/dropbox-connector/dropbox-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
   
		i) accessToken - Use the access token you got from step 3.

	Following properties should be changed to facilitate the file upload test cases. Values should be set based on the properties of the file which will uploaded. 
	
		ii) uploadContentType - Content type of the file (uploadFile method).
		iii) uploadSourcePath - File name with the extension (uploadFile method).
		iv) contentLength - Content length of the file (uploadFile method).
		v) chunkUploadContentType - Content type of the file (chunkUpload method).
		vi) chunkUploadSourcePath - File name with the extension (chunkUpload method).
		vii) chunkUploadDestinationPath - Destination path of the file (chunkUpload method).
		viii) bufferSize - Size of a single chunk. (chunkUpload method)
		
	It is optional to change the below mentioned properties unless you need to change the file and folder names, created during the integration test run.
	
		ix) folderName1 - Folder name to test craeteFolder method.
		x) folderName2 - Folder name to test createFolder method.
		xi) fileName - File name of the uploaded file.
		xii) root - This parameter should always be set to "dropbox".
		
 5. Make sure that the Dropbox connector is set as a module in esb-connectors parent pom.
                <module>dropbox/dropbox-connector/dropbox-connector-1.0.0/org.wso2.carbon.connector</module>
        Navigate to "esb-connectors" and run the following command.
        $ mvn clean install


 NOTE : Following DropBox account, can be used for run the integration tests.
    Username : dropboxconnector2014@gmail.com
    Password : dropboxcon
