Product: Integration tests for WSO2 ESB Foursquare connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - org.wso2.esb.integration.integration-base is rquired. this test suite has been configred to download this automatically. however if its fail download following project and compile using mvn clean install command to update your local repository.
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

STEPS:

 1. Make sure the ESB 4.8.1 zip file with latest patches available at "{PATH_TO_SOURCE_BUNDLE}/Foursquare-connector/Foursquare-connector-1.0.0/repository/"

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
		


 3. Create a Foursquare account and derive the access token:
	i) 	Using the URL "https://www.foursquare.com/" create a Foursquare account.
	ii) Derive the access token by following the instructions at "https://developer.foursquare.com/overview/auth".


 4. Update the Foursquare properties file at location "{PATH_TO_SOURCE_BUNDLE}/foursquare-connector/foursquare-connector-1.0.0/src/test/resources/artifacts/ESB/connector/config" as below.
   
		i) accessToken - Use the access token you got from step 3.
		ii) apiUrl - api url of foursquare (https://api.foursquare.com).
		iii) userId - id of authenticated user.
		iv) userIdInvalid - an invalid userId to test the negative cases of user methods.
		v) userIdToUnFriend - userId of ur friend who you want to unfriend.
		vi) accessTokenInvalid - invalid accessToken to test authentication and negative cases of user methods.
		vii) limit - 
		viii) offset - 
		ix) limitInvlid - 
		x) afterTimestamp - 
		xi) beforeTimestamp - 
		xii) sort -
		fousquare/foursquare-connector/foursquare-connector-1.0.0/pom.xml
 5. Navigate to "{PATH_TO_SOURCE_BUNDLE}/fousquare/foursquare-connector/foursquare-connector-1.0.0/" and run the following command.
      $ mvn clean install


 NOTE : Following Foursquare account, can be used for run the integration tests.
    Username : @gmail.com
    Password : 
