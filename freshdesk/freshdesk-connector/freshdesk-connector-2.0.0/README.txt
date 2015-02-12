Product: Integration tests for WSO2 ESB Fresh Desk connector

   Pre-requisites:

    - Maven 3.x
    - Java 1.6 or above
	- The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
      https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

    Tested Platforms: 

    - Microsoft WINDOWS V-7
    - Ubuntu 13.04
    - WSO2 ESB 4.8.1
 
Steps to follow in setting integration test.
 1.  Download ESB 4.8.1 from official website.
 2.  Deploy relevant patches, if applicable.

STEPS:

01) Please make sure that the below mentioned Axis configurations are enabled (/repository/conf/axis2/axis2.xml). 
						  
	<messageFormatter contentType="text/html"                             
					  class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>

	<messageBuilder contentType="text/html"                                
					  class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

02) Follow the below mentioned steps to create a new FreshDesk account:

	i) Navigate to the following url and create a fresh account in Freshdesk: http://freshdesk.com/signup
			(Provide the following: Name, Email (should be valid), Company, Domain - <preferred name>.freshdesk.com and phone no)
	ii) After a while an activation link would be sent to the email that was provided in step i). Click on the activation link to activate your freshdesk account.
			(You will be automatically taken to a link where you'll be asked to provide a password. Provide one and keep it for further use)
	iii) Login to the API. Follow: https://<domain>.freshdesk.com/support/login
	iv)	 Once you've logged in: Click on your profile picture on the top right corner of your portal -> Go to Profile settings Page -> Your API key will be available below the change password section to your right. Save it for further use.
	v)	In your account dashboard: Go to Customers -> Companies and Create a new company. Save the Company name for further use.
	vi)	In the company create a new contact by providing values for the required fields: Full Name, Email and Phone No.
	vii)  Go to Admin -> Security, and disable 'Secure Connection using SSL'.
	
03)	Follow the below mentioned steps to add valid certificate to access Freshdesk API over https.

	i) Extract the certificate from browser by navigating to 'https://<domain>.freshdesk.com' and place the certificate file in following location. Domain is the name used in Step 02)-(i)
	   "{FRESHDESK_HOME}/freshdesk-connector/freshdesk-connector-2.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/"
	  
	ii) Navigate to "{FRESHDESK_HOME}/freshdesk-connector/freshdesk-connector-2.0.0/org.wso2.carbon.connector/src/test/resources/keystores/products/" using command prompt and execute keytool -importcert -file CERT_FILE_NAME -keystore wso2carbon.jks -alias "CERT_NAME" in command line to import freshdesk certificate in to keystore. Give "wso2carbon" as password.
		NOTE : CERT_FILE_NAME is the certificate file name which was extracted from freshdesk.
			   CERT_NAME is name of the certificate. (e.g. freshdesk)
			   
04) Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "{FRESHDESK_HOME}/freshdesk-connector/freshdesk-connector-2.0.0/org.wso2.carbon.connector/repository/".
	
05)	Update the property file freshdesk.properties found in {FRESHDESK_HOME}/freshdesk-connector/freshdesk-connector-2.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config as follows:
	
	i)		apiUrl 				- 	API endpoint to which the service calls are made. e.g. https://virtusaabc.freshdesk.com
	ii)		apiKey 				- 	Use the API key obtained in Step 02)-(iv)
	iii)	email 				- 	Use any well formatted email (exampleName@exampleDomain.com) that currently doesn't exist as a customer email. 
	iv)		ticketDescription 	- 	Any valid String to be used as description when creating a ticket.
	v)		source 				- 	An integer value between 1 and 7  indicating the source through which the ticket is created. Refer http://freshdesk.com/api#ticket
	vi)		priority 			- 	An integer value between 1 and 4 indicating the priority level of the ticket. Refer http://freshdesk.com/api#ticket
	vii)	status 				- 	An integer value between 1 and 4 indicating the status of the ticket. Refer http://freshdesk.com/api#ticket
	viii)	letter 				- 	Use the name of the company created in Step 02)-(v)
	ix)		filterType 			- 	Type of the filter to be used. Use 'companyname'.
	x)		companyName 		- 	Use the name of the company created in Step 02)-(v) (filterType would be set to 'companyname')
	xi)		filterName 			- 	Use all_tickets to get the all the tickets.
	xii)	subject 			- 	Any String value that can be used to update the subject of the ticket with.
	xiii)	userEmail 			- 	Use the email that was used to register with Freshdesk Step 02)-(i)
	xiv)	state 				- 	State of the users to be listed. Use 'all' to list all the users. Refer http://freshdesk.com/api#view_all_user
	xv)	    noteBody 			- 	Any valid text to be added to the new note.
	xvi)	private 			- 	Indicate whether or not the note should be privet/public. Use false.
	xvii)   forumId 			-   An existing forum id in the current account.
	xviii)	categoryId 			-	An existing category id in the current account.
	
06) Navigate to "{FRESHDESK_HOME}/freshdesk-connector/freshdesk-connector-2.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install
	  