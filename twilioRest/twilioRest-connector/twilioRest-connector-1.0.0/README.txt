Product: Integration tests for WSO2 ESB twilioRest connector

Pre-requisites:

 - Maven 3.x
 - Java 1.7 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2/esb-connectors/tree/master/integration-base-1.0.1

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-BETA-SNAPSHOT
 - Java 1.7

Steps to follow in setting integration test.

 1. Download ESB 4.9.0-BETA-SNAPSHOT by following the URL: http://svn.wso2.org/repos/wso2/people/malaka/ESB/beta/

 2. Deploy relevant patches, if applicable. Place the patch files into location <ESB_HOME>/repository/components/patches.
 
 3. Navigate to location "<ESB_HOME>/repository/conf/axis2" and add/uncomment following lines in "axis2.xml". 
 
	<messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
	<messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
 
 4. Compress modified ESB as wso2esb-4.9.0-BETA-SNAPSHOT.zip and copy that zip file in to location "{ESB_CONNECTOR_HOME}/repository/".
 
 5. Make sure that twilioRest is specified as a module in ESB_Connector_Parent pom.
	<module>twilioRest/twilioRest-connector/twilioRest-connector-1.0.0/org.wso2.carbon.connector</module>

 6. Create a TwilioRest trial account and derive the API Token.
	i) 	Using the URL "https://www.twilio.com/try-twilio" create a twilio free trial account.
	ii)	Login to twilio account dashboard using the URL "https://www.twilio.com/login" and navigate to "Trial Account" > "Account" under the API Credentials find the AccounSID and AuthToken 
	
 7. Prerequisites for twilioRest Connector Integration Testing
	i)  Using developer tool to working with account, sms , calls and phone numbers. Go to 'DEV TOOLS' > 'API EXPLOREr'
	ii) Navigate to the URL "https://www.twilio.com/user/account/developer-tools/api-explorer/account-subaccount" to create new sub accounts
	iii) Using developer-tools for sms ,calls and phone numbers operations 
 
 8. Update the twilio properties file at location "<TWILIOREST_CONNECTOR_HOME>/twilioRest-connector/twilioRest-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.  
	
	i)	 accountSid 	     - 	Use the AccounSID obtain under step 6 ii)
	ii)  apiToken			 -  Use the API authentication token obtained under step 6 ii).
	iii) apiUrl				 -  The API URL specific to the created twilio account (e.g. https://www.twilio.com).
	iv)	 apiVersion			 -	The API version specific to the created twilio account (e.g. 2010-04-01 ).
	v)	 friendlyname        -  The human-readable account name. Only accounts whose name exactly match this string are returned.
	vi)	 status              -  The new status of the account: active, suspended, or closed.
	vii) from                -  twilio verified  phone number.
	viii)to                   -  twilio verified  phone number.
	ix)	 messageBody          -  The text of the message you want to send, limited to 160 characters.
	x)	 statusCallbackUrl    -  The URL that Twilio will request when the call ends to notify your application.
	xi)  applicationSid       -  The ID of the application
	xii) callSid              -  The identifier of the call.
	xiii)recordingSid         -  The identifier of the recording.
	xiv) transcriptionSid     -  The identifier of the transcription.
	xv)  phoneNumber          -  Twilio phone number
	xvi) incomingPhoneNumber  -  phone number which is receive the call
	xvii)outgoingPhoneNumber  -  phone number which is make the call
	xviii)incomingCallerId     -  caller id of the receiving call
	ix)  outgoingCallerId     -  caller id of the outgoing call
	X)   country              -  twilio Availabe country (e.g : US)
	xi)  areaCode             -  Available country area code numbers (e.g :510)

9. Navigate to "{ESB_Connector_Home}/" and run the following command.
          $ mvn clean install