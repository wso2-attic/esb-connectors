Product: Integration tests for WSO2 ESB Campaign Monitor connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform:

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

STEPS:
 
 1. Extract the certificate from browser by navigating to "https://www.campaignmonitor.com/" and place the certificate file in following locations. 

	i)  "<CAMPAIGN_MONITOR_CONNECTOR_HOME>/campaignmonitor-connector/campaignmonitor-connector-1.0.0/org wso2.carbon.connector/src/test/resources/keystores/products"

		Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "campaignmonitor"' in command line to import campaig nmonitor certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from campaign monitor with  the extension, change it accordingly. Remove the copied certificate.
	
	ii) "wso2esb-4.8.1/repository/resources/security"
	
		Navigate to the above location from command prompt and execute 'keytool -importcert -file CERT_FILE_NAME -keystore client-truststore.jks -alias "campaignmonitor"' in command line to import campaign monitor certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the file name which was extracted from campaign monitor with  the extension, change it accordingly. Remove the copied certificate.

 2. The ESB should be configured as below;
	Please make sure that the below mentioned Axis configurations are enabled (/repository/conf/axis2/axis2.xml).
   
    <messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
	
    <messageBuilder contentType="text/html"	class="org.wso2.carbon.relay.BinaryRelayBuilder"/>
	

	Note: Add the aforementioned message formatters and the message builder to the axis file, if they are not available by default.
 
 3. Make sure that the ESB 4.8.1 zip file with latest patches  and the changes in step 1 and 2, is available at "{CAMPAIGN_MONITOR_CONNECTOR_HOME}/campaignmonitor-connector/campaignmonitor-connector-1.0.0/org.wso2.carbon.connector/repository/"	
 
 4. Follow the below steps to create a campaign Monitor account.

	i) Navigate to "https://www.campaignmonitor.com/" and click on "Create a free account" button.
   ii) Enter the required details and complete the account creation. 
 
 5. Follow the steps in the below link to obtain the accesstoken.
 
	https://www.campaignmonitor.com/api/getting-started/#authenticating_with_oauth
	
 6. Required properties for Campaign Monitor Connector Integration Testing
   
	i)   apiUrl		  				- The URL of Campaign Monitor api(https://api.createsend.com).
	ii)  accessToken  				- The access token obtained in step(5) which gives access to the API
	iii) htmlUrl      				- A valid url of a html page which doesn't have any javascript code. The below mentioned url can be used for this parameter.
									  http://www.karelia.com/support/sandvox/help/z/Raw_HTML.html
	iv)  clientId     				- A Id of a specific client in your account. Follow the steps mentioned in below link to obtain the clientId from UI.
									  https://www.campaignmonitor.com/api/getting-started/#clientid
									  If the clientId is not available through the above steps, run the "listClients" method	to obtain the clientId.	
	v)   listId 	  				- A Id of a subscriber list which is assosiated with the aforementioned client Id in parameter iv. Follow the steps mentioned in below link to obtain the listId from UI.
									  https://www.campaignmonitor.com/api/getting-started/#listid
									  If the listId is not available through the above steps, run the "listSubscriberLists" method	to obtain the listId.						
	
	The following properties vi, vii , viii and ix are need to be change before run the integration testing. 
	
	vi)   subscribersMandatoryEmail - An email of the subscriber for mandatory case.
	vii)  subscribersNameMandetory	- A name of the subscriber for mandatory case.
    viii) subscribersOptionalEmail	- An email of the subscriber for optional case.
    ix)   subscribersNameOptional	- A name of the subscriber for optional case.

 7. Create a "Segment" through the subscriber list referenced by listId, mentioned in step (6) -> (v).
 
	i)   Navigate to subscriber list home page.
	ii)	 Click "Segments" link which resides in the menu panel in right hand side.
	iii) Create a new segment through "Create a new segment" link.
 
 8. Navigate to "{CAMPAIGN_MONITOR_CONNECTOR_HOME}/campaignmonitor-connector/campaignmonitor-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install
	  
 9. Account Details
	Username: wso2.connector.virtusa@hotmail.com
	Password: 1qaz2wsx@
	