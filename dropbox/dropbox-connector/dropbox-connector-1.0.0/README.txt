Product: Integration tests for WSO2 ESB DropBox connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - org.wso2.esb.integration.integration-base is rquired. this test suite has been configred to download this automatically. however if its fail download following project and compile using mvn clean install command to update your local repository.
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

STEPS:

 1. Make sure the ESB 4.8.1 zip file with latest patches available at "{PATH_TO_SOURCE_BUNDLE}/dropbox-connector/dropbox-connector-1.0.0/org.wso2.carbon.connector/repository/"

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


 4. Update the DropBox properties file at location "{PATH_TO_SOURCE_BUNDLE}/dropbox-connector/dropbox-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
   
		i) accessToken - Use the access token you got from step 3.

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
		
 5. Navigate to "{PATH_TO_SOURCE_BUNDLE}/dropbox-connector/dropbox-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install


 NOTE : Following DropBox account, can be used for run the integration tests.
    Username : dropboxconnector2014@gmail.com
    Password : dropboxcon
