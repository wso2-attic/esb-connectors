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

 5.	 Copy AmazonS3 connector, "amazons3.zip" to the location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/repository/"

 6.  Add following code block, just after the listeners block (Remove or comment all the other test blocks) in following file - "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/testng.xml"

        <test name="AmazonS3-Connector-Test" preserve-order="true" verbose="2">
        	<packages>
            	<package name="org.wso2.carbon.connector.integration.test.amazons3"/>
        	</packages>
    	</test>

  7. Copy the java file "AmazonS3/integration-test/src/test/java/org/wso2/carbon/connector/integration/test/amazons3/AmazonS3ConnectorIntegrationTest.java" to location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/java/org/wso2/carbon/connector/integration/test/amazons3/"

  8. Copy proxy files from location "AmazonS3/integration-test/src/test/resources/artifacts/ESB/config/proxies/amazons3/" to location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/config/proxies/amazons3/"

  9. Copy request files from location "AmazonS3/integration-test/src/test/resources/artifacts/ESB/config/restRequests/amazons3" to location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/config/restRequests/amazons3/"

  10. Copy resource files from location "AmazonS3/integration-test/src/test/resources/artifacts/ESB/config/resources/amazons3" to location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/config/resources/amazons3/"

  11. Copy Property File, "amazons3.properties" from location "AmazonS3/integration-test/src/test/resources/artifacts/ESB/connector/config" to location "Integration_Test/products/esb/4.8.1/modules/integration/connectors/src/test/resources/artifacts/ESB/connector/config/" 

  		and edit using valid and relevant data.

  12. Prerequisites for AmazonS3 Connector Integration Testing

    Follow these steps before start testing.
    a)  Create a fresh account in amazons3 and Log on to http://aws.amazon.com/s3/ with the web browser.
    b)  Save the AWSAccessKeyId and AWSSecretKey while continuing the registration process.
    o)  Following fields in the property file also should be updated appropriately.

        1)    friendId is Id of a friend who can be tagged to a photo and an invitation can be sent to.

        2)    userId is the profile ID of the user.
        
  13. Navigate to "Integration_Test/products/esb/4.8.1/modules/integration/connectors/" and run the following command.
     $ mvn clean install