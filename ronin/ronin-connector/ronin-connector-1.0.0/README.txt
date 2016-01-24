Product: Integration tests for WSO2 ESB Ronin connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2/esb-connectors/tree/master/integration-base-1.0.1

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.9.0-BETA-SNAPSHOT
 - Java 1.7

Steps to follow in setting integration test.

 1. Download ESB WSO2 ESB 4.9.0-BETA-SNAPSHOT by navigating the following the URL: http://svn.wso2.org/repos/wso2/people/malaka/ESB/beta/.
 
 2.	Deploy relevant patches, if applicable.
 
 3. ESB should be configured as below.
	 Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml). 
 
		<messageFormatter contentType="text/html" class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
		
		<messageBuilder contentType="text/html" class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

 4. Compress modified ESB as wso2esb-4.9.0-BETA-SNAPSHOT.zip and copy that zip file in to location "{ESB_Connector_Home}/repository/".

 5. Make sure that ronin is specified as a module in ESB_Connector_Parent pom.
 	<module>ronin/ronin-connector/ronin-connector-1.0.0/org.wso2.carbon.connector</module>

 6. Create a Ronin trial account and login.
	i) 	Go to your user profile page. You can find this on the upper right corner after logging in.
	ii)	Click to "Generate API Token" to generate a unique token.
	
 7. Prerequisites for Ronin Connector Integration Testing

	i) 	Navigate to the URL "https://[subdomain].roninapp.com/estimates", create two or more estimates. If there are no any clients create them as well.
	ii) Navigate to the URL "https://[subdomain].roninapp.com/invoices", create two or more invoices. If there are no any clients create them as well.
 
 8. Update the Ronin properties file at location "<RONIN_CONNECTOR_HOME>/ronin-connector/ronin-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl 							- 	The API URL specific to the created Ronin account (e.g. https://[subdomain].roninapp.com).
	ii) 	apiToken						-   Use the API  token obtained under step 6 ii).
	iii)	cilentName						-   A String value for the client name to create a client with mandatory parameters.
	iv)		cilentNameOpt					-	A String value for the client name to create a client with optional parameters.
	v)		cilentAddress					-	A String value for the client address to create a client with optional parameters.
	vi)		cilentAddress2					-	A String value for the client address2 to create a client with optional parameters.
	vii)	cilentCity						-	A String value for the client city to create a client with optional parameters.
	viii)	cilentCountry					-	A String value for the client country to create a client with optional parameters.
	ix)		cilentState						-	A String value for the client state to create a client with optional parameters.
	x)		contactName						-   A String value for the contact name to create a contact with mandatory parameters.
	xi)		contactEmail					-   A String value for the contact email to create a contact with mandatory parameters.
	xii)	contactNameOpt					-   A String value for the contact name to create a contact with optional parameters.
	xiii)	contactEmailOpt					-   A String value for the contact email to create a contact with optional parameters.
	xiv)	contactTitleOpt					-   A String value for the contact title to create a contact with optional parameters.
	xv)		contactMobileOpt				-   A String value for the contact mobile number to create a contact with optional parameters.
	xvi)	contactPhoneOpt					-   A String value for the contact phone number to create a contact with optional parameters.
	xvii)	contactExtOpt					-   A String value for the contact extension number  to create a contact with optional parameters.
	xviii)	projectName						-   A String value for the project name to create a project with mandatory parameters.
	xix)	projectRate						-   A numeric value for the project rate to create a project with mandatory parameters.
	xx)		projectNameOpt					-   A String value for the project name to create a project with optional parameters.
	xxi)	projectRateOpt					-   A numeric value for the project rate to create a project with optional parameters.
	xxii)	projectBudgetTypeOpt			-   A numeric value(0 or 1) for the project budget type to create a project with optional parameters.
	xxiii)	projectTypeOpt					-   A numeric value(0 or 1) for the project type to create a project with optional parameters.
	xxiv)	projectDescriptionOpt			-   A String value for the project description to create a project with optional parameters.
	xxv)	projectCurrencyCodeOpt			-   A String value(e.g. USD,LKR etc. ) for the project currency type to create a project with optional parameters.
	xxvi)	projectEndDateOpt				-   A date value in YYYY-MM-DD format for the project end date to create a project with optional parameters(any future date).
	xxvii)	taskTitle						-   A String value for the task title to create a task with mandatory parameters.
	xxviii)	taskTitleOpt					-   A String value for the task title to create a task with optional parameters.
	xxix)	taskDescriptionOpt				-   A String value for the task description to create a task with optional parameters.
	xxx)	taskDueDateOpt					-   A date value in YYYY-MM-DD format for the task due date to create a task with optional parameters(any future date).
	xxxi)	taskCompletedOpt				-   A boolean value for the task completed status to create a task with optional parameters.
	xxxii)	invoiceUpdatedSince				-   A date value in YYYY-MM-DD format to list invoices with optional parameters (any past date).
	xxxiii)	amount							-   A numeric value for the payment amount to create a invoice payment with mandatory and optional parameters.
	xxxiv)	paymentNote						-   A String value for the payment note to create a invoice payment with optional parameters.
	xxxv)	paymentReceivedOn				-   A date value in YYYY-MM-DD format to create a invoice payment with optional parameters (any past date).
	xxxvi)	transactionFeeAmount			-   A numeric value for the transaction fee to create a invoice payment with optional parameters.
	

 9. Navigate to "{ESB_Connector_Home}/" and run the following command.
      $ mvn clean install

		