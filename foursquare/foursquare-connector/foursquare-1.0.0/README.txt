Product: Integration tests for WSO2 ESB Foursquare connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - org.wso2.esb.integration.integration-base is required. this test suite has been configured to download this automatically. however if its fail download following project and compile using mvn clean install command to update your local repository.
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform:

 - UBUNTU 14.04
 - WSO2 ESB 4.9.0-ALPHA

STEPS:

 1. Download ESB 4.9.0-ALPHA by following the URL: https://svn.wso2.org/repos/wso2/scratch/ESB/

 2.Follow the below mentioned steps for adding valid certificate to access Foursquare API over https. If the certificates are already available in keystores, you can skip this step.

	i) 	 Extract the certificate from browser(Mozilla Firefox) by navigating to https://developer.foursquare.com/
	ii)  Go to new ESB 4.9.0-ALPHA folder and place the downloaded certificate into "<ESB_HOME>/repository/resources/security/" and
		 "{FOURSQURE_CONNECTOR_HOME}/foursquare-connector/foursquare-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" folders.
	iii) Navigate to "<ESB_HOME>/repository/resources/security/" using command prompt and execute the following command.

				keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "CERT_NAME"

		 This command will import Foursquare certificate into keystore.
		 To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.

		 NOTE : CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Foursquare with the extension. (e.g. foursquare.crt)
			    CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. Foursquare)

	iv) Navigate to "{FOURSQURE_CONNECTOR_HOME}/foursquare-connector/foursquare-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute the following command.

				keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME"

		This command will import Foursquare certificate into keystore.
		To import the certificate give "wso2carbon" as password. Press "Y" to complete certificate import process.

		NOTE : 	CERT_FILE_NAME - Replace CERT_FILE_NAME with the file name that was extracted from Foursquare with the extension. (e.g. foursquare.crt)
			    CERT_NAME - Replace CERT_NAME with an arbitrary name for the certificate. (e.g. Foursquare)

 2. Make sure the ESB 4.9.0-ALPHA zip file with latest patches available at "{PATH_TO_SOURCE_BUNDLE}/Foursquare-connector/Foursquare-connector-1.0.0/repository/"

 3. This ESB should be configured as below;
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



 4. Create a Foursquare account and derive the access token:
	i) 	Using the URL "https://www.foursquare.com/" create a Foursquare account.
	ii) Derive the access token by following the instructions at "https://developer.foursquare.com/overview/auth".


 5. Update the Foursquare properties file at location "{PATH_TO_SOURCE_BUNDLE}/foursquare-connector/foursquare-connector-1.0.0/src/test/resources/artifacts/ESB/connector/config" as below.

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


6. Make sure that the foursquare connector is set as a module in esb-connectors parent pom.
              <module>foursquare/foursquare-connector/foursquare-connector-1.0.0/org.wso2.carbon.connector</module>

7. Navigate to "<ESB_CONNECTORS_HOME>" and run the following command.
              $ mvn clean install


 NOTE : Following Foursquare account, can be used for run the integration tests.
    Username : foursquareconnector2014@gmail.com
    Password : foursquarecon
