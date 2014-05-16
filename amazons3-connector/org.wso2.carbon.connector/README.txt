Product: Integration tests for WSO2 ESB AmazonS3 connector
    Pre-requisites:

    - Maven 3.x
    - Java 1.6 or above

    Tested Platforms: 

    - Microsoft WINDOWS V-7
    - Ubuntu 13.04
    - WSO2 ESB 4.8.1

Note:
	This test suite can execute based on two scenarios.
		1. Use the given test account and parameters. - in this scenario you only need to replace accessKeyId , secretAccessKey in property file
		2. Setup new amazonS3 account and follow all the instruction given below
	
Steps to follow in setting integration test.
 1.  Download ESB 4.8.1 from official website.
 2.  Deploy following patches.
            patchjson
            special-char-on-get
            multipart-patch
            http PATCH request patch
            PATCH for XSLT with local entry

 3.  Navigate to location "/wso2esb-4.8.1/repository/conf/axis2" and add/uncomment following lines in "axis2.xml".
    
			<messageBuilder contentType="binary/octet-stream" class="org.wso2.carbon.relay.BinaryRelayBuilder"/> 
			
            <messageBuilder contentType="image/gif" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

			<messageBuilder contentType="img/gif" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

			<messageBuilder contentType="image/jpeg" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

			<messageBuilder contentType="image/png" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

			<messageBuilder contentType="image/ico" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

			<messageBuilder contentType="image/x-icon" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

			<messageFormatter contentType="image/gif" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

			<messageFormatter contentType="img/gif" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

			<messageFormatter contentType="image/jpeg" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

			<messageFormatter contentType="image/png" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

			<messageFormatter contentType="image/ico" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

			<messageFormatter contentType="image/x-icon" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

			<messageFormatter contentType="application/octet-stream" class="org.apache.axis2.format.BinaryFormatter"/>

 4.  Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "Integration_Test/products/esb/4.8.1/modules/distribution/target/".

 5.  Copy the main and the test folders from the provided AmazonS3 connector source bundle to the location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/". Copy pom.xml from the source bundle to "Integration_Test/products/esb/4.8.1/modules/integration/connectors/". When running integration tests, uncomment fhe following two blocks from pom.xml:

	<parent>
	<groupId>org.wso2.esb</groupId>
	<artifactId>esb-integration-tests</artifactId>
	<version>4.8.1</version>
	<relativePath>../pom.xml</relativePath>
	</parent>

 6. Prerequisites for AmazonS3 Connector Integration Testing

    Follow these steps before start testing.
    a)  Create a fresh account in amazons3 and Log on to http://aws.amazon.com/s3/ with the web browser.
    b)  Save the AWSAccessKeyId and AWSSecretKey while continuing the registration process.
    c)  Following fields in the property file at location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/connector/config" also should be updated appropriately.

        1)    accessKeyId is the access key id for the application generated from Amazon.

        2)    secretAccessKey is the secret access key for the application generated from Amazon.
			
		3)    ownderId is the ID of the owner of the created buckets and objects.
		
		4) 	  ownerdisplayName is the display name of the owner of the created buckets.
		
		5) 	  displayName is the short display name of the owner of the created buckets and objects.
		
		6)    bucketName_1 is the name of a bucket to be generated.
		
		7)    bucketName_2 is the name of a bucket to be generated.
		
		8)    bucketName_3 is the name of a bucket to be generated.
			  (Different buckets are used for different request methods.)
			  
		9)    bucketUrl_1 is the URL of the bucket generated with the name bucketName_1.
			  	
		10)   bucketUrl_2 is the URL of the bucket generated with the name bucketName_2.
		
		11)   bucketUrl_3 is the URL of the bucket generated with the name bucketName_1.
		
		12)   bucketUrl_4 is the URL of the bucket generated with the name bucketName_2.
		
		13)   bucketUrl_5 is the URL of the bucket generated with the name bucketName_2.
		
		14)   bucketUrl_6 is the URL of the bucket generated with the name bucketName_2.
		
		15)   bucketUrl_7 is the URL of the bucket generated with the name bucketName_3.
			  (Different bucket URLs are used for different request methods.)
		
		16)   objectName is the name of the Object to be creted by the Create Object method.
		
		17)   objectName_1 is a name of a Object being used for multiple object deletion.
		
		18)   objectName_2 is a name of a Object being used for multiple object deletion.
		
		19)   destinationObjectName is the name of the Object being used for creating a copy of an existing Object.
		
		20)   objectName_6 is the name of the Object being used for part uploads.
		
		21)   copySource is the path of the object to use as the source of copying to another location.
		
		22)   maxParts is the maximum number of parts to be returned when requesting for listing the uploaded parts.
		
		23)   partNumberMarker is the part number which specifies the part after which listing of uploaded parts should begin. Only parts with higher part numbers are listed.
		
		24)   encodingType is the encoding method to be uses in encoding the response in listing of the uploaded parts.
		
		25)   timeOut is the amount of time to wait for manipulate the responses.
        
 7. Navigate to "Integration_Test/products/esb/4.8.1/modules/integration/connectors/" and run the following command.
     $ mvn clean install
     
     