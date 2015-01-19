Product: Integration tests for WSO2 ESB Nexmo connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

Steps to follow in setting integration test.

 1. Download ESB 4.8.1 from official website.

 2. Deploy relevant patches, if applicable and the ESB should be configured as below.
	Please make sure that the below mentioned Axis configurations are enabled in "<ESB_HOME>/repository/conf/axis2/axis2.xml".

	<messageFormatter contentType="application/octet-stream" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

	<messageBuilder contentType="application/octet-stream" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

	<messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

	<messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

 3. Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "{Nexmo_Connector_Home}/nexmo-connector/nexmo-connector-1.0.0/org.wso2.carbon.connector/repository/".

 4. Create a Nexmo trial account and derive the API Key.
	i) 		Using the URL "https://dashboard.nexmo.com/register" create a Nexmo account and provide a valid phone number in account verification.
	ii)		Login to the created Nexmo account and go to 'Api Settings' and fetch the 'Key' and 'Secret' of the API.

 5. Update the Nexmo properties file at location "{Nexmo_Connector_Home}/nexmo-connector/nexmo-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl 					- 	Use "https://rest.nexmo.com".
	ii) 	apiKey					-   Use the Key retrieved in Step 04 - ii.
	iii)	apiSecret				- 	Use the Secret retrieved in Step 04 - ii.
	iv)		recipientPhoneNumber	- 	Use the phone number which was used in creating the account.
	v)		message					- 	Use a valid string as the body of the message to be sent.
	vi)		clientRef				-   Use a valid URL of a reference string for the reference (40 characters max).
	vii)	invalidDate			   	-   Use a valid date before the date of creating the account in the format YYYY-MM-DD.
	viii)	answerUrl			    -   Use a valid URL which contains a VoiceXML response.
	ix)	    timeOut			    	-   Use a valid integer value as the timeout between each test case execution (recommended value is 10000).
		
 6. Navigate to "{Nexmo_Connector_Home}/nexmo-connector/nexmo-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install
		