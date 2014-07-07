Product: Integration tests for WSO2 ESB Foursquare connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - org.wso2.esb.integration.integration-base is required. this test suite has been configured to download this automatically. however if its fail download following project and compile using mvn clean install command to update your local repository.
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
		v) userIdToUnFriend - userId of your friend who you want to unfriend.
		vi) accessTokenInvalid - invalid accessToken to test authentication and negative cases of user methods.
		vii) limit - Number of results to return, upto 100.
		viii) offset - Used to page through results.
		ix) limitInvalid - Invalid number of results to return, upto 100.
		x) afterTimestamp - Seconds after which to look for checkins.
		xi) beforeTimestamp - Retrieve the first results prior to these seconds since epoch.
		xii) sort -How to sort the returned checkins.
		xiii) broadcast- Who to broadcast this check-in to.
		xiv) commentId - The id of the comment to remove.
		xv) ll - Latitude and longitude of the user's location.
		xvi) comment - The text of the comment, up to 200 characters.
		xvii) llAcc- Accuracy of the user's latitude and longitude, in meters.
        xviii) alt - Altitude of the user's location, in meters.
        xix) altAcc - Vertical accuracy of the user's location, in meters.
        xx) checkinId - The ID of the checkin to add a comment to.
        xxi) signature -When checkins are sent to public feeds such as Twitter, foursquare appends a signature (s=XXXXXX) allowing users to bypass the friends-only access check on checkins.
        xxii) venueId - The venue where the user is checking in.


		foursquare/foursquare-connector/foursquare-connector-1.0.0/pom.xml
 5. Navigate to "{PATH_TO_SOURCE_BUNDLE}/fousquare/foursquare-connector/foursquare-connector-1.0.0/" and run the following command.
      $ mvn clean install


 NOTE : Following Foursquare account, can be used for run the integration tests.
    Username : foursquareconnector2014@gmail.com
    Password : foursquarecon
