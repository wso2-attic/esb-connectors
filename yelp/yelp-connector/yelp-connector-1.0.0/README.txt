Product: Integration tests for WSO2 ESB DropBox connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - org.wso2.esb.integration.integration-base is required. this test suite has been configured to download this automatically. however if its fail download following project and compile using mvn clean install command to update your local repository.
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

STEPS:

1. Make sure the ESB 4.8.1 zip file with latest patches available at "{PATH_TO_SOURCE_BUNDLE}/yelp-connector/yelp-connector-1.0.0/repository/"

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


3. Make sure "integration-base" project is placed at "{PATH_TO_SOURCE_BUNDLE}/../"


3. Create a Yelp account and get the api access keys:
	i) 	Using the URL "http://www.yelp.com/SignUp" create a yelp account.
	ii) Get the api access keys from this url "http://www.yelp.com/developers/manage_api_keys".


4. Update the Yelp properties file at location "{PATH_TO_SOURCE_BUNDLE}/yelp-connector/yelp-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
   
		i)   consumerKey - Use the consumerKey you got from step 8.
        ii)  consumerKeySecret - Use the consumerKeySecret you got from step 8.
        iii) accessToken - Use the accessToken you got from step 8.
        iv)  accessTokenSecret - Use the accessTokenSecret you got from step 8.

		
5. Navigate to "{PATH_TO_SOURCE_BUNDLE}/yelp-connector/yelp-connector-1.0.0/" and run the following command.
      $ mvn clean install


NOTE : Following Yelp account, can be used for run the integration tests.
    Username : xxxxxxxxxx@gmail.com
    Password : xxxxxxxx
