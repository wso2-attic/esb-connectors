Product: Integration tests for WSO2 ESB AmazonS3 connector
    Pre-requisites:

    - Maven 3.x
    - Java 1.6 or above
    - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

    Tested Platforms: 

    - Microsoft WINDOWS V-7
    - Ubuntu 13.04
    - WSO2 ESB 4.8.1

Note:
	This test suite can execute based on two scenarios.
		1. Use the given test account and parameters. - in this scenario you only need to replace accessKeyId , secretAccessKey in property file
		2. Setup new amazonS3 account and follow all the instruction given below
	
Steps to follow in setting integration test.

 1. Make sure the ESB 4.8.1 zip file with latest patches available at "{AmazonS3_Connector_Home}/amazons3-connector/amazons3-connector-1.0.0/org.wso2.carbon.connector/repository/"

 2. This ESB should be configured as below;
	Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml).
    
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


 6. Prerequisites for AmazonS3 Connector Integration Testing

    Follow these steps before start testing.
    a)  Create a fresh account in amazons3 and Log on to http://aws.amazon.com/s3/ with the web browser.
    b)  Save the AWSAccessKeyId and AWSSecretKey while continuing the registration process.
    c)  Update the AmazonS3 properties file at location "{AmazonS3_Connector_Home}/amazons3-connector/amazons3-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.

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
        
 7. Navigate to "{AmazonS3_Connector_Home}/amazons3-connector/amazons3-connector-1.0.0/org.wso2.carbon.connector/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install
     
     